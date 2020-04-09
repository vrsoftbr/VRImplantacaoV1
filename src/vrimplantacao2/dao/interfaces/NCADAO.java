package vrimplantacao2.dao.interfaces;

import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Importacao
 */
public class NCADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "NCA";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codtribut id,\n" +
                    "	obstribut,\n" +
                    "	aliqicms,\n" +
                    "	cst,\n" +
                    "	tipo\n" +
                    "from \n" +
                    "	nca_tributo_pdv")) {
                result.add(new MapaTributoIMP(rs.getString("id"), 
                        rs.getString("obstribut"), 
                        rs.getInt("cst"), 
                        rs.getDouble("aliqicms"), 
                        0));
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cod_empresa id,\n" +
                    "	fantasia \n" +
                    "from \n" +
                    "	nca_filial\n" +
                    "order by \n" +
                    "	1")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
}
