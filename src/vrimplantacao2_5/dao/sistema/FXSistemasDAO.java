package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/*
 *
 * @author Michael
 *
 */
public class FXSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FXSistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
        ));
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
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.OBSERVACAO));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "WITH cst AS (\n"
                    + "SELECT \n"
                    + "	DISTINCT \n"
                    + "	ID_TRIBUTACAO_SAIDAS,\n"
                    + "	substring (CST_SAIDA FROM 2 FOR 4) cst\n"
                    + "FROM\n"
                    + "	PRODUTO\n"
                    + "	WHERE ID_TRIBUTACAO_SAIDAS IS NOT null\n"
                    + "	)\n"
                    + "SELECT \n"
                    + "	cst.cst,\n"
                    + "	ALIQUOTA,\n"
                    + "	COALESCE (PERCENTUAL, 0) AS REDUCAO\n"
                    + "FROM\n"
                    + "	NF_CALCULO i\n"
                    + "JOIN\n"
                    + "	cst ON\n"
                    + "	i.ID = cst.ID_TRIBUTACAO_SAIDAS"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("aliquota") + "-" + rs.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
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
                    + "	id, \n"
                    + "	COD_BARRAS AS ean,\n"
                    + "	1 AS qtdembalagem\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "WHERE COD_BARRAS <> ''"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.ID Id,\n"
                    + "	p.DATA_CADASTRO dataCadastro,\n"
                    + "	p.DATA_ALTERACAO dataAlteracao,\n"
                    + "	p.COD_BARRAS ean,\n"
                    + "	CASE \n"
                    + "	WHEN p.COD_BARRAS LIKE '200%'\n"
                    + "	THEN SUBSTRING (p.COD_BARRAS FROM 2 FOR 11) \n"
                    + "	WHEN p.COD_BARRAS LIKE '789%' \n"
                    + "	THEN SUBSTRING (p.COD_BARRAS FROM 4 FOR 9)\n"
                    + "	WHEN p.COD_BARRAS = '' \n"
                    + "	THEN p.id\n"
                    + "	ELSE p.id \n"
                    + "	END cod_balanca,\n"
                    + "	'1' as qtdEmbalagem,\n"
                    + "	e.DESCRICAO tipoEmbalagem,\n"
                    + "	p.BALANCA e_balanca,\n"
                    + "	p.DESCRICAO descricaoCompleta,\n"
                    + "	p.DESCRICAO_FISCAL descricaoReduzida,\n"
                    + "	p.DESCRICAO descricaoGondola,    \n"
                    + "    SUBSTRING (gp.ID FROM 1 FOR 2 ) codMercadologico1,\n"
                    + "    SUBSTRING (gp.ID FROM 4 FOR 5 ) codMercadologico2,\n"
                    + "    SUBSTRING (gp.ID FROM 4 FOR 5 ) codMercadologico3,\n"
                    + "    p.PESO_BRUTO pesoBruto,\n"
                    + "    p.PESO_LIQUIDO pesoLiquido,\n"
                    + "    pe.ESTOQUE_MAXIMO estoqueMaximo,\n"
                    + "    pe.ESTOQUE_MINIMO estoqueMinimo,\n"
                    + "    pe.ESTOQUE_ATUAL estoque,\n"
                    + "    p.MARGEM_LUCRO_BRUTO margem,\n"
                    + "    p.PRECO_CUSTO_LIQUIDO custoSemImposto, \n"
                    + "    p.PRECO_CUSTO custoComImposto,\n"
                    + "    p.PRECO_VENDA precovenda,  \n"
                    + "    CASE \n"
                    + "		WHEN STATUS = '0'\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "    END situacaoCadastro,\n"
                    + "    p.NCM ncm,\n"
                    + "    p.ID_CEST cest, \n"
                    + "    p.PIS_COFINS_CST piscofinsCstDebito,\n"
                    + "    p.PIS_COFINS_CST_ENTRADA piscofinsCstCredito,\n"
                    + "    P.PIS_COFINS_NATUREZA_RECEITA piscofinsNaturezaReceita,\n"
                    + "    p.ALIQ_ICMS icmsAliqEntrada,\n"
                    + "    p.BASEREDPERC_ICMS icmsReducaoEntrada, \n"
                    + "    p.CST_SAIDA icmsCstSaida,\n"
                    + "    p.BASEREDPERC_ICMS icmsReducaoSaida,\n"
                    + "    COALESCE (p.CST_SAIDA_EXT = NULL, '040') icmsCstSaidaForaEstado\n"
                    + "FROM PRODUTO p \n"
                    + "LEFT JOIN FNC_EMBALAGENS e ON p.ID_EMBALAGENS = e.ID \n"
                    + "LEFT JOIN GRUPO_PRODUTOS gp ON p.GRUPO_PRODUTOS = gp.ID \n"
                    + "LEFT JOIN PRODUTO_ESTOQUE pe ON p.ID = pe.ID_PRODUTO \n"
                    + "WHERE pe.ID_EMPRESA = " + getLojaOrigem() + " \n"
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("Id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    imp.setDataCadastro(rs.getDate("dataCadastro"));
                    imp.setDataAlteracao(rs.getDate("dataAlteracao"));

                    imp.setCodMercadologico1("codMercadologico1");
                    imp.setCodMercadologico2("codMercadologico2");
                    imp.setCodMercadologico3("codMercadologico3");
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoqueMaximo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoqueMinimo"));
                    imp.setPesoBruto(rs.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoLiquido"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rs.getDouble("custoComImposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaoCadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsAliqEntrada(rs.getDouble("icmsAliqEntrada"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icmsReducaoEntrada"));
                    imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsReducaoSaida"));
                    imp.setIcmsCstSaidaForaEstado(rs.getInt("icmsCstSaidaForaEstado"));

                    imp.setPiscofinsCstDebito(rs.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rs.getInt("piscofinsCstCredito"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("piscofinsNaturezaReceita"));

                    int codigoProduto = Utils.stringToInt(rs.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

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
                    + "	ID importId,\n"
                    + "    NOME_FANTASIA razao, \n"
                    + "    NOME fantasia,\n"
                    + "    CNPJ cnpj_cpf,\n"
                    + "    INSCRICAO_ESTADUAL ie_rg,\n"
                    + "    INSCRICAO_MUNICIPAL insc_municipal,\n"
                    + "    INSCRICAO_SUFRAMA suframa,\n"
                    + "    CASE \n"
                    + "		WHEN STATUS = '0' --SITUACAO\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "    END ativo,\n"
                    + "    ENDERECO endereco,\n"
                    + "    NUMERO numero,\n"
                    + "    COMPLEMENTO complemento,\n"
                    + "    BAIRRO bairro,\n"
                    + "    CIDADE municipio,\n"
                    + "    UF uf,\n"
                    + "    CEP cep,\n"
                    + "    DDD||FONE fone1,\n"
                    + "    FAX fax,"
                    + "    EMAIL email,"
                    + "    FATURAMENTO_MINIMO valor_minimo_pedido,\n"
                    + "    ASSINATURA_CONTRATO datacadastro,\n"
                    + "    OBSERVACOES observacao\n"
                    + "FROM\n"
                    + "	FOR_CLI\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setInsc_municipal("insc_municipal");
                    imp.setSuframa("suframa");
                    imp.setAtivo(rs.getInt("ativo") == 1);

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setValor_minimo_pedido(rs.getFloat("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(rs.getString("fone1"));

                    String fax = (rs.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rs.getString("email"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	m1.m1,\n"
                    + "	m1.desc1 m1desc,\n"
                    + "	m2.m2,\n"
                    + "	m2.desc2 m2desc,\n"
                    + "	m3.m3,\n"
                    + "	m3.desc3 m3desc\n"
                    + "FROM\n"
                    + "	(SELECT\n"
                    + "		SUBSTRING (g.ID	FROM 1 FOR 2 ) m1,\n"
                    + "		g.DESCRICAO desc1\n"
                    + "	FROM\n"
                    + "		GRUPO_PRODUTOS g\n"
                    + "	WHERE\n"
                    + "		g.TIPO = 1\n"
                    + "	) m1\n"
                    + "JOIN (SELECT\n"
                    + "		SUBSTRING (g.ID	FROM 1 FOR 2 ) m1,\n"
                    + "		SUBSTRING (g.ID	FROM 4 FOR 5 ) m2,\n"
                    + "		g.DESCRICAO desc2\n"
                    + "	FROM\n"
                    + "		GRUPO_PRODUTOS g\n"
                    + "	WHERE\n"
                    + "		g.TIPO = 0\n"
                    + "	) m2 ON\n"
                    + "		m1.m1 = m2.m1\n"
                    + "JOIN (\n"
                    + "	SELECT\n"
                    + "		SUBSTRING (g.ID	FROM 1 FOR 2 ) m1,\n"
                    + "		SUBSTRING (g.ID	FROM 4 FOR 5 ) m2,\n"
                    + "		SUBSTRING (g.ID	FROM 4 FOR 5 ) m3,\n"
                    + "		g.DESCRICAO desc3\n"
                    + "	FROM\n"
                    + "		GRUPO_PRODUTOS g\n"
                    + "	WHERE\n"
                    + "		g.TIPO = 0\n"
                    + "	) m3 ON\n"
                    + "		m2.m1 = m3.m1\n"
                    + "	AND m2.m2 = m3.m2\n"
                    + "ORDER BY\n"
                    + "	1"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("m1desc"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("m2desc"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("m3desc"));

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
                    + "	pf.COD_FORNECEDOR id_fornecedor,\n"
                    + "	d.CODPRO id_produto,\n"
                    + "	pf.REFFORNEC cod_externo\n"
                    + "FROM\n"
                    + "	FORNECPROD pf\n"
                    + "	JOIN TDERIVACAO d ON d.CODDER = pf.CODDER\n"
                    + "WHERE\n"
                    + "	pf.REFFORNEC != '' AND pf.REFFORNEC IS NOT NULL\n"
                    + "ORDER BY 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ch.ID id,\n"
                    + "	cl.CGC cpfCnpj,\n"
                    + "	ch.MOME_CLIENTE nome,\n"
                    + "	ch.N_CHEQUE numeroCheque,\n"
                    + "	b.NOME banco,\n"
                    + "	ch.AGENCIA agencia,\n"
                    + "	ch.CONTA_CHEQUE conta,\n"
                    + "	ch.DATA_ENTREGA DATA,\n"
                    + "	ch.DATA_PREDATADO dataDeposito,\n"
                    + "	ch.VALOR valor,\n"
                    + "	ch.CONTATO telefone\n"
                    + "FROM\n"
                    + "	GER_CHEQUES_RECEBIDOS ch\n"
                    + "JOIN BANCO_AGENCIAS b ON\n"
                    + "	ch.BANCO = b.ID_BANCO\n"
                    + "JOIN CADCLI cl ON\n"
                    + "	ch.ID_CLIENTE = ID_CLIENTE"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDate(rs.getDate("DATA"));
                    imp.setDataDeposito(rs.getDate("dataDeposito"));
                    imp.setNumeroCheque(rs.getString("numeroCheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setCpf(rs.getString("cpfCnpj"));
                    imp.setNome(rs.getString("nome"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setTelefone(rs.getString("telefone"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	DATA_INICIAL dataInicio,\n"
                    + "	DATA_FINAL dataFinal,\n"
                    + "	ID_PRODUTO idProduto,\n"
                    + "	p.PRECO_VENDA precoNormal,\n"
                    + "	NOVO_VALOR precoOferta\n"
                    + "FROM\n"
                    + "	FNC_PROMOCAO_PRODUTOS g\n"
                    + "JOIN PRODUTO p ON\n"
                    + "	g.ID_PRODUTO = p.ID\n"
                    + "WHERE\n"
                    + "	ID_EMPRESA = " + getLojaOrigem() + "\n"
                    + "	AND DATA_INICIAL <= 'now' AND NOVO_VALOR < p.PRECO_VENDA\n"
                    + "ORDER BY 1, 2"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idProduto"));
                    imp.setDataInicio(rs.getDate("dataInicio"));
                    imp.setDataFim(rs.getDate("dataFinal"));
                    imp.setPrecoNormal(rs.getDouble("precoNormal"));
                    imp.setPrecoOferta(rs.getDouble("precoOferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);

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
                    + "	CODIGO AS id,\n"
                    + "    CGC AS cnpj,\n"
                    + "    IE AS inscricaoestadual,\n"
                    + "    ORGAOEXPEDIDOR AS orgaoemissor,\n"
                    + "    RAZAOSOCIAL AS razao,\n"
                    + "    NOME AS fantasia,\n"
                    + "    CASE \n"
                    + "    	WHEN OBS = 'ATV' \n"
                    + "    	THEN 1\n"
                    + "    	ELSE 0 \n"
                    + "    END ativo,\n"
                    + "    ENDERECO AS endereco,\n"
                    + "    NUMERO AS numero,\n"
                    + "    END_COMPLEMENTO AS complemento,\n"
                    + "    BAIRRO AS bairro,\n"
                    + "    CIDADE AS municipio,\n"
                    + "    UF AS uf,\n"
                    + "    CEP AS cep,\n"
                    + "    CIVIL AS estadoCivil,\n"
                    + "    NASC AS dataNascimento,\n"
                    + "    DT_INC AS dataCadastro,\n"
                    + "    EMPRESA AS empresa,\n"
                    + "    EMP_ENDERECO AS empempresaEndereco,\n"
                    + "    END_COMPLEMENTO AS empresaComplemento,\n"
                    + "    EMP_BAIRRO AS empresaBairro,\n"
                    + "    EMP_CIDADE AS empresaMunicipio,\n"
                    + "    EMP_UF AS empresaUf,\n"
                    + "    EMP_CEP AS empresaCep,\n"
                    + "    EMP_FONE AS empresaTelefone,\n"
                    + "    EMP_ADMISSAO AS dataAdmissao,\n"
                    + "    EMP_FUNCAO AS cargo,\n"
                    + "    EMP_RENDA AS salario,\n"
                    + "    CONJUGE AS nomeConjuge,\n"
                    + "    CPF_CO AS cpfConjuge,\n"
                    + "    NAS_CO AS dataNascimentoConjuge,\n"
                    + "    PAI AS nomePai,\n"
                    + "    MAE AS nomeMae,\n"
                    + "    DATAPAGTO AS diaVencimento,\n"
                    + "    DDD||FONE AS telefone,\n"
                    + "    FONE1 AS celular,\n"
                    + "    EMAIL AS email\n"
                    + "FROM\n"
                    + "	CADCLI\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setDataNascimento(rs.getDate("dataNascimento"));
                    imp.setDataCadastro(rs.getDate("dataCadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone("telefone");
                    imp.setOrgaoemissor("orgaoemissor");

                    imp.setEstadoCivil("estadoCivil");
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setNomeConjuge(rs.getString("nomeConjuge"));
                    imp.setNomeMae(rs.getString("nomeMae"));
                    imp.setNomePai(rs.getString("nomePai"));
                    imp.setEmpresa("empresa");
                    imp.setEmpresaEndereco("empresaEndereco");
                    imp.setEmpresaComplemento("empresaComplemento");
                    imp.setEmpresaBairro("empresaBairro");
                    imp.setEmpresaMunicipio("empresaMunicipio");
                    imp.setEmpresaUf("empresaUf");
                    imp.setEmpresaCep("empresaCep");
                    imp.setEmpresaTelefone("empresaTelefone");
                    imp.setDataAdmissao(rs.getDate("dataAdmissao"));
                    imp.setCargo("cargo");
                    imp.setEmpresaTelefone(rs.getString("empresaTelefone"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setDiaVencimento(rs.getInt("diaVencimento"));

                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));

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
                    + "	fcp.id id,\n"
                    + "	ID_FORNECEDOR idFornecedor,\n"
                    + "	fc.CNPJ CNPJ ,\n"
                    + "	DOCUMENTO numeroDocumento,\n"
                    + "	EMISSAO dataEmissao,\n"
                    + "	LOG_DATA_INSERIU dataEntrada,\n"
                    + "	VALOR\n"
                    + "FROM \n"
                    + "	FNC_CONTAS_PAGAR fcp \n"
                    + "JOIN FOR_CLI fc ON fcp.ID_FORNECEDOR = fc.ID \n"
                    + "WHERE ID_EMPRESA = " + getLojaOrigem() + "\n"
                    + "AND fcp.SITUACAO = 0\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setCnpj("CNPJ");
                    imp.setNumeroDocumento("numeroDocumento");
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setDataEntrada(rs.getTimestamp("dataEntrada"));
                    imp.setValor(rs.getDouble("valor"));

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
                    + "	fcr.id id,\n"
                    + "	ID_CLIENTE idCliente,\n"
                    + "	c.CGC CPFCnpj,\n"
                    + "	fcr.DOCUMENTO numeroDocumento,\n"
                    + "	EMISSAO dataEmissao,\n"
                    + "	LOG_DATA_INSERIU dataEntrada,\n"
                    + "	fcr.VENCIMENTO vencimento,\n"
                    + "	CASE \n"
                    + "	WHEN fcr.SITUACAO = 0 \n"
                    + "	THEN VALOR \n"
                    + "	ELSE VALOR - VALOR_PAGO \n"
                    + "	END VALOR,\n"
                    + "	fcr.OBSERVACOES observacao\n"
                    + "FROM \n"
                    + "	FNC_CONTAS_RECEBER fcr \n"
                    + "JOIN CADCLI c ON fcr.ID_CLIENTE = c.CODIGO \n"
                    + "WHERE fcr.ID_EMPRESA = " + getLojaOrigem() + " \n"
                    + "AND fcr.SITUACAO IN (0, 2)\n"
                    + "ORDER BY id"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idCliente"));
                    imp.setCnpjCliente(rs.getString("CPFCnpj"));
                    imp.setNumeroCupom("numeroDocumento");
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setValor(rs.getDouble("valor"));

                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
