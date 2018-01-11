package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class Maximus_DatasyncDAO extends AbstractIntefaceDao {

    @Override
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;
        Utils util = new Utils();

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
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct m1.SETOR_ID MERCA1, m1.DESCRICAO DESC_MERCA1,\n"
                    + "m2.SECAO_ID MERCA2, m2.DESCRICAO DESC_MERCA2,\n"
                    + "COALESCE(m3.CATEGORIA_ID, 1) MERCA3, \n"
                    + "COALESCE(m3.DESCRICAO, m2.DESCRICAO) DESC_MERCA3\n"
                    + "from ESTOQUE P\n"
                    + "inner join ESTOQUE_SETORES m1 on m1.SETOR_ID = P.SETOR_ID\n"
                    + "inner join ESTOQUE_SECOES m2 on m2.SECAO_ID = P.SECAO_ID\n"
                    + "left join ESTOQUE_CATEGORIAS m3 on m3.CATEGORIA_ID = P.CATEGORIA_ID\n"
                    + "order by MERCA1, MERCA2, MERCA3"
            )) {
                while (rst.next()) {
                    MercadologicoVO mercadologico = new MercadologicoVO();
                    if (nivel == 1) {
                        mercadologico.setMercadologico1(rst.getInt("MERCA1"));
                        mercadologico.setDescricao(rst.getString("DESC_MERCA1"));
                    } else if (nivel == 2) {
                        mercadologico.setMercadologico1(rst.getInt("MERCA1"));
                        mercadologico.setMercadologico2(rst.getInt("MERCA2"));
                        mercadologico.setDescricao(rst.getString("DESC_MERCA2"));
                    } else if (nivel == 3) {
                        mercadologico.setMercadologico1(rst.getInt("MERCA1"));
                        mercadologico.setMercadologico2(rst.getInt("MERCA2"));
                        mercadologico.setMercadologico3(rst.getInt("MERCA3"));
                        mercadologico.setDescricao(rst.getString("DESC_MERCA3"));
                    }
                    mercadologico.setNivel(nivel);
                    result.add(mercadologico);
                }
            }
        }
        return result;
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
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int cstSaida, cstEntrada, cstSaidaForaEstado;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select P.ESTOQUE_ID, P.CODIGO, P.DESCRICAO, P.DESCRICAO_REDUZ, U.DESCRICAO, \n"
                    + "T.TIPO_ID, T.DESCRICAO, P.INATIVO, P.CODIGO_NCM, P.CODIGO_CEST, \n"
                    + "P.CODIGO_BARRA, P.SETOR_ID, P.SECAO_ID, COALESCE(P.CATEGORIA_ID, 1) CATEGORIA_ID, \n"
                    + "F.SAI_CST_PIS, F.SAI_CST_COFINS, F.ENT_CST_PIS, F.ENT_CST_COFINS, \n"
                    + "F.ENT_CST_ICMS, F.SAI_CST_DENTRO_EST, F.SAI_CST_FORA_EST, \n"
                    + "F.SAI_ICMS_DENTRO_EST, F.SAI_ICMS_FORA_EST, F.SAI_BS_CALC_DENTRO_EST, \n"
                    + "F.SAI_BS_CALC_FORA_EST "
                    + "from ESTOQUE P \n"
                    + "inner join ESTOQUE_UNIDADES U on U.UNIDADE_ID = P.UNIDADE_ID_VENDA \n"
                    + "inner join ESTOQUE_TIPOS T on T.TIPO_ID = P.TIPO_ID \n"
                    + "inner join ESTOQUE_DADOS_FISCAIS F on F.ESTOQUE_ID = P.ESTOQUE_ID \n"
                    + "where F.EMPRESA_ID = " + idLojaCliente
            )) {
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();

                while (rst.next()) {

                    if ((rst.getString("SAI_CST_DENTRO_EST") != null)
                            && (!rst.getString("SAI_CST_DENTRO_EST").trim().isEmpty())) {
                        cstSaida = rst.getInt("SAI_CST_DENTRO_EST");
                    } else {
                        cstSaida = 90;
                    }

                    if ((rst.getString("SAI_CST_FORA_EST") != null)
                            && (!rst.getString("SAI_CST_FORA_EST").trim().isEmpty())) {
                        cstSaidaForaEstado = rst.getInt("SAI_CST_FORA_EST");
                    } else {
                        cstSaidaForaEstado = 90;
                    }

                    if ((rst.getString("ENT_CST_ICMS") != null)
                            && (!rst.getString("ENT_CST_ICMS").trim().isEmpty())) {
                        cstEntrada = rst.getInt("ENT_CST_ICMS");
                    } else {
                        cstEntrada = 90;
                    }

                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }
                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }
                    if (cstSaida > 99) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(1, 3));
                    }
                    if (cstEntrada > 99) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(1, 3));
                    }
                    if (cstSaidaForaEstado > 9) {
                        cstSaidaForaEstado = Integer.parseInt(String.valueOf(cstSaidaForaEstado).substring(0, 2));
                    }
                    if (cstSaidaForaEstado > 99) {
                        cstSaidaForaEstado = Integer.parseInt(String.valueOf(cstSaidaForaEstado).substring(1, 3));
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("ESTOQUE_ID"));
                    oProduto.setDescricaoCompleta(rst.getString("DESCRICAO").trim());
                    oProduto.setDescricaoReduzida(rst.getString("DESCRICAO_REDUZ").trim());
                    oProduto.setDescricaoGondola(rst.getString("DESCRICAO").trim());
                    oProduto.setIdSituacaoCadastro(rst.getInt("INATIVO") == 0 ? 1 : 0);
                    oProduto.setMercadologico1(rst.getInt("SETOR_ID"));
                    oProduto.setMercadologico2(rst.getInt("SECAO_ID"));
                    oProduto.setMercadologico3(rst.getInt("CATEGORIA_ID"));
                    oProduto.setIdTipoPisCofinsDebito(
                            Utils.retornarPisCofinsDebito2((rst.getString("SAI_CST_PIS") == null ? 1 : rst.getInt("SAI_CST_PIS"))));
                    oProduto.setIdTipoPisCofinsCredito(
                            Utils.retornarPisCofinsCredito2((rst.getString("ENT_CST_PIS") == null ? 13 : rst.getInt("ENT_CST_PIS"))));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    if ((rst.getString("CODIGO_NCM") != null)
                            && (!rst.getString("CODIGO_NCM").isEmpty())
                            && (rst.getString("CODIGO_NCM").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(Utils.formataNumero(rst.getString("CODIGO_NCM").trim()));
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("CODIGO_CEST") != null)
                            && (!rst.getString("CODIGO_CEST").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("CODIGO_CEST").trim()));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(0);
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);

                    long codigoProduto;

                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {

                        codigoProduto = Long.parseLong(Utils.formataNumero(
                                Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())));
                    } else {
                        codigoProduto = -2;
                    }
                    /**
                     * Aparentemente o sistema utiliza o próprio id para
                     * produtos de balança.
                     */
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
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
                        oProduto.setValidade(0);
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);

                        if ((rst.getString("CODIGO_BARRA") != null)
                                && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {

                            if (Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())) >= 1000000) {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())));
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }

                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem((rst.getString("DESCRICAO").trim().length() > 2 ? rst.getString("DESCRICAO").trim().substring(0, 2)
                                : rst.getString("DESCRICAO").trim())));
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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);
                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setIdLoja(idLojaVR);

                    oAliquota.idEstado = Global.idEstado;
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(Global.ufEstado, cstSaida, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(Global.ufEstado, cstEntrada, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, cstSaidaForaEstado, rst.getDouble("SAI_ICMS_FORA_EST"), (rst.getDouble("SAI_BS_CALC_FORA_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_FORA_EST"))));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, cstEntrada, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(Global.ufEstado, cstSaidaForaEstado, rst.getDouble("SAI_ICMS_FORA_EST"), (rst.getDouble("SAI_BS_CALC_FORA_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_FORA_EST"))));

                    oCodigoAnterior.setCodigoanterior(rst.getDouble("ESTOQUE_ID"));
                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    oCodigoAnterior.setNcm(rst.getString("CODIGO_NCM") == null ? ""
                            : Utils.formataNumero(rst.getString("CODIGO_NCM").trim()));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getString("SAI_CST_PIS") == null ? -1
                            : rst.getInt("SAI_CST_PIS"));
                    oCodigoAnterior.setPiscofinscredito(rst.getString("ENT_CST_PIS") == null ? -1
                            : rst.getInt("ENT_CST_PIS"));

                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();
        ProgressBar.setStatus("Carregando dados...Produtos...");
        List<ProdutoVO> vProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        List<LojaVO> vLoja = new LojaDAO().carregar();
        ProgressBar.setMaximum(vProdutos.size());
        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }

    public List<ProdutoVO> carregarListaDeProdutosIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int cstSaida, cstEntrada, cstSaidaForaEstado;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select P.ESTOQUE_ID, P.CODIGO, P.DESCRICAO, P.DESCRICAO_REDUZ, U.DESCRICAO, \n"
                    + "T.TIPO_ID, T.DESCRICAO, P.INATIVO, P.CODIGO_NCM, P.CODIGO_CEST, \n"
                    + "P.CODIGO_BARRA, P.SETOR_ID, P.SECAO_ID, COALESCE(P.CATEGORIA_ID, 1) CATEGORIA_ID, \n"
                    + "F.SAI_CST_PIS, F.SAI_CST_COFINS, F.ENT_CST_PIS, F.ENT_CST_COFINS, \n"
                    + "F.ENT_CST_ICMS, F.SAI_CST_DENTRO_EST, F.SAI_CST_FORA_EST, \n"
                    + "F.SAI_ICMS_DENTRO_EST, F.SAI_ICMS_FORA_EST, F.SAI_BS_CALC_DENTRO_EST, \n"
                    + "F.SAI_BS_CALC_FORA_EST "
                    + "from ESTOQUE P \n"
                    + "inner join ESTOQUE_UNIDADES U on U.UNIDADE_ID = P.UNIDADE_ID_VENDA \n"
                    + "inner join ESTOQUE_TIPOS T on T.TIPO_ID = P.TIPO_ID \n"
                    + "inner join ESTOQUE_DADOS_FISCAIS F on F.ESTOQUE_ID = P.ESTOQUE_ID \n"
                    + "where F.EMPRESA_ID = " + idLojaCliente + " "
                    + "and P.CODIGO_BARRA IS NOT NULL "
            )) {
                //Obtem os produtos de balança
                while (rst.next()) {

                    if ((rst.getString("SAI_CST_DENTRO_EST") != null)
                            && (!rst.getString("SAI_CST_DENTRO_EST").trim().isEmpty())) {
                        cstSaida = rst.getInt("SAI_CST_DENTRO_EST");
                    } else {
                        cstSaida = 90;
                    }

                    if ((rst.getString("SAI_CST_FORA_EST") != null)
                            && (!rst.getString("SAI_CST_FORA_EST").trim().isEmpty())) {
                        cstSaidaForaEstado = rst.getInt("SAI_CST_FORA_EST");
                    } else {
                        cstSaidaForaEstado = 90;
                    }

                    if ((rst.getString("ENT_CST_ICMS") != null)
                            && (!rst.getString("ENT_CST_ICMS").trim().isEmpty())) {
                        cstEntrada = rst.getInt("ENT_CST_ICMS");
                    } else {
                        cstEntrada = 90;
                    }

                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }
                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }
                    if (cstSaida > 99) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(1, 3));
                    }
                    if (cstEntrada > 99) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(1, 3));
                    }
                    if (cstSaidaForaEstado > 9) {
                        cstSaidaForaEstado = Integer.parseInt(String.valueOf(cstSaidaForaEstado).substring(0, 2));
                    }
                    if (cstSaidaForaEstado > 99) {
                        cstSaidaForaEstado = Integer.parseInt(String.valueOf(cstSaidaForaEstado).substring(1, 3));
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(rst.getInt("ESTOQUE_ID"));
                    oProduto.setDescricaoCompleta(rst.getString("DESCRICAO").trim());
                    oProduto.setDescricaoReduzida(rst.getString("DESCRICAO_REDUZ").trim());
                    oProduto.setDescricaoGondola(rst.getString("DESCRICAO").trim());
                    oProduto.setIdSituacaoCadastro(rst.getInt("INATIVO") == 0 ? 1 : 0);
                    oProduto.setMercadologico1(rst.getInt("SETOR_ID"));
                    oProduto.setMercadologico2(rst.getInt("SECAO_ID"));
                    oProduto.setMercadologico3(rst.getInt("CATEGORIA_ID"));
                    oProduto.setIdTipoPisCofinsDebito(
                            Utils.retornarPisCofinsDebito2((rst.getString("SAI_CST_PIS") == null ? 1 : rst.getInt("SAI_CST_PIS"))));
                    oProduto.setIdTipoPisCofinsCredito(
                            Utils.retornarPisCofinsCredito2((rst.getString("ENT_CST_PIS") == null ? 13 : rst.getInt("ENT_CST_PIS"))));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    if ((rst.getString("CODIGO_NCM") != null)
                            && (!rst.getString("CODIGO_NCM").isEmpty())
                            && (rst.getString("CODIGO_NCM").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(Utils.formataNumero(rst.getString("CODIGO_NCM").trim()));
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("CODIGO_CEST") != null)
                            && (!rst.getString("CODIGO_CEST").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("CODIGO_CEST").trim()));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(0);
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);

                    oProduto.setValidade(0);
                    oProduto.setPesavel(false);
                    oCodigoAnterior.setE_balanca(false);

                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {

                        if (Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())) >= 1000000) {
                            oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())));
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    } else {
                        oAutomacao.setCodigoBarras(-2);
                    }

                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem((rst.getString("DESCRICAO").trim().length() > 2 ? rst.getString("DESCRICAO").trim().substring(0, 2)
                            : rst.getString("DESCRICAO").trim())));
                    oCodigoAnterior.setCodigobalanca(0);
                    oCodigoAnterior.setE_balanca(false);

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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);
                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setIdLoja(idLojaVR);

                    oAliquota.idEstado = Global.idEstado;
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(Global.ufEstado, cstSaida, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(Global.ufEstado, cstEntrada, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, cstSaidaForaEstado, rst.getDouble("SAI_ICMS_FORA_EST"), (rst.getDouble("SAI_BS_CALC_FORA_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_FORA_EST"))));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, cstEntrada, rst.getDouble("SAI_ICMS_DENTRO_EST"), (rst.getDouble("SAI_BS_CALC_DENTRO_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_DENTRO_EST"))));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(Global.ufEstado, cstSaidaForaEstado, rst.getDouble("SAI_ICMS_FORA_EST"), (rst.getDouble("SAI_BS_CALC_FORA_EST") == 100 ? 0 : rst.getDouble("SAI_BS_CALC_FORA_EST"))));

                    oCodigoAnterior.setCodigoanterior(rst.getDouble("ESTOQUE_ID"));
                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("CODIGO_BARRA").trim())));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    oCodigoAnterior.setNcm(rst.getString("CODIGO_NCM") == null ? ""
                            : Utils.formataNumero(rst.getString("CODIGO_NCM").trim()));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getString("SAI_CST_PIS") == null ? -1
                            : rst.getInt("SAI_CST_PIS"));
                    oCodigoAnterior.setPiscofinscredito(rst.getString("ENT_CST_PIS") == null ? -1
                            : rst.getInt("ENT_CST_PIS"));

                    result.add(oProduto);
                }
            }
        }
        return result;
    }


    public void importarProdutoIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();
        ProgressBar.setStatus("Carregando dados...Produtos Integracao Loja "+idLojaVR);
        List<ProdutoVO> vProdutos = carregarListaDeProdutosIntegracao(idLojaVR, idLojaCliente);
        List<LojaVO> vLoja = new LojaDAO().carregar();
        ProgressBar.setMaximum(vProdutos.size());
        produto.implantacaoExterna = true;
        produto.salvarIntegracao(vProdutos, idLojaVR, vLoja);
    }
    
    public List<ProdutoVO> carregarListaDeCustoProduto(int idLojaVr, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        String strCodigoBarras;
        long barras;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select C.ESTOQUE_ID, C.CUSTO_BRUTO, P.CODIGO_BARRA\n"
                    + "from ESTOQUE_FORMACAO_PRECOS C\n"
                    + "inner join ESTOQUE P on P.ESTOQUE_ID = C.ESTOQUE_ID"
                    + "where C.FORMA_ID = (select MAX(FORMA_ID) \n"
                    + "   from ESTOQUE_FORMACAO_PRECOS\n"
                    + "   where ESTOQUE_ID = C.ESTOQUE_ID "
                    + "and EMPRESA_ID = " + idLojaCliente + ") "
                    + "and C.EMPRESA_ID = " + idLojaCliente
            )) {
                while (rst.next()) {
                    strCodigoBarras = "";
                    barras = -2;
                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {
                        strCodigoBarras = Utils.formataNumero(rst.getString("CODIGO_BARRA").trim());
                        if (strCodigoBarras.length() < 7) {
                            barras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                barras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                barras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    }
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(rst.getInt("ESTOQUE_ID"));
                    oProduto.setCodigoBarras(barras);
                    oComplemento.setCustoComImposto(rst.getDouble("CUSTO_BRUTO"));
                    oComplemento.setCustoSemImposto(rst.getDouble("CUSTO_BRUTO"));
                    oComplemento.setIdLoja(idLojaVr);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarListaCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Custo Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCustoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarCustoProduto(vProduto, idLojaVR);
        }
    }

    public void importarListaCustoProdutoIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Custo Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCustoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarCustoProdutoIntegracao(vProduto, idLojaVR);
        }
    }
    
    public List<ProdutoVO> carregarPrecoProdutos(int idLojaVr, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        String strCodigoBarras;
        long barras;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select PR.ESTOQUE_ID, PR.VALOR, P.CODIGO_BARRA \n"
                    + "from ESTOQUE_TABELA_PRECOS PR. \n"
                    + "inner join ESTOQUE P on P.ESTOQUE_ID = PR.ESTOQUE_ID"
                    + "where PR.EMPRESA_ID = " + idLojaCliente
            )) {

                while (rst.next()) {
                    strCodigoBarras = "";
                    barras = -2;
                    if ((rst.getString("CODIGO_BARRA") != null)
                            && (!rst.getString("CODIGO_BARRA").trim().isEmpty())) {
                        strCodigoBarras = Utils.formataNumero(rst.getString("CODIGO_BARRA").trim());
                        if (strCodigoBarras.length() < 7) {
                            barras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                barras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                barras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    }
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(rst.getInt("ESTOQUE_ID"));
                    oProduto.setCodigoBarras(barras);
                    oComplemento.setPrecoVenda(rst.getDouble("VALOR"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("VALOR"));
                    oComplemento.setIdLoja(idLojaVr);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarListaPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Preço Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarPrecoProdutos(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarPrecoProduto(vProduto, idLojaVR);
        }
    }

    public void importarListaPrecoProdutoIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Preço Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarPrecoProdutos(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarPrecoProdutoIntegracao(vProduto, idLojaVR);
        }
    }
    
    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select FORNECEDOR_ID, NOME_RAZAO, FANTASIA, TIPO, CNPJ, CPF, IE,\n"
                    + "ENDERECO, NUMERO, COMPLEMENTO, BAIRRO, CIDADE_ID, CEP, FONE, FAX,\n"
                    + "CELULAR, DATA_CADASTRO, EMAIL, ATIVO, PAIS_ID \n"
                    + "from FORNECEDORES\n"
                    + "order by NOME_RAZAO"
            )) {
                while (rst.next()) {
                    Date datacadastro;
                    datacadastro = new Date(new java.util.Date().getTime());
                    FornecedorVO oFornecedor = new FornecedorVO();
                    oFornecedor.setId(rst.getInt("FORNECEDOR_ID"));
                    oFornecedor.setCodigoanterior(oFornecedor.getId());
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setRazaosocial(rst.getString("NOME_RAZAO") == null ? "" : rst.getString("NOME_RAZAO").trim());
                    oFornecedor.setNomefantasia(rst.getString("FANTASIA") == null ? "" : rst.getString("FANTASIA").trim());
                    oFornecedor.setId_tipoinscricao("JURIDICA".equals(rst.getString("TIPO")) ? 0 : 1);
                    oFornecedor.setInscricaoestadual(rst.getString("IE") == null ? "" : rst.getString("IE"));

                    if (oFornecedor.getId_tipoinscricao() == 0) {
                        if ((rst.getString("CNPJ") != null)
                                && (!rst.getString("CNPJ").trim().isEmpty())) {
                            if (Utils.formataNumero(rst.getString("CNPJ").trim()).length() > 12) {
                                oFornecedor.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ").trim())));
                            } else {
                                oFornecedor.setCnpj(-1);
                            }
                        } else {
                            oFornecedor.setCnpj(-1);
                        }
                        //oFornecedor.setCnpj(rst.getLong("CNPJ") != 0 ? Long.parseLong(Utils.formataNumero(rst.getString("CNPJ"))) : 0);
                    } else {
                        if ((rst.getString("CNPJ") != null)
                                && (!rst.getString("CNPJ").trim().isEmpty())) {
                            if (Utils.formataNumero(rst.getString("CNPJ").trim()).length() <= 11) {
                                oFornecedor.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ").trim())));
                            } else {
                                oFornecedor.setCnpj(-1);
                            }
                        } else {
                            oFornecedor.setCnpj(-1);
                        }
                        //oFornecedor.setCnpj(rst.getLong("CPF") != 0 ? Long.parseLong(Utils.formataNumero(rst.getString("CNPJ"))) : 0);
                    }

                    oFornecedor.setEndereco(rst.getString("ENDERECO") == null ? "" : rst.getString("ENDERECO").trim());
                    oFornecedor.setNumero(rst.getString("NUMERO") == null ? "0" : rst.getString("NUMERO").trim());
                    oFornecedor.setComplemento(rst.getString("COMPLEMENTO") == null ? "" : rst.getString("COMPLEMENTO").trim());
                    oFornecedor.setBairro(rst.getString("BAIRRO") == null ? "" : rst.getString("BAIRRO").trim());

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").trim().isEmpty())) {

                        if (Utils.formataNumero(rst.getString("CEP").trim()).length() > 8) {
                            oFornecedor.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim().substring(0, 8))));
                        } else {
                            oFornecedor.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));
                        }
                    } else {
                        oFornecedor.setCep(Global.Cep);
                    }
                    //oFornecedor.setCep("".equals(rst.getString("CEP").trim()) ? Global.Cep : Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));

                    oFornecedor.setId_municipio(rst.getInt("CIDADE_ID") == 0 ? Global.idMunicipio
                            : Utils.retornarMunicipioIBGECodigo(rst.getInt("CIDADE_ID")));
                    oFornecedor.setId_estado(Integer.parseInt(String.valueOf(
                            oFornecedor.getId_municipio()).substring(0, 2)));
                    oFornecedor.setTelefone(rst.getString("FONE") == null ? "" : rst.getString("FONE").trim());
                    oFornecedor.setCelular(rst.getString("CELULAR") == null ? "" : rst.getString("CELULAR").trim());
                    oFornecedor.setFax(rst.getString("FAX") == null ? "" : rst.getString("FAX").trim());
                    oFornecedor.setEmail(rst.getString("EMAIL") == null ? "" : rst.getString("EMAIL").trim());
                    oFornecedor.setId_situacaocadastro(rst.getInt("ATIVO") == 1 ? 1 : 0);
                    result.add(oFornecedor);
                }
            }
        }
        return result;
    }

    @Override
    public void importarFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();
        new FornecedorDAO().salvarCnpj(vFornecedor);
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CLIENTE_ID, NOME_RAZAO, FANTASIA, TIPO, SEXO, EST_CIVIL, PROFISSAO,\n"
                    + "CNPJ, CPF, RG, IE, ENDERECO, NUMERO, COMPLEMENTO, BAIRRO, CIDADE_ID, FONE,\n"
                    + "FAX, CELULAR, DATA_CADASTRO, VR_LIMITE, EMAIL, ATIVO, CONJ_NOME, CONJ_RG,\n"
                    + "CONJ_CPF, NOME_PAI, NOME_MAE, PAIS_ID, CEP \n"
                    + "from CLIENTES"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    oClientePreferencial.setId(rst.getInt("CLIENTE_ID"));
                    oClientePreferencial.setCodigoanterior(rst.getLong("CLIENTE_ID"));
                    oClientePreferencial.setNome(rst.getString("NOME_RAZAO").trim());
                    oClientePreferencial.setId_tipoinscricao("JURIDICA".equals(rst.getString("TIPO")) ? 0 : 1);

                    if (oClientePreferencial.getId_tipoinscricao() == 0) {
                        oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ"))));
                        oClientePreferencial.setInscricaoestadual(rst.getString("IE") == null ? "" : rst.getString("IE").trim());
                    } else {
                        oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CPF"))));
                        oClientePreferencial.setInscricaoestadual(rst.getString("RG") == null ? "" : rst.getString("RG").trim());
                    }

                    oClientePreferencial.setSexo("Feminino".equals(rst.getString("SEXO")) ? 0 : 1);

                    if ((rst.getString("EST_CIVIL") != null)
                            && (!rst.getString("EST_CIVIL").trim().isEmpty())) {
                        if (rst.getString("EST_CIVIL").contains("Solte")) {
                            oClientePreferencial.setId_tipoestadocivil(1);
                        } else if (rst.getString("EST_CIVIL").contains("Casad")) {
                            oClientePreferencial.setId_tipoestadocivil(2);
                        } else if (rst.getString("EST_CIVIL").contains("Divor")) {
                            oClientePreferencial.setId_tipoestadocivil(6);
                        } else if (rst.getString("EST_CIVIL").contains("Viúv")) {
                            oClientePreferencial.setId_tipoestadocivil(3);
                        } else {
                            oClientePreferencial.setId_tipoestadocivil(0);
                        }
                    } else {
                        oClientePreferencial.setId_tipoestadocivil(0);
                    }

                    oClientePreferencial.setCargo(rst.getString("PROFISSAO") == null ? "" : rst.getString("PROFISSAO").trim());
                    oClientePreferencial.setEndereco(rst.getString("ENDERECO") == null ? "" : rst.getString("ENDERECO").trim());
                    oClientePreferencial.setNumero(rst.getString("NUMERO") == null ? "0" : rst.getString("NUMERO").trim());

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").trim().isEmpty())) {
                        if (Utils.formataNumero(rst.getString("CEP").trim()).length() > 8) {
                            oClientePreferencial.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim().substring(0, 8))));
                        } else {
                            oClientePreferencial.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));
                        }
                    } else {
                        oClientePreferencial.setCep(Global.Cep);
                    }
                    //oClientePreferencial.setCep("".equals(rst.getString("CEP")) ? Global.Cep : rst.getLong("CEP"));

                    oClientePreferencial.setComplemento(rst.getString("COMPLEMENTO") == null ? "" : rst.getString("COMPLEMENTO").trim());
                    oClientePreferencial.setBairro(rst.getString("BAIRRO") == null ? "" : rst.getString("BAIRRO").trim());
                    oClientePreferencial.setId_municipio(rst.getInt("CIDADE_ID") == 0 ? Global.idMunicipio
                            : Utils.retornarMunicipioIBGECodigo(rst.getInt("CIDADE_ID")));
                    oClientePreferencial.setId_estado(Integer.parseInt(String.valueOf(
                            oClientePreferencial.getId_municipio()).substring(0, 2)));
                    oClientePreferencial.setTelefone(rst.getString("FONE") == null ? "" : rst.getString("FONE").trim());
                    oClientePreferencial.setFax(rst.getString("FAX") == null ? "" : rst.getString("FAX").trim());
                    oClientePreferencial.setCelular(rst.getString("CELULAR") == null ? "" : rst.getString("CELULAR").trim());
                    oClientePreferencial.setEmail(rst.getString("EMAIL") == null ? "" : rst.getString("EMAIL").trim());
                    oClientePreferencial.setValorlimite(rst.getDouble("VR_LIMITE"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("ATIVO") == 1 ? 1 : 0);
                    oClientePreferencial.setNomeconjuge(rst.getString("CONJ_NOME") == null ? "" : rst.getString("CONJ_NOME").trim());
                    oClientePreferencial.setNomepai(rst.getString("NOME_PAI") == null ? "" : rst.getString("NOME_PAI").trim());
                    oClientePreferencial.setNomemae(rst.getString("NOME_MAE") == null ? "" : rst.getString("NOME_MAE").trim());
                    oClientePreferencial.setRgconjuge(rst.getString("CONJ_RG") == null ? "" : Utils.acertarTexto(rst.getString("CONJ_RG").trim()));

                    if ((rst.getString("CONJ_CPF") != null)
                            && (!rst.getString("CONJ_CPF").trim().isEmpty())) {
                        oClientePreferencial.setCpfconjuge(Double.parseDouble(Utils.formataNumero(rst.getString("CONJ_CPF").trim())));
                    } else {
                        oClientePreferencial.setCpfconjuge(0);
                    }
                    //oClientePreferencial.setCpfconjuge(Double.parseDouble(Utils.formataNumero((String) (rst.getString("CONJ_CPF") == null ? 0 : rst.getString("CONJ_CPF")))));

                    result.add(oClientePreferencial);
                }
            }
        }
        return result;
    }

    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
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
                    "select DEVEDOR_ID, DOCUMENTO, HISTORICO, DATA_EMISSAO, DATA_VENCIMENTO,\n"
                    + "VALOR_BRUTO, VALOR_PAGO, EMPRESA_ID, TIPO_DOC_ID, VENDA_ID, "
                    + "(VALOR_BRUTO - COALESCE(VALOR_PAGO, 0)) VALOR\n"
                    + "from CONTAS_RECEBER\n"
                    + "where TIPO_DOC_ID = 2\n"
                    + "and STATUS = 'Pendente'\n"
                    + "and MOD_ORIGEM = 'CLIENTES'\n"
                    + "and EMPRESA_ID = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                    if ((rst.getString("DOCUMENTO") != null)
                            && (!rst.getString("DOCUMENTO").trim().isEmpty())) {
                        if (!Utils.encontrouLetraCampoNumerico(rst.getString("DOCUMENTO").trim())) {
                            if (rst.getString("DOCUMENTO").trim().length() <= 9) {
                                oReceberCreditoRotativo.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("DOCUMENTO").trim())));
                            } else {
                                oReceberCreditoRotativo.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("DOCUMENTO").substring(0, 9))));
                            }
                        } else {
                            oReceberCreditoRotativo.setNumerocupom(0);
                        }
                    } else {
                        oReceberCreditoRotativo.setNumerocupom(0);
                    }

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("DEVEDOR_ID"));
                    oReceberCreditoRotativo.setDataemissao(rst.getString("DATA_EMISSAO").substring(0, 10).replace("-", "/"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getString("DATA_VENCIMENTO").substring(0, 10).replace("-", "/"));
                    oReceberCreditoRotativo.setValor(rst.getDouble("VALOR"));
                    oReceberCreditoRotativo.setObservacao(rst.getString("HISTORICO") == null ? "" : rst.getString("HISTORICO").trim());
                    oReceberCreditoRotativo.setId_loja(idLojaVR);
                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        return result;
    }

    @Override
    public void importarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(idLojaVR, idLojaCliente);
        new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCreditoRotativo, idLojaVR);
    }

    @Override
    public List<ReceberChequeVO> carregarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberChequeVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select DEVEDOR_ID, CLIENTES.NOME_RAZAO, CLIENTES.CNPJ, CLIENTES.CPF, \n"
                    + "CLIENTES.RG, CLIENTES.IE, CLIENTES.FONE, CLIENTES.TIPO,\n"
                    + " DOCUMENTO, HISTORICO, DATA_EMISSAO, DATA_VENCIMENTO,\n"
                    + "VALOR_BRUTO, VALOR_PAGO, EMPRESA_ID, TIPO_DOC_ID, VENDA_ID,\n"
                    + "(VALOR_BRUTO - COALESCE(VALOR_PAGO, 0)) VALOR,\n"
                    + "CHEQUE_EMITENTE, CHEQUE_BANCO, CHEQUE_AGENCIA, CHEQUE_CC, CHEQUE_NUMERO\n"
                    + "from CONTAS_RECEBER\n"
                    + "inner join CLIENTES on CLIENTE_ID = DEVEDOR_ID\n"
                    + "where TIPO_DOC_ID = 3\n"
                    + "and STATUS = 'Pendente'\n"
                    + "and EMPRESA_ID = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                    if ((rst.getString("DOCUMENTO") != null)
                            && (!rst.getString("DOCUMENTO").trim().isEmpty())) {
                        if (!Utils.encontrouLetraCampoNumerico(rst.getString("DOCUMENTO").trim())) {
                            if (rst.getString("DOCUMENTO").trim().length() <= 9) {
                                oReceberCheque.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("DOCUMENTO").trim())));
                            } else {
                                oReceberCheque.setNumerocupom(Integer.parseInt(Utils.formataNumero(rst.getString("DOCUMENTO").substring(0, 9))));
                            }
                        } else {
                            oReceberCheque.setNumerocupom(0);
                        }
                    } else {
                        oReceberCheque.setNumerocupom(0);
                    }
                    
                    oReceberCheque.setData(rst.getString("DATA_EMISSAO").substring(0, 10).replace("-", "/"));
                    oReceberCheque.setDatadeposito(rst.getString("DATA_VENCIMENTO").substring(0, 10).replace("-", "/"));
                    oReceberCheque.setValor(rst.getDouble("VALOR"));
                    oReceberCheque.setNome(rst.getString("NOME_RAZAO") == null ? "" : rst.getString("NOME_RAZAO").trim());

                    if ("JURIDICA".equals(rst.getString("TIPO"))) {
                        oReceberCheque.setCpf(rst.getString("CNPJ") == null ? 0 : Long.parseLong(Utils.formataNumero(rst.getString("CNPJ"))));
                        oReceberCheque.setRg(rst.getString("IE") == null ? "" : rst.getString("IE").trim());
                    } else {
                        oReceberCheque.setCpf(rst.getString("CPF") == null ? 0 : Long.parseLong(Utils.formataNumero(rst.getString("CPF"))));
                        oReceberCheque.setRg(rst.getString("RG") == null ? "" : rst.getString("RG").trim());
                    }

                    oReceberCheque.setTelefone(rst.getString("FONE") == null ? "" : Utils.formataNumero(rst.getString("FONE").trim()));
                    oReceberCheque.setObservacao(rst.getString("HISTORICO") == null ? "" : rst.getString("HISTORICO").trim());

                    result.add(oReceberCheque);
                }
            }
        }
        return result;
    }
    
    public void importarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Cheque...");
        List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(idLojaVR, idLojaCliente);
        new ReceberChequeDAO().salvar(vReceberCheque, idLojaVR);
    }
}
