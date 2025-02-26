package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.EptusDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class EptusGUI extends VRInternalFrame implements ConexaoEvent {

    private static final String SISTEMA = "Eptus";
    private static EptusGUI instance;
    private EptusDAO dao = new EptusDAO();

    public static String getSISTEMA() {
        return SISTEMA;
    }

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoMySQL.carregarParametros();
        tabProdutos.carregarParametros(params, SISTEMA);
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoMySQL.atualizarParametros();
        tabProdutos.gravarParametros(params, SISTEMA);
        Estabelecimento cliente = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.cnpj, SISTEMA, "LOJA_CLIENTE");
            vLojaCliente = cliente.cnpj;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private EptusGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        tabFornecedores.setOpcoesDisponiveis(dao);
        tabProdutos.setOpcoesDisponiveis(dao);
        tabCliente.setOpcoesDisponiveis(dao);
        
        tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                return dao.getSistema();
            }

            @Override
            public String getLoja() {
                return ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });

        this.title = "Importação " + SISTEMA;

        conexaoMySQL.host = "localhost";
        conexaoMySQL.database = "eptus";
        conexaoMySQL.port = "3306";
        conexaoMySQL.user = "root";
        conexaoMySQL.pass = "2E506Y73025m246u3J";

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        conexaoMySQL.setOnConectar(this);

        carregarParametros();

        centralizarForm();
        this.setMaximum(false);
    }

    public void carregarLojaVR() throws Exception {
        cmbLojaVR.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            cmbLojaVR.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
            if (oLoja.id == vLojaVR) {
                index = cont;
            }
            cont++;
        }
        cmbLojaVR.setSelectedIndex(index);
    }

    public void carregarLojaCliente() throws Exception {
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (Estabelecimento loja : dao.getLojas()) {
            cmbLojaOrigem.addItem(loja);
            if (vLojaCliente != null && vLojaCliente.equals(loja.cnpj)) {
                index = cont;
            }
            cont++;
        }
        cmbLojaOrigem.setSelectedIndex(index);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new EptusGUI(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    public void importarTabelas() throws Exception {

        Thread thread = new Thread() {
            int idLojaVR;
            String idLojaCliente;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);                    

                    if (tabs.getSelectedIndex() == 1) {

                        tabProdutos.setImportador(importador);
                        tabProdutos.executarImportacao();

                        tabFornecedores.setImportador(importador);
                        tabFornecedores.executarImportacao();
                        
                        tabCliente.setImportador(importador);
                        tabCliente.executarImportacao();

                        if ((chkVenda.isSelected())
                                && (txtDataInicioVenda.getDate() != null)
                                && (txtDataFimVenda.getDate() != null)) {
                            
                            dao.setDataInicioVenda(txtDataInicioVenda.getDate());
                            dao.setDataTerminoVenda(txtDataFimVenda.getDate());
                            
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                    
                } catch (Exception ex) {
                    try {
                        ConexaoMySQL.getConexao().close();
                    } catch (Exception ex1) {
                        Exceptions.printStackTrace(ex1);
                    }
                    
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRLabel1 = new vrframework.bean.label.VRLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new javax.swing.JTabbedPane();
        conexaoMySQL = new vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel();
        tabImportacao = new javax.swing.JTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabCliente = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        tabVendas = new vrframework.bean.panel.VRPanel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        chkVenda = new vrframework.bean.checkBox.VRCheckBox();
        txtDataFimVenda = new org.jdesktop.swingx.JXDatePicker();
        txtDataInicioVenda = new org.jdesktop.swingx.JXDatePicker();
        tabBalanca = new vrframework.bean.panel.VRPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();

        setTitle("Importação Eptus");
        setToolTipText("");

        vRLabel1.setText("Loja (Cliente):");

        cmbLojaOrigem.setModel(new javax.swing.DefaultComboBoxModel());

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

        jLabel1.setText("Loja:");

        javax.swing.GroupLayout pnlLojaLayout = new javax.swing.GroupLayout(pnlLoja);
        pnlLoja.setLayout(pnlLojaLayout);
        pnlLojaLayout.setHorizontalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlLojaLayout.setVerticalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        conexaoMySQL.setSistema(getSISTEMA());
        tabs.addTab("Conexão", conexaoMySQL);

        tabImportacao.addTab("Produtos", tabProdutos);
        tabImportacao.addTab("Fornecedores", tabFornecedores);
        tabImportacao.addTab("Clientes", tabCliente);

        vRLabel2.setText("Data Início");

        vRLabel3.setText("Data Fim");

        chkVenda.setText("Venda");
        chkVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendaActionPerformed(evt);
            }
        });

        txtDataFimVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataFimVendaActionPerformed(evt);
            }
        });

        txtDataInicioVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataInicioVendaActionPerformed(evt);
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
                        .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFimVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(330, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFimVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(258, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Vendas", tabVendas);

        tabs.addTab("Importação", tabImportacao);

        javax.swing.GroupLayout tabBalancaLayout = new javax.swing.GroupLayout(tabBalanca);
        tabBalanca.setLayout(tabBalancaLayout);
        tabBalancaLayout.setHorizontalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabBalancaLayout.setVerticalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(243, Short.MAX_VALUE))
        );

        tabs.addTab("Balança", tabBalanca);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        getAccessibleContext().setAccessibleName("Avance");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            importarTabelas();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void chkVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendaActionPerformed
        // TODO add your handling code here:
        if (chkVenda.isSelected()) {
            txtDataInicioVenda.setEnabled(true);
            txtDataFimVenda.setEnabled(true);
        } else {
            txtDataInicioVenda.setEnabled(false);
            txtDataFimVenda.setEnabled(false);
        }
    }//GEN-LAST:event_chkVendaActionPerformed

    private void txtDataFimVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataFimVendaActionPerformed
        if (txtDataFimVenda.getDate() == null) {
            txtDataFimVenda.setDate(new Date());
        }
    }//GEN-LAST:event_txtDataFimVendaActionPerformed

    private void txtDataInicioVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataInicioVendaActionPerformed
        if (txtDataInicioVenda.getDate() == null) {
            txtDataInicioVenda.setDate(new Date());
        }
    }//GEN-LAST:event_txtDataInicioVendaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkVenda;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel conexaoMySQL;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.panel.VRPanel tabBalanca;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabCliente;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private javax.swing.JTabbedPane tabImportacao;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabVendas;
    private javax.swing.JTabbedPane tabs;
    private org.jdesktop.swingx.JXDatePicker txtDataFimVenda;
    private org.jdesktop.swingx.JXDatePicker txtDataInicioVenda;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    // End of variables declaration//GEN-END:variables

    @Override
    public void executar() throws Exception {
        gravarParametros();
        carregarLojaVR();
        carregarLojaCliente();
    }

}
