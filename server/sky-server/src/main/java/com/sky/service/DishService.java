package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void save(DishDTO dto);

    void update(DishDTO dto);

    void deleteByIds(List<Long> ids);

    PageResult<DishVO> page(DishPageQueryDTO dto);

    DishVO getById(Long id);

    void startOrStop(Integer status, Long id);

    List<DishVO> listForUser(Long categoryId);
}
