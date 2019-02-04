/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class CerebroDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Cerebro";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.codigo_grupo as merc1,\n"
                    + "m1.descricao as desc_merc1,\n"
                    + "coalesce(m2.codigo_subgrupo, '1') as merc2,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc3\n"
                    + "from grupos_produto m1\n"
                    + "left join subgrupos_produto m2\n"
                    + "    on m2.codigo_grupo = m1.codigo_grupo\n"
                    + "order by m1.codigo_grupo,  m2.codigo_subgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
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
                    + "p.codigo_produto, \n"
                    + "p.pesavel,\n"
                    + "p.codigo_barra,\n"
                    + "p.codigo_grupo,\n"
                    + "p.codigo_subgrupo, \n"
                    + "p.descricao, \n"
                    + "p.preco_venda,\n"
                    + "p.custo_atual,\n"
                    + "p.unidade_saida,\n"
                    + "p.validade,\n"
                    + "p.peso,\n"
                    + "p.codigo_tributo,\n"
                    + "p.status,\n"
                    + "p.cst,\n"
                    + "t.codigo_tributo as cod_trib,\n"
                    + "t.descricao as icms_desc,\n"
                    + "t.cst_icms as cst_icms_saida,\n"
                    + "t.icms_saida as icms_saida,\n"
                    + "t.reducao_saida as red_saida,\n"
                    + "t.cst_icms_ent as cst_icms_ent,\n"
                    + "t.icms_entrada as icms_ent,\n"
                    + "t.reducao_entrada as red_ent,\n"
                    + "p.cst_pis,\n"
                    + "p.cst_cofins,\n"
                    + "p.cst_pis_ent,\n"
                    + "p.cst_cofins_ent,\n"
                    + "p.ncm, \n"
                    + "p.cest\n"
                    + "from produtos p\n"
                    + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.seteBalanca("T".equals(rst.getString("pesavel")));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade_saida"));
                    imp.setCodMercadologico1(rst.getString("codigo_grupo"));
                    imp.setCodMercadologico2(rst.getString("codigo_subgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("custo_atual"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins_ent"));
                    imp.setIcmsCstSaida(rst.getInt("cst_icms_saida"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_saida"));
                    imp.setIcmsReducaoSaida(rst.getDouble("red_saida"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_icms_ent"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_ent"));
                    imp.setIcmsReducao(rst.getDouble("red_ent"));
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
                    "select\n"
                    + "codigo_produto,\n"
                    + "codigo_barra,\n"
                    + "quantidade\n"
                    + "from produtos_codigo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
