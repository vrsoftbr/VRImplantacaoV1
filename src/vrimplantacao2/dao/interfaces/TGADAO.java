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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class TGADAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(IntelliconDAO.class.getName());
    public boolean gerarEANAtacado = false;
    public String lojaComplemento = "";

    @Override
    public String getSistema() {
        return "TGA" + lojaComplemento;
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codempresa,\n"
                    + "    nomefantasia || ' - ' || cgc fantasia\n"
                    + "from\n"
                    + "    gempresa")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codempresa"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codsit,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    tstributaria\n"
                    + "where\n"
                    + "    codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codsit"), rs.getString("descricao")));
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
                    + "    distinct\n"
                    + "    g.codgrupo merc1,\n"
                    + "    g.descricao descmerc1,\n"
                    + "    coalesce(d.codtipo, 1) merc2,\n"
                    + "    coalesce(d.descricao, g.descricao) descmerc2,\n"
                    + "    1 merc3,\n"
                    + "    coalesce(d.descricao, g.descricao) descmerc3\n"
                    + "from\n"
                    + "    tproduto p\n"
                    + "inner join tgrupo g on (p.codgrupo = g.codgrupo) and\n"
                    + "    p.codempresa = g.codempresa\n"
                    + "left join ttipoprod d on (p.codtip = d.codtipo) and\n"
                    + "    p.codempresa = d.codempresa\n"
                    + "where\n"
                    + "    p.codempresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    p.codgrupo, p.codtip")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (gerarEANAtacado) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "    codbarras codigobarras,\n"
                        + "    codprd eaninterno,\n"
                        + "    codprdprincipal idproduto,\n"
                        + "    unidade,\n"
                        + "    preco1 precoatacado,\n"
                        + "    qtdembalagem\n"
                        + "from\n"
                        + "    tproduto\n"
                        + "where\n"
                        + "    codempresa = " + getLojaOrigem() + " and\n"
                        + "    codprdprincipal is not null\n"
                        + "order by\n"
                        + "    codprdprincipal, codprd")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("idproduto"));
                        if ((rs.getString("codigobarras") == null)
                                || ("".equals(rs.getString("codigobarras").trim()))) {
                            imp.setEan("99" + rs.getString("eaninterno"));
                        } else {
                            imp.setEan(rs.getString("codigobarras"));
                        }
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                        result.add(imp);
                    }
                }
            }
        } else {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "    codprd idproduto,\n"
                        + "    codbarras codigobarras,\n"
                        + "    codund unidade,\n"
                        + "    1 qtdembalagem\n"
                        + "from\n"
                        + "    tprodbarras\n"
                        + "where\n"
                        + "    codempresa = " + getLojaOrigem())) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("idproduto"));
                        imp.setEan(rs.getString("codigobarras"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                        result.add(imp);
                    }
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
                    "    codprd,\n" +
                    "    codcfo,\n" +
                    "    codnofornec,\n" +
                    "    1 qtd\n" +
                    "from\n" +
                    "    tprodcfonfe\n" +
                    "order by\n" +
                    "    1, 2"
                    /*"select\n"
                    + "    pf.codprd,\n"
                    + "    pf.codcfo,\n"
                    + "    pfc.codnofornec,\n"
                    + "    qtd\n"
                    + "from\n"
                    + "    tprodcfo pf\n"
                    + "left join tprodcfonfe pfc on pf.codprd = pfc.codprd and\n"
                    + "    pf.codcfo = pfc.codcfo\n"
                    + "order by\n"
                    + "    1, 2"*/)) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("codprd"));
                    imp.setIdFornecedor(rs.getString("codcfo"));
                    imp.setCodigoExterno(rs.getString("codnofornec"));
                    imp.setQtdEmbalagem(rs.getDouble("qtd"));

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
                    + "    p.codprd id,\n"
                    + "    p.codprdprincipal,\n"
                    + "    case when p.codprdprincipal is not null\n"
                    + "    then 'S' else 'N' end idprincipal,\n"
                    + "    p.codbarras codigobarras,\n"
                    + "    p.nomefantasia descricaocompleta,\n"
                    + "    p.dtcadastramento datacadastro,\n"
                    + "    p.unidade,\n"
                    + "    p.codundcompra,\n"
                    + "    p.preco1 precovenda,\n"
                    + "    p.pesoliquido,\n"
                    + "    p.pesobruto,\n"
                    + "    p.estoqueminimo,\n"
                    + "    p.estoquemaximo,\n"
                    + "    p.custounitario custocomimposto,\n"
                    + "    p.codgrupo merc1,\n"
                    + "    coalesce(p.codtip, 1) merc2,\n"
                    + "    1 merc3,\n"
                    + "    p.margemlucrofisc,\n"
                    + "    p.margembrutalucro margem,\n"
                    + "    p.saldogeralfisico estoque,\n"
                    + "    p.inativo situacaocadastro,\n"
                    + "    p.codclas ncm,\n"
                    + "    p.cstpis,\n"
                    + "    p.cstcofins,\n"
                    + "    p.cstpisentrada,\n"
                    + "    p.cstcofinsentrada,\n"
                    + "    p.qtdembalagem,\n"
                    + "    p.cest,\n"
                    + "    p.exportabalanca,\n"
                    + "    p.codsit idtributacao\n"
                    + "from\n"
                    + "    tproduto p\n"
                    + "where\n"
                    + "    p.codempresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    p.codprd")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    if ((rs.getString("codigobarras") != null)
                            && !"".equals(rs.getString("codigobarras"))
                            && ("S".equals(rs.getString("idprincipal")))
                            && rs.getString("codigobarras").length() > 6) {
                        imp.setEan(imp.getImportId());
                    } else if ("T".equals(rs.getString("exportabalanca").trim())) {
                        imp.setEan(imp.getImportId());
                        imp.seteBalanca(true);
                    } else {
                        imp.setEan(rs.getString("codigobarras"));
                    }
                    imp.setQtdEmbalagemCotacao(1);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    if (rs.getString("situacaocadastro") != null) {
                        imp.setSituacaoCadastro("F".equals(rs.getString("situacaocadastro").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    }
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpis"));
                    imp.setPiscofinsCstDebito(rs.getString("cstcofins"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagem"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsDebitoId(rs.getString("idtributacao"));
                    imp.setIcmsCreditoId(rs.getString("idtributacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "    filho.codbarras codigobarras,\n"
                        + "    filho.codprd eaninterno,\n"
                        + "    filho.codprdprincipal idproduto,\n"
                        + "    filho.nomefantasia descricaocompleta,\n"
                        + "    filho.unidade,\n"
                        + "    pai.preco1 precovenda,\n"
                        + "    filho.preco1 precoatacado,\n"
                        + "    filho.qtdembalagem\n"
                        + "from\n"
                        + "    tproduto filho\n"
                        + "join tproduto pai on (filho.codprdprincipal = pai.codprd)\n"
                        + "where\n"
                        + "    filho.codempresa = " + getLojaOrigem() + " and\n"
                        + "    filho.codprdprincipal is not null\n"
                        + "order by\n"
                        + "    filho.codprdprincipal, filho.codprd"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("idproduto"));
                        if ((rs.getString("codigobarras") == null)
                                || ("".equals(rs.getString("codigobarras")))) {
                            imp.setEan("99" + rs.getString("eaninterno"));
                        } else {
                            imp.setEan(rs.getString("codigobarras"));
                        }
                        imp.setPrecovenda(rs.getDouble("precovenda"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rs.getDouble("precoatacado") / rs.getInt("qtdembalagem"));
                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.codcfo id,\n"
                    + "    c.nome,\n"
                    + "    c.nomefantasia,\n"
                    + "    c.cgccfo cnpj,\n"
                    + "    c.inscrestadual ie,\n"
                    + "    c.rua endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.codetd uf,\n"
                    + "    c.cep, \n"
                    + "    c.telefone,\n"
                    + "    c.telefone2,\n"
                    + "    c.email,\n"
                    + "    c.limitecredito,\n"
                    + "    c.datacriacao,\n"
                    + "    c.datanasc,\n"
                    + "    c.nomemae,\n"
                    + "    c.nomepai,\n"
                    + "    c.ativo,\n"
                    + "    c.sexo,\n"
                    + "    c.estadocivil,\n"
                    + "    coalesce(obs.observacao, '') observacao,\n"
                    + "    tipo\n"
                    + "from\n"
                    + "    fcfo c\n"
                    + "left join fcfoobs obs on (c.codcfo = obs.codcfo)\n"
                    + "where\n"
                    + "    c.tipo in ('A', 'F') and\n"
                    + "    c.codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if ((rs.getString("telefone2")) != null && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("1", "TELEFONE", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setDatacadastro(rs.getDate("datacriacao"));
                    if ((rs.getString("ativo") != null) && (!"".equals(rs.getString("ativo")))) {
                        imp.setAtivo("T".equals(rs.getString("ativo").trim()));
                    }
                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if ((rs.getString("observacao") != null) && (!"".equals(rs.getString("observacao")))) {
                        imp.setObservacao(rs.getString("observacao"));
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
                    + "	c.codcfo id,\n"
                    + "	c.nome,\n"
                    + "	c.nomefantasia,\n"
                    + "	c.cgccfo cnpj,\n"
                    + "	c.inscrestadual ie,\n"
                    + " c.ci_numero rg,\n"
                    + "	c.rua endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.cidade,\n"
                    + "	c.codetd uf,\n"
                    + "	c.cep, \n"
                    + "	c.telefone,\n"
                    + "	c.telefone2,\n"
                    + "	c.email,\n"
                    + "	c.limitecredito,\n"
                    + "	c.datacriacao,\n"
                    + "	c.datanasc,\n"
                    + "	c.nomemae,\n"
                    + "	c.nomepai,\n"
                    + "	c.ativo,\n"
                    + "	c.sexo,\n"
                    + "	c.estadocivil,\n"
                    + "	coalesce(obs.observacao, '') observacao,\n"
                    + "   c.conjuge,\n"
                    + "	tipo\n"
                    + "from\n"
                    + "	fcfo c\n"
                    + "left join fcfoobs obs on (c.codcfo = obs.codcfo)\n"
                    + "where\n"
                    + "	c.tipo in ('A', 'C') and\n"
                    + "	c.codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    if ((rs.getString("rg") == null) && ("".equals(rs.getString("rg")))) {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setDataCadastro(rs.getDate("datacriacao"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    if ((rs.getString("telefone2")) != null && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("1", "TELEFONE", rs.getString("telefone2"), null, null);
                    }
                    if ((rs.getString("ativo") != null) && (!"".equals(rs.getString("ativo")))) {
                        imp.setAtivo("T".equals(rs.getString("ativo").trim()));
                    }
                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addContato("1", "EMAIL", null, null, rs.getString("email").trim());
                    }
                    if ((rs.getString("observacao") != null) && (!"".equals(rs.getString("observacao")))) {
                        imp.setObservacao(rs.getString("observacao").trim());
                    }
                    imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    if ((rs.getString("estadocivil") != null) && (!"".equals(rs.getString("estadocivil")))) {
                        switch (rs.getString("estadocivil").trim().toUpperCase()) {
                            case "C":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "A":
                                imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                break;
                            case "S":
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break;
                            case "V":
                                imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;
                            case "D":
                                imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
                    }
                    imp.setValorLimite(rs.getDouble("limitecredito"));
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
                    + "    idlan id,\n"
                    + "    codcfo idcliente,\n"
                    + "    codcaixa,\n"
                    + "    numerodocumento,\n"
                    + "    parcela,\n"
                    + "    dataemissao,\n"
                    + "    datavencimento,\n"
                    + "    historico,\n"
                    + "    valororiginal valor\n"
                    + "from\n"
                    + "    flan\n"
                    + "where\n"
                    + "    codempresa = " + getLojaOrigem() + " and\n"
                    + "    pagrec = 'R' and\n"
                    + "    statuslan = 'A' and\n"
                    + "    codtdo in ('DP', 'PROM', 'CH DEV')\n"        
                    + "order by\n"
                    + "    dataemissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setEcf(rs.getString("codcaixa"));
                    imp.setNumeroCupom(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("historico"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valororiginal"));

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
        return new TGADAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new TGADAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
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
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("documento")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("dataemissao"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("dataemissao")) + " " + rst.getString("horaemissao");
                        String horaTermino = timestampDate.format(rst.getDate("dataemissao")) + " " + rst.getString("horaemissao");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado("C".equals(rst.getString("status").trim()) ? true : false);
                        next.setSubTotalImpressora(rst.getDouble("valorliquido"));
                        next.setCpf(rst.getString("cnpj"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("rua")) + ","
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
                    = "select\n"
                    + "    max(idmov) id,\n"
                    + "    codcaixa ecf,\n"
                    + "    numeromov documento,\n"
                    + "    dataemissao,\n"
                    + "    max(m.codcfo) idcliente,\n"
                    + "    max(c.nome) nome,\n"
                    + "    max(c.cgccfo) cnpj,\n"
                    + "    max(c.rua) rua,\n"
                    + "    max(c.bairro) bairro,\n"
                    + "    max(c.numero) numero,\n"
                    + "    c.cidade,\n"
                    + "    c.codetd estado,\n"
                    + "    c.cep,\n"
                    + "    serie,\n"
                    + "    max(status) status,\n"
                    + "    m.datasaida,\n"
                    + "    max(valorliquido) valorliquido,\n"
                    + "    max(m.valordesc) desconto,\n"
                    + "    max(extract(hour from horarioemissao) ||':'||\n"
                    + "    extract(minute from horarioemissao) ||':'||\n"
                    + "    extract(second from horarioemissao)) horaemissao,\n"
                    + "    m.codtmv idtipomov,\n"
                    + "    tm.nome tipomov\n"
                    + "from\n"
                    + "    tmov m\n"
                    + "join ttipomov tm on (m.codtmv = tm.codtipomov)\n"
                    + "join fcfo c on (m.codcfo = c.codcfo)\n"
                    + "where\n"
                    + "    m.codempresa = " + idLojaCliente + "and\n"
                    + "    m.dataemissao between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n"
                    + "    m.codtmv in ('2.2.01', '2.2.03', '2.2.05', '2.3.01', '2.3.03')\n"
                    + "group by\n"
                    + "    codcaixa,\n"
                    + "    numeromov,\n"
                    + "    dataemissao,\n"
                    + "    c.cidade,\n"
                    + "    c.codetd,\n"
                    + "    c.cep,\n"
                    + "    serie,\n"
                    + "    m.datasaida,\n"
                    + "    m.codtmv,\n"
                    + "    tm.nome\n"
                    + "order by\n"
                    + "    m.dataemissao";
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
                        String id = rst.getString("idvenda") + "-"
                                + rst.getString("coo") + "-"
                                + rst.getString("ecf") + "-"
                                + rst.getString("dataemissao") + "-"
                                + rst.getInt("sequencia") + "-"
                                + rst.getDouble("valortotal");

                        next.setId(id);
                        next.setVenda(rst.getString("idvenda"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        if ((rst.getString("ean")) != null && (rst.getString("ean").length() > 14)) {
                            next.setCodigoBarras(rst.getString("ean").substring(2, rst.getString("ean").length()));
                        } else {
                            next.setCodigoBarras(rst.getString("ean"));
                        }
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setSequencia(rst.getInt("sequencia"));

                        double icms = 0;
                        int cst = 0;

                        if (rst.getString("idtributacao") != null) {
                            switch (rst.getString("idtributacao").trim()) {
                                case "T00":
                                    icms = 0.01;
                                    cst = 0;
                                case "T07":
                                    icms = 7.00;
                                    cst = 0;
                                case "T12":
                                    icms = 12.00;
                                    cst = 0;
                                case "T17":
                                    icms = 17.00;
                                    cst = 0;
                                case "II":
                                    icms = 0;
                                    cst = 40;
                                default:
                                    icms = 0;
                                    cst = 40;
                            }
                        }
                        next.setIcmsAliq(icms);
                        next.setIcmsCst(cst);
                        next.setIcmsReduzido(0);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    m.dataemissao,\n"
                    + "    coalesce(m.codcaixa, 77) ecf,\n"
                    + "    mv.idmov idvenda,\n"
                    + "    m.numeromov coo,\n"
                    + "    nseq sequencia,\n"
                    + "    mv.codprd idproduto,\n"
                    + "    p.nomefantasia descricaoreduzida,\n"
                    + "    p.codbarras ean,\n"
                    + "    p.unidade,\n"
                    + "    mv.quantidade,\n"
                    + "    mv.rateiodesc desconto,\n"
                    + "    precounitario,\n"
                    + "    m.dataemissao,\n"
                    + "    valortotalitem valortotal,\n"
                    + "    p.codsit idtributacao\n"
                    + "from\n"
                    + "    tmovitens mv\n"
                    + "join tmov m on (mv.idmov = m.idmov)\n"
                    + "join tproduto p on (mv.codprd = p.codprd)\n"
                    + "where\n"
                    + "    mv.codempresa = " + idLojaCliente + " and\n"
                    + "    m.dataemissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and\n"
                    + "    m.codtmv in ('2.2.01', '2.2.03', '2.2.05', '2.3.01', '2.3.03')\n"
                    + "order by\n"
                    + "    mv.idmov, mv.nseq";
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
