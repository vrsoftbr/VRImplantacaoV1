package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
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
 * @author guilhermegomes
 */
public class TeleconDAO extends InterfaceDAO implements MapaTributoProvider {
    
    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "Telecon" + complemento;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.ATACADO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	descricao,\n" +
                    "	aliquota\n" +
                    "from\n" +
                    "	Aliquotas")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), 
                            rs.getString("descricao"), 0, 
                            rs.getDouble("aliquota"), 0));
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      "select \n"
                    + "	codigo,\n"
                    + "	nome\n"
                    + "from \n"
                    + "	lojas")) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	substring(g1.codigo, 0, 3) merc1,\n" +
                    "	(select nome from grupos where len(codigo) = 2 and substring(codigo, 0, 3) = substring(g1.codigo, 0, 3)) descmerc1, \n" +
                    "	substring(g2.codigo, 4, 6) merc2,\n" +
                    "	g2.nome descmerc2\n" +
                    "from \n" +
                    "	produtos p\n" +
                    "join grupos g1 on substring(p.grupo, 0, 3) = substring(g1.codigo, 0, 3)\n" +
                    "join grupos g2 on substring(g2.codigo, 0, 3) = substring(g1.codigo, 0, 3) and\n" +
                    "	 substring(g1.codigo, 4, 6) = substring(g2.codigo, 4, 6)\n" +
                    "order by 1, 3")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
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
                    "SELECT \n" +
                    "    CODIGO,\n" +
                    "    NOME\n" +
                    "FROM \n" +
                    "   PRODUTOS_ASSOCIADOS\n" +
                    "order by\n" +
                    "   2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("nome"));
                    
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
                    "	p.codigo,\n" +
                    "	p.descricao,\n" +
                    "	p.DescricaoReduzida,\n" +
                    "	ean.BARRAS,\n" +
                    "	p.DT_CADASTRO,\n" +
                    "	p.CD_SITUACAO_PRODUTO situacao,\n" +
                    "   pl.ativo,\n" +        
                    "	p.validade,\n" +
                    "	p.diasvalidade,\n" +
                    "	p.grupo,\n" +
                    "	p.CD_ASSOCIADO familia,\n" +
                    "	substring(p.grupo, 0, 3) merc1,\n" +
                    "	substring(p.grupo, 4, 6) merc2,\n" +
                    "	u.sigla unidade,\n" +
                    "	p.QT_EMBALAGEM,\n" +
                    "	p.Qtd_Embalagem_Venda,\n" +
                    "	p.VALOR_NO_PDV,\n" +
                    "	p.ValorVenda,\n" +
                    "	pl.valorProduto,\n" +
                    "	p.MargemLucro,\n" +
                    "	p.custo,\n" +
                    "	p.Qtd estoque,\n" +
                    "	pe.QtdEstoque,\n" +
                    "	p.QtdMinima,\n" +
                    "	aliquota idicms,\n" +
                    "	p.ICMS_COMPRA,\n" +
                    "	p.BASE_REDUZIDA_ICMS,\n" +
                    "	p.st,\n" +
                    "	pis.CSTPisEntrada,\n" +
                    "	pis.CSTPisSaida,\n" +
                    "	p.NaturezaReceita,\n" +
                    "	p.ncm,\n" +
                    "	p.codigocest cest,\n" +
                    "	p.mva,\n" +
                    "	p.AliquotaICMSST,\n" +
                    "	p.BaseReduzidaST,\n" +
                    "	iaa.codgia gia\n" +
                    "from \n" +
                    "	produtos p\n" +
                    "left join Unidades u on p.Unidade = u.Codigo\n" +
                    "left join AliquotasPisCofins pis on p.CodAliquotaPisCofins = pis.codigo\n" +
                    "left join PRODUTO_BARRAS ean on p.codigo = ean.CD_PRODUTO\n" +
                    "left join ProdutoLojas pl on pl.codLoja = " + getLojaOrigem() + " and \n" +
                    "	p.codigo = pl.codProduto\n" +
                    "left join ProdutoEstoqueLoja pe on pl.codLoja = pe.CodLoja and \n" +
                    "	p.codigo = pe.CodProduto\n" +
                    "left join PRODUTOINFADAPURACAOVIGENCIAS pia on pia.CodProduto = p.codigo and\n" +
                    "	pia.Excluido = 0\n" +
                    "left join INFADICIONAISAPURACAO iaa on pia.CodInfAdicionalApuracao = iaa.CodInfAdicionalApuracao\n" +
                    "where \n" +
                    "	pe.CodTipoEstoque = 1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("BARRAS"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setDataCadastro(rs.getDate("DT_CADASTRO"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rs.getDouble("ValorVenda"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rs.getDouble("MargemLucro"));
                    imp.setEstoque(rs.getDouble("QtdEstoque"));
                    imp.setCest(rs.getString("cest"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("NaturezaReceita"));
                    imp.setPiscofinsCstDebito(rs.getString("CSTPisSaida"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    
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
                    "select \n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	cnpj,\n" +
                    "	ie,\n" +
                    "	fantasia,\n" +
                    "	endereco,\n" +
                    "	cidade,\n" +
                    "	bairro,\n" +
                    "	cep,\n" +
                    "	estado,\n" +
                    "	fone,\n" +
                    "	email,\n" +
                    "	obs,\n" +
                    "	ativo\n" +
                    "from \n" +
                    "	fornecedores")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("fone"));
                    
                    String email = rs.getString("email");
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    
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
                    "	cd_produto,\n" +
                    "	cd_fornecedor,\n" +
                    "	cd_no_fornecedor codigoexterno,\n" +
                    "	qtdembalagem\n" +
                    "from\n" +
                    "	fornecedor_produtos")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("cd_produto"));
                    imp.setIdFornecedor(rs.getString("cd_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
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
                    "select \n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	razaosocial,\n" +
                    "	cpf,\n" +
                    "	rg,\n" +
                    "	endereco,\n" +
                    "	bairro,\n" +
                    "	cidade,\n" +
                    "	cep,\n" +
                    "	estado,\n" +
                    "	ddd,\n" +
                    "	fone,\n" +
                    "	datanasc,\n" +
                    "	saldo,\n" +
                    "	conjugue,\n" +
                    "	ativo,\n" +
                    "	email\n" +
                    "from\n" +
                    "	clientes")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("estado"));
                    imp.setDataNascimento(rs.getDate("datanasc"));
                    imp.setValorLimite(rs.getDouble("saldo"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	numero id,\n" +
                    "	data_emissao,\n" +
                    "	vencimento,\n" +
                    "	valor,\n" +
                    "	ordem,\n" +
                    "	codvenda,\n" +
                    "	notafiscal,\n" +
                    "	cliente,\n" +
                    "	complemento\n" +
                    "from \n" +
                    "	faturas\n" +
                    "where \n" +
                    "	cliente is not null and \n" +
                    "	data_pgto is null and \n" +
                    "	loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	vencimento")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCupom(rs.getString("notafiscal"));
                    imp.setObservacao(rs.getString("complemento"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
