package vrimplantacao.gui.interfaces;

import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import org.json.JSONArray;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoProdutoFornecedor;
import vrimplantacao2.dao.cadastro.notafiscal.OpcaoNotaFiscal;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;

public class AriusGUI extends VRInternalFrame {    
    
    private static final String NOME_SISTEMA = "Arius";
    private static final String SERVIDOR_SQL = "Oracle";
    private static AriusGUI instance;
    
    private AriusDAO ariusDAO = new AriusDAO();   
    private final ConexaoOracle connSQL = new ConexaoOracle();
    
    private int vLojaCliente = -1;
    private int vLojaVR = -1;
    private int vTipoVenda = -1;
    private int vEstoque = -1;
    
    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtHostOracle.setText(params.getWithNull("LOCALHOST", "ARIUS", "HOST"));
        txtBancoDadosOracle.setText(params.getWithNull("ARIUS","ARIUS", "DATABASE"));
        txtPortaOracle.setText(params.getWithNull("1521","ARIUS", "PORTA"));
        txtUsuarioOracle.setText(params.getWithNull("PROREG", "ARIUS", "USUARIO"));
        txtSenhaOracle.setText(params.getWithNull("automa", "ARIUS", "SENHA"));
        txtDtVencContasPagar.setDate(params.getDate("ARIUS", "DT_CONTA_PAGAR"));
        txtDataFimOferta.setDate(params.getDate(NOME_SISTEMA, "DATA_FIM_OFERTA"));
        vLojaCliente = params.getInt("ARIUS", "LOJA_CLIENTE");
        vLojaVR = params.getInt("ARIUS", "LOJA_VR");
        vTipoVenda = params.getInt("ARIUS", "TIPO_VENDA");
        vEstoque = params.getInt("ARIUS", "ESTOQUE");
        chkClClientes.setSelected(false);
        chkClEmpresas.setSelected(false);
        chkClFornecedores.setSelected(false);
        chkClTransp.setSelected(false);
        chkClAdminCard.setSelected(false);
        JSONArray array = new JSONArray(params.getWithNull("[]", "ARIUS", "OPCOES_CLIENTE"));
        for (int i = 0; i < array.length(); i++) {
            switch (array.getInt(i)) {
                case 0: chkClClientes.setSelected(true); break;
                case 1: chkClEmpresas.setSelected(true); break;
                case 2: chkClFornecedores.setSelected(true); break;
                case 3: chkClTransp.setSelected(true); break;
                case 4: chkClAdminCard.setSelected(true); break;
            }
        }
    }
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtHostOracle.getText(), "ARIUS", "HOST");
        params.put(txtBancoDadosOracle.getText(), "ARIUS", "DATABASE");
        params.put(txtPortaOracle.getText(), "ARIUS", "PORTA");
        params.put(txtUsuarioOracle.getText(), "ARIUS", "USUARIO");
        params.put(txtSenhaOracle.getText(), "ARIUS", "SENHA");
        params.put(txtDtVencContasPagar.getDate(), "ARIUS", "DT_CONTA_PAGAR");
        params.put((txtDataFimOferta.getDate() != null ? new java.sql.Date(txtDataFimOferta.getDate().getTime()) : null), NOME_SISTEMA, "DATA_FIM_OFERTA");
        ItemComboVO cliente = (ItemComboVO) cmbLojaCliente.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.id, "ARIUS", "LOJA_CLIENTE");
            vLojaCliente = cliente.id;
        }
        ItemComboVO tipoVenda = (ItemComboVO) cmbTipoVenda.getSelectedItem();
        if (tipoVenda != null) {
            params.put(tipoVenda.id, "ARIUS", "TIPO_VENDA");
            vTipoVenda = tipoVenda.id;
        }
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, "ARIUS", "LOJA_VR");
            vLojaVR = vr.id;
        }
        ItemComboVO estoque = (ItemComboVO) cmbEstoque.getSelectedItem();
        if (estoque != null) {
            params.put(estoque.id, "ARIUS", "ESTOQUE");
            vEstoque = estoque.id;
        }
        JSONArray array = new JSONArray();
        if (chkClClientes.isSelected()) { array.put(0); }
        if (chkClEmpresas.isSelected()) { array.put(1); }
        if (chkClFornecedores.isSelected()) { array.put(2); }
        if (chkClTransp.isSelected()) { array.put(3); }
        if (chkClAdminCard.isSelected()) { array.put(4); }
        params.put(array.toString(), "ARIUS", "OPCOES_CLIENTES");
        
        params.salvar();
    }
    

    private AriusGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        this.title = "Importação " + NOME_SISTEMA;
                
        cmbLojaCliente.setModel(new DefaultComboBoxModel());

        carregarParametros();
        
        btnMapaTrib.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return ariusDAO;
            }

            @Override
            public String getSistema() {
                return NOME_SISTEMA;
            }

            @Override
            public String getLoja() {
                return String.valueOf(vLojaCliente);
            }

            @Override
            public Frame getFrame() {
                return mdiFrame;
            }
        });
        
        centralizarForm();
        this.setMaximum(false);
        
        tabRotativo.pnlLista.setEnabled(false);
        tabRotativo.chkAtivar.setEnabled(false);
        
        tabCheque.pnlLista.setEnabled(false);
        tabCheque.chkAtivarCheque.setEnabled(false);

        txtDtVencContasPagar.setVisible(false);
        txtDtVencContasPagar.setFormats("dd/MM/yyyy");
        txtDtIInicioVenda.setFormats("dd/MM/yyyy");
        txtDtTerminoVenda.setFormats("dd/MM/yyyy");
        txtDataFimOferta.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
    }

    public void validarDadosAcessoOracle() throws Exception {
        if (txtHostOracle.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados " + SERVIDOR_SQL);
        }
        if (txtBancoDadosOracle.getText().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados " + SERVIDOR_SQL);
        }

        if (txtSenhaOracle.getText().isEmpty()) {
            throw new VRException("Favor informar a senha do banco de dados " + SERVIDOR_SQL);
        }

        if (txtUsuarioOracle.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + SERVIDOR_SQL);
        }
        
        ConexaoOracle.abrirConexao(txtHostOracle.getText(), txtPortaOracle.getInt(), 
                txtBancoDadosOracle.getText(), txtUsuarioOracle.getText(), txtSenhaOracle.getText());
        
        carregarLojaVR();
        carregarLojaCliente();
        carregarTipoVenda();
        carregarEstoques();
        
        gravarParametros();
        btnMapaTrib.setEnabled(true);
        tabRotativo.chkAtivar.setEnabled(true);
        tabRotativo.setDao(ariusDAO);
        
        tabCheque.chkAtivarCheque.setEnabled(true);
        tabCheque.setDao(ariusDAO);
    }
    
    public void carregarEstoques() throws Exception {
        cmbEstoque.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (ItemComboVO estoque : ariusDAO.getEstoques()) {
            cmbEstoque.addItem(new ItemComboVO(estoque.id, estoque.descricao));
            if (estoque.id == vEstoque) {
                index = cont;
            }
            cont++;
        }
        cmbEstoque.setSelectedIndex(index);
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
        cmbLojaCliente.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (ItemComboVO loja: ariusDAO.getLojasCliente()) {
            cmbLojaCliente.addItem(loja);
            if (loja.id == vLojaCliente) {
                index = cont;
            }
            cont++;
        }
        cmbLojaCliente.setSelectedIndex(index);
    }
    
    public void carregarTipoVenda() throws Exception {
        cmbTipoVenda.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (ItemComboVO tipoVenda: ariusDAO.getTipoVenda()) {
            cmbTipoVenda.addItem(tipoVenda);
            if (tipoVenda.id == vTipoVenda) {
                index = cont;
            }
            cont++;
        }
        cmbTipoVenda.setSelectedIndex(index);
    }
    
    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR, idLojaCliente, balanca; ;            
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);                   
                    
                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;                                        
                    idLojaCliente = ((ItemComboVO) cmbLojaCliente.getSelectedItem()).id;                                        
                    int idTipoVenda = ((ItemComboVO) cmbTipoVenda.getSelectedItem()).id;                                        
                    
                    Importador importador = new Importador(ariusDAO);
                    importador.setLojaOrigem(String.valueOf(idLojaCliente));
                    importador.setLojaVR(idLojaVR);
                    
                    ariusDAO.setImportarDeClientes(chkClClientes.isSelected());
                    ariusDAO.setImportarDeEmpresas(chkClEmpresas.isSelected());
                    ariusDAO.setImportarDeFornecedores(chkClFornecedores.isSelected());
                    ariusDAO.setImportarDeTransportadoras(chkClTransp.isSelected());
                    ariusDAO.setImportarDeAdminCartao(chkClAdminCard.isSelected());
                    ariusDAO.setTipoVenda(idTipoVenda);
                    ariusDAO.setEstoque(((ItemComboVO) cmbEstoque.getSelectedItem()).id);
                    ariusDAO.naoUtilizaPlanoConta = chkNUtilizaPlanoConta.isSelected();
                    
                    if (tab.getSelectedIndex() == 0) {
                        if (chkFamiliaProduto.isSelected()) {                        
                            importador.importarFamiliaProduto();
                        }

                        if (chkMercadologico.isSelected()) {
                            //importador.importarMercadologicoPorNiveis();
                            importador.importarMercadologico();
                        }

                        if (chkProdutos.isSelected()) {
                            importador.importarProduto(chkManterBalanca.isSelected());
                        }
                        
                        if (chkPautaFiscal.isSelected()) {
                            importador.importarPautaFiscal(OpcaoFiscal.NOVOS);
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
                            if (chkQtdEmbCotacao.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_COTACAO);
                            }
                            if (chkMargem.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }
                            if (chkTipoEmbalagem.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO);
                            }
                            if (chkIcmsFornecedor.isSelected()){
                                opcoes.add(OpcaoProduto.ICMS_FORNECEDOR);
                            }
                            if (chkPautaFiscalProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.EXCECAO);
                            }
                            if (chkTrocaCompl.isSelected()) {
                                opcoes.add(OpcaoProduto.TROCA);
                            }
                            if (chkNutricionalProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.NUTRICIONAL);
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
                        
                        if (chkNutricional.isSelected()) {
                            importador.importarNutricional(OpcaoNutricional.FILIZOLA, OpcaoNutricional.TOLEDO);
                        }
                        
                        if (chkReceitaProduto.isSelected()) {
                            importador.importarReceitas();
                        }
                        
                        if (chkAssociado.isSelected()) {
                            importador.importarAssociado();
                        }
                        if (chkOfertas.isSelected()){
                                importador.importarOfertas(txtDataFimOferta.getDate());
                            }
                        {
                            List<OpcaoReceitaBalanca> opcoes = new ArrayList<>();
                            if (chkReceitaFilizola.isSelected()) {
                                opcoes.add(OpcaoReceitaBalanca.FILIZOLA);
                            }
                            if (chkReceitaToledo.isSelected()) {
                                opcoes.add(OpcaoReceitaBalanca.TOLEDO);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.importarReceitaBalanca(opcoes.toArray(new OpcaoReceitaBalanca[]{}));
                            }
                        }
                                
                    } else if (tab.getSelectedIndex() == 1) {
                        if (chkFornecedor.isSelected()) {
                            ariusDAO.setImportarDeTransportadoras(chkIncluirTransportadores.isSelected());
                            importador.importarFornecedor();
                        }
                        
                        List<OpcaoFornecedor> opcoes = new ArrayList<>();
                        {
                            if (chkContatos.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONTATOS);
                            }
                            if (chkFTipoEmpresa.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                            }
                            if (chkCondPagamento.isSelected()) {
                                opcoes.add(OpcaoFornecedor.CONDICAO_PAGAMENTO);
                            }
                            if (chkObservacao.isSelected()) {
                                opcoes.add(OpcaoFornecedor.OBSERVACAO);
                            }
                            if (chkNumero.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NUMERO);
                            }
                            if (chkPrazoFornecedor.isSelected()) {
                                opcoes.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
                            }
                        }
                        
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }
                        
                        if (chkFornecedorDivisao.isSelected()) {
                            importador.importarDivisoes();
                        }
                        
                        List<OpcaoProdutoFornecedor> opcoesFor = new ArrayList<>();
                        {
                            if(chkIPI.isSelected()) {
                                opcoesFor.add(OpcaoProdutoFornecedor.IPI);
                            }
                            if(chkQtdeEmb.isSelected()) {
                                opcoesFor.add(OpcaoProdutoFornecedor.QTDEMBALAGEM);
                            }
                        }
                        
                        if(!opcoesFor.isEmpty()) {
                            importador.atualizarProdutoFornecedor(opcoesFor);
                        }                        
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }                        
                        if (chkFamiliaFornecedor.isSelected()) {
                            ariusDAO.importarFamiliaFornecedor();
                        }
                        if (chkFornecedorXFamilia.isSelected()) {
                            ariusDAO.importarFamiliaFornecedorXProduto();
                        }
                        if (chkContasAPagar.isSelected()) {
                            ariusDAO.setDataVencimentoContaPagar(txtDtVencContasPagar.getDate());
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }
                        if (chkReceberDevForn.isSelected()) {
                            ariusDAO.importarReceberDevolucao(idLojaVR);
                        }
                    } else if (tab.getSelectedIndex() == 2) {
                        if (chkClientePreferencial.isSelected()) {                            
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }
                        if (chkClienteEventual.isSelected()) {                            
                            importador.importarClienteEventual(OpcaoCliente.DADOS, OpcaoCliente.CONTATOS);
                        }                        
                        if (tabRotativo.isSelected()) {
                            ariusDAO.setPlanoContaEntrada(tabRotativo.getPlanosSelecionados());
                            importador.importarCreditoRotativo();
                        }
                        if (tabCheque.isSelected()) {
                            ariusDAO.setPlanoContaEntrada(tabCheque.getPlanosSelecionados());
                            importador.importarCheque();
                        }
                        List<OpcaoCliente> opcoes = new ArrayList<>();
                        if (chkEstadoCivil.isSelected()) {
                            opcoes.add(OpcaoCliente.ESTADO_CIVIL);
                        }
                        if (chkAtualizaLimite.isSelected()) {
                            opcoes.add(OpcaoCliente.VALOR_LIMITE);
                        }
                        if (!opcoes.isEmpty()) {
                            importador.atualizarClientePreferencial(opcoes.toArray(new OpcaoCliente[]{}));
                        }
                    } else if (tab.getSelectedIndex() == 3) {
                        if (cbxUnifProdutos.isSelected()) {  
                            importador.unificarProdutos();
                        }
                        if (cbxUnifFornecedores.isSelected()) {  
                            importador.unificarFornecedor();
                        }
                        if (cbxUnifProdutoForn.isSelected()) {  
                            importador.unificarProdutoFornecedor();
                        }
                        if (cbxUnifCliPreferencial.isSelected()) {  
                            importador.unificarClientePreferencial();
                        }
                        if (cbxUnifCliEventual.isSelected()) {  
                            importador.unificarClienteEventual();
                        }
                    }  else if (tab.getSelectedIndex() == 4) {
                        if (chkPdvVendas.isSelected()) {
                            ariusDAO.setVendaDataInicio(txtDtIInicioVenda.getDate());
                            ariusDAO.setVendaDataTermino(txtDtTerminoVenda.getDate());
                            
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    } else if (tab.getSelectedIndex() == 5) {
                        if(chkConvEmpresa.isSelected()) {
                            importador.importarConvenioEmpresa();
                        }
                        
                        if(chkConvConveniado.isSelected()) {
                            importador.importarConvenioConveniado();
                        }
                    } else if (tab.getSelectedIndex() == 6) {
                        if (chkNotasFiscais.isSelected()) {                            
                            ariusDAO.i_notaEntrada = chkNotaEntrada.isSelected();
                            ariusDAO.i_notaSaida = chkNotaSaida.isSelected();                            
                            ariusDAO.setNotasDataInicio(edtDtNotaIni.getDate());
                            ariusDAO.setNotasDataTermino(edtDtNotaFim.getDate());
                            importador.importarNotas(OpcaoNotaFiscal.IMP_REIMPORTAR_ITENS_DE_NOTAS_IMPORTADAS);
                        }
                        
                    }
                    
                    gravarParametros();
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação " + NOME_SISTEMA + " realizada com sucesso!", getTitle());
                    
                    ConexaoOracle.close();
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
                instance = new AriusGUI(i_mdiFrame);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        vRConsultaContaContabil1 = new vrframework.bean.consultaContaContabil.VRConsultaContaContabil();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabDados = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Custo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Preco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Estoque = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
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
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagem = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkPautaFiscal = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscalProduto = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTrib = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
        chkNutricional = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        cmbEstoque = new vrframework.bean.comboBox.VRComboBox();
        chkNutricionalProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkTrocaCompl = new vrframework.bean.checkBox.VRCheckBox();
        chkAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkIncluirTransportadores = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        chkQtdeEmb = new vrframework.bean.checkBox.VRCheckBox();
        chkIPI = new vrframework.bean.checkBox.VRCheckBox();
        chkFTipoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkFamiliaFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedorXFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkContasAPagar = new vrframework.bean.checkBox.VRCheckBox();
        txtDtVencContasPagar = new org.jdesktop.swingx.JXDatePicker();
        chkCondPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkObservacao = new vrframework.bean.checkBox.VRCheckBox();
        chkNumero = new vrframework.bean.checkBox.VRCheckBox();
        chkFornecedorDivisao = new vrframework.bean.checkBox.VRCheckBox();
        chkPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkReceberDevForn = new vrframework.bean.checkBox.VRCheckBox();
        tabCliente = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabClienteDados = new vrframework.bean.panel.VRPanel();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        chkClClientes = new javax.swing.JCheckBox();
        chkClEmpresas = new javax.swing.JCheckBox();
        chkClFornecedores = new javax.swing.JCheckBox();
        chkClTransp = new javax.swing.JCheckBox();
        chkClAdminCard = new javax.swing.JCheckBox();
        chkEstadoCivil = new vrframework.bean.checkBox.VRCheckBox();
        chkNUtilizaPlanoConta = new vrframework.bean.checkBox.VRCheckBox();
        chkAtualizaLimite = new vrframework.bean.checkBox.VRCheckBox();
        tabRotativo = new vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasRotativoGUI();
        tabCheque = new vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasChequeGUI();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        cbxUnifProdutos = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifProdutoForn = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliPreferencial = new vrframework.bean.checkBox.VRCheckBox();
        cbxUnifCliEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabVendas = new vrframework.bean.panel.VRPanel();
        chkPdvVendas = new vrframework.bean.checkBox.VRCheckBox();
        txtDtIInicioVenda = new org.jdesktop.swingx.JXDatePicker();
        txtDtTerminoVenda = new org.jdesktop.swingx.JXDatePicker();
        tabConv = new javax.swing.JPanel();
        chkConvEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkConvConveniado = new vrframework.bean.checkBox.VRCheckBox();
        chkConvRecebimento = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        chkNotasFiscais = new vrframework.bean.checkBox.VRCheckBox();
        edtDtNotaIni = new org.jdesktop.swingx.JXDatePicker();
        edtDtNotaFim = new org.jdesktop.swingx.JXDatePicker();
        chkNotaEntrada = new vrframework.bean.checkBox.VRCheckBox();
        chkNotaSaida = new vrframework.bean.checkBox.VRCheckBox();
        vRTabbedPane1 = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlConexao = new vrframework.bean.panel.VRPanel();
        txtUsuarioOracle = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtSenhaOracle = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtPortaOracle = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtHostOracle = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnConectarOracle = new javax.swing.JToggleButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtBancoDadosOracle = new vrframework.bean.textField.VRTextField();
        cmbLojaCliente = new vrframework.bean.comboBox.VRComboBox();
        cmbTipoVenda = new vrframework.bean.comboBox.VRComboBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        vRButton1 = new vrframework.bean.button.VRButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setTitle("Arius");
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

        vRLabel6.setText("Loja:");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMigrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

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

        chkT1Custo.setText("Custo");

        chkT1Preco.setText("Preço");

        chkT1Estoque.setText("Estoque");

        chkQtdEmbCotacao.setText("Qtd. Emb. (Cotação)");

        chkManterBalanca.setText("Manter Balança");
        chkManterBalanca.setEnabled(true);

        chkT1EAN.setText("EAN");

        chkT1EANemBranco.setText("EAN em branco");

        chkT1PisCofins.setText("PIS/COFINS");

        chkT1NatReceita.setText("Nat. Receita");

        chkT1ICMS.setText("ICMS");

        chkT1AtivoInativo.setText("Ativo/Inativo");

        chkT1DescCompleta.setText("Descrição Completa");

        chkT1DescReduzida.setText("Descrição Reduzida");

        chkT1DescGondola.setText("Descrição Gondola");

        chkT1ProdMercadologico.setText("Prod. Mercadológico");

        chkValidade.setText("Validade");

        chkAtacado.setText("Atacado");

        chkFamilia.setText("Família");

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");

        chkMargem.setText("Margem");

        chkTipoEmbalagem.setText("Tp. Emb. (Produto)");

        chkIcmsFornecedor.setText("ICMS Crédito por Fornecedor");

        chkPautaFiscal.setText("Pauta Fiscal");

        chkPautaFiscalProduto.setText("Pauta Fiscal x Produto");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnMapaTrib.setEnabled(false);
        btnMapaTrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTribActionPerformed(evt);
            }
        });

        chkNutricional.setText("Nutricional Balança");

        vRLabel9.setText("Estoque");

        chkNutricionalProduto.setText("Nutricional Produto");

        chkTrocaCompl.setText("Troca (Prod. Compl.)");

        chkAssociado.setText("Associado");

        chkReceitaToledo.setText("Receita (Toledo)");

        chkReceitaFilizola.setText("Receita (Filizola)");

        chkReceitaProduto.setText("Receita Produto");

        chkOfertas.setText("Ofertas a partir de: ");

        javax.swing.GroupLayout tabDadosLayout = new javax.swing.GroupLayout(tabDados);
        tabDados.setLayout(tabDadosLayout);
        tabDadosLayout.setHorizontalGroup(
            tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkQtdEmbCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkNutricionalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMapaTrib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkTrocaCompl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkIcmsFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkReceitaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReceitaToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReceitaFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(115, Short.MAX_VALUE))))
        );
        tabDadosLayout.setVerticalGroup(
            tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIcmsFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTrocaCompl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkReceitaFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkReceitaToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkReceitaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 42, Short.MAX_VALUE))
                    .addGroup(tabDadosLayout.createSequentialGroup()
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(tabDadosLayout.createSequentialGroup()
                                    .addGap(46, 46, 46)
                                    .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(tabDadosLayout.createSequentialGroup()
                                    .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(tabDadosLayout.createSequentialGroup()
                                        .addGap(46, 46, 46)
                                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(tabDadosLayout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(tabDadosLayout.createSequentialGroup()
                                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkQtdEmbCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkTipoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(chkNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkNutricionalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(tabDadosLayout.createSequentialGroup()
                                        .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(tabDadosLayout.createSequentialGroup()
                                .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(tabDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMapaTrib, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        tab.addTab("Dados", tabDados);

        tabFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkFornecedor.setText("Fornecedor");
        chkFornecedor.setEnabled(true);
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkContatos.setText("Contatos");
        chkContatos.setEnabled(true);
        chkContatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkContatosActionPerformed(evt);
            }
        });

        chkIncluirTransportadores.setText("Incluir transportadores nos fornecedores");
        chkIncluirTransportadores.setEnabled(true);
        chkIncluirTransportadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncluirTransportadoresActionPerformed(evt);
            }
        });

        chkProdutoFornecedor.setText("Produto Fornecedor");
        chkProdutoFornecedor.setEnabled(true);
        chkProdutoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutoFornecedorActionPerformed(evt);
            }
        });

        vRPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkQtdeEmb.setText("Qtde. Emb. Forn.");

        chkIPI.setText("IPI Fornecedor");

        chkFTipoEmpresa.setText("Tipo Empresa");
        chkFTipoEmpresa.setEnabled(true);
        chkFTipoEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFTipoEmpresaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel4Layout.createSequentialGroup()
                        .addComponent(chkQtdeEmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkIPI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkQtdeEmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkIPI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkFamiliaFornecedor.setText("Família Fornecedor");
        chkFamiliaFornecedor.setEnabled(true);
        chkFamiliaFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFamiliaFornecedorActionPerformed(evt);
            }
        });

        chkFornecedorXFamilia.setText("Fornecedor x Família");
        chkFornecedorXFamilia.setEnabled(true);
        chkFornecedorXFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorXFamiliaActionPerformed(evt);
            }
        });

        chkContasAPagar.setText("Contas à Pagar");
        chkContasAPagar.setEnabled(true);
        chkContasAPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkContasAPagarActionPerformed(evt);
            }
        });

        chkCondPagamento.setText("Condição Pagamento");

        chkObservacao.setText("Observação");

        chkNumero.setText("Número");

        chkFornecedorDivisao.setText("Divisão");
        chkFornecedorDivisao.setEnabled(true);
        chkFornecedorDivisao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorDivisaoActionPerformed(evt);
            }
        });

        chkPrazoFornecedor.setText("Prazo Fornecedor");

        chkReceberDevForn.setText("Receber Devolução");

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkFamiliaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                        .addComponent(chkIncluirTransportadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkReceberDevForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabFornecedorLayout.createSequentialGroup()
                                .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkFornecedorXFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabFornecedorLayout.createSequentialGroup()
                                .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDtVencContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabFornecedorLayout.createSequentialGroup()
                                .addComponent(chkCondPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkFornecedorDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFornecedorLayout.createSequentialGroup()
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFornecedorXFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkIncluirTransportadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkFamiliaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCondPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornecedorDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkContasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtVencContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkReceberDevForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        tab.addTab("Fornecedores", tabFornecedor);

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

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Obter clientes de"));
        vRPanel1.setLayout(new java.awt.GridLayout(5, 1));

        chkClClientes.setSelected(true);
        chkClClientes.setText("Clientes");
        vRPanel1.add(chkClClientes);

        chkClEmpresas.setText("Empresas");
        vRPanel1.add(chkClEmpresas);

        chkClFornecedores.setText("Fornecedores");
        vRPanel1.add(chkClFornecedores);

        chkClTransp.setText("Transportadora");
        vRPanel1.add(chkClTransp);

        chkClAdminCard.setText("Administradora de Cartão");
        vRPanel1.add(chkClAdminCard);

        chkEstadoCivil.setText("Estado Civíl");
        chkEstadoCivil.setEnabled(true);

        chkNUtilizaPlanoConta.setText("Não Utiliza Plano de Conta Rotativo");

        chkAtualizaLimite.setText("Atualiza Limite Cliente");

        javax.swing.GroupLayout tabClienteDadosLayout = new javax.swing.GroupLayout(tabClienteDados);
        tabClienteDados.setLayout(tabClienteDadosLayout);
        tabClienteDadosLayout.setHorizontalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNUtilizaPlanoConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtualizaLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(272, Short.MAX_VALUE))
        );
        tabClienteDadosLayout.setVerticalGroup(
            tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabClienteDadosLayout.createSequentialGroup()
                        .addGroup(tabClienteDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNUtilizaPlanoConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkAtualizaLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabCliente.addTab("Dados", tabClienteDados);
        tabCliente.addTab("Rotativo", tabRotativo);
        tabCliente.addTab("Cheque", tabCheque);

        tab.addTab("Clientes", tabCliente);

        cbxUnifProdutos.setText("Unificar produtos (Somente EANs válidos)");

        cbxUnifFornecedores.setText("Unificar Fornecedores (Somente CPF/CNPJ válidos)");

        cbxUnifProdutoForn.setText("Unificar Produto Fornecedor (Somente CPF/CNPJ válidos)");

        cbxUnifCliPreferencial.setText("Unificar Cliente Preferencial (Somente CPF/CNPJ válidos)");

        cbxUnifCliEventual.setText("Unificar Cliente Eventual (Somente CPF/CNPJ válidos)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifProdutoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(369, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxUnifProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifProdutoForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifCliPreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUnifCliEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(227, Short.MAX_VALUE))
        );

        tab.addTab("Unificação", tabUnificacao);

        chkPdvVendas.setText("Vendas (PDV)");
        chkPdvVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPdvVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabVendasLayout = new javax.swing.GroupLayout(tabVendas);
        tabVendas.setLayout(tabVendasLayout);
        tabVendasLayout.setHorizontalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDtIInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDtTerminoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(387, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPdvVendas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtIInicioVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDtTerminoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(310, Short.MAX_VALUE))
        );

        tab.addTab("Vendas", tabVendas);

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

        javax.swing.GroupLayout tabConvLayout = new javax.swing.GroupLayout(tabConv);
        tabConv.setLayout(tabConvLayout);
        tabConvLayout.setHorizontalGroup(
            tabConvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabConvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkConvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkConvRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(529, Short.MAX_VALUE))
        );
        tabConvLayout.setVerticalGroup(
            tabConvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkConvEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkConvConveniado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkConvRecebimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(273, Short.MAX_VALUE))
        );

        tab.addTab("Convenio", tabConv);

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Importar Notas Fiscais"));

        chkNotasFiscais.setEnabled(true);
        chkNotasFiscais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotasFiscaisActionPerformed(evt);
            }
        });

        chkNotaEntrada.setText("Entrada");
        chkNotaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotaEntradaActionPerformed(evt);
            }
        });

        chkNotaSaida.setText("Saída");
        chkNotaSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNotaSaidaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkNotasFiscais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtDtNotaIni, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtDtNotaFim, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(363, Short.MAX_VALUE))
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkNotasFiscais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtDtNotaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtDtNotaFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(235, Short.MAX_VALUE))
        );

        tab.addTab("Outros", vRPanel5);

        pnlConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - ORACLE"));
        pnlConexao.setPreferredSize(new java.awt.Dimension(350, 350));

        txtUsuarioOracle.setCaixaAlta(false);
        txtUsuarioOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioOracleActionPerformed(evt);
            }
        });

        vRLabel4.setText("Usuário:");

        txtSenhaOracle.setCaixaAlta(false);
        txtSenhaOracle.setMascara("");
        txtSenhaOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaOracleActionPerformed(evt);
            }
        });

        vRLabel5.setText("Senha:");

        txtPortaOracle.setText("1521");
        txtPortaOracle.setCaixaAlta(false);
        txtPortaOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaOracleActionPerformed(evt);
            }
        });

        vRLabel7.setText("Porta");

        vRLabel3.setText("Banco de Dados");

        txtHostOracle.setCaixaAlta(false);

        vRLabel2.setText("Host:");

        btnConectarOracle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        btnConectarOracle.setText("Conectar");
        btnConectarOracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarOracleActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja (Cliente):");

        txtBancoDadosOracle.setCaixaAlta(false);

        cmbLojaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaClienteActionPerformed(evt);
            }
        });

        vRLabel8.setText("Tipo Venda");

        javax.swing.GroupLayout pnlConexaoLayout = new javax.swing.GroupLayout(pnlConexao);
        pnlConexao.setLayout(pnlConexaoLayout);
        pnlConexaoLayout.setHorizontalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPortaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(cmbLojaCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnConectarOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlConexaoLayout.createSequentialGroup()
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlConexaoLayout.createSequentialGroup()
                                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 262, Short.MAX_VALUE))
                            .addComponent(txtHostOracle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBancoDadosOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuarioOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlConexaoLayout.setVerticalGroup(
            pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoLayout.createSequentialGroup()
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtSenhaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuarioOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBancoDadosOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHostOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConexaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLojaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPortaOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectarOracle)))
        );

        vRTabbedPane1.addTab("Conexão", pnlConexao);
        vRTabbedPane1.addTab("Importar Balança", pnlBalanca);

        vRButton1.setText("vRButton1");
        vRButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vRButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(vRButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vRTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void txtUsuarioOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioOracleActionPerformed

    private void txtSenhaOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaOracleActionPerformed

    private void txtPortaOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaOracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaOracleActionPerformed

    private void btnConectarOracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarOracleActionPerformed
        try {
            this.setWaitCursor();

            if (connSQL != null) {
                ConexaoOracle.close();
            }

            validarDadosAcessoOracle();
            btnConectarOracle.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarOracleActionPerformed

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

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

    private void chkPdvVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPdvVendasActionPerformed
        if (txtDtIInicioVenda.getDate() == null) {
            txtDtIInicioVenda.setDate(new Date(System.currentTimeMillis()));
        }
        if (txtDtTerminoVenda.getDate() == null) {
            txtDtTerminoVenda.setDate(new Date(System.currentTimeMillis()));
        }
    }//GEN-LAST:event_chkPdvVendasActionPerformed

    private void cmbLojaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaClienteActionPerformed
        pnlBalanca.setSistema("ARIUS");
        pnlBalanca.setLoja(String.valueOf(((ItemComboVO) cmbLojaCliente.getSelectedItem()).id));
    }//GEN-LAST:event_cmbLojaClienteActionPerformed

    private void chkContatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkContatosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkContatosActionPerformed

    private void btnMapaTribActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTribActionPerformed

    }//GEN-LAST:event_btnMapaTribActionPerformed

    private void chkIncluirTransportadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncluirTransportadoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkIncluirTransportadoresActionPerformed

    private void chkFamiliaFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFamiliaFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFamiliaFornecedorActionPerformed

    private void chkFornecedorXFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorXFamiliaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorXFamiliaActionPerformed

    private void chkContasAPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkContasAPagarActionPerformed
        txtDtVencContasPagar.setEnabled(chkContasAPagar.isSelected());
        if (txtDtVencContasPagar.getDate() == null) {
            txtDtVencContasPagar.setDate(new Date(System.currentTimeMillis()));
        }
    }//GEN-LAST:event_chkContasAPagarActionPerformed

    private void chkFTipoEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFTipoEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFTipoEmpresaActionPerformed

    private void chkConvEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvEmpresaActionPerformed

    private void chkConvConveniadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvConveniadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvConveniadoActionPerformed

    private void chkConvRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConvRecebimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConvRecebimentoActionPerformed

    private void chkFornecedorDivisaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorDivisaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFornecedorDivisaoActionPerformed

    private void chkNotasFiscaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotasFiscaisActionPerformed
        if (edtDtNotaIni.getDate() == null) {
            edtDtNotaIni.setDate(new Date());
        }
    }//GEN-LAST:event_chkNotasFiscaisActionPerformed

    private void chkNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotaEntradaActionPerformed
        // TODO add your handling code here:
        if (chkNotaEntrada.isSelected()) {
            chkNotaSaida.setSelected(false);
        }
    }//GEN-LAST:event_chkNotaEntradaActionPerformed

    private void chkNotaSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNotaSaidaActionPerformed
        // TODO add your handling code here:
        if (chkNotaSaida.isSelected()) {
            chkNotaEntrada.setSelected(false);
        }
    }//GEN-LAST:event_chkNotaSaidaActionPerformed

    private void vRButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vRButton1ActionPerformed
        new Thread() {
            public void run() {
                try (Statement st = ConexaoOracle.createStatement()) {                    
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    ProgressBar.setStatus("Notas de Entrada - Executando SQL");
                    try (ResultSet rs = st.executeQuery(
                            "select\n" +
                            "    er.ID_EMPRESA id_empresa,\n" +
                            "    id_lancamento_nota_fiscal id_novo,\n" +
                            "    nfe.id_entrada_recebimento||\n" +
                            "    er.id_participante||\n" +
                            "    nfe.numero_nota_fiscal||\n" +
                            "    to_char(to_date(er.data_hora_entrada),'YYYY-MM-DD HH24:MI:SS')||'.0' id_antigo\n" +
                            "from\n" +
                            "    arius.rec_t_entrada_nota nfe\n" +
                            "    join arius.rec_t_entrada_recebimento er on\n" +
                            "        nfe.id_entrada_recebimento = er.id_entrada_recebimento\n" +
                            "where\n" +
                            "    to_date(er.data_hora_entrada) between '01/01/2017' and '01/01/2020'"
                    )) {
                        Conexao.begin();
                        String sql = "";
                        try (Statement pg = Conexao.createStatement()) {
                            int cont = 0, aux = 0;
                            while (rs.next()) {
                                sql = String.format(
                                        "insert into implantacao.ids_notas_entradas values (%d,%d,'%s')",
                                        rs.getInt("id_empresa"),
                                        rs.getInt("id_novo"),
                                        rs.getString("id_antigo")
                                );
                                pg.execute(sql);
                                cont++;
                                aux++;
                                if (aux == 1000) {
                                    aux = 0;
                                    ProgressBar.setStatus("Notas de Entrada - Qtd. processada : " + cont);
                                }
                            }
                            Conexao.commit();
                        } catch (Exception e) {
                            System.out.println(sql);
                            Conexao.rollback();
                            throw e;
                        }
                    } finally {
                        ProgressBar.dispose();
                    }
                                        
                    ProgressBar.setStatus("Notas de Saída - Executando SQL");
                    try (ResultSet rs = st.executeQuery(
                            "select\n" +
                            "    sai.ID_EMPRESA_A id_empresa,\n" +
                            "    sai.ID_C100 id_novo,\n" +
                            "    sai.id_c100||\n" +
                            "    sai.cod_part||\n" +
                            "    coalesce(cast(sai.num_doc as varchar(20)),'null')||\n" +
                            "    to_char(to_date(sai.dt_e_s),'YYYY-MM-DD HH24:MI:SS')||'.0' id_antigo\n" +
                            "from\n" +
                            "    arius.fis_t_c100 sai\n" +
                            "    left join arius.fis_t_notas_nfe nfe on\n" +
                            "        nfe.id_nota = sai.id_c100\n" +
                            "where\n" +
                            "    sai.IND_OPER = 1 and\n" +
                            "    to_date(sai.dt_e_s) between '01/01/2017' and '01/01/2020'\n" +
                            "order by\n" +
                            "    id_novo"
                    )) {
                        Conexao.begin();
                        String sql = "";
                        try (Statement pg = Conexao.createStatement()) {
                            int cont = 0, aux = 0;
                            while (rs.next()) {
                                sql = String.format(
                                        "insert into implantacao.ids_notas_saida values (%d,%d,'%s')",
                                        rs.getInt("id_empresa"),
                                        rs.getInt("id_novo"),
                                        rs.getString("id_antigo")
                                );
                                pg.execute(sql);
                                cont++;
                                aux++;
                                if (aux == 1000) {
                                    aux = 0;
                                    ProgressBar.setStatus("Notas de Saída - Qtd. processada : " + cont);
                                }
                            }
                            Conexao.commit();
                        } catch (Exception e) {
                            System.out.println(sql);
                            Conexao.rollback();
                            throw e;
                        }
                    } finally {
                        ProgressBar.dispose();
                    }
                } catch (Exception e) {
                    Util.exibirMensagemErro(e, "Erro");
                }
            }
        }.start();
    }//GEN-LAST:event_vRButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectarOracle;
    private vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton btnMapaTrib;
    private vrframework.bean.button.VRButton btnMigrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliEventual;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifCliPreferencial;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifFornecedores;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutoForn;
    private vrframework.bean.checkBox.VRCheckBox cbxUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkAssociado;
    private vrframework.bean.checkBox.VRCheckBox chkAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkAtualizaLimite;
    private javax.swing.JCheckBox chkClAdminCard;
    private javax.swing.JCheckBox chkClClientes;
    private javax.swing.JCheckBox chkClEmpresas;
    private javax.swing.JCheckBox chkClFornecedores;
    private javax.swing.JCheckBox chkClTransp;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCondPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkContasAPagar;
    private vrframework.bean.checkBox.VRCheckBox chkContatos;
    private vrframework.bean.checkBox.VRCheckBox chkConvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkConvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkConvRecebimento;
    private vrframework.bean.checkBox.VRCheckBox chkEstadoCivil;
    private vrframework.bean.checkBox.VRCheckBox chkFTipoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedorDivisao;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedorXFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkIPI;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkIncluirTransportadores;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkMargem;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkNUtilizaPlanoConta;
    private vrframework.bean.checkBox.VRCheckBox chkNotaEntrada;
    private vrframework.bean.checkBox.VRCheckBox chkNotaSaida;
    private vrframework.bean.checkBox.VRCheckBox chkNotasFiscais;
    private vrframework.bean.checkBox.VRCheckBox chkNumero;
    private vrframework.bean.checkBox.VRCheckBox chkNutricional;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalProduto;
    private vrframework.bean.checkBox.VRCheckBox chkObservacao;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPautaFiscal;
    private vrframework.bean.checkBox.VRCheckBox chkPautaFiscalProduto;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPrazoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbCotacao;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkQtdeEmb;
    private vrframework.bean.checkBox.VRCheckBox chkReceberDevForn;
    private vrframework.bean.checkBox.VRCheckBox chkReceitaFilizola;
    private vrframework.bean.checkBox.VRCheckBox chkReceitaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkReceitaToledo;
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
    private vrframework.bean.checkBox.VRCheckBox chkT1ProdMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagem;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkTrocaCompl;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private vrframework.bean.comboBox.VRComboBox cmbEstoque;
    private vrframework.bean.comboBox.VRComboBox cmbLojaCliente;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private vrframework.bean.comboBox.VRComboBox cmbTipoVenda;
    private org.jdesktop.swingx.JXDatePicker edtDtNotaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtNotaIni;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private vrframework.bean.panel.VRPanel pnlConexao;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasChequeGUI tabCheque;
    private vrframework.bean.tabbedPane.VRTabbedPane tabCliente;
    private vrframework.bean.panel.VRPanel tabClienteDados;
    private javax.swing.JPanel tabConv;
    private vrframework.bean.panel.VRPanel tabDados;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrimplantacao2.gui.interfaces.custom.arius.AriusPlanoContasRotativoGUI tabRotativo;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.textField.VRTextField txtBancoDadosOracle;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private org.jdesktop.swingx.JXDatePicker txtDtIInicioVenda;
    private org.jdesktop.swingx.JXDatePicker txtDtTerminoVenda;
    private org.jdesktop.swingx.JXDatePicker txtDtVencContasPagar;
    private vrframework.bean.textField.VRTextField txtHostOracle;
    private vrframework.bean.textField.VRTextField txtPortaOracle;
    private vrframework.bean.passwordField.VRPasswordField txtSenhaOracle;
    private vrframework.bean.textField.VRTextField txtUsuarioOracle;
    private vrframework.bean.button.VRButton vRButton1;
    private vrframework.bean.consultaContaContabil.VRConsultaContaContabil vRConsultaContaContabil1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.label.VRLabel vRLabel9;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
