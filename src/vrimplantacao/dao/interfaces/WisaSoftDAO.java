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
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoFirebird;
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

public class WisaSoftDAO {
    
    //CARREGAMENTOS
    public List<FamiliaProdutoVO> carregarFamiliaProdutoWisaSoft() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT ID,         ");
            sql.append("        S_DESCRICAO ");
            sql.append(" FROM FAMILIAS      ");          
            sql.append(" ORDER BY ID        ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("ID"));
                oFamiliaProduto.descricao = util.acertarTexto(rst.getString("S_DESCRICAO").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        
    
    public List<MercadologicoVO> carregarMercadologicoWisaSoft(int nivel) throws Exception {
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
            sql.append(" SELECT DISTINCT P.GRUPO, M1.S_DESCRICAO AS MERC1, ");
            sql.append("        P.DEPARTAMENTO, M2.S_DESCRICAO AS MERC2,   ");
            sql.append("        P.SESSAO, M3.S_DESCRICAO AS MERC3          ");                                  
            sql.append("   FROM PRODUTOS P                        ");  
            sql.append("  INNER JOIN GRUPOS M1 ON M1.ID = P.GRUPO ");
            sql.append("  INNER JOIN DEPARTAMENTOS M2 ON M2.ID = P.DEPARTAMENTO ");
            sql.append("  INNER JOIN SESSOES M3 ON M3.ID = P.SESSAO             ");
            sql.append("  ORDER BY P.GRUPO,  P.DEPARTAMENTO, P.SESSAO;          ");  

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    descricao = util.acertarTexto(rst.getString("MERC1").replace("'", "").trim());
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
                    
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    descricao = util.acertarTexto(rst.getString("MERC2").replace("'", "").trim());
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
                } else if (nivel == 3){
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    mercadologico3 = Integer.parseInt(rst.getString("SESSAO"));
                    descricao = util.acertarTexto(rst.getString("MERC3").replace("'", "").trim());
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
    
    public List<ProdutoVO> carregarPisCofinsWisa() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProdutoPisCofins = new ArrayList<>();
        double idProduto = 0;
        int idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita, 
            idTipoPisCofinsCodigoAnterior, idTipoPisCofinsCreditoCodigoAnterior, tipoNaturezaReceitaCodigoAnterior;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append(" SELECT S.SUP001,       ");
            sql.append("        CASE S.SUP108   "); 
            sql.append("             WHEN 1 THEN 9 ");
            sql.append("             WHEN 2 THEN 3 ");
            sql.append("             WHEN 3 THEN 0 ");
            sql.append("             WHEN 4 THEN 1 ");
            sql.append("             WHEN 5 THEN 2 ");
            sql.append("        END AS PISCOFINSDEBITO, ");
            sql.append("        CASE S.SUP108 ");
            sql.append("             WHEN 1 THEN 21 ");
            sql.append("             WHEN 2 THEN 15 ");
            sql.append("             WHEN 3 THEN 12 ");
            sql.append("             WHEN 4 THEN 13 ");
            sql.append("             WHEN 5 THEN 14 ");
            sql.append("        END AS PISCOFINSCREDITO, ");
            sql.append("        S90.codigonatrec AS NATCODIGO ");
            sql.append(" FROM SUP001 S ");
            sql.append(" INNER JOIN SUP090 S90 ON ");
            sql.append(" S90.sup090 = S.sup090    ");                 
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("PISCOFINSDEBITO") != null) &&
                        (!rst.getString("PISCOFINSDEBITO").trim().isEmpty())) {
                    idTipoPisCofins               = Integer.parseInt(rst.getString("PISCOFINSDEBITO").trim());
                    idTipoPisCofinsCodigoAnterior = Integer.parseInt(rst.getString("PISCOFINSDEBITO").trim());
                } else {
                    idTipoPisCofins = 0;
                    idTipoPisCofinsCodigoAnterior = -1;
                }
                
                if ((rst.getString("PISCOFINSCREDITO") != null) &&
                        (!rst.getString("PISCOFINSCREDITO").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Integer.parseInt(rst.getString("PISCOFINSCREDITO").trim());
                    idTipoPisCofinsCreditoCodigoAnterior = Integer.parseInt(rst.getString("PISCOFINSCREDITO").trim());
                } else {
                    idTipoPisCofinsCredito = 12;
                    idTipoPisCofinsCreditoCodigoAnterior = -1;                    
                }
                
                if ((rst.getString("NATCODIGO") != null) &&
                        (!rst.getString("NATCODIGO").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            util.acertarTexto(rst.getString("NATCODIGO").trim()));
                    tipoNaturezaReceitaCodigoAnterior = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            util.acertarTexto(rst.getString("NATCODIGO").trim()));                    
                } else {
                    tipoNaturezaReceita               = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaCodigoAnterior = -1;                    
                }
                
                idProduto = Double.parseDouble(rst.getString("SUP001"));
                
                oProduto.idDouble = idProduto;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoCodigoAnterior;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofinsCodigoAnterior;                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaCodigoAnterior;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                
                vProdutoPisCofins.add(oProduto);                           
            }
            
