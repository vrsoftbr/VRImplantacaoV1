package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class WBADAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(AriusDAO.class.getName());

    @Override
    public String getSistema() {
        return "WBA";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CAST(CODIGO AS integer) AS id,\n"
                    + "	CGC cpfcnpj,\n"
                    + "	NOME nomefantasia\n"
                    + "FROM\n"
                    + "	FILIAL f \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("id"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
                            )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP002 id,\n"
                    + "	DESCRICAO,\n"
                    + "	PERCENTUAL \n"
                    + "FROM\n"
                    + "	SUP002\n"
                    + "	ORDER BY 1"
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
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO codmerc1,\n"
                    + "	NOME descmerc1,\n"
                    + "	CODIGO codmerc2,\n"
                    + "	NOME descmerc2,\n"
                    + "	CODIGO codmerc3,\n"
                    + "	NOME descmerc3\n"
                    + "FROM\n"
                    + "	CTSETOR c\n"
                    + "ORDER\n"
                    + "	BY 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

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
                    + "	TRIM(CODIGO) idproduto,\n"
                    + "	TRIM(CODIGO) ean,\n"
                    + "	1 AS qtdembalagem,\n"
                    + "	NOME\n"
                    + "FROM\n"
                    + "	CTPROD\n"
                    + "WHERE codigo <> ''\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.sup001 AS codigo,\n"
                    + "	p.SUP002 AS idaliquota,\n"
                    + "	p.descricao AS descricaocompleta,\n"
                    + "	p.descricao_etiqueta AS descricaogondola,\n"
                    + "	p.descricao_reduzida AS descricaoreduzida,\n"
                    + "	p.SUP003 AS familiaid,\n"
                    + "	p.ean AS ean,\n"
                    + "	p.DATA_CADASTRO AS datacadastro,\n"
                    + "	p.DATAALTERACAO,\n"
                    + "	p.status,\n"
                    + "	p.OBSERVACAO,\n"
                    + "	p.CODIGO_CEST AS cest,\n"
                    + "	ncm.digitos AS NCM,\n"
                    + "	pr.custo AS custocomimposto,\n"
                    + "	pr.custo_medio AS custosemimposto,\n"
                    + "	pr.margemsug AS margem,\n"
                    + "	pr.venda AS precovenda,\n"
                    + "	pr.SALDO_MINIMO AS estoqueminimo,\n"
                    + "	pr.SALDO_MAXIMO AS estoquemaximo,\n"
                    + "	pr.SALDO AS estoque,\n"
                    + "	mec1.CODIGO AS mercadologico1,\n"
                    + "	mec2.CODIGO AS mercadologico2,\n"
                    + "	mec3.CODIGO AS mercadologico3,\n"
                    + "	tp.DESCRICAO AS tipoembalagem,\n"
                    + "	tp.MULTIPLICADOR AS qtdembalagem,\n"
                    + "	tp2.DESCRICAO AS tipoembalagemcotacao,\n"
                    + "	tp2.MULTIPLICADOR AS qtdembalagemcotacao,\n"
                    + "	trib.codigo AS cst_icms,\n"
                    + "	tr.percentual AS aliquota_icms,\n"
                    + "	p.reducao_base AS red_base_icms,\n"
                    + "	p.reducao_base_st AS red_base_icms_st,\n"
                    + "	cst.cstpis_s AS cstpis,\n"
                    + "	cst.perpisd AS aliquotapis,\n"
                    + "	cst.cstcofins_s AS cstcofins,\n"
                    + "	cst.percofinsd AS aliquotacofins,\n"
                    + " p.SUP002 || p.IVA_ST || ncm.DIGITOS as idpautafiscal,\n"
                    + "	CASE\n"
                    + "		WHEN bal.SUP001 IS NOT NULL THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END AS balanca,\n"
                    + "	bal.validade,\n"
                    + "	CASE\n"
                    + "		WHEN pr.ativo = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END AS situacaocadastro\n"
                    + "FROM\n"
                    + "	SUP001 p\n"
                    + "JOIN SUP008 pr ON pr.SUP001 = p.SUP001\n"
                    + "LEFT JOIN SUP002 tr ON tr.SUP002 = p.SUP002\n"
                    + "LEFT JOIN SUP009 tp ON tp.SUP009 = p.SUP009_VENDA\n"
                    + "LEFT JOIN SUP009 tp2 ON tp2.SUP009 = p.SUP009_COMPRA\n"
                    + "LEFT JOIN SUP098 trib ON trib.SUP098 = p.SUP098\n"
                    + "LEFT JOIN sup108 ON p.sup108 = sup108.sup108\n"
                    + "LEFT JOIN sup178 cst ON sup108.sup108 = cst.sup108\n"
                    + "LEFT JOIN sup090 ncm ON p.sup090 = ncm.sup090\n"
                    + "LEFT JOIN sup004 mec1 ON mec1.SUP004 = p.SUP004\n"
                    + "LEFT JOIN sup005 mec2 ON mec2.SUP005 = p.SUP005\n"
                    + "LEFT JOIN sup006 mec3 ON mec3.SUP006 = p.SUP006\n"
                    + "LEFT JOIN SUP040 bal ON bal.SUP001 = p.SUP001\n"
                    + "	AND bal.TIPO_BALANCA = 'T'\n"
                    + "WHERE\n"
                    + "	pr.SUP999 = " + getLojaOrigem() + " --(Num. Loja)"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    //imp.setPesoBruto(rst.getDouble("pesobruto"));
                    //imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));
                    imp.setIdFamiliaProduto(rst.getString("familiaid"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPautaFiscalId(rst.getString("idpautafiscal"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " ch.SUP034 AS id,\n"
                    + " ch.CPF_CGC AS cpfcnpj ,\n"
                    + " ch.NUM_CHEQUE AS NUMCHEQUE,\n"
                    + " ch.AGENCIA,\n"
                    + " ch.CONTA,\n"
                    + " ch.VALOR,\n"
                    + " ch.JUROS,\n"
                    + " ch.DATA_EMISSAO AS DTEMISSAO,\n"
                    + " ch.DATA_VENCIMENTO AS DTVENCTO,\n"
                    + " ch.DATA_BAIXA,\n"
                    + " ch.DATA_DEVOLUCAO,\n"
                    + " ch.DATA_ENVIO,\n"
                    + " ch.DATA_DIGITACAO,\n"
                    + " ch.OBS,\n"
                    + " CASE WHEN st.SUP035 = 1 THEN 0\n"
                    + " ELSE 1 END AS situacaocheque,\n"
                    + " st.DESCRICAO \n"
                    + "FROM SUP034 ch\n"
                    + "JOIN SUP035 st ON st.SUP035 = ch.SUP035\n"
                    + "WHERE ch.SUP999 = " + getLojaOrigem() + " --(Num.Loja)"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("ID"));
                    imp.setDate(rst.getDate("DTEMISSAO"));
                    imp.setDataDeposito(rst.getDate("DTVENCTO"));
                    imp.setNumeroCheque(rst.getString("NUMCHEQUE"));
                    imp.setAgencia(rst.getString("AGENCIA"));
                    imp.setConta(rst.getString("CONTA"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValor(rst.getDouble("VALOR"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	sup010 id,\n"
                    + "	razaosocial razao,\n"
                    + "	fantasia,\n"
                    + "	cgc cnpj_cpf,\n"
                    + "	inscricao ie_rg,\n"
                    + "	inscmunicipal insc_municipal,\n"
                    + "	case when ativo = 'S' then 1 else 0 end ativo,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	c.nome municipio,\n"
                    + "	f.sup118 ibge_municipio,\n"
                    + "	c.uf uf,\n"
                    + "	telefone tel_principal,\n"
                    + "	dtcadastro datacadastro,\n"
                    + "	obs observacao\n"
                    + "FROM\n"
                    + "	sup010 f\n"
                    + "JOIN sup118 c ON c.sup118 = f.sup118\n"
                    + "WHERE\n"
                    + "	sup999 = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	distinct\n"
                    + "	p.SUP002 AS idaliquota,\n"
                    + "	p.IVA_ST,\n"
                    + "	ncm.DIGITOS ncm\n"
                    + "FROM\n"
                    + "	SUP001 p\n"
                    + "JOIN SUP008 pr ON pr.SUP001 = p.SUP001\n"
                    + "LEFT JOIN sup090 ncm ON p.sup090 = ncm.sup090\n"
                    + "WHERE\n"
                    + "	pr.SUP999 = 1 AND \n"
                    + "	IVA_ST <> 0 AND \n"
                    + "	IVA_ST > 0.01"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    String id = rst.getString("idaliquota") + rst.getString("IVA_ST") + rst.getString("ncm");

                    imp.setId(id);
                    imp.setIva(rst.getDouble("IVA_ST"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    imp.setAliquotaCreditoId(rst.getString("idaliquota"));
                    imp.setAliquotaDebitoId(imp.getAliquotaCreditoId());
                    imp.setAliquotaCreditoForaEstadoId(imp.getAliquotaCreditoId());
                    imp.setAliquotaDebitoForaEstadoId(imp.getAliquotaCreditoId());

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
                    + "	CODIGO id_produto,\n"
                    + "	FORNECEDOR id_fornecedor,\n"
                    + "	CODPRODFORNEC codexterno,\n"
                    + "	QTDEPOREMBALAGEM qtd_embalagem\n"
                    + "FROM\n"
                    + "	CTPROD_CPRITEM\n"
                    + "WHERE\n"
                    + "	CODPRODFORNEC IS NOT NULL\n"
                    + "	AND CODPRODFORNEC <> ''\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cp.SUP020 AS id,\n"
                    + "	fr.SUP010 AS idfornecedor,\n"
                    + "	cp.SUP999 AS loja,\n"
                    + "	fr.CGC AS cnpj,\n"
                    + "	cp.DUPLICATA AS numerodocumento,\n"
                    + "	cp.EMISSAO AS dataemissao,\n"
                    + "	cp.DATA_ENTRADA AS dataentrada,\n"
                    + "	cp.DATALANCAMENTO,\n"
                    + "	cp.HORALANCAMENTO,\n"
                    + "	cp.VALOR as valor,\n"
                    + "	cp.VALOR_PGTO,\n"
                    + "	cp.VALOR_DESCONTAR,\n"
                    + "	cp.OBSERVACAO as obs,\n"
                    + "	cp.VENCIMENTO as datavencimento\n"
                    + "FROM\n"
                    + "	SUP020 cp\n"
                    + "JOIN SUP010 fr ON fr.SUP010 = cp.SUP010\n"
                    + "WHERE\n"
                    + "	cp.SUP999 = " + getLojaOrigem() + "\n --(Num.Loja)"
                    + " AND\n"
                    + "	cp.VALOR_PGTO = 0"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

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
                    + "	codigo id,\n"
                    + "	nome,\n"
                    + "	cnpj,\n"
                    + "	CASE\n"
                    + "		WHEN RG IS NULL THEN IE\n"
                    + "		ELSE rg\n"
                    + "	END rg_ie,\n"
                    + "	ender endereco,\n"
                    + "	numero,\n"
                    + "	compl,\n"
                    + "	bairro,\n"
                    + "	cidade,	\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	celular,\n"
                    + "	email,\n"
                    + "	BLOQUEIO,\n"
                    + "	CASE COALESCE(INATIVO,0) WHEN 0 THEN 1 ELSE 0 END ativo,\n"
                    + "	DATA data_cadastro,\n"
                    + "	LIMITE,\n"
                    + "	PROFISSAO\n"
                    + "FROM\n"
                    + "	sigcad"
            )) {

                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("compl"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setBloqueado(rs.getBoolean("bloqueio"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setCargo(rs.getString("profissao"));

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
                    ""
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setIdCliente(rs.getString("clienteid"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("dataevencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
