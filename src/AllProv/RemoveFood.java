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
import java.util.logging.Level;
import java.util.logging.Logger;


public class RemoveFood implements Runnable{
    
    final static int PORT = 11113;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String queryy;

   
    public RemoveFood() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> foods=new ArrayList();
    
    public void seeFoods(String provider) throws SQLException{
        try {
            
            queryy=String.format("select * from food where provider='%s'",provider);
            resultSet=statement.executeQuery(queryy);
            
            int ctr=0;

            while(resultSet.next()){               
                String foodNa=resultSet.getString("foodName");
                String price=Integer.toString(resultSet.getInt("price"));
                String foodtyp=resultSet.getString("foodType");
                String rat;
                if(resultSet.getFloat(5)>1){
                      rat=Float.toString(resultSet.getFloat("rating"));
                }else{
                    rat="0.0";
                }
                String raters;
                if (true) {
                    raters=Integer.toString(resultSet.getInt("ratersCount"));
                } else {
                    raters="0";
                }
                String foodC=Integer.toString(resultSet.getInt("foodCount"));

                String one=foodNa+"~"+price+"~"+foodtyp+"~"+ rat+"~"+raters+"~"+foodC;
                foods.add(one);
            }
            
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(foods);
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
                
                input = new DataInputStream(s.getInputStream());
                output = new DataOutputStream(s.getOutputStream());
                String pr = input.readUTF();
                
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                
                try {
                    seeFoods(pr);
                } catch (SQLException ex) {
                    Logger.getLogger(RemoveFood.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                oos.writeObject(foods);
                foods.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    
}
