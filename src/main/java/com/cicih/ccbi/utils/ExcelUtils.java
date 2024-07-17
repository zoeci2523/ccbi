package com.cicih.ccbi.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelUtils {
    public static String fileToString(
        @NotNull MultipartFile multipartFile,
        @NotNull String extension
    ) {
        ExcelTypeEnum type =
            Arrays.stream(ExcelTypeEnum.values()).filter(e -> e.getValue().equals(extension)).findFirst().orElseThrow(
                () -> new BusinessException(ErrorCode.PARAMS_ERROR, "Unsupported file type"));
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream()).excelType(type).sheet().headRowNumber(0).doReadSync();
        } catch (IOException e) {
            log.error("Error in handling excel file", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // turn int csv
        StringBuilder sb = new StringBuilder();
        // read the head
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList =
            headerMap.values().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        sb.append(StringUtils.join(headerList, ",")).append("\n");
        // read the records of data
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList =
                dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            sb.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return sb.toString();
    }
}
