package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;

/**
 * Classe responsável por comunicar com o banco de dados.
 * @author Leandro
 */
public class ClientePreferencialContatoDAO {

    public void salvar(ClientePreferencialContatoVO contato) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("clientepreferencialcontato");
            sql.put("id_clientepreferencial", contato.getIdClientePreferencial());
            sql.put("nome", contato.getNome());
            sql.put("telefone", contato.getTelefone());
            sql.put("celular", contato.getCelular());
            sql.put("id_tipocontato", contato.getTipoContato().getId());
            
            stm.executeUpdate(sql.getInsert());
        }
    }

    public MultiMap<String, Void> getContatosExistentes() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id_clientepreferencial,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	celular\n" +
                    "from \n" +
                    "	clientepreferencialcontato\n" +
                    "order by\n" +
                    "	id_clientepreferencial,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	celular"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_clientepreferencial"),
                            rst.getString("nome"),
                            rst.getString("telefone"),
                            rst.getString("celular")
                    );
                }
            }
        }
        
        return result;
    }
    
}
