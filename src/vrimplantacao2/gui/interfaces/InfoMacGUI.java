package vrimplantacao2.gui.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import org.json.JSONArray;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoADS;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.InfoMacDAO;
import vrimplantacao2.parametro.Parametros;

public class InfoMacGUI extends VRInternalFrame {    
    
    private static final String NOME_SISTEMA = "InfoMac - Store";
    private static final String SERVIDOR_SQL = "ADS";
    private static InfoMacGUI instance;
    
    private final InfoMacDAO infoMacDAO = new InfoMacDAO();   
    private final ConexaoADS connSQL = new ConexaoADS();
    
    private int vLojaCliente = -1;
    private int vLojaVR = -1;
    private int vTipoVenda = -1;
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtHostADS.setText(params.getWithNull("192.168.0.205", NOME_SISTEMA, "HOST"));
        txtBancoDadosADS.setText(params.getWithNull("OMEGA", NOME_SISTEMA, "DATABASE"));
        txtPortaADS.setText(params.getWithNull("6262", NOME_SISTEMA, "PORTA"));
        txtUsuarioADS.setText(params.getWithNull("adssys", NOME_SISTEMA, "USUARIO"));
        txtSenhaADS.setText(params.getWithNull("147852", NOME_SISTEMA, "SENHA"));
        vLojaCliente = params.getInt(NOME_SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(NOME_SISTEMA, "LOJA_VR");
        vTipoVenda = params.getInt(NOME_SISTEMA, "TIPO_VENDA");        
        chkClClientes.setSelected(false);
        chkClEmpresas.setSelected(false);
        chkClFornecedores.setSelected(false);
        chkClTransp.setSelected(false);
        chkClAdminCard.setSelected(false);
        JSONArray array = new JSONArray(params.getWithNull("[]", NOME_SISTEMA, "OPCOES_CLIENTE"));
        for (int i = 0; i < array.length(); i++) {
            switch (array.getInt(i)) {
                case 0: chkClClientes.setSelected(true); break;
                case 1: chkClEmpresas.setSelected(true); break;
                case 2: chkClFornecedores.setSelected(true); break;
                case 3: chkClTransp.setSelected(true); break;
                case 4: chkClAdminCard.setSelected(true); break;
            }
        }
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtHostADS.getText(), NOME_SISTEMA, "HOST");
        params.put(txtBancoDadosADS.getText(), NOME_SISTEMA, "DATABASE");
        params.put(txtPortaADS.getText(), NOME_SISTEMA, "PORTA");
        params.put(txtUsuarioADS.getText(), NOME_SISTEMA, "USUARIO");
        params.put(txtSenhaADS.getText(), NOME_SISTEMA, "SENHA");
        ItemComboVO cliente = (ItemComboVO) cmbLojaCliente.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.id, NOME_SISTEMA, "LOJA_CLIENTE");
            vLojaCliente = cliente.id;
        }
        ItemComboVO tipoVenda = (ItemComboVO) cmbTipoVenda.getSelectedItem();
        if (tipoVenda != null) {
            params.put(tipoVenda.id, NOME_SISTEMA, "TIPO_VENDA");
            vTipoVenda = tipoVenda.id;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, NOME_SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        JSONArray array = new JSONArray();
        if (chkClClientes.isSelected()) { array.put(0); }
        if (chkClEmpresas.isSelected()) { array.put(1); }
        if (chkClFornecedores.isSelected()) { array.put(2); }
        if (chkClTransp.isSelected()) { array.put(3); }
        if (chkClAdminCard.isSelected()) { array.put(4); }
        params.put(array.toString(), NOME_SISTEMA, "OPCOES_CLIENTES");
        
        params.salvar();
    }
    

    private InfoMacGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        this.title = "Importação " + NOME_SISTEMA;
                
        cmbLojaCliente.setModel(new DefaultComboBoxModel());

        carregarParametros();
        
        centralizarForm();
        this.setMaximum(false);
        
        tabRotativo.pnlLista.setEnabled(false);
        tabRotativo.chkAtivar.setEnabled(false);
        
        tabCheque.pnlLista.setEnabled(false);
        tabCheque.chkAtivarCheque.setEnabled(false);

    }

    public void validarDadosAcessoADS() throws Exception {
        if (txtHostADS.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados " + SERVIDOR_SQL);
        }
        if (txtBancoDadosADS.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados " + SERVIDOR_SQL);
        }

        if (txtSenhaADS.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados " + SERVIDOR_SQL);
        }

        if (txtUsuarioADS.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + SERVIDOR_SQL);
        }
        
        ConexaoADS.abrirConexao(txtHostADS.getText(), txtPortaADS.getInt(), 
                txtBancoDadosADS.getText(), txtUsuarioADS.getText(), txtSenhaADS.getText());
        
        carregarLojaVR();
        carregarLojaCliente();
        carregarTipoVenda();
        
        gravarParametros();
        
        tabRotativo.chkAtivar.setEnabled(true);
        tabRotativo.setDao(infoMacDAO);
        
        tabCheque.chkAtivarCheque.setEnabled(true);
        tabCheque.setDao(infoMacDAO);
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
        cmbLojaCliente.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (ItemComboVO loja: infoMacDAO.getLojasCliente()) {
            cmbLojaCliente.addItem(loja);
            if (loja.id == vLojaCliente) {
                index = cont;
            }
            cont++;
        }
        cmbLojaCliente.setSelectedIndex(index);
    }
    
    public void carregarTipoVenda() throws Exception {
        cmbTipoVenda.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (ItemComboVO tipoVenda: infoMacDAO.getTipoVenda()) {
            cmbTipoVenda.addItem(tipoVenda);
            if (tipoVenda.id == vTipoVenda) {
                index = cont;
            }
            cont++;
        }
        cmbTipoVenda.setSelectedIndex(index);
    }
    
    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, idLojaCliente, balanca; ;            
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);                   
                    
                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    idLojaCliente = ((ItemComboVO) cmbLojaCliente.getSelectedItem()).id;                                        
                    int idTipoVenda = ((ItemComboVO) cmbTipoVenda.getSelectedItem()).id;                                        
                    
                    Importador importador = new Importador(infoMacDAO);
                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    
                    infoMacDAO.setImportarDeClientes(chkClClientes.isSelected());
                    infoMacDAO.setImportarDeEmpresas(chkClEmpresas.isSelected());
                    infoMacDAO.setImportarDeFornecedores(chkClFornecedores.isSelected());
                    infoMacDAO.setImportarDeTransportadoras(chkClTransp.isSelected());
                    infoMacDAO.setImportarDeAdminCartao(chkClAdminCard.isSelected());
                    infoMacDAO.setTipoVenda(idTipoVenda);
                    
                    if (tab.getSelectedIndex() == 0) {
                        if (chkFamiliaProduto.isSelected()) {                        
                            importador.importarFamiliaProduto();
                        }

                        if (chkMercadologico.isSelected()) {
                            importador.importarMercadologicoPorNiveis();
                        }

                        if (chkProdutos.isSelected()) {
                            importador.importarProduto(chkManterBalanca.isSelected());
                        }

                        {
                            List<OpcaoProduto> opcoes = new ArrayList<>();
                            if (chkT1Custo.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO);
                            }
                            if (chkT1Preco.isSelected()) {
                                opcoes.add(OpcaoProduto.PRECO);
                            }
                            if (chkT1Estoque.isSelected()) {
                                opcoes.add(OpcaoProduto.ESTOQUE);
                            }
                            if (chkT1PisCofins.isSelected()) {
                                opcoes.add(OpcaoProduto.PIS_COFINS);
                            }
                            if (chkT1NatReceita.isSelected()) {
                                opcoes.add(OpcaoProduto.NATUREZA_RECEITA);
                            }
                            if (chkT1ICMS.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS);
                            }
                            if (chkT1AtivoInativo.isSelected()) {
                                opcoes.add(OpcaoProduto.ATIVO);
                            }    
                            if (chkT1DescCompleta.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_COMPLETA);
                            }
                            if (chkT1DescReduzida.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_REDUZIDA);
                            }
                            if (chkT1DescGondola.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_GONDOLA);
                            }
                            if (chkT1ProdMercadologico.isSelected()) {
                                opcoes.add(OpcaoProduto.MERCADOLOGICO);
                            }                        
                            if (chkValidade.isSelected()) {
                                opcoes.add(OpcaoProduto.VALIDADE);
                            }
                            if (chkAtacado.isSelected()) {
                                opcoes.add(OpcaoProduto.ATACADO);
                            }
                            if (chkFamilia.isSelected()) {
                                opcoes.add(OpcaoProduto.FAMILIA);
                            }
                            if (chkTipoEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_EAN);
                            }
                            if (chkQtdEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_EAN);
                            }
                            if (chkQtdEmbCotacao.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_COTACAO);
                            }
                            if (chkMargem.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }
                            if (chkTipoEmbalagem.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarProdutos(opcoes);
                            }
                        }

                        if (chkT1EAN.isSelected()) {
                            importador.importarEAN();
                        }

                        if (chkT1EANemBranco.isSelected()) {
                            importador.importarEANemBranco();
                        }
                        
                    } else if (tab.getSelectedIndex() == 1) {
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }
                        
                        List<OpcaoFornecedor> opcoes = new ArrayList<>();
                        {
                            if (chkContatos.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                        }
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }

                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }
                    } else if (tab.getSelectedIndex() == 2) {
                        if (chkClientePreferencial.isSelected()) {                            
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        if (chkClienteEventual.isSelected()) {                            
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }                        
                        if (tabRotativo.isSelected()) {
                            infoMacDAO.setPlanoContaEntrada(tabRotativo.getPlanosSelecionados());
                            importador.importarCreditoRotativo();
                        }
                        if (tabCheque.isSelected()) {
                            infoMacDAO.setPlanoContaEntrada(tabCheque.getPlanosSelecionados());
                            importador.importarCheque();
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
                    }  else if (tab.getSelectedIndex() == 4) {
                        if (chkPdvVendas.isSelected()) {
                            infoMacDAO.setVendaDataInicio(txtDtIInicioVenda.getDate());
                            infoMacDAO.setVendaDataTermino(txtDtTerminoVenda.getDate());
                            
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    }                  
                    
                    gravarParametros();
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + NOME_SISTEMA + " realizada com sucesso!", getTitle());
                    
                    ConexaoADS.close();
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
                instance = new InfoMacGUI(i_mdiFrame);
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
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabDados = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Custo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Preco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Estoque = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EAN = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EANemBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1PisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkT1NatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkT1AtivoInativo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescReduzida = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ProdMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagem = new vrframework.bean.checkBox.VRCheckBox();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkContatos = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        cbxUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifProdutoForn = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliPreferencial = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabVendas = new vrframework.bean.panel.VRPanel();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        txtDtIInicioVenda = new org.jdesktop.swingx.JXDatePicker();
        txtDtTerminoVenda = new org.jdesktop.swingx.JXDatePicker();
        tabCliente = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkClClientes = new javax.swing.JCheckBox();
        chkClEmpresas = new javax.swing.JCheckBox();
        chkClFornecedores = new javax.swing.JCheckBox();
        chkClTransp = new javax.swing.JCheckBox();
        chkClAdminCard = new javax.swing.JCheckBox();
        tabRotativo = new vrimplantacao2.gui.interfaces.custom.infomac.InfoMacPlanoContasRotativoGUI();
        tabCheque = new vrimplantacao2.gui.interfaces.custom.infomac.InfoMacPlanoContasChequeGUI();
        vRTabbedPane1 = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlConexao = new vrframework.bean.panel.VRPanel();
        txtUsuarioADS = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaADS = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaADS = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostADS = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarADS = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtBancoDadosADS = new vrframework.bean.textField.VRTextField();
        cmbLojaCliente = new vrframework.bean.comboBox.VRComboBox();
        cmbTipoVenda = new vrframework.bean.comboBox.VRComboBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setTitle("Arius");
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        chkFamiliaProduto.setText("Familia Produto");
        chkFamiliaProduto.setEnabled(true);
        chkFamiliaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaProdutoActionPerformed(evt);
            }
        });

        chkMercadologico.setText("Mercadologico");
        chkMercadologico.setEnabled(true);

        chkProdutos.setText("Produtos");
        chkProdutos.setEnabled(true);

        chkT1Custo.setText("Custo");

        chkT1Preco.setText("Preço");

        chkT1Estoque.setText("Estoque");

        chkQtdEmbCotacao.setText("Qtd. Emb. (Cotação)");

        chkManterBalanca.setText("Manter Balança");
        chkManterBalanca.setEnabled(true);

        chkT1EAN.setText("EAN");

        chkT1EANemBranco.setText("EAN em branco");

        chkT1PisCofins.setText("PIS/COFINS");

        chkT1NatReceita.setText("Nat. Receita");

        chkT1ICMS.setText("ICMS");

        chkT1AtivoInativo.setText("Ativo/Inativo");

        chkT1DescCompleta.setText("Descrição Completa");

        chkT1DescReduzida.setText("Descrição Reduzida");

        chkT1DescGondola.setText("Descrição Gondola");

        chkT1ProdMercadologico.setText("Prod. Mercadológico");

        chkValidade.setText("Validade");

        chkAtacado.setText("Atacado");

        chkFamilia.setText("Família");

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");

        chkMargem.setText("Margem");

        chkTipoEmbalagem.setText("Tp. Emb. (Produto)");

        javax.swing.GroupLayout tabDadosLayout = new javax.swing.GroupLayout(tabDados);
        tabDados.setLayout(tabDadosLayout);
        tabDadosLayout.setHorizontalGroup(
            tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkQtdEmbCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        tabDadosLayout.setVerticalGroup(
            tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(tabDadosLayout.createSequentialGroup()
                            .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkQtdEmbCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        tab.addTab("Dados", tabDados);

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

        chkContatos.setText("Contatos");
        chkContatos.setEnabled(true);
        chkContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkContatosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(455, Short.MAX_VALUE))
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(158, Short.MAX_VALUE))
        );

        tab.addTab("Fornecedores", tabFornecedor);

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
                .addContainerGap(304, Short.MAX_VALUE))
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
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tab.addTab("Unificação", tabUnificacao);

        chkPdvVendas.setText("Vendas (PDV)");
        chkPdvVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPdvVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDtIInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDtTerminoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(322, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtIInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtTerminoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(177, Short.MAX_VALUE))
        );

        tab.addTab("Vendas", tabVendas);

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

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Obter clientes de"));
        vRPanel1.setLayout(new java.awt.GridLayout(5, 1));

        chkClClientes.setSelected(true);
        chkClClientes.setText("Clientes");
        vRPanel1.add(chkClClientes);

        chkClEmpresas.setText("Empresas");
        vRPanel1.add(chkClEmpresas);

        chkClFornecedores.setText("Fornecedores");
        vRPanel1.add(chkClFornecedores);

        chkClTransp.setText("Transportadora");
        vRPanel1.add(chkClTransp);

        chkClAdminCard.setText("Administradora de Cartão");
        vRPanel1.add(chkClAdminCard);

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(207, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabCliente.addTab("Dados", tabClienteDados);
        tabCliente.addTab("Crédito Rotativo", tabRotativo);
        tabCliente.addTab("Cheque", tabCheque);

        tab.addTab("Clientes", tabCliente);

        pnlConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - ADS"));
        pnlConexao.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioADS.setCaixaAlta(false);
        txtUsuarioADS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioADSActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenhaADS.setCaixaAlta(false);
        txtSenhaADS.setMascara("");
        txtSenhaADS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaADSActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaADS.setText("1521");
        txtPortaADS.setCaixaAlta(false);
        txtPortaADS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaADSActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostADS.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarADS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarADS.setText("Conectar");
        btnConectarADS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarADSActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtBancoDadosADS.setCaixaAlta(false);

        cmbLojaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaClienteActionPerformed(evt);
            }
        });

        vRLabel8.setText("Tipo Venda");

        javax.swing.GroupLayout pnlConexaoLayout = new javax.swing.GroupLayout(pnlConexao);
        pnlConexao.setLayout(pnlConexaoLayout);
        pnlConexaoLayout.setHorizontalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPortaADS, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(cmbLojaCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnConectarADS, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 197, Short.MAX_VALUE))
                            .addComponent(txtHostADS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBancoDadosADS, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuarioADS, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaADS, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
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
                    .addComponent(txtSenhaADS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuarioADS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBancoDadosADS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHostADS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPortaADS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarADS)))
        );

        vRTabbedPane1.addTab("Conexão", pnlConexao);
        pnlConexao.getAccessibleContext().setAccessibleName("Dados Origem - ADS");

        vRTabbedPane1.addTab("Importar Balança", pnlBalanca);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Info Mac - Store");

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

    private void txtUsuarioADSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioADSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioADSActionPerformed

    private void txtSenhaADSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaADSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaADSActionPerformed

    private void txtPortaADSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaADSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaADSActionPerformed

    private void btnConectarADSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarADSActionPerformed
        try {
            this.setWaitCursor();

            if (connSQL != null) {
                ConexaoADS.close();
            }

            validarDadosAcessoADS();
            btnConectarADS.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarADSActionPerformed

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void chkFamiliaProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaProdutoActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void chkProdutoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoFornecedorActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClienteEventualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClienteEventualActionPerformed

    private void chkPdvVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasActionPerformed
        if (txtDtIInicioVenda.getDate() == null) {
            txtDtIInicioVenda.setDate(new Date(System.currentTimeMillis()));
        }
        if (txtDtTerminoVenda.getDate() == null) {
            txtDtTerminoVenda.setDate(new Date(System.currentTimeMillis()));
        }
    }//GEN-LAST:event_chkPdvVendasActionPerformed

    private void cmbLojaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaClienteActionPerformed
        pnlBalanca.setSistema("Info Mac - Store");
        pnlBalanca.setLoja(String.valueOf(((ItemComboVO) cmbLojaCliente.getSelectedItem()).id));
    }//GEN-LAST:event_cmbLojaClienteActionPerformed

    private void chkContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkContatosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarADS;
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliEventual;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliPreferencial;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutoForn;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkAtacado;
    private javax.swing.JCheckBox chkClAdminCard;
    private javax.swing.JCheckBox chkClClientes;
    private javax.swing.JCheckBox chkClEmpresas;
    private javax.swing.JCheckBox chkClFornecedores;
    private javax.swing.JCheckBox chkClTransp;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkMargem;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbCotacao;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkT1AtivoInativo;
    private vrframework.bean.checkBox.VRCheckBox chkT1Custo;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescCompleta;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescGondola;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescReduzida;
    private vrframework.bean.checkBox.VRCheckBox chkT1EAN;
    private vrframework.bean.checkBox.VRCheckBox chkT1EANemBranco;
    private vrframework.bean.checkBox.VRCheckBox chkT1Estoque;
    private vrframework.bean.checkBox.VRCheckBox chkT1ICMS;
    private vrframework.bean.checkBox.VRCheckBox chkT1NatReceita;
    private vrframework.bean.checkBox.VRCheckBox chkT1PisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkT1Preco;
    private vrframework.bean.checkBox.VRCheckBox chkT1ProdMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagem;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private vrframework.bean.comboBox.VRComboBox cmbLojaCliente;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrframework.bean.comboBox.VRComboBox cmbTipoVenda;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlConexao;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrimplantacao2.gui.interfaces.custom.infomac.InfoMacPlanoContasChequeGUI tabCheque;
    private vrframework.bean.tabbedPane.VRTabbedPane tabCliente;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.panel.VRPanel tabDados;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrimplantacao2.gui.interfaces.custom.infomac.InfoMacPlanoContasRotativoGUI tabRotativo;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.textField.VRTextField txtBancoDadosADS;
    private org.jdesktop.swingx.JXDatePicker txtDtIInicioVenda;
    private org.jdesktop.swingx.JXDatePicker txtDtTerminoVenda;
    private vrframework.bean.textField.VRTextField txtHostADS;
    private vrframework.bean.textField.VRTextField txtPortaADS;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaADS;
    private vrframework.bean.textField.VRTextField txtUsuarioADS;
    private vrframework.bean.consultaContaContabil.VRConsultaContaContabil vRConsultaContaContabil1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
