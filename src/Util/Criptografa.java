/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Tadeu
 */
public class Criptografa {

    public byte[] criptografa(String texto) {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            SecretKey chaveDES = keygenerator.generateKey();

            Cipher cifraDES;

            // Cria a cifra 
            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifraDES.init(Cipher.ENCRYPT_MODE, chaveDES);
            byte[] textoEncriptado = cifraDES.doFinal(texto.getBytes());
            // Texto puro
            return texto.getBytes();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String descriptografa(byte[] texto) {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            SecretKey chaveDES = keygenerator.generateKey();

            Cipher cifraDES;

            // Cria a cifra 
            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifraDES.init(Cipher.DECRYPT_MODE, chaveDES);

            // Inicializa a cifra também para o processo de decriptação
            cifraDES.init(Cipher.DECRYPT_MODE, chaveDES);

            // Decriptografa o texto
            byte[] textoDecriptografado = cifraDES.doFinal(texto);
            String retorno=null;
            retorno=new String(textoDecriptografado);
             return retorno;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
