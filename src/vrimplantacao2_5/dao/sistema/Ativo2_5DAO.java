package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Alan
 */
public class Ativo2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Ativo";
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
                OpcaoCliente.VENCIMENTO_ROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
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
                    ""
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString(""));
                    imp.setMerc1Descricao(rst.getString(""));
                    imp.setMerc2ID(rst.getString(""));
                    imp.setMerc2Descricao(rst.getString(""));
                    imp.setMerc3ID(rst.getString(""));
                    imp.setMerc3Descricao(rst.getString(""));
                    imp.setMerc4ID(rst.getString(""));
                    imp.setMerc4Descricao(rst.getString(""));
                    imp.setMerc5ID(rst.getString(""));
                    imp.setMerc5Descricao(rst.getString(""));

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
                    ""
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString(""));
                    imp.setDescricao(rst.getString(""));

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
                    "select \n"
                    + "produto as id_produto,\n"
                    + "codbarras as ean,\n"
                    + "unidade as qtdembalagem\n"
                    + "from produtosbarras p "
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString(""));
                    imp.setEan(rst.getString(""));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString(""));
                    imp.setDescricaoReduzida(rst.getString(""));
                    imp.setDescricaoGondola(rst.getString(""));
                    imp.setTipoEmbalagem(rst.getString(""));
                    imp.setQtdEmbalagem(rst.getInt(""));
                    imp.seteBalanca(rst.getBoolean(""));

                    imp.setCustoComImposto(rst.getDouble(""));
                    imp.setCustoSemImposto(rst.getDouble(""));
                    imp.setPrecovenda(rst.getDouble(""));

                    imp.setCodMercadologico1(rst.getString(""));
                    imp.setCodMercadologico2(rst.getString(""));
                    imp.setCodMercadologico3(rst.getString(""));
                    imp.setCodMercadologico4(rst.getString(""));
                    imp.setCodMercadologico5(rst.getString(""));

                    imp.setSituacaoCadastro(rst.getInt(""));
                    imp.setDataCadastro(rst.getDate(""));
                    imp.setDataAlteracao(rst.getDate(""));
                    imp.setEstoqueMinimo(rst.getDouble(""));
                    imp.setEstoqueMaximo(rst.getDouble(""));
                    imp.setEstoque(rst.getDouble(""));
                    imp.setPesoBruto(rst.getDouble(""));
                    imp.setPesoLiquido(rst.getDouble(""));

                    imp.setNcm(rst.getString(""));
                    imp.setCest(rst.getString(""));

                    String idIcmsDebito = rst.getString("");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getString(""));
                    imp.setPiscofinsCstCredito(rst.getString(""));
                    imp.setPiscofinsNaturezaReceita(rst.getString(""));

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
                    "select\n"
                    + "	codigo as id,\n"
                    + "	nome as razao,\n"
                    + "	nomefanta as fantasia,\n"
                    + "	nroinsc as cnpj,\n"
                    + "	nroinsc as rgie,\n"
                    + "	endereco as endereco,\n"
                    + "	nroend as numero,\n"
                    + "	bairro as bairro,\n"
                    + "	cidade as municipio,\n"
                    + "	estado as uf,\n"
                    + "	cep as cep,\n"
                    + "	observacao as obs,\n"
                    + "	dataatual as datacadastro,\n"
                    + "	tel as tel\n"
                    + "from\n"
                    + "	fornecedores f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("rgie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setObservacao(rst.getString("obs"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setTel_principal(rst.getString("tel"));

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
                    "select\n"
                    + "	fornecedor as id_fornecedor,\n"
                    + "	produto as id_produto,\n"
                    + "	codfor as codigoexterno,\n"
                    + "	unidade as qtdembalagem\n"
                    + "from\n"
                    + "	produtosfornecedor p"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdemblagem"));

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
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdFornecedor(rst.getString(""));
                    imp.setNumeroDocumento(rst.getString(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate(""), rst.getDouble(""), rst.getString(""));

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
                    "select\n"
                    + "	codigo as id, \n"
                    + "	nome as razao,\n"
                    + "	nomefanta as fantasia,\n"
                    + "	nroinsc as cnpj,\n"
                    + "	nroinsc as rg,\n"
                    + "	endereco as endereco,\n"
                    + "	nroend as numero,\n"
                    + "	bairro ,\n"
                    + "	cidade as municipio,\n"
                    + "	estado as uf,\n"
                    + "	cep as cep	\n"
                    + "from\n"
                    + "	fornecedores f"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString(""));
                    imp.setRazao(rst.getString(""));
                    imp.setFantasia(rst.getString(""));
                    imp.setCnpj(rst.getString(""));
                    imp.setInscricaoestadual(rst.getString(""));

                    imp.setEndereco(rst.getString(""));
                    imp.setNumero(rst.getString(""));
                    imp.setBairro(rst.getString(""));
                    imp.setMunicipio(rst.getString(""));
                    imp.setUf(rst.getString(""));
                    imp.setCep(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Autowired
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString(""));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("")));
                    imp.setIdCliente(rst.getString(""));
                    imp.setCnpjCliente(rst.getString(""));
                    imp.setEcf(rst.getString(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataVencimento(rst.getDate(""));
                    imp.setObservacao(rst.getString(""));

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
}
