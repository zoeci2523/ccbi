package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {
    @NotNull
    private String title;
    @NotNull
    private String goal;
    @NotNull
    private String chartData;
    @NotNull
    private String chartType;

    private static final long serialVersionUID = 1L;

    public boolean validParams(){
        return !StringUtils.isAnyBlank(title, goal, chartData, chartType);
    }
}
