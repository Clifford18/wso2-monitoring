import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToJava {
    public static void main(String[] args) {
        String myJsonStr = "{\n" +
                "   \"user_id\":5,\n" +
                "   \"user_status\":\"ACTIVE\",\n" +
                "   \"user_status_description\":null,\n" +
                "   \"user_status_date\":null,\n" +
                "   \"account_access_mode\":\"API\",\n" +
                "   \"username\":\"username5\",\n" +
                "   \"first_name\":\"first_name5\",\n" +
                "   \"last_name\":\"last_name5\",\n" +
                "   \"mobile_number\":123,\n" +
                "   \"email_address\":\"f5l5@gmail.com\",\n" +
                "   \"user_pwd_status\":\"ACTIVE\",\n" +
                "   \"user_pwd\":\"user_pwd5\",\n" +
                "   \"user_pwd_status_date\":null,\n" +
                "   \"login_attempts\":0,\n" +
                "   \"max_login_attempts\":5,\n" +
                "   \"allowed_access_sources_status\":\"ACTIVE\",\n" +
                "   \"allowed_access_sources_match_type\":\"STRING\",\n" +
                "   \"max_allowed_access_sources\":5,\n" +
                "   \"allowed_access_sources\":null,\n" +
                "   \"restricted_access_sources_status\":\"ACTIVE\",\n" +
                "   \"restricted_access_sources_match_type\":\"STRING\",\n" +
                "   \"max_restricted_access_sources\":5,\n" +
                "   \"restricted_access_sources\":null,\n" +
                "   \"tracking_id\":null,\n" +
                "   \"tracking_source_ip\":null,\n" +
                "   \"tracking_url\":null,\n" +
                "   \"tracking_time\":null,\n" +
                "   \"tracking_referrer\":null,\n" +
                "   \"gender\":\"Male\",\n" +
                "   \"designation\":\"IT_Manager\",\n" +
                "   \"date_created\":null,\n" +
                "   \"date_modified\":null\n" +
                "}";
        ObjectMapper myObjectMapper = new ObjectMapper();
        try {
           UserAccounts user_accounts = myObjectMapper.readValue( myJsonStr, UserAccounts.class);
            System.out.println(user_accounts.getUser_id()+" "+user_accounts.getGender());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
