package com.cicih.ccbi.model.dto.chart;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChartEditRequest implements Serializable {
    @NotNull
    private String id;
    @Nullable
    private String title;
    @Nullable
    private String goal;
    @Nullable
    private String chartData;
    @Nullable
    private String chartType;

    private static final long serialVersionUID = 1L;
}
