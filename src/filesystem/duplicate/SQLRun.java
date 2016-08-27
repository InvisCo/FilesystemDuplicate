package filesystem.duplicate;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.google.common.io.Files;

/**
 *
 * @author InvisiCoder
 */
public class SQLRun {
    
    SQLRun(Connection con) {
        SQLRun.con = con;
    }
    
    SQLRun() {}
    
    static Connection con = null;
    static int i = 0;
    
    public void loop(File directory) throws SQLException, NullPointerException {
        SQLRun.i++;
        for (File element : directory.listFiles()) {
            if (element.isDirectory()) {
                loop(element.getAbsoluteFile());
            } else {
                long t1 = element.lastModified();
                java.util.Date t2 = new java.util.Date(t1);
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddhhmmss");
                SQLRun nc = new SQLRun();
                nc.insert(((element.getParent()).replace('\\', '/')).replaceAll("'", "''"), Files.getNameWithoutExtension((element.getName()).replaceAll("'", "''")), Files.getFileExtension(element.getName()), (int) element.length(), (timestamp.format(t2)));
            }
        }
        SQLRun.i--;
    }
    
    public void insert(String parentPath, String fileName, String fileType, int fileSize, String fileTimestamp) throws SQLException {
        Statement stmt = SQLRun.con.createStatement();
        stmt.executeUpdate("INSERT INTO fsdb(ParentPath, Name, Extension, Size, LastModified) VALUES ('"+(parentPath)+"', '"+(fileName)+"', '"+(fileType)+"', '"+(fileSize)+"', '"+(fileTimestamp)+"')");
    }
    
    public void tableCreate() throws SQLException {
        try (Statement stmt = SQLRun.con.createStatement()) {
            stmt.execute("CREATE TABLE fsdb (id INT NOT NULL AUTO_INCREMENT, ParentPath TEXT NOT NULL, Name TEXT NOT NULL, Extension TINYTEXT, Size LONG NOT NULL, LastModified TIMESTAMP NOT NULL, Duplicate INT, PRIMARY KEY (id) );");
            stmt.close();
        }
    }
    
    public void tableDelete() throws SQLException{
        Statement stmt = SQLRun.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT count(*) FROM information_schema.tables WHERE (TABLE_SCHEMA = '" + DBConnect.dbName + "') AND (TABLE_NAME = 'fsdb')");
        rs.last();
        if (rs.getRow() < 1) {
            stmt.execute("DROP TABLE fsdb;");
        }
        stmt.close();
    }
    
    public void schemaDelete() throws SQLException{
        Statement stmt = SQLRun.con.createStatement();
        stmt.execute("DROP DATABASE "+ DBConnect.dbName+";");
        stmt.close();
    }
    
    static int dupCount = 1;
    
    public void setDuplicates() throws SQLException {
        Statement stmt = SQLRun.con.createStatement();
        ResultSet rs;
        int[][] duplicate = new int[10000][2];
        int j = 0;
        
        rs = stmt.executeQuery("SELECT * FROM fsdb ORDER BY Size");
        while (rs.next()) {
            if (rs.isFirst() == false) {
                int sizeN = rs.getInt("Size");
                String extN = rs.getString("Extension");
                int idN = rs.getInt("id");
                rs.previous();
                int sizeP = rs.getInt("Size");
                String extP = rs.getString("Extension");
                int idP = rs.getInt("id");
                rs.next();
            
                if (sizeN == sizeP) {
                    if (extN.equals(extP)) {
                        int dupP = 0;
                        if (dupP == 0) {
                            duplicate[j][0] = idP;
                            duplicate[j][1] = SQLRun.dupCount;
                            duplicate[j+1][0] = idN;
                            duplicate[j+1][1] = SQLRun.dupCount;
                            dupCount ++;
                            j = j + 2;
                        } else {
                            duplicate[j][0] = idN;
                            duplicate[j][1] = dupP;
                        }
                    }
                }
            }
        }
        rs.close();
        stmt.close();
        Statement stmts;
        for (int c = 0; c <= j; c++) {
            stmts = SQLRun.con.createStatement();
            stmts.executeUpdate("UPDATE fsdb SET Duplicate="+(duplicate[c][1])+" WHERE id="+(duplicate[c][0]));
            stmts.close();
        }
    }
    
    static int finalCount = 1;
    public void Compare(String dir) throws IOException, SQLException {
        Statement stmt;
        ResultSet rs;
        for (int c = (SQLRun.dupCount - 1); c > 0 ; c--) {
            stmt = SQLRun.con.createStatement();
            rs = stmt.executeQuery("SELECT ParentPath, Name, Extension FROM fsdb WHERE Duplicate="+(c));
            rs.last();
            for (int n = rs.getRow(); n > 0; n--) {
                rs.first();
                for (int l = (n-1); l > 0; l--) {
                    rs.next();
                }
                File cntrl = new File(rs.getString("ParentPath")+"/"+rs.getString("Name")+"."+rs.getString("Extension"));
                while (rs.previous()) {
                    File test = new File(rs.getString("ParentPath")+"/"+rs.getString("Name")+"."+rs.getString("Extension"));
                    boolean eq = Files.equal(cntrl,test);
                    if (eq == true) {
                        InputOutput.printToFile(dir, cntrl.getPath(), test.getPath());
                        SQLRun.finalCount++;
                    }
                }
            }
        }
    }
}