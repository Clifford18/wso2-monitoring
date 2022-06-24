package ke.co.skyworld.utils.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

/**
 * sls-api (ke.co.scedar.http_handlers)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:52 PM
 **/
public class FallBack extends ScedarHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        send(exchange, new ExceptionRepresentation(
                "URI Not Found",
                exchange.getRequestURI(),
                "URI "+exchange.getRequestURI()+" not found on server",
                StatusCodes.NOT_FOUND,
                exchange.getRequestMethod()
        ), StatusCodes.NOT_FOUND);
    }
}
