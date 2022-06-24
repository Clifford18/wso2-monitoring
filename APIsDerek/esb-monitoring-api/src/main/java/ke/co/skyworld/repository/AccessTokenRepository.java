package ke.co.skyworld.repository;

import io.undertow.server.HttpServerExchange;
import ke.co.skyworld.domain.enums.AccessTokenScope;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.security.AccessTokenGenerator;

import java.util.*;

import static ke.co.skyworld.utils.Constants.*;

/**
 * flexicore (ke.co.skyworld.repository.services)
 * Created by: elon
 * On: 28 Jul, 2019 28/07/19 22:48
 **/
public class AccessTokenRepository {

    @Deprecated
    public static FlexicoreHashMap generate(long userId, String authIpAddress, int timeToLive, String timeUnit){
        Set<AccessTokenScope> accessTokenScopes = new HashSet<>();
        accessTokenScopes.add(AccessTokenScope.GLOBAL);
        return generate(userId, authIpAddress, accessTokenScopes, timeToLive, timeUnit);
    }

    public static FlexicoreHashMap generate(long userId, String authIpAddress, Set<AccessTokenScope> accessTokenScopes, int timeToLive,
                                            String timeUnit){
        if(!allowUserMultiSessions()) {
            Repository.delete(Table.ACCESS_TOKENS,new FilterPredicate("user_id = :user_id"), new FlexicoreHashMap()
                    .addQueryArgument("user_id", userId));
        }

        String strAccessTokenScopes = serializeAccessTokenScope(accessTokenScopes);

        return Repository.insertAutoIncremented(Table.ACCESS_TOKENS,new String[]{"user_id, auth_ip_address, scope, time_to_live, time_unit"},
                new FlexicoreHashMap()
                .addQueryArgument(":user_id", userId)
                .addQueryArgument(":auth_ip_address", authIpAddress)
                .addQueryArgument(":scope", strAccessTokenScopes)
                .addQueryArgument(":time_to_live", timeToLive)
                .addQueryArgument(":time_unit", timeUnit)
        ).getSingleRecord();
    }

    @Deprecated
    public static TransactionWrapper generate(String userId, String authIpAddress){
        Set<AccessTokenScope> accessTokenScopes = new HashSet<>();
        accessTokenScopes.add(AccessTokenScope.GLOBAL);
        return generate(userId, authIpAddress, accessTokenScopes);
    }

    public static TransactionWrapper generate(String userId, String authIpAddress, Set<AccessTokenScope> accessTokenScopes){
        if(!allowUserMultiSessions()) {
            Repository.delete(Table.ACCESS_TOKENS,new FilterPredicate("user_id = :user_id"), new FlexicoreHashMap()
                    .addQueryArgument(":user_id", userId));
        }

        String strAccessTokenScopes = serializeAccessTokenScope(accessTokenScopes);

        return Repository.insertAutoIncremented(
                Table.ACCESS_TOKENS,
                new String[]{"user_id", "access_token", "time_to_live", "time_unit","auth_ip_address", "scope", "date_created"},
                new FlexicoreHashMap()
                        .addQueryArgument(":user_id", userId)
                        .addQueryArgument(":access_token", AccessTokenGenerator.getAccessToken(getAccessTokenLength()))
                        .addQueryArgument(":time_to_live", Constants.getAccessTokenTimeout())
                        .addQueryArgument(":time_unit", Constants.getAccessTokenTimeoutTimeUnit())
                        .addQueryArgument(":auth_ip_address", authIpAddress)
                        .addQueryArgument(":scope", strAccessTokenScopes)
                        .addQueryArgument(":date_created", DateTime.getCurrentDateTime()));
    }

    @Deprecated
    public static FlexicoreHashMap validate(HttpServerExchange exchange, String accessToken){
        Set<AccessTokenScope> accessTokenScopes = new HashSet<>();
        accessTokenScopes.add(AccessTokenScope.GLOBAL);
        return validate(exchange, accessTokenScopes, accessToken);
    }

