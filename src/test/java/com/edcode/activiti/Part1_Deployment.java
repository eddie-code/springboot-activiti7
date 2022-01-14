package com.edcode.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * @author eddie.lee
 * @description 流程部署Deployment
 *
 *  mysql tables : ACT_RE_DEPLOYMENT、ACT_GE_BYTEARRAY
 *
 */
@SpringBootTest
public class Part1_Deployment {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 通过bpmn部署流程
     */
    @Test
    public void initDeploymentBPMN() {
        String filename = "BPMN/Part7_Inclusive.bpmn20.xml";
//        String pngname="BPMN/Part1_Deployment.png";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
//                .addClasspathResource(pngname)
                .name("流程部署测试 - 包容网关")
                .deploy();
        System.out.println(deployment.getName());
    }

    /**
     * 通过ZIP部署流程
     */
    @Test
    public void initDeploymentZIP() {
        InputStream fileInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("BPMN/Part1_DeploymentV2.zip");
        assert fileInputStream != null;
        ZipInputStream zip = new ZipInputStream(fileInputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zip)
                .name("流程部署测试zip")
                .deploy();
        System.out.println(deployment.getName());
    }

    /**
     * 查询流程部署
     */
    @Test
    public void getDeployments() {
        repositoryService.createDeploymentQuery().list().forEach(deployment -> {
            System.out.println("Id：" + deployment.getId());
            System.out.println("Name：" + deployment.getName());
            System.out.println("DeploymentTime：" + deployment.getDeploymentTime());
            System.out.println("Key：" + deployment.getKey() + "\n\r");
        });
    }

    /**
     * 删除流程部署
     */
    @Test
    public void deleteDeployments() {
        String id = "267a1037-7506-11ec-8633-5e879ca31830";
        repositoryService.deleteDeployment(id);
        System.out.println("删除流程部署：" + id);
    }

}