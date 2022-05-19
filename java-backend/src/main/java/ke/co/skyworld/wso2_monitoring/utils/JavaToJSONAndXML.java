package ke.co.skyworld.wso2_monitoring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ke.co.skyworld.wso2_monitoring.utils.objects.UserAccounts;

public class JavaToJSONAndXML {
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

    public static String convertToJson(Object obj){

        ObjectMapper myObjectMapper = new ObjectMapper();

        try {
            return myObjectMapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "{}";
    }

      public static String convertToXMLStr(Object obj){

        try {
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            XmlMapper mapper = new XmlMapper(xmlModule);
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            //mapper.setDateFormat(df);  // this works for outbounds but has no effect on inbounds
            //mapper.getDeserializationConfig().with(df);

            return mapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "<body/>";
    }


}
