package ke.co.skyworld.utils.http.mailing;

import ke.co.skyworld.utils.Constants;

import java.util.HashMap;
import java.util.List;

public class CreateMail {

    private Thread sendMailThread;
    private Thread sendMailsThread;
    private String recipient;
    private String ccRecipient;

    private String bccRecipient;
    private String email;
    private String subject;
    private List<String> recipients;
    private List<String> ccRecipients;
    private List<String> emails;
    private List<String> subjects;
    private HashMap<String, String> attachments = new HashMap<>();

    public CreateMail(String recipient, String subject, String email){
        this.email = email;
        this.subject = subject;
        this.recipient = recipient;
        sendMail();
    }

    public CreateMail(String recipient, String ccRecipient, String subject, String email){
        this(recipient, ccRecipient, subject, email, false);
    }

    public CreateMail(String recipient, String ccRecipient, String subject, String email, boolean sync){
        this.email = email;
        this.subject = subject;
        this.recipient = recipient;
        this.ccRecipient = ccRecipient;
        if(!sync) sendMail();
    }

    public CreateMail(String recipient, String ccRecipient, String bccRecipient, String subject, String email, boolean sync){
        this.email = email;
        this.subject = subject;
        this.recipient = recipient;
        this.ccRecipient = ccRecipient;
        this.bccRecipient = bccRecipient;
        if(!sync) sendMail();
    }

    public CreateMail(List<String> recipients, List<String> subjects, List<String> emails){
        this.emails = emails;
        this.subjects = subjects;
        this.recipients = recipients;
        sendMails();
    }

    public CreateMail(List<String> recipients, List<String> ccRecipients, List<String> subjects, List<String> emails){
        this.emails = emails;
        this.subjects = subjects;
        this.recipients = recipients;
        this.ccRecipients = ccRecipients;
        sendMails();
    }

    public CreateMail() {
    }

    public void sendMails(){
        System.out.println("Initiating send mails thread...");
        Runnable sendEmails = () -> {
            int counter = 0;
            for(String email : this.emails){
                /*Mailer mailer = new Mailer();

                mailer.setEmailSMTPHost(
                        Constants.getMailerSmtpHost());
                mailer.setEmailSMTPStartTLSEnable(
                        Boolean.parseBoolean(
                                Constants.getMailerSmtpEnableTls()));
                mailer.setEmailSMTPPort(
                        Integer.parseInt(
                                Constants.getMailerSmtpPort()));
                mailer.setFromEmailAddress(
                        Constants.getMailerFromEmailAddress());
                mailer.setEmailPassword(
                        Constants.getMailerPassword());
                mailer.setFromName("Mobile Communications Portal | HELB");
                mailer.setToEmailAddresses(this.recipients.get(counter));
                mailer.doSendMail(this.subjects.get(counter),email);*/
                MailerV2 mailer = new MailerV2(
                        Constants.getMailerSmtpHost(),
                        Integer.parseInt(Constants.getMailerSmtpPort()),
                        Constants.getPortalNoReplyEmailAddress(),
                        Constants.getPortalNoReplyPassword(),
                        this.recipients.get(counter),
                        this.subjects.get(counter),
                        email,
                        Constants.getMailerSmtpEncryption()
                );
                mailer.setFromName("Sky Portal | Sky World");
                mailer.setCC(this.ccRecipients.get(counter));
                if(attachments != null && !attachments.isEmpty()) mailer.setAttachments(attachments);
                /*mailer.setCC("derekmutende@mintel.co.ke,mutende.dp@gmail.com");
                mailer.setBCC("mutende.dp@gmail.com");
                mailer.addAttachment("proforma-invoice-skyworld.pdf", "proforma-skyworld.pdf");*/
                mailer.setDebug(Constants.getMailerSmtpDebug());
                mailer.send();
                counter++;
            }
        };
        System.out.println("Dispatching send mails thread...");
        System.out.println("Recipient: " + String.join(",", this.recipients));
        System.out.println("CC Recipient: " + String.join(",", this.ccRecipients));
        this.sendMailsThread = new Thread(sendEmails);
        this.sendMailsThread.start();
        System.out.println("Dispatched send mails thread...");
    }

    public void sendMail(){
        System.out.println("Initiating send mail thread...");

        Runnable sendEmail = () -> {
            /*Mailer mailer = new Mailer();
            mailer.setEmailSMTPHost(
                    Constants.getMailerSmtpHost());
            mailer.setEmailSMTPStartTLSEnable(
                    Boolean.getBoolean(
                            Constants.getMailerSmtpEncryption()));
            mailer.setEmailSMTPPort(
                    Integer.parseInt(
                            Constants.getMailerSmtpPort()));
            mailer.setFromEmailAddress(
                    Constants.getMailerFromEmailAddress());
            mailer.setEmailPassword(
                    Constants.getMailerPassword());
            mailer.setFromName("Mobile Communications Portal | HELB");
            mailer.setToEmailAddresses(this.recipient);
            mailer.doSendMail(this.subject,this.email);*/
            MailerV2 mailer = new MailerV2(
                    Constants.getMailerSmtpHost(),
                    Integer.parseInt(Constants.getMailerSmtpPort()),
                    Constants.getPortalNoReplyEmailAddress(),
                    Constants.getPortalNoReplyPassword(),
                    this.recipient,
                    this.subject,
                    this.email,
                    Constants.getMailerSmtpEncryption()
            );
            mailer.setFromName("Sky Portal | Sky World");
                /*mailer.setCC("derekmutende@mintel.co.ke,mutende.dp@gmail.com");
                mailer.setBCC("mutende.dp@gmail.com");
                mailer.addAttachment("proforma-invoice-skyworld.pdf", "proforma-skyworld.pdf");*/
            if(this.ccRecipient != null && !this.ccRecipient.isEmpty()){
                mailer.setCC(this.ccRecipient);
            }
            if(this.bccRecipient != null && !this.bccRecipient.isEmpty()){
                mailer.setBCC(this.bccRecipient);
            }
            if(attachments != null && !attachments.isEmpty()) mailer.setAttachments(attachments);
            mailer.setDebug(Constants.getMailerSmtpDebug());
            mailer.send();
        };

        System.out.println("Dispatching send mail thread...");
        System.out.println("Recipient: " + this.recipient);
        if(this.ccRecipient != null && !this.ccRecipient.isEmpty()){
            System.out.println("CC Recipient: " + this.ccRecipient);
        }
        if(this.bccRecipient != null && !this.bccRecipient.isEmpty()){
            System.out.println("BCC Recipient: " + this.bccRecipient);
        }
        this.sendMailThread = new Thread(sendEmail);
        this.sendMailThread.start();
        System.out.println("Dispatched send mail thread...");
    }

    public Thread getSendMailThread() {
        return sendMailThread;
    }

    public void setSendMailThread(Thread sendMailThread) {
        this.sendMailThread = sendMailThread;
    }

    public Thread getSendMailsThread() {
        return sendMailsThread;
    }

    public void setSendMailsThread(Thread sendMailsThread) {
        this.sendMailsThread = sendMailsThread;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public void addAttachment(String fileName, String filePath){
        attachments.put(fileName, filePath);
    }

    public void setAttachments(HashMap<String, String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "CreateMail{" +
                "sendMailThread=" + sendMailThread +
                ", sendMailsThread=" + sendMailsThread +
                ", recipient='" + recipient + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", recipients=" + recipients +
                ", emails=" + emails +
                ", subjects=" + subjects +
                '}';
    }
}