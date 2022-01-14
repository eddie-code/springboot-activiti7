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
                .startProcessInstanceByKey("myProcess_Exclusive");
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        Map<String, Object> variables = new HashMap<>();
        // 请假 101
        variables.put("day", "101");
        // bajie流程实例 ce30fac7-7507-11ec-a00a-5e879ca31830
        taskService.complete("ce30fac7-7507-11ec-a00a-5e879ca31830", variables);
        System.out.println("完成任务");
    }

}
