package com.sky.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.exception.BusinessException;
import com.sky.common.result.PageResult;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dto) {
        validate(dto, null);

        Dish dish = new Dish();
        dish.setName(dto.getName().trim());
        dish.setCategoryId(dto.getCategoryId());
        dish.setPrice(dto.getPrice());
        dish.setImage(dto.getImage());
        dish.setDescription(dto.getDescription());
        dish.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        dishMapper.insert(dish);

        saveFlavors(dish.getId(), dto.getFlavors());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("菜品 ID 不能为空");
        }

        Dish existing = dishMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("菜品不存在");
        }

        validate(dto, dto.getId());

        existing.setName(dto.getName().trim());
        existing.setCategoryId(dto.getCategoryId());
        existing.setPrice(dto.getPrice());
        existing.setImage(dto.getImage());
        existing.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        dishMapper.updateById(existing);

        // 口味整体覆盖：先删后插，否则越改越多
        dishFlavorMapper.delete(Wrappers.<DishFlavor>lambdaQuery()
                .eq(DishFlavor::getDishId, dto.getId()));
        saveFlavors(dto.getId(), dto.getFlavors());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的菜品");
        }

        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish == null) {
                continue;
            }
            if (Integer.valueOf(1).equals(dish.getStatus())) {
                throw new BusinessException("菜品【" + dish.getName() + "】正在起售，请先停售");
            }
            Long reference = dishMapper.countSetmealDishByDishId(id);
            if (reference != null && reference > 0) {
                throw new BusinessException("菜品【" + dish.getName() + "】已被套餐关联，不能删除");
            }
        }

        dishMapper.deleteBatchIds(ids);
        dishFlavorMapper.delete(Wrappers.<DishFlavor>lambdaQuery()
                .in(DishFlavor::getDishId, ids));
    }

    @Override
    public PageResult<DishVO> page(DishPageQueryDTO dto) {
        int pageNumber = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        pageSize = Math.min(pageSize, 100);

        Page<Dish> page = dishMapper.selectPage(
                new Page<>(pageNumber, pageSize),
                Wrappers.<Dish>lambdaQuery()
                        .like(StringUtils.hasText(dto.getName()), Dish::getName, dto.getName())
                        .eq(dto.getCategoryId() != null, Dish::getCategoryId, dto.getCategoryId())
                        .eq(dto.getStatus() != null, Dish::getStatus, dto.getStatus())
                        .orderByDesc(Dish::getUpdateTime)
                        .orderByDesc(Dish::getId)
        );

        Map<Long, String> categoryNames = categoryNames(page.getRecords().stream()
                .map(Dish::getCategoryId)
                .collect(Collectors.toSet()));

        List<DishVO> records = page.getRecords().stream()
                .map(dish -> toVO(dish, categoryNames.get(dish.getCategoryId())))
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), records);
    }

    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        DishVO vo = toVO(dish, null);
        vo.setFlavors(listFlavors(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("菜品状态只能是 0 或 1");
        }
        if (id == null) {
            throw new BusinessException("菜品 ID 不能为空");
        }

        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }

        if (status == 0) {
            Long reference = dishMapper.countOnSaleSetmealByDishId(id);
            if (reference != null && reference > 0) {
                throw new BusinessException("该菜品已被起售中的套餐关联，不能停售");
            }
        }

        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    @Override
    public List<DishVO> listForUser(Long categoryId) {
        List<Dish> dishes = dishMapper.selectList(Wrappers.<Dish>lambdaQuery()
                .eq(Dish::getStatus, 1)
                .eq(categoryId != null, Dish::getCategoryId, categoryId)
                .orderByDesc(Dish::getUpdateTime)
                .orderByAsc(Dish::getId));

        Map<Long, String> categoryNames = categoryNames(dishes.stream()
                .map(Dish::getCategoryId)
                .collect(Collectors.toSet()));

        List<DishVO> result = new ArrayList<>(dishes.size());
        for (Dish dish : dishes) {
            DishVO vo = toVO(dish, categoryNames.get(dish.getCategoryId()));
            vo.setFlavors(listFlavors(dish.getId()));
            result.add(vo);
        }
        return result;
    }

    private void validate(DishDTO dto, Long ignoredId) {
        if (categoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException("菜品分类不存在");
        }

        Long count = dishMapper.selectCount(Wrappers.<Dish>lambdaQuery()
                .eq(Dish::getName, dto.getName().trim())
                .eq(Dish::getCategoryId, dto.getCategoryId())
                .ne(ignoredId != null, Dish::getId, ignoredId));
        if (count != null && count > 0) {
            throw new BusinessException("该分类下已存在同名菜品");
        }
    }

    private void saveFlavors(Long dishId, List<DishFlavor> flavors) {
        if (flavors == null || flavors.isEmpty()) {
            return;
        }
        for (DishFlavor flavor : flavors) {
            if (flavor == null
                    || !StringUtils.hasText(flavor.getName())
                    || !StringUtils.hasText(flavor.getValue())) {
                continue;
            }
            DishFlavor entity = new DishFlavor();
            entity.setDishId(dishId);
            entity.setName(flavor.getName().trim());
            entity.setValue(flavor.getValue().trim());
            dishFlavorMapper.insert(entity);
        }
    }

    private List<DishFlavor> listFlavors(Long dishId) {
        return dishFlavorMapper.selectList(Wrappers.<DishFlavor>lambdaQuery()
                .eq(DishFlavor::getDishId, dishId)
                .orderByAsc(DishFlavor::getId));
    }

    private Map<Long, String> categoryNames(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
        Map<Long, String> map = new HashMap<>(categories.size());
        for (Category category : categories) {
            map.put(category.getId(), category.getName());
        }
        return map;
    }

    private DishVO toVO(Dish dish, String categoryName) {
        return DishVO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .categoryId(dish.getCategoryId())
                .categoryName(categoryName)
                .price(dish.getPrice())
                .image(dish.getImage())
                .description(dish.getDescription())
                .status(dish.getStatus())
                .updateTime(dish.getUpdateTime())
                .build();
    }
}
