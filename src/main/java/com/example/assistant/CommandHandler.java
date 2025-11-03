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

          if (command.contains("weather")) {
            String city = "your city"; // default
            String[] words = command.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].equalsIgnoreCase("in") && i + 1 < words.length) {
                    city = words[i + 1];
                    break;
                }
            }

            String weatherInfo = WeatherService.getWeather(city);
            response = weatherInfo;
        }

        else if (command.contains("hello") || command.contains("hi")) {
            response = "Hello there! How can I help you?";
        }
        else if (command.contains("time")) {
            response = "The current time is " + LocalTime.now().withNano(0).toString();
        }
        else if (command.contains("your name")) {
            response = "I'm your Java voice assistant.";
        }
        else if (command.contains("open google")) {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start https://www.google.com"});
                response = "Opening Google.";
            } catch (Exception e) {
                response = "Failed to open Google.";
            }
        }
        else if (command.contains("stop") || command.contains("exit") || command.contains("quit")) {
            response = "Goodbye!";
            speaker.speak(response);
            System.exit(0);
            return;
        }


        else {
            response = "I heard: " + command + ". I'm still learning to perform that action.";
        }

        System.out.println("Assistant says: " + response);
        speaker.speak(response);
    }

    private static boolean running = false;

    public void start(String modelPath) throws Exception {
        if (running) {
            System.out.println("VoiceAssistant already running. Ignoring duplicate start.");
            return;
        }
        running = true;
    }

}
