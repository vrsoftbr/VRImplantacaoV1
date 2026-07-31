package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.database.SQLBuilder;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.reformatributaria.ClassificacaoTributariaNcmVO;

/**
 *
 * @author wesley
 */
public class ClassificacaoTributariaNcmDAO {
    
    public int insert(ClassificacaoTributariaNcmVO classificacaoTributariaNcm) throws Exception {
        SQLBuilder sql = new SQLBuilder("reformatributaria", "classificacaotributariancm")
                .putSql("id", "(SELECT coalesce(max(id) + 1, 1) FROM reformatributaria.classificacaotributariancm)")
                .put("id_classificacao", classificacaoTributariaNcm.getIdClassificacao())
                .put("id_ncm", classificacaoTributariaNcm.getIdNcm())
                .put("ncm1", classificacaoTributariaNcm.getNcm1())
                .put("ncm2", classificacaoTributariaNcm.getNcm2())
                .put("ncm3", classificacaoTributariaNcm.getNcm3())
                .put("id_loja", classificacaoTributariaNcm.getIdLoja())
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
