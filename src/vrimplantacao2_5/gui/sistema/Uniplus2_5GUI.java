package vrimplantacao2_5.gui.sistema;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.UniplusDAO;
import vrimplantacao2.dao.interfaces.UniplusDAO.TabelaPreco;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.gui.componente.conexao.ConexaoEvent;
import vrimplantacao2_5.vo.enums.ESistema;

public class Uniplus2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.UNIPLUS.getNome();
    private static Uniplus2_5GUI instance;

    private final UniplusDAO dao = new UniplusDAO();

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        dao.setTabelaPreco(
                TabelaPreco.getByOrdinal(params.getInt(0, SISTEMA, "TABELA_PRECO"))
        );
        switch (dao.getTabelaPreco()) {
            case TABELA_PRECO:
                rbnTabelaPreco.setSelected(true);
                break;
            default:
                rbnTabelaFormacaoPrecoProduto.setSelected(true);
                break;
        }
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(pnlConn.getHost(), SISTEMA, "HOST");
        params.put(pnlConn.getSchema(), SISTEMA, "DATABASE");
        params.put(pnlConn.getPorta(), SISTEMA, "PORTA");
        params.put(pnlConn.getUsuario(), SISTEMA, "USUARIO");
        params.put(pnlConn.getSenha(), SISTEMA, "SENHA");
        params.put(dao.getTabelaPreco().ordinal(), SISTEMA, "TABELA_PRECO");
        params.put(chkDUN14Atacado.isSelected(), SISTEMA, "GERAR_DUN14_ATACADO");
        params.put(chkNewEan.isSelected(), SISTEMA, "PREFIXO_MAIS_CODIGO");

        pnlConn.atualizarParametros();

        params.salvar();
    }

    public Uniplus2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();
        tabProdutos.setOpcoesDisponiveis(dao);
        tabProdutos.tabParametros.add(pnlParametrosAdicionais);
        tabProdutos.btnMapaTribut.setVisible(false);
        tabFornecedor.setOpcoesDisponiveis(dao);
        tabClientes.setOpcoesDisponiveis(dao);

        pnlConn.setOnConectar(new ConexaoEvent() {
            @Override
            public void executar() throws Exception {
                tabProdutos.btnMapaTribut.setEnabled(true);
                gravarParametros();
            }
        });

        pnlConn.setSistema(ESistema.UNIPLUS);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
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

                    dao.setComplemento(pnlConn.getComplemento());
                    dao.DUN14Atacado = chkDUN14Atacado.isSelected();
                    dao.NewEan = chkNewEan.isSelected();
                    dao.ProdutoFornecedorNotas = chkProdutoNota.isSelected();
                    dao.usar_arquivoBalanca = chkTemArquivoBalanca.isSelected();
                    dao.temProdutoAssociado= chkTemProdutoAssociado.isSelected();
                    dao.produtosNaoAtualizados= chkProdutosNaoAtualizados.isSelected();
                    dao.outrasDespesas = chkContasPagarOutrasDespesas.isSelected();
                                       

                    Importador importador = new Importador(dao);

                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    importador.setIdConexao(pnlConn.idConexao);

                    tabProdutos.setImportador(importador);
                    tabFornecedor.setImportador(importador);
                    tabClientes.setImportador(importador);
                    
                    if(chkContasPagarOutrasDespesas.isSelected()){
                        importador.importarContasPagar(OpcaoContaPagar.NOVOS, OpcaoContaPagar.IMPORTAR_OUTRASDESPESAS);
                    }

                    if (tab.getSelectedIndex() == 0) {
                        switch (tabImportacao.getSelectedIndex()) {
                            case 0:
                                tabProdutos.executarImportacao();
                                break;
                            case 1:
                                tabFornecedor.executarImportacao();
                                break;
                            case 2:
                                tabClientes.executarImportacao();
                                break;
                            default:
                                break;
                        }
                    } 
                    
                    gravarParametros();
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
                instance = new Uniplus2_5GUI(i_mdiFrame);
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

        grbPriorizarPreco = new javax.swing.ButtonGroup();
        pnlParametrosAdicionais = new vr.view.components.panel.VRPanel();
        rbnTabelaFormacaoPrecoProduto = new vr.view.components.radiobutton.VRRadioButton();
        rbnTabelaPreco = new vr.view.components.radiobutton.VRRadioButton();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        vRConsultaContaContabil1 = new vrframework.bean.consultaContaContabil.VRConsultaContaContabil();
        pnlMigrar = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        pnlParam = new vrframework.bean.panel.VRPanel();
        chkDUN14Atacado = new vrframework.bean.checkBox.VRCheckBox();
        chkNewEan = new vrframework.bean.checkBox.VRCheckBox();
        chkTemArquivoBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoNota = new javax.swing.JCheckBox();
        chkTemProdutoAssociado = new javax.swing.JCheckBox();
        chkProdutosNaoAtualizados = new javax.swing.JCheckBox();
        chkContasPagarOutrasDespesas = new javax.swing.JCheckBox();
        tabBalanca = new javax.swing.JPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        grbPriorizarPreco.add(rbnTabelaFormacaoPrecoProduto);
        rbnTabelaFormacaoPrecoProduto.setSelected(true);
        rbnTabelaFormacaoPrecoProduto.setText("FormacaoPrecoProduto");
        rbnTabelaFormacaoPrecoProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnTabelaFormacaoPrecoProdutoActionPerformed(evt);
            }
        });

        grbPriorizarPreco.add(rbnTabelaPreco);
        rbnTabelaPreco.setText("Preço");
        rbnTabelaPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnTabelaPrecoActionPerformed(evt);
            }
        });

        vRLabel9.setText("Escolha a tabela para gerar o preço");
        vRLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout pnlParametrosAdicionaisLayout = new javax.swing.GroupLayout(pnlParametrosAdicionais);
        pnlParametrosAdicionais.setLayout(pnlParametrosAdicionaisLayout);
        pnlParametrosAdicionaisLayout.setHorizontalGroup(
            pnlParametrosAdicionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParametrosAdicionaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlParametrosAdicionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlParametrosAdicionaisLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(pnlParametrosAdicionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbnTabelaFormacaoPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rbnTabelaPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlParametrosAdicionaisLayout.setVerticalGroup(
            pnlParametrosAdicionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParametrosAdicionaisLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbnTabelaFormacaoPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbnTabelaPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setTitle("Uniplus");
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
        tabImportacao.addTab("Fornecedores", tabFornecedor);
        tabImportacao.addTab("Clientes", tabClientes);

        chkDUN14Atacado.setText("Gerar DUN14 para Atacado");

        chkNewEan.setText("Prefixo + Cód.");

        chkTemArquivoBalanca.setText("Tem Arquivo Balança");

        chkProdutoNota.setText("Produtos X Fornecedores da NF");

        chkTemProdutoAssociado.setText("Tem Produto Associado");

        chkProdutosNaoAtualizados.setText("Atualizar Preço e Custo de Produtos não atualizados");

        chkContasPagarOutrasDespesas.setText("Contas a Pagar (Outras Despesas)");

        javax.swing.GroupLayout pnlParamLayout = new javax.swing.GroupLayout(pnlParam);
        pnlParam.setLayout(pnlParamLayout);
        pnlParamLayout.setHorizontalGroup(
            pnlParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkDUN14Atacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNewEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTemArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoNota)
                    .addComponent(chkTemProdutoAssociado)
                    .addComponent(chkProdutosNaoAtualizados)
                    .addComponent(chkContasPagarOutrasDespesas))
                .addContainerGap(377, Short.MAX_VALUE))
        );
        pnlParamLayout.setVerticalGroup(
            pnlParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParamLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkDUN14Atacado, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkNewEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTemArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutoNota)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTemProdutoAssociado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutosNaoAtualizados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkContasPagarOutrasDespesas)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Parâmetros Extra", pnlParam);

        tab.addTab("Importação", tabImportacao);

        javax.swing.GroupLayout tabBalancaLayout = new javax.swing.GroupLayout(tabBalanca);
        tabBalanca.setLayout(tabBalancaLayout);
        tabBalancaLayout.setHorizontalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );
        tabBalancaLayout.setVerticalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        tab.addTab("Balança", tabBalanca);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
                    .addComponent(pnlMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
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

    private void rbnTabelaFormacaoPrecoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbnTabelaFormacaoPrecoProdutoActionPerformed
        dao.setTabelaPreco(TabelaPreco.TABELA_FORMACAO_PRECO_PRODUTO);
    }//GEN-LAST:event_rbnTabelaFormacaoPrecoProdutoActionPerformed

    private void rbnTabelaPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbnTabelaPrecoActionPerformed
        dao.setTabelaPreco(TabelaPreco.TABELA_PRECO);
    }//GEN-LAST:event_rbnTabelaPrecoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkContasPagarOutrasDespesas;
    private vrframework.bean.checkBox.VRCheckBox chkDUN14Atacado;
    private vrframework.bean.checkBox.VRCheckBox chkNewEan;
    private javax.swing.JCheckBox chkProdutoNota;
    private javax.swing.JCheckBox chkProdutosNaoAtualizados;
    private vrframework.bean.checkBox.VRCheckBox chkTemArquivoBalanca;
    private javax.swing.JCheckBox chkTemProdutoAssociado;
    private javax.swing.ButtonGroup grbPriorizarPreco;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private vrframework.bean.panel.VRPanel pnlParam;
    private vr.view.components.panel.VRPanel pnlParametrosAdicionais;
    private vr.view.components.radiobutton.VRRadioButton rbnTabelaFormacaoPrecoProduto;
    private vr.view.components.radiobutton.VRRadioButton rbnTabelaPreco;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private javax.swing.JPanel tabBalanca;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedor;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.consultaContaContabil.VRConsultaContaContabil vRConsultaContaContabil1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel9;
    // End of variables declaration//GEN-END:variables
}
