package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class FirewallPlugin implements Plugin {
    @Override
    public List<RuleBuilder<?, ?, ?>> getSupportedRules() {
        return Collections.singletonList(
                new FirewallPublicServiceProhibitedRuleBuilder()
        );
    }
}
