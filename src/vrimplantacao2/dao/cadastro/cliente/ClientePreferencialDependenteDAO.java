package vrimplantacao2.dao.cadastro.cliente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialDependenteVO;

/**
 * Classe responsável por comunicar com o banco de dados.
 *
 * @author Wesley
 */
public class ClientePreferencialDependenteDAO {

    public void salvar(ClientePreferencialDependenteVO dependente) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("clientepreferencialdependente");
            sql.put("id_clientepreferencial", dependente.getIdClientePreferencial());
            sql.put("cpf", dependente.getCpf());
            sql.put("nome", dependente.getNome());
            sql.put("tipodependente", dependente.getTipoDependente());

            stm.executeUpdate(sql.getInsert());
        }
    }

    public void atualizar(ClientePreferencialDependenteVO dependente) throws Exception {
        String sql = "UPDATE clientepreferencialdependente SET nome = ?, tipodependente = ? WHERE id_clientepreferencial = ? AND cpf = ?";
        try (PreparedStatement stm = Conexao.prepareStatement(sql)) {
            stm.setString(1, dependente.getNome());
            stm.setString(2, dependente.getTipoDependente());
            stm.setInt(3, dependente.getIdClientePreferencial());
            stm.setLong(4, dependente.getCpf());

            stm.executeUpdate();
        }

    }

    public MultiMap<String, Void> getDependentesExistentes() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	id_clientepreferencial,\n"
                    + "	nome,\n"
                    + "	cpf\n"
                    + "from \n"
                    + "	clientepreferencialdependente\n"
                    + "order by\n"
                    + "	id_clientepreferencial,\n"
                    + "	nome,\n"
                    + "	cpf"
            )) {
                while (rst.next()) {
                    result.put(
                            null,
                            rst.getString("id_clientepreferencial"),
                            rst.getString("cpf")
                    );
                }
            }
        }

        return result;
    }

}
