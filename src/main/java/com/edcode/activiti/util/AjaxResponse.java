package com.edcode.activiti.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author eddie.lee
 * @date 2022-01-17 20:37
 * @description
 */
@Getter
@Setter
@AllArgsConstructor
public class AjaxResponse {

  private Integer status;

  private String msg;

  private Object obj;

  public static AjaxResponse AjaxData(Integer status, String msg, Object obj) {
    return new AjaxResponse(status, msg, obj);
  }
}

