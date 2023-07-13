/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.cadastro.pdv;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Michael
 */
public class PdvCartaoLayoutDAO {
    
    public SQLBuilder copiarPdvCartaoLayout(LojaVO i_loja) throws Exception {
        String sql = "SELECT * FROM pdv.cartaolayout WHERE id_loja = " + i_loja.getIdCopiarLoja();
        SQLBuilder sqlInsert = null;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.cartaolayout");

                    sqlInsert = new SQLBuilder();
                    sqlInsert.setSchema("pdv");
                    sqlInsert.setTableName("cartaolayout");

                    sqlInsert.put("id", proximoId);
                    sqlInsert.put("id_loja", i_loja.getId());
                    sqlInsert.put("id_tipocartao", rst.getInt("id_tipocartao"));
                    sqlInsert.put("posicao", rst.getInt("posicao"));
                    sqlInsert.put("tamanho", rst.getInt("tamanho"));
                    sqlInsert.put("id_tipocartaocampo", rst.getInt("id_tipocartaocampo"));
                }
            }
        }

        return sqlInsert;
    }
    
}
