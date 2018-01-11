package vrimplantacao.dao.interfaces;
import java.sql.Date;
import vrimplantacao.dao.cadastro.FornecedorDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.ConveniadoDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
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
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao.vo.vrimplantacao.ConveniadoVO;
import vrimplantacao.vo.vrimplantacao.ConveniadoTransacaoVO;
import vrimplantacao.dao.cadastro.ConveniadoTransacaoDAO;
import vrimplantacao.vo.vrimplantacao.ConveniadoServicoVO;

public class PCSistemasDAO {

    // CARREGAMENTOS
    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idFamilia;
        String descricao;

        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT DISTINCT(CODPRODPRINC) CODIGO, DESCRICAO ");
            sql.append("FROM pcprodut ");
            sql.append("WHERE CODPROD = CODPRODPRINC ");
            sql.append("AND DESCRICAO NOT LIKE '%EXCLU%' ");
            sql.append("ORDER BY DESCRICAO ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFamilia = Integer.parseInt(rst.getString("CODIGO").trim());
                descricao = util.acertarTexto(rst.getString("DESCRICAO").trim().replace("'", ""));

                if (descricao.length() > 40) {
                    descricao = descricao.substring(0, 40);
                }

                FamiliaProdutoVO oFamilia = new FamiliaProdutoVO();
                oFamilia.id = idFamilia;
                oFamilia.descricao = descricao;
                oFamilia.codigoant = idFamilia;

                vFamiliaProduto.add(oFamilia);
            }

