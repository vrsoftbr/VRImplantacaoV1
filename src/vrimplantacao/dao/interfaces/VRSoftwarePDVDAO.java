package vrimplantacao.dao.interfaces;

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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

public class VRSoftwarePDVDAO {

    public void importarProduto(int id_loja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutos(id_loja, idLojaCliente);
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
            salvarProdutoPDV(vProdutoNovo, vLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarras();
            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            salvarCodigoBarrasPDV(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos sem Código Barras...");
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
    
    private Map<Integer, ProdutoVO> carregarProdutos(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Map<Integer, ProdutoVO> v_produto = new HashMap<>();
        int idProduto, idAliquota, idSituacaoCadastro, idTipoEmbalagem, 
            idTipoPisCofins, idTipoPisCofinsCredito = 0, ncm1 = 0, ncm2 = 0, 
            ncm3 = 0, mercadologico1 = 0, mercadologico2 = 0, mercadologico3 = 0,
            tipoNaturezaReceita = 0, idCest;
        String descricaoCompleta, descricaoReduzida, ncmAtual;
        double precoVenda;
        boolean aceitaMultiplicacaoPdv = false;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            stmPostgres = Conexao.createStatement();
            
            sql = new StringBuilder();
            sql.append("select id, descricaocompleta, descricaoreduzida, precovenda, id_aliquota, id_situacaocadastro, ");
            sql.append("aceitamultiplicacaopdv, tipoembalagem, ncm, id_tipopiscofins, cest ");
            sql.append("from produto ");
            sql.append("order by id ");
            
            rst = stm.executeQuery(sql.toString());
            int contator = 1;
            while (rst.next()) {
                
                idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("id")));
                descricaoCompleta = rst.getString("descricaocompleta");
                descricaoReduzida = rst.getString("descricaoreduzida");
                precoVenda = rst.getDouble("precovenda");
                idSituacaoCadastro = rst.getInt("id_situacaocadastro");                
                aceitaMultiplicacaoPdv = rst.getInt("aceitamultiplicacaopdv") == 1;
                
                if ("KG".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 4;
                } else if ("CX".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 1;
                } else if ("LA".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 2;
                } else if ("PT".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 3;
                } else if ("GF".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 7;
                } else if ("MT".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 6;
                } else if ("PC".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 8;
                } else if ("FD".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 5;
                } else if ("LT".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 9;
                } else if ("BD".equals(rst.getString("tipoembalagem").trim())) {
                    idTipoEmbalagem = 14;
                } else {
                    idTipoEmbalagem = 0;
                }
                
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
                }

                if ((rst.getString("cest") != null) &&
                        (!rst.getString("cest").trim().isEmpty())) {
                    idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("cest").trim())));
                } else {
                    idCest = -1;
                }
                
                idAliquota = rst.getInt("id_aliquota");
                idTipoPisCofins = rst.getInt("id_tipopiscofins");
                
                if (idTipoPisCofins == 0) {
                    idTipoPisCofinsCredito = 12;
                } else if (idTipoPisCofins == 1) {
                    idTipoPisCofinsCredito = 13;
                } else if (idTipoPisCofins == 2) {
                    idTipoPisCofinsCredito = 14;
                } else if (idTipoPisCofins == 3) {
                    idTipoPisCofinsCredito = 15;
                } else if (idTipoPisCofins == 5) {
                    idTipoPisCofinsCredito = 17;
                } else if (idTipoPisCofins == 6) {
                    idTipoPisCofinsCredito = 18;
                } else if (idTipoPisCofins == 7) {
                    idTipoPisCofinsCredito = 19;
                } else if (idTipoPisCofins == 8) {
                    idTipoPisCofinsCredito = 20;
                } else if (idTipoPisCofins == 9) {
                    idTipoPisCofinsCredito = 21;
                }
                
                tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                
                sql = new StringBuilder();
                sql.append("select mercadologico1 as mercadologico1 ");
                sql.append("from mercadologico ");
                sql.append("where descricao like '%ACERTAR%' ");
                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {
                    mercadologico1 = rstPostgres.getInt("mercadologico1");
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoCompleta;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.aceitaMultiplicacaoPdv = aceitaMultiplicacaoPdv;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.idCest = idCest;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Global.idEstado;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                oProduto.vAliquota.add(oAliquota);
                
                v_produto.put(idProduto, oProduto);                
                
                ProgressBar.setStatus("Carregando dados...Produtos..."+contator);
                contator ++;
            }            
            return v_produto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private Map<Long, ProdutoVO> carregarCodigoBarras() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Map<Long, ProdutoVO> v_produto = new HashMap<>();
        int idProduto, qtdEmbalagem, idTipoEmbalagem;
        long codigoBarras;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select pa.id_produto, pa.codigobarras, pa.qtdembalagem, p.tipoembalagem ");
            sql.append("from produtoautomacao pa ");
            sql.append("inner join produto p on p.id = pa.id_produto ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("id_produto")));
                codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("codigobarras")));
                qtdEmbalagem = rst.getInt("qtdembalagem");

                if (("KG".equals(rst.getString("tipoembalagem").trim()))) {
                    idTipoEmbalagem = 4;
                } else {
                    idTipoEmbalagem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                
                v_produto.put(codigoBarras, oProduto);
            }
            
            return v_produto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void salvarProdutoPDV(List<ProdutoVO> v_produto, List<LojaVO> vLoja) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Produtos PDV...");
            ProgressBar.setMaximum(v_produto.size());
            
            for (ProdutoVO i_produto : v_produto) {
                
                sql = new StringBuilder();
                sql.append("select id from produto ");
                sql.append("where id = " + i_produto.id);
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {                    
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {                    
                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("precovenda = " + oComplemento.precoVenda+", ");
                        sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte+" ");
                        sql.append("where id_produto = " + i_produto.id+" ");
                        sql.append("and id_loja = " + oComplemento.idLoja+";");
                        stm.execute(sql.toString());
                    }                    
                } else {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                    sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                    sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                    sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                    sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                    sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                    sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                    sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                    sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest)");
                    sql.append(" VALUES ");
                    sql.append(" (");

                    if ("".equals(i_produto.dataCadastro.trim())) {
                        i_produto.dataCadastro = new DataProcessamentoDAO().get();
                    }
                    
                    sql.append(i_produto.id + ",");
                    sql.append("'" + i_produto.descricaoCompleta + "',");
                    sql.append(i_produto.qtdEmbalagem + ",");
                    sql.append(i_produto.idTipoEmbalagem + ",");
                    sql.append(i_produto.mercadologico1 + ",");
                    sql.append(i_produto.mercadologico2 + ",");
                    sql.append(i_produto.mercadologico3 + ",");
                    sql.append(i_produto.mercadologico4 + ",");
                    sql.append(i_produto.mercadologico5 + ",");
                    sql.append(i_produto.idComprador + ",");
                    sql.append((i_produto.idFamiliaProduto == -1 ? null : i_produto.idFamiliaProduto) + ",");
                    sql.append("'" + i_produto.descricaoReduzida + "',");
                    sql.append(i_produto.pesoLiquido + ",");
                    sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");
                    sql.append(i_produto.validade + ",");
                    sql.append(i_produto.pesoBruto + ",");
                    sql.append(i_produto.tara + ",");
                    sql.append(i_produto.comprimentoEmbalagem + ",");
                    sql.append(i_produto.larguraEmbalagem + ",");
                    sql.append(i_produto.alturaEmbalagem + ",");
                    sql.append(i_produto.perda + ",");
                    sql.append(i_produto.margem + ",");
                    sql.append(i_produto.verificaCustoTabela + ",");
                    sql.append("'" + i_produto.descricaoGondola + "',");
                    sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                    sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                    sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                    sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                    sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                    sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                    sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                    sql.append(i_produto.fabricacaoPropria + ",");
                    sql.append(i_produto.sugestaoPedido + ",");
                    sql.append(i_produto.sugestaoCotacao + ",");
                    sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                    sql.append(i_produto.idFornecedorFabricante + ",");
                    sql.append(i_produto.idDivisaoFornecedor + ",");
                    sql.append(i_produto.idTipoProduto + ",");
                    sql.append(i_produto.idTipoPisCofinsDebito + ",");
                    sql.append(i_produto.idTipoPisCofinsCredito + ",");
                    sql.append(i_produto.custoFinal + ",");
                    sql.append(i_produto.percentualIpi + ",");
                    sql.append(i_produto.percentualFrete + ",");
                    sql.append(i_produto.percentualEncargo + ",");
                    sql.append(i_produto.percentualPerda + ",");
                    sql.append(i_produto.percentualSubstituicao + ",");
                    sql.append(i_produto.pesavel + ",");
                    sql.append(i_produto.sazonal + ",");
                    sql.append(i_produto.consignado + ",");
                    sql.append(i_produto.ddv + ",");
                    sql.append(i_produto.permiteTroca + ",");
                    sql.append(i_produto.temperatura + ",");
                    sql.append(i_produto.idTipoOrigemMercadoria + ",");
                    sql.append(i_produto.ipi + ",");
                    sql.append(i_produto.vendaControlada + ",");
                    sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                    sql.append(i_produto.vendaPdv + ",");
                    sql.append(i_produto.permiteQuebra + ",");
                    sql.append(i_produto.permitePerda + ",");
                    sql.append(i_produto.impostoMedioImportado + ",");
                    sql.append(i_produto.impostoMedioNacional + ",");
                    sql.append(i_produto.impostoMedioEstadual + ",");
                    sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                    sql.append(i_produto.utilizaValidadeEntrada + ",");
                    sql.append(i_produto.idTipoLocalTroca + ",");
                    sql.append(i_produto.idTipoCompra + ",");
                    sql.append("'" + i_produto.codigoAnp + "',");
                    sql.append(i_produto.numeroParcela + ",");
                    sql.append(i_produto.qtdDiasMinimoValidade + ", ");
                    sql.append((i_produto.idCest == -1 ? null : i_produto.idCest) + ");");
                    stm.execute(sql.toString());
                    
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {  
                        for (LojaVO oLoja : vLoja) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                            sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                            sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                            sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                            sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                            sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                            sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                            sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                            sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                            sql.append(" VALUES (");
                            sql.append(i_produto.id + ",");
                            sql.append("'" + oComplemento.prateleira + "',");
                            sql.append("'" + oComplemento.secao + "',");
                            sql.append(oComplemento.estoqueMinimo + ",");
                            sql.append(oComplemento.estoqueMaximo + ",");
                            sql.append(oComplemento.valorIpi + ",");
                            sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                            sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                            sql.append(oComplemento.custoSemImposto + ",");
                            sql.append(oComplemento.custoComImposto + ",");
                            sql.append(oComplemento.custoSemImpostoAnterior + ",");
                            sql.append(oComplemento.custoComImpostoAnterior + ",");
                            sql.append(oComplemento.precoVenda + ",");
                            sql.append(oComplemento.precoVendaAnterior + ",");
                            sql.append(oComplemento.precoDiaSeguinte + ",");
                            sql.append(oComplemento.estoque + ",");
                            sql.append(oComplemento.troca + ",");
                            sql.append(oComplemento.emiteEtiqueta + ",");
                            sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                            sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                            sql.append(oComplemento.custoMedioComImposto + ",");
                            sql.append(oComplemento.custoMedioSemImposto + ",");
                            sql.append(oComplemento.idAliquotaCredito + ",");
                            sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                            sql.append(oComplemento.teclaAssociada + ",");
                            sql.append(oComplemento.idSituacaoCadastro + ",");
                            sql.append(oLoja.id + ",");
                            sql.append(oComplemento.descontinuado + ",");
                            sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                            sql.append(oComplemento.centralizado + ",");
                            sql.append(oComplemento.operacional + ",");
                            sql.append(oComplemento.valorIcmsSubstituicao + ",");
                            sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                            sql.append(oComplemento.cestaBasica + ",");
                            sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                            sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                            sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                            sql.append(oComplemento.valorOutrasSubstituicao + ");");
                            stm.execute(sql.toString());
                        }
                    }

                    for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                        sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf)");
                        sql.append(" VALUES(");
                        sql.append(i_produto.id + ",");
                        sql.append(oAliquota.idEstado + ",");
                        sql.append(oAliquota.idAliquotaDebito + ",");
                        sql.append(oAliquota.idAliquotaCredito + ",");
                        sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                        sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                        sql.append(oAliquota.idAliquotaDebitoForaEstadoNF);
                        sql.append(");");
                        stm.execute(sql.toString());
                    }
                }
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvarCodigoBarrasPDV(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Importando dados...Código Barras...");
            ProgressBar.setMaximum(v_produto.size());
            
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                    sql = new StringBuilder();
                    sql.append("select * from produtoautomacao ");
                    sql.append("where codigobarras = " + oAutomacao.codigoBarras);
                    
                    rst = stm.executeQuery(sql.toString());
                    
                    if (!rst.next()) {                        
                        sql = new StringBuilder();
                        sql.append("insert into produtoautomacao (");
                        sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                        sql.append("values (");
                        sql.append(i_produto.id + ",");
                        sql.append(oAutomacao.codigoBarras + ",");
                        sql.append((oAutomacao.qtdEmbalagem == -1 ? "1" : oAutomacao.qtdEmbalagem) + ",");
                        sql.append(oAutomacao.idTipoEmbalagem+");");
                        stm.execute(sql.toString());
                    }                    
                }  
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    } 
}