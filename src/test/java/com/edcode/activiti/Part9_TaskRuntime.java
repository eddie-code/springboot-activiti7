package com.edcode.activiti;

import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @date 2022-01-17 15:20
 * @description 5.4.1 API 新特性 - TaskRuntime
 */
@SpringBootTest
public class Part9_TaskRuntime {

  @Autowired
  private SecurityUtil securityUtil;

  @Autowired
  private TaskRuntime taskRuntime;

  /**
   * 获取当前登录用户任务
   */
  @Test
  public void getTasks() {
    securityUtil.logInAs("wukong");
    Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
    tasks.getContent().forEach(task -> {
      System.out.println("-------------------");
      System.out.println("getId：" + task.getId());
      System.out.println("getName：" + task.getName());
      System.out.println("getStatus：" + task.getStatus());
      System.out.println("getCreatedDate：" + task.getCreatedDate());
      if (task.getAssignee() == null) {
        //候选人为当前登录用户，null的时候需要前端拾取
        System.out.println("Assignee：待拾取任务");
      } else {
        System.out.println("Assignee：" + task.getAssignee());
      }
    });
  }

  /**
   * 完成任务
   */
  @Test
  public void completeTask() {
    securityUtil.logInAs("wukong");
    Task task = taskRuntime.task("286808fc-74fc-11ec-b5e3-5e879ca31830");
    if (task.getAssignee() == null) {
      taskRuntime.claim(TaskPayloadBuilder.claim()
          .withTaskId(task.getId())
          .build());
    }
    taskRuntime.complete(TaskPayloadBuilder
        .complete()
        .withTaskId(task.getId())
        .build());
    System.out.println("任务执行完成");
  }
}