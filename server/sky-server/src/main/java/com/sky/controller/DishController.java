package com.sky.controller;

import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "B端菜品接口")
@RestController
@RequestMapping("/admin/dish")
@RequiredArgsConstructor
@Validated
public class DishController {

    private final DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody DishDTO dto) {
        dishService.save(dto);
        return Result.success("新增成功", null);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody DishDTO dto) {
        dishService.update(dto);
        return Result.success("修改成功", null);
    }

    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result<Void> delete(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
        dishService.deleteByIds(idList);
        return Result.success("删除成功", null);
    }

    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dto) {
        return Result.success(dishService.page(dto));
    }

    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @ApiOperation("菜品起售停售")
    @PutMapping("/status/{status}")
    public Result<Void> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        dishService.startOrStop(status, id);
        return Result.success("状态修改成功", null);
    }
}
