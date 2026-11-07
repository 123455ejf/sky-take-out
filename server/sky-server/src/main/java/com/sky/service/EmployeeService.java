package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.EmployeeVO;

public interface EmployeeService {

    EmployeeLoginVO login(EmployeeLoginDTO dto);

    void logout(String token);

    PageResult<EmployeeVO> page(EmployeePageQueryDTO dto);

    void save(EmployeeDTO dto);

    void update(EmployeeDTO dto);

    EmployeeVO getById(Long id);

    void startOrStop(Integer status, Long id);
}
