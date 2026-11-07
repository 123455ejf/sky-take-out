package com.sky.controller;

import com.sky.common.result.Result;
import com.sky.service.MinioStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "B端公共上传接口")
@RestController
@RequestMapping("/admin/common")
@RequiredArgsConstructor
public class CommonUploadController {

    private final MinioStorageService minioStorageService;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", minioStorageService.upload(file));
    }
}
