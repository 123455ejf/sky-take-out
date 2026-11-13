package com.sky.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.exception.BusinessException;
import com.sky.common.result.PageResult;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CategoryDTO dto) {
        String name = dto.getName().trim();
        ensureNameAvailable(name, dto.getType(), null);

        Category category = new Category();
        category.setType(dto.getType());
        category.setName(name);
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        category.setStatus(1);
        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类 ID 不能为空");
        }

        Category existing = categoryMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        String name = dto.getName().trim();
        ensureNameAvailable(name, dto.getType(), dto.getId());

        existing.setType(dto.getType());
        existing.setName(name);
        if (dto.getSort() != null) {
            existing.setSort(dto.getSort());
        }
        categoryMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException("分类 ID 不能为空");
        }
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }

        Long dishCount = categoryMapper.countDishByCategoryId(id);
        if (dishCount != null && dishCount > 0) {
            throw new BusinessException("该分类下还有菜品，不能删除");
        }

        Long setmealCount = categoryMapper.countSetmealByCategoryId(id);
        if (setmealCount != null && setmealCount > 0) {
            throw new BusinessException("该分类下还有套餐，不能删除");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public PageResult<Category> page(CategoryPageQueryDTO dto) {
        int pageNumber = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        pageSize = Math.min(pageSize, 100);

        Page<Category> page = categoryMapper.selectPage(
                new Page<>(pageNumber, pageSize),
                Wrappers.<Category>lambdaQuery()
                        .like(StringUtils.hasText(dto.getName()), Category::getName, dto.getName())
                        .eq(dto.getType() != null, Category::getType, dto.getType())
                        .orderByAsc(Category::getSort)
                        .orderByAsc(Category::getId)
        );

        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                .eq(type != null, Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public List<Category> listForUser(Integer type) {
        return categoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                .eq(Category::getType, type == null ? 1 : type)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    private void ensureNameAvailable(String name, Integer type, Long ignoredId) {
        Long count = categoryMapper.selectCount(Wrappers.<Category>lambdaQuery()
                .eq(Category::getName, name)
                .eq(Category::getType, type)
                .ne(ignoredId != null, Category::getId, ignoredId));
        if (count != null && count > 0) {
            throw new BusinessException("同类型下分类名称已存在");
        }
    }
}
