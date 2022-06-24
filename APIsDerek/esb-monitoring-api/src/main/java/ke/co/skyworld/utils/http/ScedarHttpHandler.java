package ke.co.skyworld.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.*;
import ke.co.skyworld.domain.enums.AccessTokenScope;
import ke.co.skyworld.domain.enums.Permissions;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.domain.enums.UserTypes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.repository.Q;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.Column;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.Constants.Table;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.data_formatting.Converter;
import ke.co.skyworld.utils.data_formatting.XmlObject;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.logging.Log;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.Encryption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessMode;
import java.util.*;

import static ke.co.skyworld.utils.Constants.*;

/**
 * sls-api (ke.co.scedar.http_handlers)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:55 PM
 **/
public class ScedarHttpHandler implements HttpHandler {

    @Deprecated
    protected boolean requestIsNotAuthentic(HttpServerExchange exchange){
        Set<AccessTokenScope> accessTokenScopes = new HashSet<>();
        accessTokenScopes.add(AccessTokenScope.GLOBAL);
        return requestIsNotAuthentic(exchange, accessTokenScopes);
    }

    protected boolean requestIsNotAuthentic(HttpServerExchange exchange, AccessTokenScope... accessTokenScopes){
        Set<AccessTokenScope> accessTokenScopeSet = new HashSet<>(Arrays.asList(accessTokenScopes));
        return requestIsNotAuthentic(exchange, accessTokenScopeSet);
    }

