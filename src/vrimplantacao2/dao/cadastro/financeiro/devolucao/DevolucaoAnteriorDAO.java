package vrimplantacao2.dao.cadastro.financeiro.devolucao;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoAnteriorVO;

/**
 *
 * @author Wesley
 */
public class DevolucaoAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_devolucao(\n"
                    + "	sistema varchar not null,\n"
                    + "	loja varchar not null,\n"
                    + "	id varchar not null,\n"
                    + "	id_fornecedor varchar not null,\n"
                    + "	codigoatual integer,\n"
                    + "	numero_nota varchar(25),\n"
                    + "	emissao date,\n"
                    + "	vencimento date,\n"
                    + "	valor numeric(10,4),\n"
                    + "	primary key (sistema, loja, id)\n"
                    + ");"
            );
        }
    }

    public Map<String, DevolucaoAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, DevolucaoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	sistema,\n"
                    + "	loja,\n"
                    + "	id_fornecedor,\n"
                    + "	id,\n"
                    + "	codigoatual,\n"
                    + "	numero_nota,\n"
                    + "	emissao,\n"
                    + " vencimento, \n"
                    + "	valor\n"
                    + "from \n"
                    + "	implantacao.codant_devolucao \n"
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
                    DevolucaoAnteriorVO vo = new DevolucaoAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdFornecedor(rst.getString("id_fornecedor"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        DevolucaoVO atual = new DevolucaoVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(atual);
                    }
                    vo.setNumeroNota(rst.getString("numero_nota"));
                    vo.setEmissao(rst.getDate("emissao"));
                    vo.setVencimento(rst.getDate("vencimento"));
                    vo.setValor(rst.getDouble("valor"));
                    result.put(
                            vo.getId(),
                            vo
                    );
                }
            }
        }
        return result;
    }

    public void gravarDevolucaoAnterior(DevolucaoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_devolucao");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("id_fornecedor", anterior.getIdFornecedor());
            sql.put("numero_nota", anterior.getNumeroNota());

            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("emissao", anterior.getEmissao());
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            stm.execute(sql.getInsert());
        }
    }
}
