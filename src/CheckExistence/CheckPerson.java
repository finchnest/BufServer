package CheckExistence;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class CheckPerson implements Runnable {

    final static int PORT = 9090;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    ResultSet rs;
    DataInputStream input;
    DataOutputStream output;
    String wholeMes="";
    String valid="no";
    String role="";
    String buyer_type="";
    String encoded;
    
    private static final int ITERATIONS = 2*1000;
    private static final int DESIRED_KEY_LEN = 128;

    public CheckPerson() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bufdata", "root", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error Connecting to Database "+e.toString());
        }
    }

    public void check(String name, String pass) throws SQLException, Exception {
            resultSet=statement.executeQuery("select * from allusers where username='"+name+"'");
            
            while(resultSet.next()){
                
                encoded=resultSet.getString("password");
                
                if(encoded.equals(pass) && encoded.length()<20){
                    valid="yes";

                        role=resultSet.getString("role");

                        if(role.equalsIgnoreCase("buyer")){
                            String qq=String.format("select * from user where username='%s' and password='%s'",name,pass);
                            rs=statement.executeQuery(qq); 
                            if(rs.next()){
                                buyer_type=rs.getString(5); 
                            }
                        }
                }else if(encoded.length()>20){
                    if(decoder(pass,encoded)){
                        System.out.println("decodedddddd");
                        valid="yes";

                        role=resultSet.getString("role");

                        if(role.equalsIgnoreCase("buyer")){
                            String qq=String.format("select * from user where username='%s' and password='%s'",name,encoded);
                            rs=statement.executeQuery(qq); 
                            if(rs.next()){
                                buyer_type=rs.getString(5); 
                            }
                        }
                    }
                }
                break;
            }
            
    }
    
    public  boolean decoder(String password, String stored) throws Exception{
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException(
                "The stored password must have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndHash[0]));
        return hashOfInput.equals(saltAndHash[1]);
    }
    
    private  String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
            password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LEN));
        return Base64.encodeBase64String(key.getEncoded());
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                Socket socket = ss.accept();
                input = new DataInputStream(socket.getInputStream());
                
                String msg = input.readUTF();
                String[] splitted = msg.split("\\s");
                try {
                    check(splitted[0], splitted[1]);
                    output = new DataOutputStream(socket.getOutputStream());
                    wholeMes=valid+"~"+role+"~"+buyer_type;
                    output.writeUTF(wholeMes);
                    valid="no";
                    wholeMes="";
                    System.out.println(wholeMes);
                    System.out.println(valid);
                    
                } catch (SQLException ex) {
                    Logger.getLogger(CheckPerson.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(CheckPerson.class.getName()).log(Level.SEVERE, null, ex);
                }

                

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}