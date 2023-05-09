package com.itheida.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheida.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
