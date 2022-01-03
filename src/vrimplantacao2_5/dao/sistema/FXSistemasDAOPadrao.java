/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
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
import vrimplantacao2_5.vo.sistema.FXSistemasVO;

/**
 *
 * @author Michael
 */
public class FXSistemasDAOPadrao extends InterfaceDAO implements MapaTributoProvider {

    public FXSistemasVO fxSistemasVO = null;
    private final String SISTEMA = "Fx Sistemas";
    private String complementoSistema = "";

    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + SISTEMA;
    }

    public String getComplementoSistema() {
        return this.complementoSistema;
    }

    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	e.ID id,\n"
                    + "	e.RAZAO_SOCIAL empresa,\n"
                    + "	e.NOME_FANTASIA\n"
                    + "FROM\n"
                    + "	EMPRESA e;"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("aliquota") + "-" + rst.getString("reducao");
                    result.add(new MapaTributoIMP(
                            rst.getString(id),
                            rst.getString(id),
                            rst.getInt("cst"),
                            Double.parseDouble(rst.getString("aliquota").replace(",", ".")),
                            rst.getDouble("reducao")));
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
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));

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
                    "SELECT FIRST 100\n"
                    + "	p.ID Id,\n"
                    + "	p.DATA_CADASTRO dataCadastro,\n"
                    + "	p.DATA_ALTERACAO dataAlteracao,\n"
                    + "	p.COD_BARRAS ean,\n"
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
                    + "    p.CST_SAIDA_EXT icmsCstSaidaForaEstado\n"
                    + "FROM PRODUTO p \n"
                    + "LEFT JOIN FNC_EMBALAGENS e ON p.ID_EMBALAGENS = e.ID \n"
                    + "LEFT JOIN GRUPO_PRODUTOS gp ON p.GRUPO_PRODUTOS = gp.ID \n"
                    + "LEFT JOIN PRODUTO_ESTOQUE pe ON p.ID = pe.ID_PRODUTO \n"
                    + "WHERE pe.ID_EMPRESA = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    if (fxSistemasVO.isProdutosBalancaIniciaCom20()) {
                        if (rst.getBoolean("e_balanca")) {
                            if (rst.getString("ean") != null && !rst.getString("ean").trim().isEmpty()) {

                                if (rst.getString("ean").trim().length() <= 13 && rst.getString("ean").startsWith("20")) {
                                    imp.setEan(rst.getString("ean").trim().substring(1, 12));
                                } else {
                                    imp.setEan(rst.getString("ean"));
                                }
                            }
                        }
                    } else {
                        imp.setEan(rst.getString("ean"));
                    }

                    if (fxSistemasVO.isProdutosBalancaIniciaCom789()) {
                        if (rst.getBoolean("e_balanca")) {
                            if (rst.getString("ean") != null && !rst.getString("ean").trim().isEmpty()) {

                                if (rst.getString("ean").trim().length() <= 13 && rst.getString("ean").startsWith("789")) {
                                    imp.setEan(rst.getString("ean").trim().substring(3, 12));
                                } else {
                                    imp.setEan(rst.getString("ean"));
                                }
                            }
                        }
                    } else {
                        imp.setEan(rst.getString("ean"));
                    }
                    if (fxSistemasVO.isTemArquivoBalanca()) {
                        if (rst.getString("ean") != null && !rst.getString("ean").trim().isEmpty()) {
                            if (rst.getString("ean").trim().length() <= 18 && rst.getString("ean").startsWith("01")) {

                                imp.setEan(rst.getString("ean").trim().substring(3, 9));
                            } else {
                                imp.setEan(rst.getString("ean"));
                            }
                        }

                        if (imp.getEan() != null && !imp.getEan().trim().isEmpty()) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getEan());

                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }
                    } else {
                        if ("Balan".equals(rst.getString("tipo")) || "Peso".equals(rst.getString("tipo"))) {
                            imp.seteBalanca(true);
                        } else {
                            imp.seteBalanca(false);
                        }
                    }

                    imp.setImportId(rst.getString("Id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setDataAlteracao(rst.getDate("dataAlteracao"));

                    imp.setCodMercadologico1("codMercadologico1");
                    imp.setCodMercadologico2("codMercadologico2");
                    imp.setCodMercadologico3("codMercadologico3");
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoqueMaximo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoqueMinimo"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsAliqEntrada(rst.getDouble("icmsAliqEntrada"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icmsReducaoEntrada"));
                    imp.setIcmsCstSaida(rst.getInt("icmsCstSaida"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icmsReducaoSaida"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("icmsCstSaidaForaEstado"));

                    imp.setPiscofinsCstDebito(rst.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinsCstCredito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	id, \n"
                    + "	COD_BARRAS AS ean,\n"
                    + "	1 AS qtdembalagem\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "WHERE COD_BARRAS <> ''"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

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
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal("insc_municipal");
                    imp.setSuframa("suframa");
                    imp.setAtivo(rst.getInt("ativo") == 1);

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setValor_minimo_pedido(rst.getFloat("valor_minimo_pedido"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    imp.setTel_principal(rst.getString("fone1"));

                    String fax = (rst.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rst.getString("email"));
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
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "     ea.COD_FORNECEDOR AS idfornecedor,\n"
                    + "     f.NOME AS descricaofornecedor,\n"
                    + "     ea.CODIGO AS idproduto,\n"
                    + "     p.NOME AS descricaoproduto,\n"
                    + "     ea.COD_FABRICANTE AS codigoexterno,\n"
                    + "     ea.DATA_CADASTRO AS dataalteracao\n"
                    + "FROM EST_ADICIONAIS ea\n"
                    + "JOIN ESTOQUE p ON p.CODIGO = ea.CODIGO \n"
                    + "JOIN FORNECEDORES f ON f.CODIGO = ea.COD_FORNECEDOR \n"
                    + "	AND ea.COD_FORNECEDOR != ''\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {

                    String[] codigosExternos = rst.getString("codigoexterno").split("\\|");

                    for (int i = 0; i < codigosExternos.length; i++) {

                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setIdProduto(rst.getString("idproduto"));
                        imp.setIdFornecedor(rst.getString("idfornecedor"));
                        imp.setCodigoExterno(codigosExternos[i].trim());
                        imp.setDataAlteracao(rst.getDate("dataalteracao"));

                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setDataNascimento(rst.getDate("dataNascimento"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone("telefone");
                    imp.setOrgaoemissor("orgaoemissor");

                    imp.setEstadoCivil("estadoCivil");
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setNomeConjuge(rst.getString("nomeConjuge"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setEmpresa("empresa");
                    imp.setEmpresaEndereco("empresaEndereco");
                    imp.setEmpresaComplemento("empresaComplemento");
                    imp.setEmpresaBairro("empresaBairro");
                    imp.setEmpresaMunicipio("empresaMunicipio");
                    imp.setEmpresaUf("empresaUf");
                    imp.setEmpresaCep("empresaCep");
                    imp.setEmpresaTelefone("empresaTelefone");
                    imp.setDataAdmissao(rst.getDate("dataAdmissao"));
                    imp.setCargo("cargo");
                    imp.setEmpresaTelefone(rst.getString("empresaTelefone"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setDiaVencimento(rst.getInt("diaVencimento"));

                    imp.setEmail(rst.getString("email"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));

                    result.add(imp);
                }
            }
        }

        return result;
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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                    + "	fcr.OBSERVACOES \n"
                    + "FROM \n"
                    + "	FNC_CONTAS_RECEBER fcr \n"
                    + "JOIN CADCLI c ON fcr.ID_CLIENTE = c.CODIGO \n"
                    + "WHERE fcr.ID_EMPRESA = " + getLojaOrigem() + " \n"
                    + "AND fcr.SITUACAO IN (0, 2)\n"
                    + "ORDER BY id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setCnpjCliente(rst.getString("CPFCnpj"));
                    imp.setNumeroCupom("numeroDocumento");
                    imp.setDataEmissao(rst.getDate("dataEmissao"));
                    imp.setValor(rst.getDouble("valor"));

                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
