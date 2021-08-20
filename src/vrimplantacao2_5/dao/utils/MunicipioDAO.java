package vrimplantacao2_5.dao.utils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2_5.vo.utils.MunicipioVO;

/**
 *
 * @author Desenvolvimento
 */
public class MunicipioDAO {

    public List<MunicipioVO> getMunicipios(int idEstado) throws Exception {
        List<MunicipioVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id, \n"
                    + "descricao \n"
                    + "from municipio \n"
                    + "where id_estado = " + idEstado + "\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    MunicipioVO vo = new MunicipioVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.add(vo);
                }
            }
        }
        return result;
    }
}
