package ke.co.skyworld.wso2_monitoring.apis;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import ke.co.skyworld.wso2_monitoring.utils.objects.UserAccounts;
import ke.co.skyworld.wso2_monitoring.utils.Errors;
import ke.co.skyworld.wso2_monitoring.utils.JavaToJSONAndXML;
import ke.co.skyworld.wso2_monitoring.utils.NamedPreparedStatement;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccountAuthenctication implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String username = getPathVar(exchange, "username");
//        String user_pwd = getPathVar(exchange, "user_pwd");

        UserAccounts userAccounts = getUser(username);

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"), "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"), Headers.CONTENT_TYPE_STRING+", "+Headers.ACCEPT_STRING+", "+Headers.AUTHORIZATION_STRING+", "+Headers.CONTENT_DISPOSITION_STRING);

        if (userAccounts==null){

            Errors errors = new Errors();
            errors.setError("Account with the provided username not found");
            errors.setErrorCode(404);

            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(JavaToJSONAndXML.convertToJson(errors));

            return;
        }

        String json = JavaToJSONAndXML.convertToJson(userAccounts);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json);
    }

    public static UserAccounts getUser(String username) {
        Connection myConn = null;

        NamedPreparedStatement myPreparedStatement = null;

        ResultSet myResult = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2_monitoring_database", "root", "Pa55w0rd");

            String mySql = "select * from user_accounts WHERE username=:myUsername";

            myPreparedStatement = NamedPreparedStatement.prepareStatement(myConn,mySql);

            myPreparedStatement.setString("myUsername", username);
            // myPreparedStatement.setString("myPassword", user_pwd);
            myResult = myPreparedStatement.executeQuery();

            if (myResult.next()){

                UserAccounts userAccounts =new UserAccounts();

                int user_id = myResult.getInt("user_id");

                userAccounts.setUser_id(user_id);

                System.out.println("ID"+user_id);

                return userAccounts;
            }
            System.out.println("Username does not exist");

            myResult.close();
            myPreparedStatement.close();
            myConn.close();

            myConn = null;
            myPreparedStatement = null;
            myResult = null;

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
            myResult = null;
        } finally {
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
            myResult = null;
        }

        return null;
    }

    public static String getPathVar(HttpServerExchange exchange, String pathVarId) {
        PathTemplateMatch pathMatch = (PathTemplateMatch)exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();
        URLUtils.decode((String)pathMatch.getParameters().get(pathVarId), StandardCharsets.UTF_8.name(), true, builder);
        return builder.toString();
    }

}
