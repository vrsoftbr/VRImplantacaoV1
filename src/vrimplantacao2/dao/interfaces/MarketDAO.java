package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class MarketDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;
    public int vPlanoContas;
    
    @Override
    public String getSistema() {
        return "Market";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cd_loja,\n" +
                    "	nm_loja\n" +
                    "from\n" +
                    "	cadastro.tb_loja")){
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cd_loja"), rs.getString("nm_loja")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	dep.cd_depto as merc1,\n" +
                    "	dep.nm_depto as descmerc1,\n" +
                    "	sec.cd_depto_secao as merc2,\n" +
                    "	sec.nm_depto_secao as descmerc2,\n" +
                    "	coalesce(gru.cd_depto_grupo, 1) as merc3,\n" +
                    "	coalesce(gru.nm_depto_grupo, sec.nm_depto_secao) as descmerc3\n" +
                    "from\n" +
                    "	produto.tb_depto dep\n" +
                    "left join\n" +
                    "	produto.tb_depto_secao sec on sec.cd_depto = dep.cd_depto\n" +
                    "left join\n" +
                    "	produto.tb_depto_grupo gru on gru.cd_depto_secao = sec.cd_depto_secao\n" +
                    "order by\n" +
                    "	dep.cd_depto, sec.cd_depto_secao")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.cd_produto,\n" +
                    "	f.cd_base_fornecedor,\n" +
                    "	d.cd_divisao,\n" +
                    "	d.nm_divisao,\n" +
                    "	pd.nr_produto_fornecedor\n" +
                    "from \n" +
                    "	produto.tb_produto_loja_forn f\n" +
                    "left join\n" +
                    "	produto.tb_divisao d on d.cd_base_fornecedor = f.cd_base_fornecedor\n" +
                    "left join\n" +
                    "	produto.tb_produto_divisao pd on pd.cd_produto = f.cd_produto and\n" +
                    "	d.cd_divisao = pd.cd_divisao\n" +
                    //"where\n" +
                    //"	f.cd_produto = 11036\n" +
                    "order by\n" +
                    "	f.cd_base_fornecedor, f.cd_produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("cd_produto"));
                    imp.setIdFornecedor(rs.getString("cd_base_fornecedor"));
                    imp.setCodigoExterno(rs.getString("nr_produto_fornecedor"));
                                  
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pro.cd_produto as id,\n" +
                    "	pro.nm_produto as nomecompleto,\n" +
                    "	pro.nm_reduzido as nomereduzido,\n" +
                    "	barra.cd_codbarra as codigobarras,\n" +
                    "	preco.vl_custo_faturado as custosemimposto,\n" +
                    "	preco.vl_custo as custocomimposto,\n" +
                    "	preco.vl_venda,\n" +
                    "	produto.fn_get_produto_margem_liquida(l.cd_loja, pro.cd_produto) as margem,\n" +
                    "	est.qt_estoque,\n" +
                    "	pro.cd_depto as codmerc1,\n" +
                    "	pro.cd_depto_secao as codmerc2,\n" +
                    "	pro.cd_depto_grupo as codmerc3,\n" +
                    "	pro.cd_depto_subgrupo as codmerc4,\n" +
                    "	pro.tp_venda as balanca,\n" +
                    "	pro.tp_embalagem as embalagem,\n" +
                    "	pro.qt_embalagem as qtdembalagem,\n" +
                    "	case when pro.is_excluido = 'N' then 1 else 0 end as ativo,\n" +
                    "	pro.dt_inc as dtcadastro,\n" +
                    "	pro.vl_peso_liquido as pesoliquido,\n" +
                    "	(select \n" +
                    "		f.nr_ncm\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "	where \n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva limit 1) as ncm,\n" +
                    "	(select \n" +
                    "		f.nr_cest\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "	where \n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva limit 1) as cest,\n" +
                    "	preco.pr_icms as icmsdebito,\n" +
                    "	preco.ds_icms,\n" +
                    "	(select \n" +
                    "		distinct\n" +
                    "		f.nr_cst_pis_cofins_saida\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "\n" +
                    "	where\n" +
                    "		f.dt_vigencia in\n" +
                    "			(select \n" +
                    "				max(n.dt_vigencia) \n" +
                    "			from \n" +
                    "				produto.tb_ncm_figura_vigencia_federal n \n" +
                    "			where \n" +
                    "				n.cd_ncm_figura_mva = f.cd_ncm_figura_mva) and\n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva) as cstpiscofinssaida,\n" +
                    "	(select \n" +
                    "		distinct\n" +
                    "		f.nr_cst_pis_cofins_entrada\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "\n" +
                    "	where\n" +
                    "		f.dt_vigencia in\n" +
                    "			(select \n" +
                    "				max(n.dt_vigencia) \n" +
                    "			from \n" +
                    "				produto.tb_ncm_figura_vigencia_federal n \n" +
                    "			where \n" +
                    "				n.cd_ncm_figura_mva = f.cd_ncm_figura_mva) and\n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva) as cstpiscofinsentrada,\n" +
                    "	pb.qt_dias_validade_balanca as validade,\n" +
                    "   pro.cd_produto_semelhante as idfamilia\n" +
                    "  from \n" +
                    "	produto.tb_produto pro\n" +
                    "left join\n" +
                    "	produto.tb_produto_codbarra barra on barra.cd_produto = pro.cd_produto\n" +
                    "left join\n" +
                    "	produto.tb_produto_loja preco on preco.cd_produto = pro.cd_produto\n" +
                    "left join\n" +
                    "	paf.tb_mercadoria_estoque est on est.cd_mercadoria = pro.cd_produto\n" +
                    "join\n" +
                    "	cadastro.tb_loja l on l.cd_loja = preco.cd_loja\n" +
                    "left join\n" +
                    "	produto.tb_produto_balanca pb on pb.cd_produto = pro.cd_produto\n" +
                    "where\n" +
                    "	barra.is_padrao = 'S' and\n" +
                    "	l.cd_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	pro.cd_produto")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("nomecompleto"));
                    imp.setDescricaoReduzida(rs.getString("nomereduzido"));
                    imp.setDescricaoGondola(rs.getString("nomecompleto"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("vl_venda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("qt_estoque"));
                    imp.setCodMercadologico1(rs.getString("codmerc1"));
                    imp.setCodMercadologico2(rs.getString("codmerc2"));
                    imp.setCodMercadologico3(rs.getString("codmerc3"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    
                   if ( rs.getString("ds_icms") != null ) {
                    switch (rs.getString("ds_icms").trim()) {
                        case "07" : imp.setIcmsAliqSaida(7);
                                    imp.setIcmsCstSaida(0);
                            break;
                        case "12" : imp.setIcmsAliqSaida(12);
                                    imp.setIcmsCstSaida(0);
                            break;
                        case "17" : imp.setIcmsAliqSaida(17);
                                    imp.setIcmsCstSaida(0);
                            break;
                        case "25" : imp.setIcmsAliqSaida(25);
                                    imp.setIcmsCstSaida(0);
                            break;
                        case "IS" : imp.setIcmsAliqSaida(0);
                                    imp.setIcmsCstSaida(40);
                            break;
                        case "ST" : imp.setIcmsAliqSaida(0);
                                    imp.setIcmsCstSaida(60);
                            break;
                        default :   imp.setIcmsAliqSaida(0);
                                    imp.setIcmsCstSaida(60);
                            break;
                    }
                   }
                    imp.setPiscofinsCstDebito(rs.getInt("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rs.getInt("cstpiscofinsentrada"));
                    
                    if ((rs.getString("codigobarras") != null) && 
                            (!"".equals(rs.getString("codigobarras").trim())) && 
                                (rs.getString("codigobarras").length() <= 6)) { 
                        if( (rs.getString("balanca") != null) && ("B".equals(rs.getString("balanca").trim())) ){
                            if (v_usar_arquivoBalanca) {
                                ProdutoBalancaVO produtoBalanca;
                                long codigoProduto;
                                String ean = rs.getString("id");
                                
                                imp.setEan(ean);
                                codigoProduto = Long.parseLong(imp.getEan().trim());
                              
                                if (codigoProduto <= Integer.MAX_VALUE) {
                                    produtoBalanca = produtosBalanca.get((int) codigoProduto);
                                } else {
                                    produtoBalanca = null;
                                }
                                if (produtoBalanca != null) {
                                    imp.seteBalanca(true);
                                    imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                                } else {
                                    imp.setValidade(0);
                                    imp.seteBalanca(false);
                                }
                            
                        } else {
                            imp.seteBalanca("B".equals(rs.getString("balanca")) ? true : false);
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                }
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	cd_produto_semelhante as id,\n" +
                    "	nm_produto_semelhante as nomeproduto,\n" +
                    "	dt_inc as dtcadastro\n" +
                    "from \n" +
                    "	produto.tb_produto_semelhante \n" +
                    "order by \n" +
                    "	nm_produto_semelhante")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("nomeproduto"));
                
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	b.cd_base as id,\n" +
                    "	ba.nm_bairro as bairro,\n" +
                    "	ci.nm_cidade as municipio,\n" +
                    "	ci.nr_ibge as idmunicipio,\n" +
                    "	b.nr_cpf_cnpj as cnpj,\n" +
                    "	doc.ds_documento as rgie,\n" +
                    "	bc.sg_ddd || '' || bc.ds_valor as telefone,\n" +
                    "	b.nr_cep as cep,\n" +
                    "	b.nr_endereco as numero,\n" +
                    "	b.nm_base as nome,\n" +
                    "	b.nm_fantasia as fantasia,\n" +
                    "	b.ds_complemento as complemento,\n" +
                    "	b.dt_inc as dtcadastro,\n" +
                    "	b.ds_email_nfe as emailnfe,\n" +
                    "	case when b.is_ativo = 'S' then 1 else 0 end as ativo,\n" +
                    "	l.nm_logradouro as logradouro,\n" +
                    "	lt.sg_logradouro_tipo\n" +
                    "from \n" +
                    "	cadastro.tb_base b\n" +
                    "join\n" +
                    "	cadastro.tb_base_tipo bt on bt.cd_base::integer = b.cd_base::integer\n" +
                    "left join\n" +
                    "	cadastro.tb_bairro ba on ba.cd_bairro::integer = b.cd_bairro::integer\n" +
                    "left join\n" +
                    "	cadastro.tb_cidade ci on ci.cd_cidade::integer = b.cd_cidade::integer\n" +
                    "left join\n" +
                    "	cadastro.tb_base_documento doc on doc.cd_base::integer = b.cd_base::integer\n" +
                    "left join \n" +
                    "	cadastro.tb_logradouro_tipo lt on lt.cd_logradouro_tipo::integer = b.cd_logradouro_tipo::integer\n" +
                    "left join 	\n" +
                    "	cadastro.tb_logradouro l on l.cd_logradouro::integer = b.cd_logradouro::integer\n" +
                    "left join\n" +
                    "	cadastro.tb_base_contato bc on bc.cd_base = b.cd_base\n" +
                    "where\n" +
                    "	bt.cd_base_tipo_flag::integer = 2\n" +
                    "order  by\n" +
                    "	b.cd_base")) {
                while(rs.next()){            
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setIbge_municipio(rs.getInt("idmunicipio"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("rgie"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setDatacadastro(rs.getDate("dtcadastro"));
                    
                    if( (rs.getString("emailnfe") != null) && (!"".equals(rs.getString("emailnfe"))) ) {
                        imp.addEmail("Email NFE", rs.getString("emailnfe"), TipoContato.NFE);
                    }
                    
                    imp.setAtivo(rs.getBoolean("ativo"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
           try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	b.cd_base as id, \n" +
                    "	ba.nm_bairro as bairro, \n" +
                    "	ci.nm_cidade as municipio, \n" +
                    "	ci.nr_ibge as idmunicipio, \n" +
                    "	b.nr_cpf_cnpj as cnpj, \n" +
                    "	doc.ds_documento as rgie, \n" +
                    "	bc.sg_ddd || '' || bc.ds_valor as telefone,\n" +
                    "	bc.nm_contato as nomecontato,\n" +
                    "	b.nr_cep as cep, \n" +
                    "	b.nr_endereco as numero, \n" +
                    "	b.nm_base as nome, \n" +
                    "	b.nm_fantasia as fantasia, \n" +
                    "	b.ds_complemento as complemento, \n" +
                    "	b.dt_inc as dtcadastro, \n" +
                    "	b.ds_email_nfe as emailnfe, \n" +
                    "	case when b.is_ativo = 'S' then 1 else 0 end as ativo, \n" +
                    "	l.nm_logradouro as logradouro, \n" +
                    "	lt.sg_logradouro_tipo,\n" +
                    "	cl.vl_limite\n" +
                    "from  \n" +
                    "	cadastro.tb_base b \n" +
                    "join \n" +
                    "	cadastro.tb_base_tipo bt on bt.cd_base::integer = b.cd_base::integer \n" +
                    "left join \n" +
                    "	cadastro.tb_bairro ba on ba.cd_bairro::integer = b.cd_bairro::integer \n" +
                    "left join \n" +
                    "	cadastro.tb_cidade ci on ci.cd_cidade::integer = b.cd_cidade::integer \n" +
                    "left join \n" +
                    "	cadastro.tb_base_documento doc on doc.cd_base::integer = b.cd_base::integer \n" +
                    "left join  \n" +
                    "	cadastro.tb_logradouro_tipo lt on lt.cd_logradouro_tipo::integer = b.cd_logradouro_tipo::integer \n" +
                    "left join 	 \n" +
                    "	cadastro.tb_logradouro l on l.cd_logradouro::integer = b.cd_logradouro::integer \n" +
                    "left join \n" +
                    "	cadastro.tb_base_contato bc on bc.cd_base = b.cd_base\n" +
                    "left join\n" +
                    "	cadastro.tb_cliente cl on cl.cd_base_cliente = b.cd_base\n" +
                    "where \n" +
                    "	bt.cd_base_tipo_flag::integer in (1, 10) \n" +
                    "order  by \n" +
                    "	b.cd_base")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("idmunicipio"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("rgie"));
                    if ( (rs.getString("telefone") != null) && (!rs.getString("telefone").isEmpty()) ) {
                        imp.addContato("1", rs.getString("nomecontato"), rs.getString("telefone"), "", "");
                    }
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setEmail(rs.getString("emailnfe"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setValorLimite(rs.getDouble("vl_limite"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public List<ItemComboVO> getPlanoContas() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	nr_titulo_tipo,\n" +
                    "	ds_titulo_tipo\n" +
                    "from \n" +
                    "	cadastro.tb_titulo_tipo\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    result.add(new ItemComboVO(rs.getInt("nr_titulo_tipo"), rs.getString("ds_titulo_tipo")));
                }
            }
        }        
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	r.cd_receber as id,\n" +
                    "	r.cd_base_cliente as idcliente,\n" +
                    "	ba.nr_cpf_cnpj as cnpj,\n" +
                    "	ba.nm_base as razao,\n" +
                    "	r.cd_caixa as ecf,\n" +
                    "	r.nr_titulo,\n" +
                    "	r.nr_digito,\n" +
                    "	r.ds_obs,\n" +
                    "	r.vl_valor,\n" +
                    "	r.dt_emissao,\n" +
                    "	r.dt_vcto,\n" +
                    "	r.nr_coo as coo,\n" +
                    "	r.nr_serie_ecf as serieecf\n" +
                    "from \n" +
                    "	receber.tb_receber r \n" +
                    "join\n" +
                    "	cadastro.tb_titulo_tipo rt on rt.cd_titulo_tipo = r.cd_titulo_tipo\n" +
                    "join\n" +
                    "	cadastro.tb_base ba on ba.cd_base = r.cd_base_cliente\n" +
                    "where \n" +
                    "	dt_ultima_baixa is null and\n" +
                    "	rt.nr_titulo_tipo = " + vPlanoContas + "\n" +
                    "order by\n" +
                    "	dt_emissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("dt_emissao"));
                    imp.setDataVencimento(rs.getDate("dt_vcto"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("ds_obs"));
                    imp.setValor(rs.getDouble("vl_valor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
