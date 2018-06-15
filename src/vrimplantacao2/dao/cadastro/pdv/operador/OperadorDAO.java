/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;

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
            sql.put("nome", vo.getNome());
            sql.put("senha", vo.getSenha());
            sql.put("id_loja", vo.getId_loja());
            sql.put("id_tiponiveloperador", vo.getId_tiponiveloperador());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            stm.execute(sql.getInsert());
        }
    }
    
    public MultiMap<String, OperadorVO> getOperadores(int idLojaVR) throws Exception {
        MultiMap<String, OperadorVO> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_loja, "
                    + "matricula, "
                    + "nome, "
                    + "senha,"
                    + "codigo,"
                    + "id_tiponiveloperador,"
                    + "id_situacaocadastro "
                    + "from pdv.operador "
                    + "where "
                    + "	id_loja = " + idLojaVR + "\n"
                    + "order by\n"
                    + "	id_loja, matricula"
            )) {
                while (rst.next()) {
                    OperadorVO vo = new OperadorVO();                    
                    vo.setId_loja(rst.getInt("id_loja"));
                    vo.setMatricula(rst.getInt("matricula"));
                    vo.setNome(rst.getString("nome"));
                    vo.setSenha(rst.getInt("senha"));
                    vo.setCodigo(rst.getInt("codigo"));
                    vo.setId_tiponiveloperador(rst.getInt("id_tiponiveloperador"));
                    vo.setSituacaoCadastro(rst.getInt("id_situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    result.put(vo, String.valueOf(vo.getId_loja()), String.valueOf(vo.getMatricula()));
                }
            }
        }
        return result;
    }
}
