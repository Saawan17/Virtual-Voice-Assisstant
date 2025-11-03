package com.example.assistant;

import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;

import javax.sound.sampled.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VoiceAssistant {

    private final CommandHandler handler;
    private final FreeTTSSpeaker speaker;

    // 👇 Prevent multiple concurrent starts
    private static boolean running = false;

    // Debounce and JSON parser setup (if you kept the earlier version)
    private String lastCommandText = "";
    private long lastCommandTs = 0L;
    private final long DUPLICATE_WINDOW_MS = 1500L;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public VoiceAssistant(CommandHandler handler, FreeTTSSpeaker speaker) {
        this.handler = handler;
        this.speaker = speaker;
    }

    public void start(String modelPath) throws Exception {
        // ✅ Prevent double initialization from IDE
        if (running) {
            System.out.println("VoiceAssistant already running. Ignoring duplicate start.");
            return;
        }
        running = true;

        LibVosk.setLogLevel(LogLevel.INFO);
        try (Model model = new Model(modelPath)) {
            Recognizer recognizer = new Recognizer(model, 16000);
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            speaker.speak("Assistant started. I am listening.");

            byte[] buffer = new byte[8192];
            while (true) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead <= 0) continue;

                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String text = extractText(recognizer.getResult());
                    if (text.trim().length() < 2) continue;

                    System.out.println("Recognized: '" + text + "'");
                    handler.handle(text);
                }
            }
        }
    }

    private String extractText(String json) {
        try {
            JsonNode root = jsonMapper.readTree(json);
            return root.has("text") ? root.get("text").asText() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
