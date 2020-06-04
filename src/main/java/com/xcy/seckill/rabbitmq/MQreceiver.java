package com.xcy.seckill.rabbitmq;

import com.xcy.seckill.pojo.SeckillOrder;
import com.xcy.seckill.pojo.SkUser;
import com.xcy.seckill.pojo.User;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.service.GoodsService;
import com.xcy.seckill.service.OrderService;
import com.xcy.seckill.service.SeckillService;
import com.xcy.seckill.vo.GoodsVo;
import com.xcy.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQreceiver {
    @Autowired
    SeckillService seckillService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RabbitListener(queues = "direct.queue")
    public void receive(String message){

        log.info("receive message:"+message);

        SeckillMessage m = RedisService.stringToBean(message, SeckillMessage.class);
        SkUser user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return;
        }

        //判断重复秒杀
        SeckillOrder order = orderService.getSkOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }

        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goodsVo);
    }

}
