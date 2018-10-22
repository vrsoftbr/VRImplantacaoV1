package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class UmPontoDoisDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "1.2 Informatica";
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	empcod id,\n" +
                    "	empnomfan fantasia\n" +
                    "from \n" +
                    "	empresa")) {
                result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	prdcod, \n" +
                    "	prdean \n" +
                    "from \n" +
                    "	cadastrodeprodutos \n" +
                    "where \n" +
                    "	prdean <> prdcodbarras and\n" +
                    "	prdean <> ''")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("prdcod"));
                    imp.setEan(rs.getString("prdean"));
                    imp.setQtdEmbalagem(1);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	GrpPrdCod merc1,\n" +
                    "	grpprddsc descmerc1,\n" +
                    "   GrpPrdCod merc2,\n" +
                    "	grpprddsc descmerc2,\n" +
                    "	GrpPrdCod merc3,\n" +
                    "	grpprddsc descmerc3\n" +
                    "from \n" +
                    "	grupodeprodutos\n" +
                    "order by \n" +
                    "	GrpPrdCod")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	PrdCod as id,\n" +
                    "	prdId as idtabela,\n" +
                    "	PrdDsc as descricaocompleta,\n" +
                    "	prdEan as ean,\n" +
                    "	PrdCodBarras codigobarras,\n" +
                    "	PrdAtv as ativo,\n" +
                    "	PrdVlrCus as valorcusto,\n" +
                    "	PrdCusMed as customedio,\n" +
                    "	PrdVlrCusSt as valorcustoST,\n" +
                    "	PrdVlrVen as valorvenda,\n" +
                    "	PrdMargLuc as margem,\n" +
                    "	PrdUndCod as embalagem,\n" +
                    "	PrdEstMin as estoqueminimo,\n" +
                    "	PrdEstAtu as estoque,\n" +
                    "	GrpPrdCod as merc1,\n" +
                    "	GrpPrdCod as merc2,\n" +
                    "	GrpPrdCod as merc3,\n" +
                    "	PrdPeso as peso,\n" +
                    "	PrdCstIcms as csticms,\n" +
                    "	PrdNcmCod as ncm,\n" +
                    "	cf.CfopSpAliq icmsdebito,\n" +
                    "   PrdValidade,\n" +
                    "   PrdValidade2\n" +
                    "from \n" +
                    "	cadastrodeprodutos c \n" +
                    "left join\n" +
                    "	cfop cf on cf.CfopSeq = c.PrdCfopSeq\n" +
                    "order by\n" +
                    "	PrdCod")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagem(1);
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setCustoComImposto(rs.getDouble("valorcusto"));
                    imp.setCustoSemImposto(rs.getDouble("valorcusto"));
                    imp.setPrecovenda(rs.getDouble("valorvenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    if(rs.getDouble("peso") != 0) {
                        imp.setPesoBruto(rs.getDouble("peso"));
                        imp.setPesoLiquido(rs.getDouble("peso"));
                    }
                    imp.setIcmsCstSaida(rs.getInt("csticms"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setNcm(rs.getString("ncm"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
