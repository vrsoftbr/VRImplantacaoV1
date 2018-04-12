package vrimplantacao.dao.financeiro.contareceber;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.ContaReceberAnteriorVO;

/**
 *
 * @author Leandro
 */
public class ContaReceberAnteriorDAO {
    
    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_contareceber (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	idfornecedor varchar,\n" +
                    "	idclienteEventual varchar,\n" +
                    "	data date,\n" +
                    "	vencimento date,\n" +
                    "	valor numeric,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }

    public ContaReceberAnteriorDAO() throws Exception {
        createTable();
    }

    public Map<String, ContaReceberAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, ContaReceberAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	idfornecedor,\n" +
                    "	idclienteeventual,\n" +
                    "	data,\n" +
                    "	vencimento,\n" +
                    "	valor\n" +
                    "from\n" +
                    "	implantacao.codant_contareceber\n" +
                    "where\n" +
                    "	sistema = '' and\n" +
                    "	loja = ''\n" +
                    "order by\n" +
                    "	1,2,3"
            )) {
                while (rst.next()) {
                    ContaReceberAnteriorVO ant = new ContaReceberAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setId(rst.getString("id"));
                    ant.setCodigoAtual(rst.getInt("codigoatual"));
                    ant.setIdFornecedor(rst.getString("idfornecedor"));
                    ant.setIdClienteEventual(rst.getString("idclienteeventual"));
                    ant.setData(rst.getDate("data"));
                    ant.setVencimento(rst.getDate("vencimento"));
                    ant.setValor(rst.getDouble("valor"));
                    
                    result.put(ant.getId(), ant);
                }
            }
        }
        
        return result;
    }

    public void gravar(ContaReceberAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_contareceber");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("codigoatual", anterior.getCodigoAtual());
            sql.put("idfornecedor", anterior.getIdFornecedor());
            sql.put("idclienteeventual", anterior.getIdClienteEventual());
            sql.put("data", anterior.getData());
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            
            stm.execute(sql.getInsert());
        }
    }
    
}
