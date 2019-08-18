
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


public class BuyOperation implements Runnable{
    
    final static int PORT = 12080;
    
    Connection connection;
    Statement statement;
    DataInputStream input;
    DataOutputStream output;
    String validater="";

   
    public BuyOperation() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    public void performBuy(String food, String provider,String user) throws SQLException{
        try{
            String qw=String.format("select * from food where foodName='%s' and provider='%s'",food,provider);
            ResultSet resultSet=statement.executeQuery(qw);

            if(resultSet.first()==true){
                int count=resultSet.getInt("foodCount");
                System.out.println(count);
                if(count>0){
                    String countReducer=String.format("update food set foodCount=%d where foodName='%s' and provider='%s'",resultSet.getInt("foodCount")-1,food,provider);
                    statement.execute(countReducer);

                    String transaction_saver=String.format("insert into buys values('%s','%s','%s')",user,provider,food);
                    statement.execute(transaction_saver);

                    validater="Buying Request Successful";

                }else{
                    validater="Buying Request Unsuccessful";
                }
            }
        }catch(Exception e){
            e.getMessage();
        }
    }
    
    @Override
    public void run() {
        try {
            ServerSocket st= new ServerSocket(PORT);
            while(true){
                Socket s=st.accept();
                
                input=new DataInputStream(s.getInputStream());
                output=new DataOutputStream(s.getOutputStream());
                
                String f=input.readUTF();
                String [] splitted=f.split("~");
                
                try {
                    performBuy(splitted[0],splitted[1],splitted[2]);
                    output.writeUTF(validater);
                    
                } catch (SQLException ex) {
                    Logger.getLogger(BuyOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
