package vrimplantacao2_5.gui.sistema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.GetWay_ProfitDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.parametro.Parametros;

public class GetWay_Profit2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = "GetWay";
    private static final String SERVIDOR_SQL = "Sql Server";
    private static GetWay_Profit2_5GUI instance;
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    private GetWay_ProfitDAO dao = new GetWay_ProfitDAO();
    private ConexaoSqlServer connSqlServer = new ConexaoSqlServer();

    private void carregarTipoDocumento() throws Exception {
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
        chkInverterAssociado.setSelected(params.getBool(SISTEMA, "INVERTER_ASSOCIADO"));
        chkUsarMargemBruta.setSelected(params.getBool(SISTEMA, "USAR_MARGEM_BRUTA"));
        chkUsarQtdCotacaoProdFornecedor.setSelected(params.getBool(SISTEMA, "USAR_QTD_COTACAO_NO_PRODFORN"));
        chkAssociadoSomenteAtivos.setSelected(params.getBool(SISTEMA, "ASSOCIADO_SOMENTE_ATIVO"));
        txtDataFimOferta.setDate(params.getDate(SISTEMA, "DATA_FIM_OFERTA"));
        chkDesconsiderarSetorBalanca.setSelected(params.getBool(SISTEMA, "DESCONSIDERAR_SETOR_BALANCA"));
        chkPesquisarKG.setSelected(params.getBool(SISTEMA, "PESQUISAR_KG_DESCRICAO"));
        chkUtilizarEmbalagemCompra.setSelected(params.getBool(SISTEMA, "UTILIZAR_EMBALAGEM_DE_COMPRA"));
        chkCopiarIcmsDebitoNaEntrada.setSelected(params.getBool(SISTEMA, "COPIAR_ICMS_DEBITO"));
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(chkInverterAssociado.isSelected(), SISTEMA, "INVERTER_ASSOCIADO");
        params.put(chkUsarMargemBruta.isSelected(), SISTEMA, "USAR_MARGEM_BRUTA");
        params.put(chkDesconsiderarSetorBalanca.isSelected(), SISTEMA, "DESCONSIDERAR_SETOR_BALANCA");
        params.put(chkPesquisarKG.isSelected(), SISTEMA, "PESQUISAR_KG_DESCRICAO");
        params.put(chkUsarQtdCotacaoProdFornecedor.isSelected(), SISTEMA, "USAR_QTD_COTACAO_NO_PRODFORN");
        params.put(chkAssociadoSomenteAtivos.isSelected(), SISTEMA, "ASSOCIADO_SOMENTE_ATIVO");
        params.put(chkUtilizarEmbalagemCompra.isSelected(), SISTEMA, "UTILIZAR_EMBALAGEM_DE_COMPRA");
        params.put((txtDataFimOferta.getDate() != null ? new java.sql.Date(txtDataFimOferta.getDate().getTime()) : null), SISTEMA, "DATA_FIM_OFERTA");
        params.put(chkCopiarIcmsDebitoNaEntrada.isSelected(), SISTEMA, "COPIAR_ICMS_DEBITO");

        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private GetWay_Profit2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        carregarParametros();

        edtDtVendaIni.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        edtDtVendaFim.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        txtDataFimOferta.setFormats(new SimpleDateFormat("dd/MM/yyyy"));

        centralizarForm();
        this.setMaximum(false);
    }

    public void carregarLojaCliente() throws Exception {
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
                instance = new GetWay_Profit2_5GUI(i_mdiFrame);
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
            String lojaMesmoId;
            DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            String strVendaDataInicio = "";
            String strVendaDataFim = "";
            java.sql.Date vendaDataInicio, vendaDataFim;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;

                    dao.v_usar_arquivoBalancaUnificacao = chkTemArquivoBalancaUnificacao.isSelected();
                    dao.usarMargemBruta = chkUsarMargemBruta.isSelected();
                    dao.setUsarQtdEmbDoProduto(chkUsarQtdCotacaoProdFornecedor.isSelected());
                    dao.utilizaMetodoAjustaAliquota = chkMetodoAliquota.isSelected();
                    dao.copiarDescricaoCompletaParaGondola = chkCopiaDescComplGondola.isSelected();

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);

                    dao.setUtilizarEmbalagemDeCompra(chkUtilizarEmbalagemCompra.isSelected());
                    dao.setCopiarIcmsDebitoNaEntrada(chkCopiarIcmsDebitoNaEntrada.isSelected());

                    switch (tabs.getSelectedIndex()) {
                        case 0:
                            break;
                        case 1:
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
                                    if (chkConvEmpresa.isSelected()) {
                                        importador.importarConvenioEmpresa();
                                    }
                                    if (chkConvConveniado.isSelected()) {
                                        importador.importarConvenioConveniado();
                                    }
                                    if (chkConvRecebimento.isSelected()) {
                                        importador.importarConvenioTransacao();
                                    }
                                    break;
                                case 4:
                                    if (chkPdvVendas.isSelected()) {
                                        dao.setDataInicioVenda(edtDtVendaIni.getDate());
                                        dao.setDataTerminoVenda(edtDtVendaFim.getDate());
                                        importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                                    }
                                    break;
                                default:
                                    break;
                            }   break;
                        case 2:
                            if (chkUnifProdutos.isSelected()) {
                                importador.unificarProdutos();
                            }   if (chkUnifFornecedor.isSelected()) {
                                importador.unificarFornecedor();
                            }   if (chkUnifProdutoFornecedor.isSelected()) {
                                importador.unificarProdutoFornecedor();
                            }   if (chkUnifClientePreferencial.isSelected()) {
                                importador.unificarClientePreferencial();
                            }   if (chkUnifClienteEventual.isSelected()) {
                                importador.unificarClienteEventual();
                            }   break;
                        default:
                            break;
                    }

                    gravarParametros();

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                    
                } catch (Exception ex) {
                    try {
                        connSqlServer.close();
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

        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabParametros = new javax.swing.JPanel();
        pnlParementosSistema = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel10 = new vrframework.bean.panel.VRPanel();
        chkInverterAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkAssociadoSomenteAtivos = new vrframework.bean.checkBox.VRCheckBox();
        chkUsarMargemBruta = new vrframework.bean.checkBox.VRCheckBox();
        chkUsarQtdCotacaoProdFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiarIcmsDebitoNaEntrada = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiaDescComplGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkDesconsiderarSetorBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkPesquisarKG = new vrframework.bean.checkBox.VRCheckBox();
        chkForcarUnidade = new vrframework.bean.checkBox.VRCheckBox();
        chkUtilizarEmbalagemCompra = new vrframework.bean.checkBox.VRCheckBox();
        chkMetodoAliquota = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        chkUsarMargemLiquidaPraticada = new vrframework.bean.checkBox.VRCheckBox();
        chkUsarMargemSobreVenda = new vrframework.bean.checkBox.VRCheckBox();
        tabConvenio1 = new javax.swing.JPanel();
        scrollRotativo = new javax.swing.JScrollPane();
        tblTipoDocumentoRotativo = new vrframework.bean.table.VRTable();
        vRLabel1 = new vr.view.components.label.VRLabel();
        jPanel1 = new javax.swing.JPanel();
        vRLabel2 = new vr.view.components.label.VRLabel();
        scrollRotativo1 = new javax.swing.JScrollPane();
        tblTipoDocumentoCheque = new vrframework.bean.table.VRTable();
        tabImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        vRPanel9 = new vrframework.bean.panel.VRPanel();
        tabFornecedores = new vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        tabClientes = new vrimplantacao2.gui.component.checks.ChecksClientePanelGUI();
        tabConvenio = new javax.swing.JPanel();
        chkConvEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkConvConveniado = new vrframework.bean.checkBox.VRCheckBox();
        chkConvRecebimento = new vrframework.bean.checkBox.VRCheckBox();
        tabVendas = new vrframework.bean.panel.VRPanel();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        pnlPdvVendaDatas = new vrframework.bean.panel.VRPanel();
        edtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        edtDtVendaFim = new org.jdesktop.swingx.JXDatePicker();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkTemArquivoBalancaUnificacao = new vrframework.bean.checkBox.VRCheckBox();

        setTitle("Importação GetWay");
        setToolTipText("");

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

        vRPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        vRPanel10.setMaximumSize(new java.awt.Dimension(400, 32767));
        vRPanel10.setMinimumSize(new java.awt.Dimension(400, 35));

        chkInverterAssociado.setText("Inverter Associado");
        chkInverterAssociado.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        chkAssociadoSomenteAtivos.setText("Somente produtos ativos (Associado)");
        chkAssociadoSomenteAtivos.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        chkUsarMargemBruta.setText("Usar Margem Bruta");
        chkUsarMargemBruta.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarMargemBruta.setEnabled(true);

        chkUsarQtdCotacaoProdFornecedor.setText("Usar qtd. de cotação no produto fornecedor");
        chkUsarQtdCotacaoProdFornecedor.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarQtdCotacaoProdFornecedor.setEnabled(true);

        chkCopiarIcmsDebitoNaEntrada.setText("Copiar ICMS débito na entrada");
        chkCopiarIcmsDebitoNaEntrada.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkCopiarIcmsDebitoNaEntrada.setEnabled(true);

        chkCopiaDescComplGondola.setText("Copiar Descrição Completa para Gôndola");

        chkDesconsiderarSetorBalanca.setText("Desconsiderar setor de balança");
        chkDesconsiderarSetorBalanca.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido,<br>neste caso é necessário utilizar o tipo de embalagem para determinar o que é de KG ou UN.<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkPesquisarKG.setText("Pesquisar flag KG na descrição");
        chkPesquisarKG.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkForcarUnidade.setText("Forçar Unidade");
        chkForcarUnidade.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkUtilizarEmbalagemCompra.setText("Utilizar embalagem de compra");
        chkUtilizarEmbalagemCompra.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkMetodoAliquota.setText("Utiliza Método Aliquota");

        jLabel3.setText("Importar ofertas apartir da data");

        chkUsarMargemLiquidaPraticada.setText("Usar Margem Liquida Praticada");
        chkUsarMargemLiquidaPraticada.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarMargemLiquidaPraticada.setEnabled(true);

        chkUsarMargemSobreVenda.setText("Usar Margem Sobre Venda");
        chkUsarMargemSobreVenda.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarMargemSobreVenda.setEnabled(true);

        javax.swing.GroupLayout vRPanel10Layout = new javax.swing.GroupLayout(vRPanel10);
        vRPanel10.setLayout(vRPanel10Layout);
        vRPanel10Layout.setHorizontalGroup(
            vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel10Layout.createSequentialGroup()
                        .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkUsarMargemBruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsarQtdCotacaoProdFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiaDescComplGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkUsarMargemLiquidaPraticada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsarMargemSobreVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkForcarUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPesquisarKG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUtilizarEmbalagemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMetodoAliquota)
                    .addComponent(chkDesconsiderarSetorBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(249, Short.MAX_VALUE))
        );
        vRPanel10Layout.setVerticalGroup(
            vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDesconsiderarSetorBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPesquisarKG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsarMargemBruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkForcarUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsarMargemLiquidaPraticada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkUtilizarEmbalagemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsarMargemSobreVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMetodoAliquota)
                    .addGroup(vRPanel10Layout.createSequentialGroup()
                        .addComponent(chkUsarQtdCotacaoProdFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCopiaDescComplGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(vRPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addContainerGap(293, Short.MAX_VALUE))
        );

        pnlParementosSistema.addTab("Produtos", vRPanel10);

        tblTipoDocumentoRotativo.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollRotativo.setViewportView(tblTipoDocumentoRotativo);

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
                        .addComponent(scrollRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                        .addGap(156, 156, 156))
                    .addGroup(tabConvenio1Layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        tabConvenio1Layout.setVerticalGroup(
            tabConvenio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenio1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addGap(242, 242, 242))
        );

        pnlParementosSistema.addTab("Receber Crédito Rotativo", tabConvenio1);

        vRLabel2.setText("Tipo Documento Cheque");
        vRLabel2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N

        tblTipoDocumentoCheque.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollRotativo1.setViewportView(tblTipoDocumentoCheque);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(scrollRotativo1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                        .addGap(156, 156, 156))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollRotativo1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addGap(242, 242, 242))
        );

        pnlParementosSistema.addTab("Receber Cheque", jPanel1);

        javax.swing.GroupLayout tabParametrosLayout = new javax.swing.GroupLayout(tabParametros);
        tabParametros.setLayout(tabParametrosLayout);
        tabParametrosLayout.setHorizontalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabParametrosLayout.createSequentialGroup()
                .addComponent(pnlParementosSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(77, 77, 77))
        );
        tabParametrosLayout.setVerticalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlParementosSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabs.addTab("Parâmetros Sistema Profit", tabParametros);

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        vRPanel7.setMaximumSize(new java.awt.Dimension(400, 32767));
        vRPanel7.setMinimumSize(new java.awt.Dimension(400, 35));
        vRPanel7.setPreferredSize(new java.awt.Dimension(400, 35));

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addComponent(tabProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 842, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );

        tabImportacao.addTab("Produtos", vRPanel7);

        vRPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout vRPanel9Layout = new javax.swing.GroupLayout(vRPanel9);
        vRPanel9.setLayout(vRPanel9Layout);
        vRPanel9Layout.setHorizontalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addComponent(tabFornecedores, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
                .addGap(89, 89, 89))
        );
        vRPanel9Layout.setVerticalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addComponent(tabFornecedores, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addGap(150, 150, 150))
        );

        tabImportacao.addTab("Fornecedores", vRPanel9);

        vRPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addComponent(tabClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(86, 86, 86))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel8Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(tabClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Clientes", vRPanel8);

        chkConvEmpresa.setText("Empresas");
        chkConvEmpresa.setEnabled(true);
        chkConvEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConvEmpresaActionPerformed(evt);
            }
        });

        chkConvConveniado.setText("Conveniados");
        chkConvConveniado.setEnabled(true);
        chkConvConveniado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConvConveniadoActionPerformed(evt);
            }
        });

        chkConvRecebimento.setText("Recebimentos em aberto");
        chkConvRecebimento.setEnabled(true);
        chkConvRecebimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConvRecebimentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabConvenioLayout = new javax.swing.GroupLayout(tabConvenio);
        tabConvenio.setLayout(tabConvenioLayout);
        tabConvenioLayout.setHorizontalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkConvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkConvRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(705, Short.MAX_VALUE))
        );
        tabConvenioLayout.setVerticalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkConvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkConvRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(419, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Convênio", tabConvenio);

        tabVendas.setBorder(null);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Vendas (PDV)"));

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
                .addComponent(edtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(549, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(412, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Vendas", tabVendas);

        tabs.addTab("Importação", tabImportacao);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifFornecedor.setText("Fornecedor (Somente com CPF/CNPJ)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifClientePreferencial.setText("Cliente Preferencial (Somente com CPF/CNPJ)");

        chkUnifClienteEventual.setText("Cliente Eventual (Somente com CPF/CNPJ)");

        chkTemArquivoBalancaUnificacao.setText("Tem Arquivo Balança");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(chkTemArquivoBalancaUnificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(519, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTemArquivoBalancaUnificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(396, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);

        jScrollPane2.setViewportView(tabs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
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

    private void chkConvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvEmpresaActionPerformed

    private void chkConvConveniadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvConveniadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvConveniadoActionPerformed

    private void chkConvRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvRecebimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvRecebimentoActionPerformed

    private void chkPdvVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPdvVendasActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAssociadoSomenteAtivos;
    private vrframework.bean.checkBox.VRCheckBox chkConvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkConvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkConvRecebimento;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaDescComplGondola;
    private vrframework.bean.checkBox.VRCheckBox chkCopiarIcmsDebitoNaEntrada;
    private vrframework.bean.checkBox.VRCheckBox chkDesconsiderarSetorBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkForcarUnidade;
    private vrframework.bean.checkBox.VRCheckBox chkInverterAssociado;
    private javax.swing.JCheckBox chkMetodoAliquota;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPesquisarKG;
    private vrframework.bean.checkBox.VRCheckBox chkTemArquivoBalancaUnificacao;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkUsarMargemBruta;
    private vrframework.bean.checkBox.VRCheckBox chkUsarMargemLiquidaPraticada;
    private vrframework.bean.checkBox.VRCheckBox chkUsarMargemSobreVenda;
    private vrframework.bean.checkBox.VRCheckBox chkUsarQtdCotacaoProdFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUtilizarEmbalagemCompra;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private vrframework.bean.tabbedPane.VRTabbedPane pnlParementosSistema;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private javax.swing.JScrollPane scrollRotativo;
    private javax.swing.JScrollPane scrollRotativo1;
    private vrimplantacao2.gui.component.checks.ChecksClientePanelGUI tabClientes;
    private javax.swing.JPanel tabConvenio;
    private javax.swing.JPanel tabConvenio1;
    private vrimplantacao2.gui.component.checks.ChecksFornecedorPanelGUI tabFornecedores;
    private vrframework.bean.tabbedPane.VRTabbedPane tabImportacao;
    private javax.swing.JPanel tabParametros;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.table.VRTable tblTipoDocumentoCheque;
    private vrframework.bean.table.VRTable tblTipoDocumentoRotativo;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private vr.view.components.label.VRLabel vRLabel1;
    private vr.view.components.label.VRLabel vRLabel2;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel10;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    // End of variables declaration//GEN-END:variables

}
