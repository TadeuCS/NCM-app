/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Tadeu
 */
public class Criptografia {
        public static String criptografaSenha(String senha) {  
        MessageDigest md = null;  
        String password = new String(senha);  
        try {  
            md = MessageDigest.getInstance("Sha-1");
        } catch (NoSuchAlgorithmException _exNSAE) {  
            System.out.println("Algoritmo de criptografia não encontrado. \n Execução abortada.");
        }  
        md.reset();  
        md.update(password.getBytes());  
        byte[] digest1 = md.digest();  
  
        StringBuffer encryptedPassword = new StringBuffer();  
        for (int i = 0; i < digest1.length; i++) {  
            encryptedPassword.append(Integer.toHexString(0xFF & digest1[i]));  
        }  
        return encryptedPassword.toString();  
    }  
}
