/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.utils.Utils;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao2.parametro.Parametros;

public class VRSoftware2DAO {

    Utils util = new Utils();

    public List<LojaVO> carregarLojaOrigem(ConexaoPostgres i_connOrigem) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        List<LojaVO> vLojaOrigem = new ArrayList<>();

        try {
            stm = i_connOrigem.createStatement();

            rst = stm.executeQuery("SELECT * FROM loja ORDER BY loja ASC");

            while (rst.next()) {
                LojaVO oLoja = new LojaVO();

                oLoja.id = rst.getInt("id");
                oLoja.descricao = rst.getString("descricao");

                vLojaOrigem.add(oLoja);
            }

            return vLojaOrigem;

        } catch (Exception e) {
            throw e;

        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }

    private Map<Long, ProdutoVO> carregarProdutoVrOrigemVrDestino(int i_idLojaOrigem, ConexaoPostgres i_connOrigem,
            boolean todosProdutos, int i_idLojaDestino) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        Map<Long, ProdutoVO> vProdutoOrigem = new HashMap<>();

        try {
            stm = i_connOrigem.createStatement();
            sql = new StringBuilder();
            sql.append("SELECT p.id as idProduto, pa.codigobarras, pc.*, p.*, pal.*, ");
            sql.append("       (select cst from tipopiscofins tp ");
            sql.append("         inner join produto p1 on p1.id_tipopiscofins = tp.id ");
            sql.append("           and p1.id = p.id) as cstSaida, ");
            sql.append("       (select cst from tipopiscofins tc ");
            sql.append("         inner join produto p1 on p1.id_tipopiscofinscredito = tc.id ");
            sql.append("           and p1.id = p.id) as cstEntrada, ");
            sql.append("       (select id_tipoentradasaida from tipopiscofins tp ");
            sql.append("         inner join produto p1 on p1.id_tipopiscofins = tp.id ");
            sql.append("           and p1.id = p.id) as saida, ");
            sql.append("       (select id_tipoentradasaida from tipopiscofins tc ");
            sql.append("         inner join produto p1 on p1.id_tipopiscofinscredito = tc.id ");
            sql.append("           and p1.id = p.id) as entrada, ");
            sql.append("       ad.situacaotributaria cst_saida, ad.porcentagem valor_saida, ad.reduzido reducao_saida, ");
            sql.append("       ac.situacaotributaria cst_entrada, ac.porcentagem valor_entrada, ac.reduzido reducao_entrada, ");
            sql.append("       adf.situacaotributaria cst_saidaFora, adf.porcentagem valor_saidaFora, adf.reduzido reducao_saidaFora, ");
            sql.append("       acf.situacaotributaria cst_entradaFora, acf.porcentagem valor_entradaFora, acf.reduzido reducao_entradaFora, ");
            sql.append("       adfNF.situacaotributaria cst_saidaForaNF, adfNF.porcentagem valor_saidaForaNF, adfNF.reduzido reducao_saidaForaNF ");
            sql.append("  FROM produtoautomacao pa ");
            sql.append(" INNER JOIN produtocomplemento pc ON pc.id_produto=pa.id_produto ");
            sql.append(" INNER JOIN produtoaliquota pal ON pal.id_produto=pa.id_produto ");
            sql.append(" INNER JOIN aliquota ad ON ad.id = pal.id_aliquotadebito ");
            sql.append(" INNER JOIN aliquota ac ON ac.id = pal.id_aliquotacredito ");
            sql.append(" INNER JOIN aliquota adf ON adf.id = pal.id_aliquotadebitoforaestado ");
            sql.append(" INNER JOIN aliquota acf ON acf.id = pal.id_aliquotacreditoforaestado ");
            sql.append(" INNER JOIN aliquota adfNF ON adfNF.id = pal.id_aliquotadebitoforaestadoNF ");
            sql.append(" INNER JOIN produto p ON p.id = pc.id_produto ");
            sql.append(" WHERE pc.id_loja = " + i_idLojaOrigem + " ");

            if (!todosProdutos) {
                sql.append("   AND p.id_tipoembalagem <> 4 ");
                sql.append("   AND p.pesavel = false ");
                sql.append("   AND LENGTH(pa.codigobarras::varchar) > 6");
            }

            rst = stm.executeQuery(sql.toString());
            int contator = 1;
            vrimplantacao2.dao.cadastro.produto.NcmDAO ncmDAO = new vrimplantacao2.dao.cadastro.produto.NcmDAO();
            while (rst.next()) {
                long codBarras = rst.getLong("codigobarras");
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = rst.getInt("idProduto");
                oProduto.codigoAnterior = rst.getDouble("idProduto");
                oProduto.aceitaMultiplicacaoPdv = rst.getBoolean("aceitamultiplicacaopdv");
                oProduto.alturaEmbalagem = rst.getInt("alturaembalagem");
                oProduto.comprimentoEmbalagem = rst.getInt("comprimentoembalagem");
                oProduto.custoFinal = rst.getDouble("custofinal");
                oProduto.dataAlteracao = rst.getString("dataalteracao") == null ? "" : Util.formatDataGUI(rst.getDate("dataalteracao"));
                oProduto.dataCadastro = Util.formatDataGUI(rst.getDate("datacadastro"));
                oProduto.descricaoCompleta = rst.getString("descricaocompleta");
                oProduto.descricaoCompletaAnterior = rst.getString("descricaocompleta");
                oProduto.descricaoGondola = rst.getString("descricaogondola");
                oProduto.descricaoReduzida = rst.getString("descricaoreduzida");
                oProduto.idDivisaoFornecedor = rst.getInt("id_divisaofornecedor");
                oProduto.excecao = rst.getString("excecao") == null ? -1 : rst.getInt("excecao");
                oProduto.fabricacaoPropria = rst.getBoolean("fabricacaopropria");
                oProduto.sugestaoCotacao = rst.getBoolean("sugestaocotacao");
                oProduto.sugestaoPedido = rst.getBoolean("sugestaopedido");
                oProduto.idComprador = 1;
                oProduto.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oProduto.idTipoProduto = rst.getInt("id_tipoproduto");
                oProduto.larguraEmbalagem = rst.getInt("larguraembalagem");
                oProduto.margem = rst.getDouble("margem");

                if ((rst.getString("ncm1") != null) && (rst.getString("ncm2") != null)
                        && (rst.getString("ncm3") != null)) {
                    vrimplantacao2.vo.enums.NcmVO oNcm = ncmDAO.getNcm(rst.getString("ncm1") + "" + rst.getString("ncm2") + "" + rst.getString("ncm3"));

                    if (oNcm == null) {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    } else {
                        oProduto.setNcm1(oNcm.getNcm1());
                        oProduto.setNcm2(oNcm.getNcm2());
                        oProduto.setNcm3(oNcm.getNcm3());
                    }

                } else {
                    oProduto.ncm1 = 402;
                    oProduto.ncm2 = 99;
                    oProduto.ncm3 = 0;
                }

                //oProduto.ncm1 = rst.getString("ncm1") == null ? -1 : rst.getInt("ncm1");
                //oProduto.ncm2 = rst.getString("ncm2") == null ? -1 : rst.getInt("ncm2");
                //oProduto.ncm3 = rst.getString("ncm3") == null ? -1 : rst.getInt("ncm3");
                oProduto.percentualEncargo = rst.getDouble("percentualencargo");
                oProduto.percentualFrete = rst.getDouble("percentualfrete");
                oProduto.percentualIpi = rst.getDouble("percentualipi");
                oProduto.percentualPerda = rst.getDouble("percentualperda");
                oProduto.percentualSubstituicao = rst.getDouble("percentualsubstituicao");
                oProduto.perda = rst.getDouble("perda");
                oProduto.pesoBruto = rst.getDouble("pesobruto");
                oProduto.pesoLiquido = rst.getDouble("pesoliquido");
                oProduto.tara = rst.getDouble("tara");
                oProduto.qtdEmbalagem = rst.getInt("qtdembalagem");
                oProduto.idTipoMercadoria = rst.getObject("id_tipomercadoria") == null ? -1 : rst.getInt("id_tipomercadoria");
                oProduto.validade = rst.getInt("validade");
                oProduto.qtdDiasMinimoValidade = rst.getInt("qtddiasminimovalidade");
                oProduto.verificaCustoTabela = rst.getBoolean("verificacustotabela");
                oProduto.pesavel = rst.getBoolean("pesavel");
                oProduto.sazonal = rst.getBoolean("sazonal");
                oProduto.consignado = rst.getBoolean("consignado");
                oProduto.ddv = rst.getInt("ddv");
                oProduto.permiteTroca = rst.getBoolean("permitetroca");
                oProduto.temperatura = rst.getInt("temperatura");
                oProduto.idTipoOrigemMercadoria = rst.getInt("id_tipoorigemmercadoria");
                oProduto.ipi = rst.getDouble("ipi");
                oProduto.vendaControlada = rst.getBoolean("vendacontrolada");
                oProduto.vendaPdv = rst.getBoolean("vendapdv");
                oProduto.conferido = rst.getBoolean("conferido");
                oProduto.permiteQuebra = rst.getBoolean("permitequebra");
                oProduto.permitePerda = rst.getBoolean("permiteperda");
                oProduto.impostoMedioNacional = rst.getDouble("impostomedionacional");
                oProduto.impostoMedioImportado = rst.getDouble("impostomedioimportado");
                oProduto.impostoMedioEstadual = rst.getDouble("impostomedioestadual");
                oProduto.utilizaTabelaSubstituicaoTributaria = rst.getBoolean("utilizatabelasubstituicaotributaria");
                oProduto.utilizaValidadeEntrada = rst.getBoolean("utilizavalidadeentrada");
                oProduto.idTipoLocalTroca = rst.getInt("id_tipolocaltroca");
                oProduto.idTipoCompra = rst.getInt("id_tipocompra");
                oProduto.codigoAnp = rst.getString("codigoanp");
                oProduto.numeroParcela = rst.getInt("numeroparcela");
                oProduto.idTipoPisCofinsDebito = Utils.retornarPisCofinsDebito(rst.getInt("cstSaida"));
                oProduto.idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(rst.getInt("cstEntrada"));
                oProduto.tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("tiponaturezareceita"));
                oProduto.idCest = (rst.getString("id_cest") == null ? -1 : rst.getInt("id_cest"));

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.emiteEtiqueta = rst.getBoolean("emiteetiqueta");
                oComplemento.estoqueMaximo = rst.getInt("estoquemaximo");
                oComplemento.estoqueMinimo = rst.getInt("estoqueminimo");
                //oComplemento.id = rst.getLong("id");
                oComplemento.idLoja = i_idLojaDestino;
                oComplemento.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
                oComplemento.prateleira = rst.getString("prateleira") == null ? "" : rst.getString("prateleira");
                oComplemento.secao = rst.getString("secao") == null ? "" : rst.getString("secao");
                oComplemento.teclaAssociada = rst.getInt("teclaassociada");
                oComplemento.descontinuado = rst.getBoolean("descontinuado");
                oComplemento.centralizado = rst.getBoolean("centralizado");
                oComplemento.operacional = rst.getDouble("operacional");
                oComplemento.valorIpi = rst.getDouble("valoripi");
                oComplemento.custoSemImposto = rst.getDouble("custosemimposto");
                oComplemento.custoComImposto = rst.getDouble("custocomimposto");
                oComplemento.custoSemImpostoAnterior = rst.getDouble("custosemimpostoanterior");
                oComplemento.custoMedioComImpostoAnterior = rst.getDouble("custocomimpostoanterior");
                oComplemento.precoVenda = rst.getDouble("precovenda");
                oComplemento.precoVendaAnterior = rst.getDouble("precovendaanterior");
                oComplemento.precoDiaSeguinte = rst.getDouble("precodiaseguinte");
                oComplemento.estoque = rst.getDouble("estoque");
                oComplemento.troca = rst.getDouble("troca");
                oComplemento.custoSemPerdaSemImposto = rst.getDouble("custosemperdasemimposto");
                oComplemento.custoSemPerdaSemImpostoAnterior = rst.getDouble("custosemperdasemimpostoanterior");
                oComplemento.custoMedioComImposto = rst.getDouble("customediocomimposto");
                oComplemento.custoMedioSemImposto = rst.getDouble("customediosemimposto");
                oComplemento.idAliquotaCredito = rst.getInt("id_aliquotacredito");
                oComplemento.dataUltimaVenda = rst.getString("dataultimavenda") == null ? "" : Util.formatDataGUI(rst.getDate("dataultimavenda"));
                oComplemento.dataUltimaEntradaAnterior = rst.getString("dataultimaentradaanterior") == null ? "" : Util.formatDataGUI(rst.getDate("dataultimaentradaanterior"));
                oComplemento.dataUltimoPreco = rst.getString("dataultimopreco") == null ? "" : Util.formatDataGUI(rst.getDate("dataultimopreco"));
                oComplemento.dataUltimaEntrada = rst.getString("dataultimaEntrada") == null ? "" : Util.formatDataGUI(rst.getDate("dataultimaEntrada"));
                oComplemento.idLoja = rst.getInt("id_loja");
                oComplemento.quantidadeUltimaEntrada = rst.getDouble("quantidadeultimaentrada");
                oComplemento.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
                oComplemento.cestaBasica = rst.getDouble("cestabasica");
                oComplemento.custoMedioComImpostoAnterior = rst.getDouble("customediocomimpostoanterior");
                oComplemento.custoMedioSemImpostoAnterior = rst.getDouble("customediosemimpostoanterior");
                oComplemento.idAliquotaCredito = Utils.getAliquotaIcms(rst.getInt("cst_entrada"), rst.getDouble("valor_entrada"), rst.getDouble("reducao_entrada"));
                oComplemento.idTipoPisCofinsCredito = oProduto.idTipoPisCofinsCredito;
                oComplemento.valorOutrasSubstituicao = rst.getDouble("valoroutrassubstituicao");
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();
                EstadoVO uf = Parametros.get().getUfPadrao();
                oProdutoAliquota.idEstado = uf.getId();
                oProdutoAliquota.idAliquotaDebito = Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst_saida"), rst.getDouble("valor_saida"), rst.getDouble("reducao_saida"));
                oProdutoAliquota.idAliquotaDebitoForaEstado = Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst_saidaFora"), rst.getDouble("valor_saidaFora"), rst.getDouble("reducao_saidaFora"));
                oProdutoAliquota.idAliquotaCredito = Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst_entrada"), rst.getDouble("valor_entrada"), rst.getDouble("reducao_entrada"));
                oProdutoAliquota.idAliquotaCreditoForaEstado = Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst_entradaFora"), rst.getDouble("valor_entradaFora"), rst.getDouble("reducao_entradaFora"));
                oProdutoAliquota.idAliquotaDebitoForaEstadoNF = Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst_saidaForaNF"), rst.getDouble("valor_saidaForaNF"), rst.getDouble("reducao_saidaForaNF"));
                oProduto.vAliquota.add(oProdutoAliquota);

                ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();
                oProdutoAutomacao.codigoBarras = codBarras;
                oProdutoAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oProdutoAutomacao.qtdEmbalagem = rst.getInt("qtdembalagem");
                oProdutoAutomacao.precoVenda = rst.getDouble("precovenda");
                oProduto.vAutomacao.add(oProdutoAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.codigoanterior = rst.getDouble("idProduto");
                oCodigoAnterior.setRef_icmsdebito(rst.getString("cst_saida"));
                oCodigoAnterior.piscofinsdebito = oProduto.idTipoPisCofinsDebito;
                oCodigoAnterior.piscofinscredito = oProduto.idTipoPisCofinsCredito;
                oCodigoAnterior.setNcm(rst.getString("ncm1") + "" + rst.getString("ncm2") + "" + rst.getString("ncm3"));
                oCodigoAnterior.barras = codBarras;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                vProdutoOrigem.put(codBarras, oProduto);

                ProgressBar.setStatus("Carregando dados para comparação..." + contator);
                contator++;
            }
            return vProdutoOrigem;
        } catch (Exception e) {
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }

