package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaAnteriorVO;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaAnteriorDAO {
    
    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_receitabalanca(\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatualfilizola integer,\n" +
                    "	codigoatualtoledo integer,\n" +
                    "	descricao varchar,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }

    public ReceitaBalancaAnteriorDAO() throws Exception {
        createTable();
    }

    public Map<String, ReceitaBalancaAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, ReceitaBalancaAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatualfilizola,\n" +
                    "	codigoatualtoledo,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	implantacao.codant_receitabalanca\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + loja + "'\n" +
                    "order by\n" +
                    "	1,2,3"
            )) {
                while (rst.next()) {
                    ReceitaBalancaAnteriorVO ant = new ReceitaBalancaAnteriorVO();                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setId(rst.getString("id"));
                    ant.setCodigoAtualFilizola(rst.getInt("codigoatualfilizola"));
                    ant.setCodigoAtualToledo(rst.getInt("codigoatualtoledo"));
                    ant.setDescricao(rst.getString("descricao"));                    
                    result.put(ant.getId(), ant);
                }
            }
        }
        
        return result;
    }

    public void gravar(ReceitaBalancaAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_receitabalanca");
            sql.put("sistema", vo.getSistema());
            sql.put("loja", vo.getLoja());
            sql.put("id", vo.getId());
            sql.put("codigoatualfilizola", vo.getCodigoAtualFilizola());
            sql.put("codigoatualtoledo", vo.getCodigoAtualToledo());
            sql.put("descricao", vo.getDescricao());
            stm.execute(sql.getInsert());            
        }
    }
    
}
