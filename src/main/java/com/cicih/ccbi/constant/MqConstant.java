package com.cicih.ccbi.constant;

import org.springframework.amqp.rabbit.annotation.Queue;

public final class MqConstant {

    public static class Chart{
        public static final String CHART_EXCHANGE = "chart_exchange";
        public static final String CHART_QUEUE = "chart_queue";
        public static final String CHART_ROUTING_KEY = "chart_routingKey";
    }


}
