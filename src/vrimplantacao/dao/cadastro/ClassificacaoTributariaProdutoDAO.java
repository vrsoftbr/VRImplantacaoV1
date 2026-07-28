package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.database.SQLBuilder;
import vrframework.classe.Conexao;

/**
 *
 * @author wesley
 */
public class ClassificacaoTributariaProdutoDAO {
    
    public int insert(int idProduto, int idClassificacaoTributaria, int idLoja) throws Exception {
        SQLBuilder sql = new SQLBuilder("reformatributaria", "classificacaotributariaproduto")
                .putSql("id", "(SELECT coalesce(max(id) + 1, 1) FROM reformatributaria.classificacaotributariaproduto)")
                .put("id_classificacao", idClassificacaoTributaria)
                .put("id_produto", idProduto)
                .put("id_loja", idLoja)
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
