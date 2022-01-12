package com.edcode.activiti;

import org.activiti.engine.RepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author eddie.lee
 * @description 流程定义 - ProcessDefinition
 */
@SpringBootTest
public class Part2_ProcessDefinition {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 查询流程定义
     */
    @Test
    public void getProcessDefinition() {
        repositoryService.createProcessDefinitionQuery().list().forEach(processDefinition -> {
            System.out.println("Name:" + processDefinition.getName());
            System.out.println("Key:" + processDefinition.getKey());
            System.out.println("ResourceName:" + processDefinition.getResourceName());
            System.out.println("DeploymentId:" + processDefinition.getDeploymentId());
            System.out.println("Version:" + processDefinition.getVersion() + "\n\r");
        });
    }

    /**
     * 删除流程定义
     *
     *  这些表都会出现变化: ACT_RE_PROCDEF --> ACT_RE_DEPLOYMENT --> ACT_GE_BYTEARRAY
     */
    @Test
    public void delProcessDefinition() {
        // 上面打印的 DeploymentId
        String pid = "e59fe37d-7388-11ec-92c7-5e879ca31830";
        // true 情况下，会删除所有的流程定义、历史等，如果是 false 是会保留历史，一般情况下是 false
        repositoryService.deleteDeployment(pid,true);
        System.out.println("删除流程定义成功");
    }

}
