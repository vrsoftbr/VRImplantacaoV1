package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.list.VRList;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.notafiscal.OpcaoNotaFiscal;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.SolidusDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.gui.interfaces.custom.solidus.Entidade;
import vrimplantacao2.gui.interfaces.custom.solidus.EntidadeListModel;
import vrimplantacao2.parametro.Parametros;

public class SolidusGUI extends VRInternalFrame {
    
    private static final Logger LOG = Logger.getLogger(SolidusGUI.class.getName());
    
    public static final String SISTEMA = "Solidus";
    private static final String SERVIDOR_SQL = "Firebird";
    private static SolidusGUI instance;
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        chkEliminarDigito.setSelected(params.getBool("localhost", SISTEMA, "ELIMINAR_DIGITO"));
        txtHost.setText(params.getWithNull("localhost", SISTEMA, "HOST"));
        txtDatabase.setArquivo(params.getWithNull("D:\\DADOS\\Gestao.FDB", SISTEMA, "DATABASE"));
        txtPorta.setText(params.getWithNull("3050", SISTEMA, "PORTA"));
        txtUsuario.setText(params.getWithNull("SYSDBA", SISTEMA, "USUARIO"));
        txtSenha.setText(params.getWithNull("masterkey", SISTEMA, "SENHA"));
        txtSistema.setText(params.getWithNull("Solidus", SISTEMA, "SISTEMA"));
        txtReiniciarID.setText(params.getWithNull("1", SISTEMA, "N_REINICIO"));
        edtDtNotaIni.setDate(params.getDate(SISTEMA, "DATA_NOTAS"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(chkEliminarDigito.isSelected(), SISTEMA, "ELIMINAR_DIGITO");
        params.put(txtHost.getText(), SISTEMA, "HOST");
        params.put(txtDatabase.getArquivo(), SISTEMA, "DATABASE");
        params.put(txtPorta.getText(), SISTEMA, "PORTA");
        params.put(txtUsuario.getText(), SISTEMA, "USUARIO");
        params.put(txtSenha.getText(), SISTEMA, "SENHA");
        params.put(txtSistema.getText(), SISTEMA, "SISTEMA");
        params.put(edtDtNotaIni.getDate(), SISTEMA, "DATA_NOTAS");
        params.put(Utils.stringToInt(txtReiniciarID.getText()), SISTEMA, "N_REINICIO");
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
    
    private SolidusDAO dao = new SolidusDAO();
    private ConexaoFirebird conn = new ConexaoFirebird();
    
    private SolidusGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        ConexaoFirebird.encoding = "WIN1252";
        initComponents();
        tabProdutos.setOpcoesDisponiveis(dao);
        tabProdutos.tabParametros.add(pnlProdutoCustom);
        
        this.tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
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
                return dao.getLojaOrigem();
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });
        
        this.title = "Importação " + SISTEMA;
        
        listSelectionListenerRotativo = new ListSelectionListenerImpl("ENTIDADE_ROTATIVO_SELECIONADAS", listEntidadesRotativo);
        listSelectionListenerCheque = new ListSelectionListenerImpl("ENTIDADE_CHEQUE_SELECIONADAS", listEntidadesCheque);
        listSelectionListenerConvenio = new ListSelectionListenerImpl("ENTIDADE_CONVENIO_SELECIONADAS", listEntidadesConvenio);
                
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        listEntidadesRotativo.setModel(new DefaultListModel());
        listEntidadesRotativo.addListSelectionListener(listSelectionListenerRotativo);
        
        listEntidadesCheque.setModel(new DefaultListModel());
        listEntidadesCheque.addListSelectionListener(listSelectionListenerCheque);
        
        listEntidadesConvenio.setModel(new DefaultListModel());
        listEntidadesConvenio.addListSelectionListener(listSelectionListenerConvenio);
        
        carregarParametros();
        
