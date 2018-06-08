package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;

/**
 *
 * @author Leandro
 */
public final class PautaFiscalAnteriorDAO {
    
    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select table_schema||'.'||table_name tabela from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_pautafiscal'"
            )) {
                if (rst.next()) {
                    return;
                }
            }
            stm.execute(
                    "create table implantacao.codant_pautafiscal(\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual int,\n" +
                    "	cstdebito varchar,\n" +
                    "	cstcredito varchar,\n" +
                    "	iva numeric,\n" +
                    "	ivaajustado numeric,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }

    public PautaFiscalAnteriorDAO() throws Exception {
        createTable();
    }

    public Map<String, PautaFiscalAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, PautaFiscalAnteriorVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	cstdebito,\n" +
                    "	cstcredito,\n" +
                    "	iva,\n" +
                    "	ivaajustado\n" +
                    "from\n" +
                    "	implantacao.codant_pautafiscal\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(loja) + "\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    PautaFiscalAnteriorVO ant = new PautaFiscalAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        PautaFiscalVO vo = new PautaFiscalVO();
                        vo.setId(rst.getInt("codigoatual"));
                        ant.setCodigoAtual(vo);
                    }
                    ant.setCstDebito(rst.getString("cstdebito"));
                    ant.setCstCredito(rst.getString("cstcredito"));
                    ant.setIva(rst.getDouble("iva"));
                    ant.setIvaAjustado(rst.getDouble("ivaajustado"));
                    
                    result.put(ant.getId(), ant);
                }
            }
        }
        
        return result;
    }

    public void atualizar(PautaFiscalAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_pautafiscal");
            sql.setWhere(
                    "sistema = " + SQLUtils.stringSQL(anterior.getSistema()) + " and\n" +
                    "loja = " + SQLUtils.stringSQL(anterior.getLoja()) + " and\n" +
                    "id = " + SQLUtils.stringSQL(anterior.getId())
            );
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("cstdebito", anterior.getCstDebito());
            sql.put("cstcredito", anterior.getCstCredito());
            sql.put("iva", anterior.getIva());
            sql.put("ivaajustado", anterior.getIvaAjustado());
            if (!sql.isEmpty()) {
                stm.execute(
                        sql.getUpdate()
                );
            }
        }
    }

    public void gravarAnterior(PautaFiscalAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_pautafiscal");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("cstdebito", anterior.getCstDebito());
            sql.put("cstcredito", anterior.getCstCredito());
            sql.put("iva", anterior.getIva());
            sql.put("ivaajustado", anterior.getIvaAjustado());
            if (!sql.isEmpty()) {
                stm.execute(
                        sql.getInsert()
                );
            }
        }
    }
    
    
    
}
