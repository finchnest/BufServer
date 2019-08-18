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
import java.math.BigDecimal;

public class AfterHandler implements Runnable{
    
    final static int PORT = 13000;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String validity="";
    String qr;
    static String splitted[];
    static String provi="";
    
    public AfterHandler() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database"+e.toString());
        }
    }
    
    public void afterChanges(String comment, String friend, String rate,String cust,String pro,String food) throws SQLException, IOException{
        
        provi=pro;
        
        if(!comment.equals("0")){
            String query=String.format("insert into comment values('%s','%s','%s','%s')",food,cust,pro,comment);
            statement.execute(query);
        }
        
        if(!rate.equals("0")){
            String rateQ=String.format("select * from food where provider='%s' and foodName='%s'",pro,food);
            resultSet=statement.executeQuery(rateQ);

            if(resultSet.first()==true){
                int old_raters=resultSet.getInt("ratersCount");
                
                int cust_rate=Integer.parseInt(rate);
                
                if(old_raters>=1){
                    float old_rate=resultSet.getFloat("rating");
                    
                    float newRate=(float)(old_rate*old_raters+cust_rate)/(old_raters+1);
                    float rounded=Float.parseFloat(String.format("%.2f", newRate));
                    String fin_change=String.format("update food set rating=%e, ratersCount=%d where foodName='%s' and provider='%s'",rounded,old_raters+1,food,pro);
                    statement.execute(fin_change);
                }else{
                    String fin_change=String.format("update food set rating=%d, ratersCount=1 where foodName='%s' and provider='%s'",cust_rate,food,pro);
                    statement.execute(fin_change);
                }
                
            }
        }
        
        
        if(!friend.equals("0")){
            String qu=String.format("select * from user where username='%s'",friend);
            resultSet=statement.executeQuery(qu);
            
                if (resultSet.next()) {
                    
                    qr=String.format("insert into notification values('%s','%s','Try this "+food+" It is Awesome')",cust,friend);
                    statement.execute(qr);
                    validity="Message Sent"; 
                } else {
                    validity="This User Does Not Exist";
                }
            
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
                splitted = msg.split("~");
                
                try {
                    afterChanges(splitted[0],splitted[1],splitted[2],splitted[3],splitted[4],splitted[5]);
                    output.writeUTF(validity);
                } catch (SQLException ex) {
                    Logger.getLogger(AfterHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
