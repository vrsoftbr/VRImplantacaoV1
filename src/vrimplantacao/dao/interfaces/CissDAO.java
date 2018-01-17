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
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
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
import vrimplantacao2.parametro.Parametros;

public class CissDAO extends AbstractIntefaceDao {
    
    /*
        Usuário padrão: dba;
        Senha padrão  : overhead;
    */
    
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
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select idempresa, razaosocial from dba.empresa order by idempresa"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("idempresa"), rst.getString("idempresa") + " - " + rst.getString("razaosocial")));
                }
            }
        } 
        
        return result;
    }
    
    

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        pg.idproduto,\n" +
                    "        p.DESCRCOMPRODUTO,\n" +
                    "        count(*)\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.PRODUTO p on\n" +
                    "                pg.idproduto = p.idproduto\n" +
                    "where\n" +
                    "        p.tipobaixamestre = 'I'\n" +
                    "group by pg.idproduto, p.descrcomproduto\n" +
                    "having count(*) > 1\n" +
                    "order by\n" +
                    "        pg.idproduto"
            )) {
                while(rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("idproduto"));
                    vo.setDescricao(rst.getString("DESCRCOMPRODUTO"));
                    vo.setCodigoant(rst.getInt("idproduto"));
                    vo.setIdLong(rst.getLong("idproduto"));
                    vo.setId_situacaocadastro(1);
                    result.add(vo);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> mercadologicos = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT distinct\n" +
                "        s.idsecao id_merc1,\n" +
                "        s.descrsecao merc1,\n" +
                "        g.idgrupo id_merc2,\n" +
                "        g.descrgrupo merc2,\n" +
                "        sub.idsubgrupo id_merc3,\n" +
                "        sub.descrsubgrupo merc3\n" +
                "from\n" +
                "        DBA.secao  as s\n" +
                "        left join DBA.grupo  as g on g.idsecao = s.idsecao\n" +
                "        left join DBA.subgrupo  as sub on sub.idgrupo = g.idgrupo\n" +
                "order by s.idsecao,g.idgrupo,sub.idsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    
                    vo.setMercadologico1(0);
                    vo.setMercadologico2(0);
                    vo.setMercadologico3(0);
                    vo.setMercadologico4(0);
                    vo.setMercadologico5(0);
                    
                    vo.setMercadologico1(rst.getInt("id_merc1"));
                    if (nivel > 1) {
                        vo.setMercadologico2(rst.getInt("id_merc2"));
                    }
                    if (nivel > 2) {
                        vo.setMercadologico3(rst.getInt("id_merc3"));
                    }   
                    vo.setNivel(nivel);
                    switch (nivel) {
                        case 1: vo.setDescricao(rst.getString("merc1")); break;
                        case 2: vo.setDescricao(rst.getString("merc2")); break;
                        case 3: vo.setDescricao(rst.getString("merc3")); break;
                    }
                    mercadologicos.add(vo);
                }
            }
        }
        return mercadologicos;
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        Map<Long, FamiliaProdutoVO> familia = new FamiliaProdutoDAO().carregarAnteriores();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        pg.idsubproduto id,\n" +
                    "        1 as qtdEmbalagem,\n" +
                    "        p.DESCRCOMPRODUTO as descricaocompleta,\n" +
                    "        pg.DESCRRESPRODUTO as descricaoreduzida,\n" +
                    "        p.DESCRCOMPRODUTO as descricaogondola,\n" +
                    "        pg.DTCADASTRO as dataCadastro,\n" +
                    "        p.idsecao as mercadologico1,\n" +
                    "        p.idgrupo as mercadologico2,\n" +
                    "        p.idsubgrupo as mercadologico3,\n" +
                    "        case pg.flaginativo when 'F' then 1 else 0 end as id_Situacaocadastro,\n" +
                    "        pg.ncm,\n" +
                    "        --case when pg.idproduto != pg.idsubproduto then pg.idproduto else -1 end as id_familiaproduto,\n" +
                    "        pg.idproduto as id_familiaproduto,\n" +
                    "        preco.permargemvarejo margem,\n" +
                    "        1 as qtdEmbalagem,\n" +
                    "        embalagemsaida as id_tipoEmbalagem,\n" +
                    "        p.diasvalidade validade,\n" +
                    "        p.IDCSTPISCOFINSSAIDA as idTipoPisCofinsDebito,\n" +
                    "        p.IDCSTPISCOFINSENTRADA as idTipoPisCofinsCredito,\n" +
                    "        nat.idcodnatureza as tipoNaturezaReceita,\n" +
                    "        preco.valprecovarejo as precovenda,\n" +
                    "        trib.uf as idEstado,\n" +
                    "        trib.idsittribsai as idAliquotaDebito,\n" +
                    "        trib.idsittribent as idAliquotaCredito,\n" +
                    "        trib.pericment as aliqIcmsEnt,\n" +
                    "        trib.pericmsai as aliqIcmsSai,\n" +
                    "        trib.perredtribsai as percReducao,\n" +
                    "        trib.permargemsubsti as percSubst,\n" +
                    "        pg.idcodbarprod codigobarras,\n" +
                    "        est.qtdatualestoque estoque,\n" +
                    "        custo.custonotafiscal custosemimposto,\n" +
                    "        custo.custogerencial custocomimposto,\n" +
                    "        pg.pesobruto,\n" +
                    "        pg.pesoliquido,\n" +
                    "        coalesce(pc.qtdestminimo,0) estmin,\n" +
                    "        coalesce(pc.qtdestmaximo,0) estmax,\n" +
                    "        trim(upper(coalesce(p.tipobaixamestre,'C'))) tipobaixamestre\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.produto p on p.idproduto = pg.idproduto\n" +
                    "        join dba.empresa emp on emp.idempresa = " + idLojaCliente + "\n" +
                    "        left join DBA.POLITICA_PRECO_PRODUTO preco on\n" +
                    "                pg.idproduto = preco.idproduto and\n" +
                    "                pg.idsubproduto = preco.idsubproduto and\n" +
                    "                preco.idempresa = emp.idempresa\n" +
                    "        left join DBA.PRODUTO_TRIBUTACAO_VW trib on\n" +
                    "                pg.idproduto = trib.idproduto and\n" +
                    "                pg.idsubproduto = trib.idsubproduto and\n" +
                    "                trib.uf = emp.UF and trib.uforigem = emp.uf\n" +
                    "        left join dba.estoque_saldo_atual est on\n" +
                    "                pg.idproduto = est.idproduto and\n" +
                    "                pg.idsubproduto = est.idsubproduto and\n" +
                    "                emp.idempresa = est.idempresa and\n" +
                    "                est.idlocalestoque = 1\n" +
                    "        left join dba.produto_grade_custo_view custo on\n" +
                    "                pg.idproduto = custo.idproduto and\n" +
                    "                pg.idsubproduto = custo.idsubproduto and\n" +
                    "                emp.idempresa = custo.idempresa\n" +
                    "        left join dba.PRODUTO_COMPRAS pc on\n" +
                    "                pg.idproduto = pc.idproduto and\n" +
                    "                pg.idsubproduto = pc.idsubproduto and\n" +
                    "                emp.idempresa = pc.idempresa\n" +
                    "        left join dba.piscofins_codigo_natureza_receita nat on\n" +
                    "                nat.idnaturezapiscofins = p.IDNATUREZAPISCOFINS\n"
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
                    String baixa = rst.getString("tipobaixamestre");
                    if ("M".equals(baixa) || "I".equals(baixa)) {
                        oProduto.setDescricaoCompleta(rst.getString("descricaoreduzida"));
                        oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        oProduto.setDescricaoGondola(rst.getString("descricaoreduzida"));
                    } else {
                        oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    }
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
                    
                    oProduto.setIdFamiliaProduto(rst.getString("id_familiaproduto") != null ? rst.getInt("id_familiaproduto") : -1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    int codigoBarra = rst.getLong("codigobarras") <= Integer.MAX_VALUE ? rst.getInt("codigobarras") : -1;
                    
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoBarra);
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
                        oProduto.eBalanca = true;
                        
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        
                        oProduto.eBalanca = false;
                        
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
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("idTipoPisCofinsDebito")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("idTipoPisCofinsCredito")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(
                        oProduto.getIdTipoPisCofins()
                        , rst.getString("tipoNaturezaReceita")));
                    
                    
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));                    
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
                    oComplemento.setEstoque(rst.getDouble("estoque"));

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(rst.getString("idEstado")));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(getAliquotaICMS(rst.getInt("idAliquotaDebito"), rst.getDouble("aliqIcmsSai"), rst.getDouble("percReducao")));
                    oAliquota.setIdAliquotaCredito(getAliquotaICMS(rst.getInt("idAliquotaDebito"), rst.getDouble("aliqIcmsSai"), rst.getDouble("percReducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(getAliquotaICMS(rst.getInt("idAliquotaDebito"), rst.getDouble("aliqIcmsSai"), rst.getDouble("percReducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(getAliquotaICMS(rst.getInt("idAliquotaDebito"), rst.getDouble("aliqIcmsSai"), rst.getDouble("percReducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(getAliquotaICMS(rst.getInt("idAliquotaDebito"), rst.getDouble("aliqIcmsSai"), rst.getDouble("percReducao")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setEstoque(oComplemento.getEstoque());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(rst.getLong("codigoBarras"));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("idTipoPisCofinsDebito"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("idTipoPisCofinsCredito"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("tipoNaturezaReceita"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("idAliquotaDebito"));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                    
                    
                    //Ajusta a descrição
                    /*if (familia.containsKey((long) oProduto.getIdFamiliaProduto())) {
                        oProduto.setDescricaoCompleta(oProduto.getDescricaoReduzida());
                        oProduto.setDescricaoGondola(oProduto.getDescricaoReduzida());
                    }*/
                }
                return vProduto;
            }
        }
    }

    @Override
    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "        pg.idsubproduto id,\n" +
                    "        preco.valprecovarejo as precovenda\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.empresa emp on emp.idempresa = " + idLojaCliente + "\n" +
                    "        left join DBA.POLITICA_PRECO_PRODUTO preco on\n" +
                    "                pg.idproduto = preco.idproduto and\n" +
                    "                pg.idsubproduto = preco.idsubproduto and\n" +
                    "                preco.idempresa = emp.idempresa\n" +
                    "where preco.valprecovarejo != 0"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();                    
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();                    
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvCodigoAnterior().add(oAnterior);
                    
                    oProduto.setId(rst.getInt("id"));                 
                    oAnterior.setCodigoAnteriorStr(rst.getString("id"));
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));                    
                    oAnterior.setPrecovenda(rst.getDouble("precovenda"));                    
                    
                    result.put((int) oProduto.getId(), oProduto);
                }
            }        
        }
        return result;
    }

    @Override
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        pg.idsubproduto id,\n" +                   
                    "        custo.custonotafiscal custosemimposto,\n" +
                    "        custo.custogerencial custocomimposto\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.produto p on p.idproduto = pg.idproduto\n" +
                    "        join dba.empresa emp on emp.idempresa = " + idLojaCliente + "\n" +
                    "        left join dba.produto_grade_custo_view custo on\n" +
                    "                pg.idproduto = custo.idproduto and\n" +
                    "                pg.idsubproduto = custo.idsubproduto and\n" +
                    "                emp.idempresa = custo.idempresa\n" + 
                    "where custo.custonotafiscal != 0 or custo.custoultimacompra != 0"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();                    
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();                    
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvCodigoAnterior().add(oAnterior);
                    
                    oProduto.setId(rst.getInt("id"));                   
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));                    
                    oAnterior.setCustocomimposto(rst.getDouble("custocomnota"));
                    oAnterior.setCustosemimposto(rst.getDouble("custosemnota"));
                    
                    result.put((int) oProduto.getId(), oProduto);
                }
            }        
        }
        return result;
    }

    @Override
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        pg.idsubproduto id,\n" +
                    "        est.qtdatualestoque estoque,\n" +
                    "        coalesce(pc.qtdestminimo,0) estmin,\n" +
                    "        coalesce(pc.qtdestmaximo,0) estmax\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.produto p on p.idproduto = pg.idproduto\n" +
                    "        join dba.empresa emp on emp.idempresa =  " + idLojaCliente + "\n" +
                    "        left join dba.estoque_saldo_atual est on\n" +
                    "                pg.idproduto = est.idproduto and\n" +
                    "                pg.idsubproduto = est.idsubproduto and\n" +
                    "                emp.idempresa = est.idempresa and\n" +
                    "                est.idlocalestoque = 1\n" +
                    "        left join dba.PRODUTO_COMPRAS pc on\n" +
                    "        		pg.idproduto = pc.idproduto and\n" +
                    "        		pg.idsubproduto = pc.idsubproduto and\n" +
                    "        		emp.idempresa = pc.idempresa\n" +
                    //"where est.qtdatualestoque != 0 or coalesce(pc.qtdestminimo,0) != 0 or coalesce(pc.qtdestmaximo,0) != 0 \n" +
                    "order by id"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();                    
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();                    
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvCodigoAnterior().add(oAnterior);
                    
                    oProduto.setId(rst.getInt("id"));
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estmax"));                    
                    oComplemento.setEstoqueMinimo(rst.getDouble("estmin"));   
                    oAnterior.setEstoque(rst.getDouble("estoque"));
                    
                    result.put((int) oProduto.getId(), oProduto);
                }
            }        
        }
        return result;
    }

    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        pg.idsubproduto id,\n" +
                    "        pg.idcodbarprod codigobarras,\n" +
                    "        pg.qtdtotalembalagem qtdEmbalagem,\n" +
                    "        p.embalagemsaida idEmbalagem\n" +
                    "from\n" +
                    "        dba.produto_grade pg\n" +
                    "        join dba.produto p on p.idproduto = pg.idproduto"
            )) {
                int cont = 1;
                while (rst.next()) {
                    
                    
                    long codigobarras = rst.getLong("codigobarras");
                                       
                    ProdutoVO oProduto = new ProdutoVO();                    
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oAnterior);

                    oProduto.setId(rst.getInt("id"));
                    oAnterior.setCodigoAnteriorStr(rst.getString("id"));
                    oAutomacao.setCodigoBarras(codigobarras);
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("idEmbalagem")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    result.put(codigobarras, oProduto);      
                    ProgressBar.setStatus("Carregando o EAN...." + cont);
                    cont++;
                }
            }        
        }
        return result;
    }    

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "	f.idclifor id,\n" +
                "	f.NOME razao,\n" +
                "	f.NOMEFANTASIA fantasia,\n" +
                "	f.CNPJCPF cnpj,\n" +
                "	f.INSCRESTADUAL inscricaoestadual,\n" +
                "	f.FONE1,\n" +
                "	f.ENDERECO,\n" +
                "	f.BAIRRO,\n" +
                "	f.NUMERO,\n" +
                "	f.COMPLEMENTO,\n" +
                "	f.IDCEP cep,\n" +
                "	f.OBSGERAL observacao,\n" +
                "	c.codigoibge id_municipio,\n" +
                "	c.descrcidade,\n" +
                "	c.uf id_estado,\n" +
                "	f.dtcadastro datacadastro,\n" +
                "	case f.idsituacao when 4 then 0 else 1 end id_situacaoCadastro,\n" +
                "	f.FONE2,\n" +
                "	f.FONEFAX fax,\n" +
                "	f.EMAIL, f.tipofisicajuridica \n" +
                "from \n" +
                "	dba.cliente_fornecedor f\n" +
                "	left join dba.cidades_ibge c on f.idcidade = c.idcidade\n" +
                "where\n" +
                "	f.TIPOCADASTRO in ('A','F')\n" +
                "order by f.idclifor"
            )) {
                while (rst.next()) {
                    
                    String razaosocial, nomefantasia, endereco, bairro, inscricaoestadual,
                    telefone1, telefone2, numero, complemento, obs, fax, email;
                    long cnpj, cep;
                    Date datacadastro;
                    int id_tipoinscricao, id_municipio = 0, id_estado, IdFornecedor;
                    
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
                        cnpj = -1;
                    }

                    if ((rst.getString("inscricaoestadual") != null)
                            && (!rst.getString("inscricaoestadual").isEmpty())) {
                        inscricaoestadual = Utils.formataNumero(rst.getString("inscricaoestadual").replace("'", ""));
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ("F".equals(rst.getString("tipofisicajuridica").trim())) {
                        id_tipoinscricao = 1;
                    } else {
                        id_tipoinscricao = 0;
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
                        cep = Parametros.get().getCepPadrao();
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
                        id_municipio = Parametros.get().getMunicipioPadrao().getId();
                    }

                    if ((rst.getString("id_estado") != null)
                            && (!rst.getString("id_estado").isEmpty())) {
                        id_estado = Utils.getEstadoPelaSigla(rst.getString("id_estado"));
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId();
                    }

                    if ((rst.getString("datacadastro") != null)
                            && (!rst.getString("datacadastro").isEmpty())) {
                        datacadastro = rst.getDate("datacadastro");                    
                    } else {
                        datacadastro = new Date(new GregorianCalendar().getTimeInMillis());
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

                    oFornecedor.setCodigoanterior(IdFornecedor);
                    oFornecedor.setRazaosocial(razaosocial);
                    oFornecedor.setNomefantasia(nomefantasia);
                    oFornecedor.setEndereco(endereco);
                    oFornecedor.setBairro(bairro);
                    oFornecedor.setId_municipio(id_municipio);
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setCep(cep);
                    oFornecedor.setId_estado(id_estado);
                    oFornecedor.setTelefone(telefone1);
                    oFornecedor.setId_tipoinscricao(id_tipoinscricao);
                    oFornecedor.setInscricaoestadual(inscricaoestadual);
                    oFornecedor.setCnpj(cnpj);
                    oFornecedor.setNumero(numero);
                    oFornecedor.setComplemento(complemento);
                    oFornecedor.setObservacao(obs);
                    oFornecedor.setTelefone2(telefone2);
                    oFornecedor.setFax(fax);
                    oFornecedor.setEmail(email);
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
        String codigoExterno = "";
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" 
                    +"pf.idclifor id_fornecedor,\n"
                    + "pf.idsubproduto id_produto,\n"
                    + "pf.CODIGOINTERNOFORN codigoexterno,\n"
                    + "f.ufclifor uf,\n"
                    + "f.CNPJCPF cnpj\n"
                    + "from\n"
                    + "dba.PRODUTO_FORNECEDOR pf\n"
                    + "join dba.CLIENTE_FORNECEDOR f on\n"
                    + "pf.idclifor = f.idclifor\n"
                    + "where\n"
                    + "f.tipocadastro = 'F'\n"
                    + "order by\n"
                    + "id_fornecedor,\n"
                    + "id_produto "
            )) {
                while (rst.next()) {
                    
                    if ((rst.getString("codigoexterno") != null) &&
                            (!rst.getString("codigoexterno").trim().isEmpty())) {
                        codigoExterno = Utils.acertarTexto(rst.getString("codigoexterno").trim().replace("'", ""));
                    } else {
                        codigoExterno = "";
                    }
                    
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_produtoStr(rst.getString("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf")));
                    vo.setCodigoexterno(codigoExterno);
                    vo.setCnpFornecedor(rst.getLong("cnpj"));
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
        List<ClientePreferencialVO> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        c.idclifor id,\n" +
                    "        c.NOME razao,\n" +
                    "        c.NOMEFANTASIA fantasia,\n" +
                    "        c.CNPJCPF cnpj,\n" +
                    "        c.INSCRESTADUAL inscricaoestadual,\n" +
                    "        c.FONE1,\n" +
                    "        c.ENDERECO,\n" +
                    "        c.BAIRRO,\n" +
                    "        c.NUMERO,\n" +
                    "        c.COMPLEMENTO,\n" +
                    "        c.IDCEP cep,\n" +
                    "        c.OBSGERAL observacao,\n" +
                    "        cid.codigoibge id_municipio,\n" +
                    "        cid.descrcidade,\n" +
                    "        cid.uf id_estado,\n" +
                    "        c.dtcadastro datacadastro,\n" +
                    "        c.fonecelular,\n" +
                    "        c.vallimitecredito,\n" +
                    "        c.vallimiteconvenio,\n" +
                    "        coalesce(conv.diavencimento,0) diavencimento,\n" +
                    "        c.tipofisicajuridica,\n" +
                    "        c.tipofisicajuridica,\n" +
                    "        case c.idsituacao when 4 then 0 else 1 end id_situacaoCadastro,\n" +
                    "        c.FONE2,\n" +
                    "        c.FONEFAX fax,\n" +
                    "        c.EMAIL\n" +
                    "from\n" +
                    "        dba.cliente_fornecedor c\n" +
                    "        left join dba.cidades_ibge cid on cid.idcidade = c.idcidade\n" +
                    "        left join dba.cliente_convenio conv on c.idconvenio = conv.idconvenio\n" +
                    "where\n" +
                    "        c.TIPOCADASTRO in ('A','C')"
                )) {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    
                    String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                        dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
                        telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                        conjuge = "", orgaoExp = "", celular = "";
                    int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha=0,
                     estadoCivil = 0;
                    long cnpj, cep;
                    double limite, salario, limiteCredito, limiteConvenio;
                    boolean bloqueado;
                    
                    id = rst.getInt("id");
                    id_situacaocadastro = rst.getInt("id_situacaocadastro");
                    dataResidencia = "1990/01/01";

                    if ("F".equals(rst.getString("tipofisicajuridica").trim())) {
                        id_tipoinscricao = 1;
                    } else {
                        id_tipoinscricao = 0;
                    }

                    if ((rst.getString("razao") != null) &&
                            (!rst.getString("razao").trim().isEmpty())) {
                        nome = Utils.acertarTexto(rst.getString("razao").replace("'", "").trim());                     
                    } else {
                        nome = "SEM RAZAO VR "+id;
                    }

                    if ((rst.getString("ENDERECO") != null) &&
                            (!rst.getString("ENDERECO").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("cnpj") != null) &&
                            (!rst.getString("cnpj").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj").trim()));
                    } else {
                        cnpj = -1;
                    }

                    if ((rst.getString("BAIRRO") != null) &&
                            (!rst.getString("BAIRRO").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("BAIRRO").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("FONE1") != null) &&
                            (!rst.getString("FONE1").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("FONE1").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("cep") != null) &&
                            (!rst.getString("cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("id_municipio") != null) &&
                            (!rst.getString("id_municipio").trim().isEmpty())) {
                        id_municipio = rst.getInt("id_municipio");
                    } else {
                        id_municipio = Parametros.get().getMunicipioPadrao().getId();
                    }

                    if ((rst.getString("id_estado") != null)
                            && (!rst.getString("id_estado").isEmpty())) {
                        id_estado = Utils.getEstadoPelaSigla(rst.getString("id_estado"));
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId();
                    }

                    if ((rst.getString("NUMERO") != null) &&
                            (!rst.getString("NUMERO").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("NUMERO").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("COMPLEMENTO") != null) &&
                            (!rst.getString("COMPLEMENTO").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("COMPLEMENTO").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("vallimitecredito") != null) &&
                            (!rst.getString("vallimitecredito").trim().isEmpty())) {
                        limiteCredito = Double.parseDouble(rst.getString("vallimitecredito").trim());
                    } else {
                        limiteCredito = 0;
                    }

                    if ((rst.getString("vallimiteconvenio") != null) &&
                            (!rst.getString("vallimiteconvenio").trim().isEmpty())) {
                        limiteConvenio = Double.parseDouble(rst.getString("vallimiteconvenio").trim());
                    } else {
                        limiteConvenio = 0;
                    }
                    
                    limite = limiteCredito + limiteConvenio;
                    
                    if ((rst.getString("inscricaoestadual") != null) &&
                            (!rst.getString("inscricaoestadual").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("inscricaoestadual").trim());
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

                    //if ((rst.getString("dataNascimento") != null) &&
                    //        (!rst.getString("dataNascimento").trim().isEmpty())) {
                    //    dataNascimento = rst.getString("dataNascimento").substring(0, 10).trim().replace("-", "/");
                    //} else {
                    dataNascimento = null;
                    //}

                    /*if ((rst.getString("bloqueado") != null) &&
                            (!rst.getString("bloqueado").trim().isEmpty())) {
                        bloqueado = rst.getBoolean("bloqueado");
                    } else {*/
                    bloqueado = false;
                    //}       
                    
                    nomePai = "";
                    nomeMae = "";                    

                    if ((rst.getString("FONE2") != null) &&
                            (!rst.getString("FONE2").trim().isEmpty())) {
                        telefone2 = Utils.formataNumero(rst.getString("FONE2").trim());
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("fax") != null) &&
                            (!rst.getString("fax").trim().isEmpty())) {
                        fax = Utils.formataNumero(rst.getString("fax").trim());
                    } else {
                        fax = "";
                    }

                    if ((rst.getString("fonecelular") != null) &&
                            (!rst.getString("fonecelular").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("fonecelular").trim());
                    } else {
                        celular = "";
                    }
                    
                    if ((rst.getString("observacao") != null) &&
                            (!rst.getString("observacao").trim().isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("observacao").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    if ((rst.getString("email") != null) &&
                            (!rst.getString("email").trim().isEmpty()) &&
                            (rst.getString("email").contains("@"))) {
                        email = Utils.acertarTexto(rst.getString("email").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    /*if ((rst.getString("sexo") != null) &&
                            (!rst.getString("sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("sexo").trim())) {
                            id_sexo = 0;
                        } else {
                            id_sexo = 1;
                        }
                    } else {*/
                    id_sexo = 1;
                    //}

                    /*if ((rst.getString("empresa") != null) &&
                            (!rst.getString("empresa").trim().isEmpty())) {
                        empresa = Utils.acertarTexto(rst.getString("empresa").trim().replace("'", ""));
                    } else {*/
                    empresa = "";
                    //}

                    
                    telEmpresa = "";

                    /*if ((rst.getString("cargo") != null) &&
                            (!rst.getString("cargo").trim().isEmpty())) {
                        cargo = Utils.acertarTexto(rst.getString("cargo").replace("'", "").trim());
                    } else {*/
                    cargo = "";
                    //}

                    
                    enderecoEmpresa = "";
                    

                    /*if ((rst.getString("salario") != null) &&
                            (!rst.getString("salario").trim().isEmpty())) {
                        salario = Double.parseDouble(rst.getString("salario").replace(".", "").replace(",", "."));
                    } else {*/
                    salario = 0;
                    //}

                    
                    estadoCivil = 0;
                    

                    /*if ((rst.getString("conjuge") != null) &&
                            (!rst.getString("conjuge").trim().isEmpty())) {
                        conjuge = Utils.acertarTexto(rst.getString("conjuge").trim().replace("'", ""));
                    } else {*/
                        conjuge = "";
                    //}
                    
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
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("diavencimento"));
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
                    oClientePreferencial.setCelular(celular);
                    
                    result.add(oClientePreferencial);
                }
            }
        }
        return result;
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "        cr.idclifor,\n" +
                    "        cr.serienota,\n" +
                    "        cr.valtitulo,\n" +
                    "        cr.dtmovimento,\n" +
                    "        dtvencimento,\n" +
                    "        cr.obstitulo,\n" +
                    "        cr.idcaixa,\n" +
                    "        cr.numcupomfiscal\n" +
                    "from\n" +
                    "        DBA.CONTAS_RECEBER cr\n" +
                    "        inner join dba.cliente_fornecedor c on c.idclifor = cr.idclifor\n" +
                    "where\n" +
                    "        cr. idempresa = " + idLojaCliente + "\n" +
                    "        and c.TIPOCADASTRO in ('A','C')\n" +
                    "        and cr.FLAGBAIXADA = 'F'"
            )) {
                
                int idCliente, numeroCupom, ecf;
                double valor;
                String dataEmissao, dataVencimento, observacao;
                
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                    
                    idCliente = Integer.parseInt(rst.getString("idclifor"));
                    valor = Double.parseDouble(rst.getString("valtitulo"));
                    dataEmissao = rst.getString("dtmovimento").trim().replace("-", "/");
                    dataVencimento = rst.getString("dtvencimento").trim().replace("-", "/");
                    
                    if ((rst.getString("obstitulo") != null) &&
                            (!rst.getString("obstitulo").trim().isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("obstitulo").trim().replace("'", ""));
                    } else {
                        observacao = "";
                    }
                    
                    if ((rst.getString("numcupomfiscal") != null) &&
                            (!rst.getString("numcupomfiscal").trim().isEmpty())) {
                        numeroCupom = Integer.parseInt(Utils.formataNumero(rst.getString("numcupomfiscal")));
                    } else {
                        numeroCupom = 0;
                    }
                    
                    if ((rst.getString("idcaixa") != null) &&
                            (!rst.getString("idcaixa").trim().isEmpty())) {
                        ecf = Integer.parseInt(rst.getString("idcaixa"));
                    } else {
                        ecf = 0;
                    }
                    
                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataEmissao;
                    oReceberCreditoRotativo.numerocupom = numeroCupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = dataVencimento;
                    oReceberCreditoRotativo.valorjuros = 0;
                    
                    result.add(oReceberCreditoRotativo);
                    
                }
            }
        }
        return result;
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
                    if (reduzido == 61.1) {
                        return 8;
                    } else {
                        return 2;
                    }
                } else if (aliquota == 25) {
                    return 3;
                } else {
                    return 8;
                }
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
                    if (reduzido == 41.7) {
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
            default: return 8;
        }
    }

    public void importarEstoqueIntegrado(int idLojaVR, int idLojaCliente) throws Exception{
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
        Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProduto(idLojaVR, idLojaCliente);

        ProgressBar.setMaximum(vEstoqueProduto.size());

        for (Integer keyId : vEstoqueProduto.keySet()) {

            ProdutoVO oProduto = vEstoqueProduto.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarEstoqueProdutoIntegrado(vProdutoNovo, idLojaVR);
    }
    
    public void ajustarDescricao(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos (Descrição).....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.acertarDescricao(new ArrayList<>(aux.values()));
    }
}
