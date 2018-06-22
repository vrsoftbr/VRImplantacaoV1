package vrimplantacao2.dao.cadastro.cliente.food;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodVO;

/**
 *
 * @author Leandro
 */
public class ClienteFoodDAO {

    public IDStack getIds() throws Exception {
        IDStack result = new IDStack();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	id\n" +
                    "from\n" +
                    "	(SELECT id FROM generate_series(1, 999999) AS s(id)\n" +
                    "	EXCEPT SELECT id FROM food.cliente) AS codigointerno\n" +
                    "order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        
        return result;
    }

    public void atualizar(ClienteFoodVO vo, HashSet<OpcaoCliente> opt) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("food");
        sql.setTableName("cliente");
        sql.setWhere("id=" + vo.getId());
        if (opt.contains(OpcaoCliente.RAZAO) || opt.contains(OpcaoCliente.FANTASIA)) {
            sql.put("nome", vo.getNome());
        }
        if (opt.contains(OpcaoCliente.ENDERECO_COMPLETO)) {
            sql.put("endereco", vo.getEndereco());
            sql.put("numero", vo.getNumero());
            sql.put("bairro", vo.getBairro());
            sql.put("id_municipio", vo.getId_municipio());
        }
        if(opt.contains(OpcaoCliente.OBSERVACOES)) {
            sql.put("observacoes", "IMPORTADO VR " + vo.getObservacao());
        }
        if (opt.contains(OpcaoCliente.SITUACAO_CADASTRO)) {
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
        }
        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void gravar(ClienteFoodVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("food");
        sql.setTableName("cliente");
        sql.put("id", vo.getId());
        sql.put("nome", vo.getNome());
        sql.put("endereco", vo.getEndereco());
        sql.put("numero", vo.getNumero());
        sql.put("bairro", vo.getBairro());
        sql.put("id_municipio", vo.getId_municipio());
        sql.put("observacao", vo.getObservacao());
        sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getInsert());
        }
    }
    
}
