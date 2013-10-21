/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.*;

/**
 *
 * @author Tadeu
 */
public class Frm_Principal extends javax.swing.JFrame {

    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    String diretorio = null;
    String dir = null;
    String ip = null;
    static String codclasfis = null;
    static String codprod = null;
    static String listaProdutos;
    static String listaProdutosbyDescricao;
    static String descricao = null;
    static int click = 0;
    PrintWriter pw;

    public Frm_Principal() {
        initComponents();
    }

    public void conecta() throws IOException {
        try {
            leArquivo();
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection(
                    "jdbc:firebirdsql://" + ip + ":3050/" + diretorio,
                    "SYSDBA",
                    "masterkey");
            st = con.createStatement();
        } catch (ClassNotFoundException ex)//caso o driver não seja localizado  
        {
            JOptionPane.showMessageDialog(null, "Driver não encontrado!");
        } catch (SQLException ex)//caso a conexão não possa se realizada  
        {
            JOptionPane.showMessageDialog(null, "Problemas na conexao com a fonte de dados");
        }
    }

    public void leArquivo() throws IOException {
        File file = new File("C:/NCM-app/src/Controller/config.txt");
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        BufferedReader br = new BufferedReader(fr);

        String linha = br.readLine();
        ip = linha;
        String linha2 = br.readLine();
        diretorio = linha2;
    }

