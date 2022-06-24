package ke.co.skyworld.endpoints.handlers.users.me;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.SystemSettingsRepository;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.Misc;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.HashUtils;

import static ke.co.skyworld.utils.Constants.REQ_PORTAL;
import static ke.co.skyworld.utils.Constants.REQ_PORTAL_TYPE_ID;

public class UpdateMe extends ScedarHttpHandler {

    @Override
    @SuppressWarnings("Duplicates")
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        if (requestIsNotAuthentic(exchange)) return;

        try {
            FlexicoreHashMap update = (FlexicoreHashMap) getBodyObject(exchange, FlexicoreHashMap.class);

            if (update == null) {
                send(exchange, new ExceptionRepresentation(
                        exchange.getQueryParameters().get(Constants.MARSHALL_ERROR).getFirst(),
                        exchange.getRequestURI(),
                        "Error: Unable to understand payload.",
                        StatusCodes.INTERNAL_SERVER_ERROR,
                        exchange.getRequestMethod()
                ), StatusCodes.INTERNAL_SERVER_ERROR);
                return;
            }

            FlexicoreHashMap stale = Repository.selectWhere(Constants.Table.USER_ACCOUNTS,
                    new FilterPredicate("user_id = :user_id"),
                    new FlexicoreHashMap().addQueryArgument(":user_id",
                            getQueryParam(exchange, Constants.REQ_USER_ID))).getSingleRecord();

            try {
                if (stale == null || stale.isEmpty()) {
                    send(exchange, new ExceptionRepresentation(
                            "Error: Unable to update your details",
                            exchange.getRequestURI(),
                            "Unable to determine account",
                            StatusCodes.INTERNAL_SERVER_ERROR,
                            exchange.getRequestMethod()
                    ), StatusCodes.INTERNAL_SERVER_ERROR);
                    return;
                }

                //Validate new password
                if (update.getValue("user_pwd") != null) {
                    try {
                        String password = update.getValue("user_pwd");
                        FlexicoreHashMap passwordPolicy = SystemSettingsRepository.getPasswordPolicyRegex();

                        if(Misc.validateString(password, passwordPolicy.getStringValue("password_policy_regex"))){
                            update.putValue("user_pwd",
                                    HashUtils.SHA256(update.getValue("user_pwd").toString()));
                            AccessTokenRepository.revokeUser(getQueryParam(exchange, Constants.REQ_USER_ID));
                        } else {
                            throw new Exception("Invalid Password. "+passwordPolicy.getStringValue("password_policy_description"));
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        send(exchange, new ExceptionRepresentation(
                                "Error updating password.",
                                exchange.getRequestURI(),
                                "Error: "+e.getMessage(),
                                StatusCodes.INTERNAL_SERVER_ERROR,
                                exchange.getRequestMethod()
                        ), StatusCodes.INTERNAL_SERVER_ERROR);
                        return;
                    }
                }

                TransactionWrapper wrapper = Repository.update(Constants.Table.USER_ACCOUNTS, update,
                        new FilterPredicate("user_id = :user_id"),
                        new FlexicoreHashMap().addQueryArgument(":user_id", getQueryParam(exchange, Constants.REQ_USER_ID)));
                update.putValue("pkValues", wrapper.getSingleRecord().getValue("pkValues"));

                if (wrapper.hasErrors()) {
                    send(exchange, new ExceptionRepresentation(
                            "Error: Unable to update your details",
                            exchange.getRequestURI(),
                            wrapper.getErrors(),
                            StatusCodes.INTERNAL_SERVER_ERROR,
                            exchange.getRequestMethod()
                    ), StatusCodes.INTERNAL_SERVER_ERROR);
                    return;
                }

                send(exchange, wrapper.getSingleRecord(), StatusCodes.OK);
                JvmManager.gc(exchange, wrapper, update, stale);

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, new ExceptionRepresentation(
                        "Error: Unable to update your details",
                        exchange.getRequestURI(),
                        "Unknown Error",
                        StatusCodes.INTERNAL_SERVER_ERROR,
                        exchange.getRequestMethod()
                ), StatusCodes.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "An error occurred while processing your request. Kindly retry later",
                    exchange.getRequestURI(),
                    "Error: " + e.getMessage(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
