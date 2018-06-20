/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutRetornoVO;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorLayoutRetornoDAO {

    public void salvar(AcumuladorLayoutRetornoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("pdv");
            sql.setTableName("acumuladorlayoutretorno");
            sql.put("id_acumuladorlayout", vo.getIdAcumuladorLayout());
            sql.put("id_acumulador", vo.getIdAcumulador());
            sql.put("retorno", vo.getRetorno());
            sql.put("titulo", vo.getTitulo());
            stm.execute(sql.getInsert());
        }
    }
}
