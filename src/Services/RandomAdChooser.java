package Services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RandomAdChooser implements Runnable {

    final static int PORT = 18600;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String randomAdver="";

    public RandomAdChooser() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error Connecting to Database "+e.toString());
        }
    }
    
    
    public void randomAd() {
        try {
            resultSet=statement.executeQuery("select * from ad order by rand() limit 1");
            
            while (resultSet.next()){
                
                randomAdver=resultSet.getString("advertizement");
            
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                Socket socket = ss.accept();
                
                randomAd();

                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(randomAdver);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
