package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;

public class FornecedorContatoDAO {    
    
    public void salvar(FornecedorContatoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("fornecedorcontato");
            sql.put("id_fornecedor", vo.getFornecedor().getId());
            sql.put("nome", vo.getNome());
            sql.put("telefone", vo.getTelefone());
            sql.put("id_tipocontato", vo.getTipoContato().getId());
            sql.put("email", vo.getEmail());
            sql.put("celular", vo.getCelular());
            sql.getReturning().add("id");

            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }

    public MultiMap<String, Void> getContatos() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	id_fornecedor,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	email,\n" +
                    "	celular\n" +
                    "from \n" +
                    "	fornecedorcontato\n" +
                    "order by\n" +
                    "	id_fornecedor,\n" +
                    "	nome"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_fornecedor"),
                            rst.getString("nome"),
                            rst.getString("telefone"),
                            rst.getString("email"),
                            rst.getString("celular")
                    );
                }
            }
        }
        return result;
    }
    
}
