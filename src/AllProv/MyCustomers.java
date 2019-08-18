package AllProv;

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


public class MyCustomers implements Runnable{
    
    final static int PORT = 14000;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;


   
    public MyCustomers() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> rec=new ArrayList();
    
    public void selectAllCustRec(String u){
        try {
            String query=("select * from buys where seller='"+u+"'");
            resultSet=statement.executeQuery(query);
            
            int ctr=0;

            while(resultSet.next()){
                String seller=resultSet.getString("seller");               
                String buyer=resultSet.getString("buyer");
                
                String foodItem=resultSet.getString("foodItem");
                
                String one=(buyer+"~"+seller+"~"+foodItem);
                rec.add(one);
            }

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(rec);
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
                selectAllCustRec(u);
                
                oos.writeObject(rec);
                rec.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
