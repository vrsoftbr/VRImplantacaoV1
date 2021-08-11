package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.tributacao.AliquotaPdvVO;

public class AliquotaPdvDAO {

    public void incluir(AliquotaPdvVO aliquotaPdvVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("pdv");
        sql.setTableName("aliquota");
        sql.putSql("id", "(select coalesce(max(id) + 1, 1) from pdv.aliquota)");
        sql.put("descricao", aliquotaPdvVO.getDescricao());
        sql.put("porcentagem", aliquotaPdvVO.getPorcentagem());
        sql.put("id_aliquota", aliquotaPdvVO.getIdAliquota());

        sql.getReturning().add("id");

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql.getInsert())) {
                if (rs.next()) {
                    aliquotaPdvVO.setId(rs.getInt("id"));
                }
            }
        }
    }
    
    public int getAliquotaCadastrada(AliquotaPdvVO aliquotaPdvVO) throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery("select id from pdv.aliquota where porcentagem = " + aliquotaPdvVO.getPorcentagem())) {
                if(rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        
        return 0;
    }

}
