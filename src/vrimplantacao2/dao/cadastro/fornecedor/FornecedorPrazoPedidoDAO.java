package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Leandro
 */
public class FornecedorPrazoPedidoDAO {

    public void salvarTodasLojas(int idFornecedor, int diasPrazoPedido) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "	v_forn integer = " + idFornecedor + ";\n" +
                    "	v_diasentregapedido integer = " + diasPrazoPedido + ";\n" +
                    "	v_diasatualizapedidoparcial integer = 5;\n" +
                    "	r record;\n" +
                    "begin\n" +
                    "	for r in select id from loja order by id\n" +
                    "	loop\n" +
                    "		if (not exists(select id from fornecedorprazopedido where \n" +
                    "			id_fornecedor = v_forn and \n" +
                    "			id_loja = r.id and \n" +
                    "			diasentregapedido = v_diasentregapedido and\n" +
                    "			diasatualizapedidoparcial = v_diasatualizapedidoparcial)) then\n" +
                    "				insert into fornecedorprazopedido (id_fornecedor, id_loja, diasentregapedido, diasatualizapedidoparcial)\n" +
                    "				values (v_forn, r.id, v_diasentregapedido, v_diasatualizapedidoparcial);\n" +
                    "		end if;\n" +
                    "	end loop;\n" +
                    "end;\n" +
                    "$$;"
            );
        }
    }

    
    
}
