package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.SituacaoTransacaoConveniado;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Leandro
 */
public class SysPdvDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(SysPdvDAO.class.getName());
    
    private TipoConexao tipoConexao;
    private String complementoSistema = "";
    public String FZDCOD = "";
    public String v_pahtFileXls;
    private boolean gerarEanAtacado = false;
    private boolean soAtivos = false;
    private Date dtOfertas;
    private boolean ignorarEnviaBalanca = false;
    private boolean utilizarPropesvarNaBalanca = false;
    private boolean usarOfertasDoEncarte = false;
    private boolean removerDigitoDaBalanca = false;
    private Set<String> finalizadorasRotativo;
    private Set<String> finalizadorasCheque;

    public void setRemoverDigitoDaBalanca(boolean removerDigitoDaBalanca) {
        this.removerDigitoDaBalanca = removerDigitoDaBalanca;
    }
    
    public void setFinalizadorasRotativo(Set<String> finalizadorasRotativo) {
        this.finalizadorasRotativo = finalizadorasRotativo;
    }

    public void setFinalizadorasCheque(Set<String> finalizadorasCheque) {
        this.finalizadorasCheque = finalizadorasCheque;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.NCM,
                OpcaoProduto.ATIVO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.CEST,
                OpcaoProduto.ATACADO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.FORCAR_ATUALIZACAO
        ));
    }
    
    public void setGerarEanAtacado(boolean gerarEanAtacado) {
        this.gerarEanAtacado = gerarEanAtacado;
    }

    public void setTipoConexao(TipoConexao tipoConexao) {
        this.tipoConexao = tipoConexao;
    }

    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }

    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + this.tipoConexao.getSistema();
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.seccod, \n"
                    + "	m1.secdes, \n"
                    + "	m2.grpcod, \n"
                    + "	m2.grpdes, \n"
                    + "	m3.sgrcod, \n"
                    + "	m3.sgrdes \n"
                    + "from \n"
                    + "	secao as m1 \n"
                    + "	left join grupo as m2 on \n"
                    + "		m2.seccod = m1.seccod \n"
                    + "	left join subgrupo as m3 on \n"
                    + "		m3.seccod = m1.seccod and\n"
                    + "		m3.grpcod = m2.grpcod \n"
                    + "order by \n"
                    + "	m1.seccod,\n"
                    + "	m2.grpcod,\n"
                    + "	m3.sgrcod"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("seccod"));
                    imp.setMerc1Descricao(rst.getString("secdes"));
                    imp.setMerc2ID(rst.getString("grpcod"));
                    imp.setMerc2Descricao(rst.getString("grpdes"));
                    
                    if("".equals(rst.getString("sgrcod"))){
                        imp.setMerc3ID(rst.getString("grpcod"));
                        imp.setMerc3Descricao(rst.getString("grpdes"));
                    }
                    else{
                        imp.setMerc3ID(rst.getString("sgrcod"));
                        imp.setMerc3Descricao(rst.getString("sgrdes"));
                    }
                    

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	procodsim,\n"
                    + "	similaresdes\n"
                    + "from\n"
                    + "	similares"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("procodsim"));
                    imp.setDescricao(rst.getString("similaresdes"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	t.trbid,\n" +
                    "	t.trbdes,\n" +
                    "	t.trbtabb cst,\n" +
                    "	t.trbalq aliquota,\n" +
                    "	t.trbred reducao,\n" +
                    "	coalesce(t.TRBALQFCP, 0) fcp\n" +
                    "from\n" +
                    "	TRIBUTACAO t\n" +
                    "where\n" +
                    "	t.trbid in (select trbid from produto)\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("trbid"),
                            String.format("%s - FCP %.2f", rst.getString("trbdes"), rst.getDouble("fcp")),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        
        return result;
    }

    public void setSoAtivos(boolean soAtivos) {
        this.soAtivos = soAtivos;
    }

    public void setDtOfertas(Date dtOfertas) {
        this.dtOfertas = dtOfertas;
    }

    public void setUtilizarPropesvarNaBalanca(boolean utilizarPropesvarNaBalanca) {
        this.utilizarPropesvarNaBalanca = utilizarPropesvarNaBalanca;
    }

    public void setUsarOfertasDoEncarte(boolean usarOfertasDoEncarte) {
        this.usarOfertasDoEncarte = usarOfertasDoEncarte;
    }

    private static class Ean {

        public String idProduto;
        public String ean;
        public int qtdEmbalagem;

        public Ean(String idProduto, String ean, int qtdEmbalagem) {
            this.idProduto = idProduto;
            this.ean = ean;
            this.qtdEmbalagem = qtdEmbalagem;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.idProduto);
            hash = 97 * hash + Objects.hashCode(this.ean);
            hash = 97 * hash + (int) (Double.doubleToLongBits(this.qtdEmbalagem) ^ (Double.doubleToLongBits(this.qtdEmbalagem) >>> 32));
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
            final Ean other = (Ean) obj;
            if (!Objects.equals(this.idProduto, other.idProduto)) {
                return false;
            }
            if (!Objects.equals(this.ean, other.ean)) {
                return false;
            }
            return Double.doubleToLongBits(this.qtdEmbalagem) == Double.doubleToLongBits(other.qtdEmbalagem);
        }

    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            Map<String, int[]> piscofins = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.procod id_produto,\n"
                    + "    pis.impfedst piscofins_entrada,\n"
                    + "    pis.impfedstsai piscofins_saida\n"
                    + "from\n"
                    + "    (select\n"
                    + "         pispro.procod,\n"
                    + "         min(pispro.impfedsim) impfedsim\n"
                    + "     from\n"
                    + "         impostos_federais pis\n"
                    + "         join impostos_federais_produto pispro on\n"
                    + "            pis.impfedsim = pispro.impfedsim\n"
                    + "            and pis.impfedtip = 'P'\n"
                    + "     group by\n"
                    + "         pispro.procod) p\n"
                    + "     join impostos_federais pis on\n"
                    + "        p.impfedsim = pis.impfedsim"
            )) {
                while (rst.next()) {
                    piscofins.put(
                            rst.getString("id_produto"),
                            new int[]{
                                Utils.stringToInt(rst.getString("piscofins_entrada")),
                                Utils.stringToInt(rst.getString("piscofins_saida"))
                            }
                    );
                }
            }

            Map<String, Set<Ean>> eans = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    procod id_produto,\n"
                    + "    procod ean,\n"
                    + "    1 qtdembalagem\n"
                    + "from\n"
                    + "    produto\n"
                    + "union\n"
                    + "select\n"
                    + "    procod id_produto,\n"
                    + "    procodaux ean,\n"
                    + "    coalesce(profatormult, 1) qtdembalagem\n"
                    + "from\n"
                    + "    produtoaux\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    long ean = Utils.stringToLong(rst.getString("ean"), -2);
                    if (ean > 999999 || !String.valueOf(rst.getString("ean")).equals(rst.getString("id_produto"))) {

                        Set<Ean> ea = eans.get(rst.getString("id_produto"));

                        if (ea == null) {
                            ea = new HashSet<>();
                        }

                        ea.add(
                                new Ean(
                                        rst.getString("id_produto"),
                                        rst.getString("ean"),
                                        Math.round(rst.getFloat("qtdembalagem"))
                                )
                        );

                        eans.put(
                                rst.getString("id_produto"),
                                ea
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    p.procod id,\n"
                    + "    p.prodes descricaocompleta,\n"
                    + "    p.prodesrdz descricaoreduzida,\n"
                    + "    p.seccod merc1,\n"
                    + "    nullif(p.grpcod, '000') merc2,\n"
                    + "    nullif(p.sgrcod, '000') merc3,\n"
                    + "    p.proestmin estoqueminimo,\n"
                    + "    p.proestmax estoquemaximo,\n"
                    + "    est.estatu estoque,\n"
                    + "    p.proncm ncm,\n"
                    + "    case when p.proforlin = 'S' then 0 else 1 end situacaocadastro,\n"
                    + "    p.proprccst custocomimposto,\n"
                    + "    p.proprccst custosemimposto,\n"
                    + "    p.prodatcadinc datacadastro,\n"
                    + "    p.proiteemb qtdembalagem,\n"
                    + "    round(((proprcvdavar / case when p.proprccst = 0.00 then 1 else p.proprccst end) - 1) * (100),2) margem,\n"
                    + "    p.promrg1 as margem2, \n"
                    + "    proprcvdavar precovenda,\n"
                    + "    items.procodsim id_familiaproduto,\n"
                    + "    p.propesbrt pesobruto,\n"
                    + "    p.propesliq pesoliquido,\n"
                    + "    case p.propesvar\n"
                    + "    when 'S' then 'KG'\n"
                    + "    when 'P' then 'KG'\n"
                    + "    else 'UN' end as tipoembalagem,\n"
                    + "    p.prounid, \n"
                    + "    case when p.proenvbal = 'S' then 1 else 0 end e_balanca,\n"
                    + "    coalesce(p.provld, 0) validade,\n"
                    + "    p.trbid,\n"
                    + "    p.procest cest,\n"
                    + "    p.natcodigo piscofins_natrec\n"
                    + "FROM \n"
                    + "    produto p\n"
                    + "    LEFT JOIN item_similares items ON \n"
                    + "        items.procod = p.procod\n"
                    + "    left join estoque est on\n"
                    + "        est.PROCOD = p.PROCOD\n"
                    + (soAtivos ? "where p.proforlin = 'N'\n" : "")
                    + "ORDER BY \n"
                    + "    p.procod"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                System.out.println(eans.size());
                while (rst.next()) {
                    
                    String id = rst.getString("id");
                    if (id != null && id.trim().isEmpty())
                        id = null;
                    Set<Ean> e = eans.get(id);

                    if (e != null) {
                        for (Ean ean : e) {

                            ProdutoIMP imp = new ProdutoIMP();

                            imp.setImportSistema(getSistema());
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportId(rst.getString("id"));
                            imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                            imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                            imp.setDescricaoGondola(imp.getDescricaoCompleta());
                            imp.setCodMercadologico1(rst.getString("merc1"));
                            imp.setCodMercadologico2(rst.getString("merc2"));
 
                            imp.setCodMercadologico3("".equals(rst.getString("merc3")) ?  
                                                    rst.getString("merc2") : rst.getString("merc3"));
                                                        
                            int plu;
                            if (removerDigitoDaBalanca) {
                                plu = ProdutoBalancaDAO.TipoConversao.REMOVER_DIGITO.convert(ean.ean);
                            } else {
                                plu = ProdutoBalancaDAO.TipoConversao.SIMPLES.convert(ean.ean);
                            }
                            final boolean isBalancaNoSysPdv = rst.getBoolean("e_balanca");
                                                        
                            if (utilizarPropesvarNaBalanca) {                                
                                if ("KG".equals(rst.getString("tipoembalagem"))) {
                                    imp.seteBalanca(true);
                                    imp.setQtdEmbalagem(1);
                                    imp.setEan(String.valueOf(plu));
                                } else {
                                    imp.seteBalanca(isBalancaNoSysPdv);
                                    imp.setQtdEmbalagem(ean.qtdEmbalagem);
                                    if (plu > 0 && plu <= 999999) {
                                        imp.setEan(String.valueOf(plu));
                                    } else {
                                        imp.setEan(ean.ean);
                                    }
                                }
                                imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                                imp.setValidade(Utils.stringToInt(rst.getString("validade")));
                            } else {
                                ProdutoBalancaVO bal = balanca.get(plu);
                                if (bal != null) {
                                    imp.seteBalanca(true);
                                    imp.setEan(String.valueOf(bal.getCodigo()));
                                    imp.setQtdEmbalagem(1);
                                    imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                                    imp.setValidade(bal.getValidade());
                                } else if (isBalancaNoSysPdv) {
                                    imp.seteBalanca(true);
                                    imp.setEan(String.valueOf(plu));
                                    imp.setQtdEmbalagem(1);
                                    imp.setTipoEmbalagem(rst.getString("prounid"));
                                    imp.setValidade(Utils.stringToInt(rst.getString("validade")));
                                } else {
                                    if (balanca.isEmpty()) {
                                        imp.seteBalanca(isBalancaNoSysPdv);
                                    } else {
                                        imp.seteBalanca(false);
                                    }
                                    imp.setEan(ean.ean);
                                    imp.setQtdEmbalagem(ean.qtdEmbalagem);
                                    imp.setTipoEmbalagem(rst.getString("prounid"));
                                    imp.setValidade(Utils.stringToInt(rst.getString("validade")));
                                }
                            }

                            imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                            imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                            imp.setEstoque(rst.getDouble("estoque"));
                            imp.setNcm(rst.getString("ncm"));
                            imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                            imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                            imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                            imp.setDataCadastro(rst.getDate("datacadastro"));
                            
                            String qtdEmb = rst.getString("qtdembalagem");
                            
                            if(qtdEmb != null && !"".equals(qtdEmb)) {
                                if(qtdEmb.length() > 6) {
                                    imp.setQtdEmbalagemCotacao(0);
                                } else {
                                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                                }
                            }
                            
                            if (rst.getDouble("margem2") > 99999999) {
                                imp.setMargem(0);
                            } else {
                                imp.setMargem(rst.getDouble("margem2"));
                            }
                            
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                            imp.setPesoBruto(rst.getDouble("pesobruto"));
                            imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                            imp.setIcmsCreditoId(rst.getString("trbid"));
                            imp.setIcmsCreditoForaEstadoId(rst.getString("trbid"));
                            imp.setIcmsDebitoId(rst.getString("trbid"));
                            imp.setIcmsDebitoForaEstadoId(rst.getString("trbid"));
                            imp.setIcmsDebitoForaEstadoNfId(rst.getString("trbid"));
                            imp.setIcmsConsumidorId(rst.getString("trbid"));

                            imp.setCest(rst.getString("cest"));

                            int[] pis = piscofins.get(rst.getString("id"));

                            if (pis != null) {
                                imp.setPiscofinsCstCredito(pis[0]);
                                imp.setPiscofinsCstDebito(pis[1]);
                                imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));
                            }

                            result.add(imp);

                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (gerarEanAtacado) {
            try (Statement stm = tipoConexao.getConnection().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "procod, "
                        + "proprc1, "
                        + "proprc2, "
                        + "proqtdminprc2 "
                        + "from produto\n"
                        + "where proqtdminprc2 > 1"
                )) {
                    while (rst.next()) {

                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));
                        
                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("procod"));
                            imp.setEan("99999" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rst.getInt("proqtdminprc2"));
                            result.add(imp);
                        }
                    }
                }
            }
            
            try (Statement stm = tipoConexao.getConnection().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "procod,\n"
                        + "proprc1,\n"
                        + "proprc3,\n"
                        + "proqtdminprc3\n"
                        + "from produto\n"
                        + "where proqtdminprc3 > 1"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("procod"));
                            imp.setEan("88888" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rst.getInt("proqtdminprc3"));
                            result.add(imp);
                        }
                    }
                }
            }
        } else {
            result = getProdutos();
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = tipoConexao.getConnection().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "procod, "
                        + "proprc1, "
                        + "proprc2, "
                        + "proqtdminprc2 "
                        + "from produto\n"
                        + "where proqtdminprc2 > 1"
                )) {
                    while (rst.next()) {

                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("procod"));
                            imp.setEan("99999" + String.valueOf(codigoAtual));
                            imp.setPrecovenda(rst.getDouble("proprc1"));
                            imp.setAtacadoPreco(rst.getDouble("proprc2"));
                            imp.setQtdEmbalagem(rst.getInt("proqtdminprc2"));
                            result.add(imp);
                        }
                    }
                }
            }
            
            try (Statement stm = tipoConexao.getConnection().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "procod,\n"
                        + "proprc1,\n"
                        + "proprc3,\n"
                        + "proqtdminprc3\n"
                        + "from produto\n"
                        + "where proqtdminprc3 > 1"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("procod"));
                            imp.setEan("88888" + String.valueOf(codigoAtual));
                            imp.setPrecovenda(rst.getDouble("proprc1"));
                            imp.setAtacadoPreco(rst.getDouble("proprc3"));
                            imp.setQtdEmbalagem(rst.getInt("proqtdminprc3"));
                            result.add(imp);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	procod as id_produto,\n"
                    + "	forcod as id_fornecedor,\n"
                    + "	prfreffor as ref,\n"
                    + "	prfqtd as qtdemb\n"
                    + "from\n"
                    + "	produto_fornecedor")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("ref"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdemb"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.forcod id,\n"
                    + "    f.fordes razao,\n"
                    + "    f.forfan fantasia,\n"
                    + "    f.forcgc cnpj,\n"
                    + "    f.forcgf inscricaoestadual,\n"
                    + "    f.forend endereco,\n"
                    + "    f.fornum numero,\n"
                    + "    f.forcmp complemento,\n"
                    + "    f.forbai bairro,\n"
                    + "    f.forcodibge ibge_municipio,\n"
                    + "    f.forcep cep,\n"
                    + "    f.fortel telefone,\n"
                    + "    " + (tipoConexao == TipoConexao.FIREBIRD ? "current_date" : "getdate()") + " datacadastro,\n"
                    + "    f.forobs observacao,\n"
                    + "    f.forprz prazoentrega,\n"
                    + "    f.forcon contato,\n"
                    + "    f.forfax fax\n"
                    + "from\n"
                    + "    fornecedor f\n"
                    + "where\n"
                    + "    f.forcod != '0000'\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(Utils.stringToInt(rst.getString("ibge_municipio")));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    String contato = Utils.acertarTexto(rst.getString("contato"));
                    if (!"".equals(contato)) {
                        imp.addContato(contato, rst.getString("telefone"), null, TipoContato.COMERCIAL, "");
                    }
                    imp.addTelefone("FAX", rst.getString("fax"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*@Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.clirgexp emissor,\n"
                    + "    c.clides razao,\n"
                    + "    c.clifan fantasia,\n"
                    + "    case when st.stablq = 'S' then 1 else 0 end bloqueado,\n"
                    + "    c.clidtblo databloqueio,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.cliestciv estadocivil,\n"
                    + "    c.clidtcad datacadastro,\n"
                    + "    c.clidtnas datanascimento,\n"
                    + "    c.clisex sexo,\n"
                    + "    c.cliemptrb empresa,\n"
                    + "    c.cliempend empresa_endereco,\n"
                    + "    c.cliemptel empresa_telefone,\n"
                    + "    c.cliempcar empresa_cargo,\n"
                    + "    c.clisal empresa_salario,\n"
                    + "    c.clilimcre valorlimite,\n"
                    + "    c.clipai nomepai,\n"
                    + "    c.climae nomemae,\n"
                    + "    c.cliobs observacao,\n"
                    + "    c.clidiafec diavencimento,\n"
                    + "    c.clitel telefone,\n"
                    + "    c.clitel2 telefone2,\n"
                    + "    c.cliemail email,\n"
                    + "    c.clifax fax,\n"
                    + "    c.cliendcob cob_endereco,\n"
                    + "    c.clinumcob cob_numero,\n"
                    + "    c.clicmp cob_complemento,\n"
                    + "    c.clibai cob_bairro,\n"
                    + "    c.clicidcob cob_cidade,\n"
                    + "    c.cliestcob cob_estado,\n"
                    + "    c.clicepcob cob_cep,\n"
                    + "    c.cliprz prazopagamento,\n"
                    + "    c.cliinscmun inscricaomunicipal,\n"
                    + "    c.clilimcre2 limitecompra\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "    left join status st on\n"
                    + "        c.stacod = st.stacod\n"
                    + "where\n"
                    + "    c.clicod != '000000000000000'\n"
                    + "order by\n"
                    + "    c.clicod"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(Integer.parseInt(Utils.formataNumero(rst.getString("ibge_municipio"))));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }*/

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.clirgexp emissor,\n"
                    + "    c.clides razao,\n"
                    + "    c.clifan fantasia,\n"
                    + "    case when st.stablq = 'S' then 1 else 0 end bloqueado,\n"
                    + "    c.clidtblo databloqueio,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.cliestciv estadocivil,\n"
                    + "    c.clidtcad datacadastro,\n"
                    + "    c.clidtnas datanascimento,\n"
                    + "    c.clisex sexo,\n"
                    + "    c.cliemptrb empresa,\n"
                    + "    c.cliempend empresa_endereco,\n"
                    + "    c.cliemptel empresa_telefone,\n"
                    + "    c.cliempcar empresa_cargo,\n"
                    + "    c.clisal empresa_salario,\n"
                    + "    c.clilimcre valorlimite,\n"
                    + "    c.clipai nomepai,\n"
                    + "    c.climae nomemae,\n"
                    + "    c.cliobs observacao2,\n"
                    + "    c.clidiafec diavencimento,\n"
                    + "    c.clitel telefone,\n"
                    + "    c.clitel2 telefone2,\n"
                    + "    c.cliemail email,\n"
                    + "    c.clifax fax,\n"
                    + "    c.cliendcob cob_endereco,\n"
                    + "    c.clinumcob cob_numero,\n"
                    + "    c.clicmp cob_complemento,\n"
                    + "    c.clibai cob_bairro,\n"
                    + "    c.clicidcob cob_cidade,\n"
                    + "    c.cliestcob cob_estado,\n"
                    + "    c.clicepcob cob_cep,\n"
                    + "    c.cliprz prazopagamento,\n"
                    + "    c.cliinscmun inscricaomunicipal,\n"
                    + "    c.clilimcre2 limitecompra\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "    left join status st on\n"
                    + "        c.stacod = st.stacod\n"
                    + "where\n"
                    + "    c.clicod != '000000000000000'\n"
                    + "order by\n"
                    + "    c.clicod"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("emissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setDataBloqueio(rst.getDate("databloqueio"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(Integer.parseInt(Utils.formataNumero(rst.getString("ibge_municipio"))));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    switch (Utils.acertarTexto(rst.getString("estadocivil"))) {
                        case "S":
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                            break;
                        case "O":
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                            break;
                        default:
                            imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                            break;
                    }
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    switch (Utils.acertarTexto(rst.getString("sexo"))) {
                        case "F":
                            imp.setSexo(TipoSexo.FEMININO);
                            break;
                        default:
                            imp.setSexo(TipoSexo.MASCULINO);
                            break;
                    }
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresa_endereco"));
                    imp.setEmpresaTelefone(rst.getString("empresa_telefone"));
                    imp.setCargo(rst.getString("empresa_cargo"));
                    imp.setSalario(rst.getDouble("empresa_salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setDiaVencimento(Utils.stringToInt(rst.getString("diavencimento")));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaNumero(rst.getString("cob_numero"));
                    imp.setCobrancaComplemento(rst.getString("cob_complemento"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_cidade"));
                    imp.setCobrancaUf(rst.getString("cob_estado"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));
                    imp.setPrazoPagamento(rst.getInt("prazopagamento"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setLimiteCompra(rst.getDouble("limitecompra"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    public static class FinalizadoraRecord {
        
        public String id;
        public String descricao;
        public boolean selected = false;

        public FinalizadoraRecord(String id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

    }

    public List<FinalizadoraRecord> getFinalizadora() throws Exception {
        List<FinalizadoraRecord> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select FZDCOD, FZDDES from finalizadora order by FZDCOD"
            )) {
                while (rst.next()) {
                    result.add(new FinalizadoraRecord(
                            rst.getString("FZDCOD"),
                            rst.getString("FZDDES")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            StringBuilder builder = new StringBuilder();
            for(Iterator<String> iterator = this.finalizadorasRotativo.iterator(); iterator.hasNext();) {
                builder
                        .append("'")
                        .append(iterator.next())
                        .append("'");
                if (iterator.hasNext())
                    builder.append(",");
            }
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    CTRID,\n"
                    + "    ctrnum,\n"
                    + "    clicod,\n"
                    + "    cxanum,\n"
                    + "    ctrdatemi,\n"
                    + "    ctrdatvnc,\n"
                    + "    ctrvlrdev,\n"
                    + "    ctrobs\n"
                    + "FROM CONTARECEBER\n"
                    + "WHERE \n"
                    + "COALESCE(ctrvlrdev,0) > 0 "
                    + "AND FZDCOD IN (" + builder.toString() + ") "
                    + "union all\n"
                    + "SELECT\n"
                    + "     CTRID,\n"
                    + "     ctrnum,\n"
                    + "     clicod,\n"
                    + "     cxanum,\n"
                    + "     ctrdatemi,\n"
                    + "     ctrdatvnc,\n"
                    + "     ctrvlrdev,\n"
                    + "     ctrobs\n"
                    + "FROM CONTARECEBER\n"
                    + "WHERE COALESCE(ctrvlrdev,0) > 0\n"
                    + "AND FZDCOD is null\n"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("CTRID"));
                    imp.setNumeroCupom(rst.getString("ctrnum"));
                    imp.setIdCliente(rst.getString("clicod"));
                    imp.setEcf(rst.getString("cxanum"));
                    imp.setDataEmissao(rst.getDate("ctrdatemi"));
                    imp.setDataVencimento(rst.getDate("ctrdatvnc"));
                    imp.setValor(rst.getDouble("ctrvlrdev"));
                    imp.setObservacao(rst.getString("ctrobs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement st = tipoConexao.getConnection().createStatement()) {            
            try (ResultSet rs = st.executeQuery(
                    "SELECT \n" +
                    "	transacao.trnseq sequencia,\n" +
                    "	transacao.cxanum numerocaixa,\n" +
                    "	transacao.trndat data,\n" +
                    "	transacao.trnseqeqp,\n" +
                    "	transacao.cxanumeqp,\n" +
                    "	transacao.funcod,\n" +
                    "	transacao.trnfunaut,\n" +
                    "	transacao.clicod,\n" +
                    "	cliente.clides,\n" +
                    "	finalizacao.fzdtext1 banco,\n" +
                    "	finalizacao.fzdtext2 agencia,\n" +
                    "	finalizacao.fzdtext3 conta,\n" +
                    "	finalizacao.fzdtext4 numerocheque,\n" +
                    "	finalizacao.fzddatven datavencimento,\n" +
                    "	finalizacao.fzdvlr valor,\n" +
                    "	finalizacao.fzdesp,\n" +
                    "	finalizacao.fzdcod,\n" +
                    "	finalizadora.fzddes observacao,\n" +
                    "	finalizadora.fzdlercmc7,\n" +
                    "	iteplapag.ipptxt1,\n" +
                    "	iteplapag.ipptxt2,\n" +
                    "	iteplapag.ipptxt3,\n" +
                    "	iteplapag.ipptxt4,\n" +
                    "	iteplapag.ippvlrlan,\n" +
                    "	iteplapag.ippdatven\n" +
                    "FROM transacao\n" +
                    "	LEFT OUTER JOIN finalizacao ON (\n" +
                    "		transacao.trnseq=finalizacao.trnseq\n" +
                    "		AND transacao.trndat=finalizacao.trndat\n" +
                    "		AND transacao.cxanum=finalizacao.cxanum\n" +
                    "	)\n" +
                    "	LEFT OUTER JOIN iteplapag ON (\n" +
                    "		finalizacao.trnseq=iteplapag.trnseq\n" +
                    "		AND finalizacao.trndat=iteplapag.trndat\n" +
                    "		AND finalizacao.cxanum=iteplapag.cxanum\n" +
                    "		AND finalizacao.fzdseq=iteplapag.seqfzd)\n" +
                    "	LEFT OUTER JOIN cliente ON (\n" +
                    "		cliente.clicod=transacao.clicod\n" +
                    "	)\n" +
                    "	LEFT OUTER JOIN finalizadora ON (\n" +
                    "		finalizadora.fzdcod=finalizacao.fzdcod\n" +
                    "	)\n" +
                    "WHERE \n" +
                    "	transacao.trntip <> '7'\n" +
                    "	AND finalizacao.fzdesp = '1'\n" +
                    "ORDER BY\n" +
                    "	transacao.TRNDAT, transacao.trnseq"
            )) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(
                            String.format(
                                    "%s-%s-%s",
                                    rs.getString("sequencia"),
                                    rs.getString("numerocaixa"),
                                    df.format(rs.getDate("data"))
                            )
                    );
                    imp.setEcf(rs.getString("numerocaixa"));
                    imp.setDate(rs.getDate("data"));
                    imp.setBanco(Utils.stringToInt(rs.getString("banco")));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setNumeroCheque(rs.getString("numerocheque"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            String dataOferta = new SimpleDateFormat("yyyy-MM-dd").format(dtOfertas);
            try (ResultSet rst = stm.executeQuery(
                    (
                            usarOfertasDoEncarte ?
                            "select\n"
                            + "     ep.PROCOD id_produto,\n"
                            + "     cast('" + dataOferta + "' as date) as datainicial,\n"
                            + "     cast(e.ENCDATFIM as date) datafinal,\n"
                            + "     ep.ENCPROPRCOFE precooferta\n"
                            + "from \n"
                            + "     ENCARTE_PRODUTO ep\n"
                            + "     join ENCARTE e on\n"
                            + "		ep.ENCCOD = e.ENCCOD\n"
                            + "where\n"
                            + "     e.ENCDATFIM >= '" + dataOferta + "'\n"
                            + "order by\n"
                            + "     id_produto" :
                                    
                            "select distinct\n"
                            + "    oft.procod id_produto,\n"
                            + "    oft.pprdatini datainicial,\n"
                            + "    oft.pprdatfim datafinal,\n"
                            + "    oft.pprprcprog precooferta\n"
                            + "from\n"
                            + "    preco_programado oft\n"
                            + "where\n"
                            + "    oft.pprdatfim >= '" + dataOferta + "'\n"
                            + "order by\n"
                            + "    id_produto"
                    )
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("datainicial"));
                    imp.setDataFim(rst.getDate("datafinal"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id, \n"
                    + "    c.clides razao,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.clitel telefone,\n"
                    + "    co.cnvdatini datainicio,\n"
                    + "    co.cnvdatvnc datatermino,\n"
                    + "    case when co.cnvsta = 'N' then 0 else 1 end situacaocadastro,\n"
                    + "    co.cnvdiafec diapagamento,\n"
                    + "    co.cnvdes observacao\n"
                    + "from\n"
                    + "    convenio co\n"
                    + "    join cliente c on\n"
                    + "        co.clicod = c.clicod\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbgeMunicipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataTermino(rst.getDate("datatermino"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDiaPagamento(rst.getInt("diapagamento"));
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id, \n"
                    + "    c.clides razao,\n"
                    + "    co.cnvcod id_empresa,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    co.cncsta status,\n"
                    + "    co.cnclimcre limitedecredito\n"
                    + "from\n"
                    + "    convenio_cliente co\n"
                    + "    join cliente c on\n"
                    + "        co.clicod = c.clicod\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("id_empresa"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("status")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setConvenioLimite(rst.getDouble("limitedecredito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString(""));
                    imp.setDataHora(rst.getTimestamp(""));
                    imp.setDataMovimento(rst.getDate(""));
                    imp.setEcf(rst.getString(""));
                    imp.setFinalizado(rst.getBoolean(""));
                    imp.setIdConveniado(rst.getString(""));
                    imp.setNumeroCupom(rst.getString(""));
                    imp.setObservacao(rst.getString(""));
                    imp.setSituacaoTransacaoConveniado(SituacaoTransacaoConveniado.PENDENTE);
                    imp.setValor(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT prpcod, prpfan FROM PROPRIO"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("prpcod"), rst.getString("prpfan")));
                }
            }
        }

        return result;
    }

    public static enum TipoConexao {

        FIREBIRD {
                    @Override
                    public Connection getConnection() {
                        return ConexaoFirebird.getConexao();
                    }

                    @Override
                    public String getSistema() {
                        return "SysPdv(FIREBIRD)";
                    }
                },
        SQL_SERVER {
                    @Override
                    public Connection getConnection() {
                        return ConexaoSqlServer.getConexao();
                    }

                    @Override
                    public String getSistema() {
                        return "SysPdv(SQLSERVER)";
                    }
                };

        public abstract Connection getConnection();

        public abstract String getSistema();

        public String getLojasClienteSQL() {
            return "";
        }

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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        //public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {

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
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //next.setCpf(rst.getString("cnpj"));
                        //String horaInicio = FORMAT.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        //String horaTermino = FORMAT.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        //next.setHoraInicio(TIMESTAMP.parse(horaInicio));
                        //next.setHoraTermino(TIMESTAMP.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	v.TRNSEQEQP || v.CXANUM || v.TRNDAT id_venda,\n"
                    + "	TRNSEQEQP numerocupom,\n"
                    + "	CXANUM ecf,\n"
                    + " cast(TRNDATVEN as timestamp) AS DATA,\n"
                    + "	v.CLICOD id_cliente,\n"
                    + " SUBSTRING(TRNHORINI FROM 11 FOR 6) horainicio,\n"
                    + " SUBSTRING(TRNHORFIN FROM 11 FOR 6) horatermino,\n"
                    + "	TRNVLR subtotalimpressora,\n"
                    + "	TRNSEREQP numeroserie\n"
                    + "FROM\n"
                    + "	TRANSACAO v\n"
                    + " LEFT JOIN CLIENTE c ON c.CLICOD = v.CLICOD \n"
                    + "WHERE\n"
                    + "	LOJCOD = " + idLojaCliente + "\n"
                    + "	AND TRNSEQEQP != 0\n"
                    + "	AND cast(TRNDATVEN as date) BETWEEN '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' \n"
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

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        
        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id_vendaitem"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("embalagem"));

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
                    + "	v.TRNSEQEQP || v.CXANUM || v.TRNDAT id_venda,\n"
                    + "	id id_vendaitem,\n"
                    + "	ITVSEQ item,\n"
                    + "	iv.PROCOD id_produto,\n"
                    + "	p.PRODESRDZ descricao,\n"
                    + "	ITVQTDVDA quantidade,\n"
                    + "	ITVVLRTOT total,\n"
                    + "	ITVVLRDCN desconto,\n"
                    + "	ITVVLRACR acrescimo,\n"
                    + "	ITVCODAUX ean,\n"
                    + "	ITVUNID embalagem\n"
                    + "FROM ITEVDA iv\n"
                    + "	LEFT JOIN TRANSACAO v ON v.TRNSEQ = iv.TRNSEQ AND iv.CXANUM = v.CXANUM \n"
                    + "	LEFT JOIN PRODUTO p ON p.PROCOD = iv.PROCOD \n"
                    + "WHERE \n"
                    + "	iv.LOJCOD = '" + idLojaCliente + "'\n"
                    + "	AND v.TRNSEQEQP != 0\n"
                    + "	AND v.TRNDAT BETWEEN '" + FORMAT.format(dataInicio) +
                                    "' and '" + FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,2";
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
