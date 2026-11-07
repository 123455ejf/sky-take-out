package com.sky.runner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Long count = employeeMapper.selectCount(Wrappers.<Employee>lambdaQuery()
                .eq(Employee::getUsername, "admin"));
        if (count != null && count > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Employee admin = new Employee();
        admin.setUsername("admin");
        admin.setName("管理员");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setPhone("13800138000");
        admin.setSex("男");
        admin.setIdNumber("110101199001011234");
        admin.setStatus(1);
        admin.setCreateTime(now);
        admin.setUpdateTime(now);
        employeeMapper.insert(admin);
        log.info("已创建默认管理员账号，用户名 admin");
    }
}
