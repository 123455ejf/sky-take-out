package com.sky.controller;

import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "B端分类接口")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation("新增分类")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody CategoryDTO dto) {
        categoryService.save(dto);
        return Result.success("新增成功", null);
    }

    @ApiOperation("修改分类")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CategoryDTO dto) {
        categoryService.update(dto);
        return Result.success("修改成功", null);
    }

    @ApiOperation("删除分类")
    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        categoryService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO dto) {
        return Result.success(categoryService.page(dto));
    }

    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(defaultValue = "1") Integer type) {
        return Result.success(categoryService.list(type));
    }
}
