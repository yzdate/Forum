package com.linxb.util;

import org.springframework.stereotype.Component;

@Component
public interface CommunityContant {
    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;

    // 默认状态的登入凭证的超时时间
    int DEFAULT_EXPIRED_SECONDS=3600*12;

    // 记住状态下的登入凭证超时时间
    int REMEMBER_EXPIRED_SECONDS=3600*24*100;

    // 实体类型：贴子
    int ENTITY_TYPE_POST = 1;

    // 实体类型:评论
    int ENTITY_COMMENT = 2;

    // 实体类型：人
    int ENTITY_PEOPLE = 3;

    // 主题 点赞
    String TOPIC_LIKE = "like";

    // 主题 评论
    String TOPIC_COMMENT = "comment";

    // 主题 关注
    String TOPIC_FOLLOW = "follow";

    int SYSTEM_ID = 1;

    // 主题 发帖
    String TOPIC_PUBLISH = "publish";
}
