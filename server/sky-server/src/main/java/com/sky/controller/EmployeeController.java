package com.sky.controller;

import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.EmployeeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "B端员工接口")
@RestController
@RequestMapping("/admin/employee")
@RequiredArgsConstructor
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@Valid @RequestBody EmployeeLoginDTO dto) {
        return Result.success("登录成功", employeeService.login(dto));
    }

    @ApiOperation("员工登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("token") String token) {
        employeeService.logout(token);
        return Result.success("登出成功", null);
    }

    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult<EmployeeVO>> page(EmployeePageQueryDTO dto) {
        return Result.success(employeeService.page(dto));
    }

    @ApiOperation("新增员工")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody EmployeeDTO dto) {
        employeeService.save(dto);
        return Result.success("新增成功", null);
    }

    @ApiOperation("修改员工")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EmployeeDTO dto) {
        employeeService.update(dto);
        return Result.success("修改成功", null);
    }

    @ApiOperation("根据ID查询员工")
    @GetMapping("/{id}")
    public Result<EmployeeVO> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    @ApiOperation("启用或禁用员工")
    @PutMapping("/status/{status}")
    public Result<Void> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        employeeService.startOrStop(status, id);
        return Result.success("状态修改成功", null);
    }
}
