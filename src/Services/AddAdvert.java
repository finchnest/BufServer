
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


public class AddAdvert implements Runnable{
    
    final static int PORT = 17777;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String mess="";
    String validity="";
    

    public AddAdvert() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void insertAd(String advertizer, String ad) {
        try {
            String query=String.format("insert into ad values('%s','%s')",advertizer,ad);
            statement.execute(query);
            mess="Advertizement Added Successfully";
        } catch (Exception e) {
            System.out.println(e);
        }
    }
        
    @Override
    public void run() {
        try {
            ServerSocket st = new ServerSocket(PORT);
            while (true) {
                Socket s = st.accept();
                input = new DataInputStream(s.getInputStream());
                output = new DataOutputStream(s.getOutputStream());
                String msg = input.readUTF();
                String[] splitted = msg.split("~");
                
                insertAd(splitted[0], splitted[1]);

                output.writeUTF(mess);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
