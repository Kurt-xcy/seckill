package com.xcy.seckill.util;

import com.alibaba.fastjson.JSON;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.redis.UserKey;
import com.xcy.seckill.result.Result;
import com.xcy.seckill.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthUtil {

    public  SkUser authorizeInRedis(HttpServletResponse response, HttpServletRequest request, RedisService redisService) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                //cookie中存在user的token
                if (cookie.getName().equals("token")) {
                    log.info("存在token的cookie");
                    String token = cookie.getValue();
                    SkUser skUser = redisService.get(UserKey.token, token, SkUser.class);
                    log.info(UserKey.token.getPrefix() + token);
                    //缓存中存在User
                    if (skUser != null) {
                        return skUser;

                    }
                }

            }
        }
        return null;
    }
}
