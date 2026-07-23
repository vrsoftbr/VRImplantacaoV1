package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.database.SQLBuilder;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.reformatributaria.CstVO;

/**
 *
 * @author wesley
 */
public class CstDAO {
    
    public int insert(CstVO cst) throws Exception {
        SQLBuilder sql = new SQLBuilder("reformatributaria", "cst")
                .putSql("id", "(SELECT coalesce(max(id) + 1, 1) FROM reformatributaria.cst)")
                .put("cst", cst.getCst())
                .put("descricao", cst.getDescricao())
                .put("datainicio", cst.getDatainicio())
                .put("id_situacaocadastro", 1)
                .put("grupoibscbs", cst.isGrupoibscbs())
                .put("gruporeducao", cst.isGruporeducao())
                .put("grupodiferimento", cst.isGrupodiferimento())
                .put("grupotribregular", cst.isGrupotribregular())
                .put("grupoibscbsmono", cst.isGrupoibscbsmono())
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
