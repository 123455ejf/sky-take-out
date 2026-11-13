package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;

import java.util.List;

public interface CategoryService {

    void save(CategoryDTO dto);

    void update(CategoryDTO dto);

    void deleteById(Long id);

    PageResult<Category> page(CategoryPageQueryDTO dto);

    List<Category> list(Integer type);

    List<Category> listForUser(Integer type);
}
