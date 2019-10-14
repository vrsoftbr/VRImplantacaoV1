package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class SophyxDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SOPHYX";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_razao_social razaosocial\n" +
                    "from\n" +
                    "    lojas"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razaosocial")));
                }
            }
        }
        return result;
    }
    
}
