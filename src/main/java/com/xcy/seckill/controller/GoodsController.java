package com.xcy.seckill.controller;


import com.xcy.seckill.pojo.Goods;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.redis.GoodsKey;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.result.Result;
import com.xcy.seckill.service.GoodsService;
import com.xcy.seckill.service.SkUserService;
import com.xcy.seckill.util.AuthUtil;
import com.xcy.seckill.vo.GoodsDetailVo;
import com.xcy.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {
    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    SkUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(Model model, HttpServletRequest request, HttpServletResponse response) {

        if (new AuthUtil().authorizeInRedis(response, request,redisService)!=null){

            log.info("身份认证成功");

        }

        String html = redisService.get(GoodsKey.getGoodsDetail,"", String.class);

        if (html!=null){
            return html;
        }
        model.addAttribute("goodsList",goodsService.selAllGoods());

          //  return "goods_list";
        WebContext ctx =   new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if (html!=null|| !html.equals("")){
            Boolean sign = redisService.set(GoodsKey.getGoodsDetail,"",html);
        }
        return html;

    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return "OK";
    }

    @RequestMapping(value = "/to_list2")
    public String list2(Model model, HttpServletRequest request, HttpServletResponse response) {

        if (new AuthUtil().authorizeInRedis(response, request,redisService)!=null){

            log.info("身份认证成功");

        }

        model.addAttribute("goodsList",goodsService.selAllGoods());
         return "goods_list";


    }

    /**
     * 商品详情页面
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, SkUser user, @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user",new AuthUtil().authorizeInRedis(response, request,redisService));
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(miaoshaStatus);
        return Result.success(vo);
    }


    /**
     * 商品详情页面
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail1(HttpServletRequest request, HttpServletResponse response, Model model, SkUser user,
                                         @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user",new AuthUtil().authorizeInRedis(response, request,redisService));
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(miaoshaStatus);
        return Result.success(vo);
    }

}

