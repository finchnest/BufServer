/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AllUsers;

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

public class ChangeAccType implements Runnable{
    
    final static int PORT = 12005;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String newType="";

    public ChangeAccType() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void changeType(String uname) throws SQLException{
    
        String query=String.format("select * from user where username='%s'",uname);
        resultSet=statement.executeQuery(query);
        
        String currentType="";
        while(resultSet.next()){
            currentType=resultSet.getString(5);
        }
        
        
        if(currentType.equalsIgnoreCase("normal")){
            String changeQuery=String.format("update user set usertype='premium' where username='%s'",uname);
            statement.execute(changeQuery);
            newType="premium";
        }else{
            String changeQuery=String.format("update user set usertype='normal' where username='%s'",uname);
            statement.execute(changeQuery);
            newType="normal";
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
                    changeType(msg);
                    output.writeUTF(newType);
                } catch (SQLException ex) {
                    Logger.getLogger(ChangeAccType.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}

