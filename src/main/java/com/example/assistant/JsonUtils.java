package com.example.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String extractText(String voskResultJson) {
        try {
            JsonNode node = MAPPER.readTree(voskResultJson);
            JsonNode text = node.get("text");
            if (text != null) return text.asText();
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}