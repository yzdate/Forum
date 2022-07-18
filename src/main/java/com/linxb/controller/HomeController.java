package com.linxb.controller;

import com.linxb.bean.DiscussPost;
import com.linxb.bean.Page;
import com.linxb.bean.User;
import com.linxb.service.DiscussPostService;
import com.linxb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController注解等价于@Controller、@ResponseBody注解结合在一起使用
//
//@ResponseBody注解的作用，是给页面返回json格式的数据，所以会把返回值直接写入HTTP response body中。最终”admin/tag_add”没有被解析为跳转路径，页面没有跳转效果。
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @RequestMapping(path="/index",method = RequestMethod.GET)
    //方法调用之前SpringMVC会自动实例化Model和Page，并将Page注入到Model中
    //所以可以直接访问page对象
    //若是路径中带有参数如index?current=2  current的值会自动封装进page中
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            for (DiscussPost post:
                    list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
//        model.addAttribute("page",page);
        return "/index";
    }
}
