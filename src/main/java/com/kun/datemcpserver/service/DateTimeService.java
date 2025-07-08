package com.kun.datemcpserver.service;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间服务类，提供各种时间获取和计算功能
 */
@Service
public class DateTimeService {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 获取当前本地时间
     */
    public String getCurrentTime() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 获取当前UTC时间
     */
    public String getCurrentUtcTime() {
        return Instant.now().atZone(ZoneOffset.UTC).format(DEFAULT_FORMATTER);
    }

    /**
     * 获取指定时区的当前时间
     */
    public String getCurrentTimeInZone(String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            return ZonedDateTime.now(zone).format(DEFAULT_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时区ID: " + zoneId);
        }
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public long getCurrentTimestampSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 时间戳转换为可读时间
     */
    public String timestampToDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DEFAULT_FORMATTER);
    }

    /**
     * 可读时间转换为时间戳
     */
    public long dateTimeToTimestamp(String dateTime) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时间格式，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
    }

    /**
     * 计算两个时间之间的差值
     */
    public Map<String, Object> calculateTimeDifference(String startTime, String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime, DEFAULT_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(endTime, DEFAULT_FORMATTER);

            Duration duration = Duration.between(start, end);

            Map<String, Object> result = new HashMap<>();
            result.put("days", duration.toDays());
            result.put("hours", duration.toHours());
            result.put("minutes", duration.toMinutes());
            result.put("seconds", duration.getSeconds());
            result.put("milliseconds", duration.toMillis());

            // 详细的时间差
            long days = ChronoUnit.DAYS.between(start, end);
            long hours = ChronoUnit.HOURS.between(start, end) % 24;
            long minutes = ChronoUnit.MINUTES.between(start, end) % 60;
            long seconds = ChronoUnit.SECONDS.between(start, end) % 60;

            result.put("detailed", String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds));

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时间格式，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
    }

    /**
     * 在指定时间基础上增加时间
     */
    public String addTime(String dateTime, int amount, String unit) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);

            switch (unit.toLowerCase()) {
                case "years":
                case "year":
                    return localDateTime.plusYears(amount).format(DEFAULT_FORMATTER);
                case "months":
                case "month":
                    return localDateTime.plusMonths(amount).format(DEFAULT_FORMATTER);
                case "days":
                case "day":
                    return localDateTime.plusDays(amount).format(DEFAULT_FORMATTER);
                case "hours":
                case "hour":
                    return localDateTime.plusHours(amount).format(DEFAULT_FORMATTER);
                case "minutes":
                case "minute":
                    return localDateTime.plusMinutes(amount).format(DEFAULT_FORMATTER);
                case "seconds":
                case "second":
                    return localDateTime.plusSeconds(amount).format(DEFAULT_FORMATTER);
                default:
                    throw new IllegalArgumentException("不支持的时间单位: " + unit);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时间格式或参数");
        }
    }

    /**
     * 格式化时间
     */
    public String formatDateTime(String dateTime, String pattern) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return localDateTime.format(formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时间格式或格式化模式");
        }
    }

    /**
     * 获取今天是星期几
     */
    public String getDayOfWeek() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        return weekDays[dayOfWeek.getValue() - 1];
    }

    /**
     * 获取当前年份
     */
    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * 获取当前月份
     */
    public int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * 获取当前日期
     */
    public int getCurrentDay() {
        return LocalDate.now().getDayOfMonth();
    }

    /**
     * 判断是否为闰年
     */
    public boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }
}

