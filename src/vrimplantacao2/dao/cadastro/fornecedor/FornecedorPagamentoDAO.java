package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorPagamentoVO;

/**
 *
 * @author Leandro
 */
public class FornecedorPagamentoDAO {

    public void salvar(int id, int vencimento) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "	v_forn integer = " + id + ";\n" +
                    "	v_venc integer = " + vencimento + ";\n" +
                    "begin\n" +
                    "	if (not exists(select id from fornecedorpagamento where id_fornecedor = v_forn and vencimento = v_venc)) then\n" +
                    "		insert into fornecedorpagamento (id_fornecedor, vencimento) values (v_forn, v_venc);\n" +
                    "	end if;\n" +
                    "end;$$"
            );
        }    
    }
    
    public void salvar(FornecedorPagamentoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("fornecedorpagamento");
            sql.put("id_fornecedor", vo.getFornecedor().getId());
            sql.put("vencimento", vo.getVencimento());
            sql.getReturning().add("id");

            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }
    
    public MultiMap<String, Void> getPagamentos() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	id_fornecedor,\n" +
                    "	vencimento "+
                    "from \n" +
                    "	fornecedorpagamento\n" +
                    "order by\n" +
                    "	id_fornecedor"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_fornecedor"),
                            rst.getString("vencimento")
                    );
                }
            }
        }
        return result;
    }

    
}
