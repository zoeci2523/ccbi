package com.cicih.ccbi.config;

import com.cicih.ccbi.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQConfig {
    @Bean
    public Queue chartQueue(){
        return new Queue(MqConstant.Chart.CHART_QUEUE);
    }

    @Bean
    public Exchange chartExchange(){
        return new TopicExchange(MqConstant.Chart.CHART_EXCHANGE);
    }

    @Bean
    public Binding chartQueueBind(Exchange chartExchange, Queue chartQueue){
        return BindingBuilder.bind(chartQueue).to(chartExchange).with(MqConstant.Chart.CHART_ROUTING_KEY).noargs();
    }


}
