/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.cadastro.pdv;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Michael
 */
public class PdvParametroValorDAO {
    
    public void atualizarValorPdvParametroValor(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("pdv");
        sql.setTableName("parametrovalor");

        sql.put("valor", i_loja.getId());

        sql.setWhere("id_loja = " + i_loja.getId() + " and id_parametro = 99");

        if (!sql.isEmpty()) {
            try (Statement stmUpdate = Conexao.createStatement()) {
                stmUpdate.execute(sql.getUpdate());
            }
        }
    }
    
}
