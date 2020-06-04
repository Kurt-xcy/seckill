package com.xcy.seckill.service;

import com.xcy.seckill.pojo.SkUser;

public interface SkUserService {
    /**
     * 根据id查找User
     * @param id
     * @return
     */
    public SkUser getUserById(long id);

    public static final String COOKIE_NAME_TOKEN = "token";
}
