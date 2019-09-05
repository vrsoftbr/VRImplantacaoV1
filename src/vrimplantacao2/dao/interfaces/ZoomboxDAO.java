package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 *
 * @author Leandro
 */
public class ZoomboxDAO extends InterfaceDAO {
    
    private String complemento = "";
    private boolean importarClienteSemNome = false;

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public void setImportarClienteSemNome(boolean importarClienteSemNome) {
        this.importarClienteSemNome = importarClienteSemNome;
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Zoombox";
        } else {
            return "Zoombox - " + complemento;
        }
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, nome from loja order by id"
            )) {
                while (rst.next()) {                    
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.id,\n" +
                    "	c.cpfcliente cpf,\n" +
                    "	c.rg ie_rg,\n" +
                    "	coalesce(nullif(trim(c.razaosocial),''), c.nome) razaosocial,\n" +
                    "	c.nome fantasia,\n" +
                    "	c.ativo,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.estado uf,\n" +
                    "	c.cep,\n" +
                    "	ec.descricao estadocivil,\n" +
                    "	c.datacadastro,\n" +
                    "	c.nascimento,\n" +
                    "	coalesce(g.descricao, '') sexo,\n" +
                    "	c.observacao,\n" +
                    "	(\n" +
                    "		select \n" +
                    "			coalesce(sum(totaldisponivel), 0.00)::numeric(10,2) \n" +
                    "		 from\n" +
                    "			clientecredito\n" +
                    "		 where\n" +
                    "			idcliente = c.id\n" +
                    "		 and ativo\n" +
                    "	) limite\n" +
                    "from\n" +
                    "	cliente c\n" +
                    "	left join estadocivil ec on\n" +
                    "		ec.id = c.idestadocivil\n" +
                    "	left join genero g on\n" +
                    "		c.idgenero = g.id\n" +
                    (!importarClienteSemNome ? "where not nome is null\n" : "") +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setSexo(rst.getString("sexo").startsWith("F") ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    
                    getContatos(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void getContatos(ClienteIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	con.id,\n" +
                    "	con.idmeiocomunicacao,\n" +
                    "	mc.descricao meiocomunicacao,\n" +
                    "	con.descricao info\n" +
                    "from\n" +
                    "	clientemeiocomunicacao con\n" +
                    "	join meiocomunicacao mc on\n" +
                    "		con.idmeiocomunicacao = mc.id\n" +
                    "where\n" +
                    "	con.idcliente = " + imp.getId() + "\n" +
                    "order by\n" +
                    "	con.id"
            )) {
                while (rst.next()) {
                    switch (rst.getInt("idmeiocomunicacao")) {
                        case 1: 
                            imp.addTelefone("RESIDENCIAL", rst.getString("info")); 
                            imp.setTelefone(rst.getString("info"));
                            break;
                        case 2: 
                            imp.addCelular("CELULAR", rst.getString("info"));
                            imp.setCelular(rst.getString("info"));
                            break;
                        case 3: 
                            imp.addEmail(rst.getString("info"), TipoContato.COMERCIAL); 
                            imp.setEmail(rst.getString("info"));
                            break;
                    }
                }
            }
        }
    }
    
    
}
