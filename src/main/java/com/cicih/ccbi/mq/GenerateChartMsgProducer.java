package com.cicih.ccbi.mq;

import com.cicih.ccbi.constant.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenerateChartMsgProducer extends MsgProducer{
    // TODO 可以在传入队列前加入消息调度机制

    @Override
    public void sendMessage(String message){
        log.info("Chart producer sent message: {}", message);
        rabbitTemplate.convertAndSend(MqConstant.Chart.CHART_EXCHANGE, MqConstant.Chart.CHART_ROUTING_KEY, message);
    }
}
