package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;

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

    public MultiMap<String, Void> getDivisoes(int idLojaVR) throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	id_fornecedor,\n"
                    + "	id_divisaofornecedor "
                    + "from \n"
                    + "	fornecedorprazo\n"
                    + "where \n"
                    + "id_loja = " + idLojaVR
                    + " order by\n"
                    + "	id_fornecedor"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_fornecedor"),
                            rst.getString("id_divisaofornecedor")
                    );
                }
            }
        }
        return result;
    }
    
}
