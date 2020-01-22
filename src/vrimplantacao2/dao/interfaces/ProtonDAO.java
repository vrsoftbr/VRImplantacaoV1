package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class ProtonDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "PROTON";
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "       tund_unidade_pk id,\n" +
                    "       tund_fantasia fantasia\n" +
                    "from \n" +
                    "       TUND_UNIDADE")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  distinct\n" +
                    "  g.tmer_grupo_mercadoria_pk merc1,\n" +
                    "  g.tmer_descricao descmerc1,\n" +
                    "  s.tmer_subgrupo_mercadoria_pk merc2,\n" +
                    "  s.tmer_descricao descmerc2\n" +
                    "from\n" +
                    "  tmer_mercadoria p\n" +
                    "join tmer_grupo_mercadoria g \n" +
                    "     on p.tmer_grupo_mercadoria_fk = g.tmer_grupo_mercadoria_pk\n" +
                    "join tmer_subgrupo_mercadoria s \n" +
                    "     on p.tmer_subgrupo_mercadoria_fk = s.tmer_subgrupo_mercadoria_pk\n" +
                    "order by\n" +
                    "  2, 4")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc2"));
                    imp.setMerc3Descricao(rs.getString("descmerc2"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  tmer_familia_pk id,\n" +
                    "  tmer_descricao descricao \n" +
                    "from \n" +
                    "  tmer_familia\n" +
                    "where            \n" +
                    "  tmer_unidade_fk_pk = 1\n" +
                    "order by\n" +
                    "  2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
