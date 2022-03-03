package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public class CPGestorByViewDAO extends InterfaceDAO {

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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	PR_CODINT id,\n"
                    + "	PR_NOME descricao\n"
                    + "FROM \n"
                    + "	vw_exp_produtos_sta \n"
                    + "WHERE \n"
                    + "	PR_MASTER = 'M' AND \n"
                    + "	PR_ATIVO = 'S' AND \n"
                    + "	LJ_ASSOCIACAO = " + getLojaOrigem())) {
                while (rs.next()) {
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
        SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

        List<ProdutoIMP> result = new ArrayList<>();
        Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca
                = new ProdutoBalancaDAO().getProdutosBalanca();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	p.pr_codint id,\n"
                    + "	p.pr_nome descricaocompleta,\n"
                    + "	p.PR_NOMEGONDOLA descricaogondola,\n"
                    + "	p.PR_NOMEABREVIADO descricaoreduzida,\n"
                    + "	ean.PR_CBARRA codigobarras,\n"
                    + "	ean.PR_QTDE qtdembalagemvenda,\n"
                    + "	p.SE_CODIG mercadologico,\n"
                    + " SUBSTR(p.se_codig, 0, 2) mercadologico1,\n"
                    + "	SUBSTR(p.se_codig, 3, 3) mercadologico2,\n" 
                    + "	SUBSTR(p.se_codig, 6, 3) mercadologico3,"        
                    + " p.PR_codigo_MASTER familia,\n"
                    + "	p.cod_depto_ecom,\n"
                    + "	p.UNI_VENDA embalagemvenda,\n"
                    + "	p.PR_VOLUME volume,\n"
                    + "	p.TC_CODIG embalagemcompra,\n"
                    + "	p.PR_ATIVO situacaocadastro,\n"
                    + "	p.PR_PRECOVENDA_ATUAL precovenda,\n"
                    + "	p.PR_MARGEM_BRUTA_SCUSTO margem,\n"
                    + "	p.PR_CUSTO_SEM_ICMS custosemimposto,\n"
                    + " p.PR_DIAS_VALIDADE validade,\n"
                    + "	p.PR_PESO_VARIAVEL balanca,\n"
                    + "	p.PR_PESO_LIQUIDO pesoliquido,\n"
                    + "	p.PR_PESO_BRUTO pesobruto,\n"
                    + "	p.PR_DIAS_VALIDADE validade,\n"
                    + "	p.PR_VENDA_PESO_UNIDADE pesavel,\n"
                    + "	p.PR_QTDE_CAIXA qtdembalagemcompra,\n"
                    + "	p.DATA_HORA_INC datacadastro,\n"
                    + "	p.LJ_ASSOCIACAO loja,\n"
                    + "	p.CODIGO_COMPRADOR comprador,\n"
                    + "	p.ESTOQUE_MINIMO estoquemin,\n"
                    + "	p.ncm,\n"
                    + "	ean.PR_CEST cest,\n"
                    + " p.pr_codigocstent cstcredito,\n"
                    + "	p.pr_codigocstsai cstdebito\n"
                    + "FROM \n"
                    + "	vw_exp_produtos_sta p\n"
                    + "LEFT JOIN vw_exp_barras_sta ean ON p.PR_CODINT = ean.PR_CODINT \n"
                    + "WHERE \n"
                    + "	p.lj_associacao = " + getLojaOrigem())) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagemvenda"));
                    imp.setTipoEmbalagem(rs.getString("embalagemvenda"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.setTipoEmbalagemCotacao(rs.getString("embalagemcompra"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));

                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    
                    ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(imp.getEan(), -2));

                    if (balanca != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(balanca.getValidade() > 1 ? balanca.getValidade() : 0);
                    } else {
                        imp.seteBalanca(rs.getString("balanca").equals("S"));
                        imp.setValidade(rs.getInt("validade"));
                    }

                    if ((rs.getString("familia") != null)
                            && (!rs.getString("familia").trim().isEmpty())) {
                        imp.setIdFamiliaProduto(rs.getString("familia"));
                    }

                    String dataCadastro = rs.getString("datacadastro");

                    if (dataCadastro != null && !dataCadastro.equals("")) {
                        imp.setDataCadastro(SDF.parse(rs.getString("datacadastro")));
                    }
                    
                    imp.setSituacaoCadastro(rs.getString("situacaocadastro").equals("S") ? 1 : 0);
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
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

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	cf_tipo tipo,\n"
                    + "	cf_codig id,\n"
                    + "	cf_razao razao,\n"
                    + "	cf_cgc cnpj,\n"
                    + "	cf_inscr ie,\n"
                    + "	cf_fanta fantasia,\n"
                    + "	cf_ender endereco,\n"
                    + "	cf_bairr bairro,\n"
                    + "	mnc_codig ibgemunicipio,\n"
                    + "	cf_cidad cidade,\n"
                    + "	cf_numero_endereco numero,\n"
                    + "   cf_complemento complemento,\n"
                    + "	cf_uf uf,\n"
                    + "	cf_cep cep,\n"
                    + "	cf_telef1 telefone,\n"
                    + "	cf_telef2 telefone2,\n"
                    + "	cf_fax fax,\n"
                    + "	cf_contato contato,\n"
                    + "	cf_inativo inativo,\n"
                    + "	data_inc datacadastro,\n"
                    + "	cf_prazo_pgto prazopagamento,\n"
                    + "	cf_fornecedor_fabricante fabrincate,\n"
                    + "	cnae,\n"
                    + "	cf_atividade,\n"
                    + "	cf_simples_nacional simples,\n"
                    + "	flg_consumidor_final consumidor,\n"
                    + "	flg_indiedest indicadorie\n"
                    + "FROM \n"
                    + "	vw_exp_forn_sta\n"
                    + "WHERE \n"
                    + "	cf_tipo = 'F'"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setAtivo(rs.getString("inativo").equals("F"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTel_principal(rs.getString("telefone"));

                    String tel2 = rs.getString("telefone2");

                    if (tel2 != null && !tel2.equals("")) {
                        imp.addContato("1", "TELEFONE2", tel2, null, TipoContato.COMERCIAL, null);
                    }

                    String fax = rs.getString("fax");

                    if (fax != null && !fax.equals("")) {
                        imp.addContato("2", "FAX", fax, null, TipoContato.COMERCIAL, null);
                    }

                    String contato = rs.getString("contato");

                    if (contato != null && !contato.equals("")) {
                        imp.addContato("3", contato, null, null, TipoContato.COMERCIAL, null);
                    }

                    imp.setPrazoPedido(rs.getInt("prazopagamento"));

                    int indicadorIE = rs.getInt("indicadorie");

                    imp.setTipoIndicadorIe(
                            indicadorIE == 1 ? TipoIndicadorIE.CONTRIBUINTE_ICMS
                                    : indicadorIE == 2 ? TipoIndicadorIE.CONTRIBUINTE_ISENTO
                                            : TipoIndicadorIE.NAO_CONTRIBUINTE);

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

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	cf_tipo tipo,\n"
                    + "	cf_codig id,\n"
                    + "	cf_razao razao,\n"
                    + "	cf_cgc cnpj,\n"
                    + "	cf_inscr ie,\n"
                    + "	cf_fanta fantasia,\n"
                    + "	cf_ender endereco,\n"
                    + "	cf_bairr bairro,\n"
                    + "	mnc_codig ibgemunicipio,\n"
                    + "	cf_cidad cidade,\n"
                    + "	cf_numero_endereco numero,\n"
                    + " cf_complemento complemento,\n"
                    + "	cf_uf uf,\n"
                    + "	cf_cep cep,\n"
                    + "	cf_telef1 telefone,\n"
                    + "	cf_telef2 telefone2,\n"
                    + "	cf_fax fax,\n"
                    + "	cf_contato contato,\n"
                    + "	cf_inativo inativo,\n"
                    + "	data_inc datacadastro,\n"
                    + "	cf_prazo_pgto prazopagamento,\n"
                    + "	cf_fornecedor_fabricante fabrincate,\n"
                    + "	cnae,\n"
                    + "	cf_atividade,\n"
                    + "	cf_simples_nacional simples,\n"
                    + "	flg_consumidor_final consumidor,\n"
                    + "	flg_indiedest indicadorie\n"
                    + "FROM \n"
                    + "	vw_exp_forn_sta\n"
                    + "WHERE \n"
                    + "	cf_tipo = 'C'")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setAtivo(rs.getString("inativo").equals("F"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    int indicadorIE = rs.getInt("indicadorie");

                    imp.setTipoIndicadorIe(indicadorIE == 1 ? TipoIndicadorIE.CONTRIBUINTE_ICMS
                            : indicadorIE == 2 ? TipoIndicadorIE.CONTRIBUINTE_ISENTO
                                    : TipoIndicadorIE.NAO_CONTRIBUINTE);

                    imp.setFax(rs.getString("fax"));

                    String tel2 = rs.getString("telefone2");

                    if (tel2 != null && !tel2.equals("")) {
                        imp.addContato("1", "TELEFONE2", tel2, null, null);
                    }

                    String contato = rs.getString("contato");

                    if (contato != null && !contato.equals("")) {
                        imp.addContato("2", contato, null, null, null);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
