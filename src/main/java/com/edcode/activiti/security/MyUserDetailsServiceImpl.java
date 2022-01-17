package com.edcode.activiti.security;

import com.edcode.activiti.entity.UserInfoBean;
import com.edcode.activiti.mapper.UserInfoBeanMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class MyUserDetailsServiceImpl implements UserDetailsService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final UserInfoBeanMapper userInfoBeanMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    logger.info("登录用户：" + username);
    String passWord = passwordEncoder().encode("123456");
    logger.info("登录密码：" + passWord);
//    return new User(
//        // 没有做任何校验，密码是自定义
//        username,
//        passWord,
//        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ACTIVITI_USER"));

    // 读取数据库判断用户
    // 如果用户是 null 抛出异常
    // 返回用户信息

    UserInfoBean userInfoBean = userInfoBeanMapper.selectByUsername(username);
    if (userInfoBean == null) {
      logger.error("数据库中没有 [{}] 用户",username);
      throw new UsernameNotFoundException("数据库中没有此用户");
    }

    return userInfoBean;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
