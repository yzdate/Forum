package com.linxb;

import com.linxb.bean.*;
import com.linxb.controller.intercepter.AlphaIntercepter;
import com.linxb.mapper.*;
import com.linxb.util.MailClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {



    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void contextLoads() {
    }

//    @Test
////    void  selectByIdTest(){
////        User user = userMapper.selectById(101);
////        System.out.println(user);
////    }

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


//    @Test
//    void  insertUserTest(){
//        User user = new User(1224,"fadfa","dagfg","dfasgas","dfgasgasd",0,1,"dfags","dasgasg",null);
//        userMapper.insertUser(user);
//        User user1 = userMapper.selectById(124);
//        System.out.println(user1);
//    }

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

//    @Test
//    public void testSelectLoginTicket(){
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
//        System.out.println(loginTicket);
//
//        loginTicketMapper.updateStatus("abc",0);
//        loginTicket = loginTicketMapper.selectByTicket("abc");
//        System.out.println(loginTicket);
//    }

    @Test
    public void testComment(){
        List<Comment> comment = commentMapper.selectCommentByEntity(1,228,0,3);
        System.out.println(comment);
    }

    @Test
    public void testInsertComment() {
        Comment comment = new Comment();
        comment.setContent("dgasdg");
        System.out.println(System.currentTimeMillis());
        comment.setCreateTime(new Date(System.currentTimeMillis()));
        comment.setStatus(2);
        comment.setEntityId(163);
        comment.setUserId(154);
        System.out.println(comment);
        commentMapper.insertComment(comment);

    }


    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectMessage() {
        Message message = new Message();
        message.setFromId(111);
        message.setContent("dgasgawsdg");
        message.setCreateTime(new Date(System.currentTimeMillis()));
        message.setStatus(1);
        message.setToId(123);
        message.setConversationId("111_123");
        List list = messageMapper.selectConversation(112,0,3);
        list.forEach(System.out::println);


    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

        String redisKey1 = "test:user";

        redisTemplate.opsForHash().put(redisKey1,"id",1);
        redisTemplate.opsForHash().put(redisKey1,"username","zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey1,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey1,"username"));
    }
}
