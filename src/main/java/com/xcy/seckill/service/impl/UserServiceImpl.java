package com.xcy.seckill.service.impl;

import com.xcy.seckill.mapper.UserMapper;
import com.xcy.seckill.pojo.User;
import com.xcy.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Override
    public User getUser(int id) {
        return userMapper.getById(id);
    }
}
