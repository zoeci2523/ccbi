package com.cicih.ccbi.mq;

import com.cicih.ccbi.config.JsonConfig;
import com.cicih.ccbi.constant.MqConstant;
import com.cicih.ccbi.controller.ApiSender;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.model.dto.api.ChatRequest;
import com.cicih.ccbi.model.dto.api.ChatResponse;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.service.TaskService;
import com.rabbitmq.client.Channel;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class GenerateChartMsgConsumer {
    @Resource
    private TaskService taskService;
    @Resource
    private ChartDetailService chartService;

    private static final String CHART_SYSTEM_PROMPT = "You are a Data analyst and Frontend developer, please provide output based on the following template and user input. Do not output any header/footer/comments:\n" +
                                                      "<<<<<\n" +
                                                      "[Apache Echarts setOption configuration json object version 5.0.0 based on analysis goal and data given by user]\n" +
                                                      "<<<<<\n" +
                                                      "[Clear analysis conclusion, be detailed, no comments]";


    @RabbitListener(queues = {MqConstant.Chart.CHART_QUEUE}, ackMode = "MANUAL")
    private void receiveMessage(
        String taskId,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        log.info("receive message from task: {}", taskId);
        try{
            if (StringUtils.isBlank(taskId)) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Message Queue is empty");
            }
            Task task = taskService.getById(taskId);
            ThrowUtils.throwIf(task == null,
                ErrorCode.NOT_FOUND_ERROR,
                "Failed to handle message queue due to task not found"
            );
            ChartDetail chart = chartService.getById(task.getContentId());
            ThrowUtils.throwIf(chart == null,
                ErrorCode.NOT_FOUND_ERROR,
                "Failed to handle message queue due to chart not found"
            );
            Task updatedTask = taskService.updateTaskStatus(taskId, Task.Status.RUNNING);
            if (!taskService.updateById(updatedTask)) {
                channel.basicAck(deliveryTag, false); // 主动失败，接收消息，不重试
                handleTaskFailure(task.getId(), "Updated content failed");
                return;
            }
            // call AI service
            String input = constructInput(chart.getChartType(), chart.getGoal(), chart.getChartData());

            ChatResponse chatResult = ApiSender.describeChartSync(new ChatRequest(taskId, CHART_SYSTEM_PROMPT, input));
            String result = chatResult.getChoices().get(0).getMessage().getContent();

            // handle AI response
            String[] splits = result.split("\n\n");
            if (splits.length < 1){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
            }
            String genChart = splits[0].trim();
            String genResult = splits[1].trim();
            // update result
            if (!chartService.updateGenChartResult(chart.getId(), genChart, genResult)){
                handleTaskFailure(task.getId(), "Updated chart result failed");
            }
            taskService.updateTaskStatus(taskId, Task.Status.SUCCEED);
            channel.basicAck(deliveryTag, false);
            // TODO 增加主动推送任务成功/失败的机制
        } catch (Exception e){
            channel.basicAck(deliveryTag, false); // 主动失败，接收消息，不重试
            handleTaskFailure(taskId, "Updated chart result failed");
        }

    }

    private void handleTaskFailure(
        @NotNull String taskId,
        @NotNull String errorMsg
    ) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(Task.Status.FAILED.getCode());
        task.setExecMessage(errorMsg);
        if (!taskService.updateById(task)) {
            log.error("Failed to update task status" + taskId + " with " + errorMsg);
        }
    }

    private String constructInput(
        @NotNull String chartType,
        @NotNull String goal,
        @NotNull String chartData
    ) {
        /**
         * Template:
         *  goal:
         *      use bar chart to analyze user's growth of the website
         *  raw data:
         *      date,num_of_user
         *      May/1,10
         *      May/2,20
         *      May/3,30
         */

        StringBuilder input = new StringBuilder();
        input.append("Analysis goal: ");
        input.append("please use ").append(chartType).append(" to ").append(goal).append("\n");
        input.append("Analysis data:").append("\n");
        input.append(chartData).append("\n");
        return new String(input);
    }

}
