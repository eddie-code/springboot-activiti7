package com.edcode.activiti.controller;

import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
