package com.sky.service.impl;

import com.sky.common.exception.BusinessException;
import com.sky.common.utils.JwtUtil;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import com.sky.vo.EmployeeLoginVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void shouldLoginWithValidCredentials() {
        Employee employee = employee(1L, "admin", "管理员", 1);
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        when(employeeMapper.selectOne(any())).thenReturn(employee);
        when(passwordEncoder.matches("123456", employee.getPassword())).thenReturn(true);
        when(jwtUtil.createAdminToken(1L)).thenReturn("test-token");

        EmployeeLoginVO result = employeeService.login(dto);

        assertEquals(1L, result.getId());
        assertEquals("管理员", result.getName());
        assertEquals("test-token", result.getToken());
    }

    @Test
    void shouldRejectWrongPassword() {
        Employee employee = employee(1L, "admin", "管理员", 1);
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("bad-password");

        when(employeeMapper.selectOne(any())).thenReturn(employee);
        when(passwordEncoder.matches("bad-password", employee.getPassword())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> employeeService.login(dto));
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void shouldRejectDisabledEmployee() {
        Employee employee = employee(1L, "admin", "管理员", 0);
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        when(employeeMapper.selectOne(any())).thenReturn(employee);
        when(passwordEncoder.matches("123456", employee.getPassword())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> employeeService.login(dto));
        assertEquals("账号已被禁用，请联系管理员", exception.getMessage());
    }

    @Test
    void shouldCreateEmployeeWithDefaultPassword() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUsername("zhangsan");
        dto.setName("张三");
        dto.setPhone("13800138001");
        dto.setSex("男");
        dto.setIdNumber("110101199001011234");

        when(employeeMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");

        employeeService.save(dto);

        org.mockito.Mockito.verify(employeeMapper).insert(any(Employee.class));
    }

    private Employee employee(Long id, String username, String name, Integer status) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setUsername(username);
        employee.setName(name);
        employee.setPassword("hashed-password");
        employee.setStatus(status);
        return employee;
    }
}
