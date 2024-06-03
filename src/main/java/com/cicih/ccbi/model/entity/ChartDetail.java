package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@TableName(value ="chart_detail")
@Data
public class ChartDetail implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * task id
     */
    private String taskId;

    /**
     * user id
     */
    private String userId;

    /**
     * analysis goal of the chart
     */
    private String goal;

    /**
     * chart title
     */
    private String title;

    /**
     * chart csv data (transferred)
     */
    private String chartData;

    /**
     * chart type
     */
    private String chartType;

    /**
     * generated chart data
     */
    private String generateChart;

    /**
     * generated analysis conclusion
     */
    private String generateResult;

    /**
     * created time
     */
    private Date createTime;

    /**
     * updated time
     */
    private Date updateTime;

    /**
     * be public or not, 0-public, 1-private
     */
    private Integer isPublic;

    /**
     * delete status, 0-false, 1-true
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}