
package AllUsers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class RemoveMyAccount implements Runnable{
    
    final static int PORT = 12001;
    
    Connection connection;
    Statement statement;
    DataInputStream input;
    DataOutputStream output;
    
    

    public RemoveMyAccount() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void deletMyAcc(String name) throws SQLException{
            String query=String.format("delete from user where username='%s'",name);
            statement.execute(query);
            String query2=String.format("delete from allUsers where username='%s'",name);
            statement.execute(query2);
            System.out.println("Successfully Deleted");
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
                
                try {
                    deletMyAcc(msg);
                } catch (SQLException ex) {
                }
                
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
