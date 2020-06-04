package com.xcy.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.xcy.seckill.exception.GlobalException;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.redis.UserKey;
import com.xcy.seckill.result.CodeMsg;
import com.xcy.seckill.result.Result;
import com.xcy.seckill.service.SkUserService;
import com.xcy.seckill.util.AuthUtil;
import com.xcy.seckill.util.MD5Util;
import com.xcy.seckill.util.UUIDUtil;
import com.xcy.seckill.util.ValidatorUtil;
import com.xcy.seckill.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller

@Slf4j
public class LoginController {
    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    SkUserService skUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/login")
    public String loginPage(){
        return "login";
    }

    @RequestMapping("/login/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo, HttpServletResponse response, HttpServletRequest request){
        log.info(loginVo.toString());
        if (new AuthUtil().authorizeInRedis(response, request,redisService)!=null){
            return Result.success(true);
        }

//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                //cookie中存在user的token
//                if (cookie.getName().equals("token")) {
//                    String token = cookie.getValue();
//                    SkUser skUser = redisService.get(UserKey.token, token, SkUser.class);
//                    log.info(UserKey.token.getPrefix() + token);
//                    //缓存中存在User
//                    if (skUser != null) {
//                        cookie.setMaxAge(UserKey.TOKEN_EXPIRE);
////                        Cookie cookieUser = new Cookie("user", JSON.toJSONString(skUser));
////                        response.addCookie(cookieUser);
//                        //在cookie中加入身份已验证标志
//                        Cookie authCookie = new Cookie("authentication", "1");
//                        cookie.setPath("/");
//                        response.addCookie(authCookie);
//                        return Result.success(true);
//
//                    }
//                }
//
//            }
//        }

        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SkUser user = skUserService.getUserById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDbPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.getUUID();
        redisService.set(UserKey.token, token, user);
        //测试user中的时间
        log.info(user.getRegisterDate().toString());
        //把token加入cookie
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        //在cookie中加入身份已验证标志
        Cookie authCookie = new Cookie("authentication","1");
        cookie.setPath("/to_list");
        response.addCookie(authCookie);

        return Result.success(true);
    }

}
