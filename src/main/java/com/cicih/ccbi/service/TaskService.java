package com.cicih.ccbi.service;

import com.cicih.ccbi.model.dto.task.TaskQueryRequest;
import com.cicih.ccbi.model.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;

public interface TaskService extends IService<Task> {

    @NotNull
    String iniTask(@NotNull String userId, @NotNull String contentId, @NotNull Task.Type type);

    @NotNull
    Task getTaskByQueryParams(@NotNull TaskQueryRequest queryRequest);

    @NotNull
    Task updateTaskStatus(
        @NotNull String taskId,
        @NotNull Task.Status status
    );
}
