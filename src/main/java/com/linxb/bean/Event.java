package com.linxb.bean;

import java.util.HashMap;
import java.util.Map;

public class Event {
    //张三给李四点赞---userId是张三，entityUserId是李四
    private String topic;
    // 发送人
    private int userId;
    private int entityType;
    // 实体Id
    private int entityId;
    // 评论人
    private int entityUserId;
    // 额外数据
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }
    public int getUserId() {
        return userId;
    }
    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }
    public int getEntityType() {
        return entityType;
    }
    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }
    public int getEntityId() {
        return entityId;
    }
    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }
    public int getEntityUserId() {
        return entityUserId;
    }
    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }
    public Map<String, Object> getData() {
        return data;
    }
    public Event setData(String key,Object object) {
        this.data.put(key,object);
        return this;
    }
}