package vrimplantacao2.dao.interfaces;

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
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.InventarioIMP;
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
 * @author Leandro
 */
public class AvanceDAO extends InterfaceDAO implements MapaTributoProvider {

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public boolean i_balanca = false;
    public int v_contaRotativo;
    public int v_contaCarteira;
    public int v_contaCheque;
    public int v_carteiraCheque;
    public int v_carteiraContaPagar;
    private Date dataInventario;
    public String idLojaContaPagar;

    private static final Logger LOG = Logger.getLogger(AvanceDAO.class.getName());

    @Override
    public String getSistema() {
        return "Avance";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.VR_ATACADO
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct id_loja, fantasia FROM adm_empresas_estab ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_loja"), rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	distinct\n"
                    + "	m.CODIGO cod_merc1,\n"
                    + " m.NOME descmerc1,	\n"
                    + "	d.CODIGO cod_merc2,\n"
                    + "	d.NOME descmerc2,\n"
                    + "	g.CODIGO cod_merc3,\n"
                    + "	g.NOME descmerc3\n"
                    + "FROM\n"
                    + "	cadmer p\n"
                    + "JOIN marca m ON p.MARCA = m.CODIGO	\n"
                    + "JOIN depto d ON p.DEPART = d.CODIGO\n"
                    + "JOIN grupo g ON p.GRUPO = g.CODIGO\n"
                    + "ORDER BY 1, 3, 5"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cod_merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("cod_merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("cod_merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, descricao FROM familia ORDER BY id"
            )) {
                while (rst.next()) {

                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.codigo id,\n"
                    + "	p.cadastro datacadastro,\n"
                    + "	p.embalagem qtdcotacao,\n"
                    + " coalesce(case when ean.codbarra = '' then '0' ELSE ean.codbarra END, '0') codbarra, \n"
                    + " p.codbalanca, \n"
                    + "	CASE WHEN p.codbalanca != 0 THEN p.codbalanca ELSE ean.codbarra END ean,\n"
                    + "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE ean.qtd_embalagem END qtdembalagem,\n"
                    + "	p.unidade,\n"
                    + "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE 0 END ebalanca,\n"
                    + "	p.validade,\n"
                    + "	p.descricao descricaocompleta,\n"
                    + "	p.descecf descricaoreduzida,\n"
                    + " p.MARCA as mercadologico1,\n"
                    + "	p.depart mercadologico2,\n"
                    + " p.grupo mercadologico3,\n"
                    + "	p.id_familia,\n"
                    + "	p.peso_bruto,\n"
                    + "	p.peso_liquido,\n"
                    + "	est.lojaestmin estoqueminimo,\n"
                    + "	est.lojaestmax estoquemaximo,\n"
                    + "	est.lojaest estoque,\n"
                    //+ "	p.dentrouf margem,\n"
                    + "	p.indice margem,\n"
                    + "	p.custo custosemimposto,\n"
                    + "	p.custofinal custocomimposto,\n" + " p.custoant custoanteriorsemimposto,\n"
                    + " p.custofinalant custoanteriorcomimposto,\n"
                    + "	p.atualvenda precovenda,\n"
                    + " est.venda_atual precovendaloja,\n"
                    + "	p.inativo situacaocadastro,\n"
                    + "	p.nbm ncm,\n"
                    + "	p.cest,\n"
                    + "	p.cst_pis_ent piscofins_entrada,\n"
                    + "	p.cst_pis_sai piscofins_saida,\n"
                    + "	p.cod_nat_receita piscofins_nat_receita,\n"
                    + "	p.cst icms_cst,\n"
                    + "	p.aliquota,\n"
                    + " p.ecotacao sugestaocotacao\n"
                    + "FROM\n"
                    + "	cadmer p\n"
                    + "	LEFT JOIN codbarra ean ON p.codigo = ean.codigo\n"
                    + "	LEFT JOIN cadmer_estoque est ON p.codigo = est.codigo AND est.id_loja = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));

                    if ((rst.getString("codbalanca") != null)
                            && (!rst.getString("codbalanca").trim().isEmpty())
                            && (!"0".equals(rst.getString("codbalanca").trim()))
                            && (Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim())) <= 999999)
                            && (rst.getInt("ebalanca") == 1)
                            && (Long.parseLong(Utils.formataNumero(rst.getString("id").trim()))
                            == Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim().substring(0, rst.getString("codbarra").trim().length() - 1))))
                            || ("0".equals(rst.getString("codbarra").trim()))) {

                        imp.setEan(rst.getString("codbalanca"));

                        if (i_balanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(Utils.formataNumero(imp.getEan().trim()));
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {

                            imp.seteBalanca((rst.getInt("ebalanca") == 1));
                            imp.setValidade(rst.getInt("validade"));

                        }
                    } else {
                        imp.setEan(rst.getString("codbarra"));
                        imp.seteBalanca(false);
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoAnteriorSemImposto(rst.getDouble("custoanteriorsemimposto"));
                    imp.setCustoAnteriorComImposto(rst.getDouble("custoanteriorcomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    if (rst.getDouble("precovendaloja") != 0) {
                        imp.setPrecovenda(rst.getDouble("precovendaloja"));
                    }
                    imp.setSituacaoCadastro((rst.getInt("situacaocadastro") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_entrada")));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_saida")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_nat_receita")));
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsCreditoId(rst.getString("aliquota"));
                    imp.setPautaFiscalId(imp.getImportId());
                    imp.setSugestaoCotacao(rst.getInt("sugestaocotacao") == 1);
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.codigo id_produto,\n"
                    + "	p.ALIQUOTA id_aliquota,\n"
                    + "	p.CST,\n"
                    + "	p.mva,\n"
                    + "	ncm.ncm\n"
                    + "FROM\n"
                    + "	cadmer p\n"
                    + "LEFT JOIN ncm ON ncm.id = p.id_ncm\n"
                    + "WHERE\n"
                    + "	p.mva > 0 and\n"
                    + "	ncm.ncm IS NOT null"
            )) {
                while (rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setId(rs.getString("id_produto"));
                    imp.setIva(rs.getDouble("mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rs.getString("ncm"));
                    imp.setAliquotaCreditoId(rs.getString("id_aliquota"));
                    imp.setAliquotaCreditoForaEstadoId(rs.getString("id_aliquota"));
                    imp.setAliquotaDebitoId(rs.getString("id_aliquota"));
                    imp.setAliquotaDebitoForaEstadoId(rs.getString("id_aliquota"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ean.codigo, \n"
                    + "	coalesce(ean.codbarra, ean.codigo) ean,\n"
                    + "	if(ean.mult_qtde = 0.00, 1, ean.mult_qtde) quantidade,\n"
                    + "	p.unidade \n"
                    + "from \n"
                    + "	codbarra ean\n"
                    + "inner join cadmer p on p.Codigo = ean.codigo\n"
                    + "order by \n"
                    + "	ean.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    /*if (imp.getEan().length() < 7) {
                        imp.setEan("999999" + rst.getString("ean"));
                    }*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opcao == OpcaoProduto.ATACADO) {            
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select \n"
                        + "	ean.codigo, \n"
                        + "	coalesce(ean.codbarra, ean.codigo) ean,\n"
                        + "	p.atualvenda,\n"
                        + "	ROUND((p.atualvenda + ean.dif_preco), 2) precoatacado,\n"
                        + "	ean.complemento,\n"
                        + "	if(ean.mult_qtde = 0.00, 1, ean.mult_qtde) quantidade,\n"
                        + "	p.unidade \n"
                        + "from \n"
                        + "	codbarra ean\n"
                        + "inner join cadmer p on p.Codigo = ean.codigo\n"
                        + "WHERE\n"
                        + "	ean.dif_preco != 0\n"
                        + "order by \n"
                        + "	ean.codigo"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportId(rs.getString("codigo"));
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setPrecovenda(rs.getDouble("atualvenda"));
                        imp.setAtacadoPreco(rs.getDouble("precoatacado"));
                        imp.setEan(rs.getString("ean"));
                        imp.setQtdEmbalagem(rs.getInt("quantidade"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        
        if (opcao == OpcaoProduto.VR_ATACADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "  id_cab,\n"
                        + "  id_cadmer AS idproduto,\n"
                        + "  preco_cadmer AS precovenda,\n"
                        + "  preco_negociado,\n"
                        + "  porcentagem\n"
                        + "FROM precos_item  \n"
                        + "WHERE id_cab = 2\n"
                        + "AND preco_negociado > 0"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setPrecovenda(rst.getDouble("preco_negociado"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT aliquota id, CONCAT(descricao,'  |cst:' ,cst) descricao FROM aliquota ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "f.Codigo,\n"
                    + "f.RAZAO,\n"
                    + "f.FANTASIA,\n"
                    + "concat(coalesce(f.logr, ''), ' ', f.ENDERECO) endereco,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.ESTADO,\n"
                    + "f.CEP,\n"
                    + "f.TELEFONE,\n"
                    + "f.FAX,\n"
                    + "f.CGC,\n"
                    + "f.inscmun,\n"
                    + "f.inscr,\n"
                    + "f.OBS,\n"
                    + "f.DATA,\n"
                    + "f.EMAIL,\n"
                    + "f.HTTP,\n"
                    + "f.VENDEDOR,\n"
                    + "f.situacao_empresa,\n"
                    + "f.anotacoes,\n"
                    + "f.senha,\n"
                    + "f.cod_autorizado,\n"
                    + "f.banco1,\n"
                    + "f.banco2,\n"
                    + "f.agencia1,\n"
                    + "f.agencia2,\n"
                    + "f.conta1,\n"
                    + "f.conta2,\n"
                    + "f.prest_serv,\n"
                    + "f.nomeconta1,\n"
                    + "f.nomeconta2,\n"
                    + "f.cod_mun,\n"
                    + "f.suframa,\n"
                    + "f.fonevend,\n"
                    + "f.supervisor,\n"
                    + "f.fonesup,\n"
                    + "f.emailsup,\n"
                    + "f.logr,\n"
                    + "f.numero,\n"
                    + "f.regime_tributario,\n"
                    + "f.id_pais,\n"
                    + "f.inativo,\n"
                    + "f.status,\n"
                    + "f.prev_entrega,\n"
                    + "f.ind_ie,\n"
                    + "f.natureza_juridica,\n"
                    + "f.id_cidade,\n"
                    + "f.reg_trib,\n"
                    + "UPPER(c.nome) NOMECIDADE,\n"
                    + "c.codibge,\n"
                    + "UPPER(u.uf) UFSIGLA,\n"
                    + "UPPER(u.descricao) NOMEESTADO,\n"
                    + "f.fpagto_padr condpagamento,\n"
                    + "f.produtor_rural\n"
                    + "FROM fornece f\n"
                    + "LEFT JOIN cidade c ON c.id = f.id_cidade\n"
                    + "LEFT JOIN uf u ON u.id = c.id_uf"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setCnpj_cpf(rst.getString("CGC"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setIe_rg(rst.getString("inscr"));
                    imp.setObservacao(rst.getString("OBS") + " " + rst.getString("anotacoes"));
                    imp.setAtivo(rst.getInt("inativo") == 0 ? true : false);
                    imp.setDatacadastro(rst.getDate("DATA"));

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("FAX").trim()),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        String email = "";
                        if (rst.getString("EMAIL").length() > 50) {
                            email = rst.getString("EMAIL").substring(0, 50);
                        }
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                email.toLowerCase()
                        );
                    }
                    if ((rst.getString("VENDEDOR") != null)
                            && (!rst.getString("VENDEDOR").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                (rst.getString("VENDEDOR").length() > 30 ? rst.getString("VENDEDOR").substring(0, 30) : rst.getString("VENDEDOR").trim()),
                                (rst.getString("fonevend") == null ? "" : rst.getString("fonevend").trim()),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("supervisor") != null)
                            && (!rst.getString("supervisor").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                (rst.getString("supervisor").length() > 30 ? rst.getString("supervisor").substring(0, 30) : rst.getString("supervisor").trim()),
                                (rst.getString("fonesup") == null ? "" : rst.getString("fonesup").trim()),
                                null,
                                TipoContato.COMERCIAL,
                                (rst.getString("emailsup") == null ? "" : rst.getString("emailsup").trim())
                        );
                    }
                    imp.setCondicaoPagamento(Utils.stringToInt(Utils.formataNumero(rst.getString("condpagamento"))));
                    imp.setPrazoEntrega(rst.getInt("prev_entrega"));

                    if (rst.getInt("produtor_rural") == 1) {
                        imp.setProdutorRural();
                    }

                    adicionaContatosFornecedor(imp);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void adicionaContatosFornecedor(FornecedorIMP imp) throws SQLException {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	id,\n"
                    + "	id_cli,\n"
                    + "	contato,\n"
                    + "	telefone,\n"
                    + "	email\n"
                    + "FROM\n"
                    + "	contato\n"
                    + "where\n"
                    + "	id_cli = " + imp.getImportId()
            )) {
                while (rs.next()) {
                    imp.addContato(rs.getString("id"),
                            rs.getString("contato"),
                            rs.getString("telefone"),
                            null,
                            TipoContato.COMERCIAL,
                            rs.getString("email") == null ? "" : rs.getString("email"));
                }
            }
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "pf.CODPROD,\n"
                    + "pf.CODFOR,\n"
                    + "pf.datahora,\n"
                    + "pf.valor,\n"
                    + "merc.referencia\n"
                    + "FROM forprod pf\n"
                    + "LEFT JOIN (SELECT id_cadmer, referencia, codfor\n"
                    + "       FROM cadmer_referencia) merc ON merc.id_cadmer = pf.CODPROD\n"
                    + "                                    AND merc.codfor = pf.CODFOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("CODPROD"));
                    imp.setIdFornecedor(rst.getString("CODFOR"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    imp.setCustoTabela(rst.getDouble("valor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "c.codigo,\n"
                    + "c.nome,\n"
                    + "c.nascimento,\n"
                    + "c.estcivil,\n"
                    + "c.sexo,\n"
                    + "c.rg,\n"
                    + "c.orgemissor,\n"
                    + "c.cpf,\n"
                    + "c.natura,\n"
                    + "c.nacional,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.conjuge,\n"
                    + "c.conjnasc,\n"
                    + "c.conjcpf,\n"
                    + "c.conjrg,\n"
                    + "c.conjrgorg,\n"
                    + "c.casamento,\n"
                    + "c.numero,\n"
                    + "c.compl,\n"
                    + "c.logr,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.uf,\n"
                    + "c.cep,\n"
                    + "c.telefone,\n"
                    + "c.empresa,\n"
                    + "c.funcao,\n"
                    + "c.empnum,\n"
                    + "c.empend,\n"
                    + "c.empendnum,\n"
                    + "c.empcompl,\n"
                    + "c.empbairro,\n"
                    + "c.empcid,\n"
                    + "c.empuf,\n"
                    + "c.empfone,\n"
                    + "c.admissao,\n"
                    + "c.refpess,\n"
                    + "c.refpessfon,\n"
                    + "c.empramal,\n"
                    + "c.banco1,\n"
                    + "c.agencia1,\n"
                    + "c.conta1,\n"
                    + "c.banco2,\n"
                    + "c.agencia2,\n"
                    + "c.conta2,\n"
                    + "c.email,\n"
                    + "c.renda,\n"
                    + "c.obs,\n"
                    + "c.limite,\n"
                    + "c.limite_chq limitecheque,\n"
                    + "c.situacao,\n"
                    + "c.bloqueado,\n"
                    + "c.motivobloq,\n"
                    + "c.cgc,\n"
                    + "c.inscr,\n"
                    + "c.fax,\n"
                    + "c.tipo,\n"
                    + "c.fantasia,\n"
                    + "c.anotacoes,\n"
                    + "c.cod_mun,\n"
                    + "c.id_pais,\n"
                    + "c.natureza_juridica,\n"
                    + "c.endereco,\n"
                    + "case when cadastro = '0000-00-00' then NOW()\n"
                    + "	else cadastro end cadastro,\n"
                    + "c.bloqueado_crd\n"
                    + "FROM clientes c"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco((rst.getString("logr") + " " + rst.getString("endereco")).trim());
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("compl"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataNascimento(rst.getDate("nascimento"));

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cpf").trim());
                    } else if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cgc").trim());
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg").trim().replace("'", ""));
                    } else if ((rst.getString("inscr") != null)
                            && (!rst.getString("inscr").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscr").trim().replace("'", ""));
                    } else {
                        imp.setInscricaoMunicipal("ISENTO");
                    }

                    if (rst.getString("orgemissor") != null && !"".equals(rst.getString("orgemissor").trim())) {
                        imp.setOrgaoemissor(rst.getString("orgemissor").replace("'", ""));
                    }
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("funcao"));
                    imp.setEmpresaEndereco(rst.getString("empend"));
                    imp.setEmpresaNumero(rst.getString("empendnum"));
                    imp.setEmpresaComplemento(rst.getString("empcompl"));
                    imp.setEmpresaBairro(rst.getString("empbairro"));
                    imp.setEmpresaMunicipio(rst.getString("empcid"));
                    imp.setEmpresaUf(rst.getString("empuf"));
                    imp.setEmpresaTelefone(rst.getString("empfone"));
                    //imp.setDataAdmissao(rst.getDate("admissao"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("limite") + rst.getDouble("limitecheque"));
                    imp.setObservacao("Fantasia: " + rst.getString("fantasia") + " - "
                            + Utils.acertarTexto(rst.getString("obs")) + " " + Utils.acertarTexto(rst.getString("anotacoes")));

                    /*if (rst.getDouble("limite") > 0) {
                        imp.setPermiteCreditoRotativo(true);
                    }*/
                                        
                    imp.setBloqueado(rst.getInt("bloqueado_crd") == 1);
                    imp.setPermiteCheque((rst.getInt("bloqueado") == 0));
                    imp.setPermiteCreditoRotativo(rst.getInt("bloqueado_crd") == 0);                    
                    
                    /*if (rst.getDouble("limitecheque") > 0) {
                        imp.setPermiteCheque(true);
                    }*/

                    //imp.setBloqueado(rst.getInt("bloqueado") == 0 ? false : true);

                    if (rst.getString("sexo") != null && !"".equals(rst.getString("sexo"))) {
                        imp.setSexo("F".equals(rst.getString("sexo").trim()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    }

                    if (rst.getString("estcivil") != null && !"".equals(rst.getString("estcivil"))) {
                        switch (rst.getString("estcivil").toUpperCase()) {
                            case "CASADO":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "SEPARADO":
                                imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            case "SOLTEIRO":
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break;
                            case "VIÚVO":
                                imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;
                            case "DIVORCIADO":
                                imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            case "AMASIADO":
                                imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break;
                        }
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax").trim()),
                                null,
                                null
                        );
                    }
                    if (rst.getString("cadastro") != null && !"0000-00-00".equals(rst.getString("cadastro").trim())) {
                        imp.setDataCadastro(rst.getDate("cadastro"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "id,\n"
                    + "emissao,\n"
                    + "vencimento,\n"
                    + "documento,\n"
                    + "codcli,\n"
                    + "cupom,\n"
                    + "valor_original,\n"
                    + "valor,\n"
                    + "valorpago,\n"
                    + "(valor - valorpago) valorconta,\n"
                    + "historico,\n"
                    + "caixa\n"
                    + "FROM receb\n"
                    + "WHERE valorpago < valor\n"
                    + "AND pago = 0\n"
                    + "AND codcli IS NOT NULL\n"
                    + "AND id_banco = " + v_contaCarteira
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcli"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorconta"));
                    imp.setNumeroCupom(rst.getString("documento").substring(0, rst.getString("documento").indexOf("/")));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setObservacao(rst.getString("historico"));
                    if (rst.getDouble("valor_original") > 0) {
                        imp.setObservacao(imp.getObservacao() + " VALOR ORIGINAL DA CONTA " + rst.getDouble("valor_original"));
                    }
                    if (rst.getDouble("valorpago") > 0) {
                        imp.setObservacao(imp.getObservacao() + " VALOR PAGO " + rst.getDouble("valorpago"));
                    }
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	r.id,\n"
                    + "	r.emissao,\n"
                    + "	r.documento,\n"
                    + "	r.ncheque cheque,\n"
                    + "	r.tipodoc,\n"
                    + "	r.codcli cliente,\n"
                    + "	c.nome,\n"
                    + "	c.telefone,\n"
                    + "	r.vencimento,\n"
                    + "	r.valor_original valor,\n"
                    + "	r.historico,\n"
                    + "	r.numbanco banco,\n"
                    + "	r.agencia,\n"
                    + "	r.cpfcgc cpf,\n"
                    + "	r.caixa,\n"
                    + "   r.datahora_alteracao\n"
                    + "FROM\n"
                    + "	receb r\n"
                    + "LEFT JOIN clientes c ON r.codcli = c.codigo\n"
                    + "WHERE\n"
                    + "	r.id_banco = " + v_carteiraCheque + " and\n"
                    + "	r.pago = 0 and\n"
                    + "	r.cancelado = 0 and\n"
                    + "   r.valor_original > 0\n"
                    + "ORDER BY\n"
                    + "	r.emissao"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("historico"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("datahora_alteracao"));
                    imp.setAlinea(0);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<InventarioIMP> getInventario() throws Exception {
        List<InventarioIMP> result = new ArrayList<>();
        String nomeTable = "";

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT nome_inventario as nomeTabela "
                    + "FROM inventarios_cab "
                    + "WHERE data_inventario = '" + FORMAT.format(dataInventario) + "' \n"
                    + "AND nome_inventario NOT LIKE '%_1'\n"
                    + "AND id_loja = " + getLojaOrigem()
            )) {
                if (rst.next()) {
                    nomeTable = rst.getString("nomeTabela");
                    System.out.println("Tabela: " + rst.getString("nomeTabela"));
                }
            }

            if ((nomeTable != null) && (!nomeTable.trim().isEmpty())) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "i.Codigo, "
                        + "i.DESCRICAO, "
                        + "i.ALIQUOTA,\n"
                        + "i.CUSTO, "
                        + "i.VENDA, "
                        + "i.LOJAEST, "
                        + "i.CUSMEDIO,\n"
                        + "i.dt_inv, "
                        + "i.aliq_pis_sai, "
                        + "i.aliq_cofins_ent\n"
                        + "FROM " + nomeTable + " i"
                )) {
                    while (rst.next()) {
                        InventarioIMP imp = new InventarioIMP();
                        imp.setId(getLojaOrigem() + "-" + rst.getString("dt_inv") + "-" + rst.getString("Codigo"));
                        imp.setData(dataInventario);
                        imp.setIdProduto(rst.getString("Codigo"));
                        imp.setDescricao(rst.getString("DESCRICAO"));
                        imp.setCustoComImposto(rst.getDouble("CUSTO"));
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        imp.setCustoMedioComImposto(rst.getDouble("CUSMEDIO"));
                        imp.setCustoMedioSemImposto(imp.getCustoMedioComImposto());
                        imp.setPrecoVenda(rst.getDouble("VENDA"));
                        imp.setQuantidade(rst.getDouble("LOJAEST"));
                        imp.setPis(rst.getDouble("aliq_pis_sai"));
                        imp.setCofins(rst.getDouble("aliq_cofins_ent"));
                        imp.setIdAliquotaDebito(rst.getString("ALIQUOTA"));
                        imp.setIdAliquotaCredito(rst.getString("ALIQUOTA"));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.id,	\n"
                    + "	p.id_produto,\n"
                    + "	pc.inicio datainicio,\n"
                    + "	pc.fim datafim,\n"
                    + "	p.promocao precopromocao,\n"
                    + "	p.venda precovenda,\n"
                    + "	p.status\n"
                    + "FROM\n"
                    + "	promocao_itens as p\n"
                    + "JOIN promocao_cab as pc ON p.id_promocao_cab = pc.id\n"
                    + "WHERE\n"
                    + "	cast(pc.fim AS DATE) >= NOW() and\n"
                    + "	pc.id_loja = " + getLojaOrigem() + " and\n"
                    + "	pc.status = 0\n"
                    + "ORDER BY\n"
                    + "	pc.fim")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoOferta(rs.getDouble("precopromocao"));
                    imp.setPrecoNormal(rs.getDouble("precovenda"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id, "
                    + "nome "
                    + "from conta "
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"),
                            rst.getString("id") + " - "
                            + rst.getString("nome")));
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getTipoCarteira() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id, "
                    + "nome "
                    + "from banco "
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("id") + " - " + rst.getString("nome")));
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getTipoCarteiraContaPagar() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	id,\n"
                    + "	nome \n"
                    + "FROM \n"
                    + "	banco \n"
                    + "WHERE \n"
                    + "	id IN \n"
                    + "		(SELECT \n"
                    + "			DISTINCT id_banco\n"
                    + "		FROM\n"
                    + "			pagto p\n"
                    + "		WHERE\n"
                    + "			p.pagamento IS null\n"
                    + "		ORDER BY\n"
                    + "			id_banco)"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("id") + " - " + rst.getString("nome")));
                }
            }
        }
        return result;
    }

    public void setDataInventario(Date dataInventario) {
        this.dataInventario = dataInventario;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.id,\n"
                    + "	p.id_for,\n"
                    + "	p.documento,\n"
                    + "	p.emissao,\n"
                    + "	p.vencimento,\n"
                    + "	p.valor,\n"
                    + "	p.valor_original,\n"
                    + "	p.historico\n"
                    + "FROM	\n"
                    + "	pagto p\n"
                    + "WHERE\n"
                    + "	pagamento IS NULL and\n"
                    + "	p.id_banco = " + v_carteiraContaPagar + "\n"
                    + //"   p.emissao between '2016-01-01' and '2018-12-31'\n" +        
                    "ORDER BY\n"
                    + "	vencimento"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_for"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("historico"));

                    result.add(imp);
                }
            }
        }
        return result;
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

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("caixa")));
                        next.setData(rst.getDate("datahora"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //String horaInicio = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
                        //String horaTermino = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(TIMESTAMP.parse(rst.getString("datahora")));
                        next.setHoraTermino(TIMESTAMP.parse(rst.getString("datahora")));
                        //next.setCancelado(rst.getInt("cancelado") == 1);
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco = "";
                        endereco = rst.getString("endereco") + ", " + rst.getString("bairro") + ", "
                                + rst.getString("numero") + " - " + rst.getString("cidade") + ", "
                                + rst.getString("uf") + " - " + rst.getString("cep");
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
                    = "SELECT\n"
                    + "	concat(v.NOTA, v.CAIXA, v.DATA) id, \n"
                    + "	v.NOTA cupom,\n"
                    + "	v.CAIXA,\n"
                    + "	v.`DATA`,\n"
                    + "	max(v.datahora) datahora,\n"
                    + "	max(v.HORA) hora,\n"
                    + "	round(sum(coalesce(v.valor, 0) * quant), 2) valor,\n"
                    + "	v.ecf,\n"
                    + "	max(v.CANCELADO) cancelado,\n"
                    + "	v.CLIENTE id_cliente,\n"
                    + "	c.nome,\n"
                    + "	c.cpf,\n"
                    + "	c.endereco,\n"
                    + "	c.bairro,\n"
                    + "	c.numero,\n"
                    + "	c.cidade,\n"
                    + "	c.uf,\n"
                    + "	c.cep,\n"
                    + "	c.telefone\n"
                    + "FROM\n"
                    + "	vendas v\n"
                    + "LEFT JOIN clientes c ON v.CLIENTE = c.codigo\n"
                    + "WHERE\n"
                    + "	v.`DATA` BETWEEN '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "' and\n"
                    + "	v.loja = " + idLojaCliente + " and\n"
                    + "	v.e_transferencia = 0 and\n"
                    + "	v.nao_bx_estoque = 0 and\n"
                    + "	v.caixa <> '999' and\n"
                    + "   v.valor > 0\n"
                    + "GROUP BY\n"
                    + "	v.NOTA,\n"
                    + "	v.CAIXA,\n"
                    + "	v.`DATA`,\n"
                    + "	v.ecf,\n"
                    + "	v.CLIENTE,\n"
                    + "	c.nome,\n"
                    + "	c.cpf,\n"
                    + "	c.endereco,\n"
                    + "	c.bairro,\n"
                    + "	c.numero,\n"
                    + "	c.cidade,\n"
                    + "	c.uf,\n"
                    + "	c.cep,\n"
                    + "	c.telefone\n"
                    + "ORDER  BY\n"
                    + "	v.CAIXA, v.data, v.datahora";
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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setCancelado(rst.getInt("cancelado") == 1 || rst.getInt("cancelado") == 2);
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));

                        if (rst.getString("aliquota") != null && !"".equals(rst.getString("aliquota"))) {
                            switch (rst.getString("aliquota").trim()) {
                                case "F":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(60);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "I":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(40);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "N":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(41);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "T01":
                                    next.setIcmsAliq(7);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "T02":
                                    next.setIcmsAliq(12);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "T03":
                                    next.setIcmsAliq(18);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "T04":
                                    next.setIcmsAliq(25);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "T05":
                                    next.setIcmsAliq(30);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                default:
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(40);
                                    next.setIcmsReduzido(0);
                                    break;
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	v.id,\n"
                    + "   CONCAT(v.NOTA, v.CAIXA, v.DATA) id_venda,\n"
                    + "	v.CAIXA,\n"
                    + "	v.NOTA cupom,\n"
                    + "	v.`DATA`,\n"
                    + "	v.CODBARRA ean,\n"
                    + "	v.CODIGO id_produto,\n"
                    + "   c.unidade,\n"
                    + "	v.DESCRICAO,\n"
                    + "	v.itemno sequenceia,\n"
                    + "	v.QUANT quantidade,\n"
                    + "	v.valor,\n"
                    + "	v.ALIQUOTA,\n"
                    + "	v.cancelado\n"
                    + "FROM\n"
                    + "	vendas v\n"
                    + "JOIN cadmer c ON v.CODIGO = c.Codigo\n"
                    + "WHERE\n"
                    + "	v.`DATA` BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND "
                    + "'" + VendaIterator.FORMAT.format(dataTermino) + "' AND\n"
                    + "	v.loja = " + idLojaCliente + " and\n"
                    + "	v.e_transferencia = 0 and\n"
                    + "	v.nao_bx_estoque = 0 and\n"
                    + " 	v.caixa <> '999' and\n"
                    + "   v.valor > 0\n"
                    + "ORDER BY\n"
                    + "	v.CAIXA, \n"
                    + "	v.data, \n"
                    + "	v.HORA";
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
