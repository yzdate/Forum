package com.linxb.controller.intercepter;

import com.linxb.bean.LoginTicket;
import com.linxb.bean.User;
import com.linxb.mapper.LoginTicketMapper;
import com.linxb.service.LoginService;
import com.linxb.service.UserService;
import com.linxb.util.CookieUtil;
import com.linxb.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService LoginService;

    @Autowired
    private  UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.通过cookie 得到ticket
        String ticket = CookieUtil.getValue(request,"ticket");
        // 2.如果ticket不为空，通过LoginService找到ticket对应的user
        if(ticket != null){
            // 查询凭证
            LoginTicket loginTicket = LoginService.findLoginTicket(ticket);

            // 检查凭证是否有效
            // 不为空 status为0 并且时间小于过期时间
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after((new Date()))){
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());

                // 在本次请求中持有用户
                hostHolder.setUsers(user);

            }
        }
        return true;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUsers();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
