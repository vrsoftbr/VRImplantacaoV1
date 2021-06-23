package vrimplantacao2_5.dao.migracao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoSistemasDAO {

    public List<Estabelecimento> getLojasOrigem(Connection conexao, String sql) throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql)) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
}
