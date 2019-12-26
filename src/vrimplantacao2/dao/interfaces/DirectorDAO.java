package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class DirectorDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Director";
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select DFcod_empresa id, DFnome_fantasia fantasia from TBempresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	d.DFid_departamento_item merc1,\n" +
                    "	d.DFdescricao descmerc1,\n" +
                    "	s.DFid_departamento_item merc2,\n" +
                    "	s.DFdescricao descmerc2,\n" +
                    "	g.DFid_departamento_item merc3,\n" +
                    "	g.DFdescricao descmerc3\n" +
                    "from \n" +
                    "	TBdepartamento_item d\n" +
                    "join\n" +
                    "	TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n" +
                    "join\n" +
                    "	TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai\n" +
                    "order by\n" +
                    "	2, 4, 6")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct(f.DFcod_produto_origem) codigo, \n" +
                    "	p.DFdescricao descricao\n" +
                    "from \n" +
                    "	TBproduto_similar f \n" +
                    "inner join TBitem_estoque p on p.DFcod_item_estoque = f.DFcod_produto_origem \n" +
                    "order by\n" +
                    "	2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	 p.DFcod_item_estoque id,\n" +
                    "	 p.DFdescricao descricaocompleta,\n" +
                    "	 p.DFdescricao_resumida descricaoreduzida,\n" +
                    "	 p.DFdescricao_analitica descricaogondola,\n" +
                    "	 cb.DFcodigo_barra ean,\n" +
                    "	 un.DFdescricao embalagem,\n" +
                    "	 pu.DFfator_conversao qtdembalagem,\n" +
                    "	 p.DFativo_inativo situacao,\n" +
                    "	 p.DFdata_cadastro datacadastro,\n" +
                    "	 merc.merc1,\n" +
                    "	 merc.merc2,\n" +
                    "	 merc.merc3,\n" +
                    "	 fam.DFcod_produto_origem familia,\n" +
                    "	 p.DFpeso_liquido pesoliquido,\n" +
                    "	 p.DFpeso_variavel pesavel,\n" +
                    "	 pa.DFcodigo_setor_balanca setor_balanca,\n" +
                    "	 pa.DFvalidade_pesaveis validade,\n" +
                    "	 pe.DFestoque_minimo estoqueminimo,\n" +
                    "	 pe.DFestoque_maximo estoquemaximo,\n" +
                    "	 es.DFquantidade_Atual estoque,\n" +
                    "	 pe.DFmargem_lucro margem,\n" +
                    "	 pr.DFpreco_venda precovenda,\n" +
                    "	 pr.DFcusto_real custoreal,\n" +
                    "	 pr.DFcusto_contabil custocontabil,\n" +
                    "	 pa.DFcod_classificacao_fiscal ncm,\n" +
                    "	 pa.DFcod_cst_pis pis_saida,\n" +
                    "	 pa.DFcod_cst_cofins cofins_saida,\n" +
                    "	 pa.DFcod_cst_pis_entrada pis_entrada,\n" +
                    "	 pa.DFcod_cst_cofins_entrada cofins_entrada,\n" +
                    "	 pa.dfcod_cest cest,\n" +
                    "	 cst.DFcod_tributacao_cst cst,\n" +
                    "	 tc.DFtipo_tributacao tipocst,\n" +
                    "	 ae.DFaliquota_icms icms_debito,\n" +
                    "	 ae.DFpercentual_reducao icms_reducao_debito,\n" +
                    "	 ae.DFaliquota_icms_subst_tributaria icms_sub_tributaria,\n" +
                    "	 ae.DFaliquota_icms_desonerado icms_desonerado,\n" +
                    "	 nr.DFcod_natureza_receita_pis_cofins naturezareceita\n" +
                    "from \n" +
                    "	TBitem_estoque p\n" +
                    "left join \n" +
                    "	TBitem_estoque_empresa pe on p.DFcod_item_estoque = pe.DFcod_item_estoque\n" +
                    "inner join\n" +
                    "	TBempresa em on pe.DFcod_empresa = em.DFcod_empresa\n" +
                    "left join \n" +
                    "	TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque\n" +
                    "left join \n" +
                    "	TBitem_estoque_atacado_varejo pa on p.DFcod_item_estoque = pa.DFcod_item_estoque_atacado_varejo\n" +
                    "left join \n" +
                    "	TBunidade un on pu.DFcod_unidade = un.DFcod_unidade\n" +
                    "inner join \n" +
                    "	TBtipo_unidade_item_estoque tu WITH (NOLOCK) on tu.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and\n" +
                    "	 tu.DFid_tipo_unidade = (SELECT DFvalor FROM TBopcoes WITH (NOLOCK) WHERE DFcodigo = 420)  \n" +
                    "left join \n" +
                    "	tbresumo_estoque es WITH (NOLOCK) on es.DFid_unidade_item_estoque = p.DFunidade_controle and \n" +
                    "	es.DFcod_empresa = pe.DFcod_empresa and \n" +
                    "	es.DFid_tipo_estoque = (select DFvalor from TBopcoes WITH (NOLOCK) where DFcodigo = 553)\n" +
                    "left join \n" +
                    "	TBunidade_item_estoque_preco pr on pr.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and \n" +
                    "	pr.DFcod_empresa = pe.DFcod_empresa\n" +
                    "left join \n" +
                    "	TBcodigo_barra cb on pu.DFid_unidade_item_estoque = cb.DFid_unidade_item_estoque\n" +
                    "left join\n" +
                    "	TBitem_cst_aliquota_icms_estado ai on p.DFcod_item_estoque = ai.DFcod_item_estoque\n" +
                    "left join\n" +
                    "	TBcst_aliquota_icms_estado ae on ai.DFid_cst_aliquota_icms_estado = ae.DFid_cst_aliquota_icms_estado\n" +
                    "left join\n" +
                    "	TBaliquota_icms_estado ac on ae.DFid_aliquota_icms_estado = ac.DFid_aliquota_icms_estado\n" +
                    "inner join\n" +
                    "	TBcst cst WITH (NOLOCK) on ae.DFid_cst = cst.DFid_cst \n" +
                    "inner join \n" +
                    "	TBramo_atividade_tributacao_cst ra WITH (NOLOCK) on ra.DFcod_tributacao_cst = cst.DFcod_tributacao_cst \n" +
                    "inner join\n" +
                    "	TBtributacao_cst tc WITH (NOLOCK) on tc.DFcod_tributacao_cst = cst.DFcod_tributacao_cst\n" +
                    "left join\n" +
                    "	TBnatureza_receita_pis_cofins nr on pa.DFid_natureza_receita_pis_cofins = nr.DFid_natureza_receita_pis_cofins\n" +
                    "left join\n" +
                    "	TBproduto_similar fam on  p.DFcod_item_estoque = fam.DFcod_produto_similar\n" +
                    "left join\n" +
                    "	(select \n" +
                    "		d.DFid_departamento_item merc1,\n" +
                    "		d.DFdescricao descmerc1,\n" +
                    "		s.DFid_departamento_item merc2,\n" +
                    "		s.DFdescricao descmerc2,\n" +
                    "		g.DFid_departamento_item merc3,\n" +
                    "		g.DFdescricao descmerc3\n" +
                    "	from \n" +
                    "		TBdepartamento_item d\n" +
                    "	join\n" +
                    "		TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n" +
                    "	join\n" +
                    "		TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai) merc on p.DFid_departamento_item = merc.merc3\n" +
                    "where\n" +
                    "	em.DFcod_empresa = " + getLojaOrigem() + " and\n" +
                    "	ai.DFpessoa_fisica_juridica = 'F' and\n" +
                    "	ra.DFid_ramo_atividade = em.DFid_ramo_atividade and\n" +
                    "	ac.DFcod_uf = em.DFuf_base and\n" +
                    "	ac.DFcod_uf_destino = em.DFuf_base and\n" +
                    "	ae.DFcod_grupo_tributacao = em.DFcod_grupo_tributacao and\n" +
                    "	ae.DFid_tipo_estabelecimento = em.DFid_tipo_estabelecimento\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.seteBalanca(rs.getInt("pesavel") == 1);
                    
                    if(imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoSemImposto(rs.getDouble("custoreal"));
                    imp.setCustoComImposto(rs.getDouble("custocontabil"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstDebito(rs.getString("cofins_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("cofins_entrada"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCst(rs.getString("cst"));
                    imp.setIcmsAliq(rs.getDouble("icms_debito"));
                    imp.setIcmsReducao(rs.getDouble("icms_reducao_debito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.DFcod_fornecedor id,\n" +
                    "	f.DFnome razao,\n" +
                    "	f.DFnome_fantasia fantasia,\n" +
                    "	f.DFcgc cnpj,\n" +
                    "	f.DFinscr_estadual ie,\n" +
                    "	f.DFprazo_entrega prazo_entrega,\n" +
                    "	f.DFobservacao obs,\n" +
                    "	f.DFdata_cadastro data_cadastro,\n" +
                    "	ft.DFid_tipo_estabelecimento idtipo_empresa,\n" +
                    "	ft.DFdescricao tipo_empresa,\n" +
                    "	fp.DFdescricao forma_pagamento,\n" +
                    "	f.DFinscricao_municipal inscricao_municipal,\n" +
                    "	fr.DFid_ramo_atividade ramo_atividade,\n" +
                    "	fr.DFdescricao descricao_ramo_atividade,\n" +
                    "	cl.DFcod_cep cep,\n" +
                    "	tl.DFdescricao + ' ' + lo.DFdescricao endereco,\n" +
                    "	f.DFcomplemento_endereco numero,\n" +
                    "	lo.DFcomplemento complemento,\n" +
                    "	ba.DFdescricao bairro,\n" +
                    "	lc.DFdescricao municipio,\n" +
                    "	lc.DFcod_uf uf,\n" +
                    "	ct.DFe_mail email,\n" +
                    "	ct.DFfax fax,\n" +
                    "	ct.DFtelefone telefone,\n" +
                    "	ct.DFtelefone_celular celular,\n" +
                    "	ct.DFcontato contato,\n" +
                    "	ct.DFcargo_contato cargo_contato,\n" +
                    "	sc.DFdescricao setor\n" +
                    "from\n" +
                    "	TBfornecedor f\n" +
                    "left join \n" +
                    "	TBtipo_estabelecimento ft on f.DFid_tipo_estabelecimento = ft.DFid_tipo_estabelecimento\n" +
                    "left join\n" +
                    "	TBplano_pagamento fp on f.DFcod_plano_pagamento = fp.DFcod_plano_pagamento\n" +
                    "left join\n" +
                    "	TBramo_atividade fr on f.DFid_ramo_atividade = fr.DFid_ramo_atividade\n" +
                    "left join\n" +
                    "	TBcep_logradouro cl on f.DFid_cep_logradouro = cl.DFid_cep_logradouro\n" +
                    "left join\n" +
                    "	TBlogradouro lo on cl.DFid_logradouro = lo.DFid_logradouro\n" +
                    "left join\n" +
                    "	TBtipo_logradouro tl on lo.DFcod_tipo_logradouro = tl.DFcod_tipo_logradouro\n" +
                    "left join\n" +
                    "	TBbairro ba on lo.DFid_bairro = ba.DFid_bairro\n" +
                    "left join\n" +
                    "	TBlocalidade lc on ba.DFcod_localidade = lc.DFcod_localidade\n" +
                    "left join\n" +
                    "	TBcontato_fornecedor ct on f.DFcod_fornecedor = ct.DFcod_fornecedor\n" +
                    "left join\n" +
                    "	TBsetor_contato sc on ct.DFid_setor_contato = sc.DFid_setor_contato\n" +
                    "order by\n" +
                    "	f.DFcod_fornecedor")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setObservacao(rs.getString("obs") + " Forma Pag.: " + rs.getString("forma_pagamento"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setInsc_municipal(rs.getString("inscricao_municipal"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("telefone"));
                    
                    int idTipoEmpresa = rs.getInt("idtipo_empresa");
                    if(idTipoEmpresa == 57) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else if(idTipoEmpresa == 98) {
                        imp.setTipoEmpresa(TipoEmpresa.PESSOA_FISICA);
                    } else if(idTipoEmpresa == 99) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    } else if(idTipoEmpresa == 101) {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    } else if(idTipoEmpresa == 102) {
                        imp.setProdutorRural();
                    } else if(idTipoEmpresa == 103) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }
                    
                    int idTipoFornecedor = rs.getInt("ramo_atividade");
                    if(idTipoFornecedor == 2) {
                        imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                    } else if(idTipoFornecedor == 3) {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    } else if(idTipoFornecedor == 7) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else if(idTipoFornecedor == 57) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else if(idTipoFornecedor == 45) {
                        imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                    } else {
                        imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                    }
                    
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("1", rs.getString("contato"), null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("2", "FAX", null, null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("3", "CELULAR", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("4", rs.getString("contato"), null, rs.getString("celular"), TipoContato.COMERCIAL, rs.getString("setor"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	DFcod_fornecedor idfornecedor,\n" +
                    "	DFcod_item_estoque idproduto,\n" +
                    "	DFpart_number codigoexterno\n" +
                    "from\n" +
                    "	TBfornecedor_item\n" +
                    "order by\n" +
                    "	1, 2")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.DFid_promocao id,\n" +
                    "	p.DFdata_inicial datainicial,\n" +
                    "	p.DFdata_final datafinal,\n" +
                    "	p.DFdescricao descricao,\n" +
                    "	pi.DFid_unidade_item_estoque idproduto,\n" +
                    "	pi.dfpreco precopromocao,\n" +
                    "	ie.DFpreco_venda precovenda\n" +
                    "from\n" +
                    "	tbpromocao p\n" +
                    "join\n" +
                    "	TBpromocao_empresa pe on p.DFid_promocao = pe.DFid_promocao\n" +
                    "join\n" +
                    "	TBpromocao_unidade_item_estoque pi on p.DFid_promocao = pi.DFid_promocao \n" +
                    "join\n" +
                    "	TBunidade_item_estoque_preco ie on pi.DFid_unidade_item_estoque = ie.DFid_unidade_item_estoque and\n" +
                    "	ie.DFcod_empresa = pe.DFcod_empresa\n" +
                    "where\n" +
                    "	p.DFdata_final >= GETDATE() and\n" +
                    "	pe.DFcod_empresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	p.DFdata_final\n" +
                    "	p.DFdata_final")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicial"));
                    imp.setDataFim(rs.getDate("datafinal"));
                    imp.setPrecoOferta(rs.getDouble("precopromocao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.DFcod_cliente id,\n" +
                    "	c.DFnome razao,\n" +
                    "	c.DFnome_fantasia fantasia,\n" +
                    "	c.DFcnpj_cpf cnpj,\n" +
                    "	c.DFfisico_juridico tipo,\n" +
                    "	c.DFinscr_estadual ie,\n" +
                    "	c.DFcarteira_identidade rg,\n" +
                    "	c.DFdata_cadastro data_cadastro,\n" +
                    "	c.DFobservacao obs,\n" +
                    "	c.DFlimite_credito valor_limite,\n" +
                    "	c.DFdata_inativacao inativacao,\n" +
                    "	ce.DFcomplemento_endereco numero,\n" +
                    "	ce.DFponto_referencia referencia,\n" +
                    "	cc.DFcod_cep cep,\n" +
                    "	tl.DFdescricao + ' ' + cl.DFdescricao endereco,\n" +
                    "	cl.DFcomplemento complemento,\n" +
                    "	lo.DFdescricao municipio,\n" +
                    "	lo.DFcod_uf uf,\n" +
                    "	cn.DFe_mail email,\n" +
                    "	cn.DFdata_aniversario data_aniversario,\n" +
                    "	cn.DFcontato contato,\n" +
                    "	sc.DFdescricao setor,\n" +
                    "	cn.DFfax fax,\n" +
                    "	cn.DFtelefone telefone,\n" +
                    "	cn.DFtelefone_celular celular\n" +
                    "from\n" +
                    "	TBcliente c\n" +
                    "left join\n" +
                    "	TBendereco_cliente ce on c.DFcod_cliente = ce.DFcod_cliente\n" +
                    "left join	\n" +
                    "	TBcep_logradouro cc on ce.DFid_cep_logradouro = cc.DFid_cep_logradouro\n" +
                    "left join\n" +
                    "	TBlogradouro cl on cc.DFid_logradouro = cl.DFid_logradouro\n" +
                    "left join\n" +
                    "	TBtipo_logradouro tl on cl.DFcod_tipo_logradouro = tl.DFcod_tipo_logradouro\n" +
                    "left join\n" +
                    "	TBlocalidade lo on cl.DFcod_localidade = lo.DFcod_localidade\n" +
                    "left join\n" +
                    "	TBcontato_cliente cn on c.DFcod_cliente = cn.DFcod_cliente\n" +
                    "left join\n" +
                    "	TBsetor_contato sc on cn.DFid_setor_contato = sc.DFid_setor_contato\n" +
                    "where\n" +
                    "	ce.DFtipo_endereco = 'C'\n" +
                    "order by\n" +
                    "	c.DFcod_cliente")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    
                    if("F".equals(rs.getString("tipo"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    }
                    
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setValorLimite(rs.getDouble("valor_limite"));
                    
                    if(rs.getDate("inativacao") != null) {
                        imp.setAtivo(false);
                    } else {
                        imp.setAtivo(true);
                    }
                    
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("data_aniversario"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
