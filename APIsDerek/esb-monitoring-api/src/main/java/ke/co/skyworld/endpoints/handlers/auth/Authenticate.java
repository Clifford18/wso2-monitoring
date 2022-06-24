package ke.co.skyworld.endpoints.handlers.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.beans.AuthenticationStatus;
import ke.co.skyworld.domain.enums.AccessTokenScope;
import ke.co.skyworld.domain.enums.ActionType;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.AccessTokenRepository;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.SystemSettingsRepository;
import ke.co.skyworld.repository.UserAccountRepository;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.data_formatting.XmlUtils;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.InMemoryCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sls-api (ke.co.scedar.endpoints.handlers.auth)
 * Created by: elon
 * On: 30 Jun, 2018 6/30/18 12:11 PM
 **/
public class Authenticate extends ScedarHttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        if(!isRequestValid(exchange)) return;

        String username = exchange.getQueryParameters().get("username").getFirst().trim();
        String authHash = exchange.getQueryParameters().get("auth_hash").getFirst().trim();
        String grantType = exchange.getQueryParameters().get("grant_type").getFirst().toLowerCase().trim();
        String client = exchange.getQueryParameters().get("client").getFirst().toLowerCase().trim();
        //String captcha = exchange.getQueryParameters().get("captcha").getFirst().trim();
        //String captchaSourceReference = exchange.getQueryParameters().get("captcha_source_reference").getFirst().trim();

        try{

            //Check captcha
            /*String captchaAnswer = captcha+"_"+captchaSourceReference;
            if(!InMemoryCache.existsAndRemove(captchaAnswer)){
                send(exchange, new ExceptionRepresentation(
                        "Access denied",
                        exchange.getRequestURI(),
                        "Invalid Captcha answer. Please reload Captcha and try again",
                        StatusCodes.FORBIDDEN,
                        exchange.getRequestMethod()
                ), StatusCodes.FORBIDDEN);
                return;
            }*/

            if (grantType.equals("password")){

                AuthenticationStatus authenticationStatus = UserAccountRepository.authenticate(
                        username, authHash, exchange.getRequestHeaders().get("X-Real-IP").getFirst());

                if(authenticationStatus.isAuthenticated()){
                    Set<AccessTokenScope> accessTokenScopes = new HashSet<>();
                    accessTokenScopes.add(AccessTokenScope.GLOBAL);

                    TransactionWrapper transactionWrapper =
                            AccessTokenRepository.generate(authenticationStatus.getUser_account().getValue("user_id").toString(),
                                    exchange.getRequestHeaders().get("X-Real-IP").getFirst(), accessTokenScopes);

                    if(transactionWrapper.hasErrors()){
                        send(exchange, new ExceptionRepresentation(
                                "Error occurred processing authentication details",
                                exchange.getRequestURI(),
                                "Error: " + transactionWrapper.getErrors(),
                                StatusCodes.INTERNAL_SERVER_ERROR,
                                exchange.getRequestMethod()
                        ), StatusCodes.INTERNAL_SERVER_ERROR);
                        return;
                    }

                    FlexicoreHashMap accessTokenRecord = transactionWrapper.getSingleRecord();
                    if (accessTokenRecord != null) {
                        accessTokenRecord.putValue("user_account", authenticationStatus.getUser_account());
                        send(exchange, accessTokenRecord, StatusCodes.OK);
                    } else throw new NullPointerException("Error generating access token");

                }else {
                    send(exchange, new ExceptionRepresentation(
                            "Access denied",
                            exchange.getRequestURI(),
                            authenticationStatus.getMessage(),
                            StatusCodes.FORBIDDEN,
                            exchange.getRequestMethod()
                    ), StatusCodes.FORBIDDEN);

                }
            } else {
                send(exchange, new ExceptionRepresentation(
                        "Unsupported grant type '"+grantType+"'",
                        exchange.getRequestURI(),
                        "Unsupported grant type '"+grantType+"'",
                        StatusCodes.BAD_REQUEST,
                        exchange.getRequestMethod()
                ), StatusCodes.BAD_REQUEST);
            }

        } catch (Exception e){
            e.printStackTrace();
            send(exchange, new ExceptionRepresentation(
                    "Internal Server Error ("+e.getMessage()+")",
                    exchange.getRequestURI(),
                    "Error: "+e.getMessage()+". Caused by: "+e.getCause(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isRequestValid(HttpServerExchange exchange){
        Deque<String> dUsername = exchange.getQueryParameters().get("username");
        Deque<String> dAuthHash = exchange.getQueryParameters().get("auth_hash");
        Deque<String> dGrantType = exchange.getQueryParameters().get("grant_type");
        Deque<String> dClient = exchange.getQueryParameters().get("client");
        //Deque<String> dPortal = exchange.getQueryParameters().get("portal");
        //Deque<String> dCaptcha = exchange.getQueryParameters().get("captcha");
        //Deque<String> dCaptchaSourceReference = exchange.getQueryParameters().get("captcha_source_reference");

        if(dUsername == null || dUsername.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'username' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        if(dAuthHash == null || dAuthHash.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'auth_hash' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        if(dGrantType == null || dGrantType.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'grant_type' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        if(dClient == null || dClient.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'client' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        /*if(dPortal == null || dPortal.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'portal' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }*/

        String grantType = dGrantType.getFirst().trim().toLowerCase();

        if(!(grantType.equals("password") || grantType.equals("biometric"))){
            send(exchange, new ExceptionRepresentation(
                    "Invalid grant_type",
                    exchange.getRequestURI(),
                    "grant_type '"+grantType+"' is invalid. Use 'password' or 'biometric'",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        String client = dClient.getFirst().trim().toLowerCase();

        if(!(client.equals("mobile") || client.equals("web") || client.equals("desktop"))){
            send(exchange, new ExceptionRepresentation(
                    "Invalid client",
                    exchange.getRequestURI(),
                    "client '"+client+"' is invalid. Use 'web', 'mobile' or 'desktop'",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        /*String portal = dPortal.getFirst().trim().toUpperCase();

        if(!(portal.equals("MEMBER") || portal.equals("PARTNER") || portal.equals("ADMIN"))){
            send(exchange, new ExceptionRepresentation(
                    "Invalid portal",
                    exchange.getRequestURI(),
                    "portal '"+portal+"' is invalid. Use 'MEMBER', 'PARTNER' or 'ADMIN'",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        if(dCaptcha == null || dCaptcha.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'captcha' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }

        if(dCaptchaSourceReference == null || dCaptchaSourceReference.getFirst().equals("")){
            send(exchange, new ExceptionRepresentation(
                    "Missing GET parameter",
                    exchange.getRequestURI(),
                    "GET parameter 'captcha_source_reference' is required",
                    StatusCodes.BAD_REQUEST,
                    exchange.getRequestMethod()
            ), StatusCodes.BAD_REQUEST);
            return false;
        }*/

        return true;
    }
}
