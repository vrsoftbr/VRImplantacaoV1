package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;

/**
 *
 * @author Leandro
 */
class PautaFiscalDAO {

    public void atualizar(PautaFiscalVO vo, Set<OpcaoFiscal> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("pautafiscal");
            sql.setWhere("id = " + vo.getId());
            
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void gravar(PautaFiscalVO vo, Set<OpcaoFiscal> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("pautafiscal");
            sql.getReturning().add("id");
            sql.getReturning().add("excecao");
                        
            sql.put("ncm1", vo.getNcm1());
            sql.put("ncm2", vo.getNcm2());
            sql.put("ncm3", vo.getNcm3());
            sql.putSql("excecao", "(select coalesce(max(excecao) + 1, 1) from pautafiscal where ncm1 = " + vo.getNcm1() + " and ncm2 = " + vo.getNcm2() + " and ncm3 = " + vo.getNcm3() + ")");
            sql.put("id_estado", vo.getId_estado());
            sql.put("iva", vo.getIva());
            sql.put("tipoIva", vo.getTipoIva().getId());
            sql.put("id_aliquotaCredito", vo.getId_aliquotaCredito());
            sql.put("id_aliquotaDebito", vo.getId_aliquotaDebito());
            sql.put("id_aliquotaDebitoForaEstado", vo.getId_aliquotaDebitoForaEstado());
            sql.put("ivaAjustado", vo.getIvaAjustado());
            sql.put("icmsRecolhidoAntecipadamente", vo.isIcmsRecolhidoAntecipadamente());
            sql.put("id_aliquotaCreditoForaEstado", vo.getId_aliquotaCreditoForaEstado());
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                vo.setId(rst.getInt("id"));
                vo.setExcecao(rst.getInt("excecao"));
            }
        }
    }
    
}
