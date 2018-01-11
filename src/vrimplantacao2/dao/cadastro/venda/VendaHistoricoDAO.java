package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 * Responsável por executar as operações de gravação de venda.
 * @author Leandro
 */
public class VendaHistoricoDAO {
    
    /**
     * Grava um registro de venda no histórico do VR.
     * @param vo Histórico de venda a ser gravado.
     * @throws Exception 
     */
    public void salvar(VendaHistoricoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("venda");

            sql.put("id_loja", vo.getId_loja());
            sql.put("id_produto", vo.getId_produto());
            sql.put("data", vo.getData());
            sql.put("precovenda", vo.getPrecoVenda());
            sql.put("quantidade", vo.getQuantidade());
            sql.put("id_comprador", vo.getId_comprador());
            sql.put("custocomimposto", vo.getCustoComImposto());
            sql.put("piscofins", vo.getPisCofins());
            sql.put("operacional", vo.getOperacional());
            sql.put("icmscredito", vo.getIcmsCredito());
            sql.put("icmsdebito", vo.getIcmsDebito());
            sql.put("valortotal", vo.getValorTotal());
            sql.put("custosemimposto", vo.getCustoSemImposto());
            sql.put("oferta", vo.isOferta());
            sql.put("perda", vo.getPerda());
            sql.put("customediosemimposto", vo.getCustoMedioSemImposto());
            sql.put("customediocomimposto", vo.getCustoMedioComImposto());
            sql.put("piscofinscredito", vo.getPisCofinsCredito());
            sql.put("cupomfiscal", vo.isCupomfiscal());
            sql.getReturning().add("id");
            
            try {
                try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
                )) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }
            } catch (Exception e) {
                sql.setFormatarSQL(true);
                System.out.println(sql.getInsert());
                throw e;
            }
        }
    }
    
}
