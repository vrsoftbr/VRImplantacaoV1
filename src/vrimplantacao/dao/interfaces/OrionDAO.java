package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
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
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class OrionDAO {

    public String Texto;
    private ConexaoDBF connDBF = new ConexaoDBF();

    //CARREGAMENTOS
    
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
            sql.append("select id, id_tipoembalagem, pesavel ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");
            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Double.parseDouble(rst.getString("id"));
                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999) || (rst.getBoolean("pesavel"))) {
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
    
    private List<FamiliaProdutoVO> carregarFamiliaProduto(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao = "";
        int idFamilia = 0;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select distinct codsub, titulograd ");
            sql.append("from ESTOQUE ");
            sql.append("where plu is not null ");
            sql.append("or trim(plu) <> '' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("codsub") != null)
                        && (!rst.getString("codsub").trim().isEmpty())) {

                    idFamilia = Integer.parseInt(rst.getString("codsub").trim());

                    if ((rst.getString("titulograd") != null)
                            && (!rst.getString("titulograd").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("titulograd").trim().replace("'", ""));
                    } else {
                        descricao = "FAMILIA SEM DESCRICAO";
                    }

                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.idLong = idFamilia;
                    oFamiliaProduto.descricao = descricao;
                    oFamiliaProduto.codigoant = idFamilia;

                    vFamiliaProduto.add(oFamiliaProduto);

                }

            }
            return vFamiliaProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(String i_arquivo, int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {
            mercadologico1 = 0;
            mercadologico2 = 0;
            mercadologico3 = 0;

            connDBF.abrirConexao(i_arquivo);
            stmPG = Conexao.createStatement();

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select distinct m1.codsetor cod_m1, m1.setor desc_m1, ");
            sql.append("m2.codgrupo cod_m2, m2.grupo desc_m2, ");
            sql.append("m3.codigo cod_m3, m3.subgrupo desc_m3 ");
            sql.append("from setor m1 ");
            sql.append("left join grupo m2 on m2.codsetor = m1.codsetor ");
            sql.append("left join subgrupo m3 on m3.codgrupo = m2.codgrupo ");
            sql.append("order by m1.codsetor, m2.codgrupo, m3.codigo ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {

                    if ((rst.getString("desc_m1") != null)
                            && (!rst.getString("desc_m1").trim().isEmpty())) {

                        mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                        descricao = Utils.acertarTexto(rst.getString("desc_m1").trim().replace("'", ""));

                        if (descricao.length() > 35) {
                            descricao = descricao.substring(0, 35);
                        }

                        oMercadologico.mercadologico1 = mercadologico1;
                        oMercadologico.mercadologico2 = 0;
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = descricao;
                        oMercadologico.nivel = 1;

                        vMercadologico.add(oMercadologico);
                    }

                } else if (nivel == 2) {

                    if ((rst.getString("desc_m2") != null)
                            && (!rst.getString("desc_m2").trim().isEmpty())) {

                        descricao = Utils.acertarTexto(rst.getString("desc_m2").trim().replace("'", ""));

                        if (descricao.length() > 35) {
                            descricao = descricao.substring(0, 35);
                        }

                        if ((rst.getString("cod_m1") != null)
                                && (!rst.getString("cod_m1").trim().isEmpty())) {

                            mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                        } else {
                            mercadologico1 = 1;
                        }

                        if ((rst.getString("cod_m2") != null) &&
                                (!rst.getString("cod_m2").trim().isEmpty())) {
                            mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                        } else {
                            mercadologico2 = 1;
                        }

                        oMercadologico.mercadologico1 = mercadologico1;
                        oMercadologico.mercadologico2 = mercadologico2;
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = descricao;
                        oMercadologico.nivel = nivel;

                        vMercadologico.add(oMercadologico);
                    }

                } else if (nivel == 3) {

                    if ((rst.getString("desc_m3") != null)
                            && (!rst.getString("desc_m3").trim().isEmpty())) {

                        descricao = Utils.acertarTexto(rst.getString("desc_m3").trim().replace("'", ""));

                        if (descricao.length() > 35) {
                            descricao = descricao.substring(0, 35);
                        }

                        if ((rst.getString("cod_m1") != null)
                                && (!rst.getString("cod_m1").trim().isEmpty())) {
                            mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                        } else {
                            mercadologico1 = 1;
                        }

                        if ((rst.getString("cod_m2") != null)
                                && (!rst.getString("cod_m2").trim().isEmpty())) {
                            mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                        } else {
                            mercadologico2 = 1;
                        }

                        if ((rst.getString("cod_m3") != null) &&
                                (!rst.getString("cod_m3").trim().isEmpty())) {
                            mercadologico3 = Integer.parseInt(rst.getString("cod_m3").trim());
                        } else {
                            mercadologico3 = 1;
                        }

                        oMercadologico.mercadologico1 = mercadologico1;
                        oMercadologico.mercadologico2 = mercadologico2;
                        oMercadologico.mercadologico3 = mercadologico3;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = descricao;
                        oMercadologico.nivel = nivel;

                        vMercadologico.add(oMercadologico);
                    }
                }
            }

            stm.close();
            stmPG.close();
            connDBF.close();

            return vMercadologico;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologicoNivel1(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {
            mercadologico1 = 0;
            mercadologico2 = 0;
            mercadologico3 = 0;

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codgrupo, grupo from grupo ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                descricao = util.acertarTexto(rst.getString("grupo").replace("'", "").trim());

                mercadologico1 = Integer.parseInt(rst.getString("codgrupo").trim());

                MercadologicoVO oMercadologico = new MercadologicoVO();
                oMercadologico.mercadologico1 = mercadologico1;
                oMercadologico.mercadologico2 = mercadologico2;
                oMercadologico.mercadologico3 = mercadologico3;
                oMercadologico.mercadologico4 = 0;
                oMercadologico.mercadologico5 = 0;
                oMercadologico.nivel = 1;
                oMercadologico.descricao = descricao;

                vMercadologico.add(oMercadologico);
            }

            connDBF.close();

            return vMercadologico;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologicoNivel2_3(String i_arquivo, int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {
            mercadologico1 = 0;
            mercadologico2 = 0;
            mercadologico3 = 0;

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, codgrupo, subgrupo from subgrupo ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("subgrupo") != null)
                        && (!rst.getString("subgrupo").trim().isEmpty())) {

                    descricao = util.acertarTexto(rst.getString("subgrupo").replace("'", "").trim());

                    if (nivel == 2) {

                        mercadologico1 = Integer.parseInt(rst.getString("codgrupo").trim());
                        mercadologico2 = Integer.parseInt(rst.getString("codigo").trim());
                        mercadologico3 = 0;

                        MercadologicoVO oMercadologico = new MercadologicoVO();
                        oMercadologico.mercadologico1 = mercadologico1;
                        oMercadologico.mercadologico2 = mercadologico2;
                        oMercadologico.mercadologico3 = mercadologico3;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.nivel = nivel;
                        oMercadologico.descricao = descricao;

                        vMercadologico.add(oMercadologico);

                    } else if (nivel == 3) {
                        mercadologico1 = Integer.parseInt(rst.getString("codgrupo").trim());
                        mercadologico2 = Integer.parseInt(rst.getString("codigo").trim());
                        mercadologico3 = 1;

                        MercadologicoVO oMercadologico = new MercadologicoVO();
                        oMercadologico.mercadologico1 = mercadologico1;
                        oMercadologico.mercadologico2 = mercadologico2;
                        oMercadologico.mercadologico3 = mercadologico3;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.nivel = nivel;
                        oMercadologico.descricao = descricao;

                        vMercadologico.add(oMercadologico);

                    }
                }
            }

            connDBF.close();

            return vMercadologico;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProduto(String i_arquivo) throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins = 0, idTipoPisCofinsCredito = 0, tipoNaturezaReceita = 0,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto = 0, validade,
                pisCofinsDebitoAnt, pisCofinsCreditoAnt, cont = 0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro,
                strSitTrib, valorIcms, strReducao;
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custoComImposto, custoSemImposto, margem, estoque;

        try {
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();
            stmPG = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.codgru, e.codsubgru, e.nome, e.descricao, e.gondola, e.custo, ");
            sql.append("       e.classfis, e.sittribut, e.icms, e.unidade, e.inclusao, e.piscst, e.cofinscst, ");
            sql.append("       e.vendavare, l.codigo, e.ultprecust, e.lucrovare, l.qtde, e.reducao, e.quantfisc, ");
            sql.append("       e.custobase, e.gradeum, e.gradedois, e.codsetor, e.codsub, e.ultprecust, ");
            sql.append("((e.ultprecust - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto ");
            sql.append("from estoque e ");
            sql.append("left join ligplu l on e.plu = l.plu ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                if ((rst.getString("plu") != null)
                        && (!rst.getString("plu").trim().isEmpty())
                        && (!"0".equals(rst.getString("plu").trim()))) {

                    eBalanca = false;
                    codigoBalanca = 0;
                    pesavel = false;
                    referencia = -1;
                    validade = 0;
                    ncmAtual = "";
                    idSituacaoCadastro = 1;

                    if ((rst.getString("unidade") != null)
                            && (!rst.getString("unidade").trim().isEmpty())) {

                        if ("Kg".equals(rst.getString("unidade").trim())) {
                            idTipoEmbalagem = 4;
                        } else {
                            idTipoEmbalagem = 0;
                        }
                    } else {
                        idTipoEmbalagem = 0;
                    }

                    if ((rst.getString("codsub") != null)
                            && (!rst.getString("codsub").trim().isEmpty())) {
                        idFamilia = Integer.parseInt(rst.getString("codsub").trim());

                        sql = new StringBuilder();
                        sql.append("select id from familiaproduto ");
                        sql.append("where id = " + idFamilia);

                        rstPG = stmPG.executeQuery(sql.toString());

                        if (!rstPG.next()) {
                            idFamilia = -1;
                        }

                    } else {
                        idFamilia = -1;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        strCodigoBarras = util.formataNumero(rst.getString("codigo").trim());
                    } else {
                        strCodigoBarras = "";
                    }

                    idProduto = Integer.parseInt(rst.getString("plu"));

                    if (!"".equals(strCodigoBarras)) {
                        if (Double.parseDouble(strCodigoBarras) < 1000000) {
                            sql = new StringBuilder();
                            sql.append("select codigo, descricao, validade, pesavel ");
                            sql.append("from implantacao.produtobalanca ");
                            sql.append("where codigo = " + Double.parseDouble(strCodigoBarras));

                            rstPG = stmPG.executeQuery(sql.toString());

                            if (rstPG.next()) {

                                eBalanca = true;
                                codigoBalanca = rstPG.getInt("codigo");
                                validade = rstPG.getInt("validade");

                                if ("P".equals(rstPG.getString("pesavel"))) {
                                    idTipoEmbalagem = 4;
                                    pesavel = false;
                                } else {
                                    pesavel = true;
                                    idTipoEmbalagem = 0;
                                }
                            }
                        }
                    }

                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        descriaoCompleta = util.acertarTexto(rst.getString("nome").trim().replace("'", ""));
                    } else {
                        descriaoCompleta = "";
                    }

                    if ((rst.getString("descricao") != null)
                            && (!rst.getString("descricao").trim().isEmpty())) {
                        descricaoReduzida = util.acertarTexto(rst.getString("descricao").trim().replace("'", ""));
                    } else {
                        descricaoReduzida = "";
                    }

                    if ((rst.getString("gondola") != null)
                            && (!rst.getString("gondola").trim().isEmpty())) {
                        descricaoGondola = util.acertarTexto(rst.getString("gondola").trim().replace("'", ""));
                    } else {
                        descricaoGondola = "";
                    }

                    if ((rst.getString("classfis") != null)
                            && (!rst.getString("classfis").trim().isEmpty())) {

                        ncmAtual = util.formataNumero(rst.getString("classfis").trim());

                        NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                        ncm1 = oNcm.ncm1;
                        ncm2 = oNcm.ncm2;
                        ncm3 = oNcm.ncm3;

                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    if ((rst.getString("vendavare") != null)
                            && (!rst.getString("vendavare").trim().isEmpty())) {
                        precoVenda = Double.parseDouble(rst.getString("vendavare").trim());
                    } else {
                        precoVenda = 0;
                    }

                    if ((rst.getString("ultprecust") != null)
                            && (!rst.getString("ultprecust").trim().isEmpty())) {
                        custoSemImposto = Double.parseDouble(rst.getString("ultprecust").trim());
                    } else {
                        custoSemImposto = 0;
                    }

                    if ((rst.getString("custocomimposto") != null)
                            && (!rst.getString("custocomimposto").trim().isEmpty())) {
                        custoComImposto = Double.parseDouble(rst.getString("custocomimposto").trim());
                    } else {
                        custoComImposto = 0;
                    }

                    if ((rst.getString("lucrovare") != null)
                            && (!rst.getString("lucrovare").trim().isEmpty())) {
                        margem = Double.parseDouble(rst.getString("lucrovare").trim());
                    } else {
                        margem = 0;
                    }

                    if ((rst.getString("quantfisc") != null)
                            && (!rst.getString("quantfisc").trim().isEmpty())) {
                        estoque = Double.parseDouble(rst.getString("quantfisc").trim());
                    } else {
                        estoque = 0;
                    }

                    dataCadastro = "";

                    if ((rst.getString("codsetor") != null)
                            && (!rst.getString("codsetor").trim().isEmpty())) {
                        mercadologico1 = Integer.parseInt(rst.getString("codsetor").trim());
                    } else {
                        mercadologico1 = -1;
                    }

                    if ((rst.getString("codgru") != null)
                            && (!rst.getString("codgru").trim().isEmpty())) {
                        mercadologico2 = Integer.parseInt(rst.getString("codgru").trim());
                    } else {
                        mercadologico2 = -1;
                    }

                    if ((rst.getString("codsubgru") != null)
                            && (!rst.getString("codsubgru").trim().isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("codsubgru").trim());
                    } else {
                        mercadologico3 = -1;
                    }

                    sql = new StringBuilder();
                    sql.append("select * from mercadologico ");
                    sql.append("where mercadologico1 = " + mercadologico1 + " ");
                    sql.append("and mercadologico2 = " + mercadologico2 + " ");
                    sql.append("and mercadologico3 = " + mercadologico3 + " ");

                    rstPG = stmPG.executeQuery(sql.toString());

                    if (!rstPG.next()) {

                        sql = new StringBuilder();
                        sql.append("select max(mercadologico1) as mercadologico1 ");
                        sql.append("from mercadologico ");

                        rstPG = stmPG.executeQuery(sql.toString());

                        if (rstPG.next()) {
                            mercadologico1 = rstPG.getInt("mercadologico1");
                            mercadologico2 = 1;
                            mercadologico3 = 1;
                        }
                    }

                    if ((rst.getString("qtde") != null)
                            && (!rst.getString("qtde").trim().isEmpty())) {
                        qtdEmbalagem = Integer.parseInt(rst.getString("qtde").trim().substring(0,
                                rst.getString("qtde").trim().length() - 4));
                    } else {
                        qtdEmbalagem = 1;
                    }

                    if (idTipoEmbalagem == 4) {
                        qtdEmbalagem = 1;
                    }

                    // codigobarras
                    if (eBalanca) {
                        codigoBarras = idProduto;
                    } else {

                        if (!strCodigoBarras.isEmpty()) {

                            codigoBarras = Long.parseLong(strCodigoBarras);

                            if (String.valueOf(codigoBarras).length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else if (String.valueOf(codigoBarras).length() < 7) {

                                codigoBarras = -1;
                            }

                        } else {

                            codigoBarras = -1;
                        }
                    }

                    if ((rst.getString("piscst") != null)
                            && (!rst.getString("piscst").trim().isEmpty())) {
                        idTipoPisCofins = Utils.retornarPisCofinsDebito2(Integer.parseInt(rst.getString("piscst").trim()));
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                        pisCofinsDebitoAnt = Integer.parseInt(rst.getString("piscst").trim());
                    } else {
                        idTipoPisCofins = 1;
                        tipoNaturezaReceita = 999;
                        pisCofinsDebitoAnt = -1;
                    }

                    if ((rst.getString("cofinscst") != null)
                            && (!rst.getString("cofinscst").trim().isEmpty())) {
                        idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito2(Integer.parseInt(rst.getString("cofinscst").trim()));
                        pisCofinsCreditoAnt = Integer.parseInt(rst.getString("cofinscst").trim());
                    } else {
                        idTipoPisCofinsCredito = 13;
                        pisCofinsCreditoAnt = -1;
                    }

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (rst.getString("SITTRIBUT").length() >= 2) {
                            strSitTrib = rst.getString("SITTRIBUT").trim().substring(rst.getString("SITTRIBUT").length() - 2);

                            if ((rst.getString("ICMS") != null)
                                    && (!rst.getString("ICMS").trim().isEmpty())) {
                                valorIcms = rst.getString("ICMS").trim().substring(0,
                                        rst.getString("ICMS").trim().length() - 3);
                            } else {
                                valorIcms = "";
                            }

                            if ((rst.getString("reducao") != null)
                                    && (!rst.getString("reducao").trim().isEmpty())) {
                                strReducao = rst.getString("reducao").trim();
                            } else {
                                strReducao = "";
                            }

                            idAliquota = retornarICMS(Integer.parseInt(strSitTrib), valorIcms, strReducao);

                        } else {
                            idAliquota = 8;
                        }
                    } else {
                        idAliquota = 8;
                    }

                    if (descriaoCompleta.length() > 60) {
                        descriaoCompleta = descriaoCompleta.substring(0, 60);
                    }

                    if (descricaoReduzida.length() > 22) {
                        descricaoReduzida = descricaoReduzida.substring(0, 22);
                    }

                    if (descricaoGondola.length() > 60) {
                        descricaoGondola = descricaoGondola.substring(0, 60);
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.descricaoCompleta = descriaoCompleta;
                    oProduto.descricaoReduzida = descricaoReduzida;
                    oProduto.descricaoGondola = descricaoGondola;
                    oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.qtdEmbalagem = qtdEmbalagem;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                    oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                    oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                    oProduto.pesavel = pesavel;
                    oProduto.mercadologico1 = mercadologico1;
                    oProduto.mercadologico2 = mercadologico2;
                    oProduto.mercadologico3 = mercadologico3;
                    oProduto.ncm1 = ncm1;
                    oProduto.ncm2 = ncm2;
                    oProduto.ncm3 = ncm3;
                    oProduto.idFamiliaProduto = idFamilia;
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
                    oProduto.dataCadastro = dataCadastro;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oComplemento.custoComImposto = custoComImposto;
                    oComplemento.custoSemImposto = custoSemImposto;
                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                    oComplemento.estoque = estoque;
                    oProduto.vComplemento.add(oComplemento);

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    oAliquota.idEstado = 35;
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                    oProduto.vAliquota.add(oAliquota);

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;

                    if (eBalanca == true) {
                        oAutomacao.qtdEmbalagem = qtdEmbalagem;
                    } else {

                        if ((rst.getString("qtde") != null)
                                && (!rst.getString("qtde").trim().isEmpty())) {
                            oAutomacao.qtdEmbalagem = Integer.parseInt(rst.getString("qtde").trim().substring(0,
                                    rst.getString("qtde").trim().length() - 4));
                        } else {
                            oAutomacao.qtdEmbalagem = 1;
                        }
                    }

                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;

                    if (!strCodigoBarras.trim().isEmpty()) {
                        oAnterior.barras = Long.parseLong(util.formataNumero(strCodigoBarras).trim());
                    } else {
                        oAnterior.barras = 0;
                    }

                    oProduto.eBalanca = eBalanca;
                    oAnterior.e_balanca = eBalanca;
                    oAnterior.codigobalanca = codigoBalanca;
                    oAnterior.referencia = referencia;
                    oAnterior.piscofinsdebito = pisCofinsDebitoAnt;
                    oAnterior.piscofinscredito = pisCofinsCreditoAnt;

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (!"000".equals(rst.getString("SITTRIBUT").trim())) {
                            oAnterior.ref_icmsdebito = rst.getString("SITTRIBUT").trim();
                        } else {
                            oAnterior.ref_icmsdebito = rst.getString("ICMS").trim();
                        }

                    } else {
                        oAnterior.ref_icmsdebito = "";
                    }

                    if (!ncmAtual.trim().isEmpty()) {
                        oAnterior.ncm = ncmAtual;
                    } else {
                        oAnterior.ncm = "";
                    }

                    oProduto.vCodigoAnterior.add(oAnterior);
                    vProduto.put(oProduto.id, oProduto);
                }
            }
            stm.close();
            stmPG.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double custoSemImposto = 0, custoComImposto = 0;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.ultprecust, e.custobase, e.custo custocomimposto, ");
            sql.append("e.vendavare, e.ultprecust, e.lucrovare, ");
            sql.append("((e.ultprecust - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custosemimposto ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append(" or trim(e.plu) <> '' ");

            //sql.append(" order by cast(e.plu as integer) ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("custosemimposto") != null)
                        && (!rst.getString("custosemimposto").trim().isEmpty())) {
                    custoSemImposto = Double.parseDouble(rst.getString("custosemimposto").trim());
                } else {
                    custoSemImposto = 0;
                }

                if ((rst.getString("custocomimposto") != null)
                        && (!rst.getString("custocomimposto").trim().isEmpty())) {
                    custoComImposto = Double.parseDouble(rst.getString("custocomimposto").trim());
                } else {
                    custoComImposto = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.custoComImposto = custoComImposto;
                oComplemento.custoSemImposto = custoSemImposto;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoVendaProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double precoVenda = 0, margem = 0;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.custo, ");
            sql.append("e.vendavare, e.ultprecust, e.lucrovare ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            //sql.append(" order by cast(e.plu as integer) ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("vendavare") != null)
                        && (!rst.getString("vendavare").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("vendavare").trim());
                } else {
                    precoVenda = 0;
                }

                if ((rst.getString("lucrovare") != null)
                        && (!rst.getString("lucrovare").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("lucrovare").trim());
                } else {
                    margem = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPisCofinsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int idTipoPisCofins = 0,
                idTipoPisCofinsCredito = 0, tipoNaturezaReceita = 0, idProduto,
                pisCofinsDebitoAnt, pisCofinsCreditoAnt;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.piscst, e.cofinscst ");
            sql.append(" from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            //sql.append(" order by e.plu ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("plu") != null)
                        && (!rst.getString("plu").trim().isEmpty())
                        && (!"0".equals(rst.getString("plu").trim()))) {

                    idProduto = Integer.parseInt(rst.getString("plu"));

                    if ((rst.getString("piscst") != null)
                            && (!rst.getString("piscst").trim().isEmpty())) {
                        idTipoPisCofins = retornaPisCofinsDebito(rst.getString("piscst").trim());
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                        pisCofinsDebitoAnt = Integer.parseInt(rst.getString("piscst").trim());
                    } else {
                        idTipoPisCofins = 1;
                        tipoNaturezaReceita = 999;
                        pisCofinsDebitoAnt = -1;
                    }

                    if ((rst.getString("cofinscst") != null)
                            && (!rst.getString("cofinscst").trim().isEmpty())) {
                        idTipoPisCofinsCredito = retornaPisCofinsCredito(rst.getString("cofinscst").trim());
                        pisCofinsCreditoAnt = Integer.parseInt(rst.getString("cofinscst").trim());
                    } else {
                        idTipoPisCofinsCredito = 13;
                        pisCofinsCreditoAnt = -1;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                    oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                    oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;
                    oAnterior.piscofinsdebito = pisCofinsDebitoAnt;
                    oAnterior.piscofinscredito = pisCofinsCreditoAnt;

                    oProduto.vCodigoAnterior.add(oAnterior);

                    vProduto.add(oProduto);

                }
            }

            stm.close();

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarIcmsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int idAliquota, idProduto;
        String strSitTrib, valorIcms, strReducao;
        long codigoBarras = 0;

        try {
            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.sittribut, e.icms, e.reducao ");
            sql.append(" from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            //sql.append(" order by e.plu ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("plu") != null)
                        && (!rst.getString("plu").trim().isEmpty())
                        && (!"0".equals(rst.getString("plu").trim()))) {

                    idProduto = Integer.parseInt(rst.getString("plu"));

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (rst.getString("SITTRIBUT").length() >= 2) {
                            strSitTrib = rst.getString("SITTRIBUT").trim().substring(rst.getString("SITTRIBUT").length() - 2);

                            if ((rst.getString("ICMS") != null)
                                    && (!rst.getString("ICMS").trim().isEmpty())) {
                                valorIcms = rst.getString("ICMS").trim().substring(0,
                                        rst.getString("ICMS").trim().length() - 3);
                            } else {
                                valorIcms = "";
                            }

                            if ((rst.getString("reducao") != null)
                                    && (!rst.getString("reducao").trim().isEmpty())) {
                                strReducao = rst.getString("reducao").trim();
                            } else {
                                strReducao = "";
                            }

                            idAliquota = retornarICMS(Integer.parseInt(strSitTrib), valorIcms, strReducao);

                        } else {
                            idAliquota = 8;
                        }
                    } else {
                        idAliquota = 8;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    oAliquota.idEstado = 35;
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                    oProduto.vAliquota.add(oAliquota);

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (!"000".equals(rst.getString("SITTRIBUT").trim())) {
                            oAnterior.ref_icmsdebito = rst.getString("SITTRIBUT").trim();
                        } else {
                            oAnterior.ref_icmsdebito = rst.getString("ICMS").trim();
                        }

                    } else {
                        oAnterior.ref_icmsdebito = "";
                    }

                    oProduto.vCodigoAnterior.add(oAnterior);

                    vProduto.add(oProduto);

                }
            }

            stm.close();

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro, Numero = "", Telefone = "",
                telefone2, telefone3, email;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT codigo, nome, razao, inscest, cgc, rua, casa, edificio, sala, ");
            sql.append("cidade, bairro, cep, estado, inclusao, email, contato, contatcom, telefone1, ");
            sql.append("telefone2, telefone3, obs ");
            sql.append("FROM FORNECE order by codigo ");
            rst = stm.executeQuery(sql.toString());
            Linha = 0;

            try {

                while (rst.next()) {

                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("codigo");

                    Linha++;

                    if ((rst.getString("razao") != null)
                            && (!rst.getString("razao").isEmpty())) {
                        byte[] bytes = rst.getBytes("razao");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));

                        if (String.valueOf(cnpj).length() > 11) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }

                    } else {
                        cnpj = -1;
                        id_tipoinscricao = 0;
                    }

                    if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("inscest").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    id_tipoinscricao = 0;

                    if ((rst.getString("rua") != null)
                            && (!rst.getString("rua").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("rua").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if (rst.getString("casa") != null) {
                        Numero = rst.getString("casa").trim();
                        if (Numero.length() > 6) {
                            Numero = Numero.substring(0, 6);
                        }
                    } else {
                        Numero = "0";
                    }

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = Global.Cep;
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").isEmpty())) {

                        if ((rst.getString("estado") != null)
                                && (!rst.getString("estado").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if ((rst.getString("telefone1") != null)
                            && (!rst.getString("telefone1").trim().isEmpty())) {
                        Telefone = util.formataNumero(rst.getString("telefone1"));
                    } else {
                        Telefone = "0000000000";
                    }

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("telefone2"));
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                    } else {
                        telefone3 = "";
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())
                            && (rst.getString("email").contains("@"))) {
                        email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                    } else {
                        email = "";
                    }

                    if (rst.getString("OBS") != null) {
                        obs = rst.getString("OBS").trim();
                    } else {
                        obs = "";
                    }

                    if (rst.getString("inclusao") != null) {
                        datacadastro = rst.getString("inclusao");
                    } else {
                        datacadastro = "";
                    }

                    ativo = true;

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

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 20) {
                        inscricaoestadual = inscricaoestadual.substring(0, 20);
                    }

                    if (Telefone.length() > 14) {
                        Telefone = Telefone.substring(0, 14);
                    }

                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (telefone3.length() > 14) {
                        telefone3 = telefone3.substring(0, 14);
                    }

                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.numero = Numero;
                    oFornecedor.telefone = Telefone;
                    oFornecedor.bairro = bairro;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;

                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.email = email;

                    vFornecedor.add(oFornecedor);
                }
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorRelatorio(String i_arquivo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, contProdForn = 0, qtdEmbalagem, cont, linha;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        Cell cellCodForn = sheet.getCell(1, i);
                        Cell cellCodExtr = sheet.getCell(2, i);
                        Cell cellCodProd = sheet.getCell(3, i);
                        Cell cellQtde = sheet.getCell(4, i);

                        idFornecedor = Integer.parseInt(cellCodForn.getContents().trim());
                        idProduto = Integer.parseInt(cellCodProd.getContents().trim());

                        if ((cellCodExtr.getContents() != null)
                                && (!cellCodExtr.getContents().trim().isEmpty())) {
                            codigoExterno = util.acertarTexto(cellCodExtr.getContents().trim().replace("'", ""));
                        } else {
                            codigoExterno = "";
                        }

                        qtdEmbalagem = Integer.parseInt(cellQtde.getContents().trim());

                        ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                        oProdutoFornecedor.id_fornecedor = idFornecedor;
                        oProdutoFornecedor.id_produto = idProduto;
                        oProdutoFornecedor.codigoexterno = codigoExterno;
                        oProdutoFornecedor.qtdembalagem = qtdEmbalagem;

                        vProdutoFornecedor.add(oProdutoFornecedor);

                    }
                }

            } catch (Exception e) {
                throw e;
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT CODFOR, CODINT, PLU, QTDE FROM LIGFAB");
            rst = stm.executeQuery(sql.toString());

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CODFOR") != null)
                        && (!rst.getString("CODFOR").trim().isEmpty())
                        && (rst.getString("CODINT") != null)
                        && (!rst.getString("CODINT").trim().isEmpty())
                        && (rst.getString("PLU") != null)
                        && (!rst.getString("PLU").trim().isEmpty())) {

                    idFornecedor = rst.getInt("CODFOR");
                    idProduto = rst.getInt("PLU");

                    if ((rst.getString("QTDE") != null)
                            && (!rst.getString("QTDE").trim().isEmpty())) {
                        qtdEmbalagem = (int) rst.getDouble("QTDE");
                    } else {
                        qtdEmbalagem = 1;
                    }

                    if ((rst.getString("CODINT") != null)
                            && (!rst.getString("CODINT").isEmpty())) {
                        codigoExterno = util.acertarTexto(rst.getString("CODINT").replace("'", ""));
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
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClienteEventualVO> carregarClienteEventual(String i_arquivo) throws Exception {
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, idEstado, idMunicipio = 0, idTipoInscricao;
        String nome, endereco, bairro, cidade, estado, telefone1, telefone2,
                telefone3, email, dataNascimento, inscricaoEstadual, numero,
                complemento = "", dataCadastro, contato;
        long cnpj, cep = 0;
        double valorLimite;

        try {
            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, nome, razao, nascimento, inscest, cgc, cic, saldo, ");
            sql.append("rua, casa, edificio, apto, cidade, bairro, cep, estado, email, abertura, ");
            sql.append("contato, telefone1, telefone2, telefone3 ");
            sql.append("from cliente ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idCliente = Integer.parseInt(rst.getString("codigo").trim());

                nome = util.acertarTexto(rst.getString("razao").trim().replace("'", ""));

                if ((rst.getString("nascimento") != null)
                        && (!rst.getString("nascimento").trim().isEmpty())) {
                    dataNascimento = rst.getString("nascimento").trim();
                } else {
                    dataNascimento = "";
                }

                if ((rst.getString("inscest") != null)
                        && (!rst.getString("inscest").trim().isEmpty())) {
                    inscricaoEstadual = util.acertarTexto(rst.getString("inscest").trim().replace("'", ""));
                } else {
                    inscricaoEstadual = "ISENTO";
                }

                if ((rst.getString("cgc") != null)
                        && (!rst.getString("cgc").trim().isEmpty())) {

                    cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));

                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                } else {

                    if ((rst.getString("cic") != null)
                            && (!rst.getString("cic").trim().isEmpty())) {

                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cic").trim()));

                        if (String.valueOf(cnpj).length() > 11) {
                            idTipoInscricao = 0;
                        } else {
                            idTipoInscricao = 1;
                        }

                    } else {
                        cnpj = idCliente;
                        idTipoInscricao = 1;
                    }
                }

                if ((rst.getString("saldo") != null)
                        && (!rst.getString("saldo").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("saldo").trim());
                } else {
                    valorLimite = 0;
                }

                if ((rst.getString("rua") != null)
                        && (!rst.getString("rua").trim().isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("rua").trim().replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("casa") != null)
                        && (!rst.getString("casa").trim().isEmpty())) {
                    numero = util.acertarTexto(rst.getString("casa").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("bairro") != null)
                        && (!rst.getString("bairro").trim().isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("edificio") != null)
                        && (!rst.getString("edificio").trim().isEmpty())) {

                    complemento = util.acertarTexto(rst.getString("edificio").trim()).trim().replace("'", "");

                    if ((rst.getString("apto") != null)
                            && (!rst.getString("apto").trim().isEmpty())) {

                        complemento = util.acertarTexto(rst.getString("edificio").trim() + " " + rst.getString("apto")).trim().replace("'", "");
                    }
                } else {
                    complemento = "";
                }

                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").isEmpty())) {

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {

                        idMunicipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = 3538709;
                        }
                    }
                } else {
                    idMunicipio = 3538709;
                }

                if ((rst.getString("estado") != null)
                        && (!rst.getString("estado").isEmpty())) {
                    idEstado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = 35;
                    }
                } else {
                    idEstado = 35;
                }

                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                } else {
                    email = "";
                }

                if ((rst.getString("abertura") != null)
                        && (!rst.getString("abertura").trim().isEmpty())) {
                    dataCadastro = rst.getString("abertura").trim();
                } else {
                    dataCadastro = "";
                }

                if ((rst.getString("contato") != null)
                        && (!rst.getString("contato").trim().isEmpty())) {
                    contato = util.acertarTexto(rst.getString("contato").trim().replace("'", ""));
                } else {
                    contato = "";
                }

                if ((rst.getString("telefone1") != null)
                        && (!rst.getString("telefone1").trim().isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("telefone1").trim());
                } else {
                    telefone1 = "0000000000";
                }

                if ((rst.getString("telefone2") != null)
                        && (!rst.getString("telefone2").trim().isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("telefone2").trim());
                } else {
                    telefone2 = "";
                }

                if ((rst.getString("telefone3") != null)
                        && (!rst.getString("telefone3").trim().isEmpty())) {
                    telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                } else {
                    telefone3 = "";
                }

                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().replace("'", "").toLowerCase());
                } else {
                    email = "";
                }

                if ((rst.getString("cep") != null)
                        && (!rst.getString("cep").trim().isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                } else {
                    cep = 0;
                }

                if (nome.length() > 60) {
                    nome = nome.substring(0, 60);
                }

                if (endereco.length() > 50) {
                    endereco = endereco.substring(0, 50);
                }

                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }

                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (telefone2.length() > 14) {
                    telefone2 = telefone2.substring(0, 14);
                }

                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }

                if (inscricaoEstadual.length() > 20) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 20);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                ClienteEventualVO oClienteEventual = new ClienteEventualVO();
                oClienteEventual.id = idCliente;
                oClienteEventual.nome = nome;
                oClienteEventual.endereco = endereco;
                oClienteEventual.bairro = bairro;
                oClienteEventual.id_estado = idEstado;
                oClienteEventual.telefone = telefone1;
                oClienteEventual.id_tipoinscricao = idTipoInscricao;
                oClienteEventual.inscricaoestadual = inscricaoEstadual;
                oClienteEventual.id_situacaocadastro = 1;
                oClienteEventual.cnpj = cnpj;
                oClienteEventual.fax = "";
                oClienteEventual.datacadastro = dataCadastro;
                oClienteEventual.limitecompra = valorLimite;
                oClienteEventual.id_municipio = idMunicipio;
                oClienteEventual.cep = cep;
                oClienteEventual.numero = numero;
                oClienteEventual.complemento = complemento;

                vClienteEventual.add(oClienteEventual);

            }

            return vClienteEventual;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, idEstado, idMunicipio = 0, idTipoInscricao;
        String nome, endereco, bairro, cidade, estado, telefone1, telefone2,
                telefone3, email, dataNascimento, inscricaoEstadual, numero,
                complemento = "", dataCadastro, contato, observacao = "",
                empresa, cargo, pai, mae;
        long cnpj, cep = 0;
        double valorLimite, salario;

        try {
            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, nome, razao, nascimento, inscest, cgc, cic, ");
            sql.append("firma, cargo, salario, saldo, pai, mae, ");
            sql.append("rua, casa, edificio, apto, cidade, bairro, cep, estado, email, abertura, ");
            sql.append("contato, telefone1, telefone2, telefone3, contatcom, rg ");
            sql.append("from cliente ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idCliente = Integer.parseInt(rst.getString("codigo").trim());

                nome = util.acertarTexto(rst.getString("razao").trim().replace("'", ""));

                if ((rst.getString("nascimento") != null)
                        && (!rst.getString("nascimento").trim().isEmpty())) {
                    dataNascimento = rst.getString("nascimento").trim();
                } else {
                    dataNascimento = "";
                }

                if ((rst.getString("inscest") != null)
                        && (!rst.getString("inscest").trim().isEmpty())) {
                    inscricaoEstadual = util.acertarTexto(rst.getString("inscest").trim().replace("'", ""));
                } else {

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        inscricaoEstadual = util.acertarTexto(rst.getString("rg").trim().replace("'", ""));
                    } else {
                        inscricaoEstadual = "ISENTO";
                    }
                }

                if ((rst.getString("cgc") != null)
                        && (!rst.getString("cgc").trim().isEmpty())) {

                    cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));

                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                } else {

                    if ((rst.getString("cic") != null)
                            && (!rst.getString("cic").trim().isEmpty())) {

                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cic").trim()));

                        if (String.valueOf(cnpj).length() > 11) {
                            idTipoInscricao = 0;
                        } else {
                            idTipoInscricao = 1;
                        }

                    } else {
                        cnpj = idCliente;
                        idTipoInscricao = 1;
                    }
                }

                if ((rst.getString("saldo") != null)
                        && (!rst.getString("saldo").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("saldo").trim());
                } else {
                    valorLimite = 0;
                }

                if ((rst.getString("rua") != null)
                        && (!rst.getString("rua").trim().isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("rua").trim().replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("casa") != null)
                        && (!rst.getString("casa").trim().isEmpty())) {
                    numero = util.acertarTexto(rst.getString("casa").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("bairro") != null)
                        && (!rst.getString("bairro").trim().isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("edificio") != null)
                        && (!rst.getString("edificio").trim().isEmpty())) {

                    complemento = util.acertarTexto(rst.getString("edificio").trim()).trim().replace("'", "");

                    if ((rst.getString("apto") != null)
                            && (!rst.getString("apto").trim().isEmpty())) {

                        complemento = util.acertarTexto(rst.getString("edificio").trim() + " " + rst.getString("apto")).trim().replace("'", "");
                    }
                } else {
                    complemento = "";
                }

                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").isEmpty())) {

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {

                        idMunicipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = 3538709;
                        }
                    }
                } else {
                    idMunicipio = 3538709;
                }

                if ((rst.getString("estado") != null)
                        && (!rst.getString("estado").isEmpty())) {
                    idEstado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = 35;
                    }
                } else {
                    idEstado = 35;
                }

                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                } else {
                    email = "";
                }

                if ((rst.getString("abertura") != null)
                        && (!rst.getString("abertura").trim().isEmpty())) {
                    dataCadastro = rst.getString("abertura").trim();
                } else {
                    dataCadastro = "";
                }

                if ((rst.getString("contato") != null)
                        && (!rst.getString("contato").trim().isEmpty())) {
                    contato = util.acertarTexto(rst.getString("contato").trim().replace("'", ""));
                } else {
                    contato = "";
                }

                if ((rst.getString("telefone1") != null)
                        && (!rst.getString("telefone1").trim().isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("telefone1").trim());
                } else {
                    telefone1 = "0000000000";
                }

                if ((rst.getString("telefone2") != null)
                        && (!rst.getString("telefone2").trim().isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("telefone2").trim());
                } else {
                    telefone2 = "";
                }

                if ((rst.getString("telefone3") != null)
                        && (!rst.getString("telefone3").trim().isEmpty())) {
                    telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                } else {
                    telefone3 = "";
                }

                if ((rst.getString("telefone3") != null)
                        && (!rst.getString("telefone3").trim().isEmpty())) {
                    telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                } else {
                    telefone3 = "";
                }

                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().replace("'", "").toLowerCase());
                } else {
                    email = "";
                }

                if ((rst.getString("cep") != null)
                        && (!rst.getString("cep").trim().isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                } else {
                    cep = 0;
                }

                if ((rst.getString("contatcom") != null)
                        && (!rst.getString("contatcom").trim().isEmpty())) {
                    observacao = "CONTATO COM.: " + util.acertarTexto(rst.getString("contatcom").replace("'", "").trim());
                } else {
                    observacao = "";
                }

                if ((rst.getString("firma") != null)
                        && (!rst.getString("firma").trim().isEmpty())) {
                    empresa = util.acertarTexto(rst.getString("firma").trim());
                } else {
                    empresa = "";
                }

                if ((rst.getString("cargo") != null)
                        && (!rst.getString("cargo").trim().isEmpty())) {
                    cargo = util.acertarTexto(rst.getString("cargo").trim());
                } else {
                    cargo = "";
                }

                if ((rst.getString("salario") != null)
                        && (!rst.getString("salario").trim().isEmpty())) {
                    salario = Double.parseDouble(rst.getString("salario").trim());
                } else {
                    salario = 0;
                }

                if ((rst.getString("pai") != null)
                        && (!rst.getString("pai").trim().isEmpty())) {
                    pai = util.acertarTexto(rst.getString("pai").trim().replace("'", ""));
                } else {
                    pai = "";
                }

                if ((rst.getString("mae") != null)
                        && (!rst.getString("mae").trim().isEmpty())) {
                    mae = util.acertarTexto(rst.getString("mae").trim().replace("'", ""));
                } else {
                    mae = "";
                }

                if (nome.length() > 40) {
                    nome = nome.substring(0, 40);
                }

                if (endereco.length() > 50) {
                    endereco = endereco.substring(0, 50);
                }

                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }

                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (telefone2.length() > 14) {
                    telefone2 = telefone2.substring(0, 14);
                }

                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }

                if (inscricaoEstadual.length() > 20) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 20);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                if (pai.length() > 40) {
                    pai = pai.substring(0, 40);
                }

                if (mae.length() > 40) {
                    mae = mae.substring(0, 40);
                }

                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                oClientePreferencial.id = idCliente;
                oClientePreferencial.nome = nome;
                oClientePreferencial.endereco = endereco;
                oClientePreferencial.bairro = bairro;
                oClientePreferencial.id_estado = idEstado;
                oClientePreferencial.telefone = telefone1;
                oClientePreferencial.telefone2 = telefone2;
                oClientePreferencial.telefone3 = telefone3;
                oClientePreferencial.email = email;
                oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                oClientePreferencial.inscricaoestadual = inscricaoEstadual;
                oClientePreferencial.id_situacaocadastro = 1;
                oClientePreferencial.cnpj = cnpj;
                oClientePreferencial.fax = "";
                oClientePreferencial.datacadastro = dataCadastro;
                oClientePreferencial.valorlimite = valorLimite;
                oClientePreferencial.id_municipio = idMunicipio;
                oClientePreferencial.cep = cep;
                oClientePreferencial.numero = numero;
                oClientePreferencial.complemento = complemento;
                oClientePreferencial.observacao = observacao;

                vClientePreferencial.add(oClientePreferencial);

            }

            return vClientePreferencial;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarDataCadastroProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        String dataCadastro;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.inclusao ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            //sql.append(" order by cast(e.plu as integer) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("inclusao") != null)
                        && (!rst.getString("inclusao").trim().isEmpty())) {
                    dataCadastro = rst.getString("inclusao").trim();
                } else {
                    dataCadastro = "";
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.dataCadastro = dataCadastro;

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double estoque = 0;

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.quantfisc ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            //sql.append(" order by cast(e.plu as integer) ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("quantfisc") != null)
                        && (!rst.getString("quantfisc").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("quantfisc").trim());
                } else {
                    estoque = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, codigocli, terminal  ");
            sql.append("from receber ");
            sql.append("where vlrpago = 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("codigocli") != null)
                        && (!rst.getString("codigocli").trim().isEmpty())) {

                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR";
                    juros = 0;

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);

                }
            }

            return vReceberCreditoRotativo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoBaixado(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros, valorPago;
        String observacao, dataemissao, datavencimento, dataPagamento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            connDBF.abrirConexao(i_arquivo);

            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, vlrpago, ");
            sql.append("codigocli, terminal, pagamento ");
            sql.append("from receber ");
            sql.append("where vlrpago > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("codigocli") != null)
                        && (!rst.getString("codigocli").trim().isEmpty())
                        && (rst.getString("pagamento") != null)
                        && (!rst.getString("pagamento").trim().isEmpty())) {

                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR => CONTA BAIXADA";
                    juros = 0;

                    dataPagamento = rst.getString("pagamento").trim();
                    valorPago = Double.parseDouble(rst.getString("vlrpago").trim());

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;
                    oReceberCreditoRotativo.dataPagamento = dataPagamento;
                    oReceberCreditoRotativo.valorPago = valorPago;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);

                }
            }

            return vReceberCreditoRotativo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    //IMPORTAÇÕES
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFamiliaProduto(String i_arquivo) throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Família Produto...");

            vFamiliaProduto = carregarFamiliaProduto(i_arquivo);
            FamiliaProdutoDAO familiaProduto = new FamiliaProdutoDAO();
            familiaProduto.verificarCodigo = true;
            familiaProduto.salvar(vFamiliaProduto);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...mercadológico nível 1...");
            vMercadologico = carregarMercadologico(i_arquivo, 1);
            new MercadologicoDAO().salvar2(vMercadologico, true);

            ProgressBar.setStatus("Carregando dados...mercadológico nível 2...");
            vMercadologico = carregarMercadologico(i_arquivo, 2);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            ProgressBar.setStatus("Carregando dados...mercadológico nível 3...");
            vMercadologico = carregarMercadologico(i_arquivo, 3);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(i_arquivo);
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
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLoja, vLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoManterBalanca(String i_arquivo, int idLojaVR) throws Exception {

        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProduto(i_arquivo).values()) {
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

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }

    public void importarCusto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoCusto = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Custo...Produtos...");
            vProdutoCusto = carregarCustoProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoCusto.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarCustoProduto(vProdutoCusto, idLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarPrecoVenda(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoPrecoVenda = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Preço Venda...Produtos...");
            vProdutoPrecoVenda = carregarPrecoVendaProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoPrecoVenda.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarPrecoProduto(vProdutoPrecoVenda, idLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Long, ProdutoVO> carregarCodigoBarras(String i_arquivo) throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        long codigoBarras;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select plu, codigo, qtde "
                    + "from ligplu "
                    + "where codigo is not null "
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();                                        
                    codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("codigo").trim()));                    
                    if (codigoBarras > 999999) {                    
                        oProduto.setId(rst.getInt("plu"));
                        oAutomacao.setCodigoBarras(codigoBarras);
                        oAutomacao.setQtdEmbalagem(rst.getInt("qtde"));
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.put(codigoBarras, oProduto);
                    }
                }
            }
        }
        return vResult;
    }
    
    public void importarCodigoBarra(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarras(i_arquivo);
            ProgressBar.setMaximum(vEstoqueProduto.size());
            for (Long keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = false;
            produto.verificarLoja = true;
            produto.id_loja = idLoja;
            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarEstoque(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoEstoque = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Preço Venda...Produtos...");
            vProdutoEstoque = carregarEstoqueProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoEstoque.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarEstoqueProduto(vProdutoEstoque, idLoja);;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarPisCofins(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Pis Cofins...");
            List<ProdutoVO> vProduto = carregarPisCofinsProduto(i_arquivo);

            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarIcms(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Icms...");
            List<ProdutoVO> vProduto = carregarIcmsProduto(i_arquivo);

            new ProdutoDAO().alterarICMSProduto(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor(String i_arquivo) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor(i_arquivo);

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }

    }

    public void importarProdutoFornecedor(String i_arquivo) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteEventual(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Eventual...");
            List<ClienteEventualVO> vClienteEventual = carregarClienteEventual(i_arquivo);

            new ClienteEventuallDAO().acertarCnpj(vClienteEventual);//salvar(vClienteEventual);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(i_arquivo);

            new PlanoDAO().salvar(1);
            new ClientePreferencialDAO().salvar(vClientePreferencial, 1, 1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarDataCadastroProduto(String i_arquivo) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...data cadastro Produtos...");
            List<ProdutoVO> vProduto = carregarDataCadastroProduto(i_arquivo);

            new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(String i_arquivo, int idLoja) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(i_arquivo, idLoja);

            new ReceberCreditoRotativoDAO().salvar(vReceberCreditoRotativo, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativoBaixado(String i_arquivo, int idLoja) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo Baixado...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativoBaixado(i_arquivo, idLoja);

            new ReceberCreditoRotativoDAO().salvarContaBaixada(vReceberCreditoRotativo, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private int retornarICMS(int codSitTrib, String valor, String reducao) {
        int retorno = 8;

        if (codSitTrib == 0) {

            if ("7".equals(valor)) {
                retorno = 0;
            } else if ("12".equals(valor)) {
                retorno = 1;
            } else if ("18".equals(valor)) {
                retorno = 2;
            } else if ("25".equals(valor)) {
                retorno = 3;
            }

        } else if ((codSitTrib == 10) || (codSitTrib == 30)
                || (codSitTrib == 60) || (codSitTrib == 70)) {
            retorno = 7;
        } else if (codSitTrib == 20) {

            if (("12".equals(valor))
                    && ("41.67").equals(reducao)) {
                retorno = 5;
            } else if (("18".equals(valor))
                    && ("33.33".equals(reducao))) {
                retorno = 9;
            } else if (("18".equals(valor))
                    && ("61.11".equals(reducao))) {
                retorno = 4;
            } else if (("25".equals(valor))
                    && ("52.00".equals(reducao))) {
                retorno = 10;
            }

        } else if (codSitTrib == 40) {
            retorno = 6;
        } else if (codSitTrib == 41) {
            retorno = 17;
        } else if (codSitTrib == 50) {
            retorno = 13;
        } else if (codSitTrib == 51) {
            retorno = 16;
        } else if (codSitTrib == 90) {
            retorno = 8;
        } else {
            retorno = 8;
        }

        return retorno;
    }

    private int retornaPisCofinsDebito(String cst) {
        int retorno = 1;

        if ("01".equals(cst)) {
            retorno = 0;
        } else if ("02".equals(cst)) {
            retorno = 5;
        } else if ("03".equals(cst)) {
            retorno = 6;
        } else if ("04".equals(cst)) {
            retorno = 3;
        } else if ("49".equals(cst)) {
            retorno = 9;
        } else if ("05".equals(cst)) {
            retorno = 2;
        } else if ("06".equals(cst)) {
            retorno = 7;
        } else if ("07".equals(cst)) {
            retorno = 1;
        } else if ("08".equals(cst)) {
            retorno = 8;
        } else {
            retorno = 1;
        }

        return retorno;
    }

    private int retornaPisCofinsCredito(String cst) {
        int retorno = 1;

        if ("01".equals(cst)) {
            retorno = 12;
        } else if ("02".equals(cst)) {
            retorno = 17;
        } else if ("03".equals(cst)) {
            retorno = 18;
        } else if ("04".equals(cst)) {
            retorno = 15;
        } else if ("49".equals(cst)) {
            retorno = 21;
        } else if ("05".equals(cst)) {
            retorno = 14;
        } else if ("06".equals(cst)) {
            retorno = 19;
        } else if ("07".equals(cst)) {
            retorno = 13;
        } else if ("08".equals(cst)) {
            retorno = 20;
        } else {
            retorno = 13;
        }

        return retorno;
    }

    
    /* especiais */
    private List<ProdutoVO> carregarAcertarFamiliaProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select plu, codsub "
                    + "from ESTOQUE "
                    + "where plu is not null "
                    + "or trim(plu) <> '' "
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("plu"));
                    if ((rst.getString("codsub") != null) &&
                            (!rst.getString("codsub").trim().isEmpty())) {
                        vo.setIdFamiliaProduto(rst.getInt("codsub"));
                    } else {
                        vo.setIdFamiliaProduto(-1);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarFamiliaProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Familia Produto...");
            vResult = carregarAcertarFamiliaProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarFamiliaProduto_Produto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarAcertarMercadologico(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select e.plu, e.codsetor, e.codgru, e.codsubgru "
                    + "from estoque e "
                    + "where e.plu is not null "
                    + " or trim(e.plu) <> '' "
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("plu"));
                    if ((rst.getString("codsetor") != null) &&
                            (!rst.getString("codsetor").trim().isEmpty())) {
                        vo.setMercadologico1(rst.getInt("codsetor"));
                    } else {
                        vo.setMercadologico1(1);
                    }
                    
                    if ((rst.getString("codgru") != null) &&
                            (!rst.getString("codgru").trim().isEmpty())) {
                        vo.setMercadologico2(rst.getInt("codgru"));
                    } else {
                        vo.setMercadologico2(1);
                    }
                    
                    if ((rst.getString("codsubgru") != null) &&
                            (!rst.getString("codsubgru").trim().isEmpty())) {
                        vo.setMercadologico3(rst.getInt("codsubgru"));
                    } else {
                        vo.setMercadologico3(1);
                    }
                    
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarMercadologicoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResut = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Mercadologico Produto...");
            vResut = carregarAcertarMercadologico(i_arquivo);
            if (!vResut.isEmpty()) {
                new ProdutoDAO().alterarMercadologicoProdutoSemCodigoAnteriorMerc(vResut);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarAcertarPisCofinsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idTipoPisCofins, tipoNaturezaReceita, pisCofinsDebitoAnt, 
                idTipoPisCofinsCredito, pisCofinsCreditoAnt;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select plu, piscst, cofinscst "
                            + "from ESTOQUE "
                            + "where plu is not null "
            )) {
                while (rst.next()) {                    
                    if ((rst.getString("piscst") != null)
                            && (!rst.getString("piscst").trim().isEmpty())) {
                        idTipoPisCofins = Utils.retornarPisCofinsDebito2(Integer.parseInt(rst.getString("piscst").trim()));
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                        pisCofinsDebitoAnt = Integer.parseInt(rst.getString("piscst").trim());
                    } else {
                        idTipoPisCofins = 1;
                        tipoNaturezaReceita = 999;
                        pisCofinsDebitoAnt = -1;
                    }

                    if ((rst.getString("cofinscst") != null)
                            && (!rst.getString("cofinscst").trim().isEmpty())) {
                        idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito2(Integer.parseInt(rst.getString("cofinscst").trim()));
                        pisCofinsCreditoAnt = Integer.parseInt(rst.getString("cofinscst").trim());
                    } else {
                        idTipoPisCofinsCredito = 13;
                        pisCofinsCreditoAnt = -1;
                    }
                    
                    ProdutoVO vo = new ProdutoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    vo.setId(rst.getInt("plu"));
                    vo.setIdTipoPisCofinsDebito(idTipoPisCofins);
                    vo.setIdTipoPisCofinsCredito(idTipoPisCofinsCredito);
                    vo.setTipoNaturezaReceita(tipoNaturezaReceita);
                    oAnterior.setPiscofinsdebito(pisCofinsDebitoAnt);
                    oAnterior.setPiscofinscredito(pisCofinsCreditoAnt);
                    vo.vCodigoAnterior.add(oAnterior);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarPisCofinsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar PisCofins Produto...");
            vResult = carregarAcertarPisCofinsProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPisCofinsProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarAcertarNcmProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        String ncmAtual;
        int ncm1, ncm2, ncm3;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select plu, classfis "
                    + "from ESTOQUE "
                    + "where plu is not null "
            )) {
                while (rst.next()) {
                    if ((rst.getString("classfis") != null)
                            && (!rst.getString("classfis").trim().isEmpty())) {

                        ncmAtual = Utils.formataNumero(rst.getString("classfis").trim());
                        NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                        ncm1 = oNcm.ncm1;
                        ncm2 = oNcm.ncm2;
                        ncm3 = oNcm.ncm3;
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                    
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("plu"));
                    vo.setNcm1(ncm1);
                    vo.setNcm2(ncm2);
                    vo.setNcm3(ncm3);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarNcmProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Ncm Produto...");
            vResult = carregarAcertarNcmProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarNcm(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
