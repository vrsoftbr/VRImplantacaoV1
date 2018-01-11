package vrimplantacao2.dao.cadastro.convenio.empresa;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaVO;

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

    public MultiMap<String, ConvenioEmpresaAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        return getAnteriores(sistema, lojaOrigem, false);
    }
    
    public MultiMap<String, ConvenioEmpresaAnteriorVO> getAnteriores(String sistema, String lojaOrigem, boolean apenasExistentes) throws Exception {
        MultiMap<String, ConvenioEmpresaAnteriorVO> result = new MultiMap<>();
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
                    if (rst.getString("codigoatual") != null) {
                        ConvenioEmpresaVO atual = new ConvenioEmpresaVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(atual);
                    }
                    vo.setCnpj(rst.getString("cnpj"));
                    vo.setRazao(rst.getString("razao"));
                    
                    result.put(
                            vo,
                            rst.getString("sistema"),
                            rst.getString("loja"),
                            rst.getString("id")
                    );
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
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("cnpj", anterior.getCnpj());
            sql.put("razao", anterior.getRazao());
            stm.execute(
                    sql.getInsert()
            );                    
        }
    }
    
}
