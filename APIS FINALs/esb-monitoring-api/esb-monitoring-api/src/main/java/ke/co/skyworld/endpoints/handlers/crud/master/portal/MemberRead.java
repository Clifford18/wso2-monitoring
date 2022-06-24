package ke.co.skyworld.endpoints.handlers.crud.master.portal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.endpoints.handlers.crud.Read;
import ke.co.skyworld.repository.Q;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.Column;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static ke.co.skyworld.utils.Constants.APP_CODE_ATM_LOGS;
import static ke.co.skyworld.utils.Constants.APP_CODE_MEMBERS_PORTAL_LOGS;
import static ke.co.skyworld.utils.Constants.Table.REQUEST_LOGS;
import static ke.co.skyworld.utils.Constants.Table.USER_ACCOUNTS;
import static ke.co.skyworld.utils.http.ScedarHttpHandler.getQueryParam;

/**
 * skyworld-api (ke.co.skyworld.endpoints.handlers.crud.master.portal)
 * Created by: elon
 * On: 19 Apr, 2020. 16:40
 **/
public class MemberRead<T extends ScedarHttpHandler> {

    private String table;
    private boolean pageable;
    private T context;

    public MemberRead(T context, String table, boolean pageable){
        this.context = context;
        this.table = table;
        this.pageable = pageable;
    }

    public void process(HttpServerExchange exchange){
        QueryBuilder queryBuilder = null;
        FlexicoreHashMap queryArguments = null;
        Read read = null;

        if(!isRequestValid(exchange)) return;

        try {

            String appCode = exchange.getQueryParameters().get("appCode").getFirst().trim();

            boolean appCodeExists = Repository.exists(Constants.Table.APPLICATIONS,
                    new FilterPredicate("app_code = :app_code"),
                    new FlexicoreHashMap().addQueryArgument(":app_code", appCode));
            if (!appCodeExists) {
                context.send(exchange, new ExceptionRepresentation(
                        "Error: Unknown Application Code '" + appCode + "'",
                        exchange.getRequestURI(),
                        "Unknown Application Code",
                        StatusCodes.NOT_FOUND,
                        exchange.getRequestMethod()
                ), StatusCodes.NOT_FOUND);
                return;
            }

            //check rights
            switch (appCode){
                case APP_CODE_ATM_LOGS: {
                    if(context.insufficientAppRights(exchange, "101:VIEW")){
                        return;
                    }
                    break;
                }

                case APP_CODE_MEMBERS_PORTAL_LOGS: {
                    if(context.insufficientAppRights(exchange, "201:VIEW")){
                        return;
                    }
                    break;
                }
            }

            if(!pageable){
                String id = context.getPathVar(exchange, "id");

                FlexicoreHashMap variable = null;

                switch (table){

                    case REQUEST_LOGS:
                    {
                        queryBuilder = new QueryBuilder()
                                .select().from(table)
                                .where("request_id = :request_id and app_code = :app_code");
                        queryArguments = new FlexicoreHashMap()
                                .addQueryArgument(":request_id",  id)
                                .addQueryArgument(":app_code", appCode);
                        break;
                    }

                    default: {
                        queryBuilder = new QueryBuilder()
                                .select().from(table)
                                .where(Q.getPKField(table)+" = :pk_field");
                        queryArguments = new FlexicoreHashMap()
                                .addQueryArgument(":pk_field",  id);
                    }
                }

                variable = Repository.select(queryBuilder, queryArguments).getSingleRecord();

                if(variable == null || variable.isEmpty()){
                    context.send(exchange, new ExceptionRepresentation(
                            table+" not found",
                            exchange.getRequestURI(),
                            table+" not found",
                            StatusCodes.NOT_FOUND,
                            exchange.getRequestMethod()
                    ), StatusCodes.NOT_FOUND);
                    return;
                }

                context.send(exchange, variable, StatusCodes.OK);
                JvmManager.gc(id, exchange, variable, queryArguments, queryBuilder);

            } else {
                switch (table){
                    case REQUEST_LOGS:
                    {
                        read = new Read(table)
                                .setInternalServerErrorMessage("Error retrieving records")
                                .setNotFoundErrorMessage("Records not found")
                                .setFilterPredicate(
                                        new FilterPredicate("app_code = :app_code"))
                                .setQueryArguments(new FlexicoreHashMap()
                                        .addQueryArgument(":app_code", appCode))
                                .getBatchRecords(exchange);
                        return;
                    }
                    default:
                    {
                        read = new Read(table)
                                .setInternalServerErrorMessage("Error retrieving records")
                                .setNotFoundErrorMessage("Records not found")
                                .getBatchRecords(exchange);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            context.send(exchange, new ExceptionRepresentation(
                    "An error occurred while processing your request. Kindly retry later",
                    exchange.getRequestURI(),
                    "Error: "+e.getMessage(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isRequestValid(HttpServerExchange exchange){
        Deque<String> dAppCode = exchange.getQueryParameters().get("appCode");

        if(dAppCode == null || dAppCode.getFirst().equals("")){
            context.send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'appCode' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }
        return true;
    }
}
