package vrimplantacao2_5.gui.sistema;

import java.awt.Frame;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.dao.interfaces.winthor.Winthor_PcSistemasDAO;
import vrimplantacao2.dao.interfaces.winthor.Winthor_PcSistemasDAO.Regiao;
import vrimplantacao2.dao.interfaces.winthor.Winthor_PcSistemasDAO.TipoRotativo;
import vrimplantacao2_5.vo.enums.ESistema;

public class Winthor_PcSistemas2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.WINTHOR.getNome();
    private static Winthor_PcSistemas2_5GUI instance;

    private final Winthor_PcSistemasDAO dao = new Winthor_PcSistemasDAO();

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        pnlBalanca.setSistema(SISTEMA);
        pnlBalanca.setLoja(dao.getLojaOrigem());
    }

    public Winthor_PcSistemas2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();
        carregarRegioes();
        carregaTipoRotativo();
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

        pnlConn.setSistema(ESistema.WINTHOR);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
    }
    
    private class TipoRotativoListModel extends AbstractListModel<TipoRotativo> implements ComboBoxModel<TipoRotativo> {

        List<TipoRotativo> rotativo = dao.getTipoRotativo();
        private TipoRotativo selecionado;

        @Override
        public int getSize() {
            return rotativo.size();
        }

        @Override
        public TipoRotativo getElementAt(int index) {
            return rotativo.get(index);
        }

        @Override
        public void setSelectedItem(Object selecionado) {
            if (selecionado instanceof Integer) {
                for (TipoRotativo rotativo : rotativo) {
                    if (rotativo.descricaocobranca == (String) selecionado) {
                        this.selecionado = rotativo;
                        return;
                    }
                }
            }
            if (selecionado instanceof TipoRotativo) {
                this.selecionado = (TipoRotativo) selecionado;
                return;
            }
            this.selecionado = null;
        }

        @Override
        public TipoRotativo getSelectedItem() {
            return this.selecionado;
        }

    }

    private class RegiaoListModel extends AbstractListModel<Regiao> implements ComboBoxModel<Regiao> {

        List<Regiao> regioes = dao.getRegioes();
        private Regiao selectionado;

        @Override
        public int getSize() {
            return regioes.size();
        }

        @Override
        public Regiao getElementAt(int index) {
            return regioes.get(index);
        }

        @Override
        public void setSelectedItem(Object selecionado) {
            if (selecionado instanceof Integer) {
                for (Regiao regiao : regioes) {
                    if (regiao.id == (int) selecionado) {
                        this.selectionado = regiao;
                        return;
                    }
                }
            }
            if (selecionado instanceof Regiao) {
                this.selectionado = (Regiao) selecionado;
                return;
            }
            this.selectionado = null;
        }

        @Override
        public Regiao getSelectedItem() {
            return this.selectionado;
        }

    }
    
    public void carregaTipoRotativo() throws Exception{
        cmbTipoRotativo.setModel(new TipoRotativoListModel());
        cmbTipoRotativo.getModel().setSelectedItem(Parametros.get().get(SISTEMA, "TIPO_ROTATIVO"));
    }

    public void carregarRegioes() throws Exception {
        cmbDentroUf.setModel(new RegiaoListModel());
        cmbDentroUf.getModel().setSelectedItem(Parametros.get().getInt(SISTEMA, "REGIAO_DENTRO_ESTADO"));
        cmbForaUf.setModel(new RegiaoListModel());
        cmbForaUf.getModel().setSelectedItem(Parametros.get().getInt(SISTEMA, "REGIAO_FORA_ESTADO"));
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

        params.salvar();
    }

    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, balanca;
            String idLojaCliente;
            String lojaMesmoID;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = pnlConn.getLojaVR();
                    idLojaCliente = pnlConn.getLojaOrigem();

                    dao.setIdRegiaoDentroEstado(((Regiao) cmbDentroUf.getModel().getSelectedItem()).id);
                    dao.setIdRegiaoForaEstado(((Regiao) cmbForaUf.getModel().getSelectedItem()).id);
                    dao.setSomenteClienteFidelidade(chkClientePreferenciaSomenteClube.isSelected());
                    dao.setTipoRotativo(((TipoRotativo) cmbTipoRotativo.getModel().getSelectedItem()).descricaocobranca);
                    dao.setTributacaoNcmFigura(chkTributacaoNcmFigura.isSelected());
                    dao.setPrecoUnitario(chkPrecoUnitario.isSelected());

                    Importador importador = new Importador(dao);

                    importador.setLojaOrigem(pnlConn.getLojaOrigem());
                    importador.setLojaVR(pnlConn.getLojaVR());
                    importador.setIdConexao(pnlConn.idConexao);

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);

                    if (tabMenu.getSelectedIndex() == 0) {

                        switch (tabImportacao.getSelectedIndex()) {
                            case 0:
                                tabProdutos.executarImportacao();
                                break;
                            case 1:
                                tabFornecedores.executarImportacao();
                                break;
                            case 2:
                                tabClientes.executarImportacao();
                                break;
                            case 3:
                                if (chkPdvVendas.isSelected()) {
                                    dao.setDataVendaInicial(edtDtVendaIni.getDate());
                                    dao.setDataVendaFinal(edtDtVendaFim.getDate());

                                    importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                                }
                                break;
                            default: ;
                        }
                    }

                    pnlConn.atualizarParametros();

                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());

                    pnlConn.fecharConexao();
                } catch (Exception ex) {
                    ProgressBar.dispose();
                    ex.printStackTrace();
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
                instance = new Winthor_PcSistemas2_5GUI(i_mdiFrame);
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
        btnMigrar = new vrframework.bean.button.VRButton();
        jBLimpar = new javax.swing.JButton();
        tabMenu = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        tabCli = new javax.swing.JPanel();
        scpClientes = new javax.swing.JScrollPane();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        tabVendas = new javax.swing.JPanel();
        pnlImpPdvVenda = new vrframework.bean.panel.VRPanel();
        jLabel15 = new javax.swing.JLabel();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        edtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        edtDtVendaFim = new org.jdesktop.swingx.JXDatePicker();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tabExtras = new vrframework.bean.panel.VRPanel();
        lblLojaOrigem1 = new javax.swing.JLabel();
        cmbDentroUf = new javax.swing.JComboBox();
        lblLojaOrigem2 = new javax.swing.JLabel();
        cmbForaUf = new javax.swing.JComboBox();
        chkClientePreferenciaSomenteClube = new vrframework.bean.checkBox.VRCheckBox();
        lblLojaOrigem3 = new javax.swing.JLabel();
        cmbTipoRotativo = new javax.swing.JComboBox();
        chkTributacaoNcmFigura = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoUnitario = new vrframework.bean.checkBox.VRCheckBox();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("WINTHOR");
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

        jBLimpar.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jBLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        jBLimpar.setText("Limpar");
        jBLimpar.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimparActionPerformed(evt);
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
            .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jBLimpar)
        );

        tabImportacao.addTab("Produtos", tabProdutos);
        tabImportacao.addTab("Fornecedores", tabFornecedores);

        scpClientes.setViewportView(tabClientes);

        javax.swing.GroupLayout tabCliLayout = new javax.swing.GroupLayout(tabCli);
        tabCli.setLayout(tabCliLayout);
        tabCliLayout.setHorizontalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCliLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabCliLayout.setVerticalGroup(
            tabCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabCli);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("PDV VENDA");

        chkPdvVendas.setText("Pdv Venda");
        chkPdvVendas.setEnabled(true);

        edtDtVendaIni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtDtVendaIniActionPerformed(evt);
            }
        });

        edtDtVendaFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtDtVendaFimActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlImpPdvVendaLayout = new javax.swing.GroupLayout(pnlImpPdvVenda);
        pnlImpPdvVenda.setLayout(pnlImpPdvVendaLayout);
        pnlImpPdvVendaLayout.setHorizontalGroup(
            pnlImpPdvVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpPdvVendaLayout.createSequentialGroup()
                .addGroup(pnlImpPdvVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpPdvVendaLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel15))
                    .addGroup(pnlImpPdvVendaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
        );
        pnlImpPdvVendaLayout.setVerticalGroup(
            pnlImpPdvVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpPdvVendaLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpPdvVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpPdvVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(edtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(edtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlImpPdvVenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addComponent(pnlImpPdvVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 172, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Vendas", tabVendas);

        tabMenu.addTab("Importação", tabImportacao);
        tabMenu.addTab("Balança", pnlBalanca);

        lblLojaOrigem1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblLojaOrigem1.setText("Região Dentro UF");

        cmbDentroUf.setModel(new DefaultComboBoxModel());

        lblLojaOrigem2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblLojaOrigem2.setText("Região Fora UF");

        cmbForaUf.setModel(new DefaultComboBoxModel());

        chkClientePreferenciaSomenteClube.setText("Cliente Preferencial: Importar somente Clube de Vantagens");
        chkClientePreferenciaSomenteClube.setEnabled(true);
        chkClientePreferenciaSomenteClube.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferenciaSomenteClubeActionPerformed(evt);
            }
        });

        lblLojaOrigem3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblLojaOrigem3.setText("Tipo Rotativo");

        cmbTipoRotativo.setModel(new DefaultComboBoxModel());

        chkTributacaoNcmFigura.setText("Tributação por NCM/Figura Tributária");
        chkTributacaoNcmFigura.setEnabled(true);
        chkTributacaoNcmFigura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTributacaoNcmFiguraActionPerformed(evt);
            }
        });

        chkPrecoUnitario.setText("Preço unitario");
        chkPrecoUnitario.setEnabled(true);
        chkPrecoUnitario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrecoUnitarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabExtrasLayout = new javax.swing.GroupLayout(tabExtras);
        tabExtras.setLayout(tabExtrasLayout);
        tabExtrasLayout.setHorizontalGroup(
            tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabExtrasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblLojaOrigem2)
                            .addComponent(lblLojaOrigem1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbDentroUf, 0, 430, Short.MAX_VALUE)
                            .addComponent(cmbForaUf, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(chkClientePreferenciaSomenteClube, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabExtrasLayout.createSequentialGroup()
                        .addComponent(lblLojaOrigem3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTipoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkTributacaoNcmFigura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrecoUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        tabExtrasLayout.setVerticalGroup(
            tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabExtrasLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbDentroUf)
                    .addComponent(lblLojaOrigem1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbForaUf)
                    .addComponent(lblLojaOrigem2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkClientePreferenciaSomenteClube, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbTipoRotativo)
                    .addComponent(lblLojaOrigem3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkTributacaoNcmFigura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrecoUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        tabMenu.addTab("Parametros Extras", tabExtras);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                    .addComponent(pnlMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void edtDtVendaIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtDtVendaIniActionPerformed
        if (edtDtVendaIni.getDate() == null) {
            edtDtVendaIni.setDate(new Date());
        }
    }//GEN-LAST:event_edtDtVendaIniActionPerformed

    private void edtDtVendaFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtDtVendaFimActionPerformed
        if (edtDtVendaFim.getDate() == null) {
            edtDtVendaFim.setDate(new Date());
        }
    }//GEN-LAST:event_edtDtVendaFimActionPerformed

    private void chkClientePreferenciaSomenteClubeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferenciaSomenteClubeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferenciaSomenteClubeActionPerformed

    private void chkTributacaoNcmFiguraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTributacaoNcmFiguraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkTributacaoNcmFiguraActionPerformed

    private void chkPrecoUnitarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrecoUnitarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrecoUnitarioActionPerformed

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        tabClientes.limparCliente();
        tabFornecedores.limparFornecedor();
    }//GEN-LAST:event_jBLimparActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferenciaSomenteClube;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoUnitario;
    private vrframework.bean.checkBox.VRCheckBox chkTributacaoNcmFigura;
    private javax.swing.JComboBox cmbDentroUf;
    private javax.swing.JComboBox cmbForaUf;
    private javax.swing.JComboBox cmbTipoRotativo;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JButton jBLimpar;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel lblLojaOrigem1;
    private javax.swing.JLabel lblLojaOrigem2;
    private javax.swing.JLabel lblLojaOrigem3;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlImpPdvVenda;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private javax.swing.JScrollPane scpClientes;
    private javax.swing.JPanel tabCli;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private vrframework.bean.panel.VRPanel tabExtras;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabMenu;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JPanel tabVendas;
    // End of variables declaration//GEN-END:variables
}
