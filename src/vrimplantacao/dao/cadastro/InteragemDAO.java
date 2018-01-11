package vrimplantacao.dao.cadastro;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.Global;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.parametro.Parametros;

public class InteragemDAO {

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProduto(int idLojaVR) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        int cstIcms;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select tabpro.codpro, tabpro.descpro, tabpro.detalhe, tabpro.codbarun, tabpro.codbarcx, tabpro.unidade,\n"
                    + "tabpro.cst, tabpro.icms, tabpro.stprod, tabpro.prcustocom, tabpro.perclucro, tabpro.balanca, tabpro.clasfiscal,\n"
                    + "tabpro.pesobruto, tabpro.pesoliquido, tabproimp.piscst "
                    + "from tabpro "
                    + "left join tabproimp on tabproimp.codpro = tabpro.codpro "
            )) {
                //Obtem os produtos de balança
                int contador = 1;
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

                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(("A".equals(rst.getString("stprod")) ? 1 : 0));
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codpro"))));
                    oProduto.setDescricaoCompleta((rst.getString("descpro") == null ? "" : rst.getString("descpro").trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setMargem(rst.getDouble("perclucro"));
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliquido"));
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
                    oProduto.setValidade(0);
                    
                    if ((rst.getString("piscst") != null) &&
                            (!rst.getString("piscst").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(Integer.parseInt(Utils.formataNumero(rst.getString("piscst").trim()))));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                    }
                    
                    if ((rst.getString("piscst") != null) &&
                            (!rst.getString("piscst").trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(Integer.parseInt(Utils.formataNumero(rst.getString("piscst").trim()))));
                    } else {
                        oProduto.setIdTipoPisCofinsCredito(13);
                    }
                    
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    if ((rst.getString("clasfiscal") != null)
                            && (!rst.getString("clasfiscal").trim().isEmpty())
                            && (rst.getString("clasfiscal").trim().length() >= 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("clasfiscal").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ("S".equals(rst.getString("balanca").trim())) {
                        oCodigoAnterior.setE_balanca(true);
                        oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade").trim()));
                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        oAutomacao.setCodigoBarras((long) oProduto.getId());
                        oCodigoAnterior.setCodigobalanca((int) oProduto.getId());

                        if (oProduto.getIdTipoEmbalagem() == 0) {
                            oProduto.setPesavel(true);
                        } else {
                            oProduto.setPesavel(false);
                        }

                    } else {
                        oCodigoAnterior.setE_balanca(false);
                        oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade").trim()));
                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        if ((rst.getString("codbarun") != null)
                                && (!rst.getString("codbarun").trim().isEmpty())
                                && (rst.getString("codbarun").trim().length() >= 7)) {

                            if (rst.getString("codbarun").trim().length() > 14) {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarun").trim().substring(0, 14))));
                            } else {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarun").trim())));
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    }

                    if ((rst.getString("cst") != null)
                            && (!rst.getString("cst").trim().isEmpty())) {
                        cstIcms = Integer.parseInt(Utils.formataNumero(rst.getString("cst").trim()));
                    } else {
                        cstIcms = 90;
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstIcms, rst.getDouble("icms"), 0, true));

                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setCodAnterior(Utils.acertarTexto(rst.getString("codpro").trim()));
                    oCodigoAnterior.setNcm((rst.getString("clasfiscal") == null ? "" : rst.getString("clasfiscal").trim()));
                    oCodigoAnterior.setMargem(rst.getDouble("perclucro"));

                    if ((rst.getString("cst") != null)
                            && (!rst.getString("cst").trim().isEmpty())) {
                        oCodigoAnterior.setRef_icmsdebito(Utils.acertarTexto(rst.getString("cst").trim()));
                    } else {
                        oCodigoAnterior.setRef_icmsdebito("");
                    }

                    if ((rst.getString("codbarun") != null)
                            && (!rst.getString("codbarun").trim().isEmpty())) {

                        if (rst.getString("codbarun").trim().length() > 14) {
                            oCodigoAnterior.setBarras((long) Double.parseDouble(Utils.formataNumero(rst.getString("codbarun").trim().substring(0, 14))));
                        } else {
                            oCodigoAnterior.setBarras((long) Double.parseDouble(Utils.formataNumero(rst.getString("codbarun").trim())));
                        }
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    vResult.put((int) oProduto.getId(), oProduto);

                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(idLojaCliente);
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

            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, prcupro "
                    + "from TABPROFIL "
                    + "where codfil = " + idLojaCliente
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(new ProdutoDAO().getIdByCodigoAnterior(rst.getString("codpro").trim()));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoComImposto(rst.getDouble("prcupro"));
                    oComplemento.setCustoSemImposto(rst.getDouble("prcupro"));
                    oAnterior.setCustocomimposto(oComplemento.getCustoComImposto());
                    oAnterior.setCustosemimposto(oComplemento.getCustoComImposto());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);

                    ProgressBar.setStatus("Carregando dados...Custo Produto Loja..." + idLojaVR + " " + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja..." + idLojaVR);
            vResult = carregarCustoProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().importarCustoProdutoSemCodigoAnterior(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, prvapro "
                    + "from TABPROFIL "
                    + "where codfil = " + idLojaCliente + " "
                    + "and codpro not in ('75826', '86729', '87931')"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(new ProdutoDAO().getIdByCodigoAnterior(rst.getString("codpro").trim()));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("prvapro"));
                    
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);

                    ProgressBar.setStatus("Carregando dados...Preco Produto Loja..." + idLojaVR + " " + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preco Produto Loja..." + idLojaVR);
            vResult = carregarPrecoProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().importarPrecoProdutoSemCodigoAnterior(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, qtdpro "
                    + "from TABPROFIL "
                    + "where codfil = " + idLojaCliente + " "
                    + "and qtdpro <> 0"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(new ProdutoDAO().getIdByCodigoAnterior(rst.getString("codpro").trim()));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("qtdpro"));
                    oAnterior.setEstoque(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);

                    ProgressBar.setStatus("Carregando dados...Estoque Produto Loja..." + idLojaVR + " " + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto Loja..." + idLojaVR);
            vResult = carregarEstoqueProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().importarEstoqueProdutoSemCodigoAnterior(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarras() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, codbarcx\n"
                    + "from tabpro\n"
                    + "where coalesce (codbarcx, '') <> ''\n"
                    + "and char_length(codbarcx) > 6"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(rst.getString("codpro").trim())));
                    oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarcx").trim())));
                    oProduto.vAutomacao.add(oAutomacao);
                    vResult.add(oProduto);

                    ProgressBar.setStatus("Carregando dados...Codigo Barras..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarCodigoBarras() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Codigo Barras...");
            vResult = carregarCodigoBarras();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().addCodigoBarras(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        int idMunicipio, idEstado;
        String pontoReferencia, contato, observacao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfor, nomfor, fanfor, endfor, baifor, pontoref, cidade,\n"
                    + "uf, cep, fone1, fone2, fax,  email, contato, cnpj, inscest,\n"
                    + "tpfornec, situacao, obs, tppessoa, represent01, fonerepre01,\n"
                    + "represent02, fonerepre02, represent03, fonerepre03,\n"
                    + "represent04, fonerepre04\n"
                    + "from tabfor"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setCodigoanterior(rst.getInt("codfor"));
                    vo.setId(rst.getInt("codfor"));
                    vo.setRazaosocial((rst.getString("nomfor") == null ? "" : rst.getString("nomfor").trim()));
                    vo.setNomefantasia((rst.getString("fanfor") == null ? "" : rst.getString("fanfor").trim()));
                    vo.setEndereco((rst.getString("endfor") == null ? "" : rst.getString("endfor").trim()));
                    vo.setBairro((rst.getString("baifor") == null ? "" : rst.getString("baifor").trim()));

                    if ((rst.getString("tppessoa") != null)
                            && (!rst.getString("tppessoa").trim().isEmpty())) {
                        if ("J".equals(rst.getString("tppessoa").trim())) {
                            vo.setId_tipoinscricao(0);
                        } else {
                            vo.setId_tipoinscricao(1);
                        }
                    } else {
                        vo.setId_tipoinscricao(0);
                    }

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        if (rst.getString("cnpj").trim().length() >= 9) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cnpj").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }
                    } else {
                        vo.setCnpj(-1);
                    }

                    if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").trim().isEmpty())) {
                        vo.setInscricaoestadual(Utils.formataNumero(rst.getString("inscest").trim()));
                    } else {
                        vo.setInscricaoestadual("ISENTO");
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        idMunicipio = Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("cidade").trim()),
                                Utils.acertarTexto(rst.getString("uf")));
                        vo.setId_municipio((idMunicipio == 0 ? Global.idMunicipio : idMunicipio));
                    } else {
                        vo.setId_municipio(Global.idMunicipio);
                    }

                    if ((rst.getString("uf") != null)
                            && (!rst.getString("uf").trim().isEmpty())) {
                        idEstado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("uf").trim()));
                        vo.setId_estado((idEstado == 0 ? Global.idEstado : idEstado));
                    } else {
                        vo.setId_estado(Global.idEstado);
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())
                            && (rst.getString("cep").trim().length() == 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("cep").trim())));
                    } else {
                        vo.setCep(Global.Cep);
                    }

                    if ((rst.getString("pontoref") != null)
                            && (!rst.getString("pontoref").trim().isEmpty())) {
                        pontoReferencia = " PONTO REFERENCIA: " + Utils.acertarTexto(rst.getString("pontoref").trim());
                    } else {
                        pontoReferencia = "";
                    }

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        contato = " CONTATO: " + Utils.acertarTexto(rst.getString("contato").trim());
                    } else {
                        contato = "";
                    }

                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = " OBS: " + Utils.acertarTexto(rst.getString("obs").trim());
                    } else {
                        observacao = "";
                    }

                    vo.setObservacao(pontoReferencia + contato + observacao);

                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("fone1").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        vo.setTelefone2(Utils.formataNumero(rst.getString("fone2").trim()));
                    } else {
                        vo.setTelefone2("");
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
                        vo.setEmail(Utils.acertarTexto(rst.getString("email").trim().toLowerCase()));
                    } else {
                        vo.setEmail("");
                    }

                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarFornecedor(int idLoja) throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vResult = carregarFornecedor();
            if (!vResult.isEmpty()) {
                new FornecedorDAO().salvar(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        java.sql.Date dataAlteracao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfor, codpro, codigo \n"
                    + "from tabprofor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(Integer.parseInt(Utils.formataNumero(rst.getString("codpro"))));
                    vo.setId_fornecedor(rst.getInt("codfor"));
                    vo.setCodigoexterno((rst.getString("codigo") == null ? "" : rst.getString("codigo").trim()));
                    dataAlteracao = new Date(new java.util.Date().getTime());
                    vo.setDataalteracao(dataAlteracao);
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
    
    private List<ProdutoVO> carregarAcertarDescricao() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        String descricao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codpro, descpro "
                    + "from tabpro"
            )) {
                int contador = 1;                
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(new ProdutoDAO().getIdByCodigoAnterior(rst.getString("codpro").trim()));
                    
                    if ((rst.getString("descpro") != null) &&
                            (!rst.getString("descpro").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("descpro");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        descricao = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                        
                    } else {
                        descricao = "";
                    }
                    
                    vo.setDescricaoCompleta(descricao);
                    vo.setDescricaoReduzida(descricao);
                    vo.setDescricaoGondola(descricao);
                    vResult.add(vo);
                    ProgressBar.setStatus("Carregando dados...Acertar Descricao..."+contador);
                    contador ++;

                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarDescricao() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Descricao...");
            vResult = carregarAcertarDescricao();
            if (!vResult.isEmpty()) {
                //new ProdutoDAO().acertarDescricaoSemCodigoAnterior(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorVO> carregarDescricaoFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " select codfor, nomfor, fanfor "
                    + "from tabfor"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setId(rst.getInt("codfor"));
                    vo.setRazaosocial((rst.getString("nomfor") == null ? "" : rst.getString("nomfor").trim()));
                    vo.setNomefantasia((rst.getString("fanfor") == null ? "" : rst.getString("fanfor").trim()));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarAcertarDescricaoFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Descricao...");
            vResult = carregarDescricaoFornecedor();
            if (!vResult.isEmpty()) {
                new FornecedorDAO().alterarRazaoFantasia(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
