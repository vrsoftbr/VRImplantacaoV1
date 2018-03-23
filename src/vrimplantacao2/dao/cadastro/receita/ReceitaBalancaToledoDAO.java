package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaToledoVO;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaToledoDAO {

    public IDStack getIdsVagos(int maxId) throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n" +
                    "(SELECT id FROM generate_series(1, " + maxId + ")\n" +
                    "AS s(id) EXCEPT SELECT id FROM comprador) AS receitatoledo ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }
        
        return result;
    }

    public void gravar(ReceitaBalancaToledoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("receitatoledo");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("observacao", vo.getObservacao());
            sql.put("receitalinha1", vo.getReceitaLinha1());
            sql.put("receitalinha2", vo.getReceitaLinha2());
            sql.put("receitalinha3", vo.getReceitaLinha3());
            sql.put("receitalinha4", vo.getReceitaLinha4());
            sql.put("receitalinha5", vo.getReceitaLinha5());
            sql.put("receitalinha6", vo.getReceitaLinha6());
            sql.put("receitalinha7", vo.getReceitaLinha7());
            sql.put("receitalinha8", vo.getReceitaLinha8());
            sql.put("receitalinha9", vo.getReceitaLinha9());
            sql.put("receitalinha10", vo.getReceitaLinha10());
            sql.put("receitalinha11", vo.getReceitaLinha11());
            sql.put("receitalinha12", vo.getReceitaLinha12());
            sql.put("receitalinha13", vo.getReceitaLinha13());
            sql.put("receitalinha14", vo.getReceitaLinha14());
            sql.put("receitalinha15", vo.getReceitaLinha15());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            
            stm.execute(sql.getInsert());
        }
    }
    
    public void gravarItem(int idReceita, int idProduto) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("receitatoledoproduto");
            sql.put("id_receitatoledo", idReceita);
            sql.put("id_produto", idProduto);
            
            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<Integer, Void> getReceitas() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_receitatoledo,\n" +
                    "	id_produto\n" +
                    "from\n" +
                    "	receitatoledoproduto"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_receitatoledo"), rst.getInt("id_produto"));
                }
            }
        }
        
        return result;
    }
    
}
