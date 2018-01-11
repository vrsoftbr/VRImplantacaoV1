package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 * Classe de importar o sistema SysMoura
 * @author Leandro
 */
public class SysMouraDAO extends AbstractIntefaceDao {
    
    private int depositoId = 1;

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo as id_merc1, 1 as id_merc2, 1 as id_merc3, "
                            + "Descricao from Grupo_Produto order by Codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO mercadologico = new MercadologicoVO();
                    mercadologico.setMercadologico1(rst.getInt("id_merc1"));
                    mercadologico.setDescricao(rst.getString("Descricao"));
                    if (nivel > 1) {
                        mercadologico.setMercadologico2(rst.getInt("id_merc2"));
                    }
                    if (nivel > 2) {
                        mercadologico.setMercadologico3(rst.getInt("id_merc3"));
                    }
                    mercadologico.setNivel(nivel);
                    
                    result.add(mercadologico);
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
            String ufEmpresa = "CE";
            
            try (ResultSet rst = stm.executeQuery("select c.Estado from empresa e join Cidade c on e.Cidade = c.Codigo where e.codigo = " + idLojaCliente)) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("Estado");
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                "declare @primeirocadastro date;\n" +
                "\n" +
                "select @primeirocadastro = min(p.Data_Cadastro) from produto p\n" +
                "\n" +
                "select\n" +
                "	p.codigo id,\n" +
                "	p.nome descricaocompleta,\n" +
                "	case when ltrim(rtrim(p.Descricao_Reduzida)) = '' then p.nome else p.Descricao_Reduzida end descricaoreduzida,\n" +
                "	p.nome descricaogondola,\n" +
                "	p.status id_situacaocadastral,\n" +
                "	isnull(p.Data_Cadastro, @primeirocadastro) datacadastro,\n" +
                "	p.grupo mercadologico1,\n" +
                "	1 as mercadologico2,\n" +
                "	1 as mercadologico3,\n" +
                "	p.ncm,\n" +
                "	p.Codigo_CEST cest,\n" +
                "	p.Margem,\n" +
                "	p.Quantidade qtdEmbalagem,\n" +
                "	p.Codigo_Barra ean,\n" +        
                "	p.balanca e_balanca,\n" +
                "	p.Validade,\n" +
                "	p.Unidade id_tipoembalagem,\n" +
                "	p.Peso_Produto peso_bruto,\n" +
                "	p.Peso_Produto peso_liquido,\n" +
                "	fs.ST_PIS pisconfinssaida,\n" +
                "	fs.ST_PIS_Entrada pisconfisentrada,\n" +
                "	p.Codigo_Incidencia_Monofasica pisconfinsnatureza,\n" +
                "	fs.CST icms_cst,\n" +
                "	fs.Aliquota_ICMS icms_aliquota,\n" +
                "	0 icms_reducao,\n" +
                "	est.Qtde estoque,\n" +
                "	p.Estoque_maximo,\n" +
                "	p.Estoque_minimo,\n" +
                "	p.Preco_Produto preco,\n" +
                "	p.Preco_Custo custo\n" +
                "from \n" +
                "	Produto p\n" +
                "	join Produto_Regra_Imposto imp on p.Codigo = imp.Produto\n" +
                "	join Fiscal_Regra_Imposto fs on imp.Tipo_Regra_Imposto = fs.Tipo_Regra_Imposto\n" +
                "	left join Estoque est on p.Codigo = est.Produto and est.Deposito = " + depositoId + "\n" +
                "order by \n" +
                "	p.codigo;"
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
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_situacaocadastral"));
                    if (rst.getString("datacadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("datacadastro")));
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
                    
                    /*
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    */
                    

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(rst.getDouble("Margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarra = Utils.stringToLong(rst.getString("ean"), -2);
                    
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get(rst.getInt("id"));
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("Validade"));
                        
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
                        oProduto.setValidade(rst.getInt("Validade"));
                        oProduto.setPesavel(false); 
                        
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
                    oProduto.setPesoBruto(rst.getDouble("peso_bruto"));
                    oProduto.setPesoLiquido(rst.getDouble("peso_liquido"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("pisconfinssaida")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("pisconfisentrada")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("pisconfinsnatureza")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custo"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("Estoque_minimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("Estoque_maximo"));                   

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliquota"), rst.getInt("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliquota"), rst.getInt("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliquota"), rst.getInt("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliquota"), rst.getInt("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(ufEmpresa, rst.getInt("icms_cst"), rst.getInt("icms_aliquota"), rst.getInt("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Utils.stringToLong(rst.getString("ean"),-2));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("pisconfinssaida"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("pisconfisentrada"));
                    oCodigoAnterior.setNaturezareceita(Utils.stringToInt(rst.getString("pisconfinsnatureza")));
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
                "select distinct \n" +
                "	codigo, \n" +
                "	ean,\n" +
                "	unidade\n" +
                "from\n" +
                "	(select \n" +
                "		codigo, \n" +
                "		codigo_barra ean, \n" +
                "		p.unidade \n" +
                "	from \n" +
                "		produto p\n" +
                "	union\n" +
                "	select \n" +
                "		pb.produto codigo, \n" +
                "		pb.codigo_barra ean, \n" +
                "		p.unidade \n" +
                "	from \n" +
                "		produto_barra pb join produto p on pb.produto = p.codigo) eans\n" +
                "order by codigo"
            )) {
                while (rst.next()) {
                    
                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setId(rst.getInt("codigo"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade")));
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
                "	p.Codigo id,\n" +
                "	p.Data_Cadastro dataCadastro,\n" +
                "	p.Nome razao,\n" +
                "	p.Nome_Fantasia fantasia,\n" +
                "	p.Endereco_Nome endereco,\n" +
                "	p.Numero,\n" +
                "	p.Complemento,\n" +
                "	p.Bairro,\n" +
                "	cast(cd.Codigo_Cidade_IBGE as integer) id_municipio,\n" +
                "	cd.Estado id_estado,\n" +
                "	p.Cep,\n" +
                "       \n" +
                "	p.Endereco_Pagamento cob_endereco,\n" +
                "	0 as cob_numero,	\n" +
                "	p.Bairro_Pagamento cob_bairro,	\n" +
                "	cast(cob_cd.Codigo_Cidade_IBGE as integer) cob_id_municipio,\n" +
                "	cob_cd.Estado cob_id_estado,\n" +
                "	p.Cep_Pagamento cob_cep,	\n" +
                "	\n" +
                "	p.Fone fone1,\n" +
                "	p.RG inscricaoestadual,\n" +
                "	p.cpf cnpj,\n" +
                "	p.Observacao,\n" +
                "	p.Fone2,\n" +
                "	p.Fax,\n" +
                "	coalesce(p.Email, p.email_utilizado, p.Emails_Promocionais) email,\n" +
                "	p.Contato observacoes,\n" +
                "	case when p.Inativo = 'N' then 1 else 0 end id_situacaocadastro\n" +
                "from\n" +
                "	Fornecedor f\n" +
                "	join Pessoa p on f.Pessoa = p.Codigo\n" +
                "	left join Cidade cd on p.Cidade = cd.Codigo\n" +
                "	left join Cidade cob_cd on p.Cidade = cob_cd.Codigo\n" +
                "order by\n" +
                "	p.Codigo;"
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
                    oFornecedor.setCodigoanterior(rst.getInt("id"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setRazaosocial(rst.getString("razao"));
                    oFornecedor.setNomefantasia(rst.getString("fantasia"));
                    
                    oFornecedor.setEndereco(rst.getString("endereco"));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    oFornecedor.setId_municipio(Utils.stringToInt(rst.getString("id_municipio")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));                    
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("id_estado")));
                    
                    oFornecedor.setEnderecocobranca(rst.getString("cob_endereco"));
                    oFornecedor.setNumerocobranca(rst.getString("cob_numero"));
                    oFornecedor.setBairrocobranca(rst.getString("cob_bairro"));
                    oFornecedor.setId_municipiocobranca(Utils.stringToInt(rst.getString("cob_id_municipio")));
                    oFornecedor.setCepcobranca(Utils.stringToLong(rst.getString("cob_cep"), 0));                    
                    oFornecedor.setId_estadocobranca(Utils.getEstadoPelaSigla(rst.getString("cob_id_estado")));
                    
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setId_tipoinscricao(oFornecedor.getInscricaoestadual().length() > 9 ? 0 : 1);
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    
                    
                    oFornecedor.setObservacao(rst.getString("observacao"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setFax(rst.getString("fax"));
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }   
    
    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	pf.produto id_produto,\n" +
                "	pf.fornecedor id_fornecedor,\n" +
                "	pf.Codigo_Produto_Fornecedor externo,\n" +
                "	cd.Estado uf\n" +
                "from \n" +
                "	Produto_Fornecedor pf\n" +
                "	join Fornecedor f on pf.Fornecedor = f.Pessoa\n" +
                "	join Pessoa p on f.Pessoa = p.Codigo\n" +
                "	join Cidade cd on p.Cidade = cd.Codigo\n" +
                "	join Produto pr on pf.Produto = pr.Codigo\n" +
                "order by\n" +
                "	pf.Fornecedor, pf.Produto, pf.Codigo_Produto_Fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf")));
                    vo.setCodigoexterno(rst.getString("externo"));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    public List<ItemComboVO> carregarDepositos(int idLojaCliente) throws Exception {
        List<ItemComboVO> itens = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo id, descricao from deposito where empresa = " + idLojaCliente
            )) {
                while (rst.next()) {
                    itens.add(new ItemComboVO(rst.getInt("id"), rst.getInt("id") + "-" + rst.getString("descricao")));
                }
            }
        }
        return itens;
    }

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> itens = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo id, razao_social descricao from empresa order by codigo"
            )) {
                while (rst.next()) {
                    itens.add(new ItemComboVO(rst.getInt("id"), rst.getInt("id") + "-" + rst.getString("descricao")));
                }
            }
        }
        return itens;
    }

    public void setDeposito(int depositoId) {
        this.depositoId = depositoId;
    }
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {                        
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "	p.Codigo id,\n" +
                "	p.Nome,\n" +
                "	p.Endereco,\n" +
                "	p.Numero,\n" +
                "	p.Complemento,\n" +
                "	p.Bairro,\n" +
                "	cd.Estado uf,\n" +
                "	cd.Codigo_Cidade_IBGE id_municipio,\n" +
                "	p.Cep,\n" +
                "	p.Fone fone1,\n" +
                "	p.RG inscricaoestadual,\n" +
                "	p.cpf cnpj,\n" +
                "	case p.Sexo when 'M' then 0 else 1 end Sexo,\n" +
                "	p.Data_Cadastro datacadastro,\n" +
                "	p.Email,\n" +
                "	p.Limite_Credito limite,\n" +
                "	p.Fax,\n" +
                "	case p.Inativo when 'N' then 1 else 0 end id_situacaocadastro,\n" +
                "	p.Fone2 telefone2,\n" +
                "	p.Observacao,\n" +
                "	p.Data_Nasc datanascimento,\n" +
                "	p.Pai nomePai,\n" +
                "	p.Mae nomeMae,\n" +
                "	p.Empresa,\n" +
                "	p.Fone_Trabalho telEmpresa,\n" +
                "	null as cargo,\n" +
                "	null as enderecoEmpresa,\n" +
                "	0 as salario,\n" +
                "	p.Estado_Civil estadocivil,\n" +
                "	p.Conjuge\n" +
                "from\n" +
                "	Cliente c\n" +
                "	join Pessoa p on c.Pessoa = p.Codigo\n" +
                "	left join Cidade cd on p.Cidade = cd.Codigo\n" +
                "order by\n" +
                "	p.Codigo"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("Endereco"));
                    oClientePreferencial.setNumero(rst.getString("Numero"));
                    oClientePreferencial.setComplemento(rst.getString("Complemento"));
                    oClientePreferencial.setBairro(rst.getString("Bairro"));
                    oClientePreferencial.setId_estado(Utils.acertarTexto(rst.getString("uf")).equals("") ? Global.idEstado : Utils.retornarEstadoDescricao(rst.getString("uf")));
                    oClientePreferencial.setId_municipio(rst.getInt("id_municipio") == 0 ? Global.idMunicipio : rst.getInt("id_municipio"));
                    oClientePreferencial.setCep(Utils.stringToInt(rst.getString("Cep")));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    oClientePreferencial.setSexo(rst.getInt("Sexo"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getString("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setFax(rst.getString("fax"));
                    oClientePreferencial.setBloqueado(false);
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClientePreferencial.setTelefone2(rst.getString("telefone2"));
                    oClientePreferencial.setObservacao(rst.getString("observacao"));
                    oClientePreferencial.setDatanascimento(rst.getString("datanascimento"));
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(rst.getString("empresa"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setEnderecoempresa(rst.getString("enderecoEmpresa"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(Utils.stringToInt(rst.getString("estadoCivil")));
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    oClientePreferencial.setOrgaoemissor(null);                  

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }   
    
    
    
}
