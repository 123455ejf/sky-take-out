package com.sky.controller;

import com.sky.common.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "C端菜品接口")
@RestController
@RequestMapping("/user/dish")
@RequiredArgsConstructor
public class UserDishController {

    private final DishService dishService;

    @ApiOperation("根据分类ID查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        return Result.success(dishService.listForUser(categoryId));
    }
}
