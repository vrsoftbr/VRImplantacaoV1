package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
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
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;

public class GetWayCloudDAO {

    public String Texto;
    //CARREGAMENTOS
    Utils util = new Utils();

    public List<ProdutoVO> carregarPisCofinsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto;
        int idTipoPisCofins, idTipoPisCofinsAux, 
                idTipoPisCofinsCredito, idTipoPisCofinsCreditoAux,
                tipoNaturezaReceita, tipoNaturezaReceitaAux;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select codprod, cst_pisentrada, cst_pissaida, ");
            sql.append("cst_cofinsentrada, cst_cofinssaida, nat_rec ");
            sql.append("from PRODUTOS ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();
                idProduto = rst.getDouble("codprod");
                if ((rst.getString("cst_pissaida") != null)
                        && (!rst.getString("cst_pissaida").trim().isEmpty())) {
                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("cst_pissaida").trim()));
                    idTipoPisCofinsAux = Integer.parseInt(rst.getString("cst_pissaida").trim());
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsAux = -1;
                }

                if ((rst.getString("cst_pisentrada") != null)
                        && (!rst.getString("cst_pisentrada").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("cst_pisentrada").trim()));
                    idTipoPisCofinsCreditoAux = Integer.parseInt(rst.getString("cst_pisentrada").trim());
                } else {
                    idTipoPisCofinsCredito = 13;
                    idTipoPisCofinsCreditoAux = -1;
                }

                if ((rst.getString("NAT_REC") != null)
                        && (!rst.getString("NAT_REC").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NAT_REC").trim());

                    tipoNaturezaReceitaAux = Integer.parseInt(rst.getString("NAT_REC").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaAux = -1;
                }
                
                oProduto.setIdDouble(idProduto);
                oProduto.setIdTipoPisCofinsDebito(idTipoPisCofins);
                oProduto.setIdTipoPisCofinsCredito(idTipoPisCofinsCredito);
                oProduto.setTipoNaturezaReceita(tipoNaturezaReceita);
                vProduto.add(oProduto);
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarPisCofinsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acerto PisCofins...");
            vProduto = carregarPisCofinsProduto();
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarCodigoBarrasAtacadoLoja(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras = -2;
        int idTipoEmbalagem = 0;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CODPROD, BARRA, UNIDADE, QUANTIDADE, PRECO_VENDA ");
            sql.append("from view_PDV_ProdutosVenda ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.setIdDouble(rst.getDouble("CODPROD"));
                
                codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                
                if ((rst.getString("UNIDADE") != null) &&
                        (!rst.getString("UNIDADE").trim().isEmpty())) {
                    if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    }
                } else {
                    idTipoEmbalagem = 0;
                }

                if (String.valueOf(codigoBarras).length() >= 7) {
                    ProdutoAutomacaoLojaVO oAutomacaoLoja = new ProdutoAutomacaoLojaVO();
                    oAutomacaoLoja.codigobarras = codigoBarras;
                    oAutomacaoLoja.precovenda = rst.getDouble("PRECO_VENDA");
                    oAutomacaoLoja.qtdEmbalagem = (int) rst.getDouble("QUANTIDADE");
                    oAutomacaoLoja.idTipoEmbalagem = idTipoEmbalagem;
                    oAutomacaoLoja.id_loja = idLoja;
                    oProduto.vAutomacaoLoja.add(oAutomacaoLoja);
                    vProduto.add(oProduto);
                }
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarrasAtacadoLoja(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Código barras atacado Loja "+idLoja+"...");
            vProduto = carregarCodigoBarrasAtacadoLoja(idLoja);
            
            if (!vProduto.isEmpty()) {
                produto.addCodigoBarrasAtacado(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutGetWay() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" select CODFAMILIA, descricao from FAMILIA; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("CODFAMILIA"));
                oFamiliaProduto.descricao = util.acertarTexto(rst.getString("DESCRICAO").replace("'", ""));
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologicoGetWay(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {
            mercadologico1 = 0;
            mercadologico2 = 0;
            mercadologico3 = 0;
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select CRECEITA.CODCRECEITA AS MERC1, coalesce(CRECEITA.DESCRICAO,'DIVERSOS') AS  DESCRICAO_M1, ");
            sql.append("	   coalesce(GRUPO.CODGRUPO, 1) AS MERC2, coalesce(GRUPO.DESCRICAO, CRECEITA.DESCRICAO) AS  DESCRICAO_M2, ");
            sql.append("	   coalesce(CATEGORIA.CODCATEGORIA, 1) AS MERC3,  coalesce(CATEGORIA.DESCRICAO,GRUPO.DESCRICAO) as DESCRICAO_M3 ");
            sql.append("from CRECEITA ");
            sql.append("left join GRUPO on GRUPO.CODCRECEITA = CRECEITA.CODCRECEITA ");
            sql.append("left join CATEGORIA on CATEGORIA.CODGRUPO = GRUPO.CODGRUPO ");
            sql.append("order by CRECEITA.CODCRECEITA, GRUPO.CODGRUPO, CATEGORIA.CODCATEGORIA ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                if (nivel == 1) {

                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    descricao = Utils.acertarTexto(rst.getString("DESCRICAO_M1").replace("'", "").trim());
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
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 2) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));

                    descricao = Utils.acertarTexto(rst.getString("DESCRICAO_M2").replace("'", "").trim());

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
                    vMercadologico.add(oMercadologico);
                } else if (nivel == 3) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));
                    mercadologico3 = Integer.parseInt(rst.getString("MERC3"));
                    
                    if ((rst.getString("DESCRICAO_M3") != null) &&
                            (!rst.getString("DESCRICAO_M3").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("DESCRICAO_M3").replace("'", "").trim());
                    } else {
                        if ((rst.getString("DESCRICAO_M2") != null) &&
                                (!rst.getString("DESCRICAO_M2").trim().isEmpty())) {
                            descricao = Utils.acertarTexto(rst.getString("DESCRICAO_M2").replace("'", "").trim());
                        } else {
                            descricao = Utils.acertarTexto(rst.getString("DESCRICAO_M1").replace("'", "").trim());
                        }
                    }
                    
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
                    vMercadologico.add(oMercadologico); 
                }
            }
            return vMercadologico;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoGetWay() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idFamilia, idSituacaoCadastro, cstEntrada, cstSaida,
            ncm1, ncm2, ncm3, codigoBalanca, validade = 0, idTipoPisCofinsAux = 0, idTipoPisCofinsCreditoAux = 0,
            tipoNaturezaReceitaAux = 0, idCest;
        double valIcmsCredito, margem;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual = "", strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;

        try {
            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select P.CODPROD, VW.BARRA, P.DESCRICAO, VW.DESCRICAO_PDV, P.UNIDADE, P.PESO_BRUTO, P.ATIVO, P.DATAINCLU, ");
            sql.append("       P2.CODTRIB CST_SAIDA, P2.CODTRIB_ENTRADA CST_ENTRADA, ");
            sql.append("       P2.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
            sql.append("       P2.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
            sql.append("       A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, P2.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
            sql.append("       P2.PER_REDUC_ENT REDUCAO_CREDITO, P2.ULTICMSCRED, ");
            sql.append("       PF.CST_PISCOFINSSAIDA, PF.CST_PISCOFINSENTRADA, PF.NAT_REC, PF.CODNCM, PF.CODCEST ");
            sql.append("  from PRODUTOS P ");
            sql.append("  left join PRODUTOS_IMPOSTOS P2 on P2.CODPROD = P.CODPROD ");
            sql.append("  left join ALIQUOTA_ICMS A on A.CODALIQ = P2.CODALIQ ");
            sql.append("  left join ALIQUOTA_ICMS A2 on A2.CODALIQ = P2.CODALIQ_NF ");
            sql.append("  left join PRODUTOS_IMPOSTOS_FEDERAIS PF on PF.CODPROD = P.CODPROD ");
            sql.append(" inner join view_PDV_ProdutosVenda VW on VW.CODPROD = P.CODPROD ");
            sql.append(" where BARRAPRINCIPALPRODUTO = 1 ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                valIcmsCredito = 0;
                margem = 0;
                
                cstSaida = rst.getInt("CST_SAIDA");
                cstEntrada = rst.getInt("CST_ENTRADA");
                
                if (cstSaida > 9) {
                    cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                }

                if (cstEntrada > 9) {
                    cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                }
                
                if ((rst.getInt("CST_ENTRADA") == 0) &&
                        (rst.getDouble("ULTICMSCRED") == 0.0)) {
                    valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                } else {
                    valIcmsCredito = rst.getDouble("ULTICMSCRED");
                }
                
                if ("1".equals(rst.getString("ATIVO").trim())) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + Utils.formataNumero(rst.getString("BARRA").trim()));

                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {

                    eBalanca = true;
                    idProduto = Integer.parseInt(rst.getString("CODPROD").trim().replace(".", ""));
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade");

                    if ("KG".equals(rst.getString("UNIDADE").trim())) {
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
                    idProduto = Integer.parseInt(rst.getString("CODPROD").trim().replace(".", ""));
                }

                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descriaoCompleta = "";
                }

                if ((rst.getString("DESCRICAO_PDV") != null)
                        && (!rst.getString("DESCRICAO_PDV").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_PDV");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = "";
                }

                descricaoGondola = descricaoReduzida;

                qtdEmbalagem = 1;
                idFamilia = -1;

                if ((rst.getString("CODNCM") != null)
                        && (!rst.getString("CODNCM").isEmpty())
                        && (rst.getString("CODNCM").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("CODNCM").trim());

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("CODCEST") != null) &&
                        (!rst.getString("CODCEST").trim().isEmpty())) {
                    idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                } else {
                    idCest = -1;
                }
                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {

                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("BARRA").trim());

                        if (strCodigoBarras.length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    }
                }

                if ((rst.getString("CST_PISCOFINSSAIDA") != null)
                        && (!rst.getString("CST_PISCOFINSSAIDA").trim().isEmpty())) {
                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("CST_PISCOFINSSAIDA").trim()));
                    idTipoPisCofinsAux = Integer.parseInt(rst.getString("CST_PISCOFINSSAIDA").trim());
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsAux = -1;
                }

                if ((rst.getString("CST_PISCOFINSENTRADA") != null)
                        && (!rst.getString("CST_PISCOFINSENTRADA").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("CST_PISCOFINSENTRADA").trim()));
                    idTipoPisCofinsCreditoAux = Integer.parseInt(rst.getString("CST_PISCOFINSENTRADA").trim());
                } else {
                    idTipoPisCofinsCredito = 13;
                    idTipoPisCofinsCreditoAux = -1;
                }

                if ((rst.getString("NAT_REC") != null)
                        && (!rst.getString("NAT_REC").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NAT_REC").trim());

                    tipoNaturezaReceitaAux = Integer.parseInt(rst.getString("NAT_REC").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaAux = -1;
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
                oProduto.descricaoCompleta = descriaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
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
                oProduto.idCest = idCest;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;                
                oComplemento.emiteEtiqueta = true;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Global.idEstado;                
                oAliquota.setIdAliquotaDebito(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCredito(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
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
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaAux;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofinsAux;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoAux;                
                oCodigoAnterior.ref_icmsdebito = rst.getString("ALIQ_DEBITO");
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                oCodigoAnterior.ncm = ncmAtual;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoIntegracaoGetWay() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idFamilia, idSituacaoCadastro, cstEntrada, cstSaida,
            ncm1, ncm2, ncm3, codigoBalanca, validade = 0, idTipoPisCofinsAux = 0, idTipoPisCofinsCreditoAux = 0,
            tipoNaturezaReceitaAux = 0, idCest;
        double valIcmsCredito, margem;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual = "", strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;

        try {
            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select P.CODPROD, VW.BARRA, P.DESCRICAO, VW.DESCRICAO_PDV, P.UNIDADE, P.PESO_BRUTO, P.ATIVO, P.DATAINCLU, ");
            sql.append("       P2.CODTRIB CST_SAIDA, P2.CODTRIB_ENTRADA CST_ENTRADA, ");
            sql.append("	   P2.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
            sql.append("       P2.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
            sql.append("       A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, P2.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
            sql.append("       P2.PER_REDUC_ENT REDUCAO_CREDITO, P2.ULTICMSCRED, ");
            sql.append("	   PF.CST_PISCOFINSSAIDA, PF.CST_PISCOFINSENTRADA, PF.NAT_REC, PF.CODNCM, PF.CODCEST ");
            sql.append("  from PRODUTOS P ");
            sql.append("  left join PRODUTOS_IMPOSTOS P2 on P2.CODPROD = P.CODPROD ");
            sql.append("  left join ALIQUOTA_ICMS A on A.CODALIQ = P2.CODALIQ ");
            sql.append("  left join ALIQUOTA_ICMS A2 on A2.CODALIQ = P2.CODALIQ_NF ");
            sql.append("  left join PRODUTOS_IMPOSTOS_FEDERAIS PF on PF.CODPROD = P.CODPROD ");
            sql.append(" inner join view_PDV_ProdutosVenda VW on VW.CODPROD = P.CODPROD ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                valIcmsCredito = 0;
                margem = 0;
                
                cstSaida = rst.getInt("CST_SAIDA");
                cstEntrada = rst.getInt("CST_ENTRADA");
                
                if (cstSaida > 9) {
                    cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                }

                if (cstEntrada > 9) {
                    cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                }
                
                if ((rst.getInt("CST_ENTRADA") == 0) &&
                        (rst.getDouble("ULTICMSCRED") == 0.0)) {
                    valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                } else {
                    valIcmsCredito = rst.getDouble("ULTICMSCRED");
                }
                
                if ("1".equals(rst.getString("ATIVO").trim())) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                pesavel = false;
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
                idProduto = Integer.parseInt(rst.getString("CODPROD").trim().replace(".", ""));

                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descriaoCompleta = "";
                }

                if ((rst.getString("DESCRICAO_PDV") != null)
                        && (!rst.getString("DESCRICAO_PDV").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_PDV");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = "";
                }

                descricaoGondola = descricaoReduzida;

                qtdEmbalagem = 1;
                idFamilia = -1;

                if ((rst.getString("CODNCM") != null)
                        && (!rst.getString("CODNCM").isEmpty())
                        && (rst.getString("CODNCM").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("CODNCM").trim());

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("CODCEST") != null) &&
                        (!rst.getString("CODCEST").trim().isEmpty())) {
                    idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(rst.getString("CODCEST").trim())));
                } else {
                    idCest = -1;
                }
                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {

                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("BARRA").trim());

                        if (strCodigoBarras.length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    }
                }

                if ((rst.getString("CST_PISCOFINSSAIDA") != null)
                        && (!rst.getString("CST_PISCOFINSSAIDA").trim().isEmpty())) {
                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("CST_PISCOFINSSAIDA").trim()));
                    idTipoPisCofinsAux = Integer.parseInt(rst.getString("CST_PISCOFINSSAIDA").trim());
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsAux = -1;
                }

                if ((rst.getString("CST_PISCOFINSENTRADA") != null)
                        && (!rst.getString("CST_PISCOFINSENTRADA").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("CST_PISCOFINSENTRADA").trim()));
                    idTipoPisCofinsCreditoAux = Integer.parseInt(rst.getString("CST_PISCOFINSENTRADA").trim());
                } else {
                    idTipoPisCofinsCredito = 13;
                    idTipoPisCofinsCreditoAux = -1;
                }

                if ((rst.getString("NAT_REC") != null)
                        && (!rst.getString("NAT_REC").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NAT_REC").trim());

                    tipoNaturezaReceitaAux = Integer.parseInt(rst.getString("NAT_REC").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaAux = -1;
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
                oProduto.descricaoCompleta = descriaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
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
                oProduto.idCest = idCest;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;                
                oComplemento.emiteEtiqueta = true;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Global.idEstado;                
                oAliquota.setIdAliquotaDebito(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCredito(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
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
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaAux;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofinsAux;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoAux;                
                oCodigoAnterior.ref_icmsdebito = rst.getString("ALIQ_DEBITO");
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                oCodigoAnterior.ncm = ncmAtual;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stm.close();
            stmPostgres.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarCestProdutoGetWay() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int ncm1 = 0, ncm2 = 0, ncm3 = 0,
                cest1 = 0, cest2 = 0, cest3 = 0;
        String ncm = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ");
            sql.append("       CODPROD, ");
            sql.append("       CODNCM, ");
            sql.append("       CODCEST ");
            sql.append("  from PRODUTOS_IMPOSTOS_FEDERAIS ");
            sql.append(" where CODCEST is not null ");
            sql.append("   and CODCEST <> 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CODPROD"));

                cest1 = -1;
                cest2 = -1;
                cest3 = -1;

                if ((rst.getString("CODNCM") != null)
                        && (!rst.getString("CODNCM").trim().isEmpty())) {

                    ncm = Utils.formataNumero(rst.getString("CODNCM").trim());

                    if (ncm.length() >= 8) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 4));
                        ncm2 = Integer.parseInt(ncm.substring(4, 6));
                        ncm3 = Integer.parseInt(ncm.substring(6, 8));
                    } else if (ncm.length() == 7) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 3));
                        ncm2 = Integer.parseInt(ncm.substring(3, 5));
                        ncm3 = Integer.parseInt(ncm.substring(5, 7));
                    } else if (ncm.length() == 6) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 2));
                        ncm2 = Integer.parseInt(ncm.substring(2, 4));
                        ncm3 = Integer.parseInt(ncm.substring(4, 6));
                    }
                } else {
                    ncm1 = -1;
                    ncm2 = -1;
                    ncm3 = -1;
                }

                if ((rst.getString("CODCEST") != null)
                        && (!rst.getString("CODCEST").trim().isEmpty())) {

                    if (rst.getString("CODCEST").trim().length() == 5) {

                        cest1 = Integer.parseInt(rst.getString("CODCEST").trim().substring(0, 1));
                        cest2 = Integer.parseInt(rst.getString("CODCEST").trim().substring(1, 3));
                        cest3 = Integer.parseInt(rst.getString("CODCEST").trim().substring(3, 5));

                    } else if (rst.getString("CODCEST").trim().length() == 6) {

                        cest1 = Integer.parseInt(rst.getString("CODCEST").trim().substring(0, 1));
                        cest2 = Integer.parseInt(rst.getString("CODCEST").trim().substring(1, 4));
                        cest3 = Integer.parseInt(rst.getString("CODCEST").trim().substring(4, 6));

                    } else if (rst.getString("CODCEST").trim().length() == 7) {

                        cest1 = Integer.parseInt(rst.getString("CODCEST").trim().substring(0, 2));
                        cest2 = Integer.parseInt(rst.getString("CODCEST").trim().substring(2, 5));
                        cest3 = Integer.parseInt(rst.getString("CODCEST").trim().substring(5, 7));
                    }

                } else {
                    cest1 = -1;
                    cest2 = -1;
                    cest3 = -1;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.cest1 = cest1;
                oProduto.cest2 = cest2;
                oProduto.cest3 = cest3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarCustoProdutoGetWay(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CODPROD, PRECO_CUSTO ");
            sql.append("from view_PDV_ProdutosVenda ");
            sql.append("where CODLOJA = " + idLojaCliente + " ");            
            sql.append("and BARRAPRINCIPALPRODUTO = 1 ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("CODPROD").replace(".", ""));
                custo = rst.getDouble("PRECO_CUSTO");
                
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarPrecoProdutoGetWay(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double preco = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CODPROD, PRECO_VENDA ");
            sql.append("from view_PDV_ProdutosVenda ");
            sql.append("where CODLOJA = " + id_lojaCliente + " ");
            sql.append("and BARRAPRINCIPALPRODUTO = 1 ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("CODPROD"));
                preco = rst.getDouble("PRECO_VENDA");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarMargemProdutoGetWay(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double margemParam = 0, margemBruta = 0, margem = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT CODFORMAVENDA, MARGEM_BRUTA, MARGEM_PARAM ");
            sql.append("FROM PRODUTOS_FORMAVENDA_LOJA ");
            sql.append("where CODLOJA = " + id_lojaCliente);
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("CODFORMAVENDA"));
                margemBruta = rst.getDouble("MARGEM_BRUTA");
                margemParam = rst.getDouble("MARGEM_PARAM");
                
                if (margemParam != 0) {
                    margem = margemBruta;
                } else {
                    margem = margemParam;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;
                vProduto.put(idProduto, oProduto);
            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarEstoqueProdutoGetWay(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double saldo = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CODPROD, ESTOQUE_DEPOSITO ");
            sql.append("from view_PDV_ProdutosVenda ");
            sql.append("where CODLOJA = " + idLoja);
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("CODPROD"));
                saldo = rst.getDouble("ESTOQUE_DEPOSITO");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);
                vProduto.put(idProduto, oProduto);
            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasGetWay() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select BARRA, CODPROD, QUANTIDADE, UNIDADE from view_PDV_ProdutosVenda   ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("CODPROD"));

                if ((rst.getString("BARRA") != null)
                        && (!rst.getString("BARRA").trim().isEmpty())) {
                    String codigobarrasAux = rst.getString("BARRA").replace(".", "");
                    codigobarrasAux = Utils.formataNumero(codigobarrasAux);
                    if (codigobarrasAux.length() > 14) {
                        codigobarrasAux = codigobarrasAux.substring(1, 14);
                    }
                    codigobarras = Long.parseLong(codigobarrasAux);
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasAlternativoGetWay() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idTipoEmbalagem = 0;
        long codigobarras;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select BARRA, CODPROD, QUANTIDADE, UNIDADE from view_PDV_ProdutosVenda ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("CODPROD"));

                if ((rst.getString("BARRA") != null)
                        && (!rst.getString("BARRA").trim().isEmpty())) {
                    String codigobarrasAux = rst.getString("BARRA").replace(".", "");
                    codigobarrasAux = Utils.formataNumero(codigobarrasAux);
                    if (codigobarrasAux.length() > 14) {
                        codigobarrasAux = codigobarrasAux.substring(1, 14);
                    }
                    codigobarras = Long.parseLong(codigobarrasAux);
                } else {
                    codigobarras = -1;
                }
                
                if ((rst.getString("UNIDADE") != null) &&
                        (!rst.getString("UNIDADE").trim().isEmpty())) {
                    if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    }
                } else {
                    idTipoEmbalagem = 0;
                }

                if (String.valueOf(codigobarras).length() >= 7) {

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigobarras;
                    oAutomacao.qtdEmbalagem = (int) rst.getDouble("QUANTIDADE");
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);
                    vProduto.put(codigobarras, oProduto);
                }
            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    public List<OfertaVO> carregarOfertaProdutoGetWay(int id_Loja) throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<OfertaVO> vOferta = new ArrayList<>();
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();

            sql.append("SELECT promo.CODPROD, promo.DATAINI, promo.DATAFIM, promo.BARRA,   ");
            sql.append("       prod.PRECO_UNIT as PRECONORMAL, promo.PRECO_UNIT as  PRECOOFERTA ");
            sql.append("  FROM [GWOLAP].[dbo].PROMOCAO promo ");
            sql.append("INNER JOIN PRODUTOS prod ON ");
            sql.append("     prod.CODPROD = promo.CODPROD ");
            sql.append("WHERE promo.DATAFIM >= '2016-02-18' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                OfertaVO oferta = new OfertaVO();
                oferta.id_loja = id_Loja;
                oferta.id_produto = rst.getInt("CODPROD");
                oferta.datainicio = rst.getString("DATAINI");
                oferta.datatermino = rst.getString("DATAFIM");
                oferta.precooferta = rst.getDouble("PRECOOFERTA");
                oferta.preconormal = rst.getDouble("PRECONORMAL");
                vOferta.add(oferta);
            }
            return vOferta;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarClienteGetWay(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        int id_municipio, id_estado;
        String inscricaoEstadual;
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append("select C.CODCLIE, C.RAZAO, C.FANTASIA, C.CNPJ_CPF, C.RG, C.IE, C.ATIVO, C.LIMITECRED, C.DTCAD, ");
            sql.append("       CE.ENDERECO, CE.BAIRRO, CE.CODIBGE, CE.CIDADE, CE.ESTADO, CE.CEP, CE.NUMERO, CE.COMPLEMENTO, ");
            sql.append("       CC.CONTATO, CC.TELEFONE, CC.TELEFONE2, CC.TELEFONE3, CC.FAX, CC.EMAIL, CC.CELULAR, CC.CELULAR2, CC.CELULAR3 ");
            sql.append("  from CLIENTES C ");
            sql.append("  left JOIN CLIENTES_ENDERECO CE ON CE.CODCLIE = C.CODCLIE ");
            sql.append("  LEFT JOIN CLIENTES_CONTATO CC ON CC.CODCLIE = C.CODCLIE ");            
            rst = stm.executeQuery(sql.toString());
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    if ((rst.getString("CIDADE") != null) && (rst.getString("ESTADO") != null)) {
                        id_estado = Utils.retornarEstadoDescricao(rst.getString("ESTADO"));
                        if (id_estado == 0) {
                            id_estado = Global.idEstado; // ESTADO ESTADO DO CLIENTE
                        }
                        
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString(), rst.getString("ESTADO").toString());
                        
                        if (id_municipio == 0) {
                            id_municipio = Global.idMunicipio;// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Global.idEstado; // ESTADO ESTADO DO CLIENTE
                        id_municipio = Global.idMunicipio; // CIDADE DO CLIENTE;                   
                    }

                    if ((rst.getString("IE") != null) &&
                            (!rst.getString("IE").trim().isEmpty()) &&
                            (!"0".equals(rst.getString("IE").trim()))) {
                        inscricaoEstadual = Utils.acertarTexto(rst.getString("IE").trim());
                        inscricaoEstadual = inscricaoEstadual.replace(".", "");
                        inscricaoEstadual = inscricaoEstadual.replace("-", "");
                        inscricaoEstadual = inscricaoEstadual.replace("/", "");
                    } else {
                        if ((rst.getString("RG") != null) &&
                                (!rst.getString("RG").trim().isEmpty()) &&
                                (!"0".equals(rst.getString("RG").trim()))) {
                            inscricaoEstadual = Utils.acertarTexto(rst.getString("RG").trim());
                            inscricaoEstadual = inscricaoEstadual.replace(".", "");
                            inscricaoEstadual = inscricaoEstadual.replace("-", "");
                            inscricaoEstadual = inscricaoEstadual.replace("/", "");                            
                        } else {
                            inscricaoEstadual = "ISENTO";
                        }
                    }
                    
                    oClientePreferencial.setId(rst.getInt("CODCLIE"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("CODCLIE"));
                    oClientePreferencial.setNome(rst.getString("RAZAO"));
                    oClientePreferencial.setEndereco(rst.getString("ENDERECO"));
                    oClientePreferencial.setBairro(rst.getString("BAIRRO"));
                    oClientePreferencial.setNumero(rst.getString("NUMERO"));
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    oClientePreferencial.setCnpj(rst.getString("CNPJ_CPF"));
                    oClientePreferencial.setCep(rst.getString("CEP"));
                    oClientePreferencial.setId_tipoinscricao((String.valueOf(oClientePreferencial.getCnpj()).length() >= 11 ? 0 : 1));
                    oClientePreferencial.setInscricaoestadual(inscricaoEstadual);
                    oClientePreferencial.setId_tipoestadocivil(0);
                    oClientePreferencial.setSexo(1);
                    oClientePreferencial.setDataresidencia("2005-01-01");
                    oClientePreferencial.setDatacadastro(rst.getString("DTCAD").substring(0, 10).trim());
                    oClientePreferencial.setDatanascimento("");
                    oClientePreferencial.setTelefone(rst.getString("TELEFONE"));
                    oClientePreferencial.setTelefone2((rst.getString("TELEFONE2") == null ? "" : Utils.formataNumero(rst.getString("TELEFONE2"))));
                    oClientePreferencial.setTelefone3((rst.getString("TELEFONE3") == null ? "" : Utils.formataNumero(rst.getString("TELEFONE3"))));
                    oClientePreferencial.setCelular(rst.getString("CELULAR"));
                    oClientePreferencial.setEmail(rst.getString("EMAIL"));
                    oClientePreferencial.setValorlimite(rst.getDouble("LIMITECRED"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("ATIVO"));
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

    public List<ClientePreferencialVO> carregarClienteParaAcertarRotativoGetWay(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT CODCLIE, RAZAO ");
            sql.append("FROM CLIENTES ");
            rst = stm.executeQuery(sql.toString());
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("CODCLIE"));
                    oClientePreferencial.setNome(rst.getString("RAZAO"));
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteGetWay(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        long cnpj;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CLIENTES.CNPJ_CPF, CODRECEBER, NUMTIT, ");
            sql.append("        RECEBER.CODCLIE,                      ");
            sql.append("       NOTAECF, DTVENCTO, DTEMISSAO, DTPAGTO, coalesce(VALOR,0) VALOR, ");
            sql.append("        coalesce(VALORJUROS,0) VALORJUROS, OBS ");
            sql.append("FROM RECEBER ");
            sql.append("INNER JOIN CLIENTES ON ");
            sql.append("CLIENTES.CODCLIE = RECEBER.CODCLIE ");
            sql.append("where UPPER(SITUACAO) = 'AB'   ");
            sql.append("  and RECEBER.CODTIPODOCUMENTO IN (4) ");
            sql.append("  and RECEBER.CODLOJA = " + id_lojaCliente);
            sql.append(" order by DTEMISSAO            ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("CODCLIE");
                dataemissao = rst.getString("DTEMISSAO").replace("-", "/").substring(0, 10);
                datavencimento = rst.getString("DTVENCTO").replace("-", "/").substring(0, 10);
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("NOTAECF")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = Double.parseDouble(rst.getString("VALORJUROS"));

                if ((rst.getString("OBS") != null)
                        && (!rst.getString("OBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("OBS").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CNPJ_CPF") != null)
                        && (!rst.getString("CNPJ_CPF").isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim()));
                } else {
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberChequeVO> carregarReceberChequeGetWay(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao;
        double valor, juros;
        String observacao, dataemissao, datavencimento, agencia, conta,
                telefone, rg, nome;
        long cnpj;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CLIENTES.CNPJ_CPF, CLIENTES.RAZAO, CLIENTES.RG, CODRECEBER, NUMTIT, ");
            sql.append("       RECEBER.CODCLIE, RECEBER.NUMCHEQUE, ");
            sql.append("       NOTAECF, DTVENCTO, DTEMISSAO, DTPAGTO, coalesce(VALOR,0) VALOR, ");
            sql.append("       coalesce(VALORJUROS,0) VALORJUROS, OBS, CLIENTES.TELEFONE, ");
            sql.append("       RECEBER.CODBANCO, RECEBER.AGENCIA, RECEBER.CONTACORR ");
            sql.append("FROM RECEBER ");
            sql.append("INNER JOIN CLIENTES ON ");
            sql.append("CLIENTES.CODCLIE = RECEBER.CODCLIE ");
            sql.append("where UPPER(SITUACAO) = 'AB' ");
            sql.append("  and RECEBER.CODLOJA = " + id_lojaCliente + " ");
            sql.append("  and RECEBER.CODTIPODOCUMENTO IN (2,3) ");
            sql.append(" order by DTEMISSAO           ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                dataemissao = rst.getString("DTEMISSAO").replace("-", "/").substring(0, 10);
                datavencimento = rst.getString("DTVENCTO").replace("-", "/").substring(0, 10);
                numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF")));
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = Double.parseDouble(rst.getString("VALORJUROS"));

                if ((rst.getString("OBS") != null)
                        && (!rst.getString("OBS").isEmpty())) {
                    observacao = Utils.acertarTexto(rst.getString("OBS").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CNPJ_CPF") != null)
                        && (!rst.getString("CNPJ_CPF").isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim()));
                } else {
                    cnpj = Long.parseLong("0");
                }

                if (String.valueOf(cnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                if ((rst.getString("CODBANCO")!= null) &&
                        (!rst.getString("CODBANCO").trim().isEmpty())) {
                    idBanco = Utils.retornarBanco(Integer.parseInt(rst.getString("CODBANCO").trim()));
                } else {
                    idBanco = 804;
                }

                if ((rst.getString("NUMCHEQUE") != null)
                        && (!rst.getString("NUMCHEQUE").trim().isEmpty())) {
                    cheque = Integer.parseInt(Utils.formataNumero(rst.getString("NUMCHEQUE").trim()));
                } else {
                    cheque = 0;
                }

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    agencia = Utils.acertarTexto(rst.getString("AGENCIA").replace("'", "").trim());
                } else {
                    agencia = "";
                }

                if ((rst.getString("CONTACORR") != null)
                        && (!rst.getString("CONTACORR").trim().isEmpty())) {
                    conta = Utils.acertarTexto(rst.getString("CONTACORR").trim().replace("'", ""));
                } else {
                    conta = "";
                }

                if ((rst.getString("TELEFONE") != null)
                        && (!rst.getString("TELEFONE").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("TELEFONE").trim());
                } else {
                    telefone = "";
                }

                if ((rst.getString("RG") != null)
                        && (!rst.getString("RG").trim().isEmpty())) {
                    rg = Utils.acertarTexto(rst.getString("RG").trim().replace("'", ""));
                } else {
                    rg = "";
                }

                if ((rst.getString("RAZAO") != null)
                        && (!rst.getString("RAZAO").trim().isEmpty())) {
                    nome = Utils.acertarTexto(rst.getString("RAZAO").trim().replace("'", ""));
                } else {
                    nome = "";
                }

                if (nome.length() > 40) {
                    nome = nome.substring(0, 40);
                }
                
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cnpj;
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

        } catch (Exception ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberClienteBaixadoGetWay(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom;
        double valor, juros, valorPago;
        String observacao, dataemissao, datavencimento, dataPagamento,
                strNumeroCupom;
        long cnpj;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CLIENTES.CNPJ_CPF, CODRECEBER, NUMTIT, ");
            sql.append("       RECEBER.CODCLIE, NOTAECF, DTVENCTO, DTEMISSAO, ");
            sql.append("       DTPAGTO, coalesce(VALOR,0) VALOR, VALORPAGO, ");
            sql.append("       coalesce(VALORJUROS,0) VALORJUROS, OBS, SITUACAO ");
            sql.append("  FROM RECEBER ");
            sql.append(" INNER JOIN CLIENTES ON ");
            sql.append("            CLIENTES.CODCLIE = RECEBER.CODCLIE ");
            sql.append(" where DTPAGTO is not null ");
            sql.append("   and RECEBER.CODLOJA = " + id_lojaCliente + " ");
            sql.append(" order by DTEMISSAO ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("CODCLIE");
                dataemissao = rst.getString("DTEMISSAO").replace("-", "/").substring(0, 10);
                datavencimento = rst.getString("DTVENCTO").replace("-", "/").substring(0, 10);
                dataPagamento = rst.getString("DTPAGTO").replace("-", "/").substring(0, 10);

                if ((rst.getString("NOTAECF") != null)
                        && (!rst.getString("NOTAECF").trim().isEmpty())) {

                    strNumeroCupom = Utils.formataNumero(rst.getString("NOTAECF").trim());

                    if (strNumeroCupom.length() > 10) {
                        strNumeroCupom = strNumeroCupom.substring(0, 10);
                    }
                } else {
                    strNumeroCupom = "0";
                }

                numerocupom = Integer.parseInt(strNumeroCupom);

                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = Double.parseDouble(rst.getString("VALORJUROS"));
                valorPago = Double.parseDouble(rst.getString("VALORPAGO"));

                if ((rst.getString("OBS") != null)
                        && (!rst.getString("OBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("OBS").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CNPJ_CPF") != null)
                        && (!rst.getString("CNPJ_CPF").isEmpty())) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim()));
                } else {
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
                oReceberCreditoRotativo.dataPagamento = dataPagamento;
                oReceberCreditoRotativo.valorPago = valorPago;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<PagarOutrasDespesasVO> carregarContasPagarGetWay(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<PagarOutrasDespesasVO> vPagarOutrasDespesas = new ArrayList<>();

        int idFornecedor, numeroNota, idSituacaoDespesa;
        String dataEmissao, dataEntrada, dataVencimento,
                dataPagamento, obs, obs2, observacao;
        double valor, valorPago;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CODFORNEC, NOTA, VALOR, DTVENCTO, ");
            sql.append("DTEMISSAO, OBS, OBS2, VALORPAGO, DTPAGTO, DTENTRADA ");
            sql.append("FROM PAGAR ");
            sql.append("where CODLOJA = " + id_lojaCliente + " ");
            sql.append("and DTPAGTO IS NULL ");
            sql.append(" order by DTEMISSAO ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idFornecedor = Integer.parseInt(rst.getString("CODFORNEC").trim());

                obs = "";
                obs2 = "";
                observacao = "";

                if ((rst.getString("NOTA") != null)
                        && (!rst.getString("NOTA").trim().isEmpty())) {
                    Long longNota = Long.parseLong(Utils.formataNumero(rst.getString("NOTA")));

                    if (longNota <= Integer.MAX_VALUE) {
                        numeroNota = Integer.parseInt(Utils.formataNumero(rst.getString("NOTA")));
                        obs = " ;NOTA: " + Utils.formataNumero(rst.getString("NOTA").trim()) + "; ";
                    } else {
                        numeroNota = 0;
                        obs = " ;NOTA: " + Utils.formataNumero(rst.getString("NOTA").trim()) + "; ";
                    }
                } else {
                    numeroNota = 0;
                }

                if ((rst.getString("VALOR") != null)
                        && (!rst.getString("VALOR").trim().isEmpty())) {
                    valor = Double.parseDouble(rst.getString("VALOR").trim());
                } else {
                    valor = 0;
                }

                if ((rst.getString("VALORPAGO") != null)
                        && (!rst.getString("VALORPAGO").trim().isEmpty())) {
                    valorPago = Double.parseDouble(rst.getString("VALORPAGO").trim());
                } else {
                    valorPago = 0;
                }

                if ((rst.getString("DTENTRADA") != null)
                        && (!rst.getString("DTENTRADA").trim().isEmpty())) {
                    dataEntrada = rst.getString("DTENTRADA").trim().substring(0, 10).replace("-", "/");
                } else {
                    dataEntrada = "";
                }

                if ((rst.getString("DTEMISSAO") != null)
                        && (!rst.getString("DTEMISSAO").trim().isEmpty())) {
                    dataEmissao = rst.getString("DTEMISSAO").trim().substring(0, 10).replace("-", "/");
                } else {
                    dataEmissao = "";
                }

                if ((rst.getString("DTVENCTO") != null)
                        && (!rst.getString("DTVENCTO").trim().isEmpty())) {
                    dataVencimento = rst.getString("DTVENCTO").trim().substring(0, 10).replace("-", "/");
                } else {
                    dataVencimento = "";
                }

                if ((rst.getString("DTPAGTO") != null)
                        && (!rst.getString("DTPAGTO").trim().isEmpty())) {
                    dataPagamento = rst.getString("DTPAGTO").trim().substring(0, 10).replace("-", "/");
                    idSituacaoDespesa = 1;
                    obs = obs + " ;DATA PAGAMENTO: " + dataPagamento + ", ";
                } else {
                    idSituacaoDespesa = 0;
                    dataPagamento = "";
                }

                if ((rst.getString("OBS") != null)
                        && (!rst.getString("OBS").trim().isEmpty())) {
                    obs = obs + " ;OBS:" + Utils.acertarTexto(rst.getString("OBS").trim().replace("'", ""));
                } else {
                    obs = obs + " ";
                }

                if ((rst.getString("OBS2") != null)
                        && (!rst.getString("OBS2").trim().isEmpty())) {
                    obs2 = obs2 + " ;OBS2: " + Utils.acertarTexto(rst.getString("OBS2").trim().replace("'", ""));
                } else {
                    obs2 = obs2 + " ";
                }

                observacao = "IMPORTADO VR => " + obs + " " + obs2;

                observacao = observacao + "; VALOR PAGO: " + valorPago;

                if (observacao.length() > 280) {
                    observacao = observacao.substring(0, 280);
                }

                PagarOutrasDespesasVO oPagarOutrasDespesas = new PagarOutrasDespesasVO();
                oPagarOutrasDespesas.id_fornecedor = idFornecedor;
                oPagarOutrasDespesas.id_loja = id_loja;
                oPagarOutrasDespesas.numerodocumento = numeroNota;
                oPagarOutrasDespesas.observacao = observacao;
                oPagarOutrasDespesas.valor = valor;
                oPagarOutrasDespesas.dataemissao = dataEmissao;
                oPagarOutrasDespesas.dataentrada = dataEntrada;
                oPagarOutrasDespesas.id_situacaopagaroutrasdespesas = idSituacaoDespesa;

                PagarOutrasDespesasVencimentoVO oPagarOutrasDespesasVencimento = new PagarOutrasDespesasVencimentoVO();
                oPagarOutrasDespesasVencimento.datavencimento = dataVencimento;
                oPagarOutrasDespesasVencimento.valor = valor;

                oPagarOutrasDespesas.vPagarOutrasDespesasVencimento.add(oPagarOutrasDespesasVencimento);

                vPagarOutrasDespesas.add(oPagarOutrasDespesas);
            }

            return vPagarOutrasDespesas;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedorGetWay() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual,
                endereco, bairro, Numero = "", Telefone = "", email = "",
                cidade, complemento, telefone2, telefone3, celular;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0, id_situacaoCadastro;
        Long cnpj, cep;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT F.CODFORNEC, F.RAZAO, F.FANTASIA, F.CNPJ_CPF, F.IE, F.ATIVO, F.DTCAD, F.RG, ");
            sql.append("       FE.ENDERECO, FE.BAIRRO, FE.CODIBGE, FE.CIDADE, FE.ESTADO, FE.CEP, FE.NUMERO, FE.COMPLEMENTO, ");
            sql.append("       FC.CONTATO, FC.TELEFONE, FC.TELEFONE2, FC.TELEFONE3, FC.CELULAR, FC.CELULAR2, FC.CELULAR3, ");
            sql.append("       FC.EMAIL ");
            sql.append("  FROM FORNECEDORES F ");
            sql.append("  left join FORNECEDORES_ENDERECO FE on FE.CODFORNEC = F.CODFORNEC ");
            sql.append("  left join FORNECEDORES_CONTATO FC on FC.CODFORNEC = F.CODFORNEC ");
            sql.append(" order by F.RAZAO ");
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    id = rst.getInt("CODFORNEC");
                    Linha++;
                    
                    if ((rst.getString("TELEFONE2") != null) &&
                            (!rst.getString("TELEFONE2").trim().isEmpty())) {
                        telefone2 = Utils.formataNumero(rst.getString("TELEFONE2").trim().replace("'", ""));
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("TELEFONE3") != null) &&
                            (!rst.getString("TELEFONE3").trim().isEmpty())) {
                        telefone3 = Utils.formataNumero(rst.getString("TELEFONE3").trim().replace("'", ""));
                    } else {
                        telefone3 = "";
                    }

                    if ((rst.getString("CELULAR") != null) &&
                            (!rst.getString("CELULAR").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("CELULAR").trim().replace("'", ""));
                    } else {
                        celular = "";
                    }
                    
                    if ((rst.getString("COMPLEMENTO") != null) &&
                            (!rst.getString("COMPLEMENTO").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("COMPLEMENTO").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("RAZAO") != null)
                            && (!rst.getString("RAZAO").isEmpty())) {
                        byte[] bytes = rst.getBytes("RAZAO");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("FANTASIA") != null)
                            && (!rst.getString("FANTASIA").isEmpty())) {
                        byte[] bytes = rst.getBytes("FANTASIA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ("".equals(nomefantasia.trim())) {
                        nomefantasia = razaosocial;
                    } else if ("".equals(razaosocial.trim())) {
                        razaosocial = nomefantasia;
                    }

                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim()));
                    } else {
                        cnpj = Long.parseLong(rst.getString("CODFORNEC"));
                    }

                    if ((rst.getString("IE") != null)
                            && (!rst.getString("IE").isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("IE").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if (String.valueOf(cnpj).length() > 11) {
                        id_tipoinscricao = 0;
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("ENDERECO") != null)
                            && (!rst.getString("ENDERECO").isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("NUMERO") != null)
                            && (rst.getString("NUMERO").trim().isEmpty())
                            && (!"S/N".equals(rst.getString("NUMERO").trim()))) {

                        Numero = rst.getString("NUMERO").trim();
                        if (Numero.length() > 6) {
                            Numero = Numero.substring(0, 6);
                        }
                    } else {
                        Numero = "0";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").isEmpty())) {

                        if ((rst.getString("ESTADO") != null)
                                && (!rst.getString("ESTADO").isEmpty())) {

                            cidade = Utils.acertarTexto(rst.getString("CIDADE").trim().replace("'", ""));

                            if (cidade.contains("SAP PAULO")) {
                                cidade = "SAO PAULO";
                            }

                            id_municipio = Utils.retornarMunicipioIBGEDescricao(cidade,
                                    Utils.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("ESTADO") != null)
                            && (!rst.getString("ESTADO").isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if (rst.getString("TELEFONE") != null) {
                        Telefone = rst.getString("TELEFONE");
                    } else {
                        Telefone = "";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").isEmpty())) {
                        email = Utils.acertarTexto(rst.getString("EMAIL").replace("'", "").trim());
                    } else {
                        email = "";
                    }

                    if ((rst.getString("CONTATO") != null) &&
                            (!rst.getString("CONTATO").trim().isEmpty())) {
                        obs = "CONTATO.: "+Utils.acertarTexto(rst.getString("CONTATO").trim());
                    } else {
                        obs = "";
                    }

                    id_situacaoCadastro = rst.getInt("ATIVO");

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
                    oFornecedor.numero = Numero;
                    oFornecedor.telefone = Telefone;
                    oFornecedor.email = email;
                    oFornecedor.bairro = bairro;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = id_situacaoCadastro;
                    oFornecedor.observacao = obs;
                    oFornecedor.complemento = complemento;
                    oFornecedor.celular = celular;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
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

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorGetWay() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        String strDataAlteracao = "";
        java.sql.Date dataAlteracao;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select  CODFORNEC, CODPROD , CODREF, DTULTATU, QTDEMBAL ");
            sql.append("from prodref ");
            sql.append("order by codfornec            ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                strDataAlteracao = "";
                
                idFornecedor = rst.getInt("CODFORNEC");
                idProduto = rst.getInt("CODPROD");

                if ((rst.getString("CODREF") != null)
                        && (!rst.getString("CODREF").isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("CODREF").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }
                
                if ((rst.getString("QTDEMBAL") != null) &&
                        (!rst.getString("QTDEMBAL").trim().isEmpty())) {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("QTDEMBAL").trim());
                } else {
                    qtdEmbalagem = 1;
                }

                if ((rst.getString("DTULTATU") != null) &&
                        (!rst.getString("DTULTATU").trim().isEmpty())) {
                    strDataAlteracao = rst.getString("DTULTATU").trim().replace("-", "/").substring(0, 10);
                    dataAlteracao = new java.sql.Date(fmt.parse(strDataAlteracao).getTime());
                } else {
                    dataAlteracao = new Date(new java.util.Date().getTime());
                }
                
                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
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
        Utils util = new Utils();

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

    private List<ProdutoVO> carregarProdutoICMS() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0, cstSaida, cstEntrada;
        double valIcmsCredito;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select P.CODPROD, VW.BARRA, P.DESCRICAO, VW.DESCRICAO_PDV, P.UNIDADE, P.PESO_BRUTO, P.ATIVO, P.DATAINCLU, ");
            sql.append("       P2.CODTRIB CST_SAIDA, P2.CODTRIB_ENTRADA CST_ENTRADA, ");
            sql.append("	   P2.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
            sql.append("       P2.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
            sql.append("       A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, P2.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
            sql.append("       P2.PER_REDUC_ENT REDUCAO_CREDITO, P2.ULTICMSCRED, ");
            sql.append("	   PF.CST_PISCOFINSSAIDA, PF.CST_PISCOFINSENTRADA, PF.NAT_REC, PF.CODNCM, PF.CODCEST ");
            sql.append("  from PRODUTOS P ");
            sql.append("  left join PRODUTOS_IMPOSTOS P2 on P2.CODPROD = P.CODPROD ");
            sql.append("  left join ALIQUOTA_ICMS A on A.CODALIQ = P2.CODALIQ ");
            sql.append("  left join ALIQUOTA_ICMS A2 on A2.CODALIQ = P2.CODALIQ_NF ");
            sql.append("  left join PRODUTOS_IMPOSTOS_FEDERAIS PF on PF.CODPROD = P.CODPROD ");
            sql.append(" inner join view_PDV_ProdutosVenda VW on VW.CODPROD = P.CODPROD ");            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idProduto = rst.getInt("CODPROD");
                
                cstSaida = 0;
                cstEntrada = 0;
                valIcmsCredito = 0;
                
                if ((rst.getInt("CST_ENTRADA") == 0) &&
                        (rst.getDouble("ULTICMSCRED") == 0.0)) {
                    valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                } else {
                    valIcmsCredito = rst.getDouble("ULTICMSCRED");
                }
                
                cstSaida = rst.getInt("CST_SAIDA");
                cstEntrada = rst.getInt("CST_ENTRADA");
                
                if (cstSaida > 9) {
                    cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                }

                if (cstEntrada > 9) {
                    cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.setIdAliquotaDebito(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCredito(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaIcms_GateWay(cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO")));
                oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaIcms_GateWay(cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO")));
                oProduto.vAliquota.add(oAliquota);
                
                /*CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.ref_icmsdebito = rst.getString("ALIQ_DEBITO");
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                vProduto.add(oProduto);*/
                
                vProduto.add(oProduto);
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    //IMPORTAÇÕES
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutGetWay();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

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

    public void importarMercadologicoGetWay() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoGetWay(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoGetWay(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoGetWay(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoGetWay(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoGetWay();

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
            produto.usarMercadologicoAcertar = true;
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoTipoEmbalagemGetWay(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos Tipo Embalagem...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoGetWay();
            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {

                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }
            produto.alterarTipoEmbalagem(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutoIntegracaoGetWay(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoIntegracaoGetWay();

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
            produto.salvarIntegracao(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarAcertoCodigoAnteriorProdutoGetWay(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Anterior");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoGetWay();

            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {

                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCodigoAnterior(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    
    public void importarCustoProdutoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoGetWay(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCustoProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Custo do Produto Loja " +id_loja+ "...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoGetWay(id_loja, id_lojaCliente);

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProdutoIntegracao(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarPrecoProdutoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoGetWay(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Preço Produto "+id_loja+"...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoGetWay(id_loja, id_lojaCliente);

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProdutoIntegracao(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarMargemProdutoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarMargemProdutoGetWay(id_loja, id_lojaCliente);

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarMargemProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProdutoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoGetWay(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Integer keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Estoque Produto "+id_loja+"...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoGetWay(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Integer keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProdutoIntegracao(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarOfertaProdutoGetWay(int id_Loja) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Oferta...");
            List<OfertaVO> vOferta = carregarOfertaProdutoGetWay(id_Loja);
            new OfertaDAO().salvar(vOferta, id_Loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarraGetWay(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasGetWay();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Long keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.verificarLoja = true;
            produto.id_loja = id_loja;
            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarraAlternativoGetWay(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasAlternativoGetWay();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Long keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.verificarLoja = true;
            produto.id_loja = id_loja;
            produto.alterarBarraAnterio = true;
            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteGetWay(int idLoja, int idLojaCliente) throws Exception {

        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");

            vCliente = carregarClienteGetWay(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vCliente, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteCpfGetWay(int idLoja, int idLojaCliente) throws Exception {

        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");

            vCliente = carregarClienteGetWay(idLoja, idLojaCliente);
            new ClientePreferencialDAO().salvarCpf(vCliente, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteNomeGetWay(int idLoja, int idLojaCliente) throws Exception {

        List<ClientePreferencialVO> vCliente = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");

            vCliente = carregarClienteGetWay(idLoja, idLojaCliente);
            new ClientePreferencialDAO().salvarNome(vCliente, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteAcertoNomeGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...Acerto pelo Nome "+idLoja+"...");
            List<ClientePreferencialVO> vReceberCliente = carregarClienteParaAcertarRotativoGetWay(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().alterarCreditoRotativoPeloNomeCliente(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberChequeGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cheque...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeGetWay(idLoja, idLojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteBaixadoGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente Baixado...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteBaixadoGetWay(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvarContaBaixada(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteComCpfGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente com Cpf...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvarComCnpj(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteCondicaoGetWay(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente com Condição...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarFornecedorGetWay() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorGetWay();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorGetWay();

            new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);

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

    public void importarContasPagar(int id_loja, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<PagarOutrasDespesasVO> vPagarOutrasDespesas = carregarContasPagarGetWay(id_loja, i_idLojaDestino);

            ProgressBar.setMaximum(vPagarOutrasDespesas.size());

            PagarOutrasDespesasDAO pagarOutrasDespesasDAO = new PagarOutrasDespesasDAO();
            pagarOutrasDespesasDAO.salvar(vPagarOutrasDespesas);
        } catch (Exception e) {
            throw e;
        }
    }

    public void importarCestProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Código CEST...");
            vProduto = carregarCestProdutoGetWay();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarCestProduto(vProduto);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarNCMCestProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Código NCM x CEST...");
            vProduto = carregarCestProdutoGetWay();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().adicionarNcmCestProduto(vProduto);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    // FUNÇÕES
    private int retornarAliquotaICMS(String codTrib) {
        int retorno;
        if ("F".equals(codTrib.substring(0, 1))) {
            retorno = 7;
        } else if ("I".equals(codTrib.substring(0, 1))) {
            retorno = 6;
        } else if ("N".equals(codTrib.substring(0, 1))) {
            retorno = 17;
        } else if ("TA".equals(codTrib)) {
            retorno = 0;
        } else if ("TB".equals(codTrib)) {
            retorno = 1;
        } else if ("TC".equals(codTrib)) {
            retorno = 2;
        } else if ("TD".equals(codTrib)) {
            retorno = 3;
        } else {
            retorno = 8;
        }
        return retorno;
    }

    private int retornarAliquotaICMS2(String codTrib) {
        int retorno;
        if ("F".equals(codTrib.substring(0, 1))) {
            retorno = 7;
        } else if ("I".equals(codTrib.substring(0, 1))) {
            retorno = 6;
        } else if ("N".equals(codTrib.substring(0, 1))) {
            retorno = 17;
        } else if ("TA".equals(codTrib)) {
            retorno = 2;
        } else if ("TB".equals(codTrib)) {
            retorno = 3;
        } else if ("TC".equals(codTrib)) {
            retorno = 1;
        } else if ("TD".equals(codTrib)) {
            retorno = 0;
        } else {
            retorno = 8;
        }
        return retorno;
    }

    private boolean verificaExisteMercadologico(int mercad1, int mercad2, int mercad3)
            throws SQLException, Exception {

        boolean retorno = true;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select * from mercadologico ");
        sql.append("where mercadologico1 = " + mercad1 + " ");
        sql.append("and mercadologico2 = " + mercad2 + " ");
        sql.append("and mercadologico3 = " + mercad3 + " ");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            retorno = false;
        }

        return retorno;
    }
    
    public void importarAcertoIcms() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...ICMS Produtos...");
            List<ProdutoVO> vProduto = carregarProdutoICMS();

            new ProdutoDAO().alterarICMSProduto(vProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    private int retornarIcmsCredito(Integer cstTrib, double valor, double reducao) {
        int retorno = 8;
    
        valor = Utils.arredondar(valor, 1);
        reducao = Utils.arredondar(reducao, 1);

        if (cstTrib == 0) {
            if (valor == 7.0) {
                retorno = 0;
            } else if (valor == 12.0) {
                retorno = 1;
            } else if (valor == 18.0) {
                retorno = 2;
            } else if (valor == 25.0) {
                retorno = 3;
            }
        } else if (cstTrib == 20) {
            if (reducao == 61.1) {
                retorno = 4;
            } else if ((reducao == 33.3)) {
                retorno = 9;
            } else if (reducao == 52.0) {
                retorno = 10;
            } else if (reducao == 41.6) {
                retorno = 5;
            }
        } else if (cstTrib == 40) {
            retorno = 6;
        } else if (cstTrib == 41) {
            retorno = 17;
        } else if (cstTrib == 50) {
            retorno = 13;
        } else if (cstTrib == 51) {
            retorno = 16;
        } else if (cstTrib == 60) {
            retorno = 7;
        } else if (cstTrib == 90) {
            retorno = 8;
        }

        return retorno;
    }
}
