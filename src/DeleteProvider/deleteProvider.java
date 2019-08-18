
package DeleteProvider;

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


public class deleteProvider implements Runnable{
    final static int PORT = 5858;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String error="";
    String validity="";
    

    public deleteProvider() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void deletP(String name) throws SQLException{
        checkUserName(name);
        if(validity.equals("yes")){
            String query=String.format("delete from provider where proname='%s'",name);
            statement.execute(query);
            String query2=String.format("delete from allUsers where username='%s'",name);
            statement.execute(query2);
            error="Successfully Deleted";
        }else{
            error="Provider Name Does Not Exist";
        }
        
    }
    public void checkUserName(String proname){
        try {
            ResultSet rs=statement.executeQuery("select 1 from provider where proname='"+proname+"'");
            if(rs.first()==true){
               validity="yes";
            }else{
               validity="no";
            }

        } catch (Exception e) {
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
                
                try {
                    deletP(msg);
                } catch (SQLException ex) {
                    Logger.getLogger(deleteProvider.class.getName()).log(Level.SEVERE, null, ex);
                }

                output.writeUTF(error);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
