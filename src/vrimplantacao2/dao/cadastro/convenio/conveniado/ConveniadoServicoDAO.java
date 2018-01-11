package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoServicoVO;

/**
 *
 * @author Leandro
 */
public class ConveniadoServicoDAO {

    public void gravarConveniadoServico(ConveniadoServicoVO servico) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("conveniadoservico");
            sql.put("id_conveniado", servico.getId_conveniado());
            sql.put("id_tiposervicoconvenio", servico.getTipoServicoConvenio().getId());
            sql.put("valor", servico.getValor());
            sql.put("valordesconto", servico.getValorDesconto());
            stm.execute(sql.getInsert());
        }
    }
    
}
