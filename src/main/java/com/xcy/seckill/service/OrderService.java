package com.xcy.seckill.service;

import com.xcy.seckill.pojo.OrderInfo;
import com.xcy.seckill.pojo.SeckillOrder;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.vo.GoodsVo;

public interface OrderService {

    SeckillOrder getSkOrderByUserIdGoodsId(long userId, long goodsId);

    OrderInfo createOrder(SkUser user, GoodsVo goods);
}
