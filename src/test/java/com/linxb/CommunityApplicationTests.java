package com.linxb;

import com.linxb.bean.DiscussPost;
import com.linxb.bean.User;
import com.linxb.mapper.DiscussPostMapper;
import com.linxb.mapper.UserMapper;
import com.linxb.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private UserMapper userMapper;

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

}
