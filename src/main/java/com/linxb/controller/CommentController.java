package com.linxb.controller;

import com.linxb.bean.Comment;
import com.linxb.service.CommentService;
import com.linxb.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUsers().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date(System.currentTimeMillis()));
//        logger.error(comment.toString());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
