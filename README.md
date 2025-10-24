# ICHTravel - 非遗文化旅游平台

## 项目概述

ICHTravel（Intangible Cultural Heritage Travel）是非遗文化旅游平台，旨在通过数字化手段展示和推广中国非物质文化遗产，为用户提供非遗文化体验、旅游路线规划、非遗商品购买以及文化交流的一站式服务。

## 项目结构

项目采用前后端分离架构：

```
ICHTravel/
├── backFornt/     # 后端Spring Boot项目
└── vue-app/       # 前端Vue 3应用
```

## 技术栈

### 前端技术栈
- Vue 3 + Vite
- Vue Router 4
- Element Plus
- Axios
- AMap JS API (高德地图)

### 后端技术栈
- Spring Boot 3.5.6
- Spring AI
- MyBatis
- MySQL
- Redis
- Kafka
- WebSocket
- Alibaba Cloud AI Tools

## 主要功能模块

### 1. 首页
- 非遗文化展示轮播图
- 功能模块快速入口
- 热门非遗推荐

### 2. 非遗地图
- 基于高德地图展示非遗文化分布
- 非遗点位详情查看
- 路线规划功能

### 3. 非遗商城
- 非遗商品浏览与搜索
- 商品分类展示
- 购物车功能
- 订单管理与结算

### 4. 3D工坊
- 3D全景展示非遗技艺
- 互动式体验
- 视频教程与评论

### 5. 交流平台
- 用户非遗文化分享
- 评论互动功能

### 6. 用户中心
- 个人信息管理
- 订单管理
- 我的路线
- 浏览历史
- 奖牌收藏
- 账号安全设置

## 安装与运行

### 前端安装

```bash
# 进入前端目录
cd vue-app

# 安装依赖
npm install

# 开发环境运行
npm run dev

# 构建生产版本
npm run build
```

### 后端安装

```bash
# 进入后端目录
cd backFornt

# 使用Maven构建
mvn clean install

# 运行应用
mvn spring-boot:run
```

## 配置说明

### 后端配置

主要配置文件位于 `backFornt/src/main/resources/application.yaml`，包含以下配置：
- 数据库连接信息
- Redis配置
- Kafka配置
- 阿里云AI工具API密钥
- 文件上传大小限制

### 前端配置

前端应用通过API调用与后端交互，API基础URL可在相应组件中配置。

## 系统架构

系统采用经典的三层架构：

1. **表示层**：Vue 3前端应用，负责用户界面和交互
2. **业务逻辑层**：Spring Boot后端服务，处理核心业务逻辑
3. **数据访问层**：基于MyBatis的数据持久化层，连接MySQL数据库

## 贡献指南

欢迎对项目进行贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

## 联系我们

如有任何问题或建议，请随时联系项目维护团队。

# ICHTravel 后端服务

## 项目概述

ICHTravel后端服务基于Spring Boot构建，为非遗文化旅游平台提供数据处理、业务逻辑和API接口支持。该服务采用RESTful API设计，支持用户管理、商品管理、订单处理、地图数据服务和AI交互等核心功能。

## 技术栈

- **框架**: Spring Boot 3.5.6
- **ORM框架**: MyBatis
- **数据库**: MySQL
- **缓存**: Redis
- **消息队列**: Kafka
- **WebSocket**: 支持实时通信
- **AI集成**: Spring AI, DashScope (阿里云)
- **身份认证**: Spring Session + Redis
- **构建工具**: Maven

## 项目结构

```
backFornt/
├── src/
│   ├── main/
│   │   ├── java/com/icht/backfront/
│   │   │   ├── api/         # API接口层
│   │   │   ├── config/      # 配置类
│   │   │   ├── controller/  # MVC控制器
│   │   │   ├── dao/         # 数据访问层
│   │   │   ├── dataobject/  # 数据对象
│   │   │   ├── model/       # 业务模型
│   │   │   ├── param/       # 请求参数
│   │   │   ├── service/     # 业务逻辑层
│   │   │   │   └── impl/    # 业务逻辑实现
│   │   │   ├── Tools/       # 工具类
│   │   │   └── BackFrontApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── mapper/      # MyBatis映射文件
│   │       └── application.yaml  # 应用配置
│   └── test/                # 测试代码
├── .gitignore               # Git忽略文件
├── mvnw                     # Maven包装器
├── mvnw.cmd                 # Maven包装器(Windows)
└── pom.xml                  # Maven项目配置
```

## 主要API模块

### 1. 用户管理API (`/api/user/*`)
- 用户注册、登录、退出
- 个人信息查询与更新
- 密码找回

### 2. 商品管理API (`/api/product/*`)
- 商品列表查询
- 商品详情获取
- 商品分类管理

### 3. 订单管理API (`/api/order/*`)
- 创建订单
- 查询订单列表
- 订单状态更新
- 订单详情查询

### 4. 购物车API (`/api/cart/*`)
- 添加商品到购物车
- 购物车商品管理
- 结算功能

### 5. 支付API (`/api/payment/*`)
- 创建支付请求
- 查询支付状态
- 支付回调处理

### 6. 内容管理API
- 非遗笔记管理 (`/api/note/*`)
- 评论管理 (`/api/comment/*`)

### 7. AI交互API
- 非遗知识问答
- WebSocket实时AI交互

## 服务组件

