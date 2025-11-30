# CareerCoach

一个基于 Spring Boot 的职业教练预约系统，集成 Cal.com API 实现在线预约和日程管理功能。

## 项目简介

CareerCoach 是一个完整的职业指导预约平台，为用户提供便捷的教练预约服务。系统支持用户注册、登录、查询可用时间段、创建和管理预约等功能，通过集成 Cal.com API 实现专业的日程安排和时间管理。

## 技术栈

- **后端框架**: Spring Boot 3.4.12
- **编程语言**: Java 21
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **ORM 框架**: MyBatis Plus 3.5.5
- **安全认证**: Spring Security + JWT
- **构建工具**: Maven
- **第三方集成**: Cal.com API

## 主要功能

### 用户管理
- 用户注册与登录
- JWT Token 认证
- 用户信息管理
- 密码加密存储

### 教练管理
- 教练档案查询
- 教练信息展示

### 预约管理
- 查询可用时间段
- 创建预约
- 取消预约
- 预约详情查询
- 预约状态管理

### Cal.com 集成
- 自动同步日程
- Webhook 事件处理
- 时区管理

## 项目结构

```
CareerCoach/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/coach/careercoach/
│   │   │       ├── api/                    # API 响应包装类
│   │   │       ├── config/                 # 配置类
│   │   │       │   ├── MybatisPlusConfig.java
│   │   │       │   ├── RedisConfig.java
│   │   │       │   ├── RestTemplateConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/             # 控制器层
│   │   │       │   ├── BookingController.java
│   │   │       │   └── UserController.java
│   │   │       ├── dto/                    # 数据传输对象
│   │   │       │   ├── auth/              # 认证相关 DTO
│   │   │       │   ├── booking/           # 预约相关 DTO
│   │   │       │   ├── calcom/            # Cal.com 相关 DTO
│   │   │       │   ├── coach/             # 教练相关 DTO
│   │   │       │   ├── user/              # 用户相关 DTO
│   │   │       │   └── webhook/           # Webhook DTO
│   │   │       ├── enums/                 # 枚举类
│   │   │       ├── exception/             # 异常处理
│   │   │       ├── external/              # 外部 API 客户端
│   │   │       │   └── CalComClient.java
│   │   │       ├── mapper/                # MyBatis Mapper
│   │   │       ├── model/                 # 实体类
│   │   │       │   └── entity/
│   │   │       ├── service/               # 服务层
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── BookingService.java
│   │   │       │   ├── CoachService.java
│   │   │       │   └── UserService.java
│   │   │       └── util/                  # 工具类
│   │   │           ├── EncryptionUtil.java
│   │   │           └── JwtUtil.java
│   │   └── resources/
│   │       ├── application.yml            # 应用配置文件
│   │       ├── static/                    # 静态资源
│   │       └── templates/                 # 模板文件
│   └── test/                              # 测试代码
├── pom.xml                                # Maven 配置文件
└── README.md                              # 项目说明文档
```

## 环境要求

- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd CareerCoach
```

### 2. 配置数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE CareerCoach DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置应用

编辑 `src/main/resources/application.yml` 文件，配置以下内容：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/CareerCoach?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
    
  data:
    redis:
      host: localhost
      port: 6379

# Cal.com 集成配置
cal:
  api-url: https://api.cal.com/v2
  base-url: https://cal.com
  username: your_cal_username
  event-type-id: your_event_type_id
  event-type-slug: your_event_slug
  time-zone: Your/TimeZone

# JWT 配置
jwt:
  secret: your_jwt_secret
  expiration: 3600000

# 加密配置
encryption:
  secret-key: your_encryption_key
```

### 4. 启动 Redis

```bash
redis-server
```

### 5. 运行项目

使用 Maven 运行：

```bash
./mvnw spring-boot:run
```

或者在 Windows 上：

```bash
mvnw.cmd spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 6. 构建项目

```bash
./mvnw clean package
```

生成的 JAR 文件位于 `target/` 目录下。

## API 文档

### 用户认证

#### 注册
- **POST** `/api/auth/register`
- Request Body:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "User Name"
}
```

#### 登录
- **POST** `/api/auth/login`
- Request Body:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### 预约管理

#### 查询可用时间段
- **GET** `/api/bookings/available-slots`
- Query Parameters: 开始日期、结束日期等

#### 创建预约
- **POST** `/api/bookings`
- Headers: `Authorization: Bearer {token}`
- Request Body: 预约详情

#### 取消预约
- **DELETE** `/api/bookings/{id}`
- Headers: `Authorization: Bearer {token}`

## 安全配置

项目使用 Spring Security 结合 JWT 实现认证和授权：

- 密码使用加密算法存储
- JWT Token 用于 API 认证
- 敏感信息加密存储
- CORS 跨域配置

## 数据库设计

主要数据表：

- **User**: 用户信息表
- **CoachProfile**: 教练档案表
- **Booking**: 预约记录表

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进项目。

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至：[your-email@example.com]

---

**注意**: 请勿将包含敏感信息的配置文件提交到版本控制系统中。建议使用环境变量或配置管理工具管理敏感配置。

