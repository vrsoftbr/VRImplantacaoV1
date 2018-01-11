package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoVO;

/**
 *
 * @author Leandro
 */
public class ConveniadoAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_conveniado (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	cnpj varchar,\n" +
                    "	razao varchar,\n" +
                    "	lojacadastro varchar,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );
        }
    }

    public MultiMap<String, ConveniadoAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        MultiMap<String, ConveniadoAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	cnpj,\n" +
                    "	razao,\n" +
                    "	lojacadastro\n" +
                    "from\n" +
                    "	implantacao.codant_conveniado\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(lojaOrigem) + "\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ConveniadoAnteriorVO vo = new ConveniadoAnteriorVO();
                    
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        ConveniadoVO atual = new ConveniadoVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(atual);
                    }
                    vo.setCnpj(rst.getString("cnpj"));
                    vo.setRazao(rst.getString("razao"));
                    vo.setLojaCadastro(rst.getString("lojacadastro"));
                    
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

    public void gravarConveniadoAnterior(ConveniadoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_conveniado");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("cnpj", anterior.getCnpj());
            sql.put("razao", anterior.getRazao());
            sql.put("lojacadastro", anterior.getLojaCadastro());
            stm.execute(
                    sql.getInsert()
            );                    
        }
    }
    
}
