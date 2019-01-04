package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
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

    public MultiMap<String, Integer> getContatos() throws Exception {
        MultiMap<String, Integer> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
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
                            rst.getInt("id"), 
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

    public void atualizar(FornecedorContatoVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("fornecedorcontato");
            if (opt.contains(OpcaoFornecedor.CONTATO_NOME)) {
                sql.put("nome", vo.getNome());
            }
            if (opt.contains(OpcaoFornecedor.TELEFONE)) {
                sql.put("telefone", vo.getTelefone());
            }
            if (opt.contains(OpcaoFornecedor.TIPO_CONTATO)) {
                sql.put("id_tipocontato", vo.getTipoContato().getId());
            }
            if (opt.contains(OpcaoFornecedor.EMAIL)) {
                sql.put("email", vo.getEmail());
            }
            if (opt.contains(OpcaoFornecedor.CELULAR)) {
                sql.put("celular", vo.getCelular());
            }
            sql.setWhere("id = " + vo.getId());
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
        }
    }
    
}
