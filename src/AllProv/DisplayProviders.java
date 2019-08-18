
package AllProv;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
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

public class DisplayProviders implements Runnable{
    
    final static int PORT = 4949;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
    String queryy;

   
    public DisplayProviders() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> pros=new ArrayList();
    
    public void selectAllPros() throws SQLException{
        try {
            
            queryy=("select* from provider");
            resultSet=statement.executeQuery(queryy);
            
            int ctr=0;

            while(resultSet.next()){               
                String username=resultSet.getString("proname");
                int account=resultSet.getInt("bankNum");
                
//                output.writeUTF(fname+" "+lname+" "+username+" "+password+" "+usertype+" "+Integer.toString(account)+" ");
//                ctr+=1;
//                System.out.println(resultSet.getString(1));
                String one=(username+"~"+Integer.toString(account));
                pros.add(one);
            }
           
            
            
//            output.writeUTF("stop");
//            System.out.println(ctr);

            
            

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(pros);
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

                
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                try {
                    selectAllPros();
                } catch (SQLException ex) {
                    Logger.getLogger(DisplayProviders.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                oos.writeObject(pros);
                pros.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
