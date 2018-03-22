package vrimplantacao2.dao.cadastro.comprador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.comprador.CompradorVO;

/**
 *
 * @author Leandro
 */
public class CompradorDAO {

    public void gravar(CompradorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("comprador");
            sql.getReturning().add("id");
            sql.put("id", vo.getId());
            sql.put("nome", vo.getNome());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            
            stm.execute(sql.getInsert());
        }
    }

    public IDStack getIdsVagos(int maxId) throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n" +
                    "(SELECT id FROM generate_series(1, " + maxId + ")\n" +
                    "AS s(id) EXCEPT SELECT id FROM comprador) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }
        
        return result;
    }
    
}
