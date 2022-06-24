package ke.co.skyworld.utils.file_utils;

import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sls-api (ke.co.scedar.utils.file_utils)
 * Created by: elon
 * On: 28 Aug, 2018 8/28/18 11:22 PM
 **/
public class FileOps {

    public static boolean exists(String path){
        return new File(path).exists();
    }

    public static boolean exists(File file){
        return file.exists();
    }

    public static String readFile(String path) {
        try {
            if (Files.isReadable(Paths.get(path))) {
                byte[] encoded = Files.readAllBytes(Paths.get(path));
                return new String(encoded, StandardCharsets.ISO_8859_1);
            } else {
                Log.error(FileOps.class, "readFile", "Error. File '"+path+"' is not readable or does not exist");
                return null;
            }
        } catch (Exception e) {
            Log.error(FileOps.class, "readFile", "Error reading File '"+path+"' ("+e.getMessage()+")");
            return null;
        }
    }

    public static String readFile(URL url) {
        try {
            File file = new File(url.getFile());

            if(file.exists()){
                byte[] encoded = Files.readAllBytes(file.toPath());
                return new String(encoded, StandardCharsets.ISO_8859_1);
            } else {
                Log.error(FileOps.class, "readFile", "Error. File '"+file.getPath()+"' is not readable or does not exist");
                return null;
            }
        } catch (Exception e) {
            Log.error(FileOps.class, "readFile", "Error reading File on URL '"+url+"' ("+e.getMessage()+")");
            return null;
        }
    }

    public static boolean deleteFile(String path){
        return deleteFile(Paths.get(path));
    }

    public static boolean deleteFile(Path path){
        try{
            Files.delete(path);
            return true;
        } catch (NoSuchFileException e){
            Log.info(FileOps.class, "deleteFile", "File not found.");
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean moveFile(String source, String destination, Set<PosixFilePermission> perms){
        return moveFile(Paths.get(source), Paths.get(destination), perms);
    }

    public static boolean moveFile(Path source, String destination, Set<PosixFilePermission> perms){
        return moveFile(source, Paths.get(destination), perms);
    }

    public static boolean moveFile(String source, Path destination, Set<PosixFilePermission> perms){
        return moveFile(Paths.get(source), destination, perms);
    }

    public static boolean moveFile(Path source, Path destination, Set<PosixFilePermission> perms){
        try {
            Files.move(source, destination);
            Files.setPosixFilePermissions(destination, perms);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyFile(String source, String destination){
        return copyFile(Paths.get(source), Paths.get(destination));
    }

    public static boolean copyFile(Path source, String destination){
        return copyFile(source, Paths.get(destination));
    }

    public static boolean copyFile(String source, Path destination){
        return copyFile(Paths.get(source), destination);
    }

    public static boolean copyFile(Path source, Path destination){
        try {
            Files.copy(source, destination);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFileExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }
        return "";
    }

    public static String extractFileName(String filePath){
        return extractFileName(filePath, FileSystems.getDefault().getSeparator());
    }

    public static String extractFileName(String filePath, String filePathSeparator){
        if(filePathSeparator.equals("\\")) filePathSeparator = "\\\\";
        String regex = "([^"+filePathSeparator+"]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filePath);
        String fileName = "";

        if (matcher.find()) {
            fileName = matcher.group(0);
        }
        return fileName;
    }

    public static long getFileSize(String file){
        return new File(file).length();
    }

    public static String getPrettyFileSize(String file){
        return prettifyFileSize(new File(file).length());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static HashMap<String, Object> getFileDetails(String path, String fileName, String context){
        HashMap<String, Object> fileMetaData = new HashMap<>();

        String extension;
        String newFileName;

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
            fileMetaData.put("extension", extension);
            newFileName = context+"_"+ DateTime.getCurrentUnixTimestamp()+"."+extension;

            if (extension.equals("csv") ||
                    extension.equals("txt") ||
                    extension.equals("xlsx")){

                if(moveFile(path+fileName,
                        path+newFileName,
                        Constants.groupAndOwnerAccessPosixPerms)){
                    fileMetaData.put("error", false);
                    fileMetaData.put("message", "'"+fileName+"' uploaded successfully to dir '"+path+
                            "' with new name '"+newFileName+"'");
                    fileMetaData.put("newFilePath", path+newFileName);
                    fileMetaData.put("fileName", newFileName);
                    fileMetaData.put(
                            "fileSize", String.valueOf(new File(path+newFileName).length())
                    );
                }else{
                    new File(path+fileName).delete();
                    fileMetaData.put("error", true);
                    fileMetaData.put("message", "Error! Permission denied");
                }


            }else{
                new File(path+fileName).delete();
                fileMetaData.put("error", true);
                fileMetaData.put("message", "File not accepted due to " +
                        "invalid extension '"+extension+"'. (Accepted extensions -> 'txt, csv, xls, xlsx')");
            }
        }else{
            new File(path+fileName).delete();
            fileMetaData.put("error", true);
            fileMetaData.put("message", "File not accepted due to " +
                    "invalid extension");
        }
        return fileMetaData;
    }

    public static String prettifyFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,###.##")
                .format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