    @SuppressWarnings("unchecked")
    public static FlexicoreHashMap validate(HttpServerExchange exchange, Set<AccessTokenScope> accessTokenScopes, String accessToken){

        TransactionWrapper twrapper = Repository.selectWhere(Table.ACCESS_TOKENS,
                new FilterPredicate().equalTo("access_token", ":access_token"),
                new FlexicoreHashMap()
                        .addQueryArgument(":access_token", accessToken)
        );

        FlexicoreHashMap staleAt = twrapper.getSingleRecord();

        if (staleAt != null && !staleAt.isEmpty()) {

            String dbAccessTokenScopes = staleAt.getStringValue("scope");
            List<String> dbAccessTokenScopesList = Arrays.asList(dbAccessTokenScopes.split(","));

            boolean inScope = accessTokenScopes.stream().anyMatch(accessTokenScope ->  dbAccessTokenScopesList.contains(accessTokenScope.value()));
            if(!inScope) return null;

            if (!staleAt.getValue("auth_ip_address").equals(exchange.getRequestHeaders().get("X-Real-IP").getFirst())) {
                return staleAt.putValue("ip_mismatch", "Remote address used to log in differs from current remote address. Kindly login again");
            }

            long accessTokenTime = ((Date) (staleAt.getValue("date_created"))).getTime();

            long currentTime = DateTime.getCurrentUnixTimestamp();
            long delay = getDelay(staleAt.getValue("time_unit"),
                    Long.parseLong(staleAt.getValue("time_to_live").toString()));

            if ((accessTokenTime + delay) <= currentTime) {
                revoke(staleAt.getValue("access_token"));
                return null;
            } else return staleAt;
        } else {
            return null;
        }
    }

    public static FlexicoreHashMap refresh(HttpServerExchange exchange){
        String accessToken = exchange.getRequestHeaders().get("Authorization").getFirst().replace(
                        "Bearer ", "");
        FlexicoreHashMap staleAt = validate(exchange, accessToken);

        if(staleAt != null){
            String strAccessTokenScopes = staleAt.getStringValue("scope");
            revoke(accessToken);
            FlexicoreHashMap queryArguments = new FlexicoreHashMap()
                    .addQueryArgument(":user_id", staleAt.getValue("user_id"))
                    .addQueryArgument(":auth_ip_address", exchange.getRequestHeaders().get("X-Real-IP").getFirst())
                    .addQueryArgument(":scope", strAccessTokenScopes)
                    .addQueryArgument(":access_token", AccessTokenGenerator.getAccessToken(getAccessTokenLength()))
                    .addQueryArgument(":time_to_live", Constants.getAccessTokenTimeout())
                    .addQueryArgument(":time_unit", Constants.getAccessTokenTimeoutTimeUnit())
                    .addQueryArgument(":date_created", DateTime.getCurrentDateTime());

            return Repository.insertAutoIncremented(Table.ACCESS_TOKENS,
                    new String[]{"user_id", "access_token", "scope", "auth_ip_address", "time_to_live", "time_unit", "date_created"},
                    queryArguments).getSingleRecord();
        }

        return null;
    }

    public static void revoke(String accessToken){
        Repository.delete(Table.ACCESS_TOKENS,new FilterPredicate("access_token = :access_token"), new FlexicoreHashMap()
        .addQueryArgument(":access_token",accessToken));
    }

    public static void revokeUser(String userId){
        Repository.delete(Table.ACCESS_TOKENS,new FilterPredicate("user_id = :user_id"), new FlexicoreHashMap()
                .addQueryArgument(":user_id", userId));
    }

    public static FlexicoreArrayList getAccessTokens(String userId){
        return (FlexicoreArrayList) Repository.selectWhere(Table.ACCESS_TOKENS,new FilterPredicate("user_id = :user_id"), new FlexicoreHashMap()
                .addQueryArgument(":user_id", userId)).getData();
    }

    public static FlexicoreHashMap getAccessToken(String accessToken){
        return Repository.selectWhere(Table.ACCESS_TOKENS,new FilterPredicate("access_token = :access_token"), new FlexicoreHashMap()
                .addQueryArgument(":access_token",accessToken)).getSingleRecord();
    }

    public static long getDelay(String timeUnit, long expiry){
        switch (timeUnit) {
            case "MINUTES":
                return expiry * 1000 * 60;
            case "HOURS":
                return expiry * 1000 * 60 * 60;
            case "DAYS":
                return expiry * 1000 * 60 * 60 * 24;
            default:
                return expiry * 1000; //SECONDS
        }
    }

    private static String serializeAccessTokenScope(Set<AccessTokenScope> accessTokenScopes){
        StringBuilder strAccessTokenScopes = new StringBuilder();
        boolean addComma = false;
        for (AccessTokenScope accessTokenScope : accessTokenScopes) {
            if(addComma) strAccessTokenScopes.append(","); else addComma = true;
            strAccessTokenScopes.append(accessTokenScope.value());
        }
        return strAccessTokenScopes.toString();
    }
}
