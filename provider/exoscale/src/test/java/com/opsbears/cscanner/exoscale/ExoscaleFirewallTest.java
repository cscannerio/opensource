package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.*;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.firewall.FirewallPublicServiceProhibitedRule;
import com.opsbears.cscanner.firewall.FirewallRule;
import com.opsbears.cscanner.s3.S3Plugin;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * This test suite tests the Exoscale firewall behavior. It requires a live Exoscale connection.
 */
@ParametersAreNonnullByDefault
public class ExoscaleFirewallTest {
    @Nullable
    private static String apiKey;
    @Nullable
    private static String apiSecret;
    private static ExoscaleTestClient testClient;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
        if (apiKey == null || apiSecret == null) {
            testClient = null;
        } else {
            testClient = new ExoscaleTestClient(apiKey, apiSecret);
        }
    }

    private ScannerCore createScannerCore(List<RuleConfiguration> rules) {
        return ScannerCoreFactory.create(apiKey, apiSecret, rules, Arrays.asList(
            new FirewallPlugin()
        ));
    }

    @Before
    public void beforeMethod() {
        org.junit.Assume.assumeTrue(testClient != null);
    }

    @Test
    public void testCompliantSecurityGroup() {
        //Setup
        testClient.ensureSecurityGroupExists("compliant");
        testClient.ensureRuleExists("compliant", "icmpv6", Arrays.asList("::/0"), null, null, 128, 0);
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
            new ArrayList<>(),
            options
        ));
        ScannerCore scannerCore = createScannerCore(
            rules
        );

        //Execute
        List<RuleResult> results = scannerCore.scan();

        //Assert
        List<RuleResult> filteredResults = results
            .stream()
            .filter(result -> result.connectionName.equals("exo"))
            .filter(result -> result.resourceName.equalsIgnoreCase("compliant"))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        assertEquals(1, filteredResults.size());
        assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);
    }

    @Test
    public void testNonCompliantSecurityGroup() {
        //Setup
        testClient.ensureSecurityGroupExists("noncompliant");
        testClient.ensureRuleExists("noncompliant", "tcp", Arrays.asList("0.0.0.0/0"), 22, 22, null, null);
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
            new ArrayList<>(),
            options
        ));
        ScannerCore scannerCore = createScannerCore(
            rules
        );

        //Execute
        List<RuleResult> results = scannerCore.scan();

        //Assert
        List<RuleResult> filteredResults = results
            .stream()
            .filter(result -> result.connectionName.equals("exo"))
            .filter(result -> result.resourceName.equalsIgnoreCase("noncompliant"))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        assertEquals(1, filteredResults.size());
        assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
    }

    @Test
    public void testProtocolAll() {
        //Setup
        testClient.ensureSecurityGroupExists("protocol-all");
        testClient.ensureRuleExists("protocol-all", "all", Arrays.asList("0.0.0.0/0"), null, null, null, null);
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
            new ArrayList<>(),
            options
        ));
        ScannerCore scannerCore = createScannerCore(
            rules
        );

        //Execute
        List<RuleResult> results = scannerCore.scan();

        //Assert
        List<RuleResult> filteredResults = results
            .stream()
            .filter(result -> result.connectionName.equals("exo"))
            .filter(result -> result.resourceName.equalsIgnoreCase("protocol-all"))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        assertEquals(1, filteredResults.size());
        assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
    }
}
