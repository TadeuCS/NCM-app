/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Util.PropertiesManager;
import Util.TableConfig;
import Util.Validade;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.layout.Region;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Tadeu
 */
public class Frm_Principal extends javax.swing.JFrame {

    static Statement st = null;
    static ResultSet rs = null;
    static String codclasfis = null;
    static String codprod = null;
    static int click = 0;
    static String ncm = null;
    static String pis = null;
    static String natReceita = null;
    int mes = 0;
    int ano = 0;
    PropertiesManager props;
    DefaultTableModel model;
    String bloqueado = null;
    //quando bloqueado='N' retorno: 8d9c307cb7f3c4a32822a51922d1ceaa
    //quando bloqueado ='S' retorno: 5dbc98dcc983a70728bd082d1a47546e

    public Frm_Principal(Statement sessao) {
        initComponents();
        loading.setVisible(false);
        this.st = sessao;
        verificaValidade();
    }

    public void verificaValidade() {
        Validade validade = new Validade();
        Date data = new Date();
        props = new PropertiesManager();
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
        bloqueado = props.ler("bloqueado");
        if ((bloqueado.equals("8d9c307cb7f3c4a32822a51922d1ceaa") == true) && (validade.validaSistema(data) == true)) {
            txt_validade.setText(props.ler("validade"));
            setVisible(true);
            Thread acao;
            acao = new Thread(new Runnable() {
                @Override
                public void run() {
                    start(st);
                }
            }
            );
            acao.start();
        } else {
            JOptionPane.showMessageDialog(null, "Sua licença expirou, entre em contato com o desenvolvedor e requisite um código de liberação!");
            String resposta = JOptionPane.showInputDialog(null, "Informe o código de liberação: ");
            if (Integer.parseInt(resposta) == validade.getDia(data) * validade.getMes(data) * validade.getAno(data) * 5) {
                props.altera("bloqueado", "8d9c307cb7f3c4a32822a51922d1ceaa");
                props.altera("validade", validade.addDayOfDate(data, 30) + "");
                JOptionPane.showMessageDialog(null, "Licença prorrogada com sucesso!\n Validade: " + validade.addDayOfDate(data, 30));
                verificaValidade();
            } else {
                JOptionPane.showMessageDialog(null, "Código de liberação inválido!");
                props.altera("bloqueado", "5dbc98dcc983a70728bd082d1a47546e");
                dispose();
            }
        }
    }

    public void buscaDadosbyEmpresa(Statement st) {
        try {
            rs = st.executeQuery("SELECT * FROM FILIAIS");
            while (rs.next()) {
                this.setTitle(this.getTitle() + rs.getString("NOMEEMPRESA") + "\t\t");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar o nome da Empresa! \n" + e.getMessage());
        }

    }

    public void start(Statement st) {
        try {
            buscaDadosbyEmpresa(st);
            marcaOpcoes();
            retiraNullos(st);
            barra.setMaximum(contaQtdeDeItens());
            listaProdutosNCM(st);
            listaProdutosPis(st);
            listaProdutoByNatureza(st);
            barra.setVisible(false);
        } catch (Exception e) {
            dispose();
        }
    }

    public void filtrar(JTextField campo, JTable tabela, JLabel qtde) {
        TableRowSorter sorter = new TableRowSorter(tabela.getModel());
        tabela.setRowSorter(sorter);
        try {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + campo.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Valor Não Encontrado!!!", "AVISO - Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            qtde.setText(tabela.getRowCount() + "");
        }

    }

    public void getCountRow(JTable tabela, JLabel campo) {
        campo.setText(tabela.getRowCount() + "");
    }

    public DefaultTableModel getModelByTabela(JTable tabela) {
        model = (DefaultTableModel) tabela.getModel();
        return model;
    }

    public void limpaTabela(DefaultTableModel model) {
        try {
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao Limpar a tabela. \n" + e.getMessage());
        }
    }

    public void enabledsOn() throws Exception {
        fundo.setEnabled(false);
        pnl_aba1.setEnabled(false);
        txt_filtroNCM.setEnabled(false);
        tabela1.setEnabled(false);
    }

    public void enabledsOff(int permissao) {
        if (permissao == 1) {
            fundo.setEnabled(true);
            pnl_aba1.setEnabled(true);
            txt_filtroNCM.setEnabled(true);
            tabela1.setEnabled(true);
        }
        if (permissao == 2) {
            pnl_aba2.setEnabled(false);
            fundo.setEnabled(true);
            pnl_aba1.setEnabled(true);
            txt_filtroNCM.setEnabled(true);
            tabela1.setEnabled(true);
        }

    }

    public void marcaOpcoes() {
        chx_selecionaAll.setSelected(true);
        chx_ativo.setSelected(true);
        chx_bloqueado.setSelected(false);
        chx_aliq_entrada.setSelected(true);
        chx_estrutura.setSelected(true);
        chx_itens_null.setSelected(true);
        chx_pis_entrada.setSelected(true);
        chx_pis_saida.setSelected(true);
        chx_apuracao.setSelected(true);
    }

    public String validaCheckbox(JCheckBox campo) {
        if (campo.getSelectedObjects() != null) {
            return "S";
        } else {
            return "N";
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fundo = new javax.swing.JTabbedPane();
        pnl_aba1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabela1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txt_filtroNCM = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chx_ativo = new javax.swing.JCheckBox();
        chx_bloqueado = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        btn_buscar = new javax.swing.JButton();
        btn_bloquear = new javax.swing.JButton();
        btn_desativar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        qtdeNCM = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_validade = new javax.swing.JTextField();
        barra = new javax.swing.JProgressBar();
        pnl_aba2 = new javax.swing.JPanel();
        pnl_dados = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_filtroPisCofins = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        qtdePisCofins = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabela2 = new javax.swing.JTable();
        pnl_aba3 = new javax.swing.JPanel();
        pnl_dados1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txt_filtroNatReceita = new javax.swing.JTextField();
        loading = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btn_executar = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabela3 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_areaProcesso = new javax.swing.JTextArea();
        pnl_opcoes = new javax.swing.JPanel();
        cbx_mes = new com.toedter.calendar.JMonthChooser();
        cbx_ano = new com.toedter.calendar.JYearChooser();
        jPanel2 = new javax.swing.JPanel();
        chx_selecionaAll = new javax.swing.JCheckBox();
        chx_estrutura = new javax.swing.JCheckBox();
        chx_itens_null = new javax.swing.JCheckBox();
        chx_pis_saida = new javax.swing.JCheckBox();
        chx_pis_entrada = new javax.swing.JCheckBox();
        chx_aliq_entrada = new javax.swing.JCheckBox();
        chx_apuracao = new javax.swing.JCheckBox();
        qtdeNatReceita = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NCM App 1.3");
        setResizable(false);

        pnl_aba1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tabela1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESCRIÇÃO", "NCM"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela1.getTableHeader().setReorderingAllowed(false);
        tabela1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabela1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tabela1);
        if (tabela1.getColumnModel().getColumnCount() > 0) {
            tabela1.getColumnModel().getColumn(0).setMinWidth(100);
            tabela1.getColumnModel().getColumn(0).setPreferredWidth(100);
            tabela1.getColumnModel().getColumn(0).setMaxWidth(100);
            tabela1.getColumnModel().getColumn(1).setPreferredWidth(400);
            tabela1.getColumnModel().getColumn(2).setMinWidth(100);
            tabela1.getColumnModel().getColumn(2).setPreferredWidth(100);
            tabela1.getColumnModel().getColumn(2).setMaxWidth(100);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txt_filtroNCM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_filtroNCMKeyReleased(evt);
            }
        });

