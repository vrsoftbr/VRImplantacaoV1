package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class IntelliconDAO extends InterfaceDAO implements MapaTributoProvider{

    public boolean vBalanca;
    
    @Override
    public String getSistema() {
        return "Intellicon";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    cod_tributacao,\n" +
                    "    aliquota,\n" +
                    "    cst_icms\n" +
                    "from\n" +
                    "    aliquotas\n" +
                    "order by\n" +
                    "    cod_tributacao")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("cod_tributacao"), rs.getString("aliquota")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    loja as id,\n" +
                    "    nome_fantasia,\n" +
                    "    cnpj\n" +
                    "from\n" +
                    "    filiais\n" +
                    "order by\n" +
                    "    loja")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome_fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    d.cod_depto as merc1,\n" +
                    "    d.nome_depto as descmerc1,\n" +
                    "    g.cod_grupo as merc2,\n" +
                    "    g.nome_grupo as descmerc2,\n" +
                    "    coalesce(s.cod_subgrupo, 1) as merc3,\n" +
                    "    coalesce(s.nome_subgrupo, g.nome_grupo) as descmerc3\n" +
                    "from\n" +
                    "    departamento d\n" +
                    "left join\n" +
                    "    grupo g on g.cod_depto = d.cod_depto\n" +
                    "left join\n" +
                    "    subgrupo s on s.cod_grupo = g.cod_grupo\n" +
                    "where\n" +
                    "    d.cod_depto != 0\n" +
                    "order by\n" +
                    "    d.cod_depto,\n" +
                    "    g.cod_grupo,\n" +
                    "    s.cod_subgrupo")) {
                while (rs.next()) {
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.cod_produto as id,\n" +
                    "    b.cod_barra as codigobarras,\n" +
                    "    p.nome_produto as nomecompleto,\n" +
                    "    p.nome_reduzido as nomereduzido,\n" +
                    "    p.cod_depto as merc1,\n" +
                    "    s.cod_grupo as merc2,\n" +
                    "    coalesce(p.cod_subgrupo, 1) as merc3,\n" +
                    "    p.preco_1 as precovenda,\n" +
                    "    p.margem_1 as margem,\n" +
                    "    p.margem_bruta_1 as margembruta,\n" +
                    "    p.custo_atual as custocomimposto,\n" +
                    "    p.custo_liquido as custosemimposto,\n" +
                    "    p.produto_pesado as isbalanca,\n" +
                    "    p.dias_validade as validade,\n" +
                    "    upper(p.unidade) as unidade,\n" +
                    "    p.qtd_unidade as qtdunidade,\n" +
                    "    p.estoque as estoque,\n" +
                    "    p.estoque_minimo as estoquemin,\n" +
                    "    p.estoque_maximo as estoquemax,\n" +
                    "    p.inativo, \n" +
                    "    ncm.codigo as ncm,\n" +
                    "    p.peso_bruto as pesobruto,\n" +
                    "    p.peso_liquido as pesoliquido,\n" +
                    "    p.data_inclusao as dtcadastro,\n" +
                    "    a.aliquota as icmssaida,\n" +
                    "    a.cst_icms as csticms,\n" +
                    "    ncm.cst_pis as cstpiscredito,\n" +
                    "    ncm.cst_pis_saida as cstpisdebito,\n" +
                    "    pi.cest\n" +
                    "from\n" +
                    "    produto p\n" +
                    "left join\n" +
                    "    ncm_produto ncm on ncm.pkcod = p.chave_ncm\n" +
                    "left join\n" +
                    "    aliquotas a on a.cod_tributacao = p.cod_tributacao\n" +
                    "left join\n" +
                    "    barra b on b.cod_produto = p.cod_produto\n" +
                    "left join\n" +
                    "    subgrupo s on s.cod_subgrupo = p.cod_subgrupo\n" +
                    "left join\n" +
                    "    produtosicomm pi on pi.cod_barra = b.cod_barra\n" +
                    "where\n" +
                    "    p.cod_produto != 0\n" +
                    "order by\n" +
                    "    p.cod_produto")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("nomecompleto"));
                    imp.setDescricaoReduzida(rs.getString("nomereduzido"));
                    imp.setDescricaoGondola(rs.getString("nomereduzido"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    
                    if ("S".equals(rs.getString("isbalanca"))) {
                        if (vBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca("S".equals(rs.getString("isbalanca")));
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdunidade"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("inativo")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmssaida"));
                    imp.setIcmsCstSaida(rs.getInt("csticms"));
                    imp.setPiscofinsCstCredito(rs.getInt("cstpiscredito"));
                    imp.setPiscofinsCstDebito(rs.getInt("cstpisdebito"));
                    imp.setCest(rs.getString("cest"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    cod_produto,\n" +
                    "    cod_fornecedor,\n" +
                    "    cod_fornecprod\n" +
                    "from\n" +
                    "    fornecprod\n" +
                    "order by\n" +
                    "    cod_produto, cod_fornecedor")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("cod_produto"));
                    imp.setIdFornecedor(rs.getString("cod_fornecedor"));
                    imp.setCodigoExterno(rs.getString("cod_fornecprod"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    f.cod_fornecedor as id,\n" +
                    "    f.nome_fornecedor as razao,\n" +
                    "    f.nome_fantasia as fantasia,\n" +
                    "    f.endereco,\n" +
                    "    f.bairro,\n" +
                    "    f.cep,\n" +
                    "    f.cidade,\n" +
                    "    f.estado,\n" +
                    "    f.fone_1,\n" +
                    "    f.fone_2,\n" +
                    "    f.fax,\n" +
                    "    f.cnpj,\n" +
                    "    f.ie,\n" +
                    "    f.im,\n" +
                    "    f.contato,\n" +
                    "    f.email,\n" +
                    "    f.prazo_entrega,\n" +
                    "    f.cod_uf,\n" +
                    "    f.cod_municipio,\n" +
                    "    f.inativo,\n" +
                    "    f.fone0800,\n" +
                    "    f.fone_vendedor,\n" +
                    "    f.nome_vendedor,\n" +
                    "    f.fone_supervisor,\n" +
                    "    f.nome_supervisor,\n" +
                    "    f.banco_padrao\n" +
                    "from\n" +
                    "    fornecedor f\n" +
                    "order by\n" +
                    "    f.cod_fornecedor")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setIbge_municipio(rs.getInt("cod_municipio"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setIbge_uf(rs.getInt("cod_uf"));
                    imp.setTel_principal(rs.getString("fone_1"));
                    
                    if ( (rs.getString("fone_2") != null) && (!"".equals(rs.getString("fone_2"))) ) {
                        imp.addTelefone("Telefone2", rs.getString("fone_2"));
                    }
                    if ( (rs.getString("fax") != null) && (!"".equals(rs.getString("fax"))) ) {
                        imp.addTelefone("fax", rs.getString("fax"));
                    }
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("im"));
                    if ( (rs.getString("contato") != null) && (!"".equals(rs.getString("contato"))) ){
                        imp.setObservacao(rs.getString("contato"));
                    }
                    
                    if ( (rs.getString("email") != null) && (!"".equals(rs.getString("email"))) ) {
                        imp.addEmail("Email", rs.getString("email"), TipoContato.FINANCEIRO);
                    }
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setAtivo("S".equals(rs.getString("inativo")) ? false : true);
                    
                    if ( (rs.getString("fone0800") != null) && (!"".equals(rs.getString("fone0800"))) ) {
                        imp.addTelefone("0800", rs.getString("fone0800"));
                    }
                    if ( (rs.getString("fone_vendedor") != null) && (!"".equals(rs.getString("fone_vendedor"))) ) {
                        imp.addContato(rs.getString("nome_vendedor"), rs.getString("fone_vendedor"), null, TipoContato.COMERCIAL, null);
                    }
                    if ( (rs.getString("fone_supervisor") != null) && (!"".equals(rs.getString("fone_supervisor"))) ) {
                        imp.addContato(rs.getString("nome_supervisor"), rs.getString("fone_supervisor"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    if( (rs.getString("banco_padrao") != null) && (rs.getInt("banco_padrao") != 0) ) {
                        imp.setIdBanco(rs.getInt("banco_padrao"));
                    }
    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
