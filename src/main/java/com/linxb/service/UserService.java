package com.linxb.service;

import com.linxb.bean.User;
import com.linxb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //因为上边只有userId根据此方法可得到userName
    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
