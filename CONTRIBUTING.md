# 苍穹外卖协作规范

## 分支规则

- `main`：稳定分支，只接受经过自测的 PR
- `dev`：集成分支，日常合并目标
- `feature/<模块名>`：个人功能分支

首次拉取：

```powershell
git clone <GitHub仓库地址>
cd <仓库目录>
git switch dev
git pull
git switch -c feature/<模块名>
```

## 开始开发前

1. 先阅读 `docs/api.md` 和 `docs/reference/` 中的原始需求。
2. 确认自己负责的接口、页面和数据库表。
3. 接口字段有变化时，先修改 `docs/api.md`。
4. 表结构有变化时，同步修改 `sql/init.sql`。
5. 在群内说明公共代码改动，避免多人同时修改基座。

## 推荐环境

- JDK 17
- Maven 3.9
- Node.js 20 或更高版本
- MySQL 8
- Redis
- MinIO

有 Docker 的成员可以运行：

```powershell
docker compose up -d
```

Windows 无 Docker 时阅读
[docs/运行验证与接力说明.md](docs/运行验证与接力说明.md)。

## 后端开发

- `sky-pojo` 放 Entity、DTO、VO。
- `sky-server` 放 Controller、Service、Mapper、配置和拦截器。
- 统一返回使用 `Result<T>`，分页使用 `PageResult<T>`。
- B 端接口使用 `/admin`，C 端接口使用 `/user`。
- 业务异常抛 `BusinessException`，不要在每个 Controller 里重复拼错误响应。
- 多表写入必须加事务。

常用命令：

```powershell
cd server
mvn test
mvn -DskipTests package
```

## 前端开发

- B 端：`admin-web`
- C 端：`customer-h5`
- 两个前端依赖分开管理，不要互相复制 `node_modules`。
- 页面请求统一放在 `src/api/`。
- Axios 统一从 `src/utils/request.js` 导入。
- 不把 Token、密码或其他密钥写死在页面里。

常用命令：

```powershell
cd admin-web
npm install
npm run serve
npm run build

cd ..\customer-h5
npm install
npm run serve
npm run build
```

## 提交前自查

- 后端可以编译，相关测试通过。
- 前端 `npm run build` 通过。
- 自己负责的接口已经用 Knife4j 或页面跑通。
- 接口字段和 `docs/api.md` 一致。
- 没有提交 `node_modules`、`target`、`dist`、`.tools`、`.runtime`。
- 没有提交本地密码、真实密钥、数据库文件和上传文件。
- PR 中说明改了什么、怎么自测、还有哪些未完成。

## 提交信息

推荐格式：

```text
feat: add category management
fix: correct employee status update
docs: sync dish api fields
test: cover order submit flow
```

## 合并流程

1. 功能分支提交 PR，目标分支选 `dev`。
2. 至少一名其他成员完成代码检查。
3. 作者处理 Review 意见。
4. PR 合并后删除个人功能分支。
5. `dev` 联调稳定后，由总负责人统一合并到 `main`。
