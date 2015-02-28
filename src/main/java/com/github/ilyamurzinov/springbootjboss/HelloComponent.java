package com.github.ilyamurzinov.springbootjboss;

import org.springframework.stereotype.Component;

/**
 * @author Murzinov Ilya [murz42@gmail.com]
 */
@Component
public class HelloComponent {
    public void go() {
        //Too lazy to add logging here :)
        System.out.println("Inside filter's component");
    }
}
