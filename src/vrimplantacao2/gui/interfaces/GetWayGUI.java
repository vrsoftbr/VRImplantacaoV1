package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.gui.interfaces.MapeamentoVendasGetWay.ItensNaoExistenteGetWayGUI;
import vrimplantacao2.dao.interfaces.GetWayDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;

public class GetWayGUI extends VRInternalFrame {
    
    private static final String SISTEMA = "GetWay";
    private static final String SERVIDOR_SQL = "Sql Server";
    private static GetWayGUI instance;    
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    private GetWayDAO getWayDAO = new GetWayDAO();
    private ConexaoSqlServer connSqlServer = new ConexaoSqlServer();

    private void carregarTipoDocumento() throws Exception {
        cmbTipoDocRotativo.removeAllItems();
        cmbTipoDocRotativo.setModel(new DefaultComboBoxModel());
        cmbTipoDocCheque.removeAllItems();
        cmbTipoDocCheque.setModel(new DefaultComboBoxModel());
        for (ItemComboVO item: getWayDAO.getTipoDocumento()) {
            cmbTipoDocRotativo.addItem(item);
            cmbTipoDocCheque.addItem(item);
        }
    }
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtHost.setText(params.get(SISTEMA, "HOST"));
        txtDatabase.setText(params.get(SISTEMA, "DATABASE"));
        txtPorta.setText(params.get(SISTEMA, "PORTA"));
        txtUsuario.setText(params.get(SISTEMA, "USUARIO"));
        txtSenha.setText(params.get(SISTEMA, "SENHA"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtHost.getText(), SISTEMA, "HOST");
        params.put(txtDatabase.getText(), SISTEMA, "DATABASE");
        params.put(txtPorta.getText(), SISTEMA, "PORTA");
        params.put(txtUsuario.getText(), SISTEMA, "USUARIO");
        params.put(txtSenha.getText(), SISTEMA, "SENHA");
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
    
    private GetWayGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        //ConexaoFirebird.encoding = "WIN1252";        
        
        this.title = "Importação " + SISTEMA;
                
        //cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        carregarParametros();
        btnMapaTrib.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return getWayDAO;
            }

            @Override
            public String getSistema() {
                return SISTEMA;
            }

