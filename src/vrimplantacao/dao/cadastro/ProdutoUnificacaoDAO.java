package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.ProdutosUnificacaoVO;

public class ProdutoUnificacaoDAO {

    public Map<Double, ProdutosUnificacaoVO> carregarUnificados() throws Exception{
        Map<Double, ProdutosUnificacaoVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select "
                        + "codigoanterior, "
                        + "codigoatual, "
                        + "barras, "
                        + "descricaoantiga, "
                        + "existe "
                + "from implantacao.produtos_unificacao "
                        + "order by codigoanterior"
            )) {
                while (rst.next()) {
                    ProdutosUnificacaoVO uni = new ProdutosUnificacaoVO();
                    uni.codigoanterior = rst.getDouble("codigoanterior");
                    uni.codigoatual = rst.getInt("codigoatual");
                    uni.barras = rst.getLong("barras");
                    uni.descricao = rst.getString("descricaoantiga");
                    uni.existe = rst.getBoolean("existe");
                    result.put(uni.codigoanterior, uni);
                }
            }
        }
        return result;
    }

    
    
}
