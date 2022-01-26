package com.edcode.activiti.controller;

import com.edcode.activiti.SecurityUtil;
import com.edcode.activiti.entity.UserInfoBean;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import com.edcode.activiti.util.TestUserConstant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author eddie.lee
 * @date 2022-01-19 17:22
 * @description
 */
@RestController
@RequestMapping("/activitiHistory")
@RequiredArgsConstructor
public class ActivitiHistoryController {

  private final SecurityUtil securityUtil;

  private final RepositoryService repositoryService;

  private final HistoryService historyService;



  /**
   * 用户历史
   * @return AjaxResponse
   */
  @GetMapping(value = "/getInstancesByUserName")
  public AjaxResponse instancesByUser() {
    try {

      List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
          // 按历史任务实例结束时间排序
          .orderByHistoricTaskInstanceEndTime().asc()
          // 登录人
          .taskAssignee("bajie")
          .list();

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          historicTaskInstances);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "获取历史任务失败",
          e.toString());
    }
  }

  /**
   * 根据流程实例ID查询历史任务：
   *                      任务实例历史
   * @param piID 流程实例ID
   * @return
   */
  @GetMapping(value = "/getInstancesByPiID")
  public AjaxResponse getInstancesByPiID(@RequestParam("piID") String piID) {
    try {

      List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
          .orderByHistoricTaskInstanceEndTime().asc()
          .processInstanceId(piID)
          .list();

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          historicTaskInstances);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "获取历史任务失败",
          e.toString());
    }
  }

  /**
   * 流程图高亮
   * @param instanceId 流程实例id
   * @param UuserInfoBean 当前登录人实体
   * @return AjaxResponse
   *
   * 主要是看最后封装Map就懂了，分三个环节：
   *  1. 获取高亮的任务节点
   *  2. 获取高亮的连线
   *  3. 获取现在还有个环节需要完成的
   *  4. 获取当前登录人做过的任务
   *  5. 以上四点封装 Map 返回
   */
  @GetMapping("/gethighLine")
  public AjaxResponse gethighLine(@RequestParam("instanceId") String instanceId, @AuthenticationPrincipal UserInfoBean UuserInfoBean) {
    try {
      //查询当前实例的历史流程
      HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
          .processInstanceId(instanceId)
          .singleResult();
      //读取bpmn,获取bpmnModel对象
      BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
      //因为我们这里只定义了一个Process 所以获取集合中的第一个即可
      Process process = bpmnModel.getProcesses().get(0);
      //获取所有的FlowElement信息 （线信息）
      Collection<FlowElement> flowElements = process.getFlowElements();

      Map<String, String> map = new HashMap<>(16);
      for (FlowElement flowElement : flowElements) {
        //判断是否是连线
        if (flowElement instanceof SequenceFlow) {
          SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
          String ref = sequenceFlow.getSourceRef();
          String targetRef = sequenceFlow.getTargetRef();
          map.put(ref + targetRef, sequenceFlow.getId());
        }
      }
      //获取流程实例 历史节点(全部)
      List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
          .processInstanceId(instanceId)
          .list();
      //各个历史节点   两两组合 key
      Set<String> keyList = new HashSet<>();
      for (HistoricActivityInstance i : list) {
        for (HistoricActivityInstance j : list) {
          if (i != j) {
            keyList.add(i.getActivityId() + j.getActivityId());
          }
        }
      }
      //高亮连线ID
      Set<String> highLine = new HashSet<>();
      //将连线的信息放入新的Set集合
      keyList.forEach(s -> highLine.add(map.get(s)));
      //获取流程实例 历史节点（已完成）
      List<HistoricActivityInstance> listFinished = historyService.createHistoricActivityInstanceQuery()
          .processInstanceId(instanceId)
          .finished() // 完成
          .list();
      //已经完成的节点高亮
      Set<String> highPoint = new HashSet<>();
      //高亮节点ID
      listFinished.forEach(s -> highPoint.add(s.getActivityId()));

      //获取流程实例 历史节点（待办节点）
      List<HistoricActivityInstance> listUnFinished = historyService.createHistoricActivityInstanceQuery()
          .processInstanceId(instanceId)
          .unfinished() // 未完成（待办）
          .list();

      //待办高亮节点
      Set<String> waitingToDo = new HashSet<>();
      listUnFinished.forEach(s -> waitingToDo.add(s.getActivityId()));

      // 当前用户的任务
      String assigneeName = null;
      if (GlobalConfig.Test) {
        assigneeName = TestUserConstant.TEST_USER;
      } else {
        assigneeName = UuserInfoBean.getUsername();
      }

      // 创建历史任务实例查询
      List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
          // 执行人
          .taskAssignee(assigneeName)
          // 已完成的
          .finished()
          // 当前实例
          .processInstanceId(instanceId).list();
      // 待办高亮节点
      Set<String> iDo = new HashSet<>();
      // 任务实例列表, 添加任务定义Key
      taskInstanceList.forEach(s -> iDo.add(s.getTaskDefinitionKey()));

      // 组装 Map 返回以上的结果集的集合
      Map<String, Object> reMap = new HashMap<>(16);
      // 高亮的任务节点
      reMap.put("highPoint", highPoint);
      // 高亮的连线
      reMap.put("highLine", highLine);
      // 现在还有个环节需要完成的
      reMap.put("waitingToDo", waitingToDo);
      // 我做过的任务
      reMap.put("iDo", iDo);

      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.SUCCESS.getCode(),
          GlobalConfig.ResponseCode.SUCCESS.getDesc(),
          reMap);

    } catch (Exception e) {
      return AjaxResponse.AjaxData(
          GlobalConfig.ResponseCode.ERROR.getCode(),
          "渲染历史流程失败",
          e.toString());
    }
  }
}
