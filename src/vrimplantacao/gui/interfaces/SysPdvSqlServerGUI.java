package vrimplantacao.gui.interfaces;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.SysPdvSqlServerDAO;
import vrimplantacao.vo.loja.LojaVO;

public class SysPdvSqlServerGUI extends VRInternalFrame {

    private final SysPdvSqlServerDAO importacaoSysPdvSqlServerDAO = new SysPdvSqlServerDAO();
    private List<LojaVO> vLojaDestino = new ArrayList<>();
    private final ConexaoSqlServer connSQLServer = new ConexaoSqlServer();

    public SysPdvSqlServerGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        txtHostSqlServer.setText("127.0.0.1");
        txtBancoDadosSqlServer.setText("SYSPDV");
        //txtUsuarioSqlServer.setText("sa");
        //txtSenhaSqlServer.setText("$ql@c3bd");
        txtUsuarioSqlServer.setText("as");
        txtSenhaSqlServer.setText("vrimplantacao");
        txtPortaSqlServer.setText("1433");  
        txtLojaCliente.setText("1");
        
        chkClienteDuplicado.setVisible(false);

        centralizarForm();
        this.setMaximum(false);

    }

    public void validarDadosAcessoSqlServer() throws Exception {
        if (txtHostSqlServer.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados SqlServer!");
        }
        if (txtBancoDadosSqlServer.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados SqlServer!");
        }

        if (txtSenhaSqlServer.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados SqlServer!");
        }

        if (txtPortaSqlServer.getText().isEmpty()) {
            throw new VRException("Favor informar a porta do banco de dados SqlServer!");
        }
        if (txtUsuarioSqlServer.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados SqlServer!");
        }
        if (txtLojaCliente.getText().isEmpty()) {
            throw new VRException("Favor informar o codigo da loja do banco de dados SqlServer!");
        }

        connSQLServer.abrirConexao(txtHostSqlServer.getText(), txtPortaSqlServer.getInt(), 
                txtBancoDadosSqlServer.getText(), txtUsuarioSqlServer.getText(), txtSenhaSqlServer.getText());

        vLojaDestino = new LojaDAO().carregar();

        carregarLojaDestino();
    }

    public void carregarLojaDestino() throws Exception {
        cmbLojaDestino.removeAllItems();
        for (LojaVO oLoja : vLojaDestino) {
            cmbLojaDestino.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }

    public void importarProdutosBalanca() {
        Thread thread = new Thread() {
            
            int opcao;
            
            @Override
            public void run() {
                
                try {
                    
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    
                    if ((!txtArquivoBalanca.getArquivo().isEmpty()) &&
                            (rdbCadTxt.isSelected()) ||
                            (rdbTxtItens.isSelected()) ||
                            (rdbItensMgv.isSelected())) {
                        
                        if (rdbCadTxt.isSelected()) {
                            opcao = 1;
                        } else if (rdbTxtItens.isSelected()) {
                            opcao = 2;
                        } else if (rdbItensMgv.isSelected()) {
                            opcao = 3;
                        }
                        
                        importacaoSysPdvSqlServerDAO.importarProdutoBalanca(txtArquivoBalanca.getArquivo(), opcao);
                    }
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação Milenio realizada com sucesso!", getTitle());
                    
                } catch(Exception ex) {
                    
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
            
            
        };
                
        thread.start();
    }
    
    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, idLojaCliente;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    idLojaVR = cmbLojaDestino.getSelectedIndex()+1;                    
                    idLojaCliente = txtLojaCliente.getInt();

                    if (chkFamiliaProduto.isSelected()) {                        
                        importacaoSysPdvSqlServerDAO.importarFamiliaProduto();
                    }
                    
                    if (chkMercadologico.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarMercadologico();
                    }
                    
                    if (chkProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarProduto6(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkCustoProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarCustoProduto(idLojaVR, idLojaCliente);
                    }                    
                    
                    if (chkPrecoProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarPrecoProduto(idLojaVR, idLojaCliente);
                    }                                        
                    
                    if (chkEstoqueProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarEstoqueProduto(idLojaVR, idLojaCliente);
                    }                      
                    
                    if (chkFornecedor.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarFornecedor();
                    } 
                    
                    if (chkProdutoFornecedor.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarProdutoFornecedor();
                    }

                    if (chkClientePreferencial.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarClientePreferencial(idLojaVR, idLojaCliente);
                    } 
                    
                    if (chkCreditoRotativo.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarReceberCliente(idLojaVR, idLojaCliente);
                    }                    
                    
                    if (chkChequeReceber.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarChequeReceber(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkClienteDuplicado.isSelected()) {
                        importacaoSysPdvSqlServerDAO.corrigirClienteDuplicado();
                    }
                    
                    if (chkCodigobarras.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarCodigoBarra();
                    }
                    
                    if (chkICMS.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarIcmsProduto();
                    }
                    
                    if (chkPisCofins.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarPisCofinsProdutoSysPdv();
                    }
                    
                    if (chkCEST.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarCEST();
                    }
                    
                    if (chkFamilia.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarProdutoFamiliaSysPdv();
                    }

                    if (chkTipoEmbalagem.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarTipoEmbalagem();
                    }
                    
                    if (chkEanEmBranco.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarEanEmBranco();
                    }
                    
                    if (chkDatCadProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarDataProduto();
                    }
                    
                    if (chkAcertarMercadologico.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarMercadologicoProduto();
                    }
                    
                    if (chkOfertas.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarOfertas(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkSituacaoProduto.isSelected()) {
                        importacaoSysPdvSqlServerDAO.ajustarSituacaoCadastral(idLojaVR, idLojaCliente);
                    }                   
                    
                    if (chkCodigoAnterior.isSelected()) {
                        importacaoSysPdvSqlServerDAO.ajustarCodigoAnterior(idLojaVR, idLojaCliente);
                    }
                    
                    if (chkRotativoTudo.isSelected()) {
                        importacaoSysPdvSqlServerDAO.importarRotativoTudo(idLojaVR, idLojaCliente);
                    }
                    
                    
                    
                    //Ajustar tipo de embalagem de kilo para unitário
                    
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação SysPdv realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        connSQLServer.close();
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
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        txtUsuarioSqlServer = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaSqlServer = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaSqlServer = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostSqlServer = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarSqlServer = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtLojaCliente = new vrframework.bean.textField.VRTextField();
        txtBancoDadosSqlServer = new vrframework.bean.textField.VRTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        cmbLojaDestino = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        txtArquivoBalanca = new vrframework.bean.fileChooser.VRFileChooser();
        rdbCadTxt = new vrframework.bean.radioButton.VRRadioButton();
        rdbTxtItens = new vrframework.bean.radioButton.VRRadioButton();
        rdbItensMgv = new vrframework.bean.radioButton.VRRadioButton();
        btnImportarBalanca = new vrframework.bean.button.VRButton();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        Dados = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkChequeReceber = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCodigobarras = new vrframework.bean.checkBox.VRCheckBox();
        chkPisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagem = new vrframework.bean.checkBox.VRCheckBox();
        chkDatCadProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkSituacaoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkEanEmBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkCEST = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkCodigoAnterior = new vrframework.bean.checkBox.VRCheckBox();
        chkAjustarBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkAcertarMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteDuplicado = new vrframework.bean.checkBox.VRCheckBox();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        chkRotativoTudo = new vrframework.bean.checkBox.VRCheckBox();

        setTitle("Importação SysPdv (SQL Server)");
        setToolTipText("");

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - SQL Server"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioSqlServer.setCaixaAlta(false);
        txtUsuarioSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioSqlServerActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenhaSqlServer.setCaixaAlta(false);
        txtSenhaSqlServer.setMascara("");
        txtSenhaSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaSqlServerActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaSqlServer.setCaixaAlta(false);
        txtPortaSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaSqlServerActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostSqlServer.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarSqlServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarSqlServer.setText("Conectar");
        btnConectarSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarSqlServerActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtLojaCliente.setCaixaAlta(false);
        txtLojaCliente.setMascara("Numero");
        txtLojaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLojaClienteActionPerformed(evt);
            }
        });

        txtBancoDadosSqlServer.setCaixaAlta(false);
        txtBancoDadosSqlServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBancoDadosSqlServerActionPerformed(evt);
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
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuarioSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPortaSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConectarSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHostSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtBancoDadosSqlServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                            .addComponent(txtHostSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBancoDadosSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)))
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vRPanel1Layout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(vRLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenhaSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPortaSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarSqlServer)
                    .addComponent(txtUsuarioSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGap(13, 13, 13)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Arquivo Balança"));

        rdbCadTxt.setText("CADTXT");
        rdbCadTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCadTxtActionPerformed(evt);
            }
        });

        rdbTxtItens.setText("TXTITENS");
        rdbTxtItens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbTxtItensActionPerformed(evt);
            }
        });

        rdbItensMgv.setText("ITENSMGV");
        rdbItensMgv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbItensMgvActionPerformed(evt);
            }
        });

        btnImportarBalanca.setText("Importar");
        btnImportarBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarBalancaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel4Layout.createSequentialGroup()
                        .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnImportarBalanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        Dados.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Tabelas"));
        Dados.setPreferredSize(new java.awt.Dimension(350, 350));

        chkFamiliaProduto.setText("Familia Produto");

        chkMercadologico.setText("Mercadologico");

        chkProduto.setText("Produto");
        chkProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoActionPerformed(evt);
            }
        });

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");

        chkClientePreferencial.setText("Cliente Preferencial");

        chkChequeReceber.setText("Cheque Receber");

        chkCustoProduto.setText("Custo Produto");
        chkCustoProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCustoProdutoActionPerformed(evt);
            }
        });

        chkPrecoProduto.setText("Preço Produto");
        chkPrecoProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrecoProdutoActionPerformed(evt);
            }
        });

        chkCreditoRotativo.setText("Credito Rotativo");

        chkEstoqueProduto.setText("Estoque Produto");
        chkEstoqueProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEstoqueProdutoActionPerformed(evt);
            }
        });

        chkCodigobarras.setText("Código Barras");

        chkPisCofins.setText("PisCofins");

        chkFamilia.setText("Familia");

        chkTipoEmbalagem.setText("Tipo Embalagem");

        chkDatCadProduto.setText("Dt.Cad Produto");

        chkSituacaoProduto.setText("Situação Produto");

        chkICMS.setText("ICMS");

        chkEanEmBranco.setText("EAN em branco");

        chkCEST.setText("CEST");

        javax.swing.GroupLayout DadosLayout = new javax.swing.GroupLayout(Dados);
        Dados.setLayout(DadosLayout);
        DadosLayout.setHorizontalGroup(
            DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCodigobarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEanEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSituacaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(DadosLayout.createSequentialGroup()
                        .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkDatCadProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(169, 169, 169))
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkCEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(31, 31, 31)))
                        .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        DadosLayout.setVerticalGroup(
            DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DadosLayout.createSequentialGroup()
                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DadosLayout.createSequentialGroup()
                        .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCodigobarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDatCadProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(DadosLayout.createSequentialGroup()
                        .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(DadosLayout.createSequentialGroup()
                                .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSituacaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEanEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Importar dados", Dados);

        chkCodigoAnterior.setText("Código Anterior");

        chkAjustarBalanca.setText("Ajustar balança");

        chkAcertarMercadologico.setText("Acertar Mercadologico");

        chkClienteDuplicado.setText("Cliente Duplicado");

        chkOfertas.setText("Ofertas");

        chkRotativoTudo.setText("Rotativo: importar tudo");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(chkAjustarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkClienteDuplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAcertarMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRotativoTudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(573, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkAjustarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(chkCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAcertarMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteDuplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkRotativoTudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        tabs.addTab("Especiais", vRPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        if (!txtLojaCliente.getText().isEmpty()){
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
            txtLojaCliente.grabFocus();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void btnConectarSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarSqlServerActionPerformed
        try {
            this.setWaitCursor();

            if (connSQLServer != null) {
                connSQLServer.close();
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

    private void rdbCadTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCadTxtActionPerformed
        
        if (rdbCadTxt.isSelected()) {
            rdbTxtItens.setSelected(false);
            rdbItensMgv.setSelected(false);
        }
    }//GEN-LAST:event_rdbCadTxtActionPerformed

    private void rdbTxtItensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbTxtItensActionPerformed
        
        if (rdbTxtItens.isSelected()) {
            rdbCadTxt.setSelected(false);
            rdbItensMgv.setSelected(false);
        }
    }//GEN-LAST:event_rdbTxtItensActionPerformed

    private void rdbItensMgvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbItensMgvActionPerformed
        
        if (rdbItensMgv.isSelected()) {
            rdbCadTxt.setSelected(false);
            rdbTxtItens.setSelected(false);
        }
    }//GEN-LAST:event_rdbItensMgvActionPerformed

    private void txtSenhaSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaSqlServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaSqlServerActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void txtPortaSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaSqlServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaSqlServerActionPerformed

    private void cmbLojaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaDestinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLojaDestinoActionPerformed

    private void txtLojaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLojaClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLojaClienteActionPerformed

    private void chkProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoActionPerformed
        // TODO add your handling code here:
        chkCustoProduto.setSelected((chkProduto.isSelected()));
        chkPrecoProduto.setSelected((chkProduto.isSelected()));
        chkEstoqueProduto.setSelected((chkProduto.isSelected()));
    }//GEN-LAST:event_chkProdutoActionPerformed

    private void chkCustoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCustoProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCustoProdutoActionPerformed

    private void chkEstoqueProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEstoqueProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkEstoqueProdutoActionPerformed

    private void btnImportarBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarBalancaActionPerformed
        importarProdutosBalanca();
    }//GEN-LAST:event_btnImportarBalancaActionPerformed

    private void chkPrecoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrecoProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrecoProdutoActionPerformed

    private void txtBancoDadosSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBancoDadosSqlServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBancoDadosSqlServerActionPerformed

    private void txtUsuarioSqlServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioSqlServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioSqlServerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.panel.VRPanel Dados;
    private javax.swing.JToggleButton btnConectarSqlServer;
    private vrframework.bean.button.VRButton btnImportarBalanca;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAcertarMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkAjustarBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkCEST;
    private vrframework.bean.checkBox.VRCheckBox chkChequeReceber;
    private vrframework.bean.checkBox.VRCheckBox chkClienteDuplicado;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCodigoAnterior;
    private vrframework.bean.checkBox.VRCheckBox chkCodigobarras;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCustoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkDatCadProduto;
    private vrframework.bean.checkBox.VRCheckBox chkEanEmBranco;
    private vrframework.bean.checkBox.VRCheckBox chkEstoqueProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkICMS;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkRotativoTudo;
    private vrframework.bean.checkBox.VRCheckBox chkSituacaoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagem;
    private javax.swing.JComboBox cmbLojaDestino;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.radioButton.VRRadioButton rdbCadTxt;
    private vrframework.bean.radioButton.VRRadioButton rdbItensMgv;
    private vrframework.bean.radioButton.VRRadioButton rdbTxtItens;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.fileChooser.VRFileChooser txtArquivoBalanca;
    private vrframework.bean.textField.VRTextField txtBancoDadosSqlServer;
    private vrframework.bean.textField.VRTextField txtHostSqlServer;
    private vrframework.bean.textField.VRTextField txtLojaCliente;
    private vrframework.bean.textField.VRTextField txtPortaSqlServer;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaSqlServer;
    private vrframework.bean.textField.VRTextField txtUsuarioSqlServer;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
