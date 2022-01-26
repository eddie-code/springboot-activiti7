package com.edcode.activiti.controller;

import com.edcode.activiti.mapper.ActivitiMapper;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author eddie.lee
 * @date 2022-01-26 10:26
 * @description
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final ActivitiMapper activitiMapper;

  /**
   * 获取用户名列表
   *
   * @return AjaxResponse
   */
  @GetMapping(value = "/getUsers")
  public AjaxResponse getUsers() {
    try {
      List<HashMap<String, Object>> userList = activitiMapper.selectUser();

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          userList);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "获取用户列表失败",
          e.toString());
    }
  }

}
