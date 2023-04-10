package com.bytedance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bytedance.exception.GlobalException;
import com.bytedance.mapper.UserMapper;
import com.bytedance.pojo.User;
import com.bytedance.service.IUserService;
import com.bytedance.util.CookieUtil;
import com.bytedance.util.MD5Util;
import com.bytedance.util.UUIDUtil;
import com.bytedance.vo.LoginVo;
import com.bytedance.vo.RespBean;
import com.bytedance.vo.RespBeanEnum;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userMapper.selectById(mobile);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if (!password.equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        String ticket = UUIDUtil.uuid();
        //将session放入Redis中
        redisTemplate.opsForValue().set("user:" + ticket , user);
        //将创建好的session放入客
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    //获取客户端传来的cookie
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasLength(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            //重置过期时间
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean uppdatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        user.setPassword(MD5Util.inputPassToFromPass(password));
        int result = userMapper.updateById(user);
        if (result == 1) {
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_ERROR);
    }


}
