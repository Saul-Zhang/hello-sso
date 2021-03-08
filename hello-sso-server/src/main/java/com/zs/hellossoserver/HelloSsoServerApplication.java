package com.zs.hellossoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ZhangSong
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.zs")
public class HelloSsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSsoServerApplication.class, args);
    }

}
