package ke.co.skyworld.endpoints.handlers.sys;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.file_utils.FileOps;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.Encryption;

/**
 * kukuza-microfinance-api (ke.co.scedar.endpoints.handlers.sys)
 * Created by: elon
 * On: 08 Sep, 2018 9/8/18 2:15 AM
 **/
public class Ftp extends ScedarHttpHandler {

    private boolean delete;
    String type;

    public Ftp() {
        delete = false;
        type = "UNKNOWN";
    }

    public Ftp(boolean delete) {
        this.delete = delete;
        type = "UNKNOWN";
    }

    public Ftp(String type) {
        delete = false;
        this.type = type;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        if (requestIsNotAuthentic(exchange)) return;

        try {
            switch (type) {
                case "UNKNOWN":
                {
                    String exposedPath = getQueryParam(exchange, "path");

                    if(delete) {
                        send(exchange, FileOps.deleteFile(Encryption.decrypto(exposedPath)) , StatusCodes.OK);
                    } else send(exchange, exposedPath);

                    JvmManager.gc(exposedPath, exchange);
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "An error occurred while processing your request. Kindly retry later",
                    exchange.getRequestURI(),
                    "Error: "+e.getMessage(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}