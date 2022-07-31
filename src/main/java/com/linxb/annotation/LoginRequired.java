package com.linxb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//自定义注解-可用域方法
@Retention(RetentionPolicy.RUNTIME)// 运行时可用
public @interface LoginRequired {
}
