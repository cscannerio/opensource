package io.cscanner.core.provider.exoscale;

import io.cscanner.core.test.engine.CloudProvider;
import io.cscanner.core.test.engine.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscalePlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Collections.singletonList(
                new ExoscaleCloudProvider()
        );
    }
}
