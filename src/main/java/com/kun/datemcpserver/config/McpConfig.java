package com.kun.datemcpserver.config;

import com.kun.datemcpserver.tools.DateTimeMcpTools;
import com.kun.datemcpserver.tools.McpTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider dateTools(List<McpTool> mcpToolList) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpToolList.toArray()).build();
    }
}
