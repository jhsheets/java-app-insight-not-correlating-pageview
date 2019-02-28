package com.example;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ThymeleafConfig {

//    /**
//     * Enable the {@link SpringSecurityDialect} for use in thymeleaf templates
//     *
//     * http://www.thymeleaf.org/doc/articles/springsecurity.html
//     */
//    @Bean
//    public SpringSecurityDialect conditionalCommentDialect() {
//        return new SpringSecurityDialect();
//    }

    /**
     * Enable layout templates
     *
     * https://ultraq.github.io/thymeleaf-layout-dialect/Installation.html
     */
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
