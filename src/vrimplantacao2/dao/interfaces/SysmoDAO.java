package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Guilherme
 */
public class SysmoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Sysmo";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "      emp.cod as id,\n" +
                    "      emp.ccg as cnpj,\n" +
                    "      emp.cnm as nome\n" +
                    "from\n" +
                    "    spsemp00 emp\n" +
                    "order by\n" +
                    "      emp.cod"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("cnpj"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }
}
