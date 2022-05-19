package ke.co.skyworld.wso2_monitoring;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Methods;
import ke.co.skyworld.wso2_monitoring.apis.GetRequestLogs;
import ke.co.skyworld.wso2_monitoring.apis.GetSingleRequestLog;
import ke.co.skyworld.wso2_monitoring.apis.UserAccountAuthenctication;
import ke.co.skyworld.wso2_monitoring.utils.CORSHandler;

public class RestServer {

    static String BASE_PORTAL_URL = "/api/rest";

    public static void main(String[] args) {
        PathHandler pathHandler = Handlers.path()
                //.addExactPath(BASE_PORTAL_URL+"/", Routes.portal())
                .addPrefixPath(BASE_PORTAL_URL + "/request-logs",
                        Handlers.routing()
                                .get("", new GetRequestLogs())
                                .put("", new GetRequestLogs())
                                .get("/{requestId}", new GetSingleRequestLog())
                                .add(Methods.OPTIONS, "/*", new CORSHandler())

                )
                .addPrefixPath(BASE_PORTAL_URL + "/user-accounts",
                        Handlers.routing()
                                //.get("", new ke.co.skyworld.wso2_monitoring.apis.GetRequestLogs())
                                //.put("", new ke.co.skyworld.wso2_monitoring.apis.GetRequestLogs())
                                .get("/{username}", new UserAccountAuthenctication())
                                .get("", new UserAccountAuthenctication())

                );

        Undertow server = Undertow.builder()
                //.setServerOption(UndertowOptions.ALLOW_ENCODED_SLASH, true)
                .setHandler(pathHandler)
                .addHttpListener(9081, "0.0.0.0")
                .build();

        server.start();

        System.out.println("Server Started!!");

    }
}


