package ke.co.skyworld.utils.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

/**
 * sls-api (ke.co.scedar.http_handlers)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:41 PM
 **/
public class InvalidMethod extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        send(exchange, new ExceptionRepresentation(
                "Method Not Allowed",
                exchange.getRequestURI(),
                "Method "+exchange.getRequestMethod()+" not allowed",
                StatusCodes.METHOD_NOT_ALLOWED,
                exchange.getRequestMethod()
        ), StatusCodes.METHOD_NOT_ALLOWED);
    }
}
