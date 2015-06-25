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
    final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    int dia;
    int mes;
    int ano;

    public int getDia(Date data) {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getMes(Date data) {
        c=(GregorianCalendar) GregorianCalendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public int getAno(Date data) {
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
            Date dataValidade = new Date(format.parse(props.ler("validade")).getTime());
            Date data2 = new Date(format.parse(format.format(data)).getTime());

            if (dataValidade.before(data2)) {
                System.out.println("Data: dataValidade é posterior à data");
            } else {
                System.out.println("Data: dataValidade é inferior à data");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public Date addDayOfDate(Date data, int dias) {
        c.setTime(data);
        c.add(Calendar.DATE, +dias);
        Date dataAlterada = null;
        try {
            dataAlterada = new Date(format.parse(format.format(c)).getTime());
        } catch (Exception e) {
            System.out.println(e);
        }
        return dataAlterada;
    }
}
