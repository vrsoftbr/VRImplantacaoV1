package vrimplantacao2.gui.interfaces;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.UniplusDAO;
import vrimplantacao2.parametro.Parametros;

public class UniplusGUI extends VRInternalFrame {    
    
    private static final String NOME_SISTEMA = "Uniplus";
    private static final String SERVIDOR_SQL = "Postgres";
    private static UniplusGUI instance;
    
    private final UniplusDAO uniplusDAO = new UniplusDAO();
    private final ConexaoPostgres connSQL = new ConexaoPostgres();
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, NOME_SISTEMA);
        txtHostPostgres.setText(params.getWithNull("localhost", NOME_SISTEMA, "HOST"));
        txtBancoDadosPostgres.setText(params.getWithNull("unico", NOME_SISTEMA, "DATABASE"));
        txtPortaPostgres.setText(params.getWithNull("5432", NOME_SISTEMA, "PORTA"));
        txtUsuarioPostgres.setText(params.getWithNull("postgres", NOME_SISTEMA, "USUARIO"));
        txtSenhaPostgres.setText(params.getWithNull("620568", NOME_SISTEMA, "SENHA"));
        vLojaCliente = params.get(NOME_SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(NOME_SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, NOME_SISTEMA);
        params.put(txtHostPostgres.getText(), NOME_SISTEMA, "HOST");
        params.put(txtBancoDadosPostgres.getText(), NOME_SISTEMA, "DATABASE");
        params.put(txtPortaPostgres.getText(), NOME_SISTEMA, "PORTA");
        params.put(txtUsuarioPostgres.getText(), NOME_SISTEMA, "USUARIO");
        params.put(txtSenhaPostgres.getText(), NOME_SISTEMA, "SENHA");
        Estabelecimento cliente = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.cnpj, NOME_SISTEMA, "LOJA_CLIENTE");
            vLojaCliente = cliente.cnpj;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, NOME_SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }
    
    private UniplusGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        this.title = "Importação " + NOME_SISTEMA;
                
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        carregarParametros();
        
        tabProdutos.setOpcoesDisponiveis(uniplusDAO);
        tabProdutos.tabParametros.add(pnlCustom);
        
        centralizarForm();
        this.setMaximum(false);  
    }

    public void validarDadosAcessoPostgres() throws Exception {
        if (txtHostPostgres.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados " + SERVIDOR_SQL);
        }
        if (txtBancoDadosPostgres.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados " + SERVIDOR_SQL);
        }

        if (txtSenhaPostgres.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados " + SERVIDOR_SQL);
        }

        if (txtUsuarioPostgres.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + SERVIDOR_SQL);
        }
        
        connSQL.abrirConexao(txtHostPostgres.getText(), txtPortaPostgres.getInt(), 
                txtBancoDadosPostgres.getText(), txtUsuarioPostgres.getText(), txtSenhaPostgres.getText());
        
        carregarLojaVR();
        carregarLojaCliente();
        gravarParametros();
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
        for (Estabelecimento loja: uniplusDAO.getLojas()) {
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
            String lojaMesmoID;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);                   
                    
                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;
                    
                    uniplusDAO.setPrefixoAtacado(Utils.stringToInt(txtIDAtacado.getText(), 999));                     
                    uniplusDAO.setComplemento(txtLojaID.getText());
                    
                    Importador importador = new Importador(uniplusDAO);
                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    tabProdutos.setImportador(importador);
                    
                    if (tab.getSelectedIndex() == 0) {
                        tabProdutos.executarImportacao();
                    } else if (tab.getSelectedIndex() == 1) {
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }
                    } else if (tab.getSelectedIndex() == 2) {
                        if (chkClientePreferencial.isSelected()) {                            
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS, OpcaoCliente.VALOR_LIMITE, OpcaoCliente.SITUACAO_CADASTRO);
                        }
                        if (chkClienteEventual.isSelected()) {                            
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        
                        if (chkLimite.isSelected()) {
                            importador.atualizarClientePreferencial(OpcaoCliente.VALOR_LIMITE);
                        }
                        
                         if(chkRotativo.isSelected()){
                            importador.importarCreditoRotativo();
                        }
                        
                    } else if (tab.getSelectedIndex() == 3) {
                        if (cbxUnifProdutos.isSelected()) {  
                            importador.unificarProdutos();
                        }
                        if (cbxUnifFornecedores.isSelected()) {  
                            importador.unificarFornecedor();
                        }
                        if (cbxUnifProdutoForn.isSelected()) {  
                            importador.unificarProdutoFornecedor();
                        }
                        if (cbxUnifCliPreferencial.isSelected()) {  
                            importador.unificarClientePreferencial();
                        }
                        if (cbxUnifCliEventual.isSelected()) {  
                            importador.unificarClienteEventual();
                        }
                    }                    
                    gravarParametros();
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + NOME_SISTEMA + " realizada com sucesso!", getTitle());
                    
                    connSQL.close();
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
                instance = new UniplusGUI(i_mdiFrame);
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
        pnlCustom = new vrframework.bean.panel.VRPanel();
        txtIDAtacado = new javax.swing.JTextField();
        lblAtacadoID = new vrframework.bean.label.VRLabel();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabCliente = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkLimite = new vrframework.bean.checkBox.VRCheckBox();
        tablCreditoRotativo = new javax.swing.JPanel();
        chkRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        cmbPlanoContas = new vrframework.bean.comboBox.VRComboBox();
        lblPlanoContas = new vrframework.bean.label.VRLabel();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        cbxUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifProdutoForn = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliPreferencial = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRTabbedPane1 = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlConexao = new vrframework.bean.panel.VRPanel();
        txtUsuarioPostgres = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaPostgres = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaPostgres = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostPostgres = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarPostgres = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtBancoDadosPostgres = new vrframework.bean.textField.VRTextField();
        cmbLojaOrigem = new javax.swing.JComboBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        txtLojaID = new vrframework.bean.textField.VRTextField();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();

        txtIDAtacado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIDAtacado.setText("999");

        lblAtacadoID.setText("Prefixo Atacado");

        javax.swing.GroupLayout pnlCustomLayout = new javax.swing.GroupLayout(pnlCustom);
        pnlCustom.setLayout(pnlCustomLayout);
        pnlCustomLayout.setHorizontalGroup(
            pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblAtacadoID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtIDAtacado))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCustomLayout.setVerticalGroup(
            pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAtacadoID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIDAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

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

        tab.addTab("Produtos", tabProdutos);

        tabFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.setEnabled(true);
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");
        chkProdutoFornecedor.setEnabled(true);
        chkProdutoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoFornecedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tab.addTab("Fornecedores", tabFornecedor);

        tabClienteDados.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkClientePreferencial.setText("Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);
        chkClientePreferencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferencialActionPerformed(evt);
            }
        });

        chkClienteEventual.setText("Cliente Eventual");
        chkClienteEventual.setEnabled(true);
        chkClienteEventual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClienteEventualActionPerformed(evt);
            }
        });

        chkLimite.setText("Limite");

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(393, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabCliente.addTab("Dados", tabClienteDados);

        chkRotativo.setText("Crédito Rotativo");

        chkCheque.setText("Cheque");

        lblPlanoContas.setText("Plano de Contas");

        javax.swing.GroupLayout tablCreditoRotativoLayout = new javax.swing.GroupLayout(tablCreditoRotativo);
        tablCreditoRotativo.setLayout(tablCreditoRotativoLayout);
        tablCreditoRotativoLayout.setHorizontalGroup(
            tablCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablCreditoRotativoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tablCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPlanoContas, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPlanoContas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(357, Short.MAX_VALUE))
        );
        tablCreditoRotativoLayout.setVerticalGroup(
            tablCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablCreditoRotativoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPlanoContas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPlanoContas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tabCliente.addTab("Crédito Rotativo", tablCreditoRotativo);

        tab.addTab("Clientes", tabCliente);

        cbxUnifProdutos.setText("Unificar produtos (Somente EANs válidos)");

        cbxUnifFornecedores.setText("Unificar Fornecedores (Somente CPF/CNPJ válidos)");

        cbxUnifProdutoForn.setText("Unificar Produto Fornecedor (Somente CPF/CNPJ válidos)");

        cbxUnifCliPreferencial.setText("Unificar Cliente Preferencial (Somente CPF/CNPJ válidos)");

        cbxUnifCliEventual.setText("Unificar Cliente Eventual (Somente CPF/CNPJ válidos)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifProdutoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifProdutoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tab.addTab("Unificação", tabUnificacao);

        pnlConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Postgres"));
        pnlConexao.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioPostgres.setCaixaAlta(false);
        txtUsuarioPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioPostgresActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenhaPostgres.setCaixaAlta(false);
        txtSenhaPostgres.setMascara("");
        txtSenhaPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaPostgresActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaPostgres.setText("1521");
        txtPortaPostgres.setCaixaAlta(false);
        txtPortaPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaPostgresActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostPostgres.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarPostgres.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarPostgres.setText("Conectar");
        btnConectarPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarPostgresActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtBancoDadosPostgres.setCaixaAlta(false);

        vRLabel8.setText("Loja ID");

        txtLojaID.setCaixaAlta(false);
        txtLojaID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLojaIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlConexaoLayout = new javax.swing.GroupLayout(pnlConexao);
        pnlConexao.setLayout(pnlConexaoLayout);
        pnlConexaoLayout.setHorizontalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addComponent(txtPortaPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLojaID, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectarPostgres))
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlConexaoLayout.createSequentialGroup()
                                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtHostPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtBancoDadosPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUsuarioPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSenhaPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(115, Short.MAX_VALUE))
        );
        pnlConexaoLayout.setVerticalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtSenhaPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuarioPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBancoDadosPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHostPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtPortaPostgres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarPostgres)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        vRTabbedPane1.addTab("Conexão", pnlConexao);
        vRTabbedPane1.addTab("Importar Balança", pnlBalanca);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

    private void txtUsuarioPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioPostgresActionPerformed
        
    }//GEN-LAST:event_txtUsuarioPostgresActionPerformed

    private void txtSenhaPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaPostgresActionPerformed
        
    }//GEN-LAST:event_txtSenhaPostgresActionPerformed

    private void txtPortaPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaPostgresActionPerformed
        
    }//GEN-LAST:event_txtPortaPostgresActionPerformed

    private void btnConectarPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarPostgresActionPerformed
        try {
            this.setWaitCursor();

            if (connSQL != null) {
                connSQL.close();
            }

            validarDadosAcessoPostgres();
            btnConectarPostgres.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarPostgresActionPerformed

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void chkClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClienteEventualActionPerformed
        
    }//GEN-LAST:event_chkClienteEventualActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkProdutoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoFornecedorActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void txtLojaIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLojaIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLojaIDActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarPostgres;
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliEventual;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliPreferencial;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutoForn;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkLimite;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkRotativo;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrframework.bean.comboBox.VRComboBox cmbPlanoContas;
    private vrframework.bean.label.VRLabel lblAtacadoID;
    private vrframework.bean.label.VRLabel lblPlanoContas;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlConexao;
    private vrframework.bean.panel.VRPanel pnlCustom;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrframework.bean.tabbedPane.VRTabbedPane tabCliente;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private javax.swing.JPanel tablCreditoRotativo;
    private vrframework.bean.textField.VRTextField txtBancoDadosPostgres;
    private vrframework.bean.textField.VRTextField txtHostPostgres;
    private javax.swing.JTextField txtIDAtacado;
    private vrframework.bean.textField.VRTextField txtLojaID;
    private vrframework.bean.textField.VRTextField txtPortaPostgres;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaPostgres;
    private vrframework.bean.textField.VRTextField txtUsuarioPostgres;
    private vrframework.bean.consultaContaContabil.VRConsultaContaContabil vRConsultaContaContabil1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
