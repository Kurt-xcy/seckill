package com.xcy.seckill.service.impl;

import com.xcy.seckill.pojo.OrderInfo;
import com.xcy.seckill.pojo.SeckillOrder;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.redis.SeckillKey;
import com.xcy.seckill.service.GoodsService;
import com.xcy.seckill.service.OrderService;
import com.xcy.seckill.service.SeckillService;
import com.xcy.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Override
    public OrderInfo seckill(SkUser user, GoodsVo goods) {
        goodsService.reduceStock(goods);
        return orderService.createOrder(user, goods);
    }

    @Override
    public long getSeckillResult(long userId, long goodsId){
        SeckillOrder order = orderService.getSkOrderByUserIdGoodsId(userId, goodsId);
        if (order != null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }
    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

}
