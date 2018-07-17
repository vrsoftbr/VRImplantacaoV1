package vrimplantacao2.dao.cadastro.mercadologico;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoVO;

/**
 *
 * @author Leandro
 */
public class MercadologicoDAO {

    public void excluir() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from mercadologico where id > 0; delete from implantacao.codant_mercadologico;");
        }
    }
    
    public void salvar(MercadologicoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("mercadologico");
            sql.put("mercadologico1", vo.getMercadologico1());
            sql.put("mercadologico2", vo.getMercadologico2());
            sql.put("mercadologico3", vo.getMercadologico3());
            sql.put("mercadologico4", vo.getMercadologico4());
            sql.put("mercadologico5", vo.getMercadologico5());
            sql.put("nivel", vo.getNivel());
            sql.put("descricao", vo.getDescricao());
            
            stm.execute(sql.getInsert());
        }
    }

    public void gerarAAcertar(int nivelMaximo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            //Inclui o nível 1 do A Acertar
            String sql = "insert into mercadologico ("
                    + "mercadologico1,"
                    + "mercadologico2,"
                    + "mercadologico3,"
                    + "mercadologico4,"
                    + "mercadologico5,"
                    + "descricao,"
                    + "nivel"
                    + ") values ("
                    + "(select id from generate_series(1,999) s(id) except select mercadologico1 from mercadologico where nivel = 1 order by id limit 1),"
                    + "0,"
                    + "0,"
                    + "0,"
                    + "0,"
                    + "'A ACERTAR',"
                    + "1) returning mercadologico1;";
            int idAcertar;
            try (ResultSet rst = stm.executeQuery(sql)) {
                rst.next();
                idAcertar = rst.getInt("mercadologico1");
            }

            sql = "insert into mercadologico ("
                    + "mercadologico1,"
                    + "mercadologico2,"
                    + "mercadologico3,"
                    + "mercadologico4,"
                    + "mercadologico5,"
                    + "descricao,"
                    + "nivel"
                    + ") values ("
                    + idAcertar + ","
                    + "1,"
                    + "0,"
                    + "0,"
                    + "0,"
                    + "'A ACERTAR',"
                    + "2);";

            sql += "insert into mercadologico ("
                    + "mercadologico1,"
                    + "mercadologico2,"
                    + "mercadologico3,"
                    + "mercadologico4,"
                    + "mercadologico5,"
                    + "descricao,"
                    + "nivel"
                    + ") values ("
                    + idAcertar + ","
                    + "1,"
                    + "1,"
                    + "0,"
                    + "0,"
                    + "'A ACERTAR',"
                    + "3);";
            if (nivelMaximo > 3) {
                sql += "insert into mercadologico ("
                        + "mercadologico1,"
                        + "mercadologico2,"
                        + "mercadologico3,"
                        + "mercadologico4,"
                        + "mercadologico5,"
                        + "descricao,"
                        + "nivel"
                        + ") values ("
                        + idAcertar + ","
                        + "1,"
                        + "1,"
                        + "1,"
                        + "0,"
                        + "'A ACERTAR',"
                        + "4);";
            }
            if (nivelMaximo > 4) {
                sql += "insert into mercadologico ("
                        + "mercadologico1,"
                        + "mercadologico2,"
                        + "mercadologico3,"
                        + "mercadologico4,"
                        + "mercadologico5,"
                        + "descricao,"
                        + "nivel"
                        + ") values ("
                        + idAcertar + ","
                        + "1,"
                        + "1,"
                        + "1,"
                        + "1,"
                        + "'A ACERTAR',"
                        + "5);";
            }

            stm.execute(sql);            
        }
    }
    
}
