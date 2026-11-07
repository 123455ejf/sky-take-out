# A 员工模块交付说明

## 交付范围

A 模块负责 B 端员工相关接口，以及管理后台登录页、员工管理页。公共框架也由本次开工一并建立，方便 B、C、D、E 成员直接接入。

## 后端文件

- `EmployeeController`：员工接口入口
- `EmployeeService`：员工业务规则
- `EmployeeMapper`：员工数据访问
- `JwtAdminInterceptor`：B 端 Token 校验
- `GlobalExceptionHandler`：统一异常返回
- `AdminAccountInitializer`：首次启动时创建默认管理员

## 前端页面

- `/login`：管理员登录
- `/employee`：员工分页、新增、编辑、启用和禁用

## 自测清单

- 使用 `admin / 123456` 登录成功
- 错误密码返回明确提示
- 不带 Token 访问员工分页返回 401
- 新增员工后可在列表查到
- 修改员工后列表显示最新信息
- 启用和禁用状态切换正常
- 退出登录后本地 Token 被清除
- 分页和姓名搜索可用

## 接口约定提醒

新增员工默认密码为 `123456`。员工密码不通过列表或详情接口返回。

前端统一使用 `/api` 作为开发代理前缀，Vue CLI 会转发到 `http://localhost:8080`。
