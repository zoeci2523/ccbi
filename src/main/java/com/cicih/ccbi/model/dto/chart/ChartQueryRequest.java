package com.cicih.ccbi.model.dto.chart;

import com.cicih.ccbi.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ChartQueryRequest extends PageRequest implements Serializable {

    @Nullable
    private String userId;
    @Nullable
    private String title;
    @Nullable
    private String goal;
    @Nullable
    private String chartData;
    @Nullable
    private String chartType;
    @NotNull
    private Boolean publicOnly;

    private static final long serialVersionUID = 1L;
}
