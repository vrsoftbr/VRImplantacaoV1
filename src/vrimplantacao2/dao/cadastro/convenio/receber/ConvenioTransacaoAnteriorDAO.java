package vrimplantacao2.dao.cadastro.convenio.receber;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoVO;

/**
 *
 * @author Leandro
 */
public class ConvenioTransacaoAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_conveniotransacao (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	data date,\n" +
                    "	valor numeric(10,2),\n" +
                    "	pago boolean not null,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );                    
        }
    }

    public Map<String, ConvenioTransacaoAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        Map<String, ConvenioTransacaoAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	data,\n" +
                    "	valor,\n" +
                    "	pago\n" +
                    "from\n" +
                    "	implantacao.codant_conveniotransacao\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(lojaOrigem) + "\n" +
                    "order by\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoAnteriorVO ant = new ConvenioTransacaoAnteriorVO();
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        ConvenioTransacaoVO atual = new ConvenioTransacaoVO();
                        atual.setId(rst.getInt("codigoatual"));
                        ant.setCodigoAtual(atual);
                    }
                    ant.setData(rst.getDate("data"));
                    ant.setValor(rst.getDouble("valor"));
                    ant.setPago(rst.getBoolean("pago"));
                    result.put(
                            ant.getId(),
                            ant
                    );
                }
            }
        }
        
        return result;
    }

    public void gravarTransacaoAnterior(ConvenioTransacaoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("implantacao");
            sql.setTableName("codant_conveniotransacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("data", anterior.getData());
            sql.put("valor", anterior.getValor());
            sql.put("pago", anterior.isPago());
            
            stm.execute(sql.getInsert());
        }
    }
    
}
