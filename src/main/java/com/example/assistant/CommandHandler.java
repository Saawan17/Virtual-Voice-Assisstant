//package com.example.assistant;
//
//import java.awt.Desktop;
//import java.net.URI;
//import java.util.Locale;
//
//public class CommandHandler {
//    private final ResponseSpeaker speaker;
//
//    public CommandHandler(ResponseSpeaker speaker) {
//        this.speaker = speaker;
//    }
//
//    public void handle(String text) {
//        if (text == null) return;
//        String t = text.toLowerCase(Locale.ROOT).trim();
//        System.out.println("Recognized: '" + t + "'");
//
//        if (t.contains("time")) {
//            String time = Utils.timeNow();
//            speaker.speak("The time is " + time);
//            return;
//        }
//
//        if (t.startsWith("open")) {
//            String after = t.replaceFirst("open", "").trim();
//            if (after.startsWith("http")) {
//                openUrl(after);
//                speaker.speak("Opening the link");
//            } else if (after.length() > 0) {
//                String url = "https://www.google.com/search?q=" + after.replace(" ", "+");
//                openUrl(url);
//                speaker.speak("Searching for " + after);
//            } else {
//                speaker.speak("What would you like me to open?");
//            }
//            return;
//        }
//
//        if (t.matches(".*+ (plus|minus|times|multiplied by|divided by) .*")) {
//            try {
//                String cleaned = t.replaceAll("what is|calculate|please|answer|compute", "").trim();
//                cleaned = cleaned.replaceAll("multiplied by", "times");
//                String[] parts = cleaned.split(" ");
//                double a = Double.parseDouble(parts[0]);
//                String op = parts[1];
//                double b = Double.parseDouble(parts[2]);
//                double res = 0;
//                switch (op) {
//                    case "plus": res = a + b; break;
//                    case "minus": res = a - b; break;
//                    case "times": res = a * b; break;
//                    case "divided": res = a / b; break;
//                    default: speaker.speak("I couldn't parse the operation."); return;
//                }
//                speaker.speak("The answer is " + res);
//            } catch (Exception ex) {
//                speaker.speak("Sorry, I couldn't calculate that.");
//            }
//            return;
//        }
//
//        if (t.contains("hello") || t.contains("hi")) {
//            speaker.speak("Hello! How can I help you?");
//            return;
//        }
//
//        if (t.contains("exit") || t.contains("quit") || t.contains("stop listening")) {
//            speaker.speak("Goodbye!");
//            System.exit(0);
//        }
//
//        speaker.speak("I heard: " + text + ". I'm still learning to perform that action.");
//    }
//
//    private void openUrl(String url) {
//        try {
//            if (!url.startsWith("http")) url = "https://" + url;
//            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(url));
//        } catch (Exception e) {
//            speaker.speak("Unable to open the URL.");
//        }
//    }
//}

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

        if (command.contains("hello") || command.contains("hi")) {
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
}
