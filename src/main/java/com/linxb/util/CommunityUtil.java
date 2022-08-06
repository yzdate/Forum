package com.linxb.util;




import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;


import java.util.Map;
import java.util.UUID;

/**
 *
 */
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

    /**
     * 封装成json的方法
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJsonString(int code, String msg){
        return getJsonString(code,msg,null);
    }
    public static String getJsonString(int code){
        return getJsonString(code,null,null);
    }
}
