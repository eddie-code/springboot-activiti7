package com.edcode.activiti.controller;

import com.edcode.activiti.SecurityUtil;
import com.edcode.activiti.mapper.ActivitiMapper;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author eddie.lee
 * @date 2022-01-19 16:29
 * @description
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

  private final TaskRuntime taskRuntime;

  private final SecurityUtil securityUtil;

  private final ProcessRuntime processRuntime;

  private final RepositoryService repositoryService;

  private final ActivitiMapper mapper;

  private static final String TEST_USER = "bajie";

  /**
   * 获取我的代办任务
   * @return AjaxResponse
   */
  @GetMapping(value = "/getTasks")
  public AjaxResponse getTasks() {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }
      Page<Task> tasks = taskRuntime.tasks(
          Pageable.of(0, 100)
      );

      List<HashMap<String, Object>> listMap = tasks.getContent().stream().map(tk -> {
        ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
        HashMap<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("id", tk.getId());
        hashMap.put("name", tk.getName());
        hashMap.put("status", tk.getStatus());
        hashMap.put("createdDate", tk.getCreatedDate());
        //执行人，null时前台显示未拾取
        if (tk.getAssignee() == null) {
          hashMap.put("assignee", "待拾取任务");
        } else {
          hashMap.put("assignee", tk.getAssignee());
        }
        hashMap.put("instanceName", processInstance.getName());
        return hashMap;
      }).collect(Collectors.toList());

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          listMap);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "获取我的代办任务失败",
          e.toString());
    }
  }

}
