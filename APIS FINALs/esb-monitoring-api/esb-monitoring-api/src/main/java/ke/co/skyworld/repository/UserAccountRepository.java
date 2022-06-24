package ke.co.skyworld.repository;

import ke.co.skyworld.domain.beans.AuthenticationStatus;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.HashUtils;
import ke.co.skyworld.utils.security.ScedarUID;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import static ke.co.skyworld.utils.Constants.*;

/**
 * sls-api (ke.co.scedar.repository)
 * Created by: elon
 * On: 04 Jul, 2018 7/4/18 9:56 PM
 **/
public class UserAccountRepository {

    public static AuthenticationStatus authenticate(String username, String authHash, String remoteAddr){
        QueryBuilder queryBuilder;
        FilterPredicate filterPredicate = null;
        FlexicoreHashMap updateSet = null;
        FlexicoreHashMap queryArguments = null;
        TransactionWrapper transactionWrapper = null;

        AuthenticationStatus authenticationStatus =
                new AuthenticationStatus(false,
                        "Unknown error occurred. Kindly retry later", null);
        try {
            //Get User
            queryBuilder = new QueryBuilder()
                    .select()
                    .selectColumn("ua.user_status AS user_status")
                    .selectColumn("ua.account_access_mode AS account_access_mode")
                    .selectColumn("ua.user_pwd AS user_pwd")
                    .selectColumn("ua.user_pwd_status AS user_pwd_status")
                    .selectColumn("ua.allowed_access_sources_status AS allowed_access_sources_status")
                    .selectColumn("ua.allowed_access_sources AS allowed_access_sources")
                    .selectColumn("ua.restricted_access_sources_status AS restricted_access_sources_status")
                    .selectColumn("ua.restricted_access_sources AS restricted_access_sources")
                    .selectColumn("ua.user_id AS user_id")
                    .selectColumn("ua.login_attempts AS login_attempts")
                    .selectColumn("ua.max_login_attempts AS max_login_attempts")
                    .from()
                    .joinPhrase(Q.getParentDatabase(Table.USER_ACCOUNTS)+"."+Table.USER_ACCOUNTS+" AS ua ")
                    .where("ua.username = :username");

            queryArguments = new FlexicoreHashMap().addQueryArgument(":username", username);

            FlexicoreHashMap userAccount = Repository.joinSelectQuery(queryBuilder, queryArguments).getSingleRecord();

            if(userAccount == null || userAccount.isEmpty()){
                authenticationStatus.setMessage("Invalid username and/or password");
                return authenticationStatus;
            }

            int loginAttempts = Integer.parseInt(userAccount.getValue("login_attempts").toString());
            int maxLoginAttempts = Integer.parseInt(userAccount.getValue("max_login_attempts").toString());

            if(!userAccount.isEmpty()){

                if(loginAttempts == maxLoginAttempts){
                    //TODO: Alert Sys Admin
                    authenticationStatus.setMessage("Max login attempts reached. Access Denied!");
                    return authenticationStatus;
                }else{
                    loginAttempts += 1;
                    int finalLoginAttempts = loginAttempts;

                    filterPredicate = new FilterPredicate("user_id = :user_id");
                    queryArguments = new FlexicoreHashMap()
                            .addQueryArgument(":user_id", userAccount.getValue("user_id"));
                    updateSet = new FlexicoreHashMap()
                            .putValue("login_attempts", finalLoginAttempts);

                    transactionWrapper = Repository.update(Table.USER_ACCOUNTS, updateSet, filterPredicate, queryArguments);

                    if(transactionWrapper.hasErrors()){
                        authenticationStatus.setMessage("Error: "+transactionWrapper.getErrors());
                        return authenticationStatus;
                    }
                }

                String correctDetails = username + "::" + userAccount.getValue("user_pwd");

                boolean isAuthentic = checkAuthHash(authHash, correctDetails);

                if(isAuthentic && userAccount.getValue("user_status").toString().equals(STATUS_ACTIVE)){

                    if(userAccount.getValue("allowed_access_sources_status").toString().equals(STATUS_ACTIVE) &&
                            !userAccount.getValue("allowed_access_sources").toString().contains(remoteAddr)){
                        authenticationStatus
                                .setMessage("Sorry, you are not allowed to access this account from " +
                                        "'"+remoteAddr+"' - Access Denied!");
                    }else if(userAccount.getValue("restricted_access_sources_status").toString().equals(STATUS_ACTIVE) &&
                            userAccount.getValue("restricted_access_sources").toString().contains(remoteAddr)){
                        authenticationStatus
                                .setMessage("Sorry, you are not allowed to access this account from " +
                                        "'"+remoteAddr+"' - Access Denied!");
                    }else if(!(userAccount.getValue("account_access_mode").toString().equals(ACCOUNT_ACCESS_MODE_INTERACTIVE) ||
                            userAccount.getValue("account_access_mode").toString().equals(ACCOUNT_ACCESS_MODE_HYBRID))){
                        authenticationStatus
                                .setMessage("Sorry, you are not allowed to login interactively " +
                                        "with this account. Access Denied!");
                    }else{

                        userAccount = Repository.selectWhere(Table.USER_ACCOUNTS,
                                new FilterPredicate().equalTo("user_id", ":user_id"),
                                new FlexicoreHashMap().addQueryArgument(":user_id",
                                        userAccount.getValue("user_id").toString())).getSingleRecord();

                        FlexicoreArrayList userGroups = (FlexicoreArrayList) Repository.selectWhere(Table.USER_APP_USER_GROUPS,
                                new FilterPredicate().equalTo("user_id", ":user_id"),
                                new FlexicoreHashMap().addQueryArgument(":user_id",
                                        userAccount.getValue("user_id").toString())).getData();

                        for (FlexicoreHashMap userGroup : userGroups){
                            userGroup.removeColumn("user_id");
                            FlexicoreArrayList userRights = (FlexicoreArrayList) Repository.selectWhere(Table.APP_USER_RIGHTS,
                                    new FilterPredicate().equalTo("group_id", ":group_id"),
                                    new FlexicoreHashMap().addQueryArgument(":group_id",
                                            userGroup.getValue("group_id").toString())).getData();
                            userGroup.putValue("app_user_rights_by_group_id", userRights);
                        }

                        userAccount.putValue("app_user_groups", userGroups);
                        userAccount.removeColumn("user_pwd");

                        authenticationStatus.setAuthenticated(true);
                        authenticationStatus.setMessage("Authentication successful");
                        authenticationStatus.setUser_account(userAccount);

                        updateSet = new FlexicoreHashMap()
                                .putValue("login_attempts", "0")
                                .putValue("tracking_id", ScedarUID.generateUid(32).toLowerCase())
                                .putValue("tracking_source_ip", remoteAddr)
                                .putValue("tracking_time", DateTime.getCurrentDateTime());
                        filterPredicate = new FilterPredicate("user_id = :user_id");
                        queryArguments = new FlexicoreHashMap()
                                .addQueryArgument(":user_id", userAccount.getValue("user_id"));

                        transactionWrapper = Repository.update(Table.USER_ACCOUNTS, updateSet, filterPredicate, queryArguments);

                        if(transactionWrapper.hasErrors()){
                            authenticationStatus.setMessage("Error: "+transactionWrapper.getErrors());
                        }
                    }
                }else if(!isAuthentic && userAccount.getValue("user_status").toString().equals(STATUS_ACTIVE)){
                    authenticationStatus.setMessage("Invalid username and/or password");
                }else if(userAccount.getValue("user_status").toString().equals(STATUS_INACTIVE)){
                    authenticationStatus.setMessage("Sorry, your account is Disabled. Access Denied!");
                }else if(userAccount.getValue("user_status").toString().equals(STATUS_SUSPENDED)){
                    authenticationStatus.setMessage("Sorry, your account is Suspended. Access Denied!");
                }else if(userAccount.getValue("user_status").toString().equals(STATUS_LOCKED)){
                    authenticationStatus.setMessage("Sorry, your account is Locked. Access Denied!");
                }else {
                    authenticationStatus.setMessage("Sorry, your account Status could not be determined. Access Denied!");
                }
            }else{
                authenticationStatus.setMessage("Invalid username and/or password");
            }
            JvmManager.gc(queryBuilder, filterPredicate, updateSet, queryArguments, transactionWrapper);
        } catch (Exception e){
            e.printStackTrace();
            authenticationStatus.setMessage("Unknown error occurred while processing your request. Please try again later");
        }

        return authenticationStatus;
    }

