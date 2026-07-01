package com.agriuav.backend.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSprayApplicationRequest(
        @NotBlank(message = "申请人姓名不能为空")
        @Size(max = 100, message = "申请人姓名不能超过100个字符")
        String applicantName,

        @NotBlank(message = "村庄编码不能为空")
        @Size(max = 64, message = "村庄编码不能超过64个字符")
        String villageCode,

        @NotBlank(message = "地块编码不能为空")
        @Size(max = 64, message = "地块编码不能超过64个字符")
        String parcelCode,

        @NotBlank(message = "作物类型不能为空")
        @Size(max = 64, message = "作物类型不能超过64个字符")
        String cropType,

        @NotNull(message = "作业面积不能为空")
        @DecimalMin(value = "0.01", message = "作业面积必须大于0")
        @Digits(integer = 10, fraction = 2, message = "作业面积最多保留2位小数")
        BigDecimal areaMu,

        @NotBlank(message = "目标病虫害不能为空")
        @Size(max = 100, message = "目标病虫害不能超过100个字符")
        String targetPest,

        @NotNull(message = "计划作业日期不能为空")
        @FutureOrPresent(message = "计划作业日期不能早于今天")
        LocalDate plannedDate
) {
}
