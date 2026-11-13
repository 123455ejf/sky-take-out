package com.sky.vo;

import com.sky.entity.DishFlavor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DishVO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Integer price;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime updateTime;
    private List<DishFlavor> flavors;
}
