package ke.co.skyworld.repository;

import ke.co.skyworld.domain.beans.FieldMetadata;
import ke.co.skyworld.domain.enums.DataType;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.annotations.ProvidedPK;
import ke.co.skyworld.utils.annotations.Table;
import ke.co.skyworld.utils.annotations.Transient;
import ke.co.skyworld.utils.logging.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ke.co.skyworld.utils.Constants.*;

public class Q {

    public static boolean isAutoincrement(Class anonymous){
        return !anonymous.isAnnotationPresent(ProvidedPK.class);
    }

    public static String getTableName(Class anonymous){
        return ((Table) anonymous.getAnnotation(Table.class)).name();
    }

    public static String getPKField(String table){
        String pkField = "id";
        HashSet pkFields = Repository.getPrimaryKeyColumns(table).getData();
        for (Object pkf : pkFields){
            pkField = pkf.toString();
            break;
        }
        return pkField;
    }

    public static String[] getColumns(Class anonymous){
        Field[] fields = anonymous.getDeclaredFields();
        String[] columns = new String[fields.length];
        for (int i = 0; i < fields.length ; i++) {
            columns[i] = fields[i].getName();
        }
        return columns;
    }

    public static String getParentDatabase(String tableName){

        switch (tableName){
            case "access_tokens":
            case "app_user_groups":
            case "app_user_rights":
            case "application_workflow_logs":
            case "application_workflows":
            case "applications":
            case "designations":
            case "genders":
            case "request_logs":
            case "user_accounts":
            case "user_app_user_groups":{
                return Constants.readXmlConf(XML_PATH_TO_MASTER_DATABASE_NAME);
            }
            default: {
                return Constants.readXmlConf(XML_PATH_TO_MASTER_DATABASE_NAME);
            }
        }
    }

    public static boolean isDomain(String domain){
        switch (domain){
            case "access_tokens":
            case "app_user_groups":
            case "app_user_rights":
            case "application_workflow_logs":
            case "application_workflows":
            case "applications":
            case "designations":
            case "genders":
            case "request_logs":
            case "user_accounts":
            case "user_app_user_groups":
                return true;

            default:
                return false;
        }
    }

    public static String generateSql(String tableName, String fields, String filterClause){
        return "SELECT "+fields+" FROM "+Q.getParentDatabase(tableName)+"."+tableName+" WHERE "+filterClause;
    }

