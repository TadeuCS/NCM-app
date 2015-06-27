/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

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
        Date data= new Date();
        validade.comparaDatas(data);
//        validade.addDayOfDate(data, 2);
        System.out.println(validade.getMes(data));
    }

}
