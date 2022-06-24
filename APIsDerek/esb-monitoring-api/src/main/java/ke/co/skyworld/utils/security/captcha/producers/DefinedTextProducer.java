package ke.co.skyworld.utils.security.captcha.producers;

import nl.captcha.text.producer.TextProducer;

/**
 * skyworld-api (ke.co.skyworld.utils.security.captcha.producers)
 * Created by: elon
 * On: 09 Mar, 2020. 3:23 PM
 **/
public class DefinedTextProducer implements TextProducer {

    private String answer;

    public DefinedTextProducer(String answer){
        this.answer = answer;
    }

    @Override
    public String getText() {
        return answer;
    }
}
