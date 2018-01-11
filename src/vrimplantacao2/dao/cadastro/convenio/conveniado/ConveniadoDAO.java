package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoVO;

/**
 *
 * @author Leandro
 */
public class ConveniadoDAO {

    public Set<Long> getCnpjCadastrado() throws Exception {
        Set<Long> result = new LinkedHashSet<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cnpj from conveniado"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("cnpj"));
                }
            }
        }
        
        return result;
    }

    public void gravarConveniado(ConveniadoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("conveniado");
            sql.put("id", vo.getId());
            sql.put("nome", vo.getNome());
            sql.put("id_empresa", vo.getId_empresa());
            sql.put("bloqueado", vo.isBloqueado());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            sql.put("senha", 0);
            sql.put("id_loja", vo.getId_loja());
            sql.put("cnpj", vo.getCnpj());
            sql.put("observacao", vo.getObservacao());
            sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());
            sql.put("matricula", vo.getMatricula());
            sql.put("datavalidadecartao", vo.getDataValidadeCartao());
            sql.put("datadesbloqueio", vo.getDataDesbloqueio());
            sql.put("visualizasaldo", vo.isVisualizaSaldo());
            sql.put("databloqueio", vo.getDataBloqueio());
            stm.execute(sql.getInsert());
        }
    }
    
}
