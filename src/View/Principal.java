/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;

/**
 *
 * @author Tadeu
 */
public class Principal extends javax.swing.JFrame {

    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static String codclasfis = null;
    static String codprod = null;
    static String listaProdutos;
    static String listaProdutosbyDescricao;
    static String descricao = null;
    static String diretorio = null;
    static String ip = null;
    static int click = 0;

    public Principal() {
        initComponents();
        cb_embranco.setSelected(true);
        try {
            conecta();
            listaProdutosPis();
            listaProdutosbyDescricao();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void conecta() throws Exception {
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
        File file = new File("C:/NCM-app/src/Ctrl/config.txt");
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);

        String linha = br.readLine();
        ip = linha;
        String linha2 = br.readLine();
        diretorio = linha2;
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
                Logger.getLogger(Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void ctrl_V(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_V) {
            try {
                tabela1.setValueAt(btn_conexao.getText(), tabela1.getSelectedRow(), 2);
            } catch (Exception ex) {
                Logger.getLogger(Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void ctrl_C(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            try {
                btn_conexao.setText(tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString());
            } catch (Exception ex) {
                Logger.getLogger(Principal.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void validaEmBranco() throws Exception {
        if (cb_embranco.getSelectedObjects() != null) {
            listaProdutos = "SELECT * FROM PRODUTO WHERE CODCLASFIS ='' where ativo='S' and prodbloqueado='N' order by descricao";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO WHERE DESCRICAO LIKE '%" + descricao + "%' AND CODCLASFIS ='' and ativo='S' and prodbloqueado='N' order by descricao";
        } else {
            listaProdutos = "SELECT * FROM PRODUTO AS P INNER JOIN CLASFISC C ON P.CODCLASFIS=C.CODCLASFIS WHERE C.CODCLASFIS <>'' and p.ativo='S' and p.prodbloqueado='N' order by p.descricao";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO P INNER JOIN CLASFISC AS C ON P.CODCLASFIS=C.CODCLASFIS WHERE P.DESCRICAO LIKE '%" + descricao + "%'  AND C.CODCLASFIS <>'' and p.ativo='S' and p.prodbloqueado='N' order by p.descricao";
        }
    }

    public void listaProdutosPis() throws Exception {
        Statement st;
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM PRODUTO P INNER JOIN PRODUTODETALHE D ON P.CODPROD=D.CODPROD WHERE D.PIS_CST='' ORDER BY D.CODPROD");
        DefaultTableModel model = (DefaultTableModel) tabela2.getModel();
        while (rs.next()) {
            String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO"), rs.getString("PIS_CST")};
            model.addRow(linha);
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
                Logger.getLogger(Principal.class
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

    public void corrigeEstrutura() throws Exception {

        int mes = cbx_mes.getMonth() + 1;
        int ano = cbx_ano.getYear();
        if (chx_estrutura.getSelectedObjects() != null) {
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "Corrigindo estrutura...\n");
            try {
                Statement st;
                st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from pedidoc inner join pedidoi on "
                        + "pedidoc.codempresa = pedidoi.codempresa and pedidoc.tipopedido ="
                        + " pedidoi.tipopedido and pedidoc.codcliente = pedidoi.codcliente and"
                        + " pedidoc.codpedido = pedidoi.codpedido inner join produto on pedidoi.codprod"
                        + " = produto.codprod left join produtodetalhe on pedidoi.codprod = produtodetalhe.codprod"
                        + " where extract(month from datapedido) = " + mes + " and extract(year from datapedido)"
                        + " = " + ano + " and (pedidoc.tipopedido = '56' or pedidoc.tipopedido = '51')");
                int contador = 0;
                while (rs.next()) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO PRODUTODETALHE (CODPROD) VALUES ('" + rs.getString("CODPROD") + "') ");
                        ps.executeUpdate();
                        ps.close();
                        contador++;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }

                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Quantidade de Alterações: " + contador + "\n");
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Estrutura corrigida!\n");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro...");
            }

        }
    }

    public void corrigePisEntrada() throws Exception{
        
    }
    public void corrigePisSaida() throws Exception{
        
    }
    public void corrigeAliqEntrada() throws Exception{
        
    }
    public void corrigeAliqSaida() throws Exception{
        
    }
    public void corrigeItensNulos() throws Exception{
        
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
        chx_aliq_saida = new javax.swing.JCheckBox();
        chx_itens_null = new javax.swing.JCheckBox();
        cbx_mes = new com.toedter.calendar.JMonthChooser();
        cbx_ano = new com.toedter.calendar.JYearChooser();
        btn_executar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_fundoLayout.setVerticalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
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

        txt_areaProcesso.setColumns(20);
        txt_areaProcesso.setRows(5);
        txt_areaProcesso.setTabSize(22);
        txt_areaProcesso.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane3.setViewportView(txt_areaProcesso);

        pnl_opcoes.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        chx_estrutura.setText("Estrutura");

        chx_pis_entrada.setText("Pis/Cofins Entrada");
        chx_pis_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_pis_entradaActionPerformed(evt);
            }
        });

        chx_pis_saida.setText("Pis/Cofins Saida");

        chx_aliq_entrada.setText("Aliquotas Entrada");

        chx_aliq_saida.setText("Aliquotas Saida");

        chx_itens_null.setText("Itens Null");

        javax.swing.GroupLayout pnl_opcoesLayout = new javax.swing.GroupLayout(pnl_opcoes);
        pnl_opcoes.setLayout(pnl_opcoesLayout);
        pnl_opcoesLayout.setHorizontalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chx_pis_entrada)
                    .addComponent(chx_pis_saida)
                    .addComponent(chx_estrutura))
                .addGap(18, 18, 18)
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chx_aliq_saida)
                            .addComponent(chx_aliq_entrada))
                        .addGap(0, 280, Short.MAX_VALUE))
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(chx_itens_null)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_opcoesLayout.setVerticalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(chx_estrutura)
                                .addComponent(chx_itens_null)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chx_pis_entrada)
                            .addComponent(chx_aliq_entrada)))
                    .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chx_pis_saida)
                    .addComponent(chx_aliq_saida))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        btn_executar.setText("Executar");
        btn_executar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_executarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_dadosLayout = new javax.swing.GroupLayout(pnl_dados);
        pnl_dados.setLayout(pnl_dadosLayout);
        pnl_dadosLayout.setHorizontalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_dadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_opcoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_executar)
                .addContainerGap())
        );
        pnl_dadosLayout.setVerticalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnl_opcoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_executar)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnl_pis_cofinsLayout = new javax.swing.GroupLayout(pnl_pis_cofins);
        pnl_pis_cofins.setLayout(pnl_pis_cofinsLayout);
        pnl_pis_cofinsLayout.setHorizontalGroup(
            pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_pis_cofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_dados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_pis_cofinsLayout.setVerticalGroup(
            pnl_pis_cofinsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_pis_cofinsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_dados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
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
            Logger.getLogger(Principal.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txt_descricaoKeyReleased

    private void btn_conexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conexaoActionPerformed
        Conexao c = new Conexao();
        c.setVisible(true);
    }//GEN-LAST:event_btn_conexaoActionPerformed

    private void tabela2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabela2KeyPressed
        enterPis(evt);
    }//GEN-LAST:event_tabela2KeyPressed

    private void btn_executarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_executarActionPerformed
        try {
            corrigeEstrutura();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }//GEN-LAST:event_btn_executarActionPerformed

    private void chx_pis_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_pis_entradaActionPerformed
        
    }//GEN-LAST:event_chx_pis_entradaActionPerformed

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
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_conexao;
    private javax.swing.JButton btn_executar;
    private javax.swing.JCheckBox cb_embranco;
    private com.toedter.calendar.JYearChooser cbx_ano;
    private com.toedter.calendar.JMonthChooser cbx_mes;
    private javax.swing.JCheckBox chx_aliq_entrada;
    private javax.swing.JCheckBox chx_aliq_saida;
    private javax.swing.JCheckBox chx_estrutura;
    private javax.swing.JCheckBox chx_itens_null;
    private javax.swing.JCheckBox chx_pis_entrada;
    private javax.swing.JCheckBox chx_pis_saida;
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
