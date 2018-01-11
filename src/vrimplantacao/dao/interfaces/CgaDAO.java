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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.CestDAO;
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
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.dao.implantacao.AliquotaCgaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.AliquotaCgaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
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
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

public class CgaDAO {

    private List<ProdutoVO> carregarAcertarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int mercadologico1, mercadologico2, mercadologico3;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"SECCod\",\n"
                    + "ret051.\"GRUCod\", ret051.\"SUBGCod\"\n"
                    + "from ret051"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(new ProdutoDAO().getIdProdutoCodigoAnterior(rst.getLong("PRODCod")));

                    Integer idMercadologico1 = rst.getInt("SECCod");
                    if (!rst.wasNull()) {
                        mercadologico1 = idMercadologico1;
                    } else {
                        mercadologico1 = 0;
                    }

                    Integer idMercadologico2 = rst.getInt("GRUCod");
                    if (!rst.wasNull()) {
                        mercadologico2 = idMercadologico2;
                    } else {
                        mercadologico2 = 0;
                    }

                    Integer idMercadologico3 = rst.getInt("SUBGCod");
                    if (!rst.wasNull()) {
                        mercadologico3 = idMercadologico3;
                    } else {
                        mercadologico3 = 0;
                    }

                    vo.setMercadologico1(mercadologico1);
                    vo.setMercadologico2(mercadologico2);
                    vo.setMercadologico3(mercadologico3);
                    vResult.add(vo);

                    ProgressBar.setStatus("Carregando dados...Acertar Mercadologico Produto..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarAcertarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Mercadologico Produto...");
            vResult = carregarAcertarMercadologicoProduto();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarMercadologicoProdutoSemCodigoAnterior(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<AliquotaCgaVO> carregarAliquotaCga() throws Exception {
        List<AliquotaCgaVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret016.\"ALIQCod\", ret016.\"ALIQDesc\",ret016.\"ALIQNFPerc\",\n"
                    + "ret016.\"ALIQRedNF\", ret016.\"ALIQPerc\", ret016.\"ALIQBema\"\n"
                    + "from ret016"
            )) {
                while (rst.next()) {
                    AliquotaCgaVO vo = new AliquotaCgaVO();
                    vo.setCodigo(rst.getInt("ALIQCod"));
                    vo.setAliquotadescricao(Utils.acertarTexto(rst.getString("ALIQDesc")));
                    vo.setAliquotaNFperc(rst.getDouble("ALIQNFPerc"));
                    vo.setAliquotaNFred(rst.getDouble("ALIQRedNF"));
                    vo.setAliquotaperc(rst.getDouble("ALIQPerc"));
                    vo.setCodigoaliquota(Utils.acertarTexto(rst.getString("ALIQBema")));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarAliquotaCGA() throws Exception {
        List<AliquotaCgaVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Aliquotas Sistema CGA...");
            vResult = carregarAliquotaCga();
            if (!vResult.isEmpty()) {
                new AliquotaCgaDAO().salvar(vResult);
            }
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

    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret011.\"SUBCod\", ret011.\"SUBDesc\" "
                    + "from ret011"
            )) {
                int contador = 1;
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();

                    vo.setId(Integer.parseInt(rst.getString("SUBCod")));
                    vo.setDescricao(Utils.acertarTexto(rst.getString("SUBDesc").replace("'", "")));
                    vo.setId_situacaocadastro(1);
                    vo.setCodigoant(0);
                    vResult.add(vo);

                    ProgressBar.setStatus("Carregando dados...Familia Produto..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
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

    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        String descricao = "";
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret018.\"SECCod\", ret018.\"SECDesc\", ret019.\"GRUCod\",\n"
                    + "ret019.\"GRUDesc\", ret020.\"SUBGCod\",ret020.\"SUBGDesc\"\n"
                    + "from ret018\n"
                    + "INNER JOIN RET019 ON RET018.\"SECCod\"  = RET019.\"SECCod\"\n"
                    + "INNER JOIN ret020 ON RET020.\"GRUCod\" = RET019.\"GRUCod\"\n"
                    + "order by RET018.\"SECCod\", RET020.\"GRUCod\""
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    if (nivel == 1) {
                        descricao = Utils.acertarTexto(rst.getString("SECDesc").trim());
                        vo.setMercadologico1(rst.getInt("SECCod"));
                    } else if (nivel == 2) {
                        descricao = Utils.acertarTexto(rst.getString("GRUDesc").trim());
                        vo.setMercadologico1(rst.getInt("SECCod"));
                        vo.setMercadologico2(rst.getInt("GRUCod"));
                    } else if (nivel == 3) {
                        descricao = Utils.acertarTexto(rst.getString("SUBGDesc").trim());
                        vo.setMercadologico1(rst.getInt("SECCod"));
                        vo.setMercadologico2(rst.getInt("GRUCod"));
                        vo.setMercadologico3(rst.getInt("SUBGCod"));
                    }
                    vo.setDescricao(descricao);
                    vo.setNivel(nivel);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologico(1);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProdutos(int idLojaDestino) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        int validade = 0, mercadologico1, mercadologico2, mercadologico3,
                ncm1, ncm2, ncm3, cst_pisdebito, cst_piscredito, id_tipopiscofinsdebito,
                id_tipopiscofinscredito, qtdembalagem, naturezareceita, id_familiaproduto,
                id_situacaocadastro, cst_pisdebitoAux, cst_piscreditoAux, cstSaida = 0, cstEntrada = 0, idCest;
        double margem, precovenda, custo;
        String ncmAtual, datacadastro, codigoAliquotaCga;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODNome\", ret051.\"PRODNomeRed\",\n"
                    + "ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\",\n"
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\",\n"
                    + "ret051.\"SUBGCod\", ret051.prodai, ret051.\"SECCod\",\n"
                    + "ret051.\"PRODBARCod\", ret051.clasfisccod, ret051.ncm,\n"
                    + "ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\",\n"
                    + "ret051.prodsdo, prodqtemb, ret051.\"ALIQCod\", ret051.\"TABBCod\" cstSaida,\n"
                    + "al1.\"ALIQNFPerc\" aliqDebito, al1.\"ALIQRedNF\" redDebito, ret051.aliqcred,\n"
                    + "ret051.tabbcred cstEntrada, al2.\"ALIQNFPerc\" aliqCredito, al2.\"ALIQRedNF\" redCredito,\n"
                    + "ret041.clasfisccod ncm, ret041.clasfisccest CODCEST, ret051.\"PRODUnid\"\n"
                    + "from RET051\n"
                    + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod\n"
                    + "left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "left join ret016 al1 on al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
                    + "left join ret016 al2 on al2.\"ALIQCod\" = ret051.aliqcred\n"
                    + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0\n"
                    + "order by ret053.\"PRODCod\""
            )) {
                int contador = 1;
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    Integer idMercadologico1 = rst.getInt("SECCod");
                    if (!rst.wasNull()) {
                        mercadologico1 = idMercadologico1;
                    } else {
                        mercadologico1 = 0;
                    }

                    Integer idMercadologico2 = rst.getInt("GRUCod");
                    if (!rst.wasNull()) {
                        mercadologico2 = idMercadologico2;
                    } else {
                        mercadologico2 = 0;

                    }

                    Integer idMercadologico3 = rst.getInt("SUBGCod");
                    if (!rst.wasNull()) {
                        mercadologico3 = idMercadologico3;
                    } else {
                        mercadologico3 = 0;
                    }

                    if (rst.getString("PRODCADAST") != null) {
                        datacadastro = Util.formatDataGUI(rst.getDate("PRODCADAST"));
                    } else {
                        datacadastro = "";
                    }

                    if ((rst.getString("prodstcofins") != null)
                            && (!"".equals(rst.getString("prodstcofins").trim()))) {

                        cst_pisdebito = Integer.parseInt(rst.getString("prodstcofins").trim());
                        cst_pisdebitoAux = cst_pisdebito;
                        id_tipopiscofinsdebito = Utils.retornarPisCofinsDebito(cst_pisdebito);
                    } else {
                        cst_pisdebitoAux = -1;
                        id_tipopiscofinsdebito = 1;
                    }

                    if ((rst.getString("prodstcofinsent") != null)
                            && (!"".equals(rst.getString("prodstcofinsent").trim()))) {

                        cst_piscredito = Integer.parseInt(rst.getString("prodstcofinsent").trim());
                        cst_piscreditoAux = cst_piscredito;
                        id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_piscredito);
                    } else {
                        cst_piscreditoAux = -1;
                        id_tipopiscofinscredito = 13;
                    }

                    naturezareceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito, "");

                    
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

                    if ((rst.getString("CODCEST") != null) &&
                            (!rst.getString("CODCEST").trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                    } else {
                        idCest = -1;
                    }
                    
                    Integer idQtdEmbalagem = rst.getInt("prodqtemb");
                    if (!rst.wasNull()) {
                        if (idQtdEmbalagem == 0) {
                            qtdembalagem = 1;
                        } else {
                            qtdembalagem = idQtdEmbalagem;
                        }
                    } else {
                        qtdembalagem = 1;
                    }

                    Double idMargem = rst.getDouble("PRODMargem");
                    if (!rst.wasNull()) {
                        margem = idMargem;
                    } else {
                        margem = 0;
                    }

                    Double idCusto = rst.getDouble("PRODCusto");
                    if (!rst.wasNull()) {
                        custo = idCusto;
                    } else {
                        custo = 0;
                    }

                    Double idPrecovenda = rst.getDouble("PRODVenda");
                    if (!rst.wasNull()) {
                        precovenda = idPrecovenda;
                    } else {
                        precovenda = 0;
                    }

                    if ((rst.getString("prodai") != null)
                            && (!"".equals(rst.getString("prodai").trim()))) {
                        if ("A".equals(rst.getString("prodai"))) {
                            id_situacaocadastro = 1;
                        } else {
                            id_situacaocadastro = 0;
                        }
                    } else {
                        id_situacaocadastro = 0;
                    }

                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("PRODCod"));
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oProduto.setDataCadastro(datacadastro);
                    oProduto.setDescricaoCompleta((rst.getString("PRODNome") == null ? "" : rst.getString("PRODNome").trim()));
                    oProduto.setDescricaoReduzida((rst.getString("PRODNomeRed") == null ? "" : rst.getString("PRODNomeRed").trim()));
                    oProduto.setDescricaoGondola((rst.getString("PRODEtq") == null ? "" : rst.getString("PRODEtq").trim()));
                    oProduto.setMercadologico1(mercadologico1);
                    oProduto.setMercadologico2(mercadologico2);
                    oProduto.setMercadologico3(mercadologico3);
                    oProduto.setIdTipoPisCofinsDebito(id_tipopiscofinsdebito);
                    oProduto.setIdTipoPisCofinsCredito(id_tipopiscofinscredito);
                    oProduto.setTipoNaturezaReceita(naturezareceita);
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setQtdEmbalagem(qtdembalagem);
                    oProduto.setMargem(margem);
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
                    oProduto.setIdCest(idCest);

