package com.itheida.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheida.pojo.User;
import com.itheida.vo.LoginVo;
import com.itheida.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface IUserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    RespBean uppdatePassword(String userTicket,String password, HttpServletRequest request, HttpServletResponse response);
}
