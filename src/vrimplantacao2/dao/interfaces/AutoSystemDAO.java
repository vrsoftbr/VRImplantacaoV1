package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * Dao do sistema AutoSystem.
 * @author Leandro
 */
public class AutoSystemDAO extends InterfaceDAO implements MapaTributoProvider {

    private int idDeposito = 0;

    public void setIdDeposito(int idDeposito) {
        this.idDeposito = idDeposito;
    }

    public int getIdDeposito() {
        return idDeposito;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT grid, grid || ' - ' || nome_reduzido descricao FROM pessoa WHERE tipo ~~ '%%E%%'::bpchar::text order by 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("grid"), rst.getString("descricao"))
                    );
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getDepositos() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            String sql = "select\n" +
                    "	grid,\n" +
                    "	nome\n" +
                    "from\n" +
                    "	deposito\n" +
                    "where\n" +
                    "	empresa = " + getLojaOrigem() + " \n" +
                    "order by codigo";
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("grid"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }    

    @Override
    public String getSistema() {
        return "AutoSystem";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g.codigo merc1,\n" +
                    "	g.nome::varchar merc1_desc,\n" +
                    "	sg.codigo merc2,\n" +
                    "	sg.nome::varchar merc2_desc\n" +
                    "from\n" +
                    "	grupo_produto g\n" +
                    "	left join subgrupo_produto sg on\n" +
                    "		sg.grupo = g.grid\n" +
                    "order by\n" +
                    "	g.codigo,\n" +
                    "	sg.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.grid id,\n" +
                    "	p.codigo,\n" +
                    "	p.data_cad datacadastro,\n" +
                    "	ean.codigo_barra ean,\n" +
                    "	1 qtdEmbalagem,\n" +
                    "	p.qtde_unid_entrada qtdEmbalagemCotacao,\n" +
                    "	p.unid_med unidade,\n" +
                    "	case p.unid_med\n" +
                    "	when 'KG' then 1\n" +
                    "	else 0\n" +
                    "	end ebalanca,\n" +
                    "	p.dias_validade validade,\n" +
                    "	p.nome descricaocompleta,\n" +
                    "	p.grupo merc1,\n" +
                    "	p.subgrupo merc2,\n" +
                    "	est.estoque,\n" +
                    "	p.margem_lucro margem,\n" +
                    "	p.preco_custo custosemimposto,\n" +
                    "	p.preco_custo custocomimposto,\n" +
                    "	coalesce(nullif(premp.preco_unit,0), p.preco_unit) precovenda,\n" +
                    "	p.flag,\n" +
                    "	p.codigo_ncm ncm,\n" +
                    "	p.cest,\n" +
                    "	p.cst_pis piscofins_saida,\n" +
                    "	p.cst_pis_entrada pisconfins_entrada,\n" +
                    "	nat.codigo_nat_rec piscofins_natureza_receita,\n" +
                    "	p.tributacao id_icms,\n" +
                    "	p.fornecedor\n" +
                    "from\n" +
                    "	produto p\n" +
                    "	join empresa emp on emp.grid = " + getLojaOrigem()+ "\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			grid,\n" +
                    "			codigo_barra\n" +
                    "		from\n" +
                    "			produto\n" +
                    "		where\n" +
                    "			not codigo_barra is null and\n" +
                    "			trim(codigo_barra) != ''\n" +
                    "		union	\n" +
                    "		select\n" +
                    "			grid,\n" +
                    "			codigo_barra\n" +
                    "		from\n" +
                    "			produto_codigo_barra\n" +
                    "		where\n" +
                    "			not codigo_barra is null and\n" +
                    "			trim(codigo_barra) != ''\n" +
                    "	) ean on\n" +
                    "		ean.grid = p.grid\n" +
                    "	left join  estoque_produto est on\n" +
                    "		est.empresa =  emp.grid\n" +
                    "		and est.deposito = " + getIdDeposito() + "\n" +
                    "		and est.produto = p.grid\n" +
                    "	left join produto_empresa premp on\n" +
                    "		premp.empresa = emp.grid\n" +
                    "		and premp.produto = p.grid\n" +
                    "	left join natureza_receita nat on\n" +
                    "		p.natureza_receita = nat.codigo\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdEmbalagemCotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    switch (Utils.acertarTexto(rst.getString("flag"))) {                        
                        case "D":
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                            imp.setDescontinuado(true);
                            break;
                        case "I":
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(true);
                            break;
                        default:
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(false);
                            break;                        
                    }
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("pisconfins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setFornecedorFabricante(rst.getString("fornecedor"));
                    imp.setCodigoSped(rst.getString("id"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	coalesce(cst,'') || '-' || coalesce(tributacao,0) || '%-' || coalesce(reducao_base,0) || '%-' || descricao  descricao\n" +
                    "from\n" +
                    "	tributacao\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.grid id,\n" +
                    "	f.nome razao,\n" +
                    "	f.nome_reduzido fantasia,\n" +
                    "	f.cpf cnpj,\n" +
                    "	f.rg,\n" +
                    "	f.inscr_est,\n" +
                    "	f.inscr_municipal,\n" +
                    "	case f.flag when 'A' then 1 else 0 end ativo,\n" +
                    "	f.logradouro,\n" +
                    "	f.numero,\n" +
                    "	f.complemento,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cep,\n" +
                    "	\n" +
                    "	f.endereco_c,\n" +
                    "	f.bairro_c,\n" +
                    "	f.cidade_c,\n" +
                    "	f.estado_c,\n" +
                    "	f.cep_c,\n" +
                    "	f.fone_c,\n" +
                    "\n" +
                    "	f.bloqueado,\n" +
                    "	f.fone telefone_principal,\n" +
                    "	f.fone_c,\n" +
                    "	f.data_cad datacadastro,\n" +
                    "	f.obs observacao,\n" +
                    "	f.email_nfe\n" +
                    "from\n" +
                    "	pessoa f\n" +
                    "WHERE\n" +
                    "	f.tipo ~~ '%%F%%'::bpchar::text\n" +
                    "order by\n" +
                    "	f.grid"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    if (Utils.stringToLong(rst.getString("cnpj")) <= 99999999999L) {
                        imp.setIe_rg(rst.getString("rg"));
                    } else {
                        imp.setIe_rg(rst.getString("inscr_est"));
                    }                    
                    imp.setInsc_municipal(rst.getString("inscr_municipal"));
                    imp.setEndereco(rst.getString("logradouro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("endereco_c"));
                    imp.setCob_bairro(rst.getString("bairro_c"));
                    imp.setCob_municipio(rst.getString("cidade_c"));
                    imp.setCob_uf(rst.getString("estado_c"));
                    imp.setCob_cep(rst.getString("cep_c"));
                    imp.addTelefone("FONE COBRANCA", rst.getString("fone_c"));
                    imp.setAtivo(!rst.getBoolean("bloqueado"));
                    imp.setTel_principal(rst.getString("telefone_principal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.addEmail("NFE", rst.getString("email_nfe"), TipoContato.NFE);
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	pf.fornecedor,\n" +
                    "	p.codigo produto,\n" +
                    "	pf.codigo codigoexterno,\n" +
                    "	pf.qtde_unid qtdembalagem\n" +
                    "from\n" +
                    "	produto_fornec pf\n" +
                    "	join produto p on\n" +
                    "		pf.produto = p.grid\n" +
                    "order by\n" +
                    "	1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.grid codigo,\n" +
                    "	c.cpf,\n" +
                    "	c.rg,\n" +
                    "	c.inscr_est,\n" +
                    "	c.rg_orgao_exp orgaoemissor,\n" +
                    "	c.nome razao,\n" +
                    "	c.nome_reduzido fantasia,\n" +
                    "	c.logradouro,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.cep,\n" +
                    "	c.fone,\n" +
                    "	c.fax,\n" +
                    "	c.celular,\n" +
                    "	c.email,\n" +
                    "	c.contato,\n" +
                    "	c.endereco_c,\n" +
                    "	c.bairro_c,\n" +
                    "	c.cidade_c,\n" +
                    "	c.estado_c,\n" +
                    "	c.fone_c,\n" +
                    "	c.bloqueado,\n" +
                    "	c.sexo,\n" +
                    "	c.data_nasc datanascimento,\n" +
                    "	c.data_cad datacadastro,\n" +
                    "	c.obs observacao,\n" +
                    "	case c.flag when 'A' then 1 else 0 end ativo,\n" +
                    "	c.inscr_municipal,\n" +
                    "	c.limite_credito,\n" +
                    "	c.data_admissao,\n" +
                    "	c.email_nfe,\n" +
                    "	c.produtor_rural,\n" +
                    "	c.nome_mae,\n" +
                    "	c.nome_pai\n" +
                    "FROM \n" +
                    "	pessoa c\n" +
                    "	left join cargo o on c.cargo = o.grid\n" +
                    "WHERE \n" +
                    "	c.tipo ~~ '%%C%%'::bpchar::text\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("codigo"));
                    imp.setCnpj(rst.getString("cpf"));
                    if (Utils.stringToLong(rst.getString("cpf")) <= 99999999999L) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("inscr_est"));
                    }   
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("logradouro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.addContato("C", rst.getString("contato"), "", "", "");
                    imp.setCobrancaEndereco(rst.getString("endereco_c"));
                    imp.setCobrancaBairro(rst.getString("bairro_c"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_c"));
                    imp.setCobrancaUf(rst.getString("estado_c"));
                    imp.setCobrancaTelefone(rst.getString("fone_c"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setInscricaoMunicipal(rst.getString("inscr_municipal"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setDataAdmissao(rst.getDate("data_admissao"));
                    imp.addEmail("NFE", rst.getString("email_nfe"), TipoContato.NFE);
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	m.grid id,\n" +
                    "	m.data emissao,\n" +
                    "	m.documento numerocupom,\n" +
                    "	m.valor, \n" +
                    "	m.obs observacao,\n" +
                    "	m.pessoa id_cliente,\n" +
                    "	m.vencto vencimento\n" +
                    "from\n" +
                    "	movto m\n" +
                    "where\n" +
                    "	m.motivo = 163 and \n" +
                    "	m.child = 0 \n" +
                    "order by\n" +
                    "	data"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
