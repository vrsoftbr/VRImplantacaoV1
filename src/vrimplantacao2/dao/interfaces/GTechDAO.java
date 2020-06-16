package vrimplantacao2.dao.interfaces;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
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
 * @author Importacao
 */
public class GTechDAO extends InterfaceDAO implements MapaTributoProvider {
    
    public boolean filtrarKgNoSql = false;

    /*
     Para localizar a senha do banco de dados do GTech ou G3 Informática,
     localizar a classe GTechEncriptDAO, passar o texto encriptografado no método Main 
     para retornar a senha do atual banco de dados.
     */
    @Override
    public String getSistema() {
        return "GTech";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	 aliquota id,\n"
                    + "    descricao\n"
                    + "FROM \n"
                    + "	aliquotas_icms\n"
                    + "order by\n"
                    + "	2")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select 1 id, 'Mercado e Sacolao da Economia' razao")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	grupo g\n" +
                    "where\n" +
                    "	EXCLUIDO = 0\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("id"));
                    imp.setMerc1Descricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    private Set<String> identificarBalanca() throws SQLException {
        Set<String> result = new HashSet<>();
        
        if (filtrarKgNoSql) {
            try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.id,\n" +
                        "       ean.ean\n" +
                        "from\n" +
                        "	produto p\n" +
                        "	join (\n" +
                        "		select id, EAN ean from produto where not nullif(ean,'') is null\n" +
                        "		union\n" +
                        "		select id, GTIN ean from produto where not nullif(GTIN,'') is null\n" +
                        "	) ean on\n" +
                        "		p.id = ean.id\n" +
                        "	left join unidade_produto un on\n" +
                        "		(p.id_unidade_produto = un.id)\n" +
                        "where\n" +
                        "	un.NOME = 'KG' and \n" +
                        "	cast(ean.ean as unsigned integer) <= 999999 and\n" +
                        "	length(ean.ean) = 5 and\n" +
                        "	p.DESATIVADO = 0\n" +
                        "order by\n" +
                        "	cast(ean.ean as unsigned integer)"
                )) {
                    while (rs.next()) {
                        result.add(rs.getString("ean"));
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id,\n" +
                    "    p.data_cadastro datacadastro,\n" +
                    "    coalesce(p.DATA_EDICAO, data_cadastro) dataalteracao,\n" +
                    "    ean.ean,\n" +
                    "    coalesce(nullif(p.qtd_por_caixa,0),1) qtdembalagem,\n" +
                    "    un.nome unidade,\n" +
                    "    coalesce(p.balanca_integrada, 0) e_balanca,\n" +
                    "    p.validade,\n" +
                    "    coalesce(p.descricao, p.DESCRICAO_PDV) descricaocompleta,\n" +
                    "    p.descricao_pdv descricaoreduzida,\n" +
                    "    p.id_grupo cod_mercadologico1,\n" +
                    "    p.estoque_max estoquemaximo,\n" +
                    "    p.estoque_min estoqueminimo,\n" +
                    "    p.qtd_estoque estoque,\n" +
                    "    p.lucro margem,\n" +
                    "    p.valor_custo custocomimposto,\n" +
                    "    p.valor_venda preco,\n" +
                    "    p.desativado descontinuado,\n" +
                    "    case p.excluido when 1 then 0 else 1 end situacaocadastro,\n" +
                    "    p.ncm,\n" +
                    "    p.cest,\n" +
                    "    ce.cst piscofinsdebito,\n" +
                    "    cs.cst piscofinscredito,\n" +
                    "    p.COD_NAT_REC piscofinsnatureza,\n" +
                    "    p.ECF_ICMS_ST icmsdebid,\n" +
                    "    p.id_fornecedor,\n" +
                    "    p.qtd_atacado,\n" +
                    "    p.valor_venda_atacado\n" +
                    "from\n" +
                    "	produto p\n" +
                    "	join (\n" +
                    "		select id, EAN ean from produto where not nullif(ean,'') is null\n" +
                    "		union\n" +
                    "		select id, GTIN ean from produto where not nullif(GTIN,'') is null\n" +
                    "	) ean on\n" +
                    "		p.id = ean.id\n" +
                    "	left join unidade_produto un on\n" +
                    "		(p.id_unidade_produto = un.id)\n" +
                    "	left join grupocofins ce on\n" +
                    "		(p.id_grupo_pis_saida = ce.id)\n" +
                    "	left join grupocofins cs on\n" +
                    "		(p.id_grupo_pis_entrada = cs.id)\n" +
                    "order by\n" +
                    "	p.id"
            )) {
                Map<Integer, ProdutoBalancaVO> balancas = new ProdutoBalancaDAO().getProdutosBalanca();
                Set<String> identificarBalanca = identificarBalanca();
                while(rs.next()) {
                    result.add(converterIMP(rs, balancas, identificarBalanca));
                   
                    double valorAtacado = rs.getDouble("valor_venda_atacado");
                    int qtdAtacado = rs.getInt("qtd_atacado");                    
                    
                    if (qtdAtacado > 0 && valorAtacado > 0) {
                        ProdutoIMP produto = converterIMP(rs, balancas, identificarBalanca);
                        produto.setEan(String.format("999999%06d", Integer.parseInt(produto.getImportId())));
                        produto.setQtdEmbalagem(qtdAtacado);
                        produto.setAtacadoPreco(valorAtacado);
                        int ean = Utils.stringToInt(produto.getEan(), -2);   
                        produto.setManterEAN(ean <= 999999 && ean > 0);
                        result.add(produto);
                    }                    
                }
            }
        }
        return result;
    }
    
    private ProdutoIMP converterIMP(ResultSet rs, Map<Integer,
            ProdutoBalancaVO> balancas, Set<String> identificarBalanca) throws SQLException {
        ProdutoIMP imp = new ProdutoIMP();
        imp.setImportSistema(getSistema());
        imp.setImportLoja(getLojaOrigem());
        imp.setImportId(rs.getString("id"));
        imp.setDataCadastro(rs.getDate("datacadastro"));
        imp.setDataAlteracao(rs.getDate("dataalteracao"));
        imp.setEan(rs.getString("ean"));
        
        int ean = Utils.stringToInt(rs.getString("ean"), -2);        
        ProdutoBalancaVO bal = balancas.get(ean);
        if (bal != null && identificarBalanca.contains(imp.getImportId())) {
            imp.seteBalanca(true);
            imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
            imp.setValidade(bal.getValidade());
            imp.setQtdEmbalagem(1);
        } else {
            imp.seteBalanca(rs.getBoolean("e_balanca"));
            imp.setTipoEmbalagem(rs.getString("unidade"));
            imp.setValidade(rs.getInt("validade"));
            imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
        }
        
        imp.setDescricaoCompleta(rs.getString("descricaoreduzida"));
        imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
        imp.setDescricaoGondola(imp.getDescricaoCompleta());
        imp.setCodMercadologico1(rs.getString("cod_mercadologico1"));
        imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
        imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
        imp.setEstoque(rs.getDouble("estoque"));
        imp.setMargem(rs.getDouble("margem"));                    
        imp.setCustoComImposto(rs.getDouble("custocomimposto"));
        imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
        imp.setPrecovenda(rs.getDouble("preco"));
        imp.setDescontinuado(rs.getBoolean("descontinuado"));
        imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
        imp.setNcm(rs.getString("ncm"));
        imp.setCest(rs.getString("cest"));
        imp.setPiscofinsCstDebito(rs.getString("piscofinsdebito"));
        //imp.setPiscofinsCstCredito(rs.getString("piscofinscredito"));
        imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsnatureza"));
        imp.setIcmsDebitoId(rs.getString("icmsdebid"));
        imp.setIcmsDebitoForaEstadoId(rs.getString("icmsdebid"));
        imp.setIcmsDebitoForaEstadoNfId(rs.getString("icmsdebid"));
        imp.setIcmsConsumidorId(rs.getString("icmsdebid"));
        imp.setIcmsCreditoId(rs.getString("icmsdebid"));
        imp.setIcmsCreditoForaEstadoId(rs.getString("icmsdebid"));
        imp.setFornecedorFabricante(rs.getString("id_fornecedor"));
        imp.setManterEAN(ean <= 999999 && ean > 0);
        
        return imp;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> result = new ArrayList<>();
            
            for (ProdutoIMP produto: getProdutos()) {
                if (produto.getQtdEmbalagem() > 0 && produto.getAtacadoPreco() > 0) {                    
                    int ean = Utils.stringToInt(produto.getEan(), -2);
                    produto.setManterEAN(ean <= 999999 && ean > 0);
                    result.add(produto);
                }
            }
            
            return result;
        }
        return null;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 id_fornecedor id,\n" +
                    "    razao_social razao,\n" +
                    "    nome_fantasia fantasia,\n" +
                    "    bairro,\n" +
                    "    cep,\n" +
                    "    municipio,\n" +
                    "    codigo_municipio municipioibge,\n" +
                    "    complemento,\n" +
                    "    numero,\n" +
                    "    endereco,\n" +
                    "    uf,\n" +
                    "    numero_documento cnpj,\n" +
                    "    ie,\n" +
                    "    contato,\n" +
                    "    email,\n" +
                    "    fax,\n" +
                    "    telefone,\n" +
                    "    ativo,\n" +
                    "    data_cadastro,\n" +
                    "    excluido\n" +
                    "from\n" +
                    "	fornecedor\n" +
                    "order by\n" +
                    "	id_fornecedor")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    if((rs.getString("contato")) != null &&(!"".equals(rs.getString("contato")))) {
                        imp.addContato("1", rs.getString("contato"), null, null, TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("email")) != null &&(!"".equals(rs.getString("email")))) {
                        imp.addContato("2", null, null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if((rs.getString("fax")) != null &&(!"".equals(rs.getString("fax")))) {
                        imp.addContato("3", null, rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 id_produto,\n" +
                    "    id_fornecedor,\n" +
                    "    codigo_produto_fornecedor codigoexterno\n" +
                    "from\n" +
                    "	fornecedor_produto\n" +
                    "order by\n" +
                    "	id_fornecedor, id_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id,\n" +
                    "    c.nome,\n" +
                    "    c.nome_fantasia,\n" +
                    "    c.cpf_cnpj,\n" +
                    "    c.cpf,\n" +
                    "    c.inscricao_estadual,\n" +
                    "    c.rg,\n" +
                    "    c.endereco,\n" +
                    "    c.bairro,\n" +
                    "    c.municipio,\n" +
                    "    c.cod_municipio,\n" +
                    "    c.complemento,\n" +
                    "    c.numero,\n" +
                    "    c.estado,\n" +
                    "    c.cep,\n" +
                    "    c.telefone,\n" +
                    "    c.celular,\n" +
                    "    c.observacao,\n" +
                    "    case\n" +
                    "    	when c.limite > 9999999 then 9999999\n" +
                    "    	when c.limite < -9999999 then -9999999 \n" +
                    "    	else c.limite \n" +
                    "    end limite,\n" +
                    "    c.data_nasc,\n" +
                    "    c.nome_mae,\n" +
                    "    c.nome_pai,\n" +
                    "    c.DATA_INSERCAO data_cadastro,\n" +
                    "    c.excluido\n" +
                    "from\n" +
                    "	cliente c\n" +
                    "order by\n" +
                    "	id"
            )) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome_fantasia"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));  
                    if(rs.getString("inscricao_estadual") == null && "".equals(rs.getString("inscricao_estadual"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("inscricao_estadual"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("cod_municipio"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getInt("excluido") == 0);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	pr.id_parcelas_receber id,\n" +
                    "	cr.id_cliente,\n" +
                    "	pr.data_emissao_parcela,\n" +
                    "	pr.data_emissao_parcela dataemissao,\n" +
                    "	cr.documento numerocupom,\n" +
                    "	pr.valor,\n" +
                    "	pr.parcelas,\n" +
                    "	cr.referente observacoes,\n" +
                    "	pr.data_vencimento_parcelas_receber datavencimento,\n" +
                    "	pr.valor_pago\n" +
                    "from\n" +
                    "	parcelas_receber pr\n" +
                    "	join contas_receber cr on\n" +
                    "		pr.id_contas_receber = cr.id_contas_receber\n" +
                    "where\n" +
                    "	pr.pago in ('NAO','PG PARCIAL')\n" +
                    "order by\n" +
                    "	pr.id_parcelas_receber")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setParcela(Utils.stringToInt(rs.getString("parcelas"), 1));
                    imp.setObservacao(rs.getString("observacoes"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    double valorPago = rs.getDouble("valor_pago");
                    if (valorPago > 0) {
                        imp.addPagamento(
                                imp.getId(),
                                valorPago,
                                0,
                                0,
                                imp.getDataVencimento(),
                                ""
                        );
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
