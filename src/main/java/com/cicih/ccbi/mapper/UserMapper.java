package com.cicih.ccbi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.constant.CommonConstant;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.model.dto.user.UserQueryRequest;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public interface UserMapper extends BaseMapper<User> {

    default QueryWrapper<User> getQueryWrapper(@NotNull UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Empty params");
        }
        String id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUsername();
        String userProfile = userQueryRequest.getProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "profile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "username", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




