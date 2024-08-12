package vrimplantacao2_5.gui.sistema;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.panel.VRPanel;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
//import vrimplantacao2.dao.interfaces.HipcomDAO;
import vrimplantacao2_5.dao.sistema.HipcomDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2_5.vo.enums.ESistema;

public class Hipcom2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = "Hipcom";
    private static Hipcom2_5GUI instance;

    public static String getSISTEMA() {
        return SISTEMA;
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        pnlBalanca.setSistema(SISTEMA);
        pnlBalanca.setLoja(dao.getLojaOrigem());
        chkVendaUtilizaDigito.setSelected(params.getBool(true, SISTEMA, "UTILIZA_DIGITO"));
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();

        tabProdutos.gravarParametros(params, SISTEMA);
        pnlConn.atualizarParametros();
        params.put(chkVendaUtilizaDigito.isSelected(), SISTEMA, "UTILIZA_DIGITO");

        params.salvar();
    }

    private HipcomDAO dao = new HipcomDAO();

    public Hipcom2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        tabProdutos.setOpcoesDisponiveis(dao);

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

        carregarParametros();

        rdbVendasV1.setSelected(true);

        pnlConn.setSistema(ESistema.HIPCOM);
        pnlConn.getNomeConexao();

        centralizarForm();
        this.setMaximum(false);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new Hipcom2_5GUI(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    public void importarTabelas() throws Exception {

        if (chkCreditoRotativo.isSelected()) {
            if (txtRotDtIni.getDate() == null || txtRotDtFim.getDate() == null) {
                Util.exibirMensagem("Verifique o intervalo de datas do crédito rotativo", "Atenção");
                return;
            }
        }

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

//                    dao.setVersaoVenda(rdbVendasV1.isSelected() ? 1 : 2);
                    Importador importador = new Importador(dao);

                    importador.setIdConexao(pnlConn.idConexao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    importador.eBancoUnificado = chkBancoUnificado.isSelected();
                    dao.setFornecedorCartao(chkFornCartao.isSelected());
                    dao.setInverteAssociado(chkInverteAssociado.isSelected());
                    dao.setPagarFornecedorBaixado(chkContasPagarBaixado.isSelected());
                    dao.setRotativoBaixado(chkCreditoRotativoBaixado.isSelected());

                    if (tabOperacoes.getSelectedIndex() == 0) {

                        tabProdutos.setImportador(importador);
                        tabProdutos.executarImportacao();

                        if (chkReceitaProduto.isSelected()) {
                            importador.importarReceitasProducao();
                        }

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
                            if (chkFTipoPagamento.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_PAGAMENTO);
                            }
                            if (chkFEndereco.isSelected()) {
                                opcoes.add(OpcaoFornecedor.ENDERECO);
                            }
                            if (chkFNumero.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NUMERO);
                            }
                            if (chkFTipoEmp.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                            }
                            if (chkFTipoForn.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_FORNECEDOR);
                            }
                            if (chkFRazaoSocial.isSelected()) {
                                opcoes.add(OpcaoFornecedor.RAZAO_SOCIAL);
                            }
                            if (chkFNomeFantasia.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NOME_FANTASIA);
                            }
                            if (chkFComplemento.isSelected()) {
                                opcoes.add(OpcaoFornecedor.COMPLEMENTO);
                            }

                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        }

                        if (chkOutrasReceitas.isSelected()) {
                            dao.setReceberDataInicial(txtOtRecDtIni.getDate());
                            dao.setReceberDataFinal(txtOtRecDtFim.getDate());
                            importador.importarOutrasReceitas(OpcaoContaReceber.NOVOS);
                        }

                        if (chkContasPagar.isSelected()) {
                            dao.setCpDataInicial(txtDtCPEntrada.getDate());
                            dao.setCpDataFinal(txtDtCPFim.getDate());
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }

                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }

                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }

                        {
                            List<OpcaoCliente> opcoes = new ArrayList<>();

                            if (chkClienteTipoInscricao.isSelected()) {
                                opcoes.add(OpcaoCliente.TIPO_INSCRICAO);
                            }
                            
                            if (chkClienteContatos.isSelected()){
                                opcoes.add(OpcaoCliente.CONTATOS);
                                opcoes.add(OpcaoCliente.CELULAR);
                                opcoes.add(OpcaoCliente.TELEFONE);
                            }

                            if (chkCIE.isSelected()) {
                                opcoes.add(OpcaoCliente.INSCRICAO_ESTADUAL);
                            }
                            if (chkConvenioEmpresa.isSelected()) {
                                importador.importarConvenioEmpresa();
                            }
                            if (chkConveniado.isSelected()) {
                                importador.importarConvenioConveniado();
                            }
                            if (chkTransacaoConvenio.isSelected()) {
                                importador.importarConvenioTransacao();
                            }

                            if (!opcoes.isEmpty()) {
                                importador.atualizarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                            }
                        }

                        if (chkCreditoRotativo.isSelected()) {
                            dao.setRotativoDataInicial(txtRotDtIni.getDate());
                            dao.setRotativoDataFinal(txtRotDtFim.getDate());
                            importador.importarCreditoRotativo();
                        }

                        if (chkCheque.isSelected()) {
                            dao.setRotativoDataInicial(txtRotDtIni.getDate());
                            dao.setRotativoDataFinal(txtRotDtFim.getDate());
                            importador.importarCheque();
                        }

                        if (chkVendas.isSelected()) {
                            dao.setDataInicioVenda(txtDtVendaIni.getDate());
                            dao.setDataTerminoVenda(txtDtVendaFim.getDate());
                            //dao.setVendaUtilizaDigito(chkVendaUtilizaDigito.isSelected());
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {
                        ConexaoMySQL.getConexao().close();
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

        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        tabOperacoes = new javax.swing.JTabbedPane();
        tabImportacao = new javax.swing.JTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        jPanel3 = new javax.swing.JPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkFEndereco = new vrframework.bean.checkBox.VRCheckBox();
        chkFNumero = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoEmp = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoForn = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFRazaoSocial = new vrframework.bean.checkBox.VRCheckBox();
        chkFNomeFantasia = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos1 = new vrframework.bean.checkBox.VRCheckBox();
        chkFComplemento = new vrframework.bean.checkBox.VRCheckBox();
        chkFornCartao = new vrframework.bean.checkBox.VRCheckBox();
        pnlOutrasReceitas = new javax.swing.JPanel();
        chkOutrasReceitas = new vrframework.bean.checkBox.VRCheckBox();
        txtOtRecDtIni = new org.jdesktop.swingx.JXDatePicker();
        txtOtRecDtFim = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tabClientes = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        jPanel1 = new javax.swing.JPanel();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        txtRotDtIni = new org.jdesktop.swingx.JXDatePicker();
        txtRotDtFim = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        chkClienteTipoInscricao = new vrframework.bean.checkBox.VRCheckBox();
        chkCIE = new javax.swing.JCheckBox();
        chkClienteEventual = new javax.swing.JCheckBox();
        chkConvenioEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkConveniado = new vrframework.bean.checkBox.VRCheckBox();
        chkTransacaoConvenio = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativoBaixado = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteContatos = new vrframework.bean.checkBox.VRCheckBox();
        tabOutras = new javax.swing.JPanel();
        chkContasPagar = new javax.swing.JCheckBox();
        txtDtCPEntrada = new org.jdesktop.swingx.JXDatePicker();
        txtDtCPFim = new org.jdesktop.swingx.JXDatePicker();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkVendas = new javax.swing.JCheckBox();
        chkVendaUtilizaDigito = new javax.swing.JCheckBox();
        txtDtVendaIni = new org.jdesktop.swingx.JXDatePicker();
        txtDtVendaFim = new org.jdesktop.swingx.JXDatePicker();
        rdbVendasV1 = new vrframework.bean.radioButton.VRRadioButton();
        rdbVendasV2 = new vrframework.bean.radioButton.VRRadioButton();
        chkBancoUnificado = new vrframework.bean.checkBox.VRCheckBox();
        chkInverteAssociado = new javax.swing.JCheckBox();
        chkReceitaProduto = new javax.swing.JCheckBox();
        chkContasPagarBaixado = new javax.swing.JCheckBox();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jBLimpar11 = new javax.swing.JButton();
        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }

        setTitle("Importação Hipcom");
        setToolTipText("");

        tabImportacao.addTab("Produtos", tabProdutos);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkFornecedor.setText("Fornecedor");
        jPanel3.add(chkFornecedor);

        chkFContatos.setText("Contatos");
        jPanel3.add(chkFContatos);

        chkFTipoPagamento.setText("Tipo Pagamento");
        jPanel3.add(chkFTipoPagamento);

        chkFEndereco.setText("Endereço");
        jPanel3.add(chkFEndereco);

        chkFNumero.setText("Número");
        jPanel3.add(chkFNumero);

        chkFTipoEmp.setText("Tipo Empresa");
        jPanel3.add(chkFTipoEmp);

        chkFTipoForn.setText("Tipo Fornecedor");
        jPanel3.add(chkFTipoForn);

        chkProdutoFornecedor.setText("Produto Fornecedor");
        jPanel3.add(chkProdutoFornecedor);

        chkFRazaoSocial.setText("Razão Social");
        jPanel3.add(chkFRazaoSocial);

        chkFNomeFantasia.setText("Nome Fantasia");
        jPanel3.add(chkFNomeFantasia);

        chkFContatos1.setText("Contatos");
        chkFContatos1.setEnabled(true);
        chkFContatos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFContatos1ActionPerformed(evt);
            }
        });
        jPanel3.add(chkFContatos1);

        chkFComplemento.setText("Complemento");
        chkFComplemento.setEnabled(true);
        chkFComplemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFComplementoActionPerformed(evt);
            }
        });
        jPanel3.add(chkFComplemento);

        chkFornCartao.setText("Fornecedor Cartão");
        chkFornCartao.setEnabled(true);
        chkFornCartao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornCartaoActionPerformed(evt);
            }
        });
        jPanel3.add(chkFornCartao);

        pnlOutrasReceitas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkOutrasReceitas.setText("Outras receitas");

        jLabel6.setText("Dt. Inicial");

        jLabel7.setText("Dt. Termino");

        javax.swing.GroupLayout pnlOutrasReceitasLayout = new javax.swing.GroupLayout(pnlOutrasReceitas);
        pnlOutrasReceitas.setLayout(pnlOutrasReceitasLayout);
        pnlOutrasReceitasLayout.setHorizontalGroup(
            pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutrasReceitasLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(chkOutrasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOutrasReceitasLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addGap(67, 67, 67))
                    .addComponent(txtOtRecDtIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOutrasReceitasLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addGap(56, 56, 56))
                    .addComponent(txtOtRecDtFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        pnlOutrasReceitasLayout.setVerticalGroup(
            pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutrasReceitasLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOutrasReceitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOtRecDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOutrasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOtRecDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlOutrasReceitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOutrasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabImportacao.addTab("Fornecedores", tabFornecedor);

        chkClientePreferencial.setText("Cliente Preferencial");

        chkCheque.setText("Cheque");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkCreditoRotativo.setText("Crédito Rotativo");

        jLabel2.setText("Dt. Inicial");

        jLabel3.setText("Dt. Termino");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                        .addGap(67, 67, 67))
                    .addComponent(txtRotDtIni, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addGap(56, 56, 56))
                    .addComponent(txtRotDtFim, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRotDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRotDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        chkClienteTipoInscricao.setText("Tipo Inscrição");

        chkCIE.setText("Inscrição Estadual");

        chkClienteEventual.setText("Cliente Eventual");

        chkConvenioEmpresa.setText("Convenio Empresa");

        chkConveniado.setText("Conveniado");

        chkTransacaoConvenio.setText("Transação Convenio");

        chkCreditoRotativoBaixado.setText("Crédito Rotativo Baixado");

        chkClienteContatos.setText("Contatos");

        javax.swing.GroupLayout tabClientesLayout = new javax.swing.GroupLayout(tabClientes);
        tabClientes.setLayout(tabClientesLayout);
        tabClientesLayout.setHorizontalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabClientesLayout.createSequentialGroup()
                                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCreditoRotativoBaixado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabClientesLayout.createSequentialGroup()
                                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkClienteEventual))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkCIE)
                                    .addGroup(tabClientesLayout.createSequentialGroup()
                                        .addComponent(chkClienteTipoInscricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(chkClienteContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(tabClientesLayout.createSequentialGroup()
                                .addComponent(chkConvenioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkTransacaoConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabClientesLayout.setVerticalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCIE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClienteTipoInscricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteEventual)
                    .addComponent(chkClienteContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativoBaixado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkConvenioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTransacaoConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Clientes", tabClientes);

        chkContasPagar.setText("Contas à Pagar");

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendas PDV"));

        chkVendas.setText("Vendas");

        chkVendaUtilizaDigito.setText("Venda utiliza digito nos prod.");
        chkVendaUtilizaDigito.setToolTipText("<html>\nExiste no Hipcom a opção de armazenar os produtos das vendas com digíto verificador.<br>\n<b>Marque esta opção se a loja a ser importada utilizar o digíto verificador.</b>\n</html>");

        rdbVendasV1.setSelected(true);
        rdbVendasV1.setText("v1.0");
        rdbVendasV1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbVendasV1tipoVendaSelect(evt);
            }
        });

        rdbVendasV2.setText("v2.0");
        rdbVendasV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbVendasV2tipoVendaSelect(evt);
            }
        });

        chkBancoUnificado.setText("Banco Hipcom é Unificado");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(chkVendas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkVendaUtilizaDigito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbVendasV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbVendasV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBancoUnificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkVendas)
                    .addComponent(txtDtVendaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtVendaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbVendasV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbVendasV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBancoUnificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVendaUtilizaDigito)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkInverteAssociado.setText("Inverter Associado");

        chkReceitaProduto.setText("Receita de Produção");

        chkContasPagarBaixado.setText("Contas à Pagar Baixado");

        javax.swing.GroupLayout tabOutrasLayout = new javax.swing.GroupLayout(tabOutras);
        tabOutras.setLayout(tabOutrasLayout);
        tabOutrasLayout.setHorizontalGroup(
            tabOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabOutrasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabOutrasLayout.createSequentialGroup()
                        .addGroup(tabOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabOutrasLayout.createSequentialGroup()
                                .addComponent(chkContasPagar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDtCPEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDtCPFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkContasPagarBaixado)
                            .addComponent(chkReceitaProduto)
                            .addComponent(chkInverteAssociado))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabOutrasLayout.setVerticalGroup(
            tabOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabOutrasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabOutrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkContasPagar)
                    .addComponent(txtDtCPEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtCPFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkContasPagarBaixado)
                .addGap(4, 4, 4)
                .addComponent(chkInverteAssociado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkReceitaProduto)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Outras", tabOutras);

        tabOperacoes.addTab("Importação", tabImportacao);
        tabOperacoes.addTab("Balança", pnlBalanca);

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

        jBLimpar11.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jBLimpar11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        jBLimpar11.setText("Limpar");
        jBLimpar11.setToolTipText("Limpa todos os itens selecionados");
        jBLimpar11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLimpar11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLojaLayout = new javax.swing.GroupLayout(pnlLoja);
        pnlLoja.setLayout(pnlLojaLayout);
        pnlLojaLayout.setHorizontalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLojaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBLimpar11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlLojaLayout.setVerticalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBLimpar11)
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
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabOperacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 612, Short.MAX_VALUE)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabOperacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Avance");

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

    private void rdbVendasV2tipoVendaSelect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbVendasV2tipoVendaSelect
        if (rdbVendasV2.isSelected()) {
            rdbVendasV1.setSelected(false);
        }

    }//GEN-LAST:event_rdbVendasV2tipoVendaSelect

    private void rdbVendasV1tipoVendaSelect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbVendasV1tipoVendaSelect

        if (rdbVendasV1.isSelected()) {
            rdbVendasV2.setSelected(false);
        }
    }//GEN-LAST:event_rdbVendasV1tipoVendaSelect

    private void chkFContatos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatos1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatos1ActionPerformed

    private void chkFComplementoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFComplementoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFComplementoActionPerformed

    private void chkFornCartaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornCartaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornCartaoActionPerformed

    private void jBLimpar11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLimpar11ActionPerformed
        tabProdutos.limparProduto();
        for (Component p : tabClientes.getComponents()) {
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
        }
        for (Component p : tabFornecedor.getComponents()) {
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
        }
        for (Component p : tabOutras.getComponents()) {
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
        }
    }//GEN-LAST:event_jBLimpar11ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkBancoUnificado;
    private javax.swing.JCheckBox chkCIE;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteContatos;
    private javax.swing.JCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkClienteTipoInscricao;
    private javax.swing.JCheckBox chkContasPagar;
    private javax.swing.JCheckBox chkContasPagarBaixado;
    private vrframework.bean.checkBox.VRCheckBox chkConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkConvenioEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativoBaixado;
    private vrframework.bean.checkBox.VRCheckBox chkFComplemento;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos1;
    private vrframework.bean.checkBox.VRCheckBox chkFEndereco;
    private vrframework.bean.checkBox.VRCheckBox chkFNomeFantasia;
    private vrframework.bean.checkBox.VRCheckBox chkFNumero;
    private vrframework.bean.checkBox.VRCheckBox chkFRazaoSocial;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoEmp;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoForn;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkFornCartao;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private javax.swing.JCheckBox chkInverteAssociado;
    private vrframework.bean.checkBox.VRCheckBox chkOutrasReceitas;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private javax.swing.JCheckBox chkReceitaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkTransacaoConvenio;
    private javax.swing.JCheckBox chkVendaUtilizaDigito;
    private javax.swing.JCheckBox chkVendas;
    private javax.swing.JButton jBLimpar11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private javax.swing.JPanel pnlOutrasReceitas;
    private vrframework.bean.radioButton.VRRadioButton rdbVendasV1;
    private vrframework.bean.radioButton.VRRadioButton rdbVendasV2;
    private vrframework.bean.panel.VRPanel tabClientes;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private javax.swing.JTabbedPane tabImportacao;
    private javax.swing.JTabbedPane tabOperacoes;
    private javax.swing.JPanel tabOutras;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private org.jdesktop.swingx.JXDatePicker txtDtCPEntrada;
    private org.jdesktop.swingx.JXDatePicker txtDtCPFim;
    private org.jdesktop.swingx.JXDatePicker txtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker txtDtVendaIni;
    private org.jdesktop.swingx.JXDatePicker txtOtRecDtFim;
    private org.jdesktop.swingx.JXDatePicker txtOtRecDtIni;
    private org.jdesktop.swingx.JXDatePicker txtRotDtFim;
    private org.jdesktop.swingx.JXDatePicker txtRotDtIni;
    private vrframework.bean.panel.VRPanel vRPanel1;
    // End of variables declaration//GEN-END:variables
}
