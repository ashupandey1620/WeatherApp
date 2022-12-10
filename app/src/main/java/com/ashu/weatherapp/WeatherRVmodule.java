package com.ashu.weatherapp;

public class WeatherRVmodule {

    String time;
    String tempurature;
    String icon;
    String windSpeed;

    public WeatherRVmodule(String time, String tempurature, String icon, String windSpeed) {
        this.time = time;
        this.tempurature = tempurature;
        this.icon = icon;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTempurature() {
        return tempurature;
    }

    public void setTempurature(String tempurature) {
        this.tempurature = tempurature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
