package com.linxb.controller.intercepter;

import com.linxb.annotation.LoginRequired;
import com.linxb.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 首先判断handle是否为方法
        if(handler instanceof HandlerMethod){
            // 转型成handlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 直接获取方法对象
            Method method = handlerMethod.getMethod();
            // 从方法对象获取自定义注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);

            // 判断注解不为空，以及判断是否能够获得user，是否登入
            if(loginRequired != null && hostHolder.getUsers()==null){
                // 获得user为空则重对象到登入界面（未登入）
                response.sendRedirect(request.getContextPath()+"/login");
                // 拒绝后续请求
                return false;
            }
        }
        return true;
    }


}
