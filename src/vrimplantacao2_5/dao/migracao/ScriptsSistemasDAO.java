package vrimplantacao2_5.dao.migracao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasDAO {
    
    public String getLojas(int id_sistema, int id_bancodados) throws Exception {
        String sql = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "script_getlojas \n"
                    + "from implantacao2_5.sistemabancodadosscripts \n"
                    + "where id_sistema = " + id_sistema + "\n"
                    + "and id_bancodados = " + id_bancodados
            )) {
                if (rs.next()) {
                    sql = rs.getString("script_getlojas");
                }
            }
        }
        return sql;
    }
}