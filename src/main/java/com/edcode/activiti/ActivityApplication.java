package com.edcode.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author eddie.lee
 * @description
 */
//@SpringBootApplication
//public class ActivityApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ActivityApplication.class, args);
//    }
//
//}

@SpringBootApplication(scanBasePackages = {"com.edcode.activiti"})
public class ActivityApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ActivityApplication.class, args);
    }

    /**
     * 打 war 包方式
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ActivityApplication.class);
    }
}