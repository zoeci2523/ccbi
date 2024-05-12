package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * chart_detail
 * @TableName chart_detail
 */
@TableName(value ="chart_detail")
@Data
public class ChartDetail implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * task id
     */
    private Long taskId;

    /**
     * user id
     */
    private Long userId;

    /**
     * analysis goal of the chart
     */
    private String goal;

    /**
     * chart name
     */
    private String name;

    /**
     * chart raw data
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
     * generated result
     */
    private String generateResult;

    /**
     * execute message
     */
    private String execMessage;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}