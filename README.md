# CampusMate

CampusMate 是一个校园搭子与倾诉广场项目，包含 Spring Boot 后端、Vue 3 前端和 MySQL 数据库脚本。

## 技术栈

- 后端：Java 17、Spring Boot 3.3.6、MyBatis、MySQL Connector/J
- 前端：Vue 3、Vite、Vue Router、Element Plus、Axios
- 数据库：MySQL 8.x，字符集建议使用 `utf8mb4`

## 目录结构

```text
CampusMate/
  backend/          Spring Boot 后端
  frontend/         Vue + Vite 前端
  database/         数据库脚本
    init.sql        一键初始化脚本
  scripts/          辅助脚本
```

## 环境准备

请先安装：

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.x

后端默认连接本机 MySQL：

```yaml
url: jdbc:mysql://localhost:3306/campusmate
username: root
password: 123456
```

如果你的 MySQL 用户名或密码不同，请修改 `backend/src/main/resources/application.yml`，或在本地创建对应的 MySQL 用户。

## 初始化数据库

方式一：直接执行单文件 SQL。

```bash
mysql --default-character-set=utf8mb4 -u root -p < database/init.sql
```

方式二：Windows PowerShell 使用脚本。

```powershell
.\scripts\init-database.ps1
```

如果 MySQL 密码不是默认的 `123456`：

```powershell
.\scripts\init-database.ps1 -User root -Password 你的密码
```

`database/init.sql` 会创建 `campusmate` 数据库、重建项目表，并导入演示数据。重复执行会重置演示数据。

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认运行在：

```text
http://127.0.0.1:8080
```

## 启动前端

首次运行先安装依赖：

```bash
cd frontend
npm install
```

启动开发服务器：

```bash
npm run dev
```

前端默认运行在：

```text
http://127.0.0.1:5173
```

Vite 已配置 `/api` 和 `/uploads` 代理到后端 `http://127.0.0.1:8080`，所以本地开发时请先启动后端，再启动前端。

## 演示账号

初始化数据库后可使用以下账号登录：

```text
账号：demo
密码：123456
```

手机号认证相关验证码为演示验证码：

```text
123456
```

## 常用命令

后端测试：

```bash
cd backend
mvn test
```

前端构建：

```bash
cd frontend
npm run build
```

前端预览构建产物：

```bash
cd frontend
npm run preview
```

## 常见问题

如果后端启动时报数据库连接失败，请确认 MySQL 已启动、`campusmate` 数据库已初始化，并检查 `application.yml` 中的用户名和密码。

如果前端页面请求失败，请确认后端正在 `8080` 端口运行，并从 Vite 地址 `http://127.0.0.1:5173` 访问前端。

如果中文数据出现乱码，请确认导入 SQL 时使用了 `--default-character-set=utf8mb4`。
