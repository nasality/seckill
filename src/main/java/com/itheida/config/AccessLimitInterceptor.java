package com.itheida.config;

import com.itheida.pojo.User;
import com.itheida.service.IUserService;
import com.itheida.util.CookieUtil;
import com.itheida.vo.RespBean;
import com.itheida.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

//拦截器
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    //前置拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            AccessLimit limit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (limit == null) {
                return true;
            }
            int second = limit.second();
            int maxCount = limit.maxCount();
            boolean needLogin = limit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }

            key = key + ":" + user.getId();
            ValueOperations operations = redisTemplate.opsForValue();
            Integer count = (Integer) operations.get(key);
            if (count == null) {
                operations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                operations.increment(key);
            } else {
                return false;
            }
        }
        return true;
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        //通过request获取请求中携带的sessionId
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (!StringUtils.hasLength(ticket)) {
            return null;
        }
        //根据页面传来的session去redis中获取响应的user对象，如果有对象， 证明已登录
        return userService.getUserByCookie(ticket, request, response);
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        //拼接响应头
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        //通用返回对象
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