            return vProdutoPisCofins;
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    public Map<Integer, ProdutoVO> carregarProdutoWisaSoft() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
            ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade=0;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double margem, precoVenda, custo;
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT SUP001.SUP001, SUP001.DESCRICAO, SUP001.DESCRICAO_ETIQUETA,  ");     
            sql.append("       SUP001.DESCRICAO_REDUZIDA, SUP001.EAN, SUP001.DATA_CADASTRO, ");    
            sql.append("       SUP002.SIGLA, SUP008.SALDO, SUP008.CUSTO, SUP008.VENDA,      ");     
            sql.append("       SUP008.MARGEMSUG as MARGEM, SUP090.DIGITOS, SUP009.DESCRICAO AS UNIDADE,  ");            
            sql.append("       SUP009.MULTIPLICADOR AS QTDEMBALAGEM, SUP001.PIS             ");    
            sql.append("FROM SUP001                                                         ");
            sql.append("   INNER JOIN SUP002 ON SUP002.SUP002 = SUP001.SUP002               ");
            sql.append("   INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001               ");
            sql.append("   INNER JOIN SUP009 ON SUP009.SUP009 = SUP001.sup009_venda         ");            
            sql.append("   LEFT JOIN SUP090 ON SUP090.SUP090 = SUP001.SUP090                ");
            sql.append("   LEFT JOIN SUP040 ON SUP040.SUP001 = SUP001.SUP001                ");
            sql.append("ORDER BY 1                                                          ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                //if (rst.getInt("ATIVO")==1) {
                    idSituacaoCadastro = 1;
                //} else {
                //    idSituacaoCadastro = 0;
                //}
                
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("EAN").replace(".", ""));

                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {
                    eBalanca = true;                         
                    idProduto = Integer.parseInt(rst.getString("SUP001").trim().replace(".", ""));               
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade"); 
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
                    eBalanca = false;
                    pesavel = false;
                    idProduto = Integer.parseInt(rst.getString("SUP001").trim().replace(".", ""));
                    validade = 0;                     
                    if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("DESCRICAO") != null) &&
                        (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoCompleta = "";
                }
                
                if ((rst.getString("DESCRICAO_REDUZIDA") != null) &&
                        (!rst.getString("DESCRICAO_REDUZIDA").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_REDUZIDA");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoCompleta;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("QTDEMBALAGEM").replace(",", ""));
                }
                
                /*if ((rst.getString("FAMILIA") != null) &&
                        (!rst.getString("FAMILIA").trim().isEmpty()) &&
                        (!"0".equals(rst.getString("FAMILIA").trim()))) {
                    idFamilia = Integer.parseInt(rst.getString("FAMILIA").trim().replace(".", ""));
                } else {*/
                    idFamilia = -1;
                //}
                
                // MERCADOLOGICO MODELO VR SOFTWARE
                mercadologico1=14; 
                mercadologico2=1; 
                mercadologico3=1;                
                
                if ((rst.getString("DIGITOS")!=null) && 
                        (!rst.getString("DIGITOS").trim().isEmpty())){
                    ncmAtual = util.formataNumero(rst.getString("DIGITOS"));
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
                    
                    if ((rst.getString("EAN") != null) &&
                            (!rst.getString("EAN").trim().isEmpty())) {
                        
                        strCodigoBarras = rst.getString("EAN").replace(".", "").trim();
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(rst.getString("EAN").trim());
                        }
                    }
                }
                
