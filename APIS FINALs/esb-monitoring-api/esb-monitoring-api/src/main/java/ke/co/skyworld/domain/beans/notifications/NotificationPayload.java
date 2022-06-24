package ke.co.skyworld.domain.beans.notifications;

import ke.co.skyworld.utils.security.ScedarUID;

public class NotificationPayload {

    private String notification_id;
    private String notification_label;
    private String notification_description;
    private String notification_type;
    private Object notification_message;

    public NotificationPayload() {}

    public NotificationPayload(String notification_type){
        this.notification_id = ScedarUID.generateUid(50);
        this.notification_type = notification_type;
    }

    public NotificationPayload(String notification_id, String notification_type){
        this.notification_id = notification_id;
        this.notification_type = notification_type;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_label() {
        return notification_label;
    }

    public void setNotification_label(String notification_label) {
        this.notification_label = notification_label;
    }

    public String getNotification_description() {
        return notification_description;
    }

    public void setNotification_description(String notification_description) {
        this.notification_description = notification_description;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public Object getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(Object notification_message) {
        this.notification_message = notification_message;
    }

    @Override
    public String toString() {
        return "NotificationPayload{" +
                "notificationId='" + notification_id + '\'' +
                ", notificationLabel='" + notification_label + '\'' +
                ", notificationDescription='" + notification_description + '\'' +
                ", notificationType='" + notification_type + '\'' +
                ", notificationMessage=" + notification_message +
                '}';
    }
}
