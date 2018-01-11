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
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class GR7DAO extends AbstractIntefaceDao {

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	d.codigo merc1,\n" +
                    "    d.descricao merc1_desc,\n" +
                    "    g.codigo merc2,\n" +
                    "    g.descricao merc2_desc\n" +
                    "from \n" +
                    "	departamento d\n" +
                    "    left join gondola g on d.codigo = g.codigo_depart\n" +
                    "where d.codigo > 0\n" +
                    "order by\n" +
                    "	merc1, merc2\n" +
                    "    "
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
                        merc.setMercadologico3(1);
                    }
                    merc.setNivel(nivel);
                    result.add(merc);
                }
            }
        }
        return result;
    }
    
    

    @Override
    public void importarMercadologico() throws Exception {
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
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "   descricao\n" +
                    "from\n" +
                    "	familia\n" +
                    "where\n" +
                    "	codigo > 0\n" +
                    "order by \n" +
                    "	codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(rst.getInt("codigo"));
                    familiaVO.setIdLong(rst.getLong("codigo"));
                    familiaVO.setDescricao(rst.getString("descricao"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {   
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	p.cod_produto id,\n" +
                "    p.produto descricaocompleta,\n" +
                "    p.produto_ecf descricaoreduzida,\n" +
                "    p.produto descricaogondola,\n" +
                "	case p.ativo when 'S' then 1 else 0 end as id_situacaocadastro,\n" +
                "    coalesce(data_cadastro, current_date()) datacadastro,\n" +
                "    p.cod_departamento merc1,\n" +
                "    p.cod_gondola merc2,\n" +
                "    p.nbm ncm,\n" +
                "    p.cest,\n" +
                "    p.cod_familia id_familia,\n" +
                "    p.lucro1 margem,\n" +
                "    p.cod_barras ean,\n" +
                "    p.cod_barras_emb ean_embalagem,\n" +
                "    p.qtd_por_emb qtd_embalagem,\n" +
                "    p.cod_barras_cx ean_caixa,\n" +
                "    p.qtd_por_cx qtd_caixa,\n" +
                "    p.validade,\n" +
                "    p.unidade,\n" +
                "    p.peso_bruto,\n" +
                "    p.peso_liq,\n" +
                "    piscofins.pis_cst_sai piscof_cst_sai,\n" +
                "    piscofins.pis_cst_ent piscof_cst_ent,\n" +
                "    piscofins.nat_rec piscof_nat_rec,\n" +
                "    p.valor_venda1 preco,\n" +
                "    p.valor_compra custo,\n" +
                "    p.qtd_estoque estoque,\n" +
                "    p.qtd_minima estoque_minimo,\n" +
                "    p.cst_rev icms_cst,\n" +
                "    p.aliq_icms_interna icms_aliq,\n" +
                "    p.reduc_icms_rev icms_reducao,\n" +
                "    case when p.pesavel != 'N' then 1 else 0 end pesavel\n" +
                "from\n" +
                "	produto p\n" +
                "    join pis_cofins piscofins on p.cod_pis_cofins = piscofins.codigo\n" +
                "order by\n" +
                "	p.cod_produto"
            )) {
                
                //Obtem os produtos de balança
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
                    if (rst.getString("dataCadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("dataCadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("merc1"));
                    oProduto.setMercadologico2(rst.getInt("merc2"));
                    oProduto.setMercadologico3(1);
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("ncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    oProduto.setIdFamiliaProduto(rst.getString("id_familia") != null ? rst.getInt("id_familia") : -1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    /*
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    } else {
                        produtoBalanca = null;
                    }*/
                    if (rst.getBoolean("pesavel")) {
                        long codigoBarra = Utils.stringToLong(rst.getString("ean"), -2);
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.eBalanca = true;
                        
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade")));
                        oProduto.setPesavel(oAutomacao.getIdTipoEmbalagem() == 4);

                        oCodigoAnterior.setCodigobalanca((int) codigoBarra);
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade")));
                        
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
                    oProduto.setPesoLiquido(rst.getDouble("peso_liq"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("piscof_cst_sai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("piscof_cst_ent")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("piscof_nat_rec")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custo"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    oComplemento.setEstoqueMaximo(0);                   

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(Global.ufEstado));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Utils.stringToLong(rst.getString("ean"), -2));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("piscof_cst_sai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("piscof_cst_ent"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("piscof_nat_rec"));
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
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.cod_produto id,\n" +
                    "    p.cod_barras ean,\n" +
                    "    p.unidade,\n" +
                    "    1 as qtdemb\n" +
                    "from \n" +
                    "	produto p\n" +
                    "union\n" +
                    "select \n" +
                    "	p.cod_produto id,\n" +
                    "    p.cod_barras_emb ean,\n" +
                    "    p.unid_da_emb unidade,\n" +
                    "    p.qtd_por_emb qtdemb\n" +
                    "from \n" +
                    "	produto p\n" +
                    "where\n" +
                    "	coalesce(p.cod_barras_emb, '') != ''"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unidade")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdemb"));
                    
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select \n" +
                "	p.codigo id,\n" +
                "    coalesce(p.data_cadastro, current_date()) datacadastro,\n" +
                "    p.razao_nome razao,\n" +
                "    p.fantasia_apelido fantasia,\n" +
                "    trim(concat(p.tipo_logradouro ,' ', p.logradouro)) endereco,\n" +
                "    p.numero,\n" +
                "    p.complemento,\n" +
                "    p.bairro,\n" +
                "    c.codigo_cid id_municipio,\n" +
                "    c.codigo_est id_estado,\n" +
                "    p.cep,\n" +
                "    p.fone fone1,\n" +
                "    p.ie_rg inscricaoestadual,\n" +
                "    p.cnpj_cpf cnpj,\n" +
                "    p.obs observacao,\n" +
                "    p.fone_trab fone2,\n" +
                "    p.fone_fax fax,\n" +
                "    p.email,\n" +
                "    1 as id_situacaocadastro,\n" +
                "    p.tipo_empresa\n" +
                "from\n" +
                "	participantes p\n" +
                "    left join cidades c on p.cod_cidade = c.codigo\n" +
                "where\n" +
                "	p.tipo_participante like '%F%'\n" +
                "order by\n" +
                "	p.codigo"
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
                    if (rst.getInt("id_municipio") > 0) {
                        oFornecedor.setId_municipio(rst.getInt("id_municipio"));
                    } else {
                        oFornecedor.setId_municipio(Global.idMunicipio);
                    }
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    if (rst.getInt("id_estado") > 0) {
                        oFornecedor.setId_estado(rst.getInt("id_estado"));
                    } else {
                        oFornecedor.setId_estado(Global.idEstado);
                    }
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), -1));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setFax(rst.getString("fax"));
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    /*switch (rst.getInt("tipo_empresa")) {
                        case: oFornecedor.setId_tipoempresa(rst.getInt("tipoempresa"));
                    }*/
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    cod_fornecedor id_fornecedor,\n" +
                "    cod_produto id_produto,\n" +
                "    cod_prod_fornec codigoexterno\n" +
                "from fornec_prod\n" +
                "order by id_fornecedor, id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setCodigoexterno(rst.getString("codigoexterno"));
                    vo.setId_estado(Global.idEstado);
                    vo.setDataalteracao(new Date(new java.util.Date().getTime()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "    p.codigo id,\n" +
                "    p.razao_nome nome,\n" +
                "    trim(concat(p.tipo_logradouro ,' ', p.logradouro)) endereco,\n" +
                "    p.numero,\n" +
                "    p.complemento,\n" +
                "    p.bairro,\n" +
                "    c.codigo_cid id_municipio,\n" +
                "    c.codigo_est id_estado,\n" +
                "    p.cep,\n" +
                "    p.fone fone1,\n" +
                "    p.ie_rg inscricaoestadual,\n" +
                "    p.cnpj_cpf cnpj,\n" +
                "    1 as sexo,\n" +
                "    case p.data_cadastro when '0000-00-00' then current_date() else p.data_cadastro end as datacadastro,\n" +
                "    p.email,\n" +
                "    case when p.limite_geral > 0 then p.limite_geral else p.limite_credito end as limite,\n" +
                "    p.fone_fax fax,\n" +
                "    case p.status when 1 then 1 else 0 end as bloqueado,\n" +
                "    1 as id_situacaocadastro,\n" +
                "    p.obs observacao,\n" +
                "    case p.data_nascimento when '0000-00-00' then null else p.data_nascimento end as datanascimento,\n" +
                "    p.Pai nomePai,\n" +
                "    p.Mae nomeMae,\n" +
                "    p.local_trab empresa,\n" +
                "    p.fone_trab telempresa,\n" +
                "    p.orgao_exp,\n" +
                "    p.conjuge,\n" +
                "    p.conjuge_cpf,\n" +
                "    case p.conjuge_data_nasc when '0000-00-00' then null else p.conjuge_data_nasc end as conjuge_data_nasc,\n" +
                "    p.conjuge_orgao_exp,\n" +
                "    p.conjuge_rg\n" +
                "from\n" +
                "	participantes p\n" +
                "    left join cidades c on p.cod_cidade = c.codigo\n" +
                "where\n" +
                "	p.tipo_participante like '%C%'\n" +
                "order by\n" +
                "	p.codigo"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("endereco"));
                    oClientePreferencial.setNumero(rst.getString("numero"));
                    oClientePreferencial.setComplemento(rst.getString("complemento"));
                    oClientePreferencial.setBairro(rst.getString("bairro"));
                    oClientePreferencial.setId_estado(rst.getInt("id_estado") == 0 ? Global.idEstado : rst.getInt("id_estado"));
                    oClientePreferencial.setId_municipio(rst.getInt("id_municipio") == 0 ? Global.idMunicipio : rst.getInt("id_municipio"));
                    oClientePreferencial.setCep(Utils.stringToInt(rst.getString("cep")));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(Utils.stringToInt(rst.getString("datacadastro")) > 0 ? rst.getDate("datacadastro") : new Date(new java.util.Date().getTime()));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setFax(rst.getString("fax"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClientePreferencial.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    if (Utils.stringToInt(rst.getString("datanascimento")) > 0) {
                        oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    }
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(rst.getString("empresa"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("telempresa"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    oClientePreferencial.setCpfconjuge(Utils.stringToLong(rst.getString("conjuge_cpf")));
                    oClientePreferencial.setOrgaoemissorconjuge(rst.getString("conjuge_orgao_exp"));
                    oClientePreferencial.setDatanascimentoconjuge(Utils.formatDate(rst.getDate("conjuge_data_nasc")));
                    oClientePreferencial.setRgconjuge(rst.getString("conjuge_rg"));
                    oClientePreferencial.setOrgaoemissor(rst.getString("orgao_exp"));                  

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	r.codigo_cliente,\n" +
                    "    c.cnpj_cpf,\n" +
                    "    r.data_venda,\n" +
                    "    r.num_venda,\n" +
                    "    r.valor_venda,\n" +
                    "    coalesce(r.comprador,'') comprador,\n" +
                    "    r.data_vcto,\n" +
                    "    r.num_ecf\n" +
                    "from \n" +
                    "	receber r\n" +
                    "    join participantes c on r.codigo_cliente = c.codigo\n" +
                    "order by\n" +
                    "	r.data_venda"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("codigo_cliente"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cnpj_cpf")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("data_venda"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("num_venda")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("valor_venda"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR" + ("".equals(rst.getString("comprador").trim()) ? " COMPRADOR - " + rst.getString("comprador").trim() : ""));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("data_vcto"));
                    oReceberCreditoRotativo.setEcf(Utils.stringToInt(rst.getString("num_ecf")));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.cod_produto id,\n" +
                    "    current_date() as datainicio,\n" +
                    "    p.data_promo_final datafinal,\n" +
                    "    p.valor_promocional1\n" +
                    "from \n" +
                    "	produto p\n" +
                    "where\n" +
                    "	p.data_promo_final >= current_date()"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datafinal"));
                    vo.setPrecooferta(rst.getDouble("valor_promocional1"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }
    
    
}
