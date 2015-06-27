/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Tadeu
 */
public class Validade {

    GregorianCalendar c = new GregorianCalendar();
    PropertiesManager props;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    int dia;
    int mes;
    int ano;

    public int getDia(Date data) {
        c.setTime(data);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getMes(Date data) {
        c.setTime(data);
        return c.get(Calendar.MONTH)+1;
    }

    public int getAno(Date data) {
        c.setTime(data);
        return c.get(Calendar.YEAR);
    }

    public boolean validaSistema(Date data2) {
        props = new PropertiesManager();
        Date dataValidade;
        Date data;
        boolean retorno = false;
        try {
            dataValidade = new Date(format.parse(props.ler("validade")).getTime());
            data = new Date(format.parse(format.format(data2)).getTime());
            if (dataValidade.after(data)) {
                retorno = true;
            } else {
                retorno = false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return retorno;
    }

    public void comparaDatas(Date data) {
        try {
            props = new PropertiesManager();
            Date dataValidade = new Date(props.ler("validade"));
            Date dataAtual = new Date();
            if (dataAtual.before(dataValidade)) {
                System.out.println(format.format(dataAtual) + " antes que " + format.format(dataValidade));
            } else {
                System.out.println(format.format(dataAtual) + " depois que " + format.format(dataValidade));
            }
        } catch (Exception e) {
            System.out.println("Erro ao comparar as datas:" + e.getMessage());
        }

    }

    public String addDayOfDate(Date data, int dias) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(data);
            c.add(Calendar.DATE, +dias);
        } catch (Exception e) {
            System.out.println("Erro ao adicionar " + dias + " a data de valide: " + e.getMessage());
        }
        return format.format(c.getTime());
    }
}
