package com.edcode.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @description
 */
@SpringBootTest
public class Part5_HistoricTaskInstance {

    @Autowired
    private HistoryService historyService;

    /**
     * 根据用户名查询历史记录
     */
    @Test
    public void HistoricTaskInstanceByUser() {
        historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .taskAssignee("bajie")
                .list().forEach(historicTaskInstance -> {
                    System.out.println("Id：" + historicTaskInstance.getId());
                    System.out.println("ProcessInstanceId：" + historicTaskInstance.getProcessInstanceId());
                    System.out.println("Name：" + historicTaskInstance.getName());
                });
    }

    /**
     * 根据流程实例ID查询历史
     */
    @Test
    public void HistoricTaskInstanceByPiID(){
        historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .processInstanceId("56174151-7421-11ec-80e4-5e879ca31830")
                .list().forEach(historicTaskInstance -> {
                    System.out.println("Id："+ historicTaskInstance.getId());
                    System.out.println("ProcessInstanceId："+ historicTaskInstance.getProcessInstanceId());
                    System.out.println("Name："+ historicTaskInstance.getName());
                });
    }

}
