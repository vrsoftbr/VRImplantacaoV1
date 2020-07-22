package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.SqlVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.DJSystemDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class DJSystemGUI extends VRInternalFrame {
    
    private static final String SISTEMA = "DJ System";
    private static final String SERVIDOR_SQL = "Dbf";
    private static DJSystemGUI instance;
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    private SqlVO oSql = null;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtDatabase.setText(params.get(SISTEMA, "DATABASE"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtDatabase.getText(), SISTEMA, "DATABASE");
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
    
    private DJSystemDAO dao = new DJSystemDAO();
    private ConexaoDBF connDBF = new ConexaoDBF();
    
    private DJSystemGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        this.title = "Importação " + SISTEMA;
        carregarParametros();
        
        pnlProduto.setOpcoesDisponiveis(dao);
        pnlProduto.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                    return SISTEMA;
            }

            @Override
            public String getLoja() {
                vLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;
                return vLojaCliente;
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });
        
        centralizarForm();
        
        this.setMaximum(false);
    }

    public void validarDadosAcesso() throws Exception {
        ConexaoDBF.abrirConexao(txtDatabase.getText());
        gravarParametros();
        carregarLojaCliente();
        carregarLojaVR();
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
                instance = new DJSystemGUI(i_mdiFrame);
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

                    if (pnlDados.getSelectedIndex() == 0) {
                        
                        if (pnlImportacao.getSelectedIndex() == 0) {
                            pnlProduto.setImportador(importador);
                            pnlProduto.executarImportacao();
                        } else if (pnlImportacao.getSelectedIndex() == 1) {

                            if (chkFornecedor.isSelected()) {
                                importador.importarFornecedor();
                            }

                            if (chkProdutoFornecedor.isSelected()) {
                                importador.importarProdutoFornecedor();
                            }

                            List<OpcaoFornecedor> opcoes = new ArrayList<>();
                            if (chkFContatos.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                            
                            if (chkFEndCobrancaForn.isSelected()) {
                                opcoes.add(OpcaoFornecedor.ENDERECO_COMPLETO_COBRANCA);
                            }

                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        } else if (pnlImportacao.getSelectedIndex() == 2) {
                            if (chkClientePreferencial.isSelected()) {
                                importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.VALOR_LIMITE, OpcaoCliente.SITUACAO_CADASTRO);
                            }

                            if (chkRotativo.isSelected()) {
                                importador.importarCreditoRotativo();
                            }
                            
                            List<OpcaoCliente> opcoes = new ArrayList<>();                            
                            
                            if (chkCSituacao.isSelected()) {
                                opcoes.add(OpcaoCliente.SITUACAO_CADASTRO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                            }
                        } 
                    } else if (pnlDados.getSelectedIndex() == 1) {
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
                            importador.unificarClientePreferencial();
                        }                        
                    }
                                       
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        connDBF.close();
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
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> vColuna = new ArrayList();

        for (String coluna : oSql.vHeader) {
            vColuna.add(new VRColumnTable(coluna, 80, true, SwingConstants.LEFT, false, null));
        }

        tblConsulta.configurarColuna(vColuna, this, "tblConsulta", "exibirConsulta", Global.idUsuario);
    }
    
    public void exibirConsulta() throws Exception {
        Object[][] dados = new Object[oSql.vConsulta.size()][tblConsulta.getvColuna().size()];

        int l = 0;

        for (List<String> vColuna : oSql.vConsulta) {
            for (int c = 0; c < vColuna.size(); c++) {
                dados[l][tblConsulta.getOrdem(c)] = vColuna.get(tblConsulta.getOrdem(c));
            }

            l++;
        }

        tblConsulta.setModel(dados);
    }
    
    private void executarSQL() throws Exception {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    setWaitCursor();

                    btnExecutar2.setEnabled(false);

                    String sql = txtSql.getSelectedText() == null ? txtSql.getText() : txtSql.getSelectedText();

                    tblConsulta.clear();
                    txtMensagem.setText("");

                    if (sql.trim().toUpperCase().startsWith("SELECT")) {
                        oSql = dao.consultar(sql);

                        configurarColuna();

                        exibirConsulta();
                        txtMensagem.setText(oSql.vConsulta.size() + " registros recuperados.");

                        if (oSql.vConsulta.size() > 0) {
                            tpnResultado.setSelectedIndex(0);
                        } else {
                            tpnResultado.setSelectedIndex(1);
                        }

                    } else {
                        if (Global.idUsuario > 0) {
                            throw new VRException("Operação não permitida!");
                        }

                        //String result = new AlfaSoftwareDAO().executar(sql);
                        //txtMensagem.setText(result);

                        //tpnResultado.setSelectedIndex(1);
                    }

                } catch (Exception ex) {
                    txtMensagem.setText(ex.getMessage());
                    tpnResultado.setSelectedIndex(1);

                } finally {
                    setDefaultCursor();                    
                    btnExecutar2.setEnabled(true);
                }
            }
        };

        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        vRTextArea1 = new vrframework.bean.textArea.VRTextArea();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        pnlBotao = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        pnlDados = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlProduto = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        pnlFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFEndCobrancaForn = new javax.swing.JCheckBox();
        pnlCliente = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCSituacao = new javax.swing.JCheckBox();
        pnlSQL = new vrframework.bean.panel.VRPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSql = new vrframework.bean.textArea.VRTextArea();
        btnExecutar2 = new vrframework.bean.button.VRButton();
        tpnResultado = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel15 = new vrframework.bean.panel.VRPanel();
        tblConsulta = new vrframework.bean.tableEx.VRTableEx();
        vRPanel16 = new vrframework.bean.panel.VRPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtMensagem = new javax.swing.JTextArea();
        pnlUnificacao = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        pnlConexao = new vrframework.bean.panel.VRPanel();
        tabsConn = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        lblBancoDados = new vrframework.bean.label.VRLabel();
        vRTextField1 = new vrframework.bean.textField.VRTextField();
        txtDatabase = new vrframework.bean.textField.VRTextField();
        lblLojaOrigem = new javax.swing.JLabel();
        btnConectar = new javax.swing.JToggleButton();
        cmbLojaOrigem = new javax.swing.JComboBox();

        vRTextArea1.setColumns(20);
        vRTextArea1.setRows(5);
        jScrollPane1.setViewportView(vRTextArea1);

        setResizable(true);
        setTitle("Importação SJ System");
        setToolTipText("");

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

        jLabel1.setText("Loja:");

        javax.swing.GroupLayout pnlBotaoLayout = new javax.swing.GroupLayout(pnlBotao);
        pnlBotao.setLayout(pnlBotaoLayout);
        pnlBotaoLayout.setHorizontalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlBotaoLayout.setVerticalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlImportacao.addTab("Produtos", pnlProduto);

        pnlFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.setEnabled(true);
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkFContatos.setText("Contatos");
        chkFContatos.setEnabled(true);
        chkFContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFContatosActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");

        chkFEndCobrancaForn.setText("Endereço Cobrança");

        javax.swing.GroupLayout pnlFornecedorLayout = new javax.swing.GroupLayout(pnlFornecedor);
        pnlFornecedor.setLayout(pnlFornecedorLayout);
        pnlFornecedorLayout.setHorizontalGroup(
            pnlFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkFEndCobrancaForn))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        pnlFornecedorLayout.setVerticalGroup(
            pnlFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkFEndCobrancaForn)
                .addContainerGap(162, Short.MAX_VALUE))
        );

        pnlImportacao.addTab("Fornecedores", pnlFornecedor);

        pnlCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkClientePreferencial.setText("Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);
        chkClientePreferencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferencialActionPerformed(evt);
            }
        });

        chkRotativo.setText("Crédito Rotativo");
        chkRotativo.setEnabled(true);
        chkRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRotativoActionPerformed(evt);
            }
        });

        chkCSituacao.setText("Situação Cadastro");

        javax.swing.GroupLayout pnlClienteLayout = new javax.swing.GroupLayout(pnlCliente);
        pnlCliente.setLayout(pnlClienteLayout);
        pnlClienteLayout.setHorizontalGroup(
            pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCSituacao))
                    .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(285, Short.MAX_VALUE))
        );
        pnlClienteLayout.setVerticalGroup(
            pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCSituacao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        pnlImportacao.addTab("Clientes", pnlCliente);

        txtSql.setColumns(1000);
        txtSql.setRows(6);
        txtSql.setTabSize(20);
        jScrollPane2.setViewportView(txtSql);

        btnExecutar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sqlexecutar.png"))); // NOI18N
        btnExecutar2.setMnemonic('e');
        btnExecutar2.setToolTipText("Executar (Alt+E)");
        btnExecutar2.setFocusable(false);
        btnExecutar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExecutar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExecutar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutar2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel15Layout = new javax.swing.GroupLayout(vRPanel15);
        vRPanel15.setLayout(vRPanel15Layout);
        vRPanel15Layout.setHorizontalGroup(
            vRPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        );
        vRPanel15Layout.setVerticalGroup(
            vRPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );

        tpnResultado.addTab("Saída de Dados", vRPanel15);

        txtMensagem.setEditable(false);
        txtMensagem.setColumns(20);
        txtMensagem.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtMensagem.setRows(5);
        jScrollPane3.setViewportView(txtMensagem);

        javax.swing.GroupLayout vRPanel16Layout = new javax.swing.GroupLayout(vRPanel16);
        vRPanel16.setLayout(vRPanel16Layout);
        vRPanel16Layout.setHorizontalGroup(
            vRPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        );
        vRPanel16Layout.setVerticalGroup(
            vRPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );

        tpnResultado.addTab("Mensagens", vRPanel16);

        javax.swing.GroupLayout pnlSQLLayout = new javax.swing.GroupLayout(pnlSQL);
        pnlSQL.setLayout(pnlSQLLayout);
        pnlSQLLayout.setHorizontalGroup(
            pnlSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(pnlSQLLayout.createSequentialGroup()
                .addComponent(btnExecutar2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(tpnResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlSQLLayout.setVerticalGroup(
            pnlSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSQLLayout.createSequentialGroup()
                .addComponent(btnExecutar2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlImportacao.addTab("SQL", pnlSQL);

        pnlDados.addTab("Importação", pnlImportacao);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifFornecedor.setText("Fornecedor (Somente com CPF/CNPJ)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifClientePreferencial.setText("Cliente Preferencial (Somente com CPF/CNPJ)");

        chkUnifClienteEventual.setText("Cliente Eventual (Somente com CPF/CNPJ)");

        javax.swing.GroupLayout pnlUnificacaoLayout = new javax.swing.GroupLayout(pnlUnificacao);
        pnlUnificacao.setLayout(pnlUnificacaoLayout);
        pnlUnificacaoLayout.setHorizontalGroup(
            pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(293, Short.MAX_VALUE))
        );
        pnlUnificacaoLayout.setVerticalGroup(
            pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnificacaoLayout.createSequentialGroup()
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
                .addContainerGap(143, Short.MAX_VALUE))
        );

        pnlDados.addTab("Unificação", pnlUnificacao);
        pnlDados.addTab("Balança", pnlBalanca);

        pnlConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Dbf"));
        pnlConexao.setPreferredSize(new java.awt.Dimension(350, 350));

        lblBancoDados.setText("Banco de Dados");

        vRTextField1.setText("vRTextField1");

        lblLojaOrigem.setText("Loja Origem");

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        cmbLojaOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaOrigemActionPerformed(evt);
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
                        .addComponent(lblBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(vRTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblLojaOrigem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLojaOrigem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsConn.addTab("Dados da conexão", jPanel4);

        javax.swing.GroupLayout pnlConexaoLayout = new javax.swing.GroupLayout(pnlConexao);
        pnlConexao.setLayout(pnlConexaoLayout);
        pnlConexaoLayout.setHorizontalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlConexaoLayout.setVerticalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addComponent(tabsConn)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                    .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlBotao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBotao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRotativoActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            this.setWaitCursor();

            if (connDBF != null) {
                connDBF.close();
            }

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void cmbLojaOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaOrigemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLojaOrigemActionPerformed

    private void btnExecutar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutar2ActionPerformed
        try {
            this.setWaitCursor();
            executarSQL();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnExecutar2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrframework.bean.button.VRButton btnExecutar2;
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.JCheckBox chkCSituacao;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private javax.swing.JCheckBox chkFEndCobrancaForn;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private vrframework.bean.label.VRLabel lblBancoDados;
    private javax.swing.JLabel lblLojaOrigem;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlBotao;
    private vrframework.bean.panel.VRPanel pnlCliente;
    private vrframework.bean.panel.VRPanel pnlConexao;
    private vrframework.bean.tabbedPane.VRTabbedPane pnlDados;
    private vrframework.bean.panel.VRPanel pnlFornecedor;
    private vrframework.bean.tabbedPane.VRTabbedPane pnlImportacao;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI pnlProduto;
    private vrframework.bean.panel.VRPanel pnlSQL;
    private vrframework.bean.panel.VRPanel pnlUnificacao;
    private javax.swing.JTabbedPane tabsConn;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
    private vrframework.bean.tabbedPane.VRTabbedPane tpnResultado;
    private vrframework.bean.textField.VRTextField txtDatabase;
    private javax.swing.JTextArea txtMensagem;
    private vrframework.bean.textArea.VRTextArea txtSql;
    private vrframework.bean.panel.VRPanel vRPanel15;
    private vrframework.bean.panel.VRPanel vRPanel16;
    private vrframework.bean.textArea.VRTextArea vRTextArea1;
    private vrframework.bean.textField.VRTextField vRTextField1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

    

}
