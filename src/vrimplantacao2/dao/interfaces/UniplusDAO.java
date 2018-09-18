package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class UniplusDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    
    @Override
    public String getSistema() {
       return "Uniplus";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	cnpj\n" +
                    "from \n" +
                    "	filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id as merc1,\n" +
                    "	nome as descmerc1,\n" +
                    "	id as merc2,\n" +
                    "	nome as descmerc2,\n" +
                    "	id as merc3,\n" +
                    "	nome as descmerc3 \n" +
                    "from \n" +
                    "	hierarquia \n" +
                    "order by\n" +
                    "	id")) {
                while(rs.next()) {
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.codigo,\n" +
                    "	p.ean,\n" +
                    "	p.inativo,\n" +
                    "	p.nome as descricaocompleta,\n" +
                    "	p.nomeecf as descricaoreduzida,\n" +
                    "	p.nome as descricaogondola,\n" +
                    "	p.datacadastro,\n" +
                    "	p.unidademedida as unidade,\n" +
                    "	1 qtdembalagem,\n" +
                    "	p.custoindireto custooperacional,\n" +
                    "	p.precocusto,\n" +
                    "	p.lucrobruto as margembruta,\n" +
                    "	p.percentuallucroajustado as margem,\n" +
                    "   round((((p.preco / case when p.precocusto = 0 then 1 else p.precocusto end) - 1) * 100),2) as margemcalculada,\n" +
                    "	p.percentualmarkup,\n" +
                    "	p.preco as precovenda,\n" +
                    "	p.quantidademinima,\n" +
                    "	p.quantidademaxima,\n" +
                    "	e.quantidade,\n" +
                    "	p.tributacao,\n" +
                    "	p.situacaotributaria as cst,\n" +
                    "	p.cstpis,\n" +
                    "	p.cstcofins,\n" +
                    "	p.cstpisentrada,\n" +
                    "	p.icmsentrada as icmscredito,\n" +
                    "	p.icmssaida as icmsdebito,\n" +
                    "	p.aliquotaicmsinterna,\n" +
                    "	p.pesavel,\n" +
                    "	p.ncm,\n" +
                    "	p.idcest,\n" +
                    "	cest.codigo as cest,\n" +
                    "	p.cstpisentrada,\n" +
                    "	p.cstcofinsentrada,\n" +
                    "	p.idfamilia,\n" +
                    "	p.idhierarquia as merc1,\n" +
                    "	p.idhierarquia as merc2,\n" +
                    "	p.idhierarquia as merc3\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join\n" +
                    "	saldoestoque e on e.idproduto = p.id and e.codigoproduto = p.codigo\n" +
                    "left join\n" +
                    "	cest on cest.id = p.idcest\n" +
                    "order by\n" +
                    "	p.codigo::integer")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setEan(rs.getString("ean"));
                    imp.setSituacaoCadastro(rs.getInt("inativo") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.seteBalanca(rs.getInt("pesavel") == 1 ? true : false);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margemcalculada"));
                    imp.setEstoqueMinimo(rs.getDouble("quantidademinima"));
                    imp.setEstoqueMaximo(rs.getDouble("quantidademaxima"));
                    imp.setEstoque(rs.getDouble("quantidade"));
                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("aliquotaicmsinterna"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpisentrada"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.codigo idproduto,\n" +
                    "	e.codigo idfornecedor,\n" +
                    "	pf.referenciafornecedor\n" +
                    "from\n" +
                    "	produtofornecedor pf\n" +
                    "join\n" +
                    "	produto p on p.id = pf.idproduto\n" +
                    "join\n" +
                    "	entidade e on e.id = pf.idfornecedor \n" +
                    "where\n" +
                    "	e.fornecedor = 1\n" +
                    "order by\n" +
                    "	pf.idproduto, pf.idfornecedor")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("referenciafornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.codigo idproduto,\n" +
                    "	ean.ean,\n" +
                    "	1 qtdembalagem \n" +
                    "from \n" +
                    "	produtoean ean\n" +
                    "join\n" +
                    "	produto p on p.id = ean.idproduto\n" +
                    "order by\n" +
                    "	idproduto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    
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
                    "select\n" +
                    "	e.codigo,\n" +
                    "	e.nome,\n" +
                    "	e.razaosocial,\n" +
                    "	e.tipopessoa,\n" +
                    "	e.cnpjcpf,\n" +
                    "	e.inscricaoestadual,\n" +
                    "	e.rg,\n" +
                    "	e.endereco,\n" +
                    "	e.numeroendereco,\n" +
                    "	e.complemento,\n" +
                    "	e.bairro,\n" +
                    "	e.idestado,\n" +
                    "	est.nome as estado,\n" +
                    "	est.codigoibge as ibgeestado,\n" +
                    "	e.idcidade,\n" +
                    "	c.nome as municipio,\n" +
                    "	c.codigoibge as ibgemunicipio,\n" +
                    "	e.cep,\n" +
                    "	e.telefone,\n" +
                    "	e.celular,\n" +
                    "	e.fax,\n" +
                    "	e.email,\n" +
                    "	e.nascimento,\n" +
                    "	e.limitecredito,\n" +
                    "	e.enderecoentrega,\n" +
                    "	e.numeroenderecoentrega,\n" +
                    "	e.complementoentrega,\n" +
                    "	e.bairroentrega,\n" +
                    "	e.idcidadeentrega,\n" +
                    "	e.cepentrega,\n" +
                    "	e.estadocivil,\n" +
                    "	e.datacadastro,\n" +
                    "	e.inativo\n" +
                    "from\n" +
                    "	entidade e\n" +
                    "left join\n" +
                    "	cidade c on c.id = e.idcidade\n" +
                    "left join\n" +
                    "	estado est on est.id = e.idestado\n" +
                    "where\n" +
                    "	e.fornecedor = 1\n" +
                    "order by\n" +
                    "	e.codigo::integer")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("razaosocial"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if((rs.getString("celular") != null) 
                            && (!"".equals(rs.getString("celular")))) {
                        imp.addContato("Celular", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("fax") != null) 
                            && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("email") != null) 
                            && (!"".equals(rs.getString("email")))) {
                        imp.addContato("Email", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getInt("inativo") == 0 ? true : false);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public List<ProdutoIMP> getEANsAtacados() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	ean,\n" +
                    "	precopauta1,\n" +
                    "	quantidadepauta1\n" +
                    "from\n" +
                    "	produto\n" +
                    "where\n" +
                    "	precopauta1 != 0 and\n" +
                    "	quantidadepauta1 != 0")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setPrecovenda(rs.getDouble("precopauta1"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("quantidadepauta1"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
