package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class AsefeDAO extends InterfaceDAO {
    
    @Override
    public String getSistema() {
        return "Asefe";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "M1.CODIGO MERC1, M1.DESCRICAO DESC_MERC1,\n"
                    + "M2.CODIGO_GRUPOS MERC2, M2.DESCRICAO_GRUPOS DESC_MERC2,\n"
                    + "'1' MERC3, M2.DESCRICAO_GRUPOS DESC_MERC3\n"
                    + "FROM\n"
                    + "CE_PRODUTOS P\n"
                    + "INNER JOIN CE_SETORES M1 ON M1.CODIGO = P.CODIGOSETOR\n"
                    + "INNER JOIN CE_GRUPOS M2 ON M2.CODIGO_GRUPOS = P.CODGRU_PRODUTOS\n"
                    + "ORDER BY M1.CODIGO, M2.CODIGO_GRUPOS"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("MERC1"));
                    imp.setMerc1Descricao(rst.getString("DESC_MERC1"));
                    imp.setMerc2ID(rst.getString("MERC2"));
                    imp.setMerc2Descricao(rst.getString("DESC_MERC2"));
                    imp.setMerc3ID(rst.getString("MERC3"));
                    imp.setMerc3Descricao(rst.getString("DESC_MERC3"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CODBARRA_PRODUTOS, "
                    + "CODPROD_PRODUTOS, "
                    + "DESCRICAO_PRODUTOS,\n"
                    + "UNIDADE_PRODUTOS, "
                    + "CODGRU_PRODUTOS, "
                    + "CUSTO_PRODUTOS, "
                    + "VENDA_PRODUTOS,\n"
                    + "NCM_PRODUTOS, "
                    + "STPIS, "
                    + "STCOFINS, "
                    + "STICMS, "
                    + "CEST, "
                    + "CODIGOSETOR, "
                    + "CODGRU_PRODUTOS\n"
                    + "FROM\n"
                    + "CE_PRODUTOS\n"
                    + "ORDER BY \n"
                    + "CODPROD_PRODUTOS"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                    imp.setEan(rst.getString("CODBARRA_PRODUTOS"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO_PRODUTOS"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoGondola());
                    imp.setCodMercadologico1(rst.getString("CODIGOSETOR"));
                    imp.setCodMercadologico2(rst.getString("CODGRU_PRODUTOS"));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rst.getDouble("VENDA_PRODUTOS"));
                    imp.setCustoComImposto(rst.getDouble("CUSTO_PRODUTOS"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("NCM_PRODUTOS"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getInt("STPIS"));
                    imp.setPiscofinsCstCredito(rst.getInt("STCOFINS"));
                    imp.setIcmsCst(rst.getInt("STICMS"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