    @Deprecated
    public static List<String> getFields(String tableName){

        List<String> fields = new ArrayList<>();

        try {
            List<Class> classes = getClasses("ke.co.skyworld.domain.db.master");
            classes.addAll(getClasses("ke.co.skyworld.domain.db.msg"));
            classes.addAll(getClasses("ke.co.skyworld.domain.db.pesa"));
            classes.addAll(getClasses("ke.co.skyworld.domain.db.survey"));
            classes.addAll(getClasses("ke.co.skyworld.domain.db.ussd"));
            for (Class clazz : classes){
                if(Q.getTableName(clazz).equals(tableName)){
                    Field[] fieldz = clazz.getDeclaredFields();
                    for (Field field : fieldz) {
                        if (!field.isAnnotationPresent(Transient.class)) {
                            fields.add(field.getName());
                        }
                    }
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.error(Q.class, "getFields", e.getMessage());
        }
        return fields;
    }

    public static List<String> getFieldsV2(String tableName){
        List<String> fields = new ArrayList<>();

        try {
            fields = Repository.getColumns(tableName).getData();
        } catch (Exception e){
            e.printStackTrace();
            Log.error(Q.class, "getFieldsV2", e.getMessage());
        }

        return fields;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static List<String> getFilterByFields(Class clazz){
        List<String> viableFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){

            Class<?> fType = field.getType();
            if(fType.isAssignableFrom(Integer.TYPE) ||
                    fType.isAssignableFrom(Integer.class) ||
                    fType.isAssignableFrom(Long.TYPE) ||
                    fType.isAssignableFrom(Long.class) ||
                    fType.isAssignableFrom(Double.TYPE) ||
                    fType.isAssignableFrom(Double.class) ||
                    fType.isAssignableFrom(Float.TYPE) ||
                    fType.isAssignableFrom(Float.class) ||
                    fType.isAssignableFrom(Short.TYPE) ||
                    fType.isAssignableFrom(Short.class) ||
                    fType.isAssignableFrom(String.class) ||
                    fType.isAssignableFrom(Timestamp.class) ||
                    fType.isAssignableFrom(Byte.class) ||
                    fType.isAssignableFrom(Byte.TYPE)){

                String fname = field.getName();
                if(!(fname.equals("user_pwd") ||
                        fname.equals("reset_password"))){
                    viableFields.add(field.getName());
                }
            }
        }
        return viableFields;
    }

    public static List<String> getAllFields(Class clazz){
        List<String> allFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            allFields.add(field.getName());
        }
        return allFields;
    }

    public static List<FieldMetadata> getFieldMetadata(Connection conn, String database, String table) {

        List<FieldMetadata> fieldMetadata = new ArrayList<>();
        String _fieldName, _type, _null, _key, _default, _extra;

        //Connection conn = ConnectionManager.getUnManagedConnection();
        Statement statement = null;
        ResultSet rs = null;

        try {
            statement = Objects.requireNonNull(conn).createStatement();
            rs = statement.executeQuery("SHOW COLUMNS FROM `" + database + "`.`" + table + "`;");

            while (rs.next()) {
                _fieldName = rs.getString("Field");
                _type = rs.getString("Type");
                _null = rs.getString("Null");
                _key = rs.getString("Key");
                _default = rs.getString("Default");
                _extra = rs.getString("Extra");

                FieldMetadata _fieldMetadata = new FieldMetadata();
                _fieldMetadata.setName(_fieldName);

                DataType dataType = DataType.TEXT;

                if (_type.contains("(")) {

                    //Extracting type
                    int iof = _type.indexOf("(");

                    switch (_type.substring(0, iof).toLowerCase()) {
                        case "bigint":
                        case "bigint unsigned":
                            dataType = DataType.BIGINT;
                            break;

                        case "int":
                            dataType = DataType.INT;
                            break;

                        case "double":
                            dataType = DataType.DOUBLE;
                            break;

                        case "longtext":
                            dataType = DataType.LONG_TEXT;
                            break;

                        case "enum":
                            dataType = DataType.ENUM;
                            break;

                        case "datetime":
                            dataType = DataType.DATETIME;
                            break;
                    }

                    _fieldMetadata.setDataType(dataType);

                    //Extract size
                    String size;

                    Pattern pattern = Pattern.compile("\\(.*?\\)");
                    Matcher matcher = pattern.matcher(_type);

                    if (matcher.find()) {
                        size = matcher.group(0)
                                .replaceFirst("\\(", "").replaceFirst("\\)", "");
                        if (dataType != DataType.ENUM) {
                            _fieldMetadata.setSize(size);
                        } else {
                            _fieldMetadata.setEnumValues(size);
                        }
                    }

                    //Check if unsigned
                    if (dataType == DataType.INT || dataType == DataType.BIGINT ||
                            dataType == DataType.DOUBLE) {
                        iof = _type.indexOf("(");

                        try {
                            if (_type.substring(iof + 2).equalsIgnoreCase("signed")) {
                                _fieldMetadata.setSigned(true);
                            }
                        } catch (StringIndexOutOfBoundsException ignore) {
                        }
                    }
                } else {
                    switch (_type) {
                        case "bigint":
                        case "bigint unsigned":
                            dataType = DataType.BIGINT;
                            break;

                        case "int":
                            dataType = DataType.INT;
                            break;

                        case "double":
                            dataType = DataType.DOUBLE;
                            break;

                        case "longtext":
                            dataType = DataType.LONG_TEXT;
                            break;

                        case "enum":
                            dataType = DataType.ENUM;
                            break;

                        case "datetime":
                            dataType = DataType.DATETIME;
                            break;
                    }
                    _fieldMetadata.setDataType(dataType);
                }


                /*//Set nullability
                if (_null.equalsIgnoreCase("NO")) {
                    _fieldMetadata.setRequired(true);
                }

                //Check if primary key
                if (_key.equalsIgnoreCase("PRI")) {
                    _fieldMetadata.setPrimaryKey(true);
                    _fieldMetadata.setImmutable(true);
                }

                //Check for uniqueness
                if (_key.equalsIgnoreCase("UNI")) {
                    _fieldMetadata.setUnique(true);
                }

                //Check if indexed
                if (_key.equalsIgnoreCase("MUL")) {
                    _fieldMetadata.setIndexed(true);
                }

                //Check default value
                _fieldMetadata.setDefaultValue((_default == null) ? "" : _default);

                //Check auto_increment
                if (_extra.equalsIgnoreCase("auto_increment")) {
                    _fieldMetadata.setAutoIncrement(true);
                }*/

                /*Statement _statement = null;
                ResultSet _rs = null;
                try {
                    //Get index and uindex names
                    if (_fieldMetadata.isIndexed() || _fieldMetadata.isUnique()) {
                        _statement = conn.createStatement();
                        _rs = _statement.executeQuery(
                                "SHOW INDEX FROM `" + table + "` WHERE Column_name = '" + _fieldName + "'");
                        if (_rs.next()) {
                            _fieldMetadata.setIndexName(_rs.getString("Key_name"));
                        }
                    }
                    if (_rs != null) _rs.close();
                    if (_statement != null) _statement.close();
                    _rs = null; _statement = null;

                } catch (Exception e){
                    e.printStackTrace();
                    Log.error(Q.class, "getFieldMetadata",
                            "Error getting table indices ("+e.getMessage()+")");
                } finally {
                    if (_rs != null) _rs.close();
                    if (_statement != null) _statement.close();
                    _rs = null; _statement = null;
                }

                //Get foreign key information
                try {
                    _statement = conn.createStatement();
                    _rs = _statement.executeQuery("SELECT " +
                            "CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                            "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                            "WHERE TABLE_SCHEMA = '" + database + "' " +
                            "AND TABLE_NAME = '" + table + "' AND COLUMN_NAME = '" + _fieldName + "'");

                    if (_rs.next()) {
                        if(_rs.getString("REFERENCED_TABLE_NAME") != null){
                            _fieldMetadata.setForeignKeyName(_rs.getString("CONSTRAINT_NAME"));
                            _fieldMetadata.setForeignKey(_rs.getString("REFERENCED_TABLE_NAME") + "."
                                    + _rs.getString("REFERENCED_COLUMN_NAME"));
                        }
                    }
                    _rs.close();
                    _statement.close();
                    _rs = null; _statement = null;
                } catch (Exception e){
                    e.printStackTrace();
                    Log.error(Q.class, "getFieldMetadata",
                            "Error getting table constraints ("+e.getMessage()+")");
                } finally {
                    if (_rs != null) _rs.close();
                    if (_statement != null) _statement.close();
                    _rs = null; _statement = null;
                }*/

                fieldMetadata.add(_fieldMetadata);
                Log.debug(Q.class, "getFieldMetadata", _fieldMetadata);
            }

            rs.close();
            statement.close();
            //conn.close();
            rs = null; statement = null; //conn = null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.error(Q.class, "getFieldMetadata",
                    "Error introspecting table ("+e.getMessage()+")");
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                //if(conn != null) conn.close();
                rs = null; statement = null; //conn = null;
            } catch (SQLException e){
                e.printStackTrace();
                Log.error(Q.class, "getFieldMetadata",
                        "Error collecting jdbc garbage ("+e.getMessage()+")");
            }
        }
        return fieldMetadata;
    }

    public static String getModelInfoDataType(DataType dataType){
        switch (dataType){
            case BIGINT:
            case INT:
                return DT_INTEGER;
            case DOUBLE:
                return DT_DOUBLE;
            case DATETIME:
                return DT_DATE;
            case LONG_TEXT:
                return DT_TEXT;
            case TEXT:
            case ENUM:
            default:
                return DT_STRING;
        }
    }

}
