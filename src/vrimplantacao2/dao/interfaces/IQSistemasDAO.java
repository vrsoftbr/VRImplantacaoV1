/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author lucasrafael
 */
public class IQSistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "IQSistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CodigoFilial, \n"
                    + "empresa, \n"
                    + "cnpj \n"
                    + "FROM filiais\n"
                    + "ORDER BY CodigoFilial"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CodigoFilial"), rst.getString("cnpj") + " - " + rst.getString("empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "g.codigo,\n"
                    + "g.grupo AS descgrupo,\n"
                    + "sg.codigosubgrupo,\n"
                    + "sg.subgrupo AS descsubgrupo\n"
                    + "FROM grupos g\n"
                    + "INNER JOIN subgrupos sg ON sg.codigogrupo = g.codigo\n"
                    + "ORDER BY g.codigo, sg.codigosubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descgrupo"));
                    imp.setMerc2ID(rst.getString("codigosubgrupo"));
                    imp.setMerc2Descricao(rst.getString("descsubgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
