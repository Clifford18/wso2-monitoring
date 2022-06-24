package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.UserAccountRepository;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;

import java.util.Deque;

public class VerifyPassword extends ScedarHttpHandler {

    @Override
    @SuppressWarnings("unchecked")
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        if(!isRequestValid(exchange)) return;
        if(requestIsNotAuthentic(exchange)) return;

        try {
            String authHash = exchange.getQueryParameters().get("auth_hash").getFirst();

            QueryBuilder queryBuilder = new QueryBuilder()
                    .select()
                    .selectColumn("username")
                    .selectColumn("user_pwd")
                    .from(Constants.Table.USER_ACCOUNTS)
                    .where("user_id = :user_id");
            FlexicoreHashMap queryArguments = new FlexicoreHashMap()
                    .addQueryArgument(":user_id", getQueryParam(exchange, Constants.REQ_USER_ID));

            FlexicoreHashMap results = Repository.select(queryBuilder, queryArguments).getSingleRecord();

            if(results == null || results.isEmpty()){
                send(exchange, new ExceptionRepresentation(
                        "User cannot be determined",
                        exchange.getRequestURI(),
                        "User cannot be determined",
                        StatusCodes.BAD_REQUEST,
                        exchange.getRequestMethod()
                ), StatusCodes.BAD_REQUEST);
                return;
            }

            String correctDetails = results.getValue("user_pwd").toString();

            send(exchange, UserAccountRepository.checkAuthHash(authHash, correctDetails), StatusCodes.OK);

            JvmManager.gc(queryArguments, queryBuilder, results);
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

    private boolean isRequestValid(HttpServerExchange exchange){

        Deque<String> dAuthHash = exchange.getQueryParameters().get("auth_hash");

        if(dAuthHash == null || dAuthHash.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'auth_hash' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }
        return true;
    }
}
