package com.cicih.ccbi.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.mapper.UserMapper;
import com.cicih.ccbi.model.dto.task.TaskQueryRequest;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.service.TaskService;
import com.cicih.ccbi.mapper.TaskMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    @Resource
    UserMapper userMapper;
    @Resource
    TaskMapper taskMapper;

    @Override
    @NotNull
    public String iniTask(@NotNull String userId, @NotNull String contentId, @NotNull Task.Type type) {
        // TODO 尝试用更保险的方法检验用户
        ThrowUtils.throwIf(userMapper.selectById(userId) == null, ErrorCode.NOT_FOUND_ERROR,
                "Failed to initialize task due to user not exist");
        String taskId = "task-" + UUID.fastUUID();
        Task task = new Task(userId, contentId, type);
        task.setId(taskId);
        task.setStatus(Task.Status.INIT.getCode());
        if (!save(task)){
            throw new BusinessException(ErrorCode.CREATE_ERROR, "Initialize task failed");
        }
        return getById(taskId).getId();
    }

    @Override
    @NotNull
    public Task getTaskByQueryParams(@NotNull TaskQueryRequest queryRequest){
        QueryWrapper<Task> queryWrapper = taskMapper.getQueryWrapper(queryRequest);
        Task task = getOne(queryWrapper);
        ThrowUtils.throwIf(task == null, ErrorCode.NOT_FOUND_ERROR);
        return task;
    }

    @Override
    @NotNull
    public Task updateTaskStatus(
        @NotNull String taskId,
        @NotNull Task.Status status
    ){
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(status.getCode());
        if (updateById(task)){
            return taskMapper.selectById(taskId);
        }else {
            throw new BusinessException(ErrorCode.UPDATE_ERROR, "Failed to update task status");
        }
    }

}




