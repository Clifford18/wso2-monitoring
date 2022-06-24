package ke.co.skyworld.utils.http.mailing.templates.models;

import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.utils.Constants;

public class OTPModel {

    private String title;
    private String fullName;
    private String otp;
    private String otpDateCreated;
    private String otpExpiryDate;
    private String portalLoginLink;

    public OTPModel(PortalTypes portal, String title, String fullName, String otp, String otpDateCreated, String otpExpiryDate) {
        this.title = title;
        this.fullName = fullName;
        this.otp = otp;
        this.otpDateCreated = otpDateCreated;
        this.otpExpiryDate = otpExpiryDate;
        this.portalLoginLink = Constants.getPortalLoginLink(portal);
    }

    public OTPModel(PortalTypes portal){
        portalLoginLink = Constants.getPortalLoginLink(portal);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpDateCreated() {
        return otpDateCreated;
    }

    public void setOtpDateCreated(String otpDateCreated) {
        this.otpDateCreated = otpDateCreated;
    }

    public String getOtpExpiryDate() {
        return otpExpiryDate;
    }

    public void setOtpExpiryDate(String otpExpiryDate) {
        this.otpExpiryDate = otpExpiryDate;
    }

    public String getPortalLoginLink() {
        return portalLoginLink;
    }

    public void setPortalLoginLink(String portalLoginLink) {
        this.portalLoginLink = portalLoginLink;
    }
}
