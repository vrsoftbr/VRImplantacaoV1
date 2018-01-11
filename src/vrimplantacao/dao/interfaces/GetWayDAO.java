package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.file.Log;
import vrimplantacao.classe.file.LogAdicional;
import vrimplantacao.classe.file.LogFileType;
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
import vrimplantacao.vo.vrimplantacao.EstadoVO;
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
import vrimplantacao2.parametro.Parametros;

public class GetWayDAO {

    public String Texto;
    //CARREGAMENTOS
    Utils util = new Utils();

    public List<ProdutoVO> carregarSituacaoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        long codigoBarras;
        int idProduto, idSituacaoCadastro;
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select BARRA, ATIVO from PRODUTOS where len(barra) >= 7"
            )) {
                while (rst.next()) {
                    
                    idProduto = 0;
                    
                    if ((rst.getString("BARRA") != null) && (!rst.getString("BARRA").trim().isEmpty())) {
                        codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                        idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(codigoBarras);
                        idSituacaoCadastro = ("A".equals(rst.getString("ATIVO").trim()) ? 1 : 0);
                        
                        if ((idProduto > 0) && (codigoBarras > 999999)) {
                            ProdutoVO oProduto = new ProdutoVO();
                            ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                            oProduto.setId(idProduto);
                            oComplemento.setIdSituacaoCadastro(idSituacaoCadastro);
                            vProduto.add(oProduto);
                        }
                    }
                }
            }
        }
        return vProduto;                
    }
    
    public void importarSituacaoProduto(int idLojaVR, int idLojaCliente) throws Exception  {
        try {
            List<ProdutoVO> vProduto = new ArrayList<>();
            ProgressBar.setStatus("Carrgando dados...Situação Produtos Loja "+idLojaVR);
            vProduto = carregarSituacaoProduto(idLojaVR, idLojaVR);
            new ProdutoDAO().alterarSituacaoCadastroProdutoIntegracao(vProduto, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente, 
            Date dataInicio, Date dataOferta, boolean produtosIguais) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        int idProduto;
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select O.CODPROD, O.PRECO_UNIT, O.DATAINI, O.DATAFIM, "
                    + "P.BARRA "
                    + "from PROMOCAO O "
                    + "inner join PRODUTOS P on P.CODPROD = O.CODPROD "
                    + "where DATAFIM >= " + Utils.dateSQL(dataOferta) + ""
            )) {
                while (rst.next()) {                    
                    idProduto = 0;                    
                    
                    if (produtosIguais) {
                        idProduto = rst.getInt("CODPROD");
                    } else {
                        if ((rst.getString("BARRA") != null)
                                && (!rst.getString("BARRA").trim().isEmpty())) {
                            idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim())));
                        }
                    }
                    
                    if (idProduto > 0) {                    
                        OfertaVO vo = new OfertaVO();
                        vo.setId_loja(idLojaVR);
                        vo.setId_produto(idProduto);
                        vo.setDatainicio(dataInicio);
                        vo.setDatatermino(rst.getString("DATAFIM").substring(0, 10));
                        vo.setPrecooferta(rst.getDouble("PRECO_UNIT"));
                        ofertas.add(vo);
                    }
                }
            }
        }
        
        return ofertas;
    }
    
    public void importarOfertas(int idLojaVR, int idLojaCliente,
            Date dataInicio, Date dataOferta, boolean produtosIguais) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente, dataInicio, dataOferta, produtosIguais);
        
        if (!produtosIguais) {
            new OfertaDAO().salvarUnificacao(ofertas, idLojaVR);
        } else {
            new OfertaDAO().salvar(ofertas, idLojaVR);
        }
    }
    
    public List<ProdutoVO> carregarNcmProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, ncm1, ncm2, ncm3;
        String ncmAtual = "";
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select codprod, codncm from produtos");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idProduto = rst.getInt("codprod");
                ncmAtual = "";
                if ((rst.getString("codncm") != null)
                        && (!rst.getString("codncm").trim().isEmpty())
                        && (rst.getString("codncm").trim().length() > 5)) {
                    ncmAtual = Utils.formataNumero(rst.getString("codncm").trim());
                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oProduto.id = idProduto;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;                
                oAnterior.codigoanterior = idProduto;
                oAnterior.ncm = ncmAtual;
                oProduto.vCodigoAnterior.add(oAnterior);
                vProduto.add(oProduto);
            }
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarNcmProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Ncm Produto...");
            vProduto = carregarNcmProduto();
            new ProdutoDAO().alterarNcm(vProduto, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarNumeroEnderecoFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        String numero = "0";
        int idFornecedor = 0;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select codfornec, numero from fornecedores ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idFornecedor = rst.getInt("codfornec");
                if ((rst.getString("numero") != null) &&
                        (!rst.getString("numero").trim().isEmpty())) {
                    numero = Utils.acertarTexto(rst.getString("numero").trim());
                } else {
                    numero = "0";
                }
                
                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }
                
                FornecedorVO oFornecedor = new FornecedorVO();
                oFornecedor.id = idFornecedor;
                oFornecedor.numero = numero;
                vFornecedor.add(oFornecedor);
            }
            
            stm.close();
            return vFornecedor;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarNumeroEnderecoFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Numero End. Fornecedor...");
            vFornecedor = carregarNumeroEnderecoFornecedor();
            new FornecedorDAO().alterarNumeroEndereco(vFornecedor);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
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
    
    public List<ProdutoVO> carregarCodigoBarrasAtacadoLoja(int idLoja, boolean i_gerarCodigoAtacado, boolean i_codigoAnterior) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras = -2;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select P.BARRA, P.DESCRICAO, E.BARRA_EMB, E.QTD, E.PRECO_UNIT ");
            sql.append("from EMBALAGENS E ");
            sql.append("inner join PRODUTOS P on P.CODPROD = E.CODPROD ");
            sql.append("where E.QTD > 1 ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                codigoBarras = -2;
                ProdutoVO oProduto = new ProdutoVO();
                ProdutoAutomacaoLojaVO oAutomacaoLoja = new ProdutoAutomacaoLojaVO();
                
                if (!i_codigoAnterior) {
                    oProduto.codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA")));
                } else {
                    oProduto.setIdDouble(rst.getDouble("CODPROD"));
                }
                
                if (i_gerarCodigoAtacado) {
                    if ((oProduto.getIdDouble() <= 9999)) {
                        codigoBarras = Utils.gerarEan13((int) oProduto.getIdDouble(), false);
                    } else {
                        codigoBarras = Utils.gerarEan13((int) oProduto.getIdDouble(), true);
                    }
                } else {                    
                    if (("789".equals(rst.getString("BARRA_EMB").trim().substring(0, 3))) && 
                            (rst.getString("BARRA_EMB").length() >= 7)) {
                        codigoBarras = Long.parseLong("1" + Utils.formataNumero(rst.getString("BARRA_EMB").trim()));
                    } else {
                        codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA_EMB").trim()));
                    }                    
                }
                
                if (String.valueOf(codigoBarras).length() > 14) {
                    codigoBarras = Long.parseLong(String.valueOf(codigoBarras).substring(0, 14));
                }

                if (rst.getString("BARRA").trim().length() >= 7) {
                    oAutomacaoLoja.codigobarras = codigoBarras;
                    oAutomacaoLoja.precovenda = rst.getDouble("PRECO_UNIT");
                    oAutomacaoLoja.qtdEmbalagem = (int) rst.getDouble("QTD");
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
    
    public void importarCodigoBarrasAtacadoLoja(int idLoja, boolean i_gerarCodigoAtacado, boolean i_codigoBarras) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Código barras atacado Loja "+idLoja+"...");
            vProduto = carregarCodigoBarrasAtacadoLoja(idLoja, i_gerarCodigoAtacado, i_codigoBarras);
            
            if (!vProduto.isEmpty()) {
                produto.addCodigoBarrasAtacadoSemCodigoAnterior(vProduto, idLoja);
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

                oFamiliaProduto.idLong = Long.parseLong(rst.getString("CODFAMILIA"));
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
            idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, cstSaida, cstEntrada,
            ncm1, ncm2, ncm3, codigoBalanca, validade = 0, idTipoPisCofinsAux = 0, idTipoPisCofinsCreditoAux = 0,
            tipoNaturezaReceitaAux = 0, idCest;
        double valIcmsCredito, margem;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual = "", strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;

        try (Log log = new Log("Balança.html", "Produtos de balança", LogFileType.HTML)) {
            try {
                stmPostgres = Conexao.createStatement();

                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();

                sql.append("select  fam.codfamilia, prod.codtrib, prod.codprod, prod.descricao, prod.desc_pdv, ");
                sql.append("        coalesce(merc.codcreceita, 1)as MERC1, ");
                sql.append("        coalesce(merc.codgrupo, 1) as MERC2, ");
                sql.append("        coalesce(merc.codcategoria, 1) as MERC3, ");
                sql.append("        prod.codaliq, prod.barra, prod.unidade, prod.estoque, prod.preco_unit, ");
                sql.append("        prod.margem_bruta, prod.margem_param, prod.codaliq_nf, prod.obs, prod.dtaltera, prod.dtinclui, ");
                sql.append("        prod.qtd_emb, prod.preco_especial, prod.cst_pisentrada, prod.cst_pissaida, ");
                sql.append("        prod.cst_cofinsentrada, prod.cst_cofinssaida, prod.nat_rec, ");
                sql.append("        prod.generoitem_sef2, prod.aliquota_ibpt, ");
                sql.append("        prod.aliquota_ibptest, prod.aliquota_ibptmun, prod.codncm, prod.ATIVO, prod.CODCEST, prod.QTD_EMBVENDA, ");
                sql.append("        Prod.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
                sql.append("        Prod.CODTRIB CST_SAIDA, Prod.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
                sql.append("        A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, Prod.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
                sql.append("        Prod.CODTRIB_ENT CST_ENTRADA, Prod.PER_REDUC_ENT REDUCAO_CREDITO, PROD.ULTICMSCRED, ");
                sql.append("        PROD.CODCEST, prod.DTINCLUI ");
                sql.append("   from produtos prod ");
                sql.append("  inner join ALIQUOTA_ICMS A on A.CODALIQ = PROD.CODALIQ ");
                sql.append("  INNER JOIN ALIQUOTA_ICMS A2 ON A2.CODALIQ = PROD.CODALIQ_NF ");
                sql.append("   left outer join CATEGORIA merc ON ");
                sql.append("	merc.CODCRECEITA = prod.codcreceita and ");
                sql.append("	merc.CODGRUPO     = prod.codgrupo and ");
                sql.append("	merc.CODCATEGORIA = prod.codcategoria ");
                sql.append("	left outer join PROD_FAMILIA fam ON ");
                sql.append("		fam.CODPROD = prod.CODPROD ");
                sql.append("   and prod.codprod > 0 ");
                sql.append("order by prod.codprod ");

                rst = stm.executeQuery(sql.toString());

                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();
                    ncmAtual = "";
                    idSituacaoCadastro = 1;
                    cstSaida = Integer.parseInt(Utils.formataNumero(rst.getString("CST_SAIDA")));
                    cstEntrada = Integer.parseInt(Utils.formataNumero(rst.getString("CST_ENTRADA")));

                    if (rst.getString("DTINCLUI") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("DTINCLUI")).substring(0, 10).replace("-", "/"));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }

                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }

                    valIcmsCredito = 0;
                    margem = rst.getDouble("margem_param");

                    if ((Integer.parseInt(Utils.formataNumero(rst.getString("CST_ENTRADA"))) == 0) &&
                            (rst.getDouble("ULTICMSCRED") == 0.0)) {
                        valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                    } else {
                        valIcmsCredito = rst.getDouble("ULTICMSCRED");
                    }

                    if ("S".equals(rst.getString("ATIVO").trim())) {
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

                        if ((rst.getString("UNIDADE") != null) &&
                                (!rst.getString("UNIDADE").trim().isEmpty())) {
                            if ("CX".equals(rst.getString("UNIDADE").trim())) {
                                idTipoEmbalagem = 1;
                            } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                                idTipoEmbalagem = 4;
                            } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                                idTipoEmbalagem = 0;
                            } else {
                                idTipoEmbalagem = 0;
                            }
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

                    if ((rst.getString("DESC_PDV") != null)
                            && (!rst.getString("DESC_PDV").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("DESC_PDV");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        descricaoReduzida = "";
                    }

                    descricaoGondola = descricaoReduzida;

                    if (idTipoEmbalagem == 4) {
                        qtdEmbalagem = 1;
                    } else {

                        if ((rst.getString("QTD_EMBVENDA") != null)
                                && (!rst.getString("QTD_EMBVENDA").trim().isEmpty())) {
                            qtdEmbalagem = (int) rst.getDouble("QTD_EMBVENDA");
                        } else {
                            qtdEmbalagem = 1;
                        }
                    }

                    if ((rst.getString("codfamilia") != null)
                            && (!rst.getString("codfamilia").trim().isEmpty())
                            && (!"0".equals(rst.getString("codfamilia").trim()))) {
                        idFamilia = Integer.parseInt(rst.getString("codfamilia").trim().replace(".", ""));
                    } else {
                        idFamilia = -1;
                    }
                    if ((rst.getString("MERC1") != null)
                            && (!rst.getString("MERC1").isEmpty())) {
                        mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                    } else {
                        mercadologico1 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                    }
                    if ((rst.getString("MERC2") != null)
                            && (!rst.getString("MERC2").isEmpty())) {
                        mercadologico2 = Integer.parseInt(rst.getString("MERC2"));
                    } else {
                        mercadologico2 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                    }

                    if ((rst.getString("MERC3") != null)
                            && (!rst.getString("MERC3").isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("MERC3"));
                    } else {
                        mercadologico3 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                    }

                    if ((rst.getString("codncm") != null)
                            && (!rst.getString("codncm").isEmpty())
                            && (rst.getString("codncm").trim().length() > 5)) {
                        ncmAtual = Utils.formataNumero(rst.getString("codncm").trim());
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

                    strCodigoBarras = "";
                    codigoBarras = -2;

                    if (eBalanca == true) {
                        codigoBarras = -1;//Long.parseLong(String.valueOf(idProduto));
                    } else {
                        if ((rst.getString("barra") != null)
                                && (!rst.getString("barra").trim().isEmpty())) {
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
                    
                    if (eBalanca) {
                        log.addLog("Balança", idProduto + " - " + codigoBarras + " - " + codigoBalanca + " - " + descriaoCompleta , new LogAdicional());
                    }

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
                                Utils.formataNumero(rst.getString("NAT_REC").trim()));
                        tipoNaturezaReceitaAux = Integer.parseInt(Utils.formataNumero(rst.getString("NAT_REC").trim()));
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
                    oProduto.idCest = idCest;
                    oProduto.margem = margem;
                    oProduto.eBalanca = eBalanca;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;                
                    oComplemento.emiteEtiqueta = true;
                    oProduto.vComplemento.add(oComplemento);

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), true));
                    oProduto.vAliquota.add(oAliquota);

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;
                    oAutomacao.qtdEmbalagem = qtdEmbalagem;
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);

                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    oCodigoAnterior.codigoanterior = idProduto;
                    oCodigoAnterior.codigoatual = idProduto;

                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {
                        strCodigoBarras = Utils.formataNumero(rst.getString("BARRA").trim());
                        if (strCodigoBarras.length() > 14) {
                            codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                        } else {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                        oCodigoAnterior.barras = codigoBarras;
                    } else {
                        oCodigoAnterior.barras = -2;
                    }

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

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    vProduto.put(idProduto, oProduto);
                }

                stm.close();
                stmPostgres.close();
                return vProduto;

            } catch (Exception ex) {
                throw ex;
            }
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoIntegracaoGetWay() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
            idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, cstSaida, cstEntrada,
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

            sql.append("select  fam.codfamilia, prod.codtrib, prod.codprod, prod.descricao, prod.desc_pdv, ");
            sql.append("        coalesce(merc.codcreceita, 1)as MERC1, ");
            sql.append("        coalesce(merc.codgrupo, 1) as MERC2, ");
            sql.append("        coalesce(merc.codcategoria, 1) as MERC3, ");
            sql.append("        prod.codaliq, prod.barra, prod.unidade, prod.estoque, prod.preco_unit, ");
            sql.append("        prod.margem_bruta, prod.margem_param, prod.codaliq_nf, prod.obs, prod.dtaltera, prod.dtinclui, ");
            sql.append("        prod.qtd_emb, prod.preco_especial, prod.cst_pisentrada, prod.cst_pissaida, ");
            sql.append("        prod.cst_cofinsentrada, prod.cst_cofinssaida, prod.nat_rec, ");
            sql.append("        prod.generoitem_sef2, prod.aliquota_ibpt, ");
            sql.append("        prod.aliquota_ibptest, prod.aliquota_ibptmun, prod.codncm, prod.ATIVO, prod.CODCEST, prod.QTD_EMBVENDA, ");
            sql.append("        Prod.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
            sql.append("        Prod.CODTRIB CST_SAIDA, Prod.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
            sql.append("        A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, Prod.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
            sql.append("        Prod.CODTRIB_ENT CST_ENTRADA, Prod.PER_REDUC_ENT REDUCAO_CREDITO, PROD.ULTICMSCRED, ");
            sql.append("        PROD.CODCEST ");
            sql.append("   from produtos prod ");
            sql.append("  inner join ALIQUOTA_ICMS A on A.CODALIQ = PROD.CODALIQ ");
            sql.append("  INNER JOIN ALIQUOTA_ICMS A2 ON A2.CODALIQ = PROD.CODALIQ_NF ");
            sql.append("   left outer join CATEGORIA merc ON ");
            sql.append("	merc.CODCRECEITA = prod.codcreceita and ");
            sql.append("	merc.CODGRUPO     = prod.codgrupo and ");
            sql.append("	merc.CODCATEGORIA = prod.codcategoria ");
            sql.append("	left outer join PROD_FAMILIA fam ON ");
            sql.append("		fam.CODPROD = prod.CODPROD ");
            sql.append("order by prod.codprod ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                cstSaida = rst.getInt("CST_SAIDA");
                cstEntrada = rst.getInt("CST_ENTRADA");
                
                if (cstSaida > 9) {
                    cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                }

                if (cstEntrada > 9) {
                    cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                }
                
                valIcmsCredito = 0;
                margem = rst.getDouble("margem_param");
                
                if ((rst.getInt("CST_ENTRADA") == 0) &&
                        (rst.getDouble("ULTICMSCRED") == 0.0)) {
                    valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                } else {
                    valIcmsCredito = rst.getDouble("ULTICMSCRED");
                }
                
                if ("S".equals(rst.getString("ATIVO").trim())) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                /*sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("BARRA").trim().replace(".", ""));

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
                }*/

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

                if ((rst.getString("DESC_PDV") != null)
                        && (!rst.getString("DESC_PDV").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESC_PDV");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = "";
                }

                descricaoGondola = descricaoReduzida;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {

                    if ((rst.getString("QTD_EMBVENDA") != null)
                            && (!rst.getString("QTD_EMBVENDA").trim().isEmpty())) {
                        qtdEmbalagem = (int) rst.getDouble("QTD_EMBVENDA");
                    } else {
                        qtdEmbalagem = 1;
                    }
                }

                idFamilia = -1;
                
                if ((rst.getString("MERC1") != null)
                        && (!rst.getString("MERC1").isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("MERC1"));
                } else {
                    mercadologico1 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                }
                if ((rst.getString("MERC2") != null)
                        && (!rst.getString("MERC2").isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("MERC2"));
                } else {
                    mercadologico2 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                }

                if ((rst.getString("MERC3") != null)
                        && (!rst.getString("MERC3").isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("MERC3"));
                } else {
                    mercadologico3 = -1;// NO BANCO DO CLIENTE 500 E PADRÃO                         
                }

                if ((rst.getString("codncm") != null)
                        && (!rst.getString("codncm").isEmpty())
                        && (rst.getString("codncm").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("codncm").trim());

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

                    if ((rst.getString("barra") != null)
                            && (!rst.getString("barra").trim().isEmpty())) {

                        codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("barra").trim()));

                        if (String.valueOf(codigoBarras).length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (String.valueOf(codigoBarras).length() > 14) {
                                codigoBarras = Long.parseLong(String.valueOf(codigoBarras).substring(0, 14));
                            } else {
                                codigoBarras = codigoBarras;
                            }
                        }
                    }
                }

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
                oProduto.codigoBarras = codigoBarras;
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
                oProduto.validade = validade;
                oProduto.idCest = idCest;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;                
                oComplemento.emiteEtiqueta = true;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Parametros.get().getUfPadrao().getId();                
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
                
                if ((rst.getString("BARRA") != null) &&
                        (!rst.getString("BARRA").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }
                
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
        Utils util = new Utils();
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
            sql.append("  from PRODUTOS ");
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

    public Map<Integer, ProdutoVO> carregarCustoProdutoGetWay(int idLoja, int idLojaCliente, 
            boolean produtosIguais) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;
        long barras;        

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select p.CODPROD, p.BARRA, \n" +
                        "       pl.PRECO_CUST\n" +
                        "from PRODUTOS p\n" +
                        "inner join PROD_LOJA pl on pl.CODPROD = p.CODPROD\n" +
                        "where pl.CODLOJA = " + idLojaCliente);
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = 0;
                barras = 0;
                custo = rst.getDouble("PRECO_CUST");

                if (produtosIguais) {
                    idProduto = Integer.parseInt(rst.getString("CODPROD"));
                } else {
                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {
                        
                        barras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                        
                        if (barras > 999999) {
                            idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(barras);
                        }
                    }
                }
                
                if (idProduto > 0) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    oProduto.id = idProduto;
                    oProduto.codigoBarras = barras;
                    oComplemento.idLoja = idLoja;
                    oComplemento.custoComImposto = custo;
                    oComplemento.custoSemImposto = custo;
                    oProduto.vComplemento.add(oComplemento);
                    oCodigoAnterior.custocomimposto = custo;
                    oCodigoAnterior.custosemimposto = custo;
                    oCodigoAnterior.id_loja = idLoja;
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);
                    vProduto.put(idProduto, oProduto);
                }
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /* o parametro produtosIguais é quando os produtos das lojas tiver o mesmo código */
    public Map<Integer, ProdutoVO> carregarPrecoProdutoGetWay(int idLoja, int id_lojaCliente, 
            boolean produtosIguais) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double preco = 0;
        long barras;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select p.CODPROD, p.BARRA, \n" +
                        "       pl.PRECO_UNIT, pl.MARGEM_PARAM\n" +
                        "from PRODUTOS p\n" +
                        "inner join PROD_LOJA pl on pl.CODPROD = p.CODPROD\n" +
                        " where pl.CODLOJA  = " + id_lojaCliente);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = 0;
                barras = 0;
                preco = rst.getDouble("PRECO_UNIT");
                
                if (produtosIguais) {
                    idProduto = rst.getInt("CODPROD");
                } else {
                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {
                        barras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                        
                        if (barras > 999999) {
                            idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(barras);
                        } 
                    }
                }
                                
                ProdutoVO oProduto = new ProdutoVO();
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oProduto.id = idProduto;
                oProduto.codigoBarras = barras;
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;
                oProduto.vComplemento.add(oComplemento);
                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                vProduto.put(idProduto, oProduto);                
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarMargemProdutoGetWay(int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double margem = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("Select CODPROD, PRECO_UNIT, "
                    + "MARGEM_PARAM, MARGEM_BRUTA from PRODUTOS ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("CODPROD"));
                margem = rst.getDouble("MARGEM_BRUTA");
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
        long barras;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select p.CODPROD, p.BARRA, \n" +
                        "       pl.ESTOQUE\n" +
                        "from PRODUTOS p\n" +
                        "inner join PROD_LOJA pl on pl.CODPROD = p.CODPROD\n" +
                        "where pl.CODLOJA = " + id_lojaCliente);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODPROD"));
                saldo = rst.getDouble("ESTOQUE");
                barras = -2;                
                if ((rst.getString("BARRA") != null)
                        && (!rst.getString("BARRA").trim().isEmpty())) {                    
                    barras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.codigoBarras = barras;

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
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarFamiliaProdutoIntegracaoGetWay() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, idFamiliaProduto;
        long codigoBarras = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select f.codfamilia, f.codprod, p.barra ");
            sql.append("from prod_familia f ");
            sql.append("inner join produtos p on p.codprod = f.codprod ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("CODPROD"));

                if ((rst.getString("BARRA") != null)
                        && (!rst.getString("BARRA").trim().isEmpty())) {

                    codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim()));

                    if (String.valueOf(codigoBarras).length() < 7) {
                        codigoBarras = -2;
                    } else {
                        if (String.valueOf(codigoBarras).length() > 14) {
                            codigoBarras = Long.parseLong(String.valueOf(codigoBarras).substring(0, 14));
                        }
                    }
                }

                idFamiliaProduto = rst.getInt("codfamilia");                
                if (String.valueOf(codigoBarras).length() >= 7) {
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.codigoBarras = codigoBarras;
                    oProduto.idFamiliaProduto = idFamiliaProduto;
                    vProduto.add(oProduto);
                }

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
        long codigoBarras = -2;
        String strCodigoBarras = "";

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("Select CODPROD, BARRA from PRODUTOS   ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("CODPROD"));
                codigoBarras = -2;
                if ((rst.getString("barra") != null)
                        && (!rst.getString("barra").trim().isEmpty())) {
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

                if (String.valueOf(codigoBarras).length() >= 7) {

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;
                    oProduto.vAutomacao.add(oAutomacao);
                    vProduto.put(codigoBarras, oProduto);
                }
            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }
   
    public Map<Long, ProdutoVO> carregarCodigoBarrasAlternativoGetWay(boolean unificacao) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idTipoEmbalagem;
        long codigobarras;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select P.CODPROD COD_PRINCIPAL, P.BARRA BARRA_PRINCIPAL, ");
            sql.append("A.BARRA, A.CODPROD, P.UNIDADE ");
            sql.append("from ALTERNATIVO A ");
            sql.append("inner join PRODUTOS P on P.CODPROD = A.CODPROD ");
            sql.append("order by COD_PRINCIPAL, BARRA_PRINCIPAL ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = 0;
                
                if (unificacao) {
                    idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("BARRA_PRINCIPAL").trim())));
                } else {
                    idProduto = Integer.parseInt(rst.getString("CODPROD"));
                }
                
                idTipoEmbalagem = 0;
                if ((rst.getString("UNIDADE") != null) && (!rst.getString("UNIDADE").trim().isEmpty())) {
                    idTipoEmbalagem = Utils.converteTipoEmbalagem(rst.getString("UNIDADE").trim());
                }

                if ((rst.getString("BARRA") != null)
                        && (!rst.getString("BARRA").trim().isEmpty())) {
                    String codigobarrasAux = rst.getString("BARRA").replace(".", "");
                    codigobarrasAux = Utils.formataNumero(codigobarrasAux);
                    if (codigobarrasAux.length() > 14) {
                        codigobarrasAux = codigobarrasAux.substring(1, 14);
                    }
                    codigobarras = Long.parseLong(codigobarrasAux);
                } else {
                    codigobarras = 0;
                }

                if (idProduto > 0) {
                    if (String.valueOf(codigobarras).length() >= 7) {
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = codigobarras;
                        
                        if (unificacao) {
                            oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                        }
                        
                        oProduto.vAutomacao.add(oAutomacao);
                        vProduto.put(codigobarras, oProduto);
                    }
                }
            }
            return vProduto;
        } catch (Exception ex) {
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
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT CASE COALESCE(PESSOA,'F') WHEN 'F' THEN 1 ELSE 0 END AS PESSOA, CODCLIE, RAZAO, ENDERECO, BAIRRO, CIDADE, ESTADO, CEP, NUMERO,  ");
            sql.append("       CNPJ_CPF, TELEFONE, RG, FONE1, FONE2, EMAIL, DTANIVER, coalesce(LIMITECRED,0) LIMITECRED, coalesce(RENDA,0) RENDA, CARGO, ");
            sql.append("       EMPRESA, FONE_EMP, CASE ATIVO WHEN 'S' THEN 1 ELSE 0 END AS ATIVO, ESTADOCIVIL, CASE SEXO WHEN 'M' THEN 1 ELSE 0 END AS SEXO, NOMEPAI, NOMEMAE, DTALTERA,");
            sql.append("       CELULAR, NOMECONJUGE, CARGOCONJUGE, CPF_CONJUGE, RG_CONJUGE, coalesce(RENDACONJUGE,0) as RENDACONJUGE, DTCAD, ");
            sql.append("       CASE ESTADOCIVIL WHEN 'S' THEN 1 ");
            sql.append("                        WHEN 'C' THEN 2 ");
            sql.append("                        WHEN 'V' THEN 3 ");
            sql.append("                        WHEN 'A' THEN 4 ");
            sql.append("                        WHEN 'O' THEN 5 ");
            sql.append("       ELSE 0 END AS ESTADOCIVILNOVO,       ");
            sql.append("       COMPLEMENTO+' '+CONTATO+' '+REF1_NOME+' '+REF2_NOME+' '+FONE1 AS OBS ");
            sql.append("FROM CLIENTES            ");
            sql.append("where CODCLIE >= 1 ");
            rst = stm.executeQuery(sql.toString());
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    
                    if ((rst.getString("CIDADE") != null) && (rst.getString("ESTADO") != null)) {
                        id_estado = Utils.retornarEstadoDescricao(rst.getString("ESTADO"));
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString(), rst.getString("ESTADO").toString());
                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        id_municipio = Parametros.get().getMunicipioPadrao2().getId(); // CIDADE DO CLIENTE;                   
                    }

                    oClientePreferencial.setId(rst.getInt("CODCLIE"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("CODCLIE"));
                    oClientePreferencial.setNome(rst.getString("RAZAO"));
                    oClientePreferencial.setEndereco(rst.getString("ENDERECO"));
                    oClientePreferencial.setBairro(rst.getString("BAIRRO"));
                    oClientePreferencial.setNumero(rst.getString("NUMERO"));

                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;

                    if ((rst.getString("CEP") != null) && (!rst.getString("CEP").trim().isEmpty())) {
                        oClientePreferencial.setCep(rst.getString("CEP"));
                    } else {
                        oClientePreferencial.setCep(Parametros.get().getCepPadrao());
                    }                    
                    
                    oClientePreferencial.setId_tipoinscricao(rst.getInt("PESSOA"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("RG"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("ESTADOCIVILNOVO"));
                    oClientePreferencial.setCnpj(rst.getString("CNPJ_CPF") == null ? "-1" : rst.getString("CNPJ_CPF"));
                    oClientePreferencial.setSexo(rst.getInt("SEXO"));
                    oClientePreferencial.setDataresidencia("2005-01-01");
                    oClientePreferencial.setDatacadastro(rst.getString("DTCAD"));
                    oClientePreferencial.setDatanascimento(rst.getString("DTANIVER"));
                    oClientePreferencial.setTelefone(rst.getString("TELEFONE"));
                    oClientePreferencial.setCelular(rst.getString("CELULAR"));
                    oClientePreferencial.setNomeconjuge(rst.getString("NOMECONJUGE"));
                    oClientePreferencial.setCargoconjuge(rst.getString("CARGOCONJUGE"));
                    oClientePreferencial.setRgconjuge(rst.getString("RG_CONJUGE"));
                    oClientePreferencial.setSalarioconjuge(rst.getDouble("RENDACONJUGE"));
                    oClientePreferencial.setEmail(rst.getString("EMAIL"));
                    oClientePreferencial.setValorlimite(rst.getDouble("LIMITECRED"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("FONE_EMP"));
                    oClientePreferencial.setEmpresa(rst.getString("EMPRESA"));
                    oClientePreferencial.setSalario(rst.getDouble("RENDA"));
                    oClientePreferencial.setNomepai(rst.getString("NOMEPAI"));
                    oClientePreferencial.setNomemae(rst.getString("NOMEMAE"));
                    oClientePreferencial.setCargo(rst.getString("CARGO"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("ATIVO"));
                    oClientePreferencial.setObservacao(rst.getString("OBS"));
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteGetWay(int id_loja, int id_lojaCliente,
            int tipoDocumento) throws Exception {

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
            if (tipoDocumento > 0) {
                sql.append(" and RECEBER.CODTIPODOCUMENTO = ").append(tipoDocumento);    
            }            
            sql.append("  and RECEBER.CODLOJA = " + id_lojaCliente);
            sql.append(" order by DTEMISSAO            ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("CODCLIE");
                dataemissao = rst.getString("DTEMISSAO").replace("-", "/").substring(0, 10);
                datavencimento = rst.getString("DTVENCTO").replace("-", "/").substring(0, 10);
                
                if ((rst.getString("NOTAECF") != null) &&
                        (!rst.getString("NOTAECF").trim().isEmpty())) {
                    if (rst.getString("NOTAECF").length() > 9) {
                        numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF").substring(0, 9)));
                    } else {
                        numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF").trim()));
                    }
                } else {
                    numerocupom = 0;
                }
                
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

    public List<ReceberChequeVO> carregarReceberChequeGetWay(int id_loja, int id_lojaCliente, int codCheque) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao;
        double valor, juros;
        String observacao = null, dataemissao, datavencimento, agencia, conta,
                telefone, rg, nome;
        long cnpj;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT CLIENTES.CNPJ_CPF, CLIENTES.RAZAO, CLIENTES.RG, CODRECEBER, NUMTIT, ");
            sql.append("       RECEBER.CODCLIE, RECEBER.NUMCHEQUE, RECEBER.CODTIPODOCUMENTO, ");
            sql.append("       NOTAECF, DTVENCTO, DTEMISSAO, DTPAGTO, coalesce(VALOR,0) VALOR, ");
            sql.append("       coalesce(VALORJUROS,0) VALORJUROS, OBS, CLIENTES.TELEFONE, ");
            sql.append("       RECEBER.CODBANCO, RECEBER.AGENCIA, RECEBER.CONTACORR ");
            sql.append("  FROM RECEBER ");
            sql.append(" INNER JOIN CLIENTES ON ");
            sql.append(" CLIENTES.CODCLIE = RECEBER.CODCLIE ");
            sql.append(" where UPPER(SITUACAO) = 'AB' ");
            sql.append("   and RECEBER.CODLOJA = " + id_lojaCliente + " ");
            sql.append("   and RECEBER.CODTIPODOCUMENTO IN ("+codCheque+")");
            sql.append(" order by DTEMISSAO           ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                dataemissao = rst.getString("DTEMISSAO").replace("-", "/").substring(0, 10);
                datavencimento = rst.getString("DTVENCTO").replace("-", "/").substring(0, 10);
                if ((rst.getString("NOTAECF") != null) &&
                        (!rst.getString("NOTAECF").trim().isEmpty())) {
                    if (rst.getString("NOTAECF").length() > 9) {
                        numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF").substring(0, 9)));
                        observacao = "NOTAECF: " + Utils.formataNumero(rst.getString("NOTAECF"));
                    } else {
                        numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF").trim()));
                    }
                } else {
                    numerocupom = 0;
                }
                valor = Double.parseDouble(rst.getString("VALOR"));
                juros = Double.parseDouble(rst.getString("VALORJUROS"));

                if ((rst.getString("OBS") != null)
                        && (!rst.getString("OBS").isEmpty())) {
                    observacao = observacao + " - " + Utils.acertarTexto(rst.getString("OBS").replace("'", ""));
                } else {
                    observacao = observacao + " - " + "IMPORTADO VR";
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
                    
                    if (Utils.formataNumero(rst.getString("CODBANCO").trim()).length() > 9) {
                        idBanco = Utils.retornarBanco(Integer.parseInt(rst.getString("CODBANCO").trim().substring(0, 8)));
                    } else {
                        idBanco = Utils.retornarBanco(Integer.parseInt(rst.getString("CODBANCO").trim()));
                    }
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
                
                if (observacao.length() > 500) {
                    observacao = observacao.substring(0, 500);
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
                oReceberCheque.id_tipoalinea = (rst.getInt("CODTIPODOCUMENTO") == 5 ? 11 : 0);

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
                cidade, datacadastro, fax, telefone2, celular;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        boolean ativo = true;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append(" SELECT CODFORNEC, RAZAO, FANTASIA, ENDERECO, NUMERO, BAIRRO, CIDADE, ESTADO, CEP, ");
            sql.append(" TELEFONE, FAX, EMAIL, CELULAR, FONE1, CONTATO, IE, CNPJ_CPF, AGENCIA,BANCO, ");
            sql.append(" CONTA,  DTCAD, VALOR_COMPRA, ATIVO, OBS ");
            sql.append(" FROM FORNECEDORES  order by codfornec ");
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("CODFORNEC");

                    Linha++;

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
                        cnpj = -1;
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
                        cep = Parametros.get().getCepPadrao();
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
                                id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                            }
                        }
                    } else {
                        id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                    }

                    if ((rst.getString("ESTADO") != null)
                            && (!rst.getString("ESTADO").isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("ESTADO").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId();
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId();
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
                    
                    if ((rst.getString("CELULAR") != null) &&
                            (!rst.getString("CELULAR").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("CELULAR").trim());
                    } else {
                        celular = "";
                    }
                    
                    if ((rst.getString("FAX") != null) &&
                            (!rst.getString("FAX").trim().isEmpty())) {
                        fax = Utils.formataNumero(rst.getString("FAX").trim());
                    } else {
                        fax = "";
                    }

                    if ((rst.getString("FONE1") != null) &&
                            (!rst.getString("FONE1").trim().isEmpty())) {
                        telefone2 = Utils.formataNumero(rst.getString("FONE1").trim());
                    } else {
                        telefone2 = "";
                    }
                    
                    if (rst.getString("OBS") != null) {
                        obs = rst.getString("OBS").trim();
                    } else {
                        obs = "";
                    }

                    if (rst.getString("DTCAD") != null) {
                        datacadastro = rst.getString("DTCAD").substring(0, 10).replace("-", "/");
                    } else {
                        datacadastro = "";
                    }

                    if ((rst.getString("ATIVO") != null) && 
                            (!rst.getString("ATIVO").trim().isEmpty())) {
                        if ("S".equals(rst.getString("ATIVO").trim())) {
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

                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.setNumero(rst.getString("NUMERO"));
                    oFornecedor.telefone = Telefone;
                    oFornecedor.email = email;
                    oFornecedor.bairro = bairro;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.datacadastroStr = datacadastro;
                    oFornecedor.fax = fax;
                    oFornecedor.telefone2 = telefone2;
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

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorGetWay(boolean unificacao) throws Exception {
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
            sql.append("select PF.CODFORNEC, PF.CODPROD, PF.CODREF, PF.QTD_EMB, PF.DATAREF, ");
            sql.append("P.BARRA, f.CNPJ_CPF ");
            sql.append("from PRODREF PF ");
            sql.append("inner join PRODUTOS P on P.CODPROD = PF.CODPROD ");
            sql.append("inner join FORNECEDORES F on f.CODFORNEC = PF.CODFORNEC");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                strDataAlteracao = "";
                idFornecedor = 0;
                idProduto = 0;
                
                if (unificacao) {                    
                    if ((rst.getString("BARRA") != null) && (!rst.getString("BARRA").trim().isEmpty()) &&
                            (rst.getString("BARRA").trim().length() >= 7)) {
                        idProduto = new ProdutoDAO().getIdProdutoCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim())));
                    }
                    
                    if ((rst.getString("CNPJ_CPF") != null) && (!rst.getString("CNPJ_CPF").trim().isEmpty()) &&
                            (rst.getString("CNPJ_CPF").trim().length() >= 9)) {
                        idFornecedor = new FornecedorDAO().getId(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim())));
                    }
                    
                } else {                
                    idFornecedor = rst.getInt("CODFORNEC");
                    idProduto = rst.getInt("CODPROD");
                }

                if ((rst.getString("CODREF") != null)
                        && (!rst.getString("CODREF").isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("CODREF").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }
                
                if ((rst.getString("QTD_EMB") != null) &&
                        (!rst.getString("QTD_EMB").trim().isEmpty())) {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("QTD_EMB").trim());
                } else {
                    qtdEmbalagem = 1;
                }

                if ((rst.getString("DATAREF") != null) &&
                        (!rst.getString("DATAREF").trim().isEmpty())) {
                    strDataAlteracao = rst.getString("DATAREF").trim().replace("-", "/").substring(0, 10);
                    dataAlteracao = new java.sql.Date(fmt.parse(strDataAlteracao).getTime());
                } else {
                    dataAlteracao = new Date(new java.util.Date().getTime());
                }
                
                if ((idProduto > 0) && (idFornecedor > 0)) {
                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    oProdutoFornecedor.id_fornecedor = idFornecedor;
                    oProdutoFornecedor.id_produto = idProduto;
                    oProdutoFornecedor.dataalteracao = dataAlteracao;
                    oProdutoFornecedor.codigoexterno = codigoExterno;
                    oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                    vProdutoFornecedor.add(oProdutoFornecedor);
                }
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
            sql.append("select P.CODPROD, ");
            sql.append("P.CODALIQ ALIQ_DEBITO, A.VALORTRIB VAL_DEBITO, ");
            sql.append("P.CODTRIB CST_SAIDA, P.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, ");
            sql.append("A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, P.PER_REDUC REDUCAO_DEBITO_FORA_ESTADO, ");
            sql.append("P.CODTRIB_ENT CST_ENTRADA, P.PER_REDUC_ENT REDUCAO_CREDITO, ");
            sql.append("P.ULTICMSCRED ");
            sql.append("from PRODUTOS P ");
            sql.append("inner join ALIQUOTA_ICMS A on A.CODALIQ = P.CODALIQ ");
            sql.append("INNER JOIN ALIQUOTA_ICMS A2 ON A2.CODALIQ = P.CODALIQ_NF ");
            
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
                EstadoVO uf = Parametros.get().getUfPadrao();
                oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO"), false));
                oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, valIcmsCredito, rst.getDouble("REDUCAO_CREDITO"), false));
                oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO_FORA_ESTADO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), false));
                oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("VAL_DEBITO"), rst.getDouble("REDUCAO_DEBITO_FORA_ESTADO"), true));
                oProduto.vAliquota.add(oAliquota);                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.ref_icmsdebito = rst.getString("ALIQ_DEBITO");
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
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

    public void importarFamiliaProdutoIntegracao() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto Integração...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutGetWay();
            FamiliaProdutoDAO familiaProduto = new FamiliaProdutoDAO();
            familiaProduto.gerarCodigo = true;
            familiaProduto.salvar(vFamiliaProduto);
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
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

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
    
    public void importarCustoProdutoGetWay(int id_loja, int id_lojaCliente, boolean produtosIguais) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoGetWay(id_loja, id_lojaCliente, produtosIguais);
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

    public void importarCustoProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente, boolean produtosIguais) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Custo do Produto Loja " +id_loja+ "...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoGetWay(id_loja, id_lojaCliente, produtosIguais);
            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {
                ProdutoVO oProduto = vCustoProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            if (produtosIguais) {
                produto.alterarCustoProduto(vProdutoNovo, id_loja);
            } else {
                produto.alterarCustoProdutoIntegracao(vProdutoNovo, id_loja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarPrecoProdutoGetWay(int id_loja, int id_lojaCliente, boolean produtosIguais) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoGetWay(id_loja, id_lojaCliente, produtosIguais);
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

    public void importarPrecoProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente, 
            boolean produtosIguais) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Produto "+id_loja+"...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoGetWay(id_loja, id_lojaCliente, 
                    produtosIguais);
            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {
                ProdutoVO oProduto = vPrecoProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            
            if (produtosIguais) {
                produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            } else {
                produto.alterarPrecoProdutoIntegracao(vProdutoNovo, id_loja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarMargemProdutoGetWay(int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarMargemProdutoGetWay(id_loja);

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

    public void importarEstoqueProdutoGetWay(int id_loja, int id_lojaCliente, boolean somarEstoque) throws Exception {
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

            if (somarEstoque) {
                produto.alterarEstoqueSomaProduto(vProdutoNovo, id_loja);
            } else {
                produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            }

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProdutoIntegracaoGetWay(int id_loja, int id_lojaCliente,
            boolean somarEstoque) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto "+id_loja+"...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoGetWay(id_loja, id_lojaCliente);
            ProgressBar.setMaximum(vEstoqueProduto.size());
            for (Integer keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            if (somarEstoque) {
                produto.alterarSomaEstoqueProdutoIntegracao(vProdutoNovo, id_loja);
            } else {
                produto.alterarEstoqueProdutoIntegracao(vProdutoNovo, id_loja);
            }
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

    public void importarCodigoBarraAlternativoGetWay(int id_loja, boolean unificacao) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarrasAlternativoGetWay(unificacao);
            ProgressBar.setMaximum(vEstoqueProduto.size());
            for (Long keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = false;
            produto.verificarLoja = true;
            produto.id_loja = id_loja;
            
            if (unificacao) {
                produto.addCodigoBarrasUnificacao(vProdutoNovo);
            } else {
                produto.addCodigoBarras(vProdutoNovo);
            }
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

    public void importarFornecedorCnpjGetWay(int idLoja) throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...Cnpj");
            vFornecedor = carregarFornecedorGetWay();
            FornecedorDAO fornecedor = new FornecedorDAO();
            fornecedor.pidLoja = idLoja;
            fornecedor.salvarCnpj(vFornecedor);
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
    
    public void importarReceberClienteGetWay(int idLoja, int idLojaCliente,
            int tipoDocumento) throws Exception {

        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente,
                    tipoDocumento);
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
    
    public void importarReceberChequeGetWay(int idLoja, int idLojaCliente, int codCheque) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cheque...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeGetWay(idLoja, idLojaCliente, codCheque);
            new ReceberChequeDAO().salvar(vReceberCheque, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberChequeComCondicaoGetWay(int idLoja, int idLojaCliente, int codCheque) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cheque Com Condição...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeGetWay(idLoja, idLojaCliente, codCheque);
            new ReceberChequeDAO().salvarComCondicao(vReceberCheque, idLoja);
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

    public void importarReceberClienteComCpfGetWay(int idLoja, int idLojaCliente, int idTipoDocumento) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente com Cpf...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente,
                    idTipoDocumento);
            new ReceberCreditoRotativoDAO().salvarComCnpj(vReceberCliente, idLoja);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteCondicaoGetWay(int idLoja, int idLojaCliente, int tipoDocumento) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente com Condição...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGetWay(idLoja, idLojaCliente, tipoDocumento);
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

    public void importarProdutoFornecedor(boolean unificacao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorGetWay(unificacao);
            
            if (unificacao) {
                new ProdutoFornecedorDAO().salvarUnificacao(vProdutoFornecedor);
            } else {
                new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
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

    public void importarFamiliaProdutoProdutoIntegracao() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto Integração...");
            vProduto = carregarFamiliaProdutoIntegracaoGetWay();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarFamiliaProdutoIntegracao(vProduto);
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

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODTIPODOCUMENTO, DESCRICAO from TIPODOCUMENTO order by CODTIPODOCUMENTO"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("CODTIPODOCUMENTO"), 
                            rst.getString("CODTIPODOCUMENTO") + " - " + 
                            rst.getString("DESCRICAO")));
                }
            }
        }
        
        return result;
    }
    
    
    
    public void importarProdutoManterBalanca(int idLojaVR, int idLojaCliente) throws Exception {

        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");
      
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarProdutoGetWay().values()) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: aux.values()) {
            if (prod.eBalanca) {
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);
        
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }
}
