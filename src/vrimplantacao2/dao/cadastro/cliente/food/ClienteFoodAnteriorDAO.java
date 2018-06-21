package vrimplantacao2.dao.cadastro.cliente.food;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodAnteriorVO;

/**
 * DAO que gerencia as operações na tabela implantacao.codant_food.
 * @author Leandro
 */
public class ClienteFoodAnteriorDAO {
    
    public ClienteFoodAnteriorDAO() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            boolean existe;
            try (ResultSet rst = stm.executeQuery(
                    "select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_clientefood'"
            )) {
                existe = rst.next();
            }            
            if (!existe) {
                stm.execute(
                    "create table implantacao.codant_clientefood (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	nome varchar,\n" +
                    "	forcargravacao boolean not null,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
                );
            }
        }
    }

    public Map<String, ClienteFoodAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        Map<String, ClienteFoodAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	codigoatual,\n" +
                "	nome,\n" +
                "	forcargravacao\n" +
                "from\n" +
                "	implantacao.codant_clientefood ant\n" +
                "where\n" +
                "	ant.sistema = '" + sistema + "' and\n" +
                "	ant.loja = '" + lojaOrigem + "'\n" +
                "order by\n" +
                "	id"
            )) {
                while (rst.next()) {
                    ClienteFoodAnteriorVO ant = new ClienteFoodAnteriorVO();
                    
                    ant.setSistema(sistema);
                    ant.setLoja(lojaOrigem);
                    ant.setId(rst.getString("id"));
                    ant.setCodigoAtual(rst.getInt("codigoatual"));
                    ant.setNome(rst.getString("nome"));
                    ant.setForcarGravacao(rst.getBoolean("forcargravacao"));
                    
                    result.put(ant.getId(), ant);
                }
            }
        }
        
        return result;
    }

    public void gravar(ClienteFoodAnteriorVO anterior) throws Exception {
        
        SQLBuilder builder = new SQLBuilder();
        
        builder.setSchema("implantacao");
        builder.setTableName("codant_clientefood");
        builder.put("sistema", anterior.getSistema());
        builder.put("loja", anterior.getLoja());
        builder.put("id", anterior.getId());
        builder.put("codigoatual", anterior.getCodigoAtual(), 0);
        builder.put("nome", anterior.getNome());
        builder.put("forcargravacao", anterior.isForcarGravacao());
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(builder.getInsert());
        }
        
    }

    public void atualizar(ClienteFoodAnteriorVO anterior) throws Exception {
        
        SQLBuilder builder = new SQLBuilder();
        
        builder.setSchema("implantacao");
        builder.setTableName("codant_clientefood");
        builder.put("codigoatual", anterior.getCodigoAtual(), 0);
        builder.put("nome", anterior.getNome());
        builder.put("forcargravacao", anterior.isForcarGravacao());
        builder.setWhere("sistema='" + anterior.getSistema() + "' and loja='" + anterior.getLoja() + "' and id='" + anterior.getId() + "'");
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(builder.getUpdate());
        }
        
    }
    
}
