package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/*
 *
 * @author Guilherme
 *
 */
public class ArautoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ARAUTO";
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
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
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
                    + "	id,\n"
                    + "	IMPOSTO,\n"
                    + "	ALIQUOTA,\n"
                    + "	cst\n"
                    + "FROM \n"
                    + "	IMPOSTO"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("imposto"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            0));
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
                    + "	p.id,\n"
                    + "	p.PRODUTO descricaocompleta,\n"
                    + "	p.NOME_COMPLEMENTAR complemento,\n"
                    + "	p.unidade,\n"
                    + "	p.UNIDADE_COMPRA,\n"
                    + "	p.QUATDE_FTD qtdembcompra,\n"
                    + "	p.cod1 ean,\n"
                    + "	p.CODIGO_CAIXA,\n"
                    + "	p.datacadastro,\n"
                    + "	p.cod2,\n"
                    + "	p.PESANOCAIXA,\n"
                    + "	p.EMPRESA,\n"
                    + "	p.ESTOQUEFISICO estoque,\n"
                    + "	p.ESTOQUEMAXIMO,\n"
                    + "	p.ESTOQUEMINIMO,\n"
                    + "	p.COMPRA custo,\n"
                    + "	p.MARGEM,\n"
                    + "	p.MARKUP,\n"
                    + "	p.unitario,\n"
                    + "	p.VENDA precovenda,\n"
                    + "	p.valor,\n"
                    + "	p.ativo,\n"
                    + "	p.IMPOSTO idicms,\n"
                    + "	p.ICMS,\n"
                    + "	p.ICMSE,\n"
                    + "	p.ICMSS,\n"
                    + "	p.SITTR cst,\n"
                    + "	p.SAT_CST,\n"
                    + "	p.NCM,\n"
                    + "	p.CEST,\n"
                    + "	p.CST_PIS_COFINS\n"
                    + "FROM \n"
                    + "	PRODUTO p"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidade_compra"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    int qtdEmbalagem = Utils.stringToInt(rs.getString("qtdembcompra"), 1);

                    imp.setQtdEmbalagemCotacao(qtdEmbalagem);
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setMargem(rs.getDouble("markup"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 1 ? 1 : 0);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getInt("cst_pis_cofins"));

                    String icmsId = rs.getString("idicms");

                    imp.setIcmsConsumidorId(icmsId);
                    imp.setIcmsDebitoId(icmsId);
                    imp.setIcmsCreditoId(icmsId);
                    imp.setIcmsCreditoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsId);

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
                    "SELECT \n"
                    + "	id, \n"
                    + "	codigo_caixa ean,\n"
                    + "	1 qtdembalagem,\n"
                    + "	unidade\n"
                    + "FROM \n"
                    + "	produto p\n"
                    + "WHERE \n"
                    + "	codigo_caixa != '' AND \n"
                    + "	codigo_caixa IS NOT NULL AND \n"
                    + "	codigo_caixa != 'SEM GTIN'\n"
                    + "UNION ALL \n"
                    + "SELECT \n"
                    + "	pc.PRODID id,\n"
                    + "	pc.PRODCOD ean,\n"
                    + "	pc.PRODQTD qtdembalagem,\n"
                    + "	'UN' unidade\n"
                    + "FROM \n"
                    + "	PRODUTO_CODADICIONAL pc")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));

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
                    + "	f.id,\n"
                    + "	f.CADASTRO,\n"
                    + "	f.razao,\n"
                    + "	f.fantazia,\n"
                    + "	f.doc1 cnpj,\n"
                    + "	f.doc2 ie,\n"
                    + "	f.endereco,\n"
                    + "   f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cidade,\n"
                    + "	f.mncod id_municipioibge,\n"
                    + "	f.numero,\n"
                    + "	f.uf,\n"
                    + "	f.ufcod uf_ibge,\n"
                    + "	f.cep,\n"
                    + "	f.tel,\n"
                    + "	f.fax,\n"
                    + "	f.contato,\n"
                    + "	f.celcontato,\n"
                    + "	f.email,\n"
                    + "	f.ativo\n"
                    + "FROM \n"
                    + "	fornecedor f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantazia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));

                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("cadastro"));

                    imp.setTel_principal(rs.getString("tel"));

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
                    ""
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("descmerc2"));

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
                    + "	pf.PRODUTO_ID id_produto,\n"
                    + "	pf.CODIGO codigoexterno,\n"
                    + "	f.ID id_fornecedor\n"
                    + "FROM \n"
                    + "	PRODUTO_POR_FORNECEDOR pf\n"
                    + "JOIN FORNECEDOR f ON pf.DOC1 = f.DOC1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

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
                    "SELECT \n"
                    + "	id,\n"
                    + "	cadastrastrado cadastro,\n"
                    + "	razao,\n"
                    + "	fantazia,\n"
                    + "	doc1 cpfcnpj,\n"
                    + "	doc2 rg,\n"
                    + "	endereco,\n"
                    + "	complemento,\n"
                    + "	numero,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	ddd,\n"
                    + "	telr fone,\n"
                    + "	telt, \n"
                    + "	cel,\n"
                    + "	site,\n"
                    + "	email,\n"
                    + "	obs,\n"
                    + "	ativa,\n"
                    + "	fax,\n"
                    + "	sexo,\n"
                    + "	nascimento,\n"
                    + "	pai,\n"
                    + "	mae\n"
                    + "FROM \n"
                    + "	clientes"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantazia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setEmail(rs.getString("email"));
                    imp.setCelular(rs.getString("cel"));

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
                    + "	id,\n"
                    + "	CLIID AS IdCliente,\n"
                    + "	DFE_CAIXA AS ecf,\n"
                    + "	PDV_ID AS NumeroCupom,\n"
                    + "	CAST (CADASTRO AS date) as dataemissao,\n"
                    + "	VALOR,\n"
                    + "	VENCIMENTO AS datavencimento\n"
                    + "FROM\n"
                    + "	CREDIARIO c\n"
                    + "WHERE\n"
                    + "	PAGOEM IS NULL")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("IdCliente"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("NumeroCupom"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setValor(rs.getDouble("VALOR"));

                    imp.setDataVencimento(rs.getDate("datavencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
