package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
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
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class ControlWareDAO extends AbstractIntefaceDao {

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codestabelec id, razaosocial descricao "
                            + "from estabelecimento order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"),rst.getInt("id") + " - " + rst.getString("descricao")));
                }
            }
        }
        return result;
    }    

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	dep.coddepto id_merc1,\n" +
                    "	dep.nome merc1,\n" +
                    "	grp.codgrupo id_merc2,\n" +
                    "	grp.descricao merc2,\n" +
                    "	sgr.codsubgrupo id_merc3,\n" +
                    "	sgr.descricao merc3\n" +
                    "from\n" +
                    "	departamento dep\n" +
                    "	left join grupoprod grp on dep.coddepto = grp.codgrupo\n" +
                    "	left join subgrupo sgr on grp.codgrupo = sgr.codgrupo\n" +
                    "order by\n" +
                    "	id_merc1,\n" +
                    "	id_merc2,\n" +
                    "	id_merc3"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("id_merc1"));
                    vo.setDescricao(rst.getString("merc1"));
                    if (nivel > 1) {
                        vo.setMercadologico2(rst.getInt("id_merc2"));
                        vo.setDescricao(rst.getString("merc2"));
                    }
                    if (nivel > 2) {
                        vo.setMercadologico3(rst.getInt("id_merc3"));
                        vo.setDescricao(rst.getString("merc3"));
                    }
                    vo.setNivel(nivel);
                    
                    result.add(vo);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	codfamilia,\n" +
                    "	descricao\n" +
                    "from \n" +
                    "	familia \n" +
                    "order by \n" +
                    "	codfamilia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("codfamilia"));
                    vo.setIdLong(rst.getInt("codfamilia"));
                    vo.setCodigoant(rst.getInt("codfamilia"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.add(vo);
                }
            }        
        }
        return result;
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {   
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.codproduto id,\n" +
                "	p.descricaofiscal descricaocompleta,\n" +
                "	p.descricao descricaoreduzida,\n" +
                "	p.descricaofiscal descricaogondola,\n" +
                "	case p.foralinha\n" +
                "	when 'S' then 0\n" +
                "	else 1 end as id_Situacaocadastro,\n" +
                "	p.datainclusao dataCadastro,\n" +
                "	p.coddepto mercadologico1,\n" +
                "	p.codgrupo mercadologico2,\n" +
                "	p.codsubgrupo mercadologico3,\n" +
                "       case when p.pesado = 'S' then true else false end as e_balanca,\n" +
                "	replace(ncm.codigoncm,'.','') ncm,\n" +
                "	replace(cest.cest,'.','') cest,\n" +
                "	codfamilia id_familiaproduto,\n" +
                "	pe.margemvrj margem,\n" +
                "	emb.quantidade qtdEmbalagem,\n" +
                "	coalesce(ean.codean,'') codigobarras,\n" +
                "	case when pe.diasvalidade = 0 then p.diasvalidade else pe.diasvalidade end as validade,\n" +
                "	un.sigla id_tipoEmbalagem,\n" +
                "	piscofinsent.codcst idTipoPisCofinsCredito,\n" +
                "	piscofinssai.codcst idTipoPisCofinsDebito,\n" +
                "	natr.codigo naturezaReceita,\n" +
                "	pe.precovrj precovenda,\n" +
                "	pe.custorep custocomnota,\n" +
                "	pe.custorep custosemnota,\n" +
                "	pe.sldsaida estoque,\n" +
                "	pe.estminimo estoque_minimo,\n" +
                "	pe.estmaximo estoque_maximo,\n" +
                "	estab.uf idEstado,\n" +
                "	cast(icms_s.codcst as integer) AS icms_s_cst,\n" +
                "	icms_s.aliqicms icms_s_aliq,\n" +
                "	icms_s.aliqredicms icms_s_reducao,\n" +
                "	cast(icms_e.codcst as integer) AS icms_e_cst,\n" +
                "	icms_e.aliqicms icms_e_aliq,\n" +
                "	icms_e.aliqredicms icms_e_reducao,\n" +
                "	p.pesoliq pesoliquido,\n" +
                "	p.pesobruto\n" +
                "from \n" +
                "	produto p\n" +
                "	join produtoestab pe on pe.codproduto = p.codproduto\n" +
                "	join ncm on ncm.idncm = p.idncm\n" +
                "	left join cest on ncm.idcest = cest.idcest\n" +
                "	join embalagem emb on p.codembalvda = emb.codembal\n" +
                "	join unidade un on emb.codunidade = un.codunidade\n" +
                "	left join produtoean ean on ean.codproduto = p.codproduto\n" +
                "	join piscofins piscofinsent ON p.codpiscofinsent = piscofinsent.codpiscofins\n" +
                "	join piscofins piscofinssai ON p.codpiscofinssai = piscofinssai.codpiscofins	\n" +
                "	left join natreceita natr on natr.natreceita = p.natreceita\n" +
                "	join estabelecimento estab on pe.codestabelec = estab.codestabelec\n" +
                "	join classfiscal icms_s ON p.codcfpdv = icms_s.codcf\n" +
                "	join classfiscal icms_e ON p.codcfnfe = icms_e.codcf\n" +
                "where	estab.codestabelec = " + idLojaCliente + "\n" +
                "order by\n" +
                "	p.codproduto;"
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
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
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
                    } else {
                        oProduto.setNcm1(9701);
                        oProduto.setNcm2(90);
                        oProduto.setNcm3(0);
                    }
                    
                    if ((rst.getString("cest") != null) &&
                        (!rst.getString("cest").trim().isEmpty())) {
                    
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

                    oProduto.setIdFamiliaProduto(rst.getString("id_familiaproduto") != null ? rst.getInt("id_familiaproduto") : -1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarra;                    
                    boolean eanValido = !rst.getString("codigobarras").equals("") && rst.getString("codigobarras").matches("[0-9]*");
                    if(!rst.getBoolean("e_balanca") && eanValido) {
                        codigoBarra = rst.getLong("codigobarras") <= Integer.MAX_VALUE ? rst.getInt("codigobarras") : -1;
                    } else {
                        codigoBarra = (long) oProduto.getId();
                    }
                    
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
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoEmbalagem")));
                        
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
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("idTipoPisCofinsDebito")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("idTipoPisCofinsCredito")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("naturezaReceita")));     
                    
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomnota"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemnota"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estoque_maximo"));                   

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(rst.getString("idEstado")));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(getAliquotaICMS(rst.getInt("icms_s_cst"), rst.getDouble("icms_s_aliq"), rst.getDouble("icms_s_reducao")));
                    oAliquota.setIdAliquotaCredito(getAliquotaICMS(rst.getInt("icms_e_cst"), rst.getDouble("icms_e_aliq"), rst.getDouble("icms_e_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(getAliquotaICMS(rst.getInt("icms_s_cst"), rst.getDouble("icms_s_aliq"), rst.getDouble("icms_s_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(getAliquotaICMS(rst.getInt("icms_e_cst"), rst.getDouble("icms_e_aliq"), rst.getDouble("icms_e_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(getAliquotaICMS(rst.getInt("icms_s_cst"), rst.getDouble("icms_s_aliq"), rst.getDouble("icms_s_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Utils.stringToLong(rst.getString("codigoBarras")));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("idTipoPisCofinsDebito"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("idTipoPisCofinsCredito"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("naturezaReceita"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("icms_s_cst"));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                }
                return vProduto;
            }
        }
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    
                    
                    
                    result.add(vo);
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        
        Map<Long, ProdutoVO> vProduto = new LinkedHashMap<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {   
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.codproduto id,\n" +
                "	emb.quantidade qtdEmbalagem,\n" +
                "	ean.codean codigobarras,	\n" +
                "	un.sigla id_tipoEmbalagem\n" +
                "from \n" +
                "	produto p\n" +
                "	join embalagem emb on p.codembalvda = emb.codembal\n" +
                "	join unidade un on emb.codunidade = un.codunidade\n" +
                "	join produtoean ean on ean.codproduto = p.codproduto\n" +
                "order by\n" +
                "	p.codproduto;"
            )) {
                
                while (rst.next()) {      
                    
                    long codigobarras;
                    
                    if ((rst.getString("codigobarras") != null) &&
                            (!rst.getString("codigobarras").trim().isEmpty())) {
                        codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("codigobarras")));
                    } else {
                        codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("codigobarras")));
                    }                    
                    
                    if (String.valueOf(codigobarras).length() >= 7) {                    
                        //Instancia o produto
                        ProdutoVO oProduto = new ProdutoVO();
                        //Prepara as variáveis
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                        //Inclui elas nas listas
                        oProduto.getvAutomacao().add(oAutomacao);
                        oProduto.getvCodigoAnterior().add(oCodigoAnterior);

                        oProduto.setId(rst.getInt("id"));  
                        oProduto.idDouble = rst.getInt("id");
                        oAutomacao.setCodigoBarras(codigobarras);
                        oAutomacao.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoEmbalagem")));

                        vProduto.put(codigobarras, oProduto);                        
                    }
                }                
            }
        }
        
        return vProduto;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	pf.codproduto id_produto, \n" +
                "	pf.codfornec id_fornecedor,\n" +
                "	pf.reffornec codigoexterno,\n" +
                "	f.uf\n" +
                "from \n" +
                "	prodfornec pf\n" +
                "	join fornecedor f on pf.codfornec = f.codfornec\n" +
                "order by \n" +
                "	pf.codproduto, \n" +
                "	pf.codfornec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf")));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    
    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
        
        String razaosocial, nomefantasia, endereco, bairro, inscricaoestadual,
                telefone1, telefone2, numero, complemento, obs, fax, email;
        Long cnpj, cep;
        String datacadastro;
        int id_tipoinscricao, id_municipio = 0, id_estado, IdFornecedor;
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.execute("set client_encoding='WIN1252'");
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "	f.codfornec id,\n" +
                "	f.razaosocial razao,\n" +
                "	f.nome fantasia,\n" +
                "	f.cpfcnpj cnpj,\n" +
                "	f.rgie inscricaoestadual,\n" +
                "	f.fone1,\n" +
                "	f.fone2,\n" +
                "	f.fone3,\n" +
                "	f.fax,\n" +
                "	f.site,\n" +
                "	f.endereco,\n" +
                "	f.numero,\n" +
                "	f.complemento,\n" +
                "	f.bairro,\n" +
                "	f.cep,\n" +
                "	c.codoficial id_municipio, \n" +
                "	e.codoficial id_estado,\n" +
                "	f.observacao,\n" +
                "	f.datainclusao datacadastro,\n" +
                "	f.email,\n" +
                "       case f.status when 'A' then 1 else 0 end as id_situacaocadastro\n" +
                "from \n" +
                "	fornecedor f\n" +
                "	left join cidade c on f.codcidade = c.codcidade\n" +
                "	left join estado e on c.uf = e.uf"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    IdFornecedor = rst.getInt("id");
                
                    if ((rst.getString("razao") != null)
                            && (!rst.getString("razao").isEmpty())) {
                        razaosocial = Utils.acertarTexto(rst.getString("razao").replace("'", ""));
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("fantasia") != null)
                            && (!rst.getString("fantasia").isEmpty())) {
                        nomefantasia = Utils.acertarTexto(rst.getString("fantasia").replace("'", ""));
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj")));
                    } else {
                        cnpj = Long.parseLong("0");
                    }

                    if ((rst.getString("inscricaoestadual") != null)
                            && (!rst.getString("inscricaoestadual").isEmpty())) {
                        inscricaoestadual = Utils.formataNumero(rst.getString("inscricaoestadual").replace("'", ""));
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if (inscricaoestadual.length() > 9) {
                        id_tipoinscricao = 0;
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").isEmpty())) {
                        telefone1 = Utils.acertarTexto(rst.getString("fone1"));
                    } else {
                        telefone1 = "0000000000";
                    }

                    if ((rst.getString("endereco") != null)
                            && (!rst.getString("endereco").isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("endereco").replace("'", ""));
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("bairro").replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("numero") != null)
                            && (!rst.getString("numero").isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("numero").replace("'", ""));
                    } else {
                        numero = "";
                    }

                    if ((rst.getString("complemento") != null)
                            && (!rst.getString("complemento").isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("complemento").replace("'", ""));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("cep")));
                    } else {
                        cep = Long.parseLong("0");
                    }

                    if ((rst.getString("observacao") != null)
                            && (!rst.getString("observacao").isEmpty())) {
                        obs = Utils.acertarTexto(rst.getString("observacao").replace("'", ""));
                    } else {
                        obs = "";
                    }

                    if ((rst.getString("id_municipio") != null)
                            && (!rst.getString("id_municipio").isEmpty())) {
                        id_municipio = rst.getInt("id_municipio");
                    } else {
                        id_municipio = 3525508;
                    }

                    if ((rst.getString("id_estado") != null)
                            && (!rst.getString("id_estado").isEmpty())) {
                        id_estado = rst.getInt("id_estado");
                    } else {
                        id_estado = 35;
                    }

                    if ((rst.getString("datacadastro") != null)
                            && (!rst.getString("datacadastro").isEmpty())) {
                        datacadastro = Util.formatDataGUI(rst.getDate("datacadastro"));                    
                    } else {
                        datacadastro = Util.formatDataGUI(new java.util.Date());                    
                    }

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").isEmpty())) {
                        telefone2 = Utils.formataNumero(rst.getString("fone2"));
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").isEmpty())) {
                        fax = Utils.formataNumero(rst.getString("fax"));
                    } else {
                        fax = "";
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").isEmpty())) {
                        email = Utils.acertarTexto(rst.getString("email").replace("'", ""));
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

                    oFornecedor.codigoanterior = IdFornecedor;
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
                    oFornecedor.observacao = obs;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.fax = fax;
                    oFornecedor.email = email;
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
               dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
               telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
               conjuge = "", orgaoExp = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha=0,
            estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;

        try {
            stm = ConexaoPostgres.getConexao().createStatement();
            stm.execute("set client_encoding='WIN1252';");
            
            rst = stm.executeQuery(                
                "select\n" +
                "	c.codcliente id,\n" +
                "	case c.codstatus when 1 then 1 when 2 then 1 else 0 end id_situacaocadastro,\n" +
                "	case c.tppessoa when 'F' then 1 else 0 end id_tipoinscricao,\n" +
                "	coalesce(c.razaosocial, c.nome) nome,\n" +
                "	c.enderres res_end,\n" +
                "	c.numerores res_num,\n" +
                "	c.complementores res_compl,\n" +
                "	c.bairrores res_bairro,\n" +
                "	c.cepres res_cep,\n" +
                "	res_cid.codoficial res_id_municipio,\n" +
                "	res_uf.codoficial res_id_estado,\n" +
                "	c.enderfat cob_end,\n" +
                "	c.numerofat cob_num,\n" +
                "	c.complementofat cob_compl,\n" +
                "	c.bairrofat cob_bairro,\n" +
                "	c.cepfat cob_cep,\n" +
                "	cob_cid.codoficial cob_id_municipio,\n" +
                "	cob_uf.codoficial cob_id_estado,\n" +
                "	c.enderent ent_end,\n" +
                "	c.numeroent ent_num,\n" +
                "	c.complementoent ent_compl,\n" +
                "	c.bairroent ent_bairro,\n" +
                "	c.cepent ent_cep,\n" +
                "	ent_cid.codoficial ent_id_municipio,\n" +
                "	ent_uf.codoficial ent_id_estado,\n" +
                "	c.cpfcnpj cnpj,\n" +
                "	c.foneres fone1,\n" +
                "	c.codempresa id_conveniado_com_cliente,\n" +
                "	case c.limite1 when 0 then c.limite2 else c.limite1 end as limite,\n" +
                "	c.rgie,\n" +
                "	c.dtinclusao datacadastro,\n" +
                "	c.dtnascto dataNascimento,\n" +
                "	case c.codstatus when 1 then false when 2 then true else true end bloqueado,\n" +
                "	c.fonefat fone2,\n" +
                "	c.faxfat fax,\n" +
                "	c.celular,\n" +
                "	c.observacao,\n" +
                "	c.email,\n" +
                "	case c.sexo when 'F' then 0 else 1 end sexo,\n" +
                "	(select razaosocial  from cliente where c.codempresa = codcliente) empresa,\n" +
                "	c.respcargo1 cargo,\n" +
                "	c.salario,\n" +
                "	0 as estadoCivil,\n" +
                "	nomeconj conjuge	\n" +
                "from\n" +
                "	cliente c\n" +
                "	left join cidade cob_cid on cob_cid.codcidade = c.codcidadefat\n" +
                "	left join estado cob_uf on cob_uf.uf = c.uffat\n" +
                "	left join cidade ent_cid on ent_cid.codcidade = c.codcidadeent\n" +
                "	left join estado ent_uf on ent_uf.uf = c.ufent\n" +
                "	left join cidade res_cid on res_cid.codcidade = c.codcidaderes\n" +
                "	left join estado res_uf on res_uf.uf = c.ufres\n" +
                "order by\n" +
                "	c.codcliente"
            );
            Linha=1;
            try{
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("id");
                    id_situacaocadastro = rst.getInt("id_situacaocadastro");
                    dataResidencia = "1990/01/01";

                    id_tipoinscricao = rst.getInt("id_tipoinscricao");

                    if ((rst.getString("nome") != null) &&
                            (!rst.getString("nome").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                    } else {
                        nome = "SEM NOME VR "+id;
                    }

                    if ((rst.getString("res_end") != null) &&
                            (!rst.getString("res_end").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("res_end").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("cnpj") != null) &&
                            (!rst.getString("cnpj").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj").trim()));
                    } else {
                        cnpj = id;
                    }

                    if ((rst.getString("res_bairro") != null) &&
                            (!rst.getString("res_bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("res_bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("fone1") != null) &&
                            (!rst.getString("fone1").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("fone1").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("res_cep") != null) &&
                            (!rst.getString("res_cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("res_cep").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("res_id_municipio") != null) &&
                            (!rst.getString("res_id_municipio").trim().isEmpty())) {
                        id_municipio = rst.getInt("res_id_municipio");
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("res_id_estado") != null) &&
                            (!rst.getString("res_id_estado").trim().isEmpty())) {
                        id_estado = rst.getInt("res_id_estado");
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if ((rst.getString("res_num") != null) &&
                            (!rst.getString("res_num").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("res_num").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("res_compl") != null) &&
                            (!rst.getString("res_compl").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("res_compl").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("limite") != null) &&
                            (!rst.getString("limite").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("limite").trim());
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("rgie") != null) &&
                            (!rst.getString("rgie").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("rgie").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("datacadastro") != null) &&
                            (!rst.getString("datacadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("datacadastro").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }
                    //dataCadastro = dataCadastro+"/"+rst.getString("clidtcad").substring(3, 5).trim();
                    //dataCadastro = dataCadastro+"/"+rst.getString("clidtcad").substring(0, 2).trim();

                    if ((rst.getString("dataNascimento") != null) &&
                            (!rst.getString("dataNascimento").trim().isEmpty())) {
                        dataNascimento = rst.getString("dataNascimento").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }

                    if ((rst.getString("bloqueado") != null) &&
                            (!rst.getString("bloqueado").trim().isEmpty())) {
                        bloqueado = rst.getBoolean("bloqueado");
                    } else {
                        bloqueado = false;
                    }       
                    
                    nomePai = "";
                    nomeMae = "";                    

                    if ((rst.getString("fone2") != null) &&
                            (!rst.getString("fone2").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("fone2").trim());
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("fax") != null) &&
                            (!rst.getString("fax").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("fax").trim());
                    } else {
                        fax = "";
                    }

                    if ((rst.getString("observacao") != null) &&
                            (!rst.getString("observacao").trim().isEmpty())) {
                        observacao = util.acertarTexto(rst.getString("observacao").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    if ((rst.getString("email") != null) &&
                            (!rst.getString("email").trim().isEmpty()) &&
                            (rst.getString("email").contains("@"))) {
                        email = util.acertarTexto(rst.getString("email").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    if ((rst.getString("sexo") != null) &&
                            (!rst.getString("sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("sexo").trim())) {
                            id_sexo = 0;
                        } else {
                            id_sexo = 1;
                        }
                    } else {
                        id_sexo = 1;
                    }

                    if ((rst.getString("empresa") != null) &&
                            (!rst.getString("empresa").trim().isEmpty())) {
                        empresa = util.acertarTexto(rst.getString("empresa").trim().replace("'", ""));
                    } else {
                        empresa = "";
                    }

                    
                    telEmpresa = "";

                    if ((rst.getString("cargo") != null) &&
                            (!rst.getString("cargo").trim().isEmpty())) {
                        cargo = util.acertarTexto(rst.getString("cargo").replace("'", "").trim());
                    } else {
                        cargo = "";
                    }

                    
                    enderecoEmpresa = "";
                    

                    if ((rst.getString("salario") != null) &&
                            (!rst.getString("salario").trim().isEmpty())) {
                        salario = Double.parseDouble(rst.getString("salario").replace(".", "").replace(",", "."));
                    } else {
                        salario = 0;
                    }

                    
                    estadoCivil = 0;
                    

                    if ((rst.getString("conjuge") != null) &&
                            (!rst.getString("conjuge").trim().isEmpty())) {
                        conjuge = util.acertarTexto(rst.getString("conjuge").trim().replace("'", ""));
                    } else {
                        conjuge = "";
                    }

                    
                    orgaoExp = "";
                    
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

                    if (inscricaoestadual.length() > 18) {
                        inscricaoestadual = inscricaoestadual.substring(0, 18);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    oClientePreferencial.setId(id);
                    oClientePreferencial.setNome(nome);
                    oClientePreferencial.setEndereco(endereco);
                    oClientePreferencial.setBairro(bairro);
                    oClientePreferencial.setId_estado(id_estado);
                    oClientePreferencial.setId_municipio(id_municipio);
                    oClientePreferencial.setCep(cep);
                    oClientePreferencial.setTelefone(telefone1);
                    oClientePreferencial.setInscricaoestadual(inscricaoestadual);
                    oClientePreferencial.setCnpj(cnpj);
                    oClientePreferencial.setSexo(id_sexo);
                    oClientePreferencial.setDataresidencia(dataResidencia);
                    oClientePreferencial.setDatacadastro(dataCadastro);
                    oClientePreferencial.setEmail(email);
                    oClientePreferencial.setValorlimite(limite);
                    oClientePreferencial.setCodigoanterior(id);
                    oClientePreferencial.setFax(fax);
                    oClientePreferencial.setBloqueado(bloqueado);
                    oClientePreferencial.setId_situacaocadastro(id_situacaocadastro);
                    oClientePreferencial.setTelefone2(telefone2);
                    oClientePreferencial.setObservacao(observacao);
                    oClientePreferencial.setDatanascimento(dataNascimento);
                    oClientePreferencial.setNomepai(nomePai);
                    oClientePreferencial.setNomemae(nomeMae);
                    oClientePreferencial.setEmpresa(Utils.acertarTexto(empresa,35));
                    oClientePreferencial.setTelefoneempresa(telEmpresa);
                    oClientePreferencial.setNumero(numero);
                    oClientePreferencial.setCargo(cargo);
                    oClientePreferencial.setEnderecoempresa(enderecoEmpresa);
                    oClientePreferencial.setId_tipoinscricao(id_tipoinscricao);
                    oClientePreferencial.setSalario(salario);
                    oClientePreferencial.setId_tipoestadocivil(estadoCivil);
                    oClientePreferencial.setNomeconjuge(conjuge);
                    oClientePreferencial.setOrgaoemissor(orgaoExp);
                    vClientePreferencial.add(oClientePreferencial);
                }
            stm.close();
            } catch (Exception ex) {
                throw ex;
                //if (Linha > 0) {
                //    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                //} else {
                //    throw ex;
                //}
            }
            return vClientePreferencial;
        } catch(SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    /*private List<EmpresaVO> carregarEmpresaConvenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<EmpresaVO> vEmpresa = new ArrayList<>();
        int idEmpresa, idMunicipio, idSituacaoCadastro, idTipoInscricao,
            idEstado;
        String razaoSocial, endereco, bairro, telefone, inscricaoEstadual,
               dataInicio, dataTermino, observacao, numero, complemento,
               email, site;
        long cep, cnpj;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoPostgres.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select c.codcliente, c.nome, c.razaosocial, c.enderfat, c.bairrofat, ");
            sql.append("       c.cepfat, c.codcidadefat, c.uffat, c.enderent, c.bairroent, ");
            sql.append("       c.cepent, c.codcidadeent, c.ufent, c.contato, c.site, c.email, ");
            sql.append("       c.tppessoa, c.cpfcnpj, c.rgie, c.observacao, c.enderres, ");
            sql.append("       c.bairrores, c.cepres, c.codcidaderes, c.ufres, c.numerofat, ");
            sql.append("       c.numeroent, c.numerores, c.complementores, cid.nome cidade, ");
            sql.append("       cid.codoficial codCidade, cid.uf ");
            sql.append("  from cliente c ");
            sql.append("  left join cidade cid on cid.codcidade = codcidadefat ");
            sql.append(" where c.codcliente in (select codempresa from cliente) ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idEmpresa = rst.getInt("codcliente");
                
                if ((rst.getString("razaosocial") != null) &&
                        (!rst.getString("razaosocial").trim().isEmpty())) {
                    razaoSocial = Utils.acertarTexto(rst.getString("razaosocial").trim().replace("'", ""));
                } else {
                    if ((rst.getString("nome") != null) &&
                            (!rst.getString("nome").trim().isEmpty())) {
                        razaoSocial = Utils.acertarTexto(rst.getString("nome").trim().replace("'", ""));
                    } else {
                        razaoSocial = "SEM RAZAO SOCIAL";
                    }
                }
                
                if ((rst.getString("enderfat") != null) &&
                        (!rst.getString("enderfat").trim().isEmpty())) {
                    endereco = Utils.acertarTexto(rst.getString("enderfat").trim().replace("'", ""));
                } else {
                    endereco = "";
                }
                
                if ((rst.getString("bairrofat") != null) &&
                        (!rst.getString("bairrofat").trim().isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("bairrofat").trim().replace("'", ""));
                } else {
                    bairro = "";
                }
                
                if ((rst.getString("numerofat") != null) &&
                        (!rst.getString("numerofat").trim().isEmpty())) {
                    numero = Utils.acertarTexto(rst.getString("numerofat").trim().replace("'", ""));
                } else {
                    numero = "";
                }
                
                if ((rst.getString("cepfat") != null) &&
                        (!rst.getString("cepfat").trim().isEmpty())) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("cepfat").trim()));
                } else {
                    cep = 0;
                }
                 
                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").trim().isEmpty())) {
                    if ((rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        idMunicipio = util.retornarMunicipioIBGEDescricao(rst.getString("cidade").trim().replace("'", ""),
                                rst.getString("uf").trim().replace("'", ""));

                        if (idMunicipio == 0) {
                            idMunicipio = Global.idMunicipio;
                        }
                    } else {
                        idMunicipio = Global.idMunicipio;
                    }
                } else {
                    idMunicipio = Global.idMunicipio;
                }
                
                if ((rst.getString("uf") != null)
                        && (!rst.getString("uf").trim().isEmpty())) {
                    idEstado = Utils.retornarEstadoDescricao(
                            rst.getString("uf").trim().replace("'", ""));

                    if (idEstado == 0) {
                        idEstado = Global.idEstado;
                    } else {
                        idEstado = Global.idEstado;
                    }
                } else {
                    idEstado = Global.idEstado;
                }
                
                if ((rst.getString("cpfcnpj") != null) &&
                        (!rst.getString("cpfcnpj").trim().isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cpfcnpj").trim()));
                } else {
                    cnpj = -1;
                }
                
                if ((rst.getString("rgie") != null) &&
                        (!rst.getString("rgie").trim().isEmpty())) {
                    inscricaoEstadual = Utils.acertarTexto(rst.getString("rgie").trim());
                    inscricaoEstadual = inscricaoEstadual.replace(".", "");
                    inscricaoEstadual = inscricaoEstadual.replace("/", "");
                    inscricaoEstadual = inscricaoEstadual.replace("-", "");
                } else {
                    inscricaoEstadual = "ISENTO";
                }
                
                if ("J".equals(rst.getString("tppessoa").trim())) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                if ((rst.getString("observacao") != null) &&
                        (!rst.getString("observacao").trim().isEmpty())) {
                    observacao = Utils.acertarTexto(rst.getString("observacao").trim().replace("'", ""));
                } else {
                    observacao = "";
                }
                
                
                
                
                
                
            }
            
            stm.close();
            return null;
        } catch(Exception ex) {
            throw ex;
        }
    }*/    
    
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

    private int getAliquotaICMS(int cst, double aliquota, double reduzido) {
        
        aliquota = Utils.arredondar(aliquota, 1);
        reduzido = Utils.arredondar(reduzido, 1);
        
        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 17) {
                    return 8;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else {
                    return 8;
                }
            }    
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        return 9;
                    } else if (reduzido == 61.1) {
                        return 4;
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        return 5;
                    } else {
                        return 1;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default: return 8;
        }
    }

    public void importarUnidadeDeBalanca() throws Exception {        

        ProgressBar.setStatus("Carregando dados...Tipo Embalagem da balança...");
        
        List<ProdutoVO> vProdutos = carregarEmbalagemDaBalanca();
        
        new ProdutoDAO().alterarTipoEmbalagem(vProdutos);
        
        
    }

    private List<ProdutoVO> carregarEmbalagemDaBalanca() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "  p.codproduto,\n" +
                    "  un.sigla,\n" +
                    "  p.pesado,\n" +
                    "  p.pesounid\n" +        
                    "from \n" +
                    "  produto p\n" +
                    "  join embalagem emb on p.codembalvda = emb.codembal\n" +
                    "  join unidade un on emb.codunidade = un.codunidade\n" +
                    "order by p.codproduto"
            )) {
                while (rst.next()) {
                    ProdutoVO produto = new ProdutoVO();
                    produto.setId(rst.getInt("codproduto"));
                    
                    boolean pesavel = Utils.acertarTexto(rst.getString("pesado")).equals("S");
                    boolean unitario = Utils.acertarTexto(rst.getString("pesounid")).equals("U");
                    
                    //produto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("sigla")));
                    if (pesavel) {                        
                        if (unitario) {
                            produto.setIdTipoEmbalagem(0);
                            produto.setPesavel(true);
                        } else {
                            produto.setIdTipoEmbalagem(4);
                            produto.setPesavel(false);
                        }
                    } else {
                        produto.setIdTipoEmbalagem(0);
                        produto.setPesavel(false);
                    }
                    result.add(produto);
                }
            }
        }
        return result;
    }

    public void ajustarDescricao(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");
        new ProdutoDAO().acertarDescricao(new ArrayList<>(carregarProduto(idLojaVR, idLojaCliente).values()));
    }
}
