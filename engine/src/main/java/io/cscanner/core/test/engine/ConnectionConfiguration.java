package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ConnectionConfiguration {
    public final String type;
    public final Map<String, Object> options;

    public ConnectionConfiguration(
        String type,
        Map<String, Object> options
    ) {
        this.type = type;
        this.options = options;
    }
}
