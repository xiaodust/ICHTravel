package com.icht.backfront.Tools;

import java.net.MalformedURLException;

public interface WeatherTool {
    String getWeather(String city) throws MalformedURLException;
}
