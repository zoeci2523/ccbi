package com.cicih.ccbi.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChartVO implements Serializable {
    private String id;
    private String taskId;
    private String status;
    private String userId;
    private String goal;
    private String title;
    private String chartData;
    private String chartType;
    private String generateChart;
    private String generateResult;
    private Date createdTime;
    private Date updatedTime;
    private Integer isPublic;
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}