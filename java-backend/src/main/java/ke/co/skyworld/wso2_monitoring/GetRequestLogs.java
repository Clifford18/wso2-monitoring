package ke.co.skyworld.wso2_monitoring;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.sql.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GetRequestLogs implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        List<RequestLog> requestLogs = readAllLogs(exchange);

        String xmlStr = JavaToJSONAndXML.convertToJson(requestLogs);

        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().add(new HttpString("RequestType"), "Request Logs");
        exchange.getResponseSender().send(xmlStr);
    }


    public static int[] getPageAndPageSize(HttpServerExchange exchange) {
        int[] pageAndPageSize = new int[]{1, 10};

        Deque<String> page = exchange.getQueryParameters().get("page");
        Deque<String> pageSize = exchange.getQueryParameters().get("pageSize");

        if (page != null) {
            try {
                pageAndPageSize[0] = Integer.parseInt(page.getFirst());

                if(pageAndPageSize[0]<1){
                    return null;
                }

            } catch (Exception ignore) {

            }
        }

        if (pageSize != null) {
            try {
                pageAndPageSize[1] = Integer.parseInt(pageSize.getFirst());
            } catch (Exception ignore) {

            }
        }
        return pageAndPageSize;
    }

    public static List<RequestLog> readAllLogs(HttpServerExchange exchange) {
        Connection myConn = null;
        ;
        NamedPreparedStatement myStatement = null;
        ;
        ResultSet myResult = null;

        try {

            int[] pagePageSize = getPageAndPageSize(exchange);

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String sql = pagePageSize!=null ? "select * from request_logs limit :limit offset :offset": "select * from request_logs";

            myStatement = NamedPreparedStatement.prepareStatement(myConn, sql);

            if(pagePageSize!=null){
                int limit = pagePageSize[1];
                int offset = (pagePageSize[0] - 1) * pagePageSize[1];

                myStatement.setInt("limit", limit);
                myStatement.setInt("offset", offset);
            }

            myResult = myStatement.executeQuery();

            List<RequestLog> requestLogs = new ArrayList<>();

            while (myResult.next()) {

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

                requestLogs.add(requestLog);

                //print results
                System.out.println("ID ::" + request_id);
                //System.out.println(myResult.getString("request_id"));
            }

            myResult.close();
            myStatement.close();
            myConn.close();

            myConn = null;
            myStatement = null;
            myResult = null;

            return requestLogs;

        } catch (Exception e) {
            e.printStackTrace();
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myStatement != null) try {
                myStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myStatement = null;
            myResult = null;
        } finally {
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myStatement != null) try {
                myStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myStatement = null;
            myResult = null;
        }

        return null;
    }
}
