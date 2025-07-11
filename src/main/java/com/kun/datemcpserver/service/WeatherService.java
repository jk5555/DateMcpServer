package com.kun.datemcpserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气服务
 * 使用Open-Meteo免费API获取天气信息
 */
@Service
public class WeatherService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Open-Meteo API URLs
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String AIR_QUALITY_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    public WeatherService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 根据城市名获取经纬度
     */
    private Map<String, Double> getCoordinatesByCity(String cityName) {
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String url = String.format("%s?name=%s&count=1&language=zh&format=json",
                    GEOCODING_URL, encodedCity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode results = jsonNode.get("results");

            if (results == null || results.size() == 0) {
                throw new RuntimeException("未找到城市: " + cityName);
            }

            JsonNode firstResult = results.get(0);
            Map<String, Double> coordinates = new HashMap<>();
            coordinates.put("latitude", firstResult.get("latitude").asDouble());
            coordinates.put("longitude", firstResult.get("longitude").asDouble());
            coordinates.put("elevation", firstResult.has("elevation") ?
                    firstResult.get("elevation").asDouble() : 0.0);

            return coordinates;
        } catch (Exception e) {
            throw new RuntimeException("获取城市坐标失败: " + e.getMessage());
        }
    }

    /**
     * 根据城市名获取当前天气
     */
    public Map<String, Object> getCurrentWeather(String cityName) {
        Map<String, Double> coordinates = getCoordinatesByCity(cityName);
        return getCurrentWeatherByCoordinates(coordinates.get("latitude"),
                coordinates.get("longitude"), cityName);
    }

    /**
     * 根据经纬度获取当前天气
     */
    public Map<String, Object> getCurrentWeatherByCoordinates(double lat, double lon) {
        return getCurrentWeatherByCoordinates(lat, lon, null);
    }

    /**
     * 根据经纬度获取当前天气（内部方法）
     */
    private Map<String, Object> getCurrentWeatherByCoordinates(double lat, double lon, String cityName) {
        try {
            String url = String.format(
                    "%s?latitude=%.6f&longitude=%.6f&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m&timezone=auto",
                    WEATHER_URL, lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode current = jsonNode.get("current");

            Map<String, Object> weather = new HashMap<>();
            weather.put("city", cityName != null ? cityName : "未知位置");
            weather.put("latitude", lat);
            weather.put("longitude", lon);
            weather.put("temperature", current.get("temperature_2m").asDouble());
            weather.put("feelsLike", current.get("apparent_temperature").asDouble());
            weather.put("humidity", current.get("relative_humidity_2m").asInt());
            weather.put("pressure", current.get("surface_pressure").asDouble());
            weather.put("windSpeed", current.get("wind_speed_10m").asDouble());
            weather.put("windDirection", current.get("wind_direction_10m").asDouble());
            weather.put("precipitation", current.get("precipitation").asDouble());

            // 天气代码转换为描述
            int weatherCode = current.get("weather_code").asInt();
            weather.put("weatherCode", weatherCode);
            weather.put("description", getWeatherDescription(weatherCode));

            weather.put("updateTime", current.get("time").asText());

            return weather;
        } catch (Exception e) {
            throw new RuntimeException("获取天气信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取7天天气预报
     */
    public Map<String, Object> getWeatherForecast(String cityName) {
        Map<String, Double> coordinates = getCoordinatesByCity(cityName);
        return getWeatherForecastByCoordinates(coordinates.get("latitude"),
                coordinates.get("longitude"), cityName);
    }

    /**
     * 根据经纬度获取7天天气预报
     */
    public Map<String, Object> getWeatherForecastByCoordinates(double lat, double lon, String cityName) {
        try {
            String url = String.format(
                    "%s?latitude=%.6f&longitude=%.6f&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max&timezone=auto",
                    WEATHER_URL, lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode daily = jsonNode.get("daily");

            Map<String, Object> forecast = new HashMap<>();
            forecast.put("city", cityName != null ? cityName : "未知位置");
            forecast.put("latitude", lat);
            forecast.put("longitude", lon);

            List<Map<String, Object>> dailyForecast = new ArrayList<>();
            JsonNode dates = daily.get("time");
            JsonNode maxTemps = daily.get("temperature_2m_max");
            JsonNode minTemps = daily.get("temperature_2m_min");
            JsonNode weatherCodes = daily.get("weather_code");
            JsonNode precipitations = daily.get("precipitation_sum");
            JsonNode windSpeeds = daily.get("wind_speed_10m_max");

            for (int i = 0; i < dates.size(); i++) {
                Map<String, Object> dayWeather = new HashMap<>();
                dayWeather.put("date", dates.get(i).asText());
                dayWeather.put("maxTemp", maxTemps.get(i).asDouble());
                dayWeather.put("minTemp", minTemps.get(i).asDouble());
                dayWeather.put("precipitation", precipitations.get(i).asDouble());
                dayWeather.put("windSpeed", windSpeeds.get(i).asDouble());

                int weatherCode = weatherCodes.get(i).asInt();
                dayWeather.put("weatherCode", weatherCode);
                dayWeather.put("description", getWeatherDescription(weatherCode));

                dailyForecast.add(dayWeather);
            }

            forecast.put("forecast", dailyForecast);
            return forecast;
        } catch (Exception e) {
            throw new RuntimeException("获取天气预报失败: " + e.getMessage());
        }
    }

    /**
     * 获取空气质量信息
     */
    public Map<String, Object> getAirQuality(double lat, double lon) {
        try {
            LocalDate today = LocalDate.now();
            String dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

            String url = String.format(
                    "%s?latitude=%.6f&longitude=%.6f&current=pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,ozone&start_date=%s&end_date=%s",
                    AIR_QUALITY_URL, lat, lon, dateStr, dateStr);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode current = jsonNode.get("current");

            Map<String, Object> airQuality = new HashMap<>();
            airQuality.put("latitude", lat);
            airQuality.put("longitude", lon);
            airQuality.put("pm10", current.get("pm10").asDouble());
            airQuality.put("pm2_5", current.get("pm2_5").asDouble());
            airQuality.put("co", current.get("carbon_monoxide").asDouble());
            airQuality.put("no2", current.get("nitrogen_dioxide").asDouble());
            airQuality.put("o3", current.get("ozone").asDouble());
            airQuality.put("updateTime", current.get("time").asText());

            // 根据PM2.5计算简单的空气质量等级
            double pm25 = current.get("pm2_5").asDouble();
            String aqiLevel = getAirQualityLevel(pm25);
            airQuality.put("aqiLevel", aqiLevel);

            return airQuality;
        } catch (Exception e) {
            throw new RuntimeException("获取空气质量信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据天气代码获取天气描述
     */
    private String getWeatherDescription(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "晴朗";
            case 1, 2, 3 -> "多云";
            case 45, 48 -> "雾";
            case 51, 53, 55 -> "小雨";
            case 56, 57, 66, 67 -> "冻雨";
            case 61, 63, 65 -> "雨";
            case 71, 73, 75 -> "雪";
            case 77 -> "雪粒";
            case 80, 81, 82 -> "阵雨";
            case 85, 86 -> "阵雪";
            case 95 -> "雷暴";
            case 96, 99 -> "雷暴伴冰雹";
            default -> "未知天气";
        };
    }

    /**
     * 根据PM2.5值获取空气质量等级
     */
    private String getAirQualityLevel(double pm25) {
        if (pm25 <= 12) return "优秀";
        else if (pm25 <= 35) return "良好";
        else if (pm25 <= 55) return "中等";
        else if (pm25 <= 150) return "较差";
        else return "很差";
    }
}
