package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;


/**
 * This is the main entry point for our application.
 * <br/><br/>
 * When using spring boot's embedded server, the main method is our entry point, like any normal Java app.
 * <br/><br/>
 * NOTE: In applications.properties I have error.whitelabel.enabled=false, so we don't see that error screen.
 * Because of this, Spring throws a weird circular error screen if you enter a URL that doesn't exist.
 * I'm excluding ErrorMvcAutoConfiguration.class to prevent this from happening, which should show a
 * simple 404 error when deployed to tomcat.
 */
@SpringBootApplication(exclude=ErrorMvcAutoConfiguration.class)
public class Application
extends SpringBootServletInitializer
{

    public static void main(final String[] args) {
        final ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        // Always use cookie-based session tracking so it's consistent for whatever container we run in
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    }
}