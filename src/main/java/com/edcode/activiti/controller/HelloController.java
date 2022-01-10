package com.edcode.activiti.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String say() {
        return "hello world";
    }

}
