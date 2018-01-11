package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;

/**
 *
 * @author Leandro
 */
class ClienteEventualContatoDAO {

    public void salvar(ClienteEventualContatoVO contato) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("clienteeventualcontato");
            sql.put("id_clienteeventual", contato.getIdClienteEventual());
            sql.put("nome", contato.getNome());
            sql.put("telefone", contato.getTelefone());
            sql.put("celular", contato.getCelular());
            sql.put("email", contato.getEmail());
            sql.put("id_tipocontato", contato.getTipoContato().getId());
            
            stm.executeUpdate(sql.getInsert());
        }
    }

    public MultiMap<String, Void> getContatosExistentes() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id_clienteeventual,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	celular,\n" +
                    "	email\n" +
                    "from \n" +
                    "	clienteeventualcontato\n" +
                    "order by\n" +
                    "	id_clienteeventual,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	celular,\n" +
                    "	email"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_clienteeventual"),
                            rst.getString("nome"),
                            rst.getString("telefone"),
                            rst.getString("celular"),
                            rst.getString("email")
                    );
                }
            }
        }
        
        return result;
    }
    
}
