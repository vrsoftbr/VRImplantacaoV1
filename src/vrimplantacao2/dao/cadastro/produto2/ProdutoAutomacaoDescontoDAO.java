package vrimplantacao2.dao.cadastro.produto2;

import java.sql.Statement;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoDescontoVO;

/**
 *
 * @author Leandro
 */
public class ProdutoAutomacaoDescontoDAO {

    public void salvar(ProdutoAutomacaoDescontoVO precoAtacadoDesconto, Set<OpcaoProduto> opt) throws Exception {
        if (opt.contains(OpcaoProduto.ATACADO)) {
            try (Statement stm = Conexao.createStatement()) {
                System.out.println("CodigoBarras " + precoAtacadoDesconto.getCodigoBarras() + " Loja " + precoAtacadoDesconto.getId_loja() + " Desconto " + precoAtacadoDesconto.getDesconto());
                stm.execute(
                        "do $$\n" +
                        "declare\n" +
                        "	v_ean bigint = " + precoAtacadoDesconto.getCodigoBarras() + ";\n" +
                        "	v_id_loja integer = " + precoAtacadoDesconto.getId_loja() + ";\n" +
                        "	v_desconto numeric(10,2) = " + precoAtacadoDesconto.getDesconto() + ";\n" +
                        "begin\n" +
                        "	if (exists(select id from produtoautomacaodesconto where codigobarras = v_ean and id_loja = v_id_loja)) then\n" +
                        "		update produtoautomacaodesconto set desconto = v_desconto where codigobarras = v_ean and id_loja = v_id_loja;\n" +
                        "	else\n" +
                        "		insert into produtoautomacaodesconto (codigobarras, id_loja, desconto) values (v_ean, v_id_loja, v_desconto);\n" +
                        "	end if;\n" +
                        "end;\n" +
                        "$$;"
                );
            }
        }
    }
    
}
