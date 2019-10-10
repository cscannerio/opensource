package io.cscanner.core.provider.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplets;
import com.myjeeva.digitalocean.pojo.FloatingIPs;
import com.myjeeva.digitalocean.pojo.Pages;
import com.opsbears.webcomponents.net.IPAddress;
import io.cscanner.core.test.engine.HostDiscoveryClient;
import io.cscanner.core.test.engine.HostDiscoveryRecord;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class DigitalOceanHostDiscoveryClient implements HostDiscoveryClient {
    private final String connectionName;
    private final DigitalOcean apiClient;

    public DigitalOceanHostDiscoveryClient(String connectionName, DigitalOceanConfiguration configuration) {
        this.connectionName = connectionName;
        apiClient = new DigitalOceanClient(configuration.apiToken);
    }

    private List<HostDiscoveryRecord> getFloatingIps() {
        List<HostDiscoveryRecord> ipAddresses = new ArrayList<>();

        Pages pages;
        int page = 0;
        int perPage = 100;
        do {
            try {
                FloatingIPs floatingIps = apiClient.getAvailableFloatingIPs(
                    page,
                    perPage
                );
                ipAddresses.addAll(floatingIps
                    .getFloatingIPs()
                    .stream()
                    .map(ip -> new HostDiscoveryRecord(
                        connectionName,
                        ip.getRegion().getName(),
                        IPAddress.getFromString(ip.getIp()),
                        Collections.singletonList(ip.getDroplet().getId().toString())
                    ))
                    .collect(Collectors.toList()));
                if (floatingIps.getLinks() != null && floatingIps.getLinks().getPages() != null && floatingIps.getLinks().getPages().getNext() != null) {
                    pages = floatingIps.getLinks().getPages();
                    page++;
                } else {
                    pages = null;
                }
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (pages != null);

        return ipAddresses;
    }

    private List<HostDiscoveryRecord> getDropletIps() {
        List<HostDiscoveryRecord> ipAddresses = new ArrayList<>();

        Pages pages;
        int page = 0;
        int perPage = 100;
        do {
            try {
                Droplets droplets = apiClient.getAvailableDroplets(
                    page,
                    perPage
                );
                droplets
                    .getDroplets()
                    .forEach(droplet -> {
                        ipAddresses.addAll(
                            droplet
                                .getNetworks()
                                .getVersion4Networks()
                                .stream()
                                .map(ip -> new HostDiscoveryRecord(
                                    connectionName,
                                    droplet.getRegion().getName(),
                                    IPAddress.getFromString(ip.getIpAddress()),
                                    Collections.singletonList(droplet.getId().toString())
                                ))
                                .collect(Collectors.toList()));
                        ipAddresses.addAll(
                            droplet
                                .getNetworks()
                                .getVersion6Networks()
                                .stream()
                                .map(ip -> new HostDiscoveryRecord(
                                    connectionName,
                                    droplet.getRegion().getName(),
                                    IPAddress.getFromString(ip.getIpAddress()),
                                    Collections.singletonList(droplet.getId().toString())
                                ))
                                .collect(Collectors.toList()));
                    });
                if (droplets.getLinks() != null && droplets.getLinks().getPages() != null && droplets.getLinks().getPages().getNext() != null) {
                    pages = droplets.getLinks().getPages();
                    page++;
                } else {
                    pages = null;
                }
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (pages != null);

        return ipAddresses;

    }

    @Override
    public Stream<HostDiscoveryRecord> listIpAddresses() {
        List<HostDiscoveryRecord> ipAddresses = new ArrayList<>();

        ipAddresses.addAll(getDropletIps());
        ipAddresses.addAll(getFloatingIps());

        return ipAddresses.stream();
    }
}
