package com.linxb.service;

import com.linxb.bean.LoginTicket;
import com.linxb.bean.User;
import com.linxb.mapper.LoginTicketMapper;
import com.linxb.mapper.UserMapper;
import com.linxb.util.CommunityContant;
import com.linxb.util.CommunityUtil;
import com.linxb.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityContant {
    @Autowired
    private UserMapper userMapper;
    //因为上边只有userId根据此方法可得到userName
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    @Autowired
    private MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 对注册信息进行处理
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            //因为这不是异常需要返回给客户端
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已经存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已经注册");
            return map;
        }
        //注册用户
        //1.随机生成盐
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //2.加盐并加密
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //'0-普通用户; 1-超级管理员; 2-版主;'
        user.setType(0);
        //'0-未激活; 1-已激活;'
        user.setStatus(0);
        // 激活码使用getUUID值
        user.setActivationCode(CommunityUtil.generateUUID());
        //牛客头像地址0-1000
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //插入后user内会回填id 具体看user-mapper.xml
        userMapper.insertUser(user);


        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //url规定这么搞：http://localhost:8080/community/activation/101/code    #101-用户id，#code-激活码
        // 跳转到LoginController.activate()控制器 进行激活操作
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        // 将url传递到context中
        context.setVariable("url",url);
        String html = templateEngine.process("/mail/activation", context);

        // 发送邮件
        mailClient.sendMail(user.getEmail(),"牛客网激活账号",html);
        return map;
    }

    // 更新注册状态
    public int activion(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user==null){
            return ACTIVATION_FAILURE;
        }else if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(!code.equals(user.getActivationCode())){
            return ACTIVATION_FAILURE;
        }else{
            //设置激活状态
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
    }


    // 更新用户头像
    public int updateHeader(int userId,String headUrl){
        return userMapper.updateHeader(userId,headUrl);
    }

    // 更改密码
    public int updatepassword(int userId,String newpassword){
        return userMapper.updatePassword(userId,newpassword);
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}


