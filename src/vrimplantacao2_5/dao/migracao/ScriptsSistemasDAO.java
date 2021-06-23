package vrimplantacao2_5.dao.migracao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasDAO {
    
    public String getLojas(int idSistema, int idBancodados) throws Exception {
        String sql = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "script_getlojas \n"
                    + "from implantacao2_5.sistemabancodadosscripts \n"
                    + "where id_sistema = " + idSistema + "\n"
                    + "and id_bancodados = " + idBancodados
            )) {
                if (rs.next()) {
                    sql = rs.getString("script_getlojas").replace("#", "'");
                }
            }
        }
        return sql;
    }
}