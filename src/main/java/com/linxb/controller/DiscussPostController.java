package com.linxb.controller;

import com.linxb.bean.Comment;
import com.linxb.bean.DiscussPost;
import com.linxb.bean.Page;
import com.linxb.bean.User;
import com.linxb.service.CommentService;
import com.linxb.service.DiscussPostService;
import com.linxb.service.LikeService;
import com.linxb.service.UserService;
import com.linxb.util.CommunityContant;
import com.linxb.util.CommunityUtil;
import com.linxb.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityContant{

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostController.class);

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUsers();
        if(user==null){
            //返回Json数据
            return CommunityUtil.getJsonString(403,"你还没有登陆");
        }
        if(title.length()==0 || content.length() == 0){
            return CommunityUtil.getJsonString(403,"标题和内容不能为空!");
        }
        // 初始化贴子内容
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date(System.currentTimeMillis()));
        discussPostService.addDiscussPost(post);
        logger.error("Insert信息");
        //报错的情况统一处理
        return CommunityUtil.getJsonString(0,"发布成功!");
    }


    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    //如果参数中有bean，最终springmvc都会存在model中，所以Page会存到model中
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){ //如果参数中有bean，最终springmvc都会存在model中
        //查询这个贴子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //根据userId查名字
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        // 查询点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);

        // 点赞状态
        int likeStatus = hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount()); //帖子相关字段中冗余存了一个commentcount
        //帖子的评论：称为--评论
        //评论的评论：称为--回复
        //评论列表
        List<Comment> comments = commentService.findCommentsByEntity(
                CommunityContant.ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(comments!=null){
            for(Comment c:comments){
                //评论Vo ：Vo的意思是viewObject的意思 视图对象
                Map<String,Object> commentVo = new HashMap<>();
                //放评论
                commentVo.put("comment",c);
                //放作者
                commentVo.put("user",userService.findUserById(c.getUserId()));
                // 查询点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT,c.getId());
                commentVo.put("likeCount",likeCount);

                // 点赞状态
                likeStatus = hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_COMMENT,c.getId());
                commentVo.put("likeStatus",likeStatus);
                //回复列表
                List<Comment> replys = commentService.findCommentsByEntity(CommunityContant.ENTITY_COMMENT, c.getId(), 0, Integer.MAX_VALUE);//不分页了
                //回复的Vo列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replys!=null){
                    for(Comment r:replys){
                        Map<String,Object> replyVo = new HashMap<>();
                        //放回复
                        replyVo.put("reply",r);
                        //放回复者user
                        replyVo.put("user",userService.findUserById(r.getUserId()));
                        // 查询点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT,r.getId());
                        replyVo.put("likeCount",likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_COMMENT,r.getId());
                        replyVo.put("likeStatus",likeStatus);
                        //放被回复者，如果有的话
                        User target = r.getTargetId() == 0 ? null : userService.findUserById(r.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }
                //回复加入进来
                commentVo.put("replys",replyVoList);
                //一条评论回复的数量
                int replyCount = commentService.findCommentCount(CommunityContant.ENTITY_COMMENT, c.getId());
                commentVo.put("replyCount",replyCount);
                //加入评论Vo
                commentVoList.add(commentVo);
            }
        }
        //传给模板
        model.addAttribute("comments",commentVoList);
        return "site/discuss-detail";
    }
}
