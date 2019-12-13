package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class GestorPdvDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "PDV";
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select codigo, nome from filiais")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome")));
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
                    "  codigo merc1,\n" +
                    "  nome descmerc1\n" +
                    "from\n" +
                    "  grupos\n" +
                    "order by\n" +
                    "  nome")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    
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
                    "  codigo id,\n" +
                    "  nome descricaocompleta,\n" +
                    "  nomered descricaoreduzida,\n" +
                    "  codean codigobarras,\n" +
                    "  grupo mercadologico1,\n" +
                    "  coalesce(preco, 0) precovenda,\n" +
                    "  coalesce(custo, 0) custo,\n" +
                    "  unidade,\n" +
                    "  icms icmsdebito,\n" +
                    "  trib cstdebito,\n" +
                    "  coalesce(redbase, 0) icmsreducaodebito,\n" +
                    "  pesavel,\n" +
                    "  coalesce(validade, 0) validade,\n" +
                    "  dtcad datacadastro,\n" +
                    "  ativo,\n" +
                    "  compvenda margem,\n" +
                    "  pesobruto,\n" +
                    "  pesoliq,\n" +
                    "  quantemb qtdembalagem,\n" +
                    "  estoque1,\n" +
                    "  ncm,\n" +
                    "  cest,\n" +
                    "  tribpis cstpiscofinsdebito,\n" +
                    "  tribpisent cstpiscofinscredito,\n" +
                    "  natrec naturezareceita\n" +
                    "from\n" +
                    "  produtos")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoreduzida")));
                    imp.seteBalanca(rs.getInt("pesavel") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setEan(rs.getString("codigobarras"));
                    
                    if(imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque1"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    
                    imp.setIcmsAliq(rs.getDouble("icmsdebito"));
                    imp.setIcmsCst(rs.getString("cstdebito"));
                    imp.setIcmsReducao(rs.getDouble("icmsreducaodebito"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpiscofinscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpiscofinsdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliq"));
                    
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
                    "  codigo id,\n" +
                    "  nome razao,\n" +
                    "  fantasia,\n" +
                    "  identidade ie,\n" +
                    "  cgc cnpj,\n" +
                    "  cpf,\n" +
                    "  endereco,\n" +
                    "  bairro,\n" +
                    "  cidade,\n" +
                    "  municipio,\n" +
                    "  numero,\n" +
                    "  compl,\n" +
                    "  uf,\n" +
                    "  cep,\n" +
                    "  telefone,\n" +
                    "  fax,\n" +
                    "  celular,\n" +
                    "  contato,\n" +
                    "  mail email,\n" +
                    "  datacad,\n" +
                    "  obs\n" +
                    "from\n" +
                    "  fornecedores")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setIe_rg(rs.getString("ie"));
                    
                    if(rs.getString("cnpj") != null && !"".equals(rs.getString("cnpj"))) {
                        imp.setCnpj_cpf(rs.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rs.getString("cpf"));
                    }

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("compl"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setDatacadastro(rs.getDate("datacad"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("2", "CELULAR", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("3", rs.getString("contato"), null, null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("4", null, null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  codigo idproduto,\n" +
                    "  fornult1 idfornecedor\n" +
                    "from\n" +
                    "  produtos\n" +
                    "where\n" +
                    "  coalesce(fornult1, 0) != 0\n" +
                    "order by\n" +
                    "  2, 1")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    
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
                    "  codigo id,\n" +
                    "  nome razao,\n" +
                    "  razao fantasia,\n" +
                    "  identidade ie,\n" +
                    "  cgc cnpj,\n" +
                    "  cpf,\n" +
                    "  endereco,\n" +
                    "  bairro,\n" +
                    "  cidade,\n" +
                    "  numero,\n" +
                    "  uf,\n" +
                    "  cep,\n" +
                    "  telefone,\n" +
                    "  fax,\n" +
                    "  celular,\n" +
                    "  mail,\n" +
                    "  datacad,\n" +
                    "  contato,\n" +
                    "  obs,\n" +
                    "  ativo,\n" +
                    "  limite,\n" +
                    "  datanasc,\n" +
                    "  saldodev,\n" +
                    "  salario,\n" +
                    "  limref limiteref\n" +
                    "from\n" +
                    "  clientes")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    
                    if(rs.getString("cnpj") != null && !"".equals(rs.getString("cnpj"))) {
                        imp.setCnpj(rs.getString("cnpj"));
                    } else {
                        imp.setCnpj(rs.getString("cpf"));
                    }
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacad"));
                    
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("1", rs.getString("contato"), null, null, null);
                    }
                    
                    imp.setObservacao(rs.getString("obs"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDataNascimento(rs.getDate("datanasc"));
                    
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
                    "  numfinan id,\n" +
                    "  numvenda cupom,\n" +
                    "  codcli idcliente,\n" +
                    "  documento,\n" +
                    "  parcela,\n" +
                    "  datalanc emissao,\n" +
                    "  datavenc vencimento,\n" +
                    "  valor,\n" +
                    "  juros,\n" +
                    "  multa,\n" +
                    "  obs\n" +
                    "from\n" +
                    "  finan\n" +
                    "where\n" +
                    "  codcli > 0 and\n" +
                    "  datapag = '0000-00-00'\n" +
                    "order by\n" +
                    "  datavenc")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setMulta(rs.getDouble("multa"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
