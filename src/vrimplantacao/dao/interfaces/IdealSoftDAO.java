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
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

public class IdealSoftDAO {

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
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

    private Map<Integer, ProdutoVO> carregarProduto() throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        String ncmAtual;
        int ncm1, ncm2, ncm3, idCest, idPisCofins, cstIcmsSaida, cstIcmsEntrada;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.Ordem, p.Codigo, p.Nome, p.Nome_Nota, p.Peso_Liq, p.Peso_Bruto,\n"
                    + "p.Data_Cadastro, p.Inativo, p.Codigo_Barras, p.Dias_Validade,\n"
                    + "p.Ordem_NCM, p.Ordem_CEST, n.Codigo as ncm, c.Codigo as cest,\n"
                    + "m.Markup_Percentual, u.Nome Unidade, ie.Codigo Cod_Imposto_Entrada,\n"
                    + "ie.Nome Imposto_Entrada, i.Codigo Cod_Imposto_Saida, i.Nome Imposto_Saida,\n"
                    + "t.Perc_Estadual, SUBSTRING(i.Nome, 1, CHARINDEX('ICMS', i.Nome) -1) as ICMS_SAIDA,\n"
                    + "SUBSTRING(i.Nome, CHARINDEX('Pis', i.Nome), LEN(i.Nome)) PISCOFINS,\n"
                    + "cioS.ICMS_Base_Norm_Reduzida, cioS.ICMS_Valor_Base_Norm_Reduzida,\n"
                    + "cioS.ICMS_Percentual_Norm\n"
                    + "from dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on m.Ordem = p.Codigo\n"
                    + "left join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on ie.Ordem = p.Ordem_Classe_Imposto_Entrada and ie.Tipo_Operacao = 'E'\n"
                    + "left join dbo.Classe_Imposto i on i.Ordem = p.Ordem_Classe_Imposto_Saida and i.Tipo_Operacao =  'S'\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on cioS.Ordem_Classe_Imposto = i.Ordem \n"
                    + "and cioS.Estados = 'PA' /*and cioS.Ordem_CFOP_Prod_NF = 39*/ and cioS.Ordem_Operacao = 134\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on t.Ordem_Prod_Serv = p.Ordem and t.Estado = 'PA'\n"
                    + "where p.Codigo > 0"
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

                    oComplemento.setIdSituacaoCadastro((rst.getInt("Inativo") == 0 ? 1 : 0));
                    oProduto.setId(rst.getInt("Codigo"));
                    oProduto.setDescricaoCompleta((rst.getString("Nome") == null ? "" : rst.getString("Nome").trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdCest(idCest);
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
                    oProduto.setPesoLiquido(rst.getDouble("Peso_Liq"));
                    oProduto.setPesoBruto(rst.getDouble("Peso_Bruto"));

                    if ((rst.getString("PISCOFINS") != null)
                            && (!rst.getString("PISCOFINS").trim().isEmpty())) {

                        idPisCofins = Integer.parseInt(Utils.formataNumero(
                                rst.getString("PISCOFINS").trim().substring(0, rst.getString("PISCOFINS").trim().length() - 2)));
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(idPisCofins));
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(idPisCofins));
                        oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                        oProduto.setIdTipoPisCofinsCredito(13);
                        oProduto.setTipoNaturezaReceita(999);
                    }

