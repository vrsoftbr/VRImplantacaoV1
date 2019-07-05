package vrimplantacao2.dao.cadastro.convenio.empresa;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;

/**
 *
 * @author Leandro
 */
public class EmpresaAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_convenioempresa(\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	cnpj varchar,\n" +
                    "	razao varchar,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }

    public Map<String, ConvenioEmpresaAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        return getAnteriores(sistema, lojaOrigem, false);
    }
    
    public Map<String, ConvenioEmpresaAnteriorVO> getAnteriores(String sistema, String lojaOrigem, boolean apenasExistentes) throws Exception {
        Map<String, ConvenioEmpresaAnteriorVO> result = new HashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	cnpj,\n" +
                    "	razao\n" +
                    "from\n" +
                    "	implantacao.codant_convenioempresa\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(lojaOrigem) + "\n" +
                    (apenasExistentes ? "	and not codigoatual is null\n" : "") +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaAnteriorVO vo = new ConvenioEmpresaAnteriorVO();
                    
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    vo.setCodigoAtual(rst.getInt("codigoatual"));
                    vo.setCnpj(rst.getString("cnpj"));
                    vo.setRazao(rst.getString("razao"));
                    
                    result.put(rst.getString("id"), vo);
                }
            }
        }
        return result;
    }

    public void gravarEmpresaAnterior(ConvenioEmpresaAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_convenioempresa");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("codigoatual", anterior.getCodigoAtual());
            sql.put("cnpj", anterior.getCnpj());
            sql.put("razao", anterior.getRazao());
            stm.execute(
                    sql.getInsert()
            );                    
        }
    }
    
}
