package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChartUpdateRequest implements Serializable {

    private String id;
    private String title;
    private String goal;
    private String chartData;
    private String chartType;
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    public boolean validParams() {
        return id != null && !StringUtils.isAnyBlank(title, goal, chartData, chartType);
    }
}
