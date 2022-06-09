package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ChequeIMP;
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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoInscricao;
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
 * @author Wagner
 *
 */
public class GansoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Ganso";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DESMEMBRAMENTO,
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
                //OpcaoProduto.VENDA_PDV,
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
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
                    "SELECT \n"
                    + "CODIGO,\n"
                    + "SIMBOLO_ECF,\n"
                    + "CST,\n"
                    + "DESCRICAO\n"
                    + "FROM PRODUTO_TRIBUTO"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("descricao"),
                            0,
                            rs.getDouble("cst"),
                            0));
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
                    "SELECT \n"
                    + " CODIGO,\n"
                    + " CODIGO_PRODUTO id,\n"
                    + " CODIGO_BARRA ean\n"
                    + "FROM PRODUTO_CODIGOSBARRAS"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(1);

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
                    "SELECT \n"
                    + " p.CODIGO,\n"
                    + " p.DESCRICAO,\n"
                    + " p.DATA_CADASTRO,\n"
                    + " p.DATA_ALTERACAO,\n"
                    + " CASE WHEN p.STATUS = 'A' THEN 1\n"
                    + "      ELSE 0 END status,\n"
                    + " p.COD_SECAO merc1,\n"
                    + " CASE WHEN p.COD_GRUPO IS NULL THEN p.COD_SECAO\n"
                    + " ELSE p.COD_GRUPO END merc2,\n"
                    + " CASE WHEN p.COD_SUBGRUPO IS NULL AND p.COD_GRUPO IS NULL THEN p.COD_SECAO\n"
                    + "      WHEN p.COD_SUBGRUPO IS NULL AND p.COD_GRUPO IS NOT NULL THEN p.COD_GRUPO\n"
                    + "      ELSE p.COD_SUBGRUPO END merc3,\n"
                    + " p.COD_MARCA,\n"
                    + " p.COD_FORNECEDOR,\n"
                    + " p.PRECO_VENDA,\n"
                    + " p.CUSTO_MEDIO,\n"
                    + " p.CODIGO_BARRA ean,\n"
                    + " p.CODIGO_EMPRESA,\n"
                    + " p.CODIGO_FILIAL,\n"
                    + " CASE WHEN e.PAF_UNIDADE_MEDIDA NOT IN ('UN','KG','CX') THEN 'UN'\n"
                    + "      ELSE e.PAF_UNIDADE_MEDIDA END tipoembalagem,\n"
                    + " CASE WHEN p.BALANCA = 'N' THEN 0\n"
                    + "      ELSE 1 END ebalanca,\n"
                    + " e.ESTOQUE,\n"
                    + " e.ESTOQUE_MINIMO,\n"
                    + " p.CST_PIS,\n"
                    + " p.CST_COFINS,\n"
                    + " p.CST_PIS_ENTRADA,\n"
                    + " p.CST_COFINS_ENTRADA,\n"
                    + " p.CODIGO_TRIBUTO,\n"
                    + " p.NCM,\n"
                    + " p.CEST\n"
                    + "FROM PRODUTO p\n"
                    + "JOIN ESTOQUE_PRODUTO e ON e.CODIGO_PRODUTO = p.CODIGO"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("codigo"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setDataCadastro(rs.getDate("data_Cadastro"));
                    imp.setDataAlteracao(rs.getDate("data_Alteracao"));

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_minimo"));

                    imp.setCustoSemImposto(rs.getDouble("custo_medio"));
                    imp.setCustoComImposto(imp.getCustoMedioSemImposto());
                    imp.setPrecovenda(rs.getDouble("preco_venda"));

                    imp.setSituacaoCadastro(rs.getInt("status"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstDebito(rs.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rs.getString("cst_pis_entrada"));

                    imp.setIcmsConsumidorId(rs.getString("CODIGO_TRIBUTO"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());

                    int codigoProduto = Utils.stringToInt(rs.getString("codigo"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(rs.getBoolean("ebalanca"));
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
                    "SELECT \n"
                    + " f.CODIGO,\n"
                    + " f.NOME,\n"
                    + " f.RAZAO_SOCIAL,\n"
                    + " f.ENDERECO,\n"
                    + " f.NUMERO,\n"
                    + " f.COMPLEMENTO,\n"
                    + " f.BAIRRO,\n"
                    + " c.NOME cidade,\n"
                    + " f.UF,\n"
                    + " f.CEP,\n"
                    + " f.DATA_CADASTRO,\n"
                    + " CASE WHEN f.STATUS = 'A' THEN 1 ELSE 0 END status,\n"
                    + " f.PESSOA,\n"
                    + " CASE WHEN f.CPF IS NULL THEN f.CNPJ ELSE f.CPF END cpfcnpj,\n"
                    + " f.CNPJ,\n"
                    + " f.INSC_ESTADUAL,\n"
                    + " f.TELEFONE,\n"
                    + " f.CODIGO_FILIAL,\n"
                    + " f.APELIDO \n"
                    + "FROM FORNECEDOR f\n"
                    + "LEFT JOIN CIDADE_VILA c ON c.CODIGO = f.CODIGO_CIDADE"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setIe_rg(rs.getString("insc_estadual"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setTipo_inscricao(TipoInscricao.FISICA);
                        imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    } else {
                        imp.setTipo_inscricao(TipoInscricao.JURIDICA);
                        imp.setCnpj_cpf(rs.getString("cpfcnpj"));
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
                    "SELECT \n"
                    + " merc1,\n"
                    + " desc1,\n"
                    + " CASE WHEN merc2 IS NULL THEN merc1\n"
                    + " ELSE merc2 END merc2,\n"
                    + " CASE WHEN desc2 IS NULL THEN desc1\n"
                    + " ELSE desc2 END desc2,\n"
                    + " CASE WHEN merc3 IS NULL AND merc2 IS NULL THEN merc1\n"
                    + "      WHEN merc3 IS NULL AND merc2 IS NOT NULL THEN merc2\n"
                    + "      ELSE merc3 END merc3,\n"
                    + " CASE WHEN desc3 IS NULL AND desc2 IS NULL THEN desc1\n"
                    + "      WHEN desc3 IS NULL AND desc2 IS NOT NULL THEN desc2\n"
                    + "      ELSE desc3 END desc3\n"
                    + "FROM (\n"
                    + "SELECT \n"
                    + " s.CODIGO merc1,\n"
                    + " s.DESCRICAO desc1,\n"
                    + " pg.CODIGO merc2,\n"
                    + " pg.DESCRICAO desc2,\n"
                    + " pg.CODIGO merc3,\n"
                    + " pg.DESCRICAO desc3\n"
                    + "FROM PRODUTO_SECAO s\n"
                    + "LEFT JOIN PRODUTO_GRUPO pg ON pg.CODIGO_SECAO = s.CODIGO\n"
                    + ") mercadologico"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

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
                    "SELECT \n"
                    + " CODIGO_PRODUTO produtoid,\n"
                    + " CODIGO_FORNECEDOR fornecedorid,\n"
                    + " CODIGO_PRODUTO_FORNECEDOR referencia\n"
                    + "FROM PRODUTO_FORNECEDOR_PRODUTO \n"
                    + "UNION\n"
                    + "SELECT \n"
                    + " CODIGO produtoid,\n"
                    + " COD_FORNECEDOR fornecedorid,\n"
                    + " REF_PRODUTO_FORNEC referencia\n"
                    + "FROM PRODUTO"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    "SELECT \n"
                    + " c.CODIGO,\n"
                    + " c.CODIGO_CONTA,\n"
                    + " c.BANCO,\n"
                    + " c.AGENCIA,\n"
                    + " c.NUMERO_CHEQUE,\n"
                    + " c.VALOR,\n"
                    + " c.DATA_VENCIMENTO,\n"
                    + " c.DATA_AQUISICAO,\n"
                    + " c.NUMERO_PDV ,\n"
                    + " ce.NOME,\n"
                    + " ce.PESSOA,\n"
                    + " ce.CPF,\n"
                    + " ce.CNPJ,\n"
                    + " ce.RG,\n"
                    + " ce.TELEFONE,\n"
                    + " ce.CELULAR\n"
                    + "FROM CHEQUE c\n"
                    + "JOIN CHEQUE_EMITENTE ce ON ce.CODIGO = c.CODIGO_EMITENTE\n"
                    + "WHERE \n"
                    + "  c.DATA_PAGAMENTO IS NULL"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setDate(rs.getDate("data_aquisicao"));
                    imp.setDataDeposito(rs.getDate("data_vendimento"));
                    imp.setNumeroCheque(rs.getString("numero_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("codigo_conta"));
                    imp.setEcf(rs.getString("numero_pdv"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setCpf(rs.getString("cpf"));
                    }

                    imp.setCpf(rs.getString("cnpj"));
                    imp.setRg(rs.getString("rg"));
                    imp.setNome(rs.getString("nome"));
                    imp.setValor(rs.getDouble("valor"));

                    String contato = (rs.getString("telefone"));
                    if (!"".equals(contato)) {
                        imp.setTelefone(rs.getString("celular"));
                    }
                    imp.setTelefone(rs.getString("telefone"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + " cl.CODIGO,\n"
                    + " cl.NOME,\n"
                    + " cl.RAZAO_SOCIAL,\n"
                    + " cl.ENDERECO,\n"
                    + " cl.NUMERO,\n"
                    + " cl.COMPLEMENTO,\n"
                    + " cl.BAIRRO,\n"
                    + " ci.NOME cidade,\n"
                    + " cl.UF,\n"
                    + " cl.CEP,\n"
                    + " cl.DATA_CADASTRO,\n"
                    + " CASE WHEN cl.SITUACAO = 'A' THEN 1\n"
                    + "      ELSE 0 END status,\n"
                    + " cl.OBSERVACAO,\n"
                    + " cl.TELEFONE,\n"
                    + " cl.CELULAR,\n"
                    + " cl.EMAIL,\n"
                    + " cl.SITE,\n"
                    + " SUBSTRING(cl.SEXO FROM 1 FOR 1) sexo,\n"
                    + " cl.PESSOA,\n"
                    + " cl.CPF,\n"
                    + " cl.RG,\n"
                    + " cl.NASCIMENTO,\n"
                    + " cl.PROFISSAO,\n"
                    + " cl.RENDA_MENSAL,\n"
                    + " cl.LIMITE,\n"
                    + " cl.PAI,\n"
                    + " cl.MAE,\n"
                    + " cl.ESTADO_CIVIL,\n"
                    + " cl.CNPJ,\n"
                    + " cl.INSC_ESTADUAL,\n"
                    + " cl.COB_ENDERECO,\n"
                    + " cl.COB_NUMERO,\n"
                    + " cl.COB_BAIRRO,\n"
                    + " ci.NOME cidade_cob,\n"
                    + " cl.COB_UF,\n"
                    + " cl.COB_CEP,\n"
                    + " cl.COB_COMPLEMENTO,\n"
                    + " cl.APELIDO,\n"
                    + " cl.CONVENIO,\n"
                    + " cl.DEBITO,\n"
                    + " cl.CODIGO_FILIAL\n"
                    + "FROM CLIENTE cl\n"
                    + "LEFT JOIN CIDADE_VILA ci ON ci.CODIGO = cl.COD_CIDADE"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao_social"));
                    imp.setFantasia(rs.getString("nome"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("J".equals(pessoa)) {
                        imp.setCnpj(rs.getString("cnpj"));
                        imp.setInscricaoestadual(rs.getString("insc_estadual"));
                    }

                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));

                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setDataCadastro(rs.getDate("data_Cadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setOrgaoemissor(rs.getString("uf"));

                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));

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
                    "SELECT \n"
                    + " cp.CODIGO,\n"
                    + " cp.CODIGO_FORNECEDOR fornecedorid,\n"
                    + " cp.NUMERO_DOC,\n"
                    + " cp.NUMERO_PARCELA,\n"
                    + " cp.DATA_EMISSAO,\n"
                    + " CASE WHEN cp.DATA_VENCIMENTO IS NULL THEN cp.DATA_LANCAMENTO ELSE cp.DATA_VENCIMENTO END DATA_VENCIMENTO,\n"
                    + " cp.DATA_LANCAMENTO,\n"
                    + " cp.VALOR_NOMINAL,\n"
                    + " cp.VALOR_ABERTO,\n"
                    + " cp.MULTA,\n"
                    + " cp.JUROS_DIA,\n"
                    + " cp.OBSERVACAO,\n"
                    + " cp.CODIGO_FILIAL\n"
                    + "FROM CONTAS_PAGAR cp\n"
                    + "LEFT JOIN CONTAS_PAGAR_PAGAMENTO cpp ON cpp.CODIGO_CP = cp.CODIGO\n"
                    + "WHERE \n"
                    + " cpp.CODIGO IS NULL "
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setNumeroDocumento(rs.getString("numero_doc"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataEntrada(rs.getTimestamp("data_lancamento"));
                    imp.setValor(rs.getDouble("valor_aberto"));
                    imp.setVencimento(rs.getDate("data_vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    "SELECT \n"
                    + " cr.CODIGO,\n"
                    + " cr.CODIGO_CLIENTE clienteid,\n"
                    + " cr.NUMERO_PARCELA,\n"
                    + " cr.DATA_EMISSAO,\n"
                    + " cr.HORA_EMISSAO,\n"
                    + " cr.DATA_VENCIMENTO,\n"
                    + " cr.STATUS,\n"
                    + " cr.VALOR_NOMINAL,\n"
                    + " cr.VALOR_ABERTO,\n"
                    + " cr.OBSERVACAO,\n"
                    + " cr.CODIGO_FILIAL,\n"
                    + " cr.CODIGO_OS,\n"
                    + " cr.CODIGO_VENDA,\n"
                    + " cr.CODIGO_VENDA_ECF\n"
                    + "FROM CONTAS_RECEBER cr\n"
                    + "LEFT JOIN CONTAS_RECEBER_PAGAMENTO crp ON crp.CODIGO_CR = cr.CODIGO \n"
                    + "WHERE \n"
                    + " crp.CODIGO IS NULL"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setIdCliente(rs.getString("clienteid"));
                    imp.setNumeroCupom(rs.getString("codigo_venda_ecf"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setEcf(rs.getString("codigo_venda_ecf"));
                    imp.setValor(rs.getDouble("valor_aberto"));

                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<DesmembramentoIMP> getDesmembramento() throws Exception {
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
        return new GansoDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new GansoDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setNumeroSerie(rst.getString("serie"));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
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
                    = ""/*"SELECT\n"
                    + "	id id_venda,\n"
                    + "	v.numero,\n"
                    + "	CASE WHEN vd.NUMERO_CFE IS NULL THEN v.ID||'-'||v.NUMERO ELSE vd.NUMERO_CFE END numerocupom,\n"
                    + "	COALESCE (id_pdv,1) ecf,\n"
                    + "	DATA_EMISSAO data,\n"
                    + "	HORA_EMISSAO hora,\n"
                    + "	v.serie,\n"
                    + "	v.VALOR_CONTABIL valor,\n"
                    + "	v.ID_CLIENTE,\n"
                    + "	c.CGC cpf,\n"
                    + "	c.NOME nomecliente,\n"
                    + "	CASE WHEN SITUACAO = 1 THEN 1 ELSE 0 END cancelado\n"
                    + "FROM\n"
                    + "	NF_SAIDA v\n"
                    + "	JOIN NF_SAIDA_SAT vd ON vd.ID_NF_SAIDA = v.ID\n"
                    + "	JOIN CADCLI c ON c.CODIGO = v.ID_CLIENTE \n"
                    + "WHERE\n"
                    + "	ID_EMPRESA = " + idLojaCliente + "\n"
                    + "	AND DATA_EMISSAO BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'\n"*/;
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
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = ""/*"SELECT\n"
                    + "	vi.id nritem,\n"
                    + "	vi.ID_NF id_venda,\n"
                    + "	vi.ID||vi.ID_NF||vi.ID_PRODUTO id_item,\n"
                    + "	vi.ID_PRODUTO,\n"
                    + "	p.DESCRICAO,\n"
                    + "	vi.QTD quantidade,\n"
                    + "	vi.VLR_UNITARIO valor,\n"
                    + "	vi.VLR_ACRESCIMO acrescimo,\n"
                    + "	p.COD_BARRAS codigobarras,\n"
                    + "	un.DESCRICAO unidade\n"
                    + "FROM\n"
                    + "	NF_SAIDA_ITENS vi\n"
                    + " JOIN PRODUTO p ON p.ID = vi.ID_PRODUTO\n"
                    + " JOIN FNC_EMBALAGENS un ON un.ID = vi.ID_EMBALAGEM \n"
                    + " JOIN NF_SAIDA v ON v.ID = vi.ID_NF \n"
                    + "WHERE\n"
                    + "	vi.ID_EMPRESA = " + idLojaCliente + "\n"
                    + "	AND v.DATA_EMISSAO BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"*/;
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
