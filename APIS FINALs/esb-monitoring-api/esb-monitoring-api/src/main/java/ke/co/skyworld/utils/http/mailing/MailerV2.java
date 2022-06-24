package ke.co.skyworld.utils.http.mailing;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import java.util.Base64;
import java.util.HashMap;

/**
 * skyworld-api (ke.co.skyworld.utils.http.mailing)
 * Created by: elon
 * On: 02 Jul, 2021. 23:54
 **/
public class MailerV2 {

    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
    private String fromName;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private String SSLEncryption;
    private String bodyType;
    private boolean debug;
    private HashMap<String, String> attachments = new HashMap<>();

    public MailerV2(String host, int port, String username, String password, String to, String subject, String body, String SSLEncryption, String bodyType, boolean debug) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.from = username;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.SSLEncryption = SSLEncryption;
        this.bodyType = bodyType;
        this.debug = debug;
    }

    public MailerV2(String host, int port, String username, String password, String to, String subject, String body, String SSLEncryption, String bodyType) {
        this(host, port, username, password, to, subject, body, SSLEncryption, bodyType, false);
    }

    public MailerV2(String host, int port, String username, String password, String to, String subject, String body, String SSLEncryption) {
        this(host, port, username, password, to, subject, body, SSLEncryption, "HTML", false);
    }

    public MailerV2(String host, int port, String username, String password, String to, String subject, String body) {
        this(host, port, username, password, to, subject, body, "NONE", "HTML", false);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setCC(String cc) {
        this.cc = cc;
    }

    public void setBCC(String bcc) {
        this.bcc = bcc;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean send(){

        boolean status = false;

        try {
            EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank().from(from).to(to).withSubject(subject);
            if(fromName != null && !fromName.isEmpty()) emailBuilder.from(fromName, from);
            if(bodyType.equals("HTML")) emailBuilder.withHTMLText(body); else emailBuilder.withPlainText(body);
            if(cc != null && !cc.isEmpty()) emailBuilder.ccMultiple(cc.split(","));
            if(bcc != null && !bcc.isEmpty()) emailBuilder.bccMultiple(bcc.split(","));

            for (String fileName : attachments.keySet()){
                DataSource source = new FileDataSource(attachments.get(fileName));
                emailBuilder.withAttachment(fileName, source);
            }

            Email email = emailBuilder.buildEmail();

            MailerRegularBuilderImpl regularBuilder = MailerBuilder.withSMTPServer(host, port, username, password);
            switch (SSLEncryption){
                case "NONE": {
                    regularBuilder.withTransportStrategy(TransportStrategy.SMTP);
                    break;
                }
                case "SSL": {
                    regularBuilder.withTransportStrategy(TransportStrategy.SMTPS);
                    break;
                }
                case "TLS": {
                    regularBuilder.withTransportStrategy(TransportStrategy.SMTP_TLS);
                    break;
                }
            }

            Mailer mailer = regularBuilder
                    .clearEmailAddressCriteria() // turns off email validation
                    .withDebugLogging(debug)
                    .buildMailer();
            mailer.sendMail(email);

            status = true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }

    public void addAttachment(String fileName, String filePath){
        attachments.put(fileName, filePath);
    }

    public void setAttachments(HashMap<String, String> attachments) {
        this.attachments = attachments;
    }
}
