/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Tadeu
 */
public class Teste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Validade validade = new Validade();
        SimpleDateFormat sd=new SimpleDateFormat("dd/MM/yyyy");
        Date dataValidade = new Date("2015/06/27");
        Date dataAtual = new Date();
//        System.out.println(sd.format(dataAtual));
//        System.out.println(sd.format(dataValidade));
        if(dataAtual.before(dataValidade)){
            System.out.println(sd.format(dataAtual)+ " antes que "+sd.format(dataValidade));
        }else{
            System.out.println(sd.format(dataAtual)+ " depois que "+sd.format(dataValidade));
        }
    }

}
