package com.cicih.ccbi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cicih.ccbi.annotation.AuthCheck;
import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.common.ResultUtils;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.mapper.UserMapper;
import com.cicih.ccbi.model.dto.user.UserAddRequest;
import com.cicih.ccbi.model.dto.user.UserQueryRequest;
import com.cicih.ccbi.model.dto.user.UserUpdateMyRequest;
import com.cicih.ccbi.model.dto.user.UserUpdateRequest;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.model.vo.LoginUserVO;
import com.cicih.ccbi.model.vo.UserVO;
import com.cicih.ccbi.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    @PostMapping("/register")
    public BaseResponse<String> userRegister(@NotNull String account, @NotNull String password, @NotNull String checkPassword) {
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            return null;
        }
        return ResultUtils.success(userService.userRegister(account, password, checkPassword));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@NotNull String account, @NotNull String password, @NotNull HttpServletRequest request) {
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(account, password, request);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<String> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<Boolean> deleteUser(@NotNull String userId) {
        return ResultUtils.success(userService.removeById(userId));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * List user by page (admin only)
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userMapper.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * List user info by page
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // restrict web crawler
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userMapper.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    /**
     * Update personal infomation
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
