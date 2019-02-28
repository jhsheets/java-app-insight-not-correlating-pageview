package com.example.endpoints;

import com.microsoft.azure.storage.core.Base64;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.StringTokenizer;


@Configuration
public class LoggingRequestInterceptor
extends WebMvcConfigurerAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public void addInterceptors(final InterceptorRegistry registry)
    {
        registry.addInterceptor(new HandlerInterceptorAdapter()
        {
            @Override
            public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
                    throws Exception
            {
                final String uri = request.getRequestURI();
                // Don't log requests to the error display
                if (!uri.equals("/error") && logger.isDebugEnabled())
                {
                    final StringBuilder msg = new StringBuilder();
                    msg.append("request = ").append(request.getMethod()).append(" ").append(uri).append("\n");
                    msg.append("ipAddress = ").append(request.getRemoteAddr()).append("\n");
                    msg.append("headers = [").append("\n");

                    final Enumeration<String> headers = request.getHeaderNames();
                    while (headers.hasMoreElements())
                    {
                        final String headerKey = headers.nextElement();
                        final String headerVal = request.getHeader(headerKey);

                        if (headerKey.equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                        {
                            // We don't want to print the entire authorization header, since it includes the users password
                            // Instead print just the username
                            final String authType = getAuthType(headerVal);
                            final String encodedUserPassword = headerVal.replaceFirst(authType, "");
                            final Pair<String,String> usernameAndPass = getUsernameAndPass(encodedUserPassword);
                            final String username = usernameAndPass.getKey();
                            msg.append("  ").append(headerKey).append(": ").append(authType).append(" ").append(username).append("\n");
                        }
                        else
                        {
                            msg.append("  ").append(headerKey).append(": ").append(headerVal).append("\n");
                        }
                    }
                    msg.append("]");

                    logger.debug(msg.toString());
                }
                return true;
            }
        });
    }

    public String getAuthType(final String authHeader)
    {
        // Should be a space between the auth type and the base64 encoded usernamePassword
        final StringTokenizer tokenizer = new StringTokenizer(authHeader, " ");
        if (tokenizer.countTokens() > 0)
        {
            return tokenizer.nextToken();
        }

        return "<UNKNOWN AUTH TYPE>";
    }

    public Pair<String,String> getUsernameAndPass(String encodedUserPassword)
    {
        String username = "";
        String password = "";

        // username:password string should always be base64 encoded
        final String usernameAndPassword = new String(Base64.decode(encodedUserPassword));

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        if (tokenizer.countTokens() == 2)
        {
            // First token is username
            username = tokenizer.nextToken();
            password = tokenizer.nextToken();
        }

        return new ImmutablePair<>(username, password);
    }
}
