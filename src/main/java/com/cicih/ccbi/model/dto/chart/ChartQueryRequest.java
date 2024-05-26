package com.cicih.ccbi.model.dto.chart;

import com.cicih.ccbi.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ChartQueryRequest extends PageRequest implements Serializable {

    private String userId;
    private String title;
    private String goal;
    private String chartData;
    private String chartType;
    private Boolean publicOnly;

    private static final long serialVersionUID = 1L;

    public boolean validParams(){
        return publicOnly != null && !StringUtils.isAnyBlank(userId, title, goal, chartData, chartType);
    }
}
