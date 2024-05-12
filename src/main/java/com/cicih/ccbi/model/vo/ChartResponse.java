package com.cicih.ccbi.model.vo;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ChartResponse {
    @NotNull
    private Long chartId;
    @Nullable
    private String generateChart;
    @Nullable
    private String generateResult;
}
