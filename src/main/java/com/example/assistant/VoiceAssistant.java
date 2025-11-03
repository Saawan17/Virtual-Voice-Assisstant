//package com.example.assistant;
//
//import org.vosk.LogLevel;
//import org.vosk.Model;
//import org.vosk.Recognizer;
//import org.vosk.LibVosk;
//
//import javax.sound.sampled.*;
//import java.io.IOException;
//
//public class VoiceAssistant {
//    private final CommandHandler handler;
//    private final ResponseSpeaker speaker;
//    private Model model;
//
//    public VoiceAssistant(CommandHandler handler, ResponseSpeaker speaker) {
//        this.handler = handler;
//        this.speaker = speaker;
//    }
//
//    public void start(String modelPath) throws IOException, LineUnavailableException {
//        LibVosk.setLogLevel(LogLevel.INFO);
//        model = new Model(modelPath);
//        speaker.speak("Assistant started. I am listening.");
//
//        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
//        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//        if (!AudioSystem.isLineSupported(info)) {
//            throw new LineUnavailableException("Microphone line not supported.");
//        }
//        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
//        microphone.open(format);
//        microphone.start();
//
//        int bufferSize = 4096;
//        byte[] buffer = new byte[bufferSize];
//
//        Recognizer recognizer = new Recognizer(model, 16000.0f);
//
//        while (true) {
//            int nbytes = microphone.read(buffer, 0, buffer.length);
//            if (nbytes < 0) break;
//            if (recognizer.acceptWaveForm(buffer, nbytes)) {
//                String result = recognizer.getResult();
//                String text = JsonUtils.extractText(result);
//                if (text != null && text.length() > 0) {
//                    handler.handle(text);
//                }
//            }
//        }
//    }
//}

package com.example.assistant;

import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import javax.sound.sampled.*;

public class VoiceAssistant {

    private final CommandHandler handler;
    private final FreeTTSSpeaker speaker;

    public VoiceAssistant(CommandHandler handler, FreeTTSSpeaker speaker) {
        this.handler = handler;
        this.speaker = speaker;
    }

    public void start(String modelPath) throws Exception {
        LibVosk.setLogLevel(LogLevel.INFO);
        try (Model model = new Model(modelPath)) {
            Recognizer recognizer = new Recognizer(model, 16000);
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            speaker.speak("Assistant started. I am listening.");

            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);

                // Handle only final recognized results
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String result = recognizer.getResult();
                    String text = extractText(result);
                    if (!text.isEmpty()) {
                        System.out.println("Recognized: '" + text + "'");
                        handler.handle(text);
                    }
                }
                // ❌ Ignore partials to prevent double triggers
                // else {
                //     String partial = recognizer.getPartialResult();
                //     System.out.println("Partial: " + partial);
                // }
            }
        }
    }

    private String extractText(String json) {
        int start = json.indexOf("\"text\" : \"") + 10;
        int end = json.indexOf("\"", start);
        if (start > 9 && end > start) {
            return json.substring(start, end);
        }
        return "";
    }
}