    public void importarProdutoVrOrigemVrDestino(int i_idLojaOrigem, int i_idLojaDestino, ConexaoPostgres i_connOrigem,
            boolean todosProdutos) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        List<ProdutoVO> vProdutoAlterado = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Long, ProdutoVO> vProdutoOrigem = carregarProdutoVrOrigemVrDestino(i_idLojaOrigem, i_connOrigem,
                    todosProdutos, i_idLojaDestino);
            Map<Long, Long> vProdutoDestino = new ProdutoDAO().carregarCodigoBarras();
            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Loja Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoOrigem.size() + vProdutoDestino.size());

            for (Long keyCodigoBarra : vProdutoOrigem.keySet()) {
                if (vProdutoDestino.containsKey(keyCodigoBarra)) {
                    long codigoProduto = vProdutoDestino.get(keyCodigoBarra);
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    oProduto.id = (int) codigoProduto;
                    vProdutoAlterado.add(oProduto);
                } else {
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    oProduto.idProdutoVasilhame = -1;
                    oProduto.idFamiliaProduto = -1;
                    oProduto.idFornecedorFabricante = Global.idFornecedor;
                    oProduto.excecao = -1;
                    oProduto.idTipoMercadoria = -1;
                    vProdutoNovo.add(oProduto);
                }
                ProgressBar.next();
            }

            if (!vProdutoAlterado.isEmpty()) {
                new ProdutoDAO().salvar(vProdutoAlterado, i_idLojaDestino, true, null, true, 0);
            }

            if (!vProdutoNovo.isEmpty()) {
                ProdutoDAO prod = new ProdutoDAO();
                prod.usarCodigoAnterior = true;
                prod.verificarLoja = true;
                prod.usarMercadologicoAcertar = true;
                prod.salvarIntegracao(vProdutoNovo, i_idLojaDestino, vLoja);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ProdutoVO> carregarProdutoPrecoVR(ConexaoPostgres i_connPostgres, int idLoja,
            boolean todosProdutos) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            stm = i_connPostgres.createStatement();
            sql = new StringBuilder();
            sql.append("select pa.codigobarras, pc.precovenda, pc.precodiaseguinte, pc.custosemimposto, "
                    + "pc.custocomimposto, pc.estoque, pc.estoqueminimo, pc.estoquemaximo, p.id "
                    + "from produto p "
                    + "inner join produtocomplemento pc on pc.id_produto = p.id "
                    + "inner join produtoautomacao pa on pa.id_produto = p.id "
                    + "where pc.id_loja = " + idLoja);
            if (!todosProdutos) {
                sql.append(" AND p.id_tipoembalagem <> 4 "
                        + " AND p.pesavel = false "
                        + " AND LENGTH(pa.codigobarras::varchar) > 6");
            }

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                long codBarras = rst.getLong("codigobarras");
                ProdutoVO oProduto = new ProdutoVO();
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oProduto.setId(rst.getInt("id"));
                oProduto.setCodigoBarras(codBarras);
                oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                oComplemento.setPrecoDiaSeguinte(rst.getDouble("precodiaseguinte"));
                oProduto.vComplemento.add(oComplemento);
                vProdutoOrigem.add(oProduto);
            }

            return vProdutoOrigem;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarProdutoCustoVR(ConexaoPostgres i_connPostgres, int idLoja,
            boolean todosProdutos) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            stm = i_connPostgres.createStatement();
            sql = new StringBuilder();
            sql.append("select pa.codigobarras, pc.precovenda, pc.precodiaseguinte, pc.custosemimposto, "
                    + "pc.custocomimposto, pc.estoque, pc.estoqueminimo, pc.estoquemaximo, p.id "
                    + "from produto p "
                    + "inner join produtocomplemento pc on pc.id_produto = p.id "
                    + "inner join produtoautomacao pa on pa.id_produto = p.id "
                    + "where pc.id_loja = " + idLoja);
            if (!todosProdutos) {
                sql.append(" AND p.id_tipoembalagem <> 4 "
                        + " AND p.pesavel = false "
                        + " AND LENGTH(pa.codigobarras::varchar) > 6");
            }

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                long codBarras = rst.getLong("codigobarras");
                ProdutoVO oProduto = new ProdutoVO();
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oProduto.setId(rst.getInt("id"));
                oProduto.setCodigoBarras(codBarras);
                oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                oProduto.vComplemento.add(oComplemento);
                vProdutoOrigem.add(oProduto);
            }

            return vProdutoOrigem;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarProdutoEstoqueVR(ConexaoPostgres i_connPostgres, int idLoja,
            boolean todosProdutos) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            stm = i_connPostgres.createStatement();
            sql = new StringBuilder();
            sql.append("select pa.codigobarras, pc.precovenda, pc.precodiaseguinte, pc.custosemimposto, "
                    + "pc.custocomimposto, pc.estoque, pc.estoqueminimo, pc.estoquemaximo, p.id "
                    + "from produto p "
                    + "inner join produtocomplemento pc on pc.id_produto = p.id "
                    + "inner join produtoautomacao pa on pa.id_produto = p.id "
                    + "where pc.id_loja = " + idLoja + " "
                    + "and pc.estoque <> 0 ");
            if (!todosProdutos) {
                sql.append(" AND p.id_tipoembalagem <> 4 "
                        + " AND p.pesavel = false "
                        + " AND LENGTH(pa.codigobarras::varchar) > 6");
            }

            rst = stm.executeQuery(sql.toString());

            int contador = 1;
            while (rst.next()) {
                long codBarras = rst.getLong("codigobarras");
                ProdutoVO oProduto = new ProdutoVO();
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oProduto.setId(new ProdutoDAO().getId(codBarras));
                oProduto.setCodigoBarras(codBarras);
                oComplemento.setEstoque(rst.getDouble("estoque"));
                oComplemento.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                oComplemento.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                oProduto.vComplemento.add(oComplemento);
                vProdutoOrigem.add(oProduto);

                ProgressBar.setStatus("Carregando dados...Estoque " + contador);
                contador++;
            }

            return vProdutoOrigem;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoPrecoVR(ConexaoPostgres i_connPostgres, int idLojaOrigem, int idLojaDestino,
            boolean todosProdutos) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço...");
            vProduto = carregarProdutoPrecoVR(i_connPostgres, idLojaOrigem, todosProdutos);
            new ProdutoDAO().alterarProdutoPrecoVR(vProduto, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoCustoVR(ConexaoPostgres i_connPostgres, int idLojaOrigem, int idLojaDestino,
            boolean todosProdutos) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo...");
            vProduto = carregarProdutoCustoVR(i_connPostgres, idLojaOrigem, todosProdutos);
            new ProdutoDAO().alterarProdutoCustoVR(vProduto, idLojaOrigem);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoEstoqueVR(ConexaoPostgres i_connPostgres, int idLojaOrigem, int idLojaDestino,
            boolean todosProdutos) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque...");
            vProduto = carregarProdutoEstoqueVR(i_connPostgres, idLojaOrigem, todosProdutos);
            //new ProdutoDAO().alterarProdutoEstoqueSomaVR(vProduto, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Long, FornecedorVO> carregarFornecedorVrOrigemVrDestino(ConexaoPostgres i_connPostgres) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        Map<Long, FornecedorVO> vFornecedorOrigem = new HashMap<>();
        long cnpj;

        try {

            stm = i_connPostgres.createStatement();
            sql = new StringBuilder();
            sql.append("SELECT id, razaosocial, nomefantasia, endereco, bairro, id_municipio,                   ");
            sql.append("       cep, id_estado, telefone, id_tipoinscricao, inscricaoestadual,                   ");
            sql.append("       cnpj, revenda, id_situacaocadastro, id_tipopagamento, numerodoc,                 ");
            sql.append("       pedidominimoqtd, pedidominimovalor, serienf, descontofunrural,                   ");
            sql.append("       senha, id_tiporecebimento, agencia, digitoagencia, conta, digitoconta,           ");
            sql.append("       id_banco, id_fornecedorfavorecido, enderecocobranca, bairrocobranca,             ");
            sql.append("       cepcobranca, id_municipiocobranca, id_estadocobranca, bloqueado,                 ");
            sql.append("       id_tipomotivofornecedor, datasintegra, id_tipoempresa, inscricaosuframa,         ");
            sql.append("       utilizaiva, id_familiafornecedor, id_tipoinspecao, numeroinspecao,               ");
            sql.append("       id_tipotroca, id_tipofornecedor, id_contacontabilfinanceiro,                     ");
            sql.append("       utilizanfe, datacadastro, utilizaconferencia, numero, permitenfsempedido,        ");
            sql.append("       modelonf, emitenf, tiponegociacao, utilizacrossdocking, id_lojacrossdocking,     ");
            sql.append("       observacao, id_pais, inscricaomunicipal, id_contacontabilfiscalpassivo,          ");
            sql.append("       numerocobranca, complemento, complementocobranca, id_contacontabilfiscalativo,   ");
            sql.append("       utilizaedi, tiporegravencimento                                                  ");
            sql.append("  FROM fornecedor                                                                       ");
            sql.append(" WHERE LENGTH(cnpj::varchar) >= 9                                                       ");
            rst = stm.executeQuery(sql.toString());

            int contador = 1;
            while (rst.next()) {
                cnpj = rst.getLong("cnpj");
                FornecedorVO oFornecedor = new FornecedorVO();
                oFornecedor.id = rst.getInt("id");
                oFornecedor.codigoanterior = rst.getLong("id");
                oFornecedor.razaosocial = rst.getString("razaosocial");
                oFornecedor.nomefantasia = rst.getString("nomefantasia");
                oFornecedor.endereco = rst.getString("endereco");
                oFornecedor.bairro = rst.getString("bairro");
                oFornecedor.id_municipio = rst.getInt("id_municipio");
                oFornecedor.cep = rst.getLong("cep");
                oFornecedor.id_estado = rst.getInt("id_estado");
                oFornecedor.telefone = rst.getString("telefone");
                oFornecedor.id_tipoinscricao = rst.getInt("id_tipoinscricao");
                oFornecedor.inscricaoestadual = rst.getString("inscricaoestadual");
                oFornecedor.cnpj = cnpj;
                oFornecedor.revenda = rst.getBoolean("revenda");
                oFornecedor.id_situacaocadastro = rst.getInt("id_situacaocadastro");
                oFornecedor.id_tipopagamento = rst.getInt("id_tipopagamento");
                oFornecedor.numerodoc = rst.getInt("numerodoc");
                oFornecedor.pedidominimoqtd = rst.getInt("pedidominimoqtd");
                oFornecedor.pedidominimovalor = rst.getInt("pedidominimovalor");
                oFornecedor.serienf = rst.getString("serienf");
                oFornecedor.descontofunrural = rst.getBoolean("descontofunrural");
                oFornecedor.senha = rst.getInt("senha");
                oFornecedor.id_tiporecebimento = rst.getInt("id_tiporecebimento");
                oFornecedor.agencia = rst.getString("agencia");
                oFornecedor.digitoagencia = rst.getString("digitoagencia");
                oFornecedor.conta = rst.getString("conta");
                oFornecedor.digitoconta = rst.getString("digitoconta");
                oFornecedor.id_banco = rst.getString("id_banco") == null ? 804 : rst.getInt("id_banco");
                oFornecedor.id_fornecedorfavorecido = rst.getString("id_fornecedorfavorecido") == null ? -1 : rst.getInt("id_fornecedorfavorecido");
                oFornecedor.enderecocobranca = rst.getString("enderecocobranca");
                oFornecedor.bairrocobranca = rst.getString("bairrocobranca");
                oFornecedor.cepcobranca = rst.getLong("cepcobranca");
                oFornecedor.id_municipiocobranca = rst.getString("id_municipiocobranca") == null ? -1 : rst.getInt("id_municipiocobranca");
                oFornecedor.id_estadocobranca = rst.getString("id_estadocobranca") == null ? -1 : rst.getInt("id_estadocobranca");
                oFornecedor.bloqueado = rst.getBoolean("bloqueado");
                oFornecedor.id_tipomotivofornecedor = rst.getInt("id_tipomotivofornecedor");
                oFornecedor.datasintegra = rst.getTimestamp("datasintegra");
                oFornecedor.id_tipoempresa = rst.getInt("id_tipoempresa");
                oFornecedor.inscricaosuframa = rst.getString("inscricaosuframa");
                oFornecedor.utilizaiva = rst.getBoolean("utilizaiva");
                oFornecedor.id_familiafornecedor = rst.getString("id_familiafornecedor") == null ? -1 : rst.getInt("id_familiafornecedor");
                oFornecedor.id_tipoinspecao = rst.getInt("id_tipoinspecao");
                oFornecedor.numeroinspecao = rst.getInt("numeroinspecao");
                oFornecedor.id_tipotroca = rst.getInt("id_tipotroca");
                oFornecedor.id_tipofornecedor = rst.getInt("id_tipofornecedor");
                //oFornecedor.id_contacontabilfinanceiro = ContaContabilFinanceiro.getByID(rst.getInt("id_contacontabilfinanceiro"));
                oFornecedor.utilizanfe = rst.getBoolean("utilizanfe");
                oFornecedor.datacadastro = rst.getDate("datacadastro");
                oFornecedor.utilizaconferencia = rst.getBoolean("utilizaconferencia");
                oFornecedor.numero = rst.getString("numero") == "" ? "0" : rst.getString("numero");
                oFornecedor.permitenfsempedido = rst.getBoolean("permitenfsempedido");
                oFornecedor.modelonf = rst.getString("modelonf");
                oFornecedor.emitenf = rst.getBoolean("emitenf");
                oFornecedor.tiponegociacao = rst.getInt("tiponegociacao");
                oFornecedor.utilizacrossdocking = rst.getBoolean("utilizacrossdocking");
                oFornecedor.id_lojacrossdocking = rst.getInt("id_lojacrossdocking");
                oFornecedor.observacao = rst.getString("observacao");
                oFornecedor.id_pais = rst.getInt("id_pais");
                oFornecedor.inscricaomunicipal = rst.getString("inscricaomunicipal");
                oFornecedor.id_contacontabilfiscalpassivo = rst.getInt("id_contacontabilfiscalpassivo");
                oFornecedor.numerocobranca = rst.getString("numerocobranca");
                oFornecedor.complemento = rst.getString("complemento");
                oFornecedor.complementocobranca = rst.getString("complementocobranca");
                oFornecedor.id_contacontabilfiscalativo = rst.getInt("id_contacontabilfiscalativo");
                oFornecedor.utilizaedi = rst.getBoolean("utilizaedi");
                oFornecedor.tiporegravencimento = rst.getInt("tiporegravencimento");
                vFornecedorOrigem.put(cnpj, oFornecedor);
                ProgressBar.setStatus("Carregando dados para comparação..." + contador);
                contador++;
            }

            return vFornecedorOrigem;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedorVrOrigemVrDestino(int i_idLojaOrigem, int i_idLojaDestino, ConexaoPostgres i_connOrigem) throws Exception {
        List<FornecedorVO> vFornecedorNovo = new ArrayList<>();
        List<FornecedorVO> vFornecedorAlterado = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Long, FornecedorVO> vFornecedorOrigem = carregarFornecedorVrOrigemVrDestino(i_connOrigem);
            Map<Long, Integer> vFornecedorDestino = new FornecedorDAO().carregarCnpj();

            ProgressBar.setStatus("Comparando fornecedores Loja Origem/Loja Destino...");
            ProgressBar.setMaximum(vFornecedorOrigem.size() + vFornecedorDestino.size());

            for (Long keyCnpj : vFornecedorOrigem.keySet()) {
                if (vFornecedorDestino.containsKey(keyCnpj)) {
                    long codigoFornecedor = vFornecedorDestino.get(keyCnpj);
                    FornecedorVO oFornecedor = vFornecedorOrigem.get(keyCnpj);
                    oFornecedor.id = (int) codigoFornecedor;
                    vFornecedorAlterado.add(oFornecedor);
                } else {
                    FornecedorVO oFornecedor = vFornecedorOrigem.get(keyCnpj);
                    oFornecedor.id = 0;
                    vFornecedorNovo.add(oFornecedor);
                }
                ProgressBar.next();
            }

            //if (!vProdutoAlterado.isEmpty()) {
            //    new ProdutoDAO().salvar(vProdutoAlterado, i_idLojaDestino, true, null, true,0);
            //}
            if (!vFornecedorNovo.isEmpty()) {
                new FornecedorDAO().salvar(vFornecedorNovo, i_idLojaDestino);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarClienteLoja(int i_idLojaDestino, ConexaoPostgres i_connOrigem) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados para importação...");
            vClientePreferencial = carregarClienteLoja(i_connOrigem);
            new PlanoDAO().salvar(i_idLojaDestino);
            ClientePreferencialDAO clientePreferencialDAO = new ClientePreferencialDAO();
            clientePreferencialDAO.salvarCpf(vClientePreferencial, i_idLojaDestino, i_idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void migrarCreditoRotativo(int idLoja, int idLojaCliente, ConexaoPostgres i_connOrigem) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente, i_connOrigem);

            new ReceberCreditoRotativoDAO().salvarComCnpj(vReceberCliente, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClienteLoja(ConexaoPostgres i_connOrigem) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try {

            stm = i_connOrigem.createStatement();

            sql = new StringBuilder();
            sql.append("SELECT id, nome, id_situacaocadastro, endereco, bairro, id_estado, id_municipio, ");
            sql.append("cep, telefone, celular, email, inscricaoestadual, orgaoemissor, ");
            sql.append("cnpj, id_tipoestadocivil, datanascimento, dataresidencia, datacadastro, ");
            sql.append("id_tiporesidencia, sexo, id_banco, agencia, conta, praca, observacao, ");
            sql.append("empresa, id_estadoempresa, id_municipioempresa, enderecoempresa, ");
            sql.append("bairroempresa, cepempresa, telefoneempresa, dataadmissao, cargo, ");
            sql.append("salario, outrarenda, valorlimite, nomeconjuge, datanascimentoconjuge, ");
            sql.append("cpfconjuge, rgconjuge, orgaoemissorconjuge, empresaconjuge, id_estadoconjuge, ");
            sql.append("id_municipioconjuge, enderecoempresaconjuge, bairroempresaconjuge, ");
            sql.append("cepempresaconjuge, telefoneempresaconjuge, dataadmissaoconjuge, ");
            sql.append("cargoconjuge, salarioconjuge, outrarendaconjuge, id_tipoinscricao, ");
            sql.append("vencimentocreditorotativo, observacao2, permitecreditorotativo, ");
            sql.append("permitecheque, nomemae, nomepai, datarestricao, bloqueado, id_plano, ");
            sql.append("bloqueadoautomatico, numero, senha, id_tiporestricaocliente, ");
            sql.append("dataatualizacaocadastro, numeroempresa, numeroempresaconjuge, ");
            sql.append("complemento, complementoempresa, complementoempresaconjuge, id_contacontabilfiscalpassivo, ");
            sql.append("id_contacontabilfiscalativo, enviasms, enviaemail, id_grupo, id_regiaocliente ");
            sql.append("FROM clientepreferencial ");
            sql.append("WHERE LENGTH(cnpj::varchar) >= 9 ");
            sql.append("order by nome ");
            rst = stm.executeQuery(sql.toString());

            int contador = 1;
            while (rst.next()) {
                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                oClientePreferencial.codigoanterior = rst.getInt("id");
                oClientePreferencial.id = rst.getInt("id");
                oClientePreferencial.nome = Utils.acertarTexto(rst.getString("nome"));
                oClientePreferencial.id_situacaocadastro = rst.getInt("id_situacaocadastro");
                oClientePreferencial.endereco = Utils.acertarTexto(rst.getString("endereco"));
                oClientePreferencial.bairro = Utils.acertarTexto(rst.getString("bairro"));
                oClientePreferencial.id_estado = rst.getInt("id_estado");
                oClientePreferencial.id_municipio = rst.getInt("id_municipio");
                oClientePreferencial.cep = rst.getLong("cep");
                oClientePreferencial.telefone = Utils.acertarTexto(rst.getString("telefone"));
                oClientePreferencial.email = Utils.acertarTexto(rst.getString("email"));
                oClientePreferencial.inscricaoestadual = Utils.acertarTexto(rst.getString("inscricaoestadual"));
                oClientePreferencial.orgaoemissor = Utils.acertarTexto(rst.getString("orgaoemissor"));
                oClientePreferencial.cnpj = rst.getLong("cnpj");
                oClientePreferencial.id_tipoestadocivil = rst.getInt("id_tipoestadocivil");
                oClientePreferencial.datanascimento = rst.getString("datanascimento") == null ? null : rst.getString("datanascimento");
                oClientePreferencial.dataresidencia = rst.getString("dataresidencia");
                oClientePreferencial.datacadastro = rst.getString("datacadastro");
                oClientePreferencial.id_tiporesidencia = rst.getInt("id_tiporesidencia");
                oClientePreferencial.sexo = rst.getInt("sexo");
                oClientePreferencial.id_banco = rst.getString("id_banco") == null ? 804 : new BancoDAO().getId(rst.getInt("id_banco"));
                oClientePreferencial.agencia = Utils.acertarTexto(rst.getString("agencia"));
                oClientePreferencial.conta = Utils.acertarTexto(rst.getString("conta"));
                oClientePreferencial.praca = Utils.acertarTexto(rst.getString("praca"));
                oClientePreferencial.observacao = Utils.acertarTexto(rst.getString("observacao"));
                oClientePreferencial.empresa = Utils.acertarTexto(rst.getString("empresa"));
                oClientePreferencial.id_estadoempresa = rst.getString("id_estadoempresa") == null ? 0 : rst.getInt("id_estadoempresa");
                oClientePreferencial.id_municipioempresa = rst.getString("id_municipioempresa") == null ? 0 : rst.getInt("id_municipioempresa");
                oClientePreferencial.enderecoempresa = rst.getString("enderecoempresa");
                oClientePreferencial.bairroempresa = rst.getString("bairroempresa");
                oClientePreferencial.cepempresa = rst.getLong("cepempresa");
                oClientePreferencial.telefoneempresa = rst.getString("telefoneempresa");
                oClientePreferencial.dataadmissao = rst.getString("dataadmissao") == null ? null : rst.getDate("dataadmissao");
                oClientePreferencial.cargo = Utils.acertarTexto(rst.getString("cargo"));
                oClientePreferencial.salario = rst.getDouble("salario");
                oClientePreferencial.outrarenda = rst.getDouble("outrarenda");
                oClientePreferencial.valorlimite = rst.getDouble("valorlimite");
                oClientePreferencial.nomeconjuge = Utils.acertarTexto(rst.getString("nomeconjuge"));
                oClientePreferencial.datanascimentoconjuge = "".equals(rst.getString("datanascimentoconjuge")) ? "" : rst.getString("datanascimentoconjuge");
                oClientePreferencial.cpfconjuge = rst.getLong("cpfconjuge");
                oClientePreferencial.rgconjuge = Utils.acertarTexto(rst.getString("rgconjuge"));
                oClientePreferencial.orgaoemissorconjuge = Utils.acertarTexto(rst.getString("orgaoemissorconjuge"));
                oClientePreferencial.empresaconjuge = Utils.acertarTexto(rst.getString("empresaconjuge"));
                oClientePreferencial.id_estadoconjuge = rst.getString("id_estadoconjuge") == null ? 0 : rst.getInt("id_estadoconjuge");
                oClientePreferencial.id_municipioconjuge = rst.getString("id_municipioconjuge") == null ? 0 : rst.getInt("id_municipioconjuge");
                oClientePreferencial.enderecoempresaconjuge = Utils.acertarTexto(rst.getString("enderecoempresaconjuge"));
                oClientePreferencial.bairroempresaconjuge = Utils.acertarTexto(rst.getString("bairroempresaconjuge"));
                oClientePreferencial.cepempresaconjuge = rst.getLong("cepempresaconjuge");
                oClientePreferencial.telefoneempresaconjuge = Utils.acertarTexto(rst.getString("telefoneempresaconjuge"));
                oClientePreferencial.dataadmissaoconjuge = rst.getString("dataadmissaoconjuge") == null ? null : rst.getDate("dataadmissaoconjuge");
                oClientePreferencial.cargoconjuge = Utils.acertarTexto(rst.getString("cargoconjuge"));
                oClientePreferencial.salarioconjuge = rst.getDouble("salarioconjuge");
                oClientePreferencial.outrarendaconjuge = rst.getDouble("outrarendaconjuge");
                oClientePreferencial.id_tipoinscricao = rst.getInt("id_tipoinscricao");
                oClientePreferencial.vencimentocreditorotativo = rst.getInt("vencimentocreditorotativo");
                oClientePreferencial.observacao2 = Utils.acertarTexto(rst.getString("observacao2"));
                oClientePreferencial.permitecreditorotativo = rst.getBoolean("permitecreditorotativo");
                oClientePreferencial.permitecheque = rst.getBoolean("permitecheque");
                oClientePreferencial.nomemae = Utils.acertarTexto(rst.getString("nomemae"));
                oClientePreferencial.nomepai = Utils.acertarTexto(rst.getString("nomepai"));
                oClientePreferencial.datarestricao = rst.getString("datarestricao") == null ? null : rst.getDate("datarestricao");
                oClientePreferencial.bloqueado = rst.getBoolean("bloqueado");
                oClientePreferencial.id_plano = rst.getString("id_plano") == null ? -1 : rst.getInt("id_plano");
                oClientePreferencial.bloqueadoautomatico = rst.getBoolean("bloqueadoautomatico");
                oClientePreferencial.numero = Utils.acertarTexto(rst.getString("numero"));
                oClientePreferencial.senha = rst.getInt("senha");
                oClientePreferencial.id_tiporestricaocliente = rst.getInt("id_tiporestricaocliente");
                oClientePreferencial.dataatualizacaocadastro = rst.getString("dataatualizacaocadastro") == null ? null : rst.getDate("dataatualizacaocadastro");
                oClientePreferencial.numeroempresa = Utils.acertarTexto(rst.getString("numeroempresa"));
                oClientePreferencial.numeroempresaconjuge = Utils.acertarTexto(rst.getString("numeroempresaconjuge"));
                oClientePreferencial.complemento = Utils.acertarTexto(rst.getString("complemento"));
                oClientePreferencial.complementoempresa = Utils.acertarTexto(rst.getString("complementoempresa"));
                oClientePreferencial.complementoempresaconjuge = Utils.acertarTexto(rst.getString("complementoempresaconjuge"));
                oClientePreferencial.id_contacontabilfiscalpassivo = rst.getString("id_contacontabilfiscalpassivo") == null ? 0 : rst.getInt("id_contacontabilfiscalpassivo");
                oClientePreferencial.id_contacontabilfiscalativo = rst.getString("id_contacontabilfiscalativo") == null ? 0 : rst.getInt("id_contacontabilfiscalativo");
                oClientePreferencial.enviasms = rst.getBoolean("enviasms");
                oClientePreferencial.enviaemail = rst.getBoolean("enviaemail");
                oClientePreferencial.id_grupo = rst.getString("id_grupo") == null ? 0 : rst.getInt("id_grupo");
                oClientePreferencial.id_regiaocliente = rst.getInt("id_regiaocliente");
                vClientePreferencial.add(oClientePreferencial);

                ProgressBar.setStatus("Carregando dados...Cliente Preferencial..." + contador);
                contador++;
            }
            return vClientePreferencial;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente, ConexaoPostgres i_connOrigem) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        try {

            stm = i_connOrigem.createStatement();

            sql = new StringBuilder();
            sql.append("SELECT r.id, id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual,  ");
            sql.append("       r.observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
            sql.append("       datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca,   ");
            sql.append("       valormulta, justificativa, exportado, datahoraalteracao, nomedependente,  ");
            sql.append("       cpfdependente, c.cnpj as cpfcliente, c.nome nomeCliente  ");
            sql.append("  FROM recebercreditorotativo r ");
            sql.append("left outer join clientepreferencial c on ");
            sql.append("   c.id = r.id_clientepreferencial ");
            sql.append("where id_situacaorecebercreditorotativo = 0 ");
            sql.append("and id_loja = " + id_loja);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                oReceberCreditoRotativo.id_clientepreferencial = rst.getInt("id");

                oReceberCreditoRotativo.id_loja = id_lojaCliente;
                oReceberCreditoRotativo.dataemissao = rst.getString("dataemissao");
                oReceberCreditoRotativo.numerocupom = rst.getInt("numerocupom");
                oReceberCreditoRotativo.ecf = rst.getInt("ecf");
                oReceberCreditoRotativo.valor = rst.getDouble("valor");
                oReceberCreditoRotativo.lancamentomanual = rst.getBoolean("lancamentomanual");
                oReceberCreditoRotativo.observacao = rst.getString("observacao");
                oReceberCreditoRotativo.id_situacaorecebercreditorotativo = rst.getInt("id_situacaorecebercreditorotativo");

                oReceberCreditoRotativo.id_clientepreferencial = rst.getInt("id_clientepreferencial");
                oReceberCreditoRotativo.cnpjCliente = rst.getLong("cpfcliente");

                oReceberCreditoRotativo.datavencimento = rst.getString("datavencimento");
                oReceberCreditoRotativo.matricula = rst.getInt("matricula");
                oReceberCreditoRotativo.parcela = rst.getInt("parcela");
                oReceberCreditoRotativo.valorjuros = rst.getDouble("valorjuros");
                oReceberCreditoRotativo.id_boleto = rst.getInt("id_boleto");
                oReceberCreditoRotativo.id_tipolocalcobranca = rst.getInt("id_tipolocalcobranca");
                oReceberCreditoRotativo.valormulta = rst.getDouble("valormulta");
                oReceberCreditoRotativo.justificativa = rst.getString("justificativa");
                oReceberCreditoRotativo.exportado = rst.getBoolean("exportado");
                oReceberCreditoRotativo.datahoraalteracao = rst.getTimestamp("datahoraalteracao");
                oReceberCreditoRotativo.nomeCliente = rst.getString("nomeCliente");
                oReceberCreditoRotativo.dataPagamento = null;//rst.getString("dataPagamento");
                oReceberCreditoRotativo.valorPago = 0;//rst.getDouble("valorPago");
                oReceberCreditoRotativo.setId_loja(id_lojaCliente);

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
            }

            return vReceberCreditoRotativo;

        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberChequeVO> carregarReceberCheque(int idLojaOrigem, int idLojaDestino,
            ConexaoPostgres i_connOrigem) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        try (Statement stm = i_connOrigem.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id_loja, cpf, numerocheque, id_banco, agencia, conta, data, \n"
                    + "       id_plano, numerocupom, ecf, valor, datadeposito, lancamentomanual, \n"
                    + "       rg, telefone, nome, observacao, id_situacaorecebercheque, id_tipolocalcobranca, \n"
                    + "       cmc7, datadevolucao, id_tipoalinea, id_tipoinscricao, dataenviocobranca, \n"
                    + "       valorpagarfornecedor, id_boleto, operadorclientebloqueado, operadorexcedelimite, \n"
                    + "       operadorproblemacheque, operadorchequebloqueado, valorjuros, \n"
                    + "       id_tipovistaprazo, justificativa, valoracrescimo, valorinicial, \n"
                    + "       datahoraalteracao, operadorclientenaocadastrado\n"
                    + "  FROM public.recebercheque\n"
                    + " where id_loja = " + idLojaOrigem
                    + "   and id_situacaorecebercheque = 0"
            )) {
                while (rst.next()) {
                    ReceberChequeVO vo = new ReceberChequeVO();
                    vo.id_loja = idLojaDestino;
                    vo.cpf = rst.getLong("cpf");
                    vo.numerocheque = rst.getInt("numerocheque");
                    vo.id_banco = (rst.getString("id_banco") == null ? 804 : new BancoDAO().getId(rst.getInt("id_banco")));
                    vo.agencia = Utils.acertarTexto(rst.getString("agencia"));
                    vo.conta = Utils.acertarTexto(rst.getString("conta"));
                    vo.data = rst.getString("data");
                    vo.numerocupom = rst.getInt("numerocupom");
                    vo.ecf = rst.getInt("ecf");
                    vo.valor = rst.getDouble("valor");
                    vo.datadeposito = rst.getString("datadeposito");
                    vo.lancamentomanual = rst.getBoolean("lancamentomanual");
                    vo.rg = Utils.acertarTexto(rst.getString("rg"));
                    vo.telefone = Utils.acertarTexto(rst.getString("telefone"));
                    vo.nome = Utils.acertarTexto(rst.getString("nome"));
                    vo.observacao = Utils.acertarTexto(rst.getString("observacao"));
                    vo.id_situacaorecebercheque = rst.getInt("id_situacaorecebercheque");
                    vo.cmc7 = Utils.acertarTexto(rst.getString("cmc7"));
                    vo.datadevolucao = rst.getString("datadevolucao");
                    vo.id_tipoalinea = rst.getInt("id_tipoalinea");
                    vo.id_tipoinscricao = rst.getInt("id_tipoinscricao");
                    vo.dataenviocobranca = rst.getDate("dataenviocobranca");
                    vo.valorpagarfornecedor = rst.getDouble("valorpagarfornecedor");
                    vo.id_boleto = rst.getInt("id_boleto");
                    vo.operadorclientebloqueado = rst.getString("operadorclientebloqueado");
                    vo.operadorexcedelimite = rst.getString("operadorexcedelimite");
                    vo.operadorproblemacheque = rst.getString("operadorproblemacheque");
                    vo.operadorchequebloqueado = rst.getString("operadorchequebloqueado");
                    vo.valorjuros = rst.getDouble("valorjuros");
                    vo.id_tipovistaprazo = rst.getInt("id_tipovistaprazo");
                    vo.justificativa = Utils.acertarTexto(rst.getString("justificativa"));
                    vo.valoracrescimo = rst.getDouble("valoracrescimo");
                    vo.valorinicial = rst.getDouble("valorinicial");
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public void importarReceberCheque(int idLojaOrigem, int idLojaDestino,
            ConexaoPostgres i_connOrigem) throws Exception {
        List<ReceberChequeVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receger Cheque...");
            vResult = carregarReceberCheque(idLojaOrigem, idLojaDestino, i_connOrigem);
            if (!vResult.isEmpty()) {
                new ReceberChequeDAO().salvar(vResult, idLojaDestino);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
