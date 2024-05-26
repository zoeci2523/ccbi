package com.cicih.ccbi.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@NoArgsConstructor
public class MQTaskResponse {
    @NotNull
    private String taskId;
    private String generateChart;
    private String generateResult;
}
