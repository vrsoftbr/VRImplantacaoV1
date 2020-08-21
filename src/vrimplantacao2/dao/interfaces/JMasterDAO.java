package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class JMasterDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_dataTermino;
    
    private String complemento = "";
    
    public void setComplemento(String complemento) {
        if (complemento == null) complemento = "";
        this.complemento = complemento.trim();
    }

    @Override
    public String getSistema() {
        return "JMaster" + ("".equals(this.complemento) ? "" : "-" + this.complemento);
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select LOJCODIGO, LOJRAZAO, LOJCGC from dbo.CADLOJ order by LOJCODIGO"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(
                        rs.getString("LOJCODIGO").trim(),
                        String.format("%s - %s",
                                rs.getString("LOJRAZAO").trim(),
                                rs.getString("LOJCGC").trim()
                        )
                ));
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with lj as (select LOJCODIGO id, LOJESTADO uf from CADLOJ l where l.LOJCODIGO = " + getLojaOrigem() + ")\n" +
                    "select distinct\n" +
                    "	'E' + aliq.NATCODIGO id,\n" +
                    "	aliq.NATDESCRICAO descricao,\n" +
                    "	aliq.NATMENSAGEM mensagem,\n" +
                    "	aliq.NATCST cst,\n" +
                    "	aliq.NATICMCOMPRA icms,\n" +
                    "	aliq.NATICMREDCMP reduzido\n" +
                    "from\n" +
                    "	cadnat aliq\n" +
                    "	join lj on\n" +
                    "		aliq.NATESTADO = lj.uf\n" +
                    "	join LOJITM li on\n" +
                    "		li.LITNATFISCAL = aliq.NATCODIGO\n" +
                    "where\n" +
                    "	NATTABNAT = 1\n" +
                    "union\n" +
                    "select distinct\n" +
                    "	'S' + aliq.NATCODIGO id,\n" +
                    "	aliq.NATDESCRICAO descricao,\n" +
                    "	aliq.NATMENSAGEM mensagem,\n" +
                    "	aliq.NATCST cst,\n" +
                    "	aliq.NATICM icms,\n" +
                    "	aliq.NATICMREDUZ reduzido\n" +
                    "from\n" +
                    "	cadnat aliq\n" +
                    "	join lj on\n" +
                    "		aliq.NATESTADO = lj.uf\n" +
                    "	join LOJITM li on\n" +
                    "		li.LITNATFISCAL = aliq.NATCODIGO\n" +
                    "where\n" +
                    "	NATTABNAT = 1\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rs.next()) {
                    String descricao = String.format(
                            "%s - %s",
                            rs.getString("descricao"),
                            rs.getString("mensagem")
                    );
                    int cst = Utils.stringToInt(rs.getString("cst"));
                    double aliq = rs.getDouble("icms");
                    double reduzido = rs.getDouble("reduzido");
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            descricao,
                            cst,
                            aliq,
                            reduzido
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "famcodigo, "
                    + "famdescricao "
                    + "FROM cadfam "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("famcodigo"));
                    imp.setDescricao(rst.getString("famdescricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "secsecao, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao <> 0 "
                    + "and secgrupo = 0 "
                    + "and secsubgrupo = 0 "
                    + "order by secsecao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("secsecao"));
                    imp.setDescricao(rst.getString("secdescri"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select secsecao, secgrupo, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao > 0 "
                    + "and secgrupo > 0 "
                    + "and secsubgrupo = 0 "
                    + "order by secsecao, secgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("secsecao"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("secgrupo"),
                                rst.getString("secdescri")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select secsecao, secgrupo, secsubgrupo, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao > 0 "
                    + "and secgrupo > 0 "
                    + "and secsubgrupo > 0 "
                    + "order by secsecao, secgrupo, secsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("secsecao"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("secgrupo"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("secsubgrupo"),
                                    rst.getString("secdescri")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.NUMERO_PARCELA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.TECLA_ASSOCIADA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.GERCODREDUZ id,\n" +
                    "	p.GERENTLIN datacadastro,\n" +
                    "	ean.EANCODIGO ean,\n" +
                    "	ean.EANQTDE qtdembalagem,\n" +
                    "	p.GERTIPVEN unidade,\n" +
                    "	p.gerembven qtdembalagemcotacao,\n" +
                    "	p.GERPESOVARIAVEL e_balanca,\n" +
                    "	p.GERFRACAO,\n" +
                    "	p.GERVALIDADE validade,\n" +
                    "	p.GERDESCRICAO descricaocompleta,\n" +
                    "	p.GERDESCREDUZ descricaoreduzida,\n" +
                    "	case when p.GERVENDAPARC = 0 then 1 else p.GERVENDAPARC end parcelas,\n" +
                    "	p.GERSECAO merc1,\n" +
                    "	p.GERGRUPO merc2,\n" +
                    "	p.GERSUBGRUPO merc3,\n" +
                    "	p.GERFAMILIA id_familia,\n" +
                    "	p.GERPESOBRT pesobruto,\n" +
                    "	p.GERPESOLIQ pesoliquido,\n" +
                    "	est.LITESTQMIN estoqueminimo,\n" +
                    "	est.LITESTQL estoque,\n" +
                    "	est.LITMRGVEN1 margem,\n" +
                    "	est.LITCUSREP custocomimposto,\n" +
                    "	est.LITCUSREP custosemimposto,\n" +
                    "	est.LITCUSMED customedio,\n" +
                    "	est.LITPRCVEN1 precovenda,\n" +
                    "	p.GERTECLA teclassociada,\n" +
                    "	p.GERSAILIN saidadelinha,\n" +
                    "	p.GERNBM ncm,\n" +
                    "	p.GERCEST cest,\n" +
                    "	p.GERTIPOPIS piscofins_saida,\n" +
                    "	p.GERTIPOPIE piscofins_entrada,\n" +
                    "	'E' + est.LITNATFISCAL id_icms_entrada,\n" +
                    "	'S' + est.LITNATFISCAL id_icms_saida,\n" +
                    "	p.GERCODFOR fornecedorfabricante\n" +
                    "from\n" +
                    "	dbo.CADGER p\n" +
                    "	JOIN dbo.LOJITM est ON\n" +
                    "		p.GERCODREDUZ = est.LITCODREDUZ \n" +
                    "	JOIN dbo.LOJSEC ON\n" +
                    "		est.LITLOJA = dbo.LOJSEC.LSCLOJA AND \n" +
                    "		p.GERSECAO = dbo.LOJSEC.LSCSECAO\n" +
                    "	left join CADEAN ean on\n" +
                    "		p.GERCODREDUZ = ean.EANCODREDUZ\n" +
                    "where\n" +
                    "	est.LITLOJA = " + getLojaOrigem() + "\n" +
                    "order by id, ean"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    try {
                        imp.setDataCadastro(format.parse(rs.getString("datacadastro")));
                    } catch (ParseException ex) {
                        System.out.println("Data inválida - id:" + imp.getImportId() + " - " + rs.getString("datacadastro"));
                    }
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    
                    int ean = Utils.stringToInt(rs.getString("ean"), -2);
                    ProdutoBalancaVO bal = balanca.get(ean);
                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                    } else {                    
                        imp.setEan(rs.getString("ean"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));                    
                        imp.seteBalanca("S".equals(rs.getString("e_balanca")));
                        imp.setValidade(rs.getInt("validade"));
                    }
                    
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setNumeroparcela(rs.getInt("parcelas"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoAnteriorComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoAnteriorSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoMedio(rs.getDouble("customedio"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setTeclaAssociada(rs.getInt("teclassociada"));
                    try {
                        java.util.Date date = format.parse(rs.getString("saidadelinha"));
                        imp.setSituacaoCadastro(
                                date.before(new java.util.Date()) ?
                                SituacaoCadastro.EXCLUIDO :
                                SituacaoCadastro.ATIVO
                        );
                    } catch (ParseException ex) {
                        System.out.println("Data inválida FORALINHA - id:" + imp.getImportId() + " - " + rs.getString("saidadelinha"));
                    }
                    imp.setNcm(Utils.formataNumero(rs.getString("ncm")).substring(0, 8));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofins_entrada"));
                    imp.setIcmsCreditoId(rs.getString("id_icms_entrada"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms_entrada"));
                    imp.setIcmsDebitoId(rs.getString("id_icms_saida"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms_saida"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms_saida"));
                    imp.setIcmsConsumidorId(rs.getString("id_icms_saida"));
                    imp.setFornecedorFabricante(rs.getString("fornecedorfabricante"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.QTD_EMBALAGEM_EAN) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select EANCODREDUZ, EANCODIGO, EANQTDE \n"
                        + "from CADEAN\n"
                        + "where EANQTDE > 1\n"
                        + "and EANCODIGO > 999999"
                )) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("EANCODREDUZ"));
                    imp.setEan(rst.getString("EANCODIGO"));
                    imp.setQtdEmbalagem(rst.getInt("EANQTDE"));
                    vResult.add(imp);
                }
            }
            return vResult;
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        Utils util = new Utils();
        String observacao = null, dataCadastro;
        java.sql.Date data = null;
        DateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.forcodigo id,\n" +
                    "	f.FORRAZAO razao,\n" +
                    "	f.FORDESCRI nome,\n" +
                    "	f.FORCGC cnpj,\n" +
                    "	f.FORINSC ie_rg,\n" +
                    "	f.FORDTFLINHA dataforalinha,\n" +
                    "	f.FORENDERECO endereco,\n" +
                    "	f.FORNUMERO numero,\n" +
                    "	f.FORCOMPL complemento,\n" +
                    "	f.FORBAIRRO bairro,\n" +
                    "	f.FORCIDADE cidade,\n" +
                    "	f.FORESTADO uf,\n" +
                    "	f.FORCEP cep,\n" +
                    "	f.FORDDD ddd,\n" +
                    "	f.FORTELEFONE telefone,\n" +
                    "	f.FORDTCAD datacadastro,\n" +
                    "	f.FORENTREGA prazoentrega,\n" +
                    "	f.FORPRAZO prazopedido,\n" +
                    "	f.FORFAX fax,\n" +
                    "	f.FOREMAIL email,\n" +
                    "	coalesce(rtrim(ltrim(f.FORCONTATO)), '') contato,\n" +
                    "	f.FORDESCISS ,\n" +
                    "	f.forbanco banco,\n" +
                    "	f.FORAGENCIA agencia,\n" +
                    "	f.FORCONTA conta\n" +
                    "from\n" +
                    "	CADFOR f\n" +
                    "order by\n" +
                    "	forcodigo"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    try {
                        java.util.Date foraLinha = fmt.parse(rs.getString("dataforalinha"));
                        imp.setAtivo(!foraLinha.before(new java.util.Date()));
                    } catch (ParseException ex) {
                        System.out.println(String.format("Data inválida - ID: %s - %s", rs.getString("id"), rs.getString("dataforalinha")));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    final String telefone = (rs.getString("ddd") == null ? "" : rs.getString("ddd"))
                            + rs.getString("telefone");
                    imp.setTel_principal(telefone);
                    try {
                        imp.setDatacadastro(fmt.parse(rs.getString("datacadastro")));
                    } catch (ParseException ex) {
                        System.out.println(String.format("Data cadastro inválida - ID: %s - %s", rs.getString("id"), rs.getString("datacadastro")));
                    }
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setPrazoPedido(rs.getInt("prazopedido"));
                    imp.addTelefone("FAX", rs.getString("fax"));
                    imp.addContato("".equals(rs.getString("contato")) ? "CONTATO" : rs.getString("contato"), telefone, null, TipoContato.COMERCIAL, rs.getString("email"));
                    if ((rs.getString("banco") != null)
                            && (!rs.getString("banco").trim().isEmpty())) {
                        observacao = "BANCO " + rs.getString("banco") + " ";
                    }
                    if ((rs.getString("agencia") != null)
                            && (!rs.getString("agencia").trim().isEmpty())) {
                        observacao = observacao + "AGENCIA " + rs.getString("agencia") + " ";
                    }
                    if ((rs.getString("conta") != null)
                            && (!rs.getString("conta").trim().isEmpty())) {
                        observacao = observacao + " CONTA " + rs.getString("conta");
                    }                    
                    imp.setObservacao(observacao);
                   
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "fitcodreduz, fitcodfor, fitreferencia, "
                    + "fitembfor, fittipfor "
                    + "from foritm "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fitcodfor"));
                    imp.setIdProduto(rst.getString("fitcodreduz"));
                    imp.setCodigoExterno(rst.getString("fitreferencia"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        Utils util = new Utils();
        String dataCadastro;
        java.sql.Date data = null;
        DateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.CLICLIENTE id,\n" +
                    "	c.CLICGC cnpj,\n" +
                    "	c.CLIINSEST ie,\n" +
                    "	c.CLIRAZAO razao,\n" +
                    "	c.CLIFANTASIA fantasia,\n" +
                    "	case when c.CLISITUACAO = 'I' then 0 else 1 end ativo,\n" +
                    "	c.CLIENDERECO endereco,\n" +
                    "	c.CLINUMERO numero,\n" +
                    "	c.CLIBAIRRO bairro,\n" +
                    "	c.CLICIDADE cidade,\n" +
                    "	c.CLIESTADO uf,\n" +
                    "	c.CLICEP cep,\n" +
                    "	c.CLIESTCIVIL estadocivil,\n" +
                    "	c.CLIANIVERSARIO dataaniversario,\n" +
                    "	c.CLIDTCADAS datacadastro,\n" +
                    "	c.CLISEXO sexo,\n" +
                    "	c.CLIEMPRESA empresa,\n" +
                    "	c.CLIDDD ddd,\n" +
                    "	c.CLITELEFONE telefone,\n" +
                    "	c.CLIDDDCOM dddcomercial,\n" +
                    "	c.CLITELEFONECOM telefonecomercial,\n" +
                    "	c.CLIDDDCEL dddcelular,\n" +
                    "	c.CLINROCEL celular,\n" +
                    "	c.CLICARGO cargo,\n" +
                    "	c.CLIRENDAM salario,\n" +
                    "	c.CLILIMITE limite,\n" +
                    "	c.CLICPFCONJUGE cpfconjuge,\n" +
                    "	c.CLIPAI pai,\n" +
                    "	c.CLIMAE mae,\n" +
                    "	c.CLIOBSERV1,\n" +
                    "	c.CLIOBSERV2,\n" +
                    "	c.CLIOBSERV3,	\n" +
                    "	c.CLIOBSERVACAO observacao,\n" +
                    "	c.CLIDIAPGTO diavencimento,\n" +
                    "	c.CLIULTDIA prazopagamento,\n" +
                    "	c.CLIEMAIL email,\n" +
                    "	c.CLIFAX fax\n" +
                    "from\n" +
                    "	cadcli c\n" +
                    "order by\n" +
                    "	c.clicliente"
            )) {
                while (rs.next()) {
                    
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEstadoCivil(rs.getString("estadocivil"));
                    try {
                        imp.setDataNascimento(fmt.parse(rs.getString("dataaniversario")));
                    } catch (ParseException ex) {
                        System.out.println(String.format("Data nascimento inválida ID: %s - %s", imp.getId(), rs.getString("dataaniversario")));
                    }
                    try {
                        imp.setDataCadastro(fmt.parse(rs.getString("datacadastro")));
                    } catch (ParseException ex) {
                        System.out.println(String.format("Data cadastro inválida ID: %s - %s", imp.getId(), rs.getString("dataaniversario")));
                    }
                    imp.setSexo(rs.getString("sexo"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setTelefone(formataTelefone(rs.getString("ddd"), rs.getString("telefone")));
                    imp.addTelefone("COMERCIAL", formataTelefone(rs.getString("dddcomercial"), rs.getString("telefonecomercial")));
                    imp.setCelular(formataTelefone(rs.getString("dddcelular"), rs.getString("celular")));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setCpfConjuge(rs.getString("cpfconjuge"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    StringBuilder obs = new StringBuilder();
                    
                    String obs1 = Utils.acertarTexto(rs.getString("CLIOBSERV1"));
                    if (!"".equals(obs1)) obs.append("OBS - ").append(obs1);

                    String obs2 = Utils.acertarTexto(rs.getString("CLIOBSERV2"));
                    if (!"".equals(obs2)) obs.append(!"".equals(obs.toString()) ? " -- " : "").append("OBS2 - ").append(obs1);
                    
                    String obs3 = Utils.acertarTexto(rs.getString("CLIOBSERV3"));
                    if (!"".equals(obs3)) obs.append(!"".equals(obs.toString()) ? " -- " : "").append("OBS3 - ").append(obs3);
                    
                    String obs4 = Utils.acertarTexto(rs.getString("observacao"));
                    if (!"".equals(obs4)) obs.append(!"".equals(obs.toString()) ? " -- " : "").append("OBS4 - ").append(obs4);
                    
                    imp.setObservacao2(obs.toString());
                    imp.setDiaVencimento(rs.getInt("diavencimento"));
                    imp.setPrazoPagamento(rs.getInt("prazopagamento"));
                    imp.setEmail(rs.getString("email"));
                    imp.setFax(rs.getString("fax"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        Utils util = new Utils();
        String dataEmissao, dataVencimento;
        java.sql.Date dataEmi = null, dataVenc = null;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "DTTEMISSAO, DTTVENCTO, \n"
                    + "DTTNOTA, DTTPARCELA, \n"
                    + "DTTCLIENTE, DTTVLRTIT,\n"
                    + "DTTOBSERVACAO, DTTPDV \n"
                    + "from DETTIT \n"
                    + "where DTTVLRPAGO = 0 "
            )) {
                while (rst.next()) {
                    if ((rst.getString("DTTEMISSAO") != null)
                            && (!rst.getString("DTTEMISSAO").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("DTTEMISSAO").substring(4, 6)),
                                Integer.parseInt(rst.getString("DTTEMISSAO").substring(6, 8)))) {
                            dataEmissao = rst.getString("DTTEMISSAO").trim().substring(0, 4);
                            dataEmissao = dataEmissao + "/" + rst.getString("DTTEMISSAO").trim().substring(4, 6);
                            dataEmissao = dataEmissao + "/" + rst.getString("DTTEMISSAO").trim().substring(6, 8);
                            dataEmi = new java.sql.Date(fmt.parse(dataEmissao).getTime());
                        } else {
                            dataEmissao = "";
                        }
                    } else {
                        dataEmissao = "";
                    }

                    if ((rst.getString("DTTVENCTO") != null)
                            && (!rst.getString("DTTVENCTO").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("DTTVENCTO").substring(4, 6)),
                                Integer.parseInt(rst.getString("DTTVENCTO").substring(6, 8)))) {
                            dataVencimento = rst.getString("DTTVENCTO").trim().substring(0, 4);
                            dataVencimento = dataVencimento + "/" + rst.getString("DTTVENCTO").trim().substring(4, 6);
                            dataVencimento = dataVencimento + "/" + rst.getString("DTTVENCTO").trim().substring(6, 8);
                            dataVenc = new java.sql.Date(fmt.parse(dataVencimento).getTime());
                        } else {
                            dataVencimento = "";
                        }
                    } else {
                        dataVencimento = "";
                    }

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(getSistema() + "-" + getLojaOrigem() + "-"
                            + rst.getString("DTTCLIENTE") + "-" + rst.getString("DTTNOTA") + "-" + rst.getString("DTTPARCELA"));
                    imp.setDataEmissao("".equals(dataEmissao) ? new Date(new java.util.Date().getTime()) : dataEmi);
                    imp.setDataVencimento("".equals(dataVencimento) ? new Date(new java.util.Date().getTime()) : dataVenc);
                    imp.setIdCliente(rst.getString("DTTCLIENTE"));
                    imp.setNumeroCupom(rst.getString("DTTNOTA"));
                    imp.setParcela(rst.getInt("DTTPARCELA"));
                    imp.setValor(rst.getDouble("DTTVLRTIT"));
                    imp.setEcf(rst.getString("DTTPDV"));
                    imp.setObservacao(rst.getString("DTTOBSERVACAO"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        Utils util = new Utils();
        String dataEmissao, dataDeposito;
        java.sql.Date dataEmi = null, dataDep = null;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "chrcgccpf, chrbanco, chragencia, chrconta, chrcheque, "
                    + "chremissao, chrvencto, chrrazao, chrinscrg, chrvalor, "
                    + "chrobserv1, chrobserv2, chrdtdeposito, chrpdv, chrtelefone "
                    + "from CHQREC "
                    + "where chrvlrpago = 0 "
                    + "and chrpagamento = 0 "
                    + "order by chremissao"
            )) {
                while (rst.next()) {
                    if ((rst.getString("chremissao") != null)
                            && (!rst.getString("chremissao").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("chremissao").substring(4, 6)),
                                Integer.parseInt(rst.getString("chremissao").substring(6, 8)))) {
                            dataEmissao = rst.getString("chremissao").trim().substring(0, 4);
                            dataEmissao = dataEmissao + "/" + rst.getString("chremissao").trim().substring(4, 6);
                            dataEmissao = dataEmissao + "/" + rst.getString("chremissao").trim().substring(6, 8);
                            dataEmi = new java.sql.Date(fmt.parse(dataEmissao).getTime());
                        } else {
                            dataEmissao = "";
                        }
                    } else {
                        dataEmissao = "";
                    }

                    if ((rst.getString("chrdtdeposito") != null)
                            && (!rst.getString("chrdtdeposito").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("chrdtdeposito").substring(4, 6)),
                                Integer.parseInt(rst.getString("chrdtdeposito").substring(6, 8)))) {
                            dataDeposito = rst.getString("chrdtdeposito").trim().substring(0, 4);
                            dataDeposito = dataDeposito + "/" + rst.getString("chrdtdeposito").trim().substring(4, 6);
                            dataDeposito = dataDeposito + "/" + rst.getString("chrdtdeposito").trim().substring(6, 8);
                            dataDep = new java.sql.Date(fmt.parse(dataDeposito).getTime());
                        } else {
                            dataDeposito = "";
                        }
                    } else {
                        dataDeposito = "";
                    }

                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("chrcgccpf") + "-" + rst.getString("chrbanco") + "-"
                            + rst.getString("chragencia") + "-" + rst.getString("chrconta") + "-" + rst.getString("chrcheque"));
                    imp.setCpf(rst.getString("chrcgccpf"));
                    imp.setRg(rst.getString("chrinscrg"));
                    imp.setTelefone(rst.getString("chrtelefone"));
                    imp.setNome(rst.getString("chrrazao"));
                    imp.setDate("".equals(dataEmissao) ? new Date(new java.util.Date().getTime()) : dataEmi);
                    imp.setDataDeposito("".equals(dataDeposito) ? new Date(new java.util.Date().getTime()) : dataDep);
                    imp.setValor(rst.getDouble("chrvalor"));
                    imp.setEcf(rst.getString("chrpdv"));
                    imp.setNumeroCheque(rst.getString("chrcheque"));
                    imp.setBanco(rst.getInt("chrbanco"));
                    imp.setAgencia(rst.getString("chragencia"));
                    imp.setConta(rst.getString("chrconta"));
                    imp.setObservacao("IMPORTADO VR " + rst.getString("chrobserv2") + " " + rst.getString("chrobserv2"));
                    imp.setAlinea(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        List<OfertaIMP> vResult = new ArrayList<>();
        String dataInicio, dataFinal;
        java.sql.Date dataIni = null, dataFim = null;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        Utils util = new Utils();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "cap.jocdataini, cap.jocdatafim, cap.jocobserv,\n"
                    + "det.JODCODREDUZ, pro.GERDESCRICAO, det.JODPRCVEN, pro.litprcven1,\n"
                    + "det.JODOBSERV\n"
                    + "from JORCAP cap\n"
                    + "inner join JORLOJ loj on loj.JOLNUMERO = cap.JOCNUMERO and loj.JOLLOJA = " + getLojaOrigem() + "\n"
                    + "inner join JORDET det on det.JODNUMERO = cap.JOCNUMERO\n"
                    + "inner join VPRODLOJA pro on pro.GERCODREDUZ = det.JODCODREDUZ\n"
                    + "where cap.jocdatafim > '" + fmt.format(new java.util.Date()) + "'\n"
                    + "order by cap.jocdatafim desc"
            )) {
                while (rst.next()) {

                    if ((rst.getString("jocdataini") != null)
                            && (!rst.getString("jocdataini").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("jocdataini").substring(4, 6)),
                                Integer.parseInt(rst.getString("jocdataini").substring(6, 8)))) {
                            dataInicio = rst.getString("jocdataini").trim().substring(0, 4);
                            dataInicio = dataInicio + "/" + rst.getString("jocdataini").trim().substring(4, 6);
                            dataInicio = dataInicio + "/" + rst.getString("jocdataini").trim().substring(6, 8);
                            dataIni = new java.sql.Date(fmt.parse(dataInicio).getTime());
                        } else {
                            dataInicio = "";
                        }
                    } else {
                        dataInicio = "";
                    }

                    if ((rst.getString("jocdatafim") != null)
                            && (!rst.getString("jocdatafim").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("jocdatafim").substring(4, 6)),
                                Integer.parseInt(rst.getString("jocdatafim").substring(6, 8)))) {
                            dataFinal = rst.getString("jocdatafim").trim().substring(0, 4);
                            dataFinal = dataFinal + "/" + rst.getString("jocdatafim").trim().substring(4, 6);
                            dataFinal = dataFinal + "/" + rst.getString("jocdatafim").trim().substring(6, 8);
                            dataFim = new java.sql.Date(fmt.parse(dataFinal).getTime());
                        } else {
                            dataFinal = "";
                        }
                    } else {
                        dataFinal = "";
                    }

                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("JODCODREDUZ"));
                    imp.setDataInicio("".equals(dataInicio) ? new Date(new java.util.Date().getTime()) : dataIni);
                    imp.setDataFim("".equals(dataFinal) ? new Date(new java.util.Date().getTime()) : dataFim);
                    imp.setPrecoOferta(rst.getDouble("JODPRCVEN"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    private String formataTelefone(String ddd, String telefone) {
        return (ddd == null ? "" : ddd) + Utils.formataNumero(telefone);
    }
    
}
