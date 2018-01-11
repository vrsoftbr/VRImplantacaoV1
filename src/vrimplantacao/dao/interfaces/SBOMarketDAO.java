package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class SBOMarketDAO extends AbstractIntefaceDao {

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	g.CODGRUPO,\n" +
                    "	g.NOMEGRUPO,\n" +
                    "	sg.CODSUB,\n" +
                    "	sg.NOMESUB\n" +
                    "from\n" +
                    "	Grupo g\n" +
                    "	left join SubGrupo sg on g.CODGRUPO = sg.CODGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();   
                    
                    if (nivel == 1) {
                        vo.setMercadologico1(rst.getInt("CODGRUPO"));
                        vo.setMercadologico2(0);
                        vo.setMercadologico3(0);
                        vo.setMercadologico4(0);
                        vo.setMercadologico5(0);
                        vo.setDescricao(rst.getString("NOMEGRUPO"));
                    } else if (nivel == 2) {
                        vo.setMercadologico1(rst.getInt("CODGRUPO"));
                        if (!Utils.acertarTexto(rst.getString("CODSUB")).equals("")) {
                            vo.setMercadologico2(rst.getInt("CODSUB"));
                            vo.setDescricao(rst.getString("NOMESUB"));
                        } else {
                            vo.setMercadologico2(1);
                            vo.setDescricao(rst.getString("NOMEGRUPO"));
                        }
                        vo.setMercadologico3(0);
                        vo.setMercadologico4(0);
                        vo.setMercadologico5(0);
                    } else if (nivel == 3) {
                        vo.setMercadologico1(rst.getInt("CODGRUPO"));
                        if (!Utils.acertarTexto(rst.getString("CODSUB")).equals("")) {
                            vo.setMercadologico2(rst.getInt("CODSUB"));
                            vo.setDescricao(rst.getString("NOMESUB"));
                        } else {
                            vo.setMercadologico2(1);
                            vo.setDescricao(rst.getString("NOMEGRUPO"));
                        }
                        vo.setMercadologico3(1);
                        vo.setMercadologico4(0);
                        vo.setMercadologico5(0);
                    }
                    vo.setNivel(nivel);
                    result.add(vo);
                    /*if (result.add(vo)) {
                        Util.exibirMensagem(vo.toString(), "Adicionou");
                    }*/
                }
            }
        }
        
        return result;
    }
    
    @Override
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

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {   
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.Codigo id,\n" +
                "	p.CodigoBar,\n" +
                "	p.Descricao descricaocompleta,\n" +
                "	p.Descricao descricaoreduzida,\n" +
                "	p.Descricao descricaogondola,\n" +
                "	p.Ativo id_situacaocadastro,\n" +
                "	p.UltAltera datacadastro,\n" +
                "	p.Grupo,\n" +
                "	p.SubGrupo,\n" +
                "	p.Marca,\n" +
                "	p.CodGeneroProduto,\n" +
                "	cl.Valor ncm,\n" +
                "	cest.CEST,\n" +
                "	p.MargemV margem,\n" +
                "	p.Balanca,\n" +
                "	p.DiasValidade validade,\n" +
                "	p.Unidade id_tipoembalagem,\n" +
                "	ct.SitTribPIS,\n" +
                "	ct.SitTribPISEnt,\n" +
                "	ct.NatRecPIS,\n" +
                "	p.PVista preco,\n" +
                "	p.Custo,\n" +
                "	p.EstoqueMin,\n" +
                "	p.Estoque,\n" +
                "	ctuf.CodUF,\n" +
                "	ctuf.SitTribICMS icms_cst,\n" +
                "	ctuf.AliqICMS icms_aliq,\n" +
                "	ctuf.Reducao icms_red\n" +
                "from\n" +
                "	Produto p	\n" +
                "	left join ClassFiscal cl on p.CodClassFiscal = cl.Codigo\n" +
                "	left join TabelaCEST cest on p.CodCEST = cest.CodCEST\n" +
                "	left join CalcTributo ct on ct.CodClassFiscal = cl.Codigo and ct.FinalidadeConfig != 'N'\n" +
                "	left join CalcTributoUF ctuf on ct.CodCalcTributo = ctuf.CodCalcTributo and ctuf.CodUF = 35\n" +
                "order by\n" +
                "	id"
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
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    if (rst.getString("datacadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("datacadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("Grupo"));
                    oProduto.setMercadologico2(rst.getInt("SubGrupo"));
                    oProduto.setMercadologico3(0);
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
                    

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarras = Utils.stringToLong(rst.getString("CodigoBar"), -2);
                    
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (oProduto.getId() <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarras);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras(codigoBarras);                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
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
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                        
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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("SitTribPIS")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("SitTribPISEnt")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("NatRecPIS")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("Custo"));
                    oComplemento.setCustoSemImposto(rst.getDouble("Custo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("Estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("EstoqueMin"));
                    oComplemento.setEstoqueMaximo(0);                   

                    String uf = "SP";
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(uf));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_red")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_red")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_red")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_red")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getInt("icms_aliq"), rst.getInt("icms_red")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(-2);
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("SitTribPIS"));
                    oCodigoAnterior.setPiscofinscredito(Utils.stringToInt(rst.getString("SitTribPISEnt")));
                    oCodigoAnterior.setNaturezareceita(Utils.stringToInt(rst.getString("NatRecPIS")));
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

    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CodigoBar ean, Codigo id_produto, Unidade id_tipoembalagem from Produto p\n" +
                    "union\n" +
                    "select ca.CodBar ean, ca.CodProd id_produto, p.Unidade id_tipoembalagem from CodAdicional ca join Produto p on ca.CodProd = p.Codigo"
            )) {            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id_produto"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(1);
                    
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

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "	f.CODIGO id,\n" +
                "	f.RAZAO razao,\n" +
                "	f.FANTASIA fantasia,\n" +
                "	CURRENT_TIMESTAMP as datacadastro,\n" +
                "	f.ENDERECO,\n" +
                "	'0' as numero,\n" +
                "	f.COMPLEMENTO,\n" +
                "	f.BAIRRO,\n" +
                "	f.CIDADE,\n" +
                "	f.UF,\n" +
                "	f.CEP,\n" +
                "	isnull(f.DDD,'') + f.FONE fone1,\n" +
                "	isnull(f.DDD,'') + f.CELULAR celular,\n" +
                "	isnull(f.DDD,'') + f.FAX fax,\n" +
                "	f.CNPJ,\n" +
                "	f.INSC_ESTA inscricaoestadual,\n" +
                "	f.EMAIL as observacao,\n" +
                "	1 as id_situacaocadastro\n" +
                "from\n" +
                "	FORNECE f\n" +
                "order by\n" +
                "	codigo	"
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
                    oFornecedor.setBairro(rst.getString("bairro"));
                    String uf = Utils.acertarTexto(rst.getString("UF"));
                    String cidade = Utils.acertarTexto(rst.getString("CIDADE"));
                    oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oFornecedor.setCep(Utils.formatCep(rst.getString("cep")));
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(uf));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setId_tipoinscricao(oFornecedor.getInscricaoestadual().length() > 9 ? 0 : 1);
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setObservacao(rst.getString("observacao"));
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setFax(rst.getString("fax"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        Global.idEstado = 35;
        Global.idMunicipio = 3552700;
        
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "	c.CODIGO id,\n" +
                "	c.NOME,\n" +
                "	c.ENDERE,\n" +
                "	c.NumLogr,\n" +
                "	c.COMPLE,\n" +
                "	c.BAIRRO,\n" +
                "	c.ESTADO,\n" +
                "	c.CIDADE,\n" +
                "	c.CEP,\n" +
                "	c.FONE,\n" +
                "	c.INSCRG,\n" +
                "	c.CPF,\n" +
                "	c.CGC,\n" +
                "	c.SEXO,\n" +
                "	c.EMAIL,\n" +
                "	c.LIMITE,\n" +
                "	c.FONECOMP1,\n" +
                "	c.FONECOMP2,\n" +
                "	c.FONECOMP3,\n" +
                "	c.CELULA,\n" +
                "	c.BloqueiaCheque,\n" +
                "	c.BloqueiaFatura,\n" +
                "	c.OBS,\n" +
                "	c.EMAIL,\n" +
                "	c.DTNASCI,\n" +
                "	c.DTCADAS,\n" +
                "	c.ATIVIDADE cargo,\n" +
                "	c.CONJUGE\n" +
                "from\n" +
                "	Cliente c\n" +
                "order by\n" +
                "	c.CODIGO"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("ENDERE"));
                    oClientePreferencial.setNumero(rst.getString("NumLogr"));
                    oClientePreferencial.setComplemento(rst.getString("COMPLE"));
                    oClientePreferencial.setBairro(rst.getString("BAIRRO"));
                    String uf = Utils.acertarTexto(rst.getString("ESTADO"));
                    String cidade = Utils.acertarTexto(rst.getString("CIDADE"));
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(uf));
                    oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(cidade, uf));
                    oClientePreferencial.setCep(Utils.formatCep(rst.getString("CEP")));
                    oClientePreferencial.setTelefone(rst.getString("FONE"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("INSCRG"));
                    if (!Utils.formataNumero(rst.getString("CPF")).equals("0")) {
                        oClientePreferencial.setCnpj(rst.getString("CPF"));
                    } else {
                        oClientePreferencial.setCnpj(rst.getString("CGC"));
                    }
                    
                    oClientePreferencial.setSexo(Utils.acertarTexto(rst.getString("SEXO")).equals("F") ? 0 : 1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("DTCADAS"));
                    oClientePreferencial.setEmail(rst.getString("EMAIL"));
                    oClientePreferencial.setValorlimite(rst.getDouble("LIMITE"));
                    boolean bloqueado = rst.getInt("BloqueiaFatura") != 0 || rst.getInt("BloqueiaCheque") != 0;
                    oClientePreferencial.setBloqueado(bloqueado);
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setTelefone2(rst.getString("FONECOMP1"));
                    oClientePreferencial.setTelefone3(Utils.acertarTexto(rst.getString("FONECOMP2")).isEmpty() ? rst.getString("FONECOMP3") : rst.getString("FONECOMP2"));                    
                    oClientePreferencial.setCelular(rst.getString("CELULA"));
                    oClientePreferencial.setObservacao("IMPORTADO VR " + rst.getString("nome") + "      " + rst.getString("OBS"));
                    oClientePreferencial.setDatanascimento(rst.getDate("DTNASCI"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	r.CODIGO,\n" +
                    "	r.CLIENTE,\n" +
                    "	c.CPF,\n" +
                    "	c.CGC,\n" +
                    "	r.EMISSAO,\n" +
                    "	r.JUROS,\n" +
                    "	r.VALPAGO,\n" +
                    "	r.NOTAPEDIDO,\n" +
                    "	r.FATURA,\n" +
                    "	r.OBS,\n" +
                    "	r.VENCIMENTO,\n" +
                    "	r.UltPag\n" +
                    "from \n" +
                    "	receber r\n" +
                    "	join Cliente c on r.CLIENTE = c.CODIGO\n" +
                    "\n" +
                    "where \n" +
                    "	r.BAIXA = 0\n" +
                    "order by\n" +
                    "	r.CODIGO"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("CLIENTE"));
                    if (Utils.formataNumero(rst.getString("CGC")).equals("")) {
                        oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("CPF")));
                    } else {
                        oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("CGC")));
                    }                    
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("EMISSAO"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("NOTAPEDIDO")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("FATURA"));
                    oReceberCreditoRotativo.setValorjuros(rst.getDouble("JUROS"));
                    oReceberCreditoRotativo.setValormulta(0);
                    oReceberCreditoRotativo.setValorPago(rst.getDouble("VALPAGO"));
                    oReceberCreditoRotativo.setDataPagamento(rst.getDate("UltPag"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString("OBS"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("VENCIMENTO"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
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
        
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }
    
    
    
    
    
}
