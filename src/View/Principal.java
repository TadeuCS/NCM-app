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
    static String diretorio =null;
    static String ip =null;

    public Principal() {
        initComponents();
        cb_embranco.setSelected(true);
        try {
            conecta();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void conecta() throws Exception {
        try {
            leArquivo();
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection(
                    "jdbc:firebirdsql://"+ip+":3050/"+diretorio,
                    "SYSDBA",
                    "masterkey");
            st = con.createStatement();
            JOptionPane.showMessageDialog(null, "Conexão Estabelecida!");
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
        ip=linha;
        String linha2 = br.readLine();
        diretorio=linha2;
    }
    
    public void listaProdutos() throws Exception {
        validaEmBranco();
        Statement st;
        st = con.createStatement();
        ResultSet rs = st.executeQuery(listaProdutos);
        DefaultTableModel model = (DefaultTableModel) tabela1.getModel();
        while (rs.next()) {
            if (cb_embranco.getSelectedObjects()!= null) {
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
            if (cb_embranco.getSelectedObjects()!=null) {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO")};
                model.addRow(linha);
            }else{
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                validaNCM(tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString());
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
            listaProdutos = "SELECT * FROM PRODUTO WHERE CODCLASFIS ='' ORDER BY DESCRICAO";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO WHERE DESCRICAO LIKE '%" + descricao + "%' AND CODCLASFIS ='' ORDER BY DESCRICAO";
        } else {
            listaProdutos = "SELECT * FROM PRODUTO AS P INNER JOIN CLASFISC C ON P.CODCLASFIS=C.CODCLASFIS WHERE C.CODCLASFIS <>'' ORDER BY P.DESCRICAO";
            listaProdutosbyDescricao = "SELECT * FROM PRODUTO P INNER JOIN CLASFISC AS C ON P.CODCLASFIS=C.CODCLASFIS WHERE P.DESCRICAO LIKE '%" + descricao + "%'  AND C.CODCLASFIS <>'' ORDER BY P.DESCRICAO";
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_fundo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabela1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txt_descricao = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cb_embranco = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        btn_conexao = new javax.swing.JButton();

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
        tabela1.getColumnModel().getColumn(1).setMinWidth(400);
        tabela1.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabela1.getColumnModel().getColumn(1).setMaxWidth(400);

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
                .addGap(11, 11, 11)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_fundoLayout.setVerticalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_fundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_fundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        Conexao c= new Conexao();
        c.setVisible(true);
    }//GEN-LAST:event_btn_conexaoActionPerformed

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
    private javax.swing.JCheckBox cb_embranco;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnl_fundo;
    private javax.swing.JTable tabela1;
    private javax.swing.JTextField txt_descricao;
    // End of variables declaration//GEN-END:variables
}
