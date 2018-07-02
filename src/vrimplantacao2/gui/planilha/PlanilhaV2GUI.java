package vrimplantacao2.gui.planilha;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao2.dao.cadastro.MercadologicoDAO;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualDAO;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.ProdutoDAO;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.PlanilhaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoView;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.window.WindowUtils;

/**
 *
 * @author Leandro
 */
public class PlanilhaV2GUI extends VRInternalFrame {
    
    private final Parametros parametros = Parametros.get();
    private final PlanilhaDAO dao = new PlanilhaDAO();
        
    private PlanilhaV2GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
                
        initComponents();
        
        inicializar();

        centralizarForm();
        this.setMaximum(false);
        tabProdImportacao.setOpcoesDisponiveis(dao);
    }
    
    private static PlanilhaV2GUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new PlanilhaV2GUI(i_mdiFrame);
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

        checksProdutoPanelGUI1 = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabModel = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrframework.bean.panel.VRPanel();
        txtDelimitadorProd = new javax.swing.JTextField();
        txtProdutoFile = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        tabsProduto = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdImportacao = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabProdEspeciais = new vrframework.bean.panel.VRPanel();
        btnDelMercadologico = new vrframework.bean.button.VRButton();
        btnDelFamiliaProduto = new vrframework.bean.button.VRButton();
        btnDelProdutos = new vrframework.bean.button.VRButton();
        tabProdUnifacao = new vrframework.bean.panel.VRPanel();
        chkProdUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        tabMapaTributacao = new vrframework.bean.panel.VRPanel();
        btnMapaTribut = new vrframework.bean.button.VRButton();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        txtDelimitadorTribut = new javax.swing.JTextField();
        txtTributoFile = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel10 = new vrframework.bean.label.VRLabel();
        tabArquivoBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtFornecedorFile = new vrframework.bean.fileChooser.VRFileChooser();
        tabsForn = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabFornImportacao = new vrframework.bean.panel.VRPanel();
        chkFornDados = new vrframework.bean.checkBox.VRCheckBox();
        chkFornContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFornProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabFornUnificacao = new vrframework.bean.panel.VRPanel();
        chkFornUnificar = new vrframework.bean.checkBox.VRCheckBox();
        chkFornUnificarProd = new vrframework.bean.checkBox.VRCheckBox();
        tabFornEspecial = new javax.swing.JPanel();
        btnFornContatos = new vrframework.bean.button.VRButton();
        btnFornDados = new vrframework.bean.button.VRButton();
        txtDelimitadorForn = new javax.swing.JTextField();
        tabVenda = new vrframework.bean.panel.VRPanel();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtVendaDelimitador = new javax.swing.JTextField();
        txtVendaStrQuote = new javax.swing.JTextField();
        txtVendaHistFile = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel11 = new vrframework.bean.label.VRLabel();
        txtVendaItemFile = new vrframework.bean.fileChooser.VRFileChooser();
        chkHistVenda = new vrframework.bean.checkBox.VRCheckBox();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel12 = new vrframework.bean.label.VRLabel();
        vRLabel13 = new vrframework.bean.label.VRLabel();
        tabClientes = new vrframework.bean.panel.VRPanel();
        txtClienteDelimitador = new javax.swing.JTextField();
        vRLabel15 = new vrframework.bean.label.VRLabel();
        txtClienteStrQuote = new javax.swing.JTextField();
        vRLabel14 = new vrframework.bean.label.VRLabel();
        txtClienteFile = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        tabsCliente = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlClienteNormal = new vrframework.bean.panel.VRPanel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkCliPrefDados = new vrframework.bean.checkBox.VRCheckBox();
        chkCliPrefContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkCliPrefRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCliPrefRotativoBaixas = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        chkCliEvenDados = new vrframework.bean.checkBox.VRCheckBox();
        chkCliEvenContatos = new vrframework.bean.checkBox.VRCheckBox();
        pnlClienteEspeciais = new vrframework.bean.panel.VRPanel();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        btnDelPrefContatos = new vrframework.bean.button.VRButton();
        btnDelPrefDados = new vrframework.bean.button.VRButton();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        btnDelEvtContatos = new vrframework.bean.button.VRButton();
        btnDelEvtDados = new vrframework.bean.button.VRButton();
        pnlUnificacao = new vrframework.bean.panel.VRPanel();
        chkClientePrefUnificar = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEvtUnificar = new vrframework.bean.checkBox.VRCheckBox();
        jLabel2 = new javax.swing.JLabel();
        cmbLojaDestino = new javax.swing.JComboBox();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSistema = new javax.swing.JTextField();
        txtCodLojaOrigem = new javax.swing.JTextField();

        checksProdutoPanelGUI1.setMaximumSize(new java.awt.Dimension(3728, 38));
        checksProdutoPanelGUI1.setPreferredSize(new java.awt.Dimension(900, 38));

        setResizable(true);
        setTitle("Importação de Planilha");
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

        tabProdutos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtDelimitadorProd.setText("^");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Informe o arquivo dos produtos");

        tabsProduto.setPreferredSize(new java.awt.Dimension(900, 202));
        tabsProduto.addTab("Importação", tabProdImportacao);

        org.openide.awt.Mnemonics.setLocalizedText(btnDelMercadologico, "Eliminar mercadológico");
        btnDelMercadologico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelMercadologicoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnDelFamiliaProduto, "Eliminar família de produto");
        btnDelFamiliaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelFamiliaProdutoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnDelProdutos, "Eliminar os produtos");
        btnDelProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelProdutosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabProdEspeciaisLayout = new javax.swing.GroupLayout(tabProdEspeciais);
        tabProdEspeciais.setLayout(tabProdEspeciaisLayout);
        tabProdEspeciaisLayout.setHorizontalGroup(
            tabProdEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabProdEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabProdEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnDelFamiliaProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDelMercadologico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnDelProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabProdEspeciaisLayout.setVerticalGroup(
            tabProdEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDelMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Especiais", tabProdEspeciais);

        org.openide.awt.Mnemonics.setLocalizedText(chkProdUnifProdutos, "Produtos (Somente com EAN)");

        javax.swing.GroupLayout tabProdUnifacaoLayout = new javax.swing.GroupLayout(tabProdUnifacao);
        tabProdUnifacao.setLayout(tabProdUnifacaoLayout);
        tabProdUnifacaoLayout.setHorizontalGroup(
            tabProdUnifacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdUnifacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkProdUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabProdUnifacaoLayout.setVerticalGroup(
            tabProdUnifacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdUnifacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkProdUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Unificação", tabProdUnifacao);

        org.openide.awt.Mnemonics.setLocalizedText(btnMapaTribut, "Mapa de Tribut.");
        btnMapaTribut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTributActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel9, "Del");

        txtDelimitadorTribut.setText(";");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel10, "Informe o arquivo dos produtos");

        javax.swing.GroupLayout tabMapaTributacaoLayout = new javax.swing.GroupLayout(tabMapaTributacao);
        tabMapaTributacao.setLayout(tabMapaTributacaoLayout);
        tabMapaTributacaoLayout.setHorizontalGroup(
            tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabMapaTributacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabMapaTributacaoLayout.createSequentialGroup()
                        .addComponent(btnMapaTribut, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabMapaTributacaoLayout.createSequentialGroup()
                        .addGroup(tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDelimitadorTribut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTributoFile, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                            .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        tabMapaTributacaoLayout.setVerticalGroup(
            tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabMapaTributacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabMapaTributacaoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(txtTributoFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabMapaTributacaoLayout.createSequentialGroup()
                        .addGroup(tabMapaTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDelimitadorTribut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMapaTribut, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(193, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Mapa de tributações", tabMapaTributacao);
        tabsProduto.addTab("Arquivo de Balança", tabArquivoBalanca);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel8, "Del");

        javax.swing.GroupLayout tabProdutosLayout = new javax.swing.GroupLayout(tabProdutos);
        tabProdutos.setLayout(tabProdutosLayout);
        tabProdutosLayout.setHorizontalGroup(
            tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdutosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabProdutosLayout.createSequentialGroup()
                        .addGroup(tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDelimitadorProd))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabProdutosLayout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(tabProdutosLayout.createSequentialGroup()
                                .addComponent(txtProdutoFile, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                .addContainerGap())))
                    .addGroup(tabProdutosLayout.createSequentialGroup()
                        .addComponent(tabsProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        tabProdutosLayout.setVerticalGroup(
            tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdutosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabProdutosLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(txtProdutoFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabProdutosLayout.createSequentialGroup()
                        .addGroup(tabProdutosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDelimitadorProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabModel.addTab("Produtos", tabProdutos);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Informe o arquivo dos fornecedores");

        org.openide.awt.Mnemonics.setLocalizedText(chkFornDados, "Dados do fornecedor");

        org.openide.awt.Mnemonics.setLocalizedText(chkFornContatos, "Contatos");

        org.openide.awt.Mnemonics.setLocalizedText(chkFornProdutoFornecedor, "Produtos Fornecedores");

        javax.swing.GroupLayout tabFornImportacaoLayout = new javax.swing.GroupLayout(tabFornImportacao);
        tabFornImportacao.setLayout(tabFornImportacaoLayout);
        tabFornImportacaoLayout.setHorizontalGroup(
            tabFornImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornImportacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFornDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(473, Short.MAX_VALUE))
        );
        tabFornImportacaoLayout.setVerticalGroup(
            tabFornImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornImportacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        tabsForn.addTab("Importação", tabFornImportacao);

        org.openide.awt.Mnemonics.setLocalizedText(chkFornUnificar, "Unificar Fornecedores (Somente com CPF/CNPJ válido)");

        org.openide.awt.Mnemonics.setLocalizedText(chkFornUnificarProd, "Unificar ProdutoFornecedor (Somente com CPF/CNPJ válido)");

        javax.swing.GroupLayout tabFornUnificacaoLayout = new javax.swing.GroupLayout(tabFornUnificacao);
        tabFornUnificacao.setLayout(tabFornUnificacaoLayout);
        tabFornUnificacaoLayout.setHorizontalGroup(
            tabFornUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFornUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornUnificarProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(295, Short.MAX_VALUE))
        );
        tabFornUnificacaoLayout.setVerticalGroup(
            tabFornUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornUnificarProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
        );

        tabsForn.addTab("Unificação", tabFornUnificacao);

        org.openide.awt.Mnemonics.setLocalizedText(btnFornContatos, "Eliminar contatos");
        btnFornContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFornContatosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnFornDados, "Eliminar dados");
        btnFornDados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFornDadosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabFornEspecialLayout = new javax.swing.GroupLayout(tabFornEspecial);
        tabFornEspecial.setLayout(tabFornEspecialLayout);
        tabFornEspecialLayout.setHorizontalGroup(
            tabFornEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornEspecialLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFornContatos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFornDados, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(485, Short.MAX_VALUE))
        );
        tabFornEspecialLayout.setVerticalGroup(
            tabFornEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornEspecialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFornContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFornDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        tabsForn.addTab("Especial", tabFornEspecial);

        txtDelimitadorForn.setText("^");

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabFornecedorLayout.createSequentialGroup()
                        .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tabsForn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(tabFornecedorLayout.createSequentialGroup()
                                .addComponent(txtDelimitadorForn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFornecedorFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFornecedorFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDelimitadorForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addComponent(tabsForn, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabModel.addTab("Fornecedores", tabFornecedor);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel5, "Arquivo da venda");

        txtVendaDelimitador.setText("^");

        txtVendaStrQuote.setText("\"");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel11, "Arquivo com os itens da venda (pdv.vendaitem)");

        org.openide.awt.Mnemonics.setLocalizedText(chkHistVenda, "Histórico de Vendas");

        org.openide.awt.Mnemonics.setLocalizedText(chkPdvVendas, "PDV Vendas");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel12, "Delimitador");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel13, "Str. Quote");

        javax.swing.GroupLayout tabVendaLayout = new javax.swing.GroupLayout(tabVenda);
        tabVenda.setLayout(tabVendaLayout);
        tabVendaLayout.setHorizontalGroup(
            tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabVendaLayout.createSequentialGroup()
                        .addGroup(tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkHistVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabVendaLayout.createSequentialGroup()
                                .addComponent(vRLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVendaDelimitador, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVendaStrQuote, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtVendaItemFile, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addComponent(txtVendaHistFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabVendaLayout.setVerticalGroup(
            tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVendaDelimitador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVendaStrQuote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVendaHistFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVendaItemFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkHistVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(200, Short.MAX_VALUE))
        );

        tabModel.addTab("Vendas", tabVenda);

        txtClienteDelimitador.setText("^");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel15, "Delimitador");

        txtClienteStrQuote.setText("\"");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel14, "Str. Quote");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel6, "Arquivo dos clientes");

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente Preferencial"));

        org.openide.awt.Mnemonics.setLocalizedText(chkCliPrefDados, "Dados");

        org.openide.awt.Mnemonics.setLocalizedText(chkCliPrefContatos, "Contatos");

        org.openide.awt.Mnemonics.setLocalizedText(chkCliPrefRotativo, "Crédito Rotativo");

        org.openide.awt.Mnemonics.setLocalizedText(chkCliPrefRotativoBaixas, "Crédito Rotativo (Baixas)");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCliPrefDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCliPrefContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCliPrefRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCliPrefRotativoBaixas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(202, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkCliPrefDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkCliPrefContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkCliPrefRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkCliPrefRotativoBaixas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        vRPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente Eventual"));

        org.openide.awt.Mnemonics.setLocalizedText(chkCliEvenDados, "Dados");

        org.openide.awt.Mnemonics.setLocalizedText(chkCliEvenContatos, "Contatos");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCliEvenDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCliEvenContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkCliEvenDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkCliEvenContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout pnlClienteNormalLayout = new javax.swing.GroupLayout(pnlClienteNormal);
        pnlClienteNormal.setLayout(pnlClienteNormalLayout);
        pnlClienteNormalLayout.setHorizontalGroup(
            pnlClienteNormalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteNormalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClienteNormalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlClienteNormalLayout.setVerticalGroup(
            pnlClienteNormalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteNormalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        tabsCliente.addTab("Importação", pnlClienteNormal);

        vRPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente Preferencial"));

        org.openide.awt.Mnemonics.setLocalizedText(btnDelPrefContatos, "Eliminar contatos");
        btnDelPrefContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelPrefContatosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnDelPrefDados, "Eliminar dados");
        btnDelPrefDados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelPrefDadosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDelPrefContatos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelPrefDados, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(318, Short.MAX_VALUE))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelPrefContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelPrefDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente Eventual"));

        org.openide.awt.Mnemonics.setLocalizedText(btnDelEvtContatos, "Eliminar contatos");
        btnDelEvtContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelEvtContatosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnDelEvtDados, "Eliminar dados");
        btnDelEvtDados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelEvtDadosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDelEvtContatos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelEvtDados, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(318, Short.MAX_VALUE))
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelEvtContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelEvtDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlClienteEspeciaisLayout = new javax.swing.GroupLayout(pnlClienteEspeciais);
        pnlClienteEspeciais.setLayout(pnlClienteEspeciaisLayout);
        pnlClienteEspeciaisLayout.setHorizontalGroup(
            pnlClienteEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClienteEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlClienteEspeciaisLayout.setVerticalGroup(
            pnlClienteEspeciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteEspeciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        tabsCliente.addTab("Especiais", pnlClienteEspeciais);

        org.openide.awt.Mnemonics.setLocalizedText(chkClientePrefUnificar, "Unificar clientes preferenciais (Somente com CPF/CNPJ válido)");

        org.openide.awt.Mnemonics.setLocalizedText(chkClienteEvtUnificar, "Unificar clientes eventuais (Somente com CPF/CNPJ válido)");

        javax.swing.GroupLayout pnlUnificacaoLayout = new javax.swing.GroupLayout(pnlUnificacao);
        pnlUnificacao.setLayout(pnlUnificacaoLayout);
        pnlUnificacaoLayout.setHorizontalGroup(
            pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkClientePrefUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteEvtUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(287, Short.MAX_VALUE))
        );
        pnlUnificacaoLayout.setVerticalGroup(
            pnlUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClientePrefUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEvtUnificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        tabsCliente.addTab("Unificação", pnlUnificacao);

        javax.swing.GroupLayout tabClientesLayout = new javax.swing.GroupLayout(tabClientes);
        tabClientes.setLayout(tabClientesLayout);
        tabClientesLayout.setHorizontalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabsCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(vRLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteDelimitador, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteStrQuote, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabClientesLayout.setVerticalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClienteDelimitador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClienteStrQuote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClienteFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabsCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabModel.addTab("Clientes", tabClientes);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Loja Destino:");

        cmbLojaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaDestinoActionPerformed(evt);
            }
        });

        btnMigrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnMigrar, "Migrar");
        btnMigrar.setFocusable(false);
        btnMigrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrarActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Loja Origem");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel4, "Sistema");

        txtSistema.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onSetLojaEvent(evt);
            }
        });

        txtCodLojaOrigem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onSetLojaEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbLojaDestino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(tabModel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabModel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void cmbLojaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaDestinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLojaDestinoActionPerformed

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            
            migrar();
            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void btnDelMercadologicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelMercadologicoActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS os mercadológicos?")) {
            try {
                new MercadologicoDAO().apagarMercadologico();
                Util.exibirMensagem("Mercadológicos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelMercadologicoActionPerformed

    private void btnDelFamiliaProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelFamiliaProdutoActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODAS as famílias dos produtos?")) {
            try {
                new FamiliaProdutoDAO().apagarFamilia();
                Util.exibirMensagem("Famílias excluídas com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelFamiliaProdutoActionPerformed

    private void btnDelProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelProdutosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS os produtos?")) {
            try {
                new ProdutoDAO().apagarProdutos();
                Util.exibirMensagem("Produtos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelProdutosActionPerformed

    private void btnDelPrefContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelPrefContatosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS CONTATOS do cliente preferêncial?")) {
            try {
                new ClientePreferencialDAO().apagarContatos();
                Util.exibirMensagem("Contatos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelPrefContatosActionPerformed

    private void btnDelPrefDadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelPrefDadosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS os DADOS do cliente preferêncial?")) {
            try {
                new ClientePreferencialDAO().apagarTudo();
                Util.exibirMensagem("Clientes excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelPrefDadosActionPerformed

    private void btnDelEvtContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelEvtContatosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS CONTATOS do cliente eventual?")) {
            try {
                new ClienteEventualDAO().apagarContatos();
                Util.exibirMensagem("Contatos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelEvtContatosActionPerformed

    private void btnDelEvtDadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelEvtDadosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS os DADOS do cliente eventual?")) {
            try {
                new ClienteEventualDAO().apagarTudo();
                Util.exibirMensagem("Contatos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDelEvtDadosActionPerformed

    private void btnFornContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFornContatosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS CONTATOS do fornecedor?")) {
            try {
                new FornecedorDAO().apagarContatos();
                Util.exibirMensagem("Contatos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnFornContatosActionPerformed

    private void btnFornDadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFornDadosActionPerformed
        if (WindowUtils.confirmar("Deseja realmente EXCLUIR TODOS os DADOS do fornecedor?")) {
            try {
                new FornecedorDAO().apagarTudo();
                Util.exibirMensagem("Contatos excluídos com sucesso!", title);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnFornDadosActionPerformed

    private void btnMapaTributActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTributActionPerformed
        try {
            dao.getOpcoes().put("delimiter", txtDelimitadorTribut.getText());                                
            dao.setArquivo(txtTributoFile.getArquivo());
            MapaTributacaoView.exibir(
                mdiFrame,
                txtSistema.getText(),
                txtCodLojaOrigem.getText(),
                dao);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        }
    }//GEN-LAST:event_btnMapaTributActionPerformed

    private void onSetLojaEvent(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onSetLojaEvent
        tabArquivoBalanca.setSistema(txtSistema.getText());
        tabArquivoBalanca.setLoja(txtCodLojaOrigem.getText());
    }//GEN-LAST:event_onSetLojaEvent

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnDelEvtContatos;
    private vrframework.bean.button.VRButton btnDelEvtDados;
    private vrframework.bean.button.VRButton btnDelFamiliaProduto;
    private vrframework.bean.button.VRButton btnDelMercadologico;
    private vrframework.bean.button.VRButton btnDelPrefContatos;
    private vrframework.bean.button.VRButton btnDelPrefDados;
    private vrframework.bean.button.VRButton btnDelProdutos;
    private vrframework.bean.button.VRButton btnFornContatos;
    private vrframework.bean.button.VRButton btnFornDados;
    private vrframework.bean.button.VRButton btnMapaTribut;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI checksProdutoPanelGUI1;
    private vrframework.bean.checkBox.VRCheckBox chkCliEvenContatos;
    private vrframework.bean.checkBox.VRCheckBox chkCliEvenDados;
    private vrframework.bean.checkBox.VRCheckBox chkCliPrefContatos;
    private vrframework.bean.checkBox.VRCheckBox chkCliPrefDados;
    private vrframework.bean.checkBox.VRCheckBox chkCliPrefRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCliPrefRotativoBaixas;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEvtUnificar;
    private vrframework.bean.checkBox.VRCheckBox chkClientePrefUnificar;
    private vrframework.bean.checkBox.VRCheckBox chkFornContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFornDados;
    private vrframework.bean.checkBox.VRCheckBox chkFornProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornUnificar;
    private vrframework.bean.checkBox.VRCheckBox chkFornUnificarProd;
    private vrframework.bean.checkBox.VRCheckBox chkHistVenda;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkProdUnifProdutos;
    private javax.swing.JComboBox cmbLojaDestino;
    private javax.swing.JLabel jLabel2;
    private vrframework.bean.panel.VRPanel pnlClienteEspeciais;
    private vrframework.bean.panel.VRPanel pnlClienteNormal;
    private vrframework.bean.panel.VRPanel pnlUnificacao;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel tabArquivoBalanca;
    private vrframework.bean.panel.VRPanel tabClientes;
    private javax.swing.JPanel tabFornEspecial;
    private vrframework.bean.panel.VRPanel tabFornImportacao;
    private vrframework.bean.panel.VRPanel tabFornUnificacao;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrframework.bean.panel.VRPanel tabMapaTributacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabModel;
    private vrframework.bean.panel.VRPanel tabProdEspeciais;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdImportacao;
    private vrframework.bean.panel.VRPanel tabProdUnifacao;
    private vrframework.bean.panel.VRPanel tabProdutos;
    private vrframework.bean.panel.VRPanel tabVenda;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsCliente;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsForn;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsProduto;
    private javax.swing.JTextField txtClienteDelimitador;
    private vrframework.bean.fileChooser.VRFileChooser txtClienteFile;
    private javax.swing.JTextField txtClienteStrQuote;
    private javax.swing.JTextField txtCodLojaOrigem;
    private javax.swing.JTextField txtDelimitadorForn;
    private javax.swing.JTextField txtDelimitadorProd;
    private javax.swing.JTextField txtDelimitadorTribut;
    private vrframework.bean.fileChooser.VRFileChooser txtFornecedorFile;
    private vrframework.bean.fileChooser.VRFileChooser txtProdutoFile;
    private javax.swing.JTextField txtSistema;
    private vrframework.bean.fileChooser.VRFileChooser txtTributoFile;
    private javax.swing.JTextField txtVendaDelimitador;
    private vrframework.bean.fileChooser.VRFileChooser txtVendaHistFile;
    private vrframework.bean.fileChooser.VRFileChooser txtVendaItemFile;
    private javax.swing.JTextField txtVendaStrQuote;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel10;
    private vrframework.bean.label.VRLabel vRLabel11;
    private vrframework.bean.label.VRLabel vRLabel12;
    private vrframework.bean.label.VRLabel vRLabel13;
    private vrframework.bean.label.VRLabel vRLabel14;
    private vrframework.bean.label.VRLabel vRLabel15;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.label.VRLabel vRLabel9;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    // End of variables declaration//GEN-END:variables

    private void migrar() throws Exception {
        if (validado()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Importador importador = new Importador(dao);
                        dao.setSistema(txtSistema.getText());
                        dao.setLojaOrigem(txtCodLojaOrigem.getText());
                        importador.setLojaVR(((ItemComboVO) cmbLojaDestino.getSelectedItem()).id);
                        tabProdImportacao.setImportador(importador);

                        ProgressBar.show();
                        ProgressBar.setCancel(true);
                        
                        switch (tabModel.getSelectedIndex()) {
                            case 0: {
                                dao.getOpcoes().put("delimiter", txtDelimitadorProd.getText());                                
                                dao.setArquivo(txtProdutoFile.getArquivo());
                                if (tabsProduto.getSelectedIndex() == 0) {
                                    tabProdImportacao.executarImportacao();
                                } else if (tabsProduto.getSelectedIndex() == 2) {
                                    if (chkProdUnifProdutos.isSelected()) {
                                        importador.unificarProdutos();
                                    }
                                }
                                
                            }break;
                            case 1: {
                                dao.getOpcoes().put("delimiter", txtDelimitadorForn.getText()); 
                                dao.setArquivo(txtFornecedorFile.getArquivo());
                                if (tabsForn.getSelectedIndex() == 0) {
                                    List<OpcaoFornecedor> opcoes = new ArrayList<>();
                                    if (chkFornDados.isSelected()) {
                                        opcoes.add(OpcaoFornecedor.DADOS);
                                    }
                                    if (chkFornContatos.isSelected()) {
                                        opcoes.add(OpcaoFornecedor.CONTATOS);
                                    }
                                    if (!opcoes.isEmpty()) {
                                        importador.importarFornecedor();                                
                                    }
                                    if (chkFornProdutoFornecedor.isSelected()) {
                                        importador.importarProdutoFornecedor();
                                    }
                                } else if (tabsForn.getSelectedIndex() == 1) {
                                    if (chkFornUnificar.isSelected()) {
                                        importador.unificarFornecedor();
                                    }
                                    if (chkFornUnificarProd.isSelected()) {
                                        importador.unificarProdutoFornecedor();
                                    }
                                }
                            }; break;
                            case 2: {
                                dao.setArquivoVendas(txtVendaHistFile.getArquivo());
                                dao.setArquivoVendasItens(txtVendaItemFile.getArquivo());
                                dao.getOpcoes().put("delimiter", txtVendaDelimitador.getText());
                                dao.getOpcoes().put("quote", txtVendaStrQuote.getText());
                                if (chkHistVenda.isSelected()) {
                                    importador.importarHistoricoVendas();
                                }
                                if (chkPdvVendas.isSelected()) {
                                    importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                                }
                            }break;
                            case 3: {
                                dao.setArquivo(txtClienteFile.getArquivo());
                                dao.getOpcoes().put("delimiter", txtClienteDelimitador.getText());
                                dao.getOpcoes().put("quote", txtClienteStrQuote.getText());
                                if (tabsCliente.getSelectedIndex() == 0) {
                                    if (chkCliPrefDados.isSelected()) {
                                        List<OpcaoCliente> opcoes = new ArrayList<>();
                                        opcoes.add(OpcaoCliente.DADOS);
                                        if (chkCliPrefContatos.isSelected()) {
                                            opcoes.add(OpcaoCliente.CONTATOS);
                                        }
                                        importador.importarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                                    }
                                    if (chkCliPrefRotativo.isSelected()) {
                                        importador.importarCreditoRotativo();
                                    }
                                    if (chkCliPrefRotativoBaixas.isSelected()) {
                                        importador.importarCreditoRotativoBaixasAgrupadas();
                                    }
                                    if (chkCliEvenDados.isSelected()) {
                                        List<OpcaoCliente> opcoes = new ArrayList<>();
                                        opcoes.add(OpcaoCliente.DADOS);
                                        if (chkCliEvenContatos.isSelected()) {
                                            opcoes.add(OpcaoCliente.CONTATOS);
                                        }
                                        importador.importarClienteEventual(opcoes.toArray(new OpcaoCliente[]{}));
                                    }
                                } else if (tabsCliente.getSelectedIndex() == 2) {
                                    if (chkClientePrefUnificar.isSelected()) {
                                        importador.unificarClientePreferencial();
                                    }
                                    if (chkClienteEvtUnificar.isSelected()) {
                                        importador.unificarClienteEventual();
                                    }
                                }
                            }; break;
                        }

                        gravarParametros();

                        ProgressBar.dispose();
                        Util.exibirMensagem("Importação efetuada com sucesso!", "Informativo");
                    } catch (Exception ex) {
                        ProgressBar.dispose();
                        Util.exibirMensagemErro(ex, getTitle());
                    } finally {
                        tabProdImportacao.setImportador(null);
                    }                 
                }
            };
            
            thread.start();
           
        }        
    }

    private boolean validado() throws Exception {
        StringBuilder error = new StringBuilder("");
        
        if (cmbLojaDestino.getSelectedIndex() == -1) {
            error.append("* Loja de destino não selecionada!\n");
        }
        
        if ("".equals(txtCodLojaOrigem.getText().trim())) {
            error.append("* Loja de origem não informada!\n");
        }
        
        if (!"".equals(error.toString())) {
            Util.exibirMensagem(error.toString(), "Verifique antes de continuar!", 200, JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private void inicializar() throws Exception {
        carregarParametros();
        
        cmbLojaDestino.removeAllItems();
        cmbLojaDestino.setModel(new DefaultComboBoxModel());
        for (LojaVO loja: new LojaDAO().carregar()) {
            cmbLojaDestino.addItem(new ItemComboVO(loja.getId(), loja.getDescricao()));
        }
    }

    private void gravarParametros() throws Exception {
        tabProdImportacao.gravarParametros(parametros, "IMPORTACAO", "PLANILHA");
        parametros.put(txtProdutoFile.getArquivo(), "IMPORTACAO", "PLANILHA", "ARQUIVO_PRODUTO");
        parametros.put(txtSistema.getText(), "IMPORTACAO", "PLANILHA", "SISTEMA");
        parametros.put(txtCodLojaOrigem.getText(), "IMPORTACAO", "PLANILHA", "LOJA_ORIGEM");
        parametros.put(((ItemComboVO) cmbLojaDestino.getSelectedItem()).id, "IMPORTACAO", "PLANILHA", "LOJA_VR");
        parametros.put(txtFornecedorFile.getArquivo(), "IMPORTACAO", "PLANILHA", "FORNECEDOR", "ARQUIVO");
        parametros.put(txtClienteDelimitador.getText(), "IMPORTACAO", "PLANILHA", "CLIENTE", "DELIMITER");
        parametros.put(txtClienteStrQuote.getText(), "IMPORTACAO", "PLANILHA", "CLIENTE", "QUOTE");
        parametros.put(txtClienteFile.getArquivo(), "IMPORTACAO", "PLANILHA", "CLIENTE", "ARQUIVO");
        parametros.put(txtDelimitadorTribut.getText(), "IMPORTACAO", "PLANILHA", "ICMS", "DELIMITER");
        parametros.put(txtTributoFile.getArquivo(), "IMPORTACAO", "PLANILHA", "ICMS", "ARQUIVO");
        parametros.put(txtVendaHistFile.getArquivo(), "IMPORTACAO", "PLANILHA", "VENDA", "ARQUIVO");
        parametros.put(txtVendaItemFile.getArquivo(), "IMPORTACAO", "PLANILHA", "VENDA-ITEM", "ARQUIVO");        
        parametros.put(txtVendaDelimitador.getText(), "IMPORTACAO", "PLANILHA", "VENDA", "DELIMITER");
        parametros.put(txtVendaStrQuote.getText(), "IMPORTACAO", "PLANILHA", "VENDA", "QUOTE");
        parametros.salvar();
    }
    
    private void carregarParametros() throws Exception {
        String arquivo = parametros.get("IMPORTACAO", "PLANILHA", "ARQUIVO_PRODUTO");
        String origem = parametros.getWithNull("1", "IMPORTACAO", "PLANILHA", "LOJA_ORIGEM");
        String sistema = parametros.getWithNull("PLANILHA", "IMPORTACAO", "PLANILHA", "SISTEMA");
        int idLojaVR = parametros.getInt("IMPORTACAO", "PLANILHA", "LOJA_VR");
        txtProdutoFile.setArquivo(arquivo != null ? arquivo : "");
        txtSistema.setText(sistema);
        txtCodLojaOrigem.setText(origem);
        onSetLojaEvent(null);
        if (idLojaVR > 0) {
            int index = -1;
            for (int i = 0; i < cmbLojaDestino.getItemCount(); i++) {
                ItemComboVO item = (ItemComboVO) cmbLojaDestino.getSelectedItem();
                if (item.id == idLojaVR) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                cmbLojaDestino.setSelectedIndex(index);
            }
        }
        
        tabProdImportacao.carregarParametros(parametros, "IMPORTACAO", "PLANILHA");
        
        String arquivoForn = parametros.get("IMPORTACAO", "PLANILHA", "FORNECEDOR", "ARQUIVO");
        txtFornecedorFile.setArquivo(arquivoForn != null ? arquivoForn : "");
        String arquivoCli = parametros.get("IMPORTACAO", "PLANILHA", "CLIENTE", "ARQUIVO");
        txtClienteFile.setArquivo(arquivoCli != null ? arquivoCli : "");
        String delCli = parametros.get("IMPORTACAO", "PLANILHA", "CLIENTE", "DELIMITER");
        txtClienteDelimitador.setText(delCli != null ? delCli : "");
        String quoteCli = parametros.get("IMPORTACAO", "PLANILHA", "CLIENTE", "QUOTE");
        txtClienteStrQuote.setText(quoteCli != null ? quoteCli : "");
        String arquivoTrib = parametros.get("IMPORTACAO", "PLANILHA", "ICMS", "ARQUIVO");
        txtTributoFile.setArquivo(arquivoTrib != null ? arquivoTrib : "");
        String delTrib = parametros.get("IMPORTACAO", "PLANILHA", "ICMS", "DELIMITER");
        txtDelimitadorTribut.setText(delTrib != null ? delTrib : "");
        
        
        
        String vend = parametros.get("IMPORTACAO", "PLANILHA", "VENDA", "ARQUIVO");
        txtVendaHistFile.setArquivo(vend != null ? vend : "");
        String vendItem = parametros.get("IMPORTACAO", "PLANILHA", "VENDA-ITEM", "ARQUIVO");
        txtVendaItemFile.setArquivo(vendItem != null ? vendItem : "");        
        String delVend = parametros.get("IMPORTACAO", "PLANILHA", "VENDA", "DELIMITER");
        txtVendaDelimitador.setText(delVend != null ? delVend : "");
        String quoteVend = parametros.get("IMPORTACAO", "PLANILHA", "VENDA", "QUOTE");
        txtVendaStrQuote.setText(quoteVend != null ? quoteVend : "");
        
    }
    
}
