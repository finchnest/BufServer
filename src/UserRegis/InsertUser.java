package UserRegis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.rowset.serial.SerialBlob;


public class InsertUser implements Runnable {

    final static int PORT = 8888;

    Connection connection;
    Statement statement;
    PreparedStatement ps;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String error="";
    String validity="";
    

    public InsertUser() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }

    public void insertData(String fn, String ln, String un, String pass, String ut, int account/*,byte[] imageByte*/) {
        try {
            if(checkBankId(account)){
                checkUserName(un);
                if(validity.equals("yes")){
                    
//                    Blob blob = new SerialBlob(imageByte);
                    
                    
                    String query=String.format("insert into user values('%s','%s','%s','%s','%s',%d);",fn,ln,un,pass,ut,account);
                    statement.execute(query);
                    
                    String qqq=String.format("insert into permanentdata values('%s','%s','%s','%s','%s',%d);",fn,ln,un,pass,ut,account);
                    statement.execute(qqq);
                    
//                    String quer="INSERT INTO user(fname,lname,username,password,usertype,bankNo,profile) "+ "VALUES(?,?,?,?,?,?,?)";
//                    ps = connection.prepareStatement(quer);
//                    ps.setString(1,fn);
//                    ps.setString(2,ln);
//                    ps.setString(3,un);
//                    ps.setString(4,pass);
//                    ps.setString(5,ut);
//                    ps.setInt(6,account);
//                    ps.setBlob(7, blob);
//                    ps.execute();
                    
                  
                    String query2=String.format("insert into allUsers values('%s','%s','%s')",un,pass,"buyer");
                    statement.execute(query2);
                    
                    error="Successfully Registered";
                    System.out.println("registered");
                    
                }else{
                    error="Username Occupied";
                }
            }else{
                error="Invalid Bank Id";
            }
            
            


        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void checkUserName(String un){
        try {
            ResultSet rs=statement.executeQuery("select 1 from allUsers where username='"+un+"'");
            if(rs.first()==true){
               validity="no";
            }else{
               validity="yes";
            }
            
        } catch (Exception e) {
        }
    }
    
    public boolean checkBankId(int bankNo){
        try {
            ResultSet rs=statement.executeQuery("select 1 from bank where bankAccount='"+bankNo+"'");
            if(rs.first()==true){
               return true;
            }else{
                return false;
            }
            
        } catch (Exception e) {
        }
        return false;
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
                
                /*
                int length = input.readInt(); 
                byte[] message = new byte[length];
                input.readFully(message, 0, message.length); 
                */
                
                insertData(splitted[0], splitted[1],splitted[2],splitted[3],splitted[4],Integer.parseInt(splitted[5])/*,message*/);

                output.writeUTF(error);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
