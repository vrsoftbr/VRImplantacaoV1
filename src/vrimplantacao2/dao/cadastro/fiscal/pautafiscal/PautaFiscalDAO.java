package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.enums.NcmVO;
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
            
            if (opt.contains(OpcaoFiscal.ALIQUOTA_CREDITO)) {
                sql.put("id_aliquotaCredito", vo.getId_aliquotaCredito());
            }
            if (opt.contains(OpcaoFiscal.ALIQUOTA_CREDITO_FORA_ESTADO)) {
                sql.put("id_aliquotaCreditoForaEstado", vo.getId_aliquotaCreditoForaEstado());
            }
            if (opt.contains(OpcaoFiscal.ALIQUOTA_DEBITO)) {
                sql.put("id_aliquotaDebito", vo.getId_aliquotaDebito());
            }
            if (opt.contains(OpcaoFiscal.ALIQUOTA_DEBITO_FORA_ESTADO)) {
                sql.put("id_aliquotaDebitoForaEstado", vo.getId_aliquotaDebitoForaEstado());
            }
            if (opt.contains(OpcaoFiscal.IVA)) {
                sql.put("iva", vo.getIva());
            }
            if (opt.contains(OpcaoFiscal.IVA_AJUSTADO)) {
                sql.put("ivaAjustado", vo.getIvaAjustado());
            }
            if (opt.contains(OpcaoFiscal.TIPO_IVA)) {
                sql.put("tipoIva", vo.getTipoIva().getId());
            }
            
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
            if (vo.getExcecao() < 0) {
                sql.putSql("excecao", "(select coalesce(max(excecao) + 1, 1) from pautafiscal where ncm1 = " + vo.getNcm1() + " and ncm2 = " + vo.getNcm2() + " and ncm3 = " + vo.getNcm3() + ")");
            } else {
                sql.put("excecao", vo.getExcecao());
            }
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

    public Map<String, ProdutoPautaVO> getNcmsProduto(String sistema, String loja, int idLojaVR ) throws Exception {
        Map<String, ProdutoPautaVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.impid,\n" +
                    "	p.ncm1,\n" +
                    "	p.ncm2,\n" +
                    "	p.ncm3,\n" +
                    "	pa.id_aliquotadebito,\n" +
                    "	pa.id_aliquotadebitoforaestado,\n" +
                    "	pa.id_aliquotacredito,\n" +
                    "	pa.id_aliquotacreditoforaestado\n" +
                    "from\n" +
                    "	implantacao.codant_produto ant\n" +
                    "	join produto p on\n" +
                    "		ant.codigoatual = p.id\n" +
                    "	join produtoaliquota pa on\n" +
                    "		pa.id_produto = p.id\n" +
                    "		and pa.id_estado = (select f.id_estado from loja l join fornecedor f on l.id_fornecedor = f.id where l.id = " + idLojaVR + ")\n" +
                    "where\n" +
                    "	ant.impsistema = '" + sistema + "'\n" +
                    "	and ant.imploja = '" + loja + "'\n" +
                    "order by\n" +
                    "	ant.impid"
            )) {
                while (rst.next()) {
                    ProdutoPautaVO vo = new ProdutoPautaVO();
                    NcmVO ncm = new NcmVO();
                    
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    
                    vo.setNcm(ncm);
                    vo.setId_aliquotaCredito(rst.getInt("id_aliquotacredito"));
                    vo.setId_aliquotaCreditoForaEstado(rst.getInt("id_aliquotacreditoforaestado"));
                    vo.setId_aliquotaDebito(rst.getInt("id_aliquotadebito"));
                    vo.setId_aliquotaDebitoForaEstado(rst.getInt("id_aliquotadebitoforaestado"));
                    
                    result.put(rst.getString("impid"), vo);
                }
            }
        }
        
        return result;
    }

    public Map<Long, ProdutoPautaVO> getNcmsProduto(int idLojaVR) throws Exception {
        Map<Long, ProdutoPautaVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ean.codigobarras,\n" +
                    "	p.ncm1,\n" +
                    "	p.ncm2,\n" +
                    "	p.ncm3,\n" +
                    "	pa.id_aliquotadebito,\n" +
                    "	pa.id_aliquotadebitoforaestado,\n" +
                    "	pa.id_aliquotacredito,\n" +
                    "	pa.id_aliquotacreditoforaestado\n" +
                    "from\n" +
                    "	produtoautomacao ean\n" +
                    "	join produto p on\n" +
                    "		ean.id_produto = p.id\n" +
                    "	join produtoaliquota pa on\n" +
                    "		pa.id_produto = p.id\n" +
                    "		and pa.id_estado = (select f.id_estado from loja l join fornecedor f on l.id_fornecedor = l.id where l.id = " + idLojaVR + ")\n" +
                    "order by\n" +
                    "	ean.codigobarras"
            )) {
                while (rst.next()) {
                    ProdutoPautaVO vo = new ProdutoPautaVO();
                    NcmVO ncm = new NcmVO();
                    
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    
                    vo.setNcm(ncm);
                    vo.setId_aliquotaCredito(rst.getInt("id_aliquotacredito"));
                    vo.setId_aliquotaCreditoForaEstado(rst.getInt("id_aliquotacreditoforaestado"));
                    vo.setId_aliquotaDebito(rst.getInt("id_aliquotadebito"));
                    vo.setId_aliquotaDebitoForaEstado(rst.getInt("id_aliquotadebitoforaestado"));
                    
                    result.put(rst.getLong("codigobarras"), vo);
                }
            }
        }
        
        return result;
    }
    
}
