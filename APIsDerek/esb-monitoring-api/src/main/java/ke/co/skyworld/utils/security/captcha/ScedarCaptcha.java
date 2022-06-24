package ke.co.skyworld.utils.security.captcha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.data_formatting.Converter;
import ke.co.skyworld.utils.memory.InMemoryCache;
import ke.co.skyworld.utils.security.HashUtils;
import ke.co.skyworld.utils.security.ScedarUID;
import ke.co.skyworld.utils.security.captcha.producers.DefinedTextProducer;
import ke.co.skyworld.utils.security.captcha.producers.NumericVoiceProducer;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.NumbersAnswerProducer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;

/**
 * skyworld-api (ke.co.skyworld.utils.security)
 * Created by: elon
 * On: 09 Mar, 2020. 10:57 AM
 **/
public class ScedarCaptcha {

    @JsonIgnore
    private Captcha captcha;
    @JsonIgnore
    private AudioCaptcha ac;

    private String image_captcha;
    private String audio_captcha;
    private String captcha_source_reference;
    private int captcha_width;
    private int captcha_height;
    private int captcha_length;

    @JsonIgnore
    private boolean addBackground;
    @JsonIgnore
    private boolean addBorder;

    private boolean is_captcha_alphanumeric;

    @JsonIgnore
    private int captchaNoiseLevel;

    private boolean do_audio_captcha;
    private int captcha_ttl;

    @JsonIgnore
    private final char[] DEFAULT_CHARS =
            new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y', '2', '3', '4', '5', '6', '7', '8'};

    public ScedarCaptcha() {
        captcha_width = Constants.getCaptchaWidth();
        captcha_height = Constants.getCaptchaHeight();
        captcha_length = Constants.getCaptchaLength();
        addBackground = Constants.getCaptchaBackground();
        addBorder = Constants.getCaptchaBorder();
        is_captcha_alphanumeric = Constants.getCaptchaAlphanumeric();
        captchaNoiseLevel = Constants.getCaptchaNoiseLevel();
        do_audio_captcha = Constants.doWeDoCaptchaAudio();
        captcha_ttl = Constants.getCaptchaTtl();
    }

    public void generate() throws Exception {
        try {
            Captcha.Builder captchaBuilder = new Captcha.Builder(captcha_width, captcha_height);

            if(is_captcha_alphanumeric) {
                captchaBuilder.addText(new DefaultTextProducer(captcha_length, DEFAULT_CHARS));
            } else {
                captchaBuilder.addText(new NumbersAnswerProducer(captcha_length));
            }

            if(addBackground) captchaBuilder.addBackground();
            if(addBorder) captchaBuilder.addBorder();

            for (int i = 0; i < captchaNoiseLevel ; i++) {
                captchaBuilder.addNoise();
            }

            captcha = captchaBuilder.build();

            String imageCaptchaAnswer = captcha.getAnswer();
            String captchaSourceReference = ScedarUID.generateUid(30);
            captcha_source_reference = captchaSourceReference;
            String imageCaptchaAnswerWithSourceReference = imageCaptchaAnswer+"_"+captchaSourceReference;
            image_captcha = Converter.imgToBase64String(captcha.getImage(), "png");

            InMemoryCache.store(imageCaptchaAnswerWithSourceReference, "IMAGE_CAPTCHA", captcha_ttl);

            if(do_audio_captcha){
                if(!is_captcha_alphanumeric){
                    AudioCaptcha.Builder audioCaptchaBuilder = new AudioCaptcha.Builder();

                    audioCaptchaBuilder.addAnswer(new DefinedTextProducer(imageCaptchaAnswer));
                    audioCaptchaBuilder.addVoice(new NumericVoiceProducer(imageCaptchaAnswer));

                    for (int i = 0; i < captchaNoiseLevel ; i++) {
                        audioCaptchaBuilder.addNoise();
                    }
                    ac = audioCaptchaBuilder.build();

                    AudioInputStream audioInputStream = ac.getChallenge().getAudioInputStream();
                    ByteArrayOutputStream audioByteArrayOS = new ByteArrayOutputStream();
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE,audioByteArrayOS);

                    audio_captcha = HashUtils.base64Encode(audioByteArrayOS.toByteArray());
                } else {
                    throw new Exception("Audio Captcha for Alphanumeric Answers not yet supported");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public String getImage_captcha() {
        return image_captcha;
    }

    public String getAudio_captcha() {
        return audio_captcha;
    }

    public int getCaptcha_width() {
        return captcha_width;
    }

    public int getCaptcha_height() {
        return captcha_height;
    }

    public int getCaptcha_length() {
        return captcha_length;
    }

    public boolean isIs_captcha_alphanumeric() {
        return is_captcha_alphanumeric;
    }

    public boolean isDo_audio_captcha() {
        return do_audio_captcha;
    }

    public int getCaptcha_ttl() {
        return captcha_ttl;
    }

    public String getCaptcha_source_reference() {
        return captcha_source_reference;
    }
}
