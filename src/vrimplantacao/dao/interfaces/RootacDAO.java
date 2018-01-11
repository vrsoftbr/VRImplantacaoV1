package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;  
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
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
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class RootacDAO {
    
    public String Texto;
    private ConexaoDBF connDBF = new ConexaoDBF();
    
    //CARREGAMENTOS
    private List<FamiliaProdutoVO> carregarFamiliaProduto(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao = "";
        int idFamilia = 0;
        Utils util = new Utils();
        
        try {           
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT DISTINCT CODIGOPLU, ESTC35DESC FROM RC003EST WHERE CODIGOPLU = ESTCCODPAI");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CODIGOPLU") != null) &&
                        (!rst.getString("CODIGOPLU").trim().isEmpty())) {
                    idFamilia = Integer.parseInt(rst.getString("CODIGOPLU").trim());

                    if ((rst.getString("ESTC35DESC") != null)
                            && (!rst.getString("ESTC35DESC").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("ESTC35DESC").trim().replace("'", ""));
                    } else {
                        descricao = "FAMILIA SEM DESCRICAO";
                    }
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.id = idFamilia;
                    oFamiliaProduto.descricao = descricao;
                    oFamiliaProduto.codigoant = idFamilia;

                    vFamiliaProduto.add(oFamiliaProduto);
                }
            }
            return vFamiliaProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(String i_arquivo, int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5;

        try {
            mercadologico1 = 0;
            mercadologico2 = 0;
            mercadologico3 = 0;
            mercadologico4 = 0;
            mercadologico5 = 0;            
            
            connDBF.abrirConexao(i_arquivo);
            stmPG = Conexao.createStatement();
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT COALESCE(GRUC03SETO,0) cod_m1,  ");
            sql.append("       COALESCE(GRUC03GRUP,0) cod_m2,  ");
            sql.append("       COALESCE(GRUC03SUBG,0) cod_m3,  ");
            sql.append("       COALESCE(GRUC03FAMI,0) cod_m4,  ");
            sql.append("       COALESCE(GRUC03SUBF,0) cod_m5,  ");
            sql.append("       GRUC35DESC desc_m               ");
            sql.append("FROM RC001GRU                          ");
            sql.append("ORDER BY GRUC03SETO, GRUC03GRUP,       "); 
            sql.append("         GRUC03SUBG, GRUC03FAMI,       ");
            sql.append("         GRUC03SUBF                    ");           
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();
                
                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                    descricao = util.acertarTexto(rst.getString("desc_m").trim(), 35, "DIVERSOS");
                    descricao = descricao.replace("??", "CA");
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = 1;
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 2) {
                    mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                    descricao = util.acertarTexto(rst.getString("desc_m").trim(),35,"DIVERSOS");
                    descricao = descricao.replace("??", "CA");                    
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 3) {
                    mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("cod_m3").trim());
                    descricao = util.acertarTexto(rst.getString("desc_m").trim(),35,"DIVERSOS");
                    descricao = descricao.replace("??", "CA");                    
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 4) {
                    mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("cod_m3").trim());
                    mercadologico4 = Integer.parseInt(rst.getString("cod_m4").trim());                        
                    descricao = util.acertarTexto(rst.getString("desc_m").trim(),35,"DIVERSOS");
                    descricao = descricao.replace("??", "CA");                    
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = mercadologico4;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 5) {
                    mercadologico1 = Integer.parseInt(rst.getString("cod_m1").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("cod_m2").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("cod_m3").trim());
                    mercadologico4 = Integer.parseInt(rst.getString("cod_m4").trim());                        
                    mercadologico5 = Integer.parseInt(rst.getString("cod_m5").trim());                                            
                    descricao = util.acertarTexto(rst.getString("desc_m").trim(),35,"DIVERSOS");
                    descricao = descricao.replace("??", "CA");                    
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = mercadologico4;
                    oMercadologico.mercadologico5 = mercadologico5;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                    vMercadologico.add(oMercadologico);
                }
            }
            
            stm.close();
            stmPG.close();
            connDBF.close();
            
            return vMercadologico;                                                
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null, sql2 = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins = 0, idTipoPisCofinsCredito = 0, tipoNaturezaReceita = 0,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, mercadologico4,
               mercadologico5, idSituacaoCadastro, ncm1, ncm2, ncm3, codigoBalanca, referencia = -1,
               idProduto=0, validade, pisCofinsDebitoAnt, pisCofinsCreditoAnt,cont=0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro,
               strSitTrib, valorIcms, strReducao, idProdutoBalanca="";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custoComImposto, custoSemImposto, margem, estoque;
        
        try {
            connDBF.abrirConexao(i_arquivo);           
            stm = connDBF.createStatement();                
            stmPostgres = Conexao.createStatement();              
            
            sql = new StringBuilder();
            sql.append(" SELECT CODIGOPLU, ESTCCODPAI,  estc03tipo AS UNIDADE, ESTC35DESC, ESTC13CODI, ESTC17RESU, ");
            sql.append(" PROCCODNCM, ESTC03SETO, ESTC03GRUP, ESTC03SUBG, ESTC03FAMI, ESTC03SUBF ");
            sql.append(" FROM RC003EST ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                if ((rst.getString("CODIGOPLU") != null) &&
                        (!rst.getString("CODIGOPLU").trim().isEmpty())) {
                    
                    eBalanca = false;
                    codigoBalanca = 0;
                    pesavel = false;
                    referencia = -1;
                    validade = 0;
                    ncmAtual = "";
                    idSituacaoCadastro = 1;
                    idProduto = (int) Double.parseDouble(rst.getString("CODIGOPLU").trim().replace(".", "").replace(",", ""));                    
                    idProdutoBalanca = util.formataNumero(rst.getString("CODIGOPLU").trim().substring(0, (rst.getString("CODIGOPLU").trim().length())-1));
                    sql2 = new StringBuilder();
                    sql2.append("select codigo, descricao, pesavel, validade ");
                    sql2.append("from implantacao.produtobalanca ");
                    sql2.append("where codigo = " + idProdutoBalanca);
                    rstPostgres = stmPostgres.executeQuery(sql2.toString());

                    if (rstPostgres.next()) {
                        eBalanca=true;
                        validade = rstPostgres.getInt("validade");
                        codigoBalanca = rstPostgres.getInt("codigo");
                        if ("CX".equals(rst.getString("UNIDADE").trim())) {
                            pesavel = false;
                            idTipoEmbalagem = 1;
                        } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                            pesavel = false;
                            idTipoEmbalagem = 4;
                        } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                            pesavel = true;
                            idTipoEmbalagem = 0;
                        } else {
                            pesavel = false;
                            idTipoEmbalagem = 4;
                        }
                    } else {
                        pesavel = false;
                        if ("CX".equals(rst.getString("UNIDADE").trim())) {
                            idTipoEmbalagem = 1;
                        } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                            idTipoEmbalagem = 4;
                        } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                            idTipoEmbalagem = 0;
                        } else {
                            idTipoEmbalagem = 0;
                        }         
                        validade = 0;                    
                    }                    
                    
                    if ((rst.getString("ESTCCODPAI") != null) &&
                            (!rst.getString("ESTCCODPAI").trim().isEmpty())) {
                        idFamilia = rst.getInt("ESTCCODPAI");
                    } else {
                        idFamilia = -1;
                    }                    
                    

                    if ((rst.getString("ESTC13CODI") != null) &&
                            (!rst.getString("ESTC13CODI").trim().isEmpty())) {
                        strCodigoBarras = util.formataNumero(rst.getString("ESTC13CODI").trim());
                    } else {
                        strCodigoBarras = "";
                    }

                    if ((rst.getString("ESTC35DESC") != null)
                            && (!rst.getString("ESTC35DESC").trim().isEmpty())) {
                        descriaoCompleta = util.acertarTexto(rst.getString("ESTC35DESC").trim().replace("'", ""));
                    } else {
                        descriaoCompleta = "";
                    }

                    if ((rst.getString("ESTC17RESU") != null)
                            && (!rst.getString("ESTC17RESU").trim().isEmpty())) {
                        descricaoReduzida = util.acertarTexto(rst.getString("ESTC17RESU").trim().replace("'", ""));
                    } else {
                        descricaoReduzida = "";
                    }

                    descricaoGondola = descriaoCompleta;

                    if ((rst.getString("PROCCODNCM") != null)
                            && (!rst.getString("PROCCODNCM").trim().isEmpty())) {

                        ncmAtual = util.formataNumero(rst.getString("PROCCODNCM").trim());

                        NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                        ncm1 = oNcm.ncm1;
                        ncm2 = oNcm.ncm2;
                        ncm3 = oNcm.ncm3;

                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    precoVenda = 0;
                    custoSemImposto = 0;
                    custoComImposto = 0;
                    margem = 0;
                    estoque = 0;
                    dataCadastro = "";
                    
                    if ((rst.getString("ESTC03SETO") != null) &&
                            (!rst.getString("ESTC03SETO").trim().isEmpty())) {
                        mercadologico1 = Integer.parseInt(rst.getString("ESTC03SETO").trim());
                    } else {
                        mercadologico1 = 0;
                    }
                    
                    if ((rst.getString("ESTC03GRUP") != null) &&
                            (!rst.getString("ESTC03GRUP").trim().isEmpty())) {
                        mercadologico2 = Integer.parseInt(rst.getString("ESTC03GRUP").trim());
                    } else {
                        mercadologico2 = 0;
                    }
                    
                    if ((rst.getString("ESTC03SUBG") != null) &&
                            (!rst.getString("ESTC03SUBG").trim().isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("ESTC03SUBG").trim());
                    } else {
                        mercadologico3 = 0;
                    }
                    
                    if ((rst.getString("ESTC03FAMI") != null) &&
                            (!rst.getString("ESTC03FAMI").trim().isEmpty())) {
                        mercadologico4 = Integer.parseInt(rst.getString("ESTC03FAMI").trim());
                    } else {
                        mercadologico4 = 0;
                    }                    
                     
                    if ((rst.getString("ESTC03SUBF") != null) &&
                            (!rst.getString("ESTC03SUBF").trim().isEmpty())) {
                        mercadologico5 = Integer.parseInt(rst.getString("ESTC03SUBF").trim());
                    } else {
                        mercadologico5 = 0;
                    }
                    
                    sql = new StringBuilder();
                    sql.append("select * from mercadologico ");
                    sql.append("where mercadologico1 = " + mercadologico1 + " ");
                    sql.append("and mercadologico2 = " + mercadologico2 + " ");
                    sql.append("and mercadologico3 = " + mercadologico3 + " ");
                    sql.append("and mercadologico4 = " + mercadologico4 + " ");
                    sql.append("and mercadologico5 = " + mercadologico5 + " ");                    
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (!rstPostgres.next()) {
                        
                        sql = new StringBuilder();
                        sql.append("select max(mercadologico1) as mercadologico1 ");
                        sql.append("from mercadologico ");
                        
                        rstPostgres = stmPostgres.executeQuery(sql.toString());
                        
                        if (rstPostgres.next()) {
                            mercadologico1 = rstPostgres.getInt("mercadologico1");
                            mercadologico2 = 1;
                            mercadologico3 = 1;
                            mercadologico4 = 0;
                            mercadologico5 = 0;                            
                        }
                    }

                    qtdEmbalagem = 1;

                    // codigobarras
                    if (eBalanca) {
                        codigoBarras = idProduto;
                    } else {
                        if (!strCodigoBarras.isEmpty()) {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                            if (String.valueOf(codigoBarras).length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else if (String.valueOf(codigoBarras).length() < 7) {
                                codigoBarras = -1;
                            }
                        } else {
                            codigoBarras = -1;
                        }
                    }

                    idTipoPisCofins = 1;
                    tipoNaturezaReceita = 999;
                    pisCofinsDebitoAnt = -1;
                    idTipoPisCofinsCredito = 13;
                    pisCofinsCreditoAnt = -1;
                    idAliquota = 8;

                    if (descriaoCompleta.length() > 60) {
                        descriaoCompleta = descriaoCompleta.substring(0, 60);
                    }

                    if (descricaoReduzida.length() > 22) {
                        descricaoReduzida = descricaoReduzida.substring(0, 22);
                    }

                    if (descricaoGondola.length() > 60) {
                        descricaoGondola = descricaoGondola.substring(0, 60);
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
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
                    oProduto.mercadologico4 = mercadologico4;
                    oProduto.mercadologico5 = mercadologico5;                    
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
                    oProduto.margem = margem;
                    oProduto.validade = validade;
                    oProduto.dataCadastro = dataCadastro;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oComplemento.custoComImposto = custoComImposto;
                    oComplemento.custoSemImposto = custoSemImposto;
                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                    oComplemento.estoque = estoque;
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

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;

                    if (!strCodigoBarras.trim().isEmpty()) {
                        oAnterior.barras = Long.parseLong(util.formataNumero(strCodigoBarras).trim());
                    } else {
                        oAnterior.barras = 0;
                    }

                    oAnterior.e_balanca = eBalanca;
                    oAnterior.codigobalanca = codigoBalanca;
                    oAnterior.referencia = referencia;
                    oAnterior.piscofinsdebito = pisCofinsDebitoAnt;
                    oAnterior.piscofinscredito = pisCofinsCreditoAnt;
                    oAnterior.ref_icmsdebito = "";


                    if (!ncmAtual.trim().isEmpty()) {
                        oAnterior.ncm = ncmAtual;
                    } else {
                        oAnterior.ncm = "";
                    }

                    oProduto.vCodigoAnterior.add(oAnterior);

                    vProduto.add(oProduto);
                }
            }
            stm.close();
            stmPostgres.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarCustoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double custoSemImposto = 0, custoComImposto = 0;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            
            sql = new StringBuilder();
            sql.append("select e.plu, e.ultprecust as custosemimposto, e.custobase, ");
            sql.append("e.vendavare, e.ultprecust, e.lucrovare, ");
            sql.append("((e.ultprecust - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            
            //sql.append(" order by cast(e.plu as integer) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("custosemimposto") != null)
                        && (!rst.getString("custosemimposto").trim().isEmpty())) {
                    custoSemImposto = Double.parseDouble(rst.getString("custosemimposto").trim());
                } else {
                    custoSemImposto = 0;
                }

                if ((rst.getString("custocomimposto") != null)
                        && (!rst.getString("custocomimposto").trim().isEmpty())) {
                    custoComImposto = Double.parseDouble(rst.getString("custocomimposto").trim());
                } else {
                    custoComImposto = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.custoComImposto = custoComImposto;
                oComplemento.custoSemImposto = custoSemImposto;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPrecoCustoEstoqueTributacao(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        Utils util = new Utils();
        try {
            int linha=0, idAliquotaICMS=8, idPisCofins=1, idPisCofinsCredito=13;
            double idProduto = 0, custoComImposto=0, custoSemImposto=0, margem=0, precoVenda=0, Estoque=0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();

            try {
                
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        custoComImposto=0;
                        custoSemImposto=0; 
                        margem=0; 
                        precoVenda=0; 
                        Estoque=0;
                        idAliquotaICMS=8; 
                        idPisCofins=1;
                        idPisCofinsCredito=13;                        
                        
                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else {
                        
                            if (linha == 3133) {
                                System.out.println("aqui");
                            }
                            
                            Cell cellIdProduto  = sheet.getCell(1, i);
                            Cell cellCusto      = sheet.getCell(8, i);
                            Cell cellMargem     = sheet.getCell(9, i);                                                        
                            Cell cellPrecoVenda = sheet.getCell(10, i);
                            Cell cellEstoque    = sheet.getCell(12, i);                            
                            
                            Cell cellPisCofins  = sheet.getCell(17, i);                                                        
                            Cell cellICMS       = sheet.getCell(18, i);                                                                                    
                            
                            idProduto       = Integer.parseInt(cellIdProduto.getContents());
                            if ((cellCusto.getContents().trim()!=null) &&
                                    (!cellCusto.getContents().trim().isEmpty())){
                                custoComImposto = Double.parseDouble(cellCusto.getContents().trim().replace(",", "."));                            
                                custoSemImposto = Double.parseDouble(cellCusto.getContents().trim().replace(",", "."));                            
                            }
                            if ((cellMargem.getContents().trim()!=null) &&
                                    (!cellMargem.getContents().trim().isEmpty())){
                                margem          = Double.parseDouble(cellMargem.getContents().trim().replace(",", "."));                            
                            }
                            if ((cellPrecoVenda.getContents().trim()!=null) &&
                                    (!cellPrecoVenda.getContents().trim().isEmpty())){
                                precoVenda      = Double.parseDouble(cellPrecoVenda.getContents().trim().replace(",", "."));                            
                            }
                            if ((cellEstoque.getContents().trim()!=null) &&
                                    (!cellEstoque.getContents().trim().isEmpty())){
                                Estoque         = Double.parseDouble(cellEstoque.getContents().trim().replace(",", "."));                            
                            }
                            
                            if ((cellPisCofins.getContents().trim()!=null) &&
                                    (!cellPisCofins.getContents().trim().isEmpty())){
                                idAliquotaICMS         = retornarICMS(cellICMS.getContents().trim().replace(",", "."));                            
                            }
                            
                            if ((cellICMS.getContents().trim()!=null) &&
                                    (!cellICMS.getContents().trim().isEmpty())){
                                idPisCofinsCredito  = retornaPisCofinsCredito(cellPisCofins.getContents().trim().replace(",", "."));                            
                                idPisCofins         = retornaPisCofinsDebito(cellPisCofins.getContents().trim().replace(",", "."));                                                            
                            }                            
                            
                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.id =(int) idProduto;
                            oProduto.codigoAnterior = idProduto;
                            oProduto.margem = margem;
                            oProduto.idTipoPisCofinsDebito = idPisCofins;
                            oProduto.idTipoPisCofinsCredito = idPisCofinsCredito;
                            oProduto.tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, null);
                            ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                            oComplemento.idLoja = idLoja;
                            oComplemento.precoVenda = precoVenda;
                            oComplemento.precoDiaSeguinte = precoVenda;
                            oComplemento.estoque = Estoque;                            
                            oComplemento.custoComImposto = custoComImposto;
                            oComplemento.custoSemImposto = custoSemImposto;
                            oComplemento.idAliquotaCredito = idAliquotaICMS;                            
                            
                            ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();

                            oProdutoAliquota.idAliquotaCredito = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaDebito = idAliquotaICMS;
                            oProdutoAliquota.idEstado = Global.idEstado;
                            oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaICMS;

                            oProduto.vAliquota.add(oProdutoAliquota);
                            
                            
                            oProduto.vComplemento.add(oComplemento);

                            CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                            oAnterior.codigoanterior = idProduto;
                            oAnterior.id_loja = idLoja;
                            oAnterior.precovenda = precoVenda;
                            oAnterior.custocomimposto = custoComImposto;
                            oAnterior.custosemimposto = custoSemImposto;

                            oProduto.vCodigoAnterior.add(oAnterior);

                            vProduto.add(oProduto);
                        }                        
                    }
                }
            
                return vProduto;
            } catch(Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
                        
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoVendaProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double precoVenda = 0, margem = 0;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            
            sql = new StringBuilder();
            sql.append("select e.plu, e.custo, ");
            sql.append("e.vendavare, e.ultprecust, e.lucrovare ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            
            //sql.append(" order by cast(e.plu as integer) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("vendavare") != null)
                        && (!rst.getString("vendavare").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("vendavare").trim());
                } else {
                    precoVenda = 0;
                }

                if ((rst.getString("lucrovare") != null)
                        && (!rst.getString("lucrovare").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("lucrovare").trim());
                } else {
                    margem = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }
            
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarras(String i_arquivo) throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;
        
        try {
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();            
            sql.append("SELECT CODIGOPLU, ESTC13CODI FROM RC077EAN"); 
            sql.append(" UNION ALL ");             
            sql.append("SELECT CODIGOPLU, DUNCCODBAR AS ESTC13CODI FROM RC003DUN");             

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODIGOPLU"));

                if ((rst.getString("ESTC13CODI") != null) &&
                        (!rst.getString("ESTC13CODI").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("ESTC13CODI").replace(".", ""));
                } else {
                    codigobarras = 0;
                }
                
                if (String.valueOf(codigobarras).length() >= 7) {
                
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigobarras;
                    oAutomacao.qtdEmbalagem = 1;
                    oProduto.vAutomacao.add(oAutomacao);
                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro, 
               Numero="",Telefone="",telefone2="", telefone3="", email ;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;

        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            
            sql.append(" SELECT F.*, (SELECT TOP 1 C.DDD+C.TELEFONE AS TELEFONE ");
            sql.append("              FROM RCTELTIP C WHERE C.CODIGO = F.CODIFABRIC AND ");
            sql.append("              C.TELEFONE <> '') AS TELEFONECONTATO     ");
            sql.append(" FROM RC008FOR F ");            
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            
            try {
                
                while (rst.next()) {
                    
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("CODIFABRIC");

                    Linha++;

                    if ((rst.getString("FORC35RAZA") != null)
                            && (!rst.getString("FORC35RAZA").isEmpty())) {
                        byte[] bytes = rst.getBytes("FORC35RAZA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("FORC10APEL") != null)
                            && (!rst.getString("FORC10APEL").isEmpty())) {
                        byte[] bytes = rst.getBytes("FORC10APEL");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("FORC15CGC") != null)
                            && (!rst.getString("FORC15CGC").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("FORC15CGC").trim()));
                        
                        if (String.valueOf(cnpj).length() > 11) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                        
                    } else {
                        cnpj = -1;
                        id_tipoinscricao = 0;
                    }
                    
                    if ((rst.getString("FORC19INSC") != null)
                            && (!rst.getString("FORC19INSC").isEmpty())) {
                        inscricaoestadual = util.formataNumero(rst.getString("FORC19INSC").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    

                    if ((rst.getString("FORC35ENDE") != null)
                            && (!rst.getString("FORC35ENDE").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("FORC35ENDE").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    Numero = "0";

                    if ((rst.getString("FORC20BAIR") != null)
                            && (!rst.getString("FORC20BAIR").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("FORC20BAIR").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("FORC08CEP") != null)
                            && (!rst.getString("FORC08CEP").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("FORC08CEP").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }

                    if ((rst.getString("FORC20CIDA") != null)
                            && (!rst.getString("FORC20CIDA").isEmpty())) {

                        if ((rst.getString("FORC02ESTA") != null)
                                && (!rst.getString("FORC02ESTA").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("FORC20CIDA").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("FORC02ESTA").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("FORC02ESTA") != null)
                            && (!rst.getString("FORC02ESTA").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("FORC02ESTA").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if ((rst.getString("FORC25FONE") != null) &&
                            (!rst.getString("FORC25FONE").trim().isEmpty())) {
                        Telefone = util.formataNumero(rst.getString("FORC25FONE"));
                    } else {
                        Telefone = "";
                    }
                    
                    if ((rst.getString("FORC25FONE") != null) &&
                            (!rst.getString("FORC25FONE").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("FORC25FONE"));
                    } else {
                        telefone2 = "";
                    }
                    
                    if ((rst.getString("FORC25FONE") != null) &&
                            (!rst.getString("FORC25FONE").trim().isEmpty())) {
                        telefone3 = util.formataNumero(rst.getString("TELEFONECONTATO"));
                    } else {
                        telefone3 = "";
                    }                    
                   
                    if ((rst.getString("FORCMAILTO") != null) &&
                            (!rst.getString("FORCMAILTO").trim().isEmpty()) &&
                            (rst.getString("FORCMAILTO").contains("@"))) {
                        email = util.acertarTexto(rst.getString("FORCMAILTO").trim().toLowerCase());
                    } else {
                        email = "";
                    }
                    
                    if (rst.getString("FORC40OBS1") != null) {
                        obs = rst.getString("FORC40OBS1").trim();
                    } else {
                        obs = "";
                    }

                    datacadastro = "";
                    ativo = true;
                    
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
                    
                    if (Telefone.length() > 14) {
                        Telefone = Telefone.substring(0, 14);
                    }
                    
                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }
                    
                    if (telefone3.length() > 14) {
                        telefone3 = telefone3.substring(0, 14);
                    }

                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.numero = Numero;
                    oFornecedor.telefone = Telefone;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.bairro = bairro;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.email = email;

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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            
            sql.append("SELECT CODIGOPLU, CODIFABRIC, ESTC08REFE, ESTN04QEMB FROM RC113REF");
            rst = stm.executeQuery(sql.toString());

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CODIGOPLU") != null) &&
                        (!rst.getString("CODIGOPLU").trim().isEmpty()) ) {
                
                    idFornecedor = rst.getInt("CODIFABRIC");
                    idProduto = rst.getInt("CODIGOPLU");
                    qtdEmbalagem = rst.getInt("ESTN04QEMB");

                    if ((rst.getString("ESTC08REFE") != null)
                            && (!rst.getString("ESTC08REFE").isEmpty())) {
                        codigoExterno = util.acertarTexto(rst.getString("ESTC08REFE").replace("'", ""));
                    } else {
                        codigoExterno = "";
                    }

                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                    oProdutoFornecedor.id_fornecedor = idFornecedor;
                    oProdutoFornecedor.id_produto = idProduto;
                    oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                    oProdutoFornecedor.dataalteracao = dataAlteracao;
                    oProdutoFornecedor.codigoexterno = codigoExterno;

                    vProdutoFornecedor.add(oProdutoFornecedor);
                
                }
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }    

    private List<ClienteEventualVO> carregarClienteEventual(String i_arquivo) throws Exception {
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, idEstado, idMunicipio = 0, idTipoInscricao;
        String nome, endereco, bairro, cidade, estado, telefone1, telefone2, 
               telefone3, email, dataNascimento, inscricaoEstadual, numero,
               complemento = "", dataCadastro, contato;
        long cnpj, cep = 0;
        double valorLimite;
        
        try {
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("select codigo, nome, razao, nascimento, inscest, cgc, cic, saldo, ");
            sql.append("rua, casa, edificio, apto, cidade, bairro, cep, estado, email, abertura, ");
            sql.append("contato, telefone1, telefone2, telefone3 ");
            sql.append("from cliente ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idCliente = Integer.parseInt(rst.getString("codigo").trim());
                
                nome = util.acertarTexto(rst.getString("razao").trim().replace("'", ""));
                
                if ((rst.getString("nascimento") != null) &&
                        (!rst.getString("nascimento").trim().isEmpty())) {
                    dataNascimento = rst.getString("nascimento").trim();
                } else {
                    dataNascimento = "";
                }
                
                if ((rst.getString("inscest") != null) &&
                        (!rst.getString("inscest").trim().isEmpty())) {
                    inscricaoEstadual = util.acertarTexto(rst.getString("inscest").trim().replace("'", ""));
                } else {
                    inscricaoEstadual = "ISENTO";
                }
                
                if ((rst.getString("cgc") != null) &&
                        (!rst.getString("cgc").trim().isEmpty())) {
                    
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));
                    
                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                } else {
                    
                    if ((rst.getString("cic") != null) &&
                            (!rst.getString("cic").trim().isEmpty())) {
                        
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cic").trim()));
                        
                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                        
                    } else {
                        cnpj = idCliente;
                        idTipoInscricao = 1;
                    }
                }
                
                if ((rst.getString("saldo") != null) &&
                        (!rst.getString("saldo").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("saldo").trim());
                } else {
                    valorLimite = 0;
                }
                
                if ((rst.getString("rua") != null) &&
                        (!rst.getString("rua").trim().isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("rua").trim().replace("'", ""));
                } else {
                    endereco = "";
                }
                
                if ((rst.getString("casa") != null) &&
                        (!rst.getString("casa").trim().isEmpty())) {
                    numero = util.acertarTexto(rst.getString("casa").trim().replace("'", ""));
                } else {
                    numero = "0";
                }
                
                if ((rst.getString("bairro") != null) &&
                        (!rst.getString("bairro").trim().isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }
                
                if ((rst.getString("edificio") != null) &&
                        (!rst.getString("edificio").trim().isEmpty())) {
                    
                    complemento = util.acertarTexto(rst.getString("edificio").trim()).trim().replace("'", "");
                    
                    if ((rst.getString("apto") != null) &&
                            (!rst.getString("apto").trim().isEmpty())) {
                        
                        complemento = util.acertarTexto(rst.getString("edificio").trim()+" "+rst.getString("apto")).trim().replace("'", "");
                    }
                } else {
                    complemento = "";
                }
                
                
                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").isEmpty())) {

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {

                        idMunicipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = 3538709;
                        }
                    }
                } else {
                    idMunicipio = 3538709;
                }

                if ((rst.getString("estado") != null)
                        && (!rst.getString("estado").isEmpty())) {
                    idEstado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = 35;
                    }
                } else {
                    idEstado = 35;
                }
                
                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                } else {
                    email = "";
                }
                
                if ((rst.getString("abertura") != null) &&
                        (!rst.getString("abertura").trim().isEmpty())) {
                    dataCadastro = rst.getString("abertura").trim();
                } else {
                    dataCadastro = "";
                }
                
                if ((rst.getString("contato") != null) &&
                        (!rst.getString("contato").trim().isEmpty())) {
                    contato = util.acertarTexto(rst.getString("contato").trim().replace("'", ""));                    
                } else {
                    contato = "";
                }
                
                if ((rst.getString("telefone1") != null) &&
                        (!rst.getString("telefone1").trim().isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("telefone1").trim());
                } else {
                    telefone1 = "0000000000";
                }
                
                if ((rst.getString("telefone2") != null) &&
                        (!rst.getString("telefone2").trim().isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("telefone2").trim());
                } else {
                    telefone2 = "";
                }
                
                if ((rst.getString("telefone3") != null) &&
                        (!rst.getString("telefone3").trim().isEmpty())) {
                    telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                } else {
                    telefone3 = "";
                }
                
                if ((rst.getString("email") != null) &&
                        (!rst.getString("email").trim().isEmpty()) &&
                        (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().replace("'", "").toLowerCase());
                } else {
                    email = "";
                }
                
                if ((rst.getString("cep") != null) &&
                        (!rst.getString("cep").trim().isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                } else {
                    cep = 0;
                }
                
                if (nome.length() > 60) {
                    nome = nome.substring(0, 60);
                }
                
                if (endereco.length() > 50) {
                    endereco = endereco.substring(0, 50);
                }
                
                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }
                   
                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (telefone2.length() > 14) {
                    telefone2 = telefone2.substring(0, 14);
                }
                
                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }
                
                if (inscricaoEstadual.length() > 20) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 20);
                }
                
                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }
                
                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }
                
                ClienteEventualVO oClienteEventual = new ClienteEventualVO();
                oClienteEventual.id = idCliente;
                oClienteEventual.nome = nome;
                oClienteEventual.endereco = endereco;
                oClienteEventual.bairro = bairro;
                oClienteEventual.id_estado = idEstado;
                oClienteEventual.telefone = telefone1;
                oClienteEventual.id_tipoinscricao = idTipoInscricao;
                oClienteEventual.inscricaoestadual = inscricaoEstadual;
                oClienteEventual.id_situacaocadastro = 1;
                oClienteEventual.cnpj = cnpj;
                oClienteEventual.fax = "";
                oClienteEventual.datacadastro = dataCadastro;
                oClienteEventual.limitecompra = valorLimite;
                oClienteEventual.id_municipio = idMunicipio;
                oClienteEventual.cep = cep;
                oClienteEventual.numero = numero;
                oClienteEventual.complemento = complemento;
                
                vClienteEventual.add(oClienteEventual);
                
            }
            
            
            return vClienteEventual;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, idEstado, idMunicipio = 0, idTipoInscricao;
        String nome, endereco, bairro, cidade, estado, telefone1, telefone2, 
               telefone3, email, dataNascimento, inscricaoEstadual, numero,
               complemento = "", dataCadastro, contato, observacao = "",
               empresa="", cargo="", pai="", mae="";
        long cnpj, cep = 0;
        double valorLimite, salario;
        
        try {
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT * FROM RC042CLI");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idCliente = Integer.parseInt(rst.getString("CLIC05CLIE").trim());
                
                nome = util.acertarTexto(rst.getString("CLIC19RG").trim().replace("'", ""));
                
                if ((rst.getString("DATANASCIM") != null) &&
                        (!rst.getString("DATANASCIM").trim().isEmpty())) {
                    dataNascimento = rst.getString("DATANASCIM").trim();
                } else {
                    dataNascimento = "";
                }
                
                if ((rst.getString("CLIC19RG") != null) &&
                        (!rst.getString("CLIC19RG").trim().isEmpty())) {
                    inscricaoEstadual = util.acertarTexto(rst.getString("CLIC19RG").trim().replace("'", ""));
                } else {
                    inscricaoEstadual = "ISENTO";
                }
                
                if ((rst.getString("CLIC15CGC") != null) &&
                        (!rst.getString("CLIC15CGC").trim().isEmpty())) {
                    
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("CLIC15CGC").trim()));
                    
                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                }else{
                    idTipoInscricao = 0;                    
                    cnpj = Long.parseLong(util.formataNumero(String.valueOf(idCliente)));                    
                }
                
                if ((rst.getString("CLIN12LIMI") != null) &&
                        (!rst.getString("CLIN12LIMI").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("CLIN12LIMI").trim());
                } else {
                    valorLimite = 0;
                }
                
                if ((rst.getString("CLIC35ENDE") != null) &&
                        (!rst.getString("CLIC35ENDE").trim().isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("CLIC35ENDE").trim().replace("'", ""));
                } else {
                    endereco = "";
                }
                
                numero = "0";
                
                if ((rst.getString("CLIC20BAIR") != null) &&
                        (!rst.getString("CLIC20BAIR").trim().isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("CLIC20BAIR").trim().replace("'", ""));
                } else {
                    bairro = "";
                }
                
                complemento = "";
                
                if ((rst.getString("CLIC20CIDA") != null)
                        && (!rst.getString("CLIC20CIDA").isEmpty())) {

                    if ((rst.getString("CLIC02ESTA") != null)
                            && (!rst.getString("CLIC02ESTA").isEmpty())) {

                        idMunicipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("CLIC20CIDA").replace("'", "").trim()),
                                util.acertarTexto(rst.getString("CLIC02ESTA").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = Global.idMunicipio;
                        }
                    }
                } else {
                    idMunicipio = Global.idMunicipio;
                }

                if ((rst.getString("CLIC02ESTA") != null)
                        && (!rst.getString("CLIC02ESTA").isEmpty())) {
                    idEstado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("CLIC02ESTA").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = Global.idEstado;
                    }
                } else {
                    idEstado = Global.idEstado;
                }
                
                if ((rst.getString("CLIC08CEP") != null)
                        && (!rst.getString("CLIC08CEP").isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("CLIC08CEP").trim()));
                } else {
                    cep = Long.parseLong("0");
                }
                
                if ((rst.getString("CLICEMAIL") != null)
                        && (!rst.getString("CLICEMAIL").trim().isEmpty())
                        && (rst.getString("CLICEMAIL").contains("@"))) {
                    email = util.acertarTexto(rst.getString("CLICEMAIL").trim().toLowerCase());
                } else {
                    email = "";
                }
                
                dataCadastro = "";

                
                if ((rst.getString("CLIC18FONE") != null) &&
                        (!rst.getString("CLIC18FONE").trim().isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("CLIC18FONE").trim());
                } else {
                    telefone1 = "0000000000";
                }
                
                
                if ((rst.getString("contato") != null) &&
                        (!rst.getString("contato").trim().isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("contato").trim().replace("'", ""));                    
                } else {
                    telefone2 = "";
                }
                telefone3 = "";
                
                if ((rst.getString("CLICEMAIL") != null) &&
                        (!rst.getString("CLICEMAIL").trim().isEmpty()) &&
                        (rst.getString("CLICEMAIL").contains("@"))) {
                    email = util.acertarTexto(rst.getString("CLICEMAIL").trim().replace("'", "").toLowerCase());
                } else {
                    email = "";
                }
                
                if (nome.length() > 40) {
                    nome = nome.substring(0, 40);
                }
                
                if (endereco.length() > 50) {
                    endereco = endereco.substring(0, 50);
                }
                
                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }
                   
                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (telefone2.length() > 14) {
                    telefone2 = telefone2.substring(0, 14);
                }
                
                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }
                
                if (inscricaoEstadual.length() > 20) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 20);
                }
                
                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }
                
                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }
                
                if (pai.length() > 40) {
                    pai = pai.substring(0, 40);
                }
                
                if (mae.length() > 40) {
                    mae = mae.substring(0, 40);
                }
                
                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                oClientePreferencial.id             = idCliente;
                oClientePreferencial.codigoanterior = idCliente;                
                oClientePreferencial.nome           = nome;
                oClientePreferencial.endereco       = endereco;
                oClientePreferencial.bairro         = bairro;
                oClientePreferencial.id_estado      = idEstado;
                oClientePreferencial.telefone       = telefone1;
                oClientePreferencial.telefone2      = telefone2;
                oClientePreferencial.telefone3      = telefone3;
                oClientePreferencial.email          = email;
                oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                oClientePreferencial.inscricaoestadual = inscricaoEstadual;
                oClientePreferencial.id_situacaocadastro = 1;
                oClientePreferencial.cnpj = cnpj;
                oClientePreferencial.fax = "";
                oClientePreferencial.datacadastro = dataCadastro;
                oClientePreferencial.valorlimite = valorLimite;
                oClientePreferencial.id_municipio = idMunicipio;
                oClientePreferencial.cep = cep;
                oClientePreferencial.numero = numero;
                oClientePreferencial.complemento = complemento;
                oClientePreferencial.observacao = observacao;
                
                vClientePreferencial.add(oClientePreferencial);
                
            }
            
            return vClientePreferencial;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double estoque = 0;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.quantfisc ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("quantfisc") != null)
                        && (!rst.getString("quantfisc").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("quantfisc").trim());
                } else {
                    estoque = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, codigocli, terminal  ");
            sql.append("from receber ");
            sql.append("where vlrpago = 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                
                if ((rst.getString("codigocli") != null) &&
                        (!rst.getString("codigocli").trim().isEmpty())) {

                
                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR";
                    juros = 0;

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
                }
            }

            
            return vReceberCreditoRotativo;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoBaixado(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros, valorPago;
        String observacao, dataemissao, datavencimento, dataPagamento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, vlrpago, ");
            sql.append("codigocli, terminal, pagamento ");
            sql.append("from receber ");
            sql.append("where vlrpago > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                
                if ((rst.getString("codigocli") != null) &&
                        (!rst.getString("codigocli").trim().isEmpty()) &&
                        (rst.getString("pagamento") != null) &&
                        (!rst.getString("pagamento").trim().isEmpty())) {

                
                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR => CONTA BAIXADA";
                    juros = 0;
                    
                    dataPagamento = rst.getString("pagamento").trim();
                    valorPago = Double.parseDouble(rst.getString("vlrpago").trim());

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;
                    oReceberCreditoRotativo.dataPagamento = dataPagamento;
                    oReceberCreditoRotativo.valorPago = valorPago;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
                }
            }

            
            return vReceberCreditoRotativo;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto=0;
        long codigobarras=-1;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("id"));
                
                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto<=9999)) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }           
    
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

    public void importarFamiliaProduto(String i_arquivo) throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Família Produto...");
            
            vFamiliaProduto = carregarFamiliaProduto(i_arquivo);
            FamiliaProdutoDAO familiaProduto = new FamiliaProdutoDAO();
            familiaProduto.salvar(vFamiliaProduto);
            
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarMercadologico(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();        
        try {
            ProgressBar.setStatus("Carregando dados...mercadológico nível 1...");
            vMercadologico = carregarMercadologico(i_arquivo, 1);            
            new MercadologicoDAO().salvar(vMercadologico, true, false);
            
            ProgressBar.setStatus("Carregando dados...mercadológico nível 2...");
            vMercadologico = carregarMercadologico(i_arquivo, 2);
            new MercadologicoDAO().salvar(vMercadologico, false, false);
            
            ProgressBar.setStatus("Carregando dados...mercadológico nível 3...");
            vMercadologico = carregarMercadologico(i_arquivo, 3);
            new MercadologicoDAO().salvar(vMercadologico, false, false);

            ProgressBar.setStatus("Carregando dados...mercadológico nível 4...");
            vMercadologico = carregarMercadologico(i_arquivo, 4);
            new MercadologicoDAO().salvar(vMercadologico, false, false);

            ProgressBar.setStatus("Carregando dados...mercadológico nível 5...");
            vMercadologico = carregarMercadologico(i_arquivo, 5);
            new MercadologicoDAO().salvar(vMercadologico, false, false);
            
            MercadologicoDAO mercDAO = new MercadologicoDAO();            
            mercDAO.temNivel4 = true;
            mercDAO.temNivel5 = true;            
            mercDAO.salvarMax();
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarProduto(String i_arquivo, int idLoja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            
            vProdutoNovo = carregarProduto(i_arquivo);
            
            ProgressBar.setMaximum(vProdutoNovo.size());
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.usarMercadoligicoProduto = true;
            produtoDAO.salvar(vProdutoNovo, idLoja, vLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarCusto(String i_arquivo, int idLoja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoCusto = new ArrayList<>();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Custo...Produtos...");
            vProdutoCusto = carregarCustoProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoCusto.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarCustoProduto(vProdutoCusto, idLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarArquivoTributacaoPrecoCustoEstoque(String i_arquivo, int idLoja, int idLojaCliente,int Tipo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dadoss...");
            vProduto = carregarPrecoCustoEstoqueTributacao(i_arquivo, idLoja);
            ProgressBar.setMaximum(vProduto.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarPrecoCustoTributacaoEstoque(vProduto, idLoja, Tipo);
            
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    public void importarPrecoVenda(String i_arquivo, int idLoja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoPrecoVenda = new ArrayList<>();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Preço Venda...Produtos...");
            vProdutoPrecoVenda = carregarPrecoVendaProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoPrecoVenda.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarPrecoProduto(vProdutoPrecoVenda, idLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarEstoque(String i_arquivo, int idLoja, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoEstoque = new ArrayList<>();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Preço Venda...Produtos...");
            vProdutoEstoque = carregarEstoqueProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoEstoque.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarEstoqueProduto(vProdutoEstoque, idLoja);;
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarFornecedor(String i_arquivo) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor(i_arquivo);

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
        
    }
    
    public void importarProdutoFornecedor(String i_arquivo) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);

            new ProdutoFornecedorDAO().alterarQtdEmbalagem(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarClienteEventual(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Eventual...");
            List<ClienteEventualVO> vClienteEventual = carregarClienteEventual(i_arquivo);
            
            new ClienteEventuallDAO().acertarCnpj(vClienteEventual);//salvar(vClienteEventual);
        } catch(Exception ex) {
            throw ex;
        }
    } 

    public void importarClientePreferencial(String i_arquivo, int id_loja, int id_lojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(i_arquivo);
            
            new PlanoDAO().salvar(1);
            new ClientePreferencialDAO().salvar(vClientePreferencial, id_loja, id_lojaCliente);
        } catch(Exception ex) {
            throw ex;
        }
    } 
    
    public void importarReceberCreditoRotativo(String i_arquivo, int idLoja) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(i_arquivo, idLoja);
            
            
            new ReceberCreditoRotativoDAO().salvar(vReceberCreditoRotativo, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativoBaixado(String i_arquivo, int idLoja) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo Baixado...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativoBaixado(i_arquivo, idLoja);
            
            
            new ReceberCreditoRotativoDAO().salvarContaBaixada(vReceberCreditoRotativo, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }
        
    public void importarCodigoBarra(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarras(i_arquivo);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Long keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.addCodigoBarras(vProdutoNovo);
            
        } catch(Exception ex) {
            
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
    
    private int retornarICMS(String codSitTrib) {
        int retorno=8;
        codSitTrib = codSitTrib.trim().toUpperCase();
        if (null != codSitTrib)switch (codSitTrib) {
            case "ISENTO":
                retorno = 6;
                break;
            case "SUBST. TRIB. 12% P/07% RE":
            case "SUBST. TRIB. 18% P/12% RE":
            case "SUBST.TRIB. 18%":
                retorno = 7;
                break;
            case "TRIB 66.67%":
            case "TRIB.18% P/07% RED.61,11%":
                retorno = 4;
                break;
            case "TRIB.12% P/07% RED.41,67":
                retorno = 5;
                break;
            case "TRIB.18% P/12% RED.33,33":
                retorno = 11;
                break;
            case "TRIBUTADO 07%":
                retorno = 1;
                break;
            case "TRIBUTADO 12%":
                retorno = 2;
                break;
            case "TRIBUTADO 18%":
                retorno = 3;
                break;
            case "TRIBUTADO 25%":
                retorno = 4;
                break;
            default:
                retorno = 8;
                break;
        }
        return retorno;
    }
    
    private int retornaPisCofinsDebito(String cst) {
        int retorno = 1;
        cst = cst.trim().toUpperCase();         
        if (null != cst) switch (cst) {
            case "0-TRIBUTADO":
                retorno = 0;
                break;
            case "1-SUBSTITUTO":
                retorno = 2;
                break;
            case "2-MONOFASICO":
            case "4-MONOFASICO":
                retorno = 3;
                break;
            case "3-ALIQ ZERO":
                retorno = 7;
                break;
            default:
                retorno = 1;
                break;
        }
        return retorno;
    }

    private int retornaPisCofinsCredito(String cst) {
        int retorno = 13;
        cst = cst.trim().toUpperCase();
        if (null != cst) switch (cst) {
            case "0-TRIBUTADO":
                retorno = 12;
                break;
            case "1-SUBSTITUTO":
                retorno = 17;
                break;
            case "2-MONOFASICO":
            case "4-MONOFASICO":
                retorno = 15;
                break;
            case "3-ALIQ ZERO":
                retorno = 19;
                break;
            default:
                retorno = 13;
                break;
        }
        return retorno;
    }

    public void importarQtdEmbalagem(String i_arquivo) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);

            new ProdutoFornecedorDAO().alterarQtdEmbalagem(vProdutoFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
