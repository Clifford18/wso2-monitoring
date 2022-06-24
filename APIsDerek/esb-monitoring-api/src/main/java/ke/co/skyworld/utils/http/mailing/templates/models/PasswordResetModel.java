package ke.co.skyworld.utils.http.mailing.templates.models;

import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.utils.Constants;

public class PasswordResetModel {

    private String title;
    private String fullName;
    private String password;
    private String portalLoginLink;

    public PasswordResetModel(PortalTypes portal, String title, String fullName, String password) {
        this.title = title;
        this.fullName = fullName;
        this.password = password;
        this.portalLoginLink = Constants.getPortalLoginLink(portal);
    }

    public PasswordResetModel(PortalTypes portal){
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortalLoginLink() {
        return portalLoginLink;
    }

    public void setPortalLoginLink(String portalLoginLink) {
        this.portalLoginLink = portalLoginLink;
    }
}
