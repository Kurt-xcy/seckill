package com.xcy.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.xcy.seckill.vo.SeckillMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class MQsender {
    @Autowired
    AmqpTemplate amqpTemplate;


    public void sendMessage(String message){
        amqpTemplate.convertAndSend("direct.queue",message);
    }

    public void sendSKmessage(SeckillMessage seckillMessage){
        String message = JSON.toJSONString(seckillMessage);
        amqpTemplate.convertAndSend(message);

    }
}
