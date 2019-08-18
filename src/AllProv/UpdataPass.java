
package AllProv;


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
import java.util.logging.Level;
import java.util.logging.Logger;


public class UpdataPass implements Runnable{
    
    final static int PORT = 11112;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String message="";
    
    
    public UpdataPass() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void updatePassword(String newPass, String proName) throws SQLException{
    
        String query=String.format("update provider set password='%s' where proname='%s'",newPass,proName);
        statement.execute(query);
        
        String query2=String.format("update allUsers set password='%s' where username='%s'",newPass,proName);
        statement.execute(query2);
        
        message="Password Changed Successfully";
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
                String[] splitted = msg.split("\\s");
                
                try {
                    updatePassword(splitted[0],splitted[1]);
                } catch (SQLException ex) {
                    Logger.getLogger(UpdataPass.class.getName()).log(Level.SEVERE, null, ex);
                }
                

                output.writeUTF(message);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    
}
