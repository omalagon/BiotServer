/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;

/**
 *
 * @author Oscar_Malagon
 */
public class conexion  {

    public static Connection getConnection() throws SQLException {
        String username = "system";
        String password = "oscar";
        String thinConn = "jdbc:oracle:thin:@localhost:1521:xe";
        DriverManager.registerDriver(new OracleDriver());
        Connection conn = DriverManager.getConnection(thinConn, username, password);
        conn.setAutoCommit(false);
        return conn;
    }
    
}
