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
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CodigoBarrasAnteriorDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.CodigoBarrasAnteriorVO;
import vrimplantacao2.parametro.Parametros;

public class DGComDAO {

    private List<ProdutoVO> carregarIdFornecedorFabricante() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.pro_cod, pro_desc, p.pro_for_cgc, f.cli_cod, f.cli_nome\n"
                    + "from escpro p, vdccli f\n"
                    + "where f.cli_cgc = p.pro_for_cgc"
            )) {
                int contador = 1;
                while (rst.next()) {
                    idFornecedor = new FornecedorDAO().getIdByCodigoAnterior(rst.getLong("cli_cod"));
                    
                    if (idFornecedor != -1) {
                        ProdutoVO vo = new ProdutoVO();
                        vo.setId(rst.getInt("pro_cod"));
                        vo.setIdFornecedorFabricante(idFornecedor);
                        vResult.add(vo);
                    }
                    ProgressBar.setStatus("Carregando dados...Id Fornecedor Fabricante..."+contador);
                    contador++;
                }
            }            
        }
        return vResult;
    }
    
    public void importarIdFornecedorFabricante() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Id Fornecedor Fabricante...");
            result = carregarIdFornecedorFabricante();

            if (!result.isEmpty()) {
                new ProdutoDAO().alterarIdFabricanteProduto(result);
            }
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

    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> vResult = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.grp_cod codMerc1, m1.grp_desc descMerc1, \n"
                    + "       m2.sgr_cod codMerc2, m2.sgr_desc descMerc2,\n"
                    + "       1 as codMerc3, m2.sgr_desc descMerc3\n"
                    + "from escgrp m1\n"
                    + "inner join escsgr m2 on m2.sgr_grp_cod = m1.grp_cod\n"
                    + "order by m1.grp_cod, m2.sgr_cod"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();

                    if (nivel == 1) {
                        vo.setMercadologico1(rst.getInt("codMerc1"));
                        vo.setDescricao((rst.getString("descMerc1") == null ? "" : rst.getString("descMerc1").trim()));
                    } else if (nivel == 2) {
                        vo.setMercadologico1(rst.getInt("codMerc1"));
                        vo.setMercadologico2(rst.getInt("codMerc2"));
                        vo.setDescricao((rst.getString("descMerc2") == null ? "" : rst.getString("descMerc2").trim()));
                    } else if (nivel == 3) {
                        vo.setMercadologico1(rst.getInt("codMerc1"));
                        vo.setMercadologico2(rst.getInt("codMerc2"));
                        vo.setMercadologico3(rst.getInt("codMerc3"));
                        vo.setDescricao((rst.getString("descMerc2") == null ? "" : rst.getString("descMerc3").trim()));
                    }

                    vo.setNivel(nivel);
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try {
            result = carregarMercadologico(1);
            new MercadologicoDAO().salvar2(result, true);

            result = carregarMercadologico(2);
            new MercadologicoDAO().salvar2(result, false);

            result = carregarMercadologico(3);
            new MercadologicoDAO().salvar2(result, false);

            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Double, ProdutoVO> carregarProduto(int idLoja) throws Exception {
        Map<Double, ProdutoVO> vResult = new HashMap<>();
        int validade;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.pro_cod, p.pro_codbarant, p.pro_desc, p.pro_grp_cod, p.pro_sgr_cod,\n"
                    + "p.pro_unidvend, p.pro_situacao, p.pro_dtcad, p.pro_lucro, p.pro_lucro1, \n"
                    + "p.pro_codbaremb, p.pro_codbarvenda, p.pro_codfiscal, p.pro_desc1, p.pro_pesobruto, \n"
                    + "p.pro_pesoliq, e.est_cst_tribicms, e.est_icms, e.est_aliqicms, e.est_pro_percreduz,\n"
                    + "e.est_cst_SaidaPIS, e.est_cst_EntradaPIS, e.est_cst_saidaCOFINS, e.est_cst_entradaCOFINS\n"
                    + "pro_diasvalid, pro_cest\n"
                    + "from escpro p\n"
                    + "left join escest e on e.est_pro_cod = p.pro_cod\n"
                    + "where est_emp_cod = " + idLoja
                    + " order by pro_cod"
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

                    oProduto.setIdDouble(rst.getDouble("pro_cod"));
                    oProduto.setDescricaoCompleta((rst.getString("pro_desc") == null ? "" : rst.getString("pro_desc").trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setMercadologico1(rst.getInt("pro_grp_cod"));
                    oProduto.setMercadologico2(rst.getInt("pro_sgr_cod"));
                    oProduto.setMercadologico3(1);

                    if ((rst.getString("est_cst_SaidaPIS") != null)
                            && (!rst.getString("est_cst_SaidaPIS").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("est_cst_SaidaPIS").trim())));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                    }

                    if ((rst.getString("est_cst_EntradaPIS") != null)
                            && (!rst.getString("est_cst_EntradaPIS").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("est_cst_EntradaPIS").trim())));
                    } else {
                        oProduto.setIdTipoPisCofinsCredito(13);
                    }

                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    if ((rst.getString("pro_codfiscal") != null)
                            && (!rst.getString("pro_codfiscal").trim().isEmpty())
                            && (rst.getString("pro_codfiscal").trim().length() >= 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("pro_codfiscal").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("pro_cest") != null)
                            && (!rst.getString("pro_cest").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("pro_cest").trim()));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    oProduto.setMargem(rst.getDouble("pro_lucro"));
                    oProduto.setPesoBruto(rst.getDouble("pro_pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pro_pesoliq"));

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

                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.formataNumero(Utils.formataNumero(rst.getString("pro_cod").trim())));

                    if ((rst.getString("pro_diasvalid") != null)
                            && (!rst.getString("pro_diasvalid").isEmpty())) {
                        validade = Integer.parseInt(Utils.formataNumero(rst.getString("pro_diasvalid").trim()));
                    } else {
                        validade = 0;
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
                        oAutomacao.setCodigoBarras((long) oProduto.getIdDouble());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : validade);
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
                        oProduto.setValidade(validade);
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);

                        if ((rst.getString("pro_codbarvenda") != null)
                                && (!rst.getString("pro_codbarvenda").trim().isEmpty())) {
                            if (Long.parseLong(Utils.formataNumero(rst.getString("pro_codbarvenda").trim())) >= 1000000) {

                                if (rst.getString("pro_codbarvenda").trim().length() > 14) {
                                    oAutomacao.setCodigoBarras(Long.parseLong(rst.getString("pro_codbarvenda").trim().substring(0, 14)));
                                } else {
                                    oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("pro_codbarvenda").trim())));
                                }
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("pro_unidvend").trim()));

                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setIdSituacaoCadastro(1);

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("est_cst_tribicms"), rst.getDouble("est_icms"), rst.getDouble("est_pro_percreduz")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("est_cst_tribicms"), rst.getDouble("est_icms"), rst.getDouble("est_pro_percreduz")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("est_cst_tribicms"), rst.getDouble("est_icms"), rst.getDouble("est_pro_percreduz")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("est_cst_tribicms"), rst.getDouble("est_icms"), rst.getDouble("est_pro_percreduz")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("est_cst_tribicms"), rst.getDouble("est_icms"), rst.getDouble("est_pro_percreduz")));

                    oCodigoAnterior.setCodigoanterior(oProduto.getIdDouble());
                    oCodigoAnterior.setNcm(rst.getString("pro_codfiscal").trim());
                    oCodigoAnterior.setId_loja(idLoja);
                    oCodigoAnterior.setNcm(rst.getString("pro_codfiscal").trim());

                    if ((rst.getString("est_cst_SaidaPIS") != null)
                            && (!rst.getString("est_cst_SaidaPIS").trim().isEmpty())) {
                        oCodigoAnterior.setPiscofinsdebito(Integer.parseInt(Utils.formataNumero(rst.getString("est_cst_SaidaPIS").trim())));
                    } else {
                        oCodigoAnterior.setPiscofinsdebito(-1);
                    }

                    if ((rst.getString("est_cst_EntradaPIS") != null)
                            && (!rst.getString("est_cst_EntradaPIS").trim().isEmpty())) {
                        oCodigoAnterior.setPiscofinscredito(rst.getInt("est_cst_EntradaPIS"));
                    } else {
                        oCodigoAnterior.setPiscofinscredito(-1);
                    }
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("est_cst_tribicms").trim());

                    vResult.put(oProduto.getIdDouble(), oProduto);

                }
            }
        }
        return vResult;
    }

    private List<CodigoBarrasAnteriorVO> carregarCodigoBarrasAnterior(boolean tabelaProduto, int idLojaVr) throws Exception {
        List<CodigoBarrasAnteriorVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            if (tabelaProduto) {
                try (ResultSet rst = stm.executeQuery(
                        "select pro_cod, pro_codbarant "
                        + "from escpro "
                )) {
                    while (rst.next()) {

                        if ((rst.getString("pro_codbarant") != null)
                                && (!rst.getString("pro_codbarant").trim().isEmpty())) {

                            CodigoBarrasAnteriorVO vo = new CodigoBarrasAnteriorVO();
                            vo.setIdLoja(idLojaVr);
                            vo.setCodigoAnterior(rst.getDouble("pro_cod"));
                            vo.setCodigoAtual(new ProdutoDAO().getIdAnterior(rst.getLong("pro_cod")));

                            if (rst.getString("pro_codbarant").trim().length() > 14) {
                                vo.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("pro_codbarant").trim().substring(0, 14))));
                            } else {
                                vo.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("pro_codbarant").trim())));
                            }
                            vResult.add(vo);
                        }
                    }
                }
            } else {
                try (ResultSet rst = stm.executeQuery(
                        "select EAN_PRO_COD, EAN_COD "
                        + "from ESCEAN "
                        + "order by EAN_PRO_COD"
                )) {
                    while (rst.next()) {
                        if ((rst.getString("EAN_COD") != null)
                                && (!rst.getString("EAN_COD").trim().isEmpty())) {

                            CodigoBarrasAnteriorVO vo = new CodigoBarrasAnteriorVO();
                            vo.setIdLoja(idLojaVr);
                            vo.setCodigoAnterior(rst.getDouble("EAN_PRO_COD"));
                            vo.setCodigoAtual(new ProdutoDAO().getIdAnterior(rst.getLong("EAN_PRO_COD")));

                            if (rst.getString("EAN_COD").trim().length() > 14) {
                                vo.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("EAN_COD").trim().substring(0, 14))));
                            } else {
                                vo.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("EAN_COD").trim())));
                            }
                            vResult.add(vo);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarProdutoManterBalanca(int idLojaVR, int idLojaCliente) throws Exception {

        List<CodigoBarrasAnteriorVO> vCodigoBarrasAnterior = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");

        vCodigoBarrasAnterior = carregarCodigoBarrasAnterior(true, idLojaVR);

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProduto(idLojaCliente).values()) {
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
        produto.usarMercadoligicoProduto = true;

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);

        if (!vCodigoBarrasAnterior.isEmpty()) {
            new CodigoBarrasAnteriorDAO().salvar(vCodigoBarrasAnterior, idLojaVR);
        }
    }

    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        List<CodigoBarrasAnteriorVO> vCodigoBarrasAnterior = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Double, ProdutoVO> vProduto = carregarProduto(idLojaCliente);
            vCodigoBarrasAnterior = carregarCodigoBarrasAnterior(true, idLojaVR);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            ProgressBar.setMaximum(vProduto.size());
            for (Double keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);

            if (!vCodigoBarrasAnterior.isEmpty()) {
                new CodigoBarrasAnteriorDAO().salvar(vCodigoBarrasAnterior, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select est_pro_cod, est_vlpreco \n"
                    + "from escest \n"
                    + "where est_emp_cod = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble(rst.getDouble("est_pro_cod"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("est_vlpreco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("est_vlpreco"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Loja " + idLojaVR + "...");
            result = carregarPrecoProduto(idLojaVR, idLojaCliente);

            if (!result.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(result, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select est_pro_cod, est_vlunitcomp, est_vlrepos\n"
                    + "from escest\n"
                    + "where est_emp_cod = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble(rst.getDouble("est_pro_cod"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoComImposto(rst.getDouble("est_vlunitcomp"));
                    oComplemento.setCustoSemImposto(rst.getDouble("est_vlrepos"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Loja " + idLojaVR + "...");
            result = carregarCustoProduto(idLojaVR, idLojaCliente);

            if (!result.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(result, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select est_pro_cod, est_qtddisp\n"
                    + "from escest\n"
                    + "where est_emp_cod = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble(rst.getDouble("est_pro_cod"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("est_qtddisp"));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente, boolean somar) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try {
            result = carregarEstoqueProduto(idLojaVR, idLojaCliente);

            if (somar) {
                ProgressBar.setStatus("Carregando dados...Soma Estoque Loja " + idLojaVR + "...");
            } else {
                ProgressBar.setStatus("Carregando dados...Estoque Loja " + idLojaVR + "...");
            }

            if (!result.isEmpty()) {
                if (somar) {
                    new ProdutoDAO().alterarEstoqueProdutoSomando(result, idLojaVR);
                } else {
                    new ProdutoDAO().alterarEstoqueProduto(result, idLojaVR);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarra() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        String strCodigoBarras;
        long codigoBarras;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select EAN_PRO_COD, EAN_COD "
                    + "from ESCEAN "
                    + "order by EAN_PRO_COD"
            )) {
                while (rst.next()) {
                    strCodigoBarras = "";
                    codigoBarras = -2;

                    if ((rst.getString("EAN_COD") != null)
                            && (!rst.getString("EAN_COD").trim().isEmpty())) {
                        strCodigoBarras = Utils.formataNumero(rst.getString("EAN_COD").trim());
                        if (strCodigoBarras.length() > 14) {
                            codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                        } else {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    }

                    if (codigoBarras != -2) {
                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                        oProduto.setIdDouble(rst.getDouble("EAN_PRO_COD"));
                        oAutomacao.setCodigoBarras(codigoBarras);
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.add(oProduto);
                    }
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarra(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        List<CodigoBarrasAnteriorVO> resultBarra = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Código de Barras...");
            result = carregarCodigoBarra();
            resultBarra = carregarCodigoBarrasAnterior(false, idLojaVR);

            if (!result.isEmpty()) {
                new ProdutoDAO().addCodigoBarras(result);
            }

            if (!resultBarra.isEmpty()) {
                new CodigoBarrasAnteriorDAO().salvar(resultBarra, idLojaVR);
            }
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

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        int idEstado, idMunicipio;
        String observacao;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cli_cod, cli_cgc, cli_nome, cli_fant, cli_end, cli_bairro,\n"
                    + "cli_cidade, cli_ufd_cod, cli_cep, cli_fone, cli_telex, cli_fax,\n"
                    + "cli_inscest, cli_contato, cli_vllimcred, cli_dtcad,\n"
                    + "cli_dtnasc, cli_fisjur, cli_sef_inscest, cli_inscestant, cli_cid_cod,\n"
                    + "cli_endnum, cli_endcomp, cli_observ \n"
                    + "from vdccli \n"
                    + "where cli_tipo = '002' \n"
                    + "order by cli_cod"
            )) {
                while (rst.next()) {
                    observacao = "";
                    FornecedorVO vo = new FornecedorVO();
                    vo.setCodigoanterior(rst.getInt("cli_cod"));
                    vo.setId(rst.getInt("cli_cod"));

                    if ((rst.getString("cli_cgc") != null)
                            && (!rst.getString("cli_cgc").trim().isEmpty())) {

                        if (rst.getString("cli_cgc").trim().length() > 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero (rst.getString("cli_cgc").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("cli_inscest") != null)
                            && (!rst.getString("cli_inscest").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("cli_inscest").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("cli_dtcad") != null)
                            && (!rst.getString("cli_dtcad").trim().isEmpty())) {
                        vo.setDatacadastroStr(rst.getString("cli_dtcad").substring(0, 10).replace("-", "/"));
                    } else {
                        vo.setDatacadastroStr("");
                    }

                    vo.setId_situacaocadastro(1);
                    vo.setRazaosocial((rst.getString("cli_nome") == null ? "" : rst.getString("cli_nome").trim()));
                    vo.setNomefantasia((rst.getString("cli_fant") == null ? "" : rst.getString("cli_fant").trim()));
                    vo.setEndereco((rst.getString("cli_end") == null ? "" : rst.getString("cli_end").trim()));
                    vo.setBairro((rst.getString("cli_bairro") == null ? "" : rst.getString("cli_bairro").trim()));
                    vo.setComplemento((rst.getString("cli_endcomp") == null ? "" : rst.getString("cli_endcomp").trim()));

                    if ((rst.getString("cli_fisjur") != null)
                            && (!rst.getString("cli_fisjur").trim().isEmpty())) {

                        if ("J".equals(rst.getString("cli_fisjur").trim())) {
                            vo.setId_tipoinscricao(0);
                        } else {
                            vo.setId_tipoinscricao(1);
                        }
                    } else {
                        vo.setId_tipoinscricao(0);
                    }

                    if ((rst.getString("cli_endnum") != null)
                            && (!rst.getString("cli_endnum").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("cli_endnum").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getInt("cli_cid_cod") > 0)) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(rst.getInt("cli_cid_cod")) == 0
                                ? Parametros.get().getMunicipioPadrao2().getId()
                                : rst.getInt("cli_cid_cod")));
                    } else if ((rst.getString("cli_cidade") != null)
                            && (!rst.getString("cli_cidade").trim().isEmpty())
                            && (rst.getString("cli_ufd_cod") != null)
                            && (!rst.getString("cli_ufd_cod").trim().isEmpty())) {

                        idMunicipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto("cli_cidade").trim(),
                                Utils.acertarTexto(rst.getString("cli_ufd_cod")));

                        idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("cli_ufd_cod")));

                        if (idMunicipio == 0) {
                            vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        } else {
                            vo.setId_municipio(idMunicipio);
                        }

                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((rst.getString("cli_ufd_cod") != null)
                            && (!rst.getString("cli_ufd_cod").trim().isEmpty())) {

                        idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("cli_ufd_cod")));
                        if (idEstado == 0) {
                            vo.setId_estado(Parametros.get().getUfPadrao().getId());
                        } else {
                            vo.setId_estado(idEstado);
                        }
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadrao().getId());
                    }

                    if ((rst.getString("cli_cep") != null)
                            && (!rst.getString("cli_cep").trim().isEmpty())
                            && (rst.getString("cli_cep").trim().length() >= 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cli_cep").trim())));
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    vo.setTelefone((rst.getString("cli_fone") == null ? "" : Utils.formataNumero(rst.getString("cli_fone").trim())));
                    vo.setTelefone2((rst.getString("cli_telex") == null ? "" : Utils.formataNumero(rst.getString("cli_telex").trim())));
                    vo.setFax((rst.getString("cli_fax") == null ? "" : Utils.formataNumero(rst.getString("cli_fax").trim())));

                    if ((rst.getString("cli_observ") != null)
                            && (!rst.getString("cli_observ").trim().isEmpty())) {
                        observacao = "OBS - " + Utils.acertarTexto(rst.getString("cli_observ").trim());
                    }

                    if ((rst.getString("cli_contato") != null)
                            && (!rst.getString("cli_contato").trim().isEmpty())) {

                        if (observacao.isEmpty()) {
                            vo.setObservacao("CONTATO - " + Utils.acertarTexto(rst.getString("cli_contato").trim()));
                        } else {
                            vo.setObservacao(observacao + " --- CONTATO - " + Utils.acertarTexto(rst.getString("cli_contato").trim()));
                        }
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            result = carregarFornecedor();

            if (!result.isEmpty()) {
                new FornecedorDAO().salvar(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        java.sql.Date dataAlteracao;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.cli_cod, f.cli_nome,\n"
                    + "pf.sim_pro_cod, pf.sim_cod, pf.sim_for_cgc\n"
                    + "from escsim pf\n"
                    + "inner join vdccli f on f.cli_cgc = pf.sim_for_cgc\n"
                    + "where cli_tipo = '002'"
            )) {
                while (rst.next()) {
                    if ((rst.getString("cli_cod") != null)
                            && (!rst.getString("cli_cod").trim().isEmpty())
                            && (rst.getString("sim_pro_cod") != null)
                            && (!rst.getString("sim_pro_cod").trim().isEmpty())) {

                        dataAlteracao = new Date(new java.util.Date().getTime());
                        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                        if ((rst.getString("sim_cod") != null)
                                && (!rst.getString("sim_cod").trim().isEmpty())) {
                            vo.setCodigoexterno(Utils.acertarTexto(rst.getString("sim_cod").trim()));
                        } else {
                            vo.setCodigoexterno("");
                        }

                        vo.setId_fornecedor(rst.getInt("cli_cod"));
                        vo.setId_produto(rst.getInt("sim_pro_cod"));
                        vo.setDataalteracao(dataAlteracao);
                        vResult.add(vo);
                    }
                }
            }
        }
        return vResult;
    }

    public void importarProdutoFornecedor(int idLoja) throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            result = carregarProdutoFornecedor();

            if (!result.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial() throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        int idEstado, idMunicipio;
        String observacao;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cli_cod, cli_cgc, cli_nome, cli_fant, cli_end, cli_bairro,\n"
                    + "cli_cidade, cli_ufd_cod, cli_cep, cli_fone, cli_telex, cli_fax,\n"
                    + "cli_inscest, cli_contato, cli_vllimcred, cli_dtcad,\n"
                    + "cli_dtnasc, cli_fisjur, cli_sef_inscest, cli_inscestant, cli_cid_cod,\n"
                    + "cli_endnum, cli_endcomp, cli_observ, cli_pai, cli_mae\n"
                    + "from vdccli \n"
                    + "where cli_tipo = '001' \n"
                    + "order by cli_cod"
            )) {
                while (rst.next()) {
                    observacao = "";
                    ClientePreferencialVO vo = new ClientePreferencialVO();

                    vo.setCodigoanterior(rst.getLong("cli_cod"));
                    vo.setId(rst.getInt("cli_cod"));

                    if ((rst.getString("cli_cgc") != null)
                            && (!rst.getString("cli_cgc").trim().isEmpty())) {

                        if (rst.getString("cli_cgc").trim().length() > 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cli_cgc").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("cli_inscest") != null)
                            && (!rst.getString("cli_inscest").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("cli_inscest").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("cli_dtcad") != null)
                            && (!rst.getString("cli_dtcad").trim().isEmpty())) {
                        vo.setDatacadastro(rst.getString("cli_dtcad").substring(0, 10).replace("-", "/"));
                    } else {
                        vo.setDatacadastro("");
                    }

                    vo.setId_situacaocadastro(1);
                    vo.setNome((rst.getString("cli_nome") == null ? "" : rst.getString("cli_nome").trim()));
                    vo.setEndereco((rst.getString("cli_end") == null ? "" : rst.getString("cli_end").trim()));
                    vo.setBairro((rst.getString("cli_bairro") == null ? "" : rst.getString("cli_bairro").trim()));
                    vo.setComplemento((rst.getString("cli_endcomp") == null ? "" : rst.getString("cli_endcomp").trim()));

                    if ((rst.getString("cli_fisjur") != null)
                            && (!rst.getString("cli_fisjur").trim().isEmpty())) {

                        if ("J".equals(rst.getString("cli_fisjur").trim())) {
                            vo.setId_tipoinscricao(0);
                        } else {
                            vo.setId_tipoinscricao(1);
                        }
                    } else {
                        vo.setId_tipoinscricao(0);
                    }

                    if ((rst.getString("cli_endnum") != null)
                            && (!rst.getString("cli_endnum").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("cli_endnum").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getInt("cli_cid_cod") > 0)) {
                        vo.setId_municipio((Utils.retornarMunicipioIBGECodigo(rst.getInt("cli_cid_cod")) == 0
                                ? Parametros.get().getMunicipioPadrao2().getId()
                                : rst.getInt("cli_cid_cod")));
                    } else if ((rst.getString("cli_cidade") != null)
                            && (!rst.getString("cli_cidade").trim().isEmpty())
                            && (rst.getString("cli_ufd_cod") != null)
                            && (!rst.getString("cli_ufd_cod").trim().isEmpty())) {

                        idMunicipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto("cli_cidade").trim(),
                                Utils.acertarTexto(rst.getString("cli_ufd_cod")));

                        idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("cli_ufd_cod")));

                        if (idMunicipio == 0) {
                            vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        } else {
                            vo.setId_municipio(idMunicipio);
                        }

                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((rst.getString("cli_ufd_cod") != null)
                            && (!rst.getString("cli_ufd_cod").trim().isEmpty())) {

                        idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("cli_ufd_cod")));
                        if (idEstado == 0) {
                            vo.setId_estado(Parametros.get().getUfPadrao().getId());
                        } else {
                            vo.setId_estado(idEstado);
                        }
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadrao().getId());
                    }

                    if ((rst.getString("cli_cep") != null)
                            && (!rst.getString("cli_cep").trim().isEmpty())
                            && (rst.getString("cli_cep").trim().length() >= 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cli_cep").trim())));
                    } else {
                        vo.setCep(Parametros.get().getCepPadrao());
                    }

                    vo.setTelefone((rst.getString("cli_fone") == null ? "" : Utils.formataNumero(rst.getString("cli_fone").trim())));
                    vo.setTelefone2((rst.getString("cli_telex") == null ? "" : Utils.formataNumero(rst.getString("cli_telex").trim())));
                    vo.setFax((rst.getString("cli_fax") == null ? "" : Utils.formataNumero(rst.getString("cli_fax").trim())));
                    vo.setNomepai((rst.getString("cli_pai") == null ? "" : rst.getString("cli_pai").trim()));
                    vo.setNomemae((rst.getString("cli_mae") == null ? "" : rst.getString("cli_mae").trim()));
                    vo.setValorlimite(rst.getDouble("cli_vllimcred"));

                    if ((rst.getString("cli_dtnasc") != null)
                            && (!rst.getString("cli_dtnasc").trim().isEmpty())) {
                        vo.setDatanascimento(rst.getString("cli_dtnasc").trim().substring(0, 10));
                    } else {
                        vo.setDatanascimento("");
                    }

                    if ((rst.getString("cli_observ") != null)
                            && (!rst.getString("cli_observ").trim().isEmpty())) {
                        observacao = "OBS - " + Utils.acertarTexto(rst.getString("cli_observ").trim());
                    }

                    if ((rst.getString("cli_contato") != null)
                            && (!rst.getString("cli_contato").trim().isEmpty())) {

                        if (observacao.isEmpty()) {
                            vo.setObservacao("CONTATO - " + Utils.acertarTexto(rst.getString("cli_contato").trim()));
                        } else {
                            vo.setObservacao(observacao + " --- CONTATO - " + Utils.acertarTexto(rst.getString("cli_contato").trim()));
                        }
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            result = carregarClientePreferencial();

            if (!result.isEmpty()) {
                new PlanoDAO().salvar(idLojaVR);
                new ClientePreferencialDAO().salvar(result, idLojaVR, idLojaCliente);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
