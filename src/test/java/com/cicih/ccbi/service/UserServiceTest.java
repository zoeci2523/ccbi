package com.cicih.ccbi.service;

import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

//    @Test
//    void userRegister() {
//        String userAccount = "yoyo";
//        String userPassword = "";
//        String checkPassword = "123456";
//        try {
//            String result = userService.userRegister(userAccount, userPassword, checkPassword);
//            Assertions.assertEquals(null, result);
//            userAccount = "yy";
//            result = userService.userRegister(userAccount, userPassword, checkPassword);
//            Assertions.assertEquals(null, result);
//        } catch (Exception e) {
//
//        }
//    }
}
