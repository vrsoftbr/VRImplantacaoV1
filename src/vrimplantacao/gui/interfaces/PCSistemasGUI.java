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
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.PCSistemasDAO;
import vrimplantacao.dao.interfaces.VRSoftwareDAO;
import vrimplantacao.vo.loja.LojaVO;


public class PCSistemasGUI extends VRInternalFrame {

    private VRSoftwareDAO migracaoVRDAO = new VRSoftwareDAO();
    private PCSistemasDAO importacaoPCSistemas = new PCSistemasDAO();
    private List<LojaVO> vLojaOrigem = new ArrayList<>();
    private List<LojaVO> vLojaDestino = new ArrayList<>();
    private ConexaoOracle connOracle = new ConexaoOracle();

    public PCSistemasGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        txtHostOracle.setText("192.168.0.66");
        txtBancoDadosOracle.setText("WINT");
        txtUsuarioOracle.setText("oeste01");
        txtSenhaOracle.setText("oeste01");
        txtPortaOracle.setText("1521");
        txtLojaCliente.setText("1");

        carregarLojaDestino();
        
        centralizarForm();
        this.setMaximum(false);

    }

    public void validarDadosAcessoOracle() throws Exception {
        if (txtHostOracle.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados Oracle!");
        }
        if (txtBancoDadosOracle.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados Oracle!");
        }

        if (txtSenhaOracle.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados Oracle!");
        }

        if (txtPortaOracle.getText().isEmpty()) {
            throw new VRException("Favor informar a porta do banco de dados Oracle!");
        }
        if (txtUsuarioOracle.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados Oracle!");
        }

        connOracle.abrirConexao(txtHostOracle.getText(), txtPortaOracle.getInt(), 
                txtBancoDadosOracle.getText(), txtUsuarioOracle.getText(), txtSenhaOracle.getText());

        vLojaDestino = new LojaDAO().carregar();
        
        
    }

    private void carregarLojaDestino() throws Exception {
        List<LojaVO> vLojaDestino = new LojaDAO().carregar();
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
                        
                        importacaoPCSistemas.importarProdutoBalanca(txtArquivoBalanca.getArquivo(), opcao);
                    }
                    
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação RM(TOTVS) realizada com sucesso!", getTitle());
                    
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
            int id_loja, id_lojaCliente;
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    id_loja = cmbLojaDestino.getSelectedIndex()+1;                                        
                    id_lojaCliente = txtLojaCliente.getInt();

                    if (chkFamiliaProduto.isSelected()) {                        
                        importacaoPCSistemas.importarFamiliaProduto();
                    }
                    
                    if (chkMercadologico.isSelected()) {
                        importacaoPCSistemas.importarMercadologico();
                    }
                    
                    if (chkProduto.isSelected()) {
                        importacaoPCSistemas.importarProduto(cmbLojaDestino.getSelectedIndex() + 1);
                    }
                    
                    if (chkCustoProduto.isSelected()) {
                        importacaoPCSistemas.importarCustoProduto(txtLojaCliente.getInt(), cmbLojaDestino.getSelectedIndex() + 1);
                    }                    
                    
                    if (chkPrecoProduto.isSelected()) {
                        importacaoPCSistemas.importarPrecoProduto(cmbLojaDestino.getSelectedIndex() + 1);
                    }                                        
                    
                    if (chkEstoqueProduto.isSelected()) {
                        importacaoPCSistemas.importarEstoqueProduto(id_loja);
                    }                      
                    
                    if (chkPisCofinsProduto.isSelected()) {
                        importacaoPCSistemas.importarPisCofinsProdutO();
                    } 
                    
                    if (chkICMS.isSelected()) {
                        importacaoPCSistemas.importarIcmsProduto();
                    }                    
                    
                    if (chkFornecedor.isSelected()) {
                        importacaoPCSistemas.importarFornecedor();
                    } 
                    
                    if (chkProdutoFornecedor.isSelected()) {
                        importacaoPCSistemas.importarProdutoFornecedor();
                    }

                    if (chkClientePreferencial.isSelected()) {
                        importacaoPCSistemas.importarClientePreferencial(id_loja, id_lojaCliente);
                    } 
                    
                    if (chkConveniado.isSelected()) {
                        importacaoPCSistemas.importarConvenio(id_loja, id_lojaCliente);
                    }
                    
                    if (chkCreditoRotativo.isSelected()) {
                        importacaoPCSistemas.importarReceberCreditoRotativo(id_loja, id_lojaCliente);                        
                    }                                        

                    if (chkCreditoConvenio.isSelected()) {
                        importacaoPCSistemas.importarReceberConveniadoTransacao(id_loja, id_lojaCliente);
                    }                    
                    
                    if (chkChequeReceber.isSelected()) {
                        importacaoPCSistemas.importarChequeReceber(id_loja, id_lojaCliente);
                    }
                    
                    if (chkCodigoBarras.isSelected()) {
                        importacaoPCSistemas.importarCodigoBarra();
                    }                    
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação PCSistemas realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        connOracle.close();
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
        txtUsuarioOracle = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaOracle = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaOracle = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostOracle = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarOracle = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtLojaCliente = new vrframework.bean.textField.VRTextField();
        txtBancoDadosOracle = new vrframework.bean.textField.VRTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        cmbLojaDestino = new javax.swing.JComboBox();
        chkChequeReceber = new vrframework.bean.checkBox.VRCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkCustoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkPisCofinsProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCodigoBarras = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoConvenio = new vrframework.bean.checkBox.VRCheckBox();
        chkConveniado = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        txtArquivoBalanca = new vrframework.bean.fileChooser.VRFileChooser();
        rdbCadTxt = new vrframework.bean.radioButton.VRRadioButton();
        rdbTxtItens = new vrframework.bean.radioButton.VRRadioButton();
        rdbItensMgv = new vrframework.bean.radioButton.VRRadioButton();
        btnImportarBalanca = new vrframework.bean.button.VRButton();

        setTitle("Importação PC Sistemas (Oracle)");
        setToolTipText("");

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - ORACLE"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioOracle.setText("gwuniao");
        txtUsuarioOracle.setCaixaAlta(false);
        txtUsuarioOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioOracleActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenhaOracle.setText("gwuniao");
        txtSenhaOracle.setCaixaAlta(false);
        txtSenhaOracle.setMascara("");
        txtSenhaOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaOracleActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaOracle.setText("1433");
        txtPortaOracle.setCaixaAlta(false);
        txtPortaOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaOracleActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostOracle.setText("192.168.1.100");
        txtHostOracle.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarOracle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarOracle.setText("Conectar");
        btnConectarOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarOracleActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtLojaCliente.setText("35");
        txtLojaCliente.setCaixaAlta(false);
        txtLojaCliente.setMascara("Numero");
        txtLojaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLojaClienteActionPerformed(evt);
            }
        });

        txtBancoDadosOracle.setText("GWOLAP");
        txtBancoDadosOracle.setCaixaAlta(false);
        txtBancoDadosOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBancoDadosOracleActionPerformed(evt);
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
                            .addComponent(txtHostOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtBancoDadosOracle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuarioOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPortaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                                .addComponent(btnConectarOracle)))))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBancoDadosOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtHostOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vRPanel1Layout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenhaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPortaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuarioOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarOracle)))
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

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(474, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Tabelas"));
        vRPanel2.setPreferredSize(new java.awt.Dimension(350, 350));

        chkFamiliaProduto.setText("Familia Produto");
        chkFamiliaProduto.setEnabled(true);

        chkMercadologico.setText("Mercadologico");
        chkMercadologico.setEnabled(true);
        chkMercadologico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMercadologicoActionPerformed(evt);
            }
        });

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

        cmbLojaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaDestinoActionPerformed(evt);
            }
        });

        chkChequeReceber.setText("Cheque Receber");

        jLabel1.setText("Loja:");

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

        chkICMS.setText("ICMS");
        chkICMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkICMSActionPerformed(evt);
            }
        });

        chkPisCofinsProduto.setText("PisCofins");
        chkPisCofinsProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPisCofinsProdutoActionPerformed(evt);
            }
        });

        chkCodigoBarras.setText("Codigo Barras");
        chkCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCodigoBarrasActionPerformed(evt);
            }
        });

        chkCreditoConvenio.setText("Credito Convenio");
        chkCreditoConvenio.setEnabled(true);

        chkConveniado.setText("Conveniado");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel2Layout.createSequentialGroup()
                                .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)))
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPisCofinsProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCreditoConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPisCofinsProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCreditoConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

    private void btnConectarOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarOracleActionPerformed
        try {
            this.setWaitCursor();

            if (connOracle != null) {
                connOracle.close();
            }

            validarDadosAcessoOracle();
            btnConectarOracle.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarOracleActionPerformed

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

    private void txtSenhaOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaOracleActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void txtPortaOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaOracleActionPerformed

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
        if (txtArquivoBalanca.getArquivo().isEmpty()) {
            try {
                Util.exibirMensagem("Arquivo da Balança Inválido!", "Aviso");
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }else{
            importarProdutosBalanca();
        }
    }//GEN-LAST:event_btnImportarBalancaActionPerformed

    private void chkPrecoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrecoProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrecoProdutoActionPerformed

    private void txtBancoDadosOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBancoDadosOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBancoDadosOracleActionPerformed

    private void txtUsuarioOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioOracleActionPerformed

    private void chkICMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkICMSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkICMSActionPerformed

    private void chkPisCofinsProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPisCofinsProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPisCofinsProdutoActionPerformed

    private void chkCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCodigoBarrasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCodigoBarrasActionPerformed

    private void chkMercadologicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMercadologicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMercadologicoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarOracle;
    private vrframework.bean.button.VRButton btnImportarBalanca;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkChequeReceber;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCodigoBarras;
    private vrframework.bean.checkBox.VRCheckBox chkConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoConvenio;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCustoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkEstoqueProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkICMS;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkPisCofinsProduto;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private javax.swing.JComboBox cmbLojaDestino;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.radioButton.VRRadioButton rdbCadTxt;
    private vrframework.bean.radioButton.VRRadioButton rdbItensMgv;
    private vrframework.bean.radioButton.VRRadioButton rdbTxtItens;
    private vrframework.bean.fileChooser.VRFileChooser txtArquivoBalanca;
    private vrframework.bean.textField.VRTextField txtBancoDadosOracle;
    private vrframework.bean.textField.VRTextField txtHostOracle;
    private vrframework.bean.textField.VRTextField txtLojaCliente;
    private vrframework.bean.textField.VRTextField txtPortaOracle;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaOracle;
    private vrframework.bean.textField.VRTextField txtUsuarioOracle;
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
