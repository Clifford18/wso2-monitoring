package ke.co.skyworld;

import io.undertow.Handlers;
import io.undertow.Undertow;
import ke.co.skyworld.domain.beans.CommandLine;
import ke.co.skyworld.endpoints.Routes;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.http.Dispatcher;
import ke.co.skyworld.utils.http.mailing.MailerV2;
import ke.co.skyworld.utils.logging.LoggerConfiguration;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import static ke.co.skyworld.utils.Constants.*;
import static ke.co.skyworld.utils.Constants.Table.*;
import static org.fusesource.jansi.Ansi.ansi;

public class
Main {
    public static void main(String[] args) {

        try {
            CommandLine.populateArgs(args);

            //Sort log4j
            LoggerConfiguration.initialize();

            //Sort Jansi Console Art
            AnsiConsole.systemInstall();

            //Set Constants
            populateApplicationCache();

            System.out.println(
                            "███████╗░██████╗██████╗░░░░░░░██╗░░░░░░█████╗░░██████╗░░██████╗░░░░░░░█████╗░██████╗░██╗\n" +
                            "██╔════╝██╔════╝██╔══██╗░░░░░░██║░░░░░██╔══██╗██╔════╝░██╔════╝░░░░░░██╔══██╗██╔══██╗██║\n" +
                            "█████╗░░╚█████╗░██████╦╝█████╗██║░░░░░██║░░██║██║░░██╗░╚█████╗░█████╗███████║██████╔╝██║\n" +
                            "██╔══╝░░░╚═══██╗██╔══██╗╚════╝██║░░░░░██║░░██║██║░░╚██╗░╚═══██╗╚════╝██╔══██║██╔═══╝░██║\n" +
                            "███████╗██████╔╝██████╦╝░░░░░░███████╗╚█████╔╝╚██████╔╝██████╔╝░░░░░░██║░░██║██║░░░░░██║\n" +
                            "╚══════╝╚═════╝░╚═════╝░░░░░░░╚══════╝░╚════╝░░╚═════╝░╚═════╝░░░░░░░╚═╝░░╚═╝╚═╝░░░░░╚═╝");
            System.out.println(ansi().render("@|green ESB Monitoring API|@ @|white (v0.0.1)|@"));
            System.out.println();

            Undertow server = Undertow.builder()
                    .setIoThreads(getIoThreadPool()) //Number of cores x2
                    .setWorkerThreads(getWorkerThreadPool()) //Number of cores x10
                    .addHttpListener(getApiContextPort(), getApiContextHost())
                    //.addHttpsListener(8443, getApiContextHost(), serverSslContext("password", "password", "password"))
                    .setHandler(Handlers.path()
                            /* MASTER */
                            .addPrefixPath(getApiContextPath()+"o", Routes.authorization())
                            .addPrefixPath(getApiContextPath()+"me", Routes.me())

                            .addPrefixPath(getApiContextPath()+"request-logs", Routes.defaultMasterReadingRouter(REQUEST_LOGS))

                            /* OTHERS */
                            .addPrefixPath(getApiContextPath()+"ftp", Routes.ftp())
                    ).build();
            server.start();
            initializeApi();

            System.out.println(ansi()
                    .fg(Ansi.Color.GREEN).a(" Server started at: ")
                    .bold().a(getApiContextHost() + ":" + getApiContextPort()).reset());
            System.out.println("\n");

        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Error starting API: "+e.getMessage());
        }
    }

    private static void initializeApi() {

        //Initialize Repository
        Repository.setup();

    }

    public static SSLContext serverSslContext(String keyStorePassword, String trustStorePassword, String password) {
        try {
            KeyStore keyStore = loadKeyStore("/media/elon/Code/Java/scripts/ssl-certs/keystore1.jks", keyStorePassword);
            KeyStore trustStore = loadKeyStore("/media/elon/Code/Java/scripts/ssl-certs/truststore1.ts", trustStorePassword);
            return createSSLContext(keyStore, trustStore, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext createSSLContext(final KeyStore keyStore, final KeyStore trustStore, String keyStorePassword) throws IOException {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (Exception e) {
            throw new IOException("Unable to create and initialise the SSLContext", e);
        }
    }

    private static KeyStore loadKeyStore(final String name, String password) throws IOException {
        try(InputStream stream = new FileInputStream(name)) {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(stream, password.toCharArray());
            return loadedKeystore;
        } catch (Exception e) {
            throw new IOException(String.format("Unable to load KeyStore %s", name), e);
        }
    }

}