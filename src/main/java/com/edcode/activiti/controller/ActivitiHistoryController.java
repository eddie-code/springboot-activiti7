package com.edcode.activiti.controller;

import com.edcode.activiti.SecurityUtil;
import com.edcode.activiti.util.AjaxResponse;
import com.edcode.activiti.util.GlobalConfig;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
