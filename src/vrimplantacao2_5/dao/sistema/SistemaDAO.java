package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class SistemaDAO {

    public List getSistema() throws Exception {
        List<SistemaVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	*\n"
                    + "from \n"
                    + "	implantacao2_5.sistema\n"
                    + "order by \n"
                    + "	nome")) {
                while (rs.next()) {
                    SistemaVO sistemaVO = new SistemaVO();

                    sistemaVO.setId(rs.getInt("id"));
                    sistemaVO.setNome(rs.getString("nome"));

                    result.add(sistemaVO);
                }
            }
        }

        return result;
    }

    public void salvar(SistemaVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao2_5");
        sql.setTableName("sistema");

        sql.put("nome", vo.getNome());

        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }
    
    public void alterar(BancoDadosVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("sistema");
        sql.setSchema("implantacao2_5");

        sql.put("nome", vo.getNome());

        sql.setWhere("id = " + vo.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }        
    }

    public boolean existeSistema(String nome) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select nome\n"
                    + "from implantacao2_5.sistema\n"
                    + "where nome = '" + nome + "'"
            )) {
                return rst.next();
            }
        }
    }    
    
    public List<BancoDadosVO> consultar() throws Exception {
        List<BancoDadosVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "nome\n"
                    + "from implantacao2_5.sistema\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    BancoDadosVO vo = new BancoDadosVO();
                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    result.add(vo);
                }
            }
            return result;
        }
    }
}
