package vrimplantacao2.dao.interfaces.gestora;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class GestoraDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Gestora";
    }
    
    private Date vendaDataIni;
    private Date vendaDataFim;

    public void setVendaDataIni(Date vendaDataIni) {
        this.vendaDataIni = vendaDataIni;
    }

    public void setVendaDataFim(Date vendaDataFim) {
        this.vendaDataFim = vendaDataFim;
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.pro_codigo id,\n"
                    + "	p.PRO_DATA_CADASTRO datacadastro,\n"
                    + "	ean.ean,\n"
                    + "	ean.qtdEmbalagem,\n"
                    + "	case p.PRO_BALANCA when 'S' then 1 else 0 end eBalanca,\n"
                    + "	rtrim(ltrim(p.PRO_UNIDADE)) tipoEmbalagem,\n"
                    + "	p.PRO_VALIDADE validade,\n"
                    + "	p.PRO_DESCRICAO descricaocompleta,\n"
                    + "	replace(p.pro_desc_etiqueta,'  ','') descricaoreduzida,\n"
                    + "	p.PRO_DESCRICAO descricaocompleta,\n"
                    + "	p.dep_codigo merc1,\n"
                    + "	p.gru_codigo merc2,\n"
                    + "	p.SUB_CODIGO merc3,\n"
                    + "	p.PRO_PAI idFamiliaProduto,\n"
                    + "	p.PRO_PESOLIQUIDO pesoliquido,\n"
                    + "	p.PRO_PESOLIQUIDO pesobruto,\n"
                    + "	p.PRO_MINIMO estoqueminimo,\n"
                    + "	p.pro_estoque estoque,\n"
                    + "	p.PRO_MARGEM margem,\n"
                    + "	p.PRO_CUSTO custosemimposto,\n"
                    + "	p.PRO_CUSTOREAL custocomimposto,\n"
                    + "	p.PRO_VENDA precovenda,\n"
                    + "	case when p.PRO_STATUS in ('E','I') then 0 else 1 end situacaoCadastro,\n"
                    + "	coalesce(p.PRO_CLASFISCAL,'') ncm,\n"
                    + "	coalesce(p.PRO_CEST,'') cest,\n"
                    + "	p.PRO_SIT_TRIBUTARIA,\n"
                    + "	case substring(tri_sweda,1,1)\n"
                    + "	when 'F' then 60\n"
                    + "	when 'T' then 0\n"
                    + "	else 40 end as icmsCst,\n"
                    + "	t.TRI_ALIQUOTA icmsAliq,\n"
                    + "	t.TRI_REDUCAO icmsRed,\n"
                    + "	p.PRO_CST_PIS_ENTRADA piscofinsCredito,\n"
                    + "	p.PRO_CST_PIS piscofinsSaida,\n"
                    + "	p.natr_codigo piscofinsNatureza\n"
                    + "from \n"
                    + "	(select \n"
                    + "		case when (pro_status = 'B') and (coalesce(pro_pai,0) > 0) then PRO_PAI else PRO_CODIGO end id,\n"
                    + "		pro_barra ean,\n"
                    + "		case when PRO_QTDE_BAIXAR < 1 then 1 else pro_qtde_baixar end qtdEmbalagem\n"
                    + "	from produtos) ean\n"
                    + "	join produtos p on\n"
                    + "		p.PRO_CODIGO = ean.id\n"
                    + "	left join tributacao t on\n"
                    + "		p.tri_codigo = t.tri_codigo\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
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
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaoCadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCst(rst.getInt("icmsCst"));
                    imp.setIcmsAliq(rst.getDouble("icmsAliq"));
                    imp.setIcmsReducao(rst.getDouble("icmsRed"));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofinsCredito")));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofinsSaida")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofinsNatureza")));

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
                    + "	c.CLI_LIMITE_CHEQ limite_cheque,\n"
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

    /*public void importarCreditoRotativo(int vrLoja) throws Exception {
     ProgressBar.setStatus("Carregando dados...Receber Cliente...");
     List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCliente();
     new ReceberCreditoRotativoDAO().salvar(vReceberCliente, vrLoja);
     }

     public void importarCheque(int vrLoja) throws Exception {
     ProgressBar.setStatus("Carregando dados...Cheque Receber...");
     List<ReceberChequeVO> vReceberCheque = carregarReceberCheque();
     new ReceberChequeDAO().salvar2(vReceberCheque, vrLoja);
     }

     private List<ReceberChequeVO> carregarReceberCheque() throws Exception {
     List<ReceberChequeVO> result = new ArrayList<>();
        
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n" +
     "	c.CLI_CPFCGC cpf,\n" +
     "	ch.CHE_NUMERO numerocheque,\n" +
     "	ch.CHE_BANCO id_banco,\n" +
     "	coalesce(ch.CHE_AGENCIA,'') agencia,\n" +
     "	coalesce(ch.CHE_CONTA,'') conta,\n" +
     "	ch.CHE_DATA data,\n" +
     "	ch.CHE_VALOR valor,\n" +
     "	ch.CHE_DATA datadeposito,\n" +
     "	c.CLI_RGINS rg,\n" +
     "	c.CLI_TELEFONE1 telefone,\n" +
     "	c.CLI_NOME nome,\n" +
     "	ch.CHE_HISTORICO observacao,\n" +
     "	coalesce(ch.CHE_STATUS,'') situacao,\n" +
     "	ch.CHE_CMC7 cm7,\n" +
     "	ch.CHE_JUROS valorjuros\n" +
     "from \n" +
     "	CHEQUE_REC ch\n" +
     "	join CLIENTES c on ch.CLI_CODIGO = c.CLI_CODIGO"
     )) {
     while (rst.next()) {
     ReceberChequeVO cheq = new ReceberChequeVO();
     cheq.setCpf(Utils.stringToLong(rst.getString("cpf")));
     cheq.setNumerocheque(Utils.stringToInt(rst.getString("numerocheque")));
     cheq.setId_banco(Utils.stringToInt(rst.getString("id_banco")));
     cheq.setAgencia(rst.getString("agencia"));
     cheq.setConta(rst.getString("conta"));
     cheq.setData(rst.getDate("data"));
     cheq.setValor(rst.getDouble("valor"));
     cheq.setDatadeposito(cheq.getData());
     cheq.setRg(rst.getString("rg"));
     cheq.setTelefone(rst.getString("telefone"));
     cheq.setNome(rst.getString("nome"));
     cheq.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
     switch(rst.getString("situacao").trim()) {
     case "E": {
     cheq.setId_tipoalinea(31);
     cheq.setId_situacaorecebercheque(2);
     }; break;
     case "R": {
     cheq.setId_tipoalinea(0);
     cheq.setId_situacaorecebercheque(1);
     ReceberChequeItemVO baixa = new ReceberChequeItemVO();
     baixa.setValor(cheq.getValor());
     baixa.setDatabaixa(rst.getDate("data"));
     baixa.setDatapagamento(rst.getDate("data"));
     baixa.setObservacao("IMPORTADO VR");
     baixa.setId_loja(1);
     cheq.getvBaixa().add(baixa);
     }; break;
     case "P": {
     cheq.setId_tipoalinea(0);
     cheq.setId_situacaorecebercheque(1);
     ReceberChequeItemVO baixa = new ReceberChequeItemVO();
     baixa.setValor(cheq.getValor());
     baixa.setDatabaixa(rst.getDate("data"));
     baixa.setDatapagamento(rst.getDate("data"));
     baixa.setObservacao("IMPORTADO VR");
     baixa.setId_loja(1);
     cheq.getvBaixa().add(baixa);
     }; break;
     default: {
     cheq.setId_tipoalinea(0);
     cheq.setId_situacaorecebercheque(0);
     }; break;
     }
     result.add(cheq);
     }
     }
     }
        
     return result;
     }*/
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ch.CHE_REGISTRO id,\n"
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
                    + "	ch.CLI_CODIGO = c.CLI_CODIGO"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
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
                    imp.setBanco(rst.getInt("id_banco"));
                    imp.setCmc7(rst.getString("cm7"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public void importarContasAPagar(int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados para comparação...");

        List<PagarOutrasDespesasVO> vPagarOutrasDespesas = carregarContasPagarGetWay(idLojaVR, getLojaOrigem());

        ProgressBar.setMaximum(vPagarOutrasDespesas.size());

        PagarOutrasDespesasDAO pagarOutrasDespesasDAO = new PagarOutrasDespesasDAO();
        pagarOutrasDespesasDAO.salvar(vPagarOutrasDespesas);
    }

    private List<PagarOutrasDespesasVO> carregarContasPagarGetWay(int idLojaVR, String lojaOrigem) throws Exception {
        List<PagarOutrasDespesasVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cp.FOR_CODIGO id_fornecedor,\n"
                    + "	cp.CON_NLCTO numerocdocumento,\n"
                    + "	210 id_tipoentrada,\n"
                    + "	coalesce(cp.CON_EMISSAO, cp.CON_DLCTO) dataemissao,\n"
                    + "	cp.CON_DLCTO dataentrada,\n"
                    + "	cp.CON_VALOR valor,\n"
                    + "	0 id_situacaopagaroutrasdespesas,\n"
                    + "	coalesce(cp.CON_HISTORICO1,'') CON_HISTORICO1,\n"
                    + "	coalesce(cp.CON_HISTORICO2,'') CON_HISTORICO2,\n"
                    + "	coalesce(cp.CON_DEBITO,'') CON_DEBITO, \n"
                    + "	coalesce(cp.CON_CREDITO,'') CON_CREDITO,\n"
                    + "	coalesce(cp.usu_login,'') usu_login,\n"
                    + "	coalesce(cp.CON_BARRA,'') CON_BARRA,\n"
                    + "	coalesce(cp.CON_NDOC,'') CON_NDOC,\n"
                    + "	cp.CON_VECTO vencimento\n"
                    + "from \n"
                    + "	contabil cp\n"
                    + "	join FORNECEDOR f on cp.FOR_CODIGO = f.FOR_CODIGO\n"
                    + "where \n"
                    + "	cp.CON_STATUS = 'X' and cp.EMP_CODIGO = " + Utils.stringToInt(lojaOrigem)
            )) {
                while (rst.next()) {
                    PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();

                    vo.setId_loja(idLojaVR);
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setNumerodocumento(Utils.stringToInt(rst.getString("numerocdocumento")));
                    vo.setId_tipoentrada(0);
                    vo.setDataemissao(rst.getDate("dataemissao"));
                    vo.setDataentrada(rst.getDate("dataentrada"));
                    vo.setValor(rst.getDouble("valor"));
                    vo.setId_situacaopagaroutrasdespesas(0);
                    String obs = "IMPORTADO VR";
                    if (!"".equals(rst.getString("CON_DEBITO").trim())) {
                        obs += "|DEB: " + Utils.acertarTexto(rst.getString("CON_DEBITO"));
                    }
                    if (!"".equals(rst.getString("CON_CREDITO").trim())) {
                        obs += "|CRED: " + Utils.acertarTexto(rst.getString("CON_CREDITO"));
                    }
                    if (!"".equals(rst.getString("CON_HISTORICO1").trim())) {
                        obs += "|OBS1: " + Utils.acertarTexto(rst.getString("CON_HISTORICO1"));
                    }
                    if (!"".equals(rst.getString("CON_HISTORICO2").trim())) {
                        obs += "|nOBS2: " + Utils.acertarTexto(rst.getString("CON_HISTORICO2"));
                    }
                    if (!"".equals(rst.getString("usu_login").trim())) {
                        obs += "|nCriado por: " + Utils.acertarTexto(rst.getString("usu_login"));
                    }
                    if (!"".equals(rst.getString("CON_BARRA").trim())) {
                        obs += "|Barra: " + Utils.acertarTexto(rst.getString("CON_BARRA"));
                    }
                    if (!"".equals(rst.getString("CON_NDOC").trim())) {
                        obs += "|nNum. Doc: " + Utils.acertarTexto(rst.getString("CON_NDOC"));
                    }
                    vo.setObservacao(Utils.acertarTexto(obs, 280));
                    PagarOutrasDespesasVencimentoVO venc = new PagarOutrasDespesasVencimentoVO();
                    venc.setDatavencimento(rst.getDate("vencimento"));
                    venc.setValor(vo.getValor());
                    vo.getvPagarOutrasDespesasVencimento().add(venc);
                    vo.setId_tipopiscofins(13);

                    result.add(vo);
                }
            }
        }

        return result;
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
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new GestoraVendaIterator(this.vendaDataIni, this.vendaDataFim);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new GestoraVendaItemIterator(this.vendaDataIni, this.vendaDataFim);
    }
}
