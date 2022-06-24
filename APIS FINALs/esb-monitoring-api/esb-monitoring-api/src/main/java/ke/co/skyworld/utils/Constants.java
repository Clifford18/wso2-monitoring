package ke.co.skyworld.utils;

import ke.co.skyworld.Main;
import ke.co.skyworld.domain.beans.CommandLine;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.domain.enums.db.DatabaseServer;
import ke.co.skyworld.utils.logging.Log;
import ke.co.skyworld.utils.security.Encryption;
import ke.co.skyworld.utils.data_formatting.XmlUtils;

import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class Constants {

    public static final String ADMIN_ORGANIZATION_NAME = "Sky World Limited";
    public static final String APPLICATION_NAME = "Sky World API";
    public static final String APPLICATION_CODE = "SKY_PORTAL";
    public static final String APPLICATION_VERSION = "0.2.2";

    /*=======================================================================================*/
    /*                              STATICS                                                  */
    /*=======================================================================================*/
    public static final String REMOVE_BLANK_LINES_XSLT = "xml-transformer-remove-blank-lines.xslt";

    public static String PASSWORD_POLICY_XSD;
    public static String PERMISSIONS_XSD;
    public static String DASHBOARD_STATS_XSD;
    public static String CREDIT_TOP_UP_XSD;
    public static String EXTERNAL_API_CREDIT_TOP_UP_XSD;
    public static String EXTERNAL_API_PESA_IN_XSD;
    public static String MSG_PRODUCT_XML_PARAMETERS_XSD;

    public static String WRAPPER_XSD;
    static {
        try {
            PASSWORD_POLICY_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/password_policy.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-1", "Error loading PASSWORD_POLICY_XSD 'xsds/password_policy.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            PERMISSIONS_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/permissions.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-2", "Error loading PERMISSIONS_XSD 'xsds/password_policy.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            DASHBOARD_STATS_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/dashboard_stats.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-3", "Error loading DASHBOARD_STATS_XSD 'xsds/dashboard_stats.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            CREDIT_TOP_UP_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/credit_top_up.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-4", "Error loading CREDIT_TOP_UP_XSD 'xsds/credit_top_up.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            EXTERNAL_API_CREDIT_TOP_UP_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/external_api_credit_top_up.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-5", "Error loading EXTERNAL_API_CREDIT_TOP_UP_XSD 'xsds/external_api_credit_top_up.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            EXTERNAL_API_PESA_IN_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/pesa_in_gw.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-5", "Error loading EXTERNAL_API_PESA_IN_XSD 'xsds/pesa_in_gw.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            MSG_PRODUCT_XML_PARAMETERS_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/msg_product_xml_parameters.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-6", "Error loading MSG_PRODUCT_XML_PARAMETERS_XSD 'xsds/msg_product_xml_parameters.xsd' - "+e.getMessage());
            System.exit(0);
        }

        try {
            WRAPPER_XSD = Misc.readInputStream(Main.class.getClassLoader().getResourceAsStream("xsds/wrapper.xsd"));
        } catch (Exception e) {
            Log.error(Constants.class, "static-6", "Error loading WRAPPER_XSD 'xsds/wrapper.xsd' - "+e.getMessage());
            System.exit(0);
        }
    }

    /*=======================================================================================*/
    /*                              APP TYPES                                                */
    /*=======================================================================================*/
    public static final String XML_PATH_TO_APP_TYPE = "/APP/@TYPE";
    public static final String XML_PATH_TO_APP_FILE_PATH = "/APP/FILE_PATH";
    public static final String XML_PATH_TO_APP_FILE_NAME = "/APP/FILE_NAME";
    public static final String XML_PATH_TO_APP_FILE_SIZE = "/APP/FILE_SIZE";
    public static final String XML_PATH_TO_APP_RECORD_COUNT = "/APP/RECORD_COUNT";
    public static final String XML_PATH_TO_APP_COST = "/APP/COST";
    public static final String XML_PATH_TO_APP_SMS = "/APP/SMS";
    public static final String XML_PATH_TO_APP_COST_ERROR = "/APP/COST/@ERROR";

    //APP = MSG CODE = 100
    public static final String APP_TYPE_COMPOSE_SMS_FORM = "COMPOSE_SMS_FORM";
    public static final String APP_TYPE_COMPOSE_SMS_CONTACTS_FILE = "COMPOSE_SMS_CONTACTS_FILE";
    public static final String APP_TYPE_SEND_SMS_FROM_FILE = "SEND_SMS_FROM_FILE";

    public static final String APP_TYPE_DND_FORM = "DND_FORM";
    public static final String APP_TYPE_WHITELIST_FORM = "WHITELIST_FORM";
    public static final String APP_TYPE_BLACKLIST_FORM = "BLACKLIST_FORM";
    public static final String APP_TYPE_DND_FILE = "DND_FILE";
    public static final String APP_TYPE_WHITELIST_FILE = "WHITELIST_FILE";
    public static final String APP_TYPE_BLACKLIST_FILE = "BLACKLIST_FILE";

    public static final String APP_TYPE_CHANNELS_FORM = "CHANNELS_FORM";
    public static final String APP_TYPE_QUESTIONNAIRES_FORM = "QUESTIONNAIRES_FORM";
    public static final String APP_TYPE_QUESTIONS_FORM = "QUESTIONS_FORM";
    public static final String APP_TYPE_QUESTIONNAIRE_AND_QUESTIONS_FORM = "QUESTIONNAIRE_AND_QUESTIONS_FORM";
    public static final String APP_TYPE_QUESTIONS_ORDER_FORM = "QUESTIONS_ORDER_FORM";
    public static final String APP_TYPE_PUBLISHING_FORM = "PUBLISHING_FORM";
    public static final String APP_TYPE_PUBLISHING_ORDER_FORM = "PUBLISHING_ORDER_FORM";
    public static final String APP_TYPE_TRIGGER_SURVEY_FORM = "TRIGGER_SURVEY_FORM";
    public static final String APP_TYPE_SURVEY_COMPLETION_REMINDER_FORM = "SURVEY_COMPLETION_REMINDER_FORM";

    public static final String APP_TYPE_MSG_TRANSACTION_MANAGEMENT = "MSG_TRANSACTION_MANAGEMENT";
    public static final String APP_TYPE_PESA_OUT_TRANSACTION_MANAGEMENT = "PESA_OUT_TRANSACTION_MANAGEMENT";
    public static final String APP_TYPE_PESA_IN_TRANSACTION_MANAGEMENT = "PESA_IN_TRANSACTION_MANAGEMENT";

    public static final String APP_TYPE_MSG_LENGTH_FORM = "MSG_LENGTH_FORM";
    public static final String APP_TYPE_MSG_TEMPLATE_FORM = "MSG_TEMPLATE_FORM";

    public static final String APP_TYPE_USSD_MENU_MANAGEMENT = "USSD_MENU_MANAGEMENT";
    public static final String APP_TYPE_USSD_MENU_GROUP_MANAGEMENT = "USSD_MENU_GROUP_MANAGEMENT";

    public static final String APP_TYPE_CREDIT_TOP_UP = "CREDIT_TOP_UP";

    public static final String APP_TYPE_ATM_LOGS = "ATM_LOGS";
    public static final String APP_TYPE_MEMBERS_PORTAL_LOGS = "MEMBERS_PORTAL_LOGS";
    public static final String APP_CODE_ATM_LOGS = "101";
    public static final String APP_CODE_MEMBERS_PORTAL_LOGS = "201";

    public static final String APP_ACTIONS_ADD = "ADD";
    public static final String APP_ACTIONS_EDIT = "EDIT";
    public static final String APP_ACTIONS_DELETE = "DELETE";
    public static final String APP_ACTIONS_HOLD = "HOLD";
    public static final String APP_ACTIONS_UNHOLD = "UNHOLD";
    public static final String APP_ACTIONS_SEND = "SEND";
    public static final String APP_ACTIONS_COMPLETE = "COMPLETE";
    public static final String APP_ACTIONS_REVERSE = "REVERSE";
    //public static final String APP_ACTIONS_RESEND = "RESEND";
    public static final String APP_ACTIONS_REJECT = "REJECT";
    public static final String APP_ACTIONS_BLACKLIST = "BLACKLIST";

    public static final String AMOUNT_TYPE_CREDIT = "CREDIT";
    public static final String AMOUNT_TYPE_DEBIT = "DEBIT";

    public static final int MEMBER_STATUS_ACTIVE = 100;
    public static final int MEMBER_STATUS_PENDING_APPROVAL = 101;
    public static final int MEMBER_STATUS_APPROVED = 102;
    public static final int MEMBER_STATUS_PENDING_VERIFICATION = 103;
    public static final int MEMBER_STATUS_VERIFIED = 104;
    public static final int MEMBER_STATUS_DISABLED = 105;
    public static final int MEMBER_STATUS_SUSPENDED = 106;
    public static final int MEMBER_STATUS_UNKNOWN = 999;

    public static final int MSG_STATUS_CODE_QUEUED = 10;
    public static final String MSG_STATUS_NAME_QUEUED = "QUEUED";
    public static final String MSG_STATUS_DESCRIPTION_QUEUED = "Queued Bulk SMS";
    public static final int MSG_STATUS_CODE_REJECTED = 811;
    public static final String MSG_STATUS_NAME_REJECTED = "MESSAGE_REJECTED";
    public static final String MSG_STATUS_DESCRIPTION_REJECTED = "Bulk SMS Rejected";
    public static final String MSG_FLAG_TYPE_NONE = "NONE";
    public static final String MSG_FLAG_NOTE_NONE = "No flag applied";

    public static final int MSG_STATUS_CODE_HOLD = 905;
    public static final String MSG_STATUS_NAME_HOLD = "HOLD";
    public static final String MSG_FLAG_TYPE_HOLD = "HOLD";
    public static final String MSG_FLAG_NOTE_HOLD = "HOLD applied. Message set on HOLD for further action by User ID: %s";

    public static final String PESA_TYPE_PESA_IN = "PESA_IN";
    public static final String PESA_TYPE_PESA_OUT = "PESA_OUT";
    public static final String PESA_TYPE_C2B = "C2B";
    public static final String PESA_TYPE_B2C = "B2C";

    public static final int PESA_STATUS_CODE_QUEUED = 10;
    public static final String PESA_STATUS_NAME_QUEUED = "QUEUED";
    public static final String PESA_STATUS_DESCRIPTION_QUEUED = "Queued PESA Transaction";
    public static final int PESA_STATUS_CODE_REJECTED = 811;
    public static final String PESA_STATUS_NAME_REJECTED = "TRANSACTION_REJECTED";
    public static final String PESA_STATUS_DESCRIPTION_REJECTED = "PESA Transaction Rejected";
    public static final String PESA_IN_STATUS_DESCRIPTION_QUEUED = "New MPESA C2B";
    public static final String PESA_FLAG_TYPE_NONE = "NONE";
    public static final String PESA_FLAG_NOTE_NONE = "No flag applied";

    public static final int PESA_STATUS_CODE_REVERSE = 201;
    public static final String PESA_STATUS_NAME_REVERSE = "REVERSE_REQUEST";
    public static final String PESA_STATUS_DESCRIPTION_REVERSE = "Reverse PESA Transaction Request";

    public static final int PESA_STATUS_CODE_CONFIRMED = 105;
    public static final String PESA_RESULT_SUBMIT_STATUS_PENDING = "PENDING";
    public static final String PESA_RESULT_SUBMIT_STATUS_ERROR = "ERROR";
    public static final String PESA_RESULT_SUBMIT_STATUS_FAILED = "FAILED";

    public static final int PESA_STATUS_CODE_HOLD = 905;
    public static final String PESA_STATUS_NAME_HOLD = "HOLD";
    public static final String PESA_FLAG_TYPE_HOLD = "HOLD";
    public static final String PESA_FLAG_NOTE_HOLD = "HOLD applied. Transaction put on HOLD for further action by User ID: %s";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_DORMANT = "DORMANT";
    public static final String STATUS_ENABLED = "ENABLED";
    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_RESET = "RESET";
    public static final String STATUS_LOCKED = "LOCKED";
    public static final String YES = "YES";
    public static final String NO = "NO";

    public static final String DT_STRING = "STRING";
    public static final String DT_INTEGER = "INTEGER";
    public static final String DT_DOUBLE = "DOUBLE";
    public static final String DT_DATE = "DATE";
    public static final String DT_TEXT = "TEXT";
    public static final String DT_XML = "XML";
    public static final String DT_JSON = "JSON";
    public static final String DT_SELECT_OPTION = "SELECT_OPTION";
    public static final String DT_FORMAT_YES = "YES";
    public static final String DT_FORMAT_NO = "NO";

    public static final String VALIDATION_STARTED = "VALIDATION_STARTED";
    public static final String VALIDATION_IN_PROGRESS = "VALIDATION_IN_PROGRESS";
    public static final String VALIDATION_SUCCESSFUL = "VALIDATION_SUCCESSFUL";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";

    public static final String WF_STATUS_DRAFT = "DRAFT";
    public static final String WF_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String WF_STATUS_REVIEW = "REVIEW";
    public static final String WF_STATUS_COMPLETED = "COMPLETED";
    public static final String WF_STATUS_DISCARDED = "DISCARDED";
    public static final String WF_STATUS_PENDING = "PENDING";
    public static final String WF_STATUS_APPROVED = "APPROVED";
    public static final String WF_STATUS_REJECTED = "REJECTED";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_FAILED = "FAILED";

    public static final String REPORT_GEN_STATUS_PENDING = "PENDING";
    public static final String REPORT_GEN_STATUS_PROGRESS = "PROGRESS";
    public static final String REPORT_GEN_STATUS_FAILED = "FAILED";
    public static final String REPORT_GEN_STATUS_ERROR = "ERROR";
    public static final String REPORT_GEN_STATUS_CANCELLED = "CANCELLED";
    public static final String REPORT_GEN_STATUS_COMPLETE = "COMPLETED";
    public static final String REPORT_GEN_WORKER_ID = "REPORT_GEN_WORKER_mS9vAaHGkqECmDbPCAjMbyIycJwOldJ7_";

    public static final String ACCOUNT_ACCESS_MODE_INTERACTIVE = "INTERACTIVE";
    public static final String ACCOUNT_ACCESS_MODE_SERVICE = "SERVICE";
    public static final String ACCOUNT_ACCESS_MODE_API = "API";
    public static final String ACCOUNT_ACCESS_MODE_HYBRID = "HYBRID";

    public static final String HTTP_CLIENT_ERROR = "HTTP_CLIENT_ERROR";

    public static final String WHITELIST = "WHITELIST";
    public static final String BLACKLIST = "BLACKLIST";
    public static final String DND = "DND";
    public static final String WHITELIST_URI = "whitelist-register";
    public static final String BLACKLIST_URI = "blacklist-register";
    public static final String DND_URI = "dnd-register";

    public static final String MIN_MSG_LENGTH_ACTION_NONE = "NONE";
    public static final String MIN_MSG_LENGTH_ACTION_REJECT = "REJECT";
    public static final String MAX_MSG_LENGTH_ACTION_NONE = "NONE";
    public static final String MAX_MSG_LENGTH_ACTION_TRUNCATE = "TRUNCATE";
    public static final String MAX_MSG_LENGTH_ACTION_REJECT = "REJECT";

    /*=======================================================================================*/
    /*                              SYSTEM SETTINGS                                          */
    /*=======================================================================================*/
    public static final String SST_MODEL_INFORMATION = "MODEL_INFORMATION";
    public static final String SST_PASSWORD_POLICY = "PASSWORD_POLICY";
    public static final String SST_DASHBOARD_STATS = "DASHBOARD_STATS";
    public static final String SST_CREDIT_TOP_UP_NOTIFICATION = "CREDIT_TOP_UP_NOTIFICATION";
    public static final String SST_VIEW = "MODEL_VIEW";
    public static final String SST_MFA_MODES = "MFA_MODES";
    public static final String SST_SYSTEM_MSG_PARAMETERS = "SYSTEM_MSG_PARAMETERS";
    public static final String SST_PESA_IN_PARAMETERS = "PESA_IN_PARAMETERS";
    public static final String SSS_GLOBAL = "GLOBAL";
    public static final String SSS_MEMBER = "MEMBER";
    public static final String SSS_PARTNER = "PARTNER";
    public static final String SSS_ADMIN = "ADMIN";


    /*=======================================================================================*/
    /*                              NOTIFICATION TYPES                                                */
    /*=======================================================================================*/
    public static final String NT_PROGRESS = "PROGRESS";
    public static final String NT_NORMAL = "NORMAL";
    public static final String NT_COMPOSE_SMS_CONTACTS_FILE = "COMPOSE_SMS_CONTACTS_FILE";
    public static final String NT_SEND_SMS_FROM_FILE = "SEND_SMS_FROM_FILE";
    public static final String NT_DND_FILE = "DND_FILE";
    public static final String NT_BLACKLIST_FILE = "BLACKLIST_FILE";
    public static final String NT_WHITELIST_FILE = "WHITELIST_FILE";
    public static final String NT_DND_FORM = "DND_FORM";
    public static final String NT_BLACKLIST_FORM = "BLACKLIST_FORM";
    public static final String NT_WHITELIST_FORM = "WHITELIST_FORM";
    public static final String NT_TRIGGER_SURVEY = "TRIGGER_SURVEY";
    public static final String NT_SURVEY_COMPLETION = "SURVEY_COMPLETION";
    public static final String NT_UNKNOWN = "UNKNOWN";
    public static final String NT_REPORT_GENERATION = "REPORT_GENERATION";
    public static final String NT_WORKFLOW_APPROVED = "WORKFLOW_APPROVED";
    public static final String NT_WORKFLOW_REVIEW = "WORKFLOW_REVIEW";
    public static final String NT_WORKFLOW_REJECTED = "WORKFLOW_REJECTED";
    public static final String NT_WORKFLOW_COMPLETED = "WORKFLOW_COMPLETED";
    public static final String NT_WORKFLOW_DISCARDED = "WORKFLOW_DISCARDED";
    public static final String NT_TRANSACTION_MANAGEMENT = "TRANSACTION_MANAGEMENT";

    public static final String NTS_READ = "READ";
    public static final String NTS_UNREAD = "UNREAD";
    public static final String NTS_UNSEEN = "UNSEEN";

    public static final String WFNT_FILE_VERIFICATION_COMPLETE = "Workflow file verification completed";
    public static final String WFNT_CREATED = "Workflow initiated";
    public static final String WFNT_APPROVED_AT_LEVEL = "Workflow approved at level ";


    /*=======================================================================================*/
    /*                              CONFIG FILE VARS & DEFAULTS                              */
    /*=======================================================================================*/
    public static final String mySql = "MySQL";
    public static final String microsoftSql = "MicrosoftSQL";
    public static final String oracle = "Oracle";
    public static final String postgreSql = "PostgreSQL";

    public static final String applicationJson = "application/json";
    public static final String applicationXml = "application/xml";
    public static final String textXml = "text/xml";

    public static final String REQ_PORTAL = "req_ctx_portal";
    public static final String REQ_USER_ID = "req_ctx_user_id";
    public static final String REQ_GROUP_IDS = "req_ctx_group_ids";
    public static final String REQ_PORTAL_TYPE_ID = "req_ctx_member_partner_admin_id";
    public static final String REQ_USERNAME = "req_ctx_username";
    public static final String REQ_GROUP_TYPE = "req_ctx_group_type";
    public static final String REQ_USER_APP_RIGHTS = "req_ctx_user_app_rights"; // APP_CODE:APPROVAL_LVL,
    public static final String REQ_ACCESS_TOKEN = "req_ctx_access_token";
    public static final String REQ_REMOTE_ADDR = "req_ctx_remote_address";
    public static final String REQ_DB_REMOTE_ADDR = "req_ctx_remote_address_from_db";
    public static final String REQ_ACTIVE_PAGE = "req_ctx_active_page";
    public static final String REQ_URI = "req_ctx_uri";
    public static final String MARSHALL_ERROR = "req_ctx_marshall_error";
    public static final String FILTER = "filter";
    public static final String FIELDS = "fields";
    public static final String ORDER_BY = "orderBy";
    public static final String FETCH_STRATEGY = "fetchStrategy";

    public static final String FETCH_STRATEGY_COUNT = "COUNT";
    public static final String FETCH_STRATEGY_LAZY = "LAZY";
    public static final String FETCH_STRATEGY_EAGER = "EAGER";

    /*=======================================================================================*/
    /*                              DATE TIME                                                */
    /*=======================================================================================*/

    //Date Time Formats
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    public static final String TIMESTAMP_MS_FORMAT = "yyyyMMddHHmmssSSS";

    // Just Now Desc.
    public static final String JUST_NOW = "Just now";

    /*=======================================================================================*/
    /*                              REPORTS                                                  */
    /*=======================================================================================*/
    public static final String SUPPORTED_RELATIONS = "eq,neq,lt,lte,gt,gte,like,contains,sw,ew,in,!eq,!neq,!lt,!lte,!gt,!gte,!like,!contains,!sw,!ew,!in";
    public static final String RELATION_EQ = "eq";
    public static final String RELATION_LT = "lt";
    public static final String RELATION_LTE = "lte";
    public static final String RELATION_GT = "gt";
    public static final String RELATION_GTE = "gte";
    public static final String RELATION_LIKE = "like";
    public static final String RELATION_ILIKE = "ilike";
    public static final String RELATION_CONTAINS = "contains";
    public static final String RELATION_ICONTAINS = "icontains";
    public static final String RELATION_SW = "sw";
    public static final String RELATION_ISW = "isw";
    public static final String RELATION_EW = "ew";
    public static final String RELATION_IEW = "iew";
    public static final String RELATION_IN = "in";
    public static final String RELATION_NEQ = "!eq";
    public static final String RELATION_NLT = "!lt";
    public static final String RELATION_NLTE = "!lte";
    public static final String RELATION_NGT = "!gt";
    public static final String RELATION_NGTE = "!gte";
    public static final String RELATION_NLIKE = "!like";
    public static final String RELATION_NILIKE = "!ilike";
    public static final String RELATION_NCONTAINS = "!contains";
    public static final String RELATION_NICONTAINS = "!icontains";
    public static final String RELATION_NSW = "!sw";
    public static final String RELATION_NISW = "!isw";
    public static final String RELATION_NEW = "!ew";
    public static final String RELATION_NIEW = "!iew";
    public static final String RELATION_NIN = "!in";

    public static final String SUPPORTED_FIND_BY_STRATEGIES = "and,or";
    public static final String SFBS = " and , or ";

    /*=======================================================================================*/
    /*                              MISCELLANEOUS                                            */
    /*=======================================================================================*/
    public static final String ENCRYPTED = "ENCRYPTED";
    public static final String CLEARTEXT = "CLEARTEXT";
    public static final String SKY_DELIMITER = "&s*k@y{$d}$&";
    public static final String FILE_PROCESSING_PREF_MEMORY = "MEMORY";
    public static final String FILE_PROCESSING_PREF_STREAM = "STREAM";

    public static final String LOCAL_CONF_FILENAME = CommandLine.getArg("-c") == null ? "conf.xml" : CommandLine.getArg("-c");

    public static final String XML_PATH_TO_UNDERTOW_IO_THREAD_POOL = "/API/CONTEXT/UNDERTOW/@IO_THREAD_POOL";
    public static final String XML_PATH_TO_UNDERTOW_WORKER_THREAD_POOL = "/API/CONTEXT/UNDERTOW/@WORKER_THREAD_POOL";

    public static final String XML_PATH_TO_TOKEN_EXPIRY = "/API/CONTEXT/API/AUTH/ACCESS_TOKEN_EXPIRY";

    public static final String XML_PATH_TO_API_REQUEST_DUMP = "/API/@REQUEST_DUMP";
    public static final String XML_PATH_TO_AUTH_MULTIPLE_SAME_USER_SESSIONS = "/API/AUTHENTICATION/@MULTIPLE_SAME_USER_SESSIONS";
    public static final String XML_PATH_TO_AUTH_ACCESS_TOKEN_LENGTH = "/API/AUTHENTICATION/ACCESS_TOKEN/LENGTH";
    public static final String XML_PATH_TO_AUTH_ACCESS_TOKEN_TIMEOUT = "/API/AUTHENTICATION/ACCESS_TOKEN/TIMEOUT";
    public static final String XML_PATH_TO_AUTH_ACCESS_TOKEN_TIMEOUT_TIME_UNIT = "/API/AUTHENTICATION/ACCESS_TOKEN/TIMEOUT/@TIME_UNIT";

    public static final String XML_PATH_TO_AUTH_CAPTCHA_WIDTH = "/API/AUTHENTICATION/CAPTCHA/@WIDTH";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_HEIGHT = "/API/AUTHENTICATION/CAPTCHA/@HEIGHT";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_LENGTH = "/API/AUTHENTICATION/CAPTCHA/@CAPTCHA_LENGTH";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_BORDER = "/API/AUTHENTICATION/CAPTCHA/@BORDER";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_BACKGROUND = "/API/AUTHENTICATION/CAPTCHA/@BACKGROUND";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_NOISE_LEVEL = "/API/AUTHENTICATION/CAPTCHA/@NOISE_LEVEL";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_ALPHANUMERIC = "/API/AUTHENTICATION/CAPTCHA/@CAPTCHA_ALPHANUMERIC";
    public static final String XML_PATH_TO_AUTH_AUDIO_CAPTCHA = "/API/AUTHENTICATION/CAPTCHA/@AUDIO_CAPTCHA";
    public static final String XML_PATH_TO_AUTH_CAPTCHA_TTL = "/API/AUTHENTICATION/CAPTCHA/@CAPTCHA_TTL";

    public static final String XML_PATH_TO_PAGE_SIZE = "/API/PAGINATION/PAGE_SIZE";

    public static final String XML_PATH_TO_PORTAL_HOST_PREFIX_PATH = "/API/CONTEXT/PORTAL/HOST/@PREFIX";
    public static final String XML_PATH_TO_PORTAL_HOST_NAME_PATH = "/API/CONTEXT/PORTAL/HOST/@NAME";
    public static final String XML_PATH_TO_PORTAL_PATHS_LOGIN_PATH = "/API/CONTEXT/PORTAL/PATHS/@LOGIN";
    public static final String XML_PATH_TO_PORTAL_PATHS_WORKFLOW_PATH = "/API/CONTEXT/PORTAL/PATHS/@WORKFLOW";

    public static final String XML_PATH_TO_DASHBOARD_STATS_INFO_WIDGET_PERIOD_PATH = "/API/CONTEXT/PORTAL/STATISTICS/DASHBOARD/INFO_WIDGET/@PERIOD";
    public static final String XML_PATH_TO_DASHBOARD_STATS_INFO_WIDGET_TIME_UNIT_PATH = "/API/CONTEXT/PORTAL/STATISTICS/DASHBOARD/INFO_WIDGET/@TIME_UNIT";

    public static final String XML_PATH_TO_MSG_LOGS_HISTORY_PERIOD_PATH = "/API/CONTEXT/PORTAL/LOGS/MSG/@HISTORY";
    public static final String XML_PATH_TO_MSG_LOGS_HISTORY_TIME_UNIT_PATH = "/API/CONTEXT/PORTAL/LOGS/MSG/@TIME_UNIT";
    public static final String XML_PATH_TO_PESA_LOGS_HISTORY_PERIOD_PATH = "/API/CONTEXT/PORTAL/LOGS/PESA/@HISTORY";
    public static final String XML_PATH_TO_PESA_LOGS_HISTORY_TIME_UNIT_PATH = "/API/CONTEXT/PORTAL/LOGS/PESA/@TIME_UNIT";

    public static final String XML_PATH_TO_CONTEXT_PATH = "/API/CONTEXT/PATH";
    public static final String XML_PATH_TO_CONTEXT_CLIENT_ROOT_DIR = "/API/CONTEXT/CLIENT/ROOT_DIR";
    public static final String XML_PATH_TO_CONTEXT_HOST = "/API/CONTEXT/HOST";
    public static final String XML_PATH_TO_CONTEXT_PORT = "/API/CONTEXT/PORT";
    public static final String XML_PATH_TO_CONTEXT_ENABLE_BASIC_AUTH = "/API/CONTEXT/ENABLE_BASIC_AUTH";

    public static final String XML_PATH_TO_CONTEXT_TIME_ZONE = "/API/CONTEXT/TIME_ZONE";

    public static final String XML_PATH_TO_UNDERTOW_ENABLE_ACCESS_LOG = "/API/CONTEXT/UNDERTOW/ENABLE_ACCESS_LOG";
    public static final String XML_PATH_TO_UNDERTOW_ACCESS_LOG_DIR = "/API/CONTEXT/UNDERTOW/ACCESS_LOG_DIR";
    public static final String XML_PATH_TO_UNDERTOW_ACCESS_LOG_PATTERN = "/API/CONTEXT/UNDERTOW/ACCESS_LOG_PATTERN";
    public static final String XML_PATH_TO_UNDERTOW_ENABLE_COMPRESSION = "/API/CONTEXT/UNDERTOW/ENABLE_COMPRESSION";
    public static final String XML_PATH_TO_UNDERTOW_COMPRESSION_MIN_RESPONSE_SIZE = "/API/CONTEXT/UNDERTOW/COMPRESSION_MIN_RESPONSE_SIZE";

    public static final String XML_PATH_TO_FILE_PROCESSING_PREF = "/API/PROCESSORS/FILE_PROCESSOR/@PROCESSING_PREF";
    public static final String XML_PATH_TO_REPORT_GEN_ENABLE_COMPRESSION = "/API/PROCESSORS/REPORT_GENERATOR/@ENABLE_COMPRESSION";
    public static final String XML_PATH_TO_REPORT_GEN_COMPRESSION_FORMAT = "/API/PROCESSORS/REPORT_GENERATOR/@COMPRESSION_FORMAT";
    public static final String XML_PATH_TO_REPORT_GEN_BUFFER_SIZE = "/API/PROCESSORS/REPORT_GENERATOR/@BUFFER_SIZE";
    public static final String XML_PATH_TO_REPORT_GEN_PAGE_SIZE = "/API/PROCESSORS/REPORT_GENERATOR/XML_BASED/@PAGE_SIZE";

    private static final String XML_PATH_TO_SERVER= "/API/DB/SERVER";
    private static final String XML_PATH_TO_HOST = "/API/DB/HOST";
    private static final String XML_PATH_TO_PORT = "/API/DB/PORT";
//    private static final String XML_PATH_TO_DATABASE_NAME = "/API/DB/NAME";
    public static final String XML_PATH_TO_MASTER_DATABASE_NAME = "/API/DB/NAMES/@MASTER";
    public static final String XML_PATH_TO_PORTAL_DATABASE_NAME = "/API/DB/NAMES/@PORTAL";
    public static final String XML_PATH_TO_MSG_DATABASE_NAME = "/API/DB/NAMES/@MSG";
    public static final String XML_PATH_TO_PESA_DATABASE_NAME = "/API/DB/NAMES/@PESA";
    public static final String XML_PATH_TO_USSD_DATABASE_NAME = "/API/DB/NAMES/@USSD";
    public static final String XML_PATH_TO_REGISTER_DATABASE_NAME = "/API/DB/NAMES/@REGISTER";
    public static final String XML_PATH_TO_MAPP_DATABASE_NAME = "/API/DB/NAMES/@MAPP";
    public static final String XML_PATH_TO_SURVEY_DATABASE_NAME = "/API/DB/NAMES/@SURVEY";
    public static final String XML_PATH_TO_MSG_BUFFER_DATABASE_NAME = "/API/DB/NAMES/@MSG_BUFFER";
    private static final String XML_PATH_TO_DB_CONN_HEALTH_CHECK_ENABLED = "/API/DB/CONNECTION_HEALTH_CHECK/@ENABLED";
    private static final String XML_PATH_TO_DB_CONN_HEALTH_CHECK_INTERVAL = "/API/DB/CONNECTION_HEALTH_CHECK/@INTERVAL";
    private static final String XML_PATH_TO_DB_PASSWORD_TYPE = "/API/DB/PASSWORD/@TYPE";
    private static final String XML_PATH_TO_DB_USERNAME = "/API/DB/USERNAME";
    private static final String XML_PATH_TO_DB_PASSWORD = "/API/DB/PASSWORD";
    private static final String XML_PATH_TO_CONN_METADATA = "/API/DB/CONN_METADATA";
    private static final String XML_PATH_TO_DRIVER_CLASS = "/API/DB/DRIVER_CLASS";
    private static final String XML_PATH_TO_DB_INITIALIZE = "/API/DB/INITIALIZE";
    private static final String XML_PATH_TO_DB_SHOW_SQL = "/API/DB/SHOW_SQL";

    private static final String SLING_RING_INITIAL_POOL_SIZE = "/API/DB/SLING_RING/INITIAL_POOL_SIZE/@VALUE";
    private static final String SLING_RING_MAXIMUM_POOL_SIZE = "/API/DB/SLING_RING/MAXIMUM_POOL_SIZE/@VALUE";
    private static final String SLING_RING_EXTRA_CONNS_SIZE = "/API/DB/SLING_RING/EXTRA_CONNS_SIZE/@VALUE";
    private static final String SLING_RING_FIND_FREE_CONN_AFTER = "/API/DB/SLING_RING/FIND_FREE_CONN_AFTER/@VALUE";
    private static final String SLING_RING_FIND_FREE_CONN_AFTER_TIME_UNIT = "/API/DB/SLING_RING/FIND_FREE_CONN_AFTER/@TIME_UNIT";
    private static final String SLING_RING_DOWNSIZE_AFTER = "/API/DB/SLING_RING/DOWNSIZE_AFTER/@VALUE";
    private static final String SLING_RING_DOWNSIZE_AFTER_TIME_UNIT = "/API/DB/SLING_RING/DOWNSIZE_AFTER/@TIME_UNIT";
    private static final String SLING_RING_PING_AFTER = "/API/DB/SLING_RING/PING_AFTER/@VALUE";
    private static final String SLING_RING_PING_AFTER_TIME_UNIT = "/API/DB/SLING_RING/PING_AFTER/@TIME_UNIT";

    private static final String SLING_RING_MAX_PINGER_THREADS = "/API/DB/SLING_RING/MAX_PINGER_THREADS/@VALUE";
    private static final String SLING_RING_MAX_TASKS_PER_PINGER_THREAD = "/API/DB/SLING_RING/MAX_TASKS_PER_PINGER_THREAD/@VALUE";


    private static final String XML_PATH_TO_TEMP_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@ROOT";
    private static final String XML_PATH_TO_TEMP_WORKFLOW_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@WORKFLOW";
    private static final String XML_PATH_TO_CREDIT_TOP_UP_ATT_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@CREDIT_TOP_UP";
    private static final String XML_PATH_TO_TEMP_REPORTS_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@REPORTS";
    private static final String XML_PATH_TO_TEMP_TEMPLATES_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@TEMPLATES";
    private static final String XML_PATH_TO_TEMP_ASSETS_FILE_PATH = "/API/PATHS/SYSTEM_FOLDERS/@ASSETS";
    private static final String XML_PATH_TO_PUBLIC_ASSETS_FOLDER_PATH = "/API/PATHS/PUBLIC_ASSETS/FOLDER";
    private static final String XML_PATH_TO_PUBLIC_ASSETS_SERVER = "/API/PATHS/PUBLIC_ASSETS/SERVER";
    private static final String XML_PATH_TO_SECURE_FOLDER_PATH = "/API/PATHS/SECURE_FOLDER";
    public static final String XML_PATH_TO_PUBLIC_ASSETS_EMAIL_SKY_LOGO_PATH = "/API/PATHS/PUBLIC_ASSETS/EMAIL_ASSETS/@SKY_LOGO";
    public static final String XML_PATH_TO_PUBLIC_ASSETS_EMAIL_MINT_LOGO_PATH = "/API/PATHS/PUBLIC_ASSETS/EMAIL_ASSETS/@MINTEL_LOGO";
    public static final String XML_PATH_TO_PUBLIC_ASSETS_EMAIL_PAID_BADGE_PATH = "/API/PATHS/PUBLIC_ASSETS/EMAIL_ASSETS/@PAID_BADGE";
    public static final String XML_PATH_TO_PUBLIC_ASSETS_EMAIL_UNPAID_BADGE_PATH = "/API/PATHS/PUBLIC_ASSETS/EMAIL_ASSETS/@UNPAID_BADGE";

    public static final String XML_PATH_TO_SMS_COST_PER_SMS = "/API/SERVICES/SMS/@COST_PER_SMS";
    public static final String XML_PATH_TO_SMS_LENGTH_SINGLE = "/API/SERVICES/SMS/LENGTH/@SINGLE_SMS";
    public static final String XML_PATH_TO_SMS_LENGTH_CONCATENATED = "/API/SERVICES/SMS/LENGTH/@CONCATENATED_SMS";
    public static final String XML_PATH_TO_SMS_LENGTH_MAX_CHARACTERS = "/API/SERVICES/SMS/LENGTH/@MAX_CHARACTERS";

    public static final String XML_PATH_TO_SURVEY_API_ENDPOINT = "/API/SURVEY_API/ENDPOINT";
    public static final String XML_PATH_TO_SURVEY_API_ORIGINATOR_ID = "/API/SURVEY_API/ORIGINATOR_ID";
    public static final String XML_PATH_TO_SURVEY_API_CHANNEL_CODE = "/API/SURVEY_API/CHANNEL_CODE";

    public static final String MRA_ENDPOINT_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/@ENDPOINT";
    public static final String MRA_CONNECTION_TIMEOUT_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/@CONNECTION_TIMEOUT";
    public static final String MRA_CONNECTION_TIMEOUT_TIMEUNIT_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/@CONNECTION_TIMEOUT_TIMEUNIT";
    public static final String MRA_WRAPPER_ID_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/PARAMETERS/@WRAPPER_ID";
    public static final String MRA_USERNAME_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/PARAMETERS/USERNAME";
    public static final String MRA_PASSWORD_TYPE_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/PARAMETERS/PASSWORD/@TYPE";
    public static final String MRA_PASSWORD_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/PARAMETERS/PASSWORD";
    public static final String MRA_ERROR_RECOVERY_MAX_RETIRES_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/ERROR_RECOVERY/RETRIES/@MAX_RETIRES";
    public static final String MRA_ERROR_RECOVERY_RETRIES_INTERVAL_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/ERROR_RECOVERY/RETRIES/@RETRIES_INTERVAL";
    public static final String MRA_ERROR_RECOVERY_RETRIES_INTERVAL_TIME_UNIT_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/ERROR_RECOVERY/RETRIES/@RETRIES_INTERVAL_TIME_UNIT";
    public static final String MRA_ERROR_RECOVERY_RETRY_STATUSES_XPATH = "/API/SERVICES/MEMBER_REGISTER_API/ERROR_RECOVERY/RETRIES/RETRY_STATUSES";

    private static final String XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_ADDRESS = "/API/EMAIL_ACCOUNTS/PORTAL_NOREPLY/ADDRESS";
    private static final String XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD = "/API/EMAIL_ACCOUNTS/PORTAL_NOREPLY/PASSOWORD";
    private static final String XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD_TYPE = "/API/EMAIL_ACCOUNTS/PORTAL_NOREPLY/PASSOWORD/@TYPE";
    private static final String XML_PATH_TO_EMAIL_SMTP_DEBUG = "/API/EMAIL_ACCOUNTS/SMTP/@DEBUG";
    private static final String XML_PATH_TO_EMAIL_SMTP_HOST = "/API/EMAIL_ACCOUNTS/SMTP/HOST";
    private static final String XML_PATH_TO_EMAIL_SMTP_ENCRYPTION = "/API/EMAIL_ACCOUNTS/SMTP/ENCRYPTION";
    private static final String XML_PATH_TO_EMAIL_SMTP_PORT = "/API/EMAIL_ACCOUNTS/SMTP/PORT";

    public static final String PK_DELIMITER = "::";

    public static Set<PosixFilePermission> groupAccessPosixPerms = new HashSet<>();
    public static Set<PosixFilePermission> ownerAccessPosixPerms = new HashSet<>();
    public static Set<PosixFilePermission> groupAndOwnerAccessPosixPerms = new HashSet<>();
    public static Set<PosixFilePermission> allAccessPosixPerms = new HashSet<>();
    public static Deque<String> filter = new ArrayDeque<>();
    public static Deque<String> fields = new ArrayDeque<>();
    public static Deque<String> strategy = new ArrayDeque<>();
    public static List<String> filterRelations = new ArrayList<>();
    private static int IO_THREAD_POOL;
    private static int WORKER_THREAD_POOL;
    private static boolean SHOW_SQL = false;
    private static String DB_HOST;
    private static int DB_PORT;
    private static String DB_NAME;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    private static int CAPTCHA_WIDTH;
    private static int CAPTCHA_HEIGHT;
    private static int CAPTCHA_LENGTH;
    private static boolean CAPTCHA_BORDER;
    private static boolean CAPTCHA_BACKGROUND;
    private static int CAPTCHA_NOISE_LEVEL;
    private static boolean CAPTCHA_ALPHANUMERIC;
    private static boolean AUDIO_CAPTCHA;
    private static int CAPTCHA_TTL;

    public static List<Object> fieldRelations;

    public static class _FieldRelations{
        private String label;
        private String description;
        private String relation;

        public _FieldRelations() {}

        _FieldRelations(String label,
                        String description,
                        String relation) {
            this.label = label;
            this.description = description;
            this.relation = relation;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        @Override
        public String toString() {
            return "_FieldRelations{" +
                    "label='" + label + '\'' +
                    ", description='" + description + '\'' +
                    ", relation='" + relation + '\'' +
                    '}';
        }
    }

    public static void populateApplicationCache(){

        groupAccessPosixPerms.add(PosixFilePermission.GROUP_WRITE);
        groupAccessPosixPerms.add(PosixFilePermission.GROUP_READ);
        groupAccessPosixPerms.add(PosixFilePermission.GROUP_EXECUTE);

        ownerAccessPosixPerms.add(PosixFilePermission.OWNER_WRITE);
        ownerAccessPosixPerms.add(PosixFilePermission.OWNER_READ);
        ownerAccessPosixPerms.add(PosixFilePermission.OWNER_EXECUTE);

        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.OWNER_WRITE);
        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.OWNER_READ);
        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.OWNER_EXECUTE);
        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.GROUP_WRITE);
        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.GROUP_READ);
        groupAndOwnerAccessPosixPerms.add(PosixFilePermission.GROUP_EXECUTE);

        allAccessPosixPerms.add(PosixFilePermission.OWNER_WRITE);
        allAccessPosixPerms.add(PosixFilePermission.OWNER_READ);
        allAccessPosixPerms.add(PosixFilePermission.OWNER_EXECUTE);
        allAccessPosixPerms.add(PosixFilePermission.GROUP_WRITE);
        allAccessPosixPerms.add(PosixFilePermission.GROUP_READ);
        allAccessPosixPerms.add(PosixFilePermission.GROUP_EXECUTE);
        allAccessPosixPerms.add(PosixFilePermission.OTHERS_WRITE);
        allAccessPosixPerms.add(PosixFilePermission.OTHERS_READ);
        allAccessPosixPerms.add(PosixFilePermission.OTHERS_EXECUTE);

        fields.add("*");
        filter.add("*");
        strategy.add("and");

        filterRelations.add("eq");
        filterRelations.add("!eq");
        filterRelations.add("lt");
        filterRelations.add("lte");
        filterRelations.add("gt");
        filterRelations.add("gte");
        filterRelations.add("contains");
        filterRelations.add("!contains");
        filterRelations.add("sw");
        filterRelations.add("!sw");
        filterRelations.add("ew");
        filterRelations.add("!ew");
        filterRelations.add("in");
        filterRelations.add("!in");
        filterRelations.add("btwn");
        filterRelations.add("!btwn");
        filterRelations.add("null");
        filterRelations.add("!null");

        fieldRelations = new ArrayList<>();

        fieldRelations.add(Constants.filterRelations);

        fieldRelations.add(new _FieldRelations(
                "Equal to",
                "Test whether provided value is exactly equal to value in the database. " +
                        "Works best with numerical figures or very precise alphanumeric queries",
                "eq"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not Equal to",
                "Negation for 'Equal To' (eq). NB. The rest of the relations work the same." +
                        " i.e. Not In (!in), Not Starts With (!sw)",
                "!eq"
        ));

        fieldRelations.add(new _FieldRelations(
                "Less than",
                "Test whether provided value is less than another value in the database. " +
                        "Works best with numerical figures. In case of alphanumerics, it performs character count",
                "lt"
        ));

        fieldRelations.add(new _FieldRelations(
                "Less than or equal to",
                "Test whether provided value is either less than or equal to value in the database. " +
                        "Works best with numerical figures. In case of alphanumerics, it performs character count",
                "lte"
        ));

        fieldRelations.add(new _FieldRelations(
                "Greater than",
                "Test whether provided value is greater than a value in the database. " +
                        "Works best with numerical figures. In case of alphanumerics, it performs character count",
                "gt"
        ));

        fieldRelations.add(new _FieldRelations(
                "Greater than or equal to",
                "Test whether provided value is greater than or equal to value in the database. " +
                        "Works best with numerical figures. In case of alphanumerics, it performs character count",
                "gte"
        ));

        fieldRelations.add(new _FieldRelations(
                "Between",
                "Between",
                "btwn"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not Between",
                "Not Between",
                "!btwn"
        ));

        fieldRelations.add(new _FieldRelations(
                "Is Null",
                "Is Null",
                "null"
        ));

        fieldRelations.add(new _FieldRelations(
                "Is Not Null",
                "Is Not Null",
                "!null"
        ));

        fieldRelations.add(new _FieldRelations(
                "Containing",
                "Test whether provided value is contained in another value in the database. " +
                        "Works best with numerical alphanumerics. It will perform a case sensitive search " +
                        "and locate the specified characters anywhere in the target field. " +
                        "For numerical cases, it will treat them as characters",
                "contains"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not Containing",
                "Test whether provided value is contained in another value in the database. " +
                        "Works best with numerical alphanumerics. It will perform a case sensitive search " +
                        "and locate the specified characters anywhere in the target field. " +
                        "For numerical cases, it will treat them as characters",
                "!contains"
        ));

        fieldRelations.add(new _FieldRelations(
                "Starting with",
                "Test whether provided value is at the beginning of a value in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "and will only retrieve a record if the provided value occurs " +
                        "exactly at the beginning of the target field",
                "sw"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not Starting with",
                "Test whether provided value is at the beginning of a value in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "and will only retrieve a record if the provided value occurs " +
                        "exactly at the beginning of the target field",
                "!sw"
        ));

        fieldRelations.add(new _FieldRelations(
                "Ending with",
                "Test whether provided value is at the end of a value in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "and will only retrieve a record if the provided value occurs " +
                        "exactly at the end of the target field",
                "ew"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not Ending with",
                "Test whether provided value is at the end of a value in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "and will only retrieve a record if the provided value occurs " +
                        "exactly at the end of the target field",
                "!ew"
        ));

        fieldRelations.add(new _FieldRelations(
                "In",
                "Test whether provided set of values is in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "of the whole word/value and will only retrieve a record if the provided value occurs. " +
                        "Sample value for this is 'Admin,User'. NB. The values are separated by ','",
                "in"
        ));

        fieldRelations.add(new _FieldRelations(
                "Not In",
                "Test whether provided set of values is in the database. " +
                        "Works best with characters. It will perform a case sensitive search " +
                        "of the whole word/value and will only retrieve a record if the provided value occurs. " +
                        "Sample value for this is 'Admin,User'. NB. The values are separated by ','",
                "!in"
        ));

        IO_THREAD_POOL = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_IO_THREAD_POOL));
        WORKER_THREAD_POOL = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_WORKER_THREAD_POOL));
        SHOW_SQL = XmlUtils.readXMLTag(XML_PATH_TO_DB_SHOW_SQL).equalsIgnoreCase("true");
        DB_HOST = XmlUtils.readXMLTag(XML_PATH_TO_HOST);
        DB_PORT = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_PORT));
        DB_NAME = XmlUtils.readXMLTag(XML_PATH_TO_MASTER_DATABASE_NAME);
        DB_USERNAME = XmlUtils.readXMLTag(XML_PATH_TO_DB_USERNAME);
        DB_PASSWORD = getDbPwd();

        CAPTCHA_WIDTH = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_WIDTH));
        CAPTCHA_HEIGHT = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_HEIGHT));
        CAPTCHA_LENGTH = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_LENGTH));
        CAPTCHA_BACKGROUND = XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_BACKGROUND).equalsIgnoreCase("true");
        CAPTCHA_BORDER = XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_BORDER).equalsIgnoreCase("true");
        CAPTCHA_NOISE_LEVEL = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_NOISE_LEVEL));
        CAPTCHA_ALPHANUMERIC = XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_ALPHANUMERIC).equalsIgnoreCase("true");
        AUDIO_CAPTCHA = XmlUtils.readXMLTag(XML_PATH_TO_AUTH_AUDIO_CAPTCHA).equalsIgnoreCase("true");
        CAPTCHA_TTL = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_CAPTCHA_TTL));

    }

    public static boolean dumpRequest(){
        return XmlUtils.readXMLTag(XML_PATH_TO_API_REQUEST_DUMP).equalsIgnoreCase("true");
    }

    public static boolean dbHealthCheckEnabled(){
        return XmlUtils.readXMLTag(XML_PATH_TO_DB_CONN_HEALTH_CHECK_ENABLED).equalsIgnoreCase("true");
    }

    public static long dbHealthCheckInterval(){
        try {
            return Long.parseLong(XmlUtils.readXMLTag(XML_PATH_TO_DB_CONN_HEALTH_CHECK_INTERVAL));
        } catch (Exception e){
            e.printStackTrace();
            return 60;
        }
    }

    public static String readStringFromConf(String path){
        return XmlUtils.readXMLTag(path);
    }

    public static int readIntFromConf(String path){
        return Integer.parseInt(XmlUtils.readXMLTag(path));
    }

    public static long readLongFromConf(String path){
        return Long.parseLong(XmlUtils.readXMLTag(path));
    }

    public static double readDoubleFromConf(String path){
        return Double.parseDouble(XmlUtils.readXMLTag(path));
    }

    public static int getAccessTokenLength(){
        int length = Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_ACCESS_TOKEN_LENGTH));

        if(length > 150){
            XmlUtils.updateXMLTag(XML_PATH_TO_AUTH_ACCESS_TOKEN_LENGTH, "150");
            return 150;
        } else return length;

