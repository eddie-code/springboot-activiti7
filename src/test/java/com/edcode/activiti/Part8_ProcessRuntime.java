package com.edcode.activiti;

import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @description
 */
@SpringBootTest
public class Part8_ProcessRuntime {

  @Autowired
  private ProcessRuntime processRuntime;

  @Autowired
  private SecurityUtil securityUtil;

  /**
   * 获取流程实例
   */
  @Test
  public void getProcessInstance() {
    // 1. 登录
    securityUtil.logInAs("bajie");
    // 2. 分页查询
    Page<ProcessInstance> processInstancePage = processRuntime.processInstances(Pageable.of(0, 100));
    System.out.println("流程实例数量：" + processInstancePage.getTotalItems());
    processInstancePage.getContent().forEach(processInstance -> {
      System.out.println("-----------------------");
      System.out.println("getId：" + processInstance.getId());
      System.out.println("getName：" + processInstance.getName());
      System.out.println("getStartDate：" + processInstance.getStartDate());
      System.out.println("getStatus：" + processInstance.getStatus());
      System.out.println("getProcessDefinitionId：" + processInstance.getProcessDefinitionId());
      System.out.println("getProcessDefinitionKey：" + processInstance.getProcessDefinitionKey());
    });
  }

  /**
   * 启动流程实例
   */
  @Test
  public void startProcessInstance() {
    securityUtil.logInAs("bajie");
    ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
        .start()
        .withProcessDefinitionKey("myProcess_ProcessRuntime")
        .withName("第一个流程实例名称")
        // .withVariable("","")
        .withBusinessKey("自定义bKey").build());
  }

  /**
   * 删除流程实例
   */
  @Test
  public void delProcessInstance() {
    securityUtil.logInAs("bajie");
    ProcessInstance processInstance = processRuntime.delete(ProcessPayloadBuilder
        .delete()
        .withProcessInstanceId("3f28b96a-775d-11ec-ac61-5e879ca31830")
        .build());
  }

  /**
   * 挂起流程实例
   * getStatus：RUNNING to getStatus：SUSPENDED （暂停）
   */
  @Test
  public void suspendProcessInstance() {
    securityUtil.logInAs("bajie");
    ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder
        .suspend()
        .withProcessInstanceId("5a5eb790-7438-11ec-b645-5e879ca31830")
        .build());
  }

  /**
   * 激活流程实例
   * getStatus：SUSPENDED to getStatus：RUNNING
   */
  @Test
  public void resumeProcessInstance() {
    securityUtil.logInAs("bajie");
    ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder
        .resume()
        .withProcessInstanceId("5a5eb790-7438-11ec-b645-5e879ca31830")
        .build());
  }

  /**
   * 流程实例 UEL变量参数
   * VariableInstance
   * 版本6 是在org.activiti.engine.*
   * 版本7 是在org.activiti.api.*
   */
  @Test
  public void getVariables() {
    securityUtil.logInAs("bajie");
    processRuntime
        .variables(ProcessPayloadBuilder.variables()
            .withProcessInstanceId("5d8dd859-7477-11ec-907c-5e879ca31830")
            .build())
        .forEach(variableInstance -> {
          System.out.println("-------------------");
          System.out.println("getName：" + variableInstance.getName());
          System.out.println("getValue：" + variableInstance.getValue());
          System.out.println("getTaskId：" + variableInstance.getTaskId());
          System.out.println("getProcessInstanceId：" + variableInstance.getProcessInstanceId());
        });
  }
}
