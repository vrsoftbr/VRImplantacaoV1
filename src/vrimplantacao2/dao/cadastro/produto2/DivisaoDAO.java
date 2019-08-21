package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import static java.util.Map.Entry;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.divisao.DivisaoFornecedorVO;

/**
 *
 * @author Leandro
 */
public class DivisaoDAO {
    
    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_divisao (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	descricao varchar,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ")"
            );
        }
    }

    public DivisaoDAO() throws Exception {
        createTable();
    }

    /**
     * Retorna as divisões anteriores.
     * @param sistema
     * @param loja
     * @return
     * @throws Exception 
     */
    public Map<String, Entry<String, Integer>> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, Entry<String, Integer>> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_divisao\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + loja + "'\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), new AbstractMap.SimpleEntry<>(
                            rst.getString("id"),
                            rst.getInt("codigoatual")
                    ));
                }
            }
        }
        
        return result;
    }

    public void salvar(DivisaoFornecedorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("public");
            sql.setTableName("divisaofornecedor");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            
            stm.execute(sql.getInsert());
        }
    }

    public void salvar(String sistema, String lojaOrigem, Entry<String, Integer> anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_divisao");
            sql.put("sistema", sistema);
            sql.put("loja", lojaOrigem);
            sql.put("id", anterior.getKey());
            sql.put("codigoatual", anterior.getValue());
            
            stm.execute(sql.getInsert());
        }
    }

    public IDStack getIdsVagos() throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	id\n" +
                    "from\n" +
                    "	(\n" +
                    "		SELECT\n" +
                    "			id\n" +
                    "		FROM\n" +
                    "			generate_series(1, 9999) AS s(id)\n" +
                    "		EXCEPT\n" +
                    "	 	SELECT\n" +
                    "			id\n" +
                    "		FROM\n" +
                    "			divisaofornecedor\n" +
                    ") AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }
        
        return result;
    }
    
}
