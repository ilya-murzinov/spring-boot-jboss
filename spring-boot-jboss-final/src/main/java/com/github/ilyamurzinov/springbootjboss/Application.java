package com.github.ilyamurzinov.springbootjboss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.ServletContextApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@SpringBootApplication
@ImportResource("classpath:context.xml")
public class Application implements WebApplicationInitializer {

    public volatile static WebApplicationContext webApplicationContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
        webApplicationContext = rootAppContext;

        if (rootAppContext != null) {
            servletContext.addListener(new ContextLoaderListener(rootAppContext) {
                @Override
                public void contextInitialized(ServletContextEvent event) {
                    // no-op because the application context is already initialized
                }
            });

        }
    }

    protected WebApplicationContext createRootApplicationContext(
            ServletContext servletContext) {
        ApplicationContext parent = null;
        Object object = servletContext
                .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (object instanceof ApplicationContext) {
            parent = (ApplicationContext) object;
            servletContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
        }
        SpringApplicationBuilder application = new SpringApplicationBuilder();
        if (parent != null) {
            application.initializers(new ParentContextApplicationContextInitializer(
                    parent));
        }
        application.initializers(new ServletContextApplicationContextInitializer(
                servletContext));
        application.contextClass(AnnotationConfigEmbeddedWebApplicationContext.class);
        application.sources(Application.class);
        application.sources(JBossProxyInitializer.class);

        return (WebApplicationContext) application.run();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}