package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class PrimeDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Prime";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'E'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    p.cadp_codigo as id,\n"
                    + "    p.cadp_balanca as balanca,\n"
                    + "    p.cadp_codigobarra as ean,\n"
                    + "    p.cadp_descricaounmedida as tipoembalagem,\n"
                    + "    p.cadp_situacao as situacaocadastro,\n"
                    + "    p.cadp_descricao as descricaocompleta,\n"
                    + "    p.cadp_descricaoreduzida as descricaoreduzida,\n"
                    + "    p.cadp_codcategoria,\n"
                    + "    p.cadp_categoria,\n"
                    + "    p.cadp_dtcadastro as datacadastro,\n"
                    + "    p.cadp_dtalteracao as dataalteracao,\n"
                    + "    p.cadp_codigoncm as ncm,\n"
                    + "    p.cadp_cest as cest,\n"
                    + "    p.cadp_cstpise as cstpisentrada,\n"
                    + "    p.cadp_cstpiss as cstpissaida,\n"
                    + "    pe.cade_codclassificacaoe,\n"
                    + "    cle.clat_cst as csticmsentrada,\n"
                    + "    cle.clat_icms as aliqicmsentrada,\n"
                    + "    cle.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_codclassificacaos,\n"
                    + "    cls.clat_cst as csticmsentrada,\n"
                    + "    cls.clat_icms as aliqicmsentrada,\n"
                    + "    cls.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_estmin as estoqueminimo,\n"
                    + "    pe.cade_estmax as estoquemaximo,\n"
                    + "    pe.cade_qemb as qtdembalagem, \n"
                    + "    pe.cade_margemcontribmin as margem,\n"
                    + "    pe.cade_prvenda as precpvenda,\n"
                    + "    pe.cade_ctnota as custo\n"
                    + "from cadprod p\n"
                    + "left join cadprodemp pe on pe.cade_codigo = p.cadp_codigo\n"
                    + "	and pe.cade_codempresa = '001'\n"
                    + "left join classtrib cle on cle.clat_codsimb = pe.cade_codclassificacaoe\n"
                    + "	and cle.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cle.clat_es = 'E'\n"
                    + "left join classtrib cls on cls.clat_codsimb = pe.cade_codclassificacaos\n"
                    + "	and cls.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cls.clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    codb_codprod as idproduto,\n"
                    + "    codb_codbarra as ean\n"
                    + "from codigosbarra\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    result.add(imp);
                }
            }
        }

        return result;
    }
}
