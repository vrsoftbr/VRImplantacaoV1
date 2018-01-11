package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
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
import vrimplantacao.gui.interfaces.classes.LojaClienteVO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

public class MilenioDAO {

    List<DocumentoMilenio> docs;
    public static boolean utilizaREFPLU = false;
    
    public List<DocumentoMilenio> getDocs() {
        if (docs == null) {
            recarregarTipoDocumento();
        }
        return docs;
    }

    public void recarregarTipoDocumento() {
        docs = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select TIPDOCCOD, TIPDOCDESCTB from TIPO_DOCUMENTO order by TIPDOCCOD"
            )) {
                while (rst.next()) {
                    DocumentoMilenio doc = new DocumentoMilenio();
                    doc.id = rst.getString("TIPDOCCOD");
                    doc.descricao = rst.getString("TIPDOCDESCTB");
                    docs.add(doc);
                }
            }
        } catch (SQLException ex) {
            Util.exibirMensagemErro(ex, null);
            ex.printStackTrace();
        }
    }

    public void importarCreditoRotativoSelecionado(int idLojaVR, int idLojaCliente) throws Exception {
        String mensagem = "Importando os seguintes recebimentos para o rotativo... \n";
        List<DocumentoMilenio> selecionados = new ArrayList<>();
        for (DocumentoMilenio doc: getDocs()) {
            if (doc.selecionado) {
                selecionados.add(doc);
                mensagem += "'" + doc.id + "' ";                
            }
        }
        ProgressBar.setStatus(mensagem);
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberClienteMilenio(idLojaCliente, selecionados);
        new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCreditoRotativo, idLojaVR);        
    }

    public void importarFornecedorComoPreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando os fornecedores como clientes...");
        List<ClientePreferencialVO> clientes = carregarFornecedorComoCliente(idLojaCliente);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.naoGravarCpfCnpjRepetidos = true;
        dao.salvar(clientes, idLojaVR, idLojaCliente);
    }
    
    private List<ProdutoVO> carregarIcmsLoja(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> result = new ArrayList();
        String ufEmpresa = "CE";
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select LOJEST from loja WHERE LOJCOD = " + idLojaCliente
            )) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("LOJEST");
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	trib.procod,\n" +
                    "	trib.trbid,\n" +
                    "	trib.treest,\n" +
                    "	ref.REFPLU,\n" +
                    "	ref.REFPLUDV, \n" +
                    "	ref.REFCODINT \n" +
                    "from \n" +
                    "	TRBESTADUAL trib \n" +
                    "	join referencia ref on trib.PROCOD = ref.PROCOD\n" +
                    "where treest = " + Utils.quoteSQL(ufEmpresa)
            )) {
                
                while (rst.next()) {
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();  
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior); 
                    oProduto.getvAliquota().add(oAliquota);
                    
                    //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                    String strId;
                    if (!this.utilizarREFCODINT) {
                        strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                    } else {
                        strId = Utils.formataNumero(rst.getString("REFCODINT"));
                        if (strId.equals("0")) {
                            strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                        }
                    }
                    long id = Long.parseLong(strId);
                    //</editor-fold>
                    
                    oProduto.idDouble = id;

                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO ICMS">
                    String tribAliquota;
                    if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                        tribAliquota = rst.getString("TRBID").trim();
                    } else {
                        tribAliquota = "999";
                    }
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(23);}
                    oAliquota.setIdAliquotaDebito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCredito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCreditoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(retornarAliquotaICMSMilenio(tribAliquota));
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="CODIGO ANTERIOR">
                    oCodigoAnterior.setCodigoanterior(id);                   
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("TRBID"));
                    //</editor-fold>
                    
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarIcmsLoja(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...ICMS.....");
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for(ProdutoVO prod: carregarIcmsLoja(idLojaVR, idLojaCliente)) {
            auxiliar.put(prod.idDouble, prod);
        }
        
        produto.incluirICMSLoja(new ArrayList(auxiliar.values()));
    }
    
    public static class DocumentoMilenio {
        
        public String id = "";
        public String descricao = "";
        public boolean selecionado = false;
        
    }
    
    /**
     * Esta variável define se será utilizado o código de integração gravado no Milênio ou o id dos produtos.
     */
    private boolean utilizarREFCODINT = false;
    
    public List<LojaClienteVO> getLojasDoCliente() throws SQLException{
        List<LojaClienteVO> lojas = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cast(lojcod as integer) id, LOJRAZ, LOJFAN, LOJCGC, LOJEST from loja order by id"
            )) {
                while (rst.next()) {
                    lojas.add(new LojaClienteVO(rst.getInt("id"), rst.getString("LOJFAN") + " - " + rst.getString("LOJCGC")));
                }
            }
        }   
        return lojas;     
    }

    public List<FamiliaProdutoVO> carregarFamiliaProdutoMilenio() throws Exception {

        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cast(PROCOD as integer) id, prodes descricao from produto where procod in\n" +
                    "(select procod from referencia group by PROCOD having COUNT(*) > 1)\n" +
                    "order by id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                    oFamiliaProduto.setId(rst.getInt("id"));
                    oFamiliaProduto.setDescricao(rst.getString("descricao"));
                    oFamiliaProduto.setId_situacaocadastro(1); 
                    oFamiliaProduto.setCodigoant(rst.getInt("id"));
                    oFamiliaProduto.setIdLong(rst.getLong("id"));

                    vFamiliaProduto.add(oFamiliaProduto);
                }            
            }            
        }
        
        return vFamiliaProduto;
    }

    public List<MercadologicoVO> carregarMercadologicoMilenio(int nivel) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT A.SECCOD, A.SECDES, B.GRPCOD, B.GRPDES, C.SBGCOD, C.SBGDES ");
            sql.append("FROM [SysacME].[dbo].[SECAO] A ");
            sql.append("INNER JOIN [SysacME].[dbo].[GRUPO] B ON B.SECCOD = A.SECCOD ");
            sql.append("INNER JOIN [SysacME].[dbo].[SUBGRUPO] C ON C.SECCOD = A.SECCOD AND C.GRPCOD = B.GRPCOD ");
            sql.append("ORDER BY A.SECCOD, B.GRPCOD, C.SBGCOD ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    descricao = util.acertarTexto(rst.getString("SECDES").replace("'", ""));

                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("SECCOD");
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {

                    descricao = util.acertarTexto(rst.getString("GRPDES").replace("'", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("SECCOD");
                    oMercadologico.mercadologico2 = rst.getInt("GRPCOD");
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {

                    descricao = util.acertarTexto(rst.getString("SBGDES").replace("'", ""));

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("SECCOD");
                    oMercadologico.mercadologico2 = rst.getInt("GRPCOD");
                    oMercadologico.mercadologico3 = rst.getInt("SBGCOD");
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
    
    /*
    public Map<Integer, ProdutoVO> carregarProduto6DigitosMilenio(int idLojaVR, int idLojaCliente, int balanca) throws SQLException, Exception {

        StringBuilder sql = null;
        Statement stm = null, stmPostgres;
        ResultSet rst = null, rst2 = null, rst3 = null;
        Utils util = new Utils();
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();

        long id;
        int mercadologico1, mercadologico2, mercadologico3, ncm1 = 0, ncm2 = 0, ncm3 = 0,
                id_familiaproduto, codigoBalanca, id_tipoEmbalagem, validade, referencia,
                qtdEmbalagem = 1;
        String tribAliquota, ncmAtual;
        String dataCadastro;
        double margem=0, custo=0, custoSemImposto, precoVenda;
        long codigobarras;
        boolean eBalanca, pesavel;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT R.*, P.*, E.*, PR.*, C.CSTREP, coalesce(PROMRG1,0) as PROMRG1 FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD ");
            sql.append("LEFT JOIN  [SysacME].[dbo].EAN E ON E.REFPLU = R.REFPLU ");
            sql.append("LEFT JOIN [SysacME].[dbo].[PRECO] PR ON PR.PROCOD = R.PROCOD ");
            sql.append("LEFT JOIN [SysacME].[dbo].[CUSTO] C ON C.REFPLU = E.REFPLU   ");                        
            sql.append("WHERE R.REFPLU <= 99999 ");
            sql.append("AND PR.LOJCOD = " + idLojaCliente);
            sql.append(" ORDER BY r.REFDES asc ");
            
            rst = stm.executeQuery(sql.toString());

            stmPostgres = Conexao.createStatement();
                
            while (rst.next()) {

                eBalanca = false;
                codigoBalanca = 0;
                referencia = Integer.parseInt(rst.getString("REFPLU"));
                id_tipoEmbalagem = 4;
                validade = 0;
                pesavel = false;

                ProdutoVO oProduto = new ProdutoVO();

               
                
                if (!this.utilizarREFCODINT) {
                    id = Integer.parseInt(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                } else {
                    if (!Utils.formataNumero("REFCODINT").equals("")) {
                        id = Long.parseLong(Utils.formataNumero(rst.getString("REFCODINT")));
                    } else {
                        id = -1;
                    }
                }

                //TODO Fazer a verificação do produto de balanca utilizando um map
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                
                if (balanca==1){
                    sql.append("where codigo = ").append(rst.getString("REFPLU"));
                }else if (balanca==2){
                    sql.append("where codigo = ").append(rst.getString("REFPLU")).append(rst.getString("REFPLUDV"));
                }else if (balanca==3) {
                    sql.append("where codigo = ").append(Utils.formataNumero(rst.getString("REFCODINT")));
                }             

                rst2 = stmPostgres.executeQuery(sql.toString());

                if (rst2.next()) {
                    eBalanca = true;
                    codigoBalanca = rst2.getInt("codigo");
                    validade = rst2.getInt("validade");
                    if ("P".equals(rst2.getString("pesavel"))) {
                        id_tipoEmbalagem = 4;
                        pesavel = false;
                    } else {
                        id_tipoEmbalagem = 0;
                        pesavel = true;
                    }
                } else {
                    eBalanca = false;
                    codigoBalanca = 0;
                    validade = 0;
                    pesavel = false;
                    if ("CX".equals(rst.getString("PROUNDVDA").trim())) {                        
                        id_tipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("PROUNDVDA").trim())) {
                        id_tipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("PROUNDVDA").trim())) {
                        id_tipoEmbalagem = 0;
                    } else {
                        id_tipoEmbalagem = 0;
                    }
                }

                qtdEmbalagem = (int) rst.getDouble("PROQTDUNDVDA");

                if (rst.getString("REFDATCAD") != null) {
                    dataCadastro = Util.formatDataGUI(rst.getDate("REFDATCAD"));
                } else {
                    dataCadastro = Util.formatDataGUI(new Date(new java.util.Date().getTime()));
                }
                
                if ((rst.getString("SECCOD") != null)
                        && (!rst.getString("SECCOD").isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("SECCOD"));
                } else {
                    mercadologico1 = 0;
                }

                if ((rst.getString("GRPCOD") != null)
                        && (!rst.getString("GRPCOD").isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("GRPCOD"));
                } else {
                    mercadologico2 = 0;
                }

                if ((rst.getString("SBGCOD") != null)
                        && (!rst.getString("SBGCOD").isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("SBGCOD"));
                } else {
                    mercadologico3 = 0;
                }

                if ((rst.getString("PRONCM") != null)
                        && (!rst.getString("PRONCM").isEmpty())
                        && (rst.getString("PRONCM").trim().length() > 5)) {

                    ncmAtual = rst.getString("PRONCM").trim();

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                    //ncm1 = 9701;
                    //ncm2 = 90;
                    //ncm3 = 0;
                }

                if ((rst.getString("PROCOD") != null)
                        && (!rst.getString("PROCOD").isEmpty())) {
                    id_familiaproduto = Integer.parseInt(rst.getString("PROCOD"));
                } else {
                    id_familiaproduto = -1;
                }

                if ((rst.getString("CSTREP") != null)
                        && (!"".equals(rst.getString("CSTREP")))) {
                    custo = rst.getDouble("CSTREP");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("PRCVDA1") != null)
                        && (!"".equals(rst.getString("PRCVDA1")))) {
                    precoVenda = rst.getDouble("PRCVDA1");
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("PROMRG1") != null)
                        && (!"".equals(rst.getString("PROMRG1")))) {
                   if (rst.getDouble("PROMRG1")>0) {
                       margem=rst.getDouble("PROMRG1");                   
                   }else{
                        if((custo>0) && (precoVenda>0)){
                            margem=((custo/precoVenda)*100);                    
                        }else{
                            margem=0;
                        }                       
                   }                                        
                }else if((custo>0) && (precoVenda>0)){
                   margem=((custo/precoVenda)*100);                    
                }
                
                if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                    tribAliquota = rst.getString("TRBID").trim();
                } else {
                    tribAliquota = "999";
                }

                if (eBalanca) {
                    codigobarras = Long.parseLong(String.valueOf(id));
                } else {
                    codigobarras = -2;// não importar produtoautomacao codigo barras
                }

                oProduto.idDouble = id;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.setDescricaoCompleta(rst.getString("REFDES"));
                oProduto.setDescricaoReduzida(rst.getString("REFDESRDZ"));
                oProduto.reajustarDescricoes();
                oProduto.dataCadastro = String.valueOf(dataCadastro);
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = id_familiaproduto;
                oProduto.margem = margem;
                oProduto.qtdEmbalagem = 1;
                oProduto.idTipoEmbalagem = id_tipoEmbalagem;
                oProduto.idComprador = 1;
                oProduto.idFornecedorFabricante = 1;
                oProduto.pesavel = pesavel;
                oProduto.validade = validade;
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
                oProduto.idTipoPisCofinsDebito = 1;
                oProduto.idTipoPisCofinsCredito = 13;
                oProduto.tipoNaturezaReceita = 999;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.idLoja = idLojaVR;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 23;
                oAliquota.idAliquotaDebito = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaCredito = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstado = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaCreditoForaEstado = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstadoNF = retornarAliquotaICMSMilenio(tribAliquota);

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.idTipoEmbalagem = id_tipoEmbalagem;
                oAutomacao.qtdEmbalagem = 1;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = id;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.barras = codigobarras;
                oCodigoAnterior.referencia = referencia;
                if ((rst.getString("PRONCM") != null)
                        && (!rst.getString("PRONCM").isEmpty())) {

                    oCodigoAnterior.ncm = rst.getString("PRONCM");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                oCodigoAnterior.id_loja = idLojaVR;

                if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {

                    oCodigoAnterior.ref_icmsdebito = rst.getString("TRBID");
                }
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put((int) id, oProduto);
            }
            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }   
    */

    private List<ProdutoVO> carregarProdutos(int idLojaVR, int idLojaCliente, int balanca, OpcaoProdutoSQLQuery opcao) throws Exception {
        List<ProdutoVO> result = new ArrayList();
        String ufEmpresa = "CE";
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select LOJEST from loja WHERE LOJCOD = " + idLojaCliente
            )) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("LOJEST");
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    opcao.getSQL(idLojaVR, idLojaCliente, utilizarREFCODINT)
            )) {                
                int cont = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();  
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior); 
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvAliquota().add(oAliquota);
                    
                    //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                    String strId;
                    if (!this.utilizarREFCODINT) {
                        strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                    } else {
                        strId = Utils.formataNumero(rst.getString("REFCODINT"));
                        if (strId.equals("0")) {
                            strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                        }
                    }
                    long id = Long.parseLong(strId);
                    //</editor-fold>
                    
                    oProduto.idDouble = id;
                    oProduto.setDescricaoCompleta(rst.getString("REFDES"));
                    oProduto.setDescricaoReduzida(rst.getString("REFDESRDZ"));
                    oProduto.reajustarDescricoes();
                    oProduto.setIdSituacaoCadastro(rst.getInt("status"));
                    if (rst.getString("REFDATCAD") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("REFDATCAD")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }
                    
                    oProduto.setMercadologico1(rst.getInt("SECCOD"));
                    oProduto.setMercadologico2(rst.getInt("GRPCOD"));
                    oProduto.setMercadologico3(rst.getInt("SBGCOD"));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("PRONCM") != null)
                            && (!rst.getString("PRONCM").isEmpty())
                            && (rst.getString("PRONCM").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("PRONCM").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    //MILÊNIO NÃO TEM CEST - ATÉ O MOMENTO NÃO
                    
                    oProduto.setIdFamiliaProduto(rst.getString("PROCOD") != null ? rst.getInt("PROCOD") : -1);
                    oProduto.setMargem(rst.getDouble("PROMRG1"));
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    //<editor-fold defaultstate="collapsed" desc="PRODUTOS DE BALANÇA E EMBALAGEM">
                    //Tratando o id da balança.
                    long codigoBarra;
                    switch(balanca) {
                        case 1: codigoBarra = rst.getLong("REFPLU"); break;
                        case 2: codigoBarra = Long.parseLong(rst.getString("REFPLU") + rst.getString("REFPLUDV")); break;
                        case 3: { 
                                    String aux = Utils.formataNumero(rst.getString("REFCODINT"));
                                    codigoBarra = aux.equals("0") ? -2 : Long.parseLong(aux);
                                }; break;
                        default: codigoBarra = -2; break;
                    }
                    
                    ProdutoBalancaVO produtoBalanca = null;                    
                    if (codigoBarra > 0 && codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    }
                    //Se um produto de balança foi loacalizado
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras(-1);                          
                        oProduto.setValidade(produtoBalanca.getValidade() >= 1 ? produtoBalanca.getValidade() : rst.getInt("PROVAL"));
                        
                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }
                        
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oProduto.setValidade(rst.getInt("PROVAL"));
                        oProduto.setPesavel(false); 
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("PROUNDVDA")));
                        
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem()); 
                    //</editor-fold>
                    
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);   
                    oProduto.setPesoBruto(rst.getDouble("PROPESBRTVDA"));
                    oProduto.setPesoLiquido(rst.getDouble("PROPESLIQVDA"));
                    
                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO PIS/CONFINS">
                    oProduto.setIdTipoPisCofinsDebito(13);
                    oProduto.setIdTipoPisCofinsCredito(1);
                    oProduto.setTipoNaturezaReceita(-1);
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="COMPLEMENTO DO PRODUTO">
                    oComplemento.setPrecoVenda(rst.getDouble("PRCVDA1"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("PRCVDA1"));
                    oComplemento.setCustoComImposto(rst.getDouble("CSTREP"));
                    oComplemento.setCustoSemImposto(rst.getDouble("CSTREP"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(rst.getInt("status"));
                    //O ajuste do estoque é feito em outro método.
                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);
                    //</editor-fold>                    
                    
                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO ICMS">
                    String tribAliquota;
                    if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                        tribAliquota = rst.getString("TRBID").trim();
                    } else {
                        tribAliquota = "999";
                    }
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(23);}
                    oAliquota.setIdAliquotaDebito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCredito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCreditoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(retornarAliquotaICMSMilenio(tribAliquota));
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="CODIGO ANTERIOR">
                    oCodigoAnterior.setCodigoanterior(id);
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Long.parseLong(
                            Utils.formataNumero(rst.getString("EANCOD")).equals("0") ?
                            "-2" :
                            Utils.formataNumero(rst.getString("EANCOD"))
                    ));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("PRONCM"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(13);
                    oCodigoAnterior.setPiscofinscredito(1);
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("TRBID")); 
                    
                    oCodigoAnterior.setCodigoAuxiliar(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                    //</editor-fold>
                    
                    result.add(oProduto);
                    
                    ProgressBar.setStatus("Carregando dados...Produtos..." + cont);
                    cont++;
                }
            }
        }
        return result;
    }
    
    /*
    public Map<Integer, ProdutoVO> carregarProdutoMaior6DigitosMilenio(int id_loja, int id_lojaDestino, int balanca) throws SQLException, Exception {

        StringBuilder sql = null;
        Statement stm = null, stmPostgres;
        ResultSet rst = null, rst2 = null, rst3 = null;
        Utils util = new Utils();
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();

        int id, mercadologico1, mercadologico2, mercadologico3, ncm1 = 0, ncm2 = 0, ncm3 = 0,
                id_familiaproduto, codigoBalanca, id_tipoEmbalagem, validade, referencia,
                qtdEmbalagem;
        String descricaocompleta, descricaoreduzida, descricaogondola, tribAliquota, ncmAtual = null;
        String dataCadastro;
        long codigobarras = -2;
        double margem=0, precoVenda=0,custo=0;
        boolean eBalanca, pesavel;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT * FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD ");
            sql.append("LEFT JOIN  [SysacME].[dbo].EAN E ON E.REFPLU  = R.REFPLU ");
            sql.append("LEFT JOIN [SysacME].[dbo].[PRECO] PR ON PR.PROCOD = R.PROCOD ");
            sql.append("LEFT JOIN [SysacME].[dbo].[CUSTO] C ON C.REFPLU = E.REFPLU   ");                                    
            sql.append("WHERE R.REFPLU > 99999 ");
            sql.append("ORDER BY r.REFDES asc ");

            rst = stm.executeQuery(sql.toString());

            stmPostgres = Conexao.createStatement();

            while (rst.next()) {
                ProdutoVO oProduto = new ProdutoVO();

                id = Integer.parseInt(rst.getString("REFPLU") + rst.getString("REFPLUDV"));

                referencia = Integer.parseInt(rst.getString("REFPLU"));

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                
                if (balanca==1){
                    sql.append("where codigo = " + rst.getString("REFPLU"));
                } else if (balanca==2){
                    sql.append("where codigo = " + rst.getString("REFPLU")+rst.getString("REFPLUDV"));                    
                } else if (balanca==3) {
                    sql.append("where codigo = " + rst.getString("REFCODINT"));
                }            

                rst2 = stmPostgres.executeQuery(sql.toString());

                if (rst2.next()) {

                    eBalanca = true;
                    codigoBalanca = rst2.getInt("codigo");
                    validade = rst2.getInt("validade");
                    if ("P".equals(rst2.getString("pesavel"))) {
                        id_tipoEmbalagem = 4;
                        pesavel = false;
                    } else {
                        id_tipoEmbalagem = 0;
                        pesavel = true;
                    }
                } else {
                    eBalanca = false;
                    codigoBalanca = 0;
                    validade = 0;
                    pesavel = false;
                    if ("CX".equals(rst.getString("PROUNDVDA").trim())) {                        
                        id_tipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("PROUNDVDA").trim())) {
                        id_tipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("PROUNDVDA").trim())) {
                        id_tipoEmbalagem = 0;
                    } else {
                        id_tipoEmbalagem = 0;
                    }                    
                }

                qtdEmbalagem = (int) rst.getDouble("PROQTDUNDVDA");

                if ((rst.getString("REFDES") != null)
                        && (!rst.getString("REFDES").isEmpty())) {
                    descricaocompleta = util.acertarTexto(rst.getString("REFDES").replace("'", "").trim());
                } else {
                    descricaocompleta = "";
                }

                if ((rst.getString("REFDESRDZ") != null)
                        && (!rst.getString("REFDESRDZ").isEmpty())) {
                    descricaoreduzida = util.acertarTexto(rst.getString("REFDESRDZ").replace("'", "").trim());
                } else {
                    descricaoreduzida = "";
                }

                descricaogondola = descricaocompleta;

                if (rst.getString("REFDATCAD") != null) {
                    dataCadastro = Util.formatDataGUI(rst.getDate("REFDATCAD"));
                } else {
                    dataCadastro = Util.formatDataGUI(new Date(new java.util.Date().getTime()));
                }
                
                if ((rst.getString("SECCOD") != null)
                        && (!rst.getString("SECCOD").isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("SECCOD"));
                } else {
                    mercadologico1 = 0;
                }

                if ((rst.getString("GRPCOD") != null)
                        && (!rst.getString("GRPCOD").isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("GRPCOD"));
                } else {
                    mercadologico2 = 0;
                }

                if ((rst.getString("SBGCOD") != null)
                        && (!rst.getString("SBGCOD").isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("SBGCOD"));
                } else {
                    mercadologico3 = 0;
                }

                if ((rst.getString("PRONCM") != null)
                        && (!rst.getString("PRONCM").isEmpty())
                        && (rst.getString("PRONCM").trim().length() > 5)) {

                    ncmAtual = rst.getString("PRONCM").trim();

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("PROCOD") != null)
                        && (!rst.getString("PROCOD").isEmpty())) {
                    id_familiaproduto = Integer.parseInt(rst.getString("PROCOD"));
                } else {
                    id_familiaproduto = -1;
                }
                               
                if ((rst.getString("CSTREP") != null)
                        && (!"".equals(rst.getString("CSTREP")))) {
                    custo = rst.getDouble("CSTREP");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("PRCVDA1") != null)
                        && (!"".equals(rst.getString("PRCVDA1")))) {
                    precoVenda = rst.getDouble("PRCVDA1");
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("PROMRG1") != null)
                        && (!"".equals(rst.getString("PROMRG1")))) {
                   if (rst.getDouble("PROMRG1")>0) {
                       margem=rst.getDouble("PROMRG1");                   
                   }else{
                        if((custo>0) && (precoVenda>0)){
                            margem=((custo/precoVenda)*100);                    
                        }else{
                            margem=0;
                        }                       
                   }                                        
                }else if((custo>0) && (precoVenda>0)){
                   margem=((custo/precoVenda)*100);                    
                }
                
                if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                    tribAliquota = rst.getString("TRBID").trim();
                } else {
                    tribAliquota = "999";
                }
                
                if (eBalanca) {		
                    codigobarras = Long.parseLong(String.valueOf(id));		
                } else {		
                    codigobarras = -2;// não importar produtoautomacao codigo barras		
                }

                if (descricaocompleta.length() > 60) {
                    descricaocompleta = descricaocompleta.substring(0, 60);
                }

                if (descricaoreduzida.length() > 22) {
                    descricaoreduzida = descricaoreduzida.substring(0, 22);
                }

                if (descricaogondola.length() > 60) {
                    descricaogondola = descricaogondola.substring(0, 60);
                }

                oProduto.id = id;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.descricaoCompleta = descricaocompleta;
                oProduto.descricaoReduzida = descricaoreduzida;
                oProduto.descricaoGondola = descricaogondola;
                oProduto.dataCadastro = String.valueOf(dataCadastro);
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = id_familiaproduto;
                oProduto.margem = margem;
                oProduto.qtdEmbalagem = 1;
                oProduto.idTipoEmbalagem = id_tipoEmbalagem;
                oProduto.idComprador = 1;
                oProduto.idFornecedorFabricante = 1;
                oProduto.pesavel = pesavel;
                oProduto.validade = validade;
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
                oProduto.idTipoPisCofinsDebito = 1;
                oProduto.idTipoPisCofinsCredito = 13;
                oProduto.tipoNaturezaReceita = 999;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.idLoja = id_loja;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 23;
                oAliquota.idAliquotaDebito = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaCredito = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstado = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaCreditoForaEstado = retornarAliquotaICMSMilenio(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstadoNF = retornarAliquotaICMSMilenio(tribAliquota);

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.idTipoEmbalagem = id_tipoEmbalagem;
                oAutomacao.qtdEmbalagem = 1;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = id;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.barras = codigobarras;
                oCodigoAnterior.referencia = referencia;

                if ((rst.getString("PRONCM") != null)
                        && (!rst.getString("PRONCM").isEmpty())) {

                    oCodigoAnterior.ncm = rst.getString("PRONCM");
                } else {
                    oCodigoAnterior.ncm = "";
                }

                oCodigoAnterior.id_loja = id_loja;

                if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {

                    oCodigoAnterior.ref_icmsdebito = rst.getString("TRBID");
                }

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(id, oProduto);
            }

            stmPostgres.close();

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    */

    public Map<Integer, ProdutoVO> carregarPrecoProdutoMilenio(int id_loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double margem=0, custo=0, precoVenda=0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT R.REFPLU, R.REFPLUDV, PR.PRCVDA1, P.PROMRG1, E.EANCOD ");
            sql.append(" FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD  ");
            sql.append("LEFT JOIN  [SysacME].[dbo].EAN E ON E.REFPLU = R.REFPLU  ");
            sql.append("LEFT JOIN [SysacME].[dbo].[PRECO] PR ").append(MilenioDAO.utilizaREFPLU ? "  ON PR.REFPLU = R.REFPLU\n" : "  ON PR.PROCOD = R.PROCOD\n");
            sql.append("where PR.LOJCOD = " + id_loja + " and (PR.PRCVDA1 != 0 or P.PROMRG1 != 0) "); 
            sql.append("ORDER BY r.REFDES asc         ");     


            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = 1;
                idProduto = Integer.parseInt(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                
                if ((rst.getString("PRCVDA1") != null)
                        && (!"".equals(rst.getString("PRCVDA1")))) {
                    precoVenda = rst.getDouble("PRCVDA1");
                } else {
                    precoVenda = 0;
                }
                

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public Map<Integer, ProdutoVO> carregarCustoProdutoMilenio(int id_loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double custoSemImposto = 0, custo = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select c.LOJCOD, R.REFPLU,R.REFPLUDV, c.CSTREP, C.CSTSEMIMP ");
            sql.append("from [SysacME].[dbo].[CUSTO] c ");
            sql.append("INNER JOIN  [SysacME].[dbo].[REFERENCIA] R ");
            sql.append("ON R.REFPLU = c.REFPLU  ");            
            sql.append("where LOJCOD = " + id_loja + " and (c.CSTREP != 0 or C.CSTSEMIMP != 0) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = 1;
                idProduto = Integer.parseInt(rst.getString("REFPLU") + rst.getString("REFPLUDV"));                

                if ((rst.getString("CSTREP") != null)
                        && (!"".equals(rst.getString("CSTREP")))) {
                    custo = rst.getDouble("CSTREP");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("CSTSEMIMP") != null)
                        && (!"".equals(rst.getString("CSTSEMIMP")))) {
                    custoSemImposto = rst.getDouble("CSTSEMIMP");
                } else {
                    custoSemImposto = 0;
                }
                
                if (custo==0){
                    if (custoSemImposto>0){
                        custo=custoSemImposto;
                    }
                }
                if (custoSemImposto==0){
                    if (custo>0){
                        custoSemImposto=custo;
                    }                    
                }                

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custoSemImposto;

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
    
    public Map<Integer, ProdutoVO> carregarEstoqueProdutoMilenio(int id_loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double estoque = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT E.LOJCOD, E.REFPLU + '' + R.REFPLUDV AS PRODUTO, E.ESTTOT ");
            sql.append("FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD  ");
            sql.append("INNER JOIN  [SysacME].[dbo].ESTOQUE E ON R.REFPLU = E.REFPLU ");
            sql.append("where LOJCOD = " + id_loja); 
            sql.append("     AND E.LOCCOD = 1 AND E.ESTTOT != 0");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = rst.getInt("LOJCOD");
                idProduto = Integer.parseInt(rst.getString("PRODUTO"));
                estoque = rst.getDouble("ESTTOT");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oComplemento.estoqueMinimo = 0;
                oComplemento.estoqueMaximo = 0;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = estoque;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    } 
    
    public Map<Integer, ProdutoVO> carregarEstoqueProdutoMilenio(int id_loja, int idLocalEstoque) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idLoja;
        double estoque = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT E.LOJCOD, E.REFPLU + '' + R.REFPLUDV AS PRODUTO, E.ESTTOT ");
            sql.append("FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD  ");
            sql.append("INNER JOIN  [SysacME].[dbo].ESTOQUE E ON R.REFPLU = E.REFPLU ");
            sql.append("where LOJCOD = " + id_loja); 
            sql.append("     AND E.LOCCOD = " + idLocalEstoque + " and E.ESTTOT != 0");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idLoja = rst.getInt("LOJCOD");
                idProduto = Integer.parseInt(rst.getString("PRODUTO"));
                estoque = rst.getDouble("ESTTOT");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oComplemento.estoqueMinimo = 0;
                oComplemento.estoqueMaximo = 0;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = estoque;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    } 
    
    public Map<Long, ProdutoVO> carregarPisCofinsMilenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        String strNaturezaReceita = "";
        int cst_pis_e, cst_pis_s, tipoNaturezaReceita, id_tipopiscofins,
                id_tipopiscofinscredito;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            /*      
             VIEW CRIADO NO BANCO, CASO NÃO TENHA USAR O SQL ABAIXO.
             OBS: MAIS DE UMA LOJA, BUSCAR POR CNPJ CODIGO COMENTADO.
            
             sql.append("select codigo_produto, cod_natureza_receita, pis_cst_e, pis_cst_s ");
             sql.append("from [SysacME].[dbo].[MXF_VW_PIS_COFINS_02292747000439] ");
             */
            sql.append("SELECT Cast(R.REFPLU + '' + R.REFPLUDV AS INT)                  AS codigo_produto, R.REFCODINT,  ");
            sql.append("       PIS.nrccod                             AS cod_natureza_receita, ");
            sql.append("       PIS.ifasittrbent                       AS pis_cst_e, ");
            sql.append("       PIS.ifasittrbsai                       AS pis_cst_s, ");
            sql.append("       COFINS.ifasittrbent                    AS cofins_cst_e, ");
            sql.append("       COFINS.ifasittrbsai                    AS cofins_cst_s ");
            sql.append("FROM [SysacME].dbo.v_loja AS L WITH (nolock)  ");
            sql.append("CROSS JOIN [SysacME].dbo.referencia AS R WITH (nolock)  ");
            sql.append("INNER JOIN [SysacME].dbo.produto AS P WITH (nolock) ON P.procod = R.procod  ");
            sql.append("INNER JOIN [SysacME].dbo.secao AS S WITH (nolock) ON P.seccod = S.seccod  ");
            sql.append("INNER JOIN [SysacME].dbo.grupo AS G WITH (nolock) ON P.seccod = G.seccod AND P.grpcod = G.grpcod ");
            sql.append("INNER JOIN [SysacME].dbo.subgrupo AS SG WITH (nolock) ON P.seccod = SG.seccod AND P.grpcod = SG.grpcod AND P.sbgcod = SG.sbgcod ");
            sql.append("INNER JOIN (SELECT F.procod,IA.ifaregfed, IA.ifasittrbent, IA.ifasittrbsai, IA.ifaalqent, IA.ifaalqsai, ");
            sql.append("			n.nrccod FROM sysacme.dbo.fat_pro AS F WITH (nolock) INNER JOIN [SysacME].dbo.imposto_federal AS I WITH (nolock) ON ");
            sql.append("			I.imfcod = F.imfcod INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS IA WITH (nolock) ON IA.imfcod = I.imfcod ");
            sql.append("            LEFT OUTER JOIN [SysacME].dbo.natureza_receita_pis_cofins AS n ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("            WHERE (I.imftip = '01' )) AS PIS ON PIS.procod = P.procod AND PIS.ifaregfed = L.lojregfed ");
            sql.append("INNER JOIN (SELECT F.procod, IA.ifaregfed, IA.ifasittrbent, IA.ifasittrbsai, IA.ifaalqent, IA.ifaalqsai, n.nrccod ");
            sql.append("            FROM sysacme.dbo.fat_pro AS F INNER JOIN [SysacME].dbo.imposto_federal AS I WITH (nolock) ON I.imfcod = F.imfcod "); 
            sql.append("            INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS IA WITH (nolock) ON IA.imfcod = I.imfcod ");
            sql.append("            LEFT OUTER JOIN [SysacME].dbo.natureza_receita_pis_cofins AS n ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("            WHERE  ( I.imftip = '02' )) AS COFINS ON ");
            sql.append("COFINS.procod = P.procod AND COFINS.ifaregfed = L.lojregfed ");          
            sql.append(" union ");
            sql.append("SELECT Cast(R.refplu AS INT)                  AS codigo_produto,   R.REFCODINT, ");
            sql.append("       PIS.nrccod                             AS cod_natureza_receita, ");
            sql.append("       PIS.ifasittrbent                       AS pis_cst_e, ");
            sql.append("       PIS.ifasittrbsai                       AS pis_cst_s, ");
            sql.append("       COFINS.ifasittrbent                    AS cofins_cst_e, ");
            sql.append("       COFINS.ifasittrbsai                    AS cofins_cst_s ");
            sql.append("FROM   [SysacME].dbo.v_loja AS L WITH (nolock) ");
            sql.append("       CROSS JOIN [SysacME].dbo.referencia AS R WITH (nolock) ");
            sql.append("       INNER JOIN [SysacME].dbo.produto AS P WITH (nolock) ");
            sql.append("               ON P.procod = R.procod ");
            sql.append("       INNER JOIN [SysacME].dbo.secao AS S WITH (nolock) ");
            sql.append("               ON P.seccod = S.seccod ");
            sql.append("       INNER JOIN [SysacME].dbo.grupo AS G WITH (nolock) ");
            sql.append("               ON P.seccod = G.seccod ");
            sql.append("                  AND P.grpcod = G.grpcod ");
            sql.append("       INNER JOIN [SysacME].dbo.subgrupo AS SG WITH (nolock) ");
            sql.append("               ON P.seccod = SG.seccod ");
            sql.append("                  AND P.grpcod = SG.grpcod ");
            sql.append("                  AND P.sbgcod = SG.sbgcod ");
            sql.append("       INNER JOIN (SELECT F.procod, ");
            sql.append("IA.ifaregfed, ");
            sql.append("                          IA.ifasittrbent, ");
            sql.append("                          IA.ifasittrbsai, ");
            sql.append("                          IA.ifaalqent, ");
            sql.append("                          IA.ifaalqsai, ");
            sql.append("                          n.nrccod ");
            sql.append("                   FROM   sysacme.dbo.fat_pro AS F WITH (nolock) ");
            sql.append("                          INNER JOIN [SysacME].dbo.imposto_federal AS I WITH ( ");
            sql.append("                                     nolock) ");
            sql.append("                                  ON I.imfcod = F.imfcod ");
            sql.append("                          INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS ");
            sql.append("                                     IA WITH ");
            sql.append("                                     (nolock) ");
            sql.append("                                  ON IA.imfcod = I.imfcod ");
            sql.append("                          LEFT OUTER JOIN ");
            sql.append("                          [SysacME].dbo.natureza_receita_pis_cofins AS n ");
            sql.append("                                       ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("                   WHERE  ( I.imftip = '01' )) AS PIS ");
            sql.append("               ON PIS.procod = P.procod ");
            sql.append("                  AND PIS.ifaregfed = L.lojregfed ");
            sql.append("       INNER JOIN (SELECT F.procod, ");
            sql.append("                          IA.ifaregfed, ");
            sql.append("                          IA.ifasittrbent, ");
            sql.append("                          IA.ifasittrbsai, ");
            sql.append("                          IA.ifaalqent, ");
            sql.append("                          IA.ifaalqsai, ");
            sql.append("                          n.nrccod ");
            sql.append("                   FROM   sysacme.dbo.fat_pro AS F ");
            sql.append("                          INNER JOIN [SysacME].dbo.imposto_federal AS I WITH ( ");
            sql.append("                                     nolock) ");
            sql.append("                                  ON I.imfcod = F.imfcod ");
            sql.append("                          INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS ");
            sql.append("                                     IA WITH ");
            sql.append("                                     (nolock) ");
            sql.append("                                  ON IA.imfcod = I.imfcod ");
            sql.append("                          LEFT OUTER JOIN ");
            sql.append("                          [SysacME].dbo.natureza_receita_pis_cofins AS n");
            sql.append("                                       ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("                   WHERE  ( I.imftip = '02' )) AS COFINS ");
            sql.append("               ON COFINS.procod = P.procod ");
            sql.append("                  AND COFINS.ifaregfed = L.lojregfed ");
            /* caso haja mais de 1 loja, buscar via CNPJ
             WHERE  ( L.agecgccpf = '08105805000101' )
             AND ( P.promix IS NULL )
             OR ( L.agecgccpf = '08105805000101' )
             AND ( L.lojmix IS NULL )
             OR ( L.agecgccpf = '08105805000101' )
             AND ( L.lojmix LIKE '%' + P.promix + '%' ) */

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                cst_pis_e = Integer.parseInt(rst.getString("pis_cst_e"));
                cst_pis_s = Integer.parseInt(rst.getString("pis_cst_s"));
                
                //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                String strId;
                if (!this.utilizarREFCODINT) {
                    strId = Utils.formataNumero(rst.getString("codigo_produto"));
                } else {
                    strId = Utils.formataNumero(rst.getString("REFCODINT"));
                    if (strId.equals("0")) {
                        strId = Utils.formataNumero(rst.getString("codigo_produto"));
                    }
                }
                long idProduto = Long.parseLong(strId);
                //</editor-fold>

                oProduto.id = 0;
                oProduto.idDouble = idProduto;

                id_tipopiscofins = Utils.retornarPisCofinsDebito(cst_pis_s);
                id_tipopiscofinscredito = Utils.retornarPisCofinsCredito(cst_pis_e);

                oProduto.idTipoPisCofinsCredito = id_tipopiscofinscredito;
                oProduto.idTipoPisCofinsDebito = id_tipopiscofins;

                if ((rst.getString("cod_natureza_receita") != null)
                        && (!rst.getString("cod_natureza_receita").isEmpty())) {
                    strNaturezaReceita = rst.getString("cod_natureza_receita");
                    oProduto.tipoNaturezaReceita = Integer.parseInt(strNaturezaReceita);
                } else {
                    strNaturezaReceita = "999";
                    oProduto.tipoNaturezaReceita = 999;
                }

                //tipoNaturezaReceita = util.retornarTipoNaturezaReceita(id_tipopiscofins, strNaturezaReceita);



                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.piscofinscredito = rst.getInt("pis_cst_e");
                oCodigoAnterior.piscofinsdebito = rst.getInt("pis_cst_s");
                oCodigoAnterior.naturezareceita = Integer.parseInt(strNaturezaReceita);

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    

    public Map<Integer, ProdutoVO> carregarSituacaoCadastroProdutoMilenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idSituacaoCadastro;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT Cast(R.refplu+R.refpluDV AS INT) AS codigo_produto, ");
            sql.append("       (SELECT TOP (1) CAST(EANCOD AS bigint) AS Expr1  ");
            sql.append("        FROM  SysacME.dbo.EAN AS E WHERE (REFPLU = R.REFPLU) AND (isnumeric(EANCOD) = 1)) AS EANCOD,  ");
            sql.append("        (SELECT TOP (1) Cast(eancod AS BIGINT) AS Expr1 FROM   sysacme.dbo.ean AS E  ");
            sql.append("        WHERE  ( refplu = R.refplu ) AND ( Isnumeric(eancod) = 1 )) AS ean, CASE  ");
            sql.append("        WHEN ( R.refdatfimlin IS NULL OR R.refdatfimlin > (SELECT Max(abrdat)  ");
            sql.append("        FROM   [SysacME].dbo.abertura) ) THEN 'ATIVO' ELSE 'INATIVO' END AS status "); 
            sql.append("FROM [SysacME].dbo.v_loja AS L WITH (nolock)  ");
            sql.append("CROSS JOIN [SysacME].dbo.referencia AS R WITH (nolock)  ");
            sql.append("INNER JOIN [SysacME].dbo.produto AS P WITH (nolock) ON P.procod = R.procod  ");
            sql.append("INNER JOIN [SysacME].dbo.secao AS S WITH (nolock) ON P.seccod = S.seccod  ");
            sql.append("INNER JOIN [SysacME].dbo.grupo AS G WITH (nolock) ON P.seccod = G.seccod AND P.grpcod = G.grpcod ");
            sql.append("INNER JOIN [SysacME].dbo.subgrupo AS SG WITH (nolock) ON P.seccod = SG.seccod AND P.grpcod = SG.grpcod AND P.sbgcod = SG.sbgcod ");
            sql.append("INNER JOIN (SELECT F.procod,IA.ifaregfed, IA.ifasittrbent, IA.ifasittrbsai, IA.ifaalqent, IA.ifaalqsai, ");
            sql.append("			n.nrccod FROM sysacme.dbo.fat_pro AS F WITH (nolock) INNER JOIN [SysacME].dbo.imposto_federal AS I WITH (nolock) ON ");
            sql.append("			I.imfcod = F.imfcod INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS IA WITH (nolock) ON IA.imfcod = I.imfcod ");
            sql.append("            LEFT OUTER JOIN [SysacME].dbo.natureza_receita_pis_cofins AS n ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("            WHERE (I.imftip = '01' )) AS PIS ON PIS.procod = P.procod AND PIS.ifaregfed = L.lojregfed ");
            sql.append("INNER JOIN (SELECT F.procod, IA.ifaregfed, IA.ifasittrbent, IA.ifasittrbsai, IA.ifaalqent, IA.ifaalqsai, n.nrccod ");
            sql.append("            FROM sysacme.dbo.fat_pro AS F INNER JOIN [SysacME].dbo.imposto_federal AS I WITH (nolock) ON I.imfcod = F.imfcod "); 
            sql.append("            INNER JOIN [SysacME].dbo.imposto_federal_aplicacao AS IA WITH (nolock) ON IA.imfcod = I.imfcod ");
            sql.append("            LEFT OUTER JOIN [SysacME].dbo.natureza_receita_pis_cofins AS n ON n.nrccodext = IA.ifanatrecpiscofins ");
            sql.append("            WHERE  ( I.imftip = '02' )) AS COFINS ON ");
            sql.append("COFINS.procod = P.procod AND COFINS.ifaregfed = L.lojregfed ");          

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                idProduto = Integer.parseInt(rst.getString("codigo_produto"));

                oProduto.id = Integer.parseInt(rst.getString("codigo_produto"));

                if ("ATIVO".equals(rst.getString("status"))) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarEanProdutoMilenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT * FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD ");
            sql.append("LEFT JOIN  [SysacME].[dbo].EAN E ON E.REFPLU  = R.REFPLU ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                String strId;
                if (this.utilizarREFCODINT) {                    
                    strId = Utils.formataNumero(rst.getString("REFCODINT"));
                    if (strId.equals("0")) {
                        strId = rst.getString("REFPLU") + rst.getString("REFPLUDV");
                    }
                } else  {
                    strId = rst.getString("REFPLU") + rst.getString("REFPLUDV");
                }
                
                oProduto.idDouble = Long.parseLong(strId);
                
                if (rst.getString("EANCOD")!=null){
                    codigobarras = rst.getLong("EANCOD");
                }else{
                    codigobarras = -1;                    
                }

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = 1;

                oProduto.vAutomacao.add(oAutomacao);

                vProduto.put(codigobarras, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<FornecedorVO> carregarFornecedorMilenio() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, endereco, bairro, inscricaoestadual,
                telefone1, telefone2, numero, complemento, obs, fax, email;
        Long cnpj, cep;
        String datacadastro;
        int id_tipoinscricao, id_municipio = 0, id_estado, IdFornecedor;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT *, ");
            sql.append("(SELECT TOP (1) CNTMAIL ");
             sql.append("FROM dbo.CONTATO AS C ");
            sql.append(" WHERE (C.AGECOD = F.AGECOD) AND CNTMAIL IS NOT NULL) AS EMAIL ");
            sql.append("from FORNECEDOR F  ");
            sql.append("INNER JOIN AGENTE A ON ");
            sql.append(" A.AGECOD = F.AGECOD ");
            sql.append("where ");
            sql.append(" AGECGCCPF is not null  ");
            sql.append("order by FORCOD  ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FornecedorVO oFornecedor = new FornecedorVO();
                
                IdFornecedor = rst.getInt("AGECOD");//rst.getInt("FORCOD");
                
                if ((rst.getString("AGEDES") != null)
                        && (!rst.getString("AGEDES").isEmpty())) {
                    razaosocial = util.acertarTexto(rst.getString("AGEDES").replace("'", ""));
                } else {
                    razaosocial = "";
                }

                if ((rst.getString("AGEFAN") != null)
                        && (!rst.getString("AGEFAN").isEmpty())) {
                    nomefantasia = util.acertarTexto(rst.getString("AGEFAN").replace("'", ""));
                } else {
                    nomefantasia = "";
                }

                if ((rst.getString("AGECGCCPF") != null)
                        && (!rst.getString("AGECGCCPF").isEmpty())) {
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("AGECGCCPF")));
                } else {
                    cnpj = Long.parseLong("0");
                }

                if ((rst.getString("AGECGFRG") != null)
                        && (!rst.getString("AGECGFRG").isEmpty())) {
                    inscricaoestadual = util.acertarTexto(rst.getString("AGECGFRG").replace("'", ""));
                } else {
                    inscricaoestadual = "ISENTO";
                }

                if ("J".equals(rst.getString("AGEPFPJ").trim())) {
                    id_tipoinscricao = 0;
                } else {
                    id_tipoinscricao = 1;
                }

                if ((rst.getString("AGETEL1") != null)
                        && (!rst.getString("AGETEL1").isEmpty())) {
                    telefone1 = util.acertarTexto(rst.getString("AGETEL1"));
                } else {
                    telefone1 = "0000000000";
                }

                if ((rst.getString("AGEEND") != null)
                        && (!rst.getString("AGEEND").isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("AGEEND").replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("AGEBAI") != null)
                        && (!rst.getString("AGEBAI").isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("AGEBAI").replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("AGENUM") != null)
                        && (!rst.getString("AGENUM").isEmpty())) {
                    numero = util.acertarTexto(rst.getString("AGENUM").replace("'", ""));
                } else {
                    numero = "";
                }

                if ((rst.getString("AGECPL") != null)
                        && (!rst.getString("AGECPL").isEmpty())) {
                    complemento = util.acertarTexto(rst.getString("AGECPL").replace("'", ""));
                } else {
                    complemento = "";
                }

                if ((rst.getString("AGECEP") != null)
                        && (!rst.getString("AGECEP").isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("AGECEP")));
                } else {
                    cep = Long.parseLong("60040100");
                }

                if ((rst.getString("AGEOBS") != null)
                        && (!rst.getString("AGEOBS").isEmpty())) {
                    obs = util.acertarTexto(rst.getString("AGEOBS").replace("'", ""));
                } else {
                    obs = "";
                }

                if ((rst.getString("AGECID") != null)
                        && (!rst.getString("AGECID").isEmpty())) {

                    if ((rst.getString("AGEEST") != null)
                            && (!rst.getString("AGEEST").isEmpty())) {

                        id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("AGECID").replace("'", "")),
                                util.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                        if (id_municipio == 0) {
                            id_municipio = 2304400;
                        }
                    }
                } else {
                    id_municipio = 2304400;
                }

                if ((rst.getString("AGEEST") != null)
                        && (!rst.getString("AGEEST").isEmpty())) {
                    id_estado = Utils.retornarEstadoDescricao(util.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                    if (id_estado == 0) {
                        id_estado = 23;
                    }
                } else {
                    id_estado = 23;
                }

                /*if ((rst.getString("AGEDATCAD") != null)
                        && (!rst.getString("AGEDATCAD").isEmpty())) {
                    datacadastro = Util.formatDataGUI(rst.getDate("AGEDATCAD"));                    
                } else {
                    datacadastro = null;                    
                }*/
                
                if ((rst.getString("AGETEL2") != null)
                        && (!rst.getString("AGETEL2").isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("AGETEL2"));
                } else {
                    telefone2 = "";
                }

                if ((rst.getString("AGEFAX") != null)
                        && (!rst.getString("AGEFAX").isEmpty())) {
                    fax = util.formataNumero(rst.getString("AGEFAX"));
                } else {
                    fax = "";
                }

                if ((rst.getString("EMAIL") != null)
                        && (!rst.getString("EMAIL").isEmpty())) {
                    email = util.acertarTexto(rst.getString("EMAIL").replace("'", ""));
                } else {
                    email = "";
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

                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                }

                if (inscricaoestadual.length() > 20) {
                    inscricaoestadual = inscricaoestadual.substring(0, 20);
                }

                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                oFornecedor.codigoanterior = IdFornecedor;
                oFornecedor.razaosocial = razaosocial;
                oFornecedor.nomefantasia = nomefantasia;
                oFornecedor.endereco = endereco;
                oFornecedor.bairro = bairro;
                oFornecedor.id_municipio = id_municipio;
                oFornecedor.cep = cep;
                oFornecedor.id_estado = id_estado;
                oFornecedor.telefone = telefone1;
                oFornecedor.id_tipoinscricao = id_tipoinscricao;
                oFornecedor.inscricaoestadual = inscricaoestadual;
                oFornecedor.cnpj = cnpj;
                oFornecedor.numero = numero;
                oFornecedor.complemento = complemento;
                oFornecedor.observacao = obs;
                oFornecedor.telefone2 = telefone2;
                oFornecedor.fax = fax;
                oFornecedor.email = email;

                vFornecedor.add(oFornecedor);
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorMilenio() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT r.REFCODINT, f.AGECGCCPF, cf.FORCOD, cf.REFPLU, R.REFPLUDV, cf.CATQTDUND, cf.UNDCOD, cf.REFFOR ");
            sql.append("FROM [SysacME].[dbo].[CATALOGO_FORNECEDOR]  cf ");
            sql.append("inner join [SysacME].[dbo].[REFERENCIA] R ");            
            sql.append("on R.REFPLU = cf.REFPLU ");            
            sql.append("inner join [SysacME].[dbo].[V_FORNECEDOR] f ");
            sql.append("on f.FORCOD = cf.FORCOD ");
            sql.append("ORDER BY REFPLU ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("FORCOD");
                
                //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                String strId;
                if (!this.utilizarREFCODINT) {
                    strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                } else {
                    strId = Utils.formataNumero(rst.getString("REFCODINT"));
                    if (strId.equals("0")) {
                        strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                    }
                }
                long idProduto = Long.parseLong(strId);
                //</editor-fold>               
              
                qtdEmbalagem = (int) rst.getDouble("CATQTDUND");

                if ((rst.getString("REFFOR") != null)
                        && (!rst.getString("REFFOR").isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("REFFOR").replace("'", ""));
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.cnpFornecedor = Utils.stringToLong(rst.getString("AGECGCCPF"));
                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;
                oProdutoFornecedor.setId_estado(23);
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
    
    public List<ClientePreferencialVO> carregarFornecedorComoCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(               
                "SELECT *, \n" +
                "(SELECT TOP (1) CNTMAIL \n" +
                "FROM dbo.CONTATO AS C \n" +
                " WHERE (C.AGECOD = F.AGECOD) AND CNTMAIL IS NOT NULL) AS EMAIL \n" +
                "from FORNECEDOR F  \n" +
                "INNER JOIN AGENTE A ON \n" +
                " A.AGECOD = F.AGECOD \n" +
                "where \n" +
                " AGECGCCPF is not null  \n" +
                "order by FORCOD "
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("agecod"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("agecod"));
                    oClientePreferencial.setNome(rst.getString("AGEDES"));
                    oClientePreferencial.setEndereco(rst.getString("AGEEND"));
                    oClientePreferencial.setNumero(rst.getString("AGENUM"));
                    oClientePreferencial.setComplemento(rst.getString("AGECPL"));
                    oClientePreferencial.setBairro(rst.getString("AGEBAI"));
                    
                    int id_municipio = 2304400, id_estado = 23;
                    if ((rst.getString("AGECID") != null)
                        && (!rst.getString("AGECID").isEmpty())) {

                    if ((rst.getString("AGEEST") != null)
                            && (!rst.getString("AGEEST").isEmpty())) {

                            id_municipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("AGECID").replace("'", "")),
                                    Utils.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                            if (id_municipio == 0) {
                                id_municipio = 2304400;
                            }
                        }
                    }

                    if ((rst.getString("AGEEST") != null)
                            && (!rst.getString("AGEEST").isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                        if (id_estado == 0) {
                            id_estado = 23;
                        }
                    } 
                    
                    oClientePreferencial.setId_estado(id_estado);
                    oClientePreferencial.setId_municipio(id_municipio);
                    oClientePreferencial.setCep(Utils.formatCep(rst.getString("AGECEP")));
                    oClientePreferencial.setTelefone(rst.getString("AGETEL1"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("AGECGFRG"));
                    oClientePreferencial.setCnpj(rst.getString("AGECGCCPF"));
                    oClientePreferencial.setSexo(1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getString("AGEDATCAD"));
                    oClientePreferencial.setEmail(rst.getString("EMAIL"));
                    oClientePreferencial.setValorlimite(0);
                    oClientePreferencial.setFax(rst.getString("AGEFAX"));
                    oClientePreferencial.setBloqueado(rst.getDate("AGEDATBLO") != null);
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setTelefone2(rst.getString("AGETEL2"));
                    oClientePreferencial.setObservacao("IMPORTADO VR " + rst.getString("AGEOBS"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setOrgaoemissor(rst.getString("AGEORGEXP"));                  

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }   

    public List<ClientePreferencialVO> carregarClienteMilenio() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, numero, complemento, obs, telefone1,
                orrgexp, inscricaoestadual, conjuge, email;
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao, id, agente;
        double limite, salario;
        boolean bloqueado;
        Long cnpj, cep;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select AGECOD, CLICOD, AGEDES, AGEORGEXP, AGEFAN, AGECGCCPF, AGECGFRG, AGEDATCAD, AGEPFPJ, ");
            sql.append(" AGEDATBLO, AGETEL1, AGETEL2, AGEFAX, AGEEND, AGEBAI, AGENUM, AGECPL, ");
            sql.append(" AGECEP, AGEOBS, AGECORELE, AGECID, AGEEST, CLILIMCRE, CLISEX, CLIREN, CLICONJG, AGEDATALT ");
            sql.append(" from [SysacME].[dbo].[V_CLIENTE] ");
            sql.append(" where AGECGCCPF is not null ");
            sql.append("order by CLICOD ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                agente = rst.getInt("AGECOD");
                id = rst.getInt("AGECOD");//rst.getInt("CLICOD");

                if ((rst.getString("AGEDES") != null)
                        && (!rst.getString("AGEDES").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("AGEDES").replace("'", ""));
                } else {
                    nome = "";
                }

                if ((rst.getString("AGEORGEXP") != null)
                        && (!rst.getString("AGEORGEXP").isEmpty())) {
                    orrgexp = util.acertarTexto(rst.getString("AGEORGEXP").replace("'", ""));
                } else {
                    orrgexp = "";
                }

                if ((rst.getString("AGECGCCPF") != null)
                        && (!rst.getString("AGECGCCPF").isEmpty())) {
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("AGECGCCPF")));
                } else {
                    cnpj = Long.parseLong(String.valueOf(0));
                }

                if ((rst.getString("AGECGFRG") != null)
                        && (!rst.getString("AGECGFRG").isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("AGECGFRG").replace("'", ""), 18);
                } else {
                    inscricaoestadual = "ISENTO";
                }

                if ((rst.getString("AGEPFPJ") != null)
                        && (!rst.getString("AGEPFPJ").isEmpty())) {
                    if ("F".equals(rst.getString("AGEPFPJ").trim())) {
                        id_tipoinscricao = 1;
                    } else {
                        id_tipoinscricao = 0;
                    }
                } else {
                    id_tipoinscricao = 1;
                }

                if ((rst.getString("AGEDATBLO") != null)
                        && (!rst.getString("AGEDATBLO").isEmpty())) {
                    bloqueado = true;
                } else {
                    bloqueado = false;
                }

                if ((rst.getString("AGETEL1") != null)
                        && (!rst.getString("AGETEL1").isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("AGETEL1"));
                } else {
                    telefone1 = "0000000000";
                }

                if ((rst.getString("AGEEND") != null)
                        && (!rst.getString("AGEEND").isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("AGEEND").replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("AGEBAI") != null)
                        && (!rst.getString("AGEBAI").isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("AGEBAI").replace("'", ""));
                } else {
                    bairro = "";
                }

                if ((rst.getString("AGENUM") != null)
                        && (!rst.getString("AGENUM").isEmpty())) {
                    numero = util.acertarTexto(rst.getString("AGENUM").replace("'", ""));
                } else {
                    numero = "";
                }

                if ((rst.getString("AGECPL") != null)
                        && (!rst.getString("AGECPL").isEmpty())) {
                    complemento = util.acertarTexto(rst.getString("AGECPL").replace("'", ""));
                } else {
                    complemento = "";
                }

                if ((rst.getString("AGECEP") != null)
                        && (!rst.getString("AGECEP").isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("AGECEP")));
                } else {
                    cep = Long.parseLong("60040100");
                }

                if ((rst.getString("AGEOBS") != null)
                        && (!rst.getString("AGEOBS").isEmpty())) {
                    obs = util.acertarTexto(rst.getString("AGEOBS").replace("'", ""));
                    if (obs.length()>80){
                        obs = obs.substring(0, 80);
                    }
                } else {
                    obs = "";
                }

                if ((rst.getString("AGECORELE") != null)
                        && (!rst.getString("AGECORELE").isEmpty())
                        && (rst.getString("AGECORELE").contains("@"))) {
                    email = util.acertarTexto(rst.getString("AGECORELE").replace("'", ""));
                } else {
                    email = "";
                }

                if ((rst.getString("AGECID") != null)
                        && (!rst.getString("AGECID").isEmpty())) {

                    if ((rst.getString("AGEEST") != null)
                            && (!rst.getString("AGEEST").isEmpty())) {

                        id_municipio = util.retornarMunicipioIBGEDescricao(
                                util.acertarTexto(rst.getString("AGECID").replace("'", "")),
                                util.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                        if (id_municipio == 0) {
                            id_municipio = 2304400;
                        }
                    }
                } else {
                    id_municipio = 2304400;
                }

                if ((rst.getString("AGEEST") != null)
                        && (!rst.getString("AGEEST").isEmpty())) {

                    id_estado = util.retornarEstadoDescricao(
                            util.acertarTexto(rst.getString("AGEEST").replace("'", "")));

                    if (id_estado == 0) {
                        id_estado = 23;
                    }
                } else {
                    id_estado = 23;
                }

                if ((rst.getString("CLILIMCRE") != null)
                        && (!rst.getString("CLILIMCRE").isEmpty())) {
                    limite = Double.parseDouble(rst.getString("CLILIMCRE"));
                } else {
                    limite = 0;
                }

                if ((rst.getString("CLISEX") != null)
                        && (!rst.getString("CLISEX").isEmpty())) {

                    if ("F".equals(rst.getString("CLISEX").trim())) {
                        id_sexo = 0;
                    } else {
                        id_sexo = 1;
                    }
                } else {
                    id_sexo = 1;
                }

                if ((rst.getString("CLIREN") != null)
                        && (!rst.getString("CLIREN").isEmpty())) {
                    salario = Double.parseDouble(rst.getString("CLIREN"));
                } else {
                    salario = 0;
                }

                if ((rst.getString("CLICONJG") != null)
                        && (!rst.getString("CLICONJG").isEmpty())) {
                    conjuge = util.acertarTexto(rst.getString("CLICONJG").replace("'", ""));
                } else {
                    conjuge = "";
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

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj));
                }

                if (inscricaoestadual.length() > 20) {
                    inscricaoestadual = inscricaoestadual.substring(0, 20);
                }

                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                if (email.length() > 50) {
                    email = email.substring(0, 50);
                }

                oClientePreferencial.id = id;
                oClientePreferencial.nome = nome;
                oClientePreferencial.endereco = endereco;
                oClientePreferencial.bairro = bairro;
                oClientePreferencial.id_estado = id_estado;
                oClientePreferencial.id_municipio = id_municipio;
                oClientePreferencial.cep = cep;
                oClientePreferencial.telefone = telefone1;
                oClientePreferencial.setInscricaoestadual(inscricaoestadual);
                oClientePreferencial.cnpj = cnpj;
                oClientePreferencial.sexo = id_sexo;
                oClientePreferencial.observacao = obs;
                oClientePreferencial.salario = salario;
                oClientePreferencial.valorlimite = limite;
                oClientePreferencial.nomeconjuge = conjuge;
                oClientePreferencial.numero = numero;
                oClientePreferencial.complemento = complemento;
                oClientePreferencial.orgaoemissor = orrgexp;
                oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                oClientePreferencial.bloqueado = bloqueado;
                oClientePreferencial.email = email;
                oClientePreferencial.codigoanterior = id;
                oClientePreferencial.codigoAgente = agente;
                oClientePreferencial.datacadastro = "";

                vClientePreferencial.add(oClientePreferencial);
            }

            return vClientePreferencial;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberClienteMilenio(int idLojaCliente) throws Exception {
        return this.carregarReceberClienteMilenio(idLojaCliente, null);
    }
    public List<ReceberCreditoRotativoVO> carregarReceberClienteMilenio(int idLojaCliente, List<DocumentoMilenio> rotativoSelecionado) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom;
        double valor;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            String documentos = "";
            if (rotativoSelecionado != null && !rotativoSelecionado.isEmpty()) {
                for (Iterator<DocumentoMilenio> iterator = rotativoSelecionado.iterator(); iterator.hasNext();) {
                    DocumentoMilenio doc = iterator.next();
                    
                    documentos += "'" + doc.id + "'";
                    
                    if (iterator.hasNext()) {
                        documentos += ",";
                    }
                }
                documentos = "AND TR.TIPDOCCOD in (" + documentos + ")\n";
            } else {         
                documentos = "AND TR.TIPDOCCOD = '004'\n";                
            }

            String sql = "SELECT\n" +
                    "  tr.LOJCOD,\n" +
                    "  tr.TIFNUMDOC,\n" +
                    "  tr.AGECOD,\n" +
                    "  tr.TIFDATEMI,\n" +
                    "  tr.TIFDATVNC,\n" +
                    "  tr.TIFVLRNOM,\n" +
                    "  tr.TIFOBS,\n" +
                    "  tr.TIFDATINC,\n" +
                    "  tr.TIFDATALT,\n" +
                    "  tr.TIFVLRJUR,\n" +
                    "  c.AGECGCCPF\n" +
                    "FROM [SysacME].[dbo].[V_TITULO_RECEBER] tr\n" +
                    "INNER JOIN [SysacME].[dbo].[AGENTE] c\n" +
                    "  ON tr.AGECOD = c.AGECOD\n" +
                    "WHERE COALESCE(TIFVLRPAG, 0) < COALESCE(TIFVLRNOM, 0)\n" +
                    "AND TR.TIFSTA = 'N'\n" +
                    documentos +
                    "AND TR.TIFSIT in ('A','P')\n" +
                    "AND tr.LOJCOD = " + idLojaCliente + "\n" +
                    "ORDER BY TIFDATEMI";
            
            rst = stm.executeQuery(sql);

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("AGECOD");
                dataemissao = rst.getString("TIFDATEMI");
                datavencimento = rst.getString("TIFDATVNC");
                
                try{
                    numerocupom = Integer.parseInt(util.formataNumero(rst.getString("TIFNUMDOC")));
                }catch(Exception ex){
                    numerocupom = 0;                    
                }
                
                valor = Double.parseDouble(rst.getString("TIFVLRNOM"));

                if ((rst.getString("TIFOBS") != null)
                        && (!rst.getString("TIFOBS").isEmpty())) {
                    observacao = "IMPORTADO VR " + util.acertarTexto(rst.getString("TIFOBS").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }

                oReceberCreditoRotativo.cnpjCliente = Long.parseLong(rst.getString("AGECGCCPF"));
                oReceberCreditoRotativo.id_loja = idLojaCliente;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarAcertarReceberCreditoRotativoMilenio(int idLojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom;
        double valor, juros;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            rst = stm.executeQuery(
                    "SELECT\n" +
                    "  tr.LOJCOD,\n" +
                    "  tr.TIFNUMDOC,\n" +
                    "  tr.AGECOD,\n" +
                    "  tr.TIFDATEMI,\n" +
                    "  tr.TIFDATVNC,\n" +
                    "  tr.TIFVLRNOM,\n" +
                    "  tr.TIFOBS,\n" +
                    "  tr.TIFDATINC,\n" +
                    "  tr.TIFDATALT,\n" +
                    "  tr.TIFVLRJUR,\n" +
                    "  c.AGECGCCPF\n" +
                    "FROM [SysacME].[dbo].[V_TITULO_RECEBER] tr\n" +
                    "INNER JOIN [SysacME].[dbo].[V_CLIENTE] c\n" +
                    "  ON tr.AGECOD = c.AGECOD\n" +
                    "AND TR.TIFSIT in ('A','P')\n" +
                    "AND tr.LOJCOD = " + idLojaCliente + "\n" +
                    "ORDER BY TIFDATEMI"
            );

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("AGECOD");
                dataemissao = rst.getString("TIFDATEMI");
                datavencimento = rst.getString("TIFDATVNC");
                
                try{
                    numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("TIFNUMDOC")));
                }catch(Exception ex){
                    numerocupom = 0;                    
                }
                
                valor = Double.parseDouble(rst.getString("TIFVLRNOM"));

                if ((rst.getString("TIFOBS") != null)
                        && (!rst.getString("TIFOBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("TIFOBS").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }
                
                juros = rst.getDouble("TIFVLRJUR");

                oReceberCreditoRotativo.cnpjCliente = Long.parseLong(rst.getString("AGECGCCPF"));
                oReceberCreditoRotativo.id_loja = idLojaCliente;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.valorjuros = juros;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ReceberChequeVO> carregarReceberCheque(int idLojaCliente, Date dataVencimento) throws Exception {
        Statement stm = null;
        ResultSet rst;
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            rst = stm.executeQuery(
                    "SELECT\n" +
                    "  ch.LOJCOD,\n" +
                    "  ch.CHRCHQNUM,\n" +
                    "  ch.CHRNUMBCO,\n" +
                    "  ch.CHRNUMAGE,\n" +
                    "  ch.CHRNUMCTA,\n" +
                    "  ch.CHREMICPF,\n" +
                    "  ch.CHREMIDES,\n" +
                    "  ch.TIFNUMDOC,\n" +
                    "  ch.TIFDATEMI,\n" +
                    "  ch.TIFDATVNC,\n" +
                    "  ch.TIFVLRNOM,\n" +
                    "  ag.agedes,\n" +
                    "  ag.AGECGCCPF,\n" +
                    "  ag.AGECGFRG\n" +
                    "FROM V_CHEQUE_RECEBER ch\n" +
                    "LEFT JOIN AGENTE ag\n" +
                    "  ON ch.AGECOD = ag.AGECOD\n" +
                    "WHERE ch.TIFDATPGT IS NULL\n" +
                    "AND ch.TIFDATVNC >= '" + Util.formatData(dataVencimento, "dd/MM/yyyy") + "'\n" +
                    "AND ch.LOJCOD = " + idLojaCliente + "\n" +
                    "order by ch.TIFDATVNC"
            );

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                if (rst.getString("CHREMICPF")!=null && !rst.getString("CHREMICPF").trim().isEmpty()){
                    cpfCnpj = Long.parseLong(rst.getString("CHREMICPF"));
                }else if (rst.getString("AGECGCCPF")!=null && !rst.getString("AGECGCCPF").trim().isEmpty()){
                    cpfCnpj = Long.parseLong(rst.getString("AGECGCCPF"));                                                        
                } else {
                    cpfCnpj = Long.parseLong("123");
                }

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = Utils.retornarBanco(Integer.parseInt(rst.getString("CHRNUMBCO").trim()));

                if ((rst.getString("CHRNUMAGE") != null)
                        && (!rst.getString("CHRNUMAGE").trim().isEmpty())) {
                    agencia = Utils.acertarTexto(rst.getString("CHRNUMAGE"));
                } else {
                    agencia = "";
                }

                if ((rst.getString("CHRNUMCTA") != null)
                        && (!rst.getString("CHRNUMCTA").trim().isEmpty())) {
                    conta = Utils.acertarTexto(rst.getString("CHRNUMCTA"));
                } else {
                    conta = "";
                }

                if ((rst.getString("CHRCHQNUM") != null)
                        && (!rst.getString("CHRCHQNUM").trim().isEmpty())) {

                    cheque = Utils.stringToInt(Utils.formataNumero(rst.getString("CHRCHQNUM")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("TIFDATEMI") != null)
                        && (!rst.getString("TIFDATEMI").trim().isEmpty())) {

                    dataemissao = rst.getString("TIFDATEMI").trim();
                } else {
                    dataemissao = Util.formatDataGUI(new java.util.Date());
                }

                if ((rst.getString("TIFDATVNC") != null)
                        && (!rst.getString("TIFDATVNC").trim().isEmpty())) {

                    datavencimento = rst.getString("TIFDATVNC").trim();
                } else {
                    datavencimento = Util.formatDataGUI(new java.util.Date());
                }

                if ((rst.getString("CHREMIDES") != null)
                        && (!rst.getString("CHREMIDES").isEmpty())) {
                    nome = Utils.acertarTexto(rst.getString("CHREMIDES").replace("'", "").trim());
                } else if ((rst.getString("agedes") != null)
                        && (!rst.getString("agedes").isEmpty())) {
                    nome = Utils.acertarTexto(rst.getString("agedes"));
                } else {
                    nome = "SEM NOME VR";
                }

                if ((rst.getString("AGECGFRG") != null) &&
                 (!rst.getString("AGECGFRG").isEmpty())) {
                    rg = Utils.acertarTexto(rst.getString("AGECGFRG"), 20);
                } else {
                    rg = "";
                }

                observacao = "IMPORTADO VR";
                
                valor = Double.parseDouble(rst.getString("TIFVLRNOM"));
                Long longCupom = Long.parseLong(Utils.formataNumero(rst.getString("TIFNUMDOC")));
                if (longCupom < Integer.MAX_VALUE) {
                    numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("TIFNUMDOC")));
                } else {
                    numerocupom = 0;
                    observacao += ".CUPOM: " + Utils.formataNumero(rst.getString("TIFNUMDOC"));
                }
                
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                 && (!rst.getString("chrobserv1").isEmpty())) {
                 observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                 } else {*/
                
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                 (!rst.getString("chrtelefone").isEmpty()) &&
                 (!"0".equals(rst.getString("chrtelefone").trim()))) {
                 telefone = util.formataNumero(rst.getString("chrtelefone"));
                 } else {*/
                telefone = "";
                //}

                oReceberCheque.setId_loja(idLojaCliente);
                oReceberCheque.setData(dataemissao);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setCpf(cpfCnpj);
                oReceberCheque.setNumerocheque(cheque);
                oReceberCheque.setId_banco(idBanco);
                oReceberCheque.setAgencia(agencia);
                oReceberCheque.setConta(conta);
                oReceberCheque.setNumerocupom(numerocupom);
                oReceberCheque.setValor(valor);
                oReceberCheque.setObservacao(observacao);
                oReceberCheque.setRg(rg);
                oReceberCheque.setTelefone(telefone);
                oReceberCheque.setNome(nome);
                oReceberCheque.setId_tipoinscricao(idTipoInscricao);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setValorjuros(juros);
                oReceberCheque.setValorinicial(valor);

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem, pesavel");
            sql.append("  from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("ID"));
                
                /*if (rst.getInt("id_tipoembalagem")==4) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }*/
                if (rst.getInt("id_tipoembalagem") == 4) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else if (rst.getInt("id_tipoembalagem") != 4 && rst.getBoolean("pesavel")) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else if (rst.getInt("id_tipoembalagem") != 4 && !rst.getBoolean("pesavel") && idProduto >= 10000) {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                } else if (rst.getInt("id_tipoembalagem") != 4 && idProduto < 10000) {
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar2(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoMilenio();

            new FamiliaProdutoDAO()
                    .setGerarCodigo(true)
                    .setVerificarCodigo(true)
                    .salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoMilenio(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoMilenio(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoMilenio(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProduto6Digito(int idLojaVR, int idLojaCliente, int balanca) throws Exception {

        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos de 6 digitos.....");
            Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
            for(ProdutoVO prod: carregarProdutos(idLojaVR, idLojaCliente, balanca, OpcaoProdutoSQLQuery.ATE_999999)) {
                auxiliar.put(prod.idDouble, prod);
            }
            List<LojaVO> vLoja = new LojaDAO().carregar();
            produto.implantacaoExterna = true;
            produto.salvar(new ArrayList(auxiliar.values()), idLojaVR, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoMaior6Digito(int idLojaVR, int idLojaCliente, int balanca) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
            for(ProdutoVO prod: carregarProdutos(idLojaVR, idLojaCliente, balanca, OpcaoProdutoSQLQuery.MAIOR_Q_999999)) {
                auxiliar.put(prod.idDouble, prod);
            }
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT max(p.procod) as CODIGOMAXIMO FROM [SysacME].[dbo].REFERENCIA R ");
            sql.append("INNER JOIN  [SysacME].[dbo].PRODUTO P ON P.PROCOD  = R.PROCOD  ");
            sql.append("INNER JOIN  [SysacME].[dbo].EAN E ON E.REFPLU = R.REFPLU  ");
            sql.append("LEFT JOIN [SysacME].[dbo].[PRECO] PR ON PR.PROCOD = R.PROCOD  ");
            sql.append("WHERE R.REFPLU <= 99999  ");
            sql.append("AND CAST(e.EANCOD AS NUMERIC) >= 1000000  ");
            sql.append("AND LOJCOD = " + idLojaVR);
            rst = stm.executeQuery(sql.toString());
            int MaxCodigoProduto = 30000;
            if (!rst.next()) {
                MaxCodigoProduto = rst.getInt("CODIGOMAXIMO");
            }
            stm.close();
            produto.implantacaoExterna = true;
            produto.salvar(new ArrayList(auxiliar.values()), idLojaVR, vLoja, false, MaxCodigoProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPisCofinsNaturezaReceita() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Pis Cofins, Natureza Receita...");
            Map<Long, ProdutoVO> vPisCofinsNaturezaReceitaMilenio = carregarPisCofinsMilenio();

            ProgressBar.setMaximum(vPisCofinsNaturezaReceitaMilenio.size());

            for (Long keyId : vPisCofinsNaturezaReceitaMilenio.keySet()) {

                ProdutoVO oProduto = vPisCofinsNaturezaReceitaMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPisCofinsNaturezaReceitaMilenio(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProdutoMilenio(int id_loja, int id_lojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProdutoMilenio = carregarPrecoProdutoMilenio(id_lojaDestino);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProdutoMilenio.size());

            for (Integer keyId : vPrecoProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vPrecoProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            //produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            produto.alterarPrecoProdutoRapido(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCustoProdutoMilenio(int id_loja, int id_lojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProdutoMilenio = carregarCustoProdutoMilenio(id_lojaDestino);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProdutoMilenio.size());

            for (Integer keyId : vCustoProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vCustoProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProdutoRapido(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarSituacaoCadastroProdutoMilenio(int id_loja, int id_lojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Situação Cadastro...");
            Map<Integer, ProdutoVO> vSituacaoCadastroProdutoMilenio = carregarSituacaoCadastroProdutoMilenio();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vSituacaoCadastroProdutoMilenio.size());

            for (Integer keyId : vSituacaoCadastroProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vSituacaoCadastroProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarSituacoCadastroProdutoMilenio(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEanProdutoMilenio() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
            Map<Long, ProdutoVO> vEanProdutoMilenio = carregarEanProdutoMilenio();

            ProgressBar.setMaximum(vEanProdutoMilenio.size());

            for (Long keyId : vEanProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEanProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    private void addCodigoBarras(List<ProdutoVO> v_produto) throws Exception {
        try {
            Conexao.begin();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra2...");
            
            class Temp {
                int id = -1;
                int id_tipoembalagem = 0;
                boolean e_balanca = false;
            }
            
            MultiMap<Double, Temp> anteriores = new MultiMap<>(1);
            {
                StringBuilder sql = new StringBuilder();
                sql.append("select p.id, id_tipoembalagem, e_balanca, codigoanterior from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            sql.toString()
                    )) {
                        while (rst.next()) {
                            Temp tp = new Temp();
                            tp.id = rst.getInt("id");
                            tp.id_tipoembalagem = rst.getInt("id_tipoembalagem");
                            tp.e_balanca = rst.getBoolean("e_balanca");
                            anteriores.put(tp, rst.getDouble("codigoanterior"));
                        }
                    }
                }
            }
            Set<Long> eansExistentes = new LinkedHashSet<>();
            {
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select codigobarras from produtoautomacao"
                    )) {
                        while (rst.next()) {
                            eansExistentes.add(rst.getLong("codigobarras"));
                        }
                    }
                }
            }

            int cont = 0;
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoVO i_produto : v_produto) {
                    for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                        if (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7
                                && String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14) {

                            Temp temp = anteriores.get(i_produto.idDouble);

                            if (temp != null) {
                                if (!temp.e_balanca) {

                                    if (!eansExistentes.contains(oAutomacao.codigoBarras)) {
                                        int qtdEmbalagem = oAutomacao.qtdEmbalagem;
                                        if (qtdEmbalagem <= 0) {
                                            qtdEmbalagem = 1;
                                        }

                                        SQLBuilder sql = new SQLBuilder();
                                        sql.setTableName("produtoautomacao");
                                        sql.put("id_produto", temp.id);
                                        sql.put("codigobarras", oAutomacao.codigoBarras);
                                        sql.put("qtdembalagem", qtdEmbalagem);
                                        sql.put("id_tipoembalagem", (oAutomacao.idTipoEmbalagem == -1 ? temp.id_tipoembalagem : oAutomacao.idTipoEmbalagem));

                                        SQLBuilder sql2 = new SQLBuilder();
                                        sql2.setTableName("codigoanterior");
                                        sql2.setSchema("implantacao");
                                        sql2.put("barras", (oAutomacao.codigoBarras > 0
                                                        ? oAutomacao.codigoBarras + ""
                                                        : null));
                                        sql2.setWhere("codigoatual = " + temp.id);

                                        try {
                                            stm.execute(sql.getInsert() + ";" + sql2.getUpdate());
                                        } catch (Exception e) {
                                            Util.exibirMensagem(sql.getInsert() + ";" + sql2.getUpdate(), "");
                                            throw e;
                                        }
                                        eansExistentes.add(oAutomacao.codigoBarras);
                                        cont++;
                                    }
                                }
                            }
                        }
                    }

                    /*if (automacaoLoja) {
                        for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {
                            sql = new StringBuilder();
                            sql.append("select * from produtoautomacaoloja ");
                            sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                            rst3 = stm3.executeQuery(sql.toString());

                            if (rst3.next()) {
                                sql = new StringBuilder();
                                sql.append("update produtoautomacaoloja set ");
                                sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                                sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                                sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                                stm3.execute(sql.toString());
                            } else {
                                sql = new StringBuilder();
                                sql.append("insert into produtoautomacaoloja (");
                                sql.append("codigobarras, precovenda, id_loja) ");
                                sql.append("values (");
                                sql.append(oAutomacaoLoja.codigobarras + ", ");
                                sql.append(oAutomacaoLoja.precovenda + ", ");
                                sql.append(oAutomacaoLoja.id_loja + ");");
                                stm3.execute(sql.toString());
                            }

                            sql = new StringBuilder();
                            sql.append("update produtoautomacao set qtdembalagem = " + oAutomacaoLoja.qtdEmbalagem + " ");
                            sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + ";");
                            stm3.execute(sql.toString());
                        }
                    }*/
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void importarEstoqueProdutoMilenio(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProdutoMilenio = carregarEstoqueProdutoMilenio(idLojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProdutoMilenio.size());

            for (Integer keyId : vEstoqueProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEstoqueProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProdutoRapido(vProdutoNovo, idLojaVR);
            
            // ESTA ROTINA SOMA O VALOR ATUAL DE ESTOQUE MAIS O VALOR CARREGADO NA LISTA
            //produto.alterarEstoqueProdutoSomando(vProdutoNovo, idLojaVR);            
            // ESTA ROTINA SOMA O VALOR ATUAL DE ESTOQUE MAIS O VALOR CARREGADO NA LISTA

        } catch (Exception ex) {

            throw ex;
        }
    }    

    public void importarFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorMilenio();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoFornecedor(int idLojaVR) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorMilenio();

            new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClientePreferencial(int id_loja, int id_lojaDestino) throws Exception {

        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteMilenio();
            new PlanoDAO().salvar(id_loja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, id_loja, id_lojaDestino);           
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int id_loja, int id_lojaDestino) throws Exception {
        id_lojaDestino = id_loja;

        try {

            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberClienteMilenio(id_lojaDestino);

            new ReceberCreditoRotativoDAO().salvarMilenio(vReceberCreditoRotativo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarAcertoReceberCreditoRotativo(int id_loja, int id_lojaDestino) throws Exception {
        id_lojaDestino = id_loja;

        try {

            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarAcertarReceberCreditoRotativoMilenio(id_lojaDestino);

            new ReceberCreditoRotativoDAO().acertarCreditoRotativoMilenio(vReceberCreditoRotativo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberCheque(int id_loja, int id_lojaDestino, Date data) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Receber Cheque...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_lojaDestino, data);

            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);

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
    
    // FUNÇÃO
    private int retornarAliquotaICMSMilenio(String codTrib) {

        int retorno = 0;

        if (null != codTrib) {
            switch (codTrib) {
                case "F00":
                    retorno = 7;
                    break;
                case "F01":
                    retorno = 19;
                    break;
                case "F04":
                    retorno = 7;
                    break;
                case "F12":
                    retorno = 7;
                    break;
                case "F17":
                    retorno = 7;
                    break;
                case "F25":
                    retorno = 7;
                    break;
                case "F27":
                    retorno = 7;
                    break;
                case "F29":
                    return 34;
                case "F30":
                    retorno = 7;
                    break;
                case "F58":
                    retorno = 7;
                    break;
                case "F61":
                    retorno = 7;
                    break;
                case "F99":
                    retorno = 7;
                    break;
                case "I00":
                    retorno = 6;
                    break;
                case "I99":
                    retorno = 6;
                    break;
                case "N99":
                    retorno = 21;
                    break;
                case "R29":
                    retorno = 34;
                    break;
                case "R41":
                    retorno = 8;
                    break;
                case "R58":
                    retorno = 26;
                    break;
                case "T01":
                    retorno = 20;
                    break;
                case "T02":
                    retorno = 20;
                    break;
                case "T03":
                    retorno = 8;
                    break;
                case "T06":
                    retorno = 20;
                    break;
                case "T07":
                    retorno = 25;
                    break;
                case "T12":
                    retorno = 24;
                    break;
                case "T17":
                    retorno = 20;
                    break;
                case "T25":
                    retorno = 3;
                    break;
                case "T27":
                    retorno = 32;
                    break;
                case "T99":
                    retorno = 20;
                    break;
                    
                case "F18":    
                    retorno = 7;
                    break;
                case "F59":    
                    retorno = 7;
                    break;
                    
                case "R02":    
                    retorno = 4; 
                    break;
                case "R33":    
                    retorno = 9;
                    break;
                case "R59":    
                    retorno = 4; 
                    break;
                case "T18":    
                    retorno = 2;
                    break;
                case "T23":    
                    retorno = 23; break;
                default:
                    retorno = 8;
                    break;
            }
        }

        return retorno;
    }

    public void importarTipoEmbalagemBalanca() throws Exception {
        ProgressBar.setStatus("Carregando dados...Tipo Embalagem da balança...");
        
        List<ProdutoVO> vProdutos = carregarEmbalagemDaBalanca();
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("create table if not exists implantacao.ajuste_unidade_balanca (id_produto integer, unidade varchar(10), pesavel boolean);");
            stm.execute("delete from implantacao.ajuste_unidade_balanca;");
            for (ProdutoVO produto: vProdutos) {
                stm.execute("insert into implantacao.ajuste_unidade_balanca (id_produto, unidade, pesavel) values (" +
                        produto.getId() + "," +
                        "'" + produto.getIdTipoEmbalagem() + "'," +
                        produto.isPesavel() +
                        ");");
            }
        }
        
        new ProdutoDAO().alterarTipoEmbalagem(vProdutos);
    }

    private List<ProdutoVO> carregarEmbalagemDaBalanca() throws Exception{
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "	r.REFPLU, \n" +
                "	r.REFPLUDV,\n" +
                "	PROUNDVDA\n" +
                "from \n" +
                "	produto p\n" +
                "	inner join REFERENCIA r on\n" +
                "		P.PROCOD  = R.PROCOD\n" +
                "order by \n" +
                "	r.REFPLU, \n" +
                "	r.REFPLUDV"
            )) {
                while (rst.next()) {
                    int idProduto = Integer.parseInt(rst.getString("REFPLU") + "" + rst.getString("REFPLUDV"));
                        if (balanca.containsKey(idProduto)) {
                        ProdutoVO produto = new ProdutoVO();
                        produto.setId(idProduto);
                        produto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("PROUNDVDA")));
                        produto.setPesavel(produto.getIdTipoEmbalagem() != 4);
                        result.add(produto);
                    }
                }
            }
        }
        return result;
    }

    public void importarCodigoAnterior() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
            Map<Long, ProdutoVO> vEanProdutoMilenio = carregarEanProdutoMilenio();

            ProgressBar.setMaximum(vEanProdutoMilenio.size());

            for (Long keyId : vEanProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEanProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            new CodigoAnteriorDAO().corrigirEANCodigoAnterior(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueSomando(int idLojaVR, int idLojaCliente, int idLocalEstoque) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...Somando...");
            Map<Integer, ProdutoVO> vEstoqueProdutoMilenio = carregarEstoqueProdutoMilenio(idLojaCliente, idLocalEstoque);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProdutoMilenio.size());

            for (Integer keyId : vEstoqueProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEstoqueProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            // ESTA ROTINA SOMA O VALOR ATUAL DE ESTOQUE MAIS O VALOR CARREGADO NA LISTA
            produto.alterarEstoqueProdutoSomando(vProdutoNovo, idLojaVR);            
            // ESTA ROTINA SOMA O VALOR ATUAL DE ESTOQUE MAIS O VALOR CARREGADO NA LISTA

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void setUtilizarREFCODINT(boolean utilizarREFCODINT) {
        this.utilizarREFCODINT = utilizarREFCODINT;
    }
    
    private List<ProdutoVO> carregarIcms(int idLojaVR, int idLojaCliente, OpcaoProdutoSQLQuery opcao) throws Exception {
        List<ProdutoVO> result = new ArrayList();
        String ufEmpresa = "CE";
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select LOJEST from loja WHERE LOJCOD = " + idLojaCliente
            )) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("LOJEST");
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    opcao.getSQL(idLojaVR, idLojaCliente, utilizarREFCODINT)
            )) {                
                while (rst.next()) {
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();  
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior); 
                    oProduto.getvAliquota().add(oAliquota);
                    
                    //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                    String strId;
                    if (!this.utilizarREFCODINT) {
                        strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                    } else {
                        strId = Utils.formataNumero(rst.getString("REFCODINT"));
                        if (strId.equals("0")) {
                            strId = Utils.formataNumero(rst.getString("REFPLU") + rst.getString("REFPLUDV"));
                        }
                    }
                    long id = Long.parseLong(strId);
                    //</editor-fold>
                    
                    oProduto.idDouble = id;

                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO ICMS">
                    String tribAliquota;
                    if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                        tribAliquota = rst.getString("TRBID").trim();
                    } else {
                        tribAliquota = "999";
                    }
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(23);}
                    oAliquota.setIdAliquotaDebito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCredito(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaCreditoForaEstado(retornarAliquotaICMSMilenio(tribAliquota));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(retornarAliquotaICMSMilenio(tribAliquota));
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="CODIGO ANTERIOR">
                    oCodigoAnterior.setCodigoanterior(id);                   
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("TRBID"));
                    //</editor-fold>
                    
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarIcms(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...ICMS.....");
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for(ProdutoVO prod: carregarIcms(idLojaVR, idLojaCliente, OpcaoProdutoSQLQuery.ATE_999999)) {
            auxiliar.put(prod.idDouble, prod);
        }
        
        produto.alterarICMSProduto(new ArrayList(auxiliar.values()));
    }

    
    private enum OpcaoProdutoSQLQuery {
        ATE_999999 {
            @Override
            public String getSQL(int idLojaVR, int idLojaCliente, boolean utilizarREFCODINT) {
                String where = !utilizarREFCODINT ? "WHERE R.REFPLU <= 999999\n" : "WHERE CAST(REPLACE(REPLACE(R.REFCODINT,'~',''),'.','') AS BIGINT) <= 999999\n";
                return 
                    "SELECT\n" +
                    "  R.*,\n" +
                    "  P.*,\n" +
                    "  E.*,\n" +
                    "  PR.*,\n" +
                    "  C.CSTREP,\n" +
                    "  COALESCE(PROMRG1, 0) AS PROMRG1,\n" +
                    "  CASE WHEN ( R.refdatfimlin IS NULL OR R.refdatfimlin > (SELECT Max(abrdat) FROM   [SysacME].dbo.abertura) ) THEN 1 ELSE 0 END AS status\n" +
                    "FROM [SysacME].[dbo].REFERENCIA R\n" +
                    "INNER JOIN [SysacME].[dbo].PRODUTO P\n" +
                    "  ON P.PROCOD = R.PROCOD\n" +
                    "LEFT JOIN [SysacME].[dbo].EAN E\n" +
                    "  ON E.REFPLU = R.REFPLU\n" +
                    "LEFT JOIN [SysacME].[dbo].[PRECO] PR\n" +
                    (MilenioDAO.utilizaREFPLU ? "  ON PR.REFPLU = R.REFPLU\n" : "  ON PR.PROCOD = R.PROCOD\n") +
                    "  AND PR.LOJCOD = " + idLojaCliente + "\n" +
                    "LEFT JOIN [SysacME].[dbo].[CUSTO] C\n" +
                    "  ON C.REFPLU = E.REFPLU\n" +
                    //where +
                    "ORDER BY r.REFDES ASC";
            }
        },
        MAIOR_Q_999999 {
            @Override
            public String getSQL(int idLojaVR, int idLojaCliente, boolean utilizarREFCODINT) {
                String where = !utilizarREFCODINT ? "WHERE R.REFPLU > 999999\n" : "WHERE CAST(REPLACE(REPLACE(R.REFCODINT,'~',''),'.','') AS BIGINT) > 999999\n";
                return 
                    "SELECT\n" +
                    "  *,\n" +
                    "  CASE WHEN ( R.refdatfimlin IS NULL OR R.refdatfimlin > (SELECT Max(abrdat) FROM   [SysacME].dbo.abertura) ) THEN 1 ELSE 0 END AS status\n" +
                    "FROM [SysacME].[dbo].REFERENCIA R\n" +
                    "INNER JOIN [SysacME].[dbo].PRODUTO P\n" +
                    "  ON P.PROCOD = R.PROCOD\n" +
                    "LEFT JOIN [SysacME].[dbo].EAN E\n" +
                    "  ON E.REFPLU = R.REFPLU\n" +
                    "LEFT JOIN [SysacME].[dbo].[PRECO] PR\n" +
                    "  ON PR.PROCOD = R.PROCOD\n" +
                    "  AND PR.LOJCOD = " + idLojaCliente + "\n" +
                    "LEFT JOIN [SysacME].[dbo].[CUSTO] C\n" +
                    "  ON C.REFPLU = E.REFPLU\n" +
                    where +
                    "ORDER BY r.REFDES ASC";
            }
        };
        
        public abstract String getSQL(int idLojaVR, int idLojaCliente, boolean utilizarREFCODINT);
    }
    
    
}
