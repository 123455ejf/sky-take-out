package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DishMapper extends BaseMapper<Dish> {

    @Select("select count(*) from setmeal_dish where dish_id = #{dishId}")
    Long countSetmealDishByDishId(@Param("dishId") Long dishId);

    @Select("select count(*) from setmeal_dish sd "
            + "inner join setmeal s on sd.setmeal_id = s.id "
            + "where sd.dish_id = #{dishId} and s.status = 1")
    Long countOnSaleSetmealByDishId(@Param("dishId") Long dishId);
}
