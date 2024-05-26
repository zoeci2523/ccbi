package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChartAddRequest implements Serializable {

    private String title;
    private String goal;
    private String chartData;
    private String chartType;

    private static final long serialVersionUID = 1L;

    public boolean validParams(){
        return !StringUtils.isAnyBlank(title, goal, chartData, chartType);
    }
}
