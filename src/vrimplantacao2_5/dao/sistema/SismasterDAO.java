package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

/**
 *
 * @author Michael
 */
public class SismasterDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "SISMASTER";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
            OpcaoProduto.VOLUME_QTD,
            OpcaoProduto.CUSTO,
            OpcaoProduto.CUSTO_COM_IMPOSTO,
            OpcaoProduto.CUSTO_SEM_IMPOSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_CONSUMIDOR,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VR_ATACADO,
            OpcaoProduto.VENDA_PDV,
            OpcaoProduto.PDV_VENDA,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.FABRICANTE,
            OpcaoProduto.RECEITA,
            OpcaoProduto.PROMOCAO,
            OpcaoProduto.ATACADO,
            OpcaoProduto.ASSOCIADO
        }));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CELULAR,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.OUTRAS_RECEITAS,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.SEXO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.MUNICIPIO
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	t.codgrandesgrupos merc1,\n"
                    + "	g.nome desc1,\n"
                    + "	t.grupoproduto merc2,\n"
                    + "	gp.grupo desc2,\n"
                    + "	t.grupoproduto merc3,\n"
                    + "	gp.grupo desc3\n"
                    + "from\n"
                    + "	tabprodutos t\n"
                    + "left join tabgrandesgrupos g on t.codgrandesgrupos = g.codgrandes\n"
                    + "left join tabgruposproduto gp on t.grupoproduto = gp.codgrupo"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codmarca codigofamilia, marca descricao from tabmarcas"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigofamilia"));
                    imp.setDescricao(rst.getString("descricao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {

        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.codproduto Id,\n"
                    + "	p.DATACAD dataCadastro,\n"
                    + "	p.dataultimaatualizacao dataAlteracao,\n"
                    + "	p.CODBARRAS4 ean,\n"
                    + "	p.UNIDADE tipoEmbalagem,\n"
                    + "	p.UNIDADE tipoEmbalagemCotacao,\n"
                    + "	p.ENVIAR_PROD_PARA_BALANCA e_balanca,\n"
                    + "	p.nomeproduto  descricaoCompleta,\n"
                    + "	p.NOMEREDU descricaoReduzida,\n"
                    + "	p.codgrandesgrupos merc1,\n"
                    + "	p.grupoproduto merc2,\n"
                    + "	p.grupoproduto merc3,\n"
                    + "	p.marcas familia,\n"
                    + "	p.PESOT pesoBruto,\n"
                    + "	p.PESOT pesoLiquido,\n"
                    + "	p.ESTOQUE estoque,\n"
                    + "	p.VENDAMAX_POR margem,\n"
                    + "	p.VALCOMPRA custoSemImposto,\n"
                    + "	p.CUSTOCOMPRA custoComImposto,\n"
                    + "	p.VENDA precovenda,\n"
                    + "	p.VENDAMIN precoatacado,\n"
                    + "	p.ativo situacaoCadastro,\n"
                    + "	n.cod_ncm ncm,\n"
                    + "	p.VALIDADE_DIAS validade,\n"
                    + "	p.tipo_cst_pis piscofinsCstDebito,\n"
                    + "	p.tipo_cst_cofins piscofinsCstCredito,\n"
                    + "	p.CST_COMPRA Cst,\n"
                    + "	trib.codigo idicms,\n"
                    + "	nat.codigo piscofinsNaturezaReceita\n"
                    + "from\n"
                    + "	tabprodutos p \n"
                    + "left join tabncm n on p.ncm = n.codigo\n"
                    + "left join tabprodutostrib tp on p.codproduto = tp.codproduto\n"
                    + "left join tabgrupotributacao trib on tp.codgrupotrib = trib.codigo\n"
                    + "left join tabnatureza nat on p.CFOP_COMPRA = nat.CFOP"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setTipoEmbalagemVolume(rst.getString("tipoEmbalagem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    //imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsCstCredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsCstDebito"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setDataAlteracao(rst.getDate("dataAlteracao"));
                    imp.setEan(rst.getString("ean"));

                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoEmbalagemCotacao"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    //imp.setSituacaoCadastro(rst.getInt("situacaoCadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
                    //imp.setFornecedorFabricante(rst.getString("fornec"));
                    imp.setValidade(rst.getInt("validade"));
                    //imp.setIdFamiliaProduto(rst.getString("familia"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setIcmsDebitoId(rst.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idicms"));
                    imp.setIcmsCreditoId(rst.getString("idicms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idicms"));
                    imp.setIcmsConsumidorId(rst.getString("idicms"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from tabgrupotributacao t "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	p.codproduto Id,\n"
                        + "	p.codbarras2 ean,\n"
                        + "	p.VENDA precovenda,\n"
                        + "	p.VENDAMIN precoatacado,\n"
                        + "	p.UNIDADE tipoEmbalagem,\n"
                        + "	p.UNIDADE tipoEmbalagemCotacao\n"
                        + "from tabprodutos p\n"
                        + "where p.codbarras2 != ''\n"
                        + "union \n"
                        + "select\n"
                        + "	p.codproduto Id,\n"
                        + "	p.codbarras3 ean,\n"
                        + "	p.VENDA precovenda,\n"
                        + "	p.VENDAMIN precoatacado,\n"
                        + "	p.UNIDADE tipoEmbalagem,\n"
                        + "	p.UNIDADE tipoEmbalagemCotacao\n"
                        + "from tabprodutos p\n"
                        + "where p.codbarras3 != ''\n"
                        + "union \n"
                        + "select\n"
                        + "	p.codproduto Id,\n"
                        + "	concat('9999999', p.codproduto) ean,\n"
                        + "	p.VENDA precovenda,\n"
                        + "	p.VENDAMIN precoatacado,\n"
                        + "	p.UNIDADE tipoEmbalagem,\n"
                        + "	p.UNIDADE tipoEmbalagemCotacao\n"
                        + "from tabprodutos p\n"
                        + "where p.codproduto not in (select\n"
                        + "	p.codproduto Id\n"
                        + "from tabprodutos p\n"
                        + "where p.codbarras2 != ''\n"
                        + "union \n"
                        + "select\n"
                        + "	p.codproduto Id\n"
                        + "from tabprodutos p\n"
                        + "where p.codbarras3 != '')"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("Id"));
                        if (rst.getString("ean").length() < 6) {
                            imp.setEan("999999" + rst.getString("ean"));
                        } else {
                            imp.setEan(rst.getString("ean"));
                        }
                        imp.setAtacadoPreco(rst.getDouble("precovenda"));
                        imp.setPrecovenda(rst.getDouble("precoatacado"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  codproduto id_pai,\n"
                    + "  codprodutoagreg id_filho,\n"
                    + "  quantidade qtde\n"
                    + " from tabprodutosagregados a"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setImpIdProduto(rst.getString("id_pai"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    imp.setImpIdProdutoItem(rst.getString("id_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtde"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codproduto idproduto,\n"
                    + "	codfornecedor idfornecedor,\n"
                    + "	codprofor externo,\n"
                    + "	quantidade_sismaster qtd\n"
                    + "from\n"
                    + "	tabcodigoprofor t"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("externo"));
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codproduto id,\n"
                    + "	codbarras ean,\n"
                    + "	unidade un\n"
                    + "from\n"
                    + "	tabprodutos\n"
                    + "union\n"
                    + "select\n"
                    + "	codproduto,\n"
                    + "	codbarras2,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	tabprodutos\n"
                    + "union\n"
                    + "select\n"
                    + "	codproduto,\n"
                    + "	codbarras3,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	tabprodutos\n"
                    + "union\n"
                    + "select\n"
                    + "	codproduto,\n"
                    + "	codbarras4,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	tabprodutos\n"
                    + "union\n"
                    + "select\n"
                    + "	codproduto,\n"
                    + "	codoriginal,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	tabprodutos"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("un"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	e.codfor codigo,\n"
                    + "	e.nomefant nome,\n"
                    + "	e.nomefor razaosocial,\n"
                    + "	e.cnpj cnpjcpf,\n"
                    + "	e.inscestadual inscricaoestadual,\n"
                    + "	e.endfor endereco,\n"
                    + "	e.numero numeroendereco,\n"
                    + "	e.bairrofor bairro,\n"
                    + "	i.uf estado,\n"
                    + "	i.cidade municipio,\n"
                    + "	e.cep,\n"
                    + "	e.fonefor telefone,\n"
                    + "	e.fonefor2 telefone2,\n"
                    + "	e.celularfor celular,\n"
                    + "	e.email,\n"
                    + "	e.rg,\n"
                    + "	e.credito_forn,\n"
                    + "	e.ativo\n"
                    + "from\n"
                    + "	tabfornecedores e\n"
                    + "left join tabibge i on e.cidadefor = i.codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("razaosocial"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setUf(rs.getString("estado"));

                    if ((rs.getString("celular") != null)
                            && (!"".equals(rs.getString("celular")))) {
                        imp.addContato("Celular", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("telefone2") != null)
                            && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("Telefone 2", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("email") != null)
                            && (!"".equals(rs.getString("email")))) {
                        imp.addContato("Email", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setAtivo((rs.getInt("ativo") == 1));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	e.codcli codigo,\n"
                    + "	e.nomecom nome,\n"
                    + "	e.nomefant razaosocial,\n"
                    + "	e.tipo tipopessoa,\n"
                    + "	e.cicoucgc cnpjcpf,\n"
                    + "	e.rgouie inscricaoestadual,\n"
                    + "	e.endcom endereco,\n"
                    + "	e.numero numeroendereco,\n"
                    + "	e.complemento,\n"
                    + "	e.baicom bairro,\n"
                    + "	i.cidade municipio,\n"
                    + "	i.uf estado,\n"
                    + "	e.cep,\n"
                    + "	e.fonecom telefone,\n"
                    + "	e.celularcli celular,\n"
                    + "	e.fax,\n"
                    + "	e.email,\n"
                    + "	e.datanasc nascimento,\n"
                    + "	e.limitecredito,\n"
                    + "	e.datacad datacadastro,\n"
                    + "	e.ativo\n"
                    + "from \n"
                    + "	tabclientes e\n"
                    + "left join tabibge i on e.cidcom = i.codigo\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setAtivo((rs.getInt("ativo") == 1));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.numbole id,\n"
                    + "	f.datapto emissao,\n"
                    + "	f.numvenda cupom,\n"
                    + "	0 ecf,\n"
                    + "	f.montante valor,\n"
                    + "	f.codcli_sacado id_cliente,\n"
                    + "	f.datavto vencimento,\n"
                    + "	substring(f.parcelas, 1, 1) parcela\n"
                    + "from\n"
                    + "	tabbolvenda f\n"
                    + "where\n"
                    + "	codcartao = 0\n"
                    + "	and montantepg = 0\n"
                    + "	and valorboleto > 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setValor(rst.getDouble("valor"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<PromocaoIMP> getPromocoes() throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	p.codigo id_promocao,\n"
                    + "	p.descricao descricao,\n"
                    + "	p.dataini inicio,\n"
                    + "	p.datafim termino,\n"
                    + "	pr.CODBARRAS4 ean,\n"
                    + "	t.codproduto id_produto,\n"
                    + "	pr.NOMEPRODUTO descricaocompleta,\n"
                    + "	p.acadaun quantidade,\n"
                    + "	p.qtdtotalvlr paga\n"
                    + "from\n"
                    + "	tabpromocao p\n"
                    + "left join tabpromocaodetalhe t on t.codpromocao = p.codigo \n"
                    + "left join tabprodutos pr on pr.CODPRODUTO = t.codproduto \n"
                    + "where p.qtdtotalvlr > 0"
            )) {
                while (rs.next()) {
                    PromocaoIMP imp = new PromocaoIMP();

                    imp.setId_promocao(rs.getString("id_promocao"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setDataInicio(rs.getDate("inicio"));
                    imp.setDataTermino(rs.getDate("termino"));
                    imp.setEan(rs.getString("ean"));
                    imp.setId_produto(rs.getString("id_produto"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setQuantidade(rs.getDouble("quantidade"));
                    imp.setPaga(rs.getDouble("paga"));

                    Result.add(imp);
                }
            }
        }
        return Result;
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
        return new SismasterDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SismasterDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	c.numvenda id_venda,\n"
                    + "	c.numvenda numerocupom,\n"
                    + "	c.codvendedor ecf,\n"
                    + "	c.datavenda data,\n"
                    + "	c.HORA hora,\n"
                    + "	c.montante valor,\n"
                    + "	c.DESCONTO desconto,\n"
                    + "	t.codcli id_cliente,\n"
                    + "	t.nomecom nomecliente\n"
                    + "FROM\n"
                    + "	tabvendas c\n"
                    + "left join tabclientes t on c.codcliente = t.codcli\n"
                    + "where cfe_protocolo_cancelamento = ''\n"
                    + "and c.datavenda between '" + strDataInicio + "' and '" + strDataTermino + "'";
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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT  \n"
                    + " v.item nritem,\n"
                    + "	v.numvenda id_venda,\n"
                    + "	v.codigo id_item,\n"
                    + "	v.codproduto id_produto,\n"
                    + "	v.nomeproduto descricao,\n"
                    + "	v.QUANTIDADE quantidade,\n"
                    + "	v.preco valor,\n"
                    + "	v.UNIDADE unidade,\n"
                    + "	v.desconto desconto\n"
                    + "FROM\n"
                    + " tabvendasdetalhe v \n"
                    + "where data_cancelamento is null\n"
                    + "and datadetalhe between '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'";
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
