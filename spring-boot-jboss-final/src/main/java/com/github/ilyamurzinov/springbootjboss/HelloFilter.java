package com.github.ilyamurzinov.springbootjboss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
public class HelloFilter extends GenericFilterBean {
    @Autowired
    private HelloComponent component;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        component.go();
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
