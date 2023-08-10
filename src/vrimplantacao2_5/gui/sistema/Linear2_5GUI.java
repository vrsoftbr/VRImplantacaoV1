package vrimplantacao2_5.gui.sistema;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import vrframework.bean.panel.VRPanel;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.dao.interfaces.linear.LinearDAO;
import vrimplantacao2.dao.interfaces.linear.LinearDAO.TipoTitulo;
import vrimplantacao2_5.vo.enums.ESistema;

public class Linear2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.LINEAR.getNome();
    private static Linear2_5GUI instance;

    private final LinearDAO dao = new LinearDAO();
    private Set<Integer> rotativoSelecionado = new HashSet<>();
    private Set<Integer> convenioSelecionado = new HashSet<>();

    private TipoDocumentoTableModel rotativoModel = new TipoDocumentoTableModel(new ArrayList<TipoTitulo>());
    private TipoDocumentoTableModel convenioModel = new TipoDocumentoTableModel(new ArrayList<TipoTitulo>());

    private void carregarTipoDocumento() throws Exception {
        this.rotativoModel = new TipoDocumentoTableModel(this.dao.getTipoDocumentoReceber());
        
        for (TipoTitulo t : this.rotativoModel.getItens()) {
            t.selected = rotativoSelecionado.contains(t.id);
        }

        this.rotativoModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TipoTitulo item = rotativoModel.getItens().get(e.getLastRow());

                if (item.selected) {
                    rotativoSelecionado.add(item.id);
                } else {
                    rotativoSelecionado.remove(item.id);
                }
            }
        });

        tblRotativo.setModel(this.rotativoModel);
        tblRotativo.getColumnModel().getColumn(2).setPreferredWidth(400);
        
        this.convenioModel = new TipoDocumentoTableModel(this.dao.getTipoDocumentoReceber());

        for (TipoTitulo f : this.convenioModel.getItens()) {
            f.selected = convenioSelecionado.contains(f.id);
        }
        this.convenioModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TipoTitulo item = convenioModel.getItens().get(e.getLastRow());
                if (item.selected) {
                    convenioSelecionado.add(item.id);
                } else {
                    convenioSelecionado.remove(item.id);
                }
            }
        });
        tblConvenio.setModel(this.convenioModel);        
        tblConvenio.getColumnModel().getColumn(2).setPreferredWidth(400);
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        chkSomenteEansUnitarios.setSelected(params.getBool(SISTEMA, "SOMENTE_PRODUTOS_UNITARIOS"));
        chkUtilizarEs1ParaCotacao.setSelected(params.getBool(false, SISTEMA, "UTILIZAR_ES!_PARA_COTACAO"));
        chkFiltrarProdutos.setSelected(params.getBool(false, SISTEMA, "FILTRAR_PRODUTOS"));
    }

    public Linear2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        pnlConn.setOnConectar(() -> {
            tabProdutos.btnMapaTribut.setEnabled(true);
            carregarTipoDocumento();
            gravarParametros();
        });

        carregarParametros();
        tabProdutos.setOpcoesDisponiveis(dao);
        tabFornecedores.setOpcoesDisponiveis(dao);
        tabClientes.setOpcoesDisponiveis(dao);
        tabProdutos.btnMapaTribut.setEnabled(false);
        pnlBalanca.setSistema(SISTEMA);
        pnlBalanca.setLoja(dao.getLojaOrigem());

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

        pnlConn.setSistema(ESistema.LINEAR);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
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

                    Importador importador = new Importador(dao);

                    importador.setLojaOrigem(pnlConn.getLojaOrigem());
                    importador.setLojaVR(pnlConn.getLojaVR());
                    importador.setIdConexao(pnlConn.idConexao);

                    tabProdutos.setImportador(importador);
                    tabFornecedores.setImportador(importador);
                    tabClientes.setImportador(importador);
                    dao.setMultiplicarQtdEmbalagemPeloVolume(chkUtilizarEs1ParaCotacao.isSelected());
                    dao.setFiltrarProdutos(chkFiltrarProdutos.isSelected());
                    dao.setTipoDocumentoRotativo(rotativoSelecionado);
                    dao.setTipoDocumentoConvenio(convenioSelecionado);

                    if (chkAjustarDigitoVerificador.isSelected()) {
                        dao.importarDigitoVerificador();
                    }

                    if (chkPdvVendas.isSelected()) {
                        dao.setVendaDataIni(edtDtVendaIni.getDate());
                        dao.setVendaDataFim(edtDtVendaFim.getDate());
                        importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                    }

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
                            default:
                                break;
                        }
                    }

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
                instance = new Linear2_5GUI(i_mdiFrame);
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
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        pnlEspecial = new javax.swing.JTabbedPane();
        tbProdutosEspecial = new javax.swing.JPanel();
        chkSomenteEansUnitarios = new vrframework.bean.checkBox.VRCheckBox();
        chkUtilizarEs1ParaCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkFiltrarProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkAjustarDigitoVerificador = new vrframework.bean.checkBox.VRCheckBox();
        pnlVendas = new vrframework.bean.panel.VRPanel();
        pnlPdvVendaDatas = new vrframework.bean.panel.VRPanel();
        edtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        edtDtVendaFim = new org.jdesktop.swingx.JXDatePicker();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        tabConvenio1 = new javax.swing.JPanel();
        scrollRotativo = new javax.swing.JScrollPane();
        tblRotativo = new vrframework.bean.table.VRTable();
        vRLabel1 = new vr.view.components.label.VRLabel();
        tabConvenio2 = new javax.swing.JPanel();
        scrollRotativo1 = new javax.swing.JScrollPane();
        tblConvenio = new vrframework.bean.table.VRTable();
        vRLabel2 = new vr.view.components.label.VRLabel();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("Linear");
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
            .addComponent(scpClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Clientes", tabCli);

        tabMenu.addTab("Importação", tabImportacao);
        tabMenu.addTab("Balança", pnlBalanca);

        chkSomenteEansUnitarios.setText("Somente EANs unitários");

        chkUtilizarEs1ParaCotacao.setText("Utilizar volume x qtdembalagem na produto e produtofornecedor");

        chkFiltrarProdutos.setText("Rede Paranaíba: Filtrar produtos");

        chkAjustarDigitoVerificador.setText("Ajustar Código de Barras (Digito Verificador)");

        pnlVendas.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Vendas (PDV)"));

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

        javax.swing.GroupLayout pnlPdvVendaDatasLayout = new javax.swing.GroupLayout(pnlPdvVendaDatas);
        pnlPdvVendaDatas.setLayout(pnlPdvVendaDatasLayout);
        pnlPdvVendaDatasLayout.setHorizontalGroup(
            pnlPdvVendaDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPdvVendaDatasLayout.createSequentialGroup()
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtVendaFim, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
        );
        pnlPdvVendaDatasLayout.setVerticalGroup(
            pnlPdvVendaDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPdvVendaDatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(edtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        chkPdvVendas.setEnabled(true);
        chkPdvVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPdvVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVendasLayout = new javax.swing.GroupLayout(pnlVendas);
        pnlVendas.setLayout(pnlVendasLayout);
        pnlVendasLayout.setHorizontalGroup(
            pnlVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(287, Short.MAX_VALUE))
        );
        pnlVendasLayout.setVerticalGroup(
            pnlVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tbProdutosEspecialLayout = new javax.swing.GroupLayout(tbProdutosEspecial);
        tbProdutosEspecial.setLayout(tbProdutosEspecialLayout);
        tbProdutosEspecialLayout.setHorizontalGroup(
            tbProdutosEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tbProdutosEspecialLayout.createSequentialGroup()
                .addGroup(tbProdutosEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSomenteEansUnitarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUtilizarEs1ParaCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFiltrarProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAjustarDigitoVerificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 202, Short.MAX_VALUE))
            .addGroup(tbProdutosEspecialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlVendas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tbProdutosEspecialLayout.setVerticalGroup(
            tbProdutosEspecialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tbProdutosEspecialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkSomenteEansUnitarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUtilizarEs1ParaCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFiltrarProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAjustarDigitoVerificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(116, Short.MAX_VALUE))
        );

        pnlEspecial.addTab("Especial", tbProdutosEspecial);

        tblRotativo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollRotativo.setViewportView(tblRotativo);

        vRLabel1.setText("Tipo Documento Rotativo");
        vRLabel1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N

        javax.swing.GroupLayout tabConvenio1Layout = new javax.swing.GroupLayout(tabConvenio1);
        tabConvenio1.setLayout(tabConvenio1Layout);
        tabConvenio1Layout.setHorizontalGroup(
            tabConvenio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenio1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabConvenio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabConvenio1Layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scrollRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabConvenio1Layout.setVerticalGroup(
            tabConvenio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenio1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlEspecial.addTab("Receber Crédito Rotativo", tabConvenio1);

        tblConvenio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollRotativo1.setViewportView(tblConvenio);

        vRLabel2.setText("Tipo Documento Rotativo");
        vRLabel2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N

        javax.swing.GroupLayout tabConvenio2Layout = new javax.swing.GroupLayout(tabConvenio2);
        tabConvenio2.setLayout(tabConvenio2Layout);
        tabConvenio2Layout.setHorizontalGroup(
            tabConvenio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenio2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabConvenio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabConvenio2Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scrollRotativo1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabConvenio2Layout.setVerticalGroup(
            tabConvenio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenio2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollRotativo1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlEspecial.addTab("Receber Convenio", tabConvenio2);

        tabMenu.addTab("Parâmetros", pnlEspecial);

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
                .addComponent(tabMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
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

    private void chkPdvVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPdvVendasActionPerformed

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        tabClientes.limparCliente();
        tabFornecedores.limparFornecedor();
        for (Component p : tbProdutosEspecial.getComponents()) {
            if (p instanceof JPanel) {
                for (Component c : ((JPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
    }//GEN-LAST:event_jBLimparActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAjustarDigitoVerificador;
    private vrframework.bean.checkBox.VRCheckBox chkFiltrarProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkSomenteEansUnitarios;
    private vrframework.bean.checkBox.VRCheckBox chkUtilizarEs1ParaCotacao;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JButton jBLimpar;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private javax.swing.JTabbedPane pnlEspecial;
    private vrframework.bean.panel.VRPanel pnlMigrar;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private vrframework.bean.panel.VRPanel pnlVendas;
    private javax.swing.JScrollPane scpClientes;
    private javax.swing.JScrollPane scrollRotativo;
    private javax.swing.JScrollPane scrollRotativo1;
    private javax.swing.JPanel tabCli;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private javax.swing.JPanel tabConvenio1;
    private javax.swing.JPanel tabConvenio2;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabMenu;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JPanel tbProdutosEspecial;
    private vrframework.bean.table.VRTable tblConvenio;
    private vrframework.bean.table.VRTable tblRotativo;
    private vr.view.components.label.VRLabel vRLabel1;
    private vr.view.components.label.VRLabel vRLabel2;
    // End of variables declaration//GEN-END:variables
class TipoDocumentoTableModel extends AbstractTableModel {

        private final List<LinearDAO.TipoTitulo> itens;

        public List<LinearDAO.TipoTitulo> getItens() {
            return itens;
        }

        public TipoDocumentoTableModel(List<LinearDAO.TipoTitulo> itens) {
            this.itens = itens;
        }

        @Override
        public int getRowCount() {
            return this.itens.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LinearDAO.TipoTitulo f = this.itens.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return f.selected;
                case 1:
                    return f.id;
                case 2:
                    return f.descricao;
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                LinearDAO.TipoTitulo item = this.itens.get(rowIndex);
                item.selected = (boolean) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                default:
                    return super.getColumnClass(columnIndex);
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "-";
                case 1:
                    return "Código";
                case 2:
                    return "Descrição";
                default:
                    return null;
            }
        }
    }
}
