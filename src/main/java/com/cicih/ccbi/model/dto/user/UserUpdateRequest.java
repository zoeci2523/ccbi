package com.cicih.ccbi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserUpdateRequest implements Serializable {
    private String id;
    private String username;
    private String avatar;
    private String profile;
    private String userRole;

    private static final long serialVersionUID = 1L;
}