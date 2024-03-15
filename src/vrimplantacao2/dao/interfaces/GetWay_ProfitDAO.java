package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.verba.receber.ReceberVerbaDAO;
import vrimplantacao2.dao.interfaces.GetWay_ProfitDAO.TipoDocumentoRecord;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVerbaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class GetWay_ProfitDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(GetWay_ProfitDAO.class.getName());

    public int v_tipoDocumento;
    public int v_tipoDocumentoCheque;
    public boolean v_usar_arquivoBalanca;
    public boolean v_usar_arquivoBalancaUnificacao;
    public boolean usarQtdEmbDoProduto = false;
    public boolean usarMargemBruta = false;
    public boolean usaMargemLiquidaPraticada = false;
    public boolean usaMargemSobreVenda = false;
    private boolean desconsiderarSetorBalanca = false;
    private boolean pesquisarKGnaDescricao;
    private boolean utilizarEmbalagemDeCompra = false;
    public boolean apenasProdutoAtivo = false;
    private boolean copiarIcmsDebitoNaEntrada = false;
    public boolean utilizaMetodoAjustaAliquota = false;
    public boolean copiarDescricaoCompletaParaGondola = false;
    public boolean removerCodigoCliente = false;

    private Set<Integer> TipoDocumentoRotativo;
    private Set<Integer> TipoDocumentoCheque;

    public void setTipoDocumentoRotativo(Set<Integer> TipoDocumentoRotativo) {
        this.TipoDocumentoRotativo = TipoDocumentoRotativo;
    }

    public void setTipoDocumentoCheque(Set<Integer> TipoDocumentoCheque) {
        this.TipoDocumentoCheque = TipoDocumentoCheque;
    }

    public void setUtilizarEmbalagemDeCompra(boolean utilizarEmbalagemDeCompra) {
        this.utilizarEmbalagemDeCompra = utilizarEmbalagemDeCompra;
    }

    public void setUsarQtdEmbDoProduto(boolean usarQtdEmbDoProduto) {
        this.usarQtdEmbDoProduto = usarQtdEmbDoProduto;
    }

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "GetWay" + (!"".equals(complemento) ? " - " + complemento : "");
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODLOJA id,\n"
                    + "	descricao\n"
                    + "from\n"
                    + "	LOJA\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codprod, "
                    + "     cast(p.dataini as date) as dataini, "
                    + "     cast(p.datafim as date) as datafim, "
                    + "     p.preco_unit precooferta, "
                    + "     prod.preco_unit as preconormal "
                    + "from promocao p "
                    + "inner join produtos prod on p.codprod = prod.codprod "
                    + "where datafim >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "' "
                    + "order by p.dataini"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("codprod"));
                    imp.setDataInicio(rst.getDate("dataini"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODFAMILIA, "
                    + "descricao "
                    + "from "
                    + "FAMILIA"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAMILIA"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.CODCRECEITA m1,\n"
                    + "	coalesce(m1.DESCRICAO, 'PADRAO') m1_desc,\n"
                    + "	p.CODGRUPO m2,\n"
                    + "	coalesce(m2.DESCRICAO, 'PADRAO') m2_desc,\n"
                    + "	p.CODCATEGORIA m3,\n"
                    + "	coalesce(m3.DESCRICAO, 'PADRAO') m3_desc\n"
                    + "from\n"
                    + "	(select distinct\n"
                    + "		codcreceita,\n"
                    + "		codgrupo,\n"
                    + "		codcategoria\n"
                    + "	from\n"
                    + "		PRODUTOS) p\n"
                    + "	left join creceita m1 on\n"
                    + "		p.codcreceita = m1.codcreceita\n"
                    + "	left join grupo m2 on\n"
                    + "		p.CODCRECEITA = m2.CODCRECEITA and\n"
                    + "		p.CODGRUPO = m2.CODGRUPO\n"
                    + "	left join categoria m3 on\n"
                    + "		p.CODCATEGORIA = m3.CODCATEGORIA\n"
                    + "order by\n"
                    + "	m1, m2, m3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc1Descricao(rst.getString("m1_desc"));
                    imp.setMerc2Descricao(rst.getString("m2_desc"));
                    imp.setMerc3Descricao(rst.getString("m3_desc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    //O select de produto mudou, foi utilizado a tabela prod_loja para lojas unificadas do Getway
    //Caso o banco de dados não tiver com esta tabela populada, verificar antes da migração
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();

        LOG.log(Level.CONFIG,
                "Parametros:\r\n - Desconsiderar setor de balan\u00e7a:{0}\r\n",
                desconsiderarSetorBalanca);

        StringBuilder rep = new StringBuilder();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            MultiMap<Comparable, Void> icms = new MultiMap<>();
            try (Statement st = Conexao.createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select \n"
                        + "	situacaotributaria cst, \n"
                        + "	porcentagem aliq, \n"
                        + "	reduzido \n"
                        + "from \n"
                        + "	aliquota \n"
                        + "where \n"
                        + "	id_situacaocadastro = 1 \n"
                        + "order by \n"
                        + "	1, 2, 3"
                )) {
                    while (rs.next()) {
                        icms.put(
                                null,
                                rs.getInt("cst"),
                                MathUtils.trunc(rs.getDouble("aliq"), 2),
                                MathUtils.trunc(rs.getDouble("reduzido"), 1)
                        );
                    }
                }
            }
            int qtdBalanca = 0, qtdNormal = 0;
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	prod.codprod id,\n"
                    + "	prod.dtinclui datacadastro,\n"
                    + "	prod.dtaltera dataalteracao,\n"
                    + "	case when prod.qtd_emb < 1 then 1 else prod.qtd_emb end qtdembalagemcotacao,\n"
                    + "	prod.BARRA codigobarras,\n"
                    + "	prod.unidade,\n"
                    + "	prod.unidade_comp,\n"
                    + "	case when prod.codsetor is null then 0 else 1 end balanca,\n"
                    + "	prod.validade,\n"
                    + "	prod.descricao descricaocompleta,\n"
                    + "	prod.desc_pdv descricaogondola,\n"
                    + "	prod.desc_pdv descricaoreduzida,\n"
                    + "	coalesce(prod.codcreceita, 1) as cod_mercadologico1,\n"
                    + "	coalesce(prod.codgrupo, 1) as cod_mercadologico2,\n"
                    + "	coalesce(prod.codcategoria, 1) as cod_mercadologico3,\n"
                    + "	fam.codfamilia id_familiaproduto,\n"
                    + "	prod.peso_bruto pesobruto,\n"
                    + "	prod.peso_liq pesoliquido,\n"
                    + "	prod.estoque_max estoquemaximo,\n"
                    + "	prod.estoque_min estoqueminimo,\n"
                    + "	pl.estoque,\n"
                    + "	trc.QTD estoquetroca,\n"
                    + "	pl.preco_cust custocomimposto,\n"
                    + "	pl.preco_cust custosemimposto,\n"
                    + "	pl.preco_unit precovenda,\n"
                    + " prod.preco_unit as precovenda_produto, \n"
                    + " prod.preco_cust as custo_produto, \n"
                    + " prod.estoque as estoque_produto, \n"
                    + "	prod.margem_bruta margem_bruta,\n"
                    + "	prod.margem_param margem_param,\n"
                    + "	prod.lucroliq margemliquidapraticada,\n"
                    + "   cast(round(((prod.PRECO_CUST / \n"
                    + "		case when prod.PRECO_UNIT = 0 then 1 else \n"
                    + "			prod.PRECO_UNIT end * 100) - 100) * -1, 2) \n"
                    + "			as numeric(12,2)) margemsobrevenda,        \n"
                    + "	prod.ativo,\n"
                    + "	case when prod.descricao like '*%' then 1 else 0 end descontinuado,\n"
                    + "	prod.codncm ncm,\n"
                    + "	prod.codcest cest,	\n"
                    + "	prod.cst_pisentrada piscofins_cst_credito,\n"
                    + "	prod.cst_pissaida piscofins_cst_debito,\n"
                    + "	prod.nat_rec piscofins_natureza_receita,\n"
                    + " ltrim(rtrim(prod.codaliq)) icms_debito_id,\n"
                    + " fcp.VALORTRIB fcp,\n"
                    + "	prod.CODTRIB icms_cst_saida,\n"
                    + "	al.ALIQUOTA icms_aliquota_saida,\n"
                    + "	prod.PER_REDUC icms_reduzido_saida,\n"
                    + "	prod.CODTRIB_ENT icms_cst_entrada,\n"
                    + "	prod.ulticmscred icms_aliquota_entrada,\n"
                    + "	prod.PER_REDUC_ENT icms_reduzido_entrada,\n"
                    + " refativoimob tipo_ativo,\n"
                    + " refusoconsumo tipo_usoconsumo,\n"
                    + "	prod.desativacompra,\n"
                    + " prod.CODANP codigoanp,\n"
                    + " prod.corredor\n"
                    + "from\n"
                    + "	produtos prod\n"
                    + "left outer join prod_familia fam on\n"
                    + "	fam.codprod = prod.codprod and\n"
                    + "	prod.codprod > 0\n"
                    + "join aliquota_icms al on\n"
                    + "	al.CODALIQ = prod.codaliq_nf\n"
                    + "left join TROCACOMPRA trc on prod.CODPROD = trc.CODPROD\n"
                    + "left join PROD_TRIBFCP fcp on prod.CODPROD = fcp.CODPROD\n"
                    + "left join prod_loja pl on prod.codprod = pl.CODPROD\n"
                    + "and pl.codloja = " + getLojaOrigem() + "\n"
                    + (apenasProdutoAtivo == true ? " where upper(ltrim(rtrim(prod.ativo))) = 'S'\n" : "")
                    + "order by\n"
                    + "	id"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(1);
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));

                    if (copiarDescricaoCompletaParaGondola) {
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    }

                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));

                    /*imp.setEstoque(rst.getDouble("estoque") == 0
                            ? rst.getDouble("estoque_produto") : rst.getDouble("estoque"));

                    imp.setTroca(rst.getDouble("estoquetroca"));

                    imp.setCustoComImposto(rst.getDouble("custocomimposto") == 0
                            ? rst.getDouble("custo_produto") : rst.getDouble("custocomimposto"));

                    imp.setCustoSemImposto(rst.getDouble("custosemimposto") == 0
                            ? rst.getDouble("custo_produto") : rst.getDouble("custosemimposto"));

                    imp.setPrecovenda(rst.getDouble("precovenda") == 0
                            ? rst.getDouble("precovenda_produto") : rst.getDouble("precovenda"));*/
                    
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    
                    imp.setEstoque(rst.getDouble("estoque_produto"));
                    imp.setPrecovenda(rst.getDouble("precovenda_produto"));
                    imp.setCustoComImposto(rst.getDouble("custo_produto"));
                    imp.setCustoSemImposto(rst.getDouble("custo_produto"));

                    if (usarMargemBruta) {
                        imp.setMargem(rst.getDouble("margem_bruta"));
                    } else if (usaMargemLiquidaPraticada) {
                        imp.setMargem(rst.getDouble("margemliquidapraticada"));
                    } else if (usaMargemSobreVenda) {
                        imp.setMargem(rst.getDouble("margemsobrevenda"));
                    } else {
                        imp.setMargem(rst.getDouble("margem_param"));
                    }

                    if ("S".equals(rst.getString("tipo_ativo"))) {
                        imp.setTipoProduto(TipoProduto.ATIVO_IMOBILIZADO);
                    } else {
                        if ("S".equals(rst.getString("tipo_usoconsumo"))) {
                            imp.setTipoProduto(TipoProduto.MATERIAL_USO_E_CONSUMO);
                        }
                    }

                    imp.setSituacaoCadastro(("S".equals(rst.getString("ativo").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setDescontinuado("S".equals(rst.getString("desativacompra")) || rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_credito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_debito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));

                    String aliquotaDebitoId = formatTributacaoId(rst.getString("icms_debito_id"), rst.getDouble("fcp"));
                    imp.setIcmsDebitoId(aliquotaDebitoId);
                    imp.setIcmsDebitoForaEstadoId(aliquotaDebitoId);
                    imp.setIcmsDebitoForaEstadoNfId(aliquotaDebitoId);
                    imp.setIcmsConsumidorId(aliquotaDebitoId);
                    if (copiarIcmsDebitoNaEntrada) {
                        imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                        imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    } else {
                        imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("icms_cst_entrada")));
                        imp.setIcmsAliqEntrada(Utils.stringToDouble(rst.getString("icms_aliquota_entrada")));
                        imp.setIcmsReducaoEntrada(Utils.stringToDouble(rst.getString("icms_reduzido_entrada")));

                        if (imp.getIcmsCstEntrada() != 20) {
                            imp.setIcmsReducaoEntrada(0);
                        }
                        if (imp.getIcmsCstEntrada() != 0
                                && imp.getIcmsCstEntrada() != 10
                                && imp.getIcmsCstEntrada() != 20
                                && imp.getIcmsCstEntrada() != 70) {
                            imp.setIcmsAliqEntrada(0);
                            imp.setIcmsReducaoEntrada(0);
                        }

                        imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                        imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntradaForaEstado());
                        imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntradaForaEstado());

                        String str = (imp.getImportId() + " - ICMS Entrada: "
                                + imp.getIcmsCstEntrada() + " - "
                                + MathUtils.trunc(imp.getIcmsAliqEntrada(), 2) + " - "
                                + MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1));

                        if (!icms.containsKey(
                                imp.getIcmsCstEntrada(),
                                MathUtils.trunc(imp.getIcmsAliqEntrada(), 2),
                                MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1)
                        )) {
                            imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                            imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                        } else {
                            imp.setIcmsCreditoId(null);
                            str += " - Encontrou";
                        }
                        LOG.finest(str);
                    }

                    if (desconsiderarSetorBalanca) {
                        String st = Utils.acertarTexto(rst.getString("unidade"), 2);
                        if ("KG".equals(st)) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("KG");
                        } else {
                            String desc = Utils.acertarTexto(imp.getDescricaoCompleta());
                            if (pesquisarKGnaDescricao && desc.contains("KG") && !desc.matches(".*[0-9](\\s)*K?G")) {
                                imp.seteBalanca(true);
                                imp.setTipoEmbalagem("KG");
                            } else {
                                imp.setTipoEmbalagem("UN");
                                imp.seteBalanca(false);
                            }
                        }
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if (imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    } else if (v_usar_arquivoBalanca) {
                        ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getEan(), -2));
                        if (bal != null) {
                            qtdBalanca++;
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                            imp.setValidade(bal.getValidade() > 1 ? bal.getValidade() : rst.getInt("VALIDADE"));
                        } else {
                            qtdNormal++;
                            imp.setValidade(0);
                            imp.setTipoEmbalagem(rst.getString("unidade"));
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca((rst.getInt("balanca") == 1));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setTipoEmbalagemCotacao(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if (imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    }

                    if (this.utilizarEmbalagemDeCompra) {
                        imp.setTipoEmbalagem(rst.getString("unidade_comp"));
                    }

                    if (utilizaMetodoAjustaAliquota) {
                        acertaAliquota(imp);
                    }

                    imp.setPautaFiscalId(imp.getImportId());
                    imp.setCodigoAnp(rst.getString("codigoanp") != null ? rst.getString("codigoanp").trim()
                            : "");

                    imp.setPrateleira(rst.getString("corredor"));

                    vResult.add(imp);
                }
            }
            LOG.log(Level.FINE,
                    "Produtos de balan\u00e7a: {0} normais: {1}",
                    new Object[]{qtdBalanca, qtdNormal});
        }
        return vResult;
    }

    //<editor-fold defaultstate="collapsed" desc="Script Com o Custo da Nota de Entrada">
    /*@Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();

        LOG.config("Parametros:\r\n"
                + " - Desconsiderar setor de balança:" + desconsiderarSetorBalanca + "\r\n");

        StringBuilder rep = new StringBuilder();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            MultiMap<Comparable, Void> icms = new MultiMap<>();
            try (Statement st = Conexao.createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select \n"
                        + "	situacaotributaria cst, \n"
                        + "	porcentagem aliq, \n"
                        + "	reduzido \n"
                        + "from \n"
                        + "	aliquota \n"
                        + "where \n"
                        + "	id_situacaocadastro = 1 \n"
                        + "order by \n"
                        + "	1, 2, 3"
                )) {
                    while (rs.next()) {
                        icms.put(
                                null,
                                rs.getInt("cst"),
                                MathUtils.trunc(rs.getDouble("aliq"), 2),
                                MathUtils.trunc(rs.getDouble("reduzido"), 1)
                        );
                    }
                }
            }
            int qtdBalanca = 0, qtdNormal = 0;
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	prod.codprod id,\n" +
                    "	prod.dtinclui datacadastro,\n" +
                    "	prod.dtaltera dataalteracao,\n" +
                    "	case when prod.qtd_emb < 1 then 1 else prod.qtd_emb end qtdembalagemcotacao,\n" +
                    "	prod.BARRA codigobarras,\n" +
                    "	prod.unidade,\n" +
                    "	prod.unidade_comp,\n" +
                    "	case when prod.codsetor is null then 0 else 1 end balanca,\n" +
                    "	prod.validade,\n" +
                    "	prod.descricao descricaocompleta,\n" +
                    "	prod.desc_pdv descricaogondola,\n" +
                    "	prod.desc_pdv descricaoreduzida,\n" +
                    "	coalesce(prod.codcreceita, 1) as cod_mercadologico1,\n" +
                    "	coalesce(prod.codgrupo, 1) as cod_mercadologico2,\n" +
                    "	coalesce(prod.codcategoria, 1) as cod_mercadologico3,\n" +
                    "	fam.codfamilia id_familiaproduto,\n" +
                    "	prod.peso_bruto pesobruto,\n" +
                    "	prod.peso_liq pesoliquido,\n" +
                    "	prod.estoque_max estoquemaximo,\n" +
                    "	prod.estoque_min estoqueminimo,\n" +
                    "	pl.estoque,\n" +
                    "	trc.QTD estoquetroca,\n" +
                    "	ab.custo custocomimposto,\n" +
                    "	ab.custo custosemimposto,\n" +
                    "	pl.preco_unit precovenda,\n" +
                    "	prod.margem_bruta margem_bruta,\n" +
                    "	prod.margem_param margem_param,\n" +
                    "	prod.lucroliq margemliquidapraticada,\n" +
                    "   cast(round(((prod.PRECO_CUST / \n" +
                    "		case when prod.PRECO_UNIT = 0 then 1 else \n" +
                    "			prod.PRECO_UNIT end * 100) - 100) * -1, 2) \n" +
                    "			as numeric(12,2)) margemsobrevenda,        \n" +
                    "	prod.ativo,\n" +
                    "	case when prod.descricao like '*%' then 1 else 0 end descontinuado,\n" +
                    "	prod.codncm ncm,\n" +
                    "	prod.codcest cest,	\n" +
                    "	prod.cst_pisentrada piscofins_cst_credito,\n" +
                    "	prod.cst_pissaida piscofins_cst_debito,\n" +
                    "	prod.nat_rec piscofins_natureza_receita,\n" +
                    "	--ltrim(rtrim(prod.codaliq)) icms_debito_id,)\n" +
                    "	ltrim(rtrim(prod.codaliq)) + coalesce(cast(fcp.VALORTRIB as varchar), '') icms_debito_id,        \n" +
                    "	prod.CODTRIB icms_cst_saida,\n" +
                    "	al.ALIQUOTA icms_aliquota_saida,\n" +
                    "	prod.PER_REDUC icms_reduzido_saida,\n" +
                    "	prod.CODTRIB_ENT icms_cst_entrada,\n" +
                    "	prod.ulticmscred icms_aliquota_entrada,\n" +
                    "	prod.PER_REDUC_ENT icms_reduzido_entrada,\n" +
                    "	refativoimob tipo_ativo,\n" +
                    "	refusoconsumo tipo_usoconsumo,\n" +
                    "	prod.desativacompra,\n" +
                    "	prod.CODANP codigoanp,\n" +
                    "	prod.corredor\n" +
                    "from\n" +
                    "	produtos prod\n" +
                    "left outer join prod_familia fam on\n" +
                    "		fam.codprod = prod.codprod and\n" +
                    "		prod.codprod > 0\n" +
                    "join aliquota_icms al on\n" +
                    "		al.CODALIQ = prod.codaliq_nf\n" +
                    "left join TROCACOMPRA trc on prod.CODPROD = trc.CODPROD\n" +
                    "left join PROD_TRIBFCP fcp on prod.CODPROD = fcp.CODPROD\n" +
                    "left join prod_loja pl on prod.codprod = pl.CODPROD   \n" +
                    "left join \n" +
                    "	(select\n" +
                    "		i.CODITMENTRADANF, \n" +
                    "		CODPROD,\n" +
                    "		custoprod custo,\n" +
                    "		n.CODLOJA \n" +
                    "	from \n" +
                    "		ITMENTRADANF i,\n" +
                    "		ENTRADANF n\n" +
                    "	where \n" +
                    "		n.CODENTRADANF = i.CODENTRADANF and\n" +
                    "		i.CODITMENTRADANF =\n" +
                    "			(select \n" +
                    "				MAX(CODITMENTRADANF) \n" +
                    "			from \n" +
                    "				ITMENTRADANF \n" +
                    "			where\n" +
                    "				CODPROD = i.CODPROD)) ab on prod.CODPROD = ab.CODPROD and\n" +
                    "		ab.CODLOJA = pl.CODLOJA   \n" +
                    "where pl.codloja = " + getLojaOrigem() + " \n" +
                    (apenasProdutoAtivo == true ? " and upper(ltrim(rtrim(prod.ativo))) = 'S' " : "") +        
                    "order by\n" +
                    "	id"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(1);
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));

                    if (copiarDescricaoCompletaParaGondola) {
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    }

                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    if (usarMargemBruta) {
                        imp.setMargem(rst.getDouble("margem_bruta"));
                    } else if (usaMargemLiquidaPraticada) {
                        imp.setMargem(rst.getDouble("margemliquidapraticada"));
                    } else if (usaMargemSobreVenda) {
                        imp.setMargem(rst.getDouble("margemsobrevenda"));
                    } else {
                        imp.setMargem(rst.getDouble("margem_param"));
                    }

                    if ("S".equals(rst.getString("tipo_ativo"))) {
                        imp.setTipoProduto(TipoProduto.ATIVO_IMOBILIZADO);
                    } else {
                        if ("S".equals(rst.getString("tipo_usoconsumo"))) {
                            imp.setTipoProduto(TipoProduto.MATERIAL_USO_E_CONSUMO);
                        }
                    }

                    imp.setSituacaoCadastro(("S".equals(rst.getString("ativo").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setDescontinuado("S".equals(rst.getString("desativacompra")) || rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_credito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_debito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));

                    imp.setIcmsDebitoId(rst.getString("icms_debito_id"));
                    if (copiarIcmsDebitoNaEntrada) {
                        imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    } else {
                        if (this.utilizarIdIcmsNaEntrada) {
                            imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                        } else {
                            imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("icms_cst_entrada")));
                            imp.setIcmsAliqEntrada(Utils.stringToDouble(rst.getString("icms_aliquota_entrada")));
                            imp.setIcmsReducaoEntrada(Utils.stringToDouble(rst.getString("icms_reduzido_entrada")));

                            if (imp.getIcmsCstEntrada() != 20) {
                                imp.setIcmsReducaoEntrada(0);
                            }
                            if (imp.getIcmsCstEntrada() != 0
                                    && imp.getIcmsCstEntrada() != 10
                                    && imp.getIcmsCstEntrada() != 20
                                    && imp.getIcmsCstEntrada() != 70) {
                                imp.setIcmsAliqEntrada(0);
                                imp.setIcmsReducaoEntrada(0);
                            }

                            String str = (imp.getImportId() + " - ICMS Entrada: "
                                    + imp.getIcmsCstEntrada() + " - "
                                    + MathUtils.trunc(imp.getIcmsAliqEntrada(), 2) + " - "
                                    + MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1));

                            if (!icms.containsKey(
                                    imp.getIcmsCstEntrada(),
                                    MathUtils.trunc(imp.getIcmsAliqEntrada(), 2),
                                    MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1)
                            )) {
                                imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                            } else {
                                imp.setIcmsCreditoId(null);
                                str += " - Encontrou";
                            }
                            LOG.finest(str);
                        }
                    }

                    if (desconsiderarSetorBalanca) {
                        String st = Utils.acertarTexto(rst.getString("unidade"), 2);
                        if ("KG".equals(st)) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("KG");
                        } else {
                            String desc = Utils.acertarTexto(imp.getDescricaoCompleta());
                            if (pesquisarKGnaDescricao && desc.contains("KG") && !desc.matches(".*[0-9](\\s)*K?G")) {
                                imp.seteBalanca(true);
                                imp.setTipoEmbalagem("KG");
                            } else {
                                imp.setTipoEmbalagem("UN");
                                imp.seteBalanca(false);
                            }
                        }
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if (imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    } else if (v_usar_arquivoBalanca) {
                        ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getEan(), -2));
                        if (bal != null) {
                            qtdBalanca++;
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                            imp.setValidade(bal.getValidade() > 1 ? bal.getValidade() : rst.getInt("VALIDADE"));
                        } else {
                            qtdNormal++;
                            imp.setValidade(0);
                            imp.setTipoEmbalagem(rst.getString("unidade"));
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca((rst.getInt("balanca") == 1));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if (imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    }

                    if (this.utilizarEmbalagemDeCompra) {
                        imp.setTipoEmbalagem(rst.getString("unidade_comp"));
                    }

                    if (utilizaMetodoAjustaAliquota) {
                        acertaAliquota(imp);
                    }

                    imp.setPautaFiscalId(imp.getImportId());
                    imp.setCodigoAnp(rst.getString("codigoanp") != null ? rst.getString("codigoanp").trim()
                            : "");

                    if (manterEAN && !imp.isBalanca() && imp.getEan() != null && imp.getEan().length() < 7) {
                        imp.setManterEAN(true);
                    }

                    imp.setPrateleira(String.valueOf(Utils.stringToInt(rst.getString("corredor"))));

                    vResult.add(imp);
                }
            }
            LOG.fine("Produtos de balança: " + qtdBalanca + " normais: " + qtdNormal);
        }
        return vResult;
    }*/
    //</editor-fold>
    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	CODPROD id,\n"
                    + "	p.descricao,\n"
                    + "	p.barra ean,\n"
                    + "	coalesce(p.codncm, 0) ncm,\n"
                    + "	p.CODALIQ icms_consumidor_id,\n"
                    + "	aliq_s_c.VALORTRIB icms_consumidor,\n"
                    + "	CODALIQ_NF icms_debito_nf_id,\n"
                    + "	codtrib cst_debito_nf,\n"
                    + "	aliq_s_nf.VALORTRIB icms_debito_nf,\n"
                    + "	PER_REDUC icms_debito_red_nf,\n"
                    + "	coalesce(PER_REDUC_ENT, 0) icms_credito_red,\n"
                    + "	coalesce(CODTRIB_ENT, 0) cst_credito,\n"
                    + "	coalesce(ULTICMSCRED, 0) icms_credito,\n"
                    + "	p.PERMVA mva\n"
                    + "from\n"
                    + "	PRODUTOS p\n"
                    + "left join ALIQUOTA_ICMS aliq_s_nf on p.CODALIQ_NF = aliq_s_nf.CODALIQ\n"
                    + "left join ALIQUOTA_ICMS aliq_s_c on p.CODALIQ = aliq_s_c.CODALIQ\n"
                    + "where\n"
                    + "	p.PERMVA > 0\n"
                    + "order by\n"
                    + "	p.DESCRICAO")) {
                while (rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setIva(rs.getDouble("mva"));
                    imp.setIvaAjustado(imp.getIva());

                    double icms = rs.getDouble("icms_credito"),
                            reducao = rs.getDouble("icms_credito_red");
                    String cst = rs.getString("cst_credito");

                    //Verificação de ICMS tributado
                    if (icms > 0.0 && reducao == 0.0) {
                        System.out.println("ICMS T: " + icms + " RED: " + reducao + " CST: " + cst);
                        imp.setAliquotaCredito(0, icms, reducao);
                        imp.setAliquotaCreditoForaEstado(0, icms, reducao);
                        imp.setAliquotaDebito(0, icms, reducao);
                        imp.setAliquotaDebitoForaEstado(0, icms, reducao);
                    }

                    //Verificação de ICMS Substítuido
                    if (icms == 0.0 && reducao == 0.0) {
                        System.out.println("ICMS S: " + icms + " RED: " + reducao + " CST: " + cst);
                        imp.setAliquotaCredito(60, icms, reducao);
                        imp.setAliquotaCreditoForaEstado(60, icms, reducao);
                        imp.setAliquotaDebito(60, icms, reducao);
                        imp.setAliquotaDebitoForaEstado(60, icms, reducao);
                    }

                    //Verificação de ICMS com redução na BC
                    if (icms > 0.0 && reducao > 0.0) {
                        System.out.println("ICMS RED: " + icms + " RED: " + reducao + " CST: " + cst);
                        imp.setAliquotaCredito(20, icms, reducao);
                        imp.setAliquotaCreditoForaEstado(20, icms, reducao);
                        imp.setAliquotaDebito(20, icms, reducao);
                        imp.setAliquotaDebitoForaEstado(20, icms, reducao);
                    }

                    //Verificação de Isenção de ICMS
                    if (icms == 0.0 && reducao == 0.0 && "040".equals(cst)) {
                        System.out.println("ICMS IS: " + icms + " RED: " + reducao + " CST: " + cst);
                        imp.setAliquotaCredito(40, icms, reducao);
                        imp.setAliquotaCreditoForaEstado(40, icms, reducao);
                        imp.setAliquotaDebito(40, icms, reducao);
                        imp.setAliquotaDebitoForaEstado(40, icms, reducao);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
     Método para ajustar a aliquota débito NF e aliquota crédito NF, 
     este método não altera a aliquota icms consumidor (aliquota de cupom)
     */
    private void acertaAliquota(ProdutoIMP imp) throws SQLException {
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	CODPROD id_produto,\n"
                    + "	p.descricao,\n"
                    + "	p.CODALIQ icms_consumidor_id,\n"
                    + "	aliq_s_c.VALORTRIB icms_consumidor,\n"
                    + "	CODALIQ_NF icms_debito_nf_id,\n"
                    + "	codtrib cst_debito_nf,\n"
                    + "	aliq_s_nf.VALORTRIB icms_debito_nf,\n"
                    + "	PER_REDUC icms_debito_red_nf,\n"
                    + "	CAST(PER_REDUC_ENT as numeric(15, 2)) icms_credito_red_nf,\n"
                    + " aliq_s_nf.VALORTRIB icms_credito_nf,"
                    + "	CODTRIB_ENT cst_credito_nf,\n"
                    //+ "	ULTICMSCRED icms_credito_nf,\n"
                    + " p.ALIQICMS_INTER aliq_interna,\n"
                    + "	p.PERMVA mva\n"
                    + "from\n"
                    + "	PRODUTOS p\n"
                    + "left join ALIQUOTA_ICMS aliq_s_nf on p.CODALIQ_NF = aliq_s_nf.CODALIQ\n"
                    + "left join ALIQUOTA_ICMS aliq_s_c on p.CODALIQ = aliq_s_c.CODALIQ\n"
                    + "where CODPROD = " + imp.getImportId())) {
                if (rs.next()) {

                    //Aliquota Débito
                    imp.setIcmsAliqSaida(rs.getDouble("icms_debito_nf"));
                    imp.setIcmsCstSaida(rs.getInt("cst_debito_nf"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_debito_red_nf"));

                    //Aliquota Crédito
                    imp.setIcmsAliqEntrada(rs.getInt("icms_credito_nf"));
                    imp.setIcmsCstEntrada(rs.getInt("cst_credito_nf"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_credito_red_nf"));

                    imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_credito_nf"));
                    imp.setIcmsCstEntradaForaEstado(rs.getInt("cst_credito_nf"));
                    imp.setIcmsReducaoEntradaForaEstado(rs.getDouble("icms_credito_red_nf"));

                    imp.setIcmsDebitoId(null);
                    imp.setIcmsCreditoId(null);
                }
            }
        }
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codprod id_produto,\n"
                    + "	barra ean,\n"
                    + "	1 qtdembalagem,\n"
                    + "	preco_unit preco\n"
                    + "from\n"
                    + "	produtos prod\n"
                    + "union\n"
                    + "select\n"
                    + "	codprod id_produto, \n"
                    + "	rtrim(barra_emb) ean, \n"
                    + "	qtd qtdembalagem,\n"
                    + "	preco_unit preco\n"
                    + "from \n"
                    + "	embalagens \n"
                    + "where\n"
                    + "	barra_emb is not null \n"
                    + "union\n"
                    + "select\n"
                    + "	codprod id_produto,\n"
                    + "	barra ean,\n"
                    + "	1 qtdembalagem,\n"
                    + "	0 preco\n"
                    + "from \n"
                    + "	alternativo\n"
                    + "where\n"
                    + "	barra is not null"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
     public List<ProdutoIMP> getProdutos() throws Exception {
     List<ProdutoIMP> vResult = new ArrayList<>();
        
     LOG.config("Parametros:\r\n"
     + " - Desconsiderar setor de balança:" + desconsiderarSetorBalanca + "\r\n");

     StringBuilder rep = new StringBuilder();
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     MultiMap<Comparable, Void> icms = new MultiMap<>();
     try (Statement st = Conexao.createStatement()) {
     try (ResultSet rs = st.executeQuery(
     "select \n"
     + "	situacaotributaria cst, \n"
     + "	porcentagem aliq, \n"
     + "	reduzido \n"
     + "from \n"
     + "	aliquota \n"
     + "where \n"
     + "	id_situacaocadastro = 1 \n"
     + "order by \n"
     + "	1, 2, 3"
     )) {
     while (rs.next()) {
     icms.put(
     null,
     rs.getInt("cst"),
     MathUtils.trunc(rs.getDouble("aliq"), 2),
     MathUtils.trunc(rs.getDouble("reduzido"), 1)
     );
     }
     }
     }
     int qtdBalanca = 0, qtdNormal = 0;
     try (ResultSet rst = stm.executeQuery(
     "select\n" +
     "	prod.codprod id,\n" +
     "	prod.dtinclui datacadastro,\n" +
     "	prod.dtaltera dataalteracao,\n" +
     "	ean.ean codigobarras,\n" +
     "	case when prod.qtd_emb < 1 then 1 else prod.qtd_emb end qtdembalagemcotacao,\n" +
     "	ean.qtdembalagem,\n" +
     "	prod.unidade,\n" +
     "	prod.unidade_comp,\n" +
     "	case when prod.codsetor is null then 0 else 1 end balanca,\n" +
     "	prod.validade,\n" +
     "	prod.descricao descricaocompleta,\n" +
     "	prod.descricao descricaogondola,\n" +
     "	prod.desc_pdv descricaoreduzida,\n" +
     "	coalesce(prod.codcreceita, 1) as cod_mercadologico1,\n" +
     "	coalesce(prod.codgrupo, 1) as cod_mercadologico2,\n" +
     "	coalesce(prod.codcategoria, 1) as cod_mercadologico3,\n" +
     "	fam.codfamilia id_familiaproduto,\n" +
     "	prod.peso_bruto pesobruto,\n" +
     "	prod.peso_liq pesoliquido,\n" +
     "	prod.estoque_max estoquemaximo,\n" +
     "	prod.estoque_min estoqueminimo,\n" +
     "	prod.estoque,\n" +
     "	prod.preco_cust custocomimposto,\n" +
     "	prod.preco_cust custosemimposto,\n" +
     "	prod.preco_unit precovenda,\n" +
     "	prod.margem_bruta margem,\n" +
     "	prod.margem_param margem_param,\n" +
     "	prod.lucroliq margemliquidapraticada,\n" +
     "	prod.ativo,\n" +
     "	case when prod.descricao like '*%' then 1 else 0 end descontinuado,\n" +
     "	prod.codncm ncm,\n" +
     "	prod.codcest cest,	\n" +
     "	prod.cst_pisentrada piscofins_cst_credito,\n" +
     "	prod.cst_pissaida piscofins_cst_debito,\n" +
     "	prod.nat_rec piscofins_natureza_receita,\n" +
     "	prod.codaliq icms_debito_id,\n" +
     "	prod.CODTRIB icms_cst_saida,\n" +
     "	al.ALIQUOTA icms_aliquota_saida,\n" +
     "	prod.PER_REDUC icms_reduzido_saida,\n" +
     "	prod.CODTRIB_ENT icms_cst_entrada,\n" +
     "	prod.ulticmscred icms_aliquota_entrada,\n" +
     "	prod.PER_REDUC_ENT icms_reduzido_entrada,\n" +
     "	prod.desativacompra\n" +
     "from\n" +
     "	produtos prod\n" +
     "	left join (\n" +
     "		select\n" +
     "			codprod id_produto,\n" +
     "			barra ean,\n" +
     "			1 qtdembalagem,\n" +
     "			preco_unit preco\n" +
     "		from\n" +
     "			produtos prod\n" +
     "		union\n" +
     "		select\n" +
     "			codprod id_produto, \n" +
     "			rtrim(barra_emb) ean, \n" +
     "			qtd qtdembalagem,\n" +
     "			preco_unit preco\n" +
     "		from embalagens \n" +
     "		where\n" +
     "			barra_emb is not null \n" +
     "		union\n" +
     "		select\n" +
     "			codprod id_produto,\n" +
     "			barra ean,\n" +
     "			1 qtdembalagem,\n" +
     "			0 preco\n" +
     "		from alternativo\n" +
     "		where\n" +
     "			barra is not null\n" +
     "	) ean on\n" +
     "		prod.codprod = ean.id_produto\n" +
     "	left outer join prod_familia fam on\n" +
     "		fam.codprod = prod.codprod and\n" +
     "		prod.codprod > 0\n" +
     "	join aliquota_icms al on\n" +
     "		al.CODALIQ = prod.codaliq_nf\n" +
     (apenasProdutoAtivo == true ? " where ltrim(rtrim(prod.ativo)) = 'S' " : "") +
     //" where prod.codprod in (251,2848,3565,1255,3028,2409,4349,4718,3370,4466,4323,4112,2265,4286,4504,4888,1101,2311,4155,2165,586,3903,3323,2710,3071,1789,2839,2769,2247,2145,2388,1820,4287,1878,4059,4643,4875,3652,2148,3106,2627,2187,2997,1060,3322,1478,1543,3367,1229,3715,2150,3483,4650,3666,3802,3911,1726,2640,4722,2206,3452,1487,3841,3823,3924,2943,2755,4135,1009,4925,991,4387,1582,4198,2830,2452,3814,495,1065,4010,252,1913,2300,4485,1950,361,974,4475,4831,1501,958,325,3300,3859,4200,142,1999,4775,3250,1333,1783,3560,4035,4667,4986,2574,4944,1252,3282,4028,5048,3510,647,4137,296,4853,4910,3335,4388,4432,3164,4105,2816,3045,1364,1389,2019,2840,2266,599,1982,3410,458,4414,3564,904,5007,4355,2405,2693,1264,3242,2699,4728,823,4360,1848,280,2576,1374,3838,1981,2886,1746,2925,3492,1000,4416,4549,2522,2542,211,2230,4787,3843,5065,3012,1800,3926,1966,1696,2183,3491,788,2373,3384,2211,4449,2112,3979,3853,2339,712,616,3082,1910,4359,617,3107,977,4189,2179,743,2262,879,2465,1427,2992,2783,4453,508,1542,3241,2785,1296,4435,2729,748,4312,3707,570,2607,2968,3942,2152,1049,3912,896,2026,1242,4242,3029,2480,4550,2687,4097,2814,4170,1840,2692,3161,2355,2587,2675,3812,2677,1405,3458,4837,4149,108,2369,1089,3379,1475,3034,1017,1978,3146,4778,4229,2719,2584,3663,3572,4557,941,2353,2625,3910,804,2611,3900,803,4347,1091,4233,494,535,1018,3237,3496,1233,2384,928,1812,382,4027,4248,4932,1733,3061,257,2686,626,4648,3839,1432,2669,2801,2562,4554,5002,387,1301,4813,2561,2621,99,2073,94,1884,2423,3166,1951,2105,1322,4241,2777,3681,4274,544,2140,4788,62,4963,2445,1040,4530,390,1624,4740,4887,1511,2685,4063,3292,5038,5049,619,2940,1937,642,2146,1492,1057,1871,448,4390,1433,1979,3014,3293,4638,3024,3183,2061,811,2038,4672,4822,1240,3505,4406,4178,3228,2291,3576,384,4232,3185,2593,1438,994,2756,1367,4543,1669,1504,3881,4043,3943,3557,2013,2470,1313,3520,968,3726,1956,849,764,1004,3038,4971,3559,4266,1905,1165,973,2908,3423,2404,1813,4947,3009,4004,3630,5027,4956,521,4847,715,1823,2487,3936,4823,1234,2585,3424,3887,4839,596,1135,1891,3222,3993,1347,4732,3412,2010,4824,3599,932,3680,3291,4343,4825,614,2288,525,4140,840,2159,3776,4596,2812,2667,4040,3113,766,2916,4540,4915,2464,2317,1643,1508,4637,1035,1494,1227,4920,1675,3875,1566,2563,74,2604,3649,222,4219,92,4931,4448,1170,1751,1861,4848,4972,2958,814,2383,1196,1685,4077,4216,2253,4508,3731,3044,562,661,3568,1297,1886,2787,981,1896,4539,4908,3584,668,1580,4329,1277,4050,4326,1521,5045,1121,1545,415,5052,3442,3865,1016,2690,2483,2269,4240,3602,1218,3784,2909,2893,746,1444,2309,388,1832,884,2820,3497,4277,4440,4592,4755,3245,1849,2377,716,1436,511,1360,3806,4616,4157,1817,1931,1639,3469,2657,4493,43,963,3498,592,3574,4056,1845,3747,4629,3527,1027,436,4278,4293,2970,4710,2711,4886,2072,2456,2663,3548,749,2341,3692,5037,2923,548,1195,3389,2772,3751,1434,1661,2517,4247,4121,2334,4891,3674,4255,985,2822,3141,3207,2505,4547,2342,3304,4862,4496,1728,1892,1676,1804,1076,3355,1831,4364,1539,518,4916,2121,2676,1659,2054,330,3244,1476,2060,2437,2539,2750,1300,1818,4500,3984,745,4810,405,625,2950,4992,1531,4606,1546,2414,1854,883,2636,3159,4630,4651,2427,1771,4985,3524,4118,1502,2666,2229,4228,4339,838,3359,4695,1388,3350,3883,833,2267,295,30,2589,3693,2963,4275,834,882,2197,4786,1222,5042,1125,1866,3569,5057,2851,2624,3706,2213,3899,1786,3147,4428,3857,442,5013,512,1584,3391,71,1308,12,1887,4032,4652,2118,324,2240,3945,5008,4731,782,1437,1362,2917,4110,5024,2133,3330,1479,492,1919,409,918,4146,4401,623,3019,4503,4071,4698,3031,3696,1137,4664,3149,4224,2031,1373,841,2901,1451,365,1394,2359,459,938,1063,1842,2568,3341,3490,1178,1970,412,628,4626,366,2320,3042,4556,4144,83,851,1219,2375,3915,4039,3503,984,3091,2680,2760,5021,2398,2953,3826,4769,2012,2259,1955,3411,2101,4128,2815,1449,1450,869,5009,837,3774,3525,719,473,2999,2695,3787,662,1944,4784,224,4106,2000,1527,3376,2715,432,846,1834,4988,1292,3901,650,1203,4251,581,935,3126,4845,3085,1795,2920,1816,4351,2546,4978,4703,4961,1370,4011,1071,2824,5025,3436,1549,421,1653,3546,3508,2661,3934,1328,1986,3260,3388,3996,61,279,3093,563,3718,1534,2469,3914,4042,1640,3992,2,353,44,4374,2549,4942,2149,4609,4061,84,707,971,4919,784,3741,2316,3908,4443,4565,3097,1099,4948,406,3746,95,644,3444,3981,751,828,633,2279,4167,435,1529,193,4394,1612,1435,4075,669,675,5061,4515,4456,2138,708,1737,2173,2947,4169,4741,4333,5059,4398,1757,341,3699,2330,5046,3922,4422,4995,3011,571,2160,2107,3627,3035,1557,3927,4705,702,2797,637,750,114,3615,2284,558,3836,1385,951,2127,269,1518,835,844,3396,688,3968,2089,4000,3716,457,2347,2776,3101,4586,3386,4674,2100,2195,761,2042,4708,767,4471,1763,2610,1686,3390,2907,420,1973,2860,3962,4688,5075,2298,81,2429,2117,4007,4271,3346,2798,5055,1700,621,4780,734,4052,3466,1287,2674,3855,3735,1637,4303,2948,3057,4599,1469,3831,3098,507,4552,1788,3683,4236,1468,2784,4797,347,410,4049,2237,3336,2340,4217,383,2821,3616,4716,5047,1028,3027,1565,3111,2959,4352,3872,4143,4420,4623,368,465,2171,3677,1038,4338,229,2166,2305,4348,948,2226,2367,726,1994,1257,4924,677,3870,1943,1261,3796,1976,3907,4969,682,3150,2043,4126,3978,3074,2945,2861,3643,3100,3990,2106,2094,2559,3467,3278,4407,3213,2526,3006,3013,3216,4444,1266,3223,3951,1015,4545,3782,3558,864,3991,2002,797,2290,425,4693,4950,4245,589,2679,1206,482,2242,1562,3913,4964,1384,4572,1139,2588,4975,1894,2492,3001,1867,4220,2258,3918,1627,4881,532,4559,1411,1793,490,4014,2540,1895,4376,4939,1324,4949,3455,2613,4509,1199,2446,4914,4309,3385,2735,2845,2871,422,4468,1074,2985,2918,4645,802,3401,2941,2486,2598,1513,3871,3356,4776,2415,2263,3611,4452,1532,2182,3134,541,3124,1288,2363,3290,5031,856,3137,4584,3095,4577,4858,5001,756,1124,1231,3205,3995,2442,3195,119,4997,2489,1269,3851,660,2870,3828,4096,3937,4472,1043,1048,2260,4053,2389,2156,213,3079,4679,3785,5015,513,1610,4879,2528,4192,4512,3230,4298,2961,4836,484,1945,1578,1920,3509,3351,3155,2050,2572,3380,2479,2361,2877,1295,576,594,2255,1335,3092,4649,523,4123,4818,4849,1829,1984,559,1293,3553,2547,3858,2673,812,1926,4008,2771,3326,980,3720,4681,2036,2631,3402,4625,2245,4607,2091,1927,5023,3434,4658,763,289,1917,4294,3050,4929,1847,4297,4622,2163,537,853,4936,4403,4767,2934,4322,1736,2462,1708,225,2092,3248,1429,3608,3703,4270,3892,4280,3973,3070,4548,3342,4678,1317,4212,3475,4725,4828,1929,2560,4055,539,689,893,4434,2098,1723,4876,2281,4657,3581,151,2186,970,2096,3158,1903,2747,1868,806,4965,1628,1734,3781,3393,2873,3249,4991,1463,4316,1836,606,885,5050,5070,3118,880,333,800,2079,4802,82,2391,2998,5068,997,578,1699,1123,3226,1249,627,5035,4513,4382,1698,3440,2372,4478,2338,4753,2922,5041,4819,3997,4174,5003,1630,3955,5012,2656,3959,2842,1672,1156,2181,1412,4579,3400,3170,3364,3117,4340,3556,3142,2536,4460,3793,1499,2075,3201,4750,3669,3729,3931,472,1211,1460,954,889,3067,3171,3200,2348,3072,2322,2775,1798,4739,1381,1452,736,3122,875,4635,1952,2021,4959,634,758,4568,4833,4175,2975,3109,2044,1235,2537,4551,3739,3502,456,728,2731,2630,2937,3053,3078,2714,645,1839,1815,2761,4381,4832,1716,2869,3928,1581,4854,685,1270,110,1344,1342,754,5063,4894,262,655,3022,2053,4054,4602,3240,1684,1862,1764,2385,3717,762,3449,4899,2856,4506,2590,4523,4642,3064,2623,3365,3357,4089,4668,3526,3675,4205,4582,3425,2639,1767,4901,3476,2280,4806,3284,831,3551,39,1115,3701,3957,1772,3348,842,1262,4855,961,2214,641,97,3325,609,2511,1416,1843,4332,2969,2350,3963,3198,3474,2170,2506,3460,3172,228,1483,2496,3888,4324,2744,4656,5062,4417,1406,1652,464,4186,665,3334,1248,3051,785,845,1852,2855,1291,4877,1254,4641,100,1238,2007,3767,219,429,2235,3617,4840,8,1962,79,450,1711,4534,1309,2490,3655,1851,1221,4483,3256,4363,3461,2763,1741,3211,1459,3757,4419,235,4763,721,297,1560,3462,3684,2378,4906,2256,2827,441,999,1041,1819,2128,4872,227,1749,4684,4507,2742,1472,2774,391,1445,2393,2609,2307,3889,2913,1724,4467,810,4774,33,1398,1559,4497,1890,1743,2718,786,2600,2712,3712,3634,2912,3397,2519,1286,3188,4487,4484,3361,3711,2858,960,791,929,1172,3221,2332,1356,3439,4183,3947,4575,4344,1256,1149,2619,2749,3030,4327,2738,356,4671,4789,1719,2370,4078,3414,3971,3601,1307,1393,4585,3884,3809,3954,3815,3665,4701,2531,4558,487,1077,4108,1086,3206,2713,3960,924,1756,4867,1585,1949,2583,2382,4031,4262,3898,4962,2595,87,3670,1515,3829,4354,2190,1616,4437,873,2168,2441,2660,4051,2001,2381,1609,1722,2628,1455,629,1037,1687,375,4897,377,1239,933,2720,2689,1503,1608,2616,3595,5029,1179,2911,3299,118,3099,3514,2412,3846,231,1424,3366,4976,1454,328,1168,3538,2249,2501,4206,1538,4567,1331,652,4464,2862,4022,4921,590,2083,2086,3830,4990,1167,4193,4676,503,5014,3688,2510,4073,4644,3174,4715,4927,604,3714,3125,1946,4841,3354,1174,1974,2203,3817,1490,2790,2602,5071,2324,4134,772,4612,1765,2295,2082,4562,3021,996,4272,4598,1105,4150,4252,3622,910,3457,2448,4179,2543,3307,4821,1396,1936,189,2185,3459,4682,1509,3778,765,1299,2233,733,1822,3274,4491,1898,3562,3515,4086,2694,2972,2988,2897,1540,2807,4892,1821,4569,4790,451,4614,1031,1033,488,1019,1877,2773,2474,3850,517,1258,1400,2005,731,3247,4902,2580,646,4365,1766,2949,4225,288,4026,738,1375,3579,817,4103,2158,1337,18,4680,4838,4372,139,2705,1656,2957,2990,4009,4423,3470,4561,2201,2541,2006,4532,1061,3437,1552,1770,601,4691,3139,4199,2955,1654,3961,4194,2374,1014,796,72,4933,2466,747,3768,1948,598,2575,1281,1901,4994,1782,3236,3394,1023,561,4463,1683,2811,2212,2966,1428,2451,545,4603,1024,1246,4791,3704,73,355,4389,4998,2569,2708,3465,3123,4613,4201,516,2732,3593,4190,1064,4033,868,1215,2618,730,3269,593,1500,3438,4510,4852,2221,1673,3220,2224,233,485,1863,4321,3096,5044,3416,2426,4900,500,1226,91,3441,3742,3133,2770,3048,897,1522,3289,2722,602,499,5056,4465,1007,3362,1874,3531,4533,3499,4898,2557,4573,3867,1921,4161,2379,2806,4758,3566,3266,4977,3587,493,2509,2202,1378,2933,27,3779,759,4230,1768,19,2084,3084,3430,2357,3156,5033,1678,1418,1533,1062,1959,4168,4566,4653,1739,2066,2351,2172,531,1355,2678,3523,1526,1657,2040,4142,510,4070,2113,2276,3614,4411,5005,2222,4058,1078,4955,3094,3153,4923,4182,1882,2924,1230,3930,4366,1430,4690,4996,1402,2939,2319,2194,2836,2819,4989,4084,2120,3422,4090,2520,821,967,2725,1968,1717,3528,871,2188,1224,2368,127,939,3489,3555,2323,1422,1303,1568,3501,3333,2193,2064,2846,3472,4151,4066,1110,2011,2818,3160,105,795,349,4003,1096,3641,423,5036,2422,717,5000,3194,4145,2853,2257,430,2932,2730,2333,4564,3737,4429,657,1572,1670,3999,4610,1446,4514,4589,316,4172,132,3902,3152,1216,4689,17,3755,903,2843,947,2130,556,4870,3702,4392,2236,2225,69,4047,4665,2986,2612,3799,4869,3255,3271,3447,1596,2248,4455,694,4542,59,2791,3610,3360,3004,4526,4844,1606,4214,248,2099,3186,2418,4095,4619,2327,3059,1705,2736,1217,2854,4139,407,1514,463,4060,1619,698,2728,2025,2635,2403,242,3780,3077,2484,4820,236,1442,1204,2752,643,294,2915,3374,1844,4223,4373,2471,4913,4726,3421,3869,1409,2902,2967,4030,1166,4617,4945,4067,4713,4244,2331,2141,3628,608,4218,1850,5030,3764,768,3413,1932,1720,760,1750,3308,1923,234,895,654,2599,4133,4843,2982,3002,4375,1965,3063,4328,4926,5022,4361,1259,2178,3547,3728,1187,4072,4633,2478,4115,3635,14,2472,1693,3896,1059,2608,1431,2581,2653,461,859,1318,3694,3264,4269,4808,1316,4454,4835,2529,3512,792,1742,4357,4501,2123,3352,1682,2681,3169,3818,150,943,2134,2721,1294,926,2508,2498,3405,4446,4263,1302,2906,3845,3844,2477,2701,3648,240,217,3719,2059,2460,3448,5010,1709,2209,4305,4346,2037,4935,3404,1070,1350,2847,5039,1841,1209,3081,3535,4029,3647,1083,2737,2285,3519,4896,35,3612,4191,4313,3181,3239,3204,1954,2781,4495,437,1510,2813,3140,4859,3820,1283,2310,2592,847,2254,2397,3358,3949,4065,3808,5067,3319,2328,3989,1662,4593,4378,4909,2261,478,1171,3482,3381,2789,3765,899,1108,4922,1120,1600,1665,3849,4519,149,2844,4580,613,307,1289,706,2219,2503,2874,3685,4204,2444,4129,3128,3301,3604,1408,4842,4905,2838,3835,5069,489,3445,3086,3173,2717,2293,4634,1320,1855,241,2556,3229,4048,861,4874,2746,3163,4258,86,4830,1864,1079,2741,1740,1129,2654,3115,2421,4511,4709,995,4292,1569,303,3700,4494,4811,2942,3966,1488,1280,3138,4265,4368,3191,413,2646,4461,1915,3606,2390,3408,2065,4021,480,4946,4310,2169,3804,155,1164,314,1345,1902,2650,2991,4928,47,1133,1458,2329,3598,4692,2458,153,2004,2438,3724,729,1573,25,891,1706,468,3733,3026,4686,1180,1306,1346,2936,1453,4618,952,2914,1118,2605,4488,491,2764,2978,2979,3825,1372,2823,2638,2668,5019,1899,3068,612,3605,1611,1131,4889,4330,2488,3203,1729,1274,2521,2312,2514,822,1486,718,4173,638,3633,534,4829,863,2786,1136,3479,1029,1776,1575,2952,221,3003,2124,3771,3847,584,1051,3432,3840,4981,3521,1885,2326,3862,4210,3112,1022,2191,1122,1792,438,4427,701,3906,4801,4764,1421,1181,3530,1587,4960,2407,4893,2696,892,232,739,3545,4525,2103,1633,1858,2987,4675,4291,4380,1934,3653,3673,2274,2545,3427,3443,4302,2122,1629,4903,2753,2802,4016,4350,865,2102,3619,2408,4636,1721,1595,683,5026,876,4345,5043,1415,2344,3252,1005,4117,4113,752,2996,3132,3055,4799,3407,3848,4538,2354,514,1267,2956,3225,4677,1995,3929,2046,2491,3316,4068,1244,1426,3008,3253,4318,1794,2810,1130,4296,670,560,245,940,3075,4369,4235,4341,1142,1339,2865,4041,4863,4451,2177,2110,4911,4023,3456,2929,2645,3219,4620,2558,2303,2665,2716,3329,757,4215,2208,3810,2734,4002,378,2282,4249,5053,1423,2270,4260,1032,2964,4482,2482,1440,1718,2252,3798,2603,3790,284,2548,4025,4727,1893,615,1163,1098,2362,3965,1704,2018,2278,5064,3324,2485,4336,4696,3803,481,4036,4670,3372,3210,1648,2204,4044,4804,3891,4203,4687,1535,1688,3624,4080,3789,3923,2139,2809,4122,2794,2841,3549,3631,720,564,1030,247,3695,4702,2034,2434,3065,4578,4770,1155,3621,1992,2551,4535,1618,4005,4627,2314,4857,1544,3904,3378,3770,3315,65,4850,3777,1039,3165,2014,2817,1474,2726,3894,2476,2045,2525,1250,2606,4459,1467,2629,2849,890,3298,3761,3982,1012,3108,3932,4952,1343,1846,2617,3636,2015,3473,3972,2904,906,3976,3214,1911,3864,4267,75,3052,1752,572,3651,1272,1044,2346,4207,220,2207,3822,1010,1464,2431,2507,2071,2430,2308,3772,1650,4706,1417,431,2499,4383,3017,3506,3709,3000,2425,595,3016,4276,3197,4608,3279,3518,1284,4615,916,3667,3331,3543,3800,4486,2129,1689,4734,1245,2965,4522,1145,907,1159,306,2494,2161,2041,575,3419,3775,1939,4856,1173,917,1827,3406,4085,1785,2137,4001,4984,4694,4773,4264,2352,4430,2792,212,1918,962,1379,1528,1671,3571,1185,285,866,1870,4177,3958,497,1351,452,577,4102,3552,550,2743,2447,4712,1838,398,2658,3500,3516,3953,3383,1872,3644,4605,2889,214,1897,1541,2136,1114,2655,4743,3154,4081,2733,1069,1462,1626,908,4185,292,2659,3484,2980,1383,1273,3854,4878,651,3876,2500,4628,3969,4100,2022,2116,2662,3433,385,2597,1597,1073,2518,1777,11,4087,3723,1210,3303,1471,1961,1141,3561,411,2866,471,4846,4880,1761,2455,2024,1810,2977,5011,2420,3650,1085,57,1243,2766,3453,1744,2074,4666,1774,1964,3632,3946,4227,1207,3919,4968,424,5072,3040,3938,3259,2468,1225,1859,3446,2782,3588,3656,1200,1298,1725,4714,4683,4782,3047,2582,1567,1598,3760,4226,4470,843,2154,3905,3988,3861,3089,2643,4766,4006,2523,2153,1194,2232,3267,1690,3231,1407,2325,3890,3970,3873,4531,2199,2070,1969,3837,4091,4555,4600,1803,4062,4408,3023,1082,3687,4733,3224,4663,2416,2234,3511,3725,1359,1828,3753,3983,4957,4119,3403,4243,152,3794,3832,3110,3251,462,4436,789,3921,3980,3816,1321,1340,4441,979,1357,5017,4211,2459,3783,1420,2104,2076,4704,937,1996,3738,31,1799,3429,215,2246,4314,1457,4038,1477,4300,37,3477,3582,4405,3418,3713,2759,326,2238,3135,3261,2883,4092,4171,4759,755,1220,4966,4254,2087,538,2299,1940,2411,540,1990,4304,4803,2591,3944,1824,725,3623,1106,3880,4621,2356,4362,855,1138,4537,3263,1825,1925,2144,2973,4273,1368,2008,3343,2513,4812,2504,607,2670,4099,48,204,4938,4458,3454,1603,3554,364,4798,4317,2032,3513,4498,4120,3313,2497,2803,5066,4418,4591,709,2833,3939,4306,4107,3305,3268,4973,3662,552,3833,4967,1971,2745,676,1154,1376,1758,4163,3893,4141,3129,3756,3488,3306,2538,2345,2481,2512,3332,4197,4546,3863,1072,4553,1550,4590,1212,1928,1796,1066,4757,2633,536,4045,4815,955,1247,1325,2250,2930,4587,2088,460,4772,2376,4951,557,414,4404,2289,2684,4711,4765,4166,4433,2648,2688,4737,1068,2157,2566,4754,4817,4238,4331,862,3309,3998,2009,1319,3288,2077,1787,1111,3184,1251,1594,2386,3344,4756,3660,4088,4396,2315,1158,4751,3540,4288,2151,2594,574,3327,4983,2365,1775,1241,1507,2780,678,3317,4792,3769,4295,1214,1397,117,3744,4104,3752,1645,2852,4356,3377,3940,3537,3985,4873,2475,990,2016,433,1097,1745,4730,2976,3917,373,4866,3708,4611,1352,1470,3878,1260,1797,2552,3417,49,1365,3287,4827,1058,3909,2921,4424,1116,4516,60,3157,2435,1773,3629,327,4342,4130,5018,469,1053,2944,4724,3758,4284,3807,3025,1695,4719,2433,2530,4208,2808,3033,4834,3698,3827,3073,3592,3431,2081,496,3010,2192,3182,3977,3280,4520,1621,2632,4655,3664,2533,600,2463,2779,3243,3199,2067,2702,1857,2051,753,3302,1883,4377,1177,2227,603,3994,4993,565,1466,972,808,798,874,4370,4285,367,3486,445,4544,1914,832,3275,1933,2647,1161,1084,2174,498,4234,3797,4890,1119,3642,1801,453,1548,3668,3145,3272,887,2033,1963,3318,455,3311,4987,2306,630,4632,4281,850,4301,2047,3805,4597,2857,3495,872,921,2020,2241,4131,2577,4473,2439,3312,2055,4885,2931,1465,1253,4447,3270,1348,1879,3682,501,434,2515,2251,3116,3189,3310,3586,4999,3895,4749,218,1403,4114,2228,230,2900,3177,1880,2313,2974,1958,4020,1547,1957,4397,2366,2550,4259,486,4283,774,714,3254,4019,4943,4184,4748,1425,1473,4319,3066,5051,611,2304,1003,4013,4601,3925,1489,268,4064,1987,3589,428,1117,2115,3948,4974,2652,1759,2167,3478,98,3626,3690,2554,3882,674,2927,2164,4541,1912,4409,4279,1366,3533,635,2573,2896,3262,2620,2336,3541,2119,2778,1134,2671,2767,3190,2762,4290,3179,3060,2428,4462,3507,727,3834,886,781,4529,2962,4912,2703,4851,1,2217,4738,2454,636,2184,2527,4308,4980,573,2649,1285,1747,3371,4391,1972,1201,1414,1888,2155,1530,915,3193,4685,2859,4729,4152,4477,2863,1989,2724,4934,4794,4136,695,1835,3196,2709,3594,737,3209,3842,4581,2825,287,2800,3232,4583,2343,41,1056,2349,2983,3273,1830,2535,1701,4353,4647,4781,2895,3956,1419,4311,4268,4868,4826,988,2634,1491,4371,4768,1561,1930,1790,2626,569,3285,3517,1941,3120,3340,3471,311,2981,2027,2864,4761,85,3062,5032,3277,3339,4315,1382,3686,1537,1993,3218,278,2406,4400,3103,5034,2884,4518,4982,1658,2457,3131,3373,3192,4256,1517,2989,1265,3167,5028,1873,4861,3637,2436,216,4289,3036,4796,809,1869,2651,881,2757,3532,2395,1341,1525,1738,4402,3102,1753,3522,2642,1860,3933,1002,1556,2570,3058,1312,3504,533,3596,4158,1942,3678,1334,3661,3464,3399,2682,3162,3897,3276,1310,2664,588,4795,2586,4384,3575,2984,1564,2085,4138,1983,2450,446,587,2147,1327,1646,4187,3349,1282,3920,4431,3577,4662,3321,4457,4809,2218,3127,2371,618,3734,302,386,1805,348,2095,138,3597,724,1413,2069,826,1055,1353,2400,3578,1392,1694,1169,128,1075,1323,3795,4148,3585,4299,4415,3069,2534,3646,1960,4156,1461,2402,4654,2898,3874,3175,192,1865,3539,2707,4111,2919,4646,3591,4385,4399,2888,1641,4574,4777,4195,4884,2210,2473,2644,1182,4760,4930,1977,4953,1237,830,1714,2335,2614,3451,1126,1579,3296,1278,1975,4570,3015,858,2727,700,3212,4502,993,3119,2532,3580,3613,3151,1147,2758,4017,2272,3736,4639,2062,857,1205,3856,2683,4771,1268,4159,4940,2410,1336,2799,4181,4412,1586,2035,4640,3877,2768,3563,4941,2239,3265,3037,4661,2461,4744,1404,3468,3105,1583,3727,3868,2321,3618,3791,3493,3208,4528,4034,4746,2273,3583,1390,4083,4101,4450,4037,3952,4717,1558,1713,2023,4442,3246,3187,3987,2175,2946,4527,3748,824,1988,4481,3609,1305,649,579,1213,4079,101,1760,1330,2971,2885,1315,4595,1607,2765,3671,4594,3281,1395,3090,773,1837,1570,2826,2142,2788,1456,3916,1482,3114,1208,4588,63,1332,1900,3480,3743,4576,3935,2875,3168,4094,2467,1904,4231,529,1791,263,2837,3368,4439,3485,4093,5058,3320,1674,4438,3732,4386,1439,4421,4904,807,3638,2056,4604,549,1814,1512,2162,4735,1369,836,4721,1519,4253,2301,3640,4699,3590,3866,4367,3018,4046,4325,5060,957,2432,379,3570,2399,3257,2109,2899,3032,238,4445,4736,3426,4489,4076,4160,3387,4865,1399,2691,624,3283,3534,3600,2058,825,1326,585,223,1967,4907,3705,3104,2704,4426,4116,1980,2090,3824,2601,5004,2220,4069,1047,4469,2555,2834,2302,4395,3964,4176,2216,2294,2196,2318,3363,1833,1811,2835,4222,1304,4659,2132,3088,66,1377,827,2392,3860,4024,2748,779,2740,1997,4785,34,4816,2394,454,3395,684,4669,2615,4700,848,2108,2198,381,4814,2296,3852,2567,351,4631,313,1826,4124,898,1998,502,3536,1263,2063,3819,2052,3337,4162,3603,1702,3813,2495,1140,426,3689,1571,2754,4476,3544,3658,4505,3005,2231,1985,1681,2596,3054,1386,3529,1924,40,3041,3258,2579,1748,2068,2641,4783,3235,4202,965,4320,2424,1712,1223,3087,1358,3056,1276,4490,2277,3080,2449,3202,4188,226,4571,4805,3639,2017,2049,3338,3679,3786,4970,2751,4098,3227,3481,3234,4752,3130,3885,4337,4860,4747,1524,3143,4413,3297,783,2578,1599,4720,3148,3941,2882,3176,2223,3749,2910,3375,4153,4521,1184,3435,1186,1387,673,3730,931,1953,4883,2337,344,4723,1506,775,2960,3382,1536,4979,1001,3020,1183,1856,966,4125,1735,4517,4379,4358,3792,3409,1447,1889,2380,2401,4524,1448,2039,4937,3136,2413,1107,914,4132,2297,4082,2417,4479,1361,4779,2739,4250,1236,3801,2387,1622,2126,4917,5073,3762,3975,4793,591,3369,1876,2180,3043,744,2951,134,4213,3217,1443,4074,877,959,2706,4480,693,1916,1563,2995,580,732,3974,530,2243,4660,3691,3773,2903,2805,3654,2176,2954,2189,5054,2057,3398,4895,3392,3076,4474,4624,2571,4393,4257,1906,2453,3763,1480,1338,1881,3745,2364,2200,566,1651,3049,3710,4560,2125,2271,2419,950,1697,546,799,2135,1067,1103,447,3567,3967,4209,2396,2292,3233,2264,4018,1020,194,1935,4196,964,2804,3542,2205,3487,4246,3046,4015,352,4147,1042,2003,703,735,4762,3550,1938,2358,2544,787,2244,3178,3314,4563,133,3950,4871,870,4057,2831,1604,3238,3672,3428,3607,3657,4697,1677,3007,277,2793,394,2048,1025,4745,1481,1727,2553,5006,2832,1947,4800,1523,2723,2926,1354,3180,310,3450,3620,3886,905,467,648,5040,992,1090,1769,2443,3676,1380,3295,1349,96,4127,3420,4221,854,4164,1176,148,888,2111,3879,2672,4165,67,5016,3625,4307,3353,4707,4282,2493,4335,1703,2440,3645,568,1520,4334,998,1391,3294,4807,3721,1875,3754,2564,3347,2905,672,1715,860,4180,4954,978,1576,1620,1577,2215,3083,829,1410,2524,3286,3722,3697,2143,4261,2637,3821,894,1054,4864,3740,4499,3215,1660,3573,4492,982,1401,4742,4012,4673,4425,4154,1371,522,4410,1784,1649,3766,2887,210,605,4109,5074,1102,1329,1691,2622,3039,878,2097,3494,3659,867,3750,2268,4536,1034,3759,3788,1228,4958,4239,3986,3144,5020,4918,3811,3463,3345,376,4882,1574,2283,1922,2795,2502,3328,4237)\n" +
     "order by\n" +
     "	id"
     )) {
     Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
     while (rst.next()) {

     ProdutoIMP imp = new ProdutoIMP();
     imp.setImportLoja(getLojaOrigem());
     imp.setImportSistema(getSistema());
     imp.setImportId(rst.getString("id"));
     imp.setDataCadastro(rst.getDate("datacadastro"));
     imp.setDataAlteracao(rst.getDate("dataalteracao"));
     imp.setEan(rst.getString("codigobarras"));
     imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
     imp.setQtdEmbalagem(rst.getInt("qtdembalagem") == 0 ? 1 : rst.getInt("qtdembalagem"));
     imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
     imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
     imp.setDescricaoGondola(imp.getDescricaoCompleta());
     imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
     imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
     imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));
     imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
     imp.setPesoBruto(rst.getDouble("pesobruto"));
     imp.setPesoLiquido(rst.getDouble("pesoliquido"));
     imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
     imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
     imp.setEstoque(rst.getDouble("estoque"));
     imp.setCustoComImposto(rst.getDouble("custocomimposto"));
     imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
     imp.setPrecovenda(rst.getDouble("precovenda"));
     if (usarMargemBruta) {
     imp.setMargem(rst.getDouble("margem_bruta"));
     } else if (usaMargemLiquidaPraticada) {
     imp.setMargem(rst.getDouble("margemliquidapraticada"));
     } else {
     imp.setMargem(rst.getDouble("margem_param"));
     }
     imp.setSituacaoCadastro(("S".equals(rst.getString("ativo").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
     imp.setDescontinuado("S".equals(rst.getString("desativacompra")) || rst.getBoolean("descontinuado"));
     imp.setNcm(rst.getString("ncm"));
     imp.setCest(rst.getString("cest"));
     imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_credito"));
     imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_debito"));
     imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));

     imp.setIcmsDebitoId(rst.getString("icms_aliquota_saida"));
     if (copiarIcmsDebitoNaEntrada) {
     imp.setIcmsCreditoId(imp.getIcmsDebitoId());
     } else {
     if (this.utilizarIdIcmsNaEntrada) {
     imp.setIcmsCreditoId(imp.getIcmsDebitoId());
     } else {
     imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("icms_cst_entrada")));
     imp.setIcmsAliqEntrada(Utils.stringToDouble(rst.getString("icms_aliquota_entrada")));
     imp.setIcmsReducaoEntrada(Utils.stringToDouble(rst.getString("icms_reduzido_entrada")));

     if (imp.getIcmsCstEntrada() != 20) {
     imp.setIcmsReducaoEntrada(0);
     }
     if (imp.getIcmsCstEntrada() != 0
     && imp.getIcmsCstEntrada() != 10
     && imp.getIcmsCstEntrada() != 20
     && imp.getIcmsCstEntrada() != 70) {
     imp.setIcmsAliqEntrada(0);
     imp.setIcmsReducaoEntrada(0);
     }

     String str = (imp.getImportId() + " - ICMS Entrada: "
     + imp.getIcmsCstEntrada() + " - "
     + MathUtils.trunc(imp.getIcmsAliqEntrada(), 2) + " - "
     + MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1));

     if (!icms.containsKey(
     imp.getIcmsCstEntrada(),
     MathUtils.trunc(imp.getIcmsAliqEntrada(), 2),
     MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1)
     )) {
     imp.setIcmsCreditoId(imp.getIcmsDebitoId());
     } else {
     imp.setIcmsCreditoId(null);
     str += " - Encontrou";
     }
     LOG.finest(str);
     }
     }

     if (desconsiderarSetorBalanca) {
     String st = Utils.acertarTexto(rst.getString("unidade"), 2);
     if ("KG".equals(st)) {
     imp.seteBalanca(true);
     imp.setTipoEmbalagem("KG");
     } else {
     String desc = Utils.acertarTexto(imp.getDescricaoCompleta());
     if (pesquisarKGnaDescricao && desc.contains("KG") && !desc.matches(".*[0-9](\\s)*K?G")) {
     imp.seteBalanca(true);
     imp.setTipoEmbalagem("KG");
     } else {
     imp.setTipoEmbalagem("UN");
     imp.seteBalanca(false);
     }
     }                        
     imp.setValidade(rst.getInt("VALIDADE"));
     if(imp.isBalanca()) {
     qtdBalanca++;
     } else {
     qtdNormal++;
     }                        
     } else if (v_usar_arquivoBalanca) {
     ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getEan(), -2));
     if (bal != null) {
     qtdBalanca++;
     imp.seteBalanca(true);
     imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
     imp.setValidade(bal.getValidade() > 1 ? bal.getValidade() : rst.getInt("VALIDADE"));
     } else {
     qtdNormal++;
     imp.setValidade(0);
     imp.setTipoEmbalagem(rst.getString("unidade"));
     imp.seteBalanca(false);
     }
     } else {
     imp.seteBalanca((rst.getInt("e_balanca") == 1));
     imp.setTipoEmbalagem(rst.getString("unidade"));
     imp.setValidade(rst.getInt("VALIDADE"));
     if(imp.isBalanca()) {
     qtdBalanca++;
     } else {
     qtdNormal++;
     }
     }
                    
     if (this.utilizarEmbalagemDeCompra) {
     imp.setTipoEmbalagem(rst.getString("unidade_comp"));
     }

     vResult.add(imp);
     }
     }            
     LOG.fine("Produtos de balança: " + qtdBalanca + " normais: " + qtdNormal);
     }
     return vResult; 
     }*/
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select  \n"
                        + "	e.codprod id_produto, \n"
                        + "	rtrim(e.barra_emb) ean, \n"
                        + "	e.qtd qtdembalagem,\n"
                        + "	e.preco_unit precoAtacado,\n"
                        + "	p.PRECO_UNIT precoVenda\n"
                        + "from \n"
                        + "	EMBALAGENS e, PRODUTOS p\n"
                        + "where \n"
                        + "	e.CODPROD = p.CODPROD and\n"
                        + "	barra_emb is not null and\n"
                        + "	coalesce(e.PRECO_UNIT, 0) > 0\n"
                        + "order by \n"
                        + "	1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("precoAtacado"));
                        imp.setPrecovenda(rst.getDouble("precoVenda"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "   f.codfornec,\n"
                    + "	f.razao,\n"
                    + "	coalesce(f.fantasia, f.RAZAO) fantasia,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.bairro,\n"
                    + "	f.complemento,\n"
                    + "	f.cidade,\n"
                    + "	f.estado,\n"
                    + "	f.cep,\n"
                    + "	f.telefone,\n"
                    + "	f.fax,\n"
                    + "	f.email,\n"
                    + "	f.celular,\n"
                    + "	f.fone1,\n"
                    + "	f.contato,\n"
                    + "	f.ie,\n"
                    + "	f.cnpj_cpf,\n"
                    + "	f.agencia,\n"
                    + "	f.banco,\n"
                    + "	f.conta,\n"
                    + "	f.dtcad,\n"
                    + "	f.valor_compra,\n"
                    + "	f.ativo, \n"
                    + "   f.obs, \n"
                    + "   c.descricao as descricaopag, \n"
                    + "   f.pentrega, \n"
                    + "   f.pvisita,\n"
                    + "   coalesce(\n"
                    + "   	case \n"
                    + "             when codtipofornec = 1 then 1 \n"
                    + "             when codtipofornec = 2 then 1 \n"
                    + "		    when codtipofornec = 3 then 0 \n"
                    + "		    when codtipofornec = 4 then 3 \n"
                    + "		    when codtipofornec = 5 then 2 \n"
                    + "		    when codtipofornec = 6 then 3 \n"
                    + "		    when codtipofornec = 7 then 7 \n"
                    + "		    when codtipofornec = 8 then 8 \n"
                    + "		end,\n"
                    + "		2\n"
                    + "	) as codtipofornec, \n"
                    + "    simples \n"
                    + "from \n"
                    + "    fornecedores f \n"
                    + "    left join condpagto c on\n"
                    + "    	f.codcondpagto = c.codcondpagto \n"
                    + "order by\n"
                    + "	codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFORNEC"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setCnpj_cpf(rst.getString("CNPJ_CPF"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setTel_principal(Utils.stringLong(rst.getString("TELEFONE")));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));

                    imp.setObservacao(rst.getString("OBS"));

                    if ((rst.getString("DESCRICAOPAG") != null) && (!rst.getString("DESCRICAOPAG").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " Cond. pag: " + Utils.acertarTexto(rst.getString("DESCRICAOPAG")));
                    }
                    if ((rst.getString("PENTREGA") != null) && (!rst.getString("PENTREGA").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " - Prazo entrega: " + rst.getInt("PENTREGA"));
                    }
                    if ((rst.getString("PVISITA") != null) && (!rst.getString("PVISITA").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " - Prazo visita: " + rst.getInt("PVISITA"));
                    }
                    imp.setObservacao(Utils.acertarTexto(rst.getString("OBS")) + " Cond. pag: "
                            + Utils.acertarTexto(rst.getString("DESCRICAOPAG"))
                            + " - Prazo entrega: " + rst.getInt("PENTREGA") + " - Prazo visita: " + rst.getInt("PVISITA"));

                    imp.setDatacadastro(rst.getDate("DTCAD"));
                    imp.setTipoFornecedor(TipoFornecedor.getById(rst.getInt("CODTIPOFORNEC")));

                    if (Utils.acertarTexto(rst.getString("simples")).equals("S")) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }
                    imp.addTelefone("FAX", rst.getString("FAX"));
                    if ((rst.getString("CONTATO") != null)
                            && (!rst.getString("CONTATO").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("CONTATO"),
                                (Utils.stringLong(rst.getString("FONE1")).equals("0") ? rst.getString("TELEFONE") : rst.getString("FONE1")),
                                rst.getString("CELULAR"),
                                TipoContato.COMERCIAL,
                                rst.getString("EMAIL")
                        );
                    } else {
                        if ((rst.getString("EMAIL") != null)
                                && (!rst.getString("EMAIL").trim().isEmpty())) {
                            imp.addContato(
                                    "2",
                                    "EMAIL",
                                    null,
                                    null,
                                    TipoContato.NFE,
                                    rst.getString("EMAIL").toLowerCase()
                            );
                        }
                        if ((rst.getString("CELULAR") != null)
                                && (!rst.getString("CELULAR").trim().isEmpty())) {
                            imp.addContato(
                                    "3",
                                    "CELULAR",
                                    null,
                                    rst.getString("CELULAR"),
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                        if ((rst.getString("FONE1") != null)
                                && (!rst.getString("FONE1").trim().isEmpty())) {
                            imp.addContato(
                                    "4",
                                    "TELEFONE",
                                    rst.getString("FONE1"),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                    }
                    /*
                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select f.CODFORNEC, "
                                + "cp.CODCONDPAGTO, "
                                + "replace(cp.DESCRICAO,'-','/') descricao, "
                                + "cp.NPARCELAS\n"
                                + "from FORNECEDORES f\n"
                                + "inner join CONDPAGTO cp on cp.CODCONDPAGTO = f.CODCONDPAGTO \n"
                                + "where f.CODFORNEC = " + imp.getImportId()
                                + "order by f.CODFORNEC, cp.CODCONDPAGTO"
                        )) {
                            int contador = 1;
                            if (rst2.next()) {
                                int numParcelas = rst2.getInt("NPARCELAS");
                                String descricao = Utils.formataNumeroParcela(rst2.getString("DESCRICAO").replace("//", "/").trim());
                                String[] cods = descricao.split("\\/");
                                if (numParcelas > 0) {

                                } else {
                                    if (!descricao.contains("/")) {
                                        if ("0".equals(Utils.formataNumero(descricao))) {
                                            imp.addPagamento(String.valueOf(contador), 1);
                                        } else {
                                            imp.addPagamento(String.valueOf(contador), Integer.parseInt(Utils.formataNumero(descricao).trim()));
                                        }

                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                    } else {
                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                        for (int i = 0; i < cods.length; i++) {
                                            if (!"".equals(cods[i])) {
                                                switch (i) {
                                                    case 0:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 1:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 2:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 3:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 4:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 5:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 6:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 7:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 8:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 9:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 10:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 11:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 12:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 13:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 14:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 15:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 16:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 17:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 18:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 19:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 20:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 21:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 22:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 23:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 24:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                                contador++;
                            }
                        }
                    }
                     */
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.CODFORNEC id_fornecedor,\n"
                    + "	pf.CODPROD id_produto,\n"
                    + "	pf.CODREF codigoexterno,\n"
                    + "	coalesce(pf.QTD_EMB, 1) qtdembalagem,\n"
                    + "	pf.DATAREF dataalteracao,\n"
                    + "	p.QTD_EMB qtd_cotacao\n"
                    + "from\n"
                    + "	PRODREF pf\n"
                    + "	join produtos p on\n"
                    + "		pf.CODPROD = p.CODPROD"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    if (this.usarQtdEmbDoProduto) {
                        imp.setQtdEmbalagem(rst.getDouble("qtd_cotacao"));
                    } else {
                        imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    }
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "    CASE COALESCE(PESSOA,'F') WHEN 'F' THEN 1 ELSE 0 END AS PESSOA, \n"
                    + "    CODCLIE, \n"
                    + "    RAZAO, \n"
                    + "    ENDERECO, \n"
                    + "    COMPLEMENTO, \n"
                    + "    BAIRRO, \n"
                    + "    CIDADE, \n"
                    + "    ESTADO, \n"
                    + "    CEP, \n"
                    + "    NUMERO, \n"
                    + "    CNPJ_CPF, \n"
                    + "    TELEFONE, \n"
                    + "    RG,\n"
                    + "    IE, \n"
                    + "    FONE1, \n"
                    + "    FONE2, \n"
                    + "    EMAIL, \n"
                    + "    DTANIVER, \n"
                    + "    coalesce(LIMITECRED,0) LIMITECRED, \n"
                    + "    coalesce(RENDA,0) RENDA, \n"
                    + "    CARGO, \n"
                    + "    EMPRESA, \n"
                    + "    FONE_EMP, \n"
                    + "    CASE ATIVO WHEN 'S' THEN 1 ELSE 0 END AS ATIVO, \n"
                    + "    ESTADOCIVIL, \n"
                    + "    CASE SEXO WHEN 'F' THEN 1 ELSE 2 END AS SEXO, \n"
                    + "    NOMEPAI, \n"
                    + "    NOMEMAE, \n"
                    + "    DTALTERA, \n"
                    + "    CELULAR, \n"
                    + "    NOMECONJUGE, \n"
                    + "    CARGOCONJUGE, \n"
                    + "    CPF_CONJUGE, \n"
                    + "    RG_CONJUGE, \n"
                    + "    coalesce(RENDACONJUGE,0) as RENDACONJUGE, \n"
                    + "    DTCAD, \n"
                    + "    CASE ESTADOCIVIL WHEN 'S' THEN 1 \n"
                    + "    WHEN 'C' THEN 2 \n"
                    + "    WHEN 'V' THEN 3 \n"
                    + "    WHEN 'A' THEN 4 \n"
                    + "    WHEN 'O' THEN 5 ELSE 0 END AS ESTADOCIVILNOVO, \n"
                    + "    coalesce(OBS1, '') + ' ' +\n"
                    + "    coalesce(CONTATO, '') + ' ' +\n"
                    + "    coalesce(REF1_NOME, '') + ' ' +\n"
                    + "    coalesce(REF2_NOME, '') + ' ' +\n"
                    + "    coalesce(FONE1, '') AS OBS,\n"
                    + "    coalesce(obs2, '') + ' Agencia ' +\n"
                    + "    coalesce(agencia, '') + ' Banco ' +\n"
                    + "    coalesce(banco, '') + ' CC ' +\n"
                    + "    coalesce(cc, '') as obs2, \n"
                    + "    BLOQCARTAO, \n"
                    + "    senhacartao \n"
                    + "FROM \n"
                    + "    CLIENTES \n"
                    + "where \n"
                    + "    CODCLIE >= 1 \n"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("CODCLIE"));
                    if (removerCodigoCliente) {
                        String idCliente = "";
                        idCliente = rst.getString("CODCLIE").substring(3, rst.getString("CODCLIE").length());
                        imp.setId(idCliente);
                    }

                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setDataCadastro(rst.getDate("DTCAD"));
                    imp.setCnpj(rst.getString("CNPJ_CPF"));
                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("RG"));
                    } else if ((rst.getString("IE") != null)
                            && (!rst.getString("IE").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("IE"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setTelefone(Utils.stringLong(rst.getString("TELEFONE")));
                    imp.setCelular(Utils.stringLong(rst.getString("CELULAR")));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setNomePai(rst.getString("NOMEPAI"));
                    imp.setNomeMae(rst.getString("NOMEMAE"));
                    imp.setNomeConjuge(rst.getString("NOMECONJUGE"));
                    imp.setDataNascimento(rst.getDate("DTANIVER"));
                    imp.setValorLimite(rst.getDouble("LIMITECRED"));
                    imp.setEmpresa(rst.getString("EMPRESA"));
                    imp.setEmpresaTelefone(Utils.stringLong(rst.getString("FONE_EMP")));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("RENDA"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setObservacao2(rst.getString("OBS2"));
                    imp.setSenha(Utils.stringToInt(rst.getString("senhacartao")));
                    imp.setAtivo("1".equals(rst.getString("ATIVO")));
                    if ((rst.getString("BLOQCARTAO") != null)
                            && (!rst.getString("BLOQCARTAO").trim().isEmpty())) {
                        if ("N".equals(rst.getString("BLOQCARTAO").trim())) {
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setBloqueado(false);
                        } else {
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setBloqueado(true);
                        }
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                        imp.setBloqueado(true);
                    }
                    imp.setSexo("1".equals(rst.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    if ((rst.getString("ESTADOCIVILNOVO") != null)
                            && (!rst.getString("ESTADOCIVILNOVO").trim().isEmpty())) {
                        if (null != rst.getString("ESTADOCIVILNOVO")) {
                            switch (rst.getString("ESTADOCIVILNOVO")) {
                                case "1":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "2":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "3":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "4":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "5":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }

                    if ((rst.getString("FONE1") != null)
                            && (!rst.getString("FONE1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 1",
                                Utils.stringLong(rst.getString("FONE1")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("FONE2") != null)
                            && (!rst.getString("FONE2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 2",
                                Utils.stringLong(rst.getString("FONE2")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "EMAIL",
                                null,
                                null,
                                rst.getString("EMAIL")
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        int decisao = JOptionPane.showConfirmDialog(null, "Deseja importar apenas contas em aberto?");
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            StringBuilder builder = new StringBuilder();

            for (Iterator<Integer> iterator = this.TipoDocumentoRotativo.iterator(); iterator.hasNext();) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            String sqlContasAbertas = "SELECT \n"
                    + "CODRECEBER AS ID, \n"
                    + "CLIENTES.CNPJ_CPF, \n"
                    + "CODRECEBER, NUMTIT, \n"
                    + "RECEBER.CODCLIE, \n"
                    + "NOTAECF, \n"
                    + "DTVENCTO, \n"
                    + "DTEMISSAO, \n"
                    + "DTPAGTO, \n"
                    + "SITUACAO, \n"
                    + "VALORPAGO, \n"
                    + "coalesce(VALOR,0) VALOR, \n"
                    + "coalesce(VALORJUROS,0) VALORJUROS, \n"
                    + "OBS \n"
                    + "FROM \n"
                    + "RECEBER \n"
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE \n"
                    + "where UPPER(SITUACAO) = 'AB'\n"
                    + "and RECEBER.CODLOJA = " + getLojaOrigem()+ " \n"
                    + "order by DTEMISSAO";

            String sqlContasGerais = "SELECT \n"
                    + "CODRECEBER AS ID, \n"
                    + "CLIENTES.CNPJ_CPF, \n"
                    + "CODRECEBER, NUMTIT, \n"
                    + "RECEBER.CODCLIE, \n"
                    + "NOTAECF, \n"
                    + "DTVENCTO, \n"
                    + "DTEMISSAO, \n"
                    + "DTPAGTO, \n"
                    + "SITUACAO, \n"
                    + "VALORPAGO, \n"
                    + "coalesce(VALOR,0) VALOR, \n"
                    + "coalesce(VALORJUROS,0) VALORJUROS, \n"
                    + "OBS \n"
                    + "FROM \n"
                    + "RECEBER \n"
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE \n"
                    + "where UPPER(SITUACAO) != 'CA'\n"
                    + "and RECEBER.CODTIPODOCUMENTO IN ('" + builder + "') \n"
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + "\n"
                    + "order by DTEMISSAO";

            String sqlEscolhido = decisao == 0 ? sqlContasAbertas : sqlContasGerais;

            try (ResultSet rst = stm.executeQuery(
                    sqlEscolhido
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdCliente(rst.getString("CODCLIE"));
                    if (removerCodigoCliente) {
                        String idCliente = "";
                        idCliente = rst.getString("CODCLIE").substring(3, rst.getString("CODCLIE").length());
                        imp.setIdCliente(idCliente);
                    }
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataVencimento(rst.getDate("DTVENCTO"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setJuros(rst.getDouble("VALORJUROS"));
                    imp.setValor(rst.getDouble("VALOR"));

                    if ("BT".equals(rst.getString("SITUACAO").toUpperCase().trim()) || "BP".equals(rst.getString("SITUACAO").toUpperCase().trim())) {
                        imp.addPagamento(imp.getId(), imp.getValor(), 0, 0, rst.getDate("DTPAGTO"), imp.getObservacao() + " - PAGO");
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            StringBuilder builder = new StringBuilder();

            for (Iterator<Integer> iterator = this.TipoDocumentoCheque.iterator(); iterator.hasNext();) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RAZAO, "
                    + "CLIENTES.RG, "
                    + "CODRECEBER, "
                    + "NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "RECEBER.NUMCHEQUE, "
                    + "RECEBER.CODTIPODOCUMENTO, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE, "
                    + "RECEBER.CODBANCO, "
                    + "RECEBER.AGENCIA, "
                    + "RECEBER.CONTACORR "
                    + "FROM RECEBER "
                    + "INNER JOIN CLIENTES ON "
                    + "CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "and RECEBER.CODTIPODOCUMENTO IN (" + builder.toString() + ") "
                    + "order by DTEMISSAO "
            )) {
                while (rst.next()) {
                    //int idBanco = new BancoDAO().getId(rst.getInt("CODBANCO"));
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("ID"));
                    imp.setDate(rst.getDate("DTEMISSAO"));
                    imp.setDataDeposito(rst.getDate("DTVENCTO"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setNumeroCheque(rst.getString("NUMCHEQUE"));
                    imp.setAgencia(rst.getString("AGENCIA"));
                    imp.setConta(rst.getString("CONTACORR"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCpf(rst.getString("CNPJ_CPF"));
                    imp.setNome(rst.getString("RAZAO"));
                    imp.setRg(rst.getString("RG"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setBanco(rst.getInt("CODBANCO"));

                    if ((v_tipoDocumentoCheque == 5)
                            || (v_tipoDocumentoCheque == 13)) {
                        imp.setAlinea(11);
                    } else {
                        imp.setAlinea(0);
                    }

                    if (v_tipoDocumentoCheque == 5) {
                        imp.setIdLocalCobranca(1);
                    } else if (v_tipoDocumentoCheque == 13) {
                        imp.setIdLocalCobranca(2);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODTIPODOCUMENTO, DESCRICAO from TIPODOCUMENTO order by CODTIPODOCUMENTO"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("CODTIPODOCUMENTO"),
                            rst.getString("CODTIPODOCUMENTO") + " - "
                            + rst.getString("DESCRICAO")));
                }
            }
        }
        return result;
    }

    public static class TipoDocumentoRecord {

        public int id;
        public String descricao;
        public boolean selected = false;

        public TipoDocumentoRecord(int id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

    }

    public List<TipoDocumentoRecord> getTipoDocumentoReceber() throws Exception {
        List<TipoDocumentoRecord> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODTIPODOCUMENTO, DESCRICAO from TIPODOCUMENTO order by CODTIPODOCUMENTO"
            )) {
                while (rst.next()) {
                    result.add(new TipoDocumentoRecord(
                            rst.getInt("CODTIPODOCUMENTO"),
                            rst.getString("CODTIPODOCUMENTO") + " - "
                            + rst.getString("DESCRICAO")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        int decisao = JOptionPane.showConfirmDialog(null, "Deseja importar apenas contas em aberto?");
        List<ContaPagarIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            String sqlAbertas = "SELECT "
                    + "CODPAGAR, "
                    + "CODFORNEC, "
                    + "coalesce(NOTA, '0') NOTA, "
                    + "DESD PARCELA, "
                    + "VALOR, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "OBS, "
                    + "OBS2, "
                    + "VALORPAGO, "
                    + "DTPAGTO, "
                    + "DTENTRADA "
                    + "FROM PAGAR "
                    + "where CODLOJA = " + getLojaOrigem() + " "
                    + "and DTPAGTO IS NULL and DTVENCTO IS NOT NULL AND\n"
                    + " SITUACAO = 'AB'\n"
                    + "order by DTEMISSAO";

            String sqlTodas = "SELECT \n"
                    + "CODPAGAR, \n"
                    + "CODFORNEC, \n"
                    + "coalesce(NOTA, '0') NOTA, \n"
                    + "DESD PARCELA, \n"
                    + "VALOR, \n"
                    + "DTVENCTO, \n"
                    + "DTEMISSAO, \n"
                    + "OBS, \n"
                    + "OBS2, \n"
                    + "VALORPAGO, \n"
                    + "DTPAGTO, \n"
                    + "DTENTRADA,\n"
                    + "SITUACAO \n"
                    + "FROM PAGAR \n"
                    + "where CODLOJA = " + getLojaOrigem() + "\n"
                    + "and SITUACAO != 'CA'\n"
                    + "order by DTEMISSAO";

            String sqlEscolhido = decisao == 0 ? sqlAbertas : sqlTodas;
            System.out.println(sqlEscolhido);
            try (ResultSet rst = stm.executeQuery(sqlEscolhido)) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("CODPAGAR"));
                    imp.setIdFornecedor(rst.getString("CODFORNEC"));

                    String doc = Utils.formataNumero(rst.getString("NOTA"));

                    imp.setNumeroDocumento(doc);

                    if (doc != null && !"".equals(doc)) {
                        if (doc.length() > 6) {
                            imp.setNumeroDocumento(doc.substring(0, 6));
                        }
                    }

                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataEntrada(rst.getDate("DTENTRADA"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("DTENTRADA"));
                    imp.setObservacao((rst.getString("OBS") == null ? "" : rst.getString("OBS")) + " "
                            + (rst.getString("OBS2") == null ? "" : rst.getString("OBS2")));
                    String parcela = Utils.formataNumero(rst.getString("PARCELA"));

                    if (null == rst.getString("DTPAGTO")) {
                        ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("DTVENCTO"), imp.getValor());
                        parc.setNumeroParcela(Integer.valueOf(parcela));

                    }
                    /*if ("BP".equals(rst.getString("SITUACAO").trim()) || "BT".equals(rst.getString("SITUACAO").trim())){
                        imp.addVencimento(
                                rst.getDate("DTVENCTO"),
                                rst.getDouble("VALORPAGO"),
                                rst.getDate("DTPAGTO")).setObservacao(imp.getObservacao());
                    }*/

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarReceberDevolucao(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados ReceberDevolucao...");
            vResult = getReceberDevolucao();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberDevolucaoVO> getReceberDevolucao() throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    String obs = "";
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            if ((rst.getString("NOTAECF") != null)
                                    && (!rst.getString("NOTAECF").trim().isEmpty())) {
                                if (rst.getString("NOTAECF").trim().length() > 9) {
                                    obs = "NOTAECF " + rst.getString("NOTAECF");
                                } else {
                                    imp.setNumeroNota(Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF"))));
                                }
                            } else {
                                imp.setNumeroNota(0);
                            }
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim())
                                    + " " + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()) + " " + obs);
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarReceberVerba(int idLojaVR) throws Exception {
        List<ReceberVerbaVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados ReceberVerba...");
            vResult = getReceberVerba();
            if (!vResult.isEmpty()) {
                new ReceberVerbaDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberVerbaVO> getReceberVerba() throws Exception {
        List<ReceberVerbaVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CLIENTES.RAZAO, "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RG, "
                    + "CLIENTES.IE, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberVerbaVO imp = new ReceberVerbaVO();
                            imp.setIdFornecedor(idFornecedor);
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setRepresentante(rst.getString("RAZAO") == null ? "" : rst.getString("RAZAO").trim());
                            imp.setTelefone((rst.getString("TELEFONE") == null ? "" : rst.getString("TELEFONE").trim()));
                            imp.setCpfRepresentante(Long.parseLong(Utils.formataNumero("CNPJ_CPF")));
                            if ((rst.getString("RG") != null)
                                    && (!rst.getString("RG").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("RG").trim());
                            } else if ((rst.getString("IE") != null)
                                    && (!rst.getString("IE").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("IE").trim());
                            } else {
                                imp.setRgRepresentante("");
                            }
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim()) + " "
                                    + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()));
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarProdutosGetWay(String i_arquio) throws Exception {
        int linha = 0;
        Statement stm = null;
        StringBuilder sql = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquio), settings);
            Sheet[] sheets = arquivo.getSheets();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellPr_codInt = sheet.getCell(0, i);
                    Cell cellPr_cBarra = sheet.getCell(1, i);
                    Cell cell_Pr_nome = sheet.getCell(2, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.produtos_getway ("
                            + "codprod, "
                            + "barras, "
                            + "descricao) "
                            + "values ("
                            + "'" + cellPr_codInt.getContents().trim() + "' ,"
                            + "lpad('" + cellPr_cBarra.getContents().trim() + "', 14, '0'), "
                            + "'" + Utils.acertarTexto(cell_Pr_nome.getContents().trim()) + "')");
                    stm.execute(sql.toString());
                    System.out.println(i);
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private String formatTributacaoId(String id, double fcp) {
        return String.format("%s-%.2f", id, fcp);
    }

    //Utilizado este método com novo script para cliente que utiliza alíquota FCP
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select \n"
                    + "	distinct\n"
                    + "	ltrim(rtrim(replace(icms.codaliq,'\\\\','\\\\\\\\\\\\'))) id,\n"
                    + "	coalesce(fcp.VALORTRIB, 0) fcp,\n"
                    + "	icms.descricao,\n"
                    + "	coalesce(icms.VALORTRIB, 0) as valor,\n"
                    + "	coalesce(icms.REDUCAO, 0) as reducao,\n"
                    + "	coalesce(icms.aliquota, 0) as aliquota\n"
                    + "from \n"
                    + "	aliquota_icms icms\n"
                    + "	left join\n"
                    + "		PRODUTOS p on icms.CODALIQ = p.CODALIQ\n"
                    + "	left join PROD_TRIBFCP fcp on p.codprod = fcp.CODPROD\n"
                    + "where\n"
                    + "	icms.descricao is not null\n"
                    + "order by\n"
                    + "	id, fcp"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            formatTributacaoId(rs.getString("id"), rs.getDouble("fcp")),
                            String.format("%s + FCP %.2f %%", rs.getString("descricao"), rs.getDouble("fcp")),
                            0,
                            rs.getDouble("valor"),
                            rs.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pi.codprod produtopai,\n"
                    + "	p.descricao descricaopai,\n"
                    + "	pi.qtd qtdembalagem,\n"
                    + "	pi.codprod_itm produtofilho,\n"
                    + "	pitem.descricao descricaofilho\n"
                    + "from\n"
                    + "	prod_itens pi\n"
                    + "	join produtos p on\n"
                    + "		pi.CODPROD = p.CODPROD\n"
                    + "	join produtos pitem on\n"
                    + "		pi.CODPROD_ITM = pitem.CODPROD\n"
                    + "order by\n"
                    + "	produtopai, produtofilho"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rst.getString("produtopai"));
                    imp.setDescricao(rst.getString("descricaopai"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setProdutoAssociadoId(rst.getString("produtofilho"));
                    imp.setDescricaoProdutoAssociado(rst.getString("descricaofilho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void setDesconsiderarSetorBalanca(boolean desconsiderarSetorBalanca) {
        this.desconsiderarSetorBalanca = desconsiderarSetorBalanca;
    }

    public void setPesquisarKGnaDescricao(boolean pesquisarKGnaDescricao) {
        this.pesquisarKGnaDescricao = pesquisarKGnaDescricao;
    }

    public void setCopiarIcmsDebitoNaEntrada(boolean copiarIcmsDebitoNaEntrada) {
        this.copiarIcmsDebitoNaEntrada = copiarIcmsDebitoNaEntrada;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    coalesce(cx.cliente, '') as idclientepreferencial,\n"
                    + "    min(cx.hora) as horainicio,\n"
                    + "    max(cx.hora) as horatermino,\n"
                    + "    min(case when cx.cancelado = 'N' then 0 else 1 end) as cancelado,\n"
                    + "    sum(cx.totitem) as subtotalimpressora,\n"
                    + "    cl.cnpj_cpf cpf,\n"
                    + "    sum(isnull(cx.descitem,0)) desconto,\n"
                    + "    sum(isnull(cx.acrescitem, 0)) acrescimo,\n"
                    + "    pdv.NUM_SERIE numeroserie,\n"
                    + "    pdv.IMP_MODELO modelo,\n"
                    + "    pdv.IMP_MARCA marca,\n"
                    + "    cl.razao nomecliente,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join parampdv pdv on cx.codloja = pdv.CODLOJA and cx.codcaixa = pdv.CODCAIXA\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + FORMAT.format(dataInicio) + "', 23) and convert(date, '" + FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')\n"
                    + "group by\n"
                    + "	   cx.coo,\n"
                    + "    cx.codcaixa,\n"
                    + "    cx.data,\n"
                    + "    coalesce(cx.cliente, ''),\n"
                    + "	   cl.cnpj_cpf,\n"
                    + "    pdv.NUM_SERIE,\n"
                    + "    pdv.IMP_MODELO,\n"
                    + "    pdv.IMP_MARCA,\n"
                    + "    cl.razao,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "order by\n"
                    + "    data, numerocupom";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));

                        String trib = rst.getString("codaliq_venda");
                        if (trib == null || "".equals(trib)) {
                            trib = rst.getString("codaliq_produto");
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             TA	7.00	ALIQUOTA 07%
             TB	12.00	ALIQUOTA 12%
             TC	18.00	ALIQUOTA 18%
             TD	25.00	ALIQUOTA 25%
             TE	11.00	ALIQUOTA 11%
             I	0.00	ISENTO
             F	0.00	SUBST TRIBUTARIA
             N	0.00	NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "TA":
                    cst = 0;
                    aliq = 7;
                    break;
                case "TB":
                    cst = 0;
                    aliq = 12;
                    break;
                case "TC":
                    cst = 0;
                    aliq = 18;
                    break;
                case "TD":
                    cst = 0;
                    aliq = 25;
                    break;
                case "TE":
                    cst = 0;
                    aliq = 11;
                    break;
                case "TF":
                    cst = 0;
                    aliq = 11;
                    break;
                case "TG":
                    cst = 0;
                    aliq = 4.5;
                    break;
                case "TH":
                    cst = 0;
                    aliq = 8;
                    break;
                case "TI":
                    cst = 0;
                    aliq = 4;
                    break;
                case "TJ":
                    cst = 0;
                    aliq = 9.14;
                    break;
                case "TL":
                    cst = 0;
                    aliq = 13.3;
                    break;
                case "TM":
                    cst = 0;
                    aliq = 4.14;
                    break;
                case "TN":
                    cst = 0;
                    aliq = 4.7;
                    break;
                case "TO":
                    cst = 0;
                    aliq = 11.2;
                    break;
                case "TP":
                    cst = 0;
                    aliq = 8.40;
                    break;
                case "TQ":
                    cst = 0;
                    aliq = 8.83;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    cx.id,\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    cx.codprod as produto,\n"
                    + "    pr.DESC_PDV as descricao,    \n"
                    + "    isnull(cx.qtd, 0) as quantidade,\n"
                    + "    isnull(cx.totitem, 0) as total,\n"
                    + "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    isnull(cx.descitem, 0) as desconto,\n"
                    + "    isnull(cx.acrescitem, 0) as acrescimo,\n"
                    + "    case\n"
                    + "     when LEN(cx.barra) > 14 \n"
                    + "     then SUBSTRING(cx.BARRA, 4, LEN(cx.barra))\n"
                    + "    else cx.BARRA end as codigobarras,\n"
                    + "    pr.unidade,\n"
                    + "    cx.codaliq codaliq_venda,\n"
                    + "    pr.codaliq codaliq_produto,\n"
                    + "    ic.DESCRICAO trib_desc\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
