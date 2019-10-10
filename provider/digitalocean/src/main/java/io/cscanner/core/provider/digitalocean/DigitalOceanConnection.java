package io.cscanner.core.provider.digitalocean;

import io.cscanner.core.test.engine.CloudProviderConnection;
import io.cscanner.core.test.engine.HostDiscoveryClient;
import io.cscanner.core.test.engine.HostDiscoveryCloudProviderConnection;
import io.cscanner.core.rule.firewall.FirewallClient;
import io.cscanner.core.rule.firewall.FirewallConnection;
import io.cscanner.core.rule.objectstorage.ObjectStorageClient;
import io.cscanner.core.rule.objectstorage.ObjectStorageConnection;
import io.cscanner.core.rule.objectstorage.S3ObjectStorageClient;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanConnection implements CloudProviderConnection, HostDiscoveryCloudProviderConnection, ObjectStorageConnection, FirewallConnection {
    private final String connectionName;
    private final DigitalOceanConfiguration configuration;

    public DigitalOceanConnection(String connectionName, DigitalOceanConfiguration configuration) {
        this.connectionName = connectionName;
        this.configuration = configuration;
    }

    @Override
    public ObjectStorageClient getObjectStorageClient() {
        return new S3ObjectStorageClient(new DigitalOceanS3ClientSupplier(configuration));
    }

    @Override
    public String getConnectionName() {
        return "digitalocean";
    }

    @Override
    public FirewallClient getFirewallClient() {
        return new DigitalOceanFirewallClient(
            configuration
        );
    }

    @Override
    public HostDiscoveryClient getHostDiscoveryClient() {
        return new DigitalOceanHostDiscoveryClient(
            connectionName,
            configuration
        );
    }
}
