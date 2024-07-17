package com.cicih.ccbi.constant;

import java.util.Arrays;
import java.util.List;

public interface FileConstant {

    /**
     * COS 访问地址
     * todo 需替换配置
     */
    String COS_HOST = "";
    long UPLOAD_FILE_SIZE_LIMITATION = 1024 * 1024L; // 1 MB
    List<String> VALID_FILE_SUFFIX_LIST = List.of(".csv", ".xlsx", ".xls");
}
