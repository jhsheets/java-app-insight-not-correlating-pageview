package com.example.metrics;

import com.microsoft.applicationinsights.extensibility.TelemetryProcessor;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Filters which requests have their telemetry sent to Azure Application Insights based on the requested path
 * <br/><br/>
 * Partially based on Microsoft examples from:
 * https://docs.microsoft.com/en-us/azure/application-insights/app-insights-java-filter-telemetry
 */
public class MyTelemetryProcessor
implements TelemetryProcessor
{
    /** acceptable paths for tracking without the context path */
    private final List<String> acceptable = Arrays.asList("/api", "/html");

    /** acceptable paths prepended with the context path */
    private final List<String> ACCEPTABLE_PATHS;


    public MyTelemetryProcessor(final String contextPath)
    {
        final String contextPath2 = StringUtils.defaultString(contextPath, "");
        // create a new list with the context path prepended to it
        this.ACCEPTABLE_PATHS = acceptable.stream().map( a -> contextPath2 + a).collect(Collectors.toList());
    }

    @Override
    public boolean process(final Telemetry telemetry)
    {
        // There are other types of telemetry.. we only want to (potentially) ignore RequestTelemetry's
        // TraceTelemetry, PageViewTelemetry, MetricTelemetry, etc
        if (telemetry instanceof RequestTelemetry)
        {
            final RequestTelemetry rt = (RequestTelemetry)telemetry;

            try
            {
                // Get current path (without the protocol and host) which is /contextPath/other/paths
                final String path = rt.getUrl() == null ? null : rt.getUrl().getPath();
                if (path != null)
                {
                    // If path starts with any of the paths in the acceptable list
                    return ACCEPTABLE_PATHS.stream().anyMatch(path::startsWith);
                }
            }
            catch (Exception e)
            {
                // Not writing to logger; don't want errors sending telemetry being sent to app insights
                e.printStackTrace();
            }
        }

        // we should process other telemetry types
        return true;
    }
}
