package com.sky.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.exception.BusinessException;
import com.sky.common.result.PageResult;
import com.sky.common.utils.JwtUtil;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.EmployeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String BLACKLIST_PREFIX = "sky:token:blacklist:";

    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO dto) {
        Employee employee = employeeMapper.selectOne(Wrappers.<Employee>lambdaQuery()
                .eq(Employee::getUsername, dto.getUsername()));

        if (employee == null || !passwordEncoder.matches(dto.getPassword(), employee.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!Integer.valueOf(1).equals(employee.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        return EmployeeLoginVO.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .name(employee.getName())
                .token(jwtUtil.createAdminToken(employee.getId()))
                .build();
    }

    @Override
    public void logout(String token) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "1",
                Duration.ofMillis(jwtUtil.getAdminTtl())
        );
    }

    @Override
    public PageResult<EmployeeVO> page(EmployeePageQueryDTO dto) {
        int pageNumber = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        pageSize = Math.min(pageSize, 100);

        Page<Employee> page = employeeMapper.selectPage(
                new Page<>(pageNumber, pageSize),
                Wrappers.<Employee>lambdaQuery()
                        .like(StringUtils.hasText(dto.getName()), Employee::getName, dto.getName())
                        .orderByDesc(Employee::getUpdateTime, Employee::getId)
        );

        List<EmployeeVO> records = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(EmployeeDTO dto) {
        normalize(dto);
        ensureUsernameAvailable(dto.getUsername(), null);

        Employee employee = new Employee();
        fillEmployee(employee, dto);
        employee.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        employee.setStatus(1);
        employeeMapper.insert(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EmployeeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("员工 ID 不能为空");
        }

        Employee existing = employeeMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("员工不存在");
        }

        normalize(dto);
        ensureUsernameAvailable(dto.getUsername(), dto.getId());
        fillEmployee(existing, dto);
        employeeMapper.updateById(existing);
    }

    @Override
    public EmployeeVO getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        return toVO(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("员工状态只能是 0 或 1");
        }
        if (id == null) {
            throw new BusinessException("员工 ID 不能为空");
        }

        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        employee.setStatus(status);
        employeeMapper.updateById(employee);
    }

    private void ensureUsernameAvailable(String username, Long ignoredId) {
        Long count = employeeMapper.selectCount(Wrappers.<Employee>lambdaQuery()
                .eq(Employee::getUsername, username)
                .ne(ignoredId != null, Employee::getId, ignoredId));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
    }

    private void normalize(EmployeeDTO dto) {
        dto.setUsername(dto.getUsername().trim());
        dto.setName(dto.getName().trim());
        dto.setPhone(dto.getPhone().trim());
        dto.setSex(dto.getSex().trim());
        dto.setIdNumber(dto.getIdNumber().trim());
    }

    private void fillEmployee(Employee employee, EmployeeDTO dto) {
        employee.setUsername(dto.getUsername());
        employee.setName(dto.getName());
        employee.setPhone(dto.getPhone());
        employee.setSex(dto.getSex());
        employee.setIdNumber(dto.getIdNumber());
    }

    private EmployeeVO toVO(Employee employee) {
        return EmployeeVO.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .name(employee.getName())
                .phone(employee.getPhone())
                .sex(employee.getSex())
                .idNumber(employee.getIdNumber())
                .status(employee.getStatus())
                .updateTime(employee.getUpdateTime())
                .build();
    }
}
