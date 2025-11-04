package com.example.assistant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class WeatherService {

    private static final String API_KEY = "418da941cc068230fc209e28655578af"; // 🔑 Replace this
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static String getWeather(String city) {
        try {
            String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(result.toString());

            if (json.has("main")) {
                double temp = json.getJSONObject("main").getDouble("temp");
                String weather = json.getJSONArray("weather").getJSONObject(0).getString("description");
                String cityName = json.getString("name");
                return String.format("Currently in %s, it is %.1f degrees Celsius with %s.", cityName, temp, weather);
            } else {
                return "Sorry, I couldn’t find weather data for " + city + ".";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch weather for " + city + ".";
        }
    }
}
