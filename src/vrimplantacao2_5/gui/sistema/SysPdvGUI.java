package vrimplantacao2_5.gui.sistema;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.SysPdvDAO;
import vrimplantacao2.dao.interfaces.SysPdvDAO.FinalizadoraRecord;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class SysPdvGUI extends VRInternalFrame {
    
    private static final Logger LOG = Logger.getLogger(SysPdvGUI.class.getName());
    
    public static final String SISTEMA = "SysPDV";
    private static final String SERVIDOR_SQL = "Firebird";
    private static SysPdvGUI instance;
    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    
    private SysPdvDAO dao = new SysPdvDAO();
    
    private Set<String> rotativoSelecionado = new HashSet<>();
    private Set<String> chequeSelecionado = new HashSet<>();
    
    private SysPdvGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();     
        
        tabProdutos.setOpcoesDisponiveis(dao);
        
        txtDtTerminoOferta.setFormats("dd/MM/yyyy");
        
        this.title = "Importação " + SISTEMA;
        
        carregarParametros();        
        centralizarForm();
        this.setMaximum(false);
    }
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();

        tabProdutos.carregarParametros(params, SISTEMA);
        txtDtTerminoOferta.setDate(params.getDate(SISTEMA, "DATA_TERMINO_OFERTA"));
        chkSoAtivos.setSelected(params.getBool(false, SISTEMA, "SO_ATIVOS"));
        chkGerarEANAtacado.setSelected(params.getBool(false, SISTEMA, "GERAR_EAN_PARA_ATACADO"));
        chkIgnorarEnviaBalanca.setSelected(params.getBool(false, SISTEMA, "IGNORA_ENVIAR_BALANCA"));
        chkOfertasEncarte.setSelected(params.getBool(false, SISTEMA, "OFERTAS_ENCARTE"));
        
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
        
        String strRotativoSelecionado = params.getWithNull("", SISTEMA, "ROTATIVO_SELECT");
        this.rotativoSelecionado.clear();
        for (String id: strRotativoSelecionado.split("\\|")) {
            if (!"".equals(id)) {
                this.rotativoSelecionado.add(id);
            }
        }
        String strChequeSelecionado = params.getWithNull("", SISTEMA, "CHEQUE_SELECT");
        this.chequeSelecionado.clear();
        for (String id: strChequeSelecionado.split("\\|")) {
            if (!"".equals(id)) {
                this.chequeSelecionado.add(id);
            }
        }
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(txtDtTerminoOferta.getDate(), SISTEMA, "DATA_TERMINO_OFERTA");        
        params.put(chkSoAtivos.isSelected(), SISTEMA, "SO_ATIVOS");
        params.put(chkGerarEANAtacado.isSelected(), SISTEMA, "GERAR_EAN_PARA_ATACADO");
        params.put(chkIgnorarEnviaBalanca.isSelected(), SISTEMA, "IGNORA_ENVIAR_BALANCA");
        params.put(chkOfertasEncarte.isSelected(), SISTEMA, "OFERTAS_ENCARTE");
        
        {
            StringBuilder builder = new StringBuilder();
            for (Iterator<String> iterator = this.rotativoSelecionado.iterator(); iterator.hasNext();) {
                builder.append(iterator.next());
                if (iterator.hasNext())
                    builder.append("|");
            }
            params.put(builder.toString(), SISTEMA, "ROTATIVO_SELECT");
        }
        {
            StringBuilder builder = new StringBuilder();
            for (Iterator<String> iterator = this.chequeSelecionado.iterator(); iterator.hasNext();) {
                builder.append(iterator.next());
                if (iterator.hasNext())
                    builder.append("|");
            }
            params.put(builder.toString(), SISTEMA, "CHEQUE_SELECT");
        }
        
        params.salvar();
    }
    
    private FinalizadoraTableModel rotativoModel = new FinalizadoraTableModel(new ArrayList<FinalizadoraRecord>());
    private FinalizadoraTableModel chequeModel = new FinalizadoraTableModel(new ArrayList<FinalizadoraRecord>());
    private void carregarFinalizadora() throws Exception {
        this.rotativoModel = new FinalizadoraTableModel(this.dao.getFinalizadora());
        for (FinalizadoraRecord f: this.rotativoModel.getItens()) {
            f.selected = rotativoSelecionado.contains(f.id);
        }
        this.rotativoModel.addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                FinalizadoraRecord item = rotativoModel.getItens().get(e.getLastRow());
                if (item.selected) {
                    rotativoSelecionado.add(item.id);
                } else {
                    rotativoSelecionado.remove(item.id);
                }
            }
        });
        tblRotativo.setModel(this.rotativoModel);
        this.chequeModel = new FinalizadoraTableModel(this.dao.getFinalizadora());
        for (FinalizadoraRecord f: this.chequeModel.getItens()) {
            f.selected = chequeSelecionado.contains(f.id);
        }
        this.chequeModel.addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                FinalizadoraRecord item = chequeModel.getItens().get(e.getLastRow());
                if (item.selected) {
                    chequeSelecionado.add(item.id);
                } else {
                    chequeSelecionado.remove(item.id);
                }
            }
        });
        tblCheque.setModel(this.chequeModel);
    }
    
    private List<String> getFinalizadorasRotativo() {
        List<String> result = new ArrayList<>();
        for (FinalizadoraRecord f: this.rotativoModel.getItens()) {
            if (f.selected) {
                result.add(f.id);
            }
        }
        return result;
    }
    
    private List<String> getFinalizadorasCheque() {
        List<String> result = new ArrayList<>();
        for (FinalizadoraRecord f: this.chequeModel.getItens()) {
            if (f.selected) {
                result.add(f.id);
            }
        }
        return result;
    }
    
    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new SysPdvGUI(i_mdiFrame);
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
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    //idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    //idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj; 
                    
                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    //dao.setComplementoSistema(txtComplNomeSistema.getText());
                    dao.setFinalizadorasRotativo(rotativoSelecionado);
                    dao.setFinalizadorasCheque(chequeSelecionado);
                    dao.setGerarEanAtacado(chkGerarEANAtacado.isSelected());
                    
                    if (tabs.getSelectedIndex() == 0) {                        
                        dao.setSoAtivos(chkSoAtivos.isSelected());
                        dao.setDtOfertas(txtDtTerminoOferta.getDate());
                        dao.setIgnorarEnviaBalanca(chkIgnorarEnviaBalanca.isSelected());
                        dao.setUsarOfertasDoEncarte(chkOfertasEncarte.isSelected());
                        tabProdutos.setImportador(importador);
                        tabProdutos.executarImportacao();
                        
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }                        
                        List<OpcaoFornecedor> opcoes = new ArrayList<>();
                        if (chkFContatos.isSelected()) {
                            opcoes.add(OpcaoFornecedor.CONTATOS);                        
                        }              
                        if (chkFCnpj.isSelected()) {
                            opcoes.add(OpcaoFornecedor.CNPJ_CPF);
                        }
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
                        }
                        if (chkPdvVendas.isSelected()) {
                            dao.setDataInicioVenda(edtDtVendaIni.getDate());
                            dao.setDataTerminoVenda(edtDtVendaFim.getDate());
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    } else if (tabs.getSelectedIndex() == 1) {
                        if (chkUnifProdutos.isSelected()) {
                            importador.unificarProdutos();
                        }
                        if (chkUnifFornecedor.isSelected()) {
                            importador.unificarFornecedor();
                        }
                        if (chkUnifProdutoFornecedor.isSelected()) {
                            importador.unificarProdutoFornecedor();
                        }                        
                        if (chkUnifClientePreferencial.isSelected()) {
                            importador.unificarClientePreferencial();
                        }                        
                        if (chkClienteEventual.isSelected()) {
                            importador.unificarClienteEventual();
                        }
                    }
                                       
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

        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRTabbedPane2 = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabParametrosGerais = new vrframework.bean.panel.VRPanel();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        chkOfertasEncarte = new vrframework.bean.checkBox.VRCheckBox();
        txtDtTerminoOferta = new org.jdesktop.swingx.JXDatePicker();
        chkSoAtivos = new vrframework.bean.checkBox.VRCheckBox();
        chkGerarEANAtacado = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        chkIgnorarEnviaBalanca = new vrframework.bean.checkBox.VRCheckBox();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabImpFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFCnpj = new vrframework.bean.checkBox.VRCheckBox();
        vRTabbedPane1 = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabCreditoRotativo = new vrframework.bean.panel.VRPanel();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        scrollRotativo = new javax.swing.JScrollPane();
        tblRotativo = new vrframework.bean.table.VRTable();
        tabCheque = new vrframework.bean.panel.VRPanel();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        scrollCheque = new javax.swing.JScrollPane();
        tblCheque = new vrframework.bean.table.VRTable();
        tabVenda = new vrframework.bean.panel.VRPanel();
        pnlDadosDataVenda = new vrframework.bean.panel.VRPanel();
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
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        try {
            configuracaoBaseDados1 = new vrimplantacao2_5.gui.componente.conexao.configuracao.ConfiguracaoBaseDados();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("Importação SQL Server");
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

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(463, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabParametrosGerais.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        vRPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos"));

        chkOfertasEncarte.setText("<html>\nBuscar ofertas na<br> \ntabela encarte\n</html>");

        chkSoAtivos.setText("Só ativos");

        chkGerarEANAtacado.setText("Gerar EAN Para atacado");

        vRLabel8.setText("Data oferta");

        chkIgnorarEnviaBalanca.setText("Ignorar flag \"Enviar p/ Balança\"");
        chkIgnorarEnviaBalanca.setToolTipText("Ao ignorar essa flag, só é considerado o parâmetro \"Fracionado\"");

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(chkOfertasEncarte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDtTerminoOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel5Layout.createSequentialGroup()
                                .addComponent(chkSoAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkIgnorarEnviaBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkGerarEANAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(239, Short.MAX_VALUE))
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(vRPanel5Layout.createSequentialGroup()
                        .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDtTerminoOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkOfertasEncarte))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSoAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIgnorarEnviaBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkGerarEANAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabParametrosGeraisLayout = new javax.swing.GroupLayout(tabParametrosGerais);
        tabParametrosGerais.setLayout(tabParametrosGeraisLayout);
        tabParametrosGeraisLayout.setHorizontalGroup(
            tabParametrosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabParametrosGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabParametrosGeraisLayout.setVerticalGroup(
            tabParametrosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabParametrosGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        vRTabbedPane2.addTab("Parâmetros", tabParametrosGerais);
        vRTabbedPane2.addTab("Produtos", tabProdutos);

        tabImpFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.setEnabled(true);
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");
        chkProdutoFornecedor.setEnabled(true);
        chkProdutoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoFornecedorActionPerformed(evt);
            }
        });

        chkFContatos.setText("Contatos");
        chkFContatos.setEnabled(true);
        chkFContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFContatosActionPerformed(evt);
            }
        });

        chkFCnpj.setText("CNPJ/CPF");
        chkFCnpj.setEnabled(true);
        chkFCnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCnpjActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabImpFornecedorLayout = new javax.swing.GroupLayout(tabImpFornecedor);
        tabImpFornecedor.setLayout(tabImpFornecedorLayout);
        tabImpFornecedorLayout.setHorizontalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        tabImpFornecedorLayout.setVerticalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(97, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Fornecedores", tabImpFornecedor);

        tabClienteDados.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkClientePreferencial.setText("Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);
        chkClientePreferencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferencialActionPerformed(evt);
            }
        });

        chkClienteEventual.setText("Cliente Eventual");
        chkClienteEventual.setEnabled(true);
        chkClienteEventual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClienteEventualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(418, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 103, Short.MAX_VALUE))
        );

        vRTabbedPane1.addTab("Clientes", tabClienteDados);

        chkCreditoRotativo.setText("Crédito Rotativo");
        chkCreditoRotativo.setEnabled(true);
        chkCreditoRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCreditoRotativoActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout tabCreditoRotativoLayout = new javax.swing.GroupLayout(tabCreditoRotativo);
        tabCreditoRotativo.setLayout(tabCreditoRotativoLayout);
        tabCreditoRotativoLayout.setHorizontalGroup(
            tabCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCreditoRotativoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addGap(13, 13, 13))
        );
        tabCreditoRotativoLayout.setVerticalGroup(
            tabCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCreditoRotativoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabCreditoRotativoLayout.createSequentialGroup()
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 127, Short.MAX_VALUE))
                    .addComponent(scrollRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        vRTabbedPane1.addTab("Crédito Rotativo", tabCreditoRotativo);

        chkCheque.setText("Cheques");
        chkCheque.setEnabled(true);
        chkCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequeActionPerformed(evt);
            }
        });

        tblCheque.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollCheque.setViewportView(tblCheque);

        javax.swing.GroupLayout tabChequeLayout = new javax.swing.GroupLayout(tabCheque);
        tabCheque.setLayout(tabChequeLayout);
        tabChequeLayout.setHorizontalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChequeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollCheque)
                .addContainerGap())
        );
        tabChequeLayout.setVerticalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChequeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(tabChequeLayout.createSequentialGroup()
                        .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 127, Short.MAX_VALUE)))
                .addContainerGap())
        );

        vRTabbedPane1.addTab("Cheque", tabCheque);

        vRTabbedPane2.addTab("Clientes", vRTabbedPane1);

        pnlDadosDataVenda.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Vendas (PDV)"));

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

        javax.swing.GroupLayout pnlDadosDataVendaLayout = new javax.swing.GroupLayout(pnlDadosDataVenda);
        pnlDadosDataVenda.setLayout(pnlDadosDataVendaLayout);
        pnlDadosDataVendaLayout.setHorizontalGroup(
            pnlDadosDataVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDadosDataVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDadosDataVendaLayout.setVerticalGroup(
            pnlDadosDataVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosDataVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosDataVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabVendaLayout = new javax.swing.GroupLayout(tabVenda);
        tabVenda.setLayout(tabVendaLayout);
        tabVendaLayout.setHorizontalGroup(
            tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDadosDataVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );
        tabVendaLayout.setVerticalGroup(
            tabVendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDadosDataVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Venda", tabVenda);

        tabs.addTab("Importação", vRTabbedPane2);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifFornecedor.setText("Fornecedor (Somente com CPF/CNPJ)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifClientePreferencial.setText("Cliente Preferencial (Somente com CPF/CNPJ)");

        chkUnifClienteEventual.setText("Cliente Eventual (Somente com CPF/CNPJ)");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);

        vRImportaArquivBalancaPanel1.setSistema("SysPdv");

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
            .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vRPanel4Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(28, 28, 28)))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 211, Short.MAX_VALUE)
            .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vRPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        tabs.addTab("Balança", vRPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(configuracaoBaseDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configuracaoBaseDados1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("");

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

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void chkProdutoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoFornecedorActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClienteEventualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClienteEventualActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void chkFCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCnpjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFCnpjActionPerformed

    private void chkCreditoRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCreditoRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCreditoRotativoActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

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

    }//GEN-LAST:event_chkPdvVendasActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkFCnpj;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkGerarEANAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkIgnorarEnviaBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkOfertasEncarte;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkSoAtivos;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.ConfiguracaoBaseDados configuracaoBaseDados1;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private vrframework.bean.panel.VRPanel pnlDadosDataVenda;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private javax.swing.JScrollPane scrollCheque;
    private javax.swing.JScrollPane scrollRotativo;
    private vrframework.bean.panel.VRPanel tabCheque;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.panel.VRPanel tabCreditoRotativo;
    private vrframework.bean.panel.VRPanel tabImpFornecedor;
    private vrframework.bean.panel.VRPanel tabParametrosGerais;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabVenda;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.table.VRTable tblCheque;
    private vrframework.bean.table.VRTable tblRotativo;
    private org.jdesktop.swingx.JXDatePicker txtDtTerminoOferta;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane1;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    // End of variables declaration//GEN-END:variables
    
}



class FinalizadoraTableModel extends AbstractTableModel {

    private final List<FinalizadoraRecord> itens;

    public List<FinalizadoraRecord> getItens() {
        return itens;
    }

    public FinalizadoraTableModel(List<FinalizadoraRecord> itens) {
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
        FinalizadoraRecord f = this.itens.get(rowIndex);
        switch (columnIndex) {
            case 0: return f.selected;
            case 1: return f.id;
            case 2: return f.descricao;
            default: return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            FinalizadoraRecord item = this.itens.get(rowIndex);
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
            case 0: return Boolean.class;
            default: return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "-";
            case 1: return "Código";
            case 2: return "Descrição";
            default: return null;
        }
    }
    
}