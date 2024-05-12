package com.cicih.ccbi.common;

import com.cicih.ccbi.constant.CommonConstant;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class PageRequest {

    /**
     * current page
     */
    @NotNull
    private long current = 1;

    /**
     * page size
     */
    @NotNull
    private long pageSize = 10;

    /**
     * sorting field
     */
    @Nullable
    private String sortField;

    /**
     * sorting order
     */
    @NotNull
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
