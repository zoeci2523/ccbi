package com.cicih.ccbi.aop;

import com.cicih.ccbi.annotation.AuthCheck;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.model.enums.UserRoleEnum;
import com.cicih.ccbi.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限校验 AOP
 *
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * Authentication interceptor
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        User.Role mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // check if current user has authentication
        User loginUser = userService.getLoginUser(request);
        User.Role userRole = loginUser.getRoleEnumValue();

        if (User.Role.BAN.equals(userRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (User.Role.ADMIN.equals(mustRole)) {
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        return joinPoint.proceed();
    }
}

