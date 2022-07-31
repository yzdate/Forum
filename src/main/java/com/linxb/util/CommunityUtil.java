package com.linxb.util;




import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;


import java.util.UUID;

@Component
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("","");
    }

    //md5加密
    // hello -> abd123def456
    // hello +3e4a8 -> abc123def456abc
    public static String md5(String key){

//        通过lang3工具包，检测前台传过来的数据是否为空
        //null,空串，空格都会判空
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
