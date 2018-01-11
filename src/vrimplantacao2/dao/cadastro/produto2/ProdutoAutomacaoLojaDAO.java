package vrimplantacao2.dao.cadastro.produto2;

import java.sql.Statement;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoLojaVO;

/**
 *
 * @author Leandro
 */
public class ProdutoAutomacaoLojaDAO {

    public void salvar(ProdutoAutomacaoLojaVO precoAtacadoLoja, Set<OpcaoProduto> opt) throws Exception {
        if (opt.contains(OpcaoProduto.ATACADO)) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(
                        "do $$\n" +
                        "declare\n" +
                        "	v_ean bigint = " + precoAtacadoLoja.getCodigoBarras() + ";\n" +
                        "	v_id_loja integer = " + precoAtacadoLoja.getId_loja() + ";\n" +
                        "	v_preco numeric(10,2) = " + precoAtacadoLoja.getPrecoVenda() + ";\n" +
                        "begin\n" +
                        "	if (exists(select id from produtoautomacaoloja where codigobarras = v_ean and id_loja = v_id_loja)) then\n" +
                        "		update produtoautomacaoloja set precovenda = v_preco where codigobarras = v_ean and id_loja = v_id_loja;\n" +
                        "	else\n" +
                        "		insert into produtoautomacaoloja (codigobarras, id_loja, precovenda) values (v_ean, v_id_loja, v_preco);\n" +
                        "	end if;\n" +
                        "end;\n" +
                        "$$;"
                );
            }
        }
    }
    
}
