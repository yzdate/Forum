package com.linxb.controller;

import com.linxb.bean.Event;
import com.linxb.bean.User;
import com.linxb.event.EventProducer;
import com.linxb.service.LikeService;
import com.linxb.util.CommunityContant;
import com.linxb.util.CommunityUtil;
import com.linxb.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityContant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId) {
        User user = hostHolder.getUsers();
        //实现点赞
        likeService.like(user.getId(), entityType, entityId,entityUserId);
        //统计数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        // 取消点赞不发送
        if(likeStatus == 1){
            Event event = new Event()
                    .setUserId(hostHolder.getUsers().getId())
                    .setEntityId(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }
}
