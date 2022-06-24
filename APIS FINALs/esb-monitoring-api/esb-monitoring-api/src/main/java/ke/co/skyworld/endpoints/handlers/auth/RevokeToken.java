package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

import java.util.Deque;

/**
 * sls-api (ke.co.scedar.endpoints.handlers.auth)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:44 PM
 **/
public class RevokeToken extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        try {
            /*Deque<String> dPortal = exchange.getQueryParameters().get("portal");
            if(dPortal == null || dPortal.getFirst().equals("")){
                send(exchange, new ExceptionRepresentation(
                        "Missing GET parameter",
                        exchange.getRequestURI(),
                        "GET parameter 'portal' is required",
                        StatusCodes.BAD_REQUEST,
                        exchange.getRequestMethod()
                ), StatusCodes.BAD_REQUEST);
                return;
            }

            String portal = dPortal.getFirst().trim().toLowerCase();

            if(!(portal.equals("member") || portal.equals("partner") || portal.equals("admin"))){
                send(exchange, new ExceptionRepresentation(
                        "Invalid portal",
                        exchange.getRequestURI(),
                        "portal '"+portal+"' is invalid. Use 'member', 'partner' or 'admin'",
                        StatusCodes.BAD_REQUEST,
                        exchange.getRequestMethod()
                ), StatusCodes.BAD_REQUEST);
                return;
            }*/

            HeaderValues authorizationHeader = exchange.getRequestHeaders()
                    .get("Authorization");

            if(authorizationHeader != null){
                AccessTokenRepository.revoke(authorizationHeader
                        .getFirst().replace("Bearer ", ""));
                send(exchange, true, StatusCodes.OK);
            }
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
