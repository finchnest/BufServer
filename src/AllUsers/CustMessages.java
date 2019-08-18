
package AllUsers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class CustMessages implements Runnable{
    
    final static int PORT = 12050;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;


   
    public CustMessages() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> noti=new ArrayList();
    
    public void selectAllMessages(String u){
        try {
            String query=String.format("select * from notification where reciever='%s'",u);
            resultSet=statement.executeQuery(query);
            
            int ctr=0;

            while(resultSet.next()){               
                String reciever=resultSet.getString("reciever");
                String sender=resultSet.getString("sender");
                String message=resultSet.getString("message");
                
                String one=reciever+"~"+sender+"~"+message;
                noti.add(one);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
        
    @Override
    public void run() {
        try {
            ServerSocket st= new ServerSocket(PORT);
            while(true){
                Socket s=st.accept();
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                DataInputStream din=new DataInputStream(s.getInputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                String u=din.readUTF();
                selectAllMessages(u);
                
                oos.writeObject(noti);
                noti.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