            return vFamiliaProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";

        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT M1.CODEPTO COD_M1, M1.DESCRICAO DESC_M1, ");
            sql.append("M2.CODSEC COD_M2, M2.DESCRICAO DESC_M2, ");
            sql.append("'1' COD_M3, M2.DESCRICAO DESC_M3 ");
            sql.append("FROM PCDEPTO M1 ");
            sql.append("INNER JOIN PCSECAO M2 ON M2.CODEPTO = M1.CODEPTO ");
            sql.append("ORDER BY M1.CODEPTO, M2.CODSEC ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    descricao = util.acertarTexto(rst.getString("DESC_M1").replace("'", ""));

                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {

                    descricao = util.acertarTexto(rst.getString("DESC_M2").replace("'", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = rst.getInt("COD_M2");
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {

                    descricao = util.acertarTexto(rst.getString("DESC_M3").replace("'", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = rst.getInt("COD_M2");
                    oMercadologico.mercadologico3 = rst.getInt("COD_M3");
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }
            stm.close();
            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        Utils util = new Utils();
        int idProduto, idTipoEmbalagem, qtdEmbalagem, idSituacaoCadastro,
            idTipoPisCofinsDebito, idTipoPisCofinsCredito, tipoNaturezaReceita, validade,
            idFamilia, codigoBalanca, mercadologico1, mercadologico2, mercadologico3, 
            ncm1, ncm2, ncm3;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, dataCadastro,
               strCodigoBarras, strNcm = "";
        boolean pesavel, eBalanca;
        double pesoLiq, pesoBruto, margem = 0, custo;
        long codigoBarras;
        
        
        try {
            Conexao.begin();
            stmPG = Conexao.createStatement();
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT CODPROD, DV, DESCRICAO, EMBALAGEM, UNIDADE, PESOLIQ, PESOBRUTO, CODEPTO, CODSEC, ");
            sql.append("QTUNIT, CODFORNEC, DTCADASTRO, CODAUXILIAR, CODPRODPRINC, PERICM, CUSTOREP, NBM, ");
            sql.append("CODFILIAL, PERPIS, PERCOFINS, NATUREZAPRODUTO, DTEXCLUSAO, OBS, OBS2 ");
            sql.append("FROM PCPRODUT ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODPROD").trim());
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + idProduto);
                
                rstPG = stmPG.executeQuery(sql.toString());
                
                if (rstPG.next()) {
                    
                    eBalanca = true;
                    codigoBalanca = rstPG.getInt("codigo");
                    validade = rstPG.getInt("validade");
                    
                    if ("P".equals(rstPG.getString("pesavel").trim())) {
                        idTipoEmbalagem = 4;
                        pesavel = false;
                    } else {
                        idTipoEmbalagem = 0;
                        pesavel = true;
                    }
                } else {
                    
                    eBalanca = false;
                    codigoBalanca = -1;
                    validade = 0;
                    idTipoEmbalagem = 0;
                    pesavel = false;
                }
                
                qtdEmbalagem = Integer.parseInt(rst.getString("QTUNIT").trim());
                
                if ((rst.getString("DESCRICAO") != null) &&
                        (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");               
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoCompleta = "PRODUTO SEM DESCRICAO";
                }
                
                descricaoReduzida = descricaoCompleta;
                descricaoGondola = descricaoCompleta;
                
                pesoLiq = Double.parseDouble(rst.getString("PESOLIQ").trim());
                pesoBruto = Double.parseDouble(rst.getString("PESOBRUTO").trim());
                
                if ((rst.getString("NBM") != null) &&
                        (!rst.getString("NBM").trim().isEmpty())) {
                    
                    strNcm = util.formataNumero(rst.getString("NBM").trim());

                    NcmVO oNcm = new NcmDAO().validar(strNcm);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;
                    
                    
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }
                
                if ((rst.getString("CODPRODPRINC") != null) &&
                        (!rst.getString("CODPRODPRINC").trim().isEmpty())) {
                    
                    idFamilia = Integer.parseInt(rst.getString("CODPRODPRINC").trim());
                    
                    sql = new StringBuilder();
                    sql.append("select id from familiaproduto ");
                    sql.append("where id = " + idFamilia);
                    
                    rstPG = stmPG.executeQuery(sql.toString());
                    
                    if (!rstPG.next()) {
                        idFamilia = -1;
                    }
                    
                } else {
                    idFamilia = -1;
                }
                
                if ((rst.getString("CODEPTO") != null) &&
                        (!rst.getString("CODEPTO").trim().isEmpty()) &&
                        (rst.getString("CODSEC") != null) &&
                        (!rst.getString("CODSEC").trim().isEmpty())) {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("CODEPTO").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("CODSEC").trim());
                    mercadologico3 = 1;
                    
                    sql = new StringBuilder();
                    sql.append("select m.mercadologico1, m.mercadologico2, m.mercadologico3 ");
                    sql.append("from mercadologico m, implantacao.codigoanterior_mercadologico ant ");
                    sql.append("where m.mercadologico1 = ant.mercadologico1_atual ");
                    sql.append("and m.mercadologico2 = ant.mercadologico2_atual ");
                    sql.append("and ant.mercadologico1_anterior = " + mercadologico1 + " ");
                    sql.append("and ant.mercadologico2_anterior = " + mercadologico2 + " ");

                    rstPG = stmPG.executeQuery(sql.toString());

                    if (!rstPG.next()) {
                        mercadologico1 = 42;
                        mercadologico2 = 1;
                        mercadologico3 = 1;
                    } else {                        
                        mercadologico1 = rstPG.getInt("mercadologico1");
                        mercadologico2 = rstPG.getInt("mercadologico2");
                        mercadologico3 = rstPG.getInt("mercadologico3");                        
                    }
                    
                } else {                    
                    mercadologico1 = 42;
                    mercadologico2 = 1;
                    mercadologico3 = 1;                    
                }
                
                if ((rst.getString("DTCADASTRO") != null) &&
                        (!rst.getString("DTCADASTRO").isEmpty())) {
                    dataCadastro = rst.getString("DTCADASTRO").trim();
                } else {
                    dataCadastro = "";
                }
                
                if ((rst.getString("DTEXCLUSAO") != null) &&
                        (!rst.getString("DTEXCLUSAO").trim().isEmpty())) {
                    idSituacaoCadastro = 0;
                } else {
                    idSituacaoCadastro = 1;
                }
                
                idSituacaoCadastro = 1;
                
                if ((rst.getString("CODAUXILIAR") != null) &&
                        (!rst.getString("CODAUXILIAR").trim().isEmpty())) {
                    
                    strCodigoBarras = util.formataNumero(rst.getString("CODAUXILIAR").trim());
                } else {
                    strCodigoBarras = "";
                }
                
                
                if (eBalanca) {
                
                    codigoBarras = idProduto;
                    
                } else {
                
                    if ((strCodigoBarras != null)
                            && (!strCodigoBarras.trim().isEmpty())) {

                        if (strCodigoBarras.length() >= 7) {
                            codigoBarras = Long.parseLong(util.formataNumero(strCodigoBarras.trim()));
                        } else if (strCodigoBarras.length() > 14) {
                            codigoBarras = Long.parseLong(util.formataNumero(strCodigoBarras.substring(0, 14).trim()));
                        } else {
                            codigoBarras = -1;
                        }
                        
                    } else {
                        codigoBarras = -1;
                    }                
                }
                
                idTipoPisCofinsDebito = 1;
                idTipoPisCofinsCredito = 13;
                tipoNaturezaReceita = 999;
                
                if ((rst.getString("CUSTOREP") != null) &&
                        (!rst.getString("CUSTOREP").trim().isEmpty())) {
                    
                    custo = Double.parseDouble(rst.getString("CUSTOREP").trim());
                } else {
                    custo = 0;
                }
                
                if (descricaoCompleta.length() > 60) {

                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {

                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {

                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
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
                oProduto.margem = margem;
                oProduto.validade = validade;
                oProduto.dataCadastro = dataCadastro;
                oProduto.pesoLiquido = pesoLiq;
                oProduto.pesoBruto = pesoBruto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = 26;
                oAliquota.idAliquotaDebito = 8;
                oAliquota.idAliquotaCredito = 8;
                oAliquota.idAliquotaDebitoForaEstado = 8;
                oAliquota.idAliquotaCreditoForaEstado = 8;
                oAliquota.idAliquotaDebitoForaEstadoNF = 8;
                oProduto.vAliquota.add(oAliquota);
                
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                
                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oAnterior.codigoanterior = idProduto;
                
                if ((rst.getString("CODAUXILIAR") != null) &&
                        (!rst.getString("CODAUXILIAR").trim().isEmpty())) {
                    
                    oAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("CODAUXILIAR").trim()));
                } else {
                    oAnterior.barras = -1;
                }
                
                oAnterior.custocomimposto = custo;
                oAnterior.custosemimposto = custo;
                
                if ((rst.getString("NBM") != null) &&
                        (!rst.getString("NBM").trim().isEmpty())) {
                    oAnterior.ncm = strNcm.trim();
                } else {
                    oAnterior.ncm = "";
                }
                
                oAnterior.e_balanca = eBalanca;
                oAnterior.codigobalanca = codigoBalanca;
                
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
                
            }
            
            Conexao.commit();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPrecoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int idProduto;
        double precoVenda;
        
        try {
            
            Conexao.begin();
            
            stmPG = Conexao.createStatement();
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT CODPROD, PVENDA FROM PCEMBALAGEM ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODPROD").trim());
                
                
                    if ((rst.getString("PVENDA") != null)
                            && (!rst.getString("PVENDA").trim().isEmpty())) {

                        precoVenda = Double.parseDouble(rst.getString("PVENDA").trim());
                    } else {
                        precoVenda = 0;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.margem = 0;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oProduto.vComplemento.add(oComplemento);

                    vProduto.add(oProduto);
                
                
            }
            
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPisCofinsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoPisCofinsDebito, idTipoPisCofinsCredito, 
            tipoNaturezaReceita;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT CODPROD, PERPIS, PERCOFINS, CODSITTRIBPISCOFINS ");
            sql.append("FROM PCPRODFILIAL where codfilial = 1 ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODPROD").trim());
                
                if ((rst.getString("CODSITTRIBPISCOFINS") != null) &&
                        (!rst.getString("CODSITTRIBPISCOFINS").trim().isEmpty())) {
                    
                    if (("7".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("71".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 1;
                        idTipoPisCofinsCredito = 13;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("4".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("70".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 3;
                        idTipoPisCofinsCredito = 15;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("49".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("99".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 9;
                        idTipoPisCofinsCredito = 21;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("8".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("74".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 8;
                        idTipoPisCofinsCredito = 20;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("5".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("75".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 2;
                        idTipoPisCofinsCredito = 14;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("2".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("60".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 5;
                        idTipoPisCofinsCredito = 17;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("3".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("51".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 6;
                        idTipoPisCofinsCredito = 18;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("6".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("73".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 7;
                        idTipoPisCofinsCredito = 19;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else if (("1".equals(rst.getString("CODSITTRIBPISCOFINS").trim())) ||
                            ("50".equals(rst.getString("CODSITTRIBPISCOFINS").trim()))) {
                        idTipoPisCofinsDebito = 0;
                        idTipoPisCofinsCredito = 12;
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    } else {
                        idTipoPisCofinsDebito = 1;
                        idTipoPisCofinsCredito = 13;
                        tipoNaturezaReceita = 999;                        
                    }
                } else {
                    idTipoPisCofinsDebito = 1;
                    idTipoPisCofinsCredito = 13;
                    tipoNaturezaReceita = 999;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                
                CodigoAnteriorVO oAnteiror = new CodigoAnteriorVO();
                
                if ((rst.getString("CODSITTRIBPISCOFINS") != null) &&
                        (!rst.getString("CODSITTRIBPISCOFINS").trim().isEmpty())) {
                    
                    oAnteiror.piscofinsdebito = Integer.parseInt(rst.getString("CODSITTRIBPISCOFINS").trim());
                    oAnteiror.piscofinscredito = Integer.parseInt(rst.getString("CODSITTRIBPISCOFINS").trim());
                    oAnteiror.naturezareceita = -1;
                } else {
                    oAnteiror.piscofinsdebito = -1;
                    oAnteiror.piscofinscredito = -1;
                    oAnteiror.naturezareceita = -1;                    
                }
                
                oProduto.vCodigoAnterior.add(oAnteiror);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idAliquota = 8;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select CODPROD, CODFILIALNF, UFDESTINO, CODST, DTULTALTER ");
            sql.append("from PCTABTRIB T1 ");
            sql.append("WHERE DTULTALTER = (SELECT MAX(DTULTALTER) ");
            sql.append("FROM PCTABTRIB T2 ");
            sql.append("WHERE T2.CODPROD = T1.CODPROD ");
            sql.append("AND T2.codfilialnf = 1 ");
            sql.append("AND T2.ufdestino = 'PE' ) ");
            sql.append("AND T1.CODFILIALNF = 1 ");
            sql.append("AND T1.ufdestino = 'PE' ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codprod").trim());
                
                if ((rst.getString("CODST") != null) &&
                        (!rst.getString("CODST").trim().isEmpty())) {
                    
                    if ("0".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("1".equals(rst.getString("CODST").trim())) {
                        idAliquota = 2;
                    } else if ("2".equals(rst.getString("CODST").trim())) {
                        idAliquota = 19;
                    } else if ("3".equals(rst.getString("CODST").trim())) {
                        idAliquota = 7;
                    } else if ("4".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("5".equals(rst.getString("CODST").trim())) {
                        idAliquota = 3;
                    } else if ("6".equals(rst.getString("CODST").trim())) {
                        idAliquota = 1;
                    } else if ("7".equals(rst.getString("CODST").trim())) {
                        idAliquota = 0;
                    } else if ("8".equals(rst.getString("CODST").trim())) {
                        idAliquota = 6;
                    } else if ("9".equals(rst.getString("CODST").trim())) {
                        idAliquota = 2;
                    } else if ("10".equals(rst.getString("CODST").trim())) {
                        idAliquota = 19;
                    } else if ("11".equals(rst.getString("CODST").trim())) {
                        idAliquota = 7;
                    } else if ("12".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("13".equals(rst.getString("CODST").trim())) {
                        idAliquota = 3;
                    } else if ("14".equals(rst.getString("CODST").trim())) {
                        idAliquota = 1;
                    } else if ("15".equals(rst.getString("CODST").trim())) {
                        idAliquota = 0;
                    } else if ("16".equals(rst.getString("CODST").trim())) {
                        idAliquota = 6;
                    } else if ("17".equals(rst.getString("CODST").trim())) {
                        idAliquota = 7;
                    } else if ("18".equals(rst.getString("CODST").trim())) {
                        idAliquota = 7;
                    } else if ("19".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("20".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("21".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("22".equals(rst.getString("CODST").trim())) {
                        idAliquota = 8;
                    } else if ("23".equals(rst.getString("CODST").trim())) {
                        idAliquota = 18;
                    } else {
                        idAliquota = 8;
                    }
                    
                } else {
                    idAliquota = 8;
                }
                
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                oProduto.vAliquota.add(oAliquota);
                
                CodigoAnteriorVO oAnteiror = new CodigoAnteriorVO();
                
                if ((rst.getString("CODST") != null) &&
                        (!rst.getString("CODST").trim().isEmpty())) {

                    oAnteiror.ref_icmsdebito = rst.getString("CODST").trim();

                } else {
                    oAnteiror.ref_icmsdebito = "";
                }
                
                oProduto.vCodigoAnterior.add(oAnteiror);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double estoque;
        
        try {
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select codprod, data, qtest, pericm, SITTRIBUT ");
            sql.append("from pchistest h ");
            sql.append("where data = (select max(h2.data) ");
            sql.append("from pchistest h2 ");
            sql.append("where h2.codprod = h.codprod ");
            sql.append("and h2.codfilial = "+idLoja+") ");
            sql.append("and h.codfilial = "+idLoja+" ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codprod").trim());
                
                if ((rst.getString("qtest") != null) &&
                        (!rst.getString("qtest").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("qtest").trim());
                } else {
                    estoque = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);
                
                vProduto.add(oProduto);
            }

            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialVO> carregarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, bairroEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;

        try {
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("SELECT CODCLI, CLIENTE, ENDERCOB, BAIRROCOB, TELCOB, MUNICCOB, ESTCOB, CEPCOB, ");
            sql.append("ENDERENT, BAIRROENT, TELENT, MUNICENT, ESTENT, CEPENT, CGCENT, IEENT, DTULTCOMP, ");
            sql.append("BLOQUEIO, FAXCLI, LIMCRED, OBS, DTCADASTRO, FANTASIA, OBS2, PONTOREFER, TIPOFJ, ");
            sql.append("TELENT1, EMAIL, DTEXCLUSAO, OBS3, OBS4, ENDERCOM, BAIRROCOM, MUNICCOM, ESTCOM, ");
            sql.append("CEPCOM, TELCOM, FAXCOM, OBSENTREGA1, OBSENTREGA2, OBSENTREGA3, NUMBANCO1, NUMAGENCIA1, ");
            sql.append("NUMCCORRENTE1, NUMBANCO2, NUMAGENCIA2, NUMCCORRENTE2, RG, ORGAORG, OBS5, EMPRESA, ");
            sql.append("ENDEREMPR, MUNICEMPR, ESTEMPR, TELEMPR, CARGO, DTADMISSAO, RENDAMENSAL, NOMECONJUGE, ");
            sql.append("CPFCONJUGE, EMPRESACONJUGE, ENDERCONJUGE, MUNICCONJUGE, ESTCONJUGE, TELCONJUGE, CARGOCONJUGE, ");
            sql.append("DTADMISSAOCONJUGE, RENDAMENSALCONJUGE, VLOUTRASRENDAS, TELCELENT, OBSERVACAO, DTNASC,  ");
            sql.append("FILIACAOPAI, FILIACAOMAE, SEXO, DTNASCCONJ, FILIACAOPAICONJ, FILIACAOMAECONJ,  ");
            sql.append("RGCONJ, ORGAORGCONJ, CODMUNICIPIO, OBSGERENCIAL1, OBSGERENCIAL2, OBSGERENCIAL3,  ");
            sql.append("COMPLEMENTOENT, CODCIDADE, COMPLEMENTOCOM, NUMEROCOM, COMPLEMENTOCOB, NUMEROENT, NUMEROCOB ");
            sql.append("FROM PCCLIENT ");
            sql.append("WHERE CODCOB <> 'CONV' ");

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("CODCLI");
                    dataResidencia = "1990/01/01";
                    
                    
                    if ((rst.getString("DTEXCLUSAO") != null)
                            && (!rst.getString("DTEXCLUSAO").trim().isEmpty())) {
                        id_situacaocadastro = 0;
                    } else {
                        id_situacaocadastro = 1;
                    }                    

                    if ((rst.getString("TIPOFJ") != null)
                            && (!rst.getString("TIPOFJ").trim().isEmpty())) {
                        if ("F".equals(rst.getString("TIPOFJ").trim())) {
                            id_tipoinscricao = 1;
                        } else if ("J".equals(rst.getString("TIPOFJ").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("CLIENTE") != null)
                            && (!rst.getString("CLIENTE").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("CLIENTE");
                        //String textoAcertado = new String(bytes, "ISO-8859-1");
                        //nome = util.acertarTexto(textoAcertado.replace("'", "").trim());
                        nome = rst.getString("CLIENTE");
                    } else {
                        nome = "SEM NOME VR " + id;
                    }

                    if ((rst.getString("ENDERENT") != null)
                            && (!rst.getString("ENDERENT").trim().isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("ENDERENT").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("CGCENT") != null)
                            && (!rst.getString("CGCENT").trim().isEmpty())
                            && (!"00.000.000/0001-91".equals(rst.getString("CGCENT")))) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CGCENT").trim()));
                    } else {
                        cnpj = id;
                    }

                    if ((rst.getString("BAIRROENT") != null)
                            && (!rst.getString("BAIRROENT").trim().isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRROENT").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("TELENT") != null)
                            && (!rst.getString("TELENT").trim().isEmpty())) {
                        telefone1 = util.formataNumero(rst.getString("TELENT").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("CEPENT") != null)
                            && (!rst.getString("CEPENT").trim().isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("CEPENT").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("MUNICENT") != null)
                            && (!rst.getString("MUNICENT").trim().isEmpty())) {
                        if ((rst.getString("ESTENT") != null)
                                && (!rst.getString("ESTENT").trim().isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("MUNICENT").trim().replace("'", "").toUpperCase()),
                                    rst.getString("ESTENT").trim().replace("'", ""));
                            if (id_municipio == 0) {
                                id_municipio = 2611002;
                            }
                        } else {
                            id_municipio = 2611002;
                        }
                    } else {
                        id_municipio = 2611002;
                    }

                    if ((rst.getString("ESTENT") != null)
                            && (!rst.getString("ESTENT").trim().isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(
                                rst.getString("ESTENT").trim().replace("'", "").toUpperCase());

                        if (id_estado == 0) {
                            id_estado = 26;
                        }
                    } else {
                        id_estado = 26;
                    }

                    numero = "0";

                    if ((rst.getString("COMPLEMENTOENT") != null)
                            && (!rst.getString("COMPLEMENTOENT").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("COMPLEMENTOENT"));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("LIMCRED") != null)
                            && (!rst.getString("LIMCRED").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMCRED"));
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("IEENT") != null)
                            && (!rst.getString("IEENT").trim().isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("IEENT").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "").replace("-", "").replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("DTCADASTRO") != null)
                            && (!rst.getString("DTCADASTRO").trim().isEmpty())) {
                        dataCadastro = rst.getString("DTCADASTRO").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }
                    if ((rst.getString("DTNASC") != null)
                            && (!rst.getString("DTNASC").trim().isEmpty())) {
                        dataNascimento = rst.getString("DTNASC").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }

                    if ((rst.getString("BLOQUEIO") != null)
                            && (!rst.getString("BLOQUEIO").trim().isEmpty())) {
                        if ("S".equals(rst.getString("BLOQUEIO"))) {
                            bloqueado = true;
                        } else {
                            bloqueado = false;
                        }
                    } else {
                        bloqueado = false;
                    }

                    if ((rst.getString("FILIACAOPAI") != null)
                            && (!rst.getString("FILIACAOPAI").trim().isEmpty())) {
                        nomePai = util.acertarTexto(rst.getString("FILIACAOPAI").trim().replace("'", ""));
                    } else {
                        nomePai = "";
                    }

                    if ((rst.getString("FILIACAOMAE") != null)
                            && (!rst.getString("FILIACAOMAE").trim().isEmpty())) {
                        nomeMae = util.acertarTexto(rst.getString("FILIACAOMAE").trim().replace("'", ""));
                    } else {
                        nomeMae = "";
                    }

                    if ((rst.getString("TELCELENT") != null)
                            && (!rst.getString("TELCELENT").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("TELCELENT").trim());
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("FAXCLI") != null)
                            && (!rst.getString("FAXCLI").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("FAXCLI").trim());
                    } else {
                        fax = "";
                    }

                    observacao = "";
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = util.acertarTexto(rst.getString("obs").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = observacao + "  " + util.acertarTexto(rst.getString("obs").replace("'", "").trim());
                    } else {
                        observacao = observacao + "";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())
                            && (rst.getString("EMAIL").contains("@"))) {
                        email = util.acertarTexto(rst.getString("EMAIL").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    id_sexo = 1;

                    if ((rst.getString("EMPRESA") != null)
                            && (!rst.getString("EMPRESA").trim().isEmpty())) {
                        empresa = util.acertarTexto(rst.getString("EMPRESA").trim().replace("'", ""));
                    } else {
                        empresa = "";
                    }

                    if ((rst.getString("ENDEREMPR") != null)
                            && (!rst.getString("ENDEREMPR").trim().isEmpty())) {
                        enderecoEmpresa = util.acertarTexto(rst.getString("ENDEREMPR").trim().replace("'", ""));
                    } else {
                        enderecoEmpresa = "";
                    }

                    if ((rst.getString("MUNICEMPR") != null)
                            && (!rst.getString("MUNICEMPR").trim().isEmpty())) {
                        bairroEmpresa = util.formataNumero(rst.getString("MUNICEMPR").trim());
                        if ((rst.getString("ESTEMPR") != null)
                                && (!rst.getString("ESTEMPR").trim().isEmpty())) {
                            bairroEmpresa = util.acertarTexto(bairroEmpresa +" "+util.formataNumero(rst.getString("ESTEMPR").trim()));                            
                        }
                    } else {
                        bairroEmpresa = "";
                    }

                    if ((rst.getString("CARGO") != null)
                            && (!rst.getString("CARGO").trim().isEmpty())) {
                        cargo = util.acertarTexto(rst.getString("CARGO").replace("'", "").trim());
                    } else {
                        cargo = "";
                    }

                    if ((rst.getString("VLOUTRASRENDAS") != null)
                            && (!rst.getString("VLOUTRASRENDAS").trim().isEmpty())) {
                        salario = Double.parseDouble(rst.getString("VLOUTRASRENDAS"));
                    } else {
                        salario = 0;
                    }

                    if ((rst.getString("NOMECONJUGE") != null)
                            && (!rst.getString("NOMECONJUGE").trim().isEmpty())) {
                        estadoCivil = 1; // CASADO
                    } else {
                        estadoCivil = 0;
                    }

                    if ((rst.getString("NOMECONJUGE") != null)
                            && (!rst.getString("NOMECONJUGE").trim().isEmpty())) {
                        conjuge = util.acertarTexto(rst.getString("NOMECONJUGE").trim().replace("'", ""));
                    } else {
                        conjuge = "";
                    }

                    if ((rst.getString("ORGAORG") != null)
                            && (!rst.getString("ORGAORG").trim().isEmpty())) {
                        orgaoExp = util.acertarTexto(rst.getString("ORGAORG").replace("'", "").trim());
                    } else {
                        orgaoExp = "";
                    }

                    if (nome.length() > 40) {
                        nome = nome.substring(0, 40);
                    }

                    if (conjuge.length() > 25) {
                        conjuge = conjuge.substring(0, 25);
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

                    if (telefone1.length() > 14) {
                        telefone1 = telefone1.substring(0, 14);
                    }

                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 18) {
                        inscricaoestadual = inscricaoestadual.substring(0, 18);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    if (observacao.length() > 80) {
                        observacao = observacao.substring(0, 80);
                    }
                    
                    if (cargo.length() > 25) {
                        cargo = cargo.substring(0, 25);
                    }                    
                    
                    if (empresa.length() > 35) {
                        empresa = empresa.substring(0, 35);
                    }                       
                    
                    if (bairroEmpresa.length() > 30) {
                        bairroEmpresa = bairroEmpresa.substring(0, 30);
                    }                         
                    
                    if (enderecoEmpresa.length() > 30) {
                        enderecoEmpresa = enderecoEmpresa.substring(0, 30);
                    }                         
                    
                    oClientePreferencial.id = id;
                    oClientePreferencial.nome = nome;
                    oClientePreferencial.endereco = endereco;
                    oClientePreferencial.bairro = bairro;
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    oClientePreferencial.cep = cep;
                    oClientePreferencial.telefone = telefone1;
                    oClientePreferencial.inscricaoestadual = inscricaoestadual;
                    oClientePreferencial.cnpj = cnpj;
                    oClientePreferencial.sexo = id_sexo;
                    oClientePreferencial.dataresidencia = dataResidencia;
                    oClientePreferencial.datacadastro = dataCadastro;
                    oClientePreferencial.email = email;
                    oClientePreferencial.valorlimite = limite;
                    oClientePreferencial.codigoanterior = id;
                    oClientePreferencial.fax = fax;
                    oClientePreferencial.bloqueado = bloqueado;
                    oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                    oClientePreferencial.celular = telefone2;
                    oClientePreferencial.observacao = observacao;
                    oClientePreferencial.datanascimento = dataNascimento;
                    oClientePreferencial.nomepai = nomePai;
                    oClientePreferencial.nomemae = nomeMae;
                    oClientePreferencial.empresa = empresa;
                    oClientePreferencial.telefoneempresa = telEmpresa;
                    oClientePreferencial.numero = numero;
                    oClientePreferencial.cargo = cargo;
                    oClientePreferencial.enderecoempresa = enderecoEmpresa;
                    oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                    oClientePreferencial.salario = salario;
                    oClientePreferencial.id_tipoestadocivil = estadoCivil;
                    oClientePreferencial.nomeconjuge = conjuge;
                    oClientePreferencial.orgaoemissor = orgaoExp;
                    oClientePreferencial.enderecoempresa = enderecoEmpresa;
                    oClientePreferencial.bairroempresa = enderecoEmpresa;
                    oClientePreferencial.complemento = complemento;

                    vClientePreferencial.add(oClientePreferencial);
                }
            stm.close();
            }catch(Exception ex) { 
                throw ex;
            }
            return vClientePreferencial;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    public List<ConveniadoVO> carregarConveniado(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ConveniadoVO> vConveniado = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, bairroEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;

        try {
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("SELECT CODCLI, CLIENTE, ENDERCOB, BAIRROCOB, TELCOB, MUNICCOB, ESTCOB, CEPCOB, ");
            sql.append("ENDERENT, BAIRROENT, TELENT, MUNICENT, ESTENT, CEPENT, CGCENT, IEENT, DTULTCOMP, ");
            sql.append("BLOQUEIO, FAXCLI, LIMCRED, OBS, DTCADASTRO, FANTASIA, OBS2, PONTOREFER, TIPOFJ, ");
            sql.append("TELENT1, EMAIL, DTEXCLUSAO, OBS3, OBS4, ENDERCOM, BAIRROCOM, MUNICCOM, ESTCOM, ");
            sql.append("CEPCOM, TELCOM, FAXCOM, OBSENTREGA1, OBSENTREGA2, OBSENTREGA3, NUMBANCO1, NUMAGENCIA1, ");
            sql.append("NUMCCORRENTE1, NUMBANCO2, NUMAGENCIA2, NUMCCORRENTE2, RG, ORGAORG, OBS5, EMPRESA, ");
            sql.append("ENDEREMPR, MUNICEMPR, ESTEMPR, TELEMPR, CARGO, DTADMISSAO, RENDAMENSAL, NOMECONJUGE, ");
            sql.append("CPFCONJUGE, EMPRESACONJUGE, ENDERCONJUGE, MUNICCONJUGE, ESTCONJUGE, TELCONJUGE, CARGOCONJUGE, ");
            sql.append("DTADMISSAOCONJUGE, RENDAMENSALCONJUGE, VLOUTRASRENDAS, TELCELENT, OBSERVACAO, DTNASC,  ");
            sql.append("FILIACAOPAI, FILIACAOMAE, SEXO, DTNASCCONJ, FILIACAOPAICONJ, FILIACAOMAECONJ,  ");
            sql.append("RGCONJ, ORGAORGCONJ, CODMUNICIPIO, OBSGERENCIAL1, OBSGERENCIAL2, OBSGERENCIAL3,  ");
            sql.append("COMPLEMENTOENT, CODCIDADE, COMPLEMENTOCOM, NUMEROCOM, COMPLEMENTOCOB, NUMEROENT, NUMEROCOB ");
            sql.append("FROM PCCLIENT ");
            sql.append("WHERE CODCOB = 'CONV' ");

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ConveniadoVO oConveniado = new ConveniadoVO();

                    id = rst.getInt("CODCLI");
                    dataResidencia = "1990/01/01";
                    
                    
                    if ((rst.getString("DTEXCLUSAO") != null)
                            && (!rst.getString("DTEXCLUSAO").trim().isEmpty())) {
                        id_situacaocadastro = 0;
                    } else {
                        id_situacaocadastro = 1;
                    }                    

                    if ((rst.getString("TIPOFJ") != null)
                            && (!rst.getString("TIPOFJ").trim().isEmpty())) {
                        if ("F".equals(rst.getString("TIPOFJ").trim())) {
                            id_tipoinscricao = 1;
                        } else if ("J".equals(rst.getString("TIPOFJ").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("CLIENTE") != null)
                            && (!rst.getString("CLIENTE").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("CLIENTE");
                        //String textoAcertado = new String(bytes, "ISO-8859-1");
                        //nome = util.acertarTexto(textoAcertado.replace("'", "").trim());
                        nome = rst.getString("CLIENTE");
                    } else {
                        nome = "SEM NOME VR " + id;
                    }

                    if ((rst.getString("ENDERENT") != null)
                            && (!rst.getString("ENDERENT").trim().isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("ENDERENT").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("CGCENT") != null)
                            && (!rst.getString("CGCENT").trim().isEmpty())
                            && (!"00.000.000/0001-91".equals(rst.getString("CGCENT")))) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CGCENT").trim()));
                    } else {
                        cnpj = id;
                    }

                    if ((rst.getString("BAIRROENT") != null)
                            && (!rst.getString("BAIRROENT").trim().isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRROENT").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("TELENT") != null)
                            && (!rst.getString("TELENT").trim().isEmpty())) {
                        telefone1 = util.formataNumero(rst.getString("TELENT").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("CEPENT") != null)
                            && (!rst.getString("CEPENT").trim().isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("CEPENT").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("MUNICENT") != null)
                            && (!rst.getString("MUNICENT").trim().isEmpty())) {
                        if ((rst.getString("ESTENT") != null)
                                && (!rst.getString("ESTENT").trim().isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("MUNICENT").trim().replace("'", "").toUpperCase()),
                                    rst.getString("ESTENT").trim().replace("'", ""));
                            if (id_municipio == 0) {
                                id_municipio = 2611002;
                            }
                        } else {
                            id_municipio = 2611002;
                        }
                    } else {
                        id_municipio = 2611002;
                    }

                    if ((rst.getString("ESTENT") != null)
                            && (!rst.getString("ESTENT").trim().isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(
                                rst.getString("ESTENT").trim().replace("'", "").toUpperCase());

                        if (id_estado == 0) {
                            id_estado = 26;
                        }
                    } else {
                        id_estado = 26;
                    }

                    numero = "0";

                    if ((rst.getString("COMPLEMENTOENT") != null)
                            && (!rst.getString("COMPLEMENTOENT").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("COMPLEMENTOENT"));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("LIMCRED") != null)
                            && (!rst.getString("LIMCRED").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMCRED"));
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("IEENT") != null)
                            && (!rst.getString("IEENT").trim().isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("IEENT").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "").replace("-", "").replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("DTCADASTRO") != null)
                            && (!rst.getString("DTCADASTRO").trim().isEmpty())) {
                        dataCadastro = rst.getString("DTCADASTRO").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }
                    if ((rst.getString("DTNASC") != null)
                            && (!rst.getString("DTNASC").trim().isEmpty())) {
                        dataNascimento = rst.getString("DTNASC").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }

                    if ((rst.getString("BLOQUEIO") != null)
                            && (!rst.getString("BLOQUEIO").trim().isEmpty())) {
                        if ("S".equals(rst.getString("BLOQUEIO"))) {
                            bloqueado = true;
                        } else {
                            bloqueado = false;
                        }
                    } else {
                        bloqueado = false;
                    }

                    if ((rst.getString("FILIACAOPAI") != null)
                            && (!rst.getString("FILIACAOPAI").trim().isEmpty())) {
                        nomePai = util.acertarTexto(rst.getString("FILIACAOPAI").trim().replace("'", ""));
                    } else {
                        nomePai = "";
                    }

                    if ((rst.getString("FILIACAOMAE") != null)
                            && (!rst.getString("FILIACAOMAE").trim().isEmpty())) {
                        nomeMae = util.acertarTexto(rst.getString("FILIACAOMAE").trim().replace("'", ""));
                    } else {
                        nomeMae = "";
                    }

                    if ((rst.getString("TELCELENT") != null)
                            && (!rst.getString("TELCELENT").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("TELCELENT").trim());
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("FAXCLI") != null)
                            && (!rst.getString("FAXCLI").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("FAXCLI").trim());
                    } else {
                        fax = "";
                    }

                    observacao = "";
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = util.acertarTexto(rst.getString("obs").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = observacao + "  " + util.acertarTexto(rst.getString("obs").replace("'", "").trim());
                    } else {
                        observacao = observacao + "";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())
                            && (rst.getString("EMAIL").contains("@"))) {
                        email = util.acertarTexto(rst.getString("EMAIL").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    id_sexo = 1;

                    if ((rst.getString("EMPRESA") != null)
                            && (!rst.getString("EMPRESA").trim().isEmpty())) {
                        empresa = util.acertarTexto(rst.getString("EMPRESA").trim().replace("'", ""));
                    } else {
                        empresa = "";
                    }

                    if ((rst.getString("ENDEREMPR") != null)
                            && (!rst.getString("ENDEREMPR").trim().isEmpty())) {
                        enderecoEmpresa = util.acertarTexto(rst.getString("ENDEREMPR").trim().replace("'", ""));
                    } else {
                        enderecoEmpresa = "";
                    }

                    if ((rst.getString("MUNICEMPR") != null)
                            && (!rst.getString("MUNICEMPR").trim().isEmpty())) {
                        bairroEmpresa = util.formataNumero(rst.getString("MUNICEMPR").trim());
                        if ((rst.getString("ESTEMPR") != null)
                                && (!rst.getString("ESTEMPR").trim().isEmpty())) {
                            bairroEmpresa = util.acertarTexto(bairroEmpresa +" "+util.formataNumero(rst.getString("ESTEMPR").trim()));                            
                        }
                    } else {
                        bairroEmpresa = "";
                    }

                    if ((rst.getString("CARGO") != null)
                            && (!rst.getString("CARGO").trim().isEmpty())) {
                        cargo = util.acertarTexto(rst.getString("CARGO").replace("'", "").trim());
                    } else {
                        cargo = "";
                    }

                    if ((rst.getString("VLOUTRASRENDAS") != null)
                            && (!rst.getString("VLOUTRASRENDAS").trim().isEmpty())) {
                        salario = Double.parseDouble(rst.getString("VLOUTRASRENDAS"));
                    } else {
                        salario = 0;
                    }

                    if ((rst.getString("NOMECONJUGE") != null)
                            && (!rst.getString("NOMECONJUGE").trim().isEmpty())) {
                        estadoCivil = 1; // CASADO
                    } else {
                        estadoCivil = 0;
                    }

                    if ((rst.getString("NOMECONJUGE") != null)
                            && (!rst.getString("NOMECONJUGE").trim().isEmpty())) {
                        conjuge = util.acertarTexto(rst.getString("NOMECONJUGE").trim().replace("'", ""));
                    } else {
                        conjuge = "";
                    }

                    if ((rst.getString("ORGAORG") != null)
                            && (!rst.getString("ORGAORG").trim().isEmpty())) {
                        orgaoExp = util.acertarTexto(rst.getString("ORGAORG").replace("'", "").trim());
                    } else {
                        orgaoExp = "";
                    }

                    if (nome.length() > 40) {
                        nome = nome.substring(0, 40);
                    }

                    if (conjuge.length() > 25) {
                        conjuge = conjuge.substring(0, 25);
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

                    if (telefone1.length() > 14) {
                        telefone1 = telefone1.substring(0, 14);
                    }

                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 18) {
                        inscricaoestadual = inscricaoestadual.substring(0, 18);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    if (observacao.length() > 60) {
                        observacao = observacao.substring(0, 60);
                    }
                    
                    if (cargo.length() > 25) {
                        cargo = cargo.substring(0, 25);
                    }                    
                    
                    if (empresa.length() > 35) {
                        empresa = empresa.substring(0, 35);
                    }                       
                    
                    if (bairroEmpresa.length() > 30) {
                        bairroEmpresa = bairroEmpresa.substring(0, 30);
                    }                         
                    
                    if (enderecoEmpresa.length() > 30) {
                        enderecoEmpresa = enderecoEmpresa.substring(0, 30);
                    }                         
                    
                    oConveniado.id = id;
                    oConveniado.nome = nome;
                    oConveniado.id_empresa = 1;
                    oConveniado.bloqueado = bloqueado;
                    oConveniado.id_situacaocadastro = id_situacaocadastro;
                    oConveniado.cnpj = cnpj;
                    oConveniado.observacao = observacao;
                    oConveniado.id_tipoinscricao = id_tipoinscricao;
                    
                    ConveniadoServicoVO oServico = new ConveniadoServicoVO();
                    oServico.valor = limite;
                    oConveniado.vConveniadoServico.add(oServico);
                    
                    vConveniado.add(oConveniado);

                    
                }
            stm.close();
            }catch(Exception ex) { 
                throw ex;
            }
            return vConveniado;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarFornecedor() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                   numero = "", complemento = "", telefone = "", telefone2 = "", telefone3 = "", email = "", fax = "";
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoOracle.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("SELECT CODFORNEC, FORNECEDOR, REPRES, CONTATO, ENDER, CIDADE, ESTADO, ");
                sql.append("CEP, TELREP, TELFAB, TELEXREP, TELEXFAB, IE, CGC, FAXREP, BAIRRO, OBS, ");
                sql.append("DTCADASTRO, OBSERVACAO, CONTATOCOM, TELEFONECOM, CONTATOADM, TELEFONEADM, ");
                sql.append("EMAIL, NUMBANCODOC, NUMAGENCIADOC, NUMCCORRENTEDOC, FANTASIA, CODMUNICIPIO, ");
                sql.append("COM_EMAIL, REP_EMAIL, SUP_EMAIL, SUP_CELULAR, COM_CELULAR, CODCIDADE, REP_OBS, ");
                sql.append("TIPOPESSOA, COMPLEMENTOEND, NUMEROEND, CODPAIS, CGCAUX   ");
                sql.append("FROM PCFORNEC                 ");

                rst = stm.executeQuery(sql.toString());

                Linha=0;
                
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("CODFORNEC");

                        Linha=id;
                        
                        if ((rst.getString("FORNECEDOR") != null)
                                && (!rst.getString("FORNECEDOR").isEmpty())) {
                           byte[] bytes = rst.getBytes("FORNECEDOR");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                           razaosocial = "";
                        }

                        if ((rst.getString("FORNECEDOR") != null)
                                && (!rst.getString("FORNECEDOR").isEmpty())) {
                           byte[] bytes = rst.getBytes("FORNECEDOR");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("CGC") != null)
                                && (!rst.getString("CGC").isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("CGC").trim()));
                        } else {
                            cnpj = Long.parseLong("-1");
                        }

                        if ((rst.getString("IE") != null)
                                && (!rst.getString("IE").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("IE").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        id_tipoinscricao = 0;

                        if ((rst.getString("ENDER") != null)
                                && (!rst.getString("ENDER").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("ENDER").replace("'", "").trim());
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
                                    id_municipio = 2611002;
                                }
                            }
                        } else {
                            id_municipio = 2611002;
                        }

                        if ((rst.getString("ESTADO") != null)
                                && (!rst.getString("ESTADO").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 26;
                            }
                        } else {
                            id_estado = 26;
                        }

                        if (rst.getString("DTCADASTRO") != null) {
                            datacadastro = rst.getString("DTCADASTRO").trim();
                        } else {
                            datacadastro = "";
                        }                            

                        pedidoMin = 0;
                        ativo = true;
                        
                        if ((rst.getString("TIPOPESSOA") != null) &&
                                (!rst.getString("TIPOPESSOA").trim().isEmpty())) {
                            if ("J".equals(rst.getString("TIPOPESSOA").trim())) {
                                id_tipoinscricao = 0;
                            } else {
                                id_tipoinscricao = 1;
                            }
                        } else {
                            id_tipoinscricao = 0;
                        }

                        if ((rst.getString("NUMEROEND") != null) &&
                                (!rst.getString("NUMEROEND").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("NUMEROEND").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }

                        if ((rst.getString("COMPLEMENTOEND") != null) &&
                                (!rst.getString("COMPLEMENTOEND").trim().isEmpty())) {
                            complemento = util.acertarTexto(rst.getString("COMPLEMENTOEND").replace("'", "").trim());
                        } else {
                            complemento = "";
                        }

                        if ((rst.getString("TELEFONECOM") != null) &&
                                (!rst.getString("TELEFONECOM").trim().isEmpty())) {
                            telefone = util.formataNumero(rst.getString("TELEFONECOM").trim());
                        } else {
                            telefone = "0";
                        }

                        if ((rst.getString("CONTATOADM") != null) &&
                                (!rst.getString("CONTATOADM").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("CONTATOADM").trim());
                        } else {
                            telefone2 = "0";
                        }

                        if ((rst.getString("TELEFONEADM") != null) &&
                                (!rst.getString("TELEFONEADM").trim().isEmpty())) {
                            telefone3 = util.formataNumero(rst.getString("TELEFONEADM").trim());
                        } else {
                            telefone3 = "0";
                        }                        


                        if ((rst.getString("EMAIL") != null) &&
                                (!rst.getString("EMAIL").trim().isEmpty()) &&
                                (rst.getString("EMAIL").contains("@"))) {
                            email = util.acertarTexto(rst.getString("EMAIL").replace("'", ""));
                        } else {
                            email = "";
                        }

                        if ((rst.getString("TELEXFAB") != null) &&
                                (!rst.getString("TELEXFAB").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("TELEXFAB").trim());
                        } else {
                            fax = "";
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
                        
                        if (complemento.length() > 30) {
                            complemento = complemento.substring(0, 30);
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
                        
                        if (email.length() > 50) {
                            email = email.substring(0, 50);
                        }                        

                        obs = "";
                        
                        if ((rst.getString("CONTATO") != null) &&
                                (!rst.getString("CONTATO").trim().isEmpty())) {                        
                            obs = obs +" CONTATO: "+ rst.getString("CONTATO").trim();
                        }        
                        if ((rst.getString("OBSERVACAO") != null) &&
                                (!rst.getString("OBSERVACAO").trim().isEmpty())) {                        
                            obs = obs +" OBS: "+ rst.getString("OBSERVACAO").trim();
                        } 
                        if ((rst.getString("REPRES") != null) &&
                                (!rst.getString("REPRES").trim().isEmpty())) {                        
                            obs = obs +" REPRESENTANTE: "+ rst.getString("REPRES").trim();
                        }        
                        if ((rst.getString("COM_EMAIL") != null) &&
                                (!rst.getString("COM_EMAIL").trim().isEmpty())) {                        
                            obs = obs +" COM_EMAIL: "+ rst.getString("COM_EMAIL").trim();
                        }                                         
                        if ((rst.getString("REP_EMAIL") != null) &&
                                (!rst.getString("REP_EMAIL").trim().isEmpty())) {                        
                            obs = obs +" REP_EMAIL: "+ rst.getString("REP_EMAIL").trim();
                        }        
                        if ((rst.getString("SUP_EMAIL") != null) &&
                                (!rst.getString("SUP_EMAIL").trim().isEmpty())) {                        
                            obs = obs +" SUP_EMAIL: "+ rst.getString("SUP_EMAIL").trim();
                        }        
                        if ((rst.getString("SUP_CELULAR") != null) &&
                                (!rst.getString("SUP_CELULAR").trim().isEmpty())) {                        
                            obs = obs +" SUP_CELULAR: "+ rst.getString("SUP_CELULAR").trim();
                        }        
                        
                        if ((rst.getString("COM_CELULAR") != null) &&
                                (!rst.getString("COM_CELULAR").trim().isEmpty())) {                        
                            obs = obs +" COM_CELULAR: "+ rst.getString("COM_CELULAR").trim();
                        }                                                   
                        if ((rst.getString("REP_OBS") != null) &&
                                (!rst.getString("REP_OBS").trim().isEmpty())) {                        
                            obs = obs +" REP_OBS: "+ rst.getString("REP_OBS").trim();
                        }                                                   
                        
                        obs = util.acertarTexto(obs);
                        
                        oFornecedor.codigoanterior = id;
                        oFornecedor.razaosocial = razaosocial;
                        oFornecedor.nomefantasia = nomefantasia;
                        oFornecedor.endereco = endereco;
                        oFornecedor.bairro = bairro;
                        oFornecedor.numero = numero;
                        oFornecedor.id_municipio = id_municipio;
                        oFornecedor.cep = cep;
                        oFornecedor.id_estado = id_estado;
                        oFornecedor.id_tipoinscricao = id_tipoinscricao;
                        oFornecedor.inscricaoestadual = inscricaoestadual;
                        oFornecedor.cnpj = cnpj;
                        oFornecedor.id_situacaocadastro = (ativo == true ?  1 : 0);                    
                        oFornecedor.observacao = obs;
                        oFornecedor.complemento = complemento;
                        oFornecedor.telefone = telefone;
                        oFornecedor.telefone2 = telefone2;                        
                        oFornecedor.telefone3 = telefone3;                                                
                        oFornecedor.email = email;
                        oFornecedor.fax = fax;

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

            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
    }       
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT DISTINCT CODFORNEC, CODPROD  ");
            sql.append("  FROM PCMOV ");
            sql.append(" WHERE CODFORNEC IS NOT NULL        ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("CODFORNEC");
                idProduto = rst.getInt("CODPROD");
                qtdEmbalagem = 1;
                codigoExterno = "";
 
                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CHQ.CODCLI, CHQ.DUPLIC, CHQ.VALOR, CHQ.DTVENC, ");
            sql.append("       CHQ.DTEMISSAO, CHQ.NUMBANCO, CHQ.NUMAGENCIA, ");
            sql.append("       CHQ.NUMCHEQUE, C.CGCENT, C.CLIENTE ");
            sql.append("FROM PCPREST CHQ ");
            sql.append("LEFT OUTER JOIN PCCLIENT C ON ");
            sql.append("    CHQ.CODCLI = C.CODCLI  ");
            sql.append(" WHERE CHQ.CODCOB = 'CHP' AND ");
            sql.append("       CHQ.DTPAG IS NULL ");
            sql.append("ORDER BY CHQ.DTEMISSAO, CHQ.DTVENC               ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(util.formataNumero(rst.getString("CGCENT").trim()));
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("NUMBANCO").trim()));                

                if ((rst.getString("NUMAGENCIA") != null) &&
                        (!rst.getString("NUMAGENCIA").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("NUMAGENCIA").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                conta = "";
                
                if ((rst.getString("NUMCHEQUE") != null) &&
                        (!rst.getString("NUMCHEQUE").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("NUMCHEQUE")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                                      
                if ((rst.getString("DTEMISSAO") != null) &&
                        (!rst.getString("DTEMISSAO").trim().isEmpty())) {
                
                    dataemissao = rst.getString("DTEMISSAO").trim();
                } else {
                    dataemissao = "2016/01/01";
                }
                
                if ((rst.getString("DTVENC") != null) &&
                        (!rst.getString("DTVENC").trim().isEmpty())) {
                
                    datavencimento = rst.getString("DTVENC").trim();
                } else {
                    datavencimento = "2016/12/01";
                }
                
                if ((rst.getString("CLIENTE") != null) &&
                        (!rst.getString("CLIENTE").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("CLIENTE").replace("'", "").trim());
                } else {
                    nome = "";
                }
                
                rg = "";
                
                valor = Double.parseDouble(rst.getString("VALOR"));
                numerocupom = 0;
                juros = 0;

                observacao = "IMPORTADO VR";
                telefone = "";
                   
                id_tipoalinea = 0;
                
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CODCLI, DUPLIC, VALOR, DTVENC, DTEMISSAO ");
            sql.append("FROM PCPREST ");
            sql.append("WHERE CODCOB NOT IN ('CONV', 'CHP') AND  ");
            sql.append("       DTPAG IS NULL ");
            sql.append("ORDER BY DTEMISSAO, DTVENC ");               
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("CODCLI");                
                dataemissao = rst.getString("DTEMISSAO").substring(0, 10).trim();
                datavencimento = rst.getString("DTVENC").substring(0, 10).trim();
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("DUPLIC")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = 0;

                /*if ((rst.getString("cxanum") != null) &&
                        (!rst.getString("cxanum").trim().isEmpty())) {
                    ecf = Integer.parseInt(rst.getString("cxanum").trim());
                } else {*/
                    ecf = 0;
                //}
                
                /*if ((rst.getString("ctrobs") != null) &&
                        (!rst.getString("ctrobs").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", ""));
                } else { */
                    observacao = "IMPORTADO VR";
                //}
                
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
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

    public List<ConveniadoTransacaoVO> carregarReceberConveniadoTransacao(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ConveniadoTransacaoVO> vConveniadoTransacao = new ArrayList<>();
        
        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CODCLI, DUPLIC, VALOR, DTVENC, DTEMISSAO ");
            sql.append("FROM PCPREST ");
             sql.append("WHERE CODCOB = 'CONV' AND  ");
            sql.append("       DTPAG IS NULL ");
            sql.append("ORDER BY DTEMISSAO, DTVENC ");               
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ConveniadoTransacaoVO oConveniadoTransacao = new ConveniadoTransacaoVO();
                
                id_cliente = rst.getInt("CODCLI");                
                dataemissao = rst.getString("DTEMISSAO").substring(0, 10).trim();
                datavencimento = rst.getString("DTVENC").substring(0, 10).trim();
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("DUPLIC")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = 0;

                /*if ((rst.getString("cxanum") != null) &&
                        (!rst.getString("cxanum").trim().isEmpty())) {
                    ecf = Integer.parseInt(rst.getString("cxanum").trim());
                } else {*/
                    ecf = 0;
                //}
                
                /*if ((rst.getString("ctrobs") != null) &&
                        (!rst.getString("ctrobs").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", ""));
                } else { */
                    observacao = "IMPORTADO VR";
                //}
                
                oConveniadoTransacao.id_conveniado = id_cliente;
                oConveniadoTransacao.id_loja = id_loja;
                oConveniadoTransacao.datahora = dataemissao;
                oConveniadoTransacao.datamovimento = dataemissao;
                oConveniadoTransacao.numerocupom = numerocupom;
                oConveniadoTransacao.valor = valor;
                oConveniadoTransacao.ecf = ecf;
                oConveniadoTransacao.observacao = observacao;
                
                vConveniadoTransacao.add(oConveniadoTransacao);
                
            }
            
            return vConveniadoTransacao;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }        
    
    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException, Exception {
        StringBuilder sql, sql2 = null;
        Statement stm, stmPostgres = null;
        ResultSet rst, rst2;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            stmPostgres = Conexao.createStatement();            
            
            sql = new StringBuilder();     
            sql.append("SELECT CODPROD, CODBARRA FROM PCBARRA ");
            sql.append("WHERE CODPROD <> CODBARRA AND ");
            sql.append("      CODBARRA > 0 ");
            sql.append("ORDER BY CODPROD ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODPROD"));

                if ((rst.getString("CODBARRA") != null) &&
                        (!rst.getString("CODBARRA").trim().isEmpty())) {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("CODBARRA").replace(".", "").trim()));
                } else {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("CODPROD").replace(".", "").trim()));
                }
                
                qtdeEmbalagem = 1;
                
                if ((String.valueOf(codigobarras).length() >= 7)) {
                
                    if (String.valueOf(codigobarras).length() > 14) {
                        codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                    }                    
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = (int) idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigobarras;
                    oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);
 
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    oCodigoAnterior.codigoatual = idProduto;
                    if((rst.getString("CODPROD")!=null) && (!rst.getString("CODPROD").trim().isEmpty())){
                       oCodigoAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("CODPROD").replace(".", "").trim()));
                    } else {
                       oCodigoAnterior.barras = 0; 
                    }
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarCustoProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double custo = 0;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select codprod, CUSTOULTENT ");
            sql.append("from pchistest h ");
            sql.append("where data = (select max(h2.data) ");
            sql.append("from pchistest h2 ");
            sql.append("where h2.codprod = h.codprod ");
            sql.append("and h2.codfilial = "+idLoja+") ");
            sql.append("and h.codfilial = "+idLoja+" ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codprod").trim());
                
                if ((rst.getString("CUSTOULTENT") != null) &&
                        (!rst.getString("CUSTOULTENT").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("CUSTOULTENT").trim());
                } else {
                    custo = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oProduto.vComplemento.add(oComplemento);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    // IMPORTAÇÕES
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

            FamiliaProdutoDAO familiaProduto = new FamiliaProdutoDAO();
            familiaProduto.verificarDescricao = false;
            familiaProduto.salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico 1...");
            vMercadologico = carregarMercadologico(1);
            new MercadologicoDAO().salvarMaior3Digitos(vMercadologico, true);

            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvarMaior3Digitos(vMercadologico, false);

            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvarMaior3Digitos(vMercadologico, false);
            
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            
            vProdutoNovo = carregarProduto();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.implantacaoExterna = true;
            produtoDAO.salvar(vProdutoNovo, idLojaDestino, vLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarPrecoProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        
        try {
            ProgressBar.setStatus("Carregando dados...Preço Produtos...");
            
            vProdutoNovo = carregarPrecoProduto();
            
            new ProdutoDAO().alterarPrecoProdutoPCSistemas(vProdutoNovo, idLojaDestino);
        } catch(Exception ex) {
            throw ex;
        }
    }    

    public void importarCustoProduto(int idLojaOrigem, int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produtos...");
            
            vProdutoNovo = carregarCustoProduto(idLojaOrigem);
            
            new ProdutoDAO().alterarCustoProdutoPCSistemas(vProdutoNovo, idLojaDestino);
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    public void importarPisCofinsProdutO() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Pis Cofins...Produtos...");
            
            vProduto = carregarPisCofinsProduto();
            
            new ProdutoDAO().alterarPisCofinsProdutoPCSistemas(vProduto);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Icms...Produtos...");
            
            vProduto = carregarIcmsProduto();
            
            new ProdutoDAO().alterarIcmsProdutoPCSistemas(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque...Produtos...");
            
            vProduto = carregarEstoqueProduto(idLoja);
            
            new ProdutoDAO().alterarEstoqueProdutoPCSistemas(vProduto);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarConvenio(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Convenio...");
            List<ConveniadoVO> vConveniado = carregarConveniado(idLoja, idLojaCliente);
            
            new ConveniadoDAO().salvar(vConveniado, idLoja, 1);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarFornecedor() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedor();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }    
    
    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }          
    
    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }    

    public void importarReceberConveniadoTransacao(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Conveniado...");
            List<ConveniadoTransacaoVO> vConveniadoTransacao = carregarReceberConveniadoTransacao(idLoja, idLojaCliente);

            
            new ConveniadoTransacaoDAO().salvar(vConveniadoTransacao, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarras();
            
            ProgressBar.setMaximum(vCodigoBarra.size());
            
            for (Long keyId : vCodigoBarra.keySet()) {
                
                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.addCodigoBarras(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }        
}