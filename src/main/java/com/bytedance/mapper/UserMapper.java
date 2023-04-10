package com.bytedance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bytedance.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
