package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
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
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                    "order by id, ean"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("gercodreduz"));
                    imp.setEan(rst.getString("EANCODIGO"));
                    imp.setQtdEmbalagem(rst.getInt("gerembven"));
                    imp.setCodMercadologico1(rst.getString("gersecao"));
                    imp.setCodMercadologico2(rst.getString("gergrupo"));
                    imp.setCodMercadologico3(rst.getString("gersubgrupo"));
                    imp.setDescricaoCompleta(rst.getString("gerdescricao"));
                    imp.setDescricaoReduzida(rst.getString("gerdescreduz"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("LITTIPFOR"));
                    imp.setIdFamiliaProduto(rst.getString("gerfamilia"));
                    imp.setMargem(rst.getDouble("litmrgven1"));
                    imp.setPrecovenda(rst.getDouble("litprcven1"));
                    imp.setCustoComImposto(rst.getDouble("litcusrep"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("litestqmin"));
                    imp.setEstoque(rst.getDouble("litestql"));

                    if ((rst.getString("gernbm") != null)
                            && (!rst.getString("gernbm").trim().isEmpty())) {
                        if (rst.getString("gernbm").trim().length() > 8) {
                            imp.setNcm(rst.getString("gernbm").trim().substring(0, 8));
                        } else {
                            imp.setNcm(rst.getString("gernbm").trim());
                        }
                    }

                    imp.setCest(rst.getString("gercest"));
                    imp.setPiscofinsCstDebito(rst.getInt("gertipopis"));
                    imp.setPiscofinsCstCredito(rst.getInt("gertipopie"));
                    imp.setIcmsDebitoId(rst.getString("litnatfiscal"));
                    imp.setIcmsCreditoId(rst.getString("litnatfiscal"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

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
        List<FornecedorIMP> vResult = new ArrayList<>();
        Utils util = new Utils();
        String observacao = null, dataCadastro;
        java.sql.Date data = null;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "forcodigo, fordescri, forcgc, forendereco, fornumero, "
                    + "forbairro, forcidade, forestado, forcep, forddd, fortelefone, "
                    + "forfax, forinsc, fordtcad, forsituacao, forbanco, "
                    + "foragencia, forconta, forrazao, foremail, forpj  "
                    + "from CADFOR "
                    + "order by forcodigo "
            )) {
                while (rst.next()) {
                    if ((rst.getString("fordtcad") != null)
                            && (!rst.getString("fordtcad").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("fordtcad").substring(4, 6)),
                                Integer.parseInt(rst.getString("fordtcad").substring(6, 8)))) {
                            dataCadastro = rst.getString("fordtcad").trim().substring(0, 4);
                            dataCadastro = dataCadastro + "/" + rst.getString("fordtcad").trim().substring(4, 6);
                            dataCadastro = dataCadastro + "/" + rst.getString("fordtcad").trim().substring(6, 8);
                            data = new java.sql.Date(fmt.parse(dataCadastro).getTime());
                        } else {
                            dataCadastro = "";
                        }
                    } else {
                        dataCadastro = "";
                    }

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("forcodigo"));
                    imp.setRazao(rst.getString("forrazao"));
                    imp.setFantasia(rst.getString("fordescri"));
                    imp.setEndereco(rst.getString("forendereco"));
                    imp.setNumero(rst.getString("fornumero"));
                    imp.setBairro(rst.getString("forbairro"));
                    imp.setMunicipio(rst.getString("forcidade"));
                    imp.setUf(rst.getString("forestado"));
                    imp.setCep(rst.getString("forcep"));
                    imp.setTel_principal((rst.getString("forddd") == null ? "" : rst.getString("forddd"))
                            + rst.getString("fortelefone"));
                    imp.setCnpj_cpf(rst.getString("forcgc"));
                    imp.setIe_rg(rst.getString("forinsc"));
                    imp.setDatacadastro(("".equals(dataCadastro) ? new Date(new java.util.Date().getTime()) : data));
                    imp.setAtivo(true);

                    if ((rst.getString("forbanco") != null)
                            && (!rst.getString("forbanco").trim().isEmpty())) {
                        observacao = "BANCO " + rst.getString("forbanco") + " ";
                    }

                    if ((rst.getString("foragencia") != null)
                            && (!rst.getString("foragencia").trim().isEmpty())) {
                        observacao = observacao + "AGENCIA " + rst.getString("foragencia") + " ";
                    }

                    if ((rst.getString("forconta") != null)
                            && (!rst.getString("forconta").trim().isEmpty())) {
                        observacao = observacao + " CONTA " + rst.getString("forconta");
                    }

                    if ((rst.getString("forfax") != null)
                            && (!rst.getString("forfax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("forfax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("forfax") != null)
                            && (!rst.getString("forfax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("foremail").toLowerCase().trim()
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
        List<ClienteIMP> vResult = new ArrayList<>();
        Utils util = new Utils();
        String dataCadastro;
        java.sql.Date data = null;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "clicliente, clirazao, clifantasia, clicgc, "
                    + "cliendereco, clibairro, clicidade, cliestado, clicep, "
                    + "clicasapropria, cliddd, clitelefone, clidddcom, "
                    + "clitelefonecom, clifax, cliinsest, clidtcadas, clilimite, "
                    + "clisituacao, clianiversario, clirendam, clisexo, cliestcivil, "
                    + "clicargo, cliempresa, clipai, climae, cliobserv1, cliemail, clipj, clitiporec "
                    + "from cadcli "
            )) {
                while (rst.next()) {
                    if ((rst.getString("clidtcadas") != null)
                            && (!rst.getString("clidtcadas").trim().isEmpty())) {
                        if (util.validarData(Integer.parseInt(rst.getString("clidtcadas").substring(4, 6)),
                                Integer.parseInt(rst.getString("clidtcadas").substring(6, 8)))) {
                            dataCadastro = rst.getString("clidtcadas").trim().substring(0, 4);
                            dataCadastro = dataCadastro + "/" + rst.getString("clidtcadas").trim().substring(4, 6);
                            dataCadastro = dataCadastro + "/" + rst.getString("clidtcadas").trim().substring(6, 8);
                            data = new java.sql.Date(fmt.parse(dataCadastro).getTime());
                        } else {
                            dataCadastro = "";
                        }
                    } else {
                        dataCadastro = "";
                    }

                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("clicliente"));
                    imp.setRazao(rst.getString("clirazao"));
                    imp.setFantasia(rst.getString("clifantasia"));
                    imp.setCnpj(rst.getString("clicgc"));
                    imp.setInscricaoestadual(rst.getString("cliinsest"));
                    imp.setEndereco(rst.getString("cliendereco"));
                    imp.setBairro(rst.getString("clibairro"));
                    imp.setMunicipio(rst.getString("clicidade"));
                    imp.setUf(rst.getString("cliestado"));
                    imp.setCep(rst.getString("clicep"));
                    imp.setDataCadastro(("".equals(dataCadastro) ? new Date(new java.util.Date().getTime()) : data));
                    imp.setValorLimite(rst.getDouble("clilimite"));
                    imp.setEmpresa(rst.getString("cliempresa"));
                    imp.setCargo(rst.getString("clicargo"));
                    imp.setSalario(rst.getDouble("clirendam"));
                    imp.setNomePai(rst.getString("clipai"));
                    imp.setNomeMae(rst.getString("climae"));
                    imp.setObservacao(rst.getString("cliobserv1"));
                    imp.setTelefone((rst.getString("cliddd") == null ? "" : rst.getString("cliddd"))
                            + rst.getString("clitelefone").trim());
                    imp.setEmail(rst.getString("cliemail") == null ? "" : rst.getString("cliemail").toLowerCase());
                    imp.setAtivo("A".equals(rst.getString("clisituacao").trim()));
                    imp.setSexo("F".equals(rst.getString("clisexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);

                    if ((rst.getString("cliestcivil") != null)
                            && (!rst.getString("cliestcivil").trim().isEmpty())) {
                        if (null != rst.getString("cliestcivil").trim()) {
                            switch (rst.getString("cliestcivil").trim()) {
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }

                    if ((rst.getString("clitiporec") != null)
                            && (!rst.getString("clitiporec").trim().isEmpty())) {
                        if (null != rst.getString("clitiporec").trim()) {
                            switch (rst.getString("clitiporec").trim()) {
                                case "C":
                                    imp.setPermiteCheque(true);
                                    imp.setPermiteCreditoRotativo(false);
                                    imp.setBloqueado(false);
                                    break;
                                case "T":
                                    imp.setPermiteCheque(true);
                                    imp.setPermiteCreditoRotativo(true);
                                    imp.setBloqueado(false);
                                    break;
                                case "R":
                                    imp.setPermiteCheque(false);
                                    imp.setPermiteCreditoRotativo(true);
                                    imp.setBloqueado(false);
                                    break;
                                case "B":
                                    imp.setPermiteCheque(false);
                                    imp.setPermiteCreditoRotativo(false);
                                    imp.setBloqueado(true);
                                    break;
                                default:
                                    imp.setPermiteCheque(true);
                                    imp.setPermiteCreditoRotativo(true);
                                    imp.setBloqueado(false);
                                    break;
                            }
                        }
                    } else {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                        imp.setBloqueado(false);
                    }

                    if ((rst.getString("clitelefonecom") != null)
                            && (!rst.getString("clitelefonecom").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE COM",
                                (rst.getString("clidddcom") == null ? "" : rst.getString("clidddcom").trim())
                                + rst.getString("clitelefonecom").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("clifax") != null)
                            && (!rst.getString("clifax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("clifax"),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
                    + "where cap.jocdatafim > '" + v_dataTermino + "'\n"
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
    
}