    protected boolean requestIsNotAuthentic(HttpServerExchange exchange, Set<AccessTokenScope> accessTokenScopes){
        HeaderValues authorizationHeader = exchange.getRequestHeaders()
                .get("Authorization");

        String activePage = getQueryParam(exchange, "activePage");
        if(activePage == null){
            send(exchange, new ExceptionRepresentation(
                    "Missing Query Param 'activePage'",
                    exchange.getRequestURI(),
                    "Query Param 'activePage' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return true;
        }

        /*String portal = getQueryParam(exchange, "portal");
        if(portal == null){
            send(exchange, new ExceptionRepresentation(
                    "Missing Query Param 'portal'",
                    exchange.getRequestURI(),
                    "Query Param 'portal' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return true;
        }

        portal = portal.trim().toUpperCase();
        if(!(portal.equals("MEMBER") || portal.equals("PARTNER") || portal.equals("ADMIN"))){
            send(exchange, new ExceptionRepresentation(
                    "Invalid portal",
                    exchange.getRequestURI(),
                    "portal '"+portal+"' is invalid. Use 'MEMBER', 'PARTNER' or 'ADMIN'",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return true;
        }*/

        //Set Portal
        //exchange.addQueryParam(REQ_PORTAL, portal);

        //Add Active page to user exchange context
        exchange.addQueryParam(REQ_ACTIVE_PAGE, activePage);

        if(authorizationHeader == null){
            send(exchange, new ExceptionRepresentation(
                    "Missing HEADER 'Authorization'",
                    exchange.getRequestURI(),
                    "HEADER 'Authorization' is required",
                    StatusCodes.FORBIDDEN,
                    exchange.getRequestMethod()
            ), StatusCodes.FORBIDDEN);
            return true;
        }else{
            String accessToken = authorizationHeader.getFirst()
                    .replace("Bearer ", "");

           FlexicoreHashMap accessTokenRecord = AccessTokenRepository.validate(exchange, accessTokenScopes, accessToken);
            if (accessTokenRecord != null && !accessTokenRecord.isEmpty()) {
                setRequestContext(exchange, accessToken);
                if (accessTokenRecord.getValue("ip_mismatch") != null) {
                    send(exchange, new ExceptionRepresentation(
                            "Remote address used to log in differs from current remote address. Kindly login again",
                            exchange.getRequestURI(),
                            "Authentication IP Address Mismatch",
                            StatusCodes.FORBIDDEN,
                            exchange.getRequestMethod()
                    ), StatusCodes.FORBIDDEN);
                    return true;
                }
                exchange.addQueryParam(Constants.REQ_ACCESS_TOKEN, accessToken);
                return false;
            } else {
                send(exchange, new ExceptionRepresentation(
                        "Invalid credentials",
                        exchange.getRequestURI(),
                        "Expired/corrupt access token",
                        StatusCodes.FORBIDDEN,
                        exchange.getRequestMethod()
                ), StatusCodes.FORBIDDEN);
                return true;
        }
        }
    }

    protected Long getAuthenticatedUserId(HttpServerExchange exchange){
        //Get authenticated user id
        return Long.parseLong(exchange.getQueryParameters()
                .get(REQ_USER_ID).getFirst());
    }

    protected boolean userIsNotPrivileged(HttpServerExchange exchange, UserTypes... userTypes){

        boolean isPrivilegedUserType = false;

        /*if(exchange.getQueryParameters()
                .get(REQ_GROUP_TYPE).getFirst().equals(UserTypes.ROOT.value())) return false;*/

        for (UserTypes userType : userTypes){
            isPrivilegedUserType = (isPrivilegedUserType || userType.value().equals(exchange.getQueryParameters()
                    .get(REQ_GROUP_TYPE).getFirst()));
        }

        if(!isPrivilegedUserType){
            send(exchange, new ExceptionRepresentation(
                            "Access denied.",
                            exchange.getRequestURI(),
                            "You have insufficient rights",
                            StatusCodes.FORBIDDEN,
                            exchange.getRequestMethod()),
                    StatusCodes.FORBIDDEN);
            return true;
        }

        return false;
    }

    protected boolean userTypeIsNotPrivileged(HttpServerExchange exchange, PortalTypes... portTypes){

        boolean isPrivilegedUserType = false;

        for (PortalTypes portalType : portTypes){
            isPrivilegedUserType =
                    (isPrivilegedUserType || portalType.value().equalsIgnoreCase(getQueryParam(exchange, REQ_PORTAL)));
        }

        if(!isPrivilegedUserType){
            send(exchange, new ExceptionRepresentation(
                            "Access denied.",
                            exchange.getRequestURI(),
                            "You are not allowed to access the requested resource",
                            StatusCodes.FORBIDDEN,
                            exchange.getRequestMethod()),
                    StatusCodes.FORBIDDEN);
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void setRequestContext(HttpServerExchange exchange, String accessToken){
        QueryBuilder queryBuilder = null;
        FlexicoreHashMap queryArguments = null;
        FlexicoreHashMap userHashMap = null;
        FlexicoreArrayList userGroups = null;
        FlexicoreHashMap updateSet = null;
        FlexicoreArrayList userRightsArraylist = null;
        TransactionWrapper twrapper = null;

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("ua.user_id as user_id"));
//        columns.add(new Column("aug.group_type as group_type"));
        columns.add(new Column("at.auth_ip_address as auth_ip_address"));
        //columns.add(new Column("aug.group_name as group_name"));
        //columns.add(new Column("aug.group_id as group_id"));
        columns.add(new Column("ua.username as username"));
        columns.add(new Column("ua.tracking_url as tracking_url"));

        queryBuilder = new QueryBuilder()
                .select()
                .selectColumns(columns)
                .from()
                .joinPhrase(Q.getParentDatabase(Table.USER_ACCOUNTS)+"."+ Table.USER_ACCOUNTS+" as ua")
                //.joinPhrase("inner join " + Q.getParentDatabase(Table.USER_APP_USER_GROUPS(portal)) + "."+ Table.USER_APP_USER_GROUPS(portal) + " uaug on ua.user_id = uaug.user_id")
                //.joinPhrase("inner join " + Q.getParentDatabase(Table.APP_USER_GROUPS(portal)) + "."+ Table.APP_USER_GROUPS(portal) + " aug on uaug.group_id = aug.group_id")
                .joinPhrase("inner join " + Q.getParentDatabase(Table.ACCESS_TOKENS) + "."+ Table.ACCESS_TOKENS + " at on ua.user_id = at.user_id")
                .where("at.access_token = :access_token");

        queryArguments = new FlexicoreHashMap()
                .addQueryArgument(":access_token",accessToken);

        twrapper = Repository.select(queryBuilder, queryArguments);
        if(twrapper.hasErrors()) return;
        userHashMap = twrapper.getSingleRecord();
        if(userHashMap == null || userHashMap.isEmpty()) {
            exchange.addQueryParam(REQ_USER_APP_RIGHTS, "NULL");
            exchange.addQueryParam(REQ_GROUP_IDS, "NULL");
            return;
        }

        //Set username in context
        exchange.addQueryParam(REQ_USERNAME, userHashMap.getStringValue("username").toString());

        //Set user id in context
        exchange.addQueryParam(REQ_USER_ID, userHashMap.getStringValue("user_id").toString());

        //Set portal user type id in context
        exchange.addQueryParam(REQ_PORTAL_TYPE_ID, "0");

        //Set user IP Address
        exchange.addQueryParam(REQ_REMOTE_ADDR,
                exchange.getRequestHeaders().get("X-Real-IP").getFirst());
        //Set DB IP Address
        exchange.addQueryParam(REQ_DB_REMOTE_ADDR, userHashMap.getValue("auth_ip_address").toString());

        /*//Check if User is ROOT
        if(userGroupsHashMap.getValue("group_type").toString().equalsIgnoreCase(UserTypes.ROOT.value())){
            exchange.addQueryParam(REQ_GROUP_TYPE, UserTypes.ROOT.value());
        }

        //Check if User is ADMIN
        else if(userGroupsHashMap.getValue("group_type").toString().equalsIgnoreCase(UserTypes.ADMIN.value())){
            exchange.addQueryParam(REQ_GROUP_TYPE, UserTypes.ADMIN.value());
        }

        //If not ADMIN or ROOT -> Standard User
        else{
            exchange.addQueryParam(REQ_GROUP_TYPE, UserTypes.STANDARD_USER.value());
        }*/
        //Set DEFAULT Group Type
        exchange.addQueryParam(REQ_GROUP_TYPE, UserTypes.DEFAULT.value());

        //Set group ids in context
        queryBuilder = new QueryBuilder()
                .select()
                .selectColumn("group_id")
                .from(Table.USER_APP_USER_GROUPS)
                .where("user_id = :user_id");

        queryArguments = new FlexicoreHashMap()
                .addQueryArgument(":user_id", userHashMap.getStringValue("user_id"));

        twrapper = Repository.select(queryBuilder, queryArguments);
        userGroups = (FlexicoreArrayList) twrapper.getData();
        StringBuilder groupIds = new StringBuilder();

        if(userGroups != null && !userGroups.isEmpty()){
            for (int i = 0; i < userGroups.size() ; i++) {
                groupIds.append(userGroups.getRecord(i).getStringValue("group_id"));
                if (i != userGroups.size() - 1) groupIds.append(",");
            }
        } else {
            exchange.addQueryParam(REQ_USER_APP_RIGHTS, "NULL");
            exchange.addQueryParam(REQ_GROUP_IDS, groupIds.toString());
            return;
        }

        exchange.addQueryParam(REQ_GROUP_IDS, groupIds.toString());
        //System.err.println("THE GROUP IDS: \n"+groupIds);

        /**
         * user_rights XML Structure
         * <RIGHTS>
         *     <PERMISSIONS INPUT="YES" APPROVE="YES" VIEW="YES" EDIT="YES" DELETE="YES" EXECUTE="NO">
         *         <APPROVAL_LEVELS>
         *             <LEVEL>1</LEVEL>
         *             <LEVEL>2</LEVEL>
         *         </APPROVAL_LEVELS>
         *     </PERMISSIONS>
         * </RIGHTS>
         *
         * NEW permission STRING
         * Paradigm: APP_CODE:PERMISSION,APP_CODE:APPROVE-LEVEL;APP_CODE:PERMISSION,APP_CODE:APPROVE-LEVEL
         * Sample: 100:VIEW,100:EDIT,100:DELETE,100:EXECUTE,100:APPROVE-1,100:APPROVE-2;100:VIEW,100:EDIT,100:DELETE,100:EXECUTE,100:APPROVE-1,100:APPROVE-2
         */

        queryBuilder = new QueryBuilder()
                .select()
                .selectColumn("user_rights_id")
                .selectColumn("app_code")
                .selectColumn("user_rights")
                .from(Table.APP_USER_RIGHTS)
                .where("group_id IN ("+groupIds.toString()+") ORDER BY app_code ASC");

        /*queryArguments = new FlexicoreHashMap()
                .addQueryArgument(":group_ids", groupIds.toString());*/

        twrapper = Repository.select(queryBuilder);

        if(twrapper.hasErrors()) return;
        userRightsArraylist = (FlexicoreArrayList) twrapper.getData();

        StringBuilder rightsString = new StringBuilder();
        int userRightsArraylistSize = userRightsArraylist.size();
        XmlObject userRightsXMLObject = null;

        //New Implementation
        try {
            for (int i = 0; i < userRightsArraylistSize ; i++) {
                FlexicoreHashMap userRightRecord = userRightsArraylist.getRecord(i);
                String userRightsXMLString = userRightRecord.getStringValue("user_rights");
                userRightsXMLObject = new XmlObject(userRightsXMLString, Constants.PERMISSIONS_XSD);

                if(!userRightsXMLObject.isValid()){
                    throw new Exception("Invalid User Rights XML. ("+userRightsXMLObject.getXmlValidationError()+")");
                }

                String appCode = userRightRecord.getStringValue("app_code");
                String inputRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@INPUT");
                String approveRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@APPROVE");
                String viewRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@VIEW");
                String editRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@EDIT");
                String deleteRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@DELETE");
                String executeRight = userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/@EXECUTE");

                List<String> rightsElements = new ArrayList<>();

                if(inputRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "INPUT"));
                if(approveRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "APPROVE"));
                if(viewRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "VIEW"));
                if(editRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "EDIT"));
                if(deleteRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "DELETE"));
                if(executeRight.equalsIgnoreCase("YES")) rightsElements.add((appCode + ":" + "EXECUTE"));

                rightsString.append(String.join(",", rightsElements));

                if(approveRight.equalsIgnoreCase("YES")){
                    try {
                        int approvalLevelsTagCount =
                                userRightsXMLObject.countInnerTags("/RIGHTS/PERMISSIONS/APPROVAL_LEVELS/LEVEL");

                        if(approvalLevelsTagCount > 0) {
                            rightsString.append(","); //append trailing , from last section and add approval levels
                        }

                        for (int j = 1; j <= approvalLevelsTagCount ; j++) {
                            String approvalLevel =
                                    userRightsXMLObject.getTagValue("/RIGHTS/PERMISSIONS/APPROVAL_LEVELS/LEVEL["+j+"]");
                            rightsString.append(appCode).append(":").append("APPROVE-").append(approvalLevel);
                            if (j != approvalLevelsTagCount) rightsString.append(",");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.error(ScedarHttpHandler.class, "setRequestContext",
                                "Error Processing Approval Level Rights. ("+e.getMessage()+":");
                    }
                }

                if (i != userRightsArraylistSize - 1) {
                    if(!rightsString.toString().equalsIgnoreCase("")){
                        rightsString.append(";");
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.error(ScedarHttpHandler.class, "setRequestContext",
                    "Error Processing Rights XML. ("+e.getMessage()+":");
        }

        //Add user rights in exchange context
        exchange.addQueryParam(REQ_USER_APP_RIGHTS, rightsString.toString());
        //System.err.println("THE RIGHTS: \n"+rightsString);

        //END of New Implementation

        updateSet = new FlexicoreHashMap()
                .putValue("tracking_source_ip", exchange.getRequestHeaders().get("X-Real-IP").getFirst())
                .putValue("tracking_url", getQueryParam(exchange, REQ_ACTIVE_PAGE))
                .putValue("tracking_referrer", userHashMap.getValue("tracking_url"))
                .putValue("tracking_time", DateTime.getCurrentDateTime());

        queryArguments = new FlexicoreHashMap()
                .addQueryArgument(":user_id", userHashMap.getValue("user_id"));

        Repository.update(Table.USER_ACCOUNTS,updateSet,new FilterPredicate("user_id = :user_id"),queryArguments);

        JvmManager.gc(queryBuilder, queryArguments, userHashMap, updateSet, userRightsArraylist, twrapper, groupIds, rightsString, userRightsXMLObject);
    }

    public boolean insufficientAppRights(HttpServerExchange exchange, String required){

        return insufficientAppRights(exchange, required, true);
    }

    public boolean insufficientAppRights(HttpServerExchange exchange, String required, boolean sendRequest){

        if(!getQueryParam(exchange, REQ_USER_APP_RIGHTS).contains(required)){
            if(sendRequest){
                send(exchange, new ExceptionRepresentation(
                                "You are not allowed to perform this action",
                                exchange.getRequestURI(),
                                "You have insufficient rights",
                                StatusCodes.FORBIDDEN,
                                exchange.getRequestMethod()),
                        StatusCodes.FORBIDDEN);
            }
            return true;
        }
        return false;
    }

    protected HashMap<String, String> getPathVars(HttpServerExchange exchange, String... pathVarIds){
        PathTemplateMatch pathMatch =
                exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);

        HashMap<String, String> pathVars = new HashMap<>(pathVarIds.length);

        for (String pathVarId : pathVarIds){
            pathVars.put(pathVarId, pathMatch.getParameters().get(pathVarId));
        }
        JvmManager.gc(pathMatch);
        return pathVars;
    }

    public String getPathVar(HttpServerExchange exchange, String pathVarId){
        PathTemplateMatch pathMatch =
                exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String pv = pathMatch.getParameters().get(pathVarId);
        JvmManager.gc(pathMatch);
        return pv;
    }

    public static String getQueryParam(HttpServerExchange exchange, String key){
        Deque<String> param = exchange.getQueryParameters().get(key);
        String paramStr = null;

        if(param != null && !param.getFirst().equals(""))
            paramStr = param.getFirst();

        JvmManager.gc(param);

        return paramStr;
    }

    protected HashMap<String, String> getQueryParams(HttpServerExchange exchange, String... keys){

        HashMap<String, String> params = new HashMap<>();
        Deque<String> param = null;

        for (String key: keys){
            param = exchange.getQueryParameters().get(key);

            if(param != null && !param.getFirst().equals(""))
                params.put(key, param.getFirst());
        }

        JvmManager.gc(param);

        return params;
    }

    protected int[] getPageAndPageSize(HttpServerExchange exchange){

        int[] pageAndPageSize = new int[] {1, 10};

        Deque<String> page = exchange.getQueryParameters().get("page");
        Deque<String> pageSize = exchange.getQueryParameters().get("pageSize");

        if(page != null){
            try{
                pageAndPageSize[0] = Integer.parseInt(page.getFirst());
            } catch (Exception ignore){

            }
        }

        if(pageSize != null){
            try{
                if(Integer.parseInt(pageSize.getFirst()) > 0){
                    pageAndPageSize[1] = Integer.parseInt(pageSize.getFirst());
                }

            } catch (Exception ignore){

            }
        }

        JvmManager.gc(page, pageSize);

        return pageAndPageSize;

    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) {
        printRequestInfo(httpServerExchange);
        setBaseRequestContext(httpServerExchange);
    }

    private void forbidProcession(HttpServerExchange exchange, Permissions... perms){
        send(exchange, new ExceptionRepresentation(
                "Access denied.",
                exchange.getRequestURI(),
                "You have insufficient rights to complete this request. " +
                        "Any of the following rights is required. ["+ getEnumString(perms) +"]",
                StatusCodes.FORBIDDEN,
                exchange.getRequestMethod()
        ), StatusCodes.FORBIDDEN);
    }

    private String getEnumString(UserTypes... userTypes){
        String[] strPerms = new String[userTypes.length];

        for (int i = 0; i < userTypes.length ; i++) {
            strPerms[i] = userTypes[i].value();
        }

        return String.join(", ", strPerms);
    }

    private String getEnumString(Permissions... perms){
        String[] strPerms = new String[perms.length];

        for (int i = 0; i < perms.length ; i++) {
            strPerms[i] = perms[i].value();
        }

        return String.join(", ", strPerms);
    }

    protected void underConstruction(HttpServerExchange exchange) {
        String message = "Endpoint still under construction\n" +
                "             __    __\n" +
                "            /  \\ /| |'-.\n" +
                "           .\\__/ || |   |\n" +
                "        _ /  `._ \\|_|_.-'\n" +
                "       | /  \\__.`=._) (_\n" +
                "       |/ ._/  |\"\"\"\"\"\"\"\"\"|\n" +
                "       |'.  `\\ | Backend |\n" +
                "       ;\"\"\"/ / |  Devs   |\n" +
                "        ) /_/| |.-------.|\n" +
                "       '  `-`' \"         \"";

        this.send(exchange, message, StatusCodes.OK, "plain/text");
    }

    protected void send(HttpServerExchange exchange, String data, Integer status, String contentType){
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        exchange.getResponseSender().send(data);

        try {
            exchange.getResponseChannel().shutdownWrites();
        } catch (IOException | NullPointerException ignore) {}

        exchange.endExchange();
        JvmManager.gc(exchange);
    }

    public void send(HttpServerExchange exchange, Object data, Integer status){
        exchange.setStatusCode(status);

        String contentType = determineAccept(exchange);
        ObjectMapper objectMapper = null;

        if(contentType.equals(applicationJson)){
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, applicationJson);
            try {
                objectMapper = getResponseObjectMapper(exchange);
                exchange.getResponseSender().send(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, applicationXml);
            try {
                objectMapper = getResponseObjectMapper(exchange);
                exchange.getResponseSender().send(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        JvmManager.gc(objectMapper);

        try {
            exchange.getResponseChannel().shutdownWrites();
        } catch (IOException | NullPointerException ignore) {}

        exchange.endExchange();
        JvmManager.gc(exchange);
    }

    public static String toJson(Object obj){
        return serialize(obj, applicationJson);
    }

    public static String toXml(Object obj){
        return serialize(obj, applicationXml);
    }

    public static String serialize(Object obj, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            String objStr = objectMapper.writeValueAsString(obj);
            JvmManager.gc(obj, objectMapper);
            return objStr;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Object toHashMap(String objStr, TypeReference T){
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = new HashMap<>();
        try {
            map = objectMapper.readValue(objStr, T);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JvmManager.gc(objectMapper, objStr);
        return map;
    }

    protected void send(HttpServerExchange exchange, String exposedPath){
        send(exchange, exposedPath, true);
    }

    @SuppressWarnings("Duplicates")
    protected void send(HttpServerExchange exchange, String exposedPath, boolean encrypted){
        String rawPath = (encrypted) ? Encryption.decrypto(exposedPath) : exposedPath;

        File file = new File(rawPath);
        OutputStream outStream = null;
        FileInputStream inputStream = null;
        int BUFFER_SIZE = 1024 * 100;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        if(file.exists()){
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
            exchange.getResponseHeaders().put(Headers.CONTENT_DISPOSITION, headerValue);
            exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, file.length());

            String exposeHeaders = Headers.CONTENT_DISPOSITION_STRING + ", " + Headers.CONTENT_LENGTH_STRING;
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Expose-Headers"), exposeHeaders);

            try {
                outStream = exchange.getOutputStream();
                inputStream = new FileInputStream(file);

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                //outStream.flush();
                outStream.close();
            } catch(IOException ioExObj) {
                ioExObj.printStackTrace();
                send(exchange, new ExceptionRepresentation(
                        "This may be due to file permissions. If this is a public attachment, " +
                                "try accessing it via '"+ getPublicAssetsServer()+file.getName()+"'",
                        exchange.getRequestURI(),
                        "Exception While Performing The I/O Operation",
                        StatusCodes.INTERNAL_SERVER_ERROR,
                        exchange.getRequestMethod()
                ).setErrors(Collections.singletonList(getPublicAssetsServer() + file.getName()))
                        , StatusCodes.INTERNAL_SERVER_ERROR);
            } finally {
                try{
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if(outStream != null){
                        outStream.flush();
                        outStream.close();
                    }
                } catch (IOException ignore){}
            }

        } else {
            send(exchange, new ExceptionRepresentation(
                    "File not found in server.",
                    exchange.getRequestURI(),
                    "File not found",
                    StatusCodes.NOT_FOUND,
                    exchange.getRequestMethod()
            ), StatusCodes.NOT_FOUND);
        }

        JvmManager.gc(rawPath, file, outStream, inputStream, BUFFER_SIZE, buffer, bytesRead);

        try {
            exchange.getResponseChannel().shutdownWrites();
        } catch (IOException | NullPointerException ignore) {}
        catch (IllegalStateException e){
            Log.warning(ScedarHttpHandler.class, "send", "Error: "+e.getMessage());
        }
        exchange.endExchange();
        JvmManager.gc(exchange);
    }

    @SuppressWarnings("unchecked")
    public Object getBodyObject(HttpServerExchange exchange, Class clazz){
        String contentType = determineContentType(exchange);
        String body = getBody(exchange);

        ObjectMapper objectMapper = getObjectMapper(contentType);

        try {
            Object obj = objectMapper.readValue(body, clazz);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            exchange.addQueryParam(MARSHALL_ERROR,e.getMessage());
            return null;
        }
    }

    protected Object[] fromJson(String data, Class clazz){

        ObjectMapper objectMapper = getObjectMapper(applicationJson);

        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return new Object[] {1, obj};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[] {-1, MARSHALL_ERROR, e.getMessage()};
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getObject(String data, Class clazz, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String determineContentType(HttpServerExchange exchange){
        try{
            return determineAorCt(exchange.getRequestHeaders()
                    .get("Content-Type").getFirst());
        }catch (NullPointerException e){
            return determineAorCt(applicationJson);
        }
    }

    private static String determineAccept(HttpServerExchange exchange){
        try{
            return determineAorCt(exchange.getRequestHeaders()
                    .get("Accept").getFirst());
        }catch (NullPointerException e){
            return determineAorCt(applicationJson);
        }
    }

    private static String determineAorCt(String headerValue){
        switch (headerValue) {
            case applicationJson:
                return applicationJson;
            case applicationXml:
                return applicationXml;
            default:
                return applicationJson;
        }
    }

    public static ObjectMapper getRequestObjectMapper(HttpServerExchange exchange){
        return getObjectMapper(determineContentType(exchange));
    }

    private static ObjectMapper getResponseObjectMapper(HttpServerExchange exchange){
        return getObjectMapper(determineAccept(exchange));
    }

    private static ObjectMapper getObjectMapper(String contentType){
        /*if(contentType.equals(applicationXml)){
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            return new XmlMapper(xmlModule);
        }else{
            return new ObjectMapper();
        }*/
        return Converter.getObjectMapper(contentType);
    }

    protected void setBaseRequestContext(HttpServerExchange exchange){
        exchange.addQueryParam(REQ_URI, exchange.getRequestURI());
    }

    protected void printRequestInfo(HttpServerExchange exchange){
        String userAgentHeader = "UNKNOWN";
        try{
            userAgentHeader = exchange.getRequestHeaders().get("User-Agent").getFirst();
        }catch (NullPointerException ignore){}
        String user = userAgentHeader.toLowerCase();

        String os = "UNKNOWN";
        String browser = "UNKNOWN";

        // Determine OS
        if (user.contains("windows")) os = "Windows";
        else if (user.contains("linux")) os = "Linux";
        else if(user.contains("mac")) os = "Mac";
        else if(user.contains("x11")) os = "Unix";
        else if(user.contains("android")) os = "Android";
        else if(user.contains("iphone")) os = "IPhone";

        // Determine Browser
        if (user.contains("msie"))
        {
            String substring= userAgentHeader.substring(userAgentHeader.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgentHeader.substring(userAgentHeader.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgentHeader.substring(userAgentHeader.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgentHeader.substring(userAgentHeader.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgentHeader.substring(userAgentHeader.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))  || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3")) )
        {
            browser = "Netscape-?";

        } else if (user.contains("firefox"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv"))
        {
            browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        }

        System.out.println("\n*********** REQUEST INFO ***********");
        System.out.println("Request URI: " + exchange.getRequestURI());
        System.out.println("Protocol: " + exchange.getProtocol());
        System.out.println("Request Method: " + exchange.getRequestMethod());
        try{
            System.out.println("Remote Address (X-Real-IP): " + exchange.getRequestHeaders().get("X-Real-IP").getFirst().replace("/", ""));
        }catch (NullPointerException e){
            System.out.println("Remote Address (Native): "+exchange.getSourceAddress().getAddress().toString());
            exchange.getRequestHeaders().add(
                    new HttpString("X-Real-IP"), exchange.getSourceAddress().getAddress().toString().replace("/", ""));
            System.out.println("Remote Address (X-Real-IP): " + exchange.getRequestHeaders().get("X-Real-IP").getFirst());
        }

        System.out.println("User-Agent: " + userAgentHeader);
        System.out.println("Remote OS: " + os);
        System.out.println("Remote Browser: " + browser);
        if(dumpRequest()) printRequestLog(exchange);
        System.out.println("Timestamp: " + DateTime.getCurrentDateTime());
        System.out.println("**************************************\n");

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"), "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"), Headers.CONTENT_TYPE_STRING+", "+Headers.ACCEPT_STRING+", "+Headers.AUTHORIZATION_STRING+", "+Headers.CONTENT_DISPOSITION_STRING);
    }


    private void printRequestLog(HttpServerExchange exchange) {
        String requestDump = "" +
                "Request Method: "+exchange.getRequestMethod().toString()+"\n"+
                "Request Headers: "+exchange.getRequestHeaders().toString()+"\n"+
                "Request GET Params: "+exchange.getQueryParameters()+"\n";

        try {
            requestDump += "Requested Resource: "+java.net.URLDecoder
                    .decode(exchange.getRequestURI(), StandardCharsets.UTF_8.name())+"\n";
        } catch (UnsupportedEncodingException e){
            requestDump += "Requested Resource: "+exchange.getRequestURI()+"\n";
        }
        System.out.print(requestDump);
    }

    public static String getFormData(HttpServerExchange exchange, String key){
        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        Deque<FormData.FormValue> formValueDeque = formData.get(key);
        String value = null;

        if(formValueDeque != null){
            value = formValueDeque.getFirst().getValue();
        }
        JvmManager.gc(formData, formValueDeque);
        if(dumpRequest()) System.out.println("Request Body (Form Data): "+key+": "+value+"\n");
        return value;
    }

    public static HashMap<String, String> getFormData(HttpServerExchange exchange, String... keys){
        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        HashMap<String, String> values = new HashMap<>();
        Deque<FormData.FormValue> formValueDeque = null;

        for(String key: keys){
            formValueDeque = formData.get(key);
            if(key != null){
                values.put(key, formValueDeque.getFirst().getValue());
            }
        }
        JvmManager.gc(formData, formValueDeque);
        if(dumpRequest()) System.out.println("Request Body (Form Data): "+formData+"\n");
        return values;
    }

    protected String getBody(HttpServerExchange exchange){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            exchange.startBlocking();
            reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

            String line;
            while((line = reader.readLine()) != null ) {
                builder.append( line );
            }
        } catch(IOException e) {
            e.printStackTrace( );
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            }
            JvmManager.gc(reader, builder);
        }

        String body = builder.toString();
        JvmManager.gc(reader, builder);

        if(dumpRequest())
            System.out.println("\nBODY\n***********\n"+body+"\n****************");

        return body;
    }
}