        jLabel3.setText("Filtro:");

        chx_ativo.setText("Ativo");
        chx_ativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_ativoActionPerformed(evt);
            }
        });

        chx_bloqueado.setText("Bloqueado");
        chx_bloqueado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_bloqueadoActionPerformed(evt);
            }
        });

        jLabel6.setText("Tipo Produtos:");

        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/carregar.png"))); // NOI18N
        btn_buscar.setToolTipText("Buscar");
        btn_buscar.setBorder(null);
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        btn_bloquear.setText("Bloquear");
        btn_bloquear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bloquearActionPerformed(evt);
            }
        });

        btn_desativar.setText("Desativar");
        btn_desativar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_desativarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chx_ativo)
                        .addGap(18, 18, 18)
                        .addComponent(chx_bloqueado)
                        .addGap(18, 18, 18)
                        .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 286, Short.MAX_VALUE)
                        .addComponent(btn_desativar)
                        .addGap(18, 18, 18)
                        .addComponent(btn_bloquear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_filtroNCM)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_filtroNCM, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chx_ativo)
                        .addComponent(chx_bloqueado)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_bloquear)
                        .addComponent(btn_desativar))
                    .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText("Linhas:");

        qtdeNCM.setForeground(new java.awt.Color(153, 0, 0));

        jLabel9.setText("Validade:");

        txt_validade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_validade.setEnabled(false);

        barra.setForeground(new java.awt.Color(0, 153, 0));
        barra.setToolTipText("Produtos Carregados");
        barra.setStringPainted(true);

        javax.swing.GroupLayout pnl_aba1Layout = new javax.swing.GroupLayout(pnl_aba1);
        pnl_aba1.setLayout(pnl_aba1Layout);
        pnl_aba1Layout.setHorizontalGroup(
            pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba1Layout.createSequentialGroup()
                .addGroup(pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_aba1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)))
                    .addGroup(pnl_aba1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_validade, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(qtdeNCM, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_aba1Layout.setVerticalGroup(
            pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_aba1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txt_validade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_aba1Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(qtdeNCM, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        fundo.addTab("NCM", pnl_aba1);

        pnl_aba2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        pnl_dados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Filtro:");

        txt_filtroPisCofins.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_filtroPisCofinsKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnl_dadosLayout = new javax.swing.GroupLayout(pnl_dados);
        pnl_dados.setLayout(pnl_dadosLayout);
        pnl_dadosLayout.setHorizontalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_filtroPisCofins)
                .addContainerGap())
        );
        pnl_dadosLayout.setVerticalGroup(
            pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dadosLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(pnl_dadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_filtroPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        jLabel2.setText("Linhas:");

        qtdePisCofins.setForeground(new java.awt.Color(153, 0, 0));

        tabela2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESCRIÇÃO", "PIS_SAIDA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela2.getTableHeader().setReorderingAllowed(false);
        tabela2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabela2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabela2);
        if (tabela2.getColumnModel().getColumnCount() > 0) {
            tabela2.getColumnModel().getColumn(0).setMinWidth(80);
            tabela2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tabela2.getColumnModel().getColumn(0).setMaxWidth(80);
            tabela2.getColumnModel().getColumn(2).setMinWidth(80);
            tabela2.getColumnModel().getColumn(2).setPreferredWidth(80);
            tabela2.getColumnModel().getColumn(2).setMaxWidth(80);
        }

        javax.swing.GroupLayout pnl_aba2Layout = new javax.swing.GroupLayout(pnl_aba2);
        pnl_aba2.setLayout(pnl_aba2Layout);
        pnl_aba2Layout.setHorizontalGroup(
            pnl_aba2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_aba2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_dados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_aba2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(qtdePisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_aba2Layout.setVerticalGroup(
            pnl_aba2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_dados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_aba2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(qtdePisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        fundo.addTab("PIS/COFINS", pnl_aba2);

        pnl_dados1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Filtro:");

        txt_filtroNatReceita.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_filtroNatReceitaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnl_dados1Layout = new javax.swing.GroupLayout(pnl_dados1);
        pnl_dados1.setLayout(pnl_dados1Layout);
        pnl_dados1Layout.setHorizontalGroup(
            pnl_dados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dados1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_filtroNatReceita)
                .addContainerGap())
        );
        pnl_dados1Layout.setVerticalGroup(
            pnl_dados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_dados1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_filtroNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loading.setForeground(new java.awt.Color(153, 0, 0));
        loading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/loading.gif"))); // NOI18N

        jLabel7.setText("Linhas:");

        btn_executar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/executar.png"))); // NOI18N
        btn_executar.setText("Executar");
        btn_executar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_executarActionPerformed(evt);
            }
        });

        tabela3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "NCM", "NAT. RECEITA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela3.getTableHeader().setReorderingAllowed(false);
        tabela3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tabela3FocusGained(evt);
            }
        });
        tabela3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabela3MousePressed(evt);
            }
        });
        tabela3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabela3KeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(tabela3);
        if (tabela3.getColumnModel().getColumnCount() > 0) {
            tabela3.getColumnModel().getColumn(0).setMinWidth(80);
            tabela3.getColumnModel().getColumn(0).setPreferredWidth(80);
            tabela3.getColumnModel().getColumn(0).setMaxWidth(80);
            tabela3.getColumnModel().getColumn(2).setMinWidth(80);
            tabela3.getColumnModel().getColumn(2).setPreferredWidth(80);
            tabela3.getColumnModel().getColumn(2).setMaxWidth(80);
        }

        txt_areaProcesso.setEditable(false);
        txt_areaProcesso.setColumns(20);
        txt_areaProcesso.setRows(5);
        txt_areaProcesso.setTabSize(22);
        txt_areaProcesso.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane3.setViewportView(txt_areaProcesso);

        pnl_opcoes.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chx_selecionaAll.setText("Selecionar Todos");
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

        chx_estrutura.setText("Estrutura");
        chx_estrutura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_estruturaActionPerformed(evt);
            }
        });

        chx_itens_null.setText("Itens Null");

        chx_pis_saida.setText("Pis/Cofins Saida");

        chx_pis_entrada.setText("Pis/Cofins Entrada");
        chx_pis_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chx_pis_entradaActionPerformed(evt);
            }
        });

        chx_aliq_entrada.setText("Aliquotas");

        chx_apuracao.setText("Apurar Entrada/Saida");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chx_itens_null)
                            .addComponent(chx_estrutura))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chx_pis_saida)
                            .addComponent(chx_pis_entrada))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chx_apuracao)
                            .addComponent(chx_aliq_entrada)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(chx_selecionaAll)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(chx_selecionaAll)
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chx_aliq_entrada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chx_apuracao))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chx_estrutura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chx_itens_null))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chx_pis_entrada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chx_pis_saida)))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_opcoesLayout = new javax.swing.GroupLayout(pnl_opcoes);
        pnl_opcoes.setLayout(pnl_opcoesLayout);
        pnl_opcoesLayout.setHorizontalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_opcoesLayout.createSequentialGroup()
                        .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_opcoesLayout.setVerticalGroup(
            pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_opcoesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_opcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbx_mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbx_ano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        qtdeNatReceita.setForeground(new java.awt.Color(153, 0, 0));

        javax.swing.GroupLayout pnl_aba3Layout = new javax.swing.GroupLayout(pnl_aba3);
        pnl_aba3.setLayout(pnl_aba3Layout);
        pnl_aba3Layout.setHorizontalGroup(
            pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_aba3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(qtdeNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(pnl_dados1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(pnl_opcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_aba3Layout.createSequentialGroup()
                        .addComponent(loading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_executar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_aba3Layout.setVerticalGroup(
            pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_aba3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_aba3Layout.createSequentialGroup()
                        .addComponent(pnl_dados1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_aba3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnl_opcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_aba3Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_executar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(loading))
                        .addContainerGap())
                    .addGroup(pnl_aba3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_aba3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(qtdeNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        btn_executar.getAccessibleContext().setAccessibleName("");

        fundo.addTab("Natureza da Receita", pnl_aba3);

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
        enter(evt, 1);
        ctrl_C(evt, 1);
        ctrl_V(evt, 1);
    }//GEN-LAST:event_tabela1KeyPressed

    private void txt_filtroNCMKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filtroNCMKeyReleased
        filtrar(txt_filtroNCM, tabela1, qtdeNCM);
    }//GEN-LAST:event_txt_filtroNCMKeyReleased

    private void tabela2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabela2KeyPressed
        enter(evt, 2);
        ctrl_C(evt, 2);
        ctrl_V(evt, 2);
    }//GEN-LAST:event_tabela2KeyPressed

    private void btn_executarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_executarActionPerformed
        Thread acao;
        acao = new Thread(new Runnable() {
            @Override
            public void run() {
                txt_areaProcesso.setText("");
                if (chx_estrutura.getSelectedObjects() != null || chx_itens_null.getSelectedObjects() != null
                        || chx_pis_saida.getSelectedObjects() != null || chx_pis_entrada.getSelectedObjects() != null
                        || chx_aliq_entrada.getSelectedObjects() != null || chx_apuracao.getSelectedObjects() != null) {
                    loading.setVisible(true);
                    corrigeEstrutura();
                    corrigeItensNulos();
                    corrigePisEntrada();
                    corrigePisSaida();
                    corrigeAliquotas();
                    apurar();
                    loading.setVisible(false);
                } else {
                    txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nSelecione Pelo menos uma opção!");
                }
            }
        }
        );
        acao.start();
    }//GEN-LAST:event_btn_executarActionPerformed

    private void chx_pis_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_pis_entradaActionPerformed
    }//GEN-LAST:event_chx_pis_entradaActionPerformed

    private void chx_estruturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_estruturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chx_estruturaActionPerformed

    private void chx_selecionaAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_selecionaAllActionPerformed

    }//GEN-LAST:event_chx_selecionaAllActionPerformed

    private void chx_selecionaAllMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chx_selecionaAllMousePressed
        if (chx_selecionaAll.getSelectedObjects() != null) {
            chx_aliq_entrada.setSelected(false);
            chx_estrutura.setSelected(false);
            chx_itens_null.setSelected(false);
            chx_pis_entrada.setSelected(false);
            chx_pis_saida.setSelected(false);
            chx_apuracao.setSelected(false);
        } else {
            chx_aliq_entrada.setSelected(true);
            chx_estrutura.setSelected(true);
            chx_itens_null.setSelected(true);
            chx_pis_entrada.setSelected(true);
            chx_pis_saida.setSelected(true);
            chx_apuracao.setSelected(true);
        }
    }//GEN-LAST:event_chx_selecionaAllMousePressed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        listaProdutosNCM(st);
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void btn_desativarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_desativarActionPerformed
        if (tabela1.getSelectedRowCount() == 1) {
            desativar(tabela1.getValueAt(tabela1.getSelectedRow(), 0).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Selecione apenas uma linha!");
        }
    }//GEN-LAST:event_btn_desativarActionPerformed

    private void btn_bloquearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bloquearActionPerformed
        if (tabela1.getSelectedRowCount() == 1) {
            bloquear(tabela1.getValueAt(tabela1.getSelectedRow(), 0).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Selecione apenas uma linha!");
        }
    }//GEN-LAST:event_btn_bloquearActionPerformed

    private void chx_ativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_ativoActionPerformed
        if (chx_ativo.getSelectedObjects() != null) {
            btn_desativar.setText("Desativar");
        } else {
            btn_desativar.setText("Ativar");
        }
    }//GEN-LAST:event_chx_ativoActionPerformed

    private void chx_bloqueadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chx_bloqueadoActionPerformed
        if (chx_bloqueado.getSelectedObjects() != null) {
            btn_bloquear.setText("Desbloquear");
        } else {
            btn_bloquear.setText("Bloquear");
        }
    }//GEN-LAST:event_chx_bloqueadoActionPerformed

    private void txt_filtroPisCofinsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filtroPisCofinsKeyReleased
        filtrar(txt_filtroPisCofins, tabela2, qtdePisCofins);
    }//GEN-LAST:event_txt_filtroPisCofinsKeyReleased

    private void tabela3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabela3KeyPressed
        enter(evt, 3);
    }//GEN-LAST:event_tabela3KeyPressed

    private void txt_filtroNatReceitaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filtroNatReceitaKeyReleased
        txt_filtroNatReceita.setText(txt_filtroNatReceita.getText().trim());
        filtrar(txt_filtroNatReceita, tabela3, loading);
    }//GEN-LAST:event_txt_filtroNatReceitaKeyReleased

    private void tabela3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tabela3FocusGained

    }//GEN-LAST:event_tabela3FocusGained

    private void tabela3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabela3MousePressed
