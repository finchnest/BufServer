package Services;


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


public class PDFReport implements Runnable {

    final static int PORT = 11999;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    DataInputStream input;
    DataOutputStream output;
//    String queryy;
    String query;

   
    public PDFReport() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to database "+e.toString());
        }
    }
    
    ArrayList<String> user=new ArrayList();
    
    public void selectAllUsers(String report_type){
        try {
//            queryy=("select* from user");
            
//            String query="";
            if (report_type.equalsIgnoreCase("current")) {
                 query=("select * from user");
            } else {
                query=("select * from permanentData");
            }
            
            
            
            resultSet=statement.executeQuery(query);
            int ctr=0;

            while(resultSet.next()){               
                String fname=resultSet.getString("fname");
                String lname=resultSet.getString("lname");
                String username=resultSet.getString("username");
                String usertype=resultSet.getString("usertype");
                int account=resultSet.getInt("bankNo");
                
                String one=(fname+"~"+lname+"~"+username+"~"+usertype+"~"+Integer.toString(account));
                user.add(one);
            }
            
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(user);
            oos.close();

            
        } catch (IOException | SQLException e) {
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

//                input=new DataInputStream(s.getInputStream());
//                selectAllUsers(input.readUTF());
                String str="";
                try {
                    str = (String)ois.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PDFReport.class.getName()).log(Level.SEVERE, null, ex);
                }
                selectAllUsers(str);
                oos.writeObject(user);
                user.clear();
                    
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
