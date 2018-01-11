package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
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

    @Override
    public String getSistema() {
        return "JMaster";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct NATCODIGO, (NATCODIGO+' - '+ NATDESCRICAO+' - CST: '+cast(NATCST as varchar)+\n"
                    + "' ICMS: '+cast(NATICM as varchar)+' RDZ: '+cast(NATICMREDUZ as varchar)) DESCRICAO\n"
                    + "from cadnat\n"
                    + "inner join VPRODLOJA on litnatfiscal = NATCODIGO\n"
                    + "where NATTABNAT = 1\n"
                    + "and NATESTADO = 'SP'\n"
                    + "order by NATCODIGO"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("NATCODIGO"), rst.getString("DESCRICAO")));
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.gercodreduz, e.EANCODIGO, e.EANQTDE,\n"
                    + "p.gersecao, p.gergrupo, p.gersubgrupo,\n"
                    + "p.gerdescricao, p.gerdescreduz, p.gertipven,\n"
                    + "p.gerembven, p.gerfamilia, p.gernbm, p.gertipopis,\n"
                    + "p.gertipopie, p.litnatfiscal, p.litmrgven1, p.litcusrep,\n"
                    + "p.litprcven1, p.litestqmin, p.litestql, p.litflinha, p.gercest,\n"
                    + "i.NATCST, i.NATICM, i.NATICMREDUZ, i.NATDESCRICAO, p.LITTIPFOR\n"
                    + "from VPRODLOJA p\n"
                    + "left join CADEAN e on e.EANCODREDUZ = p.GERCODREDUZ\n"
                    + "left join CADNAT i on i.NATCODIGO = p.litnatfiscal and i.NATTABNAT = 1 and i.NATESTADO = 'SP'\n"
                    + "order by p.gercodreduz"
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

    /* VERSÃO ANTIGA DE IMPORTAÇÃO */
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT famcodigo, famdescricao ");
            sql.append("FROM cadfam ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("famcodigo"));
                oFamiliaProduto.descricao = Utils.acertarTexto(rst.getString("famdescricao").replace("'", ""));
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select secsecao, secgrupo, secsubgrupo, secdescri ");
            sql.append("from cadsec ");
            sql.append("order by secsecao, secgrupo, secsubgrupo ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                descricao = Utils.acertarTexto(rst.getString("secdescri").replace("'", ""));

                if (descricao.length() > 35) {
                    descricao = descricao.substring(0, 35);
                }

                oMercadologico.mercadologico1 = rst.getInt("secsecao");
                oMercadologico.mercadologico2 = rst.getInt("secgrupo");
                oMercadologico.mercadologico3 = rst.getInt("secsubgrupo");
                oMercadologico.mercadologico4 = 0;
                oMercadologico.mercadologico5 = 0;

                if ((rst.getInt("secgrupo") == 0) && (rst.getInt("secsubgrupo") == 0)) {
                    nivel = 1;
                } else if ((rst.getInt("secgrupo") > 0) && (rst.getInt("secsubgrupo") == 0)) {
                    nivel = 2;
                } else if ((rst.getInt("secgrupo") > 0) && (rst.getInt("secsubgrupo") > 0)) {
                    nivel = 3;
                }

                oMercadologico.nivel = nivel;
                oMercadologico.descricao = descricao;

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }

    }

    public List<FornecedorVO> carregarFornecedor() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, endereco, bairro, inscricaoestadual,
                ddd, telefone1, telefone2, numero, complemento, obs, fax, email;
        long cnpj, cep;
        java.sql.Date datacadastro;
        int id_tipoinscricao, id_municipio = 0, id_estado;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select forcodigo, fordescri, forcgc, forendereco, fornumero, ");
            sql.append("forbairro, forcidade, forestado, forcep, forddd, fortelefone, ");
            sql.append("forfax, forinsc, fordtcad, forsituacao, forbanco, ");
            sql.append("foragencia, forconta, forrazao, foremail, forpj  ");
            sql.append("from CADFOR ");
            sql.append("order by forcodigo ");

            rst = stm.executeQuery(sql.toString());

            int contador = 1;
            while (rst.next()) {

                FornecedorVO oFornecedor = new FornecedorVO();

                if ((rst.getString("forrazao") != null)
                        && (!rst.getString("forrazao").isEmpty())) {
                    razaosocial = Utils.acertarTexto(rst.getString("forrazao").replace("'", ""));
                } else {
                    razaosocial = "";
                }

                if ((rst.getString("fordescri") != null)
                        && (!rst.getString("fordescri").isEmpty())) {
                    nomefantasia = Utils.acertarTexto(rst.getString("fordescri").replace("'", ""));
                } else {
                    nomefantasia = "";
                }

                if ((rst.getString("forcgc") != null)
                        && (!rst.getString("forcgc").isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("forcgc")));
                } else {
                    cnpj = Long.parseLong("0");
                }

                if ((rst.getString("forinsc") != null)
                        && (!rst.getString("forinsc").isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("forinsc").replace("'", ""));
                } else {
                    inscricaoestadual = "ISENTO";
                }

                if ("0".equals(rst.getString("forpj").trim())) {
                    id_tipoinscricao = 0;
                } else {
                    id_tipoinscricao = 1;
                }

                if ((rst.getString("forddd") != null)
                        && (!rst.getString("forddd").isEmpty())) {
                    ddd = Utils.acertarTexto(rst.getString("forddd"));
                } else {
                    ddd = "";
                }

                if ((rst.getString("fortelefone") != null)
                        && (!rst.getString("fortelefone").isEmpty())) {
                    telefone1 = ddd + Utils.acertarTexto(rst.getString("fortelefone"));
                } else {
                    telefone1 = ddd + "0000000000";
                }

                if ((rst.getString("forendereco") != null)
                        && (!rst.getString("forendereco").isEmpty())) {
                    endereco = Utils.acertarTexto(rst.getString("forendereco").replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("forbairro") != null)
                        && (!rst.getString("forbairro").isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("forbairro").replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("fornumero") != null)
                        && (!rst.getString("fornumero").isEmpty())) {
                    numero = Utils.acertarTexto(rst.getString("fornumero").replace("'", ""));
                } else {
                    numero = "";
                }

                complemento = "";

                if ((rst.getString("forcep") != null)
                        && (!rst.getString("forcep").isEmpty())) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("forcep")));
                } else {
                    cep = Parametros.get().getCepPadrao();
                }

                obs = "";

                if ((rst.getString("forcidade") != null)
                        && (!rst.getString("forcidade").isEmpty())) {

                    if ((rst.getString("forestado") != null)
                            && (!rst.getString("forestado").isEmpty())) {

                        id_municipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("forcidade").replace("'", "")),
                                Utils.acertarTexto(rst.getString("forestado").replace("'", "")));

                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();;
                        }
                    }
                } else {
                    id_municipio = 3106200;
                }

                if ((rst.getString("forestado") != null)
                        && (!rst.getString("forestado").isEmpty())) {
                    id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("forestado").replace("'", "")));

                    if (id_estado == 0) {
                        id_estado = Parametros.get().getUfPadrao().getId();
                    }
                } else {
                    id_estado = Parametros.get().getUfPadrao().getId();
                }

                //if (rst.getDate("AGEDATCAD") != null) {
                //    datacadastro = new java.sql.Date(rst.getDate("AGEDATCAD").getTime());
                //} else {
                datacadastro = new java.sql.Date(new java.util.Date().getTime());
                //}

                //if ((rst.getString("AGETEL2") != null) &&
                //        (!rst.getString("AGETEL2").isEmpty())) {
                //    telefone2 = util.formataNumero(rst.getString("AGETEL2"));
                //} else {
                telefone2 = "";
                //}

                if ((rst.getString("forfax") != null)
                        && (!rst.getString("forfax").isEmpty())) {
                    fax = Utils.formataNumero(rst.getString("forfax"));
                } else {
                    fax = "";
                }

                if ((rst.getString("foremail") != null)
                        && (!rst.getString("foremail").isEmpty())) {
                    email = Utils.acertarTexto(rst.getString("foremail").replace("'", ""));
                } else {
                    email = "";
                }

                if (razaosocial.length() > 40) {
                    razaosocial = razaosocial.substring(0, 40);
                }

                if (nomefantasia.length() > 30) {
                    nomefantasia = nomefantasia.substring(0, 30);
                }

                if (endereco.length() > 40) {
                    endereco = endereco.substring(0, 40);
                }

                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }

                if (String.valueOf(cep).length() > 8) {
                    cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                }

                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                }

                if (inscricaoestadual.length() > 20) {
                    inscricaoestadual = inscricaoestadual.substring(0, 20);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                oFornecedor.codigoanterior = rst.getInt("forcodigo");
                oFornecedor.razaosocial = razaosocial;
                oFornecedor.nomefantasia = nomefantasia;
                oFornecedor.endereco = endereco;
                oFornecedor.bairro = bairro;
                oFornecedor.id_municipio = id_municipio;
                oFornecedor.cep = cep;
                oFornecedor.id_estado = id_estado;
                oFornecedor.telefone = telefone1;
                oFornecedor.id_tipoinscricao = id_tipoinscricao;
                oFornecedor.inscricaoestadual = inscricaoestadual;
                oFornecedor.cnpj = cnpj;
                oFornecedor.numero = numero;
                oFornecedor.complemento = complemento;
                oFornecedor.datacadastro = datacadastro;
                oFornecedor.observacao = obs;
                oFornecedor.telefone2 = telefone2;
                oFornecedor.fax = fax;
                oFornecedor.email = email;
                vFornecedor.add(oFornecedor);
                System.out.println(contador);
                contador++;
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedorCidade() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        int idFornecedor, idMunicipio = 0, idEstado;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select forcodigo, forcidade, forestado ");
            sql.append("from CADFOR ");
            sql.append("order by forcodigo ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idFornecedor = Integer.parseInt(rst.getString("forcodigo"));

                if ((rst.getString("forcidade") != null)
                        && (!rst.getString("forcidade").isEmpty())) {

                    if ((rst.getString("forestado") != null)
                            && (!rst.getString("forestado").isEmpty())) {

                        idMunicipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("forcidade").replace("'", "").trim()),
                                Utils.acertarTexto(rst.getString("forestado").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;
                        }
                    }
                } else {
                    idMunicipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;;
                }

                if ((rst.getString("forestado") != null)
                        && (!rst.getString("forestado").isEmpty())) {
                    idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("forestado").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                    }
                } else {
                    idEstado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                }

                FornecedorVO oFornecedor = new FornecedorVO();
                oFornecedor.id = idFornecedor;
                oFornecedor.id_municipio = idMunicipio;
                oFornecedor.id_estado = idEstado;

                vFornecedor.add(oFornecedor);
            }

            return vFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem, idTipoEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select fitcodreduz, fitcodfor, fitreferencia, ");
            sql.append("fitembfor, fittipfor ");
            sql.append("from foritm ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("fitcodfor");
                idProduto = rst.getInt("fitcodreduz");
                qtdEmbalagem = (int) rst.getDouble("fitembfor");

                if ((rst.getString("fittipfor") != null)
                        && (!rst.getString("fittipfor").isEmpty())) {
                    if ("CX".equals(rst.getString("fittipfor").trim().toUpperCase())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("fittipfor").trim().toUpperCase())) {
                        idTipoEmbalagem = 4;
                    } else if ("FD".equals(rst.getString("fittipfor").trim().toUpperCase())) {
                        idTipoEmbalagem = 5;
                    } else if ("PC".equals(rst.getString("fittipfor").trim().toUpperCase())) {
                        idTipoEmbalagem = 3;
                    } else if ("LT".equals(rst.getString("fittipfor").trim().toUpperCase())) {
                        idTipoEmbalagem = 2;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }

                if ((rst.getString("fitreferencia") != null)
                        && (!rst.getString("fitreferencia").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("fitreferencia").replace("'", ""));
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarCliente() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, numero, complemento, obs, telefone1,
                orrgexp, inscricaoestadual, conjuge, email, ddd, mae, pai, cargo,
                empresa, fax, telefone2, dataNascimento = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao, id, agente,
                estadocivil, id_situcaocadastro;
        double limite, salario;
        boolean bloqueado, permiteCheque, permiteCreditoRotativo;
        long cnpj, cep;
        java.sql.Date datacadastro, dataaltcadastro, dataAniversario = null;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select clicliente, clirazao, clifantasia, clicgc, ");
            sql.append("cliendereco, clibairro, clicidade, cliestado, clicep, ");
            sql.append("clicasapropria, cliddd, clitelefone, clidddcom, ");
            sql.append("clitelefonecom, clifax, cliinsest, clidtcadas, clilimite, ");
            sql.append("clisituacao, clianiversario, clirendam, clisexo, cliestcivil, ");
            sql.append("clicargo, cliempresa, clipai, climae, cliobserv1, cliemail, clipj, clitiporec ");
            sql.append("from cadcli ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                dataNascimento = "";

                if ((rst.getString("clisituacao") != null)
                        && (!rst.getString("clisituacao").isEmpty())) {

                    if ("A".equals(rst.getString("clisituacao"))) {
                        id_situcaocadastro = 1;
                    } else {
                        id_situcaocadastro = 0;
                    }
                } else {
                    id_situcaocadastro = 0;
                }

                agente = rst.getInt("clicliente");
                id = rst.getInt("clicliente");

                if ((rst.getString("clirazao") != null)
                        && (!rst.getString("clirazao").isEmpty())) {
                    nome = Utils.acertarTexto(rst.getString("clirazao").replace("'", "").trim());
                } else {
                    nome = "";
                }

                orrgexp = "";

                if ((rst.getString("clicgc") != null)
                        && (!rst.getString("clicgc").isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("clicgc").trim()));
                } else {
                    cnpj = Long.parseLong(String.valueOf(0));
                }

                if ((rst.getString("cliinsest") != null)
                        && (!rst.getString("cliinsest").isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("cliinsest").replace("'", "").trim());
                } else {
                    inscricaoestadual = "ISENTO";
                }

                //if (rst.getDate("AGEDATCAD") != null) {
                //    datacadastro = new java.sql.Date(rst.getDate("AGEDATCAD").getTime());
                //} else {
                datacadastro = new java.sql.Date(new java.util.Date().getTime());
                //}

                //if (rst.getDate("AGEDATALT") != null) {
                //    dataaltcadastro = new java.sql.Date(rst.getDate("AGEDATALT").getTime()); 
                //} else {
                dataaltcadastro = new java.sql.Date(new java.util.Date().getTime());
                //}

                if ((rst.getString("clipj") != null)
                        && (!rst.getString("clipj").isEmpty())) {
                    if ("0".equals(rst.getString("clipj").trim())) {
                        id_tipoinscricao = 1;
                    } else {
                        id_tipoinscricao = 0;
                    }
                } else {
                    id_tipoinscricao = 1;
                }

                if ((rst.getString("cliddd") != null)
                        && (!rst.getString("cliddd").isEmpty())) {
                    ddd = Utils.formataNumero(rst.getString("cliddd").trim());
                } else {
                    ddd = "";
                }

                if ((rst.getString("clitelefone") != null)
                        && (!rst.getString("clitelefone").isEmpty())) {
                    telefone1 = ddd + Utils.formataNumero(rst.getString("clitelefone").trim());
                } else {
                    telefone1 = ddd + "0000000000";
                }

                if ((rst.getString("cliendereco") != null)
                        && (!rst.getString("cliendereco").isEmpty())) {
                    endereco = Utils.acertarTexto(rst.getString("cliendereco").replace("'", "").trim());
                } else {
                    endereco = "";
                }

                if ((rst.getString("clibairro") != null)
                        && (!rst.getString("clibairro").isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("clibairro").replace("'", "").trim());
                } else {
                    bairro = "";
                }

                //if ((rst.getString("AGENUM") != null) &&
                //        (!rst.getString("AGENUM").isEmpty())) {
                //    numero = util.acertarTexto(rst.getString("AGENUM").replace("'", ""));
                //} else {
                numero = "";
                //}

                //if ((rst.getString("AGECPL") != null) &&
                //        (!rst.getString("AGECPL").isEmpty())) {
                //    complemento = util.acertarTexto(rst.getString("AGECPL").replace("'", ""));
                //} else {
                complemento = "";
                //}

                if ((rst.getString("clicep") != null)
                        && (!rst.getString("clicep").isEmpty())) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("clicep").trim()));
                } else {
                    cep = Parametros.get().getCepPadrao();
                }

                if ((rst.getString("cliobserv1") != null)
                        && (!rst.getString("cliobserv1").isEmpty())) {
                    obs = Utils.acertarTexto(rst.getString("cliobserv1").replace("'", "").trim());
                } else {
                    obs = "";
                }

                if ((rst.getString("cliemail") != null)
                        && (!rst.getString("cliemail").isEmpty())
                        && (rst.getString("cliemail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("cliemail").replace("'", "").trim());
                } else {
                    email = "";
                }

                if ((rst.getString("clicidade") != null)
                        && (!rst.getString("clicidade").isEmpty())) {

                    if ((rst.getString("cliestado") != null)
                            && (!rst.getString("cliestado").isEmpty())) {

                        id_municipio = Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("clicidade").replace("'", "").trim()),
                                Utils.acertarTexto(rst.getString("cliestado").replace("'", "").trim()));

                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;;
                        }
                    }
                } else {
                    id_municipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;;
                }

                if ((rst.getString("cliestado") != null)
                        && (!rst.getString("cliestado").isEmpty())) {

                    id_estado = Utils.retornarEstadoDescricao(
                            Utils.acertarTexto(rst.getString("cliestado").replace("'", "").trim()));

                    if (id_estado == 0) {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE;
                    }
                } else {
                    id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE;
                }

                if ((rst.getString("clilimite") != null)
                        && (!rst.getString("clilimite").isEmpty())) {
                    limite = Double.parseDouble(rst.getString("clilimite").trim());
                } else {
                    limite = 0;
                }

                if ((rst.getString("clisexo") != null)
                        && (!rst.getString("clisexo").isEmpty())) {

                    if ("F".equals(rst.getString("clisexo").trim())) {
                        id_sexo = 0;
                    } else {
                        id_sexo = 1;
                    }
                } else {
                    id_sexo = 1;
                }

                if ((rst.getString("clirendam") != null)
                        && (!rst.getString("clirendam").isEmpty())) {
                    salario = Double.parseDouble(rst.getString("clirendam").trim());
                } else {
                    salario = 0;
                }

                if ((rst.getString("climae") != null)
                        && (!rst.getString("climae").isEmpty())) {
                    mae = Utils.acertarTexto(rst.getString("climae").replace("'", "").trim());
                } else {
                    mae = "";
                }

                if ((rst.getString("clipai") != null)
                        && (!rst.getString("clipai").isEmpty())) {
                    pai = Utils.acertarTexto(rst.getString("clipai").replace("'", "").trim());
                } else {
                    pai = "";
                }

                if ((rst.getString("cliempresa") != null)
                        && (!rst.getString("cliempresa").isEmpty())) {
                    empresa = Utils.acertarTexto(rst.getString("cliempresa").replace("'", "").trim());
                } else {
                    empresa = "";
                }

                if ((rst.getString("clicargo") != null)) {
                    cargo = Utils.acertarTexto(rst.getString("clicargo").replace("'", "").trim());
                } else {
                    cargo = "";
                }
                //if ((rst.getString("CLICONJG") != null) &&
                //        (!rst.getString("CLICONJG").isEmpty())) {
                //    conjuge = util.acertarTexto(rst.getString("CLICONJG").replace("'", ""));
                //} else {
                conjuge = "";
                //}

                if ((rst.getString("cliestcivil") != null)
                        && (!rst.getString("cliestcivil").isEmpty())) {

                    if ("C".equals(rst.getString("cliestcivil").trim())) {
                        estadocivil = 2;
                    } else if ("S".equals(rst.getString("cliestcivil").trim())) {
                        estadocivil = 1;
                    } else if ("D".equals(rst.getString("cliestcivil").trim())) {
                        estadocivil = 6;
                    } else if ("O".equals(rst.getString("cliestcivil").trim())) {
                        estadocivil = 5;
                    } else {
                        estadocivil = 0;
                    }
                } else {
                    estadocivil = 0;
                }

                if ((rst.getString("clifax") != null)
                        && (!rst.getString("clifax").isEmpty())
                        && (!"0".equals(rst.getString("clifax")))) {
                    fax = Utils.formataNumero(rst.getString("clifax").trim());
                } else {
                    fax = "";
                }

                if ((rst.getString("clitelefonecom") != null)
                        && (!rst.getString("clitelefonecom").isEmpty())
                        && (!"0".equals(rst.getString("clitelefonecom")))) {
                    telefone2 = Utils.formataNumero(rst.getString("clitelefonecom").trim());
                } else {
                    telefone2 = "";
                }

                if ((rst.getString("clianiversario") != null)
                        && (!rst.getString("clianiversario").isEmpty())
                        && (rst.getString("clianiversario").trim().length() == 8)) {

                    if (util.validarData(Integer.parseInt(rst.getString("clianiversario").substring(4, 6)),
                            Integer.parseInt(rst.getString("clianiversario").substring(6, 8)))) {

                        dataNascimento = rst.getString("clianiversario").substring(0, 4);
                        dataNascimento = dataNascimento + "/" + rst.getString("clianiversario").substring(4, 6);
                        dataNascimento = dataNascimento + "/" + rst.getString("clianiversario").substring(6, 8);
                    } else {
                        dataNascimento = "";
                    }
                } else {
                    dataNascimento = "";
                }

                if ((rst.getString("clitiporec") != null)
                        && (!rst.getString("clitiporec").trim().isEmpty())) {
                    if ("C".equals(rst.getString("clitiporec").trim())) {
                        permiteCheque = true;
                        permiteCreditoRotativo = false;
                        bloqueado = false;
                    } else if ("T".equals(rst.getString("clitiporec").trim())) {
                        permiteCheque = true;
                        permiteCreditoRotativo = true;
                        bloqueado = false;
                    } else if ("R".equals(rst.getString("clitiporec").trim())) {
                        permiteCheque = false;
                        permiteCreditoRotativo = true;
                        bloqueado = false;
                    } else if ("B".equals(rst.getString("clitiporec").trim())) {
                        permiteCheque = false;
                        permiteCreditoRotativo = false;
                        bloqueado = true;
                    } else {
                        permiteCheque = true;
                        permiteCreditoRotativo = true;
                        bloqueado = false;
                    }
                } else {
                    permiteCheque = true;
                    permiteCreditoRotativo = true;
                    bloqueado = false;
                }

                if (nome.length() > 40) {
                    nome = nome.substring(0, 40);
                }

                if (conjuge.length() > 25) {
                    conjuge = conjuge.substring(0, 25);
                }

                if (endereco.length() > 40) {
                    endereco = endereco.substring(0, 40);
                }

                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }

                if (String.valueOf(cep).length() > 8) {
                    cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                }

                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                }

                if (inscricaoestadual.length() > 20) {
                    inscricaoestadual = inscricaoestadual.substring(0, 20);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                if (email.length() > 50) {
                    email = email.substring(0, 50);
                }

                oClientePreferencial.id = id;
                oClientePreferencial.nome = nome;
                oClientePreferencial.id_situacaocadastro = id_situcaocadastro;
                oClientePreferencial.endereco = endereco;
                oClientePreferencial.bairro = bairro;
                oClientePreferencial.id_estado = id_estado;
                oClientePreferencial.id_municipio = id_municipio;
                oClientePreferencial.cep = cep;
                oClientePreferencial.telefone = telefone1;
                oClientePreferencial.inscricaoestadual = inscricaoestadual;
                oClientePreferencial.cnpj = cnpj;
                oClientePreferencial.sexo = id_sexo;
                oClientePreferencial.observacao = obs;
                oClientePreferencial.salario = salario;
                oClientePreferencial.valorlimite = limite;
                oClientePreferencial.nomeconjuge = conjuge;
                oClientePreferencial.numero = numero;
                oClientePreferencial.complemento = complemento;
                oClientePreferencial.orgaoemissor = orrgexp;
                oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                //oClientePreferencial.datacadastro = datacadastro;
                oClientePreferencial.bloqueado = bloqueado;
                oClientePreferencial.email = email;
                oClientePreferencial.codigoanterior = id;
                oClientePreferencial.dataatualizacaocadastro = dataaltcadastro;
                oClientePreferencial.empresa = empresa;
                oClientePreferencial.cargo = cargo;
                oClientePreferencial.nomepai = pai;
                oClientePreferencial.nomemae = mae;
                oClientePreferencial.id_tipoestadocivil = estadocivil;
                oClientePreferencial.codigoAgente = agente;
                oClientePreferencial.telefone2 = telefone2;
                oClientePreferencial.fax = fax;
                oClientePreferencial.id_estadoempresa = 0;
                oClientePreferencial.id_municipioempresa = 0;
                oClientePreferencial.id_contacontabilfiscalpassivo = 0;
                oClientePreferencial.id_contacontabilfiscalativo = 0;
                oClientePreferencial.datanascimento = dataNascimento;
                oClientePreferencial.permitecheque = permiteCheque;
                oClientePreferencial.permitecreditorotativo = permiteCreditoRotativo;

                vClientePreferencial.add(oClientePreferencial);
            }

            return vClientePreferencial;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarSituacaoCadastroProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idSituacaoCadastro;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select gercodreduz, gersailin ");
            sql.append("from VPRODLOJA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                idProduto = Integer.parseInt(rst.getString("gercodreduz"));

                oProduto.id = Integer.parseInt(rst.getString("gercodreduz"));

                if (!"0".equals(rst.getString("gersailin"))) {
                    idSituacaoCadastro = 0;
                } else {
                    idSituacaoCadastro = 1;
                }

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCliente(int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom, parcela;
        double valor, juros;
        String observacao = "", dataemissao = "", datavencimento = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select deldatadel, delvencto, delemissao, delnota, ");
            sql.append("delparcela, delcliente, deltotal, delvlrtit, ");
            sql.append("delobservacao ");
            sql.append("from deltit ");
            sql.append("where delpagamento = 0 ");
            sql.append("and delvlrpago = 0 ");
            sql.append("order by delvencto desc ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("delcliente");

                if (util.validarData(Integer.parseInt(rst.getString("delemissao").substring(4, 6)),
                        Integer.parseInt(rst.getString("delemissao").substring(6, 8)))) {

                    dataemissao = rst.getString("delemissao").trim().substring(0, 4);
                    dataemissao = dataemissao + "/" + rst.getString("delemissao").trim().substring(4, 6);
                    dataemissao = dataemissao + "/" + rst.getString("delemissao").trim().substring(6, 8);
                }

                if (util.validarData(Integer.parseInt(rst.getString("delvencto").substring(4, 6)),
                        Integer.parseInt(rst.getString("delvencto").substring(6, 8)))) {

                    datavencimento = rst.getString("delvencto").trim().substring(0, 4);
                    datavencimento = datavencimento + "/" + rst.getString("delvencto").trim().substring(4, 6);
                    datavencimento = datavencimento + "/" + rst.getString("delvencto").trim().substring(6, 8);
                }

                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("delnota")));
                valor = Double.parseDouble(rst.getString("delvlrtit"));
                juros = 0;
                parcela = rst.getInt("delparcela");

                if ((rst.getString("delobservacao") != null)
                        && (!rst.getString("delobservacao").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("delobservacao").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;
                oReceberCreditoRotativo.parcela = parcela;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberChequeVO> carregarReceberCheque(int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select chrcgccpf, chrbanco, chragencia, chrconta, chrcheque, ");
            sql.append("chremissao, chrvencto, chrrazao, chrinscrg, chrvalor, ");
            sql.append("chrobserv1, chrobserv2, chrdtdeposito, chrpdv, chrtelefone ");
            sql.append("from CHQREC ");
            sql.append("where chrvlrpago = 0 ");
            sql.append("and chrpagamento = 0 ");
            sql.append("order by chrvencto desc ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(rst.getString("chrcgccpf"));

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("chrbanco").trim()));

                if ((rst.getString("chragencia") != null)
                        && (!rst.getString("chragencia").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("chragencia").trim().replace("'", ""));
                } else {
                    agencia = "";
                }

                if ((rst.getString("chrconta") != null)
                        && (!rst.getString("chrconta").trim().isEmpty())) {
                    conta = util.acertarTexto(rst.getString("chrconta").trim().replace("'", ""));
                } else {
                    conta = "";
                }

                if ((rst.getString("chrcheque") != null)
                        && (!rst.getString("chrcheque").trim().isEmpty())) {

                    cheque = Integer.parseInt(util.formataNumero(rst.getString("chrcheque")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("chremissao") != null)
                        && (!rst.getString("chremissao").trim().isEmpty())
                        && (rst.getString("chremissao").trim().length() == 8)) {

                    if (util.validarData(Integer.parseInt(rst.getString("chremissao").substring(4, 6)),
                            Integer.parseInt(rst.getString("chremissao").substring(6, 8)))) {

                        dataemissao = rst.getString("chremissao").trim().substring(0, 4);
                        dataemissao = dataemissao + "/" + rst.getString("chremissao").trim().substring(4, 6);
                        dataemissao = dataemissao + "/" + rst.getString("chremissao").trim().substring(6, 8);
                    }
                } else {
                    dataemissao = "2016/01/25";
                }

                if ((rst.getString("chrvencto") != null)
                        && (!rst.getString("chrvencto").trim().isEmpty())
                        && (rst.getString("chrvencto").trim().length() == 8)) {

                    if (util.validarData(Integer.parseInt(rst.getString("chrvencto").substring(4, 6)),
                            Integer.parseInt(rst.getString("chrvencto").substring(6, 8)))) {

                        datavencimento = rst.getString("chrvencto").trim().substring(0, 4);
                        datavencimento = datavencimento + "/" + rst.getString("chrvencto").trim().substring(4, 6);
                        datavencimento = datavencimento + "/" + rst.getString("chrvencto").trim().substring(6, 8);
                    }
                } else {
                    datavencimento = "2016/01/25";
                }

                if ((rst.getString("chrrazao") != null)
                        && (!rst.getString("chrrazao").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("chrrazao").replace("'", "").trim());
                } else {
                    nome = "";
                }

                if ((rst.getString("chrinscrg") != null)
                        && (!rst.getString("chrinscrg").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("chrinscrg").trim().replace("'", ""));

                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {
                    rg = "";
                }

                valor = Double.parseDouble(rst.getString("chrvalor"));
                numerocupom = 0;

                juros = 0;

                if ((rst.getString("chrobserv1") != null)
                        && (!rst.getString("chrobserv1").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("chrtelefone") != null)
                        && (!rst.getString("chrtelefone").isEmpty())
                        && (!"0".equals(rst.getString("chrtelefone").trim()))) {
                    telefone = util.formataNumero(rst.getString("chrtelefone"));
                } else {
                    telefone = "";
                }

                oReceberCheque.id_loja = id_loja;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProduto(int id_loja) throws SQLException, Exception {

        StringBuilder sql = null;
        Statement stm = null, stmPostgres;
        ResultSet rst = null, rst2 = null, rst3 = null;
        Utils util = new Utils();
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();

        int id, mercadologico1, mercadologico2, mercadologico3, ncm1 = 0, ncm2 = 0, ncm3 = 0,
                id_familiaproduto, codigoBalanca, id_tipoEmbalagem = 0, validade, referencia,
                qtdEmbalagem = 1, id_tipoPisCofinsDebito, id_tipoPisCofinsCredito, tipoNaturezaReceita,
                idSituacaoCadastro;
        String descricaocompleta, descricaoreduzida, descricaogondola, tribAliquota, ncmAtual;
        java.sql.Date dataCadastro;
        double margem, precoVenda, custo;
        long codigobarras = 0;
        boolean eBalanca, pesavel = false;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select gercodreduz, gersecao, gergrupo, gersubgrupo, ");
            sql.append("gerdescricao, gerdescreduz, gertipven, gerembven, ");
            sql.append("gerfamilia, gernbm, gertipopis, gertipopie, litnatfiscal, ");
            sql.append("litmrgven1, litcusrep, litprcven1, litestqmin, litestql, litflinha ");
            sql.append("from VPRODLOJA ");
            sql.append("order by gercodreduz ");

            rst = stm.executeQuery(sql.toString());

            Conexao.begin();

            stmPostgres = Conexao.createStatement();

            while (rst.next()) {

                eBalanca = false;
                codigoBalanca = 0;
                referencia = -1;
                pesavel = false;

                ProdutoVO oProduto = new ProdutoVO();

                id = Integer.parseInt(rst.getString("gercodreduz"));

                if ((rst.getString("litflinha") != null)
                        && (!rst.getString("litflinha").isEmpty())) {
                    idSituacaoCadastro = 0;
                } else {
                    idSituacaoCadastro = 1;
                }

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + id);

                rst2 = stmPostgres.executeQuery(sql.toString());

                if (rst2.next()) {

                    eBalanca = true;
                    codigoBalanca = rst2.getInt("codigo");

                    if (null != rst.getString("gertipven").trim()) {
                        switch (rst.getString("gertipven").trim()) {
                            case "KG":
                                id_tipoEmbalagem = 4;
                                pesavel = false;
                                break;
                            case "UN":
                                id_tipoEmbalagem = 0;
                                pesavel = true;
                                break;
                            default:
                                id_tipoEmbalagem = 4;
                                pesavel = false;
                                break;
                        }
                    }

                    validade = rst2.getInt("validade");

                } else {

                    eBalanca = false;

                    if (null != rst.getString("gertipven").trim()) {
                        switch (rst.getString("gertipven").trim()) {
                            case "UN":
                                id_tipoEmbalagem = 0;
                                break;
                            case "CX":
                                id_tipoEmbalagem = 1;
                                break;
                            default:
                                id_tipoEmbalagem = 0;
                                break;
                        }
                    }

                    codigoBalanca = 0;
                    validade = 0;
                    pesavel = false;
                }

                qtdEmbalagem = (int) rst.getDouble("gerembven");

                if ((rst.getString("gerdescricao") != null)
                        && (!rst.getString("gerdescricao").isEmpty())) {
                    descricaocompleta = Utils.acertarTexto(rst.getString("gerdescricao").replace("'", "").trim());
                } else {
                    descricaocompleta = "";
                }

                if ((rst.getString("gerdescreduz") != null)
                        && (!rst.getString("gerdescreduz").isEmpty())) {
                    descricaoreduzida = Utils.acertarTexto(rst.getString("gerdescreduz").replace("'", "").trim());
                } else {
                    descricaoreduzida = "";
                }

                descricaogondola = descricaocompleta;

                //if ((rst.getString("REFDATCAD") != null) &&
                //        (!rst.getString("REFDATCAD").trim().isEmpty())) {
                //    dataCadastro = new java.sql.Date(rst.getDate("REFDATCAD").getTime());
                //} else {
                dataCadastro = new java.sql.Date(new java.util.Date().getTime());
                //}

                if ((rst.getString("gersecao") != null)
                        && (!rst.getString("gersecao").isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("gersecao"));
                } else {
                    mercadologico1 = 0;
                }

                if ((rst.getString("gergrupo") != null)
                        && (!rst.getString("gergrupo").isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("gergrupo"));
                } else {
                    mercadologico2 = 0;
                }

                if ((rst.getString("gersubgrupo") != null)
                        && (!rst.getString("gersubgrupo").isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("gersubgrupo"));
                } else {
                    mercadologico3 = 0;
                }

                if ((rst.getString("gernbm") != null)
                        && (!rst.getString("gernbm").isEmpty())
                        && (rst.getString("gernbm").trim().length() > 5)) {

                    ncmAtual = rst.getString("gernbm").trim();

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("litmrgven1") != null)
                        && (!rst.getString("litmrgven1").isEmpty())) {
                    margem = Double.parseDouble(rst.getString("litmrgven1"));
                } else {
                    margem = 0;
                }

                if ((rst.getString("gerfamilia") != null)
                        && (!rst.getString("gerfamilia").isEmpty())) {
                    id_familiaproduto = Integer.parseInt(rst.getString("gerfamilia"));
                } else {
                    id_familiaproduto = -1;
                }

                if ((rst.getString("litprcven1") != null)
                        && (!rst.getString("litprcven1").isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("litprcven1"));
                } else {
                    precoVenda = 0;
                }

                if ((rst.getString("litcusrep") != null)
                        && (!rst.getString("litcusrep").isEmpty())) {
                    custo = Double.parseDouble(rst.getString("litcusrep"));
                } else {
                    custo = 0;
                }

                if ((rst.getString("litnatfiscal") != null)
                        && (!rst.getString("litnatfiscal").isEmpty())) {
                    tribAliquota = rst.getString("litnatfiscal").trim();
                } else {
                    tribAliquota = "999";
                }

                if ((rst.getString("gertipopis") != null)
                        && (!rst.getString("gertipopis").isEmpty())) {
                    id_tipoPisCofinsDebito = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("gertipopis")));
                } else {
                    id_tipoPisCofinsDebito = 0;
                }

                if ((rst.getString("gertipopie") != null)
                        && (!rst.getString("gertipopie").isEmpty())) {
                    id_tipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("gertipopie")));
                } else {
                    id_tipoPisCofinsCredito = 12;
                }

                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(id_tipoPisCofinsDebito, "");

                if (eBalanca) {
                    codigobarras = Long.parseLong(String.valueOf(id));
                }

                if (descricaocompleta.length() > 60) {

                    descricaocompleta = descricaocompleta.substring(0, 60);
                }

                if (descricaoreduzida.length() > 22) {

                    descricaoreduzida = descricaoreduzida.substring(0, 22);
                }

                if (descricaogondola.length() > 60) {

                    descricaogondola = descricaogondola.substring(0, 60);
                }

                sql = new StringBuilder();
                sql.append("select id from familiaproduto ");
                sql.append("where id = " + id_familiaproduto);

                rst3 = stmPostgres.executeQuery(sql.toString());

                if (rst3.next()) {
                    id_familiaproduto = rst3.getInt("id");
                } else {
                    id_familiaproduto = -1;
                }

                oProduto.id = id;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.descricaoCompleta = descricaocompleta;
                oProduto.descricaoReduzida = descricaoreduzida;
                oProduto.descricaoGondola = descricaogondola;
                oProduto.dataCadastro = String.valueOf(dataCadastro);
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = id_familiaproduto;
                oProduto.margem = margem;
                oProduto.qtdEmbalagem = 1;
                oProduto.idTipoEmbalagem = id_tipoEmbalagem;
                oProduto.idComprador = 1;
                oProduto.idFornecedorFabricante = 1;
                oProduto.pesavel = pesavel;
                oProduto.validade = validade;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.idTipoPisCofinsDebito = id_tipoPisCofinsDebito;
                oProduto.idTipoPisCofinsCredito = id_tipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oComplemento.idLoja = id_loja;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                EstadoVO uf = Parametros.get().getUfPadrao();
                oAliquota.idEstado = uf.getId();
                oAliquota.idAliquotaDebito = retornarAliquotaICMSJMaster(tribAliquota);
                oAliquota.idAliquotaCredito = retornarAliquotaICMSJMaster(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstado = retornarAliquotaICMSJMaster(tribAliquota);
                oAliquota.idAliquotaCreditoForaEstado = retornarAliquotaICMSJMaster(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstadoNF = retornarAliquotaICMSJMaster(tribAliquota);

                oProduto.vAliquota.add(oAliquota);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = id;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.barras = codigobarras;
                oCodigoAnterior.referencia = referencia;

                if ((rst.getString("gernbm") != null)
                        && (!rst.getString("gernbm").isEmpty())) {

                    oCodigoAnterior.ncm = rst.getString("gernbm");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                oCodigoAnterior.id_loja = id_loja;

                if ((rst.getString("litnatfiscal") != null)
                        && (!rst.getString("litnatfiscal").isEmpty())) {

                    oCodigoAnterior.ref_icmsdebito = rst.getString("litnatfiscal");
                }

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(id, oProduto);
            }

            stm.close();
            stmPostgres.close();

            Conexao.commit();

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarCustoProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double custo = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select gercodreduz, litcusrep ");
            sql.append("from VPRODLOJA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = 1;
                idProduto = Integer.parseInt(rst.getString("gercodreduz"));
                custo = rst.getDouble("litcusrep");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarPrecoProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double preco = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select gercodreduz, litprcven1 ");
            sql.append("from VPRODLOJA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = 1;
                idProduto = Integer.parseInt(rst.getString("gercodreduz"));
                preco = rst.getDouble("litprcven1");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarEstoqueProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double estoque = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select gercodreduz, litestqd  ");
            sql.append("from VPRODLOJA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = 1;
                idProduto = Integer.parseInt(rst.getString("gercodreduz"));
                estoque = rst.getDouble("litestqd");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oComplemento.estoqueMinimo = 0;
                oComplemento.estoqueMaximo = 0;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = estoque;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarEanProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, quantidade;
        long codigobarras;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select eancodigo, eancodreduz, eanqtde ");
            sql.append("from CADEAN  ");
            sql.append("order by eancodreduz ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("eancodreduz"));
                codigobarras = Long.parseLong(rst.getString("eancodigo"));
                quantidade = Integer.parseInt(rst.getString("eanqtde"));

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = quantidade;

                oProduto.vAutomacao.add(oAutomacao);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarSituacaoCadastroProdutoMilenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idSituacaoCadastro;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select codigo_produto, status ");
            sql.append("from [SysacME].[dbo].[MXF_VW_PIS_COFINS_16785684000114] ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                idProduto = Integer.parseInt(rst.getString("codigo_produto"));

                oProduto.id = Integer.parseInt(rst.getString("codigo_produto"));

                if ("ATIVO".equals(rst.getString("status"))) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public void importarProduto(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de 6 digitos...");
            Map<Integer, ProdutoVO> vProdutoMilenio = carregarProduto(id_loja);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoMilenio.size());

            for (Integer keyId : vProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProduto(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProdutoMilenio = carregarEstoqueProduto();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProdutoMilenio.size());

            for (Integer keyId : vEstoqueProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEstoqueProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarSituacaoCadastroProduto(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Situação Cadastro...");
            Map<Integer, ProdutoVO> vSituacaoCadastroProdutoMilenio = carregarSituacaoCadastroProduto();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vSituacaoCadastroProdutoMilenio.size());

            for (Integer keyId : vSituacaoCadastroProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vSituacaoCadastroProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarSituacoCadastroProdutoJMaster(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEanProduto() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
            Map<Integer, ProdutoVO> vEanProdutoMilenio = carregarEanProduto();

            ProgressBar.setMaximum(vEanProdutoMilenio.size());

            for (Integer keyId : vEanProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEanProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.adicionarEanProdutoJMaster(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologico(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vFornecedor = carregarFornecedor();
            if (!vFornecedor.isEmpty()) {
                new FornecedorDAO().salvar(vFornecedor);
            } else {
                System.out.println("VAZIO");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedorCidade() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor Cidade...");
            List<FornecedorVO> vFornecedor = carregarFornecedorCidade();

            new FornecedorDAO().acertarCidadeFornecedor(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

            new ProdutoFornecedorDAO().salvarJMaster(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClientePreferencial(int id_loja) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarCliente();

            new PlanoDAO().salvar(id_loja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, 0, 0);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int id_loja) throws Exception {
        int id_lojaDestino = 0;

        id_lojaDestino = id_loja;

        try {

            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCliente(id_lojaDestino);

            new ReceberCreditoRotativoDAO().salvarJMaster(vReceberCreditoRotativo, id_lojaDestino);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberCheque(int id_loja) throws Exception {
        int id_lojaDestino = 0;

        id_lojaDestino = id_loja;

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cheque...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_lojaDestino);

            new ReceberChequeDAO().salvar(vReceberCheque, id_lojaDestino);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCustoProduto(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProduto();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProdutoJMaster(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProduto(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProduto();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProdutoJMaster(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    private int retornarAliquotaICMSJMaster(String codTrib) {

        int retorno = 8;
        if (null != codTrib) {
            switch (codTrib) {
                case "I":
                    retorno = 6;
                    break;
                case "N":
                    retorno = 17;
                    break;
                case "T07":
                    retorno = 0;
                    break;
                case "T12":
                    retorno = 1;
                    break;
                case "T18":
                    retorno = 2;
                    break;
                case "T25":
                    retorno = 3;
                    break;
                case "F18":
                    retorno = 7;
                    break;
                default:
                    retorno = 8;
                    break;
            }
        }
        return retorno;
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999)) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }

                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarrasEmBranco(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
}
