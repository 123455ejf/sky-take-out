package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CategoryDTO {

    private Long id;

    @NotNull(message = "分类类型不能为空")
    private Integer type;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 32, message = "分类名称不能超过 32 个字符")
    private String name;

    private Integer sort;
}
