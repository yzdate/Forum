package com.linxb.event;

import com.alibaba.fastjson.JSONObject;
import com.linxb.bean.DiscussPost;
import com.linxb.bean.Event;
import com.linxb.bean.Message;
import com.linxb.service.DiscussPostService;
import com.linxb.service.ElasticsearchService;
import com.linxb.service.MessageService;
import com.linxb.util.CommunityContant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityContant {
    //记日志
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //消息最终是要往message表中插入数据的
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_LIKE,TOPIC_COMMENT,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误");
            return;
        }

        //发送站内通知，主要是构造message对象
        Message message = new Message();

        //User表中id为1代表系统用户
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //为啥不直接把Event转成json存在content里边，这不就是even全数么
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误");
            return;
        }

        //查询出这个帖子
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        //往es中存数据
        elasticsearchService.saveDiscussPost(post);

    }
}
