package com.edcode.activiti.mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
  @Insert("<script> insert into formdata (PROC_DEF_ID_,PROC_INST_ID_,FORM_KEY_,Control_ID_,Control_VALUE_)" +
      "    values" +
      "    <foreach collection=\"maps\" item=\"formData\" index=\"index\" separator=\",\">" +
      "      (#{formData.PROC_DEF_ID_,jdbcType=VARCHAR}," +
      "       #{formData.PROC_INST_ID_,jdbcType=VARCHAR}," +
      "       #{formData.FORM_KEY_,jdbcType=VARCHAR}, " +
      "       #{formData.Control_ID_,jdbcType=VARCHAR}," +
      "     #{formData.Control_VALUE_,jdbcType=VARCHAR})" +
      "    </foreach>  </script>")
  int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

}
