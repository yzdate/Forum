package com.linxb.config;

import com.linxb.controller.intercepter.AlphaIntercepter;
import com.linxb.controller.intercepter.LoginTicketInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 拦截器的配置类
// 需要实现一个接口 WebMvcConfigurer
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    // 注册拦截器
    // excludePathPatterns// 设置路径不被拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(alphaIntercepter)
               .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
