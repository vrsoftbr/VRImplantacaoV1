package vrimplantacao2_5.gui.sistema;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.dao.sistema.CPGestorByViewDAO;
import vrimplantacao2_5.vo.enums.ESistema;

public class CPGestorByView2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.CPGESTOR.getNome();
    private static CPGestorByView2_5GUI instance;

    private final CPGestorByViewDAO cpgestorDAO = new CPGestorByViewDAO();

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
    }

    public CPGestorByView2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();
        tabProdutos.setOpcoesDisponiveis(cpgestorDAO);
        tabFornecedores.setOpcoesDisponiveis(cpgestorDAO);
        tabClientes.setOpcoesDisponiveis(cpgestorDAO);
        tabProdutos.btnMapaTribut.setEnabled(false);

        pnlConn.setSistema(ESistema.CPGESTOR);
        pnlConn.getNomeConexao();
        
        pnlBalanca.setLoja(pnlConn.getLojaOrigem());
        pnlBalanca.setSistema(SISTEMA + " - " + pnlConn.idConexao);

        centralizarForm();
        this.setMaximum(false);
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(pnlConn.getHost(), SISTEMA, "HOST");
        params.put(pnlConn.getSchema(), SISTEMA, "DATABASE");
        params.put(pnlConn.getPorta(), SISTEMA, "PORTA");
        params.put(pnlConn.getUsuario(), SISTEMA, "USUARIO");
        params.put(pnlConn.getSenha(), SISTEMA, "SENHA");

        pnlConn.atualizarParametros();

        params.salvar();
    }

    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, balanca;
            String idLojaCliente;
            String lojaMesmoID;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = pnlConn.getLojaVR();
                    idLojaCliente = pnlConn.getLojaOrigem();
                    cpgestorDAO.setUtilizarQtdEmbalagemIgualAUm(chkForcarMigracao.isSelected());
                    
                    Importador importador = new Importador(cpgestorDAO);
                    importador.importarPorPlanilha = chkForcarMigracao.isSelected();
                    
                    cpgestorDAO.setViewEan(txtViewEAN.getText());
                    cpgestorDAO.setViewProduto(txtViewProduto.getText());
                    cpgestorDAO.setViewVenda(txtViewVenda.getText());

                    importador.setLojaOrigem(pnlConn.getLojaOrigem());
                    
                    if (!txtImpLojaOrigem.getText().isEmpty()) {
                        importador.setLojaOrigem(txtImpLojaOrigem.getText());
                    }
                    
                    importador.setLojaVR(pnlConn.getLojaVR());
                    importador.setIdConexao(pnlConn.idConexao);

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);
                    
                    if (tabMenu.getSelectedIndex() == 0) {
                        switch (tabImportacao.getSelectedIndex()) {
                            case 0:
                                if(txtViewEAN.getText().trim().isEmpty() && txtViewProduto.getText().trim().isEmpty()) {
                                    throw new VRException("Favor, informar os nomes das views!");
                                }
                                
                                cpgestorDAO.getOpcoes().put("delimiter", txtDelimitadorProd.getText());
                                cpgestorDAO.setArquivo(txtProdutoFile.getArquivo());
                                cpgestorDAO.setComplemento(pnlConn.idConexao);
                                
                                cpgestorDAO.setDataInicioVenda(tabProdutos.edtDtVendaIni.getDate());
                                cpgestorDAO.setDataTerminoVenda(tabProdutos.edtDtVendaFim.getDate());
                                
                                tabProdutos.executarImportacao();
                                break;
                            case 1:
                                if (txtViewFornecedor.getText().trim().isEmpty()) {
                                    throw new VRException("Favor, informar os nomes das views!");
                                }
                                
                                cpgestorDAO.setViewFornecedor(txtViewFornecedor.getText());
                                tabFornecedores.executarImportacao();
                                break;
                            case 2:
                                tabClientes.executarImportacao();
                                break;
                            default: ;
                        }
                    }

                    pnlConn.atualizarParametros();

                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());

                    pnlConn.fecharConexao();
                } catch (Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new CPGestorByView2_5GUI(i_mdiFrame);
            }

            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMigrar = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        tabMenu = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabCli = new javax.swing.JPanel();
        scpClientes = new javax.swing.JScrollPane();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        tabExtras = new vrframework.bean.panel.VRPanel();
        lblViewProduto = new javax.swing.JLabel();
        txtViewProduto = new javax.swing.JTextField();
        txtViewEAN = new javax.swing.JTextField();
        lblViewEAN = new javax.swing.JLabel();
        lblViewFornecedor = new javax.swing.JLabel();
        txtViewFornecedor = new javax.swing.JTextField();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        txtDelimitadorProd = new javax.swing.JTextField();
        txtProdutoFile = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lblImpLojaOrigem = new javax.swing.JLabel();
        txtImpLojaOrigem = new javax.swing.JTextField();
        lblViewVendas = new javax.swing.JLabel();
        txtViewVenda = new javax.swing.JTextField();
        chkForcarMigracao = new javax.swing.JCheckBox();
        chkEmbalagemIgualAUm1 = new javax.swing.JCheckBox();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("Consinco");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        btnMigrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMigrar.setText("Migrar");
        btnMigrar.setFocusable(false);
        btnMigrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMigrarLayout = new javax.swing.GroupLayout(pnlMigrar);
        pnlMigrar.setLayout(pnlMigrarLayout);
        pnlMigrarLayout.setHorizontalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMigrarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlMigrarLayout.setVerticalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        tabImportacao.addTab("Produtos", tabProdutos);
        tabImportacao.addTab("Fornecedores", tabFornecedores);

        scpClientes.setViewportView(tabClientes);

        javax.swing.GroupLayout tabCliLayout = new javax.swing.GroupLayout(tabCli);
        tabCli.setLayout(tabCliLayout);
        tabCliLayout.setHorizontalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCliLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabCliLayout.setVerticalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabCli);

        lblViewProduto.setText("View Produto");

        lblViewEAN.setText("View Ean");

        lblViewFornecedor.setText("View Fornecedor");

        vRLabel8.setText("Del");

        txtDelimitadorProd.setText("^");

        vRLabel1.setText("Informe o arquivo dos produtos");

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Views Vovo Zuzu:\nvw_exp_produtos_zuzu \nvw_exp_barras_zuzu \nvw_exp_forn_zuzu  \n\nViews Panificadora Vovó\nvw_exp_produtos_panificadora\nvw_exp_barras_panificadora\n\nSanta Fé \nvw_exp_produtos_sta \nvw_exp_barras_sta \nvw_exp_forn_sta\n\nViews de Vendas\nvw_exp_vendas_stafe_01_20;\nvw_exp_vendas_stafe_01_21;\nvw_exp_vendas_stafe_01_22;\n\nvw_exp_vendas_stafe_02_20;\nvw_exp_vendas_stafe_02_21;\nvw_exp_vendas_stafe_02_22;\n\nvw_exp_vendas_stafe_03_20;\nvw_exp_vendas_stafe_03_21;\nvw_exp_vendas_stafe_03_22;\n\nvw_exp_vendas_stafe_04_20;\nvw_exp_vendas_stafe_04_21;\nvw_exp_vendas_stafe_04_22;\n\nvw_exp_vendas_vovo_01_20;\nvw_exp_vendas_vovo_01_21;\nvw_exp_vendas_vovo_01_22;");
        jScrollPane1.setViewportView(jTextArea1);

        lblImpLojaOrigem.setText("Imp Loja");

        lblViewVendas.setText("View Vendas");

        txtViewVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtViewVendaActionPerformed(evt);
            }
        });

        chkForcarMigracao.setText("Forçar Migração");

        chkEmbalagemIgualAUm1.setText("Utilizar Qtd. Emb. 1");

        javax.swing.GroupLayout tabExtrasLayout = new javax.swing.GroupLayout(tabExtras);
        tabExtras.setLayout(tabExtrasLayout);
        tabExtrasLayout.setHorizontalGroup(
            tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabExtrasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDelimitadorProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProdutoFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblViewEAN)
                                .addComponent(txtViewEAN, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                .addComponent(lblViewProduto)
                                .addComponent(txtViewProduto))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabExtrasLayout.createSequentialGroup()
                                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblImpLojaOrigem)
                                    .addComponent(txtImpLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtViewVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblViewFornecedor)
                            .addComponent(txtViewFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblViewVendas)
                            .addGroup(tabExtrasLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkEmbalagemIgualAUm1)
                                    .addComponent(chkForcarMigracao))))))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabExtrasLayout.setVerticalGroup(
            tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabExtrasLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabExtrasLayout.createSequentialGroup()
                                .addComponent(lblViewFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtViewFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabExtrasLayout.createSequentialGroup()
                                .addComponent(lblViewProduto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtViewProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(tabExtrasLayout.createSequentialGroup()
                                .addComponent(lblViewEAN)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtViewEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabExtrasLayout.createSequentialGroup()
                                .addComponent(lblViewVendas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtViewVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblImpLojaOrigem)
                            .addComponent(chkForcarMigracao))
                        .addGap(8, 8, 8)
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtImpLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEmbalagemIgualAUm1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDelimitadorProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabExtrasLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtProdutoFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        tabImportacao.addTab("Parametros Extras", tabExtras);

        tabMenu.addTab("Importação", tabImportacao);
        tabMenu.addTab("Balança", pnlBalanca);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                    .addComponent(pnlMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            gravarParametros();
            importarTabelas();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void txtViewVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtViewVendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtViewVendaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.JCheckBox chkEmbalagemIgualAUm1;
    private javax.swing.JCheckBox chkForcarMigracao;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblImpLojaOrigem;
    private javax.swing.JLabel lblViewEAN;
    private javax.swing.JLabel lblViewFornecedor;
    private javax.swing.JLabel lblViewProduto;
    private javax.swing.JLabel lblViewVendas;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private javax.swing.JScrollPane scpClientes;
    private javax.swing.JPanel tabCli;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private vrframework.bean.panel.VRPanel tabExtras;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabMenu;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JTextField txtDelimitadorProd;
    private javax.swing.JTextField txtImpLojaOrigem;
    private vrframework.bean.fileChooser.VRFileChooser txtProdutoFile;
    private javax.swing.JTextField txtViewEAN;
    private javax.swing.JTextField txtViewFornecedor;
    private javax.swing.JTextField txtViewProduto;
    private javax.swing.JTextField txtViewVenda;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel8;
    // End of variables declaration//GEN-END:variables
}
