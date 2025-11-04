package com.example.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WikipediaService {

    private static final String SUMMARY_ENDPOINT = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static final ObjectMapper mapper = new ObjectMapper();

    // Simple in-memory cache to avoid repeat requests in short term
    private static final Map<String, String> cache = new ConcurrentHashMap<>();
    private static final int MAX_SUMMARY_LENGTH = 700; // characters — adjust for TTS clarity

    /**
     * Fetches a short, TTS-friendly summary from Wikipedia for the given query.
     * The query can be a page title or a short phrase like "Albert Einstein".
     * Returns a readable summary, or a friendly error message.
     */
    public static String fetchSummary(String query) {
        if (query == null || query.isBlank()) {
            return "I didn't detect a topic to look up on Wikipedia.";
        }

        String key = query.toLowerCase().trim();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        try {
            String title = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString());
            String endpoint = SUMMARY_ENDPOINT + title;

            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "java-voice-assistant/1.0 (https://example)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(7000);

            int code = conn.getResponseCode();
            if (code == 404) {
                String notFound = "I couldn't find a Wikipedia page for " + query + ".";
                cache.put(key, notFound);
                return notFound;
            }
            if (code != 200) {
                return "Wikipedia returned an error (HTTP " + code + ").";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();

            JsonNode root = mapper.readTree(sb.toString());

            // If page is disambiguation, handle specially
            if (root.has("type") && root.get("type").asText().equalsIgnoreCase("disambiguation")) {
                String extract = root.has("extract") ? root.get("extract").asText() : "";
                String msg = "I found multiple pages for " + query + ". " +
                        (extract.isBlank() ? "Please be more specific." : truncateForTTS(extract));
                cache.put(key, msg);
                return msg;
            }

            // Normal summary
            String titleOut = root.has("title") ? root.get("title").asText() : query;
            String description = root.has("description") ? root.get("description").asText() : "";
            String extract = root.has("extract") ? root.get("extract").asText() : "";

            String combined;
            if (!description.isBlank()) {
                combined = String.format("%s. %s", description, extract);
            } else {
                combined = extract;
            }

            if (combined.isBlank()) {
                String msg = "I found the page " + titleOut + " but there wasn't a summary available.";
                cache.put(key, msg);
                return msg;
            }

            combined = truncateForTTS(combined);
            String result = titleOut + ": " + combined;

            cache.put(key, result);
            return result;

        } catch (Exception e) {
            return "Sorry, I couldn't fetch information from Wikipedia right now.";
        }
    }

    private static String truncateForTTS(String text) {
        if (text.length() <= MAX_SUMMARY_LENGTH) return text;
        // try to cut at sentence boundary
        int cut = text.lastIndexOf('.', MAX_SUMMARY_LENGTH);
        if (cut <= 0) cut = MAX_SUMMARY_LENGTH;
        return text.substring(0, cut).trim() + ".";
    }
}
