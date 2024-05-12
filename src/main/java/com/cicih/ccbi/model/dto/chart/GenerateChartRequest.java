package com.cicih.ccbi.model.dto.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class GenerateChartRequest {
    @NotNull
    private String name;
    @NotNull
    private String goal;
    @NotNull
    private String chartData;
    @NotNull
    private String chartType;
}
