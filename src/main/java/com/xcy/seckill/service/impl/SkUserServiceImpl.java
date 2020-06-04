package com.xcy.seckill.service.impl;

import com.xcy.seckill.mapper.SkUserMapper;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.service.SkUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkUserServiceImpl implements SkUserService {



    @Autowired
    private SkUserMapper skUserMapper;



    @Override
    public SkUser getUserById(long id) {
        return skUserMapper.getById(id);
    }
}
