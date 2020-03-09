package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 * DAO de importação do Milênio.
 * @author leandro
 */
public class MilenioDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(this.complemento)) {
            return "Milenio";
        } else {
            return "Milenio - " + this.complemento;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select lojcod, LOJFAN, LOJCGC, LOJEST from loja order by lojcod"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString("lojcod"),
                            String.format(
                                    "%s - %s",
                                    rs.getString("LOJFAN"),
                                    rs.getString("LOJCGC")
                            )
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<OpcaoProduto>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA_PRODUTO
        ));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	A.SECCOD,\n" +
                    "	A.SECDES,\n" +
                    "	B.GRPCOD,\n" +
                    "	B.GRPDES,\n" +
                    "	C.SBGCOD,\n" +
                    "	C.SBGDES\n" +
                    "from\n" +
                    "	SECAO A\n" +
                    "inner join GRUPO B on\n" +
                    "	B.SECCOD = A.SECCOD\n" +
                    "inner join SUBGRUPO C on\n" +
                    "	C.SECCOD = A.SECCOD\n" +
                    "	and C.GRPCOD = B.GRPCOD\n" +
                    "order by\n" +
                    "	A.SECCOD,\n" +
                    "	B.GRPCOD,\n" +
                    "	C.SBGCOD"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("SECCOD"));
                    imp.setMerc1Descricao(rs.getString("SECDES"));
                    imp.setMerc2ID(rs.getString("GRPCOD"));
                    imp.setMerc2Descricao(rs.getString("GRPDES"));
                    imp.setMerc3ID(rs.getString("SBGCOD"));
                    imp.setMerc3Descricao(rs.getString("SBGDES"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	PROCOD id,\n" +
                    "	prodes descricao\n" +
                    "from\n" +
                    "	produto\n" +
                    "where\n" +
                    "	procod in (\n" +
                    "	select\n" +
                    "		procod\n" +
                    "	from\n" +
                    "		referencia\n" +
                    "	group by\n" +
                    "		PROCOD\n" +
                    "	having\n" +
                    "		COUNT(*) > 1)\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rs.getString(""),
                                    rs.getString(""),
                                    rs.getInt(""),
                                    rs.getDouble(""),
                                    rs.getDouble("")
                            )
                    );
                }
            }
        }        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            Map<String, List<FornecedorContatoIMP>> contatos = new HashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	c.AGECOD id_fornecedor,\n" +
                    "	c.CNTNOM nome,\n" +
                    "	c.CNTTEL telefone,\n" +
                    "	c.CNTCEL celular,\n" +
                    "	c.CNTMAIL email\n" +
                    "from\n" +
                    "	contato c\n" +
                    "	join agente a on\n" +
                    "		c.AGECOD = a.AGECOD\n" +
                    "	join FORNECEDOR F on\n" +
                    "		f.AGECOD = a.AGECOD\n" +
                    "order by\n" +
                    "   id_fornecedor"
            )) {
                while (rs.next()) {
                    List<FornecedorContatoIMP> list = contatos.get(rs.getString("id_fornecedor"));
                    if (list == null) {
                        list = new ArrayList<>();
                        contatos.put(rs.getString("id_fornecedor"), list);
                    }
                    FornecedorContatoIMP imp = new FornecedorContatoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId("CONT" + (list.size() + 1));                    
                    imp.setImportFornecedorId(rs.getString("id_fornecedor")); 
                    imp.setNome(rs.getString("nome"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setTipoContato(TipoContato.COMERCIAL);
                    imp.setEmail(rs.getString("email"));
                    
                    list.add(imp);
                }
            }
            
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n" +
                    "	f.FORCOD id,\n" +
                    "	a.AGECOD,\n" +
                    "	a.AGEDES razao,\n" +
                    "	a.AGEFAN fantasia,\n" +
                    "	a.AGECGCCPF cnpj,\n" +
                    "	a.AGECGFRG ierg,\n" +
                    "	a.AGETEL1 fone,\n" +
                    "	a.AGEEND endereco,\n" +
                    "	a.AGENUM numero,\n" +
                    "	a.AGECPL complemento,\n" +
                    "	a.AGEBAI bairro,\n" +
                    "	a.AGECID municipio,\n" +
                    "	a.AGEEST uf,\n" +
                    "	a.AGECEP cep,\n" +
                    "	a.AGETEL1 tel1,\n" +
                    "	a.AGETEL2 tel2,\n" +
                    "	a.AGEFAX fax,\n" +
                    "	a.AGEATA atacado,\n" +
                    "	a.AGEDATCAD datacadastro,\n" +
                    "	a.AGEDATBLO databloqueio,\n" +
                    "	a.AGEDATALT dataalteracao,\n" +
                    "	a.AGEINSMUN inscricaomunicipal,\n" +
                    "	a.AGECTRICMS tributadoicms,\n" +
                    "	a.AGEOBS observacoes,\n" +
                    "	f.FORPRZENT prazoentrega,\n" +
                    "	f.FORPRZ prazopagamento,\n" +
                    "	(SELECT TOP (1) CNTMAIL FROM dbo.CONTATO AS C WHERE (C.AGECOD = F.AGECOD) AND CNTMAIL IS NOT NULL) AS EMAIL\n" +
                    "from\n" +
                    "	FORNECEDOR F\n" +
                    "INNER JOIN AGENTE A ON\n" +
                    "	A.AGECOD = F.AGECOD\n" +
                    "where\n" +
                    "	AGECGCCPF is not null\n" +
                    "order by\n" +
                    "	FORCOD"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ierg"));
                    imp.setTel_principal(rs.getString("fone"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.addTelefone("TEL 1", rs.getString("tel1"));
                    imp.addTelefone("TEL 2", rs.getString("tel2"));
                    imp.addTelefone("FAX", rs.getString("fax"));
                    //imp.setTipoFornecedor("S".equals(rs.getString("atacado")) ? TipoFornecedor.ATACADO);
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    //imp.set(rs.getString("databloqueio"));
                    //imp.set(rs.getString("dataalteracao"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    //imp.set(rs.getString("tributadoicms"));
                    imp.setObservacao(rs.getString("observacoes"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setPrazoPedido(rs.getInt("prazopagamento"));
                    
                    List<FornecedorContatoIMP> get = contatos.get(rs.getString("agecod"));
                    if (get != null) {                    
                        for (FornecedorContatoIMP cont: get) {
                            imp.addContato(
                                    cont.getNome(),
                                    cont.getTelefone(),
                                    cont.getCelular(),
                                    cont.getTipoContato(),
                                    cont.getEmail()
                            );
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
