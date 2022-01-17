package com.edcode.activiti.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author eddie.lee
 * @date 2022-01-17 20:30
 * @description Security 配置类
 */
@Configuration
@RequiredArgsConstructor
public class ActivitiSecurityConfig extends WebSecurityConfigurerAdapter {

  private final LoginSuccessHandler loginSuccessHandler;

  private final LoginFailureHandler loginFailureHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .formLogin()
        // 登录方法
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .successHandler(loginSuccessHandler)
        .failureHandler(loginFailureHandler)
        .and()
        .authorizeRequests()
        .anyRequest().permitAll()
        .and()
        .logout().permitAll()
        .and()
        .csrf().disable()
        .headers().frameOptions().disable()
    ;
  }
}
