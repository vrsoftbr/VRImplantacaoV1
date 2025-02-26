/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutVO;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorLayoutDAO {
    
    public void salvar(AcumuladorLayoutVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("pdv");
            sql.setTableName("acumuladorlayout");
            sql.put("id", vo.getId());
            sql.put("id_loja", vo.getIdLoja());
            sql.put("descricao", vo.getDescricao());
            stm.execute(sql.getInsert());
        }
    }
}
