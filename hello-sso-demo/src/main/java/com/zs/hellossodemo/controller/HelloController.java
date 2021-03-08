package com.zs.hellossodemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ZhangSong
 */
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "index";

    }
}
