package vrimplantacao2.dao.interfaces.gestora;

//import vrimplantacao.classe.ConexaoSqlServer;
//import vrimplantacao2.utils.sql.SQLUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialAnteriorDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCheque;

/**
 *
 * @author Leandro
 */
public class GestoraDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(GestoraDAO.class.getName());

    public String v_lojaMesmoId;

    @Override
    public String getSistema() {
        return "Gestora";
    }
    private boolean migrarMargemProduto;

    public boolean isMigrarMargemProduto() {
        return this.migrarMargemProduto;
    }

    public void setMigrarMargemProduto(boolean migrarMargemProduto) {
        this.migrarMargemProduto = migrarMargemProduto;
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.OFERTA,
                OpcaoProduto.RECEITA
        ));
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	EMP_CODIGO id,\n"
                    + "	EMP_NOME descricao,\n"
                    + "	EMP_CGC as cnpj \n"
                    + "from\n"
                    + "	EMPRESA e order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao") + " - " + rst.getString("cnpj")));
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
                    "select\n"
                    + "	d.DEP_CODIGO merc1,\n"
                    + "	d.DEP_DESCRICAO merc1_desc,\n"
                    + "	g.GRU_CODIGO merc2,\n"
                    + "	g.GRU_DESCRICAO merc2_desc,\n"
                    + "	sg.sub_codigo merc3,\n"
                    + "	sg.sub_descricao merc3_desc\n"
                    + "from\n"
                    + "	DEPARTAMENTO d\n"
                    + "	left join GRU_PRODUTOS g on\n"
                    + "		d.DEP_CODIGO = g.DEP_CODIGO\n"
                    + "	left join SUBGRUPO_PRODUTOS sg on\n"
                    + "		g.GRU_CODIGO = sg.gru_codigo\n"
                    + "order by\n"
                    + "	merc1, merc2, merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from\n"
                    + "(select \n"
                    + "	p.pro_codigo,\n"
                    + "	p.pro_descricao,\n"
                    + "	(select COUNT(*) \n"
                    + "	from produtos where PRO_PAI = p.pro_codigo) qtd\n"
                    + "from\n"
                    + "	produtos p) a\n"
                    + "where\n"
                    + "	a.qtd > 0\n"
                    + "order by\n"
                    + "	a.pro_codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("pro_codigo"));
                    imp.setDescricao(rst.getString("pro_descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    tri_codigo as codigo,\n"
                    + "    tri_descricao as descricao,\n"
                    + "    case\n"
                    + "        when tri_descricao like '%TRIBUT%' then 00\n"
                    + "        when tri_descricao like '%RED%' then 20\n"
                    + "        when tri_descricao like '%ISEN%' then 40\n"
                    + "        when tri_descricao like '%NAO TRIB%' then 41\n"
                    + "	       when tri_descricao like '%DIFER%' then 51\n"
                    + "        when tri_descricao like '%SUBSTI%' then 60\n"
                    + "    end cst,\n"
                    + "    tri_aliquota as aliquota,\n"
                    + "    tri_reducao as reducao\n"
                    + "from\n"
                    + "tributacao\n"
                    + "order by\n"
                    + "3, 4"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.pro_codigo id,\n"
                    + "	p.PRO_DATA_CADASTRO datacadastro,\n"
                    + "	ean.ean,\n"
                    + "	ean.qtdEmbalagem,\n"
                    + "	case\n"
                    + "		p.PRO_BALANCA when 'S' then 1\n"
                    + "		else 0\n"
                    + "	end eBalanca,\n"
                    + "	rtrim(ltrim(p.PRO_UNIDADE)) tipoEmbalagem,\n"
                    + "	p.PRO_VALIDADE validade,\n"
                    + "	p.PRO_DESCRICAO descricaocompleta,\n"
                    + "	replace(p.pro_desc_etiqueta, '  ', '') descricaoreduzida,\n"
                    + "	p.PRO_DESCRICAO descricaogondola,\n"
                    + "	p.dep_codigo merc1,\n"
                    + "	p.gru_codigo merc2,\n"
                    + "	p.SUB_CODIGO merc3,\n"
                    + "	p.PRO_PAI idFamiliaProduto,\n"
                    + "	p.PRO_PESOLIQUIDO pesoliquido,\n"
                    + "	p.PRO_PESOLIQUIDO pesobruto,\n"
                    + "	p.PRO_MINIMO estoqueminimo,\n"
                    + "	p.pro_estoque estoque,\n"
                    + "	p.PRO_MARGEM p_margem,\n"
                    + "	sb.sub_margem merc_margem,\n"
                    + "	p.PRO_CUSTO custosemimposto,\n"
                    + "	p.PRO_CUSTOREAL custocomimposto,\n"
                    + "	p.PRO_VENDA precovenda,\n"
                    + "	case\n"
                    + "		when p.PRO_STATUS in ('E', 'I') then 0 else 1\n"
                    + "	end situacaoCadastro,\n"
                    + "	coalesce(p.PRO_CLASFISCAL,'') ncm,\n"
                    + "	coalesce(p.PRO_CEST,'') cest,\n"
                    + "	p.PRO_SIT_TRIBUTARIA,\n"
                    + "	case\n"
                    + "		substring(tri_sweda, 1, 1) when 'F' then 60 when 'T' then 0 else 40\n"
                    + "	end as icmsCst,\n"
                    + "	t.TRI_ALIQUOTA icmsAliq,\n"
                    + "	t.TRI_REDUCAO icmsRed,\n"
                    + " t.tri_codigo, \n"
                    + "	p.PRO_CST_PIS_ENTRADA piscofinsCredito,\n"
                    + "	p.PRO_CST_PIS piscofinsSaida,\n"
                    + "	p.natr_codigo piscofinsNatureza\n"
                    + "from\n"
                    + "	(select\n"
                    + "		case\n"
                    + "			when (pro_status = 'B')\n"
                    + "			and (coalesce(pro_pai,\n"
                    + "			0) > 0) then PRO_PAI\n"
                    + "			else PRO_CODIGO\n"
                    + "		end id, pro_barra ean,\n"
                    + "		case\n"
                    + "			when PRO_QTDE_BAIXAR < 1 then 1\n"
                    + "			else pro_qtde_baixar\n"
                    + "		end qtdEmbalagem\n"
                    + "	from\n"
                    + "		produtos) ean\n"
                    + "left join produtos p on p.PRO_CODIGO = ean.id\n"
                    + "left join GRU_PRODUTOS gp on p.GRU_CODIGO = gp.GRU_CODIGO\n"
                    + "left join SUBGRUPO_PRODUTOS sb on p.SUB_CODIGO = sb.sub_codigo\n"
                    + "left join tributacao t on p.tri_codigo = t.tri_codigo\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id"));
                        imp.seteBalanca(rst.getBoolean("eBalanca"));
                        imp.setEan(rst.getString("id"));

                        ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("id"), -2));

                        if (bal != null) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                            imp.setEan(String.valueOf(bal.getCodigo()));
                        }

                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setEan(rst.getString("ean").equals("0") ? rst.getString("id") : rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                        imp.seteBalanca(rst.getBoolean("eBalanca"));
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                        imp.setCodMercadologico1(rst.getString("merc1"));
                        imp.setCodMercadologico2(rst.getString("merc2"));
                        imp.setCodMercadologico3(rst.getString("merc3"));
                        imp.setIdFamiliaProduto(rst.getString("idFamiliaProduto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setMargem(rst.getDouble("p_margem"));

//                        if (isMigrarMargemProduto()) {
//                            imp.setMargem(rst.getDouble("p_margem"));
//                        } else {
//                            imp.setMargem(rst.getDouble("merc_margem"));
//                        }
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaoCadastro")));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofinsCredito")));
                        imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofinsSaida")));
                        imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofinsNatureza")));

                        imp.setIcmsDebitoId(rst.getString("tri_codigo"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("tri_codigo"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("tri_codigo"));
                        imp.setIcmsCreditoId(rst.getString("tri_codigo"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("tri_codigo"));
                        imp.setIcmsConsumidorId(rst.getString("tri_codigo"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "case when (pro_status = 'B')\n"
                    + "	and (coalesce(pro_pai,0) > 0) then PRO_PAI\n"
                    + "	else PRO_CODIGO\n"
                    + "	end id, pro_barra ean,\n"
                    + "case when PRO_QTDE_BAIXAR < 1 then 1\n"
                    + "	else pro_qtde_baixar\n"
                    + "	end qtdEmbalagem,\n"
                    + "rtrim(ltrim(PRO_UNIDADE)) tipoEmbalagem\n"
                    + "	from\n"
                    + "produtos"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.FOR_CODIGO id,\n"
                    + "	f.FOR_RAZAO razao,\n"
                    + "	f.FOR_FANTASIA fantasia,\n"
                    + "	f.FOR_CGC cnpj,\n"
                    + "	f.FOR_INS ie_rg,\n"
                    + "	'' insc_municipal,\n"
                    + "	'0' suframa,\n"
                    + "	case ltrim(rtrim(upper(for_status))) when 'I' then 0 else 1 end ativo,\n"
                    + "	f.FOR_ENDERECO endereco,\n"
                    + "	f.FOR_ENDNRO numero,\n"
                    + "	f.FOR_BAIRRO bairro,\n"
                    + "	cd.CID_IBGE ibge_municipio,\n"
                    + "	f.FOR_CEP cep,\n"
                    + "	'' + ltrim(rtrim(f.FOR_DDD1)) + '' + ltrim(f.FOR_TELEFONE1) fone1,\n"
                    + "	'' + ltrim(rtrim(f.FOR_DDD2)) + '' + ltrim(f.FOR_TELEFONE2) fone2,\n"
                    + "	'' + ltrim(rtrim(f.FOR_FAXDDD)) + '' + ltrim(f.FOR_FAX) fax,\n"
                    + "	f.FOR_EMAIL email,\n"
                    + "	f.FOR_EMAIL_VENDEDOR1 email_vendedor1,\n"
                    + "	f.FOR_EMAIL_VENDEDOR2 email_vendedor2,\n"
                    + "	'' + ltrim(rtrim(f.FOR_venDDD1)) + '' + ltrim(f.FOR_venTELEFONE1) telvend1,\n"
                    + "	'' + ltrim(rtrim(f.FOR_VENDDD2)) + '' + ltrim(f.FOR_venTELEFONE2) telvend2,\n"
                    + "	'' + ltrim(rtrim(f.FOR_VENDDDcelular)) + '' + ltrim(f.FOR_vencelular) celvend,\n"
                    + "	f.FOR_OBS observacao,\n"
                    + "	f.for_apelido apelido,\n"
                    + "	f.FOR_ALTERACAO cadastro\n"
                    + "from \n"
                    + "	FORNECEDOR f\n"
                    + "	left join CIDADES cd on\n"
                    + "		f.CID_CODIGO = cd.CID_CODIGO"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(Utils.stringToInt(rst.getString("ibge_municipio")));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(String.valueOf(Utils.stringToLong(rst.getString("fone1"))));
                    imp.setDatacadastro(rst.getDate("cadastro"));
                    String fone2 = rst.getString("fone2"),
                            fax = rst.getString("fax"),
                            email = rst.getString("email"),
                            emailVend1 = rst.getString("email_vendedor1"),
                            emailVend2 = rst.getString("email_vendedor2"),
                            telVend1 = rst.getString("telvend1"),
                            telVend2 = rst.getString("telvend2"),
                            celVend2 = rst.getString("celvend"),
                            apelido = rst.getString("apelido"),
                            observacao = rst.getString("observacao");

                    imp.setObservacao(
                            (fone2 != null ? "FONE2: " + fone2 + "\n" : "")
                            + (fax != null ? "FAX: " + fax + "\n" : "")
                            + (email != null ? "EMAIL: " + email + "\n" : "")
                            + (emailVend1 != null ? "EMAIL VEND1: " + emailVend1 + "\n" : "")
                            + (emailVend2 != null ? "EMAIL VEND2: " + emailVend2 + "\n" : "")
                            + (telVend1 != null ? "TEL. VEND1: " + telVend1 + "\n" : "")
                            + (telVend2 != null ? "TEL. VEND2: " + telVend2 + "\n" : "")
                            + (celVend2 != null ? "CEL. VEND: " + celVend2 + "\n" : "")
                            + (apelido != null ? "APELIDO: " + apelido + "\n" : "")
                            + (observacao != null ? "OBSERVACAO: " + observacao : "")
                    );

                    result.add(imp);
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
                    + "	PRO_CODIGO id_produto,\n"
                    + "	FOR_CODIGO id_fornecedor,\n"
                    + "	PRO_FORCODIGO codigoexterno,\n"
                    + "	PRO_ALTERACAO alteracao,\n"
                    + "	coalesce(PRO_FOR_EMBALAGEM, PRO_QTDE_VOL) qtdEmbalagem\n"
                    + "from produto_for"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setDataAlteracao(rst.getDate("alteracao"));

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
                    "select\n"
                    + "	c.CLI_CODIGO id,\n"
                    + "	c.CLI_CPFCGC cnpj,\n"
                    + "	c.CLI_RGINS inscricaoestadual,\n"
                    + "	c.CLI_NOME razao,\n"
                    + "	c.CLI_FANTASIA fantasia,\n"
                    + "	case ltrim(rtrim(c.CLI_STATUS)) when 'I' then 0 else 1 end ativo,\n"
                    + "	case ltrim(rtrim(c.CLI_BLOQUEADO)) when 'S' then 1 else 0 end bloqueado,\n"
                    + "	c.CLI_ENDERECO endereco,\n"
                    + "	c.CLI_ENDNRO numero,\n"
                    + "	c.CLI_BAIRRO bairro,\n"
                    + "	c.CLI_CIDADE municipio,\n"
                    + "	c.CLI_ESTADO uf,\n"
                    + "	c.CLI_CEP cep,\n"
                    + "	c.CLI_ENDERECO_COB enderecocob,\n"
                    + "	'0' numerocob,\n"
                    + "	c.CLI_BAIRRO_COB bairrocob,\n"
                    + "	c.CLI_CIDADE_COB municipiocob,\n"
                    + "	c.CLI_ESTADO_COB ufcob,\n"
                    + "	c.CLI_CEP_COB cepcob,\n"
                    + "	coalesce(upper(c.CLI_EST_CIVIL),'') estadoCivil,\n"
                    + "	c.CLI_NASCIMENTO dataNascimento,\n"
                    + "	c.CLI_DATA_CADASTRO dataCadastro,\n"
                    + "	c.CLI_PROFISSAO cargo,\n"
                    + "	c.CLI_RENDA salario,\n"
                    + "	c.CLI_DIA_VECTO diavencimento,\n"
                    + "	coalesce(ltrim(rtrim(c.cli_obs)),'') obs1,\n"
                    + "	coalesce(c.cli_obs1,'') obs2,\n"
                    + "	coalesce(ltrim(rtrim(sta.STA_DESCRICAO)),'') obs3,\n"
                    + "	c.CLI_LIMITE limite,\n"
                    + "	-- c.CLI_LIMITE_CHEQ limite_cheque,\n"
                    + "	c.CLI_CONJUGUE conjuge,\n"
                    + "	c.CLI_PAI nomepai,\n"
                    + "	c.CLI_MAE nomemae,\n"
                    + "	'(' + ltrim(rtrim(c.CLI_DDD1)) + ')' + ltrim(c.CLI_TELEFONE1) fone1,\n"
                    + "	'(' + ltrim(rtrim(c.CLI_DDD2)) + ')' + ltrim(c.CLI_TELEFONE2) fone2,\n"
                    + "	coalesce(c.CLI_EMAIL, c.cli_email2) email,\n"
                    + "	'(' + ltrim(rtrim(c.CLI_FAXDDD)) + ')' + ltrim(c.CLI_FAX) fax,\n"
                    + "	c.CLI_INS_MUN inscricaomunicipal\n"
                    + "from \n"
                    + "	CLIENTES c\n"
                    + "	left join sta_cliente sta on\n"
                    + "		c.sta_codigo = sta.sta_codigo"//\n" +
            //"where coalesce(c.CLI_NOME,'') != ''"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setCobrancaEndereco(rst.getString("enderecocob"));
                    imp.setCobrancaBairro(rst.getString("bairrocob"));
                    imp.setCobrancaMunicipio(rst.getString("municipiocob"));
                    imp.setCobrancaUf(rst.getString("ufcob"));
                    imp.setCobrancaCep(rst.getString("cepcob"));
                    String civil = Utils.acertarTexto(rst.getString("estadoCivil"), 2);
                    switch (civil) {
                        case "CA":
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                            break;
                        case "SO":
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                            break;
                        case "VI":
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                            break;
                        default:
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                            break;
                    }
                    imp.setDataNascimento(rst.getDate("dataNascimento"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(String.valueOf(Utils.stringToLong(rst.getString("fone1"))));
                    String observacao = "";
                    if (!"".equals(rst.getString("obs1"))) {
                        observacao += Utils.acertarTexto(rst.getString("obs1")) + "\n";
                    }
                    if (!"".equals(rst.getString("obs2"))) {
                        observacao += Utils.acertarTexto(rst.getString("obs2")) + "\n";
                    }
                    if (!"".equals(rst.getString("obs3"))) {
                        observacao += Utils.acertarTexto(rst.getString("obs3")) + "\n";
                    }
                    observacao += "TELEFONE2: " + String.valueOf(Utils.stringToLong(rst.getString("fone2")));

                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao(observacao);
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));

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
                    /*"declare @juros numeric;\n" +
                     "\n" +
                     "select @juros = juros from CONFIG;\n" +
                     "\n" +
                     "select\n" +
                     "	r.VEN_DATA emissao,\n" +
                     "	r.COM_NCUPOM cupom,\n" +
                     "	r.PRO_VENDA valor,\n" +
                     "	r.PRO_DESCRICAO observacao,\n" +
                     "	r.cli_codigo id_clientepreferencial,\n" +
                     "	r.VEN_VENCIMENTO vencimento,\n" +
                     "	--cast(\n" +
                     "	case when r.VEN_VENCIMENTO < CAST(current_timestamp as date) then\n" +
                     "		cast((r.PRO_VENDA * cast(@juros as numeric) /cast(30 as numeric) * (cast(cast(current_timestamp as DATE) - r.VEN_VENCIMENTO as integer))) / 100 as numeric(10,2)) \n" +
                     "	else 0 end juros,\n" +
                     "	c.CLI_CODIGO,\n" +
                     "	c.CLI_CPFCGC cnpj,\n" +
                     "	c.CLI_NOME nome\n" +
                     "from \n" +
                     "	VENDAS_PRAZO r\n" +
                     "	join CLIENTES c on\n" +
                     "		c.CLI_CODIGO = r.CLI_CODIGO"*/
                    "declare @juros numeric;\n"
                    + "select @juros = juros from CONFIG;\n"
                    + "select\n"
                    + "	 r.VEN_REGISTRO id,\n"
                    + "	 r.VEN_DATA emissao,\n"
                    + "	 r.COM_NCUPOM cupom,\n"
                    + "	 r.PRO_VENDA valor,\n"
                    + "	 r.PRO_DESCRICAO observacao,\n"
                    + "	 r.cli_codigo id_clientepreferencial,\n"
                    + "	 r.VEN_VENCIMENTO vencimento,\n"
                    + "	 c.CLI_CODIGO,\n"
                    + "	 c.CLI_CPFCGC cnpj,\n"
                    + "	 c.CLI_NOME nome\n"
                    + "from\n"
                    + "	 VENDAS_PRAZO r\n"
                    + "join CLIENTES c on\n"
                    + "	 c.CLI_CODIGO = r.CLI_CODIGO"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_clientepreferencial"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	rc.PRO_CODIGO_RECEITA as id,\n"
                    + "	p.PRO_DESCRICAO as descricaoreceita,\n"
                    + "	rc.PRO_CODIGO as idproduto,\n"
                    + "	p2.PRO_DESCRICAO as descricaoproduto,\n"
                    + "	rc.REC_QTDE * 1000 as qtdproduto,\n"
                    + "	rc.REC_QTDE,\n"
                    + "	coalesce(rc.REC_EMBALAGEM, 1) as qtdembalagemreceita,\n"
                    + "	rc.REC_STATUS\n"
                    + "from RECEITA rc\n"
                    + "join PRODUTOS p on p.PRO_CODIGO = rc.PRO_CODIGO_RECEITA\n"
                    + "join PRODUTOS p2 on p2.PRO_CODIGO = rc.PRO_CODIGO\n"
                    + "and rc.REC_STATUS = 'A'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportsistema(getSistema());
                    imp.setImportid(rst.getString("id"));
                    imp.setIdproduto(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricaoreceita"));
                    imp.setRendimento(rst.getDouble("qtdembalagemreceita"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setQtdembalagemreceita(rst.getInt("qtdproduto"));
                    imp.setFator(1);
                    imp.setFichatecnica("");

                    imp.getProdutos().add(rst.getString("idproduto"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<ContaPagarIMP> getContasAPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pag.FIN_REGISTRO as id,\n"
                    + "	pag.FIN_NUMERONOTA as numeronota,\n"
                    + "	pag.FIN_NUMERODOC as numerodocumento,\n"
                    + "	pag.FIN_DTEMISSAO as dataemissao,\n"
                    + "	pag.FIN_VALOR as valor,\n"
                    + "	pag.FIN_VALORTOTAL as valortotal,\n"
                    + "	pag.FIN_ACRESCIMO as acrescimo,\n"
                    + "	pag.FIN_DESCONTO as desconto,\n"
                    + "	pag.FIN_HISTORICO as observacao,\n"
                    + "	pag.FIN_DTLANCAMENTO as lancamento,\n"
                    + "	pag.FOR_CODIGO as idfornecedor,\n"
                    + "	parc.FINFAT_PARCELA as numeroparcela,\n"
                    + "	parc.FINFAT_NUMERODOC,\n"
                    + "	parc.FINFAT_VENCIMENTO as datavencimento,\n"
                    + "	parc.FINFAT_VALOR as valorparcela,\n"
                    + "	parc.FINFAT_ACRESCIMO as acrescimoparcela,\n"
                    + "	parc.FINFAT_JURO as jurosparcela,\n"
                    + "	parc.FINFAT_DESCONTO as descontoparcela,\n"
                    + "	parc.FINFAT_VALORTOTAL as valortotalparcela,\n"
                    + "	parc.FINFAT_VALORPAGO as valorpagoparcela,\n"
                    + "	parc.FINFAT_OBS as observacaoparcela\n"
                    + "from FIN_FINANCEIRO pag\n"
                    + "join FIN_FATURA parc on parc.FIN_REGISTRO = pag.FIN_REGISTRO\n"
                    + "where pag.EMP_CODIGO = 1\n"
                    + "and FINFAT_DTPAGAMENTO is null\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ch.CHE_REGISTRO id,\n"
                    + "ch.CHE_VECTO as vecto,\n "
                    + "	ch.CHE_DATA dataemissao,\n"
                    + "	ch.CHE_DATA datadeposito,\n"
                    + "	ch.COM_REGISTRO numerocupom,\n"
                    + "	ch.CHE_NUMERO numerocheque,\n"
                    + "	coalesce(ch.CHE_AGENCIA, '') agencia,\n"
                    + "	coalesce(ch.CHE_CONTA, '') conta,\n"
                    + "	c.CLI_TELEFONE1 telefone,\n"
                    + "	c.CLI_CPFCGC cpf,\n"
                    + "	c.CLI_NOME nome,\n"
                    + "	c.CLI_RGINS rg,\n"
                    + "	ch.CHE_HISTORICO observacao,\n"
                    + "	ch.CHE_BANCO id_banco,\n"
                    + "	ch.CHE_VALOR valor,\n"
                    + "	coalesce(ch.CHE_STATUS, '') situacao,\n"
                    + "	ch.CHE_CMC7 cm7,\n"
                    + "	ch.CHE_JUROS valorjuros\n"
                    + "from\n"
                    + "	CHEQUE_REC ch\n"
                    + "join CLIENTES c on\n"
                    + "	ch.CLI_CODIGO = c.CLI_CODIGO \n"
                    + "where coalesce(ch.CHE_STATUS, '') = 'A' "
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setDataDeposito(rst.getDate("vecto"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNome(rst.getString("nome"));
                    imp.setRg(rst.getString("rg"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setValorJuros(rst.getDouble("valorjuros"));
                    //imp.setSituacaoCheque(SituacaoCheque.ABERTO);

                    if (rst.getString("id_banco") != null && !rst.getString("id_banco").trim().isEmpty()) {
                        imp.setBanco(Integer.parseInt(rst.getString("id_banco").trim()));
                    } else {
                        imp.setBanco(804);
                    }

                    imp.setCmc7(rst.getString("cm7"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT\n"
                    + "	CP.FOR_CODIGO ID_FORNECEDOR,\n"
                    + "	RTRIM(F.FOR_CGC) CNPJ,\n"
                    + "	CP.CON_NLCTO NUMEROCDOCUMENTO,\n"
                    + "	210 ID_TIPOENTRADA,\n"
                    + " CASE\n"
                    + "     WHEN CON_NDOC LIKE '1/%' THEN '1'\n"
                    + "     WHEN CON_NDOC LIKE '2/%' THEN '2'\n"
                    + "     WHEN CON_NDOC LIKE '3/%' THEN '3'\n"
                    + "     WHEN CON_NDOC LIKE '4/%' THEN '4'\n"
                    + "     WHEN CON_NDOC LIKE '5/%' THEN '5'\n"
                    + "     WHEN CON_NDOC LIKE '6/%' THEN '6'\n"
                    + "     ELSE '1'\n"
                    + "	END PARCELA,"
                    + "	COALESCE(CP.CON_EMISSAO, CP.CON_DLCTO) DATAEMISSAO,\n"
                    + "	CP.CON_DLCTO DATAENTRADA,\n"
                    + " ROUND(CAST(CP.CON_VALOR AS REAL),2) AS VALOR,"
                    + "	COALESCE(CP.CON_HISTORICO1, '') CON_HISTORICO1,\n"
                    + "	COALESCE(CP.CON_HISTORICO2, '') CON_HISTORICO2,\n"
                    + "	CP.CON_VECTO VENCIMENTO\n"
                    + "FROM\n"
                    + "	CONTABIL CP\n"
                    + "JOIN FORNECEDOR F ON CP.FOR_CODIGO = F.FOR_CODIGO\n"
                    + "WHERE CP.CON_STATUS = 'X'\n"
                    + "	AND CP.EMP_CODIGO = " + getLojaOrigem() + ""*/
                    "SELECT\n"
                    + "	CP.FINFAT_REGISTRO ID,	\n"
                    + "	FF.FOR_CODIGO ID_FORNECEDOR,\n"
                    + "	RTRIM(F.FOR_CGC) CNPJ,\n"
                    + "	FF.FIN_NUMERONOTA NUMERODOCUMENTO,\n"
                    + "	210 AS ID_TIPOENTRADA,\n"
                    + "	FINFAT_PARCELA PARCELA,\n"
                    + "	FIN_DTEMISSAO DATAEMISSAO,\n"
                    + "	FIN_DTLANCAMENTO DATAENTRADA,\n"
                    + " FIN_ALTERACAO DATAALTERACAO,\n"
                    + "	FINFAT_VALORTOTAL VALOR,\n"
                    + "	FINFAT_JURO JUROS,\n"
                    + "	FINFAT_DESCONTO DESCONTO,\n"
                    + "	FINFAT_VENCIMENTO DATAVENCIMENTO,\n"
                    + "	FIN_HISTORICO OBSERVACAO,\n"
                    + "	FINFAT_OBS OBSERVACAO2\n"
                    + "FROM\n"
                    + "	FIN_FINANCEIRO FF\n"
                    + "JOIN FIN_FATURA CP ON FF.FIN_REGISTRO = CP.FIN_REGISTRO\n"
                    + "JOIN FORNECEDOR F ON F.FOR_CODIGO = FF.FOR_CODIGO\n"
                    + "WHERE\n"
                    + "	FF.FIN_STATUS = 'A'\n"
                    + "	AND CP.FINFAT_DTPAGAMENTO IS NULL\n"
                    + "	AND FF.EMP_CODIGO = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    String doc = Utils.formataNumero(rst.getString("numerodocumento"));

                    imp.setNumeroDocumento(doc);

                    if (doc != null && !"".equals(doc)) {
                        if (doc.length() > 6) {
                            imp.setNumeroDocumento(doc.substring(0, 6));
                        }
                    }
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setIdTipoEntradaVR(rst.getInt("id_tipoentrada"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setValor(rst.getDouble("valor"));

                    imp.setObservacao((rst.getString("observacao") == null ? "" : rst.getString("observacao")) + " "
                            + (rst.getString("observacao2") == null ? "" : rst.getString("observacao2")));
                    imp.setVencimento(rst.getDate("datavencimento"));
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("datavencimento"), imp.getValor());
                    parc.setNumeroParcela(rst.getInt("parcela"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public void corrigirObservacoesForn() throws Exception {

        ProgressBar.setStatus("Carregando dados dos fornecedores...");
        List<FornecedorIMP> forns = getFornecedores();

        ProgressBar.setStatus("Carregando fornecedores anteriores...");
        MultiMap<String, FornecedorAnteriorVO> ants = new FornecedorAnteriorDAO().getAnteriores();
        Conexao.begin();
        try {
            ProgressBar.setStatus("Atualizando telefone e observacoes...");
            ProgressBar.setMaximum(forns.size());
            try (Statement stm = Conexao.createStatement()) {
                for (FornecedorIMP imp : forns) {
                    FornecedorAnteriorVO ant = ants.get(
                            getSistema(),
                            getLojaOrigem(),
                            imp.getImportId()
                    );
                    if (ant != null) {
                        stm.execute("update fornecedor set observacao = "
                                + SQLUtils.stringSQL("IMPORTADO VR\n" + imp.getObservacao()) + ", telefone = "
                                + SQLUtils.stringSQL(imp.getTel_principal())
                                + " where id = " + ant.getCodigoAtual().getId());
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void corrigirObservacoesCli() throws Exception {

        ProgressBar.setStatus("Carregando dados dos clientes...");
        List<ClienteIMP> forns = getClientes();

        ProgressBar.setStatus("Carregando clientes anteriores...");
        MultiMap<String, ClientePreferencialAnteriorVO> ants = new ClientePreferencialAnteriorDAO().getAnteriores(
                getSistema(),
                getLojaOrigem()
        );
        Conexao.begin();
        try {
            ProgressBar.setStatus("Atualizando telefone e observacoes...");
            ProgressBar.setMaximum(forns.size());
            try (Statement stm = Conexao.createStatement()) {
                for (ClienteIMP imp : forns) {
                    ClientePreferencialAnteriorVO ant = ants.get(
                            getSistema(),
                            getLojaOrigem(),
                            imp.getId()
                    );
                    if (ant != null) {
                        stm.execute("update clientepreferencial set observacao2 = "
                                + SQLUtils.stringSQL("IMPORTADO VR\n" + imp.getObservacao()) + ", telefone = "
                                + SQLUtils.stringSQL(imp.getTelefone())
                                + " where id = " + ant.getCodigoAtual().getId());
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	  item.PRO_CODIGO idproduto,\n"
                    + "	  promo.PME_DATA_INI datainicio,\n"
                    + "	  promo.PME_DATA_FIN datafim,\n"
                    + "	  prod.PRO_VENDA preconormal,\n"
                    + "	  promo.PME_VALOR precooferta\n"
                    + "from\n"
                    + "	  TB_PROMOCAO_ESPECIAL promo\n"
                    + "left join TB_PROMOCAO_ESPECIAL_PRODUTOS item on promo.PME_CODIGO = item.PME_CODIGO\n"
                    + "left join PRODUTOS prod on prod.PRO_CODIGO = item.PRO_CODIGO\n"
                    + "where\n"
                    + "	  promo.PME_STATUS = 'A'\n"
                    + "	  and promo.PME_DATA_FIN is not NULL\n"
                    + "   and promo.PME_DATA_FIN >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "' "
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoNormal(rst.getDouble("preconormal"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
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
        return new GestoraDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new GestoraDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
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
                    + "    DISTINCT \n"
                    + "    COM_REGISTRO as id_venda,\n"
                    + "    CASE \n"
                    + "        WHEN COM_NCUPOM = 0 THEN COM_REGISTRO\n"
                    + "        ELSE COM_NCUPOM \n"
                    + "    END as numerocupom,\n"
                    + "    ECF_GT as ecf,\n"
                    + "    DATA_PROCESSO as data,\n"
                    + "    COM_HORA as hora,\n"
                    + "   cast(COM_TOTAL as float) as valor,\n"
                    + "    CLI_CODIGO as id_cliente,\n"
                    + "    CLI_CPFCGC as cpf,\n"
                    + "    CLI_NOME as nomecliente,\n"
                    + "    MOTIVO_CANCELAMENTO as cancelado\n"
                    + "FROM\n"
                    + "    CP_11_2023\n"
                    + "WHERE\n"
                    + "    TRY_CONVERT(DATE, DATA_PROCESSO) >= '" + strDataInicio + "' AND TRY_CONVERT(DATE, DATA_PROCESSO) <= '" + strDataTermino + "'\n"
                    + "and COM_TOTAL > 0  AND COM_REGISTRO != 1405612\n";
//                    + "UNION\n"
//                    + "SELECT\n"
//                    + "    DISTINCT \n"
//                    + "    COM_REGISTRO as id_venda,\n"
//                    + "    CASE \n"
//                    + "        WHEN COM_NCUPOM = 0 THEN COM_REGISTRO \n"
//                    + "        ELSE COM_NCUPOM \n"
//                    + "    END as numerocupom,\n"
//                    + "    ECF_GT as ecf,\n"
//                    + "    DATA_PROCESSO as data,\n"
//                    + "    COM_HORA as hora,\n"
//                    + "    COM_TOTAL as valor,\n"
//                    + "    CLI_CODIGO as id_cliente,\n"
//                    + "    CLI_CPFCGC as cpf,\n"
//                    + "    CLI_NOME as nomecliente,\n"
//                    + "    MOTIVO_CANCELAMENTO as cancelado\n"
//                    + "FROM\n"
//                    + "    CP_08_2023\n"
//                    + "WHERE\n"
//                    + "    TRY_CONVERT(DATE, DATA_PROCESSO) >= '" + strDataInicio + "' AND TRY_CONVERT(DATE, DATA_PROCESSO) <= '" + strDataTermino + "'"
//                    + "and COM_TOTAL > 0";
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

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id"));
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
                    = "SELECT\n"
                    + "DISTINCT \n"
                    + "    COM_REGISTRO as id_venda,\n"
                    + "    PRO_CODIGO as id_produto,\n"
                    + "    concat(COM_REGISTRO,PRO_CODIGO, ROW_NUMBER() OVER(order by COM_REGISTRO )) as id,\n"
                    + "    SAI_REGISTRO as nritem,\n"
                    + "    pro_unidade as unidade,\n"
                    + "    pro_descricao as descricao,\n"
                    + "    SAI_QTDE as quantidade,\n"
                    + "    (cast(SAI_TOTAL as float)/SAI_QTDE) as valor,\n"
                    + "    SAI_DESCONTO_ITEM as desconto\n"
                    + "FROM\n"
                    + "    SP_11_2023\n"
                    + "WHERE\n"
                    + "    TRY_CONVERT(DATE, DATA_PROCESSO) >= '" + VendaIterator.FORMAT.format(dataInicio) + "' AND TRY_CONVERT(DATE, DATA_PROCESSO) <= '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "AND COM_REGISTRO != 1405612";
//                    + "UNION\n"
//                    + "\n"
//                    + "SELECT\n"
//                    + "DISTINCT \n"
//                    + "    COM_REGISTRO as id_venda,\n"
//                    + "    PRO_CODIGO as id_produto,\n"
//                    + "    concat(COM_REGISTRO,PRO_CODIGO,SAI_REGISTRO) as id,\n"
//                    + "    SAI_REGISTRO as nritem,\n"
//                    + "    pro_unidade as unidade,\n"
//                    + "    pro_descricao as descricao,\n"
//                    + "    SAI_QTDE as quantidade,\n"
//                    + "    SAI_TOTAL as valor,\n"
//                    + "    SAI_DESCONTO_ITEM as desconto\n"
//                    + "FROM\n"
//                    + "    SP_08_2023\n"
//                    + "WHERE\n"
//                    + "   TRY_CONVERT(DATE, DATA_PROCESSO) >= '" + VendaIterator.FORMAT.format(dataInicio) + "' AND TRY_CONVERT(DATE, DATA_PROCESSO) <= '" + VendaIterator.FORMAT.format(dataTermino) + "'";
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
