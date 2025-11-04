package com.example.assistant;

import ai.picovoice.porcupine.*;
import javax.sound.sampled.*;

public class WakeWordListener {

    private Porcupine porcupine;
    private boolean listening = true;
    private final VoiceAssistant assistant;

    public WakeWordListener(VoiceAssistant assistant) {
        this.assistant = assistant;
    }

    public void startListening() throws Exception {
        porcupine = new Porcupine.Builder()
                .setAccessKey("pv_AK_your_access_key_here") // from Picovoice console
                .setKeywordPaths(new String[]{"src/main/resources/models/hey-assistant.ppn"})
                .build();

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        TargetDataLine mic = AudioSystem.getTargetDataLine(format);
        mic.open(format);
        mic.start();

        int frameLen = porcupine.getFrameLength();
        byte[] buffer = new byte[frameLen * 2]; // 16-bit samples

        System.out.println("👂 Listening for wake word 'Hey Assistant'...");

        while (listening) {
            int bytesRead = mic.read(buffer, 0, buffer.length);
            if (bytesRead == buffer.length) {
                short[] pcm = new short[frameLen];
                for (int i = 0; i < frameLen; i++) {
                    pcm[i] = (short) ((buffer[2 * i + 1] << 8) | (buffer[2 * i] & 0xFF));
                }

                int result = porcupine.process(pcm);
                if (result >= 0) {
                    System.out.println("🟢 Wake word detected!");
                    assistant.start("vosk-model-small-en-us-0.15"); // activate assistant
                }
            }
        }
    }

    public void stopListening() {
        listening = false;
        porcupine.delete();
    }
}
