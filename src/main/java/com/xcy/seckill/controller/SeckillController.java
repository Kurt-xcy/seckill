package com.xcy.seckill.controller;


import com.xcy.seckill.pojo.OrderInfo;
import com.xcy.seckill.pojo.SeckillOrder;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.rabbitmq.MQsender;
import com.xcy.seckill.redis.GoodsKey;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.result.CodeMsg;
import com.xcy.seckill.result.Result;
import com.xcy.seckill.service.GoodsService;
import com.xcy.seckill.service.OrderService;
import com.xcy.seckill.service.SeckillService;
import com.xcy.seckill.service.SkUserService;
import com.xcy.seckill.util.AuthUtil;
import com.xcy.seckill.vo.GoodsVo;
import com.xcy.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

	@Autowired
	SkUserService skUserService;

	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	SeckillService seckillService;

	@Autowired
	MQsender sender;


	private static HashMap<Long,Boolean> localOverMap = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsVoList = goodsService.selAllGoods();
		if (goodsVoList==null){
			return;
		}
		for(GoodsVo goods : goodsVoList) {
			redisService.set(GoodsKey.getGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}


	@RequestMapping("/do_seckill")
	@ResponseBody
	public Result<Integer> doseckill(Model model,HttpServletRequest request,HttpServletResponse response,@RequestParam("goodsId")long goodsId){
		//判断登陆
		SkUser user = (new AuthUtil()).authorizeInRedis(response, request,redisService);
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//预减库存
		long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
		if (stock<0){
			return Result.error(CodeMsg.SECKILL_OVER);
		}

		//判断是否已经秒杀到了，不能重复秒杀
		SeckillOrder order = orderService.getSkOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_SECKILL);
		}

		//入队
		SeckillMessage mm = new SeckillMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		sender.sendSKmessage(mm);
		return Result.success(0);//排队中

	}


    @RequestMapping("/do_seckill2")
    public String list(Model model,
					   @RequestParam("goodsId")long goodsId, HttpServletResponse response, HttpServletRequest request) {
    	SkUser user = (new AuthUtil()).authorizeInRedis(response, request,redisService);
    	model.addAttribute("user", user);
    	if(user == null) {
    		return "login";
    	}
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
    		return "sk_fail";
    	}
    	//判断是否已经秒杀到了
    	SeckillOrder order = orderService.getSkOrderByUserIdGoodsId(user.getId(), goodsId);

    	if(order != null) {
			log.info("            "+order.getId()+"               "+order.getGoodsId());
    		model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
    		return "sk_fail";
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = seckillService.seckill(user, goods);
    	model.addAttribute("orderInfo", orderInfo);
    	model.addAttribute("goods", goods);
        return "order-detail";
    }


	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 */
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	@ResponseBody
	public Result<Long> seckillResult(Model model, HttpServletRequest request,HttpServletResponse response,
									  @RequestParam("goodsId") long goodsId) {

		SkUser user = (new AuthUtil()).authorizeInRedis(response, request,redisService);
		model.addAttribute("user", user);
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
		return Result.success(orderId);
	}

}
