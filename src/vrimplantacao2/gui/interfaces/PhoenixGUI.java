package vrimplantacao2.gui.interfaces;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.PhoenixDAO;
import vrimplantacao2.parametro.Parametros;

public class PhoenixGUI extends VRInternalFrame {

    private static final String SISTEMA = "Phoenix";
    private static PhoenixGUI instance;

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        txtProdutoFile.setArquivo(params.get(SISTEMA, "PLANILHA_PRODUTO"));
        txtFornecedorFile.setArquivo(params.get(SISTEMA, "PLANILHA_FORNECEDOR"));
        txtFornecedorMercFile.setArquivo(params.get(SISTEMA, "PLANILHA_PRODUTO_FORNECEDOR"));
        txtClienteFile.setArquivo(params.get(SISTEMA, "PLANILHA_CLIENTE"));
        txtCodLojaOrigem.setText(params.get(SISTEMA, "LOJA_CLIENTE"));
        txtCRFile.setArquivo(params.get(SISTEMA, "PLANILHA_CLIENTE_ROTATIVO"));
        txtSistema.setText(params.get(SISTEMA, "SISTEMA"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();

        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(txtSistema.getText(), SISTEMA, "SISTEMA");
        params.put(txtCodLojaOrigem.getText(), SISTEMA, "LOJA_CLIENTE");
        params.put(txtProdutoFile.getArquivo(), SISTEMA, "PLANILHA_PRODUTO");
        params.put(txtFamiliaFile.getArquivo(), SISTEMA, "PLANILHA_FAMILIA");
        params.put(txtFornecedorFile.getArquivo(), SISTEMA, "PLANILHA_FORNECEDOR");
        params.put(txtFornecedorMercFile.getArquivo(), SISTEMA, "PLANILHA_PRODUTO_FORNECEDOR");
        params.put(txtClienteFile.getArquivo(), SISTEMA, "PLANILHA_CLIENTE");
        params.put(txtCRFile.getArquivo(), SISTEMA, "PLANILHA_CLIENTE_ROTATIVO");

        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private PhoenixDAO dao = new PhoenixDAO();

    private PhoenixGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        tabProdutos.setOpcoesDisponiveis(dao);

        carregarParametros();

        dao.setLojaOrigem(txtCodLojaOrigem.getText());

        centralizarForm();
        this.setMaximum(false);

        carregarLojaVR();
    }

    public void validarDadosAcesso() throws Exception {
        gravarParametros();

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

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new PhoenixGUI(i_mdiFrame);
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

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;

                    Importador importador = new Importador(dao);

                    dao.setLojaOrigem(txtCodLojaOrigem.getText());
                    importador.setLojaOrigem(txtCodLojaOrigem.getText());
                    importador.setLojaVR(idLojaVR);
                    tabProdutos.setImportador(importador);
                    dao.setFormatoData(txtFormatoData.getText());

                    if (tabsProduto.getSelectedIndex() == 0) {
                        dao.setArquivoFamilia(txtFamiliaFile.getArquivo());
                        dao.setArquivo(txtProdutoFile.getArquivo());

                        tabProdutos.setImportador(importador);
                        tabProdutos.executarImportacao();
                    } else if (tabsProduto.getSelectedIndex() == 1) {

                        if (chkFornecedor.isSelected()) {
                            dao.setArquivoFornecedor(txtFornecedorFile.getArquivo());
                            importador.importarFornecedor();
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            dao.setArquivoProdutoFornecedor(txtFornecedorMercFile.getArquivo());
                            importador.importarProdutoFornecedor();
                        }
                    } else if (tabsProduto.getSelectedIndex() == 2) {

                        if (chkCliente.isSelected()) {
                            dao.setArquivoCliente(txtClienteFile.getArquivo());
                            
                            List<OpcaoCliente> opcoes = new ArrayList<>();
                            opcoes.add(OpcaoCliente.DADOS);

                            importador.importarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                        }
                        
                        if (chkCR.isSelected()) {
                            dao.setArquivoRotativo(txtCRFile.getArquivo());
                            
                            importador.importarCreditoRotativo();
                        }
                    }

                    gravarParametros();

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
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

        pnlBotao = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        lblLoja = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        lblSistema = new vrframework.bean.label.VRLabel();
        txtSistema = new javax.swing.JTextField();
        lblOrigem = new vrframework.bean.label.VRLabel();
        txtCodLojaOrigem = new javax.swing.JTextField();
        tabsProduto = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabCliente = new vrframework.bean.panel.VRPanel();
        chkCliente = new vrframework.bean.checkBox.VRCheckBox();
        chkCR = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        tabBalanca = new vrframework.bean.panel.VRPanel();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        txtFormatoData = new javax.swing.JTextField();
        vRLabel16 = new vrframework.bean.label.VRLabel();
        tabPrincipal = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabArquivos = new vrframework.bean.panel.VRPanel();
        lblFileFamilia = new vrframework.bean.label.VRLabel();
        txtFamiliaFile = new vrframework.bean.fileChooser.VRFileChooser();
        lblProdutoFile = new vrframework.bean.label.VRLabel();
        txtProdutoFile = new vrframework.bean.fileChooser.VRFileChooser();
        tabArquivosForn = new vrframework.bean.panel.VRPanel();
        lblFileFornecedor = new vrframework.bean.label.VRLabel();
        txtFornecedorFile = new vrframework.bean.fileChooser.VRFileChooser();
        lblFileFornecedorMercadoria = new vrframework.bean.label.VRLabel();
        txtFornecedorMercFile = new vrframework.bean.fileChooser.VRFileChooser();
        tabArquivosCliente = new vrframework.bean.panel.VRPanel();
        lblFileCliente = new vrframework.bean.label.VRLabel();
        txtClienteFile = new vrframework.bean.fileChooser.VRFileChooser();
        lblFileCR = new vrframework.bean.label.VRLabel();
        txtCRFile = new vrframework.bean.fileChooser.VRFileChooser();

        setTitle("Importação Phoenix");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(683, 494));

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

        lblLoja.setText("Loja:");

        lblSistema.setText("Sistema");

        txtSistema.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSistemaonSetLojaEvent(evt);
            }
        });

        lblOrigem.setText("Loja Origem");

        txtCodLojaOrigem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodLojaOrigemonSetLojaEvent(evt);
            }
        });

        javax.swing.GroupLayout pnlBotaoLayout = new javax.swing.GroupLayout(pnlBotao);
        pnlBotao.setLayout(pnlBotaoLayout);
        pnlBotaoLayout.setHorizontalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBotaoLayout.createSequentialGroup()
                        .addComponent(lblLoja)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlBotaoLayout.setVerticalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoja))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotaoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabsProduto.addTab("Produtos", tabProdutos);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFornecedor.setText("Fornecedores");

        chkProdutoFornecedor.setText("Produto Fornecedor");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(561, Short.MAX_VALUE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(146, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabsProduto.addTab("Fornecedores", tabFornecedor);

        chkCliente.setText("Cliente Preferencial");

        chkCR.setText("Crédito Rotativo");

        javax.swing.GroupLayout tabClienteLayout = new javax.swing.GroupLayout(tabCliente);
        tabCliente.setLayout(tabClienteLayout);
        tabClienteLayout.setHorizontalGroup(
            tabClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(564, Short.MAX_VALUE))
        );
        tabClienteLayout.setVerticalGroup(
            tabClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Clientes", tabCliente);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifFornecedores.setText("Fornecedores (Somente com CPF/CNPJ)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(417, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Unificação", tabUnificacao);

        txtFormatoData.setText("yyyy-MM-dd");

        vRLabel16.setText("Formato de Data");

        javax.swing.GroupLayout tabBalancaLayout = new javax.swing.GroupLayout(tabBalanca);
        tabBalanca.setLayout(tabBalancaLayout);
        tabBalancaLayout.setHorizontalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addGroup(tabBalancaLayout.createSequentialGroup()
                        .addGroup(tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(vRLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFormatoData, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabBalancaLayout.setVerticalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFormatoData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Balança", tabBalanca);

        lblFileFamilia.setText("Planilha Familia (Familias.DB)");

        lblProdutoFile.setText("Planilha Produto Principal (Merc.DB)");

        javax.swing.GroupLayout tabArquivosLayout = new javax.swing.GroupLayout(tabArquivos);
        tabArquivos.setLayout(tabArquivosLayout);
        tabArquivosLayout.setHorizontalGroup(
            tabArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFamiliaFile, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addComponent(txtProdutoFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabArquivosLayout.createSequentialGroup()
                        .addGroup(tabArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProdutoFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFileFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 494, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabArquivosLayout.setVerticalGroup(
            tabArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFamiliaFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblProdutoFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProdutoFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        tabPrincipal.addTab("Arquivo Produto", tabArquivos);

        lblFileFornecedor.setText("Planilha Fornecedor (Fornec.DB)");

        lblFileFornecedorMercadoria.setText("Planilha Produto Fornecedor (FornecedorMercadoria.DB)");

        javax.swing.GroupLayout tabArquivosFornLayout = new javax.swing.GroupLayout(tabArquivosForn);
        tabArquivosForn.setLayout(tabArquivosFornLayout);
        tabArquivosFornLayout.setHorizontalGroup(
            tabArquivosFornLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosFornLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabArquivosFornLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFornecedorFile, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addComponent(txtFornecedorMercFile, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addGroup(tabArquivosFornLayout.createSequentialGroup()
                        .addGroup(tabArquivosFornLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFileFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFileFornecedorMercadoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabArquivosFornLayout.setVerticalGroup(
            tabArquivosFornLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosFornLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFornecedorFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFileFornecedorMercadoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFornecedorMercFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        tabPrincipal.addTab("Arquivo Fornecedor", tabArquivosForn);

        lblFileCliente.setText("Planilha Cliente (Cliente.DB)");

        lblFileCR.setText("Planilha Rotativo (ContasReceber.DB)");

        javax.swing.GroupLayout tabArquivosClienteLayout = new javax.swing.GroupLayout(tabArquivosCliente);
        tabArquivosCliente.setLayout(tabArquivosClienteLayout);
        tabArquivosClienteLayout.setHorizontalGroup(
            tabArquivosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabArquivosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClienteFile, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addComponent(txtCRFile, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addGroup(tabArquivosClienteLayout.createSequentialGroup()
                        .addGroup(tabArquivosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFileCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFileCR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabArquivosClienteLayout.setVerticalGroup(
            tabArquivosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabArquivosClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClienteFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFileCR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCRFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        tabPrincipal.addTab("Arquivo Cliente", tabArquivosCliente);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlBotao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBotao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabPrincipal.getAccessibleContext().setAccessibleName("Arquivo Produto");

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

    private void txtCodLojaOrigemonSetLojaEvent(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodLojaOrigemonSetLojaEvent
        pnlBalanca.setSistema(txtSistema.getText());
        pnlBalanca.setLoja(txtCodLojaOrigem.getText());
    }//GEN-LAST:event_txtCodLojaOrigemonSetLojaEvent

    private void txtSistemaonSetLojaEvent(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSistemaonSetLojaEvent
        pnlBalanca.setSistema(txtSistema.getText());
        pnlBalanca.setLoja(txtCodLojaOrigem.getText());
    }//GEN-LAST:event_txtSistemaonSetLojaEvent

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCR;
    private vrframework.bean.checkBox.VRCheckBox chkCliente;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrframework.bean.label.VRLabel lblFileCR;
    private vrframework.bean.label.VRLabel lblFileCliente;
    private vrframework.bean.label.VRLabel lblFileFamilia;
    private vrframework.bean.label.VRLabel lblFileFornecedor;
    private vrframework.bean.label.VRLabel lblFileFornecedorMercadoria;
    private javax.swing.JLabel lblLoja;
    private vrframework.bean.label.VRLabel lblOrigem;
    private vrframework.bean.label.VRLabel lblProdutoFile;
    private vrframework.bean.label.VRLabel lblSistema;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlBotao;
    private vrframework.bean.panel.VRPanel tabArquivos;
    private vrframework.bean.panel.VRPanel tabArquivosCliente;
    private vrframework.bean.panel.VRPanel tabArquivosForn;
    private vrframework.bean.panel.VRPanel tabBalanca;
    private vrframework.bean.panel.VRPanel tabCliente;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrframework.bean.tabbedPane.VRTabbedPane tabPrincipal;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsProduto;
    private vrframework.bean.fileChooser.VRFileChooser txtCRFile;
    private vrframework.bean.fileChooser.VRFileChooser txtClienteFile;
    private javax.swing.JTextField txtCodLojaOrigem;
    private vrframework.bean.fileChooser.VRFileChooser txtFamiliaFile;
    private javax.swing.JTextField txtFormatoData;
    private vrframework.bean.fileChooser.VRFileChooser txtFornecedorFile;
    private vrframework.bean.fileChooser.VRFileChooser txtFornecedorMercFile;
    private vrframework.bean.fileChooser.VRFileChooser txtProdutoFile;
    private javax.swing.JTextField txtSistema;
    private vrframework.bean.label.VRLabel vRLabel16;
    private vrframework.bean.panel.VRPanel vRPanel1;
    // End of variables declaration//GEN-END:variables

}
