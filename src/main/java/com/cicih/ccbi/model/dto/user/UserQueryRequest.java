package com.cicih.ccbi.model.dto.user;

import com.cicih.ccbi.common.PageRequest;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    //    private String id;
    private String unionId;
    private String mpOpenId;
    private String username;
    private String profile;
    private String userRole;

    private static final long serialVersionUID = 1L;

    public boolean validParams() {
        return !StringUtils.isAnyBlank(unionId, mpOpenId, username, profile, userRole);
    }
}