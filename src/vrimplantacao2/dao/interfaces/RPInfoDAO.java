package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class RPInfoDAO extends InterfaceDAO {
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select unid_codigo, unid_reduzido from unidades order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("unid_codigo"), rst.getString("unid_reduzido")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "RPInfo";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA
        }));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	d.dpto_codigo merc1,\n" +
                    "	d.dpto_descricao merc1_desc,\n" +
                    "	g.grup_codigo merc2,\n" +
                    "	g.grup_descricao merc2_desc\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	join departamentos d on p.prod_dpto_codigo = d.dpto_codigo\n" +
                    "	join grupos g on p.prod_grup_codigo = g.grup_codigo\n" +
                    "order by\n" +
                    "	1, 3"
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select prod_codpreco, prod_descricao from produtos where prod_codpreco != 0 order by prod_codpreco"
            )) {
                ProdutoParaFamiliaHelper gerador = new ProdutoParaFamiliaHelper();
                while (rst.next()) {
                    gerador.gerarFamilia(rst.getString("prod_codpreco"), rst.getString("prod_descricao"), result);
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
                    ""
            )) {
                while (rst.next()) {
                
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
                    "	f.forn_codigo id,\n" +
                    "	f.forn_nome nomefantasia,\n" +
                    "	f.forn_razaosocial razaosocial,\n" +
                    "	f.forn_cnpjcpf cnpjcpf,\n" +
                    "	f.forn_inscricaoestadual ierg,\n" +
                    "	f.forn_inscricaomunicipal inscmun,\n" +
                    "	f.forn_status,\n" +
                    "	f.forn_endereco endereco,\n" +
                    "	f.forn_endereconumero numero,\n" +
                    "	f.forn_enderecocompl complemento,\n" +
                    "	f.forn_enderecoind,\n" +
                    "	f.forn_bairro bairro,\n" +
                    "	m.muni_codigoibge municipioIBGE,\n" +
                    "	m.muni_nome municipio,\n" +
                    "	m.muni_uf uf,\n" +
                    "	f.forn_cep cep,\n" +
                    "	f.forn_fone,\n" +
                    "	f.forn_foneindustria,\n" +
                    "	f.forn_fax,\n" +
                    "	f.forn_faxindustria,\n" +
                    "	f.forn_email,\n" +
                    "	f.forn_datacad datacadastro,\n" +
                    "	f.forn_obspedidos,\n" +
                    "	f.forn_obstrocas\n" +
                    "from\n" +
                    "	\n" +
                    "	fornecedores f\n" +
                    "	join municipios m on\n" +
                    "		f.forn_muni_codigo = m.muni_codigo\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setCnpj_cpf(rst.getString("cnpjcpf"));
                    imp.setIe_rg(rst.getString("ierg"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setAtivo(!"S".equals(rst.getString("forn_status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("municipioIBGE"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("forn_fone"));
                    imp.addTelefone("TEL INDUSTRIA", rst.getString("forn_foneindustria"));
                    imp.addTelefone("FAX", rst.getString("forn_fax"));
                    imp.addTelefone("FAX INDUSTRIA", rst.getString("forn_faxindustria"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("forn_obspedidos") + " " + rst.getString("forn_obstrocas"));
                    
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
                    "	c.clie_codigo id,\n" +
                    "	c.clie_cnpjcpf cnpjcpf,\n" +
                    "	c.clie_rgie inscricaoestadual,\n" +
                    "	c.clie_orgexprg orgaoemissor,\n" +
                    "	c.clie_razaosocial razaosocial,\n" +
                    "	c.clie_nome nomefantasia,\n" +
                    "	c.clie_status,\n" +
                    "	c.clie_situacao,\n" +
                    "	c.clie_endres endereco,\n" +
                    "	c.clie_endresnumero numero,\n" +
                    "	c.clie_endrescompl complemento,\n" +
                    "	c.clie_bairrores bairro,\n" +
                    "	mr.muni_codigoibge municipioIBGE,\n" +
                    "	mr.muni_nome municipio,\n" +
                    "	mr.muni_uf uf,\n" +
                    "	c.clie_cepres cep,\n" +
                    "	c.clie_estadocivil estadocivil,\n" +
                    "	c.clie_dtcad datacadastro,\n" +
                    "	c.clie_sexo sexo,\n" +
                    "	c.clie_empresa empresa,\n" +
                    "	c.clie_endcom com_endereco,\n" +
                    "	c.clie_endcomnumero com_numero,\n" +
                    "	c.clie_endcomcompl com_compl,\n" +
                    "	c.clie_bairrocom com_bairro,\n" +
                    "	mc.muni_codigoibge com_municipioIBGE,\n" +
                    "	mc.muni_nome com_municipio,\n" +
                    "	mc.muni_uf com_uf,\n" +
                    "	c.clie_cepcom com_cep,\n" +
                    "	c.clie_foneres,\n" +
                    "	c.clie_fonecel,\n" +
                    "	c.clie_fonecom,\n" +
                    "	c.clie_fonecelcom,\n" +
                    "	c.clie_email,\n" +
                    "	c.clie_emailnfe,\n" +
                    "	c.clie_dtadmissao dataadmissao,\n" +
                    "	c.clie_funcao cargo,\n" +
                    "	c.clie_rendacomprovada renda,\n" +
                    "	c.clie_limiteconv limite,\n" +
                    "	c.clie_obs observacao,\n" +
                    "	c.clie_diavenc diavencimento,\n" +
                    "	c.clie_sitconv permitecreditorotativo,\n" +
                    "	c.clie_sitcheque permitecheque,\n" +
                    "	c.clie_senhapdv senhapdv,\n" +
                    "	c.clie_limitetotalcdc limite\n" +
                    "from\n" +
                    "	clientes c\n" +
                    "	left join municipios mr on\n" +
                    "		c.clie_muni_codigo_res = mr.muni_codigo\n" +
                    "	left join municipios mc on\n" +
                    "		c.clie_muni_codigo_com = mc.muni_codigo\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setAtivo(!"S".equals(rst.getString("clie_status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("municipioIBGE"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("com_endereco"));
                    imp.setEmpresaNumero(rst.getString("com_numero"));
                    imp.setEmpresaComplemento(rst.getString("com_compl"));
                    imp.setEmpresaBairro(rst.getString("com_bairro"));
                    imp.setEmpresaMunicipioIBGE(rst.getInt("com_municipioIBGE"));
                    imp.setEmpresaMunicipio(rst.getString("com_municipio"));
                    imp.setEmpresaUf(rst.getString("com_uf"));
                    imp.setEmpresaCep(rst.getString("com_cep"));
                    imp.setTelefone(rst.getString("clie_foneres"));
                    imp.setCelular(rst.getString("clie_fonecel"));
                    imp.setEmpresaTelefone(rst.getString("clie_fonecom"));
                    imp.addCelular("CEL COMERCIAL", rst.getString("clie_fonecelcom"));
                    imp.addEmail(rst.getString("clie_email"), TipoContato.COMERCIAL);
                    imp.addEmail(rst.getString("clie_emailnfe"), TipoContato.NFE);
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setPermiteCreditoRotativo("S".equals(rst.getString("permitecreditorotativo")));
                    imp.setPermiteCheque("S".equals(rst.getString("permitecheque")));
                    imp.setSenha(rst.getInt("senhapdv"));
                    imp.setLimiteCompra(rst.getDouble("limite"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
    
}
