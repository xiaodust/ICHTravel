package com.icht.backfront.Tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Component
public class WeatherToolImpl implements WeatherTool{
    private final String key;

    public WeatherToolImpl(@Value("${spring.ai.weather.api-key}") String key) {
        this.key = key;
    }

    @Override
    @Tool(description = "获取指定城市天气 such as beijing/shanghai/hangzhou",name = "weather")
    public String getWeather(String city) throws MalformedURLException {
        String url="https://api.seniverse.com/v3/weather/now.json?key="+key+"&location="+city+"&language=zh-Hans&unit=c";
        HttpClient client=HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

