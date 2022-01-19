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
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class KcmsDAO extends InterfaceDAO implements MapaTributoProvider {

    public String id_loja;
    public boolean usarMargemBruta;
    public boolean vBalanca;
    public int vPlanoContas;
    public int vPlanoContaCP;

    private static final Logger LOG = Logger.getLogger(KcmsDAO.class.getName());

    @Override
    public String getSistema() {
        return "KCMS" + id_loja;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + " concat(p.CODALIQ ,'.', p.PERCREDUC,'.',trib.SITTRIB) id_aliquota,\n"
                    + " trib.SITTRIB cst,\n"
                    + " aliq.DESCRCAD DESCRICAO,\n"
                    + " aliq.DESCRECF,\n"
                    + " aliq.PERCENTUAL percentual,\n"
                    + " p.PERCREDUC reducao\n"
                    + "FROM CDPRODUTOS p\n"
                    + "JOIN CDALIQUOTA aliq ON aliq.CODALIQ = p.CODALIQ\n"
                    + "JOIN CDTRIBUTACAO trib ON trib.CODTRIB = p.CODTRIB\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id_aliquota"),
                            rs.getString("DESCRICAO"),
                            rs.getInt("cst"),
                            rs.getDouble("percentual"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codloja,\n"
                    + "	razaosocial,\n"
                    + "	fantasia \n"
                    + "from empresa")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codloja"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	GEN.CODGENERO as codmerc1,\n"
                    + "	GEN.DESCRICAO as descmerc1,\n"
                    + "	GRU.CODGRUPO as codmerc2,\n"
                    + "	GRU.GRUPO as descmerc2,\n"
                    + "	COALESCE(CODGRU.CODSUBGRUPO, 1) as codmerc3, \n"
                    + "	coalesce(CODGRU.DESCRICAO, gru.grupo) as descmerc3 \n"
                    + "FROM \n"
                    + "	CDGENEROS AS GEN\n"
                    + "INNER JOIN \n"
                    + "	CDGRUPOS AS GRU ON GRU.CODGENERO = GEN.CODGENERO\n"
                    + "LEFT JOIN \n"
                    + "	CDSUBGRUPOS AS CODGRU ON CODGRU.CODGRUPO  = GRU.CODGRUPO AND\n"
                    + "	CODGRU.CODGENERO = GRU.CODGENERO\n"
                    + "ORDER BY \n"
                    + "	GEN.CODGENERO,\n"
                    + "	GRU.CODGRUPO,\n"
                    + "	CODGRU.CODSUBGRUPO")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("codmerc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("codmerc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	CODFAMILIA,\n"
                    + "	DESCRICAO,\n"
                    + "	INATIVO\n"
                    + "FROM \n"
                    + "	CDFAMILIAS")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codfamilia"));
                    imp.setDescricao(rs.getString("descricao"));

                    imp.setSituacaoCadastro("N".equals(rs.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT  \n"
                    + "    P.CODPROD, \n"
                    + "    P.CODBARRA, \n"
                    + "    P.DESCRICAO, \n"
                    + "    P.DTCADAST, \n"
                    + "    P.DESCRNF, \n"
                    + "    P.DESCRBAL, \n"
                    + "    P.VALBALANCA, \n"
                    + "    P.PESAVEL, \n"
                    + "    P.UNIDADE, \n"
                    + "    P.UNDCOMPRA, \n"
                    + "    P.QTDEMBAL, \n"
                    + "    P.QTDEMBALCPR, \n"
                    + "    P.CODGENERO, \n"
                    + "    P.CODGRUPO, \n"
                    + "    1 codsubgrupo, \n"
                    + "    P.CODFAMILIA, \n"
                    + "    P.ESTOATU, \n"
                    + "    P.ESTOMIN, \n"
                    + "    P.PRECOENTR, \n"
                    + "    P.PRECOCUSTO, \n"
                    + "    P.PRECOVEND, \n"
                    + "    P.CODTRIB, \n"
                    + "    trib.SITTRIB as csticms, \n"
                    + "    P.CODALIQ, \n"
                    + "    P.CODALIQNF, \n"
                    + "    P.PERCICMSCR, \n"
                    + "    P.PERCREDUC, \n"
                    + "    P.MARGBRUT, \n"
                    + "    P.MARGPARAM, \n"
                    + "    P.CODSETOR, \n"
                    + "    P.PROMOCAO, \n"
                    + "    P.PEGAR_PESO, \n"
                    + "    P.INATIVO, \n"
                    + "    P.ESTOIDEAL, \n"
                    + "    P.CODTIPOPRODUTO, \n"
                    + "    P.IDCDTIPOCOFINS, \n"
                    + "    fc.cod_cst_cofins, \n"
                    + "    P.IDCDTIPOPIS, \n"
                    + "    fp.cod_cst_pis, \n"
                    + "    P.MARGBRUTWEB, \n"
                    + "    P.CODLOJA, \n"
                    + "    P.CODCFOP, \n"
                    + "    P.CODSIMILAR, \n"
                    + "    P.CODGRUPOFISCAL, \n"
                    + "    P.PERCALIQSUBTRIB, \n"
                    + "    P.TIPOSUBTRIB, \n"
                    + "    P.CODALIQECF, \n"
                    + "    P.IDNCM, \n"
                    + "    P.CODTRIBSN, \n"
                    + "    P.CODCSTPIS, \n"
                    + "    P.CODCSTCOFINS, \n"
                    + "    P.COD_NAT_BC_CRED_COFINS, \n"
                    + "    P.COD_NAT_BC_CRED_PIS, \n"
                    + "    P.COD_TIPO_CREDITO_COFINS, \n"
                    + "    P.COD_TIPO_CREDITO_PIS, \n"
                    + "    P.CODCFOP_ENTRADA, \n"
                    + "    P.NAT_RECEITA_COFINS, \n"
                    + "    P.NAT_RECEITA_PIS, \n"
                    + "	 nat.cod_fis_natureza_receita_pis_cofins cod_natureza_receita,\n"
                    + "    P.CODCSTPIS_ENTRADA, \n"
                    + "    P.CODCSTCOFINS_ENTRADA, \n"
                    + "    P.PERCCOFINS_ENTRADA, \n"
                    + "    NCM.CODNCM,\n"
                    + "  concat(p.CODALIQ ,'.', p.PERCREDUC,'.',trib.SITTRIB) id_aliquota \n"
                    + "FROM  \n"
                    + "    CDPRODUTOS AS P \n"
                    + "LEFT JOIN  \n"
                    + "    CDNCM AS NCM ON NCM.IDNCM = P.IDNCM \n"
                    + "LEFT JOIN \n"
                    + "    fis_cst_cofins fc ON fc.id_fis_cst_cofins = p.CODCSTCOFINS \n"
                    + "LEFT JOIN \n"
                    + "    fis_cst_pis fp ON fp.id_fis_cst_pis = p.CODCSTPIS \n"
                    + "LEFT JOIN \n"
                    + "    CDTRIBUTACAO trib ON trib.CODTRIB = p.CODTRIB\n"
                    + " LEFT JOIN  CDALIQUOTA aliq ON aliq.CODALIQ = p.CODALIQ \n"
                    + "LEFT OUTER JOIN\n"
                    + "	fis_natureza_receita_pis_cofins AS NAT ON P.NAT_RECEITA_COFINS = NAT.id_fis_natureza_receita_pis_cofins \n"
                    + "ORDER BY  \n"
                    + "    PESAVEL DESC, \n"
                    + "    DESCRICAO")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codprod"));
                    imp.setEan(rs.getString("codbarra"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricao"));
                    imp.setDataCadastro(rs.getDate("dtcadast"));
                    imp.setDescricaoGondola(rs.getString("descrnf"));
                    imp.setValidade(rs.getInt("valbalanca"));
                    imp.seteBalanca("S".equals(rs.getString("pesavel")) ? true : false);
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembal"));
                    imp.setCodMercadologico1(rs.getString("codgenero"));
                    imp.setCodMercadologico2(rs.getString("codgrupo"));
                    imp.setCodMercadologico3(rs.getString("codsubgrupo"));
                    imp.setIdFamiliaProduto(rs.getString("codfamilia"));
                    imp.setEstoque(rs.getDouble("estoatu"));
                    imp.setEstoqueMinimo(rs.getDouble("estomin"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(rs.getDouble("precoentr"));
                    if (usarMargemBruta) {
                        imp.setMargem(rs.getDouble("margbrut"));
                    } else {
                        imp.setMargem(rs.getDouble("margparam"));
                    }
                    imp.setPrecovenda(rs.getDouble("precovend"));
                    imp.setIcmsCstSaida(rs.getInt("csticms"));
                    imp.setIcmsAliqSaida(rs.getDouble("percicmscr"));
                    imp.setIcmsAliqEntrada(rs.getDouble("percicmscr"));
                    imp.setIcmsReducaoSaida(rs.getDouble("percreduc"));
                    imp.setPiscofinsCstDebito(rs.getString("cod_cst_cofins"));
                    imp.setPiscofinsCstCredito(rs.getString("cod_cst_cofins"));
                    imp.setNcm(rs.getString("codncm"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("cod_natureza_receita"));

                    imp.setIcmsDebitoId(rs.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_aliquota"));
                    imp.setIcmsConsumidorId(rs.getString("id_aliquota"));

                    if ((rs.getString("codbarra") != null)
                            && ("S".equals(rs.getString("pesavel")))
                            && (rs.getString("codbarra").length() <= 7)) {
                        if (vBalanca) {
                            imp.setEan(rs.getString("codbarra").substring(2, rs.getString("codbarra").length()));
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getEan().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("valbalanca"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(true);
                            imp.setValidade(rs.getInt("valbalanca"));
                        }
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + " 	pf.codforn,\n"
                    + "	pf.codprodforn,\n"
                    + "	pr.CODPROD\n"
                    + "FROM \n"
                    + "	PRODFORN pf\n"
                    + "join\n"
                    + "	CDPRODUTOs pr on pr.CODBARRA = pf.codbarra\n"
                    + "WHERE\n"
                    + "	pf.codforn > 0\n"
                    + "order by\n"
                    + "	pr.CODPROD")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("codforn"));
                    imp.setIdProduto(rs.getString("codprod"));
                    imp.setCodigoExterno(rs.getString("codprodforn"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.CODPROD,\n"
                    + "	pro.CODBARRA,\n"
                    + "	pro.DESCRICAO,\n"
                    + "	pro.DATADE,\n"
                    + "	pro.DATAATE,\n"
                    + "	pro.PRECOPROMO,\n"
                    + "	pro.PRECOVEND\n"
                    + "FROM \n"
                    + "	CDPROMOCAOITEM pro\n"
                    + "join\n"
                    + "	CDPRODUTOS p on p.codbarra = pro.CODBARRA\n"
                    + "where \n"
                    + "	pro.DATAATE >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "'")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("codprod"));
                    imp.setDataInicio(rs.getDate("datade"));
                    imp.setDataFim(rs.getDate("dataate"));
                    imp.setPrecoOferta(rs.getDouble("precopromo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	f.codforn,\n"
                    + "	f.dtcadast,\n"
                    + "	f.tipojurfis,\n"
                    + "	f.razaosocial,\n"
                    + "	f.fantasia,\n"
                    + "	f.cnpj,\n"
                    + "	f.ie,\n"
                    + "	f.cpf,\n"
                    + "	f.rg,\n"
                    + "	f.endereco,\n"
                    + "	f.nrcasa,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.cidade,\n"
                    + "	f.estado,\n"
                    + "	f.cxpostal,\n"
                    + "	f.email,\n"
                    + "	f.website,\n"
                    + "	f.fone,\n"
                    + "	f.fax,\n"
                    + "	f.obs,\n"
                    + "	f.contato,\n"
                    + "	f.inativo,\n"
                    + "	f.codibge_municipio, \n"
                    + "	f.codpais,\n"
                    + "	cp.DESCRICAO\n"
                    + "from\n"
                    + "	cdfornecedor f\n"
                    + "left join\n"
                    + "	cdcondpgto cp on cp.codcond = f.CODCOND\n"
                    + "order by\n"
                    + "	f.CODFORN")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codforn"));
                    imp.setDatacadastro(rs.getDate("dtcadast"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if ("J".equals(rs.getString("tipojurfis"))) {
                        imp.setCnpj_cpf(rs.getString("cnpj"));
                        imp.setIe_rg(rs.getString("ie"));
                    } else {
                        imp.setCnpj_cpf(rs.getString("cpf"));
                        imp.setCnpj_cpf(rs.getString("ie"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("nrcasa"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setIbge_municipio(rs.getInt("codibge_municipio"));

                    if ((rs.getString("cxpostal") != null) && (!"".equals(rs.getString("cxpostal")))) {
                        imp.addTelefone("Cx. Postal", rs.getString("cxpostal"));
                    }

                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addEmail("Email", rs.getString("email"), TipoContato.COMERCIAL);
                    }

                    if ((rs.getString("website") != null) && (!"".equals(rs.getString("website")))) {
                        imp.addContato("Website", rs.getString("website"), null, TipoContato.COMERCIAL, null);
                    }

                    imp.setTel_principal(rs.getString("fone"));

                    if ((rs.getString("fax") != null) && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }

                    imp.setObservacao(rs.getString("obs"));

                    if ((rs.getString("contato") != null)
                            && (!"".equals(rs.getString("contato")))
                            && (!"N/D".equals(rs.getString("contato").trim()))) {
                        imp.addContato("Contato", rs.getString("contato"), null, TipoContato.COMERCIAL, null);
                    }

                    imp.setAtivo("N".equals(rs.getString("inativo")) ? true : false);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codcli,\n"
                    + "    TIPOJURFIS,\n"
                    + "    razaosocial, \n"
                    + "    fantasia,\n"
                    + "    ie,\n"
                    + "	 replace(replace(replace(CNPJ, '.', ''), '/', ''), '-', '') as cnpj,\n"
                    + "	 replace(replace(cpf, '.', ''), '-', '') as cpf,\n"
                    + "    rg,\n"
                    + "    endereco,\n"
                    + "    nrcasa,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    cep,\n"
                    + "    cidade,\n"
                    + "    estado,\n"
                    + "    email,\n"
                    + "    fone,\n"
                    + "    fone2,\n"
                    + "    celular,\n"
                    + "    fax,\n"
                    + "    obs,\n"
                    + "    contato,\n"
                    + "    endcobr,\n"
                    + "    nrcasacobr,\n"
                    + "    complcobr,\n"
                    + "    bairrocobr,\n"
                    + "    cepcobr,\n"
                    + "    estadocobr,\n"
                    + "    cidadecobr,\n"
                    + "    endentrg,\n"
                    + "    nrcasaentr,\n"
                    + "    bairroentrg,\n"
                    + "    cepentrg,\n"
                    + "    estadoentrg,\n"
                    + "    cidadeentrg,\n"
                    + "    dtcadast,\n"
                    + "    dtalteracao,\n"
                    + "    dtnasc,\n"
                    + "    limitecred,\n"
                    + "    saldo,\n"
                    + "    inativo,\n"
                    + "    dtalter,\n"
                    + "    codibge_municipio,\n"
                    + "    produtor_rural\n"
                    + "from\n"
                    + "    cdclientes\n"
                    + "where\n"
                    + "   codcli > 0\n"
                    + "order by\n"
                    + "    codcli")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codcli"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));

                    if ((rs.getString("cpf") != null) && (rs.getString("tipojurfis") != null) && ("F".equals(rs.getString("tipojurfis")))) {
                        if (rs.getString("cpf").contains("T")) {
                            imp.setCnpj(imp.getId());
                            imp.setInscricaoestadual(rs.getString("rg"));
                        } else {
                            imp.setCnpj(rs.getString("cpf"));
                            imp.setInscricaoestadual(rs.getString("rg"));
                        }
                    } else if ((rs.getString("tipojurfis") != null) && ("J".equals(rs.getString("tipojurfis")))) {
                        if (rs.getString("cnpj").contains("T")) {
                            imp.setCnpj(imp.getId());
                            imp.setInscricaoestadual(rs.getString("ie"));
                        } else {
                            imp.setCnpj(rs.getString("cnpj"));
                            imp.setInscricaoestadual(rs.getString("ie"));
                        }
                    } else {
                        imp.setCnpj(rs.getString("cpf"));
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("nrcasa"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("codibge_municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("fone"));
                    if ((rs.getString("fone2") != null) && (!"".equals(rs.getString("fone2")))) {
                        imp.addTelefone("Telefone 2", rs.getString("fone2"));
                    }
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    if ((rs.getString("contato") != null) && (!"".equals(rs.getString("contato")))) {
                        imp.addContato("1", "Contato", rs.getString("contato"), null, null);
                    }
                    imp.setObservacao(rs.getString("obs"));

                    //Endereço de Cobrança
                    imp.setCobrancaEndereco(rs.getString("endcobr"));
                    imp.setCobrancaNumero(rs.getString("nrcasacobr"));
                    imp.setCobrancaComplemento(rs.getString("complcobr"));
                    imp.setCobrancaBairro(rs.getString("bairrocobr"));
                    imp.setCobrancaCep(rs.getString("cepcobr"));
                    imp.setCobrancaUf(rs.getString("estadocobr"));
                    imp.setCobrancaMunicipio(rs.getString("cidadecobr"));

                    imp.setDataCadastro(rs.getDate("dtcadast"));
                    imp.setDataNascimento(rs.getDate("dtnasc"));
                    imp.setValorLimite(rs.getDouble("limitecred"));
                    imp.setAtivo("N".equals(rs.getString("inativo")) ? true : false);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);

                    LOG.fine("id_cliente: " + imp.getId() + " cnpj: " + imp.getCnpj());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getPlanoContas() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codforma,\n"
                    + "	descricao\n"
                    + "from \n"
                    + "	cdformapgto\n"
                    + "order by\n"
                    + "descricao")) {
                while (rs.next()) {
                    result.add(new ItemComboVO(rs.getInt("codforma"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.idlancfin as id,\n"
                    + "	l.codcli as idcliente,\n"
                    + "	c.cnpj,\n"
                    + "	c.cpf,\n"
                    + "	l.nrseqparc as parcela,\n"
                    + "	l.nrtitulo as idconta,\n"
                    + "	l.historico as obs,\n"
                    + "	l.dtemissao,\n"
                    + "	l.dtvenc,\n"
                    + "	l.vlrtitulo\n"
                    + "from\n"
                    + "	lanc_fin l \n"
                    + "join\n"
                    + "	cdclientes c on c.codcli = l.codcli\n"
                    + "where\n"
                    + "	l.codforma = " + vPlanoContas + " and\n"
                    + "	l.status = 'AB' and\n"
                    + "	l.tipotrans = 'C'\n"
                    + "order by\n"
                    + "	l.dtemissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    if ((rs.getString("cnpj") != null)
                            && ((rs.getString("cnpj").replace("/", "").replace("-", "").replace(".", "").trim()) != null)
                            && (!"".equals(rs.getString("cnpj").replace("/", "").replace("-", "").replace(".", "").trim()))) {
                        imp.setCnpjCliente(rs.getString("cnpj"));
                    } else {
                        imp.setCnpjCliente(rs.getString("cpf"));
                    }
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setNumeroCupom(rs.getString("idconta"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvenc"));
                    imp.setValor(rs.getDouble("vlrtitulo"));

                    result.add(imp);
                }
            }

        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.idlancfin as id,\n"
                    + "	l.codcli as idcliente,\n"
                    + "	c.cnpj,\n"
                    + "	c.cpf,\n"
                    + "   c.rg,\n"
                    + "   c.RAZAOSOCIAL,\n"
                    + "   c.fone,\n"
                    + "	l.nrseqparc as parcerla,\n"
                    + "	l.nrtitulo as idconta,\n"
                    + "	l.historico as obs,\n"
                    + "	l.dtemissao,\n"
                    + "	l.dtvenc,\n"
                    + "	l.vlrtitulo,\n"
                    + "	lc.AGENCIA,\n"
                    + "	lc.CODBANCO,\n"
                    + "	lc.CTACORRENTE,\n"
                    + "	lc.NRCHEQUE\n"
                    + "from\n"
                    + "	lanc_fin l \n"
                    + "join\n"
                    + "	cdclientes c on c.codcli = l.codcli\n"
                    + "join\n"
                    + "	lanc_cheques lc on lc.idlancfin = l.idlancfin\n"
                    + "where\n"
                    + "	l.codforma = " + vPlanoContas + " and\n"
                    + "	l.status = 'AB' and\n"
                    + "	l.tipotrans = 'C'\n"
                    + "order by\n"
                    + "	l.dtemissao")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setAlinea(0);
                    imp.setBanco(rs.getInt("codbanco"));
                    imp.setCpf(rs.getString("cpf"));
                    imp.setRg(rs.getString("rg"));
                    imp.setConta(rs.getString("ctacorrente"));
                    imp.setDate(rs.getDate("dtemissao"));
                    imp.setDataDeposito(rs.getDate("dtvenc"));
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCheque(rs.getString("nrcheque"));
                    imp.setNumeroCupom(rs.getString("idconta"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setValor(rs.getDouble("vlrtitulo"));
                    imp.setNome(rs.getString("razaosocial"));
                    imp.setSituacaoCheque(SituacaoCheque.ABERTO);
                    imp.setTelefone(rs.getString("fone"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    l.idlancfin as id,\n"
                    + "    l.CODFORN,\n"
                    + "    l.nrseqparc as parcela,\n"
                    + "    l.nrtitulo as idconta,\n"
                    + "    l.historico as obs,\n"
                    + "    l.dtemissao,\n"
                    + "    l.dtentrada, \n"
                    + "    l.dtvenc,\n"
                    + "    l.vlrtitulo\n"
                    + "from\n"
                    + "    lanc_fin l \n"
                    + "where\n"
                    + "    l.codforma = " + vPlanoContaCP + " and\n"
                    + "    l.status = 'AB' and\n"
                    + "    l.tipotrans = 'D'\n"
                    + "order by\n"
                    + "    l.dtemissao")) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("codforn"));
                    imp.setNumeroDocumento(rs.getString("idconta"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setValor(rs.getDouble("vlrtitulo"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataEntrada(rs.getDate("dtentrada"));
                    imp.addVencimento(rs.getDate("dtvenc"), imp.getValor());

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
        return new KcmsDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new KcmsDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

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

                        next.setId(rst.getString("idvenda"));
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("cupomfiscal")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("dtemissao"));
                        next.setIdClientePreferencial(rst.getString("codcli"));
                        next.setHoraInicio(timestamp.parse(rst.getString("dhemissao")));
                        next.setHoraTermino(timestamp.parse(rst.getString("datahoraautorizacao")));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("vltotal"));

                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("vldesc"));
                        next.setValorAcrescimo(rst.getDouble("vlacres"));
                        next.setNumeroSerie(rst.getString("numserieecf") + " PDV: " + rst.getString("codpdv"));
                        next.setModeloImpressora(rst.getString("modeloecf"));
                        next.setChaveCfe(rst.getString("chave_acesso"));
                        next.setNomeCliente(rst.getString("razaosocial"));

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("nrcasa")) + ","
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
                    + "    v.idvdapdv as idvenda,\n"
                    + "    v.codpdv,\n"
                    + "    v.codpdv as ecf,\n"
                    + "    v.id_externo,\n"
                    + "    v.coo as cupomfiscal,\n"
                    + "    v.dtemissao,\n"
                    + "    v.hremissao,\n"
                    + "    v.dhemissao,\n"
                    + "    v.datahoraautorizacao,\n"
                    + "    case when v.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    coalesce(v.vlrcompra, 0) as vlcompra,\n"
                    + "    coalesce(v.vlrdesc, 0) as vldesc,\n"
                    + "    coalesce(v.vlracres, 0) as vlacres,\n"
                    + "    coalesce(v.vlrtotal, 0) as vltotal,\n"
                    + "    v.dtmovto,\n"
                    + "    v.modeloecf,\n"
                    + "    v.numserieecf,\n"
                    + "    v.chave_acesso,\n"
                    + "    v.modelo_doc,\n"
                    + "    v.totimpostocf,\n"
                    + "    case when v.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    case when c.codcli = 0 then null else c.codcli end as codcli,\n"
                    + "    c.razaosocial,\n"
                    + "    c.CPF,\n"
                    + "    c.cnpj,\n"
                    + "    c.endereco,\n"
                    + "    c.nrcasa,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.estado,\n"
                    + "    c.cep,\n"
                    + "    tri.percentual\n"
                    + "from\n"
                    + "    vdapdv v\n"
                    + "left join\n"
                    + "    cdclientes c on c.codcli = v.codcli\n"
                    + "left join\n"
                    + "    cdaliquota tri on tri.codaliq = v.regime_tributacao\n"
                    + "where\n"
                    + "    (v.dtemissao between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103) and convert(datetime, '" + FORMAT.format(dataTermino) + "', 103)) and\n"
                    + "    v.coo <> 0 and\n"
                    + "    v.vlrcompra is not null\n"
                    + "order by\n"
                    + "    v.dtemissao, v.coo";
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        next.setVenda(rst.getString("idvenda"));
                        next.setId(rst.getString("idvendaitem"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("aliqicms"));
                        next.setIcmsCst(rst.getInt("csticms"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	vi.idvdapdvitem as idvendaitem,\n"
                    + "	vi.idvdapdv as idvenda,\n"
                    + "   p.codprod as idproduto,\n"
                    + "	vi.codbarra as codigobarras,\n"
                    + "   vi.unidade,\n"
                    + "	vi.qtde as quantidade,\n"
                    + "	vi.precocusto,\n"
                    + "	vi.precovenda,\n"
                    + "   vi.vlrdesc as desconto,\n"
                    + "   vi.vlracres as acrescimo,\n"
                    + "	vi.totalitem as valortotal,\n"
                    + "	case when vi.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "	vi.percicms as aliqicms,\n"
                    + "	cast(vi.csticms as integer) as csticms,\n"
                    + "	vi.cstcofins,\n"
                    + "	vi.percpis,\n"
                    + "	vi.perccofins,\n"
                    + "	vi.dtemissao as dataemissao,\n"
                    + "	vi.hremissao as horaemissao,\n"
                    + "	vi.dhemissao as datahoraemissao,\n"
                    + "	(case when \n"
                    + "		vi.descricao is null then\n"
                    + "		p.descricao\n"
                    + "	else vi.descricao end) as descricao\n"
                    + "from\n"
                    + "	vdapdvitem vi \n"
                    + "join\n"
                    + "	cdprodutos p on p.codbarra = vi.codbarra\n"
                    + "   where\n"
                    + "       (vi.dtemissao between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103) and convert(datetime, '" + FORMAT.format(dataTermino) + "', 103))"
                    + "order by\n"
                    + "	idvdapdv";
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
