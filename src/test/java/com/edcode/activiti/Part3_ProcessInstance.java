package com.edcode.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @description
 */
@SpringBootTest
public class Part3_ProcessInstance {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 初始化流程实例
     *
     * 流程实例ID：myProcess_Part1:1:d7652884-7388-11ec-b254-5e879ca31830
     *
     * mysql tables ACT_RU_EXECUTION 创建数据
     */
    @Test
    public void initProcessInstance() {
        //1、获取页面表单填报的内容，请假时间，请假事由，String fromData
        //2、fromData 写入业务表，返回业务表主键ID==businessKey
        //3、把业务数据与Activiti7流程数据关联
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_1uel_V2", "bKey003");
        //
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
    }

    /**
     * 获取流程实例列表
     */
    @Test
    public void getProcessInstances() {
        runtimeService.createProcessInstanceQuery().list().forEach(processInstance -> {
            System.out.println("--------流程实例------");
            System.out.println("ProcessInstanceId：" + processInstance.getProcessInstanceId());
            System.out.println("ProcessDefinitionId：" + processInstance.getProcessDefinitionId());
            System.out.println("isEnded" + processInstance.isEnded());
            System.out.println("isSuspended：" + processInstance.isSuspended());
        });
    }

    /**
     * 暂停与激活流程实例
     */
    @Test
    public void activitieProcessInstance() {
//        runtimeService.suspendProcessInstanceById("df08e9f8-73b9-11ec-8e8a-5e879ca31830");
//        System.out.println("挂起流程实例");

        runtimeService.activateProcessInstanceById("df08e9f8-73b9-11ec-8e8a-5e879ca31830");
        System.out.println("激活流程实例");
    }

    /**
     * 删除流程实例
     */
    @Test
    public void delProcessInstance() {
        try {
            runtimeService.deleteProcessInstance("df08e9f8-73b9-11ec-8e8a-5e879ca31830", "删着玩");
            System.out.println("删除流程实例");
        } catch (Exception e) {
            System.out.println("因为挂起或者激活过，都会拋异常！ 其实删除流程成功的！");
            getProcessInstances();
        }
    }

}
