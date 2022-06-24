package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.captcha.ScedarCaptcha;

/**
 * skyworld-api (ke.co.skyworld.endpoints.handlers.auth)
 * Created by: elon
 * On: 09 Mar, 2020. 11:55 AM
 **/
public class ReCaptcha extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        try {
            ScedarCaptcha scedarCaptcha = new ScedarCaptcha();
            scedarCaptcha.generate();
            send(exchange, scedarCaptcha, StatusCodes.OK);

            JvmManager.gc(scedarCaptcha);

        } catch (Exception e){
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "Error occurred while generating Captcha",
                    exchange.getRequestURI(),
                    "Error: " + e.getMessage(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
