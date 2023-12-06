package vrimplantacao2_5.gui.sistema;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.panel.VRPanel;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.VRToVRDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author Guilherme
 */
public class VRToVR2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.VRMASTER.getNome();

    private static VRToVR2_5GUI instance;
    private final VRToVRDAO dao;

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new VRToVR2_5GUI(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    /**
     * Creates new form RPInfoGUI
     *
     * @param frame
     * @throws java.lang.Exception
     */
    public VRToVR2_5GUI(VRMdiFrame frame) throws Exception {
        super(frame);
        this.dao = new VRToVRDAO();
        initComponents();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                instance = null;
            }
        });

        tabProdutos.btnMapaTribut.setProvider(new MapaTributacaoButtonProvider() {
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

        tabProdutos.setOpcoesDisponiveis(dao);

        carregarParametros();

        pnlConn.setSistema(ESistema.VRMASTER);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();

        tabProdutos.carregarParametros(params, SISTEMA);
        chkCvTransacaoBaixados.setSelected(params.getBool(SISTEMA, "CV_BAIXADOS"));
        chkCreditoRotativoBaixados.setSelected(params.getBool(SISTEMA, "CR_BAIXADOS"));
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();

        tabProdutos.gravarParametros(params, SISTEMA);
        params.put(pnlConn.getComplemento(), SISTEMA, "COMPLEMENTO");
        params.put(chkCvTransacaoBaixados.isSelected(), SISTEMA, "CV_BAIXADOS");
        params.put(chkCreditoRotativoBaixados.isSelected(), SISTEMA, "CR_BAIXADOS");

        pnlConn.atualizarParametros();

        params.salvar();
    }

    private void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR;
            String idLojaCliente;

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = pnlConn.getLojaVR();
                    idLojaCliente = pnlConn.getLojaOrigem();

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    importador.setIdConexao(pnlConn.idConexao);

                    dao.eanAtacado = chkEANAtacado.isSelected();
                    dao.apenasAtivo = chkSomenteAtivo.isSelected();
                    dao.setComplemento(pnlConn.getComplemento());
                    dao.setPrecoVendaSemOferta(chkPrecoVendaSemOferta.isSelected());

                    if (tabs.getSelectedIndex() == 0) {

                        tabProdutos.setImportador(importador);
                        tabProdutos.executarImportacao();

                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }
                        {
                            List<OpcaoFornecedor> opcoes = new ArrayList<>();
                            if (chkFContatos.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                            if (chkFCnpj.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CNPJ_CPF);
                            }
                            if (chkFTipoEmpresa.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                            }
                            if (chkFEmail.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                            if (chkFTipoFornecedor.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_FORNECEDOR);
                            }
                            if (chkRazaoSocial.isSelected()) {
                                opcoes.add(OpcaoFornecedor.RAZAO_SOCIAL);
                            }
                            if (chkNomeFantasia.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NOME_FANTASIA);
                            }
                            if (chkFPrazo.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONDICAO_PAGAMENTO);
                                opcoes.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        }
                        if (chkFContasAPagar.isSelected()) {
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }
                        if (chkPessoaImpM5_0.isSelected()){
                            importador.importarPessoaImp();
                        }
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }
                        dao.importarRotativoBaixados = chkCreditoRotativoBaixados.isSelected();
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
                        }
                        if (chkPdvVendas.isSelected()) {
                            dao.setDataInicioVenda(edtDtVendaIni.getDate());
                            dao.setDataTerminoVenda(edtDtVendaFim.getDate());
                            OpcaoVenda[] opt = new OpcaoVenda[10];
                            opt[0] = OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR;
                            if (chkPdvVendasCustos.isSelected()) {
                                opt[1] = OpcaoVenda.ATUALIZAR_CUSTOS;
                            }
                            importador.importarVendas(opt);
                        }
                        if (chkLimiteCredito.isSelected()) {
                            importador.atualizarClientePreferencial(OpcaoCliente.VALOR_LIMITE);
                        }
                        if (chkCDataNascimento.isSelected()) {
                            importador.atualizarClientePreferencial(OpcaoCliente.DATA_NASCIMENTO);
                        }
                        if (chkBloqueado.isSelected()) {
                            importador.atualizarClientePreferencial(OpcaoCliente.BLOQUEADO);
                        }
                        if (chkCvEmpresa.isSelected()) {
                            importador.importarConvenioEmpresa();
                        }
                        if (chkCvConveniado.isSelected()) {
                            importador.importarConvenioConveniado();
                        }
                        dao.importarConveniosBaixados = chkCvTransacaoBaixados.isSelected();
                        if (chkCvTransacao.isSelected()) {
                            importador.importarConvenioTransacao();
                        }
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabParametros = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabImpFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFCnpj = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkFEmail = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkRazaoSocial = new vrframework.bean.checkBox.VRCheckBox();
        chkNomeFantasia = new vrframework.bean.checkBox.VRCheckBox();
        chkFPrazo = new vrframework.bean.checkBox.VRCheckBox();
        chkFContasAPagar = new vrframework.bean.checkBox.VRCheckBox();
        tabClientes = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativoBaixados = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        chkLimiteCredito = new vrframework.bean.checkBox.VRCheckBox();
        chkCDataNascimento = new vrframework.bean.checkBox.VRCheckBox();
        chkBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        chkCvEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCvConveniado = new vrframework.bean.checkBox.VRCheckBox();
        chkCvTransacao = new vrframework.bean.checkBox.VRCheckBox();
        chkCvTransacaoBaixados = new vrframework.bean.checkBox.VRCheckBox();
        chkPessoaImpM5_0 = new javax.swing.JCheckBox();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        pnlPdvVendaDatas = new vrframework.bean.panel.VRPanel();
        edtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        edtDtVendaFim = new org.jdesktop.swingx.JXDatePicker();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        chkPdvVendasCustos = new vrframework.bean.checkBox.VRCheckBox();
        jPanel1 = new javax.swing.JPanel();
        chkEANAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkSomenteAtivo = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoVendaSemOferta = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jBLimpar = new javax.swing.JButton();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Importação VR para VR");

        tabParametros.addTab("Produtos", tabProdutos);

        tabImpFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(chkFornecedor, "Fornecedor");
        chkFornecedor.setEnabled(true);
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkProdutoFornecedor, "Produto Fornecedor");
        chkProdutoFornecedor.setEnabled(true);
        chkProdutoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoFornecedorActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkFContatos, "Contatos");
        chkFContatos.setEnabled(true);
        chkFContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFContatosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkFCnpj, "CNPJ/CPF");
        chkFCnpj.setEnabled(true);
        chkFCnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCnpjActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkFTipoEmpresa, "Tipo Empresa");
        chkFTipoEmpresa.setEnabled(true);
        chkFTipoEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFTipoEmpresaActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkFEmail, "E-mail");

        org.openide.awt.Mnemonics.setLocalizedText(chkFTipoFornecedor, "Tipo Fornecedor");

        org.openide.awt.Mnemonics.setLocalizedText(chkRazaoSocial, "Razão Social");

        org.openide.awt.Mnemonics.setLocalizedText(chkNomeFantasia, "Nome Fantasia");

        org.openide.awt.Mnemonics.setLocalizedText(chkFPrazo, "Prazos");

        org.openide.awt.Mnemonics.setLocalizedText(chkFContasAPagar, "Contas a pagar em aberto");

        javax.swing.GroupLayout tabImpFornecedorLayout = new javax.swing.GroupLayout(tabImpFornecedor);
        tabImpFornecedor.setLayout(tabImpFornecedorLayout);
        tabImpFornecedorLayout.setHorizontalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFTipoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFPrazo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57))
        );
        tabImpFornecedorLayout.setVerticalGroup(
            tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addGroup(tabImpFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFTipoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabImpFornecedorLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(chkRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFPrazo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        tabParametros.addTab("Fornecedores", tabImpFornecedor);

        tabClienteDados.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(chkClientePreferencial, "Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);
        chkClientePreferencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferencialActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkClienteEventual, "Cliente Eventual");
        chkClienteEventual.setEnabled(true);
        chkClienteEventual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClienteEventualActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCreditoRotativo, "Crédito Rotativo");
        chkCreditoRotativo.setEnabled(true);
        chkCreditoRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCreditoRotativoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCreditoRotativoBaixados, "Importar Baixados (Rotativo)");
        chkCreditoRotativoBaixados.setEnabled(true);
        chkCreditoRotativoBaixados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCreditoRotativoBaixadosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCheque, "Cheques");
        chkCheque.setEnabled(true);
        chkCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkLimiteCredito, "Limite Crédito");
        chkLimiteCredito.setEnabled(true);
        chkLimiteCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLimiteCreditoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCDataNascimento, "Data Nascimento");

        org.openide.awt.Mnemonics.setLocalizedText(chkBloqueado, "Bloqueado");

        org.openide.awt.Mnemonics.setLocalizedText(chkCvEmpresa, "Empresas (Convênio)");
        chkCvEmpresa.setEnabled(true);
        chkCvEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvEmpresaActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCvConveniado, "Conveniados (Convênio)");
        chkCvConveniado.setEnabled(true);
        chkCvConveniado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvConveniadoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCvTransacao, "Transação (Convênio)");
        chkCvTransacao.setEnabled(true);
        chkCvTransacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvTransacaoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkCvTransacaoBaixados, "Importar Baixados (Convênio)");
        chkCvTransacaoBaixados.setEnabled(true);
        chkCvTransacaoBaixados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCvTransacaoBaixadosActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkPessoaImpM5_0, "Pessoa M5.0");

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkCvTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCvTransacaoBaixados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                                .addComponent(chkCDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPessoaImpM5_0)))
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCreditoRotativoBaixados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkLimiteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPessoaImpM5_0))
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativoBaixados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkCvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCvTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCvTransacaoBaixados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE))
        );

        tabClientes.addTab("Descrição", tabClienteDados);

        tabParametros.addTab("Clientes", tabClientes);

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

        org.openide.awt.Mnemonics.setLocalizedText(chkPdvVendasCustos, "Atualizar custos da vendas");
        chkPdvVendasCustos.setEnabled(true);
        chkPdvVendasCustos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPdvVendasCustosActionPerformed(evt);
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
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPdvVendasCustos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPdvVendasCustos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        tabParametros.addTab("Vendas", vRPanel1);

        tabs.addTab("Importação", tabParametros);

        org.openide.awt.Mnemonics.setLocalizedText(chkEANAtacado, "Importar EAN Atacado");

        org.openide.awt.Mnemonics.setLocalizedText(chkSomenteAtivo, "Somente Produto Ativo");

        org.openide.awt.Mnemonics.setLocalizedText(chkPrecoVendaSemOferta, "Preço venda (Sem Oferta)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrecoVendaSemOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSomenteAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEANAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(381, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkEANAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkSomenteAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrecoVendaSemOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
        );

        tabs.addTab("Especifico", jPanel1);

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

        jBLimpar.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jBLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBLimpar, "Limpar");
        jBLimpar.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBLimpar)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 573, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 254, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void chkProdutoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoFornecedorActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void chkFCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCnpjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFCnpjActionPerformed

    private void chkFTipoEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFTipoEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFTipoEmpresaActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClienteEventualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClienteEventualActionPerformed

    private void chkCreditoRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCreditoRotativoActionPerformed

    }//GEN-LAST:event_chkCreditoRotativoActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            importarTabelas();
            gravarParametros();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void chkLimiteCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLimiteCreditoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkLimiteCreditoActionPerformed

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

    private void chkCvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvEmpresaActionPerformed

    private void chkCvConveniadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvConveniadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvConveniadoActionPerformed

    private void chkCvTransacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvTransacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvTransacaoActionPerformed

    private void chkCvTransacaoBaixadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCvTransacaoBaixadosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCvTransacaoBaixadosActionPerformed

    private void chkCreditoRotativoBaixadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCreditoRotativoBaixadosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCreditoRotativoBaixadosActionPerformed

    private void chkPdvVendasCustosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasCustosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPdvVendasCustosActionPerformed

    private void jBLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimparActionPerformed
        tabProdutos.limparProduto();
        for (Component p : tabImpFornecedor.getComponents()) {
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
        for (Component p : vRPanel1.getComponents()) {
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
        for (Component p : tabClienteDados.getComponents()) {
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
    private vrframework.bean.checkBox.VRCheckBox chkBloqueado;
    private vrframework.bean.checkBox.VRCheckBox chkCDataNascimento;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativoBaixados;
    private vrframework.bean.checkBox.VRCheckBox chkCvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkCvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCvTransacao;
    private vrframework.bean.checkBox.VRCheckBox chkCvTransacaoBaixados;
    private vrframework.bean.checkBox.VRCheckBox chkEANAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkFCnpj;
    private vrframework.bean.checkBox.VRCheckBox chkFContasAPagar;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFEmail;
    private vrframework.bean.checkBox.VRCheckBox chkFPrazo;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkLimiteCredito;
    private vrframework.bean.checkBox.VRCheckBox chkNomeFantasia;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendasCustos;
    private javax.swing.JCheckBox chkPessoaImpM5_0;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoVendaSemOferta;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkRazaoSocial;
    private vrframework.bean.checkBox.VRCheckBox chkSomenteAtivo;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JButton jBLimpar;
    private javax.swing.JPanel jPanel1;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private vrframework.bean.tabbedPane.VRTabbedPane tabClientes;
    private vrframework.bean.panel.VRPanel tabImpFornecedor;
    private vrframework.bean.tabbedPane.VRTabbedPane tabParametros;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    // End of variables declaration//GEN-END:variables
}
