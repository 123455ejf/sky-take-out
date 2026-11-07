# 苍穹外卖 API 接口文档

项目采用 B 端管理后台和 C 端浏览器 H5 共用一套后端。接口变更必须同步更新本文件，并通知全体成员。

## 基础约定

- 后端地址：`http://localhost:8080`
- B 端前缀：`/admin`
- C 端前缀：`/user`
- 请求头：`token: <JWT>`
- 时间格式：`yyyy-MM-dd HH:mm:ss`
- 金额单位：分

统一返回：

```json
{
  "code": 1,
  "msg": "成功",
  "data": {}
}
```

| code | 含义 |
| --- | --- |
| 1 | 成功 |
| 0 | 业务异常 |
| 401 | 未登录或 Token 失效 |

## B 端员工模块

### 1. 员工登录

`POST /admin/employee/login`

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应 `data`：

```json
{
  "id": 1,
  "username": "admin",
  "name": "管理员",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. 员工登出

`POST /admin/employee/logout`

请求头：`token`

### 3. 员工分页查询

`GET /admin/employee/page`

查询参数：

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| page | 否 | 页码，默认 1 |
| pageSize | 否 | 每页条数，默认 10 |
| name | 否 | 姓名模糊查询 |

### 4. 新增员工

`POST /admin/employee`

```json
{
  "username": "zhangsan",
  "name": "张三",
  "phone": "13800138001",
  "sex": "男",
  "idNumber": "110101199001011234"
}
```

新员工默认密码为 `123456`。

### 5. 修改员工

`PUT /admin/employee`

请求体在新增字段基础上携带 `id`。

### 6. 根据 ID 查询员工

`GET /admin/employee/{id}`

### 7. 启用或禁用员工

`PUT /admin/employee/status/{status}?id={id}`

`status`：`1` 启用，`0` 禁用。

## 公共接口

### 公共文件上传

`POST /admin/common/upload`

请求头：`token`

请求体：`multipart/form-data`，文件字段为 `file`

响应 `data` 为可公开访问的文件地址。

## 其他模块接口

完整接口以原始《api接口文档》为准。当前项目已预留以下模块目录和路由：

- B 端：分类、菜品、套餐、订单、报表、公共上传
- C 端：用户、地址簿、购物车、菜品查询、套餐查询、订单

未实现的接口不得由前端先行假定字段。需要增减字段时先在本文件登记。
