package vrimplantacao.dao.interfaces;

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
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
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

public class DestroDAO {

    public List<FamiliaProdutoVO> carregarFamiliaProdutoCGA() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ret011.\"SUBCod\", ret011.\"SUBDesc\" from ret011");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("SUBCod"));
                oFamiliaProduto.descricao = util.acertarTexto(rst.getString("SUBDesc").replace("'", ""));
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologicoDestro(int nivel) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "", strMercadologico1 = "";

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select SECAO_CHAVE, S_DES ");
            sql.append("from SECAO ");
            sql.append("order by SECAO_CHAVE ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    descricao = Utils.acertarTexto(rst.getString("S_DES").replace("'", "").replace("-", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    strMercadologico1 = Utils.acertarTexto(rst.getString("SECAO_CHAVE").trim());

                    oMercadologico.strMercadologico1 = strMercadologico1;
                    oMercadologico.mercadologico1 = 0;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {
                    descricao = Utils.acertarTexto(rst.getString("S_DES").replace("'", "").replace("-", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    strMercadologico1 = Utils.acertarTexto(rst.getString("SECAO_CHAVE").trim());

                    oMercadologico.strMercadologico1 = strMercadologico1;
                    oMercadologico.mercadologico1 = 0;
                    oMercadologico.mercadologico2 = 1;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {
                    descricao = Utils.acertarTexto(rst.getString("S_DES").replace("'", "").replace("-", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    strMercadologico1 = Utils.acertarTexto(rst.getString("SECAO_CHAVE").trim());

                    oMercadologico.strMercadologico1 = strMercadologico1;

                    oMercadologico.mercadologico1 = 0;
                    oMercadologico.mercadologico2 = 1;
                    oMercadologico.mercadologico3 = 1;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProduto(int id_loja, int id_lojaDestino) throws SQLException, Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int id_produto, id_tipoembalagem, validade, codigobalanca,
                mercadologico1 = 0, mercadologico2 = 0, mercadologico3 = 0, ncm1, ncm2, ncm3,
                cst_pisdebito, cst_piscredito, id_tipopiscofinsdebito,
                id_tipopiscofinscredito, qtdembalagem, id_familiaproduto,
                id_situacaocadastro, aliquota, cst_pisdebitoAux, cst_piscreditoAux,
                tipoNaturezaReceita, tipoNaturezaReceitaAux;
        boolean pesavel, ebalanca;
        double margem, precovenda, estoque, custo;
        String descricaocompleta, descricaoreduzida, descricaogondola,
                ncmAtual = null, datacadastro;
        long codigobarras;

        stm = ConexaoFirebird.getConexao().createStatement();
        stmPG = Conexao.createStatement();

        try {
            sql = new StringBuilder();
            sql.append("SELECT P.ESTITEM_CHAVE, P.I_BAR, P.I_DES, P.UNIDADE_UNV_CHAVE, P.I_QUN, P.I_PUN, ");
            sql.append("       P.I_DRD, P.I_ICMS, IC.ICMS_COD_CHAVE, IC.I_PER, IC.i_prb, P.DATA_INCLUSAO, P.DATA_ALTERACAO, ");
            sql.append("       P.NCM, P.PESOLIQ, P.PESOBRUTO, P.PIS_SAIDA_SITTRIB, P.PIS_ENTRADA_SITTRIB, ");
            sql.append("       P.NATREC, P.CST_ICMS_SAIDA, I.FILIAL_CHAVE, I.T_CUS, T_PVL, I.secao_chave ");
            sql.append("  FROM ESTITEM P ");
            sql.append(" INNER JOIN item I ON I.estitem_chave  = P.estitem_chave ");
            sql.append(" INNER JOIN ICMS IC ON IC.ICMS_COD_CHAVE = P.i_icms and I.estitem_chave  = P.estitem_chave ");
            sql.append(" where IC.estado_chave = 'SP' ");
            sql.append(" ORDER BY P.estitem_chave ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();

                if ((rst.getString("I_BAR") != null)
                        && (!rst.getString("I_BAR").trim().isEmpty())) {

                    sql = new StringBuilder();
                    sql.append("select codigo, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where cast(codigo as numeric(14,0)) = " + Double.parseDouble(Utils.formataNumero(rst.getString("ESTITEM_CHAVE").trim())));

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

                        if ((rst.getString("UNIDADE_UNV_CHAVE") != null)
                                && (!rst.getString("UNIDADE_UNV_CHAVE").trim().isEmpty())) {

                            if ("KG".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                                id_tipoembalagem = 4;
                            } else if ("CX".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                                id_tipoembalagem = 1;
                            } else if ("PC".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                                id_tipoembalagem = 3;
                            } else if ("UN".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                                id_tipoembalagem = 0;
                            } else {
                                id_tipoembalagem = 0;
                            }
                        } else {
                            id_tipoembalagem = 0;
                        }
                        ebalanca = false;
                        pesavel = false;
                        codigobalanca = 0;
                        validade = 0;
                    }
                } else {
                    if ((rst.getString("UNIDADE_UNV_CHAVE") != null)
                            && (!rst.getString("UNIDADE_UNV_CHAVE").trim().isEmpty())) {

                        if ("KG".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                            id_tipoembalagem = 4;
                        } else if ("CX".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                            id_tipoembalagem = 1;
                        } else if ("PC".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                            id_tipoembalagem = 3;
                        } else if ("UN".equals(rst.getString("UNIDADE_UNV_CHAVE").trim())) {
                            id_tipoembalagem = 0;
                        } else {
                            id_tipoembalagem = 0;
                        }
                    } else {
                        id_tipoembalagem = 0;
                    }
                    ebalanca = false;
                    pesavel = false;
                    codigobalanca = 0;
                    validade = 0;
                }

                id_produto = Integer.parseInt(Utils.formataNumero(rst.getString("ESTITEM_CHAVE").trim()));

                if ((rst.getString("I_DES") != null)
                        && (!rst.getString("I_DES").trim().isEmpty())) {
                    descricaocompleta = Utils.acertarTexto(rst.getString("I_DES").trim().replace("'", ""));
                } else {
                    descricaocompleta = "";
                }

                if ((rst.getString("I_DRD") != null)
                        && (!rst.getString("I_DRD").trim().isEmpty())) {
                    descricaoreduzida = Utils.acertarTexto(rst.getString("I_DRD").trim().replace("'", ""));
                } else {
                    descricaoreduzida = "";
                }

                descricaogondola = descricaocompleta;

                sql = new StringBuilder();
                sql.append("select merc1, merc2, merc3 ");
                sql.append("from implantacao.codigoanteriormercadologico ");
                sql.append("where str_mercadologico1 = '" + Utils.acertarTexto(rst.getString("secao_chave").trim()) + "'");
                sql.append(" and nivel = 3");

                rstPG = stmPG.executeQuery(sql.toString());

                if (rstPG.next()) {
                    mercadologico1 = rstPG.getInt("merc1");
                    mercadologico2 = rstPG.getInt("merc2");
                    mercadologico3 = rstPG.getInt("merc3");
                }

                if ((rst.getString("DATA_INCLUSAO") != null)
                        && (!rst.getString("DATA_INCLUSAO").trim().isEmpty())) {
                    datacadastro = Util.formatDataGUI(rst.getDate("DATA_INCLUSAO"));
                } else {
                    if ((rst.getString("DATA_ALTERACAO") != null)
                            && (!rst.getString("DATA_ALTERACAO").trim().isEmpty())) {
                        datacadastro = Util.formatDataGUI(rst.getDate("DATA_ALTERACAO"));
                    } else {
                        datacadastro = "";
                    }
                }

                if ((rst.getString("PIS_SAIDA_SITTRIB") != null)
                        && (!"".equals(rst.getString("PIS_SAIDA_SITTRIB").trim()))) {

                    cst_pisdebito = Integer.parseInt(rst.getString("PIS_SAIDA_SITTRIB").trim());
                    cst_pisdebitoAux = cst_pisdebito;
                    id_tipopiscofinsdebito = Utils.retornarPisCofinsDebito(cst_pisdebito);
                } else {

                    cst_pisdebitoAux = -1;
                    id_tipopiscofinsdebito = 1;
                }

                if ((rst.getString("PIS_ENTRADA_SITTRIB") != null)
                        && (!"".equals(rst.getString("PIS_ENTRADA_SITTRIB").trim()))) {

                    cst_piscredito = Integer.parseInt(rst.getString("PIS_ENTRADA_SITTRIB").trim());
                    cst_piscreditoAux = cst_piscredito;
                    id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_piscredito);
                } else {

                    cst_piscreditoAux = -1;
                    id_tipopiscofinscredito = 13;
                }

                if ((rst.getString("NATREC") != null)
                        && (!rst.getString("NATREC").trim().isEmpty())) {
                    tipoNaturezaReceitaAux = Integer.parseInt(Utils.formataNumero(rst.getString("NATREC").trim()));
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito,
                            Utils.formataNumero(rst.getString("NATREC").trim()));
                } else {
                    tipoNaturezaReceitaAux = -1;
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(id_tipopiscofinsdebito, "");
                }

                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").isEmpty())
                        && (rst.getString("NCM").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("NCM").trim());
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

                if ((rst.getString("CST_ICMS_SAIDA") != null)
                        && (!rst.getString("CST_ICMS_SAIDA").trim().isEmpty())) {

                    if (Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()) == 0) {

                        if ((rst.getString("I_PER") != null)
                                && (!rst.getString("I_PER").trim().isEmpty())) {

                            aliquota = retornarIcms(Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()),
                                    Double.parseDouble(rst.getString("I_PER").trim()), 0);
                        } else {
                            aliquota = 8;
                        }

                    } else if (Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()) == 20) {

                        if ((rst.getString("I_PER") != null)
                                && (!rst.getString("I_PER").trim().isEmpty())
                                && (rst.getString("i_prb") != null)
                                && (!rst.getString("i_prb").trim().isEmpty())) {

                            aliquota = retornarIcms(Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()),
                                    Double.parseDouble(rst.getString("I_PER").trim()),
                                    Double.parseDouble(rst.getString("i_prb").trim()));

                        } else {
                            aliquota = 8;
                        }

                    } else {
                        aliquota = retornarIcms(Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()),
                                0, 0);
                    }

                } else {
                    aliquota = 8;
                }

                qtdembalagem = 1;
                margem = 0;
                id_familiaproduto = -1;
                custo = 0;
                precovenda = 0;
                estoque = 0;
                id_situacaocadastro = 1;

                if (ebalanca) {

                    codigobarras = Long.parseLong(String.valueOf(id_produto));
                } else {

                    if ((rst.getString("I_BAR") != null)
                            && (!"".equals(rst.getString("I_BAR")))) {

                        codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("I_BAR").trim()));

                        if ((String.valueOf(codigobarras).length() < 7)) {

                            codigobarras = -2;
                        } else if ((String.valueOf(codigobarras).length() > 14)) {

                            codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                        }
                    } else {

                        codigobarras = -2;
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
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
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

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.custoSemImposto = custo;
                oComplemento.custoComImposto = custo;
                oComplemento.precoVenda = precovenda;
                oComplemento.precoDiaSeguinte = precovenda;
                oComplemento.idSituacaoCadastro = id_situacaocadastro;
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Global.idEstado;
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

                if ((rst.getString("I_BAR") != null)
                        && (!"".equals(rst.getString("I_BAR").trim()))) {

                    oAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("I_BAR").trim()));
                } else {

                    oAnterior.barras = -1;
                }

                oAnterior.piscofinsdebito = cst_pisdebitoAux;
                oAnterior.piscofinscredito = cst_piscreditoAux;

                if ((rst.getString("CST_ICMS_SAIDA") != null)
                        && (!rst.getString("CST_ICMS_SAIDA").trim().isEmpty())) {

                    if ((Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()) != 0)
                            && (Integer.parseInt(rst.getString("CST_ICMS_SAIDA").trim()) != 20)) {

                        oAnterior.ref_icmsdebito = rst.getString("CST_ICMS_SAIDA").trim();
                    } else {

                        if ((rst.getString("I_PER") != null)
                                && (!rst.getString("I_PER").trim().isEmpty())) {
                            oAnterior.ref_icmsdebito = rst.getString("I_PER").trim();
                        } else {
                            oAnterior.ref_icmsdebito = "";
                        }
                    }
                } else {
                    oAnterior.ref_icmsdebito = "";
                }

                oAnterior.estoque = estoque;
                oAnterior.e_balanca = ebalanca;
                oAnterior.codigobalanca = codigobalanca;
                oAnterior.custosemimposto = custo;
                oAnterior.custocomimposto = custo;
                oAnterior.margem = margem;

                if ((rst.getString("NCM") != null)
                        && !rst.getString("NCM").trim().isEmpty()) {
                    oAnterior.ncm = ncmAtual;
                } else {
                    oAnterior.ncm = "";
                }

                oAnterior.naturezareceita = tipoNaturezaReceitaAux;

                oProduto.vCodigoAnterior.add(oAnterior);

                vProduto.put(id_produto, oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarras() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int id_produto;
        long codigobarras;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select estitem_chave, codbarr_chave_bar ");
            sql.append("from CODBARR ");
            sql.append("where cast(codbarr_chave_bar as numeric(14,0)) > 999999 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("estitem_chave") != null)
                        && (!rst.getString("estitem_chave").trim().isEmpty())) {

                    id_produto = Integer.parseInt(Utils.formataNumero(rst.getString("estitem_chave").trim()));

                    if ((rst.getString("codbarr_chave_bar") != null)
                            && (!rst.getString("codbarr_chave_bar").trim().isEmpty())) {

                        codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("codbarr_chave_bar").trim()));

                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = id_produto;

                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = codigobarras;
                        oAutomacao.qtdEmbalagem = 1;
                        oProduto.vAutomacao.add(oAutomacao);

                        vProduto.add(oProduto);
                    }
                }
            }

            stm.close();
            return vProduto;
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
        double idProduto=0;
        long codigobarras=-1;

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("id"));
                
                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto<=9999)) {
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
    
    private List<ProdutoVO> carregarPrecoProduto(int idLoja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double margem, precoVenda;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select P.ESTITEM_CHAVE, I.t_pv1 ");
            sql.append("  FROM ESTITEM P ");
            sql.append(" INNER JOIN item I ON I.estitem_chave  = P.estitem_chave ");
            sql.append(" where I.FILIAL_CHAVE = " + id_lojaCliente);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("ESTITEM_CHAVE").trim()));

                if ((rst.getString("t_pv1") != null)
                        && (!rst.getString("t_pv1").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("t_pv1").trim());
                } else {
                    precoVenda = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            stm.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(int idLoja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double custo;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select P.ESTITEM_CHAVE, I.T_CUS ");
            sql.append("  FROM ESTITEM P ");
            sql.append(" INNER JOIN item I ON I.estitem_chave  = P.estitem_chave ");
            sql.append(" where I.FILIAL_CHAVE = " + id_lojaCliente);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("ESTITEM_CHAVE").trim()));

                if ((rst.getString("T_CUS") != null)
                        && (!rst.getString("T_CUS").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("T_CUS").trim());
                } else {
                    custo = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.custoSemImposto = custo;
                oComplemento.custoComImposto = custo;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            stm.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double estoque = 0;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ret051.\"PRODCod\", ret051.prodsdo ");
            sql.append("from RET051 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("PRODCod").trim());

                if ((rst.getString("prodsdo") != null)
                        && (!rst.getString("prodsdo").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("prodsdo").trim());
                } else {
                    estoque = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int id_municipio, id_estado, id_tipoinscricao, id_situacaocadastro;
        String razaoSocial, nomeFantasia, ie, endereco, bairro, strEstado,
                telefone, numero, complemento, agencia, conta, observacao,
                contato, email, fax, telefone2;
        long cnpj, cep, id_fornecedor = 0;
        java.sql.Date datacadastro;
        Utils util = new Utils();

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT F.clifor_chave, F.c_tip, F.c_ins, F.c_des, F.c_fan, F.c_cad, F.data_exclusao, ");
            sql.append("       EN.estado_chave, EN.endereco, EN.numero, EN.cidade, EN.bairro, EN.complemento, ");
            sql.append("       EM.email, TEL.telefone, TEL.contato, EN.estado_chave, EN.cep ");
            sql.append("  FROM CLIFOR F ");
            sql.append("  LEFT join CLIENDERECOS ON CLIENDERECOS.clifor_chave = F.clifor_chave ");
            sql.append("  LEFT JOIN ENDERECOS EN ON EN.id_endereco = CLIENDERECOS.id_endereco ");
            sql.append("  LEFT JOIN CLIEMAIL ON CLIEMAIL.clifor_chave = F.clifor_chave ");
            sql.append("  LEFT JOIN EMAIL EM ON  EM.id_email = CLIEMAIL.id_email ");
            sql.append("  LEFT JOIN clitelefones CTEL ON CTEL.clifor_chave = F.clifor_chave ");
            sql.append("  LEFT JOIN telefones TEL ON TEL.id_telefone = CTEL.id_telefone ");
            sql.append(" WHERE F.CLIFOR_TIPO IN (2,3) ");
            sql.append(" ORDER BY F.clifor_chave ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                id_fornecedor = Long.parseLong(Utils.formataNumero(rst.getString("clifor_chave").trim()));

                if ((rst.getString("c_des") != null)
                        && (!"".equals(rst.getString("c_des").trim()))) {
                    razaoSocial = Utils.acertarTexto(rst.getString("c_des").trim().replace("'", ""));
                } else {
                    razaoSocial = "";
                }

                if ((rst.getString("c_fan") != null)
                        && (!"".equals(rst.getString("c_fan").trim()))) {
                    nomeFantasia = Utils.acertarTexto(rst.getString("c_fan").trim().replace("'", ""));
                } else {
                    nomeFantasia = "";
                }

                if (nomeFantasia.isEmpty()) {
                    nomeFantasia = razaoSocial;
                }

                if ((rst.getString("endereco") != null)
                        && (!"".equals(rst.getString("endereco").trim()))) {
                    endereco = Utils.acertarTexto(rst.getString("endereco").trim().replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("bairro") != null)
                        && (!"".equals(rst.getString("bairro").trim()))) {
                    bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("cidade") != null)
                        && (!"".equals(rst.getString("cidade").trim()))) {
                    if ((rst.getString("estado_chave") != null)
                            && (!"".equals(rst.getString("estado_chave").trim()))) {
                        strEstado = Utils.acertarTexto(rst.getString("estado_chave").trim().replace("'", ""));
                    } else {
                        strEstado = "SP";
                    }

                    id_municipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("cidade")),
                            strEstado);

                    if (id_municipio == 0) {

                        id_municipio = Global.idMunicipio;
                    }
                } else {

                    id_municipio = Global.idMunicipio;
                }

                if ((rst.getString("cep") != null)
                        && (!"".equals(rst.getString("cep").trim()))) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim().replace("'", "")));
                } else {
                    cep = Global.Cep;
                }

                if ((rst.getString("estado_chave") != null)
                        && (!"".equals(rst.getString("estado_chave").trim()))) {
                    id_estado = Utils.retornarEstadoDescricao(
                            Utils.acertarTexto(rst.getString("estado_chave").trim().replace("'", "")));

                    if (id_estado == 0) {
                        id_estado = Global.idEstado;
                    }
                } else {
                    id_estado = Global.idEstado;
                }

                if ((rst.getString("telefone") != null)
                        && (!"".equals(rst.getString("telefone").trim()))) {
                    telefone = Utils.formataNumero(rst.getString("telefone").trim().replace("'", ""));
                } else {
                    telefone = "0000000000";
                }

                if ((rst.getString("clifor_chave") != null)
                        && (!"".equals(rst.getString("clifor_chave").trim()))) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("clifor_chave").trim()));
                } else {
                    cnpj = -1;
                }

                if ((rst.getString("c_ins") != null)
                        && (!"".equals(rst.getString("c_ins").trim()))) {

                    ie = Utils.acertarTexto(rst.getString("c_ins").trim().replace("'", ""));
                    ie = ie.replace(".", "");
                    ie = ie.replace("-", "");
                    ie = ie.replace(",", "");
                    ie = ie.replace("/", "");
                    ie = ie.replace("\"", "");
                    ie = ie.replace("*", "");
                } else {

                    ie = "ISENTO";
                }

                if ("J".equals(rst.getString("c_tip").trim())) {
                    id_tipoinscricao = 0;
                } else {
                    id_tipoinscricao = 1;
                }

                if ((rst.getString("data_exclusao") != null)
                        && (!rst.getString("data_exclusao").trim().isEmpty())) {
                    id_situacaocadastro = 0;
                } else {
                    id_situacaocadastro = 1;
                }

                if ((rst.getString("numero") != null)
                        && (!"".equals(rst.getString("numero").trim()))
                        && (!"S/N".equals(rst.getString("numero").trim()))) {
                    numero = Utils.acertarTexto(rst.getString("numero").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("complemento") != null)
                        && (!"".equals(rst.getString("complemento").trim()))) {
                    complemento = Utils.acertarTexto(rst.getString("complemento").trim().replace("'", ""));
                } else {
                    complemento = "";
                }

                if ((rst.getString("c_cad") != null)
                        && (!rst.getString("c_cad").trim().isEmpty())) {
                    datacadastro = new java.sql.Date(rst.getDate("c_cad").getTime());
                } else {
                    datacadastro = new java.sql.Date(new java.util.Date().getTime());
                }

                agencia = "";
                conta = "";

                if ((rst.getString("contato") != null)
                        && (!"".equals(rst.getString("contato").trim()))) {
                    observacao = "CONTATO: " + Utils.acertarTexto(rst.getString("contato").trim().replace("'", ""));
                } else {
                    observacao = "";
                }

                contato = "";
                /* contatos do fornecedor */
                if ((rst.getString("email") != null)
                        && (!"".equals(rst.getString("email").trim()))) {
                    if ((rst.getString("email").contains("@"))
                            || (rst.getString("email").contains("www"))) {
                        email = Utils.acertarTexto(rst.getString("email").trim().replace("'", ""));
                    } else {
                        email = "";
                    }
                } else {
                    email = "";
                }

                fax = "";

                telefone2 = "";

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

                FornecedorVO oFornecedor = new FornecedorVO();

                oFornecedor.codigoanterior = id_fornecedor;
                oFornecedor.datacadastro = datacadastro;
                oFornecedor.razaosocial = razaoSocial;
                oFornecedor.nomefantasia = nomeFantasia;
                oFornecedor.endereco = endereco;
                oFornecedor.bairro = bairro;
                oFornecedor.numero = numero;
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
                vFornecedor.add(oFornecedor);

            }

            return vFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idFornecedor = 0, idProduto = 0;
        String codigoExterno = "";

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select clifor_chave, estitem_chave, cod ");
            sql.append("from fornec_prod");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idFornecedor = Double.parseDouble(Utils.formataNumero(rst.getString("clifor_chave").trim()));
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("estitem_chave").trim()));

                if ((rst.getString("cod") != null)
                        && (!rst.getString("cod").trim().isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("cod").trim().replace("'", ""));
                } else {
                    codigoExterno = "";
                }

                if (codigoExterno.length() > 50) {
                    codigoExterno = codigoExterno.substring(0, 50);
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                oProdutoFornecedor.id_fornecedorDouble = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;
                oProdutoFornecedor.codigoexterno = codigoExterno;
                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long idCliente, cep, cnpj;
        String nome, endereco, bairro, numero, complemento, telefone1, email,
               dataNascimento, dataCadastro, inscricaoEstadual, strEstado;
        int idTipoInscricao, idMunicipio, idEstado, idSexo, idSituacaoCadastro;
        double valorLimite;
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        Utils util = new Utils();

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT C.clifor_chave, C.c_tip, C.c_ins, C.c_des, C.c_fan, C.c_cad, C.data_exclusao, ");
            sql.append("       EN.estado_chave, EN.endereco, EN.numero, EN.cidade, EN.estado_chave, ");
            sql.append("       EN.bairro, EN.complemento, EM.email, TEL.telefone, TEL.contato, ");
            sql.append("       EN.cep, C.c_nas, C.c_lim, C.sexo ");
            sql.append("  FROM CLIFOR C ");
            sql.append("  LEFT join CLIENDERECOS ON CLIENDERECOS.clifor_chave = C.clifor_chave ");
            sql.append("  LEFT JOIN ENDERECOS EN ON EN.id_endereco = CLIENDERECOS.id_endereco ");
            sql.append("  LEFT JOIN CLIEMAIL ON CLIEMAIL.clifor_chave = C.clifor_chave ");
            sql.append("  LEFT JOIN EMAIL EM ON EM.id_email = CLIEMAIL.id_email ");
            sql.append("  LEFT JOIN clitelefones CTEL ON CTEL.clifor_chave = C.clifor_chave ");
            sql.append("  LEFT JOIN telefones TEL ON TEL.id_telefone = CTEL.id_telefone ");
            sql.append(" WHERE C.CLIFOR_TIPO IN (1) ");
            sql.append(" ORDER BY C.clifor_chave ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idCliente = Long.parseLong(Utils.formataNumero(rst.getString("clifor_chave").trim()));
                
                if ("F".equals(rst.getString("c_tip").trim())) {
                    idTipoInscricao = 1;
                } else {
                    idTipoInscricao = 0;
                }
                
                cnpj = Long.parseLong(Utils.formataNumero(rst.getString("clifor_chave").trim()));
                
                if ((rst.getString("c_ins") != null) &&
                        (!rst.getString("c_ins").trim().isEmpty())) {
                    inscricaoEstadual = Utils.acertarTexto(rst.getString("c_ins").trim().replace("'", ""));
                    inscricaoEstadual = inscricaoEstadual.replace("\"", "");
                    inscricaoEstadual = inscricaoEstadual.replace("-", "");
                    inscricaoEstadual = inscricaoEstadual.replace(".", "");
                    inscricaoEstadual = inscricaoEstadual.replace("/", "");
                    inscricaoEstadual = inscricaoEstadual.replace("*", "");
                } else {
                    inscricaoEstadual = "ISENTO";
                }

                if ((rst.getString("c_des") != null)
                        && (!rst.getString("c_des").trim().isEmpty())) {

                    nome = Utils.acertarTexto(rst.getString("c_des").replace("'", "").trim());
                } else {

                    if ((rst.getString("c_fan") != null)
                            && (!rst.getString("c_fan").trim().isEmpty())) {
                        nome = Utils.acertarTexto(rst.getString("c_fan").replace("'", "").trim());
                    } else {
                        nome = "CLIENTE SEM NOME";
                    }
                }
                
                if ((rst.getString("endereco") != null) &&
                        (!rst.getString("endereco").trim().isEmpty())) {
                    endereco = Utils.acertarTexto(rst.getString("endereco").trim().replace("'", ""));
                } else {
                    endereco = "";
                }
                
                if ((rst.getString("bairro") != null) &&
                        (!rst.getString("bairro").trim().isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }
                
                if ((rst.getString("numero") != null) &&
                        (!rst.getString("numero").trim().isEmpty())) {
                    numero = Utils.acertarTexto(rst.getString("numero").trim().replace("'", ""));
                } else {
                    numero = "0";
                }
                
                if ((rst.getString("complemento") != null) &&
                        (!rst.getString("complemento").trim().isEmpty())) {
                    complemento = Utils.acertarTexto(rst.getString("complemento").trim().replace("'", ""));
                } else {
                    complemento = "";
                }

                if ((rst.getString("cidade") != null)
                        && (!"".equals(rst.getString("cidade").trim()))) {
                    if ((rst.getString("estado_chave") != null)
                            && (!"".equals(rst.getString("estado_chave").trim()))) {
                        strEstado = Utils.acertarTexto(rst.getString("estado_chave").trim().replace("'", ""));
                    } else {
                        strEstado = "SP";
                    }

                    idMunicipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("cidade")),
                            strEstado);

                    if (idMunicipio == 0) {

                        idMunicipio = Global.idMunicipio;
                    }
                } else {

                    idMunicipio = Global.idMunicipio;
                }

                if ((rst.getString("cep") != null)
                        && (!"".equals(rst.getString("cep").trim()))) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim().replace("'", "")));
                } else {
                    cep = Global.Cep;
                }

                if ((rst.getString("estado_chave") != null)
                        && (!"".equals(rst.getString("estado_chave").trim()))) {
                    idEstado = Utils.retornarEstadoDescricao(
                            Utils.acertarTexto(rst.getString("estado_chave").trim().replace("'", "")));

                    if (idEstado == 0) {
                        idEstado = Global.idEstado;
                    }
                } else {
                    idEstado = Global.idEstado;
                }

                if ((rst.getString("telefone") != null)
                        && (!"".equals(rst.getString("telefone").trim()))) {
                    telefone1 = Utils.formataNumero(rst.getString("telefone").trim().replace("'", ""));
                } else {
                    telefone1 = "0000000000";
                }
                
                if ((rst.getString("email") != null) &&
                        (!rst.getString("email").trim().isEmpty()) &&
                        (rst.getString("email").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("email").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }
                
                if ((rst.getString("sexo") != null) &&
                        (!rst.getString("sexo").trim().isEmpty())) {
                    
                    if ("M".equals(rst.getString("sexo").trim())) {
                        idSexo = 1;
                    } else {
                        idSexo = 0;
                    }
                } else {
                    idSexo = 1;
                }
                
                if ((rst.getString("c_cad") != null) &&
                        (!rst.getString("c_cad").trim().isEmpty())) {
                    dataCadastro = rst.getString("c_cad").trim().replace(".", "/");
                } else {
                    dataCadastro = "";
                }
                
                if ((rst.getString("c_nas") != null) &&
                        (!rst.getString("c_nas").trim().isEmpty())) {
                    dataNascimento = rst.getString("c_nas").trim().replace(".", "/");
                } else {
                    dataNascimento = "";
                }
                
                valorLimite = rst.getDouble("c_lim");
                
                if ((rst.getString("data_exclusao") != null) &&
                        (!rst.getString("data_exclusao").trim().isEmpty())) {
                    idSituacaoCadastro = 0;
                } else {
                    idSituacaoCadastro = 1;
                }
                
                if (nome.length() > 40) {
                    nome = nome.substring(0, 40);
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

                if (inscricaoEstadual.length() > 18) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 18);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                if (email.length() > 50) {
                    email = email.substring(0, 50);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                oClientePreferencial.idLong = idCliente;
                oClientePreferencial.nome = nome;
                oClientePreferencial.endereco = endereco;
                oClientePreferencial.bairro = bairro;
                oClientePreferencial.id_estado = idEstado;
                oClientePreferencial.id_municipio = idMunicipio;
                oClientePreferencial.cep = cep;
                oClientePreferencial.telefone = telefone1;
                oClientePreferencial.inscricaoestadual = inscricaoEstadual;
                oClientePreferencial.cnpj = cnpj;
                oClientePreferencial.sexo = idSexo;
                oClientePreferencial.datacadastro = dataCadastro;
                oClientePreferencial.email = email;
                oClientePreferencial.valorlimite = valorLimite;
                oClientePreferencial.codigoanterior = idCliente;
                oClientePreferencial.id_situacaocadastro = idSituacaoCadastro;
                oClientePreferencial.datanascimento = dataNascimento;
                oClientePreferencial.numero = numero;
                oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                vClientePreferencial.add(oClientePreferencial);
            }

            return vClientePreferencial;
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
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoCGA();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoDestro(1);
            new MercadologicoDAO().salvarDestro(vMercadologico, true);

            vMercadologico = carregarMercadologicoDestro(2);
            new MercadologicoDAO().salvarDestro(vMercadologico, false);

            vMercadologico = carregarMercadologicoDestro(3);
            new MercadologicoDAO().salvarDestro(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProduto(int id_loja, int id_lojadestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");

            Map<Integer, ProdutoVO> vProduto = carregarProduto(id_loja, id_lojadestino);

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
            produto.salvar(vProdutoNovo, id_loja, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            vProduto = carregarCodigoBarras();

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.addCodigoBarras(vProduto);

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
    
    public void importarPrecoProduto(int idLoja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Produto...");

            vProduto = carregarPrecoProduto(idLoja, id_lojaCliente);
            new ProdutoDAO().alterarPrecoProduto(vProduto, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCustoProduto(int idLojaDestino, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Custo Produtos...");

            vProdutoNovo = carregarCustoProduto(idLojaDestino, id_lojaCliente);

            new ProdutoDAO().alterarCustoProduto(vProdutoNovo, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vEstoqueProduto = new ArrayList<>();

        try {
            vEstoqueProduto = carregarEstoqueProduto(idLoja);

            new ProdutoDAO().alterarEstoqueProduto(vEstoqueProduto, idLoja);
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

    public void importarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vProdutoFornecedor = carregarProdutoFornecedor();

            if (!vProdutoFornecedor.isEmpty()) {
                new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vClientePreferencial = carregarClientePreferencial();

            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);
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

    private int retornarIcms(int cst, double valor, double reducao) {
        int retorno = 8;

        if (cst == 0) {

            if (valor == 7.00) {
                retorno = 0;
            } else if (valor == 12.00) {
                retorno = 1;
            } else if (valor == 18.00) {
                retorno = 2;
            } else if (valor == 25.00) {
                retorno = 3;
            }

        } else if (cst == 10) {
            retorno = 7;
        } else if (cst == 20) {

            if ((valor == 12.00) && (reducao == 10.49)) {
                retorno = 11;
            } else if ((valor == 12.00) && (reducao == 41.67)) {
                retorno = 5;
            } else if ((valor == 18.00) && (reducao == 33.33)) {
                retorno = 9;
            } else if ((valor == 18.00) && (reducao == 61.11)) {
                retorno = 4;
            } else if ((valor == 25.00) && (reducao == 10.49)) {
                retorno = 12;
            } else if ((valor == 25.00) && (reducao == 52.00)) {
                retorno = 10;
            }

        } else if (cst == 30) {
            retorno = 7;
        } else if (cst == 40) {
            retorno = 6;
        } else if (cst == 41) {
            retorno = 17;
        } else if (cst == 50) {
            retorno = 13;
        } else if (cst == 51) {
            retorno = 16;
        } else if (cst == 60) {
            retorno = 7;
        } else if (cst == 70) {
            retorno = 7;
        } else if (cst == 90) {
            retorno = 8;
        }

        return retorno;
    }
}
