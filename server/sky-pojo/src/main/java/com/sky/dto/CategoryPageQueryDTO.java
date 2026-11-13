package com.sky.dto;

import lombok.Data;

@Data
public class CategoryPageQueryDTO {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String name;
    private Integer type;
}
