package vrimplantacao2.dao.cadastro.cliente;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualEnderecoVO;

/**
 *
 * @author Wesley
 */
class ClienteEventualEnderecoDAO {

    public void salvar(ClienteEventualEnderecoVO endereco) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("clienteeventualendereco");
            sql.put("id_clienteeventual", endereco.getClienteEventual().getId());
            sql.put("id_tipoendereco", endereco.getTipo_endereco());
            sql.put("endereco", endereco.getEndereco());
            sql.put("numero", endereco.getNumero());
            sql.put("bairro", endereco.getBairro());
            sql.put("complemento", endereco.getComplemento());
            sql.put("cep", endereco.getCep());
            sql.put("id_municipio", endereco.getId_municipio());
            sql.put("id_estado", endereco.getId_estado());
            sql.put("id_pais", endereco.getId_pais());
            sql.put("inscricaoestadual", endereco.getInscricao_estadual());
            sql.put("inscricaomunicipal", endereco.getInscricao_municipal());
            sql.put("id_tipoindicadorie", endereco.getId_tipoindicadorie());
            sql.put("telefone", endereco.getTelefone());
            
            stm.executeUpdate(sql.getInsert());
        }
    }
}
