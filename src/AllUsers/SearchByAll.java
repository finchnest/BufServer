
package AllUsers;

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


public class SearchByAll implements Runnable{
    
    final static int PORT = 19111;
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;


   
    public SearchByAll() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> foodData=new ArrayList();
    
    public void selectAllRequestedFoods(String f) throws IOException{
        try {
            String q=String.format("select * from food where category is not null");
            resultSet=statement.executeQuery(q);
            
            int ctr=0;

            while(resultSet.next()){
                if(resultSet.getInt("foodCount")>0){
                    String food=resultSet.getString("foodName");
                    String pro=resultSet.getString("provider");
                    int price=resultSet.getInt("price");
                    String type=resultSet.getString("foodType");
                    Float rating =resultSet.getFloat("rating");
                    int available=resultSet.getInt("foodCount");

                    String one=food+"~"+pro+"~"+Integer.toString(price)+"~"+type+"~"+Float.toString(rating)+"~"+Integer.toString(available);
                    foodData.add(one);
                }
            }

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(foodData);
            oos.close();
            
        } catch (SQLException e) {
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
                input=new DataInputStream(s.getInputStream());
//                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                
                String f=input.readUTF();
//                String[] sep=f.split("~");
                
                selectAllRequestedFoods(f);
                
                oos.writeObject(foodData);
                foodData.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
