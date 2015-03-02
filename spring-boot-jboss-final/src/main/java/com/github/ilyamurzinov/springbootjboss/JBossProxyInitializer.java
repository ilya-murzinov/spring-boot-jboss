package com.github.ilyamurzinov.springbootjboss;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@Component
public class JBossProxyInitializer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //Disabling filters created by Spring so JBoss wouldn't register them as well
        for (String name : beanFactory.getBeanDefinitionNames()) {
            if (!name.contains("FilterRegistrationBean")) {
                continue;
            }
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            definition.getPropertyValues().add("enabled", false);
        }

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HelloFilterProxy());
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));

        beanFactory.registerSingleton("helloFilterProxyFilterRegistrationBean", filterRegistrationBean);

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new DispatcherServletProxy());
        servletRegistrationBean.setUrlMappings(Collections.singletonList("/*"));

        beanFactory.registerSingleton("dispatcherServletProxyRegistrationBean", servletRegistrationBean);
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
