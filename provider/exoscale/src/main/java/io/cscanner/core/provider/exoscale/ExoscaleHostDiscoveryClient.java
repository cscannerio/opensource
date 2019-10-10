package io.cscanner.core.provider.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opsbears.webcomponents.net.IPAddress;
import io.cscanner.core.test.engine.HostDiscoveryClient;
import io.cscanner.core.test.engine.HostDiscoveryRecord;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class ExoscaleHostDiscoveryClient implements HostDiscoveryClient {
    private final String connectionName;
    private final String apiKey;
    private final String apiSecret;

    public ExoscaleHostDiscoveryClient(
        String connectionName,
        ExoscaleConfiguration configuration
    ) {
        this.connectionName = connectionName;
        this.apiKey = configuration.key;
        this.apiSecret = configuration.secret;
    }

    @Override
    public Stream<HostDiscoveryRecord> listIpAddresses() {
        ApacheCloudStackUser apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        ApacheCloudStackClient apacheCloudStackClient = new ApacheCloudStackClient(
            "https://api.exoscale.ch/compute",
            apacheCloudStackUser
        );

        ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("listVirtualMachines");
        String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        JsonObject responseObject = new Gson().fromJson(response, JsonObject.class);
        JsonObject vmResponse = responseObject
            .get("listvirtualmachinesresponse")
            .getAsJsonObject();
        if (!vmResponse.has("virtualmachine")) {
            return Stream.empty();
        }
        JsonArray vmList = vmResponse
            .get("virtualmachine")
            .getAsJsonArray();

        Map<String, List<String>> vmMaps = new HashMap<>();
        Map<String, String> regionMap = new HashMap<>();

        for (int vmNo = 0; vmNo < vmList.size(); vmNo++) {
            JsonObject vm = vmList.get(vmNo).getAsJsonObject();

            JsonArray nics = vm.get("nic").getAsJsonArray();
            List<String> hostIps = new ArrayList<>();
            for (int nicNo = 0; nicNo < nics.size(); nicNo++) {
                JsonObject nic = nics.get(nicNo).getAsJsonObject();
                if (nic.has("ipaddress")) {
                    String ipAddress = nic.get("ipaddress").getAsString();
                    hostIps.add(ipAddress);
                }
                if (nic.has("ip6address")) {
                    String ipAddress = nic.get("ip6address").getAsString();
                    hostIps.add(ipAddress);
                }
                if (nic.has("secondaryip")) {
                    JsonArray secondaryIps = nic.get("secondaryip").getAsJsonArray();
                    for (int secondaryNo = 0; secondaryNo < secondaryIps.size(); secondaryNo++) {
                        JsonObject secondaryIp = secondaryIps.get(secondaryNo).getAsJsonObject();
                        if (secondaryIp.has("ipaddress")) {
                            hostIps.add(secondaryIp.get("ipaddress").getAsString());
                        }
                        if (secondaryIp.has("ip6address")) {
                            hostIps.add(secondaryIp.get("ip6address").getAsString());
                        }
                    }
                }
            }
            for (String hostIp : hostIps) {
                if (!vmMaps.containsKey(hostIp)) {
                    vmMaps.put(hostIp, new ArrayList<>());
                }
                regionMap.put(hostIp, vm.get("zonename").getAsString());
                vmMaps.get(hostIp).add(vm.get("id").getAsString());
            }
        }

        Stream.Builder<HostDiscoveryRecord> streamBuilder = Stream.builder();
        for (String ip : vmMaps.keySet()) {
            streamBuilder.add(new HostDiscoveryRecord(
                connectionName,
                regionMap.get(ip),
                IPAddress.getFromString(ip),
                vmMaps.get(ip)
            ));
        }

        return streamBuilder.build();
    }
}
