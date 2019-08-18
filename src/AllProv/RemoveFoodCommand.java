package AllProv;

import static AllProv.RemoveFood.PORT;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoveFoodCommand implements Runnable{
    
    final static int PORT = 11115;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String queryy;
    
    public RemoveFoodCommand() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    public void dele(String pro, String food) throws SQLException, IOException{
        String query=String.format("delete from food where provider='%s' and foodName='%s'",pro,food);
        statement.execute(query);
        
    }
    
    @Override
    public void run() {
        try {
            ServerSocket st= new ServerSocket(PORT);
            while(true){
                Socket s=st.accept();
                
                input = new DataInputStream(s.getInputStream());
                output = new DataOutputStream(s.getOutputStream());
                String pr = input.readUTF();
                String [] splitted=pr.split("~");
                try {
                    dele(splitted[0],splitted[1]);
                    output.writeUTF("Deleted Sccessfully");
                } catch (SQLException ex) {
                    Logger.getLogger(RemoveFoodCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                 
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
