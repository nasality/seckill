package com.bytedance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bytedance.pojo.User;
import com.bytedance.vo.LoginVo;
import com.bytedance.vo.RespBean;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface IUserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    RespBean uppdatePassword(String userTicket,String password, HttpServletRequest request, HttpServletResponse response);
}
