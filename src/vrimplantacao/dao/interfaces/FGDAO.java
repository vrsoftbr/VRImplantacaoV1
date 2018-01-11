package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorContatoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorContatoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.parametro.Parametros;

public class FGDAO {

    private final ConexaoDBF connDBF = new ConexaoDBF();

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
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
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
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
    
    private List<FamiliaProdutoVO> carregarFamiliaProduto(String i_arquivo, int idLojaCliente) throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codagru03, desagru03 "
                    + "from tabagr "
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("codagru03"));
                    vo.setDescricao((rst.getString("desagru03") == null ? "" : rst.getString("desagru03")));
                    vo.setCodigoant(vo.getId());
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFamiliaProduto(String i_arquivo, int idLojaCliente) throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            vResult = carregarFamiliaProduto(i_arquivo, idLojaCliente);
            if (!vResult.isEmpty()) {
                new FamiliaProdutoDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(String i_arquivo, int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.coddepto, m1.nomedepto, "
                    + "m2.codgrupo, m2.descgrupo, "
                    + "m3.codgss00, m3.descgss00 "
                    + "from tabdep m1 "
                    + "inner join tabgru m2 on m2.coddepto = m1.coddepto "
                    + "inner join cadgss m3 on m3.codgrupo00 = m2.codgrupo and m2.coddepto = m1.coddepto "
                    + "order by m1.coddepto, m2.codgrupo, m3.codgss00 "
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    if (nivel == 1) {
                        vo.setMercadologico1(rst.getInt("coddepto"));
                        vo.setDescricao((rst.getString("nomedepto") == null ? "" : rst.getString("nomedepto").trim()));
                    } else if (nivel == 2) {
                        vo.setMercadologico1(rst.getInt("coddepto"));
                        vo.setMercadologico2(rst.getInt("codgrupo"));
                        vo.setDescricao((rst.getString("descgrupo") == null ? "" : rst.getString("descgrupo").trim()));
                    } else if (nivel == 3) {
                        vo.setMercadologico1(rst.getInt("coddepto"));
                        vo.setMercadologico2(rst.getInt("codgrupo"));
                        vo.setMercadologico3(rst.getInt("codgss00"));
                        vo.setDescricao((rst.getString("descgss00") == null ? "" : rst.getString("descgss00").trim()));
                    }
                    vo.setNivel(nivel);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarMercadologico(String i_arquivo) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vResult = carregarMercadologico(i_arquivo, 1);
            new MercadologicoDAO().salvar2(vResult, true);

            vResult = carregarMercadologico(i_arquivo, 2);
            new MercadologicoDAO().salvar2(vResult, false);

            vResult = carregarMercadologico(i_arquivo, 3);
            new MercadologicoDAO().salvar2(vResult, false);

            new MercadologicoDAO().salvarMax();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProduto(String i_arquivo, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        String ncmAtual;
        int ncm1, ncm2, ncm3, idCest, idFamiliaProduto;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro01, codgss01, descpro01, descabr01, custenc01 "
                    + "custocomimposto, precust01 custosemimposto, prevend01, "
                    + "margtra01, qtdeemb01, unidpro01, alikicm01, pesopro01, "
                    + "sitpro01, validpro01, codgrupo01, icmcompr01, margemst01, "
                    + "cdsitrib01, natpisco01, pbcreduz01, codcest01, estatu01, "
                    + "estmin01, estmax01, codncm01 ncm, agrupa01, datacad01, piscofin01 "
                    + "from cadpro "
                    + "where codfil01 = " + idLojaCliente
            )) {
                int contador = 1;
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {

                        ncmAtual = Utils.formataNumero(rst.getString("ncm").trim());
                        if ((ncmAtual != null)
                                && (!ncmAtual.isEmpty())
                                && (ncmAtual.length() > 5)) {
                            try {
                                NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;
                            } catch (Exception ex) {
                                ncm1 = 402;
                                ncm2 = 99;
                                ncm3 = 0;
                            }
                        } else {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    if ((rst.getString("codcest01") != null)
                            && (!rst.getString("codcest01").trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("codcest01").trim())));
                    } else {
                        idCest = -1;
                    }

                    if ((rst.getString("agrupa01") != null)
                            && (!rst.getString("agrupa01").trim().isEmpty())) {
                        idFamiliaProduto = rst.getInt("agrupa01");
                    } else {
                        idFamiliaProduto = -1;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("codpro01"));
                    oProduto.setDescricaoCompleta((rst.getString("descpro01") == null ? "" : rst.getString("descpro01").trim()));
                    oProduto.setDescricaoReduzida((rst.getString("descabr01") == null ? "" : rst.getString("descabr01").trim()));
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setMargem(rst.getDouble("margtra01"));
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdCest(idCest);
                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);
                    oProduto.setIdFamiliaProduto(idFamiliaProduto);
                    oProduto.setQtdEmbalagem(rst.getInt("qtdeemb01"));                    
                    
                    if ((rst.getString("piscofin01") != null) &&
                            (!rst.getString("piscofin01").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(retornarPisCofinsDebito(rst.getString("piscofin01").trim()));
                        oProduto.setIdTipoPisCofinsCredito(retornarPisCofinsCredito(rst.getString("piscofin01").trim()));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(retornarPisCofinsDebito(""));
                        oProduto.setIdTipoPisCofinsCredito(retornarPisCofinsCredito(""));
                    }
                    
                    if ((rst.getString("natpisco01") != null) &&
                            (!rst.getString("natpisco01").trim().isEmpty())) {
                        oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), rst.getString("natpisco01").trim()));
                    } else {
                        oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    }

                    if ((rst.getString("codgrupo01") != null)
                            && (!rst.getString("codgrupo01").trim().isEmpty())
                            && (rst.getString("codgss01") != null)
                            && (!rst.getString("codgss01").trim().isEmpty())) {
                        MercadologicoVO oMercadologico = new MercadologicoDAO().getMercadologicoByMercad2Mercad3(rst.getInt("codgrupo01"), rst.getInt("codgss01"));
                        oProduto.setMercadologico1(oMercadologico.mercadologico1);
                        oProduto.setMercadologico2(oMercadologico.mercadologico2);
                        oProduto.setMercadologico3(oMercadologico.mercadologico3);
                    } else {
                        oProduto.setMercadologico1(0);
                        oProduto.setMercadologico2(0);
                        oProduto.setMercadologico3(0);
                    }

                    if ((rst.getString("datacad01") != null)
                            && (!rst.getString("datacad01").trim().isEmpty())) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("datacad01")).substring(0, 10).replace("-", "/"));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    long codigoProduto;
                    codigoProduto = (long) oProduto.getId();
                    ProdutoBalancaVO produtoBalanca;

                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validpro01"));
                        oCodigoAnterior.setE_balanca(true);
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());

                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }

                    } else {
                        oProduto.setValidade(rst.getInt("validpro01"));
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);
                        oAutomacao.setCodigoBarras(-2);

                        if ((rst.getString("unidpro01") != null) && (!rst.getString("unidpro01").trim().isEmpty())) {
                            oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidpro01").trim()));
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                        }

                        oCodigoAnterior.setCodigobalanca(0);
                    }
                    
                    oAutomacao.setQtdEmbalagem(1);

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cdsitrib01"), rst.getDouble("icmcompr01"), rst.getDouble("pbcreduz01"), true));

                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setNcm((rst.getString("ncm") == null ? "" : rst.getString("ncm").trim()));
                    oCodigoAnterior.setRef_icmsdebito((rst.getString("cdsitrib01") == null ? "" : rst.getString("cdsitrib01")));
                    oCodigoAnterior.setCest((rst.getString("codcest01") == null ? "" : rst.getString("codcest01").trim()));
                    oCodigoAnterior.setCodigoAuxiliar((rst.getString("piscofin01") == null ? "" : rst.getString("piscofin01").trim()));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("natpisco01"));

                    vResult.put((int) oProduto.getId(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(i_arquivo, idLojaVR);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro01, custenc01 custocomimposto, "
                    + "precust01 custosemimposto "
                    + "from cadpro "
                    + "where codfil01 = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("codpro01"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oAnterior.setCustocomimposto(oComplemento.getCustoComImposto());
                    oAnterior.setCustosemimposto(oComplemento.getCustoSemImposto());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLojaVR + "...");
            vResult = carregarCustoProduto(i_arquivo, idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro01, prevend01 "
                    + "from cadpro "
                    + "where codfil01 = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("codpro01"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("prevend01"));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preco Produto Loja " + idLojaVR + "...");
            vResult = carregarPrecoProduto(i_arquivo, idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro01, estatu01, "
                    + "estmin01, estmax01 "
                    + "from cadpro "
                    + "where codfil01 = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("codpro01"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("estatu01"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estmin01"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estmax01"));
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados Estoque Produto Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(i_arquivo, idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarrasProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        long codigoBarras;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, codbarra, qtdeembal "
                    + "from arqbar "
            )) {
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                    if ((rst.getString("codbarra") != null)
                            && (!rst.getString("codbarra").trim().isEmpty())) {
                        codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim()));
                    } else {
                        codigoBarras = -2;
                    }

                    if (codigoBarras > 999999) {
                        oProduto.setId(rst.getInt("codpro"));
                        oAutomacao.setCodigoBarras(codigoBarras);
                        oAutomacao.setQtdEmbalagem((int) rst.getDouble("qtdeembal"));
                        oProduto.vAutomacao.add(oAutomacao);
                    } else {
                        oProduto.setId(rst.getInt("codpro"));
                        oAnterior.setCodigoanterior(oProduto.getId());
                        oAnterior.setBarras(codigoBarras);
                        oProduto.vCodigoAnterior.add(oAnterior);
                    }
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarrasProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Codigo de Barras...");
            vResult = carregarCodigoBarrasProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                ProdutoDAO produto = new ProdutoDAO();
                produto.alterarBarraAnterio = true;
                produto.addCodigoBarras(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarSituacaoCadastroProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro01, sitpro01 "
                    + "from cadpro "
                    + "where codfil01 = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("codpro01"));
                    
                    oComplemento.setIdLoja(idLojaVR);
                    if ((rst.getString("sitpro01") != null) &&
                            (!rst.getString("sitpro01").trim().isEmpty()) &&
                            ("I".equals(rst.getString("sitpro01").trim()))) {
                        oComplemento.setIdSituacaoCadastro(0);
                    } else {
                        oComplemento.setIdSituacaoCadastro(1);
                    }
                    
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }
    
    public void importarSituacaoCadastroProduto(String i_arquivo, int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Situacao Cadastro Produto Loja "+idLojaCliente+"...");
            vResult = carregarSituacaoCadastroProduto(i_arquivo, idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarSituacaoCadastroProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.codforn02, f.razforn02, f.endforn02, f.cgcforn02, f.insforn02, f.cepforn02, "
                    + "f.fonefor02, f.nomecon02, f.fonecon02, f.nomeabr02, f.prazo02, f.numfax02, f.bairro02, "
                    + "f.nomesup02, f.fonesup02, f.nomeger02, f.foneger02, f.observac02, f.obscontr02, "
                    + "f.datacad02, f.emailfor02, f.pzvisita02, f.numender02, f.compleme02, f.nextelrp02, "
                    + "f.emailrep02, f.celcont02, m.codigo, m.nome municipio, m.uf estado, m.codibge "
                    + "from cadforn f "
                    + "left join tabmun m on m.codigo = f.codmunic02 "
                    + "order by f.razforn02"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setCodigoanterior(rst.getInt("codforn02"));
                    vo.setId(rst.getInt("codforn02"));
                    vo.setRazaosocial((rst.getString("razforn02") == null ? "" : rst.getString("razforn02").trim()));
                    vo.setNomefantasia((rst.getString("nomeabr02") == null ? "" : rst.getString("nomeabr02").trim()));
                    vo.setEndereco((rst.getString("endforn02") == null ? "" : rst.getString("endforn02").trim()));
                    vo.setBairro((rst.getString("bairro02") == null ? "" : rst.getString("bairro02").trim()));
                    vo.setComplemento((rst.getString("compleme02") == null ? "" : Utils.acertarTexto(rst.getString("compleme02").trim())));

                    if ((rst.getString("numender02") != null)
                            && (!rst.getString("numender02").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("numender02").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("cgcforn02") != null)
                            && (!rst.getString("cgcforn02").trim().isEmpty())
                            && (rst.getString("cgcforn02").trim().length() >= 9)) {
                        vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cgcforn02").trim())));

                        if (String.valueOf(vo.getCnpj()).length() > 13) {
                            vo.setId_tipoinscricao(0);
                        } else {
                            vo.setId_tipoinscricao(1);
                        }
                    } else {
                        vo.setId_tipoinscricao(0);
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("insforn02") != null)
                            && (!rst.getString("insforn02").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("insforn02").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("cepforn02") != null)
                            && (!rst.getString("cepforn02").trim().isEmpty())
                            && (rst.getString("cepforn02").trim().length() >= 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cepforn02").trim())));
                    } else {
                        vo.setCep(Global.Cep);
                    }

                    if ((rst.getString("codibge") != null)
                            && (!rst.getString("codibge").trim().isEmpty())
                            && (rst.getString("codibge").trim().length() > 5)) {

                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(rst.getInt("codibge"))
                                == 0
                                        ? Global.idMunicipio
                                        : rst.getInt("codibge")));

                    } else if ((rst.getString("municipio") != null)
                            && (!rst.getString("municipio").trim().isEmpty())) {

                        vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("municipio").trim()),
                                rst.getString("estado")) == 0
                                        ? Global.idMunicipio
                                        : Utils.retornarMunicipioIBGEDescricao(
                                                Utils.acertarTexto(rst.getString("municipio").trim()),
                                                rst.getString("estado"))));

                    } else {
                        vo.setId_municipio(Global.idMunicipio);
                    }

                    vo.setId_estado((Utils.retornarEstadoDescricao(rst.getString("estado").trim())
                            == 0
                                    ? Global.idEstado
                                    : Utils.retornarEstadoDescricao(rst.getString("estado").trim())));

                    if ((rst.getString("fonefor02") != null)
                            && (!rst.getString("fonefor02").trim().isEmpty())
                            && (rst.getString("fonefor02").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("fonefor02").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("datacad02") != null)
                            && (!rst.getString("datacad02").trim().isEmpty())) {
                        vo.setDatacadastroStr(rst.getString("datacad02").trim());
                    } else {
                        vo.setDatacadastroStr("");
                    }

                    vo.setObservacao((rst.getString("observac02") == null ? "" : Utils.acertarTexto(rst.getString("observac02").trim())));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vResult = carregarFornecedor(i_arquivo);
            if (!vResult.isEmpty()) {
                new FornecedorDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorContatoVO> carregarFornecedorContato(String i_arquivo) throws Exception {
        List<FornecedorContatoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.codforn02, f.razforn02, f.endforn02, f.cgcforn02, f.insforn02, f.cepforn02, "
                    + "f.fonefor02, f.nomecon02, f.fonecon02, f.nomeabr02, f.prazo02, f.numfax02, f.bairro02, "
                    + "f.nomesup02, f.fonesup02, f.nomeger02, f.foneger02, f.observac02, f.obscontr02, "
                    + "f.datacad02, f.emailfor02, f.pzvisita02, f.numender02, f.compleme02, f.nextelrp02, "
                    + "f.emailrep02, f.celcont02, m.codigo, m.nome municipio, m.uf estado, m.codibge "
                    + "from cadforn f "
                    + "left join tabmun m on m.codigo = f.codmunic02 "
                    + "order by f.razforn02"

            )) {
                while (rst.next()) {
                    FornecedorContatoVO oContato = new FornecedorContatoVO();
                    oContato.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    oContato.setNome((rst.getString("nomecon02") == null ? "" : rst.getString("nomecon02").trim()));
                    if ((rst.getString("fonecon02") != null) &&
                            (!rst.getString("fonecon02").trim().isEmpty())) {
                        oContato.setTelefone(Utils.formataNumero(rst.getString("fonecon02").trim()));
                    } else {
                        oContato.setTelefone("");
                    }
                    
                    if ((!"".equals(oContato.getNome())) || (!"".equals(oContato.getTelefone()))) {
                        vResult.add(oContato);
                    }

                    FornecedorContatoVO oContato1 = new FornecedorContatoVO();
                    oContato1.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    if ((rst.getString("numfax02") != null)
                            && (!rst.getString("numfax02").trim().isEmpty())) {
                        oContato1.setFax("FAX");
                        oContato1.setFax(Utils.formataNumero(rst.getString("numfax02").trim()));
                    } else {
                        oContato1.setFax("");
                    }
                    
                    if (!"".equals(oContato.getFax())) {
                        vResult.add(oContato1);
                    }

                    FornecedorContatoVO oContato2 = new FornecedorContatoVO();
                    oContato2.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    oContato2.setNome((rst.getString("nomesup02") == null ? "" : rst.getString("nomesup02").trim()));
                    if ((rst.getString("fonesup02") != null) &&
                            (!rst.getString("fonesup02").trim().isEmpty())) {
                        oContato2.setTelefone(Utils.formataNumero(rst.getString("fonesup02").trim()));
                    } else {
                        oContato2.setTelefone("");
                    }
                    
                    if ((!"".equals(oContato.getNome()) || (!"".equals(oContato.getTelefone())))) {
                        vResult.add(oContato2);
                    }
                    
                    FornecedorContatoVO oContato3 = new FornecedorContatoVO();
                    oContato3.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    oContato3.setNome((rst.getString("nomeger02") == null ? "" : rst.getString("nomeger02").trim()));
                    if ((rst.getString("foneger02") != null) &&
                            (!rst.getString("foneger02").trim().isEmpty())) {
                        oContato3.setTelefone(Utils.formataNumero(rst.getString("foneger02").trim()));
                    } else {
                        oContato3.setTelefone("");
                    }
                    
                    if ((!"".equals(oContato3.getNome())) || (!"".equals(oContato3.getTelefone()))) {
                        vResult.add(oContato3);
                    }
                    
                    FornecedorContatoVO oContato4 = new FornecedorContatoVO();
                    oContato4.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    if ((rst.getString("nextelrp02") != null)
                            && (!rst.getString("nextelrp02").trim().isEmpty())) {
                        oContato4.setNome("NEXTEL");
                        oContato4.setTelefone(Utils.formataNumero(rst.getString("nextelrp02").trim()));
                    } else {
                        oContato4.setTelefone("");
                    }
                    
                    if (!"".equals(oContato4.getTelefone())) {
                        vResult.add(oContato4);
                    }

                    FornecedorContatoVO oContato5 = new FornecedorContatoVO();
                    oContato5.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    if ((rst.getString("emailfor02") != null)
                            && (!rst.getString("emailfor02").trim().isEmpty())
                            && (rst.getString("emailfor02").contains("@"))) {
                        oContato5.setNome("EMAIL");
                        oContato5.setEmail(Utils.acertarTexto(rst.getString("emailfor02").trim().toLowerCase()));
                    } else {
                        oContato5.setEmail("");
                    }
                    
                    if (!"".equals(oContato5.getEmail())) {
                        vResult.add(oContato5);
                    }

                    FornecedorContatoVO oContato6 = new FornecedorContatoVO();
                    oContato6.setIdFornecedorAnterior(rst.getInt("codforn02"));
                    if ((rst.getString("celcont02") != null) &&
                            (!rst.getString("celcont02").trim().isEmpty())) {
                        oContato6.setNome("CELULAR");
                        oContato6.setCelular(Utils.formataNumero(rst.getString("celcont02").trim()));
                    } else {
                        oContato6.setCelular("");
                    }
                    
                    if (!"".equals(oContato6.getCelular())) {
                        vResult.add(oContato6);
                    }
                    
                }
            }
        }
        return vResult;
    }
    
    public void importarFornecedorContato(String i_arquivo) throws Exception {
        List<FornecedorContatoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor Contato...");
            vResult = carregarFornecedorContato(i_arquivo);
            if (!vResult.isEmpty()) {
                new FornecedorContatoDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, cdfabric, codforn "
                    + "from arqfab "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_fornecedor(rst.getInt("codforn"));
                    vo.setId_produto(rst.getInt("codpro"));
                    vo.setCodigoexterno((rst.getString("cdfabric") == null ? "" : rst.getString("cdfabric")));
                    vo.setDataalteracao(new Date(new java.util.Date().getTime()));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vResult = carregarProdutoFornecedor(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
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
    
    /* funções sistema fg */
    private int retornarPisCofinsDebito (String i_codigo) {
        int retorno = 1;
        if ("".equals(i_codigo)) {
            retorno = 0;
        } else if ("I".equals(i_codigo)) {
            retorno = 1;
        } else if ("M".equals(i_codigo)) {
            retorno = 3;
        } else if ("E".equals(i_codigo)) {
            retorno = 1;
        } else if ("S".equals(i_codigo)) {
            retorno = 2;
        } else if ("C".equals(i_codigo)) {
            retorno = 8;
        } else if ("P".equals(i_codigo)) {
            retorno = 4;
        } else if ("Z".equals(i_codigo)) {
            retorno = 7;
        } else {
            retorno = 1;
        }
        
        return retorno;
    }
    
    private int retornarPisCofinsCredito (String i_codigo) {
        int retorno = 1;
        if ("".equals(i_codigo)) {
            retorno = 12;
        } else if ("I".equals(i_codigo)) {
            retorno = 13;
        } else if ("M".equals(i_codigo)) {
            retorno = 15;
        } else if ("E".equals(i_codigo)) {
            retorno = 13;
        } else if ("S".equals(i_codigo)) {
            retorno = 14;
        } else if ("C".equals(i_codigo)) {
            retorno = 20;
        } else if ("P".equals(i_codigo)) {
            retorno = 10;
        } else if ("Z".equals(i_codigo)) {
            retorno = 19;
        } else {
            retorno = 13;
        }
        
        return retorno;
    }
}
