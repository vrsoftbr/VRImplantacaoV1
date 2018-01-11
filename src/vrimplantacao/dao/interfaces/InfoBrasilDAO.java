package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
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
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

public class InfoBrasilDAO extends AbstractIntefaceDao {
    
    public static final String SISTEMA = "INFO_BRASIL";
    private String loja = "1";

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getLoja() {
        return loja;
    }

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    loj_codigo id,\n" +
                    "    loj_codigo || ' - ' || loj_nome descricao\n" +
                    "from\n" +
                    "    lojas\n" +
                    "order by\n" +
                    "    loj_codigo"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "    p.pro_codigo id,\n" +
                    "    p.pro_descricao descricao\n" +
                    "from\n" +
                    "    prod_similares s\n" +
                    "    join produtos p on s.pro_codigo = p.pro_codigo\n" +
                    "order by\n" +
                    "    p.pro_codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setImpSistema(SISTEMA);
                    familiaVO.setImpLoja(getLoja());
                    familiaVO.setImpId(rst.getString("id"));
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    s.sec_codigo merc1,\n" +
                    "    s.sec_descricao merc1_desc,\n" +
                    "    coalesce(g.gru_codigo, 1) merc2,\n" +
                    "    coalesce(g.gru_descricao, s.sec_descricao) merc2_desc,\n" +
                    "    1 merc3,\n" +
                    "    coalesce(g.gru_descricao, s.sec_descricao) merc3_desc\n" +
                    "from\n" +
                    "    seccao s\n" +
                    "    left join grupospro g on s.sec_codigo = g.sec_codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO merc = new MercadologicoVO();
                    merc.setMercadologico1(rst.getInt("merc1"));
                    merc.setDescricao(rst.getString("merc1_desc"));
                    if (nivel > 1) {
                        merc.setMercadologico2(rst.getInt("merc2"));
                        merc.setDescricao(rst.getString("merc2_desc"));
                    }
                    if (nivel > 2) {
                        merc.setMercadologico3(rst.getInt("merc3"));
                        merc.setDescricao(rst.getString("merc3_desc"));
                    }
                    merc.setNivel(nivel);
                    
