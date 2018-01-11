package vrimplantacao2.dao.cadastro.cheque;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeVO;

/**
 *
 * @author Leandro
 */
public class ChequeAnteriorDAO {
    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_recebercheque (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	data date,\n" +
                    "	banco integer,\n" +
                    "	agencia varchar,\n" +
                    "	conta varchar,\n" +
                    "	cheque varchar,\n" +
                    "	valor numeric,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ");"
            );                    
        }
    }

    public MultiMap<String, ChequeAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        MultiMap<String, ChequeAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.sistema,\n" +
                    "	ant.loja,\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	ant.data,\n" +
                    "	ant.banco,\n" +
                    "	ant.agencia,\n" +
                    "	ant.conta,\n" +
                    "	ant.cheque,\n" +
                    "	ant.valor\n" +
                    "from \n" +
                    "	implantacao.codant_recebercheque ant\n" +
                    "where\n" +
                    "	ant.sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	ant.loja = " + SQLUtils.stringSQL(lojaOrigem) + "\n" +
                    "order by\n" +
                    "	ant.sistema,\n" +
                    "	ant.loja,\n" +
                    "	ant.id"
            )) {
                while (rst.next()) {
                    ChequeAnteriorVO vo = new ChequeAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        ChequeVO atual = new ChequeVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoatual(atual);
                    }
                    vo.setBanco(rst.getInt("banco"));
                    vo.setAgencia(rst.getString("agencia"));
                    vo.setConta(rst.getString("conta"));
                    vo.setCheque(rst.getString("cheque"));
                    vo.setValor(rst.getDouble("valor"));
                    result.put(
                            vo,
                            vo.getSistema(),
                            vo.getLoja(),
                            vo.getId()
                    );
                }
            }
        }
        return result;
    }

    public void gravarChequeAnterior(ChequeAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_recebercheque");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoatual() != null) {
                sql.put("codigoatual", anterior.getCodigoatual().getId());
            }
            sql.put("data", anterior.getData());
            sql.put("banco", anterior.getBanco());
            sql.put("agencia", anterior.getAgencia());
            sql.put("conta", anterior.getConta());
            sql.put("cheque", anterior.getCheque());
            sql.put("valor", anterior.getValor());
            stm.execute(sql.getInsert());
        }
    }
    
}
