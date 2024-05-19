package com.cicih.ccbi.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * login user view object (desensitisation)
 *
 **/
@Data
public class LoginUserVO implements Serializable {

    private String id;
    private String username;
    private String avatar;
    private String profile;
    private Integer role;
    private Date createTime;
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}