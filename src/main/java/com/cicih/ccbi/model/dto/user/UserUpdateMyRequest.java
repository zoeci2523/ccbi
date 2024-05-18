package com.cicih.ccbi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserUpdateMyRequest implements Serializable {

    private String username;
    private String avatar;
    private String profile;

    private static final long serialVersionUID = 1L;
}