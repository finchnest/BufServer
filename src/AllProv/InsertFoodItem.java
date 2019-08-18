
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

public class InsertFoodItem implements Runnable{

    final static int PORT = 11111;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String mess="";
    String validity="";
    
    public InsertFoodItem() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void insertFoodData(String foodName, double price,  String foodtype, int count,String provider, String cat) throws SQLException {
        
        resultSet=statement.executeQuery("select * from food where provider='"+provider+"' and foodName='"+foodName+"'");

        if(resultSet.next()){
            int newCount=resultSet.getInt(7)+count;
            String query=String.format("update food set foodCount='%d' where provider='%s' and foodName='%s'",newCount,provider,foodName);
            statement.execute(query);
        }else{
            String query=String.format("insert into food values('%s','%s','%e','%s',1,null,'%d','%s')",foodName,provider,price,foodtype,count, cat);
            statement.execute(query);
        }
        
        mess="Successfully Registered";
        System.out.println("registered");
    
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
                try {
                    insertFoodData(splitted[0], Double.parseDouble(splitted[1]),splitted[2],Integer.parseInt(splitted[3]), splitted[4], splitted[5]);
                } catch (SQLException ex) {
                    Logger.getLogger(InsertFoodItem.class.getName()).log(Level.SEVERE, null, ex);
                }

                output.writeUTF(mess);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
