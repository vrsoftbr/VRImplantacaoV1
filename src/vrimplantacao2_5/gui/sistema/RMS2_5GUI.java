package vrimplantacao2_5.gui.sistema;

import java.awt.Frame;
import java.util.Date;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.radioButton.VRRadioButton;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.dao.sistema.RMSDAO;
import vrimplantacao2_5.vo.enums.ESistema;

public class RMS2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.RMS.getNome();
    private static RMS2_5GUI instance;

    private final RMSDAO dao = new RMSDAO();

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
    }

    public RMS2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();
        tabProdutos.setOpcoesDisponiveis(dao);
        tabFornecedores.setOpcoesDisponiveis(dao);
        tabClientes.setOpcoesDisponiveis(dao);
        tabProdutos.btnMapaTribut.setEnabled(false);

        tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                return dao.getSistema() + " - " + pnlConn.idConexao;
            }

            @Override
            public String getLoja() {
                dao.setLojaOrigem(pnlConn.getLojaOrigem());
                return dao.getLojaOrigem();
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });

        pnlConn.setSistema(ESistema.RMS);
        pnlConn.getNomeConexao();

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

                    Importador importador = new Importador(dao);

                    importador.setLojaOrigem(pnlConn.getLojaOrigem());
                    importador.setLojaVR(pnlConn.getLojaVR());
                    importador.setIdConexao(pnlConn.idConexao);
                    dao.setUtilizarViewMixFiscal(chkUtilizarViewMixFiscal.isSelected());

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);
                    
                    if(chkVendas.isSelected()) {
                            dao.setDataInicioVenda(edtVendaDtIni.getDate());
                            dao.setDataTerminoVenda(edtVendaDtFim.getDate());
                            
                            if (rdbVendasV1.isSelected()) {
                                dao.setVersaoDaVenda(1);
                            } else if (rdbVendasV2.isSelected()) {
                                dao.setVersaoDaVenda(2);
                            } else if (rdbVendasV3.isSelected()) {
                                dao.setVersaoDaVenda(3);
                            }
                            
                            importador.eBancoUnificado = chkBancoUnificado.isSelected();
                            
                            /*if (rdbVendasV1.equals(groupVendasPdv.getSelection())) {
                                dao.setVersaoDaVenda(1);
                            } else if (rdbVendasV2.equals(groupVendasPdv.getSelection())) {
                                dao.setVersaoDaVenda(2);                                
                            } else if (rdbVendasV3.equals(groupVendasPdv.getSelection())) {
                                dao.setVersaoDaVenda(3);                                
                            }*/                            
                            RMSDAO.tabela_venda = dtVenda.getText();
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }

                    if (tabMenu.getSelectedIndex() == 0) {
                        switch (tabImportacao.getSelectedIndex()) {
                            case 0:
                                tabProdutos.executarImportacao();
                                break;
                            case 1:
                                tabFornecedores.executarImportacao();
                                break;
                            case 2:
                                tabClientes.executarImportacao();
                                break;
                            default:
                                break;
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
                instance = new RMS2_5GUI(i_mdiFrame);
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
        jBLimpar = new javax.swing.JButton();
        tabMenu = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabCli = new javax.swing.JPanel();
        scpClientes = new javax.swing.JScrollPane();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tabVendas = new vrframework.bean.panel.VRPanel();
        edtVendaDtIni = new org.jdesktop.swingx.JXDatePicker();
        edtVendaDtFim = new org.jdesktop.swingx.JXDatePicker();
        chkHistoricoVendas = new vrframework.bean.checkBox.VRCheckBox();
        lblMesVenda1 = new vrframework.bean.label.VRLabel();
        lblMesVenda2 = new vrframework.bean.label.VRLabel();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        lblMesVenda = new vrframework.bean.label.VRLabel();
        dtVenda = new vrframework.bean.textField.VRTextField();
        chkVendas = new vrframework.bean.checkBox.VRCheckBox();
        rdbVendasV1 = new vrframework.bean.radioButton.VRRadioButton();
        rdbVendasV2 = new vrframework.bean.radioButton.VRRadioButton();
        rdbVendasV3 = new vrframework.bean.radioButton.VRRadioButton();
        chkBancoUnificado = new vrframework.bean.checkBox.VRCheckBox();
        jPanel1 = new javax.swing.JPanel();
        chkUtilizarViewMixFiscal = new vrframework.bean.checkBox.VRCheckBox();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("RMS");
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

        jBLimpar.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jBLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        jBLimpar.setText("Limpar");
        jBLimpar.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMigrarLayout = new javax.swing.GroupLayout(pnlMigrar);
        pnlMigrar.setLayout(pnlMigrarLayout);
        pnlMigrarLayout.setHorizontalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMigrarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jBLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlMigrarLayout.setVerticalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jBLimpar)
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
                .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabCliLayout.setVerticalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabCli);

        tabMenu.addTab("Importação", tabImportacao);
        tabMenu.addTab("Balança", pnlBalanca);

        chkHistoricoVendas.setText("Histórico de Vendas (Notas de Saída)");
        chkHistoricoVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHistoricoVendasActionPerformed(evt);
            }
        });

        lblMesVenda1.setText("Data Inicial");

        lblMesVenda2.setText("Data Final");

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendas PDV"));

        lblMesVenda.setText("Mês da Venda");

        dtVenda.setEditable(true);

        chkVendas.setText("Importar");
        chkVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendasActionPerformed(evt);
            }
        });

        rdbVendasV1.setSelected(true);
        rdbVendasV1.setText("v1.0");
        rdbVendasV1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbVendasV1tipoVendaSelect(evt);
            }
        });

        rdbVendasV2.setText("v2.0");
        rdbVendasV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbVendasV2tipoVendaSelect(evt);
            }
        });

        rdbVendasV3.setText("v3.0");
        rdbVendasV3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbVendasV3tipoVendaSelect(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(lblMesVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbVendasV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbVendasV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbVendasV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 52, Short.MAX_VALUE))))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbVendasV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbVendasV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbVendasV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMesVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        chkBancoUnificado.setText("Banco RMS é Unificado");

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkHistoricoVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabVendasLayout.createSequentialGroup()
                        .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edtVendaDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMesVenda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMesVenda2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabVendasLayout.createSequentialGroup()
                                .addComponent(edtVendaDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkBancoUnificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(219, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMesVenda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMesVenda2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edtVendaDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtVendaDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBancoUnificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkHistoricoVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabMenu.addTab("Vendas", tabVendas);

        chkUtilizarViewMixFiscal.setText("Utilizar View de Mix Fiscal");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUtilizarViewMixFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(423, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(chkUtilizarViewMixFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(216, Short.MAX_VALUE))
        );

        tabMenu.addTab("Extras", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                    .addComponent(pnlMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("RMS");

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

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        tabClientes.limparCliente();
        tabFornecedores.limparFornecedor();
    }//GEN-LAST:event_jBLimparActionPerformed

    private void chkHistoricoVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHistoricoVendasActionPerformed
        if (edtVendaDtIni.getDate() == null) {
            edtVendaDtIni.setDate(new Date());
        }
        if (edtVendaDtFim.getDate() == null) {
            edtVendaDtFim.setDate(new Date());
        }
    }//GEN-LAST:event_chkHistoricoVendasActionPerformed

    private void chkVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendasActionPerformed
        rdbVendasV1.setEnabled(chkVendas.isSelected());
        rdbVendasV2.setEnabled(chkVendas.isSelected());
        rdbVendasV3.setEnabled(chkVendas.isSelected());
    }//GEN-LAST:event_chkVendasActionPerformed

    private void rdbVendasV1tipoVendaSelect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbVendasV1tipoVendaSelect
        VRRadioButton btn = (VRRadioButton) evt.getSource();
        dtVenda.setEnabled(
                chkVendas.isSelected() && (btn.equals(rdbVendasV2)
                || btn.equals(rdbVendasV3))
        );
        edtVendaDtIni.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
        edtVendaDtFim.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
    }//GEN-LAST:event_rdbVendasV1tipoVendaSelect

    private void rdbVendasV2tipoVendaSelect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbVendasV2tipoVendaSelect
        VRRadioButton btn = (VRRadioButton) evt.getSource();
        dtVenda.setEnabled(
                chkVendas.isSelected() && (btn.equals(rdbVendasV2)
                || btn.equals(rdbVendasV3))
        );
        edtVendaDtIni.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
        edtVendaDtFim.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
    }//GEN-LAST:event_rdbVendasV2tipoVendaSelect

    private void rdbVendasV3tipoVendaSelect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbVendasV3tipoVendaSelect
        VRRadioButton btn = (VRRadioButton) evt.getSource();
        dtVenda.setEnabled(
                chkVendas.isSelected() && (btn.equals(rdbVendasV2)
                || btn.equals(rdbVendasV3))
        );
        edtVendaDtIni.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
        edtVendaDtFim.setEnabled(
                chkVendas.isSelected()
                && btn.equals(rdbVendasV1)
        );
    }//GEN-LAST:event_rdbVendasV3tipoVendaSelect

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkBancoUnificado;
    private vrframework.bean.checkBox.VRCheckBox chkHistoricoVendas;
    private vrframework.bean.checkBox.VRCheckBox chkUtilizarViewMixFiscal;
    private vrframework.bean.checkBox.VRCheckBox chkVendas;
    private vrframework.bean.textField.VRTextField dtVenda;
    private org.jdesktop.swingx.JXDatePicker edtVendaDtFim;
    private org.jdesktop.swingx.JXDatePicker edtVendaDtIni;
    private javax.swing.JButton jBLimpar;
    private javax.swing.JPanel jPanel1;
    private vrframework.bean.label.VRLabel lblMesVenda;
    private vrframework.bean.label.VRLabel lblMesVenda1;
    private vrframework.bean.label.VRLabel lblMesVenda2;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private vrframework.bean.radioButton.VRRadioButton rdbVendasV1;
    private vrframework.bean.radioButton.VRRadioButton rdbVendasV2;
    private vrframework.bean.radioButton.VRRadioButton rdbVendasV3;
    private javax.swing.JScrollPane scpClientes;
    private javax.swing.JPanel tabCli;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabMenu;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.panel.VRPanel vRPanel1;
    // End of variables declaration//GEN-END:variables
}
