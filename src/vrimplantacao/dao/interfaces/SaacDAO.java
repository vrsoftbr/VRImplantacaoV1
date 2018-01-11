package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
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
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.produto.NcmDAO;
import vrimplantacao2.parametro.Parametros;

public class SaacDAO {

    /**
     * *** carregamentos *
     */
    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct vinculacao\n"
                    + "from itens\n"
                    + "where vinculacao is not null\n"
                    + "order by vinculacao"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("vinculacao"));
                    vo.setDescricao(rst.getString("vinculacao"));
                    vo.setCodigoant(rst.getInt("vinculacao"));
                    vResult.add(vo);
                }
            }
            return vResult;
        }
    }
    
    public void importarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            vResult = carregarFamiliaProduto();
            if (!vResult.isEmpty()) {
                FamiliaProdutoDAO familiaProdutoDAO = new FamiliaProdutoDAO();
                familiaProdutoDAO.gerarCodigo = true;
                familiaProdutoDAO.salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarAcertoFamiliaProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idFamiliaProduto;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo,  vinculacao\n"
                    + "from itens\n"
                    + "where vinculacao is not null"
            )) {
                while (rst.next()) {
                    idFamiliaProduto = new FamiliaProdutoDAO().getCodAnt(rst.getLong("vinculacao"));
                    if (idFamiliaProduto != -1) {
                        ProdutoVO vo = new ProdutoVO();
                        vo.setId(rst.getInt("codigo"));
                        vo.setIdFamiliaProduto(idFamiliaProduto);
                        vResult.add(vo);
                    }
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertoFamiliaProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto Familia Produto...");
            vResult = carregarAcertoFamiliaProduto();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarFamiliaProduto_Produto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.codigo, upper(m1.descricao) descricao\n"
                    + "from grupo m1\n"
                    + "order by m1.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setDescricao((rst.getString("descricao") == null ? "" : rst.getString("descricao").trim()));
                    vo.setNivel(nivel);
                    if (nivel == 1) {
                        vo.setMercadologico1(rst.getInt("codigo"));
                    } else if (nivel == 2) {
                        vo.setMercadologico1(rst.getInt("codigo"));
                        vo.setMercadologico2(1);
                    } else if (nivel == 3) {
                        vo.setMercadologico1(rst.getInt("codigo"));
                        vo.setMercadologico2(1);
                        vo.setMercadologico3(1);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    private Map<Integer, ProdutoVO> carregarProduto(int idLoja) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        long codigoBarra = -2;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codigo, p.barras, upper(p.descricao) descricao,\n"
                    + "upper(p.descricaopdv) descricaoreduzida, p.prcvenda, p.grupo,\n"
                    + "p.subgrupo, p.compraprccusto, p.compraultimopreco, p.compraprccustomedio,\n"
                    + "p.ativo,p.dtcadastro, p.margem, p.ncm,p.piscstent, p.cofinscstent,\n"
                    + "p.piscstsai, p.cofinscstsai, p.natrec,p.cst, p.cstent, a.indice,\n"
                    + "p.cest, p.familia,upper(u.descricaoresumida) as tipoembalagem\n"
                    + "from itens p\n"
                    + "inner join aliquotas a on a.codigo = p.aliquota\n"
                    + "inner join unidademedida u on u.codigo = p.unidademedida\n"
                    + "order by codigo"
            )) {
                int contator = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                NcmDAO ncmDAO = new NcmDAO();
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvAliquota().add(oAliquota);

                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codigo"))));
                    oProduto.setDescricaoCompleta((rst.getString("descricao") == null ? "" : rst.getString("descricao").trim()));
                    oProduto.setDescricaoReduzida((rst.getString("descricaoreduzida") == null ? "" : rst.getString("descricaoreduzida").trim()));
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setMercadologico1(rst.getInt("grupo"));
                    oProduto.setMercadologico2(1);
                    oProduto.setMercadologico3(1);
                    oComplemento.setIdLoja(idLoja);

                    if ((rst.getString("ativo") != null)
                            && (!rst.getString("ativo").trim().isEmpty())) {
                        if ("S".equals(rst.getString("ativo").trim())) {
                            oComplemento.setIdSituacaoCadastro(1);
                        } else {
                            oComplemento.setIdSituacaoCadastro(0);
                        }
                    } else {
                        oComplemento.setIdSituacaoCadastro(0);
                    }

                    if ((rst.getString("NCM") != null)
                            && (!rst.getString("NCM").isEmpty())
                            && (rst.getString("NCM").trim().length() > 5)) {

                        vrimplantacao2.vo.enums.NcmVO oNcm = ncmDAO.getNcm(rst.getString("NCM").trim());

                        if (oNcm == null) {
                            oProduto.setNcm1(402);
                            oProduto.setNcm2(99);
                            oProduto.setNcm3(0);
                        } else {
                            oProduto.setNcm1(oNcm.getNcm1());
                            oProduto.setNcm2(oNcm.getNcm2());
                            oProduto.setNcm3(oNcm.getNcm3());
                        }
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("cest") != null)
                            && (!rst.getString("cest").trim().isEmpty())) {

                        if (rst.getString("cest").trim().length() == 5) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("cest").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("cest").trim().substring(1, 3)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("cest").trim().substring(3, 5)));

                        } else if (rst.getString("cest").trim().length() == 6) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("cest").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("cest").trim().substring(1, 4)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("cest").trim().substring(4, 6)));

                        } else if (rst.getString("cest").trim().length() == 7) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("cest").trim().substring(0, 2)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("cest").trim().substring(2, 5)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("cest").trim().substring(5, 7)));
                        }
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    //<editor-fold defaultstate="collapsed" desc="PRODUTOS DE BALANÇA E EMBALAGEM">
                    //Tratando o id da balança.
                    if ((rst.getString("barras") != null)
                            && (!rst.getString("barras").trim().isEmpty())) {
                        codigoBarra = Utils.stringToLong(rst.getString("barras").trim());

                        ProdutoBalancaVO produtoBalanca = null;
                        if (codigoBarra > 0 && codigoBarra <= 999999) {
                            produtoBalanca = produtosBalanca.get(
                                    Integer.parseInt(String.valueOf(codigoBarra).substring(0,
                                                    String.valueOf(codigoBarra).length() - 1)));

                            if (produtoBalanca != null) {
                                oAutomacao.setCodigoBarras(codigoBarra);
                                oProduto.setValidade(produtoBalanca.getValidade() >= 1 ? produtoBalanca.getValidade() : 0);

                                if ("P".equals(produtoBalanca.getPesavel())) {
                                    oAutomacao.setIdTipoEmbalagem(4);
                                    oProduto.setPesavel(false);
                                } else {
                                    oAutomacao.setIdTipoEmbalagem(0);
                                    oProduto.setPesavel(true);
                                }

                                oProduto.eBalanca = true;
                                oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                                oCodigoAnterior.setBarras(codigoBarra);
                                oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                                oCodigoAnterior.setE_balanca(true);
                            } else {
                                if ((rst.getString("tipoembalagem") != null) && (!rst.getString("tipoembalagem").trim().isEmpty())) {
                                    oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("tipoembalagem").trim()));
                                    oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                                } else {
                                    oAutomacao.setIdTipoEmbalagem(0);
                                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                                }

                                if (codigoBarra > 999999) {
                                    oAutomacao.setCodigoBarras(codigoBarra);
                                } else {
                                    oAutomacao.setCodigoBarras(-1);
                                }

                                oProduto.eBalanca = false;
                                oProduto.setValidade(0);
                                oProduto.setPesavel(false);
                                oCodigoAnterior.setBarras(codigoBarra);
                                oCodigoAnterior.setCodigobalanca(0);
                                oCodigoAnterior.setE_balanca(false);
                            }
                        } else {
                            if (codigoBarra > 999999) {
                                oAutomacao.setCodigoBarras(codigoBarra);
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }

                            if ((rst.getString("tipoembalagem") != null) && (!rst.getString("tipoembalagem").trim().isEmpty())) {
                                oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("tipoembalagem").trim()));
                                oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                            } else {
                                oAutomacao.setIdTipoEmbalagem(0);
                                oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                            }

                            oProduto.eBalanca = false;
                            oProduto.setValidade(0);
                            oProduto.setPesavel(false);
                            oCodigoAnterior.setBarras(-2);
                            oCodigoAnterior.setCodigobalanca(0);
                            oCodigoAnterior.setE_balanca(false);
                        }
                    } else {
                        codigoBarra = -2;
                        if ((rst.getString("tipoembalagem") != null) && (!rst.getString("tipoembalagem").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("tipoembalagem").trim()));
                            oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                        }

                        oProduto.eBalanca = false;
                        oProduto.setValidade(0);
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setBarras(-2);
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    //</editor-fold>

                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
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
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(rst.getInt("piscstsai") == 0 ? -1 : rst.getInt("piscstsai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(rst.getInt("piscstent") == 0 ? -1 : rst.getInt("piscstent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(),
                            (rst.getString("natrec") == null ? "" : rst.getString("natrec").trim())));

                    int cstSaida = rst.getInt("cst");
                    int cstEntrada = rst.getInt("cstent");
                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }
                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("indice"), 0, false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("indice"), 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("indice"), 0, false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("indice"), 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("indice"), 0, false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("indice"), 0, true));

                    oCodigoAnterior.setCodigoanterior(Double.parseDouble(Utils.formataNumero(rst.getString("codigo"))));
                    oCodigoAnterior.setMargem(rst.getDouble("margem"));
                    oCodigoAnterior.setNcm(rst.getString("ncm").trim());
                    oCodigoAnterior.setId_loja(idLoja);
                    oCodigoAnterior.setPiscofinsdebito(Utils.retornarPisCofinsDebito(rst.getInt("piscstsai") == 0 ? -1 : rst.getInt("piscstsai")));
                    oCodigoAnterior.setPiscofinscredito(Utils.retornarPisCofinsCredito(rst.getInt("piscstent") == 0 ? -1 : rst.getInt("piscstent")));
                    oCodigoAnterior.setNaturezareceita(Utils.stringToInt(rst.getString("natrec")));
                    oCodigoAnterior.setRef_icmsdebito((rst.getString("cst") == null ? "" : rst.getString("cst").trim()));

                    vResult.put((int) oProduto.getId(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produto..." + contator);
                    contator++;
                }
            }
        }
        return vResult;
    }

    private List<ProdutoVO> carregarCustoProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "compraprccusto,\n"
                    + "compraultimopreco,\n"
                    + "compraprccustomedio\n"
                    + "from itens"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codigo"))));
                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setCustoComImposto(rst.getDouble("compraprccusto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("compraultimopreco"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "prcvenda\n"
                    + "from itens"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codigo"))));
                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setPrecoVenda(rst.getDouble("prcvenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("prcvenda"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "estoque,\n"
                    + "estoquemin,\n"
                    + "estoquemax\n"
                    + "from itens"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codigo"))));
                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public List<OfertaVO> carregarOferta(int idLojaVR,
            Date dataInicio, Date dataOferta) throws Exception {
        List<OfertaVO> vResult = new ArrayList<>();
        int idProduto;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select item, dtinicio, dtfim, precovi, precovf\n"
                    + "from itensoferta "
                    + "where dtfim >= '" + dataOferta + "'"
            )) {
                while (rst.next()) {
                    idProduto = 0;
                    idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("item").trim()));

                    if (idProduto > 0) {
                        OfertaVO vo = new OfertaVO();
                        vo.setId_loja(idLojaVR);
                        vo.setId_produto(idProduto);
                        vo.setDatainicio(dataInicio);
                        vo.setDatatermino(rst.getString("dtfim").replace(".", "/").substring(0, 10));
                        vo.setPrecooferta(rst.getDouble("precovi"));
                        vResult.add(vo);
                    }
                }
            }
        }
        return vResult;
    }

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        int id_municipio, id_estado;
        String dataCadastro;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.codfornecedor, upper(f.fornecedor) fantasia, upper(f.razaosocial) razao,\n"
                    + "f.cgc, f.inscricaoest, f.ativo, f.produtor, f.endereco, f.bairro, f.cep, f.telfixo,\n"
                    + "f.telfax, f.observacao, f.email, f.site, f.datacadastro, f.endereconum, f.enderecocomp,\n"
                    + "upper(l.cidade) cidade, upper(l.estado) estado\n"
                    + "from fornecedores f\n"
                    + "left join localidades l on l.codigo = f.localidade"
            )) {
                int contatador = 1;
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();

                    if (rst.getString("datacadastro") != null) {
                        dataCadastro = rst.getString("datacadastro").substring(0, 10).replace(".", "/");
                    } else {
                        dataCadastro = "";
                    }

                    if ((rst.getString("cidade") != null)
                            && (rst.getString("estado") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (!rst.getString("estado").trim().isEmpty())) {
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade"), rst.getString("estado"));
                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_municipio = Parametros.get().getMunicipioPadrao2().getId(); // CIDADE DO CLIENTE;                   
                    }

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(rst.getString("estado"));
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                    }

                    if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        if (rst.getString("cgc").trim().length() >= 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cgc").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("endereconum") != null)
                            && (!rst.getString("endereconum").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("endereconum").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("ativo") != null)
                            && (!rst.getString("ativo").trim().isEmpty())) {
                        if ("S".equals(rst.getString("ativo").trim())) {
                            vo.setId_situacaocadastro(1);
                        } else {
                            vo.setId_situacaocadastro(0);
                        }
                    } else {
                        vo.setId_situacaocadastro(0);
                    }

                    vo.setDatacadastroStr(dataCadastro);
                    vo.setInscricaoestadual((rst.getString("inscricaoest") == null ? "" : rst.getString("inscricaoest").trim()));
                    vo.setCodigoanterior(Integer.parseInt(Utils.formataNumero(rst.getString("codfornecedor").trim())));
                    vo.setRazaosocial((rst.getString("razao") == null ? "" : rst.getString("razao").trim()));
                    vo.setNomefantasia((rst.getString("fantasia") == null ? "" : rst.getString("fantasia").trim()));
                    vo.setEndereco((rst.getString("endereco") == null ? "" : rst.getString("endereco").trim()));
                    vo.setBairro((rst.getString("bairro") == null ? "" : rst.getString("bairro").trim()));
                    vo.setComplemento((rst.getString("enderecocomp") == null ? "" : rst.getString("enderecocomp").trim()));
                    vo.setId_municipio(id_municipio);
                    vo.setId_estado(id_estado);

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        if (rst.getString("cep").trim().length() >= 8) {
                            vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cep").trim())));
                        } else {
                            vo.setCep(Parametros.get().getCepPadrao());
                        }
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    if ((rst.getString("telfixo") != null)
                            && (!rst.getString("telfixo").trim().isEmpty())) {
                        if (rst.getString("telfixo").trim().length() >= 8) {
                            vo.setTelefone(Utils.formataNumero(rst.getString("telfixo").trim()));
                        } else {
                            vo.setTelefone("0000000000");
                        }
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("telfax") != null)
                            && (!rst.getString("telfax").trim().isEmpty())) {
                        if (rst.getString("telfax").trim().length() >= 8) {
                            vo.setFax(Utils.formataNumero(rst.getString("telfax").trim()));
                        } else {
                            vo.setFax("");
                        }
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        if (rst.getString("email").contains("@")) {
                            vo.setEmail(rst.getString("email").toLowerCase().trim());
                        } else {
                            vo.setEmail("");
                        }
                    }

                    vo.setObservacao((rst.getString("observacao") == null ? "" : rst.getString("observacao").trim()));
                    vResult.add(vo);

                    ProgressBar.setStatus("Carregando dados...Fornecedor..." + contatador);
                    contatador++;
                }
            }
        }
        return vResult;
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        java.sql.Date dataAlteracao;
        dataAlteracao = new Date(new java.util.Date().getTime());

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "fornecedor,\n"
                    + "produto,\n"
                    + "codigofornecedor\n"
                    + "from vinculoitensfornec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(Integer.parseInt(Utils.formataNumero(rst.getString("produto").trim())));
                    vo.setId_fornecedor(Integer.parseInt(Utils.formataNumero(rst.getString("fornecedor").trim())));
                    vo.setCodigoexterno((rst.getString("codigofornecedor") == null ? "" : rst.getString("codigofornecedor").trim()));
                    vo.setDataalteracao(dataAlteracao);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    private List<ClientePreferencialVO> carregarClientePreferencial() throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        String inscricaoEstadual, strDataAdmissao = "", strDataAdmissaoConj, rgConjuge;
        java.sql.Date dataAdmissao, dataAdmissaoConj;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select substring (cast(c.codigo as varchar(10)) from 4 for 6) codigo_cli, c.codigo, upper(c.nome) nome, upper(c.razaosocial) razao, c.cgccpf, c.rg,\n"
                    + "c.inscest, c.situacao, c.tipo, c.estcivil, c.sexo, c.dtnasc, c.endereco, c.endereconum,\n"
                    + "c.enderecocomp, c.bairro, upper(l.cidade) cidade, upper(l.estado) estado, c.cep,\n"
                    + "c.foneres, c.fonecel, c.filiacaopai, c.filiacaomae, c.email, c.empnome, c.empfone,\n"
                    + "c.empendereco, c.empbairro, upper(lo.cidade) cidadeEmpresa, upper(lo.estado) estadoEmpresa,\n"
                    + "c.empfuncao, c.empdtadmissao, c.empgerente, c.emprenda, c.conjnome, c.conjdtnasc, c.conjrg,\n"
                    + "c.conjcgccpf, c.conjempnome, c.conjempfone, c.conjempendereco, c.conjempfuncao, c.conjempdtadmissao,\n"
                    + "c.conjemprenda, c.limitecheque, c.limitecrediario, c.credpossui, c.creddiavenc, c.credsituacao,\n"
                    + "c.dtcad, c.ativo, c.contato, c.observacoes\n"
                    + "from client c\n"
                    + "left join localidades l on l.codigo = c.localidade\n"
                    + "left join localidades lo on lo.codigo = c.emplocalidade\n"
                    + "left join localidades lo2 on lo2.codigo = c.conjemploc"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    if ((rst.getString("cgccpf") != null)
                            && (!rst.getString("cgccpf").trim().isEmpty())) {
                        if (rst.getString("cgccpf").trim().length() >= 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cgccpf").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        inscricaoEstadual = Utils.acertarTexto(rst.getString("rg").replace(".", ""));
                        inscricaoEstadual = inscricaoEstadual.replace("-", "");
                        inscricaoEstadual = inscricaoEstadual.replace(",", "");
                        inscricaoEstadual = inscricaoEstadual.replace("/", "");
                        inscricaoEstadual = inscricaoEstadual.replace("'\'", "");
                        vo.setInscricaoestadual(inscricaoEstadual);
                    } else if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").trim().isEmpty())) {
                        inscricaoEstadual = Utils.acertarTexto(rst.getString("inscest").replace(".", ""));
                        inscricaoEstadual = inscricaoEstadual.replace("-", "");
                        inscricaoEstadual = inscricaoEstadual.replace(",", "");
                        inscricaoEstadual = inscricaoEstadual.replace("/", "");
                        inscricaoEstadual = inscricaoEstadual.replace("'\'", "");
                        vo.setInscricaoestadual(inscricaoEstadual);
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("ativo") != null)
                            && (!rst.getString("ativo").trim().isEmpty())) {
                        if ("S".equals(rst.getString("ativo").trim())) {
                            vo.setId_situacaocadastro(1);
                        } else {
                            vo.setId_situacaocadastro(0);
                        }
                    } else {
                        vo.setId_situacaocadastro(0);
                    }

                    if ((rst.getString("tipo") != null)
                            && (!rst.getString("tipo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("tipo").trim())) {
                            vo.setId_tipoinscricao(1);
                        } else {
                            vo.setId_tipoinscricao(0);
                        }
                    } else {
                        vo.setId_tipoinscricao(1);
                    }

                    if ((rst.getString("estcivil") != null)
                            && (!rst.getString("estcivil").trim().isEmpty())) {
                        if ("A".equals(rst.getString("estcivil").trim())) {
                            vo.setId_tipoestadocivil(4);
                        } else if ("C".equals(rst.getString("estcivil").trim())) {
                            vo.setId_tipoestadocivil(2);
                        } else if ("D".equals(rst.getString("estcivil").trim())) {
                            vo.setId_tipoestadocivil(6);
                        } else if ("S".equals(rst.getString("estcivil").trim())) {
                            vo.setId_tipoestadocivil(1);
                        } else if ("V".equals(rst.getString("estcivil").trim())) {
                            vo.setId_tipoestadocivil(3);
                        } else {
                            vo.setId_tipoestadocivil(0);
                        }
                    } else {
                        vo.setId_tipoestadocivil(0);
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("sexo").trim())) {
                            vo.setSexo(0);
                        } else {
                            vo.setSexo(1);
                        }
                    } else {
                        vo.setSexo(1);
                    }

                    if ((rst.getString("dtnasc") != null)
                            && (!rst.getString("dtnasc").trim().isEmpty())) {
                        vo.setDatanascimento(rst.getString("dtnasc").substring(0, 10).trim().replace(".", "/"));
                    } else {
                        vo.setDatanascimento("");
                    }

                    vo.setCodigoanterior(Long.parseLong(Utils.formataNumero(rst.getString("codigo").trim())));
                    vo.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codigo_cli").trim())));
                    vo.setNome((rst.getString("nome") == null ? "" : rst.getString("nome").trim()));
                    vo.setEndereco((rst.getString("endereco") == null ? "" : rst.getString("endereco").trim()));
                    vo.setBairro((rst.getString("bairro") == null ? "" : rst.getString("bairro").trim()));
                    vo.setComplemento((rst.getString("enderecocomp") == null ? "" : rst.getString("enderecocomp").trim()));

                    if ((rst.getString("endereconum") != null)
                            && (!rst.getString("endereconum").trim().isEmpty())) {
                        vo.setNumero(rst.getString("endereconum").trim());
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {

                        vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                Utils.acertarTexto(rst.getString("estado").trim())) == 0
                                        ? Parametros.get().getMunicipioPadrao2().getId()
                                        : Utils.retornarMunicipioIBGEDescricao(
                                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                                Utils.acertarTexto(rst.getString("estado").trim()))));
                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {
                        vo.setId_estado((Utils.retornarEstadoDescricao(
                                Utils.acertarTexto(rst.getString("estado"))) == 0
                                        ? Parametros.get().getUfPadrao().getId()
                                        : Utils.retornarEstadoDescricao(
                                                Utils.acertarTexto(rst.getString("estado").trim()))));
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadrao().getId());
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        if (rst.getString("cep").trim().length() >= 8) {
                            vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cep").trim())));
                        } else {
                            vo.setCep(Parametros.get().getCepPadrao());
                        }
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    if ((rst.getString("foneres") != null)
                            && (!rst.getString("foneres").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("foneres").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("fonecel") != null)
                            && (!rst.getString("fonecel").trim().isEmpty())) {
                        vo.setCelular(Utils.formataNumero(rst.getString("fonecel").trim()));
                    } else {
                        vo.setCelular("");
                    }

                    vo.setNomepai((rst.getString("filiacaopai") == null ? "" : rst.getString("filiacaopai").trim()));
                    vo.setNomemae((rst.getString("filiacaomae") == null ? "" : rst.getString("filiacaomae").trim()));
                    vo.setEmail((rst.getString("email") == null ? "" : rst.getString("email").trim()));
                    vo.setEmpresa((rst.getString("empnome") == null ? "" : rst.getString("empnome").trim()));
                    vo.setTelefoneempresa((rst.getString("empfone") == null ? "" : rst.getString("empfone").trim()));
                    vo.setEnderecoempresa((rst.getString("empendereco") == null ? "" : rst.getString("empendereco").trim()));
                    vo.setBairroempresa((rst.getString("empbairro") == null ? "" : rst.getString("empbairro").trim()));

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {

                        vo.setId_municipioempresa((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                Utils.acertarTexto(rst.getString("estado").trim()))));
                    } else {
                        vo.setId_municipioempresa(0);
                    }

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {
                        vo.setId_estadoempresa((Utils.retornarEstadoDescricao(
                                Utils.acertarTexto(rst.getString("estado")))));
                    } else {
                        vo.setId_estadoempresa(0);
                    }

                    vo.setCargo((rst.getString("empfuncao") == null ? "" : rst.getString("empfuncao").trim()));
                    vo.setSalario(rst.getDouble("emprenda"));

                    if ((rst.getString("empdtadmissao") != null)
                            && (!rst.getString("empdtadmissao").trim().isEmpty())) {
                        strDataAdmissao = rst.getString("empdtadmissao").substring(0, 10).replace(".", "/").trim();
                        dataAdmissao = new java.sql.Date(fmt.parse(strDataAdmissao).getTime());
                        vo.setDataadmissao(dataAdmissao);
                    } else {
                        vo.setDataadmissao(null);
                    }

                    vo.setNomeconjuge((rst.getString("conjnome") == null ? "" : rst.getString("conjnome").trim()));

                    if ((rst.getString("conjdtnasc") != null)
                            && (!rst.getString("conjdtnasc").trim().isEmpty())) {
                        vo.setDatanascimentoconjuge(rst.getString("conjdtnasc").substring(0, 10).trim().replace(".", "/"));
                    } else {
                        vo.setDatanascimentoconjuge(null);
                    }

                    if ((rst.getString("conjrg") != null)
                            && (!rst.getString("conjrg").trim().isEmpty())) {
                        rgConjuge = Utils.acertarTexto(rst.getString("conjrg").trim().replace(".", ""));
                        rgConjuge = rgConjuge.replace("-", "");
                        rgConjuge = rgConjuge.replace("/", "");
                        rgConjuge = rgConjuge.replace("'\'", "");
                        vo.setRgconjuge(rgConjuge);
                    } else {
                        vo.setRgconjuge("");
                    }

                    if ((rst.getString("conjcgccpf") != null)
                            && (!rst.getString("conjcgccpf").trim().isEmpty())) {
                        if (rst.getString("conjcgccpf").trim().length() >= 9) {
                            vo.setCpfconjuge(Double.parseDouble(Utils.formataNumero(rst.getString("conjcgccpf").trim())));
                        } else {
                            vo.setCpfconjuge(-1);
                        }
                    } else {
                        vo.setCpfconjuge(-1);
                    }

                    vo.setEmpresaconjuge((rst.getString("conjempnome") == null ? "" : rst.getString("conjempnome").trim()));

                    if ((rst.getString("conjempfone") != null)
                            && (!rst.getString("conjempfone").trim().isEmpty())) {
                        vo.setTelefoneempresaconjuge(Utils.formataNumero(rst.getString("conjempfone").trim()));
                    } else {
                        vo.setTelefoneempresaconjuge("");
                    }

                    vo.setEnderecoempresaconjuge((rst.getString("conjempendereco") == null ? "" : rst.getString("conjempendereco").trim()));
                    vo.setCargoconjuge((rst.getString("conjempfuncao") == null ? "" : rst.getString("conjempfuncao").trim()));

                    if ((rst.getString("conjempdtadmissao") != null)
                            && (!rst.getString("conjempdtadmissao").trim().isEmpty())) {
                        strDataAdmissaoConj = rst.getString("conjempdtadmissao").replace(".", "/").trim().substring(0, 10);
                        dataAdmissaoConj = new java.sql.Date(fmt.parse(strDataAdmissaoConj).getTime());
                        vo.setDataadmissaoconjuge(dataAdmissaoConj);
                    } else {
                        vo.setDataadmissaoconjuge(null);
                    }

                    vo.setSalarioconjuge(rst.getDouble("conjemprenda"));
                    vo.setValorlimite(rst.getDouble("limitecheque") + rst.getDouble("limitecrediario"));

                    if ((rst.getString("credpossui") != null)
                            && (!rst.getString("credpossui").trim().isEmpty())) {
                        if ("S".equals(rst.getString("credpossui").trim())) {
                            vo.setPermitecreditorotativo(true);
                            vo.setPermitecheque(true);
                        } else {
                            vo.setPermitecreditorotativo(false);
                            vo.setPermitecheque(false);
                        }
                    } else {
                        vo.setPermitecreditorotativo(false);
                        vo.setPermitecheque(false);
                    }

                    vo.setVencimentocreditorotativo(rst.getInt("creddiavenc"));

                    if ((rst.getString("credsituacao") != null)
                            && (!rst.getString("credsituacao").trim().isEmpty())) {
                        if ("L".equals(rst.getString("credsituacao").trim())) {
                            vo.setBloqueado(false);
                        } else {
                            vo.setBloqueado(true);
                        }
                    } else {
                        vo.setBloqueado(false);
                    }

                    if ((rst.getString("dtcad") != null)
                            && (!rst.getString("dtcad").trim().isEmpty())) {
                        vo.setDatacadastro(rst.getString("dtcad").replace(".", "/").trim().substring(0, 10));
                    } else {
                        vo.setDatacadastro("");
                    }

                    vo.setObservacao((rst.getString("observacoes") == null ? "" : rst.getString("observacoes").trim()));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        vo.setObservacao2("CONTATO - " + Utils.acertarTexto(rst.getString("contato").trim()));
                    } else {
                        vo.setObservacao2("");
                    }

                    ProgressBar.setStatus("Carregando dados...Cliente Preferencial..."+contador+"...");
                    contador++;
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLoja) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        long numeroCupom;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cliente, docnum, parcelanum, valor, datadocumento, datavencimento, caixa\n"
                    + "from finctreceber\n"
                    + "where baixa = 'N'"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();

                    if ((rst.getString("docnum") != null)
                            && (!rst.getString("docnum").trim().isEmpty())) {
                        numeroCupom = Long.parseLong(Utils.formataNumero(rst.getString("docnum").trim()));
                    } else {
                        numeroCupom = 0;
                    }

                    vo.setId_clientepreferencial(Integer.parseInt(Utils.formataNumero(rst.getString("cliente").trim())));
                    vo.setValor(rst.getDouble("valor"));
                    vo.setDataemissao(rst.getString("datadocumento").trim().replace(".", "/").substring(0, 10));
                    vo.setDatavencimento(rst.getString("datavencimento").trim().replace(".", "/").substring(0, 10));
                    vo.setEcf(rst.getInt("caixa"));
                    vo.setParcela(rst.getInt("parcelanum"));
                    vo.setObservacao("IMPORTACAO VR");
                    vo.setId_loja(idLoja);

                    if (numeroCupom > Integer.MAX_VALUE) {
                        vo.setObservacao(vo.getObservacao() + " NUMERO DOCUMENTO - " + rst.getString("docnum").trim());
                    } else {
                        vo.setNumerocupom((int) numeroCupom);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    private List<ReceberChequeVO> carregarReceberCheque(int idLoja) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        long codigoAnterior, idCliente, numeroCupom;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cliente, cpf, nome, valor, banco, cheque, telefone, caixa, devolvido,\n"
                    + "datadevolucao, alinea, datainclusao, datavencimento, observacoes, cupom\n"
                    + "from finchequerec\n"
                    + "where baixa = 'N'"
            )) {
                while (rst.next()) {
                    ReceberChequeVO vo = new ReceberChequeVO();
                    codigoAnterior = Long.parseLong(Utils.formataNumero(rst.getString("cliente").trim()));
                    idCliente = new ClientePreferencialDAO().getIdByCodigoAnterior((int) codigoAnterior, idLoja);

                    if ((rst.getString("cupom") != null)
                            && (!rst.getString("cupom").trim().isEmpty())) {
                        numeroCupom = Long.parseLong(Utils.formataNumero(rst.getString("cupom").trim()));
                    } else {
                        numeroCupom = 0;
                    }

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        vo.setCpf(Long.parseLong(Utils.formataNumero(rst.getString("cpf").trim())));
                    } else {
                        vo.setCpf(new ClientePreferencialDAO().getCnpj((int) idCliente));
                    }

                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        vo.setNome(Utils.acertarTexto(rst.getString("nome").trim()));
                    } else {
                        vo.setNome(new ClientePreferencialDAO().getNome((int) idCliente));
                    }

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("telefone").trim()));
                    } else {
                        vo.setTelefone(new ClientePreferencialDAO().getTelefone((int) idCliente));
                    }

                    if ((rst.getString("banco") != null)
                            && (!rst.getString("banco").trim().isEmpty())) {
                        vo.setId_banco(new BancoDAO().getId(rst.getInt("banco")));
                    } else {
                        vo.setId_banco(804);
                    }

                    if ((rst.getString("cheque") != null)
                            && (!rst.getString("cheque").trim().isEmpty())) {
                        vo.setNumerocheque(Integer.parseInt(Utils.formataNumero(rst.getString("cheque").trim())));
                    } else {
                        vo.setNumerocheque(0);
                    }

                    if ((rst.getString("devolvido") != null)
                            && (!rst.getString("devolvido").trim().isEmpty())) {
                        if ("N".equals(rst.getString("devolvido").trim())) {
                            if ((rst.getString("alinea") != null)
                                    && (!rst.getString("alina").trim().isEmpty())) {
                                vo.setId_tipoalinea(rst.getInt("alinea"));
                            } else {
                                vo.setId_tipoalinea(0);
                            }
                        } else {
                            if ((rst.getString("alinea") != null)
                                    && (!rst.getString("alinea").trim().isEmpty())) {
                                vo.setId_tipoalinea(rst.getInt("alinea"));
                            } else {
                                vo.setId_tipoalinea(11);
                            }
                            vo.setDatadevolucao(rst.getString("datadevolucao").trim().replace(".", "/").substring(0, 10));
                        }
                    } else {
                        vo.setDatadevolucao("");
                        vo.setId_tipoalinea(0);
                    }

                    vo.setValor(rst.getDouble("valor"));
                    vo.setData(rst.getString("datainclusao").trim().replace(".", "/").substring(0, 10));
                    vo.setDatadeposito(rst.getString("datavencimento").trim().replace(".", "/").substring(0, 10));
                    vo.setEcf(rst.getInt("caixa"));

                    if ((rst.getString("observacoes") != null)
                            && (!rst.getString("observacoes").trim().isEmpty())) {
                        vo.setObservacao(Utils.acertarTexto(rst.getString("observacoes").trim()));
                    } else {
                        vo.setObservacao("IMPORTACAO VR");
                    }

                    if (numeroCupom > Integer.MAX_VALUE) {
                        vo.setObservacao(vo.getObservacao() + " NUMERO DOCUMENTO - " + rst.getString("cupom").trim());
                    } else {
                        vo.setNumerocupom((int) numeroCupom);
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    /**
     * * importações
     *
     *
     * @throws java.lang.Exception
     */
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

            new MercadologicoDAO().salvarMax();
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

    public void importarProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produto...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(idLoja);
            ProgressBar.setMaximum(vProduto.size());
            List<LojaVO> vLoja = new LojaDAO().carregar();

            for (Integer keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vResult.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vResult, idLoja, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoManterBalanca(int idLojaVR) throws Exception {

        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProduto(idLojaVR).values()) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());

        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod : aux.values()) {
            if (prod.eBalanca) {
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;

        ProgressBar.setStatus("Carregando dados...Produtos de balança...");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais...");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }

    public void importarCustoProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLoja + "...");
            vResult = carregarCustoProduto(idLoja);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarPrecoProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Produto Loja " + idLoja + "...");
            vResult = carregarPrecoProduto(idLoja);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto Loja " + idLoja + "...");
            vResult = carregarEstoqueProduto(idLoja);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueProduto(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarOferta(int idLojaVR, Date dataInicio,
            Date dataOferta) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOferta(idLojaVR, dataInicio, dataOferta);
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }

    public void importarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vResult = carregarFornecedor();
            if (!vResult.isEmpty()) {
                new FornecedorDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vResult = carregarProdutoFornecedor();
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(int idLoja) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vResult = carregarClientePreferencial();
            if (!vResult.isEmpty()) {
                new PlanoDAO().salvar(idLoja);
                new ClientePreferencialDAO().salvar(vResult, idLoja, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int idLoja) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo Loja " + idLoja + "...");
            vResult = carregarReceberCreditoRotativo(idLoja);
            if (!vResult.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvar(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCheque(int idLoja) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cheque Loja " + idLoja + "...");
            vResult = carregarReceberCheque(idLoja);
            if (!vResult.isEmpty()) {
                new ReceberChequeDAO().salvar(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
