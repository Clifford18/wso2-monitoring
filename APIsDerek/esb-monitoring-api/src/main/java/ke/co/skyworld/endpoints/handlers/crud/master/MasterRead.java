package ke.co.skyworld.endpoints.handlers.crud.master;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.endpoints.handlers.crud.master.portal.MemberRead;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

import static ke.co.skyworld.utils.Constants.REQ_PORTAL;
import static ke.co.skyworld.utils.Constants.Table.*;

public class MasterRead extends ScedarHttpHandler {

    private String table;
    private boolean pageable;

    public MasterRead(String table){
        this.table = table;
        this.pageable = false;
    }

    public MasterRead(String table, boolean pageable){
        this.table = table;
        this.pageable = pageable;
    }


    @Override
    @SuppressWarnings("Duplicates,unchecked")
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        if(requestIsNotAuthentic(exchange)) return;

        switch (table) {
            case ACCESS_TOKENS:
            case APP_USER_GROUPS:
            case APP_USER_RIGHTS:
            case APPLICATION_WORKFLOW_LOGS:
            case APPLICATION_WORKFLOWS:
            case APPLICATIONS:
            case DESIGNATIONS:
            case GENDERS:
            case REQUEST_LOGS:
            case USER_ACCOUNTS:
            case USER_APP_USER_GROUPS:
            {
                new MemberRead<>(this, table, pageable).process(exchange);
                break;
            }

            default: {
                send(exchange, new ExceptionRepresentation(
                        "Access to this resource is restricted",
                        exchange.getRequestURI(),
                        "Access denied",
                        StatusCodes.FORBIDDEN,
                        exchange.getRequestMethod()
                ), StatusCodes.FORBIDDEN);
            }
        }
    }
}
