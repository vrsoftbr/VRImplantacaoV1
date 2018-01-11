package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.BancoDAO;
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

public class SicomDAO {

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT cod_grupopreco, grupo_preco\n"
                    + "  FROM get_produtos\n"
                    + " WHERE cod_grupopreco IS NOT NULL\n"
                    + " ORDER BY cod_grupopreco"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("cod_grupopreco"));
                    vo.setDescricao((rst.getString("grupo_preco")));
                    vo.setCodigoant(rst.getInt("cod_grupopreco"));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            vResult = carregarFamiliaProduto();
            if (!vResult.isEmpty()) {
                new FamiliaProdutoDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT coddpto, depto,\n"
                    + "       codgrupo, grupo,\n"
                    + "       subgrupo, codsubgrupo\n"
                    + "  FROM GET_PRODUTOS_ATUALIZACAO\n"
                    + "  where coddpto IS NOT null\n"
                    + " ORDER BY coddpto, codgrupo, subgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setNivel(nivel);
                    if (nivel == 1) {
                        vo.setMercadologico1(rst.getInt("coddpto"));
                        vo.setDescricao((rst.getString("depto") == null ? "" : rst.getString("depto").trim()));
                    } else if (nivel == 2) {
                        vo.setMercadologico1(rst.getInt("coddpto"));
                        vo.setMercadologico2(rst.getInt("codgrupo"));
                        vo.setDescricao((rst.getString("grupo") == null ? "" : rst.getString("grupo").trim()));
                    } else if (nivel == 3) {
                        vo.setMercadologico1(rst.getInt("coddpto"));
                        vo.setMercadologico2(rst.getInt("codgrupo"));
                        vo.setMercadologico3(rst.getInt("codsubgrupo"));
                        vo.setDescricao((rst.getString("subgrupo") == null ? "" : rst.getString("subgrupo").trim()));
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 1...");
            vResult = carregarMercadologico(1);
            new MercadologicoDAO().salvar(vResult, true);

            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            vResult = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vResult, false);

            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            vResult = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vResult, false);
            new MercadologicoDAO().salvarMax();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProduto(int idLojaVR, boolean importarMercadologicoCliente) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        int ncm1, ncm2, ncm3, idCest;
        String ncmAtual;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT p.idproduto, p.codgrupo, p.codsubgrupo, p.aliquota, p.codbarra, p.descricao,\n"
                    + "       p.unidade, p.balanca, p.coddpto, p.codbalanca, p.ativo, p.validade, p.peso, p.ncmsh ncm,\n"
                    + "       p.dtcadastro, p.cest, p.pesoliq_produto, p.pesobruto_produto, p.idpiscofins,\n"
                    + "       pc.cst_pis_ent, pc.cst_pis_sai, pc.cst_cofins_ent, pc.cst_cofins_sai, p.aliquota,\n"
                    + "       b.aliquota aliq, b.uf, b.cst, b.icm, b.base, b.icm_efetivo, m.markupfixo\n"
                    + "  FROM produtos p\n"
                    + "  left JOIN piscofins pc ON pc.idpiscofins = p.idpiscofins\n"
                    + "  left JOIN aliquota a ON a.aliquota = p.aliquota\n"
                    + " INNER JOIN det_aliquota b ON b.aliquota = a.aliquota\n"
                    + "  left JOIN multi_preco m ON m.idproduto = p.idproduto\n"
                    + " WHERE b.uf = 'GO'\n"
                    + "   AND p.idproduto > 0\n"
                    + " ORDER BY p.idproduto"
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
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
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    if ((rst.getString("cest") != null)
                            && (!rst.getString("cest").trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("cest").trim())));
                    } else {
                        idCest = -1;
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

                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(("S".equals(rst.getString("ativo").trim()) ? 1 : 0));

                    oProduto.setId(rst.getInt("idproduto"));
                    oProduto.setCodigoBarras(rst.getLong("codbarra"));
                    oProduto.setDescricaoCompleta((rst.getString("descricao") == null ? "" : rst.getString("descricao").trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setMargem(rst.getDouble("markupfixo"));
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdCest(idCest);

                    if (importarMercadologicoCliente) {
                        oProduto.setMercadologico1(rst.getInt("coddpto"));
                        oProduto.setMercadologico2(rst.getInt("codgrupo"));
                        oProduto.setMercadologico3(rst.getInt("codsubgrupo"));
                    } else {
                        oProduto.setMercadologico1(14);
                        oProduto.setMercadologico2(1);
                        oProduto.setMercadologico3(1);
                    }

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
                    oProduto.setPesoBruto(rst.getDouble("pesobruto_produto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliq_produto"));
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("cst_pis_sai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("cst_cofins_ent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    long codigoProduto;
                    codigoProduto = (long) oProduto.getCodigoBarras();
                    ProdutoBalancaVO produtoBalanca;

                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        oProduto.eBalanca = true;
                        oCodigoAnterior.setE_balanca(true);
                        oAutomacao.setCodigoBarras((long) oProduto.getCodigoBarras());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());

                        if ((rst.getString("unidade") != null) && (!rst.getString("unidade").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade").trim()));
                        } else {
                            oProduto.setIdTipoEmbalagem(0);
                        }

                        if (oProduto.getIdTipoEmbalagem() == 4) {
                            oProduto.setPesavel(false);
                        } else {
                            oProduto.setPesavel(true);
                        }

                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                    } else {
                        oProduto.eBalanca = false;
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);

                        if ((rst.getString("codbarra") != null)
                                && (!rst.getString("codbarra").trim().isEmpty())
                                && (rst.getString("codbarra").trim().length() >= 7)) {
                            oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim())));
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }

                        if ((rst.getString("unidade") != null) && (!rst.getString("unidade").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade").trim()));
                        } else {
                            oProduto.setIdTipoEmbalagem(0);
                        }

                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                        oCodigoAnterior.setCodigobalanca(0);
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("icm_efetivo"), rst.getDouble("base"), true));

                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("cst_pis_sai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("cst_cofins_ent"));

                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").trim().isEmpty())) {
                        oCodigoAnterior.setNcm(Utils.formataNumero(rst.getString("ncm").trim()));
                    } else {
                        oCodigoAnterior.setNcm("");
                    }

                    if ((rst.getString("cest") != null)
                            && (!rst.getString("cest").trim().isEmpty())) {
                        oCodigoAnterior.setCest(Utils.formataNumero(rst.getString("cest").trim()));
                    } else {
                        oCodigoAnterior.setCest("");
                    }

                    if ((rst.getString("codbarra") != null)
                            && (!rst.getString("codbarra").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(rst.getLong("codbarra"));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    if ((rst.getString("cst") != null)
                            && (!rst.getString("cst").trim().isEmpty())) {
                        if ("0".equals(rst.getString("cst").trim())) {
                            oCodigoAnterior.setRef_icmsdebito(rst.getString("icm_efetivo").trim());
                        } else {
                            oCodigoAnterior.setRef_icmsdebito(rst.getString("cst").trim());
                        }
                    } else {
                        oCodigoAnterior.setRef_icmsdebito("");
                    }

                    vResult.put((int) oProduto.getId(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarProduto(int idLojaVR, boolean importarMercadologicoCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(idLojaVR, importarMercadologicoCliente);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoManterBalanca(int idLojaVR, boolean importarMercadologicoCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProduto(idLojaVR, importarMercadologicoCliente).values()) {
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
        produto.usarCodigoBalancaComoID = true;
        produto.usarMercadoligicoProduto = false;

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }

    private List<ProdutoVO> carregarCustoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT idempresa, idproduto,\n"
                    + "       vrcustoreal custosemimposto, vrcusto custocomimposto\n"
                    + "  FROM multi_preco\n"
                    + " WHERE idempresa = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("idproduto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLojaVR + "...");
            vResult = carregarCustoProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT idproduto, idempresa, vrvenda\n"
                    + "  FROM get_estoque\n"
                    + " WHERE idempresa = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("idproduto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("vrvenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("vrvenda"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preco Produto Loja " + idLojaVR + "...");
            vResult = carregarPrecoProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT idproduto, idempresa, qtde\n"
                    + "  FROM get_estoque\n"
                    + " WHERE idempresa = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(rst.getInt("idproduto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("qtde"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Long, ProdutoVO> carregarCodigoBarrasProduto() throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT idproduto, codbarra, codauxiliar, fatoremb\n"
                    + "  FROM codauxiliar\n"
                    + " WHERE codauxiliar is not null "
                    + " ORDER BY idproduto"
            )) {
                while (rst.next()) {
                    if (rst.getLong("codauxiliar") > 999999) {
                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oProduto.setId(rst.getInt("idproduto"));
                        oAutomacao.setCodigoBarras(rst.getLong("codauxiliar"));
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.put(oAutomacao.getCodigoBarras(), oProduto);
                    }
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarraProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasProduto();
            ProgressBar.setMaximum(vEstoqueProduto.size());
            for (Long keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = false;
            produto.verificarLoja = true;
            produto.id_loja = idLoja;
            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }

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
            sql.append("select id, id_tipoembalagem, pesavel ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");
            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Double.parseDouble(rst.getString("id"));
                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999) || (rst.getBoolean("pesavel"))) {
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
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras Em Branco...");
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

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        long cnpj;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT f.codparceiro, f.razao, f.fantasia, f.tipofj, To_Char(f.dtcadastro,'dd/mm/yyyy') dtcadastro, f.dtnascimento, f.email,\n"
                    + "       f.credito, f.obs, f.ativado, f.bloqued, f.diasprazo, f.estado_civil, f.cargo, f.cli,\n"
                    + "       f.frn, f.senha, f.empresatrabalha, f.telefoneempresa, f.temposervico, f.cargoempresa,\n"
                    + "       f.renda, f.sexo, f.obs2, f.obs3, f.senha_autpdv, f.diretor_comercial, f.email_diretor_comercial,\n"
                    + "       f.fone_diretor_comercial, f.gerente_comercial, email_gerente_comercial,\n"
                    + "       f.fone_gerente_comercial, f.vendedor_representante, f.email_vendedor_representante,\n"
                    + "       f.responsavel_financeiro, f.email_responsavel_financeiro, f.fone_responsavel_financeiro,\n"
                    + "       f.responsavel_logistico, f.email_responsavel_logistico, f.fone_responsavel_logistico,\n"
                    + "       f.numero_contrato, e.endereco, e.bairro, e.cidade, e.uf, e.telefone, e.celular, e.fax,\n"
                    + "       e.cnpj_cpf, e.rg_insc, e.cep, e.idcidade, e.numero, e.complemento\n"
                    + "  FROM parceiros f\n"
                    + " INNER JOIN parceiros_end e ON e.codparceiro = f.codparceiro\n"
                    + " WHERE  frn = 'S'\n"
                    + " ORDER BY codparceiro"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setCodigoanterior(rst.getInt("codparceiro"));
                    vo.setId(rst.getInt("codparceiro"));
                    vo.setId_situacaocadastro(("S".equals(rst.getString("ativado")) ? 1 : 0));
                    vo.setId_tipoinscricao(("J".equals(rst.getString("tipofj")) ? 0 : 1));
                    vo.setBloqueado(("S".equals(rst.getString("bloqued"))));
                    vo.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs").trim()));
                    vo.setRazaosocial((rst.getString("razao") == null ? "" : rst.getString("razao").trim()));
                    vo.setNomefantasia((rst.getString("fantasia") == null ? "" : rst.getString("fantasia").trim()));
                    vo.setEndereco((rst.getString("endereco") == null ? "" : rst.getString("endereco").trim()));
                    vo.setBairro((rst.getString("bairro") == null ? "" : rst.getString("bairro").trim()));
                    vo.setNumero((rst.getString("numero") == null ? "" : rst.getString("numero").trim()));
                    vo.setComplemento((rst.getString("complemento") == null ? "" : rst.getString("complemento").trim()));

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cep").trim())));
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    if ((rst.getString("idcidade") != null)
                            && (!rst.getString("idcidade").trim().isEmpty())) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(rst.getInt("idcidade")) == 0
                                ? Parametros.get().getMunicipioPadrao2().getId()
                                : Utils.retornarMunicipioIBGECodigo(rst.getInt("idcidade"))));

                    } else if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                Utils.acertarTexto(rst.getString("uf").trim()))
                                == 0
                                        ? Parametros.get().getMunicipioPadrao2().getId()
                                        : Utils.retornarMunicipioIBGEDescricao(
                                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                                Utils.acertarTexto(rst.getString("uf").trim()))));
                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        vo.setId_estado(Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("uf").trim())));
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadraoV2().getId());
                    }

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("telefone").trim()));
                    } else {
                        vo.setTelefone("");
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        vo.setCelular(Utils.formataNumero(rst.getString("celular").trim()));
                    } else {
                        vo.setCelular("");
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        vo.setFax(Utils.formataNumero(rst.getString("fax").trim()));
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())
                            && (rst.getString("email").contains("@"))) {
                        vo.setEmail(Utils.acertarTexto(rst.getString("email").trim()).toLowerCase());
                    } else {
                        vo.setEmail("");
                    }

                    if ((rst.getString("cnpj_cpf") != null)
                            && (!rst.getString("cnpj_cpf").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj_cpf").trim()));
                    } else {
                        cnpj = -1;
                    }

                    if (cnpj > 99999999) {
                        vo.setCnpj(cnpj);
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("rg_insc") != null)
                            && (!rst.getString("rg_insc").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("rg_insc").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    vo.setId_tipoindicadorie();

                    if ((rst.getString("dtcadastro") != null)
                            && (!rst.getString("dtcadastro").trim().isEmpty())) {
                        vo.setDatacadastroStr(rst.getString("dtcadastro").trim());
                    } else {
                        vo.setDatacadastroStr("");
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vResult = carregarFornecedor();
            if (!vResult.isEmpty()) {
                new FornecedorDAO().salvar(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT idproduto, codfor, codref \n"
                    + "  FROM CODREFERENCIA_FOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("idproduto"));
                    vo.setId_fornecedor(rst.getInt("codfor"));
                    vo.setCodigoexterno((rst.getString("codref") == null ? "" : rst.getString("codref").trim()));
                    vo.setDataalteracao(new Date(new java.util.Date().getTime()));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vResult = carregarProdutoFornecedor();
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial() throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        long cnpj;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT f.codparceiro, f.razao, f.fantasia, f.tipofj, To_Char(f.dtcadastro,'dd/mm/yyyy') dtcadastro, "
                    + "       To_Char(f.dtnascimento, 'dd/mm/yyyy') dtnascimento, f.email,\n"
                    + "       f.credito, f.obs, f.ativado, f.bloqued, f.diasprazo, f.estado_civil, f.cargo, f.cli,\n"
                    + "       f.frn, f.senha, f.empresatrabalha, f.telefoneempresa, f.temposervico, f.cargoempresa,\n"
                    + "       f.renda, f.sexo, f.obs2, f.obs3, f.senha_autpdv, f.diretor_comercial, f.email_diretor_comercial,\n"
                    + "       f.fone_diretor_comercial, f.gerente_comercial, email_gerente_comercial,\n"
                    + "       f.fone_gerente_comercial, f.vendedor_representante, f.email_vendedor_representante,\n"
                    + "       f.responsavel_financeiro, f.email_responsavel_financeiro, f.fone_responsavel_financeiro,\n"
                    + "       f.responsavel_logistico, f.email_responsavel_logistico, f.fone_responsavel_logistico,\n"
                    + "       f.numero_contrato, e.endereco, e.bairro, e.cidade, e.uf, e.telefone, e.celular, e.fax,\n"
                    + "       e.cnpj_cpf, e.rg_insc, e.cep, e.idcidade, e.numero, e.complemento\n"
                    + "  FROM parceiros f\n"
                    + " INNER JOIN parceiros_end e ON e.codparceiro = f.codparceiro\n"
                    + " WHERE  cli = 'S'\n"
                    + " ORDER BY codparceiro"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setCodigoanterior(rst.getInt("codparceiro"));
                    vo.setId(rst.getInt("codparceiro"));
                    vo.setId_situacaocadastro(("S".equals(rst.getString("ativado")) ? 1 : 0));
                    vo.setId_tipoinscricao(("J".equals(rst.getString("tipofj")) ? 0 : 1));
                    vo.setBloqueado(("S".equals(rst.getString("bloqued"))));
                    vo.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs").trim()));
                    vo.setNome((rst.getString("razao") == null ? "" : rst.getString("razao").trim()));
                    vo.setEndereco((rst.getString("endereco") == null ? "" : rst.getString("endereco").trim()));
                    vo.setBairro((rst.getString("bairro") == null ? "" : rst.getString("bairro").trim()));
                    vo.setNumero((rst.getString("numero") == null ? "" : rst.getString("numero").trim()));
                    vo.setComplemento((rst.getString("complemento") == null ? "" : rst.getString("complemento").trim()));

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cep").trim())));
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    if ((rst.getString("idcidade") != null)
                            && (!rst.getString("idcidade").trim().isEmpty())) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(rst.getInt("idcidade")) == 0
                                ? Parametros.get().getMunicipioPadrao2().getId()
                                : Utils.retornarMunicipioIBGECodigo(rst.getInt("idcidade"))));

                    } else if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                Utils.acertarTexto(rst.getString("uf").trim()))
                                == 0
                                        ? Parametros.get().getMunicipioPadrao2().getId()
                                        : Utils.retornarMunicipioIBGEDescricao(
                                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                                Utils.acertarTexto(rst.getString("uf").trim()))));
                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        vo.setId_estado(Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("uf").trim())));
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadraoV2().getId());
                    }

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("telefone").trim()));
                    } else {
                        vo.setTelefone("");
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        vo.setCelular(Utils.formataNumero(rst.getString("celular").trim()));
                    } else {
                        vo.setCelular("");
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        vo.setFax(Utils.formataNumero(rst.getString("fax").trim()));
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())
                            && (rst.getString("email").contains("@"))) {
                        vo.setEmail(Utils.acertarTexto(rst.getString("email").trim()).toLowerCase());
                    } else {
                        vo.setEmail("");
                    }

                    if ((rst.getString("cnpj_cpf") != null)
                            && (!rst.getString("cnpj_cpf").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj_cpf").trim()));
                    } else {
                        cnpj = -1;
                    }

                    if (cnpj > 99999999) {
                        vo.setCnpj(cnpj);
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("rg_insc") != null)
                            && (!rst.getString("rg_insc").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("rg_insc").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("dtcadastro") != null)
                            && (!rst.getString("dtcadastro").trim().isEmpty())) {
                        vo.setDatacadastro(rst.getString("dtcadastro").trim());
                    } else {
                        vo.setDatacadastro("");
                    }

                    vo.setValorlimite(rst.getDouble("credito"));
                    if ((rst.getString("dtnascimento") != null)
                            && (!rst.getString("dtnascimento").trim().isEmpty())) {
                        vo.setDatanascimento(rst.getString("dtnascimento").trim());
                    } else {
                        vo.setDatanascimento("");
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("sexo").trim())) {
                            vo.setSexo(1);
                        } else {
                            vo.setSexo(0);
                        }
                    } else {
                        vo.setSexo(1);
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarClientePreferencial(int idLojaCliente, int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vResult = carregarClientePreferencial();
            if (!vResult.isEmpty()) {
                new PlanoDAO().salvar(idLojaVR);
                new ClientePreferencialDAO().salvar(vResult, idLojaCliente, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT duplicata, nrocupom, obs, To_Char(dtvenda, 'yyyy/mm/dd') dtvenda, valor,\n"
                    + "       To_Char(dtvenc, 'yyyy/mm/dd') dtvenc, codparceiro, parcela, codpdv \n"
                    + "  FROM areceber\n"
                    + " WHERE quitada = 'N'\n"
                    + "   AND codparceiro IS not null "
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_clientepreferencial(rst.getInt("codparceiro"));
                    vo.setValor(rst.getDouble("valor"));
                    vo.setEcf(rst.getInt("codpdv"));
                    vo.setNumerocupom(rst.getInt("nrocupom"));
                    vo.setParcela(rst.getInt("parcela"));
                    vo.setDataemissao(rst.getString("dtvenda"));
                    vo.setDatavencimento(rst.getString("dtvenc"));
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        vo.setObservacao(Utils.acertarTexto(rst.getString("obs").trim()));
                    } else {
                        vo.setObservacao("IMPORTADO VR");
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    private List<ReceberChequeVO> carregarReceberCheque(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        String numeroPedido, obs, obsDevolucao;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT ch.nrocheque, ch.valor, ch.valorpg, ch.titular, c.codparceiro, To_Char(ch.dtemissao, 'yyyy/mm/dd') dtemissao,\n"
                    + "       To_Char(ch.bompara, 'yyyy/mm/dd') bompara, ch.codpdv, ch.codbco, ch.nropedido,\n"
                    + "       e.telefone, e.cnpj_cpf, e.rg_insc, ch.observacao, ch.devolvido, ch.obsdevolucao, To_Char(ch.datadevolucao, 'yyyy/mm/dd') datadevolucao \n"
                    + "  FROM cheque ch\n"
                    + "  left JOIN parceiros c ON c.codparceiro = ch.codparceiro\n"
                    + " INNER JOIN parceiros_end e ON e.codparceiro = c.codparceiro\n"
                    + " WHERE ch.baixado = 'N'\n"
                    + "   AND ch.idempresa = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ReceberChequeVO vo = new ReceberChequeVO();
                    vo.setId_loja(idLojaVR);
                    vo.setValor(rst.getDouble("valor"));
                    vo.setNome((rst.getString("titular") == null ? "" : rst.getString("titular").trim()));
                    vo.setData(rst.getString("dtemissao"));
                    vo.setDatadeposito(rst.getString("bompara"));
                    vo.setEcf(rst.getInt("codpdv"));
                    vo.setId_banco((new BancoDAO().getId(rst.getInt("codbco"))));
                    vo.setNumerocheque(Integer.parseInt(Utils.formataNumero(rst.getString("nrocheque"))));
                    if ((rst.getString("nropedido") != null)
                            && (!rst.getString("nropedido").trim().isEmpty())) {
                        numeroPedido = "NRO PEDIDO - " + Utils.acertarTexto(rst.getString("nropedido") + "  - ");
                    } else {
                        numeroPedido = "";
                    }

                    if ((rst.getString("cnpj_cpf") != null)
                            && (!rst.getString("cnpj_cpf").trim().isEmpty())) {
                        vo.setCpf(Long.parseLong(Utils.formataNumero(rst.getString("cnpj_cpf").trim())));
                    } else {
                        vo.setCpf(Long.parseLong("0"));
                    }

                    if ((rst.getString("rg_insc") != null)
                            && (!rst.getString("rg_insc").trim().isEmpty())) {
                        vo.setRg(Utils.acertarTexto(rst.getString("rg_insc").trim()));
                    } else {
                        vo.setRg("");
                    }

                    if ((rst.getString("devolvido") != null)
                            && (!rst.getString("devolvido").trim().isEmpty())) {
                        vo.setId_tipoalinea(11);

                        if ((rst.getString("datadevolucao") != null)
                                && (!rst.getString("datadevolucao").trim().isEmpty())) {
                            vo.setDatadevolucao(rst.getString("datadevolucao"));
                        } else {
                            vo.setDatadevolucao("");
                        }
                        obsDevolucao = " - " + (rst.getString("obsdevolucao") == null ? "" : Utils.acertarTexto(rst.getString("obsdevolucao").trim())) + " ";
                    } else {
                        vo.setId_tipoalinea(0);
                        obsDevolucao = "";
                    }

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("telefone").trim()));
                    } else {
                        vo.setTelefone("");
                    }

                    obs = (rst.getString("observacao") == null ? "" : Utils.acertarTexto(rst.getString("observacao").trim()));

                    vo.setObservacao(numeroPedido + obs + obsDevolucao);

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarReceberCheque(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cheque Loja " + idLojaVR + "...");
            vResult = carregarReceberCheque(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ReceberChequeDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo Loja " + idLojaVR + "...");
            vResult = carregarReceberCreditoRotativo(idLojaVR);
            if (!vResult.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /* especiais */
    private List<ProdutoVO> carregarAcertoMercadologico(boolean importarMercadologicoCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT p.idproduto, p.codgrupo, p.codsubgrupo, p.aliquota, p.codbarra, p.descricao,\n"
                    + "       p.unidade, p.balanca, p.coddpto, p.codbalanca, p.ativo, p.validade, p.peso, p.ncmsh ncm,\n"
                    + "       p.dtcadastro, p.cest, p.pesoliq_produto, p.pesobruto_produto, p.idpiscofins,\n"
                    + "       pc.cst_pis_ent, pc.cst_pis_sai, pc.cst_cofins_ent, pc.cst_cofins_sai, p.aliquota,\n"
                    + "       b.aliquota aliq, b.uf, b.cst, b.icm, b.base, b.icm_efetivo, m.markupfixo\n"
                    + "  FROM produtos p\n"
                    + "  left JOIN piscofins pc ON pc.idpiscofins = p.idpiscofins\n"
                    + "  left JOIN aliquota a ON a.aliquota = p.aliquota\n"
                    + " INNER JOIN det_aliquota b ON b.aliquota = a.aliquota\n"
                    + "  left JOIN multi_preco m ON m.idproduto = p.idproduto\n"
                    + " WHERE b.uf = 'GO'\n"
                    + "   AND p.idproduto > 0\n"
                    + " ORDER BY p.idproduto"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("idproduto"));
                    if (importarMercadologicoCliente) {
                        vo.setMercadologico1(rst.getInt("coddpto"));
                        vo.setMercadologico2(rst.getInt("codgrupo"));
                        vo.setMercadologico3(rst.getInt("codsubgrupo"));
                    } else {
                        vo.setMercadologico1(14);
                        vo.setMercadologico2(1);
                        vo.setMercadologico3(1);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarAcertoMercadologico(boolean importarMercadologicoCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto Mercadologico Produto...");
            vResult = carregarAcertoMercadologico(importarMercadologicoCliente);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarMercadologicoProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarAcertoFamiliaProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo, cod_grupopreco\n"
                    + "  FROM get_produtos\n"
                    + " WHERE cod_grupopreco IS NOT null"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("codigo"));
                    vo.setIdFamiliaProduto(rst.getInt("cod_grupopreco"));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarAcertoFamiliaProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto Familia Produto...");
            vResult = carregarAcertoFamiliaProduto();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarFamiliaProduto_Produto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
