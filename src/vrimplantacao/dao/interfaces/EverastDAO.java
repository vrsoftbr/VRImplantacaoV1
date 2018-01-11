package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import vrimplantacao.dao.cadastro.FornecedorDAO;

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
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
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

public class EverastDAO {

    // CARREGAMENTOS
    
    private List<ProdutoVO> carregarAcertarPisCofinsExcel(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, idTipoPisCofinsDebito, idTipoPisCofinsCredito,
            naturezaReceita;
        
        try {
            int linha = 0;
            
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

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellNaturezaReceita = sheet.getCell(1, i);
                        Cell cellPisCofins = sheet.getCell(2, i);
                        
                        idProduto = Integer.parseInt(Utils.formataNumero(cellIdProduto.getContents().trim()));
                        
                        if ((cellPisCofins.getContents() != null) &&
                                (!cellPisCofins.getContents().trim().isEmpty())) {
                            idTipoPisCofinsDebito = retornarPisCofinsDebito(cellPisCofins.getContents().trim());
                        } else {
                            idTipoPisCofinsDebito = 1;
                        }
                        
                        if ((cellPisCofins.getContents() != null) &&
                                (!cellPisCofins.getContents().trim().isEmpty())) {
                            idTipoPisCofinsCredito = retornarPisCofinsCredito(cellPisCofins.getContents().trim());
                        } else {
                            idTipoPisCofinsCredito = 1;
                        }
                        
                        if ((cellNaturezaReceita.getContents() != null) &&
                                (!cellNaturezaReceita.getContents().trim().isEmpty())) {
                            naturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, 
                                    Utils.formataNumero(cellNaturezaReceita.getContents().trim()));
                        } else {
                            naturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");
                        }
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
                        oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                        oProduto.tipoNaturezaReceita = naturezaReceita;
                        vProduto.add(oProduto);
                    }
                }

                return vProduto;
            } catch(Exception ex) {
                throw ex;
            }            
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarAcertarICMSExcel(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, aliquotaICMS;
        
        try {
            int linha = 0;
            
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

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellIcms = sheet.getCell(4, i);
                        
                        idProduto = Integer.parseInt(Utils.formataNumero(cellIdProduto.getContents().trim()));
                        
                        if ((cellIcms.getContents() != null)
                                && (!cellIcms.getContents().trim().isEmpty())) {
                            aliquotaICMS = retornarAliquotaICMS(Integer.parseInt(cellIcms.getContents().trim()));
                        } else {
                            aliquotaICMS = 8;
                        }
                        
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        
                        ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                        oAliquota.idAliquotaDebito = aliquotaICMS;
                        oAliquota.idAliquotaCredito = aliquotaICMS;
                        oAliquota.idAliquotaDebitoForaEstado = aliquotaICMS;
                        oAliquota.idAliquotaCreditoForaEstado = aliquotaICMS;
                        oAliquota.idAliquotaDebitoForaEstadoNF = aliquotaICMS;
                        oProduto.vAliquota.add(oAliquota);
                        vProduto.add(oProduto);
                    }
                }

                return vProduto;
            } catch(Exception ex) {
                throw ex;
            }            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idFamilia;
        String descricao;

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT DISTINCT ID, DESCRICAO FROM PRODUTOFAMILIA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFamilia = Integer.parseInt(rst.getString("ID").trim());
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

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("    SELECT SETOR COD_M1, COALESCE(S.DESCRICAO,G.DESCRICAO) DESC_M1,  ");
            sql.append("           GRUPO COD_M2, COALESCE(G.DESCRICAO,S.DESCRICAO) DESC_M2,  ");
            sql.append("	   FAMILIA COD_M3,  ");
            sql.append("           COALESCE(F.DESCRICAO,COALESCE(G.DESCRICAO,S.DESCRICAO)) AS DESC_M3 ");
            sql.append("FROM PRODUTO P ");
            sql.append("LEFT OUTER JOIN SETOR S ON  ");
            sql.append("	S.IDSETOR = P.SETOR ");
            sql.append("LEFT OUTER JOIN GRUPO G ON  ");
            sql.append("	G.IDGRUPO = P.GRUPO ");
            sql.append("LEFT OUTER JOIN FAMILIA F ON  ");
            sql.append("	F.IDFAMILIA = P.FAMILIA ");
            sql.append("LEFT OUTER JOIN MARCA M ON  ");
            sql.append("	M.IDMARCA = P.MARCA	 ");
            sql.append("ORDER BY SETOR, GRUPO, FAMILIA; ");
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
        int idProduto, idTipoEmbalagem, qtdEmbalagem, idSituacaoCadastro = 1,
                idTipoPisCofinsDebito, idTipoPisCofinsCredito, tipoNaturezaReceita, validade,
                idFamilia = -1, codigoBalanca, mercadologico1, mercadologico2, mercadologico3,
                ncm1, ncm2, ncm3, aliquotaICMS=8;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, dataCadastro = "",
                strCodigoBarras, strNcm = "";
        boolean pesavel, eBalanca;
        double pesoLiq = 0, pesoBruto = 0, margem = 0, custo = 0, precoVenda = 0;
        long codigoBarras;

        try {
            Conexao.begin();
            stmPG = Conexao.createStatement();

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select  idProduto,  codbarra,  referencia,  descricao,  descricaoresumida,  unidade,     ");
            sql.append("        cst,  precocusto,  customedio,  precovenda,  setor,  grupo,  familia,  marca,    ");
            sql.append("        qtmaxima,  qtminima,  qtdeposito,  qtestoque,  margem,  datapreco,  pesavel,     ");
            sql.append("        tipouso,  codfabricante,  situacao,  Log,  func,  cc,  NCM,  Estoque,            ");
            sql.append("        PisCofins,  diferidoPR,  mensagem,  validade,  Garantia,  Passociado,            ");
            sql.append("        comissao,  ccfora,  cstfora,  IPI,  dataestoque,  datamarkup,  maxde,            ");
            sql.append("        Pcompra,  datacomissao,  SI,  simbolo,  Volume,  endereco,  peso,  externo,      ");
            sql.append("        TM,  cfop,  IPPT,  md5,  CSOSN,  HoraPreco,  pg,  cstpc,  observacao,  PC,       ");
            sql.append("        qtEmb,  integra,  tipoI,  natuPC,  codaux,  imagem,  foto,  categImp,  qtEmbsai, ");
            sql.append("        codANP,  qtpeca,  vpeca,  codEmb,  regMapa,  vtributo,  ibpt,  PesoLiq,  serv,   ");
            sql.append("        obsFT,  configOP, PASSOCIADO                                                     ");
            sql.append("from produto                                                                             ");
            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("idproduto").trim());

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("codbarra"));

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
                    pesavel = false;
                    eBalanca = false;
                    codigoBalanca = -1;
                    validade = 0;
                    if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("LT".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 9;
                    } else {
                        idTipoEmbalagem = 0;
                    }

                }

                //qtdEmbalagem = (int) Double.parseDouble(rst.getString("QTEMB"));
                qtdEmbalagem = 1;

                if ((rst.getString("SITUACAO") != null)
                        && (!rst.getString("SITUACAO").trim().isEmpty())) {
                    if ("A".equals(rst.getString("SITUACAO"))){
                        idSituacaoCadastro = 1;                                           
                    }else{
                        idSituacaoCadastro = 0;                                                                   
                    }
                }else{
                    idSituacaoCadastro = 1;                    
                }

                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoCompleta = "PRODUTO SEM DESCRICAO";
                }

                if ((rst.getString("DESCRICAORESUMIDA") != null)
                        && (!rst.getString("DESCRICAORESUMIDA").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAORESUMIDA");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = descricaoCompleta;
                }

                descricaoGondola = descricaoCompleta;

                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {

                    strNcm = util.formataNumero(rst.getString("NCM").trim());

                    NcmVO oNcm = new NcmDAO().validar(strNcm);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("SETOR") != null)
                        && (!rst.getString("SETOR").trim().isEmpty())
                        && (rst.getString("GRUPO") != null)
                        && (!rst.getString("GRUPO").trim().isEmpty())
                        && (rst.getString("FAMILIA") != null)
                        && (!rst.getString("FAMILIA").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("SETOR").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("GRUPO").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("FAMILIA").trim());
                } else {
                    mercadologico1 = 1;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }

                if (eBalanca) {
                    codigoBarras = idProduto;
                } else {
                    if ((rst.getString("CODBARRA") != null)
                            && (!rst.getString("CODBARRA").trim().isEmpty())) {

                        strCodigoBarras = util.formataNumero(rst.getString("CODBARRA").replace(".", "").trim());

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            if ((idProduto >= 10000) && (!"KG".equals(rst.getString("UNIDADE").trim()))) {
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }
                
                idFamilia = -1;                                    
                if ((rst.getString("passociado") != null)
                        && (!rst.getString("passociado").trim().isEmpty())
                        && (!"0".equals(rst.getString("passociado").trim()))) {
                    sql = new StringBuilder();
                    sql.append("select id from familiaproduto ");
                    sql.append("where id = " + rst.getString("passociado"));
                    rstPG = stmPG.executeQuery(sql.toString());
                    if (rstPG.next()){
                        idFamilia = rstPG.getInt("id");                        
                    }
                }            
                
                if ((rst.getString("pc") != null)
                        && (!rst.getString("pc").trim().isEmpty())) {
                    idTipoPisCofinsDebito   = retornarPisCofinsDebito(rst.getString("pc"));
                    idTipoPisCofinsCredito  = retornarPisCofinsCredito(rst.getString("pc"));                    
                    
                    if ((rst.getString("natupc") != null)
                            && (!rst.getString("natupc").trim().isEmpty())) {
                        tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, rst.getString("natupc").trim());                    
                    }else{
                        tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");                                            
                    }
                    
                }else{
                    idTipoPisCofinsDebito   = 1;
                    idTipoPisCofinsCredito  = 13;
                    tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");                                            
                }

                if ((rst.getString("cc") != null)
                        && (!rst.getString("cc").trim().isEmpty())) {
                    aliquotaICMS = retornarAliquotaICMS(rst.getInt("cc"));
                } else {
                    aliquotaICMS = 8;
                }

                
                if ((rst.getString("PRECOCUSTO") != null)
                        && (!rst.getString("PRECOCUSTO").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("PRECOCUSTO").trim());
                } else {
                    custo = 0;
                }

                if ((rst.getString("PRECOVENDA") != null)
                        && (!rst.getString("PRECOVENDA").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECOVENDA").trim());
                } else {
                    precoVenda = 0;
                }

                if ((rst.getString("PESOLIQ") != null)
                        && (!rst.getString("PESOLIQ").trim().isEmpty())) {
                    pesoLiq = Double.parseDouble(rst.getString("PESOLIQ").trim());
                }else{
                    pesoLiq = 0;                
                }
                
                if ((rst.getString("PESO") != null)
                        && (!rst.getString("PESO").trim().isEmpty())) {
                    pesoBruto = Double.parseDouble(rst.getString("PESO").trim());
                }else{
                    pesoBruto = 0;                    
                }

                if ((rst.getString("MARGEM") != null)
                        && (!rst.getString("MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MARGEM").trim());
                } else {
                    margem = 0;
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
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.precoVenda = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = 31;
                oAliquota.idAliquotaDebito = aliquotaICMS;
                oAliquota.idAliquotaCredito = aliquotaICMS;
                oAliquota.idAliquotaDebitoForaEstado = aliquotaICMS;
                oAliquota.idAliquotaCreditoForaEstado = aliquotaICMS;
                oAliquota.idAliquotaDebitoForaEstadoNF = aliquotaICMS;
                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oAnterior.codigoanterior = idProduto;

                if ((rst.getString("CODBARRA") != null)
                        && (!rst.getString("CODBARRA").trim().isEmpty())) {
                    oAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("CODBARRA").trim()));
                } else {
                    oAnterior.barras = -1;
                }

                oAnterior.custocomimposto = custo;
                oAnterior.custosemimposto = custo;

                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {
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
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarCustoProduto(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT IDPRODUTO, PRECOCUSTO FROM PRODUTO ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("idproduto").replace(".", ""));

                if ((rst.getString("precocusto") != null)
                        && (!rst.getString("precocusto").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("precocusto").replace(",", "."));
                } else {
                    custo = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double precoVenda = 0, margem = 0, custo = 0;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT IDPRODUTO, MARGEM, PRECOCUSTO, PRECOVENDA FROM PRODUTO ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("IDPRODUTO").trim());

                if ((rst.getString("PRECOCUSTO") != null)
                        && (!rst.getString("PRECOCUSTO").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("PRECOCUSTO").trim());
                } else {
                    custo = 0;
                }

                if ((rst.getString("PRECOVENDA") != null)
                        && (!rst.getString("PRECOVENDA").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECOVENDA").trim());
                } else {
                    precoVenda = 0;
                }

                if ((rst.getString("MARGEM") != null)
                        && (!rst.getString("MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MARGEM").trim());
                } else {
                    if ((custo > 0) && (precoVenda > 0)) {
                        margem = (custo / precoVenda) * 100;
                    } else {
                        margem = 0;
                    }
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
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarNaturezaReceita() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoPisCofinsDebito,tipoNaturezaReceita;
        double precoVenda = 0, margem = 0, custo = 0;
        Utils util = new Utils();
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT IDPRODUTO, NATUPC, PISCOFINS FROM PRODUTO WHERE NATUPC IS NOT NULL AND NATUPC <> '' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("IDPRODUTO").trim());

                idTipoPisCofinsDebito   = retornarPisCofinsDebito(rst.getString("piscofins"));
                if ((rst.getString("natupc").trim()!=null) && 
                        (!rst.getString("natupc").trim().isEmpty())){
                    tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, rst.getString("natupc").trim());                    
                }else{
                    tipoNaturezaReceita     = 999;                                        
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                vProduto.add(oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    private List<ProdutoVO> carregarPisCofins() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoPisCofinsDebito, idTipoPisCofinsCredito, tipoNaturezaReceita;
        Utils util = new Utils();
        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT IDPRODUTO, coalesce(NATUPC,0) AS NATUPC, PC as PISCOFINS FROM PRODUTO ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("IDPRODUTO").trim());

                idTipoPisCofinsDebito    = retornarPisCofinsDebito(rst.getString("piscofins"));
                idTipoPisCofinsCredito   = retornarPisCofinsCredito(rst.getString("piscofins"));                

                if ((rst.getString("natupc").trim()!=null) && 
                        (!rst.getString("natupc").trim().isEmpty())){
                    tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, rst.getString("natupc").trim());                    
                }else{
                    tipoNaturezaReceita     = 999;                                        
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.idTipoPisCofinsDebito  = idTipoPisCofinsDebito;                      
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita    = tipoNaturezaReceita;
                vProduto.add(oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
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
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT IDPRODUTO, NATUPC, QTESTOQUE FROM PRODUTO ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("idproduto").trim());

                if ((rst.getString("QTESTOQUE") != null)
                        && (!rst.getString("QTESTOQUE").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("QTESTOQUE").trim());
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
        } catch (Exception ex) {
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
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT COD, NOME, RAZAO, RUA, NUM, COMP, BAIRRO, CIDADE, UF, coalesce(CEP,'0') as CEP, ");
            sql.append("       FONE, CEL, FAX, EMAIL, WEB, DTCAD, coalesce(CPFCNPJ,cod) as CPFCNPJ, RGIE, RG, ORGAO, ");
            sql.append("       ATIVO, RENDA, MAE, PAI, ESTADO AS ESTCIVIL, ATIV AS CARGO, LIMITE, OBS ");
            sql.append("FROM CLIFOR     ");
            sql.append("WHERE TIPO = 0  ");           
            sql.append("ORDER BY COD  ");                       

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("COD");
                    dataResidencia = "1990/01/01";
                    
                    if ((rst.getString("ATIVO") != null)
                            && (!rst.getString("ATIVO").trim().isEmpty())) {
                        if (rst.getInt("ATIVO")==1){
                            id_situacaocadastro = 1;
                        }else{
                            id_situacaocadastro = 0;                            
                        }
                    } else {
                        id_situacaocadastro = 0;
                    }

                    if ((util.formataNumero(rst.getString("CPFCNPJ")) != null)
                            && (!util.formataNumero(rst.getString("CPFCNPJ")).isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CPFCNPJ").trim()));
                    } else {
                        cnpj = Long.parseLong("-1");
                    }

                    if ((rst.getString("CPFCNPJ") != null)
                            && (!rst.getString("CPFCNPJ").trim().isEmpty())) {
                        if (rst.getString("CPFCNPJ").trim().length()==11) {                        
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        } 
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = util.acertarTexto(textoAcertado.replace("'", "").trim().replace(".", "").trim());
                        if ("".equals(nome)){
                            nome = "SEM NOME VR " + id;                            
                        }
                    } else {
                        nome = "SEM NOME VR " + id;
                    }

                    if ((rst.getString("RUA") != null)
                            && (!rst.getString("RUA").trim().isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("RUA").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").trim().isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRRO").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("FONE") != null)
                            && (!rst.getString("FONE").trim().isEmpty())) {
                        telefone1 = util.formataNumero(rst.getString("FONE").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((util.formataNumero(rst.getString("CEP")) != null)
                            && (!util.formataNumero(rst.getString("CEP")).trim().isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("CEP").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").trim().isEmpty())) {
                        if ((rst.getString("UF") != null)
                                && (!rst.getString("UF").trim().isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("CIDADE").trim().replace("'", "").toUpperCase()),
                                    rst.getString("UF").trim().replace("'", ""));
                            if (id_municipio == 0) {
                                id_municipio = 3147907;
                            }
                        } else {
                            id_municipio = 3147907;
                        }
                    } else {
                        id_municipio = 3147907;
                    }

                    if ((rst.getString("UF") != null)
                            && (!rst.getString("UF").trim().isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(
                                rst.getString("UF").trim().replace("'", "").toUpperCase());

                        if (id_estado == 0) {
                            id_estado = 31;
                        }
                    } else {
                        id_estado = 31;
                    }

                    numero = "0";

                    if ((rst.getString("COMP") != null)
                            && (!rst.getString("COMP").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("COMP"));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("LIMITE") != null)
                            && (!rst.getString("LIMITE").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMITE"));
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").trim().isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("RGIE").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "").replace("-", "").replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    dataCadastro = "";

                    bloqueado = false;

                    if ((rst.getString("PAI") != null)
                            && (!rst.getString("PAI").trim().isEmpty())) {
                        nomePai = util.acertarTexto(rst.getString("PAI").trim().replace("'", ""));
                    } else {
                        nomePai = "";
                    }

                    if ((rst.getString("MAE") != null)
                            && (!rst.getString("MAE").trim().isEmpty())) {
                        nomeMae = util.acertarTexto(rst.getString("MAE").trim().replace("'", ""));
                    } else {
                        nomeMae = "";
                    }

                    if ((rst.getString("CEL") != null)
                            && (!rst.getString("CEL").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("CEL").trim());
                    } else {
                        telefone2 = "";
                    }

                    observacao = "";
                    if ((rst.getString("obs") != null)
                            && (!rst.getString("obs").trim().isEmpty())) {
                        observacao = util.acertarTexto(rst.getString("obs").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())
                            && (rst.getString("EMAIL").contains("@"))) {
                        email = util.acertarTexto(rst.getString("EMAIL").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    if ((rst.getString("ESTCIVIL") != null)
                            && (!rst.getString("ESTCIVIL").trim().isEmpty())) {
                        if ("SOL".equals(rst.getString("ESTCIVIL").trim().substring(0,3))){
                            estadoCivil = 1;
                        }else if ("CAS".equals(rst.getString("ESTCIVIL").trim().substring(0,3))){
                            estadoCivil = 2;                            
                        }else if ("VIU".equals(rst.getString("ESTCIVIL").trim().substring(0,3))){
                            estadoCivil = 3;                            
                        }else if ("DIV".equals(rst.getString("ESTCIVIL").trim().substring(0,3))){
                            estadoCivil = 6;                            
                        }else if ("AMI".equals(rst.getString("ESTCIVIL").trim().substring(0,3))){
                            estadoCivil = 4;                            
                        }
                    } else {
                        estadoCivil = 0;
                    }

                    id_sexo = 1;

                    if ((rst.getString("CARGO") != null)
                            && (!rst.getString("CARGO").trim().isEmpty())) {
                        cargo = util.acertarTexto(rst.getString("CARGO").replace("'", "").trim());
                    } else {
                        cargo = "";
                    }

                    if ((rst.getString("RENDA") != null)
                            && (!rst.getString("RENDA").trim().isEmpty())) {
                        salario = Double.parseDouble(rst.getString("RENDA"));
                    } else {
                        salario = 0;
                    }

                    if ((rst.getString("ORGAO") != null)
                            && (!rst.getString("ORGAO").trim().isEmpty())) {
                        orgaoExp = util.acertarTexto(rst.getString("ORGAO").replace("'", "").trim());
                        if (orgaoExp.length()>6){
                            orgaoExp = orgaoExp.substring(0, 6);
                        }
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

                    //if (bairroEmpresa.length() > 30) {
                        //bairroEmpresa = bairroEmpresa.substring(0, 30);
                        bairroEmpresa = "";
                    //}

                    //if (enderecoEmpresa.length() > 30) {
                        //enderecoEmpresa = enderecoEmpresa.substring(0, 30);
                        enderecoEmpresa = "";
                    //}

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
            } catch (Exception ex) {
                throw ex;
            }
            return vClientePreferencial;
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
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        Long cnpj, cep;
        boolean ativo = true;

        //try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT COD, NOME, RAZAO, RUA, NUM, COMP, BAIRRO, CIDADE, UF as ESTADO, COALESCE(CEP,'0') AS CEP, ");
            sql.append("       FONE, CEL, FAX, EMAIL, WEB, DTNASC, DTCAD, COALESCE(CPFCNPJ,COD) AS CPFCNPJ, RGIE, RG, ORGAO, ");
            sql.append("       ATIVO ");
            sql.append("FROM CLIFOR WHERE TIPO = 0 ");             

            rst = stm.executeQuery(sql.toString());

            Linha = 0;

            //try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("COD");

                    Linha = id;                        
                    if (id==817){
                        Linha = id;                        
                    }

                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("RAZAO") != null)
                            && (!rst.getString("RAZAO").isEmpty())) {
                        byte[] bytes = rst.getBytes("RAZAO");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((util.formataNumero(rst.getString("CPFCNPJ")) != null)
                            && (!util.formataNumero(rst.getString("CPFCNPJ")).isEmpty())) {
                        if (util.formataNumero(rst.getString("CPFCNPJ").toString()).length()>11){
                            id_tipoinscricao = 0;                        
                        }else{
                            id_tipoinscricao = 1;                                                
                        }
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CPFCNPJ").trim()));
                    } else {
                        cnpj = Long.parseLong(String.valueOf(id));
                        id_tipoinscricao = 0;                                                                        
                    }

                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("RGIE").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }


                    if ((rst.getString("RUA") != null)
                            && (!rst.getString("RUA").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("RUA").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if (rst.getString("CEP")!=null){
                        String CEPAUX = util.formataNumero(rst.getString("CEP").trim().replace("/", "").replace("-", "").replace(".", ""));
                        if ((CEPAUX != null)
                                && (!CEPAUX.isEmpty())) {
                            cep = Long.parseLong(CEPAUX);

                        } else {
                            cep = Long.parseLong("0");
                        }
                    }else{
                        cep = Long.parseLong("0");                        
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").isEmpty())) {
                        if ((rst.getString("ESTADO") != null)
                                && (!rst.getString("ESTADO").isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("CIDADE").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));
                            if (id_municipio == 0) {
                                id_municipio = 3147907;
                            }
                        }
                    } else {
                        id_municipio = 3147907;
                    }

                    if ((rst.getString("ESTADO") != null)
                            && (!rst.getString("ESTADO").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = 31;
                        }
                    } else {
                        id_estado = 31;
                    }

                    if (rst.getString("DTCAD") != null) {
                        datacadastro = rst.getString("DTCAD").trim();
                    } else {
                        datacadastro = "";
                    }
                    
                    
                    if (rst.getInt("ATIVO") == 0) {
                        ativo = false;
                    } else {
                        ativo = true;
                    }                    
                    
                    if ((rst.getString("CPFCNPJ") != null)
                            && (!rst.getString("CPFCNPJ").trim().isEmpty())) {
                        if (rst.getString("CPFCNPJ").length() == 11){
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    } else {
                        id_tipoinscricao = 0;
                    }

                    if ((rst.getString("NUM") != null)
                            && (!rst.getString("NUM").trim().isEmpty())) {
                        numero = util.acertarTexto(rst.getString("NUM").trim().replace("'", ""));
                        if (numero.length()>6){
                            numero = numero.substring(0,6);
                        }
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("COMP") != null)
                            && (!rst.getString("COMP").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("COMP").replace("'", "").trim());
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("FONE") != null)
                            && (!rst.getString("FONE").trim().isEmpty())) {
                        telefone = util.formataNumero(rst.getString("FONE").trim());
                    } else {
                        telefone = "0";
                    }

                    if ((rst.getString("CEL") != null)
                            && (!rst.getString("CEL").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("CEL").trim());
                    } else {
                        telefone2 = "0";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())
                            && (rst.getString("EMAIL").contains("@"))) {
                        email = util.acertarTexto(rst.getString("EMAIL").replace("'", ""));
                    } else {
                        email = "";
                    }

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("FAX").trim());
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
                    oFornecedor.complemento = complemento;
                    oFornecedor.telefone = telefone;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.email = email;
                    oFornecedor.fax = fax;

                    vFornecedor.add(oFornecedor);
                }
            /*} catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }*/
            return vFornecedor;
        /*} catch (SQLException | NumberFormatException ex) {

            throw ex;
        }*/
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

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT IDPF, PRODUTO, FORNEC FROM PROFOR  PF ");
            sql.append("INNER JOIN CLIFOR C ON ");
            sql.append("	C.COD = PF.FORNEC AND ");
            sql.append("	C.tipo = 0 ");
            sql.append("ORDER BY FORNEC ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("FORNEC");
                idProduto = rst.getInt("PRODUTO");
                qtdEmbalagem = 1;
                codigoExterno = rst.getString("IDPF");

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

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT BANCO, AGENCIA, CONTA, NUMERO, ");
            sql.append("       VALOR, CPFCNPJ, DATAEMI, DATAVENC ");
            sql.append("FROM CHEQUE ");
            sql.append("WHERE DATASAI IS NULL ");
            sql.append("ORDER BY DATAVENC ");             

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(util.formataNumero(rst.getString("cpfcnpj").trim()));

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    idBanco = util.retornarBanco(Integer.parseInt(util.formataNumero(util.acertarTexto(rst.getString("BANCO").trim()))));
                }else{
                    idBanco = 999;
                }

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("AGENCIA").trim().replace("'", ""));
                } else {
                    agencia = "";
                }

                conta = "";

                if ((rst.getString("NUMERO") != null)
                        && (!rst.getString("NUMERO").trim().isEmpty())) {

                    cheque = Integer.parseInt(util.formataNumero(rst.getString("NUMERO")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("DATAEMI") != null)
                        && (!rst.getString("DATAEMI").trim().isEmpty())) {
                    dataemissao = rst.getString("DATAEMI").trim();
                } else {
                    dataemissao = "2016/01/01";
                }

                if ((rst.getString("DATAVENC") != null)
                        && (!rst.getString("DATAVENC").trim().isEmpty())) {

                    datavencimento = rst.getString("DATAVENC").trim();
                } else {
                    datavencimento = "2016/12/01";
                }

                nome = "";
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

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT DTLANCAMENTO, DTVENCIMENTO, VALOR, CLIFOR, NDOC FROM FINANCA ");
            sql.append("WHERE PG = 'N' ");
            sql.append("ORDER BY CLIFOR, DTLANCAMENTO, DTVENCIMENTO ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("CLIFOR");
                dataemissao = rst.getString("DTLANCAMENTO").substring(0, 10).trim();
                datavencimento = rst.getString("DTVENCIMENTO").substring(0, 10).trim();
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("NDOC")));
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id ");
            sql.append("  from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("ID"));
                codigobarras = util.gerarEan13((int)idProduto, true);                
                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
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
            new MercadologicoDAO().salvar(vMercadologico, true);

            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

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

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProduto(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Double keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Preço Produtos...");

            vProdutoNovo = carregarPrecoProduto();

            new ProdutoDAO().alterarPrecoProduto(vProdutoNovo, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarNaturezaReceita(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Natureza Receita...");

            vProdutoNovo = carregarNaturezaReceita();

            new ProdutoDAO().alterarNaturezaReceita(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    public void importarPisCofins(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Natureza Receita...");

            vProdutoNovo = carregarPisCofins();

            new ProdutoDAO().alterarPisCofinsProduto(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }        

    public void importarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque...Produtos...");

            vProduto = carregarEstoqueProduto(idLoja);

            new ProdutoDAO().alterarEstoqueProduto(vProduto, idLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(int idLoja, int idLojaCliente, boolean deletar) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente,deletar);

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

            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);

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

    public void importarCodigoBarraEmBranco() throws Exception {
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

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarAcertarPisCofinsExcel(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Piscofins...");
            vProduto = carregarAcertarPisCofinsExcel(i_arquivo);
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().acertarPisCofinsEverest(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarAcertarIcmsExcel(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar ICMS...");
            vProduto = carregarAcertarICMSExcel(i_arquivo);
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().acertarICMSEverest(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    // FUNÇÕES
    public int retornarPisCofinsDebito(String csttipopiscofins) {
        int retorno;

        if ("1".equals(csttipopiscofins)) {
            retorno = 0;
        } else if (("2".equals(csttipopiscofins)) ||
                   ("3".equals(csttipopiscofins))){
            retorno = 1;
        } else if ("4".equals(csttipopiscofins)) {
            retorno = 3;
        } else if ("5".equals(csttipopiscofins)) {
            retorno = 7;
        } else if (("6".equals(csttipopiscofins)) ||
                   ("7".equals(csttipopiscofins)) ||                
                   ("8".equals(csttipopiscofins)) ||                                
                   ("10".equals(csttipopiscofins)) ){
            retorno = 8;
        } else if ("9".equals(csttipopiscofins)) {
            retorno = 2;
        } else {
            retorno = 1;
        }

        return retorno;
    }
    
    public int retornarPisCofinsCredito(String csttipopiscofins) {
        int retorno;

        if ("1".equals(csttipopiscofins)) {
            retorno = 12;
        } else if (("2".equals(csttipopiscofins)) ||
                   ("3".equals(csttipopiscofins))){
            retorno = 13;
        } else if ("4".equals(csttipopiscofins)) {
            retorno = 15;
        } else if ("5".equals(csttipopiscofins)) {
            retorno = 19;
        } else if (("6".equals(csttipopiscofins)) ||
                   ("7".equals(csttipopiscofins)) ||                
                   ("8".equals(csttipopiscofins)) ||                                
                   ("10".equals(csttipopiscofins)) ){
            retorno = 21;
        } else if ("9".equals(csttipopiscofins)) {
            retorno = 14;
        } else {
            retorno = 13;
        }

        return retorno;
    }    
    
    private int retornarAliquotaICMS(Integer codTrib) {
        int retorno = 8;
        switch (codTrib) {
            case 7:
                retorno = 7;
                break;
            case 11:
                retorno = 7;
                break;
            case 10:
                retorno = 6;
                break;
            case 13:
                retorno = 6;
                break;
            case 1:
                retorno = 2;
                break;
            case 2:
                retorno = 2;
                break;
            case 6:
                retorno = 2;
                break;
            case 8:
                retorno = 2;
                break;                    
            case 3:
                retorno = 9;
                break;
            case 4:
                retorno = 0;
                break;
            case 5:
                retorno = 1;
                break;
            case 9:
                retorno = 8;
                break;
            case 12:
                retorno = 1;
                break;
            default:
                retorno = 8;
                break;
        }
        return retorno;
    }
    
}
