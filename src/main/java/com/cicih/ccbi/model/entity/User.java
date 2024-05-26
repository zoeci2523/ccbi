package com.cicih.ccbi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import com.cicih.ccbi.model.enums.FileUploadBizEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

@TableName(value = "user")
@Data
public class User implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * account name, be unique
     */
    private String account;
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
     * user role：0-user, 1-admin, 2-ban, default 0-user
     */
    private Integer role;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Getter
    @AllArgsConstructor
    public enum Role {
        USER(0, "user"),
        ADMIN(1, "admin"),
        BAN(2, "ban");

        @NotNull
        private final Integer code;
        @NotNull
        private final String message;
    }

    @NotNull
    public Role getRoleEnumByValue(@NotNull Integer value) {
        return Arrays.stream(Role.values()).
                filter(role -> role.getCode().equals(value)).
                findFirst().
                orElseThrow(() -> new RuntimeException("Unknown role type: " + value));
    }

    public Role getRoleEnumValue(){
        return getRoleEnumByValue(this.role);
    }
}