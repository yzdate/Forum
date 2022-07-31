package com.linxb.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    @Bean
    public Producer kapchaProducer(){
        Properties properties = new Properties();
        // 设置宽度
        properties.setProperty("kaptcha.image.width","100");
        // 设置长度
        properties.setProperty("kaptcha.image.height","40");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size","32");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.color","0,0,0");
        // 验证码类型
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMN");
        // 生成验证码长度
        properties.setProperty("kaptcha.textproducer.char.length","4");
        // 生成验证码的噪声，防止暴力破解
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;

    }

}
