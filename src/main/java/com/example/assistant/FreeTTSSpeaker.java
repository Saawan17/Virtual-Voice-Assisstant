package com.example.assistant;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class FreeTTSSpeaker implements ResponseSpeaker {
    private final Voice voice;

    public FreeTTSSpeaker() {
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");
        if (voice == null) throw new IllegalStateException("Cannot find FreeTTS voice 'kevin16'");
        voice.allocate();
    }

    @Override
    public void speak(String text) {
        System.out.println("Assistant says: " + text);
        voice.speak(text);
    }
}