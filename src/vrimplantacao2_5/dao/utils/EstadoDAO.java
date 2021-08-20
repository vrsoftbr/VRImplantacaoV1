package vrimplantacao2_5.dao.utils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2_5.vo.utils.EstadoVO;


/**
 *
 * @author Desenvolvimento
 */
public class EstadoDAO {

    public List<EstadoVO> getEstados() throws Exception {
        List<EstadoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "descricao\n"
                    + "from estado\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    EstadoVO vo = new EstadoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.add(vo);
                }
            }
        }
        return result;
    }
}
