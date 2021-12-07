package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.SygmaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class SygmaGUI extends VRInternalFrame {
    
    private static final String SISTEMA = "Sygma";
    private static final String SERVIDOR_SQL = "Firebird";
    private static SygmaGUI instance;
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        txtHost.setText(params.getWithNull("localhost", SISTEMA, "HOST"));
        txtDatabase.setArquivo(params.getWithNull("C:\\Sigma\\Bancos\\SISTEMA.FDB", SISTEMA, "DATABASE"));
        txtPorta.setText(params.getWithNull("3050", SISTEMA, "PORTA"));
        txtUsuario.setText(params.getWithNull("SYSDBA", SISTEMA, "USUARIO"));
        txtSenha.setText(params.getWithNull("masterkey", SISTEMA, "SENHA"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(txtHost.getText(), SISTEMA, "HOST");
        params.put(txtDatabase.getArquivo(), SISTEMA, "DATABASE");
        params.put(txtPorta.getText(), SISTEMA, "PORTA");
        params.put(txtUsuario.getText(), SISTEMA, "USUARIO");
        params.put(txtSenha.getText(), SISTEMA, "SENHA");
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
    
    private SygmaDAO dao = new SygmaDAO();
    private ConexaoFirebird connFirebird = new ConexaoFirebird();
    
    private SygmaGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        tabProdutos.setOpcoesDisponiveis(dao);
        
        ConexaoFirebird.encoding = "WIN1252";        
        
        this.title = "Importação " + SISTEMA;
                
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        carregarParametros();
        
        tabProdutos.setOpcoesDisponiveis(dao);

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
                dao.setLojaOrigem(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj);
                return dao.getLojaOrigem();
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
            connFirebird.abrirConexao(txtHost.getText(), txtPorta.getInt(), 
                    txtDatabase.getArquivo(), txtUsuario.getText(), txtSenha.getText());
        }
        
        gravarParametros();
        
        carregarLojaVR();
        carregarLojaCliente();
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
    
    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new SygmaGUI(i_mdiFrame);
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
                    
                    if (tabs.getSelectedIndex() == 0) {
                    
                        tabProdutos.executarImportacao();

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
                        
                        if (chkFCondicaoPagamento.isSelected()) {
                            opcoes.add(OpcaoFornecedor.CONDICAO_PAGAMENTO);
                        }
                        
                        if (chkFPrazoFornecedor.isSelected()) {
                            opcoes.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
                        }
                        
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }

                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }

                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }
                        
                        if (chkRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
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
                            importador.unificarClientePreferencial();
                        }                        
                        if (chkClienteEventual.isSelected()) {
                            importador.unificarClienteEventual();
                        }
                    }
                                       
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        connFirebird.close();
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

        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRTabbedPane2 = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        vRPanel9 = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFCondicaoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel20 = new vrframework.bean.label.VRLabel();
        vRLabel21 = new vrframework.bean.label.VRLabel();
        btnConectar = new javax.swing.JToggleButton();
        tabsConn = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txtHost = new vrframework.bean.textField.VRTextField();
        vRLabel23 = new vrframework.bean.label.VRLabel();
        txtPorta = new vrframework.bean.textField.VRTextField();
        vRLabel24 = new vrframework.bean.label.VRLabel();
        vRLabel25 = new vrframework.bean.label.VRLabel();
        txtDatabase = new vrframework.bean.fileChooser.VRFileChooser();
        jLabel2 = new javax.swing.JLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();

        setTitle("Importação Sygma");
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

        vRPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        chkRotativo.setText("Crédito Rotativo");
        chkRotativo.setEnabled(true);
        chkRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRotativoActionPerformed(evt);
            }
        });

        chkCheque.setText("Cheque");
        chkCheque.setEnabled(true);
        chkCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel9Layout = new javax.swing.GroupLayout(vRPanel9);
        vRPanel9.setLayout(vRPanel9Layout);
        vRPanel9Layout.setHorizontalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(479, Short.MAX_VALUE))
        );
        vRPanel9Layout.setVerticalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Clientes", vRPanel9);

        vRPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        chkFPrazoFornecedor.setText("Prazo Fornecedor");
        chkFPrazoFornecedor.setEnabled(true);
        chkFPrazoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFPrazoFornecedorActionPerformed(evt);
            }
        });

        chkFCondicaoPagamento.setText("Condição Pagamento");
        chkFCondicaoPagamento.setEnabled(true);
        chkFCondicaoPagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCondicaoPagamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(499, Short.MAX_VALUE))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Fornecedores", vRPanel8);

        tabs.addTab("Importação", vRTabbedPane2);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifFornecedor.setText("Fornecedor (Somente com CPF/CNPJ)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifClientePreferencial.setText("Cliente Preferencial (Somente com CPF/CNPJ)");

        chkUnifClienteEventual.setText("Cliente Eventual (Somente com CPF/CNPJ)");

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
                    .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(459, Short.MAX_VALUE))
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
                .addContainerGap(62, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);

        vRPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Firebird"));
        vRPanel6.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuario.setText("gwuniao");
        txtUsuario.setCaixaAlta(false);
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        txtSenha.setText("gwuniao");
        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");
        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        vRLabel20.setText("Usuário:");

        vRLabel21.setText("Senha:");

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        txtHost.setText("192.168.1.100");
        txtHost.setCaixaAlta(false);

        vRLabel23.setText("Porta");

        txtPorta.setText("1433");
        txtPorta.setCaixaAlta(false);
        txtPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

        vRLabel24.setText("Host");

        vRLabel25.setText("Banco de Dados");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsConn.addTab("Dados da conexão", jPanel4);

        jLabel2.setText("Loja Origem");

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsConn)
                    .addGroup(vRPanel6Layout.createSequentialGroup()
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(vRLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void chkRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRotativoActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            this.setWaitCursor();

            if (connFirebird != null) {
                connFirebird.close();
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

    private void chkFPrazoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFPrazoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFPrazoFornecedorActionPerformed

    private void chkFCondicaoPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCondicaoPagamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFCondicaoPagamentoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkFCondicaoPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFPrazoFornecedor;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel4;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private javax.swing.JTabbedPane tabsConn;
    private vrframework.bean.fileChooser.VRFileChooser txtDatabase;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrframework.bean.label.VRLabel vRLabel20;
    private vrframework.bean.label.VRLabel vRLabel21;
    private vrframework.bean.label.VRLabel vRLabel23;
    private vrframework.bean.label.VRLabel vRLabel24;
    private vrframework.bean.label.VRLabel vRLabel25;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

    

}
