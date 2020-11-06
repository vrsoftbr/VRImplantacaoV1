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
import vrframework.classe.Conexao;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class MerceariaSeneDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Mercearia Sene";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_saida "
                    + "from implantacao.produtoplanilha"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_saida"),
                            rst.getString("icms_saida")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_entrada "
                    + "from implantacao.produtoplanilha"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_entrada"),
                            rst.getString("icms_entrada")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_pdv "
                    + "from implantacao.produtoplanilha"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_pdv"),
                            rst.getString("icms_pdv")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	plu as id,\n"
                    + "	ean,\n"
                    + "	descricao,\n"
                    + "	fora_linha,\n"
                    + "	saneamento,\n"
                    + "	piscofins_saida,\n"
                    + "	piscofins_entrada,\n"
                    + "	naturezareceita,\n"
                    + "	ncm,\n"
                    + "	cest,\n"
                    + "	iva,\n"
                    + "	icms_saida,\n"
                    + "	icms_entrada,\n"
                    + "	icms_pdv\n"
                    + "from implantacao.produtoplanilha"
            )) {
                while (rst.next()) {
                    
                }
            }
        }

        return null;
    }
}
