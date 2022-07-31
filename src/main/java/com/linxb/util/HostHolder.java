package com.linxb.util;

import com.linxb.bean.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    // 使用ThreadLocal set 存值，get取值
    private ThreadLocal<User> users = new ThreadLocal<>();

    // 以线程为T存取值来保证线程安全
    public void setUsers(User user){
        users.set(user);
    }

    public User  getUsers(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
