package vrimplantacao2.dao.cadastro.financeiro.verbas;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasAnteriorVO;

/**
 *
 * @author Wesley
 */
public class VerbasAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_verbas(\n"
                    + "	sistema varchar not null,\n"
                    + "	loja varchar not null,\n"
                    + "	id varchar not null,\n"
                    + "	id_fornecedor varchar not null,\n"
                    + "	codigoatual integer,\n"
                    + "	emissao date,\n"
                    + "	vencimento date,\n"
                    + "	valor numeric(10,4),\n"
                    + "	codigo_atual_vencimento integer, \n"
                    + "	primary key (sistema, loja, id)\n"
                    + ");"
            );
        }
    }

    public Map<String, VerbasAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, VerbasAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	sistema,\n"
                    + "	loja,\n"
                    + "	id_fornecedor,\n"
                    + "	id,\n"
                    + "	codigoatual,\n"
                    + "	emissao,\n"
                    + " vencimento, \n"
                    + "	valor, \n"
                    + " codigo_atual_vencimento \n"
                    + "from \n"
                    + "	implantacao.codant_verbas \n"
                    + "where\n"
                    + "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	loja = " + SQLUtils.stringSQL(loja) + "\n"
                    + "order by\n"
                    + "	sistema,\n"
                    + "	loja,\n"
                    + "	id_fornecedor,\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    VerbasAnteriorVO vo = new VerbasAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdFornecedor(rst.getString("id_fornecedor"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        VerbasVO atual = new VerbasVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(atual);
                    }
                    vo.setEmissao(rst.getDate("emissao"));
                    vo.setVencimento(rst.getDate("vencimento"));
                    vo.setValor(rst.getDouble("valor"));
                    result.put(
                            vo.getId(),
                            vo
                    );
                    vo.setCodigoAtualVencimento(rst.getInt("codigo_atual_vencimento"));
                }
            }
        }
        return result;
    }

    public void gravarVerbasAnterior(VerbasAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_verbas");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("id_fornecedor", anterior.getIdFornecedor());

            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("emissao", anterior.getEmissao());
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            sql.put("codigo_atual_vencimento", anterior.getCodigoAtualVencimento());
            stm.execute(sql.getInsert());
        }
    }
}
