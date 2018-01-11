package vrimplantacao.gui.interfaces;

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
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.SciDAO;
import vrimplantacao.vo.loja.LojaVO;

public class SciGUI extends VRInternalFrame {

    private final SciDAO sciDAO = new SciDAO();
    private List<LojaVO> vLojaDestino = new ArrayList<>();
    private final ConexaoMySQL conn = new ConexaoMySQL();

    public SciGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        txtHost.setText("192.168.1.10");
        txtBancoDados.setText("dbtabelas");
        txtUsuario.setText("sci");
        txtSenha.setText("sci2000e");
        txtPorta.setText("3306");
        
        centralizarForm();
        this.setMaximum(false);

    }

    public void validarDadosAcessoSqlServer() throws Exception {
        if (txtHost.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados!");
        }
        if (txtBancoDados.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados!");
        }

        if (txtSenha.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados!");
        }

        if (txtPorta.getText().isEmpty()) {
            throw new VRException("Favor informar a porta do banco de dados!");
        }
        if (txtUsuario.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados!");
        }

        conn.abrirConexao(txtHost.getText(), txtPorta.getInt(), 
                txtBancoDados.getText(), txtUsuario.getText(), txtSenha.getText());

        vLojaDestino = new LojaDAO().carregar();

        carregarLojaCliente();
        carregarLojaDestino();
    }

    private void carregarLojaDestino() throws Exception {
        cmbLojaDestino.removeAllItems();
        cmbLojaDestino.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : vLojaDestino) {
            cmbLojaDestino.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
        cmbLojaDestino.setEnabled(true);
    }
    
    private void carregarLojaCliente() throws Exception {
        cmbLojaCliente.removeAllItems();
        cmbLojaCliente.setModel(new DefaultComboBoxModel());
        for (ItemComboVO oLoja : sciDAO.carregarLojasCliente()) {
            cmbLojaCliente.addItem(oLoja);
        }
        cmbLojaCliente.setEnabled(true);
    }
    
    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, idLojaCliente;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    idLojaVR = ((ItemComboVO) cmbLojaDestino.getSelectedItem()).id;                    
                    idLojaCliente = ((ItemComboVO) cmbLojaCliente.getSelectedItem()).id;
                    
                    sciDAO.gerarIdParaClienteEventual = chkGerarIdParaClienteEventual.isSelected();
                    sciDAO.gerarIdDosProdutos = chkGerarIdDosProdutos.isSelected();
                    
                    if (chkProdutoFamilia.isSelected()) {
                        sciDAO.importarFamiliaProduto();
                    }
                    
                    if (chkMapaMercadologico.isSelected()) {
                        sciDAO.importarMapeamentoDeMercadologico(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkProdutos.isSelected()) {
                        sciDAO.importarProduto(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkEAN.isSelected()) {
                        sciDAO.importarEanProduto(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkEANEmBranco.isSelected()) {
                        sciDAO.importarCodigoBarraEmBranco();
                    }
                    
                    if (chkPreco.isSelected()) {
                        sciDAO.importarPrecoProduto(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkCusto.isSelected()) {
                        sciDAO.importarCustoProduto(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkEstoque.isSelected()) {
                        sciDAO.importarEstoqueProduto(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkFornecedores.isSelected()) {
                        sciDAO.importarFornecedor();
                    }
                    
                    if (chkClientesPreferenciais.isSelected()) {
                        sciDAO.importarClientePreferencial(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkCreditoRotativo.isSelected()) {
                        sciDAO.importarReceberCreditoRotativo(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkClientesEventuais.isSelected()) {
                        sciDAO.importarClienteEventual(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkOfertas.isSelected()) {
                        sciDAO.importarOfertas(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkCheques.isSelected()) {
                        sciDAO.importarCheques(idLojaVR, idLojaCliente);
                    }
                    
                    if (tab.getSelectedIndex() == 1) {
                        if (chkMapearMercadologico.isSelected()) {
                            sciDAO.mapearMercadologico(idLojaVR, idLojaCliente);
                        }
                        if (chkCorrecaoCliente.isSelected()) {
                            sciDAO.corrigirClientePreferencial(idLojaVR, idLojaCliente);
                        }
                        if (chkCorrigirEstoque.isSelected()) {
                            sciDAO.corrigirEstoque(idLojaVR, idLojaCliente);
                        }                        
                        if (chkCopiarListaDeProduto.isSelected()) {
                            sciDAO.copiarListaDeProdutos(idLojaVR, idLojaCliente);
                        }
                        if (chkImportarSenhas.isSelected()) {
                            sciDAO.importarSenhaClientePreferencial(idLojaCliente);
                        }
                    }
                    
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação Sci realizada com sucesso!", getTitle());
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

        vRButton1 = new vrframework.bean.button.VRButton();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPorta = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHost = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarSqlServer = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtBancoDados = new vrframework.bean.textField.VRTextField();
        cmbLojaCliente = new javax.swing.JComboBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        cmbLojaDestino = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlImportarDados = new vrframework.bean.panel.VRPanel();
        pnlProdutos = new vrframework.bean.panel.VRPanel();
        chkProdutoFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkMapaMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkEANEmBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkPreco = new vrframework.bean.checkBox.VRCheckBox();
        chkCusto = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        pnlFornecedores = new vrframework.bean.panel.VRPanel();
        chkFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        pnlClientesPreferenciais1 = new vrframework.bean.panel.VRPanel();
        chkClientesPreferenciais = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkClientesEventuais = new vrframework.bean.checkBox.VRCheckBox();
        chkCheques = new vrframework.bean.checkBox.VRCheckBox();
        pnlEspeciais = new vrframework.bean.panel.VRPanel();
        chkMapearMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkGerarIdParaClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkGerarIdDosProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkCorrecaoCliente = new vrframework.bean.checkBox.VRCheckBox();
        chkCorrigirEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiarListaDeProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkImportarSenhas = new vrframework.bean.checkBox.VRCheckBox();

        vRButton1.setText("vRButton1");

        setTitle("Sci");
        setToolTipText("");

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - MySQL"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuario.setCaixaAlta(false);
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");
        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPorta.setCaixaAlta(false);
        txtPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHost.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarSqlServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarSqlServer.setText("Conectar");
        btnConectarSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarSqlServerActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtBancoDados.setCaixaAlta(false);
        txtBancoDados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBancoDadosActionPerformed(evt);
            }
        });

        cmbLojaCliente.setEnabled(false);
        cmbLojaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(cmbLojaCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectarSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPorta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(vRPanel1Layout.createSequentialGroup()
                                        .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(39, 39, 39))))
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(vRLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(vRLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConectarSqlServer)
                    .addComponent(cmbLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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

        cmbLojaDestino.setEnabled(false);
        cmbLojaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaDestinoActionPerformed(evt);
            }
        });

        jLabel1.setText("Loja:");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaDestino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(vRPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlImportarDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlImportarDados.setPreferredSize(new java.awt.Dimension(350, 350));

        pnlProdutos.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos"));

        chkProdutoFamilia.setText("Família");

        chkMapaMercadologico.setText("Mercadológico (Mapeamento)");

        chkProdutos.setText("Produtos");

        chkEAN.setText("EAN");

        chkEANEmBranco.setText("EAN em branco");

        chkPreco.setText("Preço");

        chkCusto.setText("Custo");

        chkEstoque.setText("Estoque");

        chkOfertas.setText("Ofertas");

        javax.swing.GroupLayout pnlProdutosLayout = new javax.swing.GroupLayout(pnlProdutos);
        pnlProdutos.setLayout(pnlProdutosLayout);
        pnlProdutosLayout.setHorizontalGroup(
            pnlProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProdutosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProdutosLayout.createSequentialGroup()
                        .addComponent(chkProdutoFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlProdutosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chkMapaMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(177, 177, 177))
                    .addGroup(pnlProdutosLayout.createSequentialGroup()
                        .addGroup(pnlProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEANEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlProdutosLayout.setVerticalGroup(
            pnlProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProdutosLayout.createSequentialGroup()
                .addComponent(chkProdutoFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMapaMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEANEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFornecedores.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedores"));

        chkFornecedores.setText("Cadastros");

        javax.swing.GroupLayout pnlFornecedoresLayout = new javax.swing.GroupLayout(pnlFornecedores);
        pnlFornecedores.setLayout(pnlFornecedoresLayout);
        pnlFornecedoresLayout.setHorizontalGroup(
            pnlFornecedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFornecedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        pnlFornecedoresLayout.setVerticalGroup(
            pnlFornecedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkFornecedores, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlClientesPreferenciais1.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        chkClientesPreferenciais.setText("Preferênciais");

        chkCreditoRotativo.setText("Crédito Rotativo");
        chkCreditoRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCreditoRotativoActionPerformed(evt);
            }
        });

        chkClientesEventuais.setText("Eventuais");
        chkClientesEventuais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientesEventuaisActionPerformed(evt);
            }
        });

        chkCheques.setText("Cheques");
        chkCheques.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlClientesPreferenciais1Layout = new javax.swing.GroupLayout(pnlClientesPreferenciais1);
        pnlClientesPreferenciais1.setLayout(pnlClientesPreferenciais1Layout);
        pnlClientesPreferenciais1Layout.setHorizontalGroup(
            pnlClientesPreferenciais1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesPreferenciais1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClientesPreferenciais1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCheques, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlClientesPreferenciais1Layout.setVerticalGroup(
            pnlClientesPreferenciais1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientesPreferenciais1Layout.createSequentialGroup()
                .addComponent(chkClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheques, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlImportarDadosLayout = new javax.swing.GroupLayout(pnlImportarDados);
        pnlImportarDados.setLayout(pnlImportarDadosLayout);
        pnlImportarDadosLayout.setHorizontalGroup(
            pnlImportarDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportarDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImportarDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlClientesPreferenciais1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFornecedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(234, Short.MAX_VALUE))
        );
        pnlImportarDadosLayout.setVerticalGroup(
            pnlImportarDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportarDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlImportarDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImportarDadosLayout.createSequentialGroup()
                        .addComponent(pnlFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlClientesPreferenciais1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tab.addTab("Importar Tabelas", pnlImportarDados);

        chkMapearMercadologico.setText("Executar mapeamento do mercadológico");

        chkGerarIdParaClienteEventual.setText("Gerar ID para cliente eventual");
        chkGerarIdParaClienteEventual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkGerarIdParaClienteEventualActionPerformed(evt);
            }
        });

        chkGerarIdDosProdutos.setText("Gerar ID dos produtos");
        chkGerarIdDosProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkGerarIdDosProdutosActionPerformed(evt);
            }
        });

        chkCorrecaoCliente.setText("Corrigir Dia Vencimento Rotativo e Bloqueado");
        chkCorrecaoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCorrecaoClienteActionPerformed(evt);
            }
        });

        chkCorrigirEstoque.setText("Corrigir Estoque");
        chkCorrigirEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCorrigirEstoqueActionPerformed(evt);
            }
        });

        chkCopiarListaDeProduto.setText("Copiar lista de produto");
        chkCopiarListaDeProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCopiarListaDeProdutoActionPerformed(evt);
            }
        });

        chkImportarSenhas.setText("Importar senhas dos clientes ");
        chkImportarSenhas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImportarSenhasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEspeciaisLayout = new javax.swing.GroupLayout(pnlEspeciais);
        pnlEspeciais.setLayout(pnlEspeciaisLayout);
        pnlEspeciaisLayout.setHorizontalGroup(
            pnlEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMapearMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkGerarIdParaClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkGerarIdDosProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCorrecaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCorrigirEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiarListaDeProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkImportarSenhas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(393, Short.MAX_VALUE))
        );
        pnlEspeciaisLayout.setVerticalGroup(
            pnlEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkMapearMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(chkGerarIdDosProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkGerarIdParaClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCorrecaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCorrigirEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkCopiarListaDeProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkImportarSenhas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        tab.addTab("Operações especiais", pnlEspeciais);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        if (cmbLojaCliente.getItemCount() > 0){
            try {
                this.setWaitCursor();
                importarTabelas();

            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());

            } finally {
                this.setDefaultCursor();
            }            
        }else{
            try {
                Util.exibirMensagem("Informe o código da Loja do Cliente!", getTitle());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            cmbLojaCliente.grabFocus();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void btnConectarSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarSqlServerActionPerformed
        try {
            this.setWaitCursor();

            if (conn != null) {
                conn.close();
            }

            validarDadosAcessoSqlServer();
            btnConectarSqlServer.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            btnConectarSqlServer.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png")));            

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarSqlServerActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void txtPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaActionPerformed
        
    }//GEN-LAST:event_txtPortaActionPerformed

    private void cmbLojaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaDestinoActionPerformed
        
    }//GEN-LAST:event_cmbLojaDestinoActionPerformed

    private void txtBancoDadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBancoDadosActionPerformed
        
    }//GEN-LAST:event_txtBancoDadosActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void cmbLojaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaClienteActionPerformed
        
    }//GEN-LAST:event_cmbLojaClienteActionPerformed

    private void chkCreditoRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCreditoRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCreditoRotativoActionPerformed

    private void chkClientesEventuaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientesEventuaisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientesEventuaisActionPerformed

    private void chkGerarIdParaClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkGerarIdParaClienteEventualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkGerarIdParaClienteEventualActionPerformed

    private void chkGerarIdDosProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkGerarIdDosProdutosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkGerarIdDosProdutosActionPerformed

    private void chkCorrecaoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCorrecaoClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCorrecaoClienteActionPerformed

    private void chkChequesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequesActionPerformed

    private void chkCorrigirEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCorrigirEstoqueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCorrigirEstoqueActionPerformed

    private void chkCopiarListaDeProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCopiarListaDeProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCopiarListaDeProdutoActionPerformed

    private void chkImportarSenhasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImportarSenhasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkImportarSenhasActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarSqlServer;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCheques;
    private vrframework.bean.checkBox.VRCheckBox chkClientesEventuais;
    private vrframework.bean.checkBox.VRCheckBox chkClientesPreferenciais;
    private vrframework.bean.checkBox.VRCheckBox chkCopiarListaDeProduto;
    private vrframework.bean.checkBox.VRCheckBox chkCorrecaoCliente;
    private vrframework.bean.checkBox.VRCheckBox chkCorrigirEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCusto;
    private vrframework.bean.checkBox.VRCheckBox chkEAN;
    private vrframework.bean.checkBox.VRCheckBox chkEANEmBranco;
    private vrframework.bean.checkBox.VRCheckBox chkEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedores;
    private vrframework.bean.checkBox.VRCheckBox chkGerarIdDosProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkGerarIdParaClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkImportarSenhas;
    private vrframework.bean.checkBox.VRCheckBox chkMapaMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkMapearMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPreco;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private javax.swing.JComboBox cmbLojaCliente;
    private javax.swing.JComboBox cmbLojaDestino;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.panel.VRPanel pnlClientesPreferenciais1;
    private vrframework.bean.panel.VRPanel pnlEspeciais;
    private vrframework.bean.panel.VRPanel pnlFornecedores;
    private vrframework.bean.panel.VRPanel pnlImportarDados;
    private vrframework.bean.panel.VRPanel pnlProdutos;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrframework.bean.textField.VRTextField txtBancoDados;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrframework.bean.button.VRButton vRButton1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
