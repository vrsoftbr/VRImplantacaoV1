package vrimplantacao2_5.gui.sistema;

import java.awt.Frame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.RPInfoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author Guilherme
 */
public class RPInfo2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.RPINFO.getNome();

    private static RPInfo2_5GUI instance;
    private final RPInfoDAO dao;

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new RPInfo2_5GUI(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    /**
     * Creates new form RPInfoGUI
     *
     * @param frame
     * @throws java.lang.Exception
     */
    public RPInfo2_5GUI(VRMdiFrame frame) throws Exception {
        super(frame);
        this.dao = new RPInfoDAO();
        initComponents();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                instance = null;
            }
        });

        tabProdutos.btnMapaTribut.setProvider(new MapaTributacaoButtonProvider() {
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

        tabProdutos.setOpcoesDisponiveis(dao);

        carregarParametros();
        
        pnlConn.setSistema(ESistema.RPINFO);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        
        tabProdutos.carregarParametros(params, SISTEMA);
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(pnlConn.getComplemento(), SISTEMA, "COMPLEMENTO");
        
        pnlConn.atualizarParametros();

        params.salvar();
    }

    private void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR;
            String idLojaCliente;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = pnlConn.getLojaVR();
                    idLojaCliente = pnlConn.getLojaOrigem();

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    importador.setIdConexao(pnlConn.idConexao);
                    
                    dao.gerarCodigoAtacado = chkGerarCodigoAtacado.isSelected();
                    dao.utilizarCustoNota = chkUtilizarCustoNota.isSelected();
                    dao.removeDigitoEAN = chkRemoveDigitoBalanca.isSelected();
                    dao.importarFuncionario = chkImpFuncForn.isSelected();

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);
                    
                    switch (tabs.getSelectedIndex()) {
                        case 0:
                            switch (tabParametros.getSelectedIndex()) {
                                case 0:
                                    tabProdutos.executarImportacao();
                                    break;
                                case 1:
                                    tabFornecedores.executarImportacao();
                                    break;
                                case 2:
                                    tabClientes.executarImportacao();
                                    break;
                                case 3:
                                    if (chkVendas.isSelected()) {
                                        if (!dtVenda.getText().trim().isEmpty()) {
                                            dao.setTabelaVenda(dtVenda.getText().trim());
                                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                                        } else {
                                            throw new Exception("Preencha os campos para realizar a importação de vendas.");
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            } break;
                        case 1:
                            break;
                        default:
                            break;
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabParametros = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        tabVendas = new vrframework.bean.panel.VRPanel();
        lblMesVenda = new vrframework.bean.label.VRLabel();
        dtVenda = new vrframework.bean.textField.VRTextField();
        chkVendas = new vrframework.bean.checkBox.VRCheckBox();
        jPanel1 = new javax.swing.JPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        chkGerarCodigoAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkUtilizarCustoNota = new vrframework.bean.checkBox.VRCheckBox();
        chkRemoveDigitoBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkImpFuncForn = new javax.swing.JCheckBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jBLimpar = new javax.swing.JButton();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Importação RPInfo");

        tabParametros.addTab("Produtos", tabProdutos);
        tabParametros.addTab("Fornecedores", tabFornecedores);
        tabParametros.addTab("Clientes", tabClientes);

        tabVendas.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Vendas (PDV)"));

        org.openide.awt.Mnemonics.setLocalizedText(lblMesVenda, "Mês/Ano da Venda");

        dtVenda.setEditable(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkVendas, "Pdv Vendas");
        chkVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabVendasLayout.createSequentialGroup()
                        .addComponent(lblMesVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMesVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        tabParametros.addTab("Vendas", tabVendas);

        tabs.addTab("Importação", tabParametros);

        org.openide.awt.Mnemonics.setLocalizedText(chkGerarCodigoAtacado, "Gerar Código de Atacado");

        org.openide.awt.Mnemonics.setLocalizedText(chkUtilizarCustoNota, "Utilizar custo das notas");

        org.openide.awt.Mnemonics.setLocalizedText(chkRemoveDigitoBalanca, "Remove Digíto do EAN para Produto de Balança");

        org.openide.awt.Mnemonics.setLocalizedText(chkImpFuncForn, "Importar Funcionário em Fornecedor");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkRemoveDigitoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkGerarCodigoAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkUtilizarCustoNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkImpFuncForn)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkGerarCodigoAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUtilizarCustoNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkImpFuncForn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkRemoveDigitoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        tabs.addTab("Especifico", jPanel1);

        btnMigrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnMigrar, "Migrar");
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
        org.openide.awt.Mnemonics.setLocalizedText(jBLimpar, "Limpar");
        jBLimpar.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBLimpar)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            importarTabelas();
            gravarParametros();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void chkVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendasActionPerformed

    }//GEN-LAST:event_chkVendasActionPerformed

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        tabClientes.limparCliente();
        tabFornecedores.limparFornecedor();
    }//GEN-LAST:event_jBLimparActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkGerarCodigoAtacado;
    private javax.swing.JCheckBox chkImpFuncForn;
    private vrframework.bean.checkBox.VRCheckBox chkRemoveDigitoBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkUtilizarCustoNota;
    private vrframework.bean.checkBox.VRCheckBox chkVendas;
    private vrframework.bean.textField.VRTextField dtVenda;
    private javax.swing.JButton jBLimpar;
    private javax.swing.JPanel jPanel1;
    private vrframework.bean.label.VRLabel lblMesVenda;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabParametros;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    // End of variables declaration//GEN-END:variables
}
