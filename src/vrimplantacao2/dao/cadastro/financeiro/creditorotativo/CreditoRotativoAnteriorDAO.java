package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoAnteriorDAO {
    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                "create table if not exists implantacao.codant_recebercreditorotativo(\n" +
                "	sistema varchar not null,\n" +
                "	loja varchar not null,\n" +
                "	id varchar not null,\n" +
                "	id_cliente varchar not null,\n" +
                "	codigoatual integer,\n" +
                "	vencimento date,\n" +
                "	valor numeric(10,4),\n" +
                "	primary key (sistema, loja, id)\n" +
                ");"
            );
        }
    }

    public Map<String, CreditoRotativoAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, CreditoRotativoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id_cliente,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	vencimento,\n" +
                    "	valor\n" +
                    "from \n" +
                    "	implantacao.codant_recebercreditorotativo\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(loja) + "\n" +
                    "order by\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id_cliente,\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    CreditoRotativoAnteriorVO vo = new CreditoRotativoAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdCliente(rst.getString("id_cliente"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        CreditoRotativoVO atual = new CreditoRotativoVO();
                        atual.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(atual);
                    }
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

    public void gravarRotativoAnterior(CreditoRotativoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_recebercreditorotativo");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id_cliente", anterior.getIdCliente());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            stm.execute(sql.getInsert());
        }
    }

    public Map<String, CreditoRotativoAnteriorVO> getTodoCreditoRotativoAnterior() throws Exception {
        Map<String, CreditoRotativoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.sistema,\n" +
                    "	ant.loja,\n" +
                    "	ant.id_cliente,\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	ant.vencimento,\n" +
                    "	ant.valor,\n" +
                    "	r.id_loja a_id_loja,\n" +
                    "	r.valor a_valor,\n" +
                    "	r.valormulta a_multa,\n" +
                    "	r.valorjuros a_juros,\n" +
                    "	r.dataemissao a_dataemissao,\n" +
                    "	r.datavencimento a_datavencimento\n" +
                    "from \n" +
                    "	implantacao.codant_recebercreditorotativo ant\n" +
                    "	left join recebercreditorotativo r on\n" +
                    "		ant.codigoatual = r.id\n" +
                    "order by\n" +
                    "	r.dataemissao"
            )) {
                while (rst.next()) {
                    CreditoRotativoAnteriorVO vo = new CreditoRotativoAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdCliente(rst.getString("id_cliente"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        CreditoRotativoVO atual = new CreditoRotativoVO();
                        atual.setId(rst.getInt("codigoatual"));
                        atual.setId_loja(rst.getInt("a_id_loja"));
                        atual.setValor(rst.getDouble("a_valor"));
                        atual.setValorMulta(rst.getDouble("a_multa"));
                        atual.setValorJuros(rst.getDouble("a_juros"));
                        atual.setDataEmissao(rst.getDate("a_dataemissao"));
                        atual.setDataVencimento(rst.getDate("a_datavencimento"));
                        vo.setCodigoAtual(atual);
                    }
                    vo.setVencimento(rst.getDate("vencimento"));
                    vo.setValor(rst.getDouble("valor"));
                    result.put(
                            String.format(
                                    "%s-%s-%s",
                                    vo.getSistema(),
                                    vo.getLoja(),
                                    vo.getId()
                            ),
                            vo
                    );
                }
            }
        }
        return result;
    }
    
}
