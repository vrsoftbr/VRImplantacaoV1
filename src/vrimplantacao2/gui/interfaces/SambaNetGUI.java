package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.planilhas.SambaNetDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class SambaNetGUI extends VRInternalFrame {

    private static final String SISTEMA = "SAMBANET";
    private static final String SERVIDOR_SQL = "Planilha";
    private static SambaNetGUI instance;

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        txtLoja.setText(params.getWithNull("1", SISTEMA, "COMPLEMENTO_SISTEMA"));
        txtPlanilhaFamilia.setArquivo(params.get(SISTEMA, "PLAN_FAMILIA_PRODUTO"));
        txtPlanilhaProdutos.setArquivo(params.get(SISTEMA, "PRODUTOS"));
        txtPlanilhaProdutosContador.setArquivo(params.get(SISTEMA, "PRODUTOS_CONTADOR"));
        txtPlanilhaFornecedor.setArquivo(params.get(SISTEMA, "FORNECEDOR"));
        txtPlanilhaClientes.setArquivo(params.get(SISTEMA, "CLIENTES"));
        txtPlanilhaBalanca.setArquivo(params.get(SISTEMA, "BALANCA"));
        chkInativacao.setSelected(params.getBool(SISTEMA, "INATIVACAO"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(txtLoja.getText(), SISTEMA, "COMPLEMENTO_SISTEMA");
        params.put(txtPlanilhaFamilia.getArquivo(), SISTEMA, "PLAN_FAMILIA_PRODUTO");
        params.put(txtPlanilhaProdutos.getArquivo(), SISTEMA, "PRODUTOS");
        params.put(txtPlanilhaProdutosContador.getArquivo(), SISTEMA, "PRODUTOS_CONTADOR");
        params.put(txtPlanilhaFornecedor.getArquivo(), SISTEMA, "FORNECEDOR");
        params.put(txtPlanilhaClientes.getArquivo(), SISTEMA, "CLIENTES");
        params.put(txtPlanilhaBalanca.getArquivo(), SISTEMA, "BALANCA");
        params.put(chkInativacao.isSelected(), SISTEMA, "INATIVACAO");
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private SambaNetDAO dao = new SambaNetDAO();

    private SambaNetGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;
        
        tabProdutos.setOpcoesDisponiveis(dao);
        tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                dao.setPlanilhaProdutosContator(txtPlanilhaProdutosContador.getArquivo());
                if (!new File(txtPlanilhaProdutosContador.getArquivo()).exists()) {
                    throw new RuntimeException("Arquivo RelProdutosListagemContadorXtra.xls não informado!");
                }
                return dao;
            }

            @Override
            public String getSistema() {
                return dao.getSistema();
            }

            @Override
            public String getLoja() {
                dao.setLojaOrigem(txtLoja.getText());
                return dao.getLojaOrigem();
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });
        
        carregarParametros();
        
        dao.setLojaOrigem(txtLoja.getText());
        
        pnlBalanca.setSistema(dao.getSistema());
        pnlBalanca.setLoja(dao.getLojaOrigem());

        centralizarForm();
        this.setMaximum(false);

        carregarLojaVR();
    }

    public void validarDadosAcesso() throws Exception {
        gravarParametros();
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
                instance = new SambaNetGUI(i_mdiFrame);
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
                    importador.importarPorPlanilha = true;
                    importador.setLojaOrigem(txtLoja.getText());
                    importador.setLojaVR(idLojaVR);
                    tabProdutos.setImportador(importador);
                    dao.setInativacao(chkInativacao.isSelected());

                    if (tabsProduto.getSelectedIndex() == 1) {
                        StringBuilder erros = new StringBuilder();
                        if (tabProdutos.chkFamilia.isSelected()) {
                            if (txtPlanilhaFamilia.getArquivo().equals("")) {
                                erros.append("Família Produto - Informe o arquivo RelFamiliaPrecoXtra.xls").append("\n");
                            }
                        }
                        if (tabProdutos.chkMercadologico.isSelected()) {
                            if (txtPlanilhaProdutos.getArquivo().equals("")) {
                                erros.append("Mercadológico - Informe o arquivo RelProdutosXtra.xls").append("\n");
                            }
                        }
                        if (tabProdutos.chkProdutos.isSelected()) {
                            if (
                                    txtPlanilhaProdutos.getArquivo().equals("")||
                                    txtPlanilhaProdutosContador.getArquivo().equals("")||
                                    txtPlanilhaBalanca.getArquivo().equals("")/*||
                                    txtPlanilhaFamilia.getArquivo().equals("")*/) {
                                erros.append("Produtos - Informe o arquivo RelProdutosXtra.xls, RelFamiliaPrecoXtra.xls e o RelProdutosListagemContadorXtra.xls").append("\n");
                            }
                        }
                        if (erros.toString().equals("")) {
                            dao.setPlanilhaFamiliaProduto(txtPlanilhaFamilia.getArquivo());
                            dao.setPlanilhaProdutos(txtPlanilhaProdutos.getArquivo());
                            dao.setPlanilhaProdutosContator(txtPlanilhaProdutosContador.getArquivo());
                            dao.setPlanilhaBalanca(txtPlanilhaBalanca.getArquivo());
                            tabProdutos.executarImportacao();
                        } else {
                            Util.exibirMensagem("Atenção", erros.toString());
                        }
                    } else if (tabsProduto.getSelectedIndex() == 2) {
                        if (chkInativacao.isSelected()) {
                            if (txtPlanilhaFornecedor.getArquivo().equals("")) {
                                Util.exibirMensagem("Atenção", "Fornecedores - Informe o arquivo RelFornecedoresXtra.xls");
                            } else {
                                dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                importador.atualizarFornecedor(OpcaoFornecedor.SITUACAO_CADASTRO);
                            }
                        } else {
                            if (chkFornecedor.isSelected()) {
                                if (txtPlanilhaFornecedor.getArquivo().equals("")) {
                                    Util.exibirMensagem("Atenção", "Fornecedores - Informe o arquivo RelFornecedoresXtra.xls");
                                } else {
                                    dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                    importador.importarFornecedor(OpcaoFornecedor.DADOS);
                                }
                            }
                            if (chkContatoFornecedor.isSelected()) {
                                if (txtPlanilhaFornecedor.getArquivo().equals("")) {
                                    Util.exibirMensagem("Atenção", "Fornecedores - Informe o arquivo RelFornecedoresXtra.xls");
                                } else {
                                    dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                    importador.importarFornecedor(OpcaoFornecedor.CONTATOS);
                                }
                            }
                            if (chkClientesPreferenciais.isSelected()) {
                                if (txtPlanilhaClientes.getArquivo().equals("")) {
                                    Util.exibirMensagem("Atenção", "Clientes - Informe o arquivo RelClientesXtra.xls");
                                } else {
                                    dao.setPlanilhaClientes(txtPlanilhaClientes.getArquivo());
                                    importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                                }
                            }
                            if (chkClientesEventuais.isSelected()) {
                                if (txtPlanilhaClientes.getArquivo().equals("")) {
                                    Util.exibirMensagem("Atenção", "Clientes - Informe o arquivo RelClientesXtra.xls");
                                } else {
                                    dao.setPlanilhaClientes(txtPlanilhaClientes.getArquivo());
                                    importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                                }
                            }
                            
                            {
                                dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                List<OpcaoFornecedor> opt = new ArrayList<>();

                                if (chkEnderecoFornecedor.isSelected()) {
                                    opt.add(OpcaoFornecedor.ENDERECO);
                                }
                                if (chkNumeroFornecedor.isSelected()) {
                                    opt.add(OpcaoFornecedor.NUMERO);
                                }
                                if (chkTelefoneFornecedor.isSelected()) {
                                    opt.add(OpcaoFornecedor.TELEFONE);
                                }
                                if (chkComplemento.isSelected()) {
                                    opt.add(OpcaoFornecedor.COMPLEMENTO);
                                }

                                if (!opt.isEmpty()) {
                                    importador.atualizarFornecedor(opt.toArray(new OpcaoFornecedor[]{}));
                                }
                            }
                        }
                    } else if (tabsProduto.getSelectedIndex() == 3) {
                        if (chkUnifProdutos.isSelected()) {
                            if (
                                    txtPlanilhaProdutos.getArquivo().equals("")||
                                    txtPlanilhaProdutosContador.getArquivo().equals("")||
                                    txtPlanilhaFamilia.getArquivo().equals("")) {
                                Util.exibirMensagem("Atenção", "Produtos - Informe o arquivo RelProdutosXtra.xls, RelFamiliaPrecoXtra.xls e o RelProdutosListagemContadorXtra.xls");
                            } else {                                    
                                dao.setPlanilhaFamiliaProduto(txtPlanilhaFamilia.getArquivo());
                                dao.setPlanilhaProdutos(txtPlanilhaProdutos.getArquivo());
                                dao.setPlanilhaProdutosContator(txtPlanilhaProdutosContador.getArquivo());
                                dao.setPlanilhaBalanca(txtPlanilhaBalanca.getArquivo());
                                importador.unificarProdutos();
                            }
                        }
                        if (chkUnifFornecedores.isSelected()) {
                            if (
                                    txtPlanilhaFornecedor.getArquivo().equals("")) {
                                Util.exibirMensagem("Atenção", "Fornecedores - Informe o arquivo RelFornecedoresXtra.xls");
                            } else {
                                dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                importador.unificarFornecedor();
                            }
                        }
                        if (chkUnifClientesPreferenciais.isSelected()) {
                            if (
                                    txtPlanilhaClientes.getArquivo().equals("")) {
                                Util.exibirMensagem("Atenção", "Clientes - Informe o arquivo RelClientesXtra.xls");
                            } else {
                                dao.setPlanilhaClientes(txtPlanilhaClientes.getArquivo());
                                importador.unificarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                            }
                        }
                        if (chkUnifClientesEventuais.isSelected()) {
                            if (
                                    txtPlanilhaClientes.getArquivo().equals("")) {
                                Util.exibirMensagem("Atenção", "Clientes - Informe o arquivo RelClientesXtra.xls");
                            } else {
                                dao.setPlanilhaClientes(txtPlanilhaClientes.getArquivo());
                                importador.unificarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                            }
                        }
                    }                    
                    
                    gravarParametros();

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    ProgressBar.dispose();
                    ex.printStackTrace();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRLabel25 = new vrframework.bean.label.VRLabel();
        txtLoja = new javax.swing.JTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabsProduto = new vrframework.bean.tabbedPane.VRTabbedPane();
        scroll = new javax.swing.JScrollPane();
        tabPlanilhasProduto = new vrframework.bean.panel.VRPanel();
        vRLabel24 = new vrframework.bean.label.VRLabel();
        vRLabel26 = new vrframework.bean.label.VRLabel();
        txtPlanilhaFamilia = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel43 = new vrframework.bean.label.VRLabel();
        vRLabel44 = new vrframework.bean.label.VRLabel();
        txtPlanilhaBalanca = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel35 = new vrframework.bean.label.VRLabel();
        vRLabel36 = new vrframework.bean.label.VRLabel();
        txtPlanilhaProdutos = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel37 = new vrframework.bean.label.VRLabel();
        vRLabel38 = new vrframework.bean.label.VRLabel();
        txtPlanilhaProdutosContador = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel39 = new vrframework.bean.label.VRLabel();
        vRLabel40 = new vrframework.bean.label.VRLabel();
        txtPlanilhaFornecedor = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel41 = new vrframework.bean.label.VRLabel();
        vRLabel42 = new vrframework.bean.label.VRLabel();
        txtPlanilhaClientes = new vrframework.bean.fileChooser.VRFileChooser();
        chkInativacao = new vrframework.bean.checkBox.VRCheckBox();
        btnMigrar1 = new vrframework.bean.button.VRButton();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkContatoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkNumeroFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkEnderecoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkTelefoneFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkComplemento = new javax.swing.JCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkClientesPreferenciais = new vrframework.bean.checkBox.VRCheckBox();
        chkClientesEventuais = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientesPreferenciais = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientesEventuais = new vrframework.bean.checkBox.VRCheckBox();

        setTitle("Importação Liteci");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(683, 494));

        vRLabel25.setText("Loja de Origem");

        txtLoja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoja.setText("1");
        txtLoja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLojaKeyReleased(evt);
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

        vRLabel24.setText("Informe a localização da planilha RelFamiliaPrecoXtra.xls ");

        vRLabel26.setText("(localizada em Produtos->Famílias de Preço->Imprimir)");
        vRLabel26.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        vRLabel43.setText("Informe a localização da planilha RelProdutosBalancaXtra.xls");

        vRLabel44.setText("(localizada em Balança->Relatório Produtos Balança ->Imprimir)");
        vRLabel44.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        vRLabel35.setText("Informe a localização da planilha RelProdutosXtra.xls");

        vRLabel36.setText("(localizada em Produtos->Produtos->Imprimir)");
        vRLabel36.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        vRLabel37.setText("Informe a localização da planilha RelProdutosListagemContadorXtra.xls");

        vRLabel38.setText("(localizada em Produtos->Produtos->Imprimir->Marque listagem para contador)");
        vRLabel38.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        vRLabel39.setText("Informe a localização da planilha RelFornecedoresXtra.xls");

        vRLabel40.setText("(localizada em Fornecedores->Fornecedores->Imprimir)");
        vRLabel40.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        vRLabel41.setText("Informe a localização da planilha RelClientesXtra.xls");

        vRLabel42.setText("(localizada em Clientes->Clientes->Imprimir)");
        vRLabel42.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        chkInativacao.setText("Inativação");

        btnMigrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMigrar1.setText("Gravar parâmetros");
        btnMigrar1.setFocusable(false);
        btnMigrar1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrar1ActionPerformed(evt);
            }
        });

        pnlBalanca.setSistema("");

        javax.swing.GroupLayout tabPlanilhasProdutoLayout = new javax.swing.GroupLayout(tabPlanilhasProduto);
        tabPlanilhasProduto.setLayout(tabPlanilhasProdutoLayout);
        tabPlanilhasProdutoLayout.setHorizontalGroup(
            tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPlanilhaFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabPlanilhasProdutoLayout.createSequentialGroup()
                        .addComponent(chkInativacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMigrar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPlanilhaBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaProdutosContador, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                        .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabPlanilhasProdutoLayout.setVerticalGroup(
            tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaProdutosContador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(vRLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMigrar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkInativacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        scroll.setViewportView(tabPlanilhasProduto);

        tabsProduto.addTab("Planilhas", scroll);
        tabsProduto.addTab("Produtos", tabProdutos);

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedores"));

        chkFornecedor.setText("Fornecedores");

        chkContatoFornecedor.setText("Contato Fornecedor");

        chkProdutoFornecedor.setText("Produto Fornecedor");

        chkNumeroFornecedor.setText("Número");

        chkEnderecoFornecedor.setText("Endereço");

        chkTelefoneFornecedor.setText("Telefone");

        chkComplemento.setText("Complemento");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContatoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(chkEnderecoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkNumeroFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTelefoneFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkComplemento)))
                .addContainerGap(520, Short.MAX_VALUE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkContatoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNumeroFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEnderecoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTelefoneFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkComplemento)))
        );

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        chkClientesPreferenciais.setText("Preferenciais");

        chkClientesEventuais.setText("Eventuais");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(chkClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(chkClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(239, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Fornecedores e Clientes", tabFornecedor);

        chkUnifProdutos.setText("Produtos (Somente com EAN válido)");

        chkUnifProdutoFornecedor.setText("Produto Fornecedor (Somente com CPF/CNPJ)");

        chkUnifFornecedores.setText("Fornecedores (Somente com CPF/CNPJ)");

        chkUnifClientesPreferenciais.setText("Clientes Preferenciais (Somente com CPF/CNPJ)");

        chkUnifClientesEventuais.setText("Clientes Eventuais (Somente com CPF/CNPJ)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUnifClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(338, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientesPreferenciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUnifClientesEventuais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(247, Short.MAX_VALUE))
        );

        tabsProduto.addTab("Unificação", tabUnificacao);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabsProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

    private void btnMigrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrar1ActionPerformed
        try {
            gravarParametros();
            Util.exibirMensagem("Parâmetros gravados com sucesso!", "Mensagem");
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnMigrar1ActionPerformed

    private void txtLojaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLojaKeyReleased
        dao.setLojaOrigem(txtLoja.getText());
    }//GEN-LAST:event_txtLojaKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.button.VRButton btnMigrar1;
    private vrframework.bean.checkBox.VRCheckBox chkClientesEventuais;
    private vrframework.bean.checkBox.VRCheckBox chkClientesPreferenciais;
    private javax.swing.JCheckBox chkComplemento;
    private vrframework.bean.checkBox.VRCheckBox chkContatoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkEnderecoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkInativacao;
    private vrframework.bean.checkBox.VRCheckBox chkNumeroFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkTelefoneFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientesEventuais;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientesPreferenciais;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private javax.swing.JLabel jLabel1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private javax.swing.JScrollPane scroll;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrframework.bean.panel.VRPanel tabPlanilhasProduto;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsProduto;
    private javax.swing.JTextField txtLoja;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaBalanca;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaClientes;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaFamilia;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaFornecedor;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaProdutos;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaProdutosContador;
    private vrframework.bean.label.VRLabel vRLabel24;
    private vrframework.bean.label.VRLabel vRLabel25;
    private vrframework.bean.label.VRLabel vRLabel26;
    private vrframework.bean.label.VRLabel vRLabel35;
    private vrframework.bean.label.VRLabel vRLabel36;
    private vrframework.bean.label.VRLabel vRLabel37;
    private vrframework.bean.label.VRLabel vRLabel38;
    private vrframework.bean.label.VRLabel vRLabel39;
    private vrframework.bean.label.VRLabel vRLabel40;
    private vrframework.bean.label.VRLabel vRLabel41;
    private vrframework.bean.label.VRLabel vRLabel42;
    private vrframework.bean.label.VRLabel vRLabel43;
    private vrframework.bean.label.VRLabel vRLabel44;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    // End of variables declaration//GEN-END:variables

}
