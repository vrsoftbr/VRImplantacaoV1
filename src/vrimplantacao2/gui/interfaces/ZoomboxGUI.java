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
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.ZoomboxDAO;
import vrimplantacao2.parametro.Parametros;

public class ZoomboxGUI extends VRInternalFrame {    
    
    private static final String NOME_SISTEMA = "Uniplus";
    private static final String SERVIDOR_SQL = "Postgres";
    private static ZoomboxGUI instance;
    
    private final ZoomboxDAO dao = new ZoomboxDAO();
    private final ConexaoPostgres connSQL = new ConexaoPostgres();
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtHostPostgres.setText(params.getWithNull("localhost", NOME_SISTEMA, "HOST"));
        txtBancoDadosPostgres.setText(params.getWithNull("unico", NOME_SISTEMA, "DATABASE"));
        txtPortaPostgres.setText(params.getWithNull("5432", NOME_SISTEMA, "PORTA"));
        txtUsuarioPostgres.setText(params.getWithNull("postgres", NOME_SISTEMA, "USUARIO"));
        txtSenhaPostgres.setText(params.getWithNull("620568", NOME_SISTEMA, "SENHA"));
        txtIDAtacado.setText(params.getWithNull("999", NOME_SISTEMA, "PREFIXO_ATACADO"));
        chkForcarIdProdutoQuandoPesavel.setSelected(params.getBool(NOME_SISTEMA, "FORCAR_ID_PLU"));
        vLojaCliente = params.get(NOME_SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(NOME_SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtHostPostgres.getText(), NOME_SISTEMA, "HOST");
        params.put(txtBancoDadosPostgres.getText(), NOME_SISTEMA, "DATABASE");
        params.put(txtPortaPostgres.getText(), NOME_SISTEMA, "PORTA");
        params.put(txtUsuarioPostgres.getText(), NOME_SISTEMA, "USUARIO");
        params.put(txtSenhaPostgres.getText(), NOME_SISTEMA, "SENHA");
        params.put(txtIDAtacado.getText(), NOME_SISTEMA, "PREFIXO_ATACADO");
        params.put(chkForcarIdProdutoQuandoPesavel.isSelected(), NOME_SISTEMA, "FORCAR_ID_PLU");
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
    
    private ZoomboxGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        this.title = "Importação " + NOME_SISTEMA;
                
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        carregarParametros();
        
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
            String lojaMesmoID;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);                   
                    
                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;
                 
                    dao.setComplemento(txtLojaID.getText());
                    
                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    
                    if (tab.getSelectedIndex() == 0) {                        
                        if (chkClientePreferencial.isSelected()) {                            
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS, OpcaoCliente.VALOR_LIMITE, OpcaoCliente.SITUACAO_CADASTRO);
                        }
                        if (chkClienteEventual.isSelected()) {                            
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                    } else if (tab.getSelectedIndex() == 1) {
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
                instance = new ZoomboxGUI(i_mdiFrame);
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
        chkForcarIdProdutoQuandoPesavel = new vrframework.bean.checkBox.VRCheckBox();
        pnlOutras = new vrframework.bean.panel.VRPanel();
        chkVendas = new vrframework.bean.checkBox.VRCheckBox();
        edtVendaDtIni = new org.jdesktop.swingx.JXDatePicker();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
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

        chkForcarIdProdutoQuandoPesavel.setText("Forçar ID como PLU nos produtos pesáveis");

        javax.swing.GroupLayout pnlCustomLayout = new javax.swing.GroupLayout(pnlCustom);
        pnlCustom.setLayout(pnlCustomLayout);
        pnlCustomLayout.setHorizontalGroup(
            pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblAtacadoID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtIDAtacado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkForcarIdProdutoQuandoPesavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );
        pnlCustomLayout.setVerticalGroup(
            pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAtacadoID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkForcarIdProdutoQuandoPesavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkVendas.setText("Vendas");
        chkVendas.setEnabled(true);
        chkVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOutrasLayout = new javax.swing.GroupLayout(pnlOutras);
        pnlOutras.setLayout(pnlOutrasLayout);
        pnlOutrasLayout.setHorizontalGroup(
            pnlOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutrasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtVendaDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(395, Short.MAX_VALUE))
        );
        pnlOutrasLayout.setVerticalGroup(
            pnlOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutrasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtVendaDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(157, Short.MAX_VALUE))
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

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(469, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(170, Short.MAX_VALUE))
        );

        tab.addTab("Clientes", tabClienteDados);

        cbxUnifCliPreferencial.setText("Unificar Cliente Preferencial (Somente CPF/CNPJ válidos)");

        cbxUnifCliEventual.setText("Unificar Cliente Eventual (Somente CPF/CNPJ válidos)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(170, Short.MAX_VALUE))
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
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void txtLojaIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLojaIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLojaIDActionPerformed

    private void chkVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkVendasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarPostgres;
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliEventual;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliPreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkForcarIdProdutoQuandoPesavel;
    private vrframework.bean.checkBox.VRCheckBox chkVendas;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private org.jdesktop.swingx.JXDatePicker edtVendaDtIni;
    private vrframework.bean.label.VRLabel lblAtacadoID;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlConexao;
    private vrframework.bean.panel.VRPanel pnlCustom;
    private vrframework.bean.panel.VRPanel pnlOutras;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.panel.VRPanel tabUnificacao;
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
