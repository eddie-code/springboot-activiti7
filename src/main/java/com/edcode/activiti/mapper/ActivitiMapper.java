package com.edcode.activiti.mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author eddie.lee
 * @date 2022-01-19 16:30
 * @description
 */
@Mapper
@Component
public interface ActivitiMapper {

  /**
   * 写入表单
   * @param maps
   * @return int
   * @sql insert into formdata (PROC_DEF_ID_,PROC_INST_ID_,FORM_KEY_,Control_ID_,Control_VALUE_)
   * values (?, ?, ?, ?, ?) , (?, ?, ?, ?, ?)
   */
  @Insert("<script> INSERT INTO formdata (PROC_DEF_ID_,PROC_INST_ID_,FORM_KEY_,Control_ID_,Control_VALUE_)" +
      "    values" +
      "    <foreach collection=\"maps\" item=\"formData\" index=\"index\" separator=\",\">" +
      "      (#{formData.PROC_DEF_ID_,jdbcType=VARCHAR}," +
      "       #{formData.PROC_INST_ID_,jdbcType=VARCHAR}," +
      "       #{formData.FORM_KEY_,jdbcType=VARCHAR}, " +
      "       #{formData.Control_ID_,jdbcType=VARCHAR}," +
      "     #{formData.Control_VALUE_,jdbcType=VARCHAR})" +
      "    </foreach>  </script>")
  int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

  /**
   * 读取表单
   * @param PROC_INST_ID
   * @return List
   * @sql SELECT Control_ID_,Control_VALUE_ from formdata where PROC_INST_ID_ = ?
   */
  @Select("SELECT Control_ID_,Control_VALUE_ FROM formdata WHERE PROC_INST_ID_ = #{PROC_INST_ID}")
  List<HashMap<String,Object>> selectFormData(@Param("PROC_INST_ID") String PROC_INST_ID);

  /**
   * 获取用户名列表
   * @return List
   */
  @Select("SELECT name,username FROM user")
  List<HashMap<String,Object>> selectUser();

// 查询流程定义产生的流程实例数
//  SELECT
//  p.NAME_,
//  COUNT( DISTINCT e.PROC_INST_ID_ ) AS PiNUM
//  FROM
//  ACT_RU_EXECUTION AS e
//  RIGHT JOIN ACT_RE_PROCDEF AS p ON e.PROC_DEF_ID_ = p.ID_
//      WHERE
//  p.NAME_ IS NOT NULL
//  GROUP BY
//  p.NAME_

  //流程定义数
  //SELECT COUNT(ID_) from ACT_RE_PROCDEF

  //进行中的流程实例
  //SELECT COUNT(DISTINCT PROC_INST_ID_) from act_ru_execution

}
