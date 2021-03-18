package vrimplantacao2.dao.interfaces;

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
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class SuperControle_SuperServerDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(SuperControle_SuperServerDAO.class.getName());
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "SuperServer" + ("".equals(complemento) ? "" : " - " + complemento);
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id, "
                    + "nomeFamilia "
                    + "from CadProduto.Familia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("nomeFamilia"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.id as codM1, \n"
                    + "	m1.nomeCategoria as descM1, \n"
                    + "	m2.id as codM2, \n"
                    + "	m2.nomeCategoria as descM2, \n"
                    + "	1 codM3, \n"
                    + "	m2.nomeCategoria as descM3 \n"
                    + "from \n"
                    + "	CadProduto.Categoria m1 \n"
                    + "	left join CadProduto.SubCategoria m2 on \n"
                    + "		m2.fkCategoria = m1.id \n"
                    + "order by \n"
                    + "	codM1, \n"
                    + "	codM2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codM1"));
                    imp.setMerc1Descricao(rst.getString("descM1"));
                    imp.setMerc2ID(rst.getString("codM2"));
                    imp.setMerc2Descricao(rst.getString("descM2"));
                    imp.setMerc3ID(rst.getString("codM3"));
                    imp.setMerc3Descricao(rst.getString("descM3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_LOJA
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare \n"
                    + "	@idLoja varchar,\n"
                    + "	@fkCliente integer;\n"
                    + "\n"
                    + "set @idLoja = " + SQLUtils.stringSQL(getLojaOrigem()) + ";\n"
                    + "set @fkCliente = 1;\n"
                    + "\n"
                    + "select\n"
                    + "	p.id,\n"
                    + "	ean.ean,\n"
                    + "	ean.unidade,\n"
                    + "	p.balanca balanca,\n"
                    + "	p.balancaUnit e_unitario_pesavel,\n"
                    + "	p.nomeProduto descricaocompleta,\n"
                    + "	p.nomeImpressao descricaoreduzida,\n"
                    + "	p.nomeProduto descricaogondola,\n"
                    + "	p.fkCategoria cod_mercadologico1, \n"
                    + "	p.fkSubCategoria cod_mercadologico2, \n"
                    + "	case p.fkFamilia when 0 then null else p.fkFamilia end id_familiaproduto,\n"
                    + "	p.peso pesobruto,\n"
                    + "	p.peso pesoliquido,\n"
                    + "	p.dtUltimaEntrada datacadastro,\n"
                    + "	p.balancaValidade validade,\n"
                    + "	p.margemDesejada margem,\n"
                    + "	e.estoqueAtual estoque,\n"
                    + "	e.estoqueMin estoqueminimo,\n"
                    + "	cast(p.custoCaixa / (case when p.tamCaixa < 1 then 1 else p.tamCaixa end) as numeric(10,2)) custocomimposto,\n"
                    + "	cast(p.custoCaixa / (case when p.tamCaixa < 1 then 1 else p.tamCaixa end) as numeric(10,2)) custosemimposto,\n"
                    + "	coalesce(preco.precoAtivo, p.precoVenda) precovenda,\n"
                    + "	p.ativo,\n"
                    + "	p.precoPromo,\n"
                    + "	p.dtInicioPromo,\n"
                    + "	p.dtFimPromo,\n"
                    + "	p.classFiscal ncm,\n"
                    + "	null cest,\n"
                    + "	p.tribPIS piscofins_cst_debito,\n"
                    + "	p.TribPisCofinsEntrada piscofins_cst_credito ,\n"
                    + "	p.naturezaReceitaPisCofins piscofins_natureza_receita,\n"
                    + "	p.tribICMS icms_cst,\n"
                    + "	p.aliqICMS,\n"
                    + "	p.aliqPIS,\n"
                    + "	icms.taxa icms_aliq,\n"
                    + "	icms.aliquotaReduzidaA icms_reducao\n"
                    + "from\n"
                    + "	CadProduto.Produto p\n"
                    + "	left join (select\n"
                    + "			p.id id_produto,\n"
                    + "			p.id ean,\n"
                    + "			p.unidade,\n"
                    + "			1 qtdembalagem\n"
                    + "		from\n"
                    + "			CadProduto.Produto p\n"
                    + "		union\n"
                    + "		select\n"
                    + "			ean.fkProduto id_produto,\n"
                    + "			ean.id ean,\n"
                    + "			p.unidade,\n"
                    + "			case when ean.qtdade < 1 then 1 else ean.qtdade end as unidade\n"
                    + "		from\n"
                    + "			CadProduto.EanAfiliado ean\n"
                    + "			join CadProduto.Produto p on\n"
                    + "				p.id = ean.fkProduto) ean on\n"
                    + "		ean.id_produto = p.id\n"
                    + "	left join MultiLoja.Loja loja on\n"
                    + "		loja.id = @idLoja\n"
                    + "	left join CadProduto.ListaPreco lista on\n"
                    + "		lista.id = loja.fkListaPreco\n"
                    + "	left join CadProduto.ListaPrecoExcecao preco on\n"
                    + "		preco.fkProduto = p.id and\n"
                    + "		preco.fkListaPreco = lista.id\n"
                    + "	left join CadProduto.EstoqueMultiLoja e on\n"
                    + "		e.fkProduto = p.id and\n"
                    + "		e.fkLoja = loja.id and\n"
                    + "		e.fkCliente = @fkCliente\n"
                    + "	left join CadProduto.AliquotaICMS icms on \n"
                    + "		icms.id = p.aliqICMS and\n"
                    + "		icms.fkCliente = @fkCliente\n"
                    + "order by p.balanca desc, ean.ean"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();

                System.out.println(getSistema());
                System.out.println(getLojaOrigem());

                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));

                    String ean = tratandoPLU(rst.getString("ean"));
                    int plu = Utils.stringToInt(ean, -2);

                    ProdutoBalancaVO bal = balanca.get(plu);
                    if (bal != null && rst.getBoolean("balanca")) {
                        imp.setEan(ean);
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                    } else {
                        imp.setEan(ean);
                        imp.seteBalanca(rst.getBoolean("balanca"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    if (rst.getInt("ativo") == 0) {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    }

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_cst_debito")));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_cst_credito")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_natureza_receita")));

                    String idIcms = getAliquotaKey(
                            rst.getString("icms_cst"),
                            rst.getDouble("icms_aliq"),
                            rst.getDouble("icms_reducao")
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

    private String tratandoPLU(String ean) throws SQLException {
        //ean = 0000000200020
        String eanBal = Utils.stringLong(ean); //200020
        if (eanBal.startsWith("2") && eanBal.endsWith("0")) {
            int plu = Utils.stringToInt(
                    eanBal.substring(1, eanBal.length() - 1),//0002
                    -2
            ); //ean = 2
            ean = String.valueOf(plu);
        }
        return ean;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	c.id,\n"
                        + "	c.razaoSocial,\n"
                        + "	c.nomeFantasia,\n"
                        + "	c.cnpj,\n"
                        + "	c.inscricaoEstadual,\n"
                        + "	c.inscricaoMunicipal,\n"
                        + "	null as suframa,\n"
                        + "	c.ativo,\n"
                        + "	ender.logradouro,\n"
                        + "	ender.numero,\n"
                        + "	ender.complemento,\n"
                        + "	ender.bairro,\n"
                        + "	ender.fkMunicipio,\n"
                        + "	ender.fkUf,\n"
                        + "	ender.cep,\n"
                        + "	c.bloqueado,\n"
                        + "	c.dtCadastro,\n"
                        + "	c.dtNascimento,\n"
                        + "	c.obs\n"
                        + "from\n"
                        + "	Cadastro.Entidade c\n"
                        + "	LEFT join Cadastro.Endereco ender on\n"
                        + "		c.id = ender.fkEntidade and\n"
                        + "		ender.id in (select max(id) id from Cadastro.Endereco group by fkEntidade)\n"
                        + "where \n"
                        + "	isFornecedor = 1 and\n"
                        + "	fkCliente = 1\n"
                        + "order by\n"
                        + "	id"
                )) {
                    while (rst.next()) {
                        FornecedorIMP imp = new FornecedorIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setRazao(rst.getString("razaoSocial"));
                        imp.setFantasia(rst.getString("nomeFantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                        imp.setIe_rg(rst.getString("inscricaoEstadual"));
                        imp.setInsc_municipal(rst.getString("inscricaoMunicipal"));
                        imp.setBloqueado(rst.getInt("bloqueado") != 0);
                        imp.setAtivo(rst.getInt("ativo") == 1);

                        imp.setEndereco(rst.getString("logradouro"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setIbge_municipio(rst.getInt("fkMunicipio"));
                        imp.setIbge_uf(rst.getInt("fkUf"));
                        imp.setCep(rst.getString("cep"));

                        imp.setCob_endereco(rst.getString("logradouro"));
                        imp.setCob_numero(rst.getString("numero"));
                        imp.setCob_complemento(rst.getString("complemento"));
                        imp.setCob_bairro(rst.getString("bairro"));
                        imp.setCob_ibge_municipio(rst.getInt("fkMunicipio"));
                        imp.setCob_ibge_uf(rst.getInt("fkUf"));
                        imp.setCob_cep(rst.getString("cep"));

                        imp.setDatacadastro(rst.getTimestamp("dtCadastro"));
                        imp.setObservacao(rst.getString("obs"));

                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "	id,\n"
                                + "	'(FONE) ' + case coalesce(ltrim(rtrim(tipo)), '') \n"
                                + "		when '' then 'COMERCIAL' \n"
                                + "		else upper(ltrim(rtrim(tipo))) \n"
                                + "	end tipo,\n"
                                + "	numero\n"
                                + "from \n"
                                + "	Cadastro.Fone\n"
                                + "where\n"
                                + "	fkEntidade = " + imp.getImportId()
                        )) {
                            boolean first = true;
                            while (rst2.next()) {
                                if (first) {
                                    imp.setTel_principal(rst2.getString("numero"));
                                    first = false;
                                }
                                FornecedorContatoIMP contato = imp.getContatos()
                                        .make(getSistema(),
                                                getLojaOrigem(),
                                                rst2.getString("id")
                                        );
                                contato.setTipoContato(TipoContato.COMERCIAL);
                                contato.setImportSistema(getSistema());
                                contato.setImportLoja(getLojaOrigem());
                                contato.setImportId(rst2.getString("id"));
                                contato.setNome(rst2.getString("tipo"));
                                contato.setTelefone(rst2.getString("numero"));
                            }
                        }

                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "	id,\n"
                                + "	'(EMAIL) ' + case coalesce(ltrim(rtrim(tipo)), '') \n"
                                + "		when '' then 'COMERCIAL' \n"
                                + "		else upper(ltrim(rtrim(tipo))) \n"
                                + "	end tipo,\n"
                                + "	endereco email\n"
                                + "from \n"
                                + "	Cadastro.Email\n"
                                + "where\n"
                                + "	fkEntidade = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {
                                FornecedorContatoIMP contato = imp.getContatos()
                                        .make(getSistema(),
                                                getLojaOrigem(),
                                                rst2.getString("id")
                                        );
                                contato.setTipoContato(TipoContato.COMERCIAL);
                                contato.setImportSistema(getSistema());
                                contato.setImportLoja(getLojaOrigem());
                                contato.setImportId(rst2.getString("id"));
                                contato.setNome(rst2.getString("tipo"));
                                contato.setEmail(rst2.getString("email"));
                            }
                        }
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	r.fkProduto,\n"
                    + "	r.fkFornecedor,\n"
                    + "	r.sref,\n"
                    + "	r.tamEmb\n"
                    + "from\n"
                    + "	CadProduto.Referencia r\n"
                    + "	join CadProduto.Produto p on\n"
                    + "		r.fkProduto = p.id\n"
                    + "	join Cadastro.Entidade e on\n"
                    + "		e.isFornecedor = 1 and\n"
                    + "		e.id = r.fkFornecedor\n"
                    + "where\n"
                    + "	r.fkCliente = 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fkFornecedor"));
                    imp.setIdProduto(rst.getString("fkProduto"));
                    imp.setCodigoExterno(rst.getString("sref"));
                    imp.setQtdEmbalagem(rst.getInt("tamEmb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "c.id,\n"
                    + "c.cnpj,\n"
                    + "c.inscricaoMunicipal,\n"
                    + "c.inscricaoEstadual,\n"
                    + "c.razaoSocial,\n"
                    + "c.nomeFantasia,\n"
                    + "c.dtNascimento,\n"
                    + "c.dtCadastro,\n"
                    + "c.ativo,\n"
                    + "c.obs,\n"
                    + "ender.logradouro,\n"
                    + "ender.numero,\n"
                    + "ender.complemento,\n"
                    + "ender.bairro,\n"
                    + "ender.cep,\n"
                    + "ender.fkMunicipio,\n"
                    + "ender.fkUF,\n"
                    + "ender.fkPais,\n"
                    + "email.endereco as email,\n"
                    + "crm.vlRotativoTotal as valorlimite,\n"
                    + "crm.liberadoRotativo as permitecreditorotativo,\n"
                    + "crm.liberadoCheque as permitecheque,\n"
                    + "crm.vlSalario,\n"
                    + "crm.dtAdmissao,\n"
                    + "crm.empresa,\n"
                    + "crm.cargo,\n"
                    + "crm.pai,\n"
                    + "crm.mae,\n"
                    + "crm.observacao obs2,\n"
                    + "crm.cdInternoCli,\n"
                    + "crm.senha,\n"
                    + "crm.clienteespecial\n"
                    + "from Cadastro.Entidade c\n"
                    + "left join Cadastro.Endereco ender on ender.fkEntidade = c.id \n"
                    + "and ender.id in (select max(id) id from Cadastro.Endereco group by fkEntidade)\n"
                    + "left join Cadastro.Email email on email.fkEntidade = c.id\n"
                    + "and email.id in (select max(id) id from Cadastro.Email group by fkEntidade)\n"
                    + "left join CRM.Cadastro crm on crm.fkEntidade = c.id\n"
                    + "where c.isCliente = 1\n"
                    + "and crm.fkCliente = 1\n"
                    + "order by c.id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaoMunicipal"));
                    imp.setInscricaoestadual(rst.getString("inscricaoEstadual"));
                    imp.setRazao(rst.getString("razaoSocial"));
                    imp.setFantasia(rst.getString("nomeFantasia"));
                    imp.setDataNascimento(rst.getDate("dtNascimento"));
                    imp.setDataCadastro(rst.getDate("dtCadastro"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setEndereco(rst.getString("logradouro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipioIBGE(rst.getInt("fkMunicipio"));
                    imp.setUfIBGE(rst.getInt("fkUF"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setPermiteCreditoRotativo(rst.getBoolean("permitecreditorotativo"));
                    imp.setPermiteCheque(rst.getBoolean("permitecheque"));
                    imp.setSalario(rst.getDouble("vlSalario"));
                    imp.setDataAdmissao(rst.getDate("dtAdmissao"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao2(rst.getString("obs2"));
                    //imp.setSenha(rst.getInt("senha"));

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "	id,\n"
                                + "	'(FONE) ' + case coalesce(ltrim(rtrim(tipo)), '') \n"
                                + "		when '' then 'COMERCIAL' \n"
                                + "		else upper(ltrim(rtrim(tipo))) \n"
                                + "	end tipo,\n"
                                + "	numero\n"
                                + "from \n"
                                + "	Cadastro.Fone\n"
                                + "where\n"
                                + "	fkEntidade = " + imp.getId()
                        )) {
                            boolean first = true;
                            while (rst2.next()) {
                                if (first) {
                                    imp.setTelefone(rst2.getString("numero"));
                                    first = false;
                                }

                                imp.addContato(
                                        rst.getString("id"),
                                        "TELEFONE",
                                        rst.getString("numero"),
                                        null,
                                        null
                                );
                            }
                        }
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "r.fkVenda as id,\n"
                    + "r.fkEntidade as cliente,\n"
                    + "dtVenda as emissao,\n"
                    + "dateadd(DAY, 30, r.dtVenda) as vencimento,\n"
                    + "(r.valorVenda - r.valorPago) as valor,\n"
                    + "r.valorJuros as juros,\n"
                    + "r.fkPDV as ecf,\n"
                    + "v.coo numerocupom\n"
                    + "from Comercial.VendaPrazo r\n"
                    + "left join Comercial.Venda v on v.id = r.fkVenda\n"
                    + "where coalesce(r.valorPago, 0) < coalesce(r.valorVenda,0)\n"
                    + "and r.fkCliente = " + getLojaOrigem() + "\n"
                    + "ORDER BY r.dtVenda desc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id") + rst.getString("cliente"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    //@Override
    public List<MapaTributoIMP> _getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id, \n"
                    + "descricaoAliquota, \n"
                    + "taxa, \n"
                    + "reducaoBaseCalculo \n"
                    + "from CadProduto.AliquotaICMS \n"
                    + "where fkCliente = 1\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("id"), rst.getString("descricaoAliquota")));
                }
            }
            return result;
        }
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws Exception {
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct	\n"
                    + "	p.aliqICMS,\n"
                    + "	icm.descricaoAliquota,\n"
                    + "	p.tribICMS as cst_icms,\n"
                    + "	icm.taxa as aliq_icms,\n"
                    + "	icm.reducaoBaseCalculo as red_icms\n"
                    + "from CadProduto.Produto p\n"
                    + "left join CadProduto.AliquotaICMS icm\n"
                    + "	on icm.id = p.aliqICMS\n"
                    + "where p.tribICMS is not null\n"
                    + "and p.tribICMS != ''\n"
                    + "and icm.fkCliente = 1\n"
                    + "order by p.tribICMS, icm.taxa"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst_icms"),
                            rst.getDouble("aliq_icms"),
                            rst.getDouble("red_icms")
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    rst.getString("descricaoAliquota"),
                                    rst.getInt("cst_icms"),
                                    rst.getDouble("aliq_icms"),
                                    rst.getDouble("red_icms")
                            )
                    );
                }
            }
            return result;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id, \n"
                    + "(descricaoLoja + ' - ' + cnpjLoja) as descricao\n"
                    + "from MultiLoja.Loja\n"
                    + "where fkCliente = 1\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("id"), rst.getString("descricao"))
                    );
                }
            }
        }
        return result;
    }

    private List<ProdutoAutomacaoVO> getDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();
        boolean isGerarDigito = true;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, \n"
                    + "id_tipoembalagem, \n"
                    + "pesavel \n"
                    + "from produto \n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));

                    isGerarDigito = !(rst.getInt("id_tipoembalagem") == 4 || rst.getBoolean("pesavel") == true);

                    vo.setCodigoBarras(gerarEan13(rst.getLong("id"), isGerarDigito));
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void importarDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregar Produtos...");
        try {
            result = getDigitoVerificador();

            if (!result.isEmpty()) {
                gravarCodigoBarrasDigitoVerificador(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void gravarCodigoBarrasDigitoVerificador(List<ProdutoAutomacaoVO> vo) throws Exception {

        Conexao.begin();
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        String sql = "";
        ProgressBar.setStatus("Gravando Código de Barras...");
        ProgressBar.setMaximum(vo.size());

        try {

            for (ProdutoAutomacaoVO i_vo : vo) {

                sql = "select codigobarras from produtoautomacao where codigobarras = " + i_vo.getCodigoBarras();
                rst = stm.executeQuery(sql);

                if (!rst.next()) {
                    sql = "insert into produtoautomacao ("
                            + "id_produto, "
                            + "codigobarras, "
                            + "id_tipoembalagem, "
                            + "qtdembalagem, "
                            + "pesobruto,"
                            + "dun14) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ", "
                            + i_vo.getIdTipoEmbalagem() + ", 1, 0, false);";
                    stm.execute(sql);
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public long gerarEan13(long i_codigo, boolean i_digito) throws Exception {
        String codigo = String.format("%012d", i_codigo);

        int somaPar = 0;
        int somaImpar = 0;

        for (int i = 0; i < 12; i += 2) {
            somaImpar += Integer.parseInt(String.valueOf(codigo.charAt(i)));
            somaPar += Integer.parseInt(String.valueOf(codigo.charAt(i + 1)));
        }

        int soma = somaImpar + (3 * somaPar);
        int digito = 0;
        boolean verifica = false;
        int calculo = 0;

        do {
            calculo = soma % 10;

            if (calculo != 0) {
                digito += 1;
                soma += 1;
            }
        } while (calculo != 0);

        if (i_digito) {
            return Long.parseLong(codigo + digito);
        } else {
            return Long.parseLong(codigo);
        }
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        //next.setNumeroSerie(rst.getString("numeroserie"));
                        //next.setModeloImpressora(rst.getString("modelo"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
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
                    + "	v.id id,\n"
                    + "	coo numerocupom,\n"
                    + "	v.fkCliente idcliente,\n"
                    + "	fkPDV ecf,\n"
                    + "	CAST(CONVERT(NVARCHAR, dtInicio, 23) as date) data,\n"
                    + " CAST(CONVERT(NVARCHAR, dtInicio, 8) as time) horainicio,\n"
                    + " CAST(CONVERT(NVARCHAR, dtFim, 8) as time) horatermino,\n"
                    + "	vlSubTotal subtotalimpressora,\n"
                    + "	cpfCnpj cpf,\n"
                    + "	vlDesconto desconto,\n"
                    + "	vlAcrescimo acrescimo,\n"
                    + "	c.nomeFantasia nomecliente,\n"
                    + "	ender.logradouro endereco,\n"
                    + "	ender.numero numero,\n"
                    + "	ender.complemento complemento,\n"
                    + "	ender.bairro bairro,\n"
                    + "	UPPER(nomeMunicipio) cidade,\n"
                    + "	siglauf estado,\n"
                    + "	ender.cep cep\n"
                    + "from\n"
                    + "	Comercial.Venda v\n"
                    + "	left join Cadastro.Entidade c on v.fkCliente = c.id\n"
                    + "	left join Cadastro.Endereco ender on ender.fkEntidade = c.id and ender.id in (select max(id) id from Cadastro.Endereco group by fkEntidade)\n"
                    + "	left join Cadastro.Municipio cid on cid.cdMunicipio = ender.fkmunicipio\n"
                    + "	left join Cadastro.UF est on est.cdUF = cid.fkUF\n"
                    + "where\n"
                    + "	fkLoja = " + idLojaCliente + " \n"
                    + "	and CONVERT(NVARCHAR, dtInicio, 23) BETWEEN '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'\n"
                    + "	order by 5,2";
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

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
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
                    + "    cx.id,\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    cx.codprod as produto,\n"
                    + "    pr.DESC_PDV as descricao,    \n"
                    + "    isnull(cx.qtd, 0) as quantidade,\n"
                    + "    isnull(cx.totitem, 0) as total,\n"
                    + "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    isnull(cx.descitem, 0) as desconto,\n"
                    + "    isnull(cx.acrescitem, 0) as acrescimo,\n"
                    + "    case\n"
                    + "     when LEN(cx.barra) > 14 \n"
                    + "     then SUBSTRING(cx.BARRA, 4, LEN(cx.barra))\n"
                    + "    else cx.BARRA end as codigobarras,\n"
                    + "    pr.unidade,\n"
                    + "    cx.codaliq codaliq_venda,\n"
                    + "    pr.codaliq codaliq_produto,\n"
                    + "    ic.DESCRICAO trib_desc\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
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
