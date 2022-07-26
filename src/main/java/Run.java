import work.LineFile;
import work.LineProtocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Run {

    public static void main(String[] args) {
        String url = "****";
        String user = "****";
        String password = "****";
        try{
            Connection con = DriverManager.getConnection(url, user, password);
            LineFile lineFile = new LineFile(con.createStatement());
            lineFile.setParseFile();
            LineProtocol lineProtocol = new LineProtocol(con.createStatement());
            lineProtocol.listen();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
