package com.sky.dto;

import lombok.Data;

@Data
public class DishPageQueryDTO {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String name;
    private Long categoryId;
    private Integer status;
}
