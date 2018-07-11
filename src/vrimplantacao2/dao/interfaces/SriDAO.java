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
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SriDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SRI";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_aliquota as codigo,\n"
                    + "percentual as descricao,\n"
                    + "valor as percentual\n"
                    + "from aliquota\n"
                    + "where ativo = 'S'"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "empresa as id,\n"
                    + "fantasia as nome\n"
                    + "from empresa"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.cod_grupo as merc1, m1.descricao as merc1_descricao,\n"
                    + "m2.cod_subgrupo as merc2, m2.descricao as merc2_descricao,\n"
                    + "'1' as merc3, m2.descricao as merc3_descricao\n"
                    + "from grupo_prod m1\n"
                    + "inner join subgrupo_prod m2 on m2.cod_grupo = m1.cod_grupo\n"
                    + "where m1.cod_grupo > 0\n"
                    + "and m2.cod_subgrupo > 0\n"
                    + "and m1.empresa = " + getLojaOrigem() + "\n"
                    + "and m2.empresa = " + getLojaOrigem() + "\n"
                    + "order by m1.cod_grupo, m2.cod_subgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
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
                    + "cod_interno,\n"
                    + "cod_produto,\n"
                    + "descricao,\n"
                    + "cod_grupo,\n"
                    + "cod_subgrupo,\n"
                    + "bruto,\n"
                    + "liquido,\n"
                    + "estoque,\n"
                    + "minimo,\n"
                    + "custo,\n"
                    + "venda,\n"
                    + "icms_in,\n"
                    + "icms_out,\n"
                    + "st,\n"
                    + "st_out,\n"
                    + "aliquota,\n"
                    + "data_cadastro,\n"
                    + "balanca,\n"
                    + "bal_validade,\n"
                    + "cod_ncm,\n"
                    + "cest,\n"
                    + "cstpc,\n"
                    + "cstpc_entrada,\n"
                    + "cod_receita_pis\n"
                    + "inativo,\n"
                    + "markup\n"
                    + "from produto\n"
                    + "where empresa = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                }
            }
        }
        return null;
    }
}
