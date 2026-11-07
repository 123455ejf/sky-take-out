package com.sky.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeVO {

    private Long id;
    private String username;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private Integer status;
    private LocalDateTime updateTime;
}
