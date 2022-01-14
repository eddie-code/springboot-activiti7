package com.edcode.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eddie.lee
 * @description
 */
@SpringBootTest
public class Part6_UEL {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 启动流程实例带参数，执行执行人
     */
    @Test
    public void initProcessInstanceWithArgs() {
        //流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("Executor", "wukong");
        //variables.put("Executor2", "aaa");
        //variables.put("Executor3", "wukbbbong");
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "myProcess_UEL_V1"
                        , "bKey002"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
    }

    /**
     * 完成任务带参数，指定流程变量测试
     */
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("pay", "101");
        taskService.complete("5d93f2dd-7477-11ec-907c-5e879ca31830", variables);
        System.out.println("完成任务");
    }

    /**
     * 启动流程实例带参数，使用实体类
     */
    @Test
    public void initProcessInstanceWithClassArgs() {

        UEL_POJO uel_pojo = UEL_POJO.builder()
                .executor("bajie")
                .build();

        //流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("uelpojo", uel_pojo);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "myProcess_uelv3"
                        , "bKey002"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());

    }

    /**
     * 任务完成环节带参数，指定多个候选人
     */
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        Map<String, Object> variables = new HashMap<>();
        // candidate 是在 Part6_UEL_V3.bpmn20.xml 中定义的， 这里含义是：八戒完成任务后，指定候选人为 wukong与tangseng
        variables.put("candidate", "wukong,tangseng");
        taskService.complete("efc7a59c-747f-11ec-965e-5e879ca31830", variables);
        System.out.println("完成任务");
    }

    //直接指定流程变量
    @Test
    public void otherArgs() {
        runtimeService.setVariable("efc7a59c-747f-11ec-965e-5e879ca31830", "pay", "101");
//        runtimeService.setVariables();
//        taskService.setVariable();
//        taskService.setVariables();

    }

    //局部变量
    @Test
    public void otherLocalArgs() {
        runtimeService.setVariableLocal("efc7a59c-747f-11ec-965e-5e879ca31830", "pay", "101");
//        runtimeService.setVariablesLocal();
//        taskService.setVariableLocal();
//        taskService.setVariablesLocal();
    }

}
