import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JavaToJson {
    public static void main(String[] args) {
        UserAccounts new_employee = new UserAccounts(5,
                "ACTIVE",
                null,
                null,
                "API",
                "username5",
                "first_name5",
                "last_name5",
                123,
                "f5l5@gmail.com",
                "ACTIVE",
                "user_pwd5",
                null,
                0,
                5,
                "ACTIVE",
                "STRING",
                5,
                null,
                "ACTIVE",
                "STRING",
                5,
                null,
                null,
                null,
                null,
                null,
                null,
                "Male",
                "IT_Manager",
                null,
                null);
        ObjectMapper myObjectMapper = new ObjectMapper();

        try {
            String myJsonStr = myObjectMapper.writeValueAsString(new_employee);
            System.out.println(myJsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
