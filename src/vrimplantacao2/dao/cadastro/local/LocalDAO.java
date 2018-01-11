package vrimplantacao2.dao.cadastro.local;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.local.EstadoVO;

/**
 *
 * @author Leandro
 */
public class LocalDAO {
    
    public Map<String, EstadoVO> getEstados() throws Exception {
        Map<String, EstadoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, sigla, descricao from estado order by sigla"
            )) {
                while (rst.next()) {
                    EstadoVO vo = new EstadoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setSigla(rst.getString("sigla"));                    
                    result.put(vo.getSigla(), vo);
                }            
            }
        }
        
        return result;
    }
    
}
