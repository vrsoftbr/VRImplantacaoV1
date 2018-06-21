package vrimplantacao2.dao.cadastro.cliente.food;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Leandro
 */
public class ClienteFoodTelefoneDAO {

    public Map<Long, ClienteFoodVO> getTelefones() throws Exception {
        Map<Long, ClienteFoodVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ct.telefone,\n" +
                    "	c.id,\n" +
                    "	c.nome,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.bairro,\n" +
                    "	c.observacao,\n" +
                    "	c.id_situacaocadastro,\n" +
                    "	c.id_municipio\n" +
                    "from\n" +
                    "	food.clientetelefone ct\n" +
                    "	join food.cliente c on\n" +
                    "		ct.id_cliente = c.id\n" +
                    "order by\n" +
                    "	1, 2"
            )) {
                while (rst.next()) {
                    ClienteFoodVO vo = new ClienteFoodVO();
                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    vo.setEndereco(rst.getString("endereco"));
                    vo.setNumero(rst.getString("numero"));
                    vo.setBairro(rst.getString("bairro"));
                    vo.setObservacao(rst.getString("observacao"));
                    vo.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("id_situacaocadastro")));
                    vo.setId_municipio(rst.getInt("id_municipio"));
                    
                    result.put(Utils.stringToLong(rst.getString("telefone")), vo);
                }
            }
        }
        
        return result;
    }

    public void incluirTelefone(int id, long telefone) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("food");
        sql.setTableName("clientetelefone");
        sql.put("id_cliente", id);
        sql.put("telefone", String.valueOf(telefone));
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getInsert());
        }
    }
    
}
