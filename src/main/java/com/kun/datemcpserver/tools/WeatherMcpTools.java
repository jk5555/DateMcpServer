package com.kun.datemcpserver.tools;

import com.kun.datemcpserver.service.WeatherService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 天气相关的MCP工具类
 * 提供给大模型调用的天气查询功能
 */
@Component
public class WeatherMcpTools implements McpTool {

    private final WeatherService weatherService;

    public WeatherMcpTools(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * 根据城市名获取当前天气
     */
    @Tool(name = "get_current_weather", description = "根据城市名获取当前天气信息，包括温度、湿度、风速等")
    public Map<String, Object> getCurrentWeather(
            @ToolParam(description = "城市名称，支持中文和英文，例如：北京, Beijing, 上海, Shanghai, New York") String cityName) {
        return weatherService.getCurrentWeather(cityName);
    }

    /**
     * 根据经纬度获取当前天气
     */
    @Tool(name = "get_weather_by_coordinates", description = "根据经纬度获取当前天气信息")
    public Map<String, Object> getCurrentWeatherByCoordinates(
            @ToolParam(description = "纬度，范围 -90 到 90，例如：39.9042 (北京纬度)") double lat,
            @ToolParam(description = "经度，范围 -180 到 180，例如：116.4074 (北京经度)") double lon) {
        return weatherService.getCurrentWeatherByCoordinates(lat, lon);
    }

    /**
     * 获取7天天气预报
     */
    @Tool(name = "get_weather_forecast", description = "获取指定城市的7天天气预报")
    public Map<String, Object> getWeatherForecast(
            @ToolParam(description = "城市名称，支持中文和英文，例如：北京, Beijing, 上海, Shanghai") String cityName) {
        return weatherService.getWeatherForecast(cityName);
    }

    /**
     * 根据经纬度获取7天天气预报
     */
    @Tool(name = "get_forecast_by_coordinates", description = "根据经纬度获取7天天气预报")
    public Map<String, Object> getWeatherForecastByCoordinates(
            @ToolParam(description = "纬度，范围 -90 到 90，例如：39.9042") double lat,
            @ToolParam(description = "经度，范围 -180 到 180，例如：116.4074") double lon,
            @ToolParam(description = "城市名称（可选），用于显示，例如：北京") String cityName) {
        return weatherService.getWeatherForecastByCoordinates(lat, lon, cityName);
    }

    /**
     * 获取空气质量信息
     */
    @Tool(name = "get_air_quality", description = "根据经纬度获取空气质量信息，包括PM2.5、PM10等污染物浓度")
    public Map<String, Object> getAirQuality(
            @ToolParam(description = "纬度，范围 -90 到 90，例如：39.9042") double lat,
            @ToolParam(description = "经度，范围 -180 到 180，例如：116.4074") double lon) {
        return weatherService.getAirQuality(lat, lon);
    }
}

