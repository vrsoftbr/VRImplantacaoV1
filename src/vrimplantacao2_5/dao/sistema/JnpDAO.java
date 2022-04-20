/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.openide.util.Exceptions;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.vo.sistema.JnpVO;

/**
 *
 * @author Michael
 */
public class JnpDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean utilizarSup025 = false;
    public JnpVO jnpVO = null;
    private final String SISTEMA = "JNP";

    @Override
    public String getSistema() {
        return SISTEMA;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP999 id,\n"
                    + "	FANTASIA nomefantasia,\n"
                    + "	CNPJ cpfcnpj\n"
                    + "FROM\n"
                    + "	SUP999\n"
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

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	s.FANTASIA descricao\n"
                    + "FROM\n"
                    + "	SUP999 s "
            )) {
                while (rst.next()) {
                    result.add(rst.getString("descricao"));
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
                    "SELECT DISTINCT \n"
                    + "	p.SUP002,\n"
                    + "	trib.CODIGO, \n"
                    + "	CASE \n"
                    + "	WHEN p.SUP002 = 1\n"
                    + "	THEN 60\n"
                    + "	WHEN p.SUP002 = 2\n"
                    + "	THEN 40\n"
                    + "	WHEN p.SUP002 = 3\n"
                    + "	THEN 41\n"
                    + "	WHEN p.SUP002 != 1 OR p.SUP002 != 2  OR p.SUP002 != 3 \n"
                    + "	THEN '00'\n"
                    + "	ELSE p.SITUACAOTRIB \n"
                    + "	END cst,\n"
                    + "	tr.percentual AS aliquota,\n"
                    + "	p.REDUCAO_BASE AS reducao\n"
                    + "FROM\n"
                    + "	SUP001 p\n"
                    + "LEFT JOIN SUP002 tr ON tr.SUP002 = p.SUP002\n"
                    + "LEFT JOIN SUP098 trib ON trib.SUP098 = p.SUP098"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("aliquota") + "-" + rs.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    /*@Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP002 id,\n"
                    + "	DESCRICAO,\n"
                    + "	PERCENTUAL \n"
                    + "FROM\n"
                    + "	SUP002\n"
                    + "	ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }*/
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
                    + "	LEFT JOIN SUP005 m2 ON m2.SUP004 = m1.SUP004 AND m2.ATIVO = 'S'\n"
                    + "	LEFT JOIN SUP006 m3 ON m3.SUP005 = m2.SUP005 AND m2.SUP004 = m1.SUP004 AND m3.ATIVO = 'S'\n"
                    + "WHERE m1.ATIVO = 'S'\n"
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
                    + "	TRIM(ncm.COD_CEST) AS cest,\n"
                    + "	TRIM(ncm.digitos) AS NCM,\n"
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
                    + "	CASE \n"
                    + "	WHEN p.SUP002 = 1\n"
                    + "	THEN 60\n"
                    + "	WHEN p.SUP002 = 2\n"
                    + "	THEN 40\n"
                    + "	WHEN p.SUP002 = 3\n"
                    + "	THEN 41\n"
                    + "	WHEN p.SUP002 != 1 OR p.SUP002 != 2  OR p.SUP002 != 3 \n"
                    + "	THEN '00'\n"
                    + "	ELSE p.SITUACAOTRIB \n"
                    + "	END cst_icms,\n"
                    + "	tr.percentual AS aliquota_icms,\n"
                    + "	p.REDUCAO_BASE AS red_base_icms,\n"
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
                    + "	pr.SUP999 = '" + getLojaOrigem() + "' --(Num. Loja)"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
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
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("cest"));

                    /*imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));
                    imp.setIdFamiliaProduto(rst.getString("familiaid"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPautaFiscalId(rst.getString("idpautafiscal"));*/
                    String icmsId = rst.getString("cst_icms") + "-" + rst.getString("aliquota_icms") + "-" + rst.getString("red_base_icms");

                    imp.setIcmsConsumidorId(icmsId);
                    imp.setIcmsDebitoId(icmsId);
                    imp.setIcmsCreditoId(icmsId);
                    imp.setIcmsCreditoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsId);

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
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
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	pf.SUP010 AS idfornecedor,\n"
                    + "	p.SUP001 AS idproduto,\n"
                    + "	pf.ULTIMA_COMPRA AS dtalteracao,\n"
                    + "	pf.REFERENCIA AS codigoexterno\n"
                    + "FROM\n"
                    + "	SUP016 pf\n"
                    + "JOIN SUP001 p ON p.SUP001 = pf.SUP001\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataAlteracao(rst.getDate("dtalteracao"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    //imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

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
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DATA_INICIAL dataInicio,\n"
                    + "	DATA_FINAL dataFim,\n"
                    + "	item.PRODUTO idProduto,\n"
                    + "	PRECO.VENDA precoNormal,\n"
                    + "	ITEM.PRECO precoOferta\n"
                    + "FROM\n"
                    + "	PROG_MIDIA ofer\n"
                    + "JOIN PROG_MIDIA_EMPRESAS emp ON emp.PROG_MIDIA = ofer.ID\n"
                    + "JOIN PROG_MIDIA_ITENS item ON ofer.ID = item.PROG_MIDIA\n"
                    + "JOIN PRODUTOS_PRECOS preco ON item.PRODUTO = preco.PRODUTO\n"
                    + "WHERE\n"
                    + "	emp.CONCLUIDO = 'F'\n"
                    + "     AND emp.EMPRESA = " + getLojaOrigem() + "\n"
                    + "     AND DATA_FINAL >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "' "
                    + "	ORDER BY 1,2"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setDataInicio(rst.getDate("dataInicio"));
                    imp.setDataFim(rst.getDate("dataFim"));
                    imp.setPrecoNormal(rst.getDouble("precoNormal"));
                    imp.setPrecoOferta(rst.getDouble("precoOferta"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.SUP001 AS id_produtopai,\n"
                    + "	p.DESCRICAO AS descricao,\n"
                    + "	r.PRODUTO AS id_produtofilho,\n"
                    + "	p2.DESCRICAO AS descricaofilho,\n"
                    + "	r.QUANTIDADE,\n"
                    + "	(r.QUANTIDADE * 1000) AS qtde,\n"
                    + "	1 AS rendimento\n"
                    + "FROM\n"
                    + "	SUP124 r\n"
                    + "JOIN SUP001 p ON p.SUP001 = r.SUP001\n"
                    + "JOIN SUP001 P2 ON p2.SUP001 = r.PRODUTO"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id_produtopai"));
                    imp.setIdproduto(rst.getString("id_produtopai"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita(rst.getInt("qtde"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString("id_produtofilho"));

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
                    + "  p.SUP001 AS idproduto,\n"
                    + "  cx.DUN AS ean,\n"
                    + "  c.MULTIPLICADOR AS qtdembalagem\n"
                    + " FROM SUP081 cx\n"
                    + " JOIN SUP001 p ON p.SUP001 = cx.SUP001 \n"
                    + " JOIN SUP009 c ON c.SUP009 = cx.SUP009\n"
                    + " UNION\n"
                    + " SELECT\n"
                    + "	SUP001 idproduto,\n"
                    + "	EAN,\n"
                    + "	MULTIPLICADOR qtdembalagem\n"
                    + "FROM\n"
                    + "	SUP013\n"
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	f.sup010 id,\n"
                    + "	f.razaosocial razao,\n"
                    + "	f.fantasia,\n"
                    + "	f.cgc cnpj_cpf,\n"
                    + "	f.inscricao ie_rg,\n"
                    + "	null insc_municipal,\n"
                    + "	case when f.ativo = 'S' then 1 else 0 end as ativo,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	c.nome municipio,\n"
                    + "	f.sup118 ibge_municipio,\n"
                    + "	c.uf uf,\n"
                    + "	f.telefone as tel_principal,\n"
                    + "	cnt.NOME as nomecontato,\n"
                    + "	cnt.TELEFONE as telefone,\n"
                    + "	cnt.TELEFONE2 as telefone2,\n"
                    + "	cnt.CELULAR as celular,\n"
                    + "	cnt.FAX as fax,\n"
                    + "	cnt.EMAIL as email,\n"
                    + "	f.dtcadastro datacadastro,\n"
                    + "	f.obs observacao\n"
                    + "FROM\n"
                    + "	sup010 f\n"
                    + "JOIN sup118 c ON c.sup118 = f.sup118\n"
                    + "LEFT JOIN SUP012 cnt ON cnt.SUP010 = f.SUP010 \n"
                    + "WHERE\n"
                    + "	f.sup999 = '" + getLojaOrigem() + "'\n"
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
                    //imp.setInsc_municipal(rst.getString("insc_municipal"));
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

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }

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

        String sqlCliente
                = "SELECT\n"
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
                + "	s.LIMITE limite, \n"
                + "	CASE WHEN ATIVO = 'S' THEN 1\n"
                + "	ELSE 0 END AS STATUS\n"
                + "FROM\n"
                + "	SUP024 C\n"
                + "JOIN sup118 Mun ON\n"
                + "	Mun.sup118 = c.sup118\n"
                + "LEFT JOIN SUP025 s ON C.SUP024 = s.SUP024 \n"
                + "ORDER BY 1";

        if (utilizarSup025) {
            sqlCliente = "SELECT\n"
                    + "	'D.'||c.sup025 AS  id,\n"
                    + "	c.nome razao,\n"
                    + "	c.APELIDO fantasia,\n"
                    + "	c.NASCIMENTO dtnascimento,\n"
                    + "	c.CPF cpfcnpj,\n"
                    + "	c.rg inscricao,\n"
                    + "	c.TELEFONE1,\n"
                    + "	c.TELEFONE2,\n"
                    + "	c.CELULAR,\n"
                    + "	c.EMAIL,\n"
                    + "	c.EMISSAO AS datacadastro,\n"
                    + "	CASE\n"
                    + "		WHEN c.BLOQUEADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END AS BLOQUEADO,\n"
                    + "	c.databloq databloqueio,\n"
                    + "	CASE\n"
                    + "		WHEN c.CANCELADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END status,\n"
                    + "	c.OBSERVACAO,\n"
                    + "	c.PRAZO_PGTO,\n"
                    + "	c.DIA_VENCTO vencimento,\n"
                    + "	CASE\n"
                    + "		WHEN c.CHEQUE_BLOQUEADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END permitechq,\n"
                    + "	c.ENDERECO,\n"
                    + "	c.NUMERO,\n"
                    + "	c.COMPLEMENTO,\n"
                    + "	c.BAIRRO,\n"
                    + "	c.CEP,\n"
                    + "	mun.nome municipio,\n"
                    + "	mun.uf uf,\n"
                    + "	c.pai,\n"
                    + "	c.MAE,\n"
                    + "	c.CONJUGE,\n"
                    + "	c.CPFCONJUGE,\n"
                    + "	c.PROFISSAO,\n"
                    + "	c.RENDAMENSAL SALARIO,\n"
                    + "	c.LIMITE valorlimite\n"
                    + "FROM\n"
                    + "	SUP025 C\n"
                    + "JOIN sup118 Mun ON Mun.sup118 = c.sup118\n"
                    + "ORDER BY 1";
        }

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(sqlCliente)) {
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
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setObservacao(rs.getString("OBSERVACAO"));

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
                    imp.setValorLimite(rs.getDouble("limite"));

                    if (utilizarSup025) {
                        imp.setCargo(rs.getString("profissao"));
                        imp.setSalario(rs.getDouble("salario"));
                        imp.setValorLimite(rs.getDouble("valorlimite"));
                        imp.setBloqueado(rs.getBoolean("bloqueado"));
                        imp.setDataBloqueio(rs.getDate("databloqueio"));
                        imp.setPrazoPagamento(rs.getInt("prazo_pgto"));
                        imp.setDiaVencimento(rs.getInt("vencimento"));
                        imp.setPermiteCheque(rs.getBoolean("permitechq"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        String rotativoCliente = "SELECT\n"
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
                + " d.SUP029 <> 638\n"
                + "	AND\n"
                + "	d.SUP999 = '" + getLojaOrigem() + "' -- Num. Loja";

        if (utilizarSup025) {
            rotativoCliente = "SELECT \n"
                    + " r.SUP026 AS id,\n"
                    + " 'D.'||c.SUP025 AS clienteid,\n"
                    + " r.CUPOM AS numerocupom,\n"
                    + " r.DATA AS dataemissao,\n"
                    + " r.DATAVENC AS dataevencimento,\n"
                    + " r.DATA_PGTO,\n"
                    + " r.VALOR,\n"
                    + " r.VALOR_PAGO,\n"
                    + " r.VALOR_DESC,\n"
                    + " r.JUROS,\n"
                    + " r.TOTAL_JUROS,\n"
                    + " 'IMP' AS OBS\n"
                    + "FROM SUP026 r\n"
                    + "JOIN SUP025 c ON c.SUP025 = r.SUP025 \n"
                    + "WHERE \n"
                    + " r.VALOR_PAGO = 0\n"
                    + " AND \n"
                    + " r.VALOR > 0\n"
                    + " AND \n"
                    + " r.SUP999 = '" + getLojaOrigem() + "' -- Num. Loja";
        }

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(rotativoCliente)) {
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

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	vsc.CONVENIO id,\n"
                    + "	vsc.NOME_CONVENIADO nome,\n"
                    + "	vsc.SITUACAO bloqueado,\n"
                    + "	s.CPF cnpj,\n"
                    + "	vsc.SITUACAO status, \n"
                    + "	vsc.LIMITE convenioLimite\n"
                    + "FROM\n"
                    + "	VW_SOCIM_CONVENIADOS vsc\n"
                    + "	JOIN SUP025 s ON vsc.CODIGO_CLIENTE = s.SUP025 "
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("nome"));
                    imp.setIdEmpresa(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setSituacaoCadastro("0".equals(rst.getString("status")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setConvenioLimite(rst.getDouble("convenioLimite"));

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
                    + "	cp.VALOR - COALESCE(cp.VLRDEDUCOES, 0) as valor,\n"
                    + "	cp.VALOR_PGTO,\n"
                    + "	cp.OBSERVACAO as obs,\n"
                    + "	cp.VENCIMENTO as datavencimento\n"
                    + "FROM\n"
                    + "	SUP020 cp\n"
                    + "JOIN SUP010 fr ON fr.SUP010 = cp.SUP010\n"
                    + "WHERE\n"
                    + "	cp.SUP999 = " + getLojaOrigem() + "\n"
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
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "WITH fibra AS (SELECT SUP161 id, SUP001 prod, PORCAO fibra FROM SUP161\n"
                    + "  	WHERE DESCRICAO = 'Fibra Alimentar'), sodio AS (SELECT SUP161 id, SUP001 prod, PORCAO sodio FROM SUP161\n"
                    + "  	WHERE DESCRICAO = 'Sodio'), saturadas AS (SELECT SUP161 id, SUP001 prod, PORCAO saturadas FROM SUP161\n"
                    + "  	WHERE DESCRICAO = 'Gorduras Saturadas'), trans AS (SELECT SUP161 id, SUP001 prod, PORCAO trans FROM SUP161\n"
                    + "  	WHERE DESCRICAO = 'Gorduras Trans')\n"
                    + "  SELECT DISTINCT \n"
                    + "  n.SUP001 id,\n"
                    + "  n.DESCRICAO_ETIQUETA descritivo,\n"
                    + "  n.ATIVO id_situacaocadastro,\n"
                    + "  n.VRENERGETICO caloria,\n"
                    + "  n.CARBOIDRATOS carboidratos,\n"
                    + "  n.PROTEINAS proteina,\n"
                    + "  n.LIPIDIOS gorduras,\n"
                    + "  saturadas.saturadas saturadas,\n"
                    + "  trans.trans trans,\n"
                    + "  CAST (n.NUTRI_QTD_CAS AS int) unidade,\n"
                    + "  CASE \n"
                    + "  WHEN substring(n.NUTRI_QTD_CAS FROM 3) = 50000\n"
                    + "  THEN 3\n"
                    + "  WHEN substring(n.NUTRI_QTD_CAS FROM 3) = 40000\n"
                    + "  THEN 1\n"
                    + "  ELSE 0\n"
                    + "  END id_tipomedidadecimal,\n"
                    + "  CAST (n.NUTRI_QTD_POR AS int) peso,\n"
                    + "  fibra.fibra fibra,\n"
                    + "  sodio.sodio sodio,\n"
                    + "  COALESCE(trim(n.SUP185), 5) medida\n"
                    + "from\n"
                    + "  SUP001 n\n"
                    + "  JOIN fibra ON n.SUP001 = fibra.prod\n"
                    + "  JOIN sodio ON n.SUP001 = sodio.prod\n"
                    + "  JOIN saturadas ON n.SUP001 = saturadas.prod\n"
                    + "  JOIN trans ON n.SUP001 = trans.prod\n"
                    + "  WHERE n.NUTRI_QTD_POR IS NOT NULL "
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidratos"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setGordura(rst.getDouble("gorduras"));
                    imp.setGorduraSaturada(rst.getDouble("saturadas"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao("0." + rst.getString("peso"));
                    imp.setId_tipomedidadecimal(rst.getInt("id_tipomedidadecimal"));
                    imp.setIdTipoMedida(rst.getInt("medida") - 1);
                    imp.setMedidaInteira(rst.getInt("unidade"));
                    imp.addProduto(rst.getString("id"));

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
                    + "WHERE ch.SUP999 = '" + getLojaOrigem() + "' --(Num.Loja)"
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
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  select distinct\n"
                    + " s.SUP001 id, \n"
                    + " d.DESCRICAO descritivo,\n"
                    + " p.LINHA1 receita,\n"
                    + " p.LINHA2 receita2,\n"
                    + " p.LINHA3 receita3,\n"
                    + " p.LINHA4 receita4,\n"
                    + " p.LINHA5 receita5,\n"
                    + " s.SUP001 produto\n"
                    + "from SUP040 s\n"
                    + "JOIN SUP042 p ON p.SUP042 = s.SUP042 \n"
                    + "JOIN SUP001 d ON s.SUP001 = d.SUP001 \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setReceita(rst.getString("receita") +
                                rst.getString("receita2") + 
                                rst.getString("receita3") +
                                rst.getString("receita4") + 
                                rst.getString("receita5"));
                    imp.getProdutos().add(rst.getString("produto"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new JnpDAO.VendaIterator(getLojaOrigem(), this.jnpVO.getDataInicioVenda(), this.jnpVO.getDataTerminoVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new JnpDAO.VendaItemIterator(getLojaOrigem(), this.jnpVO.getDataInicioVenda(), this.jnpVO.getDataTerminoVenda());
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm;
        private ResultSet rst;
        private final String sql;

        public VendaIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = vrimplantacao.classe.ConexaoFirebird.getConexao().createStatement();
            this.sql
                    = "SELECT\n"
                    + "	v.SUP184 AS id,\n"
                    + "	CAST(v.COO AS INT) AS numerocupom,\n"
                    + "	s.ECF_NUMERO,\n"
                    + " CAST(V.CAIXA AS INT) as CAIXA,\n"
                    + " CASE WHEN v.ECF_SERIE =  'IMP NAO FISCAL' \n"
                    + " THEN \n"
                    + " V.CAIXA ELSE s.ECF_NUMERO END AS ecf, \n"
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
                    + "  v.SUP999 = '" + origem + "'\n"
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
                    imp.setNumeroCupom(rst.getInt("numerocupom"));
                    imp.setEcf(rst.getInt("CAIXA"));
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

        public VendaItemIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = vrimplantacao.classe.ConexaoFirebird.getConexao().createStatement();
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
                    + "	v.UNITARIO AS valor,\n"
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
                    + "   v.SUP999 = '" + origem + "'\n"
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
