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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.SysPdvDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.parametro.Parametros;

public class SysPdvGUI extends VRInternalFrame {

    private SysPdvDAO importacaoSysPdvDAO = new SysPdvDAO();
    private List<LojaVO> vLojaOrigem = new ArrayList<>();
    private List<LojaVO> vLojaDestino = new ArrayList<>();
    private ConexaoFirebird connFB = new ConexaoFirebird();

    public SysPdvGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        txtHostFirebird.setText("localhost");
        txtBancoDadosFirebird.setArquivo("C:\\SYSPDV\\syspdv_srv.fdb");
        txtUsuarioFirebird.setText("SYSDBA");
        txtSenhaFirebird.setText("masterkey");
        txtPortaFirebird.setText("3050");  
        txtLojaCliente.setText("1");
        txtCidadePadrao.setText("1501402");
        txtCepPadrao.setText("66640225");
        txtCidadeDescricaoPadrao.setText("BELEM");
        txtUFSiglaPadrao.setText("PA");
        centralizarForm();
        this.setMaximum(false);

    }

    private void carregarTipoDocumento() throws Exception {
        cmbTipoDocRotativo.removeAllItems();
        cmbTipoDocRotativo.setModel(new DefaultComboBoxModel());
        cmbTipoDocCreditoRotativoInt.removeAllItems();
        cmbTipoDocCreditoRotativoInt.setModel(new DefaultComboBoxModel());        
        cmbTipoDocCheque.removeAllItems();
        cmbTipoDocCheque.setModel(new DefaultComboBoxModel());
        
        cmbTipoDocRotativo.addItem(new ItemComboVO(0, "--VAZIO--"));
        cmbTipoDocCreditoRotativoInt.addItem(new ItemComboVO(0, "--VAZIO--"));
        cmbTipoDocCheque.addItem(new ItemComboVO(0, "--VAZIO--"));
        
        for (ItemComboVO item: importacaoSysPdvDAO.getTipoDocumento()) {
            cmbTipoDocRotativo.addItem(item);
            cmbTipoDocCreditoRotativoInt.addItem(item);
            cmbTipoDocCheque.addItem(item);
        }
    }
    
    public void validarDadosAcessoFirebird() throws Exception {
        if (txtHostFirebird.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados Firebird!");
        }
        if (txtBancoDadosFirebird.getArquivo().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados Firebird!");
        }

        if (txtSenhaFirebird.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados Firebird!");
        }

        if (txtPortaFirebird.getText().isEmpty()) {
            throw new VRException("Favor informar a porta do banco de dados Firebird!");
        }
        if (txtUsuarioFirebird.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados Firebird!");
        }
        if (txtLojaCliente.getText().isEmpty()) {
            throw new VRException("Favor informar o codigo da loja do banco de dados Firebird!");
        }

        connFB.abrirConexao(txtHostFirebird.getText(), txtPortaFirebird.getInt(), 
                txtBancoDadosFirebird.getArquivo(), txtUsuarioFirebird.getText(), txtSenhaFirebird.getText());

        vLojaDestino = new LojaDAO().carregar();

        carregarLojaDestino();
        carregarTipoDocumento();
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
                        
                        importacaoSysPdvDAO.importarProdutoBalanca(txtArquivoBalanca.getArquivo(), opcao);
                    }
                    
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação SysPdv realizada com sucesso!", getTitle());
                    
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
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    Global.idEstado = uf.getId();
                    Global.idMunicipio = Parametros.get().getMunicipioPadrao2().getId();
                    Global.Cep = Parametros.get().getCepPadrao();
                    
                    if (tabs.getSelectedIndex() != 2) {                    
                        importacaoSysPdvDAO.setImportarSomenteAtivos(chkImportarSomenteAtivos.isSelected());

                        if (chkFamiliaProduto.isSelected()) {                        
                            importacaoSysPdvDAO.importarFamilia();
                        }

                        if (chkMercadologico.isSelected()) {
                            importacaoSysPdvDAO.importarMercadologico();
                        }

                        if (chkProduto.isSelected()) {
                            importacaoSysPdvDAO.importarProduto(idLojaVR, idLojaCliente);
                        }

                        if (chkCustoProduto.isSelected()) {
                            importacaoSysPdvDAO.importarCustoProduto(idLojaVR, idLojaCliente);
                        }                    

                        if (chkPrecoProduto.isSelected()) {
                            importacaoSysPdvDAO.importarPrecoProduto(idLojaVR, idLojaCliente);
                        }                                        

                        if (chkEstoqueProduto.isSelected()) {
                            importacaoSysPdvDAO.importarEstoqueProduto(idLojaVR, idLojaCliente);
                        } 

                        if (chkCodigobarras.isSelected()) {
                            importacaoSysPdvDAO.importarCodigoBarra();
                        }
                        
                        if (chkProdutoSemCodigoBarras.isSelected()) {
                            importacaoSysPdvDAO.importarCodigoBarraEmBranco();
                        }

                        if (chkTipoEmbalagem.isSelected()) {
                            importacaoSysPdvDAO.importarTipoEmbalagem();
                        }
                        
                        if (chkCEST.isSelected()) {
                            importacaoSysPdvDAO.importarCEST();
                        }
                        
                        if (chkICMS.isSelected()) {
                            importacaoSysPdvDAO.importarIcmsProduto();
                        }
                        
                        if (chkPisCofins.isSelected()) {
                            importacaoSysPdvDAO.importarPisCofinsProdutoSysPdv();
                        }

                        if (chkFamilia.isSelected()) {
                            importacaoSysPdvDAO.importarProdutoFamilia();
                        }

                        if (chkFornecedor.isSelected()) {
                            importacaoSysPdvDAO.importarFornecedor();
                        } 

                        if (chkProdutoFornecedor.isSelected()) {
                            importacaoSysPdvDAO.importarProdutoFornecedor();
                        }

                        if (chkClientePreferencial.isSelected()) {
                            importacaoSysPdvDAO.importarClientePreferencial(idLojaVR, idLojaCliente);
                        } 

                        if (chkCreditoRotativo.isSelected()) {
                            importacaoSysPdvDAO.importarReceberCliente(idLojaVR, idLojaCliente, chkChequesComoRotativo.isSelected(),
                                    ((ItemComboVO)cmbTipoDocRotativo.getSelectedItem()).id, txtCodAgente.getText());
                        }                    

                        if (chkChequeReceber.isSelected()) {
                            importacaoSysPdvDAO.importarChequeReceber(idLojaVR, idLojaCliente, 
                                    ((ItemComboVO)cmbTipoDocCheque.getSelectedItem()).id);
                        }

                        if (chkOfertas.isSelected()) {
                            importacaoSysPdvDAO.importarOfertas(idLojaVR, idLojaCliente, edtDtOfertas.getText());
                        }

                        if (chkCorrigeBalanca.isSelected()) {
                            importacaoSysPdvDAO.corrigirProdutoDeBalanca(idLojaVR, idLojaCliente);
                        }
                
                        if (chkProdutoAtacado.isSelected()) {
                            importacaoSysPdvDAO.importarProdutoAutomacaoLoja(idLojaVR);
                        }
                        
                        if (chkOfertaSemData.isSelected()) {
                            importacaoSysPdvDAO.importarOfertaSemData(idLojaVR, idLojaCliente);
                        }
                        
                        if (chkImportICMSEstadoLoja.isSelected()) {
                            importacaoSysPdvDAO.importarIcmsLoja(idLojaVR, idLojaCliente);
                        }
                        
                        if (chkCorrigeBloqueado.isSelected()) {
                            importacaoSysPdvDAO.corrigirClienteBloqueado(idLojaVR, idLojaCliente);
                        }
                        
                        if (chkCreditoRotativoManual.isSelected()) {
                            importacaoSysPdvDAO.importarReceberCliente(idLojaVR, idLojaCliente, chkChequesComoRotativo.isSelected(),
                                    Utils.stringToInt(txtCreditoRotarivoManual.getText()), txtCodAgente2.getText());
                        }
                        
                        if (chkEmprestimo.isSelected()) {
                            importacaoSysPdvDAO.importarImprestimos(idLojaVR, idLojaCliente, txtEmprestimoTipoDoc.getText());
                        }
                    } else {
                        
                        if (chkProdutoIntegracao.isSelected()) {
                            importacaoSysPdvDAO.importarProdutoIntegracao(idLojaVR, idLojaCliente, 
                                    txtPathLog.getText().trim());
                        }
                        
                        if (chkCustoProdutoSysPDV.isSelected()) {
                            importacaoSysPdvDAO.integImportarCustoSysPDV(idLojaVR, idLojaCliente);
                        }

                        if (chkPrecoProdutoSysPDV.isSelected()) {
                            importacaoSysPdvDAO.integImportarPrecoSysPDV(idLojaVR, idLojaCliente);
                        }

                        if (chkEstoqueProdutoSysPDV.isSelected()) {
                            importacaoSysPdvDAO.integImportarEstoqueSysPDV(idLojaVR, idLojaCliente);
                        }
                        
                        if (chkCodigoBarrasInt.isSelected()) {
                            importacaoSysPdvDAO.importarCodigoBarraIntegracao(idLojaVR);
                        }
                        
                        if (chkClientePreferencialSysPDV.isSelected()) {
                            importacaoSysPdvDAO.integClientePreferencial(idLojaVR, idLojaCliente, 
                                    txtPathLog.getText().trim());
                        }
                        
                        if (chkCreditoRotativoSysPDV.isSelected()) {
                            importacaoSysPdvDAO.integCreditoRotativo(idLojaVR, idLojaCliente, chkChequesComoRotativo.isSelected(),
                                    ((ItemComboVO)cmbTipoDocCreditoRotativoInt.getSelectedItem()).id);
                        }
                    }
                    
                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação SysPdv realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {                    
                        connFB.close();
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
        txtUsuarioFirebird = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaFirebird = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaFirebird = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostFirebird = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarFirebird = new javax.swing.JToggleButton();
        txtBancoDadosFirebird = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtLojaCliente = new vrframework.bean.textField.VRTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaDestino = new javax.swing.JComboBox();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        txtArquivoBalanca = new vrframework.bean.fileChooser.VRFileChooser();
        rdbCadTxt = new vrframework.bean.radioButton.VRRadioButton();
        rdbTxtItens = new vrframework.bean.radioButton.VRRadioButton();
        rdbItensMgv = new vrframework.bean.radioButton.VRRadioButton();
        btnImportarBalanca = new vrframework.bean.button.VRButton();
        tabs = new javax.swing.JTabbedPane();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        vRPanel10 = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCodigobarras = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoSemCodigoBarras = new vrframework.bean.checkBox.VRCheckBox();
        chkCEST = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagem = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkPisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkImportarSomenteAtivos = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoAtacado = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel11 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel12 = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkChequeReceber = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        cmbTipoDocRotativo = new vrframework.bean.comboBox.VRComboBox();
        cmbTipoDocCheque = new vrframework.bean.comboBox.VRComboBox();
        txtCodAgente = new javax.swing.JTextField();
        chkEmprestimo = new vrframework.bean.checkBox.VRCheckBox();
        txtEmprestimoTipoDoc = new javax.swing.JTextField();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        edtDtOfertas = new vrframework.bean.textField.VRTextField();
        chkCorrigeBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        chkCorrigeBalanca = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        chkChequesComoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkOfertaSemData = new vrframework.bean.checkBox.VRCheckBox();
        chkImportICMSEstadoLoja = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativoManual = new vrframework.bean.checkBox.VRCheckBox();
        txtCreditoRotarivoManual = new vrframework.bean.textField.VRTextField();
        txtCodAgente2 = new javax.swing.JTextField();
        vRLabel13 = new vrframework.bean.label.VRLabel();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkCustoProdutoSysPDV = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoProdutoSysPDV = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueProdutoSysPDV = new vrframework.bean.checkBox.VRCheckBox();
        chkClientePreferencialSysPDV = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativoSysPDV = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoIntegracao = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        txtPathLog = new vrframework.bean.textField.VRTextField();
        vRLabel12 = new vrframework.bean.label.VRLabel();
        cmbTipoDocCreditoRotativoInt = new vrframework.bean.comboBox.VRComboBox();
        chkCodigoBarrasInt = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel9 = new vrframework.bean.panel.VRPanel();
        txtUFSiglaPadrao = new vrframework.bean.textField.VRTextField();
        vRLabel10 = new vrframework.bean.label.VRLabel();
        txtCidadeDescricaoPadrao = new vrframework.bean.textField.VRTextField();
        vRLabel11 = new vrframework.bean.label.VRLabel();
        txtCepPadrao = new vrframework.bean.textField.VRTextField();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        txtCidadePadrao = new vrframework.bean.textField.VRTextField();
        vRLabel6 = new vrframework.bean.label.VRLabel();

        setTitle("Importação SysPdv (Firebird)");
        setToolTipText("");

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Firebird"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioFirebird.setText("SYSDBA");
        txtUsuarioFirebird.setCaixaAlta(false);

        vRLabel4.setText("Usuário:");

        txtSenhaFirebird.setCaixaAlta(false);
        txtSenhaFirebird.setMascara("");
        txtSenhaFirebird.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaFirebirdActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaFirebird.setText("3050");
        txtPortaFirebird.setCaixaAlta(false);
        txtPortaFirebird.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaFirebirdActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostFirebird.setText("127.0.0.1");
        txtHostFirebird.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarFirebird.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarFirebird.setText("Conectar");
        btnConectarFirebird.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarFirebirdActionPerformed(evt);
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
                            .addComponent(txtUsuarioFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPortaFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConectarFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHostFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtBancoDadosFirebird, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHostFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(txtBancoDadosFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(txtSenhaFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPortaFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarFirebird)
                    .addComponent(txtUsuarioFirebird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jLabel1.setText("Loja:");

        cmbLojaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaDestinoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaDestino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(9, 9, 9))
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
                .addContainerGap()
                .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                    .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(80, 80, 80))
        );

        vRPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        vRPanel2.setPreferredSize(new java.awt.Dimension(350, 350));

        vRPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos"));

        chkFamiliaProduto.setText("Familia Produto");

        chkMercadologico.setText("Mercadologico");

        chkProduto.setText("Produto");
        chkProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoActionPerformed(evt);
            }
        });

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

        chkEstoqueProduto.setText("Estoque Produto");
        chkEstoqueProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEstoqueProdutoActionPerformed(evt);
            }
        });

        chkCodigobarras.setText("Código Barras");

        chkProdutoSemCodigoBarras.setText("Produtos sem Código Barras");

        chkCEST.setText("CEST");
        chkCEST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCESTActionPerformed(evt);
            }
        });

        chkTipoEmbalagem.setText("Tipo Embalagem");
        chkTipoEmbalagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTipoEmbalagemActionPerformed(evt);
            }
        });

        chkFamilia.setText("Família no Produto");
        chkFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaActionPerformed(evt);
            }
        });

        chkICMS.setText("ICMS");

        chkPisCofins.setText("PisCofins");

        chkImportarSomenteAtivos.setText("Ativos");
        chkImportarSomenteAtivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImportarSomenteAtivosActionPerformed(evt);
            }
        });

        chkProdutoAtacado.setText("Produtos Atacado");
        chkProdutoAtacado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoAtacadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel10Layout = new javax.swing.GroupLayout(vRPanel10);
        vRPanel10.setLayout(vRPanel10Layout);
        vRPanel10Layout.setHorizontalGroup(
            vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCodigobarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoSemCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkImportarSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoAtacado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel10Layout.setVerticalGroup(
            vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel10Layout.createSequentialGroup()
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCodigobarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrecoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoSemCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkImportarSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoqueProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        vRPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedor"));

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");

        javax.swing.GroupLayout vRPanel11Layout = new javax.swing.GroupLayout(vRPanel11);
        vRPanel11.setLayout(vRPanel11Layout);
        vRPanel11Layout.setHorizontalGroup(
            vRPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel11Layout.setVerticalGroup(
            vRPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        vRPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        chkClientePreferencial.setText("Cliente Preferencial");

        chkChequeReceber.setText("Cheque Receber");

        chkCreditoRotativo.setText("Credito Rotativo");

        txtCodAgente.setToolTipText("Codigo do Agente, se houver");

        chkEmprestimo.setText("Empréstimo (Tipo Doc)");

        txtEmprestimoTipoDoc.setToolTipText("\"Tipo de Documento\" utilizado para o empréstimo");

        javax.swing.GroupLayout vRPanel12Layout = new javax.swing.GroupLayout(vRPanel12);
        vRPanel12.setLayout(vRPanel12Layout);
        vRPanel12Layout.setHorizontalGroup(
            vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel12Layout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodAgente, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTipoDocRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbTipoDocCheque, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(vRPanel12Layout.createSequentialGroup()
                        .addComponent(chkEmprestimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmprestimoTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        vRPanel12Layout.setVerticalGroup(
            vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbTipoDocCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(chkChequeReceber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(vRPanel12Layout.createSequentialGroup()
                .addGroup(vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipoDocRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodAgente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEmprestimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmprestimoTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        tabs.addTab("Importar tabelas", vRPanel2);

        vRPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Ofertas"));

        chkOfertas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOfertasActionPerformed(evt);
            }
        });

        edtDtOfertas.setEnabled(false);
        edtDtOfertas.setMascara("Data");

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtOfertas, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtDtOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chkCorrigeBloqueado.setText("Corrigir Bloqueado (Preferencial)");
        chkCorrigeBloqueado.setToolTipText("Corrige o código dos produtos de balança, trocando o codigo de barras pelo id do produto.");
        chkCorrigeBloqueado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCorrigeBloqueadoActionPerformed(evt);
            }
        });

        chkCorrigeBalanca.setText("Corrigir produtos de balança");
        chkCorrigeBalanca.setToolTipText("Corrige o código dos produtos de balança, trocando o codigo de barras pelo id do produto.");
        chkCorrigeBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCorrigeBalancaActionPerformed(evt);
            }
        });

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Crédito Rotativo"));

        chkChequesComoRotativo.setText("Incluir cheques no rotativo");
        chkChequesComoRotativo.setToolTipText("Ao importar o crédito rotativo, puxa todos os registros em aberto como crédito rotativo, desconsiderando se é cheque.");

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkChequesComoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkChequesComoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        chkOfertaSemData.setText("Oferta sem data");
        chkOfertaSemData.setToolTipText("Corrige o código dos produtos de balança, trocando o codigo de barras pelo id do produto.");
        chkOfertaSemData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOfertaSemDataActionPerformed(evt);
            }
        });

        chkImportICMSEstadoLoja.setText("Importar ICMS para estado da Loja");

        chkCreditoRotativoManual.setText("Credito Rotativo Manual");

        txtCreditoRotarivoManual.setText("007");
        txtCreditoRotarivoManual.setCaixaAlta(false);

        txtCodAgente2.setToolTipText("Codigo do Agente, se houver");

        vRLabel13.setText("Agente");

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOfertaSemData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkCreditoRotativoManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCreditoRotarivoManual, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodAgente2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkCorrigeBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkImportICMSEstadoLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCorrigeBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(260, Short.MAX_VALUE))
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(chkCorrigeBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCorrigeBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkImportICMSEstadoLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkOfertaSemData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCreditoRotativoManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCreditoRotarivoManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodAgente2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        tabs.addTab("* Operações Especiais *", vRPanel5);

        chkCustoProdutoSysPDV.setText("Custo");
        chkCustoProdutoSysPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCustoProdutoSysPDVActionPerformed(evt);
            }
        });

        chkPrecoProdutoSysPDV.setText("Preço");
        chkPrecoProdutoSysPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrecoProdutoSysPDVActionPerformed(evt);
            }
        });

        chkEstoqueProdutoSysPDV.setText("Estoque");
        chkEstoqueProdutoSysPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEstoqueProdutoSysPDVActionPerformed(evt);
            }
        });

        chkClientePreferencialSysPDV.setText("Cliente Preferêncial");

        chkCreditoRotativoSysPDV.setText("Crédito Rotativo");

        chkProdutoIntegracao.setText("Produto");

        vRLabel8.setText("Path Log");

        vRLabel12.setText("Tipo Documento Crédito Rotativo");

        chkCodigoBarrasInt.setText("Codigo Barras");

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkProdutoIntegracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel8Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkCustoProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkPrecoProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkEstoqueProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkCodigoBarrasInt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(1, 1, 1)
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClientePreferencialSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel8Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(chkCreditoRotativoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbTipoDocCreditoRotativoInt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(vRLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPathLog, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(363, Short.MAX_VALUE))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkProdutoIntegracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCustoProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkClientePreferencialSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkCreditoRotativoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrecoProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoDocCreditoRotativoInt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEstoqueProdutoSysPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCodigoBarrasInt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPathLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        tabs.addTab("Integrações", vRPanel8);

        vRLabel10.setText("UF Sigla Padrao");

        vRLabel11.setText("Cidade Padrao");

        vRLabel9.setText("Cep Padrao");

        txtCidadePadrao.setCaixaAlta(false);
        txtCidadePadrao.setMascara("Numero");
        txtCidadePadrao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCidadePadraoActionPerformed(evt);
            }
        });

        vRLabel6.setText("Cidade IBGE Padrao");

        javax.swing.GroupLayout vRPanel9Layout = new javax.swing.GroupLayout(vRPanel9);
        vRPanel9.setLayout(vRPanel9Layout);
        vRPanel9Layout.setHorizontalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCidadePadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(vRLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCepPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCidadeDescricaoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(vRLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUFSiglaPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(367, Short.MAX_VALUE))
        );
        vRPanel9Layout.setVerticalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel9Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(txtCidadeDescricaoPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel9Layout.createSequentialGroup()
                            .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCepPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel9Layout.createSequentialGroup()
                            .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCidadePadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel9Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(txtUFSiglaPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(198, Short.MAX_VALUE))
        );

        tabs.addTab("Valores Padrão", vRPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tabs))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
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

    private void btnConectarFirebirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarFirebirdActionPerformed
        try {
            this.setWaitCursor();

            if (connFB != null) {
                connFB.close();
            }

            validarDadosAcessoFirebird();
            btnConectarFirebird.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            btnConectarFirebird.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png")));            

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarFirebirdActionPerformed

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

    private void txtSenhaFirebirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaFirebirdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaFirebirdActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void txtPortaFirebirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaFirebirdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaFirebirdActionPerformed

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

    private void chkFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaActionPerformed

    private void chkTipoEmbalagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTipoEmbalagemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkTipoEmbalagemActionPerformed

    private void chkCESTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCESTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCESTActionPerformed

    private void chkOfertasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOfertasActionPerformed
        edtDtOfertas.setEnabled(chkOfertas.isSelected());
        if (chkOfertas.isSelected()) {
            try {
                edtDtOfertas.setText(Util.getDataAtual());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            edtDtOfertas.setText("");
        }
    }//GEN-LAST:event_chkOfertasActionPerformed

    private void chkImportarSomenteAtivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImportarSomenteAtivosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkImportarSomenteAtivosActionPerformed

    private void chkCustoProdutoSysPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCustoProdutoSysPDVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCustoProdutoSysPDVActionPerformed

    private void chkPrecoProdutoSysPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrecoProdutoSysPDVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrecoProdutoSysPDVActionPerformed

    private void chkEstoqueProdutoSysPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEstoqueProdutoSysPDVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkEstoqueProdutoSysPDVActionPerformed

    private void txtCidadePadraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCidadePadraoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCidadePadraoActionPerformed

    private void chkProdutoAtacadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoAtacadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoAtacadoActionPerformed

    private void chkCorrigeBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCorrigeBalancaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCorrigeBalancaActionPerformed

    private void chkOfertaSemDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOfertaSemDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkOfertaSemDataActionPerformed

    private void chkCorrigeBloqueadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCorrigeBloqueadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCorrigeBloqueadoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarFirebird;
    private vrframework.bean.button.VRButton btnImportarBalanca;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCEST;
    private vrframework.bean.checkBox.VRCheckBox chkChequeReceber;
    private vrframework.bean.checkBox.VRCheckBox chkChequesComoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencialSysPDV;
    private vrframework.bean.checkBox.VRCheckBox chkCodigoBarrasInt;
    private vrframework.bean.checkBox.VRCheckBox chkCodigobarras;
    private vrframework.bean.checkBox.VRCheckBox chkCorrigeBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkCorrigeBloqueado;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativoManual;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativoSysPDV;
    private vrframework.bean.checkBox.VRCheckBox chkCustoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkCustoProdutoSysPDV;
    private vrframework.bean.checkBox.VRCheckBox chkEmprestimo;
    private vrframework.bean.checkBox.VRCheckBox chkEstoqueProduto;
    private vrframework.bean.checkBox.VRCheckBox chkEstoqueProdutoSysPDV;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkICMS;
    private vrframework.bean.checkBox.VRCheckBox chkImportICMSEstadoLoja;
    private vrframework.bean.checkBox.VRCheckBox chkImportarSomenteAtivos;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkOfertaSemData;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoProduto;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoProdutoSysPDV;
    private vrframework.bean.checkBox.VRCheckBox chkProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoIntegracao;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoSemCodigoBarras;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagem;
    private javax.swing.JComboBox cmbLojaDestino;
    private vrframework.bean.comboBox.VRComboBox cmbTipoDocCheque;
    private vrframework.bean.comboBox.VRComboBox cmbTipoDocCreditoRotativoInt;
    private vrframework.bean.comboBox.VRComboBox cmbTipoDocRotativo;
    private vrframework.bean.textField.VRTextField edtDtOfertas;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.radioButton.VRRadioButton rdbCadTxt;
    private vrframework.bean.radioButton.VRRadioButton rdbItensMgv;
    private vrframework.bean.radioButton.VRRadioButton rdbTxtItens;
    private javax.swing.JTabbedPane tabs;
    private vrframework.bean.fileChooser.VRFileChooser txtArquivoBalanca;
    private vrframework.bean.fileChooser.VRFileChooser txtBancoDadosFirebird;
    private vrframework.bean.textField.VRTextField txtCepPadrao;
    private vrframework.bean.textField.VRTextField txtCidadeDescricaoPadrao;
    private vrframework.bean.textField.VRTextField txtCidadePadrao;
    private javax.swing.JTextField txtCodAgente;
    private javax.swing.JTextField txtCodAgente2;
    private vrframework.bean.textField.VRTextField txtCreditoRotarivoManual;
    private javax.swing.JTextField txtEmprestimoTipoDoc;
    private vrframework.bean.textField.VRTextField txtHostFirebird;
    private vrframework.bean.textField.VRTextField txtLojaCliente;
    private vrframework.bean.textField.VRTextField txtPathLog;
    private vrframework.bean.textField.VRTextField txtPortaFirebird;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaFirebird;
    private vrframework.bean.textField.VRTextField txtUFSiglaPadrao;
    private vrframework.bean.textField.VRTextField txtUsuarioFirebird;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel10;
    private vrframework.bean.label.VRLabel vRLabel11;
    private vrframework.bean.label.VRLabel vRLabel12;
    private vrframework.bean.label.VRLabel vRLabel13;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.label.VRLabel vRLabel9;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel10;
    private vrframework.bean.panel.VRPanel vRPanel11;
    private vrframework.bean.panel.VRPanel vRPanel12;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
