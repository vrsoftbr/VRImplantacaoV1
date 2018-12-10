package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.InventarioIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class IntelliconDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean vBalanca;
    private static final Logger LOG = Logger.getLogger(IntelliconDAO.class.getName());

    @Override
    public String getSistema() {
        return "Intellicon";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    cod_tributacao,\n"
                    + "    aliquota,\n"
                    + "    cst_icms\n"
                    + "from\n"
                    + "    aliquotas\n"
                    + "order by\n"
                    + "    cod_tributacao")) {
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
                    "select\n"
                    + "    loja as id,\n"
                    + "    nome_fantasia,\n"
                    + "    cnpj\n"
                    + "from\n"
                    + "    filiais\n"
                    + "order by\n"
                    + "    loja")) {
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
                    "select\n"
                    + "    d.cod_depto as merc1,\n"
                    + "    d.nome_depto as descmerc1,\n"
                    + "    g.cod_grupo as merc2,\n"
                    + "    g.nome_grupo as descmerc2,\n"
                    + "    coalesce(s.cod_subgrupo, 1) as merc3,\n"
                    + "    coalesce(s.nome_subgrupo, g.nome_grupo) as descmerc3\n"
                    + "from\n"
                    + "    departamento d\n"
                    + "left join\n"
                    + "    grupo g on g.cod_depto = d.cod_depto\n"
                    + "left join\n"
                    + "    subgrupo s on s.cod_grupo = g.cod_grupo\n"
                    + "where\n"
                    + "    d.cod_depto != 0\n"
                    + "order by\n"
                    + "    d.cod_depto,\n"
                    + "    g.cod_grupo,\n"
                    + "    s.cod_subgrupo")) {
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
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.loja, \n" +
                    "    p.cod_produto,\n" +
                    "    p.data_inicial, \n" +
                    "    p.data_final,\n" +
                    "    p.preco_promocional,\n" +
                    "    p.preco_alterado,\n" +
                    "    p.margem\n" +
                    "from\n" +
                    "    promocao p\n" +
                    "where\n" +
                    "    p.data_final >= '" + FORMAT.format(dataTermino) + " 23:59' \n" +
                    "order by\n" +
                    "    p.data_final")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setDataFim(rs.getDate("data_final"));
                    imp.setDataInicio(rs.getDate("data_inicial"));
                    imp.setIdProduto(rs.getString("cod_produto"));
                    imp.setPrecoOferta(rs.getDouble("preco_promocional"));
                    
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
                    "select\n"
                    + "    p.cod_produto as id,\n"
                    + "    b.cod_barra as codigobarras,\n"
                    + "    p.nome_produto as nomecompleto,\n"
                    + "    p.nome_reduzido as nomereduzido,\n"
                    + "    p.cod_depto as merc1,\n"
                    + "    s.cod_grupo as merc2,\n"
                    + "    coalesce(p.cod_subgrupo, 1) as merc3,\n"
                    + "    p.preco_1 as precovenda,\n"
                    + "    p.margem_1 as margem,\n"
                    + "    p.margem_bruta_1 as margembruta,\n"
                    + "    p.custo_atual as custocomimposto,\n"
                    + "    p.custo_liquido as custosemimposto,\n"
                    + "    p.produto_pesado as isbalanca,\n"
                    + "    p.dias_validade as validade,\n"
                    + "    upper(p.unidade) as unidade,\n"
                    + "    p.qtd_unidade as qtdunidade,\n"
                    + "    1 qtduni, \n"
                    + "    p.estoque as estoque,\n"
                    + "    p.estoque_minimo as estoquemin,\n"
                    + "    p.estoque_maximo as estoquemax,\n"
                    + "    p.inativo, \n"
                    + "    ncm.codigo as ncm,\n"
                    + "    p.peso_bruto as pesobruto,\n"
                    + "    p.peso_liquido as pesoliquido,\n"
                    + "    p.data_inclusao as dtcadastro,\n"
                    + "    a.aliquota as icmssaida,\n"
                    + "    a.cst_icms as csticms,\n"
                    + "    ncm.cst_pis as cstpiscredito,\n"
                    + "    ncm.cst_pis_saida as cstpisdebito,\n"
                    + "    pi.cest,\n"
                    + "    pi.cod_natureza_financeira naturezareceita,\n"
                    + "    ncm.cod_va gia"
                    + "from\n"
                    + "    produto p\n"
                    + "left join\n"
                    + "    ncm_produto ncm on ncm.pkcod = p.chave_ncm\n"
                    + "left join\n"
                    + "    aliquotas a on a.cod_tributacao = p.cod_tributacao\n"
                    + "left join\n"
                    + "    barra b on b.cod_produto = p.cod_produto\n"
                    + "left join\n"
                    + "    subgrupo s on s.cod_subgrupo = p.cod_subgrupo\n"
                    + "left join\n"
                    + "    produtosicomm pi on pi.cod_barra = b.cod_barra\n"
                    + "where\n"
                    + "    p.cod_produto != 0\n"
                    + "order by\n"
                    + "    p.cod_produto")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("nomecompleto"));
                    
                    if((rs.getString("nomereduzido") == null) && ("".equals(rs.getString("nomereduzido")))) {
                        imp.setDescricaoReduzida(rs.getString("nomecompleto").substring(1, 22));
                    } else {
                        imp.setDescricaoReduzida(rs.getString("nomereduzido"));
                    }
                        
                    imp.setDescricaoGondola(rs.getString("nomecompleto"));
                    
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margembruta"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));

                    if ("S".equals(rs.getString("isbalanca"))) {
                        if (vBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId().trim());
                            imp.setEan(String.valueOf(codigoProduto));
                            
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
                    imp.setQtdEmbalagem(rs.getInt("qtduni"));
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
                    imp.setPiscofinsNaturezaReceita(rs.getInt("naturezareceita"));
                        
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
                    "select\n"
                    + "    cod_produto,\n"
                    + "    cod_fornecedor,\n"
                    + "    cod_fornecprod\n"
                    + "from\n"
                    + "    fornecprod\n"
                    + "order by\n"
                    + "    cod_produto, cod_fornecedor")) {
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
    
    private Date dataInventario;
    public void setDataInventario(Date dataInventario) {
        this.dataInventario = dataInventario;
    }
    
    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public Date getDataInventario() {
        return dataInventario;
    }
    
    @Override
    public List<InventarioIMP> getInventario() throws Exception {
        List<InventarioIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    est.data || est.cod_produto id,\n" +
                    "    est.cod_produto id_produto,\n" +
                    "    cast('30.09.2018' as date) data,\n" +
                    "    p.nome_produto descricao,\n" +
                    "    upper(p.unidade) unidade,\n" +
                    "    p.preco_1 precovenda,\n" +
                    "    est.estoque quantidade,\n" +
                    "    est.custo_medio custocomimposto,\n" +
                    "    est.custo_medio custosemimposto,\n" +
                    "    round((est.custo_medio * est.estoque), 2) custototal,\n" +
                    "    p.cod_tributacao id_aliquota,\n" +        
                    "    a.aliquota,\n" +
                    "    a.cst_icms,\n" +
                    "    n.cst_pis,\n" +
                    "    n.cst_cofins,\n" +        
                    "    n.cst_pis_saida\n" +
                    "from\n" +
                    "    produtovendido est\n" +
                    "join \n" +
                    "    produto p on (p.cod_produto = est.cod_produto)\n" +
                    "join\n" +
                    "    aliquotas a on (p.cod_tributacao = a.cod_tributacao)\n" +
                    "join\n" +
                    "    ncm_produto n on (n.pkcod = p.chave_ncm)\n" +
                    "where\n" +
                    "    est.data = '" + FORMAT.format(getDataInventario()) + "' and\n" +
                    "    est.cod_produto >= 0 and\n" +
                    "    est.estoque > 0")) {
                while(rs.next()) {
                    InventarioIMP imp = new InventarioIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setData(rs.getDate("data"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setQuantidade(rs.getDouble("quantidade"));
                    imp.setPrecoVenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPis(rs.getDouble("cst_pis"));
                    imp.setCofins(rs.getDouble("cst_cofins"));
                    imp.setIdAliquotaCredito(rs.getString("id_aliquota"));
                    imp.setIdAliquotaDebito(rs.getString("id_aliquota"));
                    
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
                    "select\n"
                    + "    f.cod_fornecedor as id,\n"
                    + "    f.nome_fornecedor as razao,\n"
                    + "    f.nome_fantasia as fantasia,\n"
                    + "    f.endereco,\n"
                    + "    f.bairro,\n"
                    + "    f.cep,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.fone_1,\n"
                    + "    f.fone_2,\n"
                    + "    f.fax,\n"
                    + "    f.cnpj,\n"
                    + "    f.ie,\n"
                    + "    f.im,\n"
                    + "    f.contato,\n"
                    + "    f.email,\n"
                    + "    f.prazo_entrega,\n"
                    + "    f.cod_uf,\n"
                    + "    f.cod_municipio,\n"
                    + "    f.inativo,\n"
                    + "    f.fone0800,\n"
                    + "    f.fone_vendedor,\n"
                    + "    f.nome_vendedor,\n"
                    + "    f.fone_supervisor,\n"
                    + "    f.nome_supervisor,\n"
                    + "    f.banco_padrao\n"
                    + "from\n"
                    + "    fornecedor f\n"
                    + "order by\n"
                    + "    f.cod_fornecedor")) {
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

                    if ((rs.getString("fone_2") != null) && (!"".equals(rs.getString("fone_2")))) {
                        imp.addTelefone("Telefone2", rs.getString("fone_2"));
                    }
                    if ((rs.getString("fax") != null) && (!"".equals(rs.getString("fax")))) {
                        imp.addTelefone("fax", rs.getString("fax"));
                    }
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("im"));
                    if ((rs.getString("contato") != null) && (!"".equals(rs.getString("contato")))) {
                        imp.setObservacao(rs.getString("contato"));
                    }

                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addEmail("Email", rs.getString("email"), TipoContato.FINANCEIRO);
                    }
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setAtivo("S".equals(rs.getString("inativo")) ? false : true);

                    if ((rs.getString("fone0800") != null) && (!"".equals(rs.getString("fone0800")))) {
                        imp.addTelefone("0800", rs.getString("fone0800"));
                    }
                    if ((rs.getString("fone_vendedor") != null) && (!"".equals(rs.getString("fone_vendedor")))) {
                        imp.addContato(rs.getString("nome_vendedor"), rs.getString("fone_vendedor"), null, TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("fone_supervisor") != null) && (!"".equals(rs.getString("fone_supervisor")))) {
                        imp.addContato(rs.getString("nome_supervisor"), rs.getString("fone_supervisor"), null, TipoContato.COMERCIAL, null);
                    }

                    if ((rs.getString("banco_padrao") != null) && (rs.getInt("banco_padrao") != 0)) {
                        imp.setIdBanco(rs.getInt("banco_padrao"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente as id,\n"
                    + "    c.nome_cliente as razao,\n"
                    + "    c.endereco,\n"
                    + "    c.bairro,\n"
                    + "    c.cep,\n"
                    + "    c.numero,\n"
                    + "    c.cod_municipio as idmunicipio,\n"
                    + "    c.cidade,\n"
                    + "    c.cod_uf as iduf,\n"
                    + "    c.estado,\n"
                    + "    c.fone_1 as fone1,\n"
                    + "    c.fone_2 as fone2,\n"
                    + "    c.fax,\n"
                    + "    c.celular,\n"
                    + "    c.ramal,\n"
                    + "    c.sexo,\n"
                    + "    c.data_nascimento,\n"
                    + "    c.rg,\n"
                    + "    c.cpfcnpj,\n"
                    + "    c.data_cadastro,\n"
                    + "    c.situacao,\n"
                    + "    c.statusshop,\n"
                    + "    c.credito_rotativo limite\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "order by\n"
                    + "    c.cod_cliente")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipioIBGE(rs.getInt("idmunicipio"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUfIBGE(rs.getInt("iduf"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTelefone(rs.getString("fone1"));
                    if ((rs.getString("fone2") != null) && (!"".equals(rs.getString("fone2")))) {
                        imp.addTelefone("Telefone2", rs.getString("fone2"));
                    }
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setSexo("M".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setAtivo("S".equals(rs.getString("statusshop")) ? false : true);
                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    if(rs.getInt("situacao") == 3) {
                        imp.setBloqueado(true);
                    } else {
                        imp.setBloqueado(false);
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    substring(dr.cod_cliente || '' || dr.num_ecf || '' || dr.num_cupom || '' || dr.valor from 1 for 10)  as id,\n"
                    + "    dr.cod_cliente,\n"
                    + "    c.cpfcnpj as cnpj,\n"
                    + "    c.rg,\n"
                    + "    dr.data_cupom as datacupom,\n"
                    + "    dr.num_ecf as ecf,\n"
                    + "    dr.num_cupom as coo,\n"
                    + "    dr.num_parcela as parcela,\n"
                    + "    dr.valor,\n"
                    + "    dr.obs,\n"
                    + "    dr.data_vencto as dtvencimento,\n"
                    + "    dr.num_recibo as recibo,\n"
                    + "    dr.parcelas,\n"
                    + "    dr.data_recpag as dtrecimento,\n"
                    + "    dr.num_documento as nrdoc\n"
                    + "from\n"
                    + "    debitorotativo dr\n"
                    + "join\n"
                    + "    cliente c on c.cod_cliente = dr.cod_cliente\n"
                    + "where\n"
                    + "    dr.loja = " + getLojaOrigem() + " and\n"
                    + "    dr.pago = 'N' and\n"
                    + "    dr.num_ecf != 0 and\n"
                    + "    dr.data_cupom between '01.01.2018' and '30.09.2018'\n"
                    + "order by\n"
                    + "    dr.data_cupom")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("cod_cliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("datacupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.num_cupom as coo,\n"
                    + "    c.data_venda as dtcheque,\n"
                    + "    c.terminal as ecf,\n"
                    + "    c.cod_cliente as idcliente,\n"
                    + "    c.nome_cliente as razao,\n"
                    + "    c.valor,\n"
                    + "    c.num_cheque as nrcheque,\n"
                    + "    c.banco,\n"
                    + "    c.conta,\n"
                    + "    c.agencia,\n"
                    + "    c.cpfcnpj,\n"
                    + "    c.telefone,\n"
                    + "    c.data_pre as dtvencimento,\n"
                    + "    c.cod_alinea,\n"
                    + "    c.num_parcelas,\n"
                    + "    c.situacao\n"
                    + "from\n"
                    + "    cheque c\n"
                    + "where\n"
                    + "    loja = " + getLojaOrigem() + " and\n"
                    + "    c.situacao in (1, 6)\n"
                    + "order by\n"
                    + "    c.data_venda")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    int id = 0;
                    Random r = new Random();
                    id = r.nextInt(99999) + 10000;
                    imp.setId(String.valueOf(id));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDate(rs.getDate("dtcheque"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNome(rs.getString("razao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCheque(rs.getString("nrcheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setConta(rs.getString("conta"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setCpf(rs.getString("cpfcnpj"));
                    if ((rs.getString("telefone") != null) && (!"".equals(rs.getString("telefone")))) {
                        imp.setTelefone(rs.getString("telefone"));
                    }
                    imp.setDataDeposito(rs.getDate("dtvencimento"));
                    imp.setAlinea(rs.getInt("cod_alinea"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new IntelliconDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new IntelliconDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("dtemissao");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("dtemissao"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("dtabertura")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("dtemissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado("S".equals(rst.getString("cancelado").trim()) ? true : false);
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCpf(rst.getString("cnpjcliente"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modeloecf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "    c.num_cupom coo,\n" +
                    "    c.num_ecf ecf,\n" +
                    "    c.data_cupom dtemissao,\n" +
                    "    cast(c.data_cupom as time) horatermino,\n" +
                    "    c.valor_total valor,\n" +
                    "    c.valor_desconto desconto,\n" +
                    "    c.valor_acrescimo acrescimo,\n" +
                    "    c.data_abertura dtabertura,\n" +
                    "    cast(c.data_abertura as time) horainicio,\n" +
                    "    c.cancelado,\n" +
                    "    c.statusshop status,\n" +
                    "    c.ident_cod_cliente idcliente,\n" +
                    "    c.ident_nome_cliente nomecliente,\n" +
                    "    c.ident_cpfcnpj cnpjcliente,\n" +
                    "    cl.nome_cliente razaosocial,\n" +
                    "    cl.endereco,\n" +
                    "    cl.bairro,\n" +
                    "    cl.numero,\n" +
                    "    cl.cidade,\n" +
                    "    cl.estado,\n" +
                    "    cl.cep,\n" +
                    "    c.modelo_documento idmodelo,\n" +
                    "    doc.descricao descricaodocumento,\n" +
                    "    ecf.ecf_modelo modeloecf,\n" +
                    "    ecf.ecf_numserie numeroserie\n" +
                    "from\n" +
                    "    cupom c\n" +
                    "left join\n" +
                    "    equipamento_ecf ecf on c.num_ecf = ecf.ecf_caixa\n" +
                    "left join\n" +
                    "    modelo_documento doc on c.modelo_documento = doc.codigo\n" +
                    "left join\n" +
                    "    cliente cl on c.ident_cod_cliente = cl.cod_cliente\n" +
                    "where\n" +
                    "    c.data_cupom between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n" +
                    "    c.loja = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "    c.data_cupom";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("dtemissao") + "-" + rst.getDouble("posicao");
                        String idVenda = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("dtemissao");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado("S".equals(rst.getString("cancelado").trim()) ? true : false);
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("aliqicms"));
                        next.setIcmsCst(rst.getInt("cst_icms"));
                        next.setSequencia(rst.getInt("posicao"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = 
                    "select\n" +
                    "    i.loja,\n" +
                    "    i.num_cupom coo,\n" +
                    "    i.num_ecf ecf,\n" +
                    "    i.data_cupom dtemissao,\n" +
                    "    i.cod_produto idproduto,\n" +
                    "    case when p.nome_reduzido = '' then\n" +
                    "     substring(p.nome_produto from 1 for 30) else\n" +
                    "    p.nome_produto end descricaoreduzida,\n" +
                    "    upper(p.unidade) unidade,\n" +
                    "    i.cod_barra ean,\n" +
                    "    i.quantidade,\n" +
                    "    i.preco,\n" +
                    "    i.valor_total valortotal,\n" +
                    "    i.valor_desc_item desconto,\n" +
                    "    i.valor_acresc_item acrescimo,\n" +
                    "    i.cancelado,\n" +
                    "    i.statusshop status,\n" +
                    "    i.cod_tributacao idaliquota,\n" +
                    "    a.aliquota aliqicms,\n" +
                    "    a.cst_icms,\n" +
                    "    i.aliq_pis,\n" +
                    "    i.cst_pis,\n" +
                    "    i.cst_cofins,\n" +
                    "    i.cod_natureza_financeira naturezareceita,\n" +
                    "    i.cest,\n" +
                    "    i.posicao\n" +
                    "from\n" +
                    "    itenscupom i\n" +
                    "inner join\n" +
                    "    produto p on i.cod_produto = p.cod_produto\n" +
                    "left join\n" +
                    "    aliquotas a on i.cod_tributacao = a.cod_tributacao\n" +
                    "where\n" +
                    "    i.data_cupom between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and\n" +
                    "    i.loja = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "    i.data_cupom";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
