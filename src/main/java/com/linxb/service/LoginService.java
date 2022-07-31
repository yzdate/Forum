package com.linxb.service;

import com.linxb.bean.LoginTicket;
import com.linxb.bean.User;
import com.linxb.mapper.LoginTicketMapper;
import com.linxb.mapper.UserMapper;
import com.linxb.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class LoginService {
    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    UserMapper userMapper;

    public Map<String,Object> login(String username, String password, int expiredSeconds){
        // 设置一个返回的map,用来传输错误的信息
        Map<String,Object> map = new HashMap<>();
        //空值的处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }else if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","用户不存在");
            return map;
        }
        //验证状态
        if(user.getActivationCode()==null){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(user.getStatus());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;


    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }
}