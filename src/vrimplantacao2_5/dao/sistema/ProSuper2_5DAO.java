package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Bruno
 */
public class ProSuper2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ProSuper";
    }

    /*
    Arquivos utilizados: 
    EAN = escodbar,
    Mercadologicos = esclassi,
    Produto e afins = estoque,
    Fornecedor = focadfor,
    Script Loja origem = ccconfig,
    Cliente = ccclient,
    CredRotativo = cocadrec
    
     */
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ATIVO,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.NCM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.PRECO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DADOS,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                     "select distinct \n"
                    + "tqaliicm||tqtribut||tqsubsti as id,\n"
                    + "tqtribut||tqaliicm as descricao,\n"
                    + "tqaliicm  as aliq,\n"
                    + "tqsubsti as reducao, \n"
                    + "tqtribut as cst \n"
                    + "from \n"
                    + "estoque "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
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
                    + "distinct \n"
                    + "m.elcodigo as merc1,\n"
                    + "tqgrupo as desc1 ,\n"
                    + "m1.elcodigo as merc2 ,\n"
                    + "tqsgrupo as desc2 \n"
                    + "from\n"
                    + "estoque e\n"
                    + "join esclassi m on m.eldescri = e.tqgrupo \n"
                    + "join esclassi m1 on m1.eldescri = e.tqsgrupo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc2"));
                    imp.setMerc3Descricao(rst.getString("desc2"));

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
                    "select\n"
                    + "	distinct \n"
                    + "	tqid as id_produto,\n"
                    + "	e.tqobserv as descricaocompleta,\n"
                    + "	tqdescri as descricaoreduzida,\n"
                    + "	e.tqncm as cod_ncm,\n"
                    + "	tqcest as cest,\n"
                    + "	case \n"
                    + "		when tqativo = 'S' then 1\n"
                    + "		when tqativo = 'N' then 0\n"
                    + "		else 1\n"
                    + "	end as ativo,\n"
                    + "	tqqtdmax as estoquemaximo,\n"
                    + "	tqqtdmin as estoqueminimo,\n"
                    + "	tqsaldo as estoque,\n"
                    + "	m.elcodigo as merc1,\n"
                    + "	m.elcodigo as merc2 ,\n"
                    + "	tqpreven as preco,\n"
                    + "	tqprecus as custo,\n"
                    + "	tqlucro as margem,\n"
                    + "	tqnatise as nat_receita,\n"
                    + "	tqaliicm||tqtribut||tqsubsti as icms,\n"
                    + "	tqcstcof as cst_cofins,\n"
                    + "	tqcstpie as cst_pis_entrada\n"
                    + "from\n"
                    + "	estoque e\n"
                    + "left join escodbar barra on barra.cbcodigo = e.tqcodigo\n"
                    + "left join esclassi m on m.eldescri = e.tqgrupo"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id_produto"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.seteBalanca(false);
                        //imp.setTipoEmbalagem(rst.getString("UN"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(1);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida").isEmpty()
                            ? imp.getDescricaoCompleta()
                            : rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setNcm(rst.getString("cod_ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));

                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setMargem(rst.getDouble("margem"));

                    String idIcms = rst.getString("icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    imp.setPiscofinsCstDebito(rst.getString("cst_cofins"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_receita"));

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
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtd"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cbcodigo as id_produto,\n"
                    + "	cbcodbar as ean\n"
                    + "from\n"
                    + "	escodbar\n"
                    + "	order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));

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
                    "select\n"
                    + "	focodigo as id_fornecedor,\n"
                    + "	forazao as razao,\n"
                    + "	fofantasia as fantasia,\n"
                    + "	focgc as cnpj,\n"
                    + "	foinsest as ie,\n"
                    + "	foendereco as endereco,\n"
                    + "	fonumero as numero,\n"
                    + "	fobairro as bairro,\n"
                    + "	focidade as municipio,\n"
                    + "	foativo as ativo,\n"
                    + "	foestado as uf,\n"
                    + "	focep as cep,\n"
                    + "	fodatcad as data_cadastro,\n"
                    + "	foobservac as observacoes,\n"
                    + "	fofone1 \n"
                    + "from\n"
                    + "	focadfor "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_fornecedor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacoes"));

                    imp.setTel_principal(rst.getString("fofone1"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	conumdoc as id, \n"
                    + "	coclient as id_fornecedor,\n"
                    + "	conumdoc as documento,\n"
                    + "	codtdcto as emissao,\n"
                    + "	codtvcto as vencimento,\n"
                    + "	covalor as valor,\n"
                    + "	cocomple as observacao\n"
                    + "from\n"
                    + "	cocadcta\n"
                    + "where\n"
                    + "	costatus <> 'PAG'"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("emissao"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

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
                    "select\n"
                    + "	clcodigo as id,\n"
                    + "	clrazao as razao,\n"
                    + "	clnome as fantasia,\n"
                    + "	clnumero as numero,\n"
                    + "	clendereco as endereco,\n"
                    + "	clbairro as bairro,\n"
                    + "	clcep as cep,\n"
                    + "	clcidade as cidade,\n"
                    + "	clativo as status,\n"
                    + "	cltel1 as contato,\n"
                    + "	clestado as uf,\n"
                    + "	clobs1 as email,\n"
                    + "	clcpf as cpf_cnpj,\n"
                    + "	cltel1 as fone,\n"
                    + "	clinsest as inscricaoest\n"
                    + "	from\n"
                    + "	ccclient "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setObservacao(rst.getString("contato"));

                    imp.setUf(rst.getString("uf"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoest"));
                    imp.setTelefone(rst.getString("fone"));

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
                    "select\n"
                    + "	conumdoc as id, \n"
                    + "	coclient as id_cliente,\n"
                    + "	conumdoc as nota,\n"
                    + "	codtdcto as data_venda,\n"
                    + "	codtvcto as vencimento,\n"
                    + "	covalor as valor,\n"
                    + "	codtpgto \n"
                    + "from\n"
                    + "	cocadrec\n"
                    + "	where costatus = 'ABE'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("nota"));
//imp.setParcela(rst.getInt("parcela") == 0 ? 1 : rst.getInt("parcela"));
                    imp.setDataEmissao(rst.getDate("data_venda"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
