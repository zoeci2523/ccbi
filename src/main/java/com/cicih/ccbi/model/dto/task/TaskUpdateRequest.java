package com.cicih.ccbi.model.dto.task;

import com.cicih.ccbi.model.entity.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TaskUpdateRequest implements Serializable {

    private String id;
    private Task.Status status;
    private String execMessage;

    public boolean validParams(){
        return id != null && status != null;
    }
}
