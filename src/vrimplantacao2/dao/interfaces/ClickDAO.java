package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class ClickDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Click";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select 1 id, 'SUP. PORTO 5' fantasia")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 id,\n" +
                    "    nome\n" +
                    "from\n" +
                    "	prodgrupo")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("id"));
                    imp.setMerc1Descricao(rs.getString("nome"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rs.getString("nome"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("nome"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select * from prodfamilia")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportId(rs.getString("id"));
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setDescricao(rs.getString("nome"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 p.id,\n" +
                    "    pc.codigo ean,\n" +
                    "    p.nome descricaocompleta,\n" +
                    "    pc.custo,\n" +
                    "    pc.venda,\n" +
                    "    p.datacadastro,\n" +
                    "    p.dataalteracao,\n" +        
                    "    p.idgrupo merc1,\n" +
                    "    1 merc2,\n" +
                    "    1 merc3,\n" +
                    "    p.validade,\n" +
                    "    p.estoqueminimo,\n" +
                    "    pn.ncm,\n" +
                    "    pn.cest,\n" +
                    "    pn.cofins_cst,\n" +
                    "    pn.pis_cst,\n" +
                    "    pn.icms_cst\n" +
                    "from\n" +
                    "	proddetalhes p\n" +
                    "join prodcodigos pc on (p.id = pc.idproduto)\n" +
                    "join prodnfe pn on (p.id = pn.idproduto)\n" +
                    "order by\n" +
                    "	p.nome")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(Utils.formataNumero(rs.getString("ean")));
                    if((imp.getEan() != null) && (imp.getEan().length() < 7)) {
                        imp.seteBalanca(true);
                        imp.setValidade(rs.getInt("validade"));
                    }
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cofins_cst"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_cst"));
                    if((rs.getString("icms_cst") != null) && (!"".equals(rs.getString("icms_cst")))) {
                        switch(rs.getString("icms_cst").trim()) {
                            case "500": 
                                    imp.setIcmsAliq(0); 
                                    imp.setIcmsCst(60);
                                    break;
                            default:
                                    imp.setIcmsAliq(0); 
                                    imp.setIcmsCst(40);
                                    break;
                            
                        }
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 p.id,\n" +
                    "    p.razaosocial,\n" +
                    "    p.nomefantasia,\n" +
                    "    p.cnpj,\n" +
                    "    p.ie,\n" +
                    "    p.email,\n" +
                    "    p.datacadastro,\n" +
                    "    p.dataalteracao,\n" +
                    "    p.limitecredito,\n" +
                    "    p.bloqueado,\n" +
                    "    pe.endereco,\n" +
                    "    pe.bairro,\n" +
                    "    pe.cidade,\n" +
                    "    pe.estado,\n" +
                    "    pe.cep,\n" +
                    "    pe.numero,\n" +
                    "    pe.complemento,\n" +
                    "    pe.pontoreferencia,\n" +
                    "    pt.fone,\n" +
                    "    pt.contato\n" +
                    "from\n" +
                    "	pessoas p \n" +
                    "left join pessoasendereco pe on (p.id = pe.idpessoa)\n" +
                    "left join pessoastelefone pt on (p.id = pt.idpessoa)\n" +
                    "where\n" +
                    "	p.tcliente = 1")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setAtivo(rs.getInt("bloqueado") == 1 ? false : true);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento") + " " + rs.getString("pontoreferencia"));
                    imp.setTelefone(rs.getString("fone"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 p.id,\n" +
                    "    p.razaosocial,\n" +
                    "    p.nomefantasia,\n" +
                    "    p.cnpj,\n" +
                    "    p.ie,\n" +
                    "    p.email,\n" +
                    "    p.datacadastro,\n" +
                    "    p.dataalteracao,\n" +
                    "    p.limitecredito,\n" +
                    "    p.bloqueado,\n" +
                    "    pe.endereco,\n" +
                    "    pe.bairro,\n" +
                    "    pe.cidade,\n" +
                    "    pe.estado,\n" +
                    "    pe.cep,\n" +
                    "    pe.numero,\n" +
                    "    pe.complemento,\n" +
                    "    pe.pontoreferencia,\n" +
                    "    pt.fone,\n" +
                    "    pt.contato\n" +
                    "from\n" +
                    "	pessoas p \n" +
                    "left join pessoasendereco pe on (p.id = pe.idpessoa)\n" +
                    "left join pessoastelefone pt on (p.id = pt.idpessoa)\n" +
                    "where\n" +
                    "	p.tfornecedor = 1")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addContato("1", "EMAIL", null, null, rs.getString("email").trim());
                    }
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getInt("bloqueado") == 1 ? false : true);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento") + " " + rs.getString("pontoreferencia"));
                    imp.setTel_principal(rs.getString("fone"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 c.id,\n" +
                    "    c.idcliente,\n" +
                    "    p.cnpj,\n" +
                    "    p.razaosocial,\n" +
                    "    c.iddocumento,\n" +
                    "    c.data,\n" +
                    "    c.descricao observacao,\n" +
                    "    c.valororiginal,\n" +
                    "    c.vencimento,\n" +
                    "    valorpago\n" +
                    "from\n" +
                    "	contasreceber c\n" +
                    "join pessoas p on (c.idcliente = p.id) \n" +
                    "where\n" +
                    "	pago = 'NÃO'\n" +
                    "order by\n" +
                    "	c.data")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("iddocumento"));
                    imp.setDataEmissao(rs.getDate("data"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setValor(rs.getDouble("valororiginal"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
