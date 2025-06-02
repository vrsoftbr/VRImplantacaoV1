package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static vr.core.utils.StringUtils.LOG;
import vrframework.classe.ProgressBar;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.hipcom.HipcomVendaItemIterator;
import vrimplantacao2.dao.interfaces.hipcom.HipcomVendaIterator;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.enums.TipoReceita;
import vrimplantacao2.vo.enums.TipoVistaPrazo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class HipcomDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(HipcomDAO.class.getName());

    private Date rotativoDataInicial;
    private Date rotativoDataFinal;

    //private Date vendaDataInicial;
    //private Date vendaDataFinal;
    private Date receberDataInicial;
    private Date receberDataFinal;

    private Date cpDataInicial;
    private Date cpDataFinal;

    private boolean fornecedorCartao = false;
    private boolean inverteAssociado = false;
    private boolean rotativoBaixado = false;
    private boolean pagarFornecedorBaixado = false;

    //private boolean vendaUtilizaDigito = false;
    //private Integer versaoVenda = 2;
    private boolean importarIcmsEntradaCad = false;

    public void setInverteAssociado(boolean inverteAssociado) {
        this.inverteAssociado = inverteAssociado;
    }

    private boolean getFornecedorCartao() {
        return this.fornecedorCartao;
    }

    public void setFornecedorCartao(boolean fornecedorCartao) {
        this.fornecedorCartao = fornecedorCartao;
    }

    public void setRotativoBaixado(boolean rotativoBaixado) {
        this.rotativoBaixado = rotativoBaixado;
    }

    public void setPagarFornecedorBaixado(boolean pagarFornecedorBaixado) {
        this.pagarFornecedorBaixado = pagarFornecedorBaixado;
    }

    /*public Integer getVersaoVenda() {
        return this.versaoVenda;
    }*/

 /* public void setVersaoVenda(Integer versaoVenda) {
        this.versaoVenda = versaoVenda;
    }*/
    public void setRotativoDataInicial(Date rotativoDataInicial) {
        this.rotativoDataInicial = rotativoDataInicial;
    }

    public void setRotativoDataFinal(Date rotativoDataFinal) {
        this.rotativoDataFinal = rotativoDataFinal;
    }

    /*public void setVendaDataInicial(Date vendaDataInicial) {
        this.vendaDataInicial = vendaDataInicial;
    }

    public void setVendaDataFinal(Date vendaDataFinal) {
        this.vendaDataFinal = vendaDataFinal;
    }*/
    public void setReceberDataInicial(Date receberDataInicial) {
        this.receberDataInicial = receberDataInicial;
    }

    public void setReceberDataFinal(Date receberDataFinal) {
        this.receberDataFinal = receberDataFinal;
    }

    public void setCpDataInicial(Date cpDataInicial) {
        this.cpDataInicial = cpDataInicial;
    }

    public void setCpDataFinal(Date cpDataFinal) {
        this.cpDataFinal = cpDataFinal;
    }

    /*public void setVendaUtilizaDigito(boolean vendaUtilizaDigito) {
        this.vendaUtilizaDigito = vendaUtilizaDigito;
    }*/
    public boolean isImportarIcmsEntradaCad() {
        return this.importarIcmsEntradaCad;
    }

    public void setImportarIcmsEntradaCad(boolean importarIcmsEntradaCad) {
        this.importarIcmsEntradaCad = importarIcmsEntradaCad;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select lojcod, concat(lojcod,' - ', lojfantas) descricao, lojcnpj from hiploj order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("lojcod"), rst.getString("descricao") + " - " + rst.getString("lojcnpj")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "Hipcom";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	t.trbcod,\n"
                    + "	t.trbdescr,\n"
                    + "	t.trbstrib cst,\n"
                    + "	t.trbaliq aliq,\n"
                    + "	t.trbreduc reducao\n"
                    + "from\n"
                    + "	hiptrb t\n"
                    + "order by\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("trbcod"),
                            rst.getString("trbdescr"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.NORMA_REPOSICAO,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.FABRICACAO_PROPRIA,
                OpcaoProduto.RECEITA,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.RECEITA,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ASSOCIADO
        ));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            MultiMap<String, MercadologicoNivelIMP> maps = new MultiMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.depdepto merc1,\n"
                    + "	m1.depdescr merc1desc\n"
                    + "from\n"
                    + "	hipdep m1	\n"
                    + "where\n"
                    + "	not depdepto in (11,12,13) and \n"
                    + "	depsecao = 0\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL1: " + rst.getString("merc1") + " - " + rst.getString("merc1desc"));
                    MercadologicoNivelIMP merc = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1desc"));
                    maps.put(merc,
                            rst.getString("merc1")
                    );
                    result.add(merc);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m.depdepto merc1,\n"
                    + "	m.depsecao merc2,\n"
                    + "	m.depdescr merc2desc\n"
                    + "from\n"
                    + "	hipdep m	\n"
                    + "where\n"
                    + "	m.depdepto != 0 and\n"
                    + "	m.depsecao != 0 and\n"
                    + "	m.depgrupo = 0\n"
                    + "order by 1,2"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL2: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc2desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc2"), rst.getString("merc2desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m.depdepto merc1,\n"
                    + "	m.depsecao merc2,\n"
                    + "	m.depgrupo merc3,\n"
                    + "	m.depdescr merc3desc\n"
                    + "from\n"
                    + "	hipdep m	\n"
                    + "where\n"
                    + "	m.depdepto != 0 and\n"
                    + "	m.depsecao != 0 and\n"
                    + "	m.depgrupo != 0 and\n"
                    + "	m.depsubgr = 0\n"
                    + "order by 1,2, 3"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL3: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc3desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc3"), rst.getString("merc3desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m.depdepto merc1,\n"
                    + "	m.depsecao merc2,\n"
                    + "	m.depgrupo merc3,\n"
                    + "	m.depsubgr merc4,\n"
                    + "	m.depdescr merc4desc\n"
                    + "from\n"
                    + "	hipdep m	\n"
                    + "where\n"
                    + "	m.depdepto != 0 and\n"
                    + "	m.depsecao != 0 and\n"
                    + "	m.depgrupo != 0 and\n"
                    + "	m.depsubgr != 0\n"
                    + "order by 1,2, 3, 4"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL4: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc4") + " - " + rst.getString("merc4desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"), rst.getString("merc3"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc4"), rst.getString("merc4desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3"),
                                rst.getString("merc4")
                        );
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select famcod, famdescr from hipfam order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("famcod"));
                    imp.setDescricao(rst.getString("famdescr"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        String importacaoIcmsEntrada = isImportarIcmsEntradaCad() ? "prc.prlcodtriecad icmsentradaid,\n" : "prc.prlcodtrie icmsentradaid,\n";

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.procodplu id,\n"
                    + "	case when p.prodtcad = '0000-00-00' then '1900-01-01' else p.prodtcad end datacadastro,\n"
                    + "	coalesce(ean.barcodbar, p.procodplu) ean,\n"
                    + "	coalesce(ean.barqtemb, 1) qtdembalagem,\n"
                    + "	coalesce(cot.embqtemb, 1) qtdcotacao,\n"
                    + "	substring(p.proembu, 1,2) tipoembalagem,\n"
                    + "	substring(p.proemb, 1,2) tipoembalagemcotacao,\n"
                    + "	case p.propesado\n"
                    + "	when 'S' then 1\n"
                    + "	else 0 end as ebalanca,\n"
                    + "	p.provalid validade,\n"
                    + "	trim(p.prodescr) descricaocompleta,\n"
                    + "	coalesce(nullif(trim(p.prodescgon),''), p.prodescr) descricaogondola,\n"
                    + "	trim(p.prodescres) descricaoreduzida,\n"
                    + "	p.prodepto merc1,\n"
                    + "	p.prosecao merc2,\n"
                    + "	p.progrupo merc3,\n"
                    + "	p.prosubgr merc4,\n"
                    + "	p.procodfam id_familia,\n"
                    + "	p.propeso pesoliquido,\n"
                    + "	p.propesobruto pesobruto,\n"
                    + "	prc.prlestoq estoque,\n"
                    + "	trc.estoquetroca,\n"
                    + "	prc.prlmargind margemunit,\n"
                    + "	prc.prlcttotu custosemimposto,\n"
                    + "	prc.prlcttotu custocomimposto,\n"
                    + "	prc.prlprvenu precovenda,\n"
                    + "	prc.prlforalin id_situacaocadastro,\n"
                    + "	case prc.prlcotacao when 'S' then 1 else 0 end cotacao,\n"
                    + "	p.proclasfisc ncm,\n"
                    + "	p.procest cest,\n"
                    + "	prc.prlcodpiscofe piscofinsentrada,\n"
                    + "	prc.prlcodpiscofs piscofinssaida,\n"
                    + "	prc.prltabpiscof piscofinsnatrec,\n"
                    + "	prc.prlcodtris icmssaidaid,\n"
                    + importacaoIcmsEntrada
                    + "	prc.prlcodtrie icmsentradaid,\n"
                    + "	prc.prlprvena precoatacado,\n"
                    + "	prc.prlmargata margematacado,\n"
                    + "	l.lojestado estado,\n"
                    + "	prc.prlpivast p_iva,\n"
                    + "	prc.prlvivast v_iva,\n"
                    + "	prc.prlcotacao sugestaocotacao,\n"
                    + "	prc.prlcodcmp id_comprador,\n"
                    + "	p.proalcoolico,\n"
                    + "	p.profinalidade,\n"
                    + "	p.profabterc,\n"
                    + "	prc.prlcodbenef beneficio\n"
                    + "from\n"
                    + "	hippro p\n"
                    + "	left join hiploj l on\n"
                    + "		l.lojcod = " + getLojaOrigem() + "\n"
                    + "	left join hipbar ean on\n"
                    + "		ean.barcodplu = p.procodplu\n"
                    + "	left join hipprl prc on\n"
                    + "		prc.prlcodplu = p.procodplu and\n"
                    + "		prc.prlloja = l.lojcod\n"
                    + "	left join cotemb cot on\n"
                    + "		cot.embcodplu = p.procodplu\n"
                    + "	LEFT join\n"
                    + "		(SELECT\n"
                    + "	trccodplu,\n"
                    + "	sum(trcqtde) estoquetroca\n"
                    + "FROM \n"
                    + "	hiptrc\n"
                    + "WHERE\n"
                    + "	trcdtbxa IS NULL and\n"
                    + "	trcloja = " + getLojaOrigem() + " and\n"
                    + "	trcstatus = 'P'\n"
                    + "GROUP BY\n"
                    + "	1) trc ON p.procodplu = trc.trccodplu\n"
                    + "order BY \n"
                    + "	1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                        imp.seteBalanca(rst.getBoolean("ebalanca"));
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    switch (rst.getInt("profinalidade")) {
                        case 0:
                            imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                            break;
                        //TODO: Incluir os outros tipos.
                    }
                    imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setMargem(rst.getDouble("margemunit"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setFabricacaoPropria("T".equals(rst.getString("profabterc")));

                    switch (Utils.acertarTexto(rst.getString("id_situacaocadastro"))) {
                        case "S":
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(false);
                            break;
                        case "E":
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(true);
                            imp.setVendaPdv(true);
                            break;
                        case "A":
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(true);
                            break;
                        default:
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(true);
                            break;
                    }

                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsDebitoId(rst.getString("icmssaidaid"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icmssaidaid"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icmssaidaid"));
                    imp.setIcmsConsumidorId(rst.getString("icmssaidaid"));
                    imp.setIcmsCreditoId(rst.getString("icmsentradaid"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icmsentradaid"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                    imp.setAtacadoPorcentagem(rst.getDouble("margematacado"));
                    imp.setPautaFiscalId(rst.getString("id"));
                    imp.setSugestaoCotacao("S".equals(rst.getString("sugestaocotacao")));
                    imp.setIdComprador(rst.getString("id_comprador"));
                    imp.setProdutoControlado(rst.getString("proalcoolico"));
                    imp.setBeneficio(rst.getString("beneficio"));

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
                    "SELECT \n"
                    + "	p.procodplu id,\n"
                    + "	p.prorefloja as ean,\n"
                    + " 1 as qtdembalagem, \n"
                    + "	substring(p.proembu, 1,2) tipoembalagem\n"
                    + "FROM hippro p\n"
                    + "WHERE p.prorefloja IS NOT NULL\n"
                    + "UNION ALL \n"
                    + "SELECT\n"
                    + "	p.procodplu id,\n"
                    + "	ean.barcodbar as ean,\n"
                    + "	coalesce(ean.barqtemb, 1) qtdembalagem,\n"
                    + "	substring(p.proembu, 1,2) tipoembalagem\n"
                    + "from\n"
                    + "	hippro p\n"
                    + "	left join hiploj l on\n"
                    + "		l.lojcod = " + getLojaOrigem() + "\n"
                    + "	left join hipbar ean on\n"
                    + "		ean.barcodplu = p.procodplu\n"
                    + "order BY \n"
                    + "	1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        String SqlFornecedor;

        SqlFornecedor
                = "select\n"
                + "	f.forcod id,\n"
                + "	f.forrazao razao,\n"
                + "	coalesce(nullif(trim(f.forfantas),''), f.forrazao) fantasia,\n"
                + "	f.forcnpj cnpj,\n"
                + "	f.forinsest inscricaoestadual,\n"
                + "	f.forinsmunic inscricaomunicipal,\n"
                + "	case f.forforalin when 'S' then 0 else 1 end ativo,\n"
                + "	f.forendere endereco,\n"
                + "	f.forbairro bairro,\n"
                + "	f.formunicip municipio,\n"
                + "	f.forestado uf,\n"
                + "	f.forcodmunic ibge_munic,\n"
                + "	f.forcep cep,\n"
                + "	f.forfone telefone,\n"
                + "	f.forqtmincxa qtdminimapedido,\n"
                + "	f.forfatmin valorminimopedido,\n"
                + "	f.forobserv observacao,\n"
                + "	f.forentrega prazoentrega,\n"
                + "	f.forvisita prazovisita,\n"
                + "	f.forcondpag condicaopagamento,\n"
                + "	f.forcontato contato,\n"
                + "	f.forfonecont fonecontato,\n"
                + "	f.forsite site,\n"
                + "	f.foremail email,\n"
                + "	f.foremailxml emailnfe,\n"
                + "	f.fortipforn tipofornecedor,\n"
                + "	case f.forprodutor when 'S' then 1 else 0 end produtorural\n"
                + "from\n"
                + "	hipfor f\n"
                + "order by 1";

        if (fornecedorCartao) {
            SqlFornecedor = "select\n"
                    + "	f.forcod id,\n"
                    + "	f.forrazao razao,\n"
                    + "	coalesce(nullif(trim(f.forfantas),''), f.forrazao) fantasia,\n"
                    + "	f.forcnpj cnpj,\n"
                    + "	f.forinsest inscricaoestadual,\n"
                    + "	f.forinsmunic inscricaomunicipal,\n"
                    + "	case f.forforalin when 'S' then 0 else 1 end ativo,\n"
                    + "	f.forendere endereco,\n"
                    + "	f.forbairro bairro,\n"
                    + "	f.formunicip municipio,\n"
                    + "	f.forestado uf,\n"
                    + "	f.forcodmunic ibge_munic,\n"
                    + "	f.forcep cep,\n"
                    + "	f.forfone telefone,\n"
                    + "	f.forqtmincxa qtdminimapedido,\n"
                    + "	f.forfatmin valorminimopedido,\n"
                    + "	f.forobserv observacao,\n"
                    + "	f.forentrega prazoentrega,\n"
                    + "	f.forvisita prazovisita,\n"
                    + "	f.forcondpag condicaopagamento,\n"
                    + "	f.forcontato contato,\n"
                    + "	f.forfonecont fonecontato,\n"
                    + "	f.forsite site,\n"
                    + "	f.foremail email,\n"
                    + "	f.foremailxml emailnfe,\n"
                    + "	f.fortipforn tipofornecedor,\n"
                    + "	case f.forprodutor when 'S' then 1 else 0 end produtorural\n"
                    + "from\n"
                    + "	hipfor f\n"
                    + "union	\n"
                    + "   select \n"
                    + "	concat('card-',ctaid) id,\n"
                    + "	ctadescr razao,\n"
                    + "	ctadescr fantasia,\n"
                    + "	ctaid cnpj,\n"
                    + "	'' inscricaoestadual,\n"
                    + "	'' inscricaomunicipal,\n"
                    + "	1 ativo,\n"
                    + "	'' endereco,\n"
                    + "	'' bairro,\n"
                    + "	'' municipio,\n"
                    + "	'' uf,\n"
                    + "	'' ibge_munic,\n"
                    + "	'' cep,\n"
                    + "	'' telefone,\n"
                    + "	0 qtdminimapedido,\n"
                    + "	0 valorminimopedido,\n"
                    + "	'' observacao,\n"
                    + "	0 prazoentrega,\n"
                    + "	0 prazovisita,\n"
                    + "	'' condicaopagamento,\n"
                    + "	'' contato,\n"
                    + "	'' fonecontato,\n"
                    + "	'' site,\n"
                    + "	'' email,\n"
                    + "	'' emailnfe,\n"
                    + "	'' tipofornecedor,\n"
                    + "	0 produtorural\n"
                    + "   from fincta \n"
                    + "	where\n"
                    + "	ctaconta = 1\n"
                    + "	and \n"
                    + "	ctagrupo in (3,4,5,9,7)";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(SqlFornecedor)) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    String endereco = rst.getString("endereco");
                    if (endereco == null) {
                        endereco = "";
                    }
                    int index = endereco.indexOf(",");
                    String numero = "SN";
                    if (index >= 0) {
                        numero = endereco.substring(index + 1, endereco.length());
                        endereco = endereco.substring(0, index);
                    }

                    imp.setEndereco(endereco);
                    imp.setNumero(numero);
                    imp.setComplemento(numero);
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("ibge_munic"));
                    imp.setCep(rst.getString("cep"));

                    imp.copiarEnderecoParaCobranca();

                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setQtd_minima_pedido(rst.getInt("qtdminimapedido"));
                    imp.setValor_minimo_pedido(rst.getDouble("valorminimopedido"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));

                    String cond = rst.getString("condicaopagamento");
                    if (cond == null) {
                        cond = "";
                    }
                    cond = cond.replaceAll("[^0-9 \\/-]", "");

                    String[] parcs = cond.split("\\/| |\\-");

                    for (String parc : parcs) {
                        if (!"".equals(parc.trim())) {
                            int dia = Utils.stringToInt(parc);
                            imp.addCondicaoPagamento(dia);
                        }
                    }

                    imp.addEmail("NFE", rst.getString("emailnfe"), TipoContato.NFE);
                    imp.addTelefone("TELEFONE", rst.getString("telefone"));
                    imp.addTelefone("TELEFONE", rst.getString("contato"));
                    imp.addCelular("CELULAR", rst.getString("fonecontato"));
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);

                    //gravarContatoFornecedor(imp);
                    /*imp.addContato(rst.getString("contato"), rst.getString("fonecontato"), "", TipoContato.COMERCIAL, "");
                    imp.addEmail("SITE", rst.getString("site"), TipoContato.COMERCIAL);
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.addEmail("NFE", rst.getString("emailnfe"), TipoContato.NFE);*/
                    if (rst.getBoolean("produtorural")) {
                        imp.setProdutorRural();
                    }
                    switch (Utils.acertarTexto(rst.getString("tipofornecedor"))) {
                        case "I":
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                        case "A":
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                            break;
                        case "P":
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            break;
                    }
                    if ("S".equals(Utils.acertarTexto(rst.getString("tipofornecedor")))) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void gravarContatoFornecedor(FornecedorIMP imp) throws Exception {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            String sql = "select distinct\n"
                    + "	h.rfofor id_fornecedor,\n"
                    + "	h.rfonome nome,\n"
                    + "	h.rfoemail email,\n"
                    + "	h.rfofone telefone\n"
                    + "from\n"
                    + "	hiprfo h\n"
                    + "where h.rfofor = " + imp.getImportId();
            LOG.finer(sql);
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    FornecedorContatoIMP c = imp.addContato(
                            rst.getString("nome"),
                            rst.getString("telefone"),
                            "",
                            TipoContato.COMERCIAL,
                            rst.getString("email")
                    );
                    System.out.println(c);
                }
            }
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	pf.prfforn idfornecedor,\n"
                    + "	pf.prfcodplu idproduto,\n"
                    + "	pf.prfreffor codigoexterno,\n"
                    + "	case when pf.prfqtemb < 1 then 1 else pf.prfqtemb end qtdembalagem,\n"
                    + "	pf.prfprtab precopacote\n"
                    + "from\n"
                    + "	hipprf pf\n"
                    + "order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	n.nutcodplu id,\n"
                    + "	p.prodescres descricao,\n"
                    + "	n.nutcaloria caloria,\n"
                    + "	n.nutcarboid carboidrato,\n"
                    + "	n.nutproteina proteina,\n"
                    + "	n.nutgordtot gordura,\n"
                    + "	n.nutgordsat gordurasaturada,\n"
                    + "	n.nutgordtrns gorduratrans,\n"
                    + "	n.nutfibra fibra,\n"
                    + "	n.nutsodio sodio,\n"
                    + "	concat(n.nutqtde, n.nutunidade) porcao\n"
                    + "from\n"
                    + "	hipnut n\n"
                    + "	join hippro p on n.nutcodplu = p.procodplu\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidrato"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setGordura(rst.getDouble("gordura"));
                    imp.setGorduraSaturada(rst.getDouble("gordurasaturada"));
                    imp.setGorduraTrans(rst.getDouble("gorduratrans"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));

                    imp.addProduto(rst.getString("id"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	p.procodplu id,\n"
                    + "	l.lojestado uf,\n"
                    + "	p.proclasfisc ncm,\n"
                    + "	pl.prlpivast p_iva,\n"
                    + "	pl.prlvivast v_iva,\n"
                    + "	#pl.prlvpauta pauta,\n"
                    + "	trs.trbstrib s_cst,\n"
                    + "	trs.trbaliq s_aliq,\n"
                    + "	trs.trbreduc s_reduc,\n"
                    + "	trs.trbicmsst s_aliqst,\n"
                    + "	tre.trbstrib e_cst,\n"
                    + "	tre.trbaliq e_aliq,\n"
                    + "	tre.trbreduc e_reduc,\n"
                    + "	tre.trbicmsst e_aliqst,\n"
                    + "	pl.prlcodtris icmssaidaid,\n"
                    + "	pl.prlcodtrie icmsentradaid\n"
                    + "from\n"
                    + "	hipprl pl\n"
                    + "	join hiploj l on\n"
                    + "		pl.prlloja = l.lojcod\n"
                    + "	join hippro p on\n"
                    + "		pl.prlcodplu = p.procodplu\n"
                    + "	join hiptrb trs on\n"
                    + "		pl.prlcodtris = trs.trbcod \n"
                    + "	join hiptrb tre on\n"
                    + "		pl.prlcodtrie = tre.trbcod\n"
                    + "where\n"
                    + "	pl.prlloja = " + getLojaOrigem() + " and\n"
                    + "	(pl.prlpivast != 0 or\n"
                    + "	pl.prlvivast != 0)\n"
                    + "order by 1,2"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("id"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setUf(rst.getString("uf"));

                    if (rst.getDouble("p_iva") != 0) {
                        imp.setTipoIva(TipoIva.PERCENTUAL);
                        imp.setIva(rst.getDouble("p_iva"));
                    } else {
                        imp.setTipoIva(TipoIva.VALOR);
                        imp.setIva(rst.getDouble("v_iva"));
                    }

                    if (rst.getInt("s_cst") == 60) {
                        imp.setAliquotaDebito(0, rst.getDouble("s_aliqst"), 0);
                    } else {
                        imp.setAliquotaDebito(rst.getInt("s_cst"), rst.getDouble("s_aliq"), rst.getDouble("s_reduc"));
                    }

                    if (rst.getInt("e_cst") == 60) {
                        imp.setAliquotaCredito(0, rst.getDouble("e_aliqst"), 0);
                    } else {
                        imp.setAliquotaCredito(rst.getInt("e_cst"), rst.getDouble("e_aliq"), rst.getDouble("e_reduc"));
                    }

                    if (rst.getInt("s_cst") == 60) {
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("s_aliqst"), 0);
                    } else {
                        imp.setAliquotaDebitoForaEstado(rst.getInt("s_cst"), rst.getDouble("s_aliq"), rst.getDouble("s_reduc"));
                    }

                    if (rst.getInt("e_cst") == 60) {
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("e_aliqst"), 0);
                    } else {
                        imp.setAliquotaCreditoForaEstado(rst.getInt("e_cst"), rst.getDouble("e_aliq"), rst.getDouble("e_reduc"));
                    }

                    //imp.setAliquotaDebitoId(rst.getString("icmsids"));
                    //imp.setAliquotaCreditoId(rst.getString("icmside"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    private String formatPautaFiscalId(String uf, String ncm, double p_iva, double v_iva, int idIcmsSaida, int idIcmsEntrada) {
        return String.format("%s-%s-%.2f-%.2f-%d-%d", uf, ncm, p_iva, v_iva, idIcmsSaida, idIcmsEntrada);
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " cv.cnvcod id,\n"
                    + " cv.cnvnome nome,\n"
                    + " case when cv.cnvcpfcnpj = 0 or cv.cnvcpfcnpj is null then cv.cnvcod\n"
                    + "      else cv.cnvcpfcnpj end cnpj,\n"
                    + " cv.cnvender endereco,\n"
                    + " cv.cnvbairro bairro,\n"
                    + " cv.cnvestado uf,\n"
                    + " ci.ciddescr cidade,\n"
                    + " cv.cnvcep cep,\n"
                    + " cv.cnvprazodias prazo,\n"
                    + " cv.cnvdiafech fechamento\n"
                    + "from clicnv cv\n"
                    + "left join clicid ci on ci.cidcod = cv.cnvcodcida\n"
                    + "where\n"
                    + " cnvcod not in (1,15,17)"
            )) {
                while (rs.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDiaPagamento(rs.getInt("prazo"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + " c.clicod id,\n"
                    + "	c.clicpfcnpj cnpj,\n"
                    + "	c.clirgie inscricaoestadual,\n"
                    + "	c.clinome razao,\n"
                    + "	coalesce(nullif(trim(c.clireduzid),''), c.clinome) fantasia,\n"
                    + "	1 ativo,\n"
                    + "	case sit.sitlicsn when 'S' then 0\n"
                    + "	  else 1 end as bloqueado,\n"
                    + "	c.cliender endereco,\n"
                    + "	c.clicompl complemento,\n"
                    + "	c.clibairro bairro,\n"
                    + "	c.clicidade cidade,\n"
                    + "	c.cliestado estado,\n"
                    + "	c.clicep cep,\n"
                    + "	c.clilimite valorlimite,\n"
                    + "	c.cliprazopag prazopagamento,\n"
                    + "	c.cliloja loja,\n"
                    + " c.clicodconv convenio\n"
                    + "from\n"
                    + "	clicli c\n"
                    + "	left join clisit sit on	c.clicodsitu = sit.sitcod\n"
                    + "where cliloja = " + getLojaOrigem() + "\n"
                    + " and clicodconv not in (0,1,15,17)\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setIdEmpresa(rs.getString("convenio"));
                    imp.setNome(rs.getString("razao"));
                    imp.setConvenioLimite(rs.getDouble("valorlimite"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setLojaCadastro(rs.getInt("loja"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                    + "	r.ctrdtemiss dataemissao,\n"
                    + "	r.ctrdoc numerocupom,\n"
                    + "	r.ctrcaixa ecf,\n"
                    + "	r.ctrvalor valor,\n"
                    + "	r.ctrjuros juros,\n"
                    + "	r.ctrdesc desconto,\n"
                    + "	r.ctrvalabt abatimento,\n"
                    + "	r.ctrsaldo valorfinal,\n"
                    + "	r.ctrobs observacao,\n"
                    + "	r.ctrcod idcliente,\n"
                    + "	r.ctrdtvenc vencimento,\n"
                    + "	r.ctrparc parcela\n"
                    + "from\n"
                    + "	finctr r\n"
                    + "	join clicli c on c.clicod = r.ctrcod and c.clicodconv not in (15,17)\n"
                    + "where\n"
                    + "	r.ctrloja = " + getLojaOrigem() + " and\n"
                    + "	r.ctrvalor > 0 and r.ctrsaldo > 0 and\n"
                    + "	r.ctrtipo = 'C' and\n"
                    + "    r.ctrgrupo in (0, 1, 3, 4, 5, 6, 7, 8, 10) \n"
                    + "order by\n"
                    + "	r.ctrdtemiss"
            )) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setIdConveniado(rs.getString("idcliente"));
                    imp.setDataMovimento(rs.getDate("dataemissao"));
                    imp.setValor(rs.getDouble("valor"));

                    imp.setDataHora(new Timestamp(format.parse(imp.getDataMovimento() + " 00:00:00").getTime()));

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
                    "select\n"
                    + //"	concat(c.cliloja,'-',c.clicod) id,\n" +
                    "   c.clicod id,\n"
                    + "	c.clitipo tipoempresa,\n"
                    + "	c.clicpfcnpj cnpj,\n"
                    + "	c.clirgie inscricaoestadual,\n"
                    + "	c.cliorgaopublic,\n"
                    + "	c.clinome razao,\n"
                    + "	coalesce(nullif(trim(c.clireduzid),''), c.clinome) fantasia,\n"
                    + "	1 ativo,\n"
                    + "	case sit.sitlicsn\n"
                    + "	when 'S' then 0\n"
                    + "	else 1 end as bloqueado,\n"
                    + "	c.cliender endereco,\n"
                    + "	c.clicompl complemento,\n"
                    + "	c.clibairro bairro,\n"
                    + "	c.clicidade cidade,\n"
                    + "	c.cliestado estado,\n"
                    + "	c.clicep cep,\n"
                    + "	c.cliestciv estadocivil,\n"
                    + "	c.clidtcadas datacadastro,\n"
                    + "	c.clidtnasc datanascimento,\n"
                    + "	c.clirazcom empresa,\n"
                    + "	c.cliendcom endereco_empresa,\n"
                    + "	c.clibaicom bairro_empresa,\n"
                    + "	c.clicidcom cidade_empresa,\n"
                    + "	c.cliestcom estado_empresa,\n"
                    + "	c.clicepcom cep_empresa,\n"
                    + "	c.clifonecom telefone_empresa,\n"
                    + "	c.clirenda salario,\n"
                    + "	c.clilimite valorlimite,\n"
                    + "	c.clicontconj conjuge,\n"
                    + "	c.clinomepai pai,\n"
                    + "	c.clinomemae mae,\n"
                    + "	c.clivdd,\n"
                    + "	c.cliprazopag prazopagamento,\n"
                    + "	c.clifoneres fone,\n"
                    + "	c.clifonepro,\n"
                    + "	c.cliemail email,\n"
                    + "	c.clicontato,\n"
                    + "	c.clifax\n"
                    + "from\n"
                    + "	clicli c\n"
                    + "	left join clisit sit on\n"
                    + "		c.clicodsitu = sit.sitcod\n"
                    + "where cliloja = " + getLojaOrigem() + "\n"
                    + " and clicodconv in (1,15,17) "
                    + "order by\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setTipoInscricao("J".equals(rst.getString("tipoempresa")) ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
                    imp.setCnpj(rst.getString("cnpj"));
                    //imp.setInscricaoestadual(Utils.formataNumero(rst.getString("inscricaoestadual")));
                    if ((rst.getString("inscricaoestadual") != null) && (!"".equals(rst.getString("inscricaoestadual")))) {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual").replace("\\", "").replace("-", "").replace(".", ""));
                    }
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    String estCiv = Utils.acertarTexto(rst.getString("estadocivil"));
                    if (estCiv.startsWith("CAS")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else if (estCiv.startsWith("DIV")) {
                        imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                    } else if (estCiv.startsWith("SOL")) {
                        imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                    } else if (estCiv.startsWith("SEP")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("endereco_empresa"));
                    imp.setEmpresaBairro(rst.getString("bairro_empresa"));
                    imp.setEmpresaMunicipio(rst.getString("cidade_empresa"));
                    imp.setEmpresaUf(rst.getString("estado_empresa"));
                    imp.setEmpresaCep(rst.getString("cep_empresa"));
                    imp.setEmpresaTelefone(rst.getString("telefone_empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setPrazoPagamento(rst.getInt("prazopagamento"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setCelular(rst.getString("clifonepro"));
                    imp.setEmail(rst.getString("email"));
                    imp.addContato("A", rst.getString("clicontato"), "", "", "");
                    imp.setFax(rst.getString("clifax"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        //Tabela de plano de contas: "fincta"
        List<CreditoRotativoIMP> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String sqlCreditoRotativo
                = "select\n"
                + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                + "	r.ctrdtemiss dataemissao,\n"
                + "	r.ctrdoc numerocupom,\n"
                + "	r.ctrcaixa ecf,\n"
                + "	r.ctrvalor valor,\n"
                + "	r.ctrjuros juros,\n"
                + "	r.ctrdesc desconto,\n"
                + "	r.ctrvalabt abatimento,\n"
                + "	r.ctrsaldo valorfinal,\n"
                + "	r.ctrobs observacao,\n"
                + // "	concat(r.ctrclilj,'-',r.ctrcod) idcliente,\n" +
                "	r.ctrcod idcliente,\n"
                + "	r.ctrdtvenc vencimento,\n"
                + "	r.ctrparc parcela\n"
                + "from\n"
                + "	finctr r\n"
                + "where\n"
                + "	r.ctrdtemiss >= '" + dateFormat.format(rotativoDataInicial) + "' and\n"
                + "	r.ctrdtemiss <= '" + dateFormat.format(rotativoDataFinal) + "' and\n"
                + "	r.ctrloja = " + getLojaOrigem() + " and\n"
                + "	r.ctrvalor > 0 and r.ctrsaldo > 0 and\n"
                + "	r.ctrtipo = 'C' and\n"
                + "   r.ctrgrupo in (0, 1, 3, 4, 5, 6, 7, 8, 10) \n"
                + "order by\n"
                + "	r.ctrdtemiss";
        if (rotativoBaixado) {
            sqlCreditoRotativo
                    = "select\n"
                    + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                    + "	r.ctrdtemiss dataemissao,\n"
                    + "	r.ctrdoc numerocupom,\n"
                    + "	r.ctrcaixa ecf,\n"
                    + "	r.ctrvalor valor,\n"
                    + "	r.ctrjuros juros,\n"
                    + "	r.ctrdesc desconto,\n"
                    + "	r.ctrvalabt abatimento,\n"
                    + "	r.ctrsaldo valorfinal,\n"
                    + "	r.ctrobs observacao,\n"
                    + // "	concat(r.ctrclilj,'-',r.ctrcod) idcliente,\n" +
                    "	r.ctrcod idcliente,\n"
                    + "	r.ctrdtvenc vencimento,\n"
                    + "	r.ctrparc parcela,\n"
                    + " r.ctrdtbaixa databaixa\n"
                    + "from\n"
                    + "	finctr r\n"
                    + "where\n"
                    + "	r.ctrdtemiss >= '" + dateFormat.format(rotativoDataInicial) + "' and\n"
                    + "	r.ctrdtemiss <= '" + dateFormat.format(rotativoDataFinal) + "' and\n"
                    + "	r.ctrloja = " + getLojaOrigem() + " and\n"
                    + "	r.ctrvalor > 0 and r.ctrdtbaixa is not null and\n"
                    + "	r.ctrtipo = 'C' and\n"
                    + "   r.ctrgrupo in (0, 1, 3, 4, 5, 6, 7, 8, 10) \n"
                    + "order by\n"
                    + "	r.ctrdtemiss";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sqlCreditoRotativo)) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valorfinal"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));

                    if (rotativoBaixado) {
                        imp.addPagamento(imp.getId(), imp.getValor(), 0, 0, rst.getDate("databaixa"), imp.getObservacao() + " - PAGO");
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct cmpcod, cmpnome from hipcmp order by 1"
            )) {
                while (rst.next()) {
                    CompradorIMP imp = new CompradorIMP();

                    imp.setId(rst.getString("cmpcod"));
                    imp.setDescricao(rst.getString("cmpnome"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pr.prlcodplu id_produto,\n"
                    + "	pr.prlprpromu precooferta,\n"
                    + "	pr.prlprvenu preconormal,\n"
                    + "	pr.prldtinipr datainicio,\n"
                    + "	pr.prldtfimpr datafim\n"
                    + "from\n"
                    + "	hipprl pr\n"
                    + "where\n"
                    + "	pr.prlloja = " + getLojaOrigem() + " and\n"
                    + "	not pr.prldtfimpr is null and\n"
                    + "	pr.prldtfimpr >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "'\n"
                    + "   and prldtinipr is not null\n"
                    + "order by\n"
                    + "	datainicio"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoNormal(rst.getDouble("preconormal"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /* "select\n"
                    + "	p.procodplu,\n"
                    + "	p.prodescr,\n"
                    + "	a.reccod,\n"
                    + "	a.recdescr,\n"
                    + "	a.recmemo\n"
                    + "from\n"
                    + "	hiprec a\n"
                    + "	join hipprl pl on\n"
                    + "		a.reccod = pl.prlcodrec and\n"
                    + "		pl.prlloja = " + getLojaOrigem() + "\n"
                    + "	join hippro p on\n"
                    + "		p.procodplu = pl.prlcodplu\n"
                    + "order by\n"
                    + "	reccod, prlcodplu"*/
                    "select \n"
                    + "	 reccod,\n"
                    + "	 recdescr,\n"
                    + "	 recmemo\n"
                    + "	from hiprec"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();

                while (rst.next()) {

                    ReceitaBalancaIMP imp = receitas.get(rst.getString("reccod"));

                    if (imp == null) {
                        imp = new ReceitaBalancaIMP();
                        imp.setId(rst.getString("reccod"));
                        imp.setDescricao(rst.getString("recdescr"));
                        imp.setReceita(rst.getString("recmemo"));
                        receitas.put(imp.getId(), imp);
                    }

                    imp.getProdutos().add(rst.getString("reccod"));
                }

                return new ArrayList<>(receitas.values());
            }
        }

    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sqlReceber;

        sqlReceber
                = "select\n"
                + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                + "	r.ctrcod idfornecedor,\n"
                + "	r.ctrdtemiss dataemissao,\n"
                + "	r.ctrdtvenc vencimento,\n"
                + "	r.ctrvalor + coalesce(r.ctrjuros, 0) - coalesce(r.ctrdesc, 0) valor,\n"
                + "	r.ctrvalabt abatimento,\n"
                + "	r.ctrjuros juros,\n"
                + "	r.ctrdesc desconto,\n"
                + "	r.ctrsaldo valorfinal,\n"
                + "	r.ctrobs observacao\n"
                + "from\n"
                + "	finctr r\n"
                + "where\n"
                + "	r.ctrdtemiss >= '" + dateFormat.format(receberDataInicial) + "' and\n"
                + "	r.ctrdtemiss <= '" + dateFormat.format(receberDataFinal) + "' and\n"
                + "	r.ctrloja = " + getLojaOrigem() + " and\n"
                + "	r.ctrvalor > 0 and r.ctrsaldo > 0 and\n"
                + "	r.ctrtipo = 'F' and\n"
                + "   r.ctrcod IN (SELECT forcod FROM hipfor)\n"
                + "order by\n"
                + "	r.ctrdtemiss";

        if (fornecedorCartao) {
            sqlReceber
                    = "select distinct\n"
                    + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                    + "	r.ctrdtemiss dataemissao,\n"
                    + "	r.ctrdtvenc vencimento,\n"
                    + "	r.ctrvalor + coalesce(r.ctrjuros, 0) - coalesce(r.ctrdesc, 0) valor,\n"
                    + "	r.ctrvalabt abatimento,\n"
                    + "	r.ctrjuros juros,\n"
                    + "	r.ctrdesc desconto,\n"
                    + "	r.ctrsaldo valorfinal,\n"
                    + "	r.ctrobs observacao,\n"
                    + "	concat('card-',e.ctaid) idfornecedor,\n"
                    + "	case when r.ctrgrupo = 3 then 7\n"
                    + "	     when r.ctrgrupo = 4 then 8\n"
                    + "      when r.ctrgrupo = 5 then 9\n"
                    + "      when r.ctrgrupo = 9 and r.ctrsubgr = 1 then 4\n"
                    + "      when r.ctrgrupo = 7 then 10\n"
                    + "	     else 1 end tiporeceita\n"
                    + "from\n"
                    + "	finctr r\n"
                    + "	join fincta e on e.ctagrupo = r.ctrgrupo \n"
                    + "				and e.ctasubgr = r.ctrsubgr \n"
                    + "				and e.ctaconta = 1 and e.ctagrupo in (3,4,5,9,7)\n"
                    + "where\n"
                    + "	r.ctrdtemiss >= '" + dateFormat.format(receberDataInicial) + "' and\n"
                    + "	r.ctrdtemiss <= '" + dateFormat.format(receberDataFinal) + "' and\n"
                    + "	r.ctrloja = " + getLojaOrigem() + "  and\n"
                    + "	r.ctrvalor > 0 and R.ctrdtbaixa is null and\n"
                    + "	r.ctrtipo in ('F','C')\n"
                    + "union\n"
                    + "select distinct\n"
                    + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                    + "	r.ctrdtemiss dataemissao,\n"
                    + "	r.ctrdtvenc vencimento,\n"
                    + "	r.ctrvalor + coalesce(r.ctrjuros, 0) - coalesce(r.ctrdesc, 0) valor,\n"
                    + "	r.ctrvalabt abatimento,\n"
                    + "	r.ctrjuros juros,\n"
                    + "	r.ctrdesc desconto,\n"
                    + "	r.ctrsaldo valorfinal,\n"
                    + "	r.ctrobs observacao,\n"
                    + "	r.ctrcod idfornecedor,\n"
                    + "	case when ctaid = 59 then 3\n"
                    + "	     when ctaid = 65 then 4\n"
                    + "	     when ctaid = 70 then 5\n"
                    + "	     when ctaid = 71 then 6\n"
                    + "	  else 1 end tiporeceita\n"
                    + "from\n"
                    + "	finctr r\n"
                    + "	join fincta e on e.ctagrupo = r.ctrgrupo \n"
                    + "				and e.ctasubgr = r.ctrsubgr \n"
                    + "				and e.ctaconta = 1 and e.ctagrupo not in (3,4,5,9,7)\n"
                    + "where\n"
                    + "	r.ctrdtemiss >= '" + dateFormat.format(receberDataInicial) + "' and\n"
                    + "	r.ctrdtemiss <= '" + dateFormat.format(receberDataFinal) + "' and\n"
                    + "	r.ctrloja = " + getLojaOrigem() + "  and\n"
                    + "	r.ctrvalor > 0 and r.ctrdtbaixa is null and\n"
                    + "	r.ctrtipo = 'F' and \n"
                    + "	r.ctrcod IN (SELECT forcod FROM hipfor)";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sqlReceber)) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    //imp.setTipoReceita(TipoReceita.CR_OUTRAS_UNIDADES);
                    if (rst.getDouble("abatimento") > 0) {
                        imp.add(imp.getId(), rst.getDouble("abatimento"), 0, 0, 0, rst.getDate("vencimento"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        //Tabela de plano de contas: "fincta"
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n"
                    + "	r.ctrdtemiss dataemissao,\n"
                    + "	r.ctrdoc numerocupom,\n"
                    + "	r.ctrncheque cheque,\n"
                    + "   r.ctrbanco banco,\n"
                    + "	r.ctragenc agencia,\n"
                    + "	r.ctrcaixa ecf,\n"
                    + "	r.ctrvalor valor,\n"
                    + "	r.ctrjuros juros,\n"
                    + "	r.ctrdesc desconto,\n"
                    + " r.ctralinea, \n"
                    + " case when r.ctrsubgr = 3 then 46\n"
                    + "	     when r.ctrsubgr = 4 then 77\n"
                    + "	     else 0 end alinea,\n"
                    + "	r.ctrvalabt abatimento,\n"
                    + "	r.ctrsaldo valorfinal,\n"
                    + "	r.ctrobs observacao,\n"
                    + "	concat(r.ctrclilj,'-',r.ctrcod) idcliente,\n"
                    + "   r.ctrcpfcgc cnpj,\n"
                    + "   c.clinome nome,\n"
                    + "	c.clirgie rg,\n"
                    + "	c.clifoneres fone,\n"
                    + "	r.ctrdtvenc vencimento,\n"
                    + "	r.ctrparc parcela,\n"
                    + " r.ctrgrupo,\n"
                    + " r.ctrsubgr\n"
                    + "from\n"
                    + "	finctr r\n"
                    + "LEFT JOIN clicli c ON r.ctrcod = c.clicod\n"
                    + "where\n"
                    + "	r.ctrdtemiss >= '" + dateFormat.format(rotativoDataInicial) + "' and\n"
                    + "	r.ctrdtemiss <= '" + dateFormat.format(rotativoDataFinal) + "' and\n"
                    + "	r.ctrloja = " + getLojaOrigem() + " and\n"
                    + "	r.ctrvalor > 0 and r.ctrsaldo > 0 and\n"
                    + "	r.ctrtipo = 'C' and\n"
                    + "	r.ctrgrupo IN (2) \n"
                    // + " and  r.ctralinea IN (11, 12, 13, 14, 20, 21, 22, 23, 24, 25, 28, 29, 35, 37, 62, 63)\n"
                    + "order by\n"
                    + "	r.ctrdtemiss")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rs.getString("id"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setDate(rs.getDate("dataemissao"));
                    imp.setDataDeposito(rs.getDate("vencimento"));
                    imp.setCpf(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setNumeroCheque(rs.getString("cheque"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valorfinal"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setNome(rs.getString("nome"));
                    imp.setRg(rs.getString("rg"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setVistaPrazo(rs.getInt("ctrsubgr") == 1 ? TipoVistaPrazo.A_VISTA : TipoVistaPrazo.PRAZO);
                    imp.setAlinea(rs.getInt("alinea"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new HipcomVendaIterator(getLojaOrigem(), this.vendaDataInicial, this.vendaDataFinal, this.getVersaoVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new HipcomVendaItemIterator(this.vendaUtilizaDigito, getLojaOrigem(), this.vendaDataInicial, this.vendaDataFinal, this.getVersaoVenda());
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private static final SimpleDateFormat TIMESTAMP_DATE = new SimpleDateFormat("yyyy-MM-dd");
        private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        private Statement stm = ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        public static String makeId(String idLoja, Date data, String ecf, String idCaixa, String numeroCupom) {
            return idLoja + "-" + TIMESTAMP_DATE.format(data) + "-" + ecf + "-" + idCaixa + "-" + numeroCupom;
        }

        private void obterNext() {
            try {

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = makeId(rst.getString("id_loja"), rst.getDate("data"), rst.getString("ecf"), rst.getString("id_caixa"), rst.getString("numerocupom"));
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        String horaInicio = TIMESTAMP_DATE.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = TIMESTAMP_DATE.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(TIMESTAMP.parse(horaInicio));
                        next.setHoraTermino(TIMESTAMP.parse(horaTermino));
                        next.setCancelado("S".equals(rst.getString("cancelado")));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        private String getVendaSQL(String idLojaCliente, Date dataInicio, Date dataTermino, String tableName) {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);

            return "select\n"
                    + "	v.loja id_loja,\n"
                    + "	v.codigo_caixa id_caixa,\n"
                    + "	v.numero_cupom_fiscal numerocupom,\n"
                    + "	v.codigo_terminal ecf,\n"
                    + "	v.data,\n"
                    + "	min(v.hora) horainicio,\n"
                    + "	max(v.hora) horatermino,	\n"
                    + "	min(v.cupom_cancelado) cancelado,\n"
                    + "	sum(v.valor_total) subtotalimpressora\n"
                    + "from\n"
                    + "	" + tableName + " v\n"
                    + "where\n"
                    + "	v.loja = " + idLojaCliente + " and\n"
                    + "	v.data >= '" + strDataInicio + "' and\n"
                    + "	v.data <= '" + strDataTermino + "'\n"
                    + "group by\n"
                    + "	id_loja,\n"
                    + "	id_caixa,\n"
                    + "	numerocupom,\n"
                    + "	ecf,\n"
                    + "	data\n";
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            StringBuilder str = new StringBuilder();

            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_ultimos_meses2"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2017"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2016"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2015"));
            /*str.append("union\n");
            str.append(getVendaSQL("2014", idLojaCliente, dataInicio, dataTermino));
            str.append("union\n");
            str.append(getVendaSQL("2013", idLojaCliente, dataInicio, dataTermino));*/

 /* this.sql = str.toString();

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

    private static class SmProduto {

        public String id;
        public String ean;
        public String descricao;
        public String embalagem;

        public SmProduto(String id, String ean, String descricao, String embalagem) {
            this.id = id;
            this.ean = ean;
            this.descricao = descricao;
            this.embalagem = embalagem;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.id);
            hash = 31 * hash + Objects.hashCode(this.ean);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SmProduto other = (SmProduto) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            return Objects.equals(this.ean, other.ean);
        }

        @Override
        public String toString() {
            return "SmProduto{" + "id=" + id + ", ean=" + ean + ", descricao=" + descricao + '}';
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private Map<String, SmProduto> produtos;
        private Set<String> ids = new HashSet<>();

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {

                        String vendaId = VendaIterator.makeId(rst.getString("id_loja"), rst.getDate("data"), rst.getString("ecf"), rst.getString("id_caixa"), rst.getString("numerocupom"));
                        String idVendaItem = vendaId + "-" + rst.getString("sequencia");

                        if (!ids.contains(idVendaItem)) {

                            ids.add(idVendaItem);

                            String ean = rst.getString("ean");

                            if (ean == null) {
                                ean = "";
                            }

                            if (ean.length() < 7 && ean.length() > 1) {
                                String old = ean;
                                ean = ean.substring(0, ean.length() - 1);
                                LOG.finest("EAN de balanca anterior: " + old + " atual: " + ean);
                            }

                            SmProduto prod = produtos.get(ean);

                            next = new VendaItemIMP();

                            next.setId(idVendaItem);

                            next.setVenda(vendaId);
                            next.setSequencia(rst.getInt("sequencia"));
                            if (prod != null) {
                                next.setProduto(prod.id);
                                next.setDescricaoReduzida(prod.descricao);
                                next.setUnidadeMedida(prod.embalagem);
                                next.setCodigoBarras(prod.ean);
                            } else {
                                next.setProduto("");
                                next.setDescricaoReduzida("SEM DESCRICAO");
                                next.setUnidadeMedida("UN");
                                next.setCodigoBarras(ean);
                            }
                            next.setQuantidade(rst.getDouble("quantidade"));
                            next.setTotalBruto(rst.getDouble("total_bruto"));
                            next.setValorDesconto(0);
                            next.setValorAcrescimo(0);
                            next.setCancelado("S".equals(rst.getString("cancelado")));
                            next.setIcmsCst(Utils.stringToInt(rst.getString("cst")));
                            next.setIcmsAliq(rst.getDouble("aliquota"));

                        }
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        private String getVendaSQL(String idLojaCliente, Date dataInicio, Date dataTermino, String nomeTabela) {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);

            return "select\n"
                    + "	v.loja id_loja,\n"
                    + "	v.data,\n"
                    + "	v.codigo_terminal ecf,\n"
                    + "	v.codigo_caixa id_caixa,\n"
                    + "	v.numero_cupom_fiscal numerocupom,\n"
                    + "	v.sequencia,\n"
                    + "	v.codigo_plu_bar ean,\n"
                    + "	v.quantidade_itens quantidade,\n"
                    + "	v.valor_total total_bruto,\n"
                    + "	v.item_cancelado cancelado,\n"
                    + "	v.legenda icms,\n"
                    + "	case substring(v.legenda,1,1)\n"
                    + "	when 'T' then 0\n"
                    + "	when '0' then 0\n"
                    + "	when 'F' then 60\n"
                    + "	when 'I' then 40\n"
                    + "	else 40\n"
                    + "	end as cst,\n"
                    + "	case substring(v.legenda,1,1)\n"
                    + "	when 'T' then v.aliquota\n"
                    + "	when '0' then v.aliquota\n"
                    + "	when 'F' then 0\n"
                    + "	when 'I' then 0\n"
                    + "	else 0\n"
                    + "	end as aliquota\n"
                    + "from\n"
                    + "	" + nomeTabela + " v\n"
                    + "where\n"
                    + "	v.loja = " + idLojaCliente + " and\n"
                    + "	v.data >= '" + strDataInicio + "' and\n"
                    + "	v.data <= '" + strDataTermino + "'\n";
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            this.produtos = new HashMap<>();

            ProgressBar.setStatus("Vendas(Itens)...Carregando produtos");

            try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select distinct\n"
                        + "	coalesce(ean.barcodbar, p.procodplu) ean,\n"
                        + "	p.procodplu id,\n"
                        + "	coalesce(nullif(trim(p.prodescres),''),p.prodescr) descricao,\n"
                        + "	substring(p.proembu, 1,2) embalagem\n"
                        + "from\n"
                        + "	hippro p\n"
                        + "	left join hipbar ean on\n"
                        + "		ean.barcodplu = p.procodplu"
                )) {
                    while (rs.next()) {
                        SmProduto smProduto = new SmProduto(
                                rs.getString("id"),
                                rs.getString("ean"),
                                rs.getString("descricao"),
                                rs.getString("embalagem")
                        );
                        produtos.put(smProduto.ean, smProduto);
                    }
                }
            }

            StringBuilder str = new StringBuilder();

            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_ultimos_meses2"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2017"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2016"));
            str.append("union\n");
            str.append(getVendaSQL(idLojaCliente, dataInicio, dataTermino, "hip_cupom_item_semcript_2015"));
            str.append("order by id_loja, data, ecf, numerocupom");

            this.sql = str.toString();

            LOG.log(Level.FINE, "SQL da venda item: " + sql);
            rst = stm.executeQuery(sql);

            LOG.fine("Quantidade de produtos SM: " + produtos.size());

            ProgressBar.setStatus("Vendas(Itens)...Carregando os itens da venda");
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
    }*/
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String sqlContaPagar
                = "select\n"
                + "	concat(r.ctptipo,'-',r.ctpforn,'-',r.ctpclilj,'-',r.ctpnf,'-',r.ctpserie,'-',r.ctpparc,'-',r.ctploja) id,\n"
                + "	r.ctpforn idfornecedor,\n"
                + "	r.ctpnf numeroDocumento,\n"
                + "	r.ctpdtemiss dataemissao,\n"
                + " r.ctpdtentr dataentrada,\n"
                + "	r.ctpvalor + coalesce(r.ctpjuros, 0) - coalesce(r.ctpdesc, 0) valor,\n"
                + "	r.ctpjuros juros,\n"
                + "	r.ctpdesc desconto,\n"
                + "	r.ctpvalabt abatimento,\n"
                + "	r.ctpobs observacao,\n"
                + "	r.ctpdtvenc vencimento,\n"
                + "	r.ctpparc parcela,\n"
                + "	case when r.ctpdtpagto is null then 0 else 1 end pago\n"
                + "from\n"
                + "	finctp r\n"
                + "where\n"
                + "	r.ctpdtentr >= '" + dateFormat.format(cpDataInicial) + "' and\n"
                + "	r.ctpdtentr <= '" + dateFormat.format(cpDataFinal) + "' and\n"
                + "	r.ctploja = " + getLojaOrigem() + " and\n"
                + "	r.ctpvalor > 0 and\n"
                + "	r.ctpdtpagto is null and\n"
                + "	r.ctptipo = 'F'\n"
                + "order by\n"
                + "	r.ctpdtemiss";

        if (pagarFornecedorBaixado) {
            sqlContaPagar
                    = "select\n"
                    + "	concat(r.ctptipo,'-',r.ctpforn,'-',r.ctpclilj,'-',r.ctpnf,'-',r.ctpserie,'-',r.ctpparc,'-',r.ctploja) id,\n"
                    + "	r.ctpforn idfornecedor,\n"
                    + "	r.ctpnf numeroDocumento,\n"
                    + "	r.ctpdtemiss dataemissao,\n"
                    + " r.ctpdtentr dataentrada,\n"
                    + "	r.ctpvalor + coalesce(r.ctpjuros, 0) - coalesce(r.ctpdesc, 0) valor,\n"
                    + "	r.ctpjuros juros,\n"
                    + "	r.ctpdesc desconto,\n"
                    + "	r.ctpvalabt abatimento,\n"
                    + "	r.ctpobs observacao,\n"
                    + "	r.ctpdtvenc vencimento,\n"
                    + "	r.ctpparc parcela,\n"
                    + "	case when r.ctpdtpagto is null then 0 else 1 end pago,\n"
                    + " r.ctpdtpagto databaixa,\n"
                    + "	r.ctpvalorpg valorpago\n"
                    + "from\n"
                    + "	finctp r\n"
                    + "where\n"
                    + "	r.ctpdtentr >= '" + dateFormat.format(cpDataInicial) + "' and\n"
                    + "	r.ctpdtentr <= '" + dateFormat.format(cpDataFinal) + "' and\n"
                    + "	r.ctploja = " + getLojaOrigem() + " and\n"
                    + "	r.ctpvalor > 0 and\n"
                    + "	r.ctpdtpagto is not null and\n"
                    + "	r.ctptipo = 'F'\n"
                    + "order by\n"
                    + "	r.ctpdtemiss";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sqlContaPagar)) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));

                    imp.setObservacao("PARCELA " + rst.getString("parcela") + " OBS " + rst.getString("observacao"));
                    imp.setValor(rst.getDouble("valor"));
                    //imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));

                    if (pagarFornecedorBaixado) {
                        imp.addVencimento(
                                rst.getDate("vencimento"),
                                rst.getDouble("valorpago"),
                                rst.getDate("databaixa")).
                                setObservacao(rst.getString("observacao") + " - PAGO");
                    } else {
                        imp.addVencimento(
                                rst.getDate("vencimento"),
                                imp.getValor()).
                                setObservacao(rst.getString("observacao"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " r.grpcodgrp id_produtopai,\n"
                    + " r.grpcodplu id_produtofilho,\n"
                    + " p.prodescr descricao,\n"
                    + " r.grprendim rendimento,\n"
                    + " r.grpqtde quantidade\n"
                    + "from hipgrp r\n"
                    + "join hippro p on p.procodplu = r.grpcodgrp\n"
                    + "where \n"
                    + " grptipo = 'P' "
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id_produtopai"));
                    imp.setIdproduto(rst.getString("id_produtopai"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita(rst.getInt("quantidade"));
                    imp.setQtdembalagemproduto(1);
                    imp.setFator(1);
                    imp.getProdutos().add(rst.getString("id_produtofilho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        String sqlAssociado;

        sqlAssociado
                = "select \n"
                + "  a.grpcodgrp id_produtopai,\n"
                + "  p.prodescr descricao_pai,\n"
                + "  a.grpcodplu id_produtofilho,\n"
                + "  p2.prodescr descricao_filho,\n"
                + "  a.grpqtde qtde,\n"
                + "  case when a.grppercprv >= 100 then (a.grppercprv - 100)\n"
                + "       else a.grppercprv end percentualpreco,\n"
                + "  a.grpcoefcus percentualcusto,\n"
                + "  a.grpcalccus calculacusto\n"
                + "from hipgrp a\n"
                + "join hippro p on p.procodplu = a.grpcodgrp\n"
                + "join hippro p2 on p2.procodplu = a.grpcodplu\n"
                + "where \n"
                + " grploja = " + getLojaOrigem() + " and grptipo = 'S'";

        if (inverteAssociado) {
            sqlAssociado
                    = "select \n"
                    + "  a.grpcodgrp id_produtofilho,\n"
                    + "  p.prodescr descricao_filho,\n"
                    + "  a.grpcodplu id_produtopai,\n"
                    + "  p2.prodescr descricao_pai,\n"
                    + "  a.grpqtde qtde,\n"
                    + "  case when a.grppercprv >= 100 then  (a.grppercprv - 100)\n"
                    + "       else a.grppercprv end percentualpreco,\n"
                    + "  a.grpcoefcus percentualcusto,\n"
                    + "  a.grpcalccus calculacusto\n"
                    + "from hipgrp a\n"
                    + "join hippro p on p.procodplu = a.grpcodgrp\n"
                    + "join hippro p2 on p2.procodplu = a.grpcodplu\n"
                    + "where \n"
                    + " grptipo = 'S'";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(sqlAssociado)) {
                while (rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setImpIdProduto(rs.getString("id_produtopai"));
                    imp.setDescricaoAssociado(rs.getString("descricao_pai"));
                    imp.setQtdEmbalagem(rs.getInt("qtde"));
                    imp.setImpIdProdutoItem(rs.getString("id_produtofilho"));
                    imp.setDescricaoAssociadoItem(rs.getString("descricao_filho"));
                    imp.setPercentualPreco(rs.getDouble("percentualpreco"));
                    imp.setPercentualPreco(rs.getDouble("percentualcusto"));

                    if (inverteAssociado) {
                        imp.setImpIdProduto(rs.getString("id_produtopai"));
                        imp.setDescricaoAssociado(rs.getString("descricao_pai"));
                        imp.setQtdEmbalagem(rs.getInt("qtde"));
                        imp.setImpIdProdutoItem(rs.getString("id_produtofilho"));
                        imp.setDescricaoAssociadoItem(rs.getString("descricao_filho"));
                        imp.setPercentualPreco(rs.getDouble("percentualpreco"));
                        imp.setPercentualcustoestoque(rs.getDouble("percentualcusto"));
                        imp.setAplicaEstoque(false);
                        imp.setAplicaCusto(true);
                    }

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
        return new HipcomDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new HipcomDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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
                        String id = rst.getString("id");
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
                        next.setSubTotalImpressora(rst.getDouble("valor"));

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
                    = "select distinct\n"
                    + " concat(pdv.movcup,'.',pdv.movdata,'.',ccf.ccfloja,'.',ccf.ccfcxa) id,\n"
                    + " ccf.ccfcup numerocupom,\n"
                    + " ccf.ccfdata data,\n"
                    + " pdv.movhora hora,\n"
                    + " ccf.ccfcxa ecf,\n"
                    + " sum(ccf.ccfvlr) valor \n"
                    + "from hipccf ccf\n"
                    + " join pdvmov pdv on ccf.ccfcup = pdv.movcup \n"
                    + " and ccf.ccfdata = pdv.movdata and pdv.movcxa = ccf.ccfcxa and pdv.movtipo = 'F'\n"
                    + "where \n"
                    + " ccf.ccfdata between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + " and ccf.ccfloja = " + idLojaCliente + "\n"
                    + " group by 1,2,3,4,5";

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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id"));
                        next.setSequencia(rst.getInt("seq"));
                        next.setProduto(rst.getString("produtoid"));
                        //next.setProduto(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("qtde"));
                        next.setPrecoVenda(rst.getDouble("valorunitario"));
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
                    = "select distinct\n"
                    + " vi.movid id,\n"
                    + " vi.movcup numerocupom,\n"
                    + " vi.movdescr descricao,\n"
                    + " vi.movcodint produtoid,\n"
                    + " trim(vi.movplu) ean,\n"
                    + " vi.movqtd qtde,\n"
                    + " vi.movvlr valorunitario,\n"
                    + " cast((vi.movqtd * vi.movvlr) as decimal(10,2)) valor,\n"
                    + " vi.movdata data,\n"
                    + " vi.movhora hora,\n"
                    + " vi.movcxa ecf,\n"
                    + " (vi.movseq-1) seq,\n"
                    + " upper(vi.movemb) unidade,\n"
                    + " concat(vi.movcup,'.',vi.movdata,'.',ccf.ccfloja,'.',ccf.ccfcxa) id_venda,\n"
                    + "case when vi.movcanc = 'S' then 2\n"
                    + "      else 0 end cancelado\n"
                    + "from pdvmov vi\n"
                    + "join hipccf ccf on ccf.ccfcup = vi.movcup and ccf.ccfdata = vi.movdata and vi.movcxa = ccf.ccfcxa\n"
                    + "join hippro p on p.procodplu  = vi.movcodint \n"
                    + "where \n"
                    + "vi.movdata between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "and vi.movtipo = 'C'\n"
                    + "and ccf.ccfloja = " + idLojaCliente + " ;";

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
