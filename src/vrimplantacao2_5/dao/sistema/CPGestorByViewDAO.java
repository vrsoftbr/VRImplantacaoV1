package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public class CPGestorByViewDAO extends InterfaceDAO implements MapaTributoProvider {
    
    @Override
    public String getSistema() {
        return ESistema.CPGESTOR.getNome();
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.VENDA_PDV
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.PRAZO_FORNECEDOR,
                OpcaoFornecedor.PRAZO_PEDIDO_FORNECEDOR));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString(""), 
                            rs.getString("") + " - " + 
                                rs.getString("") + " - " + 
                                    rs.getString(""), 
                            rs.getInt(""),
                            rs.getDouble(""), 
                            rs.getDouble("")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString(""));
                    imp.setMerc1Descricao(rs.getString(""));
                    imp.setMerc2ID(rs.getString(""));
                    imp.setMerc2Descricao(rs.getString(""));
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
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	PR_CODINT id,\n" +
                    "	PR_NOME descricao\n" +
                    "FROM \n" +
                    "	vw_exp_produtos_sta \n" +
                    "WHERE \n" +
                    "	PR_MASTER = 'M' AND \n" +
                    "	PR_ATIVO = 'S' AND \n" +
                    "	LJ_ASSOCIACAO = " + getLojaOrigem())) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
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
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	p.pr_codint id,\n" +
                    "	p.pr_nome descricaocompleta,\n" +
                    "	p.PR_NOMEGONDOLA descricaogondola,\n" +
                    "	p.PR_NOMEABREVIADO descricaoreduzida,\n" +
                    "	ean.PR_CBARRA codigobarras,\n" +
                    "	ean.PR_QTDE qtdembalagemvenda,\n" +
                    "	p.SE_CODIG mercadologico,\n" +
                    "	p.cod_depto_ecom,\n" +
                    "	p.UNI_VENDA embalagemvenda,\n" +
                    "	p.PR_VOLUME volume,\n" +
                    "	p.TC_CODIG embalagem,\n" +
                    "	p.PR_ATIVO situacaocadastro,\n" +
                    "	p.PR_PRECOVENDA_ATUAL precovenda,\n" +
                    "	p.PR_MARGEM_BRUTA_SCUSTO margem,\n" +
                    "	p.PR_CUSTO_SEM_ICMS custosemimposto,\n" +
                    "   p.PR_DIAS_VALIDADE validade,\n" +        
                    "	p.PR_PESO_VARIAVEL balanca,\n" +
                    "	p.PR_PESO_LIQUIDO pesoliquido,\n" +
                    "	p.PR_PESO_BRUTO pesobruto,\n" +
                    "	p.PR_DIAS_VALIDADE validade,\n" +
                    "	p.PR_VENDA_PESO_UNIDADE pesavel,\n" +
                    "	p.PR_QTDE_CAIXA qtdembalagemcompra,\n" +
                    "	p.DATA_HORA_INC datacadastro,\n" +
                    "	p.LJ_ASSOCIACAO loja,\n" +
                    "	p.CODIGO_COMPRADOR comprador,\n" +
                    "	p.ESTOQUE_MINIMO estoquemin,\n" +
                    "	p.ncm,\n" +
                    "	ean.PR_CEST cest,\n" +
                    "   p.pr_codigocstent cstcredito,\n" +
                    "	p.pr_codigocstsai cstdebito\n" +        
                    "FROM \n" +
                    "	vw_exp_produtos_sta p\n" +
                    "LEFT JOIN vw_exp_barras_sta ean ON p.PR_CODINT = ean.PR_CODINT \n" +
                    "WHERE \n" +
                    "	p.lj_associacao = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.seteBalanca(rs.getString("balanca").equals("S"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagemvenda"));
                    imp.setTipoEmbalagem(rs.getString("embalagemvenda"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.setTipoEmbalagemCotacao(rs.getString("embalagemcompra"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    
                    if ((rs.getString("PR_CODIGO_MASTER") != null)
                            && (!rs.getString("PR_CODIGO_MASTER").trim().isEmpty())) {
                        imp.setIdFamiliaProduto(rs.getString("PR_CODIGO_MASTER"));
                    }
                    
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(rs.getString("situacaocadastro").equals("S") ? 1 : 0);
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCest(rs.getString("cest"));
                    imp.setNcm(rs.getString("ncm"));
                    
                    /*imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setPiscofinsCstCredito(rs.getString("pisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pisaida"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());*/
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    result.add(new CompradorIMP(rst.getString(""), rst.getString("")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    ""        
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString(""));
                    imp.setRazao(rs.getString(""));
                    imp.setFantasia(rs.getString(""));
                    imp.setCnpj_cpf(rs.getString(""));
                    imp.setIe_rg(rs.getString(""));
                    imp.setBairro(rs.getString(""));
                    imp.setCep(rs.getString(""));
                    imp.setEndereco(rs.getString(""));
                    imp.setNumero(rs.getString(""));
                    imp.setMunicipio(rs.getString(""));
                    imp.setUf(rs.getString(""));
                    imp.setAtivo(rs.getString("").equals(""));
                    imp.setComplemento(rs.getString(""));
                    imp.setTel_principal(rs.getString(""));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString(""));
                    imp.setIdProduto(rs.getString(""));
                    imp.setCodigoExterno(rs.getString(""));
                    imp.setTipoIpi(1);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("nrocpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaorg"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString(""));
                    imp.setNumeroCupom(rs.getString(""));
                    imp.setIdCliente(rs.getString(""));
                    imp.setDataEmissao(rs.getDate(""));
                    imp.setDataVencimento(rs.getDate(""));
                    imp.setParcela(rs.getInt(""));
                    imp.setValor(rs.getDouble(""));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
