package filesystem.duplicate;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author InvisiCoder
 */
public class DBConnect {
    static String dbURL;
    static String user;
    static String pass;
    private static String dbAdd;
    static String dbName;
    Connection conn;
    static Connection connn;//Used to close the connection

    public  Connection makeConnection(){
        boolean r = false;
        for (String retval: dbURL.split("/")){
            if (r == false){
                dbAdd = retval;
                r = true;
            }else{
                dbName = retval;
            }
        }   
        try {
            //System.out.println("create connection");
            conn = DriverManager.getConnection("jdbc:mysql://" + DBConnect.dbURL, DBConnect.user, DBConnect.pass);
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Access denied for user")) {
                System.err.println("Incorrect Credentials");
                System.out.println("=================================================================================================================================\n");
                System.exit(0);
            } else if (e.getMessage().startsWith("Communications link failure")) {
                System.err.println("Connection failed");
                System.out.println("=================================================================================================================================\n");
                System.exit(0);
            } else if (e.getMessage().startsWith("Unknown database")) {
                System.err.println("Database " + dbName + " does not exist");
                createDatabase();
            } else {
                System.err.println("Unknown Error");
                System.out.println("=================================================================================================================================\n");
                System.exit(0);
            }
        }
        DBConnect.connn = conn;
        return conn;
    }
    
    private void createDatabase(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + dbAdd, user, pass);
            Statement s = conn.createStatement();
            System.out.println("Creating new database "+dbName);
            s.executeUpdate("CREATE DATABASE " + dbName);
            s.close();
            makeConnection();
        } catch (Exception ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}