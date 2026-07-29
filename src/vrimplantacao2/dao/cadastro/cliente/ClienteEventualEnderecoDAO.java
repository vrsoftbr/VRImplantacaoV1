package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualEnderecoVO;

/**
 *
 * @author Wesley
 */
class ClienteEventualEnderecoDAO {

    public void salvar(ClienteEventualEnderecoVO endereco) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("clienteeventualendereco");
            sql.put("id_clienteeventual", endereco.getIdClienteEventual());
            sql.put("id_tipoendereco", endereco.getTipo_endereco());
            sql.put("endereco", endereco.getEndereco());
            sql.put("numero", endereco.getNumero());
            sql.put("bairro", endereco.getBairro());
            sql.put("complemento", endereco.getComplemento());
            sql.put("cep", endereco.getCep());
            sql.put("id_municipio", endereco.getId_municipio());
            sql.put("id_estado", endereco.getId_estado());
            sql.put("id_pais", endereco.getId_pais());
            sql.put("inscricaoestadual", endereco.getInscricao_estadual());
            sql.put("inscricaomunicipal", endereco.getInscricao_municipal());
            sql.put("id_tipoindicadorie", endereco.getId_tipoindicadorie());
            sql.put("telefone", endereco.getTelefone());

            stm.executeUpdate(sql.getInsert());
        }
    }

    public MultiMap<String, Void> getEnderecosExistentes() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " id_clienteeventual,\n"
                    + " endereco,\n"
                    + " numero,\n"
                    + " bairro,\n"
                    + " cep,\n"
                    + " id_municipio,\n"
                    + " id_estado,\n"
                    + " id_pais \n,"
                    + " id_tipoendereco \n"
                    + "FROM clienteeventualendereco c \n"
                    + "ORDER BY 1, 2, 3, 4"
            )) {
                while (rst.next()) {
                    result.put(
                            null,
                            rst.getString("id_clienteeventual"),
                            rst.getString("endereco"),
                            rst.getString("numero"),
                            rst.getString("bairro"),
                            rst.getString("cep"),
                            rst.getString("id_municipio"),
                            rst.getString("id_estado"),
                            rst.getString("id_pais"),
                            rst.getString("id_tipoendereco")
                    );
                }
            }
        }

        return result;
    }
}