            @Override
            public String getLoja() {
                return vLojaCliente;
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });
        
        centralizarForm();
        this.setMaximum(false);
    }

    public void validarDadosAcesso() throws Exception {
        if (tabsConn.getSelectedIndex() == 0) {
            if (txtHost.getText().isEmpty()) {
                throw new VRException("Favor informar host do banco de dados " + SERVIDOR_SQL + "!");
            }
            if (txtPorta.getText().isEmpty()) {
                throw new VRException("Favor informar a porta do banco de dados " + SERVIDOR_SQL + "!");
            }
            if (txtDatabase.getText().isEmpty()) {
                throw new VRException("Favor informar nome do banco de dados " + SERVIDOR_SQL + "!");
            }
        } 
        if (txtSenha.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados " + SERVIDOR_SQL + "!");
        }
        if (txtUsuario.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + SERVIDOR_SQL + "!");
        }

        if (tabsConn.getSelectedIndex() == 0) {
            connSqlServer.abrirConexao(txtHost.getText(), txtPorta.getInt(), 
                    txtDatabase.getText(), txtUsuario.getText(), txtSenha.getText());
        }
        
        btnMapaTrib.setEnabled(true);
        gravarParametros();
        carregarLojaCliente();
        carregarLojaVR();
        carregarTipoDocumento();
    }
    
    public void carregarLojaCliente() throws Exception {
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (Estabelecimento loja: getWayDAO.getLojas()) {
            cmbLojaOrigem.addItem(loja);
            if (vLojaCliente != null && vLojaCliente.equals(loja.cnpj)) {
                index = cont;
            }
            cont++;
        }
        cmbLojaOrigem.setSelectedIndex(index);
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
                instance = new GetWayGUI(i_mdiFrame);
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
                    getWayDAO.v_tipoDocumento = ((ItemComboVO)cmbTipoDocRotativo.getSelectedItem()).id;
                    getWayDAO.v_tipoDocumentoCheque = ((ItemComboVO)cmbTipoDocCheque.getSelectedItem()).id;
                    getWayDAO.v_usar_arquivoBalanca = chkTemArquivoBalanca.isSelected();
                    getWayDAO.usarMargemBruta = chkUsarMargemBruta.isSelected();
                    
                    Importador importador = new Importador(getWayDAO);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);                    

                    if (tabs.getSelectedIndex() == 0) {
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
                            if (chkCustoComImposto.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO_COM_IMPOSTO);
                            }
                            if (chkCustoSemImposto.isSelected()) {
                                opcoes.add(OpcaoProduto.CUSTO_SEM_IMPOSTO);
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
                            if (chkT1NCM.isSelected()) {
                                opcoes.add(OpcaoProduto.NCM);
                            }
                            if (chkT1CEST.isSelected()) {
                                opcoes.add(OpcaoProduto.CEST);
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
                            if (chkT1ProdMercadologico.isSelected()) {
                                opcoes.add(OpcaoProduto.MERCADOLOGICO);
                            }                        
                            if (chkValidade.isSelected()) {
                                opcoes.add(OpcaoProduto.VALIDADE);
                            }
                            if (chkAtacado.isSelected()) {
                                opcoes.add(OpcaoProduto.ATACADO);
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
                            if (chkCategoria.isSelected()) {
                                opcoes.add(OpcaoProduto.CATEGORIA);
                            }
                            if (chkMargem.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarProdutos(opcoes);
                            }
                        }
                        
                        if (chkOfertas.isSelected()) {
                            importador.importarOfertas(txtDataFimOferta.getDate());
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
                        if (chkFCondicaoPagamento.isSelected()) {
                            opcoes.add(OpcaoFornecedor.CONDICAO_PAGAMENTO2);
                        }                        
                        if (chkFPrazoFornecedor.isSelected()) {
                            opcoes.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
                        }
                        if (chkSituacaoCadastroForn.isSelected()) {
                            opcoes.add(OpcaoFornecedor.SITUACAO_CADASTRO);
                        }
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }
                        if (chkContasPagar.isSelected()) {
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }                        
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.VALOR_LIMITE, 
                                    OpcaoCliente.SITUACAO_CADASTRO, OpcaoCliente.INSCRICAO_ESTADUAL);
                        }
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual();
                        }                        
                        if (chkRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkreceberDevolucao.isSelected()) {
                            getWayDAO.importarReceberDevolucao(idLojaVR);
                        }
                        if (chkReceberVerba.isSelected()) {
                            getWayDAO.importarReceberVerba(idLojaVR);
                        }
                        if (chkCheque.isSelected()) {
                            importador.importarCheque();
                        }
                        if (chkNutricionalFilizola.isSelected()) {
                            importador.importarNutricionalFilizola();
                        }
                        if (chkNutricionalToledo.isSelected()) {
                            //importador.importarNutricionalToledo();
                            //shiDAO.importarNutricionalToledo();
                        }                        
                        if (chkConvEmpresa.isSelected()) {
                            importador.importarConvenioEmpresa();
                        }                        
                        if (chkConvConveniado.isSelected()) {
                            importador.importarConvenioConveniado();
                        }                        
                        if (chkConvRecebimento.isSelected()) {
                            importador.importarConvenioTransacao();
                        }
                        if (chkPdvVendas.isSelected()) {
                            getWayDAO.setDataInicioVenda(edtDtVendaIni.getDate());
                            getWayDAO.setDataTerminoVenda(edtDtVendaFim.getDate());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        vRTextArea1 = new vrframework.bean.textArea.VRTextArea();
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
        jPanel1 = new javax.swing.JPanel();
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
        chkT1ProdMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkCategoria = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoComImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoSemImposto = new vrframework.bean.checkBox.VRCheckBox();
        jPanel3 = new javax.swing.JPanel();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        chkT1NCM = new vrframework.bean.checkBox.VRCheckBox();
        chkT1CEST = new vrframework.bean.checkBox.VRCheckBox();
        chkTemArquivoBalanca = new vrframework.bean.checkBox.VRCheckBox();
        jPanel2 = new javax.swing.JPanel();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        chkUsarMargemBruta = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTrib = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
        vRPanel9 = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        chkRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        chkTemFicha = new vrframework.bean.checkBox.VRCheckBox();
        chkPagamentoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkValorLimite = new vrframework.bean.checkBox.VRCheckBox();
        cmbTipoDocRotativo = new javax.swing.JComboBox();
        cmbTipoDocCheque = new javax.swing.JComboBox();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        chkPermiteRotativoCheque = new vrframework.bean.checkBox.VRCheckBox();
        chkreceberDevolucao = new vrframework.bean.checkBox.VRCheckBox();
        chkReceberVerba = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFCondicaoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkContasPagar = new vrframework.bean.checkBox.VRCheckBox();
        chkSituacaoCadastroForn = new vrframework.bean.checkBox.VRCheckBox();
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
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        btnMapDivergencias = new vrframework.bean.button.VRButton();
        chkMapearProdutoEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkVerificarCodigoBarras = new vrframework.bean.checkBox.VRCheckBox();
        chkVerificarCodAnterior = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkUnifClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel20 = new vrframework.bean.label.VRLabel();
        vRLabel21 = new vrframework.bean.label.VRLabel();
        btnConectar = new javax.swing.JToggleButton();
        tabsConn = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txtHost = new vrframework.bean.textField.VRTextField();
        vRLabel23 = new vrframework.bean.label.VRLabel();
        txtPorta = new vrframework.bean.textField.VRTextField();
        vRLabel24 = new vrframework.bean.label.VRLabel();
        vRLabel25 = new vrframework.bean.label.VRLabel();
        vRTextField1 = new vrframework.bean.textField.VRTextField();
        txtDatabase = new vrframework.bean.textField.VRTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbLojaOrigem = new javax.swing.JComboBox();

        vRTextArea1.setColumns(20);
        vRTextArea1.setRows(5);
        jScrollPane1.setViewportView(vRTextArea1);

        setTitle("Importação GetWay");
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
        vRPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        chkFamiliaProduto.setText("Familia Produto");
        chkFamiliaProduto.setEnabled(true);
        chkFamiliaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaProdutoActionPerformed(evt);
            }
        });
        vRPanel7.add(chkFamiliaProduto);

        chkMercadologico.setText("Mercadologico");
        chkMercadologico.setEnabled(true);
        vRPanel7.add(chkMercadologico);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkProdutos.setText("Produtos");
        chkProdutos.setEnabled(true);
        jPanel1.add(chkProdutos);

        chkManterBalanca.setText("Manter Balança");
        chkManterBalanca.setEnabled(true);
        jPanel1.add(chkManterBalanca);

        vRPanel7.add(jPanel1);

        chkT1Custo.setText("Custo");
        vRPanel7.add(chkT1Custo);

        chkT1Preco.setText("Preço");
        vRPanel7.add(chkT1Preco);

        chkT1Estoque.setText("Estoque");
        vRPanel7.add(chkT1Estoque);

        chkT1EAN.setText("EAN");
        vRPanel7.add(chkT1EAN);

        chkT1EANemBranco.setText("EAN em branco");
        vRPanel7.add(chkT1EANemBranco);

        chkT1PisCofins.setText("PIS/COFINS");
        vRPanel7.add(chkT1PisCofins);

        chkT1NatReceita.setText("Nat. Receita");
        vRPanel7.add(chkT1NatReceita);

        chkT1ICMS.setText("ICMS");
        vRPanel7.add(chkT1ICMS);

        chkT1AtivoInativo.setText("Ativo/Inativo");
        vRPanel7.add(chkT1AtivoInativo);

        chkT1DescCompleta.setText("Descrição Completa");
        vRPanel7.add(chkT1DescCompleta);

        chkT1DescReduzida.setText("Descrição Reduzida");
        vRPanel7.add(chkT1DescReduzida);

        chkT1DescGondola.setText("Descrição Gondola");
        vRPanel7.add(chkT1DescGondola);

        chkT1ProdMercadologico.setText("Prod. Mercadológico");
        vRPanel7.add(chkT1ProdMercadologico);

        chkNutricionalFilizola.setText("Nutricional Filizola");
        vRPanel7.add(chkNutricionalFilizola);

        chkNutricionalToledo.setText("Nutricional Toledo");
        vRPanel7.add(chkNutricionalToledo);

        chkValidade.setText("Validade");
        chkValidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkValidadeActionPerformed(evt);
            }
        });
        vRPanel7.add(chkValidade);

        chkAtacado.setText("Atacado");
        vRPanel7.add(chkAtacado);

        chkFamilia.setText("Família");
        vRPanel7.add(chkFamilia);

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");
        vRPanel7.add(chkTipoEmbalagemEAN);

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");
        vRPanel7.add(chkQtdEmbalagemEAN);

        chkCategoria.setText("Categoria");
        vRPanel7.add(chkCategoria);

        chkCustoComImposto.setText("Custo Com Imposto");
        vRPanel7.add(chkCustoComImposto);

        chkCustoSemImposto.setText("Custo Sem Imposto");
        vRPanel7.add(chkCustoSemImposto);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkOfertas.setText("Ofertas");
        jPanel3.add(chkOfertas);
        jPanel3.add(txtDataFimOferta);

        vRPanel7.add(jPanel3);

        chkT1NCM.setText("NCM");
        vRPanel7.add(chkT1NCM);

        chkT1CEST.setText("CEST");
        vRPanel7.add(chkT1CEST);

        chkTemArquivoBalanca.setText("Tem Arquivo Balança");
        vRPanel7.add(chkTemArquivoBalanca);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkMargem.setText("Margem");
        jPanel2.add(chkMargem);

        chkUsarMargemBruta.setText("Usar Margem Bruta");
        chkUsarMargemBruta.setEnabled(true);
        jPanel2.add(chkUsarMargemBruta);

        vRPanel7.add(jPanel2);

        btnMapaTrib.setEnabled(false);
        btnMapaTrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTribActionPerformed(evt);
            }
        });
        vRPanel7.add(btnMapaTrib);

        vRTabbedPane2.addTab("Produtos", vRPanel7);

        vRPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        chkTemFicha.setText("Tem Ficha");

        chkPagamentoRotativo.setText("Pagamento Rotativo");

        chkValorLimite.setText("Valor Limite");

        vRLabel1.setText("Tipo Documento Rotativo");

        vRLabel2.setText("Tipo Documento Cheque");

        chkPermiteRotativoCheque.setText("Permite Rotativo/Cheque");

        chkreceberDevolucao.setText("Devolução");

        chkReceberVerba.setText("Verba");

        javax.swing.GroupLayout vRPanel9Layout = new javax.swing.GroupLayout(vRPanel9);
        vRPanel9.setLayout(vRPanel9Layout);
        vRPanel9Layout.setHorizontalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTipoDocRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoDocCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPagamentoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkReceberVerba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel9Layout.createSequentialGroup()
                                .addComponent(chkTemFicha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPermiteRotativoCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkreceberDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        vRPanel9Layout.setVerticalGroup(
            vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTemFicha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPermiteRotativoCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkreceberDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkReceberVerba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPagamentoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipoDocRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoDocCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Clientes", vRPanel9);

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

        chkFPrazoFornecedor.setText("Prazo Fornecedor");
        chkFPrazoFornecedor.setEnabled(true);
        chkFPrazoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFPrazoFornecedorActionPerformed(evt);
            }
        });

        chkFCondicaoPagamento.setText("Condição Pagamento");
        chkFCondicaoPagamento.setEnabled(true);
        chkFCondicaoPagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCondicaoPagamentoActionPerformed(evt);
            }
        });

        chkContasPagar.setText("Contas a Pagar");

        chkSituacaoCadastroForn.setText("Situacao Cadastro");

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkSituacaoCadastroForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(279, Short.MAX_VALUE))
        );
        vRPanel8Layout.setVerticalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSituacaoCadastroForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(111, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Fornecedores", vRPanel8);

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
                .addContainerGap(397, Short.MAX_VALUE))
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
                .addContainerGap(162, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Convênio", tabConvenio);

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
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPdvVendaDatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        vRPanel4.setPreferredSize(new java.awt.Dimension(390, 137));

        btnMapDivergencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMapDivergencias.setText("Divergências");
        btnMapDivergencias.setEnabled(false);
        btnMapDivergencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapDivergenciasActionPerformed(evt);
            }
        });

        chkMapearProdutoEAN.setText("Mapear produto não cadastrado (EAN)");
        chkMapearProdutoEAN.setEnabled(false);

        chkVerificarCodigoBarras.setText("Verificar Código Barras");
        chkVerificarCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVerificarCodigoBarrasActionPerformed(evt);
            }
        });

        chkVerificarCodAnterior.setText("Verificar Código Anterior");

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel4Layout.createSequentialGroup()
                        .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkVerificarCodAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkVerificarCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(119, 119, 119))
                    .addGroup(vRPanel4Layout.createSequentialGroup()
                        .addComponent(chkMapearProdutoEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMapDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVerificarCodAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVerificarCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMapearProdutoEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMapDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Vendas", tabVendas);

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
                .addContainerGap(296, Short.MAX_VALUE))
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
                .addContainerGap(139, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);
        tabs.addTab("Balança", vRImportaArquivBalancaPanel1);

        vRPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Sql Server"));
        vRPanel6.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuario.setText("gwuniao");
        txtUsuario.setCaixaAlta(false);
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        txtSenha.setText("gwuniao");
        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");
        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        vRLabel20.setText("Usuário:");

        vRLabel21.setText("Senha:");

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        txtHost.setText("192.168.1.100");
        txtHost.setCaixaAlta(false);

        vRLabel23.setText("Porta");

        txtPorta.setText("1433");
        txtPorta.setCaixaAlta(false);
        txtPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

        vRLabel24.setText("Host");

        vRLabel25.setText("Banco de Dados");

        vRTextField1.setText("vRTextField1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsConn.addTab("Dados da conexão", jPanel4);

        jLabel2.setText("Loja Origem");

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsConn)
                    .addGroup(vRPanel6Layout.createSequentialGroup()
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void chkClienteEventualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClienteEventualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClienteEventualActionPerformed

    private void chkRotativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRotativoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRotativoActionPerformed

    private void chkChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkChequeActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            this.setWaitCursor();

            if (connSqlServer != null) {
                connSqlServer.close();
            }

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    private void txtPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaActionPerformed

    private void chkFContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFContatosActionPerformed

    private void chkFPrazoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFPrazoFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFPrazoFornecedorActionPerformed

    private void chkFCondicaoPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCondicaoPagamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFCondicaoPagamentoActionPerformed

    private void chkConvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvEmpresaActionPerformed

    private void chkConvConveniadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvConveniadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvConveniadoActionPerformed

    private void chkConvRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvRecebimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvRecebimentoActionPerformed

    private void chkValidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkValidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkValidadeActionPerformed

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

    private void btnMapaTribActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTribActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMapaTribActionPerformed

    private void btnMapDivergenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapDivergenciasActionPerformed
        ItensNaoExistenteGetWayGUI.exibir(this.mdiFrame);
    }//GEN-LAST:event_btnMapDivergenciasActionPerformed

    private void chkVerificarCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVerificarCodigoBarrasActionPerformed
        chkMapearProdutoEAN.setEnabled(chkVerificarCodigoBarras.isSelected());
        chkMapearProdutoEAN.setSelected(chkVerificarCodigoBarras.isSelected());
        btnMapDivergencias.setEnabled(chkVerificarCodigoBarras.isSelected());
    }//GEN-LAST:event_chkVerificarCodigoBarrasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrframework.bean.button.VRButton btnMapDivergencias;
    private vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton btnMapaTrib;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkCategoria;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkContasPagar;
    private vrframework.bean.checkBox.VRCheckBox chkConvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkConvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkConvRecebimento;
    private vrframework.bean.checkBox.VRCheckBox chkCustoComImposto;
    private vrframework.bean.checkBox.VRCheckBox chkCustoSemImposto;
    private vrframework.bean.checkBox.VRCheckBox chkFCondicaoPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFPrazoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkMapearProdutoEAN;
    private vrframework.bean.checkBox.VRCheckBox chkMargem;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalFilizola;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalToledo;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPagamentoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPermiteRotativoCheque;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkReceberVerba;
    private vrframework.bean.checkBox.VRCheckBox chkRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastroForn;
    private vrframework.bean.checkBox.VRCheckBox chkT1AtivoInativo;
    private vrframework.bean.checkBox.VRCheckBox chkT1CEST;
    private vrframework.bean.checkBox.VRCheckBox chkT1Custo;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescCompleta;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescGondola;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescReduzida;
    private vrframework.bean.checkBox.VRCheckBox chkT1EAN;
    private vrframework.bean.checkBox.VRCheckBox chkT1EANemBranco;
    private vrframework.bean.checkBox.VRCheckBox chkT1Estoque;
    private vrframework.bean.checkBox.VRCheckBox chkT1ICMS;
    private vrframework.bean.checkBox.VRCheckBox chkT1NCM;
    private vrframework.bean.checkBox.VRCheckBox chkT1NatReceita;
    private vrframework.bean.checkBox.VRCheckBox chkT1PisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkT1Preco;
    private vrframework.bean.checkBox.VRCheckBox chkT1ProdMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkTemArquivoBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkTemFicha;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkUsarMargemBruta;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private vrframework.bean.checkBox.VRCheckBox chkValorLimite;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodAnterior;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodigoBarras;
    private vrframework.bean.checkBox.VRCheckBox chkreceberDevolucao;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private javax.swing.JComboBox cmbTipoDocCheque;
    private javax.swing.JComboBox cmbTipoDocRotativo;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private javax.swing.JPanel tabConvenio;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private javax.swing.JTabbedPane tabsConn;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private vrframework.bean.textField.VRTextField txtDatabase;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel20;
    private vrframework.bean.label.VRLabel vRLabel21;
    private vrframework.bean.label.VRLabel vRLabel23;
    private vrframework.bean.label.VRLabel vRLabel24;
    private vrframework.bean.label.VRLabel vRLabel25;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    private vrframework.bean.textArea.VRTextArea vRTextArea1;
    private vrframework.bean.textField.VRTextField vRTextField1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

    

}
