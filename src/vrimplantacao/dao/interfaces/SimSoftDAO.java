package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.classe.file.Log;
import vrimplantacao.classe.file.LogAdicional;
import vrimplantacao.classe.file.LogFileType;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaItemVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

/**
 *
 * @author Leandro
 */
public class SimSoftDAO extends AbstractIntefaceDao {
    
    private int idPreco = 1;
    private String uf = null;
    public int idLojaCliente;
    
    public void setPreco(int idPreco) {
        this.idPreco = idPreco;
    }
    
    private String getUfEmpresa(int idLojaCliente) throws Exception {
        if (uf == null) {
            uf = "SP";
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                    "select mun.MUN_SIG_UF from cademp emp join cadmun mun on "
                            + "emp.EMP_CODMUN = mun.MUN_CODIGO "
                            + "where emp.EMP_CODIGO = " + idLojaCliente
                )) {
                    if (rst.next()) {
                        uf = rst.getString("MUN_SIG_UF");
                    }
                }
            }
        }
        return uf;
    }
    
    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select emp_codigo, emp_nomemp from cademp order by emp_codigo"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("emp_codigo"),  rst.getString("emp_codigo") + " - " + rst.getString("emp_nomemp")));
                }
            }
        }        
        return result;
    }
    
    public List<ItemComboVO> carregarPrecos() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pre_codigo, pre_descri from cadpre order by PRE_CODIGO"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("pre_codigo"),  rst.getString("pre_codigo") + " - " + rst.getString("pre_descri")));
                }
            }
        }        
        return result;
    }
    
    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	SIM_CODIGO,\n" +
                    "	SIM_DESCRI\n" +
                    "FROM \n" +
                    "	CADSIM\n" +
                    "order by SIM_CODIGO"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(rst.getInt("SIM_CODIGO"));
                    familiaVO.setIdLong(rst.getLong("SIM_CODIGO"));
                    familiaVO.setDescricao(rst.getString("SIM_DESCRI"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	gru_codigo merc1,\n" +
                    "	0 as merc2,\n" +
                    "	0 as merc3,\n" +
                    "	gru_descri descricao\n" +
                    "from\n" +
                    "	cadgru\n" +
                    "union\n" +
                    "select\n" +
                    "	g.GRU_CODIGO merc1,\n" +
                    "	coalesce(cs.SUB_CODIGO, 1) merc2,\n" +
                    "	0 as merc3,\n" +
                    "	coalesce(cs.SUB_DESCRI, g.gru_descri) descricao\n" +
                    "from\n" +
                    "	cadgru g\n" +
                    "	left join cadsub cs on cs.SUB_CODGRU = g.gru_codigo\n" +
                    "union\n" +
                    "select\n" +
                    "	g.GRU_CODIGO merc1,\n" +
                    "	coalesce(cs.SUB_CODIGO, 1) merc2,\n" +
                    "	1 as merc3,\n" +
                    "	coalesce(cs.SUB_DESCRI, g.gru_descri) descricao\n" +
                    "from\n" +
                    "	cadgru g\n" +
                    "	left join cadsub cs on cs.SUB_CODGRU = g.gru_codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();                    
                    result.add(oMercadologico);
                    
                    oMercadologico.setMercadologico1(rst.getInt("merc1"));
                    if (nivel > 1) {
                        oMercadologico.setMercadologico2(rst.getInt("merc2"));
                    }
                    if (nivel > 2) {
                        oMercadologico.setMercadologico3(rst.getInt("merc3"));
                    }
                    oMercadologico.setDescricao(rst.getString("descricao"));
                    oMercadologico.setNivel(nivel);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void importarMercadologico() throws Exception{
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
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {            
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.ITE_CODIGO id,\n" +
                "	p.ITE_DESCRI +  isnull(' ' + p.ITE_REFERE, '') descricao,\n" +
                "	p.ITE_ATIVO_ id_situacaocadastro,\n" +
                "	p.ITE_DTACAD datacadastro,\n" +
                "	p.ITE_CODGRU merc1,\n" +
                "	p.ITE_CODSUB merc2,\n" +
                "	1 as merc3,\n" +
                "	p.ITE_NFENCM ncm,\n" +
                "	null as cest,\n" +
                "	p.ite_codsim id_familia,\n" +
                "	preco.MPR_MARGEM margem,\n" +
                "	p.ITE_QTDEMB qtdembalagem,\n" +
                "	ean.CBR_CODBAR codigobarras,\n" +
                "	0 as validade,\n" +
                "	p.ITE_UNDCOM id_tipoembalagem,\n" +
                "	p.ITE_PESBRU pesobru,\n" +
                "	p.ITE_PESLIQ pesoliq,\n" +
                "	p.ITE_CSTPIS pisconfinscstsai,\n" +
                "	p.ITE_CSTPIS_ENT piscofinscstent,\n" +
                "	p.ITE_IDNATU piscofinsnatreceita,\n" +
                "	preco.MPR_PREFIN preco,\n" +
                "	p.ITE_CUSBRU custosemimposto,\n" +
                "	p.ITE_CUSREA custocomimposto,\n" +
                "	est.EST_SALDOS estoque,\n" +
                "	est.EST_ESTMIN estoquemin,\n" +
                "	p.ITE_CSTRIB icmscst,\n" +
                "	p.ITE_ALQICM icmsaliq,\n" +
                "	p.ITE_REDICM icmsred\n" +
                "from\n" +
                "	cadite p\n" +
                "	left join movpre preco on p.ITE_CODIGO = preco.MPR_CODITE and preco.MPR_CODPRE = " + idPreco + "\n" +
                "	left join cadcbr ean on ean.CBR_CODITE = p.ITE_CODIGO\n" +
                "	left join cadest est on est.EST_CODITE = p.ITE_CODIGO and est.EST_CODEMP = " + idLojaCliente + "\n" +
                "order by\n" +
                "	ITE_CODIGO"
            )) {
                
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
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
                    oProduto.setDescricaoCompleta(rst.getString("descricao"));
                    oProduto.setDescricaoReduzida(rst.getString("descricao"));
                    oProduto.setDescricaoGondola(rst.getString("descricao"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    if (rst.getString("datacadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("datacadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("merc1"));
                    oProduto.setMercadologico2(rst.getInt("merc2"));
                    oProduto.setMercadologico3(rst.getInt("merc3"));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
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
                    
                    long codigoBarra = Utils.stringToLong(rst.getString("codigobarras"),-2);
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        oProduto.eBalanca = true;
                        
                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }
                        
                        oProduto.eBalanca = true;
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        if (String.valueOf(codigoBarra).length() >= 7 && 
                                String.valueOf(codigoBarra).length() <= 14) {
                            oAutomacao.setCodigoBarras(codigoBarra);
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
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
                    oProduto.setPesoBruto(rst.getDouble("pesobru"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliq"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("pisconfinscstsai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("piscofinscstent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("piscofinsnatreceita")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    oComplemento.setEstoqueMaximo(0);                   

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(getUfEmpresa(idLojaCliente)));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS("SP", rst.getInt("icmscst"), rst.getDouble("icmsaliq"), rst.getDouble("icmsred")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS("SP", rst.getInt("icmscst"), rst.getDouble("icmsaliq"), rst.getDouble("icmsred")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS("SP", rst.getInt("icmscst"), rst.getDouble("icmsaliq"), rst.getDouble("icmsred")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS("SP", rst.getInt("icmscst"), rst.getDouble("icmsaliq"), rst.getDouble("icmsred")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS("SP", rst.getInt("icmscst"), rst.getDouble("icmsaliq"), rst.getDouble("icmsred")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(codigoBarra);
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("pisconfinscstsai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("piscofinscstent"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("piscofinsnatreceita"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("icmscst"));

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
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.ITE_CODIGO id,\n" +
                "	p.ITE_QTDEMB qtdembalagem,\n" +
                "	ean.CBR_CODBAR codigobarras,\n" +
                "	p.ITE_UNDCOM id_tipoembalagem\n" +
                "from\n" +
                "	cadite p\n" +
                "	left join cadcbr ean on ean.CBR_CODITE = p.ITE_CODIGO\n" +
                "order by\n" +
                "	ITE_CODIGO"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("codigobarras")));
                    //oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setIdTipoEmbalagem(1);
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    
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
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "	f.FOR_CODIGO id,\n" +
                "	f.FOR_DTACAD datacadastro,\n" +
                "	f.FOR_NOMFOR razao,\n" +
                "	f.FOR_FANTAS fantasia,\n" +
                "	f.FOR_ENDERE endereco,\n" +
                "	f.FOR_ENDNUM numero,\n" +
                "	f.FOR_ENDCPL complemento,\n" +
                "	f.FOR_BAIRRO bairro,\n" +
                "	f.FOR_CODMUN id_municipio,\n" +
                "	f.FOR_C_E_P_ cep,\n" +
                "	mun.MUN_COD_UF id_estado,\n" +
                "	f.FOR_FONE01 fone1,\n" +
                "	f.FOR_FONE02 fone2,\n" +
                "	f.FOR_IES_RG inscricaoestadual,\n" +
                "	f.FOR_CNPCPF cnpj,\n" +
                "	'' as observacao,\n" +
                "	null as fax,\n" +
                "	null as email,\n" +
                "	case f.for_inativ when 1 then 0 else 1 end as id_situacaocadastro\n" +
                "from \n" +
                "	cadfor f\n" +
                "	left join cadmun mun on f.FOR_CODMUN = mun.MUN_CODIGO\n" +
                "order by\n" +
                "	f.FOR_CODIGO"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro;
                    
                    if ((rst.getString("datacadastro") != null)
                            && (!rst.getString("datacadastro").isEmpty())) {
                        datacadastro = rst.getDate("datacadastro");                    
                    } else {
                        datacadastro = new Date(new java.util.Date().getTime()); 
                    }

                    oFornecedor.setId(rst.getInt("id"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setCodigoanterior(rst.getInt("id"));
                    oFornecedor.setRazaosocial(rst.getString("razao"));
                    oFornecedor.setNomefantasia(rst.getString("fantasia"));
                    oFornecedor.setEndereco(rst.getString("endereco"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    oFornecedor.setId_municipio(Utils.stringToInt(rst.getString("id_municipio")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    oFornecedor.setId_estado(rst.getInt("id_estado"));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setId_tipoinscricao(oFornecedor.getInscricaoestadual().length() > 9 ? 0 : 1);
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setFax(rst.getString("fax"));
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	pf.FIT_CODFOR id_fornecedor,\n" +
                "	f.FOR_CNPCPF cnpj,\n" +
                "	pf.FIT_CODITE id_produto,\n" +
                "	pf.FIT_CODFAB codigoexterno,\n" +
                "	pf.FIT_ULTCOM alteracao	\n" +
                "from\n" +
                "	forite pf\n" +
                "	join cadfor f on pf.FIT_CODFOR = f.FOR_CODIGO\n" +
                "where\n" +
                "	isnull(pf.FIT_CODFAB, '') != ''\n" +
                "order by\n" +
                "	pf.FIT_CODFOR,\n" +
                "	pf.FIT_CODITE"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setCnpFornecedor(Utils.stringToLong(rst.getString("cnpj")));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Utils.getEstadoPelaSigla(getUfEmpresa(idLojaCliente)));
                    vo.setCodigoexterno(rst.getString("codigoexterno"));
                    Date alteracao;
                    if (rst.getDate("alteracao") == null) {
                        alteracao = new Date(new java.util.Date().getTime());
                    } else {
                        alteracao = rst.getDate("alteracao");
                    }
                    vo.setDataalteracao(alteracao);
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {                       
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "	c.CLI_CODIGO id,\n" +
                "	c.CLI_NOMCLI nome,\n" +
                "	c.CLI_ENDERE res_endereco,\n" +
                "	c.CLI_ENDNUM res_numero,\n" +
                "	c.CLI_ENDCPL res_complemento,\n" +
                "	c.CLI_BAIRRO res_bairro,\n" +
                "	c.CLI_CODMUN res_id_municipio,\n" +
                "	mun.MUN_COD_UF res_uf,\n" +
                "	c.CLI_C_E_P_ res_cep,\n" +
                "	c.CLI_FONE01 telefone1,\n" +
                "	c.CLI_FONE02 telefone2,\n" +
                "	c.CLI_IES_RG inscricaoestadual,\n" +
                "	c.CLI_C_P_F_ cnpj,\n" +
                "	1 as sexo,\n" +
                "	c.CLI_DTACAD datacadastro,\n" +
                "	c.CLI_E_MAIL email,\n" +
                "	c.CLI_LIMITE limite,\n" +
                "	c.CLI_F_A_X_ fax,\n" +
                "	case c.CLI_ATIVO_ when 'N' then 1 else 0 end as bloqueado,\n" +
                "	1 as id_situacaocadastro,\n" +
                "	c.CLI_OBSGER observacaogeral,\n" +
                "	c.CLI_OBSCAX observacaocaixa,\n" +
                "	c.CLI_DTANSC datanascimento\n" +
                "from\n" +
                "	cadcli c\n" +
                "	left join cadmun mun on c.CLI_CODMUN = mun.MUN_CODIGO\n" +
                "order by\n" +
                "	c.CLI_CODIGO"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));
                    oClientePreferencial.setId_estado(rst.getInt("res_uf") == 0 ? Global.idEstado : rst.getInt("res_uf"));
                    oClientePreferencial.setId_municipio(rst.getInt("res_id_municipio") == 0 ? Global.idMunicipio : rst.getInt("res_id_municipio"));
                    oClientePreferencial.setCep(Utils.formatCep(rst.getString("res_cep")));
                    oClientePreferencial.setTelefone(rst.getString("telefone1"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getString("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setFax(rst.getString("fax"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClientePreferencial.setTelefone2(rst.getString("telefone2"));
                    String observacao = rst.getString("observacaogeral") + "  ..  " + rst.getString("observacaocaixa");
                    oClientePreferencial.setObservacao("IMPORTADO VR " + observacao);
                    oClientePreferencial.setDatanascimento(rst.getString("datanascimento"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);                

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	rec.CTR_CODCLI id_clientepreferencial,\n" +
                    "	c.CLI_C_P_F_ cnpj,\n" +
                    "	rec.CTR_DTAENT emissao,\n" +
                    "	rec.CTR_NUMDOC cupom,\n" +
                    "	rec.CTR_VALDUP valor,\n" +
                    "	isnull(rec.CTR_HISTOR, '') + '   ' + isnull(rec.CTR_HISREC, '') observacao,\n" +
                    "	rec.CTR_DTAVEN vencimento\n" +
                    "from\n" +
                    "	cadrec rec\n" +
                    "	join cadcli c on rec.CTR_CODCLI = c.CLI_CODIGO\n" +
                    "where\n" +
                    "	rec.CTR_DTAREC is null\n" +
                    "order by\n" +
                    "	rec.CTR_DTAENT"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("id_clientepreferencial"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cnpj")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("emissao"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("cupom")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("valor"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("vencimento"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    public void integrarProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        List<ProdutoVO> vProdutoAlterado = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Long, ProdutoVO> vProdutoOrigem = carregarProdutoIntegracao(idLojaVR, idLojaCliente); //carregarProdutoOrigemVrToVr(i_idLojaOrigem, i_connOrigem);
            Map<Long, Long> vProdutoDestino = new ProdutoDAO().carregarCodigoBarras();
            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Loja Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoOrigem.size() + vProdutoDestino.size());

            for (Long keyCodigoBarra : vProdutoOrigem.keySet()) {
                if (vProdutoDestino.containsKey(keyCodigoBarra)) {
                    long codigoProduto = vProdutoDestino.get(keyCodigoBarra);
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    oProduto.id = (int) codigoProduto;
                    vProdutoAlterado.add(oProduto);
                } else {
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    //oProduto.id = 0;
                    oProduto.idProdutoVasilhame = -1;
                    oProduto.idFamiliaProduto = -1;
                    oProduto.idFornecedorFabricante = Global.idFornecedor;
                    oProduto.excecao = -1;
                    oProduto.idTipoMercadoria = -1;
                    vProdutoNovo.add(oProduto);                    
                }
                ProgressBar.next();
            }

            if (!vProdutoAlterado.isEmpty()) {
                new ProdutoDAO().salvar(vProdutoAlterado, idLojaVR, true, null, true,0);
            }
            
            if (!vProdutoNovo.isEmpty()) {
                ProdutoDAO prod = new ProdutoDAO();
                prod.usarMercadologicoAcertar = true;
                prod.salvar(vProdutoNovo, idLojaVR, vLoja, true);
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public Date dataInicial = new Date(new java.util.Date().getTime());
    
    @Override
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String sql = "SELECT \n" +
                    "	i.IPR_CODITE id_produto,\n" +
                    "	p.PRO_DTAFIN datatermino,\n" +
                    "	i.IPR_PREPRO precooferta\n" +
                    "FROM \n" +
                    "	cadpro p\n" +
                    "	join itepro i on i.IPR_CODPRO = p.PRO_CODIGO \n" +
                    "where \n" +
                    "	p.pro_dtafin >= " + Utils.quoteSQL(format.format(dataInicial)) + "\n" +
                    "order by \n" +
                    "	p.PRO_DTAINI";
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setDatainicio(dataInicial);
                    vo.setDatatermino(rst.getDate("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }

    public void importarRotativoNaoRepedito(int idLoja, int idLojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...Não Repetido");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPdvVenda(int idLojaVR, int idLojaCliente, 
            Date dataInicial, boolean semSAT, boolean porEAN) throws Exception {
        ProgressBar.setStatus("Carregando vendas...");
        List<ImportacaoLogVendaVO> result = carregarPdvVendas(idLojaCliente, dataInicial, semSAT);
        List<DivergenciaVO> retorno = new LogVendaDAO().salvar(result, true, !porEAN, porEAN, idLojaVR);
        try (Log log = new Log("c:\\vr\\divergencias.html", "Divergencias das vendas", LogFileType.HTML)) {
            for (DivergenciaVO div: retorno) {
                log.addLog("" + div.tipo, div.descricao, new LogAdicional());
            }
        }
    }

    private List<ImportacaoLogVendaVO> carregarPdvVendas(int idLojaCliente, Date dataInicial, boolean semSAT) throws Exception {
        String sql = 
                "select\n" +
                "       v.FTG_CODEMP,\n" +
                "	v.FTG_NUMCAX,\n" +
                "	v.FTG_NUMDOC,\n" +
                "	v.FTG_ESPDOC,\n" +
                "	v.FTG_SEQCAI,\n" +
                "	v.FTG_CODEMP id_loja,\n" +
                "	v.FTG_ECFCOO numerocupom,\n" +
                "	v.FTG_ECFNCX ecf,\n" +
                "	v.FTG_DTAENT data,\n" +
                "	v.FTG_CODCLI id_clientepreferencial,\n" +
                "	500001 as matricula,\n" +
                "	v.FTG_HORENT horainicio,\n" +
                "	v.FTG_HORENT horatermino,\n" +
                "	case v.FTG_CANCEL when 'S' then 1 else 0 end as cancelado,\n" +
                "	v.FTG_TOTLQD subtotalimpressora,\n" +
                "	case v.FTG_CANCEL when 'S' then 500001 else null end as matriculacancelamento,\n" +
                "	case v.FTG_CANCEL when 'S' then 2 else null end as id_tipocancelamento,\n" +
                "	c.CLI_C_P_F_ cpf,\n" +
                "	0 as valordesconto,\n" +
                "	0 as valoracrescimo,\n" +
                "	case v.FTG_CANCEL when 'S' then 1 else 0 end as canceladoemvenda,\n" +
                "	v.FTG_ECFSER numeroserie,\n" +
                "	0 as mfadicional,\n" +
                "	v.FTG_ECFMOD modeloimpressora,\n" +
                "	FTG_CODVEN numerousuario,\n" +
                "	c.CLI_NOMCLI nomecliente,\n" +
                "	null as id_clienteeventual,\n" +
                "	isnull(cast(c.CLI_ENDERE as varchar),'') + isnull(' ' + cast(c.CLI_ENDNUM as varchar), '') + ISNULL(' ' + cast(c.CLI_BAIRRO as varchar), '') enderecocliente,\n" +
                ( !semSAT ? "	v.FTG_SAT_CHAVE chavecfe,\n" : "null as chavecfe,") +
                "	null as cpfcrm,\n" +
                "	null as cpfcnpjentidade,\n" +
                "	null as razaosocialentidade,\n" +
                "	null chavenfce,\n" +
                ( !semSAT ? "	null xml,\n" : "") +
                "	null protocolorecebimentonfce,\n" + 
                "	null datahoraemissaonfce,\n" +
                "	null datahorarecebimentonfce,\n" +
                "	null recibonfce\n" +
                "from \n" +
                "	fatger v \n" +
                "	left join cadcli c on v.FTG_CODCLI = c.CLI_CODIGO\n" +
                "where \n" +
                "	(" +
                    (
                        semSAT ?
                        "       isnull(v.FTG_ECFSER,'') != '' and v.FTG_ECFCOO > 0" :
                        "       (isnull(v.FTG_ECFSER,'') != '' and isnull(v.FTG_SAT_CHAVE,'') = '' and v.FTG_ECFCOO > 0) or\n" +
                        "	(isnull(v.FTG_SAT_CHAVE,'') != '' and isnull(v.FTG_ECFSER,'') = '')"
                    ) +
                "       ) and\n" +
                "	v.FTG_CODEMP = " + idLojaCliente  + " and\n" +
                "	v.FTG_DTAENT >= " + Utils.quoteSQL(new SimpleDateFormat("dd/MM/yyyy").format(dataInicial)) + "\n" +
                "order by \n" +
                "	v.FTG_DTAENT";
        List<ImportacaoLogVendaVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                while (rst.next()) {
                    ImportacaoLogVendaVO oVenda = new ImportacaoLogVendaVO();
                    result.add(oVenda);
                    
                    oVenda.idLoja = idLojaCliente;
                    oVenda.numeroCupom = Utils.stringToInt(rst.getString("numerocupom"));
                    oVenda.ecf = Utils.stringToInt(rst.getString("ecf"));
                    oVenda.data = dataFormat.format(rst.getDate("data"));
                    oVenda.idClientePreferencial = rst.getInt("id_clientepreferencial");
                    oVenda.matricula = rst.getInt("matricula");
                    oVenda.horaInicio = timeFormat.format(rst.getTime("horainicio"));
                    oVenda.horaTermino = timeFormat.format(rst.getTime("horatermino"));
                    oVenda.cancelado = rst.getBoolean("cancelado");
                    oVenda.subtotalImpressora = rst.getDouble("subtotalimpressora");
                    oVenda.matriculacancelamento = rst.getInt("matriculacancelamento");
                    oVenda.id_tipocancelamento = rst.getInt("id_tipocancelamento");
                    oVenda.canceladoEmVenda = false;
                    oVenda.cpf = Utils.stringToLong(rst.getString("cpf"));
                    oVenda.valorDesconto = rst.getDouble("valordesconto");
                    oVenda.valorAcrescimo = rst.getDouble("valoracrescimo");
                    oVenda.numeroSerie = rst.getString("numeroserie");
                    oVenda.mfadicional = rst.getInt("mfadicional");
                    oVenda.modeloImpressora = rst.getString("modeloimpressora");
                    //numerousuario
                    oVenda.nomeCliente = rst.getString("nomecliente");
                    //id_clienteeventual
                    oVenda.chavecfe = rst.getString("chavecfe");
                    //cpfcrm
                    //cpfcnpjentidade
                    //razaosocialentidade
                    oVenda.chavenfce = rst.getString("chavenfce");
                    oVenda.xml = null;    
                    
                    buildVendaItens(
                            rst.getInt("FTG_CODEMP"), 
                            rst.getString("FTG_NUMCAX"), 
                            rst.getInt("FTG_NUMDOC"), 
                            rst.getString("FTG_ESPDOC"), 
                            rst.getString("FTG_SEQCAI"), 
                            oVenda);                
                }
            }
        }
        return result;
    }

    private void buildVendaItens(int codEmpresa, String numCaixa, int numDoc,
            String espDoc, String seqCai, ImportacaoLogVendaVO venda) 
            throws SQLException {
        String sql2 =
                "select\n" +
                "	vi.FTD_CODITE id_produto,\n" +
                "	vi.FTD_POSCAN sequencia,\n" +
                "	vi.FTD_QTDITE quantidade,\n" +
                "	vi.FTD_UNITAR precovenda,\n" +
                "	vi.FTD_TOTITE valortotal,\n" +
                "	vi.FTD_SITTRI icms_cst,\n" +
                "	vi.FTD_ALQICM icms_aliq,\n" +
                "	case vi.FTD_CANCEL when 'S' then 1 else 0 end cancelado,\n" +
                "	case vi.FTD_CANCEL when 'S' then vi.FTD_TOTITE else 0 end valorcancelado,\n" +
                "	case vi.FTD_CANCEL when 'S' then 2 else null end id_tipocancelamento,\n" +
                "	case vi.FTD_CANCEL when 'S' then 500001 else null end matriculacancelamento,\n" +
                "	null contadordoc, \n" +
                "	0 valordesconto,\n" +
                "	0 valoracrescimo,\n" +
                "	0 valordescontocupom,\n" +
                "	0 valoracrescimocupom,\n" +
                "	'T' regracalculo,\n" +
                "	vi.FTD_CODBAR codigobarras,\n" +
                "	p.ITE_UNIDAD unidademedida,\n" +
                "	'' totalizadorparcial,\n" +
                "	null as sequencia,\n" +
                "	0 valoracrescimofixo,\n" +
                "	0 valordescontopromocao,\n" +
                "	0 oferta\n" +
                "from\n" +
                "	fatdet vi\n" +
                "	join cadite p on vi.FTD_CODITE = p.ITE_CODIGO\n" +
                "where\n" +
                "	vi.FTD_CODEMP = " + codEmpresa + " and\n" +
                "	vi.FTD_NUMCAX = " + Utils.quoteSQL(numCaixa) + " and\n" +
                "	vi.FTD_NUMDOC = " + numDoc + " and\n" +
                "	vi.FTD_ESPDOC = " + Utils.quoteSQL(espDoc) + " and\n" +
                "	vi.FTD_SEQCAI = " + Utils.quoteSQL(seqCai);
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql2)) {
                venda.vLogVendaItem.clear();
                double total = 0;
                while (rst.next()) {
                    ImportacaoLogVendaItemVO item = new ImportacaoLogVendaItemVO();
                    venda.vLogVendaItem.add(item);
                    
                    item.sequencia = Utils.stringToInt(rst.getString("sequencia"));
                    item.numeroCupom = venda.numeroCupom;
                    item.ecf = venda.ecf;
                    //item.idProduto = Utils.stringToInt("id_produto");
                    item.codigoAnterior = Utils.stringToLong(rst.getString("id_produto"));
                    item.quantidade = rst.getDouble("quantidade");
                    item.precoVenda = rst.getDouble("precovenda");
                    item.valorTotal = rst.getDouble("valortotal");
                    item.idAliquota = Utils.getAliquotaICMS(
                            Parametros.get().getUfPadrao().getSigla(), 
                            rst.getInt("icms_cst"), 
                            rst.getDouble("icms_aliq"), 0);
                    item.aliquota = "";
                    item.cancelado = rst.getBoolean("cancelado");
                    item.valorCancelado = rst.getDouble("valorcancelado");
                    item.idTipoCancelamento = venda.id_tipocancelamento;
                    item.matriculaCancelamento = venda.matriculacancelamento;
                    item.contadorDoc = venda.contadorDoc;
                    item.valorDesconto = rst.getDouble("valordesconto");
                    item.valorAcrescimo = rst.getDouble("valoracrescimo");
                    item.valorDescontoCupom = rst.getDouble("valordescontocupom");
                    item.valorAcrescimoCupom = rst.getDouble("valoracrescimocupom");
                    item.regraCalculo = rst.getString("regracalculo");
                    item.codigoBarras = Utils.stringToLong(rst.getString("codigobarras"), -2);
                    item.unidadeMedida = Utils.acertarTexto(rst.getString("unidademedida"), 3);
                    item.totalizadorParcial = rst.getString("totalizadorparcial");
                    item.numeroSerie = venda.numeroSerie;
                    item.numeroEcf = venda.ecf;
                    
                    total += item.valorTotal;
                }
                
                venda.valorTotal = total;
            }
        }
    }
 
    
}
