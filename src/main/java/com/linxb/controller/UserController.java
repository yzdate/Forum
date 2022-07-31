package com.linxb.controller;

import com.linxb.annotation.LoginRequired;
import com.linxb.bean.User;
import com.linxb.service.UserService;
import com.linxb.util.CommunityUtil;
import com.linxb.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method= RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        // 判断是否上传图片
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }

        // 防止图片名重复
        String filename = headerImage.getOriginalFilename();

        // 取得后缀
        String suffix = filename.substring(filename.lastIndexOf("."));

        if(StringUtils.isEmpty(suffix)){
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }

        // 生成随机图片名
        filename = CommunityUtil.generateUUID()+suffix;

        // 确定文件存放的路径
        File dest = new File(uploadPath+"/"+filename);

        // 写入文件
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }

        // 更新当前用户头像的路径(web访问路径)
        // http://localhost:8089/community/user/header/xxx.png

        // 1. 获取当前用户
        User users = hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/" +filename;
        userService.updateHeader(users.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    private void getHerder(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存放的路劲
        fileName = uploadPath + "/" + fileName;
        // 文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 设置响应的类型
        response.setContentType("image/"+suffix);
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取头像失败！"+e.getMessage());
        }
    }


    @RequestMapping(path="/changepwd",method = RequestMethod.POST)
    public String changePassword(String oldpassword,String newpassword1,String newpassword,Model model){

        User user = hostHolder.getUsers();

        if (oldpassword == null) {
            model.addAttribute("oldLengthMsg", "请您重新输入原密码!");
            return "/site/setting";
        } else {
            if (oldpassword.length() < 6) {
                model.addAttribute("oldLengthMsg", "密码长度不能小于6位!");
                return "/site/setting";
            }
        }

        if (newpassword == null || newpassword1 == null) {
            model.addAttribute("lengthMsg", "请您重新输入原密码!");
            return "/site/setting";
        } else {
            if (newpassword.length() < 6) {
                model.addAttribute("lengthMsg", "密码长度不能小于6位!");
                return "/site/setting";
            }
        }

        if(!newpassword.equals(newpassword1)){
            model.addAttribute("equalsErr", "两次密码不一致!");
            return "/site/setting";
        }

        if(user != null){
            oldpassword = CommunityUtil.md5(oldpassword+user.getSalt());
            if(user.getPassword().equals(oldpassword)){
                newpassword = CommunityUtil.md5(newpassword+user.getSalt());
                userService.updatepassword(user.getId(),newpassword);
                return "redirect:/index";
            }else{
                model.addAttribute("oldLengthMsg","原始密码错误!");
            }
        }else{
            model.addAttribute("oldlengthMsg","更改密码错误!");
        }

        return "/site/setting";
    }
}
