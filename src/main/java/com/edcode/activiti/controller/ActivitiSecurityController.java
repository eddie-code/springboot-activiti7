package com.edcode.activiti.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @date 2022-01-17 20:52
 * @description 外部请求
 */
@RestController
@RequiredArgsConstructor
public class ActivitiSecurityController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @PostMapping("/login")
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  public String requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
    return new String("需要登录，使用/login.html或发起post求情");
  }

}
