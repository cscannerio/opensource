package io.cscanner.core.provider.digitalocean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.cscanner.core.rule.objectstorage.S3Factory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanS3ClientSupplier implements S3Factory {
    private final DigitalOceanConfiguration configuration;

    public DigitalOceanS3ClientSupplier(DigitalOceanConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        if (configuration.spacesKey != null && configuration.spacesSecret != null) {
            builder.withCredentials(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new AWSCredentials() {
                        @Override
                        public String getAWSAccessKeyId() {
                            return configuration.spacesKey;
                        }

                        @Override
                        public String getAWSSecretKey() {
                            return configuration.spacesSecret;
                        }
                    };
                }

                @Override
                public void refresh() {}
            });
        }

        if (region != null) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                "https://" + region + ".digitaloceanspaces.com",
                region
            ));
        } else {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                "https://ams3.digitaloceanspaces.com",
                "ams3"
            ));
        }

        return builder.build();
    }
}
