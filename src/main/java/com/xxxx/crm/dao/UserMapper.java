package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;
import org.apache.ibatis.annotations.Mapper;


public interface UserMapper extends BaseMapper<User, Integer> {

    // 通过 用户名来查询用户记录，返回用户对象
    public User queryUserByName(String userName);

    //
}