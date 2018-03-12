package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;

/**
 * DAO para manipular a tabela implantacao.codant_nutricional.
 * @author Leandro
 */
public class NutricionalAnteriorDAO {

    public NutricionalAnteriorDAO() throws Exception {
        createTable();
    }

    /**
     * Cria a tabela implantacao.codant_nutricional no banco.
     * @throws Exception 
     */
    public final void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_nutricional (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatualfilizola integer,\n" +
                    "	codigoatualtoledo integer,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }
    
    /**
     * Retorna um {@link Map} com os códigos anteriores dos nutricionais.
     * @param sistema Sistema importado.
     * @param loja Loja importada.
     * @return {@link Map} com os códigos anteriores importados;
     * @throws Exception 
     */
    public Map<String, NutricionalAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, NutricionalAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatualfilizola,\n" +
                    "	codigoatualtoledo\n" +
                    "from\n" +
                    "	implantacao.codant_nutricional\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(loja) + "\n" +
                    "order by 1,2,3"
            )) {
                while (rst.next()) {
                    NutricionalAnteriorVO ant = new NutricionalAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setId(rst.getString("id"));
                    ant.setCodigoAtualFilizola((Integer) rst.getObject("codigoatualfilizola"));
                    ant.setCodigoAtualFilizola((Integer) rst.getObject("codigoatualtoledo"));
                    
                    result.put(rst.getString("id"), ant);
                }
            }
        }
        
        return result;
    }

    /**
     * Grava um {@link NutricionalAnteriorVO} no banco de dados.
     * @param anterior {@link NutricionalAnteriorVO} a ser gravado.
     * @throws Exception 
     */
    public void gravar(NutricionalAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("implantacao");
            sql.setTableName("codant_nutricional");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("codigoatualfilizola", anterior.getCodigoAtualFilizola());
            sql.put("codigoatualtoledo", anterior.getCodigoAtualToledo());
            
            stm.execute(sql.getInsert());
        }
    }
    
}
