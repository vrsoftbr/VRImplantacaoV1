package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class HipcomDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(HipcomDAO.class.getName());

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select lojcod, concat(lojcod,' - ', lojfantas) descricao from hiploj order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("lojcod"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "Hipcom";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	t.trbcod,\n" +
                    "	t.trbdescr,\n" +
                    "	t.trbstrib cst,\n" +
                    "	t.trbaliq aliq,\n" +
                    "	t.trbreduc reducao\n" +
                    "from\n" +
                    "	hiptrb t\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("trbcod"),
                            rst.getString("trbdescr"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            MultiMap<String, MercadologicoNivelIMP> maps = new MultiMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m1.depdepto merc1,\n" +
                    "	m1.depdescr merc1desc\n" +
                    "from\n" +
                    "	hipdep m1	\n" +
                    "where\n" +
                    "	depsecao = 0\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL1: " + rst.getString("merc1") + " - " + rst.getString("merc1desc"));
                    MercadologicoNivelIMP merc = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1desc"));
                    maps.put(merc,
                            rst.getString("merc1")
                    );
                    result.add(merc);
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depdescr merc2desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo = 0\n" +
                    "order by 1,2"
            )) {
                while (rst.next()) {                    
                    LOG.fine("NIVEL2: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc2desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc2"), rst.getString("merc2desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2")
                        );
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depgrupo merc3,\n" +
                    "	m.depdescr merc3desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo != 0 and\n" +
                    "	m.depsubgr = 0\n" +
                    "order by 1,2, 3"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL3: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc3desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc3"), rst.getString("merc3desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3")
                        );
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depgrupo merc3,\n" +
                    "	m.depsubgr merc4,\n" +
                    "	m.depdescr merc4desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo != 0 and\n" +
                    "	m.depsubgr != 0\n" +
                    "order by 1,2, 3, 4"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL3: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc4") + " - " + rst.getString("merc4desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"), rst.getString("merc3"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc4"), rst.getString("merc4desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3"),
                                rst.getString("merc4")
                        );
                    }
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
                    "select famcod, famdescr from hipfam order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("famcod"));
                    imp.setDescricao(rst.getString("famdescr"));
                    
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
                    "select\n" +
                    "	p.procodplu id,\n" +
                    "	p.prodtcad datacadastro,\n" +
                    "	coalesce(ean.barcodbar, p.procodplu) ean,\n" +
                    "	coalesce(ean.barqtemb, 1) qtdembalagem,\n" +
                    "	coalesce(cot.embqtemb, 1) qtdcotacao,\n" +
                    "	substring(p.proembu, 1,2) tipoembalagem,\n" +
                    "	case p.propesado\n" +
                    "	when 'S' then 1\n" +
                    "	else 0 end as ebalanca,\n" +
                    "	p.provalid validade,\n" +
                    "	p.prodescr descricaocompleta,\n" +
                    "	coalesce(nullif(trim(p.prodescgon),''), p.prodescr) descricaogondola,\n" +
                    "	p.prodescres descricaoreduzida,\n" +
                    "	p.prodepto merc1,\n" +
                    "	p.prosecao merc2,\n" +
                    "	p.progrupo merc3,\n" +
                    "	p.prosubgr merc4,\n" +
                    "	p.procodfam id_familia,\n" +
                    "	p.propeso peso,\n" +
                    "	prc.prlestoq estoque,\n" +
                    "	prc.prlmargind margemunit,\n" +
                    "	prc.prlctentr custocomimposto,\n" +
                    "	prc.prlctbal custosemimposto,\n" +
                    "	prc.prlprven precovenda,\n" +
                    "	case prc.prlforalin\n" +
                    "	when 'E' then 0\n" +
                    "	else 1 end id_situacaocadastro,\n" +
                    "	case prc.prlforalin when 'S' then 1 else 0 end descontinuacao,\n" +
                    "	case prc.prlcotacao when 'S' then 1 else 0 end cotacao,\n" +
                    "	p.proclasfisc ncm,\n" +
                    "	p.procest cest,\n" +
                    "	prc.prlcodpiscofe piscofinsentrada,\n" +
                    "	prc.prlcodpiscofs piscofinssaida,\n" +
                    "	prc.prlcodrec piscofinsnatrec,\n" +
                    "	prc.prlcodtris icmssaidaid,\n" +
                    "	prc.prlcodtrie icmsentradaid,\n" +
                    "	prc.prlprvena precoatacado,\n" +
                    "	prc.prlmargata margematacado\n" +
                    "from\n" +
                    "	hippro p\n" +
                    "	left join hipbar ean on\n" +
                    "		ean.barcodplu = p.procodplu\n" +
                    "	left join hipprl prc on\n" +
                    "		prc.prlcodplu = p.procodplu and\n" +
                    "		prc.prlloja = 1\n" +
                    "	left join cotemb cot on\n" +
                    "		cot.embcodplu = p.procodplu\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margemunit"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsDebitoId(rst.getString("icmssaidaid"));
                    imp.setIcmsCreditoId(rst.getString("icmsentradaid"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                    imp.setAtacadoPorcentagem(rst.getDouble("margematacado"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
