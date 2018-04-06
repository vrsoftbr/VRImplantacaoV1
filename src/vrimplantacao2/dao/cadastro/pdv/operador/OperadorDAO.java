/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorVO;

/**
 *
 * @author lucasrafael
 */
public class OperadorDAO {

    public void salvar(OperadorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("pdv");
            sql.setTableName("operador");
            sql.put("id", vo.getId());
            sql.put("codigo", vo.getCodigo());
            sql.put("matricula", vo.getMatricula());
            sql.put("senha", vo.getSenha());
            sql.put("id_loja", vo.getId_loja());
            sql.put("id_tiponiveloperador", vo.getId_tiponiveloperador());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            stm.execute(sql.getInsert());
        }
    }
}
