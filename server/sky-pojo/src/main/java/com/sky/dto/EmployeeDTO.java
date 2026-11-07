package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度为 3 到 32 位")
    private String username;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 32, message = "姓名不能超过 32 个字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "性别不能为空")
    private String sex;

    @NotBlank(message = "身份证号不能为空")
    @Size(max = 32, message = "身份证号不能超过 32 个字符")
    private String idNumber;
}
