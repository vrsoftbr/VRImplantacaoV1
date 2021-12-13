package vrimplantacao2.gui.interfaces;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao2.dao.interfaces.CerebroDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;

import vrimplantacao2.parametro.Parametros;

 public class CerebroGUI extends VRInternalFrame implements ConexaoEvent {

    private static final String SISTEMA = "Cerebro";
    private static final String SERVIDOR_SQL = "Firebird";
    private static CerebroGUI instance;

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexao.carregarParametros();
        txtComplSistema.setText(params.get(SISTEMA, "COMPL_SISTEMA"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexao.atualizarParametros();
        params.put(txtComplSistema.getText(), SISTEMA, "COMPL_SISTEMA");
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

    private CerebroDAO dao = new CerebroDAO();
    private ConexaoFirebird connFirebird = new ConexaoFirebird();

    @Override
    public void executar() throws Exception {
        carregarLojaVR();
        carregarLojaCliente();
        carregarTiposDocumentos();
        gravarParametros();
    }

    private CerebroGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        ConexaoFirebird.encoding = "WIN1252";

        conexao.setSistema(SISTEMA);
        conexao.setOnConectar(this);
        conexao.stringConexao = "jdbc:firebirdsql://host:port//opt/firebird/db.fdb";

        this.title = "Importação " + SISTEMA;

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

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

    public void carregarTiposDocumentos() throws Exception {
        int linha = 1;
        for (String doc : dao.getTipoDocumentos()) {
            txtTiposDocumentos.insert(doc, txtTiposDocumentos.getCaretPosition());
            txtTiposDocumentos.append("\n");
            linha++;
        }
        txtTiposDocumentos.setRows(linha);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new CerebroGUI(i_mdiFrame);
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
                    dao.complSistema = txtComplSistema.getText();
                    dao.tipoDocumento = txtCodTiposDocumentos.getText();

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);

                    if (tabs.getSelectedIndex() == 0) {

                        importador.setImportarIndividualLoja(chkImportarIndividualLoja.isSelected());
                        
                        if (chkFamiliaProduto.isSelected()) {
                            importador.importarFamiliaProduto();
                        }

                        if (chkMercadologico.isSelected()) {
                            importador.importarMercadologico();
                        }

                        if (chkProdutos.isSelected()) {
                            importador.importarProduto(chkManterBalanca.isSelected());
                        }

                        {
                            List<OpcaoProduto> opcoes = new ArrayList<>();
                            
                            if (chkT1Custo.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO);
                            }
                            if (chkT1Preco.isSelected()) {
                                opcoes.add(OpcaoProduto.PRECO);
                            }
                            if (chkT1Estoque.isSelected()) {
                                opcoes.add(OpcaoProduto.ESTOQUE);
                            }
                            if (chkT1PisCofins.isSelected()) {
                                opcoes.add(OpcaoProduto.PIS_COFINS);
                            }
                            if (chkT1NatReceita.isSelected()) {
                                opcoes.add(OpcaoProduto.NATUREZA_RECEITA);
                            }
                            if (chkT1ICMS.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS);
                            }
                            if (chkT1AtivoInativo.isSelected()) {
                                opcoes.add(OpcaoProduto.ATIVO);
                            }
                            if (chkT1DescCompleta.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_COMPLETA);
                            }
                            if (chkT1DescReduzida.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_REDUZIDA);
                            }
                            if (chkT1DescGondola.isSelected()) {
                                opcoes.add(OpcaoProduto.DESC_GONDOLA);
                            }
                            if (chkTipoEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_EAN);
                            }
                            if (chkQtdEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_EAN);
                            }
                            if (chkTipoEmbalagemPROD.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO);
                            }
                            if (chkQtdEmbalagemPROD.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_COTACAO);
                            }                            
                            if (chkValidade.isSelected()) {
                                opcoes.add(OpcaoProduto.VALIDADE);
                            }
                            if (chkFamilia.isSelected()) {
                                opcoes.add(OpcaoProduto.FAMILIA);
                            }
                            if (chkIcmsSaida.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_SAIDA);
                            }
                            if (chkIcmsSaidaForaEstado.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO);
                            }
                            if (chkIcmsEntrada.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_ENTRADA);
                            }
                            if (chkIcmsEntradaForaEstado.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO);
                            }
                            if (chkIcmsConsumidor.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_CONSUMIDOR);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarProdutos(opcoes);
                            }
                        }

                        if (chkT1EAN.isSelected()) {
                            importador.importarEAN();
                        }

                        if (chkT1EANemBranco.isSelected()) {
                            importador.importarEANemBranco();
                        }

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
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS,
                                    OpcaoCliente.SITUACAO_CADASTRO, OpcaoCliente.BLOQUEADO);
                        }
                        if (chkRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
                        }
                        if (chkConvEmpresa.isSelected()) {
                            importador.importarConvenioEmpresa();
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
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
                    try {
                        connFirebird.close();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        vRTextArea1 = new vrframework.bean.textArea.VRTextArea();
        vRCheckBox1 = new vrframework.bean.checkBox.VRCheckBox();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRTabbedPane2 = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Custo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Preco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Estoque = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EAN = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EANemBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1PisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkT1NatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkT1AtivoInativo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescReduzida = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsSaida = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsEntrada = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsConsumidor = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsEntradaForaEstado = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemPROD = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemPROD = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsSaidaForaEstado = new vrframework.bean.checkBox.VRCheckBox();
        chkImportarIndividualLoja = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel9 = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        txtCodTiposDocumentos = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTiposDocumentos = new vrframework.bean.textArea.VRTextArea();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        tabConveniado = new javax.swing.JPanel();
        chkConvEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        cmbLojaOrigem = new javax.swing.JComboBox();
        conexao = new vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel();
        txtComplSistema = new vrframework.bean.textField.VRTextField();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        vRLabel2 = new vrframework.bean.label.VRLabel();

        vRTextArea1.setColumns(20);
        vRTextArea1.setRows(5);
        jScrollPane1.setViewportView(vRTextArea1);

        vRCheckBox1.setText("vRCheckBox1");

        setTitle("Importação Cerebro");
        setToolTipText("");

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);

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

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFamiliaProduto.setText("Familia Produto");
        chkFamiliaProduto.setEnabled(true);
        chkFamiliaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaProdutoActionPerformed(evt);
            }
        });

        chkMercadologico.setText("Mercadologico");
        chkMercadologico.setEnabled(true);

        chkProdutos.setText("Produtos");
        chkProdutos.setEnabled(true);

        chkManterBalanca.setText("Manter Balança");
        chkManterBalanca.setEnabled(true);

        chkT1Custo.setText("Custo");

        chkT1Preco.setText("Preço");

        chkT1Estoque.setText("Estoque");

        chkT1EAN.setText("EAN");

        chkT1EANemBranco.setText("EAN em branco");

        chkT1PisCofins.setText("PIS/COFINS");

        chkT1NatReceita.setText("Nat. Receita");

        chkT1ICMS.setText("ICMS");

        chkT1AtivoInativo.setText("Ativo/Inativo");

        chkT1DescCompleta.setText("Descrição Completa");

        chkT1DescReduzida.setText("Descrição Reduzida");

        chkT1DescGondola.setText("Descrição Gondola");

        chkValidade.setText("Validade");
        chkValidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkValidadeActionPerformed(evt);
            }
        });

        chkFamilia.setText("Família");

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");

        chkIcmsSaida.setText("Icms Saída");

        chkIcmsEntrada.setText("Icms Entrada");

        chkIcmsConsumidor.setText("Icms Consumidor");

        chkIcmsEntradaForaEstado.setText("Icms Entrada Fora Estado");

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");

        chkTipoEmbalagemPROD.setText("Tipo Emb. PROD");

        chkQtdEmbalagemPROD.setText("Qtd. Emb. PROD");

        chkIcmsSaidaForaEstado.setText("Icms Saída Fora Estado");

        chkImportarIndividualLoja.setText("Importar Individual Loja");

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkTipoEmbalagemPROD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkQtdEmbalagemPROD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(31, 31, 31)
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkIcmsSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkIcmsEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkIcmsEntradaForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkIcmsConsumidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkIcmsSaidaForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(chkImportarIndividualLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(vRPanel7Layout.createSequentialGroup()
                                        .addGap(46, 46, 46)
                                        .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(vRPanel7Layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(vRPanel7Layout.createSequentialGroup()
                                        .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkTipoEmbalagemPROD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkQtdEmbalagemPROD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIcmsSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(vRPanel7Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkIcmsSaidaForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIcmsEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIcmsEntradaForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIcmsConsumidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(chkImportarIndividualLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vRTabbedPane2.addTab("Produtos", vRPanel7);

        vRPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(360, Short.MAX_VALUE))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Fornecedores", vRPanel8);

        vRPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkClientePreferencial.setText("Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);
        chkClientePreferencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClientePreferencialActionPerformed(evt);
            }
        });

        chkRotativo.setText("Crédito Rotativo");
        chkRotativo.setEnabled(true);
        chkRotativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRotativoActionPerformed(evt);
            }
        });

        chkCheque.setText("Cheque");
        chkCheque.setEnabled(true);
        chkCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChequeActionPerformed(evt);
            }
        });

        txtTiposDocumentos.setEditable(false);
        txtTiposDocumentos.setColumns(1000);
        txtTiposDocumentos.setRows(100);
        txtTiposDocumentos.setTabSize(100);
        jScrollPane2.setViewportView(txtTiposDocumentos);

        vRLabel3.setText("O quadro ao mostra os tipo de documentos usadas pelo cliente.");

        vRLabel4.setText("Identifique quais se referem ao crédito rotativo, ");

        vRLabel5.setText("e digite o código no campo acima. ");

        vRLabel6.setText("Se for mais de uma finalizadora, separe os códigos por ");

        vRLabel7.setText("vírgula.");

        vRLabel9.setText("Documentos");

        javax.swing.GroupLayout vRPanel9Layout = new javax.swing.GroupLayout(vRPanel9);
        vRPanel9.setLayout(vRPanel9Layout);
        vRPanel9Layout.setHorizontalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodTiposDocumentos, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 223, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel9Layout.setVerticalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodTiposDocumentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Clientes", vRPanel9);

        chkConvEmpresa.setText("Empresa");
        chkConvEmpresa.setEnabled(true);
        chkConvEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConvEmpresaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabConveniadoLayout = new javax.swing.GroupLayout(tabConveniado);
        tabConveniado.setLayout(tabConveniadoLayout);
        tabConveniadoLayout.setHorizontalGroup(
            tabConveniadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConveniadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(556, Short.MAX_VALUE))
        );
        tabConveniadoLayout.setVerticalGroup(
            tabConveniadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConveniadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(214, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Conveniado", tabConveniado);

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
                .addContainerGap(377, Short.MAX_VALUE))
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
                .addContainerGap(145, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);
        tabs.addTab("Balança", vRImportaArquivBalancaPanel1);

        vRPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Firebird"));
        vRPanel6.setPreferredSize(new java.awt.Dimension(350, 350));

        cmbLojaOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaOrigemActionPerformed(evt);
            }
        });

        txtComplSistema.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtComplSistemaKeyReleased(evt);
            }
        });

        vRLabel1.setText("Compl. Sistema:");

        vRLabel2.setText("Loja:");

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(conexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(vRPanel6Layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtComplSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addComponent(conexao, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComplSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();

            if ((chkRotativo.isSelected())
                    && (txtTiposDocumentos.getText().trim().isEmpty())) {
                Util.exibirMensagem("Digite o código do Documento para importar o Crédito Rotativo", getTitle());
            } else {
                importarTabelas();
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void chkFamiliaProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaProdutoActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorActionPerformed

    private void chkProdutoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutoFornecedorActionPerformed

    private void chkClientePreferencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClientePreferencialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClientePreferencialActionPerformed

    private void chkRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRotativoActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void chkValidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkValidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkValidadeActionPerformed

    private void cmbLojaOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaOrigemActionPerformed
        dao.setLojaOrigem(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj);
    }//GEN-LAST:event_cmbLojaOrigemActionPerformed

    private void txtComplSistemaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComplSistemaKeyReleased
        dao.complSistema = txtComplSistema.getText();
    }//GEN-LAST:event_txtComplSistemaKeyReleased

    private void chkConvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvEmpresaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkConvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsConsumidor;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsEntrada;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsEntradaForaEstado;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsSaida;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsSaidaForaEstado;
    private vrframework.bean.checkBox.VRCheckBox chkImportarIndividualLoja;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemPROD;
    private vrframework.bean.checkBox.VRCheckBox chkRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkT1AtivoInativo;
    private vrframework.bean.checkBox.VRCheckBox chkT1Custo;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescCompleta;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescGondola;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescReduzida;
    private vrframework.bean.checkBox.VRCheckBox chkT1EAN;
    private vrframework.bean.checkBox.VRCheckBox chkT1EANemBranco;
    private vrframework.bean.checkBox.VRCheckBox chkT1Estoque;
    private vrframework.bean.checkBox.VRCheckBox chkT1ICMS;
    private vrframework.bean.checkBox.VRCheckBox chkT1NatReceita;
    private vrframework.bean.checkBox.VRCheckBox chkT1PisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkT1Preco;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemPROD;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel conexao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel tabConveniado;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private javax.swing.JTextField txtCodTiposDocumentos;
    private vrframework.bean.textField.VRTextField txtComplSistema;
    private vrframework.bean.textArea.VRTextArea txtTiposDocumentos;
    private vrframework.bean.checkBox.VRCheckBox vRCheckBox1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel9;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    private vrframework.bean.textArea.VRTextArea vRTextArea1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
