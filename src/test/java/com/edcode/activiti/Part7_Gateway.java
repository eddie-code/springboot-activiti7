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
 * @description 流程网关
 */
@SpringBootTest
public class Part7_Gateway {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 创建流程实例
     */
    @Test
    public void initProcessInstance() {
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_Inclusive");
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        Map<String, Object> variables = new HashMap<>();
        // 请假 1, 那么 wukong 和 shaseng 都能看到任务
        variables.put("day", "1");
        // bajie：0dddfea8-7518-11ec-a86a-5e879ca31830
//        taskService.complete("0dddfea8-7518-11ec-a86a-5e879ca31830");
        // 流程实例：0ddaf164-7518-11ec-a86a-5e879ca31830

        // wukong：a9631394-7518-11ec-8c08-5e879ca31830
        taskService.complete("a9631394-7518-11ec-8c08-5e879ca31830");
        // shaseng：a9631396-7518-11ec-8c08-5e879ca31830
        taskService.complete("a9631396-7518-11ec-8c08-5e879ca31830");
        System.out.println("完成任务");
    }

}
