package com.github.ilyamurzinov.springbootjboss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@SpringBootApplication
@ImportResource("classpath:context.xml")
public class Application extends SpringBootServletInitializer {

    public volatile static WebApplicationContext webApplicationContext;

    @Override
    protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
        webApplicationContext = super.createRootApplicationContext(servletContext);
        return webApplicationContext;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class, JBossProxyInitializer.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
