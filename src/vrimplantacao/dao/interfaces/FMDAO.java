package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
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
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

public class FMDAO extends InterfaceDAO {

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	codigo, \n" +
                    "	secao\n" +
                    "from \n" +
                    "	fm.secoes\n" +
                    "where \n" +
                    "	secao <> ''\n" +
                    "	and char_length(secao) > 1\n" +
                    "order by \n" +
                    "	codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("secao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        opt.addAll(OpcaoProduto.getMercadologico());
        return opt;
    }
    
    
    
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
    
    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct(familia) as familia from fm.mercadorias\n"
                    + "where familia <> ''\n"
                    + "and char_length(secao) > 4\n"
                    + "order by familia;"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setDescricao(rst.getString("familia").trim());
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, secao\n"
                    + "from fm.secoes\n"
                    + "where secao <> ''\n"
                    + "and char_length(secao) > 1\n"
                    + "order by codigo;"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setDescricao(rst.getString("secao"));
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
                    vo.setNivel(nivel);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public Map<Double, ProdutoVO> carregarProduto(int idLoja) throws Exception {
        Map<Double, ProdutoVO> vResult = new HashMap<>();
        long codigoBarra = -2;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT P.CODIGO, P.DATACADASTRO, P.NOME, P.CODBARRAS, "
                    + "P.CUSTO, P.VENDA, P.ML, P.SECAO, P.UM,\n"
                    + "P.CSTICMS, P.ALIICMS,P.CLASFISCAL, P.CSTPIS, "
                    + "P.CSTCOFINS, P.NCM, P.CEST, P.FAMILIA, P.PESO\n"
                    + "FROM fm.MERCADORIAS P ORDER BY P.CODIGO"
            )) {
                int contator = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                vrimplantacao2.dao.cadastro.produto.NcmDAO ncmDAO = new vrimplantacao2.dao.cadastro.produto.NcmDAO();
                
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
                    
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("CODIGO"))));
                    oProduto.setDescricaoCompleta((rst.getString("NOME") == null ? "" : rst.getString("NOME").trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oComplemento.setIdSituacaoCadastro(1 );
                    oComplemento.setIdLoja(idLoja);
                    
                    if ((rst.getString("familia") != null) &&
                            (!rst.getString("familia").trim().isEmpty())) {
                        oProduto.setIdFamiliaProduto(new FamiliaProdutoDAO().getIdByDescricao(Utils.acertarTexto(rst.getString("familia").trim())));
                    } else {
                        oProduto.setIdFamiliaProduto(-1);
                    }
                    
                    if ((rst.getString("secao") != null) &&
                            (!rst.getString("secao").trim().isEmpty())) {
                        MercadologicoVO oMercadologico = new MercadologicoDAO().getMercadologicoByDescricao(Utils.acertarTexto(rst.getString("secao")));
                        oProduto.setMercadologico1(oMercadologico.mercadologico1);
                        oProduto.setMercadologico2(oMercadologico.mercadologico2);
                        oProduto.setMercadologico3(oMercadologico.mercadologico3);
                    } else {
                        oProduto.setMercadologico1(0);
                        oProduto.setMercadologico2(0);
                        oProduto.setMercadologico3(0);
                    }
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        
                        vrimplantacao2.vo.enums.NcmVO oNcm = ncmDAO.getNcm(rst.getString("ncm").trim());
                        
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
                    
                    if ((rst.getString("CEST") != null)
                            && (!rst.getString("CEST").trim().isEmpty())) {
                        
                        if (rst.getString("CEST").trim().length() == 5) {
                            
                            oProduto.setCest1(Integer.parseInt(rst.getString("CEST").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("CEST").trim().substring(1, 3)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("CEST").trim().substring(3, 5)));
                            
                        } else if (rst.getString("CEST").trim().length() == 6) {
                            
                            oProduto.setCest1(Integer.parseInt(rst.getString("CEST").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("CEST").trim().substring(1, 4)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("CEST").trim().substring(4, 6)));
                            
                        } else if (rst.getString("CEST").trim().length() == 7) {
                            
                            oProduto.setCest1(Integer.parseInt(rst.getString("CEST").trim().substring(0, 2)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("CEST").trim().substring(2, 5)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("CEST").trim().substring(5, 7)));
                        }
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    //<editor-fold defaultstate="collapsed" desc="PRODUTOS DE BALANÇA E EMBALAGEM">
                    //Tratando o id da balança.
                    if ((rst.getString("CODBARRAS") != null)
                            && (!rst.getString("CODBARRAS").trim().isEmpty())) {
                        codigoBarra = Utils.stringToLong(Utils.formataNumero(rst.getString("CODBARRAS").trim()));
                        
                        if (codigoBarra == 1) {
                            System.out.println(codigoBarra);
                        }
                        
                        ProdutoBalancaVO produtoBalanca = null;
                        if (codigoBarra > 0 && codigoBarra <= 999999) {
                            produtoBalanca = produtosBalanca.get((int) codigoBarra);
                            
                            if (produtoBalanca != null) {
                                oAutomacao.setCodigoBarras(-1);
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
                                if ((rst.getString("UM") != null) && (!rst.getString("UM").trim().isEmpty())) {
                                    oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("UM").trim()));
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
                            
                            if ((rst.getString("UM") != null) && (!rst.getString("UM").trim().isEmpty())) {
                                oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("UM").trim()));
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
                        if ((rst.getString("UM") != null) && (!rst.getString("UM").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("UM").trim()));
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

                    oProduto.setMargem(rst.getDouble("ML"));
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
                    
                    if ((rst.getString("CSTPIS") != null) &&
                            (!rst.getString("CSTPIS").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("CSTPIS").substring(0, 2))));
                        oCodigoAnterior.setPiscofinsdebito(Integer.parseInt(rst.getString("CSTPIS").trim().substring(0, 2)));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                        oCodigoAnterior.setPiscofinsdebito(-1);
                    }
                    
                    if ((rst.getString("CSTCOFINS") != null) &&
                            (!rst.getString("CSTCOFINS").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("CSTCOFINS").substring(0, 2))));
                        oCodigoAnterior.setPiscofinscredito(Integer.parseInt(rst.getString("CSTCOFINS").trim().substring(0, 2)));
                    } else {
                        oProduto.setIdTipoPisCofinsCredito(13);
                        oCodigoAnterior.setPiscofinscredito(-1);
                    }
                    
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("VENDA"));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oComplemento.setCustoComImposto(rst.getDouble("CUSTO"));
                    oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.setIdEstado(uf.getId());
                    if ((rst.getString("CSTICMS") != null) &&
                            (!rst.getString("CSTICMS").trim().isEmpty())) {
                        oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, false));
                        oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, false));
                        oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, false));
                        oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, false));
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, false));
                        oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), Integer.parseInt(rst.getString("CSTICMS").trim().substring(0, 2)), rst.getDouble("ALIICMS"), 0, true));
                        oCodigoAnterior.setRef_icmsdebito(rst.getString("CSTICMS").trim().substring(0, 2));
                    } else {
                        oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, false));
                        oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, false));
                        oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, false));
                        oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, false));
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, false));
                        oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), 0, 0, 0, true));
                        oCodigoAnterior.setRef_icmsdebito("");
                    }
                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    
                    if ((rst.getString("codbarras") != null)
                            && (!rst.getString("codbarras").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarras"))));
                    } else {
                        oCodigoAnterior.barras = -2;
                    }
                    
                    oCodigoAnterior.setCustosemimposto(rst.getDouble("CUSTO"));
                    oCodigoAnterior.setCustocomimposto(rst.getDouble("CUSTO"));
                    oCodigoAnterior.setMargem(rst.getDouble("ML"));
                    oCodigoAnterior.setPrecovenda(rst.getDouble("VENDA"));
                    oCodigoAnterior.setNcm(rst.getString("NCM"));
                    oCodigoAnterior.setCest(rst.getString("CEST"));
                    
                    ProgressBar.setStatus("Carregando dados...Produto..." + contator + "...");
                    contator++;
                    vResult.put(oProduto.getId(), oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarCustoProduto(int idLoja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, CUSTO "
                            + "FROM MERCADORIAS"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    
                    oProduto.setId(rst.getInt("CODIGO"));
                    oComplemento.setCustoComImposto(rst.getDouble("CUSTO"));
                    oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    oProduto.vComplemento.add(oComplemento);
                    oAnterior.setCustocomimposto(oComplemento.getCustoComImposto());
                    oAnterior.setCustosemimposto(oComplemento.getCustoComImposto());
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                }
            }
            return vResult;            
        }
    }
    
    public List<ProdutoVO> carregarPrecoProduto(int idLoja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, VENDA "
                            + "FROM MERCADORIAS"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();                    
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("CODIGO"));
                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setPrecoVenda(rst.getDouble("VENDA"));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    oAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }
    
    private List<ClientePreferencialVO> carregarCliente(int idLoja, int idLOjaCliente) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge = null,
               dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
               telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
               conjuge = "", orgaoExp = "", celular;
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo, datacadastro, nome, endereco, numero, "
                            + "complemento,  bairro, cidade, estado, cep, observacoes, "
                            + "fone1, fone2, celular, rg, cpf, DataNascimento, email, limitecredito, bloqueado "
                            + "FROM clientes"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    id = rst.getInt("codigo");
                    id_situacaocadastro = 1;
                    dataResidencia = "1990/01/01";
                    id_tipoinscricao = 1;
                    
                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nome = "SEM NOME VR " + id;
                    }
                    
                    if ((rst.getString("endereco") != null)
                            && (!rst.getString("endereco").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("endereco").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cpf").trim()));
                    } else {
                        cnpj = -1;
                    }
                    
                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }
                    
                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("fone1").trim());
                    } else {
                        telefone1 = "0000000000";
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("celular").trim());
                    } else {
                        celular = "0";
                    }
                    
                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = Parametros.get().getCepPadrao();
                    }
                    
                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())) {
                        if ((rst.getString("estado") != null)
                                && (!rst.getString("estado").trim().isEmpty())) {
                            id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade").trim().replace("'", ""),
                                    rst.getString("estado").trim().replace("'", ""));
                            
                            if (id_municipio == 0) {
                                id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                            }
                        } else {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                        }
                    } else {
                        id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                    }
                    
                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(
                                rst.getString("estado").trim().replace("'", ""));
                        
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadraoV2().getId();
                        } else {
                            id_estado = Parametros.get().getUfPadraoV2().getId();
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadraoV2().getId();
                    }
                    
                    if ((rst.getString("numero") != null)
                            && (!rst.getString("numero").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("numero").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }
                    
                    if ((rst.getString("complemento") != null)
                            && (!rst.getString("complemento").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("complemento").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("limitecredito") != null)
                            && (!rst.getString("limitecredito").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("limitecredito").replace(".", "").replace(",", "."));
                    } else {
                        limite = 0;
                    }
                    
                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("RG").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    dataCadastro = rst.getString("datacadastro").substring(0, 10).trim().replace("-", "/");

                    if ((rst.getString("datanascimento") != null)
                            && (!rst.getString("datanascimento").trim().isEmpty())) {
                        dataNascimento = rst.getString("datanascimento").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }
                    
                    if ((rst.getString("bloqueado") != null)
                            && (!rst.getString("bloqueado").trim().isEmpty())) {
                        if ("SIM".equals(rst.getString("bloqueado").trim())) {
                            bloqueado = true;
                        } else {
                            bloqueado = false;
                        }
                    } else {
                        bloqueado = false;
                    }

                    nomePai = "";
                    nomeMae = "";
                    telefone2 = "";
                    fax = "";
                    observacao = "";

                    if ((rst.getString("observacoes") != null)
                            && (!rst.getString("observacoes").trim().isEmpty())
                            && (rst.getString("observacoes").contains("@"))) {
                        email = Utils.acertarTexto(rst.getString("observacoes").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    id_sexo = 1;
                    empresa = "";
                    telEmpresa = "";
                    cargo = "";
                    enderecoEmpresa = "";
                    salario = 0;
                    estadoCivil = 0;
                    conjuge = "";
                    orgaoExp = "";
                    
                    vo.setId(id) ;
                    vo.setNome(nome);
                    vo.setEndereco(endereco);
                    vo.setBairro(bairro);
                    vo.setId_estado(id_estado);
                    vo.setId_municipio(id_municipio);
                    vo.setCep(cep);
                    vo.setTelefone(telefone1);
                    vo.setInscricaoestadual(inscricaoestadual);
                    vo.setCnpj(cnpj);
                    vo.setSexo(id_sexo);
                    vo.setDataresidencia(dataResidencia);
                    vo.setDatacadastro(dataCadastro);
                    vo.setEmail(email);
                    vo.setValorlimite(limite);
                    vo.setCodigoanterior(id);
                    vo.setFax(fax);
                    vo.setBloqueado(bloqueado);
                    vo.setId_situacaocadastro(id_situacaocadastro);
                    vo.setCelular(celular);
                    vo.setObservacao(observacao);
                    vo.setDatanascimento(dataNascimento);
                    vo.setNomepai(nomePai);
                    vo.setNomemae(nomeMae);
                    vo.setEmpresa(empresa);
                    vo.setTelefoneempresa(telEmpresa);
                    vo.setNumero(numero);
                    vo.setCargo(cargo);
                    vo.setEnderecoempresa(enderecoEmpresa);
                    vo.setId_tipoinscricao(id_tipoinscricao);
                    vo.setSalario(salario);
                    vo.setId_tipoestadocivil(estadoCivil);
                    vo.setNomeconjuge(nomeConjuge);
                    vo.setOrgaoemissor(orgaoExp);
                    vResult.add(vo);                    
                }
            }
        }
        return vResult;
    }
    
    
    
    
    
    public List<FornecedorVO> carregarFornecedorFM() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        
        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                numero = "", complemento = "", telefone = "", email = "", fax = "";
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        Long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select forcod, fordes, forend, forbai, forcid, forest, ");
            sql.append("fortel, forfax, forcep, fornum, forcmp, forcon, forobs, ");
            sql.append("forfan, forcgc, forcgf, foremail, forpfpj, forpais, forcodibge ");
            sql.append("from fornecedor  ");
            sql.append("order by fordes ");
            
            rst = stm.executeQuery(sql.toString());
            
            Linha = 0;
            
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    id = rst.getInt("forcod");
                    
                    Linha++;
                    if (Linha == 3) {
                        Linha--;
                        Linha++;
                    }
                    if ((rst.getString("fordes") != null)
                            && (!rst.getString("fordes").isEmpty())) {
                        byte[] bytes = rst.getBytes("fordes");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }
                    
                    if ((rst.getString("forfan") != null)
                            && (!rst.getString("forfan").isEmpty())) {
                        byte[] bytes = rst.getBytes("forfan");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }
                    
                    if ((rst.getString("forcgc") != null)
                            && (!rst.getString("forcgc").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("forcgc").trim()));
                    } else {
                        cnpj = Long.parseLong(rst.getString("forcod"));
                    }
                    
                    if ((rst.getString("forcgf") != null)
                            && (!rst.getString("forcgf").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("forcgf").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    id_tipoinscricao = 0;
                    
                    if ((rst.getString("forend") != null)
                            && (!rst.getString("forend").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("forend").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    if ((rst.getString("forbai") != null)
                            && (!rst.getString("forbai").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("forbai").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }
                    
                    if ((rst.getString("forcep") != null)
                            && (!rst.getString("forcep").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("forcep").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }
                    
                    if ((rst.getString("forcid") != null)
                            && (!rst.getString("forcid").isEmpty())) {
                        
                        if ((rst.getString("forest") != null)
                                && (!rst.getString("forest").isEmpty())) {
                            
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("forcid").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("forest").replace("'", "").trim()));
                            
                            if (id_municipio == 0) {
                                id_municipio = 3525508;
                            }
                        }
                    } else {
                        id_municipio = 3525508;
                    }
                    
                    if ((rst.getString("forest") != null)
                            && (!rst.getString("forest").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("forest").replace("'", "").trim()));
                        
                        if (id_estado == 0) {
                            id_estado = 23;
                        }
                    } else {
                        id_estado = 23;
                    }
                    
                    if (rst.getString("forobs") != null) {
                        obs = rst.getString("forobs").trim();
                    } else {
                        obs = "";
                    }
                    
                    datacadastro = "";
                    
                    pedidoMin = 0;
                    
                    ativo = true;
                    
                    if ((rst.getString("forpfpj") != null)
                            && (!rst.getString("forpfpj").trim().isEmpty())) {
                        if ("J".equals(rst.getString("forpfpj").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    } else {
                        id_tipoinscricao = 0;
                    }
                    
                    if ((rst.getString("fornum") != null)
                            && (!rst.getString("fornum").trim().isEmpty())) {
                        numero = util.acertarTexto(rst.getString("fornum").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }
                    
                    if ((rst.getString("forcmp") != null)
                            && (!rst.getString("forcmp").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("forcmp").replace("'", "").trim());
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("fortel") != null)
                            && (!rst.getString("fortel").trim().isEmpty())) {
                        telefone = util.formataNumero(rst.getString("fortel").trim());
                    } else {
                        telefone = "0";
                    }
                    
                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }
                    
                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }
                    
                    if ((rst.getString("foremail") != null)
                            && (!rst.getString("foremail").trim().isEmpty())
                            && (rst.getString("foremail").contains("@"))) {
                        email = util.acertarTexto(rst.getString("foremail").replace("'", ""));
                    } else {
                        email = "";
                    }
                    
                    if ((rst.getString("forfax") != null)
                            && (!rst.getString("forfax").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("forfax").trim());
                    } else {
                        fax = "";
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
                    
                    if (telefone.length() > 14) {
                        telefone = telefone.substring(0, 14);
                    }
                    
                    oFornecedor.codigoanterior = rst.getInt("forcod");
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.bairro = bairro;
                    oFornecedor.numero = numero;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.complemento = complemento;
                    oFornecedor.telefone = telefone;
                    oFornecedor.email = email;
                    oFornecedor.fax = fax;
                    
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
    
    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        
        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;
        
        try {
            
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
            sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
            sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
            sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
            sql.append("FROM CHEQUES c ");
            sql.append("WHERE c.FILIAL = " + String.valueOf(id_lojaCliente));
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                
                cpfCnpj = Long.parseLong(rst.getString("ciccgc").trim());
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("bancox").trim()));
                
                if ((rst.getString("agenci") != null)
                        && (!rst.getString("agenci").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                if ((rst.getString("contax") != null)
                        && (!rst.getString("contax").trim().isEmpty())) {
                    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                } else {
                    conta = "";
                }
                
                if ((rst.getString("cheque") != null)
                        && (!rst.getString("cheque").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                
                if ((rst.getString("dataxx") != null)
                        && (!rst.getString("dataxx").trim().isEmpty())) {
                    
                    dataemissao = rst.getString("dataxx").trim();
                } else {
                    dataemissao = "2016/02/01";
                }
                
                if ((rst.getString("vencim") != null)
                        && (!rst.getString("vencim").trim().isEmpty())) {
                    
                    datavencimento = rst.getString("vencim").trim();
                } else {
                    datavencimento = "2016/02/12";
                }
                
                if ((rst.getString("observ") != null)
                        && (!rst.getString("observ").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("observ").replace("'", "").trim());
                } else {
                    nome = "";
                }

                /*if ((rst.getString("chrinscrg") != null) &&
                 (!rst.getString("chrinscrg").isEmpty())) {
                 rg = util.acertarTexto(rst.getString("chrinscrg").trim().replace("'", ""));
                    
                 if (rg.length() > 20) {
                 rg = rg.substring(0, 20);
                 }
                 } else {*/
                rg = "";
                //}

                valor = Double.parseDouble(rst.getString("valorx"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                 && (!rst.getString("chrobserv1").isEmpty())) {
                 observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                 } else {*/
                observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                 (!rst.getString("chrtelefone").isEmpty()) &&
                 (!"0".equals(rst.getString("chrtelefone").trim()))) {
                 telefone = util.formataNumero(rst.getString("chrtelefone"));
                 } else {*/
                telefone = "";
                //}

                if (rst.getInt("status") == 1) {
                    id_tipoalinea = 0;
                } else if (rst.getInt("status") == 2) {
                    id_tipoalinea = 15;
                } else {
                    id_tipoalinea = 0;
                }
                
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DATA, DESCRICAO, VALOR, VENCIMENTO, CODCLIENTE, CODCUPOM "
                    + "FROM fm.contasreceber "
                    + "WHERE PAGO = 'NÃO'"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    
                    id_cliente = rst.getInt("CODCLIENTE");
                    dataemissao = rst.getString("DATA").substring(0, 10).trim();
                    datavencimento = rst.getString("VENCIMENTO").substring(0, 10).trim();
                    numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("CODCUPOM")));
                    valor = Double.parseDouble(rst.getString("VALOR"));
                    juros = 0;
                    ecf = 0;

                    if ((rst.getString("DESCRICAO") != null)
                            && (!rst.getString("DESCRICAO").isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("DESCRICAO").replace("'", ""));
                    } else {
                        observacao = "IMPORTADO VR";
                    }
                    
                    vo.setId_loja(id_loja);
                    vo.setDataemissao(dataemissao);
                    vo.setNumerocupom(numerocupom);
                    vo.setValor(valor);
                    vo.setEcf(ecf);
                    vo.setObservacao(observacao);
                    vo.setId_clientepreferencial(id_cliente);
                    vo.setDatavencimento(datavencimento);
                    vo.setValorjuros(juros);;
                    vResult.add(vo);                    
                }
            }
        }
        return vResult;
    }
    
    
    //** **IMPORTAÇÕES ***/
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
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            vResult = carregarFamiliaProduto();
            if (!vResult.isEmpty()) {
                FamiliaProdutoDAO familiaProd = new FamiliaProdutoDAO();
                familiaProd.gerarCodigo = true;
                familiaProd.salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vResult = carregarMercadologico(1);
            new MercadologicoDAO().salvar(vResult, true);

            vResult = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vResult, false);

            vResult = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vResult, false);

            new MercadologicoDAO().salvarMax();
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProduto(int idLoja) throws Exception {        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produto...");
            Map<Double, ProdutoVO> vProdutoFM = carregarProduto(idLoja);            
            List<LojaVO> vLoja = new LojaDAO().carregar();            
            ProgressBar.setMaximum(vProdutoFM.size());
            
            for (Double keyId : vProdutoFM.keySet()) {                
                ProdutoVO oProduto = vProdutoFM.get(keyId);                
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = 0;
                oProduto.idTipoMercadoria = -1;                
                vProdutoNovo.add(oProduto);                
                ProgressBar.next();
            }
            
            produto.implantacaoExterna = true;
            produto.usarMercadoligicoProduto = true;
            produto.salvar(vProdutoNovo, idLoja, vLoja);            
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
        produto.usarMercadoligicoProduto = true;
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
            ProgressBar.setStatus("Carregando dados...Custo Produto..."+idLoja+"...");
            vResult = carregarCustoProduto(idLoja, idLoja);
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
            ProgressBar.setStatus("Carregando dados...Preço Produto..."+idLoja+"...");
            vResult = carregarPrecoProduto(idLoja, idLoja);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLoja);
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
    
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {        
        try {            
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);            
            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);            
        } catch (Exception ex) {            
            throw ex;
        }
    }
    
    
    /*
    *
    *
    *
    *
    *
    **/
    
    
    
    public void importarFornecedor() throws Exception {
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorFM();
            
            new FornecedorDAO().salvar(vFornecedor);
            
        } catch (Exception ex) {
            
            throw ex;
        }
    }
    
    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);
            
            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);
            
        } catch (Exception ex) {
            
            throw ex;
        }
    }
    
    // FUNÇÕES
    private int retornarAliquotaICMSFM(String codTrib, String descTrib) {
        
        int retorno = 8;
        if (codTrib.trim() != "") {
            if ("7.0000".equals(codTrib.trim())) {
                retorno = 0;
            } else if ("12.0000".equals(codTrib.trim())) {
                retorno = 1;
            } else if ("18.0000".equals(codTrib.trim())) {
                retorno = 2;
            } else if ("25.0000".equals(codTrib.trim())) {
                retorno = 3;
            } else {
                retorno = 8;
            }
        } else if (descTrib.trim() != "") {
            if ("NN".equals(descTrib.trim())) {
                retorno = 6;
            } else if ("FF".equals(descTrib.trim())) {
                retorno = 7;
            } else if ("II".equals(descTrib.trim())) {
                retorno = 6;
            } else if ("IC".equals(descTrib.trim())) {
                retorno = 8;
            } else {
                retorno = 8;
            }
        }
        return retorno;
    }

    @Override
    public String getSistema() {
        return "FM";
    }

    public Iterable<Estabelecimento> getLojas() {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
}
