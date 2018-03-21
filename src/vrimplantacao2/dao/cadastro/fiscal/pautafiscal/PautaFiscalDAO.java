package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;

/**
 *
 * @author Leandro
 */
public class PautaFiscalDAO {

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

    public Map<String, Integer> getPautaExcecao(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        PautaFiscalAnteriorDAO.createTable();
        
        try (Statement stm = Conexao.createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.id,\n" +
                    "	pf.excecao,\n" +
                    "	lpad(pf.ncm1::varchar,4,'0') || lpad(pf.ncm2::varchar,2,'0') || lpad(pf.ncm3::varchar,2,'0') ncm\n" +
                    "from\n" +
                    "	implantacao.codant_pautafiscal ant\n" +
                    "	join pautafiscal pf on\n" +
                    "		ant.codigoatual = pf.id\n" +
                    "where\n" +
                    "	ant.sistema = '" + sistema + "' and\n" +
                    "	ant.loja = '" + loja + "'\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getInt("excecao"));
                }
            }
        }
        
        return result;
    }
    
}
