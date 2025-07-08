#!/bin/bash

# DateTime MCP Server 启动脚本
# 用于MCP客户端通过stdio方式连接

cd "$(dirname "$0")"

# 构建项目
./mvnw clean package -DskipTests

# 启动MCP服务器（stdio模式）
java -jar target/DateMcpServer-0.0.1-SNAPSHOT.jar

