package com.edcode.activiti.controller;

import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.RepositoryService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author eddie.lee
 * @date 2022-01-18 21:54
 * @description 流程定义 api
 */
@RestController
@RequestMapping("/processDefinition")
@RequiredArgsConstructor
public class ProcessDefinitionController {

  private final RepositoryService repositoryService;

  /**
   * 获取流程定义列表
   * @return AjaxResponse
   */
  @GetMapping(value = "/getDefinitions")
  public AjaxResponse getDefinitions() {

    try {
      List<HashMap<String, Object>> listMap = new ArrayList<>();
      List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();

      // Lambda 升序排列
      list.sort((y, x) -> x.getVersion() - y.getVersion());

      repositoryService.createProcessDefinitionQuery().list().forEach(processDefinition -> {
        // 每次创建新 hashmap
        HashMap<String, Object> hashMap = new HashMap<>(16);
        //System.out.println("流程定义ID："+processDefinition.getId());
        hashMap.put("processDefinitionID", processDefinition.getId());
        hashMap.put("name", processDefinition.getName());
        hashMap.put("key", processDefinition.getKey());
        hashMap.put("resourceName", processDefinition.getResourceName());
        hashMap.put("deploymentID", processDefinition.getDeploymentId());
        hashMap.put("version", processDefinition.getVersion());
        listMap.add(hashMap);
      });

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          listMap
      );
    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "获取流程定义失败",
          e.toString()
      );
    }
  }

  /**
   * 上传BPMN流媒体
   * @param multipartFile 文件
   * @return AjaxResponse
   */
  @PostMapping(value = "/uploadStreamAndDeployment")
  public AjaxResponse uploadStreamAndDeployment(@RequestParam("processFile") MultipartFile multipartFile) {
    // 获取上传的文件名
    String fileName = multipartFile.getOriginalFilename();

    try {
      // 得到输入流（字节流）对象
      InputStream fileInputStream = multipartFile.getInputStream();

      // 文件的扩展名
      String extension = FilenameUtils.getExtension(fileName);

      Deployment deployment = null;
      String isZip = "zip";
      if (isZip.equals(extension)) {
        ZipInputStream zip = new ZipInputStream(fileInputStream);
        //初始化流程
        deployment = repositoryService.createDeployment()
            .addZipInputStream(zip)
            .name("流程部署名称可通过接口传递现在写死")
            .deploy();
      } else {
        //初始化流程
        deployment = repositoryService.createDeployment()
            .addInputStream(fileName, fileInputStream)
            .name("流程部署名称可通过接口传递现在写死")
            .deploy();
      }

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          deployment.getId()+";"+fileName);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "部署流程失败",
          e.toString());
    }

  }

  /**
   * 部署BPMN字符
   *  添加流程定义通过在线提交 BPMN 的 XML
   * @param stringBPMN
   * @return
   */
  @PostMapping(value = "/addDeploymentByString")
  public AjaxResponse addDeploymentByString(
      @RequestParam("stringBPMN") String stringBPMN,
      @RequestParam("deploymentName")String deploymentName) {
    try {
      Deployment deployment = repositoryService.createDeployment()
          .addString("CreateWithBPMNJS.bpmn",stringBPMN)
          .name("不知道在哪显示的部署名称")
          .deploy();

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          deployment.getId());

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "string部署流程失败",
          e.toString());
    }
  }

}