        centralizarForm();
        this.setMaximum(false);
    }
    
    private ListSelectionListenerImpl listSelectionListenerRotativo;
    private ListSelectionListenerImpl listSelectionListenerCheque;
    private ListSelectionListenerImpl listSelectionListenerContas;
    private ListSelectionListenerImpl listSelectionListenerConvenio;

    public void validarDadosAcesso() throws Exception {
        if (tabsConn.getSelectedIndex() == 0) {
            if (txtHost.getText().isEmpty()) {
                throw new VRException("Favor informar host do banco de dados " + SERVIDOR_SQL + "!");
            }
            if (txtPorta.getText().isEmpty()) {
                throw new VRException("Favor informar a porta do banco de dados " + SERVIDOR_SQL + "!");
            }
            if (txtDatabase.getArquivo().isEmpty()) {
                throw new VRException("Favor informar nome do banco de dados " + SERVIDOR_SQL + "!");
            }
        } 
        if (txtSenha.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados " + SERVIDOR_SQL + "!");
        }
        if (txtUsuario.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + SERVIDOR_SQL + "!");
        }

        if (tabsConn.getSelectedIndex() == 0) {
            conn.abrirConexao(txtHost.getText(), txtPorta.getInt(), 
                    txtDatabase.getArquivo(), txtUsuario.getText(), txtSenha.getText());
        }
        
        gravarParametros();
        
        carregarLojaVR();
        carregarLojaCliente();
        carregarEntidades();

        
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
        for (Estabelecimento loja: dao.getLojasCliente()) {
            cmbLojaOrigem.addItem(loja);
            if (vLojaCliente != null && vLojaCliente.equals(loja.cnpj)) {
                index = cont;
            }
            cont++;
        }
        cmbLojaOrigem.setSelectedIndex(index);
    }
    
    private void carregarEntidades() {
        
        try {
            
            try {
                this.listSelectionListenerRotativo.acionar = false;
                listEntidadesRotativo.setModel(new EntidadeListModel(dao.getEntidades()));
                getEntidadesSelecionadas(
                        Parametros.get().get(SISTEMA, "ENTIDADE_ROTATIVO_SELECIONADAS"),
                        listEntidadesRotativo                        
                );
            } finally {
                this.listSelectionListenerRotativo.acionar = true;
            }
            
            try {
                this.listSelectionListenerCheque.acionar = false;
                listEntidadesCheque.setModel(new EntidadeListModel(dao.getEntidades()));
                getEntidadesSelecionadas(
                        Parametros.get().get(SISTEMA, "ENTIDADE_CHEQUE_SELECIONADAS"), 
                        listEntidadesCheque
                );
            } finally {
                this.listSelectionListenerCheque.acionar = true;
            }
            
            try {
                this.listSelectionListenerConvenio.acionar = false;
                listEntidadesConvenio.setModel(new EntidadeListModel(dao.getEntidades()));
                getEntidadesSelecionadas(
                        Parametros.get().get(SISTEMA, "ENTIDADE_CONVENIO_SELECIONADAS"), 
                        listEntidadesConvenio
                );
            } finally {
                this.listSelectionListenerConvenio.acionar = true;
            }
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao carregar as entidades", ex);
            Util.exibirMensagemErro(ex, "Erro ao carregar as entidades");
        }
        
    }
    
    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new SolidusGUI(i_mdiFrame);
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
                    
                    dao.setSistema(txtSistema.getText());
                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    dao.setRemoverDigitoProdutoBalanca(chkEliminarDigito.isSelected());

                    if (tabs.getSelectedIndex() == 0) {

                        tabProdutos.setImportador(importador);
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
                            if (chkFCnpj.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CNPJ_CPF);
                            }
                            if (chkFTipoEmpresa.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                            }
                            if (chkFSituacaoCadastro.isSelected()) {
                                opcoes.add(OpcaoFornecedor.SITUACAO_CADASTRO);
                            }
                            if (chkFObservacao.isSelected()) {
                                opcoes.add(OpcaoFornecedor.OBSERVACAO);
                            }
                            if (chkFBloqueado.isSelected()) {
                                opcoes.add(OpcaoFornecedor.BLOQUEADO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        }
                        if (chkClientePreferencial.isSelected()) {
                            List<OpcaoCliente> opcoes = new ArrayList<>();
                            if (chkReiniciarIDCliente.isSelected()) {
                                opcoes.add(
                                    OpcaoCliente.IMP_REINICIAR_NUMERACAO.addParametro(
                                        "N_REINICIO",
                                        Utils.stringToInt(txtReiniciarID.getText())
                                    )
                                );
                            }
                            importador.importarClientePreferencial(opcoes.toArray(new OpcaoCliente[] {}));
                        }
                        {
                            List<OpcaoCliente> opcoes = new ArrayList<>();                            
                            if (chkCObservacao2.isSelected()) {
                                opcoes.add(OpcaoCliente.OBSERVACOES2);
                            }
                            if (chkCSituacaoCadastro.isSelected()) {
                                opcoes.add(OpcaoCliente.SITUACAO_CADASTRO);
                            }
                            if (chkCBloqueado.isSelected()) {
                                opcoes.add(OpcaoCliente.BLOQUEADO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                            }
                        }
                        if (chkClienteEventual.isSelected()) {
                            List<OpcaoCliente> opcoes = new ArrayList<>();
                            if (chkReiniciarIDCliente.isSelected()) {
                                opcoes.add(
                                    OpcaoCliente.IMP_REINICIAR_NUMERACAO.addParametro(
                                        "N_REINICIO",
                                        Utils.stringToInt(txtReiniciarID.getText())
                                    )
                                );
                            }
                            importador.importarClienteEventual(opcoes.toArray(new OpcaoCliente[] {}));
                        }
                        if (chkCreditoRotativo.isSelected()) {
                            dao.setEntidadesCreditoRotativo((List<Entidade>) listEntidadesRotativo.getSelectedValuesList());
                            importador.importarCreditoRotativo();
                        }
                        if (chkCheque.isSelected()) {
                            dao.setEntidadesCheques((List<Entidade>) listEntidadesCheque.getSelectedValuesList());
                            importador.importarCheque();
                        }
                        if (chkOfertas.isSelected()) {
                            importador.importarOfertas(edtDtOferta.getDate());
                        }
                        if (chkContasAPagar.isSelected()) {
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }
                        if (chkPdvVendas.isSelected()) {
                            dao.setVendasDataInicio(edtDtVendaIni.getDate());
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                        if (chkNotasFiscais.isSelected()) {
                            dao.setNotasDataInicio(edtDtNotaIni.getDate());
                            importador.importarNotas(OpcaoNotaFiscal.IMP_EXCLUIR_NOTAS_EXISTENTES);
                        }
                        if (chkCvEmpresa.isSelected()) {
                            importador.importarConvenioEmpresa();
                        }
                        if (chkCvConveniado.isSelected()) {
                            importador.importarConvenioConveniado();
                        }
                        if (chkCvTransacao.isSelected()) {
                            dao.setEntidadesConvenio((List<Entidade>) listEntidadesConvenio.getSelectedValuesList());
                            importador.importarConvenioTransacao();
                        }
                    } else if (tabs.getSelectedIndex() == 1) {
                        if (chkUnifProdutos.isSelected()) {
                            importador.unificarProdutos();
                        }
                        if (chkUnifFornecedor.isSelected()) {
                            importador.unificarFornecedor();
                        }
                        if (chkUnifProdutoFornecedor.isSelected()) {
                            importador.unificarProdutoFornecedor();
                        }                        
                        if (chkUnifClientePreferencial.isSelected()) {
                            List<OpcaoCliente> opcoes = new ArrayList<>();
                            if (chkReiniciarIDClienteUnif.isSelected()) {
                                opcoes.add(
                                    OpcaoCliente.IMP_REINICIAR_NUMERACAO.addParametro(
                                        "N_REINICIO",
                                        Utils.stringToInt(txtReiniciarIDClienteUnif.getText())
                                    )
                                );
                            }
                            importador.unificarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                        }                        
                        if (chkClienteEventual.isSelected()) {
                            List<OpcaoCliente> opcoes = new ArrayList<>();
                            if (chkReiniciarIDClienteUnif.isSelected()) {
                                opcoes.add(
                                    OpcaoCliente.IMP_REINICIAR_NUMERACAO.addParametro(
                                        "N_REINICIO",
                                        Utils.stringToInt(txtReiniciarIDClienteUnif.getText())
                                    )
                                );
                            }
                            importador.unificarClienteEventual(opcoes.toArray(new OpcaoCliente[]{}));
                        }
                    }
                                       
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        conn.close();
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

        pnlProdutoCustom = new vrframework.bean.panel.VRPanel();
        chkEliminarDigito = new vrframework.bean.checkBox.VRCheckBox();
        jLabel3 = new javax.swing.JLabel();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRTabbedPane2 = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabImpFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFCnpj = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkFSituacaoCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkFBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        chkFObservacao = new vrframework.bean.checkBox.VRCheckBox();
        chkContasAPagar = new vrframework.bean.checkBox.VRCheckBox();
        tabClientes = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkReiniciarIDCliente = new vrframework.bean.checkBox.VRCheckBox();
        txtReiniciarID = new vrframework.bean.textField.VRTextField();
        chkCObservacao2 = new vrframework.bean.checkBox.VRCheckBox();
        chkCSituacaoCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkCBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        tabClienteRotativo = new vrframework.bean.panel.VRPanel();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        listEntidadesRotativo = new vrframework.bean.list.VRList();
        tabCheque = new vrframework.bean.panel.VRPanel();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        listEntidadesCheque = new vrframework.bean.list.VRList();
        tabConvenio = new vrframework.bean.panel.VRPanel();
        chkCvEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCvConveniado = new vrframework.bean.checkBox.VRCheckBox();
        chkCvTransacao = new vrframework.bean.checkBox.VRCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        listEntidadesConvenio = new vrframework.bean.list.VRList();
        jLabel4 = new javax.swing.JLabel();
        vRTabbedPane1 = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabOutros = new vrframework.bean.panel.VRPanel();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        edtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        edtDtOferta = new org.jdesktop.swingx.JXDatePicker();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        chkNotasFiscais = new vrframework.bean.checkBox.VRCheckBox();
        edtDtNotaIni = new org.jdesktop.swingx.JXDatePicker();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkReiniciarIDClienteUnif = new vrframework.bean.checkBox.VRCheckBox();
        txtReiniciarIDClienteUnif = new vrframework.bean.textField.VRTextField();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        tabsConn = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txtHost = new vrframework.bean.textField.VRTextField();
        vRLabel23 = new vrframework.bean.label.VRLabel();
        txtPorta = new vrframework.bean.textField.VRTextField();
        vRLabel24 = new vrframework.bean.label.VRLabel();
        vRLabel25 = new vrframework.bean.label.VRLabel();
        txtDatabase = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel20 = new vrframework.bean.label.VRLabel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel21 = new vrframework.bean.label.VRLabel();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        jLabel2 = new javax.swing.JLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        btnConectar = new javax.swing.JToggleButton();
        txtSistema = new vrframework.bean.textField.VRTextField();
        vRLabel22 = new vrframework.bean.label.VRLabel();

        chkEliminarDigito.setText("Eliminar dígito final dos produtos de balança");
        chkEliminarDigito.setToolTipText("<html>\n<p>Quando esta opção é marcada o digito no final dos PLUs dos produtos de balança é <b>removido</b></p>\n</html>");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("CUSTOM");

        javax.swing.GroupLayout pnlProdutoCustomLayout = new javax.swing.GroupLayout(pnlProdutoCustom);
        pnlProdutoCustom.setLayout(pnlProdutoCustomLayout);
        pnlProdutoCustomLayout.setHorizontalGroup(
            pnlProdutoCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProdutoCustomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProdutoCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProdutoCustomLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(chkEliminarDigito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProdutoCustomLayout.setVerticalGroup(
            pnlProdutoCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProdutoCustomLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEliminarDigito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setTitle("Importação Solidus");
        setToolTipText("");

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

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Produtos", tabProdutos);

        tabImpFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        chkFContatos.setText("Contatos");
        chkFContatos.setEnabled(true);
        chkFContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFContatosActionPerformed(evt);
            }
        });

        chkFCnpj.setText("CNPJ/CPF");
        chkFCnpj.setEnabled(true);
        chkFCnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCnpjActionPerformed(evt);
            }
        });

        chkFTipoEmpresa.setText("Tipo Empresa");
        chkFTipoEmpresa.setEnabled(true);
        chkFTipoEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFTipoEmpresaActionPerformed(evt);
            }
        });

        chkFSituacaoCadastro.setText("Situação Cadastro");
        chkFSituacaoCadastro.setEnabled(true);
        chkFSituacaoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFSituacaoCadastroActionPerformed(evt);
            }
        });

        chkFBloqueado.setText("Bloqueado");
        chkFBloqueado.setEnabled(true);
        chkFBloqueado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFBloqueadoActionPerformed(evt);
            }
        });

        chkFObservacao.setText("Observações");
        chkFObservacao.setEnabled(true);
        chkFObservacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFObservacaoActionPerformed(evt);
            }
        });

        chkContasAPagar.setText("Contas à Pagar");
        chkContasAPagar.setEnabled(true);

        javax.swing.GroupLayout tabImpFornecedorLayout = new javax.swing.GroupLayout(tabImpFornecedor);
        tabImpFornecedor.setLayout(tabImpFornecedorLayout);
        tabImpFornecedorLayout.setHorizontalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(190, Short.MAX_VALUE))
        );
        tabImpFornecedorLayout.setVerticalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Fornecedores", tabImpFornecedor);

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

        chkReiniciarIDCliente.setText("Reiniciar ID");
        chkReiniciarIDCliente.setEnabled(true);
        chkReiniciarIDCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkReiniciarIDClienteActionPerformed(evt);
            }
        });

        txtReiniciarID.setMascara("Numero");

        chkCObservacao2.setText("Observações 2");
        chkCObservacao2.setEnabled(true);
        chkCObservacao2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCObservacao2ActionPerformed(evt);
            }
        });

        chkCSituacaoCadastro.setText("Situação Cadastro");
        chkCSituacaoCadastro.setEnabled(true);
        chkCSituacaoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCSituacaoCadastroActionPerformed(evt);
            }
        });

        chkCBloqueado.setText("Bloqueado");
        chkCBloqueado.setEnabled(true);
        chkCBloqueado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCBloqueadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkReiniciarIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReiniciarID, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCObservacao2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(264, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCObservacao2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkReiniciarIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReiniciarID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabClientes.addTab("Descrição", tabClienteDados);

        chkCreditoRotativo.setText("Crédito Rotativo");
        chkCreditoRotativo.setEnabled(true);

        listEntidadesRotativo.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listEntidadesRotativo);

        javax.swing.GroupLayout tabClienteRotativoLayout = new javax.swing.GroupLayout(tabClienteRotativo);
        tabClienteRotativo.setLayout(tabClienteRotativoLayout);
        tabClienteRotativoLayout.setHorizontalGroup(
            tabClienteRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabClienteRotativoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        tabClienteRotativoLayout.setVerticalGroup(
            tabClienteRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteRotativoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteRotativoLayout.createSequentialGroup()
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabClientes.addTab("Crédito Rotativo", tabClienteRotativo);

        chkCheque.setText("Cheques");
        chkCheque.setEnabled(true);
        chkCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequeActionPerformed(evt);
            }
        });

        listEntidadesCheque.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(listEntidadesCheque);

        javax.swing.GroupLayout tabChequeLayout = new javax.swing.GroupLayout(tabCheque);
        tabCheque.setLayout(tabChequeLayout);
        tabChequeLayout.setHorizontalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabChequeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        tabChequeLayout.setVerticalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChequeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGroup(tabChequeLayout.createSequentialGroup()
                        .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabClientes.addTab("Cheques", tabCheque);

        chkCvEmpresa.setText("Empresas");
        chkCvEmpresa.setEnabled(true);
        chkCvEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvEmpresaActionPerformed(evt);
            }
        });

        chkCvConveniado.setText("Conveniados");
        chkCvConveniado.setEnabled(true);
        chkCvConveniado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvConveniadoActionPerformed(evt);
            }
        });

        chkCvTransacao.setText("Transações");
        chkCvTransacao.setEnabled(true);
        chkCvTransacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvTransacaoActionPerformed(evt);
            }
        });

        listEntidadesConvenio.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(listEntidadesConvenio);

        jLabel4.setText("Transações do convênio");

        javax.swing.GroupLayout tabConvenioLayout = new javax.swing.GroupLayout(tabConvenio);
        tabConvenio.setLayout(tabConvenioLayout);
        tabConvenioLayout.setHorizontalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabConvenioLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 271, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCvTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabConvenioLayout.setVerticalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(tabConvenioLayout.createSequentialGroup()
                        .addComponent(chkCvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCvTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 51, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabClientes.addTab("Convênio", tabConvenio);

        vRTabbedPane2.addTab("Clientes", tabClientes);

        vRPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Vendas (PDV)"));

        chkPdvVendas.setEnabled(true);
        chkPdvVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPdvVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        vRPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Ofertas"));

        chkOfertas.setEnabled(true);
        chkOfertas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOfertasActionPerformed(evt);
            }
        });

        edtDtOferta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtDtOfertaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(edtDtOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Notas Fiscais"));

        chkNotasFiscais.setEnabled(true);
        chkNotasFiscais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotasFiscaisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addComponent(chkNotasFiscais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtNotaIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(chkNotasFiscais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(edtDtNotaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout tabOutrosLayout = new javax.swing.GroupLayout(tabOutros);
        tabOutros.setLayout(tabOutrosLayout);
        tabOutrosLayout.setHorizontalGroup(
            tabOutrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabOutrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabOutrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(vRPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(169, Short.MAX_VALUE))
        );
        tabOutrosLayout.setVerticalGroup(
            tabOutrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabOutrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabOutrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        vRTabbedPane1.addTab("Outros", tabOutros);

        vRTabbedPane2.addTab("Outros", vRTabbedPane1);

        tabs.addTab("Importação", vRTabbedPane2);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifFornecedor.setText("Fornecedor (Somente com CPF/CNPJ)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifClientePreferencial.setText("Cliente Preferencial (Somente com CPF/CNPJ)");

        chkUnifClienteEventual.setText("Cliente Eventual (Somente com CPF/CNPJ)");

        chkReiniciarIDClienteUnif.setText("Reiniciar ID (Clientes)");
        chkReiniciarIDClienteUnif.setEnabled(true);
        chkReiniciarIDClienteUnif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkReiniciarIDClienteUnifActionPerformed(evt);
            }
        });

        txtReiniciarIDClienteUnif.setMascara("Numero");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkReiniciarIDClienteUnif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReiniciarIDClienteUnif, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(249, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkReiniciarIDClienteUnif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReiniciarIDClienteUnif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabs.addTab("Unificação", vRPanel2);

        vRPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Firebird"));
        vRPanel6.setPreferredSize(new java.awt.Dimension(350, 350));

        txtHost.setText("localhost");
        txtHost.setCaixaAlta(false);

        vRLabel23.setText("Porta");

        txtPorta.setText("3050");
        txtPorta.setCaixaAlta(false);
        txtPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

        vRLabel24.setText("Host");

        vRLabel25.setText("Banco de Dados");

        vRLabel20.setText("Usuário:");

        txtUsuario.setText("gwuniao");
        txtUsuario.setCaixaAlta(false);
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        vRLabel21.setText("Senha:");

        txtSenha.setText("gwuniao");
        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");
        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        jLabel2.setText("Loja Origem");

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        cmbLojaOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaOrigemActionPerformed(evt);
            }
        });

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPorta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1))
                    .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(vRLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        tabsConn.addTab("Dados da conexão", jPanel4);

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsConn, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        txtSistema.setText("Solidus");
        txtSistema.setCaixaAlta(false);
        txtSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSistemaActionPerformed(evt);
            }
        });

        vRLabel22.setText("Sistema");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(vRLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vRPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
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
            gravarParametros();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

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

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            this.setWaitCursor();

            if (conn != null) {
                conn.close();
            }

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    private void txtPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void chkFCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCnpjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFCnpjActionPerformed

    private void chkOfertasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOfertasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkOfertasActionPerformed

    private void chkPdvVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasActionPerformed
        if (edtDtVendaIni.getDate() == null) {
            edtDtVendaIni.setDate(new Date());
        }
    }//GEN-LAST:event_chkPdvVendasActionPerformed

    private void edtDtOfertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtDtOfertaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtDtOfertaActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

    private void chkFTipoEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFTipoEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFTipoEmpresaActionPerformed

    private void txtSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSistemaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSistemaActionPerformed

    private void chkReiniciarIDClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkReiniciarIDClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkReiniciarIDClienteActionPerformed

    private void chkReiniciarIDClienteUnifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkReiniciarIDClienteUnifActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkReiniciarIDClienteUnifActionPerformed

    private void chkNotasFiscaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotasFiscaisActionPerformed
        if (edtDtNotaIni.getDate() == null) {
            edtDtNotaIni.setDate(new Date());
        }
    }//GEN-LAST:event_chkNotasFiscaisActionPerformed

    private void chkFSituacaoCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFSituacaoCadastroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFSituacaoCadastroActionPerformed

    private void chkFBloqueadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFBloqueadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFBloqueadoActionPerformed

    private void chkFObservacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFObservacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFObservacaoActionPerformed

    private void chkCObservacao2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCObservacao2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCObservacao2ActionPerformed

    private void cmbLojaOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaOrigemActionPerformed
        dao.setLojaOrigem(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj);
    }//GEN-LAST:event_cmbLojaOrigemActionPerformed

    private void chkCvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvEmpresaActionPerformed

    private void chkCvConveniadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvConveniadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvConveniadoActionPerformed

    private void chkCvTransacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvTransacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvTransacaoActionPerformed

    private void chkCSituacaoCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCSituacaoCadastroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCSituacaoCadastroActionPerformed

    private void chkCBloqueadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCBloqueadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCBloqueadoActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCBloqueado;
    private vrframework.bean.checkBox.VRCheckBox chkCObservacao2;
    private vrframework.bean.checkBox.VRCheckBox chkCSituacaoCadastro;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkContasAPagar;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkCvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCvTransacao;
    private vrframework.bean.checkBox.VRCheckBox chkEliminarDigito;
    private vrframework.bean.checkBox.VRCheckBox chkFBloqueado;
    private vrframework.bean.checkBox.VRCheckBox chkFCnpj;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFObservacao;
    private vrframework.bean.checkBox.VRCheckBox chkFSituacaoCadastro;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkNotasFiscais;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkReiniciarIDCliente;
    private vrframework.bean.checkBox.VRCheckBox chkReiniciarIDClienteUnif;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private org.jdesktop.swingx.JXDatePicker edtDtNotaIni;
    private org.jdesktop.swingx.JXDatePicker edtDtOferta;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private vrframework.bean.list.VRList listEntidadesCheque;
    private vrframework.bean.list.VRList listEntidadesConvenio;
    private vrframework.bean.list.VRList listEntidadesRotativo;
    private vrframework.bean.panel.VRPanel pnlProdutoCustom;
    private vrframework.bean.panel.VRPanel tabCheque;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.panel.VRPanel tabClienteRotativo;
    private vrframework.bean.tabbedPane.VRTabbedPane tabClientes;
    private vrframework.bean.panel.VRPanel tabConvenio;
    private vrframework.bean.panel.VRPanel tabImpFornecedor;
    private vrframework.bean.panel.VRPanel tabOutros;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private javax.swing.JTabbedPane tabsConn;
    private vrframework.bean.fileChooser.VRFileChooser txtDatabase;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.textField.VRTextField txtReiniciarID;
    private vrframework.bean.textField.VRTextField txtReiniciarIDClienteUnif;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private vrframework.bean.textField.VRTextField txtSistema;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrframework.bean.label.VRLabel vRLabel20;
    private vrframework.bean.label.VRLabel vRLabel21;
    private vrframework.bean.label.VRLabel vRLabel22;
    private vrframework.bean.label.VRLabel vRLabel23;
    private vrframework.bean.label.VRLabel vRLabel24;
    private vrframework.bean.label.VRLabel vRLabel25;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane1;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    // End of variables declaration//GEN-END:variables

    private void getEntidadesSelecionadas(String param, VRList list) {
        if (param != null && !"".equals(param)) {
            for (String id: param.split(":")) {
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    Entidade entidade = (Entidade) list.getModel().getElementAt(i);
                    if (entidade.getId() == Integer.parseInt(id)) {
                        list.addSelectionInterval(i, i);
                        break;
                    }
                }
            }
        }
    }


    private class ListSelectionListenerImpl implements ListSelectionListener {
        
        private String id;
        private VRList list;

        public ListSelectionListenerImpl(String id, VRList list) {
            this.id = id;
            this.list = list;
        }
        
        public boolean acionar = true;

        private String toStringEntidades() {
            StringBuilder builder = new StringBuilder();
            for (Iterator<Entidade> iterator = list.getSelectedValuesList().iterator(); iterator.hasNext(); ) {
                builder.append(iterator.next().getId());
                if (iterator.hasNext()) {
                    builder.append(":");
                }
            }
            return builder.toString();
        }
        
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (acionar) {
                if (!e.getValueIsAdjusting()) {
                    Parametros.get().put(toStringEntidades(), SISTEMA, id);
                }
            }
        }
    }
    
}
