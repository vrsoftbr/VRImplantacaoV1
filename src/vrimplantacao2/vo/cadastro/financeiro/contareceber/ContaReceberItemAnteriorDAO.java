package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Leandro
 */
public class ContaReceberItemAnteriorDAO {

    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_contareceberitem (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	idcontareceber varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	data date,\n" +
                    "	valor numeric,\n" +
                    "	primary key (sistema, loja, idcontareceber, id)\n" +
                    ");"
            );
        }
    }

    public ContaReceberItemAnteriorDAO() throws Exception {
        createTable();
    }
    
    public void gravar(ContaReceberItemAnteriorVO ant) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("implantacao");
            sql.setTableName("codant_contareceberitem");
            sql.put("sistema", ant.getSistema());
            sql.put("loja", ant.getLoja());
            sql.put("idcontareceber", ant.getIdContaReceber());
            sql.put("id", ant.getId());
            sql.put("codigoatual", ant.getCodigoAtual());
            sql.put("data", ant.getData());
            sql.put("valor", ant.getValor());
            
            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<String, ContaReceberItemAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        MultiMap<String, ContaReceberItemAnteriorVO> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.sistema,\n" +
                    "	ant.loja,\n" +
                    "	ant.idcontareceber,\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	ant.data,\n" +
                    "	ant.valor\n" +
                    "from\n" +
                    "	implantacao.codant_contareceberitem ant\n" +
                    "where\n" +
                    "	ant.sistema = '" + sistema + "' and\n" +
                    "	ant.loja = '" + loja + "'\n" +
                    "order by\n" +
                    "	1,2,3,4"
            )) {
                while (rst.next()) {
                    ContaReceberItemAnteriorVO ant = new ContaReceberItemAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setIdContaReceber(rst.getString("idcontareceber"));
                    ant.setId(rst.getString("id"));
                    ant.setCodigoAtual(rst.getInt("codigoatual"));
                    ant.setData(rst.getDate("data"));
                    ant.setValor(rst.getDouble("valor"));
                    
                    result.put(ant, ant.getIdContaReceber(), ant.getId());
                }
            }
        }
        
        return result;
    }
    
}
