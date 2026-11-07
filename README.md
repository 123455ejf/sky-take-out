# 苍穹外卖

苍穹外卖是前后端分离的外卖项目，包含商家管理后台和浏览器端客户 H5。

组员协作前请先阅读 [CONTRIBUTING.md](CONTRIBUTING.md)。

当前版本由项目首个开发者搭建，已完成：

- 仓库目录、数据库脚本和协作文档框架
- Spring Boot 多模块后端基座
- Vue2 管理后台和客户 H5 基础工程
- A 负责的员工模块完整闭环
- 默认管理员初始化、JWT 鉴权和统一异常处理

## 目录

```text
.
├── admin-web/       B 端商家后台，Vue2 + ElementUI
├── customer-h5/     C 端客户 H5，Vue2 + Vant
├── docs/            接口和开发计划
├── server/          Spring Boot 多模块后端
├── sql/             MySQL 初始化脚本
└── docker-compose.yml
```

## 技术栈

- 后端：Spring Boot 2.7、MyBatis-Plus、MySQL 8、Redis、JWT、MinIO、Knife4j
- B 端：Vue2、ElementUI、Axios、Vue Router、Vuex、ECharts
- C 端：Vue2、Vant、Axios、Vue Router

## 本地启动

建议环境：JDK 17、Maven 3.9、Node.js 20 或更高版本、MySQL 8、Redis 7。

1. 启动基础设施：

```powershell
docker compose up -d mysql redis minio
```

2. 启动后端：

```powershell
cd server
mvn clean package
mvn -pl sky-server spring-boot:run
```

3. 启动管理后台：

```powershell
cd admin-web
npm install
npm run serve
```

4. 启动客户 H5：

```powershell
cd customer-h5
npm install
npm run serve
```

## 无 Docker 演示

Windows 电脑可直接使用项目内的便携环境：

```powershell
.\scripts\dev\setup-infra.ps1 -AllowOutsideProjectJunction
.\scripts\dev\start-demo.ps1
.\scripts\dev\verify-demo.ps1
```

完整地址、窗口说明和下一阶段分工见
[docs/运行验证与接力说明.md](docs/运行验证与接力说明.md)。

默认管理员账号：

```text
用户名：admin
密码：123456
```

后端默认地址为 `http://localhost:8080`，Knife4j 文档地址为
`http://localhost:8080/doc.html`。

## 接口与协作

- 接口契约：[docs/api.md](docs/api.md)
- 开发计划：[docs/plan.md](docs/plan.md)
- A 模块说明：[docs/A_员工模块交付说明.md](docs/A_员工模块交付说明.md)
- 运行与交接：[docs/运行验证与接力说明.md](docs/运行验证与接力说明.md)
- 原始需求：[docs/reference/README.md](docs/reference/README.md)
- GitHub 发布：[docs/GitHub发布与组员接入.md](docs/GitHub发布与组员接入.md)

接口、数据库字段发生变更时，需要同时更新 `docs/api.md` 和 `sql/init.sql`。
