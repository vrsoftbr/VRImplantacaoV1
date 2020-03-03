/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class RKSoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(WeberDAO.class.getName());

    @Override
    public String getSistema() {
        return "RK Software";
    }

    private String Encoding = "WIN1252";

    public void setEncoding(String Encoding) {
        this.Encoding = Encoding == null ? "WIN1252" : Encoding;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select fl_codigo as id, (fl_fantasia||' - '||fl_cgc) as fantasia from filiais order by fl_codigo"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
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
                    "select\n"
                    + "    t.tr_codigo as id,\n"
                    + "    t.tr_nome as descricao,\n"
                    + "    tr.dt_tributo as situacaotributaria,\n"
                    + "    tr.dt_codstrib as csticms,\n"
                    + "    coalesce(tr.dt_percicm, 0) as aliqicms,\n"
                    + "    coalesce(tr.dt_reduicm, 0) as reducaoicms,\n"
                    + "    coalesce(tr.dt_cod_beneficio_fiscal, '') as codigobeneficio\n"
                    + "from tributacao t\n"
                    + "inner join detltrib tr on tr.dt_tributacao = t.tr_codigo\n"
                    + "order by t.tr_codigo"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao")
                            + rs.getString("csticms") + " "
                            + rs.getString("aliqicms") + " "
                            + rs.getString("reducaoicms")));
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
                    "select distinct\n"
                    + "    m1.gr_codigo as merc1,\n"
                    + "    m1.gr_nome as desc_merc1,\n"
                    + "    m2.sg_codigo as merc2,\n"
                    + "    m2.sg_nome as desc_merc2\n"
                    + "from produtos p\n"
                    + "inner join grupos m1 on m1.gr_codigo = p.pr_grupo\n"
                    + "inner join subgrupos m2 on m2.sg_codigo = p.pr_subgrupo\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select\n"
                    + "    p.pr_codigo as id,\n"
                    + "    p.pr_codbarra as ean,\n"
                    + "    p.pr_balvalid as validade,\n"
                    + "    p.pr_nome as descricao,\n"
                    + "    p.pr_grupo as merc1,\n"
                    + "    p.pr_subgrupo as merc2,\n"
                    + "    p.pr_unidade as tipoembalagem,\n"
                    + "    p.pr_pbruto as pesobruto,\n"
                    + "    p.pr_pliquid as pesoliquido,\n"
                    + "    case p.pr_inativo when 'N' then 1 else 0 end ativo,\n"
                    + "    p.pr_ncm as ncm,\n"
                    + "    p.pr_codgia as gia,\n"
                    + "    p.pr_cest as cest,\n"
                    + "    p.pr_tribut as tributacao,\n"
                    + "    p.pr_precocust as custo,\n"
                    + "    p.pr_precovend as precovenda,\n"
                    + "    p.pr_percaplic as margem,\n"
                    + "    p.pr_dtcadastro as datacadastro,\n"
                    + "    p.pr_tribut as tributacao, \n"
                    + "    t.tr_codigo as id_tributacao,\n"
                    + "    t.tr_nome as descricaotributacao,\n"
                    + "    tr.dt_tributo as situacaotributaria,\n"
                    + "    tr.dt_codstrib as csticms,\n"
                    + "    coalesce(tr.dt_percicm, 0) as aliqicms,\n"
                    + "    coalesce(tr.dt_reduicm, 0) as reducaoicms,\n"
                    + "    coalesce(tr.dt_cod_beneficio_fiscal, '') as codigobeneficio\n"
                    + "from produtos p\n"
                    + "left join tributacao t on t.tr_codigo = p.pr_tribut\n"
                    + "inner join detltrib tr on tr.dt_tributacao = t.tr_codigo\n"
                    + "order by p.pr_codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCodigoGIA(rst.getString("gia"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsDebitoId(rst.getString("id_tributacao"));
                    imp.setIcmsCreditoId(rst.getString("id_tributacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
