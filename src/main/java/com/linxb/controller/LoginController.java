package com.linxb.controller;

import com.google.code.kaptcha.Producer;
import com.linxb.bean.User;
import com.linxb.service.LoginService;
import com.linxb.service.UserService;
import com.linxb.util.CommunityContant;
import com.linxb.util.CommunityUtil;
import com.linxb.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityContant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path="/register",method= RequestMethod.GET)
    public String getRegisterPage(){
        return"/site/register";
    }

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    // 接收注册信息数据
    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        System.out.println("**********************************************************************");
        // 将user传入Service进行验证与处理，返回一个注册信息验证map
        Map<String, Object> map = userService.register(user);
        System.out.println(user);
        System.out.println(map);
        // map没有返回信息，跳转到operate-result界面 跳转到目标连接
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送一封邮件，请查收并激活账号");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else{
            //注册失败返回注册页面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 激活函数
    //url规定这么搞：http://localhost:8080/community/activation/101/code    #101-用户id，#code-激活码
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId")int userId,
                             @PathVariable("code") String code){
        int result = userService.activion(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号可以使用");
            model.addAttribute("target","/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活");
            model.addAttribute("target","/login");
        }else{
            model.addAttribute("msg","激活失败，激活码不正确请重新注册");
            model.addAttribute("target","/register");
        }
        return "site/operate-result";
    }
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(Model model){
        return "site/login";
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        // 将验证码存入session
//        session.setAttribute("kaptcha",text);

        // 将验证码存入redis
        // 1.生成验证码凭证
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextpath);
        response.addCookie(cookie);

        // 2. 存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            // 使用os进行传输
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败",e.getMessage());
        }

    }

    @RequestMapping(path="/login",method=RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,Model model,
                        HttpSession session,HttpServletResponse httpServletResponse,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        // 在表现层判断验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;

        // 从Cookie中提取验证码凭证
        if(!StringUtils.isEmpty(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if(StringUtils.isEmpty(kaptcha) || StringUtils.isEmpty(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "site/login";
        }

        // 检查账号，密码（可以给业务层处理）
        int expireSecond = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = loginService.login(username, password, expireSecond);
        // 判断是否有传回ticket
        if(map.containsKey("ticket")){
            System.out.println(map.containsKey("ticket"));
            // 登入成功，跳到首页
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextpath);
            //设置有效时间
            cookie.setMaxAge(expireSecond);
            // 提取cookie时使用servlet发送cookie
            httpServletResponse.addCookie(cookie);
            return "redirect:/index";
        }else{
            // 没有则回到登入界面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }


    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        loginService.logout(ticket);
        return "redirect:/login";
    }
}
