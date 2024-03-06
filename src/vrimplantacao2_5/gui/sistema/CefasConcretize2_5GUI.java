package vrimplantacao2_5.gui.sistema;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.dao.sistema.CefasConcretizeDAO2_5;
import vrimplantacao2_5.vo.enums.ESistema;

public class CefasConcretize2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.CEFAS.getNome();
    private static CefasConcretize2_5GUI instance;

    private final CefasConcretizeDAO2_5 dao = new CefasConcretizeDAO2_5();

    private int vTipoVenda = -1;
    private int vEstoque = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        pnlBalanca.setSistema(SISTEMA);
        pnlBalanca.setLoja(dao.getLojaOrigem());
        vTipoVenda = params.getInt(SISTEMA, "TIPO_VENDA");
        vEstoque = params.getInt(SISTEMA, "ESTOQUE");
        chkClClientes.setSelected(false);
        chkClEmpresas.setSelected(false);
        chkClFornecedores.setSelected(false);
        chkClTransp.setSelected(false);
        chkClAdminCard.setSelected(false);
        JSONArray array = new JSONArray(params.getWithNull("[]", SISTEMA, "OPCOES_CLIENTE"));
        for (int i = 0; i < array.length(); i++) {
            switch (array.getInt(i)) {
                case 0:
                    chkClClientes.setSelected(true);
                    break;
                case 1:
                    chkClEmpresas.setSelected(true);
                    break;
                case 2:
                    chkClFornecedores.setSelected(true);
                    break;
                case 3:
                    chkClTransp.setSelected(true);
                    break;
                case 4:
                    chkClAdminCard.setSelected(true);
                    break;
            }
        }
    }

    public CefasConcretize2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();
        tabProdutos.setOpcoesDisponiveis(dao);
        tabFornecedores.setOpcoesDisponiveis(dao);
        tabClientes.setOpcoesDisponiveis(dao);
        tabProdutos.btnMapaTribut.setEnabled(false);

        tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                return dao.getSistema() + " - " + pnlConn.idConexao;
            }

            @Override
            public String getLoja() {
                dao.setLojaOrigem(pnlConn.getLojaOrigem());
                return dao.getLojaOrigem();
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });

        pnlConn.setSistema(ESistema.CEFAS);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);

        tabRotativo.pnlLista.setEnabled(false);
        tabRotativo.chkAtivar.setEnabled(false);
        tabCheque.pnlLista.setEnabled(false);
        tabCheque.chkAtivarCheque.setEnabled(false);

        txtDtVencContasPagar.setVisible(true);
        txtDtVencContasPagar.setFormats("dd/MM/yyyy");
        txtDataFimOferta.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(pnlConn.getHost(), SISTEMA, "HOST");
        params.put(pnlConn.getSchema(), SISTEMA, "DATABASE");
        params.put(pnlConn.getPorta(), SISTEMA, "PORTA");
        params.put(pnlConn.getUsuario(), SISTEMA, "USUARIO");
        params.put(pnlConn.getSenha(), SISTEMA, "SENHA");

        pnlConn.atualizarParametros();

        ItemComboVO tipoVenda = (ItemComboVO) cmbTipoVenda1.getSelectedItem();
        if (tipoVenda != null) {
            params.put(tipoVenda.id, SISTEMA, "TIPO_VENDA");
            vTipoVenda = tipoVenda.id;
        }

        ItemComboVO estoque = (ItemComboVO) cmbEstoque1.getSelectedItem();
        if (estoque != null) {
            params.put(estoque.id, SISTEMA, "ESTOQUE");
            vEstoque = estoque.id;
        }
        JSONArray array = new JSONArray();
        if (chkClClientes.isSelected()) {
            array.put(0);
        }
        if (chkClEmpresas.isSelected()) {
            array.put(1);
        }
        if (chkClFornecedores.isSelected()) {
            array.put(2);
        }
        if (chkClTransp.isSelected()) {
            array.put(3);
        }
        if (chkClAdminCard.isSelected()) {
            array.put(4);
        }
        params.put(array.toString(), SISTEMA, "OPCOES_CLIENTES");

        params.salvar();
    }

    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, balanca;
            String idLojaCliente;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = pnlConn.getLojaVR();
                    idLojaCliente = pnlConn.getLojaOrigem();
                    int idTipoVenda = ((ItemComboVO) cmbTipoVenda1.getSelectedItem()).id;

                    Importador importador = new Importador(dao);

                    importador.setLojaOrigem(pnlConn.getLojaOrigem());
                    importador.setLojaVR(pnlConn.getLojaVR());
                    importador.setIdConexao(pnlConn.idConexao);

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);
                    /*if (tabProdutos.edtDtVendaIni.getDate() != null) {
                        dao.vendaDataInicio(tabProdutos.edtDtVendaIni.getDate());
                    }
                    if (tabProdutos.edtDtVendaFim.getDate() != null) {
                        dao.vendaDataTermino(tabProdutos.edtDtVendaFim.getDate());
                    }*/

                    pnlConn.atualizarParametros();

                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());

                    pnlConn.fecharConexao();
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
                instance = new CefasConcretize2_5GUI(i_mdiFrame);
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

        pnlMigrar = new vrframework.bean.panel.VRPanel();
        jBLimpar = new javax.swing.JButton();
        btnMigrar = new vrframework.bean.button.VRButton();
        tabMenu = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabCli = new javax.swing.JPanel();
        scpClientes = new javax.swing.JScrollPane();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tabExtras = new javax.swing.JTabbedPane();
        tabProdutos2 = new javax.swing.JPanel();
        tabEFornecedor1 = new javax.swing.JPanel();
        vRLabel10 = new vrframework.bean.label.VRLabel();
        cmbTipoVenda1 = new vrframework.bean.comboBox.VRComboBox();
        vRLabel11 = new vrframework.bean.label.VRLabel();
        cmbEstoque1 = new vrframework.bean.comboBox.VRComboBox();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        tabCli2 = new javax.swing.JPanel();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkClClientes = new javax.swing.JCheckBox();
        chkClEmpresas = new javax.swing.JCheckBox();
        chkClFornecedores = new javax.swing.JCheckBox();
        chkClTransp = new javax.swing.JCheckBox();
        chkClAdminCard = new javax.swing.JCheckBox();
        chkNUtilizaPlanoConta = new vrframework.bean.checkBox.VRCheckBox();
        tabRotativo = new vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasRotativoGUI();
        tabCheque = new vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasChequeGUI();
        tabFornecedores2 = new javax.swing.JPanel();
        tabEFornecedor = new javax.swing.JPanel();
        chkFamiliaFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedorXFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkIncluirTransportadores = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedorDivisao = new vrframework.bean.checkBox.VRCheckBox();
        chkContasAPagar = new vrframework.bean.checkBox.VRCheckBox();
        chkReceberDevForn = new vrframework.bean.checkBox.VRCheckBox();
        txtDtVencContasPagar = new org.jdesktop.swingx.JXDatePicker();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkNotasFiscais1 = new vrframework.bean.checkBox.VRCheckBox();
        edtDtNotaIni1 = new org.jdesktop.swingx.JXDatePicker();
        edtDtNotaFim1 = new org.jdesktop.swingx.JXDatePicker();
        chkNotaEntrada1 = new vrframework.bean.checkBox.VRCheckBox();
        chkNotaSaida1 = new vrframework.bean.checkBox.VRCheckBox();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("Arius 2_5");
        setMinimumSize(new java.awt.Dimension(50, 31));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(709, 681));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jBLimpar.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jBLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        jBLimpar.setText("Limpar");
        jBLimpar.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimparActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout pnlMigrarLayout = new javax.swing.GroupLayout(pnlMigrar);
        pnlMigrar.setLayout(pnlMigrarLayout);
        pnlMigrarLayout.setHorizontalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMigrarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jBLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlMigrarLayout.setVerticalGroup(
            pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlMigrarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlMigrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBLimpar)))
        );

        tabImportacao.addTab("Produtos", tabProdutos);
        tabImportacao.addTab("Fornecedores", tabFornecedores);

        scpClientes.setViewportView(tabClientes);

        javax.swing.GroupLayout tabCliLayout = new javax.swing.GroupLayout(tabCli);
        tabCli.setLayout(tabCliLayout);
        tabCliLayout.setHorizontalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpClientes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );
        tabCliLayout.setVerticalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabCli);

        tabMenu.addTab("Importação", tabImportacao);
        tabMenu.addTab("Balança", pnlBalanca);

        javax.swing.GroupLayout tabEFornecedor1Layout = new javax.swing.GroupLayout(tabEFornecedor1);
        tabEFornecedor1.setLayout(tabEFornecedor1Layout);
        tabEFornecedor1Layout.setHorizontalGroup(
            tabEFornecedor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
        );
        tabEFornecedor1Layout.setVerticalGroup(
            tabEFornecedor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        vRLabel10.setText("Tipo Venda");

        vRLabel11.setText("Estoque");

        chkOfertas.setText("Ofertas a partir de: ");

        javax.swing.GroupLayout tabProdutos2Layout = new javax.swing.GroupLayout(tabProdutos2);
        tabProdutos2.setLayout(tabProdutos2Layout);
        tabProdutos2Layout.setHorizontalGroup(
            tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdutos2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabProdutos2Layout.createSequentialGroup()
                        .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbEstoque1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabProdutos2Layout.createSequentialGroup()
                        .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabProdutos2Layout.createSequentialGroup()
                        .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbTipoVenda1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabEFornecedor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );
        tabProdutos2Layout.setVerticalGroup(
            tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabProdutos2Layout.createSequentialGroup()
                .addGroup(tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabEFornecedor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabProdutos2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbEstoque1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(tabProdutos2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoVenda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        tabExtras.addTab("Produtos", tabProdutos2);

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

        chkNUtilizaPlanoConta.setText("Não Utiliza Plano de Conta Rotativo");

        javax.swing.GroupLayout tabCli2Layout = new javax.swing.GroupLayout(tabCli2);
        tabCli2.setLayout(tabCli2Layout);
        tabCli2Layout.setHorizontalGroup(
            tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCli2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkNUtilizaPlanoConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabCli2Layout.createSequentialGroup()
                        .addComponent(tabRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tabCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(70, 70, 70))
        );
        tabCli2Layout.setVerticalGroup(
            tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCli2Layout.createSequentialGroup()
                .addGroup(tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabCli2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkNUtilizaPlanoConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabCli2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(tabCli2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tabCheque, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .addComponent(tabRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabExtras.addTab("Clientes", tabCli2);

        chkFamiliaFornecedor.setText("Família Fornecedor");
        chkFamiliaFornecedor.setEnabled(true);
        chkFamiliaFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaFornecedorActionPerformed(evt);
            }
        });

        chkFornecedorXFamilia.setText("Fornecedor x Família");
        chkFornecedorXFamilia.setEnabled(true);
        chkFornecedorXFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorXFamiliaActionPerformed(evt);
            }
        });

        chkIncluirTransportadores.setText("Incluir transportadores nos fornecedores");
        chkIncluirTransportadores.setEnabled(true);
        chkIncluirTransportadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncluirTransportadoresActionPerformed(evt);
            }
        });

        chkFornecedorDivisao.setText("Divisão");
        chkFornecedorDivisao.setEnabled(true);
        chkFornecedorDivisao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorDivisaoActionPerformed(evt);
            }
        });

        chkContasAPagar.setText("Contas à Pagar");
        chkContasAPagar.setEnabled(true);
        chkContasAPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkContasAPagarActionPerformed(evt);
            }
        });

        chkReceberDevForn.setText("Receber Devolução");

        javax.swing.GroupLayout tabEFornecedorLayout = new javax.swing.GroupLayout(tabEFornecedor);
        tabEFornecedor.setLayout(tabEFornecedorLayout);
        tabEFornecedorLayout.setHorizontalGroup(
            tabEFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabEFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabEFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFamiliaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornecedorXFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIncluirTransportadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornecedorDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkReceberDevForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabEFornecedorLayout.createSequentialGroup()
                        .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDtVencContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(134, Short.MAX_VALUE))
        );
        tabEFornecedorLayout.setVerticalGroup(
            tabEFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabEFornecedorLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(chkFamiliaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornecedorXFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkIncluirTransportadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornecedorDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(tabEFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtVencContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkReceberDevForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Notas Fiscais"));

        chkNotasFiscais1.setEnabled(true);
        chkNotasFiscais1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotasFiscais1ActionPerformed(evt);
            }
        });

        chkNotaEntrada1.setText("Entrada");
        chkNotaEntrada1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotaEntrada1ActionPerformed(evt);
            }
        });

        chkNotaSaida1.setText("Saída");
        chkNotaSaida1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotaSaida1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkNotaEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkNotaSaida1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addComponent(chkNotasFiscais1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtDtNotaIni1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtDtNotaFim1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNotaEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNotaSaida1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkNotasFiscais1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtDtNotaIni1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtDtNotaFim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabFornecedores2Layout = new javax.swing.GroupLayout(tabFornecedores2);
        tabFornecedores2.setLayout(tabFornecedores2Layout);
        tabFornecedores2Layout.setHorizontalGroup(
            tabFornecedores2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedores2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedores2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tabEFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabFornecedores2Layout.setVerticalGroup(
            tabFornecedores2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedores2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(vRPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabEFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabExtras.addTab("Fornecedores", tabFornecedores2);

        tabMenu.addTab("Extras", tabExtras);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnlMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tabMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            gravarParametros();
            importarTabelas();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void chkNotaSaida1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotaSaida1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkNotaSaida1ActionPerformed

    private void chkNotaEntrada1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotaEntrada1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkNotaEntrada1ActionPerformed

    private void chkNotasFiscais1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotasFiscais1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkNotasFiscais1ActionPerformed

    private void chkContasAPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkContasAPagarActionPerformed
        txtDtVencContasPagar.setEnabled(chkContasAPagar.isSelected());
        if (txtDtVencContasPagar.getDate() == null) {
            txtDtVencContasPagar.setDate(new Date(System.currentTimeMillis()));
        }
    }//GEN-LAST:event_chkContasAPagarActionPerformed

    private void chkFornecedorDivisaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorDivisaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorDivisaoActionPerformed

    private void chkIncluirTransportadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncluirTransportadoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkIncluirTransportadoresActionPerformed

    private void chkFornecedorXFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorXFamiliaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorXFamiliaActionPerformed

    private void chkFamiliaFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaFornecedorActionPerformed

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        tabClientes.limparCliente();
        tabFornecedores.limparFornecedor();
    }//GEN-LAST:event_jBLimparActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.JCheckBox chkClAdminCard;
    private javax.swing.JCheckBox chkClClientes;
    private javax.swing.JCheckBox chkClEmpresas;
    private javax.swing.JCheckBox chkClFornecedores;
    private javax.swing.JCheckBox chkClTransp;
    private vrframework.bean.checkBox.VRCheckBox chkContasAPagar;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedorDivisao;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedorXFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkIncluirTransportadores;
    private vrframework.bean.checkBox.VRCheckBox chkNUtilizaPlanoConta;
    private vrframework.bean.checkBox.VRCheckBox chkNotaEntrada1;
    private vrframework.bean.checkBox.VRCheckBox chkNotaSaida1;
    private vrframework.bean.checkBox.VRCheckBox chkNotasFiscais1;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkReceberDevForn;
    private vrframework.bean.comboBox.VRComboBox cmbEstoque1;
    private vrframework.bean.comboBox.VRComboBox cmbTipoVenda1;
    private org.jdesktop.swingx.JXDatePicker edtDtNotaFim1;
    private org.jdesktop.swingx.JXDatePicker edtDtNotaIni1;
    private javax.swing.JButton jBLimpar;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private javax.swing.JScrollPane scpClientes;
    private vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasChequeGUI tabCheque;
    private javax.swing.JPanel tabCli;
    private javax.swing.JPanel tabCli2;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private javax.swing.JPanel tabEFornecedor;
    private javax.swing.JPanel tabEFornecedor1;
    private javax.swing.JTabbedPane tabExtras;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private javax.swing.JPanel tabFornecedores2;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabMenu;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JPanel tabProdutos2;
    private vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasRotativoGUI tabRotativo;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private org.jdesktop.swingx.JXDatePicker txtDtVencContasPagar;
    private vrframework.bean.label.VRLabel vRLabel10;
    private vrframework.bean.label.VRLabel vRLabel11;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel8;
    // End of variables declaration//GEN-END:variables
}
