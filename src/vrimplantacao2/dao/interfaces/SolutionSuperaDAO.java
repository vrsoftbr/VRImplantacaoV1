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
public class SolutionSuperaDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SolutionSupera";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.codigo_grp as merc1,\n"
                    + "m1.grupo as desc_merc1,\n"
                    + "m2.codigo_sgp as merc2,\n"
                    + "m2.subgrupo as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "m2.subgrupo as desc_merc3\n"
                    + "from grupos m1\n"
                    + "inner join subgrupos m2 on m2.codigo_grp = m1.codigo_grp\n"
                    + "order by m1.codigo_grp, m2.codigo_sgp"
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
                    + "p.codigo_pro as id,\n"
                    + "p.codigo_grp as merc1,\n"
                    + "p.codigo_sgp as merc2,\n"
                    + "p.codigo_ean as ean,\n"
                    + "p.descricao as descricaoproduto,\n"
                    + "p.cod_ncm as ncm,\n"
                    + "p.custo_unitario as custo,\n"
                    + "p.preco_venda as preco,\n"
                    + "p.unidade_entrada as embcompra,\n"
                    + "p.unidade_venda as embvenda,\n"
                    + "p.quanti_embalagem as qtdembalagem,\n"
                    + "p.estoque,\n"
                    + "p.peso_bruto,\n"
                    + "p.peso_liquido,\n"
                    + "p.data_cadastro,\n"
                    + "p.status as situacaocadastro,\n"
                    + "p.margemlucro as margem1,\n"
                    + "p.margemlucro2 as margem2,\n"
                    + "p.estoque_max,\n"
                    + "p.produto_balanca as balanca,\n"
                    + "p.cod_nat_receita as naturezareceita,\n"
                    + "p.cest, \n"
                    + "p.cst_pis_saida,\n"
                    + "p.cst_pis_entrada,\n"
                    + "p.cst_icms_saida_interno as cst_icms_debito,\n"
                    + "p.cst_icms_saida_externo as cst_icms_debito_fora_estado,\n"
                    + "p.cst_icms_entrada_interno as cst_icms_credito,\n"
                    + "p.cst_icms_entrada_externo as cst_icms_credito_fora_estado\n"
                    + "from produtos p\n"
                    + "order by p.codigo_pro"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
