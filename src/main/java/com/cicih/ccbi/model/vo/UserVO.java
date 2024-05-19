package com.cicih.ccbi.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User view object (desensitisation)
 *
 */
@Data
public class UserVO implements Serializable {

    private String id;
    private String username;
    private String avatar;
    private String profile;
    private Integer role;
    private Date createTime;

    private static final long serialVersionUID = 1L;
}