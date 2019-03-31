package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ChequeIMP;
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
public class JM2OnlineDAO extends InterfaceDAO implements MapaTributoProvider {

    private String descricaoAdicional = "";

    public void setDescricaoAdicional(String descricaoAdicional) {
        this.descricaoAdicional = descricaoAdicional == null ? "" : descricaoAdicional;
        System.out.println(getSistema());
    }
    
    @Override
    public String getSistema() {
        return "JM2Online" + (!"".equals(descricaoAdicional) ? " - " + descricaoAdicional : "");
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, e_razaoSocial from Empresas order by id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("e_razaoSocial")));
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
                    "select\n" +
                    "	g.id merc1,\n" +
                    "	g.descricao merc1_desc,\n" +
                    "	sg.id merc2,\n" +
                    "	sg.descricao merc2_desc\n" +
                    "from\n" +
                    "	ProdutosSubGrupos sg\n" +
                    "	join ProdutosGrupos g on\n" +
                    "		sg.idGrupo = g.id\n" +
                    "order by\n" +
                    "	merc1_desc, merc2_desc"
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricao from ProdutosFamilias order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
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
                    "	p.codigo id,\n" +
                    "	p.id codigosped,\n" +
                    "	p.dataInicio datacadastro,\n" +
                    "	coalesce(ean.ean, cast(p.codigo as varchar)) ean,\n" +
                    "	coalesce(ean.qtdembalagem, 1) qtdembalagem,\n" +
                    "	case p.balanca\n" +
                    "	when 'P' then 'KG'\n" +
                    "	else unidade end tipoEmbalagem,\n" +
                    "	case when p.balanca in ('U', 'P') then 1\n" +
                    "	else 0 end pesavel,\n" +
                    "	p.descricaoInterna descricaocompleta,\n" +
                    "	p.descricaoDaNotaFiscal descricaoreduzida,\n" +
                    "	p.idGrupo merc1,\n" +
                    "	p.idSubGrupo merc2,\n" +
                    "	nullif(p.idFamilia, -1) id_familia,\n" +
                    "	p.peso pesoliquido,\n" +
                    "	p.pesoB pesobruto,\n" +
                    "	p.saldoAtual estoque,\n" +
                    "	pr.margem1 margem,\n" +
                    "	p.precoUltimaEntrada custocomimposto,\n" +
                    "	p.precoUltimaEntradaLiquido custosemimposto,\n" +
                    "	pr.preco1 preco,\n" +
                    "	p.statusProduto status,\n" +
                    "	p.classificacaoFiscal ncm,\n" +
                    "	nullif(p.codigoCEST,-1) cest,\n" +
                    "	p.situacaoTributaria icms_cst,\n" +
                    "	p.aliquotaICMS icms_aliquota,\n" +
                    "	p.reducaoBaseICMS icms_reduzido,\n" +
                    "	pis.tipoPisCofins piscofinsSaida,\n" +
                    "	ent.Codigo codigofabricante\n" +
                    "from\n" +
                    "	Produtos p\n" +
                    "	join Empresas emp on emp.id = " + getLojaOrigem() + "\n" +
                    "	left join Entidades ent on p.idFornecedor = ent.id\n" +
                    "	left join ProdutosEmpresas pe on pe.idProduto = p.id and pe.idEmpresa = emp.id\n" +
                    "	join (select * from ProdutosPrecos where (ativoTabela = 'A' or ativoNormal = 'A' or ativoTabela = 'A')) pr on pr.codigoProduto = p.codigo and pr.idEmpresa = emp.id\n" +
                    "	left join (\n" +
                    "		select \n" +
                    "			p.id idProduto,\n" +
                    "			nullif(ltrim(rtrim(p.codigoDeBarras)),'') ean,\n" +
                    "			1 qtdembalagem\n" +
                    "		from\n" +
                    "			Produtos p\n" +
                    "		where\n" +
                    "			not nullif(ltrim(rtrim(codigoDeBarras)),'') is null\n" +
                    "		union\n" +
                    "		select\n" +
                    "			ean.idProduto,\n" +
                    "			ean.codigoDeBarras ean,\n" +
                    "			ean.quantidadeCB qtdembalagem\n" +
                    "		from\n" +
                    "			ProdutosCodigosDeBarras ean\n" +
                    "	) ean on ean.idProduto = p.id\n" +
                    "	left join ImpostosPDVPISCOFINS pis on pis.codigo = substring(p.impostoPDV,1,1)\n" +
                    "where\n" +
                    "	p.dataFinal is null\n" +
                    "order by\n" +
                    "	p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    boolean isBalanca = rst.getBoolean("pesavel");
                    String ean;
                    
                    if (isBalanca && rst.getString("ean") != null & rst.getString("ean").matches("0*2{1}[0-9]*0{1}")) {
                        String e = Utils.stringLong(rst.getString("ean"));
                        ean = e.substring(1, e.length() - 1); 
                    } else {
                        ean = rst.getString("ean");
                    }
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(ean);
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(isBalanca);
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro("I".equals(rst.getString("status")) ? 0 : 1);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsSaida"));
                    imp.setFornecedorFabricante(rst.getString("codigofabricante"));
                    imp.setCodigoSped(rst.getString("codigosped"));
                                        
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
                    "select\n" +
                    "	e.tipoEntidade,\n" +
                    "	e.codigo,\n" +
                    "	e.razaoSocial,\n" +
                    "	e.fantasia,\n" +
                    "	e.cnpjCPF,\n" +
                    "	e.ieRG,\n" +
                    "	e.codigoSuframa,\n" +
                    "	e.status,\n" +
                    "	e.endereco,\n" +
                    "	e.numero,\n" +
                    "	e.complemento,\n" +
                    "	e.bairro,\n" +
                    "	e.cidade,\n" +
                    "	e.estado,\n" +
                    "	e.cep,\n" +
                    "	e.cobEndereco,\n" +
                    "	e.cobNumero,\n" +
                    "	e.cobComplemento,\n" +
                    "	e.cobBairro,\n" +
                    "	e.cobCidade,\n" +
                    "	e.cobEstado,\n" +
                    "	e.cobCep,\n" +
                    "	e.telefone,\n" +
                    "	e.dataInicio,\n" +
                    "	e.obsFixa,\n" +
                    "	e.prazoLimite,\n" +
                    "	e.prazoLimiteF\n" +
                    "from\n" +
                    "	Entidades e\n" +
                    "where\n" +
                    "	e.tipoEntidade like '%F%' and\n" +
                    "	e.dataFinal is null\n" +
                    "order by\n" +
                    "	e.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razaoSocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpjCPF"));
                    imp.setIe_rg(rst.getString("ieRG"));
                    imp.setSuframa(rst.getString("codigoSuframa"));
                    imp.setAtivo(!"I".equals(rst.getString("status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cobEndereco"));
                    imp.setCob_numero(rst.getString("cobNumero"));
                    imp.setCob_complemento(rst.getString("cobComplemento"));
                    imp.setCob_bairro(rst.getString("cobBairro"));
                    imp.setCob_municipio(rst.getString("cobCidade"));
                    imp.setCob_uf(rst.getString("cobEstado"));
                    imp.setCob_cep(rst.getString("cobCep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("dataInicio"));
                    imp.setObservacao(rst.getString("obsFixa"));
                    imp.setPrazoEntrega(rst.getInt("prazoLimite"));
                    imp.setPrazoSeguranca(rst.getInt("prazoLimiteF"));
                    
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
                    "SELECT\n" +
                    "	codigoFornecedor,\n" +
                    "	codigoProduto,\n" +
                    "	codigoProdutoNoFornecedor,\n" +
                    "	quantidadeEmbalagem\n" +
                    "FROM\n" +
                    "	ProdutosCodigosNoFornecedor\n" +
                    "where\n" +
                    "	codigoFornecedor != -1\n" +
                    "order by\n" +
                    "	1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codigoFornecedor"));
                    imp.setIdProduto(rst.getString("codigoProduto"));
                    imp.setCodigoExterno(rst.getString("codigoProdutoNoFornecedor"));
                    imp.setQtdEmbalagem(rst.getInt("quantidadeEmbalagem"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }    

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	e.codigo id,\n" +
                    "	e.cnpjCPF cnpj,\n" +
                    "	case when upper(e.ieRG) = 'ISENTO' then e.rg else e.ieRG end as ieRG,\n" +
                    "	e.razaoSocial,\n" +
                    "	e.fantasia,\n" +
                    "	e.status,\n" +
                    "	e.vendaBloqueada,\n" +
                    "	e.endereco,\n" +
                    "	e.numero,\n" +
                    "	e.complemento,\n" +
                    "	e.bairro,\n" +
                    "	e.cidade,\n" +
                    "	e.estado,\n" +
                    "	e.cep,\n" +
                    "	e.dataNascimento,\n" +
                    "	e.dataInicio,\n" +
                    "	e.cargoU,\n" +
                    "	e.limiteDeCredito,\n" +
                    "	e.obsFixa,\n" +
                    "	coalesce(e.acVen1, 0) diavencimento,\n" +
                    "	e.telefone,\n" +
                    "	e.emailEntidade email,\n" +
                    "	e.fax,\n" +
                    "	e.cobendereco,\n" +
                    "	e.cobnumero,\n" +
                    "	e.cobcomplemento,\n" +
                    "	e.cobbairro,\n" +
                    "	e.cobcidade,\n" +
                    "	e.cobestado,\n" +
                    "	e.cobcep,\n" +
                    "	e.contato,\n" +
                    "	e2.codigo e2\n" +
                    "from\n" +
                    "	Entidades e\n" +
                    "	left join (\n" +
                    "		select distinct\n" +
                    "			ent.codigo\n" +
                    "		from\n" +
                    "			Contas c\n" +
                    "			left join ContasPDV pdv on\n" +
                    "				pdv.id = c.idGerador\n" +
                    "			left join PDVs on\n" +
                    "				pdv.idPdv = PDVs.id\n" +
                    "			left join Entidades ent on\n" +
                    "				ent.id = c.idEntidade\n" +
                    "		where\n" +
                    "				(c.idEmpresa = " + getLojaOrigem() + ") and \n" +
                    "				not (\n" +
                    "						(\n" +
                    "							(\n" +
                    "								(c.status = N'Q') or \n" +
                    "								(c.status = N'A')\n" +
                    "							) and \n" +
                    "							(c.restante = 0)\n" +
                    "						)\n" +
                    "				) and \n" +
                    "				not ((c.status = N'C')) \n" +
                    "				and (c.tipoDaConta = N'R')\n" +
                    "				and c.numeroNota = 0\n" +
                    "				and not ent.razaoSocial like '%NÃO ESPECIFICADO%'\n" +
                    "	) e2 on e.codigo = e2.codigo\n" +
                    "where\n" +
                    "	(\n" +
                    "		e.tipoEntidade like '%C%' or\n" +
                    "		not e2.codigo is null\n" +
                    "	)\n" +
                    "order by\n" +
                    "	e.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ieRG"));
                    imp.setRazao(rst.getString("razaoSocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(!"I".equals(rst.getString("status")));
                    imp.setBloqueado("S".equals(rst.getString("vendaBloqueada")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("dataNascimento"));
                    imp.setDataCadastro(rst.getDate("dataInicio"));
                    imp.setCargo(rst.getString("cargoU"));
                    imp.setValorLimite(rst.getDouble("limiteDeCredito"));
                    imp.setObservacao2(
                            (
                                    rst.getString("contato") != null && !rst.getString("contato").trim().equals("") ?
                                    "CONTATOS " + rst.getString("contato") + " - " :
                                    ""
                            ) + rst.getString("obsFixa")
                    );
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaEndereco(rst.getString("cobendereco"));
                    imp.setCobrancaNumero(rst.getString("cobnumero"));
                    imp.setCobrancaComplemento(rst.getString("cobcomplemento"));
                    imp.setCobrancaBairro(rst.getString("cobbairro"));
                    imp.setCobrancaMunicipio(rst.getString("cobcidade"));
                    imp.setCobrancaUf(rst.getString("cobestado"));
                    imp.setCobrancaCep(rst.getString("cobcep"));
                    
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
                    "	c.id,\n" +
                    "	c.dataEmissao,\n" +
                    "	c.dataVencimento,\n" +
                    "	pdv.coo,\n" +
                    "	PDVs.idECF ecf,\n" +
                    "	c.valorDaDuplicata,\n" +
                    "	c.obs,\n" +
                    "	ent.codigo idCliente,\n" +
                    "	c.parcela,\n" +
                    "	c.contaJuros,\n" +
                    "	c.valorDaDuplicata - c.restante valorPago\n" +
                    "from\n" +
                    "	Contas c\n" +
                    "	left join ContasPDV pdv on\n" +
                    "		pdv.id = c.idGerador\n" +
                    "	left join PDVs on\n" +
                    "		pdv.idPdv = PDVs.id\n" +
                    "	left join Entidades ent on\n" +
                    "		ent.id = c.idEntidade\n" +
                    "where\n" +
                    "	(c.idEmpresa = " + getLojaOrigem() + ") and \n" +
                    "	not (\n" +
                    "			(\n" +
                    "				(\n" +
                    "					(c.status = N'Q') or \n" +
                    "					(c.status = N'A')\n" +
                    "				) and \n" +
                    "				(c.restante = 0)\n" +
                    "			)\n" +
                    "	) and \n" +
                    "	not ((c.status = N'C')) \n" +
                    "	and (c.tipoDaConta = N'R')\n" +
                    "	and c.numeroNota = 0\n" +
                    "	and not ent.razaoSocial like '%NÃO ESPECIFICADO%'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataEmissao"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));
                    imp.setNumeroCupom(rst.getString("coo"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valorDaDuplicata"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("contaJuros"));
                    if (rst.getDouble("valorPago") > 0) {
                        imp.addPagamento(rst.getString("id"), rst.getDouble("valorPago"),0,0,rst.getDate("dataVencimento"),"");
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.id,\n" +
                    "	c.cpfCliente,\n" +
                    "	c.numeroCheque,\n" +
                    "	c.numeroBanco,\n" +
                    "	c.dataEmissao,\n" +
                    "	c.coo,\n" +
                    "	p.idECF ecf,\n" +
                    "	c.valor\n" +
                    "from\n" +
                    "	ChequesPDV c\n" +
                    "	left join PDVs p on\n" +
                    "		c.idPDV = p.id\n" +
                    "order by\n" +
                    "	c.dataEmissao"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ATIVO,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.OFERTA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.PRECO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO
        ));
    }
    
}
