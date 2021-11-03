package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.*;
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
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class ItuInfoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ItuInfo";
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
                    "select\n"
                    + "	código as id,\n"
                    + "	fantasia descricao,\n"
                    + "	cnpj\n"
                    + "from\n"
                    + "	sistema"
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
                    "select\n"
                    + "	codigo as codmerc1,\n"
                    + "	setor as descmerc1,\n"
                    + "	codigo as codmerc2,\n"
                    + "	setor as descmerc2,\n"
                    + "	codigo as codmerc3,\n"
                    + "	setor as descmerc3\n"
                    + "from\n"
                    + "	setor\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

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
                    + "	proCodDepartamento AS cod_mercadologico1,\n"
                    + "	proCodDepartamento AS cod_mercadologico2,\n"
                    + " proCodDepartamento AS cod_mercadologico3,\n"
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
                    "select\n"
                    + "	id idproduto,\n"
                    + "	codigobarra ean,\n"
                    + "	1 as qtdeembalagem\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdeembalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo as id,\n"
                    + "	razao,\n"
                    + "	cnpj as cnpj_cpf,\n"
                    + "	ie as ie_rg,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	municipio,\n"
                    + "	uf,\n"
                    + "	cep,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	obs as observacao\n"
                    + "from\n"
                    + "	fornecedor\n"
                    + "order by 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setObservacao(rs.getString("observacao"));

                    String email = rs.getString("email");
                    if ((email) != null && (!"".equals(email))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo as id,\n"
                    + "	nome as razao,\n"
                    + "	cnpj as cpfcnpj,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	uf,\n"
                    + "	cep,\n"
                    + "	telefone as telefone1,\n"
                    + "	celular,\n"
                    + "	bloqueado,\n"
                    + "	obs\n"
                    + "from\n"
                    + "	clientecheque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cpfcnpj"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setObservacao(rst.getString("obs"));

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
                    + "    left join tbclientes c on cr.crpcodcliente = c.cliId\n"
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

    public List<ChequeIMP> getCheque() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	emissao,\n"
                    + "	vencimento datadeposito,\n"
                    + "	nvenda num_cupom,\n"
                    + "	ncheque num_cheque,\n"
                    + "	banco,\n"
                    + "	agencia,\n"
                    + "	conta,\n"
                    + "	telefone,\n"
                    + "	cpf,\n"
                    + "	cliente nome,\n"
                    + "	rg,\n"
                    + "	valorcheque,\n"
                    + "	celular observacao\n"
                    + "from\n"
                    + "	chequepre \n"
                    + "order by 1")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDate(rs.getDate("emissao"));
                    imp.setDataDeposito(rs.getDate("datadeposito"));
                    imp.setNumeroCupom(rs.getString("num_cupom"));
                    imp.setNumeroCheque(rs.getString("num_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCpf(rs.getString("cpf"));
                    imp.setNome(rs.getString("nome"));
                    imp.setRg(rs.getString("rg"));
                    imp.setValor(rs.getDouble("valorcheque"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