                idTipoPisCofins        = 0;
                idTipoPisCofinsCredito = 12;                    
               
                /*if ((rst.getString("NATREC") != null) &&
                        (!rst.getString("NATREC").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            rst.getString("NATREC").trim());
                } else {*/
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                //}
                
                if ((rst.getString("SIGLA") != null) &&
                        (!rst.getString("SIGLA").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSWisaSoft(rst.getString("SIGLA").trim().toUpperCase(),"");
                } else {
                    idAliquota = 8;
                }
                
                if ((rst.getString("margem") != null) &&
                        (!rst.getString("margem").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("margem").replace(",", "."));
                } else {
                    margem = 0;
                }
                if ((rst.getString("venda") != null) &&
                        (!rst.getString("venda").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("venda").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                if ((rst.getString("custo") != null) &&
                        (!rst.getString("custo").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("custo").replace(",", "."));
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
                
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
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
                oProduto.validade = validade;
                oProduto.margem = margem;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                
                oProduto.vComplemento.add(oComplemento);
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;                
                
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
                
                oCodigoAnterior.codigoanterior = idProduto;
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.barras = codigoBarras;
                
                //oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                //oCodigoAnterior.piscofinsdebito = idTipoPisCofins;
                //oCodigoAnterior.piscofinscredito = idTipoPisCofinsCredito;
                oCodigoAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                if ((rst.getString("DIGITOS")!=null) && 
                        (!rst.getString("DIGITOS").trim().isEmpty())){
                    ncmAtual = util.formataNumero(rst.getString("DIGITOS"));
                    oCodigoAnterior.ncm = ncmAtual;
                } else {
                    oCodigoAnterior.ncm = "";
                }                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
            }
            
            stmPostgres.close();
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }    

    public List<ClientePreferencialVO> carregarClienteWisaSoft(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone,telefone2, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                   dataResidencia,  dataCadastro, numero, dataAniversario, nomePai, nomeMae, conjuge ; 
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao, id, agente, id_situacaocadastro, Linha=0;
            Long cnpj, cep;
            double limite;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("SELECT S.SUP024, S.SUP025, S.NOME, COALESCE(S.NASCIMENTO,CLI2.DTNASCIMENTO) AS DTNASCIMENTO, S.CPF, S.RG, COALESCE(S.TELEFONE1,CLI2.TELEFONE1) AS TELEFONE, ");    
                sql.append("       COALESCE(S.TELEFONE2,CLI2.TELEFONE2) AS TELEFONE2, COALESCE(S.CELULAR,CLI2.CELULAR) AS CELULAR, S.EMISSAO, S.BLOQUEADO, S.OBSERVACAO,  ");
                sql.append("       S.LIMITE, ");
                sql.append("       S.ENDERECO, S.NUMERO, S.COMPLEMENTO, S.BAIRRO, ");
                sql.append("       COALESCE(S.CEP,CLI2.CEP) AS CEP, COALESCE(S.EMAIL,CLI2.EMAIL) AS EMAIL, S.PAI, S.MAE, S.CONJUGE,  ");
                sql.append("       S.PROFISSAO,  S.SALARIO,  ");
                sql.append("       S.SEXO, S.RENDAMENSAL,  ");
                sql.append("       CID.NOME AS CIDADE, CID.UF ");
                sql.append("FROM SUP025 S ");
                sql.append("INNER JOIN SUP118 CID ON CID.SUP118 = S.SUP118 ");
                sql.append("LEFT OUTER JOIN SUP024 CLI2 ON CLI2.SUP024 = S.SUP024 ");
                sql.append("ORDER BY SUP025 ");               

                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        id = rst.getInt("SUP025");
                        
                        if (id==101){
                            id_situacaocadastro = 1;                            
                        }else{
                            id_situacaocadastro = 1;
                        }
                        
                        if ((rst.getString("NOME")!=  null) &&
                                (!rst.getString("NOME").isEmpty())) {
                            byte[] bytes = rst.getBytes("NOME");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        if ((rst.getString("ENDERECO")!=  null) &&
                            (!rst.getString("ENDERECO").isEmpty())) {                        
                            endereco            = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                        }else{
                            endereco            = "ENDERECO VR";                            
                        }
                        if (endereco.length()>50){
                            endereco = endereco.substring(0,50);
                        }
                        
                        if (rst.getString("BAIRRO")!=null){
                            bairro              = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                        }else{
                            bairro              = "";
                        }                        
                        
                        
                        if ((rst.getString("CIDADE")!=null) && (rst.getString("UF")!=null)){
                           id_estado           = util.retornarEstadoDescricao(rst.getString("UF"));     
                           if (id_estado==0){
                               id_estado=35; // ESTADO ESTADO DO CLIENTE
                           }
                           id_municipio        = util.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString(),rst.getString("UF").toString());
                           if(id_municipio==0){
                               id_municipio=3529005;// CIDADE DO CLIENTE;
                           }
                        } else{
                            id_estado    = 35; // ESTADO ESTADO DO CLIENTE
                            id_municipio = 3529005; // CIDADE DO CLIENTE;                   
                        }
                        
                        if (rst.getString("CEP")!=null){
                            cep                 = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                        }else{
                            cep = Long.parseLong("0");
                        }
                        
                        if (rst.getString("TELEFONE")!=null){
                            telefone           = util.formataNumero(rst.getString("TELEFONE"));  
                        }else{
                            telefone           = "";
                        }
                        
                        if (telefone==""){
                            if (rst.getString("TELEFONE2")!=null){
                                telefone           = util.formataNumero(rst.getString("TELEFONE2"));  
                            }else{
                                telefone           = "";
                            }    
                        }
                        
                        if (rst.getString("CELULAR")!=null){
                            telefone2           = util.formataNumero(rst.getString("CELULAR"));  
                        }else{
                            telefone2           = "";
                        }                        
                        
                        if (rst.getString("NUMERO")!=null){
                            numero               = util.acertarTexto(rst.getString("NUMERO"));  
                            if(numero.length()>6){
                                numero = numero.substring(0,6);
                            }
                        }else{
                            numero               = "";                        
                        }
                        
                        if (rst.getString("EMAIL")!=null){
                            email               = util.acertarTexto(rst.getString("EMAIL"));  
                            if(email.length()>50){
                                email = email.substring(0,50);
                            }
                        }else{
                            email               = "";
                        }
                        
                        if ((rst.getString("RG")!=null)&&
                                (!rst.getString("RG").trim().isEmpty())){                                    
                            inscricaoestadual   = util.acertarTexto(rst.getString("RG"));    
                            if (!inscricaoestadual.trim().isEmpty()){
                                inscricaoestadual   = inscricaoestadual.replace(".","").replace("/", "").replace(",", "");  
                                if (inscricaoestadual.length()>18){
                                    inscricaoestadual = inscricaoestadual.substring(0,18);
                                }
                            }else{
                                inscricaoestadual = "ISENTO";                                        
                            }
                        }else{
                            inscricaoestadual   = "ISENTO";
                        }
                        
                        if ((rst.getString("CPF")!=null)&&
                                (!rst.getString("CPF").trim().isEmpty())){
                            if (String.valueOf(rst.getString("CPF").trim()).length()<=11){
                                id_tipoinscricao = 1; // PESSOA FISICA                                
                            }else{
                                id_tipoinscricao = 0; // PESSOA JURIDICA
                            }
                            cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CPF").trim())));     
                        }else{
                            id_tipoinscricao = 1; // PESSOA FISICA                                                            
                            cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("SUP025"))));     
                        }                   
                        
                        
                        if (rst.getString("SEXO")!=null){
                            if ("F".equals(rst.getString("SEXO"))){ 
                                id_sexo             = 1;     
                            }else{
                                id_sexo             = 0;
                            }
                        }else{
                            id_sexo                = 1;
                        }
                            
