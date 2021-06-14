package vr.implantacao.dao.bancodados;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vr.implantacao.vo.cadastro.BancoDadosVO;
import vrframework.classe.Conexao;

/**
 *
 * @author guilhermegomes
 */
public class BancoDadosDAO {
    
    public List getBancoDadosPorSistema(int idSistema) throws Exception {
        List<BancoDadosVO> result = new ArrayList<>();
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	s.id id_sistema,\n" +
                    "	s.nome sistema,\n" +
                    "	b.id id_banco,\n" +
                    "	b.nome bancodados\n" +
                    "from \n" +
                    "	implantacao2_5.sistemabancodados sb\n" +
                    "join implantacao2_5.sistema s on sb.id_sistema = s.id \n" +
                    "join implantacao2_5.bancodados b on sb.id_bancodados = b.id\n" +
                    "where \n" +
                    "	s.id = " + idSistema + " order by b.nome")) {
                while(rs.next()) {
                    BancoDadosVO bdVO = new BancoDadosVO();
                    
                    bdVO.setId(rs.getInt("id_banco"));
                    bdVO.setNome(rs.getString("bancodados"));
                    
                    result.add(bdVO);
                }
            }
        }
        
        return result;
    }    
}
