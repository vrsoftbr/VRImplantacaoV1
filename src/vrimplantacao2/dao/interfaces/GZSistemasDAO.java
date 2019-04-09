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
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author lucasrafael
 */
public class GZSistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "GZSistemas";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "e.grupo merc1, g.descricao merc1_desc,\n"
                    + "e.depto merc2, d.descricao merc2_desc\n"
                    + "from mercodb.estoque e\n"
                    + "inner join mercodb.grupo g on g.codigo = e.grupo\n"
                    + "inner join mercodb.depto d on d.codigo = e.depto\n"
                    + "where e.depto is not null\n"
                    + "  and e.grupo is not null\n"
                    + "order by e.grupo, e.depto"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("merc2_desc"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
