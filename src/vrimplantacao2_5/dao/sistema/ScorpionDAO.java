package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Alan
 */
public class ScorpionDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Scorpion";
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
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
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
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
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
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	p.COD_TRIBUTACAO||'-'||st.COD_SITUACAOFISCAL id,\n"
                    + "	t.DESCRICAO,\n"
                    + "	st.COD_SITUACAOFISCAL cst_saida,\n"
                    + "	t.ALIQUOTA aliq_saida,\n"
                    + "	t.REDUCAO_BASE red_saida\n"
                    + "FROM\n"
                    + "	TB_PRODUTOS p\n"
                    + "	JOIN TB_TRIBUTACAO t ON p.COD_TRIBUTACAO = t.CODIGO_TRIBUTACAO \n"
                    + "	JOIN TB_SITUACAO_FISCAL st ON p.COD_SITUACAOTRIBUTARIA = st.CODIGO_SITUACAOFISCAL"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliq_saida"),
                            rs.getDouble("red_saida"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PRODUTOPRECOEQUIV id_familia,\n"
                    + "	DESCRICAO familia\n"
                    + "FROM\n"
                    + "	TB_PRODUTO_PRECO_EQUIVALENTE\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("familia"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PRODUTO id_produto,\n"
                    + "	CODIGO_BARRA ean,\n"
                    + "	UNIDADE_REFERENCIA tipo_embalagem,\n"
                    + "	1 qtdembalagem\n"
                    + "FROM\n"
                    + "	TB_PRODUTOS tp\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PRODUTO idproduto,\n"
                    + "	CODIGO_BARRA ean,\n"
                    + "	p.DESCRICAO desc_completa,\n"
                    + "	DESCRICAO_PDV desc_reduzida,\n"
                    + "	UNIDADE_REFERENCIA tipoembalagem,\n"
                    + "	QUANTIDADE_PORCAIXA qtde_emb_compra,\n"
                    + "	CASE WHEN COD_SETORBALANCA IS NOT NULL THEN 1 ELSE 0 END e_balanca,\n"
                    + "	DIAS_VALIDADE validade,\n"
                    + "	p.COD_PRECO_PRODUT_EQUIV familia,\n"
                    + "	PRECO_CUSTO custo,\n"
                    + "	PRECO_VENDA precovenda,\n"
                    + "	MARGEM_LUCRO margem,\n"
                    + "	CAST(p.DATA_ALTERACAO AS date) DATA_ALTERACAO,\n"
                    + "	CAST(p.DATA_CADASTRO AS date) DATA_CADASTRO,\n"
                    + "	CASE WHEN p.SITUACAO = 'A' THEN 1 ELSE 0 END ativo,\n"
                    + "	NCM,\n"
                    + "	cest.COD_CEST CEST,\n"
                    + "	est.SALDO estoque,\n"
                    + "	ESTOQUE_MINIMO estmin,\n"
                    + "	ESTOQUE_MAXIMO estmax,\n"
                    + "	PESO_BRUTO,\n"
                    + "	PESO_LIQUIDO,\n"
                    + "	p.COD_TRIBUTACAO||'-'||st.COD_SITUACAOFISCAL id_debito,\n"
                    + "	CST_PIS_ENT piscof_credito,\n"
                    + "	CST_PIS_ENT piscof_debito\n"
                    + "FROM\n"
                    + "	TB_PRODUTOS p\n"
                    + "	LEFT JOIN TB_CEST cest ON p.COD_CEST = cest.ID_CEST\n"
                    + "	LEFT JOIN TB_PRODUTO_ESTOQUE_LOJA est ON est.COD_PRODUTO = p.CODIGO_PRODUTO \n"
                    + "	JOIN TB_TRIBUTACAO t ON p.COD_TRIBUTACAO = t.CODIGO_TRIBUTACAO \n"
                    + "	JOIN TB_SITUACAO_FISCAL st ON p.COD_SITUACAOTRIBUTARIA = st.CODIGO_SITUACAOFISCAL\n"
                    + "WHERE\n"
                    + "	est.LOJA = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(rst.getString("desc_completa"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
//                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtde_emb_compra"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
//                    imp.setValidade(rst.getInt("validade"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    
                    String idIcmsDebito/*, IdIcmsCredito, IdIcmsForaEstado*/;

                    idIcmsDebito = rst.getString("id_debito");
//                    IdIcmsCredito = rst.getString("id_credito");
//                    IdIcmsForaEstado = rst.getString("id_debito_fe");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
//                    imp.setIcmsDebitoForaEstadoId(IdIcmsForaEstado);
//                    imp.setIcmsDebitoForaEstadoNfId(IdIcmsForaEstado);
//                    imp.setIcmsCreditoId(IdIcmsCredito);
//                    imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);

                    imp.setPiscofinsCstCredito(rst.getString("piscof_credito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscof_debito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	 CODIGO_PRODUTO id_produto,\n"
                    + "	 DESCRICAO,\n"
                    + "	 CAST (INICIO_PROMOCAO AS date) data_ini,\n"
                    + "	 CAST (TERMINO_PROMOCAO AS date) data_fim,\n"
                    + "	 PRECO_VENDA preconormal,\n"
                    + "	 PRECO_PROMOCAO precooferta\n"
                    + "FROM\n"
                    + "	 TB_PRODUTOS\n"
                    + "WHERE\n"
                    + "	 PROMOCAO = 'S'\n"
                    + "	 AND TERMINO_PROMOCAO >= 'now'"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("data_ini"));
                    imp.setDataFim(rs.getDate("data_fim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PESSOA id,\n"
                    + "	RAZAOSOCIAL razao,\n"
                    + "	NOMEFANTASIA fantasia,\n"
                    + "	CNPJ,\n"
                    + "	INSCESTADUAL ie,\n"
                    + "	ENDERECO,\n"
                    + "	NRO numero,\n"
                    + "	COMPLEMENTO,\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE,\n"
                    + " cep,\n"
                    + "	ESTADO uf,\n"
                    + "	FONE1 telefone,\n"
                    + "	EMAIL,\n"
                    + "	DT_CADASTRO data_cad,\n"
                    + "	OBS\n"
                    + "FROM\n"
                    + "	TB_PESSOA p\n"
                    + "WHERE\n"
                    + "	p.CODIGO_PESSOA IN (SELECT COD_FORNECEDORPESSOA FROM TB_FORNECEDORES)\n"
                    + "ORDER BY 1"
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
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("data_cad"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone")));

                    String email = Utils.acertarTexto(rs.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL,
                                (email.length() > 50 ? email.substring(0, 50) : email));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_FORNECEDOR id_fornecedor,\n"
                    + "	COD_PRODUTO id_produto,\n"
                    + "	CODIGO_PRODFORN cod_externo,\n"
                    + " VOLUME qtdembalagem\n"
                    + "FROM\n"
                    + "	TB_PRODUTO_FORNECEDOR\n"
                    + "ORDER BY 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	FP.CODigo_financeiro_p id,\n"
                    + "	COD_CLIENTE_FORNECEDOR id_fornecedor,\n"
                    + "	p.CNPJ,\n"
                    + "	CAST (fp.DATA_EMISSAO AS date)  emissao,\n"
                    + "	CAST (fp.DATA_LANCAMENTO AS date) entrada,\n"
                    + "	fp.VALOR_PARCELA valor,\n"
                    + "	CAST (fp.DATA_VENCIMENTO AS date) vencimento,\n"
                    + "	f.OBS\n"
                    + "FROM\n"
                    + "	TB_FINANCEIRO f\n"
                    + "	LEFT JOIN TB_FINANCEIRO_P fp ON f.CODIGO_FINANCEIRO = fp.COD_FINANCEIRO\n"
                    + "	LEFT JOIN TB_PESSOA p ON p.CODIGO_PESSOA = f.COD_CLIENTE_FORNECEDOR AND p.TIPO = 'F'\n"
                    + "WHERE\n"
                    + "	LOJA = 1\n"
                    + "	AND fp.SITUACAO = 'A'\n"
                    + "	AND fp.DATA_PAGAMENTO IS NULL \n"
                    + "	AND COD_CLIENTE_FORNECEDOR IN\n"
                    + "	(SELECT codigo_pessoa FROM TB_PESSOA WHERE tipo = 'F')"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("entrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PESSOA id,\n"
                    + "	RAZAOSOCIAL razao,\n"
                    + "	NOMEFANTASIA fantasia,\n"
                    + "	CNPJ cnpj_cpf,\n"
                    + "	CASE WHEN rg IS NULL THEN INSCESTADUAL ELSE RG END rg_ie,\n"
                    + "	ENDERECO,\n"
                    + "	NRO numero,\n"
                    + " complemento,\n"
                    + "	BAIRRO,\n"
                    + "	cep,\n"
                    + "	CIDADE,\n"
                    + "	ESTADO uf,\n"
                    + "	FONE1 telefone,\n"
                    + "	celular,\n"
                    + "	CAST (DATANASCIMENTO AS date) dt_nasc,\n"
                    + "	EMAIL,\n"
                    + "	CAST (DT_CADASTRO AS date) dt_cad,\n"
                    + "	LIMITECONVENIO limite,\n"
                    + "	CASE WHEN STATUS = 'B' THEN 1 ELSE 0 END bloqueado,\n"
                    + "	OBS \n"
                    + "FROM\n"
                    + "	TB_PESSOA P\n"
                    + "WHERE\n"
                    + "	TIPO <> 'F'\n"
                    + "	AND p.CODIGO_PESSOA IN (SELECT COD_CLIENTEPESSOA FROM TB_CLIENTES)\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setDataNascimento(rs.getDate("dt_nasc"));
                    imp.setDataCadastro(rs.getDate("dt_cad"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_FINANCEIRO_P id,\n"
                    + "	f.NRO_DOCUMENTO numerocupom,\n"
                    + "	COD_CLIENTE codcli,\n"
                    + "	p.CNPJ cpfcnpj,\n"
                    + "	f.NRO_PDV ecf,\n"
                    + "	VALOR_PARCELA valor,\n"
                    + "	CAST (DATA_EMISSAO AS date) emissao,\n"
                    + "	CAST (DATA_VENCIMENTO AS date) vencimento,\n"
                    + "	fp.obs\n"
                    + "FROM\n"
                    + "	TB_FINANCEIRO_P fp\n"
                    + "	JOIN TB_FINANCEIRO f ON fp.COD_FINANCEIRO = f.CODIGO_FINANCEIRO\n"
                    + "	LEFT JOIN TB_PESSOA p ON p.CODIGO_PESSOA = f.COD_CLIENTE_FORNECEDOR AND p.TIPO = 'C'\n"
                    + "WHERE\n"
                    + "	f.LOJA = " + getLojaOrigem() + "\n"
                    + "	AND COD_FORMAPAGAMENTO = 5\n"
                    + "	AND DATA_PAGAMENTO IS NULL\n"
                    + "	AND fp.SITUACAO = 'A'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(rs.getString("cpfcnpj"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new ScorpionDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ScorpionDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	CODIGO_VENDA id_venda,\n"
                    + "	COO_ECF_NF numerocupom,\n"
                    + "	SERIE_ECF ecf,\n"
                    + "	CAST(DATA_VENDA AS DATE) data,\n"
                    + "	SUBSTRING(DATA_VENDA FROM 12 FOR 8) hora,\n"
                    + "	VALOR_DESCONTO desconto,\n"
                    + "	VALOR_ACRESCIMO acrescimo,\n"
                    + "	VALOR_LIQUIDO_VENDA total,\n"
                    + "	CASE WHEN v.vendaativa = 'N' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "WHERE\n"
                    + "	NUMERO_LOJA = " + idLojaCliente + "\n"
                    + "	AND CAST(DATA_VENDA AS DATE) BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	COD_VENDA id_venda,\n"
                    + "	CODIGO_VENDAITEM id_item,\n"
                    + "	SEQUENCIAL_ITEM_CUPOM nroitem,\n"
                    + "	COD_PRODUTO produto,\n"
                    + "	p.UNIDADE_REFERENCIA unidade,\n"
                    + "	COD_BARRA codigobarras,\n"
                    + "	p.DESCRICAO_PDV descricao,\n"
                    + "	QUANTIDADE,\n"
                    + "	VALOR_UNITARIO precovenda,\n"
                    + "	VALOR_TOTAL total,\n"
                    + "	CASE WHEN CANCELADO = 'S' THEN 1 ELSE 0 END CANCELADO\n"
                    + "FROM\n"
                    + "	TB_VENDA_ITEM vi\n"
                    + "	JOIN TB_VENDA v ON v.CODIGO_VENDA = vi.COD_VENDA\n"
                    + "	JOIN TB_PRODUTOS p ON p.CODIGO_PRODUTO = vi.COD_PRODUTO \n"
                    + "WHERE\n"
                    + "	v.NUMERO_LOJA = " + idLojaCliente + "\n"
                    + "	AND CAST(DATA_VENDA AS DATE) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	ORDER BY 1,3";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
