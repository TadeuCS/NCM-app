/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Tadeu
 */
public class Usuario {

    public Usuario() {
        senha = "3e3c5d866425a9f1e9641566ed35943b31ed5c";
        user= "admin";
    }
    
    private String user;
    private String senha;
    

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    
}
