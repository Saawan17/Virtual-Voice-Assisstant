package com.example.assistant;

import java.time.LocalTime;

public class CommandHandler {

    private final FreeTTSSpeaker speaker;

    public CommandHandler(FreeTTSSpeaker speaker) {
        this.speaker = speaker;
    }

    public void handle(String command) {
        command = command.toLowerCase().trim();
        String response;

        // --- WEATHER QUERY ---
        if (command.matches(".*\\b(weather)\\b.*")) {
            String city = extractCity(command);
            if (city == null || city.isBlank()) {
                response = "Please tell me the city name.";
            } else {
                response = WeatherService.getWeather(city);
            }
        } else if (command.contains("time")) {
            response = "The current time is " + LocalTime.now().withNano(0).toString();
        } else if (command.contains("your name")) {
            response = "I'm your Java voice assistant.";
        } else if (command.contains("open google")) {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start https://www.google.com"});
                response = "Opening Google.";
            } catch (Exception e) {
                response = "Failed to open Google.";
            }
        }

        else if (command.startsWith("who") || command.startsWith("what") ||
                command.startsWith("why") || command.startsWith("how") ||
                command.contains("explain") || command.contains("describe")) {

            response = ChatService.ask(command);
        }

        // --- WIKIPEDIA LOOKUPS ---
        else if (command.matches(".*\\b(who is|what is|tell me about)\\b.*")) {
            String topic = extractTopic(command);
            System.out.println("WIKI TOPIC: [" + topic + "]");
            String wikiResult = WikipediaService.fetchSummary(topic);
            response = wikiResult;
        } else if (command.matches(".*\\b(hello|hi|hey)\\b.*")) {
            response = "Hello! How can I help you?";
        } else if (command.matches(".*\\b(stop|exit|quit)\\b.*")) {
            response = "Goodbye!";
            speaker.speak(response);
            System.exit(0);
            return;
        } else {
            response = "I heard: " + command + ". I'm still learning to perform that action.";
        }

        System.out.println("Assistant says: " + response);
        speaker.speak(response);
    }

    private String extractCity(String command) {
        command = command.replaceAll("what's", "what is");
        command = command.replaceAll("tell me", "");
        command = command.replaceAll("about", "");
        command = command.replaceAll("the", "").trim();

        String[] words = command.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("in") && i + 1 < words.length) {
                return words[i + 1];
            }
        }
        return null;
    }


    /**
     * Extracts the most likely Wikipedia topic from the user’s spoken phrase.
     */
    private String extractTopic(String command) {
        String[] triggers = {"tell me about", "who is", "what is"};
        for (String t : triggers) {
            int idx = command.indexOf(t);
            if (idx != -1) {
                String after = command.substring(idx + t.length()).trim();
                after = after.replaceAll("^[\\s,:;\\-]+", "");
                after = after.replaceAll("\\bplease\\b", "").trim();
                if (after.length() > 80) after = after.substring(0, 80).trim();
                return after.isEmpty() ? command : after;
            }
        }
        String[] words = command.split("\\s+");
        int n = words.length;
        if (n <= 3) return command;
        return words[n - 3] + " " + words[n - 2] + " " + words[n - 1];
    }

    /**
     * Breaks long text into sentences for smoother FreeTTS playback.
     */
    private void speakLong(String text) {
        String[] sentences = text.split("(?<=[.!?])\\s+");
        for (String s : sentences) {
            speaker.speak(s);
            try {
                Thread.sleep(350);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
