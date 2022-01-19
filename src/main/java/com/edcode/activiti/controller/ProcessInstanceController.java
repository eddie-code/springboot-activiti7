package com.edcode.activiti.controller;

import com.edcode.activiti.SecurityUtil;
import com.edcode.activiti.entity.UserInfoBean;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import com.edcode.activiti.util.GlobalConfig.ResponseCode;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author eddie.lee
 * @date 2022-01-19 9:37
 * @description 流程实例
 */
@RestController
@RequestMapping("/processInstance")
@RequiredArgsConstructor
public class ProcessInstanceController {

  private final RepositoryService repositoryService;

  private final ProcessRuntime processRuntime;

  private final SecurityUtil securityUtil;

  private final String TEST_USER = "bajie";

  /**
   * 查询流程实例:
   *    注解 @AuthenticationPrincipal 认证用的
   * @param userInfoBean
   * @return AjaxResponse
   */
  @GetMapping(value = "/getInstances")
  public AjaxResponse getInstances(@AuthenticationPrincipal UserInfoBean userInfoBean) {
    try {

      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }

      Page<ProcessInstance> processInstances = processRuntime.processInstances(
          Pageable.of(0, 100)
      );
      List<ProcessInstance> list = processInstances.getContent();
      // Lambda 降序排序
      list.sort((y, x) -> x.getStartDate().toString().compareTo(y.getStartDate().toString()));
      // jdk8 写法
      List<HashMap<String, Object>> listMap = processInstances.getContent().stream().map(processInstance -> {
        HashMap<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("id", processInstance.getId());
        hashMap.put("name", processInstance.getName());
        hashMap.put("status", processInstance.getStatus());
        hashMap.put("processDefinitionId", processInstance.getProcessDefinitionId());
        hashMap.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        hashMap.put("startDate", processInstance.getStartDate());
        hashMap.put("processDefinitionVersion", processInstance.getProcessDefinitionVersion());
        // TODO 这块之后 LEFT JOIN 语句会更有效率
        //因为processRuntime.processDefinition("流程部署ID")查询的结果没有部署流程与部署ID，所以用repositoryService查询
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            // 仅选择具有给定id的流程定义
            .processDefinitionId(processInstance.getProcessDefinitionId())
            .singleResult();
        // 部署流程名称
        hashMap.put("resourceName", processDefinition.getResourceName());
        // 部署ID
        hashMap.put("deploymentId", processDefinition.getDeploymentId());
        return hashMap;
      }).collect(Collectors.toList());

      return AjaxResponse.AjaxData(
          ResponseCode.SUCCESS.getCode(),
          ResponseCode.SUCCESS.getDesc(),
          listMap);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          ResponseCode.ERROR.getCode(),
          "获取流程实例失败",
          e.toString());
    }
  }

  /**
   * 启动流程实例
   * @param processDefinitionKey
   * @param instanceName
   * @return
   */
  @GetMapping(value = "/startProcess")
  public AjaxResponse startProcess(
      // 流程定义的Key
      @RequestParam("processDefinitionKey") String processDefinitionKey,
      @RequestParam("instanceName") String instanceName
  ){

    try {

      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }else{
        securityUtil.logInAs(SecurityContextHolder.getContext().getAuthentication().getName());
      }

      // org.activiti.api.process.model.payloads.StartProcessPayload
      ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
          .start()
          .withProcessDefinitionKey(processDefinitionKey)
          // 自定义名称
          .withName(instanceName)
          //.withVariable("content", instanceVariable)
          //.withVariable("参数2", "参数2的值")
          .withBusinessKey("自定义BusinessKey")
          .build());

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          processInstance.getName()+"；"+processInstance.getId()
      );

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "创建流程实例失败", e.toString()
      );
    }
  }

  /**
   * 挂起流程实例
   *    描述：在当前运行中的流程实例，用户暂时不想使用，又不想删除，所以只能挂起
   * @param instanceID 实例ID
   * @return AjaxResponse
   */
    @GetMapping(value = "/suspendInstance")
  public AjaxResponse suspendInstance(@RequestParam("instanceID") String instanceID) {

    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }

      ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder
          .suspend()
          .withProcessInstanceId(instanceID)
          .build()
      );
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          processInstance.getName());
    }
    catch(Exception e)
    {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "挂起流程实例失败",
          e.toString());
    }
  }

  /**
   * 激活流程实例
   * @param instanceID
   * @return
   */
  @GetMapping(value = "/resumeInstance")
  public AjaxResponse resumeInstance(@RequestParam("instanceID") String instanceID) {

    try {
      if (GlobalConfig.Test) {
        securityUtil.logInAs(TEST_USER);
      }

      ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder
          .resume()
          .withProcessInstanceId(instanceID)
          .build()
      );
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          processInstance.getName());
    }
    catch(Exception e)
    {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "激活流程实例失败",
          e.toString());
    }
  }

}
