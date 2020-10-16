package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
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
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.STSitemasDAO;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class STSistemasGUI extends VRInternalFrame implements ConexaoEvent {

    private static final String SISTEMA = "STSitemas";
    private static STSistemasGUI instance;

    public static String getSISTEMA() {
        return SISTEMA;
    }

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.carregarParametros(params, SISTEMA);
        conexao.carregarParametros();
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        tabProdutos.gravarParametros(params, SISTEMA);
        conexao.atualizarParametros();
        Estabelecimento cliente = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.cnpj, SISTEMA, "LOJA_CLIENTE");
            vLojaCliente = cliente.cnpj;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private STSitemasDAO dao = new STSitemasDAO();

    private STSistemasGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        conexao.host = "10.0.0.105";
        conexao.database = "Merchant300";
        conexao.port = "1433";
        conexao.user = "sa";
        conexao.pass = "";

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        
        tabProdutos.setOpcoesDisponiveis(dao);
        
        tabProdutos.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                if (!txtCompLoja.getText().trim().isEmpty()) {
                    return dao.getSistema() + " - " + txtCompLoja.getText().trim();
                } else {
                    return dao.getSistema();
                }
                
            }

            @Override
            public String getLoja() {                
                dao.setLojaOrigem(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj);
                return dao.getLojaOrigem();
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });

        conexao.setOnConectar(this);

        carregarParametros();
        
        vRImportaArquivBalancaPanel1.setSistema(SISTEMA);
        vRImportaArquivBalancaPanel1.setLoja(vLojaCliente);

        centralizarForm();
        this.setMaximum(false);
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

    public void carregarLojaCliente() throws Exception {
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (Estabelecimento loja : dao.getLojasCliente()) {
            cmbLojaOrigem.addItem(loja);
            if (vLojaCliente != null && vLojaCliente.equals(loja.cnpj)) {
                index = cont;
            }
            cont++;
        }
        cmbLojaOrigem.setSelectedIndex(index);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new STSistemasGUI(i_mdiFrame);
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

                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);
                    tabProdutos.setImportador(importador);
                    dao.compLoja = txtCompLoja.getText().trim();

                    if (tabOperacoes.getSelectedIndex() == 0) {

                        tabProdutos.executarImportacao();
                        
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }

                        {
                            List<OpcaoFornecedor> opcoes = new ArrayList<>();
                            
                            if (chkFornNumeroEnd.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NUMERO);
                            }
                            
                            if (chkFornTelefone.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TELEFONE);
                            }
                            
                            if (chkFornPrazoFornecedor.isSelected()) {
                                opcoes.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
                            }
                            
                            if (chkFornPrazoPedido.isSelected()) {
                                opcoes.add(OpcaoFornecedor.PRAZO_PEDIDO_FORNECEDOR);
                            }

                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
                        }
                        
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }

                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        
                        List<OpcaoCliente> opt = new ArrayList<>();
                        if (chkClienteBloqueado.isSelected()) {
                            opt.add(OpcaoCliente.BLOQUEADO);
                        }
                        if (chkClienteValorLimite.isSelected()) {
                            opt.add(OpcaoCliente.VALOR_LIMITE);
                        }
                        if (chkDataNascimentoConjuge.isSelected()) {
                            opt.add(OpcaoCliente.DATA_NASCIMENTO_CONJUGE);
                        }
                        
                        if (chkCliEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.EMPRESA);
                        }
                        if (chkCliEnderecoEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.ENDERECO_EMPRESA);
                        }
                        if (chkCliNumeroEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.NUMERO_EMPRESA);
                        }
                        if (chkCliComplementoEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.COMPLEMENTO_EMPRESA);
                        }
                        if (chkCliBairroEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.BAIRRO_EMPRESA);
                        }
                        if (chkCliMunicipioEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.MUNICIPIO_EMPRESA);
                        }
                        if (chkCliUFEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.UF_EMPRESA);
                        }
                        if (chkCliCepEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.CEP_EMPRESA);
                        }
                        if (chkCliTelefoneEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.TELEFONE_EMPRESA);
                        }
                        if (chkCliCargoEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.CARGO);
                        }
                        if (chkCliDataAdmissaoEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.DATA_ADMISSAO);
                        }
                        if (chkCliSalarioEmpresa.isSelected()) {
                            opt.add(OpcaoCliente.SALARIO);
                        }
                        if (chkCliAtivo.isSelected()) {
                            opt.add(OpcaoCliente.SITUACAO_CADASTRO);
                        }
                        
                        if (!opt.isEmpty()) {
                            importador.atualizarClientePreferencial(opt.toArray(new OpcaoCliente[]{}));
                        }
                        
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                    } else if (tabOperacoes.getSelectedIndex() == 1) {
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
                        if (chkUnifClienteEventual.isSelected()) {
                            importador.unificarClienteEventual();
                        }
                    }

                    ProgressBar.dispose();
                    
                    gravarParametros();
                    
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {
                        ConexaoSqlServer.getConexao().close();
                    } catch (Exception ex1) {
                        Exceptions.printStackTrace(ex1);
                    }
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                } finally {
                    tabProdutos.setImportador(null);
                }
            }
        };

        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRCheckBox1 = new vrframework.bean.checkBox.VRCheckBox();
        conexao = new vrimplantacao2.gui.component.conexao.sqlserver.ConexaoSqlServerPanel();
        lblLojaCliente = new vrframework.bean.label.VRLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        tabOperacoes = new javax.swing.JTabbedPane();
        tabImportacao = new javax.swing.JTabbedPane();
        tabProdutos = new vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFornNumeroEnd = new vrframework.bean.checkBox.VRCheckBox();
        chkFornTelefone = new vrframework.bean.checkBox.VRCheckBox();
        chkFornPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFornPrazoPedido = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabClientes = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteValorLimite = new vrframework.bean.checkBox.VRCheckBox();
        chkDataNascimentoConjuge = new vrframework.bean.checkBox.VRCheckBox();
        chkCliEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliEnderecoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliNumeroEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliComplementoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliBairroEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliMunicipioEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliUFEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliCepEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliTelefoneEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliCargoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliDataAdmissaoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCliSalarioEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkCliAtivo = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        lblCompLoja = new javax.swing.JLabel();
        txtCompLoja = new javax.swing.JTextField();

        vRCheckBox1.setText("vRCheckBox1");

        setTitle("Importação ST Sistemas");
        setToolTipText("");

        conexao.setSistema("JM2Online");

        lblLojaCliente.setText("Loja (Cliente):");

        cmbLojaOrigem.setModel(new javax.swing.DefaultComboBoxModel());

        tabImportacao.addTab("Produtos", tabProdutos);

        tabFornecedor.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkFornecedor.setText("Fornecedor");
        tabFornecedor.add(chkFornecedor);

        chkFornNumeroEnd.setText("Número");
        tabFornecedor.add(chkFornNumeroEnd);

        chkFornTelefone.setText("Telefone");
        tabFornecedor.add(chkFornTelefone);

        chkFornPrazoFornecedor.setText("Prazo Fornecedor");
        tabFornecedor.add(chkFornPrazoFornecedor);

        chkFornPrazoPedido.setText("Prazo Pedido");
        tabFornecedor.add(chkFornPrazoPedido);

        chkProdutoFornecedor.setText("Produto Fornecedor");
        tabFornecedor.add(chkProdutoFornecedor);

        tabImportacao.addTab("Fornecedores", tabFornecedor);

        chkClientePreferencial.setText("Cliente Preferencial");

        chkCreditoRotativo.setText("Crédito Rotativo");

        chkClienteBloqueado.setText("Bloqueado");

        chkClienteValorLimite.setText("Valor Limite");

        chkDataNascimentoConjuge.setText("Data Nascimento Conjuge");

        chkCliEmpresa.setText("Empresa");

        chkCliEnderecoEmpresa.setText("Endereço Empresa");

        chkCliNumeroEmpresa.setText("Número Empresa");

        chkCliComplementoEmpresa.setText("Complemento Empresa");

        chkCliBairroEmpresa.setText("Bairro Empresa");

        chkCliMunicipioEmpresa.setText("Município Empresa");

        chkCliUFEmpresa.setText("UF Empresa");

        chkCliCepEmpresa.setText("Cep Empresa");

        chkCliTelefoneEmpresa.setText("Telefone Empresa");

        chkCliCargoEmpresa.setText("Cargo Empresa");

        chkCliDataAdmissaoEmpresa.setText("Data Admissão");

        chkCliSalarioEmpresa.setText("Salário");
        chkCliSalarioEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCliSalarioEmpresaActionPerformed(evt);
            }
        });

        chkClienteEventual.setText("Cliente Eventual");

        chkCliAtivo.setText("Ativo");

        javax.swing.GroupLayout tabClientesLayout = new javax.swing.GroupLayout(tabClientes);
        tabClientes.setLayout(tabClientesLayout);
        tabClientesLayout.setHorizontalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkClienteBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkClienteValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDataNascimentoConjuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(chkCliEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliEnderecoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliNumeroEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliComplementoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliBairroEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(chkCliMunicipioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliUFEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliCepEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliTelefoneEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCliCargoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabClientesLayout.createSequentialGroup()
                        .addComponent(chkCliDataAdmissaoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliSalarioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCliAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        tabClientesLayout.setVerticalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClienteValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDataNascimentoConjuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCliEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliEnderecoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliNumeroEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliComplementoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliBairroEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCliMunicipioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliUFEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliCepEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliTelefoneEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliCargoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCliDataAdmissaoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliSalarioEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCliAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Clientes", tabClientes);

        tabOperacoes.addTab("Importação", tabImportacao);

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
                .addContainerGap(342, Short.MAX_VALUE))
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
                .addContainerGap(143, Short.MAX_VALUE))
        );

        tabOperacoes.addTab("Unificação", vRPanel2);
        tabOperacoes.addTab("Balança", vRImportaArquivBalancaPanel1);

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

        javax.swing.GroupLayout pnlLojaLayout = new javax.swing.GroupLayout(pnlLoja);
        pnlLoja.setLayout(pnlLojaLayout);
        pnlLojaLayout.setHorizontalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlLojaLayout.setVerticalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblCompLoja.setText("Complemento Loja:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(conexao, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabOperacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCompLoja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCompLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCompLoja)
                    .addComponent(txtCompLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabOperacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
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

    private void chkCliSalarioEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCliSalarioEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCliSalarioEmpresaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCliAtivo;
    private vrframework.bean.checkBox.VRCheckBox chkCliBairroEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliCargoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliCepEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliComplementoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliDataAdmissaoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliEnderecoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliMunicipioEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliNumeroEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliSalarioEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliTelefoneEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkCliUFEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkClienteBloqueado;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkClienteValorLimite;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkDataNascimentoConjuge;
    private vrframework.bean.checkBox.VRCheckBox chkFornNumeroEnd;
    private vrframework.bean.checkBox.VRCheckBox chkFornPrazoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornPrazoPedido;
    private vrframework.bean.checkBox.VRCheckBox chkFornTelefone;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.sqlserver.ConexaoSqlServerPanel conexao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCompLoja;
    private vrframework.bean.label.VRLabel lblLojaCliente;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.panel.VRPanel tabClientes;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private javax.swing.JTabbedPane tabImportacao;
    private javax.swing.JTabbedPane tabOperacoes;
    private vrimplantacao2.gui.component.checks.ChecksProdutoPanelGUI tabProdutos;
    private javax.swing.JTextField txtCompLoja;
    private vrframework.bean.checkBox.VRCheckBox vRCheckBox1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void executar() throws Exception {
        tabProdutos.btnMapaTribut.setEnabled(true);

        gravarParametros();
        carregarLojaVR();
        carregarLojaCliente();
    }

}
