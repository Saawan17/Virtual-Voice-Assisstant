package com.example.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private static final String API_KEY = "418da941cc068230fc209e28655578af"; // replace with your key

    public static String getWeather(String city) {
        try {
            String endpoint = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                    city, API_KEY
            );

            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.toString());
            String weather = json.get("weather").get(0).get("description").asText();
            double temp = json.get("main").get("temp").asDouble();

            return String.format("The weather in %s is %s with temperature %.1f degrees Celsius.",
                    city, weather, temp);

        } catch (Exception e) {
            return "Sorry, I couldn’t fetch the weather for " + city + ".";
        }
    }
}
