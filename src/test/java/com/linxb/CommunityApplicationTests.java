package com.linxb;

import com.linxb.bean.DiscussPost;
import com.linxb.bean.LoginTicket;
import com.linxb.bean.User;
import com.linxb.controller.intercepter.AlphaIntercepter;
import com.linxb.mapper.DiscussPostMapper;
import com.linxb.mapper.LoginTicketMapper;
import com.linxb.mapper.UserMapper;
import com.linxb.util.MailClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;
import java.util.List;

@SpringBootTest
class CommunityApplicationTests {



    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void  selectByIdTest(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    void  updateHeaderTest(){
        userMapper.updateHeader(101,"safasg");
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    void  updateStatusTest(){
        userMapper.updateStatus(101,1);
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    void  updatePasswordTest(){
        userMapper.updatePassword(101,"asdfasd");
        User user = userMapper.selectById(101);
        System.out.println(user);
    }


    @Test
    void  insertUserTest(){
        User user = new User(1224,"fadfa","dagfg","dfasgas","dfgasgasd",0,1,"dfags","dasgasg",null);
        userMapper.insertUser(user);
        User user1 = userMapper.selectById(124);
        System.out.println(user1);
    }

    @Test
    void setDiscussPostMapperTest(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 100, 10);
        discussPosts.forEach(System.out::println);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",0);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
}
