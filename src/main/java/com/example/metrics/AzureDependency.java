package com.example.metrics;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.springframework.util.StopWatch;


/**
 * Track dependencies in azure Application Insights
 */
public class AzureDependency
{
    TelemetryClient telemetryClient;
    String dependency;
    String action;
    StopWatch stopWatch;

    public AzureDependency(final TelemetryClient telemetryClient, final String dependency, final String action)
    {
        this.telemetryClient = telemetryClient;
        this.dependency = dependency;
        this.action = action;
        this.stopWatch = new StopWatch();
        stopWatch.start();
    }

    public AzureDependency track(boolean success)
    {
        stopWatch.stop();
        final long time = stopWatch.getLastTaskTimeMillis();
        if (telemetryClient != null)
            telemetryClient.trackDependency(dependency, action, new Duration(time), success);
        return this;
    }

    public AzureDependency trackSuccess()
    {
        return track(true);
    }

    public AzureDependency trackFailure()
    {
        return track(false);
    }
}