package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author importacao
 */
public class FocusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FOCUS";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.RECEITA,
            OpcaoProduto.SECAO,
            OpcaoProduto.PRATELEIRA,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE
        }));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.ENDERECO_EMPRESA,
                OpcaoCliente.BAIRRO_EMPRESA,
                OpcaoCliente.COMPLEMENTO_EMPRESA,
                OpcaoCliente.MUNICIPIO_EMPRESA,
                OpcaoCliente.UF_EMPRESA,
                OpcaoCliente.CEP_EMPRESA,
                OpcaoCliente.TELEFONE_EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.DATA_NASCIMENTO_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	tribut id,\n" +
                    "	case when tribut = '01' then '25%'\n" +
                    "	when tribut = '02' then '18%'\n" +
                    "	when tribut = '03' then '12%'\n" +
                    "	when tribut = '04' then '07%'\n" +
                    "	when tribut = '05' then 'ISENTO'\n" +
                    "	when tribut = '06' then 'SUBSTITUIDO'\n" +
                    "	when tribut = '07' then 'NAO ICIDENCIA'\n" +
                    "	when tribut = '08' then '11%'\n" +
                    "	when tribut = '09' then '4,5%'\n" +
                    "	when tribut = '10' then '3,2%'\n" +
                    "	when tribut = '11' then '9,4%'\n" +
                    "	when tribut = '12' then '13,3%'\n" +
                    "	when tribut = '13' then '11,2%'\n" +
                    "	when tribut = '14' then '4,7%'\n" +
                    "	when tribut = '15' then '4,14'\n" +
                    "	when tribut = '16' then '5,5%'\n" +
                    "	else 'ISENTO' end descricao\n" +
                    "from \n" +
                    "	estoque")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	(select depto from deptos where depto = d.depto and secao = 0 and grupo = 0 and subgr = 0) codmerc1,\n" +
                    "	(select descricao from deptos where depto = d.depto and secao = 0 and grupo = 0 and subgr = 0) merc1,\n" +
                    "	(select secao from deptos where depto = d.depto and secao = d.secao and grupo = 0 and subgr = 0) codmerc2,\n" +
                    "	(select descricao from deptos where depto = d.depto and secao = d.secao and grupo = 0 and subgr = 0) merc2,\n" +
                    "	(select grupo from deptos where depto = d.depto and secao = d.secao and grupo = d.grupo and subgr = 0) codmerc3,\n" +
                    "	(select descricao from deptos where depto = d.depto and secao = d.secao and grupo = d.grupo and subgr = 0) merc3,\n" +
                    "	(select subgr from deptos where depto = d.depto and secao = d.secao and grupo = d.grupo and subgr = d.subgr) codmerc4,\n" +
                    "	(select descricao from deptos where depto = d.depto and secao = d.secao and grupo = d.grupo and subgr = d.subgr) merc4\n" +
                    "from \n" +
                    "	deptos d\n" +
                    "where\n" +
                    "	d.secao != 0 and \n" +
                    "	d.grupo != 0 and \n" +
                    "	d.subgr != 0")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("merc1"));
                    imp.setMerc2ID(rs.getString("codmerc2"));
                    imp.setMerc2Descricao(rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("codmerc3"));
                    imp.setMerc3Descricao(rs.getString("merc3"));
                    imp.setMerc4ID(rs.getString("codmerc4"));
                    imp.setMerc4Descricao(rs.getString("merc4"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.id,\n" +
                    "	p.codigo ean,\n" +
                    "	p.CODIGOINTERNO,\n" +
                    "	p.DESCRICAO descricaoreduzida,\n" +
                    "	p.DESCRICAO_INT descricaocompleta,\n" +
                    "	p.un unidade,\n" +
                    "	p.QTDEMB qtdembalagem,\n" +
                    "   p.DEPTO merc1,\n" +
                    "	p.SECAO merc2,\n" +
                    "	p.NBM merc3,\n" +
                    "	p.SUBCAT1 merc4," +        
                    "	p.valdias validade,\n" +
                    "	p.datacad cadastro,\n" +
                    "	p.margem,\n" +
                    "	p.precovenda,\n" +
                    "	p.precomedio,\n" +
                    "	p.PRECOVENDA3 custo,\n" +
                    "	p.PRECOATACADO,\n" +
                    "	p.QTDE estoque,\n" +
                    "	p.MIN estoqueminimo,\n" +
                    "	p.CLFISCAL NCM,\n" +
                    "	p.cest,\n" +
                    "	p.icms,\n" +
                    "	p.CST,\n" +
                    "	p.ecst,\n" +
                    "	p.eicms,\n" +
                    "	p.tribut idicms,\n" +
                    "	p.eiva,\n" +
                    "	p.CSTCOFINS,\n" +
                    "	p.natrec naturezareceita,\n" +
                    "	p.SITUACAO,\n" +
                    "	p.PESOBRUTO,\n" +
                    "	p.PESOLIQUIDO \n" +
                    "from \n" +
                    "	estoque p")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    
                    if (imp.getDescricaoCompleta() == null || imp.getDescricaoCompleta().isEmpty()) {
                        imp.setDescricaoCompleta(imp.getDescricaoReduzida());
                    }
                    
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 0 ? 1 : 0);
                    
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    
                    imp.setMargem(rs.getDouble("margem"));
                    
                    if (imp.getMargem() > 99999999) {
                        imp.setMargem(0d);
                    }
                    
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPiscofinsCstDebito(rs.getString("CSTCOFINS"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	FORNEC id_fornecedor,\n" +
                    "	e.ID id_produto,\n" +
                    "	pf.CODPLU codigoexterno,\n" +
                    "	pf.EMB embalagem,\n" +
                    "	pf.QTEMB qtdembalagem\n" +
                    "from 	\n" +
                    "	plu pf \n" +
                    "join estoque e on pf.codigo = e.CODIGO")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	cliente razao,\n" +
                    "	fantasia,\n" +
                    "	cnpj,\n" +
                    "	insc ie,\n" +
                    "	endereco,\n" +
                    "	bairro,\n" +
                    "	cidade,\n" +
                    "	cep,\n" +
                    "	estado,\n" +
                    "	numero,\n" +
                    "	codmunicip municipio_ibge,\n" +
                    "	telefone,\n" +
                    "	telefone2,\n" +
                    "	telefone3,\n" +
                    "	contato,\n" +
                    "	contato2,\n" +
                    "	contato3,\n" +
                    "	prazo,\n" +
                    "	vendedor,\n" +
                    "	regime,\n" +
                    "	tipocontribuinte,\n" +
                    "	emailxml,\n" +
                    "	datacad cadastro\n" +
                    "from \n" +
                    "	fornec")) { 
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("estado"));
                    imp.setIbge_municipio(rs.getInt("municipio_ibge"));
                    imp.setTel_principal(rs.getString("telefone"));
                    
                    String email = rs.getString("emailxml");
                    
                    if (email != null && !email.trim().isEmpty()) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    String telfone3 = rs.getString("telefone3");
                    
                    if (telfone3 != null && !telfone3.trim().isEmpty()) {
                        imp.addContato("2", "TELEFONE2", telfone3, null, TipoContato.NFE, null);
                    }
                    
                    String contato = rs.getString("vendedor");
                    
                    if (contato != null && !contato.trim().isEmpty()) {
                        imp.addContato("3", contato, null, null, TipoContato.NFE, null);
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	c.id,\n" +
                    "	c.cnpj,\n" +
                    "	c.ie,\n" +
                    "	c.datacad,\n" +
                    "	c.nome,\n" +
                    "	c.FANTASIA,\n" +
                    "	c.EMAIL,\n" +
                    "	c.rua,\n" +
                    "	c.bairro,\n" +
                    "	c.NUMERO,\n" +
                    "	c.Cidade,\n" +
                    "	c.cep,\n" +
                    "	c.Uf,\n" +
                    "	c.TipoPessoa,\n" +
                    "	c.bloqueio,\n" +
                    "	c.fone,\n" +
                    "	c.CELULAR,\n" +
                    "	c.LimiteCrediito,\n" +
                    "	c.OBS,\n" +
                    "	c.CADASTRO,\n" +
                    "	c.NASC,\n" +
                    "	c.DEBITO,\n" +
                    "	c.DIAVENC\n" +
                    "from \n" +
                    "	clientes c")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setEndereco(rs.getString("rua"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id,\n" +
                    "	codcli idcliente,\n" +
                    "	cliente,\n" +
                    "	pedido,\n" +
                    "	pdv,\n" +
                    "	duplicata,\n" +
                    "	valor,\n" +
                    "	dtvencimento,\n" +
                    "	dtemissao\n" +
                    "from \n" +
                    "	dupreceber d\n" +
                    "where \n" +
                    "	ValorPago is null")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setEcf(rs.getString("pdv"));
                    imp.setNumeroCupom(rs.getString("pedido"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
