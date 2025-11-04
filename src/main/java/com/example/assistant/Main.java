package com.example.assistant;

public class Main {
    public static void main(String[] args) {
        String modelPath = null;
        if (args.length >= 2 && args[0].equals("--modelPath")) {
            modelPath = args[1];
        } else if (System.getenv("VOSK_MODEL_PATH") != null) {
            modelPath = System.getenv("VOSK_MODEL_PATH");
        } else {
            System.err.println("Usage: --modelPath <path-to-vosk-model> OR set VOSK_MODEL_PATH env var");
            System.exit(1);
        }

        FreeTTSSpeaker speaker = new FreeTTSSpeaker();
        CommandHandler handler = new CommandHandler(speaker);
        VoiceAssistant assistant = new VoiceAssistant(handler, speaker);
        try {
            assistant.start(modelPath);
        } catch (Exception e) {
            e.printStackTrace();
            speaker.speak("Failed to start assistant: " + e.getMessage());
        }
    }
}

//package com.example.assistant;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            VoiceAssistant assistant = new VoiceAssistant();
//            WakeWordListener listener = new WakeWordListener(assistant);
//            listener.startListening();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

