package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class TecnosoftDAO extends InterfaceDAO implements MapaTributoProvider{

    @Override
    public String getSistema() {
        return "TECNOSOFT";
    }
    
    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from FILIAL"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento("1", "LOJA 01"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_ecf,\n" +
                    "    descricao_tribut desc\n" +
                    "from\n" +
                    "    tribut_ecf")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id_ecf"), rs.getString("desc")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    distinct\n" +
                    "    p.id_dep merc1,\n" +
                    "    d.descricao descmerc1,\n" +
                    "    p.id_set merc2,\n" +
                    "    s.descricao descmerc2,\n" +
                    "    p.id_sub merc3,\n" +
                    "    ss.descricao descmerc3\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "join departamentos d on p.id_dep = d.id_dep\n" +
                    "join setores s on p.id_set = s.id_set\n" +
                    "join subsetor ss on p.id_sub = ss.id_sub\n" +
                    "order by\n" +
                    "    1, 3, 5")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_prod id,\n" +
                    "    p.codigo,\n" +
                    "    p.codigobar ean,\n" +
                    "    coalesce(p.balanca, 0) balanca,\n" +
                    "    coalesce(p.validade, 0) validade,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.desativar,\n" +
                    "    un.descricao embalagem,\n" +
                    "    unc.descricao embalagemcompra,\n" +
                    "    p.descricao,\n" +
                    "    p.descricao_ecf,\n" +
                    "    p.descricao_nf,\n" +
                    "    p.estoque,\n" +
                    "    p.minimo estoqueminimo,\n" +
                    "    p.maximo estoquemaximo,\n" +
                    "    p.peso_bruto,\n" +
                    "    p.peso_liquido,\n" +
                    "    p.lucro margem,\n" +
                    "    p.margem_bruta,\n" +
                    "    p.precovenda,\n" +
                    "    p.custoinicial,\n" +
                    "    p.custofinal,\n" +
                    "    p.pis_st,\n" +
                    "    p.cofins_st,\n" +
                    "    p.id_dep merc1,\n" +
                    "    p.id_set merc2,\n" +
                    "    p.id_sub merc3,\n" +
                    "    p.id_ecf idicms,\n" +
                    "    p.ncm_sh ncm,\n" +
                    "    p.cest,\n" +
                    "    p.nat_receita_pis naturezareceita\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "left join unidades un on p.id_unid = un.id_unid\n" +
                    "left join unidades unc on p.id_unid_forn = unc.id_unid\n" +
                    "order by\n" +
                    "    1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricao_ecf"));
                    imp.setDescricaoGondola(rs.getString("descricao"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setSituacaoCadastro(rs.getInt("desativar") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custoinicial"));
                    imp.setCustoComImposto(rs.getDouble("custofinal"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_st"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    f.id_forn,\n" +
                    "    f.codigo,\n" +
                    "    f.docum1 cnpj,\n" +
                    "    f.docum2 ie,\n" +
                    "    f.razaosocial,\n" +
                    "    f.nomefantasia fantasia,\n" +
                    "    f.endereco,\n" +
                    "    f.end_numero numero,\n" +
                    "    f.bairro,\n" +
                    "    f.cep,\n" +
                    "    f.complemento,\n" +
                    "    f.cidade,\n" +
                    "    f.estado,\n" +
                    "    f.telefone1,\n" +
                    "    f.telefone2,\n" +
                    "    f.telefone3,\n" +
                    "    f.telefone4,\n" +
                    "    f.email_compras,\n" +
                    "    f.email_financeiro,\n" +
                    "    f.data_cadastro,\n" +
                    "    f.desativar,\n" +
                    "    f.contato,\n" +
                    "    f.observacoes\n" +
                    "from\n" +
                    "    fornecedores f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_forn"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("telefone1"));
                    
                    if(rs.getString("telefone2") != null && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", "TEL2", rs.getString("telefone2"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("telefone3") != null && !"".equals(rs.getString("telefone3"))) {
                        imp.addContato("2", "TEL3", rs.getString("telefone3"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("telefone4") != null && !"".equals(rs.getString("telefone4"))) {
                        imp.addContato("3", "TEL4", rs.getString("telefone4"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("email_compras") != null && !"".equals(rs.getString("email_compras"))) {
                        imp.addContato("4", "EMAIL", null, null, TipoContato.NFE, rs.getString("email_compras"));
                    }
                    
                    if(rs.getString("email_financeiro") != null && !"".equals(rs.getString("email_financeiro"))) {
                        imp.addContato("5", "EMAIL2", null, null, TipoContato.NFE, rs.getString("email_financeiro"));
                    }
                    
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getInt("desativar") == 1 ? false : true);
                    imp.setObservacao(rs.getString("observacao") == null ? "" : rs.getString("observacao").trim());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_cli,\n" +
                    "    c.codigo,\n" +
                    "    c.docum1 cnpj,\n" +
                    "    c.docum2 ie,\n" +
                    "    c.docum3,\n" +
                    "    c.razaosocial,\n" +
                    "    c.nomefantasia fantasia,\n" +
                    "    c.endereco,\n" +
                    "    c.end_numero numero,\n" +
                    "    c.bairro bairro,\n" +
                    "    c.cep,\n" +
                    "    c.cidade,\n" +
                    "    c.estado,\n" +
                    "    c.complemento,\n" +
                    "    c.telefone1,\n" +
                    "    c.telefone2,\n" +
                    "    c.telefone3,\n" +
                    "    c.telefone4,\n" +
                    "    c.email_nfe,\n" +
                    "    c.email_contato,\n" +
                    "    c.email_cobranca,\n" +
                    "    c.data_cadastro,\n" +
                    "    c.data_nascimento,\n" +
                    "    c.desativar,\n" +
                    "    c.limite_credito,\n" +
                    "    c.contato,\n" +
                    "    c.observacoes,\n" +
                    "    c.pai,\n" +
                    "    c.mae\n" +
                    "from\n" +
                    "    clientes c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id_cli"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTelefone(rs.getString("telefone1"));
                    if(rs.getString("telefone2") != null && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", "TEL2", rs.getString("telefone2"), null, null);
                    }
                    
                    if(rs.getString("telefone3") != null && !"".equals(rs.getString("telefone3"))) {
                        imp.addContato("2", "TEL3", rs.getString("telefone3"), null, null);
                    }
                    
                    if(rs.getString("telefone4") != null && !"".equals(rs.getString("telefone4"))) {
                        imp.addContato("3", "TEL4", rs.getString("telefone4"), null, null);
                    }
                    
                    if(rs.getString("email_contato") != null && !"".equals(rs.getString("email_contato"))) {
                        imp.addContato("4", "EMAIL", null, null, rs.getString("email_contato"));
                    }
                    
                    if(rs.getString("email_cobranca") != null && !"".equals(rs.getString("email_cobranca"))) {
                        imp.addContato("5", "EMAIL2", null, null, rs.getString("email_cobranca"));
                    }
                    
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    imp.setAtivo(rs.getInt("desativar") == 1 ? false : true);
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setObservacao(rs.getString("observacoes") == null ? "" : rs.getString("observacoes"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    } 

}
