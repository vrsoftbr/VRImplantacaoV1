package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import sun.rmi.rmic.iiop.Constants;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.classe.Global;
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
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.utils.classe.exclusiva.TributacaoICMSRMS;
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
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class RMSDAO {
    public String Texto;
    public int Contador = 0;    
    Utils util = new Utils();    
    //CARREGAMENTOS
    
    // INICIO PRODUTO
    public List<FamiliaProdutoVO> carregarFamiliaProdutoRMS() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT DISTINCT F.FAM_PAI AS CODIGO,P.GIT_DESCRICAO AS DESCRICAO ");
            sql.append("  FROM AA1FITEM  F ");
            sql.append("INNER JOIN AA3CITEM  P ON P.GIT_COD_ITEM = F.FAM_PAI ");
            sql.append("ORDER BY F.FAM_PAI             ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id                  = Integer.parseInt(rst.getString("CODIGO"));
                oFamiliaProduto.descricao           = util.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant           = Integer.parseInt(rst.getString("CODIGO"));

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<MercadologicoVO> carregarMercadologicoRMS(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5;

        try {
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            /*
            sql.append("select  nvl(ncc_departamento,0) AS MERC1,      ");
            sql.append("	nvl(ncc_secao,0) AS MERC2,     ");
            sql.append("	nvl(ncc_grupo,0) AS MERC3,     ");
            sql.append("	nvl(ncc_subgrupo,0) AS MERC4,  "); 
            sql.append("	nvl(ncc_categoria,0) AS MERC5, ");
            sql.append("        nvl(ncc_descricao,'MERCADOLOGICO VR') AS DESCRICAO     ");
            sql.append("  from aa3cnvcc                                ");
            sql.append("order by 1,2,3,4,5                             "); 
            
            */
            sql.append("select  nvl(ncc_secao,0) AS MERC1,     ");
            sql.append("	nvl(ncc_grupo,0) AS MERC2,     ");
            sql.append("	nvl(ncc_subgrupo,0) AS MERC3,  "); 
            sql.append("	nvl(ncc_categoria,0) AS MERC4, ");
            sql.append("        nvl(ncc_descricao,'MERCADOLOGICO VR') AS DESCRICAO     ");
            sql.append("  from aa3cnvcc                                ");
            sql.append("order by 1,2,3,4                             "); 
            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                mercadologico4 = 0;
                mercadologico5 = 0;                
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    descricao = util.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;             
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2)  {
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));                                                
                    descricao = util.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));                                                
                    mercadologico3 = Integer.parseInt(rst.getString("MERC3"));                                                                    
                    descricao = util.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;

                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 4) {
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));                                                
                    mercadologico3 = Integer.parseInt(rst.getString("MERC3"));
                    mercadologico4 = Integer.parseInt(rst.getString("MERC4"));
                    descricao = util.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = mercadologico4;

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

    public Map<Double, ProdutoVO> carregarProdutoRMS() throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null, sql2 = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, idSituacaoCadastro,
            ncm1, ncm2, ncm3, codigoBalanca, referencia = -1,validade=0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double idProduto;
        try {

            Conexao.begin();
            stmPostgres = Conexao.createStatement();
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT NCM.DET_CLASS_FIS, P.GIT_DEPTO,P.GIT_SECAO,P.GIT_GRUPO,P.GIT_SUBGRUPO,P.GIT_CATEGORIA,P.GIT_COD_FOR,");
            sql.append("       FAM.FAM_PAI AS  FAMILIA, P.GIT_COD_ITEM,P.GIT_DIGITO,P.GIT_CODIGO_PAI,P.GIT_CODIGO_EAN13,P.GIT_DESCRICAO,");
            sql.append("       P.GIT_DESC_REDUZ,P.GIT_DESC_COML,P.GIT_REFERENCIA,P.GIT_EMB_FOR,P.GIT_TPO_EMB_FOR, P.GIT_TPO_EMB_VENDA, P.GIT_EMB_VENDA, ");
            sql.append("       P.GIT_EAN_EMB_FOR,P.GIT_EMB_TRANSF,P.GIT_LINHA,P.GIT_PRZ_ENTRG,P.GIT_DIA_VISIT,");
            sql.append("       P.GIT_FRQ_VISIT,P.GIT_TIPO_ETQ,P.GIT_TIPO_PRO,P.GIT_COMPRADOR,P.GIT_COND_PGTO,");
            sql.append("       P.GIT_COND_PGTO_ANT,P.GIT_COND_PGTO_MAN,P.GIT_CDPG_VDA,P.GIT_TIPO_ETQ_GON,P.GIT_COR,");
            sql.append("       P.GIT_DAT_ENT_LIN,P.GIT_DAT_SAI_LIN,P.GIT_NAT_FISCAL,P.GIT_ESTADO,P.GIT_COD_PAUTA,");
            sql.append("       P.GIT_PERC_IPI,P.GIT_QTDE_ETQ_GON,P.GIT_PERC_BONIF,P.GIT_PERC_BONIF_ANT,P.GIT_PERC_BONIF_MAN,");
            sql.append("       P.GIT_ENTREGA,P.GIT_FRETE,P.GIT_CUS_FOR,P.GIT_CUSF_ANT,P.GIT_CUSF_MAN,P.GIT_DAT_CUS_FOR,");
            sql.append("       P.GIT_DAT_CUSF_ANT,P.GIT_DAT_CUSF_MAN,P.GIT_CUS_REP,P.GIT_CUSR_ANT,P.GIT_CUSR_MAN,");
            sql.append("       P.GIT_DAT_CUS_REP,P.GIT_DAT_CUSR_ANT,P.GIT_DAT_CUSR_MAN,P.GIT_CUS_MED,P.GIT_CUSM_ANT,");
            sql.append("       P.GIT_CUSM_MAN,P.GIT_DAT_CUS_MED,P.GIT_DAT_CUSM_ANT,P.GIT_DAT_CUSM_MAN,P.GIT_CUS_MED_C,");
            sql.append("       P.GIT_DAT_CUS_MED_C,P.GIT_PRC_VEN_1,P.GIT_PRCV_ANT_1,P.GIT_PRCV_MAN_1,P.GIT_MRG_LUCRO_2,");
            sql.append("       P.GIT_DSC_MAX_1,P.GIT_COMISSAO_1,P.GIT_DAT_PRC_VEN_1,P.GIT_DAT_PRCV_ANT_1,P.GIT_DAT_PRCV_MAN_1,");
            sql.append("       P.GIT_PRC_VEN_2,P.GIT_PRCV_ANT_2,P.GIT_PRCV_MAN_2,P.GIT_MRG_LUCRO_2,P.GIT_DSC_MAX_2,P.GIT_COMISSAO_2,");
            sql.append("       P.GIT_DAT_PRC_VEN_2,P.GIT_DAT_PRCV_ANT_2,P.GIT_DAT_PRCV_MAN_2,P.GIT_ESTQ_ATUAL,P.GIT_ESTQ_DP,");
            sql.append("       P.GIT_ESTQ_LJ,P.GIT_QDE_PEND,P.GIT_CUS_INV,P.GIT_ESTQ_PADRAO,P.GIT_SAI_MED_CAL,P.GIT_TAMANHO,");
            sql.append("       P.GIT_SAI_ACM_UN,P.GIT_SAI_ACM_CUS,P.GIT_SAI_ACM_VEN, P.GIT_CUS_ULT_ENT_BRU,P.GIT_ENT_ACM_UN,");
            sql.append("       P.GIT_ENT_ACM_CUS,P.GIT_DAT_ULT_FAT,P.GIT_ULT_QDE_FAT,P.GIT_ULT_QDE_ENT,P.GIT_CUS_ULT_ENT,P.GIT_DAT_ULT_ENT,");
            sql.append("       P.GIT_ABC_F,P.GIT_ABC_S,P.GIT_ABC_T,P.GIT_PERECIVEL,P.GIT_PRZ_VALIDADE,P.GIT_TOT_PEDIDO,P.GIT_TOT_FALTA,");
            sql.append("       P.GIT_DESP_ACES_ISEN_MAN,P.GIT_LINHA_VALIDA,P.GIT_QUANT_EAN,P.GIT_FILLER ");
            sql.append("FROM  AA3CITEM P                             ");
            sql.append("LEFT OUTER JOIN AA1DITEM NCM ON              ");
            sql.append("    NCM.DET_COD_ITEM = P.GIT_COD_ITEM        ");
            sql.append("LEFT OUTER JOIN AA1FITEM FAM ON              ");
            sql.append("    FAM.FAM_PAI = P.GIT_COD_ITEM             ");    
            sql.append(" ORDER BY P.GIT_COD_ITEM                     ");

            rst = stm.executeQuery(sql.toString());
            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();
                idSituacaoCadastro = 1;
 
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                sql2 = new StringBuilder();
                sql2.append("select codigo, descricao, pesavel, validade ");
                sql2.append("from implantacao.produtobalanca ");
                sql2.append("where codigo = " + Integer.parseInt(rst.getString("GIT_COD_ITEM").trim().replace(".", "").replace(",", "")));
                rstPostgres = stmPostgres.executeQuery(sql2.toString());
    
                if (rstPostgres.next()) {
                    eBalanca=true;
                    idProduto = Double.parseDouble(rst.getString("GIT_COD_ITEM").trim().replace(".", "").replace(",", "")+
                                                   rst.getString("GIT_DIGITO").trim().replace(".", "").replace(",", ""));
                    validade = rstPostgres.getInt("validade");
                    codigoBalanca = rstPostgres.getInt("codigo");

                    /*if ("CX".equals(rst.getString("GIT_TPO_EMB_FOR").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 1;
                    } else*/ 
                    if ("KG".equals(rst.getString("GIT_TPO_EMB_VENDA").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("GIT_TPO_EMB_VENDA").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    pesavel = false;
                    if ("CX".equals(rst.getString("GIT_TPO_EMB_VENDA").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("GIT_TPO_EMB_VENDA").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("GIT_TPO_EMB_VENDA").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }   
                    validade = 0;                    
                    idProduto = Double.parseDouble(rst.getString("GIT_COD_ITEM").trim().replace(".", "").replace(",", "")+
                                                   rst.getString("GIT_DIGITO").trim().replace(".", "").replace(",", ""));
                }
                
                if ((rst.getString("GIT_DESCRICAO") != null)
                        && (!rst.getString("GIT_DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("GIT_DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descriaoCompleta = "DESCRICAO PRODUTO VR";
                }
                if ((rst.getString("GIT_DESC_REDUZ") != null)
                        && (!rst.getString("GIT_DESC_REDUZ").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("GIT_DESC_REDUZ");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = descriaoCompleta;
                }
                if ((rst.getString("GIT_DESC_COML") != null)
                        && (!rst.getString("GIT_DESC_COML").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("GIT_DESC_COML");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoGondola = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoGondola = descriaoCompleta;
                }                              

                if ((rst.getString("FAMILIA") != null)
                        && (!rst.getString("FAMILIA").trim().isEmpty())){
                    idFamilia = Integer.parseInt(rst.getString("FAMILIA").trim().replace(".", ""));
                    
                    sql = new StringBuilder();
                    sql.append("select id from familiaproduto ");
                    sql.append("where id = " + idFamilia);
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (!rstPostgres.next()) {
                        idFamilia = -1;
                    }
                    
                } else {
                    idFamilia = -1;
                }

                if ((rst.getString("GIT_EMB_VENDA") != null)
                        && (!rst.getString("GIT_EMB_VENDA").trim().isEmpty())){
                    qtdEmbalagem  = rst.getInt("GIT_EMB_VENDA");               
                } else {
                    qtdEmbalagem  = 1;               
                }                
               
                if ((rst.getString("GIT_SECAO")!= null) && 
                        (!rst.getString("GIT_SECAO").isEmpty())){
                        mercadologico1 = Integer.parseInt(rst.getString("GIT_SECAO").trim());
                }else{
                        mercadologico1 = 0;                    
                }        
                if ((rst.getString("GIT_GRUPO")!= null) && 
                        (!rst.getString("GIT_GRUPO").isEmpty())){
                        mercadologico2 = Integer.parseInt(rst.getString("GIT_GRUPO").trim());
                }else{
                        mercadologico2 = 0;                    
                }        
                if ((rst.getString("GIT_SUBGRUPO")!= null) && 
                        (!rst.getString("GIT_SUBGRUPO").isEmpty())){
                        mercadologico3 = Integer.parseInt(rst.getString("GIT_SUBGRUPO").trim());
                }else{
                        mercadologico3 = 0;                    
                }
                
                if ((rst.getString("GIT_CATEGORIA") != null) &&
                        (!rst.getString("GIT_CATEGORIA").trim().isEmpty())) {
                    mercadologico4 = Integer.parseInt(rst.getString("GIT_CATEGORIA").trim());
                } else {
                    mercadologico4 = 0;
                }
                
                /*sql2 = new StringBuilder();
                sql2.append("SELECT MERCADOLOGICO1, MERCADOLOGICO2, MERCADOLOGICO3, MERCADOLOGICO4 "); 
                sql2.append("FROM MERCADOLOGICO                                    "); 
                sql2.append("WHERE MERCADOLOGICO1 = "+mercadologico1); 
                sql2.append("  AND MERCADOLOGICO2 = "+mercadologico2); 
                sql2.append("  AND MERCADOLOGICO3 = "+mercadologico3);
                sql2.append("  AND MERCADOLOGICO4 = "+mercadologico4);
                rstPostgres = stmPostgres.executeQuery(sql2.toString());                
                
                if (!rstPostgres.next()) {                
                    mercadologico1=1; 
                    mercadologico2=0; 
                    mercadologico3=0;                
                }*/
                
                if (!Utils.verificaExisteMercadologico4Nivel(mercadologico1, mercadologico2, mercadologico3, mercadologico4)) {
                    
                    sql = new StringBuilder();
                    sql.append("select max(mercadologico1) as mercadologico1 ");
                    sql.append("from mercadologico ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;
                        mercadologico4 = 1;
                    }
                }                
                
                if ((rst.getString("DET_CLASS_FIS")!= null) && 
                        (!rst.getString("DET_CLASS_FIS").isEmpty())){
                    ncmAtual = util.formataNumero(rst.getString("DET_CLASS_FIS"));
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
                    codigoBarras = Long.parseLong(String.valueOf(idProduto).trim().replace(".", "").replace(",", ""));
                } else {

                    if ((rst.getString("GIT_CODIGO_EAN13") != null)
                            && (!rst.getString("GIT_CODIGO_EAN13").trim().isEmpty())) {

                        strCodigoBarras = rst.getString("GIT_CODIGO_EAN13").replace(".", "").trim();

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13((int) idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13((int) idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(rst.getString("GIT_CODIGO_EAN13").trim());
                        }
                    }
                }
                
                /*if ((rst.getString("CST_PISVEND") != null)
                        && (!rst.getString("CST_PISVEND").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("CST_PISVEND").trim()));
                } else {*/
                    idTipoPisCofins = 1;
                //}

                /*if ((rst.getString("CST_PIS") != null)
                        && (!rst.getString("CST_PIS").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("CST_PIS").trim()));
                } else {*/
                    idTipoPisCofinsCredito = 13;
                //}

                /*if ((rst.getString("NATREC") != null)
                        && (!rst.getString("NATREC").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NATREC").trim());
                } else {*/
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                //}

                /*if ((rst.getString("TRIBUTACAO") != null)
                        && (!rst.getString("TRIBUTACAO").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMS(rst.getString("TRIBUTACAO").trim().toUpperCase());
                } else {*/
                    idAliquota = 8;
                //}
               
                if (descriaoCompleta.length() > 60) {
                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.idDouble = idProduto;
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
                
                if (oProduto.mercadologico4 > 0) {
                    oProduto.mercadologico4 = mercadologico4;
                }                
                
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

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = Global.idEstado; // ESTADO LOJA
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
                
                if ((rst.getString("GIT_CODIGO_EAN13") != null) &&
                        (!rst.getString("GIT_CODIGO_EAN13").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("GIT_CODIGO_EAN13").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }                
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                oCodigoAnterior.piscofinsdebito = -1;
                oCodigoAnterior.piscofinscredito = -1;
                oCodigoAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("DET_CLASS_FIS") != null)
                        && (!rst.getString("DET_CLASS_FIS").isEmpty())) {
                    oCodigoAnterior.ncm = Utils.formataNumero(rst.getString("DET_CLASS_FIS").trim());
                } else {
                    oCodigoAnterior.ncm = "";
                }
 
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }
            stmPostgres.close();
            Conexao.commit();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProdutoRMS(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();       
            sql.append("select P.GIT_COD_ITEM||P.GIT_DIGITO as CODIGO, ");
            sql.append("       NVL(git_cus_ult_ent_bru,git_cus_rep) PRECOCUSTO ");
            sql.append("from aa3citem P ");
            sql.append("WHERE NVL(git_cus_ult_ent_bru,git_cus_rep) > 0 ");           
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO").replace(".", ""));
                custo = Double.parseDouble(rst.getString("PRECOCUSTO").replace(",", "."));
                
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
    
    public Map<Double, ProdutoVO> carregarPisCofinsICMSRMS(String i_arquivo,int idLoja) throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        int linha=0, idTipoPisCofins = 9, idAliquota=8;
        double idProduto=0;
        long codigoBarras=-1;
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    //ignora o cabeçalho
                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;                                                
                    }
                    
                    Cell cellCodProduto  = sheet.getCell(0, i);
                    Cell cellPisCofins   = sheet.getCell(2, i);                                        
                    Cell cellICMS        = sheet.getCell(3, i);

                    
                    idProduto              = Double.parseDouble(util.formataNumero(cellCodProduto.getContents()));
                    idAliquota             = Integer.parseInt((String) (cellICMS.getContents()=="" ? 599 : cellICMS.getContents()));
                    idTipoPisCofins        = Integer.parseInt(cellPisCofins.getContents());                    
                    
                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.idDouble = idProduto;                
                    oProduto.idTipoPisCofinsDebito  = retornarPisCofins(String.valueOf(idTipoPisCofins),"S");
                    oProduto.idTipoPisCofinsCredito = retornarPisCofins(String.valueOf(idTipoPisCofins),"E");                           
                    oProduto.tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                    oAliquota.idEstado =  Global.idEstado; // FORTALEZA
                    idAliquota = retornarAliquotaICMS(idAliquota);
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                    oProduto.vAliquota.add(oAliquota);

                    vProduto.put(idProduto, oProduto);  
                }
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    private List<ProdutoVO> carregarNCMParaAcertar() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        String ncmAtual = "";
        int ncm1, ncm2, ncm3;
        double idProduto;
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try {

            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT NCM.DET_CLASS_FIS, P.GIT_COD_ITEM,P.GIT_DIGITO   ");
            sql.append("FROM  AA3CITEM P                                        ");
            sql.append("LEFT OUTER JOIN AA1DITEM NCM ON                         ");
            sql.append("    NCM.DET_COD_ITEM = P.GIT_COD_ITEM                   ");
            sql.append("LEFT OUTER JOIN AA1FITEM FAM ON                         ");
            sql.append("    FAM.FAM_PAI = P.GIT_COD_ITEM                        ");    
            sql.append(" ORDER BY P.GIT_COD_ITEM                                ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
            
                idProduto = Double.parseDouble(rst.getString("GIT_COD_ITEM").trim().replace(".", "").replace(",", "")
                        + rst.getString("GIT_DIGITO").trim().replace(".", "").replace(",", ""));
                
                if ((rst.getString("DET_CLASS_FIS")!= null) && 
                        (!rst.getString("DET_CLASS_FIS").isEmpty())){
                    ncmAtual = Utils.formataNumero(rst.getString("DET_CLASS_FIS"));
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try{
                            NcmVO oNcm = new NcmDAO().validar2(ncmAtual);
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
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                vProduto.add(oProduto);
                
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarNCMArquivo(String i_arquivo,int idLoja) throws Exception {
        StringBuilder sql=null;
        Statement stmPostgres=null;
        ResultSet rstPostgres=null;
        stmPostgres = Conexao.createStatement();
       
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        int linha=0, idTipoPisCofins = 9, idAliquota=8, ncm1,ncm2,ncm3;
        double idProduto=0;
        long codigoBarras=-1;
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        String ncmAtual="", ncm1Str="", ncm2Str="", ncm3Str="";
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    //ignora o cabeçalho
                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;                                                
                    }
                    
                    Cell cellCodProduto  = sheet.getCell(2, i);
                    Cell cellNCM   = sheet.getCell(1, i);                                        
                    
                    idProduto = Integer.parseInt(util.formataNumero(cellCodProduto.getContents()));
                    ncmAtual = util.formataNumero(cellNCM.getContents());                    

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;
                    
                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.id   = (int) idProduto;                
                    
                    oProduto.ncm1 = ncm1;
                    oProduto.ncm2 = ncm2;
                    oProduto.ncm3 = ncm3;                    

                    vProduto.put(idProduto, oProduto);  
                }
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }        
    
    public Map<Double, ProdutoVO> carregarMargemRMS(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double margem = 0, idProduto;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();  
            sql.append(" select P.GIT_COD_ITEM||P.GIT_DIGITO as CODIGO, ");
            sql.append("        P.GIT_MRG_LUCRO_1 AS MARGEM   ");
            sql.append(" from aa3citem P                      ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO").replace(".", ""));
                margem = Double.parseDouble(rst.getString("MARGEM").replace(",", "."));
                
                ProdutoVO oProduto = new ProdutoVO();

                oProduto.idDouble = idProduto;                
                oProduto.margem   = margem;                                
                
                vProduto.put(idProduto, oProduto);                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Double, ProdutoVO> carregarPrecoProdutoRMS(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, margem = 0, idProduto;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();    

        
            sql.append("select GIT_COD_ITEM||GIT_DIGITO as CODIGO, ");
            sql.append("       GIT_PRC_VEN_1 AS PRECOVENDA, ");
            sql.append("       GIT_MRG_LUCRO_1 AS LUCRO ");
            sql.append("from aa3citem ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("CODIGO"));
                preco = rst.getDouble("PRECOVENDA");
                
                if ((rst.getString("LUCRO") != null) &&
                        !rst.getString("LUCRO").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("LUCRO").replace(",", "."));
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
    
    public Map<Double, ProdutoVO> carregarEstoqueProdutoRMS(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double saldo, idProduto = 0;
        
        try {
            stm = ConexaoOracle.getConexao().createStatement();
            sql = new StringBuilder(); 

            sql.append("SELECT GET_COD_PRODUTO, ");
            sql.append("       ESTOQUE ");
            sql.append("FROM (");            
            sql.append("SELECT PROD.GIT_COD_ITEM||PROD.GIT_DIGITO as GET_COD_PRODUTO, ");
            sql.append("       SUM(EST.GET_ESTOQUE) AS ESTOQUE ");
            sql.append("FROM AA2CESTQ EST, AA3CITEM PROD ");
            sql.append("WHERE PROD.GIT_COD_ITEM||PROD.GIT_DIGITO = EST.GET_COD_PRODUTO  ");
            sql.append("GROUP BY PROD.GIT_COD_ITEM||PROD.GIT_DIGITO        ");
            sql.append(") WHERE ESTOQUE > 0");                        
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("GET_COD_PRODUTO").trim());                
                saldo = rst.getDouble("ESTOQUE");
                
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
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasRMS() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;
        
        try {
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT EAN_COD_EAN AS BARRA,EAN_COD_PRO_ALT AS CODPROD ");
            sql.append("FROM AA3CCEAN  ");
            sql.append("where LENGTH(EAN_COD_EAN) > 7 ");
            sql.append("and EAN_COD_EAN not like '30000%' ");
            sql.append("ORDER BY EAN_COD_PRO_ALT ");            

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODPROD"));

                if ((rst.getString("barra") != null) &&
                        (!rst.getString("barra").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("barra").replace(".", ""));
                } else {
                    codigobarras = -1;
                }
                
                if (String.valueOf(codigobarras).length() >= 7) {
                
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }        
    
    public List<OfertaVO> carregarOfertaProdutoRMS(int id_Loja) throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<OfertaVO> vOferta = new ArrayList<>();
        try {
            stm = ConexaoOracle.getConexao().createStatement();
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
    
    public Map<Integer, ProdutoVO> carregarNCM() throws Exception {
        StringBuilder sql=null, sql2=null;
        Statement stm=null, stmPostgres = null;
        ResultSet rst=null, rstPostgres=null;
        Utils util = new Utils();
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, ncm1, ncm2, ncm3;
        String ncmAtual, ncm1Str, ncm2Str, ncm3Str;

        try {
            stmPostgres = Conexao.createStatement();
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            
            sql.append("SELECT nvl(NCM.DET_CLASS_FIS,'4029000') AS NCM, P.GIT_COD_ITEM||P.GIT_DIGITO as PRODUTO ");                
            sql.append("FROM  AA3CITEM P                             ");
            sql.append("LEFT OUTER JOIN AA1DITEM NCM ON              ");
            sql.append("NCM.DET_COD_ITEM = P.GIT_COD_ITEM            ");
         
            rst = stm.executeQuery(sql.toString());
            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();
                idProduto = Integer.parseInt((rst.getString("PRODUTO")));
                ncmAtual = util.formataNumero(rst.getString("NCM"));

                /*NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                ncm1 = oNcm.ncm1;
                ncm2 = oNcm.ncm2;
                ncm3 = oNcm.ncm3;
                */
                if (ncmAtual.length() >= 8) {
                    ncm1Str = ncmAtual.substring(0, 4);
                    ncm2Str = ncmAtual.substring(4, 6);
                    ncm3Str = ncmAtual.substring(6, 8);
                } else if (ncmAtual.length() == 7) {
                    ncm1Str = ncmAtual.substring(0, 4);
                    ncm2Str = ncmAtual.substring(4, 6);
                    ncm3Str = ncmAtual.substring(6, 7);
                } else if (ncmAtual.length() == 6) {
                    ncm1Str = ncmAtual.substring(0, 3);
                    ncm2Str = ncmAtual.substring(3, 5);
                    ncm3Str = ncmAtual.substring(5, 6);
                } else {
                    ncm1Str = "0402";
                    ncm2Str = "99";
                    ncm3Str = "00"; 
                }                
                ncm1 = Integer.parseInt(ncm1Str);
                ncm2 = Integer.parseInt(ncm1Str);
                ncm3 = Integer.parseInt(ncm1Str);                        
                
                sql2 = new StringBuilder();
                sql2.append("select * from ncm ");
                sql2.append(" where ncm1 = "+ncm1);
                sql2.append("   and ncm2 = "+ncm2);
                sql2.append("   and ncm3 = "+ncm3);                
                rstPostgres = stmPostgres.executeQuery(sql2.toString());
                if (!rstPostgres.next()){
                   ncm1 = 402;
                   ncm2 = 99;
                   ncm3 = 0;                        
                }
                
                oProduto.id   = idProduto;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.ncm = ncmAtual;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        
    // FIM PRODUTO    
    
    // INICIO CLIENTE
    public List<ClientePreferencialVO> carregarClientePreferencialRMS(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String CPF, nome, endereco, bairro, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,
                dataResidencia, dataCadastro = null, dataNascimento, telefone, telefone2, numeroCasa, celular, observacao = "", cargo,
                empresa, foneEmpresa, nomePai, nomeMae, cargoConjuge, cpfConjuge, rgConjuge;
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao=0, id, agente, id_situacaocadastro = 0, Linha = 0, id_tipoestadocivil = 0, situacao = 0;
        
        Long cnpj, cep;
        double limite, salario, salarioConjuge;

        try {
            stm = ConexaoOracle.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT TIP.tip_cgc_cpf  Cgc_cpf, TIP.tip_codigo   Codigo , ");
            sql.append("       TIP.tip_razao_social Razao_social, TIP.tip_nome_fantasia Nome_fantasia,  ");
            sql.append("       TIP.tip_endereco Endereco, TIP.tip_bairro   Bairro , TIP.tip_cidade Cidade,  ");
            sql.append("       TIP.tip_estado Estado, TIP.tip_cep Cep, TIP.tip_natureza Natureza,  ");
            sql.append("       TIP.tip_data_cad Data_cad, TIP.tip_fax_ddd Fax_ddd, TIP.tip_fax_num Fax_num,  ");
            sql.append("       TIP.tip_fone_ddd Fone_ddd, TIP.tip_fone_num Fone_num, TIP.tip_fis_jur Fis_jur,  ");
            sql.append("       TIP.tip_insc_est_ident Insc_est_ident, TIP.tip_regiao  Regiao ,  ");
            sql.append("       TIP.tip_divisao Divisao, TIP.tip_distrito Distrito, CLI.cli_contato Contato_principal,  ");
            sql.append("       CLI.cli_cod_vend Vendedor,  ");
            sql.append("       CLI.cli_situacao Status , CLI.cli_limite_cred,  ");
            sql.append("       Round(CLI.cli_limite_cred * (SELECT To_number(Substr(tab_conteudo, 1, 15) ) / 1000000  ");
            sql.append("                                    FROM   aa2ctabe WHERE  tab_codigo = (SELECT emp_ind_limite  ");
            sql.append("                                    FROM   aa2cempr  ");
            sql.append("                                    WHERE emp_codigo = TIP.tip_empresa) AND tab_acesso = Rpad( ");
            sql.append("                                    To_char(SYSDATE, 'YYMMDD'), 10, ' ')), 2) Limite_cred,  ");
            sql.append("       Nvl(por_banco, 0)                                                  banco,  ");
            sql.append("       Decode(cli.cli_situacao, 'A', 'ATIVO', ");
            sql.append("       Decode(cli.cli_situacao, 'I', 'INATIVO',  ");
            sql.append("       Decode(cli.cli_situacao, 'C', 'CANCELADO',  ");
            sql.append("       Decode(cli.cli_situacao, 'S', 'SUSPENSO',  ");
            sql.append("       'ATIVO'))))  ");
            sql.append("       SIGLA_STATUS, ");
            sql.append("       Nvl(dtip_cod_municipio, 0) COD_MUNI        ");
            sql.append("FROM   aa2cclir CLI, aa2ctipo TIP, final_cliente FIN, aa1rport, aa1dtipo ");
            sql.append("WHERE  TIP.tip_cgc_cpf >= 0 ");
            sql.append("       AND TIP.tip_codigo >= 0 ");
            sql.append("       AND TIP.tip_digito >= 0 ");
            sql.append("       AND TIP.tip_codigo = CLI.cli_codigo ");
            sql.append("       AND TIP.tip_digito = CLI.cli_digito ");
            sql.append("       AND FIN.cli_codigo(+) = CLI.cli_codigo ");
            sql.append("       AND por_portador (+) = cli.cli_port ");
            sql.append("       AND dtip_codigo (+) = tip_codigo             ");
            rst = stm.executeQuery(sql.toString());
            try {
                while (rst.next()) {
                    Linha++;
                    Texto = "";
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("CODIGO");
                    CPF = util.formataNumero(rst.getString("CGC_CPF").trim().replace(".", "").replace("-", "").trim());

                    if ((rst.getString("FIS_JUR") != null) &&
                            (!rst.getString("FIS_JUR").trim().isEmpty())) {
                        
                        if ("F".equals(rst.getString("FIS_JUR").trim())) {
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } else {
                        id_tipoinscricao = 1;
                    }                    
                    
                    if ((rst.getString("RAZAO_SOCIAL") != null)
                            && (!rst.getString("RAZAO_SOCIAL").isEmpty())) {
                        byte[] bytes = rst.getBytes("RAZAO_SOCIAL");
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
                    if (id==15397){
                        Texto = "";
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
                            id_estado = Global.idEstado; // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = util.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString().trim(), rst.getString("ESTADO").toString().trim());
                        if (id_municipio == 0) {
                            id_municipio = Global.idMunicipio;// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Global.idEstado; // ESTADO ESTADO DO CLIENTE
                        id_municipio = Global.idMunicipio; // CIDADE DO CLIENTE;                   
                    }
                    Texto = id + " - " + nome +" - cidade";                                                              
                    if (rst.getString("CEP") != null) {
                        cep = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                    } else {
                        cep = Long.parseLong("0");
                    }
                    
                    /*if (rst.getString("NUMERO") != null) {
                        numeroCasa = util.acertarTexto(rst.getString("NUMERO"));
                        if (numeroCasa.length()>6){
                            numeroCasa = numeroCasa.substring(0,6);
                        }
                    } else {*/
                        numeroCasa = "";
                    /*}
                    
                    if (rst.getString("EMAIL") != null) {
                        email = util.acertarTexto(rst.getString("EMAIL"));
                        if (email.length() > 50) {
                            email = email.substring(0, 50);
                        }
                    } else {*/
                        email = "";
                    //}
                    
                    if (rst.getString("INSC_EST_IDENT") != null) {
                        inscricaoestadual = util.acertarTexto(rst.getString("INSC_EST_IDENT"));
                        inscricaoestadual = inscricaoestadual.replace(".", "").replace("/", "").replace(",", "").trim();
                        if (inscricaoestadual.length() > 18) {
                            inscricaoestadual = inscricaoestadual.substring(0, 18);
                        }
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    if (rst.getString("CGC_CPF") != null) {
                        CPF = util.formataNumero(rst.getString("CGC_CPF").trim().replace(".", "").replace("-", "").trim());
                        if (CPF.length() >= 11) {
                            cnpj = Long.parseLong(CPF);
                        } else {
                            cnpj = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CODIGO"))));
                        }
                    } else {
                        cnpj = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CODIGO"))));
                    }
                    
                    /*if (rst.getString("SEXO") != null) {
                        if (rst.getString("SEXO") == "M") {
                            id_sexo = 1;
                        } else {
                            id_sexo = 0;
                        }
                    } else {*/
                        id_sexo = 1;
                    //}
                    
                    dataCadastro = "2000/01/01";
                    dataResidencia = "2000/01/01";
                    dataNascimento = "2000/01/01";                    
                    
                    if ((rst.getString("LIMITE_CRED") != null) &&
                            (!rst.getString("LIMITE_CRED").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMITE_CRED"));
                    } else {
                        
                        if ((rst.getString("CLI_LIMITE_CRED") != null) &&
                                (!rst.getString("CLI_LIMITE_CRED").trim().isEmpty())) {
                            limite = Double.parseDouble(rst.getString("CLI_LIMITE_CRED").trim());
                        } else {
                            limite = 0;
                        }                        
                    }


                    
                    if (rst.getString("FONE_NUM") != null) {
                        telefone = util.formataNumero(rst.getString("FONE_DDD")+rst.getString("FONE_NUM"));
                    } else {
                        telefone = null;
                    }
                    
                    if (rst.getString("FAX_NUM") != null) {
                        telefone2 = util.formataNumero(rst.getString("FONE_DDD")+rst.getString("FONE_NUM"));
                    } else {
                        telefone2 = "";
                    }
                    
                    if ((rst.getString("STATUS") != null) &&
                            (!rst.getString("STATUS").trim().isEmpty())) {
                        
                        if ("A".equals(rst.getString("STATUS").trim())) {
                            situacao = 1; // ATIVO
                        } else {
                            situacao = 0; // ATIVO
                        }
                    } else {
                        situacao = 0; // ATIVO
                    }
                    
                    celular = "";
                    salario = 0;
                    cargo = "";
                    empresa = "";
                    foneEmpresa = "";
                    id_tipoestadocivil = 0;
                    nomePai = "";
                    nomeMae = "";
                    nomeConjuge = "";
                    cargoConjuge = "";
                    rgConjuge = "";
                    salarioConjuge = 0;
                    observacao="";

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
                    oClientePreferencial.datanascimento = dataNascimento;
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
                    oClientePreferencial.codigoanterior = id;
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
                //if (Linha > 0) {
                //    throw new VRException("Linha " + Linha + ": "+Texto+" " + ex.getMessage());
                //} else {
                    throw ex;
                //}
            }
            return vClientePreferencial;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteRMS(int id_loja, int id_lojaCliente) throws Exception {
        
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
            
            stm = ConexaoOracle.getConexao().createStatement();

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
    public List<FornecedorVO> carregarFornecedorRMS() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, 
               datacadastro, telefone, telefone2, numero, ddd, dddFax, fax,
               dddTel2, inscricaoMunicipal;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        boolean ativo = true;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
        java.sql.Date data = null;

        try {
            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT F.TIP_CODIGO, F.TIP_DIGITO, F.TIP_RAZAO_SOCIAL, F.TIP_NOME_FANTASIA,      ");
            sql.append("        F.TIP_ENDERECO, F.TIP_BAIRRO, F.TIP_CIDADE, F.TIP_ESTADO,   ");
            sql.append("        F.TIP_CEP, F.TIP_LOJ_CLI,F.TIP_NATUREZA, F.TIP_DATA_CAD,    ");
            sql.append("        F.TIP_FONE_NUM, F.TIP_FIS_JUR, F.TIP_CGC_CPF,               "); 
            sql.append("        F.TIP_INSC_EST_IDENT, F.TIP_INSC_MUN, CONT.FOR_CONTATO,     ");
            sql.append("        F.TIP_FONE_DDD, F.TIP_FAX_DDD, F.TIP_FAX_NUM, F.TIP_TELEX_DDD, ");
            sql.append("        F.TIP_TELEX_NUM, F.TIP_INSC_MUN ");
            sql.append(" FROM AA2CTIPO F                                                    ");     
            sql.append(" LEFT JOIN AA2CFORN  CONT ON CONT.FOR_CODIGO = F.TIP_CODIGO         ");
            sql.append(" WHERE TIP_LOJ_CLI = 'F' ");
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = Integer.parseInt(rst.getString("TIP_CODIGO")+rst.getString("TIP_DIGITO"));

                    Linha++;

                    if ((rst.getString("TIP_RAZAO_SOCIAL") != null)
                            && (!rst.getString("TIP_RAZAO_SOCIAL").isEmpty())) {
                        byte[] bytes = rst.getBytes("TIP_RAZAO_SOCIAL");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("TIP_NOME_FANTASIA") != null)
                            && (!rst.getString("TIP_NOME_FANTASIA").isEmpty())) {
                        byte[] bytes = rst.getBytes("TIP_NOME_FANTASIA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("TIP_CGC_CPF") != null)
                            && (!rst.getString("TIP_CGC_CPF").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("TIP_CGC_CPF").trim()));
                    } else {
                        cnpj = -1;
                    }

                    if ((rst.getString("TIP_INSC_EST_IDENT") != null)
                            && (!rst.getString("TIP_INSC_EST_IDENT").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("TIP_INSC_EST_IDENT").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("TIP_FIS_JUR") != null) &&
                            (!rst.getString("TIP_FIS_JUR").trim().isEmpty())) {
                        if ("J".equals(rst.getString("TIP_FIS_JUR").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    }
                    id_tipoinscricao = 0;

                    if ((rst.getString("TIP_ENDERECO") != null)
                            && (!rst.getString("TIP_ENDERECO").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("TIP_ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("TIP_BAIRRO") != null)
                            && (!rst.getString("TIP_BAIRRO").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("TIP_BAIRRO").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("TIP_CEP") != null)
                            && (!rst.getString("TIP_CEP").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("TIP_CEP").trim()));
                    } else {
                        cep = Global.Cep;
                    }

                    if ((rst.getString("TIP_CIDADE") != null)
                            && (!rst.getString("TIP_CIDADE").isEmpty())) {

                        if ((rst.getString("TIP_ESTADO") != null)
                                && (!rst.getString("TIP_ESTADO").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("TIP_CIDADE").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("TIP_ESTADO").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("TIP_ESTADO") != null)
                            && (!rst.getString("TIP_ESTADO").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("TIP_ESTADO").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if ((rst.getString("TIP_FONE_DDD") != null) &&
                            (!rst.getString("TIP_FONE_DDD").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("TIP_FONE_DDD").trim()))) {
                        ddd = util.formataNumero(rst.getString("TIP_FONE_DDD").trim());
                    } else {
                        ddd = "";
                    }                    
                    
                    if (rst.getString("TIP_FONE_NUM") != null) {
                        telefone = util.formataNumero(rst.getString("TIP_FONE_NUM").trim());                        
                        telefone = ddd + telefone;
                    } else {
                        telefone = "0000000000";
                    }

                    if ((rst.getString("TIP_FAX_DDD") != null) &&
                            (!rst.getString("TIP_FAX_DDD").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("TIP_FAX_DDD").trim()))) {
                        dddFax = util.formataNumero(rst.getString("TIP_FAX_DDD").trim());
                    } else {
                        dddFax = "";
                    }
                    
                    if ((rst.getString("TIP_FAX_NUM") != null) &&
                            (!rst.getString("TIP_FAX_NUM").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("TIP_FAX_NUM").trim()))) {
                        fax = util.formataNumero(rst.getString("TIP_FAX_NUM").trim());
                        fax = dddFax + fax;
                    } else {
                        fax = "";
                    }
                    
                    if ((rst.getString("TIP_TELEX_DDD") != null) &&
                            (!rst.getString("TIP_TELEX_DDD").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("TIP_TELEX_DDD").trim()))) {
                        dddTel2 = util.formataNumero(rst.getString("TIP_TELEX_DDD").trim());
                    } else {
                        dddTel2 = "";
                    }
                    
                    if ((rst.getString("TIP_TELEX_NUM") != null) &&
                            (!rst.getString("TIP_TELEX_NUM").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("TIP_TELEX_NUM").trim()))) {
                        telefone2 = util.formataNumero(rst.getString("TIP_TELEX_NUM").trim());
                        telefone2 = dddTel2 + telefone2;
                    } else {
                        telefone2 = "";
                    }
                    
                    numero = "0";
                    obs = "";

                    if ((rst.getString("TIP_DATA_CAD") != null) &&
                            (!rst.getString("TIP_DATA_CAD").trim().isEmpty())) {
                        
                        if (rst.getString("TIP_DATA_CAD").trim().length() == 5) {
                            datacadastro = rst.getString("TIP_DATA_CAD").trim();
                            datacadastro = rst.getString("TIP_DATA_CAD").substring(0, 1) + "/"
                                    + rst.getString("TIP_DATA_CAD").substring(1, 3) + "/"
                                    + rst.getString("TIP_DATA_CAD").substring(3, 5);
                            datacadastro = datacadastro.trim();
                            data = new java.sql.Date(formato.parse(datacadastro).getTime());
                            
                        } else if (rst.getString("TIP_DATA_CAD").trim().length() == 6) {
                            datacadastro = rst.getString("TIP_DATA_CAD").trim();
                            datacadastro = rst.getString("TIP_DATA_CAD").substring(0, 2) + "/"
                                    + rst.getString("TIP_DATA_CAD").substring(2, 4) + "/"
                                    + rst.getString("TIP_DATA_CAD").substring(4, 6);
                            datacadastro = datacadastro.trim();
                            data = new java.sql.Date(formato.parse(datacadastro).getTime());
                        }
                    } else {
                        datacadastro = "";
                        data = null;
                    }
                    
                    if ((rst.getString("TIP_INSC_MUN") != null) &&
                            (!rst.getString("TIP_INSC_MUN").trim().isEmpty())) {
                        inscricaoMunicipal = util.acertarTexto(rst.getString("TIP_INSC_MUN").replace("'", "").trim());
                    } else {
                        inscricaoMunicipal = "";
                    }

                    if (!inscricaoMunicipal.isEmpty()) {
                        obs = "INSCRICAO MUNICIPAL.: " + inscricaoMunicipal;
                    }
                    
                    /*
                    if (rst.getString("INATIV") != null) {
                        if (!"S".equals(rst.getString("INATIV").trim())) {
                            ativo = true;
                        } else {
                            ativo = false;
                        }
                    } else {*/
                        ativo = true;
                    //}
                    
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
                    oFornecedor.bairro = bairro;
                    oFornecedor.numero = numero;                                        
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.datacadastro = data;
                    oFornecedor.telefone  = telefone;                    
                    oFornecedor.fax = fax;
                    oFornecedor.telefone2 = telefone2;

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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorRMS() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        double idFornecedor;
        double idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());
        int qtembalagem=1;
        try {

            stm = ConexaoOracle.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT FORITE_COD_FORN||FORITE_DIG_FORN AS FORNECEDOR, ");
            sql.append("       GIT_COD_ITEM||GIT_DIGITO PRODUTO, ");
            sql.append("       FORITE_REFERENCIA  AS REFERENCIA, PROD.GIT_EMB_FOR AS EMBALAGEM  ");
            sql.append(" FROM AA1FORIT FORN, AA3CITEM PROD ");
            sql.append(" WHERE PROD.GIT_COD_ITEM = FORN.FORITE_COD_ITEM ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getDouble("FORNECEDOR");
                idProduto = Double.parseDouble(rst.getString("PRODUTO"));
                qtembalagem = rst.getInt("EMBALAGEM");                

                if ((rst.getString("REFERENCIA") != null)
                        && (!rst.getString("REFERENCIA").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("REFERENCIA").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedorDouble = idFornecedor;
                oProdutoFornecedor.id_produtoDouble    = idProduto;               
                oProdutoFornecedor.qtdembalagem        = qtembalagem;
                oProdutoFornecedor.dataalteracao       = dataAlteracao;
                oProdutoFornecedor.codigoexterno       = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPisCofinsRMS() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto = 0, idTipoPisCofinsDebito, idTipoPisCofinsCredito, 
                idTipoNaturezaReceita;
        
        try {
            
            stm = ConexaoOracle.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT DISTINCT T.PISI_ITE, T.PISI_PIS_CST, T.PISI_NAT_REC ");
            sql.append("FROM AA2CPISI T ");
            sql.append("where T.PISI_CFO IN (5102,5405) ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("PISI_ITE").trim());
                
                if ((rst.getString("PISI_PIS_CST") != null) &&
                        (!rst.getString("PISI_PIS_CST").trim().isEmpty())) {
                    
                    if (("1".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("50".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 0;
                        idTipoPisCofinsCredito = 12;
                    } else if (("7".equals(rst.getString("PISI_PIS_CST").trim()))||
                            ("71".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 1;
                        idTipoPisCofinsCredito = 13;
                    } else if (("5".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("75".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 2;
                        idTipoPisCofinsCredito = 14;
                    } else if (("4".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("70".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 3;
                        idTipoPisCofinsCredito = 15;
                    } else if (("2".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("60".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 5;
                        idTipoPisCofinsCredito = 17;
                    } else if (("3".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("51".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 6;
                        idTipoPisCofinsCredito = 18;
                    } else if (("6".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("73".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 7;
                        idTipoPisCofinsCredito = 19;
                    } else if (("8".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("74".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 8;
                        idTipoPisCofinsCredito = 20;
                    } else if (("49".equals(rst.getString("PISI_PIS_CST").trim())) ||
                            ("99".equals(rst.getString("PISI_PIS_CST").trim()))) {
                        
                        idTipoPisCofinsDebito = 9;
                        idTipoPisCofinsCredito = 21;
                    } else {
                        idTipoPisCofinsDebito = 1;
                        idTipoPisCofinsCredito = 13;
                    }
                    
                    if ((rst.getString("PISI_NAT_REC") != null) &&
                            (!rst.getString("PISI_NAT_REC").trim().isEmpty())) {
                        
                        idTipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, rst.getString("PISI_NAT_REC"));
                    } else {
                    
                        idTipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                    }
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
                    oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                    oProduto.tipoNaturezaReceita = idTipoNaturezaReceita;
                    
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    
                    if ((rst.getString("PISI_PIS_CST") != null) &&
                            (!rst.getString("PISI_PIS_CST").trim().isEmpty())) {
                        oAnterior.piscofinsdebito = Integer.parseInt(rst.getString("PISI_PIS_CST").trim());
                        oAnterior.piscofinscredito = Integer.parseInt(rst.getString("PISI_PIS_CST").trim());
                    } else {
                        oAnterior.piscofinsdebito = -1;
                        oAnterior.piscofinscredito = -1;
                    }
                    
                    if ((rst.getString("PISI_NAT_REC") != null) &&
                            (!rst.getString("PISI_NAT_REC").trim().isEmpty())) {
                        oAnterior.naturezareceita = Integer.parseInt(rst.getString("PISI_NAT_REC").trim());
                    } else {
                        oAnterior.naturezareceita = -1;
                    }
                    
                    oProduto.vCodigoAnterior.add(oAnterior);
                    
                    vProduto.add(oProduto);
                }
            }
            
            stm.close();
            return vProduto;                    
            
        } catch(Exception ex) {
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

    public void importarPisCofinsICMS(String arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...PisCofins...ICMS...");
            Map<Double, ProdutoVO> vCustoProduto = carregarPisCofinsICMSRMS(arquivo, idLoja);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Double keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPisCofinsProduto(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    
    
    public void importarNCMArquivo(String arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Pis Cofins, Natureza Receita...");
            Map<Double, ProdutoVO> vNCM = carregarNCMArquivo(arquivo, idLoja);

            ProgressBar.setMaximum(vNCM.size());

            for (Double keyId : vNCM.keySet()) {

                ProdutoVO oProduto = vNCM.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarNcmRMS(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }        
    }        
    
    public void importarNCM() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Pis Cofins, Natureza Receita...");
            Map<Integer, ProdutoVO> vNCM = carregarNCM();

            ProgressBar.setMaximum(vNCM.size());

            for (Integer keyId : vNCM.keySet()) {

                ProdutoVO oProduto = vNCM.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarNcmRMS(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarFamiliaProdutoRMS() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoRMS();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologicoRMS() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            
            MercadologicoDAO mercadologicoDAO = new MercadologicoDAO();
            
            vMercadologico = carregarMercadologicoRMS(1);
            mercadologicoDAO.salvar(vMercadologico, true);
            
            vMercadologico = carregarMercadologicoRMS(2);
            mercadologicoDAO.salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoRMS(3);
            mercadologicoDAO.salvar(vMercadologico, false);
            
            vMercadologico = carregarMercadologicoRMS(4);
            mercadologicoDAO.salvar(vMercadologico, false);
            
            mercadologicoDAO.temNivel4 = true;
            mercadologicoDAO.salvarMax();
            
           
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoRMS(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Double, ProdutoVO> vProduto = carregarProdutoRMS();
            
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
            
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }    
    
    public void importarCustoProdutoRMS(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoRMS(id_loja, id_lojaCliente);
            
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
    
    public void importarPrecoProdutoRMS(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoRMS(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Double keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProdutoPCSistemas(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProdutoRMS(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoRMS(id_loja, id_lojaCliente);
            
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
    
    public void importarOfertaProdutoRMS(int id_Loja) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Oferta...");
            List<OfertaVO> vOferta = carregarOfertaProdutoRMS(id_Loja);
            new OfertaDAO().salvar(vOferta, id_Loja);

        } catch (Exception ex) {

            throw ex;
        }
    }        
        
    public void importarCodigoBarraRMS() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vProduto = carregarCodigoBarrasRMS();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProduto.size());
            
            for (Long keyId : vProduto.keySet()) {
                
                ProdutoVO oProduto = vProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.addCodigoBarras(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }        

    public void importarClientePreferencialRMS(int idLoja, int idLojaCliente) throws Exception {

        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");

            vCliente = carregarClientePreferencialRMS(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);            
            new ClientePreferencialDAO().salvar(vCliente, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteRMS(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteRMS(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvarShi(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }    

    public void importarFornecedorRMS(int municipioPadrao, int estadoPadrao, long cepPadrao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorRMS();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutoFornecedorRMS() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorRMS();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarMargemRMS(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Margem...");
            Map<Double, ProdutoVO> vMargemProduto = carregarMargemRMS(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vMargemProduto.size());
            
            for (Double keyId : vMargemProduto.keySet()) {
                
                ProdutoVO oProduto = vMargemProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarMargemProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarPisCofinsRMS() throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...PisCofins...");
            List<ProdutoVO> vProduto = carregarPisCofinsRMS();
            
            
            
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public void importarAcertarNCM() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Acertar NCM...");
            List<ProdutoVO> vProduto = carregarNCMParaAcertar();
            
            new ProdutoDAO().acertarNcmProdutoRMS(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    // FUNÇÕES
    private int retornarAliquotaICMS(int codTrib) {      
        int retorno=-1;
        if (Global.idEstado == 23) {
            if (TributacaoICMSRMS.getTributacaoICMSCeara().get(codTrib) == null){
               retorno=8;
            }else{
               retorno=TributacaoICMSRMS.getTributacaoICMSCeara().get(codTrib);
            }
            if (retorno == -1){
                retorno=8;
            }
        }else{
            if (TributacaoICMSRMS.getTributacaoICMSSaoPaulo().get(codTrib) == null){
               retorno=8;
            }else{
               retorno=TributacaoICMSRMS.getTributacaoICMSSaoPaulo().get(codTrib);
            }
            if (retorno == -1){
                retorno=8;
            }
        }
        return retorno;
    }    

    private int retornarPisCofins(String codTrib, String Tipo) {
        int retorno;
        if ("0".equals(codTrib)) {
            retorno = ("S".equals(Tipo)? 0 : 12);
        } else if ("1".equals(codTrib)) {
            retorno = ("S".equals(Tipo)? 2 : 14);
        } else if ("2".equals(codTrib)) {
            retorno = ("S".equals(Tipo)? 1 : 13);
        } else if (("3".equals(codTrib))||
                   ("7".equals(codTrib))||
                   ("8".equals(codTrib))){
            retorno = ("S".equals(Tipo)? 7 : 19);
        } else if ("05".equals(codTrib)) {
            retorno = ("S".equals(Tipo)? 5 : 17);
        } else if ("13".equals(codTrib)) {
            retorno = ("S".equals(Tipo)? 3 : 15);
        } else {
            retorno = ("S".equals(Tipo)? 9 : 21);
        }
        return retorno;
    }         
}
