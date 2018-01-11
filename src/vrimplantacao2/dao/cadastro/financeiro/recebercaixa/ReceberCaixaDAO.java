package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.gui.component.mapatiporecebiveis.MapaTipoRecebivelDAO;
import vrimplantacao2.gui.component.mapatiporecebiveis.MapaTipoRecebivelVO;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;

/**
 * Classe que controla as operações na tabela recebercaixa.
 * @author Leandro
 */
public class ReceberCaixaDAO {
        
    private MapaTipoRecebivelDAO mapaTipoRecebivelDAO = new MapaTipoRecebivelDAO();

    public Map<String, Integer> getMapaTipoRecebivel(String sistema, String agrupador) throws Exception {
        
        List<MapaTipoRecebivelVO> mapa = mapaTipoRecebivelDAO.getMapa(sistema, agrupador);
        Map<String, Integer> result = new LinkedHashMap<>() ;
        
        for (MapaTipoRecebivelVO mp: mapa) {
            if (mp.getCodigoatual() != null) {
                result.put(mp.getId(), mp.getCodigoatual().getId());
            }
        }
        
        return result;
        
    }

    public void gravarRecebimentoCaixa(RecebimentoCaixaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("recebercaixa");
            sql.put("id_tiporecebivel", vo.getIdTipoRecebivel());
            sql.put("dataemissao", vo.getDataEmissao());
            sql.put("id_situacaorecebercaixa", vo.getSituacaoReceberCaixa().getId());
            sql.put("valor", vo.getValor());
            sql.put("observacao", vo.getObservacao());
            sql.put("id_tipolocalcobranca", vo.getIdTipoLocalCobranca());
            sql.put("id_tiporecebimento", vo.getIdTipoRecebimento());
            sql.put("datavencimento", vo.getDataVencimento());
            sql.put("id_loja", vo.getIdLoja());
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
            
        }
    }

    public void atualizarRecebimentoCaixa(RecebimentoCaixaVO vo, Set<OpcaoRecebimentoCaixa> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            if (!opt.isEmpty()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("recebercaixa"); 
                if (opt.contains(OpcaoRecebimentoCaixa.TIPO_RECEBIVEL)) {
                    sql.put("id_tiporecebivel", vo.getIdTipoRecebivel());
                }
                if (opt.contains(OpcaoRecebimentoCaixa.DATA_EMISSAO)) {
                    sql.put("dataemissao", vo.getDataEmissao());
                }
                //sql.put("id_situacaorecebercaixa", vo.getSituacaoReceberCaixa().getId());
                if (opt.contains(OpcaoRecebimentoCaixa.VALOR)) {
                    sql.put("valor", vo.getValor());
                }
                if (opt.contains(OpcaoRecebimentoCaixa.OBSERVACAO)) {
                    sql.put("observacao", vo.getObservacao());
                }
                //sql.put("id_tipolocalcobranca", vo.getIdTipoLocalCobranca());
                //sql.put("id_tiporecebimento", vo.getIdTipoRecebimento());
                if (opt.contains(OpcaoRecebimentoCaixa.DATA_VENCIMENTO)) {
                    sql.put("datavencimento", vo.getDataVencimento());
                }
                sql.setWhere("id = " + vo.getId());
                
                stm.execute(sql.getUpdate());
            
            }
        }
    }
    
}
