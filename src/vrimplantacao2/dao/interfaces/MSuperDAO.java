package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
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
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class MSuperDAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean importarCodigoPrincipal;

    private Date vendaDataInicio;
    private Date vendaDataTermino;

    private static final Logger LOG = Logger.getLogger(AriusDAO.class.getName());

    public boolean isImportarCodigoPrincipal() {
        return this.importarCodigoPrincipal;
    }

    public void setImportarCodigoPrincipal(boolean importarCodigoPrincipal) {
        this.importarCodigoPrincipal = importarCodigoPrincipal;
    }

    @Override
    public String getSistema() {
        return "MSuper";
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " SUP003 AS id,\n"
                    + " DESCRICAO AS descricao\n"
                    + "FROM SUP003"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
                OpcaoProduto.DATA_CADASTRO
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

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP999 codigo,\n"
                    + "	FANTASIA nomefantasia,\n"
                    + "	CNPJ cpfcnpj\n"
                    + "FROM\n"
                    + "	SUP999\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
                            )
                    );
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
                    + "	m1.CODIGO codmerc1,\n"
                    + "	m1.DESCRICAO descmerc1,\n"
                    + "	m2.CODIGO codmerc2,\n"
                    + "	m2.DESCRICAO descmerc2,\n"
                    + "	m3.CODIGO codmerc3,\n"
                    + "	m3.DESCRICAO descmerc3\n"
                    + "FROM \n"
                    + "	SUP004 m1\n"
                    + "	LEFT JOIN SUP005 m2 ON m2.SUP004 = m1.SUP004\n"
                    + "	LEFT JOIN SUP006 m3 ON m3.SUP005 = m2.SUP005 AND m2.SUP004 = m1.SUP004\n"
                    + "WHERE m1.ATIVO = 'S' AND m2.ATIVO = 'S' AND m3.ATIVO = 'S'\n"
                    + "ORDER BY 1,3,5"
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
                    + "	pr.margem AS margem,\n"
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
                    + "	SUP001 id,\n"
                    + "	EAN,\n"
                    + "	MULTIPLICADOR qtdembalagem\n"
                    + "FROM\n"
                    + "	SUP013\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

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
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  SELECT\n"
                    + "	f.SUP010 AS idfornecedor,\n"
                    + "	p.SUP001 AS idproduto,\n"
                    + "	f.DTALTERACAO AS dtalteracao,\n"
                    + "FROM\n"
                    + "	SUP010 f\n"
                    + "JOIN SUP001 p ON p.SUP010 = f.SUP010\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

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
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	c.sup024 id,\n"
                    + "	c.razaosocial razao,\n"
                    + "	c.FANTASIA ,\n"
                    + "	c.DTNASCIMENTO as dtnascimento,\n"
                    + "	c.CNPJ_CPF as cpfcnpj,\n"
                    + "	c.INSCRICAO,\n"
                    + "	c.TELEFONE1,\n"
                    + "	c.TELEFONE2,\n"
                    + "	c.CELULAR,\n"
                    + "	c.EMAIL,\n"
                    + "	c.DTCADASTRO as datacadastro,\n"
                    + "	c.DTALTERACAO,\n"
                    + "	c.ENDERECO,\n"
                    + "	c.NUMERO,\n"
                    + "	c.COMPLEMENTO,\n"
                    + "	c.BAIRRO,\n"
                    + "	c.CEP,\n"
                    + "	mun.nome as municipio,\n"
                    + "	mun.uf as uf,\n"
                    + "	c.NOME_PAI as pai,\n"
                    + "	c.NOME_MAE as mae,\n"
                    + "	c.CONJUJE as conjuge,\n"
                    + "	c.OBSERVACAO,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1\n"
                    + "	ELSE 0 END AS STATUS\n"
                    + "FROM\n"
                    + "	SUP024 C\n"
                    + "JOIN sup118 Mun ON\n"
                    + "	Mun.sup118 = c.sup118\n"
                    + "WHERE\n"
                    + "	sup999 = " + getLojaOrigem() + " --(Num. Loja)\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setDataNascimento(rs.getDate("dtnascimento"));

                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricao"));

                    imp.setTelefone(rs.getString("telefone1"));
                    imp.setFax(rs.getString("telefone2"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    //imp.setBloqueado(rs.getBoolean("bloqueado"));
                    //imp.setDataBloqueio(rs.getDate("databloqueio"));
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setObservacao(rs.getString("OBSERVACAO"));
                    //imp.setPrazoPagamento(rs.getInt("prazo_pgto"));
                    //imp.setDiaVencimento(rs.getInt("vencimento"));
                    //imp.setPermiteCheque(rs.getBoolean("permitechq"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));

                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    //imp.setCargo(rs.getString("profissao"));
                    //imp.setSalario(rs.getDouble("salario"));
                    //imp.setValorLimite(rs.getDouble("valorlimite"));

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
                    + "	d.SUP043 AS id,\n"
                    + "	c.SUP024 AS clienteid,\n"
                    + "	d.DUPLICATA AS numerocupom,\n"
                    + "	d.PARCIAL,\n"
                    + "	d.EMISSAO AS dataemissao,\n"
                    + "	d.VENCIMENTO AS dataevencimento,\n"
                    + "	d.DATA_PGTO,\n"
                    + "	d.VALOR,\n"
                    + "	d.VALOR_PAGO,\n"
                    + "	d.JUROS as juros,\n"
                    + "	d.DESCONTO,\n"
                    + "	d.ACRESCIMO,\n"
                    + "	d.OBSERVACAO as obs\n"
                    + "FROM\n"
                    + "	SUP043 d\n"
                    + "JOIN SUP024 c ON c.SUP024 = d.SUP024\n"
                    + "WHERE\n"
                    + "	d.VALOR_PAGO = 0\n"
                    + "	AND\n"
                    + "	d.SUP999 = " + getLojaOrigem() + "-- Num. Loja"
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

    public void setVendaDataInicio(Date vendaDataInicio) {
        this.vendaDataInicio = vendaDataInicio;
    }

    public void setVendaDataTermino(Date vendaDataTermino) {
        this.vendaDataTermino = vendaDataTermino;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(Utils.stringToInt(getLojaOrigem()), vendaDataInicio, vendaDataTermino);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(Utils.stringToInt(getLojaOrigem()), vendaDataInicio, vendaDataTermino);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm;
        private ResultSet rst;
        private final String sql;

        public VendaIterator(int origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoFirebird.getConexao().createStatement();
            this.sql
                    = "SELECT\n"
                    + "	v.SUP184 AS id,\n"
                    + "	v.COO AS numerocupom,\n"
                    + "	s.ECF_NUMERO AS ecf,\n"
                    + "	v.ECF_SERIE AS numeroserie,\n"
                    + "	v.DATA,\n"
                    + "	CASE\n"
                    + "		WHEN v.CANCELADO = 'N' THEN 0\n"
                    + "		ELSE 1\n"
                    + "	END AS cancelado,\n"
                    + "	v.CNPJ_CPF AS cpf,\n"
                    + "	v.HORAI AS horainicio,\n"
                    + "	v.HORAF AS horafim,\n"
                    + "	v.TOTAL AS valor\n"
                    + "FROM\n"
                    + "	SUP184 v\n"
                    + "LEFT JOIN SUP050 s ON s.ECF_SERIE = v.ECF_SERIE\n"
                    + "WHERE\n"
                    + "  v.DATA >= '" + DATE_FORMAT.format(vendaDataInicio) + "'\n"
                    + "	AND\n"
                    + "  v.DATA <= '" + DATE_FORMAT.format(vendaDataTermino) + "'\n"
                    + "	AND\n"
                    + "  v.SUP999 = " + origem + "\n"
                    + "ORDER BY 1";
            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public VendaIMP next() {
            try {
                if (rst.next()) {
                    VendaIMP imp = new VendaIMP();
                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.stringToInt(imp.getId()));
                    imp.setEcf(rst.getInt("ecf"));
                    imp.setData(rst.getDate("data"));
                    
                    String horaInicio = TIMESTAMP.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                    String horaTermino = TIMESTAMP.format(rst.getDate("data")) + " " + rst.getString("horafim");
                    
                    imp.setHoraInicio(TIMESTAMP.parse(horaInicio));
                    imp.setHoraTermino(TIMESTAMP.parse(horaTermino));
                    imp.setCancelado(rst.getBoolean("cancelado"));
                    imp.setSubTotalImpressora(rst.getDouble("valor"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroSerie(rst.getString("numeroserie"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm;
        private ResultSet rst;
        private String sql;

        public VendaItemIterator(int origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoFirebird.getConexao().createStatement();
            this.sql
                    = "SELECT\n"
                    + "	v.SUP155 AS id,\n"
                    + " vc.sup184 idvenda,\n"
                    + "	v.ITEM AS sequencia,\n"
                    + "	v.COO AS numerocupom,\n"
                    + "	vc.COO,\n"
                    + "	v.ECFSERIE AS ecf,\n"
                    + "	v.DATA,\n"
                    + "	v.SUP001 AS produtoid,\n"
                    + "	p.EAN AS ean,\n"
                    + "	v.QUANTIDADE AS qtde,\n"
                    + "	CASE\n"
                    + "		WHEN v.CANCELADO = 'N' THEN 0\n"
                    + "		ELSE 1\n"
                    + "	END AS cancelado,\n"
                    + "	v.DESCONTO,\n"
                    + "	v.ACRESCIMO,\n"
                    + "	v.TOTAL AS valor,\n"
                    + "	tr.DESCRICAO AS unidade,\n"
                    + "	v.CST_ICMS AS cst,\n"
                    + "	v.VL_ICMS AS aliquota\n"
                    + "FROM\n"
                    + "	SUP155 v\n"
                    + "LEFT JOIN SUP184 vc ON v.SUP184 = vc.SUP184\n"
                    + "LEFT JOIN SUP001 p ON p.SUP001 = v.SUP001\n"
                    + "LEFT JOIN SUP009 tr ON tr.SUP009 = p.SUP009_VENDA\n"
                    + "WHERE\n"
                    + "  v.DATA >= '" + DATE_FORMAT.format(vendaDataInicio) + "'\n"
                    + "	AND\n"
                    + "  v.DATA <= '" + DATE_FORMAT.format(vendaDataTermino) + "'\n"
                    + "	AND\n"
                    + "   v.SUP999 = " + origem + "\n"
                    + "ORDER BY 1";
            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public VendaItemIMP next() {
            try {
                if (rst.next()) {
                    VendaItemIMP imp = new VendaItemIMP();

                    imp.setId(rst.getString("id"));
                    imp.setVenda(rst.getString("idvenda"));
                    imp.setSequencia(rst.getInt("sequencia"));
                    imp.setProduto(rst.getString("produtoid"));
                    imp.setQuantidade(rst.getDouble("qtde"));
                    imp.setPrecoVenda(rst.getDouble("valor"));
                    imp.setValorDesconto(rst.getDouble("desconto"));
                    imp.setValorAcrescimo(rst.getDouble("acrescimo"));
                    imp.setCodigoBarras(rst.getString("ean"));
                    imp.setUnidadeMedida(rst.getString("unidade"));
                    imp.setIcmsCst(rst.getInt("cst"));
                    imp.setIcmsAliq(rst.getDouble("aliquota"));
                    imp.setCancelado(rst.getBoolean("cancelado"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }
    }
}
