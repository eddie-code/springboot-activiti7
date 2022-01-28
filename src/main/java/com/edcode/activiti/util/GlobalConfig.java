package com.edcode.activiti.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eddie.lee
 * @date 2022-01-17 20:37
 * @description
 */
public class GlobalConfig {

  /**
   * 开发阶段 / 测试阶段
   */
  public static final Boolean Test = true;
  /**
   * windows
   */
  public static final String BPMN_PathMapping = "file:D:\\Develop\\Mine\\IdeaProjects\\springboot-activiti7\\src\\main\\resources\\resources\\bpmn\\";

  /**
   * linux
   */
//  public static final String BPMN_PathMapping = "/opt/activiti";

  @Getter
  @AllArgsConstructor
  public enum ResponseCode {
    SUCCESS(0, "成功"),
    ERROR(1, "错误");

    private final int code;
    private final String desc;

  }

}
