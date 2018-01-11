package vrimplantacao2.dao.cadastro.convenio.receber;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoVO;

/**
 *
 * @author Leandro
 */
public class ConvenioTransacaoDAO {

    public void gravarTransacao(ConvenioTransacaoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("conveniadotransacao");
            sql.put("id_conveniado", vo.getId_conveniado());
            sql.put("ecf", vo.getEcf());
            sql.put("numerocupom", vo.getNumeroCupom());
            sql.put("datahora", vo.getDataHora());
            sql.put("id_loja", vo.getId_loja());
            sql.put("valor", vo.getValor());
            sql.put("id_situacaotransacaoconveniado", vo.getSituacaoTransacaoConveniado().getId());
            sql.put("lancamentomanual", vo.isLancamentoManual());
            sql.put("matricula", vo.getMatricula());
            sql.put("datamovimento", vo.getDataMovimento());
            sql.put("finalizado", vo.isFinalizado());
            sql.put("id_tiposervicoconvenio", vo.getTipoServicoConvenio().getId());
            sql.put("observacao", vo.getObservacao());
            sql.getReturning().add("id");
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                vo.setId(rst.getInt("id"));
            }
        }
    }
    
}