//        if(tabela3.getSelectedColumnCount()==1){
//            tabela3.setColumnSelectionInterval(2, 2);
//        }
    }//GEN-LAST:event_tabela3MousePressed

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
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btn_bloquear;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_desativar;
    private javax.swing.JButton btn_executar;
    private com.toedter.calendar.JYearChooser cbx_ano;
    private com.toedter.calendar.JMonthChooser cbx_mes;
    private javax.swing.JCheckBox chx_aliq_entrada;
    private javax.swing.JCheckBox chx_apuracao;
    private javax.swing.JCheckBox chx_ativo;
    private javax.swing.JCheckBox chx_bloqueado;
    private javax.swing.JCheckBox chx_estrutura;
    private javax.swing.JCheckBox chx_itens_null;
    private javax.swing.JCheckBox chx_pis_entrada;
    private javax.swing.JCheckBox chx_pis_saida;
    private javax.swing.JCheckBox chx_selecionaAll;
    private javax.swing.JTabbedPane fundo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel loading;
    private javax.swing.JPanel pnl_aba1;
    private javax.swing.JPanel pnl_aba2;
    private javax.swing.JPanel pnl_aba3;
    private javax.swing.JPanel pnl_dados;
    private javax.swing.JPanel pnl_dados1;
    private javax.swing.JPanel pnl_opcoes;
    private javax.swing.JLabel qtdeNCM;
    private javax.swing.JLabel qtdeNatReceita;
    private javax.swing.JLabel qtdePisCofins;
    private javax.swing.JTable tabela1;
    private javax.swing.JTable tabela2;
    private javax.swing.JTable tabela3;
    private javax.swing.JTextArea txt_areaProcesso;
    private javax.swing.JTextField txt_filtroNCM;
    private javax.swing.JTextField txt_filtroNatReceita;
    private javax.swing.JTextField txt_filtroPisCofins;
    private javax.swing.JTextField txt_validade;
    // End of variables declaration//GEN-END:variables

    private void listaProdutosNCM(Statement st) {
        model = getModelByTabela(tabela1);
        limpaTabela(model);
        try {
            rs = st.executeQuery("SELECT P.CODPROD,P.DESCRICAO,C.CODIGONCM FROM PRODUTO P \n"
                    + "                    inner JOIN CLASFISC C ON P.CODCLASFIS=C.CODCLASFIS\n"
                    + "                     WHERE P.ATIVO = '" + validaCheckbox(chx_ativo) + "'\n"
                    + "                     and P.prodbloqueado = '" + validaCheckbox(chx_bloqueado) + "'\n"
                    + "                     order by p.descricao;");
            while (rs.next()) {
                String[] linha = new String[]{rs.getString("CODPROD"), rs.getString("DESCRICAO"), rs.getString("CODIGONCM")};
                model.addRow(linha);
                barra.setValue(barra.getValue() + 1);
            }
            getCountRow(tabela1, qtdeNCM);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar os produtos. \n" + e.getMessage());
        }
    }

    private void listaProdutosPis(Statement st) {
        model = getModelByTabela(tabela2);
        limpaTabela(model);
        ResultSet rs1;
        try {
            rs1 = st.executeQuery("SELECT p.codprod,p.descricao,d.pis_cst FROM PRODUTO P INNER JOIN PRODUTODETALHE D ON P.CODPROD=D.CODPROD ORDER BY p.descricao");
            while (rs1.next()) {
                String[] linha = new String[]{rs1.getString("CODPROD"), rs1.getString("DESCRICAO"), rs1.getString("PIS_CST")};
                model.addRow(linha);
                barra.setValue(barra.getValue() + 1);
            }
            getCountRow(tabela2, qtdePisCofins);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao Listar os produtos sem PIS/Cofins" + e.getMessage());
        }
    }

    private void listaProdutoByNatureza(Statement st) {
        try {
            model = (DefaultTableModel) tabela3.getModel();
            limpaTabela(model);
            rs = st.executeQuery("SELECT * FROM CLASFISC");
            while (rs.next()) {
                String[] linha = new String[]{rs.getString("CODCLASFIS"), rs.getString("CODIGONCM"), rs.getString("CODNATRECEITA")};
                model.addRow(linha);
                barra.setValue(barra.getValue() + 1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro.\n" + e.getMessage());
        } finally {
            getCountRow(tabela3, qtdeNatReceita);
        }
    }

    private void validaNatReceita(String codigo, String receita) {
        if (receita.length() == 3) {
            try {
                rs = st.executeQuery("SELECT * FROM NATRECEITA WHERE CODRECEITA= '" + receita + "'");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar código natureza da receita: " + receita + ".\n" + e.getMessage());
            }
            try {
                if (rs.next()) {
                    st.executeUpdate("UPDATE CLASFISC SET CODNATRECEITA = '" + receita + "' WHERE codclasfis ='" + codigo + "';");
                    JOptionPane.showMessageDialog(null, receita + " inserido com sucesso para o codigo " + codigo);
                } else {
                    insereNatReceita(receita);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar a NATUREZA DA RECEITA "
                        + receita + " no codigo " + codigo + ".\n" + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "NAT. RECEITA deve conter 3 caracteres!");
        }
    }

    private void validaNCM(String codigoNcm) {
        if (codigoNcm.length() == 8) {
            try {
                rs = st.executeQuery("SELECT * FROM CLASFISC WHERE CODIGONCM='" + codigoNcm + "'");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar NCM: " + codigoNcm);
            }
            try {
                if (rs.next()) {
                    codclasfis = rs.getString("CODCLASFIS");
                    codprod = tabela1.getValueAt(tabela1.getSelectedRow(), 0).toString();
                    st.executeUpdate("UPDATE PRODUTO SET CODCLASFIS = '" + codclasfis + "' WHERE CODPROD ='" + codprod + "'");
                    JOptionPane.showMessageDialog(null, "NCM: " + codigoNcm + " foi inserido no Produto: " + codprod);
                } else {
                    JOptionPane.showMessageDialog(null, "NCM: " + codigoNcm + " invalido!");
                    insereNCM(codigoNcm);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar o NCM "
                        + codigoNcm + " no produto " + codprod + ".\n" + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "NCM deve conter 8 caracteres!");
        }
    }

    private void insereNCM(String codigoNcm) {
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
                st.executeUpdate("INSERT INTO CLASFISC (CODCLASFIS, CODNBM, PISCONFRETIDO,CODIGONCM) VALUES "
                        + "('" + codclasfis + "', '" + codigoNcm + "', 1,'" + codigoNcm + "');");
                if (JOptionPane.showConfirmDialog(null, "NCM: " + codigoNcm + " Cadastrado com Sucesso! \n"
                        + "Deseja Ultiliza-lo no Produto: " + tabela1.getValueAt(tabela1.getSelectedRow(), 0), "", JOptionPane.YES_NO_OPTION) == 0) {
                    validaNCM(codigoNcm);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void enter(KeyEvent e, int tabela) {
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 0)) {
            click++;
        }
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (click == 1)) {
            try {
                if (tabela == 1) {
                    validaNCM(tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString());
                    click = 0;
                } else {
                    if (tabela == 2) {
                        if (tabela2.getSelectedRowCount() == 1) {
                            validaPis(tabela2.getValueAt(tabela2.getSelectedRow(), 2).toString(), tabela2.getValueAt(tabela2.getSelectedRow(), 0).toString());
                            click = 0;
                        }
                    } else {
                        if (tabela == 3) {
                            validaNatReceita(tabela3.getValueAt(tabela3.getSelectedRow(), 0).toString(),
                                    tabela3.getValueAt(tabela3.getSelectedRow(), 2).toString());
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void ctrl_V(KeyEvent e, int tabela) {
        if (e.getKeyCode() == KeyEvent.VK_V) {
            try {
                if (tabela == 1) {
                    tabela1.setValueAt(ncm, tabela1.getSelectedRow(), 2);
                } else {
                    if (tabela == 2) {
                        tabela2.setValueAt(pis, tabela2.getSelectedRow(), 2);
                    } else {
                        if (tabela == 3) {
                            tabela3.setValueAt(natReceita, tabela3.getSelectedRow(), 2);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void ctrl_C(KeyEvent e, int tabela) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            try {
                if (tabela == 1) {
                    ncm = tabela1.getValueAt(tabela1.getSelectedRow(), 2).toString();
                } else {
                    if (tabela == 2) {
                        pis = tabela2.getValueAt(tabela2.getSelectedRow(), 2).toString();
                    } else {
                        if (tabela == 3) {
                            natReceita = tabela3.getValueAt(tabela3.getSelectedRow(), 2).toString();
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void retiraNullos(Statement st) {
        try {
            st.executeUpdate("UPDATE PRODUTO SET CODCLASFIS='' WHERE CODCLASFIS IS NULL");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro. \n" + e.getMessage());
        }
    }

    private void validaPis(String pis, String codprod) {
        try {
            if (pis.compareTo("01") == 0) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='50',COFINSENT_CST='50',"
                        + "                                 ALIQPIS='1.65',ALIQCOFINS='7.60',"
                        + "                                 ALIQPISENT='1.65',ALIQCOFINSENT='7.60' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else if (pis.compareTo("04") == 0) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='70',COFINSENT_CST='70',"
                        + "                                 ALIQPIS='0.00',ALIQCOFINS='0.00',"
                        + "                                 ALIQPISENT='0.00',ALIQCOFINSENT='0.00' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else if (pis.compareTo("06") == 0) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='73',COFINSENT_CST='73',"
                        + "                                 ALIQPIS='0.00',ALIQCOFINS='0.00',"
                        + "                                 ALIQPISENT='0.00',ALIQCOFINSENT='0.00' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else if (pis.compareTo("49") == 0) {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST = '" + pis + "',COFINS_CST='" + pis + "',"
                        + "                                 PISENT_CST='98',COFINSENT_CST='98',"
                        + "                                 ALIQPIS='0.00',ALIQCOFINS='0.00',"
                        + "                                 ALIQPISENT='0.00',ALIQCOFINSENT='0.00' WHERE CODPROD ='" + codprod + "'");
                JOptionPane.showMessageDialog(null, "PIS INSERIDO COM SUCESSO!");
            } else {
                JOptionPane.showMessageDialog(null, "PIS DE SAIDA INVALIDO!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao validar PIS informado.\n " + e.getMessage());
        }
    }

    private void corrigeEstrutura() {
        if (chx_estrutura.getSelectedObjects() != null) {
            try {
                rs = st.executeQuery("Select * From produto\n"
                        + " Where produto.codprod Not In (Select produtodetalhe.codprod From produtodetalhe\n"
                        + "                                Where produtodetalhe.codprod = produto.codprod)");
                List<String> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add("INSERT INTO PRODUTODETALHE (CODPROD) VALUES ('" + rs.getString("CODPROD") + "');");
                }
                for (int i = 0; i < lista.size(); i++) {
                    st.executeUpdate(lista.get(i));
                }
                txt_areaProcesso.setText("Estrutura\n" + "Erros de estrutura: " + lista.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro de insersão.\n" + e.getMessage());
            }
        }
    }

    private void corrigePisEntrada() {
        if (chx_pis_entrada.getSelectedObjects() != null) {
            String quantidade = null;
            try {
                rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PISENT_CST <>"
                        + " COFINSENT_CST OR (PIS_CST='01' AND PISENT_CST<>'50')\n"
                        + "                        OR (PIS_CST='04' AND PISENT_CST<>'70')\n"
                        + "                        OR (PIS_CST='49' AND PISENT_CST<>'98')\n"
                        + "                        OR (PIS_CST='06' AND PISENT_CST<>'73')");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao retornar a quantidade de produtos da tabela PRODUTODETALHE"
                        + " que contem o CST_PIS diferente do CST_COFINS.\n" + e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nPis/Cofins de Entrada" + "\nRealizando Correções... ");
            try {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PISENT_CST='98' WHERE PIS_CST='49';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PISENT_CST='50' WHERE PIS_CST='01';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PISENT_CST='70' WHERE PIS_CST='04';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PISENT_CST='73' WHERE PIS_CST='06';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET COFINSENT_CST=PISENT_CST;");
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade + "\n");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao corrigir os produtos da tabela PRODUTODETALHE"
                        + " que contem o CST_PIS diferente do CST_COFINS.\n" + e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro nos Pis/Cofins de Entrada...\n");
            }
        }
    }

    private void corrigePisSaida() {
        if (chx_pis_saida.getSelectedObjects() != null) {
            String quantidade = null;
            try {
                rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PIS_CST <> COFINS_CST\n"
                        + "                         OR (PISENT_CST='50' AND PIS_CST<>'01')\n"
                        + "                         OR (PISENT_CST='70' AND PIS_CST<>'04')\n"
                        + "                         OR (PISENT_CST='98' AND PIS_CST<>'49')\n"
                        + "                         OR (PISENT_CST='73' AND PIS_CST<>'06')");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao retornar a quantidade de produtos da tabela PRODUTODETALHE"
                        + " que contem o CST_PISENT incorreto em relação ao CST_PIS.\n" + e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nPis/Cofins de Saida" + "\nRealizando Correções... ");
            try {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST='49' WHERE PISENT_CST='98';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST='01' WHERE PISENT_CST='50';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST='04' WHERE PISENT_CST='70';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST='06' WHERE PISENT_CST='73';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET COFINS_CST=PIS_CST;");
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao corrigir os produtos da tabela PRODUTODETALHE"
                        + " que contem o CST_PISENT incorreto em relação ao CST_PIS.\n" + e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro nos Pis/Cofins de Saida...");
            }
        }
    }

    private void corrigeAliquotas() {
        if (chx_aliq_entrada.getSelectedObjects() != null) {
            String quantidade = null;
            try {
                rs = st.executeQuery("select COUNT(*) as qtde from produtodetalhe where (ALIQPIS<>ALIQPISENT or ALIQCOFINS<>ALIQCOFINSENT) or"
                        + "(PIS_CST='01' AND (ALIQPIS<>'1.65' OR ALIQPISENT<>'1.65')) OR"
                        + "(PIS_CST='04' AND (ALIQPIS<>'0.00' OR ALIQPISENT<>'0.00')) OR"
                        + "(PIS_CST='49' AND (ALIQPIS<>'0.00' OR ALIQPISENT<>'0.00')) OR"
                        + "(PIS_CST='06' AND (ALIQPIS<>'0.00' OR ALIQPISENT<>'0.00'))");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao retornar a quantidade de produtos da tabela PRODUTODETALHE"
                        + " que contem a ALIQPIS diferente da ALIQPISENT.\n" + e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nAliquotas Entrada/Saida" + "\nRealizando Correções... ");
            try {
                st.executeUpdate("UPDATE PRODUTODETALHE SET ALIQPIS='1.65',ALIQCOFINS='7.60',ALIQPISENT='1.65',ALIQCOFINSENT='7.60' WHERE PIS_CST='01';");
                st.executeUpdate("UPDATE PRODUTODETALHE SET ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE PIS_CST='04';\n");
                st.executeUpdate("UPDATE PRODUTODETALHE SET ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE PIS_CST='06';\n");
                st.executeUpdate("UPDATE PRODUTODETALHE SET ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE PIS_CST='49';\n");
                st.executeUpdate("UPDATE PRODUTODETALHE SET ALIQPIS=ALIQPISENT,ALIQCOFINS=ALIQCOFINSENT;");
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao corrigir os produtos da tabela PRODUTODETALHE"
                        + " que contem a ALIQPIS diferente da ALIQPISENT.\n" + e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro nas Aliquotas...");
            }
        }
    }

    private void corrigeItensNulos() {
        if (chx_itens_null.getSelectedObjects() != null) {
            String quantidade = null;
            try {
                rs = st.executeQuery("SELECT COUNT(*) as qtde FROM PRODUTODETALHE WHERE PIS_CST IS NULL");
                if (rs.next()) {
                    quantidade = rs.getString("qtde");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao retornar a quantidade de registros da tabela "
                        + "ProdutoDetalhe com PIS/COFINS nullos.\n" + e.getMessage());
            }
            txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nItens Nullos" + "\nRealizando Correções... ");
            try {
                st.executeUpdate("UPDATE PRODUTODETALHE SET PIS_CST='',COFINS_CST='',"
                        + "PISENT_CST='',COFINSENT_CST='',ALIQPIS=0,ALIQCOFINS=0,ALIQPISENT=0,ALIQCOFINSENT=0 WHERE "
                        + "PIS_CST IS NULL");
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\nCorreções Feitas: " + quantidade);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao corrigir os registros da tabela "
                        + "ProdutoDetalhe com PIS/COFINS nullos.\n" + e.getMessage());
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "Erro nos Itens Nullos...");
            } finally {
                listaProdutosPis(st);
            }
        }
    }

    private void apurar() {
        int mes = cbx_mes.getMonth() + 1;
        int ano = cbx_ano.getYear();
        if (chx_apuracao.getSelectedObjects() != null) {
            try {
                parte1();
                parte2();
                parte3();
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nApuração\n" + "Apuração do período: " + mes + "/" + ano);
                txt_areaProcesso.setText(txt_areaProcesso.getText() + "\n\nFim da Correção!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao efetuar a apuração da entrada/saida "
                        + "de acordo com o cadastro de produtos.\n" + e.getMessage());
            }
        }
    }

    private void parte1() {
        mes = cbx_mes.getMonth() + 1;
        ano = cbx_ano.getYear();
        try {
            st.executeUpdate("Update NfEntri Set CstPis = (Select PisEnt_Cst From Produtodetalhe\n"
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

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    private void parte2() {
        mes = cbx_mes.getMonth() + 1;
        ano = cbx_ano.getYear();
        try {
            st.executeUpdate("Update NfEntrc Set VlrPis = (Select Sum(vlrPis) From NfEntri\n"
                    + "                             Where NfEntri.CodEmpresa = NfEntrc.CodEmpresa\n"
                    + "                               And NfEntri.Numeronf = NfEntrc.Numeronf \n"
                    + "                               And NfEntri.Codfornec = NfEntrc.Codfornec),\n"
                    + "                   VlrCofins = (Select Sum(vlrCofins) From NfEntri\n"
                    + "                             Where NfEntri.CodEmpresa = NfEntrc.CodEmpresa\n"
                    + "                               And NfEntri.Numeronf = NfEntrc.Numeronf \n"
                    + "                               And NfEntri.Codfornec = NfEntrc.Codfornec)\n"
                    + "where extract(month from NfEntrc.Dt_Entrada)=" + mes + " and extract(year from NfEntrc.Dt_Entrada)=" + ano + ";");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void parte3() {
        mes = cbx_mes.getMonth() + 1;
        ano = cbx_ano.getYear();
        try {
            st.executeUpdate("Update NfSaidi Set CstPis = (Select Pis_Cst From Produtodetalhe\n"
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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void desativar(String codigo) {
        try {
            if (btn_desativar.getText().equals("Desativar") == false) {
                st.executeUpdate("UPDATE PRODUTO SET ATIVO = 'S' where CODPROD = " + codigo + ";");
                JOptionPane.showMessageDialog(null, "Produto " + codigo + " ATIVADO com sucesso!");
            } else {
                st.executeUpdate("UPDATE PRODUTO SET ATIVO = 'N' where CODPROD = " + codigo + ";");
                JOptionPane.showMessageDialog(null, "Produto " + codigo + " DESATIVADO com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            listaProdutosNCM(st);
        }
    }

    private void bloquear(String codigo) {
        try {
            if (btn_bloquear.getText().equals("Bloquear") == true) {
                st.executeUpdate("UPDATE PRODUTO SET PRODBLOQUEADO = 'S' where CODPROD = " + codigo + ";");
                JOptionPane.showMessageDialog(null, "Produto " + codigo + " BLOQUEADO com sucesso!");
            } else {
                st.executeUpdate("UPDATE PRODUTO SET PRODBLOQUEADO = 'N' where CODPROD = " + codigo + ";");
                JOptionPane.showMessageDialog(null, "Produto " + codigo + " DESBLOQUEADO com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            listaProdutosNCM(st);
        }
    }

    private void insereNatReceita(String natReceita) {
        if (JOptionPane.showConfirmDialog(null, "Deseja Cadastrar o segunte codigo NATUREZA DA RECEITA: " + natReceita, ""
                + "", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                rs = st.executeQuery("select max(ID) as qtde from NATRECEITA");
                if (rs.next()) {
                    try {
                        st.executeUpdate("INSERT INTO NATRECEITA (ID, CODRECEITA) VALUES "
                                + "('" + Integer.parseInt(rs.getString("qtde")) + 1 + "', '" + natReceita + "');");
                        if (JOptionPane.showConfirmDialog(null, "NATUREZA DA RECEITA " + natReceita + " Cadastrada com Sucesso! \n"
                                + "Deseja Utiliza-la no NCM: " + tabela3.getValueAt(tabela3.getSelectedRow(), 1), "", JOptionPane.YES_NO_OPTION) == 0) {
                            st.executeUpdate("UPDATE CLASFISC SET CODNATRECEITA = '" + natReceita + "' WHERE CODCLASFIS ='"
                                    + tabela3.getValueAt(tabela3.getSelectedRow(), 0).toString() + "';");
                        }
                        JOptionPane.showMessageDialog(null, natReceita + " inserido com sucesso para o codigo "
                                + tabela3.getValueAt(tabela3.getSelectedRow(), 0));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao retornar a quantidade de natureza da receita.\n" + e.getMessage());
            }
        }
    }

    private int contaQtdeDeItens() {
        try {
            //aquii
            int qtdeProdutosNCM = 0;
            int qtdeProdutosPis = 0;
            int qtdeNatReceita = 0;
            rs = st.executeQuery("SELECT count(*) qtde FROM PRODUTO P \n"
                    + "                    inner JOIN CLASFISC C ON P.CODCLASFIS=C.CODCLASFIS\n"
                    + "                     WHERE P.ATIVO = '" + validaCheckbox(chx_ativo) + "'\n"
                    + "                     and P.prodbloqueado = '" + validaCheckbox(chx_bloqueado) + "';");
            if (rs.next()) {
                qtdeProdutosNCM = Integer.parseInt(rs.getString("qtde"));
            }
            rs = st.executeQuery("SELECT count(*) qtde FROM PRODUTO P "
                    + "INNER JOIN PRODUTODETALHE D ON P.CODPROD=D.CODPROD");
            if (rs.next()) {
                qtdeProdutosPis = Integer.parseInt(rs.getString("qtde"));
            }
            rs = st.executeQuery("SELECT count(*) qtde FROM CLASFISC");
            if (rs.next()) {
                qtdeNatReceita = Integer.parseInt(rs.getString("qtde"));
            }
            return qtdeProdutosNCM + qtdeProdutosPis + qtdeNatReceita;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "erro ao contar a quantidade de produtos!\n" + e);
            return 0;
        }

    }
}
