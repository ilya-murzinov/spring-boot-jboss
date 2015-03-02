package com.github.ilyamurzinov.springbootjboss;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@Component
public class JBossProxyInitializer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Disabling filters created by Spring so JBoss wouldn't register them as well
        for (String name : beanFactory.getBeanDefinitionNames()) {
            if (!name.contains("FilterRegistrationBean")) {
                continue;
            }
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            definition.getPropertyValues().add("enabled", false);
        }

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new Application.HelloFilterProxy());
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));

        beanFactory.registerSingleton("helloFilterProxyFilterRegistrationBean", filterRegistrationBean);

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new Application.DispatcherServletProxy());
        servletRegistrationBean.setUrlMappings(Collections.singletonList("/*"));

        beanFactory.registerSingleton("dispatcherServletProxyRegistrationBean", servletRegistrationBean);
    }
}