                        if ((rst.getString("EMISSAO")!=null)&&
                                (!rst.getString("EMISSAO").isEmpty())){
                            dataCadastro                = rst.getString("EMISSAO");
                        }else{
                            dataCadastro                = "";
                        }
                        if ((rst.getString("DTNASCIMENTO")!=null)&&
                                (!rst.getString("DTNASCIMENTO").isEmpty())){
                            dataAniversario                = rst.getString("DTNASCIMENTO");
                        }else{
                            dataAniversario                = null;
                        }
                        
                        if ((rst.getString("PAI")!=null)&&
                                (!rst.getString("PAI").isEmpty())){
                            nomePai                = rst.getString("PAI");
                        }else{
                            nomePai                = "";
                        }                            
                        
                        if ((rst.getString("MAE")!=null)&&
                                (!rst.getString("MAE").isEmpty())){
                            nomeMae                = rst.getString("MAE");
                        }else{
                            nomeMae                = "";
                        }   
                        
                        if ((rst.getString("CONJUGE")!=null)&&
                                (!rst.getString("CONJUGE").isEmpty())){
                            conjuge                = rst.getString("CONJUGE");
                        }else{
                            conjuge                = "";
                        }                        
                        
                        dataResidencia   = "1990/01/01";

                        if (Linha==639){
                            Linha++; 
                        }else{
                            Linha++;
                        }
                        if (rst.getString("LIMITE")!=null){
                            limite           = rst.getDouble("LIMITE");  
                        }else{
                            limite           = 0;
                        }
                        
