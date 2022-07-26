package work;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class LineFile {

    private final Statement st;

    public LineFile(Statement st) {
        this.st = st;
    }

    public void setParseFile() {
        //Чтение из файла
        new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String result = scanner.nextLine();
                if (result.split("/").length > 0) {
                    //Тогда это путь
                    try (FileInputStream file = new FileInputStream(result)) {
                        ArrayList<String> lines = new ArrayList<>(Files.readAllLines(Paths.get(result)));
                        for (String s : lines) {
                            set(s, result);
                        }
                        System.out.println("Данные из файла занесены в базу данных.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Некорректно введено.");
                    }
                }else{
                    System.out.println("Некорректно введено.");
                }
            }
        }).start();
    }

    public void set(String string, String result) {
        try{
            String state = string.split(" ")[7].replace(":", "");
            if(!state.equals("JSERVICES_NAT_OUTOF_PORTS_APP")) {
                String dt = "'" + result.split("/")[result.split("/").length - 1].split("-")[1].substring(0, 4) + " " + string.split(" ")[0] + " " + string.split(" ")[1] + " " + string.split(" ")[2] + "'"; //dt
                String sql = "";
                String privateip = "'" + string.split(" ")[8] + "'";   //privateip
                String publicip = "'" + string.split(" ")[10].split(":")[0] + "'";
                String publicstartport = string.split(" ")[10].split(":")[1].split("-")[0];
                String publicendport = string.split(" ")[10].split(":")[1].split("-")[1];
                if (state.equals("JSERVICES_NAT_PORT_BLOCK_ALLOC")) {
                    sql = MessageFormat.format("INSERT INTO syslog (startdt, privateip, publicip, publicstartport, publicendport) VALUES ({0}, {1}, {2}, {3}, {4})", dt, privateip, publicip, publicstartport, publicendport);
                } else if (state.equals("JSERVICES_NAT_PORT_BLOCK_RELEASE")) {
                    sql = MessageFormat.format("UPDATE syslog SET stopdt = {0} WHERE privateip = {1} AND publicip = {2} AND publicstartport = {3} AND publicendport = {4} AND stopdt is null", dt, privateip, publicip, publicstartport, publicendport);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
