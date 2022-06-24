package ke.co.skyworld.utils.security.captcha.producers;

import nl.captcha.audio.Sample;
import nl.captcha.audio.producer.VoiceProducer;
import nl.captcha.util.FileUtil;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * skyworld-api (ke.co.skyworld.utils.security.captcha.producers)
 * Created by: elon
 * On: 09 Mar, 2020. 1:22 PM
 **/
public class NumericVoiceProducer implements VoiceProducer {
    private static final Random RAND = new SecureRandom();
    private static final String[] DEFAULT_VOICES = new String[]{"alex", "bruce", "fred", "ralph", "kathy", "vicki", "victoria"};
    private static final Map<Integer, String[]> DEFAULT_VOICES_MAP = new HashMap();
    private final Map<Integer, String[]> _voices;

    static {


    }

    public NumericVoiceProducer(String answer) {
        this(DEFAULT_VOICES_MAP, answer);
    }

    public NumericVoiceProducer(Map<Integer, String[]> voices, String answer) {
        this._voices = voices;

        char[] answerChar = answer.toCharArray();

        for (char c : answerChar) {
            String[] files_for_num = new String[DEFAULT_VOICES.length];

            for (int j = 0; j < files_for_num.length; ++j) {
                String sb = "/sounds/en/numbers/" + c + "-" + DEFAULT_VOICES[j] + ".wav";
                files_for_num[j] = sb;
            }

            DEFAULT_VOICES_MAP.put(Integer.parseInt(String.valueOf(c)), files_for_num);
        }
    }

    public Map<Integer, String[]> getVoices() {
        return Collections.unmodifiableMap(this._voices);
    }

    public final Sample getVocalization(char num) {
        try {
            Integer.parseInt(String.valueOf(num));
        } catch (NumberFormatException var5) {
            throw new IllegalArgumentException("Expected <num> to be a number, got '" + num + "' instead.", var5);
        }

        int idx = Integer.parseInt(String.valueOf(num));
        String[] files = (String[])this._voices.get(idx);
        String filename = files[RAND.nextInt(files.length)];
        return FileUtil.readSample(filename);
    }
}
