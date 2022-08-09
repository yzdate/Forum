package com.linxb.controller;

import com.alibaba.fastjson.JSONObject;
import com.linxb.bean.Message;
import com.linxb.bean.Page;
import com.linxb.bean.User;
import com.linxb.mapper.UserMapper;
import com.linxb.service.MessageService;
import com.linxb.service.UserService;
import com.linxb.util.CommunityContant;
import com.linxb.util.CommunityUtil;
import com.linxb.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityContant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //处理私信列表
    @RequestMapping(path = "letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUsers();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //拿到对话对方的userId
                int targetId = user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //用户未读的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId, Page page, Model model){
//        // 减少未读的数量
//        messageService.deleteUnreadLetterNum(conversationId);
        //设置分页信息
        page.setPath("/letter/detail/"+conversationId);
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        //私信列表
        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(lettersList!=null){
            for(Message message:lettersList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //判断和谁对话
        User target = getLetterTarget(conversationId);
        model.addAttribute("target",target);

        // 设置已读
        List<Integer> ids = getLetterIds(lettersList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        User user = hostHolder.getUsers();
        String[] ids = conversationId.split("_");
        int id1 = Integer.parseInt(ids[0]);
        int id2 = Integer.parseInt(ids[1]);
        return user.getId()==id1?userService.findUserById(id2):userService.findUserById(id1);
    }

    // 得到未读信息的id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for(Message message: letterList){
                if(hostHolder.getUsers().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");

        }
        Message message = new Message();
        message.setFromId(hostHolder.getUsers().getId());
        message.setToId(target.getId());
        message.setCreateTime(new Date(System.currentTimeMillis()));
        if(message.getFromId() < message.getToId()){
            message.setConversationId((message.getFromId() + "_" + message.getToId()));
        }else{
            message.setConversationId((message.getToId() + "_" + message.getFromId()));
        }
        message.setContent(content);
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    // 显示通知列表
    @RequestMapping(path="/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUsers();
        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String,Object> messageVo = new HashMap<>();
        if(message!=null){
            messageVo.put("message",message);
            String content = message.getContent();
            //{&quot;entityType&quot;:1,&quot;entityId&quot;:275,&quot;postId&quot;:275,&quot;userId&quot;:111}
            content = HtmlUtils.htmlUnescape(content);  //这步可以去除上方转义字符
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            //谁发给我？
            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unread",unread);
        }
        model.addAttribute("commentNotice",messageVo);
        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVo = new HashMap<>();
        if(message!=null){
            messageVo.put("message",message);
            String content = message.getContent();
            //{&quot;entityType&quot;:1,&quot;entityId&quot;:275,&quot;postId&quot;:275,&quot;userId&quot;:111}
            content = HtmlUtils.htmlUnescape(content);  //这步可以去除上方转义字符
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            //谁发给我？
            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unread",unread);
        }
        model.addAttribute("likeNotice",messageVo);
        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        if(message!=null){
            messageVo.put("message",message);
            String content = message.getContent();
            //{&quot;entityType&quot;:1,&quot;entityId&quot;:275,&quot;postId&quot;:275,&quot;userId&quot;:111}
            content = HtmlUtils.htmlUnescape(content);  //这步可以去除上方转义字符
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            //谁关注了我？
            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unread",unread);
        }
        model.addAttribute("followNotice",messageVo);
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return  "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Page page,Model model){
        User user = hostHolder.getUsers();
        //处理分页
        page.setLimit(5);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        page.setPath("/notice/detail/"+topic);
        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if(noticeList!=null){
            for(Message notice:noticeList){
                Map<String,Object> map = new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content = notice.getContent();
                content = HtmlUtils.htmlUnescape(content);
                HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer)data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                map.put("fromUser",userService.findUserById(notice.getFromId())); //系统名
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);
        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
