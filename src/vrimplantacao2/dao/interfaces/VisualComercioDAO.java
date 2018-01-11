package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
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
 * @author Leandro
 */
public class VisualComercioDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Visual Comercio";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	fa_codigo,\n" +
                    "	fa_descricao\n" +
                    "from\n" +
                    "	familias\n" +
                    "order by\n" +
                    "	fa_codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("fa_codigo"));
                    imp.setDescricao(rst.getString("fa_descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	g.gr_codi merc1,\n" +
                    "	g.gr_nome merc1_desc,\n" +
                    "	dp.dp_codigo merc2,\n" +
                    "	dp.dp_descricao merc2_desc,\n" +
                    "	sg.sg_codi merc3,\n" +
                    "	sg.sg_nome merc3_desc\n" +
                    "from \n" +
                    "	produtos p\n" +
                    "	join grupos g on p.pr_grupo = g.gr_codi\n" +
                    "	left join departamentos dp on p.pr_depto = dp.dp_codigo\n" +
                    "	left join sub_grupos sg on p.pr_subg = sg.sg_codi\n" +
                    "order by\n" +
                    "	1, 3, 5"
            )) {
                while (rst.next()) {                    
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.pr_codi id,\n" +
                    "	p.pr_cadastro datacadastro,\n" +
                    "	case when p.pr_balanca > 0 then cast(p.pr_balanca as varchar(20)) else cast(ean.ean as varchar(20)) end ean,\n" +
                    "	p.pr_embalagem qtdembalagem,\n" +
                    "	case when p.pr_unidade = 'PB' then 'KG' else p.pr_unidade end unidade,\n" +
                    "	case when p.pr_balanca > 0 then 1 else 0 end e_balanca,\n" +
                    "	p.pr_validade_balanca validade,\n" +
                    "	p.pr_descricao descricaocompleta,\n" +
                    "	coalesce(p.pr_desc_reduzida, p.pr_descricao) descricaoreduzida,\n" +
                    "	p.pr_descricao descricaogondola,\n" +
                    "	p.pr_grupo merc1,\n" +
                    "	nullif(p.pr_depto, 0) merc2,\n" +
                    "	nullif(p.pr_subg, 0) merc3,\n" +
                    "	nullif(p.pr_familia, 0) id_familia,\n" +
                    "	p.pr_minimo estoqueminimo,\n" +
                    "	p.pr_maximo estoquemaximo,\n" +
                    "	p.pr_atual estoque,\n" +
                    "	p.pr_margem margem,\n" +
                    "	p.pr_custo custo,\n" +
                    "	p.pr_venda precovenda,\n" +
                    "	1 as situacaocadastro,\n" +
                    "	p.pr_ncm ncm,\n" +
                    "	p.pr_cest cest,\n" +
                    "	p.pr_pis_cst_saida piscofins_debito,\n" +
                    "	p.pr_pis_cst_entrada piscofins_credito,\n" +
                    "	p.pr_cod_natureza_receita piscofins_natureza_receita,\n" +
                    "	p.pr_icms icms_id\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	left join (\n" +
                    "		select pr_codi id, cast(pr_barras as varchar(20)) ean from produtos p\n" +
                    /*"		union\n" +
                    "		select pr_codi id, cast(pr_barras1 as varchar(20)) ean from produtos p where not nullif(pr_barras1,'') is null\n" +
                    "		union\n" +
                    "		select pr_codi id, cast(pr_barras2 as varchar(20)) ean from produtos p where not nullif(pr_barras2,'') is null\n" +*/
                    "	) ean on p.pr_codi = ean.id\n" +
                    "order by 1"
            )) {
                while (rst.next()) {                    
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    if (imp.getCodMercadologico2() == null) {
                        imp.setCodMercadologico2("1");
                    }
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    if (imp.getCodMercadologico3() == null) {
                        imp.setCodMercadologico3("1");
                    }
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_debito")));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_credito")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_natureza_receita")));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
        
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	f.fo_codi id,\n" +
                    "	f.fo_nome razao,\n" +
                    "	f.fo_fantasia fantasia,\n" +
                    "	coalesce(nullif(f.fo_cgc, ''),nullif(f.fo_cpf,'')) cnpj,\n" +
                    "	f.fo_inscricao ie,\n" +
                    "	f.fo_endereco endereco,\n" +
                    "	f.fo_numero numero,\n" +
                    "	f.fo_bairro bairro,\n" +
                    "	cd.ci_nome municipio,\n" +
                    "	cd.ci_esta uf,\n" +
                    "	cd.ci_codigo_municipio municipio_ibge,\n" +
                    "	f.fo_cep cep,\n" +
                    "	f.fo_fone tel_principal,\n" +
                    "	f.fo_celular celular,\n" +
                    "	f.FO_CELULAR2 celular2,\n" +
                    "	coalesce(f.FO_EMAIL,'') email,\n" +
                    "	f.FO_FAX fax,\n" +
                    "	f.fo_observacao observacao\n" +
                    "from\n" +
                    "	fornecedores f\n" +
                    "	left join cidades cd on f.fo_cidade = cd.ci_codi\n" +
                    "order by 1"
            )) {
                while (rst.next()) {                    
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(Utils.stringToInt(rst.getString("municipio_ibge")));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(Utils.stringLong(rst.getString("tel_principal")));
                    String celular1 = Utils.stringLong(rst.getString("celular"));
                    String celular2 = Utils.stringLong(rst.getString("celular2"));
                    if (celular1.length() > 8 || celular2.length() > 8) {
                        imp.addContato("A", "CELULARES", celular1, celular2, TipoContato.COMERCIAL, "");
                    }
                    String email = rst.getString("email");
                    if (!"".equals(email)) {
                        imp.addContato("B", "E-MAIL", "", "", TipoContato.COMERCIAL, email.toLowerCase());
                    }
                    String fax = Utils.stringLong(rst.getString("fax"));
                    if (fax.length() > 8) {
                        imp.addContato("C", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    imp.setObservacao(rst.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	pf.pf_fornecedor id_fornecedor,\n" +
                    "	pf.pf_codigo_original id_produto,\n" +
                    "	pf.pf_codigo_produto codigoexterno\n" +
                    "from\n" +
                    "	produto_fornecedor pf\n" +
                    "order by\n" +
                    "	1, 2"
            )) {
                while (rst.next()) {                    
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.cl_codi id,\n" +
                    "	c.cl_nome nome,\n" +
                    "	c.cl_fantasia fantasia, \n" +
                    "	c.cl_cnpj cnpj,\n" +
                    "	c.cl_insc inscricaoestadual,\n" +
                    "	case when c.cl_situacao = 'ATIVO' then 1 else 0 end ativo,\n" +
                    "	case when c.cl_bloq_fe = 's' then 1 else 0 end bloqueado,\n" +
                    "	c.cl_endereco endereco,\n" +
                    "	c.cl_numero numero,\n" +
                    "	c.cl_complemento complemento,\n" +
                    "	c.cl_bairro bairro,\n" +
                    "	cd.ci_nome municipio,\n" +
                    "	cd.ci_esta uf,\n" +
                    "	cd.ci_codigo_municipio municipio_ibge,\n" +
                    "	c.cl_cep cep,\n" +
                    "	c.cl_estado_civil estadocivil,\n" +
                    "	c.cl_nascimento datanascimento,\n" +
                    "	c.cl_data datacadastro,\n" +
                    "	c.CL_TRABALHO empresa,\n" +
                    "	c.cl_fonetrabalho empresatelefone,\n" +
                    "	c.CL_PROFISSAO cargo,\n" +
                    "	c.cl_salario salario,\n" +
                    "	c.cl_limite limite,\n" +
                    "	c.cl_conjuge conjuge,\n" +
                    "	c.cl_nomepai pai,\n" +
                    "	c.cl_nomemae mae,\n" +
                    "	c.CL_OBS observacao,\n" +
                    "	c.CL_DIA_VENCTO dia_vencimento,\n" +
                    "	c.CL_FONE telefone,\n" +
                    "	c.cl_celular celular,\n" +
                    "	nullif(c.CL_EMAIL,'') email,\n" +
                    "	nullif(c.CL_EMAIL2,'') email2,\n" +
                    "	c.cl_fax fax,\n" +
                    "	c.cl_endereco_cob cob_endereco,	\n" +
                    "	c.cl_bairro_cob cob_bairro,\n" +
                    "	cdc.ci_nome cob_municipio,\n" +
                    "	cdc.ci_esta cob_uf,\n" +
                    "	cdc.ci_codigo_municipio cob_municipio_ibge\n" +
                    "from\n" +
                    "	clientes c\n" +
                    "	left join cidades cd on c.cl_cidade = cd.ci_codi\n" +
                    "	left join cidades cdc on c.cl_cidade_cob = cdc.ci_codi\n" +
                    "order by 1"
            )) {
                while (rst.next()) {                    
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setMunicipioIBGE(Utils.stringToInt(rst.getString("municipio_ibge")));
                    imp.setCep(rst.getString("cep"));      
                    TipoEstadoCivil estadoCivil = null;
                    for (TipoEstadoCivil civ: TipoEstadoCivil.values()) {
                        if (civ.getDescricao().equals(rst.getString("estadocivil"))) {
                            estadoCivil = civ;
                        }
                    }
                    imp.setEstadoCivil(estadoCivil != null ? estadoCivil : TipoEstadoCivil.NAO_INFORMADO);
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("dia_vencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    String email2 = rst.getString("email2") != null ? rst.getString("email2").trim().toLowerCase() : "";
                    if (!"".equals(email2)) {
                        imp.addContato("A", "E-MAIL2", "", "", email2);
                    }
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_municipio"));
                    imp.setCobrancaUf(rst.getString("cob_uf"));
                    imp.setCobrancaMunicipioIBGE(Utils.stringToInt(rst.getString("cob_municipio_ibge")));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	r.RE_CODIGO id,\n" +
                    "	r.RE_DATA emissao,\n" +
                    "	r.re_cupom cupom,\n" +
                    "	r.RE_VALOR valor,\n" +
                    "	r.RE_OBSERVACAO observacao,\n" +
                    "	r.re_cliente id_cliente,\n" +
                    "	r.re_vencimento vencimento,\n" +
                    "	r.RE_PARCELA parcela,\n" +
                    "	nullif(c.CL_CNPJ, '') cnpj\n" +
                    "from\n" +
                    "	receber r\n" +
                    "	join clientes c on r.re_cliente = c.cl_codi\n" +
                    "where\n" +
                    "	r.re_pago != '*'\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {                    
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select lo_codigo, lo_descricao from lojas order by 1"
            )) {
                while (rst.next()) {                    
                    result.add(new Estabelecimento(rst.getString("lo_codigo"), rst.getString("lo_descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct pr_icms from produtos order by 1"
            )) {
                while (rst.next()) {                    
                    result.add(new MapaTributoIMP(rst.getString("pr_icms"), rst.getString("pr_icms")));
                }
            }
        }
        
        return result;
    }
    
    
    
}
