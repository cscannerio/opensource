package io.cscanner.core.provider.digitalocean;

import io.cscanner.core.test.engine.CloudProvider;
import io.cscanner.core.test.engine.HostDiscoveryCloudProvider;
import io.cscanner.core.rule.firewall.FirewallCloudProvider;
import io.cscanner.core.rule.objectstorage.ObjectStorageCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanCloudProvider implements
    CloudProvider<DigitalOceanConfiguration, DigitalOceanConnection>,
    HostDiscoveryCloudProvider<DigitalOceanConfiguration, DigitalOceanConnection>,
    ObjectStorageCloudProvider<DigitalOceanConfiguration, DigitalOceanConnection>,
    FirewallCloudProvider<DigitalOceanConfiguration, DigitalOceanConnection> {
    @Override
    public String getName() {
        return "digitalocean";
    }

    @Override
    public Class<DigitalOceanConfiguration> getConfigurationType() {
        return DigitalOceanConfiguration.class;
    }

    @Override
    public Class<DigitalOceanConnection> getConnectionType() {
        return DigitalOceanConnection.class;
    }

    @Override
    public DigitalOceanConnection getConnection(
        String name,
        DigitalOceanConfiguration configuration
    ) {
        return new DigitalOceanConnection(name, configuration);
    }
}
