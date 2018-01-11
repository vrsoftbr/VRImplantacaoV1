package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.ProdutoAutomacaoDAO;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.MercadologicoMapDAO;
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
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ProdutosUnificacaoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public abstract class AbstractIntefaceDao {
    
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        
        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(
                    "SQL AQUI"
            )) {
                while (rst.next()) {
                    MercadologicoVO merc = new MercadologicoVO();
                    merc.setMercadologico1(rst.getInt("merc1"));
                    merc.setDescricao(rst.getString("merc1_desc"));
                    if (nivel > 1) {
                        merc.setMercadologico2(rst.getInt("merc2"));
                        merc.setDescricao(rst.getString("merc2_desc"));
                    }
                    if (nivel > 2) {
                        merc.setMercadologico3(rst.getInt("merc3"));
                        merc.setDescricao(rst.getString("merc3_desc"));
                    }
                    merc.setNivel(nivel);
                    
                    result.add(merc);
                }
            }
        }
        
        return result;
    }  
    
    public List<ReceberChequeVO> carregarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(
                    "SQL AQUI"
            )) {
                while (rst.next()) {

                    ReceberChequeVO oReceberCheque = new ReceberChequeVO();                
                    oReceberCheque.setId_loja(idLojaVR);
                    oReceberCheque.setEcf(rst.getInt(null));
                    oReceberCheque.setId_tipoalinea(rst.getInt(null));
                    oReceberCheque.setData(Utils.formatDate(rst.getDate(null)));
                    oReceberCheque.setDatadeposito(Utils.formatDate(rst.getDate(null)));
                    oReceberCheque.setCpf(Utils.stringToLong(rst.getString(null)));
                    oReceberCheque.setNumerocheque(rst.getInt(null));
                    int banco = Utils.retornarBanco(rst.getInt(null));
                    if (banco == 0) {banco = 804;}
                    oReceberCheque.setId_banco(banco);
                    oReceberCheque.setAgencia(rst.getString(null));
                    oReceberCheque.setConta(rst.getString(null));
                    oReceberCheque.setNumerocupom(rst.getInt(null));
                    oReceberCheque.setValor(rst.getDouble(null));
                    oReceberCheque.setObservacao(rst.getString(null));
                    oReceberCheque.setRg(rst.getString(null));
                    oReceberCheque.setTelefone(rst.getString(null));
                    oReceberCheque.setNome(rst.getString(null));
                    oReceberCheque.setId_tipoinscricao(rst.getInt(null));
                    oReceberCheque.setValorjuros(rst.getDouble(null));
                    oReceberCheque.setValorinicial(rst.getDouble(null));

                    vReceberCheque.add(oReceberCheque);
                }
            }
        }
        return vReceberCheque;
    }
    
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = null/*ConexaoPostgres.getConexao().createStatement()*/) {   
            try (ResultSet rst = stm.executeQuery(
                " COLOCAR O SQL AQUI"
            )) {
                
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {      
                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);  
                                      
                    oProduto.setId(rst.getInt("id"));
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
                    if (rst.getString("dataCadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("dataCadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("mercadologico1"));
                    oProduto.setMercadologico2(rst.getInt("mercadologico2"));
                    oProduto.setMercadologico3(rst.getInt("mercadologico3"));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("ncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(9701);
                        oProduto.setNcm2(90);
                        oProduto.setNcm3(0);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    oProduto.setIdFamiliaProduto(rst.getString("") != null ? rst.getInt("") : -1);
                    oProduto.setMargem(rst.getDouble(""));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarra;                    
                    if(!rst.getBoolean("E BALANCA?")) {
                        codigoBarra = rst.getLong("") <= Integer.MAX_VALUE ? rst.getInt("") : -1;
                    } else {
                        codigoBarra = (long) oProduto.getId();
                    }
                    
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt(""));
                        oProduto.eBalanca = true;
                        
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
                        oProduto.setValidade(rst.getInt(""));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("")));
                        
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }                    
                    oAutomacao.setQtdEmbalagem(1);
                                        
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());                    
                                     
                    
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
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliquido"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("piscofins_cst_sai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("piscofins_cst_ent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("piscofins_natrec")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("minimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("maximo"));                   

                    String uf = rst.getString("uf");
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(uf));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(rst.getLong("id"));
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(rst.getLong("ean"));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("piscofins_cst_sai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("piscofins_cst_ent"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("piscofins_natrec"));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("icms_cst"));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                }                
            }
        } 
        
        return vProduto;
    }
    
    @Deprecated
    public Map<Integer, ProdutoVO> carregarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();
        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            int id = (int) (vo.getId() != 0 ? vo.getId() : vo.idDouble);
            result.put(id, vo);
        }
        
        return result;
    }
    
    public Map<Long, ProdutoVO> carregarProduto2(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            long id = (long)(vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId());
            result.put(id, vo);
        }
        
        return result;
    }
    
    public Map<Long, ProdutoVO> carregarProdutoIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            
            if (vo.getvAutomacao() != null) {
                for (ProdutoAutomacaoVO oAutomacao: vo.getvAutomacao()) {
                    if (
                            (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7) &&
                            (String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14)
                    ) {
                        ProdutosUnificacaoVO oProdutosUnificacaoVO = new ProdutosUnificacaoVO();

                        double id;
                        if (vo.getIdDouble() > 0) {
                            id = vo.getIdDouble();
                        } else {
                            id = vo.getId();
                        }
                        
                        oProdutosUnificacaoVO.barras = oAutomacao.getCodigoBarras();
                        oProdutosUnificacaoVO.codigoanterior = id;
                        oProdutosUnificacaoVO.descricao = vo.getDescricaoCompleta();

                        vo.vProdutosUnificacao.add(oProdutosUnificacaoVO);
                        
                        result.put(oAutomacao.getCodigoBarras(), vo);
                    }
                }
            } else if (
                            (String.valueOf(vo.getCodigoBarras()).length() >= 7) &&
                            (String.valueOf(vo.getCodigoBarras()).length() <= 14)
                    ) {
                    ProdutosUnificacaoVO oProdutosUnificacaoVO = new ProdutosUnificacaoVO();

                    double id;
                    if (vo.getIdDouble() > 0) {
                        id = vo.getIdDouble();
                    } else {
                        id = vo.getId();
                    }

                    oProdutosUnificacaoVO.barras = vo.getCodigoBarras();
                    oProdutosUnificacaoVO.codigoanterior = id;
                    oProdutosUnificacaoVO.descricao = vo.getDescricaoCompleta();

                    vo.vProdutosUnificacao.add(oProdutosUnificacaoVO);
                    
                    result.put(vo.getCodigoBarras(), vo);
            }     
        }
        
        return result;
    }
    
    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(
                    "INCLUIR O SQL"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id_produto"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    String ean = String.valueOf(oAutomacao.getCodigoBarras());
                    if ((ean.length() >= 7) &&
                        (ean.length() <= 14)) {                                             
                        result.put(oAutomacao.getCodigoBarras(), oProduto);
                    }                    
                }                 
            }
        }
            
        return result;
    }
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(                    
                "COLOQUE SEU SQL AQUI"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro;
                    
                    if ((rst.getString("datacadastro") != null)
                            && (!rst.getString("datacadastro").isEmpty())) {
                        datacadastro = rst.getDate("datacadastro");                    
                    } else {
                        datacadastro = new Date(new java.util.Date().getTime()); 
                    }

                    oFornecedor.setId(rst.getInt("id"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setCodigoanterior(rst.getInt("id"));
                    oFornecedor.setRazaosocial(rst.getString("razao"));
                    oFornecedor.setNomefantasia(rst.getString("fantasia"));
                    oFornecedor.setEndereco(rst.getString("endereco"));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade"), rst.getString("estado")));                     
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("estado")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setObservacao("IMPORTADO VR");
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oFornecedor.setId_tipoindicadorie();
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = null) {//ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "INSIRA SEU SQL AQUI"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf")));
                    vo.setCodigoexterno(rst.getString("codigoexterno"));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }
    
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = null) {                    
            try (ResultSet rst = stm.executeQuery(               
                "INSIRA O SQL AQUI"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));
                    oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("res_cidade"), rst.getString("res_uf")));                     
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.getString("res_uf")));
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setTelefone2(rst.getString("fone2"));
                    oClientePreferencial.setCelular(rst.getString("celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                        oClientePreferencial.setCnpj(-1);
                    }
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("PRAZODIAS"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limitepreferencial"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR  - limite: " + String.format("%.02f", rst.getFloat("limite")));
                    oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(rst.getString("empresa"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    oClientePreferencial.setOrgaoemissor(rst.getString("ORGAOEMISSOR"));                   

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }   
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = null) {
            try ( ResultSet rst = stm.executeQuery(
                    "SQL AQUI"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt(""));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate(""));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("")));
                    oReceberCreditoRotativo.setValor(rst.getDouble(""));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString(""));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate(""));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }
    @Deprecated
    public Map<Integer, ProdutoVO> carregarICMS(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    @Deprecated
    public Map<Integer, ProdutoVO> carregarPisCofins(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, idLojaCliente);
    }
    
    public Map<Long, ProdutoVO> carregarICMS2(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto2(idLojaVR, idLojaCliente);
    }
    
    public Map<Long, ProdutoVO> carregarPisCofins2(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto2(idLojaVR, idLojaCliente);
    }
    
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = null) {//ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(rst.getInt("id"));
                    familiaVO.setIdLong(rst.getLong("id"));
                    familiaVO.setDescricao(rst.getString("descricao"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }
    
    public List<MercadologicoMapaVO> carregarMapeamentoDeMercadologico(int idLojaVR, int idLojaCliente) throws Exception{
        return new ArrayList<>();
    }
    
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        result.add(new ItemComboVO(1, "1 - LOJA PADRÃO"));
        return result;
    }
    
    public void importarMercadologico() throws Exception{
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);
        
        dao.salvarMax();
    }
    
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.salvar(new ArrayList<>(aux.values()), idLojaVR, vLoja);
    }
    
    public void importarProdutoMantendoCodigoDeBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");
      
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
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
        
        
        produto.usarCodigoBalancaComoID = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }
    
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarPrecoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }

    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Custo...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarCustoProdutoRapido(new ArrayList<>(aux.values()), idLojaVR);
    }

    public void importarEanProduto(int idLojaVR, int idLojaCliente) throws Exception{

        ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
        Map<Long, ProdutoVO> vEanProdutos = carregarEanProduto(idLojaVR, idLojaCliente);

        ProgressBar.setMaximum(vEanProdutos.size());

        new ProdutoAutomacaoDAO().salvar(new ArrayList<>(vEanProdutos.values()));
    }    
    
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception{
        
        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.alterarEstoqueProdutoRapido(new ArrayList<>(aux.values()), idLojaVR);
    }
    
    public void importarFornecedor() throws Exception {
        importarFornecedor(Global.idLojaFornecedor);
    }
    
    public void importarFornecedor(int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor, idLojaCliente);
        } else {
            new FornecedorDAO().salvar(vFornecedor, idLojaCliente);
        }
    }

    public void importarProdutoFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
        List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

        if (Global.compararCnpjProdUnificado) {
            new ProdutoFornecedorDAO().salvarCnpjFornecedorProdUnificacao(vProdutoFornecedor);
        } else {
            new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
        }
        
    }
    
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        
        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }

    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja, true);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, id_tipoembalagem, pesavel from produto p where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id)"
            )) {     
                while (rst.next()) {
                    idProduto    = Double.parseDouble(rst.getString("id"));

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
            }
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
    
    public void importarPisCofins(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Pis e Cofins...");
        Map<Long, ProdutoVO> vCodigoBarra = carregarPisCofins2(idLojaVR, idLojaCliente);

        produto.alterarPisCofinsProduto(new ArrayList<>(vCodigoBarra.values()));
    }
    
    public void importarICMS(int idLojaVR, int idLojaCliente) throws Exception {
        
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Pis e Cofins...");
        Map<Long, ProdutoVO> vCodigoBarra = carregarICMS2(idLojaVR, idLojaCliente);

        produto.alterarICMSProduto(new ArrayList<>(vCodigoBarra.values()));
    }
    
    @Deprecated
    public void importarFamiliaProduto() throws Exception {
        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

        new FamiliaProdutoDAO().salvar(vFamiliaProduto);
    }
    
    /**
     * Importa família de produtos utilizando a tabela implantacao.codantfamilia2.
     * @throws Exception 
     */
    public void importarFamiliaProdutoV2() throws Exception {
        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

        new FamiliaProdutoDAO().salvar2(vFamiliaProduto);
    }
    
    public void importarConvenio(int idLojaVR, int idLojaCliente) throws Exception {
        throw new UnsupportedOperationException("Função não suportada");
    }
    
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(
                    "COLOQUE O SQL AQUI"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }
    
    public void importarOfertas(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente);
        
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }
    
    public void importarMapeamentoDeMercadologico(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Mercadológico para mapeamento...");
        List<MercadologicoMapaVO> vMapa = carregarMapeamentoDeMercadologico(idLojaVR, idLojaCliente);

        new MercadologicoMapDAO().salvar(vMapa, true);
    }
    
    public void importarCheques(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cheque Receber...");
        List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(idLojaVR, idLojaCliente);

        new ReceberChequeDAO().salvar(vReceberCheque,idLojaVR);
    }
    
    /**
     * Efetua a importação de clientes eventuais.
     * @param idLojaVR código da loja de destino VR.
     * @param idLojaCliente código da loja do cliente.
     * @throws Exception 
     */
    public void importarClienteEventual(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carrando dados...Cliente Eventual...");
        List<ClienteEventualVO> vEventuais = carregarClienteEventual(idLojaVR, idLojaCliente);
        new ClienteEventuallDAO().salvar(vEventuais);
    }

    public List<ClienteEventualVO> carregarClienteEventual(int idLojaVR, int idLojaCliente) throws Exception {
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();
        try (Statement stm = null) {
            try (ResultSet rst = stm.executeQuery(
                    "SCRIPT DE IMPORTACAO"
            )) {
                while (rst.next()) {
                    ClienteEventualVO oClienteEventual = new ClienteEventualVO();

                    oClienteEventual.setId(rst.getInt("id"));
                    oClienteEventual.setNome(rst.getString("nome"));
                    oClienteEventual.setEndereco(rst.getString("endereco"));
                    oClienteEventual.setBairro(rst.getString("bairro"));
                    oClienteEventual.setId_estado(rst.getInt("id_estado"));
                    oClienteEventual.setId_municipio(rst.getInt("id_municipio"));
                    oClienteEventual.setCep(rst.getLong("id_cep"));
                    oClienteEventual.setTelefone(rst.getString("fone1"));
                    oClienteEventual.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClienteEventual.setCnpj(Utils.stringToLong(rst.getString("cnpj")));
                    oClienteEventual.setDatacadastro(rst.getDate("datacadastro"));
                    oClienteEventual.setEmail(rst.getString("email"));
                    oClienteEventual.setLimitecompra(rst.getDouble("limite"));
                    oClienteEventual.setFax(rst.getString("fax"));
                    oClienteEventual.setBloqueado((rst.getInt("bloqueado") == 1));
                    oClienteEventual.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClienteEventual.setTelefone2(rst.getString("fone2"));
                    oClienteEventual.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oClienteEventual.setNumero(rst.getString("numero"));
                    oClienteEventual.setId_tipoinscricao(String.valueOf(oClienteEventual.getCnpj()).length() <= 9 ? 1 : 0);

                    vClienteEventual.add(oClienteEventual);
                }
            }
        } 
        return vClienteEventual;
    }
    
    public void integrarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        List<ProdutoVO> vProdutoAlterado = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Long, ProdutoVO> vProdutoOrigem = carregarProdutoIntegracao(idLojaVR, idLojaCliente); //carregarProdutoOrigemVrToVr(i_idLojaOrigem, i_connOrigem);
            Map<Long, Long> vProdutoDestino = new ProdutoDAO().carregarCodigoBarras();
            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Loja Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoOrigem.size() + vProdutoDestino.size());

            for (Long keyCodigoBarra : vProdutoOrigem.keySet()) {
                if (vProdutoDestino.containsKey(keyCodigoBarra)) {
                    long codigoProduto = vProdutoDestino.get(keyCodigoBarra);
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    oProduto.id = (int) codigoProduto;
                    vProdutoAlterado.add(oProduto);
                } else {
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);
                    //oProduto.id = 0;
                    oProduto.idProdutoVasilhame = -1;
                    oProduto.idFamiliaProduto = -1;
                    oProduto.idFornecedorFabricante = Global.idFornecedor;
                    oProduto.excecao = -1;
                    oProduto.idTipoMercadoria = -1;
                    vProdutoNovo.add(oProduto);                    
                }
                ProgressBar.next();
            }

            if (!vProdutoAlterado.isEmpty()) {
                new ProdutoDAO().salvar(vProdutoAlterado, idLojaVR, true, null, true,0);
            }
            
            if (!vProdutoNovo.isEmpty()) {
                ProdutoDAO prod = new ProdutoDAO();
                prod.usarMercadologicoAcertar = true;
                prod.salvar(vProdutoNovo, idLojaVR, vLoja, true);
            }
        } catch (Exception e) {
            throw e;
        }
        
        
        /*.setStatus("Carregando dados...Integração....Produtos.....");
      
        Map<Long, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Long barra = vo.getvAutomacao().get(0).getCodigoBarras() > 0 ? vo.getvAutomacao().get(0).getCodigoBarras() : vo.getCodigoBarras();
            if (barra > 999999) {
                aux.put(barra, vo);
            }
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());

        List<ProdutoVO> normais = new ArrayList<>(aux.values());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;       
        produto.usarCodigoBalancaComoID = false;
        ProgressBar.setStatus("Carregando dados...Integração....Produtos");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja, true);*/
    }
    
    public void integrarFornecedorCpfCnpj (int idLojaVR) throws Exception {        
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...Cnpj");
            List<FornecedorVO> vFornecedor = carregarFornecedor();
            FornecedorDAO fornecedor = new FornecedorDAO();
            fornecedor.pidLoja = idLojaVR;
            fornecedor.salvarCnpj(vFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void integrarProdutoFornecedor(int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
        List<ProdutoFornecedorVO> produtoFornecedor = carregarProdutoFornecedor();
        ProdutoFornecedorDAO dao = new ProdutoFornecedorDAO();
        dao.idLojaVR = idLojaVR;
        dao.salvarComCnpj(produtoFornecedor, idLojaVR);
    }
    
    public void integrarClienteCpfCnpj (int idLoja, int idLojaCliente) throws Exception {        
        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vCliente = carregarCliente(idLojaCliente);
            new ClientePreferencialDAO().salvarCpf(vCliente, idLoja, idLojaCliente);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void integrarReceberClienteComCpf(int idLoja, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Cliente com Cpf...");
        List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

        new ReceberCreditoRotativoDAO().salvarComCnpj(vReceberCliente, idLoja);
    }

}
