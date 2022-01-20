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

  private static final String TEST_USER = "wukong";

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

  /**
   * 完成待办任务
   * @param taskID
   * @return
   */
  @GetMapping(value = "/completeTask")
  public AjaxResponse completeTask(@RequestParam("taskID") String taskID) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }

      Task task = taskRuntime.task(taskID);

      // 判断执行人是否null
      if (task.getAssignee() == null) {
        // 那么就是候选人, 就直接拾取任务
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
      }
      // 完成任务
      taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId())
          //.withVariable("num", "2")//执行环节设置变量
          .build());

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          null);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "完成任务 {"+taskID+"} 失败",
          e.toString());
    }
  }


  /**
   * 渲染表单
   * @param taskID
   * @return
   */
  @GetMapping(value = "/formDataShow")
  public AjaxResponse formDataShow(@RequestParam("taskID") String taskID) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs("bajie");
      }
      Task task = taskRuntime.task(taskID);
      //本实例所有保存的表单数据HashMap，为了快速读取控件以前环节存储的值
      HashMap<String, String> controlistMap = new HashMap<>();
      // 通过任务id，可以拿到流程定义ID
      UserTask userTask =  (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
          // 在 v6 版本是可以获取到任务的key, 现在 v7 版本反而获取不到，不过可以使用另外一种方式，就是使用表单的key
          .getFlowElement(task.getFormKey());

      if (userTask == null) {
        return AjaxResponse.AjaxData(
            GlobalConfig.ResponseCode.SUCCESS.getCode(),
            GlobalConfig.ResponseCode.SUCCESS.getDesc(),
            "无表单");
      }

      List<FormProperty> formProperties = userTask.getFormProperties();
      List<HashMap<String, Object>> listMap = new ArrayList<>();
      for (FormProperty fp : formProperties) {
        // FormProperty_0rg77oq-_!string-_!姓名-_!请输入姓名-_!f
        String[] splitFP = fp.getId().split("-_!");
        HashMap<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("id", splitFP[0]);
        // 控制类型
        hashMap.put("controlType", splitFP[1]);
        // 控制标签
        hashMap.put("controlLiable", splitFP[2]);
        // 默认值
        hashMap.put("controlDevalue", splitFP[3]);
        // 参数
        hashMap.put("controlParam", splitFP[4]);

        listMap.add(hashMap);
      }

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          listMap);
    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "失败",
          e.toString());
    }
  }

  /**
   * 保存表单
   * @param taskID 任务ID
   * @param formData 表单数据
   * @return
   */
  @PostMapping(value = "/formDataSave")
  public AjaxResponse formDataSave(
      @RequestParam("taskID") String taskID,
      @RequestParam("formData") String formData) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs("bajie");
      }
      Task task = taskRuntime.task(taskID);

      List<HashMap<String, Object>> listMap = new ArrayList<>();
      //前端传来的字符串，拆分成每个控件
      String[] formDataList = formData.split("!_!");
      for (String controlItem : formDataList) {
        String[] formDataItem = controlItem.split("-_!");
        HashMap<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
        hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
        hashMap.put("FORM_KEY_", task.getFormKey());
        // 从 formData 获取
        hashMap.put("Control_ID_", formDataItem[0]);
        hashMap.put("Control_VALUE_", formDataItem[1]);
        listMap.add(hashMap);
      }

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          listMap);
    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "提交任务表单失败",
          e.toString());
    }
  }

}
