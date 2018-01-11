package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Leandro
 */
class FornecedorPrazoDAO {

    public void salvar(int lojaVR, int idFornecedor, int id_divisao, int prazoEntrega, int prazoVisita, int prazoSeguranca) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "	vIdForn integer = " + idFornecedor + ";\n" +
                    "	vIdLoja integer = " + lojaVR + ";\n" +
                    "	vIdDivisao integer = " + id_divisao + ";\n" +
                    "	vEntrega integer = " + prazoEntrega + ";\n" +
                    "	vVisita integer = " + prazoVisita + ";\n" +
                    "	vSeguranca integer = " + prazoSeguranca + ";\n" +
                    "begin\n" +
                    "	if (exists(select id from fornecedorprazo where id_fornecedor = vIdForn and id_loja = vIdLoja and id_divisaofornecedor = vIdDivisao)) then\n" +
                    "		update fornecedorprazo set \n" +
                    "			prazoentrega = vEntrega, \n" +
                    "			prazovisita = vVisita, \n" +
                    "			prazoseguranca = vSeguranca \n" +
                    "		where \n" +
                    "			id_fornecedor = vIdForn \n" +
                    "			and id_loja = vIdLoja \n" +
                    "			and id_divisaofornecedor = vIdDivisao;\n" +
                    "	else\n" +
                    "		insert into fornecedorprazo (\n" +
                    "			id_fornecedor,\n" +
                    "			id_loja,\n" +
                    "			id_divisaofornecedor,\n" +
                    "			prazoentrega,\n" +
                    "			prazovisita,\n" +
                    "			prazoseguranca\n" +
                    "		) values (\n" +
                    "			vIdForn,\n" +
                    "			vIdLoja,\n" +
                    "			vIdDivisao,\n" +
                    "			vEntrega,\n" +
                    "			vVisita,\n" +
                    "			vSeguranca\n" +
                    "		);\n" +
                    "	end if;\n" +
                    "end;\n" +
                    "$$;"
            );
        }
    }
    
}
