package com.edcode.activiti.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author eddie.lee
 * @date 2022-01-17 16:48
 * @description
 */
@Component
public class UserInfoBean implements UserDetails {

  private Long id;

  public String name;

  private String address;

  private String username;

  private String password;

  private String roles;

  /**
   * 从数据库中取出roles字符串后，进行分解，构成一个GrantedAuthority的List返回
   *
   * 库里面是逗号分隔，ROLE_ACTIVITI_USER,GROUP_activitiTeam,g_bajiewukong
   *
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.stream(roles.split(","))
        .map(e -> new SimpleGrantedAuthority(e))
        .collect(Collectors.toSet());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getAddress() {
    return address;
  }
}
