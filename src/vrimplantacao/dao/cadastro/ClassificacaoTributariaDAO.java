package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.database.SQLBuilder;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.reformatributaria.ClassificacaoTributariaVO;

/**
 *
 * @author wesley
 */
public class ClassificacaoTributariaDAO {
    
    public int insert(ClassificacaoTributariaVO classificacaoTributaria) throws Exception {
        SQLBuilder sql = new SQLBuilder("reformatributaria", "classificacaotributaria")
                .putSql("id", "(SELECT coalesce(max(id) + 1, 1) FROM reformatributaria.classificacaotributaria)")
                .put("reducao", classificacaoTributaria.getReducao())
                .put("diferimento", classificacaoTributaria.getDiferimento())
                .put("id_cstibscbs", classificacaoTributaria.getIdCstIbsCbs())
                .put("cclasstrib", classificacaoTributaria.getCclasstrib())
                .put("descricao", classificacaoTributaria.getDescricao())
                .put("fundamentacaolegal", classificacaoTributaria.getFundamentacaolegal())
                .put("aliquotazero", classificacaoTributaria.isAliquotazero())
                .put("id_situacaocadastro", classificacaoTributaria.getId_situacaocadastro())
                .returning("id");
        try (
                Statement st = Conexao.createStatement();
                ResultSet rs = st.executeQuery(
                        sql.insert()
                )
        ) {
            rs.next();
            return rs.getInt("id");
                
        }
    }
}
