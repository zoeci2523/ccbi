package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
public class ChartUpdateRequest  implements Serializable {

    @NotNull
    private Long id;
    @Nullable
    private String name;
    @Nullable
    private String goal;
    @Nullable
    private String chartData;
    @Nullable
    private String chartType;
    @Nullable
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
