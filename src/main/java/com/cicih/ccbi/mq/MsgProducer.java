package com.cicih.ccbi.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

public abstract class MsgProducer {
    @Resource
    protected RabbitTemplate rabbitTemplate;

    protected void sendMessage(String message){}
}

