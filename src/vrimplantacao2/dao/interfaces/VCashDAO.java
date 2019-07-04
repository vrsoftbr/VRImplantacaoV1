/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author lucasrafael
 */
public class VCashDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "VCash";
    }

    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_trib,\n"
                    + "descricao\n"
                    + "from tributac\n"
                    + "order by cod_trib"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("cod_trib"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "nome\n"
                    + "from tip_prod\n"
                    + "where sub_tip = 0\n"
                    + "order by cod_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("cod_tip"));
                    imp.setDescricao(rst.getString("nome"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "sub_tip,\n"
                    + "nome\n"
                    + "from tip_prod "
                    + "where sub_tip > 0"
                    + "order by cod_tip, sub_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("cod_tip"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("sub_tip"),
                                rst.getString("nome")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "sub_tip,\n"
                    + "'1' as merc3"
                    + "nome\n"
                    + "from tip_prod "
                    + "where sub_tip > 0"
                    + "order by cod_tip, sub_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("cod_tip"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("sub_tip"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("nome")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }
}
