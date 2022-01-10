package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
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
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoDesconto;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Michael
 *
 */
public class VersatilDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Versatil";
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
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
                    + " COD_INCIDENCIA id,\n"
                    + " SIMBOLO,\n"
                    + " DESCRICAO\n"
                    + "FROM INSIDENCIA_PRODUTOS"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao")));
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
                    " SELECT \n"
                    + "  COD_PRODUTO,\n"
                    + "  REF2\n"
                    + " FROM PRODUTOS\n"
                    + " WHERE REF2 <> ''\n"
                    + " AND REF2 IS NOT NULL "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("cod_produto"));
                    imp.setEan(rs.getString("ref2"));

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
                    + " p.COD_PRODUTO,\n"
                    + " p.DESCRICAO,\n"
                    + " p.COD_BARRA_PRODUTO,\n"
                    + " UPPER(uni.SIMBOLO) AS unidade,\n"
                    + " p.COD_FORNECEDOR,\n"
                    + " p.COD_GRUPO m1id,\n"
                    + " p.COD_GRUPO m2id,\n"
                    + " p.COD_GRUPO m3id,\n"
                    + " p.PRECO_CUSTO,\n"
                    + " p.VALOR_DESCONTO,\n"
                    + " p.VALOR_UNITARIO,\n"
                    + " p.VALOR_MARGEM, \n"
                    + " p.ESTOQUE,\n"
                    + " p.ESTOQUE_MAXIMO,\n"
                    + " p.ESTOQUE_MINIMO,\n"
                    + " p.COD_INCIDENCIA id_icms,\n"
                    + " p.PRECO_AVISTA,\n"
                    + " p.PRECO_PRAZO,\n"
                    + " p.COD_SITUACAO_TRIBUTARIA,\n"
                    + " p.ATIVO,\n"
                    + " p.ALIQUOTA_ICMS,\n"
                    + " p.NUMERO_NCM,\n"
                    + " p.CEST,\n"
                    + " p.COD_PIS,\n"
                    + " p.ALIQUOTA_PIS,\n"
                    + " p.COD_COFINS,\n"
                    + " p.ALIQUOTA_COFINS,\n"
                    + " p.CST_PIS_ENTRADA,\n"
                    + " p.CST_COFINS_ENTRADA\n"
                    + " FROM PRODUTOS p\n"
                    + " JOIN UNIDADE_PRODUTO uni ON uni.COD_UNIDADE = p.COD_UNIDADE"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("cod_produto"));
                    imp.setEan(rs.getString("cod_barra_produto"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    imp.setCodMercadologico1("m1id");
                    imp.setCodMercadologico2("m2id");
                    imp.setCodMercadologico3("m3id");

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setFornecedorFabricante("cod_fornecedor");

                    imp.setMargem(rs.getDouble("valor_margem"));
                    imp.setCustoComImposto(rs.getDouble("preco_custo"));
                    imp.setPrecovenda(rs.getDouble("preco_avista"));

                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setNcm(rs.getString("numero_ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms"));
                    imp.setIcmsCreditoId(rs.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsConsumidorId(rs.getString("id_icms"));

                    imp.setIcmsAliqEntrada(rs.getDouble("aliquota_icms"));

                    imp.setPiscofinsCstDebito(rs.getInt("CST_PIS_ENTRADA"));
                    imp.setPiscofinsCstCredito(rs.getInt("CST_PIS_ENTRADA"));

                    int codigoProduto = Utils.stringToInt(rs.getString("cod_produto"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("cod_barra_produto"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rs.getString("unidade"));
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
                    + "	f.COD_FORNECEDOR id,\n"
                    + "    f.RAZAO_SOCIAL razao, \n"
                    + "    f.NOME fantasia,\n"
                    + "    f.CNPJ cnpj,\n"
                    + "    f.INSCRICAO_ESTADUAL ie,\n"
                    + "    CASE \n"
                    + "		WHEN f.ATIVO = 1\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "    END ativo,\n"
                    + "    f.ENDERECO endereco,\n"
                    + "    f.NUMERO_ENDERECO numero,\n"
                    + "    f.BAIRRO bairro,\n"
                    + "    c.NOM_CIDADE municipio,\n"
                    + "    e.SGL_ESTADO uf,\n"
                    + "    c.COD_IBGE ibge_municipio,\n"
                    + "    f.CEP cep,\n"
                    + "    f.FONE1 fone1,\n"
                    + "    f.FONE2 fone2,\n"
                    + "    f.FAX fax,\n"
                    + "    f.EMAIL email,\n"
                    + "    f.DATA_CADASTRO datacadastro\n"
                    + "FROM\n"
                    + "	FORNECEDORES f\n"
                    + "JOIN CIDADE c ON f.COD_CIDADE = c.COD_CIDADE \n"
                    + "JOIN ESTADO e ON c.COD_ESTADO = e.COD_ESTADO \n"
                    + "ORDER BY COD_FORNECEDOR"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setAtivo(rs.getInt("ativo") == 1);

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setBairro(rs.getString("bairro"));

                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));

                    imp.setTel_principal(rs.getString("fone1"));

                    String fone2 = (rs.getString("fone2"));
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fone2);
                    }

                    String fax = (rs.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rs.getString("email"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
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
                    " SELECT \n"
                    + "  COD_GRUPO m1id,\n"
                    + "  NOME m1desc,\n"
                    + "  COD_GRUPO m2id,\n"
                    + "  NOME m2desc,\n"
                    + "  COD_GRUPO m3id,\n"
                    + "  NOME m3desc\n"
                    + " FROM GRUPOS"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("m1id"));
                    imp.setMerc1Descricao(rs.getString("m1desc"));
                    imp.setMerc2ID(rs.getString("m2id"));
                    imp.setMerc2Descricao(rs.getString("m2desc"));
                    imp.setMerc3ID(rs.getString("m3id"));
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
                    "SELECT \n"
                    + "  COD_PRODUTO,\n"
                    + "  COD_FORNECEDOR\n"
                    + " FROM PRODUTOS\n"
                    + " WHERE \n"
                    + " COD_FORNECEDOR IS NOT NULL"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("COD_FORNECEDOR"));
                    imp.setIdProduto(rs.getString("COD_PRODUTO"));

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
                    "SELECT  \n"
                    + " cli.COD_CLIENTE id_cliente,\n"
                    + " cli.NOME,\n"
                    + " cli.RAZAO,\n"
                    + " cli.ENDERECO,\n"
                    + " cli.BAIRRO,\n"
                    + " cli.CEP,\n"
                    + " ci.NOM_CIDADE municipio,\n"
                    + " ci.COD_IBGE,\n"
                    + " es.SGL_ESTADO uf,\n"
                    + " cli.NUMERO_ENDERECO numero,\n"
                    + " cli.FONE telefone1,\n"
                    + " cli.CELULAR,\n"
                    + " cli.CNPJ_CGC,\n"
                    + " cli.IE_RG,\n"
                    + " cli.LIMCREDITO limite,\n"
                    + " cli.EMAIL,\n"
                    + " cli.DATCADASTRO dtcadastro,\n"
                    + " cli.DATA_NASCIMENTO dtnascimento,\n"
                    + " cli.CONJUGE,\n"
                    + " cli.PAI,\n"
                    + " cli.MAE,\n"
                    + " cli.ATIVO,\n"
                    + " cli.BLOQUEIA,\n"
                    + " cli.SEXO\n"
                    + "FROM CLIENTES cli\n"
                    + "LEFT JOIN CIDADE ci ON ci.COD_CIDADE = cli.COD_CIDADE\n"
                    + "LEFT JOIN ESTADO es ON es.COD_ESTADO = ci.COD_ESTADO"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id_cliente"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cnpj_cgc"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                
                    if(rs.getString("limite") == null || "".equals(rs.getString("limite"))) {
                        imp.setValorLimite(0.0);
                    } else if (rs.getString("limite").length() > 7){
                        imp.setValorLimite(Double.parseDouble(rs.getString("limite").replace(",", ".").substring(0, 4))); 
                    } else {
                    imp.setValorLimite(Double.parseDouble(rs.getString("limite").replace(",", ".")));
                    }
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone("telefone");
                    imp.setCelular(rs.getString("celular"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueia"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));

                    imp.setEmail(rs.getString("email"));
                    imp.setSexo(rs.getString("sexo"));

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
                    + "	cp.COD_CPAGAR id,\n"
                    + "	cp.COD_FORNECEDOR idFornecedor,\n"
                    + "	f.CNPJ CNPJ ,\n"
                    + "	cp.DOC numeroDocumento,\n"
                    + "	cp.DATA_ENTRADA dataEntrada,\n"
                    + "	cp.DATA_ENTRADA dataEmissao,\n"
                    + "	cp.TOTAL_NOTA valor,\n"
                    + "	cp.OBSERVACAO observacao\n"
                    + "FROM \n"
                    + "	 CONTAS_PAGAR cp \n"
                    + "LEFT JOIN FORNECEDORES f ON cp.COD_FORNECEDOR = f.COD_FORNECEDOR\n"
                    + "WHERE f.ATIVO = 1\n"
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
                    imp.setObservacao("observacao");

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
                    + " c_re.COD_CRECEBER id,\n"
                    + " c_re.COD_CLIENTE id_cliente,\n"
                    + " ic_receb.EMISSAO,\n"
                    + " ic_receb.VENCIMENTO,\n"
                    + " ic_receb.NUMERO_DUPLICATA numerodocumento,\n"
                    + " ic_receb.VALOR,\n"
                    + " ic_receb.TAXA_JUROS\n"
                    + "FROM ITENS_CONTA_RECEBER ic_receb\n"
                    + "JOIN CONTAS_RECEBER c_re ON c_re.COD_CRECEBER = ic_receb.COD_CRECEBER \n"
                    + "WHERE \n"
                    + " ic_receb.DATA_PAGAMENTO IS NULL "
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom("numerodocumento");
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("taxa_juros"));

                    imp.setDataVencimento(rs.getDate("vencimento"));

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
        return new VersatilDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VersatilDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpfcnpj"));
                        next.setNomeCliente(rst.getString("nome_cliente"));
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
                    + "	v.COD_VENDA id_venda,\n"
                    + "	CASE\n"
                    + "		WHEN v.NR_CUPOM IS NULL\n"
                    + "		THEN v.COD_VENDA\n"
                    + "		ELSE v.NR_CUPOM\n"
                    + "	END numerocupom,\n"
                    + "	v.COD_CAIXA ecf,\n"
                    + "	v.DATA_VENDA DATA,\n"
                    + "	v.HORA,\n"
                    + "	v.COD_CLIENTE id_cliente,\n"
                    + "	v.NOME_CLIENTE,\n"
                    + "	c.CNPJ_CGC cpfcnpj,\n"
                    + "	CASE\n"
                    + "		WHEN STATUS_VENDA = 'CANCELADA'\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END cancelado\n"
                    + "FROM\n"
                    + "	VENDAS v\n"
                    + "JOIN CLIENTES c ON c.COD_CLIENTE = v.COD_CLIENTE\n"
                    + "WHERE\n"
                    + "	DATA_VENDA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "ORDER BY 1";
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
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
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
                    = "SELECT\n"
                    + "	vi.HORA_VENDA,\n"
                    + "	vi.COD_VENDA id_venda,\n"
                    + "	vi.COD_ITENS_VENDA id_item,\n"
                    + "	vi.COD_PRODUTO id_produto,\n"
                    + "	p.COD_BARRA_PRODUTO ean,\n"
                    + "	UPPER(u.SIMBOLO) unidade,\n"
                    + "	p.DESCRICAO,\n"
                    + "	vi.QTDE quantidade,\n"
                    + "	vi.VALOR_UNITARIO precovenda,\n"
                    + "	vi.DESCONTO,\n"
                    + "	CASE\n"
                    + "		WHEN vi.CANCELADO = 'SIM'\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END CANCELADO\n"
                    + "FROM\n"
                    + "	ITENS_VENDA vi\n"
                    + "JOIN PRODUTOS p ON p.COD_PRODUTO = vi.COD_PRODUTO\n"
                    + "JOIN UNIDADE_PRODUTO u ON u.COD_UNIDADE = p.COD_UNIDADE\n"
                    + "WHERE\n"
                    + "	vi.DATA_VENDA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1, 2";
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
