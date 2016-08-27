package filesystem.duplicate;
import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author InvisiCoder
 */
public class FilesystemDuplicate {
    static int newFile;
    
    public static void main(String[] args) {
        System.out.println("\n=================================================================================================================================");
        System.out.println("Filesystem Duplicate Scanner v1.0.2");
        System.out.println("=================================================================================================================================\n");
        
        try {
            System.out.print("Enter existing/new database URL --> mysql://");
            DBConnect.dbURL = InputOutput.line();
            System.out.print("Enter the database admin username --> ");
            DBConnect.user = InputOutput.line();
            System.out.print("Enter the database admin password --> ");
            DBConnect.pass = InputOutput.line();
            DBConnect dbc = new DBConnect();
            Connection con = dbc.makeConnection();
            
            if (con == null) {System.err.println("Connection error");}
            
            SQLRun table = new SQLRun(con);
            table.tableDelete();
            table.tableCreate();
            
            System.out.print("\nEnter the directory path --> ");
            String sDirectory = InputOutput.line();
            File fDirectory = new File(sDirectory);
            SQLRun nc = new SQLRun();
            if (fDirectory.exists() == true) {
                System.out.println("\nProcessing...");
                nc.loop(fDirectory);
            } else {
                System.err.println("Directory does not exist : \"" + sDirectory + "\"");
            }
            
            System.out.println("More Processing...");
            newFile = 0;
            
            nc.setDuplicates();
            nc.Compare(sDirectory);
            System.out.println("\n" + (SQLRun.finalCount-1) + " duplicates have been scanned and stored in a text file located at:\n" + sDirectory + "/Filesystem_Duplicates.txt");
            
        } catch (Exception e) {
            System.out.println("Error occurred while executing the program: \n");
            Logger.getLogger(FilesystemDuplicate.class.getName()).log(Level.SEVERE, null, e);
        }
        
        finally{
            System.out.println("exiting db:"+DBConnect.dbName);
            exit();
        }
    }
    public static void exit(){
        try {
            SQLRun run = new SQLRun();
            run.tableDelete();
            run.schemaDelete();
            System.out.print("\nDatabase has been deleted.");
            DBConnect.connn.close();
            System.out.println("=================================================================================================================================\n");
            System.exit(0);
        } catch (SQLException ex) {
            System.out.println("Error occurred while executing the program: \n");
            Logger.getLogger(FilesystemDuplicate.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("=================================================================================================================================\n");
            System.exit(0);
        }
    }
}