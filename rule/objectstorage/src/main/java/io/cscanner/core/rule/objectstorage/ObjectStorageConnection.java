package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.test.engine.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageConnection extends CloudProviderConnection {
    ObjectStorageClient getObjectStorageClient();
}
