package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
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
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Bruno
 *
 */
public class AlphaSys2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    // SISTEMA REFATORADO DA 2.0 E NÃO VALIDADO, FAVOR REVER TODOS OS CAMPOS INCLUSIVE ESCRIPTLOJAORIGEM -- SELECT LOJA.
    @Override
    public String getSistema() {
        return "AlphaSys";
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
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.PDV_VENDA,
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
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.DESMEMBRAMENTO
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OBSERVACAO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.RECEBER_CHEQUE,
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
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	SITUACAO_TRIBUTARIA AS id,\n"
                    + "	CASE\n"
                    + "		WHEN pt.situacao_tributaria = 102 THEN 'tributado'\n"
                    + "		ELSE 'substituido'\n"
                    + "	END descricao ,\n"
                    + "	SITUACAO_TRIBUTARIA AS cst_saida,\n"
                    + "	MODALIDADE_BC_ICMS AS aliquota_saida,\n"
                    + "	MODALIDADE_BC_ICMS_ST AS aliquota_saida_st,\n"
                    + "	REDUCAO_BC_ICMS AS reducao_saida\n"
                    + "FROM\n"
                    + "	PRODUTO_TRIBUTACAO pt"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "DISTINCT \n"
                    + "	CAST(G.COD_GRUPO AS VARCHAR(60)) GRUPOPRODUTOCODIGO,\n"
                    + "	G.NOME GRUPOPRODUTONOME,\n"
                    + "	CAST(S.COD_GRUPO AS VARCHAR(60)) SUBGRUPOPRODUTOCODIGO,\n"
                    + "	S.NOME SUBGRUPOPRODUTONOME\n"
                    + "FROM\n"
                    + "	PRODUTO P\n"
                    + "JOIN GRUPO S ON S.COD_GRUPO = P.COD_GRUPO\n"
                    + "LEFT JOIN GRUPO G ON G.COD_GRUPO = S.COD_JUNCAO\n"
                    + "LEFT JOIN GRUPO SE ON SE.COD_GRUPO = G.COD_JUNCAO;"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("GRUPOPRODUTOCODIGO"));
                    imp.setMerc1Descricao(rst.getString("GRUPOPRODUTONOME"));
                    imp.setMerc2ID(rst.getString("SUBGRUPOPRODUTOCODIGO"));
                    imp.setMerc2Descricao(rst.getString("SUBGRUPOPRODUTONOME"));
                    imp.setMerc3ID(rst.getString("SUBGRUPOPRODUTOCODIGO"));
                    imp.setMerc3Descricao(rst.getString("SUBGRUPOPRODUTONOME"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "p.COD_PRODUTO AS PRODCod,\n"
                    + "p.COD_BARRAS AS ean,\n"
                    + "PRODUTO_BALANCA AS prodbalanca,\n"
                    + "p.nome AS PRODNome,\n"
                    + "DT_CADASTRO AS PRODCadast,\n"
                    + "pc.PRECO_COMPRA AS  custocomimposto,\n"
                    + "pc.PRECO_VENDA  AS PRODVenda,\n"
                    + "pa.ENTRADA AS prodsdo,\n"
                    + "G.COD_GRUPO AS merc1,\n"
                    + "S.COD_GRUPO AS merc2,\n"
                    + "S.COD_GRUPO AS merc3,\n"
                    + "gn.NCM AS ncm,\n"
                    + "gc.CEST AS cest ,\n"
                    + "p.COD_UNIDADE_SAIDA AS PRODUnid,\n"
                    + "CASE\n"
                    + "	WHEN p.SITUACAO = 1 THEN 'A'\n"
                    + "END AS prodai,\n"
                    + "pt.ALIQUOTA_PIS AS  prodstcofins,\n"
                    + "pt.SITUACAO_TRIBUTARIA AS  id_icms_saida	\n"
                    + "FROM\n"
                    + "PRODUTO p\n"
                    + "LEFT join PRODUTO_COMPLEMENTO pc ON pc.COD_PRODUTO = p.COD_PRODUTO \n"
                    + "LEFT JOIN PRODUTO_ALMOXARIFADO pa ON pa.COD_PRODUTO = p.COD_PRODUTO \n"
                    + "LEFT JOIN GRUPO_NCM gn ON gn.COD_GRUPO_NCM = p.COD_GRUPO_NCM \n"
                    + "LEFT JOIN GRUPO_CEST gc ON gc.COD_GRUPO_CEST = p.COD_GRUPO_CEST \n"
                    + "LEFT JOIN PRODUTO_TRIBUTACAO pt ON pt.COD_PRODUTO = p.COD_PRODUTO \n"
                    + "LEFT JOIN GRUPO S ON S.COD_GRUPO = P.COD_GRUPO\n"
                    + "LEFT JOIN GRUPO G ON G.COD_GRUPO = S.COD_JUNCAO\n"
                    + "LEFT JOIN GRUPO SE ON SE.COD_GRUPO = G.COD_JUNCAO"
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportId(rst.getString("PRODCod"));
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setEan(rst.getString("ean"));

                    //long codigoProduto;
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean")));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(bal.getValidade());
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(1);
                        imp.seteBalanca("TRUE".equals(rst.getString("prodbalanca")));
                        imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    }

                    imp.setDescricaoCompleta(Utils.acertarTexto(rst.getString("PRODNome")));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("PRODCadast"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("PRODVenda"));
                    imp.setEstoque(rst.getDouble("prodsdo"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    imp.setTipoEmbalagemCotacao(rst.getString("PRODUnid"));

                    if ((rst.getString("prodai") != null)
                            && (!rst.getString("prodai").trim().isEmpty())) {
                        imp.setSituacaoCadastro(rst.getString("prodai").contains("A") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }

                    String idIcmsDebito = rst.getString("id_icms_saida");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofins"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofins"))));
                    vResult.add(imp);
                    contador++;
                    ProgressBar.setStatus("Carregando dados..." + contador);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_PRODUTO AS PRODCod,\n"
                    + "	COD_BARRAS AS BARCod,\n"
                    + "	CONVERSAO AS barunbxa,\n"
                    + "	COD_UNIDADE_ENTRADA AS PRODUnid\n"
                    + "FROM\n"
                    + "	PRODUTO p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("PRODCod"));
                    imp.setEan(rst.getString("BARCod"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_FORNECEDOR AS forcod,\n"
                    + "	COD_PRODUTO AS prodcod,\n"
                    + "	CODIGO AS codfrabicante\n"
                    + "FROM\n"
                    + "	PRODUTO_FORNECEDOR pf"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("forcod"));
                    imp.setIdProduto(rst.getString("prodcod"));
                    imp.setCodigoExterno(rst.getString("codfabricante") == null ? "0" : rst.getString("codfabricante"));
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT\n"
                    + "	f.COD_FORNECEDOR ,\n"
                    + "	c.COD_COLABORADOR AS idFornecedor ,\n"
                    + "	c.NOME AS razao,\n"
                    + "	c.FANTASIA AS fantasia,\n"
                    + "	l.NOME AS endereco ,\n"
                    + "	l.NOME AS bairro ,\n"
                    + "	c.CEP AS cep,\n"
                    + "	c.NUMERO ,\n"
                    + "	c.COMPLEMENTO ,\n"
                    + "	c.COD_ESTADO ,\n"
                    + "	c.COD_MUNICIPIO ,\n"
                    + "	c.CGC AS cnpj,\n"
                    + "	c.IES AS ie ,\n"
                    + "	f.SITUACAO AS ativo,\n"
                    + "	c.fone AS telefone,\n"
                    + "	c.DT_CADASTRO AS datacadastro\n"
                    + "FROM\n"
                    + "FORNECEDOR f\n"
                    + "JOIN COLABORADOR c ON f.COD_FORNECEDOR = COD_COLABORADOR\n"
                    + "JOIN LOGRADOURO l ON  c.COD_LOGRADOURO = l.COD_LOGRADOURO\n"
                    + "JOIN BAIRRO b ON c.COD_BAIRRO = b.COD_BAIRRO"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idFornecedor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("cod_estado"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(true);
                    imp.setTel_principal(rst.getString("telefone"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "  c.COD_CLIENTE AS CLICod,\n"
                    + "  c2.NOME AS CLINome,\n"
                    + "  c2.FANTASIA AS CLIFantasia  ,\n"
                    + "  b.NOME AS CLIBairro,\n"
                    + "  c2.CEP AS CLICep,\n"
                    + "  m.NOME  AS CIDNome,\n"
                    + "  c2.COD_ESTADO AS ciduf,\n"
                    + "  c2.NUMERO AS clinumero,\n"
                    + "  c2.COMPLEMENTO AS clicomplemento,\n"
                    + "  c.LIMITE_CREDITO AS CLILIMCred,\n"
                    + "  c2.DT_CADASTRO AS CLICadastro,\n"
                    + "  c2.DT_NASCIMENTO_FUNDACAO AS CLINasc,\n"
                    + "  c2.FONE AS CLIFone1,\n"
                    + "  c2.CGC AS clicpf\n"
                    + "  FROM CLIENTE c \n"
                    + "  JOIN COLABORADOR c2 ON c2.COD_COLABORADOR = c.COD_CLIENTE \n"
                    + "  JOIN BAIRRO b ON b.COD_BAIRRO = c2.COD_BAIRRO \n"
                    + "  JOIN MUNICIPIO m ON m.COD_MUNICIPIO = c2.COD_MUNICIPIO "
            )) {
                int contador = 1;
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CLICod"));
                    imp.setRazao(rst.getString("CLINome"));
                    imp.setFantasia(rst.getString("CLIFantasia"));
                    imp.setBairro(rst.getString("CLIBairro"));
                    imp.setCep(rst.getString("CLICep"));
                    imp.setMunicipio(rst.getString("CIDNome"));
                    imp.setUf(rst.getString("ciduf"));
                    imp.setNumero(rst.getString("clinumero"));
                    imp.setComplemento(rst.getString("clicomplemento"));
                    imp.setValorLimite(rst.getDouble("CLILIMCred") > 1000000.00 ? 10000.00 : rst.getDouble("CLILIMCred"));
                    imp.setDataCadastro(rst.getDate("CLICadastro"));
                    imp.setDataNascimento(rst.getDate("CLINasc"));
                    imp.setTelefone(rst.getString("CLIFone1"));
                    imp.setCnpj(rst.getString("clicpf"));

                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
                return vResult;
            }
        }
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_CONTAS_PAGAR AS id,\n"
                    + "	COD_COLABORADOR AS idfornecedor,\n"
                    + "	NUMERO AS numero_doc,\n"
                    + "	DT_EMISSAO AS dataemissao,\n"
                    + "	DT_VENCIMENTO  AS vencimento,\n"
                    + "	VL_SUBTOTAL AS total,\n"
                    + "	OBSERVACAO AS obs,\n"
                    + "	PARCELAS AS parcela \n"
                    + "FROM\n"
                    + "	CONTAS_PAGAR cp\n"
                    + "	WHERE SITUACAO = 1\n"
                    + "	AND DT_VENCIMENTO IS NOT null"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setNumeroDocumento(rs.getString("numero_doc"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getTimestamp("dataemissao"));
                    imp.setValor(rs.getDouble("total"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.addVencimento(
                            rs.getDate("vencimento"),
                            rs.getDouble("total"),
                            rs.getInt("parcela"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_CONTAS_RECEBER AS CCTCod,\n"
                    + "	COD_COLABORADOR AS CLICod,\n"
                    + "	NUMERO ,\n"
                    + "	DT_VENCIMENTO AS cctvcto,\n"
                    + "	COD_VENDA  ,\n"
                    + "	DT_EMISSAO AS CCTData ,\n"
                    + "	VL_TOTAL AS CCTDebito,\n"
                    + "	OBSERVACAO AS cctobs,\n"
                    + "	COD_CUPOM AS CCTCupom ,\n"
                    + "	COD_CAIXA AS cctecf\n"
                    + "FROM\n"
                    + "	CONTAS_RECEBER cr\n"
                    + "	WHERE COD_EMPRESA_PAGAMENTO IS null AND SITUACAO = 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("CCTCod"));
                    imp.setIdCliente(rst.getString("CLICod"));
                    imp.setNumeroCupom(rst.getString("CCTCupom"));
                    imp.setEcf(rst.getString("cctecf"));
                    imp.setDataEmissao(rst.getDate("CCTData"));
                    imp.setDataVencimento(rst.getDate("cctvcto"));
                    imp.setValor(rst.getDouble("CCTDebito"));
                    imp.setObservacao(rst.getString("cctobs"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<DesmembramentoIMP> getDesmembramentos() throws Exception {
        List<DesmembramentoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	i.codigo id_desmem,\n"
                    + "	d.CODIGO_PRODUTO_FRACIONAVEL prod_pai,\n"
                    + "	i.CODIGO_PRODUTO_FRACIONADO prod_filho,\n"
                    + "	p.DESCRICAO produto,\n"
                    + "	i.QUANTIDADE percentual\n"
                    + "FROM\n"
                    + "	PRODUTO_FRACIONAMENTO d\n"
                    + "	JOIN PRODUTO_FRACIONAMENTO_ITEM i ON d.CODIGO = i.CODIGO_FRACIONAMENTO\n"
                    + "	JOIN produto p ON p.CODIGO = i.CODIGO_PRODUTO_FRACIONADO\n"
                    + "WHERE\n"
                    + "	d.CODIGO_FILIAL = " + getLojaOrigem() + "\n"
                    + " ORDER by 1"
            )) {
                while (rs.next()) {
                    DesmembramentoIMP imp = new DesmembramentoIMP();

                    imp.setId(rs.getString("id_desmem"));
                    imp.setProdutoPai(rs.getString("prod_pai"));
                    imp.setProdutoFilho(rs.getString("prod_filho"));
                    imp.setPercentual(rs.getDouble("percentual"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new AlphaSys2_5DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new AlphaSys2_5DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setSubTotalImpressora(rst.getDouble("valorliquido"));
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
                    = "SELECT \n"
                    + " CODIGO id_venda,\n"
                    + " TIPO_VENDA,\n"
                    + " DATA_VENDA data,\n"
                    + " HORA_VENDA hora,\n"
                    + " VENDA_BRUTA valorbruto,\n"
                    + " VENDA_LIQUIDA valorliquido,\n"
                    + " DESCONTO_VALOR desconto,\n"
                    + " CODIGO numerocupom,\n"
                    + " CODIGO_ECF ecf,\n"
                    + " CASE WHEN ESTORNO = 'S' THEN 1 ELSE 0 END cancelado,\n"
                    + " CODIGO_FILIAL\n"
                    + "FROM VENDA\n"
                    + "WHERE \n"
                    + " DATA_VENDA BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'";

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
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("produtoid"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
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
                    = "SELECT \n"
                    + "vi.CODIGO_ITEM id_item,\n"
                    + "vi.CODIGO_VENDA id_venda,\n"
                    + "vi.CODIGO_PRODUTO produtoid,\n"
                    + "vi.PAF_DESCRICAO_PRODUTO descricao,\n"
                    + "vi.CODIGO_BARRA codigobarras,\n"
                    + "CASE WHEN vi.PAF_UNIDADE_MEDIDA NOT IN ('UN','KG','CX') THEN 'UN'\n"
                    + "     ELSE vi.PAF_UNIDADE_MEDIDA END unidade,\n"
                    + "vi.SEQUENCIA,\n"
                    + "vi.QUANTIDADE,\n"
                    + "vi.PRECO_UNITARIO valor,\n"
                    + "vi.DESCONTO_VALOR desconto,\n"
                    + "CASE WHEN vi.ESTORNO = 'S' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM VENDA_ITEM vi\n"
                    + "JOIN VENDA v ON v.CODIGO = vi.CODIGO_VENDA \n"
                    + "WHERE \n"
                    + "  v.DATA_VENDA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "';";
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
