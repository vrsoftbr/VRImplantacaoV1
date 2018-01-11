package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.MercadologicoMapDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO.OpcaoClientePreferencial;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;

/**
 * @author Leandro
 */
public class SciDAO extends AbstractIntefaceDao {
    
    private static final String ID_SISTEMA = "SCI";
    public boolean gerarIdDosProdutos = false;
    
    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select lj_noloja id, lj_nome descricao from dbtabelas.loja order by id"
            )) {            
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), (rst.getInt("id") + " - " + rst.getString("descricao"))));
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  GradP_NoGrade id,\n" +
                "  GradP_DescricaoG descricao\n" +
                "from dbtabelas.produto_gradepco\n" +
                "order by id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(rst.getInt("id"));
                    familiaVO.setIdLong(rst.getLong("id"));
                    familiaVO.setDescricao(rst.getString("descricao"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CL_NoCL merc1, CL_Nome descricao FROM centro_de_lucro c order by CL_NoCL"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("merc1"));
                    if (nivel > 1) {
                        vo.setMercadologico2(1);
                    }
                    if (nivel > 2) {
                        vo.setMercadologico3(1);
                    }
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setNivel(nivel);
                    result.add(vo);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {   
            String ufEmpresa;
            try (ResultSet rst = stm.executeQuery(
                    "select Lj_Estado uf from loja where Lj_NoLoja = " + idLojaCliente
            )) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("uf");
                } else {
                    ufEmpresa = "CE";
                }
            }
            MercadologicoVO mercadologicoPadrao = MercadologicoDAO.getMaxMercadologico();
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  p.prd_NoProd id,\n" +
                "  p.Prd_Descricao descricaocompleta,\n" +
                "  p.Prd_DescAbrev descricaoreduzida,\n" +
                "  p.Prd_Descricao descricaogondola,\n" +
                "  case p.Prd_foralinha when 'N' then 1 else 0 end id_Situacaocadastro,\n" +
                "  p.Prd_dataInc dataCadastro,\n" +
                "  p.Prd_CentLucro merc1,\n" +
                "  1 as merc2,\n" +
                "  1 as merc3,\n" +
                "  p.Prd_codNCM ncm,\n" +
                "  p.Prd_CodCEST cest,\n" +
                "  p.Prd_GradePco id_familia,\n" +
                "  pr.Pco_Lucro margem,\n" +
                "  p.Prd_DiaValidade validade,\n" +
                "  p.Prd_Unidade id_tipoembalagem,\n" +
                "  p.Prd_QtdeEmb qtdEmbalagem,\n" +
                "  p.Prd_PesoBruto pesobruto,\n" +
                "  p.Prd_PesoLiq pesoliquido,\n" +
                "  p.Prd_CofinsPisEnt,\n" +
                "  p.Prd_CofinsPisSai,\n" +
                "  p.Prd_NatRecPC,\n" +
                "  p.Prd_usabalanca,\n" +
                "  pr.Pco_PcoVend preco,\n" +
                "  pr.Pco_CtoCont custosemimp,\n" +
                "  pr.Pco_PcoFsD custocomimp,\n" +
                "  est.Et_Est,\n" +
                "  est.Et_EstMinino,\n" +
                "  est.Et_EstMaximo,\n" +
                "  p.Prd_CodSitTri icms_cst,\n" +
                "  p.Prd_AliqVenda icms_aliq,\n" +
                "  0 icms_reduz\n" +
                "from\n" +
                "  dbtabelas.produto p\n" +
                "  left join dbtabelas.preco pr on pr.Pco_NoProd = p.Prd_NoProd and pr.Pco_NoLoja = " + idLojaCliente + "\n" +
                "  left join dbtabelas.estoque est on est.Et_NoProd = p.Prd_NoProd and est.Et_NoLoja = " + idLojaCliente + "\n" +
                "order by\n" +
                "  p.prd_NoProd"
            )) {                
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                Map<String, MercadologicoVO> mapeados = new MercadologicoMapDAO().getMercadologicosMapeados();
                while (rst.next()) {      
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
                                      
                    oProduto.setId(rst.getInt("id"));
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
                    if (rst.getString("dataCadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("dataCadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }
                    
                    //MercadologicoVO mapeado = mapeados.get(ID_SISTEMA + "-" + idLojaCliente + "-" + rst.getInt("merc1"));
                    
                    //Util.exibirMensagem(ID_SISTEMA + "-" + idLojaCliente + "-" + rst.getInt("merc1"), "teste");
                    //Util.exibirMensagem(String.valueOf(mapeados.containsKey(ID_SISTEMA + "-" + idLojaCliente + "-" + rst.getInt("merc1"))), "");

                    //if (mapeado == null) {
                    //mapeado = mercadologicoPadrao;
                    //}
                    
                    oProduto.setMercadologico1(mercadologicoPadrao.getMercadologico1());
                    oProduto.setMercadologico2(mercadologicoPadrao.getMercadologico2());
                    oProduto.setMercadologico3(mercadologicoPadrao.getMercadologico3());
                    oProduto.setMercadologico4(mercadologicoPadrao.getMercadologico4());
                    oProduto.setMercadologico5(mercadologicoPadrao.getMercadologico5());
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("ncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(9701);
                        oProduto.setNcm2(90);
                        oProduto.setNcm3(0);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    oProduto.setIdFamiliaProduto(rst.getString("id_familia") != null ? rst.getInt("id_familia") : -1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);                                     
                    
                    
                    
                    if (Utils.acertarTexto(rst.getString("Prd_usabalanca"), "N").equals("S")) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(rst.getInt("validade"));
                        
                        if ("KG".equals(Utils.acertarTexto(rst.getString("id_tipoembalagem")))) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }
                        
                        oProduto.eBalanca = true;
                        oCodigoAnterior.setCodigobalanca((int) oProduto.getId());
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                        
                        oProduto.eBalanca = false;
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }                    
                    oAutomacao.setQtdEmbalagem(1);
                                        
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());                    
                                     
                    
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
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliquido"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("Prd_CofinsPisSai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("Prd_CofinsPisEnt")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("Prd_NatRecPC")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimp"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimp"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
                    oComplemento.setEstoque(Utils.stringToDouble(rst.getString("Et_Est")));
                    oComplemento.setEstoqueMinimo(Utils.stringToDouble(rst.getString("Et_EstMinino")));
                    oComplemento.setEstoqueMaximo(Utils.stringToDouble(rst.getString("Et_EstMaximo")));   

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_reduz")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_reduz")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_reduz")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_reduz")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_reduz")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(-2);
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("Prd_CofinsPisSai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("Prd_CofinsPisEnt"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("Prd_NatRecPC"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("icms_cst"));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                }                
            }
        } 
        
        return vProduto;
    }

    @Override
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                            + "Et_NoLoja, "
                            + "Et_NoProd, "
                            + "Et_est,  "
                            + "Et_EstMinino, "
                            + "Et_EstMaximo "
                    + "from estoque "
                    + "where Et_NoLoja = " + idLojaCliente + " "
                    + "order by Et_NoProd, Et_NoLoja"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    
                    oProduto.setId(rst.getInt("Et_NoProd"));
                    oComplemento.setEstoque(Utils.stringToDouble(rst.getString("Et_est")));
                    oComplemento.setEstoqueMinimo(Utils.stringToDouble(rst.getString("Et_EstMinino")));
                    oComplemento.setEstoqueMaximo(Utils.stringToDouble(rst.getString("Et_EstMaximo")));
                    
                    result.put((int) oProduto.getId(), oProduto);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception{
        
        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarEstoqueProduto(idLojaVR, idLojaCliente).values()) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.alterarEstoqueProduto(new ArrayList<>(aux.values()), idLojaVR);
    }
    
    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from\n" +
                    "(select\n" +
                    "  p.Prd_NoProd id_produto,\n" +
                    "  p.Prd_CodBarra ean,\n" +
                    "  1 qtdEmbalagem,\n" +
                    "  p.Prd_Unidade id_tipoembalagem\n" +
                    "from\n" +
                    "  produto p\n" +
                    "union\n" +
                    "select\n" +
                    "  CBA_NoProd id_produto,\n" +
                    "  CBA_CodBarra ean,\n" +
                    "  coalesce(CBA_QtdeEmb, p.Prd_QtdeEmb) qtdEmbalagem,\n" +
                    "  coalesce(CBA_Unidade, p.Prd_Unidade) id_tipoembalagem\n" +
                    "from\n" +
                    "  produto_cbaux ean\n" +
                    "  join produto p on ean.CBA_NoProd = p.Prd_NoProd) eans\n" +
                    "order by id_produto"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id_produto"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    String ean = String.valueOf(oAutomacao.getCodigoBarras());
                    if ((ean.length() >= 7) &&
                        (ean.length() <= 14)) {                                             
                        result.put(oAutomacao.getCodigoBarras(), oProduto);
                    }                    
                }                 
            }
        }
            
        return result;
    }

    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");
        List<LojaVO> vLoja = new LojaDAO().carregar();        
        
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        ProgressBar.setMaximum(aux.size());
        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.gerarCodigo = gerarIdDosProdutos;
        produto.salvar(new ArrayList<>(aux.values()), idLojaVR, vLoja);
    }
    

    @Override
    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);

        dao.salvarMax();
    }

    @Override
    public List<MercadologicoMapaVO> carregarMapeamentoDeMercadologico(int idLojaVR, int idLojaCliente) throws Exception {
        List<MercadologicoMapaVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CL_NoCL merc1, CL_Nome descricao FROM centro_de_lucro c order by CL_NoCL"
            )) {
                while (rst.next()) {
                    result.add(new MercadologicoMapaVO (
                            ID_SISTEMA,
                            idLojaCliente + "",
                            rst.getString("merc1"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
                
        int idMunicipioLoja = 0, idUfLoja = 0;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_municipio, id_estado from fornecedor where id = 1"
            )) {
                if (rst.next()) {
                    idMunicipioLoja = rst.getInt("id_municipio");
                    idUfLoja = rst.getInt("id_estado");
                }
            }
        }
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "  f.Forn_NoForn id,\n" +
                "  f.Forn_Nome razao,\n" +
                "  f.Forn_Apelido fantasia,\n" +
                "  concat(ifnull(lg.TL_Nome,\"RUA\"), \" \", f.Forn_Endereco) endereco,\n" +
                "  f.Forn_Numero numero,\n" +
                "  f.Forn_Complemento complemento,\n" +
                "  concat(ifnull(tb.TB_Nome,\"BAIRRO\"), \" \", f.Forn_Bairro) bairro,\n" +
                "  mun.Mun_Nome cidade,\n" +
                "  f.Forn_Estado uf,\n" +
                "  f.Forn_Cep cep,\n" +
                "  concat(coalesce(f.Forn_DDD1,\"85\"), f.Forn_Telef1) fone1,\n" +
                "  concat(coalesce(f.Forn_DDD2,\"85\"), f.Forn_Telef2) fone2,\n" +
                "  concat(coalesce(f.Forn_DDD,\"85\"), f.Forn_Fax) fax,\n" +
                "  concat(\"CONTATO: \", coalesce(f.Forn_Contato,''), \" RESPONSAVEL: \", coalesce(f.Forn_Responsavel,''), \" OBSERVACOES: \", coalesce(f.Forn_Obs,'')) observacao,\n" +
                "  f.forn_InscEst inscricaoestadual,\n" +
                "  f.forn_CGC cnpj\n" +
                "from\n" +
                "  fornecedor f\n" +
                "  left join tipo_logradouro lg on f.Forn_TpLogradouro = lg.TL_Codigo\n" +
                "  left join tipo_bairro tb on f.Forn_TpBairro = tb.TB_Codigo\n" +
                "  left join municipio mun on f.Forn_CodMunic = mun.Mun_Codigo and f.Forn_Estado = mun.Mun_UF\n" +
                "order by\n" +
                "  f.Forn_NoForn"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro = new Date(new java.util.Date().getTime());                     
                    
                    String cidade = Utils.acertarTexto(rst.getString("cidade"));
                    String uf = Utils.acertarTexto(rst.getString("uf"));

                    oFornecedor.setId(rst.getInt("id"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setCodigoanterior(rst.getInt("id"));
                    oFornecedor.setRazaosocial(rst.getString("razao"));
                    oFornecedor.setNomefantasia(rst.getString("fantasia"));
                    oFornecedor.setEndereco(rst.getString("endereco"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    int idMunicipio = Utils.retornarMunicipioIBGEDescricao(cidade, uf);                    
                    oFornecedor.setId_municipio(idMunicipio > 0 ? idMunicipio : idMunicipioLoja);
                    oFornecedor.setCep(Utils.formatCep(rst.getString("cep")));
                    int idUf = Utils.getEstadoPelaSigla(rst.getString("uf"));
                    oFornecedor.setId_estado(idUf > 0 ? idUf : idUfLoja);
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setId_tipoinscricao(oFornecedor.getInscricaoestadual().length() < 11 ? 1 : 9);
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setFax(rst.getString("fax"));
                    //oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(1);
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {                        
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                    "  c.Cli_NoCliente idantigo,\n" +
                    "  c.Cli_CartProfissional id_oficial,\n" +
                    "  c.Cli_Nome nome,\n" +
                    "  c.Cli_Endereco endereco,\n" +
                    "  c.Cli_Numero numero,\n" +
                    "  c.Cli_PontoRef complemento,\n" +
                    "  c.Cli_Bairro bairro,\n" +
                    "  c.Cli_Cidade cidade,\n" +
                    "  c.Cli_Estado estado,\n" +
                    "  c.Cli_Cep cep,\n" +
                    "  concat(coalesce(c.Cli_DDD1,''), c.Cli_Telefone1) fone1,\n" +
                    "  concat(coalesce(c.Cli_DDD2,''), c.Cli_Telefone2) fone2,\n" +
                    "  c.Cli_Pai,\n" +
                    "  c.Cli_Mae,\n" +
                    "  c.Cli_EnderecoCob enderecocob,\n" +
                    "  c.Cli_BairroCob bairrocob,\n" +
                    "  c.Cli_CidadeCob cidadecob,\n" +
                    "  c.Cli_EstadoCob estadocob,\n" +
                    "  c.Cli_CepCob cepcob,\n" +
                    "  c.Cli_CI_Fantz inscricaoestadual,\n" +
                    "  c.Cli_Cpf_Cgc cnpj,\n" +
                    "  case upper(c.Cli_SexoFM) when 'F' then 0 else 1 end as sexo,\n" +
                    "  c.Cli_DtNascimento datanasc,\n" +
                    "  c.Cli_VlrLimComp limite,\n" +
                    "  c.Cli_TitEleitor dias /*Campo informado por Raphael*/,\n" +
                    "  c.Cli_Situacao situacaocadastro,\n" +
                    "  c.Cli_DtCadastro,\n" +
                    "  c.Cli_DtNascimento datanascimento,\n" +
                    "  c.Cli_CartIdentRG rg,\n" +
                    "  c.Cli_Profissao,\n" +
                    "  c.Cli_Empregador,\n" +
                    "  c.Cli_DtAdmissao,\n" +
                    "  c.Cli_RendaDec,\n" +
                    "  c.Cli_EnderecoCob,\n" +
                    "  c.Cli_BairroCob,\n" +
                    "  c.Cli_CidadeCob,\n" +
                    "  c.Cli_CepCob,\n" +
                    "  c.Cli_EstadoCob,\n" +
                    "  c.Cli_DDDCob,\n" +
                    "  c.Cli_TelefoneCob,\n" +
                    "  c.Cli_EnderecoEmp,\n" +
                    "  c.Cli_BairroEmp,\n" +
                    "  c.Cli_CidadeEmp,\n" +
                    "  c.Cli_EstadoEmp,\n" +
                    "  c.Cli_CepEmp,\n" +
                    "  c.Cli_DDDEmp,\n" +
                    "  c.Cli_RamalEmp,\n" +
                    "  c.Cli_TelefoneEmp,\n" +
                    "  c.Cli_EstCivil,\n" +
                    "  c.Cli_TitEleitor,\n" +
                    "  case c.Cli_TipoCli when 'A4' then 1 else 0 end bloqueado\n" +
                    "from\n" +
                    "  clientes c\n" +
                    "where not c.Cli_CartProfissional is null\n" +
                    "order by\n" +
                    //"  c.Cli_CartProfissional"
                    "  c.Cli_NoCliente"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id_oficial"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("idantigo"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    if (oClientePreferencial.getId() == 774) {
                        Util.exibirMensagem("Cliente: " + oClientePreferencial.getNome(), "");
                    }
                    oClientePreferencial.setEndereco(rst.getString("endereco"));
                    oClientePreferencial.setNumero(rst.getString("numero"));
                    oClientePreferencial.setComplemento(rst.getString("complemento"));
                    oClientePreferencial.setBairro(rst.getString("bairro"));
                    String uf = Utils.acertarTexto(rst.getString("cidade"));
                    String cidade = Utils.acertarTexto(rst.getString("estado"));
                    oClientePreferencial.setId_estado(uf.equals("") ? Global.idEstado : Utils.getEstadoPelaSigla(uf));
                    oClientePreferencial.setId_municipio(cidade.equals("") ? Global.idMunicipio : Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oClientePreferencial.setCep(rst.getString("cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setTelefone2(rst.getString("fone2"));
                    oClientePreferencial.setNomepai(rst.getString("Cli_Pai"));
                    oClientePreferencial.setNomemae(rst.getString("Cli_Mae"));
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("dias"));
                    oClientePreferencial.setDatanascimento(rst.getDate("datanasc"));
                    oClientePreferencial.setCargo(rst.getString("Cli_Profissao"));
                    oClientePreferencial.setEmpresa(rst.getString("Cli_Empregador"));
                    oClientePreferencial.setDataadmissao(rst.getDate("Cli_DtAdmissao"));
                    oClientePreferencial.setSalario(rst.getDouble("Cli_RendaDec"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setEnderecoempresa(rst.getString("Cli_EnderecoEmp"));
                    oClientePreferencial.setBairroempresa(rst.getString("Cli_BairroEmp"));
                    uf = Utils.acertarTexto(rst.getString("Cli_CidadeEmp"));
                    cidade = Utils.acertarTexto(rst.getString("Cli_EstadoEmp"));
                    oClientePreferencial.setId_estadoempresa(uf.equals("") ? Global.idEstado : Utils.getEstadoPelaSigla(uf));
                    oClientePreferencial.setId_municipioempresa(cidade.equals("") ? Global.idMunicipio : Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oClientePreferencial.setCepempresa(Utils.formatCep(rst.getString("Cli_CepEmp")));
                    oClientePreferencial.setTelefoneempresa(Utils.formataNumero(rst.getString("Cli_DDDEmp")) + Utils.formataNumero(rst.getString("Cli_TelefoneEmp")));
                    switch (Utils.acertarTexto(rst.getString("Cli_EstCivil"))) {
                        case "S": oClientePreferencial.setId_tipoestadocivil(1); break;
                        case "C": oClientePreferencial.setId_tipoestadocivil(2); break;
                        case "A": oClientePreferencial.setId_tipoestadocivil(2); break;
                        default: oClientePreferencial.setId_tipoestadocivil(0); break;
                    }
                    oClientePreferencial.setDatacadastro(rst.getString("Cli_DtCadastro"));
                    
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    //oClientePreferencial.setEmail(rst.getString("email"));
                    //oClientePreferencial.setFax(rst.getString("fax"));
                    oClientePreferencial.setBloqueado(rst.getInt("bloqueado") == 1);
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("VR IMPORTACAO\n"
                            + "   ENDERECO COB- " + Utils.acertarTexto(rst.getString("Cli_EnderecoCob")) + "\n"
                            + "   BAIRRO COB- " + Utils.acertarTexto(rst.getString("Cli_BairroCob")) + "\n"
                            + "   CIDADE COB- " + Utils.acertarTexto(rst.getString("Cli_CidadeCob")) + "-" + Utils.acertarTexto(rst.getString("Cli_EstadoCob")) + "\n"
                            + "   CEP COB- " + Utils.acertarTexto(rst.getString("Cli_CepCob"))
                            + "   TEL COB-" + Utils.acertarTexto(rst.getString("Cli_DDDCob")) + Utils.acertarTexto(rst.getString("Cli_TelefoneCob"))
                            + "   RAMAL EMPRESA- " + rst.getString("Cli_RamalEmp") + "\n"
                    );
                    //oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
                    //oClientePreferencial.setEnderecoempresa(rst.getString("enderecoEmpresa"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    //oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    //oClientePreferencial.setOrgaoemissor(rst.getString("orgaoExp"));                  

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }
    
    @Override
    public List<ReceberChequeVO> carregarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  ch.Che_DtVenda,\n" +
                    "  ch.Che_DtDep,\n" +
                    "  c.Cli_Cpf_Cgc,\n" +
                    "  c.Cli_Nome,\n" +
                    "  ch.Che_NoBanco,\n" +
                    "  ch.Che_NoAge,\n" +
                    "  ch.Che_NoConta,\n" +
                    "  ch.Che_NoChe,\n" +
                    "  ch.Che_Valor\n" +
                    "FROM clientes c\n" +
                    "  left join cartao_credito_proprio cp on(c.Cli_NoCliente = cp.Ccp_NoCliente)\n" +
                    "  left join cartao_proprio_limite cl on(cp.Ccp_NoLimitCred = cl.CcpL_NoFaixa)\n" +
                    "  left join cheque ch on(ch.che_NoCli = c.Cli_NoCliente)\n" +
                    "where ch.Che_DtVenda >= '2016-01-01'"
            )) {
                while (rst.next()) {

                    ReceberChequeVO oReceberCheque = new ReceberChequeVO();                
                    oReceberCheque.setId_loja(idLojaVR);
                    oReceberCheque.setData(Utils.formatDate(rst.getDate("Che_DtVenda") != null ? rst.getDate("Che_DtVenda") : new java.util.Date()));
                    oReceberCheque.setDatadeposito(rst.getDate("Che_DtDep") != null ? Utils.formatDate(rst.getDate("Che_DtDep")) : oReceberCheque.getData());
                    oReceberCheque.setCpf(Utils.stringToLong(rst.getString("Cli_Cpf_Cgc")));
                    oReceberCheque.setNumerocheque(rst.getInt("Che_NoChe"));
                    int banco = Utils.retornarBanco(rst.getInt("Che_NoBanco"));
                    if (banco == 0) {banco = 804;}
                    oReceberCheque.setId_banco(banco);
                    oReceberCheque.setAgencia(rst.getString("Che_NoAge"));
                    oReceberCheque.setConta(rst.getString("Che_NoConta"));
                    oReceberCheque.setValor(rst.getDouble("Che_Valor"));
                    oReceberCheque.setObservacao("IMPORTADO VR");
                    oReceberCheque.setNome(rst.getString("Cli_Nome"));

                    vReceberCheque.add(oReceberCheque);
                }
            }
        }
        return vReceberCheque;
    }

    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.utilizarCodigoAnterior = true;
        dao.manterID = true;
        dao.salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }
    
    public void corrigirClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...Correção...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.utilizarCodigoAnterior = true;
        dao.corrigirInformacoes(vClientePreferencial, idLojaVR, 
                OpcaoClientePreferencial.BLOQUEADO,
                OpcaoClientePreferencial.DIA_VENCIMENTO);
    }
    
    
    
    @Override
    public List<ClienteEventualVO> carregarClienteEventual(int idLojaVR, int idLojaCliente) throws Exception {
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  c.Cli_NoCliente idantigo,\n" +
                    "  c.Cli_CartProfissional id_oficial,\n" +
                    "  c.Cli_Nome nome,\n" +
                    "  c.Cli_Endereco endereco,\n" +
                    "  c.Cli_Numero numero,\n" +
                    "  c.Cli_Bairro bairro,\n" +
                    "  c.Cli_Cidade cidade,\n" +
                    "  c.Cli_Estado estado,\n" +
                    "  c.Cli_Cep cep,\n" +
                    "  concat(coalesce(c.Cli_DDD1,''), c.Cli_Telefone1) fone1,\n" +
                    "  concat(coalesce(c.Cli_DDD2,''), c.Cli_Telefone2) fone2,\n" +
                    "  c.Cli_Pai,\n" +
                    "  c.Cli_Mae,\n" +
                    "  c.Cli_EnderecoCob enderecocob,\n" +
                    "  c.Cli_BairroCob bairrocob,\n" +
                    "  c.Cli_CidadeCob cidadecob,\n" +
                    "  c.Cli_EstadoCob estadocob,\n" +
                    "  c.Cli_CepCob cepcob,\n" +
                    "  c.Cli_CI_Fantz inscricaoestadual,\n" +
                    "  c.Cli_Cpf_Cgc cnpj,\n" +
                    "  c.Cli_SexoFM sexo,\n" +
                    "  c.Cli_DtNascimento datanasc,\n" +
                    "  c.Cli_VlrLimComp limite,\n" +
                    "  c.Cli_TitEleitor dias /*Campo informado por Raphael*/,\n" +
                    "  c.Cli_Situacao situacaocadastro,\n" +
                    "  c.Cli_DtCadastro datacadastro,\n" +
                    "  c.Cli_DtNascimento datanascimento,\n" +
                    "  c.Cli_CartIdentRG rg,\n" +
                    "  c.Cli_Profissao,\n" +
                    "  c.Cli_Empregador,\n" +
                    "  c.Cli_RendaDec,\n" +
                    "  c.Cli_TitEleitor,\n" +
                    "  case c.Cli_TipoCli when 'A4' then 1 else 0 end id_situacaocadastro\n" +
                    "from\n" +
                    "  clientes c\n" +
                    //"where not c.Cli_CartProfissional is null\n" +
                    "order by\n" +
                    //"  c.Cli_CartProfissional"
                    "  c.Cli_NoCliente"                            
            )) {
                while (rst.next()) {
                    ClienteEventualVO oClienteEventual = new ClienteEventualVO();

                    oClienteEventual.setId(rst.getInt("idantigo"));
                    oClienteEventual.setNome(rst.getString("nome"));
                    
                    oClienteEventual.setEndereco(rst.getString("endereco"));
                    oClienteEventual.setNumero(rst.getString("numero"));
                    oClienteEventual.setBairro(rst.getString("bairro"));
                    String cidade = Utils.acertarTexto(rst.getString("cidade"));
                    String uf = Utils.acertarTexto(rst.getString("estado"));
                    oClienteEventual.setId_municipio(Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oClienteEventual.setId_estado(Utils.getEstadoPelaSigla(uf));
                    oClienteEventual.setCep(Utils.formatCep(rst.getString("cep")));
                    
                    oClienteEventual.setEnderecocobranca(rst.getString("enderecocob"));
                    oClienteEventual.setBairrocobranca(rst.getString("bairrocob"));
                    cidade = Utils.acertarTexto(rst.getString("cidadecob"));
                    uf = Utils.acertarTexto(rst.getString("estadocob"));
                    oClienteEventual.setId_municipiocobranca(Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oClienteEventual.setId_estadocobranca(Utils.getEstadoPelaSigla(uf));
                    
                    if (oClienteEventual.getId_estadocobranca() == 0) {
                        oClienteEventual.setId_estadocobranca(23);
                    }                    
                    if (oClienteEventual.getId_municipiocobranca() == 0) {
                        oClienteEventual.setId_municipiocobranca(2304400);
                    }
                    if (oClienteEventual.getId_estado() == 0) {
                        oClienteEventual.setId_estado(23);
                    }                    
                    if (oClienteEventual.getId_municipio() == 0) {
                        oClienteEventual.setId_municipio(2304400);
                    }
                    
                    oClienteEventual.setCepcobranca(Utils.formatCep(rst.getString("cepcob")));
                                        
                    oClienteEventual.setTelefone(rst.getString("fone1"));
                    oClienteEventual.setTelefone2(rst.getString("fone2"));
                    oClienteEventual.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClienteEventual.setCnpj(Utils.stringToLong(rst.getString("cnpj")));
                    oClienteEventual.setDatacadastro(rst.getDate("datacadastro"));
                    //oClienteEventual.setEmail(rst.getString("email"));
                    oClienteEventual.setLimitecompra(rst.getDouble("limite"));
                    oClienteEventual.setPrazopagamento(rst.getInt("dias"));
                    //oClienteEventual.setFax(rst.getString("fax"));
                    oClienteEventual.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClienteEventual.setObservacao("IMPORTADO VR");
                    oClienteEventual.setId_tipoinscricao(String.valueOf(oClienteEventual.getCnpj()).length() <= 9 ? 1 : 0);

                    vClienteEventual.add(oClienteEventual);
                }
            }
        } 
        return vClienteEventual;
    }
    
    public boolean gerarIdParaClienteEventual = false;
    
    @Override
    public void importarClienteEventual(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carrando dados...Cliente Eventual...");
        List<ClienteEventualVO> vEventuais = carregarClienteEventual(idLojaVR, idLojaCliente);
        ClienteEventuallDAO dao = new ClienteEventuallDAO();
        dao.salvar(vEventuais, idLojaCliente, gerarIdParaClienteEventual);
    }

    public void mapearMercadologico(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Importando mercadologicos anteriores....");
        List<ProdutoVO> produtos = this.carregarMercadologicoProduto();
        
        ProgressBar.setStatus("Ajustando o mercadologico");
        ProgressBar.setMaximum(produtos.size());
        Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
        Map<String, MercadologicoVO> mapeados = new MercadologicoMapDAO().getMercadologicosMapeados();
        MercadologicoVO padrao = MercadologicoDAO.getMercadologicoAAcertar();
        Conexao.begin();
        try {
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoVO vo: produtos) {
                    CodigoAnteriorVO anterior = anteriores.get(vo.getId());
                    if (anterior != null) {

                        MercadologicoVO novo = mapeados.get(ID_SISTEMA + "-" + idLojaCliente + "-" + vo.getMercadologico1());
                        if (novo == null) {
                            novo = padrao;
                        } 

                        stm.execute(
                            "update produto set \n" +
                            "mercadologico1 = " + novo.getMercadologico1() + ",\n" +
                            "mercadologico2 = " + novo.getMercadologico2() + ",\n" +
                            "mercadologico3 = " + novo.getMercadologico3() + ",\n" +
                            "mercadologico4 = " + novo.getMercadologico4() + ",\n" +
                            "mercadologico5 = " + novo.getMercadologico5() + "\n" +
                            "where id = " + (long) anterior.getCodigoatual()
                        );
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private List<ProdutoVO> carregarMercadologicoProduto() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  Prd_NoProd,\n" +
                "  Prd_Descricao,\n" +
                "  Prd_CentLucro\n" +
                "from\n" +
                "  produto\n" +
                "order by\n" +
                "  Prd_NoProd"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setId(rst.getInt("Prd_NoProd"));
                    oProduto.setDescricaoCompleta("Prd_Descricao");
                    oProduto.reajustarDescricoes();
                    oProduto.setMercadologico1(rst.getInt("Prd_CentLucro"));
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  Prd_NoProd as id_produto,\n" +
                "  Prd_PesoLiq as precooferta,\n" +
                "  cast(Prd_CodFab as date) as datatermino\n" +
                "from\n" +
                "  produto\n" +
                "where\n" +
                "  Prd_PesoLiq > 0\n" +
                "order by\n" +
                "  Prd_NoProd"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setDatainicio("2016-09-03");
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    /*"SELECT\n" +
                    "  ccp.Ccp_NoCliente,\n" +
                    "  cpt.Ccpt_NoLojaCompra,\n" +
                    "  cpt.Ccpt_NoPDV,\n" +
                    "  cpt.Ccpt_NoTransacao,\n" +
                    "  cpt.Ccpt_TipoTrans,\n" +
                    "  cpt.Ccpt_NoCartao,\n" +
                    "  cpt.Ccpt_Data,\n" +
                    "  cpt.Ccpt_Documento,\n" +
                    "  cpt.Ccpt_CodNF,\n" +
                    "  cpt.Ccpt_Valor,\n" +
                    "  cpt.Ccpt_DataGeracao\n" +
                    "FROM\n" +
                    "  cartao_proprio_transacao cpt\n" +
                    "  join cartao_credito_proprio ccp on\n" +
                    "    cpt.CcpT_NoCartao = ccp.Ccp_NoCartao\n" +
                    "where\n" +
                    "  cpt.Ccpt_Fechada = 0 and\n" +
                    "  cpt.Ccpt_NoLojaCompra = " + idLojaCliente + " and\n" +
                    "  not Ccpt_Valor is null and\n" +
                    "  not Ccpt_DataGeracao is null\n" +
                    "order by\n" +
                    "  cpt.Ccpt_NoLojaCompra,\n" +
                    "  cpt.Ccpt_NoPDV,\n" +
                    "  cpt.Ccpt_NoTransacao"*/
                    "SELECT\n" +
                    "  ccp.Ccp_NoCliente,\n" +
                    "  c.Cli_CartProfissional,\n" +
                    "  cast('2016.08.31' as date) emissao,\n" +
                    "  c.Cli_Nome,\n" +
                    "  cpt.Ccpt_NoCartao,\n" +
                    "  truncate(sum(cpt.Ccpt_Valor),2) Ccpt_Valor,\n" +
                    "  cpt.Ccpt_DataGeracao\n" +
                    "FROM\n" +
                    "  cartao_proprio_transacao cpt\n" +
                    "  join cartao_credito_proprio ccp on\n" +
                    "    cpt.CcpT_NoCartao = ccp.Ccp_NoCartao\n" +
                    "  join clientes c on c.Cli_NoCliente = ccp.Ccp_NoCliente\n" +
                    "where\n" +
                    "  cpt.Ccpt_Fechada = 0 and\n" +
                    "  not Ccpt_Valor is null and\n" +
                    "  not Ccpt_DataGeracao is null\n" +
                    //"  and ccp.Ccp_NoCliente = 46922213\n" +
                    "group by\n" +
                    "  ccp.Ccp_NoCliente,\n" +
                    "  c.Cli_CartProfissional,\n" +
                    "  emissao,\n" +
                    "  c.Cli_Nome,\n" +
                    "  cpt.Ccpt_NoCartao,\n" +
                    "  cpt.Ccpt_DataGeracao"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("Ccp_NoCliente"));
                    //oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("emissao"));
                    //Tenta extrair o código do cupom da String
                    /*int cupom = 0;
                    String documento = rst.getString("Ccpt_Documento");
                    //É cupom fiscal
                    if (documento.contains("COO")) {
                        cupom = Utils.stringToInt(documento.substring(documento.indexOf("COO")));
                    } else {
                        cupom = rst.getInt("Ccpt_CodNF");
                    }*/
                    oReceberCreditoRotativo.setNumerocupom(0);
                    //oReceberCreditoRotativo.setEcf(rst.getInt("Ccpt_NoPDV"));
                    oReceberCreditoRotativo.setValor(rst.getDouble("Ccpt_Valor"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR");
                    /*oReceberCreditoRotativo.setObservacao("IMPORTADO VR\n " + 
                            "   LOJA - " + rst.getString("Ccpt_NoLojaCompra") + "\n" +
                            "   PDV - " + rst.getString("Ccpt_NoPDV") + "\n" +
                            "   TRANSACAO - " + rst.getString("Ccpt_NoTransacao") + "\n" +
                            "   TIPO TRANSACAO - " + rst.getString("Ccpt_TipoTrans")
                    );*/
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("Ccpt_DataGeracao"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    public void corrigirEstoque(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...Correção...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.alterarEstoqueProdutoSomando(new ArrayList<>(aux.values()), idLojaVR);
    }

    public void copiarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Produtos...Lista de Produtos...");
        
        List<ProdutoVO> produtos = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.prd_NoProd,\n" +
                    "  p.Prd_Descricao,\n" +
                    "  p.Prd_DescAbrev,\n" +                    
                    "  p.Prd_CodBarra\n" +
                    "from\n" +
                    "  dbtabelas.produto p\n" +
                    "  left join dbtabelas.preco pr on pr.Pco_NoProd = p.Prd_NoProd and pr.Pco_NoLoja = " + idLojaCliente + "\n" +
                    "  left join dbtabelas.estoque est on est.Et_NoProd = p.Prd_NoProd and est.Et_NoLoja = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "  p.prd_NoProd"
            )) {
                while (rst.next()) {
                    ProdutoVO produto = new ProdutoVO();
                    produto.setId(rst.getInt("prd_NoProd"));
                    produto.setDescricaoCompleta(rst.getString("Prd_Descricao"));
                    produto.setDescricaoReduzida(rst.getString("Prd_DescAbrev"));
                    produto.setCodigoBarras(Utils.stringToLong(rst.getString("Prd_CodBarra")));
                    produtos.add(produto);
                }
            }
        }
        
        Conexao.begin();
        ProgressBar.setStatus("Gravando...Produtos...Lista de Produtos...");
        ProgressBar.setMaximum(produtos.size());
        try {
            
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("create table if not exists implantacao.produtoscosmos (id bigint, ean varchar(25), descricao varchar(100), descricaored varchar(100));");
                stm.execute("delete from implantacao.produtoscosmos;");
                
                for (ProdutoVO produto: produtos) {
                    stm.execute("insert into implantacao.produtoscosmos values (" + 
                            produto.getId() + "," + 
                            Utils.longIntSQL(produto.getCodigoBarras(), -2) + "," + 
                            Utils.quoteSQL(produto.getDescricaoCompleta()) + "," + 
                            Utils.quoteSQL(produto.getDescricaoReduzida()) + ");");
                    ProgressBar.next();
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
        
    }
    
    public void importarSenhaClientePreferencial(int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando as senhas...");
        List<ClientePreferencialVO> clientes = carregarSenha();
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.utilizarCodigoAnterior = true;
        dao.manterID = true;
        dao.corrigirSenhas(clientes, false, idLojaCliente);
    }
    
    private List<ClientePreferencialVO> carregarSenha() throws Exception{
        List<ClientePreferencialVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  c.Cli_CartProfissional codigo_atual,\n" +
                    "  c.Cli_NoCliente codigo_anterior,\n" +
                    "  Cli_Senha senha,\n" +
                    "  Cli_Nome razao,\n" +
                    "  Cli_Cpf_Cgc cnpj\n" +
                    "FROM\n" +
                    "  clientes c\n" +
                    "where\n" +
                    "  not c.Cli_senha is null\n" +
                    "  and not c.Cli_CartProfissional is null\n" +
                    "  and trim(c.Cli_Senha) != ''\n" +
                    "order by codigo_atual"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setId(rst.getInt("codigo_atual"));
                    vo.setCodigoanterior(rst.getLong("codigo_anterior"));
                    vo.setSenha(rst.getInt("senha"));
                    vo.setNome(rst.getString("razao"));
                    vo.setCnpj(rst.getString("cnpj"));
                    result.add(vo);
                }
            }
        }
        return result;
    }
    

    
}
