package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.verba.receber.ReceberVerbaDAO;
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
 * @author Alan
 */
public class PoligonDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(PoligonDAO.class.getName());

    public int v_tipoDocumento;
    public int v_tipoDocumentoCheque;
    public boolean v_usar_arquivoBalanca;
    public boolean v_usar_arquivoBalancaUnificacao;
    public boolean usarMargemBruta = false;
    
    public boolean usarQtdEmbDoProduto = false;
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
        return "Poligon" + (!"".equals(complemento) ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Id_loja id,\n"
                    + "	RazaoSocial descricao\n"
                    + "FROM \n"
                    + "	PADARIA.dbo.Loja\n"
                    + "order by 1"
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

    @Override
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
                    + "   refativoimob tipo_ativo,\n"
                    + "   refusoconsumo tipo_usoconsumo,\n"
                    + "	prod.desativacompra,\n"
                    + " prod.CODANP codigoanp,\n"
                    + " prod.corredor\n"
                    + "from\n"
                    + "	produtos prod\n"
                    + "left outer join prod_familia fam on\n"
                    + "		fam.codprod = prod.codprod and\n"
                    + "		prod.codprod > 0\n"
                    + "join aliquota_icms al on\n"
                    + "		al.CODALIQ = prod.codaliq_nf\n"
                    + "left join TROCACOMPRA trc on prod.CODPROD = trc.CODPROD\n"
                    + "left join PROD_TRIBFCP fcp on prod.CODPROD = fcp.CODPROD\n"
                    + "left join prod_loja pl on prod.codprod = pl.CODPROD\n"      
                    + "and pl.codloja = " + getLojaOrigem() + "\n"
                    + (apenasProdutoAtivo == true ? " and upper(ltrim(rtrim(prod.ativo))) = 'S'\n" : "")        
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
                    imp.setEstoque(rst.getDouble("estoque") == 0 ? rst.getDouble("estoque_produto") : rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto") == 0 ? rst.getDouble("custo_produto") : rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto") == 0 ? rst.getDouble("custo_produto") : rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda") == 0 ? rst.getDouble("precovenda_produto") : rst.getDouble("precovenda"));
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
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if (imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    }

                    imp.setPautaFiscalId(imp.getImportId());
                    imp.setCodigoAnp(rst.getString("codigoanp") != null ? rst.getString("codigoanp").trim()
                            : "");

                    imp.setPrateleira(rst.getString("corredor"));

                    vResult.add(imp);
                }
            }
            LOG.fine("Produtos de balança: " + qtdBalanca + " normais: " + qtdNormal);
        }
        return vResult;
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
                    "select \n" +
                    "   f.codfornec,\n" +
                    "	f.razao,\n" +
                    "	coalesce(f.fantasia, f.RAZAO) fantasia,\n" +
                    "	f.endereco,\n" +
                    "	f.numero,\n" +
                    "	f.bairro,\n" +
                    "	f.complemento,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cep,\n" +
                    "	f.telefone,\n" +
                    "	f.fax,\n" +
                    "	f.email,\n" +
                    "	f.celular,\n" +
                    "	f.fone1,\n" +
                    "	f.contato,\n" +
                    "	f.ie,\n" +
                    "	f.cnpj_cpf,\n" +
                    "	f.agencia,\n" +
                    "	f.banco,\n" +
                    "	f.conta,\n" +
                    "	f.dtcad,\n" +
                    "	f.valor_compra,\n" +
                    "	f.ativo, \n" +
                    "   f.obs, \n" +
                    "   c.descricao as descricaopag, \n" +
                    "   f.pentrega, \n" +
                    "   f.pvisita,\n" +
                    "   coalesce(\n" +
                    "   	case \n" +
                    "               when codtipofornec = 1 then 0 \n" +
                    "               when codtipofornec = 2 then 1 \n" +
                    "		    when codtipofornec = 3 then 2 \n" +
                    "		    when codtipofornec = 4 then 3 \n" +
                    "		    when codtipofornec = 5 then 5 \n" +
                    "		    when codtipofornec = 6 then 6 \n" +
                    "		    when codtipofornec = 7 then 7 \n" +
                    "		    when codtipofornec = 8 then 8 \n" +
                    "		end,\n" +
                    "		2\n" +
                    "	) as codtipofornec, \n" +
                    "    simples \n" +
                    "from \n" +
                    "    fornecedores f \n" +
                    "    left join condpagto c on\n" +
                    "    	f.codcondpagto = c.codcondpagto \n" +
                    "order by\n" +
                    "	codfornec"
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
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
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
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setJuros(rst.getDouble("VALORJUROS"));
                    
                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
                    "select \n" +
                    "	distinct\n" +
                    "	ltrim(rtrim(replace(icms.codaliq,'\\','\\\\\\'))) id,\n" +
                    "	coalesce(fcp.VALORTRIB, 0) fcp,\n" +
                    "	icms.descricao\n" +
                    "from \n" +
                    "	aliquota_icms icms\n" +
                    "	left join\n" +
                    "		PRODUTOS p on icms.CODALIQ = p.CODALIQ\n" +
                    "	left join PROD_TRIBFCP fcp on p.codprod = fcp.CODPROD\n" +
                    "where\n" +
                    "	icms.descricao is not null\n" +
                    "order by\n" +
                    "	id, fcp"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            formatTributacaoId(rs.getString("id"), rs.getDouble("fcp")), 
                            String.format("%s + FCP %.2f %%", rs.getString("descricao"), rs.getDouble("fcp"))
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
