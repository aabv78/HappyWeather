package com.aabv78.happyweather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    private String weatherTemperature;
    private String weatherCity;
    private String weatherIcon;
    private int weatherCondition;


    public static WeatherDataModel fromJson(JSONObject jsonObject) {

        // creating the data model from a json object
        try {
            WeatherDataModel weatherData = new WeatherDataModel();

            weatherData.weatherCity = jsonObject.getString("name");
            weatherData.weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.weatherIcon = updateWeatherIcon(weatherData.weatherCondition);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);

            weatherData.weatherTemperature = Integer.toString(roundedValue);

            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "no";
    }


    // getters
    public String getWeatherTemperature() {
        return weatherTemperature + "°";
    }


    public String getWeatherCity() {
        return weatherCity;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

}
