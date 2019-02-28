package com.example.metrics;

import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.applicationinsights.extensibility.TelemetryProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;


/**
 * If we've set the aiInstrumentationKey variable, update the {@link TelemetryConfiguration} with this instrumentation key.
 * Once set, instrumentation should get properly uploaded to Azure ApplicationInsights
 */
@Configuration
//@DependsOn(value = {"azurePropertySource"}) // make sure AzurePropertySourceConfig.java's azurePropertySource @Bean loads first. Disabling because it breaks @WebMvcTest!
public class AzureAppInsightsContextListener
//extends WebMvcConfigurerAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(AzureAppInsightsContextListener.class);

    @Value("${azure.application-insights.instrumentation-key}")
    private String aiInstrumentationKey;

    @Autowired
    private ServletContext servletContext;

    @Bean
    public TelemetryProcessor myTelemetryProcessor()
    {
        return new MyTelemetryProcessor(servletContext.getContextPath());
    }



//    @Bean
//    public String telemetryConfig()
//    {
//        if (StringUtils.isNotBlank(aiInstrumentationKey))
//        {
//            logger.debug("Setting Azure ApplicationInsights instrumentation key");
//            TelemetryConfiguration.getActive().setInstrumentationKey(aiInstrumentationKey);
//        }
//        return aiInstrumentationKey;
//    }
//
//    /**
//     * Programmatically registers a FilterRegistrationBean to register WebRequestTrackingFilter
//     * @param webRequestTrackingFilter
//     * @return Bean of type {@link FilterRegistrationBean}
//     */
//    @Bean
//    public FilterRegistrationBean webRequestTrackingFilterRegistrationBean(WebRequestTrackingFilter webRequestTrackingFilter)
//    {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(webRequestTrackingFilter);
//        // Track our REST and MVC endpoints
//        // Don't track everything, or we'll log requests to webjars and static html/js/css files
//        // See BrowserSecurityConfig.java and RestSecurityConfig.java
//        registration.addUrlPatterns("/v1/*", "/v2/*", "/v3/*", "/v4/*", "/v5/*", "/html/*", "/login");
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
//        return registration;
//    }
//
//
//    /**
//     * Creates bean of type WebRequestTrackingFilter for request tracking
//     * @param applicationName Name of the application to bind filter to
//     * @return {@link Bean} of type {@link WebRequestTrackingFilter}
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public WebRequestTrackingFilter webRequestTrackingFilter(@Value("${spring.application.name:application}") String applicationName) {
//        return new WebRequestTrackingFilter(applicationName);
//    }



//    @Override
//    public void addInterceptors(final InterceptorRegistry registry)
//    {
//        if (StringUtils.isNotBlank(aiInstrumentationKey))
//        {
//            logger.debug("Setting Azure ApplicationInsights instrumentation key");
//            TelemetryConfiguration.getActive().setInstrumentationKey(aiInstrumentationKey);
//            registry.addInterceptor(getRequestNameHandlerInterceptor()).addPathPatterns("/**");
//        }
//    }
//
//    @Bean
//    public RequestNameHandlerInterceptorAdapter getRequestNameHandlerInterceptor()
//    {
//        return new RequestNameHandlerInterceptorAdapter();
//    }
//
//    @Bean
//    public FilterRegistrationBean someFilterRegistration(WebRequestTrackingFilter webRequestTrackingFilter)
//    {
//        final FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(webRequestTrackingFilter);
//        // Track our REST and MVC endpoints
//        // Don't track everything, or we'll log requests to webjars and static html/js/css files
//        // See BrowserSecurityConfig.java and RestSecurityConfig.java
//        registration.addUrlPatterns("/v1/*", "/v2/*", "/v3/*", "/v4/*", "/v5/*", "/html/*");
//        registration.setOrder(1);
//        return registration;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public WebRequestTrackingFilter webRequestTrackingFilter(@Value("${spring.application.name:application}") String applicationName) {
//        return new WebRequestTrackingFilter(applicationName);
//    }
}
