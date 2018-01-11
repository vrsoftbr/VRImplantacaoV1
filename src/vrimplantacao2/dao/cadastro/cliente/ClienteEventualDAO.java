package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;

/**
 * Dao para gravar o cliente eventual.
 * @author Leandro
 */
public class ClienteEventualDAO {

    public void salvar(ClienteEventualVO cliente) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("clienteeventual");
            sql.put("id", cliente.getId());
            sql.put("nome", cliente.getNome());
            sql.put("endereco", cliente.getEndereco());
            sql.put("bairro", cliente.getBairro());
            sql.put("id_estado", cliente.getId_estado());
            sql.put("telefone", cliente.getTelefone());
            sql.put("id_tipoInscricao", cliente.getTipoInscricao().getId());
            sql.put("inscricaoestadual", cliente.getInscricaoEstadual());
            sql.put("id_situacaoCadastro", cliente.getSituacaoCadastro().getId());
            sql.put("fax", cliente.getFax());
            sql.put("enderecoCobranca", cliente.getEnderecoCobranca());
            sql.put("bairroCobranca", cliente.getBairroCobranca());
            sql.put("id_estadoCobranca", cliente.getId_estadoCobranca());
            sql.put("telefoneCobranca", cliente.getTelefoneCobranca());
            sql.put("prazoPagamento", cliente.getPrazoPagamento());
            sql.put("id_tipoOrgaoPublico", cliente.getTipoOrgaoPublico().getIndex());
            sql.put("dataCadastro", cliente.getDataCadastro());
            sql.put("limiteCompra", cliente.getLimiteCompra());
            sql.put("cobraTaxaNotaFiscal", cliente.isCobraTaxaNotaFiscal());
            sql.put("id_municipio", cliente.getId_municipio());
            sql.put("id_municipioCobranca", cliente.getId_municipioCobranca());
            sql.put("cep", cliente.getCep());
            sql.put("cnpj", cliente.getCnpj());
            sql.put("cepCobranca", cliente.getCepCobranca());
            sql.put("id_tiporecebimento", cliente.getId_tiporecebimento());
            sql.put("bloqueado", cliente.isBloqueado());
            sql.put("numero", cliente.getNumero());
            sql.put("observacao", cliente.getObservacao());
            sql.put("id_pais", cliente.getId_pais());
            sql.put("inscricaoMunicipal", cliente.getInscricaoMunicipal());
            if (cliente.getId_contaContabilFiscalPassivo() > 0) {
                sql.put("id_contaContabilFiscalPassivo", cliente.getId_contaContabilFiscalPassivo());
            }
            sql.put("numeroCobranca", cliente.getNumeroCobranca());
            sql.put("complemento", cliente.getComplemento());
            sql.put("complementoCobranca", cliente.getComplementoCobranca());
            if (cliente.getId_contaContabilFiscalAtivo() > 0) {
                sql.put("id_contaContabilFiscalAtivo", cliente.getId_contaContabilFiscalAtivo());
            }
            sql.put("id_tipoIndicadorIe", cliente.getTipoIndicadorIe().getId());
            sql.put("id_classeRisco", cliente.getId_classeRisco());
            
            stm.execute(sql.getInsert());
        }
    }

    /**
     * Returna os CNPJs cadastrados nos clientes eventuais;
     * @return
     * @throws Exception 
     */
    public Map<Long, Integer> getCnpjCadastrados() throws Exception {
        Map<Long, Integer> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, cnpj from clienteeventual;"
            )) {
                while (rst.next()) {
                    result.put(rst.getLong("cnpj"), rst.getInt("id"));
                }
            }
        }
        return result;
    }

    public void apagarContatos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "delete from clienteeventualcontato;\n" +
                    "alter sequence clienteeventualcontato_id_seq restart with 1;"
            );
        }
    }

    public void apagarTudo() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "delete from clienteeventualcontato;\n" +
                    "delete from clienteeventual;\n" +
                    "drop table if exists implantacao.codant_clienteeventual;\n" +
                    "alter sequence clienteeventualcontato_id_seq restart with 1;\n" +
                    "alter sequence clienteeventual_id_seq restart with 1;"
            );
        }
    }

    public Map<Long, ClienteEventualVO> getClientesPorCnpj() throws Exception {
        Map<Long, ClienteEventualVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.id,\n" +
                    "	c.nome,\n" +
                    "	c.cnpj,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.id_municipio,\n" +
                    "	c.id_estado,\n" +
                    "	c.cep\n" +
                    "from\n" +
                    "	clienteeventual c\n" +
                    "order by\n" +
                    "	c.id"
            )) {
                while (rst.next()) {
                    ClienteEventualVO vo = new ClienteEventualVO();
                    
                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    vo.setCnpj(rst.getLong("cnpj"));
                    vo.setEndereco(rst.getString("endereco"));
                    vo.setNumero(rst.getString("numero"));
                    vo.setComplemento(rst.getString("complemento"));
                    vo.setBairro(rst.getString("bairro"));
                    vo.setId_municipio(rst.getInt("id_municipio"));
                    vo.setId_estado(rst.getInt("id_estado"));
                    vo.setCep(rst.getInt("cep"));
                    
                    result.put(vo.getCnpj(), vo);
                }
            }
        }
        
        return result;
    }
    
    
    
}
