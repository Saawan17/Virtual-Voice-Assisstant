package com.example.assistant;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatService {

    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_KEY = "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"; // 🔑 Replace with your Hugging Face token

    public static String ask(String question) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("inputs", question);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
                os.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray arr = new JSONArray(response.toString());
            String text = arr.getJSONObject(0).getString("generated_text");

            return text.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn’t connect to the AI server right now.";
        }
    }
}
