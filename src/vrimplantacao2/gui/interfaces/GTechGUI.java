package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.GTechDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;

public class GTechGUI extends VRInternalFrame implements ConexaoEvent {

    private static final String SISTEMA = "GTech";
    private static GTechGUI instance;

    public static String getSISTEMA() {
        return SISTEMA;
    }

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoMySQL.carregarParametros();
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
        chkFiltrarKgNoSql.setSelected(params.getBool(SISTEMA, "FILTRAR_KG_NO_SQL"));
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexaoMySQL.atualizarParametros();
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
        params.put(chkFiltrarKgNoSql.isSelected(), SISTEMA, "FILTRAR_KG_NO_SQL");
        params.salvar();
    }

    private GTechDAO dao = new GTechDAO();

    private GTechGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        conexaoMySQL.host = "localhost";
        conexaoMySQL.database = "gtech-gestao";
        conexaoMySQL.port = "3306";
        conexaoMySQL.user = "g3_automacao";
        conexaoMySQL.pass = "#g31nf#";

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        
        btnMapaTrib.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
               return SISTEMA;
            }

            @Override
            public String getLoja() {
                dao.setLojaOrigem(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj); 
                return vLojaCliente;
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });

        conexaoMySQL.setOnConectar(this);

        carregarParametros();

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
        for (Estabelecimento loja : dao.getLojas()) {
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
                instance = new GTechGUI(i_mdiFrame);
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
                    dao.filtrarKgNoSql = chkFiltrarKgNoSql.isSelected();

                    if (tabOperacoes.getSelectedIndex() == 0) {

                        if (chkFamiliaProduto.isSelected()) {
                            importador.importarFamiliaProduto();
                        }

                        if (chkMercadologico.isSelected()) {
                            importador.importarMercadologico();
                        }

                        if (chkProdutos.isSelected()) {
                            importador.importarProduto(chkManterBalanca.isSelected());
                        }
                        if (chkPautaFiscal.isSelected()) {
                            importador.importarPautaFiscal(OpcaoFiscal.NOVOS);
                        }
                        if (chkPComprador.isSelected()) {
                            importador.importarComprador();
                        }             

                        {
                            List<OpcaoProduto> opcoes = new ArrayList<>();
                            if (chkCusto.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO);
                            }
                            if (chkCustoComImposto.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO_COM_IMPOSTO);
                            }
                            if (chkCustoSemImposto.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO_SEM_IMPOSTO);
                            }
                            if (chkPreco.isSelected()) {
                                opcoes.add(OpcaoProduto.PRECO);
                            }
                            if (chkEstoque.isSelected()) {
                                opcoes.add(OpcaoProduto.ESTOQUE);
                            }
                            if (chkPisCofins.isSelected()) {
                                opcoes.add(OpcaoProduto.PIS_COFINS);
                            }
                            if (chkNatReceita.isSelected()) {
                                opcoes.add(OpcaoProduto.NATUREZA_RECEITA);
                            }
                            if (chkICMS.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS);
                            }
                            if (chkAtivoInativo.isSelected()) {
                                opcoes.add(OpcaoProduto.ATIVO);
                            }
                            if (chkDescCompleta.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_COMPLETA);
                            }
                            if (chkDescReduzida.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_REDUZIDA);
                            }
                            if (chkDescGondola.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_GONDOLA);
                            }
                            if (chkProdMercadologico.isSelected()) {
                                opcoes.add(OpcaoProduto.MERCADOLOGICO);
                            }
                            if (chkValidade.isSelected()) {
                                opcoes.add(OpcaoProduto.VALIDADE);
                            }
                            if (chkMargem.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }
                            if (chkFamilia.isSelected()) {
                                opcoes.add(OpcaoProduto.FAMILIA);
                            }
                            if (chkTipoEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_EAN);
                            }
                            if (chkQtdEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_EAN);
                            }
                            if (chkPautaFiscalProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.EXCECAO);
                            }
                            if (chkPSitCadastro.isSelected()) {
                                opcoes.add(OpcaoProduto.ATIVO);
                            }
                            if (chkPDescontinuado.isSelected()) {
                                opcoes.add(OpcaoProduto.DESCONTINUADO);
                            }
                            if (chkPVendaPdv.isSelected()) {
                                opcoes.add(OpcaoProduto.VENDA_PDV);
                            }
                            if (chkPSugestaoCotacao.isSelected()) {
                                opcoes.add(OpcaoProduto.SUGESTAO_COTACAO);
                            }
                            if (chkPProdComprador.isSelected()) {
                                opcoes.add(OpcaoProduto.COMPRADOR_PRODUTO);
                            }
                            if (chkAtacado.isSelected()) {
                                opcoes.add(OpcaoProduto.ATACADO);
                            }
                            if (chkNcm.isSelected()) {
                                opcoes.add(OpcaoProduto.NCM);
                            }
                            if (chkCest.isSelected()) {
                                opcoes.add(OpcaoProduto.CEST);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarProdutos(opcoes);
                            }
                        }
                        if (chkEAN.isSelected()) {
                            importador.importarEAN();
                        }
                        if (chkEANemBranco.isSelected()) {
                            importador.importarEANemBranco();
                        }                        
                        
                        {
                            List<OpcaoNutricional> opcoes = new ArrayList<>();
                            if (chkNutricionalFilizola.isSelected()) {
                                opcoes.add(OpcaoNutricional.FILIZOLA);
                            }
                            if (chkNutricionalToledo.isSelected()) {
                                opcoes.add(OpcaoNutricional.TOLEDO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.importarNutricional(opcoes.toArray(new OpcaoNutricional[] {}));
                            }                            
                        }
                        
                        {
                            List<OpcaoReceitaBalanca> opcoes = new ArrayList<>();
                            if (chkPReceitaFilizola.isSelected()) {
                                opcoes.add(OpcaoReceitaBalanca.FILIZOLA);
                            }
                            if (chkPReceitaToledo.isSelected()) {
                                opcoes.add(OpcaoReceitaBalanca.TOLEDO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.importarReceitaBalanca(opcoes.toArray(new OpcaoReceitaBalanca[] {}));
                            }                            
                        }
                        
                        if (chkOferta.isSelected()) {
                            importador.importarOfertas(new Date());
                        }
                        
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }

                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }

                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial();
                        }
                        if (chkCAtivo.isSelected()) {
                            importador.atualizarClientePreferencial(OpcaoCliente.SITUACAO_CADASTRO);
                        }
                        
                        if (chkCreditoRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
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
        vRLabel1 = new vrframework.bean.label.VRLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        tabOperacoes = new javax.swing.JTabbedPane();
        tabImportacao = new javax.swing.JTabbedPane();
        tabBalanca = new vrframework.bean.panel.VRPanel();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        chkFiltrarKgNoSql = new vrframework.bean.checkBox.VRCheckBox();
        tabProdutos = new javax.swing.JPanel();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkCusto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoSemImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoComImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkPreco = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkEANemBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkPisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkNatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkAtivoInativo = new vrframework.bean.checkBox.VRCheckBox();
        chkDescCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkDescReduzida = new vrframework.bean.checkBox.VRCheckBox();
        chkDescGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkProdMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscal = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscalProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPSitCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkPDescontinuado = new vrframework.bean.checkBox.VRCheckBox();
        chkPVendaPdv = new vrframework.bean.checkBox.VRCheckBox();
        chkPSugestaoCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkPComprador = new vrframework.bean.checkBox.VRCheckBox();
        chkPProdComprador = new vrframework.bean.checkBox.VRCheckBox();
        chkPReceitaFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkPReceitaToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkOferta = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTrib = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
        chkNcm = new vrframework.bean.checkBox.VRCheckBox();
        chkCest = new vrframework.bean.checkBox.VRCheckBox();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        jPanel3 = new javax.swing.JPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabClientes = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCAtivo = new vrframework.bean.checkBox.VRCheckBox();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new javax.swing.JTabbedPane();
        conexaoMySQL = new vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel();

        setTitle("Importação Emporio");
        setToolTipText("");

        vRLabel1.setText("Loja (Cliente):");

        cmbLojaOrigem.setModel(new javax.swing.DefaultComboBoxModel());

        chkFiltrarKgNoSql.setText("Filtrar KG no SQL");

        javax.swing.GroupLayout tabBalancaLayout = new javax.swing.GroupLayout(tabBalanca);
        tabBalanca.setLayout(tabBalancaLayout);
        tabBalancaLayout.setHorizontalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabBalancaLayout.createSequentialGroup()
                        .addComponent(chkFiltrarKgNoSql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabBalancaLayout.setVerticalGroup(
            tabBalancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabBalancaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkFiltrarKgNoSql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Balança", tabBalanca);

        tabProdutos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkMercadologico.setText("Mercadologico");
        chkMercadologico.setEnabled(true);
        tabProdutos.add(chkMercadologico);

        chkFamiliaProduto.setText("Familia Produto");
        chkFamiliaProduto.setEnabled(true);
        chkFamiliaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaProdutoActionPerformed(evt);
            }
        });
        tabProdutos.add(chkFamiliaProduto);

        chkProdutos.setText("Produtos");
        chkProdutos.setEnabled(true);

        chkManterBalanca.setText("Manter Balança");
        chkManterBalanca.setEnabled(true);

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tabProdutos.add(vRPanel1);

        chkCusto.setText("Custo");
        tabProdutos.add(chkCusto);

        chkCustoSemImposto.setText("Custo Sem Imposto");
        tabProdutos.add(chkCustoSemImposto);

        chkCustoComImposto.setText("Custo Com Imposto");
        tabProdutos.add(chkCustoComImposto);

        chkPreco.setText("Preço");
        tabProdutos.add(chkPreco);

        chkEstoque.setText("Estoque");
        tabProdutos.add(chkEstoque);

        chkEAN.setText("EAN");
        tabProdutos.add(chkEAN);

        chkEANemBranco.setText("EAN em branco");
        tabProdutos.add(chkEANemBranco);

        chkPisCofins.setText("PIS/COFINS");
        tabProdutos.add(chkPisCofins);

        chkNatReceita.setText("Nat. Receita");
        tabProdutos.add(chkNatReceita);

        chkICMS.setText("ICMS");
        tabProdutos.add(chkICMS);

        chkAtivoInativo.setText("Ativo/Inativo");
        tabProdutos.add(chkAtivoInativo);

        chkDescCompleta.setText("Descrição Completa");
        tabProdutos.add(chkDescCompleta);

        chkDescReduzida.setText("Descrição Reduzida");
        tabProdutos.add(chkDescReduzida);

        chkDescGondola.setText("Descrição Gondola");
        tabProdutos.add(chkDescGondola);

        chkProdMercadologico.setText("Prod. Mercadológico");
        tabProdutos.add(chkProdMercadologico);

        chkValidade.setText("Validade");
        tabProdutos.add(chkValidade);

        chkFamilia.setText("Família X Produto");
        chkFamilia.setToolTipText("Corrige o relacionamento entre o produto e a família.");
        tabProdutos.add(chkFamilia);

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");
        tabProdutos.add(chkTipoEmbalagemEAN);

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");
        tabProdutos.add(chkQtdEmbalagemEAN);

        chkMargem.setText("Margem");
        tabProdutos.add(chkMargem);

        chkNutricionalFilizola.setText("Nutricional (Filizola)");
        tabProdutos.add(chkNutricionalFilizola);

        chkNutricionalToledo.setText("Nutricional (Toledo)");
        tabProdutos.add(chkNutricionalToledo);

        chkPautaFiscal.setText("Pauta Fiscal");
        tabProdutos.add(chkPautaFiscal);

        chkPautaFiscalProduto.setText("Pauta Fiscal X Produto");
        tabProdutos.add(chkPautaFiscalProduto);

        chkPSitCadastro.setText("Sit. Cadastro");
        tabProdutos.add(chkPSitCadastro);

        chkPDescontinuado.setText("Descontinuado");
        tabProdutos.add(chkPDescontinuado);

        chkPVendaPdv.setText("Venda (PDV)");
        tabProdutos.add(chkPVendaPdv);

        chkPSugestaoCotacao.setText("Sugestão Cotação");
        tabProdutos.add(chkPSugestaoCotacao);

        chkPComprador.setText("Comprador");
        tabProdutos.add(chkPComprador);

        chkPProdComprador.setText("Produto Comprador");
        tabProdutos.add(chkPProdComprador);

        chkPReceitaFilizola.setText("Receita (Filizola)");
        tabProdutos.add(chkPReceitaFilizola);

        chkPReceitaToledo.setText("Receita (Toledo)");
        tabProdutos.add(chkPReceitaToledo);

        chkOferta.setText("Oferta");
        tabProdutos.add(chkOferta);

        chkAtacado.setText("Atacado");
        tabProdutos.add(chkAtacado);

        btnMapaTrib.setEnabled(false);
        btnMapaTrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTribActionPerformed(evt);
            }
        });
        tabProdutos.add(btnMapaTrib);

        chkNcm.setText("NCM");
        tabProdutos.add(chkNcm);

        chkCest.setText("CEST");
        tabProdutos.add(chkCest);

        tabImportacao.addTab("Produtos", tabProdutos);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkFornecedor.setText("Fornecedor");
        jPanel3.add(chkFornecedor);

        chkProdutoFornecedor.setText("Produto Fornecedor");
        jPanel3.add(chkProdutoFornecedor);

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addGap(74, 74, 74))
        );

        tabImportacao.addTab("Fornecedores", tabFornecedor);

        chkClientePreferencial.setText("Cliente Preferencial");

        chkCheque.setText("Cheque");

        chkCreditoRotativo.setText("Crédito Rotativo");

        chkCAtivo.setText("Ativo");

        javax.swing.GroupLayout tabClientesLayout = new javax.swing.GroupLayout(tabClientes);
        tabClientes.setLayout(tabClientesLayout);
        tabClientesLayout.setHorizontalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(376, Short.MAX_VALUE))
        );
        tabClientesLayout.setVerticalGroup(
            tabClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkCAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(165, Short.MAX_VALUE))
        );

        tabImportacao.addTab("Clientes", tabClientes);

        tabOperacoes.addTab("Importação", tabImportacao);

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
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
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

        conexaoMySQL.setSistema(getSISTEMA());
        tabs.addTab("Conexão", conexaoMySQL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabOperacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabOperacoes)
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

    private void chkFamiliaProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaProdutoActionPerformed

    private void btnMapaTribActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTribActionPerformed

    }//GEN-LAST:event_btnMapaTribActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton btnMapaTrib;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkAtivoInativo;
    private vrframework.bean.checkBox.VRCheckBox chkCAtivo;
    private vrframework.bean.checkBox.VRCheckBox chkCest;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkCusto;
    private vrframework.bean.checkBox.VRCheckBox chkCustoComImposto;
    private vrframework.bean.checkBox.VRCheckBox chkCustoSemImposto;
    private vrframework.bean.checkBox.VRCheckBox chkDescCompleta;
    private vrframework.bean.checkBox.VRCheckBox chkDescGondola;
    private vrframework.bean.checkBox.VRCheckBox chkDescReduzida;
    private vrframework.bean.checkBox.VRCheckBox chkEAN;
    private vrframework.bean.checkBox.VRCheckBox chkEANemBranco;
    private vrframework.bean.checkBox.VRCheckBox chkEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFiltrarKgNoSql;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkICMS;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkMargem;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkNatReceita;
    private vrframework.bean.checkBox.VRCheckBox chkNcm;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalFilizola;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalToledo;
    private vrframework.bean.checkBox.VRCheckBox chkOferta;
    private vrframework.bean.checkBox.VRCheckBox chkPComprador;
    private vrframework.bean.checkBox.VRCheckBox chkPDescontinuado;
    private vrframework.bean.checkBox.VRCheckBox chkPProdComprador;
    private vrframework.bean.checkBox.VRCheckBox chkPReceitaFilizola;
    private vrframework.bean.checkBox.VRCheckBox chkPReceitaToledo;
    private vrframework.bean.checkBox.VRCheckBox chkPSitCadastro;
    private vrframework.bean.checkBox.VRCheckBox chkPSugestaoCotacao;
    private vrframework.bean.checkBox.VRCheckBox chkPVendaPdv;
    private vrframework.bean.checkBox.VRCheckBox chkPautaFiscal;
    private vrframework.bean.checkBox.VRCheckBox chkPautaFiscalProduto;
    private vrframework.bean.checkBox.VRCheckBox chkPisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkPreco;
    private vrframework.bean.checkBox.VRCheckBox chkProdMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel conexaoMySQL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.panel.VRPanel tabBalanca;
    private vrframework.bean.panel.VRPanel tabClientes;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private javax.swing.JTabbedPane tabImportacao;
    private javax.swing.JTabbedPane tabOperacoes;
    private javax.swing.JPanel tabProdutos;
    private javax.swing.JTabbedPane tabs;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.panel.VRPanel vRPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void executar() throws Exception {
        gravarParametros();
        carregarLojaVR();
        carregarLojaCliente();
        btnMapaTrib.setEnabled(true);
    }

}
