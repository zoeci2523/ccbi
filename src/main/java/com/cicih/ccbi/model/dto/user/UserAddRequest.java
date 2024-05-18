package com.cicih.ccbi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserAddRequest implements Serializable {

    private String username;
    private String account;
    private String avatar;
    private String role;

    private static final long serialVersionUID = 1L;
}