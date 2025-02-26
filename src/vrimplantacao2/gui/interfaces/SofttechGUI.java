package vrimplantacao2.gui.interfaces;

import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.SofttechDAO;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.parametro.Parametros;

public class SofttechGUI extends VRInternalFrame implements ConexaoEvent {
    
    private static SofttechGUI instance;    
    private final SofttechDAO dao = new SofttechDAO();
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, "PRODUTOS");
        vLojaCliente = params.get(SofttechDAO.NOME_SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SofttechDAO.NOME_SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, "PRODUTOS");
        Estabelecimento cliente = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.cnpj, SofttechDAO.NOME_SISTEMA, "LOJA_CLIENTE");
            vLojaCliente = cliente.cnpj;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SofttechDAO.NOME_SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }
    
    private SofttechGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        /**
         * fantastsoftace
         * FaN_951753_@aCCe# 
         */
        conexao.host = ("localhost");
        conexao.database = ("fs-gerencial");
        conexao.user = ("fantastsoft");
        conexao.pass = ("fan_951753");
        conexao.port = ("5432");
        
        conexao.setSistema(SofttechDAO.NOME_SISTEMA);
        conexao.setOnConectar(this); 
        conexao.carregarParametros();
        
        tabProdutos.setOpcoesDisponiveis(dao);
        
        carregarParametros();
        
        this.title = "Importação " + SofttechDAO.NOME_SISTEMA;
                
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        vRImportaArquivBalancaPanel1.setSistema("Sofftech");
        vRImportaArquivBalancaPanel1.setLoja("1");
        centralizarForm();
        this.setMaximum(false);  
    }

    private void carregarLojaVR() throws Exception {
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
    
    private void carregarLojaCliente() throws Exception {
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (Estabelecimento loja: dao.getLojas()) {
            cmbLojaOrigem.addItem(loja);
            if (vLojaCliente != null && vLojaCliente.equals(loja.cnpj)) {
                index = cont;
            }
            cont++;
        }
        cmbLojaOrigem.setSelectedIndex(index);
    }
    
    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, balanca;
            String idLojaCliente;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);                   
                    
                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;
                    
                    String lojaAuxiliar = "";
                    if((!"".equals(txtLojaAux.getText())) && (!txtLojaAux.getText().isEmpty())) {
                        lojaAuxiliar = txtLojaAux.getText();
                    }
                    
                    dao.auxiliar = lojaAuxiliar;
                    
                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    tabProdutos.setImportador(importador);
                    
                    if (tabs.getSelectedIndex() == 0) {
                        tabProdutos.executarImportacao();
                    } else if (tabs.getSelectedIndex() == 1) {
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor(OpcaoFornecedor.DADOS, OpcaoFornecedor.CONTATOS);
                        }
                        if (chkFornecedorContato.isSelected()) {
                            importador.atualizarFornecedor(OpcaoFornecedor.CONTATOS);
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }
                        if (chkContasPagar.isSelected()) {
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                    } else if (tabs.getSelectedIndex() == 2) {
                        if(chkUnificacaoProduto.isSelected()) {
                            importador.unificarProdutos();
                        }
                        if(chkUnificacaoProdForn.isSelected()) {
                            importador.unificarProdutoFornecedor();
                        }
                        if(chkUnificacaoCliente.isSelected()) {
                            importador.unificarClientePreferencial();
                        }
                        if(chkUnificacaoCliEventual.isSelected()) {
                            importador.unificarClienteEventual();
                        }
                        if(chkUnificacaoForn.isSelected()) {
                            importador.unificarFornecedor();
                        }
                    }
                    gravarParametros();
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + SofttechDAO.NOME_SISTEMA + " realizada com sucesso!", getTitle());                  
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
                instance = new SofttechGUI(i_mdiFrame);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        vRConsultaContaContabil1 = new vrframework.bean.consultaContaContabil.VRConsultaContaContabil();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedorCliente = new vrframework.bean.panel.VRPanel();
        pnlFornecedores = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedorContato = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkContasPagar = new vrframework.bean.checkBox.VRCheckBox();
        pnlClientes = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        chkUnificacaoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkUnificacaoProdForn = new vrframework.bean.checkBox.VRCheckBox();
        chkUnificacaoCliente = new vrframework.bean.checkBox.VRCheckBox();
        chkUnificacaoForn = new vrframework.bean.checkBox.VRCheckBox();
        chkUnificacaoCliEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabBalanca = new vrframework.bean.panel.VRPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        conexao = new vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel();
        lblLojaAux = new vrframework.bean.label.VRLabel();
        txtLojaAux = new vrframework.bean.textField.VRTextField();

        setTitle("Uniplus");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
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

        vRLabel6.setText("Loja:");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabs.addTab("Produtos", tabProdutos);

        pnlFornecedores.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedor"));

        chkFornecedor.setText("Dados");

        chkFornecedorContato.setText("Contatos");

        chkProdutoFornecedor.setText("Produto Fornecedor");

        chkContasPagar.setText("Contas à pagar");

        javax.swing.GroupLayout pnlFornecedoresLayout = new javax.swing.GroupLayout(pnlFornecedores);
        pnlFornecedores.setLayout(pnlFornecedoresLayout);
        pnlFornecedoresLayout.setHorizontalGroup(
            pnlFornecedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFornecedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornecedorContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
        );
        pnlFornecedoresLayout.setVerticalGroup(
            pnlFornecedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkFornecedorContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlClientes.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        chkClientePreferencial.setText("Preferenciais");

        chkClienteEventual.setText("Eventuais");

        chkCreditoRotativo.setText("Crédito Rotativo");

        javax.swing.GroupLayout pnlClientesLayout = new javax.swing.GroupLayout(pnlClientes);
        pnlClientes.setLayout(pnlClientesLayout);
        pnlClientesLayout.setHorizontalGroup(
            pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClientesLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlClientesLayout.setVerticalGroup(
            pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesLayout.createSequentialGroup()
                .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout tabFornecedorClienteLayout = new javax.swing.GroupLayout(tabFornecedorCliente);
        tabFornecedorCliente.setLayout(tabFornecedorClienteLayout);
        tabFornecedorClienteLayout.setHorizontalGroup(
            tabFornecedorClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFornecedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabFornecedorClienteLayout.setVerticalGroup(
            tabFornecedorClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(171, Short.MAX_VALUE))
        );

        tabs.addTab("Fornecedor e Cliente", tabFornecedorCliente);

        chkUnificacaoProduto.setText("Produtos (Somente com EAN válido)");

        chkUnificacaoProdForn.setText("Produto Fornecedor (Somente com CPF/CNPJ válido)");

        chkUnificacaoCliente.setText("Cliente Preferencial (Somente com CPF/CNPJ válido)");

        chkUnificacaoForn.setText("Fornecedor (Somente com CPF/CNPJ válido)");

        chkUnificacaoCliEventual.setText("Cliente Eventual (Somente com CPF/CNPJ válido)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnificacaoProdForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnificacaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnificacaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnificacaoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnificacaoCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(287, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnificacaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkUnificacaoProdForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnificacaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnificacaoCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnificacaoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", tabUnificacao);

        vRImportaArquivBalancaPanel1.setSistema("GetWay");

        javax.swing.GroupLayout tabBalancaLayout = new javax.swing.GroupLayout(tabBalanca);
        tabBalanca.setLayout(tabBalancaLayout);
        tabBalancaLayout.setHorizontalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );
        tabBalancaLayout.setVerticalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(198, Short.MAX_VALUE))
        );

        tabs.addTab("Balança", tabBalanca);

        vRLabel1.setText("Loja (Cliente):");

        lblLojaAux.setText("Loja Aux:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblLojaAux, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLojaAux, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(conexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLojaAux, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaAux, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkContasPagar;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedorContato;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnificacaoCliEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnificacaoCliente;
    private vrframework.bean.checkBox.VRCheckBox chkUnificacaoForn;
    private vrframework.bean.checkBox.VRCheckBox chkUnificacaoProdForn;
    private vrframework.bean.checkBox.VRCheckBox chkUnificacaoProduto;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel conexao;
    private vrframework.bean.label.VRLabel lblLojaAux;
    private vrframework.bean.panel.VRPanel pnlClientes;
    private vrframework.bean.panel.VRPanel pnlFornecedores;
    private vrframework.bean.panel.VRPanel tabBalanca;
    private vrframework.bean.panel.VRPanel tabFornecedorCliente;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.textField.VRTextField txtLojaAux;
    private vrframework.bean.consultaContaContabil.VRConsultaContaContabil vRConsultaContaContabil1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.panel.VRPanel vRPanel3;
    // End of variables declaration//GEN-END:variables

    @Override
    public void executar() throws Exception {
        carregarParametros();
        carregarLojaVR();
        carregarLojaCliente();
    }

}
