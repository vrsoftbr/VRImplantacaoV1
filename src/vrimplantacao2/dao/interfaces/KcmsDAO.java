package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class KcmsDAO extends InterfaceDAO implements MapaTributoProvider {

    public String id_loja;
    public boolean usarMargemBruta;
    public boolean vBalanca;
    
    @Override
    public String getSistema() {
        return "KCMS" + id_loja;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	codtrib,\n" +
                    "	sittrib,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	cdtributacao")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codtrib"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codloja,\n" +
                    "	razaosocial,\n" +
                    "	fantasia \n" +
                    "from empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codloja"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	GEN.CODGENERO as codmerc1,\n" +
                    "	GEN.DESCRICAO as descmerc1,\n" +
                    "	GRU.CODGRUPO as codmerc2,\n" +
                    "	GRU.GRUPO as descmerc2,\n" +
                    "	COALESCE(CODGRU.CODSUBGRUPO, 1) as codmerc3, \n" +
                    "	coalesce(CODGRU.DESCRICAO, gru.grupo) as descmerc3 \n" +
                    "FROM \n" +
                    "	CDGENEROS AS GEN\n" +
                    "INNER JOIN \n" +
                    "	CDGRUPOS AS GRU ON GRU.CODGENERO = GEN.CODGENERO\n" +
                    "LEFT JOIN \n" +
                    "	CDSUBGRUPOS AS CODGRU ON CODGRU.CODGRUPO  = GRU.CODGRUPO AND\n" +
                    "	CODGRU.CODGENERO = GRU.CODGENERO\n" +
                    "ORDER BY \n" +
                    "	GEN.CODGENERO,\n" +
                    "	GRU.CODGRUPO,\n" +
                    "	CODGRU.CODSUBGRUPO")) {
                while(rs.next()) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	CODFAMILIA,\n" +
                    "	DESCRICAO,\n" +
                    "	INATIVO\n" +
                    "FROM \n" +
                    "	CDFAMILIAS")) {
                while(rs.next()) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "  SELECT \n" +
                    "	P.CODPROD,\n" +
                    "	P.CODBARRA,\n" +
                    "	P.DESCRICAO,\n" +
                    "	P.DTCADAST,\n" +
                    "	P.DESCRNF,\n" +
                    "	P.DESCRBAL,\n" +
                    "	P.VALBALANCA,\n" +
                    "	P.PESAVEL,\n" +
                    "	P.UNIDADE,\n" +
                    "	P.UNDCOMPRA,\n" +
                    "	P.QTDEMBAL,\n" +
                    "	P.QTDEMBALCPR,\n" +
                    "	P.CODGENERO,\n" +
                    "	P.CODGRUPO,\n" +
                    "	coalesce(P.CODSUBGRUPO, 1) codsubgrupo,\n" +
                    "	P.CODFAMILIA,\n" +
                    "	P.ESTOATU,\n" +
                    "	P.ESTOMIN,\n" +
                    "	P.PRECOENTR,\n" +
                    "	P.PRECOCUSTO,\n" +
                    "	P.PRECOVEND,\n" +
                    "	P.CODTRIB,\n" +
                    "	trib.SITTRIB as csticms,\n" +
                    "	P.CODALIQ,\n" +
                    "	P.CODALIQNF,\n" +
                    "	P.PERCICMSCR,\n" +
                    "	P.PERCREDUC,\n" +
                    "	P.MARGBRUT,\n" +
                    "	P.MARGPARAM,\n" +
                    "	P.CODSETOR,\n" +
                    "	P.PROMOCAO,\n" +
                    "	P.PEGAR_PESO,\n" +
                    "	P.INATIVO,\n" +
                    "	P.ESTOIDEAL,\n" +
                    "	P.CODTIPOPRODUTO,\n" +
                    "	P.IDCDTIPOCOFINS,\n" +
                    "	fc.cod_cst_cofins,\n" +
                    "	P.IDCDTIPOPIS,\n" +
                    "	fp.cod_cst_pis,\n" +
                    "	P.MARGBRUTWEB,\n" +
                    "	P.CODLOJA,\n" +
                    "	P.CODCFOP,\n" +
                    "	P.CODSIMILAR,\n" +
                    "	P.CODGRUPOFISCAL,\n" +
                    "	P.PERCALIQSUBTRIB,\n" +
                    "	P.TIPOSUBTRIB,\n" +
                    "	P.CODALIQECF,\n" +
                    "	P.IDNCM,\n" +
                    "	P.CODTRIBSN,\n" +
                    "	P.CODCSTPIS,\n" +
                    "	P.CODCSTCOFINS,\n" +
                    "	P.COD_NAT_BC_CRED_COFINS,\n" +
                    "	P.COD_NAT_BC_CRED_PIS,\n" +
                    "	P.COD_TIPO_CREDITO_COFINS,\n" +
                    "	P.COD_TIPO_CREDITO_PIS,\n" +
                    "	P.CODCFOP_ENTRADA,\n" +
                    "	P.NAT_RECEITA_COFINS,\n" +
                    "	P.NAT_RECEITA_PIS,\n" +
                    "	P.CODCSTPIS_ENTRADA,\n" +
                    "	P.CODCSTCOFINS_ENTRADA,\n" +
                    "	P.PERCCOFINS_ENTRADA,\n" +
                    "	NCM.CODNCM\n" +
                    "FROM \n" +
                    "	CDPRODUTOS AS P\n" +
                    "LEFT JOIN \n" +
                    "	CDNCM AS NCM ON NCM.IDNCM = P.IDNCM\n" +
                    "LEFT JOIN\n" +
                    "	fis_cst_cofins fc ON fc.id_fis_cst_cofins = p.IDCDTIPOCOFINS\n" +
                    "LEFT JOIN\n" +
                    "	fis_cst_pis fp ON fp.id_fis_cst_pis = p.IDCDTIPOPIS\n" +
                    "LEFT JOIN\n" +
                    "	CDTRIBUTACAO trib ON trib.CODTRIB = p.CODTRIB\n" +
                    " --where \n" +
                    "	--codbarra  = '2000256' \n" +
                    "  --where p.CODPROD = 58531\n" +
                    "ORDER BY \n" +
                    "	PESAVEL DESC,\n" +
                    "	DESCRICAO")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codprod"));
                    imp.setEan(rs.getString("codbarra"));                    
                    imp.setDescricaoCompleta(rs.getString("descrnf"));
                    imp.setDescricaoReduzida(rs.getString("descricao"));
                    imp.setDataCadastro(rs.getDate("dtcadast"));
                    imp.setDescricaoGondola(rs.getString("descricao"));
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
                    if(usarMargemBruta) {
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
                    
                    String ean = rs.getString("codbarra").substring(2, rs.getString("codbarra").length());
                    
                    if((rs.getString("codbarra") != null) && ("S".equals(rs.getString("pesavel"))) && 
                            (ean.trim().length() <= 6)){
                        if(vBalanca) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    " 	pf.codforn,\n" +
                    "	pf.codprodforn,\n" +
                    "	pr.CODPROD\n" +
                    "FROM \n" +
                    "	PRODFORN pf\n" +
                    "join\n" +
                    "	CDPRODUTOs pr on pr.CODBARRA = pf.codbarra\n" +
                    "WHERE\n" +
                    "	pf.codforn > 0\n" +
                    "order by\n" +
                    "	pr.CODPROD")) {
                while(rs.next()) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	p.CODPROD,\n" +
                    "	pro.CODBARRA,\n" +
                    "	pro.DESCRICAO,\n" +
                    "	pro.DATADE,\n" +
                    "	pro.DATAATE,\n" +
                    "	pro.PRECOPROMO,\n" +
                    "	pro.PRECOVEND\n" +
                    "FROM \n" +
                    "	CDPROMOCAOITEM pro\n" +
                    "join\n" +
                    "	CDPRODUTOS p on p.codbarra = pro.CODBARRA\n" +
                    "where \n" +
                    "	pro.DATAATE >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "'")) {
                while(rs.next()) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.codforn,\n" +
                    "	f.dtcadast,\n" +
                    "	f.tipojurfis,\n" +
                    "	f.razaosocial,\n" +
                    "	f.fantasia,\n" +
                    "	f.cnpj,\n" +
                    "	f.ie,\n" +
                    "	f.cpf,\n" +
                    "	f.rg,\n" +
                    "	f.endereco,\n" +
                    "	f.nrcasa,\n" +
                    "	f.bairro,\n" +
                    "	f.cep,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cxpostal,\n" +
                    "	f.email,\n" +
                    "	f.website,\n" +
                    "	f.fone,\n" +
                    "	f.fax,\n" +
                    "	f.obs,\n" +
                    "	f.contato,\n" +
                    "	f.inativo,\n" +
                    "	f.codibge_municipio, \n" +
                    "	f.codpais,\n" +
                    "	cp.DESCRICAO\n" +
                    "from\n" +
                    "	cdfornecedor f\n" +
                    "left join\n" +
                    "	cdcondpgto cp on cp.codcond = f.CODCOND\n" +
                    "order by\n" +
                    "	f.CODFORN")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codforn"));
                    imp.setDatacadastro(rs.getDate("dtcadast"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if("J".equals(rs.getString("tipojurfis"))) {
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
                    
                    if((rs.getString("cxpostal") != null) && (!"".equals(rs.getString("cxpostal")))) {
                        imp.addTelefone("Cx. Postal", rs.getString("cxpostal"));
                    }
                    
                    if((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addEmail("Email", rs.getString("email"), TipoContato.COMERCIAL);
                    }
                    
                    if((rs.getString("website") != null) && (!"".equals(rs.getString("website")))) {
                        imp.addContato("Website", rs.getString("website"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    imp.setTel_principal(rs.getString("fone"));
                    
                    if((rs.getString("fax") != null) && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    imp.setObservacao(rs.getString("obs"));
                    
                    if((rs.getString("contato") != null) && 
                            (!"".equals(rs.getString("contato"))) &&
                                (!"N/D".equals(rs.getString("contato").trim()))) {
                        imp.addContato("Contato", rs.getString("contato"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    imp.setAtivo("N".equals(rs.getString("inativo")) ? true : false);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
