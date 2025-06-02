package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class VRSUPER2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VR-SUPER";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.PDV_VENDA // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "r2.id AS id,\n"
                    + "r2.descricao AS descricao,\n"
                    + "r.\"CSTICMS\" AS cst,\n"
                    + "r.\"ICMS\" AS aliq,\n"
                    + "r.reducao AS red\n"
                    + "FROM fiscal.regrafiscalicms r\n"
                    + "JOIN fiscal.regrafiscal r2 ON r2.id = r.\"idRegraFiscal\" "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("red"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	c.id AS cod_mercadologico1,\n"
                    + "	c.descricao AS descricao_mercadologico,\n"
                    + "	c2.id AS cod_mercadologico2,\n"
                    + "	c2.descricao AS descricao_mercadologico2,\n"
                    + "	c3.id AS cod_mercadologico3,\n"
                    + "	c3.descricao AS descricao_mercadologico3\n"
                    + "FROM produto.classificacaoproduto c\n"
                    + "LEFT JOIN produto.classificacaoproduto c2 ON c2.\"idPai\" = c.id AND c2.nivel = 2\n"
                    + "LEFT JOIN produto.classificacaoproduto c3 ON c3.\"idPai\" = c2.id AND c3.nivel = 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("cod_mercadologico1"));
                    imp.setMerc1Descricao(rst.getString("descricao_mercadologico"));
                    imp.setMerc2ID(rst.getString("cod_mercadologico2"));
                    imp.setMerc2Descricao(rst.getString("descricao_mercadologico2"));
                    imp.setMerc3ID(rst.getString("cod_mercadologico3"));
                    imp.setMerc3Descricao(rst.getString("descricao_mercadologico3"));
//                    imp.setMerc4ID(rst.getString(""));
//                    imp.setMerc4Descricao(rst.getString(""));
//                    imp.setMerc5ID(rst.getString(""));
//                    imp.setMerc5Descricao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	f.id,\n"
                    + "	f.descricao\n"
                    + "FROM\n"
                    + "	produto.familiaproduto f "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.id AS id,\n"
                    + "	p.\"codigoBarras\" AS ean,\n"
                    + "	p.\"qtdEmbalagem\" AS qtdEmbalagem,\n"
                    + "	p.embalagem AS embalagem \n"
                    + "FROM\n"
                    + "	produto.produtocodigobarras p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "p.id AS id,\n"
                    + "p4.\"codigoBarras\" AS ean,\n"
                    + "p.\"descricaoCompleta\" AS descricaoCompleta,\n"
                    + "p.\"descricaoCupom\" AS descricaoCupom,\n"
                    + "p.\"descricaoGondola\" AS descricaoGondola,\n"
                    + "p.embalagem  AS embalagem,\n"
                    + "p.\"qtdEmbalagem\" AS qtdEmbalagem,\n"
                    + "p.pesavel AS pesavel,\n"
                    + "p2.\"custoComImposto\" AS custoComImposto,\n"
                    + "p2.\"custoSemImposto\" AS custoSemImposto,\n"
                    + "p2.\"precoComum\" AS precoVenda,\n"
                    + "p2.\"margemCustoComum\",\n"
                    + "c1.id AS cod_mercadologico1,\n"
                    + "c1.nivel AS nivel_mercadologico1,\n"
                    + "c2.id AS cod_mercadologico2,\n"
                    + "c2.nivel AS nivel2,\n"
                    + "c3.id AS cod_mercadologico3,\n"
                    + "c3.nivel AS nivel3,\n"
                    + "fp.\"idFamiliaProduto\" AS id_familia_produto,\n"
                    + "CASE WHEN p.ativo THEN 1 ELSE 0 END AS ativo,\n"
                    + "p.\"dataCadastro\" AS dataCadastro,\n"
                    + "CASE WHEN p3.\"dataAlteracao\" IS NOT NULL THEN p3.\"dataAlteracao\" ELSE p.\"dataCadastro\" END AS dataAlteracao,\n"
                    + "e.saldo AS estoque,\n"
                    + "p8.\"estoqueSeguranca\" AS estoque_minimo,\n"
                    + "p8.\"estoqueMaximo\" AS estoque_maximo,\n"
                    + "p5.\"pesoBruto\" AS peso_bruto,\n"
                    + "p5.\"pesoLiquido\" AS peso_liquido,\n"
                    + "REPLACE(n.codigo, '.', '') AS ncm,\n"
                    + "c.codigo AS cest,\n"
                    + "rf.id AS icms,\n"
                    + "pisd.cst AS id_pis_cofins_debito,\n"
                    + "pisc.cst AS id_pis_cofins_credito,\n"
                    + "nr.codigo AS id_natureza_receita\n"
                    + "FROM produto.produto p \n"
                    + "LEFT JOIN produto.produtocomplemento p2 ON p2.\"produtoId\" = p.id AND p2.\"idLoja\" = " + getLojaOrigem() + " --COLOCAR O ID DA LOJA AQUI, NO CASO TEM QUE CHEGAR 11\n"
                    + "LEFT JOIN produto.classificacaoproduto c3 ON c3.id = p.\"idClassificacaoProduto\" \n"
                    + "LEFT JOIN produto.classificacaoproduto c2 ON c2.id = c3.\"idPai\" AND c2.nivel = 2\n"
                    + "LEFT JOIN produto.classificacaoproduto c1 ON c1.id = c2.\"idPai\" AND c1.nivel = 3\n"
                    + "LEFT JOIN produto.familiaprodutoitem fp ON fp.\"idProduto\" = p.id\n"
                    + "LEFT JOIN estoque.estoqueonline e ON e.id = p.id AND e.\"idLoja\" = 11 --COLOCAR O ID DA LOJA AQUI\n"
                    + "LEFT JOIN produto.produtocompra p7 ON p7.\"idProduto\" = p.id\n"
                    + "LEFT JOIN produto.produtocompraloja p8 ON p8.\"idProdutoCompra\" = p7.id AND p8.\"idLoja\" = " + getLojaOrigem() + " --COLOCAR O ID DA LOJA AQUI, NO CASO TEM QUE CHEGAR 11\n"
                    + "LEFT JOIN produto.produtocodigobarras p4 ON p4.\"idProduto\" = p.id\n"
                    + "LEFT JOIN produto.pesosmedidas p5 ON p5.\"idCodigoBarras\" = p4.id\n"
                    + "LEFT JOIN fiscal.ncm n ON n.id = p.\"idNCM\" \n"
                    + "LEFT JOIN fiscal.cest c ON c.id = p.\"idCEST\" \n"
                    + "LEFT JOIN produto.produtopiscofins p6 ON p6.\"idProduto\" = p.id \n"
                    + "LEFT JOIN fiscal.piscofins pisc ON p6.\"idPisCofinsCredito\" = pisc.id \n"
                    + "LEFT JOIN fiscal.piscofins pisd ON p6.\"idPisCofinsDebito\" = pisd.id \n"
                    + "LEFT JOIN fiscal.naturezareceita nr ON p6.\"idNaturezaReceita\" = nr.id \n"
                    + "JOIN fiscal.produtofiscal pf ON pf.\"idProduto\" = p.id \n"
                    + "JOIN fiscal.produtofiscalregrafiscal prf ON prf.\"idProdutoFiscal\" = pf.id\n"
                    + "JOIN fiscal.regrafiscal rf ON rf.id = prf.\"idRegraFiscal\" AND rf.\"tipoOperacao\" = 1\n"
                    + "LEFT JOIN (\n"
                    + "    SELECT DISTINCT ON (p3.\"produtoComplementoId\") p3.*\n"
                    + "    FROM produto.precohistorico p3\n"
                    + "    ORDER BY p3.\"produtoComplementoId\", p3.\"dataAlteracao\" DESC\n"
                    + ") p3 \n"
                    + "    ON p3.\"produtoComplementoId\" = p2.id\n"
                    + "WHERE p4.\"codigoBarras\" IS NOT NULL"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoCupom"));
                    imp.setDescricaoGondola(rst.getString("descricaoGondola"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.seteBalanca(rst.getBoolean("pesavel"));

                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setPrecovenda(rst.getDouble("precoVenda"));
                    imp.setMargem(rst.getDouble("margemCustoComum"));

                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));
                    imp.setCodMercadologico4(rst.getString("cod_mercadologico4"));
//                    imp.setCodMercadologico5(rst.getString(""));
                    imp.setIdFamiliaProduto(rst.getString("id_familia_produto"));

                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setDataAlteracao(rst.getDate("dataAlteracao"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito = rst.getString("icms");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getString("id_pis_cofins_debito"));
                    imp.setPiscofinsCstCredito(rst.getString("id_pis_cofins_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("id_natureza_receita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "p2.\"idProduto\" AS id_produto,\n"
                    + "p.datainicio AS data_inicio,\n"
                    + "p.datatermino AS data_termino,\n"
                    + "p2.precode AS preco_normal,\n"
                    + "p2.precopor AS preco_oferta\n"
                    + "FROM pdv.promocao p \n"
                    + "JOIN pdv.promocaodepor p2 ON p2.id_promocao = p.id \n"
                    + "WHERE p.\"idLoja\" = " + getLojaOrigem() + " --COLOCAR O ID DA LOJA AQUI, NO CASO TEM QUE CHEGAR 11"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString(""));
                    imp.setDataInicio(rst.getDate(""));
                    imp.setDataFim(rst.getDate(""));
                    imp.setPrecoNormal(rst.getDouble(""));
                    imp.setPrecoOferta(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setImpIdProduto(rst.getString(""));
                    imp.setQtdEmbalagem(rst.getInt(""));
                    imp.setImpIdAssociadoItem(rst.getString(""));
                    imp.setQtdEmbalagemItem(rst.getInt(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());

                    imp.setImportid(rst.getString(""));
                    imp.setIdproduto(rst.getString(""));
                    imp.setDescricao(rst.getString(""));
                    imp.setRendimento(rst.getDouble(""));
                    imp.setQtdembalagemreceita(rst.getInt(""));
                    imp.setQtdembalagemproduto(1);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "p.\"idPessoa\" AS id,\n"
                    + "CASE WHEN p3.\"razaoSocial\"IS NOT NULL THEN p3.\"razaoSocial\" ELSE p4.nome END AS razaoSocial,\n"
                    + "p3.\"nomeFantasia\" AS fantasia,\n"
                    + "CASE WHEN p3.cnpj IS NOT NULL THEN p3.cnpj ELSE p4.cpf END AS cnpj,\n"
                    + "CASE WHEN p3.ie IS NOT NULL THEN p3.ie ELSE p4.rg END AS ie,\n"
                    + "e.endereco AS endereco,\n"
                    + "e.num AS numero,\n"
                    + "e.bairro AS bairro,\n"
                    + "e.municipio AS municipio,\n"
                    + "e.uf AS uf,\n"
                    + "e.cep AS cep,\n"
                    + "e.complemento AS complemento,\n"
                    + "p2.\"dataCadastro\" AS data_cadasrto,\n"
                    + "CASE WHEN c.celular IS NOT NULL THEN c.celular ELSE c.telefone END AS contato,\n"
                    + "p2.ativo AS ativo\n"
                    + "FROM pessoa.pessoafornecedor p \n"
                    + "JOIN pessoa.pessoa p2 ON p2.id = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.pessoajuridica p3 ON p3.\"idPessoa\" = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.pessoafisica p4 ON p4.\"idPessoa\" = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.endereco e ON e.\"idPessoa\"  = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.contato c ON c.\"idPessoa\" = p.\"idPessoa\""
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razaoSocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("complemento"));

                    imp.setAtivo(rst.getBoolean("ativo"));
//                    imp.setObservacao(rst.getString(""));
                    imp.setDatacadastro(rst.getDate("data_cadasrto"));
                    imp.setTel_principal(rst.getString("contato"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "t.\"idPessoaFornecedor\" AS id_fornecedor,\n"
                    + "t2.\"idProduto\" AS id_produto,\n"
                    + "t3.\"codigoExterno\" AS codigo_externo,\n"
                    + "t4.\"quantidadeCompra\" AS qtd_embalagem\n"
                    + "FROM pessoa.tabelafornecedor t \n"
                    + "LEFT JOIN pessoa.tabelafornecedoritem t2 ON t2.\"idTabelaFornecedor\" = t.id\n"
                    + "JOIN pessoa.tabelafornecedorcodigoexterno t3 ON t3.\"idTabelaFornecedorItem\" = t2.id \n"
                    + "JOIN pessoa.tabelafornecedorconversao t4 ON t4.\"idTabelaFornecedorItem\" = t2.id "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigo_externo"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd_embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "c.id AS id,\n"
                    + "p.id AS id_pessoa,\n"
                    + "c.titulo AS numero_documento,\n"
                    + "c.\"dataEmissao\" AS data_emissao,\n"
                    + "c.\"dataVencimento\" AS data_vencimento,\n"
                    + "c.\"valorBruto\" AS valor_bruto,\n"
                    + "c.observacao AS observacao \n"
                    + "FROM financeiro.contapagar c \n"
                    + "JOIN pessoa.pessoa p ON p.id = c.\"idPessoa\" \n"
                    + "WHERE c.\"idLoja\" = " + getLojaOrigem() + " --COLOCAR O ID DA LOJA AQUI, NO CASO TEM QUE CHEGAR 11\n"
                    + "AND c.\"dataBaixa\" IS null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_pessoa"));
                    imp.setNumeroDocumento(rst.getString("numero_documento"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
//                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate("data_vencimento"), rst.getDouble("valor_bruto"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdFornecedor(rst.getString(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataVencimento(rst.getDate(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "p2.id AS id_cliente,\n"
                    + "CASE WHEN p3.nome IS NOT NULL THEN p3.nome ELSE p4.\"razaoSocial\" END AS razao,\n"
                    + "p4.\"nomeFantasia\" AS fantasia,\n"
                    + "CASE WHEN p3.cpf IS NOT NULL THEN p3.cpf ELSE p4.cnpj END AS cnpj,\n"
                    + "CASE WHEN p3.rg IS NOT NULL THEN p3.rg ELSE p4.ie END AS rg,\n"
                    + "e.endereco AS endereco,\n"
                    + "e.endereco AS endereco,\n"
                    + "e.num AS numero,\n"
                    + "e.complemento AS complemento,\n"
                    + "e.bairro AS bairro,\n"
                    + "e.municipio AS municipio,\n"
                    + "e.uf AS uf,\n"
                    + "e.cep AS cep,\n"
                    + "p2.\"dataCadastro\" AS data_cadastro,\n"
                    + "p2.ativo,\n"
                    + "CASE WHEN c.celular IS NOT NULL THEN c.celular ELSE c.telefone END AS contato\n"
                    + "FROM pessoa.pessoacliente p \n"
                    + "LEFT JOIN pessoa.pessoa p2 ON p2.id = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.pessoafisica p3 ON p3.\"idPessoa\" = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.pessoajuridica p4 ON p4.\"idPessoa\" = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.endereco e ON e.\"idPessoa\"  = p.\"idPessoa\" \n"
                    + "LEFT JOIN pessoa.contato c ON c.\"idPessoa\" = p.\"idPessoa\" "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id_cliente"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setTelefone(rst.getString("contato"));
//                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<CreditoRotativoIMP> getCreditoRotato() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "c.id AS id,\n"
                    + "c.\"numeroCupom\" AS numero_cupom,\n"
                    + "p.id AS id_pessoa,\n"
                    + "p2.cpf AS cpf,\n"
                    + "c.pdv AS ecf,\n"
                    + "c.\"valorBruto\" AS valor,\n"
                    + "c.\"dataEmissao\" AS data_emissao,\n"
                    + "c.\"dataVencimento\" AS data_vencimento,\n"
                    + "c.observacao AS observacao \n"
                    + "FROM financeiro.contareceber c \n"
                    + "JOIN pessoa.pessoa p ON p.id = c.\"idPessoa\" \n"
                    + "JOIN pessoa.pessoafisica p2 ON p2.\"idPessoa\" = p.id \n"
                    + "WHERE c.\"idLoja\" = " + getLojaOrigem() + "\n --COLOCAR O ID DA LOJA AQUI, NO CASO TEM QUE CHEGAR 11"
                    + "AND \"idFormaPagamento\" = 5"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("numero_cupom")));
                    imp.setIdCliente(rst.getString("id_pessoa"));
                    imp.setCnpjCliente(rst.getString("cpf"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
                    imp.setDataVencimento(rst.getDate("data_vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString(""));
                    imp.setDataDeposito(rst.getDate(""));
                    imp.setNumeroCheque(rst.getString(""));
                    imp.setDate(rst.getDate(""));
                    imp.setBanco(rst.getInt(""));
                    imp.setAgencia(rst.getString(""));
                    imp.setConta(rst.getString(""));
                    imp.setNome(rst.getString(""));
                    imp.setTelefone(rst.getString(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setNumeroCupom(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString(""));
                    imp.setCnpj(rst.getString(""));
                    imp.setInscricaoEstadual(rst.getString(""));
                    imp.setRazao(rst.getString(""));
                    imp.setEndereco(rst.getString(""));
                    imp.setNumero(rst.getString(""));
                    imp.setBairro(rst.getString(""));
                    imp.setMunicipio(rst.getString(""));
                    imp.setUf(rst.getString(""));
                    imp.setCep(rst.getString(""));
                    imp.setTelefone(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString(""));
                    imp.setNome(rst.getString(""));
                    imp.setIdEmpresa(rst.getString(""));
                    imp.setCnpj(rst.getString(""));
                    imp.setConvenioLimite(rst.getDouble(""));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rst.getInt("") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdConveniado(rst.getString(""));
                    imp.setNumeroCupom(rst.getString(""));
                    imp.setDataHora(rst.getTimestamp(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setObservacao(rst.getString(""));

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
        return new VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
    
        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static class VendaIterator implements Iterator<VendaIMP> {


        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numero_cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestamp.format(rst.getDate("data_inicio"));
                        String horaTermino = timestamp.format(rst.getDate("data_termino"));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotal"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            this.sql
                    = "SELECT\n"
                    + "v.id AS id_venda,\n"
                    + "v.\"numeroCupom\" AS numero_cupom,\n"
                    + "v.\"numeroPDV\" AS ecf,\n"
                    + "v.\"dataHoraEmissaoCupom\" AS data,\n"
                    + "v.\"dataHoraInicio\" AS data_inicio,\n"
                    + "v.\"dataHoraTermino\" AS data_termino,\n"
                    + "v.subtotal AS subtotal,\n"
                    + "v.cancelado AS cancelado \n"
                    + "FROM venda.venda v \n"
                    + "WHERE \"idLoja\" = 11\n"
                    + "AND v.\"dataHoraEmissaoCupom\" BETWEEN '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "';";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_venda_item"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("embalagem"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("preco_venda"));
                        next.setTotalBruto(rst.getDouble("total_bruto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT \n"
                    + "vi.\"idVenda\" AS id_venda,\n"
                    + "vi.id AS id_venda_item,\n"
                    + "vi.sequencia AS sequencia,\n"
                    + "vi.\"idProduto\" AS id_produto,\n"
                    + "vi.embalagem AS embalagem,\n"
                    + "vi.codigobarras AS ean,\n"
                    + "vi.\"descricaoProduto\" AS descricao,\n"
                    + "vi.quantidade AS quantidade,\n"
                    + "vi.\"precoVenda\" AS preco_venda,\n"
                    + "vi.\"valorSubTotal\" AS preco_venda,\n"
                    + "vi.\"valorTotal\" AS total_bruto,\n"
                    + "vi.cancelado AS cancelado\n"
                    + "FROM venda.vendaitem vi \n"
                    + "JOIN venda.venda v ON v.id = vi.\"idVenda\" \n"
                    + "WHERE v.\"idLoja\" = 11\n"
                    + "AND v.\"dataHoraEmissaoCupom\" BETWEEN '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "';";
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
