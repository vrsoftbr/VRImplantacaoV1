package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class LyncisDAO extends InterfaceDAO implements MapaTributoProvider {
    
    public boolean v_usar_arquivoBalanca = false;
    
    @Override
    public String getSistema() {
        return "Lyncis";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	 id,\n" +
                    "    cnpj,\n" +
                    "    fantasia\n" +
                    "from \n" +
                    "	empresa")) {
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
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	  a.depto,\n" +
                    "     a.descritivo AS desc_depto,\n" +
                    "     coalesce(b.secao, a.depto) secao,\n" +
                    "     coalesce(b.descritivo, a.descritivo) AS desc_secao,\n" +
                    "     coalesce(c.grupo, coalesce(b.secao, a.depto)) grupo,\n" +
                    "     coalesce(c.descritivo, coalesce(b.descritivo, a.descritivo)) AS desc_grupo,\n" +
                    "     coalesce(d.subgrupo, coalesce(c.grupo, b.secao)) subgrupo,\n" +
                    "     coalesce(d.descritivo, coalesce(c.descritivo, b.descritivo)) AS desc_subgrupo\n" +
                    "FROM depto a\n" +
                    "     LEFT JOIN depto b ON a.depto = b.depto AND b.secao <> 0 AND b.grupo = 0\n" +
                    "     LEFT JOIN depto c ON b.depto = c.depto AND b.secao = c.secao AND c.grupo <> 0 AND c.subgrupo = 0\n" +
                    "     LEFT JOIN depto d ON c.depto = d.depto AND c.secao = d.secao AND c.grupo = d.grupo AND d.subgrupo <> 0\n" +
                    "WHERE \n" +
                    "	 a.secao = 0\n" +
                    "ORDER BY \n" +
                    "	 a.depto, \n" +
                    "    b.secao, \n" +
                    "    c.grupo, \n" +
                    "    d.subgrupo")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("depto"));
                    imp.setMerc1Descricao(rs.getString("desc_depto"));
                    imp.setMerc2ID(rs.getString("secao"));
                    imp.setMerc2Descricao(rs.getString("desc_secao"));
                    imp.setMerc3ID(rs.getString("grupo"));
                    imp.setMerc3Descricao(rs.getString("desc_grupo"));
                    imp.setMerc4ID(rs.getString("subgrupo"));
                    imp.setMerc4Descricao(rs.getString("desc_subgrupo"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
