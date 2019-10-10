package io.cscanner.core.cli;

import io.cscanner.core.test.engine.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class TextOutputFormatter implements OutputFormatter {
    @Override
    public String getType() {
        return "text";
    }

    @Override
    public String format(List<RuleResult> results) {
        StringBuilder output = new StringBuilder();
        for (RuleResult result : results) {
            output
                .append(result.connectionName)
                .append("\t")
                .append(result.resourceType)
                .append("\t")
                .append(result.resourceRegion == null ? "global" : result.resourceRegion)
                .append("\t")
                .append(result.resourceName)
                .append("\t")
                .append(result.compliancy);

            if (result.compliancy == RuleResult.Compliancy.NONCOMPLIANT) {
                output
                    .append("\t[")
                    .append(result.violations.stream().map(violation -> {
                        String text = "";
                        if (violation.subresource != null) {
                            text += violation.subresource + ": ";
                        }
                        text += violation.description;
                        return text;
                    }).collect(Collectors.joining("|"))).append("]");
            }
            output.append("\n");
        }
        return output.toString();
    }
}