                    result.add(merc);
                }
            }
        }
        
        return result;
    } 

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            
            Map<String, String> familias = new LinkedHashMap<>();
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    s.prs_codigo,\n" +
                "    max(s.pro_codigo) pro_codigo\n" +
                "from PROD_SIMILARES s\n" +
                "group by\n" +
                "    s.prs_codigo"
            )) {
                while (rst.next()) {
                    familias.put(rst.getString("prs_codigo"), rst.getString("pro_codigo"));
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    p.pro_codigo id,\n" +
                "    p.pro_descricao descricaocompleta,\n" +
                "    p.pro_descfiscal descricaoreduzida,\n" +
                "    p.pro_descricao descricaogondola,\n" +
                "    case pro_situacao when 'I' then 0 else 1 end as id_situacaocadastro,\n" +
                "    p.pro_datacadastro datacadastro,\n" +
                "    g.sec_codigo mercadologico1,\n" +
                "    p.gru_codigo mercadologico2,\n" +
                "    1 as mercadologico3,\n" +
                "    p.pro_ncm ncm,\n" +
                "    cest.cis_cest cest,\n" +
                "    0 margem,\n" +
                "    p.pro_codigo ean,\n" +
                "    case p.pro_balanca when 'S' then 1 else 0 end as e_balanca,\n" +
                "    coalesce(p.pro_prazogarantia, 0) validade,\n" +
                "    case p.pro_balanca when 'S' then\n" +
                "        case p.pro_fracionado when 'S' then 'KG' else 'UN' end\n" +
                "    else\n" +
                "        p.pro_unidade\n" +
                "    end as id_tipoembalagem,\n" +
                "    p.pro_peso pesobruto,\n" +
                "    p.pro_pesoliquido pesoliquido,\n" +
                "    p.pis_codigo piscofins_cst_sai,\n" +
                "    p.pis_codigoent piscofins_cst_ent,\n" +
                "    null piscofins_natrec,\n" +
                "    e.pro_preco1 preco,\n" +
                "    p.pro_prccusto custosemimposto,\n" +
                "    p.pro_custofiscal custocomimposto,\n" +
                "    e.est_apoio estoque,\n" +
                "    e.est_minimo minimo,\n" +
                "    e.est_maximo maximo,\n" +
                "    p.icm_codigo icms_cst,\n" +
                "    p.pro_icms icms_aliq,\n" +
                "    p.pro_reducaoicms icms_reducao\n" +
                "from\n" +
                "    produtos p\n" +
                "    left join grupospro g on p.gru_codigo = g.gru_codigo\n" +
                "    join estoque e on e.pro_codigo = p.pro_codigo and e.loj_codigo = 1\n" +
                "    left join tabela_cest cest on cest.cis_codigo = e.cis_codigo\n" +
                "order by\n" +
                "    p.pro_codigo"
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
                    String idProduto = rst.getString("id");
                                      
                    oProduto.setIdDouble(Utils.stringToDouble(idProduto));
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    if (rst.getString("dataCadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("dataCadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("mercadologico1"));
                    oProduto.setMercadologico2(rst.getInt("mercadologico2"));
                    oProduto.setMercadologico3(rst.getInt("mercadologico3"));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("ncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    
                    oProduto.setFamiliaProduto(familias.get(idProduto));
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarra = -2;
                    if(rst.getBoolean("e_balanca")) {
                        String num = Utils.formataNumero(rst.getString("ean"));
                        if (num.matches("2[0-9]*00")) {
                            num = num.replaceAll("00$", "");
                            num = num.replaceAll("^2", "");
                            codigoBarra = Long.parseLong(num);
                        }
                    }                
                    
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    
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
                        
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                        
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
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("piscofins_cst_sai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("piscofins_cst_ent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("piscofins_natrec")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("minimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("maximo"));                   

                    String uf = Parametros.get().getUfPadrao().getSigla();
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(uf));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoAnteriorStr(idProduto);
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Utils.stringToLong(rst.getString("ean")));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("piscofins_cst_sai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("piscofins_cst_ent"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("piscofins_natrec"));
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
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.pro_codigo id_produto,\n" +                    
                    "    p.pro_codigo ean,\n" +    
                    "    case p.pro_balanca when 'S' then\n" +
                    "        case p.pro_fracionado when 'S' then 'KG' else 'UN' end\n" +
                    "    else\n" +
                    "        p.pro_unidade\n" +
                    "    end as id_tipoembalagem\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "order by\n" +
                    "    p.pro_codigo"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.setCodigoAnteriorStr(rst.getString("id_produto"));
                    oProduto.getvCodigoAnterior().add(oAnterior);
                    
                    oProduto.setIdDouble(Utils.stringToDouble(rst.getString("id_produto")));
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(1);
                    
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "    f.cre_codigo id,\n" +
                "    f.cre_datacadastro datacadastro,\n" +
                "    f.cre_nome razao,\n" +
                "    f.cre_fantasia fantasia,\n" +
                "    f.cre_endereco endereco,\n" +
                "    f.cre_numero numero,\n" +
                "    f.cre_compl_endereco complemento,\n" +
                "    f.cre_bairro bairro,\n" +
                "    f.mun_codigo cidade,\n" +
                "    f.cre_uf estado,\n" +
                "    f.cre_cep cep,\n" +
                "    f.cre_fone fone1,\n" +
                "    f.cre_fonerep fone2,\n" +
                "    f.cre_celular celular,\n" +
                "    f.cre_celularrep,\n" +
                "    f.cre_cgf inscricaoestadual,\n" +
                "    f.cre_cnpj cnpj,\n" +
                "    f.cre_email email,\n" +
                "    case f.cre_situacao when 'I' then 1 else 0 end as bloqueado\n" +
                "from\n" +
                "    credores f\n" +
                "order by\n" +
                "    f.cre_codigo"
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
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    oFornecedor.setId_municipio(rst.getInt("cidade"));
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("estado")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setObservacao("IMPORTADO VR");
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setBloqueado(rst.getBoolean("bloqueado"));
                    oFornecedor.setId_tipoindicadorie();
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    pf.pro_codigo id_produto,\n" +
                "    pf.cre_codigo id_fornecedor,\n" +
                "    pf.pra_codigo codigoexterno\n" +
                "from\n" +
                "    prod_fornec pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produtoStr(rst.getString("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Parametros.get().getUfPadrao().getId());
                    vo.setCodigoexterno(rst.getString("codigoexterno"));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "    c.cli_codigo id,\n" +
                "    c.cli_nome nome,\n" +
                "    c.cli_endereco res_endereco,\n" +
                "    c.cli_numero res_numero,\n" +
                "    c.cli_compl_endereco res_complemento,\n" +
                "    c.cli_bairro res_bairro,\n" +
                "    c.mun_codigo res_cidade,\n" +
                "    c.cli_uf res_uf,\n" +
                "    c.cli_cep res_cep,\n" +
                "    c.cli_fone fone1,\n" +
                "    c.cli_celular2 fone2,\n" +
                "    c.cli_celular celular,\n" +
                "    c.cli_fax fax,\n" +
                "    c.cli_identidade inscricaoestadual,\n" +
                "    c.cli_cpf_cnpj cnpj,\n" +
                "    case c.cli_sexo when 'F' then 0 else 1 end sexo,\n" +
                "    c.cli_diavenc prazodias,\n" +
                "    c.cli_email email,\n" +
                "    c.cli_datacadastro datacadastro,\n" +
                "    c.cli_limite limitepreferencial,\n" +
                "    case c.cli_bloqueio when 'S' then 1 else 0 end bloqueado,\n" +
                "    case c.cli_situacao when 'I' then 0 else 1 end id_situacaocadastro,\n" +
                "    c.cli_datanasc datanascimento,\n" +
                "    c.cli_pai nomePai,\n" +
                "    c.cli_mae nomeMae,\n" +
                "    c.cli_cargo cargo,\n" +
                "    c.cli_renda salario,\n" +
                "    case c.cli_estadocivil\n" +
                "    when 'C' then 2\n" +
                "    when 'S' then 1\n" +
                "    when 'V' then 3\n" +
                "    else 0 end\n" +
                "    estadocivil,\n" +
                "\n" +
                "    c.cli_endereco_ent,\n" +
                "    c.cli_numero_ent,\n" +
                "    c.cli_compl_endereco_ent,\n" +
                "    c.cli_bairro_ent,\n" +
                "    c.mun_codent,\n" +
                "    c.cli_uf_ent,\n" +
                "    c.cli_cep_ent,\n" +
                "\n" +
                "    c.cli_endereco_cob,\n" +
                "    c.cli_numero_cob,\n" +
                "    c.cli_compl_endereco_cob,\n" +
                "    c.cli_bairro_cob,\n" +
                "    c.mun_codent,\n" +
                "    c.cli_uf_cob,\n" +
                "    c.cli_cep_cob\n" +
                "from\n" +
                "    clientes c\n" +
                "order by c.cli_codigo"
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
                    oClientePreferencial.setId_municipio(rst.getInt("res_cidade"));                     
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.getString("res_uf")));
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setTelefone2(rst.getString("fone2"));
                    oClientePreferencial.setCelular(rst.getString("celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                        oClientePreferencial.setCnpj(-1);
                    }
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("PRAZODIAS"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limitepreferencial"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClientePreferencial.setObservacao("IMPORTADO VR");
                    oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));               

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    r.cli_codigo,\n" +
                    "    c.cli_cpf_cnpj,\n" +
                    "    r.rec_datalanc,\n" +
                    "    r.rec_doc,\n" +
                    "    r.rec_valor,\n" +
                    "    r.rec_observacoes,\n" +
                    "    r.rec_datavenc\n" +
                    "from\n" +
                    "    contasreceber r\n" +
                    "    join clientes c on r.cli_codigo = c.cli_codigo\n" +
                    "where\n" +
                    "    r.rec_datapag is null and\n" +
                    "    r.fpg_codigo in (5,7)\n" +
                    "order by\n" +
                    "    r.rec_datalanc"
            )) {                
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("cli_codigo"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cli_cpf_cnpj")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("rec_datalanc"));
                    String cupom = rst.getString("rec_doc") != null ? rst.getString("rec_doc") : "";
                    if (cupom.matches("[0-9]*-[0-9]*-[0-9]\\/[0-9]{2}")) {
                        String[] array = cupom.split("(-|\\/)");
                        cupom = array[1];
                    } else if (cupom.matches("[0-9]*.*")) {
                        cupom = cupom.replaceAll("(-|/).*", "");
                    }
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(cupom));
                    oReceberCreditoRotativo.setValor(rst.getDouble("rec_valor"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString("rec_observacoes"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("rec_datavenc"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        List<ProdutoVO> produtos = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        ProgressBar.setMaximum(produtos.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.setImportSistema(SISTEMA);
        produto.setImportLoja(getLoja());
        produto.implantacaoExterna = true;
        produto.salvar(produtos, idLojaVR, vLoja);
    }
    
    @Override
    public void importarProdutoMantendoCodigoDeBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos mantendo balança.....");
      
        List<ProdutoVO> produtos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(produtos.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: produtos) {
            if (prod.eBalanca) {
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
            ProgressBar.next();
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.setImportSistema(SISTEMA);
        produto.setImportLoja(getLoja());
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);
        
        
        produto.usarCodigoBalancaComoID = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }
    
    @Override
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");

        List<ProdutoVO> produtos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        ProgressBar.setMaximum(produtos.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarPrecoProdutoRapido(produtos, idLojaVR);
    }
    
    @Override
    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Custo...");

        List<ProdutoVO> produtos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        ProgressBar.setMaximum(produtos.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarCustoProdutoRapido(produtos, idLojaVR);
    }
    
    @Override
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception{
        
        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");

        List<ProdutoVO> produtos = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        
        ProgressBar.setMaximum(produtos.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.alterarEstoqueProdutoRapido(produtos, idLojaVR);
    }
    
}
