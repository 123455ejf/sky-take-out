# GitHub 发布与组员接入

## 首次上传仓库

推荐使用 Git 命令上传，能够保留 `main`、`dev` 两个分支和提交历史。

1. 在 GitHub 新建空仓库。
2. 不要勾选自动创建 README、`.gitignore` 或 License。
3. 在项目根目录关联远程仓库：

```powershell
git remote add origin https://github.com/YU1000-7/<仓库名>.git
```

4. 推送两个分支：

```powershell
git push -u origin main
git push -u origin dev
```

5. 在 GitHub 仓库设置中把默认分支设为 `main`。

如果远程地址写错，可以修改：

```powershell
git remote set-url origin https://github.com/YU1000-7/<仓库名>.git
```

## 组员首次下载

```powershell
git clone https://github.com/YU1000-7/<仓库名>.git
cd <仓库名>
git switch dev
git pull
```

不要让组员直接下载 ZIP 后再上传，否则会产生互不关联的历史，后期很难合并。

## 组员开发新功能

```powershell
git switch dev
git pull
git switch -c feature/<模块名>
```

完成开发并自测后：

```powershell
git add .
git commit -m "feat: complete <模块名>"
git push -u origin feature/<模块名>
```

然后在 GitHub 创建 Pull Request，目标分支选择 `dev`。

## 同步最新代码

```powershell
git switch dev
git pull
git switch feature/<模块名>
git merge dev
```

出现冲突时先通知相关同学，不要直接覆盖对方代码。

## 不应上传的内容

以下目录和文件已经在 `.gitignore` 中排除：

- `node_modules/`
- `target/`
- `dist/`
- `.tools/`
- `.runtime/`
- `release/`
- IDE 配置
- 本地日志和数据库数据

不要把 369MB 的免安装演示包上传到仓库。它只用于线下发送；GitHub 仓库只保存源码、SQL、文档、测试和构建配置。

## 第一次上传前检查

```powershell
git status
git log --oneline --graph --all
```

确认：

- 工作区没有未提交文件
- `main` 和 `dev` 指向当前首版提交
- 没有 `node_modules`、`target`、`dist`、`.runtime`
- 没有真实密码、私钥或证书