                        if (nome.length() > 40) {
                            nome = nome.substring(0, 40);
                        }

                        if (endereco.length() > 50) {
                            endereco = endereco.substring(0, 50);
                        }

                        oClientePreferencial.id = id;
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.endereco = endereco;
                        oClientePreferencial.bairro = bairro;
                        oClientePreferencial.numero = numero;                        
                        oClientePreferencial.id_estado = id_estado;
                        oClientePreferencial.id_municipio = id_municipio;
                        oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                        oClientePreferencial.cep = cep;
                        oClientePreferencial.nomepai = nomePai;
                        oClientePreferencial.nomemae = nomeMae;
                        oClientePreferencial.nomeconjuge = conjuge;                        
                        oClientePreferencial.telefone = telefone;
                        oClientePreferencial.telefone2 = telefone2;                        
                        oClientePreferencial.inscricaoestadual = inscricaoestadual;
                        oClientePreferencial.cnpj = cnpj;
                        oClientePreferencial.sexo = id_sexo;
                        oClientePreferencial.dataresidencia = dataResidencia;
                        oClientePreferencial.datanascimento = dataAniversario;
                        oClientePreferencial.datacadastro = dataCadastro;
                        oClientePreferencial.email = email;
                        oClientePreferencial.valorlimite = limite;
                        oClientePreferencial.codigoanterior = id;
                        vClientePreferencial.add(oClientePreferencial);
                    }
                stm.close();
                } catch (Exception ex) {
                    if (Linha > 0) {
                        throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                    } else {
                        throw ex;
                    }
                }
                return vClientePreferencial;
            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
        }    

    public List<FornecedorVO> carregarFornecedorWisaSoft() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, 
                   bairro, datacadastro, numero, telefone, telefone2, email;
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select sup010.sup010, sup010.sup118, sup010.razaosocial, sup010.fantasia, ");
                sql.append("       sup010.endereco, sup010.numero, sup010.bairro, sup010.complemento, ");
                sql.append("       sup010.cep, sup010.cgc, sup010.inscricao, sup010.inscricaosubst, sup010.telefone, ");
                sql.append("       sup010.fax, sup010.telefone_gratis, sup010.email, sup010.site, sup010.obs, ");
                sql.append("       sup010.pessoa, sup010.codsimples, sup010.recolhe_guia_st, sup010.distribuidor, ");
                sql.append("       sup010.dtcadastro, sup010.hrcadastro, sup010.inscmunicipal, sup010.ativo, ");
                sql.append("       sup010.dtalteracao, sup010.usuario, sup010.sup999, sup010.contribuinteicms, ");
                sql.append("       sup010.ivaajustado, sup010.modelonf, sup010.simplesnacional, sup118.nome as cidade, sup118.uf  ");
                sql.append("from sup010 ");
                sql.append("inner join sup118  on sup118.sup118 = sup010.sup118 ");
                sql.append("order by sup010.sup010 ");

                rst = stm.executeQuery(sql.toString());
                Linha=0;
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("sup010");

                        Linha++; 
                        if (id==31){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("razaosocial") != null)
                                && (!rst.getString("razaosocial").isEmpty())) {
                           byte[] bytes = rst.getBytes("razaosocial");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "RAZAO SOCIAL VR";
                        }

                        if ((rst.getString("fantasia") != null)
                                && (!rst.getString("fantasia").isEmpty())) {
                           byte[] bytes = rst.getBytes("fantasia");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "NOME FANTASIA VR";
                        }

                        if ("F".equals(rst.getString("pessoa").trim() )){
                            id_tipoinscricao = 1;                                                            
                        }else{
                            id_tipoinscricao = 0;                                                            
                        }
                        
                        if ((rst.getString("pessoa") != null)
                                && (!rst.getString("pessoa").isEmpty())) {
                            if ((rst.getString("cgc") != null)
                                    && (!rst.getString("cgc").isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));
                            } else {
                                cnpj = Long.parseLong(rst.getString("sup010"));
                            }
                        }else{
                            cnpj = Long.parseLong(rst.getString("sup010"));
                        }

                        if ((rst.getString("INSCRICAO") != null)
                                && (!rst.getString("INSCRICAO").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("INSCRICAO").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        if ((rst.getString("endereco") != null)
                                && (!rst.getString("endereco").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("endereco").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("bairro") != null)
                                && (!rst.getString("bairro").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("cep") != null)
                                && (!rst.getString("cep").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }
                        
                        if ((rst.getString("NUMERO") != null)
                                && (!rst.getString("NUMERO").isEmpty())) {
                            numero = rst.getString("NUMERO").trim();
                            if (numero.length()>6){
                                numero = numero.substring(0,6);
                            }
                        } else {
                            numero = "0";
                        }                        

                        if ((rst.getString("cidade") != null)
                                && (!rst.getString("cidade").isEmpty())) {

                            if ((rst.getString("uf") != null)
                                    && (!rst.getString("uf").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3525508;
                                }
                            }
                        } else {
                            id_municipio = 3525508;
                        }

                        if ((rst.getString("uf") != null)
                                && (!rst.getString("uf").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }

                        if (rst.getString("TELEFONE") != null) {
                            telefone = rst.getString("TELEFONE").trim();
                        } else {
                            telefone = "";
                        }
                        if (rst.getString("FAX") != null) {
                            telefone2 = rst.getString("FAX").trim();
                        } else {
                            telefone2 = "";
                        }                        
                        
                        if (rst.getString("EMAIL") != null) {
                            email = rst.getString("EMAIL").trim();
                        } else {
                            email = "";
                        }                          
                        
                        if (rst.getString("OBS") != null) {
                            obs = rst.getString("OBS").trim();
                        } else {
                            obs = "";
                        }

                        if (rst.getString("DTCADASTRO") != null) {
                            datacadastro = rst.getString("DTCADASTRO");
                        } else {
                            datacadastro = "";
                        }

                        if (rst.getString("ATIVO") != null) {
                            if ("S".equals(rst.getString("ATIVO").trim())){
                                ativo = true;
                            }else{
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

                        oFornecedor.codigoanterior = id;
                        oFornecedor.razaosocial = razaosocial;
                        oFornecedor.nomefantasia = nomefantasia;
                        oFornecedor.endereco = endereco;
                        oFornecedor.numero=numero;
                        oFornecedor.bairro = bairro;
                        oFornecedor.telefone=telefone;
                        oFornecedor.telefone2=telefone2;                        
                        oFornecedor.email=email;                        
                        oFornecedor.id_municipio = id_municipio;
                        oFornecedor.cep = cep;
                        oFornecedor.id_estado = id_estado;
                        oFornecedor.id_tipoinscricao = id_tipoinscricao;
                        oFornecedor.inscricaoestadual = inscricaoestadual;
                        oFornecedor.cnpj = cnpj;
                        oFornecedor.id_situacaocadastro = (ativo == true ?  1 : 0);                    
                        oFornecedor.observacao = obs;

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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorWisaSoft() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" select SUP001 AS PRODUTO, sup010 AS FORNECEDOR,  REFERENCIA from sup016 PF ");             

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("Fornecedor");
                idProduto = rst.getInt("produto");

                if ((rst.getString("REFERENCIA") != null)
                        && (!rst.getString("REFERENCIA").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("REFERENCIA").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarCustoProdutoWisaSoft(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder(); 
            sql.append("SELECT SUP001.SUP001, COALESCE(SUP008.CUSTO,0) AS CUSTO ");
            sql.append("FROM SUP001                                             ");
            sql.append("INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001      ");
            sql.append("WHERE COALESCE(SUP008.CUSTO,0) > 0                      ");            
            sql.append("ORDER BY SUP001.SUP001                                  ");
           
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("SUP001").replace(".", ""));
                custo = Double.parseDouble(rst.getString("CUSTO").replace(",", "."));
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
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
       
    public List<ReceberChequeVO> carregarReceberChequeWisaSoft(int id_loja, int id_lojaCliente) throws Exception {

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

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("SELECT CHQ.CPF_CGC, CHQ.SUP029 AS BANCO, CHQ.AGENCIA, CHQ.CONTA, ");
            sql.append("       CHQ.NUM_CHEQUE, CHQ.DATA_EMISSAO, CHQ.DATA_VENCIMENTO, CHQ.OBS, ");
            sql.append("       CHQ.VALOR, CHQ.NOME ");
            sql.append("FROM SUP034 CHQ ");
            sql.append("WHERE CHQ.DATA_BAIXA IS NULL ");
        
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(rst.getString("CPF_CGC").trim());
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("BANCO").trim()));                

                if ((rst.getString("AGENCIA") != null) &&
                        (!rst.getString("AGENCIA").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("AGENCIA").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                if ((rst.getString("CONTA") != null) &&
                        (!rst.getString("CONTA").trim().isEmpty()))  {
                    conta = util.acertarTexto(rst.getString("CONTA").trim().replace("'", ""));
                } else {
                    conta = "";
                }
                
                if ((rst.getString("NUM_CHEQUE") != null) &&
                        (!rst.getString("NUM_CHEQUE").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("NUM_CHEQUE")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                                      
                if ((rst.getString("DATA_EMISSAO") != null) &&
                        (!rst.getString("DATA_EMISSAO").trim().isEmpty())) {
                    dataemissao = rst.getString("DATA_EMISSAO").trim();
                } else {
                    dataemissao = "2016/02/01";
                }
                
                if ((rst.getString("DATA_VENCIMENTO") != null) &&
                        (!rst.getString("DATA_VENCIMENTO").trim().isEmpty())) {
                
                    datavencimento = rst.getString("DATA_VENCIMENTO").trim();
                } else {
                    datavencimento = "2016/02/12";
                }
                
                if ((rst.getString("NOME") != null) &&
                        (!rst.getString("NOME").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("NOME").replace("'", "").trim());
                } else {
                    nome = "IMPORTADO VR";
                }
                
                rg = "";
                
                valor = Double.parseDouble(rst.getString("VALOR"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                        && (!rst.getString("chrobserv1").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                } else {*/
                    observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                        (!rst.getString("chrtelefone").isEmpty()) &&
                        (!"0".equals(rst.getString("chrtelefone").trim()))) {
                    telefone = util.formataNumero(rst.getString("chrtelefone"));
                } else {*/
                    telefone = "";
                //}
                    
                //if (rst.getInt("status")==1){
                    id_tipoalinea = 0;
                /*} else if (rst.getInt("status")==2){
                    id_tipoalinea = 15;                    
                } else {
                    id_tipoalinea = 0;
                }*/
                
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteWisaSoft(int id_loja, int id_lojaCliente) throws Exception {
        
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
            sql.append(" SELECT S.SUP025, S.DATA, S.CUPOM, S.VALOR, S.DATAVENC, SUP025.CPF ");
            sql.append(" FROM SUP026 S                                                     ");
            sql.append(" LEFT OUTER JOIN SUP025 ON                                         ");
            sql.append("     SUP025.SUP025 = S.SUP025                                      ");
            sql.append(" WHERE S.DATA_PGTO IS NULL                                         ");
            sql.append(" ORDER BY SUP025                                                   ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("sup025");                
                dataemissao = rst.getString("data");
                datavencimento = rst.getString("datavenc");
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("cupom")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = 0;
                
                observacao = "IMPORTADO VR";
                
                if ((rst.getString("CPF")!=  null) &&
                            (!rst.getString("CPF").isEmpty())) {
                    cnpj = Long.parseLong(rst.getString("CPF").trim().replace(".","").replace("-","").replace("/",""));
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

    public Map<Integer, ProdutoVO> carregarPrecoProdutoWisaSoft(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double preco = 0, margem = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder(); 
            sql.append("SELECT SUP001.SUP001, COALESCE(SUP008.VENDA,0) AS VENDA,COALESCE(SUP008.margem,0) AS MARGEM ");
            sql.append("FROM SUP001 ");
            sql.append("INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001 ");
            sql.append("ORDER BY 1             ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("SUP001"));
                preco = rst.getDouble("VENDA");
                
                if ((rst.getString("MARGEM") != null) &&
                        !rst.getString("MARGEM").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("MARGEM").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
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
    
    public Map<Integer, ProdutoVO> carregarEstoqueProdutoWisaSoft(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double saldo = 0;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();         
            sql.append("SELECT SUP001.SUP001, COALESCE(SUP008.SALDO,0) AS ESTOQUE ");
            sql.append("FROM SUP001 ");
            sql.append("INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001 ");
            sql.append("WHERE SALDO > 0 ");
            sql.append("ORDER BY 1             ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("SUP001"));
                saldo = rst.getDouble("ESTOQUE");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
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
    
    public Map<Integer, ProdutoVO> carregarCodigoBarras() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append("select codpro, barras from barras"); 

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro"));

                if ((rst.getString("barras") != null) &&
                        (!rst.getString("barras").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("barras").replace(".", ""));
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
    
    //IMPORTAÇÕES
    public void importarFamiliaProdutoWisaSoft() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoWisaSoft();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologicoWisaSoft() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoWisaSoft(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoWisaSoft(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoWisaSoft(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarClientePreferencialWisaSoft(int idLoja, int idLojaCliente) throws Exception {

            try {
                ProgressBar.setStatus("Carregando dados...Clientes...");
                List<ClientePreferencialVO> vClientePreferencial = carregarClienteWisaSoft(idLoja, idLojaCliente);
                new PlanoDAO().salvar(idLoja);
                new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

            } catch (Exception ex) {

                throw ex;
            }
        }  

    public void importarFornecedorWisaSoft() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedorWisaSoft();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }

    public void importarProdutoWisaSoft(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProdutoMilenio = carregarProdutoWisaSoft();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoMilenio.size());
            
            for (Integer keyId : vProdutoMilenio.keySet()) {
                
                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

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
    
    public void importarCustoProdutoWisaSoft(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoWisaSoft(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Integer keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarCustoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarPrecoProdutoWisaSoft(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoWisaSoft(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Integer keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProdutoWisaSoft(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoWisaSoft(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Integer keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarCodigoBarras();
            
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
    
    public void importarChequeReceberWisaSoft(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeWisaSoft(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    public void importarProdutoFornecedorWisaSoft() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorWisaSoft();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteWisaSoft(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteWisaSoft(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

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
    
    public void importarPisCofinsProdutoWisa() throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Pis Cofins...Natureza Receita...");
            List<ProdutoVO> vProduto = carregarPisCofinsWisa();
            
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }    
    // FUNÇÕES
    private int retornarAliquotaICMSWisaSoft(String codTrib, String descTrib) {

        int retorno;

        if ("F1".equals(codTrib)) {
            retorno = 7;
        }else if ("I1".equals(codTrib)){
            retorno = 6;        
        }else if ("T07".equals(codTrib)) {
            retorno = 0;
        }else if ("T12".equals(codTrib)){
            retorno = 1;        
        }else if (("T18".equals(codTrib))||("16".equals(codTrib))){
            retorno = 2;        
        }else if ("T25".equals(codTrib)){
            retorno = 3;        
        }else{
            retorno = 8;
        }
        
        return retorno;
    }    
    
    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    
    
    /* Novo Método de Importação */   
}