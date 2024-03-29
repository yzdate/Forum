package com.linxb.controller.intercepter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 拦截器
@Component
public class AlphaIntercepter implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlphaIntercepter.class);

    //cotroller 控制请求之前执行，返回布尔值
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle"+handler.toString());
        return true;
    }

    // 在调用完Controller之后执行的

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle"+handler.toString());
    }

    // 在templateEngine之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion"+handler.toString());
    }
}
