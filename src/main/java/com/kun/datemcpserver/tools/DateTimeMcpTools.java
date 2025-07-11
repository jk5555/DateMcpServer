package com.kun.datemcpserver.tools;

import com.kun.datemcpserver.service.DateTimeService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 时间相关的MCP工具类
 * 提供给大模型调用的时间获取和计算功能
 */
@Component
public class DateTimeMcpTools implements McpTool {

    private final DateTimeService dateTimeService;

    public DateTimeMcpTools(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * 获取当前本地时间
     */
    @Tool(name = "get_current_time", description = "获取当前本地时间，格式为 yyyy-MM-dd HH:mm:ss")
    public String getCurrentTime() {
        return dateTimeService.getCurrentTime();
    }

    /**
     * 获取当前UTC时间
     */
    @Tool(name = "get_current_utc_time", description = "获取当前UTC时间，格式为 yyyy-MM-dd HH:mm:ss")
    public String getCurrentUtcTime() {
        return dateTimeService.getCurrentUtcTime();
    }

    /**
     * 获取指定时区的当前时间
     */
    @Tool(name = "get_time_in_zone", description = "获取指定时区的当前时间")
    public String getCurrentTimeInZone(@ToolParam(description = "时区ID，例如：Asia/Shanghai, America/New_York, Europe/London, UTC") String zoneId) {
        return dateTimeService.getCurrentTimeInZone(zoneId);
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    @Tool(name = "get_current_timestamp", description = "获取当前时间戳（毫秒）")
    public long getCurrentTimestamp() {
        return dateTimeService.getCurrentTimestamp();
    }

    /**
     * 获取当前时间戳（秒）
     */
    @Tool(name = "get_current_timestamp_seconds", description = "获取当前时间戳（秒）")
    public long getCurrentTimestampSeconds() {
        return dateTimeService.getCurrentTimestampSeconds();
    }

    /**
     * 时间戳转换为可读时间
     */
    @Tool(name = "timestamp_to_datetime", description = "将时间戳（毫秒）转换为可读的时间格式")
    public String timestampToDateTime(@ToolParam(description = "时间戳（毫秒），例如：1703123456789") long timestamp) {
        return dateTimeService.timestampToDateTime(timestamp);
    }

    /**
     * 可读时间转换为时间戳
     */
    @Tool(name = "datetime_to_timestamp", description = "将可读时间（yyyy-MM-dd HH:mm:ss格式）转换为时间戳")
    public long dateTimeToTimestamp(@ToolParam(description = "日期时间字符串，格式为 yyyy-MM-dd HH:mm:ss，例如：2023-12-21 10:30:00") String dateTime) {
        return dateTimeService.dateTimeToTimestamp(dateTime);
    }

    /**
     * 计算两个时间之间的差值
     */
    @Tool(name = "calculate_time_difference", description = "计算两个时间之间的差值，返回详细的时间差信息")
    public Map<String, Object> calculateTimeDifference(
            @ToolParam(description = "开始时间，格式为 yyyy-MM-dd HH:mm:ss，例如：2023-12-21 10:00:00") String startTime,
            @ToolParam(description = "结束时间，格式为 yyyy-MM-dd HH:mm:ss，例如：2023-12-21 15:30:00") String endTime) {
        return dateTimeService.calculateTimeDifference(startTime, endTime);
    }

    /**
     * 在指定时间基础上增加时间
     */
    @Tool(name = "add_time", description = "在指定时间基础上增加指定数量的时间单位（years/months/days/hours/minutes/seconds）")
    public String addTime(
            @ToolParam(description = "基础时间，格式为 yyyy-MM-dd HH:mm:ss，例如：2023-12-21 10:00:00") String dateTime,
            @ToolParam(description = "要增加的数量，可以为正数或负数，例如：5, -3") int amount,
            @ToolParam(description = "时间单位，可选值：years, months, days, hours, minutes, seconds") String unit) {
        return dateTimeService.addTime(dateTime, amount, unit);
    }

    /**
     * 格式化时间
     */
    @Tool(name = "format_datetime", description = "按照指定格式格式化时间")
    public String formatDateTime(
            @ToolParam(description = "要格式化的时间，格式为 yyyy-MM-dd HH:mm:ss，例如：2023-12-21 10:30:00") String dateTime,
            @ToolParam(description = "目标格式模式，例如：yyyy年MM月dd日, MM/dd/yyyy, HH:mm:ss") String pattern) {
        return dateTimeService.formatDateTime(dateTime, pattern);
    }

    /**
     * 获取今天是星期几
     */
    @Tool(name = "get_day_of_week", description = "获取今天是星期几")
    public String getDayOfWeek() {
        return dateTimeService.getDayOfWeek();
    }

    /**
     * 获取当前年份
     */
    @Tool(name = "get_current_year", description = "获取当前年份")
    public int getCurrentYear() {
        return dateTimeService.getCurrentYear();
    }

    /**
     * 获取当前月份
     */
    @Tool(name = "get_current_month", description = "获取当前月份")
    public int getCurrentMonth() {
        return dateTimeService.getCurrentMonth();
    }

    /**
     * 获取当前日期
     */
    @Tool(name = "get_current_day", description = "获取当前日期（几号）")
    public int getCurrentDay() {
        return dateTimeService.getCurrentDay();
    }

    /**
     * 判断是否为闰年
     */
    @Tool(name = "is_leap_year", description = "判断指定年份是否为闰年")
    public boolean isLeapYear(@ToolParam(description = "要判断的年份，例如：2024, 2023") int year) {
        return dateTimeService.isLeapYear(year);
    }

    /**
     * 获取完整的当前时间信息
     */
    @Tool(name = "get_full_time_info", description = "获取完整的当前时间信息，包括日期、时间、星期、时间戳等")
    public Map<String, Object> getFullTimeInfo() {
        return Map.of(
            "currentTime", dateTimeService.getCurrentTime(),
            "currentUtcTime", dateTimeService.getCurrentUtcTime(),
            "timestamp", dateTimeService.getCurrentTimestamp(),
            "timestampSeconds", dateTimeService.getCurrentTimestampSeconds(),
            "dayOfWeek", dateTimeService.getDayOfWeek(),
            "year", dateTimeService.getCurrentYear(),
            "month", dateTimeService.getCurrentMonth(),
            "day", dateTimeService.getCurrentDay(),
            "isLeapYear", dateTimeService.isLeapYear(dateTimeService.getCurrentYear())
        );
    }
}

