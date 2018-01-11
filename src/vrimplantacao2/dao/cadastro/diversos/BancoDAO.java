package vrimplantacao2.dao.cadastro.diversos;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;

/**
 *
 * @author Leandro
 */
public class BancoDAO {

    public Set<Integer> getBancosExistentes() throws Exception {
        Set<Integer> result = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from banco where id_situacaocadastro = 1 order by id"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
}
