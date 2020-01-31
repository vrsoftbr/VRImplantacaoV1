/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.produto2;

import java.sql.Statement;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.AtacadoProdutoComplementoVO;

/**
 *
 * @author Lucas
 */
public class AtacadoProdutoComplementoDAO {

    public void salvar(AtacadoProdutoComplementoVO vo, Set<OpcaoProduto> opt) throws Exception {

        if (opt.contains(OpcaoProduto.VR_ATACADO)) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(
                        "do $$\n"
                        + "declare \n"
                        + "     v_idproduto int = " + vo.getIdProduto() + ";\n"
                        + "     v_idloja int = " + vo.getIdLoja() + ";\n"
                        + "     v_precovenda numeric(11,2) = " + vo.getPrecoVenda() + ";\n"
                        + "begin \n"
                        + "     if (exists(select id from atacado.produtocomplemento where id_produto = v_idproduto and id_loja = v_idloja)) then\n"
                        + "         update atacado.produtocomplemento set precovenda = v_precovenda where id_produto = v_idproduto and id_loja = v_idloja;\n"
                        + "     else \n"
                        + "         insert into atacado.produtocomplemento(id_produto, id_loja, precovenda) \n"
                        + "         values \n"
                        + "         (v_idproduto, v_idloja, v_precovenda);"
                        + "     end if;\n"
                        + "end;\n"
                        + "$$;"
                );                
            }
        }
    }
}
