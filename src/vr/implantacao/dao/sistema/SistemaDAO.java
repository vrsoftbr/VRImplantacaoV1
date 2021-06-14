package vr.implantacao.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vr.implantacao.vo.cadastro.SistemaVO;
import vrframework.classe.Conexao;

/**
 *
 * @author guilhermegomes
 */
public class SistemaDAO {
    
    public List getSistema() throws Exception {
        List<SistemaVO> result = new ArrayList<>();
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	*\n" +
                    "from \n" +
                    "	implantacao2_5.sistema\n" +
                    "order by \n" +
                    "	nome")) {
                while(rs.next()) {
                    SistemaVO sistemaVO = new SistemaVO();
                    
                    sistemaVO.setId(rs.getInt("id"));
                    sistemaVO.setNome(rs.getString("nome"));
                    
                    result.add(sistemaVO);
                }
            }
        }
        
        return result;
    }
}