    public static FlexicoreHashMap getAuthenticatedUser(String authenticatedUserId){
        FlexicoreHashMap authenticatedUser = new FlexicoreHashMap();
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select()
                .selectColumns(
                        "ua.user_id AS user_id, " +
                                "ua.username AS username, " +
                                "ua.full_name AS full_name, " +
                                "ua.user_pwd AS user_pwd, " +
                                "ua.email_address AS email_address, " +
                                "ua.mobile_number AS mobile_number, " +
                                "ua.tracking_source_ip AS tracking_source_ip")
                .from()
                .joinPhrase(Q.getParentDatabase(Table.USER_ACCOUNTS)+"."+ Table.USER_ACCOUNTS+" AS ua ")
                .where("ua.user_id = :user_id");
        authenticatedUser = Repository.select(queryBuilder,
                new FlexicoreHashMap().addQueryArgument(":user_id", authenticatedUserId)).getSingleRecord();
        return authenticatedUser;
    }

    public static boolean checkAuthHash(String authHash, String correctDetails) {
        try {
            authHash = HashUtils.base64Decode(authHash);
        } catch (Exception e){ authHash = "BadBase64Encoding";}

        boolean isAuthentic = false;

        try {
            isAuthentic = BCrypt.checkpw(correctDetails, authHash);
        } catch (Exception e){
            e.printStackTrace();
        }

        return isAuthentic;
    }

    public static boolean checkAPIAuth(String APIPassword, String DBPassword) {
        boolean isAuthentic = false;
        try {
            if(APIPassword.equals(HashUtils.SHA256(DBPassword))) {
                isAuthentic = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return isAuthentic;
    }

    public static String getUserFullName(String firstName, String lastName, String otherNames){
        firstName = (firstName == null) ? "" : firstName;
        lastName = (lastName == null) ? "" : lastName;
        otherNames = (otherNames == null) ? "" : otherNames;
        return (firstName+" "+otherNames+" "+lastName).replaceAll("\\s{2,}", " ").trim();
    }

}
