package vrimplantacao2.dao.cadastro;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class Stock_PostgresDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Stock";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.RAZAO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CEP,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	triid as id,\n"
                    + "	tridestributo as descricao,\n"
                    + "	tricstcsosnsaida as csticms,\n"
                    + "	trivalortributacao as aliquotaicms,\n"
                    + "	0 as reducaoicms\n"
                    + "FROM\n"
                    + "	tbtributacoes"
            )) {
                while (rs.next()) {
                    String id = getAliquotaKey(
                            rs.getString("csticms"),
                            rs.getDouble("aliquotaicms"),
                            rs.getDouble("reducaoicms")
                    );
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rs.getString("csticms")),
                            rs.getDouble("aliquotaicms"),
                            rs.getDouble("reducaoicms")
                    ));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	empid as id,\n"
                    + "	empnome as descricao\n"
                    + "FROM\n"
                    + "	tbempresas"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " g.depid merc1,\n"
                    + " g.depdesdepartamento desc1,\n"
                    + " case when sg.subgrcodigointerno is null then g.depid \n"
                    + "      else sg.subgrcodigointerno end merc2,\n"
                    + " case when sg.subgrdescricao is null then g.depdesdepartamento\n"
                    + "      else sg.subgrdescricao end desc2,\n"
                    + " case when sg.subgrcodigointerno is null then g.depid \n"
                    + "      else sg.subgrcodigointerno end merc3,\n"
                    + " case when sg.subgrdescricao is null then g.depdesdepartamento\n"
                    + "      else sg.subgrdescricao end desc3  \n"
                    + "from tbgrupos g\n"
                    + "left join tbsubgrupo sg on sg.subgrcodigo = g.depid\n"
                    + "order by 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " p.proid idproduto,\n"
                    + " f.forid idfornecedor\n"
                    + "from tbprodutos p\n"
                    + "join tbfornecedores f on f.fordesfornecedor = p.profornecedorrec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	proid AS id,\n"
                    + "	procodbarras AS codigobarras,\n"
                    + "	pronomproduto AS descricaocompleta,\n"
                    + "	proabrproduto AS descricaoreduzida,\n"
                    + "	prodesunidade AS unidade,\n"
                    + "	provalprecovenda AS precovenda,\n"
                    + "	provalcusto AS custosemimposto,\n"
                    + " provalcusto AS custocomimposto,\n"
                    + "	prolucro AS margem,\n"
                    + "	proqntminima AS estoqueminimo,\n"
                    + "	proqntestoque AS estoque,\n"
                    + "	procoddepartamento AS cod_mercadologico1,\n"
                    + "	procodsubgrupo AS cod_mercadologico2,\n"
                    + " procodsubgrupo AS cod_mercadologico3,\n"
                    + "	propeso AS pesobruto,\n"
                    + "	prodataalterado AS dataalteracao,\n"
                    + "	proncm AS ncm,\n"
                    + "	procest AS cest,\n"
                    + "	proflagbalanca AS balanca,\n"
                    + "	procodtributo as icms_credito_id,\n"
                    + "	procodtributo as icms_credito_foraestado_id,\n"
                    + "	procodtributo as icms_debito_id,\n"
                    + "	procodtributo as icms_debito_foraestado_id,\n"
                    + "	procodtributo as icms_debito_foraestadonf_id,\n"
                    + "	procodtributo as icms_consumidor_id,\n"
                    + "	p.procst_entrada AS piscofins_cst_credito,\n"
                    + "	p.procst_saida AS piscofins_cst_debito,\n"
                    + "	procodnatreceita AS piscofins_natureza_receita,\n"
                    + " tricstcsosnsaida as csticms,\n"
                    + "	trivalortributacao as aliquotaicms,\n"
                    + " 0 reducaoicms\n"
                    + "FROM\n"
                    + "	tbprodutos p,\n"
                    + "	tbGrupos m,\n"
                    + "	tbtributacoes t,\n"
                    + "	tbPisCofins pc\n"
                    + "WHERE\n"
                    + "	p.procodtributo = t.triid\n"
                    + "	AND m.depid = p.proCodDepartamento\n"
                    + "	AND p.proCodPisCofins = pc.pisId\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.seteBalanca(rst.getBoolean("balanca"));

                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_credito"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));

                    String idIcms = getAliquotaKey(
                            rst.getString("csticms"),
                            rst.getDouble("aliquotaicms"),
                            rst.getDouble("reducaoicms")
                    );

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "Select\n"
                    + " proid as idproduto,\n"
                    + " procodbarras as ean,\n"
                    + " 1 as qtdembalagem \n"
                    + "from tbprodutos\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	forId as id,\n"
                    + "	forDesFornecedor as razao,\n"
                    + "	forAbrFornecedor as fantasia,\n"
                    + "	forCGC as cnpj,\n"
                    + "	forInsc as ie,\n"
                    + "	forEndereco as endereco,\n"
                    + "	fornro as numero,\n"
                    + "	forcomplemento as complemento,\n"
                    + "	forBairro as bairro,\n"
                    + "	forCidade as cidade,\n"
                    + "	forEstado as uf,\n"
                    + "	forCEP as cep,\n"
                    + "	forEmail as email,\n"
                    + "	forTelefone as fone,\n"
                    + "	forFax as fax,\n"
                    + "	forObservacao as obs\n"
                    + "FROM\n"
                    + "	tbFornecedores"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));

                    if (rst.getString("email") != null && !rst.getString("email").trim().isEmpty()) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
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
                    "SELECT\n"
                    + "    cliid as id,\n"
                    + "    clinomcliente as nome,\n"
                    + "    clifantasia as fantasia,\n"
                    + "    clidatanascimento as data_nasc,\n"
                    + "    clicpf_cgc as cpfcnpj,\n"
                    + "    clirg_ie as rg_ie,\n"
                    + "    cliendereco as endereco,\n"
                    + "    clinro as numero,\n"
                    + "    clibairro as bairro,\n"
                    + "    clicomplemento as complemento,\n"
                    + "    clicidade as cidade,\n"
                    + "    cliestado as uf,\n"
                    + "    clicep as cep,\n"
                    + "    clitelefone as telefone,\n"
                    + "    cliemail as email,\n"
                    + "    clifax as celular,\n"
                    + "    clinomeempresa as empresa,\n"
                    + "    clitelefonetrabalho as fone_empresa,\n"
                    + "    clidesde as data_cadastro,\n"
                    + "    cliflagbloqueado as bloqueado,\n"
                    + "    clilimitecompra as limite,\n"
                    + "    clidiavencconta as dia_vencimento,\n"
                    + "    clipai as nomepai,\n"
                    + "    climae as nomemae,\n"
                    + "    cliconjuge as nome_conju,\n"
                    + "    clicpfconjuge as cpf_conju,\n"
                    + "    clinascconjuge as nasc_conju,\n"
                    + "    clitelefone as observacao\n"
                    + "FROM\n"
                    + "    tbclientes\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setDataNascimento(rst.getDate("data_nasc"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("fone_empresa"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setDiaVencimento(rst.getInt("dia_vencimento"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nome_conju"));
                    imp.setCpfConjuge(rst.getString("cpf_conju"));
                    imp.setDataNascimentoConjuge(rst.getDate("nasc_conju"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    crpnrolancamento as id,\n"
                    + "    crpdatalancamento as emissao,\n"
                    + "    crpnumdocumento as cupom,\n"
                    + "    crpnrocaixa as ecf,\n"
                    + "    crpvalorlancamento as valor,\n"
                    + "    crpcodcliente as idcliente,\n"
                    + "    c.clicpf_cgc as cnpjcpf,\n"
                    + "    crpvencimentoconta as vencimento,\n"
                    + "    crpdeslancamento as obs \n"
                    + "FROM    \n"
                    + "    tbcontasreceberpagar cr\n"
                    + "    join tbclientes c on cr.crpcodcliente = c.cliId\n"
                    + "where\n"
                    + "    crpdatapagamento is null\n"
                    + "    and crpflaglancado = false\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setCnpjCliente(rst.getString("cnpjcpf"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
