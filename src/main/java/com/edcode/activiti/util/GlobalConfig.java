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
  public static final Boolean Test = false;

  @Getter
  @AllArgsConstructor
  public enum ResponseCode {
    SUCCESS(0, "成功"),
    ERROR(1, "错误");

    private final int code;
    private final String desc;

  }

}
