package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaFilizolaVO;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaFilizolaDAO {

    public IDStack getIdsVagos(int maxId) throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n" +
                    "(SELECT id FROM generate_series(1, " + maxId + ")\n" +
                    "AS s(id) EXCEPT SELECT id FROM comprador) AS receitafilizola ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }
        
        return result;
    }

    public void gravar(ReceitaBalancaFilizolaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("receitafilizola");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("receita", vo.getReceita());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            
            stm.execute(sql.getInsert());
        }
    }

    public void gravarItem(int idReceita, int idProduto) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("receitafilizolaproduto");
            sql.put("id_receitafilizola", idReceita);
            sql.put("id_produto", idProduto);
            
            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<Integer, Void> getReceitas() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_receitafilizola,\n" +
                    "	id_produto\n" +
                    "from\n" +
                    "	receitafilizolaproduto"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_receitafilizola"), rst.getInt("id_produto"));
                }
            }
        }
        
        return result;
    }
    
}
