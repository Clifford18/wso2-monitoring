package ke.co.skyworld.endpoints.handlers.users.me;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.AccessTokenScope;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.Column;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;

import static ke.co.skyworld.utils.Constants.REQ_ACCESS_TOKEN;
import static ke.co.skyworld.utils.Constants.REQ_PORTAL;

public class Me extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        if(requestIsNotAuthentic(exchange, AccessTokenScope.PRE_ACCESS, AccessTokenScope.GLOBAL)) return;

        try {
            FlexicoreHashMap userAccount = Repository.selectWhere(Constants.Table.USER_ACCOUNTS,
                    new FilterPredicate("user_id = :user_id"),
                    new FlexicoreHashMap()
                            .addQueryArgument(":user_id", getQueryParam(exchange, Constants.REQ_USER_ID)))
                    .getSingleRecord();

            FlexicoreArrayList userRights = null;
            FlexicoreArrayList userGroups = (FlexicoreArrayList) Repository.selectWhere(Constants.Table.APP_USER_GROUPS,
                    new FilterPredicate("group_id IN ("+getQueryParam(exchange, Constants.REQ_GROUP_IDS)+")"),
                    new FlexicoreHashMap().addQueryArgument(":group_ids", getQueryParam(exchange, Constants.REQ_GROUP_IDS)))
                    .getData();

            for (FlexicoreHashMap userGroup : userGroups){
                userRights = (FlexicoreArrayList) Repository.selectWhere(Constants.Table.APP_USER_RIGHTS,
                        new FilterPredicate().equalTo("group_id", ":group_id"),
                        new FlexicoreHashMap().addQueryArgument(":group_id",
                                userGroup.getStringValue("group_id"))).getData();
                userGroup.putValue("app_user_rights_by_group_id", userRights);
            }

            userAccount.putValue("user_user_groups", userGroups);
            userAccount.removeColumn("user_pwd");
            userAccount.removeColumn("reset_pwd_verification_code");
            userAccount.putValue("access_token", AccessTokenRepository.getAccessToken(getQueryParam(exchange, REQ_ACCESS_TOKEN)));

            send(exchange, userAccount, StatusCodes.OK);

            JvmManager.gc(exchange, userAccount, userGroups, userRights);
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
