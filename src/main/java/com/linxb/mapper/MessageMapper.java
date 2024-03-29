package com.linxb.mapper;


import com.linxb.bean.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {
    //查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversation(int userId, int offset, int limit);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);
    //查询某个会话所包含的私信列表
    List<Message> selectLetter(String conversationId,int offset,int limit);
    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);
    //查询未读的私信数量
    int selectLetterUnreadCount(int userId,String conversationId);
    // 减少未读私信数量
    int updateUnreadLetterNum(String conversationId);
    //  新增消息
    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId,String topic);
    // 查询某个主体下最新的通知数量
    int selectLatestNoticeCount(int userId,String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId,String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
