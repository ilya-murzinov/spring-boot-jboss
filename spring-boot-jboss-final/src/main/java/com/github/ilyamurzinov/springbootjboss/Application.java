package com.github.ilyamurzinov.springbootjboss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@SpringBootApplication
@ImportResource("classpath:context.xml")
public class Application extends SpringBootServletInitializer {

    private volatile static WebApplicationContext webApplicationContext;

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

    public static class HelloFilterProxy extends DelegatingFilterProxy {
        @Override
        protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
            return Application.webApplicationContext.getBean(HelloFilter.class);
        }
    }

    public static class DispatcherServletProxy implements Servlet {

        private Servlet delegate;

        @Override
        public void init(ServletConfig config) throws ServletException {
            delegate = Application.webApplicationContext.getBean(DispatcherServlet.class);
            delegate.init(config);
        }

        @Override
        public ServletConfig getServletConfig() {
            return delegate.getServletConfig();
        }

        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            delegate.service(req, res);
        }

        @Override
        public String getServletInfo() {
            return delegate.getServletInfo();
        }

        @Override
        public void destroy() {
            delegate.destroy();
        }
    }
}
