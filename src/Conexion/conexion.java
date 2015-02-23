/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleDriver;

/**
 *
 * @author Oscar_Malagon
 */
public class conexion  {

 /*   public static Connection getConnection() throws SQLException {
        String username = "system";
        String password = "oscar";
        String thinConn = "jdbc:oracle:thin:@localhost:1521:xe";
        DriverManager.registerDriver(new OracleDriver());
        Connection conn = DriverManager.getConnection(thinConn, username, password);
        conn.setAutoCommit(false);
        return conn;
    }
   */ 
    
   public static Connection getConnection()throws SQLException{
        try {
            //cargar nuestro driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
           Connection con=DriverManager.getConnection("jdbc:mysql://localhost/biotrends","root","arcgis2015");
       return con;
   }
    
   
   
}
