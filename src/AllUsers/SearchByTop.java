
package AllUsers;

import static AllUsers.DisplayUsers.PORT;
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


public class SearchByTop implements Runnable{
    
        
    final static int PORT = 13600;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    DataInputStream inputS;


   
    public SearchByTop() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> topData=new ArrayList();
    
    
    public void selectTop(String f){
        try {
            
            String query="";
            if (f.equals("all")) {
                query=String.format("select * from food where category is not null order by rating desc");
            } else {
                query=String.format("select * from food where category='%s' order by rating desc",f);

            }
            
            
            resultSet=statement.executeQuery(query);
            int ctr=0;

            while(resultSet.next()){               
                String foo=resultSet.getString("foodName");
                String pro=resultSet.getString("provider");
                int pri=resultSet.getInt("price");
                String type=resultSet.getString("foodType");
                float rating=resultSet.getFloat("rating");
                int available=resultSet.getInt("foodCount");

                String one=foo+"~"+pro+"~"+pri+"~"+type+"~"+rating+"~"+available;
                topData.add(one);
            }

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(topData);
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
                
                inputS=new DataInputStream(s.getInputStream());
                
                String f=inputS.readUTF();

                selectTop(f);
                
                oos.writeObject(topData);
                topData.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
