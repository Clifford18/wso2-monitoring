package ke.co.skyworld.wso2_monitoring.apis;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import ke.co.skyworld.wso2_monitoring.utils.objects.RequestLog;
import ke.co.skyworld.wso2_monitoring.utils.Errors;
import ke.co.skyworld.wso2_monitoring.utils.JavaToJSONAndXML;
import ke.co.skyworld.wso2_monitoring.utils.NamedPreparedStatement;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetSingleRequestLog implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String requestId = getPathVar(exchange, "requestId");

        RequestLog requestLog = readSingleLog(requestId);

        if (requestLog == null) {

            Errors errors = new Errors();
            errors.setError("Log with the provided requestId not found");
            errors.setErrorCode(404);

            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(JavaToJSONAndXML.convertToJson(errors));

            return;
        }

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"), "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"), Headers.CONTENT_TYPE_STRING+", "+Headers.ACCEPT_STRING+", "+Headers.AUTHORIZATION_STRING+", "+Headers.CONTENT_DISPOSITION_STRING);

        String json = JavaToJSONAndXML.convertToJson(requestLog);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json);
    }

    public static RequestLog readSingleLog(String requestId) {
        Connection myConn = null;
        ;
        NamedPreparedStatement myPreparedStatement = null;
        ;
        ResultSet myResult = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2_monitoring_database", "root", "Pa55w0rd");

            String sql = "select * from request_logs where request_id = :request_id";
            myPreparedStatement = NamedPreparedStatement.prepareStatement(myConn, sql);
            myPreparedStatement.setString("request_id", requestId);

            myResult = myPreparedStatement.executeQuery();

            if (myResult.next()) {

                RequestLog requestLog = new RequestLog();

                int request_id = myResult.getInt("request_id");
                String request_reference = myResult.getString("request_reference");
                String request_method = myResult.getString("request_method");
                String request_resource = myResult.getString("request_resource");
                String request_parameters = myResult.getString("request_parameters");
                String request_headers = myResult.getString("request_headers");
                String request_body = myResult.getString("request_body");
                String request_origin_ip = myResult.getString("request_origin_ip");
                String response_headers = myResult.getString("response_headers");
                String response_body = myResult.getString("response_body");
                String error_code = myResult.getString("error_code");
                String error_message = myResult.getString("error_message");
                String error_stacktrace = myResult.getString("error_stacktrace");
                String date_created = myResult.getString("date_created");
                String date_modified = myResult.getString("date_modified");

                requestLog.setRequest_id(request_id);
                requestLog.setRequest_reference(request_reference);
                requestLog.setRequest_method(request_method);
                requestLog.setRequest_resource(request_resource);
                requestLog.setRequest_parameters(request_parameters);
                requestLog.setRequest_headers(request_headers);
                requestLog.setRequest_body(request_body);
                requestLog.setRequest_origin_ip(request_origin_ip);
                requestLog.setResponse_headers(response_headers);
                requestLog.setResponse_body(response_body);
                requestLog.setError_code(error_code);
                requestLog.setError_message(error_message);
                requestLog.setError_stacktrace(error_stacktrace);
                requestLog.setDate_created(date_created);
                requestLog.setDate_modified(date_modified);

                //print results
                System.out.println("ID ::" + request_id);
                //System.out.println(myResult.getString("request_id"));
                return requestLog;
            }

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
        PathTemplateMatch pathMatch = (PathTemplateMatch) exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();
        URLUtils.decode((String) pathMatch.getParameters().get(pathVarId), StandardCharsets.UTF_8.name(), true, builder);
        return builder.toString();
    }
}
