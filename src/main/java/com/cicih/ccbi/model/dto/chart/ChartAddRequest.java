package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {
    @NotNull
    private String name;
    @NotNull
    private String goal;
    @NotNull
    private String chartData;
    @NotNull
    private String chartType; // TODO 改成 enum 吗？

    private static final long serialVersionUID = 1L;
}
