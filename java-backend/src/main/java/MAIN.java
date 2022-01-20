import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MAIN {
    public static void readAllLogs() {
        Connection myConn = null;
        ;
        Statement myStatement = null;
        ;
        ResultSet myResult = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            myStatement = myConn.createStatement();

            myResult = myStatement.executeQuery("select * from request_logs");

            List<RequestLog> requestLogs = new ArrayList<>();

            while (myResult.next()) {

                RequestLog requestLog = new RequestLog();

                int request_id = myResult.getInt("request_id");
                String request_reference = myResult.getString("request_reference");
                String request_method = myResult.getString("request_method");
                String request_resource = myResult.getString("request_resource");
                String request_parameters = myResult.getString("request_parameters");
                String request_headers = myResult.getString("request_headers");
                String request_body = myResult.getString("request_body");
                String request_origin_ip = myResult.getString("request_origin_ip");
                String response_headers = myResult.getString("response_headers");
                String response_body = myResult.getString("response_body");
                String error_code = myResult.getString("error_code");
                String error_message = myResult.getString("error_message");
                String error_stacktrace = myResult.getString("error_stacktrace");
                String date_created = myResult.getString("date_created");
                String date_modified = myResult.getString("date_modified");

                requestLog.setRequest_id(request_id);
                requestLog.setRequest_reference(request_reference);
                requestLog.setRequest_method(request_method);
                requestLog.setRequest_resource(request_resource);
                requestLog.setRequest_parameters(request_parameters);
                requestLog.setRequest_headers(request_headers);
                requestLog.setRequest_body(request_body);
                requestLog.setRequest_origin_ip(request_origin_ip);
                requestLog.setResponse_headers(response_headers);
                requestLog.setResponse_body(response_body);
                requestLog.setError_code(error_code);
                requestLog.setError_message(error_message);
                requestLog.setError_stacktrace(error_stacktrace);
                requestLog.setDate_created(date_created);
                requestLog.setDate_modified(date_modified);

                requestLogs.add(requestLog);

                //print results
                System.out.println("ID ::" + request_id);
                //System.out.println(myResult.getString("request_id"));
            }


            myResult.close();
            myStatement.close();
            myConn.close();

            myConn = null;
            myStatement = null;
            myResult = null;

        } catch (Exception e) {
            e.printStackTrace();
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myStatement != null) try {
                myStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myStatement = null;
            myResult = null;
        } finally {
            if (myResult != null) try {
                myResult.close();
            } catch (SQLException ignore) {
            }
            if (myStatement != null) try {
                myStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myStatement = null;
            myResult = null;
        }
    }

    public static void insertUser(UserAccounts user_accounts) {
        Connection myConn = null;

        NamedPreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "INSERT INTO user_accounts (user_status, account_access_mode, username, first_name, last_name, mobile_number,\n" +
                    "                           email_address, user_pwd_status, user_pwd, allowed_access_sources_status,\n" +
                    "                           allowed_access_sources_match_type, restricted_access_sources_status,\n" +
                    "                           restricted_access_sources_match_type, gender, designation)\n" +
                    "VALUES (:user_status, :account_access_mode, :username, :first_name, :last_name, :mobile_number, " +
                    ":email_address, :user_pwd_status, :user_pwd, :allowed_access_sources_status, :allowed_access_sources_match_type," +
                    " :restricted_access_sources_status, :restricted_access_sources_match_type, :gender, :designation)";
            myPreparedStatement = NamedPreparedStatement.prepareStatement(myConn, mySql);
            myPreparedStatement.setString("user_status", user_accounts.getUser_status());
            myPreparedStatement.setString("account_access_mode", user_accounts.getAccount_access_mode());
            myPreparedStatement.setString("username", user_accounts.getUsername());
            myPreparedStatement.setString("first_name", user_accounts.getFirst_name());
            myPreparedStatement.setString("last_name", user_accounts.getLast_name());
            myPreparedStatement.setInt("mobile_number", user_accounts.getMobile_number());
            myPreparedStatement.setString("email_address", user_accounts.getEmail_address());
            myPreparedStatement.setString("user_pwd_status", user_accounts.getUser_pwd_status());
            myPreparedStatement.setString("user_pwd", user_accounts.getUser_pwd());
            myPreparedStatement.setString("allowed_access_sources_status", user_accounts.getAllowed_access_sources_status());
            myPreparedStatement.setString("allowed_access_sources_match_type", user_accounts.getAllowed_access_sources_match_type());
            myPreparedStatement.setString("restricted_access_sources_status", user_accounts.getRestricted_access_sources_status());
            myPreparedStatement.setString("restricted_access_sources_match_type", user_accounts.getRestricted_access_sources_match_type());
            myPreparedStatement.setString("gender", user_accounts.getGender());
            myPreparedStatement.setString("designation", user_accounts.getDesignation());


            myPreparedStatement.executeUpdate();

            myPreparedStatement.close();
            myConn.close();

            System.out.println("User Added");

            myConn = null;
            myPreparedStatement = null;

        } catch (Exception e) {
            e.printStackTrace();
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        } finally {
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        }
    }

    public static void updateUser(UserAccounts user_accounts) {
        Connection myConn = null;

        PreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "UPDATE user_accounts SET gender=?, designation=? WHERE user_id=?";

            myPreparedStatement = myConn.prepareStatement(mySql);

            myPreparedStatement.setString(1, user_accounts.getGender());
            myPreparedStatement.setString(2, user_accounts.getDesignation());
            myPreparedStatement.setInt(3, user_accounts.getUser_id());


            myPreparedStatement.executeUpdate();

            myPreparedStatement.close();
            myConn.close();

            System.out.println("User Updated Successfully");

            myConn = null;
            myPreparedStatement = null;

        } catch (Exception e) {
            e.printStackTrace();
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        } finally {
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        }
    }

    public static void deleteUser(UserAccounts user_accounts) {
        Connection myConn = null;

        PreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "DELETE FROM user_accounts WHERE user_id=?";

            myPreparedStatement = myConn.prepareStatement(mySql);

            myPreparedStatement.setInt(1, user_accounts.getUser_id()); //value of id is 5


            myPreparedStatement.executeUpdate();

            myPreparedStatement.close();
            myConn.close();

            System.out.println("User Deleted Successfully");

            myConn = null;
            myPreparedStatement = null;

        } catch (Exception e) {
            e.printStackTrace();
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        } finally {
            if (myPreparedStatement != null) try {
                myPreparedStatement.close();
            } catch (SQLException ignore) {
            }
            if (myConn != null) try {
                myConn.close();
            } catch (SQLException ignore) {
            }

            myConn = null;
            myPreparedStatement = null;
        }
    }


    public static void main(String[] args) {
        //readAllLogs();
        //insertUser();"
//        UserAccounts new_user = new UserAccounts();
//
//        new_user.setUser_status("ACTIVE");
//        new_user.setAccount_access_mode("API");
//        new_user.setUsername("username5");
//        new_user.setFirst_name("first_name5");
//        new_user.setLast_name("last_name5");
//        new_user.setMobile_number(254725111);
//        new_user.setEmail_address("f5l5@gmail.com");
//        new_user.setUser_pwd_status("ACTIVE");
//        new_user.setUser_pwd("user_pwd5");
//        new_user.setAllowed_access_sources_status("ACTIVE");
//        new_user.setAllowed_access_sources_match_type("STRING");
//        new_user.setRestricted_access_sources_status("ACTIVE");
//        new_user.setRestricted_access_sources_match_type("STRING");
//        new_user.setGender("Male");
//        new_user.setDesignation("IT_Manager");
//
//        insertUser(new_user);

//        insertUser("ACTIVE", "API", "username5", "first_name5", "last_name5", "254725111222", "f5l5@gmail.com", "ACTIVE",
//                "user_pwd5", "ACTIVE",
//                "STRING", "ACTIVE", "STRING", "Male", "IT_Manager");

        //updateUser();
//        updateUser(6,"FeMale","C_E_O");
//
//        UserAccounts update_user =new UserAccounts();
//        update_user.setUser_id(7);
//        update_user.setGender("FeMale");
//        update_user.setDesignation("C_E_O");
//        updateUser(update_user);

        //deleteUser();
//        UserAccounts delete_user = new UserAccounts();
//        delete_user.setUser_id(7);
//        deleteUser(delete_user);

        //deleteUser(6);
    }
}
