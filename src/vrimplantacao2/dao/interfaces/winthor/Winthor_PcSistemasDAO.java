package vrimplantacao2.dao.interfaces.winthor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
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
import vr.core.utils.StringUtils;
import vrframework.classe.ProgressBar;
//import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoAtacado;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.FornecedorPagamentoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class Winthor_PcSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private int idRegiaoDentroEstado;
    private int idRegiaoForaEstado;
    private boolean somenteClienteFidelidade = false;
    private String tipoRotativo = "";
    private boolean tributacaoNcmFigura = false;
    private boolean precoUnitario = false;

    private Date dataVendaInicial;
    private Date dataVendaFinal;

    public void setDataVendaInicial(Date dataVendaInicial) {
        this.dataVendaInicial = dataVendaInicial;
    }

    public void setDataVendaFinal(Date dataVendaFinal) {
        this.dataVendaFinal = dataVendaFinal;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public void setIdRegiaoDentroEstado(int idRegiaoDentroEstado) {
        this.idRegiaoDentroEstado = idRegiaoDentroEstado;
    }

    public void setIdRegiaoForaEstado(int idRegiaoForaEstado) {
        this.idRegiaoForaEstado = idRegiaoForaEstado;
    }

    public void setTipoRotativo(String tipoRotativo) {
        this.tipoRotativo = tipoRotativo;
    }

    public void setSomenteClienteFidelidade(boolean somenteClienteFidelidade) {
        this.somenteClienteFidelidade = somenteClienteFidelidade;
    }

    public void setTributacaoNcmFigura(boolean tributacaoNcmFigura) {
        this.tributacaoNcmFigura = tributacaoNcmFigura;
    }

    public void setPrecoUnitario(boolean precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String getSistema() {
        return "WINTHOR";//+ (!"".equals(complemento) ? " - " + complemento : "");
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MARGEM_MINIMA,
                OpcaoProduto.ATACADO,
                OpcaoProduto.CODIGO_BENEFICIO
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    codigo,\n"
                    + "    codigo || ' - ' || coalesce(fantasia, razaosocial) descricao\n"
                    + "FROM \n"
                    + "    pcfilial \n"
                    + "ORDER BY \n"
                    + "    codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    public static class TipoRotativo {

        public final String descricaocobranca;

        public TipoRotativo(String descricaocobranca) {
            this.descricaocobranca = descricaocobranca;
        }

        @Override
        public String toString() {
            return String.format(descricaocobranca);
        }
    }

    public List<TipoRotativo> getTipoRotativo() {
        List<TipoRotativo> result = new ArrayList<>();
        try (
                Statement st = ConexaoOracle.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT DISTINCT \n"
                        + "     CODCOB descricao\n"
                        + "    FROM PCPREST\n"
                        + "    ORDER BY 1"
                )) {
            while (rs.next()) {
                result.add(new TipoRotativo(rs.getString("descricao")));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao carregar as Tipos de Rotativo", ex);
        }
        return result;
    }

    public static class Regiao {

        public final int id;
        public final String descricao;

        public Regiao(int id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return String.format("%04d - %s", id, descricao);
        }
    }

    public List<Regiao> getRegioes() {
        List<Regiao> result = new ArrayList<>();
        try (
                Statement st = ConexaoOracle.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT\n"
                        + "	r.NUMREGIAO id,\n"
                        + "	r.REGIAO || ' - '|| r.UF || ' - filial ' || r.CODFILIAL descricao\n"
                        + "FROM\n"
                        + "	pcregiao r\n"
                        + "ORDER BY\n"
                        + "	NUMREGIAO"
                )) {
            while (rs.next()) {
                result.add(new Regiao(rs.getInt("id"), rs.getString("descricao")));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao carregar as regiões", ex);
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "    codprodprinc id,\n"
                    + "    descricao,\n"
                    + "    usados\n"
                    + "from\n"
                    + "    (SELECT\n"
                    + "        p.codprod,\n"
                    + "        p.descricao,\n"
                    + "        p.CODPRODPRINC,\n"
                    + "        (SELECT COUNT(*) FROM pcprodut WHERE codprodprinc = p.codprod) usados\n"
                    + "    FROM \n"
                    + "        pcprodut p) A\n"
                    + "WHERE\n"
                    + "    usados > 1\n"
                    + "ORDER by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT\n"
                    + "    dp.codepto merc1,\n"
                    + "    dp.descricao merc1_descricao,\n"
                    + "    sc.codsec merc2,\n"
                    + "    sc.descricao merc2_descricao,\n"
                    + "    ct.codcategoria merc3,\n"
                    + "    ct.categoria merc3_descricao,\n"
                    + "    sc.codsubcategoria merc4,\n"
                    + "    sc.subcategoria merc4_descricao\n"
                    + "FROM\n"
                    + "    pcdepto dp\n"
                    + "    LEFT JOIN pcsecao sc ON\n"
                    + "        dp.codepto = sc.codepto\n"
                    + "    LEFT JOIN pccategoria ct ON\n"
                    + "        ct.codsec = sc.codsec\n"
                    + "    LEFT JOIN pcsubcategoria sc ON\n"
                    + "        sc.codsec = ct.codsec AND\n"
                    + "        sc.codcategoria = ct.codcategoria\n"
                    + "order BY\n"
                    + "    dp.codepto,\n"
                    + "    sc.codsec,\n"
                    + "    ct.codcategoria,\n"
                    + "    sc.codsubcategoria"*/
                    "WITH mercadologico AS (\n"
                    + "    SELECT DISTINCT\n"
                    + "     codepto merc1,\n"
                    + "	 codsec merc2,\n"
                    + "	 CASE WHEN codcategoria = 0 THEN NULL \n"
                    + "	 ELSE codcategoria END merc3,\n"
                    + "	 CASE WHEN codsubcategoria = 0 THEN NULL \n"
                    + "	 ELSE codsubcategoria END merc4\n"
                    + "    FROM PCPRODUT\n"
                    + "    ORDER BY 1\n"
                    + "   )\n"
                    + "   SELECT \n"
                    + "    p.merc1,\n"
                    + "    d.DESCRICAO merc1_descricao,\n"
                    + "    p.merc2,\n"
                    + "    s.DESCRICAO merc2_descricao,\n"
                    + "    CASE WHEN p.merc3 IS NULL THEN p.merc2\n"
                    + "         ELSE p.merc3 END merc3,\n"
                    + "    CASE WHEN p.merc3 IS NULL THEN s.DESCRICAO\n"
                    + "         ELSE pc.CATEGORIA END merc3_descricao,\n"
                    + "    CASE WHEN p.merc4 IS NULL AND p.merc3 IS NULL THEN p.merc2\n"
                    + "         WHEN p.merc4 IS NULL THEN p.merc3\n"
                    + "         ELSE p.merc4 END merc4,\n"
                    + "    CASE WHEN p.merc4 IS NULL AND p.merc3 IS NULL THEN s.DESCRICAO\n"
                    + "         WHEN p.merc4 IS NULL THEN pc.CATEGORIA\n"
                    + "         ELSE sb.SUBCATEGORIA END merc4_descricao\n"
                    + "   FROM mercadologico p\n"
                    + "   LEFT JOIN PCDEPTO d ON d.CODEPTO = p.merc1\n"
                    + "   LEFT JOIN PCSECAO s ON s.CODSEC = p.merc2\n"
                    + "   LEFT JOIN PCCATEGORIA pc ON pc.CODCATEGORIA = p.merc3\n"
                    + "   LEFT JOIN PCSUBCATEGORIA sb ON sb.CODSUBCATEGORIA = p.merc4\n"
                    + "   ORDER BY 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	codst id,\n"
                    + "	mensagem descricao,\n"
                    + "	sittribut cst,\n"
                    + "	codicm aliquota,\n"
                    + "	COALESCE(NULLIF(100-COALESCE(percbasered, 0), 100), 0) reducao,\n"
                    + " percbasered reducao2\n"
                    + "FROM\n"
                    + "	PCTRIBUT\n"
                    + "ORDER BY\n"
                    + "	codst"
            )) {
                while (rs.next()) {
                    if (tributacaoNcmFigura) {
                        result.add(new MapaTributoIMP(
                                rs.getString("id"),
                                rs.getString("descricao"),
                                rs.getInt("cst"),
                                MathUtils.round(rs.getDouble("aliquota"), 2),
                                MathUtils.round(rs.getDouble("reducao2"), 2)
                        ));
                    }
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            MathUtils.round(rs.getDouble("aliquota"), 2),
                            MathUtils.round(rs.getDouble("reducao"), 2)
                    ));
                }
            }
        }
        return result;
    }

    private class Trib {

        public int icmsCst;
        public double icmsAliq;
        public double icmsRed;
        public int pisCofins;

        public Trib(int icmsCst, double icmsAliq, double icmsRed, int pisCofins) {
            this.icmsCst = icmsCst;
            this.icmsAliq = icmsAliq;
            this.icmsRed = icmsRed;
            this.pisCofins = pisCofins;
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Comentado metódo do produto para uma nova versão do select">
    /*@Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            Map<String, Trib> tribs = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "      ent.ncm,\n" +
                    "      case ent.tipofornec\n" +
                    "      when 'I' then 1\n" +
                    "      when 'D' then 2\n" +
                    "      when 'C' then 3\n" +
                    "      when 'V' then 4\n" +
                    "      else 10 end tipo,\n" +
                    "      trib.codsittribpiscofins piscofins,\n" +
                    "      trib.sittribut icms_cst,\n" +
                    "      trib.percicm icms_aliquota,\n" +
                    "      trib.percbaseredent icms_reducao\n" +
                    "from \n" +
                    "     PCTRIBENTRADA ent\n" +
                    "     join PCTRIBFIGURA trib on\n" +
                    "          ent.codfigura = trib.codfigura\n" +
                    "     join pcfilial e on\n" +
                    "          e.codigo = ent.codfilial\n" +
                    "where\n" +
                    "     ent.codfilial = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "      ncm, tipo"
            )) {
                while (rst.next()) {
                    if (!tribs.containsKey(rst.getString("ncm"))) {
                        tribs.put(rst.getString("ncm"), new Trib(
                                rst.getInt("icms_cst"),
                                rst.getDouble("icms_aliquota"),
                                rst.getDouble("icms_reducao"),
                                rst.getInt("piscofins")
                        ));
                    }
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    p.codprod id,\n" +
                    "    p.dtcadastro datacadastro, \n" +
                    "    ean.ean,\n" +
                    "    ean.qtunit qtdembalagem,\n" +
                    "    ean.unidade tipoembalagem,\n" +
                    "    p.qtunitcx qtdembalagemcompra,\n" +
                    "    p.unidademaster tipoembalagemcompra,\n" +        
                    "    p.aceitavendafracao e_balanca,\n" +
                    "    ean.prazoval validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.codepto merc1,\n" +
                    "    p.codsec merc2,\n" +
                    "    p.codcategoria merc3,\n" +
                    "    p.codsubcategoria merc4,\n" +
                    "    CASE WHEN p.codprodprinc != p.codprod then p.codprodprinc else null END id_familiaproduto,\n" +
                    "    round(coalesce(p.pesobruto, 0),2) pesobruto,\n" +
                    "    round(coalesce(p.pesoliq, 0),2) pesoliquido,\n" +
                    "    coalesce(est.estmin, 0) estoqueminimo,\n" +
                    "    coalesce(est.estmax, 0) estoquemaximo,    \n" +
                    "    coalesce(est.qtest,0) estoque,\n" +
                    "    coalesce(ean.margem,0) margem,\n" +
                    "    coalesce(est.custoreal,0) custosemimposto,\n" +
                    "    coalesce(est.custorep,0) custocomimposto,\n" +
                    "    coalesce(ean.pvenda / (CASE WHEN coalesce(ean.qtunit,1) = 0 THEN 1 ELSE coalesce(ean.qtunit,1) end),0) precovenda,\n" +
                    "    CASE WHEN pf.ativo = 'N' THEN 0 ELSE 1 END situacaocadastro,\n" +
                    "    p.nbm ncm,\n" +
                    "    coalesce(est.codcest, p.codcest) cest,\n" +
                    "    piscofins.sittribut piscofins_debito,\n" +
                    "    piscofins.descricaotribpiscofins,\n" +
                    "    p.codsittribpiscofins piscofins,\n" +
                    "    t.codnatrec piscofins_natrec,\n" +
                    "    tabst.codst idtributacao,\n" +
                    "    icms.sittribut icmscst,\n" +
                    "    icms.codicm icmsaliq,\n" +
                    "    icms.codicmtab icmsred,\n" +
                    "    p.codncmex,\n" +
                    "    p.codfornec fabricante\n" +        
                    "FROM\n" +
                    "    pcprodut p\n" +
                    "    JOIN pcfilial emp ON emp.codigo = " + getLojaOrigem() + "\n" +
                    "    JOIN pcfornec f ON emp.codfornec = f.codfornec\n" +
                    "    LEFT JOIN (\n" +
                    "        SELECT\n" +
                    "            ean.codprod,\n" +
                    "            ean.codfilial,\n" +
                    "            ean.codauxiliar ean,\n" +
                    "            ean.unidade,\n" +
                    "            ean.qtunit,\n" +
                    "            ean.prazoval,\n" +
                    "            ean.pvenda,\n" +
                    "            ean.margem\n" +
                    "        FROM\n" +
                    "            pcembalagem ean\n" +
                    "        union\n" +
                    "        SELECT\n" +
                    "            a.codprod,\n" +
                    "            '0' codfilial,\n" +
                    "            a.ean,\n" +
                    "            a.unidade,\n" +
                    "            a.qtunit,\n" +
                    "            a.prazoval,\n" +
                    "            0 precovenda,\n" +
                    "            0 margem\n" +
                    "        from\n" +
                    "            (SELECT codprod, codauxiliar ean, descricao, unidade, qtunit, prazoval FROM pcprodut\n" +
                    "            UNION\n" +
                    "            SELECT codprod, codauxiliar2 ean, descricao, unidade, qtunit, prazoval FROM pcprodut) a\n" +
                    "        WHERE \n" +
                    "            NOT ean IN (SELECT codauxiliar FROM pcembalagem)\n" +
                    "        ORDER BY \n" +
                    "            codprod    \n" +
                    "    ) ean ON\n" +
                    "        ean.codprod = p.codprod AND\n" +
                    "        ean.codfilial = emp.codigo\n" +
                    "    JOIN pcest est ON\n" +
                    "        est.codprod = p.codprod AND\n" +
                    "        est.codfilial = emp.codigo\n" +
                    "    LEFT JOIN pcprodfilial pf ON\n" +
                    "        pf.codprod = p.codprod AND\n" +
                    "        pf.codfilial = emp.codigo\n" +
                    "    LEFT JOIN PCTABESCRSPED t ON\n" +
                    "        (T.CODPROD = p.codprod OR p.codprod = 0)\n" +
                    "        AND T.TIPOREGISTRO IN ('M4310','C4311','B4311','A4311','P4312')\n" +
                    "        AND ((T.DATAINIESCR IS NULL AND T.DATAFINESCR IS NULL)\n" +
                    "        OR  (T.DATAINIESCR <= current_date AND T.DATAFINESCR IS NULL)\n" +
                    "        OR  (current_date BETWEEN T.DATAINIESCR AND T.DATAFINESCR AND T.DATAINIESCR IS NOT NULL AND T.DATAFINESCR IS NOT NULL))\n" +
                    "    LEFT JOIN PCTABTRIB ic ON\n" +
                    "        ic.codprod = p.codprod\n" +
                    "        AND ic.codfilialnf = emp.codigo\n" +
                    "        AND ic.ufdestino = emp.uf\n" +
                    "    LEFT JOIN PCTRIBUT icms ON\n" +
                    "        ic.codst = icms.codst\n" +
                    "    LEFT JOIN pctribpiscofins piscofins ON\n" +
                    "        piscofins.codtribpiscofins = ic.codtribpiscofins\n" +
                    "    LEFT JOIN (select \n" +
                    "                    icm.codprod,\n" +
                    "                    icm.codst,\n" +
                    "                    reg.codfilial\n" +
                    "                from \n" +
                    "                    pctabpr icm \n" +
                    "                left join pcregiao reg on icm.numregiao = reg.numregiao\n" +
                    "                left join PCTRIBUT trb on icm.codst = trb.codst) tabst on tabst.codprod = p.codprod and\n" +
                    "                tabst.codfilial = emp.codigo\n" +
                    "ORDER BY id, ean"
            )) {
                int cont = 0, cont2 = 0;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcompra"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcompra"));
                    if(rst.getString("e_balanca") != null && !"".equals(rst.getString("e_balanca"))) {
                        imp.seteBalanca("S".equals(rst.getString("e_balanca").trim()) ? true : false);
                    } else {
                        imp.seteBalanca(false);
                    }
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(Utils.stringToDouble(rst.getString("pesoliquido")));
                    imp.setPesoLiquido(Utils.stringToDouble(rst.getString("pesobruto")));                    
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(Utils.stringToInt(rst.getString("situacaocadastro"))));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    //imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    //imp.setPiscofinsCstCredito(0);
                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                    //imp.setIcmsCst(rst.getInt("icmscst"));
                    //imp.setIcmsAliq(rst.getDouble("icmsaliq"));
                    //imp.setIcmsReducao(rst.getDouble("icmsred"));
                    imp.setIcmsDebitoId(rst.getString("idtributacao"));
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

                    Trib trib = tribs.get(rst.getString("codncmex"));
                    if (trib != null) {
                        imp.setPiscofinsCstDebito(0);
                        imp.setPiscofinsCstCredito(trib.pisCofins);
                        imp.setIcmsCst(trib.icmsCst);
                        imp.setIcmsAliq(trib.icmsAliq);
                        imp.setIcmsReducao(trib.icmsRed);
                        if ("561421".equals(imp.getImportId())) {
                            System.out.println(String.format(
                                    "cst=%d aliq=%f red=%f piscofins=%d",
                                    trib.icmsCst,
                                    trib.icmsAliq,
                                    trib.icmsRed,
                                    trib.pisCofins                                    
                            ));
                        }
                    }
                    
                    imp.setFornecedorFabricante(rst.getString("fabricante"));

                    result.add(imp);

                    cont2++;
                    cont++;

                    if (cont == 1000) {
                        ProgressBar.setStatus("Carregando produtos...." + cont2);
                        cont = 0;
                    }
                }                
            }
        }
        return result;
    }*/
    //</editor-fold>
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            Map<String, Trib> tribs = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "      ent.ncm,\n"
                    + "      case ent.tipofornec\n"
                    + "      when 'I' then 1\n"
                    + "      when 'D' then 2\n"
                    + "      when 'C' then 3\n"
                    + "      when 'V' then 4\n"
                    + "      else 10 end tipo,\n"
                    + "      trib.codsittribpiscofins piscofins,\n"
                    + "      trib.sittribut icms_cst,\n"
                    + "      trib.percicm icms_aliquota,\n"
                    + "      trib.percbaseredent icms_reducao\n"
                    + "from \n"
                    + "     PCTRIBENTRADA ent\n"
                    + "     join PCTRIBFIGURA trib on\n"
                    + "          ent.codfigura = trib.codfigura\n"
                    + "     join pcfilial e on\n"
                    + "          e.codigo = ent.codfilial\n"
                    + "where\n"
                    + "     ent.codfilial = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "      ncm, tipo"
            )) {
                while (rst.next()) {
                    if (!tribs.containsKey(rst.getString("ncm"))) {
                        tribs.put(rst.getString("ncm"), new Trib(
                                rst.getInt("icms_cst"),
                                rst.getDouble("icms_aliquota"),
                                rst.getDouble("icms_reducao"),
                                rst.getInt("piscofins")
                        ));
                    }
                }
            }
            String sql = "WITH \n"
                    + "icms_dentro_estado AS (\n"
                    + "	SELECT\n"
                    + "		ic.CODPROD id_produto,\n"
                    + "		ic.CODST id_tributacao,\n"
                    + "		PISCOFINS.SITTRIBUT piscofins_s,\n"
                    + "		piscofins.SITTRIBUTDEV piscofins_e\n"
                    + "	FROM\n"
                    + "		pctabpr ic	\n"
                    + "		LEFT JOIN pctribpiscofins piscofins ON\n"
                    + "			piscofins.codtribpiscofins = ic.codtribpiscofins\n"
                    + "	WHERE\n"
                    + "		ic.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                    + "),\n"
                    + "icms_fora_estado AS (\n"
                    + "	SELECT\n"
                    + "		ic.CODPROD id_produto,\n"
                    + "		ic.CODST id_tributacao\n"
                    + "	FROM\n"
                    + "		pctabpr ic\n"
                    + "	WHERE\n"
                    + "		ic.NUMREGIAO = " + idRegiaoForaEstado + "\n"
                    + "),\n"
                    + "natrec AS (\n"
                    + "	SELECT\n"
                    + "		distinct\n"
                    + "		t.CODPROD id_produto,\n"
                    + "		REPLACE(t.NCM, '.','') ncm,\n"
                    + "		t.CODNATREC natrec\n"
                    + "	FROM\n"
                    + "		PCTABESCRSPED t\n"
                    + "	WHERE\n"
                    + "		NOT (t.CODPROD IS NULL AND t.ncm IS null) and\n"
                    + "		T.TIPOREGISTRO IN ('M4310','C4311','B4311','A4311','P4312', 'S4316', 'I4314', 'R4313')\n"
                    + "		AND (\n"
                    + "			(T.DATAINIESCR IS NULL AND T.DATAFINESCR IS NULL)\n"
                    + "			OR (\n"
                    + "				T.DATAINIESCR <= current_date\n"
                    + "				AND T.DATAFINESCR IS NULL\n"
                    + "			) OR (\n"
                    + "				(current_date BETWEEN T.DATAINIESCR AND T.DATAFINESCR)\n"
                    + "				AND T.DATAINIESCR IS NOT NULL\n"
                    + "				AND T.DATAFINESCR IS NOT NULL\n"
                    + "			)\n"
                    + "		)\n"
                    + "),\n"
                    + "piscofins_icms AS (\n"
                    + "SELECT\n"
                    + " pt.CODPROD,\n"
                    + " pt.CODFILIALNF,\n"
                    + " pt.UFDESTINO,\n"
                    + " pt.CODST idtributacao,\n"
                    + " pt.CODTRIBPISCOFINS,\n"
                    + " pt.DTULTALTER,\n"
                    + " pc.DESCRICAOTRIBPISCOFINS,\n"
                    + " pc.SITTRIBUT piscofinscst\n"
                    + "FROM PCTABTRIB pt\n"
                    + "LEFT JOIN PCTRIBPISCOFINS pc ON pc.CODTRIBPISCOFINS = pt.CODTRIBPISCOFINS\n"
                    + "WHERE pt.UFDESTINO = 'PA'\n"
                    + "AND pt.CODFILIALNF = '" + getLojaOrigem() + "'\n"
                    + "ORDER BY pt.DTULTALTER DESC \n"
                    + ")"
                    + "SELECT\n"
                    + "	p.codprod id,\n"
                    + "	p.dtcadastro datacadastro, \n"
                    + "	COALESCE(ean.codauxiliar, p.CODAUXILIAR) ean,\n"
                    + "	p.CODAUXILIAR2,\n"
                    + "	COALESCE(\n"
                    + "		(CASE\n"
                    + "			WHEN ean.QTUNIT = 1 AND ean.QTMINIMAATACADO > 1 THEN ean.QTMINIMAATACADO\n"
                    + "			WHEN ean.QTUNIT >=2 THEN ean.QTUNIT\n"
                    + "			ELSE 1\n"
                    + "		END), \n"
                    + "		1\n"
                    + "	) as qtdembalagem,\n"
                    + "	coalesce(ean.qtunit, 1) embalagemunitario,\n"
                    + "	COALESCE(ean.unidade, 'UN') tipoembalagem,\n"
                    + "	p.qtunitcx qtdembalagemcompra,\n"
                    + "	p.unidademaster tipoembalagemcompra,        \n"
                    + "	p.aceitavendafracao e_balanca,\n"
                    + "	ean.prazoval validade,\n"
                    + "	p.descricao descricaocompleta,\n"
                    //                    + "	p.codepto merc1,\n"
                    //                    + "	p.codsec merc2,\n"
                    //                    + "	p.codcategoria merc3,\n"
                    //                    + "	p.codsubcategoria merc4,\n"
                    + " p.codepto merc1,\n"
                    + "	p.codsec merc2,\n"
                    + "	CASE \n"
                    + "		WHEN \n"
                    + "		(CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) IS NULL THEN p.codsec\n"
                    + "		ELSE \n"
                    + "		(CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) \n"
                    + "	END merc3,\n"
                    + "	CASE \n"
                    + "		WHEN \n"
                    + "	    (CASE WHEN p.codsubcategoria = 0 THEN NULL ELSE p.codsubcategoria END) IS NULL \n"
                    + "	    	AND\n"
                    + "	    (CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) IS NULL THEN p.codsec\n"
                    + "	    WHEN \n"
                    + "	     (CASE WHEN p.codsubcategoria = 0 THEN NULL ELSE p.codsubcategoria END) IS NULL THEN p.codcategoria\n"
                    + "	    ELSE\n"
                    + "	     p.codsubcategoria\n"
                    + "	 END merc4,\n"
                    + "	CASE WHEN p.codprodprinc != p.codprod then p.codprodprinc else null END id_familiaproduto,\n"
                    + "	round(coalesce(p.pesobruto, 0),2) pesobruto,\n"
                    + "	round(coalesce(p.pesoliq, 0),2) pesoliquido,\n"
                    + "	coalesce(est.estmin, 0) estoqueminimo,\n"
                    + "	coalesce(est.estmax, 0) estoquemaximo,    \n"
                    + "	coalesce(est.qtestger,0) estoque,\n"
                    + "	coalesce(ean.margem,0) margem,\n"
                    + "	coalesce(est.CUSTOULTENTCONT,0),\n"
                    + " est.CUSTOREAL custosemimposto,\n"
                    + "	coalesce(est.VLULTPCOMPRA,0),\n"
                    + " est.CUSTOULTENT custocomimposto,\n"
                    + "	coalesce(est.custofin,0) customedio,\n"
                    + "	ean.PVENDA,\n"
                    + "	coalesce(ean.pvenda / (CASE WHEN coalesce(ean.qtunit,1) = 0 THEN 1 ELSE coalesce(ean.qtunit,1) end),0) precovenda,\n"
                    + "	CASE WHEN pf.ativo = 'N' OR pf.FORALINHA = 'S' THEN 0 ELSE 1 END situacaocadastro,\n"
                    + "	p.nbm ncm,\n"
                    + "	coalesce(est.codcest, p.codcest) cest,\n"
                    + " cest.CODCEST new_cest,\n"
                    + "	icms_dentro_estado.piscofins_s,\n"
                    + "	icms_dentro_estado.piscofins_e,\n"
                    + "	(SELECT natrec FROM natrec WHERE (id_produto = p.codprod OR p.nbm LIKE natrec.ncm||'%') AND rownum = 1) piscofins_natrec,\n"
                    + "	icms_dentro_estado.id_tributacao icms_dentro_estado,\n"
                    + "	icms_fora_estado.id_tributacao icms_fora_estado,\n"
                    + "	p.codncmex,\n"
                    + "	p.codfornec fabricante,\n"
                    + "	pf.CODFIGURA,\n"
                    + " pic.idtributacao,\n"
                    + "	pic.piscofinscst,\n"
                    + " tb.PVENDA1 precoatacado,\n"
                    + " tb.PVENDAATAC1 precovarejo,\n"
                    + " ean.QTMINIMAATACADO qtdatacado\n"
                    + "FROM\n"
                    + "	pcprodut p\n"
                    + "	JOIN pcfilial emp ON emp.codigo = '" + getLojaOrigem() + "'\n"
                    + "	JOIN pcfornec f ON emp.codfornec = f.codfornec\n"
                    + " LEFT JOIN PCTABPR tb ON tb.CODPROD = p.CODPROD AND tb.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                    + " LEFT JOIN PCCESTPRODUTO prodcest ON prodcest.CODPROD = p.CODPROD\n"
                    + "	LEFT JOIN pccest cest ON cest.CODIGO = prodcest.CODSEQCEST\n"
                    + "	LEFT JOIN PCEMBALAGEM ean ON\n"
                    + "		ean.codprod = p.codprod AND\n"
                    + "		ean.codfilial = emp.codigo\n"
                    + "	LEFT JOIN pcest est ON\n"
                    + "		est.codprod = p.codprod AND\n"
                    + "		est.codfilial = emp.codigo\n"
                    + "	LEFT JOIN pcprodfilial pf ON\n"
                    + "		pf.codprod = p.codprod AND\n"
                    + "		pf.codfilial = emp.codigo\n"
                    + "	LEFT JOIN icms_dentro_estado ON\n"
                    + "		icms_dentro_estado.id_produto = p.codprod\n"
                    + "	LEFT JOIN icms_fora_estado ON\n"
                    + "		icms_fora_estado.id_produto = p.codprod\n"
                    + " LEFT JOIN piscofins_icms pic ON pic.codprod = p.codprod\n"
                    + "ORDER BY\n"
                    + "	id";
            if (precoUnitario) {
                sql = "WITH \n"
                        + "icms_dentro_estado AS (\n"
                        + "	SELECT\n"
                        + "		ic.CODPROD id_produto,\n"
                        + "		ic.CODST id_tributacao,\n"
                        + "		PISCOFINS.SITTRIBUT piscofins_s,\n"
                        + "		piscofins.SITTRIBUTDEV piscofins_e\n"
                        + "	FROM\n"
                        + "		pctabpr ic	\n"
                        + "		LEFT JOIN pctribpiscofins piscofins ON\n"
                        + "			piscofins.codtribpiscofins = ic.codtribpiscofins\n"
                        + "	WHERE\n"
                        + "		ic.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                        + "),\n"
                        + "icms_fora_estado AS (\n"
                        + "	SELECT\n"
                        + "		ic.CODPROD id_produto,\n"
                        + "		ic.CODST id_tributacao\n"
                        + "	FROM\n"
                        + "		pctabpr ic\n"
                        + "	WHERE\n"
                        + "		ic.NUMREGIAO = " + idRegiaoForaEstado + "\n"
                        + "),\n"
                        + "natrec AS (\n"
                        + "	SELECT\n"
                        + "		distinct\n"
                        + "		t.CODPROD id_produto,\n"
                        + "		REPLACE(t.NCM, '.','') ncm,\n"
                        + "		t.CODNATREC natrec\n"
                        + "	FROM\n"
                        + "		PCTABESCRSPED t\n"
                        + "	WHERE\n"
                        + "		NOT (t.CODPROD IS NULL AND t.ncm IS null) and\n"
                        + "		T.TIPOREGISTRO IN ('M4310','C4311','B4311','A4311','P4312', 'S4316', 'I4314', 'R4313')\n"
                        + "		AND (\n"
                        + "			(T.DATAINIESCR IS NULL AND T.DATAFINESCR IS NULL)\n"
                        + "			OR (\n"
                        + "				T.DATAINIESCR <= current_date\n"
                        + "				AND T.DATAFINESCR IS NULL\n"
                        + "			) OR (\n"
                        + "				(current_date BETWEEN T.DATAINIESCR AND T.DATAFINESCR)\n"
                        + "				AND T.DATAINIESCR IS NOT NULL\n"
                        + "				AND T.DATAFINESCR IS NOT NULL\n"
                        + "			)\n"
                        + "		)\n"
                        + "),\n"
                        + "piscofins_icms AS (\n"
                        + "SELECT\n"
                        + " pt.CODPROD,\n"
                        + " pt.CODFILIALNF,\n"
                        + " pt.UFDESTINO,\n"
                        + " pt.CODST idtributacao,\n"
                        + " pt.CODTRIBPISCOFINS,\n"
                        + " pt.DTULTALTER,\n"
                        + " pc.DESCRICAOTRIBPISCOFINS,\n"
                        + " pc.SITTRIBUT piscofinscst\n"
                        + "FROM PCTABTRIB pt\n"
                        + "LEFT JOIN PCTRIBPISCOFINS pc ON pc.CODTRIBPISCOFINS = pt.CODTRIBPISCOFINS\n"
                        + "WHERE pt.UFDESTINO = 'PA'\n"
                        + "AND pt.CODFILIALNF = '" + getLojaOrigem() + "'\n"
                        + "ORDER BY pt.DTULTALTER DESC \n"
                        + "),\n"
                        + "valores AS (\n"
                        + "	SELECT DISTINCT\n"
                        + "	 CODPROD id, \n"
                        + "	 LAST_VALUE(DTULTALTERSRVPRC) OVER (ORDER BY CODPROD) datahora\n"
                        + "	FROM PCEMBALAGEM \n"
                        + "	WHERE \n"
                        + "	CODFILIAL = '" + getLojaOrigem() + "'\n"
                        + "	AND QTUNIT = 1\n"
                        + "	AND DTULTALTPVENDA IS NOT NULL\n"
                        + ")\n"
                        + "SELECT\n"
                        + "	p.codprod id,\n"
                        + "	p.dtcadastro datacadastro, \n"
                        + "	COALESCE(ean.codauxiliar, p.CODAUXILIAR) ean,\n"
                        + "	p.CODAUXILIAR2,\n"
                        + "	COALESCE(\n"
                        + "		(CASE\n"
                        + "			WHEN ean.QTUNIT = 1 AND ean.QTMINIMAATACADO > 1 THEN ean.QTMINIMAATACADO\n"
                        + "			WHEN ean.QTUNIT >=2 THEN ean.QTUNIT\n"
                        + "			ELSE 1\n"
                        + "		END), \n"
                        + "		1\n"
                        + "	) as qtdembalagem,\n"
                        + "	coalesce(ean.qtunit, 1) embalagemunitario,\n"
                        + "	COALESCE(ean.unidade, 'UN') tipoembalagem,\n"
                        + "	p.qtunitcx qtdembalagemcompra,\n"
                        + "	p.unidademaster tipoembalagemcompra,        \n"
                        + "	p.aceitavendafracao e_balanca,\n"
                        + "	ean.prazoval validade,\n"
                        + "	p.descricao descricaocompleta,\n"
                        //                    + "	p.codepto merc1,\n"
                        //                    + "	p.codsec merc2,\n"
                        //                    + "	p.codcategoria merc3,\n"
                        //                    + "	p.codsubcategoria merc4,\n"
                        + " p.codepto merc1,\n"
                        + "	p.codsec merc2,\n"
                        + "	CASE \n"
                        + "		WHEN \n"
                        + "		(CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) IS NULL THEN p.codsec\n"
                        + "		ELSE \n"
                        + "		(CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) \n"
                        + "	END merc3,\n"
                        + "	CASE \n"
                        + "		WHEN \n"
                        + "	    (CASE WHEN p.codsubcategoria = 0 THEN NULL ELSE p.codsubcategoria END) IS NULL \n"
                        + "	    	AND\n"
                        + "	    (CASE WHEN p.codcategoria = 0 THEN NULL ELSE p.codcategoria END) IS NULL THEN p.codsec\n"
                        + "	    WHEN \n"
                        + "	     (CASE WHEN p.codsubcategoria = 0 THEN NULL ELSE p.codsubcategoria END) IS NULL THEN p.codcategoria\n"
                        + "	    ELSE\n"
                        + "	     p.codsubcategoria\n"
                        + "	 END merc4,\n"
                        + "	CASE WHEN p.codprodprinc != p.codprod then p.codprodprinc else null END id_familiaproduto,\n"
                        + "	round(coalesce(p.pesobruto, 0),2) pesobruto,\n"
                        + "	round(coalesce(p.pesoliq, 0),2) pesoliquido,\n"
                        + "	coalesce(est.estmin, 0) estoqueminimo,\n"
                        + "	coalesce(est.estmax, 0) estoquemaximo,    \n"
                        + "	coalesce(est.qtestger,0) estoque,\n"
                        + "	coalesce(ean.margem,0) margem,\n"
                        + "	coalesce(est.CUSTOULTENTCONT,0),\n"
                        + " est.CUSTOREAL custosemimposto,\n"
                        + "	coalesce(est.VLULTPCOMPRA,0),\n"
                        + " est.CUSTOULTENT custocomimposto,\n"
                        + "	coalesce(est.custofin,0) customedio,\n"
                        + "	ean.PVENDA precovenda,\n"
                        + "	coalesce(ean.pvenda / (CASE WHEN coalesce(ean.qtunit,1) = 0 THEN 1 ELSE coalesce(ean.qtunit,1) end),0),\n"
                        + "	CASE WHEN pf.ativo = 'N' THEN 0 ELSE 1 END situacaocadastro,\n"
                        + "	p.nbm ncm,\n"
                        + "	coalesce(est.codcest, p.codcest) cest,\n"
                        + " cest.CODCEST new_cest,\n"
                        + "	icms_dentro_estado.piscofins_s,\n"
                        + "	icms_dentro_estado.piscofins_e,\n"
                        + "	(SELECT natrec FROM natrec WHERE (id_produto = p.codprod OR p.nbm LIKE natrec.ncm||'%') AND rownum = 1) piscofins_natrec,\n"
                        + "	icms_dentro_estado.id_tributacao icms_dentro_estado,\n"
                        + "	icms_fora_estado.id_tributacao icms_fora_estado,\n"
                        + "	p.codncmex,\n"
                        + "	p.codfornec fabricante,\n"
                        + "	pf.CODFIGURA,\n"
                        + "     pic.idtributacao,\n"
                        + "	pic.piscofinscst,\n"
                        + "     tb.PVENDA1 precovenda2,\n"
                        + "	tb.PVENDAATAC1 precoatacado\n"
                        + "FROM\n"
                        + "	pcprodut p\n"
                        + "	JOIN pcfilial emp ON emp.codigo = '" + getLojaOrigem() + "'\n"
                        + "	JOIN pcfornec f ON emp.codfornec = f.codfornec\n"
                        + "     LEFT JOIN PCTABPR tb ON tb.CODPROD = p.CODPROD AND tb.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                        + "     LEFT JOIN PCCESTPRODUTO prodcest ON prodcest.CODPROD = p.CODPROD\n"
                        + "	LEFT JOIN pccest cest ON cest.CODIGO = prodcest.CODSEQCEST\n"
                        + "	LEFT JOIN PCEMBALAGEM ean ON\n"
                        + "		ean.codprod = p.codprod AND\n"
                        + "		ean.codfilial = emp.codigo\n"
                        + "	LEFT JOIN pcest est ON\n"
                        + "		est.codprod = p.codprod AND\n"
                        + "		est.codfilial = emp.codigo\n"
                        + "	LEFT JOIN pcprodfilial pf ON\n"
                        + "		pf.codprod = p.codprod AND\n"
                        + "		pf.codfilial = emp.codigo\n"
                        + "	LEFT JOIN icms_dentro_estado ON\n"
                        + "		icms_dentro_estado.id_produto = p.codprod\n"
                        + "	LEFT JOIN icms_fora_estado ON\n"
                        + "		icms_fora_estado.id_produto = p.codprod\n"
                        + " LEFT JOIN piscofins_icms pic ON pic.codprod = p.codprod\n"
                        + " JOIN valores v ON v.id = ean.CODPROD AND v.datahora = ean.DTULTALTERSRVPRC\n"
                        + " WHERE \n"
                        //+ "  ean.CODPROD in ('175785','240','176245') \n"
                        + "	ean.QTUNIT = 1\n"
                        + "	AND ean.DTULTALTPVENDA IS NOT NULL\n"
                        + "ORDER BY\n"
                        + "	id, ean.DTULTALTERSRVPRC";
            }
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                int cont = 0, cont2 = 0;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcompra"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcompra"));

                    if (rst.getString("e_balanca") != null && !"".equals(rst.getString("e_balanca"))) {
                        imp.seteBalanca("S".equals(rst.getString("e_balanca").trim()) ? true : false);
                    } else {
                        imp.seteBalanca(false);
                    }

                    //int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    //ProdutoBalancaVO bal = produtosBalanca.get(codigoProduto); --GOIAS
                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO bal = produtosBalanca.get(codigoProduto);

                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setTipoEmbalagemCotacao(imp.getTipoEmbalagem());
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(bal.getValidade());
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca("S".equals(rst.getString("e_balanca")));
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                        imp.setTipoEmbalagemCotacao(imp.getTipoEmbalagem());
                        //imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setQtdEmbalagem(rst.getInt("embalagemunitario"));
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));

                    /*imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));*/
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));

                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(Utils.stringToDouble(rst.getString("pesoliquido")));
                    imp.setPesoLiquido(Utils.stringToDouble(rst.getString("pesobruto")));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoMedioComImposto(rst.getDouble("customedio"));
                    imp.setCustoMedioSemImposto(rst.getDouble("customedio"));
                    //imp.setPrecovenda(rst.getDouble("precovenda"));
                    //imp.setPrecovenda(rst.getDouble("precovarejo"));
                    imp.setPrecovenda(rst.getDouble("precoatacado"));

                    /*if(rst.getInt("qtdatacado") > 1){
                        
                        if (imp.getEan() != null && !"".equals(imp.getEan()) && imp.getEan().length() < 7) {
                            imp.setEan(getLojaOrigem() + "00000" + imp.getEan());
                        }

                        imp.setQtdEmbalagem(rst.getInt("qtdatacado"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setPrecovenda(rst.getDouble("precovarejo"));
                        imp.setTipoAtacado(TipoAtacado.QTDE_TOTAL);
                    }*/
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(Utils.stringToInt(rst.getString("situacaocadastro"))));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    if (tributacaoNcmFigura) {
                        imp.setPiscofinsCstDebito(rst.getInt("piscofinscst"));
                        imp.setIcmsDebitoId(rst.getString("idtributacao"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("idtributacao"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("idtributacao"));
                        imp.setIcmsConsumidorId(rst.getString("idtributacao"));
                        imp.setIcmsCreditoId(rst.getString("idtributacao"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("idtributacao"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                    } else {
                        imp.setPiscofinsCstDebito(rst.getString("piscofins_s"));
                        imp.setPiscofinsCstCredito(rst.getString("piscofins_e"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                        imp.setIcmsDebitoId(rst.getString("icms_dentro_estado"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("icms_fora_estado"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_fora_estado"));
                        imp.setIcmsConsumidorId(rst.getString("icms_dentro_estado"));
                        imp.setIcmsCreditoId(rst.getString("icms_dentro_estado"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("icms_fora_estado"));
                    }

                    imp.setFornecedorFabricante(rst.getString("fabricante"));

                    result.add(imp);

                    cont2++;
                    cont++;

                    if (cont == 1000) {
                        ProgressBar.setStatus("Carregando produtos...." + cont2);
                        cont = 0;
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
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        /*"SELECT \n"
                        + "	p.CODPROD idproduto,\n"
                        + "	p.codauxiliar ean,\n"
                        + "	varejo.pvenda AS precovarejo,\n"
                        + "	-- Qtd de atacado por quantidade total\n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.QTMINIMAATACADO\n"
                        + "	--Qtd embalagem por embalagem\n"
                        + "	WHEN p.QTUNIT >=2 THEN p.QTUNIT ELSE 1 END) AS qtdatacado,\n"
                        + "	-- Preço do atacado por quantidade total\n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.PVENDAATAC \n"
                        + "	-- Preço do atacado por embalagem\n"
                        + "	WHEN p.QTUNIT >=2 AND p.PVENDA > 0 \n"
                        + "		THEN round((p.PVENDA / p.QTUNIT), 2) END) AS precoatacado, \n"
                        + "	p.MARGEM,\n"
                        + "	p.MARGEMIDEALATAC,\n"
                        + "	p.EMBALAGEM,\n"
                        + "	p.UNIDADE\n"
                        + "FROM \n"
                        + "	pcembalagem p\n"
                        + "JOIN\n"
                        + "	(SELECT \n"
                        + "		a.PVENDA,\n"
                        + "		a.CODPROD,\n"
                        + "		a.CODFILIAL,\n"
                        + "		a.QTUNIT\n"
                        + "	FROM \n"
                        + "		PCEMBALAGEM a \n"
                        + "	WHERE \n"
                        + "		COALESCE(a.QTUNIT, 1) = 1 and \n"
                        + "		a.dtinativo IS NULL) varejo ON p.CODPROD = varejo.codprod AND \n"
                        + "		varejo.codfilial = p.CODFILIAL\n"
                        + "WHERE \n"
                        + "	p.CODFILIAL = '" + getLojaOrigem() + "' AND \n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.QTMINIMAATACADO\n"
                        + "	WHEN p.QTUNIT >= 2 THEN p.QTUNIT ELSE 1 END) > 1 AND \n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.PVENDAATAC \n"
                        + "	-- Preço do atacado por embalagem\n"
                        + "	WHEN p.QTUNIT >=2 AND p.PVENDA > 0 \n"
                        + "		THEN round((p.PVENDA / p.QTUNIT), 2) END) > 0"*/
                        "SELECT \n"
                        + "       a.CODPROD idproduto,\n"
                        + "       emb.CODAUXILIAR ean,\n"
                        + "       a.NUMREGIAO,\n"
                        + "       a.PVENDA1 precoatacado,\n"
                        + "       a.PVENDAATAC1 precovarejo,\n"
                        + "       emb.QTMINIMAATACADO qtdatacado\n"
                        + "      FROM PCTABPR a\n"
                        + "      JOIN PCEMBALAGEM emb ON emb.CODPROD = a.CODPROD\n"
                        + "      WHERE \n"
                        + "       a.NUMREGIAO = " + idRegiaoForaEstado + "\n"
                        + "       AND \n"
                        + "       emb.CODFILIAL = '" + getLojaOrigem() + "'\n"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(rst.getString("ean"));

                        if (imp.getEan() != null && !"".equals(imp.getEan()) && imp.getEan().length() < 7) {
                            imp.setEan(getLojaOrigem() + "00000" + imp.getEan());
                        }

                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        } else if (opt == OpcaoProduto.MARGEM_MINIMA) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	p.CODPROD idproduto,\n"
                        + "	p.CODAUXILIAR ean,\n"
                        + "	p.UNIDADE,\n"
                        + "	COALESCE((CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.MARGEMIDEALATAC \n"
                        + "	WHEN p.QTUNIT >=2 THEN \n"
                        + "		(SELECT min(margem) FROM pcembalagem\n"
                        + "		WHERE \n"
                        + "		 codprod = p.CODPROD AND \n"
                        + "		 CODFILIAL = p.CODFILIAL) ELSE 0 END), 0) margemminima,\n"
                        + "	COALESCE((CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.margem \n"
                        + "	WHEN p.QTUNIT >=2 THEN \n"
                        + "		(SELECT max(margem) FROM pcembalagem\n"
                        + "		WHERE \n"
                        + "		 codprod = p.CODPROD AND \n"
                        + "		 CODFILIAL = p.CODFILIAL) ELSE 0 END), 0) margemmaxima,\n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN 'QTD_TOTAL'\n"
                        + "	--Qtd embalagem por embalagem\n"
                        + "	WHEN p.QTUNIT >=2 THEN 'QTD_EMBALAGEM' ELSE 'EMBALAGEM' END) AS tipoatacado\n"
                        + "FROM \n"
                        + "	pcembalagem p\n"
                        + "WHERE \n"
                        + "	p.CODFILIAL = '" + getLojaOrigem() + "' AND \n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.QTMINIMAATACADO\n"
                        + "	WHEN p.QTUNIT >=2 THEN p.QTUNIT ELSE 1 END) > 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setMargemMinima(rst.getDouble("margemminima"));
                        imp.setMargemMaxima(rst.getDouble("margemmaxima"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        } /*else if (opt == OpcaoProduto.TIPO_ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	p.CODPROD idproduto,\n"
                        + "	p.CODAUXILIAR ean,\n"
                        + "	p.UNIDADE,\n"
                        + "	COALESCE((CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.MARGEMIDEALATAC \n"
                        + "	WHEN p.QTUNIT >=2 THEN \n"
                        + "		(SELECT min(margem) FROM pcembalagem\n"
                        + "		WHERE \n"
                        + "		 codprod = p.CODPROD AND \n"
                        + "		 CODFILIAL = p.CODFILIAL) ELSE 0 END), 0) margemminima,\n"
                        + "	COALESCE((CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.margem \n"
                        + "	WHEN p.QTUNIT >=2 THEN \n"
                        + "		(SELECT max(margem) FROM pcembalagem\n"
                        + "		WHERE \n"
                        + "		 codprod = p.CODPROD AND \n"
                        + "		 CODFILIAL = p.CODFILIAL) ELSE 0 END), 0) margemmaxima,\n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN 'QTD_TOTAL'\n"
                        + "	--Qtd embalagem por embalagem\n"
                        + "	WHEN p.QTUNIT >=2 THEN 'QTD_EMBALAGEM' ELSE 'EMBALAGEM' END) AS tipoatacado\n"
                        + "FROM \n"
                        + "	pcembalagem p\n"
                        + "WHERE \n"
                        + "	p.CODFILIAL = '" + getLojaOrigem() + "' AND \n"
                        + "	(CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                        + "	 THEN p.QTMINIMAATACADO\n"
                        + "	WHEN p.QTUNIT >=2 THEN p.QTUNIT ELSE 1 END) > 1 AND\n"
                        + "       p.dtinativo IS NULL"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(rst.getString("ean"));

                        if (rst.getString("tipoatacado") != null
                                && !"".equals(rst.getString("tipoatacado"))) {
                            switch (rst.getString("tipoatacado").trim()) {
                                case "QTD_TOTAL":
                                    imp.setTipoAtacado(TipoAtacado.QTDE_TOTAL);
                                    break;
                                case "QTD_EMBALAGEM":
                                    imp.setTipoAtacado(TipoAtacado.QTDE_EMBALAGEM);
                                    break;
                                default:
                                    imp.setTipoAtacado(TipoAtacado.EMBALAGEM);
                                    break;
                            }
                        }

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        } /*else if (opt == OpcaoProduto.PRECO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH \n"
                        + "icms_dentro_estado AS (\n"
                        + "	SELECT\n"
                        + "		ic.CODPROD id_produto,\n"
                        + "		ic.CODST id_tributacao,\n"
                        + "		PISCOFINS.SITTRIBUT piscofins_s,\n"
                        + "		piscofins.SITTRIBUTDEV piscofins_e\n"
                        + "	from pctabpr ic	\n"
                        + "		LEFT JOIN pctribpiscofins piscofins ON	piscofins.codtribpiscofins = ic.codtribpiscofins\n"
                        + "	WHERE\n"
                        + "		ic.NUMREGIAO = "+idRegiaoDentroEstado+"\n"
                        + "),\n"
                        + "icms_fora_estado AS (\n"
                        + "	SELECT\n"
                        + "		ic.CODPROD id_produto,\n"
                        + "		ic.CODST id_tributacao\n"
                        + "	from pctabpr ic\n"
                        + "	WHERE\n"
                        + "		ic.NUMREGIAO = "+idRegiaoForaEstado+"\n"
                        + "),\n"
                        + "natrec AS (\n"
                        + "	SELECT\n"
                        + "		distinct\n"
                        + "		t.CODPROD id_produto,\n"
                        + "		REPLACE(t.NCM, '.','') ncm,\n"
                        + "		t.CODNATREC natrec\n"
                        + "	from PCTABESCRSPED t\n"
                        + "	WHERE\n"
                        + "		NOT (t.CODPROD IS NULL AND t.ncm IS null) and\n"
                        + "		T.TIPOREGISTRO IN ('M4310','C4311','B4311','A4311','P4312', 'S4316', 'I4314', 'R4313')\n"
                        + "		AND (\n"
                        + "			(T.DATAINIESCR IS NULL AND T.DATAFINESCR IS NULL)\n"
                        + "			OR (\n"
                        + "				T.DATAINIESCR <= current_date\n"
                        + "				AND T.DATAFINESCR IS NULL\n"
                        + "			) OR (\n"
                        + "				(current_date BETWEEN T.DATAINIESCR AND T.DATAFINESCR)\n"
                        + "				AND T.DATAINIESCR IS NOT NULL\n"
                        + "				AND T.DATAFINESCR IS NOT NULL\n"
                        + "			)\n"
                        + "		)\n"
                        + "),\n"
                        + "piscofins_icms AS (\n"
                        + "SELECT\n"
                        + " pt.CODPROD,\n"
                        + " pt.CODFILIALNF,\n"
                        + " pt.UFDESTINO,\n"
                        + " pt.CODST idtributacao,\n"
                        + " pt.CODTRIBPISCOFINS,\n"
                        + " pt.DTULTALTER,\n"
                        + " pc.DESCRICAOTRIBPISCOFINS,\n"
                        + " pc.SITTRIBUT piscofinscst\n"
                        + "FROM PCTABTRIB pt\n"
                        + "LEFT JOIN PCTRIBPISCOFINS pc ON pc.CODTRIBPISCOFINS = pt.CODTRIBPISCOFINS\n"
                        + "WHERE pt.UFDESTINO = 'GO'\n"
                        + "AND pt.CODFILIALNF = '"+getLojaOrigem()+"'\n"
                        + "ORDER BY pt.DTULTALTER DESC \n"
                        + ")\n"
                        + "SELECT\n"
                        + "	p.codprod id,\n"
                        + "	coalesce(ean.pvenda / (CASE WHEN coalesce(ean.qtunit,1) = 0 THEN 1 ELSE coalesce(ean.qtunit,1) end),0) precovenda\n"
                        + "FROM\n"
                        + "	pcprodut p\n"
                        + "	JOIN pcfilial emp ON emp.codigo = '"+getLojaOrigem()+"'\n"
                        + "	JOIN pcfornec f ON emp.codfornec = f.codfornec\n"
                        + " 	LEFT JOIN PCCESTPRODUTO prodcest ON prodcest.CODPROD = p.CODPROD\n"
                        + "	LEFT JOIN pccest cest ON cest.CODIGO = prodcest.CODSEQCEST\n"
                        + "	LEFT JOIN PCEMBALAGEM ean on ean.codprod = p.codprod AND\n"
                        + "		ean.codfilial = emp.codigo\n"
                        + "	LEFT JOIN pcest est on est.codprod = p.codprod AND\n"
                        + "		est.codfilial = emp.codigo \n"
                        + "	LEFT JOIN pcprodfilial pf on pf.codprod = p.codprod AND\n"
                        + "		pf.codfilial = emp.codigo\n"
                        + "	LEFT JOIN icms_dentro_estado ON	icms_dentro_estado.id_produto = p.codprod\n"
                        + "	LEFT JOIN icms_fora_estado on icms_fora_estado.id_produto = p.codprod\n"
                        + " LEFT JOIN piscofins_icms pic ON pic.codprod = p.codprod where p.codprod = '176245'\n"
                        + "ORDER BY\n"
                        + "	id,ean.DTULTALTERSRVPRC"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }*/ else if (opt == OpcaoProduto.CODIGO_BENEFICIO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH teste AS (\n"
                        + "SELECT PASSO1.CODPROD,\n"
                        + "       PASSO1.DESCRICAO,\n"
                        + "       PASSO1.UFDESTINO,\n"
                        + "       PASSO1.CODFILIALNF,\n"
                        + "       PASSO1.CODST FIGURA ,\n"
                        + "       PASSO1.DESCRICAOFIG,\n"
                        + "CASE WHEN \n"
                        + "  (SELECT PCEXCECAOCADASTROSFISCAIS.codcadastroexcecao\n"
                        + "     FROM PCEXCECAOCADASTROSFISCAIS \n"
                        + "    WHERE PCEXCECAOCADASTROSFISCAIS.codcadastroprinc = PASSO1.P_BENEFICIO\n"
                        + "      AND PCEXCECAOCADASTROSFISCAIS.TIPO1 = 'FT'\n"
                        + "      AND PCEXCECAOCADASTROSFISCAIS.VALOR1 = PASSO1.CODST ) IS NOT NULL \n"
                        + "     THEN (SELECT PCEXCECAOCADASTROSFISCAIS.codcadastroexcecao\n"
                        + "             FROM PCEXCECAOCADASTROSFISCAIS \n"
                        + "            WHERE PCEXCECAOCADASTROSFISCAIS.codcadastroprinc = PASSO1.P_BENEFICIO\n"
                        + "              AND PCEXCECAOCADASTROSFISCAIS.TIPO1 = 'FT'\n"
                        + "              AND PCEXCECAOCADASTROSFISCAIS.VALOR1 = PASSO1.CODST )\n"
                        + "   ELSE PASSO1.P_BENEFICIO   \n"
                        + "   END BENEFICIO_CORRETO\n"
                        + "FROM\n"
                        + "(select PCTABTRIB.CODPROD,\n"
                        + "       PCPRODUT.DESCRICAO, \n"
                        + "       PCTABTRIB.ufdestino,\n"
                        + "       PCTABTRIB.CODFILIALNF,\n"
                        + "       PCTABTRIB.CODST,\n"
                        + "       PCTRIBUT.mensagem DESCRICAOFIG,\n"
                        + "       (SELECT PCCODIGOBENEFICIOFISCALVINCULO.codigobeneficio \n"
                        + "          FROM PCCODIGOBENEFICIOFISCALVINCULO\n"
                        + "         WHERE PCCODIGOBENEFICIOFISCALVINCULO.codfiscal = PCTRIBUT.CODFISCAL \n"
                        + "           AND PCCODIGOBENEFICIOFISCALVINCULO.SITTRIBUT = PCTRIBUT.sittribut\n"
                        + "          ) P_BENEFICIO     \n"
                        + "  from PCTABTRIB , PCTRIBUT, PCPRODUT\n"
                        + " where PCTABTRIB.CODST = PCTRIBUT.CODST\n"
                        + "   --and PCTABTRIB.CODPROD IN (66085, 2309) --(ARROZ,FEIJAO)\n"
                        + "   and PCTABTRIB.CODPROD = PCPRODUT.CODPROD\n"
                        + "   and PCTABTRIB.UFDESTINO = 'GO'\n"
                        + "   and PCTRIBUT.CODFISCAL = 5102 \n"
                        + "   AND PCTABTRIB.CODFILIALNF = '" + getLojaOrigem() + "')PASSO1)\n"
                        + "   SELECT \n"
                        + "    CODPROD,\n"
                        + "    DESCRICAO,\n"
                        + "    BENEFICIO_CORRETO\n"
                        + "   FROM teste\n"
                        + "   WHERE BENEFICIO_CORRETO IS NOT NULL"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CODPROD"));
                        imp.setBeneficio(rst.getString("BENEFICIO_CORRETO"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    /*"SELECT * FROM (SELECT \n"
                    + "	p.CODPROD,\n"
                    + "	p.codfilial,\n"
                    + "	p.CODAUXILIAR ean,\n"
                    + "	p.UNIDADE,\n"
                    + "	-- Qtd de atacado por quantidade total\n"
                    + "	COALESCE((CASE WHEN p.QTUNIT = 1 AND p.QTMINIMAATACADO > 1\n"
                    + "	 THEN p.QTMINIMAATACADO\n"
                    + "	--Qtd embalagem por embalagem\n"
                    + "	WHEN p.QTUNIT >=2 THEN p.QTUNIT ELSE 1 END), 0) AS QTUNIT\n"
                    + "FROM \n"
                    + "	pcembalagem p \n"
                    + "union\n"
                    + "SELECT\n"
                    + "	a.codprod,\n"
                    + "	'0' codfilial,\n"
                    + "	a.ean,\n"
                    + "	a.unidade,\n"
                    + "	a.qtunit\n"
                    + "from\n"
                    + "	(SELECT codprod, codauxiliar ean, descricao, unidade, qtunit, prazoval FROM pcprodut\n"
                    + "	UNION\n"
                    + "	SELECT codprod, codauxiliar2 ean, descricao, unidade, qtunit, prazoval FROM pcprodut) a\n"
                    + "WHERE \n"
                    + "	NOT ean IN (SELECT codauxiliar FROM pcembalagem)\n"
                    + "ORDER BY \n"
                    + "	codprod) eans WHERE eans.codfilial = '" + getLojaOrigem() + "'"*/
                    "SELECT \n"
                    + "	 CODPROD,\n"
                    + "	 CAST(CODAUXILIAR AS varchar(100)) ean,\n"
                    + "	 UNIDADE,\n"
                    + "	 QTUNIT\n"
                    + "	FROM PCEMBALAGEM \n"
                    + "	WHERE \n"
                    + "	 CODFILIAL = '" + getLojaOrigem() + "'\n"
                    + "UNION\n"
                    + "SELECT \n"
                    + "       a.CODPROD codprod,\n"
                    + "       emb.CODFILIAL || '00000' || a.CODPROD,\n" //||emb.QTMINIMAATACADO ean,\n"
                    + "       upper(emb.UNIDADE) unidade,\n"
                    + "       emb.QTMINIMAATACADO qtunit\n"
                    + "      FROM PCTABPR a\n"
                    + "      JOIN PCEMBALAGEM emb ON emb.CODPROD = a.CODPROD\n"
                    + "      WHERE \n"
                    + "       a.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                    + "       AND emb.QTMINIMAATACADO > 1\n"
                    + "       AND emb.CODFILIAL = '" + getLojaOrigem() + "'\n"
                    + "       AND a.PVENDA1 > 0\n"
            /*+ "UNION   \n"
                    + "SELECT\n"
                    + "	pf.CODPROD codprod,\n"
                    + "	pf.CODFILIAL || '00000' || pf.CODPROD || pf.QTMINIMAATACADO ean,\n"
                    + "	'UN' unidade,\n"
                    + "	pf.QTMINIMAATACADO qtunit\n"
                    + "FROM\n"
                    + "	PCPRODFILIAL pf\n"
                    + "JOIN PCTABPR pt ON	pt.CODPROD = pf.CODPROD\n"
                    + "WHERE\n"
                    + "	pf.QTMINIMAATACADO > 1\n"
                    + "	AND pf.CODFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND pt.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                    + "	AND pt.PVENDAATAC1 > pt.PVENDA1\n"
                    + "	AND pt.PVENDAATAC1 IS NOT NULL\n"
                    + "	AND pf.CODFILIAL || '00000' || pf.CODPROD || pf.QTMINIMAATACADO NOT IN\n"
                    + "        (\n"
                    + "	SELECT\n"
                    + "		emb.CODFILIAL || '00000' || a.CODPROD || emb.QTMINIMAATACADO ean\n"
                    + "	FROM PCTABPR a\n"
                    + "	JOIN PCEMBALAGEM emb ON	emb.CODPROD = a.CODPROD\n"
                    + "	WHERE\n"
                    + "		a.NUMREGIAO = " + idRegiaoDentroEstado + "\n"
                    + "		AND emb.QTMINIMAATACADO > 1\n"
                    + "		AND emb.CODFILIAL = '" + getLojaOrigem() + "'\n"
                    + "		AND a.PVENDA1 > 0)"*/
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codprod"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtunit"));

//                    if (imp.getEan() != null && !"".equals(imp.getEan()) && imp.getEan().length() < 7) {
//                        imp.setEan(getLojaOrigem() + "00000" + imp.getEan());
//                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT\n"
                    + "    f.codfornec id,\n"
                    + "    f.fornecedor razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.cgc cnpj,\n"
                    + "    f.ie ie,\n"
                    + "    f.inscmunicip,\n"
                    + "    f.suframa,\n"
                    + "    CASE f.excluido WHEN 'S' THEN 0 ELSE 1 END ativo,\n"
                    + "    f.ender endereco,\n"
                    + "    f.numeroend numero,\n"
                    + "    f.complementoend complemento,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.cep,\n"
                    + "    f.endercob cob_endereco,\n"
                    + "    f.bairrocob cob_bairro,\n"
                    + "    f.municob cob_cidade,\n"
                    + "    f.estcob cob_estado,\n"
                    + "    f.cepcob cob_cep,\n"
                    + "    f.telefonecom,\n"
                    + "    f.telefoneadm,\n"
                    + "    f.telrep,\n"
                    + "    f.telfab,\n"
                    + "    f.telcob,\n"
                    + "    f.prazomin,\n"
                    + "    f.prazoentrega,\n"
                    + "    f.email,\n"
                    + "    f.emailnfe,\n"
                    + "    CASE f.contribuinteicms WHEN 'S' THEN 1 ELSE 3 END contribuinteicms,\n"
                    + "    f.vlminpedcompra,\n"
                    + "    f.vlminpedreposicao,\n"
                    + "    f.dtcadastro,\n"
                    + "    f.obs,\n"
                    + "    f.obs2,\n"
                    + "    f.observacao,\n"
                    + "    f.codparcela,\n"
                    + "    parc.descricao parcela,\n"
                    + "    f.tipofornec\n"
                    + "FROM\n"
                    + "    pcfornec f\n"
                    + "    LEFT JOIN PCPARCELASC parc ON\n"
                    + "        f.codparcela = parc.codparcela\n"
                    + "ORDER BY\n"
                    + "    f.codfornec"*/
                    "SELECT\n"
                    + "    f.codfornec id,\n"
                    + "    f.fornecedor razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.cgc cnpj,\n"
                    + "    f.ie ie,\n"
                    + "    f.inscmunicip,\n"
                    + "    f.suframa,\n"
                    + "    CASE f.excluido WHEN 'S' THEN 0 ELSE 1 END ativo,\n"
                    + "    f.ender endereco,\n"
                    + "    f.numeroend numero,\n"
                    + "    f.complementoend complemento,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.cep,\n"
                    + "    f.endercob cob_endereco,\n"
                    + "    f.bairrocob cob_bairro,\n"
                    + "    f.municob cob_cidade,\n"
                    + "    f.estcob cob_estado,\n"
                    + "    f.cepcob cob_cep,\n"
                    + "    f.telefonecom telefonecomercial2,\n"
                    + "    f.telefoneadm tel_financeiro,\n"
                    + "    f.telrep telefonecomercial,\n"
                    + "    f.telrep,\n"
                    + "    f.telfab,\n"
                    + "    f.telcob,\n"
                    + "    f.prazomin,\n"
                    + "    f.prazoentrega,\n"
                    + "    f.email,\n"
                    + "    f.emailnfe,\n"
                    + "    CASE f.contribuinteicms WHEN 'S' THEN 1 ELSE 3 END contribuinteicms,\n"
                    + "    f.vlminpedcompra,\n"
                    + "    f.vlminpedreposicao,\n"
                    + "    f.dtcadastro,\n"
                    + "    f.obs,\n"
                    + "    f.obs2,\n"
                    + "    trim(f.observacao) observacao,\n"
                    + "    f.codparcela,\n"
                    + "    parc.descricao parcela,\n"
                    + "    trim(f.tipofornec) tipofornec,\n"
                    + "    f.REPRES,\n"
                    + "    f.REP_CONTATO contatocomercial,\n"
                    + "    f.REP_EMAIL emailcomercial,\n"
                    + "    trim(f.REP_OBS) obscomercial,\n"
                    + "    f.COM_EMAIL emailfinanceiro,\n"
                    + "    f.SUP_EMAIL emailcomercial2,\n"
                    + "    f.REVENDA\n"
                    + "    ,TRIM(REGEXP_SUBSTR(REGEXP_REPLACE(parc.descricao,'[A-Z]',''), '[^/]+', 1, 1)) parcela1,\n"
                    + "    TRIM(REGEXP_SUBSTR(REGEXP_REPLACE(parc.descricao,'[A-Z]',''), '[^/]+', 1, 2)) parcela2,\n"
                    + "    TRIM(REGEXP_SUBSTR(REGEXP_REPLACE(parc.descricao,'[A-Z]',''), '[^/]+', 1, 3)) parcela3,\n"
                    + "    TRIM(REGEXP_SUBSTR(REGEXP_REPLACE(parc.descricao,'[A-Z]',''), '[^/]+', 1, 4)) parcela4,\n"
                    + "    TRIM(REGEXP_SUBSTR(REGEXP_REPLACE(parc.descricao,'[A-Z]',''), '[^/]+', 1, 5)) parcela5\n"
                    + "FROM\n"
                    + "    pcfornec f\n"
                    + "    LEFT JOIN PCPARCELASC parc ON\n"
                    + "        f.codparcela = parc.codparcela\n"
                    + "ORDER BY\n"
                    + "    f.codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("inscmunicip"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_municipio(rst.getString("cob_cidade"));
                    imp.setCob_uf(rst.getString("cob_estado"));
                    imp.setCob_cep(rst.getString("cob_cep"));
                    imp.setTel_principal(rst.getString("telrep"));

                    if (rst.getString("emailnfe") != null) {
                        imp.addEmail("E-MAIL NFE", rst.getString("emailnfe"), TipoContato.NFE);
                    }
                    if (rst.getString("telefonecomercial") != null) {
                        imp.addContato(rst.getString("repres"), rst.getString("telefonecomercial"), "", TipoContato.COMERCIAL, rst.getString("emailcomercial"));
                    }
                    if (rst.getString("tel_financeiro") != null) {
                        imp.addContato("FINANCEIRO", rst.getString("tel_financeiro"), "", TipoContato.FINANCEIRO, rst.getString("emailfinanceiro"));
                    }
                    if (rst.getString("telefonecomercial2") != null) {
                        imp.addContato("COMERCIAL - 2", rst.getString("telefonecomercial2"), "", TipoContato.COMERCIAL, rst.getString("emailcomercial2"));
                    }

                    if ("S".equals(rst.getString("tipofornec"))) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    }

                    if ("S".equals(rst.getString("revenda"))) {
                        imp.setRevenda(true);
                    }

                    /*if (Utils.stringToLong(rst.getString("telrep")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("REPRESENTANTE");
                        cont.setTelefone(Utils.stringLong(rst.getString("telrep")));
                        imp.getContatos().put(cont, "1");
                    }
                    if (Utils.stringToLong(rst.getString("telfab")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FABRICANTE");
                        cont.setTelefone(Utils.stringLong(rst.getString("telfab")));
                        imp.getContatos().put(cont, "2");
                    }
                    if (Utils.stringToLong(rst.getString("telcob")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("COBRANCA");
                        cont.setTelefone(Utils.stringLong(rst.getString("telcob")));
                        imp.getContatos().put(cont, "3");
                    }
                    if (Utils.stringToLong(rst.getString("email")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("4");
                        cont.setNome("EMAIL");
                        cont.setTelefone(Utils.stringLong(rst.getString("email")));
                        imp.getContatos().put(cont, "4");
                    }
                    if (Utils.stringToLong(rst.getString("emailnfe")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("5");
                        cont.setNome("EMAIL NFE");
                        cont.setTelefone(Utils.stringLong(rst.getString("emailnfe")));
                        cont.setTipoContato(TipoContato.NFE);
                        imp.getContatos().put(cont, "5");
                    }*/
                    
                    if (rst.getString("parcela1") != null) {
                        imp.addCondicaoPagamento(rst.getInt("parcela1"));
                    }
                    if (rst.getString("parcela2") != null) {
                        imp.addCondicaoPagamento(rst.getInt("parcela2"));
                    }
                    if (rst.getString("parcela3") != null) {
                        imp.addCondicaoPagamento(rst.getInt("parcela3"));
                    }
                    if (rst.getString("parcela4") != null) {
                        imp.addCondicaoPagamento(rst.getInt("parcela4"));
                    }
                    if (rst.getString("parcela5") != null) {
                        imp.addCondicaoPagamento(rst.getInt("parcela5"));
                    }

                    imp.setObservacao(
                            "PRAZO MINIMO: " + Utils.acertarTexto(rst.getString("prazomin")) + "\n"
                            + "VLR. MINIMO REPOSICAO: " + Utils.stringToDouble(rst.getString("vlminpedreposicao")) + "\n"
                            + "PARCELAMENTO: " + Utils.stringToDouble(rst.getString("parcela")) + "\n"
                            + Utils.acertarTexto(rst.getString("obs") == null ? "" : rst.getString("obs")) + "\n"
                            + Utils.acertarTexto(rst.getString("obs2") == null ? "" : rst.getString("obs2")) + "\n"
                            + Utils.acertarTexto(rst.getString("observacao") == null ? "" : rst.getString("observacao")) + "\n"
                            + Utils.acertarTexto(rst.getString("obscomercial") == null ? "" : rst.getString("obscomercial"))
                    );
                    imp.setValor_minimo_pedido(rst.getDouble("vlminpedcompra"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    switch (Utils.acertarTexto(rst.getString("tipofornec"))) {
                        case "I":
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                        case "D":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            break;
                        default:
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                            break;
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

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT m.* from\n"
                    + "(SELECT codfornec, codprod \n"
                    + "FROM pcmov GROUP BY codfornec, codprod) m\n"
                    + "JOIN pcprodut p ON m.codprod = p.codprod\n"
                    + "JOIN pcfornec f ON m.codfornec = f.codfornec"*/
                    "SELECT \n"
                    + "m.*,\n"
                    + "CAST(regexp_replace(p.EMBALAGEMMASTER,'[^0-9]','') AS int) qtde\n"
                    + "from\n"
                    + "(SELECT codfornec, codprod \n"
                    + "FROM pcmov GROUP BY codfornec, codprod) m\n"
                    + "JOIN pcprodut p ON m.codprod = p.codprod\n"
                    + "JOIN pcfornec f ON m.codfornec = f.codfornec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    imp.setIdProduto(rst.getString("codprod"));
                    imp.setCodigoExterno(rst.getString("codprod"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (Statement stm2 = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT\n"
                        + "    c.codcli id,\n"
                        + "    c.cgcent cnpj,\n"
                        + "    coalesce(c.ieent, c.rg) inscricaoestadual,\n"
                        + "    c.orgaorg orgaoemissor,\n"
                        + "    c.cliente razao,\n"
                        + "    c.fantasia,    \n"
                        + "    CASE c.bloqueio WHEN 'S' THEN 1 ELSE 0 END bloqueado,\n"
                        + "    c.dtbloq databloqueio,\n"
                        + "    c.enderent endereco,\n"
                        + "    c.numeroent numero,\n"
                        + "    c.complementoent complemento,\n"
                        + "    c.bairroent bairro,\n"
                        + "    c.municent municipio,\n"
                        + "    c.estent estado,\n"
                        + "    c.cepent cep,\n"
                        + "    c.dtnasc datanascimento,\n"
                        + "    c.dtcadastro datacadastro,\n"
                        + "    c.sexo,\n"
                        + "    c.empresa,\n"
                        + "    c.enderempr empresaendereco,\n"
                        + "    c.municempr empresamunicipio,\n"
                        + "    c.estempr empresauf,\n"
                        + "    c.telempr empresatelefone,\n"
                        + "    c.dtadmissao dataadmissao,\n"
                        + "    c.cargo,\n"
                        + "    c.rendamensal salario,\n"
                        + "    c.limcred valorlimite,\n"
                        + "    c.nomeconjuge,\n"
                        + "    c.filiacaopai nomepai,\n"
                        + "    c.filiacaomae nomemae,\n"
                        + "    c.observacao,\n"
                        + "    c.obscredito,\n"
                        + "    c.obs,\n"
                        + "    c.obs2,\n"
                        + "    c.obs3,\n"
                        + "    c.obs4,\n"
                        + "    c.diafaturar diavencimento,\n"
                        + "    c.telent,\n"
                        + "    c.telent1,\n"
                        + "    c.telcob,\n"
                        + "    c.telcom,\n"
                        + "    c.telconjuge,\n"
                        + "    c.telcelent,\n"
                        + "    c.email,\n"
                        + "    c.emailnfe,\n"
                        + "    c.emailcob,\n"
                        + "    c.faxcli,\n"
                        + "    c.endercob,\n"
                        + "    c.numerocob,\n"
                        + "    c.complementocob,\n"
                        + "    c.bairrocob,\n"
                        + "    c.municcob,\n"
                        + "    c.estcob,\n"
                        + "    c.cepcob\n"
                        + "FROM \n"
                        + "    PCCLIENT c\n"
                        + //                        "WHERE\n" +
                        //                        (
                        //                                somenteClienteFidelidade ?
                        //                                "    nvl(c.NUMCARTAOFIDELIDADE,0) > 0" :""
                        //                                //"    CODCOB <> 'CONV'\n" 
                        //                        ) +
                        "ORDER BY\n"
                        + "    c.codcli"
                )) {
                    while (rst.next()) {
                        ClienteIMP imp = new ClienteIMP();
                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                        imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setAtivo(true);
                        imp.setBloqueado(rst.getBoolean("bloqueado"));
                        imp.setDataBloqueio(rst.getDate("databloqueio"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setDataNascimento(rst.getDate("datanascimento"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        switch (StringUtils.acertarTexto(rst.getString("sexo"), "M")) {
                            case "F":
                                imp.setSexo(TipoSexo.FEMININO);
                                break;
                            default:
                                imp.setSexo(TipoSexo.MASCULINO);
                                break;
                        }
                        imp.setEmpresa(rst.getString("empresa"));
                        imp.setEmpresaEndereco(rst.getString("empresaendereco"));
                        imp.setEmpresaMunicipio(rst.getString("empresamunicipio"));
                        imp.setEmpresaUf(rst.getString("empresauf"));
                        imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                        imp.setDataAdmissao(rst.getDate("dataadmissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("salario"));
                        imp.setValorLimite(rst.getDouble("valorlimite"));
                        imp.setNomeConjuge(rst.getString("nomeconjuge"));
                        imp.setNomePai(rst.getString("nomepai"));
                        imp.setNomeMae(rst.getString("nomemae"));
                        imp.setObservacao(
                                Utils.acertarTexto(rst.getString("observacao")) + "\n"
                                + Utils.acertarTexto(rst.getString("obs")) + "\n"
                                + (rst.getString("telent") != null ? "\n" + "TEL. PRINC: " + rst.getString("telent") : "")
                                + (rst.getString("telent1") != null ? "\n" + "CELULAR: " + rst.getString("telent1") : "")
                                + (rst.getString("telcob") != null ? "\n" + "TEL. COB: " + rst.getString("telcob") : "")
                                + (rst.getString("telcom") != null ? "\n" + "TEL. COM: " + rst.getString("telcom") : "")
                                + (rst.getString("telconjuge") != null ? "\n" + "TEL. CONJUGE: " + rst.getString("telconjuge") : "")
                                + (rst.getString("telcelent") != null ? "\n" + "CELULAR2: " + rst.getString("telcelent") : "")
                                + (rst.getString("email") != null ? "\n" + "EMAIL: " + rst.getString("email") : "")
                                //+ (rst.getString("emailnfe") != null ? "\n" + "EMAIL NF-E: " + rst.getString("emailnfe") : "")
                                + (rst.getString("emailcob") != null ? "\n" + "EMAIL COB.: " + rst.getString("emailcob") : "")
                        );
                        imp.setTelefone(rst.getString("telent"));
                        imp.setCelular(rst.getString("telent1"));

                        if (rst.getString("emailnfe") != null) {
                            imp.addContato("1", "NFE", "", "", rst.getString("emailnfe"));
                        }
                        imp.setEmail(rst.getString("emailnfe"));
                        imp.setCobrancaEndereco(rst.getString("endercob"));
                        imp.setCobrancaNumero(rst.getString("numerocob"));
                        imp.setCobrancaComplemento(rst.getString("complementocob"));
                        imp.setCobrancaBairro(rst.getString("bairrocob"));
                        imp.setCobrancaMunicipio(rst.getString("municcob"));
                        imp.setCobrancaUf(rst.getString("estcob"));
                        imp.setCobrancaCep(rst.getString("cepcob"));

                        result.add(imp);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoOracle.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT\n"
                        + "	cp.RECNUM id,\n"
                        + "	f.CODFORNEC id_fornecedor,\n"
                        + "	f.cgc cnpj,\n"
                        + "	cp.NUMNOTA numerodocumento,\n"
                        + "	cp.DTEMISSAO dataemissao,\n"
                        + "	cp.DTLANC dataentrada,\n"
                        + "	cp.DTULTALTER datahoraalteracao,\n"
                        + "	cp.DTVENC datavencimento,\n"
                        + "	cp.VALOR real,\n"
                        + "	cp.VALORDEV,\n"
                        + "	cp.DUPLIC parcela,\n"
                        + "     cp.DESCONTOFIN desconto,\n"
                        + "	CASE WHEN cp.DESCONTOFIN > 0 THEN (cp.VALOR - cp.DESCONTOFIN) ELSE cp.VALOR END valor,\n"
                        + "	coalesce(cp.HISTORICO,'') observacoes,\n"
                        + "	coalesce(cp.HISTORICO2,'') observacoes2\n"
                        + "FROM\n"
                        + "	PCLANC cp\n"
                        + "	JOIN PCFORNEC f ON\n"
                        + "		cp.CODFORNEC  = f.CODFORNEC \n"
                        + "WHERE\n"
                        + "	cp.vpago IS NULL AND\n"
                        + "	cp.CODFILIAL = " + getLojaOrigem() + "\n"
                        + "ORDER BY\n"
                        + "	cp.RECNUM"
                )) {
            while (rs.next()) {
                ContaPagarIMP imp = new ContaPagarIMP();

                imp.setId(rs.getString("id"));
                imp.setIdFornecedor(rs.getString("id_fornecedor"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setNumeroDocumento(rs.getString("numerodocumento"));
                imp.setDataEmissao(rs.getDate("dataemissao"));
                imp.setDataEntrada(rs.getDate("dataentrada"));
                imp.setDataHoraAlteracao(rs.getTimestamp("datahoraalteracao"));
                String observacoes
                        = rs.getString("observacoes")
                        + "\n"
                        + rs.getString("observacoes2")
                        + "\n Valor Original:" + rs.getString("real")
                        + "\n Desconto: " + rs.getString("desconto");
                ContaPagarVencimentoIMP parcela = imp.addVencimento(
                        rs.getDate("datavencimento"),
                        rs.getDouble("valor"),
                        StringUtils.toInt(rs.getString("parcela"))
                );
                parcela.setObservacao(observacoes);

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "    r.numtransvenda || '-' || r.prest id,\n"
                    + "    r.dtemissao emissao,\n"
                    + "    r.duplic cupom,\n"
                    + "    r.valor,\n"
                    + "    r.obsfinanc obs,\n"
                    + "    r.codcli,\n"
                    + "    r.dtvenc vencimento\n"
                    + "FROM \n"
                    + "    PCPREST r\n"
                    + "WHERE \n"
                    + "    r.codfilialnf = " + getLojaOrigem() + " and\n"
                    + "    r.dtpag IS NULL AND \n"
                    + "    r.codcli != 1 AND \n"
                    + "    r.codcob = '" + tipoRotativo + "'"//IN ('CANC', 'BK', 'CHDV', 'CHD1', 'CHP', 'JUR')"//'CONV', 'JUR')"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs") + " - " + tipoRotativo);
                    imp.setIdCliente(rst.getString("codcli"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    r.numtransvenda || '-' || r.prest id,\n"
                    + "    c.cgcent cnpj,\n"
                    + "    r.numcheque || CASE WHEN r.dvcheque IS NULL THEN '' ELSE '-' || r.dvcheque END numerocheque,\n"
                    + "    r.numbanco banco,\n"
                    + "    r.numagencia || CASE WHEN r.dvagencia IS NULL THEN '' ELSE '-' || r.dvagencia END agencia,\n"
                    + "    r.numcontacorrente || CASE WHEN r.dvconta IS NULL THEN '' ELSE '-' || r.dvconta END conta,\n"
                    + "    r.dtemissao data,\n"
                    + "    r.duplic numerocupom,\n"
                    + "    r.valor,\n"
                    + "    coalesce(c.ieent, c.rg) rg,\n"
                    + "    c.telent telefone,\n"
                    + "    c.cliente nome,\n"
                    + "    r.obs observacao,\n"
                    + "    r.alinea,\n"
                    + "    r.dtultalter dataHoraAlteracao\n"
                    + "FROM \n"
                    + "    PCPREST r\n"
                    + "    JOIN pcclient c ON\n"
                    + "        r.codcli = c.codcli\n"
                    + "WHERE \n"
                    + "    r.codfilialnf = " + getLojaOrigem() + " and\n"
                    + "    r.dtpag IS NULL AND \n"
                    + "    r.codcli != 1 AND \n"
                    + "    r.codcob IN ('CHP')"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cnpj"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("data"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAlinea(rst.getInt("alinea"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataHoraAlteracao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT\n"
                    + "    emp.CONV_CODIGO id,\n"
                    + "    emp.CONV_DESCRICAO razao,\n"
                    + "    cli.TIP_CGC_CPF cnpj,\n"
                    + "    cli.TIP_INSC_EST_IDENT inscricaoestadual,\n"
                    + "    cli.TIP_ENDERECO endereco,\n"
                    + "    cli.TIP_BAIRRO bairro,\n"
                    + "    cli.TIP_CIDADE cidade,\n"
                    + "    cli.TIP_ESTADO uf,\n"
                    + "    cli.TIP_CEP cep,\n"
                    + "    cast(cli.TIP_FONE_DDD||cli.TIP_FONE_NUM as numeric) fone1,\n"
                    + "    cicl.cicl_dta_inicio dataInicio,\n"
                    + "    cicl.cicl_dta_fim dataTermino,\n"
                    + "    emp.CONV_DESCONTO desconto,\n"
                    + "    emp.CONV_DIA_COBRANCA diapagamento,\n"
                    + "    emp.CONV_DIA_CORTE diainiciorenovacao,\n"
                    + "    emp.CONV_BLOQUEAR bloquear\n"
                    + "FROM \n"
                    + "    AC1CCONV emp\n"
                    + "    left join AA2CTIPO cli on\n"
                    + "        emp.conv_emp_codigo = cli.TIP_CODIGO and\n"
                    + "        emp.conv_emp_digito = cli.TIP_DIGITO\n"
                    + "    left join (\n"
                    + "        select\n"
                    + "            *\n"
                    + "        from\n"
                    + "            AC2CVCIC c\n"
                    + "        where\n"
                    + "            c.cicl_codigo = \n"
                    + "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n"
                    + "    ) cicl on\n"
                    + "        emp.conv_codigo = cicl.conv_codigo\n"
                    + "order by \n"
                    + "    emp.CONV_CODIGO"*/
                    "SELECT \n"
                    + "  CODIGO id,\n"
                    + "  RAZAOSOCIAL razao,\n"
                    + "  ENDERECO,\n"
                    + "  CIDADE,\n"
                    + "  BAIRRO,\n"
                    + "  NUMERO,\n"
                    + "  UF,\n"
                    + "  CEP,\n"
                    + "  CGC cnpj,\n"
                    + "  TELEFONE fone1\n"
                    + " FROM PCFILIAL\n"
                    + " WHERE \n"
                    + "  CODIGO = 1"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    //imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("fone1"));
                    /*imp.setDataInicio(format.parse(rst.getString("datainicio")));
                    imp.setDataTermino(format.parse(rst.getString("datatermino")));
                    imp.setDesconto(rst.getDouble("desconto"));
                    imp.setDiaPagamento(rst.getInt("diapagamento"));
                    imp.setDiaInicioRenovacao(rst.getInt("diainiciorenovacao"));
                    imp.setBloqueado(rst.getBoolean("bloquear"));*/

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select \n"
                    + "    cli.cli_codigo||cli.cli_digito id,\n"
                    + "    cli.CLI_CPF_CNPJ cnpj,\n"
                    + "    cli.CLI_NOME razao,\n"
                    + "    cli.CLI_FIL_CAD loja,\n"
                    + "    cli.cli_convenio idEmpresa,\n"
                    + "    case when cli.CLI_STATUS = 0 then 0 else 1 end bloqueado,\n"
                    + "    coalesce(lim_cv.LIM_LIMITE,0) limite_convenio,\n"
                    + "    coalesce(conv.CONV_DESCONTO,0) desconto\n"
                    + "from \n"
                    + "    CAD_CLIENTE cli\n"
                    + "    left join END_CLIENTE ender on\n"
                    + "        cli.cli_codigo = ender.cli_codigo\n"
                    + "        and ender.end_tpo_end = 1\n"
                    + "    left join AC1QLIMI lim_cv on\n"
                    + "        cli.cli_codigo = lim_cv.lim_codigo\n"
                    + "        and cli.cli_digito = lim_cv.LIM_DIGITO\n"
                    + "        and lim_cv.LIM_MODALIDADE = 3\n"
                    + "    left join AC1CCONV conv on\n"
                    + "        conv.CONV_CODIGO = cli.CLI_convenio\n"
                    + "where\n"
                    + "    cli.cli_convenio > 0\n"
                    + "order by \n"
                    + "    cli.cli_codigo"*/
                    "SELECT\n"
                    + "    c.codcli id,\n"
                    + "    c.cgcent cnpj,\n"
                    + "    c.cliente razao,\n"
                    + "    c.fantasia,    \n"
                    + "    CASE c.bloqueio WHEN 'S' THEN 1 ELSE 0 END bloqueado,\n"
                    + "    c.limcred limite_convenio,\n"
                    + "    '1' idEmpresa\n"
                    + "FROM \n"
                    + "    PCCLIENT c\n"
                    + "WHERE \n"
                    + " c.CODCOB = 'CONV'\n"
                    + "ORDER BY c.codcli"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("idEmpresa"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setConvenioLimite(rst.getDouble("limite_convenio"));
                    //imp.setConvenioDesconto(rst.getDouble("desconto"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    lan.lanc_codigo||'-'||lan.lanc_data||'-'||lan.lanc_seq id,\n"
                    + "    lan.lanc_loja id_loja,\n"
                    + "    lan.lanc_codigo idcliente,\n"
                    + "    lan.lanc_caixa ecf,\n"
                    + "    case lan.lanc_cupom when 0 then lan.lanc_documento else lan.lanc_cupom end numerocupom,\n"
                    + "    lan.lanc_data dataHora,\n"
                    + "    lan.lanc_valor valor,\n"
                    + "    lan.lanc_historico historico,\n"
                    + "    emp.CONV_DIA_CORTE,\n"
                    + "    cicl.cicl_dta_inicio dataInicio,\n"
                    + "    cicl.cicl_dta_fim dataTermino\n"
                    + "from\n"
                    + "    ac1clanc lan\n"
                    + "    join CAD_CLIENTE c on\n"
                    + "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n"
                    + "    join AC1CCONV emp on\n"
                    + "        c.cli_convenio = emp.conv_codigo\n"
                    + "    left join (\n"
                    + "        select\n"
                    + "            *\n"
                    + "        from\n"
                    + "            AC2CVCIC c\n"
                    + "        where\n"
                    + "            c.cicl_codigo = \n"
                    + "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n"
                    + "    ) cicl on\n"
                    + "        emp.conv_codigo = cicl.conv_codigo\n"
                    + "where\n"
                    + "    lan.lanc_tipo = 1\n"
                    + "    and lan.lanc_modalidade = 3\n"
                    + "    and c.cli_convenio > 0\n"
                    + "    and lan.lanc_data >= cicl.cicl_dta_inicio\n"
                    + "    and cicl_dta_fim >= cast('1' || (extract(year from current_date) - 2000) || \n"
                    + "    (lpad(extract(month from current_date), 2, '0')) ||\n"
                    + "    (lpad(extract(day from current_date), 2, '0')) as numeric)\n"
                    + "    and lan.lanc_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n"
                    + "order by\n"
                    + "    lan.lanc_codigo,\n"
                    + "    lan.lanc_data,\n"
                    + "    lan.lanc_seq"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("idCliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(new Timestamp(format.parse(rst.getString("datahora")).getTime()));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("historico"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	pr.CODPROD,\n"
                    + "	pr.DESCRICAO,\n"
                    + "	rec.CODIGO idreceita,\n"
                    + "	rec.DESCRICAO descreceita,\n"
                    + "	rec.INGREDIENTE1,\n"
                    + "	rec.INGREDIENTE2,\n"
                    + "	rec.INGREDIENTE3,\n"
                    + "	rec.INGREDIENTE4,\n"
                    + "	rec.INGREDIENTE5,\n"
                    + "	rec.INGREDIENTE6,\n"
                    + "	rec.INGREDIENTE7\n"
                    + "FROM \n"
                    + "	pcprodut pr \n"
                    + "JOIN pcembalagem emb ON pr.CODPROD = emb.CODPROD\n"
                    + "JOIN PCINFEXTRA rec ON emb.CODINFEXTRABAL = rec.CODIGO\n"
                    + "WHERE \n"
                    + "	emb.CODFILIAL = '" + getLojaOrigem() + "' AND \n"
                    + "	emb.CODINFEXTRABAL IS NOT NULL \n"
                    + "ORDER BY \n"
                    + "	pr.CODPROD"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();
                while (rst.next()) {

                    ReceitaBalancaIMP imp = receitas.get(rst.getString("idreceita"));

                    if (imp == null) {
                        imp = new ReceitaBalancaIMP();
                        imp.setId(rst.getString("idreceita"));
                        imp.setDescricao(rst.getString("descreceita"));
                        imp.setReceita(
                                rst.getString("INGREDIENTE1") == null ? "" : rst.getString("INGREDIENTE1") + " "
                                + rst.getString("INGREDIENTE2") == null ? "" : rst.getString("INGREDIENTE2") + " "
                                + rst.getString("INGREDIENTE3") == null ? "" : rst.getString("INGREDIENTE3") + " "
                                + rst.getString("INGREDIENTE4") == null ? "" : rst.getString("INGREDIENTE4") + " "
                                + rst.getString("INGREDIENTE5") == null ? "" : rst.getString("INGREDIENTE5") + " "
                                + rst.getString("INGREDIENTE6") == null ? "" : rst.getString("INGREDIENTE6") + " "
                                + rst.getString("INGREDIENTE7") == null ? "" : rst.getString("INGREDIENTE7"));
                        receitas.put(imp.getId(), imp);
                    }

                    imp.getProdutos().add(rst.getString("CODPROD"));
                }

                return new ArrayList<>(receitas.values());
            }
        }
    }

    private void testarDatasDaVenda() throws NullPointerException {
        if (this.dataVendaInicial == null && this.dataVendaFinal == null) {
            throw new NullPointerException("Por favor informe o intervalo das vendas a serem importadas");
        }
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        testarDatasDaVenda();
        return new WinthorVendaIterator(getLojaOrigem(), this.dataVendaInicial, this.dataVendaFinal);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        testarDatasDaVenda();
        return new WinthorVendaItemIterator(getLojaOrigem(), this.dataVendaInicial, this.dataVendaFinal);
    }

}
