package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

/**
 * sls-api (ke.co.scedar.endpoints.handlers.auth)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:43 PM
 **/
public class RefreshToken extends ScedarHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        if(requestIsNotAuthentic(exchange)) return;

        try {
            send(exchange, AccessTokenRepository.refresh(exchange),
                    StatusCodes.OK);
        } catch (Exception e){
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "An error occurred while processing your request. Kindly retry later",
                    exchange.getRequestURI(),
                    "Error: "+e.getMessage(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
