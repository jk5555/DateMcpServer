# MCP客户端连接指南

## 概述

DateTime MCP Server 是一个基于Spring AI 1.0.0构建的MCP（Model Context Protocol）服务器，提供时间相关的工具函数。本指南详细说明如何将MCP客户端连接到此服务器。

## 连接方式

### 方式一：stdio连接（推荐）

MCP协议主要通过stdio（标准输入输出）方式进行通信，这是最标准和推荐的连接方式。

#### 1. 准备服务器

```bash
# 克隆或下载项目
cd /path/to/DateMcpServer

# 构建项目
./mvnw clean package -DskipTests

# 验证jar包生成
ls -la target/DateMcpServer-0.0.1-SNAPSHOT.jar
```

#### 2. 测试服务器

```bash
# 直接运行测试
java -jar target/DateMcpServer-0.0.1-SNAPSHOT.jar
```

服务器启动后，你应该看到类似以下的日志：
```
2025-07-08 18:01:27 [main] INFO  c.k.d.DateMcpServerApplication - Starting DateMcpServerApplication
2025-07-08 18:01:27 [main] INFO  o.s.a.m.s.a.McpServerAutoConfiguration - Enable tools capabilities, notification: true
2025-07-08 18:01:28 [main] INFO  c.k.d.DateMcpServerApplication - Started DateMcpServerApplication in 0.622 seconds
```

## 客户端配置

### 1. Claude Desktop

在Claude Desktop中配置MCP服务器：

**配置文件位置**: `~/Library/Application Support/Claude/claude_desktop_config.json` (macOS)

```json
{
  "mcpServers": {
    "datetime-server": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/kun/IdeaProjects/DateMcpServer/target/DateMcpServer-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**使用方法**:
1. 保存配置文件
2. 重启Claude Desktop
3. 在对话中询问时间相关问题，如："现在几点了？"

### 2. Continue.dev

在Continue.dev中配置MCP服务器：

**配置文件**: `.continue/config.json`

```json
{
  "mcpServers": [
    {
      "name": "datetime-server",
      "command": ["java", "-jar", "/Users/kun/IdeaProjects/DateMcpServer/target/DateMcpServer-0.0.1-SNAPSHOT.jar"]
    }
  ]
}
```

### 3. Python MCP客户端

使用Python的MCP客户端库连接：

```python
import asyncio
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

async def main():
    # 配置服务器参数
    server_params = StdioServerParameters(
        command="java",
        args=["-jar", "/Users/kun/IdeaProjects/DateMcpServer/target/DateMcpServer-0.0.1-SNAPSHOT.jar"]
    )

    # 连接到服务器
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            # 初始化连接
            await session.initialize()

            # 列出可用工具
            tools_result = await session.list_tools()
            print("可用工具:")
            for tool in tools_result.tools:
                print(f"  - {tool.name}: {tool.description}")

            # 调用工具获取当前时间
            result = await session.call_tool("get_current_time", {})
            print(f"\n当前时间: {result.content[0].text}")

            # 调用工具获取完整时间信息
            result = await session.call_tool("get_full_time_info", {})
            print(f"\n完整时间信息: {result.content[0].text}")

if __name__ == "__main__":
    asyncio.run(main())
```

### 4. Node.js MCP客户端

使用Node.js的MCP客户端：

```javascript
import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StdioClientTransport } from "@modelcontextprotocol/sdk/client/stdio.js";
import { spawn } from "child_process";

async function main() {
  // 启动服务器进程
  const serverProcess = spawn("java", [
    "-jar",
    "/Users/kun/IdeaProjects/DateMcpServer/target/DateMcpServer-0.0.1-SNAPSHOT.jar"
  ]);

  // 创建传输层
  const transport = new StdioClientTransport({
    stdin: serverProcess.stdin,
    stdout: serverProcess.stdout,
    stderr: serverProcess.stderr
  });

  // 创建客户端
  const client = new Client({
    name: "datetime-client",
    version: "1.0.0"
  }, {
    capabilities: {
      tools: {}
    }
  });

  // 连接
  await client.connect(transport);

  // 列出工具
  const tools = await client.listTools();
  console.log("可用工具:", tools.tools.map(t => t.name));

  // 调用工具
  const result = await client.callTool({
    name: "get_current_time",
    arguments: {}
  });

  console.log("当前时间:", result.content);

  // 清理
  await client.close();
  serverProcess.kill();
}

main().catch(console.error);
```

## 可用工具列表

服务器提供以下16个工具函数：

1. **get_current_time** - 获取当前本地时间
2. **get_current_utc_time** - 获取当前UTC时间
3. **get_time_in_zone** - 获取指定时区的当前时间
4. **get_current_timestamp** - 获取当前时间戳（毫秒）
5. **get_current_timestamp_seconds** - 获取当前时间戳（秒）
6. **timestamp_to_datetime** - 时间戳转换为可读时间
7. **datetime_to_timestamp** - 可读时间转换为时间戳
8. **calculate_time_difference** - 计算两个时间之间的差值
9. **add_time** - 在指定时间基础上增加时间
10. **format_datetime** - 格式化时间
11. **get_day_of_week** - 获取今天是星期几
12. **get_current_year** - 获取当前年份
13. **get_current_month** - 获取当前月份
14. **get_current_day** - 获取当前日期
15. **is_leap_year** - 判断是否为闰年
16. **get_full_time_info** - 获取完整的当前时间信息

## 使用示例

连接成功后，你可以向大模型询问以下问题：

- "现在几点了？"
- "今天是星期几？"
- "2024年是闰年吗？"
- "计算一下从2024年1月1日到现在过了多少天？"
- "给我显示东京的当前时间"
- "将时间戳1704067200000转换为可读时间"

## 故障排除

### 1. 服务器启动失败
- 确保Java 17已安装
- 检查jar包是否正确生成
- 查看启动日志中的错误信息

### 2. 客户端连接失败
- 确认jar包路径正确
- 检查配置文件格式是否正确
- 验证服务器是否正常启动

### 3. 工具调用失败
- 检查工具名称是否正确
- 验证参数格式是否符合要求
- 查看服务器日志中的错误信息

## 开发和调试

### REST API测试（开发模式）

除了MCP协议，服务器还提供REST API用于开发测试：

```bash
# 启动开发服务器
./mvnw spring-boot:run

# 测试API
curl http://localhost:8080/api/datetime/current
curl http://localhost:8080/api/datetime/health
```

### 日志配置

在`application.yml`中调整日志级别：

```yaml
logging:
  level:
    com.kun.datemcpserver: DEBUG
    org.springframework.ai.mcp: DEBUG
```

这样就可以看到详细的MCP协议交互日志。

