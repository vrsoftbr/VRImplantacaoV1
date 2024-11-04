package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
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
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Bruno
 */
public class SimpleSolution2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SimpleSolution";
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
                OpcaoCliente.CNPJ,
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ecfAliq_ID as id,\n"
                    + "	ecfAliq_DESCRICAO as descricao,\n"
                    + "	case \n"
                    + "	when ecfAliq_TRIBUTACAO = 'ST' then 60	\n"
                    + "	when ecfAliq_TRIBUTACAO = 'TR' then 0\n"
                    + "	when ecfAliq_TRIBUTACAO = 'NT' then 40\n"
                    + "	end as cst,\n"
                    + "	ecfAliq_ALIQ as aliq,\n"
                    + "	0 as red\n"
                    + "from\n"
                    + "	ecf_aliquota"
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	sec_ID as merc1,\n"
                    + "	sec_DESCRICAO as desc1,\n"
                    + "	sec_ID  as merc2,\n"
                    + "	sec_DESCRICAO as desc2,\n"
                    + "	sec_ID  as merc3,\n"
                    + "	sec_DESCRICAO as desc3\n"
                    + "from\n"
                    + "	secao_cad sc "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	prod_ID as id_produto,\n"
                    + "	prod_CODBARRAS as ean,\n"
                    + "	prod_EMBALAGEM as embalagem\n"
                    + "from\n"
                    + "	produto_cad"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "prod_ID as id_produto,\n"
                    + "prod_REFERENCIA as referencia,\n"
                    + "prod_BALANCACODIGO as codigobalanca,\n"
                    + "p.prod_CODBARRAS as ean,\n"
                    + "prod_DESCRICAO as descricao,\n"
                    + "m.med_ABREVIATURA as tipoembalagem,\n"
                    + "mm.sec_ID as merc1,\n"
                    + "mm.sec_DESCRICAO as desc1,\n"
                    + "mm.sec_ID as merc2,\n"
                    + "mm.sec_DESCRICAO as desc2,\n"
                    + "mm.sec_ID as merc3,\n"
                    + "mm.sec_DESCRICAO as desc3,\n"
                    + "case when p.prod_ATIVO = 'S' then 1 else 0 end as ativo,\n"
                    + "p.prod_ALIQUOTAECF_ID as icms,\n"
                    + "prod_embalagem as embalagem,\n"
                    + "p.prod_CUSTOPRODUTO as custo,\n"
                    + "p.prod_DTCAD as datacadastro,\n"
                    + "p.prod_DTALT as dataalteracao,\n"
                    + "p.prod_ESTOQUEREAL as estoque,\n"
                    + "p.prod_PESOBRUTO as pesobruto,\n"
                    + "p.prod_PESOLIQUIDO as pesoliquido,\n"
                    + "ncm.prodNCM_NCM as ncm,\n"
                    + "ncm.prodNCM_CEST as cest,\n"
                    + "pp.prodPr_VALOR as preco\n,"
                    + "49 as cofins \n"
                    + "from\n"
                    + "	produto_cad p \n"
                    + "left join medida_cad m on p.prod_UNIDADECOMPRAID  = m.med_ID \n"
                    + "left join produto_ncm ncm on p.prod_NCM = ncm.prodNCM_ID \n"
                    + "left join produto_preco pp on pp.prodPr_PRODUTO = p.prod_ID \n"
                    + "left join secao_cad mm on mm.sec_ID = p.prod_SECAOID\n"
                    + "where prod_REFERENCIA is not null;"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("referencia"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("codigobalanca"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));
                    //imp.seteBalanca(rst.getBoolean(""));

                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito = rst.getString("icms");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getInt("cofins"));
                    imp.setPiscofinsCstCredito(rst.getInt("cofins"));
                    //imp.setPiscofinsNaturezaReceita();

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString(""));
                    imp.setRazao(rst.getString(""));
                    imp.setFantasia(rst.getString(""));
                    imp.setCnpj_cpf(rst.getString(""));
                    imp.setIe_rg(rst.getString(""));

                    imp.setEndereco(rst.getString(""));
                    imp.setNumero(rst.getString(""));
                    imp.setBairro(rst.getString(""));
                    imp.setMunicipio(rst.getString(""));
                    imp.setUf(rst.getString(""));
                    imp.setCep(rst.getString(""));

                    imp.setAtivo(rst.getBoolean(""));
                    imp.setObservacao(rst.getString(""));
                    imp.setDatacadastro(rst.getDate(""));
                    imp.setTel_principal(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString(""));
                    imp.setIdProduto(rst.getString(""));
                    imp.setCodigoExterno(rst.getString(""));
                    imp.setQtdEmbalagem(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
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
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    //                    "select\n"
                    //                    + "	cli_ID as id,\n"
                    //                    + "	cli_NOMERAZAO as razao,\n"
                    //                    + "	cli_CPFCNPJ as cpf,\n"
                    //                    + "	cli_RGIE  as rg,\n"
                    //                    + "	cli_LOGRADOURO as endereco,\n"
                    //                    + "	cli_nro as numero,\n"
                    //                    + "	cli_COMPLEMENTO as complemento,\n"
                    //                    + "	cli_BAIRRO as bairro,\n"
                    //                    + "	cad.cid_NOME as municipio,\n"
                    //                    + "	es.est_SIGLA as uf,\n"
                    //                    + "	cli_cep as cep,\n"
                    //                    + "	cli_DTCAD as data_cadastro,\n"
                    //                    + "	case when cli_ATIVO = 'S' then 1 else 0 end ativo,\n"
                    //                    + "	cli_celular1 as contato,\n"
                    //                    + "	c.cli_OBS as obs\n"
                    //                    + "from\n"
                    //                    + "	cliente_cad c\n"
                    //                    + "	left join cidade_cad cad on c.cli_CIDADE_ID = cad.cid_ID \n"
                    //                    + "	left join estado_cad es on cad.cid_ESTADO_ID  = es.est_ID "

                    "SELECT\n"
                    + "    cli_ID AS id,\n"
                    + "    cli_NOMERAZAO AS razao,\n"
                    + "    IFNULL(cli_CPFCNPJ, LPAD(FLOOR(RAND() * 99999999999), 11, '0')) AS cpf,\n"
                    + "    cli_RGIE AS rg,\n"
                    + "    cli_LOGRADOURO AS endereco,\n"
                    + "    cli_nro AS numero,\n"
                    + "    cli_COMPLEMENTO AS complemento,\n"
                    + "    cli_BAIRRO AS bairro,\n"
                    + "    cad.cid_NOME AS municipio,\n"
                    + "    es.est_SIGLA AS uf,\n"
                    + "    cli_cep AS cep,\n"
                    + "    cli_DTCAD AS data_cadastro,\n"
                    + "    CASE WHEN cli_ATIVO = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "    cli_celular1 AS contato,\n"
                    + "    c.cli_OBS AS obs\n"
                    + "FROM\n"
                    + "    cliente_cad c\n"
                    + "    LEFT JOIN cidade_cad cad ON c.cli_CIDADE_ID = cad.cid_ID \n"
                    + "    LEFT JOIN estado_cad es ON cad.cid_ESTADO_ID = es.est_ID;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));

                    String cpf = rst.getString("cpf");
                    imp.setCnpj(cpf == null ? "9999999" + cpf : cpf);

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
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " parc_ID id,\n"
                    + " parc_CODIGO numdoc,\n"
                    + " parc_ORIGEM,\n"
                    + " parc_EMPRESA loja,\n"
                    + " parc_SACADO clienteid,\n"
                    + " parc_VRTITULO valor,\n"
                    + " parc_TOTALPARCELAS totalparcelas, \n"
                    + " parc_DTVCTO vencimento, \n"
                    + " parc_EMISSAO emissao\n"
                    + "from parcela_cad \n"
                    + "where parc_DTPGTO is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("numdoc")));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
