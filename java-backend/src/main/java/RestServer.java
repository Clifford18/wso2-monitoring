import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

public class RestServer {

    static String BASE_PORTAL_URL = "/api/rest";

    public static void main(String[] args) {
        PathHandler pathHandler = Handlers.path()
                //.addExactPath(BASE_PORTAL_URL+"/", Routes.portal())
                .addPrefixPath(BASE_PORTAL_URL+"/request-logs",
                        Handlers.routing()
                                .get("", new GetRequests())
                                .put("", new GetRequests())
                                .get("/{requestId}", new GetRequest())

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