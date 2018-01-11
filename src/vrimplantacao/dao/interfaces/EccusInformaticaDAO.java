package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoAnteriorDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 *
 * @author Leandro
 */
public class EccusInformaticaDAO extends AbstractIntefaceDao{

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> mercadologicos = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            String sql;
            
            sql = 
                "declare @merc1 integer, @merc1_desc varchar(60), @merc2_desc varchar(60);\n" +
                "declare cur cursor for\n" +
                "	select distinct \n" +
                "		p.CODGRUPO merc1,\n" +
                "		g.DESCRICAO merc1_desc, \n" +
                "		p.CARACTERISTICA merc2_desc\n" +
                "	from \n" +
                "		produtos p \n" +
                "		join grupo g on \n" +
                "			p.CODGRUPO = g.CODIGO\n" +
                "	order by\n" +
                "		p.CODGRUPO, merc2_desc;\n" +
                "declare @temp as table(\n" +
                "	id_mercadologico1 integer,\n" +
                "	mercadologico1 varchar(60),\n" +
                "	id_mercadologico2 integer,\n" +
                "	mercadologico2 varchar(60),\n" +
                "	id_mercadologico3 integer,\n" +
                "	mercadologico3 varchar(60)\n" +
                ");\n" +
                "\n" +
                "declare @cont integer, @mercAnterior integer = 0;\n" +
                "\n" +
                "set @cont = 0;\n" +
                "\n" +
                "\n" +
                "open cur;\n" +
                "fetch next from cur into @merc1, @merc1_desc, @merc2_desc;\n" +
                "while @@FETCH_STATUS = 0\n" +
                "begin\n" +
                "	if (@mercAnterior != @merc1)\n" +
                "	begin\n" +
                "		set @cont = 1;\n" +
                "		set @mercAnterior = @merc1;\n" +
                "	end;\n" +
                "	else\n" +
                "		set @cont = @cont + 1;\n" +
                "\n" +
                "	insert into @temp (id_mercadologico1, id_mercadologico2, id_mercadologico3, mercadologico1, mercadologico2, mercadologico3)\n" +
                "	values (@merc1, @cont, 1, @merc1_desc, @merc2_desc, @merc2_desc);\n" +
                "	fetch next from cur into @merc1, @merc1_desc, @merc2_desc;\n" +
                "end;\n" +
                "close cur;\n" +
                "deallocate cur;\n" +
                "\n" +
                "select * from @temp;";
            
