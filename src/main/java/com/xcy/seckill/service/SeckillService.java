package com.xcy.seckill.service;


import com.xcy.seckill.pojo.OrderInfo;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.vo.GoodsVo;

public interface SeckillService {
    public OrderInfo seckill(SkUser user, GoodsVo goods);

    public long getSeckillResult(long userId, long goodsId);

}
