package vrimplantacao2.gui.interfaces;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.FaciliteDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoView;
import vrimplantacao2.parametro.Parametros;

public class FaciliteGUI extends VRInternalFrame implements ConexaoEvent {

    private static final String SISTEMA = "FACILITE";
    private static FaciliteGUI instance;

    public static String getSISTEMA() {
        return SISTEMA;
    }

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoFirebird.carregarParametros();
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoFirebird.atualizarParametros();
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

    private FaciliteDAO dao = new FaciliteDAO();

    private FaciliteGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;
        
        ConexaoFirebird.encoding = "WIN1252";

        conexaoFirebird.host = "localhost";
        conexaoFirebird.database = "C:/Fenix/DB/FENIX.fdb";
        conexaoFirebird.port = "3050";
        conexaoFirebird.user = "pointer";
        conexaoFirebird.pass = "sysadmin";

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        
        tabProdutos.pnlMercNivel.setVisible(false);
        tabProdutos.chkPReceitaFilizola.setVisible(false);
        tabProdutos.chkPReceitaToledo.setVisible(false);
        tabProdutos.btnMapaTribut.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MapaTributacaoView.exibir(
                            mdiFrame,
                            SISTEMA,
                            ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj,
                            dao);
                } catch (Exception ex) {
                    Util.exibirMensagemErro(ex, "Erro ao abrir");
                }
            }            
        });

        conexaoFirebird.setOnConectar(this);

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
        for (Estabelecimento loja : dao.getLojasCliente()) {
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
                instance = new FaciliteGUI(i_mdiFrame);
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
                    tabProdutos.setImportador(importador);

                    if (tabOperacoes.getSelectedIndex() == 0) {

                        tabProdutos.executarImportacao();
                        
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }

                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }

                        {
                            List<OpcaoFornecedor> opcoes = new ArrayList<>();
                            if (chkFContatos.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                            if (chkFTipoPagamento.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_PAGAMENTO);
                            }
                            if (chkFEndereco.isSelected()) {
                                opcoes.add(OpcaoFornecedor.ENDERECO);
                            }
                            if (chkFNumero.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NUMERO);
                            }
                            if (chkFTipoEmp.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                            }
                            if (chkFTipoForn.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_FORNECEDOR);
                            }

                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        }
                        
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
                        }
                        
                    } else if (tabOperacoes.getSelectedIndex() == 1) {
                        if (chkUnifProdutos.isSelected()) {
                            importador.unificarProdutos();
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
                } finally {
                    tabProdutos.setImportador(null);
                }
            }
        };

        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        tabOperacoes = new javax.swing.JTabbedPane();
        tabImportacao = new javax.swing.JTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        jPanel3 = new javax.swing.JPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkFEndereco = new vrframework.bean.checkBox.VRCheckBox();
        chkFNumero = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoEmp = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoForn = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabClientes = new vrframework.bean.panel.VRPanel();
        jPanel4 = new javax.swing.JPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new javax.swing.JPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutosFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientesPreferenciais = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientesEventuais = new vrframework.bean.checkBox.VRCheckBox();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new javax.swing.JTabbedPane();
        conexaoFirebird = new vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel();

        setTitle("Importação Emporio");
        setToolTipText("");

        vRLabel1.setText("Loja (Cliente):");

        cmbLojaOrigem.setModel(new javax.swing.DefaultComboBoxModel());

        tabImportacao.addTab("Produtos", tabProdutos);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkFornecedor.setText("Fornecedor");
        jPanel3.add(chkFornecedor);

        chkFContatos.setText("Contatos");
        jPanel3.add(chkFContatos);

        chkFTipoPagamento.setText("Tipo Pagamento");
        jPanel3.add(chkFTipoPagamento);

        chkFEndereco.setText("Endereço");
        jPanel3.add(chkFEndereco);

        chkFNumero.setText("Número");
        jPanel3.add(chkFNumero);

        chkFTipoEmp.setText("Tipo Empresa");
        jPanel3.add(chkFTipoEmp);

        chkFTipoForn.setText("Tipo Fornecedor");
        jPanel3.add(chkFTipoForn);

        chkProdutoFornecedor.setText("Produto Fornecedor");
        jPanel3.add(chkProdutoFornecedor);

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 494, Short.MAX_VALUE)
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Fornecedores", tabFornecedor);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkClientePreferencial.setText("Cliente Preferencial");
        jPanel4.add(chkClientePreferencial);

        chkClienteEventual.setText("Cliente Eventual");
        jPanel4.add(chkClienteEventual);

        chkCreditoRotativo.setText("Crédito Rotativo");
        jPanel4.add(chkCreditoRotativo);

        chkCheque.setText("Cheque");
        jPanel4.add(chkCheque);

        javax.swing.GroupLayout tabClientesLayout = new javax.swing.GroupLayout(tabClientes);
        tabClientes.setLayout(tabClientesLayout);
        tabClientesLayout.setHorizontalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
        );
        tabClientesLayout.setVerticalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabClientes);

        tabOperacoes.addTab("Importação", tabImportacao);

        chkUnifProdutos.setText("Unificar produtos (Somente com EAN válido)");

        chkUnifFornecedores.setText("Unificar fornecedores (Somente com CNPJ/CPF válido)");

        chkUnifProdutosFornecedores.setText("Unificar produtos fornecedores (Somente com EAN e com CNPJ/CPF válido)");

        chkUnifClientesPreferenciais.setText("Unificar clientes preferenciais (com CNPJ/CPF válido)");

        chkUnifClientesEventuais.setText("Unificar clientes eventuais (com CNPJ/CPF válido)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutosFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifProdutosFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(199, Short.MAX_VALUE))
        );

        tabOperacoes.addTab("Unificação", tabUnificacao);

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
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
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

        conexaoFirebird.setSistema("PWS");
        tabs.addTab("Conexão", conexaoFirebird);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabOperacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabOperacoes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFEndereco;
    private vrframework.bean.checkBox.VRCheckBox chkFNumero;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoEmp;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoForn;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientesEventuais;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientesPreferenciais;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutosFornecedores;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel conexaoFirebird;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.panel.VRPanel tabClientes;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private javax.swing.JTabbedPane tabImportacao;
    private javax.swing.JTabbedPane tabOperacoes;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JPanel tabUnificacao;
    private javax.swing.JTabbedPane tabs;
    private vrframework.bean.label.VRLabel vRLabel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void executar() throws Exception {
        tabProdutos.btnMapaTribut.setEnabled(true);

        gravarParametros();
        carregarLojaVR();
        carregarLojaCliente();
    }

}
