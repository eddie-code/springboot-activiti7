package com.edcode.activiti.controller;

import com.edcode.activiti.SecurityUtil;
import com.edcode.activiti.mapper.ActivitiMapper;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import com.edcode.activiti.util.TestUserConstant;
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

  /**
   * 获取我的代办任务
   *
   * @return AjaxResponse
   */
  @GetMapping(value = "/getTasks")
  public AjaxResponse getTasks() {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TestUserConstant.TEST_USER);
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
   *
   * @param taskID
   * @return
   */
  @GetMapping(value = "/completeTask")
  public AjaxResponse completeTask(@RequestParam("taskID") String taskID) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TestUserConstant.TEST_USER);
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
          "完成任务 {" + taskID + "} 失败",
          e.toString());
    }
  }


  /**
   * 渲染表单
   *
   * @param taskID
   * @return
   */
  @GetMapping(value = "/formDataShow")
  public AjaxResponse formDataShow(@RequestParam("taskID") String taskID) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TestUserConstant.TEST_USER);
      }
      Task task = taskRuntime.task(taskID);

      //-----------------------构建表单控件历史数据字典-----------------------
      // 本实例所有保存的表单数据HashMap，为了快速读取控件以前环节存储的值
      HashMap<String, String> controlistMap = new HashMap<>(16);
      // 本实例所有保存的表单数据
      List<HashMap<String, Object>> tempControlList = mapper.selectFormData(task.getProcessInstanceId());
      for (HashMap<String, Object> ls : tempControlList) {
        controlistMap.put(ls.get("Control_ID_").toString(), ls.get("Control_VALUE_").toString());
      }
      /*
        FormProperty_0ueitp2-_!类型-_!名称-_!默认值-_!是否参数
        例子：
        FormProperty_0lovri0-_!string-_!姓名-_!请输入姓名-_!f
        FormProperty_1iu6onu-_!int-_!年龄-_!请输入年龄-_!s

        默认值：无、字符常量、FormProperty_开头定义过的控件ID
        是否参数：f为不是参数，s是字符，t是时间(不需要int，因为这里int等价于string)
        注：类型是可以获取到的，但是为了统一配置原则，都配置到
       */
      // 注意!!!!!!!!:表单Key必须要任务编号一模一样，因为参数需要任务key，但是无法获取，只能获取表单key“task.getFormKey()”当做任务key
      UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
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
        // 参数
        hashMap.put("controlParam", splitFP[4]);
        //默认值，如果是表单控件ID
        if (splitFP[3].startsWith("FormProperty_")) {
          //控件ID存在
          if (controlistMap.containsKey(splitFP[3])) {
            hashMap.put("controlDefValue", controlistMap.get(splitFP[3]));
          } else {
            //控件ID不存在
            hashMap.put("controlDefValue", "读取失败，检查" + splitFP[0] + "配置");
          }
        } else {
          //默认值如果不是表单控件ID则写入默认值
          hashMap.put("controlDefValue", splitFP[3]);
        }
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
   *
   * @param taskID   任务ID
   * @param formData 表单数据
   * @return AjaxResponse
   * <p>
   * formData:控件id-_!控件值-_!是否参数!_!控件id-_!控件值-_!是否参数 <p>
   * FormProperty_0rg77oq-_!不是参数-_!f!_!FormProperty_02og61s-_!我是参数-_!s
   */
  @PostMapping(value = "/formDataSave")
  public AjaxResponse formDataSave(
      @RequestParam("taskID") String taskID,
      @RequestParam("formData") String formData) {
    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TestUserConstant.TEST_USER);
      }
      Task task = taskRuntime.task(taskID);

      HashMap<String, Object> variables = new HashMap<>(16);
      //没有任何参数
      boolean hasVariables = false;

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
//        hashMap.put("Control_PARAM_", formDataItem[2]);
        listMap.add(hashMap);

        // 构建参数集合
        // 是否参数：f为不是参数，s是字符，t是时间，b是布尔值（不需要int，因为这里 int 等价于 string）
        switch (formDataItem[2]) {
          case "f":
            System.out.println("控件值不作为参数");
            break;
          case "s":
            // key=控件ID, value=控件的值
            variables.put(formDataItem[0], formDataItem[1]);
            hasVariables = true;
            break;
          case "t":
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            variables.put(formDataItem[0], timeFormat.parse(formDataItem[2]));
            hasVariables = true;
            break;
          case "b":
            variables.put(formDataItem[0], BooleanUtils.toBoolean(formDataItem[2]));
            hasVariables = true;
            break;
          default:
            System.out.println("控件参数类型配置错误：" + formDataItem[0] + "的参数类型不存在，" + formDataItem[2]);
        }
      }

      if (hasVariables) {
        //带参数完成任务
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID)
            .withVariables(variables)
            .build());
      } else {
        // 没有任何参数
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID)
            .build());
      }

      //写入数据库
      mapper.insertFormData(listMap);

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
