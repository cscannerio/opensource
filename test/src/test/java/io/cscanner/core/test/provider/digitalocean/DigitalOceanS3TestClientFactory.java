package io.cscanner.core.test.provider.digitalocean;

import com.amazonaws.services.s3.AmazonS3;
import io.cscanner.core.provider.digitalocean.DigitalOceanConfiguration;
import io.cscanner.core.provider.digitalocean.DigitalOceanPlugin;
import io.cscanner.core.provider.digitalocean.DigitalOceanS3ClientSupplier;
import io.cscanner.core.test.engine.ConnectionConfiguration;
import io.cscanner.core.test.engine.Plugin;
import io.cscanner.core.test.engine.ScannerCore;
import io.cscanner.core.test.engine.ScannerCoreFactory;
import io.cscanner.core.rule.objectstorage.ObjectStoragePlugin;
import io.cscanner.core.test.rule.objectstorage.ObjectStorageTestClientSupplier;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class DigitalOceanS3TestClientFactory implements ObjectStorageTestClientSupplier {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    private static final DigitalOceanConfiguration digitaloceanConfiguration;

    static {
        apiKey = System.getenv("DIGITALOCEAN_SPACES_KEY");
        apiSecret = System.getenv("DIGITALOCEAN_SPACES_SECRET");
        if (apiKey != null && apiSecret != null) {
            digitaloceanConfiguration =
                new DigitalOceanConfiguration(
                    null,
                    apiKey,
                    apiSecret
            );
        } else {
            digitaloceanConfiguration = null;
        }
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public String getDefaultZone() {
        return "ams3";
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        return new DigitalOceanS3ClientSupplier(digitaloceanConfiguration).get(region);
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("spacesKey", apiKey);
            options.put("spacesSecret", apiSecret);
            connections.put("do", new ConnectionConfiguration(
                "digitalocean",
                options
            ));

            List<Plugin> plugins = new ArrayList<>(Arrays.asList(
                new TestPlugin(
                    Arrays.asList(
                        new TestConfigurationLoader(
                            connections,
                            rules
                        )
                    )
                ),
                new DigitalOceanPlugin()
            ));
            plugins.add(new ObjectStoragePlugin());
            return new ScannerCore(plugins);
        };
    }
}
