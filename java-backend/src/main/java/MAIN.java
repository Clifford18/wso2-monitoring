import java.sql.*;

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

            while (myResult.next()) {
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

    public static void insertUser() {
        Connection myConn = null;

        PreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "INSERT INTO user_accounts (user_status, account_access_mode, username, first_name, last_name, mobile_number,\n" +
                    "                           email_address, user_pwd_status, user_pwd, allowed_access_sources_status,\n" +
                    "                           allowed_access_sources_match_type, restricted_access_sources_status,\n" +
                    "                           restricted_access_sources_match_type, gender, designation)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            myPreparedStatement = myConn.prepareStatement(mySql);
            myPreparedStatement.setString(1, "ACTIVE");
            myPreparedStatement.setString(2, "API");
            myPreparedStatement.setString(3, "username5");
            myPreparedStatement.setString(4, "first_name5");
            myPreparedStatement.setString(5, "last_name5");
            myPreparedStatement.setString(6, "254725111222");
            myPreparedStatement.setString(7, "f5l5@gmail.com");
            myPreparedStatement.setString(8, "ACTIVE");
            myPreparedStatement.setString(9, "user_pwd5");
            myPreparedStatement.setString(10, "ACTIVE");
            myPreparedStatement.setString(11, "STRING");
            myPreparedStatement.setString(12, "ACTIVE");
            myPreparedStatement.setString(13, "STRING");
            myPreparedStatement.setString(14, "FeMale");
            myPreparedStatement.setString(15, "IT_Manager");


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

    public static void updateUser() {
        Connection myConn = null;

        PreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "UPDATE user_accounts SET gender=?, designation=? WHERE user_id=?";

            myPreparedStatement = myConn.prepareStatement(mySql);

            myPreparedStatement.setString(1, "Male");
            myPreparedStatement.setString(2, "C_E_O");
            myPreparedStatement.setInt(3, 5);


            myPreparedStatement.executeUpdate();

            myPreparedStatement.close();
            myConn.close();

            System.out.println("User Updated");

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

    public static void deleteUser() {
        Connection myConn = null;

        PreparedStatement myPreparedStatement = null;

        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wso2-monitoring-database", "root", "Pa55w0rd");

            String mySql = "DELETE FROM user_accounts WHERE user_id=?";

            myPreparedStatement = myConn.prepareStatement(mySql);

            myPreparedStatement.setInt(1, 5); //value of id is 5


            myPreparedStatement.executeUpdate();

            myPreparedStatement.close();
            myConn.close();

            System.out.println("User Deleted");

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
        //insertUser();
        //updateUser();
        deleteUser();
    }
}
