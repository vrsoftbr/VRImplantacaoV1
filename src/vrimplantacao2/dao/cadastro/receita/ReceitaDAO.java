/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author lucasrafael
 */
public class ReceitaDAO {

    public IDStack getIdsVagos(int maxId) throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n"
                    + "(SELECT id FROM generate_series(1, " + maxId + ")\n"
                    + "AS s(id) EXCEPT SELECT id FROM comprador) AS receita ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }        
        return result;
    }
    
    public void gravar(ReceitaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setTableName("receita");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("fichatecnica", vo.getFichatecnica());
            sql.put("id_situacaocadastro", vo.getId_situacaocadastro());            
            stm.execute(sql.getInsert());
        }
    }
}
