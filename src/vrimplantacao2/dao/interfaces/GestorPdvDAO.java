package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
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
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
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
}
