package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * task
 * @TableName task
 */
@TableName(value ="task")
@Data
public class Task implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * content id
     */
    private Long contentId;

    /**
     * task type
     */
    private Integer type;

    /**
     * task progress: 0-init, 1-wait, 2-running, 3-succeed, 4-failed
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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}