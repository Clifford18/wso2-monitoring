package ke.co.skyworld.utils.data_formatting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.memory.JvmManager;
import ke.co.skyworld.utils.security.HashUtils;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * flexicore (ke.co.skyworld.utilities.xml)
 * Created by: elon
 * On: 28 Jul, 2019 28/07/19 20:29
 **/
public class Converter {

    public static String toJson(Object obj){
        return serialize(obj, Constants.applicationJson);
    }

    public static String toXml(Object obj){
        return serialize(obj, Constants.applicationXml);
    }

    public static String serialize(Object obj, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            String objStr = objectMapper.writeValueAsString(obj);
            JvmManager.gc(obj, objectMapper);
            return objStr;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected Object[] fromJson(String data, Class clazz){

        ObjectMapper objectMapper = getObjectMapper(Constants.applicationJson);

        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return new Object[] {1, obj};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[] {-1, Constants.MARSHALL_ERROR,e.getMessage()};
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getObject(String data, Class clazz, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getObject(Object obj, Class clazz){
        String json = toJson(obj);
        return getObject(json, clazz, Constants.applicationJson);
    }

    public static Object toHashMap(String objStr, TypeReference T){
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = new HashMap<>();
        try {
            map = objectMapper.readValue(objStr, T);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JvmManager.gc(objectMapper, objStr);
        return map;
    }

    public static ObjectMapper getObjectMapper(String contentType){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(contentType.equals(Constants.applicationXml)){
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            XmlMapper mapper = new XmlMapper(xmlModule);
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);  // this works for outbounds but has no effect on inbounds
            mapper.getDeserializationConfig().with(df); // Gave this a shot but still does not sniff strings for a format that we declare should be treated as java.util.Date
            return mapper;
        }else{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);  // this works for outbounds but has no effect on inbounds
            mapper.getDeserializationConfig().with(df); // Gave this a shot but still does not sniff strings for a format that we declare should be treated as java.util.Date
            return mapper;
        }
    }

    public static String imgToBase64String(RenderedImage img, String formatName) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String base64Img;

        try {
            if(ImageIO.write(img, formatName, os)){
                base64Img = HashUtils.base64Encode(os.toByteArray());
            } else {
                throw new Exception("Error Generating Captcha: Unable to convert Buffered Image to Base64 String");
            }
            os.close();
            JvmManager.gc(os);
            return base64Img;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

}