                    if ((rst.getString("Data_Cadastro") != null)
                            && (!rst.getString("Data_Cadastro").trim().isEmpty())) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("Data_Cadastro")).substring(0, 10).replace("-", "/"));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    long codigoProduto;
                    codigoProduto = (long) oProduto.getId();
                    ProdutoBalancaVO produtoBalanca;

                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("Dias_Validade"));
                        oCodigoAnterior.setE_balanca(true);
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());

                        if ((rst.getString("Unidade") != null) && (!rst.getString("Unidade").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("Unidade").trim()));
                        } else {
                            oProduto.setIdTipoEmbalagem(0);
                        }

                        if (oProduto.getIdTipoEmbalagem() == 4) {
                            oProduto.setPesavel(false);
                        } else {
                            oProduto.setPesavel(true);
                        }

                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());

                        /*if ("P".equals(produtoBalanca.getPesavel())) {
                         oAutomacao.setIdTipoEmbalagem(4);
                         oProduto.setPesavel(false);
                         } else {
                         oAutomacao.setIdTipoEmbalagem(0);
                         oProduto.setPesavel(true);
                         }*/
                    } else {
                        oProduto.setValidade(rst.getInt("Dias_Validade"));
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);

                        if ((rst.getString("Codigo_Barras") != null)
                                && (!rst.getString("Codigo_Barras").trim().isEmpty())
                                && (rst.getString("Codigo_Barras").trim().length() >= 7)) {
                            oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo_Barras").trim())));
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }

                        if ((rst.getString("Unidade") != null) && (!rst.getString("Unidade").trim().isEmpty())) {
                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("Unidade").trim()));
                        } else {
                            oProduto.setIdTipoEmbalagem(0);
                        }

                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());

                        oCodigoAnterior.setCodigobalanca(0);
                    }
                    oAutomacao.setQtdEmbalagem(1);

                    if ((rst.getString("ICMS_SAIDA") != null)
                            && (!rst.getString("ICMS_SAIDA").trim().isEmpty())) {

                        if (rst.getString("ICMS_SAIDA").contains("Isento")) {
                            cstIcmsSaida = 40;
                        } else if (rst.getString("ICMS_SAIDA").contains("Não Trib")) {
                            cstIcmsSaida = 41;
                        } else if (rst.getString("ICMS_SAIDA").contains("Subs")) {
                            cstIcmsSaida = 60;
                        } else if (rst.getString("ICMS_SAIDA").contains("Difer")) {
                            cstIcmsSaida = 51;
                        } else if (rst.getString("ICMS_SAIDA").contains("Red")) {
                            cstIcmsSaida = 20;
                        } else if (rst.getString("ICMS_SAIDA").contains("Trib")) {
                            cstIcmsSaida = 0;
                        } else {
                            cstIcmsSaida = 90;
                        }
                    } else {
                        cstIcmsSaida = 90;
                    }

                    if ((rst.getString("Imposto_Entrada") != null)
                            && (!rst.getString("Imposto_Entrada").trim().isEmpty())) {

                        if (rst.getString("Imposto_Entrada").contains("Isento")) {
                            cstIcmsEntrada = 40;
                        } else if (rst.getString("Imposto_Entrada").contains("Não Trib")) {
                            cstIcmsEntrada = 41;
                        } else if (rst.getString("Imposto_Entrada").contains("Subs")) {
                            cstIcmsEntrada = 60;
                        } else if (rst.getString("Imposto_Entrada").contains("Difer")) {
                            cstIcmsEntrada = 51;
                        } else if (rst.getString("Imposto_Entrada").contains("Red")) {
                            cstIcmsEntrada = 20;
                        } else if (rst.getString("Imposto_Entrada").contains("Trib")) {
                            cstIcmsEntrada = 0;
                        } else {
                            cstIcmsEntrada = 90;
                        }
                    } else {
                        cstIcmsEntrada = 90;
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsEntrada, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsEntrada, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), true));

                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    if ((rst.getString("Codigo_Barras") != null)
                            && (!rst.getString("Codigo_Barras").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo_Barras").trim())));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    oCodigoAnterior.setNcm((rst.getString("ncm") == null ? "" : rst.getString("ncm")));
                    oCodigoAnterior.setCest((rst.getString("cest") == null ? "" : rst.getString("cest")));

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
            Map<Integer, ProdutoVO> vProduto = carregarProduto();
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
            produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCustoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Ordem_Tabela_Preco, Preco "
                    + "from View_Prod_Serv_Precos "
                    + "where Ordem_Tabela_Preco = 4"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("Codigo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setCustoComImposto(rst.getDouble("Preco"));
                    oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    oAnterior.setCustocomimposto(oComplemento.getCustoComImposto());
                    oAnterior.setCustosemimposto(oComplemento.getCustoSemImposto());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                    ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLojaVR + "..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarCustoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLojaVR + "...");
            vResult = carregarCustoProduto(idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Ordem_Tabela_Preco, Preco "
                    + "from View_Prod_Serv_Precos "
                    + "where Ordem_Tabela_Preco = 1"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("Codigo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("Preco"));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                    ProgressBar.setStatus("Carregando dados...Preco Produto Loja " + idLojaVR + "..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarPrecoProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preco Produto Loja " + idLojaVR + "...");
            vResult = carregarPrecoProduto(idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select e.Ordem_Filial, e.Ordem_Prod_Serv, \n"
                     + "       e.Qtde_Estoque_Atual, e.Estoque_Minimo, e.Estoque_Ideal\n"
                     + "  from dbo.Estoque_Atual e\n"
                     + " where e.Ordem_Filial = " + idLojaCliente*/
                    /*"select distinct Filiais_Codigo, Prod_Serv_Codigo, Estoque_Atual_Qtde_Atual \n"
                    + "from View_Movimento_Contagem_Estoque_Tela_Relatorio\n"
                    + "where Filiais_Codigo = " + idLojaCliente*/
                    "select e.Ordem_Filial, e.Ordem_Prod_Serv, e.Qtde_Estoque_Atual, p.Codigo\n"
                    + "from Estoque_Atual e\n"
                    + "inner join Prod_Serv p on p.Ordem = e.Ordem_Prod_Serv\n"
                    + "where e.Ordem_Filial = " + idLojaCliente
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(rst.getInt("Codigo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setEstoque(rst.getDouble("Qtde_Estoque_Atual"));
                    oProduto.vComplemento.add(oComplemento);
                    oProduto.vCodigoAnterior.add(oAnterior);
                    vResult.add(oProduto);
                    ProgressBar.setStatus("Carregando dados Estoque Produto Loja " + idLojaVR + "..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarEstoqueProduto(int idLojaCliente, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados Estoque Produto Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueProduto(vResult, idLojaVR);
            }
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

    private List<ProdutoVO> carregarProdutoAtacado(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pr.Codigo, pr.Ordem_Tabela_Preco, pr.Preco, coalesce(ata.De, 5) De \n"
                    + "from View_Prod_Serv_Precos pr\n"
                    + "inner join Prod_Serv p on p.Codigo = pr.Codigo\n"
                    + "inner join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda and u.Nome <> 'KG'\n"
                    + "left join Prod_Serv_Precos_Faixa_Qtde ata on ata.Ordem_Prod_Serv = p.Codigo\n"
                    + "where Ordem_Tabela_Preco = 2 \n"
                    + "and pr.Preco > 0\n"
                    + "and coalesce(ata.De, 5) > 1"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoLojaVO oAutomacaoLoja = new ProdutoAutomacaoLojaVO();
                    oProduto.setId(rst.getInt("Codigo"));
                    oAutomacaoLoja.codigobarras = Utils.gerarEan13(rst.getLong("Codigo"), true);
                    oAutomacaoLoja.qtdEmbalagem = (int) rst.getDouble("De");
                    oAutomacaoLoja.precovenda = rst.getDouble("Preco");
                    oAutomacaoLoja.id_loja = idLojaVR;
                    oProduto.vAutomacaoLoja.add(oAutomacaoLoja);
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;
    }

    public void importarProdutoAtacado(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Atacado...");
            vResult = carregarProdutoAtacado(idLojaVR);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().addCodigoBarrasAtacado2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarMargemProduto() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Markup_Percentual, Codigo_Prod_Serv\n"
                    + "from View_Prod_Serv_Entrada_Custo_Venda\n"
                    + "where Ordem_Tabela_Preco = 1"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("Codigo_Prod_Serv"));
                    vo.setMargem(rst.getDouble("Markup_Percentual"));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarMargemProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Margem Produto...");
            vResult = carregarMargemProduto();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarMargemProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.Ordem, f.Ordem_Cidade, f.Ordem_Pais, f.Tipo, f.Fisica_Juridica, \n"
                    + "       f.Nome, f.Fantasia, f.Endereco, f.Numero, f.Complemento, f.Bairro, \n"
                    + "       f.Cidade, f.Estado, f.CEP, f.CPF, f.CNPJ, f.RG_IE, f.Inscricao_Estadual_PF,\n"
                    + "       f.Inscricao_Municipal, f.Fone_1, f.Fone_2, f.Fax, f.Bloqueado, f.Data_Cadastro,\n"
                    + "       f.Inativo, upper(c.Cidade) Municipio, c.UF, c.Cod_Ibge,\n"
                    + "       f.Endereco_Cob, f.Numero_Cob, f.Complemento_Cob, f.Bairro_Cob, f.Cidade_Cob, f.Estado_Cob,\n"
                    + "       upper(cob.Cidade) Municipio_Cob, cob.UF Estado_Cod2, cob.Cod_Ibge Cod_Ibge_Cob\n"
                    + "  from dbo.Cli_For f\n"
                    + "  left join Cidades c on c.Ordem = f.Ordem_Cidade\n"
                    + "  left join Cidades cob on cob.Ordem = f.Ordem_Cidade_Cob\n"
                    + " where f.Tipo = 'F'"
            )) {
                int contador = 1;
                while (rst.next()) {
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    FornecedorVO vo = new FornecedorVO();
                    vo.setCodigoanterior(rst.getInt("Ordem"));
                    vo.setRazaosocial((rst.getString("Nome") == null ? "" : rst.getString("Nome").trim()));
                    vo.setNomefantasia((rst.getString("Fantasia") == null ? "" : rst.getString("Fantasia").trim()));
                    vo.setEndereco((rst.getString("Endereco") == null ? "" : rst.getString("Endereco").trim()));
                    vo.setBairro((rst.getString("Bairro") == null ? "" : rst.getString("Bairro").trim()));
                    vo.setComplemento((rst.getString("Complemento") == null ? "" : rst.getString("Complemento").trim()));
                    vo.setId_tipoinscricao(("F".equals(rst.getString("Fisica_Juridica")) ? 1 : 0));
                    vo.setId_situacaocadastro((rst.getInt("Inativo") == 0 ? 1 : 0));
                    vo.setBloqueado((rst.getInt("Bloqueado") != 0));

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").trim().isEmpty())
                            && (rst.getString("CEP").trim().length() >= 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));
                    } else {
                        vo.setCep(Global.Cep);
                    }

                    if ((rst.getString("Numero") != null)
                            && (!rst.getString("Numero").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("Numero").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("Data_Cadastro") != null)
                            && (!rst.getString("Data_Cadastro").trim().isEmpty())) {
                        vo.setDatacadastroStr(rst.getString("Data_Cadastro").trim());
                    } else {
                        vo.setDatacadastroStr("");
                    }

                    if (vo.getId_tipoinscricao() == 0) {
                        if ((rst.getString("CNPJ") != null)
                                && (!rst.getString("CNPJ").trim().isEmpty())
                                && (rst.getString("CNPJ").trim().length() > 12)) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                        if ((rst.getString("Inscricao_Estadual_PF") != null)
                                && (!rst.getString("Inscricao_Estadual_PF").trim().isEmpty())) {
                            vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("Inscricao_Estadual_PF").trim()));
                        } else {
                            vo.setInscricaoestadual("ISENTO");
                        }
                    } else {
                        if ((rst.getString("CPF") != null)
                                && (!rst.getString("CPF").trim().isEmpty())
                                && (rst.getString("CPF").trim().length() > 12)) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CPF").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                        if ((rst.getString("RG_IE") != null)
                                && (!rst.getString("RG_IE").trim().isEmpty())) {
                            vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("RG_IE").trim()));
                        } else {
                            vo.setInscricaoestadual("ISENTO");
                        }
                    }

                    if ((rst.getString("Fone_1") != null)
                            && (!rst.getString("Fone_1").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("Fone_1").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("Fone_2") != null)
                            && (!rst.getString("Fone_2").trim().isEmpty())) {
                        vo.setTelefone2(Utils.formataNumero(rst.getString("Fone_2").trim()));
                    } else {
                        vo.setTelefone2("");
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        vo.setFax(Utils.formataNumero(rst.getString("Fax").trim()));
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("Cod_Ibge") != null)
                            && (!rst.getString("Cod_Ibge").trim().isEmpty())
                            && (rst.getString("Cod_Ibge").trim().length() >= 5)) {

                        vo.setId_municipio(Utils.retornarMunicipioIBGECodigo(rst.getInt("Cod_Ibge")));

                    } else if ((rst.getString("Municipio_Cob") != null)
                            && (!rst.getString("Municipio_Cob").trim().isEmpty())) {

                        if ((rst.getString("UF") != null)
                                && (!rst.getString("UF").trim().isEmpty())) {

                            vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio_Cob").trim()),
                                    rst.getString("UF").trim()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio_Cob").trim()),
                                                    rst.getString("UF").trim())));

                        } else {
                            vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio_Cob").trim()),
                                    uf.getSigla()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio_Cob").trim()),
                                                    uf.getSigla())));

                        }
                    } else {
                        vo.setId_municipio(Global.idMunicipio);
                    }

                    if ((rst.getString("UF") != null)
                            && (!rst.getString("UF").trim().isEmpty())) {
                        vo.setId_estado(Utils.retornarEstadoDescricao(rst.getString("UF").trim()));
                    } else {
                        vo.setId_estado(Global.idEstado);
                    }

                    vo.setEnderecocobranca((rst.getString("Endereco_Cob") == null ? "" : rst.getString("Endereco_Cob").trim()));
                    vo.setComplementocobranca((rst.getString("Complemento_Cob") == null ? "" : rst.getString("Complemento_Cob").trim()));
                    vo.setBairrocobranca((rst.getString("Bairro_Cob") == null ? "" : rst.getString("Bairro_Cob")));
                    if ((rst.getString("Numero_Cob") != null)
                            && (!rst.getString("Numero_Cob").trim().isEmpty())) {
                        vo.setNumerocobranca(Utils.acertarTexto(rst.getString("Numero_Cob").trim()));
                    } else {
                        vo.setNumerocobranca("0");
                    }

                    if ((rst.getString("Cod_Ibge_Cob") != null)
                            && (!rst.getString("Cod_Ibge_Cob").trim().isEmpty())
                            && (rst.getString("Cod_Ibge_Cob").trim().length() >= 5)) {

                        vo.setId_municipiocobranca(Utils.retornarMunicipioIBGECodigo(rst.getInt("Cod_Ibge_Cob")));

                    } else if ((rst.getString("Municipio") != null)
                            && (!rst.getString("Municipio").trim().isEmpty())) {

                        if ((rst.getString("Estado_Cod2") != null)
                                && (!rst.getString("Estado_Cod2").trim().isEmpty())) {

                            vo.setId_municipiocobranca((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                    rst.getString("Estado_Cod2").trim()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                                    rst.getString("Estado_Cod2").trim())));

                        } else {
                            vo.setId_municipiocobranca((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                    uf.getSigla()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                                    uf.getSigla())));

                        }
                    } else {
                        vo.setId_municipiocobranca(Global.idMunicipio);
                    }

                    if ((rst.getString("Estado_Cod2") != null)
                            && (!rst.getString("Estado_Cod2").trim().isEmpty())) {
                        vo.setId_estadocobranca(Utils.retornarEstadoDescricao(rst.getString("Estado_Cod2").trim()));
                    } else {
                        vo.setId_estadocobranca(Global.idEstado);
                    }

                    vResult.add(vo);
                    ProgressBar.setStatus("Carregando dados...Fornecedor..." + contador);
                    contador++;
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Ordem_Fornecedor1 as Fornecedor, Codigo as Produto, Codigo_Adicional1 as CodExterno\n"
                    + "from dbo.Prod_Serv\n"
                    + "where Ordem_Fornecedor1 > 0\n"
                    + "union all\n"
                    + "select Ordem_Fornecedor2 as Fornecedor, Codigo as Produto, Codigo_Adicional2 as CodExterno\n"
                    + "from dbo.Prod_Serv\n"
                    + "where Ordem_Fornecedor2 > 0\n"
                    + "union all\n"
                    + "select Ordem_Fornecedor3 as Fornecedor, Codigo as Produto, Codigo_Adicional3 as CodExterno\n"
                    + "from dbo.Prod_Serv\n"
                    + "where Ordem_Fornecedor3 > 0\n"
                    + "union all\n"
                    + "select Ordem_Fornecedor4 as Fornecedor, Codigo as Produto, Codigo_Adicional4 as CodExterno\n"
                    + "from dbo.Prod_Serv\n"
                    + "where Ordem_Fornecedor4 > 0\n"
                    + "union all\n"
                    + "select Ordem_Fornecedor5 as Fornecedor, Codigo as Produto, Codigo_Adicional5 as CodExterno\n"
                    + "from dbo.Prod_Serv\n"
                    + "where Ordem_Fornecedor5 > 0"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_fornecedor(rst.getInt("Fornecedor"));
                    vo.setId_produto(rst.getInt("Produto"));
                    vo.setCodigoexterno((rst.getString("CodExterno") == null ? "" : rst.getString("CodExterno")));
                    vo.setDataalteracao(new Date(new java.util.Date().getTime()));
                    vResult.add(vo);
                    ProgressBar.setStatus("Carregando dados...Produto Fornecedor..." + contador);
                    contador++;
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.Ordem, c.Ordem_Cidade, c.Ordem_Pais, c.Tipo, c.Fisica_Juridica, \n"
                    + "       c.Nome, c.Fantasia, c.Endereco, c.Numero, c.Complemento, c.Bairro, \n"
                    + "       c.Cidade, c.Estado, c.CEP, c.CPF, c.CNPJ, c.RG_IE, c.Inscricao_Estadual_PF,\n"
                    + "       c.Inscricao_Municipal, c.Fone_1, c.Fone_2, c.Fax, c.Bloqueado, c.Data_Cadastro,\n"
                    + "       c.Inativo, upper(m.Cidade) Municipio, m.UF, m.Cod_Ibge, c.Endereco_Cob, \n"
                    + "       c.Numero_Cob, c.Complemento_Cob, c.Bairro_Cob, c.Cidade_Cob, c.Estado_Cob,\n"
                    + "       c.Limite_Credito, upper(cob.Cidade) Municipio_Cob, cob.UF, cob.Cod_Ibge\n"
                    + "  from dbo.Cli_For c\n"
                    + "  left join Cidades m on m.Ordem = c.Ordem_Cidade\n"
                    + "  left join Cidades cob on cob.Ordem = c.Ordem_Cidade_Cob\n"
                    + " where c.Tipo = 'C'"
            )) {
                int contador = 1;
                while (rst.next()) {
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setCodigoanterior(rst.getInt("Ordem"));
                    vo.setId(rst.getInt("Ordem"));
                    vo.setNome((rst.getString("Nome") == null ? "" : rst.getString("Nome").trim()));
                    vo.setEndereco((rst.getString("Endereco") == null ? "" : rst.getString("Endereco").trim()));
                    vo.setBairro((rst.getString("Bairro") == null ? "" : rst.getString("Bairro").trim()));
                    vo.setComplemento((rst.getString("Complemento") == null ? "" : rst.getString("Complemento").trim()));
                    vo.setId_tipoinscricao(("F".equals(rst.getString("Fisica_Juridica")) ? 1 : 0));
                    vo.setId_situacaocadastro((rst.getInt("Inativo") == 0 ? 1 : 0));
                    vo.setBloqueado((rst.getInt("Bloqueado") != 0));

                    if ((rst.getString("Numero") != null)
                            && (!rst.getString("Numero").trim().isEmpty())) {
                        vo.setNumero(Utils.acertarTexto(rst.getString("Numero").trim()));
                    } else {
                        vo.setNumero("0");
                    }

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").trim().isEmpty())
                            && (rst.getString("CEP").trim().length() >= 8)) {
                        vo.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));
                    } else {
                        vo.setCep(Global.Cep);
                    }

                    if (vo.getId_tipoinscricao() == 0) {
                        if ((rst.getString("CNPJ") != null)
                                && (!rst.getString("CNPJ").trim().isEmpty())
                                && (rst.getString("CNPJ").trim().length() > 12)) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                        if ((rst.getString("Inscricao_Estadual_PF") != null)
                                && (!rst.getString("Inscricao_Estadual_PF").trim().isEmpty())) {
                            vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("Inscricao_Estadual_PF").trim()));
                        } else {
                            vo.setInscricaoestadual("ISENTO");
                        }
                    } else {
                        if ((rst.getString("CPF") != null)
                                && (!rst.getString("CPF").trim().isEmpty())
                                && (rst.getString("CPF").trim().length() > 12)) {
                            vo.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CPF").trim())));
                        } else {
                            vo.setCnpj(-1);
                        }

                        if ((rst.getString("RG_IE") != null)
                                && (!rst.getString("RG_IE").trim().isEmpty())) {
                            vo.setInscricaoestadual(Utils.acertarTexto(rst.getString("RG_IE").trim()));
                        } else {
                            vo.setInscricaoestadual("ISENTO");
                        }
                    }

                    if ((rst.getString("Cod_Ibge") != null)
                            && (!rst.getString("Cod_Ibge").trim().isEmpty())
                            && (rst.getString("Cod_Ibge").trim().length() >= 5)) {

                        vo.setId_municipio(Utils.retornarMunicipioIBGECodigo(rst.getInt("Cod_Ibge")));

                    } else if ((rst.getString("Municipio") != null)
                            && (!rst.getString("Municipio").trim().isEmpty())) {

                        if ((rst.getString("UF") != null)
                                && (!rst.getString("UF").trim().isEmpty())) {

                            vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                    rst.getString("UF").trim()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                                    rst.getString("UF").trim())));
                        } else {
                            vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                    uf.getSigla()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Municipio").trim()),
                                                    uf.getSigla())));
                        }
                    } else {
                        vo.setId_municipio(Global.idMunicipio);
                        //vo.setId_estado(Global.idEstado);
                    }

                    if ((rst.getString("UF") != null)
                            && (!rst.getString("UF").trim().isEmpty())) {
                        vo.setId_estado(Utils.retornarEstadoDescricao(rst.getString("UF").trim()));
                    } else {
                        vo.setId_estado(Global.idEstado);
                    }

                    if ((rst.getString("Fone_1") != null)
                            && (!rst.getString("Fone_1").trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(rst.getString("Fone_1").trim()));
                    } else {
                        vo.setTelefone("0000000000");
                    }

                    if ((rst.getString("Fone_2") != null)
                            && (!rst.getString("Fone_2").trim().isEmpty())) {
                        vo.setTelefone2(Utils.formataNumero(rst.getString("Fone_2").trim()));
                    } else {
                        vo.setTelefone2("");
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        vo.setFax(Utils.formataNumero(rst.getString("Fax").trim()));
                    } else {
                        vo.setFax("");
                    }

                    if ((rst.getString("Data_Cadastro") != null)
                            && (!rst.getString("Data_Cadastro").trim().isEmpty())) {
                        vo.setDatacadastro(rst.getString("Data_Cadastro").trim());
                    } else {
                        vo.setDatacadastro("");
                    }

                    vResult.add(vo);
                    ProgressBar.setStatus("Carregando dados...Clientes..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarClientePreferencial(int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");
            vCliente = carregarClientePreferencial();
            new PlanoDAO().salvar(idLojaVR);
            new ClientePreferencialDAO().salvar(vCliente, idLojaVR, idLojaVR);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.Codigo, c.Nome, r.Ordem, r.Tela_Origem, r.Pagar_Receber, r.Tipo_Conta, r.Situacao, r.Ordem_Filial,\n"
                    + "       r.Ordem_Caixa, r.Ordem_Cli_For, r.Nota, r.Fatura, r.Descricao, r.Data_Emissao,\n"
                    + "       r.Data_Vencimento, r.Data_Quitacao, r.Data_Baixa, r.Valor_Base, r.Valor_Total,\n"
                    + "       r.Valor_Juros, r.Valor_Desconto, r.Valor_Total_Calculado, r.Valor_Quitado, r.Valor_Final_Calculado\n"
                    + "  from dbo.Financeiro_Contas r\n"
                    + " inner join  dbo.Cli_For c on c.Ordem = r.Ordem_Cli_For and c.Tipo = 'C'\n"
                    + " where r.Situacao = 'A'\n"
                    + "   and r.Pagar_Receber = 'R'\n"
                    + "   and r.Ordem_Filial = " + idLojaCliente
                    + "   and r.Tipo_Conta = 'R'"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    vo.setNumerocupom(rst.getInt("Ordem"));
                    vo.setId_clientepreferencial(rst.getInt("Ordem_Cli_For"));
                    vo.setDataemissao(rst.getString("Data_Emissao").substring(0, 10));
                    vo.setDatavencimento(rst.getString("Data_Vencimento").substring(0, 10));
                    vo.setValor(rst.getDouble("Valor_Total_Calculado"));
                    vo.setObservacao((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }
    
    public void importarReceberCreditoRotativo(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Contas Receber...");
            vResult = carregarReceberCreditoRotativo(idLojaCliente);
            if (!vResult.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /* acertos */
    private List<ProdutoVO> carregarAcertoPisCofins() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idPisCofins;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.Ordem, p.Codigo, p.Nome, p.Nome_Nota, p.Peso_Liq, p.Peso_Bruto,\n"
                    + "p.Data_Cadastro, p.Inativo, p.Codigo_Barras, p.Dias_Validade,\n"
                    + "p.Ordem_NCM, p.Ordem_CEST, n.Codigo as ncm, c.Codigo as cest,\n"
                    + "m.Markup_Percentual, u.Nome Unidade, ie.Codigo Cod_Imposto_Entrada,\n"
                    + "ie.Nome Imposto_Entrada, i.Codigo Cod_Imposto_Saida, i.Nome Imposto_Saida,\n"
                    + "t.Perc_Estadual, SUBSTRING(i.Nome, 1, CHARINDEX('ICMS', i.Nome) -1) as ICMS_SAIDA,\n"
                    + "SUBSTRING(i.Nome, CHARINDEX('Pis', i.Nome), LEN(i.Nome)) PISCOFINS, ''\n"
                    + "from dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on m.Ordem = p.Codigo\n"
                    + "left join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on ie.Ordem = p.Ordem_Classe_Imposto_Entrada\n"
                    + "left join dbo.Classe_Imposto i on i.Ordem = p.Ordem_Classe_Imposto_Saida\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on t.Ordem_Prod_Serv = p.Ordem and t.Estado = 'PA' "
                    + "where p.Codigo > 0"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("Codigo"));
                    if ((rst.getString("PISCOFINS") != null)
                            && (!rst.getString("PISCOFINS").trim().isEmpty())) {

                        idPisCofins = Integer.parseInt(Utils.formataNumero(
                                rst.getString("PISCOFINS").trim().substring(rst.getString("PISCOFINS").trim().length() - 2)));

                        //JOptionPane.showMessageDialog(null, rst.getString("PISCOFINS").trim().substring(rst.getString("PISCOFINS").trim().length() -2));
                        vo.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(idPisCofins));
                        vo.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(idPisCofins));
                        vo.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(vo.getIdTipoPisCofins(), ""));
                    } else {
                        vo.setIdTipoPisCofinsDebito(1);
                        vo.setIdTipoPisCofinsCredito(13);
                        vo.setTipoNaturezaReceita(999);
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarAcertoPisCofins() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto Piscofins");
            vResult = carregarAcertoPisCofins();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPisCofinsProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarAcertoIcms() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int cstIcmsSaida, cstIcmsEntrada;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.Ordem, p.Codigo, p.Nome, p.Nome_Nota, p.Peso_Liq, p.Peso_Bruto,\n"
                    + "p.Data_Cadastro, p.Inativo, p.Codigo_Barras, p.Dias_Validade,\n"
                    + "p.Ordem_NCM, p.Ordem_CEST, n.Codigo as ncm, c.Codigo as cest,\n"
                    + "m.Markup_Percentual, u.Nome Unidade, ie.Codigo Cod_Imposto_Entrada,\n"
                    + "ie.Nome Imposto_Entrada, i.Codigo Cod_Imposto_Saida, i.Nome Imposto_Saida,\n"
                    + "t.Perc_Estadual, SUBSTRING(i.Nome, 1, CHARINDEX('ICMS', i.Nome) -1) as ICMS_SAIDA,\n"
                    + "SUBSTRING(i.Nome, CHARINDEX('Pis', i.Nome), LEN(i.Nome)) PISCOFINS,\n"
                    + "cioS.ICMS_Base_Norm_Reduzida, cioS.ICMS_Valor_Base_Norm_Reduzida,\n"
                    + "cioS.ICMS_Percentual_Norm\n"
                    + "from dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on m.Ordem = p.Codigo\n"
                    + "left join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on ie.Ordem = p.Ordem_Classe_Imposto_Entrada and ie.Tipo_Operacao = 'E'\n"
                    + "left join dbo.Classe_Imposto i on i.Ordem = p.Ordem_Classe_Imposto_Saida and i.Tipo_Operacao =  'S'\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on cioS.Ordem_Classe_Imposto = i.Ordem \n"
                    + "and cioS.Estados = 'PA' /*and cioS.Ordem_CFOP_Prod_NF = 39*/ and cioS.Ordem_Operacao = 134\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on t.Ordem_Prod_Serv = p.Ordem and t.Estado = 'PA'\n"
                    + "where p.Codigo > 0"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    oProduto.setId(rst.getInt("Codigo"));

                    if ((rst.getString("ICMS_SAIDA") != null)
                            && (!rst.getString("ICMS_SAIDA").trim().isEmpty())) {

                        if (rst.getString("ICMS_SAIDA").contains("Isento")) {
                            cstIcmsSaida = 40;
                        } else if (rst.getString("ICMS_SAIDA").contains("Não Trib")) {
                            cstIcmsSaida = 41;
                        } else if (rst.getString("ICMS_SAIDA").contains("Subs")) {
                            cstIcmsSaida = 60;
                        } else if (rst.getString("ICMS_SAIDA").contains("Difer")) {
                            cstIcmsSaida = 51;
                        } else if (rst.getString("ICMS_SAIDA").contains("Red")) {
                            cstIcmsSaida = 20;
                        } else if (rst.getString("ICMS_SAIDA").contains("Trib")) {
                            cstIcmsSaida = 0;
                        } else {
                            cstIcmsSaida = 90;
                        }
                    } else {
                        cstIcmsSaida = 90;
                    }

                    if ((rst.getString("Imposto_Entrada") != null)
                            && (!rst.getString("Imposto_Entrada").trim().isEmpty())) {

                       if (rst.getString("Imposto_Entrada").contains("Isento")) {
                            cstIcmsEntrada = 40;
                        } else if (rst.getString("Imposto_Entrada").contains("Não Trib")) {
                            cstIcmsEntrada = 41;
                        } else if (rst.getString("Imposto_Entrada").contains("Subs")) {
                            cstIcmsEntrada = 60;
                        } else if (rst.getString("Imposto_Entrada").contains("Difer")) {
                            cstIcmsEntrada = 51;
                        } else if (rst.getString("Imposto_Entrada").contains("Red")) {
                            cstIcmsEntrada = 20;
                        } else if (rst.getString("Imposto_Entrada").contains("Trib")) {
                            cstIcmsEntrada = 0;
                        } else {
                            cstIcmsEntrada = 90;
                        } 
                    } else {
                        cstIcmsEntrada = 90;
                    }

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsEntrada, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsEntrada, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstIcmsSaida, rst.getDouble("ICMS_Percentual_Norm"), rst.getDouble("ICMS_Valor_Base_Norm_Reduzida"), true));
                    oProduto.vAliquota.add(oAliquota);
                    vResult.add(oProduto);

                    ProgressBar.setStatus("Carregando dados...Acerto Icms..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    public void importarAcertoIcms() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto Icms...");
            vResult = carregarAcertoIcms();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarICMSProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private Map<Long, ProdutoVO> carregarCodigoBarras() throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.Ordem, p.Codigo, p.Nome, p.Nome_Nota, p.Peso_Liq, p.Peso_Bruto,\n"
                    + "p.Data_Cadastro, p.Inativo, p.Codigo_Barras, p.Dias_Validade,\n"
                    + "p.Ordem_NCM, p.Ordem_CEST, n.Codigo as ncm, c.Codigo as cest,\n"
                    + "m.Markup_Percentual, u.Nome Unidade, ie.Codigo Cod_Imposto_Entrada,\n"
                    + "ie.Nome Imposto_Entrada, i.Codigo Cod_Imposto_Saida, i.Nome Imposto_Saida,\n"
                    + "t.Perc_Estadual, SUBSTRING(i.Nome, 1, CHARINDEX('ICMS', i.Nome) -1) as ICMS_SAIDA,\n"
                    + "SUBSTRING(i.Nome, CHARINDEX('Pis', i.Nome), LEN(i.Nome)) PISCOFINS,\n"
                    + "cioS.ICMS_Base_Norm_Reduzida, cioS.ICMS_Valor_Base_Norm_Reduzida,\n"
                    + "cioS.ICMS_Percentual_Norm\n"
                    + "from dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on m.Ordem = p.Codigo\n"
                    + "left join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on ie.Ordem = p.Ordem_Classe_Imposto_Entrada and ie.Tipo_Operacao = 'E'\n"
                    + "left join dbo.Classe_Imposto i on i.Ordem = p.Ordem_Classe_Imposto_Saida and i.Tipo_Operacao =  'S'\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on cioS.Ordem_Classe_Imposto = i.Ordem \n"
                    + "and cioS.Estados = 'PA' /*and cioS.Ordem_CFOP_Prod_NF = 39*/ and cioS.Ordem_Operacao = 134\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on t.Ordem_Prod_Serv = p.Ordem and t.Estado = 'PA'\n"
                    + "where p.Codigo > 0"
            )) {
                while (rst.next()) {
                    if ((rst.getString("Codigo_Barras") != null)
                            && (!rst.getString("Codigo_Barras").trim().isEmpty())
                            && (rst.getString("Codigo_Barras").trim().length() >= 7)) {
                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oProduto.setId(rst.getInt("Codigo"));
                        oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo_Barras").trim())));
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.put(oAutomacao.getCodigoBarras(), oProduto);
                    }
                }
            }            
        }
        return vResult;
    }
    
    public void importarCodigoBarras(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarras();

            ProgressBar.setMaximum(vEstoqueProduto.size());
            for (Long keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = false;
            produto.verificarLoja = true;
            produto.id_loja = id_loja;
            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPisCofins() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int idPisCofins;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.Ordem, p.Codigo, p.Nome, p.Nome_Nota, p.Peso_Liq, p.Peso_Bruto,\n"
                    + "p.Data_Cadastro, p.Inativo, p.Codigo_Barras, p.Dias_Validade,\n"
                    + "p.Ordem_NCM, p.Ordem_CEST, n.Codigo as ncm, c.Codigo as cest,\n"
                    + "m.Markup_Percentual, u.Nome Unidade, ie.Codigo Cod_Imposto_Entrada,\n"
                    + "ie.Nome Imposto_Entrada, i.Codigo Cod_Imposto_Saida, i.Nome Imposto_Saida,\n"
                    + "t.Perc_Estadual, SUBSTRING(i.Nome, 1, CHARINDEX('ICMS', i.Nome) -1) as ICMS_SAIDA,\n"
                    + "SUBSTRING(i.Nome, CHARINDEX('Pis', i.Nome), LEN(i.Nome)) PISCOFINS,\n"
                    + "cioS.ICMS_Base_Norm_Reduzida, cioS.ICMS_Valor_Base_Norm_Reduzida,\n"
                    + "cioS.ICMS_Percentual_Norm\n"
                    + "from dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on m.Ordem = p.Codigo\n"
                    + "left join dbo.Unidades_Venda u on u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on ie.Ordem = p.Ordem_Classe_Imposto_Entrada and ie.Tipo_Operacao = 'E'\n"
                    + "left join dbo.Classe_Imposto i on i.Ordem = p.Ordem_Classe_Imposto_Saida and i.Tipo_Operacao =  'S'\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on cioS.Ordem_Classe_Imposto = i.Ordem \n"
                    + "and cioS.Estados = 'PA' /*and cioS.Ordem_CFOP_Prod_NF = 39*/ and cioS.Ordem_Operacao = 134\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on t.Ordem_Prod_Serv = p.Ordem and t.Estado = 'PA'\n"
                    + "where p.Codigo > 0"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setId(rst.getInt("Codigo"));
                    if ((rst.getString("PISCOFINS") != null)
                            && (!rst.getString("PISCOFINS").trim().isEmpty())) {

                        idPisCofins = Integer.parseInt(Utils.formataNumero(rst.getString("PISCOFINS").trim()));
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(idPisCofins));
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(idPisCofins));
                        oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                        oProduto.setIdTipoPisCofinsCredito(13);
                        oProduto.setTipoNaturezaReceita(999);
                    }
                    vResult.add(oProduto);
                }
            }
        }
        return vResult;                
    }
    
    public void importarPisCofins() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Piscofins..");
            vResult = carregarPisCofins();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPisCofinsProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private Map<Long, ProdutoVO> carregarCodigoBarrasAdicional() throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Codigo_Adicional1 as codigoBarras\n"
                    + "from Prod_Serv\n"
                    + "where Codigo_Adicional1 is not null\n"
                    + "union all\n"
                    + "select Codigo, Codigo_Adicional2 as codigoBarras\n"
                    + "from Prod_Serv\n"
                    + "where Codigo_Adicional1 is not null\n"
                    + "union all\n"
                    + "select Codigo, Codigo_Adicional3 as codigoBarras\n"
                    + "from Prod_Serv\n"
                    + "where Codigo_Adicional1 is not null\n"
                    + "union all\n"
                    + "select Codigo, Codigo_Adicional4 as codigoBarras\n"
                    + "from Prod_Serv\n"
                    + "where Codigo_Adicional1 is not null\n"
                    + "union all\n"
                    + "select Codigo, Codigo_Adicional5 as codigoBarras\n"
                    + "from Prod_Serv\n"
                    + "where Codigo_Adicional1 is not null"
            )) {
                while (rst.next()) {
                    if ((rst.getString("codigoBarras") != null) &&
                            (!rst.getString("codigoBarras").trim().isEmpty()) &&
                            (Long.parseLong(Utils.formataNumero(rst.getString("codigoBarras").trim())) > 999999)) {
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oProduto.setId(rst.getInt("Codigo"));
                        oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codigoBarras").trim())));
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.put(oAutomacao.getCodigoBarras(), oProduto);                        
                    }
                }
            }
        }
        return vResult;
    }
    
    public void importarCodigoBarrasAdicional(int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasAdicional();

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
}
