package com.cicih.ccbi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.service.TaskService;
import com.cicih.ccbi.mapper.TaskMapper;
import org.springframework.stereotype.Service;

/**
* @author fengxiaoha
* @description 针对表【task(task)】的数据库操作Service实现
* @createDate 2024-05-05 03:36:33
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

}




