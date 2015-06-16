/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Tadeu
 */
public class Conexao {

    PropertiesManager props;
    Connection con;
    Statement st;

    public Statement getConexao(String ip,String diretorio, String usuario, String senha) {
        try {
            props = new PropertiesManager();
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection(
                    "jdbc:firebirdsql://"
                    + ip + ":3050/"
                    + diretorio,
                    usuario,
                    senha);
            st = con.createStatement();
            return st;
        } catch(Exception e){
            return null;
        }
    }
}