    public void start() throws Exception {
        marcaOpcoes();
        try {
            conecta();
            listaProdutosPis();
            listaProdutos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void enabledsOn() throws Exception {
        fundo.setEnabled(false);
        pnl_fundo.setEnabled(false);
        txt_descricao.setEnabled(false);
        cb_embranco.setEnabled(false);
        btn_conexao.setEnabled(false);
        tabela1.setEnabled(false);
    }

    public void enabledsOff() throws Exception {
        fundo.setEnabled(true);
        pnl_fundo.setEnabled(true);
        txt_descricao.setEnabled(true);
        cb_embranco.setEnabled(true);
        btn_conexao.setEnabled(true);
        tabela1.setEnabled(true);
    }

    public void marcaOpcoes() {
        chx_selecionaAll.setSelected(true);
        cb_embranco.setSelected(true);
        chx_aliq_entrada.setSelected(true);
        chx_estrutura.setSelected(true);
        chx_itens_null.setSelected(true);
        chx_pis_entrada.setSelected(true);
        chx_pis_saida.setSelected(true);
    }

    public void listaProdutos() throws Exception {
        validaEmBranco();
        Statement st;
        st = con.createStatement();
        ResultSet rs = st.executeQuery(listaProdutos);
        DefaultTableModel model = (DefaultTableModel) tabela1.getModel();
        while (rs.next()) {
            if (cb_embranco.getSelectedObjects() != null) {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO")};
                model.addRow(linha);
            } else {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO"), rs.getString("CODIGONCM")};
                model.addRow(linha);
            }
        }

    }

    public void listaProdutosbyDescricao() throws Exception {
        limpaTabela1();
        validaEmBranco();
        Statement st;
        st = con.createStatement();
        ResultSet rs = st.executeQuery(listaProdutosbyDescricao);
        DefaultTableModel model = (DefaultTableModel) tabela1.getModel();
        while (rs.next()) {
            if (cb_embranco.getSelectedObjects() != null) {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO")};
                model.addRow(linha);
            } else {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO"), rs.getString("CODIGONCM")};
                model.addRow(linha);
            }
        }

    }

    public void limpaTabela1() {
        try {
            DefaultTableModel tblRemove = (DefaultTableModel) tabela1.getModel();
            while (tblRemove.getRowCount() > 0) {
                for (int i = 1; i <= tblRemove.getRowCount(); i++) {
                    tblRemove.removeRow(0);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public void limpaTabela2() {
        try {
            DefaultTableModel tblRemove = (DefaultTableModel) tabela2.getModel();
            while (tblRemove.getRowCount() > 0) {
                for (int i = 1; i <= tblRemove.getRowCount(); i++) {
                    tblRemove.removeRow(0);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public void validaNCM(String codigoNcm) throws Exception {
        DefaultTableModel model = (DefaultTableModel) tabela1.getModel();
        st = con.createStatement();

        rs = st.executeQuery("SELECT * FROM CLASFISC WHERE CODIGONCM='" + codigoNcm + "'");


        if (rs.next()) {
            codclasfis = rs.getString("CODCLASFIS");
            codprod = tabela1.getValueAt(tabela1.getSelectedRow(), 0).toString();
            try {
                st.executeUpdate("UPDATE PRODUTO SET CODCLASFIS = '" + codclasfis + "' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "NCM: " + codigoNcm + " foi inserido no Produto: " + codprod);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        } else {
            insereNCM(codigoNcm);
        }
    }

    public void insereNCM(String codigoNcm) {
        JOptionPane.showMessageDialog(null, "NCM: " + codigoNcm + " invalido!");
        if (JOptionPane.showConfirmDialog(null, "Deseja Cadastrar o segunte NCM: " + codigoNcm, ""
                + "", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                rs = st.executeQuery("select max(codclasfis) as qtde from clasfisc");
                if (rs.next()) {
                    int qtde = Integer.parseInt(rs.getString("qtde").toString()) + 1;
                    codclasfis = qtde + "";
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO CLASFISC (CODCLASFIS, CODNBM, PISCONFRETIDO,CODIGONCM) VALUES "
                        + "('" + codclasfis + "', '" + codigoNcm + "', 1,'" + codigoNcm + "');");
                ps.executeUpdate();
                ps.close();
                if (JOptionPane.showConfirmDialog(null, "NCM: " + codigoNcm + " Cadastrado com Sucesso! \n"
                        + "Deseja Ultiliza-lo no Produto: " + tabela1.getValueAt(tabela1.getSelectedRow(), 0), "", JOptionPane.YES_NO_OPTION) == 0) {
                    validaNCM(codigoNcm);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void enter(KeyEvent e) {

        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 0)) {
            click++;
        }
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 1)) {
            try {
                validaNCM(tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString());
                click = 0;
            } catch (Exception ex) {
                Logger.getLogger(Frm_Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void ctrl_V(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_V) {
            try {
                tabela1.setValueAt(btn_conexao.getText(), tabela1.getSelectedRow(), 2);
            } catch (Exception ex) {
                Logger.getLogger(Frm_Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void ctrl_C(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            try {
                btn_conexao.setText(tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString());
            } catch (Exception ex) {
                Logger.getLogger(Frm_Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void validaEmBranco() throws Exception {
        if (cb_embranco.getSelectedObjects() != null) {
            listaProdutos = "SELECT * FROM PRODUTO WHERE CODCLASFIS ='' and ativo='S' and prodbloqueado='N' order by descricao";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO WHERE DESCRICAO LIKE '%" + descricao + "%' AND CODCLASFIS ='' and ativo='S' and prodbloqueado='N' order by descricao";
        } else {
            listaProdutos = "SELECT * FROM PRODUTO AS P INNER JOIN CLASFISC C ON P.CODCLASFIS=C.CODCLASFIS WHERE C.CODCLASFIS <>'' and p.ativo='S' and p.prodbloqueado='N' order by p.descricao";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO P INNER JOIN CLASFISC AS C ON P.CODCLASFIS=C.CODCLASFIS WHERE P.DESCRICAO LIKE '%" + descricao + "%'  AND C.CODCLASFIS <>'' and p.ativo='S' and p.prodbloqueado='N' order by p.descricao";
        }
    }

    public void listaProdutosPis() throws Exception {
        Statement st;
        st = con.createStatement();
        limpaTabela2();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM PRODUTO P INNER JOIN PRODUTODETALHE D ON P.CODPROD=D.CODPROD WHERE D.PIS_CST='' ORDER BY p.descricao");
            DefaultTableModel model = (DefaultTableModel) tabela2.getModel();
            while (rs.next()) {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO"), rs.getString("PIS_CST")};
                model.addRow(linha);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void enterPis(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 0)) {
            click++;
        }
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 1)) {
            try {
                validaPis(tabela2.getValueAt(tabela2.getSelectedRow(), 2).toString(), tabela2.getValueAt(tabela2.getSelectedRow(), 0).toString());
                click = 0;
            } catch (Exception ex) {
                Logger.getLogger(Frm_Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void validaPis(String pis, String codprod) throws Exception {
        Statement st;
        st = con.createStatement();
        try {
            if ((pis.compareTo("01") == 0) && (tabela2.getSelectedRow() >= 0)) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='50',COFINSENT_CST='50',"
                        + "                                 ALIQPIS='1.65',ALIQCOFINS='7.60',"
                        + "                                 ALIQPISENT='1.65',ALIQCOFINSENT='7.60' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else if ((pis.compareTo("04") == 0) && (tabela2.getSelectedRow() >= 0)) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='70',COFINSENT_CST='70',"
                        + "                                 ALIQPIS='0.00',ALIQCOFINS='0.00',"
                        + "                                 ALIQPISENT='0.00',ALIQCOFINSENT='0.00' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else if ((pis.compareTo("06") == 0) && (tabela2.getSelectedRow() >= 0)) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='70',COFINSENT_CST='70',"
                        + "                                 ALIQPIS='0.00',ALIQCOFINS='0.00',"
                        + "                                 ALIQPISENT='0.00',ALIQCOFINSENT='0.00' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else {
                JOptionPane.showMessageDialog(null, "PIS DE SAIDA INVALIDO!");
                JOptionPane.showMessageDialog(null, "SELECIONE UM PIS E CLICK EM 'ENTER'!");
            }
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void pegaDiretorio() {
        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
        jfc.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        jfc.showSaveDialog(jfc);
        String teste = jfc.getSelectedFile().getPath();
        if((teste.contains(".SQL"))||(teste.contains(".SQL"))){
            dir=teste;
        }else{
            dir=teste+".SQL";
        }
        dir=dir.replace("\\", "/");
    }

    public void corrigeEstrutura() throws Exception {
        Statement st;
        pegaDiretorio();
        pw = new PrintWriter(new FileWriter(dir, false));
        if (chx_estrutura.getSelectedObjects() != null) {
            try {
                st = con.createStatement();
                ResultSet rs = st.executeQuery("Select * From produto\n"
                        + " Where produto.codprod Not In (Select produtodetalhe.codprod From produtodetalhe\n"
                        + "                                Where produtodetalhe.codprod = produto.codprod)");
                int contador = 0;
                txt_areaProcesso.setText("Estrutura\n" + "Gerando Correção...");
                while (rs.next()) {
                    try {
                        pw.println("INSERT INTO PRODUTODETALHE (CODPROD) VALUES ('" + rs.getString("CODPROD") + "');");
                        contador++;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
                pw.close();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n Linhas geradas: " + contador);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n Correção gerada!");
                JOptionPane.showMessageDialog(null, "Arquivo Gerado com Sucesso!\n" + "Caminho: "+dir);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro...");
            }
        }
    }

    public void corrigePisEntrada() throws Exception {
        if (chx_pis_entrada.getSelectedObjects() != null) {
            String quantidade = null;
            PreparedStatement ps;
            Statement st;
            try {

                st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PISENT_CST <>"
                        + " COFINSENT_CST OR (PIS_CST='01' AND PISENT_CST<>'50')\n"
                        + "                        OR (PIS_CST='04' AND PISENT_CST<>'70')\n"
                        + "                        OR (PIS_CST='06' AND PISENT_CST<>'73')");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nPis/Cofins de Entrada" + "\nRealizando Correções... ");
            try {
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PISENT_CST='50' WHERE PIS_CST='01';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PISENT_CST='70' WHERE PIS_CST='04';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PISENT_CST='73' WHERE PIS_CST='06';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET COFINSENT_CST=PISENT_CST;");
                ps.executeUpdate();
                ps.close();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nFim da Correção!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void corrigePisSaida() throws Exception {
        if (chx_pis_saida.getSelectedObjects() != null) {
            String quantidade = null;
            PreparedStatement ps;
            Statement st;
            try {

                st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PIS_CST <> COFINS_CST\n"
                        + "                         OR (PISENT_CST='50' AND PIS_CST<>'01')\n"
                        + "                         OR (PISENT_CST='70' AND PIS_CST<>'04')\n"
                        + "                         OR (PISENT_CST='73' AND PIS_CST<>'06')");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nPis/Cofins de Saida" + "\nRealizando Correções... ");
            try {
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PIS_CST='01' WHERE PISENT_CST='50';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PIS_CST='04' WHERE PISENT_CST='70';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PIS_CST='06' WHERE PISENT_CST='73';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET COFINS_CST=PIS_CST;");
                ps.executeUpdate();
                ps.close();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nFim da Correção!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void corrigeAliquotas() throws Exception {
        if (chx_aliq_entrada.getSelectedObjects() != null) {
            String quantidade = null;
            PreparedStatement ps;
            Statement st;
            try {

                st = con.createStatement();
                ResultSet rs = st.executeQuery("select COUNT(*) as qtde from produtodetalhe where (ALIQPIS<>ALIQPISENT or ALIQCOFINS<>ALIQCOFINSENT) or"
                        + "(PIS_CST='01' AND (ALIQPIS<>'1.65' OR ALIQPISENT<>'1.65')) OR"
                        + "(PIS_CST='04' AND (ALIQPIS<>'0.00' OR ALIQPISENT<>'0.00')) OR"
                        + "(PIS_CST='06' AND (ALIQPIS<>'0.00' OR ALIQPISENT<>'0.00'))");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nAliquotas Entrada/Saida" + "\nRealizando Correções... ");
            try {
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET ALIQPIS='1.65',ALIQCOFINS='7.60',ALIQPISENT='1.65',ALIQCOFINSENT='7.60' WHERE PIS_CST='01';");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE PIS_CST='04';\n");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE PIS_CST='06';\n");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE PRODUTODETALHE SET ALIQPIS=ALIQPISENT,ALIQCOFINS=ALIQCOFINSENT;");
                ps.executeUpdate();
                ps.close();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nFim da Correção!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void corrigeItensNulos() throws Exception {
        if (chx_itens_null.getSelectedObjects() != null) {
            String quantidade = null;
            try {
                Statement st;
                st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PIS_CST IS NULL");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nItens Nullos" + "\nRealizando Correções... ");
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE PRODUTODETALHE SET PIS_CST='',COFINS_CST='',"
                        + "PISENT_CST='',COFINSENT_CST='',ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE "
                        + "PIS_CST IS NULL");
                ps.executeUpdate();
                ps.close();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nFim da Correção!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void apurar() throws Exception {
        PreparedStatement ps;
        int mes = cbx_mes.getMonth() + 1;
        int ano = cbx_ano.getYear();
        try {
            ps = con.prepareStatement("Update NfEntri Set CstPis = (Select PisEnt_Cst From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod),\n"
                    + "                   AliqPis = (Select AliqPisEnt From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod),\n"
                    + "                   VlrPis = (((Select AliqPisEnt From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod) /100) *\n"
                    + "                                  ((NfEntri.Preconf * Nfentri.Quantidade) - NfEntri.Descvlr -\n"
                    + "                                                                        (Select (((N.quantidade)*(N.preconf))/(NfEntrc.descvlrnf+Nfentrc.totalnf))*(NfEntrc.descvlrnf)\n"
                    + "                                                                           From NfEntrc\n"
                    + "                                                                         Inner Join nfentri N on N.codempresa = nfentrc.codempresa And\n"
                    + "                                                                                                 N.numeronf   = nfentrc.numeronf   And\n"
                    + "                                                                                                 N.codfornec  = nfentrc.codfornec  And\n"
                    + "                                                                                                 N.codempresa = nfentri.codempresa And\n"
                    + "                                                                                                 N.numeronf   = nfentri.numeronf   And\n"
                    + "                                                                                                 N.codfornec  = nfentri.codfornec  And\n"
                    + "                                                                                                 N.CodProd    = NfEntri.CodProd))+\n"
                    + "                                                                        (((Select AliqPisEnt From Produtodetalhe\n"
                    + "                                                                            Where ProdutoDetalhe.Codprod = NfEntri.Codprod) /100) *\n"
                    + "                                                                        (Select NfEntrc.totalipi\n"
                    + "                                                                           From NfEntrc\n"
                    + "                                                                          Where NfEntrc.Codempresa = NfEntri.Codempresa\n"
                    + "                                                                            And Nfentrc.Codfornec = NFEntri.Codfornec\n"
                    + "                                                                            And NfEntrc.NumeroNf = NfEntri.NumeroNF))     ),\n"
                    + "                   CstCofins = (Select CofinsEnt_Cst From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod),\n"
                    + "                   AliqCofins = (Select AliqCofinsEnt From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod),\n"
                    + "                   VlrCofins = (((Select AliqCofinsEnt From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfEntri.Codprod) /100) *\n"
                    + "                                  ((NfEntri.Preconf * Nfentri.Quantidade) - NfEntri.Descvlr -  (Select (((Nfentri.quantidade)*(nfentri.preconf))/(NfEntrc.descvlrnf+Nfentrc.totalnf))*(NfEntrc.descvlrnf)\n"
                    + "                                                                                                  From NfEntrc\n"
                    + "                                                                                            Inner Join nfentri N on N.codempresa = nfentrc.codempresa And\n"
                    + "                                                                                                                    N.numeronf   = nfentrc.numeronf   And\n"
                    + "                                                                                                                    N.codfornec  = nfentrc.codfornec  And\n"
                    + "                                                                                                                    N.codempresa = nfentri.codempresa And\n"
                    + "                                                                                                                    N.numeronf   = nfentri.numeronf   And\n"
                    + "                                                                                                                    N.codfornec  = nfentri.codfornec  And\n"
                    + "                                                                                                                    N.CodProd    = NfEntri.CodProd))+\n"
                    + "                                                                                                    (((Select AliqCofins From Produtodetalhe\n"
                    + "                                                                                                        Where ProdutoDetalhe.Codprod = NfEntri.Codprod) /100) *\n"
                    + "                                                                                                      (Select NfEntrc.totalipi\n"
                    + "                                                                                                         From NfEntrc\n"
                    + "                                                                                                        Where NfEntrc.Codempresa = NfEntri.Codempresa\n"
                    + "                                                                                                          And Nfentrc.Codfornec = NFEntri.Codfornec\n"
                    + "                                                                                                          And NfEntrc.NumeroNf = NfEntri.NumeroNF))\n"
                    + "                                                                                                    )\n"
                    + "Where NfEntri.Numeronf In (Select NumeroNf From NfEntrc\n"
                    + "                           Where NfEntrc.Codempresa = NfEntri.Codempresa \n"
                    + "                             And Nfentrc.Codfornec = NFEntri.Codfornec \n"
                    + "                             And extract(month from nfentrc.dt_entrada)=" + mes + " and extract(year from nfentrc.dt_entrada)=" + ano + ");");
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("Update NfEntrc Set VlrPis = (Select Sum(vlrPis) From NfEntri\n"
                    + "                             Where NfEntri.CodEmpresa = NfEntrc.CodEmpresa\n"
                    + "                               And NfEntri.Numeronf = NfEntrc.Numeronf \n"
                    + "                               And NfEntri.Codfornec = NfEntrc.Codfornec),\n"
                    + "                   VlrCofins = (Select Sum(vlrCofins) From NfEntri\n"
                    + "                             Where NfEntri.CodEmpresa = NfEntrc.CodEmpresa\n"
                    + "                               And NfEntri.Numeronf = NfEntrc.Numeronf \n"
                    + "                               And NfEntri.Codfornec = NfEntrc.Codfornec)\n"
                    + "where extract(month from NfEntrc.Dt_Entrada)=" + mes + " and extract(year from NfEntrc.Dt_Entrada)=" + ano + ";");
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("Update NfSaidi Set CstPis = (Select Pis_Cst From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod),\n"
                    + "                   AliqPis = (Select AliqPis From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod),\n"
                    + "                   VlrPis = (((Select AliqPis From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod) /100) * NfSaidi.Totalrateado),\n"
                    + "                   CstCofins = (Select Cofins_Cst From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod),\n"
                    + "                   AliqCofins = (Select AliqCofins From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod),\n"
                    + "                   VlrCofins = (((Select AliqCofins From Produtodetalhe\n"
                    + "                             Where ProdutoDetalhe.Codprod = NfSaidi.Codprod) /100) * NfSaidi.Totalrateado)\n"
                    + "Where NfSaidi.Numnf In (Select NumNf From NfSaidc\n"
                    + "                           Where NfSaidc.Codempresa = NfSaidi.Codempresa\n"
                    + "                             And NfSaidc.Serie = NFSaidi.Serie\n"
                    + "                             And extract(month from NfSaidc.Dt_Emissao)=" + mes + " and extract(year from NfSaidc.Dt_Emissao)=" + ano + ");");
            ps.executeUpdate();
            ps.close();
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "Corrigido Movimentos do mês: " + mes + "/" + ano);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fundo = new javax.swing.JTabbedPane();
        pnl_fundo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabela1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txt_descricao = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cb_embranco = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        btn_conexao = new javax.swing.JButton();
        pnl_pis_cofins = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabela2 = new javax.swing.JTable();
        pnl_dados = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_areaProcesso = new javax.swing.JTextArea();
        pnl_opcoes = new javax.swing.JPanel();
        chx_estrutura = new javax.swing.JCheckBox();
        chx_pis_entrada = new javax.swing.JCheckBox();
        chx_pis_saida = new javax.swing.JCheckBox();
        chx_aliq_entrada = new javax.swing.JCheckBox();
        chx_itens_null = new javax.swing.JCheckBox();
        cbx_mes = new com.toedter.calendar.JMonthChooser();
        cbx_ano = new com.toedter.calendar.JYearChooser();
        chx_selecionaAll = new javax.swing.JCheckBox();
        btn_executar = new javax.swing.JButton();
        btn_apurar = new javax.swing.JButton();
        btn_atualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NCM App 1.1");
        setResizable(false);

        pnl_fundo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tabela1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Descrição", "NCM"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela1.setColumnSelectionAllowed(true);
        tabela1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabela1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tabela1);
        tabela1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela1.getColumnModel().getColumn(0).setMinWidth(100);
        tabela1.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabela1.getColumnModel().getColumn(0).setMaxWidth(100);
        tabela1.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabela1.getColumnModel().getColumn(2).setMinWidth(100);
        tabela1.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela1.getColumnModel().getColumn(2).setMaxWidth(100);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txt_descricao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_descricaoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_descricaoKeyReleased(evt);
            }
        });

        jLabel3.setText("Descrição:");

        cb_embranco.setText("Em Branco");

        jLabel4.setText("Tipo NCM:");

        btn_conexao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/conexão.gif"))); // NOI18N
        btn_conexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conexaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_descricao)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cb_embranco)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_conexao, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cb_embranco)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_conexao, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_fundoLayout = new javax.swing.GroupLayout(pnl_fundo);
        pnl_fundo.setLayout(pnl_fundoLayout);
        pnl_fundoLayout.setHorizontalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_fundoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_fundoLayout.setVerticalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        fundo.addTab("NCM", pnl_fundo);

        pnl_pis_cofins.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tabela2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Descrição", "PIS_SAIDA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela2.setColumnSelectionAllowed(true);
        tabela2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabela2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabela2);
        tabela2.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela2.getColumnModel().getColumn(0).setMinWidth(100);
        tabela2.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabela2.getColumnModel().getColumn(0).setMaxWidth(100);
        tabela2.getColumnModel().getColumn(2).setMinWidth(100);
        tabela2.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela2.getColumnModel().getColumn(2).setMaxWidth(100);

        pnl_dados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txt_areaProcesso.setEditable(false);
        txt_areaProcesso.setColumns(20);
        txt_areaProcesso.setRows(5);
        txt_areaProcesso.setTabSize(22);
        txt_areaProcesso.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane3.setViewportView(txt_areaProcesso);

        pnl_opcoes.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        chx_estrutura.setText("Estrutura");
        chx_estrutura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_estruturaActionPerformed(evt);
            }
        });

        chx_pis_entrada.setText("Pis/Cofins Entrada");
        chx_pis_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_pis_entradaActionPerformed(evt);
            }
        });

        chx_pis_saida.setText("Pis/Cofins Saida");

        chx_aliq_entrada.setText("Aliquotas");
        chx_aliq_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_aliq_entradaActionPerformed(evt);
            }
        });

        chx_itens_null.setText("Itens Null");

        chx_selecionaAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chx_selecionaAllMousePressed(evt);
            }
        });
        chx_selecionaAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_selecionaAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_opcoesLayout = new javax.swing.GroupLayout(pnl_opcoes);
        pnl_opcoes.setLayout(pnl_opcoesLayout);
        pnl_opcoesLayout.setHorizontalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chx_pis_saida)
                    .addComponent(chx_estrutura)
                    .addComponent(chx_selecionaAll))
                .addGap(30, 30, 30)
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(chx_aliq_entrada)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(chx_pis_entrada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(chx_itens_null)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                        .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_opcoesLayout.setVerticalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chx_itens_null)
                            .addComponent(chx_selecionaAll))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chx_aliq_entrada)
                            .addComponent(chx_estrutura))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chx_pis_saida)
                            .addComponent(chx_pis_entrada)))
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_executar.setText("Executar");
        btn_executar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_executarActionPerformed(evt);
            }
        });

        btn_apurar.setText("Apurar");
        btn_apurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_apurarActionPerformed(evt);
            }
        });

        btn_atualizar.setText("Atualizar");
        btn_atualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_atualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_dadosLayout = new javax.swing.GroupLayout(pnl_dados);
        pnl_dados.setLayout(pnl_dadosLayout);
        pnl_dadosLayout.setHorizontalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_dadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_atualizar)
                    .addComponent(pnl_opcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_dadosLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btn_apurar, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_executar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_dadosLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3)))
                .addGap(14, 14, 14))
        );
        pnl_dadosLayout.setVerticalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnl_opcoes, javax.swing.GroupLayout.PREFERRED_SIZE, 93, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_executar)
                    .addComponent(btn_apurar)
                    .addComponent(btn_atualizar))
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout pnl_pis_cofinsLayout = new javax.swing.GroupLayout(pnl_pis_cofins);
        pnl_pis_cofins.setLayout(pnl_pis_cofinsLayout);
        pnl_pis_cofinsLayout.setHorizontalGroup(
            pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_pis_cofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_dados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        pnl_pis_cofinsLayout.setVerticalGroup(
            pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_pis_cofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_dados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        fundo.addTab("PIS/COFINS", pnl_pis_cofins);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fundo)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fundo)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tabela1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabela1KeyPressed
        enter(evt);
        ctrl_C(evt);
        ctrl_V(evt);
    }//GEN-LAST:event_tabela1KeyPressed

    private void txt_descricaoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_descricaoKeyPressed
    }//GEN-LAST:event_txt_descricaoKeyPressed

    private void txt_descricaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_descricaoKeyReleased
        try {
            descricao = txt_descricao.getText().toUpperCase();
            descricao = descricao.replace(" ", "%");
            listaProdutosbyDescricao();
        } catch (Exception ex) {
            Logger.getLogger(Frm_Principal.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txt_descricaoKeyReleased

    private void btn_conexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conexaoActionPerformed
        Frm_Conexao c = new Frm_Conexao();
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_conexaoActionPerformed

    private void tabela2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabela2KeyPressed
        enterPis(evt);
    }//GEN-LAST:event_tabela2KeyPressed

    private void btn_executarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_executarActionPerformed
        try {
            txt_areaProcesso.setText("");
            corrigeEstrutura();
            corrigeItensNulos();
            corrigePisEntrada();
            corrigePisSaida();
            corrigeAliquotas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }//GEN-LAST:event_btn_executarActionPerformed

    private void chx_pis_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_pis_entradaActionPerformed
    }//GEN-LAST:event_chx_pis_entradaActionPerformed

    private void chx_aliq_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_aliq_entradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chx_aliq_entradaActionPerformed

    private void btn_apurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_apurarActionPerformed
        try {
            txt_areaProcesso.setText("");
            apurar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_apurarActionPerformed

    private void btn_atualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_atualizarActionPerformed
        try {
            limpaTabela2();
            listaProdutosPis();
            JOptionPane.showMessageDialog(null, "Tabela Atualizada Com Sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }//GEN-LAST:event_btn_atualizarActionPerformed

    private void chx_estruturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_estruturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chx_estruturaActionPerformed

    private void chx_selecionaAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_selecionaAllActionPerformed
        
    }//GEN-LAST:event_chx_selecionaAllActionPerformed

    private void chx_selecionaAllMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chx_selecionaAllMousePressed
        if(chx_selecionaAll.getSelectedObjects()!=null){
            chx_aliq_entrada.setSelected(false);
            chx_estrutura.setSelected(false);
            chx_itens_null.setSelected(false);
            chx_pis_entrada.setSelected(false);
            chx_pis_saida.setSelected(false);
        }else{
            chx_aliq_entrada.setSelected(true);
            chx_estrutura.setSelected(true);
            chx_itens_null.setSelected(true);
            chx_pis_entrada.setSelected(true);
            chx_pis_saida.setSelected(true);
        }
    }//GEN-LAST:event_chx_selecionaAllMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;


                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frm_Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frm_Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frm_Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frm_Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frm_Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_apurar;
    private javax.swing.JButton btn_atualizar;
    private javax.swing.JButton btn_conexao;
    private javax.swing.JButton btn_executar;
    private javax.swing.JCheckBox cb_embranco;
    private com.toedter.calendar.JYearChooser cbx_ano;
    private com.toedter.calendar.JMonthChooser cbx_mes;
    private javax.swing.JCheckBox chx_aliq_entrada;
    private javax.swing.JCheckBox chx_estrutura;
    private javax.swing.JCheckBox chx_itens_null;
    private javax.swing.JCheckBox chx_pis_entrada;
    private javax.swing.JCheckBox chx_pis_saida;
    private javax.swing.JCheckBox chx_selecionaAll;
    private javax.swing.JTabbedPane fundo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnl_dados;
    private javax.swing.JPanel pnl_fundo;
    private javax.swing.JPanel pnl_opcoes;
    private javax.swing.JPanel pnl_pis_cofins;
    private javax.swing.JTable tabela1;
    private javax.swing.JTable tabela2;
    private javax.swing.JTextArea txt_areaProcesso;
    private javax.swing.JTextField txt_descricao;
    // End of variables declaration//GEN-END:variables
}
