package com.cicih.ccbi.model.dto.task;

import com.cicih.ccbi.common.PageRequest;
import com.cicih.ccbi.model.entity.Task;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueryRequest extends PageRequest implements Serializable {

    private String userId;
    private String contentId;
    private Task.Type type;
    private Task.Status status;

    public boolean validParams(){
        return !ObjectUtils.allNull(userId, contentId, type, status);
    }

}
