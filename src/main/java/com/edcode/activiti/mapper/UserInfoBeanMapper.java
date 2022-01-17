package com.edcode.activiti.mapper;

import com.edcode.activiti.entity.UserInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author eddie.lee
 * @date 2022-01-17 17:14
 * @description
 */
@Mapper
@Component
public interface UserInfoBeanMapper {

  /**
   * 从数据库中查询用户
   *
   * @param username
   * @return
   */
  @Select("select * from user where username = #{username}")
  UserInfoBean selectByUsername(@Param("username") String username);

}
