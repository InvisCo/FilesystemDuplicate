package filesystem.duplicate;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author InvisiCoder
 */
public class InputOutput {
    static Scanner key = new Scanner(System.in);
    public static String str(){
        String a = key.next();
        return a;
    }
    
    public static  String line(){
        String a = key.nextLine();
        return a;
    }
    
    public static void printToFile(String dir, String x, String y) throws FileNotFoundException, UnsupportedEncodingException{
        FilesystemDuplicate.newFile++;
        try(FileWriter fw = new FileWriter(dir+"/Filesystem_Duplicates.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            if (FilesystemDuplicate.newFile == 1){out.println("\n\nNEW RUN\n");}
            out.println("\n/================================================================================================================================================================================");
            out.println("|"+x);
            out.println("| Is duplicate of");
            out.println("|"+y);
            out.println("\\================================================================================================================================================================================");
        } catch (IOException e) {
            System.out.println("Error occurred while executing the program: \n");
            Logger.getLogger(FilesystemDuplicate.class.getName()).log(Level.SEVERE, null, e);
            FilesystemDuplicate.exit();
        }
    }
}