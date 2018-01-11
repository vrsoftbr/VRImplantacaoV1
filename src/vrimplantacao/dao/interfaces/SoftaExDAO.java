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
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
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
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class SoftaExDAO {
    public String Texto;
    public int Contador = 0;    
    //CARREGAMENTOS
    
    // INICIO PRODUTO
    public List<FamiliaProdutoVO> carregarFamiliaProdutoSoftaEx() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT TRIM(CODIGO) AS CODIGO, NOME FROM GRUPOS ORDER BY TRIM(CODIGO) ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id                  = Integer.parseInt(rst.getString("CODIGO"));
                oFamiliaProduto.descricao           = util.acertarTexto(rst.getString("NOME").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant           = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<MercadologicoVO> carregarMercadologicoSoftaEx(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT CASE TRIM(CODIGO) WHEN '1000' THEN '9999' ELSE trim(CODIGO) END as CODIGO,");
            if (nivel==3){            
                sql.append(" NOME AS DESCRI FROM DEPARTAMENTOS ORDER BY 1 DESC                            ");
            }else{
                sql.append(" NOME AS DESCRI FROM DEPARTAMENTOS ORDER BY 1                                 ");                
            }
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2)  {
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    if (rst.getString("CODIGO").length()==4){
                        mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(2, 4));                                                
                    }else{
                        mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));                                                                        
                    }
                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    if (rst.getString("CODIGO").length()==4){
                        mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(2, 4));                                                
                        mercadologico3 = Integer.parseInt(rst.getString("CODIGO").substring(2, 4));                                                                        
                    }else if (rst.getString("CODIGO").length()==6){
                        mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(2, 4));                                                                        
                        mercadologico3 = Integer.parseInt(rst.getString("CODIGO").substring(4, 6));                                                                                                
                    }else{
                        mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));                                                                        
                        mercadologico3 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));                                                                                                
                    }

                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        

    public Map<Integer, ProdutoVO> carregarProdutoSoftaEx() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null, sql2 = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
            ncm1, ncm2, ncm3, codigoBalanca, referencia = -1;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double CodigoAnterior=0,precoVenda=0,custo=0,margem=0,strCodigoBarras;
        try {

            stmPostgres = Conexao.createStatement();
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT cast(codbarras as numeric(14,0)) AS CODIGOBARRAS, ");
            sql.append("         COALESCE(P.TIPO,'UN') AS TIPO, ");
            sql.append("         P.PER_ICMS_E as ALIQUOTAICMS, ");
            sql.append("         P.*  ");
            sql.append(" FROM top_018 P ");
            rst = stm.executeQuery(sql.toString());
            

            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();
                idSituacaoCadastro = 1;
 
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;
                
                idProduto = Integer.parseInt(rst.getString("CODIGO"));
                
                sql2 = new StringBuilder();
                sql2.append("select codigo, descricao, pesavel, validade ");
                sql2.append("from implantacao.produtobalanca ");
                sql2.append("where codigo = " + rst.getString("CODIGO").trim().replace(".", "").replace(",", ""));
                rstPostgres = stmPostgres.executeQuery(sql2.toString());

                if (rstPostgres.next()) {
                    eBalanca=true;
                    codigoBalanca = rstPostgres.getInt("codigo");
                    if ("CX".equals(rst.getString("TIPO").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("TIPO").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("TIPO").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    pesavel = false;
                    if ("CX".equals(rst.getString("TIPO").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("TIPO").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("TIPO").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }                    
                }
                
                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    if ("".equals(descriaoCompleta)){
                        descriaoCompleta = "PRODUTO SEM DESCRIÇÃO "+idProduto;
                    }
                } else {
                    descriaoCompleta = "";
                }
                if ((rst.getString("DESC_CF") != null)
                        && (!rst.getString("DESC_CF").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESC_CF");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = descriaoCompleta;
                }

                descricaoGondola = descricaoReduzida;
                qtdEmbalagem = 1;
                idFamilia = -1;
                
                mercadologico1 = 14;
                mercadologico3 = 1;                    
                mercadologico2 = 1;
                
                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {               
                    ncmAtual = util.formataNumero(rst.getString("NCM"));
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try{
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

                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {

                    if ((rst.getString("CODIGOBARRAS") != null)
                            && (!rst.getString("CODIGOBARRAS").trim().isEmpty())) {

                        strCodigoBarras = Long.parseLong(util.acertarTexto(rst.getString("CODIGOBARRAS").trim()));

                        if (String.valueOf(strCodigoBarras).length() < 7) {
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13((int) idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13((int) idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(rst.getString("CODIGOBARRAS").trim());
                        }
                    }
                }
                
                idTipoPisCofins = 1;
                idTipoPisCofinsCredito = 13;

                if ((rst.getString("STF") != null)
                        && (!rst.getString("STF").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMS(rst.getString("STF"), rst.getDouble("ALIQUOTAICMS"));
                    if ((idAliquota==0) && (idAliquota==1) && (idAliquota==2) && (idAliquota==3)){
                        tipoNaturezaReceita = 0;                                        
                    }else{
                        tipoNaturezaReceita = 999;                                                                
                    }
                } else {
                    idAliquota = 8;
                    tipoNaturezaReceita = 999;                    
                }
                
                if ((rst.getString("VL_CUSTO") != null)
                        && (!rst.getString("VL_CUSTO").trim().isEmpty())) {
                    custo = rst.getDouble("VL_CUSTO");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("VL_UNITAR") != null)
                        && (!rst.getString("VL_UNITAR").trim().isEmpty())) {
                    precoVenda = rst.getDouble("VL_UNITAR");
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("PER_VENDA") != null)
                        && (!rst.getString("PER_VENDA").trim().isEmpty())) {
                    margem = rst.getDouble("PER_VENDA");
                } else {
                    margem = 0;
                }                

                if (descriaoCompleta.length() > 60) {
                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.id = idProduto;
                oProduto.idDouble = CodigoAnterior;
                oProduto.descricaoCompleta = descriaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
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

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

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

                oCodigoAnterior.codigoanterior = CodigoAnterior;
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.barras = codigoBarras;

                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofins;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCredito;
                oCodigoAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                oCodigoAnterior.ncm = String.valueOf(ncm1);
 
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
                
                Contador++;
            }
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }    

    public Map<Double, ProdutoVO> carregarCustoProdutoSoftaEx(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder(); 
            sql.append(" SELECT CODIGO, COALESCE(VL_CUSTO,0) AS VL_CUSTO FROM top_018 ");            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO").replace(".", ""));
                custo = Double.parseDouble(rst.getString("VL_CUSTO").replace(",", "."));
                
                ProdutoVO oProduto = new ProdutoVO();
                //oProduto.id = idProduto;
                oProduto.idDouble = idProduto;                
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.custocomimposto = custo;                
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarPrecoProdutoSoftaEx(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, margem = 0, idProduto;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder(); 
            sql.append(" SELECT CODIGO, VL_UNITAR, COALESCE(PER_VENDA,0) AS PER_VENDA  FROM top_018 ");                        
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO"));
                preco = rst.getDouble("VL_UNITAR");
                
                if ((rst.getString("PER_VENDA") != null) &&
                        !rst.getString("PER_VENDA").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("PER_VENDA").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.margem = margem;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;
                
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Double, ProdutoVO> carregarEstoqueProdutoSoftaEx(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double idProduto, saldo = 0;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append(" SELECT CODIGO, COALESCE(QT_ESTOQUE,0) AS QT_ESTOQUE FROM top_018 ");               
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO"));
                saldo = rst.getDouble("QT_ESTOQUE");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();               
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Integer, ProdutoVO> carregarCodigoBarrasSoftaEx() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();  
            sql.append("SELECT P.CODIGO, ");
            sql.append("       CAST(codbarras2 AS NUMERIC(14,0)) AS CODIGOBARRAS ");
            sql.append("FROM top_018 P ");
            sql.append("WHERE TRIM(codbarras2) <> '' ");            

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODIGO"));

                if ((rst.getString("CODIGOBARRAS") != null) &&
                        (!rst.getString("CODIGOBARRAS").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("CODIGOBARRAS").replace(".", ""));
                } else {
                    codigobarras = 0;
                }
                
                if (String.valueOf(codigobarras).length() >= 7) {
                
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(idProduto, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }        
    
    public List<OfertaVO> carregarOfertaProdutoSoftaEx(int id_Loja) throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<OfertaVO> vOferta = new ArrayList<>();
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();            

            sql.append("SELECT promo.CODPROD, promo.DATAINI, promo.DATAFIM, promo.BARRA,   ");
            sql.append("       prod.PRECO_UNIT as PRECONORMAL, promo.PRECO_UNIT as  PRECOOFERTA ");
            sql.append("  FROM [GWOLAP].[dbo].PROMOCAO promo ");
            sql.append("INNER JOIN PRODUTOS prod ON ");
            sql.append("     prod.CODPROD = promo.CODPROD ");
            sql.append("WHERE promo.DATAFIM >= '2016-02-18' ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                OfertaVO oferta   = new OfertaVO();                
                oferta.id_loja    = id_Loja;
                oferta.id_produto = rst.getInt("CODPROD");
                oferta.datainicio = rst.getString("DATAINI");                
                oferta.datatermino = rst.getString("DATAFIM");                                
                oferta.precooferta = rst.getDouble("PRECOOFERTA");
                oferta.preconormal = rst.getDouble("PRECONORMAL");                
                vOferta.add(oferta);                
            }
            return vOferta;            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    // FIM PRODUTO    
    
    // INICIO CLIENTE
    public List<ClientePreferencialVO> carregarClientePreferencialSoftaEx(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String CPF, nome, endereco, bairro, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, dataNascimento, telefone, telefone2, numeroCasa, celular, observacao = "", cargo,
                empresa, foneEmpresa, nomePai, nomeMae, cargoConjuge, cpfConjuge, rgConjuge;
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao=0, id, agente, id_situacaocadastro = 0, Linha = 0, id_tipoestadocivil = 0, situacao = 0;
        
        Long cnpj, cep;
        double limite, salario, salarioConjuge;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append(" FROM TOP_007 ");
            sql.append(" WHERE SUBSTRING(HISTORICO FROM 1 FOR 6) = 'CLIENT' ");           
            rst = stm.executeQuery(sql.toString());
            try {
                while (rst.next()) {
                    Linha++;
                    Texto = "";
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("CODIGO");
                    CPF = util.formataNumero(rst.getString("CNPJ_CPF").trim().replace(".", "").replace("-", "").trim());
                    if ((CPF != "0") && (CPF != null)) {
                        if (CPF.length() == 11) {
                            id_tipoinscricao = 1; // PESSOA FISICA
                        } else if (CPF.length() == 14) {
                            id_tipoinscricao = 0; // PESSOA JURIDICA                          
                        }else{
                            id_tipoinscricao = 1; // PESSOA FISICA                            
                        }
                    }else{
                        id_tipoinscricao = 1;
                    }
                    
                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = util.acertarTexto(textoAcertado.replace("'", "").trim());
                        if (nome.length()>40){
                            nome = nome.substring(0,40);
                        }
                    } else {
                        nome = "SEM NOME VR " + id;
                    }
                    
                    Texto = id + " - " + nome; 
                    
                    if ((rst.getString("ENDERECO") != null)
                            && (!rst.getString("ENDERECO").isEmpty())) {                    
                        endereco = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                        
                        if (endereco.length() > 50) {
                            endereco = endereco.substring(0, 50);
                        }
                    }else{
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").isEmpty())) {                    
                        bairro = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                        bairro = bairro.replace("'", "").trim();                        
                    }else{
                        bairro = "";                        
                    }

                    if ((rst.getString("CIDADE") != null) && (rst.getString("ESTADO") != null)) {
                        id_estado = util.retornarEstadoDescricao(rst.getString("ESTADO").trim());
                        if (id_estado == 0) {
                            id_estado = 35; // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = util.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString().trim(), rst.getString("ESTADO").toString().trim());
                        if (id_municipio == 0) {
                            id_municipio = 3507506;// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = 35; // ESTADO ESTADO DO CLIENTE
                        id_municipio = 3507506; // CIDADE DO CLIENTE;                   
                    }
                    Texto = id + " - " + nome +" - cidade";                                                              
                    if (rst.getString("CEP") != null) {
                        cep = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                    } else {
                        cep = Long.parseLong("0");
                    }
                    
                    if (rst.getString("NUMERO") != null) {
                        numeroCasa = rst.getString("NUMERO");
                        if (numeroCasa.length()>6){
                            numeroCasa=numeroCasa.substring(1,6);
                        }
                    } else {                    
                        numeroCasa = "";
                    }
                    
                    if (rst.getString("EMAIL") != null) {
                        email = util.acertarTexto(rst.getString("EMAIL"));
                        if (email.length() > 50) {
                            email = email.substring(0, 50);
                        }
                    } else {
                        email = "";
                    }
                    
                    if (rst.getString("IE_RG") != null) {
                        inscricaoestadual = util.acertarTexto(rst.getString("IE_RG"));
                        inscricaoestadual = inscricaoestadual.replace(".", "").replace("/", "").replace(",", "");
                        if (inscricaoestadual.length() > 18) {
                            inscricaoestadual = inscricaoestadual.substring(0, 18);
                        }
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    if (rst.getString("CNPJ_CPF") != null) {
                        CPF = util.formataNumero(rst.getString("CNPJ_CPF").trim().replace(".", "").replace("-", "").trim());
                        if (CPF.length() >= 11) {
                            cnpj = Long.parseLong(CPF);
                        } else {
                            cnpj = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CODIGO"))));
                        }
                    } else {
                        cnpj = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CODIGO"))));
                    }
                    
                    id_sexo = 1;
                    dataResidencia = "1990/01/01";
                    dataCadastro = dataResidencia;                    
                    
                    if (rst.getString("VL_LIMITE") != null) {
                        limite = rst.getDouble("VL_LIMITE");
                    } else {
                        limite = 0;
                    }
                    if (rst.getString("TELEFONE") != null) {
                        telefone = util.formataNumero(rst.getString("TELEFONE"));
                    } else {
                        telefone = "";
                    }
                    
                    if (rst.getString("FAX") != null) {
                        telefone2 = util.formataNumero(rst.getString("FAX"));
                    } else {
                        telefone2 = "";
                    }
                    
                    if (rst.getString("CELULAR") != null) {
                        celular = rst.getString("CELULAR");
                    } else {
                        celular = "";
                    }
                    
                    salario = 0;
                    cargo = "";
                    empresa = "";
                    foneEmpresa = "";
                    situacao = 1; // ATIVO
                    id_tipoestadocivil = 0;
                    nomePai = "";
                    nomeMae = "";
                    nomeConjuge = "";
                    cargoConjuge = "";
                    rgConjuge = "";
                    salarioConjuge = 0;
                    observacao="";
                    
                    if (telefone.length() > 14) {
                        telefone = telefone.substring(0, 14);
                    }
                    
                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (celular.length() > 14) {
                        celular = celular.substring(0, 14);
                    }                    

                    oClientePreferencial.id = id;
                    oClientePreferencial.nome = nome;
                    oClientePreferencial.endereco = endereco;
                    oClientePreferencial.bairro = bairro;
                    oClientePreferencial.numero = numeroCasa;
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    oClientePreferencial.cep = cep;
                    oClientePreferencial.inscricaoestadual = inscricaoestadual;
                    oClientePreferencial.id_tipoestadocivil = id_tipoestadocivil;
                    oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                    oClientePreferencial.cnpj = cnpj;
                    oClientePreferencial.sexo = id_sexo;
                    oClientePreferencial.dataresidencia = dataResidencia;
                    oClientePreferencial.datacadastro = dataCadastro; 
                    oClientePreferencial.telefone = telefone;
                    oClientePreferencial.telefone2 = telefone2;
                    oClientePreferencial.celular = celular;
                    oClientePreferencial.nomeconjuge = nomeConjuge;
                    oClientePreferencial.cargoconjuge = cargoConjuge;
                    oClientePreferencial.rgconjuge = rgConjuge;
                    oClientePreferencial.salarioconjuge = salarioConjuge;
                    oClientePreferencial.email = email;
                    oClientePreferencial.valorlimite = limite;
                    oClientePreferencial.empresa = empresa;
                    oClientePreferencial.telefoneempresa = foneEmpresa;
                    //oClientePreferencial.codigoanterior = id;
                    oClientePreferencial.salario = salario;
                    oClientePreferencial.nomepai = nomePai;
                    oClientePreferencial.nomemae = nomeMae;
                    oClientePreferencial.cargo = cargo;
                    oClientePreferencial.id_situacaocadastro = situacao;
                    oClientePreferencial.observacao = observacao;
                    vClientePreferencial.add(oClientePreferencial);
                }
                stm.close();
            } catch (Exception ex) {
                throw ex;
            }
            return vClientePreferencial;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteSoftaEx(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int id_cliente, numerocupom;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        long cnpj;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT [GWOLAP].[dbo].CLIENTES.CNPJ_CPF,CODRECEBER, NUMTIT, [GWOLAP].[dbo].RECEBER.CODCLIE, ");
            sql.append("       NOTAECF, DTVENCTO, DTEMISSAO, DTPAGTO, VALOR, VALORJUROS, OBS ");
            sql.append("FROM [GWOLAP].[dbo].RECEBER ");
            sql.append("INNER JOIN [GWOLAP].[dbo].CLIENTES ON ");
            sql.append("[GWOLAP].[dbo].CLIENTES.CODCLIE = [GWOLAP].[dbo].RECEBER.CODCLIE ");
            sql.append("where UPPER(SITUACAO) = 'AB' and DTEMISSAO >='2002-12-01' order by DTEMISSAO             ");            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("CODCLIE");                
                dataemissao = rst.getString("DTEMISSAO");
                datavencimento = rst.getString("DTVENCTO");
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("NOTAECF")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = Double.parseDouble(rst.getString("VALORJUROS"));
                
                if ((rst.getString("OBS") != null) &&
                        (!rst.getString("OBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("OBS").replace("'", ""));
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
                if ((rst.getString("CNPJ_CPF")!=  null) &&
                            (!rst.getString("CNPJ_CPF").isEmpty())) {
                    cnpj = Long.parseLong(rst.getString("CNPJ_CPF").trim());
                }else{
                    cnpj = Long.parseLong("0");
                }
                
                oReceberCreditoRotativo.cnpjCliente = cnpj;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;
                
                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
            }
            
            return vReceberCreditoRotativo;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }        
    // INICIO CLIENTE    
    
    // INICIO FORNECEDOR    
    public List<FornecedorVO> carregarFornecedorSoftaEx() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro="", 
               telefone, telefone2, numero, email, celular;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        Long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT * ");
            sql.append(" FROM TOP_007 ");
            sql.append(" WHERE SUBSTRING(HISTORICO FROM 1 FOR 6) = 'FORNEC' ");
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    id = rst.getInt("CODIGO");

                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("SOBREFANTA") != null)
                            && (!rst.getString("SOBREFANTA").isEmpty())) {
                        byte[] bytes = rst.getBytes("SOBREFANTA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CNPJ_CPF").trim()));
                    } else {
                        cnpj = Long.parseLong(String.valueOf(id));
                    }

                    if ((rst.getString("IE_RG") != null)
                            && (!rst.getString("IE_RG").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("IE_RG").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("TIPO") != null)
                            && (!rst.getString("TIPO").isEmpty())) {
                        if ("F".equals(rst.getString("TIPO").substring(0,1))){
                            id_tipoinscricao = 1;                            
                        }else{
                            id_tipoinscricao = 0;                            
                        }
                    } else {
                        id_tipoinscricao = 0;
                    }                    


                    if ((rst.getString("ENDERECO") != null)
                            && (!rst.getString("ENDERECO").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("CEP").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").isEmpty())) {

                        if ((rst.getString("ESTADO") != null)
                                && (!rst.getString("ESTADO").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("CIDADE").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = 3530706;
                            }
                        }
                    } else {
                        id_municipio = 3530706;
                    }

                    if ((rst.getString("ESTADO") != null)
                            && (!rst.getString("ESTADO").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = 35;
                        }
                    } else {
                        id_estado = 35;
                    }

                    if (rst.getString("TELEFONE") != null) {
                        telefone = util.formataNumero(rst.getString("TELEFONE").trim());
                    } else {
                        telefone = "";
                    }
                    if (rst.getString("FAX") != null) {
                        telefone2 = util.formataNumero(rst.getString("FAX").trim());
                    } else {
                        telefone2 = "";
                    }
                    
                    if (rst.getString("EMAIL") != null) {
                        email = rst.getString("EMAIL").trim();
                    } else {
                        email = "";
                    }   
                    
                    if (rst.getString("CELULAR") != null) {
                        celular = rst.getString("CELULAR").trim();
                    } else {
                        celular = "";
                    }                      
                    
                    numero = "";
                    obs = "";
                    datacadastro = "";
                    pedidoMin = 0;
                           
                    if (rst.getString("BLOQUEIO") != null) {
                        if (!"SIM".equals(rst.getString("BLOQUEIO").trim())) {
                            ativo = true;
                        } else {
                            ativo = false;
                        }
                    } else {
                        ativo = true;
                    }
                    
                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }

                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 20) {
                        inscricaoestadual = inscricaoestadual.substring(0, 20);
                    }
                    
                    if (telefone.length() > 14) {
                        telefone = telefone.substring(0, 14);
                    }
                    
                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (celular.length() > 14) {
                        celular = celular.substring(0, 14);
                    }
                    
                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.bairro = bairro;
                    oFornecedor.numero = numero;                    
                    oFornecedor.telefone  = telefone;
                    oFornecedor.telefone2 = telefone2;                    
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.email = email;
                    oFornecedor.celular = celular;

                    vFornecedor.add(oFornecedor);
                }
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorSoftaEx() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor;
        double idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT P.codcliente, P.codigo FROM top_018 P ");
            sql.append("INNER JOIN top_007 F ON ");
            sql.append("    F.codigo = P.CODCLIENTE             ");
   

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("CODCLIENTE");
                idProduto = Double.parseDouble(rst.getString("CODIGO"));
                codigoExterno = "";

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }    
    // FIM FORNECEDOR        

    //IMPORTAÇÕES
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarFamiliaProdutoSoftaEx() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoSoftaEx();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologicoSoftaEx() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoSoftaEx(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoSoftaEx(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoSoftaEx(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoSoftaEx(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoSoftaEx();
            
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
            produto.salvar(vProdutoNovo, id_loja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }        
    
    public void importarCustoProdutoSoftaEx(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoSoftaEx(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Double keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarCustoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    
    
    public void importarPrecoProdutoSoftaEx(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoSoftaEx(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Double keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProdutoSoftaEx(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoSoftaEx(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Double keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }        
    
    public void importarOfertaProdutoSoftaEx(int id_Loja) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Oferta...");
            List<OfertaVO> vOferta = carregarOfertaProdutoSoftaEx(id_Loja);
            new OfertaDAO().salvar(vOferta, id_Loja);

        } catch (Exception ex) {

            throw ex;
        }
    }        
        
    public void importarCodigoBarraSoftaEx() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasSoftaEx();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Integer keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.addCodigoBarras(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }        

    public void importarClientePreferencialSoftaEx(int idLoja, int idLojaCliente) throws Exception {

        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");

            vCliente = carregarClientePreferencialSoftaEx(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);            
            new ClientePreferencialDAO().salvar(vCliente, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteSoftaEx(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteSoftaEx(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }    

    public void importarFornecedorSoftaEx() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorSoftaEx();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutoFornecedorSoftaEx() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorSoftaEx();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    
    // FUNÇÕES
    private int retornarAliquotaICMS(String codTrib, Double Aliquota) {
        int retorno;
        if ("000".equals(codTrib)) {
            if (Aliquota==7.0){
                retorno = 0;                
            }else if (Aliquota==12.0){
                retorno = 1;                
            }else if (Aliquota==18.0){
                retorno = 2;
            }else if (Aliquota==25.0){
                retorno = 3;                
            }else{
                retorno = 6;
            }
        } else if (("010".equals(codTrib))||
                   ("060".equals(codTrib))||
                   ("110".equals(codTrib))){
            retorno = 7;
        } else if (("020".equals(codTrib))||
                   ("040".equals(codTrib))||
                   ("050".equals(codTrib))||
                   ("070".equals(codTrib))) {
            retorno = 6;
        } else {
            retorno = 8;
        }
        return retorno;
    }     
}
