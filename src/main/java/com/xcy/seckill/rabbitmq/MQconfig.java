package com.xcy.seckill.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQconfig {
    /**
     * direct交换机模式
     * @return
     */
    @Bean
    public Queue directQueue(){
        return new Queue("direct.queue",true);
    }


}