            try (ResultSet rst = stm.executeQuery(sql)) {
                
                while (rst.next()) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();

                    String descricao;
                 
                    if (nivel == 1) {
                        descricao = Utils.acertarTexto(rst.getString("mercadologico1"), 35);

                        oMercadologico.mercadologico1 = rst.getInt("id_mercadologico1");
                        oMercadologico.mercadologico2 = 0;
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = descricao;
                        oMercadologico.nivel = nivel;

                    } else if (nivel == 2) {

                        descricao = Utils.acertarTexto(rst.getString("mercadologico2"), 35);

                        oMercadologico.mercadologico1 = rst.getInt("id_mercadologico1");
                        
                        if (rst.getInt("id_mercadologico2") != 0) {
                            oMercadologico.mercadologico2 = rst.getInt("id_mercadologico2");
                        } else {
                            oMercadologico.mercadologico2 = 1;
                        }
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = Utils.acertarTexto(descricao, Utils.acertarTexto(rst.getString("mercadologico1")));
                        oMercadologico.nivel = nivel;
                    } else if (nivel == 3) {

                        descricao = Utils.acertarTexto(rst.getString("mercadologico3"), 35);
                        
                        oMercadologico.mercadologico1 = rst.getInt("id_mercadologico1");
                        if (rst.getInt("id_mercadologico2") != 0) {
                            oMercadologico.mercadologico2 = rst.getInt("id_mercadologico2");
                        } else {
                            oMercadologico.mercadologico2 = 1;
                        }
                        if (rst.getInt("id_mercadologico3") != 0) {
                            oMercadologico.mercadologico3 = rst.getInt("id_mercadologico3");
                        } else {
                            oMercadologico.mercadologico3 = 1;
                        }
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = Utils.acertarTexto(descricao, rst.getString("mercadologico2"));
                        if (oMercadologico.descricao.equals("")) {
                            oMercadologico.descricao = Utils.acertarTexto(rst.getString("mercadologico1"),35);
                        }
                        oMercadologico.nivel = nivel;
                    }

                    mercadologicos.add(oMercadologico);
                }
            }
        }
        return mercadologicos; 
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, pesoBruto, pesoLiquido;

        Map<Integer, ProdutoBalancaVO> produtoBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g.CODIGO idProduto,\n" +
                    "	p.DESCRICAO + ' ' + g.DESCRICAO descriaoCompleta,\n" +
                    "	p.DESCRICAO + ' ' + coalesce(g.desc_reduzida, g.DESCRICAO) descricaoReduzida,\n" +
                    "	p.DESCRICAO + ' ' + g.DESCRICAO descricaoGondola,\n" +
                    "	p.UNIDADE idTipoEmbalagem,\n" +
                    "	1 qtdEmbalagem,\n" +
                    "	p.NFE_CSTPIS idTipoPisCofins,\n" +
                    "	p.NFE_CSTPIS_ENT idTipoPisCofinsCredito,\n" +
                    "	'' as tipoNaturezaReceita,\n" +
                    "	p.CODGRUPO mercadologico1,\n" +
                    "	p.CARACTERISTICA mercadologico2,\n" +
                    "	1 as mercadologico3,\n" +
                    "	g.NCM ncm,\n" +
                    "	-1 as idFamilia,\n" +
                    "	0 as validade,\n" +
                    "	g.MARGEM as margem,\n" +
                    "	case g.ativo when 'N' then 0 else 1 end as idSituacaoCadastro,\n" +
                    "	g.VENDA as precoVenda,\n" +
                    "	g.venda as procoVendaDiaSeguinte,\n" +
                    "	g.CUSTO as custoComImposto,\n" +
                    "	g.CUSTO as custoSemImposto,\n" +
                    "	g.PESOLIQUIDO,\n" +
                    "	g.PESOBRUTO,\n" +
                    "	35 as idEstado,\n" +
                    "	p.CODSITTRIB idAliquota,\n" +
                    "	p.TX_TRIBUTACAO,\n" +
                    "	p.TRIBUTACAO,\n" +
                    "	cast(g.CODBARRA as bigint) codigobarras,\n" +
                    "	g.QTDE estoque\n" +
                    "from\n" +
                    "	grade g\n" +
                    "	join produtos p on\n" +
                    "		g.CODPROD = p.codigo\n" +
                    "order by\n" +
                    "	g.CODIGO"
            )){

                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();

                    dataCadastro = "";
                    idSituacaoCadastro = rst.getInt("idSituacaoCadastro");
                    codigoAnterior = rst.getLong("idProduto");
                    idProduto = rst.getInt("idProduto");

                    if (produtoBalanca.containsKey((int) rst.getLong("codigobarras"))) {

                        ProdutoBalancaVO bal = produtoBalanca.get((int) rst.getLong("codigobarras"));

                        eBalanca = true;
                        codigoBalanca = bal.getCodigo();
                        validade = bal.getValidade();

                        switch (bal.getPesavel()) {
                            case "U":
                                pesavel = true;
                                idTipoEmbalagem = 0;
                                break;
                            case "P":
                                pesavel = false;
                                idTipoEmbalagem = 4;
                                break;
                        }
                    } else {
                        codigoBalanca = -1;
                        eBalanca = false;
                        pesavel = false;

                        if ((rst.getString("validade") != null) &&
                                (!rst.getString("validade").trim().isEmpty())) {
                            validade = Integer.parseInt(rst.getString("validade").trim());
                        } else {
                            validade = 0;
                        }

                        if (null != rst.getString("idTipoEmbalagem").trim()) {
                            switch (rst.getString("idTipoEmbalagem").trim()) {
                                case "KG":
                                    idTipoEmbalagem = 4;
                                    break;
                                case "UN":
                                    idTipoEmbalagem = 0;
                                    break;
                                default:
                                    idTipoEmbalagem = 0;
                                    break;
                            }
                        }
                    }

                    if ((rst.getString("qtdEmbalagem") != null)
                            && (!rst.getString("qtdEmbalagem").trim().isEmpty())) {

                        qtdEmbalagem = (int) Double.parseDouble(rst.getString("qtdEmbalagem").trim());
                    } else {
                        qtdEmbalagem = 1;
                    }

                    idFamilia = -1;
                    
                    MercadologicoVO merc = this.mapearMercadologico(
                            rst.getString("mercadologico1"), 
                            rst.getString("mercadologico2"), 
                            rst.getString("mercadologico3"));

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())
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
                    }  else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    if (eBalanca == true) {
                        codigoBarras = Long.parseLong(String.valueOf(idProduto));
                    } else {

                        if ((rst.getString("codigobarras") != null)
                                && (!rst.getString("codigobarras").trim().isEmpty())) {

                            strCodigoBarras = Utils.formataNumero(rst.getString("codigobarras").replace(".", "").trim());

                            if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                                codigoBarras = -1;
                            } else {
                                codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("codigobarras").trim()));
                            }
                        } else {
                            codigoBarras = -1;
                        }
                    }

                    if ((rst.getString("idTipoPisCofins") != null)
                            && (!rst.getString("idTipoPisCofins").trim().isEmpty())) {
                        idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(Utils.formataNumero(rst.getString("idTipoPisCofins")).trim()));
                    } else {
                        idTipoPisCofins = 1;
                    }

                    if ((rst.getString("idTipoPisCofinsCredito") != null)
                            && (!rst.getString("idTipoPisCofinsCredito").trim().isEmpty())) {

                        idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(Utils.formataNumero(rst.getString("idTipoPisCofinsCredito")).trim()));
                    } else {
                        idTipoPisCofinsCredito = 13;
                    }

                    if ((rst.getString("tipoNaturezaReceita") != null)
                            && (!rst.getString("tipoNaturezaReceita").trim().isEmpty())) {
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                                rst.getString("tipoNaturezaReceita").trim());
                    } else {
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    }

                    if ((rst.getString("idAliquota") != null) &&
                            (!rst.getString("idAliquota").trim().isEmpty())) {
                        idAliquota = retornarIcms(rst.getInt("idAliquota"), rst.getInt("TX_TRIBUTACAO"));
                    } else {
                        idAliquota = 8;
                    }

                    precoVenda = rst.getDouble("precoVenda");

                    custo = rst.getDouble("custoComImposto");

                    if ((rst.getString("margem") != null)
                            && (!rst.getString("margem").trim().isEmpty())) {
                        margem = Double.parseDouble(rst.getString("margem"));
                    } else {
                        margem = 0;
                    }

                    if ((rst.getString("PESOBRUTO") != null)
                            && (!rst.getString("PESOBRUTO").trim().isEmpty())) {
                        pesoBruto = Double.parseDouble(rst.getString("PESOBRUTO"));
                    } else {
                        pesoBruto = 0;
                    }

                    if ((rst.getString("PESOLIQUIDO") != null)
                            && (!rst.getString("PESOLIQUIDO").trim().isEmpty())) {
                        pesoLiquido = Double.parseDouble(rst.getString("PESOLIQUIDO"));
                    } else {
                        pesoLiquido = 0;
                    }


                    oProduto.id = idProduto;
                    oProduto.setDescricaoCompleta(rst.getString("descriaoCompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    oProduto.reajustarDescricoes();
                    
                    oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.qtdEmbalagem = qtdEmbalagem;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                    oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                    oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                    oProduto.pesavel = pesavel;
                    oProduto.mercadologico1 = merc.getMercadologico1();
                    oProduto.mercadologico2 = merc.getMercadologico2();
                    oProduto.mercadologico3 = merc.getMercadologico3();
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
                    oProduto.validade = validade;
                    oProduto.margem = margem;
                    oProduto.pesoBruto = pesoBruto;
                    oProduto.pesoLiquido = pesoLiquido;
                    oProduto.dataCadastro = dataCadastro;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                    oComplemento.estoque = rst.getDouble("estoque");
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oComplemento.setCustoComImposto(custo);
                    oComplemento.setCustoSemImposto(custo);

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
                    oAutomacao.qtdEmbalagem = qtdEmbalagem;
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                    oProduto.vAutomacao.add(oAutomacao);

                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.codigoanterior = codigoAnterior;

                    if ((rst.getString("codigobarras") != null)
                            && (!rst.getString("codigobarras").trim().isEmpty())) {
                        oCodigoAnterior.barras = Long.parseLong(
                                Utils.formataNumero(rst.getString("codigobarras").trim()));
                    } else {
                        oCodigoAnterior.barras = -1;
                    }

                    if ((rst.getString("idTipoPisCofins") != null)
                            && (!rst.getString("idTipoPisCofins").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(Utils.formataNumero(rst.getString("idTipoPisCofins")).trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("idTipoPisCofinsCredito") != null)
                            && (!rst.getString("idTipoPisCofinsCredito").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(Utils.formataNumero(rst.getString("idTipoPisCofinsCredito")).trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    if ((rst.getString("tipoNaturezaReceita") != null)
                            && (!rst.getString("tipoNaturezaReceita").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("tipoNaturezaReceita").trim());
                    } else {
                        oCodigoAnterior.naturezareceita = -1;
                    }

                    if ((rst.getString("idAliquota") != null)
                            && (!rst.getString("idAliquota").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = Utils.acertarTexto(rst.getString("idAliquota").trim().replace("'", ""));
                    } else {
                        oCodigoAnterior.ref_icmsdebito = "";
                    }

                    oCodigoAnterior.estoque = rst.getDouble("estoque");
                    oCodigoAnterior.e_balanca = eBalanca;
                    oCodigoAnterior.codigobalanca = codigoBalanca;
                    oCodigoAnterior.custosemimposto = rst.getDouble("custoSemImposto");
                    oCodigoAnterior.custocomimposto = rst.getDouble("custoComImposto");
                    oCodigoAnterior.margem = rst.getDouble("margem");
                    oCodigoAnterior.precovenda = rst.getDouble("precoVenda");
                    //Milenio oCodigoAnterior.referencia = -1;

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())) {
                        oCodigoAnterior.ncm = rst.getString("ncm").trim();
                    } else {
                        oCodigoAnterior.ncm = "";
                    }

                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    vProduto.add(oProduto);
                }
            }

            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }    

    @Override
    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }

    @Override
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }

    @Override
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    
    @Override
    public void importarMercadologico() throws Exception{
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);

        new MercadologicoAnteriorDAO().reorganizaMercadologico();
        
        dao.salvarMax();
    }

    private MercadologicoVO mapearMercadologico(String mercadologico1, 
            String mercadologico2, String mercadologico3) throws Exception {        
        
        if ((mercadologico1 != null && mercadologico2 != null && mercadologico3 != null) && (
            !mercadologico1.trim().isEmpty() && !mercadologico2.trim().isEmpty() && !mercadologico3.trim().isEmpty()
        )) {
        
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select ant1, ant2, ant3, descricao, nivel "
                                + "from implantacao.codigoanteriormercadologico "
                                + "where ant1 = " + Utils.acertarTexto(mercadologico1) + " and "
                                + "descricao = '" + Utils.acertarTexto(mercadologico2) + "' and "
                                + "ant3 = 0"
                )) {
                    if (rst.next()) {
                        MercadologicoVO vo = new MercadologicoVO();
                        vo.setMercadologico1(rst.getInt("ant1"));
                        vo.setMercadologico2(rst.getInt("ant2"));
                        vo.setMercadologico3(rst.getInt("ant3"));
                        vo.setMercadologico4(0);
                        vo.setMercadologico5(0);
                        vo.setNivel(rst.getInt("nivel"));
                        vo.setDescricao(rst.getString("descricao"));

                        return vo;
                    } else {
                        return MercadologicoDAO.getMaxMercadologico();
                    }
                }
            }
        } else {
            return MercadologicoDAO.getMaxMercadologico();
        }
    }

    private int retornarIcms(int cst, double aliq) {
        switch(cst) {
            case 0: {	
                if (aliq == 0.00d) {
                    return 8;
                } else if (aliq == 7.00d) {
                    return 0;
                } else if (aliq == 8.40d) {
                    return 15;
                } else if (aliq == 12.00d) {
                    return 1;
                } else if (aliq == 18.00d) {
                    return 2;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {  
                return 8;
            }
            case 40: {    
                return 6;
            }
            case 60: {    
                return 7;
            }
            case 500: {
                return 7;
            }
            default: return 8;
        }
        
    }
    
    @Override
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
        Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProduto(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vPrecoProduto.size());

        for (Integer keyId : vPrecoProduto.keySet()) {

            ProdutoVO oProduto = vPrecoProduto.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarPrecoProdutoRapido(vProdutoNovo, idLojaVR);
    }
    
    @Override
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception{
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
        Map<Integer, ProdutoVO> vEstoqueProdutoMilenio = carregarEstoqueProduto(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vEstoqueProdutoMilenio.size());

        for (Integer keyId : vEstoqueProdutoMilenio.keySet()) {

            ProdutoVO oProduto = vEstoqueProdutoMilenio.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarEstoqueProdutoRapido(vProdutoNovo, idLojaVR);
    }
    
    @Override
    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
        Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProduto(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vCustoProduto.size());

        for (Integer keyId : vCustoProduto.keySet()) {

            ProdutoVO oProduto = vCustoProduto.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarCustoProdutoRapido(vProdutoNovo, idLojaVR);
    }

    public void importarDescricaoProduto(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Descrição dos Produtos.....");

        new ProdutoDAO().alterarDescricaoProduto(carregarProduto(idLojaVR, idLojaCliente).values());
    }

}
