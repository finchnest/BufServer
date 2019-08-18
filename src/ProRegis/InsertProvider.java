
package ProRegis;


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


public class InsertProvider implements Runnable{
    
    final static int PORT = 7777;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String mess="";
    String validity="";
    

    public InsertProvider() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void insertData(String un, String pass,  int account) {
        try {
            if(checkBankId(account)){
                checkUserName(un);
                if(validity.equals("yes")){
                    String query=String.format("insert into provider values('%s','%s','%d')",un,pass,account);
                    statement.execute(query);
                    String query2=String.format("insert into allUsers values('%s','%s','%s')",un,pass,"provider");
                    statement.execute(query2);
                    mess="Successfully Registered";
                    System.out.println("registered");
                }else{
                    mess="Username Occupied";
                }
            }else{
                mess="Invalid Bank Id";
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
                insertData(splitted[0], splitted[1],Integer.parseInt(splitted[2]));

                output.writeUTF(mess);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
