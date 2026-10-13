# StarVMS 仓储管理系统（后端）

基于 Spring Boot 3 + JeecgBoot 3.8 二次开发的仓储管理系统（WMS），覆盖从商品建档到发运出库的完整仓储业务闭环，并扩展了 AI 智能助手与 IoT 环境监控能力。

## 技术栈

| 类别 | 技术 |
|------|------|
| 基础框架 | Spring Boot 3.4.5 / JDK 17 / JeecgBoot 3.8.1 |
| 数据层 | MyBatis-Plus / MySQL 8 / Redis |
| 中间件 | RabbitMQ（IoT 消息）/ InfluxDB（时序数据）/ MQTT |
| AI | Spring AI（RAG 检索增强 + Function Calling 工具调用） |
| 其他 | Redisson 分布式锁 / PageHelper / EasyExcel / 顺丰丰桥 API |

## 功能模块

- **商品基础资料**：品牌 / 类别 / 商品 / 属性管理
- **仓库与储位**：仓库、库区、储位管理
- **入库管理**：入库单（EasyExcel 导入）/ 收货 / 上架任务
- **库存**：库存管理与库存分配
- **波次与拣货**：波次创建与拣货分拣，支持 SISQ / SIFQ / SIW / EOW 波次策略；A* 算法规划拣货路径
- **出库管理**：出库单与出库流程
- **打包与发运**：打包策略、发运策略（策略模式 + 工厂 + 责任链）
- **顺丰对接**：运单下单、路由查询、Lodop 面单打印
- **AI 智能助手**：Spring AI 对话，RAG 知识库检索，Function Calling 查询库存（WmsInventoryTools）
- **IoT 环境监控**：MQTT 采集温湿度等环境数据，阈值告警，数据模拟器，实时监控大屏（WebSocket 推送）

## 快速开始

1. 环境要求：JDK 17、MySQL 8、Redis、Maven 3.8+
2. 导入数据库：执行 `db/xingchenwms-20251114.sql`
3. 修改 `jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml` 中的数据库与 Redis 连接
4. 启动：

```bash
mvn clean install -DskipTests
# 运行 jeecg-system-start 的 JeecgSystemApplication
```

5. 访问 `http://localhost:8080/jeecg-boot`，默认账号 `admin / 123456`

> IoT 子工程 `jeecg-module-iot` 为独立父子工程，含 manage / alert / simulate 三个服务，需单独构建启动（依赖 RabbitMQ、InfluxDB、MQTT Broker）。

## 工程结构

```
├── jeecg-boot-base-core          # 框架核心
├── jeecg-boot-module
│   ├── jeecg-boot-module-airag   # AI 模块（由 langchain4j 改造为 Spring AI）
│   └── jeecg-module-demo         # 示例模块
├── jeecg-module-system           # 系统模块（启动入口 jeecg-system-start）
├── jeecg-module-wms              # WMS 业务模块
│   └── org.jeecg.modules.wms
│       ├── goods / warehouse     # 商品、仓库储位
│       ├── inorder / wmstask     # 入库、通用任务域
│       ├── inventory / outorder  # 库存、出库
│       ├── wave / pickroute      # 波次、A* 拣货路径
│       ├── shipment / waybill    # 发运、顺丰运单
│       ├── ai                    # AI 助手（RAG + Function Calling）
│       └── analysis / websocket  # 大屏分析、WebSocket
└── jeecg-module-iot              # IoT 子工程（独立构建）
```

## 说明

- 本项目基于 [JeecgBoot 3.8.1](https://github.com/jeecgboot/JeecgBoot) 二次开发，业务代码位于 `jeecg-module-wms` 与 `jeecg-module-iot`
- 顺丰对接使用丰桥沙箱环境（测试样例见 `jeecg-module-wms/src/test`）
- Windows 环境建议开启 git 长路径支持：`git config core.longpaths true`
