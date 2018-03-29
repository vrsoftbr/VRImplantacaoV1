package vrimplantacao.dao.financeiro.contareceber;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OutraReceitaItemVO;

/**
 *
 * @author Leandro
 */
public class OutraReceitaItemDAO {

    public void gravar(OutraReceitaItemVO item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("receberoutrasreceitasitem");
            sql.put("id_receberoutrasreceitas", item.getIdReceberOutrasReceitas());
            sql.put("valor", item.getValor());
            sql.put("valordesconto", item.getValorDesconto());
            sql.put("valorjuros", item.getValorJuros());
            sql.put("valormulta", item.getValorMulta());
            sql.put("valortotal", item.getValorTotal());
            sql.put("databaixa", item.getDataBaixa());
            sql.put("datapagamento", item.getDataPagamento());
            sql.put("observacao", item.getObservacao());
            sql.put("id_banco", item.getIdBanco(), 0);
            sql.put("agencia", item.getAgencia());
            sql.put("conta", item.getConta());
            sql.put("id_tiporecebimento", item.getTipoRecebimento().getId());
            sql.put("id_conciliacaobancarialancamento", item.getIdConciliacaoBancariaLancamento(), 0);
            sql.put("id_recebercheque", item.getIdReceberCheque(), 0);
            sql.put("id_usuario", item.getIdUsuario());
            sql.put("id_loja", item.getIdLoja());
            sql.getReturning().add("id");
            
            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                if (rst.next()) {
                    item.setId(rst.getInt("id"));
                }
            }
        }
    }
    
}
