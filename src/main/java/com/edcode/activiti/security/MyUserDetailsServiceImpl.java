package com.edcode.activiti.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author eddie.lee
 * @date 2022-01-17 16:23
 * @description
 */
@Configuration
public class MyUserDetailsServiceImpl implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    String passWord = passwordEncoder().encode("123456");
    return new User(
        // 没有做任何校验，密码是自定义
        username,
        passWord,
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ACTIVITI_USER"));

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
