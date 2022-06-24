package ke.co.skyworld.endpoints;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Methods;
import ke.co.skyworld.endpoints.handlers.auth.*;
import ke.co.skyworld.endpoints.handlers.crud.master.MasterRead;
import ke.co.skyworld.endpoints.handlers.sys.Ftp;
import ke.co.skyworld.endpoints.handlers.sys.ReadPasswordPolicy;
import ke.co.skyworld.endpoints.handlers.users.me.Me;
import ke.co.skyworld.endpoints.handlers.users.me.UpdateMe;
import ke.co.skyworld.utils.http.CorsHandler;
import ke.co.skyworld.utils.http.Dispatcher;
import ke.co.skyworld.utils.http.FallBack;
import ke.co.skyworld.utils.http.InvalidMethod;

/**
 * skyworld-api (ke.co.skyworld.endpoints)
 * Created By: elon
 * On: 26 Jan, 2019 10:40 AM
 **/

public class Routes {

    //Authorization
    public static RoutingHandler authorization() {
        return Handlers.routing()
                .get("/generate-captcha", new Dispatcher(new ReCaptcha()))
                .get("/verify-password", new Dispatcher(new VerifyPassword()))
                .get("/check-user-email", new Dispatcher(new CheckUserEmail()))
                .post("/token", new Dispatcher(new Authenticate()))
                .post("/refresh", new Dispatcher(new RefreshToken()))
                .delete("/revoke", new Dispatcher(new RevokeToken()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    //Default Master Reading Routing Handler
    public static RoutingHandler defaultMasterReadingRouter(String entity) {
        return Handlers.routing()
                .get("/", new Dispatcher(new MasterRead(entity, true)))
                .get("/{id}", new Dispatcher(new MasterRead(entity)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    public static RoutingHandler me() {
        return Handlers.routing()
                .get("/", new Dispatcher(new Me()))
                .add(Methods.PATCH, "/", new BlockingHandler(new UpdateMe()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    public static RoutingHandler passwordPolicy() {
        return Handlers.routing()
                .get("/", new Dispatcher(new ReadPasswordPolicy(false)))
                .get("/global", new Dispatcher(new ReadPasswordPolicy(true)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    public static RoutingHandler ftp() {
        return Handlers.routing()
                .get("/", new BlockingHandler(new Ftp()))
                .get("/templates", new BlockingHandler(new Ftp("TEMPLATES")))
                .delete("/", new Dispatcher(new Ftp(true)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    private static EagerFormParsingHandler uploadHandler(HttpHandler next) {
        return new EagerFormParsingHandler(
                FormParserFactory
                        .builder()
                        .addParser(new MultiPartParserDefinition())
                        .build()
        ).setNext(next);
    }
}