//        return length > 150 ? 150 : length;
//        return Math.min(length, 150);
    }

    public static int getCaptchaWidth(){
        return CAPTCHA_WIDTH;
    }

    public static int getCaptchaHeight(){
        return CAPTCHA_HEIGHT;
    }

    public static int getCaptchaLength(){
        return CAPTCHA_LENGTH;
    }

    public static boolean getCaptchaBorder(){
        return CAPTCHA_BORDER;
    }

    public static boolean getCaptchaBackground(){
        return CAPTCHA_BACKGROUND;
    }

    public static int getCaptchaNoiseLevel(){
        return CAPTCHA_NOISE_LEVEL;
    }

    public static boolean getCaptchaAlphanumeric(){
        return CAPTCHA_ALPHANUMERIC;
    }

    public static boolean doWeDoCaptchaAudio(){
        return AUDIO_CAPTCHA;
    }

    public static int getCaptchaTtl(){
        return CAPTCHA_TTL;
    }

    public static String getPortalLoginLink(PortalTypes portal){
        String portalLoginLink = XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_HOST_PREFIX_PATH) +
                XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_HOST_NAME_PATH) +
                XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_PATHS_LOGIN_PATH);
        switch (portal) {
            case PARTNER: {
                portalLoginLink = portalLoginLink.replaceAll("/user/", "/partner/");
                break;
            }
            case ADMIN: {
                portalLoginLink = portalLoginLink.replaceAll("/user/", "/simba/");
                break;
            }
        }
        return portalLoginLink;
    }

    public static String getPortalWorkflowLink(PortalTypes portal){
        String portalWorkflowLink = XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_HOST_PREFIX_PATH) +
                XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_HOST_NAME_PATH) +
                XmlUtils.readXMLTag(XML_PATH_TO_PORTAL_PATHS_WORKFLOW_PATH);
        switch (portal) {
            case PARTNER: {
                portalWorkflowLink = portalWorkflowLink.replaceAll("/user/", "/partner/");
                break;
            }
            case ADMIN: {
                portalWorkflowLink = portalWorkflowLink.replaceAll("/user/", "/simba/");
                break;
            }
        }
        return portalWorkflowLink;
    }

    public static int getInfoWidgetPeriod(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_DASHBOARD_STATS_INFO_WIDGET_PERIOD_PATH));
    }

    public static String getInfoWidgetTimeUnit(){
        return XmlUtils.readXMLTag(XML_PATH_TO_DASHBOARD_STATS_INFO_WIDGET_TIME_UNIT_PATH);
    }

    public static int getAccessTokenTimeout(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_AUTH_ACCESS_TOKEN_TIMEOUT));
    }

    public static int getMsgLogsHistory(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_MSG_LOGS_HISTORY_PERIOD_PATH));
    }

    public static int getPesaLogsHistory(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_PESA_LOGS_HISTORY_PERIOD_PATH));
    }

    public static String getFileProcessingPref(){
        return XmlUtils.readXMLTag(XML_PATH_TO_FILE_PROCESSING_PREF);
    }
    public static int getDefaultPageSIze(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_PAGE_SIZE));
    }
    public static String getPublicAssetsServer(){
        return XmlUtils.readXMLTag(XML_PATH_TO_PUBLIC_ASSETS_SERVER);
    }
    public static String getAccessTokenTimeoutTimeUnit(){
        return XmlUtils.readXMLTag(XML_PATH_TO_AUTH_ACCESS_TOKEN_TIMEOUT_TIME_UNIT);
    }
    public static String getSecureFolderPath(){
        return XmlUtils.readXMLTag(XML_PATH_TO_SECURE_FOLDER_PATH);
    }
    public static String getPublicAssetsPath(){
        return XmlUtils.readXMLTag(XML_PATH_TO_PUBLIC_ASSETS_FOLDER_PATH);
    }
    public static String getEnableBasicAuth(){
        return XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_ENABLE_BASIC_AUTH);
    }
    public static String getApiContextHost(){
        return XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_HOST);
    }
    public static String getApiContextClientRootDir(){
        return XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_CLIENT_ROOT_DIR);
    }

    public static String getDefaultTimeZone(){
        return XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_TIME_ZONE);
    }


    public static String getUndertowEnableAccessLog(){
        return XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_ENABLE_ACCESS_LOG);
    }
    public static String getUndertowAccessLogDir(){
        return XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_ACCESS_LOG_DIR);
    }
    public static String getUndertowAccessLogPattern(){
        return XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_ACCESS_LOG_PATTERN);
    }
    public static String getUndertowEnableCompression(){
        return XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_ENABLE_COMPRESSION);
    }
    public static String getUndertowCompressionMinResponseSize(){
        return XmlUtils.readXMLTag(XML_PATH_TO_UNDERTOW_COMPRESSION_MIN_RESPONSE_SIZE);
    }


    public static DatabaseServer getDbServer() {
        return DatabaseServer.valueOf(XmlUtils.readXMLTag(XML_PATH_TO_SERVER));
    }

    public static DatabaseServer getDatabaseServer(){
        return DatabaseServer.parse(readStringFromConf(XML_PATH_TO_SERVER));
    }

    public static String getApiContextPath() {
        return XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_PATH);
    }
    public static Integer getApiContextPort() {
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_CONTEXT_PORT));
    }

    public static int getIoThreadPool() {
        return IO_THREAD_POOL;
    }

    public static int getWorkerThreadPool() {
        return WORKER_THREAD_POOL;
    }

    public static int getTokenExpiry(){
        return Integer.parseInt(XmlUtils.readXMLTag(XML_PATH_TO_TOKEN_EXPIRY));
    }

    public static String getDbHost() {
        return DB_HOST;
    }

    public static int getDbPort() {
        return DB_PORT;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static String getDbDriverClass() {
        return readStringFromConf(XML_PATH_TO_DRIVER_CLASS);
    }

    public static String readXmlConf(String xmlPath){
        return XmlUtils.readXMLTag(xmlPath);
    }

    public static boolean showSql() {
        return SHOW_SQL;
    }

    public static String getPathToTmpFolder() {
        return XmlUtils.readXMLTag(XML_PATH_TO_TEMP_FILE_PATH);
    }
    public static String getPathToTmpWorkflowFolder() {
        return getPathToTmpFolder()+XmlUtils.readXMLTag(XML_PATH_TO_TEMP_WORKFLOW_FILE_PATH);
    }
    public static String getPathToTmpReportsFolder() {
        return getPathToTmpFolder()+XmlUtils.readXMLTag(XML_PATH_TO_TEMP_REPORTS_FILE_PATH);
    }

    public static String getPasswordType(String path) {

        String passwordType = XmlUtils.readXMLTag(path);

        if(!passwordType.equals(SKY_DELIMITER)){
            return passwordType;
        }

        return SKY_DELIMITER;
    }

    public static String getDbUsername(){
        return DB_USERNAME;
    }

    public static String getDbPassword(){
        return DB_PASSWORD;
    }

    private static String getDbPwd(){
        if (getPasswordType(XML_PATH_TO_DB_PASSWORD_TYPE).equals(CLEARTEXT)){
            String dbPassword = XmlUtils.readXMLTag(XML_PATH_TO_DB_PASSWORD);

            String encryptedText = Encryption.encrypt(dbPassword);

            if (!(encryptedText.equals(SKY_DELIMITER) || dbPassword.equals(SKY_DELIMITER))){
                setDbPassword(encryptedText);
                setDbPasswordType(ENCRYPTED);
                return dbPassword;
            }

            return SKY_DELIMITER;
        }else{
            String decryptedText = Encryption.decrypt(
                    XmlUtils.readXMLTag(XML_PATH_TO_DB_PASSWORD)
            );

            if (!decryptedText.equals(SKY_DELIMITER)){
                return decryptedText;
            }
        }
        return SKY_DELIMITER;
    }

    public static String getMemberRegisterAPIPassword(){
        if (getPasswordType(MRA_PASSWORD_TYPE_XPATH).equals(CLEARTEXT)){
            String dbPassword = XmlUtils.readXMLTag(MRA_PASSWORD_XPATH);

            String encryptedText = Encryption.encrypt(dbPassword);

            if (!(encryptedText.equals(SKY_DELIMITER) || dbPassword.equals(SKY_DELIMITER))){
                XmlUtils.updateXMLTag(MRA_PASSWORD_XPATH, encryptedText);
                XmlUtils.updateXMLTag(MRA_PASSWORD_TYPE_XPATH, ENCRYPTED);
                return dbPassword;
            }

            return SKY_DELIMITER;
        }else{
            String decryptedText = Encryption.decrypt(
                    XmlUtils.readXMLTag(MRA_PASSWORD_XPATH)
            );

            if (!decryptedText.equals(SKY_DELIMITER)){
                return decryptedText;
            }
        }
        return SKY_DELIMITER;
    }

    public static boolean getMailerSmtpDebug() {
        return XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_SMTP_DEBUG).equalsIgnoreCase("true");
    }

    public static String getMailerSmtpHost() {
        return XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_SMTP_HOST);
    }

    public static String getMailerSmtpEncryption() {
        return XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_SMTP_ENCRYPTION);
    }

    public static String getMailerSmtpPort() {
        return XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_SMTP_PORT);
    }

    public static String getPortalNoReplyEmailAddress() {
        return XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_ADDRESS);
    }

    public static String getPortalNoReplyPasswordType() {

        String passwordType = XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD_TYPE);

        if(!passwordType.equals(SKY_DELIMITER)){
            return passwordType;
        }

        return SKY_DELIMITER;
    }

    public static String getPortalNoReplyPassword(){
        if (getPortalNoReplyPasswordType().equals(CLEARTEXT)){
            String password = XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD);

            String encryptedText = Encryption.encrypt(password);

            if (!(encryptedText.equals(SKY_DELIMITER) || password.equals(SKY_DELIMITER))){
                setPortalNoReplyPassword(encryptedText);
                setPortalNoReplyPasswordType(ENCRYPTED);
                return password;
            }

            return SKY_DELIMITER;
        }else{
            String decryptedText = Encryption.decrypt(
                    XmlUtils.readXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD)
            );

            if (!decryptedText.equals(SKY_DELIMITER)){
                return decryptedText;
            }
        }
        return SKY_DELIMITER;
    }

    private static void setPortalNoReplyPassword(String password)  {
        XmlUtils.updateXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD, password);
    }

    private static void setPortalNoReplyPasswordType(String passwordType)  {
        XmlUtils.updateXMLTag(XML_PATH_TO_EMAIL_PORTAL_NO_REPLY_PASSWORD_TYPE, passwordType);
    }


    public static String getDbConnMetadata() {
        return readStringFromConf(XML_PATH_TO_CONN_METADATA);
    }

    public static int getSlingRingInitialPoolSize() {
        return Integer.parseInt(readStringFromConf(SLING_RING_INITIAL_POOL_SIZE));
    }

    public static int getSlingRingMaxPoolSize() {
        return Integer.parseInt(readStringFromConf(SLING_RING_MAXIMUM_POOL_SIZE));
    }

    public static int getSlingRingExtraConnsSize() {
        return Integer.parseInt(readStringFromConf(SLING_RING_EXTRA_CONNS_SIZE));
    }

    public static long getSlingRingFindFreeConnAfter() {
        return Long.parseLong(readStringFromConf(SLING_RING_FIND_FREE_CONN_AFTER));
    }

    public static String getSlingRingFindFreeConnAfterTimeUnit() {
        return readStringFromConf(SLING_RING_FIND_FREE_CONN_AFTER_TIME_UNIT);
    }

    public static long getSlingRingDownSizeAfter() {
        return Long.parseLong(readStringFromConf(SLING_RING_DOWNSIZE_AFTER));
    }

    public static String getSlingRingDownSizeAfterTimeUnit() {
        return readStringFromConf(SLING_RING_DOWNSIZE_AFTER_TIME_UNIT);
    }

    public static long getSlingRingPingAfter() {
        return Long.parseLong(readStringFromConf(SLING_RING_PING_AFTER));
    }

    public static String getSlingRingPingAfterTimeUnit() {
        return readStringFromConf(SLING_RING_PING_AFTER_TIME_UNIT);
    }



    public static int getSlingRingMaxPingerThreads() {
        return Integer.parseInt(readStringFromConf(SLING_RING_MAX_PINGER_THREADS));
    }

    public static int getSlingRingMaxTasksPerPingerThread() {
        return Integer.parseInt(readStringFromConf(SLING_RING_MAX_TASKS_PER_PINGER_THREAD));
    }

    public static boolean doWeCompressReport(){
        return XmlUtils.readXMLTag(XML_PATH_TO_REPORT_GEN_ENABLE_COMPRESSION).equalsIgnoreCase("true");
    }


    public static boolean doWeInitializeDb(boolean refactor){
        String dbInitStatus = XmlUtils.readXMLTag(XML_PATH_TO_DB_INITIALIZE).toLowerCase().trim();

        if(dbInitStatus.equals("true")){
            if(refactor) XmlUtils.updateXMLTag(XML_PATH_TO_DB_INITIALIZE, "false");
            return true;
        }

        return false;
    }

    public static boolean allowUserMultiSessions(){
        return XmlUtils.readXMLTag(XML_PATH_TO_AUTH_MULTIPLE_SAME_USER_SESSIONS).equalsIgnoreCase("true");
    }

    public static boolean doWeInitializeDb(){
        return doWeInitializeDb(false);
    }

    private static void setDbPasswordType(String credentialsType)  {
        XmlUtils.updateXMLTag(XML_PATH_TO_DB_PASSWORD_TYPE, credentialsType);
    }

    public static boolean setDbUsername(String dbUsedname)  {
        return XmlUtils.updateXMLTag(XML_PATH_TO_DB_USERNAME, dbUsedname);
    }

    private static void setDbPassword(String dbPassword)  {
        XmlUtils.updateXMLTag(XML_PATH_TO_DB_PASSWORD, dbPassword);
    }

    public static final class Table {
        //Master Database
        public static final String ACCESS_TOKENS = "access_tokens";
        public static final String APP_USER_GROUPS = "app_user_groups";
        public static final String APP_USER_RIGHTS = "app_user_rights";
        public static final String APPLICATION_WORKFLOW_LOGS = "application_workflow_logs";
        public static final String APPLICATION_WORKFLOWS = "application_workflows";
        public static final String APPLICATIONS = "applications";
        public static final String DESIGNATIONS = "designations";
        public static final String GENDERS = "genders";
        public static final String REQUEST_LOGS = "request_logs";
        public static final String USER_ACCOUNTS = "user_accounts";
        public static final String USER_APP_USER_GROUPS = "user_app_user_groups";
        public static final String SYSTEM_SETTINGS = "system_settings";
    }

    public static final class CreditTopUpVar {
        public static final String NAME = "\\[NAME]";
        public static final String LOGO = "\\[LOGO]";
        public static final String BADGE = "\\[BADGE]";
        public static final String RECEIPT_DATE = "\\[RECEIPT_DATE]";
        public static final String RECEIPT_NO = "\\[RECEIPT_NO]";
        public static final String PURPOSE_OF_PAYMENT = "\\[PURPOSE_OF_PAYMENT]";
        public static final String ORGANIZATION_NAME = "\\[ORGANIZATION_NAME]";
        public static final String ORGANIZATION_POSTAL_ADDR = "\\[ORGANIZATION_POSTAL_ADDR]";
        public static final String ORGANIZATION_EMAIL_ADDR = "\\[ORGANIZATION_EMAIL_ADDR]";
        public static final String ATTENTION_NAME = "\\[ATTENTION_NAME]";
        public static final String ATTENTION_TEL_NO = "\\[ATTENTION_TEL_NO]";
        public static final String PAID_BY_IDENTIFIER = "\\[PAID_BY_IDENTIFIER]";
        public static final String PAID_BY_NAME = "\\[PAID_BY_NAME]";
        public static final String PAYMENT_DATE = "\\[PAYMENT_DATE]";
        public static final String PAYMENT_MODE = "\\[PAYMENT_MODE]";
        public static final String PAYMENT_REFERENCE = "\\[PAYMENT_REFERENCE]";
        public static final String SERVICE = "\\[SERVICE]";
        public static final String PAYMENT_AMOUNT = "\\[PAYMENT_AMOUNT]";
        public static final String TOTAL_PAYMENT_AMOUNT = "\\[TOTAL_PAYMENT_AMOUNT]";

        public static final String PAYMENT_ACCOUNT = "\\[PAYMENT_ACCOUNT]";
    }

    public static final class OTPMessageVars {
        public static final String NAME = "\\[NAME]";
        public static final String OTP = "\\[OTP]";
        public static final String DATE_CREATED = "\\[DATE_CREATED]";
        public static final String EXPIRY_DATE = "\\[EXPIRY_DATE]";
    }

    public static final class MsgFileColumn {
        public static final String MOBILE_NUMBER = "Mobile Number";
        public static final String MESSAGE = "Message";
        public static final int MOBILE_NUMBER_INT = 0;
        public static final int MESSAGE_INT = 1;
    }

    public static final class BlackWhiteListColumn {
        public static final String IDENTIFIER_TYPE = "Identifier Type";
        public static final String IDENTIFIER = "Identifier";
        public static final String FULL_NAME = "Full Name";
        public static final String IDENTITY_TYPE = "Identity Type";
        public static final String IDENTITY_NUMBER = "Identity Number";
        public static final String STATUS = "Status";
        public static final String STATUS_DESCRIPTION = "Status Description";
        public static final int IDENTIFIER_TYPE_INT = 0;
        public static final int IDENTIFIER_INT = 1;
        public static final int FULL_NAME_INT = 2;
        public static final int IDENTITY_TYPE_INT = 3;
        public static final int IDENTITY_NUMBER_INT = 4;
        public static final int STATUS_INT = 5;
        public static final int STATUS_DESCRIPTION_INT = 6;
    }

    public static final class DndColumn {
        public static final String IDENTIFIER_TYPE = "Identifier Type";
        public static final String IDENTIFIER = "Identifier";
        public static final String FULL_NAME = "Full Name";
        public static final String NOTIFICATION_CATEGORY = "Notification Category";
        public static final String STATUS = "Status";
        public static final String STATUS_DESCRIPTION = "Status Description";
        public static final String REASON = "Reason";
        public static final int IDENTIFIER_TYPE_INT = 0;
        public static final int IDENTIFIER_INT = 1;
        public static final int FULL_NAME_INT = 2;
        public static final int NOTIFICATION_CATEGORY_INT = 3;
        public static final int STATUS_INT = 4;
        public static final int STATUS_DESCRIPTION_INT = 5;
        public static final int REASON_INT = 6;
    }

    public static final class IdentifierType {
        public static final String MSISDN = "MSISDN";
        public static final String IMSI = "IMSI";
        public static final String MEID = "MEID";
        public static final String ESN = "ESN";
        public static final String IMEI = "IMEI";
        public static final String GSFID = "GSFID";
        public static final String APP_ID = "APP_ID";
        public static final String ACCOUNT_NO = "ACCOUNT_NO";
        public static final String CARD_NO = "CARD_NO";
    }

    public static final class IdentityType {
        public static final String NATIONAL_ID = "NATIONAL_ID";
        public static final String PASSPORT = "PASSPORT";
        public static final String DRIVING_LICENSE = "DRIVING_LICENSE";
    }

    public static final class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String SUSPENDED = "SUSPENDED";
    }

    /**
     * dangerDark = "#F44336";
     * warningDark = "#F57C00";
     * successDark = "#4CAF50";
     * primaryDark = "#0078D4";
     * other="#A0A0A0"
     */
}
