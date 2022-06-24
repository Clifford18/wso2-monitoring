package ke.co.skyworld.utils;

import ke.co.skyworld.utils.memory.JvmManager;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sls-api (ke.co.scedar.utils)
 * Created by: elon
 * On: 04 Jul, 2018 7/4/18 10:22 PM
 **/
public class Misc {

    public static List<Integer> convertToIntArray(List<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                System.err.println("Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    public static List<Integer> convertToIntArray(String stringArray, String delimiter){
        return convertToIntArray(Arrays.asList(stringArray.split(delimiter)));
    }

    public static List<Long> convertToLongArray(List<String> stringArray) {
        ArrayList<Long> result = new ArrayList<Long>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Long.parseLong(stringValue));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                System.err.println("Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    public static List<Long> convertToLongArray(String stringArray, String delimiter){
        return convertToLongArray(Arrays.asList(stringArray.split(delimiter)));
    }

    public static List<String> convertFromCamelCaseToHeaderCase(List<String> words){
        List<String> converted = new ArrayList<>();

        for (String camelValue : words){
            camelToHeader(converted, camelValue);
        }

        return converted;
    }

    public static String camelToSnake(String camelCase){
        /*Matcher m = Pattern.compile("(?<=[a-z])[A-Z]").matcher(camelCase);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "_"+m.group().toLowerCase());
        }
        return m.appendTail(sb).toString().toLowerCase();*/
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public static List<String> convertFromSnakeCaseToHeaderCase(List<String> words){
        List<String> converted = new ArrayList<>();

        for (String snakeValue : words){
            String camelValue = snakeToCamel(snakeValue);
            camelToHeader(converted, camelValue);
        }

        return converted;
    }

    public static String convertFromSnakeCaseToHeaderCase(String word){
        String camelValue = snakeToCamel(word);
        return camelToHeader(camelValue);
    }

    private static void camelToHeader(List<String> converted, String camelValue) {
        StringBuilder word = new StringBuilder();

        for (String w : camelValue.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            word.append(w).append(" ");
        }
        String convertedText = word.toString().trim();
        convertedText = Character.toUpperCase(convertedText.charAt(0)) + convertedText.substring(1);

        converted.add(convertedText);
    }

    private static String camelToHeader(String camelValue) {
        StringBuilder word = new StringBuilder();

        for (String w : camelValue.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            word.append(w).append(" ");
        }
        String convertedText = word.toString().trim();
        convertedText = Character.toUpperCase(convertedText.charAt(0)) + convertedText.substring(1);

        return convertedText;
    }

    public static String snakeToCamel(String snakeCase){
        String[] words = snakeCase.split("_");
        StringBuilder camelCase = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++)
            camelCase.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));
        return camelCase.toString();
    }

    public static String abbreviateString(String string,int noOfChacters) {
        if (string.trim().length() > 0)
        {
            string = string.substring(0, Math.min(string.length(), noOfChacters));
            int length = string.length();
            if(length==20) {
                string = string.substring(0, length - 3) + "...";
            }
        }

        return string;
    }

    public static String sanitizePhoneNumber(String thePhoneNumber){
        thePhoneNumber = thePhoneNumber.replaceAll("\\s","");
        try {
            if(thePhoneNumber.startsWith("+")){
                thePhoneNumber = thePhoneNumber.replaceFirst("^\\+", "");
            }

            if(thePhoneNumber.matches("^2547\\d{8}$")){
                return thePhoneNumber;
            }

            if(thePhoneNumber.matches("^07\\d{8}$")) {
                return thePhoneNumber.replaceFirst("^0", "254");
            }

            if(thePhoneNumber.matches("^7\\d{8}$")){
                return "254"+thePhoneNumber;
            }

            if(thePhoneNumber.matches("^25407\\d{8}$")){
                return thePhoneNumber.replaceFirst("^25407", "2547");
            }

            if(thePhoneNumber.matches("^2541\\d{8}$")){
                return thePhoneNumber;
            }

            if(thePhoneNumber.matches("^01\\d{8}$")) {
                return thePhoneNumber.replaceFirst("^0", "254");
            }

            if(thePhoneNumber.matches("^1\\d{8}$")){
                return "254"+thePhoneNumber;
            }

            if(thePhoneNumber.matches("^25401\\d{8}$")){
                return thePhoneNumber.replaceFirst("^25401", "2541");
            }

            return Constants.VALIDATION_FAILED;
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String sanitizeNationalIDNumber(String idNumber){
        idNumber = idNumber.trim().replaceAll("\\s", "");
        return idNumber;

        /*if (idNumber.matches("[0-9]+") && idNumber.length() > 4) {
            return idNumber;
        } else {
            return Constants.VALIDATION_FAILED;
        }*/
    }

    public static String sanitizeNationalIDNumberOLD(String idNumber){
        idNumber = idNumber.trim().replaceAll("\\s", "");

        if (idNumber.matches("[0-9]+") && idNumber.length() > 4) {
            return idNumber;
        } else {
            return Constants.VALIDATION_FAILED;
        }
    }

    public static String sanitizeIdentity(String identityType, String identity){

        if(identityType.equals(Constants.IdentityType.NATIONAL_ID)){
            return sanitizeNationalIDNumber(identity);
        } else {
            return identity.trim().replaceAll("\\s","");
        }
    }

    public static String sanitizeIdentifier(String identifierType, String identifier){
        if(identifierType.equals(Constants.IdentifierType.MSISDN)){
            return sanitizePhoneNumber(identifier);
        }else {
            return identifier.trim().replaceAll("\\s","");
        }
    }

    public static XSSFCellStyle getHeaderCellStyle(XSSFWorkbook workbook) throws Exception{
        XSSFCellStyle headerStyle = workbook.createCellStyle();

        String rgbS = "006CBE";
        byte[] rgbB = Hex.decodeHex(rgbS);
        XSSFColor color = new XSSFColor(rgbB, null);

        headerStyle.setFillForegroundColor(color);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontName("Arial");

        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    public static XSSFCellStyle getRowCellStyle(XSSFWorkbook workbook){
        XSSFCellStyle rowStyle = workbook.createCellStyle();
        rowStyle.setBorderTop(BorderStyle.THIN);
        rowStyle.setBorderBottom(BorderStyle.THIN);
        rowStyle.setBorderLeft(BorderStyle.THIN);
        rowStyle.setBorderRight(BorderStyle.THIN);

        XSSFFont rowFont = workbook.createFont();
        rowFont.setBold(false);
        rowFont.setFontHeightInPoints((short)10);
        rowFont.setColor(IndexedColors.BLACK.getIndex());
        rowFont.setFontName("Arial");

        rowStyle.setFont(rowFont);
        return rowStyle;
    }

    public static XSSFCellStyle getHeaderCellStyle(SXSSFWorkbook workbook) throws Exception{
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();

        String rgbS = "006CBE";
        byte[] rgbB = Hex.decodeHex(rgbS);
        XSSFColor color = new XSSFColor(rgbB, null);

        headerStyle.setFillForegroundColor(color);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontName("Arial");

        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    public static XSSFCellStyle getRowCellStyle(SXSSFWorkbook workbook){
        XSSFCellStyle rowStyle = (XSSFCellStyle) workbook.createCellStyle();
        rowStyle.setBorderTop(BorderStyle.THIN);
        rowStyle.setBorderBottom(BorderStyle.THIN);
        rowStyle.setBorderLeft(BorderStyle.THIN);
        rowStyle.setBorderRight(BorderStyle.THIN);

        XSSFFont rowFont = (XSSFFont) workbook.createFont();
        rowFont.setBold(false);
        rowFont.setFontHeightInPoints((short)10);
        rowFont.setColor(IndexedColors.BLACK.getIndex());
        rowFont.setFontName("Arial");

        rowStyle.setFont(rowFont);
        return rowStyle;
    }

    public static String removeNonUTF8Characters(String string){
        string = string.replaceAll("[^\\x00-\\x7F]", " ");
        //string = string.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");
        //string = string.replaceAll("\\p{C}", " ");
        //return string.trim().replaceAll("\\s{2,}", " ");
        return string.trim();
    }

    public static boolean validateString(String string, String regex){
        try {
            return Pattern.compile(regex).matcher(string).find();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // convert input stream to string
    public static String readInputStream(InputStream is) throws  Exception {

        StringBuilder inputStreamContents = new StringBuilder();
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        String line;

        try {
            streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new BufferedReader(streamReader);
            while ((line = reader.readLine()) != null) { inputStreamContents.append(line);}
            streamReader.close(); reader.close(); is.close();
            JvmManager.gc(streamReader, reader, is);
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (streamReader != null) { streamReader.close(); }
            if (reader != null) { reader.close(); }
            is.close();
            JvmManager.gc(streamReader, reader, is);
        }
        return inputStreamContents.toString();
    }

    public static String generateRandomIntegerString(int theLength){
        return generateRandomIntegerString(theLength, true);
    }

    public static String generateRandomIntegerString(int theLength, boolean withLeadingZero) {
        StringBuilder strRandom = new StringBuilder();

        try {
            char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            SecureRandom random = new SecureRandom();

            for(int i = 0; i < theLength; ++i) {
                char c = chars[random.nextInt(chars.length)];
                if( i == 0 && c =='0' ) {
                    if(withLeadingZero){
                        c = chars[random.nextInt(chars.length)];
                        while (c != '0'){
                            c = chars[random.nextInt(chars.length)];
                        }
                    }
                }
                strRandom.append(c);
            }
            return strRandom.toString();
        } catch (Exception var5) {
            System.out.println(var5.getMessage());
            return strRandom.toString();
        }
    }

}
