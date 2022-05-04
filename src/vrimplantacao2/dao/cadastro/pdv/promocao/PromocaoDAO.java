/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;

/**
 *
 * @author lucasrafael
 */
public class PromocaoDAO {

    public MultiMap<Integer, PromocaoVO> getPromocoes() throws Exception {
        MultiMap<Integer, PromocaoVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id "
                    + "from promocao "
                    + "order by id "
            )) {
                while (rst.next()) {
                    PromocaoVO vo = new PromocaoVO();
                    vo.setId((rst.getString("id")));
                    result.put(vo, Integer.parseInt(vo.getId()));
                }
            }
        }
        return result;
    }
}
