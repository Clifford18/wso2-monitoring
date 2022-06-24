package ke.co.skyworld.domain.enums;

/**
 * sls-api (ke.co.scedar.domain.enums)
 * Created by: elon
 * On: 06 Jul, 2018 7/6/18 9:01 PM
 **/
public enum  Permissions {

    INIT_CLIENT("Initialize Client"),

    //User Accounts
    VIEW_USERS("View Users"),
    MOD_USERS("Modify Users"),

    //Course Registration
    REGISTER_USERS("Register Users"),
    VIEW_BIOMETRICS_DATA("View Biometric Data"),
    MOD_BIOMETRICS("Modify Biometric Data"),

    //Attendance
    VIEW_ATTENDANCE("View Attendance"),
    RECORD_ATTENDANCE("Record Attendance"),

    //Courses
    MOD_COURSES("Modify Courses"),

    //Course Lecturers
    MOD_COURSE_LEC("Modify Course Lecturers"),

    //Course Units
    MOD_COURSE_UNITS("Modify Course Units"),

    //Finger Types
    MOD_FINGER_TYPES("Modify Finger Types"),

    //Lectures
    MOD_LECTURES("Modify Lectures"),

    //LectureMetaData
    MOD_LECTURE_METADATA("Modify Lecture Metadata"),

    //Lecturer Attendance Status
    MOD_LEC_ATT_STATUS("Modify Lecturer Attendance Status"),

    //System Roles
    MOD_SYS_ROLES("Modify System Roles"),

    //User Roles
    MOD_USER_ROLES("Modify User Roles"),

    //User Types
    MOD_USER_TYPES("Modify User Types"),

    //Attachment Types
    MOD_ATT("Modify Attachment Types");

    private String value;

    Permissions(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
