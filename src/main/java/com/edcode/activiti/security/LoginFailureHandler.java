package com.edcode.activiti.security;

import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author eddie.lee
 * @date 2022-01-17 20:44
 * @description 登录失败
 */
@Component("loginFailureHandler")
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, AuthenticationException e)
      throws IOException, ServletException {
    logger.info("登录失败");
    httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    httpServletResponse.setContentType("application/json;charset=UTF-8");

    httpServletResponse.getWriter().write(objectMapper.writeValueAsString(
        AjaxResponse.AjaxData(
            GlobalConfig.ResponseCode.ERROR.getCode(),
            GlobalConfig.ResponseCode.ERROR.getDesc(),
            "登录失败："+e.getMessage()
        )));
  }
}
