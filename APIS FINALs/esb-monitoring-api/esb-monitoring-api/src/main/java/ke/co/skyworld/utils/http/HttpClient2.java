package ke.co.skyworld.utils.http;

import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.logging.Log;
import ke.co.skyworld.utils.memory.JvmManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient2 {

    public static String get(
            String url, HashMap<String, String> params,
            HashMap<String, String> headers
    ) {
        return get(url, params, headers, 1000);
    }

    public static String get(
            String url, HashMap<String, String> params
    ) {
        return get(url, params, new HashMap<>(), 1000);
    }

    public static String get(
            String url
    ) {
        return get(url, new HashMap<>(), new HashMap<>(), 1000);
    }

    @SuppressWarnings("Duplicates")
    public static String get(
            String url, HashMap<String, String> params,
            HashMap<String, String> headers, int connectionTimeout
    ) {
        StringBuffer response = new StringBuffer();
        HttpURLConnection connection = null;
        BufferedReader in = null;
        int responseCode = -1;

        try {
            connection = (HttpURLConnection)
                    new URL(url + "?" + getParametersString(params)).openConnection();

            //Set Request Method
            connection.setRequestMethod("GET");
            //Add Headers
            addHeaders(connection, headers);
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                return connection.getResponseMessage();
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JvmManager.gc(response, connection, in, url, params, headers, responseCode, connectionTimeout);
        return null;
    }

    public static String post(
            String url, HashMap<String, String> params,
            HashMap<String, String> headers, Object body
    ) {
        return post(url, params, headers, body, 1000);
    }

    public static String post(
            String url, HashMap<String, String> headers, Object body
    ) {
        return post(url, new HashMap<>(), headers, body, 1000);
    }

    public static String post(
            String url, Object body
    ) {
        return post(url, new HashMap<>(), new HashMap<>(), body, 1000);
    }

    public static String post(
            String url, HashMap<String, String> params,
            HashMap<String, String> headers, Object body, int connectionTimeout
    ) {
        StringBuffer response = new StringBuffer();
        int responseCode = -1;
        HttpURLConnection connection = null;
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            connection = (HttpURLConnection)
                    new URL(url + "?" + getParametersString(params)).openConnection();
            //Set Request Method
            connection.setRequestMethod("POST");
            //Add Headers
            addHeaders(connection, headers);
            connection.setDoOutput(true);

            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(ScedarHttpHandler.toJson(body));
            out.flush();

            response = new StringBuffer();
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } else {
                return connection.getResponseMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        JvmManager.gc(response, connection, in, url, params, headers, responseCode, body, connectionTimeout);
        return null;
    }

    public static String postData(String url, HashMap<String, String> params,
                                  HashMap<String, String> headers, String body, int connectionTimeout
    ) {
        StringBuffer response = new StringBuffer();
        int responseCode = -1;
        HttpURLConnection connection = null;
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            connection = (HttpURLConnection)
                    new URL(url + ((params == null) ? "" : "?" + getParametersString(params))).openConnection();

            //Set Request Method
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectionTimeout);
            //Add Headers
            addHeaders(connection, headers);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Length", Integer.toString(body.length()));
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

            response = new StringBuffer();
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } else {
                return connection.getResponseMessage();
            }

        } catch (IOException e) {
            Log.error(HTTPClient.class, "postData", "Error! Connection Timeout: "+e.getMessage());
            return Constants.HTTP_CLIENT_ERROR;
        } catch (Exception e) {
            Log.error(HTTPClient.class, "postData", "Error Posting Data: "+e.getMessage());
            return Constants.HTTP_CLIENT_ERROR;
        } finally {
            try { if(in != null) in.close(); } catch (IOException e) { e.printStackTrace(); }
            JvmManager.gc(response, connection, in, url, params, headers, responseCode, body);
        }
    }

    private static void addHeaders(HttpURLConnection connection, HashMap<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static String getParametersString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            try {
                result.append(URLEncoder.encode(entry.getKey(), String.valueOf(StandardCharsets.UTF_8)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), String.valueOf(StandardCharsets.UTF_8)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

}
