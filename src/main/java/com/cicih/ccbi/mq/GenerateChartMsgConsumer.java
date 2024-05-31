package com.cicih.ccbi.mq;

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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class GenerateChartMsgConsumer {
    @Resource
    private TaskService taskService;
    @Resource
    private ChartDetailService chartService;

    // TODO 补充生成图的系统设置
    private static final String CHART_SYSTEM_PROMPT = "";

    @SneakyThrows
    @RabbitListener(queues = {MqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    private void receiveMessage(
        String taskId,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) {
        log.info("receive message from task: {}", taskId);
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
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setStatus(Task.Status.RUNNING.getCode());
        if (!taskService.updateById(updatedTask)) {
            channel.basicNack(deliveryTag, false, false);
            handleTaskFailure(task.getId(), "Updated content failed");
            return;
        }
        // call AI service
        String input = constructInput(chart.getChartType(), chart.getGoal(), chart.getChartData());
        String result = Optional
            .of(Optional
                .ofNullable(ApiSender.describeChartSync(new ChatRequest(CHART_SYSTEM_PROMPT, input)))
                .orElseThrow(() -> new BusinessException(ErrorCode.CREATE_ERROR)))
            .get()
            .getResult();
        // TODO 调用 e-chart
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
         *  // TODO 补充 让它生成 e-chart 格式的提示词
         */

        StringBuilder input = new StringBuilder();
        input.append("goal:").append("\n");
        input.append("please use ").append(chartType).append(" to ").append(goal).append("\n");
        input.append("input data:").append("\n");
        input.append(chartData).append("\n");
        return new String(input);
    }


}
