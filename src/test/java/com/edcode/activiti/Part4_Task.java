package com.edcode.activiti;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @description
 *
 *  相关的表：ACT_RU_TASK、ACT_RU_VARIABLE、ACT_RU_IDENTITYLINK
 *
 */
@SpringBootTest
public class Part4_Task {

    @Autowired
    private TaskService taskService;

    /**
     * 任务查询
     */
    @Test
    public void getTasks() {
        taskService.createTaskQuery().list().forEach(task -> {
            System.out.println("Id：" + task.getId());
            System.out.println("Name：" + task.getName());
            System.out.println("Assignee：" + task.getAssignee());
        });
    }

    /**
     * 查询我的代办任务
     */
    @Test
    public void getTasksByAssignee() {
        taskService.createTaskQuery()
                .taskAssignee("wukong")
                .list().forEach(task -> {
                    System.out.println("Id：" + task.getId());
                    System.out.println("Name：" + task.getName());
                    System.out.println("Assignee：" + task.getAssignee());
                });
    }

    /**
     * 执行任务
     */
    @Test
    public void completeTask() {
        taskService.complete("659387de-7422-11ec-9bf1-5e879ca31830");
        System.out.println("完成任务");
    }

    /**
     * 拾取任务
     */
    @Test
    public void claimTask() {
        Task task = taskService.createTaskQuery().taskId("5a628824-7438-11ec-b645-5e879ca31830").singleResult();
        taskService.claim("5a628824-7438-11ec-b645-5e879ca31830", "bajie");
    }

    /**
     * 归还与交办任务
     */
    @Test
    public void setTaskAssignee() {
        Task task = taskService.createTaskQuery().taskId("5a628824-7438-11ec-b645-5e879ca31830").singleResult();
        taskService.setAssignee("5a628824-7438-11ec-b645-5e879ca31830", "null");//归还候选任务
        taskService.setAssignee("5a628824-7438-11ec-b645-5e879ca31830", "wukong");//交办
    }

}
