package AllProv;

import static AllProv.MyCustomers.PORT;
import java.io.ByteArrayOutputStream;
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
import java.util.ArrayList;

public class Comments implements Runnable{
    
    final static int PORT = 15000;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;


   
    public Comments() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> comms=new ArrayList();
    
    public void selectAllCustComm(String u){
        try {
            String query=("select * from comment where producer='"+u+"'");
            resultSet=statement.executeQuery(query);
            
            int ctr=0;

            while(resultSet.next()){
                String foodItem=resultSet.getString("foodItem");
                String cust=resultSet.getString("commentor");               
                String pro=resultSet.getString("producer");
                String comment=resultSet.getString("comment");
               
                
                String one=(foodItem+"~"+cust+"~"+pro+"~"+comment);
                comms.add(one);
            }

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(comms);
            oos.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
        
    @Override
    public void run() {
        try {
            ServerSocket st= new ServerSocket(PORT);
            while(true){
                Socket s=st.accept();
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                DataInputStream dinp=new DataInputStream(s.getInputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                String u=dinp.readUTF();
                selectAllCustComm(u);
                
                oos.writeObject(comms);
                comms.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
