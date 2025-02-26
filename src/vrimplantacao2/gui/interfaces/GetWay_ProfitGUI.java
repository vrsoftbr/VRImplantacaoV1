package vrimplantacao2.gui.interfaces;

import java.awt.Frame;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.interfaces.GetWay_ProfitDAO;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.OpcaoFiscal;

public class GetWay_ProfitGUI extends VRInternalFrame {

    private static final String SISTEMA = "GetWay";
    private static final String SERVIDOR_SQL = "Sql Server";
    private static GetWay_ProfitGUI instance;
    private String vLojaCliente = "-1";
    private int vLojaVR = -1;
    private GetWay_ProfitDAO dao = new GetWay_ProfitDAO();
    private ConexaoSqlServer connSqlServer = new ConexaoSqlServer();

    private void carregarTipoDocumento() throws Exception {
        cmbTipoDocRotativo.removeAllItems();
        cmbTipoDocRotativo.setModel(new DefaultComboBoxModel());
        cmbTipoDocCheque.removeAllItems();
        cmbTipoDocCheque.setModel(new DefaultComboBoxModel());
        for (ItemComboVO item : dao.getTipoDocumento()) {
            cmbTipoDocRotativo.addItem(item);
            cmbTipoDocCheque.addItem(item);
        }
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtLojaMesmoID.setText(params.get(SISTEMA, "COMPLEMENTO"));
        txtHost.setText(params.get(SISTEMA, "HOST"));
        txtDatabase.setText(params.get(SISTEMA, "DATABASE"));
        txtPorta.setText(params.get(SISTEMA, "PORTA"));
        txtUsuario.setText(params.get(SISTEMA, "USUARIO"));
        txtSenha.setText(params.get(SISTEMA, "SENHA"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
        chkInverterAssociado.setSelected(params.getBool(SISTEMA, "INVERTER_ASSOCIADO"));
        chkManterBalanca.setSelected(params.getBool(SISTEMA, "MANTER_BALANCA"));
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
        params.put(txtLojaMesmoID.getText(), SISTEMA, "COMPLEMENTO");
        params.put(txtHost.getText(), SISTEMA, "HOST");
        params.put(txtDatabase.getText(), SISTEMA, "DATABASE");
        params.put(txtPorta.getText(), SISTEMA, "PORTA");
        params.put(txtUsuario.getText(), SISTEMA, "USUARIO");
        params.put(txtSenha.getText(), SISTEMA, "SENHA");
        params.put(chkInverterAssociado.isSelected(), SISTEMA, "INVERTER_ASSOCIADO");
        params.put(chkManterBalanca.isSelected(), SISTEMA, "MANTER_BALANCA");
        params.put(chkUsarMargemBruta.isSelected(), SISTEMA, "USAR_MARGEM_BRUTA");
        params.put(chkDesconsiderarSetorBalanca.isSelected(), SISTEMA, "DESCONSIDERAR_SETOR_BALANCA");
        params.put(chkPesquisarKG.isSelected(), SISTEMA, "PESQUISAR_KG_DESCRICAO");
        params.put(chkUsarQtdCotacaoProdFornecedor.isSelected(), SISTEMA, "USAR_QTD_COTACAO_NO_PRODFORN");
        params.put(chkAssociadoSomenteAtivos.isSelected(), SISTEMA, "ASSOCIADO_SOMENTE_ATIVO");
        params.put(chkUtilizarEmbalagemCompra.isSelected(), SISTEMA, "UTILIZAR_EMBALAGEM_DE_COMPRA");
        params.put((txtDataFimOferta.getDate() != null ? new java.sql.Date(txtDataFimOferta.getDate().getTime()) : null), SISTEMA, "DATA_FIM_OFERTA");
        params.put(chkCopiarIcmsDebitoNaEntrada.isSelected(), SISTEMA, "COPIAR_ICMS_DEBITO");

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

    private GetWay_ProfitGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        //ConexaoFirebird.encoding = "WIN1252";        

        this.title = "Importação " + SISTEMA;

        //cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        carregarParametros();
        btnMapaTrib.setProvider(new MapaTributacaoButtonProvider() {
            @Override
            public MapaTributoProvider getProvider() {
                return dao;
            }

            @Override
            public String getSistema() {
                dao.setComplemento(txtLojaMesmoID.getText());
                return dao.getSistema();
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

        edtDtVendaIni.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        edtDtVendaFim.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        txtDataFimOferta.setFormats(new SimpleDateFormat("dd/MM/yyyy"));

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
        for (Estabelecimento loja : dao.getLojas()) {
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
                instance = new GetWay_ProfitGUI(i_mdiFrame);
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
                    idLojaCliente = ((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj;

                    dao.v_tipoDocumento = ((ItemComboVO) cmbTipoDocRotativo.getSelectedItem()).id;
                    dao.v_tipoDocumentoCheque = ((ItemComboVO) cmbTipoDocCheque.getSelectedItem()).id;
                    dao.v_usar_arquivoBalanca = chkTemArquivoBalanca.isSelected();
                    dao.v_usar_arquivoBalancaUnificacao = chkTemArquivoBalancaUnificacao.isSelected();
                    dao.usarMargemBruta = chkUsarMargemBruta.isSelected();
                    dao.usaMargemSobreVenda = chkMargemSobreVenda.isSelected();
                    dao.setUsarQtdEmbDoProduto(chkUsarQtdCotacaoProdFornecedor.isSelected());
                    dao.usaMargemLiquidaPraticada = chkUsaMargemLiquida.isSelected();
                    dao.apenasProdutoAtivo = chkProdutoAtivo.isSelected();
                    dao.utilizaMetodoAjustaAliquota = chkMetodoAliquota.isSelected();
                    dao.copiarDescricaoCompletaParaGondola = chkCopiaDescComplGondola.isSelected();
                    dao.removerCodigoCliente = chkRemoverIDCliente.isSelected();

                    dao.setComplemento(txtLojaMesmoID.getText());
                    dao.utilizaEstoqueMultiLoja = chkMultiLoja.isSelected();
                    dao.utilizaPrecoMultiloja = chkPrecoMultiloja.isSelected();

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);

                    dao.setUtilizarEmbalagemDeCompra(chkUtilizarEmbalagemCompra.isSelected());
                    dao.setCopiarIcmsDebitoNaEntrada(chkCopiarIcmsDebitoNaEntrada.isSelected());

                    if (tabs.getSelectedIndex() == 1) {
                        if (chkFamiliaProduto.isSelected()) {
                            importador.importarFamiliaProduto();
                        }
                        
                        if (chkMercadologico.isSelected()) {
                            if(chkNaoExcluirMercadologico.isSelected()) {
                                importador.importarMercadologico(OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR);
                            } else {
                                importador.importarMercadologico();
                            }
                            
                        }
                        if (chkPauta.isSelected()) {
                            List<OpcaoFiscal> opcoes = new ArrayList<>();
                            
                            opcoes.add(OpcaoFiscal.NOVOS);
                            opcoes.add(OpcaoFiscal.USAR_IDPRODUTO);
                            
                            importador.importarPautaFiscal(opcoes.toArray(new OpcaoFiscal[]{}));
                        }
                        if (chkProdutos.isSelected()) {
                            dao.setDesconsiderarSetorBalanca(chkDesconsiderarSetorBalanca.isSelected());
                            dao.setPesquisarKGnaDescricao(chkPesquisarKG.isSelected());
                            {
                                ArrayList<OpcaoProduto> opcoes = new ArrayList<>();
                                if (chkManterBalanca.isSelected()) {
                                    opcoes.add(OpcaoProduto.IMPORTAR_MANTER_BALANCA);
                                }
                                if (chkManterEAN.isSelected()) {
                                    opcoes.add(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
                                }
                                importador.importarProduto(opcoes.toArray(new OpcaoProduto[]{}));
                            }
                            //importador.importarProduto(chkManterBalanca.isSelected());
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
                                if (chkSomarEstoque.isSelected()) {
                                    opcoes.add(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE);
                                }
                            }
                            if (chkTrocaEstoque.isSelected()) {
                                opcoes.add(OpcaoProduto.TROCA);
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
                            if (chkIcmsEntrada.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_ENTRADA);
                            }
                            if (chkIcmsSaida.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_SAIDA);
                            }
                            if (chkIcmsSaidaNF.isSelected()) {
                                opcoes.add(OpcaoProduto.ICMS_SAIDA_NF);
                            }
                            if (chkT1NCM.isSelected()) {
                                opcoes.add(OpcaoProduto.NCM);
                            }
                            if (chkDescontinuado.isSelected()) {
                                opcoes.add(OpcaoProduto.DESCONTINUADO);
                            }
                            if (chkT1CEST.isSelected()) {
                                opcoes.add(OpcaoProduto.CEST);
                            }
                            if (chkQtdCotacao.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_COTACAO);
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
                            if (chkForcarUnidade.isSelected()) {
                                opcoes.add(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN);
                            }
                            if (chkT1ForcarprecoCusto.isSelected()) {
                                opcoes.add(OpcaoProduto.FORCAR_ATUALIZACAO);
                            }
                            if (chkTipoEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_EAN);
                            }
                            if (chkTipoEmbalagemProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO);
                            }
                            if (chkQtdEmbalagemEAN.isSelected()) {
                                opcoes.add(OpcaoProduto.QTD_EMBALAGEM_EAN);
                            }
                            if (chkMargem.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }

                            if (chkUsaMargemLiquida.isSelected()) {
                                opcoes.add(OpcaoProduto.MARGEM);
                            }

                            if (chkTipoProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.TIPO_PRODUTO);
                            }

                            if (chkDtAlteracao.isSelected()) {
                                opcoes.add(OpcaoProduto.DATA_ALTERACAO);
                            }

                            if (chkPautaProduto.isSelected()) {
                                opcoes.add(OpcaoProduto.EXCECAO);
                            }
                            
                            if (chkCodigoANP.isSelected()) {
                                opcoes.add(OpcaoProduto.CODIGO_ANP);
                            }
                            
                            if (chkPrateleira.isSelected()) {
                                opcoes.add(OpcaoProduto.PRATELEIRA);
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
                        if (chkAssociado.isSelected()) {
                            List<OpcaoAssociado> opcoes = new ArrayList<>();
                            if (chkInverterAssociado.isSelected()) {
                                opcoes.add(OpcaoAssociado.IMP_INVERTER);
                            }
                            if (chkAssociadoSomenteAtivos.isSelected()) {
                                opcoes.add(OpcaoAssociado.IMP_SOMENTE_ATIVOS);
                            }
                            importador.importarAssociado(opcoes.toArray(new OpcaoAssociado[]{}));
                        }
                        if (chkFornecedor.isSelected()) {
                            importador.importarFornecedor();
                        }
                        if (chkProdutoFornecedor.isSelected()) {
                            importador.importarProdutoFornecedor();
                        }
                        {
                            List<OpcaoFornecedor> opcoes = new ArrayList<>();
                            if (chkFIndicadorIe.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TIPO_INDICADOR_IE);
                            }
                            if (chkFTelefone.isSelected()) {
                                opcoes.add(OpcaoFornecedor.TELEFONE);
                            }
                            if (chkFFantasia.isSelected()) {
                                opcoes.add(OpcaoFornecedor.NOME_FANTASIA);
                            }
                            if (!opcoes.isEmpty()) {
                                importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                            }
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
                        if (chkComplemento.isSelected()) {
                            opcoes.add(OpcaoFornecedor.COMPLEMENTO);
                        }
                        if (!opcoes.isEmpty()) {
                            importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                        }
                        if (chkContasPagar.isSelected()) {
                            importador.importarContasPagar(OpcaoContaPagar.NOVOS);
                        }
                        if (chkClientePreferencial.isSelected()) {
                            importador.importarClientePreferencial(OpcaoCliente.DADOS, OpcaoCliente.VALOR_LIMITE,
                                    OpcaoCliente.SITUACAO_CADASTRO);
                        }

                        {
                            List<OpcaoCliente> opt = new ArrayList<>();

                            if (chkValorLimite.isSelected()) {
                                opt.add(OpcaoCliente.VALOR_LIMITE);
                            }
                            
                            if (chkCContato.isSelected()) {
                                opt.add(OpcaoCliente.CONTATOS);
                            }

                            if (!opt.isEmpty()) {
                                importador.atualizarClientePreferencial(opt.toArray(new OpcaoCliente[]{}));
                            }

                        }
                        
                        List<OpcaoCliente> opt = new ArrayList<>();

                        if (chkCContato.isSelected()) {
                            opt.add(OpcaoCliente.CONTATOS);
                        }
                        
                        if (chkClienteEventual.isSelected()) {
                            importador.importarClienteEventual(opt.toArray(new OpcaoCliente[]{}));
                        }

                        if (chkRotativo.isSelected()) {
                            importador.importarCreditoRotativo();
                        }
                        if (chkreceberDevolucao.isSelected()) {
                            dao.importarReceberDevolucao(idLojaVR);
                        }
                        if (chkReceberVerba.isSelected()) {
                            dao.importarReceberVerba(idLojaVR);
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
                            /*strVendaDataInicio = txtVendaDataInicio.getText();
                             strVendaDataFim = txtVendaDataFim.getText();
                            
                             vendaDataInicio = new java.sql.Date(fmt.parse(strVendaDataInicio).getTime());
                             vendaDataFim = new java.sql.Date(fmt.parse(strVendaDataFim).getTime());*/

                            //getWayDAO.setDataInicioVenda(vendaDataInicio);
                            //getWayDAO.setDataTerminoVenda(vendaDataFim);
                            dao.setDataInicioVenda(edtDtVendaIni.getDate());
                            dao.setDataTerminoVenda(edtDtVendaFim.getDate());
                            importador.importarVendas(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR);
                        }
                    } else if (tabs.getSelectedIndex() == 2) {
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

        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
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
        lblLoja = new vrframework.bean.label.VRLabel();
        txtLojaMesmoID = new vrframework.bean.textField.VRTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tabParametros = new javax.swing.JPanel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        chkInverterAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkAssociadoSomenteAtivos = new vrframework.bean.checkBox.VRCheckBox();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkUsarMargemBruta = new vrframework.bean.checkBox.VRCheckBox();
        txtDataFimOferta = new org.jdesktop.swingx.JXDatePicker();
        chkUsarQtdCotacaoProdFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTrib = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
        jLabel3 = new javax.swing.JLabel();
        chkDesconsiderarSetorBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkPesquisarKG = new vrframework.bean.checkBox.VRCheckBox();
        chkForcarUnidade = new vrframework.bean.checkBox.VRCheckBox();
        chkUtilizarEmbalagemCompra = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiarIcmsDebitoNaEntrada = new vrframework.bean.checkBox.VRCheckBox();
        chkMetodoAliquota = new javax.swing.JCheckBox();
        chkSomarEstoque = new javax.swing.JCheckBox();
        chkCopiaDescComplGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkManterEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkNaoExcluirMercadologico = new javax.swing.JCheckBox();
        chkMultiLoja = new vrframework.bean.checkBox.VRCheckBox();
        chkPrecoMultiloja = new vrframework.bean.checkBox.VRCheckBox();
        vRTabbedPane2 = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel7 = new vrframework.bean.panel.VRPanel();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Custo = new vrframework.bean.checkBox.VRCheckBox();
        chkT1Preco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ForcarprecoCusto = new vrframework.bean.checkBox.VRCheckBox();
        chkTrocaEstoque = new javax.swing.JCheckBox();
        chkT1Estoque = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EAN = new vrframework.bean.checkBox.VRCheckBox();
        chkT1EANemBranco = new vrframework.bean.checkBox.VRCheckBox();
        chkT1PisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkT1NatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsEntrada = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsSaidaNF = new javax.swing.JCheckBox();
        chkIcmsSaida = new vrframework.bean.checkBox.VRCheckBox();
        chkT1AtivoInativo = new vrframework.bean.checkBox.VRCheckBox();
        chkDescontinuado = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescReduzida = new vrframework.bean.checkBox.VRCheckBox();
        chkT1DescGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkT1ProdMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoComImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoSemImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkT1NCM = new vrframework.bean.checkBox.VRCheckBox();
        chkOfertas = new vrframework.bean.checkBox.VRCheckBox();
        chkT1CEST = new vrframework.bean.checkBox.VRCheckBox();
        chkTemArquivoBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkDtAlteracao = new vrframework.bean.checkBox.VRCheckBox();
        chkAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoAtivo = new vrframework.bean.checkBox.VRCheckBox();
        chkUsaMargemLiquida = new vrframework.bean.checkBox.VRCheckBox();
        chkMargemSobreVenda = new javax.swing.JCheckBox();
        chkTipoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPauta = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkCodigoANP = new vrframework.bean.checkBox.VRCheckBox();
        chkPrateleira = new vrframework.bean.checkBox.VRCheckBox();
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
        chkRemoverIDCliente = new vrframework.bean.checkBox.VRCheckBox();
        chkCContato = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel8 = new vrframework.bean.panel.VRPanel();
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFContatos = new vrframework.bean.checkBox.VRCheckBox();
        chkFPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkFCondicaoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkContasPagar = new vrframework.bean.checkBox.VRCheckBox();
        chkSituacaoCadastroForn = new vrframework.bean.checkBox.VRCheckBox();
        chkComplemento = new vrframework.bean.checkBox.VRCheckBox();
        chkFIndicadorIe = new vrframework.bean.checkBox.VRCheckBox();
        chkFTelefone = new vrframework.bean.checkBox.VRCheckBox();
        chkFFantasia = new vrframework.bean.checkBox.VRCheckBox();
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
                .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
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
        cmbLojaOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaOrigemActionPerformed(evt);
            }
        });

        lblLoja.setText("Loja ID (Cliente)");

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(vRPanel6Layout.createSequentialGroup()
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtLojaMesmoID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConectar))
                            .addGroup(vRPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE)))))
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
                    .addComponent(jLabel2)
                    .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)
                    .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaMesmoID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        vRImportaArquivBalancaPanel1.setSistema("GetWay");

        chkInverterAssociado.setText("Inverter Associado");
        chkInverterAssociado.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        chkAssociadoSomenteAtivos.setText("Somente produtos ativos (Associado)");
        chkAssociadoSomenteAtivos.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        chkManterBalanca.setText("Manter código PLU dos produtos de balança");
        chkManterBalanca.setEnabled(true);

        chkUsarMargemBruta.setText("Usar Margem Bruta");
        chkUsarMargemBruta.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarMargemBruta.setEnabled(true);

        chkUsarQtdCotacaoProdFornecedor.setText("Usar qtd. de cotação no produto fornecedor");
        chkUsarQtdCotacaoProdFornecedor.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkUsarQtdCotacaoProdFornecedor.setEnabled(true);

        btnMapaTrib.setEnabled(false);
        btnMapaTrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTribActionPerformed(evt);
            }
        });

        jLabel3.setText("Importar ofertas apartir da data");

        chkDesconsiderarSetorBalanca.setText("Desconsiderar setor de balança");
        chkDesconsiderarSetorBalanca.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido,<br>neste caso é necessário utilizar o tipo de embalagem para determinar o que é de KG ou UN.<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkPesquisarKG.setText("Pesquisar flag KG na descrição");
        chkPesquisarKG.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkForcarUnidade.setText("Forçar Unidade");
        chkForcarUnidade.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkUtilizarEmbalagemCompra.setText("Utilizar embalagem de compra");
        chkUtilizarEmbalagemCompra.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido<br> \ne nem o campo tipo de embalagem está correto, neste caso a rotina irá verificar se a palavra KG pode ser encontrado nos produtos<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkCopiarIcmsDebitoNaEntrada.setText("Copiar ICMS débito na entrada");
        chkCopiarIcmsDebitoNaEntrada.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkCopiarIcmsDebitoNaEntrada.setEnabled(true);

        chkMetodoAliquota.setText("Utiliza Método Aliquota");

        chkSomarEstoque.setText("Somar Estoque na Atualização");

        chkCopiaDescComplGondola.setText("Copiar Descrição Completa para Gôndola");

        chkManterEAN.setText("Manter EAN");

        chkNaoExcluirMercadologico.setText("Não excluir Mercadológico");

        chkMultiLoja.setText("Estoque MultiLoja");
        chkMultiLoja.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido,<br>neste caso é necessário utilizar o tipo de embalagem para determinar o que é de KG ou UN.<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        chkPrecoMultiloja.setText("Utiliza Preço MultiLoja");
        chkPrecoMultiloja.setToolTipText("<html>\n<p>Selecione essa opção caso o cliente não utilize setor de balança para determinar quais são os produtos pesáveis.</p>\n<p>Caso o cliente não envie carga para a balança, pode ser que o campo setor de balança não esteja preenchido,<br>neste caso é necessário utilizar o tipo de embalagem para determinar o que é de KG ou UN.<br>\n<b>OBS: Nesta opção, não será possível importar os pesáveis unitários.</b> \n</html>");

        javax.swing.GroupLayout tabParametrosLayout = new javax.swing.GroupLayout(tabParametros);
        tabParametros.setLayout(tabParametrosLayout);
        tabParametrosLayout.setHorizontalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabParametrosLayout.createSequentialGroup()
                        .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabParametrosLayout.createSequentialGroup()
                                .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(tabParametrosLayout.createSequentialGroup()
                                        .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkUsarMargemBruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkUsarQtdCotacaoProdFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkCopiaDescComplGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnMapaTrib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkSomarEstoque)
                                    .addComponent(chkForcarUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkUtilizarEmbalagemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkMetodoAliquota)
                                    .addComponent(chkNaoExcluirMercadologico)
                                    .addComponent(chkManterEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(tabParametrosLayout.createSequentialGroup()
                                        .addComponent(chkDesconsiderarSetorBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkMultiLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tabParametrosLayout.createSequentialGroup()
                                        .addComponent(chkPesquisarKG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(chkPrecoMultiloja, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(tabParametrosLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(78, 78, 78))
                    .addGroup(tabParametrosLayout.createSequentialGroup()
                        .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        tabParametrosLayout.setVerticalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabParametrosLayout.createSequentialGroup()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDesconsiderarSetorBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMultiLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabParametrosLayout.createSequentialGroup()
                        .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUsarMargemBruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUsarQtdCotacaoProdFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCopiaDescComplGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMapaTrib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDataFimOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(tabParametrosLayout.createSequentialGroup()
                        .addGroup(tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkPesquisarKG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPrecoMultiloja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkForcarUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUtilizarEmbalagemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSomarEstoque)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMetodoAliquota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkManterEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkNaoExcluirMercadologico)))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        tabs.addTab("Parâmetros", tabParametros);

        vRPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        vRPanel7.setMaximumSize(new java.awt.Dimension(400, 32767));
        vRPanel7.setMinimumSize(new java.awt.Dimension(400, 35));
        vRPanel7.setPreferredSize(new java.awt.Dimension(400, 35));

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
        chkProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProdutosActionPerformed(evt);
            }
        });

        chkT1Custo.setText("Custo");

        chkT1Preco.setText("Preço");

        chkT1ForcarprecoCusto.setText("(Forçar Preço e/ou Custo)");
        chkT1ForcarprecoCusto.setToolTipText("<html>\n<b>Força</b> a alteração do preço e/ou custo mesmo<br>\nque os mesmos tenham sido alterados no VR.\n</html>");

        chkTrocaEstoque.setText("Estoque de Troca");

        chkT1Estoque.setText("Estoque");

        chkT1EAN.setText("EAN");

        chkT1EANemBranco.setText("EAN em branco");

        chkT1PisCofins.setText("PIS/COFINS");

        chkT1NatReceita.setText("Nat. Receita");

        chkT1ICMS.setText("ICMS");

        chkIcmsEntrada.setText("ICMS (Entrada)");

        chkIcmsSaidaNF.setText("ICMS Saída (NF)");

        chkIcmsSaida.setText("ICMS (Saída)");

        chkT1AtivoInativo.setText("Ativo/Inativo");

        chkDescontinuado.setText("Descontinuado");

        chkT1DescCompleta.setText("Descrição Completa");

        chkT1DescReduzida.setText("Descrição Reduzida");

        chkT1DescGondola.setText("Descrição Gondola");

        chkT1ProdMercadologico.setText("Prod. Mercadológico");

        chkNutricionalFilizola.setText("Nutricional Filizola");

        chkNutricionalToledo.setText("Nutricional Toledo");

        chkValidade.setText("Validade");
        chkValidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkValidadeActionPerformed(evt);
            }
        });

        chkAtacado.setText("Atacado");

        chkFamilia.setText("Família");

        chkTipoEmbalagemProduto.setText("Tipo Emb. Produto");

        chkTipoEmbalagemEAN.setText("Tipo Emb. EAN");

        chkQtdEmbalagemEAN.setText("Qtd. Emb. EAN");

        chkQtdCotacao.setText("Qtd. Cotação");

        chkCustoComImposto.setText("Custo Com Imposto");

        chkCustoSemImposto.setText("Custo Sem Imposto");

        chkT1NCM.setText("NCM");

        chkOfertas.setText("Ofertas");

        chkT1CEST.setText("CEST");

        chkTemArquivoBalanca.setText("Tem Arquivo Balança");

        chkDtAlteracao.setText("Data Alteração");

        chkAssociado.setText("Associado");

        chkMargem.setText("Margem");

        chkProdutoAtivo.setText("Apenas Produtos Ativos");
        chkProdutoAtivo.setToolTipText("Marcando esta opção será importado apenas produtos que estão ativo");

        chkUsaMargemLiquida.setText("Usa Margem Liquida Praticada");

        chkMargemSobreVenda.setText("Margem Sobre Venda");

        chkTipoProduto.setText("Tipo Produto");

        chkPauta.setText("Pauta Fiscal");

        chkPautaProduto.setText("Pauta Fiscal x Produto");

        chkCodigoANP.setText("Código ANP");

        chkPrateleira.setText("Prateleira");

        javax.swing.GroupLayout vRPanel7Layout = new javax.swing.GroupLayout(vRPanel7);
        vRPanel7.setLayout(vRPanel7Layout);
        vRPanel7Layout.setHorizontalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkT1ForcarprecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkTrocaEstoque)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkIcmsEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkIcmsSaidaNF)
                        .addGap(5, 5, 5)
                        .addComponent(chkIcmsSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkNutricionalToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkTipoEmbalagemProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkQtdCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1NCM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkT1CEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkTemArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkDtAlteracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkProdutoAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkUsaMargemLiquida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkMargemSobreVenda)
                        .addGap(5, 5, 5)
                        .addComponent(chkTipoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel7Layout.createSequentialGroup()
                        .addComponent(chkPauta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkPautaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkCodigoANP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        vRPanel7Layout.setVerticalGroup(
            vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1Custo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkT1Preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkT1ForcarprecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkTrocaEstoque)
                    .addComponent(chkT1Estoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1EAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1EANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1PisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkT1NatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1ICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsSaidaNF)
                    .addComponent(chkIcmsSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkT1AtivoInativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1DescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1DescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkT1DescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1ProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkNutricionalToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagemProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkQtdCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1NCM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOfertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkT1CEST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTemArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkDtAlteracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdutoAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUsaMargemLiquida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMargemSobreVenda)
                    .addComponent(chkTipoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(vRPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPauta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPautaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCodigoANP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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

        chkRemoverIDCliente.setText("Remover Código Extra do ID do Cliente");

        chkCContato.setText("Contato");

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
                    .addGroup(vRPanel9Layout.createSequentialGroup()
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPagamentoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkRemoverIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReceberVerba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(vRPanel9Layout.createSequentialGroup()
                                .addComponent(chkTemFicha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPermiteRotativoCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkreceberDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(321, Short.MAX_VALUE))
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
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRemoverIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPagamentoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipoDocRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoDocCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
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

        chkComplemento.setText("Complemento");

        chkFIndicadorIe.setText("Indicador IE");

        chkFTelefone.setText("Telefone");

        chkFFantasia.setText("Nome Fantasia");

        javax.swing.GroupLayout vRPanel8Layout = new javax.swing.GroupLayout(vRPanel8);
        vRPanel8.setLayout(vRPanel8Layout);
        vRPanel8Layout.setHorizontalGroup(
            vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSituacaoCadastroForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel8Layout.createSequentialGroup()
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFContatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkContasPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkFIndicadorIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(535, Short.MAX_VALUE))
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
                .addGroup(vRPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSituacaoCadastroForn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFIndicadorIe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
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
                .addContainerGap(660, Short.MAX_VALUE))
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
                .addContainerGap(302, Short.MAX_VALUE))
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
                .addContainerGap(531, Short.MAX_VALUE))
        );
        tabVendasLayout.setVerticalGroup(
            tabVendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVendasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );

        vRTabbedPane2.addTab("Vendas", tabVendas);

        tabs.addTab("Importação", vRTabbedPane2);

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
                .addContainerGap(440, Short.MAX_VALUE))
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
                .addContainerGap(282, Short.MAX_VALUE))
        );

        tabs.addTab("Unificação", vRPanel2);

        jScrollPane2.setViewportView(tabs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
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

    }//GEN-LAST:event_btnMapaTribActionPerformed

    private void chkProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProdutosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkProdutosActionPerformed

    private void cmbLojaOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaOrigemActionPerformed
        Estabelecimento est = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (est != null) {
            vRImportaArquivBalancaPanel1.setLoja(est.cnpj);
        }
    }//GEN-LAST:event_cmbLojaOrigemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton btnMapaTrib;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkAssociado;
    private vrframework.bean.checkBox.VRCheckBox chkAssociadoSomenteAtivos;
    private vrframework.bean.checkBox.VRCheckBox chkAtacado;
    private vrframework.bean.checkBox.VRCheckBox chkCContato;
    private vrframework.bean.checkBox.VRCheckBox chkCheque;
    private vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkCodigoANP;
    private vrframework.bean.checkBox.VRCheckBox chkComplemento;
    private vrframework.bean.checkBox.VRCheckBox chkContasPagar;
    private vrframework.bean.checkBox.VRCheckBox chkConvConveniado;
    private vrframework.bean.checkBox.VRCheckBox chkConvEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkConvRecebimento;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaDescComplGondola;
    private vrframework.bean.checkBox.VRCheckBox chkCopiarIcmsDebitoNaEntrada;
    private vrframework.bean.checkBox.VRCheckBox chkCustoComImposto;
    private vrframework.bean.checkBox.VRCheckBox chkCustoSemImposto;
    private vrframework.bean.checkBox.VRCheckBox chkDesconsiderarSetorBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkDescontinuado;
    private vrframework.bean.checkBox.VRCheckBox chkDtAlteracao;
    private vrframework.bean.checkBox.VRCheckBox chkFCondicaoPagamento;
    private vrframework.bean.checkBox.VRCheckBox chkFContatos;
    private vrframework.bean.checkBox.VRCheckBox chkFFantasia;
    private vrframework.bean.checkBox.VRCheckBox chkFIndicadorIe;
    private vrframework.bean.checkBox.VRCheckBox chkFPrazoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkFTelefone;
    private vrframework.bean.checkBox.VRCheckBox chkFamilia;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkForcarUnidade;
    private vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsEntrada;
    private vrframework.bean.checkBox.VRCheckBox chkIcmsSaida;
    private javax.swing.JCheckBox chkIcmsSaidaNF;
    private vrframework.bean.checkBox.VRCheckBox chkInverterAssociado;
    private vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkManterEAN;
    private vrframework.bean.checkBox.VRCheckBox chkMargem;
    private javax.swing.JCheckBox chkMargemSobreVenda;
    private vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    private javax.swing.JCheckBox chkMetodoAliquota;
    private vrframework.bean.checkBox.VRCheckBox chkMultiLoja;
    private javax.swing.JCheckBox chkNaoExcluirMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalFilizola;
    private vrframework.bean.checkBox.VRCheckBox chkNutricionalToledo;
    private vrframework.bean.checkBox.VRCheckBox chkOfertas;
    private vrframework.bean.checkBox.VRCheckBox chkPagamentoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkPauta;
    private vrframework.bean.checkBox.VRCheckBox chkPautaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkPdvVendas;
    private vrframework.bean.checkBox.VRCheckBox chkPermiteRotativoCheque;
    private vrframework.bean.checkBox.VRCheckBox chkPesquisarKG;
    private vrframework.bean.checkBox.VRCheckBox chkPrateleira;
    private vrframework.bean.checkBox.VRCheckBox chkPrecoMultiloja;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoAtivo;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkQtdCotacao;
    private vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkReceberVerba;
    private vrframework.bean.checkBox.VRCheckBox chkRemoverIDCliente;
    private vrframework.bean.checkBox.VRCheckBox chkRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastroForn;
    private javax.swing.JCheckBox chkSomarEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkT1AtivoInativo;
    private vrframework.bean.checkBox.VRCheckBox chkT1CEST;
    private vrframework.bean.checkBox.VRCheckBox chkT1Custo;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescCompleta;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescGondola;
    private vrframework.bean.checkBox.VRCheckBox chkT1DescReduzida;
    private vrframework.bean.checkBox.VRCheckBox chkT1EAN;
    private vrframework.bean.checkBox.VRCheckBox chkT1EANemBranco;
    private vrframework.bean.checkBox.VRCheckBox chkT1Estoque;
    private vrframework.bean.checkBox.VRCheckBox chkT1ForcarprecoCusto;
    private vrframework.bean.checkBox.VRCheckBox chkT1ICMS;
    private vrframework.bean.checkBox.VRCheckBox chkT1NCM;
    private vrframework.bean.checkBox.VRCheckBox chkT1NatReceita;
    private vrframework.bean.checkBox.VRCheckBox chkT1PisCofins;
    private vrframework.bean.checkBox.VRCheckBox chkT1Preco;
    private vrframework.bean.checkBox.VRCheckBox chkT1ProdMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkTemArquivoBalanca;
    private vrframework.bean.checkBox.VRCheckBox chkTemArquivoBalancaUnificacao;
    private vrframework.bean.checkBox.VRCheckBox chkTemFicha;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    private vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemProduto;
    private vrframework.bean.checkBox.VRCheckBox chkTipoProduto;
    private javax.swing.JCheckBox chkTrocaEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClienteEventual;
    private vrframework.bean.checkBox.VRCheckBox chkUnifClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUnifProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkUsaMargemLiquida;
    private vrframework.bean.checkBox.VRCheckBox chkUsarMargemBruta;
    private vrframework.bean.checkBox.VRCheckBox chkUsarQtdCotacaoProdFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkUtilizarEmbalagemCompra;
    private vrframework.bean.checkBox.VRCheckBox chkValidade;
    private vrframework.bean.checkBox.VRCheckBox chkValorLimite;
    private vrframework.bean.checkBox.VRCheckBox chkreceberDevolucao;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private javax.swing.JComboBox cmbTipoDocCheque;
    private javax.swing.JComboBox cmbTipoDocRotativo;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaFim;
    private org.jdesktop.swingx.JXDatePicker edtDtVendaIni;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private vrframework.bean.label.VRLabel lblLoja;
    private vrframework.bean.panel.VRPanel pnlPdvVendaDatas;
    private javax.swing.JPanel tabConvenio;
    private javax.swing.JPanel tabParametros;
    private vrframework.bean.panel.VRPanel tabVendas;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private javax.swing.JTabbedPane tabsConn;
    private org.jdesktop.swingx.JXDatePicker txtDataFimOferta;
    private vrframework.bean.textField.VRTextField txtDatabase;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtLojaMesmoID;
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
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.panel.VRPanel vRPanel7;
    private vrframework.bean.panel.VRPanel vRPanel8;
    private vrframework.bean.panel.VRPanel vRPanel9;
    private vrframework.bean.tabbedPane.VRTabbedPane vRTabbedPane2;
    private vrframework.bean.textField.VRTextField vRTextField1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
