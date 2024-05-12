package com.cicih.ccbi.model.dto.chart;

import com.cicih.ccbi.common.PageRequest;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    @NotNull
    private Long id;
    @Nullable
    private Long userId;
    @Nullable
    private String name;
    @Nullable
    private String goal;
    @Nullable
    private String chartData;
    @Nullable
    private String chartType;

    private static final long serialVersionUID = 1L;
}
