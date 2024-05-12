package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * user
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * user account
     */
    private String account;

    /**
     * user account password
     */
    private String password;

    /**
     * wechat open platform id
     */
    private String unionId;

    /**
     * wechat mp(media platform) openId
     */
    private String mpopenId;

    /**
     * username, can be duplicated
     */
    private String username;

    /**
     * user avatar, url
     */
    private String avatar;

    /**
     * user profile
     */
    private String profile;

    /**
     * user role：user/admin/ban, default user
     */
    private String role;

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