package ke.co.skyworld.wso2_monitoring;

public class UserAccounts {

    private int user_id;
    private String user_status;
    private String user_status_description;
    private String user_status_date;
    private String account_access_mode;
    private String username;
    private String first_name;
    private String last_name;
    private int mobile_number;
    private String email_address;
    private String user_pwd_status;
    private String user_pwd;
    private String user_pwd_status_date;
    private int login_attempts;
    private int max_login_attempts;
    private String allowed_access_sources_status;
    private String allowed_access_sources_match_type;
    private int max_allowed_access_sources;
    private String allowed_access_sources;
    private String restricted_access_sources_status;
    private String restricted_access_sources_match_type;
    private int max_restricted_access_sources;
    private String restricted_access_sources;
    private String tracking_id;
    private String tracking_source_ip;
    private String tracking_url;
    private String tracking_time;
    private String tracking_referrer;
    private String gender;
    private String designation;
    private String date_created;
    private String date_modified;

    public UserAccounts(int user_id, String user_status, String user_status_description, String user_status_date, String account_access_mode, String username, String first_name, String last_name, int mobile_number, String email_address, String user_pwd_status, String user_pwd, String user_pwd_status_date, int login_attempts, int max_login_attempts, String allowed_access_sources_status, String allowed_access_sources_match_type, int max_allowed_access_sources, String allowed_access_sources, String restricted_access_sources_status, String restricted_access_sources_match_type, int max_restricted_access_sources, String restricted_access_sources, String tracking_id, String tracking_source_ip, String tracking_url, String tracking_time, String tracking_referrer, String gender, String designation, String date_created, String date_modified) {
        this.user_id = user_id;
        this.user_status = user_status;
        this.user_status_description = user_status_description;
        this.user_status_date = user_status_date;
        this.account_access_mode = account_access_mode;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mobile_number = mobile_number;
        this.email_address = email_address;
        this.user_pwd_status = user_pwd_status;
        this.user_pwd = user_pwd;
        this.user_pwd_status_date = user_pwd_status_date;
        this.login_attempts = login_attempts;
        this.max_login_attempts = max_login_attempts;
        this.allowed_access_sources_status = allowed_access_sources_status;
        this.allowed_access_sources_match_type = allowed_access_sources_match_type;
        this.max_allowed_access_sources = max_allowed_access_sources;
        this.allowed_access_sources = allowed_access_sources;
        this.restricted_access_sources_status = restricted_access_sources_status;
        this.restricted_access_sources_match_type = restricted_access_sources_match_type;
        this.max_restricted_access_sources = max_restricted_access_sources;
        this.restricted_access_sources = restricted_access_sources;
        this.tracking_id = tracking_id;
        this.tracking_source_ip = tracking_source_ip;
        this.tracking_url = tracking_url;
        this.tracking_time = tracking_time;
        this.tracking_referrer = tracking_referrer;
        this.gender = gender;
        this.designation = designation;
        this.date_created = date_created;
        this.date_modified = date_modified;
    }
    public UserAccounts(){

    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_status_description() {
        return user_status_description;
    }

    public void setUser_status_description(String user_status_description) {
        this.user_status_description = user_status_description;
    }

    public String getUser_status_date() {
        return user_status_date;
    }

    public void setUser_status_date(String user_status_date) {
        this.user_status_date = user_status_date;
    }

    public String getAccount_access_mode() {
        return account_access_mode;
    }

    public void setAccount_access_mode(String account_access_mode) {
        this.account_access_mode = account_access_mode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(int mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getUser_pwd_status() {
        return user_pwd_status;
    }

    public void setUser_pwd_status(String user_pwd_status) {
        this.user_pwd_status = user_pwd_status;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public String getUser_pwd_status_date() {
        return user_pwd_status_date;
    }

    public void setUser_pwd_status_date(String user_pwd_status_date) {
        this.user_pwd_status_date = user_pwd_status_date;
    }

    public int getLogin_attempts() {
        return login_attempts;
    }

    public void setLogin_attempts(int login_attempts) {
        this.login_attempts = login_attempts;
    }

    public int getMax_login_attempts() {
        return max_login_attempts;
    }

    public void setMax_login_attempts(int max_login_attempts) {
        this.max_login_attempts = max_login_attempts;
    }

    public String getAllowed_access_sources_status() {
        return allowed_access_sources_status;
    }

    public void setAllowed_access_sources_status(String allowed_access_sources_status) {
        this.allowed_access_sources_status = allowed_access_sources_status;
    }

    public String getAllowed_access_sources_match_type() {
        return allowed_access_sources_match_type;
    }

    public void setAllowed_access_sources_match_type(String allowed_access_sources_match_type) {
        this.allowed_access_sources_match_type = allowed_access_sources_match_type;
    }

    public int getMax_allowed_access_sources() {
        return max_allowed_access_sources;
    }

    public void setMax_allowed_access_sources(int max_allowed_access_sources) {
        this.max_allowed_access_sources = max_allowed_access_sources;
    }

    public String getAllowed_access_sources() {
        return allowed_access_sources;
    }

    public void setAllowed_access_sources(String allowed_access_sources) {
        this.allowed_access_sources = allowed_access_sources;
    }

    public String getRestricted_access_sources_status() {
        return restricted_access_sources_status;
    }

    public void setRestricted_access_sources_status(String restricted_access_sources_status) {
        this.restricted_access_sources_status = restricted_access_sources_status;
    }

    public String getRestricted_access_sources_match_type() {
        return restricted_access_sources_match_type;
    }

    public void setRestricted_access_sources_match_type(String restricted_access_sources_match_type) {
        this.restricted_access_sources_match_type = restricted_access_sources_match_type;
    }

    public int getMax_restricted_access_sources() {
        return max_restricted_access_sources;
    }

    public void setMax_restricted_access_sources(int max_restricted_access_sources) {
        this.max_restricted_access_sources = max_restricted_access_sources;
    }

    public String getRestricted_access_sources() {
        return restricted_access_sources;
    }

    public void setRestricted_access_sources(String restricted_access_sources) {
        this.restricted_access_sources = restricted_access_sources;
    }

    public String getTracking_id() {
        return tracking_id;
    }

    public void setTracking_id(String tracking_id) {
        this.tracking_id = tracking_id;
    }

    public String getTracking_source_ip() {
        return tracking_source_ip;
    }

    public void setTracking_source_ip(String tracking_source_ip) {
        this.tracking_source_ip = tracking_source_ip;
    }

    public String getTracking_url() {
        return tracking_url;
    }

    public void setTracking_url(String tracking_url) {
        this.tracking_url = tracking_url;
    }

    public String getTracking_time() {
        return tracking_time;
    }

    public void setTracking_time(String tracking_time) {
        this.tracking_time = tracking_time;
    }

    public String getTracking_referrer() {
        return tracking_referrer;
    }

    public void setTracking_referrer(String tracking_referrer) {
        this.tracking_referrer = tracking_referrer;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }
}
