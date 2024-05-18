package com.cicih.ccbi.service;

import com.cicih.ccbi.model.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;

/**
* @author fengxiaoha
* @description 针对表【task(task)】的数据库操作Service
* @createDate 2024-05-05 03:36:33
*/
public interface TaskService extends IService<Task> {

    @NotNull
    String iniTask(@NotNull String userId, @NotNull String contentId, @NotNull Task.Type type);
}
