package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
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
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class FaucomDAO extends AbstractIntefaceDao{
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws SQLException {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	desc1 + \n" +
                    "	(case \n" +
                    "		when isnull(desc2, '') != '' then ' ' + desc2 \n" +
                    "		else '' \n" +
                    "	end) + \n" +
                    "	(case \n" +
                    "		when isnull(desc3, '') != '' then ' ' + desc3 \n" +
                    "		else '' \n" +
                    "	end) familia\n" +
                    "from	\n" +
                    "	(select\n" +
                    "		rtrim(ltrim(gg.Descricaoproduto)) desc1,\n" +
                    "		rtrim(ltrim(marc.Descricaomarca)) desc2,\n" +
                    "		rtrim(ltrim(g2.Descricaograde)) desc3,\n" +
                    "		ee.Es_precodevenda,\n" +
                    "		count(*) contagem\n" +
                    "	from\n" +
                    "		Estoque e\n" +
                    "		left join Estoqueempresa ee on ee.Es_codigo = e.Es_codigo\n" +
                    "		left join Estoqgrade gg on e.Codigoproduto = gg.Codigoproduto\n" +
                    "		left join Estoquemarca marc on gg.Codigomarca = marc.Codigomarca\n" +
                    "		left join Grade g2 on e.Codigogradesec = g2.Codigograde\n" +
                    "	where\n" +
                    "		gg.Precoigualnagrade = 1\n" +
                    "	group by\n" +
                    "		rtrim(ltrim(gg.Descricaoproduto)),\n" +
                    "		rtrim(ltrim(marc.Descricaomarca)),\n" +
                    "		rtrim(ltrim(g2.Descricaograde)),\n" +
                    "		ee.Es_precodevenda\n" +
                    "	having count(*) > 1) familia"
            )) {
                int cont = 1;
                while (rst.next()) {
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.setId(cont);
                    oFamiliaProduto.setDescricao(rst.getString("familia"));
                    oFamiliaProduto.setCodigoant(cont);
                    result.add(oFamiliaProduto);
                    cont++;
                }
            }
        }
        
        return result;
    }
        
    @Override
    public void importarFamiliaProduto() throws Exception{
        List<FamiliaProdutoVO> vFamiliaProduto;

        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        vFamiliaProduto = carregarFamiliaProduto();
        FamiliaProdutoDAO dao = new FamiliaProdutoDAO();
        dao.gerarCodigo = true;
        dao.salvar(vFamiliaProduto);
    }
    
    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.Codigosecao, m1.Secao, "
                            + "m2.Codigogrupo, m2.Descricaogrupo, "
                            + "m3.Codigosubgrupo, m3.Descricaosubgrupo "
                            + "from Secao m1 "
                            + "left join Grupo m2 on m2.Codigosecao = m1.Codigosecao "
                            + "left join Subgrupo m3 on m3.Codigogrupo = m2.Codigogrupo "
                            + "order by m1.Codigosecao, m2.Codigogrupo, m3.Codigosubgrupo "
            )) {
                while (rst.next()) {
                    MercadologicoVO mercadologico = new MercadologicoVO();                    
                    if (nivel == 1) {
                        mercadologico.setMercadologico1(rst.getInt("Codigosecao"));
                        mercadologico.setMercadologico2(0);
                        mercadologico.setMercadologico3(0);
                        mercadologico.setMercadologico4(0);
                        mercadologico.setMercadologico5(0);
                        mercadologico.setDescricao((rst.getString("Secao") == null ? "" : Utils.acertarTexto(rst.getString("Secao"))));
                        mercadologico.setNivel(nivel);
                    } else if (nivel == 2) {
                        mercadologico.setMercadologico1(rst.getInt("Codigosecao"));
                        mercadologico.setMercadologico2((rst.getInt("Codigogrupo") == 0 ? 1 : rst.getInt("Codigogrupo")));
                        mercadologico.setMercadologico3(0);
                        mercadologico.setMercadologico4(0);
                        mercadologico.setMercadologico5(0);
                        mercadologico.setDescricao((rst.getString("Descricaogrupo") == null ? "" : Utils.acertarTexto(rst.getString("Descricaogrupo"))));
                        mercadologico.setNivel(nivel);
                    } else if (nivel == 3) {
                        mercadologico.setMercadologico1(rst.getInt("Codigosecao"));
                        mercadologico.setMercadologico2((rst.getInt("Codigogrupo") == 0 ? 1 : rst.getInt("Codigogrupo")));
                        mercadologico.setMercadologico3((rst.getInt("Codigosubgrupo") == 0 ? 1 : rst.getInt("Codigosubgrupo")));
                        mercadologico.setMercadologico4(0);
                        mercadologico.setMercadologico5(0);
                        mercadologico.setDescricao((rst.getString("Descricaosubgrupo") == null ? "" : Utils.acertarTexto(rst.getString("Descricaosubgrupo"))));
                        mercadologico.setNivel(nivel);
                    }
                    result.add(mercadologico);
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
                "SELECT DISTINCT\n" +
                "  p.Es_codigo,\n" +
                "  p.Codigoproduto codFamilia,\n" +
                "  p.Es_descricao,\n" +
                "  p.Es_descricaonf,\n" +
                "  p.Es_unidade,\n" +
                "  p.Codigoecf,\n" +
                "  p.Quantidadeembalagem,\n" +
                "  p.Es_pesobruto,\n" +
                "  p.Es_pesoliquido,\n" +
                "  n.Classificacao ncm,\n" +
                "  c.Codigocest cest,\n" +
                "  p.Es_balanca,\n" +
                "  p.Validade,\n" +
                "  p.Dataalteracao,\n" +
                "  b.Codigoean,\n" +
                "  i.codigosituacaob svc_cst,\n" +
                "  i.icms svc_alq,\n" +
                "  i.reducao svc_rbc,\n" +
                "  natrec.Codigonaturezapc cod_natureza_receita,\n" +
                "  pc.codigosituacaopcdevolucao pis_cst_e,\n" +
                "  pc.codigosituacaopc pis_cst_s,\n" +
                "  e.Margemlucro,\n" +
                "  e.Cadastro,\n" +
                "  e.Es_inativo,\n" +
                "  s.Codigosecao,\n" +
                "  g.Codigogrupo,\n" +
                "  p.Codigosubgrupo,\n" +
                "  rtrim(ltrim(gg.Descricaoproduto)) descricaoproduto,\n" +
                "  rtrim(ltrim(marc.Descricaomarca)) descricaomarca,\n" +
                "  rtrim(ltrim(g2.Descricaograde)) descricaopeso\n" +
                "FROM Estoque p\n" +
                "left join Estoqgrade gg on p.Codigoproduto = gg.Codigoproduto\n" +
                "left join Estoquemarca marc on gg.Codigomarca = marc.Codigomarca\n" +
                "left join Grade g2 on p.Codigogradesec = g2.Codigograde\n" +
                "LEFT JOIN Classificacaofiscal n\n" +
                "  ON n.Codigoclassificacao = p.Codigoclassificacao\n" +
                "LEFT JOIN Classificacaocest c\n" +
                "  ON c.Codigoclassificacaocest = p.Codigoclassificacaocest\n" +
                "LEFT JOIN Estoquecodigoean b\n" +
                "  ON b.Es_codigo = p.Es_codigo\n" +
                "LEFT JOIN Estoquetributacaoestadual i\n" +
                "  ON i.Es_codigo = p.Es_codigo\n" +
                "LEFT JOIN Estoquetributacaofederal pc\n" +
                "  ON pc.Es_codigo = p.Es_codigo\n" +
                "LEFT JOIN Estoquetabelanaturezapc natrec\n" +
                "	ON pc.Codigotabelapc =natrec.Codigotabelapc\n" +
                "LEFT JOIN Estoqueempresa e\n" +
                "  ON e.Es_codigo = p.Es_codigo\n" +
                "LEFT JOIN Subgrupo sg\n" +
                "  ON sg.Codigosubgrupo = p.Codigosubgrupo\n" +
                "LEFT JOIN Grupo g\n" +
                "  ON g.Codigogrupo = sg.Codigogrupo\n" +
                "LEFT JOIN Secao s\n" +
                "  ON s.Codigosecao = g.Codigosecao\n" +
                "WHERE e.Codigoempresa = "  + idLojaCliente
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
                                      
                    oProduto.setIdDouble(rst.getDouble("Es_codigo"));
                    oProduto.setDescricaoCompleta(rst.getString("Es_descricao"));
                    oProduto.setDescricaoReduzida(rst.getString("Es_descricao"));
                    oProduto.setDescricaoGondola(rst.getString("Es_descricao"));
                    oProduto.setIdSituacaoCadastro((rst.getInt("Es_inativo") == 0 ? 1 : 0));
                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    oProduto.setMercadologico1((rst.getInt("Codigosecao") == 0 ? 1 : rst.getInt("Codigosecao")));
                    oProduto.setMercadologico2((rst.getInt("Codigogrupo") == 0 ? 1 : rst.getInt("Codigogrupo")));
                    oProduto.setMercadologico3((rst.getInt("Codigosubgrupo") == 0 ? 1 : rst.getInt("Codigosubgrupo")));
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
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }
                    
                    if ((rst.getString("cest") != null)
                            && (!rst.getString("cest").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("cest")));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    
                    oProduto.setIdFamiliaProduto(getFamilia(
                            rst.getString("descricaoproduto"),
                            rst.getString("descricaomarca"),
                            rst.getString("descricaopeso")
                    ));
                    oProduto.setMargem(rst.getDouble("Margemlucro"));
                    oProduto.setQtdEmbalagem((int) (rst.getDouble("Quantidadeembalagem") == 0 ? 1 : rst.getDouble("Quantidadeembalagem")));
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoProduto;                    
                    codigoProduto = (long) oProduto.getIdDouble();
                    
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getIdDouble());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("Validade"));
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
                        oAutomacao.setCodigoBarras(codigoProduto);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("Validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;

                        if ((rst.getString("Codigoean") != null) &&
                                (!rst.getString("Codigoean").trim().isEmpty()) &&
                                (rst.getLong("Codigoean") >= 1000000)) {
                            oAutomacao.setCodigoBarras(rst.getLong("Codigoean"));
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                        
                        if (rst.getInt("Es_balanca") == 1) {
                            oAutomacao.setIdTipoEmbalagem(4);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            //oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("Es_unidade").trim()));
                        }
                        
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
                    oProduto.setPesoBruto(rst.getDouble("Es_pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("Es_pesoliquido"));
                    
                    oProduto.setIdTipoPisCofinsDebito(retornarPisCofinsDebito(rst.getInt("pis_cst_s")));
                    oProduto.setIdTipoPisCofinsCredito(retornarPisCofinsCredito(rst.getInt("pis_cst_e")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), 
                            (rst.getString("cod_natureza_receita") == null ? "" : Utils.formataNumero(rst.getString("cod_natureza_receita").trim()))));

                    oComplemento.setIdSituacaoCadastro((rst.getInt("Es_inativo") == 0 ? 1 : 0));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);                   
                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(Global.ufEstado));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}                    
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("svc_cst"), rst.getDouble("svc_alq"), rst.getDouble("svc_rbc"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("svc_cst"), rst.getDouble("svc_alq"), rst.getDouble("svc_rbc"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("svc_cst"), rst.getDouble("svc_alq"), rst.getDouble("svc_rbc"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("svc_cst"), rst.getDouble("svc_alq"), rst.getDouble("svc_rbc"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("svc_cst"), rst.getDouble("svc_alq"), rst.getDouble("svc_rbc"), false));
                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getIdDouble());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(rst.getLong("Codigoean"));
                    oCodigoAnterior.setReferencia(0);
                    oCodigoAnterior.setNcm((rst.getString("ncm") == null ? "" : rst.getString("ncm").trim()));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("pis_cst_s"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("pis_cst_e"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("cod_natureza_receita"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("svc_cst"));

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
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        List<ProdutoVO> vProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }
    
    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> itens = new ArrayList<>();
           itens.add(new ItemComboVO(1, "LOJA 01"));
        return itens;
    }
    
    public List<ProdutoVO> carregarListaDeCodigoBarraProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select Es_codigo, Codigoean as barra, 1 as Quantembalagem "
                        + "from Estoquecodigoean "
                        + "union all "
                        + "select Es_codigo, Codigoean14 as barra, Quantembalagem "
                        + "from Estoquecodigoean14"
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getDouble("Es_codigo"));
                    oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("barra").trim())));
                    oAutomacao.setQtdEmbalagem((int) rst.getDouble("Quantembalagem"));                    
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaDeCodigoBarrasProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Código Barras Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCodigoBarraProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.verificarLoja = true;
            produto.addCodigoBarras(vProduto);
        }
    }

    @Override
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql;
        Statement stmPostgres;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
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
    
    @Override
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
    
    public List<ProdutoVO> carregarListaDePrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select Es_codigo, Es_precodevenda "
                        + "from Estoqueempresa where Codigoempresa = " + idLojaCliente
            )) {            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    oProduto.setIdDouble(rst.getDouble("Es_codigo"));
                    oComplemento.setPrecoVenda(rst.getDouble("Es_precodevenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("Es_precodevenda"));
                    oComplemento.setIdLoja(idLojaVR);
                    vProduto.add(oProduto);
                }
            }
        }
        return vProduto;
    }
    
    public void importarListaPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Preço Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDePrecoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarPrecoProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select Es_codigo, Es_precodecompra "
                        + "from Estoqueempresa where Codigoempresa = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    oProduto.setIdDouble(rst.getDouble("Es_codigo"));
                    oComplemento.setCustoComImposto(rst.getDouble("Es_precodecompra"));
                    oComplemento.setCustoSemImposto(rst.getDouble("Es_precodecompra"));
                    oComplemento.setIdLoja(idLojaVR);

                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Custo Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCustoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarCustoProduto(vProduto, idLojaVR);
        }
    }
    
    public List<ProdutoVO> carregarListaDeSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select Codigo, Inativo "
                        + "from cad_produto"
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    
                    oProduto.setIdDouble(rst.getDouble("Codigo"));
                    
                    if (rst.getInt("Inativo") == 0) {
                        oComplemento.setIdSituacaoCadastro(1);
                    } else {
                        oComplemento.setIdSituacaoCadastro(0);
                    }
                    
                    oComplemento.setIdLoja(idLojaVR);
                    vProduto.add(oProduto);
                }
            }
        }        
        return vProduto;
    }

    public void importarListaSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Situação Cadastro Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeSituacaoCadastroProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarSituacaoCadastroProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select Es_codigo, Es_quantidade + Es_pqp Es_quantidade, "
                        + "Es_quantidademinima, Es_quantidademaxima "
                        + "from Estoqueempresa where Codigoempresa = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                                          
                    oProduto.setIdDouble(rst.getDouble("Es_codigo"));
                    oComplemento.setEstoque(rst.getDouble("Es_quantidade"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("Es_quantidademinima"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("Es_quantidademaxima"));                    
                    oComplemento.setIdLoja(idLojaVR);
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Estoque Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeEstoqueProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarEstoqueProduto(vProduto, idLojaVR);
        }
    }
    
    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigofornecedor, Cadastro, Pessoa, Cnpj, Cgf, "
                            + "Razaosocial, Nomefantasia, Logradouro, Endereco, Numeroende, "
                            + "Complemento, Cep, Bairro, Cidade, Uf, Contato, Telefone1, Telefone2, "
                            + "Fax, Email, Internet, Observacoes "
                            + "from Fornecedores"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro;
                    datacadastro = new Date(new java.util.Date().getTime()); 
                    
                    oFornecedor.setId(rst.getInt("Codigofornecedor"));
                    oFornecedor.setCodigoanterior(rst.getInt("Codigofornecedor"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setRazaosocial((rst.getString("Razaosocial") == null ? "" : rst.getString("Razaosocial").trim()));
                    oFornecedor.setNomefantasia((rst.getString("Nomefantasia") == null ? "" : rst.getString("Nomefantasia").trim()));                    
                    oFornecedor.setEndereco((rst.getString("Logradouro") == null ? "" : rst.getString("Logradouro").trim()) +
                            (rst.getString("Endereco") == null ? "" : rst.getString("Endereco").trim()));
                    oFornecedor.setNumero((rst.getString("Numeroende") == null ? "0" : rst.getString("Numeroende").trim()));
                    oFornecedor.setComplemento((rst.getString("Complemento") == null ? "" : rst.getString("Complemento").trim()));
                    oFornecedor.setBairro((rst.getString("Bairro") == null ? "" : rst.getString("Bairro").trim()));
                    
                    oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(
                            Utils.acertarTexto(rst.getString("Cidade")), rst.getString("Uf").toUpperCase()));
                    
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("Cep"), Global.Cep));                    
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("Uf").trim()));
                    
                    oFornecedor.setEnderecocobranca("");
                    oFornecedor.setNumerocobranca("");
                    oFornecedor.setBairrocobranca("");
                    oFornecedor.setId_municipiocobranca(Utils.stringToInt("-1"));
                    oFornecedor.setCepcobranca(0);
                    oFornecedor.setId_estadocobranca(0);                    
                    oFornecedor.setTelefone((rst.getString("Telefone1") == null ? "" : rst.getString("Telefone1").trim()));
                    oFornecedor.setInscricaoestadual((rst.getString("Cgf") == null ? "" : rst.getString("Cgf").trim()));
                    oFornecedor.setCnpj(Utils.stringToLong((rst.getString("Cnpj") == null ? "" : rst.getString("Cnpj")), -1));
                    oFornecedor.setId_tipoinscricao(("Jurídica".equals(rst.getString("Pessoa")) ? 0 : 1));
                    oFornecedor.setObservacao((rst.getString("Observacoes") == null ? "" : rst.getString("Observacoes").trim()));
                    oFornecedor.setTelefone2((rst.getString("Telefone2") == null ? "" : rst.getString("Telefone2").trim()));
                    oFornecedor.setFax((rst.getString("Fax") == null ? "" : rst.getString("Fax").trim()));
                    oFornecedor.setEmail((rst.getString("Email") == null ? "" : rst.getString("Email").trim()));
                    oFornecedor.setId_situacaocadastro(1);                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void importarFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().salvar(vFornecedor);
        }
    }
    
    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pf.Es_codigo, pf.Codigofornecedor, pf.Codigonofornecedor, e.Quantembalagem "
                            + "from Estoquefor pf "
                            + "left join Estoqueforunidade e on e.Codigoestoquefor = pf.Codigoestoquefor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    
                    Date dataAlteracao;
                    dataAlteracao = new Date(new java.util.Date().getTime()); 
                    oProdutoFornecedor.setId_produto(rst.getInt("Es_codigo"));
                    oProdutoFornecedor.setId_fornecedor(rst.getInt("Codigofornecedor"));
                    oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    oProdutoFornecedor.setCodigoexterno((rst.getString("Codigonofornecedor") == null ? "" : rst.getString("Codigonofornecedor").trim()));
                    oProdutoFornecedor.setQtdembalagem((int) rst.getDouble("Quantembalagem"));
                    result.add(oProdutoFornecedor);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void importarProdutoFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
        List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();
        new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
    }

    public void importarAcertarEnderecoFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Endereço Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().acertarEndereco(vFornecedor);
        }
    }

    public void importarAcertarTelefoneFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Telefone Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().alterarTelefone(vFornecedor);
        }
    }
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {                        
            try (ResultSet rst = stm.executeQuery(
                    "select Codigocliente, Pessoa, Cnpjcpf, Cgf, "
                            + "Razaosocial, Nomefantasia, Cadastro, Logradouro, Endereco, Numeroende, "
                            + "Complemento, Bairro, Cep, Cidade, Uf, Pontodereferencia, Telefone1, Telefone2, "
                            + "Fax, Celular, Contato, Email, Internet, Inativo, Datatainativacao, Bloqueado, "
                            + "Limitecredito, Creditodisponivel, Observacoes "
                            + "from Clientes"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("Codigocliente"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("Codigocliente"));
                    oClientePreferencial.setNome(rst.getString("Razaosocial").trim());
                    oClientePreferencial.setEndereco((rst.getString("Logradouro") == null ? "" : rst.getString("Logradouro").trim()) 
                            + (rst.getString("Endereco") == null ? "" : rst.getString("Endereco").trim()));

                    oClientePreferencial.setNumero((rst.getString("Numeroende") == null ? "" : rst.getString("Numeroende").trim()));
                    oClientePreferencial.setComplemento((rst.getString("Complemento") == null ? "" : rst.getString("Complemento").trim()));
                    oClientePreferencial.setBairro((rst.getString("Bairro") == null ? "" : rst.getString("Bairro").trim()));
                    oClientePreferencial.setId_estado(Utils.acertarTexto(rst.getString("Uf")).equals("") ? 
                            Global.idEstado : Utils.retornarEstadoDescricao(rst.getString("Uf")));
                    
                        oClientePreferencial.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("Cidade").trim()), rst.getString("Uf").toUpperCase()) == 0 ? Global.idMunicipio : 
                                Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("Cidade").trim()), rst.getString("Uf").toUpperCase())));

                    oClientePreferencial.setCep(Utils.stringToInt(rst.getString("Cep")));
                    oClientePreferencial.setTelefone((rst.getString("Telefone1") == null ? "" : rst.getString("Telefone1").trim()));
                    oClientePreferencial.setCelular((rst.getString("Celular") == null ? "" : rst.getString("Celular").trim()));
                    oClientePreferencial.setInscricaoestadual((rst.getString("Cgf") == null ? "" : rst.getString("Cgf").trim()));
                    oClientePreferencial.setCnpj((rst.getString("Cnpjcpf") == null ? "-1" : rst.getString("Cnpjcpf").trim()));
                    oClientePreferencial.setId_tipoinscricao(("Física".equals(rst.getString("Pessoa")) ? 1 : 0));
                    oClientePreferencial.setSexo(1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getString("Cadastro").substring(0, 10).replace("-", "/"));
                    oClientePreferencial.setEmail((rst.getString("Email") == null ? "" : rst.getString("Email").trim()));
                    oClientePreferencial.setValorlimite(rst.getDouble("Limitecredito"));
                    oClientePreferencial.setFax((rst.getString("Fax") == null ? "" : rst.getString("Fax").trim()));                    
                    oClientePreferencial.setBloqueado((rst.getInt("Bloqueado") != 0));
                    oClientePreferencial.setId_situacaocadastro((rst.getInt("Inativo") == 0 ? 1 : 0));                    
                    oClientePreferencial.setTelefone2((rst.getString("Telefone2") == null ? "" : rst.getString("Telefone2").trim()));
                    oClientePreferencial.setObservacao((rst.getString("Observacoes") == null ? "" : rst.getString("Observacoes").trim()));
                    result.add(oClientePreferencial);
                }                
            }
        }
        return result;
    }   
    
    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        
        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }
    
    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente)
            throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Lancamento, Romaneio, Codigocliente, Compra, "
                            + "Vencimento, (Valor - Valorpago) ValorConta, Valorpago, Juros, Codcaixa, Codigocobranca "
                            + "from Receber "
                            + "where Datapagamento is null "
                            + "and Codigoempresa = " + idLojaCliente + ""
                            + "union all "
                            + "select Lancamento, Romaneio, Codigocliente, Compra, "
                            + "Vencimento, (Valor - Valorpago) ValorConta, Valorpago, Juros, Codcaixa, Codigocobranca "
                            + "from Receber "
                            + "where Valorpago < Valor "
                            + "and Codigoempresa = " + idLojaCliente 
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceber = new ReceberCreditoRotativoVO();
                    oReceber.setId_loja(idLojaVR);
                    oReceber.setId_clientepreferencial(rst.getInt("Codigocliente"));
                    oReceber.setValor(rst.getDouble("ValorConta"));
                    oReceber.setDataemissao(rst.getString("Compra").trim().substring(0, 10));
                    oReceber.setDatavencimento(rst.getString("Vencimento").trim().substring(0, 10));
                    oReceber.setNumerocupom(Utils.stringToInt(rst.getString("Romaneio")));
                    oReceber.setEcf(rst.getInt("Codcaixa"));
                    oReceber.setObservacao("");
                    result.add(oReceber);
                }
            }
        }
        return result;
    }
    
    @Override
    public void importarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(idLojaVR, idLojaCliente);
        
        new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCreditoRotativo, idLojaVR);
    }
    
    /********************************************/
    private int retornarPisCofinsDebito(int cst) {
        int retorno = 1;
        
        if ((cst == 1) || (cst == 50)) {
            retorno = 0;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 5;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 6;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 3;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 2;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 7;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 1;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 8;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 9;
        }
        
        return retorno;
    }

    private int retornarPisCofinsCredito(int cst) {
        int retorno = 13;
        
        if ((cst == 1) || (cst == 50)) {
            retorno = 12;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 17;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 18;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 15;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 14;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 19;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 13;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 20;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 21;
        }
        
        return retorno;
    }
    
    private int retornarICMS(String valorAliquota) {
        int retorno = 8;
        
        if (null != valorAliquota) switch (valorAliquota) {
            case "FF":
                retorno = 7;
                break;
            case "II":
                retorno = 6;
                break;
            case "NN":
                retorno = 17;
                break;
            default:
                if (Utils.truncar(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 0.0) {
                    retorno = 6;
                } else if (Utils.truncar(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 7.0) {
                    retorno = 0;
                } else if (Utils.truncar(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 12.0) {
                    retorno = 1;
                } else if (Utils.truncar(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 18.0) {
                    retorno = 2;
                } else if (Utils.truncar(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 25.0) {
                    retorno = 3;
                }   break;
        }
        
        return retorno;
    }

    public void corrigirNaturezaReceita(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Importando os dados do tributo....Natureza da Receita");
        new ProdutoDAO().alterarNaturezaReceita(carregarListaDeProdutos(idLojaVR, idLojaCliente));
    }

    private Map<String, Integer> familiaDesc;
    private int getFamilia(String descProduto, String descMarca, String descPeso) throws Exception {
        descProduto = Utils.acertarTexto(descProduto);
        descMarca = Utils.acertarTexto(descMarca);
        descPeso = Utils.acertarTexto(descPeso);
        String descricaoFamilia = descProduto;
        descricaoFamilia += !"".equals(descricaoFamilia) && !"".equals(descMarca) ? " " + descMarca : descMarca;
        descricaoFamilia += !"".equals(descricaoFamilia) && !"".equals(descPeso) ? " " + descPeso : descPeso;
        
        if (familiaDesc == null) {
            familiaDesc = new LinkedHashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                    "select id, descricao from familiaproduto order by descricao"
                )) {
                    while (rst.next()) {
                        familiaDesc.put(rst.getString("descricao"), rst.getInt("id"));
                    }
                }
            } 
        }
        if (!familiaDesc.containsKey(descricaoFamilia)) {
            return -1;
        } else {
            return familiaDesc.get(descricaoFamilia);
        }
    }

    public void importarProdutoFamilia(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos família.....");
        List<ProdutoVO> vProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        new ProdutoDAO().acertarFamiliaProdutoSysPdv(vProdutos);
    }
    
}
