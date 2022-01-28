package com.edcode.activiti.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author eddie.lee
 * @date 2022-01-18 21:12
 * @description
 */
@Configuration
public class PathMapping implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 默认映射
    registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/");
    // 指定路径，那么就不用在去 target 找文件
    registry.addResourceHandler("/bpmn/**").addResourceLocations(GlobalConfig.BPMN_PathMapping);
  }
}
