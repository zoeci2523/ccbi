package com.cicih.ccbi.config;

import com.cicih.ccbi.constant.MqConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQConfig {
    @Bean
    public Queue chartQueue(){
        return new Queue(MqConstant.Chart.CHART_QUEUE);
    }


}
