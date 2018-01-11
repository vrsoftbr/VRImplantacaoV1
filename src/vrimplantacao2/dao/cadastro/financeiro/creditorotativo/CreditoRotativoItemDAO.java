package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoItemDAO {

    public void gravarRotativoItem(CreditoRotativoItemVO item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("recebercreditorotativoitem");
            sql.put("id_recebercreditorotativo", item.getId_receberCreditoRotativo());
            sql.put("valor", item.getValor());
            sql.put("valordesconto", item.getValorDesconto());
            sql.put("valormulta", item.getValorMulta());
            sql.put("valortotal", item.getValorTotal());
            sql.put("databaixa", item.getDatabaixa());
            sql.put("datapagamento", item.getDataPagamento());
            sql.put("observacao", item.getObservacao());
            sql.put("id_banco", item.getId_banco(), 0);
            sql.put("agencia", item.getAgencia());
            sql.put("conta", item.getConta());
            sql.put("id_tiporecebimento", item.getId_tipoRecebimento());
            sql.put("id_usuario", item.getId_usuario());
            sql.put("id_loja", item.getId_loja());
            sql.put("id_recebercheque", item.getId_receberCheque(), 0);
            sql.put("id_recebercaixa", item.getId_receberCaixa(), 0);
            sql.put("id_conciliacaobancarialancamento", item.getId_conciliacaoBancariaLancamento(), 0);
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                item.setId(rst.getInt("id"));
            }
        }
    }

    public Map<Integer, Double> getBaixas() throws Exception {
        Map<Integer, Double> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ri.id_recebercreditorotativo,\n" +
                    "	coalesce(sum(ri.valortotal),0) total\n" +
                    "from \n" +
                    "	recebercreditorotativoitem ri\n" +
                    "group by\n" +
                    "	ri.id"
            )) {
                while (rst.next()) {
                    result.put(rst.getInt("id_recebercreditorotativo"), rst.getDouble("total"));
                }
            }
        }
        
        return result;
    }
    
}
