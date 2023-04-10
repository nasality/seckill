package com.bytedance.controller;

import com.bytedance.service.IUserService;
import com.bytedance.vo.LoginVo;
import com.bytedance.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private IUserService userService;

    @ResponseBody
    @RequestMapping("/doLogin")
    public RespBean doLogin(LoginVo loginVo , HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginVo, request, response);
    }

    @RequestMapping("/tologin")
    public String toLogin() {
        return "login";
    }
}
