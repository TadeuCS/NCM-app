/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Tadeu
 */
public class Frm_Conexao extends javax.swing.JFrame {

    static Frm_Principal p = new Frm_Principal();
    static String diretorio = "";
    static Statement st;
    static Connection con;
    PrintWriter pw;

    public Frm_Conexao() {
        initComponents();
        try {
            leArquivo();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void buscaDiretorio() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) {
        } else {
            String file = fileChooser.getSelectedFile().getPath();
            txt_diretorio.setText(file);
        }
    }

    public void alteraDiretorio() {
        diretorio = "";
        if (cbx_tipo.getSelectedIndex() == 0) {
            diretorio += "localhost:3050/";
        } else {
            diretorio += txt_ip.getText() + ":3050/";
        }
        if (txt_diretorio.getText().compareTo("") != 0) {
            diretorio += txt_diretorio.getText().replace("\\", "/");
            txt_diretorio.setText(txt_diretorio.getText().replace("\\", "/"));
        }
    }

    public void testaConexão() {
        if ((cbx_tipo.getSelectedIndex() != 0) && (txt_ip.getText().compareTo("") == 0)) {
            JOptionPane.showMessageDialog(null, "IP é Obrigatorio se Tipo de conexão é REMOTO");
            txt_ip.requestFocus();
        } else {
            if (txt_diretorio.getText().compareTo("") == 0) {
                JOptionPane.showMessageDialog(null, "Campo Diretorio é Obrigatorio");
                txt_diretorio.requestFocus();
            } else {
                if (txt_user.getText().compareTo("") == 0) {
                    JOptionPane.showMessageDialog(null, "Campo User é Obrigatorio");
                    txt_user.requestFocus();
                } else {
                    if (txt_password.getText().compareTo("") == 0) {
                        JOptionPane.showMessageDialog(null, "Campo Password é Obrigatorio");
                        txt_password.requestFocus();
                    } else {
                        try {
                            alteraDiretorio();
                            Class.forName("org.firebirdsql.jdbc.FBDriver");
                            con = DriverManager.getConnection(
                                    "jdbc:firebirdsql://" + diretorio,
                                    txt_user.getText(),
                                    txt_password.getText());
                            st = con.createStatement();
                            status.setText("Conexão Bem Sucedida!");
                        } catch (ClassNotFoundException ex)//caso o driver não seja localizado  
                        {
                            JOptionPane.showMessageDialog(null, "Driver não encontrado!");
                        } catch (SQLException ex)//caso a conexão não possa se realizada  
                        {
                            status.setText("Sem Conexão!");
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void validaTipo() {
        if (cbx_tipo.getSelectedIndex() == 0) {
            txt_ip.setEditable(false);
            txt_ip.setText("");
            txt_diretorio.requestFocus();
        }
        if (cbx_tipo.getSelectedIndex() != 0) {
            txt_ip.setEditable(true);
            txt_ip.requestFocus();
        }
    }

    public void grava() {
        try {
            pw = new PrintWriter(new FileWriter("C:/NCM-app/src/Controller/config.txt", false));
            if (cbx_tipo.getSelectedIndex() != 0) {
                pw.println(txt_ip.getText());
            } else {
                pw.println("localhost");
            }
            if (txt_diretorio.getText().compareTo("") != 0) {
                pw.println(txt_diretorio.getText());
            }
            JOptionPane.showMessageDialog(null, "Configurações salvas com Sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "erro ao gravar arquivo! " + e.getMessage());
        }
        pw.close();
        try {
            p.setVisible(false);
            p.start();
            p.setVisible(true);
            setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

//Mesma coisa, salva a String s no arquivo.   
//Mas lembre, se você for salvar várias linhas, é bom adcionar bw.newLine() depois de salvar cada linha  
    }

    public void leArquivo() throws IOException {
        File file = new File("C:/NCM-app/src/Controller/config.txt");
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frm_Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);

        String linha = br.readLine();
        txt_ip.setText(linha);
        if (txt_ip.getText().compareTo("localhost") == 0) {
            cbx_tipo.setSelectedIndex(0);
            txt_ip.setText("");
        } else {
            cbx_tipo.setSelectedIndex(1);
        }
        String linha2 = br.readLine();
        txt_diretorio.setText(linha2);
        txt_user.setText("SYSDBA");
        txt_password.setText("masterkey");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_fundo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbx_tipo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txt_ip = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_diretorio = new javax.swing.JTextField();
        txt_buscar = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        txt_user = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btn_testar = new javax.swing.JButton();
        status = new javax.swing.JLabel();
        txt_password = new javax.swing.JPasswordField();
        btn_gravar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuração de Conexão");

        pnl_fundo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Tipo:");

        cbx_tipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Local", "Remoto" }));
        cbx_tipo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbx_tipoFocusLost(evt);
            }
        });

        jLabel2.setText("IP:");

        txt_ip.setEditable(false);

        jLabel3.setText("Diretorio:");

        txt_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/buscar.gif"))); // NOI18N
        txt_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_buscarActionPerformed(evt);
            }
        });

        jLabel4.setText("User:");

        jLabel5.setText("Password:");

        btn_testar.setText("Testar");
        btn_testar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_testarActionPerformed(evt);
            }
        });

        status.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        status.setForeground(new java.awt.Color(0, 51, 153));

        btn_gravar.setText("Gravar");
        btn_gravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_gravarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_fundoLayout = new javax.swing.GroupLayout(pnl_fundo);
        pnl_fundo.setLayout(pnl_fundoLayout);
        pnl_fundoLayout.setHorizontalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_fundoLayout.createSequentialGroup()
                        .addComponent(cbx_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_ip, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                    .addGroup(pnl_fundoLayout.createSequentialGroup()
                        .addComponent(txt_diretorio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_fundoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_user, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_fundoLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_fundoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_testar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_gravar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        pnl_fundoLayout.setVerticalGroup(
            pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_fundoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbx_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txt_ip, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_diretorio)
                    .addComponent(txt_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_user, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_testar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_gravar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_fundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_fundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_buscarActionPerformed
        buscaDiretorio();
    }//GEN-LAST:event_txt_buscarActionPerformed

    private void btn_testarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_testarActionPerformed
        testaConexão();
    }//GEN-LAST:event_btn_testarActionPerformed

    private void cbx_tipoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbx_tipoFocusLost
        validaTipo();
    }//GEN-LAST:event_cbx_tipoFocusLost

    private void btn_gravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_gravarActionPerformed
        grava();
    }//GEN-LAST:event_btn_gravarActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;


                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frm_Conexao.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frm_Conexao.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frm_Conexao.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frm_Conexao.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frm_Conexao().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_gravar;
    private javax.swing.JButton btn_testar;
    private javax.swing.JComboBox cbx_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel pnl_fundo;
    private javax.swing.JLabel status;
    private javax.swing.JToggleButton txt_buscar;
    private javax.swing.JTextField txt_diretorio;
    private javax.swing.JTextField txt_ip;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_user;
    // End of variables declaration//GEN-END:variables
}
