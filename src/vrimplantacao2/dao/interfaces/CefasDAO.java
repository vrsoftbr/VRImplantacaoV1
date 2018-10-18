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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.PlanoContasVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class CefasDAO extends InterfaceDAO {

    public String vPlanoContas;
    public boolean vBalanca = false;
    
    private static final Logger LOG = Logger.getLogger(CefasDAO.class.getName());

    @Override
    public String getSistema() {
        return "CEFAS";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    codfilial id,\n"
                    + "    nomefantasia,\n"
                    + "    cpfcnpj\n"
                    + "from \n"
                    + "    filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nomefantasia")));
                }
            }
            return result;
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    d.codepto merc1,\n"
                    + "    d.departamento descmerc1,\n"
                    + "    s.codsec merc2,\n"
                    + "    s.secao descmerc2,\n"
                    + "    coalesce(cast(c.codcateg as integer), 1) merc3,\n"
                    + "    decode(c.categoria, '', s.secao, c.categoria) descmerc3,\n"
                    + "    coalesce(cast(sc.codsubcateg as integer), 1) merc4,\n"
                    + "    decode(sc.subcategoria, '', decode(c.categoria, '', s.secao, c.categoria), sc.subcategoria) descmerc4\n"
                    + "from\n"
                    + "    depto d\n"
                    + "left join\n"
                    + "    secao s on s.codepto = d.codepto\n"
                    + "left join\n"
                    + "    categ c on c.codsec = s.codsec and\n"
                    + "    c.codepto = d.codepto\n"
                    + "left join\n"
                    + "    subcateg sc on sc.codcateg = c.codcateg and\n"
                    + "    sc.codsec = s.codsec and\n"
                    + "    sc.codepto = d.codepto\n"
                    + "order by\n"
                    + "    d.departamento, s.secao, c.categoria, sc.subcategoria")) {
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
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    p.codprod id,\n"
                    + "    p.descricao descricaocompleta,\n"
                    + "    p.abreviacao descricaoreduzida,\n"
                    + "    p.embalagem,\n"
                    + "    p.codbarra codigobarras,\n"
                    + "    p.codepto merc1,\n"
                    + "    p.codsec merc2,\n"
                    + "    decode(p.codcat, '', '1', p.codcat) merc3,\n"
                    + "    decode(p.codsubcat, '', '1', p.codsubcat) merc4,\n"
                    + "    p.dtcadastro,\n"
                    + "    p.unidade,\n"
                    + "    p.qtunitcx qtdcaixa,\n"
                    + "    p.qtunit qtdunidade,\n"
                    + "    p.peso,\n"
                    + "    p.clafiscal ncm,\n"
                    + "    p.prazovalid validade,\n"
                    + "    e.custoreal custo,\n"
                    + "    em.margem,\n"
                    + "    pre.pvenda venda,\n"
                    + "    pisentrada.cstpis pisentrada,\n"
                    + "    pissaida.cstpis pissaida,\n"
                    + "    pissaida.cest,\n"
                    + "    t.aliqicms icmsdebito,\n"
                    + "    t.sittribut cst,\n"
                    + "    t.perbasered redicms,\n"
                    + "    e.qtest estoque,\n"
                    + "    e.qtestmin estoqueminimo,\n"
                    + "    dtexclusao excluido, --campo null nao excluido, campo not null excluido\n"
                    + "    p.codfornecprinc fornprincipal\n"        
                    + "from\n"
                    + "    produto p \n"
                    + "left join\n"
                    + "    preco pre on pre.codprod = p.codprod\n"
                    + "join\n"
                    + "    tributacao t on t.codtribut = pre.codtribut\n"
                    + "left join\n"
                    + "    prodmelo pm on pm.codbarra = p.codbarra\n"
                    + "left join\n"
                    + "    embalagem em on em.codprod = p.codprod\n"
                    + "left join\n"
                    + "    estoque e on e.codprod = p.codprod\n"
                    + "left join\n"
                    + "    (select\n"
                    + "        pis.cstpis,\n"
                    + "        pis.cest,\n"
                    + "        pis.codprod\n"
                    + "    from\n"
                    + "        cadncmpiscofins pis\n"
                    + "    where\n"
                    + "        pis.operacao = 'E') pisentrada on p.codprod = pisentrada.codprod\n"
                    + "left join\n"
                    + "    (select\n"
                    + "        pis.cstpis,\n"
                    + "        pis.cest,\n"
                    + "        pis.codprod\n"
                    + "    from\n"
                    + "        cadncmpiscofins pis\n"
                    + "    where\n"
                    + "        pis.operacao = 'S') pissaida on p.codprod = pissaida.codprod\n"
                    + "where \n"
                    + "    pre.numregiao = 1\n"
                    + "order by\n"
                    + "    p.codprod")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setQtdEmbalagem(rs.getInt("qtdunidade"));
                    imp.setPesoLiquido(rs.getDouble("peso"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("pisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pissaida"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmsdebito"));
                    imp.setIcmsCstSaida(rs.getInt("cst"));
                    imp.setIcmsCstEntrada(rs.getInt("cst"));
                    imp.setIcmsReducaoSaida(rs.getInt("redicms"));
                    imp.setIcmsReducaoEntrada(rs.getInt("redicms"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    if (rs.getDate("excluido") == null) {
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }
                    if((rs.getString("codigobarras") != null) && 
                            (rs.getString("codigobarras").length() <= 6) && 
                                ("KG".equals(rs.getString("unidade").trim()))) {
                        if(vBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getEan().trim());
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
                            imp.setValidade(rs.getInt("validade"));
                        }  
                    }
                    imp.setFornecedorFabricante(rs.getString("fornprincipal"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    codfornec idfornecedor,\n"
                    + "    codprod idproduto,\n"
                    + "    codprodfor referencia\n"
                    + "from \n"
                    + "    prodfornec\n"
                    + "where\n"
                    + "    codprod != 0\n"
                    + "order by\n"
                    + "    codprod, codfornec")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("referencia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codfornec id,\n"
                    + "    fornecedor razaosocial,\n"
                    + "    fantasia,\n"
                    + "    cpfcnpj,\n"
                    + "    ie,\n"
                    + "    endereco,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    codmunicipio,\n"
                    + "    estado,\n"
                    + "    cep,\n"
                    + "    telefone,\n"
                    + "    fax,\n"
                    + "    email,\n"
                    + "    email2,\n"
                    + "    contato,\n"
                    + "    dtcadastro,\n"
                    + "    bloqueio,\n"
                    + "    obs,\n"
                    + "    prazoent,\n"
                    + "    repres,\n"
                    + "    telefone2,\n"
                    + "    endercob,\n"
                    + "    bairrocob,\n"
                    + "    cidadecob,\n"
                    + "    estcob,\n"
                    + "    cepcob,\n"
                    + "    telcob,\n"
                    + "    prazo1,\n"
                    + "    prazo2,\n"
                    + "    prazo3\n"
                    + "from\n"
                    + "    fornecedor\n"
                    + "order by\n"
                    + "    codfornec")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("codmunicipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if ((rs.getString("fax") != null) && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.FINANCEIRO, null);
                    }
                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.FINANCEIRO, rs.getString("email"));
                    }
                    if ((rs.getString("telefone2") != null) && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("3", "TELEFONE2", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("email2") != null) && (!"".equals(rs.getString("email2")))) {
                        imp.addContato("2", "EMAIL2", null, null, TipoContato.FINANCEIRO, rs.getString("email2"));
                    }
                    imp.setDatacadastro(rs.getDate("dtcadastro"));
                    imp.setAtivo("S".equals(rs.getString("bloqueio")) ? false : true);
                    if ((rs.getString("obs") != null) && (!"".equals(rs.getString("obs")))) {
                        imp.setObservacao(rs.getString("obs"));
                    }

                    imp.setPrazoEntrega(rs.getInt("prazoent"));
                    if ((rs.getString("repres") != null) && (!"".equals(rs.getString("repres")))) {
                        imp.addContato("REPRESENTANTE", null, null, TipoContato.COMERCIAL, null);
                    }

                    if ((rs.getString("endercob") != null) && (!"".equals(rs.getString("endercob")))) {
                        imp.setCob_endereco(rs.getString("endercob"));
                    }
                    if ((rs.getString("bairrocob") != null) && (!"".equals(rs.getString("bairrocob")))) {
                        imp.setCob_bairro(rs.getString("bairrocob"));
                    }
                    if ((rs.getString("cidadecob") != null) && (!"".equals(rs.getString("cidadecob")))) {
                        imp.setCob_municipio(rs.getString("cidadecob"));
                    }
                    if ((rs.getString("estcob") != null) && (!"".equals(rs.getString("estcob")))) {
                        imp.setCob_uf(rs.getString("estcob"));
                    }
                    if ((rs.getString("cepcob") != null) && (!"".equals(rs.getString("cepcob")))) {
                        imp.setCob_cep(rs.getString("cepcob"));
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
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codcli id,\n"
                    + "    cliente razaosocial,\n"
                    + "    fantasia,\n"
                    + "    cpfcnpj,\n"
                    + "    ie,\n"
                    + "    bloq,\n"
                    + "    dtcadastro,\n"
                    + "    limcred,\n"
                    + "    obs,\n"
                    + "    endereco,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    codmunicipio,\n"
                    + "    estado,\n"
                    + "    cep,\n"
                    + "    telefone,\n"
                    + "    email,\n"
                    + "    dtnasc,\n"
                    + "    contato,\n"
                    + "    telcontato2,\n"
                    + "    enderent,\n"
                    + "    bairroent,\n"
                    + "    cidadeent,\n"
                    + "    estadoent,\n"
                    + "    cepent,\n"
                    + "    telent\n"
                    + "from\n"
                    + "    cliente\n"
                    + "order by\n"
                    + "    codcli")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setAtivo("S".equals(rs.getString("bloq")) ? false : true);
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setValorLimite(rs.getDouble("limcred"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("codmunicipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("dtnasc"));
                    if ((rs.getString("contato") != null) && (!"".equals(rs.getString("contato")))) {
                        imp.addContato("1", rs.getString("contato"), null, null, null);
                    }
                    if ((rs.getString("telcontato2") != null) && (!"".equals(rs.getString("telcontato2")))) {
                        imp.addContato("2", "TEL2", rs.getString("telcontato2"), null, null);
                    }
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    c.numvenda id,\n"
                    + "    c.numnota coo,\n"
                    + "    c.prest parcela,\n"
                    + "    c.codcli idcliente,\n"
                    + "    cli.cpfcnpj,\n"
                    + "    coalesce(cli.fantasia, cli.cliente) razao,\n"
                    + "    c.numcx ecf,\n"
                    + "    to_char(c.dtemissao, 'yyyy-MM-dd') dtemissao,\n"
                    + "    to_char(c.dtvenc, 'yyyy-MM-dd') dtvencimento,\n"
                    + "    c.valor,\n"
                    + "    c.vljuro,\n"
                    + "    cob.codcob,\n"
                    + "    cob.descricao\n"
                    + "from\n"
                    + "    creceber c, cobranca cob, cliente cli\n"
                    + "where\n"
                    + "    c.codcob = cob.codcob and\n"
                    + "    c.codcli = cli.codcli and\n"
                    + "    status = 'A' and\n"
                    + "    c.vpago is null and\n"
                    + "    cob.codcob = '" + vPlanoContas + "' \n"
                    + "order by\n"
                    + "    c.dtvenc")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cpfcnpj"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("vljuro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                      "SELECT c.numvenda ID, c.numnota coo, c.prest parcela, c.codcli idcliente, c.vljuro,\n" +
                    "         cli.cpfcnpj, COALESCE (cli.fantasia, cli.cliente) razao, c.numcx ecf,\n" +
                    "         TO_CHAR (c.dtemissao, 'yyyy-MM-dd') dtemissao,\n" +
                    "         TO_CHAR (c.dtvenc, 'yyyy-MM-dd') dtvencimento, c.valor, cob.codcob,\n" +
                    "         cob.descricao, c.obs, c.obs2, c.numch cheque, c.numag agencia, c.numbco banco, c.numconta conta\n" +
                    "    FROM creceber c, cobranca cob, cliente cli\n" +
                    "   WHERE c.codcob = cob.codcob\n" +
                    "     AND c.codcli = cli.codcli\n" +
                    "     AND status = 'A'\n" +
                    "     AND c.vpago = 0\n" +
                    "     AND cob.codcob = '" + vPlanoContas + "'\n" +
                    "     AND c.valor > 0\n" +
                    "ORDER BY c.dtvenc")) {
                while(rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCpf(rs.getString("cpfcnpj"));
                    imp.setNumeroCheque(rs.getString("cheque"));
                    imp.setNome(rs.getString("razao"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setValorJuros(rs.getDouble("vljuro"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDate(rs.getDate("dtemissao"));
                    imp.setDataDeposito(rs.getDate("dtvencimento"));
                    imp.setEcf(rs.getString("ecf"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<PlanoContasVO> getPlanoContas() throws Exception {
        List<PlanoContasVO> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    codcob,\n"
                    + "    descricao,\n"
                    + "    txjuro\n"
                    + "from \n"
                    + "    cobranca\n"
                    + "order by\n"
                    + "    descricao")) {
                while (rs.next()) {
                    result.add(new PlanoContasVO(rs.getString("codcob"), rs.getString("descricao")));
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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoOracle.getConexao().createStatement();
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("dtemissao"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("dtemissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("dtemissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("vltotal"));
                        next.setCpf(rst.getString("cpfcnpj"));
                        next.setValorDesconto(rst.getDouble("vldesconto"));
                        next.setNomeCliente(rst.getString("razaosocial"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
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
                    = "select \n" +
                    "    n.numvenda id,\n" +
                    "    n.numped pedido,\n" +
                    "    n.numcx ecf,\n" +
                    "    n.numcupom coo,\n" +
                    "    n.dtemissao,\n" +
                    "    n.dtsaida,\n" +
                    "    n.codcli idcliente,\n" +
                    "    c.cliente razaosocial,\n" +
                    "    to_char(n.dtemissao, 'HH24:MI:SS') horainicio,\n" +
                    "    to_char(n.dtsaida, 'HH24:MI:SS') horatermino,\n" +
                    "    c.cpfcnpj,\n" +
                    "    c.ie,\n" +
                    "    c.endereco,\n" +
                    "    c.bairro,\n" +
                    "    c.cidade,\n" +
                    "    ci.estado, \n" +
                    "    c.cep,\n" +
                    "    n.vloutras,\n" +
                    "    n.vldesconto,\n" +
                    "    n.vltotal,\n" +
                    "    n.obs,\n" +
                    "    case when dtcancel is not null then 1 else 0 end cancelado\n" +
                    "from \n" +
                    "    nfsaid n\n" +
                    "left join\n" +
                    "    cliente c on n.codcli = c.codcli\n" +
                    "left join\n" +
"                        cidade ci on c.codmunicipio = ci.codmunicipio\n" +
                    "where \n" +
                    "    n.especie = 'CE' and\n" +
                    "    TO_CHAR (n.dtemissao, 'yyyy-MM-dd') between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' \n" +
                    "order by\n" +
                    "    n.dtemissao";
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

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("coo"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsCst(rst.getInt("cst"));
                        next.setIcmsAliq(rst.getDouble("aliqicms"));
                        next.setIcmsReduzido(rst.getDouble("icmsred"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select \n" +
                    "    m.id,\n" +
                    "    m.numvenda coo,\n" +
                    "    m.numped pedido,\n" +
                    "    m.dtmov dtemissao,\n" +
                    "    m.codprod idproduto,\n" +
                    "    p.descricao, \n" +
                    "    m.codbarra ean,\n" +
                    "    m.unorig unidade,\n" +
                    "    m.seq sequencia,\n" +
                    "    m.qtorig quantidadeoriginal,\n" +
                    "    m.qt quantidade,\n" +
                    "    m.qtcont quantidadeacumulada,\n" +
                    "    m.custocont custo,\n" +
                    "    m.custocontant custoacumulado,\n" +
                    "    m.custofin custofinal,\n" +
                    "    m.ptabela,\n" +
                    "    round(m.qt * m.ptabela, 2) valortotal, \n" +
                    "    m.vldesc desconto, \n" +
                    "    m.punit,\n" +
                    "    m.punitcont,\n" +
                    "    m.sittribut cst,\n" +
                    "    t.aliqicms,\n" +
                    "    t.perbasered icmsred,\n" +
                    "    m.numnota,\n" +
                    "    m.unorig,\n" +
                    "    m.cstpis,\n" +
                    "    m.cstcofins\n" +
                    "from \n" +
                    "    movimentacao m\n" +
                    "left join\n" +
                    "    tributacao t on m.codtribut = t.codtribut\n" +
                    "join\n" +
                    "    nfsaid nf on m.numvenda = nf.numvenda\n" +
                    "join " +
                    "    produto p ON m.codprod = p.codprod\n" +
                    "where \n" +
                    "    to_char(m.dtmov, 'yyyy-MM-dd') between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and\n" +
                    "    nf.especie = 'CE'\n" +
                    "order by\n" +
                    "    m.dtmov";
            LOG.log(Level.FINE, "SQL da venda: " + sql)
                    ;
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
