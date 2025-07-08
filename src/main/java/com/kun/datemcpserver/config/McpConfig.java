package com.kun.datemcpserver.config;

import com.kun.datemcpserver.tools.DateTimeMcpTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider dateTools(DateTimeMcpTools dateTimeMcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(dateTimeMcpTools).build();
    }
}
