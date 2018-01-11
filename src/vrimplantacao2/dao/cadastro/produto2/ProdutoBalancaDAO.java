package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;

/**
 * DAO do produto balanca.
 * @author Leandro
 */
public class ProdutoBalancaDAO {

    public Map<Integer, ProdutoBalancaVO> getProdutosBalanca() throws Exception {
        Map<Integer, ProdutoBalancaVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from implantacao.produtobalanca order by codigo"
            )) {
                while (rst.next()) {
                    ProdutoBalancaVO vo = new ProdutoBalancaVO();
                    
                    vo.setCodigo(rst.getInt("codigo"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setPesavel(rst.getString("pesavel"));
                    vo.setValidade(rst.getInt("validade"));
                    
                    result.put(vo.getCodigo(), vo);
                }
            }
        }
        return result;
    }
    
}
