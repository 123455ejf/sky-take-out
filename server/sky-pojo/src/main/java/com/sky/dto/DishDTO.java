package com.sky.dto;

import com.sky.entity.DishFlavor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class DishDTO {

    private Long id;

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 64, message = "菜品名称不能超过 64 个字符")
    private String name;

    @NotNull(message = "菜品分类不能为空")
    private Long categoryId;

    /** 单位：分 */
    @NotNull(message = "菜品价格不能为空")
    @Min(value = 0, message = "菜品价格不能为负数")
    private Integer price;

    private String image;
    private String description;
    private Integer status;

    private List<DishFlavor> flavors;
}
