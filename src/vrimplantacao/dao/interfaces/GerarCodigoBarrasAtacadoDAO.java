package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

public class GerarCodigoBarrasAtacadoDAO {

    public Map<Long, ProdutoVO> getCodigoBarrasAtacado() throws SQLException, Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        long codigoBarras;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, id_tipoembalagem, pesavel "
                    + "from produto "
            )) {
                while (rst.next()) {
                    if ((rst.getInt("id_tipoembalagem") == 4) || (rst.getInt("id") <= 9999)
                            || (rst.getBoolean("pesavel"))) {
                        codigoBarras = Long.parseLong("99999" + String.valueOf(rst.getInt("id")));
                    } else {
                        codigoBarras = Utils.gerarEan13((int) rst.getInt("id"), true);
                    }

                    ProdutoVO vo = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    vo.setId(rst.getInt("id"));
                    oAutomacao.setCodigoBarras(codigoBarras);
                    oAutomacao.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.vAutomacao.add(oAutomacao);
                    vResult.put(codigoBarras, vo);
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarrasAtacado(int qtdEmbalagem) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = getCodigoBarrasAtacado();
            ProgressBar.setMaximum(vCodigoBarra.size());
            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.adicionarCodigoBarrasAtacado(vProdutoNovo, qtdEmbalagem);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
