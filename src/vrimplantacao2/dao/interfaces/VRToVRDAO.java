package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoPostgres2;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.convenio.transacao.SituacaoTransacaoConveniado;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.enums.TipoVistaPrazo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.OperadorIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.PessoaImp;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class VRToVRDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(VRToVRDAO.class.getName());
    public boolean eanAtacado = false;
    public boolean apenasAtivo = false;
    public boolean apenasProdutoAtivo = false;

    public boolean importarRotativoBaixados = false;
    public boolean importarConveniosBaixados = false;

    public boolean precoVendaSemOferta = false;

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public void setPrecoVendaSemOferta(boolean precoVendaSemOferta) {
        this.precoVendaSemOferta = precoVendaSemOferta;
    }

    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "VR MASTER";
        }
        return "VR MASTER - " + complemento;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.ATACADO,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.ASSOCIADO,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.TROCA,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.DESCONTINUADO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MARGEM_MINIMA,
                    OpcaoProduto.MARGEM_MAXIMA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.FABRICANTE,
                    OpcaoProduto.NUMERO_PARCELA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.NUTRICIONAL,
                    OpcaoProduto.VENDA_PDV,
                    OpcaoProduto.PDV_VENDA,
                    OpcaoProduto.RECEITA_BALANCA,
                    OpcaoProduto.RECEITA
                }
        ));
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	a2.id id,\n"
                    + "	a2.id_produto idproduto,\n"
                    + "   a2.qtdembalagem,\n"
                    + "	p2.descricaocompleta descricaoassociado,\n"
                    + "	a.id_produto idprodutoitem,\n"
                    + "	p.descricaocompleta descricaoassociadoitem,\n"
                    + "	a.aplicacusto,\n"
                    + "	a.aplicaestoque,\n"
                    + "	a.aplicapreco,\n"
                    + "	a.percentualcustoestoque,\n"
                    + "	a.percentualpreco,\n"
                    + "	a.qtdembalagem qtdembalagemitem\n"
                    + "from\n"
                    + "	associadoitem a\n"
                    + "join produto p on a.id_produto = p.id\n"
                    + "join associado a2 on a.id_associado = a2.id\n"
                    + "join produto p2 on a2.id_produto = p2.id")) {
                while (rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rs.getString("idproduto"));
                    imp.setDescricao(rs.getString("descricaoassociado"));
                    imp.setDescricaoProdutoAssociado(rs.getString("descricaoassociadoitem"));
                    imp.setProdutoAssociadoId(rs.getString("idprodutoitem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemItem(rs.getInt("qtdembalagemitem"));
                    imp.setPercentualCusto(rs.getDouble("percentualcustoestoque"));
                    imp.setPercentualPreco(rs.getDouble("percentualpreco"));
                    imp.setAplicaCusto(rs.getBoolean("aplicacusto"));
                    imp.setAplicaEstoque(rs.getBoolean("aplicaestoque"));
                    imp.setAplicaPreco(rs.getBoolean("aplicapreco"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial \n"
                    + "from \n"
                    + "	loja l \n"
                    + "inner join fornecedor f on l.id_fornecedor = f.id where l.id_situacaocadastro = 1\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojasVR() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres2.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial \n"
                    + "from \n"
                    + "	loja l \n"
                    + "inner join fornecedor f on l.id_fornecedor = f.id where l.id_situacaocadastro = 1\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with aliquotasusadas as (\n"
                    + "	select p.id_aliquotadebito id from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotadebitoforaestado from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotadebitoforaestadonf from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotaconsumidor from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotacredito from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotacreditocusto from produtoaliquota p\n"
                    + "	union\n"
                    + "	select p.id_aliquotacreditoforaestado from produtoaliquota p\n"
                    + ")\n"
                    + "select \n"
                    + "	id,\n"
                    + "	descricao,\n"
                    + "	situacaotributaria,\n"
                    + "	porcentagem,\n"
                    + "	reduzido,\n"
                    + "	porcentagemfcp,\n"
                    + "	icmsdesonerado,\n"
                    + "	percentualicmsdesonerado \n"
                    + "from 	\n"
                    + "	aliquota\n"
                    + "where \n"
                    + "	id in (select id from aliquotasusadas)\n"
                    + "order by\n"
                    + "	descricao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("situacaotributaria"),
                            rs.getDouble("porcentagem"),
                            rs.getDouble("reduzido"),
                            rs.getDouble("porcentagemfcp"),
                            rs.getBoolean("icmsdesonerado"),
                            rs.getDouble("percentualicmsdesonerado")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	descricao,\n"
                    + "	id_situacaocadastro\n"
                    + "from\n"
                    + "	familiaproduto\n"
                    + "order by\n"
                    + "	1")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	m.mercadologico1 cod_mercadologico1,\n"
                    + "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and nivel = 1) mercadologico1,\n"
                    + "	m.mercadologico2 cod_mercadologico2,\n"
                    + "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and nivel = 2) mercadologico2,\n"
                    + "	m.mercadologico3 cod_mercadologico3,\n"
                    + "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and nivel = 3) mercadologico3,\n"
                    + "	m.mercadologico4 cod_mercadologico4,\n"
                    + "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and nivel = 4) mercadologico4,\n"
                    + "	m.mercadologico5 cod_mercadologico5,\n"
                    + "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and mercadologico5 = m.mercadologico5 and nivel = 5) mercadologico5\n"
                    + "from\n"
                    + "	mercadologico m\n"
                    + "where \n"
                    + "	nivel = (select valor::integer from public.parametrovalor where id_loja = " + getLojaOrigem() + " and id_parametro = 1)\n"
                    + "order by\n"
                    + "	1,3,5,7,9")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("cod_mercadologico1"));
                    imp.setMerc1Descricao(rs.getString("mercadologico1"));
                    imp.setMerc2ID(rs.getString("cod_mercadologico2"));
                    imp.setMerc2Descricao(rs.getString("mercadologico2"));
                    imp.setMerc3ID(rs.getString("cod_mercadologico3"));
                    imp.setMerc3Descricao(rs.getString("mercadologico3"));
                    imp.setMerc4ID(rs.getString("cod_mercadologico4"));
                    imp.setMerc4Descricao(rs.getString("mercadologico4"));
                    imp.setMerc5ID(rs.getString("cod_mercadologico5"));
                    imp.setMerc5Descricao(rs.getString("mercadologico5"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (eanAtacado) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	pad.codigobarras,\n"
                        + "	pad.desconto,\n"
                        + "	pad.descontodiaanterior,\n"
                        + "	pad.descontodiaseguinte,\n"
                        + "	pad.dataultimodesconto,\n"
                        + "	pa.id_produto,\n"
                        + "     pa.qtdembalagem,\n"
                        + "	t.descricao unidade\n"
                        + "from\n"
                        + "	produtoautomacaodesconto pad\n"
                        + "join produtoautomacao pa on pad.codigobarras = pa.codigobarras\n"
                        + "join tipoembalagem t on pa.id_tipoembalagem = t.id\n"
                        + "where pad.id_loja = " + getLojaOrigem() + "\n"
                        + "order by\n"
                        + "	pa.id_produto"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("id_produto"));
                        imp.setEan(rs.getString("codigobarras"));

                        if (imp.getEan().length() < 7) {
                            imp.setEan("99999" + imp.getEan());
                        }

                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                        result.add(imp);
                    }
                }
            }
        } else {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select \n"
                        + "	p.id,\n"
                        + "	id_produto,\n"
                        + "	codigobarras,\n"
                        + "	qtdembalagem,\n"
                        + "	t.descricao unidade\n"
                        + "from \n"
                        + "	produtoautomacao p \n"
                        + "join tipoembalagem t on p.id_tipoembalagem = t.id")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("id_produto"));
                        imp.setEan(rs.getString("codigobarras"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));

                        result.add(imp);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	pad.codigobarras,\n"
                        + "	pad.desconto,\n"
                        + "	pad.descontodiaanterior,\n"
                        + "	pad.descontodiaseguinte,\n"
                        + "	pad.dataultimodesconto,\n"
                        + "	pa.id_produto,\n"
                        + "	pa.qtdembalagem,\n"
                        + "	pc.precovenda\n"
                        + "from\n"
                        + "	produtoautomacaodesconto pad\n"
                        + "join produtoautomacao pa on pad.codigobarras = pa.codigobarras\n"
                        + "join produtocomplemento pc on pa.id_produto = pc.id_produto and\n"
                        + "	pc.id_loja = pad.id_loja\n"
                        + "where\n"
                        + "	pc.id_loja = " + getLojaOrigem() + "\n"
                        + "order by\n"
                        + "	pa.id_produto"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(rst.getString("codigobarras"));

                        if (imp.getEan().length() < 7) {
                            imp.setEan("99999" + imp.getEan());
                        }

                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPorcentagem(rst.getDouble("desconto"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Versao versao = Versao.createFromConnectionInterface(ConexaoPostgres.getConexao());

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with \n"
                    + "	lj as (select loja.id, f.id_estado from loja join fornecedor f on loja.id_fornecedor = f.id where loja.id = " + getLojaOrigem() + ")\n"
                    + "select\n"
                    + "	p.id,\n"
                    + "	p.datacadastro,\n"
                    + "	ean.codigobarras,\n"
                    + "	p.qtdembalagem qtdembalagemcotacao,\n"
                    + "   emb.descricao embalagemcotacao,\n"
                    + "	ean.qtdembalagem,\n"
                    + "	ean_un.descricao unidade,\n"
                    + "	case when p.id_tipoembalagem = 4 or p.pesavel then 'S' else 'N' end balanca,\n"
                    + "	p.descricaocompleta,\n"
                    + "	p.descricaoreduzida,\n"
                    + "	p.descricaogondola,\n"
                    + "	p.mercadologico1,\n"
                    + "	p.mercadologico2,\n"
                    + "	p.mercadologico3,\n"
                    + "	p.mercadologico4,\n"
                    + "	p.mercadologico5,\n"
                    + "	p.id_familiaproduto,\n"
                    + "	p.pesobruto,\n"
                    + "	p.pesoliquido,\n"
                    + "	vend.estoquemaximo,\n"
                    + "	vend.estoqueminimo,\n"
                    + "	vend.estoque,\n"
                    + "	vend.troca,\n"
                    + "	vend.custosemimposto,\n"
                    + "	vend.custocomimposto,\n"
                    + (versao.menorQue(4, 2, 0) ? " p.validade, \n" : " vend.validade  ,\n")
                    + (precoVendaSemOferta ? "coalesce(o.preconormal, vend.precovenda) precovenda,\n" : "vend.precovenda,\n")
                    + (versao.igualOuMaiorQue(4)
                    ? " 	vend.margem,\n"
                    + " 	vend.margemmaxima,\n"
                    + " 	vend.margemminima,\n"
                    : " 	p.margem,\n")
                    + "	vend.id_situacaocadastro,\n"
                    + "	vend.descontinuado,\n"
                    + "	lpad(p.ncm1::varchar,4,'0') || lpad(p.ncm2::varchar,2,'0') || lpad(p.ncm3::varchar,2,'0') ncm,\n"
                    + "	lpad(cest.cest1::varchar,2,'0') || lpad(cest.cest2::varchar,3,'0') || lpad(cest.cest3::varchar,2,'0') cest,\n"
                    + "	piscofdeb.cst piscofins_cst_debito,\n"
                    + "	piscofcred.cst piscofins_cst_credito,\n"
                    + "	p.tiponaturezareceita piscofins_natureza_receita,\n"
                    + " 	aliq.id_aliquotadebito,\n"
                    + " 	aliq.id_aliquotadebitoforaestado,\n"
                    + " 	aliq.id_aliquotadebitoforaestadonf,\n"
                    + " 	aliq.id_aliquotaconsumidor,\n"
                    + " 	aliq.id_aliquotacredito,\n"
                    + " 	aliq.id_aliquotacreditocusto,\n"
                    + " 	aliq.id_aliquotacreditoforaestado,\n"
                    + "	case when p.sugestaocotacao then 'S' else 'N' end as sugestaocotacao,\n"
                    + "	case when p.sugestaopedido then 'S' else 'N' end as sugestaopedido,\n"
                    + "	pad.desconto atacadodesconto,\n"
                    + "	pf.id id_pautafiscal,\n"
                    + "	p.id_fornecedorfabricante,\n"
                    + "	p.numeroparcela\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	join lj on true\n"
                    + "	left join produtoautomacao ean on\n"
                    + "		ean.id_produto = p.id\n"
                    + "	left join tipoembalagem ean_un on\n"
                    + "		ean_un.id = ean.id_tipoembalagem\n"
                    + "left join tipoembalagem emb on\n"
                    + "		emb.id = p.id_tipoembalagem\n"
                    + "	join produtocomplemento vend on\n"
                    + "		p.id = vend.id_produto and vend.id_loja = lj.id\n"
                    + (precoVendaSemOferta
                            ? "left join oferta o on o.id_loja = vend.id_loja and o.id_produto = vend.id_produto and o.datatermino > now()\n" : "\n")
                    + "	left join cest on\n"
                    + "		cest.id = p.id_cest\n"
                    + "	left join tipopiscofins piscofcred on \n"
                    + "		p.id_tipopiscofinscredito = piscofcred.id\n"
                    + "	left join tipopiscofins piscofdeb on \n"
                    + "		p.id_tipopiscofins = piscofdeb.id\n"
                    + "	join produtoaliquota aliq on \n"
                    + "		p.id = aliq.id_produto and \n"
                    + "		aliq.id_estado = lj.id_estado\n"
                    + "	left join produtoautomacaodesconto pad on\n"
                    + "		pad.codigobarras = ean.codigobarras and\n"
                    + "		pad.id_loja = lj.id\n"
                    + "	left join pautafiscal pf on\n"
                    + "		p.ncm1 = pf.ncm1 and\n"
                    + "		p.ncm2 = pf.ncm2 and\n"
                    + "		p.ncm3 = pf.ncm3 and\n"
                    + "		aliq.excecao = pf.excecao\n"
                    + (apenasProdutoAtivo == true ? " where vend.id_situacaocadastro = 1" : "")
                    + "order by\n"
                    + "	p.id"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rs.getString("embalagemcotacao"));
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.setCodMercadologico4(rs.getString("mercadologico4"));
                    imp.setCodMercadologico5(rs.getString("mercadologico5"));
                    imp.setIdFamiliaProduto(rs.getString("id_familiaproduto"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setTroca(rs.getDouble("troca"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    if (versao.igualOuMaiorQue(4)) {
                        imp.setMargemMaxima(rs.getDouble("margemmaxima"));
                        imp.setMargemMaxima(rs.getDouble("margemminima"));
                    }
                    imp.setAtacadoPorcentagem(rs.getDouble("atacadodesconto"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDescontinuado(rs.getBoolean("descontinuado"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofins_cst_credito"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_cst_debito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofins_natureza_receita"));
                    imp.setSugestaoCotacao("S".equals(rs.getString("sugestaocotacao")));
                    imp.setSugestaoPedido("S".equals(rs.getString("sugestaopedido")));
                    imp.setPautaFiscalId(rs.getString("id_pautafiscal"));
                    imp.setIcmsDebitoId(rs.getString("id_aliquotadebito"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_aliquotadebitoforaestado"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_aliquotadebitoforaestadonf"));
                    imp.setIcmsConsumidorId(rs.getString("id_aliquotaconsumidor"));
                    imp.setIcmsCreditoId(rs.getString("id_aliquotacredito"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_aliquotacreditoforaestado"));
                    imp.setFornecedorFabricante(rs.getString("id_fornecedorfabricante"));
                    imp.setNumeroparcela(rs.getInt("numeroparcela"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	f.id,\n"
                    + "	f.razaosocial razao,\n"
                    + "	f.nomefantasia fantasia,\n"
                    + "	f.cnpj cnpj_cpf,\n"
                    + "	f.inscricaoestadual ie_rg,\n"
                    + "	f.inscricaomunicipal insc_municipal,\n"
                    + "	f.inscricaosuframa suframa,\n"
                    + "	f.bloqueado,\n"
                    + "	f.id_situacaocadastro,\n"
                    + " f.id_tipopagamento,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.id_municipio ibge_municipio,\n"
                    + "	m.descricao municipio,\n"
                    + "	f.id_estado ibge_uf,\n"
                    + "	e.sigla uf,\n"
                    + "	f.cep,\n"
                    + "	f.enderecocobranca cob_endereco,\n"
                    + "	f.numerocobranca cob_numero,\n"
                    + "	f.complementocobranca cob_complemento,\n"
                    + "	f.bairrocobranca cob_bairro,\n"
                    + "	f.id_municipiocobranca cob_ibge_municipio,\n"
                    + "	cm.descricao cob_municipio,\n"
                    + "	f.id_estadocobranca cob_ibge_uf,\n"
                    + "	ce.sigla cob_uf,\n"
                    + "	f.cepcobranca cob_cep,		\n"
                    + "	f.telefone tel_principal,\n"
                    + "	f.pedidominimoqtd qtd_minima_pedido,\n"
                    + "	f.pedidominimovalor valor_minimo_pedido,\n"
                    + "	f.datacadastro,\n"
                    + "	f.observacao\n"
                    + "from \n"
                    + "	fornecedor f\n"
                    + "	left join municipio m on f.id_municipio = m.id\n"
                    + "	left join estado e on f.id_estado = e.id\n"
                    + "	left join municipio cm on f.id_municipiocobranca = cm.id\n"
                    + "	left join estado ce on f.id_estadocobranca = ce.id\n"
                    + "order by \n"
                    + "	id")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setInsc_municipal(rs.getString("insc_municipal"));
                    imp.setSuframa(rs.getString("suframa"));
                    imp.setAtivo(rs.getInt("id_situacaocadastro") == 1);
                    imp.setTipoPagamento(rs.getInt("id_tipopagamento") == 0 ? TipoPagamento.CARTEIRA : TipoPagamento.BOLETO_BANCARIO);
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_uf(rs.getInt("ibge_uf"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCob_endereco(rs.getString("cob_endereco"));
                    imp.setCob_numero(rs.getString("cob_numero"));
                    imp.setCob_complemento(rs.getString("cob_complemento"));
                    imp.setCob_bairro(rs.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rs.getInt("cob_ibge_municipio"));
                    imp.setCob_municipio(rs.getString("cob_municipio"));
                    imp.setCob_uf(rs.getString("cob_uf"));
                    imp.setCob_cep(rs.getString("cob_cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setQtd_minima_pedido(rs.getInt("qtd_minima_pedido"));
                    imp.setValor_minimo_pedido(rs.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));

                    getContatoFornecedor(imp);
                    getDivisaoFornecedor(imp);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pf.id,\n"
                    + "	lpad(pf.ncm1::varchar, 4, '0') || lpad(pf.ncm2::varchar, 2, '0') || lpad(pf.ncm3::varchar, 2, '0') ncm,\n"
                    + "	pf.excecao,\n"
                    + "	uf.sigla uf,\n"
                    + "	pf.iva,\n"
                    + "	pf.tipoiva,\n"
                    + "	pf.ivaajustado,\n"
                    + "	pf.icmsrecolhidoantecipadamente,\n"
                    + "	ac.situacaotributaria credito_cst,\n"
                    + "	ac.porcentagem credito_aliquota,\n"
                    + "	ac.reduzido credito_reduzido,\n"
                    + "	ad.situacaotributaria debito_cst,\n"
                    + "	ad.porcentagem debito_aliquota,\n"
                    + "	ad.reduzido debito_reduzido,\n"
                    + "	adfe.situacaotributaria debito_foraest_cst,\n"
                    + "	adfe.porcentagem debito_foraest_aliquota,\n"
                    + "	adfe.reduzido debito_foraest_reduzido,\n"
                    + "	acfe.situacaotributaria credito_foraest_cst,\n"
                    + "	acfe.porcentagem credito_foraest_aliquota,\n"
                    + "	acfe.reduzido credito_foraest_reduzido\n"
                    + "from\n"
                    + "	pautafiscal pf\n"
                    + "	join estado uf on\n"
                    + "		pf.id_estado = uf.id\n"
                    + "	left join aliquota ac on\n"
                    + "		pf.id_aliquotacredito = ac.id\n"
                    + "	left join aliquota ad on\n"
                    + "		pf.id_aliquotadebito = ad.id\n"
                    + "	left join aliquota adfe on\n"
                    + "		pf.id_aliquotadebitoforaestado = adfe.id\n"
                    + "	left join aliquota acfe on\n"
                    + "		pf.id_aliquotacreditoforaestado = acfe.id\n"
                    + "order by\n"
                    + "	pf.id")) {
                while (rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIva(rs.getDouble("iva"));
                    imp.setTipoIva(rs.getInt("tipoiva") == 0 ? TipoIva.PERCENTUAL : TipoIva.VALOR);
                    imp.setIvaAjustado(rs.getDouble("ivaajustado"));
                    imp.setIcmsRecolhidoAntecipadamente(rs.getBoolean("icmsrecolhidoantecipadamente"));
                    imp.setAliquotaCredito(rs.getInt("credito_cst"), rs.getDouble("credito_aliquota"), rs.getDouble("credito_reduzido"));
                    imp.setAliquotaDebito(rs.getInt("debito_cst"), rs.getDouble("debito_aliquota"), rs.getDouble("debito_reduzido"));
                    imp.setAliquotaCreditoForaEstado(rs.getInt("credito_foraest_cst"), rs.getDouble("credito_foraest_aliquota"), rs.getDouble("debito_foraest_reduzido"));
                    imp.setAliquotaDebitoForaEstado(rs.getInt("debito_foraest_cst"), rs.getDouble("debito_foraest_aliquota"), rs.getDouble("credito_foraest_reduzido"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	pf.codigoexterno,\n"
                    + "	pf.qtdembalagem,\n"
                    + "	pf.dataalteracao,\n"
                    + "	pf.pesoembalagem\n"
                    + "from\n"
                    + "	produtofornecedor pf\n"
                    + "order by\n"
                    + "	id_fornecedor,\n"
                    + "	id_produto,\n"
                    + "	codigoexterno")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setPesoEmbalagem(rs.getDouble("pesoembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void getContatoFornecedor(FornecedorIMP imp) throws SQLException {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " fc.id,\n"
                    + "	telefone,\n"
                    + " nome,\n"
                    + "	celular,\n"
                    + "	email,\n"
                    + "	tp.id tipo\n"
                    + "from \n"
                    + "	fornecedorcontato fc\n"
                    + "join tipocontato tp on fc.id_tipocontato = tp.id\n"
                    + "where 	\n"
                    + "	fc.id_fornecedor = " + imp.getImportId())) {
                while (rs.next()) {

                    int id = rs.getInt("tipo");
                    imp.addContato(rs.getString("id"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("celular"),
                            id == 0 ? TipoContato.COMERCIAL
                                    : id == 1 ? TipoContato.FINANCEIRO
                                            : id == 2 ? TipoContato.FISCAL : TipoContato.NFE,
                            rs.getString("email"));
                }
            }
        }
    }

    private void getDivisaoFornecedor(FornecedorIMP imp) throws SQLException {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id,\n"
                    + "	id_divisaofornecedor,\n"
                    + "	prazoentrega,\n"
                    + "	prazovisita,\n"
                    + "	prazoseguranca\n"
                    + "from \n"
                    + "	fornecedorprazo\n"
                    + "where 	\n"
                    + "	id_fornecedor = " + imp.getImportId() + " and\n"
                    + "	id_loja = " + getLojaOrigem())) {
                while (rs.next()) {
                    imp.addDivisao(rs.getString("id"), rs.getInt("prazovisita"), rs.getInt("prazoentrega"), rs.getInt("prazoseguranca"));
                }
            }
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.id id,\n"
                    + "	c.cnpj,\n"
                    + "	c.inscricaoestadual,\n"
                    + "	c.orgaoemissor,\n"
                    + "	c.nome razao,\n"
                    + "	c.nome fantasia,\n"
                    + "	c.id_situacaocadastro,\n"
                    + "	case when c.bloqueado then 'S' else 'N' end bloqueado,\n"
                    + "	c.datarestricao databloqueio,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.id_municipio municipioIBGE,\n"
                    + "	mun.descricao municipio,\n"
                    + "	c.id_estado ufIBGE,\n"
                    + "	est.sigla uf,\n"
                    + "	c.cep,\n"
                    + " civil.id id_estadocivil,\n"
                    + "	coalesce(substring(civil.descricao,1,3), 'NAO') estadocivil,\n"
                    + "	c.datanascimento,\n"
                    + "	c.datacadastro,\n"
                    + "	case c.sexo when 0 then 'F' else 'M' end sexo,\n"
                    + "	c.empresa,\n"
                    + "	c.enderecoempresa empresaendereco,\n"
                    + "	c.numeroempresa empresanumero,\n"
                    + "	c.complementoempresa empresacomplemento,\n"
                    + "	c.bairroempresa empresabairro,\n"
                    + "	c.id_municipioempresa empresamunicipioIBGE,\n"
                    + "	mun_emp.descricao empresamunicipio,\n"
                    + "	c.id_estadoempresa empresaufIBGE,\n"
                    + "	est_emp.sigla empresauf,\n"
                    + "	c.cepempresa empresacep,\n"
                    + "	c.telefoneempresa empresatelefone,\n"
                    + "	c.dataadmissao,\n"
                    + "	c.cargo,\n"
                    + "	c.salario,\n"
                    + "	c.valorlimite,\n"
                    + "	c.nomeconjuge,\n"
                    + "	c.nomepai,\n"
                    + "	c.nomemae,\n"
                    + "	regexp_replace(c.observacao2,'[\\\\n\\\\r]+',' ','g') observacao,\n"
                    + "	c.vencimentocreditorotativo diavencimento,\n"
                    + "	case when c.permitecreditorotativo then 'S' else 'N' end permitecreditorotativo,\n"
                    + "	case when c.permitecheque then 'S' else 'N' end permitecheque,\n"
                    + "	c.telefone,\n"
                    + "	c.celular,\n"
                    + "	c.email,\n"
                    + "	c.telefone cobrancaTelefone,\n"
                    + "	0 prazopagamento,\n"
                    + "	c.endereco cobrancaendereco,\n"
                    + "	c.numero cobrancanumero,\n"
                    + "	c.complemento cobrancacomplemento,\n"
                    + "	c.bairro cobrancabairro,\n"
                    + "	c.id_municipio cobrancamunicipioibge,\n"
                    + "	mun.descricao cobrancamunicipio,\n"
                    + "	c.id_estado cobrancaufibge,\n"
                    + "	est.sigla cobrancauf,\n"
                    + "	c.cep cobrancacep,\n"
                    + "	'NENHUM'::varchar tipoorgaopublico,\n"
                    + "	0 limitecompra,\n"
                    + "	''::varchar inscricaomunicipal,\n"
                    + "	'NAO CONTRIBUINTE'::varchar tipoindicadorie\n"
                    + "from \n"
                    + "	clientepreferencial c\n"
                    + "	left join municipio mun on\n"
                    + "		c.id_municipio = mun.id\n"
                    + "	left join estado est on\n"
                    + "		c.id_estado = est.id\n"
                    + "	left join municipio mun_emp on\n"
                    + "		c.id_municipioempresa = mun_emp.id\n"
                    + "	left join estado est_emp on\n"
                    + "		c.id_estadoempresa = est_emp.id\n"
                    + "	left join tipoestadocivil civil on\n"
                    + "		c.id_tipoestadocivil = civil.id\n"
                    + "order by\n"
                    + "	c.id")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rs.getString("orgaoemissor"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getInt("id_situacaocadastro") == 1);
                    imp.setBloqueado("S".equals(rs.getString("bloqueado")));
                    imp.setDataBloqueio(rs.getDate("databloqueio"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("municipioibge"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUfIBGE(rs.getInt("ufibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEstadoCivil(rs.getInt("id_estadocivil"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                    imp.setEmpresaNumero(rs.getString("empresanumero"));
                    imp.setEmpresaComplemento(rs.getString("empresacomplemento"));
                    imp.setEmpresaBairro(rs.getString("empresabairro"));
                    imp.setEmpresaMunicipioIBGE(rs.getInt("empresamunicipioibge"));
                    imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                    imp.setEmpresaUfIBGE(rs.getInt("empresaufibge"));
                    imp.setEmpresaUf(rs.getString("empresauf"));
                    imp.setEmpresaCep(rs.getString("empresacep"));
                    imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                    imp.setDataAdmissao(rs.getDate("dataadmissao"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setEmail(rs.getString("email"));
                    imp.setCobrancaTelefone(rs.getString("cobrancatelefone"));
                    imp.setPrazoPagamento(rs.getInt("prazopagamento"));
                    imp.setCobrancaEndereco(rs.getString("cobrancaendereco"));
                    imp.setCobrancaNumero(rs.getString("cobrancanumero"));
                    imp.setCobrancaComplemento(rs.getString("cobrancacomplemento"));
                    imp.setCobrancaBairro(rs.getString("cobrancabairro"));
                    imp.setCobrancaMunicipioIBGE(rs.getInt("cobrancamunicipioibge"));
                    imp.setCobrancaMunicipio(rs.getString("cobrancamunicipio"));
                    imp.setCobrancaUfIBGE(rs.getInt("cobrancaufibge"));
                    imp.setCobrancaUf(rs.getString("cobrancauf"));
                    imp.setCobrancaCep(rs.getString("cobrancacep"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                    imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);

                    getContatoCliente(imp);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void getContatoCliente(ClienteIMP imp) throws SQLException {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cp.id,\n"
                    + "	nome,\n"
                    + "	telefone,\n"
                    + "	celular,\n"
                    + "	tc.descricao contato\n"
                    + "from \n"
                    + "	clientepreferencialcontato cp\n"
                    + "join tipocontato tc on cp.id_tipocontato = tc.id \n"
                    + "where 	\n"
                    + "	cp.id_clientepreferencial = " + imp.getId())) {
                while (rs.next()) {
                    imp.addContato(rs.getString("id"), rs.getString("nome"), rs.getString("telefone"), rs.getString("celular"), null);
                }
            }
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            Map<String, List<CreditoRotativoItemIMP>> pagamentos = new HashMap<>();
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	ci.id,\n"
                    + "	ci.id_recebercreditorotativo,\n"
                    + "	ci.valor,\n"
                    + "	ci.valordesconto,\n"
                    + "	ci.valormulta,\n"
                    + "	ci.databaixa,\n"
                    + "	ci.observacao,\n"
                    + "	ci.id_banco,\n"
                    + "	ci.agencia,\n"
                    + "	ci.conta,\n"
                    + "	ci.id_tiporecebimento\n"
                    + "from\n"
                    + "	recebercreditorotativoitem ci\n"
                    + "	join recebercreditorotativo c on\n"
                    + "     ci.id_recebercreditorotativo = c.id and\n"
                    + (!importarRotativoBaixados ? "     c.id_situacaorecebercreditorotativo = 0 and\n" : "")
                    + "     c.id_loja = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "	ci.id"
            )) {
                while (rs.next()) {

                    List<CreditoRotativoItemIMP> list = pagamentos.get(rs.getString("id_recebercreditorotativo"));
                    if (list == null) {
                        list = new ArrayList<>();
                        pagamentos.put(rs.getString("id_recebercreditorotativo"), list);
                    }

                    CreditoRotativoItemIMP i = new CreditoRotativoItemIMP();

                    i.setId(rs.getString("id"));
                    i.setDataPagamento(rs.getDate("databaixa"));
                    i.setDesconto(rs.getDouble("valordesconto"));
                    i.setMulta(rs.getDouble("valormulta"));
                    i.setObservacao("BC " + rs.getInt("id_banco") + " " + rs.getString("observacao"));
                    i.setValor(rs.getDouble("valor"));
                    //i.setId_banco(rs.getInt("id_banco"));
                    i.setAgencia(rs.getString("agencia"));
                    i.setConta(rs.getString("conta"));
                    i.setId_tiporecebimento(rs.getInt("id_tiporecebimento"));

                    list.add(i);
                }
            }
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	r.id,\n"
                    + "	c.cnpj,\n"
                    + "	r.dataemissao,\n"
                    + "	r.datavencimento,\n"
                    + "	r.ecf,\n"
                    + "	r.id_clientepreferencial idcliente,\n"
                    + "	r.valorjuros juros,\n"
                    + "	r.valormulta multa,\n"
                    + "	r.numerocupom cupom,\n"
                    + "	r.observacao,\n"
                    + "	r.parcela,\n"
                    + "	r.valor\n"
                    + "from\n"
                    + "	recebercreditorotativo r\n"
                    + "	join clientepreferencial c on\n"
                    + "     r.id_clientepreferencial = c.id\n"
                    + "where\n"
                    + "	id_loja = " + getLojaOrigem() + "\n"
                    + (!importarRotativoBaixados ? "	and id_situacaorecebercreditorotativo = 0\n" : "")
                    + "order by\n"
                    + "	r.id")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setMulta(rs.getDouble("multa"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valor"));

                    List<CreditoRotativoItemIMP> pags = pagamentos.get(imp.getId());
                    if (pags != null) {
                        for (CreditoRotativoItemIMP pg : pags) {
                            pg.setCreditoRotativo(imp);
                            imp.getPagamentos().add(pg);
                        }
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoPostgres.getConexao().createStatement(); ResultSet rs = st.executeQuery(
                "select\n"
                + "	c.id,\n"
                + "	c.cpf,\n"
                + "	c.numerocheque,\n"
                + "	c.id_banco,\n"
                + "	c.agencia,\n"
                + "	c.conta,\n"
                + "	c.data,\n"
                + "	c.datadeposito,\n"
                + "	c.numerocupom,\n"
                + "	c.ecf,\n"
                + "	c.valor,\n"
                + "	c.rg,\n"
                + "	c.telefone,\n"
                + "	c.nome,\n"
                + "	c.observacao,\n"
                + "	c.id_situacaorecebercheque,\n"
                + "	c.cmc7,\n"
                + "	c.id_tipoalinea,\n"
                + "	c.valorjuros,\n"
                + "	c.valoracrescimo,\n"
                + "	c.id_tipolocalcobranca,\n"
                + "	c.datahoraalteracao,\n"
                + "	c.id_tipovistaprazo\n"
                + "from\n"
                + "	recebercheque c\n"
                + "where\n"
                + "	c.id_loja = " + getLojaOrigem() + "\n"
                + "	and c.id_situacaorecebercheque = 0\n"
                + "order by\n"
                + "	c.id"
        )) {
            while (rs.next()) {
                ChequeIMP imp = new ChequeIMP();

                imp.setId(rs.getString("id"));
                imp.setCpf(rs.getString("cpf"));
                imp.setNumeroCheque(rs.getString("numerocheque"));
                imp.setBanco(rs.getInt("id_banco"));
                imp.setAgencia(rs.getString("agencia"));
                imp.setConta(rs.getString("conta"));
                imp.setDate(rs.getDate("data"));
                imp.setDataDeposito(rs.getDate("datadeposito"));
                imp.setNumeroCupom(rs.getString("numerocupom"));
                imp.setEcf(rs.getString("ecf"));
                imp.setValor(rs.getDouble("valor"));
                imp.setRg(rs.getString("rg"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setNome(rs.getString("nome"));
                imp.setObservacao(rs.getString("observacao"));
                imp.setSituacaoCheque(SituacaoCheque.getById(rs.getInt("id_situacaorecebercheque")));
                imp.setCmc7(rs.getString("cmc7"));
                imp.setAlinea(rs.getInt("id_tipoalinea"));
                imp.setValorJuros(rs.getDouble("valorjuros"));
                imp.setValorAcrescimo(rs.getDouble("valoracrescimo"));
                //imp.setIdLocalCobranca(rs.getInt("id_tipolocalcobranca"));
                imp.setDataHoraAlteracao(rs.getTimestamp("datahoraalteracao"));
                imp.setVistaPrazo(TipoVistaPrazo.getById(rs.getInt("id_tipovistaprazo")));

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoPostgres.getConexao().createStatement(); ResultSet rs = st.executeQuery(
                "select\n"
                + "	e.id,\n"
                + "	e.razaosocial,\n"
                + "	e.cnpj,\n"
                + "	e.inscricaoestadual,\n"
                + "	e.endereco,\n"
                + "	e.numero,\n"
                + "	e.complemento,\n"
                + "	e.bairro,\n"
                + "	e.id_municipio,\n"
                + "	e.cep,\n"
                + "	e.telefone,\n"
                + "	e.datainicio,\n"
                + "	e.datatermino,\n"
                + "	e.id_situacaocadastro,\n"
                + "	e.percentualdesconto,\n"
                + "	e.renovacaoautomatica,\n"
                + "	e.diapagamento,\n"
                + "	e.bloqueado,\n"
                + "	e.databloqueio,\n"
                + "	e.diainiciorenovacao,\n"
                + "	e.diaterminorenovacao,\n"
                + "	e.observacao\n"
                + "from\n"
                + "	empresa e\n"
                + "order by\n"
                + "	e.id"
        )) {
            while (rs.next()) {
                ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                imp.setId(rs.getString("id"));
                imp.setRazao(rs.getString("razaosocial"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoEstadual(rs.getString("inscricaoestadual"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setIbgeMunicipio(rs.getInt("id_municipio"));
                imp.setCep(rs.getString("cep"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setDataInicio(rs.getDate("datainicio"));
                imp.setDataTermino(rs.getDate("datatermino"));
                imp.setSituacaoCadastro(SituacaoCadastro.getById(rs.getInt("id_situacaocadastro")));
                imp.setDesconto(rs.getDouble("percentualdesconto"));
                imp.setRenovacaoAutomatica(rs.getBoolean("renovacaoautomatica"));
                imp.setDiaPagamento(rs.getInt("diapagamento"));
                imp.setBloqueado(rs.getBoolean("bloqueado"));
                imp.setDataBloqueio(rs.getDate("databloqueio"));
                imp.setDiaInicioRenovacao(rs.getInt("diainiciorenovacao"));
                imp.setDiaFimRenovacao(rs.getInt("diaterminorenovacao"));
                imp.setObservacoes(rs.getString("observacao"));

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoPostgres.getConexao().createStatement(); ResultSet rs = st.executeQuery(
                "select\n"
                + "	c.id,\n"
                + "	c.nome,\n"
                + "	c.id_empresa,\n"
                + "	c.bloqueado,\n"
                + "	c.id_situacaocadastro,\n"
                + "	c.senha,\n"
                + "	c.cnpj,\n"
                + "	c.observacao,\n"
                + "	c.datavalidadecartao,\n"
                + "	c.datadesbloqueio,\n"
                + "	c.visualizasaldo,\n"
                + "	c.databloqueio,\n"
                + "	c.id_loja\n"
                + "from\n"
                + "	conveniado c\n"
                + "order by\n"
                + "	c.id"
        )) {
            while (rs.next()) {
                ConveniadoIMP imp = new ConveniadoIMP();

                imp.setId(rs.getString("id"));
                imp.setNome(rs.getString("nome"));
                imp.setIdEmpresa(rs.getString("id_empresa"));
                imp.setBloqueado(rs.getBoolean("bloqueado"));
                imp.setSituacaoCadastro(SituacaoCadastro.getById(rs.getInt("id_situacaocadastro")));
                imp.setSenha(rs.getInt("senha"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setObservacao(rs.getString("observacao"));
                imp.setValidadeCartao(rs.getDate("datavalidadecartao"));
                imp.setDataDesbloqueio(rs.getDate("datadesbloqueio"));
                imp.setVisualizaSaldo(rs.getBoolean("visualizasaldo"));
                imp.setDataBloqueio(rs.getDate("databloqueio"));
                imp.setLojaCadastro(rs.getInt("id_loja"));

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + " t.id,\n"
                    + " t.id_conveniado,\n"
                    + " t.ecf,\n"
                    + " t.numerocupom,\n"
                    + " t.datahora,\n"
                    + " t.valor,\n"
                    + " t.id_situacaotransacaoconveniado,\n"
                    + " t.datamovimento,\n"
                    + " t.finalizado,\n"
                    + " t.observacao\n"
                    + "from\n"
                    + " conveniadotransacao t\n"
                    + "where\n"
                    + " t.id_loja = " + getLojaOrigem() + "\n"
                    //+ (!importarConveniosBaixados ? " and t.id_situacaotransacaoconveniado = 1\n" : "")
                    + "order by\n"
                    + " t.id"
            )) {
                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdConveniado(rs.getString("id_conveniado"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setDataHora(rs.getTimestamp("datahora"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setSituacaoTransacaoConveniado(SituacaoTransacaoConveniado.getById(rs.getInt("id_situacaotransacaoconveniado")));
                    imp.setDataMovimento(rs.getDate("datamovimento"));
                    imp.setFinalizado(rs.getBoolean("finalizado"));
                    imp.setObservacao(rs.getString("observacao"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	p.id,\n"
                    + "	p.id_fornecedor,\n"
                    + "	p.numerodocumento,\n"
                    + " 	p.id_tipoentrada,\n"
                    + "	p.dataentrada,\n"
                    + "	p.dataemissao\n"
                    + "from \n"
                    + "	pagarfornecedor p \n"
                    + "    join pagarfornecedorparcela pp\n"
                    + "       on p.id = pp.id_pagarfornecedor\n"
                    + "    join tipoentrada t2 on\n"
                    + "       p.id_tipoentrada = t2.id\n"
                    + "where\n"
                    + "	pp.id_situacaopagarfornecedorparcela = 0 and \n"
                    + "	p.id_loja = " + getLojaOrigem())) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));
                    //imp.setIdTipoEntradaVR(rs.getInt("id_tipoentrada"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    //imp.setValor(rs.getDouble("valor"));

                    incluirVencimentos(imp);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void incluirVencimentos(ContaPagarIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pp.id,\n"
                    + "	pp.id_pagarfornecedor,\n"
                    + "	pp.numeroparcela,\n"
                    + "	pp.datavencimento,\n"
                    + "	pp.datapagamento,\n"
                    + "	pp.valor,\n"
                    + "	pp.observacao,\n"
                    + "	pp.id_banco,\n"
                    + "	pp.agencia,\n"
                    + "	pp.conta,\n"
                    + "	pp.conferido,\n"
                    + "	'TE '||t2.id || ' - ' || t2.descricao tipoentrada\n"
                    + "from \n"
                    + "	pagarfornecedor p \n"
                    + "    join pagarfornecedorparcela pp\n"
                    + "       on p.id = pp.id_pagarfornecedor\n"
                    + "    join tipoentrada t2 on\n"
                    + "       p.id_tipoentrada = t2.id\n"
                    + "where\n"
                    + "	pp.id_situacaopagarfornecedorparcela = 0 and \n"
                    + "	pp.id_pagarfornecedor = " + imp.getId()
            )) {
                while (rs.next()) {
                    ContaPagarVencimentoIMP i = imp.addVencimento(rs.getDate("datavencimento"), rs.getDouble("valor"));
                    i.setId(rs.getString("id"));
                    i.setNumeroParcela(rs.getInt("numeroparcela"));
                    i.setDataPagamento(rs.getDate("datavencimento"));
                    i.setValor(rs.getDouble("valor"));
                    i.setObservacao(
                            String.format(
                                    "BANCO (%d) TIPO ENTRADA (%s) OBS (%s)",
                                    rs.getInt("id_banco"),
                                    rs.getString("tipoentrada"),
                                    rs.getString("observacao")
                            )
                    );
                    i.setAgencia(rs.getString("agencia"));
                    i.setConta(rs.getString("conta"));
                    i.setConferido(rs.getBoolean("conferido"));
                }
            }
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	*\n"
                    + "from \n"
                    + "	oferta o \n"
                    + "where \n"
                    + "	id_loja = " + getLojaOrigem() + " and \n"
                    + "	datatermino >= now()")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setSituacaoOferta(rs.getInt("id_situacaooferta") == 1 ? SituacaoOferta.ATIVO : SituacaoOferta.CANCELADO);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OperadorIMP> getOperadores() throws Exception {
        List<OperadorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	*\n"
                    + "from\n"
                    + "	pdv.operador\n"
                    + "where\n"
                    + "	id_loja = " + getLojaOrigem())) {
                while (rs.next()) {
                    OperadorIMP imp = new OperadorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setId(rs.getString("id"));
                    imp.setCodigo(rs.getString("codigo"));
                    imp.setNome(rs.getString("nome"));
                    imp.setImportarMatricula(rs.getString("matricula"));
                    imp.setSenha(rs.getString("senha"));
                    imp.setId_tiponiveloperador(rs.getString("id_tiponiveloperador"));
                    imp.setId_situacadastro(rs.getString("id_situacaocadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " n.id,\n"
                    + " n.descricao,\n"
                    + " n.id_situacaocadastro,\n"
                    + " n.caloria,\n"
                    + " n.carboidrato,\n"
                    + " n.carboidratoInferior,\n"
                    + " n.proteina,\n"
                    + " n.proteinaInferior,\n"
                    + " n.gordura,\n"
                    + " n.gorduraSaturada,\n"
                    + " n.gorduraTrans,\n"
                    + " n.colesterolInferior,\n"
                    + " n.fibra,\n"
                    + " n.fibraInferior,\n"
                    + " n.calcio,\n"
                    + " n.ferro,\n"
                    + " n.sodio,\n"
                    + " n.percentualCaloria,\n"
                    + " n.percentualCarboidrato,\n"
                    + " n.percentualProteina,\n"
                    + " n.percentualGordura,\n"
                    + " n.percentualGorduraSaturada,\n"
                    + " n.percentualFibra,\n"
                    + " n.quantidade,\n"
                    + " n.percentualCalcio,\n"
                    + " n.percentualFerro,\n"
                    + " n.percentualSodio,\n"
                    + " n.id_TipoMedida,\n"
                    + " n.medidaInteira,\n"
                    + " n.id_tipomedidadecimal,\n"
                    + " n.Id_tipounidadeporcao,\n"
                    + " n.mensagemalergico1||' '||n.mensagemalergico2||' '||n.mensagemalergico3||\n"
                    + " ' '||n.mensagemalergico4||' '||n.mensagemalergico5 as mensagemalergico,\n"
                    + " ni.id_produto \n"
                    + "from nutricionaltoledo n\n"
                    + "join nutricionaltoledoitem ni on ni.id_nutricionaltoledo = n.id"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setSituacaoCadastro("1".equals(rst.getString("id_situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidrato"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setQuantidade(rst.getInt("quantidade"));
                    imp.setGordura(rst.getDouble("gordura"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPercentualCaloria(rst.getInt("percentualCaloria"));
                    imp.setPercentualCarboidrato(rst.getInt("percentualCarboidrato"));
                    imp.setPercentualProteina(rst.getInt("percentualProteina"));
                    imp.setPercentualGordura(rst.getInt("percentualGordura"));
                    imp.setPercentualGorduraSaturada(rst.getInt("percentualGorduraSaturada"));
                    imp.setPercentualFibra(rst.getInt("percentualFibra"));
                    imp.setPercentualCalcio(rst.getInt("percentualCalcio"));
                    imp.setPercentualFerro(rst.getInt("percentualFerro"));
                    imp.setPercentualSodio(rst.getInt("percentualSodio"));
                    imp.setIdTipoMedida(rst.getInt("id_TipoMedida"));
                    imp.setMedidaInteira(rst.getInt("medidaInteira"));
                    imp.setId_tipomedidadecimal(rst.getInt("id_tipomedidadecimal"));
                    imp.setId_tipounidadeporcao(rst.getInt("Id_tipounidadeporcao"));

                    imp.getMensagemAlergico().add(rst.getString("mensagemalergico"));
                    imp.addProduto(rst.getString("id_produto"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement st = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select \n"
                    + " r.id,\n"
                    + " r.descricao,\n"
                    + " r.id_situacaocadastro,\n"
                    + " rp.id_produto produtoreceita,\n"
                    + " rp.rendimento,\n"
                    + " ri.id_produto receitaitem,\n"
                    + " ri.qtdembalagemreceita,\n"
                    + " ri.qtdembalagemproduto,\n"
                    + " ri.baixaestoque,\n"
                    + " ri.fatorconversao,\n"
                    + " ri.embalagem \n"
                    + "from receita r\n"
                    + "join receitaproduto rp on rp.id_receita = r.id \n"
                    + "join receitaitem ri on ri.id_receita = r.id "
            )) {
                while (rs.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rs.getString("id"));
                    imp.setIdproduto(rs.getString("produtoreceita"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setId_situacaocadastro("1".equals(rs.getString("id_situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setRendimento(rs.getDouble("rendimento"));
                    imp.setQtdembalagemproduto(rs.getInt("qtdembalagemreceita"));
                    imp.setQtdembalagemreceita(rs.getInt("qtdembalagemproduto"));

                    imp.setFator(rs.getDouble("fatorconversao"));
                    imp.getProdutos().add(rs.getString("receitaitem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<PessoaImp> getPessoaImp() throws Exception {
        List<PessoaImp> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with forn as (select cnpj from fornecedor),\n"
                    + "	 convenio as (select id from conveniado)\n"
                    + "select\n"
                    + getLojaOrigem() + " cnpj_loja_mercado,\n"
                    + "	c.id impid,\n"
                    + "	c.cnpj,\n"
                    + "	c.inscricaoestadual,\n"
                    + "	c.orgaoemissor,\n"
                    + "	c.nome razao,\n"
                    + "	c.nome fantasia,\n"
                    + "	CASE c.id_situacaocadastro WHEN 1 THEN 'S' ELSE 'N' END ativo,\n"
                    + "	CASE WHEN c.bloqueado THEN 'S' ELSE 'N' END bloqueado,\n"
                    + "	to_char(c.datarestricao, 'DD/MM/YYYY') databloqueio,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.id_municipio municipioIBGE,\n"
                    + "	mun.descricao municipio,\n"
                    + "	c.id_estado ufIBGE,\n"
                    + "	est.sigla uf,\n"
                    + "	c.cep,\n"
                    + "	COALESCE(SUBSTRING(civil.descricao,1,3), 'NAO') estadocivil,\n"
                    + "	to_char(c.datanascimento, 'DD/MM/YYYY') datanascimento,\n"
                    + "	to_char(c.datacadastro, 'DD/MM/YYYY') datacadastro,\n"
                    + "	CASE c.sexo WHEN 0 THEN 'F' ELSE 'M' END sexo,\n"
                    + "	c.empresa,\n"
                    + "	c.enderecoempresa empresaendereco,\n"
                    + "	c.numeroempresa empresanumero,\n"
                    + "	c.complementoempresa empresacomplemento,\n"
                    + "	c.bairroempresa empresabairro,\n"
                    + "	c.id_municipioempresa empresamunicipioIBGE,\n"
                    + "	mun_emp.descricao empresamunicipio,\n"
                    + "	c.id_estadoempresa empresaufIBGE,\n"
                    + "	est_emp.sigla empresauf,\n"
                    + "	c.cepempresa empresacep,\n"
                    + "	c.telefoneempresa empresatelefone,\n"
                    + "	to_char(c.dataadmissao, 'DD/MM/YYYY') dataadmissao,\n"
                    + "	c.cargo,\n"
                    + "	to_char(c.salario, '999999990D00') salario,\n"
                    + "	to_char(c.valorlimite, '999999990D00') valorlimite,\n"
                    + "	c.nomeconjuge,\n"
                    + "	c.nomepai,\n"
                    + "	c.nomemae,\n"
                    + "	regexp_replace(c.observacao2,'[\\\\n\\\\r]+',' ','g') observacao,\n"
                    + "	c.vencimentocreditorotativo diavencimento,\n"
                    + "	CASE WHEN c.permitecreditorotativo THEN 'S' ELSE 'N' END permitecreditorotativo,\n"
                    + "	CASE WHEN c.permitecheque THEN 'S' ELSE 'N' END permitecheque,	\n"
                    + "	c.telefone,\n"
                    + "	c.celular,\n"
                    + "	c.email,\n"
                    + "	NULL fax,\n"
                    + "	c.telefone cobrancaTelefone,\n"
                    + "	0 prazopagamento,\n"
                    + "	c.endereco cobrancaendereco,\n"
                    + "	c.numero cobrancanumero,\n"
                    + "	c.complemento cobrancacomplemento,\n"
                    + "	c.bairro cobrancabairro,\n"
                    + "	c.id_municipio cobrancamunicipioibge,\n"
                    + "	mun.descricao cobrancamunicipio,\n"
                    + "	c.id_estado cobrancaufibge,\n"
                    + "	est.sigla cobrancauf,\n"
                    + "	c.cep cobrancacep,\n"
                    + "	'NENHUM'::VARCHAR tipoorgaopublico,\n"
                    + "	0 limitecompra,\n"
                    + "	''::VARCHAR inscricaomunicipal,\n"
                    + "	'NAO CONTRIBUINTE'::VARCHAR tipoindicadorie,\n"
                    + "	case when isforn.cnpj = c.cnpj then true else false end fornecedor,\n"
                    + "	case when conv.id = c.id then true else false end conveniado\n"
                    + "FROM \n"
                    + "	clientepreferencial c\n"
                    + "	LEFT JOIN municipio mun ON\n"
                    + "		c.id_municipio = mun.id\n"
                    + "	LEFT JOIN estado est ON\n"
                    + "		c.id_estado = est.id\n"
                    + "	LEFT JOIN municipio mun_emp ON\n"
                    + "		c.id_municipioempresa = mun_emp.id\n"
                    + "	LEFT JOIN estado est_emp ON\n"
                    + "		c.id_estadoempresa = est_emp.id\n"
                    + "	LEFT JOIN tipoestadocivil civil ON\n"
                    + "		c.id_tipoestadocivil = civil.id\n"
                    + "	left join forn isforn on c.cnpj = isforn.cnpj\n"
                    + "	left join convenio conv on c.id = conv.id\n"
                    + "union \n"
                    + "select\n"
                    + getLojaOrigem() + " cnpj_loja_mercado,\n"
                    + "	f.id impid,\n"
                    + "	f.cnpj,\n"
                    + "	f.inscricaoestadual,\n"
                    + "	null orgaoemissor,\n"
                    + "	f.razaosocial razao,\n"
                    + "	f.nomefantasia fantasia,\n"
                    + "	CASE f.id_situacaocadastro WHEN 1 THEN 'S' ELSE 'N' END ativo,\n"
                    + "	CASE WHEN f.bloqueado THEN 'S' ELSE 'N' END bloqueado,\n"
                    + "	null databloqueio,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.id_municipio municipioIBGE,\n"
                    + "	m.descricao municipio,\n"
                    + "	f.id_estado ufIBGE,\n"
                    + "	est.sigla uf,\n"
                    + "	f.cep,\n"
                    + "	'NÂO' estadocivil,\n"
                    + "	to_char(f.datacadastro, 'DD/MM/YYYY') datanascimento,\n"
                    + "	to_char(f.datacadastro, 'DD/MM/YYYY') datacadastro,\n"
                    + "	null sexo,\n"
                    + "	f.razaosocial empresa,\n"
                    + "	f.endereco empresaendereco,\n"
                    + "	f.numero empresanumero,\n"
                    + "	f.complemento empresacomplemento,\n"
                    + "	f.bairro empresabairro,\n"
                    + "	f.id_municipio empresamunicipioIBGE,\n"
                    + "	m.descricao empresamunicipio,\n"
                    + "	f.id_estado empresaufIBGE,\n"
                    + "	est.sigla empresauf,\n"
                    + "	f.cep empresacep,\n"
                    + "	f.telefone empresatelefone,\n"
                    + "	to_char(f.datasintegra, 'DD/MM/YYYY') dataadmissao,\n"
                    + "	null cargo,\n"
                    + "	null salario,\n"
                    + "	null valorlimite,\n"
                    + "	null nomeconjuge,\n"
                    + "	null nomepai,\n"
                    + "	null nomemae,\n"
                    + "	regexp_replace(f.observacao,'[\\\\n\\\\r]+',' ','g') observacao,\n"
                    + "	f.tiporegravencimento diavencimento,\n"
                    + "	null permitecreditorotativo,\n"
                    + "	null permitecheque,	\n"
                    + "	f.telefone,\n"
                    + "	null celular,\n"
                    + "	null email,\n"
                    + "	NULL fax,\n"
                    + "	f.telefone cobrancaTelefone,\n"
                    + "	0 prazopagamento,\n"
                    + "	f.endereco cobrancaendereco,\n"
                    + "	f.numero cobrancanumero,\n"
                    + "	f.complemento cobrancacomplemento,\n"
                    + "	f.bairro cobrancabairro,\n"
                    + "	f.id_municipio cobrancamunicipioibge,\n"
                    + "	m.descricao cobrancamunicipio,\n"
                    + "	f.id_estado cobrancaufibge,\n"
                    + "	est.sigla cobrancauf,\n"
                    + "	f.cep cobrancacep,\n"
                    + "	'NENHUM'::VARCHAR tipoorgaopublico,\n"
                    + "	0 limitecompra,\n"
                    + "	''::VARCHAR inscricaomunicipal,\n"
                    + "	'NAO CONTRIBUINTE'::VARCHAR tipoindicadorie,\n"
                    + "	true fornecedor,\n"
                    + "	case when conv.id is not null then true else false end conveniado\n"
                    + "FROM \n"
                    + "	fornecedor f\n"
                    + "	LEFT JOIN municipio m ON f.id_municipio = m.id\n"
                    + "	LEFT JOIN estado est ON f.id_estado = est.id\n"
                    + "	LEFT JOIN municipio cm ON f.id_municipiocobranca = cm.id\n"
                    + "	LEFT JOIN estado ce ON f.id_estadocobranca = ce.id\n"
                    + "	left join convenio conv on f.id = conv.id")) {
                while (rs.next()) {
                    PessoaImp imp = new PessoaImp();

                    imp.setCnpjLojaMercado(rs.getString("cnpj_loja_mercado"));
                    imp.setImpid(rs.getInt("impid"));
                    imp.setCnpj(rs.getLong("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rs.getString("orgaoemissor"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getString("ativo"));
                    imp.setBloqueado(rs.getString("bloqueado"));
                    imp.setDatabloqueio(rs.getDate("databloqueio"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioibge(rs.getString("municipioibge"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUfibge(rs.getString("ufibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEstadocivil(rs.getString("estadocivil"));
                    imp.setDatanascimento(rs.getString("datanascimento"));
                    imp.setDatacadastro(rs.getString("datacadastro"));
                    imp.setSexo(rs.getString("sexo"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaendereco(rs.getString("empresaendereco"));
                    imp.setEmpresanumero(rs.getString("empresanumero"));
                    imp.setEmpresacomplemento(rs.getString("empresacomplemento"));
                    imp.setEmpresabairro(rs.getString("empresabairro"));
                    imp.setEmpresamunicipioibge(rs.getInt("empresamunicipioibge"));
                    imp.setEmpresamunicipio(rs.getString("empresamunicipio"));
                    imp.setEmpresaufibge(rs.getInt("empresaufibge"));
                    imp.setEmpresauf(rs.getString("empresauf"));
                    imp.setEmpresacep(rs.getString("empresacep"));
                    imp.setEmpresatelefone(rs.getString("empresatelefone"));
                    imp.setDataadmissao(rs.getString("dataadmissao"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getString("salario"));
                    imp.setValorlimite(rs.getString("valorlimite"));
                    imp.setNomeconjuge(rs.getString("nomeconjuge"));
                    imp.setNomepai(rs.getString("nomepai"));
                    imp.setNomemae(rs.getString("nomemae"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDiavencimento(rs.getInt("diavencimento"));
                    imp.setPermitecreditorotativo(rs.getString("permitecreditorotativo"));
                    imp.setPermitecheque(rs.getString("permitecheque"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCobrancatelefone(rs.getString("cobrancatelefone"));
                    imp.setPrazopagamento(rs.getInt("prazopagamento"));
                    imp.setCobrancaendereco(rs.getString("cobrancaendereco"));
                    imp.setCobrancanumero(rs.getString("cobrancanumero"));
                    imp.setCobrancacomplemento(rs.getString("cobrancacomplemento"));
                    imp.setCobrancabairro(rs.getString("cobrancabairro"));
                    imp.setCobrancamunicipioibge(rs.getInt("cobrancamunicipioibge"));
                    imp.setCobrancamunicipio(rs.getString("cobrancamunicipio"));
                    imp.setCobrancaufibge(rs.getInt("cobrancaufibge"));
                    imp.setCobrancauf(rs.getString("cobrancauf"));
                    imp.setCobrancacep(rs.getString("cobrancacep"));
                    imp.setTipoorgaopublico(rs.getString("tipoorgaopublico"));
                    imp.setLimitecompra(rs.getInt("limitecompra"));
                    imp.setInscricaomunicipal(rs.getString("inscricaomunicipal"));
                    imp.setTipoindicadorie(rs.getString("tipoindicadorie"));
                    imp.setFornecedor(rs.getBoolean("fornecedor"));
                    imp.setConveniado(rs.getBoolean("conveniado"));

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
        return new VRToVRDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VRToVRDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                        next.setIdClientePreferencial(rst.getString("clientepreferencial"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("valordesconto"));
                        next.setValorAcrescimo(rst.getDouble("valoracrescimo"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        next.setEnderecoCliente(rst.getString("enderecocliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCanceladoEmVenda(rst.getBoolean("canceladoemvenda"));

                        int tipoCancelamento = rst.getInt("tipocancelamento");
                        TipoCancelamento tc = null;
                        if (tipoCancelamento != 0) {
                            switch (tipoCancelamento) {
                                case 1:
                                    tc = TipoCancelamento.DEVOLUCAO_DE_MERCADORIA;
                                    break;
                                case 2:
                                    tc = TipoCancelamento.ERRO_DE_REGISTRO;
                                    break;
                                case 3:
                                    tc = TipoCancelamento.DINHEIRO_DO_CLIENTE_INSUFICIENTE;
                                    break;
                                case 4:
                                    tc = TipoCancelamento.PRODUTO_COM_PRECO_ERRADO;
                                    break;
                                case 5:
                                    tc = TipoCancelamento.TESTE_DE_EQUIPAMENTO;
                                    break;
                                case 6:
                                    tc = TipoCancelamento.CHEQUE_DO_CLIENTE_RECUSADO;
                                    break;
                                case 7:
                                    tc = TipoCancelamento.CARTAO_RECUSADO_OU_SEM_SALDO;
                                    break;
                                default:
                                    tc = TipoCancelamento.PROBLEMA_NO_EQUIPAMENTO;
                                    break;
                            }
                        }

                        next.setTipoCancelamento(tc);
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modeloimpressora"));
                        next.setChaveCfe(rst.getString("chavecfe"));
                        next.setChaveNfCe(rst.getString("chavenfce"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	id,\n"
                    + "	numerocupom,\n"
                    + "	ecf,\n"
                    + "	data,\n"
                    + "	id_clientepreferencial clientepreferencial,\n"
                    + "	to_char(horainicio, 'HH:MM:SS') as horainicio,\n"
                    + "	to_char(horatermino, 'HH:MM:SS') as horatermino,\n"
                    + "	cancelado,\n"
                    + "	subtotalimpressora,\n"
                    + "	id_tipocancelamento tipocancelamento,\n"
                    + "	cpf,\n"
                    + "	valordesconto,\n"
                    + "	valoracrescimo,\n"
                    + "	canceladoemvenda,\n"
                    + "	numeroserie,\n"
                    + "	modeloimpressora,\n"
                    + "	nomecliente,\n"
                    + "	enderecocliente,\n"
                    + "	id_clienteeventual clienteeventual,\n"
                    + "	chavecfe,\n"
                    + "	chavenfce,\n"
                    + "	id_tipodesconto tipodesconto\n"
                    + "	--,chavenfcecontingencia\n"
                    + "from\n"
                    + "	pdv.venda v \n"
                    + "where \n"
                    + "	id_loja = " + idLojaCliente + "\n"
                    + "	and data >= '" + FORMAT.format(dataInicio) + "'\n"
                    + "	and data <= '" + FORMAT.format(dataTermino) + "'\n"
                    + "order by\n"
                    + "	id";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("cod_venda"));
                        next.setProduto(rst.getString("cod_produto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorAcrescimo(rst.getDouble("valoracrescimo"));
                        next.setValorDesconto(rst.getDouble("valordesconto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidademedida"));
                        next.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        next.setCustoComImposto(rst.getDouble("custocomimposto"));
                        next.setCustoMedioSemImposto(rst.getDouble("customediosemimposto"));
                        next.setCustoMedioComImposto(rst.getDouble("customediocomimposto"));
                        next.setIcmsAliq(rst.getDouble("icms_aliq"));
                        next.setIcmsCst(rst.getInt("icms_cst"));
                        next.setIcmsReduzido(rst.getDouble("icms_red"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	vi.id,\n"
                    + "	vi.sequencia,\n"
                    + "	vi.id_venda cod_venda,\n"
                    + "	vi.id_produto cod_produto,\n"
                    + "	p.descricaoreduzida,\n"
                    + "	vi.quantidade,\n"
                    + "	vi.precovenda,\n"
                    + "	vi.cancelado,\n"
                    + "	vi.valorcancelado,\n"
                    + "	vi.id_tipocancelamento tipocancelamento,\n"
                    + "	vi.valordescontopromocao + vi.valordesconto valordesconto,\n"
                    + "	vi.valoracrescimo,\n"
                    + "	vi.codigobarras,\n"
                    + "	vi.unidademedida,\n"
                    + "	vi.custosemimposto,\n"
                    + "	vi.custocomimposto,\n"
                    + "	vi.customediosemimposto,\n"
                    + "	vi.customediocomimposto,\n"
                    + "	vi.id_tipodesconto tipodesconto,\n"
                    + "	aliq.situacaotributaria icms_cst,\n"
                    + "	aliq.porcentagem icms_aliq,\n"
                    + "	aliq.reduzido icms_red,\n"
                    + "	vi.contadordoc\n"
                    + "from\n"
                    + "	pdv.vendaitem vi\n"
                    + "	join produto p on vi.id_produto = p.id\n"
                    + "	join aliquota aliq on vi.id_aliquota = aliq.id\n"
                    + "where\n"
                    + "	id_venda in (select id from pdv.venda \n"
                    + "	where\n"
                    + "		id_loja = " + idLojaCliente + "\n"
                    + "		and data >= '" + VendaIterator.FORMAT.format(dataInicio) + "' \n"
                    + "		and data <= '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	order by id)\n"
                    + "order by\n"
                    + "	vi.id_venda, vi.sequencia";
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

    public void deletaLogEstoque(Date data, int idLoja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from logestoque where datamovimento = " + Utils.dateSQL(data)
                    + " and id_loja = " + idLoja);
        }
    }

}
