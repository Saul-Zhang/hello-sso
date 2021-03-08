package com.zs.hellossodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.zs.hellossoserver"})
public class HelloSsoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSsoDemoApplication.class, args);
    }

}
