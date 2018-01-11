package vrimplantacao.dao.interfaces;

import java.sql.Date;
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
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.dao.cadastro.ClientePreferencialContatoDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorContatoDAO;
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
import vrimplantacao.vo.vrimplantacao.ClientePreferencialContatoVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorContatoVO;
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
import vrimplantacao.vo.vrimplantacao.SqlVO;
import vrimplantacao2.parametro.Parametros;

public class AlfaSoftwareDAO {

    public SqlVO consultar(String i_sql) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = ConexaoAccess.getConexao().createStatement();
        rst = stm.executeQuery(i_sql);

        SqlVO oSql = new SqlVO();

        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
            oSql.vHeader.add(rst.getMetaData().getColumnName(i));
        }

        while (rst.next()) {
            List<String> vColuna = new ArrayList();

            for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                vColuna.add(rst.getString(i));
            }

            oSql.vConsulta.add(vColuna);
        }

        stm.close();

        return oSql;
    }

    public String executar(String i_sql) throws Exception {
        Statement stm = null;

        try {
            stm = ConexaoAccess.getConexao().createStatement();
            int result = stm.executeUpdate(i_sql);
            stm.close();
            return "Executado com sucesso: " + result + " registros afetados.";
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
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT P.PROGRP, 1, G.GRPDES, P.PROSBG, SB.SBGDES, \n"
                    + "1 AS COD_M3,  SB.SBGDES AS DESC_M3\n"
                    + "FROM ((TB_SAVPRO P) INNER JOIN TB_SAVGRP G ON G.GRPIDE = P.PROGRP)\n"
                    + "INNER JOIN TB_SAVSBG SB ON SB.SBGIDE = P.PROSBG\n"
                    + "ORDER BY  P.PROGRP,  P.PROSBG"
            )) {
                while (rst.next()) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    if (nivel == 1) {
                        oMercadologico.setMercadologico1(rst.getInt("PROGRP"));
                        oMercadologico.setDescricao(rst.getString("GRPDES").trim().replace("?O", "AO"));
                    } else if (nivel == 2) {
                        oMercadologico.setMercadologico1(rst.getInt("PROGRP"));
                        oMercadologico.setMercadologico2(rst.getInt("PROSBG"));
                        oMercadologico.setDescricao(rst.getString("SBGDES").trim().replace("?O", "AO"));
                    } else if (nivel == 3) {
                        oMercadologico.setMercadologico1(rst.getInt("PROGRP"));
                        oMercadologico.setMercadologico2(rst.getInt("PROSBG"));
                        oMercadologico.setMercadologico3(rst.getInt("COD_M3"));
                        oMercadologico.setDescricao(rst.getString("DESC_M3").trim().replace("?O", "AO"));
                    }
                    oMercadologico.setNivel(nivel);
                    result.add(oMercadologico);
                }
            }
        }
        return result;
    }

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

    public List<ProdutoVO> carregarProdutos(int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        String descricao, mercadologico1, mercadologico2, codigoBarras, cstIcms,
               ncm, unidade, cstPis, cstCofins, aliquotaIcms, percReducaoIcms;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT P.PROIDE, P.PRODES, P.PROGRP, P.PROSBG, P.PROBAR, P.PROUND, P.PROMAR, "
                    + "P.PROAUX, P.CLASSEFISCALID, NCM.CFISCALCODIGO, UN.UNDDES, P.PROTBT, "
                    + "NCM.CFISCALCODIGO, NCM.CFISCALPIS_CST, NCM.CFISCALCOFINS_CST, NCM.CFISCALALQICMS,"
                    + "NCM.CFISCALPERREDUCAOICMS\n"
                    + "FROM ((TB_SAVPRO P) LEFT JOIN TB_CLASSE_FISCAL NCM ON NCM.CFISCALID = P.CLASSEFISCALID)\n"
                    + "LEFT JOIN TB_SAVUND UN ON UN.UNDIDE = P.PROUND"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    
                    descricao = rst.getString("PRODES");
                    mercadologico1 = rst.getString("PROGRP");
                    mercadologico2 = rst.getString("PROSBG");
                    codigoBarras = rst.getString("PROBAR");
                    cstIcms = rst.getString("PROTBT");
                    aliquotaIcms = rst.getString("CFISCALALQICMS");
                    percReducaoIcms = rst.getString("CFISCALPERREDUCAOICMS");
                    ncm = rst.getString("CFISCALCODIGO");
                    unidade = rst.getString("UNDDES");
                    cstPis = rst.getString("CFISCALPIS_CST");
                    cstCofins = rst.getString("CFISCALCOFINS_CST");
                    
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

                    oProduto.setId(rst.getInt("PROIDE"));
                    oProduto.setDescricaoCompleta(descricao == null ? "" : descricao.trim().replace("?O", "AO"));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setIdSituacaoCadastro(1);
                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    oProduto.setMercadologico1(Integer.parseInt(mercadologico1));
                    oProduto.setMercadologico2(Integer.parseInt(mercadologico2));
                    oProduto.setMercadologico3(1);
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(Utils.stringToInt(cstPis)));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(Utils.stringToInt(cstCofins)));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    
                    if ((ncm != null) && (!ncm.trim().isEmpty()) && (ncm.trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(ncm.trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }
                    
                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(rst.getDouble("PROMAR"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                              
                    ProdutoBalancaVO produtoBalanca;
                    if (oProduto.getId() <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get(oProduto.getId());
                    } else {
                        produtoBalanca = null;
                    }
                    
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        oCodigoAnterior.setE_balanca(true);

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
                        oCodigoAnterior.setE_balanca(false);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(unidade.trim()));
                        if ((codigoBarras != null) && (!codigoBarras.trim().isEmpty())) {
                            codigoBarras = Utils.formataNumero(codigoBarras);
                            if (Long.parseLong(codigoBarras) >= 1000000) {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(codigoBarras)));
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    }
                    
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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);

                    oComplemento.setIdLoja(idLojaVr);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), Utils.stringToInt(cstIcms), Double.parseDouble(aliquotaIcms), Double.parseDouble(percReducaoIcms)));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), Utils.stringToInt(cstIcms), Double.parseDouble(aliquotaIcms), Double.parseDouble(percReducaoIcms)));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), Utils.stringToInt(cstIcms), Double.parseDouble(aliquotaIcms), Double.parseDouble(percReducaoIcms)));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), Utils.stringToInt(cstIcms), Double.parseDouble(aliquotaIcms), Double.parseDouble(percReducaoIcms)));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), Utils.stringToInt(cstIcms), Double.parseDouble(aliquotaIcms), Double.parseDouble(percReducaoIcms)));
                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    
                    String barraAnterior = codigoBarras;
                    if ((barraAnterior != null) && (!barraAnterior.trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(barraAnterior)));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    
                    String ncmAnt = ncm;
                    if ((ncmAnt != null) && (!ncmAnt.trim().isEmpty())) {
                        oCodigoAnterior.setNcm(Utils.formataNumero(ncm.trim()));
                    } else {
                        oCodigoAnterior.setNcm("");
                    }
                    
                    oCodigoAnterior.setId_loja(idLojaVr);
                    oCodigoAnterior.setRef_icmsdebito(cstIcms);

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    public void importarProduto(int idLojaVR) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        List<ProdutoVO> vProdutos = carregarProdutos(idLojaVR);
        List<LojaVO> vLoja = new LojaDAO().carregar();
        ProgressBar.setMaximum(vProdutos.size());

        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }
    
    public List<ProdutoVO> carregarCustoProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT PROIDE, PROCTM FROM TB_SAVPRO"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("PROIDE"));
                    oComplemento.setIdLoja(idLojaVr);
                    oComplemento.setCustoComImposto(rst.getDouble("PROCTM"));
                    oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    oProduto.vComplemento.add(oComplemento);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    public void importarCustoProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto...");
            vProduto = carregarCustoProduto(idLojaVr);
            new ProdutoDAO().alterarCustoProduto(vProduto, idLojaVr);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarPrecoProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT PROIDE, PROVND FROM TB_SAVPRO"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("PROIDE"));
                    oComplemento.setIdLoja(idLojaVr);
                    oComplemento.setPrecoVenda(rst.getDouble("PROVND"));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    public void importarPrecoProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Produto...");
            vProduto = carregarPrecoProduto(idLojaVr);
            new ProdutoDAO().alterarPrecoProduto(vProduto, idLojaVr);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarEstoqueProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT PROIDE, PROQTD, PROMIN, PROMAX FROM TB_SAVPRO"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("PROIDE"));
                    oComplemento.setIdLoja(idLojaVr);
                    oComplemento.setEstoque(rst.getDouble("PROQTD"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("PROMIN"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("PROMAX"));
                    oProduto.vComplemento.add(oComplemento);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    public void importarEstoqueProduto(int idLojaVr) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto...");
            vProduto = carregarEstoqueProduto(idLojaVr);
            new ProdutoDAO().alterarEstoqueProduto(vProduto, idLojaVr);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarPisCofinsProduto() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        String cstPis, cstCofins;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT P.PROIDE, P.PROAUX, P.CLASSEFISCALID, NCM.CFISCALCODIGO, NCM.CFISCALPIS_CST, NCM.CFISCALCOFINS_CST\n" +
                    "FROM ((TB_SAVPRO P) LEFT JOIN TB_CLASSE_FISCAL NCM ON NCM.CFISCALID = P.CLASSEFISCALID)\n" +
                    "LEFT JOIN TB_SAVUND UN ON UN.UNDIDE = P.PROUND"
            )) {
                while (rst.next()) {
                    cstPis = rst.getString("CFISCALPIS_CST");
                    cstCofins = rst.getString("CFISCALCOFINS_CST");
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setId(rst.getInt("PROIDE"));
                    
                    if ((cstPis != null) && (!cstPis.trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(Integer.parseInt(cstPis)));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                    }
                    
                    if ((cstCofins != null) && (!cstCofins.trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(Integer.parseInt(cstCofins)));
                    } else {
                        oProduto.setIdTipoPisCofinsCredito(13);
                    }
                    
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    result.add(oProduto);
                }
            }
        }
        return result;
    }
    
    public void importarPisCofinsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Piscofins...");
            vProduto = carregarPisCofinsProduto();
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
        int id_estado, id_municipio;
        String cnpj, razaoSocial, nomeFantasia, inscricaoEstadual, endereco, 
                numero, bairro, complemento, cep, municipio, estado, observacao;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT F.FORIDE, F.FORNOM, F.FORFAN, F.FOREND, F.FORNUM, F.FORCMP, F.FORBAI, \n"
                    + "M.MUNNOM, F.FORMUN, F.FOREST, F.FORCEP, FORTPS, \n"
                    + "F.FORDTC, F.FORCIC, F.FORNRG, F.FOROBS, F.FORBLQ\n"
                    + "FROM ((TB_SAVFOR F) LEFT JOIN TB_SAVMUN M ON M.MUNIDE = F.FORMUN)"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    oFornecedor.setId(rst.getInt("FORIDE"));
                    oFornecedor.setCodigoanterior(oFornecedor.getId());
                    
                    razaoSocial = rst.getString("FORNOM");
                    oFornecedor.setRazaosocial(razaoSocial == null ? "" : razaoSocial.trim().replace("?O", "AO"));
                    
                    nomeFantasia = rst.getString("FORFAN");
                    oFornecedor.setNomefantasia(nomeFantasia == null ? "" : nomeFantasia.trim().replace("?O", "AO"));
                    oFornecedor.setId_tipoinscricao("J".equals(rst.getString("FORTPS")) ? 0 : 1);
                    
                    cnpj = Utils.formataNumero(rst.getString("FORCIC"));                    
                    if ((cnpj != null) && (!cnpj.trim().isEmpty())) {
                        oFornecedor.setCnpj(Long.parseLong(cnpj.trim()));
                    } else {
                        oFornecedor.setCnpj(-1);
                    }
                    
                    inscricaoEstadual = rst.getString("FORNRG");
                    if ((inscricaoEstadual != null) && (!inscricaoEstadual.trim().isEmpty())) {
                        oFornecedor.setInscricaoestadual(inscricaoEstadual.trim());
                    } else {
                        oFornecedor.setInscricaoestadual("ISENTO");
                    }
                    
                    endereco = rst.getString("FOREND");
                    oFornecedor.setEndereco(endereco == null ? "" : endereco.trim().replace("?O", "AO"));
                    
                    numero = rst.getString("FORNUM");
                    oFornecedor.setNumero(numero == null ? "" : numero.trim());
                    
                    bairro = rst.getString("FORBAI").trim();
                    oFornecedor.setBairro(bairro == null ? "" : bairro.trim().replace("?O", "AO"));
                    
                    complemento = rst.getString("FORCMP");
                    oFornecedor.setComplemento(complemento == null ? "" : complemento.trim().replace("?O", "AO"));
                    
                    cep = Utils.formataNumero(rst.getString("FORCEP"));
                    if ((cep != null) && (!cep.trim().isEmpty())) {
                        oFornecedor.setCep(Long.parseLong(cep.trim()));
                    } else {
                        oFornecedor.setCep(Parametros.get().getCepPadrao());
                    }
                    
                    municipio = Utils.acertarTexto(rst.getString("MUNNOM"));
                    estado = Utils.acertarTexto(rst.getString("FOREST"));
                    if ((municipio != null) && (estado != null)) {
                        id_estado = Utils.retornarEstadoDescricao(estado.trim());
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(municipio.trim(), estado.trim());
                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao().getId();// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        id_municipio = Parametros.get().getMunicipioPadrao().getId(); // CIDADE DO CLIENTE;                   
                    }
                    
                    oFornecedor.setId_estado(id_estado);
                    oFornecedor.setId_municipio(id_municipio);
                    
                    observacao = rst.getString("FOROBS");
                    oFornecedor.setObservacao(observacao == null ? "" : observacao.trim().replace("?O", "AO"));
                    //oFornecedor.setDatacadastro(rst.getString("FORDTC").substring(0, 10).replace("-", "/").trim());
                    result.add(oFornecedor);                    
                }
            }
        }
        return result;
    }
    
    public void importarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        vFornecedor = carregarFornecedor();
        new FornecedorDAO().salvarCnpj(vFornecedor);
    }
    
    public List<FornecedorContatoVO> carregarContatoFornecedor() throws Exception {
        List<FornecedorContatoVO> result = new ArrayList<>();
        double idFornecedor;
        String numero, observacao, descContato;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT C.CTAFOR, TC.TCTDES, C.CTANUM, C.CTAOBS\n"
                    + "FROM TB_SAVCTA_FOR C\n"
                    + "INNER JOIN TB_SAVTCT TC ON TC.TCTIDE = C.CTATCT"
            )) {
                while (rst.next()) {                    
                    FornecedorContatoVO oFornecedorContato = new FornecedorContatoVO();
                    idFornecedor = rst.getDouble("CTAFOR");
                    oFornecedorContato.setIdFornecedorAnterior(idFornecedor);
                    
                    descContato = rst.getString("TCTDES");
                    numero = rst.getString("CTANUM");
                    observacao = rst.getString("CTAOBS");
                    
                    if ((numero != null) && (!numero.trim().isEmpty())) {
                        
                        if ((observacao != null) && (!observacao.trim().isEmpty())) {
                            oFornecedorContato.setNome(Utils.acertarTexto(observacao.trim()));
                        } else {
                            oFornecedorContato.setNome(Utils.acertarTexto(descContato.trim()));
                        }                        
                        
                        if (numero.contains("www") || (numero.contains("@"))) {                            
                            oFornecedorContato.setEmail(Utils.acertarTexto(numero.trim().toLowerCase()));
                        } else {
                            if (oFornecedorContato.getNome().contains("CELULAR")) {
                                oFornecedorContato.setCelular(Utils.formataNumero(numero.trim()));
                            } else {
                                oFornecedorContato.setTelefone(Utils.formataNumero(numero.trim()));
                            }
                        }
                        result.add(oFornecedorContato);
                    }
                }
            }
        }
        return result;
    }
    
    public void importarFornecedorContato() throws Exception {
        List<FornecedorContatoVO> vFornecedorContato = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Fornecedor Contato...");
        vFornecedorContato = carregarContatoFornecedor();
        new FornecedorContatoDAO().salvar(vFornecedorContato);
    }
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        String codigoExterno;
        java.sql.Date dataAlteracao;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT PROIDE, PROAUX, PROFOR FROM TB_SAVPRO"
            )) {
                while (rst.next()) {
                    dataAlteracao = new Date(new java.util.Date().getTime());
                    codigoExterno = rst.getString("PROAUX");
                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    oProdutoFornecedor.setId_fornecedor(rst.getInt("PROFOR"));
                    oProdutoFornecedor.setId_produto(rst.getInt("PROIDE"));
                    oProdutoFornecedor.setCodigoexterno(codigoExterno);
                    oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    result.add(oProdutoFornecedor);
                }
            }
        }
        return result;        
    }
    
    public void importarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vProdutoFornecedor = carregarProdutoFornecedor();
            if (!vProdutoFornecedor.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialVO> carregarClientePreferencial() throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        String nome, endereco, numero, complemento, bairro, estado, cep,
                sexo, dataNasc, cnpj, inscricaoEstadual, mae, pai, observacao,
                valorCredito, dataCad, municipio;
        int id_estado, id_municipio;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT C.CLIIDE, C.CLINOM, C.CLIEND, C.CLINUM, C.CLICMP, C.CLIBAI, C.CLICIC,"
                    + "C.CLIMUN, M.MUNNOM, C.CLIEST, C.CLICEP, C.CLISEX, C.CLIDTN, C.CLITPS, "
                    + "C.CLINRG, C.CLIMAE, C.CLIPAI, C.CLIOBS, C.CLIBLQ, C.CLICRE_VALOR, C.CLIDTC "
                    + "FROM ((TB_SAVCLI C) LEFT JOIN TB_SAVMUN M ON M.MUNIDE = C.CLIMUN) "
            )) {
                while (rst.next()) {
                    nome = rst.getString("CLINOM");
                    endereco = rst.getString("CLIEND");
                    numero = rst.getString("CLINUM");
                    complemento = rst.getString("CLICMP");
                    bairro = rst.getString("CLIBAI");
                    estado = rst.getString("CLIEST");
                    municipio = rst.getString("MUNNOM");
                    cep = rst.getString("CLICEP");
                    sexo = rst.getString("CLISEX");
                    dataNasc = rst.getString("CLIDTN");
                    cnpj = rst.getString("CLICIC");
                    inscricaoEstadual = rst.getString("CLINRG");
                    mae = rst.getString("CLIMAE");
                    pai = rst.getString("CLIPAI");
                    observacao = rst.getString("CLIOBS");
                    valorCredito = rst.getString("CLICRE_VALOR");
                    dataCad = rst.getString("CLIDTC");
                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    oClientePreferencial.setId(rst.getInt("CLIIDE"));
                    oClientePreferencial.setCodigoanterior(oClientePreferencial.getId());
                    oClientePreferencial.setId_tipoinscricao("J".equals(rst.getString("CLITPS").trim()) ? 0 : 1);
                    oClientePreferencial.setBloqueado((rst.getInt("CLIBLQ") != 0));
                    oClientePreferencial.setNome(nome == null ? "" : nome.trim().replace("?O", "AO"));
                    oClientePreferencial.setEndereco(endereco == null ? "" : endereco.trim().replace("?O", "AO"));
                    oClientePreferencial.setNumero(numero == null ? "" : numero.trim());
                    oClientePreferencial.setComplemento(complemento == null ? "" : complemento.trim().replace("?O", "AO"));
                    oClientePreferencial.setBairro(bairro == null ? "" : bairro.replace("?O", "AO"));
                    
                    if ((cep != null) && (!cep.trim().isEmpty())) {
                        oClientePreferencial.setCep(Long.parseLong(cep.trim()));
                    } else {
                        oClientePreferencial.setCep(Parametros.get().getCepPadrao());
                    }
                    
                    if ((sexo != null) && (!sexo.trim().isEmpty())) {
                        oClientePreferencial.setSexo("M".equals(sexo.trim()) ? 1 : 0);
                    } else {
                        oClientePreferencial.setSexo(1);
                    }
                    
                    if ((dataNasc != null) && (!dataNasc.trim().isEmpty())) {
                        oClientePreferencial.setDatanascimento(dataNasc);
                    } else {
                        oClientePreferencial.setDatanascimento("");
                    }
                    
                    if ((cnpj != null) && (!cnpj.trim().isEmpty())) {
                        oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(cnpj.trim())));
                    } else {
                        oClientePreferencial.setCnpj(-1);
                    }
                    
                    if ((inscricaoEstadual != null) && (!inscricaoEstadual.trim().isEmpty())) {
                        oClientePreferencial.setInscricaoestadual(inscricaoEstadual.trim());
                    } else {
                        oClientePreferencial.setInscricaoestadual("ISENTO");
                    }
                    
                    if ((mae != null) && (mae.trim().isEmpty())) {
                        oClientePreferencial.setNomemae(mae.trim());
                    } else {
                        oClientePreferencial.setNomemae("");
                    }
                    
                    if ((pai != null) && (pai.trim().isEmpty())) {
                        oClientePreferencial.setNomepai(pai.trim());
                    } else {
                        oClientePreferencial.setNomepai("");
                    }
                    
                    oClientePreferencial.setObservacao(observacao);
                    oClientePreferencial.setValorlimite(Double.parseDouble(valorCredito));
                    oClientePreferencial.setDatacadastro(dataCad.trim());
                    
                    municipio = Utils.acertarTexto(municipio);
                    estado = Utils.acertarTexto(estado);
                    
                    if ((municipio != null) && (estado != null)) {
                        id_estado = Utils.retornarEstadoDescricao(estado.trim());
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(municipio.trim(), estado.trim());
                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao().getId();// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        id_municipio = Parametros.get().getMunicipioPadrao().getId(); // CIDADE DO CLIENTE;                   
                    }
                    
                    oClientePreferencial.setId_estado(id_estado);
                    oClientePreferencial.setId_municipio(id_municipio);
                    result.add(oClientePreferencial);
                }
            }
        }       
        return result;
    }
    
    public void importarClientePreferencial(int idLojaVr) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vClientePreferencial = carregarClientePreferencial();
            new PlanoDAO().salvar(idLojaVr);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVr, 1);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialContatoVO> carregarClientePreferencialContato() throws Exception {
        List<ClientePreferencialContatoVO> result = new ArrayList<>();
        double idCliente;
        String numero, observacao, descContato;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT C.CTACLI, TC.TCTDES, C.CTANUM, C.CTAOBS\n"
                    + "FROM TB_SAVCTA_CLI C\n"
                    + "INNER JOIN TB_SAVTCT TC ON TC.TCTIDE = C.CTATCT"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialContatoVO oClientePreferencialContato = new ClientePreferencialContatoVO();
                    idCliente = rst.getDouble("CTACLI");
                    oClientePreferencialContato.setIdClientePreferencialAnterior(idCliente);
                    
                    descContato = rst.getString("TCTDES");
                    numero = rst.getString("CTANUM");
                    observacao = rst.getString("CTAOBS");
                    
                    if ((numero != null) && (!numero.trim().isEmpty())) {
                        
                        if ((observacao != null) && (!observacao.trim().isEmpty())) {
                            oClientePreferencialContato.setNome(Utils.acertarTexto(observacao.trim()));
                        } else {
                            oClientePreferencialContato.setNome(Utils.acertarTexto(descContato.trim()));
                        }                        
                        
                        if (numero.contains("www") || (numero.contains("@"))) {                            
                            oClientePreferencialContato.setEmail(Utils.acertarTexto(numero.trim().toLowerCase()));
                        } else {
                            if (oClientePreferencialContato.getNome().contains("CELULAR")) {
                                oClientePreferencialContato.setCelular(Utils.formataNumero(numero.trim()));
                            } else {
                                oClientePreferencialContato.setTelefone(Utils.formataNumero(numero.trim()));
                            }
                        }
                        result.add(oClientePreferencialContato);
                    }
                }
            }
        }
        return result;
    }
    
    public void importarClientePreferencialContato() throws Exception {
        List<ClientePreferencialContatoVO> vClientePreferencialContato = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial Contato...");
        vClientePreferencialContato = carregarClientePreferencialContato();
        new ClientePreferencialContatoDAO().salvar(vClientePreferencialContato);
    }
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVr) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        String dataEmissao, dataVencimento, observacao, numDoc, obs;
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CRCNUM, CRCCLI, CRCDTE, CRCDTV, (CRCVDP - CRCVPG) AS VALOR, CRCOBS, CRCDOC\n"
                    + "FROM TB_SAVCRC\n"
                    + "WHERE CRCVPG = 0"
            )) {
                while (rst.next()) {
                    observacao = "";
                    obs = rst.getString("CRCOBS");
                    numDoc = rst.getString("CRCDOC");
                    
                    if ((obs != null) && (!obs.trim().isEmpty())) {
                        observacao = obs;
                    }
                    
                    if ((numDoc != null) && (!numDoc.trim().isEmpty())) {
                        observacao = observacao + " NUMDOC.: " + numDoc;
                    }
                    
                    dataEmissao = rst.getString("CRCDTE").trim().substring(0, 10).replace("-", "/");
                    dataVencimento = rst.getString("CRCDTV").trim().substring(0, 10).replace("-", "/");
                    ReceberCreditoRotativoVO oReceber = new ReceberCreditoRotativoVO();
                    oReceber.setId_clientepreferencial(rst.getInt("CRCCLI"));
                    oReceber.setValor(rst.getDouble("VALOR"));
                    oReceber.setNumerocupom(rst.getInt("CRCNUM"));
                    oReceber.setDataemissao(dataEmissao);
                    oReceber.setDatavencimento(dataVencimento);
                    
                    if ((observacao != null) && (!observacao.trim().isEmpty())) {
                        oReceber.setObservacao(observacao.replace("?O", "AO"));
                    }
                    result.add(oReceber);
                }
            }
        }
        return result;
    }
    
    public void importarReceberCreditoRotativo(int idLojaVr) throws Exception {
        List<ReceberCreditoRotativoVO> vReceber = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo Loja "+idLojaVr+"...");
            vReceber = carregarReceberCreditoRotativo(idLojaVr);
            new ReceberCreditoRotativoDAO().salvar(vReceber, idLojaVr);
        } catch (Exception ex) {
            throw ex;
        }
    }
}