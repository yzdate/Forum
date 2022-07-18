package com.linxb;

import com.linxb.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
public class MailTests {


    @Autowired
    private MailClient mailClient;


    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("841025100@qq.com","test","welcome");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("841025100@qq.com","HTML",content);
    }
}
