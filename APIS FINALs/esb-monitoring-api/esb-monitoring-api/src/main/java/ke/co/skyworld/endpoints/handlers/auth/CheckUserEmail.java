package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

import java.util.Deque;

public class CheckUserEmail extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        if (!isRequestValid(exchange)) return;

        try {
            String email = exchange.getQueryParameters().get("email").getFirst();

            FilterPredicate filterPredicate = new FilterPredicate("email_address = :email_address");
            FlexicoreHashMap queryArguments = new FlexicoreHashMap()
                    .addQueryArgument(":email_address", email);

            send(exchange, Repository.exists(Constants.Table.USER_ACCOUNTS, filterPredicate, queryArguments), StatusCodes.OK);
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

    private boolean isRequestValid(HttpServerExchange exchange) {
        Deque<String> dEmail = exchange.getQueryParameters().get("email");

        if (dEmail == null || dEmail.getFirst().equals("")) {
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'email' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        return true;
    }
}
