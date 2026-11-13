package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("select count(*) from dish where category_id = #{categoryId}")
    Long countDishByCategoryId(@Param("categoryId") Long categoryId);

    @Select("select count(*) from setmeal where category_id = #{categoryId}")
    Long countSetmealByCategoryId(@Param("categoryId") Long categoryId);
}
