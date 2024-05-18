package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@TableName(value = "task")
@Data
@NoArgsConstructor
public class Task implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * user id
     */
    private String userId;

    /**
     * content id
     */
    private String contentId;

    /**
     * task type
     */
    private Integer type;

    /**
     * task status: 0-init, 1-wait, 2-running, 3-succeed, 4-failed
     */
    private Integer status;

    /**
     * created time
     */
    private Date createTime;

    /**
     * updated time
     */
    private Date updateTime;

    /**
     * delete status
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Task(@NotNull String userId, @NotNull String contentId, @NotNull Type type) {
        this.userId = userId;
        this.contentId = contentId;
        this.type = type.getCode();
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        // Task type: 0-chart, 1-chat ...
        CHART(0, "chart"),
        CHAT(1, "chat");

        @NotNull
        private final Integer code;
        @NotNull
        private final String message;
    }

    @Getter
    @AllArgsConstructor
    public enum Status {
        // Task status: 0-init, 1-wait, 2-running, 3-succeed, 4-failed
        INIT(0, "initialization"),
        WAITING(1, "waiting"),
        RUNNING(2, "running"),
        SUCCEED(3, "succeed"),
        FAILED(4, "failed");

        @NotNull
        private final Integer code;
        @NotNull
        private final String message;
    }
}