                    oComplemento.setIdLoja(idLojaDestino);
                    oComplemento.setPrecoVenda(precovenda);
                    oComplemento.setPrecoDiaSeguinte(precovenda);
                    oComplemento.setCustoComImposto(custo);
                    oComplemento.setCustoSemImposto(custo);
                    oComplemento.setIdSituacaoCadastro(id_situacaocadastro);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {

                        if ((rst.getDouble("aliqDebito") == 0)
                                && (rst.getDouble("redDebito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstSaida") == 20) {

                                    if ((rst.getDouble("aliqDebito") > 0)
                                            && (rst.getDouble("redDebito") == 0)) {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstSaida = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstSaida = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstSaida = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstSaida") == 20) {

                                if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") > 0)) {

                                    cstSaida = rst.getInt("cstSaida");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = rst.getInt("cstSaida");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstSaida = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstSaida = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstSaida = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redDebito") > 0) {
                                cstSaida = 20;
                            } else {
                                cstSaida = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstSaida = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstSaida = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstSaida = 41;
                            }
                        }
                    }

                    if ((rst.getString("cstEntrada") != null)
                            && (!rst.getString("cstEntrada").trim().isEmpty())) {

                        if ((rst.getDouble("aliqCredito") == 0)
                                && (rst.getDouble("redCredito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstEntrada") == 20) {

                                    if ((rst.getDouble("aliqCredito") > 0)
                                            && (rst.getDouble("redCredito") == 0)) {

                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstEntrada = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstEntrada = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstEntrada = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstEntrada") == 20) {

                                if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") > 0)) {

                                    cstEntrada = rst.getInt("cstEntrada");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = rst.getInt("cstEntrada");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstEntrada = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstEntrada = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstEntrada = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redCredito") > 0) {
                                cstEntrada = 20;
                            } else {
                                cstEntrada = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstEntrada = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstEntrada = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstEntrada = 41;
                            }
                        }
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), true));

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
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : validade);
                        oCodigoAnterior.setE_balanca(true);

                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }

                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oProduto.setValidade(validade);
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);

                        if ((rst.getString("PRODBARCod") != null)
                                && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                            if (Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())) >= 1000000) {

                                if (rst.getString("PRODBARCod").trim().length() > 14) {
                                    oAutomacao.setCodigoBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                                } else {
                                    oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                                }
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                        
                        if ((rst.getString("PRODUnid") != null) && (!rst.getString("PRODUnid").trim().isEmpty())) {
                            oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("PRODUnid").trim()));
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                        }
                                          
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                    if ((rst.getString("PRODBARCod") != null)
                            && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                        if (rst.getString("PRODBARCod").trim().length() > 14) {
                            oCodigoAnterior.setBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                        } else {
                            oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }

                    oCodigoAnterior.setPiscofinsdebito(cst_pisdebitoAux);
                    oCodigoAnterior.setPiscofinscredito(cst_piscreditoAux);
                    oCodigoAnterior.setMargem(margem);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {
                        oCodigoAnterior.setRef_icmsdebito(rst.getString("cstSaida").trim());
                    } else {
                        oCodigoAnterior.setRef_icmsdebito("");
                    }

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())) {
                        oCodigoAnterior.setNcm(rst.getString("ncm").trim());
                    } else {
                        oCodigoAnterior.setNcm("");
                    }

                    vResult.put((int) oProduto.getId(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutos(idLojaVR);
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

    private List<ProdutoVO> carregarCustoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idProduto;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODCusto\",\n"
                    + "ret051.prodcustofinal\n"
                    + "from ret051"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    idProduto = rst.getInt("PRODCod");

                    oProduto.setId(idProduto);
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoComImposto(rst.getDouble("prodcustofinal"));
                    oComplemento.setCustoSemImposto(rst.getDouble("PRODCusto"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Loja " + idLojaVR + "...");
            vProdutoNovo = carregarCustoProduto(idLojaVR);
            new ProdutoDAO().alterarCustoProduto(vProdutoNovo, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idProduto;
        double precoVenda;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", "
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\" "
                    + "from RET051"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    idProduto = rst.getInt("PRODCod");
                    precoVenda = rst.getDouble("PRODVenda");

                    oProduto.setId(idProduto);
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(precoVenda);
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Loja " + idLojaVR);
            vProduto = carregarPrecoProduto(idLojaVR);
            new ProdutoDAO().alterarPrecoProduto(vProduto, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.prodsdo\n"
                    + "from RET051\n"
                    + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0 "
                    + "and ret051.prodsdo <> 0"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oProduto.setId(rst.getInt("PRODCod"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("prodsdo"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(int idLojaVR, boolean somarEstoque) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(idLojaVR);
            if (!vResult.isEmpty()) {

                if (somarEstoque) {
                    new ProdutoDAO().alterarEstoqueProdutoSomando(vResult, idLojaVR);
                } else {
                    new ProdutoDAO().alterarEstoqueProduto(vResult, idLojaVR);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarras() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int id_produto;
        long codigobarras;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret052.\"BARCod\", ret052.\"PRODCod\" "
                    + "from ret052"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    id_produto = rst.getInt("PRODCod");

                    if ((rst.getString("BARCod") != null)
                            && (!rst.getString("BARCod").trim().isEmpty())) {

                        if (rst.getString("BARCod").trim().length() > 14) {
                            codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("BARCod").trim().substring(0, 14)));
                        } else {
                            codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("BARCod").trim()));
                        }

                        if (codigobarras > 999999) {
                            oProduto.setId(id_produto);
                            oAutomacao.setCodigoBarras(codigobarras);
                            oProduto.vAutomacao.add(oAutomacao);
                            vResult.add(oProduto);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarras() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Código de Barras...");
            vResult = carregarCodigoBarras();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().addCodigoBarras(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;

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

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int id_fornecedor = 0, id_municipio, id_estado, id_tipoinscricao,
                id_situacaocadastro, id_banco = 0;
        String razaoSocial, nomeFantasia, ie, endereco, bairro, strEstado,
                telefone, numero, complemento, agencia, conta, observacao,
                contato, email, fax, telefone2,
                representante, telefone3, telefone4, email2;
        long cnpj, cep;
        java.sql.Date datacadastro;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            stmPG = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select ret007.\"FORCod\", ret007.\"FORRazao\", ret007.\"FORFant\", ret007.\"FOREnd\", \n"
                    + "ret007.\"FORBairro\",ret007.\"FORCep\", ret501.cidibge, ret501.\"CIDNome\", ret501.ciduf, \n"
                    + "ret007.fornumero,ret007.forcomplemento, ret007.forcnpj, ret007.forie, ret007.forcpf, \n"
                    + "ret007.forativo,ret007.\"FORFone1\", ret007.\"FORFone2\", ret007.\"FORFax\", ret007.\"FORContato\", \n"
                    + "ret007.\"FORBco\", ret007.\"FORAg\", ret007.\"FORCta\", ret007.\"FOREmail\", ret007.forobs, \n"
                    + "ret007.forinclusao, ret007.\"FORRep\", ret007.\"FORRepF1\", ret007.\"FORRepF2\", ret007.forrepemail\n"
                    + "from RET007\n"
                    + "inner join RET501 on RET501.\"CIDCod\" = ret007.\"CIDCod\" ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                id_fornecedor = Integer.parseInt(rst.getString("FORCod").trim());

                if ((rst.getString("FORRazao") != null)
                        && (!"".equals(rst.getString("FORRazao").trim()))) {
                    razaoSocial = Utils.acertarTexto(rst.getString("FORRazao").trim().replace("'", ""));
                } else {
                    razaoSocial = "";
                }

                if ((rst.getString("FORFant") != null)
                        && (!"".equals(rst.getString("FORFant").trim()))) {
                    nomeFantasia = Utils.acertarTexto(rst.getString("FORFant").trim().replace("'", ""));
                } else {
                    nomeFantasia = "";
                }

                if ((rst.getString("FOREnd") != null)
                        && (!"".equals(rst.getString("FOREnd").trim()))) {
                    endereco = Utils.acertarTexto(rst.getString("FOREnd").trim().replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("FORBairro") != null)
                        && (!"".equals(rst.getString("FORBairro").trim()))) {
                    bairro = Utils.acertarTexto(rst.getString("FORBairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }

                Integer idMunicipio = rst.getInt("cidibge");
                if (!rst.wasNull()) {
                    id_municipio = Utils.retornarMunicipioIBGECodigo(idMunicipio);
                    if (id_municipio == 0) {
                        id_municipio = 3526902;
                    }
                } else {
                    if ((rst.getString("CIDNome") != null)
                            && (!"".equals(rst.getString("CIDNome").trim()))) {
                        if ((rst.getString("ciduf") != null)
                                && (!"".equals(rst.getString("ciduf").trim()))) {
                            strEstado = Utils.acertarTexto(rst.getString("ciduf").trim().replace("'", ""));
                        } else {
                            strEstado = "SP";
                        }
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("CIDNome")),
                                strEstado);
                        if (id_municipio == 0) {
                            id_municipio = Global.idMunicipio;
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }
                }

                if ((rst.getString("FORCep") != null)
                        && (!"".equals(rst.getString("FORCep").trim()))) {
                    cep = Long.parseLong(rst.getString("FORCep").trim().replace("'", ""));
                } else {
                    cep = Global.Cep;
                }

                if ((rst.getString("ciduf") != null)
                        && (!"".equals(rst.getString("ciduf").trim()))) {
                    id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ciduf").trim().replace("'", "")));
                    if (id_estado == 0) {
                        id_estado = Global.idEstado;
                    }
                } else {
                    id_estado = Global.idEstado;
                }

                if ((rst.getString("FORFone1") != null)
                        && (!"".equals(rst.getString("FORFone1").trim()))) {
                    telefone = Utils.formataNumero(rst.getString("FORFone1").trim().replace("'", ""));
                } else {
                    telefone = "0000000000";
                }

                if ((rst.getString("forcnpj") != null)
                        && (!"".equals(rst.getString("forcnpj").trim()))) {
                    if ("00000000000000".equals(rst.getString("forcnpj").trim())) {
                        cnpj = -1;
                    } else {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("forcnpj").trim()));
                    }

                    id_tipoinscricao = 0;
                } else if ((rst.getString("forcpf") != null)
                        && (!"".equals(rst.getString("forcpf").trim()))) {
                    if ("00000000000".equals(rst.getString("forcpf").trim())) {
                        cnpj = -1;
                    } else {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("forcpf").trim()));
                    }
                    id_tipoinscricao = 1;
                } else {
                    cnpj = Long.parseLong(String.valueOf(id_fornecedor));
                    id_tipoinscricao = 0;
                }

                if ((rst.getString("forie") != null)
                        && (!"".equals(rst.getString("forie").trim()))) {
                    ie = Utils.acertarTexto(rst.getString("forie").trim().replace("'", ""));
                    ie = ie.replace(".", "");
                    ie = ie.replace("-", "");
                    ie = ie.replace(",", "");
                    ie = ie.replace("/", "");
                    ie = ie.replace("\"", "");
                } else {
                    ie = "ISENTO";
                }

                id_situacaocadastro = 1;

                if ((rst.getString("fornumero") != null)
                        && (!"".equals(rst.getString("fornumero").trim()))
                        && (!"S/N".equals(rst.getString("fornumero").trim()))) {
                    numero = Utils.acertarTexto(rst.getString("fornumero").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("forcomplemento") != null)
                        && (!"".equals(rst.getString("forcomplemento").trim()))) {
                    complemento = Utils.acertarTexto(rst.getString("forcomplemento").trim().replace("'", ""));
                } else {
                    complemento = "";
                }

                if ((rst.getString("forinclusao") != null)
                        && (!rst.getString("forinclusao").trim().isEmpty())) {
                    datacadastro = new java.sql.Date(rst.getDate("forinclusao").getTime());
                } else {
                    datacadastro = new java.sql.Date(new java.util.Date().getTime());
                }

                if ((rst.getString("FORBco") != null)
                        && (!"".equals(rst.getString("FORBco").trim()))) {
                    if (Utils.encontrouLetraCampoNumerico(rst.getString("FORBco").trim())) {
                        sql = new StringBuilder();
                        sql.append("select id from banco ");
                        sql.append("where descricao like '%" + rst.getString("FORBco") + "%'");
                        rstPG = stmPG.executeQuery(sql.toString());
                        if (rstPG.next()) {
                            id_banco = rstPG.getInt("id");
                        } else {
                            id_banco = 804;
                        }
                    } else {
                        sql = new StringBuilder();
                        sql.append("select id from banco ");
                        sql.append("where id = " + rst.getString("FORBco"));
                        rstPG = stmPG.executeQuery(sql.toString());
                        if (rstPG.next()) {
                            id_banco = rstPG.getInt("id");
                        } else {
                            id_banco = 804;
                        }
                    }
                }

                if ((rst.getString("FORAg") != null)
                        && (!"".equals(rst.getString("FORAg").trim()))) {
                    agencia = Utils.acertarTexto(rst.getString("FORAg").trim().replace("'", ""));
                } else {
                    agencia = "";
                }

                if ((rst.getString("FORCta") != null)
                        && (!"".equals(rst.getString("FORCta").trim()))) {
                    conta = Utils.acertarTexto(rst.getString("FORCta").trim().replace("'", ""));
                } else {
                    conta = "";
                }

                if ((rst.getString("forobs") != null)
                        && (!"".equals(rst.getString("forobs").trim()))) {
                    observacao = Utils.acertarTexto(rst.getString("forobs").trim().replace("'", ""));
                } else {
                    observacao = "";
                }

                if ((rst.getString("FORContato") != null)
                        && (!"".equals(rst.getString("FORContato").trim()))) {
                    contato = Utils.acertarTexto(rst.getString("FORContato").trim().replace("'", ""));
                } else {
                    contato = "";
                }

                /**
                 * ***** contatos do fornecedor
                 */
                if ((rst.getString("FOREmail") != null)
                        && (!"".equals(rst.getString("FOREmail").trim()))
                        && (rst.getString("FOREmail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("FOREmail").trim().replace("'", ""));
                } else {
                    email = "";
                }

                if ((rst.getString("FORFax") != null)
                        && (!"".equals(rst.getString("FORFax").trim()))) {
                    fax = Utils.formataNumero(rst.getString("FORFax").trim().replace("'", ""));
                } else {
                    fax = "";
                }

                if ((rst.getString("FORFone2") != null)
                        && (!"".equals(rst.getString("FORFone2").trim()))) {
                    telefone2 = Utils.formataNumero(rst.getString("FORFone2").trim().replace("'", ""));
                } else {
                    telefone2 = "";
                }

                if ((rst.getString("FORRep") != null)
                        && (!rst.getString("FORRep").trim().isEmpty())) {
                    representante = Utils.acertarTexto(rst.getString("FORRep").trim());
                } else {
                    representante = "";
                }

                if ((rst.getString("FORRepF1") != null)
                        && (!rst.getString("FORRepF1").trim().isEmpty())) {
                    telefone3 = Utils.formataNumero(rst.getString("FORRepF1").trim());
                } else {
                    telefone3 = "";
                }

                if ((rst.getString("FORRepF2") != null)
                        && (!rst.getString("FORRepF2").trim().isEmpty())) {
                    telefone4 = Utils.formataNumero(rst.getString("FORRepF2").trim());
                } else {
                    telefone4 = "";
                }

                if ((rst.getString("forrepemail") != null)
                        && (!rst.getString("forrepemail").trim().isEmpty())
                        && (rst.getString("forrepemail").contains("@"))) {
                    email2 = Utils.acertarTexto(rst.getString("forrepemail").trim());
                } else {
                    email2 = "";
                }

                /**
                 * ***
                 */
                if (observacao.isEmpty()) {

                    observacao = contato;
                } else {

                    observacao = observacao + ", " + contato;
                }

                if (agencia.length() > 6) {
                    agencia = agencia.substring(0, 6);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                if (razaoSocial.length() > 40) {
                    razaoSocial = razaoSocial.substring(0, 40);
                }

                if (nomeFantasia.length() > 30) {
                    nomeFantasia = nomeFantasia.substring(0, 30);
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

                if (telefone.length() > 14) {
                    telefone = telefone.substring(0, 14);
                }

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                }

                if (ie.length() > 20) {
                    ie = ie.substring(0, 20);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                if (agencia.length() > 6) {
                    agencia = agencia.substring(0, 6);
                }

                if (conta.length() > 12) {
                    conta = conta.substring(0, 12);
                }

                if (representante.length() > 30) {
                    representante = representante.substring(0, 30);
                }

                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }

                if (telefone4.length() > 14) {
                    telefone4 = telefone4.substring(0, 14);
                }

                if (email2.length() > 50) {
                    email2 = email2.substring(0, 50);
                }

                FornecedorVO oFornecedor = new FornecedorVO();
                oFornecedor.setCodigoanterior(id_fornecedor);
                oFornecedor.setDatacadastro(datacadastro);
                oFornecedor.setRazaosocial(razaoSocial);
                oFornecedor.setNomefantasia(nomeFantasia);
                oFornecedor.setEndereco(endereco);
                oFornecedor.setBairro(bairro);
                oFornecedor.setNumero(numero);
                oFornecedor.id_municipio = id_municipio;
                oFornecedor.cep = cep;
                oFornecedor.id_estado = id_estado;
                oFornecedor.id_tipoinscricao = id_tipoinscricao;
                oFornecedor.inscricaoestadual = ie;
                oFornecedor.cnpj = cnpj;
                oFornecedor.id_situacaocadastro = id_situacaocadastro;
                oFornecedor.observacao = observacao;
                oFornecedor.complemento = complemento;
                oFornecedor.telefone = telefone;
                oFornecedor.email = email;
                oFornecedor.fax = fax;
                oFornecedor.telefone2 = telefone2;
                oFornecedor.id_banco = id_banco;
                oFornecedor.telefone3 = telefone3;
                oFornecedor.telefone4 = telefone4;
                oFornecedor.email2 = email2;
                oFornecedor.representante = representante;
                vFornecedor.add(oFornecedor);
            }

            return vFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vFornecedor = carregarFornecedor();
            new FornecedorDAO().salvar(vFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        String codigoExterno;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret154.forcod, ret154.prodcod,\n"
                    + "ret154.prodbarcod, ret154.codfabricante\n"
                    + "from RET154;"
            )) {
                while (rst.next()) {

                    if ((rst.getString("codfabricante") != null)
                            && (!rst.getString("codfabricante").trim().isEmpty())) {
                        codigoExterno = Utils.acertarTexto(rst.getString("codfabricante").trim());
                    } else {
                        codigoExterno = "";
                    }

                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_fornecedor(Integer.parseInt(Utils.formataNumero(rst.getString("forcod"))));
                    vo.setId_produto(Integer.parseInt(Utils.formataNumero(rst.getString("prodcod"))));
                    vo.setCodigoexterno(codigoExterno);
                    vo.setDataalteracao(new Date(new java.util.Date().getTime()));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
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

    private List<ClientePreferencialVO> carregarClientePreferencial(boolean somenteAtivos) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret028.\"CLICod\", ret028.\"CLINome\", ret028.\"CLIFantasia\", ret028.\"CLIContato\",\n"
                    + "ret028.\"CLIEnd\", ret028.\"CLIBairro\", ret028.\"CLICep\", ret501.cidibge, ret501.\"CIDNome\",\n"
                    + "ret501.ciduf, ret028.\"CLIFone1\", ret028.\"CLIFone2\", ret028.\"CLIFax\", ret028.clicpf,\n"
                    + "ret028.clirg, ret028.clicnpj, ret028.cliie, ret028.\"CLIInclusao\", ret028.\"CLICadastro\",\n"
                    + "ret028.\"CLIEmail\", ret028.\"CLINasc\", ret028.clinumero, ret028.clicomplemento, ret028.\"CLICred\", \n"
                    + "ret028.\"CLIEstCIV\", ret028.clisexo, ret028.\"CLIPai\", ret028.\"CLIMae\", ret028.clicj,\n"
                    + "ret028.clicjcpf, ret028.clicjrg, ret028.\"CLICJNasc\", ret028.\"CLIObs\", ret028.\"CLIBco1\",\n"
                    + "ret028.\"CLIAg1\", ret028.\"CLICta1\", ret028.\"CLILIMCred\", ret028.\"CLICPTrab\", ret028.\"CLITrab\",\n"
                    + "ret028.\"CLICPRenda\", ret028.\"CLITrabFone\", ret028.cliativo\n"
                    + "from ret028\n"
                    + "left join RET501 on RET501.\"CIDCod\"  = ret028.\"CIDCod\" "
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setId(rst.getInt("CLICod"));
                    vo.setCodigoanterior(vo.getId());
                    vo.setNome((rst.getString("CLINome") == null ? "" : rst.getString("CLINome").trim()));
                    vo.setEndereco((rst.getString("CLIEnd") == null ? "" : rst.getString("CLIEnd").trim()));
                    vo.setBairro((rst.getString("CLIBairro") == null ? "" : rst.getString("CLIBairro").trim()));
                    vo.setComplemento((rst.getString("clicomplemento") == null ? "" : rst.getString("clicomplemento").trim()));

                    if ((rst.getString("clinumero") != null)
                            && (!rst.getString("clinumero").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("clinumero").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("CLICep") != null)
                            && (!rst.getString("CLICep").trim().isEmpty())) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CLICep").trim())));
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    if ((rst.getString("cidibge") != null)
                            && (!rst.getString("cidibge").trim().isEmpty())) {

                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(Integer.parseInt(Utils.formataNumero(rst.getString("cidibge").trim())))
                                == 0 ? Parametros.get().getMunicipioPadrao2().getId()
                                        : Utils.retornarMunicipioIBGECodigo(Integer.parseInt(Utils.formataNumero(rst.getString("cidibge").trim())))));

                        if ((rst.getString("ciduf") != null)
                                && (!rst.getString("ciduf").trim().isEmpty())) {
                            vo.setId_estado((Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ciduf").trim()))
                                    == 0 ? Parametros.get().getUfPadrao().getId()
                                            : Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ciduf").trim()))));
                        } else {
                            vo.setId_estado(Parametros.get().getUfPadrao().getId());
                        }
                    } else if ((rst.getString("CIDNome") != null)
                            && (!rst.getString("CIDNome").trim().isEmpty())
                            && (rst.getString("ciduf") != null)
                            && (!rst.getString("ciduf").trim().isEmpty())) {

                        vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("CIDNome").trim()), Utils.acertarTexto(rst.getString("ciduf")))
                                == 0 ? Parametros.get().getMunicipioPadrao2().getId()
                                        : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto("CIDNome").trim(), Utils.acertarTexto(rst.getString("ciduf").trim()))));

                        vo.setId_estado((Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ciduf").trim()))
                                == 0 ? Parametros.get().getUfPadrao().getId()
                                        : Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ciduf").trim()))));
                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        vo.setId_estado(Parametros.get().getUfPadrao().getId());
                    }

                    if ((rst.getString("CLIFone1") != null)
                            && (!rst.getString("CLIFone1").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("CLIFone1").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("CLIFone2") != null)
                            && (!rst.getString("CLIFone2").trim().isEmpty())) {
                        vo.setTelefone2(Utils.formataNumero(rst.getString("CLIFone2").trim()));
                    } else {
                        vo.setTelefone2("");
                    }

                    if ((rst.getString("CLIFax") != null)
                            && (!rst.getString("CLIFax").trim().isEmpty())) {
                        vo.setFax(Utils.formataNumero(rst.getString("CLIFax").trim()));
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("clicpf") != null)
                            && (!rst.getString("clicpf").trim().isEmpty())) {

                        vo.setId_tipoinscricao(1);
                        if (rst.getString("clicpf").trim().length() >= 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("clicpf").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                    } else if ((rst.getString("clicnpj") != null)
                            && (!rst.getString("clicnpj").trim().isEmpty())) {

                        vo.setId_tipoinscricao(0);
                        if (rst.getString("clicnpj").trim().length() > 11) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("clicnpj").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                    } else {
                        vo.setCnpj(-1);
                        vo.setId_tipoinscricao(0);
                    }

                    if ((rst.getString("clirg") != null)
                            && (!rst.getString("clirg").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("clirg").trim()));
                    } else if ((rst.getString("cliie") != null)
                            && (!rst.getString("cliie").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("cliie").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("CLICadastro") != null)
                            && (!rst.getString("CLICadastro").trim().isEmpty())) {
                        vo.setDatacadastro(rst.getString("CLICadastro").replace(".", "/"));
                    } else {
                        vo.setDatacadastro("");
                    }

                    if ((rst.getString("CLIEmail") != null)
                            && (!rst.getString("CLIEmail").trim().isEmpty())) {
                        vo.setEmail(Utils.acertarTexto(rst.getString("CLIEmail").trim().toLowerCase()));
                    } else {
                        vo.setEmail("");
                    }

                    if ((rst.getString("CLINasc") != null)
                            && (!rst.getString("CLINasc").trim().isEmpty())) {
                        vo.setDatanascimento(rst.getString("CLINasc").trim().replace(".", "/"));
                    } else {
                        vo.setDatanascimento("");
                    }

                    if ((rst.getString("CLIEstCIV") != null)
                            && (!rst.getString("CLIEstCIV").trim().isEmpty())) {
                        if (null != rst.getString("CLIEstCIV").trim()) {
                            switch (rst.getString("CLIEstCIV").trim()) {
                                case "O":
                                    vo.setId_tipoestadocivil(5);
                                    break;
                                case "C":
                                    vo.setId_tipoestadocivil(2);
                                    break;
                                case "S":
                                    vo.setId_tipoestadocivil(1);
                                    break;
                                case "V":
                                    vo.setId_tipoestadocivil(3);
                                    break;
                                case "D":
                                    vo.setId_tipoestadocivil(6);
                                    break;
                                default:
                                    vo.setId_tipoestadocivil(0);
                                    break;
                            }
                        }
                    } else {
                        vo.setId_tipoestadocivil(0);
                    }

                    if ((rst.getString("clisexo") != null)
                            && (!rst.getString("clisexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("clisexo").trim())) {
                            vo.setSexo(0);
                        } else {
                            vo.setSexo(1);
                        }
                    } else {
                        vo.setSexo(1);
                    }

                    vo.setNomepai((rst.getString("CLIPai") == null ? "" : rst.getString("CLIPai").trim()));
                    vo.setNomemae((rst.getString("CLIMae") == null ? "" : rst.getString("CLIMae").trim()));
                    vo.setNomeconjuge((rst.getString("clicj") == null ? "" : rst.getString("clicj").trim()));

                    if ((rst.getString("clicjcpf") != null)
                            && (!rst.getString("clicjcpf").trim().isEmpty())) {
                        vo.setCpfconjuge(Double.parseDouble(Utils.formataNumero(rst.getString("clicjcpf").trim())));
                    } else {
                        vo.setCpfconjuge(-1);
                    }

                    if ((rst.getString("clicjrg") != null)
                            && (!rst.getString("clicjrg").trim().isEmpty())) {
                        vo.setRgconjuge(Utils.acertarTexto(rst.getString("clicjrg").trim()));
                    } else {
                        vo.setRgconjuge("");
                    }

                    if ((rst.getString("CLICJNasc") != null)
                            && (!rst.getString("CLICJNasc").trim().isEmpty())) {
                        vo.setDatanascimentoconjuge(rst.getString("CLICJNasc").trim().replace(".", "/"));
                    } else {
                        vo.setDatanascimentoconjuge("");
                    }

                    vo.setObservacao((rst.getString("CLIObs") == null ? "" : rst.getString("CLIObs").trim()));

                    if ((rst.getString("CLIBco1") != null)
                            && (!rst.getString("CLIBco1").trim().isEmpty())) {
                        vo.setId_banco(new BancoDAO().getId(Integer.parseInt(Utils.formataNumero(rst.getString("CLIBco1").trim()))));
                    } else {
                        vo.setId_banco(804);
                    }

                    if ((rst.getString("CLIAg1") != null)
                            && (!rst.getString("CLIAg1").trim().isEmpty())) {
                        vo.setAgencia(Utils.acertarTexto(rst.getString("CLIAg1").trim()));
                    } else {
                        vo.setAgencia("");
                    }

                    if ((rst.getString("CLICta1") != null)
                            && (!rst.getString("CLICta1").trim().isEmpty())) {
                        vo.setConta(Utils.acertarTexto(rst.getString("CLICta1").trim()));
                    } else {
                        vo.setConta("");
                    }

                    vo.setValorlimite(rst.getDouble("CLILIMCred"));
                    vo.setEmpresa((rst.getString("CLITrab") == null ? "" : rst.getString("CLITrab").trim()));
                    vo.setSalario(rst.getDouble("CLICPRenda"));

                    if ((rst.getString("CLITrabFone") != null)
                            && (!rst.getString("CLITrabFone").trim().isEmpty())) {
                        vo.setTelefoneempresa(Utils.formataNumero(rst.getString("CLITrabFone").trim()));
                    } else {
                        vo.setTelefoneempresa("");
                    }

                    //if ((rst.getString("cliativo") != null)
                    //        && (!rst.getString("cliativo").trim().isEmpty())) {
                    //    if ("S".equals(rst.getString("cliativo").trim())) {
                    vo.setId_situacaocadastro(1);
                    //    } else {
                    //        vo.setId_situacaocadastro(0);
                    //    }
                    //} else {
                    //    vo.setId_situacaocadastro(0);
                    //}

                    if ((rst.getString("CLICred") != null)
                            && (!rst.getString("CLICred").trim().isEmpty())) {
                        if ("S".equals(rst.getString("CLICred").trim())) {
                            vo.setBloqueado(false);
                            vo.setPermitecreditorotativo(true);
                            vo.setPermitecheque(true);
                        } else {
                            vo.setBloqueado(true);
                            vo.setPermitecreditorotativo(false);
                            vo.setPermitecheque(false);
                        }
                    } else {
                        vo.setBloqueado(true);
                        vo.setPermitecreditorotativo(false);
                        vo.setPermitecheque(false);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarClientePreferencial(int idLojaVR, boolean somenteAtivos) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vResult = carregarClientePreferencial(somenteAtivos);
            if (!vResult.isEmpty()) {
                new PlanoDAO().salvar(idLojaVR);
                new ClientePreferencialDAO().salvar(vResult, idLojaVR, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret010.\"CLICod\", ret010.\"CCTCupom\", ret010.cctecf, ret010.\"CCTData\",\n"
                    + "ret010.cctvcto,ret010.\"CCTDebito\", ret010.cctobs, ret010.\"CCTPgto\"\n"
                    + "from ret010\n"
                    + "where ret010.\"CCTPG\" = 'N'"
                    //+ "where ret010.\"CCTPago\" < ret010.\"CCTDebito\""
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    vo.setId_clientepreferencial(rst.getInt("CLICod"));
                    vo.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("CCTCupom"))));
                    vo.setEcf(Integer.parseInt(Utils.formataNumero(rst.getString("cctecf"))));
                    vo.setDataemissao(rst.getString("CCTData").trim());
                    vo.setDatavencimento(rst.getString("cctvcto"));
                    vo.setValor(rst.getDouble("CCTDebito"));
                    vo.setObservacao("IMPORTADO VR");
                    vo.setId_loja(idLojaVR);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarReceberCreditoRotativo(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente Loja " + idLojaVR + "...");
            vResult = carregarReceberCreditoRotativo(idLojaVR);
            if (!vResult.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private boolean verificaExisteMercadologico(int mercad1, int mercad2, int mercad3)
            throws SQLException, Exception {

        boolean retorno = true;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select * from mercadologico ");
        sql.append("where mercadologico1 = " + mercad1 + " ");
        sql.append("and mercadologico2 = " + mercad2 + " ");
        sql.append("and mercadologico3 = " + mercad3 + " ");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            retorno = false;
        }

        return retorno;
    }

    public int retornarAliquota(int aliquota) {
        int retorno;

        if (aliquota == 0) {
            retorno = 0;
        } else if (aliquota == 1) {
            retorno = 2;
        } else if (aliquota == 2) {
            retorno = 0;
        } else if (aliquota == 3) {
            retorno = 1;
        } else if (aliquota == 4) {
            retorno = 3;
        } else if (aliquota == 5) {
            retorno = 7;
        } else if (aliquota == 6) {
            retorno = 6;
        } else if (aliquota == 7) {
            retorno = 17;
        } else if (aliquota == 9) {
            retorno = 10;
        } else {
            retorno = 8;
        }

        return retorno;
    }

    /*public Map<Integer, ProdutoVO> carregarProduto(int id_loja, int id_lojaDestino) throws SQLException, Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int progresso, id_produto, id_tipoembalagem, validade, codigobalanca,
                mercadologico1, mercadologico2, mercadologico3, ncm1, ncm2, ncm3,
                cst_pisdebito, cst_piscredito, id_tipopiscofinsdebito,
                id_tipopiscofinscredito, qtdembalagem, naturezareceita, id_familiaproduto,
                id_situacaocadastro, aliquota, cst_pisdebitoAux, cst_piscreditoAux,
                mercadMax;
        boolean pesavel, ebalanca;
        double margem, precovenda, estoque, custo;
        String descricaocompleta, descricaoreduzida, descricaogondola, ncmAtual, datacadastro;
        long codigobarras;
        Utils util = new Utils();

        stm = ConexaoFirebird.getConexao().createStatement();
        stmPG = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT MAX(MERCADOLOGICO1) as MERCADOLOGICO1 FROM MERCADOLOGICO ");
        rstPG = stmPG.executeQuery(sql.toString());
        if (rstPG.next()) {
            Global.mercadologicoPadrao1 = rstPG.getInt("MERCADOLOGICO1");
            Global.mercadologicoPadrao2 = 1;
            Global.mercadologicoPadrao3 = 1;
        }

        try {
            sql = new StringBuilder();
            sql.append("select ret051.\"PRODCod\", ret051.\"PRODNome\", ret051.\"PRODNomeRed\", ");
            sql.append("ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\", ");
            sql.append("ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\", ");
            sql.append("ret051.\"SUBGCod\", ret051.\"ALIQCod\", ret051.prodai, ");
            sql.append("ret051.\"PRODBARCod\", ret051.clasfisccod, ret051.ncm, ");
            sql.append("ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\", ret051.prodsdo, ");
            sql.append("prodqtemb from RET051 ");
            sql.append("left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\" ");
            sql.append("where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0 ");
            sql.append("order by ret053.\"PRODCod\" ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();
                sql = new StringBuilder();
                sql.append("select codigo, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + Integer.parseInt(rst.getString("PRODCod").trim()));

                rstPG = stmPG.executeQuery(sql.toString());

                if (rstPG.next()) {

                    if ("P".equals(rstPG.getString("pesavel"))) {

                        id_tipoembalagem = 4;
                        pesavel = false;

                    } else {

                        id_tipoembalagem = 0;
                        pesavel = true;
                    }

                    ebalanca = true;
                    codigobalanca = rstPG.getInt("codigo");
                    validade = rstPG.getInt("validade");

                } else {

                    ebalanca = false;
                    id_tipoembalagem = 0;
                    pesavel = false;
                    codigobalanca = 0;
                    validade = 0;
                }

                id_produto = rst.getInt("PRODCod");

                if ((rst.getString("PRODNome") != null)
                        && (!rst.getString("PRODNome").trim().isEmpty())) {

                    descricaocompleta = util.acertarTexto(rst.getString("PRODNome").trim().replace("'", ""));
                } else {

                    descricaocompleta = "";
                }

                if ((rst.getString("PRODNomeRed") != null)
                        && (!rst.getString("PRODNomeRed").trim().isEmpty())) {

                    descricaoreduzida = util.acertarTexto(rst.getString("PRODNomeRed").trim().replace("'", ""));
                } else {

                    descricaoreduzida = "";
                }

                if ((rst.getString("PRODEtq") != null)
                        && (!rst.getString("PRODEtq").trim().isEmpty())) {

                    descricaogondola = util.acertarTexto(rst.getString("PRODEtq").trim().replace("'", ""));
                } else {

                    descricaogondola = "";
                }

                Integer idMercadologico1 = rst.getInt("GRUCod");

                if (!rst.wasNull()) {

                    mercadologico1 = idMercadologico1;
                } else {

                    mercadologico1 = 0;
                }

                Integer idMercadologico2 = rst.getInt("SUBGCod");

                if (!rst.wasNull()) {

                    mercadologico2 = idMercadologico2;
                    mercadologico3 = 1;
                } else {
                    mercadologico2 = 0;
                    mercadologico3 = 0;
                }

                if (!Utils.verificaExisteMercadologico4Nivel(mercadologico1, mercadologico2, mercadologico3, 0)) {
                    mercadologico1 = Global.mercadologicoPadrao1;
                    mercadologico2 = Global.mercadologicoPadrao2;
                    mercadologico3 = Global.mercadologicoPadrao3;
                }

                if (rst.getString("PRODCADAST") != null) {
                    datacadastro = Util.formatDataGUI(rst.getDate("PRODCADAST"));
                } else {
                    datacadastro = "";
                }

                if ((rst.getString("prodstcofins") != null)
                        && (!"".equals(rst.getString("prodstcofins").trim()))) {

                    cst_pisdebito = Integer.parseInt(rst.getString("prodstcofins").trim());
                    cst_pisdebitoAux = cst_pisdebito;
                    id_tipopiscofinsdebito = util.retornarPisCofinsDebito(cst_pisdebito);
                } else {

                    cst_pisdebitoAux = -1;
                    id_tipopiscofinsdebito = 1;
                }

                if ((rst.getString("prodstcofinsent") != null)
                        && (!"".equals(rst.getString("prodstcofinsent").trim()))) {

                    cst_piscredito = Integer.parseInt(rst.getString("prodstcofinsent").trim());
                    cst_piscreditoAux = cst_piscredito;
                    id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_piscredito);
                } else {

                    cst_piscreditoAux = -1;
                    id_tipopiscofinscredito = 13;
                }

                if ((rst.getString("CODCEST") != null)
                        && (!rst.getString("CODCEST").trim().isEmpty())) {
                    idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                } else {
                    idCest = -1;
                }
                
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

                Integer idQtdEmbalagem = rst.getInt("prodqtemb");

                if (!rst.wasNull()) {

                    if (idQtdEmbalagem == 0) {

                        qtdembalagem = 1;
                    } else {

                        qtdembalagem = idQtdEmbalagem;
                    }
                } else {
                    qtdembalagem = 1;

                }

                if (id_tipoembalagem == 4) {

                    qtdembalagem = 1;
                }

                naturezareceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito, "");

                Double idMargem = rst.getDouble("PRODMargem");

                if (!rst.wasNull()) {

                    margem = idMargem;
                } else {

                    margem = 0;
                }

                Integer idFamiliaProduto = rst.getInt("SUBCod");

                if (!rst.wasNull()) {

                    sql = new StringBuilder();
                    sql.append("select * from familiaproduto ");
                    sql.append("where id = " + idFamiliaProduto);

                    rstPG = stmPG.executeQuery(sql.toString());

                    if (rstPG.next()) {

                        id_familiaproduto = idFamiliaProduto;
                    } else {

                        id_familiaproduto = -1;
                    }
                } else {

                    id_familiaproduto = -1;
                }

                Double idCusto = rst.getDouble("PRODCusto");

                if (!rst.wasNull()) {

                    custo = idCusto;
                } else {

                    custo = 0;
                }

                Double idPrecovenda = rst.getDouble("PRODVenda");

                if (!rst.wasNull()) {

                    precovenda = idPrecovenda;
                } else {

                    precovenda = 0;
                }

                Double idEstoque = rst.getDouble("prodsdo");

                if (!rst.wasNull()) {

                    estoque = idEstoque;
                } else {

                    estoque = 0;
                }

                if ((rst.getString("prodai") != null)
                        && (!"".equals(rst.getString("prodai").trim()))) {

                    if ("A".equals(rst.getString("prodai"))) {

                        id_situacaocadastro = 1;
                    } else {

                        id_situacaocadastro = 0;
                    }
                } else {

                    id_situacaocadastro = 0;
                }

                if ((rst.getString("ALIQCod") != null)
                        && (!"".equals(rst.getString("ALIQCod").trim()))) {

                    aliquota = Integer.parseInt(rst.getString("ALIQCod").trim());
                } else {

                    aliquota = 999;
                }

                if (ebalanca) {

                    codigobarras = Long.parseLong(String.valueOf(id_produto));
                } else {

                    if ((rst.getString("PRODBARCod") != null)
                            && (!"".equals(rst.getString("PRODBARCod")))) {

                        codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim()));

                        if ((String.valueOf(codigobarras).length() < 7)) {

                            codigobarras = -1;
                        } else if ((String.valueOf(codigobarras).length() > 14)) {

                            codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                        }
                    } else {

                        codigobarras = -1;
                    }
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

                oProduto.id = id_produto;
                oProduto.dataCadastro = datacadastro;
                oProduto.descricaoCompleta = descricaocompleta;
                oProduto.descricaoReduzida = descricaoreduzida;
                oProduto.descricaoGondola = descricaogondola;
                oProduto.idTipoEmbalagem = id_tipoembalagem;
                oProduto.qtdEmbalagem = qtdembalagem;
                oProduto.idTipoPisCofinsDebito = id_tipopiscofinsdebito;
                oProduto.idTipoPisCofinsCredito = id_tipopiscofinscredito;
                oProduto.tipoNaturezaReceita = naturezareceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = id_familiaproduto;
                oProduto.idFornecedorFabricante = 1;
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
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.margem = margem;
                oProduto.validade = validade;
                oProduto.setIdCest(idCest);

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.custoSemImposto = custo;
                oComplemento.custoComImposto = custo;
                oComplemento.precoVenda = precovenda;
                oComplemento.precoDiaSeguinte = precovenda;
                oComplemento.idSituacaoCadastro = id_situacaocadastro;
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = 35;
                oAliquota.idAliquotaDebito = aliquota;
                oAliquota.idAliquotaCredito = aliquota;
                oAliquota.idAliquotaDebitoForaEstado = aliquota;
                oAliquota.idAliquotaCreditoForaEstado = aliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = aliquota;
                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.idTipoEmbalagem = id_tipoembalagem;
                oAutomacao.qtdEmbalagem = qtdembalagem;
                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oAnterior.codigoanterior = id_produto;

                if ((rst.getString("PRODBARCod") != null)
                        && (!"".equals(rst.getString("PRODBARCod").trim()))) {

                    oAnterior.barras = Long.parseLong(rst.getString("PRODBARCod").trim());
                } else {

                    oAnterior.barras = -1;
                }

                oAnterior.piscofinsdebito = cst_pisdebitoAux;
                oAnterior.piscofinscredito = cst_piscreditoAux;

                if ((rst.getString("ALIQCod") != null)
                        && (!"".equals(rst.getString("ALIQCod").trim()))) {

                    oAnterior.ref_icmsdebito = rst.getString("ALIQCod").trim();
                } else {

                    oAnterior.ref_icmsdebito = "";
                }

                oAnterior.estoque = estoque;
                oAnterior.e_balanca = ebalanca;
                oAnterior.codigobalanca = codigobalanca;
                oAnterior.custosemimposto = custo;
                oAnterior.custocomimposto = custo;
                oAnterior.margem = margem;
                oProduto.vCodigoAnterior.add(oAnterior);

                vProduto.put(id_produto, oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
*/
    private List<ProdutoVO> carregarCest() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int ncm1, ncm2, ncm3;
        String ncmAtual;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ret051.\"PRODCod\",ret041.clasfisccod ncm, ret041.clasfisccest\n"
                    + "from RET051\n"
                    + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod");

            rst = stm.executeQuery(sql.toString());
            CestDAO cestDAO = new CestDAO();

            int contador = 1;
            while (rst.next()) {

                if ((rst.getString("clasfisccest") != null)
                        && (!rst.getString("clasfisccest").trim().isEmpty())
                        && (rst.getString("ncm") != null)
                        && (!rst.getString("ncm").trim().isEmpty()) && (rst.getString("ncm").trim().length() > 5)) {

                    idProduto = Double.parseDouble(rst.getString("PRODCod"));
                    CestVO cest = cestDAO.getCestValido(rst.getString("clasfisccest"));

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

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.idDouble = idProduto;
                    oProduto.cest1 = cest.getCest1();
                    oProduto.cest2 = cest.getCest2();
                    oProduto.cest3 = cest.getCest3();
                    oProduto.ncm1 = ncm1;
                    oProduto.ncm2 = ncm2;
                    oProduto.ncm3 = ncm3;

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                    oAnterior.setNcm(rst.getString("ncm"));
                    oAnterior.setCest(rst.getString("clasfisccest"));

                    oProduto.getvCodigoAnterior().add(oAnterior);

                    vProduto.add(oProduto);

                }
                ProgressBar.setStatus("Carregando dados...Código CEST..." + contador);
                contador++;

            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCEST() throws Exception {
        List<ProdutoVO> vProduto;

        ProgressBar.setStatus("Carregando dados...Código CEST...");
        vProduto = carregarCest();

        if (!vProduto.isEmpty()) {
            new ProdutoDAO().alterarCestProduto(vProduto);
        }
    }

    private List<ProdutoFornecedorVO> carregarAcertarQtdEmbalagem() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"FORCod\", ret051.prodqtemb\n"
                    + "from ret051"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(new ProdutoDAO().getIdProdutoCodigoAnterior(rst.getLong("PRODCod")));
                    vo.setId_fornecedor(new FornecedorDAO().getIdByCodigoAnterior(rst.getLong("FORCod")));
                    vo.setQtdembalagem((int) rst.getDouble("prodqtemb"));
                    vResult.add(vo);

                    ProgressBar.setStatus("Carregando dados...QtdEmbalagem Produto Fornecedor..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarAcertarQtdEmbalagem() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...QtdEmbalagem Produto Fornecedor...");
            vResult = carregarAcertarQtdEmbalagem();
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().acertarQtdEmbalagem(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProdutosRestaurante(int idLojaVR) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        int validade = 0, mercadologico1, mercadologico2, mercadologico3,
                ncm1, ncm2, ncm3, cst_pisdebito, cst_piscredito, id_tipopiscofinsdebito,
                id_tipopiscofinscredito, qtdembalagem, naturezareceita, id_familiaproduto,
                id_situacaocadastro, cst_pisdebitoAux, cst_piscreditoAux, cstSaida = 0, 
                cstEntrada = 0, idCest;
        double margem, precovenda, custo;
        String ncmAtual, datacadastro, codigoAliquotaCga;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODNome\", ret051.\"PRODNomeRed\",\n"
                    + "ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\",\n"
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\",\n"
                    + "ret051.\"SUBGCod\", ret051.prodai, ret051.\"SECCod\",\n"
                    + "ret051.\"PRODBARCod\", ret051.clasfisccod, ret051.ncm,\n"
                    + "ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\",\n"
                    + "ret051.prodsdo, prodqtemb, ret051.\"ALIQCod\", ret051.\"TABBCod\" cstSaida,\n"
                    + "al1.\"ALIQNFPerc\" aliqDebito, al1.\"ALIQRedNF\" redDebito, ret051.aliqcred,\n"
                    + "ret051.tabbcred cstEntrada, al2.\"ALIQNFPerc\" aliqCredito, al2.\"ALIQRedNF\" redCredito,\n"
                    + "ret041.clasfisccod ncm, ret041.clasfisccest CODCEST, ret052.\"BARCod\" codigobarras, ret051.\"PRODUnid\"\n"
                    + "from RET051\n"
                    + "left join ret052 on ret052.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod\n"
                    + "left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "left join ret016 al1 on al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
                    + "left join ret016 al2 on al2.\"ALIQCod\" = ret051.aliqcred\n"
                    + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0\n"
                    + "order by ret052.\"BARCod\""
            )) {
                int contador = 1;
                //Obtem os produtos de balança
                while (rst.next()) {

                    //Integer idMercadologico1 = rst.getInt("SECCod");
                    //if (!rst.wasNull()) {
                    //    mercadologico1 = idMercadologico1;
                    //} else {
                        mercadologico1 = 0;
                    //}

                    //Integer idMercadologico2 = rst.getInt("GRUCod");
                    //if (!rst.wasNull()) {
                    //    mercadologico2 = idMercadologico2;
                    //} else {
                        mercadologico2 = 0;
                    //}

                    //Integer idMercadologico3 = rst.getInt("SUBGCod");
                    //if (!rst.wasNull()) {
                    //    mercadologico3 = idMercadologico3;
                    //} else {
                        mercadologico3 = 0;
                    //}

                    if (rst.getString("PRODCADAST") != null) {
                        datacadastro = Util.formatDataGUI(rst.getDate("PRODCADAST"));
                    } else {
                        datacadastro = "";
                    }

                    if ((rst.getString("prodstcofins") != null)
                            && (!"".equals(rst.getString("prodstcofins").trim()))) {

                        cst_pisdebito = Integer.parseInt(rst.getString("prodstcofins").trim());
                        cst_pisdebitoAux = cst_pisdebito;
                        id_tipopiscofinsdebito = Utils.retornarPisCofinsDebito(cst_pisdebito);
                    } else {
                        cst_pisdebitoAux = -1;
                        id_tipopiscofinsdebito = 1;
                    }

                    if ((rst.getString("prodstcofinsent") != null)
                            && (!"".equals(rst.getString("prodstcofinsent").trim()))) {

                        cst_piscredito = Integer.parseInt(rst.getString("prodstcofinsent").trim());
                        cst_piscreditoAux = cst_piscredito;
                        id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_piscredito);
                    } else {
                        cst_piscreditoAux = -1;
                        id_tipopiscofinscredito = 13;
                    }

                    naturezareceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito, "");

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

                    if ((rst.getString("CODCEST") != null) &&
                            (!rst.getString("CODCEST").trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                    } else {
                        idCest = -1;
                    }
                    
                    Integer idQtdEmbalagem = rst.getInt("prodqtemb");
                    if (!rst.wasNull()) {
                        if (idQtdEmbalagem == 0) {
                            qtdembalagem = 1;
                        } else {
                            qtdembalagem = idQtdEmbalagem;
                        }
                    } else {
                        qtdembalagem = 1;
                    }

                    Double idMargem = rst.getDouble("PRODMargem");
                    if (!rst.wasNull()) {
                        margem = idMargem;
                    } else {
                        margem = 0;
                    }

                    Double idCusto = rst.getDouble("PRODCusto");
                    if (!rst.wasNull()) {
                        custo = idCusto;
                    } else {
                        custo = 0;
                    }

                    Double idPrecovenda = rst.getDouble("PRODVenda");
                    if (!rst.wasNull()) {
                        precovenda = idPrecovenda;
                    } else {
                        precovenda = 0;
                    }

                    if ((rst.getString("prodai") != null)
                            && (!"".equals(rst.getString("prodai").trim()))) {
                        if ("A".equals(rst.getString("prodai"))) {
                            id_situacaocadastro = 1;
                        } else {
                            id_situacaocadastro = 0;
                        }
                    } else {
                        id_situacaocadastro = 0;
                    }

                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("PRODCod"));
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oProduto.setDataCadastro(datacadastro);
                    oProduto.setDescricaoCompleta((rst.getString("PRODNome") == null ? "" : rst.getString("PRODNome").trim()));
                    oProduto.setDescricaoReduzida((rst.getString("PRODNomeRed") == null ? "" : rst.getString("PRODNomeRed").trim()));
                    oProduto.setDescricaoGondola((rst.getString("PRODEtq") == null ? "" : rst.getString("PRODEtq").trim()));
                    oProduto.setMercadologico1(mercadologico1);
                    oProduto.setMercadologico2(mercadologico2);
                    oProduto.setMercadologico3(mercadologico3);
                    oProduto.setIdTipoPisCofinsDebito(id_tipopiscofinsdebito);
                    oProduto.setIdTipoPisCofinsCredito(id_tipopiscofinscredito);
                    oProduto.setTipoNaturezaReceita(naturezareceita);
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setQtdEmbalagem(qtdembalagem);
                    oProduto.setMargem(margem);
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
                    oProduto.setIdCest(idCest);

                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(precovenda);
                    oComplemento.setPrecoDiaSeguinte(precovenda);
                    oComplemento.setCustoComImposto(custo);
                    oComplemento.setCustoSemImposto(custo);
                    oComplemento.setIdSituacaoCadastro(id_situacaocadastro);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {

                        if ((rst.getDouble("aliqDebito") == 0)
                                && (rst.getDouble("redDebito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstSaida") == 20) {

                                    if ((rst.getDouble("aliqDebito") > 0)
                                            && (rst.getDouble("redDebito") == 0)) {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstSaida = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstSaida = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstSaida = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstSaida") == 20) {

                                if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") > 0)) {

                                    cstSaida = rst.getInt("cstSaida");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = rst.getInt("cstSaida");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstSaida = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstSaida = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstSaida = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redDebito") > 0) {
                                cstSaida = 20;
                            } else {
                                cstSaida = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstSaida = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstSaida = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstSaida = 41;
                            }
                        }
                    }

                    if ((rst.getString("cstEntrada") != null)
                            && (!rst.getString("cstEntrada").trim().isEmpty())) {

                        if ((rst.getDouble("aliqCredito") == 0)
                                && (rst.getDouble("redCredito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstEntrada") == 20) {

                                    if ((rst.getDouble("aliqCredito") > 0)
                                            && (rst.getDouble("redCredito") == 0)) {

                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstEntrada = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstEntrada = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstEntrada = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstEntrada") == 20) {

                                if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") > 0)) {

                                    cstEntrada = rst.getInt("cstEntrada");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = rst.getInt("cstEntrada");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstEntrada = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstEntrada = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstEntrada = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redCredito") > 0) {
                                cstEntrada = 20;
                            } else {
                                cstEntrada = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstEntrada = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstEntrada = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstEntrada = 41;
                            }
                        }
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), true));

                    oProduto.setValidade(validade);
                    oProduto.setPesavel(false);
                    oCodigoAnterior.setE_balanca(false);

                    if ((rst.getString("PRODUnid") != null) && (!rst.getString("PRODUnid").trim().isEmpty())) {
                        oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("PRODUnid").trim()));
                    } else {
                        oProduto.setIdTipoEmbalagem(0);
                    }
                    
                    if ((rst.getString("PRODBARCod") != null)
                            && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                        if (Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())) >= 1000000) {

                            if (rst.getString("PRODBARCod").trim().length() > 14) {
                                oAutomacao.setCodigoBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                            } else {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }
                    oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                    oCodigoAnterior.setCodigobalanca(0);
                    oCodigoAnterior.setE_balanca(false);

                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                    if ((rst.getString("PRODBARCod") != null)
                            && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                        if (rst.getString("PRODBARCod").trim().length() > 14) {
                            oCodigoAnterior.setBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                        } else {
                            oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }

                    oCodigoAnterior.setPiscofinsdebito(cst_pisdebitoAux);
                    oCodigoAnterior.setPiscofinscredito(cst_piscreditoAux);
                    oCodigoAnterior.setMargem(margem);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {
                        oCodigoAnterior.setRef_icmsdebito(rst.getString("cstSaida").trim());
                    } else {
                        oCodigoAnterior.setRef_icmsdebito("");
                    }

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())) {
                        oCodigoAnterior.setNcm(rst.getString("ncm").trim());
                    } else {
                        oCodigoAnterior.setNcm("");
                    }

                    vResult.put((int) oProduto.getId(), oProduto);
                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }
    
    public void importarProdutosRestaurante(int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos Restaurante...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutosRestaurante(idLojaVR);
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
            //produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.verificarLoja = true;
            produto.importarProdutoRestaurante(vProdutoNovo, idLojaVR, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ClientePreferencialVO> carregarValorLimite(int idLoja) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret028.\"CLICod\", ret028.clilimcc "
                            + "from ret028"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setId(new ClientePreferencialDAO().getIdCodigoAnterior(rst.getInt("CLICod"), idLoja));                    
                    vo.setValorlimite(rst.getDouble("clilimcc"));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarValorLimite(int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Valor Limite Cliente...");
            vResult = carregarValorLimite(idLojaVR);
            if (!vResult.isEmpty()) {
                new ClientePreferencialDAO().acertarValorLimite(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    /* INTEGRACAO*/
    private Map<Long, ProdutoVO> carregarProdutosIntegracao(int idLojaDestino) throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        int validade = 0, mercadologico1, mercadologico2, mercadologico3,
                ncm1, ncm2, ncm3, cst_pisdebito, cst_piscredito, id_tipopiscofinsdebito,
                id_tipopiscofinscredito, qtdembalagem, naturezareceita, id_familiaproduto,
                id_situacaocadastro, cst_pisdebitoAux, cst_piscreditoAux, cstSaida = 0, cstEntrada = 0, idCest;
        double margem, precovenda, custo;
        String ncmAtual, datacadastro, codigoAliquotaCga;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODNome\", ret051.\"PRODNomeRed\",\n"
                    + "ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\",\n"
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\",\n"
                    + "ret051.\"SUBGCod\", ret051.prodai, ret051.\"SECCod\",\n"
                    + "ret051.\"PRODBARCod\", ret051.clasfisccod, ret051.ncm ncm,\n"
                    + "ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\",\n"
                    + "ret051.prodsdo, prodqtemb, ret051.\"ALIQCod\", ret051.\"TABBCod\" cstSaida,\n"
                    + "al1.\"ALIQNFPerc\" aliqDebito, al1.\"ALIQRedNF\" redDebito, ret051.aliqcred,\n"
                    + "ret051.tabbcred cstEntrada, al2.\"ALIQNFPerc\" aliqCredito, al2.\"ALIQRedNF\" redCredito,\n"
                    + "ret041.clasfisccod ncm, ret041.clasfisccest CODCEST, ret051.\"PRODUnid\"\n"
                    + "from RET051\n"
                    + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod\n"
                    + "left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "left join ret016 al1 on al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
                    + "left join ret016 al2 on al2.\"ALIQCod\" = ret051.aliqcred\n"
                    + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0\n"
                    + "and ret051.\"PRODBARCod\" is not null "
                    + "order by ret053.\"PRODCod\" "
            )) {
                int contador = 1;
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    mercadologico1 = 0;
                    mercadologico2 = 0;
                    mercadologico3 = 0;

                    if (rst.getString("PRODCADAST") != null) {
                        datacadastro = Util.formatDataGUI(rst.getDate("PRODCADAST"));
                    } else {
                        datacadastro = "";
                    }

                    if ((rst.getString("prodstcofins") != null)
                            && (!"".equals(rst.getString("prodstcofins").trim()))) {

                        cst_pisdebito = Integer.parseInt(rst.getString("prodstcofins").trim());
                        cst_pisdebitoAux = cst_pisdebito;
                        id_tipopiscofinsdebito = Utils.retornarPisCofinsDebito(cst_pisdebito);
                    } else {
                        cst_pisdebitoAux = -1;
                        id_tipopiscofinsdebito = 1;
                    }

                    if ((rst.getString("prodstcofinsent") != null)
                            && (!"".equals(rst.getString("prodstcofinsent").trim()))) {

                        cst_piscredito = Integer.parseInt(rst.getString("prodstcofinsent").trim());
                        cst_piscreditoAux = cst_piscredito;
                        id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_piscredito);
                    } else {
                        cst_piscreditoAux = -1;
                        id_tipopiscofinscredito = 13;
                    }

                    naturezareceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito, "");

                    
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

                    if ((rst.getString("CODCEST") != null) &&
                            (!rst.getString("CODCEST").trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                    } else {
                        idCest = -1;
                    }
                    
                    Integer idQtdEmbalagem = rst.getInt("prodqtemb");
                    if (!rst.wasNull()) {
                        if (idQtdEmbalagem == 0) {
                            qtdembalagem = 1;
                        } else {
                            qtdembalagem = idQtdEmbalagem;
                        }
                    } else {
                        qtdembalagem = 1;
                    }

                    Double idMargem = rst.getDouble("PRODMargem");
                    if (!rst.wasNull()) {
                        margem = idMargem;
                    } else {
                        margem = 0;
                    }

                    Double idCusto = rst.getDouble("PRODCusto");
                    if (!rst.wasNull()) {
                        custo = idCusto;
                    } else {
                        custo = 0;
                    }

                    Double idPrecovenda = rst.getDouble("PRODVenda");
                    if (!rst.wasNull()) {
                        precovenda = idPrecovenda;
                    } else {
                        precovenda = 0;
                    }

                    if ((rst.getString("prodai") != null)
                            && (!"".equals(rst.getString("prodai").trim()))) {
                        if ("A".equals(rst.getString("prodai"))) {
                            id_situacaocadastro = 1;
                        } else {
                            id_situacaocadastro = 0;
                        }
                    } else {
                        id_situacaocadastro = 0;
                    }

                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("PRODCod"));
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oProduto.setDataCadastro(datacadastro);
                    oProduto.setDescricaoCompleta((rst.getString("PRODNome") == null ? "" : rst.getString("PRODNome").trim()));
                    oProduto.setDescricaoReduzida((rst.getString("PRODNomeRed") == null ? "" : rst.getString("PRODNomeRed").trim()));
                    oProduto.setDescricaoGondola((rst.getString("PRODEtq") == null ? "" : rst.getString("PRODEtq").trim()));
                    oProduto.setMercadologico1(mercadologico1);
                    oProduto.setMercadologico2(mercadologico2);
                    oProduto.setMercadologico3(mercadologico3);
                    oProduto.setIdTipoPisCofinsDebito(id_tipopiscofinsdebito);
                    oProduto.setIdTipoPisCofinsCredito(id_tipopiscofinscredito);
                    oProduto.setTipoNaturezaReceita(naturezareceita);
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setQtdEmbalagem(qtdembalagem);
                    oProduto.setMargem(margem);
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
                    oProduto.setIdCest(idCest);

                    oComplemento.setIdLoja(idLojaDestino);
                    oComplemento.setPrecoVenda(precovenda);
                    oComplemento.setPrecoDiaSeguinte(precovenda);
                    oComplemento.setCustoComImposto(custo);
                    oComplemento.setCustoSemImposto(custo);
                    oComplemento.setIdSituacaoCadastro(id_situacaocadastro);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {

                        if ((rst.getDouble("aliqDebito") == 0)
                                && (rst.getDouble("redDebito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstSaida") == 20) {

                                    if ((rst.getDouble("aliqDebito") > 0)
                                            && (rst.getDouble("redDebito") == 0)) {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstSaida = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstSaida = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstSaida = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstSaida = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstSaida = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstSaida = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstSaida = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstSaida") == 20) {

                                if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqDebito") > 0)
                                        && (rst.getDouble("redDebito") > 0)) {

                                    cstSaida = rst.getInt("cstSaida");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstSaida = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstSaida = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstSaida = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstSaida = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redDebito") > 0) {
                                        cstSaida = 20;
                                    } else {
                                        cstSaida = rst.getInt("cstSaida");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstSaida = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstSaida = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstSaida = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("ALIQCod"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redDebito") > 0) {
                                cstSaida = 20;
                            } else {
                                cstSaida = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstSaida = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstSaida = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstSaida = 41;
                            }
                        }
                    }

                    if ((rst.getString("cstEntrada") != null)
                            && (!rst.getString("cstEntrada").trim().isEmpty())) {

                        if ((rst.getDouble("aliqCredito") == 0)
                                && (rst.getDouble("redCredito") == 0)) {

                            codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                            if (!codigoAliquotaCga.contains("I")
                                    && (!codigoAliquotaCga.contains("F"))
                                    && (!codigoAliquotaCga.contains("N"))) {

                                if (rst.getInt("cstEntrada") == 20) {

                                    if ((rst.getDouble("aliqCredito") > 0)
                                            && (rst.getDouble("redCredito") == 0)) {

                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 0;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    } else {
                                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                        if (!codigoAliquotaCga.contains("I")
                                                && (!codigoAliquotaCga.contains("F"))
                                                && (!codigoAliquotaCga.contains("N"))) {
                                            cstEntrada = 90;
                                        } else {
                                            if (codigoAliquotaCga.contains("I")) {
                                                cstEntrada = 40;
                                            } else if (codigoAliquotaCga.contains("F")) {
                                                cstEntrada = 60;
                                            } else if (codigoAliquotaCga.contains("N")) {
                                                cstEntrada = 41;
                                            }
                                        }
                                    }
                                } else {
                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = 0;
                                    }
                                }
                            } else {
                                if (codigoAliquotaCga.contains("I")) {
                                    cstEntrada = 40;
                                } else if (codigoAliquotaCga.contains("F")) {
                                    cstEntrada = 60;
                                } else if (codigoAliquotaCga.contains("N")) {
                                    cstEntrada = 41;
                                }
                            }
                        } else {

                            if (rst.getInt("cstEntrada") == 20) {

                                if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") == 0)) {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 0;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                } else if ((rst.getDouble("aliqCredito") > 0)
                                        && (rst.getDouble("redCredito") > 0)) {

                                    cstEntrada = rst.getInt("cstEntrada");
                                } else {
                                    codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));

                                    if (!codigoAliquotaCga.contains("I")
                                            && (!codigoAliquotaCga.contains("F"))
                                            && (!codigoAliquotaCga.contains("N"))) {
                                        cstEntrada = 90;
                                    } else {
                                        if (codigoAliquotaCga.contains("I")) {
                                            cstEntrada = 40;
                                        } else if (codigoAliquotaCga.contains("F")) {
                                            cstEntrada = 60;
                                        } else if (codigoAliquotaCga.contains("N")) {
                                            cstEntrada = 41;
                                        }
                                    }
                                }
                            } else {

                                codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                                if (!codigoAliquotaCga.contains("I")
                                        && (!codigoAliquotaCga.contains("F"))
                                        && (!codigoAliquotaCga.contains("N"))) {

                                    if (rst.getDouble("redCredito") > 0) {
                                        cstEntrada = 20;
                                    } else {
                                        cstEntrada = rst.getInt("cstEntrada");
                                    }
                                } else {
                                    if (codigoAliquotaCga.contains("I")) {
                                        cstEntrada = 40;
                                    } else if (codigoAliquotaCga.contains("F")) {
                                        cstEntrada = 60;
                                    } else if (codigoAliquotaCga.contains("N")) {
                                        cstEntrada = 41;
                                    }
                                }
                            }
                        }
                    } else {

                        codigoAliquotaCga = new AliquotaCgaDAO().getCodigoAliquota(rst.getInt("aliqcred"));
                        if (!codigoAliquotaCga.contains("I")
                                && (!codigoAliquotaCga.contains("F"))
                                && (!codigoAliquotaCga.contains("N"))) {

                            if (rst.getDouble("redCredito") > 0) {
                                cstEntrada = 20;
                            } else {
                                cstEntrada = 0;
                            }
                        } else {
                            if (codigoAliquotaCga.contains("I")) {
                                cstEntrada = 40;
                            } else if (codigoAliquotaCga.contains("F")) {
                                cstEntrada = 60;
                            } else if (codigoAliquotaCga.contains("N")) {
                                cstEntrada = 41;
                            }
                        }
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("aliqCredito"), rst.getDouble("redCredito"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), false));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("aliqDebito"), rst.getDouble("redDebito"), true));

                    oProduto.setValidade(validade);
                    oProduto.setPesavel(false);
                    oCodigoAnterior.setE_balanca(false);

                    if ((rst.getString("PRODBARCod") != null)
                            && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                        if (Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())) >= 1000000) {

                            if (rst.getString("PRODBARCod").trim().length() > 14) {
                                oAutomacao.setCodigoBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                            } else {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }

                    if ((rst.getString("PRODUnid") != null) && (!rst.getString("PRODUnid").trim().isEmpty())) {
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("PRODUnid").trim()));
                    } else {
                        oAutomacao.setIdTipoEmbalagem(0);
                    }

                    oCodigoAnterior.setCodigobalanca(0);
                    oCodigoAnterior.setE_balanca(false);

                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                    if ((rst.getString("PRODBARCod") != null)
                            && (!rst.getString("PRODBARCod").trim().isEmpty())) {
                        if (rst.getString("PRODBARCod").trim().length() > 14) {
                            oCodigoAnterior.setBarras(Long.parseLong(rst.getString("PRODBARCod").trim().substring(0, 14)));
                        } else {
                            oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim())));
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }

                    oCodigoAnterior.setPiscofinsdebito(cst_pisdebitoAux);
                    oCodigoAnterior.setPiscofinscredito(cst_piscreditoAux);
                    oCodigoAnterior.setMargem(margem);

                    if ((rst.getString("cstSaida") != null)
                            && (!rst.getString("cstSaida").trim().isEmpty())) {
                        oCodigoAnterior.setRef_icmsdebito(rst.getString("cstSaida").trim());
                    } else {
                        oCodigoAnterior.setRef_icmsdebito("");
                    }

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())) {
                        oCodigoAnterior.setNcm(rst.getString("ncm").trim());
                    } else {
                        oCodigoAnterior.setNcm("");
                    }

                    oProduto.setCodigoBarras(oAutomacao.getCodigoBarras());
                    
                    vResult.put(oProduto.getCodigoBarras(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produtos Integracao..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarProdutoIntegracao(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos Integracao...");
            Map<Long, ProdutoVO> vProduto = carregarProdutosIntegracao(idLojaVR);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            ProgressBar.setMaximum(vProduto.size());

            for (Long keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.implantacaoExterna = true;
            produto.salvarIntegracao(vProdutoNovo, idLojaVR, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    private List<ProdutoVO> carregarPrecoProdutoIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idProduto;
        double precoVenda;
        long codigobarras;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODBARCod\", "
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\" "
                    + "from RET051 "
                    + "where ret051.\"PRODBARCod\" is not null"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    idProduto = rst.getInt("PRODCod");
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim()));
                    precoVenda = rst.getDouble("PRODVenda");

                    if (codigobarras > 99999) {
                        oProduto.setCodigoBarras(codigobarras);
                        oComplemento.setIdLoja(idLojaVR);
                        oComplemento.setPrecoVenda(precoVenda);
                        oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                        oProduto.vComplemento.add(oComplemento);
                        vResult.add(oProduto);
                        ProgressBar.setStatus("Carregando dados...Preço Loja Integracao " + idLojaVR+" "+contador);
                        contador++;
                    }
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProdutoIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Loja Integracao " + idLojaVR);
            vProduto = carregarPrecoProdutoIntegracao(idLojaVR);
            new ProdutoDAO().alterarPrecoProdutoIntegracao(vProduto, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarCustoProdutoIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idProduto;
        long codigobarras;

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODCusto\", ret051.\"PRODBARCod\", \n"
                    + "ret051.prodcustofinal\n"
                    + "from ret051 "
                    + "where ret051.\"PRODBARCod\" is not null"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    idProduto = rst.getInt("PRODCod");
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim()));
                    
                    if (codigobarras > 999999) {
                        oProduto.setCodigoBarras(codigobarras);
                        oComplemento.setIdLoja(idLojaVR);
                        oComplemento.setCustoComImposto(rst.getDouble("prodcustofinal"));
                        oComplemento.setCustoSemImposto(rst.getDouble("PRODCusto"));
                        oProduto.vComplemento.add(oComplemento);
                        vResult.add(oProduto);
                        
                        ProgressBar.setStatus("Carregando dados...Custo Integracao Loja " + idLojaVR + " "+contador);
                        contador ++;
                    }
                }
            }
        }
        return vResult;
    }

    public void importarCustoProdutoIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Integracao Loja " + idLojaVR + "...");
            vProdutoNovo = carregarCustoProdutoIntegracao(idLojaVR);
            new ProdutoDAO().alterarCustoProdutoIntegracao(vProdutoNovo, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    private List<ProdutoVO> carregarEstoqueProdutoIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        long codigobarras;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.prodsdo, ret051.\"PRODBARCod\"\n"
                    + "from RET051\n"
                    + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0 "
                    + "and ret051.\"PRODBARCod\" is not null"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("PRODBARCod").trim()));
                    
                    if (codigobarras > 999999) {
                        oProduto.setId(rst.getInt("PRODCod"));
                        oComplemento.setIdLoja(idLojaVR);
                        oComplemento.setEstoque(rst.getDouble("prodsdo"));
                        oProduto.vComplemento.add(oComplemento);
                        vResult.add(oProduto);
                        
                        ProgressBar.setStatus("Carregando dados...Estoque Integracao Loja " + idLojaVR + " "+contador);
                        contador++;
                    }
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProdutoIntegracao(int idLojaVR, boolean somarEstoque) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Integracao Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProdutoIntegracao(idLojaVR);
            if (!vResult.isEmpty()) {

                if (somarEstoque) {
                    new ProdutoDAO().alterarEstoqueProdutoSomando(vResult, idLojaVR);
                } else {
                    new ProdutoDAO().alterarEstoqueProdutoIntegracao(vResult, idLojaVR);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarrasIntegracao(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Código de Barras...");
            vResult = carregarCodigoBarras();
            if (!vResult.isEmpty()) {
                ProdutoDAO produto = new ProdutoDAO();
                produto.verificarLoja = true;
                produto.id_loja = idLojaVR;
                produto.addCodigoBarras(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    
    public void importarFornecedorIntegracao(int idLojaVR) throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor Integracao...");
            vResult = carregarFornecedor();
            if (!vResult.isEmpty()) {
                new FornecedorDAO().salvarCnpj(vResult, idLojaVR);
            }
            
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarClientePreferencialIntegracao(int idLojaCliente, int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial Integracao Loja "+idLojaVR);
            vResult = carregarClientePreferencial(true);
            if (!vResult.isEmpty()) {
                new ClientePreferencialDAO().salvarCpf(vResult, idLojaVR, idLojaCliente);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoIntegracao(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        long cpfCnpj;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret010.\"CLICod\", ret010.\"CCTCupom\", ret010.cctecf, ret010.\"CCTData\",\n"
                    + "ret010.cctvcto,ret010.\"CCTDebito\", ret010.cctobs, ret010.\"CCTPgto\",\n"
                    + "ret028.clicpf, ret028.clicnpj\n"
                    + "from ret010\n"
                    + "left join ret028 on ret028.\"CLICod\" = ret010.\"CLICod\"\n"
                    + "where ret010.\"CCTPG\" = 'N';"
            )) {
                while (rst.next()) {
                    
                    if ((rst.getString("clicpf") != null)
                            && (!rst.getString("clicpf").trim().isEmpty())) {
                        cpfCnpj = Long.parseLong(Utils.formataNumero(rst.getString("clicpf").trim()));
                    } else if ((rst.getString("clicnpj") != null)
                            && (!rst.getString("clicnpj").trim().isEmpty())) {
                        cpfCnpj = Long.parseLong(Utils.formataNumero(rst.getString("clicnpj").trim()));
                    } else {
                        cpfCnpj = -1;
                    }

                    if (cpfCnpj > 99999999) {

                        ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                        vo.setId_clientepreferencial(rst.getInt("CLICod"));
                        vo.setCnpjCliente(cpfCnpj);
                        vo.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("CCTCupom"))));
                        vo.setEcf(Integer.parseInt(Utils.formataNumero(rst.getString("cctecf"))));
                        vo.setDataemissao(rst.getString("CCTData").trim());
                        vo.setDatavencimento(rst.getString("cctvcto"));
                        vo.setValor(rst.getDouble("CCTDebito"));
                        vo.setId_loja(idLojaVR);
                        vResult.add(vo);
                    }
                }
            }
        }
        return vResult;
    }

    public void importarReceberCreditoRotativoIntegracao(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Integracao Cliente Loja " + idLojaVR + "...");
            vResult = carregarReceberCreditoRotativoIntegracao(idLojaVR);
            if (!vResult.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvarComCnpj(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