### 1. ChatService
提供与AI大模型的交互能力，支持流式响应和历史对话管理。

### 2. FileStorageService
处理文件上传、存储和检索功能。

### 3. WebSocketService
管理WebSocket连接，实现实时消息推送和AI交互。

### 4. PaymentService
集成支付系统，处理支付请求和回调。

## 配置说明

主要配置文件：`src/main/resources/application.yaml`

核心配置项：

```yaml
# 服务器配置
server:
  port: 8080

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/icht
    username: username
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

# Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: password

# Kafka配置
  kafka:
    bootstrap-servers: localhost:9092
    # 更多Kafka配置...

# MyBatis配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

## AI集成

系统集成了阿里云AI工具，支持天气查询、时间查询和网络搜索等功能：

- 天气API: 提供地点天气信息查询
- 时间工具: 提供当前时间信息
- 网络搜索: 支持通过API进行网络搜索

## 安装与运行

### 前置条件

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Kafka 3.0+

### 构建与运行

```bash
# 构建项目
mvn clean install

# 运行应用
mvn spring-boot:run

# 或运行jar包
java -jar target/backFornt-0.0.1-SNAPSHOT.jar
```

## 开发指南

### 代码规范

- 遵循Java编码规范
- 类名使用大驼峰命名法
- 方法名和变量名使用小驼峰命名法
- 常量使用全大写+下划线命名法
- 所有方法和类必须添加适当的注释

### API开发流程

1. 定义请求参数类(`param`包)
2. 定义响应模型类(`model`包)
3. 在`dao`层定义数据访问接口
4. 在`service`层实现业务逻辑
5. 在`api`层或`controller`层暴露接口

### 数据库操作

- 使用MyBatis进行数据库操作
- SQL语句定义在mapper XML文件中
- 使用事务管理确保数据一致性

## 安全说明

- 所有API请求需要进行身份验证
- 敏感数据传输采用HTTPS
- 密码采用加密存储
- 防止SQL注入和XSS攻击

# ICHTravel 前端应用

## 项目概述

ICHTravel前端应用基于Vue 3 + Vite构建，是一个为非遗文化旅游平台提供用户界面的单页应用(SPA)。该应用旨在为用户提供直观、美观且功能完备的非遗文化体验。

## 技术栈

- **框架**: Vue 3
- **构建工具**: Vite
- **路由**: Vue Router 4
- **UI组件库**: Element Plus
- **HTTP请求**: Axios
- **地图服务**: AMap JS API (高德地图)
- **CSS预处理器**: Sass
- **代码规范**: ESLint

## 项目结构

```
vue-app/
├── public/          # 静态资源
├── src/
│   ├── assets/      # 项目资源文件
│   ├── components/  # 通用组件
│   ├── router/      # 路由配置
│   ├── views/       # 页面组件
│   │   ├── Commit/  # 交流平台
│   │   ├── Home/    # 首页
│   │   ├── Login/   # 登录注册
│   │   ├── Mall/    # 非遗商城
│   │   ├── Map/     # 非遗地图
│   │   ├── WorkShop/ # 3D工坊
│   │   ├── layout/  # 布局组件
│   │   └── user/    # 用户中心
│   ├── App.vue      # 根组件
│   ├── main.js      # 入口文件
│   └── style.css    # 全局样式
├── .vscode/         # VS Code配置
├── .gitignore       # Git忽略文件
├── index.html       # HTML模板
├── package.json     # 项目配置和依赖
└── vite.config.js   # Vite配置
```

## 主要功能模块

### 1. 首页 (`/views/Home/`)
- 非遗文化展示轮播图
- 功能模块快速入口
- 城市非遗推荐

### 2. 非遗地图 (`/views/Map/`)
- 非遗文化点位地图展示
- 路线规划功能
- 图层切换

### 3. 非遗商城 (`/views/Mall/`)
- 商品列表与分页
- 商品详情展示
- 购物车管理
- 订单结算流程

### 4. 3D工坊 (`/views/WorkShop/`)
- 3D全景体验
- 视频教程观看
- 互动评论系统

### 5. 交流平台 (`/views/Commit/`)
- 用户非遗文化分享
- 个人主页展示

### 6. 用户中心 (`/views/user/`)
- 个人信息管理
- 订单管理
- 我的路线
- 浏览历史
- 奖牌收藏
- 账号设置

## 安装与运行

```bash
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 路由配置

路由配置位于`src/router/index.js`，主要路由包括：
- `/` - 首页
- `/login` - 登录页
- `/heritage-map` - 非遗地图
- `/heritage-mall` - 非遗商城
- `/3d-workshop` - 3D工坊
- `/heritage-commit` - 交流平台
- `/user-center/*` - 用户中心相关页面

## API交互

前端通过Axios与后端API进行交互，主要涉及：
- 用户认证与管理
- 商品数据获取与操作
- 订单处理
- 非遗信息查询
- 地图数据获取

## 开发指南

### 组件开发

推荐使用Vue 3的Composition API和`<script setup>`语法开发组件，遵循单一职责原则。

### 样式规范

- 使用Sass预处理器
- 组件样式使用`scoped`属性隔离
- 全局样式定义在`style.css`

### 最佳实践

- 使用Vue的响应式API管理状态
- 合理使用组件拆分与复用
- 遵循Vue 3的性能优化建议
- 确保代码可读性与可维护性
