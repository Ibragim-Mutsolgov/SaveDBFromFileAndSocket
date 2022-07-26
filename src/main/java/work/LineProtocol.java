package work;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;

public class LineProtocol {

    private final Statement st;

    public LineProtocol(Statement st){
        this.st = st;
    }

    public void listen(){
        new Thread(()->{
            try (ServerSocket server = new ServerSocket(8007);)
            {
                while(true) {
                    try (Socket socket = server.accept();
                         InputStream stream = socket.getInputStream();
                         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                         OutputStream outputStream = socket.getOutputStream();
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));)
                    {
                        String string = bufferedReader.readLine();
                        if(string.split(" ").length == 12){
                            String state = string.split(" ")[7].replace(":", "");

                            String dt = "'" + Year.now().toString() +" " + string.split(" ")[0] +" " + string.split(" ")[1] + " " + string.split(" ")[2]+"'"; //dt

                            int intState = 1;

                            if (state.equals("JSERVICES_NAT_PORT_BLOCK_ALLOC") | state.equals("JSERVICES_NAT_PORT_BLOCK_RELEASE")) {
                                if (state.equals("JSERVICES_NAT_PORT_BLOCK_RELEASE")) intState = 2;
                                int finalIntState = intState;
                                new Thread(() -> {
                                    String sql = "INSERT INTO syslog (dt, privateip, publicip, publicstartport, publicendport, state) \n" +
                                            "VALUES (" +
                                            dt + "," +
                                            "'"+string.split(" ")[8]+"',"+  //privateip
                                            "'"+string.split(" ")[10].split(":")[0]+"',"  + //publicip
                                            string.split(" ")[10].split(":")[1].split("-")[0]+","  + //publicstartport
                                            string.split(" ")[10].split(":")[1].split("-")[1]+","+ finalIntState +")";
                                    try {
                                        st.executeQuery(sql);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                        }
                        else{
                            writer.write("Некорректно введено.");
                            writer.flush();
                            writer.close();
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }
}
