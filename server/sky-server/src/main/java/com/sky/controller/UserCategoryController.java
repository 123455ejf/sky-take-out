package com.sky.controller;

import com.sky.common.result.Result;
import com.sky.entity.Category;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "C端分类接口")
@RestController
@RequestMapping("/user/category")
@RequiredArgsConstructor
public class UserCategoryController {

    private final CategoryService categoryService;

    @ApiOperation("查询分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(defaultValue = "1") Integer type) {
        return Result.success(categoryService.listForUser(type));
    }
}
