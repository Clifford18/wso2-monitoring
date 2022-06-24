package ke.co.skyworld.endpoints.handlers.sys;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.AccessTokenScope;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

import static ke.co.skyworld.utils.Constants.REQ_PORTAL;
import static ke.co.skyworld.utils.Constants.REQ_PORTAL_TYPE_ID;

/**
 * skyworld-api (ke.co.skyworld.endpoints.handlers.sys)
 * Created by: elon
 * On: 16 Jan, 2021. 19:39
 **/
public class ReadPasswordPolicy extends ScedarHttpHandler {

    private final boolean global;

    public ReadPasswordPolicy(boolean global){
        this.global = global;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        printRequestInfo(exchange);

        if (requestIsNotAuthentic(exchange, AccessTokenScope.PRE_ACCESS, AccessTokenScope.GLOBAL)) return;

        try {
            PortalTypes portal = PortalTypes.valueOf(getQueryParam(exchange, REQ_PORTAL));
            String portalTypeId = getQueryParam(exchange, REQ_PORTAL_TYPE_ID);

            FilterPredicate filterPredicate;
            FlexicoreHashMap queryVariables = new FlexicoreHashMap();
            queryVariables.addQueryArgument(":system_settings_type", Constants.SST_PASSWORD_POLICY);

            if(global){
                filterPredicate = new FilterPredicate(
                        "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope"
                );
                queryVariables.addQueryArgument(":system_settings_scope", Constants.SSS_GLOBAL);

            } else {
                filterPredicate = new FilterPredicate(
                        "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope " +
                                "AND system_settings_identifier = :system_settings_identifier"
                );
                queryVariables.addQueryArgument(":system_settings_scope", portal.value());
                queryVariables.addQueryArgument(":system_settings_identifier", portalTypeId);
            }

            TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                    filterPredicate, queryVariables);

            if (wrapper.hasErrors()) {
                send(exchange, new ExceptionRepresentation(
                        "Error: Unable to retrieve Password Policy.",
                        exchange.getRequestURI(),
                        wrapper.getErrors(),
                        StatusCodes.INTERNAL_SERVER_ERROR,
                        exchange.getRequestMethod()
                ), StatusCodes.INTERNAL_SERVER_ERROR);
                return;
            }

            if (wrapper.getSingleRecord() == null) {
                send(exchange, new ExceptionRepresentation(
                        "Error: Password Policy NOT set.",
                        exchange.getRequestURI(),
                        "Password Policy NOT set.",
                        StatusCodes.NOT_FOUND,
                        exchange.getRequestMethod()
                ), StatusCodes.NOT_FOUND);
                return;
            }
            
            FlexicoreArrayList systemSettings = (FlexicoreArrayList) wrapper.getData();
            if(!systemSettings.isEmpty()){
                String passwordPolicyXML = wrapper.getSingleRecord().getStringValue("system_settings_xml");
                send(exchange, passwordPolicyXML, StatusCodes.OK, Constants.applicationXml);
            } else {
                send(exchange, new ExceptionRepresentation(
                        "Error: Unable to retrieve Password Policy.",
                        exchange.getRequestURI(),
                        wrapper.getErrors(),
                        StatusCodes.NOT_FOUND,
                        exchange.getRequestMethod()
                ), StatusCodes.NOT_FOUND);
            }
            
        } catch (Exception e){
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "Internal Server Error ("+e.getMessage()+")",
                    exchange.getRequestURI(),
                    "Error: "+e.getMessage()+". Caused by: "+e.getCause(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
