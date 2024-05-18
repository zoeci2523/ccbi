package com.cicih.ccbi.annotation;

import com.cicih.ccbi.model.entity.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Authentication validation
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * User role that must have
     *
     * @return
     */
    User.Role mustRole() default User.Role.USER;

}

